package com.pfms.domain.model.transaction;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for Transaction Abstract Base Class")
class TransactionTest {

    private UUID transactionId;
    private Money defaultAmount;
    private TransactionDate defaultDate;
    private Wallet activeWallet;
    private Category expenseCategory;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        defaultAmount = Money.of("100000.00");
        defaultDate = TransactionDate.of(LocalDate.now());

        activeWallet = new Wallet("Ví thanh toán");
        activeWallet.credit(Money.of("500000.00"));

        expenseCategory = new Category(
                UUID.randomUUID(),
                "Phí chuyển tiền",
                UUID.randomUUID(),
                TransactionType.EXPENSE,
                "fee-icon",
                "#FF5722");
    }

    static class ConcreteTestTransaction extends Transaction {
        private boolean applied = false;
        private boolean reverted = false;

        public ConcreteTestTransaction(UUID transactionId, Money amount, TransactionDate transactionDate,
                String note, TransactionFee fee) {
            super(transactionId, amount, transactionDate, note, fee);
        }

        public ConcreteTestTransaction(UUID transactionId, Money amount, TransactionDate transactionDate,
                String note, TransactionFee fee, LocalDateTime createdAt) {
            super(transactionId, amount, transactionDate, note, fee, createdAt);
        }

        @Override
        public void applyToWallet() {
            this.applied = true;
            getFee().ifPresent(TransactionFee::applyFee);
        }

        @Override
        public void revertFromWallet() {
            this.reverted = true;
            getFee().ifPresent(TransactionFee::revertFee);
        }

        public boolean isApplied() {
            return applied;
        }

        public boolean isReverted() {
            return reverted;
        }

        public void updateBaseAmount(Money newAmount) {
            setAmount(newAmount);
        }

        public void updateBaseTransactionDate(TransactionDate newDate) {
            setTransactionDate(newDate);
        }

        public void updateBaseNote(String newNote) {
            setNote(newNote);
        }

        public void updateBaseFee(TransactionFee newFee) {
            setFee(newFee);
        }
    }

    @Nested
    @DisplayName("Group 1: Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo giao dịch hợp lệ không kèm chi phí phụ")
        void testValidConstructionWithoutFee() {
            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Tiền ăn trưa",
                    null);

            assertEquals(transactionId, txn.getTransactionId());
            assertEquals(defaultAmount, txn.getAmount());
            assertEquals(defaultDate, txn.getTransactionDate());
            assertEquals("Tiền ăn trưa", txn.getNote());
            assertTrue(txn.getFee().isEmpty());
            assertNotNull(txn.getCreatedAt());
            assertTrue(txn.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        }

        @Test
        @DisplayName("Khởi tạo giao dịch giữ nguyên thời điểm createdAt lịch sử qua constructor 6 tham số")
        void testValidConstructionPreservesHistoricalCreatedAt() {
            LocalDateTime historicalTime = LocalDateTime.of(2024, 4, 10, 15, 20, 30);

            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Giao dịch lịch sử",
                    null,
                    historicalTime);

            assertEquals(historicalTime, txn.getCreatedAt());
        }

        @Test
        @DisplayName("Khởi tạo giao dịch hợp lệ có kèm chi phí phụ hợp thành")
        void testValidConstructionWithFee() {
            TransactionFee fee = new TransactionFee(
                    UUID.randomUUID(),
                    transactionId,
                    Money.of("5000.00"),
                    activeWallet,
                    expenseCategory);

            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Chuyển khoản liên ngân hàng",
                    fee);

            assertTrue(txn.getFee().isPresent());
            assertSame(fee, txn.getFee().get());
        }

        @Test
        @DisplayName("Thực thi phương thức đa hình applyToWallet và revertFromWallet")
        void testPolymorphicExecution() {
            TransactionFee fee = new TransactionFee(
                    UUID.randomUUID(),
                    transactionId,
                    Money.of("5000.00"),
                    activeWallet,
                    expenseCategory);

            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Giao dịch test",
                    fee);

            assertFalse(txn.isApplied());
            txn.applyToWallet();
            assertTrue(txn.isApplied());
            assertEquals(Money.of("495000.00"), activeWallet.getCurrentBalance());

            assertFalse(txn.isReverted());
            txn.revertFromWallet();
            assertTrue(txn.isReverted());
            assertEquals(Money.of("500000.00"), activeWallet.getCurrentBalance());
        }
    }

    @Nested
    @DisplayName("Group 2: Boundary and Normalization Tests")
    class BoundaryAndNormalizationTests {

        @Test
        @DisplayName("Ghi chú được cắt tỉa khoảng trắng hai đầu")
        void testNoteTrimming() {
            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "   Nội dung có khoảng trắng thừa   ",
                    null);

            assertEquals("Nội dung có khoảng trắng thừa", txn.getNote());
        }

        @Test
        @DisplayName("Ghi chú null được chuẩn hóa thành chuỗi rỗng")
        void testNullNoteNormalizedToEmpty() {
            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    null,
                    null);

            assertEquals("", txn.getNote());
        }

        @Test
        @DisplayName("Chấp nhận ghi chú có độ dài tối đa 255 ký tự")
        void testMaxNoteLength() {
            String maxNote = "a".repeat(255);
            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    maxNote,
                    null);

            assertEquals(maxNote, txn.getNote());
        }

        @Test
        @DisplayName("Chấp nhận số tiền dương nhỏ nhất 0.01 VNĐ")
        void testMinimumPositiveAmount() {
            Money minAmount = Money.of("0.01");
            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    minAmount,
                    defaultDate,
                    "Phí tối thiểu",
                    null);

            assertEquals(minAmount, txn.getAmount());
        }
    }

    @Nested
    @DisplayName("Group 3: Null and Invalid Input Tests")
    class NullAndInvalidInputTests {

        @Test
        @DisplayName("Từ chối khởi tạo khi transactionId bị null")
        void testNullTransactionIdThrowsException() {
            assertThrows(NullPointerException.class,
                    () -> new ConcreteTestTransaction(null, defaultAmount, defaultDate, "Test", null));
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi transactionDate bị null")
        void testNullTransactionDateThrowsException() {
            assertThrows(NullPointerException.class,
                    () -> new ConcreteTestTransaction(transactionId, defaultAmount, null, "Test", null));
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi amount bị null")
        void testNullAmountThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> new ConcreteTestTransaction(transactionId, null, defaultDate, "Test", null));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi amount bằng 0.00 VNĐ")
        void testZeroAmountThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> new ConcreteTestTransaction(transactionId, Money.ZERO, defaultDate, "Test", null));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi amount mang giá trị âm")
        void testNegativeAmountThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> new ConcreteTestTransaction(transactionId, Money.of("-10000.00"), defaultDate, "Test", null));
            assertEquals(ErrorCode.ERR_FIN_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi note vượt quá 255 ký tự")
        void testNoteExceedingMaxLengthThrowsException() {
            String tooLongNote = "a".repeat(256);
            assertThrows(IllegalArgumentException.class,
                    () -> new ConcreteTestTransaction(transactionId, defaultAmount, defaultDate, tooLongNote, null));
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi fee có parentTransactionId không khớp với transactionId của giao dịch")
        void testMismatchedFeeParentIdThrowsException() {
            UUID differentTxnId = UUID.randomUUID();
            TransactionFee mismatchedFee = new TransactionFee(
                    UUID.randomUUID(),
                    differentTxnId,
                    Money.of("5000.00"),
                    activeWallet,
                    expenseCategory);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> new ConcreteTestTransaction(transactionId, defaultAmount, defaultDate, "Test",
                            mismatchedFee));
            assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi createdAt truyền vào constructor 6 tham số bị null")
        void testNullCreatedAtThrowsException() {
            assertThrows(NullPointerException.class,
                    () -> new ConcreteTestTransaction(transactionId, defaultAmount, defaultDate, "Test", null, null));
        }
    }

    @Nested
    @DisplayName("Group 4: Robustness and Identity Tests")
    class RobustnessAndIdentityTests {

        @Test
        @DisplayName("Hai giao dịch có cùng transactionId được coi là tương đương theo equals và hashCode")
        void testEqualsAndHashCode() {
            ConcreteTestTransaction txn1 = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Note 1",
                    null);

            ConcreteTestTransaction txn2 = new ConcreteTestTransaction(
                    transactionId,
                    Money.of("200000.00"),
                    TransactionDate.of(LocalDate.now().minusDays(1)),
                    "Note 2",
                    null);

            assertEquals(txn1, txn2);
            assertEquals(txn1.hashCode(), txn2.hashCode());
        }

        @Test
        @DisplayName("Hai giao dịch có transactionId khác nhau không bằng nhau")
        void testDifferentTransactionIdNotEquals() {
            ConcreteTestTransaction txn1 = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Note",
                    null);

            ConcreteTestTransaction txn2 = new ConcreteTestTransaction(
                    UUID.randomUUID(),
                    defaultAmount,
                    defaultDate,
                    "Note",
                    null);

            assertNotEquals(txn1, txn2);
        }

        @Test
        @DisplayName("equals tuân thủ hợp đồng khi so sánh với chính nó, với null và với đối tượng khác kiểu")
        void testEqualsContractEdgeCases() {
            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Note",
                    null);

            assertEquals(txn, txn);
            assertNotEquals(null, txn);
            assertNotEquals("non-transaction string", txn);
        }

        @Test
        @DisplayName("toString hiển thị đầy đủ thông tin giao dịch có phí và không kèm phí")
        void testToStringFormat() {
            ConcreteTestTransaction txnWithoutFee = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Ăn trưa",
                    null);

            String strWithoutFee = txnWithoutFee.toString();
            assertTrue(strWithoutFee.contains("transactionId=" + transactionId));
            assertTrue(strWithoutFee.contains("amount=" + defaultAmount));
            assertTrue(strWithoutFee.contains("feeId=none"));

            TransactionFee fee = new TransactionFee(
                    UUID.randomUUID(),
                    transactionId,
                    Money.of("5000.00"),
                    activeWallet,
                    expenseCategory);

            ConcreteTestTransaction txnWithFee = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Có kèm phí",
                    fee);

            String strWithFee = txnWithFee.toString();
            assertTrue(strWithFee.contains("feeId=" + fee.getFeeId()));
        }
    }

    @Nested
    @DisplayName("Group 5: Protected Helper Invariant Tests")
    class ProtectedHelperTests {

        @Test
        @DisplayName("setAmount cập nhật thành công số tiền hợp lệ và ném BusinessException khi số tiền âm/zero")
        void testProtectedSetAmount() {
            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Note",
                    null);

            Money newAmount = Money.of("250000.00");
            txn.updateBaseAmount(newAmount);
            assertEquals(newAmount, txn.getAmount());

            BusinessException exZero = assertThrows(BusinessException.class, () -> txn.updateBaseAmount(Money.ZERO));
            assertEquals(ErrorCode.ERR_FIN_001, exZero.getErrorCode());

            BusinessException exNull = assertThrows(BusinessException.class, () -> txn.updateBaseAmount(null));
            assertEquals(ErrorCode.ERR_FIN_001, exNull.getErrorCode());
        }

        @Test
        @DisplayName("setTransactionDate cập nhật ngày hợp lệ và từ chối khi date null")
        void testProtectedSetTransactionDate() {
            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Note",
                    null);

            TransactionDate newDate = TransactionDate.of(LocalDate.now().minusDays(2));
            txn.updateBaseTransactionDate(newDate);
            assertEquals(newDate, txn.getTransactionDate());

            assertThrows(NullPointerException.class, () -> txn.updateBaseTransactionDate(null));
        }

        @Test
        @DisplayName("setNote chuẩn hóa khoảng trắng và từ chối khi note vượt quá 255 ký tự")
        void testProtectedSetNote() {
            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Note",
                    null);

            txn.updateBaseNote("   Ghi chú mới đã cắt tỉa   ");
            assertEquals("Ghi chú mới đã cắt tỉa", txn.getNote());

            txn.updateBaseNote(null);
            assertEquals("", txn.getNote());

            String tooLongNote = "x".repeat(256);
            assertThrows(IllegalArgumentException.class, () -> txn.updateBaseNote(tooLongNote));
        }

        @Test
        @DisplayName("setFee cập nhật phí hợp lệ và từ chối phí có parentTransactionId không khớp")
        void testProtectedSetFee() {
            ConcreteTestTransaction txn = new ConcreteTestTransaction(
                    transactionId,
                    defaultAmount,
                    defaultDate,
                    "Note",
                    null);

            TransactionFee validFee = new TransactionFee(
                    UUID.randomUUID(),
                    transactionId,
                    Money.of("3000.00"),
                    activeWallet,
                    expenseCategory);

            txn.updateBaseFee(validFee);
            assertTrue(txn.getFee().isPresent());
            assertSame(validFee, txn.getFee().get());

            txn.updateBaseFee(null);
            assertTrue(txn.getFee().isEmpty());

            TransactionFee mismatchedFee = new TransactionFee(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    Money.of("3000.00"),
                    activeWallet,
                    expenseCategory);

            BusinessException ex = assertThrows(BusinessException.class, () -> txn.updateBaseFee(mismatchedFee));
            assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Group 5: Shared Descending Order Comparator")
    class DescendingOrderComparatorTests {

        private Transaction createSampleTransaction(TransactionDate date, LocalDateTime createdAt) {
            return new ConcreteTestTransaction(
                    UUID.randomUUID(),
                    defaultAmount,
                    date,
                    "Sample",
                    null,
                    createdAt);
        }

        @Test
        @DisplayName("DESCENDING_ORDER should place newer transactionDate before older dates")
        void testOrdersByTransactionDateDescending() {
            Transaction older = createSampleTransaction(
                    TransactionDate.of(LocalDate.now().minusDays(5)),
                    LocalDateTime.of(2025, 1, 10, 23, 59));
            Transaction middle = createSampleTransaction(
                    TransactionDate.of(LocalDate.now().minusDays(2)),
                    LocalDateTime.of(2025, 1, 10, 8, 0));
            Transaction newest = createSampleTransaction(
                    TransactionDate.now(),
                    LocalDateTime.of(2025, 1, 10, 6, 0));

            List<Transaction> sorted = List.of(older, newest, middle).stream()
                    .sorted(Transaction.DESCENDING_ORDER)
                    .toList();

            assertEquals(List.of(newest, middle, older), sorted);
        }

        @Test
        @DisplayName("DESCENDING_ORDER should break same-date ties by createdAt descending")
        void testBreaksSameDateTiesByCreatedAtDescending() {
            TransactionDate sameDay = TransactionDate.of(LocalDate.now().minusDays(1));
            Transaction morning = createSampleTransaction(sameDay, LocalDateTime.of(2025, 3, 1, 8, 15, 0));
            Transaction noon = createSampleTransaction(sameDay, LocalDateTime.of(2025, 3, 1, 12, 30, 0));
            Transaction evening = createSampleTransaction(sameDay, LocalDateTime.of(2025, 3, 1, 19, 45, 0));

            List<Transaction> sorted = List.of(morning, evening, noon).stream()
                    .sorted(Transaction.DESCENDING_ORDER)
                    .toList();

            assertEquals(List.of(evening, noon, morning), sorted);
        }

        @Test
        @DisplayName("DESCENDING_ORDER should return zero when date and createdAt match")
        void testReturnsZeroForEqualDateAndCreatedAt() {
            TransactionDate sameDay = TransactionDate.of(LocalDate.now().minusDays(1));
            LocalDateTime sameTimestamp = LocalDateTime.of(2025, 3, 1, 14, 0, 0);
            Transaction firstTransaction = createSampleTransaction(sameDay, sameTimestamp);
            Transaction secondTransaction = createSampleTransaction(sameDay, sameTimestamp);

            assertEquals(0, Transaction.DESCENDING_ORDER.compare(firstTransaction, secondTransaction));
        }
    }
}
