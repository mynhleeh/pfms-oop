package com.pfms.presentation.transfer.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.transfer.TransferGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
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
 * Hộp thoại modal điều chuyển tiền giữa hai ví tài chính (UC-TRF-01).
 * Thiết kế Luxury Black &amp; Gold bám sát tuyệt đối preview-transfer.html và RecordTransactionDialog:
 * - Header màu than chì #0E0E0E với tiêu đề Champagne Gold #D4AF37
 * - Toàn bộ label và control căn lề trái chuẩn xác (Left-aligned)
 * - Input field và combobox chiều cao chuẩn 38px, bo góc mượt mà
 * - Live currency preview hint hiển thị số tiền định dạng thời gian thực
 * - Nhãn số dư khả dụng và số dư hiện tại hiển thị ngay bên dưới mỗi dropdown ví
 * - Khung phụ phí phát sinh dạng Accordion Card bo tròn viền mịn, tự động ẩn hiện khi tích chọn
 * - Footer nút bấm bo góc 8px với nút "Xác nhận chuyển tiền" nổi bật màu vàng Gold.
 */
public class TransferFundsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final TransferGUIController transferController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;

    private final JComboBox<WalletResponseDTO> sourceWalletCombo;
    private final JLabel sourceBalanceHintLabel;
    private final JComboBox<WalletResponseDTO> destWalletCombo;
    private final JLabel destBalanceHintLabel;

    private final JTextField amountField;
    private final JLabel amountInWordsHint;
    private final JTextField dateField;
    private final JTextField noteField;

    private final JCheckBox feeCheckBox;
    private final JPanel feeDetailsPanel;
    private final JTextField feeAmountField;
    private final JComboBox<CategoryHierarchyResponseDTO> feeGroupCombo;
    private final JComboBox<CategoryResponseDTO> feeCategoryCombo;

    private final JButton cancelButton;
    private final JButton transferButton;

    private final List<CategoryHierarchyResponseDTO> feeExpenseGroups = new ArrayList<>();

    private final DecimalFormat currencyFormatter;

    private boolean success = false;
    private TransferResponseDTO createdTransfer = null;
    private String errorMessage = null;
    private Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Lỗi chuyển tiền", JOptionPane.ERROR_MESSAGE);
    };

    public TransferFundsDialog(
            JFrame parent,
            TransferGUIController transferController,
            WalletGUIController walletController,
            CategoryGUIController categoryController) {

        super(parent, "Chuyển Tiền Nội Bộ", true);
        this.transferController = Objects.requireNonNull(transferController, "TransferGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);

        setSize(520, 680);
        setMinimumSize(new Dimension(480, 600));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ViewConstants.COLOR_CARD_BG);

        loadFeeCategories();

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(14, 14, 14));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(14, 22, 14, 22)
        ));
        JLabel titleLabel = new JLabel("Chuyển Tiền Nội Bộ");
        titleLabel.setFont(ViewConstants.FONT_TITLE);
        titleLabel.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Form Body Panel inside ScrollPane
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        formPanel.setBackground(ViewConstants.COLOR_CARD_BG);

        // Ví nguồn
        this.sourceWalletCombo = new JComboBox<>();
        styleComboBox(this.sourceWalletCombo);

        this.sourceBalanceHintLabel = new JLabel("Số dư khả dụng: 0 VNĐ");
        this.sourceBalanceHintLabel.setFont(ViewConstants.FONT_STATUS);
        this.sourceBalanceHintLabel.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.sourceBalanceHintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel sourceHintBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        sourceHintBox.setOpaque(false);
        sourceHintBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        sourceHintBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        sourceHintBox.add(this.sourceBalanceHintLabel);

        formPanel.add(createFieldHeader("Ví nguồn (xuất tiền)", true, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.sourceWalletCombo);
        formPanel.add(sourceHintBox);
        formPanel.add(Box.createVerticalStrut(10));

        // Ví đích
        this.destWalletCombo = new JComboBox<>();
        styleComboBox(this.destWalletCombo);

        this.destBalanceHintLabel = new JLabel("Số dư hiện tại: 0 VNĐ");
        this.destBalanceHintLabel.setFont(ViewConstants.FONT_STATUS);
        this.destBalanceHintLabel.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.destBalanceHintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel destHintBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        destHintBox.setOpaque(false);
        destHintBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        destHintBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        destHintBox.add(this.destBalanceHintLabel);

        configureWalletCombos();

        formPanel.add(createFieldHeader("Ví đích (nhận tiền)", true, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.destWalletCombo);
        formPanel.add(destHintBox);
        formPanel.add(Box.createVerticalStrut(10));

        // Số tiền chuyển kèm live hint
        this.amountInWordsHint = new JLabel("0 VNĐ");
        this.amountInWordsHint.setFont(ViewConstants.FONT_STATUS);
        this.amountInWordsHint.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);

        this.amountField = new JTextField();
        styleInputField(this.amountField);
        this.amountField.putClientProperty("JTextField.placeholderText", "Ví dụ: 5000000");
        this.amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateAmountHint(); }
            @Override public void removeUpdate(DocumentEvent e) { updateAmountHint(); }
            @Override public void changedUpdate(DocumentEvent e) { updateAmountHint(); }
        });

        formPanel.add(createFieldHeader("Số tiền chuyển (VNĐ)", true, this.amountInWordsHint));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.amountField);
        formPanel.add(Box.createVerticalStrut(14));

        // Ngày chuyển tiền
        this.dateField = new JTextField(LocalDate.now().toString());
        styleInputField(this.dateField);
        this.dateField.putClientProperty("JTextField.placeholderText", "yyyy-MM-dd");

        formPanel.add(createFieldHeader("Ngày chuyển tiền (yyyy-MM-dd)", true, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.dateField);
        formPanel.add(Box.createVerticalStrut(14));

        // Ghi chú
        this.noteField = new JTextField();
        styleInputField(this.noteField);
        this.noteField.putClientProperty("JTextField.placeholderText", "Ví dụ: Chuyển tiền tiết kiệm, nạp tiền tiêu dùng...");

        formPanel.add(createFieldHeader("Ghi chú", false, null));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.noteField);
        formPanel.add(Box.createVerticalStrut(14));

        // 3. Khung Phụ phí phát sinh dạng Accordion Card
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

        this.feeGroupCombo = new JComboBox<>();
        styleComboBox(this.feeGroupCombo);
        this.feeGroupCombo.setEnabled(false);

        this.feeCategoryCombo = new JComboBox<>();
        styleComboBox(this.feeCategoryCombo);
        this.feeCategoryCombo.setEnabled(false);

        configureFeeGroupCombo();
        configureFeeCategoryCombo();

        this.feeDetailsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        this.feeDetailsPanel.setOpaque(false);
        this.feeDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.feeDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(10, 0, 2, 0)
        ));
        this.feeDetailsPanel.setVisible(false); // Ẩn mặc định, mở ra mượt mà khi tích chọn

        JPanel feeAmountCol = new JPanel();
        feeAmountCol.setLayout(new BoxLayout(feeAmountCol, BoxLayout.Y_AXIS));
        feeAmountCol.setOpaque(false);
        JLabel feeAmtLabel = new JLabel("Số tiền phí (*)");
        feeAmtLabel.setFont(ViewConstants.FONT_STATUS);
        feeAmtLabel.setForeground(ViewConstants.COLOR_EXPENSE);
        feeAmountCol.add(feeAmtLabel);
        feeAmountCol.add(Box.createVerticalStrut(4));
        feeAmountCol.add(this.feeAmountField);

        JPanel feeGroupCol = new JPanel();
        feeGroupCol.setLayout(new BoxLayout(feeGroupCol, BoxLayout.Y_AXIS));
        feeGroupCol.setOpaque(false);
        JLabel feeGrpLabel = new JLabel("Nhóm chi phí (*)");
        feeGrpLabel.setFont(ViewConstants.FONT_STATUS);
        feeGrpLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        feeGroupCol.add(feeGrpLabel);
        feeGroupCol.add(Box.createVerticalStrut(4));
        feeGroupCol.add(this.feeGroupCombo);

        JPanel feeCatCol = new JPanel();
        feeCatCol.setLayout(new BoxLayout(feeCatCol, BoxLayout.Y_AXIS));
        feeCatCol.setOpaque(false);
        JLabel feeCatLabel = new JLabel("Danh mục phí (*)");
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
                onFeeGroupSelected();
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

        // 4. Footer Button Panel
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

        this.transferButton = new JButton("Xác nhận chuyển tiền");
        this.transferButton.setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.transferButton.setBackground(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.transferButton.setForeground(Color.BLACK);
        this.transferButton.setFocusPainted(false);
        this.transferButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.transferButton.putClientProperty("FlatLaf.style", "arc: 8");
        this.transferButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        this.transferButton.addActionListener(e -> triggerTransfer());

        buttonPanel.add(this.cancelButton);
        buttonPanel.add(this.transferButton);
        add(buttonPanel, BorderLayout.SOUTH);
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
                this.amountInWordsHint.setText(this.currencyFormatter.format(val) + " VNĐ");
            }
        } catch (Exception ex) {
            this.amountInWordsHint.setText("0 VNĐ");
        }
    }
    // endregion

    // region Data Loading & Binding
    private void loadFeeCategories() {
        this.feeExpenseGroups.clear();
        List<CategoryHierarchyResponseDTO> hierarchies = this.categoryController.loadCategoryHierarchy();
        if (hierarchies != null) {
            for (CategoryHierarchyResponseDTO group : hierarchies) {
                if ("EXPENSE".equalsIgnoreCase(group.getTransactionType())) {
                    this.feeExpenseGroups.add(group);
                }
            }
        }
    }

    private void configureWalletCombos() {
        WalletsSummaryResponseDTO summary = this.walletController.loadWalletsSummary();
        this.sourceWalletCombo.removeAllItems();
        this.destWalletCombo.removeAllItems();

        if (summary != null && summary.getWallets() != null) {
            for (WalletResponseDTO w : summary.getWallets()) {
                if ("ACTIVE".equalsIgnoreCase(w.getStatus())) {
                    this.sourceWalletCombo.addItem(w);
                    this.destWalletCombo.addItem(w);
                }
            }
        }

        if (this.destWalletCombo.getItemCount() > 1) {
            this.destWalletCombo.setSelectedIndex(1);
        }

        DefaultListCellRenderer walletRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof WalletResponseDTO w) {
                    setText(w.getName());
                }
                return this;
            }
        };

        this.sourceWalletCombo.setRenderer(walletRenderer);
        this.destWalletCombo.setRenderer(walletRenderer);

        this.sourceWalletCombo.addActionListener(e -> updateWalletBalanceHints());
        this.destWalletCombo.addActionListener(e -> updateWalletBalanceHints());
        updateWalletBalanceHints();
    }

    private void updateWalletBalanceHints() {
        WalletResponseDTO src = (WalletResponseDTO) this.sourceWalletCombo.getSelectedItem();
        if (src != null && src.getBalance() != null) {
            this.sourceBalanceHintLabel.setText("Số dư khả dụng: " + this.currencyFormatter.format(src.getBalance()) + " VNĐ");
        } else {
            this.sourceBalanceHintLabel.setText("Số dư khả dụng: 0 VNĐ");
        }

        WalletResponseDTO dst = (WalletResponseDTO) this.destWalletCombo.getSelectedItem();
        if (dst != null && dst.getBalance() != null) {
            this.destBalanceHintLabel.setText("Số dư hiện tại: " + this.currencyFormatter.format(dst.getBalance()) + " VNĐ");
        } else {
            this.destBalanceHintLabel.setText("Số dư hiện tại: 0 VNĐ");
        }
    }

    private void configureFeeGroupCombo() {
        this.feeGroupCombo.removeAllItems();
        this.feeGroupCombo.addItem(null);
        for (CategoryHierarchyResponseDTO group : this.feeExpenseGroups) {
            this.feeGroupCombo.addItem(group);
        }

        this.feeGroupCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryHierarchyResponseDTO g) {
                    setText(g.getNeedGroupName());
                } else {
                    setText("-- Chọn nhóm chi phí --");
                }
                return this;
            }
        });

        this.feeGroupCombo.addActionListener(e -> onFeeGroupSelected());
    }

    private void onFeeGroupSelected() {
        CategoryHierarchyResponseDTO selectedGroup = (CategoryHierarchyResponseDTO) this.feeGroupCombo.getSelectedItem();
        this.feeCategoryCombo.removeAllItems();
        if (selectedGroup != null && selectedGroup.getCategories() != null && !selectedGroup.getCategories().isEmpty()) {
            for (CategoryResponseDTO cat : selectedGroup.getCategories()) {
                if ("ACTIVE".equalsIgnoreCase(cat.getStatus())) {
                    this.feeCategoryCombo.addItem(cat);
                }
            }
            this.feeCategoryCombo.setEnabled(this.feeCheckBox.isSelected());
        } else {
            this.feeCategoryCombo.setEnabled(false);
        }
    }

    private void configureFeeCategoryCombo() {
        this.feeCategoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryResponseDTO cat) {
                    setText(cat.getName());
                } else {
                    setText("-- Vui lòng chọn nhóm trước --");
                }
                return this;
            }
        });
        onFeeGroupSelected();
    }
    // endregion

    // region Action Handlers
    public void triggerTransfer() {
        WalletResponseDTO sourceWallet = (WalletResponseDTO) this.sourceWalletCombo.getSelectedItem();
        WalletResponseDTO destWallet = (WalletResponseDTO) this.destWalletCombo.getSelectedItem();

        if (sourceWallet == null || destWallet == null) {
            handleError("Vui lòng chọn ví nguồn và ví đích.");
            return;
        }

        if (sourceWallet.getWalletId().equals(destWallet.getWalletId())) {
            handleError("Ví nguồn và ví đích không được trùng nhau.");
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
            } else {
                handleError("Vui lòng chọn nhóm và danh mục phụ phí.");
                return;
            }
        }

        try {
            this.createdTransfer = this.transferController.handleTransferFunds(
                    sourceWallet.getWalletId(),
                    destWallet.getWalletId(),
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

    public TransferResponseDTO getCreatedTransfer() {
        return this.createdTransfer;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    public WalletResponseDTO getSourceWallet() {
        return (WalletResponseDTO) this.sourceWalletCombo.getSelectedItem();
    }

    public void setSourceWallet(WalletResponseDTO wallet) {
        this.sourceWalletCombo.setSelectedItem(wallet);
        updateWalletBalanceHints();
    }

    public WalletResponseDTO getDestinationWallet() {
        return (WalletResponseDTO) this.destWalletCombo.getSelectedItem();
    }

    public void setDestinationWallet(WalletResponseDTO wallet) {
        this.destWalletCombo.setSelectedItem(wallet);
        updateWalletBalanceHints();
    }

    public String getAmountInputValue() {
        return this.amountField.getText();
    }

    public void setAmountInputValue(String amount) {
        this.amountField.setText(amount);
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

    public JComboBox<CategoryHierarchyResponseDTO> getFeeGroupCombo() {
        return this.feeGroupCombo;
    }

    public JComboBox<CategoryResponseDTO> getFeeCategoryCombo() {
        return this.feeCategoryCombo;
    }

    public JCheckBox getFeeCheckBox() {
        return this.feeCheckBox;
    }

    public JTextField getFeeAmountField() {
        return this.feeAmountField;
    }

    public JPanel getFeeDetailsPanel() {
        return this.feeDetailsPanel;
    }

    public JLabel getSourceBalanceHintLabel() {
        return this.sourceBalanceHintLabel;
    }

    public JLabel getDestBalanceHintLabel() {
        return this.destBalanceHintLabel;
    }

    public JLabel getAmountInWordsHint() {
        return this.amountInWordsHint;
    }

    public JButton getCancelButton() {
        return this.cancelButton;
    }

    public JButton getTransferButton() {
        return this.transferButton;
    }

    public void setFeeData(boolean hasFee, String feeAmount, CategoryResponseDTO feeCategory) {
        this.feeCheckBox.setSelected(hasFee);
        this.feeAmountField.setEnabled(hasFee);
        this.feeGroupCombo.setEnabled(hasFee);
        this.feeDetailsPanel.setVisible(hasFee);
        this.feeAmountField.setText(feeAmount != null ? feeAmount : "0");
        if (hasFee && feeCategory != null) {
            for (int i = 0; i < this.feeGroupCombo.getItemCount(); i++) {
                CategoryHierarchyResponseDTO g = this.feeGroupCombo.getItemAt(i);
                if (g != null && g.getNeedGroupId().equals(feeCategory.getParentGroupId())) {
                    this.feeGroupCombo.setSelectedItem(g);
                    break;
                }
            }
            this.feeCategoryCombo.setSelectedItem(feeCategory);
            this.feeCategoryCombo.setEnabled(true);
        } else {
            this.feeCategoryCombo.setEnabled(false);
        }
        revalidate();
        repaint();
    }
    // endregion
}
