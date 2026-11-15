package com.pfms.presentation.view.transfer;

import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.controller.TransferGUIController;
import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.util.InputValidator;
import com.pfms.presentation.view.common.ViewConstants;
import com.pfms.presentation.view.transfer.dialog.TransferFundsDialog;
import com.pfms.presentation.view.transfer.model.TransferTableModel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Chuyển tiền Nội bộ (CARD_TRANSFERS).
 * Quản lý danh sách, tìm kiếm, lọc theo ví nguồn/đích, phân trang và tạo lệnh chuyển tiền.
 */
public class TransferPanel extends JPanel {

    private final TransferGUIController transferController;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;

    private final TransferTableModel tableModel;
    private final JTable table;

    private final JComboBox<Object> sourceWalletBox;
    private final JComboBox<Object> destWalletBox;
    private final JTextField startDateField;
    private final JTextField endDateField;

    private final JLabel pageInfoLabel;
    private final JButton prevButton;
    private final JButton nextButton;
    private final JLabel summaryLabel;

    private final DecimalFormat currencyFormatter;
    private Runnable onTransferChangedListener;

    private int currentPage = 1;
    private int pageSize = 20;
    private int totalPages = 1;
    private long totalRecords = 0;

    private Consumer<String> messageConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    };
    private Consumer<String> warningConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    };
    private Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    };
    private Predicate<String> confirmConsumer = msg -> {
        int confirm = JOptionPane.showConfirmDialog(
                this, msg, "Xác nhận thao tác", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return confirm == JOptionPane.YES_OPTION;
    };

    public TransferPanel(
            TransferGUIController transferController,
            WalletGUIController walletController,
            CategoryGUIController categoryController) {

        this.transferController = Objects.requireNonNull(transferController, "TransferGUIController không được null");
        this.walletController = Objects.requireNonNull(walletController, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được null");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);

        setLayout(new BorderLayout(10, 10));
        setBackground(ViewConstants.COLOR_CONTENT_BG);

        this.tableModel = new TransferTableModel();
        this.table = new JTable(this.tableModel);
        setupTable();

        this.sourceWalletBox = new JComboBox<>();
        this.sourceWalletBox.setFont(ViewConstants.FONT_BODY);

        this.destWalletBox = new JComboBox<>();
        this.destWalletBox.setFont(ViewConstants.FONT_BODY);

        this.startDateField = new JTextField(8);
        this.startDateField.setFont(ViewConstants.FONT_BODY);

        this.endDateField = new JTextField(8);
        this.endDateField.setFont(ViewConstants.FONT_BODY);

        this.pageInfoLabel = new JLabel("Trang 1 / 1 (Tổng 0 giao dịch)");
        this.pageInfoLabel.setFont(ViewConstants.FONT_BODY);

        this.prevButton = new JButton("< Trước");
        this.prevButton.setFont(ViewConstants.FONT_BODY);

        this.nextButton = new JButton("Sau >");
        this.nextButton.setFont(ViewConstants.FONT_BODY);

        this.summaryLabel = new JLabel("Tổng tiền chuyển: 0 VNĐ | Tổng phí: 0 VNĐ");
        this.summaryLabel.setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.summaryLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);

        initViews();
        loadWallets();
        reloadTransfers();
    }

    private void initViews() {
        // Vùng trên: Header và Thanh công cụ
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JLabel titleLabel = new JLabel("Chuyển tiền Nội bộ");
        titleLabel.setFont(ViewConstants.FONT_PANEL_HEADER);
        titleLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hàng lọc 1
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterRow.setBackground(ViewConstants.COLOR_CONTENT_BG);
        filterRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel srcLbl = new JLabel("Ví nguồn:");
        srcLbl.setFont(ViewConstants.FONT_BODY);
        filterRow.add(srcLbl);
        filterRow.add(this.sourceWalletBox);

        JLabel destLbl = new JLabel("Ví đích:");
        destLbl.setFont(ViewConstants.FONT_BODY);
        filterRow.add(destLbl);
        filterRow.add(this.destWalletBox);

        JLabel fromLbl = new JLabel("Từ ngày:");
        fromLbl.setFont(ViewConstants.FONT_BODY);
        filterRow.add(fromLbl);
        this.startDateField.setToolTipText("dd/MM/yyyy");
        filterRow.add(this.startDateField);

        JLabel toLbl = new JLabel("Đến ngày:");
        toLbl.setFont(ViewConstants.FONT_BODY);
        filterRow.add(toLbl);
        this.endDateField.setToolTipText("dd/MM/yyyy");
        filterRow.add(this.endDateField);

        JButton filterButton = createToolButton("Lọc", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        filterButton.addActionListener(e -> applyFilter());
        filterRow.add(filterButton);

        JButton resetButton = createToolButton("Đặt lại", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        resetButton.addActionListener(e -> resetFilter());
        filterRow.add(resetButton);

        // Hàng công cụ 2: Các nút hành động
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionRow.setBackground(ViewConstants.COLOR_CONTENT_BG);
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton newTransferButton = createToolButton("+ Chuyển tiền mới", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE);
        newTransferButton.addActionListener(e -> openTransferFundsDialog());
        actionRow.add(newTransferButton);

        JButton deleteButton = createToolButton("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE);
        deleteButton.addActionListener(e -> handleDeleteTransfer());
        actionRow.add(deleteButton);

        JButton refreshButton = createToolButton("Làm mới", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        refreshButton.addActionListener(e -> {
            loadWallets();
            reloadTransfers();
        });
        actionRow.add(refreshButton);

        northPanel.add(titleLabel);
        northPanel.add(Box.createVerticalStrut(10));
        northPanel.add(filterRow);
        northPanel.add(Box.createVerticalStrut(5));
        northPanel.add(actionRow);

        add(northPanel, BorderLayout.NORTH);

        // Vùng trung tâm: Bảng dữ liệu
        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 24, 12, 24),
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)));
        add(scrollPane, BorderLayout.CENTER);

        // Vùng dưới: Phân trang & Tóm tắt tổng
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        southPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)));

        southPanel.add(this.summaryLabel, BorderLayout.WEST);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        paginationPanel.setBackground(ViewConstants.COLOR_STATUS_BG);

        this.prevButton.setEnabled(false);
        this.prevButton.addActionListener(e -> changePage(-1));
        paginationPanel.add(this.prevButton);

        paginationPanel.add(this.pageInfoLabel);

        this.nextButton.setEnabled(false);
        this.nextButton.addActionListener(e -> changePage(1));
        paginationPanel.add(this.nextButton);

        southPanel.add(paginationPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
    }

    private JButton createToolButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(ViewConstants.FONT_BODY);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        return btn;
    }

    private void setupTable() {
        this.table.setFont(ViewConstants.FONT_BODY);
        this.table.setRowHeight(28);
        this.table.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.table.getTableHeader().setBackground(new Color(245, 245, 245));
        this.table.setGridColor(ViewConstants.COLOR_BORDER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        if (this.table.getColumnModel().getColumnCount() >= 7) {
            this.table.getColumnModel().getColumn(0).setPreferredWidth(50);
            this.table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

            this.table.getColumnModel().getColumn(1).setPreferredWidth(100);
            this.table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

            this.table.getColumnModel().getColumn(2).setPreferredWidth(160);
            this.table.getColumnModel().getColumn(3).setPreferredWidth(160);

            this.table.getColumnModel().getColumn(4).setPreferredWidth(130);
            this.table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

            this.table.getColumnModel().getColumn(5).setPreferredWidth(110);
            this.table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

            this.table.getColumnModel().getColumn(6).setPreferredWidth(200);
        }
    }

    private void loadWallets() {
        try {
            WalletsSummaryResponseDTO summary = this.walletController.loadWalletsSummary();
            this.sourceWalletBox.removeAllItems();
            this.destWalletBox.removeAllItems();

            this.sourceWalletBox.addItem("Tất cả ví nguồn");
            this.destWalletBox.addItem("Tất cả ví đích");

            if (summary != null && summary.getWallets() != null) {
                for (WalletResponseDTO wallet : summary.getWallets()) {
                    WalletComboItem item = new WalletComboItem(wallet.getWalletId(), wallet.getName());
                    this.sourceWalletBox.addItem(item);
                    this.destWalletBox.addItem(item);
                }
            }

            DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof WalletComboItem item) {
                        setText(item.name());
                    }
                    return this;
                }
            };
            this.sourceWalletBox.setRenderer(renderer);
            this.destWalletBox.setRenderer(renderer);
        } catch (Exception ex) {
            // bỏ qua lỗi nạp danh sách ví
        }
    }

    private void applyFilter() {
        this.currentPage = 1;
        loadData();
    }

    private void resetFilter() {
        this.sourceWalletBox.setSelectedIndex(0);
        this.destWalletBox.setSelectedIndex(0);
        this.startDateField.setText("");
        this.endDateField.setText("");
        this.currentPage = 1;
        loadData();
    }

    private void changePage(int delta) {
        int targetPage = this.currentPage + delta;
        if (targetPage >= 1 && targetPage <= this.totalPages) {
            this.currentPage = targetPage;
            loadData();
        }
    }

    public void reloadTransfers() {
        loadData();
    }

    private void loadData() {
        try {
            FilterTransfersRequestDTO filter = buildFilterRequest();
            TransferListResponseDTO response = this.transferController.loadTransfers(filter);

            if (response != null) {
                this.tableModel.setTransfers(response.getItems());
                this.totalPages = Math.max(response.getTotalPages(), 1);
                this.totalRecords = response.getTotalRecords();
                this.currentPage = Math.min(Math.max(response.getPageNumber(), 1), this.totalPages);

                this.pageInfoLabel.setText(String.format("Trang %d / %d (Tổng %d giao dịch)",
                        this.currentPage, this.totalPages, this.totalRecords));

                String totalTransferredStr = formatCurrency(response.getTotalTransferredAmount());
                String totalFeesStr = formatCurrency(response.getTotalFees());
                this.summaryLabel.setText(String.format("Tổng tiền chuyển: %s VNĐ | Tổng phí: %s VNĐ",
                        totalTransferredStr, totalFeesStr));
            } else {
                this.tableModel.clear();
                this.totalPages = 1;
                this.totalRecords = 0;
                this.pageInfoLabel.setText("Trang 1 / 1 (Tổng 0 giao dịch)");
                this.summaryLabel.setText("Tổng tiền chuyển: 0 VNĐ | Tổng phí: 0 VNĐ");
            }

            this.prevButton.setEnabled(this.currentPage > 1);
            this.nextButton.setEnabled(this.currentPage < this.totalPages);

            if (this.onTransferChangedListener != null) {
                this.onTransferChangedListener.run();
            }
        } catch (Exception ex) {
            this.errorConsumer.accept("Không thể tải danh sách chuyển tiền: " + ex.getMessage());
        }
    }

    private FilterTransfersRequestDTO buildFilterRequest() {
        UUID sourceWalletId = null;
        Object selectedSource = this.sourceWalletBox.getSelectedItem();
        if (selectedSource instanceof WalletComboItem item) {
            sourceWalletId = item.id();
        }

        UUID destWalletId = null;
        Object selectedDest = this.destWalletBox.getSelectedItem();
        if (selectedDest instanceof WalletComboItem item) {
            destWalletId = item.id();
        }

        LocalDate startDate = InputValidator.parseOptionalDate(this.startDateField.getText(), "Ngày bắt đầu");
        LocalDate endDate = InputValidator.parseOptionalDate(this.endDateField.getText(), "Ngày kết thúc");

        return new FilterTransfersRequestDTO(
                startDate, endDate, sourceWalletId, destWalletId, this.currentPage, this.pageSize);
    }

    private void openTransferFundsDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        TransferFundsDialog dialog = new TransferFundsDialog(
                parentFrame, this.transferController, this.walletController, this.categoryController);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            this.messageConsumer.accept("Chuyển tiền thành công!");
            reloadTransfers();
        }
    }

    private void handleDeleteTransfer() {
        int selectedRow = this.table.getSelectedRow();
        if (selectedRow < 0) {
            this.warningConsumer.accept("Vui lòng chọn giao dịch chuyển tiền cần xóa!");
            return;
        }

        TransferResponseDTO transfer = this.tableModel.getTransferAt(selectedRow);
        if (transfer == null) {
            return;
        }

        boolean confirm = this.confirmConsumer.test(
                "Bạn có chắc chắn muốn xóa giao dịch chuyển tiền từ ví \""
                        + transfer.getSourceWalletName() + "\" đến ví \""
                        + transfer.getDestinationWalletName() + "\" không?");

        if (confirm) {
            try {
                this.transferController.handleDeleteTransfer(transfer.getTransferId());
                this.messageConsumer.accept("Đã xóa giao dịch chuyển tiền thành công!");
                reloadTransfers();
            } catch (Exception ex) {
                this.errorConsumer.accept("Xóa giao dịch chuyển tiền thất bại: " + ex.getMessage());
            }
        }
    }

    private String formatCurrency(BigDecimal amount) {
        BigDecimal safe = (amount != null) ? amount : BigDecimal.ZERO;
        return this.currencyFormatter.format(safe);
    }

    // Getters and Listeners
    public TransferTableModel getTableModel() {
        return this.tableModel;
    }

    public JTable getTable() {
        return this.table;
    }

    public String getSummaryText() {
        return this.summaryLabel.getText();
    }

    public String getPageInfoText() {
        return this.pageInfoLabel.getText();
    }

    public void setOnTransferChangedListener(Runnable listener) {
        this.onTransferChangedListener = listener;
    }

    public void setMessageConsumer(Consumer<String> consumer) {
        this.messageConsumer = Objects.requireNonNull(consumer);
    }

    public void setWarningConsumer(Consumer<String> consumer) {
        this.warningConsumer = Objects.requireNonNull(consumer);
    }

    public void setErrorConsumer(Consumer<String> consumer) {
        this.errorConsumer = Objects.requireNonNull(consumer);
    }

    public void setConfirmConsumer(Predicate<String> consumer) {
        this.confirmConsumer = Objects.requireNonNull(consumer);
    }

    private record WalletComboItem(UUID id, String name) {
        @Override
        public String toString() {
            return name;
        }
    }
}
