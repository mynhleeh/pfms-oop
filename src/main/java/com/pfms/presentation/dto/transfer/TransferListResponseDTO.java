package com.pfms.presentation.dto.transfer;

import com.pfms.common.util.PaginationUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DTO kết xuất danh sách phân trang các giao dịch chuyển tiền nội bộ thỏa mãn bộ lọc
 * kèm hai chỉ số tài chính tổng hợp: tổng tiền điều chuyển và tổng phụ phí phát sinh (UC-TRF-02).
 */
public final class TransferListResponseDTO {

    // region State
    private final List<TransferResponseDTO> items;
    private final BigDecimal totalTransferredAmount;
    private final BigDecimal totalFees;
    private final int pageNumber;
    private final int pageSize;
    private final long totalRecords;
    private final int totalPages;
    // endregion

    // region Constructors
    public TransferListResponseDTO() {
        this(
                Collections.emptyList(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                PaginationUtils.DEFAULT_PAGE_NUMBER,
                PaginationUtils.DEFAULT_PAGE_SIZE,
                0L,
                0
        );
    }

    public TransferListResponseDTO(
            List<TransferResponseDTO> items,
            BigDecimal totalTransferredAmount,
            BigDecimal totalFees,
            int pageNumber,
            int pageSize,
            long totalRecords,
            int totalPages) {

        this.totalTransferredAmount = Objects.requireNonNull(
                totalTransferredAmount, "Tổng số tiền chuyển không được để null");
        this.totalFees = Objects.requireNonNull(
                totalFees, "Tổng chi phí phát sinh không được để null");

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
    public List<TransferResponseDTO> getItems() {
        return this.items;
    }

    public int getItemCount() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public BigDecimal getTotalTransferredAmount() {
        return this.totalTransferredAmount;
    }

    public BigDecimal getTotalFees() {
        return this.totalFees;
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
        if (!(o instanceof TransferListResponseDTO that)) {
            return false;
        }
        return this.pageNumber == that.pageNumber
                && this.pageSize == that.pageSize
                && this.totalRecords == that.totalRecords
                && this.totalPages == that.totalPages
                && Objects.equals(this.items, that.items)
                && (this.totalTransferredAmount.compareTo(that.totalTransferredAmount) == 0)
                && (this.totalFees.compareTo(that.totalFees) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.items,
                this.totalTransferredAmount.stripTrailingZeros(),
                this.totalFees.stripTrailingZeros(),
                this.pageNumber,
                this.pageSize,
                this.totalRecords,
                this.totalPages);
    }

    @Override
    public String toString() {
        return "TransferListResponseDTO{" +
                "itemsCount=" + this.items.size() +
                ", totalTransferredAmount=" + this.totalTransferredAmount +
                ", totalFees=" + this.totalFees +
                ", pageNumber=" + this.pageNumber +
                ", pageSize=" + this.pageSize +
                ", totalRecords=" + this.totalRecords +
                ", totalPages=" + this.totalPages +
                '}';
    }
    // endregion
}

