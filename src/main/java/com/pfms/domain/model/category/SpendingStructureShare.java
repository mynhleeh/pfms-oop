package com.pfms.domain.model.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Tỷ trọng chi tiêu trong hệ thống PFMS [0.00% - 100.00%].
 * Đo lường phần trăm đóng góp của nhóm nhu cầu hoặc danh mục trên tổng chi
 * tiêu.
 */
public class SpendingStructureShare implements Comparable<SpendingStructureShare> {

    // region State
    private final BigDecimal share;
    private final String unit;
    private final String scope;
    private static final String DEFAULT_UNIT = "%";
    public static final String SCOPE_NEED_GROUP = "NeedGroup";
    public static final String SCOPE_CATEGORY = "Category";
    // endregion

    // region Constructors
    public SpendingStructureShare(BigDecimal share, String scope) {
        if (share == null
                || share.compareTo(BigDecimal.ZERO) < 0
                || share.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException(ErrorCode.ERR_FIN_002,
                    "Tỷ trọng cơ cấu chi tiêu phải nằm trong đoạn [0.00, 100.00]");
        }
        if (scope == null) {
            throw new BusinessException(ErrorCode.ERR_FIN_002, "Phạm vi phân tích không được để null");
        }
        String trimmedScope = scope.trim();
        if (!trimmedScope.equalsIgnoreCase(SCOPE_NEED_GROUP) && !trimmedScope.equalsIgnoreCase(SCOPE_CATEGORY)) {
            throw new BusinessException(ErrorCode.ERR_FIN_002,
                    "Phạm vi phân tích không hợp lệ, chỉ chấp nhận 'NeedGroup' hoặc 'Category'");
        }

        this.share = share.setScale(2, RoundingMode.HALF_EVEN);
        this.scope = trimmedScope.equalsIgnoreCase(SCOPE_NEED_GROUP) ? SCOPE_NEED_GROUP : SCOPE_CATEGORY;
        this.unit = DEFAULT_UNIT;
    }
    // endregion

    // region Factories
    public static SpendingStructureShare of(BigDecimal itemAmount, BigDecimal totalAmount, String scope) {
        if (itemAmount == null || totalAmount == null) {
            throw new BusinessException(ErrorCode.ERR_FIN_002, "Số tiền tính tỷ trọng không được để null");
        }
        if (itemAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.ERR_FIN_002, "Số tiền phân tích không được âm");
        }
        if (totalAmount.signum() <= 0) {
            return new SpendingStructureShare(BigDecimal.ZERO, scope);
        }

        BigDecimal calculatedShare = itemAmount
                .divide(totalAmount, 4, RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_EVEN);

        return new SpendingStructureShare(calculatedShare, scope);
    }
    // endregion

    // region Queries
    public BigDecimal getShare() {
        return this.share;
    }

    public String getUnit() {
        return this.unit;
    }

    public String getScope() {
        return this.scope;
    }

    public boolean isZero() {
        return this.share.signum() == 0;
    }

    public String format() {
        return this.share.toPlainString() + this.unit;
    }
    // endregion

    // region Comparison
    @Override
    public int compareTo(SpendingStructureShare other) {
        Objects.requireNonNull(other, "Đối tượng SpendingStructureShare so sánh không được để null");
        return other.share.compareTo(this.share);
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SpendingStructureShare other)) {
            return false;
        }
        return this.share.compareTo(other.share) == 0
                && Objects.equals(this.scope, other.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.share.stripTrailingZeros(), this.scope);
    }

    @Override
    public String toString() {
        return String.format("SpendingStructureShare[share=%s, scope='%s']", this.share.toPlainString(), this.scope);
    }
    // endregion
}
