package com.pfms.presentation.common;

import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử AbstractBasePane - Lớp cơ sở cho các bảng điều khiển phân hệ")
class AbstractBasePaneTest {

    private static class TestPane extends AbstractBasePane {
        // Lớp con cụ thể phục vụ kiểm thử
    }

    private TestPane pane;

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @BeforeEach
    void setUp() {
        JavaFxTestHelper.runOnFxThread(() -> pane = new TestPane());
    }

    @Test
    @DisplayName("Khởi tạo cấu trúc giao diện nền chuẩn và footer sẵn sàng")
    void testInitialLayout() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertEquals("-fx-background-color: -color-bg-base;", pane.getStyle());
            assertNotNull(pane.getBottom());
            assertEquals("Sẵn sàng", pane.getSummaryText());
        });
    }

    @Test
    @DisplayName("Điều phối thông báo qua các Consumer khi được đăng ký")
    void testNotificationConsumers() {
        AtomicReference<String> msgRef = new AtomicReference<>();
        AtomicReference<String> warnRef = new AtomicReference<>();
        AtomicReference<String> errRef = new AtomicReference<>();

        pane.setMessageConsumer(msgRef::set);
        pane.setWarningConsumer(warnRef::set);
        pane.setErrorConsumer(errRef::set);

        JavaFxTestHelper.runOnFxThread(() -> {
            pane.showMessage("Thành công");
            pane.showWarning("Cảnh báo");
            pane.showError("Có lỗi");
        });

        assertEquals("Thành công", msgRef.get());
        assertEquals("Cảnh báo", warnRef.get());
        assertEquals("Có lỗi", errRef.get());
    }

    @Test
    @DisplayName("runOnFx thực thi hành động ngay lập tức nếu đã ở trên luồng FX Thread")
    void testRunOnFxExecution() {
        AtomicBoolean executed = new AtomicBoolean(false);
        JavaFxTestHelper.runOnFxThread(() -> {
            pane.runOnFx(() -> executed.set(true));
            assertTrue(executed.get());
        });
    }
}
