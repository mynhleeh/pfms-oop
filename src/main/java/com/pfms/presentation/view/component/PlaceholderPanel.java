package com.pfms.presentation.view.component;

import com.pfms.presentation.view.common.ViewConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.util.Objects;

/**
 * Tấm nền giữ chỗ cho các màn hình phân hệ trước khi hoàn thiện giao diện chi tiết.
 */
public class PlaceholderPanel extends JPanel {

    private final String cardName;
    private final String title;

    public PlaceholderPanel(String cardName, String title) {
        this.cardName = Objects.requireNonNull(cardName, "Mã thẻ phân hệ không được để null");
        this.title = Objects.requireNonNull(title, "Tiêu đề phân hệ không được để null");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ViewConstants.COLOR_CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel(this.title);
        titleLabel.setFont(ViewConstants.FONT_PANEL_HEADER);
        titleLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Phân hệ: " + this.cardName + " - Đang trong lộ trình hoàn thiện");
        subtitleLabel.setFont(ViewConstants.FONT_BODY);
        subtitleLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(titleLabel);
        add(Box.createVerticalStrut(12));
        add(subtitleLabel);
    }

    public String getCardName() {
        return this.cardName;
    }

    public String getTitle() {
        return this.title;
    }
}
