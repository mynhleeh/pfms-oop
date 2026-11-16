package com.pfms.presentation.common.component;

import com.pfms.presentation.common.ViewConstants;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Objects;

/**
 * Thẻ thống kê KPI bo góc chuẩn (KPI Capsule Panel) cho tầng Presentation.
 * Hiển thị cấu trúc 2 tầng: Nhãn tiêu đề phụ (10px, Muted) và Giá trị số học nổi bật (18px, Bold).
 * Đóng gói sẵn padding và kích thước tiêu chuẩn nhằm chống hiện tượng vỡ layout hoặc cắt chữ.
 */
public class KpiCapsulePanel extends RoundedCardPanel {

    private static final Font FONT_KPI_TITLE = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font FONT_KPI_VALUE = new Font("Segoe UI", Font.BOLD, 18);
    private static final int DEFAULT_MIN_WIDTH = 160;
    private static final int DEFAULT_MIN_HEIGHT = 56;

    private final JLabel titleLabel = new JLabel();
    private final JLabel valueLabel = new JLabel();

    public KpiCapsulePanel(String title, String value) {
        this(title, value, ViewConstants.COLOR_PRIMARY);
    }

    public KpiCapsulePanel(String title, String value, Color valueColor) {
        super(new BorderLayout(0, 3), 10, ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_BORDER);

        this.titleLabel.setFont(FONT_KPI_TITLE);
        this.titleLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        this.titleLabel.setText(title != null ? title.toUpperCase() : "");

        this.valueLabel.setFont(FONT_KPI_VALUE);
        this.valueLabel.setForeground(valueColor != null ? valueColor : ViewConstants.COLOR_PRIMARY);
        this.valueLabel.setText(value != null ? value : "0");

        setBorder(new EmptyBorder(8, 16, 8, 16));
        setPreferredSize(new Dimension(DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT));

        add(this.titleLabel, BorderLayout.NORTH);
        add(this.valueLabel, BorderLayout.CENTER);
    }

    public String getTitle() {
        return this.titleLabel.getText();
    }

    public void setTitle(String title) {
        this.titleLabel.setText(title != null ? title.toUpperCase() : "");
    }

    public String getValue() {
        return this.valueLabel.getText();
    }

    public void setValue(String value) {
        this.valueLabel.setText(value != null ? value : "0");
    }

    public void setValue(String value, Color color) {
        this.valueLabel.setText(value != null ? value : "0");
        if (color != null) {
            this.valueLabel.setForeground(color);
        }
    }

    public Color getValueColor() {
        return this.valueLabel.getForeground();
    }

    public Color getTitleColor() {
        return this.titleLabel.getForeground();
    }
}
