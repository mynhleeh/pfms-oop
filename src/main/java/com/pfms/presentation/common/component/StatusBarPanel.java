package com.pfms.presentation.common.component;

import com.pfms.presentation.common.ViewConstants;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Thanh trạng thái thông tin hệ thống (BorderLayout.SOUTH).
 * Hiển thị tệp lưu trữ đang làm việc theo chuẩn giao diện Luxury Black &amp; Gold.
 */
public class StatusBarPanel extends JPanel {

    private final JLabel dataPathLabel;
    private final JLabel totalBalanceLabel;
    private final DecimalFormat currencyFormatter;

    public StatusBarPanel(String initialDataPath) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(ViewConstants.APP_WIDTH, ViewConstants.STATUS_BAR_HEIGHT));
        setBackground(ViewConstants.COLOR_STATUS_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(0, 16, 0, 16)));

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);

        this.dataPathLabel = new JLabel();
        this.dataPathLabel.setFont(ViewConstants.FONT_STATUS);
        this.dataPathLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        this.totalBalanceLabel = new JLabel();
        this.totalBalanceLabel.setFont(ViewConstants.FONT_STATUS);
        this.totalBalanceLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);

        updateDataFilePath(initialDataPath);
        updateTotalBalance(BigDecimal.ZERO, ViewConstants.CURRENCY_DEFAULT);

        add(this.dataPathLabel, BorderLayout.WEST);
        // Lưu ý: Không add totalBalanceLabel vào panel layout để không hiển thị tổng số dư ở thanh trạng thái.
        // Vẫn duy trì totalBalanceLabel và các phương thức cập nhật phục vụ tương thích ngược 100% với callers và test suites.
    }

    public void updateDataFilePath(String path) {
        String displayPath = (path != null && !path.trim().isEmpty()) ? path.trim() : "Chưa kết nối tệp";
        this.dataPathLabel.setText("● Tệp dữ liệu: " + displayPath);
    }

    public void updateTotalBalance(BigDecimal amount, String currency) {
        BigDecimal safeAmount = (amount != null) ? amount : BigDecimal.ZERO;
        String cur = (currency != null && !currency.trim().isEmpty()) ? currency.trim() : ViewConstants.CURRENCY_DEFAULT;
        this.totalBalanceLabel.setText("Tổng số dư: " + this.currencyFormatter.format(safeAmount) + " " + cur);
    }

    public String getDataFilePathText() {
        return this.dataPathLabel.getText();
    }

    public String getTotalBalanceText() {
        return this.totalBalanceLabel.getText();
    }

    public JLabel getDataPathLabel() {
        return this.dataPathLabel;
    }

    public JLabel getTotalBalanceLabel() {
        return this.totalBalanceLabel;
    }
}
