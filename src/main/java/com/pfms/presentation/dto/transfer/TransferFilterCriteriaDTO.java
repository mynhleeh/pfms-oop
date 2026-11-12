package com.pfms.presentation.dto.transfer;

import com.pfms.common.util.Preconditions;

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
        this(null, null, null, null, 1, 50);
    }

    public TransferFilterCriteriaDTO(
            LocalDate startDate,
            LocalDate endDate,
            UUID sourceWalletId,
            UUID destinationWalletId) {

        this(startDate, endDate, sourceWalletId, destinationWalletId, 1, 50);
    }

    public TransferFilterCriteriaDTO(
            LocalDate startDate,
            LocalDate endDate,
            UUID sourceWalletId,
            UUID destinationWalletId,
            int pageNumber,
            int pageSize) {

        if (startDate != null && endDate != null) {
            Preconditions.checkArgument(
                    !startDate.isAfter(endDate),
                    "Ngày bắt đầu không được sau ngày kết thúc: " + startDate + " > " + endDate);
        }

        Preconditions.checkArgument(
                pageNumber >= 1,
                "Chỉ số trang phải lớn hơn hoặc bằng 1: " + pageNumber);
        Preconditions.checkArgument(
                pageSize >= 10 && pageSize <= 100,
                "Kích thước trang phải nằm trong khoảng từ 10 đến 100 bản ghi: " + pageSize);

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
