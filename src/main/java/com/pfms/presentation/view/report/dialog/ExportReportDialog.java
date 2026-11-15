package com.pfms.presentation.view.report.dialog;

import com.pfms.presentation.view.common.ViewConstants;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Hộp thoại modal hiển thị và sao chép văn bản báo cáo tài chính đã xuất (UC-REP-03).
 */
public class ExportReportDialog extends JDialog {

    private final String reportContent;
    private final JTextArea reportTextArea;

    private Consumer<String> messageConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    };

    public ExportReportDialog(JFrame parent, String title, String reportContent) {
        super(parent, title != null ? title : "Xuất Báo cáo Tài chính", true);
        this.reportContent = Objects.requireNonNull(reportContent, "Nội dung báo cáo không được để null");

        setSize(720, 520);
        setMinimumSize(new Dimension(500, 380));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        this.reportTextArea = new JTextArea(this.reportContent);
        this.reportTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        this.reportTextArea.setEditable(false);
        this.reportTextArea.setBackground(ViewConstants.COLOR_STATUS_BG);
        this.reportTextArea.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.reportTextArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(this.reportTextArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 10, 15),
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        buttonPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        JButton copyButton = new JButton("Sao chép vào Clipboard");
        copyButton.setFont(ViewConstants.FONT_BODY);
        copyButton.setBackground(ViewConstants.COLOR_PRIMARY_LIGHT);
        copyButton.setForeground(ViewConstants.COLOR_STATUS_BG);
        copyButton.setFocusPainted(false);
        copyButton.addActionListener(e -> triggerCopy());

        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(ViewConstants.FONT_BODY);
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(copyButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void triggerCopy() {
        try {
            StringSelection selection = new StringSelection(this.reportContent);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        } catch (Exception ignored) {
            // bỏ qua lỗi clipboard trong môi trường headless
        }
        this.messageConsumer.accept("Đã sao chép nội dung báo cáo vào bộ nhớ tạm (Clipboard)!");
    }

    public String getReportText() {
        return this.reportContent;
    }

    public void setMessageConsumer(Consumer<String> consumer) {
        this.messageConsumer = Objects.requireNonNull(consumer);
    }
}
