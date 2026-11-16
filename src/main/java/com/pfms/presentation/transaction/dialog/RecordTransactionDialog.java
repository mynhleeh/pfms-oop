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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Hộp thoại modal ghi nhận giao dịch Thu &amp; Chi mới (UC-TRN-01, UC-TRN-02).
 * Tái cấu trúc chuẩn Luxury Black &amp; Gold theo phong cách Rounded trong preview-transaction.html:
 * - Type Switcher toggle 2 nút (Khoản Chi Tiêu / Khoản Thu Nhập)
 * - Amount Input kèm nhãn gợi ý live format tiền tệ (Ví dụ: 150,000 VNĐ)
 * - Cụm Ví tài chính &amp; Số dư khả dụng căn lề đồng bộ, hiển thị cân đối
 * - Dropdown Nhóm danh mục: chưa chọn nhóm thì danh mục bị vô hiệu hóa
 * - Dropdown Danh mục: hiển thị theo nhóm, chọn nhóm nào chỉ hiện danh mục của nhóm đó
 * - Fee Box card bo tròn viền mịn, accordion khít chặt khi đóng và mở rộng cân đối khi bật
 * - JScrollPane cuộn mượt mà bảo vệ kích thước và bố cục trên mọi độ phân giải.
 */
public class RecordTransactionDialog extends JDialog {

    private final TransactionGUIController transactionController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;

    private final JComboBox<String> typeCombo;
    private final JButton expenseTypeBtn;
    private final JButton incomeTypeBtn;

    private final JTextField amountField;
    private final JLabel amountInWordsHint;
    private final JComboBox<WalletResponseDTO> walletCombo;
    private final JLabel availableBalanceLabel;
    private final JPanel balanceHintRow;

    private final JComboBox<CategoryHierarchyResponseDTO> groupCombo;
    private final JComboBox<CategoryResponseDTO> categoryCombo;
    private final JTextField dateField;
    private final JTextField noteField;

    private final JCheckBox feeCheckBox;
    private final JPanel feeDetailsPanel;
    private final JTextField feeAmountField;
    private final JComboBox<CategoryResponseDTO> feeCategoryCombo;

    private final JButton cancelButton;
    private final JButton saveButton;

    private final List<CategoryHierarchyResponseDTO> expenseGroups = new ArrayList<>();
    private final List<CategoryHierarchyResponseDTO> incomeGroups = new ArrayList<>();
    private final List<CategoryResponseDTO> expenseCategories = new ArrayList<>();

    private boolean success = false;
    private TransactionResponseDTO createdTransaction = null;
    private String errorMessage = null;
    private Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Lỗi ghi nhận giao dịch", JOptionPane.ERROR_MESSAGE);
    };

    public RecordTransactionDialog(
            JFrame parent,
            TransactionGUIController transactionController,
            WalletGUIController walletController,
            CategoryGUIController categoryController) {

        super(parent, "Ghi Nhận Giao Dịch Mới (Thu / Chi)", true);
        this.transactionController = Objects.requireNonNull(transactionController, "TransactionGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");

        setSize(520, 680);
        setMinimumSize(new Dimension(480, 600));
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
        JLabel titleLabel = new JLabel("Ghi Nhận Giao Dịch Mới (Thu / Chi)");
        titleLabel.setFont(ViewConstants.FONT_TITLE);
        titleLabel.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Form Body Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        formPanel.setBackground(ViewConstants.COLOR_CARD_BG);

        // Type Switcher Control
        this.typeCombo = new JComboBox<>(new String[]{"Chi tiêu", "Thu nhập"});
        this.typeCombo.setVisible(false); // Giữ ngầm để tương thích ngược với API

        this.expenseTypeBtn = new JButton("Khoản Chi Tiêu");
        this.incomeTypeBtn = new JButton("Khoản Thu Nhập");
        styleTypeButton(this.expenseTypeBtn);
        styleTypeButton(this.incomeTypeBtn);

        this.expenseTypeBtn.addActionListener(e -> setSelectedTransactionType("Chi tiêu"));
        this.incomeTypeBtn.addActionListener(e -> setSelectedTransactionType("Thu nhập"));

        JPanel switcherContainer = new JPanel(new GridLayout(1, 2, 8, 0));
        switcherContainer.setBackground(new Color(8, 8, 8));
        switcherContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        switcherContainer.putClientProperty("FlatLaf.style", "arc: 12");
        switcherContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        switcherContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        switcherContainer.add(this.expenseTypeBtn);
        switcherContainer.add(this.incomeTypeBtn);

        formPanel.add(createFieldHeader("Phân loại giao dịch", true, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(switcherContainer);
        formPanel.add(Box.createVerticalStrut(14));

        // Amount Field with Live Preview Hint
        this.amountInWordsHint = new JLabel("0 VNĐ");
        this.amountInWordsHint.setFont(ViewConstants.FONT_STATUS);
        this.amountInWordsHint.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);

        this.amountField = new JTextField();
        styleInputField(this.amountField);
        this.amountField.putClientProperty("JTextField.placeholderText", "Ví dụ: 150000");
        this.amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateAmountHint(); }
            @Override public void removeUpdate(DocumentEvent e) { updateAmountHint(); }
            @Override public void changedUpdate(DocumentEvent e) { updateAmountHint(); }
        });

        formPanel.add(createFieldHeader("Số tiền giao dịch (VNĐ)", true, this.amountInWordsHint));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.amountField);
        formPanel.add(Box.createVerticalStrut(14));

        // Wallet Dropdown & Balanced Balance Hint Container
        this.walletCombo = new JComboBox<>();
        styleComboBox(this.walletCombo);
        this.availableBalanceLabel = new JLabel("Số dư khả dụng: 0 VNĐ");
        this.availableBalanceLabel.setFont(ViewConstants.FONT_STATUS);
        this.availableBalanceLabel.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.availableBalanceLabel.setVisible(false);

        this.balanceHintRow = new JPanel(new BorderLayout());
        this.balanceHintRow.setOpaque(false);
        this.balanceHintRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.balanceHintRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        this.balanceHintRow.setBorder(BorderFactory.createEmptyBorder(3, 8, 0, 8));
        this.balanceHintRow.add(this.availableBalanceLabel, BorderLayout.WEST);
        this.balanceHintRow.setVisible(false);

        configureWalletCombo();

        JPanel walletGroupPanel = new JPanel();
        walletGroupPanel.setLayout(new BoxLayout(walletGroupPanel, BoxLayout.Y_AXIS));
        walletGroupPanel.setOpaque(false);
        walletGroupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        walletGroupPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        walletGroupPanel.add(this.walletCombo);
        walletGroupPanel.add(this.balanceHintRow);

        formPanel.add(createFieldHeader("Ví tài chính thanh toán", true, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(walletGroupPanel);
        formPanel.add(Box.createVerticalStrut(14));

        // Group Dropdown (Mới: Nhóm danh mục - Bắt buộc chọn nhóm trước)
        this.groupCombo = new JComboBox<>();
        styleComboBox(this.groupCombo);
        configureGroupCombo();

        formPanel.add(createFieldHeader("Nhóm danh mục", true, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.groupCombo);
        formPanel.add(Box.createVerticalStrut(14));

        // Category Dropdown (Hiển thị theo nhóm, khóa khi chưa chọn nhóm)
        this.categoryCombo = new JComboBox<>();
        styleComboBox(this.categoryCombo);
        configureCategoryCombo(this.categoryCombo);
        this.categoryCombo.setEnabled(false); // Khóa ban đầu khi chưa chọn nhóm

        formPanel.add(createFieldHeader("Danh mục thu chi", true, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.categoryCombo);
        formPanel.add(Box.createVerticalStrut(14));

        // Date Field
        this.dateField = new JTextField(LocalDate.now().toString());
        styleInputField(this.dateField);
        this.dateField.putClientProperty("JTextField.placeholderText", "yyyy-MM-dd");

        formPanel.add(createFieldHeader("Ngày thực hiện giao dịch", true, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.dateField);
        formPanel.add(Box.createVerticalStrut(14));

        // Note Field
        this.noteField = new JTextField();
        styleInputField(this.noteField);
        this.noteField.putClientProperty("JTextField.placeholderText", "Ví dụ: Ăn trưa cùng đồng nghiệp, đổ xăng...");

        formPanel.add(createFieldHeader("Ghi chú chi tiết", false, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.noteField);
        formPanel.add(Box.createVerticalStrut(14));

        // Fee Box (Accordion Card with Balanced Dimensions & Tight Wrap)
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

        this.feeAmountField = new JTextField("0");
        styleInputField(this.feeAmountField);
        this.feeAmountField.setEnabled(false);

        this.feeCategoryCombo = new JComboBox<>();
        styleComboBox(this.feeCategoryCombo);
        this.feeCategoryCombo.setEnabled(false);
        configureCategoryCombo(this.feeCategoryCombo);

        this.feeDetailsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        this.feeDetailsPanel.setOpaque(false);
        this.feeDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.feeDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(10, 0, 2, 0)
        ));
        this.feeDetailsPanel.setVisible(false); // Ẩn mặc định, mở ra mượt mà khi tích checkbox

        JPanel feeAmountCol = new JPanel();
        feeAmountCol.setLayout(new BoxLayout(feeAmountCol, BoxLayout.Y_AXIS));
        feeAmountCol.setOpaque(false);
        JLabel feeAmtLabel = new JLabel("Số tiền phụ phí");
        feeAmtLabel.setFont(ViewConstants.FONT_STATUS);
        feeAmtLabel.setForeground(ViewConstants.COLOR_EXPENSE);
        feeAmountCol.add(feeAmtLabel);
        feeAmountCol.add(Box.createVerticalStrut(4));
        feeAmountCol.add(this.feeAmountField);

        JPanel feeCatCol = new JPanel();
        feeCatCol.setLayout(new BoxLayout(feeCatCol, BoxLayout.Y_AXIS));
        feeCatCol.setOpaque(false);
        JLabel feeCatLabel = new JLabel("Danh mục phụ phí");
        feeCatLabel.setFont(ViewConstants.FONT_STATUS);
        feeCatLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        feeCatCol.add(feeCatLabel);
        feeCatCol.add(Box.createVerticalStrut(4));
        feeCatCol.add(this.feeCategoryCombo);

        this.feeDetailsPanel.add(feeAmountCol);
        this.feeDetailsPanel.add(feeCatCol);

        this.feeCheckBox.addActionListener(e -> {
            boolean enabled = this.feeCheckBox.isSelected();
            this.feeAmountField.setEnabled(enabled);
            this.feeCategoryCombo.setEnabled(enabled);
            this.feeDetailsPanel.setVisible(enabled);
            if (!enabled) {
                this.feeAmountField.setText("0");
            }
            feeBox.revalidate();
            feeBox.repaint();
        });

        feeBox.add(this.feeCheckBox);
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

        this.saveButton = new JButton("Lưu giao dịch");
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

        // Khởi tạo trạng thái mặc định
        setSelectedTransactionType("Chi tiêu");
        populateFeeCategories();
    }

    // region Styling Helpers
    private JPanel createFieldHeader(String title, boolean required, JLabel rightHint) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
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

    private void styleTypeButton(JButton btn) {
        btn.setFont(ViewConstants.FONT_NAV_ACTIVE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("FlatLaf.style", "arc: 8");
        btn.setPreferredSize(new Dimension(0, 36));
    }

    private void styleInputField(JTextField field) {
        field.setFont(ViewConstants.FONT_BODY);
        field.setBackground(new Color(10, 10, 10));
        field.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        field.setCaretColor(ViewConstants.COLOR_PRIMARY);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        combo.setPreferredSize(new Dimension(0, 38));
        combo.putClientProperty("FlatLaf.style", "arc: 8");
    }

    private void updateTypeSwitcherUI() {
        String type = getSelectedTransactionType();
        if ("Thu nhập".equalsIgnoreCase(type)) {
            this.incomeTypeBtn.setBackground(new Color(74, 222, 128, 46));
            this.incomeTypeBtn.setForeground(ViewConstants.COLOR_INCOME);
            this.incomeTypeBtn.setBorder(BorderFactory.createLineBorder(new Color(74, 222, 128, 90), 1, true));

            this.expenseTypeBtn.setBackground(new Color(10, 10, 10));
            this.expenseTypeBtn.setForeground(ViewConstants.COLOR_TEXT_MUTED);
            this.expenseTypeBtn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        } else {
            this.expenseTypeBtn.setBackground(new Color(251, 113, 133, 46));
            this.expenseTypeBtn.setForeground(ViewConstants.COLOR_EXPENSE);
            this.expenseTypeBtn.setBorder(BorderFactory.createLineBorder(new Color(251, 113, 133, 90), 1, true));

            this.incomeTypeBtn.setBackground(new Color(10, 10, 10));
            this.incomeTypeBtn.setForeground(ViewConstants.COLOR_TEXT_MUTED);
            this.incomeTypeBtn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        }
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
        this.expenseGroups.clear();
        this.incomeGroups.clear();
        this.expenseCategories.clear();

        List<CategoryHierarchyResponseDTO> hierarchies = this.categoryController.loadCategoryHierarchy();
        if (hierarchies != null) {
            for (CategoryHierarchyResponseDTO group : hierarchies) {
                if ("INCOME".equalsIgnoreCase(group.getTransactionType())) {
                    this.incomeGroups.add(group);
                } else {
                    this.expenseGroups.add(group);
                    if (group.getCategories() != null) {
                        this.expenseCategories.addAll(group.getCategories());
                    }
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
            this.balanceHintRow.setVisible(true);
        } else {
            this.availableBalanceLabel.setText("");
            this.availableBalanceLabel.setVisible(false);
            this.balanceHintRow.setVisible(false);
        }
    }

    private void configureGroupCombo() {
        this.groupCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryHierarchyResponseDTO g) {
                    setText(g.getNeedGroupName());
                } else {
                    setText("-- Chọn nhóm danh mục --");
                    if (!isSelected) setForeground(ViewConstants.COLOR_TEXT_MUTED);
                }
                return this;
            }
        });

        this.groupCombo.addActionListener(e -> onGroupSelected());
    }

    private void onGroupSelected() {
        CategoryHierarchyResponseDTO selectedGroup = getSelectedGroup();
        this.categoryCombo.removeAllItems();

        if (selectedGroup == null || selectedGroup.getCategories() == null || selectedGroup.getCategories().isEmpty()) {
            this.categoryCombo.setEnabled(false);
        } else {
            this.categoryCombo.setEnabled(true);
            for (CategoryResponseDTO cat : selectedGroup.getCategories()) {
                this.categoryCombo.addItem(cat);
            }
            if (this.categoryCombo.getItemCount() > 0) {
                this.categoryCombo.setSelectedIndex(0);
            }
        }
    }

    private void configureCategoryCombo(JComboBox<CategoryResponseDTO> combo) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryResponseDTO cat) {
                    setText(cat.getName());
                } else {
                    setText(categoryCombo.isEnabled() ? "-- Chọn danh mục --" : "-- Vui lòng chọn nhóm trước --");
                    if (!isSelected) setForeground(ViewConstants.COLOR_TEXT_MUTED);
                }
                return this;
            }
        });
    }

    private void updateGroupsForSelectedType() {
        this.groupCombo.removeAllItems();
        this.groupCombo.addItem(null); // Item rỗng đại diện cho placeholder "-- Chọn nhóm danh mục --"

        String selectedType = getSelectedTransactionType();
        List<CategoryHierarchyResponseDTO> targetGroups = "Thu nhập".equalsIgnoreCase(selectedType)
                ? this.incomeGroups
                : this.expenseGroups;

        for (CategoryHierarchyResponseDTO g : targetGroups) {
            this.groupCombo.addItem(g);
        }
        this.groupCombo.setSelectedItem(null);

        // Khi đổi type, reset dropdown danh mục và khóa lại
        this.categoryCombo.removeAllItems();
        this.categoryCombo.setEnabled(false);
    }

    private void populateFeeCategories() {
        this.feeCategoryCombo.removeAllItems();
        for (CategoryResponseDTO cat : this.expenseCategories) {
            this.feeCategoryCombo.addItem(cat);
        }
    }

    private CategoryHierarchyResponseDTO findGroupForCategory(CategoryResponseDTO cat) {
        if (cat == null) return null;
        List<CategoryHierarchyResponseDTO> allGroups = new ArrayList<>(this.expenseGroups);
        allGroups.addAll(this.incomeGroups);
        for (CategoryHierarchyResponseDTO g : allGroups) {
            if (g.getCategories() != null) {
                for (CategoryResponseDTO c : g.getCategories()) {
                    if (c.getCategoryId().equals(cat.getCategoryId())) {
                        return g;
                    }
                }
            }
        }
        return null;
    }
    // endregion

    // region Action Handlers
    public void triggerSave() {
        WalletResponseDTO wallet = (WalletResponseDTO) this.walletCombo.getSelectedItem();
        CategoryHierarchyResponseDTO group = getSelectedGroup();
        CategoryResponseDTO category = (CategoryResponseDTO) this.categoryCombo.getSelectedItem();

        if (wallet == null) {
            handleError("Vui lòng chọn ví tài chính.");
            return;
        }
        if (group == null) {
            handleError("Vui lòng chọn nhóm danh mục.");
            return;
        }
        if (category == null) {
            handleError("Vui lòng chọn danh mục thu chi.");
            return;
        }

        String rawAmount = this.amountField.getText();
        String rawDate = this.dateField.getText();
        String rawNote = this.noteField.getText();

        String rawFeeAmount = "0";
        UUID feeCatId = null;
        if (this.feeCheckBox.isSelected()) {
            rawFeeAmount = this.feeAmountField.getText();
            CategoryResponseDTO feeCat = (CategoryResponseDTO) this.feeCategoryCombo.getSelectedItem();
            if (feeCat != null) {
                feeCatId = feeCat.getCategoryId();
            }
        }

        try {
            this.createdTransaction = this.transactionController.handleRecordTransaction(
                    wallet.getWalletId(),
                    category.getCategoryId(),
                    rawAmount,
                    rawDate,
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

    public TransactionResponseDTO getCreatedTransaction() {
        return this.createdTransaction;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    public String getSelectedTransactionType() {
        return (String) this.typeCombo.getSelectedItem();
    }

    public void setSelectedTransactionType(String type) {
        this.typeCombo.setSelectedItem(type);
        updateGroupsForSelectedType();
        updateTypeSwitcherUI();
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

    public JComboBox<CategoryHierarchyResponseDTO> getGroupCombo() {
        return this.groupCombo;
    }

    public CategoryHierarchyResponseDTO getSelectedGroup() {
        Object item = this.groupCombo.getSelectedItem();
        return (item instanceof CategoryHierarchyResponseDTO g) ? g : null;
    }

    public void setSelectedGroup(CategoryHierarchyResponseDTO group) {
        this.groupCombo.setSelectedItem(group);
    }

    public JComboBox<CategoryResponseDTO> getCategoryCombo() {
        return this.categoryCombo;
    }

    public CategoryResponseDTO getSelectedCategory() {
        return (CategoryResponseDTO) this.categoryCombo.getSelectedItem();
    }

    public void setSelectedCategory(CategoryResponseDTO category) {
        if (category == null) {
            this.categoryCombo.setSelectedItem(null);
            return;
        }
        CategoryHierarchyResponseDTO foundGroup = findGroupForCategory(category);
        if (foundGroup != null) {
            this.groupCombo.setSelectedItem(foundGroup);
        }
        this.categoryCombo.setSelectedItem(category);
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

    public void setFeeData(boolean hasFee, String feeAmount, CategoryResponseDTO feeCategory) {
        this.feeCheckBox.setSelected(hasFee);
        this.feeAmountField.setEnabled(hasFee);
        this.feeCategoryCombo.setEnabled(hasFee);
        this.feeDetailsPanel.setVisible(hasFee);
        this.feeAmountField.setText(feeAmount != null ? feeAmount : "0");
        if (feeCategory != null) {
            this.feeCategoryCombo.setSelectedItem(feeCategory);
        }
        revalidate();
        repaint();
    }

    public JButton getExpenseTypeBtn() {
        return this.expenseTypeBtn;
    }

    public JButton getIncomeTypeBtn() {
        return this.incomeTypeBtn;
    }

    public JLabel getAmountInWordsHint() {
        return this.amountInWordsHint;
    }

    public JPanel getFeeDetailsPanel() {
        return this.feeDetailsPanel;
    }

    public JTextField getFeeAmountField() {
        return this.feeAmountField;
    }

    public JComboBox<CategoryResponseDTO> getFeeCategoryCombo() {
        return this.feeCategoryCombo;
    }
    // endregion
}
