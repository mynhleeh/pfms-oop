package com.pfms.presentation.dto.transaction;

import com.pfms.common.util.PaginationUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * DTO đóng gói bộ tiêu chí tra cứu và lọc sổ giao dịch thu chi kèm thông số
 * phân trang (UC-TXN-02).
 * Hỗ trợ lọc theo khoảng thời gian, tập các ví, loại thu chi, tập danh mục và
 * từ khóa tìm kiếm.
 */
public final class FilterTransactionsRequestDTO {

    // region State
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Set<UUID> walletIds;
    private final String transactionType;
    private final Set<UUID> categoryIds;
    private final String keyword;
    private final int pageNumber;
    private final int pageSize;
    // endregion

    // region Constructors
    public FilterTransactionsRequestDTO() {
        this(null, null, null, null, null, "",
                PaginationUtils.DEFAULT_PAGE_NUMBER, PaginationUtils.DEFAULT_PAGE_SIZE);
    }

    public FilterTransactionsRequestDTO(LocalDate startDate, LocalDate endDate, Set<UUID> walletIds) {
        this(startDate, endDate, walletIds, null, null, "",
                PaginationUtils.DEFAULT_PAGE_NUMBER, PaginationUtils.DEFAULT_PAGE_SIZE);
    }

    public FilterTransactionsRequestDTO(
            LocalDate startDate,
            LocalDate endDate,
            Set<UUID> walletIds,
            String transactionType,
            Set<UUID> categoryIds,
            String keyword) {

        this(startDate, endDate, walletIds, transactionType, categoryIds, keyword,
                PaginationUtils.DEFAULT_PAGE_NUMBER, PaginationUtils.DEFAULT_PAGE_SIZE);
    }

    public FilterTransactionsRequestDTO(
            LocalDate startDate,
            LocalDate endDate,
            Set<UUID> walletIds,
            String transactionType,
            Set<UUID> categoryIds,
            String keyword,
            int pageNumber,
            int pageSize) {

        if (transactionType != null) {
            String normalizedType = transactionType.trim().toUpperCase(Locale.ROOT);
            this.transactionType = (normalizedType.isEmpty() || "ALL".equals(normalizedType))
                    ? null
                    : normalizedType;
        } else {
            this.transactionType = null;
        }

        this.startDate = startDate;
        this.endDate = endDate;
        this.walletIds = copyNonNullSet(walletIds);
        this.categoryIds = copyNonNullSet(categoryIds);
        this.keyword = (keyword == null) ? "" : keyword.trim();
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public static FilterTransactionsRequestDTO currentMonth() {
        YearMonth currentMonth = YearMonth.now();
        return new FilterTransactionsRequestDTO(currentMonth.atDay(1), currentMonth.atEndOfMonth(), null);
    }
    // endregion

    // region Getters
    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Set<UUID> getWalletIds() {
        return this.walletIds;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public Set<UUID> getCategoryIds() {
        return this.categoryIds;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public boolean hasDateFilter() {
        return this.startDate != null || this.endDate != null;
    }

    public boolean hasWalletFilter() {
        return !this.walletIds.isEmpty();
    }

    public boolean hasCategoryFilter() {
        return !this.categoryIds.isEmpty();
    }

    public boolean hasTypeFilter() {
        return this.transactionType != null;
    }

    public boolean hasKeywordFilter() {
        return !this.keyword.isEmpty();
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilterTransactionsRequestDTO that)) {
            return false;
        }
        return this.pageNumber == that.pageNumber
                && this.pageSize == that.pageSize
                && Objects.equals(this.startDate, that.startDate)
                && Objects.equals(this.endDate, that.endDate)
                && Objects.equals(this.walletIds, that.walletIds)
                && Objects.equals(this.transactionType, that.transactionType)
                && Objects.equals(this.categoryIds, that.categoryIds)
                && Objects.equals(this.keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.startDate,
                this.endDate,
                this.walletIds,
                this.transactionType,
                this.categoryIds,
                this.keyword,
                this.pageNumber,
                this.pageSize);
    }

    @Override
    public String toString() {
        return "FilterTransactionsRequestDTO{" +
                "startDate=" + this.startDate +
                ", endDate=" + this.endDate +
                ", walletIds=" + this.walletIds +
                ", transactionType='" + this.transactionType + '\'' +
                ", categoryIds=" + this.categoryIds +
                ", keyword='" + this.keyword + '\'' +
                ", pageNumber=" + this.pageNumber +
                ", pageSize=" + this.pageSize +
                '}';
    }

    private static Set<UUID> copyNonNullSet(Set<UUID> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptySet();
        }
        Set<UUID> copy = new HashSet<>(source.size());
        for (UUID id : source) {
            if (id != null) {
                copy.add(id);
            }
        }
        return copy.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(copy);
    }
    // endregion
}
