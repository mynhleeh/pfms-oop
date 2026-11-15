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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for TransactionFactory (GoF Creational Pattern)")
class TransactionFactoryTest {

    private Wallet sourceWallet;
    private Wallet destinationWallet;
    private Category incomeCategory;
    private Category expenseCategory;
    private Category feeCategory;
    private Money txnAmount;
    private Money feeAmount;
    private TransactionDate txnDate;

    @BeforeEach
    void setUp() {
        sourceWallet = new Wallet("Ví thanh toán");
        sourceWallet.credit(Money.of("1000000.00"));

        destinationWallet = new Wallet("Ví tiết kiệm");
        destinationWallet.credit(Money.of("500000.00"));

        incomeCategory = new Category(
                UUID.randomUUID(),
                "Lương tháng",
                UUID.randomUUID(),
                TransactionType.INCOME,
                "salary-icon",
                "#4CAF50"
        );

        expenseCategory = new Category(
                UUID.randomUUID(),
                "Ăn uống",
                UUID.randomUUID(),
                TransactionType.EXPENSE,
                "food-icon",
                "#F44336"
        );

        feeCategory = new Category(
                UUID.randomUUID(),
                "Phí chuyển khoản",
                UUID.randomUUID(),
                TransactionType.EXPENSE,
                "fee-icon",
                "#FF9800"
        );

        txnAmount = Money.of("200000.00");
        feeAmount = Money.of("5000.00");
        txnDate = TransactionDate.of(LocalDate.now());
    }

    @Nested
    @DisplayName("Group 1: Architecture & Structural Constraints")
    class StructuralTests {

        @Test
        @DisplayName("TransactionFactory phải có hàm tạo private và không thể khởi tạo từ bên ngoài")
        void testPrivateConstructor() throws Exception {
            Constructor<TransactionFactory> constructor = TransactionFactory.class.getDeclaredConstructor();
            assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor phải mang phạm vi truy cập private");

            constructor.setAccessible(true);
            InvocationTargetException exception = assertThrows(
                    InvocationTargetException.class,
                    constructor::newInstance,
                    "Không được phép khởi tạo thể hiện của lớp Factory tiện ích"
            );
            assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
        }
    }

    @Nested
    @DisplayName("Group 2: Create IncomeExpenseTransaction")
    class IncomeExpenseCreationTests {

        @Test
        @DisplayName("Tạo IncomeExpenseTransaction không có phí giao dịch (feeAmount = null)")
        void testCreateIncomeExpenseWithoutFeeNull() {
            IncomeExpenseTransaction txn = TransactionFactory.createIncomeExpense(
                    TransactionType.EXPENSE,
                    sourceWallet,
                    expenseCategory,
                    txnAmount,
                    txnDate,
                    "Cà phê sáng",
                    null,
                    null
            );

            assertNotNull(txn);
            assertNotNull(txn.getTransactionId());
            assertEquals(TransactionType.EXPENSE, txn.getTransactionType());
            assertEquals(sourceWallet, txn.getWallet());
            assertEquals(expenseCategory, txn.getCategory());
            assertEquals(txnAmount, txn.getAmount());
            assertEquals(txnDate, txn.getTransactionDate());
            assertEquals("Cà phê sáng", txn.getNote());
            assertTrue(txn.getFee().isEmpty());
        }

        @Test
        @DisplayName("Tạo IncomeExpenseTransaction với phí bằng 0 (feeAmount = Money.ZERO)")
        void testCreateIncomeExpenseWithZeroFee() {
            IncomeExpenseTransaction txn = TransactionFactory.createIncomeExpense(
                    TransactionType.INCOME,
                    sourceWallet,
                    incomeCategory,
                    txnAmount,
                    txnDate,
                    "Tiền thưởng",
                    Money.ZERO,
                    feeCategory
            );

            assertNotNull(txn);
            assertEquals(TransactionType.INCOME, txn.getTransactionType());
            assertTrue(txn.getFee().isEmpty());
        }

        @Test
        @DisplayName("Tạo IncomeExpenseTransaction với phí bằng 0 và không truyền danh mục phí (feeCategory = null)")
        void testCreateIncomeExpenseWithZeroFeeAndNullCategory() {
            IncomeExpenseTransaction txn = TransactionFactory.createIncomeExpense(
                    TransactionType.EXPENSE,
                    sourceWallet,
                    expenseCategory,
                    txnAmount,
                    txnDate,
                    "Chi tiêu không phụ phí",
                    Money.ZERO,
                    null
            );

            assertNotNull(txn);
            assertTrue(txn.getFee().isEmpty());
        }

        @Test
        @DisplayName("Tạo IncomeExpenseTransaction kèm TransactionFee hợp thành sinh tử khi feeAmount > 0")
        void testCreateIncomeExpenseWithFee() {
            IncomeExpenseTransaction txn = TransactionFactory.createIncomeExpense(
                    TransactionType.EXPENSE,
                    sourceWallet,
                    expenseCategory,
                    txnAmount,
                    txnDate,
                    "Mua sách chuyên ngành",
                    feeAmount,
                    feeCategory
            );

            assertNotNull(txn);
            assertTrue(txn.getFee().isPresent());

            TransactionFee fee = txn.getFee().get();
            assertNotNull(fee.getFeeId());
            assertEquals(txn.getTransactionId(), fee.getParentTransactionId(), "Mã giao dịch chủ phải khớp với parentTransactionId của phí");
            assertEquals(feeAmount, fee.getAmount());
            assertEquals(sourceWallet, fee.getWallet(), "Ví trích phí phải là ví của giao dịch thu chi");
            assertEquals(feeCategory, fee.getCategory());
        }

        @Test
        @DisplayName("Tạo IncomeExpenseTransaction với UUID chỉ định trước (tái tạo từ kho lưu trữ)")
        void testCreateIncomeExpenseWithExplicitId() {
            UUID explicitId = UUID.randomUUID();
            IncomeExpenseTransaction txn = TransactionFactory.createIncomeExpense(
                    explicitId,
                    TransactionType.INCOME,
                    sourceWallet,
                    incomeCategory,
                    txnAmount,
                    txnDate,
                    "Lương",
                    feeAmount,
                    feeCategory
            );

            assertEquals(explicitId, txn.getTransactionId());
            assertTrue(txn.getFee().isPresent());
            assertEquals(explicitId, txn.getFee().get().getParentTransactionId());
        }

        @Test
        @DisplayName("Tạo IncomeExpenseTransaction với thể hiện TransactionFee cung cấp trực tiếp")
        void testCreateIncomeExpenseWithDirectFeeInstance() {
            UUID explicitId = UUID.randomUUID();
            TransactionFee directFee = new TransactionFee(
                    UUID.randomUUID(),
                    explicitId,
                    feeAmount,
                    sourceWallet,
                    feeCategory
            );

            IncomeExpenseTransaction txn = TransactionFactory.createIncomeExpense(
                    explicitId,
                    TransactionType.EXPENSE,
                    sourceWallet,
                    expenseCategory,
                    txnAmount,
                    txnDate,
                    "Giao dịch nạp lại",
                    directFee
            );

            assertEquals(explicitId, txn.getTransactionId());
            assertTrue(txn.getFee().isPresent());
            assertSame(directFee, txn.getFee().get());
        }
    }

    @Nested
    @DisplayName("Group 3: Create TransferTransaction")
    class TransferCreationTests {

        @Test
        @DisplayName("Tạo TransferTransaction không có phí giao dịch (feeAmount = null)")
        void testCreateTransferWithoutFeeNull() {
            TransferTransaction txn = TransactionFactory.createTransfer(
                    sourceWallet,
                    destinationWallet,
                    txnAmount,
                    txnDate,
                    "Chuyển tiền tiết kiệm",
                    null,
                    null
            );

            assertNotNull(txn);
            assertNotNull(txn.getTransactionId());
            assertEquals(sourceWallet, txn.getSourceWallet());
            assertEquals(destinationWallet, txn.getDestinationWallet());
            assertEquals(txnAmount, txn.getAmount());
            assertEquals(txnDate, txn.getTransactionDate());
            assertEquals("Chuyển tiền tiết kiệm", txn.getNote());
            assertTrue(txn.getFee().isEmpty());
        }

        @Test
        @DisplayName("Tạo TransferTransaction với phí bằng 0 (feeAmount = Money.ZERO)")
        void testCreateTransferWithZeroFee() {
            TransferTransaction txn = TransactionFactory.createTransfer(
                    sourceWallet,
                    destinationWallet,
                    txnAmount,
                    txnDate,
                    "Chuyển miễn phí",
                    Money.ZERO,
                    feeCategory
            );

            assertNotNull(txn);
            assertTrue(txn.getFee().isEmpty());
        }

        @Test
        @DisplayName("Tạo TransferTransaction với phí bằng 0 và không truyền danh mục phí (feeCategory = null)")
        void testCreateTransferWithZeroFeeAndNullCategory() {
            TransferTransaction txn = TransactionFactory.createTransfer(
                    sourceWallet,
                    destinationWallet,
                    txnAmount,
                    txnDate,
                    "Chuyển tiền không phụ phí",
                    Money.ZERO,
                    null
            );

            assertNotNull(txn);
            assertTrue(txn.getFee().isEmpty());
        }

        @Test
        @DisplayName("Tạo TransferTransaction kèm TransactionFee hợp thành trích từ sourceWallet khi feeAmount > 0")
        void testCreateTransferWithFee() {
            TransferTransaction txn = TransactionFactory.createTransfer(
                    sourceWallet,
                    destinationWallet,
                    txnAmount,
                    txnDate,
                    "Chuyển tiền liên ngân hàng",
                    feeAmount,
                    feeCategory
            );

            assertNotNull(txn);
            assertTrue(txn.getFee().isPresent());

            TransactionFee fee = txn.getFee().get();
            assertNotNull(fee.getFeeId());
            assertEquals(txn.getTransactionId(), fee.getParentTransactionId());
            assertEquals(feeAmount, fee.getAmount());
            assertEquals(sourceWallet, fee.getWallet(), "Ví trích phí của chuyển tiền bắt buộc là ví nguồn");
            assertEquals(feeCategory, fee.getCategory());
        }

        @Test
        @DisplayName("Tạo TransferTransaction với UUID chỉ định trước")
        void testCreateTransferWithExplicitId() {
            UUID explicitId = UUID.randomUUID();
            TransferTransaction txn = TransactionFactory.createTransfer(
                    explicitId,
                    sourceWallet,
                    destinationWallet,
                    txnAmount,
                    txnDate,
                    "Chuyển tiền nội bộ",
                    feeAmount,
                    feeCategory
            );

            assertEquals(explicitId, txn.getTransactionId());
            assertTrue(txn.getFee().isPresent());
            assertEquals(explicitId, txn.getFee().get().getParentTransactionId());
        }

        @Test
        @DisplayName("Tạo TransferTransaction với thể hiện TransactionFee cung cấp trực tiếp")
        void testCreateTransferWithDirectFeeInstance() {
            UUID explicitId = UUID.randomUUID();
            TransactionFee directFee = new TransactionFee(
                    UUID.randomUUID(),
                    explicitId,
                    feeAmount,
                    sourceWallet,
                    feeCategory
            );

            TransferTransaction txn = TransactionFactory.createTransfer(
                    explicitId,
                    sourceWallet,
                    destinationWallet,
                    txnAmount,
                    txnDate,
                    "Chuyển tiền nạp lại",
                    directFee
            );

            assertEquals(explicitId, txn.getTransactionId());
            assertTrue(txn.getFee().isPresent());
            assertSame(directFee, txn.getFee().get());
        }
    }

    @Nested
    @DisplayName("Group 4: Create Fee Explicitly")
    class FeeCreationTests {

        @Test
        @DisplayName("Tạo TransactionFee độc lập với mã feeId chỉ định trước")
        void testCreateFeeWithExplicitId() {
            UUID feeId = UUID.randomUUID();
            UUID parentId = UUID.randomUUID();

            TransactionFee fee = TransactionFactory.createFee(
                    feeId,
                    parentId,
                    feeAmount,
                    sourceWallet,
                    feeCategory
            );

            assertNotNull(fee);
            assertEquals(feeId, fee.getFeeId());
            assertEquals(parentId, fee.getParentTransactionId());
            assertEquals(feeAmount, fee.getAmount());
            assertEquals(sourceWallet, fee.getWallet());
            assertEquals(feeCategory, fee.getCategory());
        }

        @Test
        @DisplayName("Tạo TransactionFee tự động sinh UUID feeId")
        void testCreateFeeWithAutoId() {
            UUID parentId = UUID.randomUUID();

            TransactionFee fee = TransactionFactory.createFee(
                    parentId,
                    feeAmount,
                    sourceWallet,
                    feeCategory
            );

            assertNotNull(fee);
            assertNotNull(fee.getFeeId());
            assertEquals(parentId, fee.getParentTransactionId());
            assertEquals(feeAmount, fee.getAmount());
        }

        @Test
        @DisplayName("Ném BusinessException khi gọi createFee trực tiếp với feeAmount không hợp lệ")
        void testCreateFeeWithInvalidAmountThrowsException() {
            UUID parentId = UUID.randomUUID();
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> TransactionFactory.createFee(parentId, null, sourceWallet, feeCategory)
            );
            assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném NullPointerException khi gọi createFee trực tiếp với parentTransactionId null")
        void testCreateFeeWithNullParentIdThrowsException() {
            assertThrows(
                    NullPointerException.class,
                    () -> TransactionFactory.createFee(null, feeAmount, sourceWallet, feeCategory)
            );
        }
    }

    @Nested
    @DisplayName("Group 5: Boundary & Exception Validations")
    class ValidationTests {

        @Test
        @DisplayName("Ném BusinessException khi feeAmount > 0 nhưng feeCategory là null")
        void testFeeWithoutCategoryThrowsException() {
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> TransactionFactory.createIncomeExpense(
                            TransactionType.EXPENSE,
                            sourceWallet,
                            expenseCategory,
                            txnAmount,
                            txnDate,
                            "Thiếu danh mục phí",
                            feeAmount,
                            null
                    )
            );
            assertEquals(ErrorCode.ERR_FEE_002, exception.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException khi feeCategory không thuộc loại EXPENSE")
        void testFeeWithIncomeCategoryThrowsException() {
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> TransactionFactory.createTransfer(
                            sourceWallet,
                            destinationWallet,
                            txnAmount,
                            txnDate,
                            "Danh mục phí sai loại",
                            feeAmount,
                            incomeCategory
                    )
            );
            assertEquals(ErrorCode.ERR_FEE_003, exception.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException khi feeAmount là số âm")
        void testFeeWithNegativeAmountThrowsException() {
            Money negativeFee = Money.of("-5000.00");
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> TransactionFactory.createIncomeExpense(
                            TransactionType.EXPENSE,
                            sourceWallet,
                            expenseCategory,
                            txnAmount,
                            txnDate,
                            "Phí âm",
                            negativeFee,
                            feeCategory
                    )
            );
            assertEquals(ErrorCode.ERR_FEE_001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException khi ví nguồn trùng ví đích trong chuyển tiền")
        void testTransferSameWalletsThrowsException() {
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> TransactionFactory.createTransfer(
                            sourceWallet,
                            sourceWallet,
                            txnAmount,
                            txnDate,
                            "Trùng ví",
                            null,
                            null
                    )
            );
            assertEquals(ErrorCode.ERR_TRF_001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException khi danh mục không tương thích với loại thu chi")
        void testIncomeWithExpenseCategoryThrowsException() {
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> TransactionFactory.createIncomeExpense(
                            TransactionType.INCOME,
                            sourceWallet,
                            expenseCategory,
                            txnAmount,
                            txnDate,
                            "Thu vào danh mục chi",
                            null,
                            null
                    )
            );
            assertEquals(ErrorCode.ERR_CAT_001, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Group 6: Reconstitute Persisted Transactions and Fees")
    class ReconstitutionTests {

        @Test
        @DisplayName("Tái tạo TransactionFee với ví đã lưu trữ và danh mục phí đã xóa")
        void testReconstituteFeeWithArchivedWalletAndDeletedCategory() {
            sourceWallet.archive();
            feeCategory.markDeleted();
            UUID feeId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            UUID parentId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            Money persistedFeeAmount = Money.of("15000.00");

            TransactionFee fee = TransactionFactory.reconstituteFee(
                    feeId,
                    parentId,
                    persistedFeeAmount,
                    sourceWallet,
                    feeCategory
            );

            assertEquals(feeId, fee.getFeeId());
            assertEquals(parentId, fee.getParentTransactionId());
            assertEquals(Money.of("15000.00"), fee.getAmount());
            assertSame(sourceWallet, fee.getWallet());
            assertSame(feeCategory, fee.getCategory());
        }

        @Test
        @DisplayName("Tái tạo TransactionFee vẫn từ chối danh mục không phải EXPENSE")
        void testReconstituteFeeRejectsIncomeCategory() {
            UUID feeId = UUID.randomUUID();
            UUID parentId = UUID.randomUUID();

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> TransactionFactory.reconstituteFee(
                            feeId,
                            parentId,
                            feeAmount,
                            sourceWallet,
                            incomeCategory
                    )
            );
            assertEquals(ErrorCode.ERR_FEE_003, ex.getErrorCode());
        }

        @Test
        @DisplayName("Tái tạo IncomeExpenseTransaction giữ nguyên createdAt và cho phép ví lưu trữ, danh mục đã xóa")
        void testReconstituteIncomeExpensePreservesCreatedAtAndAllowsInactiveReferences() {
            sourceWallet.archive();
            expenseCategory.markDeleted();
            feeCategory.markDeleted();
            UUID txnId = UUID.fromString("33333333-3333-3333-3333-333333333333");
            UUID feeId = UUID.fromString("44444444-4444-4444-4444-444444444444");
            LocalDateTime persistedCreatedAt = LocalDateTime.of(2024, 11, 20, 14, 45, 30);
            TransactionDate persistedDate = TransactionDate.of(LocalDate.of(2024, 11, 20));
            TransactionFee persistedFee = TransactionFactory.reconstituteFee(
                    feeId, txnId, feeAmount, sourceWallet, feeCategory);

            IncomeExpenseTransaction txn = TransactionFactory.reconstituteIncomeExpense(
                    txnId,
                    TransactionType.EXPENSE,
                    sourceWallet,
                    expenseCategory,
                    Money.of("250000.00"),
                    persistedDate,
                    "Chi phí lịch sử",
                    persistedFee,
                    persistedCreatedAt
            );

            assertEquals(txnId, txn.getTransactionId());
            assertEquals(TransactionType.EXPENSE, txn.getTransactionType());
            assertSame(sourceWallet, txn.getWallet());
            assertSame(expenseCategory, txn.getCategory());
            assertEquals(Money.of("250000.00"), txn.getAmount());
            assertEquals(persistedDate, txn.getTransactionDate());
            assertEquals("Chi phí lịch sử", txn.getNote());
            assertEquals(persistedCreatedAt, txn.getCreatedAt());
            assertSame(persistedFee, txn.getFee().orElseThrow());
        }

        @Test
        @DisplayName("Tái tạo IncomeExpenseTransaction không có phí giữ Optional.empty và từ chối sai loại danh mục")
        void testReconstituteIncomeExpenseWithoutFeeAndRejectsCategoryMismatch() {
            UUID txnId = UUID.randomUUID();
            LocalDateTime persistedCreatedAt = LocalDateTime.of(2024, 9, 1, 8, 0, 0);

            IncomeExpenseTransaction txn = TransactionFactory.reconstituteIncomeExpense(
                    txnId,
                    TransactionType.INCOME,
                    sourceWallet,
                    incomeCategory,
                    Money.of("500000.00"),
                    txnDate,
                    "Thu nhập không phí",
                    null,
                    persistedCreatedAt
            );

            assertTrue(txn.getFee().isEmpty());
            assertEquals(persistedCreatedAt, txn.getCreatedAt());

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> TransactionFactory.reconstituteIncomeExpense(
                            txnId,
                            TransactionType.INCOME,
                            sourceWallet,
                            expenseCategory,
                            txnAmount,
                            txnDate,
                            "Sai loại",
                            null,
                            persistedCreatedAt
                    )
            );
            assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Tái tạo TransferTransaction giữ nguyên createdAt và cho phép hai ví đã lưu trữ kèm phụ phí")
        void testReconstituteTransferPreservesCreatedAtAndAllowsArchivedWallets() {
            sourceWallet.archive();
            destinationWallet.archive();
            feeCategory.markDeleted();
            UUID txnId = UUID.fromString("55555555-5555-5555-5555-555555555555");
            UUID feeId = UUID.fromString("66666666-6666-6666-6666-666666666666");
            LocalDateTime persistedCreatedAt = LocalDateTime.of(2024, 10, 5, 9, 15, 0);
            TransactionDate persistedDate = TransactionDate.of(LocalDate.of(2024, 10, 5));
            TransactionFee persistedFee = TransactionFactory.reconstituteFee(
                    feeId, txnId, feeAmount, sourceWallet, feeCategory);

            TransferTransaction txn = TransactionFactory.reconstituteTransfer(
                    txnId,
                    sourceWallet,
                    destinationWallet,
                    Money.of("300000.00"),
                    persistedDate,
                    "Chuyển khoản lịch sử",
                    persistedFee,
                    persistedCreatedAt
            );

            assertEquals(txnId, txn.getTransactionId());
            assertSame(sourceWallet, txn.getSourceWallet());
            assertSame(destinationWallet, txn.getDestinationWallet());
            assertEquals(Money.of("300000.00"), txn.getAmount());
            assertEquals(persistedDate, txn.getTransactionDate());
            assertEquals("Chuyển khoản lịch sử", txn.getNote());
            assertEquals(persistedCreatedAt, txn.getCreatedAt());
            assertSame(persistedFee, txn.getFee().orElseThrow());
        }

        @Test
        @DisplayName("Tái tạo TransferTransaction vẫn từ chối trùng ví nguồn-đích và createdAt null")
        void testReconstituteTransferRejectsSameWalletsAndNullCreatedAt() {
            UUID txnId = UUID.randomUUID();
            LocalDateTime persistedCreatedAt = LocalDateTime.of(2024, 10, 5, 9, 15, 0);

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> TransactionFactory.reconstituteTransfer(
                            txnId,
                            sourceWallet,
                            sourceWallet,
                            txnAmount,
                            txnDate,
                            "Trùng ví",
                            null,
                            persistedCreatedAt
                    )
            );
            assertEquals(ErrorCode.ERR_TRF_001, ex.getErrorCode());

            assertThrows(
                    NullPointerException.class,
                    () -> TransactionFactory.reconstituteTransfer(
                            txnId,
                            sourceWallet,
                            destinationWallet,
                            txnAmount,
                            txnDate,
                            "Thiếu thời điểm tạo",
                            null,
                            null
                    )
            );

            assertThrows(
                    NullPointerException.class,
                    () -> TransactionFactory.reconstituteIncomeExpense(
                            txnId,
                            TransactionType.EXPENSE,
                            sourceWallet,
                            expenseCategory,
                            txnAmount,
                            txnDate,
                            "Thiếu thời điểm tạo",
                            null,
                            null
                    )
            );
        }
    }
}

