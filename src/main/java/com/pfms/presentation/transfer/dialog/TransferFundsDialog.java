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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Hộp thoại modal điều chuyển tiền giữa hai ví tài chính (UC-TRF-01).
 */
public class TransferFundsDialog extends JDialog {

    private final TransferGUIController transferController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;

    private final JComboBox<WalletResponseDTO> sourceWalletCombo;
    private final JComboBox<WalletResponseDTO> destWalletCombo;
    private final JTextField amountField;
    private final JTextField dateField;
    private final JTextField noteField;
    private final JCheckBox feeCheckBox;
    private final JTextField feeAmountField;
    private final JComboBox<CategoryResponseDTO> feeCategoryCombo;

    private final List<CategoryResponseDTO> feeCategories = new ArrayList<>();

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

        setSize(480, 520);
        setMinimumSize(new Dimension(420, 460));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        loadFeeCategories();

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        formPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Ví nguồn
        JLabel sourceLabel = new JLabel("Ví nguồn (xuất tiền) (*):");
        sourceLabel.setFont(ViewConstants.FONT_BODY);
        this.sourceWalletCombo = new JComboBox<>();
        this.sourceWalletCombo.setFont(ViewConstants.FONT_BODY);

        // Ví đích
        JLabel destLabel = new JLabel("Ví đích (nhận tiền) (*):");
        destLabel.setFont(ViewConstants.FONT_BODY);
        this.destWalletCombo = new JComboBox<>();
        this.destWalletCombo.setFont(ViewConstants.FONT_BODY);

        configureWalletCombos();

        // Số tiền
        JLabel amountLabel = new JLabel("Số tiền chuyển (*):");
        amountLabel.setFont(ViewConstants.FONT_BODY);
        this.amountField = new JTextField();
        this.amountField.setFont(ViewConstants.FONT_BODY);

        // Ngày
        JLabel dateLabel = new JLabel("Ngày chuyển tiền (yyyy-MM-dd):");
        dateLabel.setFont(ViewConstants.FONT_BODY);
        this.dateField = new JTextField(LocalDate.now().toString());
        this.dateField.setFont(ViewConstants.FONT_BODY);

        // Ghi chú
        JLabel noteLabel = new JLabel("Ghi chú:");
        noteLabel.setFont(ViewConstants.FONT_BODY);
        this.noteField = new JTextField();
        this.noteField.setFont(ViewConstants.FONT_BODY);

        // Phụ phí
        this.feeCheckBox = new JCheckBox("Có phụ phí phát sinh");
        this.feeCheckBox.setFont(ViewConstants.FONT_BODY);
        this.feeCheckBox.setBackground(ViewConstants.COLOR_CONTENT_BG);

        JLabel feeAmountLabel = new JLabel("Số tiền phí:");
        feeAmountLabel.setFont(ViewConstants.FONT_BODY);
        this.feeAmountField = new JTextField("0");
        this.feeAmountField.setFont(ViewConstants.FONT_BODY);
        this.feeAmountField.setEnabled(false);

        JLabel feeCategoryLabel = new JLabel("Danh mục phí:");
        feeCategoryLabel.setFont(ViewConstants.FONT_BODY);
        this.feeCategoryCombo = new JComboBox<>();
        this.feeCategoryCombo.setFont(ViewConstants.FONT_BODY);
        this.feeCategoryCombo.setEnabled(false);
        configureFeeCategoryCombo();

        this.feeCheckBox.addActionListener(e -> {
            boolean enabled = this.feeCheckBox.isSelected();
            this.feeAmountField.setEnabled(enabled);
            this.feeCategoryCombo.setEnabled(enabled);
            if (!enabled) {
                this.feeAmountField.setText("0");
            }
        });

        // Add to form panel
        formPanel.add(sourceLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.sourceWalletCombo);
        formPanel.add(Box.createVerticalStrut(8));

        formPanel.add(destLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.destWalletCombo);
        formPanel.add(Box.createVerticalStrut(8));

        formPanel.add(amountLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.amountField);
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

        JButton transferButton = new JButton("Xác nhận chuyển tiền");
        transferButton.setFont(ViewConstants.FONT_BODY);
        transferButton.setBackground(ViewConstants.COLOR_PRIMARY_LIGHT);
        transferButton.setForeground(ViewConstants.COLOR_STATUS_BG);
        transferButton.addActionListener(e -> triggerTransfer());

        buttonPanel.add(cancelButton);
        buttonPanel.add(transferButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFeeCategories() {
        this.feeCategories.clear();
        List<CategoryHierarchyResponseDTO> hierarchies = this.categoryController.loadCategoryHierarchy();
        if (hierarchies != null) {
            for (CategoryHierarchyResponseDTO group : hierarchies) {
                if ("EXPENSE".equalsIgnoreCase(group.getTransactionType())) {
                    this.feeCategories.addAll(group.getCategories());
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

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);

        DefaultListCellRenderer walletRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof WalletResponseDTO w) {
                    setText(w.getName() + " (" + formatter.format(w.getBalance()) + " VNĐ)");
                }
                return this;
            }
        };

        this.sourceWalletCombo.setRenderer(walletRenderer);
        this.destWalletCombo.setRenderer(walletRenderer);
    }

    private void configureFeeCategoryCombo() {
        this.feeCategoryCombo.removeAllItems();
        for (CategoryResponseDTO cat : this.feeCategories) {
            this.feeCategoryCombo.addItem(cat);
        }

        this.feeCategoryCombo.setRenderer(new DefaultListCellRenderer() {
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
    }

    public WalletResponseDTO getDestinationWallet() {
        return (WalletResponseDTO) this.destWalletCombo.getSelectedItem();
    }

    public void setDestinationWallet(WalletResponseDTO wallet) {
        this.destWalletCombo.setSelectedItem(wallet);
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

    public void setFeeData(boolean hasFee, String feeAmount, CategoryResponseDTO feeCategory) {
        this.feeCheckBox.setSelected(hasFee);
        this.feeAmountField.setEnabled(hasFee);
        this.feeCategoryCombo.setEnabled(hasFee);
        this.feeAmountField.setText(feeAmount != null ? feeAmount : "0");
        if (feeCategory != null) {
            this.feeCategoryCombo.setSelectedItem(feeCategory);
        }
    }
}
