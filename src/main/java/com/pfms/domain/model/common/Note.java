package com.pfms.domain.model.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import com.pfms.common.util.Preconditions;

/**
 * Đối tượng giá trị bất biến đại diện cho ghi chú diễn giải trong PFMS.
 * Đảm bảo chuẩn hóa chuỗi tối đa 255 ký tự sau khi cắt tỉa khoảng trắng
 * và tự động chuyển đổi giá trị null thành chuỗi rỗng.
 */
public final class Note implements Comparable<Note>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // region Constants
    private static final int MAX_LENGTH = 255;
    private static final String DEFAULT_LABEL = "Ghi chú";
    public static final Note EMPTY = new Note("");
    // endregion

    // region State
    private final String value;
    // endregion

    // region Constructors
    private Note(String value) {
        this.value = value;
    }
    // endregion

    // region Factories
    public static Note empty() {
        return EMPTY;
    }

    public static Note of(String rawNote) {
        return of(rawNote, DEFAULT_LABEL);
    }

    public static Note of(String rawNote, String label) {
        if (rawNote == null) {
            return EMPTY;
        }

        String trimmed = rawNote.trim();
        if (trimmed.isEmpty()) {
            return EMPTY;
        }

        String resolvedLabel = (label != null && !label.trim().isEmpty())
                ? label.trim()
                : DEFAULT_LABEL;
        Preconditions.checkArgument(trimmed.length() <= MAX_LENGTH,
                resolvedLabel + " không được vượt quá 255 ký tự.");

        return new Note(trimmed);
    }
    // endregion

    // region Getters
    public String getValue() {
        return this.value;
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public int length() {
        return this.value.length();
    }
    // endregion

    // region Comparison
    @Override
    public int compareTo(Note other) {
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
        Note note = (Note) o;
        return Objects.equals(this.value, note.value);
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

