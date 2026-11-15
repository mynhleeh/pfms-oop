package com.pfms.presentation.common.component;

import com.pfms.presentation.common.ViewConstants;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/**
 * Tấm nền Card bo góc (Rounded Card Panel) chuyên dụng cho giao diện người dùng PFMS.
 * Hỗ trợ bo tròn các góc mềm mại, vẽ nền và viền kim loại khử răng cưa mượt mà.
 */
public class RoundedCardPanel extends JPanel {

    private int arc;
    private Color borderColor;

    public RoundedCardPanel() {
        this(10, ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_BORDER);
    }

    public RoundedCardPanel(int arc) {
        this(arc, ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_BORDER);
    }

    public RoundedCardPanel(int arc, Color backgroundColor, Color borderColor) {
        super();
        this.arc = Math.max(0, arc);
        this.borderColor = borderColor;
        setBackground(backgroundColor != null ? backgroundColor : ViewConstants.COLOR_CARD_BG);
        setOpaque(false);
    }

    public RoundedCardPanel(LayoutManager layout, int arc, Color backgroundColor, Color borderColor) {
        super(layout);
        this.arc = Math.max(0, arc);
        this.borderColor = borderColor;
        setBackground(backgroundColor != null ? backgroundColor : ViewConstants.COLOR_CARD_BG);
        setOpaque(false);
    }

    public int getArc() {
        return this.arc;
    }

    public void setArc(int arc) {
        this.arc = Math.max(0, arc);
        repaint();
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();

            if (width > 0 && height > 0) {
                // Vẽ nền bo góc
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, width - 1, height - 1, this.arc, this.arc);

                // Vẽ đường viền bo góc
                if (this.borderColor != null) {
                    g2.setColor(this.borderColor);
                    g2.drawRoundRect(0, 0, width - 1, height - 1, this.arc, this.arc);
                }
            }
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
