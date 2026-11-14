package com.pfms.presentation.dto.transaction;

import com.pfms.common.util.PaginationUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DTO kết xuất bảng danh sách giao dịch thu chi phân trang kèm các chỉ số tài chính tổng hợp (UC-TXN-02).
 * Phục vụ hiển thị bảng dữ liệu JTable và thanh tóm tắt dòng tiền (tổng thu, tổng chi gồm phí, dòng tiền thuần).
 */
public final class TransactionListResponseDTO {

    // region State
    private final List<TransactionResponseDTO> items;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;
    private final BigDecimal netCashFlow;
    private final int pageNumber;
    private final int pageSize;
    private final long totalRecords;
    private final int totalPages;
    // endregion

    // region Constructors
    public TransactionListResponseDTO() {
        this(Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                PaginationUtils.DEFAULT_PAGE_NUMBER, PaginationUtils.DEFAULT_PAGE_SIZE, 0, 0);
    }

    public TransactionListResponseDTO(
            List<TransactionResponseDTO> items,
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal netCashFlow,
            int pageNumber,
            int pageSize,
            long totalRecords,
            int totalPages) {

        this.totalIncome = Objects.requireNonNull(totalIncome, "Tổng thu nhập không được để null");
        this.totalExpense = Objects.requireNonNull(totalExpense, "Tổng chi tiêu không được để null");
        this.netCashFlow = Objects.requireNonNull(netCashFlow, "Dòng tiền thuần không được để null");

        this.items = (items == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(items));
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
    }
    // endregion

    // region Getters
    public List<TransactionResponseDTO> getItems() {
        return this.items;
    }

    public int getItemCount() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public BigDecimal getTotalIncome() {
        return this.totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return this.totalExpense;
    }

    public BigDecimal getNetCashFlow() {
        return this.netCashFlow;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public long getTotalRecords() {
        return this.totalRecords;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public boolean hasNextPage() {
        return this.pageNumber < this.totalPages;
    }

    public boolean hasPreviousPage() {
        return this.pageNumber > 1;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionListResponseDTO that)) {
            return false;
        }
        return this.pageNumber == that.pageNumber
                && this.pageSize == that.pageSize
                && this.totalRecords == that.totalRecords
                && this.totalPages == that.totalPages
                && Objects.equals(this.items, that.items)
                && (this.totalIncome.compareTo(that.totalIncome) == 0)
                && (this.totalExpense.compareTo(that.totalExpense) == 0)
                && (this.netCashFlow.compareTo(that.netCashFlow) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.items,
                this.totalIncome.stripTrailingZeros(),
                this.totalExpense.stripTrailingZeros(),
                this.netCashFlow.stripTrailingZeros(),
                this.pageNumber,
                this.pageSize,
                this.totalRecords,
                this.totalPages
        );
    }

    @Override
    public String toString() {
        return "TransactionListResponseDTO{" +
                "itemsCount=" + this.items.size() +
                ", totalIncome=" + this.totalIncome +
                ", totalExpense=" + this.totalExpense +
                ", netCashFlow=" + this.netCashFlow +
                ", pageNumber=" + this.pageNumber +
                ", pageSize=" + this.pageSize +
                ", totalRecords=" + this.totalRecords +
                ", totalPages=" + this.totalPages +
                '}';
    }
    // endregion
}

