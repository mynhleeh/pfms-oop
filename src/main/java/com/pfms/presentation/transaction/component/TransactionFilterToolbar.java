package com.pfms.presentation.transaction.component;

import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.common.InputValidator;
import com.pfms.presentation.common.ViewConstants;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Thanh công cụ lọc và tìm kiếm giao dịch thu chi (UC-TRN-03).
 * Thành phần giao diện độc lập phân tách từ TransactionPanel.
 */
public class TransactionFilterToolbar extends JPanel {

    // region Fields
    private final JComboBox<String> typeFilterBox = new JComboBox<>(new String[]{"Tất cả", "INCOME", "EXPENSE"});
    private final JComboBox<Object> walletFilterBox = new JComboBox<>();
    private final JComboBox<Object> categoryFilterBox = new JComboBox<>();
    private final JTextField startDateField = new JTextField(8);
    private final JTextField endDateField = new JTextField(8);
    private final JTextField keywordField = new JTextField(10);
    private final JButton filterButton = createButton("Lọc", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
    private final JButton resetButton = createButton("Đặt lại", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MUTED);

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
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setBackground(ViewConstants.COLOR_CONTENT_BG);
        setAlignmentX(Component.LEFT_ALIGNMENT);

        this.typeFilterBox.setFont(ViewConstants.FONT_BODY);
        this.typeFilterBox.addActionListener(e -> triggerFilter());

        this.walletFilterBox.setFont(ViewConstants.FONT_BODY);
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

        this.categoryFilterBox.setFont(ViewConstants.FONT_BODY);
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

        this.startDateField.setFont(ViewConstants.FONT_BODY);
        this.endDateField.setFont(ViewConstants.FONT_BODY);
        this.keywordField.setFont(ViewConstants.FONT_BODY);

        this.filterButton.addActionListener(e -> triggerFilter());
        this.resetButton.addActionListener(e -> handleReset());

        add(createLabel("Loại:"));
        add(this.typeFilterBox);
        add(createLabel("Ví:"));
        add(this.walletFilterBox);
        add(createLabel("Danh mục:"));
        add(this.categoryFilterBox);
        add(createLabel("Từ:"));
        add(this.startDateField);
        add(createLabel("Đến:"));
        add(this.endDateField);
        add(createLabel("Tìm:"));
        add(this.keywordField);
        add(this.filterButton);
        add(this.resetButton);
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
            this.categoryFilterBox.removeAllItems();
            this.categoryFilterBox.addItem("Tất cả danh mục");
            if (categories != null) {
                for (CategoryResponseDTO c : categories) {
                    this.categoryFilterBox.addItem(c);
                }
            }
        } finally {
            this.isUpdating = false;
        }
    }

    public void resetFilters() {
        this.isUpdating = true;
        try {
            this.typeFilterBox.setSelectedIndex(0);
            this.walletFilterBox.setSelectedIndex(0);
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
        } finally {
            this.isUpdating = false;
        }
        triggerFilter();
    }

    public JComboBox<String> getTypeFilterBox() { return this.typeFilterBox; }
    public JComboBox<Object> getWalletFilterBox() { return this.walletFilterBox; }
    public JComboBox<Object> getCategoryFilterBox() { return this.categoryFilterBox; }
    public JTextField getStartDateField() { return this.startDateField; }
    public JTextField getEndDateField() { return this.endDateField; }
    public JTextField getKeywordField() { return this.keywordField; }
    public JButton getFilterButton() { return this.filterButton; }
    public JButton getResetButton() { return this.resetButton; }
    public void setOnFilterAction(Runnable listener) { this.onFilterAction = listener; }
    public void setOnResetAction(Runnable listener) { this.onResetAction = listener; }
    // endregion

    // region Helpers
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ViewConstants.FONT_BODY);
        return lbl;
    }

    private JButton createButton(String text, java.awt.Color bg, java.awt.Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(ViewConstants.FONT_BODY);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return btn;
    }
    // endregion
}
