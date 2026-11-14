package com.pfms.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử lớp tiện ích PaginationUtils (com.pfms.common.util)")
class PaginationUtilsTest {

    private List<Integer> buildSequentialItems(int totalCount) {
        return IntStream.rangeClosed(1, totalCount).boxed().toList();
    }

    @Nested
    @DisplayName("Thiết kế Utility Class (Effective Java Item 4)")
    class UtilityClassDesignTests {

        @Test
        @DisplayName("Hàm khởi tạo private phải ném AssertionError khi bị gọi qua Reflection")
        void shouldPreventInstantiationViaReflection() throws NoSuchMethodException {
            Constructor<PaginationUtils> constructor = PaginationUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
            assertInstanceOf(AssertionError.class, exception.getCause());
            assertEquals("Utility class cannot be instantiated", exception.getCause().getMessage());
        }
    }

    @Nested
    @DisplayName("Nhóm 1: Happy Path - Cắt trang chính xác và bất biến")
    class HappyPathTests {

        @Test
        @DisplayName("Cắt đúng trang đầu, trang giữa và trang cuối lẻ trên danh sách 25 phần tử")
        void shouldSliceFirstMiddleAndPartialLastPage() {
            List<Integer> sourceItems = buildSequentialItems(25);

            List<Integer> pageOne = PaginationUtils.slice(sourceItems, 1, 10);
            List<Integer> pageTwo = PaginationUtils.slice(sourceItems, 2, 10);
            List<Integer> pageThree = PaginationUtils.slice(sourceItems, 3, 10);

            assertEquals(IntStream.rangeClosed(1, 10).boxed().toList(), pageOne);
            assertEquals(IntStream.rangeClosed(11, 20).boxed().toList(), pageTwo);
            assertEquals(IntStream.rangeClosed(21, 25).boxed().toList(), pageThree);
        }

        @Test
        @DisplayName("Danh sách trả về từ slice phải bất biến và độc lập với danh sách gốc")
        void shouldReturnUnmodifiableDetachedSnapshot() {
            List<String> mutableSource = new ArrayList<>(buildSequentialItems(12).stream().map(String::valueOf).toList());

            List<String> sliced = PaginationUtils.slice(mutableSource, 1, 10);
            mutableSource.set(0, "999");

            assertEquals("1", sliced.get(0));
            assertThrows(UnsupportedOperationException.class, () -> sliced.add("13"));
        }
    }

    @Nested
    @DisplayName("Nhóm 2: Boundary & Precision - Giá trị biên và tràn số học chỉ số")
    class BoundaryAndOverflowTests {

        @ParameterizedTest(name = "Chấp nhận kích thước trang hợp lệ: {0}")
        @ValueSource(ints = {10, 50, 100})
        @DisplayName("Chấp nhận pageSize tại cận dưới (10), mặc định (50) và cận trên (100)")
        void shouldAcceptBoundaryPageSizes(int validPageSize) {
            assertDoesNotThrow(() -> PaginationUtils.validate(1, validPageSize));
        }

        @Test
        @DisplayName("Trả về tất cả 100 phần tử khi kích thước trang bằng đúng cận trên 100")
        void shouldReturnAllItemsWhenPageSizeAtUpperBound() {
            List<Integer> sourceItems = buildSequentialItems(100);

            List<Integer> sliced = PaginationUtils.slice(sourceItems, 1, PaginationUtils.MAX_PAGE_SIZE);

            assertEquals(100, sliced.size());
            assertEquals(1, sliced.get(0));
            assertEquals(100, sliced.get(99));
        }

        @Test
        @DisplayName("Trả về danh sách rỗng khi nguồn rỗng hoặc trang vượt quá tổng số phần tử")
        void shouldReturnEmptyListWhenSourceEmptyOrPageOutOfRange() {
            assertTrue(PaginationUtils.slice(List.of(), 1, 10).isEmpty());
            assertTrue(PaginationUtils.slice(buildSequentialItems(15), 3, 10).isEmpty());
        }

        @Test
        @DisplayName("Không bị tràn số nguyên khi pageNumber rất lớn (Integer.MAX_VALUE)")
        void shouldPreventIntegerOverflowForExtremePageNumber() {
            List<Integer> sourceItems = buildSequentialItems(12);

            List<Integer> sliced = PaginationUtils.slice(sourceItems, Integer.MAX_VALUE, 100);

            assertTrue(sliced.isEmpty());
        }
    }

    @Nested
    @DisplayName("Nhóm 3 & 5: Validation, Null & Robustness")
    class ValidationAndNullTests {

        @ParameterizedTest(name = "Chỉ số trang không hợp lệ: {0}")
        @ValueSource(ints = {Integer.MIN_VALUE, -1, 0})
        @DisplayName("Ném IllegalArgumentException khi pageNumber nhỏ hơn 1")
        void shouldRejectNonPositivePageNumber(int invalidPageNumber) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> PaginationUtils.validate(invalidPageNumber, 10)
            );
            assertTrue(exception.getMessage().contains("Chỉ số trang phải lớn hơn hoặc bằng 1"));
        }

        @ParameterizedTest(name = "Kích thước trang ngoài đoạn [10, 100]: {0}")
        @ValueSource(ints = {-10, 0, 9, 101, 500})
        @DisplayName("Ném IllegalArgumentException khi pageSize ngoài khoảng [10, 100]")
        void shouldRejectOutOfRangePageSize(int invalidPageSize) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> PaginationUtils.validate(1, invalidPageSize)
            );
            assertTrue(exception.getMessage().contains("Kích thước trang phải nằm trong khoảng từ 10 đến 100"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi danh sách đầu vào là null")
        void shouldRejectNullSourceList() {
            assertThrows(NullPointerException.class, () -> PaginationUtils.slice(null, 1, 10));
        }
    }
}