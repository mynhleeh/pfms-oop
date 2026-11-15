package com.pfms.presentation.view.component;

import com.pfms.presentation.controller.MainController;
import com.pfms.presentation.view.common.ViewConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Thanh điều hướng bên trái (BorderLayout.WEST, chiều rộng 220px).
 * Hiển thị khối logo/tiêu đề và 7 nút bấm chuyển đổi giữa các phân hệ theo bảng màu Luxury Black & Gold.
 */
public class SidebarPanel extends JPanel {

    private final Consumer<String> onNavigateCallback;
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private final JLabel brandLabel = new JLabel(ViewConstants.APP_BRAND);
    private final JLabel subtitleLabel = new JLabel(ViewConstants.APP_SUBTITLE);
    private String activeCard = MainController.CARD_DASHBOARD;

    public SidebarPanel(Consumer<String> onNavigateCallback) {
        this.onNavigateCallback = Objects.requireNonNull(onNavigateCallback, "Callback điều hướng không được để null");

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(ViewConstants.SIDEBAR_WIDTH, ViewConstants.APP_HEIGHT));
        setBackground(ViewConstants.COLOR_SIDEBAR_BG);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ViewConstants.COLOR_BORDER));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createNavButtonsPanel(), BorderLayout.CENTER);

        updateActiveButtonStyles();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(ViewConstants.COLOR_SIDEBAR_BG);
        header.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        this.brandLabel.setFont(ViewConstants.FONT_TITLE);
        this.brandLabel.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.brandLabel.setIcon(NavIconFactory.createBrandIcon(22, ViewConstants.COLOR_PRIMARY_LIGHT));
        this.brandLabel.setIconTextGap(10);

        this.subtitleLabel.setFont(ViewConstants.FONT_SUBTITLE);
        this.subtitleLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        this.subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(this.brandLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(this.subtitleLabel);

        return header;
    }

    private JPanel createNavButtonsPanel() {
        JPanel navPanel = new JPanel(new GridLayout(7, 1, 0, 4));
        navPanel.setBackground(ViewConstants.COLOR_SIDEBAR_BG);
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 16, 12));

        addNavButton(navPanel, MainController.CARD_DASHBOARD, "Tổng quan");
        addNavButton(navPanel, MainController.CARD_WALLETS, "Ví tài chính");
        addNavButton(navPanel, MainController.CARD_TRANSACTIONS, "Thu & Chi");
        addNavButton(navPanel, MainController.CARD_TRANSFERS, "Chuyển tiền");
        addNavButton(navPanel, MainController.CARD_CATEGORIES, "Danh mục");
        addNavButton(navPanel, MainController.CARD_REPORTS, "Báo cáo");
        addNavButton(navPanel, MainController.CARD_DATA_MANAGEMENT, "Quản trị dữ liệu");

        return navPanel;
    }

    private void addNavButton(JPanel container, String cardName, String title) {
        JButton button = new JButton(title);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFont(ViewConstants.FONT_NAV);
        button.setBackground(ViewConstants.COLOR_SIDEBAR_BG);
        button.setForeground(ViewConstants.COLOR_TEXT_SIDEBAR);
        button.setIcon(NavIconFactory.createNavIcon(cardName, 18));
        button.setIconTextGap(12);
        button.setMargin(new java.awt.Insets(8, 14, 8, 14));
        button.putClientProperty("FlatLaf.style", "arc: 8; margin: 8,14,8,14");

        button.addActionListener(e -> {
            setActiveCard(cardName);
            this.onNavigateCallback.accept(cardName);
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!cardName.equals(activeCard)) {
                    button.setBackground(ViewConstants.COLOR_SIDEBAR_HOVER);
                    button.setForeground(ViewConstants.COLOR_GOLD_LIGHT);
                    button.putClientProperty("FlatLaf.style", "arc: 8; margin: 8,14,8,14; background: #111111; foreground: #F3E5AB");
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!cardName.equals(activeCard)) {
                    button.setBackground(ViewConstants.COLOR_SIDEBAR_BG);
                    button.setForeground(ViewConstants.COLOR_TEXT_SIDEBAR);
                    button.putClientProperty("FlatLaf.style", "arc: 8; margin: 8,14,8,14; background: #000000; foreground: #A1A1AA");
                }
            }
        });

        this.navButtons.put(cardName, button);
        container.add(button);
    }

    public void setActiveCard(String cardName) {
        if (cardName != null && this.navButtons.containsKey(cardName)) {
            this.activeCard = cardName;
            updateActiveButtonStyles();
        }
    }

    public String getActiveCard() {
        return this.activeCard;
    }

    public void triggerCardClick(String cardName) {
        JButton btn = this.navButtons.get(cardName);
        if (btn != null) {
            btn.doClick();
        }
    }

    public JButton getNavButton(String cardName) {
        return this.navButtons.get(cardName);
    }

    public JLabel getBrandLabel() {
        return this.brandLabel;
    }

    public JLabel getSubtitleLabel() {
        return this.subtitleLabel;
    }

    private void updateActiveButtonStyles() {
        for (Map.Entry<String, JButton> entry : this.navButtons.entrySet()) {
            boolean isActive = entry.getKey().equals(this.activeCard);
            JButton btn = entry.getValue();
            if (isActive) {
                btn.setBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
                btn.setForeground(ViewConstants.COLOR_TEXT_ACTIVE);
                btn.setFont(ViewConstants.FONT_NAV_ACTIVE);
                btn.putClientProperty("FlatLaf.style", "arc: 8; margin: 8,14,8,14; background: #14120B; foreground: #E5C07B; borderColor: rgba(212,175,55,64)");
            } else {
                btn.setBackground(ViewConstants.COLOR_SIDEBAR_BG);
                btn.setForeground(ViewConstants.COLOR_TEXT_SIDEBAR);
                btn.setFont(ViewConstants.FONT_NAV);
                btn.putClientProperty("FlatLaf.style", "arc: 8; margin: 8,14,8,14; background: #000000; foreground: #A1A1AA; borderColor: #000000");
            }
            btn.repaint();
        }
    }
}
