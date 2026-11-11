package com.pfms.domain.model.common;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Đối tượng giá trị bất biến đại diện cho tên định danh của thực thể trong PFMS.
 * Đảm bảo độ dài từ 1 đến 50 ký tự sau khi cắt tỉa khoảng trắng theo BR-NAM-01
 * và cung cấp khóa chuẩn hóa để so sánh duy nhất không phân biệt hoa thường.
 */
public final class EntityName implements Comparable<EntityName>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // region Constants
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 50;
    private static final String DEFAULT_LABEL = "Tên";
    // endregion

    // region State
    private final String value;
    private final String normalizedKey;
    // endregion

    // region Constructors
    private EntityName(String value, String normalizedKey) {
        this.value = value;
        this.normalizedKey = normalizedKey;
    }
    // endregion

    // region Factories
    public static EntityName of(String rawName) {
        return of(rawName, DEFAULT_LABEL);
    }

    public static EntityName of(String rawName, String entityLabel) {
        String label = (entityLabel != null && !entityLabel.trim().isEmpty())
                ? entityLabel.trim()
                : DEFAULT_LABEL;

        if (rawName == null) {
            throw new BusinessException(ErrorCode.ERR_NAM_001, label + " không được để null.");
        }

        String trimmed = rawName.trim();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new BusinessException(
                    ErrorCode.ERR_NAM_001,
                    "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu.");
        }

        String key = trimmed.toLowerCase(Locale.ROOT);
        return new EntityName(trimmed, key);
    }
    // endregion

    // region Getters
    public String getValue() {
        return this.value;
    }

    public String getNormalizedKey() {
        return this.normalizedKey;
    }

    public int length() {
        return this.value.length();
    }
    // endregion

    // region Matching & Comparison
    public boolean matches(String otherRawName) {
        if (otherRawName == null) {
            return false;
        }
        String trimmedOther = otherRawName.trim();
        if (trimmedOther.isEmpty()) {
            return false;
        }
        return this.normalizedKey.equals(trimmedOther.toLowerCase(Locale.ROOT));
    }

    public boolean matches(EntityName other) {
        if (other == null) {
            return false;
        }
        return this.normalizedKey.equals(other.normalizedKey);
    }

    @Override
    public int compareTo(EntityName other) {
        Objects.requireNonNull(other, "Đối tượng so sánh không được để null");
        return this.value.compareTo(other.value);
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityName that = (EntityName) o;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return this.value;
    }
    // endregion
}
