package com.pfms.service.impl.report;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.infrastructure.memory.MemoryCategoryRepository;
import com.pfms.infrastructure.memory.MemoryNeedGroupRepository;
import com.pfms.infrastructure.memory.MemoryTransactionRepository;
import com.pfms.presentation.dto.report.CashFlowReportRequestDTO;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.CategoryShareResponseDTO;
import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportRequestDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ReportServiceImpl Unit Tests")
class ReportServiceImplTest {

    private MemoryTransactionRepository transactionRepository;
    private MemoryCategoryRepository categoryRepository;
    private MemoryNeedGroupRepository needGroupRepository;
    private ReportService reportService;

    private Wallet primaryWallet;
    private Wallet secondaryWallet;

    private NeedGroup essentialGroup;
    private NeedGroup financeGroup;
    private NeedGroup leisureGroup;
    private NeedGroup primaryIncomeGroup;

    private Category foodCategory;
    private Category transportCategory;
    private Category shoppingCategory;
    private Category feeCategory;
    private Category entertainmentCategory;
    private Category salaryCategory;
    private Category bonusCategory;

    @BeforeEach
    void setUp() {
        this.transactionRepository = new MemoryTransactionRepository();
        this.categoryRepository = new MemoryCategoryRepository();
        this.needGroupRepository = new MemoryNeedGroupRepository();
        this.reportService = new ReportServiceImpl(
                this.transactionRepository,
                this.categoryRepository,
                this.needGroupRepository);

        this.primaryWallet = new Wallet(Money.of(100_000_000L));
        this.secondaryWallet = new Wallet(Money.of(100_000_000L));

        this.essentialGroup = saveGroup("Thiết yếu", TransactionType.EXPENSE);
        this.financeGroup = saveGroup("Tài chính", TransactionType.EXPENSE);
        this.leisureGroup = saveGroup("Giải trí", TransactionType.EXPENSE);
        this.primaryIncomeGroup = saveGroup("Thu nhập chính", TransactionType.INCOME);

        this.foodCategory = saveCategory("Ăn uống", this.essentialGroup.getNeedGroupId(), TransactionType.EXPENSE);
        this.transportCategory = saveCategory("Đi lại", this.essentialGroup.getNeedGroupId(), TransactionType.EXPENSE);
        this.shoppingCategory = saveCategory("Mua sắm", this.essentialGroup.getNeedGroupId(), TransactionType.EXPENSE);
        this.feeCategory = saveCategory("Phí ngân hàng", this.financeGroup.getNeedGroupId(), TransactionType.EXPENSE);
        this.entertainmentCategory = saveCategory("Xem phim", this.leisureGroup.getNeedGroupId(), TransactionType.EXPENSE);
        this.salaryCategory = saveCategory("Lương", this.primaryIncomeGroup.getNeedGroupId(), TransactionType.INCOME);
        this.bonusCategory = saveCategory("Thưởng", this.primaryIncomeGroup.getNeedGroupId(), TransactionType.INCOME);
    }

    private NeedGroup saveGroup(String name, TransactionType type) {
        NeedGroup group = new NeedGroup(UUID.randomUUID(), name, type, "icon", "#112233");
        return this.needGroupRepository.save(group);
    }

    private Category saveCategory(String name, UUID needGroupId, TransactionType type) {
        Category category = new Category(UUID.randomUUID(), name, needGroupId, type, "icon", "#445566");
        return this.categoryRepository.save(category);
    }

    private void recordTx(
            TransactionType type,
            Wallet wallet,
            Category category,
            String amount,
            LocalDate date,
            String feeAmount) {

        UUID txId = UUID.randomUUID();
        TransactionFee fee = (feeAmount == null)
                ? null
                : new TransactionFee(UUID.randomUUID(), txId, Money.of(feeAmount), wallet, this.feeCategory);
        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                txId,
                type,
                wallet,
                category,
                Money.of(amount),
                new TransactionDate(date),
                "test-note",
                fee);
        tx.applyToWallet();
        this.transactionRepository.save(tx);
    }

    private void recordTransfer(
            Wallet source,
            Wallet destination,
            String amount,
            LocalDate date,
            String feeAmount) {

        UUID txId = UUID.randomUUID();
        TransactionFee fee = (feeAmount == null)
                ? null
                : new TransactionFee(UUID.randomUUID(), txId, Money.of(feeAmount), source, this.feeCategory);
        TransferTransaction transfer = new TransferTransaction(
                txId,
                source,
                destination,
                Money.of(amount),
                new TransactionDate(date),
                "transfer-note",
                fee);
        transfer.applyToWallet();
        this.transactionRepository.save(transfer);
    }

    @Nested
    @DisplayName("UC-REP-01: Twelve-Month Cash Flow Report Tests")
    class CashFlowTests {

        @Test
        @DisplayName("Generates 12 months across year boundary, excludes transfer principal, and includes all fees in expense")
        void shouldGenerateTwelveMonthsAcrossYearBoundaryAndHandleTransfersAndFees() {
            recordTx(TransactionType.INCOME, primaryWallet, salaryCategory, "9999999.00", LocalDate.of(2024, 3, 31), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "8888888.00", LocalDate.of(2025, 4, 1), null);

            recordTx(TransactionType.INCOME, primaryWallet, salaryCategory, "1200000.00", LocalDate.of(2024, 4, 10), null);
            recordTx(TransactionType.INCOME, primaryWallet, salaryCategory, "1000000.00", LocalDate.of(2024, 10, 5), "15000.00");
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "300000.00", LocalDate.of(2024, 10, 20), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, transportCategory, "200000.00", LocalDate.of(2024, 12, 15), null);
            recordTransfer(primaryWallet, secondaryWallet, "5000000.00", LocalDate.of(2025, 1, 10), "25000.00");
            recordTx(TransactionType.INCOME, primaryWallet, salaryCategory, "300000.00", LocalDate.of(2025, 2, 14), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "100000.00", LocalDate.of(2025, 2, 28), null);
            recordTx(TransactionType.INCOME, primaryWallet, bonusCategory, "500000.00", LocalDate.of(2025, 3, 1), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, shoppingCategory, "600000.00", LocalDate.of(2025, 3, 31), null);

            CashFlowReportResponseDTO report = reportService.generateCashFlowReport(new CashFlowReportRequestDTO(3, 2025));

            assertNotNull(report);
            assertEquals(12, report.getMonthCount());

            List<MonthlyCashFlowResponseDTO> months = report.getMonths();
            assertMonth(months.get(0), "2024-04", "1200000.00", "0.00", "1200000.00", "100.00");
            for (int i = 1; i <= 5; i++) {
                String expectedMonth = YearMonth.of(2024, 4).plusMonths(i).toString();
                assertMonth(months.get(i), expectedMonth, "0.00", "0.00", "0.00", "0.00");
            }
            assertMonth(months.get(6), "2024-10", "1000000.00", "315000.00", "685000.00", "68.50");
            assertMonth(months.get(7), "2024-11", "0.00", "0.00", "0.00", "0.00");
            assertMonth(months.get(8), "2024-12", "0.00", "200000.00", "-200000.00", "0.00");
            assertMonth(months.get(9), "2025-01", "0.00", "25000.00", "-25000.00", "0.00");
            assertMonth(months.get(10), "2025-02", "300000.00", "100000.00", "200000.00", "66.67");
            assertMonth(months.get(11), "2025-03", "500000.00", "600000.00", "-100000.00", "-20.00");

            assertEquals(new BigDecimal("250000.00"), report.getAvgIncome());
            assertEquals(new BigDecimal("103333.33"), report.getAvgExpense());
            assertEquals(new BigDecimal("146666.67"), report.getAvgNetCashFlow());
            assertEquals(report, reportService.generateCashFlowReport(new CashFlowReportRequestDTO(3, 2025)));
        }

        @Test
        @DisplayName("Applies HALF_EVEN rounding strictly when computing 12-month averages")
        void shouldApplyHalfEvenRoundingOnMonthlyAverages() {
            recordTx(TransactionType.INCOME, primaryWallet, salaryCategory, "0.06", LocalDate.of(2025, 1, 15), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "0.18", LocalDate.of(2025, 1, 16), null);

            CashFlowReportResponseDTO report = reportService.generateCashFlowReport(new CashFlowReportRequestDTO(6, 2025));

            assertEquals(12, report.getMonthCount());
            assertEquals(new BigDecimal("0.00"), report.getAvgIncome());
            assertEquals(new BigDecimal("0.02"), report.getAvgExpense());
            assertEquals(new BigDecimal("-0.01"), report.getAvgNetCashFlow());
        }

        @Test
        @DisplayName("Allows future period query in read-only mode and returns zeroes")
        void shouldAllowFutureMonthAndReturnZeroes() {
            YearMonth futureEnd = YearMonth.now().plusMonths(8);

            CashFlowReportResponseDTO report = reportService.generateCashFlowReport(
                    new CashFlowReportRequestDTO(futureEnd.getMonthValue(), futureEnd.getYear()));

            assertEquals(12, report.getMonthCount());
            for (MonthlyCashFlowResponseDTO month : report.getMonths()) {
                assertEquals(new BigDecimal("0.00"), month.getTotalIncome());
                assertEquals(new BigDecimal("0.00"), month.getTotalExpense());
                assertEquals(new BigDecimal("0.00"), month.getNetCashFlow());
                assertEquals(new BigDecimal("0.00"), month.getSavingsRate());
            }
            assertEquals(new BigDecimal("0.00"), report.getAvgIncome());
            assertEquals(new BigDecimal("0.00"), report.getAvgExpense());
            assertEquals(new BigDecimal("0.00"), report.getAvgNetCashFlow());
        }

        private void assertMonth(
                MonthlyCashFlowResponseDTO dto,
                String expectedYearMonth,
                String expectedIncome,
                String expectedExpense,
                String expectedNet,
                String expectedSavingsRate) {

            assertEquals(expectedYearMonth, dto.getYearMonth());
            assertEquals(new BigDecimal(expectedIncome), dto.getTotalIncome());
            assertEquals(new BigDecimal(expectedExpense), dto.getTotalExpense());
            assertEquals(new BigDecimal(expectedNet), dto.getNetCashFlow());
            assertEquals(new BigDecimal(expectedSavingsRate), dto.getSavingsRate());
        }
    }

    @Nested
    @DisplayName("UC-REP-02: Spending & Income Structure Tests")
    class SpendingStructureTests {

        @Test
        @DisplayName("Aggregates expense structure with dual shares, transfer/income fees, zero exclusion, and descending sort")
        void shouldAggregateExpenseStructureWithFeesDualSharesAndDescendingSort() {
            LocalDate start = LocalDate.of(2025, 5, 1);
            LocalDate end = LocalDate.of(2025, 5, 31);

            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "600000.00", LocalDate.of(2025, 5, 5), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, transportCategory, "200000.00", LocalDate.of(2025, 5, 10), null);
            recordTransfer(primaryWallet, secondaryWallet, "2000000.00", LocalDate.of(2025, 5, 12), "60000.00");
            recordTx(TransactionType.INCOME, primaryWallet, salaryCategory, "5000000.00", LocalDate.of(2025, 5, 15), "40000.00");

            SpendingStructureReportResponseDTO report = reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(start, end, null));

            assertEquals("EXPENSE", report.getTransactionType());
            assertEquals(start, report.getStartDate());
            assertEquals(end, report.getEndDate());
            assertEquals(new BigDecimal("900000.00"), report.getTotalAmount());
            assertEquals(2, report.getGroupCount());

            // Group 0: Thiết yếu (800,000 / 900,000 = 88.89%), Shopping excluded since 0.00
            NeedGroupShareResponseDTO g0 = report.getGroups().get(0);
            assertEquals(essentialGroup.getNeedGroupId(), g0.getNeedGroupId());
            assertEquals("Thiết yếu", g0.getName());
            assertEquals("ACTIVE", g0.getStatus());
            assertEquals(new BigDecimal("800000.00"), g0.getTotalAmount());
            assertEquals(new BigDecimal("88.89"), g0.getShare());
            assertEquals(2, g0.getCategoryCount());

            CategoryShareResponseDTO c0 = g0.getCategories().get(0);
            assertEquals(foodCategory.getCategoryId(), c0.getCategoryId());
            assertEquals("Ăn uống", c0.getName());
            assertEquals(new BigDecimal("600000.00"), c0.getTotalAmount());
            assertEquals(new BigDecimal("66.67"), c0.getShare());
            assertEquals(new BigDecimal("75.00"), c0.getShareInGroup());

            CategoryShareResponseDTO c1 = g0.getCategories().get(1);
            assertEquals(transportCategory.getCategoryId(), c1.getCategoryId());
            assertEquals("Đi lại", c1.getName());
            assertEquals(new BigDecimal("200000.00"), c1.getTotalAmount());
            assertEquals(new BigDecimal("22.22"), c1.getShare());
            assertEquals(new BigDecimal("25.00"), c1.getShareInGroup());

            // Group 1: Tài chính (100,000 / 900,000 = 11.11%)
            NeedGroupShareResponseDTO g1 = report.getGroups().get(1);
            assertEquals(financeGroup.getNeedGroupId(), g1.getNeedGroupId());
            assertEquals(new BigDecimal("100000.00"), g1.getTotalAmount());
            assertEquals(new BigDecimal("11.11"), g1.getShare());
            assertEquals(1, g1.getCategoryCount());

            CategoryShareResponseDTO feeCatDto = g1.getCategories().get(0);
            assertEquals(feeCategory.getCategoryId(), feeCatDto.getCategoryId());
            assertEquals(new BigDecimal("100000.00"), feeCatDto.getTotalAmount());
            assertEquals(new BigDecimal("11.11"), feeCatDto.getShare());
            assertEquals(new BigDecimal("100.00"), feeCatDto.getShareInGroup());
        }

        @Test
        @DisplayName("Aggregates INCOME structure only and ignores expense transactions and transaction fees")
        void shouldAggregateIncomeStructureOnly() {
            LocalDate start = LocalDate.of(2025, 6, 1);
            LocalDate end = LocalDate.of(2025, 6, 30);

            recordTx(TransactionType.INCOME, primaryWallet, salaryCategory, "9000000.00", LocalDate.of(2025, 6, 5), "50000.00");
            recordTx(TransactionType.INCOME, primaryWallet, bonusCategory, "1000000.00", LocalDate.of(2025, 6, 20), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "500000.00", LocalDate.of(2025, 6, 10), null);
            recordTransfer(primaryWallet, secondaryWallet, "2000000.00", LocalDate.of(2025, 6, 11), "10000.00");

            SpendingStructureReportResponseDTO report = reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(start, end, "  income  "));

            assertEquals("INCOME", report.getTransactionType());
            assertEquals(new BigDecimal("10000000.00"), report.getTotalAmount());
            assertEquals(1, report.getGroupCount());

            NeedGroupShareResponseDTO incomeDto = report.getGroups().get(0);
            assertEquals(new BigDecimal("100.00"), incomeDto.getShare());
            assertEquals(2, incomeDto.getCategoryCount());
            assertEquals("Lương", incomeDto.getCategories().get(0).getName());
            assertEquals(new BigDecimal("90.00"), incomeDto.getCategories().get(0).getShare());
            assertEquals(new BigDecimal("90.00"), incomeDto.getCategories().get(0).getShareInGroup());
            assertEquals("Thưởng", incomeDto.getCategories().get(1).getName());
            assertEquals(new BigDecimal("10.00"), incomeDto.getCategories().get(1).getShare());
            assertEquals(new BigDecimal("10.00"), incomeDto.getCategories().get(1).getShareInGroup());
        }

        @Test
        @DisplayName("Reflects live category rename, NeedGroup move, and preserves original name with DELETED status for soft-deleted items")
        void shouldReflectLiveHierarchyAndPreserveOriginalNameWithDeletedStatus() {
            LocalDate start = LocalDate.of(2025, 4, 1);
            LocalDate end = LocalDate.of(2025, 4, 30);

            // Initially in leisureGroup / essentialGroup / financeGroup
            recordTx(TransactionType.EXPENSE, primaryWallet, entertainmentCategory, "300000.00", LocalDate.of(2025, 4, 5), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "200000.00", LocalDate.of(2025, 4, 6), null);
            recordTransfer(primaryWallet, secondaryWallet, "1000000.00", LocalDate.of(2025, 4, 7), "50000.00");

            // Rename and move entertainmentCategory into essentialGroup
            entertainmentCategory.rename("Du lịch cuối tuần");
            entertainmentCategory.changeNeedGroup(essentialGroup.getNeedGroupId(), TransactionType.EXPENSE);
            categoryRepository.save(entertainmentCategory);

            // Soft-delete foodCategory, feeCategory, and leisureGroup (plus shoppingCategory so leisureGroup has deleted child)
            foodCategory.markDeleted();
            categoryRepository.save(foodCategory);
            feeCategory.markDeleted();
            categoryRepository.save(feeCategory);

            Category leisureDeletedChild = saveCategory("Karaoke cũ", leisureGroup.getNeedGroupId(), TransactionType.EXPENSE);
            recordTx(TransactionType.EXPENSE, primaryWallet, leisureDeletedChild, "100000.00", LocalDate.of(2025, 4, 8), null);
            leisureDeletedChild.markDeleted();
            categoryRepository.save(leisureDeletedChild);
            leisureGroup.markDeleted(3);
            needGroupRepository.save(leisureGroup);

            SpendingStructureReportResponseDTO report = reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(start, end, "EXPENSE"));

            assertEquals(new BigDecimal("650000.00"), report.getTotalAmount());
            assertEquals(3, report.getGroupCount());

            // 1. essentialGroup: 300k (renamed/moved) + 200k (deleted foodCategory) = 500k
            NeedGroupShareResponseDTO essentialDto = report.getGroups().get(0);
            assertEquals("ACTIVE", essentialDto.getStatus());
            assertEquals(new BigDecimal("500000.00"), essentialDto.getTotalAmount());
            assertEquals(2, essentialDto.getCategoryCount());
            assertEquals("Du lịch cuối tuần", essentialDto.getCategories().get(0).getName());
            assertEquals("ACTIVE", essentialDto.getCategories().get(0).getStatus());
            assertEquals("Ăn uống", essentialDto.getCategories().get(1).getName());
            assertEquals("DELETED", essentialDto.getCategories().get(1).getStatus());

            // 2. leisureGroup (DELETED) with deleted child "Karaoke cũ" (100k)
            NeedGroupShareResponseDTO leisureDto = report.getGroups().get(1);
            assertEquals("Giải trí", leisureDto.getName());
            assertEquals("DELETED", leisureDto.getStatus());
            assertEquals(new BigDecimal("100000.00"), leisureDto.getTotalAmount());
            assertEquals("Karaoke cũ", leisureDto.getCategories().get(0).getName());
            assertEquals("DELETED", leisureDto.getCategories().get(0).getStatus());

            // 3. financeGroup with deleted feeCategory (50k)
            NeedGroupShareResponseDTO financeDto = report.getGroups().get(2);
            assertEquals("ACTIVE", financeDto.getStatus());
            assertEquals("Phí ngân hàng", financeDto.getCategories().get(0).getName());
            assertEquals("DELETED", financeDto.getCategories().get(0).getStatus());
        }
    }

    @Nested
    @DisplayName("UC-REP-02: Period Comparison, Future Periods & Read-Only Guarantee")
    class ComparisonAndReadOnlyTests {

        @Test
        @DisplayName("Calculates prior period with identical day span and covers prior>0, isNew, and category-dropped cases")
        void shouldCalculatePeriodComparisonAcrossExactEqualDaySpan() {
            // Current: [2025-07-01 .. 2025-07-10] (10 days) -> Prior: [2025-06-21 .. 2025-06-30] (10 days)
            LocalDate start = LocalDate.of(2025, 7, 1);
            LocalDate end = LocalDate.of(2025, 7, 10);

            // Strictly outside both periods (must be excluded)
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "999999.00", LocalDate.of(2025, 6, 20), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "999999.00", LocalDate.of(2025, 7, 11), null);

            // foodCategory: prior = 300,000, current = 100,000 -> diff = -200,000, pct = -66.67%, isNew = false
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "150000.00", LocalDate.of(2025, 6, 21), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "150000.00", LocalDate.of(2025, 6, 30), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "100000.00", LocalDate.of(2025, 7, 1), null);

            // transportCategory: prior = 0, current = 200,000 -> diff = +200,000, pct = 0.00%, isNew = true
            recordTx(TransactionType.EXPENSE, primaryWallet, transportCategory, "200000.00", LocalDate.of(2025, 7, 10), null);

            // shoppingCategory: prior = 100,000, current = 0 -> excluded from categories, still counted in essentialGroup's prior (300k + 100k = 400k)
            recordTx(TransactionType.EXPENSE, primaryWallet, shoppingCategory, "100000.00", LocalDate.of(2025, 6, 25), null);

            SpendingStructureReportResponseDTO report = reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(start, end, "EXPENSE"));

            // Root totals: current = 300,000, prior = 400,000, diff = -100,000, pct = -25.00%
            assertEquals(new BigDecimal("300000.00"), report.getTotalAmount());
            assertEquals(new BigDecimal("400000.00"), report.getPreviousTotalAmount());
            assertEquals(new BigDecimal("-100000.00"), report.getDifferenceAmount());
            assertEquals(new BigDecimal("-25.00"), report.getPercentageChange());
            assertFalse(report.isNew());

            NeedGroupShareResponseDTO essentialDto = report.getGroups().get(0);
            assertEquals(new BigDecimal("300000.00"), essentialDto.getTotalAmount());
            assertEquals(new BigDecimal("400000.00"), essentialDto.getPreviousTotalAmount());
            assertEquals(new BigDecimal("-100000.00"), essentialDto.getDifferenceAmount());
            assertEquals(new BigDecimal("-25.00"), essentialDto.getPercentageChange());
            assertFalse(essentialDto.isNew());
            assertEquals(2, essentialDto.getCategoryCount());

            CategoryShareResponseDTO newCat = essentialDto.getCategories().get(0);
            assertEquals("Đi lại", newCat.getName());
            assertEquals(new BigDecimal("0.00"), newCat.getPreviousTotalAmount());
            assertEquals(new BigDecimal("200000.00"), newCat.getDifferenceAmount());
            assertEquals(new BigDecimal("0.00"), newCat.getPercentageChange());
            assertTrue(newCat.isNew());

            CategoryShareResponseDTO droppedPctCat = essentialDto.getCategories().get(1);
            assertEquals("Ăn uống", droppedPctCat.getName());
            assertEquals(new BigDecimal("300000.00"), droppedPctCat.getPreviousTotalAmount());
            assertEquals(new BigDecimal("-200000.00"), droppedPctCat.getDifferenceAmount());
            assertEquals(new BigDecimal("-66.67"), droppedPctCat.getPercentageChange());
            assertFalse(droppedPctCat.isNew());
        }

        @Test
        @DisplayName("Handles 0/0 and 0/prior root edge cases with empty group list")
        void shouldHandleZeroCurrentVsZeroAndPositivePrior() {
            // Case 1: 0 / 0
            SpendingStructureReportResponseDTO zeroBoth = reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5), "EXPENSE"));
            assertEquals(new BigDecimal("0.00"), zeroBoth.getTotalAmount());
            assertEquals(new BigDecimal("0.00"), zeroBoth.getPreviousTotalAmount());
            assertEquals(new BigDecimal("0.00"), zeroBoth.getDifferenceAmount());
            assertEquals(new BigDecimal("0.00"), zeroBoth.getPercentageChange());
            assertFalse(zeroBoth.isNew());
            assertTrue(zeroBoth.getGroups().isEmpty());

            // Case 2: current = 0, prior = 250,000 -> -250,000, -100.00%, isNew = false, empty groups
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "250000.00", LocalDate.of(2025, 8, 3), null);

            SpendingStructureReportResponseDTO droppedToZero = reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(LocalDate.of(2025, 8, 6), LocalDate.of(2025, 8, 10), "EXPENSE"));
            assertEquals(new BigDecimal("0.00"), droppedToZero.getTotalAmount());
            assertEquals(new BigDecimal("250000.00"), droppedToZero.getPreviousTotalAmount());
            assertEquals(new BigDecimal("-250000.00"), droppedToZero.getDifferenceAmount());
            assertEquals(new BigDecimal("-100.00"), droppedToZero.getPercentageChange());
            assertFalse(droppedToZero.isNew());
            assertTrue(droppedToZero.getGroups().isEmpty());
        }

        @Test
        @DisplayName("Remains strictly read-only on repositories and wallet balances and accepts future date ranges")
        void shouldRemainStrictlyReadOnlyAndAcceptFutureRanges() {
            recordTx(TransactionType.INCOME, primaryWallet, salaryCategory, "2000000.00", LocalDate.of(2025, 9, 1), null);
            recordTx(TransactionType.EXPENSE, primaryWallet, foodCategory, "500000.00", LocalDate.of(2025, 9, 2), null);

            Money balanceBefore = primaryWallet.getCurrentBalance();
            int txCountBefore = transactionRepository.findAll().size();
            int catCountBefore = categoryRepository.findAll().size();
            int groupCountBefore = needGroupRepository.findAll().size();

            reportService.generateCashFlowReport(new CashFlowReportRequestDTO(9, 2025));
            SpendingStructureReportResponseDTO futureStructure = reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(LocalDate.now().plusDays(10), LocalDate.now().plusDays(20), "EXPENSE"));

            assertEquals(new BigDecimal("0.00"), futureStructure.getTotalAmount());
            assertTrue(futureStructure.getGroups().isEmpty());
            assertEquals(balanceBefore, primaryWallet.getCurrentBalance());
            assertEquals(txCountBefore, transactionRepository.findAll().size());
            assertEquals(catCountBefore, categoryRepository.findAll().size());
            assertEquals(groupCountBefore, needGroupRepository.findAll().size());
        }

        @Test
        @DisplayName("Queries combined prior-plus-current date range in a single repository scan")
        void shouldQueryCombinedDateRangeInSingleRepositoryCall() {
            LocalDate start = LocalDate.of(2025, 7, 1);
            LocalDate end = LocalDate.of(2025, 7, 10);
            LocalDate expectedPriorStart = LocalDate.of(2025, 6, 21);

            AtomicInteger queryCount = new AtomicInteger(0);
            LocalDate[] capturedRange = new LocalDate[2];
            MemoryTransactionRepository countingRepo = new MemoryTransactionRepository() {
                @Override
                public List<com.pfms.domain.model.transaction.Transaction> findByDateRange(
                        LocalDate s, LocalDate e) {
                    queryCount.incrementAndGet();
                    capturedRange[0] = s;
                    capturedRange[1] = e;
                    return super.findByDateRange(s, e);
                }
            };

            IncomeExpenseTransaction priorTx = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, primaryWallet, foodCategory,
                    Money.of("150000.00"), new TransactionDate(LocalDate.of(2025, 6, 30)), "prior", null);
            IncomeExpenseTransaction currentTx = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, primaryWallet, foodCategory,
                    Money.of("300000.00"), new TransactionDate(LocalDate.of(2025, 7, 1)), "current", null);
            countingRepo.save(priorTx);
            countingRepo.save(currentTx);

            ReportService optimizedService = new ReportServiceImpl(
                    countingRepo, categoryRepository, needGroupRepository);

            SpendingStructureReportResponseDTO report = optimizedService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(start, end, "EXPENSE"));

            assertEquals(1, queryCount.get());
            assertEquals(expectedPriorStart, capturedRange[0]);
            assertEquals(end, capturedRange[1]);
            assertEquals(new BigDecimal("300000.00"), report.getTotalAmount());
            assertEquals(new BigDecimal("150000.00"), report.getPreviousTotalAmount());
            assertEquals(new BigDecimal("100.00"), report.getPercentageChange());
        }
    }

    @Nested
    @DisplayName("Validation & ERR_REP_001 Tests")
    class ValidationTests {

        @Test
        @DisplayName("Rejects null repositories on construction")
        void shouldRejectNullRepositories() {
            assertThrows(NullPointerException.class,
                    () -> new ReportServiceImpl(null, categoryRepository, needGroupRepository));
            assertThrows(NullPointerException.class,
                    () -> new ReportServiceImpl(transactionRepository, null, needGroupRepository));
            assertThrows(NullPointerException.class,
                    () -> new ReportServiceImpl(transactionRepository, categoryRepository, null));
        }

        @Test
        @DisplayName("Throws BusinessException ERR_REP_001 for invalid CashFlowReportRequestDTO inputs")
        void shouldThrowErrRep001ForInvalidCashFlowRequests() {
            assertErrRep001(() -> reportService.generateCashFlowReport(null));
            assertErrRep001(() -> reportService.generateCashFlowReport(new CashFlowReportRequestDTO(0, 2025)));
            assertErrRep001(() -> reportService.generateCashFlowReport(new CashFlowReportRequestDTO(13, 2025)));
            assertErrRep001(() -> reportService.generateCashFlowReport(new CashFlowReportRequestDTO(5, 1999)));
        }

        @Test
        @DisplayName("Throws BusinessException ERR_REP_001 for invalid SpendingStructureReportRequestDTO inputs")
        void shouldThrowErrRep001ForInvalidSpendingStructureRequests() {
            LocalDate today = LocalDate.of(2025, 5, 10);

            assertErrRep001(() -> reportService.generateSpendingStructureReport(null));
            assertErrRep001(() -> reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(null, today, "EXPENSE")));
            assertErrRep001(() -> reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(today, null, "EXPENSE")));
            assertErrRep001(() -> reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(today, today.minusDays(1), "EXPENSE")));
            assertErrRep001(() -> reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(today.minusDays(5), today, "TRANSFER")));
            assertErrRep001(() -> reportService.generateSpendingStructureReport(
                    new SpendingStructureReportRequestDTO(today.minusDays(5), today, "UNKNOWN")));
        }

        private void assertErrRep001(org.junit.jupiter.api.function.Executable action) {
            BusinessException ex = assertThrows(BusinessException.class, action);
            assertEquals(ErrorCode.ERR_REP_001, ex.getErrorCode());
        }
    }
}

