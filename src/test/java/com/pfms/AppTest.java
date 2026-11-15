package com.pfms;

import com.pfms.presentation.view.MainFrame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử App - Điểm khởi chạy và nạp chuỗi phụ thuộc ứng dụng")
class AppTest {

    @Test
    @DisplayName("Khởi tạo ứng dụng thành công với đường dẫn tệp lưu trữ tạm")
    void testCreateApplicationWithCustomPath(@TempDir Path tempDir) {
        Path storageFile = tempDir.resolve("test_pfms_data.json");
        MainFrame frame = App.createApplication(storageFile);

        assertNotNull(frame);
        assertNotNull(frame.getMainController());
        assertNotNull(frame.getSidebarPanel());
        assertNotNull(frame.getStatusBarPanel());

        frame.dispose();
    }

    @Test
    @DisplayName("Ném NullPointerException khi đường dẫn tệp bị null")
    void testCreateApplicationNullPath() {
        assertThrows(NullPointerException.class, () -> App.createApplication(null));
    }
}
