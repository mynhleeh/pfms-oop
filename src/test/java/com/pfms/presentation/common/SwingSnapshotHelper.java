package com.pfms.presentation.common;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Tiện ích hỗ trợ chụp ảnh màn hình các thành phần Swing trong môi trường Headless.
 * Cho phép xuất hình ảnh kết xuất thực tế của JComponent/JPanel ra định dạng PNG
 * với bộ lọc khử răng cưa và subpixel rendering chất lượng cao phục vụ đối soát giao diện.
 */
public final class SwingSnapshotHelper {

    public static final Path DEFAULT_SNAPSHOT_DIR = Paths.get("target", "snapshots");

    private SwingSnapshotHelper() {
        // Chặn khởi tạo lớp tiện ích
    }

    /**
     * Dựng hình một JComponent và lưu thành file ảnh PNG với kích thước chỉ định.
     *
     * @param component Thành phần giao diện cần chụp (không được null)
     * @param width     Chiều rộng ảnh (pixel, > 0)
     * @param height    Chiều cao ảnh (pixel, > 0)
     * @param filename  Tên tệp ảnh xuất ra (ví dụ: "wallet-panel.png")
     * @return Đường dẫn Path dẫn tới tệp ảnh đã xuất
     * @throws IOException khi gặp lỗi ghi file I/O
     */
    public static Path capture(JComponent component, int width, int height, String filename) throws IOException {
        Objects.requireNonNull(component, "Component giao diện không được để null");
        Objects.requireNonNull(filename, "Tên file ảnh xuất không được để null");
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Kích thước width và height phải lớn hơn 0");
        }

        // Gắn vào một frame tạm nhằm kích hoạt addNotify() và kết nối cấu trúc peer cho JScrollPane/JTableHeader
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setUndecorated(true);
        frame.setContentPane(component);
        frame.setSize(width, height);
        component.setSize(width, height);
        component.setPreferredSize(new Dimension(width, height));
        frame.pack();
        frame.setSize(width, height);
        frame.validate();
        layoutHierarchy(component);

        // Khởi tạo bộ đệm ARGB để hỗ trợ độ trong suốt và bo tròn khử răng cưa
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            component.printAll(g2);
        } finally {
            g2.dispose();
            frame.dispose();
        }

        Path outputDir = DEFAULT_SNAPSHOT_DIR;
        Files.createDirectories(outputDir);
        Path outputPath = outputDir.resolve(filename);

        ImageIO.write(image, "PNG", outputPath.toFile());
        return outputPath;
    }

    /**
     * Đệ quy kích hoạt doLayout trên tất cả các thành phần con trong container.
     */
    private static void layoutHierarchy(Component comp) {
        comp.doLayout();
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                layoutHierarchy(child);
            }
        }
    }
}
