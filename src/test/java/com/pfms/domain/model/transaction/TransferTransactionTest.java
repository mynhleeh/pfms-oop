package com.pfms.domain.model.transaction;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TransferTransaction Domain Model Tests")
class TransferTransactionTest {

    private Wallet sourceWallet;
    private Wallet destinationWallet;
    private Category feeCategory;
    private TransactionDate validDate;

    @BeforeEach
    void setUp() {
        sourceWallet = new Wallet("Ví nguồn", "Ví chuyển đi", false);
        sourceWallet.credit(Money.of(5_000_000));

        destinationWallet = new Wallet("Ví đích", "Ví nhận về", false);
        destinationWallet.credit(Money.of(2_000_000));

        feeCategory = new Category(
                UUID.randomUUID(), "Phí chuyển khoản", UUID.randomUUID(),
                TransactionType.EXPENSE, "bank_icon", "#9E9E9E");

        validDate = TransactionDate.of(2026, 3, 20);
    }

    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo giao dịch chuyển tiền hợp lệ không phụ phí thành công")
        void testCreateTransferTransaction_WithoutFee_Success() {
            UUID txId = UUID.randomUUID();
            Money amount = Money.of(1_000_000);

            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, amount, validDate, "Chuyển tiền tiết kiệm", null);

            assertEquals(txId, tx.getTransactionId());
            assertEquals(sourceWallet, tx.getSourceWallet());
            assertEquals(destinationWallet, tx.getDestinationWallet());
            assertEquals(amount, tx.getAmount());
            assertEquals(validDate, tx.getTransactionDate());
            assertEquals("Chuyển tiền tiết kiệm", tx.getNote());
            assertTrue(tx.getFee().isEmpty());
            assertEquals(amount, tx.getTotalSourceDeduction());
            assertNotNull(tx.getCreatedAt());
        }

        @Test
        @DisplayName("Khởi tạo giao dịch chuyển tiền hợp lệ có phụ phí thành công")
        void testCreateTransferTransaction_WithFee_Success() {
            UUID txId = UUID.randomUUID();
            Money amount = Money.of(2_000_000);
            Money feeAmount = Money.of(11_000);
            TransactionFee fee = new TransactionFee(
                    UUID.randomUUID(), txId, feeAmount, sourceWallet, feeCategory);

            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, amount, validDate, "Chuyển tiền liên ngân hàng", fee);

            assertEquals(txId, tx.getTransactionId());
            assertEquals(amount, tx.getAmount());
            assertTrue(tx.getFee().isPresent());
            assertEquals(fee, tx.getFee().get());
            // Tổng tiền trích từ ví nguồn bao gồm cả tiền chuyển và phụ phí
            assertEquals(amount.add(feeAmount), tx.getTotalSourceDeduction());
        }

        @Test
        @DisplayName("applyToWallet() không phụ phí: trừ ví nguồn và cộng ví đích chính xác")
        void testApplyToWallet_WithoutFee_Success() {
            UUID txId = UUID.randomUUID();
            Money initialSource = sourceWallet.getCurrentBalance(); // 5_000_000
            Money initialDest = destinationWallet.getCurrentBalance(); // 2_000_000
            Money transferAmount = Money.of(1_500_000);

            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, transferAmount, validDate, "Chuyển tiền", null);

            tx.applyToWallet();

            // Ví nguồn trừ 1.5M, ví đích cộng 1.5M, tổng tài sản bảo toàn
            assertEquals(initialSource.subtract(transferAmount), sourceWallet.getCurrentBalance());
            assertEquals(initialDest.add(transferAmount), destinationWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("applyToWallet() có phụ phí: trừ ví nguồn amount + fee, cộng ví đích amount")
        void testApplyToWallet_WithFee_Success() {
            UUID txId = UUID.randomUUID();
            Money initialSource = sourceWallet.getCurrentBalance(); // 5_000_000
            Money initialDest = destinationWallet.getCurrentBalance(); // 2_000_000
            Money transferAmount = Money.of(1_000_000);
            Money feeAmount = Money.of(7_700);

            TransactionFee fee = new TransactionFee(
                    UUID.randomUUID(), txId, feeAmount, sourceWallet, feeCategory);
            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, transferAmount, validDate, "Chuyển tiền có phí", fee);

            tx.applyToWallet();

            // Ví nguồn: 5M - 1M - 7.7k = 3.992.300 VNĐ
            assertEquals(initialSource.subtract(transferAmount).subtract(feeAmount), sourceWallet.getCurrentBalance());
            // Ví đích: 2M + 1M = 3M
            assertEquals(initialDest.add(transferAmount), destinationWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("revertFromWallet() không phụ phí: hoàn nguyên trừ ví đích và cộng trả ví nguồn")
        void testRevertFromWallet_WithoutFee_Success() {
            UUID txId = UUID.randomUUID();
            Money initialSource = sourceWallet.getCurrentBalance();
            Money initialDest = destinationWallet.getCurrentBalance();
            Money amount = Money.of(500_000);

            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, amount, validDate, "Chuyển nhầm", null);

            tx.applyToWallet();
            tx.revertFromWallet();

            assertEquals(initialSource, sourceWallet.getCurrentBalance());
            assertEquals(initialDest, destinationWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("revertFromWallet() có phụ phí: hoàn phí và hoàn tiền chuyển về ví nguồn, trừ ví đích")
        void testRevertFromWallet_WithFee_Success() {
            UUID txId = UUID.randomUUID();
            Money initialSource = sourceWallet.getCurrentBalance();
            Money initialDest = destinationWallet.getCurrentBalance();
            Money amount = Money.of(800_000);
            Money feeAmount = Money.of(10_000);

            TransactionFee fee = new TransactionFee(
                    UUID.randomUUID(), txId, feeAmount, sourceWallet, feeCategory);
            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, amount, validDate, "Chuyển có phí", fee);

            tx.applyToWallet();
            tx.revertFromWallet();

            assertEquals(initialSource, sourceWallet.getCurrentBalance());
            assertEquals(initialDest, destinationWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("reconstitute() giữ nguyên createdAt lịch sử và cho phép ví nguồn/đích đã lưu trữ hoặc xóa mềm")
        void testReconstitute_PreservesHistoricalCreatedAtAndAllowsArchivedOrDeletedWallets() {
            UUID txId = UUID.randomUUID();
            LocalDateTime historicalTime = LocalDateTime.of(2024, 1, 20, 18, 10, 0);
            Wallet archivedSource = sourceWallet.copy();
            archivedSource.archive();
            Wallet deletedDest = destinationWallet.copy();
            deletedDest.delete();
            feeCategory.markDeleted();
            TransactionFee historicalFee = TransactionFee.reconstitute(
                    UUID.randomUUID(), txId, Money.of(3_300), archivedSource, feeCategory);

            TransferTransaction restored = TransferTransaction.reconstitute(
                    txId, archivedSource, deletedDest, Money.of(1_200_000), validDate,
                    "Khôi phục chuyển khoản", historicalFee, historicalTime);

            assertEquals(txId, restored.getTransactionId());
            assertSame(archivedSource, restored.getSourceWallet());
            assertSame(deletedDest, restored.getDestinationWallet());
            assertTrue(restored.getFee().isPresent());
            assertSame(historicalFee, restored.getFee().get());
            assertEquals(historicalTime, restored.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Null and Invalid Input Tests")
    class NullAndInvalidInputTests {

        @Test
        @DisplayName("Khởi tạo với sourceWallet null ném NullPointerException")
        void testConstructor_NullSourceWallet_ThrowsException() {
            assertThrows(NullPointerException.class, () ->
                    new TransferTransaction(
                            UUID.randomUUID(), null, destinationWallet,
                            Money.of(100_000), validDate, "Note", null));
        }

        @Test
        @DisplayName("Khởi tạo với destinationWallet null ném NullPointerException")
        void testConstructor_NullDestinationWallet_ThrowsException() {
            assertThrows(NullPointerException.class, () ->
                    new TransferTransaction(
                            UUID.randomUUID(), sourceWallet, null,
                            Money.of(100_000), validDate, "Note", null));
        }

        @Test
        @DisplayName("Khởi tạo với ví nguồn trùng ví đích ném BusinessException ERR_TRF_001")
        void testConstructor_SameSourceAndDestination_ThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransferTransaction(
                            UUID.randomUUID(), sourceWallet, sourceWallet,
                            Money.of(100_000), validDate, "Trùng ví", null));
            assertEquals(ErrorCode.ERR_TRF_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Khởi tạo với ví nguồn đã bị xóa ném BusinessException ERR_WAL_001")
        void testConstructor_DeletedSourceWallet_ThrowsException() {
            Wallet deletedWallet = new Wallet("Ví đã xóa");
            deletedWallet.delete();

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransferTransaction(
                            UUID.randomUUID(), deletedWallet, destinationWallet,
                            Money.of(100_000), validDate, "Note", null));
            assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Khởi tạo với ví đích đã bị xóa ném BusinessException ERR_WAL_001")
        void testConstructor_DeletedDestinationWallet_ThrowsException() {
            Wallet deletedWallet = new Wallet("Ví đích đã xóa");
            deletedWallet.delete();

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransferTransaction(
                            UUID.randomUUID(), sourceWallet, deletedWallet,
                            Money.of(100_000), validDate, "Note", null));
            assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Khởi tạo với phụ phí gắn vào ví khác ví nguồn ném BusinessException ERR_FEE_001")
        void testConstructor_FeeWalletDifferentFromSource_ThrowsException() {
            UUID txId = UUID.randomUUID();
            // Phụ phí gắn vào destinationWallet thay vì sourceWallet -> Không hợp lệ
            TransactionFee fee = new TransactionFee(
                    UUID.randomUUID(), txId, Money.of(5_000), destinationWallet, feeCategory);

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransferTransaction(
                            txId, sourceWallet, destinationWallet,
                            Money.of(100_000), validDate, "Sai ví phí", fee));
            assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Khởi tạo với số tiền bằng không hoặc âm ném BusinessException ERR_TRF_002")
        void testConstructor_ZeroOrNegativeAmount_ThrowsBusinessException_ERR_TRF_002() {
            BusinessException exZero = assertThrows(BusinessException.class, () ->
                    new TransferTransaction(
                            UUID.randomUUID(), sourceWallet, destinationWallet,
                            Money.ZERO, validDate, "Tiền bằng 0", null));
            assertEquals(ErrorCode.ERR_TRF_002, exZero.getErrorCode());

            BusinessException exNegative = assertThrows(BusinessException.class, () ->
                    new TransferTransaction(
                            UUID.randomUUID(), sourceWallet, destinationWallet,
                            Money.of(-100_000), validDate, "Tiền âm", null));
            assertEquals(ErrorCode.ERR_TRF_002, exNegative.getErrorCode());
        }

        @Test
        @DisplayName("Khởi tạo với transactionDate null ném NullPointerException")
        void testConstructor_NullTransactionDate_ThrowsException() {
            assertThrows(NullPointerException.class, () ->
                    new TransferTransaction(
                            UUID.randomUUID(), sourceWallet, destinationWallet,
                            Money.of(100_000), null, "Null date", null));
        }

        @Test
        @DisplayName("Khởi tạo với phụ phí có parentTransactionId không khớp ném BusinessException ERR_FEE_001")
        void testConstructor_FeeWithMismatchedParentId_ThrowsException() {
            UUID txId = UUID.randomUUID();
            UUID differentTxId = UUID.randomUUID();
            TransactionFee fee = new TransactionFee(
                    UUID.randomUUID(), differentTxId, Money.of(5_000), sourceWallet, feeCategory);

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransferTransaction(
                            txId, sourceWallet, destinationWallet,
                            Money.of(100_000), validDate, "Sai parent ID", fee));
            assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("reconstitute() vẫn từ chối trùng ví, số tiền không dương và createdAt null")
        void testReconstitute_StillRejectsSameWalletInvalidAmountAndNullCreatedAt() {
            LocalDateTime historicalTime = LocalDateTime.of(2024, 1, 20, 18, 10, 0);

            BusinessException sameWalletEx = assertThrows(BusinessException.class, () ->
                    TransferTransaction.reconstitute(
                            UUID.randomUUID(), sourceWallet, sourceWallet,
                            Money.of(100_000), validDate, "Trùng ví", null, historicalTime));
            assertEquals(ErrorCode.ERR_TRF_001, sameWalletEx.getErrorCode());

            BusinessException zeroAmountEx = assertThrows(BusinessException.class, () ->
                    TransferTransaction.reconstitute(
                            UUID.randomUUID(), sourceWallet, destinationWallet,
                            Money.ZERO, validDate, "Tiền 0", null, historicalTime));
            assertEquals(ErrorCode.ERR_TRF_002, zeroAmountEx.getErrorCode());

            assertThrows(NullPointerException.class, () ->
                    TransferTransaction.reconstitute(
                            UUID.randomUUID(), sourceWallet, destinationWallet,
                            Money.of(100_000), validDate, "Null time", null, null));
        }
    }

    @Nested
    @DisplayName("State Transitions and Invariants Tests")
    class StateTransitionsAndInvariantsTests {

        @Test
        @DisplayName("applyToWallet() khi ví nguồn không đủ tiền chuyển ném InsufficientBalanceException")
        void testApplyToWallet_SourceInsufficientBalance_ThrowsException() {
            UUID txId = UUID.randomUUID();
            Money excessiveAmount = Money.of(10_000_000); // Ví nguồn có 5M

            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, excessiveAmount, validDate, "Quá số dư", null);

            assertThrows(InsufficientBalanceException.class, tx::applyToWallet);
            // Bảo toàn số dư cả hai ví không bị biến đổi dở dang
            assertEquals(Money.of(5_000_000), sourceWallet.getCurrentBalance());
            assertEquals(Money.of(2_000_000), destinationWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("applyToWallet() khi ví nguồn đủ tiền chuyển nhưng không đủ trả thêm phụ phí ném InsufficientBalanceException")
        void testApplyToWallet_SourceInsufficientBalanceForFee_ThrowsException() {
            UUID txId = UUID.randomUUID();
            Wallet tightWallet = new Wallet("Ví vừa khít");
            tightWallet.credit(Money.of(100_000));

            // Chuyển 95k + phí 10k = 105k > 100k
            TransactionFee fee = new TransactionFee(
                    UUID.randomUUID(), txId, Money.of(10_000), tightWallet, feeCategory);
            TransferTransaction tx = new TransferTransaction(
                    txId, tightWallet, destinationWallet, Money.of(95_000), validDate, "Thiếu phí", fee);

            assertThrows(InsufficientBalanceException.class, tx::applyToWallet);
            assertEquals(Money.of(100_000), tightWallet.getCurrentBalance());
            assertEquals(Money.of(2_000_000), destinationWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("revertFromWallet() khi ví đích đã tiêu hết tiền nhận ném InsufficientBalanceException")
        void testRevertFromWallet_DestinationInsufficientBalance_ThrowsException() {
            UUID txId = UUID.randomUUID();
            Money transferAmount = Money.of(1_000_000);

            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, transferAmount, validDate, "Chuyển tiền", null);
            tx.applyToWallet();
            // Số dư ví đích lúc này: 2M + 1M = 3M

            // Ví đích tiêu mất 2.5M -> Số dư ví đích chỉ còn 500k
            destinationWallet.debit(Money.of(2_500_000));
            assertEquals(Money.of(500_000), destinationWallet.getCurrentBalance());

            // Cố gắng hoàn nguyên khoản tiền 1M trong khi ví đích chỉ còn 500k -> Ném lỗi
            assertThrows(InsufficientBalanceException.class, tx::revertFromWallet);
            // Số dư hai ví không bị trừ lẹm
            assertEquals(Money.of(4_000_000), sourceWallet.getCurrentBalance());
            assertEquals(Money.of(500_000), destinationWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("applyToWallet() khi ví nguồn bị chuyển sang lưu trữ ném BusinessException ERR_WAL_001")
        void testApplyToWallet_ArchivedSourceWallet_ThrowsException() {
            UUID txId = UUID.randomUUID();
            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, Money.of(50_000), validDate, "Test", null);

            sourceWallet.archive();

            BusinessException ex = assertThrows(BusinessException.class, tx::applyToWallet);
            assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("applyToWallet() khi ví đích bị chuyển sang lưu trữ ném BusinessException ERR_WAL_001")
        void testApplyToWallet_ArchivedDestinationWallet_ThrowsException() {
            UUID txId = UUID.randomUUID();
            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, Money.of(50_000), validDate, "Test", null);

            destinationWallet.archive();

            BusinessException ex = assertThrows(BusinessException.class, tx::applyToWallet);
            assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("revertFromWallet() khi ví nguồn bị lưu trữ ném BusinessException ERR_WAL_001")
        void testRevertFromWallet_ArchivedSourceWallet_ThrowsException() {
            UUID txId = UUID.randomUUID();
            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, Money.of(50_000), validDate, "Test", null);
            tx.applyToWallet();

            sourceWallet.archive();

            BusinessException ex = assertThrows(BusinessException.class, tx::revertFromWallet);
            assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("revertFromWallet() khi ví đích bị lưu trữ ném BusinessException ERR_WAL_001")
        void testRevertFromWallet_ArchivedDestinationWallet_ThrowsException() {
            UUID txId = UUID.randomUUID();
            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, Money.of(50_000), validDate, "Test", null);
            tx.applyToWallet();

            destinationWallet.archive();

            BusinessException ex = assertThrows(BusinessException.class, tx::revertFromWallet);
            assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Robustness and Identity Tests")
    class RobustnessAndIdentityTests {

        @Test
        @DisplayName("Hai giao dịch chuyển tiền cùng transactionId là bằng nhau")
        void testEqualsAndHashCode() {
            UUID txId = UUID.randomUUID();
            TransferTransaction tx1 = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, Money.of(100_000), validDate, "Note 1", null);

            Wallet anotherWallet = new Wallet("Ví khác");
            anotherWallet.credit(Money.of(1_000_000));
            TransferTransaction tx2 = new TransferTransaction(
                    txId, sourceWallet, anotherWallet, Money.of(200_000), validDate, "Note 2", null);

            TransferTransaction tx3 = new TransferTransaction(
                    UUID.randomUUID(), sourceWallet, destinationWallet, Money.of(100_000), validDate, "Note 1", null);

            assertEquals(tx1, tx2);
            assertEquals(tx1.hashCode(), tx2.hashCode());
            assertNotEquals(tx1, tx3);
            assertNotEquals(tx1, null);
            assertNotEquals(tx1, "string");
        }

        @Test
        @DisplayName("toString() trả về chuỗi chứa thông tin ví nguồn, ví đích và số tiền")
        void testToString() {
            UUID txId = UUID.randomUUID();
            TransferTransaction tx = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, Money.of(100_000), validDate, "Ghi chú", null);

            String result = tx.toString();
            assertTrue(result.contains(txId.toString()));
            assertTrue(result.contains(sourceWallet.getId().toString()));
            assertTrue(result.contains(destinationWallet.getId().toString()));
            assertTrue(result.contains("100.000"));
        }
    }

    @Nested
    @DisplayName("Live Wallet Revert Operations (UC-TRF-03)")
    class LiveWalletRevertTests {

        @Test
        @DisplayName("revertFromWallet(liveSource, liveDest) không phí cập nhật đúng hai thực thể live tách biệt")
        void testRevertFromLiveWalletsWithoutFee() {
            UUID transferId = UUID.randomUUID();
            TransferTransaction transaction = new TransferTransaction(
                    transferId, sourceWallet, destinationWallet, Money.of(1_000_000), validDate, "Hoàn tác", null);
            transaction.applyToWallet();

            Wallet liveSource = sourceWallet.copy();
            Wallet liveDestination = destinationWallet.copy();

            transaction.revertFromWallet(liveSource, liveDestination);

            assertEquals(Money.of(5_000_000), liveSource.getCurrentBalance());
            assertEquals(Money.of(2_000_000), liveDestination.getCurrentBalance());
            assertEquals(Money.of(4_000_000), transaction.getSourceWallet().getCurrentBalance());
            assertEquals(Money.of(3_000_000), transaction.getDestinationWallet().getCurrentBalance());
        }

        @Test
        @DisplayName("revertFromWallet(liveSource, liveDest) có phí hoàn trả cả gốc lẫn phí về ví nguồn live")
        void testRevertFromLiveWalletsWithFeeRefundsFeeToLiveSource() {
            UUID transferId = UUID.randomUUID();
            Money transferAmount = Money.of(1_000_000);
            Money feeAmount = Money.of(15_000);
            TransactionFee transactionFee = new TransactionFee(
                    UUID.randomUUID(), transferId, feeAmount, sourceWallet, feeCategory);
            TransferTransaction transaction = new TransferTransaction(
                    transferId, sourceWallet, destinationWallet, transferAmount, validDate, "Có phí", transactionFee);
            transaction.applyToWallet();

            Wallet liveSource = sourceWallet.copy();
            Wallet liveDestination = destinationWallet.copy();

            transaction.revertFromWallet(liveSource, liveDestination);

            assertEquals(Money.of(5_000_000), liveSource.getCurrentBalance());
            assertEquals(Money.of(2_000_000), liveDestination.getCurrentBalance());
            assertEquals(Money.of(3_985_000), transaction.getSourceWallet().getCurrentBalance());
        }

        @Test
        @DisplayName("revertFromWallet(liveSource, liveDest) từ chối khi ví đích không đủ tiền và giữ nguyên số dư")
        void testRevertFromLiveWalletsRejectsInsufficientDestinationBalance() {
            UUID transferId = UUID.randomUUID();
            TransactionFee transactionFee = new TransactionFee(
                    UUID.randomUUID(), transferId, Money.of(15_000), sourceWallet, feeCategory);
            TransferTransaction transaction = new TransferTransaction(
                    transferId, sourceWallet, destinationWallet, Money.of(1_000_000), validDate, "Thiếu hoàn", transactionFee);
            transaction.applyToWallet();

            Wallet liveSource = sourceWallet.copy();
            Wallet liveDestination = destinationWallet.copy();
            liveDestination.debit(Money.of(2_500_000));

            InsufficientBalanceException exception = assertThrows(
                    InsufficientBalanceException.class,
                    () -> transaction.revertFromWallet(liveSource, liveDestination));

            assertEquals(ErrorCode.ERR_WAL_001, exception.getErrorCode());
            assertEquals(Money.of(3_985_000), liveSource.getCurrentBalance());
            assertEquals(Money.of(500_000), liveDestination.getCurrentBalance());
        }

        @Test
        @DisplayName("revertFromWallet(liveSource, liveDest) từ chối ví live đang lưu trữ hoặc đã xóa với ERR_WAL_001")
        void testRevertFromLiveWalletsRejectsInactiveLiveWallets() {
            TransferTransaction transaction = new TransferTransaction(
                    UUID.randomUUID(), sourceWallet, destinationWallet, Money.of(100_000), validDate, "Trạng thái", null);
            transaction.applyToWallet();

            Wallet archivedSource = sourceWallet.copy();
            archivedSource.archive();
            Wallet liveDestination = destinationWallet.copy();

            BusinessException archivedException = assertThrows(
                    BusinessException.class,
                    () -> transaction.revertFromWallet(archivedSource, liveDestination));
            assertEquals(ErrorCode.ERR_WAL_001, archivedException.getErrorCode());
            assertEquals(Money.of(4_900_000), archivedSource.getCurrentBalance());
            assertEquals(Money.of(2_100_000), liveDestination.getCurrentBalance());

            Wallet liveSource = sourceWallet.copy();
            Wallet deletedDestination = destinationWallet.copy();
            deletedDestination.delete();

            BusinessException deletedException = assertThrows(
                    BusinessException.class,
                    () -> transaction.revertFromWallet(liveSource, deletedDestination));
            assertEquals(ErrorCode.ERR_WAL_001, deletedException.getErrorCode());
            assertEquals(Money.of(4_900_000), liveSource.getCurrentBalance());
            assertEquals(Money.of(2_100_000), deletedDestination.getCurrentBalance());
        }

        @Test
        @DisplayName("revertFromWallet(liveSource, liveDest) từ chối tham số null hoặc ví không khớp định danh")
        void testRevertFromLiveWalletsRejectsNullAndMismatchedIds() {
            TransferTransaction transaction = new TransferTransaction(
                    UUID.randomUUID(), sourceWallet, destinationWallet, Money.of(100_000), validDate, "Kiểm ID", null);
            Wallet liveSource = sourceWallet.copy();
            Wallet liveDestination = destinationWallet.copy();
            Wallet unrelatedWallet = new Wallet("Ví Lạ");
            Money initialUnrelatedBalance = unrelatedWallet.getCurrentBalance();

            assertThrows(NullPointerException.class, () -> transaction.revertFromWallet(null, liveDestination));
            assertThrows(NullPointerException.class, () -> transaction.revertFromWallet(liveSource, null));
            assertThrows(IllegalArgumentException.class,
                    () -> transaction.revertFromWallet(liveDestination, liveSource));
            assertThrows(IllegalArgumentException.class,
                    () -> transaction.revertFromWallet(unrelatedWallet, liveDestination));
            assertThrows(IllegalArgumentException.class,
                    () -> transaction.revertFromWallet(liveSource, unrelatedWallet));
            assertEquals(Money.of(5_000_000), liveSource.getCurrentBalance());
            assertEquals(Money.of(2_000_000), liveDestination.getCurrentBalance());
            assertEquals(initialUnrelatedBalance, unrelatedWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("applyToWallet() và revertFromWallet(live, live) trừ/hoàn phí nhất quán trên sourceWallet và chặn lệch ví phí")
        void testFeeWalletConsistencyBetweenApplyAndLiveRevert() {
            UUID txId = UUID.randomUUID();
            TransactionFee copiedFee = new TransactionFee(
                    UUID.randomUUID(), txId, Money.of(5_000), sourceWallet.copy(), feeCategory);
            TransferTransaction transaction = new TransferTransaction(
                    txId, sourceWallet, destinationWallet, Money.of(100_000), validDate, "Đồng nhất phí", copiedFee);
            transaction.applyToWallet();
            assertEquals(Money.of(4_895_000), sourceWallet.getCurrentBalance());
            assertEquals(Money.of(2_100_000), destinationWallet.getCurrentBalance());
            Wallet preDriftSource = sourceWallet.copy();
            Wallet preDriftDestination = destinationWallet.copy();
            transaction.revertFromWallet(preDriftSource, preDriftDestination);
            assertEquals(Money.of(5_000_000), preDriftSource.getCurrentBalance());
            assertEquals(Money.of(2_000_000), preDriftDestination.getCurrentBalance());
            copiedFee.updateFee(Money.of(5_000), feeCategory, new Wallet("Ví Khác"));
            Wallet liveSource = sourceWallet.copy();
            Wallet liveDestination = destinationWallet.copy();
            assertEquals(ErrorCode.ERR_FEE_001, assertThrows(BusinessException.class, transaction::applyToWallet).getErrorCode());
            assertEquals(ErrorCode.ERR_FEE_001, assertThrows(BusinessException.class,
                    () -> transaction.revertFromWallet(liveSource, liveDestination)).getErrorCode());
            assertEquals(Money.of(4_895_000), sourceWallet.getCurrentBalance());
            assertEquals(Money.of(2_100_000), destinationWallet.getCurrentBalance());
            assertEquals(Money.of(4_895_000), liveSource.getCurrentBalance());
            assertEquals(Money.of(2_100_000), liveDestination.getCurrentBalance());
        }
    }
}

