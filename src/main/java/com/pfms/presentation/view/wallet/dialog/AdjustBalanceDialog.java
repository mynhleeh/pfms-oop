package com.pfms.presentation.view.wallet.dialog;

import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.wallet.BalanceAdjustmentResponseDTO;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Hộp thoại modal kiểm kê điều chỉnh số dư ví thực tế (UC-WAL-04).
 */
public class AdjustBalanceDialog extends JDialog {

    private final WalletGUIController walletController;
    private final WalletResponseDTO wallet;
    private final JTextField actualBalanceField;
    private final JTextField reasonField;
    private final JTextField dateField;
    private boolean success = false;
    private BalanceAdjustmentResponseDTO adjustmentResponse = null;
    private String errorMessage = null;
    private Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Lỗi kiểm kê số dư",
                JOptionPane.ERROR_MESSAGE);
    };
    private Consumer<String> infoConsumer = msg -> {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Kết quả kiểm kê",
                JOptionPane.INFORMATION_MESSAGE);
    };

    public AdjustBalanceDialog(JFrame parent, WalletGUIController walletController, WalletResponseDTO wallet) {
        super(parent, "Kiểm Kê Điều Chỉnh Số Dư Ví", true);
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được để null");
        this.wallet = Objects.requireNonNull(wallet, "WalletResponseDTO không được để null");

        setSize(440, 320);
        setMinimumSize(new Dimension(400, 280));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        formPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);

        JLabel currentInfoLabel = new JLabel("Ví: " + wallet.getName() +
                " | Số dư sổ sách: " + formatter.format(wallet.getBalance()) + " VNĐ");
        currentInfoLabel.setFont(ViewConstants.FONT_NAV_ACTIVE);
        currentInfoLabel.setForeground(ViewConstants.COLOR_PRIMARY);
        currentInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel actualBalanceLabel = new JLabel("Số dư thực tế quan sát được (*):");
        actualBalanceLabel.setFont(ViewConstants.FONT_BODY);
        actualBalanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.actualBalanceField = new JTextField();
        this.actualBalanceField.setFont(ViewConstants.FONT_BODY);
        this.actualBalanceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.actualBalanceField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel reasonLabel = new JLabel("Lý do điều chỉnh (*):");
        reasonLabel.setFont(ViewConstants.FONT_BODY);
        reasonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.reasonField = new JTextField();
        this.reasonField.setFont(ViewConstants.FONT_BODY);
        this.reasonField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.reasonField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel("Ngày kiểm kê (dd/MM/yyyy hoặc yyyy-MM-dd):");
        dateLabel.setFont(ViewConstants.FONT_BODY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.dateField = new JTextField(LocalDate.now().toString());
        this.dateField.setFont(ViewConstants.FONT_BODY);
        this.dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.dateField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(currentInfoLabel);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(actualBalanceLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.actualBalanceField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(reasonLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.reasonField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(dateLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.dateField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        JButton cancelButton = new JButton("Hủy bỏ");
        cancelButton.setFont(ViewConstants.FONT_BODY);
        cancelButton.addActionListener(e -> dispose());

        JButton adjustButton = new JButton("Xác nhận kiểm kê");
        adjustButton.setFont(ViewConstants.FONT_BODY);
        adjustButton.setBackground(ViewConstants.COLOR_PRIMARY_LIGHT);
        adjustButton.setForeground(ViewConstants.COLOR_STATUS_BG);
        adjustButton.addActionListener(e -> triggerAdjust());

        buttonPanel.add(cancelButton);
        buttonPanel.add(adjustButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void triggerAdjust() {
        String rawBalance = this.actualBalanceField.getText();
        String reason = this.reasonField.getText();
        String date = this.dateField.getText();

        try {
            this.adjustmentResponse = this.walletController.handleAdjustBalance(
                    this.wallet.getWalletId(), rawBalance, reason, date);
            this.success = true;
            this.errorMessage = null;
            if (this.infoConsumer != null) {
                this.infoConsumer.accept("Kiểm kê số dư thành công!\nChênh lệch điều chỉnh: " +
                        this.adjustmentResponse.getDifference() + " VNĐ");
            }
            dispose();
        } catch (Exception ex) {
            this.errorMessage = ex.getMessage();
            this.success = false;
            if (this.errorConsumer != null) {
                this.errorConsumer.accept(ex.getMessage());
            }
        }
    }

    public boolean isSuccess() {
        return this.success;
    }

    public BalanceAdjustmentResponseDTO getAdjustmentResponse() {
        return this.adjustmentResponse;
    }

    public void setActualBalanceInputValue(String balance) {
        this.actualBalanceField.setText(balance);
    }

    public void setReasonInputValue(String reason) {
        this.reasonField.setText(reason);
    }

    public String getDateInputValue() {
        return this.dateField.getText();
    }

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    public void setInfoConsumer(Consumer<String> infoConsumer) {
        this.infoConsumer = infoConsumer;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
