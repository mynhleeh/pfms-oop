package com.pfms.domain.model.transaction;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for TransactionFee Entity")
class TransactionFeeTest {

    private UUID feeId;
    private UUID parentTransactionId;
    private Money defaultAmount;
    private Wallet activeWallet;
    private Category expenseCategory;

    @BeforeEach
    void setUp() {
        feeId = UUID.randomUUID();
        parentTransactionId = UUID.randomUUID();
        defaultAmount = Money.of("5000.00");

        activeWallet = new Wallet("Ví thanh toán");
        activeWallet.credit(Money.of("50000.00"));

        expenseCategory = new Category(
                UUID.randomUUID(),
                "Phí chuyển khoản",
                UUID.randomUUID(),
                TransactionType.EXPENSE,
                "fee-icon",
                "#FF5722"
        );
    }

    @Nested
    @DisplayName("Group 1: Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Khởi tạo chi phí giao dịch hợp lệ với đầy đủ thuộc tính")
        void testValidConstruction() {
            TransactionFee fee = new TransactionFee(
                    feeId,
                    parentTransactionId,
                    defaultAmount,
                    activeWallet,
                    expenseCategory
            );

            assertEquals(feeId, fee.getFeeId());
            assertEquals(parentTransactionId, fee.getParentTransactionId());
            assertEquals(defaultAmount, fee.getAmount());
            assertSame(activeWallet, fee.getWallet());
            assertSame(expenseCategory, fee.getCategory());
        }

        @Test
        @DisplayName("applyFee trừ đúng số dư ví thanh toán")
        void testApplyFeeDeductsFromWallet() {
            TransactionFee fee = new TransactionFee(
                    feeId,
                    parentTransactionId,
                    defaultAmount,
                    activeWallet,
                    expenseCategory
            );

            fee.applyFee();

            assertEquals(Money.of("45000.00"), activeWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("revertFee hoàn nguyên cộng lại số dư ví thanh toán")
        void testRevertFeeRefundsToWallet() {
            TransactionFee fee = new TransactionFee(
                    feeId,
                    parentTransactionId,
                    defaultAmount,
                    activeWallet,
                    expenseCategory
            );

            fee.applyFee();
            assertEquals(Money.of("45000.00"), activeWallet.getCurrentBalance());

            fee.revertFee();
            assertEquals(Money.of("50000.00"), activeWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("updateFee cập nhật thành công số tiền, danh mục và ví mới")
        void testUpdateFeeWithValidArguments() {
            TransactionFee fee = new TransactionFee(
                    feeId,
                    parentTransactionId,
                    defaultAmount,
                    activeWallet,
                    expenseCategory
            );

            Wallet newWallet = new Wallet("Ví phụ");
            newWallet.credit(Money.of("20000.00"));

            Category newExpenseCategory = new Category(
                    UUID.randomUUID(),
                    "Phí dịch vụ ngân hàng",
                    UUID.randomUUID(),
                    TransactionType.EXPENSE,
                    "bank-icon",
                    "#00BCD4"
            );

            Money newAmount = Money.of("7000.00");
            fee.updateFee(newAmount, newExpenseCategory, newWallet);

            assertEquals(newAmount, fee.getAmount());
            assertSame(newExpenseCategory, fee.getCategory());
            assertSame(newWallet, fee.getWallet());
        }

        @Test
        @DisplayName("reconstitute cho phép tái tạo phí gắn ví lưu trữ/đã xóa và danh mục chi tiêu đã xóa mềm")
        void testReconstituteAllowsArchivedWalletAndDeletedExpenseCategory() {
            activeWallet.archive();
            expenseCategory.markDeleted();

            TransactionFee fee = TransactionFee.reconstitute(
                    feeId,
                    parentTransactionId,
                    defaultAmount,
                    activeWallet,
                    expenseCategory
            );

            assertEquals(feeId, fee.getFeeId());
            assertEquals(parentTransactionId, fee.getParentTransactionId());
            assertEquals(defaultAmount, fee.getAmount());
            assertSame(activeWallet, fee.getWallet());
            assertSame(expenseCategory, fee.getCategory());
        }
    }

    @Nested
    @DisplayName("Group 2: Boundary and Precision Tests")
    class BoundaryAndPrecisionTests {

        @Test
        @DisplayName("Chấp nhận mức phí dương nhỏ nhất 0.01 VNĐ và giữ nguyên độ chính xác 2 chữ số thập phân")
        void testMinimumPositiveAmount() {
            Money minAmount = Money.of("0.01");
            TransactionFee fee = new TransactionFee(
                    feeId,
                    parentTransactionId,
                    minAmount,
                    activeWallet,
                    expenseCategory
            );

            assertEquals(minAmount, fee.getAmount());
            fee.applyFee();
            assertEquals(Money.of("49999.99"), activeWallet.getCurrentBalance());
        }
    }

    @Nested
    @DisplayName("Group 3: Null and Invalid Input Tests")
    class NullAndInvalidInputTests {

        @Test
        @DisplayName("Từ chối khởi tạo khi feeId bị null")
        void testNullFeeIdThrowsException() {
            assertThrows(NullPointerException.class, () ->
                    new TransactionFee(null, parentTransactionId, defaultAmount, activeWallet, expenseCategory)
            );
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi parentTransactionId bị null")
        void testNullParentTransactionIdThrowsException() {
            assertThrows(NullPointerException.class, () ->
                    new TransactionFee(feeId, null, defaultAmount, activeWallet, expenseCategory)
            );
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi amount bị null")
        void testNullAmountThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransactionFee(feeId, parentTransactionId, null, activeWallet, expenseCategory)
            );
            assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi amount bằng 0.00 VNĐ")
        void testZeroAmountThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransactionFee(feeId, parentTransactionId, Money.ZERO, activeWallet, expenseCategory)
            );
            assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi amount mang giá trị âm")
        void testNegativeAmountThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransactionFee(feeId, parentTransactionId, Money.of("-5000.00"), activeWallet, expenseCategory)
            );
            assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi category bị null")
        void testNullCategoryThrowsException() {
            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransactionFee(feeId, parentTransactionId, defaultAmount, activeWallet, null)
            );
            assertEquals(ErrorCode.ERR_FEE_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi category thuộc nhánh INCOME")
        void testIncomeCategoryThrowsException() {
            Category incomeCategory = new Category(
                    UUID.randomUUID(),
                    "Thu nhập khác",
                    UUID.randomUUID(),
                    TransactionType.INCOME,
                    "income-icon",
                    "#4CAF50"
            );

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransactionFee(feeId, parentTransactionId, defaultAmount, activeWallet, incomeCategory)
            );
            assertEquals(ErrorCode.ERR_FEE_003, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi category ở trạng thái đã xóa mềm")
        void testInactiveCategoryThrowsException() {
            expenseCategory.markDeleted();

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransactionFee(feeId, parentTransactionId, defaultAmount, activeWallet, expenseCategory)
            );
            assertEquals(ErrorCode.ERR_FEE_003, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi wallet bị null")
        void testNullWalletThrowsException() {
            assertThrows(NullPointerException.class, () ->
                    new TransactionFee(feeId, parentTransactionId, defaultAmount, null, expenseCategory)
            );
        }

        @Test
        @DisplayName("Từ chối khởi tạo khi wallet không ở trạng thái ACTIVE")
        void testInactiveWalletThrowsException() {
            activeWallet.archive();

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    new TransactionFee(feeId, parentTransactionId, defaultAmount, activeWallet, expenseCategory)
            );
            assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("reconstitute vẫn từ chối số tiền không dương, tham chiếu null và danh mục loại INCOME")
        void testReconstituteRejectsInvalidAmountNullReferencesAndIncomeCategory() {
            Category incomeCategory = new Category(
                    UUID.randomUUID(),
                    "Thu nhập khác",
                    UUID.randomUUID(),
                    TransactionType.INCOME,
                    "income-icon",
                    "#4CAF50"
            );

            assertThrows(NullPointerException.class, () ->
                    TransactionFee.reconstitute(feeId, parentTransactionId, defaultAmount, null, expenseCategory));

            BusinessException missingCategory = assertThrows(BusinessException.class, () ->
                    TransactionFee.reconstitute(feeId, parentTransactionId, defaultAmount, activeWallet, null));
            assertEquals(ErrorCode.ERR_FEE_002, missingCategory.getErrorCode());

            BusinessException wrongCategoryType = assertThrows(BusinessException.class, () ->
                    TransactionFee.reconstitute(feeId, parentTransactionId, defaultAmount, activeWallet, incomeCategory));
            assertEquals(ErrorCode.ERR_FEE_003, wrongCategoryType.getErrorCode());

            BusinessException invalidAmount = assertThrows(BusinessException.class, () ->
                    TransactionFee.reconstitute(feeId, parentTransactionId, Money.ZERO, activeWallet, expenseCategory));
            assertEquals(ErrorCode.ERR_FEE_001, invalidAmount.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Group 4: State Transitions and Financial Invariants")
    class StateTransitionsAndInvariantsTests {

        @Test
        @DisplayName("applyFee thất bại và ném InsufficientBalanceException khi số dư ví không đủ chi trả phí")
        void testApplyFeeWithInsufficientBalanceThrowsException() {
            Wallet poorWallet = new Wallet("Ví nghèo");
            poorWallet.credit(Money.of("2000.00"));

            TransactionFee fee = new TransactionFee(
                    feeId,
                    parentTransactionId,
                    Money.of("5000.00"),
                    poorWallet,
                    expenseCategory
            );

            InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class, fee::applyFee);
            assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
            assertEquals(Money.of("2000.00"), poorWallet.getCurrentBalance());
        }

        @Test
        @DisplayName("updateFee từ chối khi cập nhật số tiền âm hoặc bằng 0")
        void testUpdateFeeWithInvalidAmountThrowsException() {
            TransactionFee fee = new TransactionFee(
                    feeId,
                    parentTransactionId,
                    defaultAmount,
                    activeWallet,
                    expenseCategory
            );

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    fee.updateFee(Money.ZERO, expenseCategory, activeWallet)
            );
            assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("updateFee từ chối khi cập nhật danh mục thuộc loại INCOME hoặc đã xóa")
        void testUpdateFeeWithInvalidCategoryThrowsException() {
            TransactionFee fee = new TransactionFee(
                    feeId,
                    parentTransactionId,
                    defaultAmount,
                    activeWallet,
                    expenseCategory
            );

            Category incomeCategory = new Category(
                    UUID.randomUUID(),
                    "Thưởng",
                    UUID.randomUUID(),
                    TransactionType.INCOME,
                    "bonus-icon",
                    "#4CAF50"
            );

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    fee.updateFee(defaultAmount, incomeCategory, activeWallet)
            );
            assertEquals(ErrorCode.ERR_FEE_003, ex.getErrorCode());
        }

        @Test
        @DisplayName("updateFee từ chối khi cập nhật sang ví không ACTIVE")
        void testUpdateFeeWithInactiveWalletThrowsException() {
            TransactionFee fee = new TransactionFee(
                    feeId,
                    parentTransactionId,
                    defaultAmount,
                    activeWallet,
                    expenseCategory
            );

            Wallet archivedWallet = new Wallet("Ví cũ");
            archivedWallet.credit(Money.of("10000.00"));
            archivedWallet.archive();

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    fee.updateFee(defaultAmount, expenseCategory, archivedWallet)
            );
            assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Group 5: Robustness and Identity Tests")
    class RobustnessAndIdentityTests {

        @Test
        @DisplayName("Hai đối tượng TransactionFee có cùng feeId được coi là tương đương theo equals và hashCode")
        void testEqualsAndHashCode() {
            TransactionFee fee1 = new TransactionFee(
                    feeId,
                    parentTransactionId,
                    defaultAmount,
                    activeWallet,
                    expenseCategory
            );

            TransactionFee fee2 = new TransactionFee(
                    feeId,
                    UUID.randomUUID(),
                    Money.of("10000.00"),
                    activeWallet,
                    expenseCategory
            );

            assertEquals(fee1, fee2);
            assertEquals(fee1.hashCode(), fee2.hashCode());
        }

        @Test
        @DisplayName("Hai đối tượng TransactionFee có feeId khác nhau không bằng nhau")
        void testDifferentFeeIdNotEquals() {
            TransactionFee fee1 = new TransactionFee(
                    feeId,
                    parentTransactionId,
                    defaultAmount,
                    activeWallet,
                    expenseCategory
            );

            TransactionFee fee2 = new TransactionFee(
                    UUID.randomUUID(),
                    parentTransactionId,
                    defaultAmount,
                    activeWallet,
                    expenseCategory
            );

            assertNotEquals(fee1, fee2);
        }
    }
}

