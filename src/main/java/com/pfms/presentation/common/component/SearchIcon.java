package com.pfms.presentation.common.component;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Biểu tượng tìm kiếm vector (Search Icon) vẽ bằng Graphics2D khử răng cưa.
 * Đảm bảo hiển thị sắc nét trên mọi hệ điều hành và độ phân giải màn hình, không bị lỗi font như emoji.
 */
public class SearchIcon implements Icon {

    private final int size;
    private final Color color;

    public SearchIcon() {
        this(14, new Color(161, 161, 170));
    }

    public SearchIcon(int size, Color color) {
        this.size = Math.max(10, size);
        this.color = (color != null) ? color : new Color(161, 161, 170);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(this.color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int diameter = (int) (this.size * 0.65f);
            int circleX = x + 1;
            int circleY = y + 1;
            g2.drawOval(circleX, circleY, diameter, diameter);

            int handleStartX = circleX + (int) (diameter * 0.82f);
            int handleStartY = circleY + (int) (diameter * 0.82f);
            int handleEndX = x + this.size - 2;
            int handleEndY = y + this.size - 2;
            g2.drawLine(handleStartX, handleStartY, handleEndX, handleEndY);
        } finally {
            g2.dispose();
        }
    }

    @Override
    public int getIconWidth() {
        return this.size;
    }

    @Override
    public int getIconHeight() {
        return this.size;
    }
}
