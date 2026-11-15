package com.pfms.service.impl.transaction;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.TransactionFactory;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.ArchivedWalletState;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.RecordTransactionRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.transaction.UpdateTransactionRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionServiceImpl Unit Tests")
class TransactionServiceImplTest {

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private WalletRepository walletRepository;

        @Mock
        private CategoryRepository categoryRepository;

        private TransactionServiceImpl transactionService;

        private UUID walletId;
        private UUID categoryId;
        private UUID feeCategoryId;
        private Wallet activeWallet;
        private Category incomeCategory;
        private Category expenseCategory;
        private Category expenseFeeCategory;

        @BeforeEach
        void setUp() {
                transactionService = new TransactionServiceImpl(
                                transactionRepository, walletRepository, categoryRepository);

                walletId = UUID.randomUUID();
                categoryId = UUID.randomUUID();
                feeCategoryId = UUID.randomUUID();

                activeWallet = new Wallet(
                                walletId, "Ví Tiền Mặt", "Ví mặc định", true,
                                LocalDateTime.now().minusDays(5), new ActiveWalletState(), Money.of("1000000.00"));

                incomeCategory = new Category(
                                categoryId, "Tiền lương", UUID.randomUUID(),
                                TransactionType.INCOME, "salary-icon", "#00ff00");

                expenseCategory = new Category(
                                categoryId, "Ăn uống", UUID.randomUUID(),
                                TransactionType.EXPENSE, "food-icon", "#ff0000");

                expenseFeeCategory = new Category(
                                feeCategoryId, "Phí dịch vụ", UUID.randomUUID(),
                                TransactionType.EXPENSE, "fee-icon", "#ff5500");
        }

        @Nested
        @DisplayName("Kiểm thử Constructor và Tiêm phụ thuộc")
        class ConstructorTests {

                @Test
                @DisplayName("Khởi tạo thất bại khi TransactionRepository là null")
                void shouldThrowWhenTransactionRepositoryIsNull() {
                        NullPointerException ex = assertThrows(
                                        NullPointerException.class,
                                        () -> new TransactionServiceImpl(null, walletRepository, categoryRepository));
                        assertTrue(ex.getMessage().contains("TransactionRepository không được để null"));
                }

                @Test
                @DisplayName("Khởi tạo thất bại khi WalletRepository là null")
                void shouldThrowWhenWalletRepositoryIsNull() {
                        NullPointerException ex = assertThrows(
                                        NullPointerException.class,
                                        () -> new TransactionServiceImpl(transactionRepository, null,
                                                        categoryRepository));
                        assertTrue(ex.getMessage().contains("WalletRepository không được để null"));
                }

                @Test
                @DisplayName("Khởi tạo thất bại khi CategoryRepository là null")
                void shouldThrowWhenCategoryRepositoryIsNull() {
                        NullPointerException ex = assertThrows(
                                        NullPointerException.class,
                                        () -> new TransactionServiceImpl(transactionRepository, walletRepository,
                                                        null));
                        assertTrue(ex.getMessage().contains("CategoryRepository không được để null"));
                }

                @Test
                @DisplayName("Khởi tạo thành công khi tất cả phụ thuộc hợp lệ")
                void shouldInstantiateSuccessfully() {
                        TransactionServiceImpl service = new TransactionServiceImpl(
                                        transactionRepository, walletRepository, categoryRepository);
                        assertNotNull(service);
                }
        }

        @Nested
        @DisplayName("UC-TXN-01: Ghi nhận giao dịch thu chi (recordIncomeExpense)")
        class RecordIncomeExpenseTests {

                @Test
                @DisplayName("Ném NullPointerException khi request là null")
                void shouldThrowWhenRequestIsNull() {
                        assertThrows(NullPointerException.class, () -> transactionService.recordIncomeExpense(null));
                }

                @Test
                @DisplayName("Ném BusinessException khi số tiền giao dịch <= 0")
                void shouldThrowWhenAmountIsZeroOrNegative() {
                        RecordTransactionRequestDTO zeroReq = new RecordTransactionRequestDTO(
                                        walletId, categoryId, BigDecimal.ZERO);
                        BusinessException ex1 = assertThrows(
                                        BusinessException.class, () -> transactionService.recordIncomeExpense(zeroReq));
                        assertEquals(ErrorCode.ERR_FIN_001, ex1.getErrorCode());

                        RecordTransactionRequestDTO negativeReq = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("-50000.00"));
                        BusinessException ex2 = assertThrows(
                                        BusinessException.class,
                                        () -> transactionService.recordIncomeExpense(negativeReq));
                        assertEquals(ErrorCode.ERR_FIN_001, ex2.getErrorCode());
                }

                @Test
                @DisplayName("Ném BusinessException khi phí âm")
                void shouldThrowWhenFeeAmountIsNegative() {
                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("100000.00"),
                                        LocalDate.now(), "Ăn trưa", new BigDecimal("-1000.00"), feeCategoryId);

                        BusinessException ex = assertThrows(
                                        BusinessException.class, () -> transactionService.recordIncomeExpense(req));
                        assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném BusinessException khi phí dương nhưng feeCategoryId là null")
                void shouldThrowWhenFeeIsPositiveButFeeCategoryIdIsNull() {
                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("100000.00"),
                                        LocalDate.now(), "Ăn trưa", new BigDecimal("5000.00"), null);

                        BusinessException ex = assertThrows(
                                        BusinessException.class, () -> transactionService.recordIncomeExpense(req));
                        assertEquals(ErrorCode.ERR_FEE_002, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném EntityNotFoundException khi không tìm thấy ví")
                void shouldThrowWhenWalletNotFound() {
                        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("100000.00"));

                        EntityNotFoundException ex = assertThrows(
                                        EntityNotFoundException.class,
                                        () -> transactionService.recordIncomeExpense(req));
                        assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném BusinessException khi ví không ở trạng thái ACTIVE")
                void shouldThrowWhenWalletNotActive() {
                        Wallet archivedWallet = new Wallet(
                                        walletId, "Ví Lưu Trữ", "Ghi chú", false,
                                        LocalDateTime.now(), new ArchivedWalletState(), Money.of("1000000.00"));

                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(archivedWallet));

                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("100000.00"));

                        BusinessException ex = assertThrows(
                                        BusinessException.class, () -> transactionService.recordIncomeExpense(req));
                        assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném EntityNotFoundException khi không tìm thấy danh mục")
                void shouldThrowWhenCategoryNotFound() {
                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("100000.00"));

                        EntityNotFoundException ex = assertThrows(
                                        EntityNotFoundException.class,
                                        () -> transactionService.recordIncomeExpense(req));
                        assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném BusinessException khi danh mục đã bị xóa mềm")
                void shouldThrowWhenCategoryNotActive() {
                        expenseCategory.markDeleted();
                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("100000.00"));

                        BusinessException ex = assertThrows(
                                        BusinessException.class, () -> transactionService.recordIncomeExpense(req));
                        assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném EntityNotFoundException khi danh mục phí không tồn tại")
                void shouldThrowWhenFeeCategoryNotFound() {
                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));
                        when(categoryRepository.findById(feeCategoryId)).thenReturn(Optional.empty());

                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("100000.00"),
                                        LocalDate.now(), "Ăn trưa", new BigDecimal("5000.00"), feeCategoryId);

                        EntityNotFoundException ex = assertThrows(
                                        EntityNotFoundException.class,
                                        () -> transactionService.recordIncomeExpense(req));
                        assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném BusinessException khi danh mục phí không thuộc loại chi tiêu hoặc không active")
                void shouldThrowWhenFeeCategoryNotExpenseOrDeleted() {
                        Category incomeFeeCat = new Category(
                                        feeCategoryId, "Thu phụ", UUID.randomUUID(),
                                        TransactionType.INCOME, "icon", "color");

                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));
                        when(categoryRepository.findById(feeCategoryId)).thenReturn(Optional.of(incomeFeeCat));

                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("100000.00"),
                                        LocalDate.now(), "Ăn trưa", new BigDecimal("5000.00"), feeCategoryId);

                        BusinessException ex = assertThrows(
                                        BusinessException.class, () -> transactionService.recordIncomeExpense(req));
                        assertEquals(ErrorCode.ERR_FEE_003, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném InsufficientBalanceException khi chi tiêu vượt số dư ví")
                void shouldThrowWhenInsufficientBalanceOnExpense() {
                        Wallet poorWallet = new Wallet(
                                        walletId, "Ví Nghèo", "", false,
                                        LocalDateTime.now(), new ActiveWalletState(), Money.of("50000.00"));

                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(poorWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("100000.00"));

                        InsufficientBalanceException ex = assertThrows(
                                        InsufficientBalanceException.class,
                                        () -> transactionService.recordIncomeExpense(req));
                        assertEquals(walletId.toString(), ex.getWalletId());
                }

                @Test
                @DisplayName("Ghi nhận thu nhập không kèm phí thành công, cộng số dư ví")
                void shouldRecordIncomeWithoutFeeSuccessfully() {
                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(incomeCategory));

                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("500000.00"),
                                        LocalDate.of(2026, 11, 10), "Tiền lương thưởng");

                        TransactionResponseDTO response = transactionService.recordIncomeExpense(req);

                        assertNotNull(response);
                        assertEquals(walletId, response.getWalletId());
                        assertEquals("Ví Tiền Mặt", response.getWalletName());
                        assertEquals(categoryId, response.getCategoryId());
                        assertEquals("Tiền lương", response.getCategoryName());
                        assertEquals(new BigDecimal("500000.00"), response.getAmount());
                        assertEquals("INCOME", response.getTransactionType());
                        assertEquals("Tiền lương thưởng", response.getNote());
                        assertEquals(new BigDecimal("1500000.00"), response.getWalletBalanceAfter());
                        assertFalse(response.hasFee());
                        assertEquals(BigDecimal.ZERO, response.getFeeAmount());
                        assertNull(response.getFeeCategoryId());

                        verify(transactionRepository).save(any(IncomeExpenseTransaction.class));
                        verify(walletRepository).save(activeWallet);
                        assertEquals(Money.of("1500000.00"), activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("Ghi nhận chi tiêu kèm phụ phí thành công, trừ số dư ví cả khoản chi và phí")
                void shouldRecordExpenseWithFeeSuccessfully() {
                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));
                        when(categoryRepository.findById(feeCategoryId)).thenReturn(Optional.of(expenseFeeCategory));

                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("200000.00"),
                                        LocalDate.of(2026, 11, 10), "Đi siêu thị",
                                        new BigDecimal("5000.00"), feeCategoryId);

                        TransactionResponseDTO response = transactionService.recordIncomeExpense(req);

                        assertNotNull(response);
                        assertEquals(walletId, response.getWalletId());
                        assertEquals(categoryId, response.getCategoryId());
                        assertEquals("Ăn uống", response.getCategoryName());
                        assertEquals(new BigDecimal("200000.00"), response.getAmount());
                        assertEquals("EXPENSE", response.getTransactionType());
                        assertEquals("Đi siêu thị", response.getNote());
                        assertEquals(new BigDecimal("795000.00"), response.getWalletBalanceAfter());
                        assertTrue(response.hasFee());
                        assertEquals(new BigDecimal("5000.00"), response.getFeeAmount());
                        assertEquals(feeCategoryId, response.getFeeCategoryId());
                        assertEquals("Phí dịch vụ", response.getFeeCategoryName());

                        verify(transactionRepository).save(any(IncomeExpenseTransaction.class));
                        verify(walletRepository).save(activeWallet);
                        assertEquals(Money.of("795000.00"), activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("Tự động rollback xóa bù trừ giao dịch và khôi phục ví khi lưu ví gặp StorageException")
                void shouldRollbackWhenWalletRepositorySaveFails() {
                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));
                        doThrow(new StorageException("Lỗi ghi đĩa khi lưu ví", new java.io.IOException("disk full")))
                                        .doAnswer(inv -> inv.getArgument(0))
                                        .when(walletRepository).save(any(Wallet.class));

                        RecordTransactionRequestDTO req = new RecordTransactionRequestDTO(
                                        walletId, categoryId, new BigDecimal("100000.00"));

                        StorageException ex = assertThrows(
                                        StorageException.class, () -> transactionService.recordIncomeExpense(req));
                        assertTrue(ex.getMessage().contains("Lỗi ghi đĩa khi lưu ví"));

                        ArgumentCaptor<IncomeExpenseTransaction> txnCaptor = ArgumentCaptor
                                        .forClass(IncomeExpenseTransaction.class);
                        verify(transactionRepository).save(txnCaptor.capture());
                        verify(transactionRepository).deleteById(txnCaptor.getValue().getTransactionId());
                        assertEquals(Money.of("1000000.00"), activeWallet.getCurrentBalance());
                }
        }

        @Nested
        @DisplayName("UC-TXN-02: Lọc và phân trang sổ giao dịch (filterTransactions)")
        class FilterTransactionsTests {

                private IncomeExpenseTransaction txnIncome;
                private IncomeExpenseTransaction txnExpense;
                private IncomeExpenseTransaction txnExpenseWithFee;

                @BeforeEach
                void setUpFixtures() {
                        txnIncome = TransactionFactory.createIncomeExpense(
                                        TransactionType.INCOME,
                                        activeWallet,
                                        incomeCategory,
                                        Money.of("5000000.00"),
                                        TransactionDate.of(2026, 11, 5),
                                        "Lương tháng 11",
                                        Money.ZERO,
                                        null);

                        txnExpense = TransactionFactory.createIncomeExpense(
                                        TransactionType.EXPENSE,
                                        activeWallet,
                                        expenseCategory,
                                        Money.of("200000.00"),
                                        TransactionDate.of(2026, 11, 6),
                                        "Ăn tối với đồng nghiệp",
                                        Money.ZERO,
                                        null);

                        txnExpenseWithFee = TransactionFactory.createIncomeExpense(
                                        TransactionType.EXPENSE,
                                        activeWallet,
                                        expenseCategory,
                                        Money.of("300000.00"),
                                        TransactionDate.of(2026, 11, 7),
                                        "Mua sách chuyên ngành",
                                        Money.of("10000.00"),
                                        expenseFeeCategory);
                }

                @Test
                @DisplayName("Ném NullPointerException khi criteria là null")
                void shouldThrowWhenFilterCriteriaIsNull() {
                        assertThrows(NullPointerException.class, () -> transactionService.filterTransactions(null));
                }

                @Test
                @DisplayName("Ném BusinessException khi khoảng ngày không hợp lệ (startDate > endDate)")
                void shouldThrowWhenDateRangeIsInvalid() {
                        FilterTransactionsRequestDTO request = new FilterTransactionsRequestDTO(
                                        LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 5), null);

                        BusinessException ex = assertThrows(
                                        BusinessException.class, () -> transactionService.filterTransactions(request));
                        assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném IllegalArgumentException khi thông số phân trang không hợp lệ")
                void shouldThrowWhenPaginationParamsAreInvalid() {
                        FilterTransactionsRequestDTO invalidPage = new FilterTransactionsRequestDTO(
                                        null, null, null, null, null, "", 0, 50);
                        assertThrows(IllegalArgumentException.class,
                                        () -> transactionService.filterTransactions(invalidPage));

                        FilterTransactionsRequestDTO invalidSizeLow = new FilterTransactionsRequestDTO(
                                        null, null, null, null, null, "", 1, 9);
                        assertThrows(IllegalArgumentException.class,
                                        () -> transactionService.filterTransactions(invalidSizeLow));

                        FilterTransactionsRequestDTO invalidSizeHigh = new FilterTransactionsRequestDTO(
                                        null, null, null, null, null, "", 1, 101);
                        assertThrows(IllegalArgumentException.class,
                                        () -> transactionService.filterTransactions(invalidSizeHigh));
                }

                @Test
                @DisplayName("Lọc theo khoảng ngày và tập mã ví")
                void shouldFilterByDateRangeAndWallet() {
                        when(transactionRepository.findByDateRange(LocalDate.of(2026, 11, 5),
                                        LocalDate.of(2026, 11, 6)))
                                        .thenReturn(java.util.List.of(txnIncome, txnExpense));
                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));

                        FilterTransactionsRequestDTO request = new FilterTransactionsRequestDTO(
                                        LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 6),
                                        java.util.Set.of(walletId));

                        TransactionListResponseDTO response = transactionService.filterTransactions(request);

                        assertNotNull(response);
                        assertEquals(2, response.getTotalRecords());
                        assertEquals(2, response.getItemCount());
                        assertEquals(new BigDecimal("5000000.00"), response.getTotalIncome());
                        assertEquals(new BigDecimal("200000.00"), response.getTotalExpense());
                        assertEquals(new BigDecimal("4800000.00"), response.getNetCashFlow());
                }

                @Test
                @DisplayName("Lọc theo loại thu chi (INCOME)")
                void shouldFilterByTransactionTypeIncome() {
                        when(transactionRepository.findAll())
                                        .thenReturn(java.util.List.of(txnIncome, txnExpense, txnExpenseWithFee));
                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));

                        FilterTransactionsRequestDTO request = new FilterTransactionsRequestDTO(
                                        null, null, null, "INCOME", null, "");

                        TransactionListResponseDTO response = transactionService.filterTransactions(request);

                        assertNotNull(response);
                        assertEquals(1, response.getTotalRecords());
                        assertEquals("INCOME", response.getItems().get(0).getTransactionType());
                        assertEquals(new BigDecimal("5000000.00"), response.getTotalIncome());
                        assertEquals(new BigDecimal("0.00"), response.getTotalExpense());
                        assertEquals(new BigDecimal("5000000.00"), response.getNetCashFlow());
                }

                @Test
                @DisplayName("Lọc theo từ khóa ghi chú (không phân biệt hoa thường)")
                void shouldFilterByKeywordCaseInsensitive() {
                        when(transactionRepository.findAll())
                                        .thenReturn(java.util.List.of(txnIncome, txnExpense, txnExpenseWithFee));
                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));

                        FilterTransactionsRequestDTO request = new FilterTransactionsRequestDTO(
                                        null, null, null, null, null, "ăN tỐi");

                        TransactionListResponseDTO response = transactionService.filterTransactions(request);

                        assertNotNull(response);
                        assertEquals(1, response.getTotalRecords());
                        assertEquals("Ăn tối với đồng nghiệp", response.getItems().get(0).getNote());
                }

                @Test
                @DisplayName("Tính đúng tổng chi tiêu bao gồm cả phụ phí và phân trang chuẩn xác")
                void shouldCalculateExpenseWithFeeAndPaginate() {
                        when(transactionRepository.findAll())
                                        .thenReturn(java.util.List.of(txnIncome, txnExpense, txnExpenseWithFee));
                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));

                        // totalExpense = 200,000 + (300,000 + 10,000 fee) = 510,000
                        // totalIncome = 5,000,000
                        // netCashFlow = 5,000,000 - 510,000 = 4,490,000
                        FilterTransactionsRequestDTO request = new FilterTransactionsRequestDTO(
                                        null, null, null, null, null, "", 1, 10);

                        TransactionListResponseDTO response = transactionService.filterTransactions(request);

                        assertNotNull(response);
                        assertEquals(3, response.getTotalRecords());
                        assertEquals(1, response.getTotalPages());
                        assertEquals(3, response.getItemCount());
                        assertEquals(new BigDecimal("5000000.00"), response.getTotalIncome());
                        assertEquals(new BigDecimal("510000.00"), response.getTotalExpense());
                        assertEquals(new BigDecimal("4490000.00"), response.getNetCashFlow());
                        // Check sorting: 2026-11-07 (txnExpenseWithFee) should be first
                        assertEquals(txnExpenseWithFee.getTransactionId(),
                                        response.getItems().get(0).getTransactionId());
                }

                @Test
                @DisplayName("Lọc danh sách giao dịch theo mã nhóm nhu cầu cha (needGroupId)")
                void shouldFilterByParentNeedGroupId() {
                        when(transactionRepository.findAll())
                                        .thenReturn(java.util.List.of(txnIncome, txnExpense, txnExpenseWithFee));
                        when(walletRepository.findById(walletId)).thenReturn(Optional.of(activeWallet));

                        UUID expenseGroupId = expenseCategory.getNeedGroupId();
                        FilterTransactionsRequestDTO request = new FilterTransactionsRequestDTO(
                                        null, null, null, null, java.util.Set.of(expenseGroupId), "");

                        TransactionListResponseDTO response = transactionService.filterTransactions(request);

                        assertNotNull(response);
                        assertEquals(2, response.getTotalRecords());
                        assertEquals(2, response.getItemCount());
                        assertTrue(response.getItems().stream()
                                        .allMatch(item -> item.getTransactionType().equals("EXPENSE")));
                }
        }

        @Nested
        @DisplayName("UC-TXN-03: Cập nhật giao dịch thu chi (updateTransaction)")
        class UpdateTransactionTests {

                private UUID sampleTxnId;
                private UUID newCategoryId;
                private Category newExpenseCategory;
                private IncomeExpenseTransaction liveExpenseTxn;
                private IncomeExpenseTransaction liveExpenseWithFeeTxn;

                @BeforeEach
                void setUpUpdateFixtures() {
                        sampleTxnId = UUID.randomUUID();
                        newCategoryId = UUID.randomUUID();

                        newExpenseCategory = new Category(
                                        newCategoryId, "Mua sắm", UUID.randomUUID(),
                                        TransactionType.EXPENSE, "shop-icon", "#ffaa00");

                        // activeWallet balance: 1,000,000.00
                        // Create expense of 200,000.00 -> balance becomes 800,000.00
                        liveExpenseTxn = TransactionFactory.createIncomeExpense(
                                        TransactionType.EXPENSE,
                                        activeWallet,
                                        expenseCategory,
                                        Money.of("200000.00"),
                                        TransactionDate.of(2026, 11, 5),
                                        "Ăn trưa ban đầu",
                                        Money.ZERO,
                                        null);
                        liveExpenseTxn.applyToWallet();

                        // liveExpenseWithFeeTxn: expense of 100,000.00 + fee 5,000.00
                        Wallet feeWallet = new Wallet(
                                        UUID.randomUUID(), "Ví phụ", "", false,
                                        LocalDateTime.now(), new ActiveWalletState(), Money.of("1000000.00"));
                        liveExpenseWithFeeTxn = TransactionFactory.createIncomeExpense(
                                        TransactionType.EXPENSE,
                                        feeWallet,
                                        expenseCategory,
                                        Money.of("100000.00"),
                                        TransactionDate.of(2026, 11, 6),
                                        "Phí giao dịch cũ",
                                        Money.of("5000.00"),
                                        expenseFeeCategory);
                        liveExpenseWithFeeTxn.applyToWallet();
                }

                @Test
                @DisplayName("Ném NullPointerException khi request là null")
                void shouldThrowWhenRequestIsNull() {
                        assertThrows(NullPointerException.class, () -> transactionService.updateTransaction(null));
                }

                @Test
                @DisplayName("Ném BusinessException khi newAmount <= 0")
                void shouldThrowWhenNewAmountIsZeroOrNegative() {
                        UpdateTransactionRequestDTO zeroReq = new UpdateTransactionRequestDTO(
                                        sampleTxnId, activeWallet.getId(), BigDecimal.ZERO, categoryId);
                        BusinessException ex1 = assertThrows(
                                        BusinessException.class, () -> transactionService.updateTransaction(zeroReq));
                        assertEquals(ErrorCode.ERR_FIN_001, ex1.getErrorCode());

                        UpdateTransactionRequestDTO negReq = new UpdateTransactionRequestDTO(
                                        sampleTxnId, activeWallet.getId(), new BigDecimal("-10000.00"), categoryId);
                        BusinessException ex2 = assertThrows(
                                        BusinessException.class, () -> transactionService.updateTransaction(negReq));
                        assertEquals(ErrorCode.ERR_FIN_001, ex2.getErrorCode());
                }

                @Test
                @DisplayName("Ném BusinessException khi newFeeAmount âm")
                void shouldThrowWhenNewFeeIsNegative() {
                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        sampleTxnId, activeWallet.getId(), new BigDecimal("100000.00"), LocalDate.now(),
                                        categoryId, "",
                                        new BigDecimal("-1000.00"), feeCategoryId);

                        BusinessException ex = assertThrows(
                                        BusinessException.class, () -> transactionService.updateTransaction(req));
                        assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném BusinessException khi newFeeAmount dương nhưng newFeeCategoryId null")
                void shouldThrowWhenNewFeeIsPositiveButFeeCategoryIdIsNull() {
                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        sampleTxnId, activeWallet.getId(), new BigDecimal("100000.00"), LocalDate.now(),
                                        categoryId, "",
                                        new BigDecimal("5000.00"), null);

                        BusinessException ex = assertThrows(
                                        BusinessException.class, () -> transactionService.updateTransaction(req));
                        assertEquals(ErrorCode.ERR_FEE_002, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném EntityNotFoundException khi giao dịch không tồn tại")
                void shouldThrowWhenTransactionNotFound() {
                        when(transactionRepository.findById(sampleTxnId)).thenReturn(Optional.empty());

                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        sampleTxnId, activeWallet.getId(), new BigDecimal("150000.00"), categoryId);

                        EntityNotFoundException ex = assertThrows(
                                        EntityNotFoundException.class, () -> transactionService.updateTransaction(req));
                        assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném EntityNotFoundException khi danh mục mới không tồn tại")
                void shouldThrowWhenNewCategoryNotFound() {
                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.empty());

                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        liveExpenseTxn.getTransactionId(), activeWallet.getId(),
                                        new BigDecimal("150000.00"), newCategoryId);

                        EntityNotFoundException ex = assertThrows(
                                        EntityNotFoundException.class, () -> transactionService.updateTransaction(req));
                        assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném BusinessException khi danh mục mới không tương thích với loại thu chi")
                void shouldThrowWhenNewCategoryTypeMismatches() {
                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(incomeCategory.getCategoryId()))
                                        .thenReturn(Optional.of(incomeCategory));

                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        liveExpenseTxn.getTransactionId(), activeWallet.getId(),
                                        new BigDecimal("150000.00"), incomeCategory.getCategoryId());

                        BusinessException ex = assertThrows(
                                        BusinessException.class, () -> transactionService.updateTransaction(req));
                        assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném InsufficientBalanceException và bảo toàn số dư khi số tiền mới vượt quá số dư khả dụng")
                void shouldThrowWhenInsufficientBalanceAfterUpdate() {
                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

                        // activeWallet hiện có 800k (sau khi trừ 200k). Revert lại được 1M. Nhưng sửa
                        // thành 1.5M -> InsufficientBalance!
                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        liveExpenseTxn.getTransactionId(), activeWallet.getId(),
                                        new BigDecimal("1500000.00"), categoryId);

                        assertThrows(InsufficientBalanceException.class,
                                        () -> transactionService.updateTransaction(req));
                        // Bảo toàn số dư ví ban đầu (800k)
                        assertEquals(Money.of("800000.00"), activeWallet.getCurrentBalance());
                        // Bảo toàn số tiền giao dịch (200k)
                        assertEquals(Money.of("200000.00"), liveExpenseTxn.getAmount());
                }

                @Test
                @DisplayName("Cập nhật giao dịch chi tiêu thành công: đổi số tiền, danh mục, ngày, ghi chú và thêm phụ phí")
                void shouldUpdateExpenseDetailsSuccessfully() {
                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newExpenseCategory));
                        when(categoryRepository.findById(feeCategoryId)).thenReturn(Optional.of(expenseFeeCategory));

                        // Ban đầu: trừ 200k -> số dư 800k.
                        // Sửa thành: chi 350k + phí 10k -> tổng trừ mới 360k.
                        // Số dư sau cập nhật: 1,000,000 - 360,000 = 640,000.00
                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        liveExpenseTxn.getTransactionId(),
                                        activeWallet.getId(),
                                        new BigDecimal("350000.00"),
                                        LocalDate.of(2026, 11, 10),
                                        newCategoryId,
                                        "Ăn tối buffet hải sản",
                                        new BigDecimal("10000.00"),
                                        feeCategoryId);

                        TransactionResponseDTO response = transactionService.updateTransaction(req);

                        assertNotNull(response);
                        assertEquals(liveExpenseTxn.getTransactionId(), response.getTransactionId());
                        assertEquals(new BigDecimal("350000.00"), response.getAmount());
                        assertEquals("EXPENSE", response.getTransactionType());
                        assertEquals(newCategoryId, response.getCategoryId());
                        assertEquals("Mua sắm", response.getCategoryName());
                        assertEquals("Ăn tối buffet hải sản", response.getNote());
                        assertTrue(response.hasFee());
                        assertEquals(new BigDecimal("10000.00"), response.getFeeAmount());
                        assertEquals(feeCategoryId, response.getFeeCategoryId());
                        assertEquals("Phí dịch vụ", response.getFeeCategoryName());
                        assertEquals(new BigDecimal("640000.00"), response.getWalletBalanceAfter());

                        verify(transactionRepository).save(liveExpenseTxn);
                        verify(walletRepository).save(activeWallet);
                        assertEquals(Money.of("640000.00"), activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("Gỡ bỏ phụ phí và hoàn tiền khi sửa giao dịch có phí thành không có phí (newFeeAmount = ZERO)")
                void shouldRemoveFeeWhenUpdatedFeeIsZero() {
                        Wallet feeWallet = liveExpenseWithFeeTxn.getWallet();
                        // feeWallet ban đầu 1M, sau khi tạo giao dịch 100k + fee 5k -> số dư còn
                        // 895,000.00
                        assertEquals(Money.of("895000.00"), feeWallet.getCurrentBalance());

                        when(transactionRepository.findById(liveExpenseWithFeeTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseWithFeeTxn));
                        when(walletRepository.findById(feeWallet.getId())).thenReturn(Optional.of(feeWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

                        // Sửa lại thành chi 100k nhưng fee = 0 -> số dư ví trở thành 900,000.00
                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        liveExpenseWithFeeTxn.getTransactionId(),
                                        feeWallet.getId(),
                                        new BigDecimal("100000.00"),
                                        LocalDate.of(2026, 11, 6),
                                        categoryId,
                                        "Giao dịch không còn phí",
                                        BigDecimal.ZERO,
                                        null);

                        TransactionResponseDTO response = transactionService.updateTransaction(req);

                        assertNotNull(response);
                        assertFalse(response.hasFee());
                        assertEquals(BigDecimal.ZERO, response.getFeeAmount());
                        assertNull(response.getFeeCategoryId());
                        assertEquals(new BigDecimal("900000.00"), response.getWalletBalanceAfter());
                        assertEquals(Money.of("900000.00"), feeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("Cập nhật giao dịch chuyển sang ví khác: hoàn tiền ví cũ và trừ tiền ví mới")
                void shouldUpdateTransactionSwitchingWalletsSuccessfully() {
                        UUID newWalletId = UUID.randomUUID();
                        Wallet newWallet = new Wallet(
                                        newWalletId, "Ví MB Bank", "", false,
                                        LocalDateTime.now(), new ActiveWalletState(), Money.of("500000.00"));

                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(activeWallet));
                        when(walletRepository.findById(newWalletId)).thenReturn(Optional.of(newWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        liveExpenseTxn.getTransactionId(),
                                        newWalletId,
                                        new BigDecimal("300000.00"),
                                        LocalDate.of(2026, 11, 10),
                                        categoryId,
                                        "Chuyển ví thanh toán",
                                        BigDecimal.ZERO,
                                        null);

                        TransactionResponseDTO response = transactionService.updateTransaction(req);

                        assertNotNull(response);
                        assertEquals(newWalletId, response.getWalletId());
                        assertEquals(new BigDecimal("300000.00"), response.getAmount());
                        assertEquals(new BigDecimal("200000.00"), response.getWalletBalanceAfter());

                        verify(walletRepository).save(activeWallet);
                        verify(walletRepository).save(newWallet);
                        verify(transactionRepository).save(liveExpenseTxn);
                        assertEquals(Money.of("1000000.00"), activeWallet.getCurrentBalance());
                        assertEquals(Money.of("200000.00"), newWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("Rollback cả 2 ví và giao dịch khi lưu ví đích gặp StorageException")
                void shouldRollbackBothWalletsWhenStorageExceptionOccursOnSwitchingWallets() {
                        UUID newWalletId = UUID.randomUUID();
                        Wallet newWallet = new Wallet(
                                        newWalletId, "Ví MB Bank", "", false,
                                        LocalDateTime.now(), new ActiveWalletState(), Money.of("500000.00"));

                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(activeWallet));
                        when(walletRepository.findById(newWalletId)).thenReturn(Optional.of(newWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

                        doAnswer(inv -> inv.getArgument(0)).when(walletRepository).save(activeWallet);
                        doThrow(new StorageException("Lỗi lưu ví đích", new java.io.IOException("disk full")))
                                        .when(walletRepository).save(newWallet);

                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        liveExpenseTxn.getTransactionId(),
                                        newWalletId,
                                        new BigDecimal("300000.00"),
                                        LocalDate.of(2026, 11, 10),
                                        categoryId,
                                        "Chuyển ví lỗi đĩa",
                                        BigDecimal.ZERO,
                                        null);

                        assertThrows(StorageException.class, () -> transactionService.updateTransaction(req));

                        assertEquals(Money.of("800000.00"), activeWallet.getCurrentBalance());
                        assertEquals(Money.of("500000.00"), newWallet.getCurrentBalance());
                        assertEquals(activeWallet.getId(), liveExpenseTxn.getWallet().getId());
                        assertEquals(Money.of("200000.00"), liveExpenseTxn.getAmount());
                }

                @Test
                @DisplayName("Cho phép giữ nguyên danh mục cũ khi danh mục đó đã bị xóa mềm (FR-TXN-04)")
                void shouldAllowKeepingDeletedCategoryWhenCategoryUnchanged() {
                        Category deletedExpenseCategory = new Category(
                                        categoryId, "Ăn uống cũ", UUID.randomUUID(),
                                        TransactionType.EXPENSE, "food-icon", "#ff0000");
                        deletedExpenseCategory.markDeleted();

                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(deletedExpenseCategory));

                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        liveExpenseTxn.getTransactionId(),
                                        activeWallet.getId(),
                                        new BigDecimal("250000.00"),
                                        LocalDate.of(2026, 11, 10),
                                        categoryId,
                                        "Giữ danh mục cũ",
                                        BigDecimal.ZERO,
                                        null);

                        TransactionResponseDTO response = transactionService.updateTransaction(req);
                        assertNotNull(response);
                        assertEquals(categoryId, response.getCategoryId());
                        assertEquals(new BigDecimal("250000.00"), response.getAmount());
                }

                @Test
                @DisplayName("Bảo toàn phụ phí hiện tại khi newFeeAmount để trống (null)")
                void shouldPreserveExistingFeeWhenNewFeeAmountIsNull() {
                        Wallet feeWallet = liveExpenseWithFeeTxn.getWallet();
                        when(transactionRepository.findById(liveExpenseWithFeeTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseWithFeeTxn));
                        when(walletRepository.findById(feeWallet.getId())).thenReturn(Optional.of(feeWallet));
                        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expenseCategory));

                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        liveExpenseWithFeeTxn.getTransactionId(),
                                        feeWallet.getId(),
                                        new BigDecimal("150000.00"),
                                        LocalDate.of(2026, 11, 6),
                                        categoryId,
                                        "Giữ nguyên phụ phí",
                                        null,
                                        null);

                        TransactionResponseDTO response = transactionService.updateTransaction(req);
                        assertNotNull(response);
                        assertTrue(response.hasFee());
                        assertEquals(new BigDecimal("5000.00"), response.getFeeAmount());
                        assertEquals(feeCategoryId, response.getFeeCategoryId());
                        assertEquals(new BigDecimal("845000.00"), response.getWalletBalanceAfter());
                }

                @Test
                @DisplayName("Từ chối cập nhật khi ví đích mới ở trạng thái ARCHIVED")
                void shouldRejectUpdatingTransactionWithInactiveTargetWallet() {
                        UUID archivedWalletId = UUID.randomUUID();
                        Wallet archivedWallet = new Wallet(
                                        archivedWalletId, "Ví Đã Lưu Trữ", "", false,
                                        LocalDateTime.now(), new ArchivedWalletState(), Money.of("500000.00"));

                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(activeWallet));
                        when(walletRepository.findById(archivedWalletId)).thenReturn(Optional.of(archivedWallet));

                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        liveExpenseTxn.getTransactionId(),
                                        archivedWalletId,
                                        new BigDecimal("150000.00"),
                                        categoryId);

                        BusinessException ex = assertThrows(
                                        BusinessException.class, () -> transactionService.updateTransaction(req));
                        assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Rollback toàn diện cả giao dịch và số dư ví khi lưu ví gặp StorageException")
                void shouldRollbackWhenStorageExceptionOccurs() {
                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(activeWallet));
                        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newExpenseCategory));
                        doThrow(new StorageException("Lỗi ghi ví", new java.io.IOException("disk full")))
                                        .doAnswer(inv -> inv.getArgument(0))
                                        .when(walletRepository).save(any(Wallet.class));

                        UpdateTransactionRequestDTO req = new UpdateTransactionRequestDTO(
                                        liveExpenseTxn.getTransactionId(), activeWallet.getId(),
                                        new BigDecimal("300000.00"), newCategoryId);

                        StorageException ex = assertThrows(
                                        StorageException.class, () -> transactionService.updateTransaction(req));
                        assertTrue(ex.getMessage().contains("Lỗi ghi ví"));

                        // Đảm bảo số dư ví và giao dịch được hoàn nguyên
                        assertEquals(Money.of("800000.00"), activeWallet.getCurrentBalance());
                        assertEquals(Money.of("200000.00"), liveExpenseTxn.getAmount());
                }
        }

        @Nested
        @DisplayName("UC-TXN-04: Xóa giao dịch thu chi (deleteTransaction)")
        class DeleteTransactionTests {

                private UUID sampleTxnId;
                private IncomeExpenseTransaction liveExpenseTxn;
                private IncomeExpenseTransaction liveExpenseWithFeeTxn;
                private IncomeExpenseTransaction liveIncomeTxn;
                private Wallet feeWallet;

                @BeforeEach
                void setUpDeleteFixtures() {
                        sampleTxnId = UUID.randomUUID();

                        // liveExpenseTxn: expense 200k trên activeWallet (balance từ 1M thành 800k)
                        liveExpenseTxn = TransactionFactory.createIncomeExpense(
                                        TransactionType.EXPENSE,
                                        activeWallet,
                                        expenseCategory,
                                        Money.of("200000.00"),
                                        TransactionDate.of(2026, 11, 5),
                                        "Ăn trưa",
                                        Money.ZERO,
                                        null);
                        liveExpenseTxn.applyToWallet();

                        // feeWallet: balance 1M, expense 100k + fee 5k -> balance còn 895k
                        feeWallet = new Wallet(
                                        UUID.randomUUID(), "Ví phụ", "", false,
                                        LocalDateTime.now(), new ActiveWalletState(), Money.of("1000000.00"));
                        liveExpenseWithFeeTxn = TransactionFactory.createIncomeExpense(
                                        TransactionType.EXPENSE,
                                        feeWallet,
                                        expenseCategory,
                                        Money.of("100000.00"),
                                        TransactionDate.of(2026, 11, 6),
                                        "Phí giao dịch",
                                        Money.of("5000.00"),
                                        expenseFeeCategory);
                        liveExpenseWithFeeTxn.applyToWallet();

                        // liveIncomeTxn: thu 500k trên activeWallet
                        Wallet incomeWallet = new Wallet(
                                        UUID.randomUUID(), "Ví thu", "", false,
                                        LocalDateTime.now(), new ActiveWalletState(), Money.of("100000.00"));
                        liveIncomeTxn = TransactionFactory.createIncomeExpense(
                                        TransactionType.INCOME,
                                        incomeWallet,
                                        incomeCategory,
                                        Money.of("500000.00"),
                                        TransactionDate.of(2026, 11, 5),
                                        "Thu thưởng",
                                        Money.ZERO,
                                        null);
                        liveIncomeTxn.applyToWallet(); // balance thành 600k
                }

                @Test
                @DisplayName("Ném NullPointerException khi transactionId là null")
                void shouldThrowWhenTransactionIdIsNull() {
                        assertThrows(NullPointerException.class, () -> transactionService.deleteTransaction(null));
                }

                @Test
                @DisplayName("Ném EntityNotFoundException khi không tìm thấy giao dịch")
                void shouldThrowWhenTransactionNotFound() {
                        when(transactionRepository.findById(sampleTxnId)).thenReturn(Optional.empty());

                        EntityNotFoundException ex = assertThrows(
                                        EntityNotFoundException.class,
                                        () -> transactionService.deleteTransaction(sampleTxnId));
                        assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném BusinessException khi ví không ở trạng thái ACTIVE")
                void shouldThrowWhenWalletNotActive() {
                        Wallet archivedWallet = new Wallet(
                                        activeWallet.getId(), "Ví Lưu Trữ", "", false,
                                        LocalDateTime.now(), new ArchivedWalletState(), Money.of("800000.00"));

                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(archivedWallet));

                        BusinessException ex = assertThrows(
                                        BusinessException.class,
                                        () -> transactionService.deleteTransaction(liveExpenseTxn.getTransactionId()));
                        assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Ném InsufficientBalanceException khi hoàn nguyên giao dịch thu làm âm số dư ví")
                void shouldThrowWhenRevertingIncomeCausesNegativeBalance() {
                        // liveIncomeTxn là khoản thu 500k. Ví hiện chỉ có 100k (đã chi tiêu gần hết).
                        Wallet spentWallet = new Wallet(
                                        liveIncomeTxn.getWallet().getId(), "Ví cạn kiệt", "", false,
                                        LocalDateTime.now(), new ActiveWalletState(), Money.of("100000.00"));

                        when(transactionRepository.findById(liveIncomeTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveIncomeTxn));
                        when(walletRepository.findById(liveIncomeTxn.getWallet().getId()))
                                        .thenReturn(Optional.of(spentWallet));

                        assertThrows(
                                        InsufficientBalanceException.class,
                                        () -> transactionService.deleteTransaction(liveIncomeTxn.getTransactionId()));

                        verify(transactionRepository, never()).deleteById(any());
                }

                @Test
                @DisplayName("Xóa giao dịch chi tiêu kèm phụ phí thành công, hoàn trả số tiền gốc và phí về ví")
                void shouldDeleteExpenseWithFeeSuccessfully() {
                        when(transactionRepository.findById(liveExpenseWithFeeTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseWithFeeTxn));
                        when(walletRepository.findById(feeWallet.getId())).thenReturn(Optional.of(feeWallet));

                        assertEquals(Money.of("895000.00"), feeWallet.getCurrentBalance());

                        transactionService.deleteTransaction(liveExpenseWithFeeTxn.getTransactionId());

                        verify(transactionRepository).deleteById(liveExpenseWithFeeTxn.getTransactionId());
                        verify(walletRepository).save(feeWallet);
                        // 895,000 + 100,000 (tiền chi) + 5,000 (phụ phí) = 1,000,000.00
                        assertEquals(Money.of("1000000.00"), feeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("Xóa giao dịch chi tiêu không có phụ phí thành công, hoàn trả số tiền về ví")
                void shouldDeleteExpenseWithoutFeeSuccessfully() {
                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(activeWallet));

                        transactionService.deleteTransaction(liveExpenseTxn.getTransactionId());

                        verify(transactionRepository).deleteById(liveExpenseTxn.getTransactionId());
                        verify(walletRepository).save(activeWallet);
                        // 800,000 + 200,000 = 1,000,000.00
                        assertEquals(Money.of("1000000.00"), activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("Rollback phục hồi giao dịch và số dư ví khi lưu ví phát sinh StorageException")
                void shouldRollbackWhenStorageExceptionOccursOnDeletion() {
                        when(transactionRepository.findById(liveExpenseTxn.getTransactionId()))
                                        .thenReturn(Optional.of(liveExpenseTxn));
                        when(walletRepository.findById(activeWallet.getId())).thenReturn(Optional.of(activeWallet));
                        doThrow(new StorageException("Lỗi lưu ví", new java.io.IOException("disk full")))
                                        .doAnswer(inv -> inv.getArgument(0))
                                        .when(walletRepository).save(any(Wallet.class));

                        StorageException ex = assertThrows(
                                        StorageException.class,
                                        () -> transactionService.deleteTransaction(liveExpenseTxn.getTransactionId()));
                        assertTrue(ex.getMessage().contains("Lỗi lưu ví"));

                        // Kiểm chứng: giao dịch được phục hồi lại vào repo và số dư ví in-memory được
                        // giữ nguyên
                        verify(transactionRepository).save(liveExpenseTxn);
                        assertEquals(Money.of("800000.00"), activeWallet.getCurrentBalance());
                }
        }
}
