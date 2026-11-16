package com.pfms.presentation.transfer;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.presentation.dto.transfer.*;
import com.pfms.presentation.dto.wallet.*;
import com.pfms.presentation.common.InputValidator;
import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.transfer.component.TransferTableActionRenderer;
import com.pfms.presentation.transfer.dialog.TransferFundsDialog;
import com.pfms.presentation.transfer.model.TransferTableModel;
import com.pfms.presentation.transfer.viewmodel.TransferItemViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Chuyển tiền Nội bộ theo mô hình MVP (Passive View).
 * Tái cấu trúc chuẩn Luxury Black &amp; Gold bám sát tuyệt đối preview-transfer.html:
 * - Header Row: Tiêu đề "Chuyển tiền Nội bộ" bên trái, 3 thẻ KPI Stats Pills bên phải.
 * - Thẻ Bộ Lọc (Filter Card): Khung Card bo viền mịn màng, thanh tìm kiếm từ khóa,
 *   bộ chọn ví nguồn, ví đích, khoảng ngày, nút [Lọc], [Đặt lại] và cụm nút tác vụ [+ Chuyển tiền mới].
 * - Bảng dữ liệu 7 cột chuẩn với nút thao tác [Sửa] và [Xóa] trên từng dòng.
 * - Footer tóm tắt dòng tiền và cụm phân trang chuẩn.
 */
public class TransferPanel extends JPanel implements TransferView {

    private static final long serialVersionUID = 1L;

    // region Fields
    private final TransferPresenter presenter;
    private final WalletGUIController walletController;
    private final CategoryGUIController categoryController;

    private final TransferTableModel tableModel = new TransferTableModel();
    private final JTable table = new JTable(this.tableModel);

    // KPI Stats Pills
    private final JLabel statTotalTransferredLabel = new JLabel("Tổng chuyển: 0 VNĐ");
    private final JLabel statTotalFeesLabel = new JLabel("Tổng phí: 0 VNĐ");
    private final JLabel statCountLabel = new JLabel("0 giao dịch");

    // Filter controls
    private final JTextField searchField = new JTextField();
    private final JComboBox<Object> sourceWalletBox = new JComboBox<>();
    private final JComboBox<Object> destWalletBox = new JComboBox<>();
    private final JTextField startDateField = new JTextField(8);
    private final JTextField endDateField = new JTextField(8);

    // South pagination & summary
    private final JLabel pageInfoLabel = new JLabel("Trang 1 / 1 (Tổng 0 giao dịch)");
    private final JLabel summaryLabel = new JLabel("Tổng tiền chuyển: 0 VNĐ | Tổng phí: 0 VNĐ");
    private final JButton prevButton = new JButton("< Trước");
    private final JButton nextButton = new JButton("Sau >");

    private List<TransferItemViewModel> rawTransferViewModels = Collections.emptyList();
    private Runnable onTransferChangedListener;
    private Consumer<String> messageConsumer, warningConsumer, errorConsumer;
    private Predicate<String> confirmConsumer;
    // endregion

    // region Constructors
    public TransferPanel(TransferGUIController tc, WalletGUIController wc, CategoryGUIController cc) {
        this.walletController = Objects.requireNonNull(wc, "WalletGUIController không được null");
        this.categoryController = Objects.requireNonNull(cc, "CategoryGUIController không được null");
        this.presenter = new TransferPresenter(this, Objects.requireNonNull(tc, "TransferGUIController không được null"));
        initUI();
        loadWallets();
        reloadTransfers();
    }
    // endregion

    // region UI Initialization
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Vùng phía Bắc (North Area) gồm Header Top Row và Thẻ Bộ Lọc (Filter Card)
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(ViewConstants.COLOR_CONTENT_BG);
        north.setBorder(BorderFactory.createEmptyBorder(16, 20, 10, 20));

        // 1. Header Top Row: Tiêu đề bên trái, 3 KPI Stats Pills bên phải
        JPanel headerRow = new JPanel(new BorderLayout(16, 0));
        headerRow.setBackground(ViewConstants.COLOR_CONTENT_BG);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Chuyển tiền Nội bộ");
        title.setFont(ViewConstants.FONT_PANEL_HEADER);
        title.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        headerRow.add(title, BorderLayout.WEST);

        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        statsRow.setBackground(ViewConstants.COLOR_CONTENT_BG);
        statsRow.add(createStatPill(this.statTotalTransferredLabel, ViewConstants.COLOR_PRIMARY_LIGHT));
        statsRow.add(createStatPill(this.statTotalFeesLabel, ViewConstants.COLOR_EXPENSE));
        statsRow.add(createStatPill(this.statCountLabel, ViewConstants.COLOR_TEXT_MUTED));
        headerRow.add(statsRow, BorderLayout.EAST);

        north.add(headerRow);
        north.add(Box.createVerticalStrut(12));

        // 2. Thẻ Bộ Lọc (Filter Card) - Khung Card nền Obsidian đen viền tinh tế
        JPanel filterCard = new JPanel(new BorderLayout(12, 0));
        filterCard.setBackground(ViewConstants.COLOR_CARD_BG);
        filterCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterCard.putClientProperty("FlatLaf.style", "arc: 12");
        filterCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));

        // Bộ điều khiển bên trái Toolbar
        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        leftFilters.setOpaque(false);

        // Ô tìm kiếm nhanh từ khóa
        this.searchField.setPreferredSize(new Dimension(170, 32));
        this.searchField.putClientProperty("JTextField.placeholderText", "Tìm theo ghi chú, tên ví...");
        this.searchField.putClientProperty("FlatLaf.style", "arc: 8");
        this.searchField.setBackground(ViewConstants.COLOR_STATUS_BG);
        this.searchField.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        this.searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyClientSearchFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyClientSearchFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyClientSearchFilter(); }
        });

        // Dropdown ví nguồn & ví đích
        styleDropdown(this.sourceWalletBox);
        styleDropdown(this.destWalletBox);

        // Khoảng ngày
        styleDateField(this.startDateField);
        styleDateField(this.endDateField);

        JPanel dateRangeBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        dateRangeBox.setOpaque(false);
        JLabel lblTu = new JLabel("Từ:");
        lblTu.setFont(ViewConstants.FONT_STATUS);
        lblTu.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        JLabel lblDen = new JLabel("- Đến:");
        lblDen.setFont(ViewConstants.FONT_STATUS);
        lblDen.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        dateRangeBox.add(lblTu);
        dateRangeBox.add(this.startDateField);
        dateRangeBox.add(lblDen);
        dateRangeBox.add(this.endDateField);

        // Nút Lọc và Đặt lại
        JButton btnFilter = createBtn("Lọc", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        JButton btnReset = createBtn("Đặt lại", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MUTED);
        btnFilter.addActionListener(e -> applyFilter());
        btnReset.addActionListener(e -> resetFilter());

        JLabel lblSrc = new JLabel("Ví nguồn:");
        lblSrc.setFont(ViewConstants.FONT_STATUS);
        lblSrc.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        JLabel lblDst = new JLabel("Ví đích:");
        lblDst.setFont(ViewConstants.FONT_STATUS);
        lblDst.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        leftFilters.add(this.searchField);
        leftFilters.add(lblSrc);
        leftFilters.add(this.sourceWalletBox);
        leftFilters.add(lblDst);
        leftFilters.add(this.destWalletBox);
        leftFilters.add(dateRangeBox);
        leftFilters.add(btnFilter);
        leftFilters.add(btnReset);

        // Cụm nút tác vụ bên phải Toolbar: [Làm mới] và [+ Chuyển tiền mới]
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        rightActions.setOpaque(false);

        JButton btnRefresh = createBtn("Làm mới", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        btnRefresh.addActionListener(e -> { loadWallets(); reloadTransfers(); });

        JButton btnNew = new JButton("+ Chuyển tiền mới");
        btnNew.setFont(ViewConstants.FONT_NAV_ACTIVE);
        btnNew.setBackground(ViewConstants.COLOR_PRIMARY_LIGHT);
        btnNew.setForeground(Color.BLACK);
        btnNew.setFocusPainted(false);
        btnNew.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNew.putClientProperty("FlatLaf.style", "arc: 8");
        btnNew.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        btnNew.addActionListener(e -> openTransferFundsDialog());

        rightActions.add(btnRefresh);
        rightActions.add(btnNew);

        filterCard.add(leftFilters, BorderLayout.CENTER);
        filterCard.add(rightActions, BorderLayout.EAST);

        north.add(filterCard);
        add(north, BorderLayout.NORTH);

        // 3. Cấu hình bảng dữ liệu giao dịch
        setupTable();
        JScrollPane scroll = new JScrollPane(this.table);
        scroll.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        scroll.setBackground(ViewConstants.COLOR_CARD_BG);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 8, 20),
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1, true)
        ));
        add(scroll, BorderLayout.CENTER);

        // 4. Footer phân trang & tóm tắt
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(ViewConstants.COLOR_STATUS_BG);
        south.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        this.summaryLabel.setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.summaryLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        south.add(this.summaryLabel, BorderLayout.WEST);

        JPanel pgn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pgn.setBackground(ViewConstants.COLOR_STATUS_BG);
        this.prevButton.setFont(ViewConstants.FONT_BODY);
        this.nextButton.setFont(ViewConstants.FONT_BODY);
        this.pageInfoLabel.setFont(ViewConstants.FONT_BODY);
        this.prevButton.setEnabled(false);
        this.nextButton.setEnabled(false);
        this.prevButton.addActionListener(e -> this.presenter.prevPage());
        this.nextButton.addActionListener(e -> this.presenter.nextPage());

        pgn.add(this.prevButton);
        pgn.add(this.pageInfoLabel);
        pgn.add(this.nextButton);
        south.add(pgn, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);
    }

    private void setupTable() {
        com.pfms.presentation.common.ViewUtils.styleTable(this.table);
        this.table.setRowHeight(36);

        DefaultTableCellRenderer centerRenderer = com.pfms.presentation.common.ViewUtils.createCellRenderer(SwingConstants.CENTER, ViewConstants.COLOR_TEXT_MUTED);
        DefaultTableCellRenderer leftMainRenderer = com.pfms.presentation.common.ViewUtils.createCellRenderer(SwingConstants.LEFT, ViewConstants.COLOR_TEXT_MAIN);
        DefaultTableCellRenderer rightAmountRenderer = com.pfms.presentation.common.ViewUtils.createCellRenderer(SwingConstants.RIGHT, ViewConstants.COLOR_PRIMARY_LIGHT);
        DefaultTableCellRenderer leftMutedRenderer = com.pfms.presentation.common.ViewUtils.createCellRenderer(SwingConstants.LEFT, ViewConstants.COLOR_TEXT_MUTED);

        // Renderer riêng cho Phụ phí: nếu có phí hiển thị màu đỏ Coral, không phí hiển thị xám
        DefaultTableCellRenderer feeRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!isSelected) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    String val = (v != null) ? v.toString().trim() : "";
                    if (!val.equals("-") && !val.equals("0 VNĐ") && !val.isEmpty()) {
                        setForeground(ViewConstants.COLOR_EXPENSE);
                    } else {
                        setForeground(ViewConstants.COLOR_TEXT_DIM);
                    }
                }
                return this;
            }
        };

        if (this.table.getColumnModel().getColumnCount() >= 7) {
            int[] colWidths = {105, 160, 160, 130, 110, 220, 130};
            for (int i = 0; i < colWidths.length; i++) {
                this.table.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
            }
            this.table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);      // Ngày
            this.table.getColumnModel().getColumn(1).setCellRenderer(leftMainRenderer);    // Ví nguồn
            this.table.getColumnModel().getColumn(2).setCellRenderer(leftMainRenderer);    // Ví đích
            this.table.getColumnModel().getColumn(3).setCellRenderer(rightAmountRenderer); // Số tiền
            this.table.getColumnModel().getColumn(4).setCellRenderer(feeRenderer);          // Phụ phí
            this.table.getColumnModel().getColumn(5).setCellRenderer(leftMutedRenderer);   // Ghi chú

            TransferTableActionRenderer actionRenderer = new TransferTableActionRenderer(
                    this::handleEditFromRow,
                    this::handleDeleteFromRow
            );
            this.table.getColumnModel().getColumn(6).setCellRenderer(actionRenderer);      // Thao tác
            this.table.getColumnModel().getColumn(6).setCellEditor(actionRenderer);
        }
    }

    private void handleEditFromRow(Object data) {
        showMessage("Để đảm bảo tính toàn vẹn số dư ví tài chính, việc điều chỉnh giao dịch chuyển tiền được thực hiện bằng cách xóa và thực hiện chuyển tiền mới.");
    }

    private void handleDeleteFromRow(Object data) {
        if (data instanceof TransferResponseDTO trf) {
            this.presenter.deleteTransfer(trf.getTransferId(), trf.getSourceWalletName(), trf.getDestinationWalletName());
        }
    }

    private JPanel createStatPill(JLabel label, Color accentColor) {
        label.setFont(ViewConstants.FONT_NAV_ACTIVE);
        label.setForeground(accentColor);
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        pill.setBackground(ViewConstants.COLOR_CARD_BG);
        pill.putClientProperty("FlatLaf.style", "arc: 999");
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 90), 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        pill.add(label);
        return pill;
    }

    private void styleDropdown(JComboBox<Object> combo) {
        combo.setFont(ViewConstants.FONT_BODY);
        combo.setBackground(ViewConstants.COLOR_STATUS_BG);
        combo.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        combo.setPreferredSize(new Dimension(140, 32));
        combo.putClientProperty("FlatLaf.style", "arc: 8");
    }

    private void styleDateField(JTextField field) {
        field.setFont(ViewConstants.FONT_BODY);
        field.setBackground(ViewConstants.COLOR_STATUS_BG);
        field.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        field.setPreferredSize(new Dimension(84, 32));
        field.putClientProperty("JTextField.placeholderText", "dd/MM/yyyy");
        field.putClientProperty("FlatLaf.style", "arc: 8");
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
    }
    // endregion

    // region Actions
    public void applyFilter() {
        this.presenter.loadTransfers(buildFilter(1));
    }

    public void resetFilter() {
        this.searchField.setText("");
        this.sourceWalletBox.setSelectedIndex(0);
        this.destWalletBox.setSelectedIndex(0);
        this.startDateField.setText("");
        this.endDateField.setText("");
        this.presenter.loadTransfers(new FilterTransfersRequestDTO());
    }

    public void handleDeleteTransfer() {
        int row = this.table.getSelectedRow();
        if (row < 0) {
            showWarning("Vui lòng chọn giao dịch chuyển tiền cần xóa!");
            return;
        }
        if (row < this.rawTransferViewModels.size()) {
            this.presenter.deleteTransfer(this.rawTransferViewModels.get(row));
        }
    }

    @Override
    public void openTransferFundsDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        TransferFundsDialog dialog = new TransferFundsDialog(parent, this.presenter.getController(), this.walletController, this.categoryController);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            showMessage("Chuyển tiền thành công!");
            reloadTransfers();
        }
    }
    // endregion

    // region Data Binding
    @Override
    public void renderTransfers(List<TransferItemViewModel> transfers, String summaryText, String pageInfoText) {
        this.rawTransferViewModels = (transfers != null) ? transfers : Collections.emptyList();
        this.summaryLabel.setText(summaryText);
        this.pageInfoLabel.setText(pageInfoText);
        updateKPIStats(summaryText, pageInfoText);
        applyClientSearchFilter();

        this.prevButton.setEnabled(this.presenter.getCurrentPage() > 1);
        this.nextButton.setEnabled(this.presenter.getCurrentPage() < this.presenter.getTotalPages());
        if (this.onTransferChangedListener != null) this.onTransferChangedListener.run();
    }

    private void applyClientSearchFilter() {
        String kw = this.searchField.getText().toLowerCase().trim();
        List<TransferItemViewModel> filtered = this.rawTransferViewModels;
        if (!kw.isEmpty()) {
            filtered = this.rawTransferViewModels.stream().filter(vm ->
                    (vm.fromWalletName() != null && vm.fromWalletName().toLowerCase().contains(kw)) ||
                    (vm.toWalletName() != null && vm.toWalletName().toLowerCase().contains(kw)) ||
                    (vm.note() != null && vm.note().toLowerCase().contains(kw)) ||
                    (vm.formattedAmount() != null && vm.formattedAmount().toLowerCase().contains(kw))
            ).toList();
        }
        this.tableModel.setTransfers(filtered.stream().map(this::toTransferDto).toList());
    }

    private void updateKPIStats(String summaryText, String pageInfoText) {
        if (summaryText != null && summaryText.contains("|")) {
            String[] parts = summaryText.split("\\|");
            if (parts.length >= 2) {
                String transferPart = parts[0].trim();
                String feePart = parts[1].trim();
                this.statTotalTransferredLabel.setText(transferPart.replace("Tổng tiền chuyển:", "Tổng chuyển:"));
                this.statTotalFeesLabel.setText(feePart);
            }
        }
        if (pageInfoText != null && pageInfoText.contains("Tổng")) {
            int idx = pageInfoText.indexOf("Tổng");
            int endIdx = pageInfoText.indexOf(")", idx);
            if (idx != -1 && endIdx != -1) {
                String countPart = pageInfoText.substring(idx + 5, endIdx).trim();
                this.statCountLabel.setText(countPart);
            }
        }
    }

    public void reloadTransfers() {
        this.presenter.loadTransfers(buildFilter(this.presenter.getCurrentPage()));
    }

    private void loadWallets() {
        try {
            WalletsSummaryResponseDTO s = this.walletController.loadWalletsSummary();
            this.sourceWalletBox.removeAllItems();
            this.destWalletBox.removeAllItems();
            this.sourceWalletBox.addItem("Tất cả ví nguồn");
            this.destWalletBox.addItem("Tất cả ví đích");
            if (s != null && s.getWallets() != null) {
                for (WalletResponseDTO w : s.getWallets()) {
                    this.sourceWalletBox.addItem(new WalletComboItem(w.getWalletId(), w.getName()));
                    this.destWalletBox.addItem(new WalletComboItem(w.getWalletId(), w.getName()));
                }
            }
        } catch (Exception ignored) {}
    }

    private FilterTransfersRequestDTO buildFilter(int page) {
        UUID src = (this.sourceWalletBox.getSelectedItem() instanceof WalletComboItem it) ? it.id() : null;
        UUID dst = (this.destWalletBox.getSelectedItem() instanceof WalletComboItem it) ? it.id() : null;
        return new FilterTransfersRequestDTO(
                InputValidator.parseOptionalDate(this.startDateField.getText(), "Ngày bắt đầu"),
                InputValidator.parseOptionalDate(this.endDateField.getText(), "Ngày kết thúc"),
                src, dst, page, 20);
    }
    // endregion

    // region Getters
    public TransferPresenter getPresenter() { return this.presenter; }
    public TransferTableModel getTableModel() { return this.tableModel; }
    public JTable getTable() { return this.table; }
    public String getSummaryText() { return this.summaryLabel.getText(); }
    public String getPageInfoText() { return this.pageInfoLabel.getText(); }
    public JTextField getSearchField() { return this.searchField; }
    public JLabel getStatTotalTransferredLabel() { return this.statTotalTransferredLabel; }
    public JLabel getStatTotalFeesLabel() { return this.statTotalFeesLabel; }
    public JLabel getStatCountLabel() { return this.statCountLabel; }
    public void setOnTransferChangedListener(Runnable l) { this.onTransferChangedListener = l; }
    public void setMessageConsumer(Consumer<String> c) { this.messageConsumer = c; }
    public void setWarningConsumer(Consumer<String> c) { this.warningConsumer = c; }
    public void setErrorConsumer(Consumer<String> c) { this.errorConsumer = c; }
    public void setConfirmConsumer(Predicate<String> c) { this.confirmConsumer = c; }
    // endregion

    // region Helpers
    @Override
    public void showMessage(String m) {
        if (messageConsumer != null) messageConsumer.accept(m);
        else JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showWarning(String m) {
        if (warningConsumer != null) warningConsumer.accept(m);
        else JOptionPane.showMessageDialog(this, m, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void showError(String m) {
        if (errorConsumer != null) errorConsumer.accept(m);
        else JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public boolean confirmAction(String m) {
        return (confirmConsumer != null)
                ? confirmConsumer.test(m)
                : JOptionPane.showConfirmDialog(this, m, "Xác nhận thao tác", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private JButton createBtn(String txt, Color bg, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(ViewConstants.FONT_BODY);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.putClientProperty("FlatLaf.style", "arc: 8");
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        return b;
    }

    private TransferResponseDTO toTransferDto(TransferItemViewModel vm) {
        LocalDate d = null;
        if (vm.dateFormatted() != null && !vm.dateFormatted().isBlank() && !"-".equals(vm.dateFormatted())) {
            try {
                d = LocalDate.parse(vm.dateFormatted(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception ignored) {
                d = LocalDate.now();
            }
        }
        return new TransferResponseDTO(
                vm.id(),
                UUID.randomUUID(),
                vm.fromWalletName(),
                UUID.randomUUID(),
                vm.toWalletName(),
                vm.rawAmount() != null ? vm.rawAmount() : BigDecimal.ZERO,
                d,
                vm.note(),
                vm.fromBalanceAfter() != null ? vm.fromBalanceAfter() : BigDecimal.ZERO,
                vm.toBalanceAfter() != null ? vm.toBalanceAfter() : BigDecimal.ZERO,
                null);
    }

    private record WalletComboItem(UUID id, String name) {
        @Override public String toString() { return name; }
    }
    // endregion
}
