package com.pfms.service.impl.report;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFactory;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SpendingPeriodAccumulator Unit Tests")
class SpendingPeriodAccumulatorTest {

    private Wallet testWallet;
    private UUID groupId1;
    private UUID groupId2;
    private Category category1;
    private Category category2;
    private Category feeCategory;

    @BeforeEach
    void setUp() {
        testWallet = new Wallet(
                UUID.randomUUID(), "Ví chính", "", false,
                LocalDateTime.now(), new ActiveWalletState(), Money.of("10000000.00"));

        groupId1 = UUID.randomUUID();
        groupId2 = UUID.randomUUID();

        category1 = new Category(
                UUID.randomUUID(), "Ăn uống", groupId1,
                TransactionType.EXPENSE, "food.png", "#FF5722");
        category2 = new Category(
                UUID.randomUUID(), "Đi lại", groupId2,
                TransactionType.EXPENSE, "car.png", "#2196F3");
        feeCategory = new Category(
                UUID.randomUUID(), "Phí dịch vụ", groupId2,
                TransactionType.EXPENSE, "fee.png", "#9E9E9E");
    }

    @Test
    @DisplayName("Khởi tạo đúng trạng thái ban đầu có hoặc không theo dõi danh mục")
    void shouldInitializeCorrectlyWithAndWithoutTracking() {
        SpendingPeriodAccumulator tracked = new SpendingPeriodAccumulator(true);
        SpendingPeriodAccumulator untracked = new SpendingPeriodAccumulator(false);

        assertEquals(Money.ZERO, tracked.getTotalAmount());
        assertEquals(Money.ZERO, untracked.getTotalAmount());
        assertTrue(tracked.getGroupTotals().isEmpty());
        assertTrue(untracked.getGroupTotals().isEmpty());
        assertTrue(tracked.groupResolvedCategories().isEmpty());
    }

    @Test
    @DisplayName("Tích lũy chính xác giao dịch kỳ hiện tại và kỳ trước bao gồm cả phụ phí")
    void shouldAccumulateCurrentAndPriorPeriodTransactionsWithFees() {
        LocalDate priorStartDate = LocalDate.of(2026, 1, 4);
        LocalDate startDate = LocalDate.of(2026, 1, 15);
        LocalDate endDate = LocalDate.of(2026, 1, 25);

        // Giao dịch kỳ trước (2026-01-10)
        IncomeExpenseTransaction priorTx = new IncomeExpenseTransaction(
                UUID.randomUUID(), TransactionType.EXPENSE, testWallet, category1,
                Money.of("500000.00"), TransactionDate.of(2026, 1, 10), "Ăn uống kỳ trước", null);

        // Giao dịch kỳ hiện tại (2026-01-20) kèm phụ phí 20,000 VNĐ
        IncomeExpenseTransaction currentTx = TransactionFactory.createIncomeExpense(
                TransactionType.EXPENSE, testWallet, category2, Money.of("1000000.00"),
                TransactionDate.of(2026, 1, 20), "Đi lại kỳ này",
                Money.of("20000.00"), feeCategory);

        SpendingPeriodAccumulator current = new SpendingPeriodAccumulator(true);
        SpendingPeriodAccumulator prior = new SpendingPeriodAccumulator(false);

        Map<UUID, Category> liveCategories = Map.of(
                category1.getCategoryId(), category1,
                category2.getCategoryId(), category2,
                feeCategory.getCategoryId(), feeCategory
        );

        SpendingPeriodAccumulator.accumulatePeriods(
                priorStartDate, startDate, endDate, TransactionType.EXPENSE,
                current, prior, liveCategories, List.of(priorTx, currentTx));

        // Kiểm tra kỳ trước
        assertEquals(Money.of("500000.00"), prior.getTotalAmount());
        assertEquals(Money.of("500000.00"), prior.getGroupTotal(groupId1));
        assertEquals(Money.of("500000.00"), prior.getCategoryTotal(category1.getCategoryId()));

        // Kiểm tra kỳ hiện tại (1,000,000 + 20,000 phụ phí)
        assertEquals(Money.of("1020000.00"), current.getTotalAmount());
        assertEquals(Money.of("1000000.00"), current.getCategoryTotal(category2.getCategoryId()));
        assertEquals(Money.of("20000.00"), current.getCategoryTotal(feeCategory.getCategoryId()));
        assertEquals(Money.of("1020000.00"), current.getGroupTotal(groupId2));

        // Kiểm tra gom nhóm danh mục đã giải quyết
        Map<UUID, List<Category>> groupedCategories = current.groupResolvedCategories();
        assertNotNull(groupedCategories.get(groupId2));
        assertEquals(2, groupedCategories.get(groupId2).size());
    }

    @Test
    @DisplayName("Bỏ qua giao dịch ngoài khoảng ngày, sai loại dòng tiền hoặc danh sách rỗng")
    void shouldIgnoreTransactionsOutsideRangeAndHandleNullsGracefully() {
        LocalDate priorStartDate = LocalDate.of(2026, 1, 5);
        LocalDate startDate = LocalDate.of(2026, 1, 10);
        LocalDate endDate = LocalDate.of(2026, 1, 20);

        // Quá khứ xa trước priorStartDate
        IncomeExpenseTransaction tooEarlyTx = new IncomeExpenseTransaction(
                UUID.randomUUID(), TransactionType.EXPENSE, testWallet, category1,
                Money.of("100000.00"), TransactionDate.of(2026, 1, 2), "Quá sớm", null);

        // Sau endDate
        IncomeExpenseTransaction tooLateTx = new IncomeExpenseTransaction(
                UUID.randomUUID(), TransactionType.EXPENSE, testWallet, category1,
                Money.of("200000.00"), TransactionDate.of(2026, 1, 22), "Quá trễ", null);

        // Sai TransactionType (INCOME khi đang gom EXPENSE)
        Category incomeCategory = new Category(
                UUID.randomUUID(), "Lương", groupId1,
                TransactionType.INCOME, "salary.png", "#4CAF50");
        IncomeExpenseTransaction incomeTx = new IncomeExpenseTransaction(
                UUID.randomUUID(), TransactionType.INCOME, testWallet, incomeCategory,
                Money.of("900000.00"), TransactionDate.of(2026, 1, 15), "Thu nhập", null);

        SpendingPeriodAccumulator current = new SpendingPeriodAccumulator(true);
        SpendingPeriodAccumulator prior = new SpendingPeriodAccumulator(false);

        // Danh sách null
        SpendingPeriodAccumulator.accumulatePeriods(
                priorStartDate, startDate, endDate, TransactionType.EXPENSE,
                current, prior, Map.of(), null);
        assertEquals(Money.ZERO, current.getTotalAmount());
        assertEquals(Money.ZERO, prior.getTotalAmount());

        // Danh sách chứa các giao dịch bị loại bỏ
        SpendingPeriodAccumulator.accumulatePeriods(
                priorStartDate, startDate, endDate, TransactionType.EXPENSE,
                current, prior, Map.of(category1.getCategoryId(), category1),
                List.of(tooEarlyTx, tooLateTx, incomeTx));

        assertEquals(Money.ZERO, current.getTotalAmount());
        assertEquals(Money.ZERO, prior.getTotalAmount());
    }

    @Test
    @DisplayName("Ưu tiên sử dụng thông tin danh mục trực tiếp từ liveCategoriesById")
    void shouldResolveLiveCategoryOverSnapshot() {
        LocalDate priorStartDate = LocalDate.of(2026, 1, 1);
        LocalDate startDate = LocalDate.of(2026, 1, 10);
        LocalDate endDate = LocalDate.of(2026, 1, 20);

        UUID newGroupId = UUID.randomUUID();
        Category liveCategory = new Category(
                category1.getCategoryId(), "Ăn uống mới", newGroupId,
                TransactionType.EXPENSE, "food_new.png", "#4CAF50");

        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                UUID.randomUUID(), TransactionType.EXPENSE, testWallet, category1,
                Money.of("300000.00"), TransactionDate.of(2026, 1, 12), "Chi tiêu", null);

        SpendingPeriodAccumulator current = new SpendingPeriodAccumulator(true);
        SpendingPeriodAccumulator prior = new SpendingPeriodAccumulator(false);

        SpendingPeriodAccumulator.accumulatePeriods(
                priorStartDate, startDate, endDate, TransactionType.EXPENSE,
                current, prior, Map.of(category1.getCategoryId(), liveCategory), List.of(tx));

        assertEquals(Money.of("300000.00"), current.getTotalAmount());
        assertEquals(Money.of("300000.00"), current.getGroupTotal(newGroupId));
        assertEquals(Money.ZERO, current.getGroupTotal(groupId1));
        assertEquals("Ăn uống mới", current.groupResolvedCategories().get(newGroupId).get(0).getName());
    }
}
