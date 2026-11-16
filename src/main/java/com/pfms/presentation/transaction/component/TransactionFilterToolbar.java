package com.pfms.presentation.transaction.component;

import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.common.InputValidator;
import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.common.component.RoundedBorder;
import com.pfms.presentation.common.component.RoundedCardPanel;
import com.pfms.presentation.common.component.SearchIcon;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Thanh công cụ lọc và tìm kiếm giao dịch thu chi (UC-TRN-03).
 * Thiết kế 2 tầng cân đối:
 * - Hàng 1: Thao tác thẻ ngày nhanh & Ô tìm kiếm từ khóa.
 * - Hàng 2: Bộ lọc chuyên sâu trải đều (Phân loại, Ví tài chính, Nhóm nhu cầu, Danh mục, Khoảng ngày).
 */
public class TransactionFilterToolbar extends JPanel {

    private static final long serialVersionUID = 1L;

    // region Fields
    private final JButton addButton = createActionButton("+ Ghi nhận giao dịch mới", ViewConstants.COLOR_PRIMARY, Color.BLACK, true);
    private final JButton resetButton = createActionButton("Đặt lại bộ lọc", new Color(23, 23, 23), ViewConstants.COLOR_TEXT_MAIN, false);
    private final JButton refreshButton = createActionButton("Làm mới", new Color(23, 23, 23), ViewConstants.COLOR_TEXT_MUTED, false);

    private final JComboBox<String> typeFilterBox = new JComboBox<>(new String[]{"Tất cả", "INCOME", "EXPENSE"});
    private final JComboBox<Object> walletFilterBox = new JComboBox<>();
    private final JComboBox<Object> groupFilterBox = new JComboBox<>();
    private final JComboBox<Object> categoryFilterBox = new JComboBox<>();
    private final JTextField startDateField = new JTextField(8);
    private final JTextField endDateField = new JTextField(8);
    private final JTextField keywordField = new JTextField(16);
    private final JButton filterButton = createActionButton("Lọc", new Color(23, 23, 23), ViewConstants.COLOR_PRIMARY_LIGHT, false);

    private final JButton btnQuickAll = createTagButton("Tất cả", "ALL");
    private final JButton btnQuickMonth = createTagButton("Tháng này", "MONTH");
    private final JButton btnQuick7Days = createTagButton("7 ngày qua", "7DAYS");
    private String currentQuickTag = "ALL";

    private final List<CategoryResponseDTO> allCategories = new ArrayList<>();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Runnable onFilterAction;
    private Runnable onResetAction;
    private boolean isUpdating = false;
    // endregion

    // region Constructors
    public TransactionFilterToolbar(Runnable onFilterAction, Runnable onResetAction) {
        this.onFilterAction = onFilterAction;
        this.onResetAction = onResetAction;
        initUI();
    }

    public TransactionFilterToolbar(Runnable onFilterAction) {
        this(onFilterAction, null);
    }
    // endregion

    // region UI
    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ViewConstants.COLOR_CARD_BG);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(14, ViewConstants.COLOR_BORDER, new Insets(14, 18, 14, 18)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Styling type box (clean text, no broken emoji tofu)
        this.typeFilterBox.setFont(ViewConstants.FONT_BODY);
        this.typeFilterBox.setBackground(new Color(13, 13, 13));
        this.typeFilterBox.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.typeFilterBox.setPreferredSize(new Dimension(135, 34));
        this.typeFilterBox.putClientProperty("Component.arc", 8);
        this.typeFilterBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if ("INCOME".equals(value)) {
                    setText("Thu nhập");
                    if (!isSelected) setForeground(ViewConstants.COLOR_INCOME);
                } else if ("EXPENSE".equals(value)) {
                    setText("Chi tiêu");
                    if (!isSelected) setForeground(ViewConstants.COLOR_EXPENSE);
                } else if ("Tất cả loại".equals(value) || "Tất cả".equals(value)) {
                    setText("Tất cả loại");
                    if (!isSelected) setForeground(ViewConstants.COLOR_TEXT_MAIN);
                }
                return this;
            }
        });
        this.typeFilterBox.addActionListener(e -> {
            onTypeFilterChanged();
            triggerFilter();
        });

        // Styling wallet box (clean text, no broken emoji tofu)
        this.walletFilterBox.setFont(ViewConstants.FONT_BODY);
        this.walletFilterBox.setBackground(new Color(13, 13, 13));
        this.walletFilterBox.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.walletFilterBox.setPreferredSize(new Dimension(140, 34));
        this.walletFilterBox.putClientProperty("Component.arc", 8);
        this.walletFilterBox.addItem("Tất cả ví");
        this.walletFilterBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof WalletResponseDTO w) {
                    setText(w.getName());
                } else if (value != null) {
                    setText(value.toString());
                }
                return this;
            }
        });
        this.walletFilterBox.addActionListener(e -> triggerFilter());

        // Styling group box (clean text, no broken emoji tofu)
        this.groupFilterBox.setFont(ViewConstants.FONT_BODY);
        this.groupFilterBox.setBackground(new Color(13, 13, 13));
        this.groupFilterBox.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.groupFilterBox.setPreferredSize(new Dimension(140, 34));
        this.groupFilterBox.putClientProperty("Component.arc", 8);
        this.groupFilterBox.addItem("Tất cả nhóm");
        this.groupFilterBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(value.toString());
                }
                return this;
            }
        });
        this.groupFilterBox.addActionListener(e -> {
            onGroupFilterChanged();
            triggerFilter();
        });

        // Styling category box (clean text, no broken emoji tofu)
        this.categoryFilterBox.setFont(ViewConstants.FONT_BODY);
        this.categoryFilterBox.setBackground(new Color(13, 13, 13));
        this.categoryFilterBox.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.categoryFilterBox.setPreferredSize(new Dimension(150, 34));
        this.categoryFilterBox.putClientProperty("Component.arc", 8);
        this.categoryFilterBox.addItem("Tất cả danh mục");
        this.categoryFilterBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryResponseDTO c) {
                    setText(c.getName());
                } else if (value != null) {
                    setText(value.toString());
                }
                return this;
            }
        });
        this.categoryFilterBox.addActionListener(e -> triggerFilter());

        // Text fields styling with FlatLaf placeholders
        styleDateField(this.startDateField, "dd/MM/yyyy");
        styleDateField(this.endDateField, "dd/MM/yyyy");
        styleSearchField(this.keywordField, "Tìm kiếm ghi chú, danh mục, ví...");

        this.filterButton.addActionListener(e -> triggerFilter());
        this.resetButton.addActionListener(e -> handleReset());

        updateTagStyles();

        // --- HÀNG 1: Nhóm nút tác vụ (Trái) & Thẻ ngày nhanh + Tìm kiếm (Phải) ---
        JPanel row1 = new JPanel(new BorderLayout(16, 0));
        row1.setBackground(ViewConstants.COLOR_CARD_BG);
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(ViewConstants.COLOR_CARD_BG);
        actionsPanel.add(this.addButton);
        actionsPanel.add(this.resetButton);
        actionsPanel.add(this.refreshButton);

        JPanel quickAndSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        quickAndSearchPanel.setBackground(ViewConstants.COLOR_CARD_BG);
        JLabel quickDateLabel = createLabel("Thời gian:");
        quickDateLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        quickAndSearchPanel.add(quickDateLabel);

        quickAndSearchPanel.add(this.btnQuickAll);
        quickAndSearchPanel.add(this.btnQuickMonth);
        quickAndSearchPanel.add(this.btnQuick7Days);
        quickAndSearchPanel.add(Box.createHorizontalStrut(6));

        JPanel searchWrapper = new JPanel(new BorderLayout(6, 0));
        searchWrapper.setBackground(new Color(13, 13, 13));
        searchWrapper.setPreferredSize(new Dimension(230, 34));
        JLabel searchIconLabel = new JLabel(new SearchIcon(14, ViewConstants.COLOR_PRIMARY_LIGHT));
        searchIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        searchWrapper.add(searchIconLabel, BorderLayout.WEST);
        searchWrapper.add(this.keywordField, BorderLayout.CENTER);
        searchWrapper.setBorder(new RoundedBorder(17, ViewConstants.COLOR_BORDER, new Insets(1, 4, 1, 4)));

        quickAndSearchPanel.add(searchWrapper);
        this.keywordField.addActionListener(e -> triggerFilter());

        row1.add(actionsPanel, BorderLayout.WEST);
        row1.add(quickAndSearchPanel, BorderLayout.EAST);

        // Đường phân cách mỏng giữa 2 hàng
        JPanel divider = new JPanel();
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(100, 1));
        divider.setBackground(ViewConstants.COLOR_BORDER);
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- HÀNG 2: Bộ lọc chuyên sâu trải đều ---
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row2.setBackground(ViewConstants.COLOR_CARD_BG);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);

        row2.add(createLabel("Loại:"));
        row2.add(this.typeFilterBox);
        row2.add(createLabel("Ví:"));
        row2.add(this.walletFilterBox);
        row2.add(createLabel("Nhóm:"));
        row2.add(this.groupFilterBox);
        row2.add(createLabel("Danh mục:"));
        row2.add(this.categoryFilterBox);
        row2.add(createLabel("Khoảng ngày:"));
        row2.add(this.startDateField);
        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        arrowLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        row2.add(arrowLabel);
        row2.add(this.endDateField);
        row2.add(this.filterButton);

        add(row1);
        add(Box.createVerticalStrut(10));
        add(divider);
        add(Box.createVerticalStrut(10));
        add(row2);
    }

    private void styleDateField(JTextField tf, String placeholder) {
        tf.setFont(ViewConstants.FONT_BODY);
        tf.setBackground(new Color(13, 13, 13));
        tf.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        tf.setCaretColor(ViewConstants.COLOR_TEXT_MAIN);
        tf.setHorizontalAlignment(SwingConstants.CENTER);
        tf.setPreferredSize(new Dimension(100, 34));
        if (placeholder != null && !placeholder.isBlank()) {
            tf.putClientProperty("JTextField.placeholderText", placeholder);
            tf.putClientProperty("JTextField.showClearButton", true);
        }
        tf.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, new Color(55, 55, 55), new Insets(1, 4, 1, 4)),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
    }

    private void styleSearchField(JTextField tf, String placeholder) {
        tf.setFont(ViewConstants.FONT_BODY);
        tf.setBackground(new Color(13, 13, 13));
        tf.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        tf.setCaretColor(ViewConstants.COLOR_TEXT_MAIN);
        tf.setOpaque(true);
        if (placeholder != null && !placeholder.isBlank()) {
            tf.putClientProperty("JTextField.placeholderText", placeholder);
            tf.putClientProperty("JTextField.showClearButton", true);
        }
        tf.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 8));
    }
    // endregion

    // region Cascading Logic
    private void onTypeFilterChanged() {
        if (this.isUpdating) {
            return;
        }
        this.isUpdating = true;
        try {
            updateGroupOptions();
            updateCategoryOptions();
        } finally {
            this.isUpdating = false;
        }
    }

    private void onGroupFilterChanged() {
        if (this.isUpdating) {
            return;
        }
        this.isUpdating = true;
        try {
            updateCategoryOptions();
        } finally {
            this.isUpdating = false;
        }
    }

    private void updateGroupOptions() {
        String selectedType = (String) this.typeFilterBox.getSelectedItem();
        boolean filterByType = selectedType != null && !"Tất cả".equalsIgnoreCase(selectedType);

        Set<String> groups = new LinkedHashSet<>();
        for (CategoryResponseDTO c : this.allCategories) {
            if (!filterByType || selectedType.equalsIgnoreCase(c.getTransactionType())) {
                if (c.getParentGroupName() != null && !c.getParentGroupName().isBlank()) {
                    groups.add(c.getParentGroupName());
                }
            }
        }

        this.groupFilterBox.removeAllItems();
        this.groupFilterBox.addItem("Tất cả nhóm");
        for (String g : groups) {
            this.groupFilterBox.addItem(g);
        }
    }

    private void updateCategoryOptions() {
        String selectedType = (String) this.typeFilterBox.getSelectedItem();
        boolean filterByType = selectedType != null && !"Tất cả".equalsIgnoreCase(selectedType);

        Object selectedGroup = this.groupFilterBox.getSelectedItem();
        boolean filterByGroup = selectedGroup instanceof String g && !"Tất cả nhóm".equalsIgnoreCase(g);
        String groupName = filterByGroup ? (String) selectedGroup : null;

        this.categoryFilterBox.removeAllItems();
        this.categoryFilterBox.addItem("Tất cả danh mục");
        for (CategoryResponseDTO c : this.allCategories) {
            boolean matchesType = !filterByType || selectedType.equalsIgnoreCase(c.getTransactionType());
            boolean matchesGroup = !filterByGroup || (groupName != null && groupName.equalsIgnoreCase(c.getParentGroupName()));
            if (matchesType && matchesGroup) {
                this.categoryFilterBox.addItem(c);
            }
        }
    }
    // endregion

    // region Actions
    public void populateWallets(List<WalletResponseDTO> wallets) {
        this.isUpdating = true;
        try {
            this.walletFilterBox.removeAllItems();
            this.walletFilterBox.addItem("Tất cả ví");
            if (wallets != null) {
                for (WalletResponseDTO w : wallets) {
                    this.walletFilterBox.addItem(w);
                }
            }
        } finally {
            this.isUpdating = false;
        }
    }

    public void populateCategories(List<CategoryResponseDTO> categories) {
        this.isUpdating = true;
        try {
            this.allCategories.clear();
            if (categories != null) {
                this.allCategories.addAll(categories);
            }
            updateGroupOptions();
            updateCategoryOptions();
        } finally {
            this.isUpdating = false;
        }
    }

    public void setQuickDate(String period) {
        this.currentQuickTag = period;
        updateTagStyles();
        LocalDate today = LocalDate.now();
        switch (period) {
            case "ALL" -> {
                this.startDateField.setText("");
                this.endDateField.setText("");
            }
            case "MONTH" -> {
                LocalDate firstDay = today.withDayOfMonth(1);
                this.startDateField.setText(this.dateFormatter.format(firstDay));
                this.endDateField.setText(this.dateFormatter.format(today));
            }
            case "7DAYS" -> {
                LocalDate past7 = today.minusDays(7);
                this.startDateField.setText(this.dateFormatter.format(past7));
                this.endDateField.setText(this.dateFormatter.format(today));
            }
            default -> {}
        }
        triggerFilter();
    }

    public void resetFilters() {
        this.isUpdating = true;
        try {
            this.currentQuickTag = "ALL";
            updateTagStyles();
            this.typeFilterBox.setSelectedIndex(0);
            this.walletFilterBox.setSelectedIndex(0);
            updateGroupOptions();
            updateCategoryOptions();
            this.groupFilterBox.setSelectedIndex(0);
            this.categoryFilterBox.setSelectedIndex(0);
            this.startDateField.setText("");
            this.endDateField.setText("");
            this.keywordField.setText("");
        } finally {
            this.isUpdating = false;
        }
    }

    private void handleReset() {
        resetFilters();
        if (this.onResetAction != null) {
            this.onResetAction.run();
        } else {
            triggerFilter();
        }
    }

    private void triggerFilter() {
        if (!this.isUpdating && this.onFilterAction != null) {
            this.onFilterAction.run();
        }
    }
    // endregion

    // region Getters
    public FilterTransactionsRequestDTO buildFilterRequest(int page, int pageSize) {
        String typeFilter = (String) this.typeFilterBox.getSelectedItem();
        String type = ("Tất cả".equalsIgnoreCase(typeFilter) || typeFilter == null) ? null : typeFilter;

        Set<UUID> walletIds = null;
        Object selectedWallet = this.walletFilterBox.getSelectedItem();
        if (selectedWallet instanceof WalletResponseDTO w) {
            walletIds = new HashSet<>(Collections.singleton(w.getWalletId()));
        }

        Set<UUID> categoryIds = null;
        Object selectedCategory = this.categoryFilterBox.getSelectedItem();
        if (selectedCategory instanceof CategoryResponseDTO c) {
            categoryIds = new HashSet<>(Collections.singleton(c.getCategoryId()));
        } else {
            Object selectedGroup = this.groupFilterBox.getSelectedItem();
            if (selectedGroup instanceof String g && !"Tất cả nhóm".equalsIgnoreCase(g)) {
                categoryIds = new HashSet<>();
                for (CategoryResponseDTO c : this.allCategories) {
                    if (g.equalsIgnoreCase(c.getParentGroupName())) {
                        if (type == null || type.equalsIgnoreCase(c.getTransactionType())) {
                            categoryIds.add(c.getCategoryId());
                        }
                    }
                }
                if (categoryIds.isEmpty()) {
                    categoryIds = null;
                }
            }
        }

        LocalDate start = InputValidator.parseOptionalDate(this.startDateField.getText(), "Ngày bắt đầu");
        LocalDate end = InputValidator.parseOptionalDate(this.endDateField.getText(), "Ngày kết thúc");
        String kw = this.keywordField.getText();

        return new FilterTransactionsRequestDTO(start, end, walletIds, type, categoryIds, kw, page, pageSize);
    }

    public String getSelectedTypeFilter() {
        return (String) this.typeFilterBox.getSelectedItem();
    }

    public void setSelectedTypeFilter(String type) {
        this.isUpdating = true;
        try {
            if (type == null) {
                this.typeFilterBox.setSelectedIndex(0);
            } else {
                this.typeFilterBox.setSelectedItem(type);
            }
            updateGroupOptions();
            updateCategoryOptions();
        } finally {
            this.isUpdating = false;
        }
        triggerFilter();
    }

    public JComboBox<String> getTypeFilterBox() { return this.typeFilterBox; }
    public JComboBox<Object> getWalletFilterBox() { return this.walletFilterBox; }
    public JComboBox<Object> getGroupFilterBox() { return this.groupFilterBox; }
    public JComboBox<Object> getCategoryFilterBox() { return this.categoryFilterBox; }
    public JTextField getStartDateField() { return this.startDateField; }
    public JTextField getEndDateField() { return this.endDateField; }
    public JTextField getKeywordField() { return this.keywordField; }
    public JButton getFilterButton() { return this.filterButton; }
    public JButton getResetButton() { return this.resetButton; }
    public JButton getAddButton() { return this.addButton; }
    public JButton getRefreshButton() { return this.refreshButton; }
    public void setOnFilterAction(Runnable listener) { this.onFilterAction = listener; }
    public void setOnResetAction(Runnable listener) { this.onResetAction = listener; }
    // endregion

    // region Helpers
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ViewConstants.FONT_BODY);
        lbl.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        return lbl;
    }

    private JButton createActionButton(String text, Color bg, Color fg, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFont(isPrimary ? new Font("Segoe UI", Font.BOLD, 12) : new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("Component.arc", 8);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        Color borderColor = isPrimary ? ViewConstants.COLOR_PRIMARY_LIGHT : ViewConstants.COLOR_BORDER;
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, borderColor, new Insets(1, 4, 1, 4)),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)));
        return btn;
    }

    private JButton createTagButton(String text, String period) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("Component.arc", 16);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.addActionListener(e -> setQuickDate(period));
        return btn;
    }

    private void updateTagStyles() {
        applyTagStyle(this.btnQuickAll, "ALL".equalsIgnoreCase(this.currentQuickTag));
        applyTagStyle(this.btnQuickMonth, "MONTH".equalsIgnoreCase(this.currentQuickTag));
        applyTagStyle(this.btnQuick7Days, "7DAYS".equalsIgnoreCase(this.currentQuickTag));
    }

    private void applyTagStyle(JButton btn, boolean isActive) {
        if (isActive) {
            btn.setBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
            btn.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(14, ViewConstants.COLOR_PRIMARY, new Insets(1, 4, 1, 4)),
                    BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        } else {
            btn.setBackground(ViewConstants.COLOR_STATUS_BG);
            btn.setForeground(ViewConstants.COLOR_TEXT_MUTED);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(14, ViewConstants.COLOR_BORDER, new Insets(1, 4, 1, 4)),
                    BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        }
    }
    // endregion
}
