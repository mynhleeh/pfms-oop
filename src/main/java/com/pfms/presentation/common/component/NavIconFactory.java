package com.pfms.presentation.common.component;

import com.pfms.presentation.common.MainController;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;

/**
 * Nhà máy khởi tạo biểu tượng vector (Vector Icon Factory) thuần Java Swing.
 * Vẽ các đường nét biểu tượng bằng Graphics2D khử răng cưa, tự động đổi màu theo trạng thái của thành phần.
 */
public final class NavIconFactory {

    public enum IconType {
        DASHBOARD,
        WALLETS,
        TRANSACTIONS,
        TRANSFERS,
        CATEGORIES,
        REPORTS,
        DATA_MANAGEMENT,
        BRAND
    }

    private NavIconFactory() {
        throw new UnsupportedOperationException("Lớp tiện ích không thể khởi tạo trực tiếp");
    }

    public static Icon createNavIcon(String cardName, int size) {
        IconType type = switch (cardName) {
            case MainController.CARD_DASHBOARD -> IconType.DASHBOARD;
            case MainController.CARD_WALLETS -> IconType.WALLETS;
            case MainController.CARD_TRANSACTIONS -> IconType.TRANSACTIONS;
            case MainController.CARD_TRANSFERS -> IconType.TRANSFERS;
            case MainController.CARD_CATEGORIES -> IconType.CATEGORIES;
            case MainController.CARD_REPORTS -> IconType.REPORTS;
            case MainController.CARD_DATA_MANAGEMENT -> IconType.DATA_MANAGEMENT;
            default -> IconType.DASHBOARD;
        };
        return new VectorNavIcon(type, size, null);
    }

    public static Icon createBrandIcon(int size, Color fixedColor) {
        return new VectorNavIcon(IconType.BRAND, size, fixedColor);
    }

    public static Icon createIcon(IconType type, int size, Color fixedColor) {
        return new VectorNavIcon(type, size, fixedColor);
    }

    /**
     * Đối tượng Icon vector tự vẽ đồ họa 2D.
     */
    public static class VectorNavIcon implements Icon {

        private final IconType type;
        private final int size;
        private final Color fixedColor;

        public VectorNavIcon(IconType type, int size, Color fixedColor) {
            this.type = Objects.requireNonNull(type, "IconType không được để null");
            this.size = Math.max(12, size);
            this.fixedColor = fixedColor;
        }

        public IconType getType() {
            return this.type;
        }

        @Override
        public int getIconWidth() {
            return this.size;
        }

        @Override
        public int getIconHeight() {
            return this.size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                Color strokeColor = (this.fixedColor != null)
                        ? this.fixedColor
                        : ((c != null) ? c.getForeground() : Color.WHITE);
                g2.setColor(strokeColor);

                // Dịch chuyển đến tọa độ (x, y) và scale tương đối theo 24x24 viewBox chuẩn
                g2.translate(x, y);
                double scale = (double) this.size / 24.0;
                g2.scale(scale, scale);

                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                switch (this.type) {
                    case DASHBOARD -> paintDashboard(g2);
                    case WALLETS -> paintWallets(g2);
                    case TRANSACTIONS -> paintTransactions(g2);
                    case TRANSFERS -> paintTransfers(g2);
                    case CATEGORIES -> paintCategories(g2);
                    case REPORTS -> paintReports(g2);
                    case DATA_MANAGEMENT -> paintDataManagement(g2);
                    case BRAND -> paintBrand(g2);
                }
            } finally {
                g2.dispose();
            }
        }

        private void paintDashboard(Graphics2D g2) {
            // 4 ô bo góc dạng grid (như SVG: rect 3,3 w7 h9 rx1.5; rect 14,3 w7 h5; rect 14,12 w7 h9; rect 3,16 w7 h5)
            g2.draw(new RoundRectangle2D.Float(3f, 3f, 7f, 8.5f, 3f, 3f));
            g2.draw(new RoundRectangle2D.Float(14f, 3f, 7f, 5.5f, 3f, 3f));
            g2.draw(new RoundRectangle2D.Float(14f, 12f, 7f, 9f, 3f, 3f));
            g2.draw(new RoundRectangle2D.Float(3f, 15f, 7f, 6f, 3f, 3f));
        }

        private void paintWallets(Graphics2D g2) {
            // Khung ví chữ nhật bo góc và dải thẻ: rect 2,5 w20 h14 rx3, line 2,10 -> 22,10
            g2.draw(new RoundRectangle2D.Float(2f, 5f, 20f, 14f, 4f, 4f));
            g2.drawLine(2, 10, 22, 10);
            g2.draw(new RoundRectangle2D.Float(15f, 13f, 3.5f, 2.5f, 1f, 1f));
        }

        private void paintTransactions(Graphics2D g2) {
            // Biểu tượng tiền tệ / thu chi: đường thẳng giữa và 2 vòng cong hoặc dấu +/-
            g2.drawLine(12, 2, 12, 22);
            Path2D.Float path = new Path2D.Float();
            path.moveTo(17, 6);
            path.curveTo(17, 3, 9, 3, 9, 7);
            path.curveTo(9, 11, 16, 11, 16, 16);
            path.curveTo(16, 21, 7, 21, 7, 18);
            g2.draw(path);
        }

        private void paintTransfers(Graphics2D g2) {
            // 2 mũi tên chuyển tiền hai chiều
            // Mũi tên trên (qua phải)
            g2.drawLine(3, 7, 20, 7);
            g2.drawLine(16, 3, 20, 7);
            g2.drawLine(16, 11, 20, 7);
            // Mũi tên dưới (qua trái)
            g2.drawLine(21, 17, 4, 17);
            g2.drawLine(8, 13, 4, 17);
            g2.drawLine(8, 21, 4, 17);
        }

        private void paintCategories(Graphics2D g2) {
            // Thẻ tag danh mục có lỗ tròn
            Path2D.Float tag = new Path2D.Float();
            tag.moveTo(10, 2);
            tag.lineTo(2, 10);
            tag.lineTo(13, 21);
            tag.lineTo(21, 13);
            tag.lineTo(21, 2);
            tag.closePath();
            g2.draw(tag);
            g2.drawOval(15, 6, 2, 2);
        }

        private void paintReports(Graphics2D g2) {
            // Biểu đồ cột 3 thanh tăng dần
            g2.drawLine(2, 21, 22, 21); // trục hoành
            g2.draw(new RoundRectangle2D.Float(4f, 13f, 3.5f, 8f, 1.5f, 1.5f));
            g2.draw(new RoundRectangle2D.Float(10.5f, 8f, 3.5f, 13f, 1.5f, 1.5f));
            g2.draw(new RoundRectangle2D.Float(17f, 4f, 3.5f, 17f, 1.5f, 1.5f));
        }

        private void paintDataManagement(Graphics2D g2) {
            // Biểu tượng khiên bảo mật an toàn dữ liệu
            Path2D.Float shield = new Path2D.Float();
            shield.moveTo(12, 2);
            shield.lineTo(20, 5);
            shield.curveTo(20, 14, 16, 19, 12, 22);
            shield.curveTo(8, 19, 4, 14, 4, 5);
            shield.closePath();
            g2.draw(shield);
            // vạch chữ V nhỏ bên trong khiên
            g2.drawLine(9, 11, 11, 14);
            g2.drawLine(11, 14, 15, 9);
        }

        private void paintBrand(Graphics2D g2) {
            // Khung logo hình thoi kim cương PFMS
            Path2D.Float diamond = new Path2D.Float();
            diamond.moveTo(12, 2);
            diamond.lineTo(22, 12);
            diamond.lineTo(12, 22);
            diamond.lineTo(2, 12);
            diamond.closePath();
            g2.draw(diamond);
            g2.drawOval(10, 10, 4, 4);
        }
    }
}
