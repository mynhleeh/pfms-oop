package com.pfms.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Tiện ích chuẩn hóa thông số phân trang và cắt lát danh sách theo trang
 * ở mức an toàn chỉ số (tránh tràn số nguyên và bảo vệ bất biến kết quả).
 */
public final class PaginationUtils {

    // region Constants
    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final int MIN_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    // endregion

    // region Constructors
    private PaginationUtils() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    public static void validate(int pageNumber, int pageSize) {
        Preconditions.checkArgument(
                pageNumber >= DEFAULT_PAGE_NUMBER,
                "Chỉ số trang phải lớn hơn hoặc bằng 1: " + pageNumber);
        validatePageSize(pageSize);
    }

    public static int calculateTotalPages(int totalRecords, int pageSize) {
        Preconditions.checkArgument(
                totalRecords >= 0,
                "Tổng số bản ghi không được âm: " + totalRecords);
        validatePageSize(pageSize);
        if (totalRecords == 0) {
            return 0;
        }
        return (int) (((long) totalRecords + pageSize - 1) / pageSize);
    }

    public static <T> List<T> slice(List<T> sourceItems, int pageNumber, int pageSize) {
        Objects.requireNonNull(sourceItems, "Danh sách cần phân trang không được để null");
        validate(pageNumber, pageSize);

        long startOffset = (long) (pageNumber - 1) * pageSize;
        if (startOffset >= sourceItems.size()) {
            return Collections.emptyList();
        }
        int fromIndex = (int) startOffset;
        int toIndex = (int) Math.min(startOffset + pageSize, sourceItems.size());
        return Collections.unmodifiableList(new ArrayList<>(sourceItems.subList(fromIndex, toIndex)));
    }
    // endregion

    // region Helpers
    private static void validatePageSize(int pageSize) {
        Preconditions.checkArgument(
                pageSize >= MIN_PAGE_SIZE && pageSize <= MAX_PAGE_SIZE,
                "Kích thước trang phải nằm trong khoảng từ 10 đến 100 bản ghi: " + pageSize);
    }
    // endregion
}

