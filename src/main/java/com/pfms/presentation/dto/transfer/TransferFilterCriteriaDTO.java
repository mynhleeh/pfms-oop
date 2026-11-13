package com.pfms.presentation.dto.transfer;

import com.pfms.common.util.PaginationUtils;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Tiêu chí lọc và phân trang khi tra cứu lịch sử chuyển tiền
 */
public final class TransferFilterCriteriaDTO {

    // region State
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final UUID sourceWalletId;
    private final UUID destinationWalletId;
    private final int pageNumber;
    private final int pageSize;
    // endregion

    // region Constructors
    public TransferFilterCriteriaDTO() {
        this(null, null, null, null, PaginationUtils.DEFAULT_PAGE_NUMBER, PaginationUtils.DEFAULT_PAGE_SIZE);
    }

    public TransferFilterCriteriaDTO(
            LocalDate startDate,
            LocalDate endDate,
            UUID sourceWalletId,
            UUID destinationWalletId) {

        this(startDate, endDate, sourceWalletId, destinationWalletId,
                PaginationUtils.DEFAULT_PAGE_NUMBER, PaginationUtils.DEFAULT_PAGE_SIZE);
    }

    public TransferFilterCriteriaDTO(
            LocalDate startDate,
            LocalDate endDate,
            UUID sourceWalletId,
            UUID destinationWalletId,
            int pageNumber,
            int pageSize) {

        this.startDate = startDate;
        this.endDate = endDate;
        this.sourceWalletId = sourceWalletId;
        this.destinationWalletId = destinationWalletId;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
    // endregion

    // region Getters
    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public UUID getSourceWalletId() {
        return this.sourceWalletId;
    }

    public UUID getDestinationWalletId() {
        return this.destinationWalletId;
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

    public boolean hasSourceWalletFilter() {
        return this.sourceWalletId != null;
    }

    public boolean hasDestinationWalletFilter() {
        return this.destinationWalletId != null;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransferFilterCriteriaDTO that)) {
            return false;
        }
        return this.pageNumber == that.pageNumber
                && this.pageSize == that.pageSize
                && Objects.equals(this.startDate, that.startDate)
                && Objects.equals(this.endDate, that.endDate)
                && Objects.equals(this.sourceWalletId, that.sourceWalletId)
                && Objects.equals(this.destinationWalletId, that.destinationWalletId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.startDate,
                this.endDate,
                this.sourceWalletId,
                this.destinationWalletId,
                this.pageNumber,
                this.pageSize);
    }

    @Override
    public String toString() {
        return "TransferFilterCriteriaDTO{" +
                "startDate=" + this.startDate +
                ", endDate=" + this.endDate +
                ", sourceWalletId=" + this.sourceWalletId +
                ", destinationWalletId=" + this.destinationWalletId +
                ", pageNumber=" + this.pageNumber +
                ", pageSize=" + this.pageSize +
                '}';
    }
    // endregion
}
