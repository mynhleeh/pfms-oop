package com.pfms.service.impl.transfer;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFactory;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.ArchivedWalletState;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferFundsRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransferServiceImpl Unit Tests")
class TransferServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private TransferServiceImpl transferService;

    private UUID sourceWalletId;
    private UUID destinationWalletId;
    private UUID feeCategoryId;
    private Wallet sourceWallet;
    private Wallet destinationWallet;
    private Category expenseFeeCategory;

    @BeforeEach
    void setUp() {
        transferService = new TransferServiceImpl(transactionRepository, walletRepository, categoryRepository);
        sourceWalletId = UUID.randomUUID();
        destinationWalletId = UUID.randomUUID();
        feeCategoryId = UUID.randomUUID();

        sourceWallet = new Wallet(
                sourceWalletId, "Ví Tiền Mặt", "Ví nguồn", true,
                LocalDateTime.now().minusDays(10), new ActiveWalletState(), Money.of("1000000.00"));
        destinationWallet = new Wallet(
                destinationWalletId, "Ví Ngân Hàng", "Ví đích", false,
                LocalDateTime.now().minusDays(10), new ActiveWalletState(), Money.of("500000.00"));
        expenseFeeCategory = new Category(
                feeCategoryId, "Phí chuyển khoản", UUID.randomUUID(),
                TransactionType.EXPENSE, "fee", "#FF0000");
    }

    @Nested
    @DisplayName("Constructor Dependency Validation Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should reject null TransactionRepository in constructor")
        void shouldRejectNullTransactionRepository() {
            assertThrows(NullPointerException.class,
                    () -> new TransferServiceImpl(null, walletRepository, categoryRepository));
        }

        @Test
        @DisplayName("Should reject null WalletRepository in constructor")
        void shouldRejectNullWalletRepository() {
            assertThrows(NullPointerException.class,
                    () -> new TransferServiceImpl(transactionRepository, null, categoryRepository));
        }

        @Test
        @DisplayName("Should reject null CategoryRepository in constructor")
        void shouldRejectNullCategoryRepository() {
            assertThrows(NullPointerException.class,
                    () -> new TransferServiceImpl(transactionRepository, walletRepository, null));
        }
    }

    @Nested
    @DisplayName("UC-TRF-01: transferFunds() — 5-Group Edge-Case Matrix")
    class TransferFundsTests {

        @Test
        @DisplayName("Group 1 (Happy Path): Should transfer funds without fee and preserve total system balance")
        void shouldTransferFundsWithoutFeeSuccessfully() {
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("300000.00"),
                    LocalDate.now(), "Chuyển tiền tiết kiệm");
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));

            TransferResponseDTO response = transferService.transferFunds(request);

            assertNotNull(response.getTransferId());
            assertEquals(new BigDecimal("300000.00"), response.getAmount());
            assertEquals(new BigDecimal("0.00"), response.getFeeAmount());
            assertNull(response.getFeeCategoryId());
            assertEquals("", response.getFeeCategoryName());
            assertEquals(new BigDecimal("700000.00"), response.getSourceBalanceAfter());
            assertEquals(new BigDecimal("800000.00"), response.getDestinationBalanceAfter());
            verify(walletRepository).save(sourceWallet);
            verify(walletRepository).save(destinationWallet);
            verify(transactionRepository).save(any(TransferTransaction.class));
        }

        @Test
        @DisplayName("Group 1 (Happy Path): Should transfer funds with positive fee deducted solely from source wallet")
        void shouldTransferFundsWithFeeSuccessfully() {
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("400000.00"),
                    LocalDate.now(), "Chuyển liên ngân hàng", new BigDecimal("5500.00"), feeCategoryId);
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));
            when(categoryRepository.findById(feeCategoryId)).thenReturn(Optional.of(expenseFeeCategory));

            TransferResponseDTO response = transferService.transferFunds(request);

            assertEquals(new BigDecimal("400000.00"), response.getAmount());
            assertEquals(new BigDecimal("5500.00"), response.getFeeAmount());
            assertEquals(feeCategoryId, response.getFeeCategoryId());
            assertEquals("Phí chuyển khoản", response.getFeeCategoryName());
            assertEquals(new BigDecimal("594500.00"), response.getSourceBalanceAfter());
            assertEquals(new BigDecimal("900000.00"), response.getDestinationBalanceAfter());

            ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
            verify(transactionRepository).save(transactionCaptor.capture());
            TransferTransaction savedTransfer = (TransferTransaction) transactionCaptor.getValue();
            assertTrue(savedTransfer.getFee().isPresent());
            assertEquals(Money.of("5500.00"), savedTransfer.getFee().orElseThrow().getAmount());
        }

        @Test
        @DisplayName("Group 2 (Boundary & Precision): Should handle minimum 0.01 unit and exact full-balance depletion")
        void shouldDepleteSourceWalletToExactZeroWithMinimumUnitAndFee() {
            Wallet smallSource = new Wallet(
                    sourceWalletId, "Ví Lẻ", "", false,
                    LocalDateTime.now().minusDays(1), new ActiveWalletState(), Money.of("0.02"));
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("0.01"),
                    LocalDate.now(), "Chuyển mức tối thiểu", new BigDecimal("0.01"), feeCategoryId);
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(smallSource));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));
            when(categoryRepository.findById(feeCategoryId)).thenReturn(Optional.of(expenseFeeCategory));

            TransferResponseDTO response = transferService.transferFunds(request);

            assertEquals(new BigDecimal("0.00"), response.getSourceBalanceAfter());
            assertEquals(new BigDecimal("500000.01"), response.getDestinationBalanceAfter());
        }

        @Test
        @DisplayName("Group 2 (Boundary & Precision): Should accept maximum 255-character note")
        void shouldAcceptMax255CharacterNote() {
            String maxNote = "N".repeat(255);
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("10000.00"),
                    LocalDate.now(), maxNote);
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));

            TransferResponseDTO response = transferService.transferFunds(request);

            assertEquals(255, response.getNote().length());
        }

        @Test
        @DisplayName("Group 2 (Boundary & Precision): Should reject note exceeding 255 characters")
        void shouldRejectNoteExceeding255Characters() {
            String oversizedNote = "A".repeat(256);
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("10000.00"),
                    LocalDate.now(), oversizedNote);

            assertThrows(IllegalArgumentException.class, () -> transferService.transferFunds(request));
            verify(walletRepository, never()).save(any());
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.00", "-100.00"})
        @DisplayName("Group 3 (Negative & Zero): Should reject non-positive transfer amount with ERR_TRF_002")
        void shouldRejectNonPositiveTransferAmount(String invalidAmountText) {
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal(invalidAmountText));

            BusinessException exception = assertThrows(
                    BusinessException.class, () -> transferService.transferFunds(request));

            assertEquals(ErrorCode.ERR_TRF_002, exception.getErrorCode());
        }

        @Test
        @DisplayName("Group 3 (Negative & Zero): Should reject negative fee amount with ERR_FEE_001")
        void shouldRejectNegativeFeeAmount() {
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("100000.00"),
                    LocalDate.now(), "", new BigDecimal("-1.00"), null);

            BusinessException exception = assertThrows(
                    BusinessException.class, () -> transferService.transferFunds(request));

            assertEquals(ErrorCode.ERR_FEE_001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Group 3 (Overdraft Denial): Should throw InsufficientBalanceException when amount + fee exceeds source balance")
        void shouldRejectWhenTotalDeductionExceedsSourceBalance() {
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("995000.00"),
                    LocalDate.now(), "Vượt số dư do phí", new BigDecimal("10000.00"), feeCategoryId);
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));
            when(categoryRepository.findById(feeCategoryId)).thenReturn(Optional.of(expenseFeeCategory));

            InsufficientBalanceException exception = assertThrows(
                    InsufficientBalanceException.class, () -> transferService.transferFunds(request));

            assertEquals(ErrorCode.ERR_WAL_001, exception.getErrorCode());
            assertEquals(Money.of("1000000.00"), sourceWallet.getCurrentBalance());
            verify(walletRepository, never()).save(any());
            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 4 (Temporal Invariants): Should accept past leap-day date 2024-02-29")
        void shouldAcceptPastLeapDayTransferDate() {
            TransferFundsRequestDTO leapDayRequest = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("50000.00"),
                    LocalDate.of(2024, 2, 29), "Ngày nhuận");
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));

            TransferResponseDTO leapResponse = transferService.transferFunds(leapDayRequest);

            assertEquals(LocalDate.of(2024, 2, 29), leapResponse.getTransferDate());
        }

        @Test
        @DisplayName("Group 4 (Temporal Invariants): Should reject future transfer date with ERR_FIN_001")
        void shouldRejectFutureTransferDate() {
            TransferFundsRequestDTO futureRequest = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("50000.00"),
                    LocalDate.now().plusDays(1), "Ngày tương lai");

            BusinessException exception = assertThrows(
                    BusinessException.class, () -> transferService.transferFunds(futureRequest));

            assertEquals(ErrorCode.ERR_FIN_001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Group 5 (State & Robustness): Should reject null request with NullPointerException")
        void shouldRejectNullTransferRequest() {
            assertThrows(NullPointerException.class, () -> transferService.transferFunds(null));
        }

        @Test
        @DisplayName("Group 5 (State & Robustness): Should reject identical source and destination wallet IDs with ERR_TRF_001")
        void shouldRejectSameSourceAndDestinationWalletIds() {
            TransferFundsRequestDTO sameWalletRequest = new TransferFundsRequestDTO(
                    sourceWalletId, sourceWalletId, new BigDecimal("100000.00"));

            BusinessException exception = assertThrows(
                    BusinessException.class, () -> transferService.transferFunds(sameWalletRequest));

            assertEquals(ErrorCode.ERR_TRF_001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Group 5 (State & Robustness): Should throw EntityNotFoundException when source or destination wallet is missing")
        void shouldThrowEntityNotFoundWhenWalletDoesNotExist() {
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("100000.00"));
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> transferService.transferFunds(request));

            assertEquals(ErrorCode.ERR_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("Group 5 (State & Robustness): Should reject archived source or destination wallet with ERR_WAL_001")
        void shouldRejectArchivedWallet() {
            Wallet archivedSource = new Wallet(
                    sourceWalletId, "Ví Lưu Trữ", "", false,
                    LocalDateTime.now().minusDays(5), new ArchivedWalletState(), Money.of("1000000.00"));
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("100000.00"));
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(archivedSource));

            BusinessException exception = assertThrows(
                    BusinessException.class, () -> transferService.transferFunds(request));

            assertEquals(ErrorCode.ERR_WAL_001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Group 5 (Fee Category Rules): Should reject positive fee when feeCategoryId is null with ERR_FEE_002")
        void shouldRejectPositiveFeeWithoutCategoryId() {
            TransferFundsRequestDTO missingCategoryRequest = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("100000.00"),
                    LocalDate.now(), "", new BigDecimal("1000.00"), null);

            BusinessException exception = assertThrows(
                    BusinessException.class, () -> transferService.transferFunds(missingCategoryRequest));

            assertEquals(ErrorCode.ERR_FEE_002, exception.getErrorCode());
        }

        @Test
        @DisplayName("Group 5 (Fee Category Rules): Should throw EntityNotFoundException when fee category does not exist")
        void shouldThrowEntityNotFoundWhenFeeCategoryIsMissing() {
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("100000.00"),
                    LocalDate.now(), "", new BigDecimal("1000.00"), feeCategoryId);
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));
            when(categoryRepository.findById(feeCategoryId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> transferService.transferFunds(request));

            assertEquals(ErrorCode.ERR_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("Group 5 (Fee Category Rules): Should reject INCOME or DELETED fee category with ERR_FEE_003")
        void shouldRejectIncomeOrDeletedFeeCategory() {
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("100000.00"),
                    LocalDate.now(), "", new BigDecimal("1000.00"), feeCategoryId);
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));

            Category incomeCategory = new Category(
                    feeCategoryId, "Lương", UUID.randomUUID(), TransactionType.INCOME, "inc", "#00FF00");
            when(categoryRepository.findById(feeCategoryId)).thenReturn(Optional.of(incomeCategory));
            BusinessException incomeException = assertThrows(
                    BusinessException.class, () -> transferService.transferFunds(request));
            assertEquals(ErrorCode.ERR_FEE_003, incomeException.getErrorCode());

            expenseFeeCategory.markDeleted();
            when(categoryRepository.findById(feeCategoryId)).thenReturn(Optional.of(expenseFeeCategory));
            BusinessException deletedException = assertThrows(
                    BusinessException.class, () -> transferService.transferFunds(request));
            assertEquals(ErrorCode.ERR_FEE_003, deletedException.getErrorCode());
        }

        @Test
        @DisplayName("Group 5 (Compensating Rollback): Should restore source wallet when saving destination wallet fails")
        void shouldRollbackSourceWalletWhenDestinationWalletSaveThrowsStorageException() {
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("200000.00"));
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));
            when(walletRepository.save(sourceWallet)).thenReturn(sourceWallet);
            doThrow(new StorageException(new IOException("Write error on destination wallet")))
                    .when(walletRepository).save(destinationWallet);

            assertThrows(StorageException.class, () -> transferService.transferFunds(request));

            assertEquals(Money.of("1000000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.of("500000.00"), destinationWallet.getCurrentBalance());
            verify(walletRepository, times(3)).save(any(Wallet.class));
            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 5 (Compensating Rollback): Should restore both wallet snapshots and delete partial transfer on StorageException")
        void shouldRollbackWalletSnapshotsWhenTransactionSaveThrowsStorageException() {
            TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                    sourceWalletId, destinationWalletId, new BigDecimal("200000.00"),
                    LocalDate.now(), "Lỗi lưu giao dịch", new BigDecimal("5000.00"), feeCategoryId);
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));
            when(categoryRepository.findById(feeCategoryId)).thenReturn(Optional.of(expenseFeeCategory));
            doThrow(new StorageException(new IOException("Disk full")))
                    .when(transactionRepository).save(any(TransferTransaction.class));

            assertThrows(StorageException.class, () -> transferService.transferFunds(request));

            assertEquals(Money.of("1000000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.of("500000.00"), destinationWallet.getCurrentBalance());
            verify(walletRepository, times(4)).save(any(Wallet.class));
            verify(transactionRepository).deleteById(any(UUID.class));
        }
    }

    @Nested
    @DisplayName("UC-TRF-02: filterTransfers() — 5-Group Edge-Case Matrix")
    class FilterTransfersTests {

        private TransferTransaction buildTransfer(
                Wallet fromWallet, Wallet toWallet, String amount, LocalDate date,
                String note, Money fee, Category feeCategory) {
            return TransactionFactory.createTransfer(
                    fromWallet, toWallet, Money.of(amount), TransactionDate.of(date), note, fee, feeCategory);
        }

        private List<Transaction> buildTwelvePaginationTransfers(LocalDate today) {
            List<Transaction> transfers = new ArrayList<>();
            transfers.add(buildTransfer(sourceWallet, destinationWallet, "1000000000000.01",
                    today, "Nghìn tỷ", Money.of("0.01"), expenseFeeCategory));
            for (int index = 1; index <= 11; index++) {
                transfers.add(buildTransfer(sourceWallet, destinationWallet, "0.01",
                        today.minusDays(index), "Siêu nhỏ " + index, Money.ZERO, null));
            }
            return transfers;
        }

        @Test
        @DisplayName("Group 1 (Happy Path): Should filter transfers sorted descending and read updated wallet names without mutating state")
        void shouldFilterAllTransfersSortedDescendingInReadOnlyMode() {
            LocalDate today = LocalDate.now();
            Wallet renamedSource = new Wallet(sourceWalletId, "Ví Tiền Mặt Mới", "", true,
                    LocalDateTime.now().minusDays(10), new ActiveWalletState(), Money.of("550000.00"));
            TransferTransaction older = buildTransfer(sourceWallet, destinationWallet, "150000.00",
                    today.minusDays(3), "Cũ", Money.ZERO, null);
            TransferTransaction newer = buildTransfer(sourceWallet, destinationWallet, "300000.00",
                    today.minusDays(1), "Mới", Money.of("5500.00"), expenseFeeCategory);
            IncomeExpenseTransaction expenseTxn = TransactionFactory.createIncomeExpense(
                    TransactionType.EXPENSE, sourceWallet, expenseFeeCategory,
                    Money.of("90000.00"), TransactionDate.of(today), "Chi tiêu", Money.ZERO, null);
            when(transactionRepository.findAll()).thenReturn(List.of(older, expenseTxn, newer));
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(renamedSource));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));

            TransferListResponseDTO history = transferService.filterTransfers(new FilterTransfersRequestDTO());

            assertEquals(2, history.getTotalRecords());
            assertEquals(new BigDecimal("450000.00"), history.getTotalTransferredAmount());
            assertEquals(new BigDecimal("5500.00"), history.getTotalFees());
            assertEquals(newer.getTransactionId(), history.getItems().get(0).getTransferId());
            assertEquals("Ví Tiền Mặt Mới", history.getItems().get(0).getSourceWalletName());
            verify(walletRepository, never()).save(any());
            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 1 (Happy Path): Should delegate closed date range to findByDateRange and filter by source and destination wallets")
        void shouldFilterByClosedDateRangeAndWalletPair() {
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();
            Wallet thirdWallet = new Wallet(UUID.randomUUID(), "Ví Dự Phòng", "", false,
                    LocalDateTime.now().minusDays(10), new ActiveWalletState(), Money.of("200000.00"));
            TransferTransaction matched = buildTransfer(sourceWallet, destinationWallet, "250000.00",
                    endDate.minusDays(2), "Đúng cặp ví", Money.ZERO, null);
            TransferTransaction wrongSource = buildTransfer(thirdWallet, destinationWallet, "100000.00",
                    endDate.minusDays(1), "Sai nguồn", Money.ZERO, null);
            TransferTransaction wrongDest = buildTransfer(sourceWallet, thirdWallet, "100000.00",
                    endDate.minusDays(1), "Sai đích", Money.ZERO, null);
            when(transactionRepository.findByDateRange(startDate, endDate))
                    .thenReturn(List.of(matched, wrongSource, wrongDest));

            TransferListResponseDTO history = transferService.filterTransfers(
                    new FilterTransfersRequestDTO(startDate, endDate, sourceWalletId, destinationWalletId));

            verify(transactionRepository).findByDateRange(startDate, endDate);
            verify(transactionRepository, never()).findAll();
            assertEquals(1, history.getTotalRecords());
            assertEquals(matched.getTransactionId(), history.getItems().get(0).getTransferId());
            assertEquals(new BigDecimal("250000.00"), history.getTotalTransferredAmount());
        }

        @Test
        @DisplayName("Group 2 (Boundary & Precision): Should sum ultra-large 10^12+ and 0.01 amounts accurately on paginated page")
        void shouldSumUltraLargeAndMinimalAmountsAcrossPages() {
            when(transactionRepository.findAll()).thenReturn(buildTwelvePaginationTransfers(LocalDate.now()));

            TransferListResponseDTO page1 = transferService.filterTransfers(
                    new FilterTransfersRequestDTO(null, null, null, null, 1, 10));
            TransferListResponseDTO page2 = transferService.filterTransfers(
                    new FilterTransfersRequestDTO(null, null, null, null, 2, 10));

            assertEquals(1, page1.getPageNumber());
            assertEquals(10, page1.getPageSize());
            assertEquals(12L, page1.getTotalRecords());
            assertEquals(2, page1.getTotalPages());
            assertEquals(10, page1.getItemCount());
            assertTrue(page1.hasNextPage());
            assertFalse(page1.hasPreviousPage());

            assertEquals(2, page2.getPageNumber());
            assertEquals(10, page2.getPageSize());
            assertEquals(12L, page2.getTotalRecords());
            assertEquals(2, page2.getTotalPages());
            assertEquals(2, page2.getItemCount());
            assertFalse(page2.hasNextPage());
            assertTrue(page2.hasPreviousPage());
            assertEquals(new BigDecimal("1000000000000.12"), page2.getTotalTransferredAmount());
            assertEquals(new BigDecimal("0.01"), page2.getTotalFees());
        }

        @Test
        @DisplayName("Group 2 (Boundary & Precision): Should return empty page items while keeping full totals when page exceeds total records")
        void shouldReturnEmptyItemsWhenPageExceedsTotalRecords() {
            when(transactionRepository.findAll()).thenReturn(buildTwelvePaginationTransfers(LocalDate.now()));

            TransferListResponseDTO outOfRange = transferService.filterTransfers(
                    new FilterTransfersRequestDTO(null, null, null, null, 3, 10));

            assertTrue(outOfRange.isEmpty());
            assertEquals(3, outOfRange.getPageNumber());
            assertEquals(10, outOfRange.getPageSize());
            assertEquals(12L, outOfRange.getTotalRecords());
            assertEquals(2, outOfRange.getTotalPages());
            assertFalse(outOfRange.hasNextPage());
            assertTrue(outOfRange.hasPreviousPage());
            assertEquals(new BigDecimal("1000000000000.12"), outOfRange.getTotalTransferredAmount());
            assertEquals(new BigDecimal("0.01"), outOfRange.getTotalFees());
        }

        @Test
        @DisplayName("Group 2 (Boundary & Precision): Should return all matching records when pageSize is at inclusive upper bound 100")
        void shouldReturnAllItemsWhenPageSizeAtUpperBound() {
            when(transactionRepository.findAll()).thenReturn(buildTwelvePaginationTransfers(LocalDate.now()));

            TransferListResponseDTO history = transferService.filterTransfers(
                    new FilterTransfersRequestDTO(null, null, null, null, 1, 100));

            assertEquals(1, history.getPageNumber());
            assertEquals(100, history.getPageSize());
            assertEquals(12L, history.getTotalRecords());
            assertEquals(1, history.getTotalPages());
            assertEquals(12, history.getItemCount());
            assertFalse(history.hasNextPage());
            assertFalse(history.hasPreviousPage());
            assertEquals(new BigDecimal("1000000000000.12"), history.getTotalTransferredAmount());
            assertEquals(new BigDecimal("0.01"), history.getTotalFees());
        }

        @Test
        @DisplayName("Group 3 (Negative & Zero): Should return zero totals at scale 2 when no TransferTransaction matches")
        void shouldReturnZeroTotalsWhenNoTransfersMatch() {
            IncomeExpenseTransaction incomeTxn = TransactionFactory.createIncomeExpense(
                    TransactionType.INCOME, sourceWallet,
                    new Category(UUID.randomUUID(), "Thưởng", UUID.randomUUID(), TransactionType.INCOME, "gift", "#00AA00"),
                    Money.of("500000.00"), TransactionDate.now(), "Thu nhập", Money.ZERO, null);
            when(transactionRepository.findAll()).thenReturn(List.of(incomeTxn));

            TransferListResponseDTO history = transferService.filterTransfers(new FilterTransfersRequestDTO());

            assertTrue(history.isEmpty());
            assertEquals(1, history.getPageNumber());
            assertEquals(50, history.getPageSize());
            assertEquals(0L, history.getTotalRecords());
            assertEquals(0, history.getTotalPages());
            assertFalse(history.hasNextPage());
            assertFalse(history.hasPreviousPage());
            assertEquals(new BigDecimal("0.00"), history.getTotalTransferredAmount());
            assertEquals(new BigDecimal("0.00"), history.getTotalFees());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        @DisplayName("Group 3 (Negative & Zero Pagination): Should reject non-positive pageNumber")
        void shouldRejectNonPositivePageNumber(int invalidPageNumber) {
            FilterTransfersRequestDTO criteria = new FilterTransfersRequestDTO(
                    null, null, null, null, invalidPageNumber, 50);

            assertThrows(IllegalArgumentException.class, () -> transferService.filterTransfers(criteria));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 9, 101})
        @DisplayName("Group 3 (Out-of-Bounds Pagination): Should reject pageSize outside 10..100")
        void shouldRejectOutOfRangePageSize(int invalidPageSize) {
            FilterTransfersRequestDTO criteria = new FilterTransfersRequestDTO(
                    null, null, null, null, 1, invalidPageSize);

            assertThrows(IllegalArgumentException.class, () -> transferService.filterTransfers(criteria));
        }

        @Test
        @DisplayName("Group 4 (Temporal Invariants): Should handle leap day open-ended startDate and sort same-day ties by createdAt descending")
        void shouldFilterOpenEndedFromLeapDayAndSortSameDayDescending() {
            LocalDate leapDay = LocalDate.of(2024, 2, 29);
            TransferTransaction beforeLeapDay = buildTransfer(sourceWallet, destinationWallet, "10000.00",
                    LocalDate.of(2024, 2, 28), "Trước ngày nhuận", Money.ZERO, null);
            TransferTransaction olderSameDay = spy(buildTransfer(sourceWallet, destinationWallet, "20000.00",
                    leapDay, "Sáng ngày nhuận", Money.ZERO, null));
            TransferTransaction newerSameDay = spy(buildTransfer(sourceWallet, destinationWallet, "30000.00",
                    leapDay, "Chiều ngày nhuận", Money.ZERO, null));
            when(olderSameDay.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 2, 29, 8, 30));
            when(newerSameDay.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 2, 29, 17, 45));
            when(transactionRepository.findAll()).thenReturn(List.of(olderSameDay, beforeLeapDay, newerSameDay));

            TransferListResponseDTO history = transferService.filterTransfers(
                    new FilterTransfersRequestDTO(leapDay, null, null, null));

            verify(transactionRepository).findAll();
            assertEquals(2, history.getTotalRecords());
            assertEquals(newerSameDay.getTransactionId(), history.getItems().get(0).getTransferId());
            assertEquals(olderSameDay.getTransactionId(), history.getItems().get(1).getTransferId());
        }

        @Test
        @DisplayName("Group 4 (Temporal Invariants): Should filter open-ended criteria when only endDate is specified")
        void shouldFilterOpenEndedUpToEndDateOnly() {
            LocalDate cutoff = LocalDate.now().minusDays(2);
            TransferTransaction onCutoff = buildTransfer(sourceWallet, destinationWallet, "40000.00",
                    cutoff, "Đúng mốc cuối", Money.ZERO, null);
            TransferTransaction afterCutoff = buildTransfer(sourceWallet, destinationWallet, "60000.00",
                    cutoff.plusDays(1), "Vượt mốc cuối", Money.ZERO, null);
            when(transactionRepository.findAll()).thenReturn(List.of(onCutoff, afterCutoff));

            TransferListResponseDTO history = transferService.filterTransfers(
                    new FilterTransfersRequestDTO(null, cutoff, null, null));

            verify(transactionRepository).findAll();
            assertEquals(1, history.getTotalRecords());
            assertEquals(onCutoff.getTransactionId(), history.getItems().get(0).getTransferId());
        }

        @ParameterizedTest
        @CsvSource({"1, 0", "0, 1"})
        @DisplayName("Group 4 (Temporal Invariants): Should reject future startDate or endDate with ERR_FIN_001")
        void shouldRejectFutureFilterDates(int startOffsetDays, int endOffsetDays) {
            LocalDate futureStart = (startOffsetDays > 0) ? LocalDate.now().plusDays(startOffsetDays) : null;
            LocalDate futureEnd = (endOffsetDays > 0) ? LocalDate.now().plusDays(endOffsetDays) : null;
            FilterTransfersRequestDTO criteria = new FilterTransfersRequestDTO(futureStart, futureEnd, null, null);

            BusinessException exception = assertThrows(
                    BusinessException.class, () -> transferService.filterTransfers(criteria));

            assertEquals(ErrorCode.ERR_FIN_001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Group 4 (Temporal Invariants): Should reject startDate after endDate with ERR_FIN_001")
        void shouldRejectStartDateAfterEndDate() {
            FilterTransfersRequestDTO invalidRange = new FilterTransfersRequestDTO(
                    LocalDate.now(), LocalDate.now().minusDays(1), null, null);

            BusinessException exception = assertThrows(
                    BusinessException.class, () -> transferService.filterTransfers(invalidRange));

            assertEquals(ErrorCode.ERR_FIN_001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Group 5 (State & Robustness): Should reject null filter criteria with NullPointerException")
        void shouldRejectNullFilterCriteria() {
            assertThrows(NullPointerException.class, () -> transferService.filterTransfers(null));
        }

        @Test
        @DisplayName("Group 5 (State & Robustness): Should fallback to embedded wallet data when wallet is absent in repository")
        void shouldFallbackToEmbeddedWalletsWhenAbsentFromRepository() {
            TransferTransaction transfer = buildTransfer(sourceWallet, destinationWallet, "75000.00",
                    LocalDate.now(), "Kiểm tra fallback", Money.ZERO, null);
            when(transactionRepository.findAll()).thenReturn(List.of(transfer));
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.empty());
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.empty());

            TransferListResponseDTO history = transferService.filterTransfers(new FilterTransfersRequestDTO());

            assertEquals(1, history.getTotalRecords());
            assertEquals("Ví Tiền Mặt", history.getItems().get(0).getSourceWalletName());
            assertEquals("Ví Ngân Hàng", history.getItems().get(0).getDestinationWalletName());
        }

        @Test
        @DisplayName("Group 5 (State & Robustness): Should return empty history without wallet lookups when repository is empty")
        void shouldReturnEmptyHistoryWhenRepositoryIsEmpty() {
            when(transactionRepository.findAll()).thenReturn(List.of());

            TransferListResponseDTO history = transferService.filterTransfers(new FilterTransfersRequestDTO());

            assertTrue(history.isEmpty());
            assertEquals(1, history.getPageNumber());
            assertEquals(50, history.getPageSize());
            assertEquals(0L, history.getTotalRecords());
            assertEquals(0, history.getTotalPages());
            assertFalse(history.hasNextPage());
            assertFalse(history.hasPreviousPage());
            assertEquals(new BigDecimal("0.00"), history.getTotalTransferredAmount());
            assertEquals(new BigDecimal("0.00"), history.getTotalFees());
            verify(walletRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Group 5 (State & Robustness): Should propagate StorageException when repository fails during filter")
        void shouldPropagateStorageExceptionWhenRepositoryFails() {
            when(transactionRepository.findAll()).thenThrow(new StorageException(new IOException("Read error")));

            assertThrows(StorageException.class, () -> transferService.filterTransfers(new FilterTransfersRequestDTO()));
        }
    }

    @Nested
    @DisplayName("UC-TRF-03: deleteTransfer() — 5-Group Edge-Case Matrix")
    class DeleteTransferTests {

        private TransferTransaction buildStoredTransfer(
                Wallet snapshotSource, Wallet snapshotDest, Money amount, Money feeAmount) {
            Category category = feeAmount.isPositive() ? expenseFeeCategory : null;
            return TransactionFactory.createTransfer(
                    snapshotSource, snapshotDest, amount, TransactionDate.now(),
                    "Giao dịch chuyển thử", feeAmount, category);
        }

        private void stubLookupWallets(TransferTransaction transfer, Wallet liveSource, Wallet liveDest) {
            when(transactionRepository.findById(transfer.getTransactionId())).thenReturn(Optional.of(transfer));
            when(walletRepository.findById(liveSource.getId())).thenReturn(Optional.of(liveSource));
            when(walletRepository.findById(liveDest.getId())).thenReturn(Optional.of(liveDest));
        }

        @Test
        @DisplayName("Group 1 (Happy Path): Should delete transfer without fee and revert balances on live wallets in sequence order")
        void shouldDeleteTransferWithoutFeeAndRevertLiveWallets() {
            Wallet staleSource = sourceWallet.copy();
            Wallet staleDest = destinationWallet.copy();
            TransferTransaction transfer = buildStoredTransfer(staleSource, staleDest, Money.of("200000.00"), Money.ZERO);
            stubLookupWallets(transfer, sourceWallet, destinationWallet);
            when(transactionRepository.deleteById(transfer.getTransactionId())).thenReturn(true);

            transferService.deleteTransfer(transfer.getTransactionId());

            assertEquals(Money.of("1200000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.of("300000.00"), destinationWallet.getCurrentBalance());
            assertEquals(Money.of("1000000.00"), staleSource.getCurrentBalance());
            assertEquals(Money.of("500000.00"), staleDest.getCurrentBalance());
            InOrder callOrder = inOrder(transactionRepository, walletRepository);
            callOrder.verify(transactionRepository).deleteById(transfer.getTransactionId());
            callOrder.verify(walletRepository).save(destinationWallet);
            callOrder.verify(walletRepository).save(sourceWallet);
        }

        @Test
        @DisplayName("Group 1 (Happy Path): Should refund transfer amount plus fee back to live source wallet")
        void shouldDeleteTransferWithFeeAndRefundTotalToSource() {
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), destinationWallet.copy(), Money.of("200000.00"), Money.of("5000.00"));
            stubLookupWallets(transfer, sourceWallet, destinationWallet);
            when(transactionRepository.deleteById(transfer.getTransactionId())).thenReturn(true);

            transferService.deleteTransfer(transfer.getTransactionId());

            assertEquals(Money.of("1205000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.of("300000.00"), destinationWallet.getCurrentBalance());
            verify(walletRepository).save(destinationWallet);
            verify(walletRepository).save(sourceWallet);
        }

        @Test
        @DisplayName("Group 2 (Boundary & Precision): Should allow deletion when live destination balance equals transfer amount exactly")
        void shouldDeleteTransferWhenDestinationBalanceEqualsAmountExactly() {
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), destinationWallet.copy(), Money.of("500000.00"), Money.of("1000.00"));
            stubLookupWallets(transfer, sourceWallet, destinationWallet);
            when(transactionRepository.deleteById(transfer.getTransactionId())).thenReturn(true);

            transferService.deleteTransfer(transfer.getTransactionId());

            assertEquals(Money.of("1501000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.ZERO, destinationWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("Group 2 (Boundary & Precision): Should preserve BigDecimal scale 2 for 10^12+ and 0.01 amounts")
        void shouldPreserveBigDecimalPrecisionForExtremeTransferAmounts() {
            Wallet largeSource = new Wallet(sourceWalletId, "Nguồn", "", false,
                    LocalDateTime.now().minusDays(2), new ActiveWalletState(), Money.of("0.01"));
            Wallet largeDest = new Wallet(destinationWalletId, "Đích", "", false,
                    LocalDateTime.now().minusDays(2), new ActiveWalletState(), Money.of("1000000000000.01"));
            TransferTransaction transfer = buildStoredTransfer(
                    largeSource.copy(), largeDest.copy(), Money.of("1000000000000.01"), Money.of("0.01"));
            stubLookupWallets(transfer, largeSource, largeDest);
            when(transactionRepository.deleteById(transfer.getTransactionId())).thenReturn(true);

            transferService.deleteTransfer(transfer.getTransactionId());

            assertEquals(Money.of("1000000000000.03"), largeSource.getCurrentBalance());
            assertEquals(Money.ZERO, largeDest.getCurrentBalance());
        }

        @Test
        @DisplayName("Group 3 (Negative & Null): Should reject null transferId without querying repositories")
        void shouldRejectNullTransferId() {
            assertThrows(NullPointerException.class, () -> transferService.deleteTransfer(null));
            verify(transactionRepository, never()).findById(any());
            verify(walletRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 3 (Negative & Missing): Should throw EntityNotFoundException when transferId does not exist")
        void shouldThrowEntityNotFoundWhenTransferDoesNotExist() {
            UUID missingId = UUID.randomUUID();
            when(transactionRepository.findById(missingId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> transferService.deleteTransfer(missingId));

            assertEquals(ErrorCode.ERR_NOT_FOUND, exception.getErrorCode());
            verify(transactionRepository, never()).deleteById(any());
            verify(walletRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 3 (Negative & Type Guard): Should reject non-transfer transaction ID with EntityNotFoundException")
        void shouldRejectNonTransferTransactionId() {
            IncomeExpenseTransaction expenseTxn = TransactionFactory.createIncomeExpense(
                    TransactionType.EXPENSE, sourceWallet, expenseFeeCategory,
                    Money.of("50000.00"), TransactionDate.now(), "Chi ăn trưa", Money.ZERO, null);
            when(transactionRepository.findById(expenseTxn.getTransactionId())).thenReturn(Optional.of(expenseTxn));

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> transferService.deleteTransfer(expenseTxn.getTransactionId()));

            assertEquals(ErrorCode.ERR_NOT_FOUND, exception.getErrorCode());
            verify(transactionRepository, never()).deleteById(any());
            verify(walletRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 3 (Negative & Insufficient Balance): Should reject delete when live dest balance < amount and preserve data")
        void shouldRejectDeleteWhenDestinationBalanceInsufficient() {
            Wallet highSnapshotDest = new Wallet(destinationWalletId, "Ví Ngân Hàng", "", false,
                    LocalDateTime.now().minusDays(2), new ActiveWalletState(), Money.of("900000.00"));
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), highSnapshotDest, Money.of("500000.01"), Money.ZERO);
            stubLookupWallets(transfer, sourceWallet, destinationWallet);

            InsufficientBalanceException exception = assertThrows(
                    InsufficientBalanceException.class, () -> transferService.deleteTransfer(transfer.getTransactionId()));

            assertEquals(ErrorCode.ERR_WAL_001, exception.getErrorCode());
            assertEquals(Money.of("1000000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.of("500000.00"), destinationWallet.getCurrentBalance());
            verify(transactionRepository, never()).deleteById(any());
            verify(walletRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 3 (Negative & Missing Wallet): Should throw EntityNotFoundException when live source wallet is missing")
        void shouldThrowEntityNotFoundWhenLiveSourceWalletMissing() {
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), destinationWallet.copy(), Money.of("100000.00"), Money.ZERO);
            when(transactionRepository.findById(transfer.getTransactionId())).thenReturn(Optional.of(transfer));
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> transferService.deleteTransfer(transfer.getTransactionId()));

            assertEquals(ErrorCode.ERR_NOT_FOUND, exception.getErrorCode());
            assertEquals(Money.of("500000.00"), destinationWallet.getCurrentBalance());
            verify(transactionRepository, never()).deleteById(any());
            verify(walletRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 3 (Negative & Missing Wallet): Should throw EntityNotFoundException and leave source untouched when dest wallet is missing")
        void shouldThrowEntityNotFoundWhenLiveDestinationWalletMissing() {
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), destinationWallet.copy(), Money.of("100000.00"), Money.ZERO);
            when(transactionRepository.findById(transfer.getTransactionId())).thenReturn(Optional.of(transfer));
            when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
            when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> transferService.deleteTransfer(transfer.getTransactionId()));

            assertEquals(ErrorCode.ERR_NOT_FOUND, exception.getErrorCode());
            assertEquals(Money.of("1000000.00"), sourceWallet.getCurrentBalance());
            verify(transactionRepository, never()).deleteById(any());
            verify(walletRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 4 (State & Integrity): Should reject delete with ERR_WAL_001 and preserve balances when live source wallet is archived")
        void shouldRejectDeleteWhenLiveSourceWalletArchived() {
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), destinationWallet.copy(), Money.of("100000.00"), Money.ZERO);
            Wallet archivedSource = new Wallet(sourceWalletId, "Ví Tiền Mặt", "", true,
                    LocalDateTime.now().minusDays(10), new ArchivedWalletState(), Money.of("1000000.00"));
            stubLookupWallets(transfer, archivedSource, destinationWallet);

            BusinessException exception = assertThrows(
                    BusinessException.class, () -> transferService.deleteTransfer(transfer.getTransactionId()));

            assertEquals(ErrorCode.ERR_WAL_001, exception.getErrorCode());
            assertEquals(Money.of("1000000.00"), archivedSource.getCurrentBalance());
            assertEquals(Money.of("500000.00"), destinationWallet.getCurrentBalance());
            verify(transactionRepository, never()).deleteById(any());
            verify(walletRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 4 (State & Integrity): Should reject delete with ERR_WAL_001 and preserve balances when live destination wallet is deleted")
        void shouldRejectDeleteWhenLiveDestinationWalletDeleted() {
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), destinationWallet.copy(), Money.of("100000.00"), Money.ZERO);
            destinationWallet.delete();
            stubLookupWallets(transfer, sourceWallet, destinationWallet);

            BusinessException exception = assertThrows(
                    BusinessException.class, () -> transferService.deleteTransfer(transfer.getTransactionId()));

            assertEquals(ErrorCode.ERR_WAL_001, exception.getErrorCode());
            assertEquals(Money.of("1000000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.of("500000.00"), destinationWallet.getCurrentBalance());
            verify(transactionRepository, never()).deleteById(any());
            verify(walletRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 4 (Concurrency / Idempotency): Should throw EntityNotFoundException and restore balances if deleteById returns false")
        void shouldThrowEntityNotFoundAndRestoreBalancesWhenDeleteByIdReturnsFalse() {
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), destinationWallet.copy(), Money.of("100000.00"), Money.of("5000.00"));
            stubLookupWallets(transfer, sourceWallet, destinationWallet);
            when(transactionRepository.deleteById(transfer.getTransactionId())).thenReturn(false);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> transferService.deleteTransfer(transfer.getTransactionId()));

            assertEquals(ErrorCode.ERR_NOT_FOUND, exception.getErrorCode());
            assertEquals(Money.of("1000000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.of("500000.00"), destinationWallet.getCurrentBalance());
            verify(walletRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 5 (Atomic Rollback): Should restore in-memory balances when deleteById throws StorageException")
        void shouldRestoreInMemoryBalancesWhenDeleteByIdThrowsStorageException() {
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), destinationWallet.copy(), Money.of("100000.00"), Money.of("5000.00"));
            stubLookupWallets(transfer, sourceWallet, destinationWallet);
            doThrow(new StorageException(new IOException("Delete IO error")))
                    .when(transactionRepository).deleteById(transfer.getTransactionId());

            assertThrows(StorageException.class, () -> transferService.deleteTransfer(transfer.getTransactionId()));

            assertEquals(Money.of("1000000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.of("500000.00"), destinationWallet.getCurrentBalance());
            verify(walletRepository, never()).save(any());
            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Group 5 (Atomic Rollback): Should restore transaction record and balances when saving destination wallet fails")
        void shouldRollbackTransactionWhenSavingDestinationWalletFails() {
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), destinationWallet.copy(), Money.of("100000.00"), Money.of("5000.00"));
            stubLookupWallets(transfer, sourceWallet, destinationWallet);
            when(transactionRepository.deleteById(transfer.getTransactionId())).thenReturn(true);
            doThrow(new StorageException(new IOException("Disk full on dest")))
                    .when(walletRepository).save(destinationWallet);

            assertThrows(StorageException.class, () -> transferService.deleteTransfer(transfer.getTransactionId()));

            assertEquals(Money.of("1000000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.of("500000.00"), destinationWallet.getCurrentBalance());
            verify(transactionRepository).save(transfer);
            verify(walletRepository, times(1)).save(any(Wallet.class));
        }

        @Test
        @DisplayName("Group 5 (Atomic Rollback): Should restore transaction and destination wallet snapshot when saving source wallet fails")
        void shouldRollbackTransactionAndDestSnapshotWhenSavingSourceWalletFails() {
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), destinationWallet.copy(), Money.of("100000.00"), Money.of("5000.00"));
            stubLookupWallets(transfer, sourceWallet, destinationWallet);
            when(transactionRepository.deleteById(transfer.getTransactionId())).thenReturn(true);
            List<Money> savedDestBalances = new ArrayList<>();
            doAnswer(invocation -> {
                Wallet saved = invocation.getArgument(0);
                if (saved.getId().equals(sourceWalletId)) {
                    throw new StorageException(new IOException("Disk full on source"));
                }
                savedDestBalances.add(saved.getCurrentBalance());
                return saved;
            }).when(walletRepository).save(any(Wallet.class));

            assertThrows(StorageException.class, () -> transferService.deleteTransfer(transfer.getTransactionId()));

            assertEquals(List.of(Money.of("400000.00"), Money.of("500000.00")), savedDestBalances);
            assertEquals(Money.of("1000000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.of("500000.00"), destinationWallet.getCurrentBalance());
            verify(transactionRepository).save(transfer);
        }

        @Test
        @DisplayName("Group 5 (Rollback Isolation): Should still restore transaction and preserve primary StorageException if compensating wallet save fails")
        void shouldRestoreTransactionAndPreservePrimaryExceptionWhenCompensatingWalletSaveFails() {
            TransferTransaction transfer = buildStoredTransfer(
                    sourceWallet.copy(), destinationWallet.copy(), Money.of("100000.00"), Money.of("5000.00"));
            stubLookupWallets(transfer, sourceWallet, destinationWallet);
            when(transactionRepository.deleteById(transfer.getTransactionId())).thenReturn(true);
            StorageException primaryFailure = new StorageException("Primary source fail", new IOException());
            StorageException secondaryFailure = new StorageException("Secondary dest fail", new IOException());
            int[] destSaveAttempts = {0};
            doAnswer(invocation -> {
                Wallet wallet = invocation.getArgument(0);
                if (wallet.getId().equals(sourceWalletId)) {
                    throw primaryFailure;
                }
                if (++destSaveAttempts[0] == 2) {
                    throw secondaryFailure;
                }
                return wallet;
            }).when(walletRepository).save(any(Wallet.class));

            StorageException thrown = assertThrows(
                    StorageException.class, () -> transferService.deleteTransfer(transfer.getTransactionId()));

            assertSame(primaryFailure, thrown);
            assertEquals(1, thrown.getSuppressed().length);
            assertSame(secondaryFailure, thrown.getSuppressed()[0]);
            assertEquals(Money.of("1000000.00"), sourceWallet.getCurrentBalance());
            assertEquals(Money.of("500000.00"), destinationWallet.getCurrentBalance());
            verify(transactionRepository).save(transfer);
        }
    }
}
