package com.pfms.presentation.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử SwingSnapshotHelper - Tiện ích chụp ảnh màn hình Headless cho Swing")
class SwingSnapshotHelperTest {

    private Path capturedPath;

    @AfterEach
    void tearDown() throws IOException {
        if (capturedPath != null && Files.exists(capturedPath)) {
            Files.deleteIfExists(capturedPath);
        }
    }

    @Test
    @DisplayName("Chụp panel thành công sinh file ảnh PNG đúng kích thước 400x200")
    void testCaptureComponentGeneratesValidImage() throws IOException {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        JLabel label = new JLabel("Test Snapshot Label");
        label.setForeground(Color.WHITE);
        panel.add(label);

        capturedPath = SwingSnapshotHelper.capture(panel, 400, 200, "unit-test-snapshot.png");

        assertNotNull(capturedPath);
        assertTrue(Files.exists(capturedPath), "Tệp ảnh PNG chụp màn hình phải tồn tại");

        BufferedImage img = ImageIO.read(capturedPath.toFile());
        assertNotNull(img, "Dữ liệu tệp phải giải mã được thành BufferedImage");
        assertEquals(400, img.getWidth());
        assertEquals(200, img.getHeight());
    }

    @Test
    @DisplayName("Ném ngoại lệ khi component bị null hoặc kích thước không hợp lệ")
    void testInvalidParametersThrowException() {
        assertThrows(NullPointerException.class, () -> 
                SwingSnapshotHelper.capture(null, 400, 200, "test.png"));
        assertThrows(IllegalArgumentException.class, () -> 
                SwingSnapshotHelper.capture(new JPanel(), 0, 200, "test.png"));
        assertThrows(IllegalArgumentException.class, () -> 
                SwingSnapshotHelper.capture(new JPanel(), 400, -10, "test.png"));
        assertThrows(NullPointerException.class, () -> 
                SwingSnapshotHelper.capture(new JPanel(), 400, 200, null));
    }
}
