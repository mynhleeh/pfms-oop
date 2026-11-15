package com.pfms.presentation.view.transaction.dialog;

import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.controller.TransactionGUIController;
import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.view.common.ViewConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
 */
public class UpdateTransactionDialog extends JDialog {

    private final TransactionGUIController transactionController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;
    private final TransactionResponseDTO existingTxn;

    private final JTextField amountField;
    private final JComboBox<WalletResponseDTO> walletCombo;
    private final JComboBox<CategoryResponseDTO> categoryCombo;
    private final JTextField dateField;
    private final JTextField noteField;
    private final JCheckBox feeCheckBox;
    private final JTextField feeAmountField;
    private final JComboBox<CategoryResponseDTO> feeCategoryCombo;

    private final List<CategoryResponseDTO> availableCategories = new ArrayList<>();
    private final List<CategoryResponseDTO> expenseCategories = new ArrayList<>();

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

        super(parent, "Chỉnh Sửa Giao Dịch", true);
        this.transactionController = Objects.requireNonNull(transactionController, "TransactionGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");
        this.existingTxn = Objects.requireNonNull(existingTxn, "TransactionResponseDTO không được null");

        setSize(480, 520);
        setMinimumSize(new Dimension(420, 460));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        loadCategories();

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        formPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Loại giao dịch (chỉ đọc)
        JLabel typeInfoLabel = new JLabel("Loại giao dịch: " + mapTransactionType(existingTxn.getTransactionType()));
        typeInfoLabel.setFont(ViewConstants.FONT_NAV_ACTIVE);
        typeInfoLabel.setForeground(ViewConstants.COLOR_PRIMARY);

        // Số tiền
        JLabel amountLabel = new JLabel("Số tiền (*):");
        amountLabel.setFont(ViewConstants.FONT_BODY);
        this.amountField = new JTextField(existingTxn.getAmount() != null ? existingTxn.getAmount().toPlainString() : "");
        this.amountField.setFont(ViewConstants.FONT_BODY);

        // Ví
        JLabel walletLabel = new JLabel("Ví tài chính (*):");
        walletLabel.setFont(ViewConstants.FONT_BODY);
        this.walletCombo = new JComboBox<>();
        this.walletCombo.setFont(ViewConstants.FONT_BODY);
        configureWalletCombo();

        // Danh mục
        JLabel categoryLabel = new JLabel("Danh mục (*):");
        categoryLabel.setFont(ViewConstants.FONT_BODY);
        this.categoryCombo = new JComboBox<>();
        this.categoryCombo.setFont(ViewConstants.FONT_BODY);
        configureCategoryCombo(this.categoryCombo);
        populateCategories();

        // Ngày
        JLabel dateLabel = new JLabel("Ngày giao dịch (yyyy-MM-dd):");
        dateLabel.setFont(ViewConstants.FONT_BODY);
        this.dateField = new JTextField(existingTxn.getTransactionDate() != null ? existingTxn.getTransactionDate().toString() : "");
        this.dateField.setFont(ViewConstants.FONT_BODY);

        // Ghi chú
        JLabel noteLabel = new JLabel("Ghi chú:");
        noteLabel.setFont(ViewConstants.FONT_BODY);
        this.noteField = new JTextField(existingTxn.getNote() != null ? existingTxn.getNote() : "");
        this.noteField.setFont(ViewConstants.FONT_BODY);

        // Phụ phí
        this.feeCheckBox = new JCheckBox("Có phụ phí phát sinh");
        this.feeCheckBox.setFont(ViewConstants.FONT_BODY);
        this.feeCheckBox.setBackground(ViewConstants.COLOR_CONTENT_BG);
        this.feeCheckBox.setSelected(existingTxn.hasFee());

        JLabel feeAmountLabel = new JLabel("Số tiền phí:");
        feeAmountLabel.setFont(ViewConstants.FONT_BODY);
        this.feeAmountField = new JTextField(existingTxn.hasFee() && existingTxn.getFeeAmount() != null
                ? existingTxn.getFeeAmount().toPlainString()
                : "0");
        this.feeAmountField.setFont(ViewConstants.FONT_BODY);
        this.feeAmountField.setEnabled(existingTxn.hasFee());

        JLabel feeCategoryLabel = new JLabel("Danh mục phí:");
        feeCategoryLabel.setFont(ViewConstants.FONT_BODY);
        this.feeCategoryCombo = new JComboBox<>();
        this.feeCategoryCombo.setFont(ViewConstants.FONT_BODY);
        this.feeCategoryCombo.setEnabled(existingTxn.hasFee());
        configureCategoryCombo(this.feeCategoryCombo);
        populateFeeCategories();

        this.feeCheckBox.addActionListener(e -> {
            boolean enabled = this.feeCheckBox.isSelected();
            this.feeAmountField.setEnabled(enabled);
            this.feeCategoryCombo.setEnabled(enabled);
            if (!enabled) {
                this.feeAmountField.setText("0");
            }
        });

        // Add to form panel
        formPanel.add(typeInfoLabel);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(amountLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.amountField);
        formPanel.add(Box.createVerticalStrut(8));

        formPanel.add(walletLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.walletCombo);
        formPanel.add(Box.createVerticalStrut(8));

        formPanel.add(categoryLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.categoryCombo);
        formPanel.add(Box.createVerticalStrut(8));

        formPanel.add(dateLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.dateField);
        formPanel.add(Box.createVerticalStrut(8));

        formPanel.add(noteLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.noteField);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(this.feeCheckBox);
        formPanel.add(Box.createVerticalStrut(6));

        JPanel feeRow = new JPanel(new GridLayout(1, 2, 8, 0));
        feeRow.setBackground(ViewConstants.COLOR_CONTENT_BG);
        JPanel feeAmountCol = new JPanel(new BorderLayout(0, 4));
        feeAmountCol.setBackground(ViewConstants.COLOR_CONTENT_BG);
        feeAmountCol.add(feeAmountLabel, BorderLayout.NORTH);
        feeAmountCol.add(this.feeAmountField, BorderLayout.CENTER);

        JPanel feeCatCol = new JPanel(new BorderLayout(0, 4));
        feeCatCol.setBackground(ViewConstants.COLOR_CONTENT_BG);
        feeCatCol.add(feeCategoryLabel, BorderLayout.NORTH);
        feeCatCol.add(this.feeCategoryCombo, BorderLayout.CENTER);

        feeRow.add(feeAmountCol);
        feeRow.add(feeCatCol);
        formPanel.add(feeRow);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        JButton cancelButton = new JButton("Hủy bỏ");
        cancelButton.setFont(ViewConstants.FONT_BODY);
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Lưu thay đổi");
        saveButton.setFont(ViewConstants.FONT_BODY);
        saveButton.setBackground(ViewConstants.COLOR_PRIMARY_LIGHT);
        saveButton.setForeground(ViewConstants.COLOR_STATUS_BG);
        saveButton.addActionListener(e -> triggerSave());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCategories() {
        this.availableCategories.clear();
        this.expenseCategories.clear();

        List<CategoryHierarchyResponseDTO> hierarchies = this.categoryController.loadCategoryHierarchy();
        if (hierarchies != null) {
            for (CategoryHierarchyResponseDTO group : hierarchies) {
                if ("EXPENSE".equalsIgnoreCase(group.getTransactionType())) {
                    this.expenseCategories.addAll(group.getCategories());
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
        WalletResponseDTO match = null;
        if (summary != null && summary.getWallets() != null) {
            for (WalletResponseDTO w : summary.getWallets()) {
                if ("ACTIVE".equalsIgnoreCase(w.getStatus()) || w.getWalletId().equals(this.existingTxn.getWalletId())) {
                    this.walletCombo.addItem(w);
                    if (w.getWalletId().equals(this.existingTxn.getWalletId())) {
                        match = w;
                    }
                }
            }
        }
        if (match != null) {
            this.walletCombo.setSelectedItem(match);
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);

        this.walletCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof WalletResponseDTO w) {
                    setText(w.getName() + " (" + formatter.format(w.getBalance()) + " VNĐ)");
                }
                return this;
            }
        });
    }

    private void configureCategoryCombo(JComboBox<CategoryResponseDTO> combo) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryResponseDTO cat) {
                    setText(cat.getName() + " [" + cat.getParentGroupName() + "]");
                }
                return this;
            }
        });
    }

    private void populateCategories() {
        this.categoryCombo.removeAllItems();
        CategoryResponseDTO match = null;
        for (CategoryResponseDTO cat : this.availableCategories) {
            this.categoryCombo.addItem(cat);
            if (cat.getCategoryId().equals(this.existingTxn.getCategoryId())) {
                match = cat;
            }
        }
        if (match != null) {
            this.categoryCombo.setSelectedItem(match);
        }
    }

    private void populateFeeCategories() {
        this.feeCategoryCombo.removeAllItems();
        CategoryResponseDTO match = null;
        for (CategoryResponseDTO cat : this.expenseCategories) {
            this.feeCategoryCombo.addItem(cat);
            if (this.existingTxn.hasFee() && cat.getCategoryId().equals(this.existingTxn.getFeeCategoryId())) {
                match = cat;
            }
        }
        if (match != null) {
            this.feeCategoryCombo.setSelectedItem(match);
        }
    }

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

    private String mapTransactionType(String type) {
        if (type == null) return "Không xác định";
        return switch (type.toUpperCase(Locale.ROOT)) {
            case "INCOME" -> "Thu nhập";
            case "EXPENSE" -> "Chi tiêu";
            case "ADJUSTMENT" -> "Kiểm kê";
            default -> type;
        };
    }

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

    public WalletResponseDTO getSelectedWallet() {
        return (WalletResponseDTO) this.walletCombo.getSelectedItem();
    }

    public void setSelectedWallet(WalletResponseDTO wallet) {
        this.walletCombo.setSelectedItem(wallet);
    }

    public CategoryResponseDTO getSelectedCategory() {
        return (CategoryResponseDTO) this.categoryCombo.getSelectedItem();
    }

    public void setSelectedCategory(CategoryResponseDTO category) {
        this.categoryCombo.setSelectedItem(category);
    }
}
