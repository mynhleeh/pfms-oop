package com.pfms.service.impl.report;

import com.pfms.service.impl.common.EntityLookupSupport;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.presentation.dto.report.CashFlowReportRequestDTO;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportRequestDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.service.ReportService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Cài đặt dịch vụ báo cáo thống kê ({@link ReportService}).
 * Trích xuất dữ liệu giao dịch ở chế độ chỉ đọc và ủy quyền tính toán cho
 * {@link CashFlowAggregator} và {@link SpendingStructureAggregator}.
 */
public class ReportServiceImpl implements ReportService {

    // region Constants
    private static final int CASH_FLOW_MONTH_WINDOW = 12;
    private static final int MIN_REPORT_YEAR = 2000;
    // endregion

    // region State
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final NeedGroupRepository needGroupRepository;
    // endregion

    // region Constructors
    public ReportServiceImpl(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            NeedGroupRepository needGroupRepository) {

        this.transactionRepository = Objects.requireNonNull(
                transactionRepository, "TransactionRepository không được để null");
        this.categoryRepository = Objects.requireNonNull(
                categoryRepository, "CategoryRepository không được để null");
        this.needGroupRepository = Objects.requireNonNull(
                needGroupRepository, "NeedGroupRepository không được để null");
    }
    // endregion

    // region Queries
    @Override
    public CashFlowReportResponseDTO generateCashFlowReport(CashFlowReportRequestDTO request) {
        YearMonth endYm = validateCashFlowRequest(request);
        YearMonth startYm = endYm.minusMonths(CASH_FLOW_MONTH_WINDOW - 1L);

        List<Transaction> transactions = this.transactionRepository.findByDateRange(
                startYm.atDay(1), endYm.atEndOfMonth());

        return CashFlowAggregator.aggregateCashFlow(startYm, transactions);
    }

    @Override
    public SpendingStructureReportResponseDTO generateSpendingStructureReport(
            SpendingStructureReportRequestDTO request) {

        TransactionType targetType = validateStructureRequest(request);
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        LocalDate priorStartDate = startDate.minusDays(ChronoUnit.DAYS.between(startDate, endDate) + 1L);

        Map<UUID, Category> liveCategoriesById = EntityLookupSupport.indexById(
                this.categoryRepository.findAll(), Category::getCategoryId);
        Map<UUID, NeedGroup> liveNeedGroupsById = EntityLookupSupport.indexById(
                this.needGroupRepository.findAll(), NeedGroup::getNeedGroupId);

        List<Transaction> transactions = this.transactionRepository.findByDateRange(priorStartDate, endDate);

        return SpendingStructureAggregator.calculateSpendingStructure(
                targetType, startDate, endDate, priorStartDate, transactions, liveCategoriesById, liveNeedGroupsById);
    }
    // endregion

    // region Helpers
    private static YearMonth validateCashFlowRequest(CashFlowReportRequestDTO request) {
        if (request == null
                || request.getEndMonth() < 1
                || request.getEndMonth() > 12
                || request.getEndYear() < MIN_REPORT_YEAR) {
            throw new BusinessException(ErrorCode.ERR_REP_001);
        }
        return YearMonth.of(request.getEndYear(), request.getEndMonth());
    }

    private static TransactionType validateStructureRequest(SpendingStructureReportRequestDTO request) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null
                || request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException(ErrorCode.ERR_REP_001);
        }
        try {
            return TransactionType.fromString(request.getTransactionType());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.ERR_REP_001);
        }
    }
    // endregion
}
