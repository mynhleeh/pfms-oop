package com.pfms.presentation.report.dialog;

import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử ExportReportDialog - Hộp thoại xem và xuất nội dung báo cáo văn bản JavaFX")
class ExportReportDialogTest {

    private ExportReportDialog dialog;
    private static final String SAMPLE_REPORT = """
            +------------------------------------------------+
            | BÁO CÁO DÒNG TIỀN 12 THÁNG LIÊN TỤC             |
            +---------+--------------+--------------+--------+
            | Tháng   | Thu nhập     | Chi tiêu     | Net    |
            | 2026-10 | 20,000,000   | 12,000,000   | 8,000  |
            +---------+--------------+--------------+--------+
            """;

    @BeforeAll
    static void initJfx() {
        JavaFxTestHelper.initPlatform();
    }

    @BeforeEach
    void setUp() {
        JavaFxTestHelper.runOnFxThread(() -> {
            dialog = new ExportReportDialog(null, "Xuất Báo cáo Dòng tiền", SAMPLE_REPORT);
            dialog.setMessageConsumer(msg -> {});
        });
    }

    @Test
    @DisplayName("Khởi tạo hộp thoại nạp đúng chuỗi nội dung báo cáo văn bản monospaced")
    void testInitialDialogContent() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertEquals(SAMPLE_REPORT, dialog.getReportText());
            assertNotNull(dialog.getTitle());
            assertTrue(dialog.getTitle().contains("Xuất Báo cáo Dòng tiền"));
        });
    }

    @Test
    @DisplayName("Kích hoạt hành động sao chép clipboard gọi thông báo thành công")
    void testCopyActionTriggersMessage() {
        AtomicBoolean copied = new AtomicBoolean(false);
        dialog.setMessageConsumer(msg -> copied.set(true));

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.triggerCopy();
        });
        assertTrue(copied.get());
    }

    @Test
    @DisplayName("Ném NullPointerException khi chuỗi nội dung báo cáo bị null")
    void testNullContentThrowsException() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertThrows(NullPointerException.class, () -> new ExportReportDialog(null, "Tiêu đề", null));
        });
    }
}
