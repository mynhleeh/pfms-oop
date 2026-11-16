package com.pfms.presentation.transaction.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.transaction.TransactionGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.common.ViewConstants;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Hộp thoại modal chỉnh sửa thông tin giao dịch Thu / Chi hiện có (UC-TRN-04).
 * Thiết kế đồng bộ chuẩn phong cách Luxury Black &amp; Gold với RecordTransactionDialog:
 * - Header màu than chì đậm tinh tế kèm tiêu đề vàng Champagne
 * - Nhãn Loại giao dịch hiển thị rõ ràng, chuyên nghiệp
 * - Live Currency format preview cạnh ô nhập số tiền
 * - Dropdown Ví chỉ hiển thị tên ví, số dư khả dụng hiển thị bên dưới
 * - Fee Box card bo tròn viền mịn dạng Accordion mở rộng linh hoạt
 * - JScrollPane cuộn mượt mà bảo vệ kích thước trên mọi màn hình.
 */
public class UpdateTransactionDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final TransactionGUIController transactionController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;
    private final TransactionResponseDTO existingTxn;

    private final JTextField amountField;
    private final JLabel amountInWordsHint;
    private final JComboBox<WalletResponseDTO> walletCombo;
    private final JLabel availableBalanceLabel = new JLabel();
    private final JComboBox<CategoryResponseDTO> categoryCombo;
    private final JTextField dateField;
    private final JTextField noteField;

    private final JCheckBox feeCheckBox;
    private final JPanel feeDetailsPanel;
    private final JTextField feeAmountField;
    private final JComboBox<CategoryHierarchyResponseDTO> feeGroupCombo;
    private final JComboBox<CategoryResponseDTO> feeCategoryCombo;

    private final JButton cancelButton;
    private final JButton saveButton;

    private final List<CategoryHierarchyResponseDTO> expenseGroups = new ArrayList<>();
    private final List<CategoryResponseDTO> availableCategories = new ArrayList<>();

    private boolean success = false;
    private TransactionResponseDTO updatedTransaction = null;
    private String errorMessage = null;
    private Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Lỗi cập nhật giao dịch", JOptionPane.ERROR_MESSAGE);
    };

    public UpdateTransactionDialog(
            JFrame parent,
            TransactionGUIController transactionController,
            WalletGUIController walletController,
            CategoryGUIController categoryController,
            TransactionResponseDTO existingTxn) {

        super(parent, "Chỉnh Sửa Thông Tin Giao Dịch", true);
        this.transactionController = Objects.requireNonNull(transactionController, "TransactionGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");
        this.existingTxn = Objects.requireNonNull(existingTxn, "TransactionResponseDTO không được null");

        setSize(520, 660);
        setMinimumSize(new Dimension(480, 580));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ViewConstants.COLOR_CARD_BG);

        loadCategories();

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(14, 14, 14));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(14, 22, 14, 22)
        ));
        JLabel titleLabel = new JLabel("✏️  Chỉnh Sửa Thông Tin Giao Dịch");
        titleLabel.setFont(ViewConstants.FONT_TITLE);
        titleLabel.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Form Body Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        formPanel.setBackground(ViewConstants.COLOR_CARD_BG);

        // Loại giao dịch (Chỉ đọc với Badge màu tương ứng)
        boolean isIncome = "INCOME".equalsIgnoreCase(existingTxn.getTransactionType());
        Color typeColor = isIncome ? ViewConstants.COLOR_INCOME : ViewConstants.COLOR_EXPENSE;
        Color typeBg = isIncome ? new Color(74, 222, 128, 30) : new Color(251, 113, 133, 30);

        JPanel typeBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        typeBadge.setBackground(typeBg);
        typeBadge.setBorder(BorderFactory.createLineBorder(new Color(typeColor.getRed(), typeColor.getGreen(), typeColor.getBlue(), 90), 1, true));
        typeBadge.putClientProperty("FlatLaf.style", "arc: 8");
        typeBadge.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel typeText = new JLabel(isIncome ? "🟢 THU NHẬP (INCOME)" : "🔴 CHI TIÊU (EXPENSE)");
        typeText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        typeText.setForeground(typeColor);
        typeBadge.add(typeText);

        formPanel.add(createFieldHeader("Phân loại giao dịch", false, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(typeBadge);
        formPanel.add(Box.createVerticalStrut(14));

        // Amount Field with Live Preview Hint
        this.amountInWordsHint = new JLabel("0 VNĐ");
        this.amountInWordsHint.setFont(ViewConstants.FONT_STATUS);
        this.amountInWordsHint.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);

        this.amountField = new JTextField(existingTxn.getAmount() != null ? existingTxn.getAmount().toPlainString() : "");
        styleInputField(this.amountField);
        this.amountField.putClientProperty("JTextField.placeholderText", "Ví dụ: 150000");
        this.amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateAmountHint(); }
            @Override public void removeUpdate(DocumentEvent e) { updateAmountHint(); }
            @Override public void changedUpdate(DocumentEvent e) { updateAmountHint(); }
        });
        updateAmountHint();

        formPanel.add(createFieldHeader("Số tiền giao dịch (VNĐ)", true, this.amountInWordsHint));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.amountField);
        formPanel.add(Box.createVerticalStrut(14));

        // Wallet Dropdown & Balance Hint
        this.walletCombo = new JComboBox<>();
        styleComboBox(this.walletCombo);
        this.availableBalanceLabel.setFont(ViewConstants.FONT_STATUS);
        this.availableBalanceLabel.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.availableBalanceLabel.setVisible(false);

        JPanel balanceHintRow = new JPanel(new BorderLayout());
        balanceHintRow.setOpaque(false);
        balanceHintRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        balanceHintRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        balanceHintRow.setBorder(BorderFactory.createEmptyBorder(3, 8, 0, 8));
        balanceHintRow.add(this.availableBalanceLabel, BorderLayout.WEST);
        configureWalletCombo();

        JPanel walletGroupPanel = new JPanel();
        walletGroupPanel.setLayout(new BoxLayout(walletGroupPanel, BoxLayout.Y_AXIS));
        walletGroupPanel.setOpaque(false);
        walletGroupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        walletGroupPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        walletGroupPanel.add(this.walletCombo);
        walletGroupPanel.add(balanceHintRow);

        formPanel.add(createFieldHeader("Ví tài chính thanh toán", true, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(walletGroupPanel);
        formPanel.add(Box.createVerticalStrut(14));

        // Category Dropdown
        this.categoryCombo = new JComboBox<>();
        styleComboBox(this.categoryCombo);
        configureCategoryCombo(this.categoryCombo);
        populateCategories();

        formPanel.add(createFieldHeader("Danh mục thu chi", true, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.categoryCombo);
        formPanel.add(Box.createVerticalStrut(14));

        // Date Field
        this.dateField = new JTextField(existingTxn.getTransactionDate() != null ? existingTxn.getTransactionDate().toString() : "");
        styleInputField(this.dateField);
        this.dateField.putClientProperty("JTextField.placeholderText", "yyyy-MM-dd");

        formPanel.add(createFieldHeader("Ngày thực hiện giao dịch", true, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.dateField);
        formPanel.add(Box.createVerticalStrut(14));

        // Note Field
        this.noteField = new JTextField(existingTxn.getNote() != null ? existingTxn.getNote() : "");
        styleInputField(this.noteField);
        this.noteField.putClientProperty("JTextField.placeholderText", "Ví dụ: Ăn trưa cùng đồng nghiệp, đổ xăng...");

        formPanel.add(createFieldHeader("Ghi chú chi tiết", false, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.noteField);
        formPanel.add(Box.createVerticalStrut(14));

        // Fee Box (Accordion Card with Rounded Border)
        JPanel feeBox = new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        feeBox.setLayout(new BoxLayout(feeBox, BoxLayout.Y_AXIS));
        feeBox.setBackground(new Color(10, 10, 10));
        feeBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        feeBox.putClientProperty("FlatLaf.style", "arc: 12");
        feeBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.feeCheckBox = new JCheckBox("Có phụ phí phát sinh");
        this.feeCheckBox.setFont(ViewConstants.FONT_BODY);
        this.feeCheckBox.setBackground(ViewConstants.COLOR_CARD_BG);
        this.feeCheckBox.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.feeCheckBox.setOpaque(false);
        this.feeCheckBox.setFocusPainted(false);
        this.feeCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.feeCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.feeCheckBox.setSelected(existingTxn.hasFee());

        this.feeAmountField = new JTextField(existingTxn.hasFee() && existingTxn.getFeeAmount() != null
                ? existingTxn.getFeeAmount().toPlainString()
                : "0");
        styleInputField(this.feeAmountField);
        this.feeAmountField.setEnabled(existingTxn.hasFee());

        this.feeGroupCombo = new JComboBox<>();
        styleComboBox(this.feeGroupCombo);
        this.feeGroupCombo.setEnabled(existingTxn.hasFee());
        configureFeeGroupCombo();

        this.feeCategoryCombo = new JComboBox<>();
        styleComboBox(this.feeCategoryCombo);
        this.feeCategoryCombo.setEnabled(existingTxn.hasFee());
        configureCategoryCombo(this.feeCategoryCombo);
        populateInitialFeeData();

        this.feeDetailsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        this.feeDetailsPanel.setOpaque(false);
        this.feeDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.feeDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(10, 0, 2, 0)
        ));
        this.feeDetailsPanel.setVisible(existingTxn.hasFee());

        JPanel feeAmountCol = new JPanel();
        feeAmountCol.setLayout(new BoxLayout(feeAmountCol, BoxLayout.Y_AXIS));
        feeAmountCol.setOpaque(false);
        JLabel feeAmtLabel = new JLabel("Số tiền phụ phí");
        feeAmtLabel.setFont(ViewConstants.FONT_STATUS);
        feeAmtLabel.setForeground(ViewConstants.COLOR_EXPENSE);
        feeAmountCol.add(feeAmtLabel);
        feeAmountCol.add(Box.createVerticalStrut(4));
        feeAmountCol.add(this.feeAmountField);

        JPanel feeGroupCol = new JPanel();
        feeGroupCol.setLayout(new BoxLayout(feeGroupCol, BoxLayout.Y_AXIS));
        feeGroupCol.setOpaque(false);
        JLabel feeGrpLabel = new JLabel("Nhóm phụ phí (*)");
        feeGrpLabel.setFont(ViewConstants.FONT_STATUS);
        feeGrpLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        feeGroupCol.add(feeGrpLabel);
        feeGroupCol.add(Box.createVerticalStrut(4));
        feeGroupCol.add(this.feeGroupCombo);

        JPanel feeCatCol = new JPanel();
        feeCatCol.setLayout(new BoxLayout(feeCatCol, BoxLayout.Y_AXIS));
        feeCatCol.setOpaque(false);
        JLabel feeCatLabel = new JLabel("Danh mục phụ phí (*)");
        feeCatLabel.setFont(ViewConstants.FONT_STATUS);
        feeCatLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        feeCatCol.add(feeCatLabel);
        feeCatCol.add(Box.createVerticalStrut(4));
        feeCatCol.add(this.feeCategoryCombo);

        this.feeDetailsPanel.add(feeAmountCol);
        this.feeDetailsPanel.add(feeGroupCol);
        this.feeDetailsPanel.add(feeCatCol);

        this.feeCheckBox.addActionListener(e -> {
            boolean enabled = this.feeCheckBox.isSelected();
            this.feeAmountField.setEnabled(enabled);
            this.feeGroupCombo.setEnabled(enabled);
            this.feeDetailsPanel.setVisible(enabled);
            if (!enabled) {
                this.feeAmountField.setText("0");
                this.feeCategoryCombo.setEnabled(false);
            } else {
                onFeeGroupChanged();
            }
            feeBox.revalidate();
            feeBox.repaint();
        });

        feeBox.add(this.feeCheckBox);
        feeBox.add(Box.createVerticalStrut(8));
        feeBox.add(this.feeDetailsPanel);

        formPanel.add(feeBox);
        formPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Footer Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        buttonPanel.setBackground(new Color(14, 14, 14));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        this.cancelButton = new JButton("Hủy bỏ");
        this.cancelButton.setFont(ViewConstants.FONT_BODY);
        this.cancelButton.setBackground(ViewConstants.COLOR_CARD_BG);
        this.cancelButton.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.cancelButton.setFocusPainted(false);
        this.cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.cancelButton.putClientProperty("FlatLaf.style", "arc: 8");
        this.cancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        this.cancelButton.addActionListener(e -> dispose());

        this.saveButton = new JButton("Lưu thay đổi");
        this.saveButton.setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.saveButton.setBackground(ViewConstants.COLOR_PRIMARY);
        this.saveButton.setForeground(Color.BLACK);
        this.saveButton.setFocusPainted(false);
        this.saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.saveButton.putClientProperty("FlatLaf.style", "arc: 8");
        this.saveButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_PRIMARY, 1, true),
                BorderFactory.createEmptyBorder(8, 22, 8, 22)
        ));
        this.saveButton.addActionListener(e -> triggerSave());

        buttonPanel.add(this.cancelButton);
        buttonPanel.add(this.saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // region Styling Helpers
    private JPanel createFieldHeader(String title, boolean required, JLabel rightHint) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JPanel leftPart = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPart.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ViewConstants.FONT_BODY);
        titleLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        leftPart.add(titleLabel);

        if (required) {
            JLabel star = new JLabel(" *");
            star.setFont(ViewConstants.FONT_BODY);
            star.setForeground(ViewConstants.COLOR_EXPENSE);
            leftPart.add(star);
        }
        header.add(leftPart, BorderLayout.WEST);

        if (rightHint != null) {
            header.add(rightHint, BorderLayout.EAST);
        }
        return header;
    }

    private void styleInputField(JTextField field) {
        field.setFont(ViewConstants.FONT_BODY);
        field.setBackground(new Color(10, 10, 10));
        field.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        field.setCaretColor(ViewConstants.COLOR_PRIMARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setPreferredSize(new Dimension(0, 38));
        field.putClientProperty("FlatLaf.style", "arc: 8");
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
    }

    private <T> void styleComboBox(JComboBox<T> combo) {
        combo.setFont(ViewConstants.FONT_BODY);
        combo.setBackground(new Color(10, 10, 10));
        combo.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        combo.setPreferredSize(new Dimension(0, 38));
        combo.putClientProperty("FlatLaf.style", "arc: 8");
    }

    private void updateAmountHint() {
        String text = this.amountField.getText();
        if (text == null || text.isBlank()) {
            this.amountInWordsHint.setText("0 VNĐ");
            return;
        }
        try {
            String clean = text.replace(",", "").trim();
            BigDecimal val = new BigDecimal(clean);
            if (val.compareTo(BigDecimal.ZERO) <= 0) {
                this.amountInWordsHint.setText("0 VNĐ");
            } else {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                symbols.setGroupingSeparator(',');
                DecimalFormat df = new DecimalFormat("#,##0", symbols);
                this.amountInWordsHint.setText(df.format(val) + " VNĐ");
            }
        } catch (Exception ex) {
            this.amountInWordsHint.setText("0 VNĐ");
        }
    }
    // endregion

    // region Data Loading & Binding
    private void loadCategories() {
        this.availableCategories.clear();
        this.expenseGroups.clear();

        List<CategoryHierarchyResponseDTO> hierarchies = this.categoryController.loadCategoryHierarchy();
        if (hierarchies != null) {
            for (CategoryHierarchyResponseDTO group : hierarchies) {
                if ("EXPENSE".equalsIgnoreCase(group.getTransactionType())) {
                    this.expenseGroups.add(group);
                }
                if (group.getTransactionType().equalsIgnoreCase(this.existingTxn.getTransactionType())) {
                    this.availableCategories.addAll(group.getCategories());
                }
            }
        }
    }

    private void configureWalletCombo() {
        WalletsSummaryResponseDTO summary = this.walletController.loadWalletsSummary();
        this.walletCombo.removeAllItems();
        if (summary != null && summary.getWallets() != null) {
            for (WalletResponseDTO w : summary.getWallets()) {
                if ("ACTIVE".equalsIgnoreCase(w.getStatus())) {
                    this.walletCombo.addItem(w);
                    if (w.getWalletId().equals(this.existingTxn.getWalletId())) {
                        this.walletCombo.setSelectedItem(w);
                    }
                }
            }
        }

        this.walletCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof WalletResponseDTO w) {
                    setText(w.getName());
                }
                return this;
            }
        });

        this.walletCombo.addActionListener(e -> updateAvailableBalance());
        updateAvailableBalance();
    }

    private void updateAvailableBalance() {
        WalletResponseDTO w = (WalletResponseDTO) this.walletCombo.getSelectedItem();
        if (w != null && w.getBalance() != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            symbols.setGroupingSeparator(',');
            DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
            this.availableBalanceLabel.setText("Số dư khả dụng: " + formatter.format(w.getBalance()) + " VNĐ");
            this.availableBalanceLabel.setVisible(true);
        } else {
            this.availableBalanceLabel.setText("");
            this.availableBalanceLabel.setVisible(false);
        }
    }

    private void configureCategoryCombo(JComboBox<CategoryResponseDTO> combo) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryResponseDTO cat) {
                    setText(cat.getName());
                }
                return this;
            }
        });
    }

    private void populateCategories() {
        this.categoryCombo.removeAllItems();
        for (CategoryResponseDTO cat : this.availableCategories) {
            this.categoryCombo.addItem(cat);
            if (cat.getCategoryId().equals(this.existingTxn.getCategoryId())) {
                this.categoryCombo.setSelectedItem(cat);
            }
        }
    }

    private void configureFeeGroupCombo() {
        this.feeGroupCombo.removeAllItems();
        this.feeGroupCombo.addItem(null);
        for (CategoryHierarchyResponseDTO group : this.expenseGroups) {
            this.feeGroupCombo.addItem(group);
        }

        this.feeGroupCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryHierarchyResponseDTO g) {
                    setText(g.getNeedGroupName());
                } else {
                    setText("-- Chọn nhóm phụ phí --");
                }
                return this;
            }
        });

        this.feeGroupCombo.addActionListener(e -> onFeeGroupChanged());
    }

    private void onFeeGroupChanged() {
        CategoryHierarchyResponseDTO selected = (CategoryHierarchyResponseDTO) this.feeGroupCombo.getSelectedItem();
        this.feeCategoryCombo.removeAllItems();
        if (selected != null && selected.getCategories() != null && !selected.getCategories().isEmpty()) {
            for (CategoryResponseDTO cat : selected.getCategories()) {
                if ("ACTIVE".equalsIgnoreCase(cat.getStatus())) {
                    this.feeCategoryCombo.addItem(cat);
                }
            }
            this.feeCategoryCombo.setEnabled(this.feeCheckBox.isSelected());
        } else {
            this.feeCategoryCombo.setEnabled(false);
        }
    }

    private void populateInitialFeeData() {
        if (this.existingTxn.hasFee() && this.existingTxn.getFeeCategoryId() != null) {
            UUID feeCatId = this.existingTxn.getFeeCategoryId();
            for (CategoryHierarchyResponseDTO group : this.expenseGroups) {
                if (group.getCategories() != null) {
                    for (CategoryResponseDTO cat : group.getCategories()) {
                        if (cat.getCategoryId().equals(feeCatId)) {
                            this.feeGroupCombo.setSelectedItem(group);
                            onFeeGroupChanged();
                            for (int i = 0; i < this.feeCategoryCombo.getItemCount(); i++) {
                                CategoryResponseDTO c = this.feeCategoryCombo.getItemAt(i);
                                if (c != null && c.getCategoryId().equals(feeCatId)) {
                                    this.feeCategoryCombo.setSelectedIndex(i);
                                    break;
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
    // endregion

    // region Action Handlers
    public void triggerSave() {
        WalletResponseDTO wallet = (WalletResponseDTO) this.walletCombo.getSelectedItem();
        CategoryResponseDTO category = (CategoryResponseDTO) this.categoryCombo.getSelectedItem();

        if (wallet == null) {
            handleError("Vui lòng chọn ví tài chính.");
            return;
        }
        if (category == null) {
            handleError("Vui lòng chọn danh mục thu chi.");
            return;
        }

        String rawAmount = this.amountField.getText();
        String rawDate = this.dateField.getText();
        String rawNote = this.noteField.getText();

        String rawFeeAmount = null;
        UUID feeCatId = null;
        if (this.feeCheckBox.isSelected()) {
            rawFeeAmount = this.feeAmountField.getText();
            CategoryResponseDTO feeCat = (CategoryResponseDTO) this.feeCategoryCombo.getSelectedItem();
            if (feeCat != null) {
                feeCatId = feeCat.getCategoryId();
            } else {
                handleError("Vui lòng chọn nhóm và danh mục phụ phí.");
                return;
            }
        }

        try {
            this.updatedTransaction = this.transactionController.handleUpdateTransaction(
                    this.existingTxn.getTransactionId(),
                    wallet.getWalletId(),
                    rawAmount,
                    rawDate,
                    category.getCategoryId(),
                    rawNote,
                    rawFeeAmount,
                    feeCatId);
            this.success = true;
            this.errorMessage = null;
            dispose();
        } catch (Exception ex) {
            handleError(ex.getMessage());
        }
    }

    private void handleError(String message) {
        this.errorMessage = message;
        this.success = false;
        if (this.errorConsumer != null) {
            this.errorConsumer.accept(message);
        }
    }
    // endregion

    // region Getters & Setters
    public boolean isSuccess() {
        return this.success;
    }

    public TransactionResponseDTO getUpdatedTransaction() {
        return this.updatedTransaction;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    public String getAmountInputValue() {
        return this.amountField.getText();
    }

    public void setAmountInputValue(String amount) {
        this.amountField.setText(amount);
        updateAmountHint();
    }

    public WalletResponseDTO getSelectedWallet() {
        return (WalletResponseDTO) this.walletCombo.getSelectedItem();
    }

    public void setSelectedWallet(WalletResponseDTO wallet) {
        this.walletCombo.setSelectedItem(wallet);
        updateAvailableBalance();
    }

    public JLabel getAvailableBalanceLabel() {
        return this.availableBalanceLabel;
    }

    public JComboBox<WalletResponseDTO> getWalletCombo() {
        return this.walletCombo;
    }

    public JComboBox<CategoryResponseDTO> getCategoryCombo() {
        return this.categoryCombo;
    }

    public CategoryResponseDTO getSelectedCategory() {
        return (CategoryResponseDTO) this.categoryCombo.getSelectedItem();
    }

    public void setSelectedCategory(CategoryResponseDTO category) {
        this.categoryCombo.setSelectedItem(category);
    }

    public String getDateInputValue() {
        return this.dateField.getText();
    }

    public void setDateInputValue(String date) {
        this.dateField.setText(date);
    }

    public String getNoteInputValue() {
        return this.noteField.getText();
    }

    public void setNoteInputValue(String note) {
        this.noteField.setText(note);
    }

    public JButton getCancelButton() {
        return this.cancelButton;
    }

    public JButton getSaveButton() {
        return this.saveButton;
    }

    public JCheckBox getFeeCheckBox() {
        return this.feeCheckBox;
    }

    public JTextField getFeeAmountField() {
        return this.feeAmountField;
    }

    public JComboBox<CategoryHierarchyResponseDTO> getFeeGroupCombo() {
        return this.feeGroupCombo;
    }

    public JComboBox<CategoryResponseDTO> getFeeCategoryCombo() {
        return this.feeCategoryCombo;
    }

    public JPanel getFeeDetailsPanel() {
        return this.feeDetailsPanel;
    }
    // endregion
}
