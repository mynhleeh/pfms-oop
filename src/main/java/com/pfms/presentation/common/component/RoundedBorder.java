package com.pfms.presentation.common.component;

import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

/**
 * Đường viền bo góc khử răng cưa (Anti-aliased Rounded Border) cho các thành phần Swing.
 * Giúp giao diện mềm mại, loại bỏ cảm giác hình chữ nhật sắc cạnh cứng nhắc.
 */
public class RoundedBorder implements Border {

    private final int radius;
    private final Color color;
    private final Insets insets;

    public RoundedBorder(int radius, Color color) {
        this(radius, color, new Insets(radius / 4, radius / 2, radius / 4, radius / 2));
    }

    public RoundedBorder(int radius, Color color, Insets insets) {
        this.radius = Math.max(0, radius);
        this.color = color;
        this.insets = (insets != null) ? insets : new Insets(2, 6, 2, 6);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return this.insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (this.color == null || width <= 0 || height <= 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.color);
            g2.drawRoundRect(x, y, width - 1, height - 1, this.radius, this.radius);
        } finally {
            g2.dispose();
        }
    }
}
