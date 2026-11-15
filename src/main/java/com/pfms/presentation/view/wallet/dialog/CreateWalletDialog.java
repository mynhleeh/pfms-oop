package com.pfms.presentation.view.wallet.dialog;

import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.view.common.ViewConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Objects;

/**
 * Hộp thoại modal tạo ví tài chính mới (UC-WAL-01).
 */
public class CreateWalletDialog extends JDialog {

    private final WalletGUIController walletController;
    private final JTextField nameField;
    private final JTextField noteField;
    private boolean success = false;
    private WalletResponseDTO createdWallet = null;
    private String errorMessage = null;
    private java.util.function.Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Lỗi tạo ví",
                JOptionPane.ERROR_MESSAGE);
    };

    public CreateWalletDialog(JFrame parent, WalletGUIController walletController) {
        super(parent, "Thêm Ví Tài Chính Mới", true);
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được để null");

        setSize(420, 240);
        setMinimumSize(new Dimension(380, 220));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        formPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);

        JLabel nameLabel = new JLabel("Tên ví (*):");
        nameLabel.setFont(ViewConstants.FONT_BODY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.nameField = new JTextField();
        this.nameField.setFont(ViewConstants.FONT_BODY);
        this.nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel noteLabel = new JLabel("Ghi chú:");
        noteLabel.setFont(ViewConstants.FONT_BODY);
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.noteField = new JTextField();
        this.noteField.setFont(ViewConstants.FONT_BODY);
        this.noteField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.noteField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.nameField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(noteLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(this.noteField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        JButton cancelButton = new JButton("Hủy bỏ");
        cancelButton.setFont(ViewConstants.FONT_BODY);
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Lưu ví");
        saveButton.setFont(ViewConstants.FONT_BODY);
        saveButton.setBackground(ViewConstants.COLOR_PRIMARY_LIGHT);
        saveButton.setForeground(ViewConstants.COLOR_STATUS_BG);
        saveButton.addActionListener(e -> triggerSave());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void triggerSave() {
        String name = this.nameField.getText();
        String note = this.noteField.getText();

        try {
            this.createdWallet = this.walletController.handleCreateWallet(name, note);
            this.success = true;
            this.errorMessage = null;
            dispose();
        } catch (Exception ex) {
            this.errorMessage = ex.getMessage();
            this.success = false;
            if (this.errorConsumer != null) {
                this.errorConsumer.accept(ex.getMessage());
            }
        }
    }

    public void setErrorConsumer(java.util.function.Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public WalletResponseDTO getCreatedWallet() {
        return this.createdWallet;
    }

    public String getNameInputValue() {
        return this.nameField.getText();
    }

    public void setNameInputValue(String name) {
        this.nameField.setText(name);
    }

    public String getNoteInputValue() {
        return this.noteField.getText();
    }

    public void setNoteInputValue(String note) {
        this.noteField.setText(note);
    }
}
