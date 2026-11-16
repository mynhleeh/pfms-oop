package com.pfms.presentation.common.component;

import com.pfms.presentation.common.component.KpiPill.Variant;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử KpiPill Component")
class KpiPillTest {

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Nested
    @DisplayName("Khởi tạo và Giá trị mặc định")
    class ConstructorTests {

        @Test
        @DisplayName("Khởi tạo với đầy đủ tham số thiết lập đúng nhãn và variant")
        void testConstructorWithAllParams() {
            JavaFxTestHelper.runOnFxThread(() -> {
                KpiPill pill = new KpiPill("Tổng số dư", "10,000,000 VNĐ", Variant.GOLD);
                assertEquals("Tổng số dư", pill.getTitle());
                assertEquals("10,000,000 VNĐ", pill.getValue());
                assertEquals(Variant.GOLD, pill.getVariant());
                assertTrue(pill.getStyleClass().contains("kpi-pill"));
                assertTrue(pill.getStyleClass().contains("kpi-pill-gold"));
            });
        }

        @Test
        @DisplayName("Khởi tạo với 2 tham số mặc định variant NEUTRAL")
        void testConstructorWithTwoParams() {
            JavaFxTestHelper.runOnFxThread(() -> {
                KpiPill pill = new KpiPill("Đang dùng", "3 ví");
                assertEquals("Đang dùng", pill.getTitle());
                assertEquals("3 ví", pill.getValue());
                assertEquals(Variant.NEUTRAL, pill.getVariant());
                assertTrue(pill.getStyleClass().contains("kpi-pill"));
                assertTrue(pill.getStyleClass().contains("kpi-pill-neutral"));
            });
        }

        @Test
        @DisplayName("Ném ngoại lệ khi truyền tiêu đề null")
        void testConstructorNullTitle() {
            assertThrows(NullPointerException.class, () -> new KpiPill(null, "100 ₫"));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi truyền giá trị null")
        void testConstructorNullValue() {
            assertThrows(NullPointerException.class, () -> new KpiPill("Tiêu đề", null));
        }

        @Test
        @DisplayName("Ném ngoại lệ khi truyền variant null")
        void testConstructorNullVariant() {
            assertThrows(NullPointerException.class, () -> new KpiPill("Tiêu đề", "100 ₫", null));
        }
    }

    @Nested
    @DisplayName("Cập nhật Dữ liệu Động")
    class DynamicUpdateTests {

        @Test
        @DisplayName("Cập nhật giá trị hiển thị thành công")
        void testSetValue() {
            JavaFxTestHelper.runOnFxThread(() -> {
                KpiPill pill = new KpiPill("Tổng chi", "0 ₫", Variant.EXPENSE);
                pill.setValue("500,000 ₫");
                assertEquals("500,000 ₫", pill.getValue());
            });
        }

        @Test
        @DisplayName("Cập nhật tiêu đề thành công")
        void testSetTitle() {
            JavaFxTestHelper.runOnFxThread(() -> {
                KpiPill pill = new KpiPill("Thu", "1,000,000 ₫", Variant.INCOME);
                pill.setTitle("Tổng thu nhập");
                assertEquals("Tổng thu nhập", pill.getTitle());
            });
        }

        @Test
        @DisplayName("Thay đổi variant cập nhật đúng style class")
        void testSetVariant() {
            JavaFxTestHelper.runOnFxThread(() -> {
                KpiPill pill = new KpiPill("Chênh lệch", "0 ₫", Variant.NEUTRAL);
                pill.setVariant(Variant.GOLD);
                assertEquals(Variant.GOLD, pill.getVariant());
                assertTrue(pill.getStyleClass().contains("kpi-pill-gold"));
                assertTrue(!pill.getStyleClass().contains("kpi-pill-neutral"));
            });
        }
    }
}
