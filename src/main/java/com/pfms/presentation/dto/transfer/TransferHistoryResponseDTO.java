package com.pfms.presentation.dto.transfer;

import com.pfms.common.util.Preconditions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DTO kết xuất bảng tổng hợp lịch sử các lần chuyển tiền nội bộ thỏa mãn bộ lọc
 * kèm hai chỉ số tài chính tổng hợp: tổng tiền điều chuyển và tổng phụ phí phát sinh (UC-TRF-02).
 */
public final class TransferHistoryResponseDTO {

    // region State
    private final List<TransferResponseDTO> items;
    private final BigDecimal totalTransferredAmount;
    private final BigDecimal totalFees;
    private final int totalRecords;
    // endregion

    // region Constructors
    public TransferHistoryResponseDTO() {
        this(Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, 0);
    }

    public TransferHistoryResponseDTO(
            List<TransferResponseDTO> items,
            BigDecimal totalTransferredAmount,
            BigDecimal totalFees,
            int totalRecords) {

        Objects.requireNonNull(totalTransferredAmount, "Tổng số tiền chuyển không được để null");
        this.totalTransferredAmount = Preconditions.requireNonNegative(
                totalTransferredAmount, "Tổng số tiền chuyển không được âm: " + totalTransferredAmount);

        Objects.requireNonNull(totalFees, "Tổng chi phí phát sinh không được để null");
        this.totalFees = Preconditions.requireNonNegative(
                totalFees, "Tổng chi phí phát sinh không được âm: " + totalFees);

        Preconditions.checkArgument(
                totalRecords >= 0,
                "Tổng số bản ghi không được âm: " + totalRecords);

        this.items = (items == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(items));
        this.totalRecords = totalRecords;
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

    public int getTotalRecords() {
        return this.totalRecords;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransferHistoryResponseDTO that)) {
            return false;
        }
        return this.totalRecords == that.totalRecords
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
                this.totalRecords);
    }

    @Override
    public String toString() {
        return "TransferHistoryResponseDTO{" +
                "itemsCount=" + this.items.size() +
                ", totalTransferredAmount=" + this.totalTransferredAmount +
                ", totalFees=" + this.totalFees +
                ", totalRecords=" + this.totalRecords +
                '}';
    }
    // endregion
}
