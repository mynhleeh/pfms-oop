package com.pfms.presentation.wallet;

import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.common.component.RoundedCardPanel;
import com.pfms.presentation.common.component.SearchIcon;
import com.pfms.presentation.wallet.dialog.AdjustBalanceDialog;
import com.pfms.presentation.wallet.dialog.CreateWalletDialog;
import com.pfms.presentation.wallet.dialog.UpdateWalletDialog;
import com.pfms.presentation.wallet.model.WalletTableModel;
import com.pfms.presentation.wallet.viewmodel.WalletItemViewModel;
import com.pfms.presentation.wallet.viewmodel.WalletsSummaryViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Quản lý Ví theo mô hình MVP (Passive View).
 * Cung cấp chế độ hiển thị kép (Dual-View): Thẻ ví (Card Grid) và Bảng dữ liệu (Table),
 * cùng bảng thông số KPI Header và thanh công cụ tìm kiếm lọc dữ liệu theo thời gian thực.
 * Tuân thủ quy chuẩn thiết kế Luxury Black &amp; Gold và tương đồng tuyệt đối với bản preview.
 */
public class WalletPanel extends JPanel implements WalletView {

    // region Fields
    private final WalletPresenter presenter;
    private final WalletTableModel tableModel = new WalletTableModel();
    private final JTable table = new JTable(this.tableModel);
    private final JScrollPane tableScrollPane = new JScrollPane(this.table);

    // Viewport kép: Card Grid & Table
    private final CardLayout viewportLayout = new CardLayout();
    private final JPanel viewportPanel = new JPanel(this.viewportLayout);
    private final JPanel cardsGridPanel = new JPanel();
    private final JPanel cardsWrapperPanel = new JPanel(new BorderLayout());
    private final JScrollPane cardsScrollPane = new JScrollPane(this.cardsWrapperPanel);

    private final JButton btnViewCards = createViewModeButton("Thẻ ví", true);
    private final JButton btnViewTable = createViewModeButton("Bảng dữ liệu", false);

    // Header KPI Capsules
    private final JLabel kpiTotalBalanceLabel = new JLabel("0 ₫");
    private final JLabel kpiActiveCountLabel = new JLabel("0 ví");
    private final JLabel kpiArchivedCountLabel = new JLabel("0 ví");

    // Toolbar components
    private final JComboBox<String> statusFilterBox = new JComboBox<>(new String[]{"Tất cả", "ACTIVE", "ARCHIVED", "DELETED"});
    private final JTextField searchField = new JTextField();
    private final JButton addButton = createToolBtn("+ Thêm ví mới", ViewConstants.COLOR_PRIMARY, Color.BLACK, ViewConstants.FONT_NAV_ACTIVE);

    // Action buttons retained for test contract and programmatic execution
    private final JButton editButton = createToolBtn("Sửa", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_TEXT_MAIN, ViewConstants.FONT_BODY);
    private final JButton adjustButton = createToolBtn("Kiểm kê số dư", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_TEXT_MAIN, ViewConstants.FONT_BODY);
    private final JButton toggleStatusButton = createToolBtn("Lưu trữ / Kích hoạt", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_TEXT_MAIN, ViewConstants.FONT_BODY);
    private final JButton deleteButton = createToolBtn("Xóa ví", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_EXPENSE, ViewConstants.FONT_BODY);

    // Footer Statusbar
    private final JLabel summaryLabel = new JLabel("Đang tải dữ liệu...");
    private final JPanel bottomPanel = new JPanel(new BorderLayout());

    private List<WalletItemViewModel> walletViewModels = Collections.emptyList();
    private List<WalletResponseDTO> rawWalletDtos = Collections.emptyList();
    private Runnable onWalletChangedListener;
    private Consumer<String> messageConsumer, warningConsumer, errorConsumer;
    private Predicate<String> confirmConsumer;
    // endregion

    // region Constructors
    public WalletPanel(WalletGUIController walletController) {
        this.presenter = new WalletPresenter(this, Objects.requireNonNull(walletController, "WalletGUIController không được để null"));
        initUI();
        reloadWallets();
    }
    // endregion

    // region UI Setup
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(ViewConstants.COLOR_CONTENT_BG);

        // 1. Phía trên: Header Phân Hệ (Title + Subtitle + KPI Stat Pills) + Toolbar
        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.setBackground(ViewConstants.COLOR_CONTENT_BG);

        northContainer.add(createHeaderPanel());
        northContainer.add(createToolbarPanel());
        add(northContainer, BorderLayout.NORTH);

        // 2. Vùng trung tâm: Dual-View Viewport (Cards vs Table)
        setupCardsViewport();
        configureTable();
        this.viewportPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);
        this.viewportPanel.add(this.cardsScrollPane, "CARDS");
        this.viewportPanel.add(this.tableScrollPane, "TABLE");

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(ViewConstants.COLOR_CONTENT_BG);
        centerContainer.setBorder(new EmptyBorder(16, 24, 16, 24));
        centerContainer.add(this.viewportPanel, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        // 3. Phía dưới: Footer Statusbar
        setupBottomPanel();
        add(this.bottomPanel, BorderLayout.SOUTH);

        // Sự kiện các nút công cụ cũ (đảm bảo tương thích ngược)
        this.addButton.addActionListener(e -> handleAddWallet());
        this.editButton.addActionListener(e -> handleEditWallet());
        this.adjustButton.addActionListener(e -> handleAdjustBalance());
        this.toggleStatusButton.addActionListener(e -> handleToggleStatus());
        this.deleteButton.addActionListener(e -> handleDeleteWallet());

        this.statusFilterBox.addActionListener(e -> applyFilter());
        this.searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterAndRender(); }
            @Override public void removeUpdate(DocumentEvent e) { filterAndRender(); }
            @Override public void changedUpdate(DocumentEvent e) { filterAndRender(); }
        });

        this.btnViewCards.addActionListener(e -> switchView("CARDS"));
        this.btnViewTable.addActionListener(e -> switchView("TABLE"));
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ViewConstants.COLOR_HEADER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, ViewConstants.COLOR_BORDER),
                new EmptyBorder(18, 24, 16, 24)
        ));

        // Tiêu đề & phụ đề bên trái
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel title = new JLabel("Quản lý Ví tài chính");
        title.setFont(ViewConstants.FONT_PANEL_HEADER);
        title.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);

        JLabel subtitle = new JLabel("Theo dõi tài sản khả dụng và kiểm kê đối soát chênh lệch trực tiếp trên từng ví.");
        subtitle.setFont(ViewConstants.FONT_BODY);
        subtitle.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subtitle);
        header.add(titleBox, BorderLayout.WEST);

        // 3 Thẻ KPI Capsule bên phải
        JPanel kpiPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        kpiPanel.setOpaque(false);

        kpiPanel.add(createKpiCapsule("Tổng tài sản:", this.kpiTotalBalanceLabel, ViewConstants.COLOR_PRIMARY,
                new Color(212, 175, 55, 30), ViewConstants.COLOR_GOLD_BORDER));
        kpiPanel.add(createKpiCapsule("Đang hoạt động:", this.kpiActiveCountLabel, ViewConstants.COLOR_INCOME,
                ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_BORDER));
        kpiPanel.add(createKpiCapsule("Lưu trữ:", this.kpiArchivedCountLabel, new Color(251, 191, 36),
                ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_BORDER));

        header.add(kpiPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createToolbarPanel() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(ViewConstants.COLOR_CONTENT_BG);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, ViewConstants.COLOR_BORDER),
                new EmptyBorder(12, 24, 12, 24)
        ));

        // Bên trái: Nút Thêm ví mới (Gold)
        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftGroup.setOpaque(false);
        leftGroup.add(this.addButton);
        toolbar.add(leftGroup, BorderLayout.WEST);

        // Bên phải: Bộ lọc trạng thái + Ô tìm kiếm + Bộ chuyển chế độ xem
        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightGroup.setOpaque(false);

        JLabel filterLabel = new JLabel("Trạng thái:");
        filterLabel.setFont(ViewConstants.FONT_BODY);
        filterLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        this.statusFilterBox.setFont(ViewConstants.FONT_BODY);
        this.statusFilterBox.setBackground(ViewConstants.COLOR_CARD_BG);
        this.statusFilterBox.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.statusFilterBox.setPreferredSize(new Dimension(130, 32));

        // Ô tìm kiếm có icon
        JPanel searchBox = new JPanel(new BorderLayout(6, 0));
        searchBox.setBackground(ViewConstants.COLOR_CARD_BG);
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER),
                new EmptyBorder(0, 8, 0, 8)
        ));
        searchBox.setPreferredSize(new Dimension(220, 32));
        searchBox.add(new JLabel(new SearchIcon(14, ViewConstants.COLOR_TEXT_DIM)), BorderLayout.WEST);

        this.searchField.setFont(ViewConstants.FONT_BODY);
        this.searchField.setBackground(ViewConstants.COLOR_CARD_BG);
        this.searchField.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.searchField.setCaretColor(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.searchField.setBorder(null);
        searchBox.add(this.searchField, BorderLayout.CENTER);

        // Nhóm nút chuyển chế độ xem
        JPanel viewSwitchGroup = new JPanel(new GridLayout(1, 2, 0, 0));
        viewSwitchGroup.setOpaque(false);
        viewSwitchGroup.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER));
        viewSwitchGroup.add(this.btnViewCards);
        viewSwitchGroup.add(this.btnViewTable);

        rightGroup.add(filterLabel);
        rightGroup.add(this.statusFilterBox);
        rightGroup.add(searchBox);
        rightGroup.add(viewSwitchGroup);

        toolbar.add(rightGroup, BorderLayout.EAST);
        return toolbar;
    }

    private void setupCardsViewport() {
        this.cardsGridPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);
        this.cardsGridPanel.setLayout(new GridLayout(0, 2, 12, 12));

        this.cardsWrapperPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);
        this.cardsWrapperPanel.add(this.cardsGridPanel, BorderLayout.NORTH);

        this.cardsScrollPane.getViewport().setBackground(ViewConstants.COLOR_CONTENT_BG);
        this.cardsScrollPane.setBackground(ViewConstants.COLOR_CONTENT_BG);
        this.cardsScrollPane.setBorder(null);
        this.cardsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }

    private void configureTable() {
        this.table.setFont(ViewConstants.FONT_BODY);
        this.table.setRowHeight(54);
        this.table.setBackground(ViewConstants.COLOR_CARD_BG);
        this.table.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.table.setGridColor(ViewConstants.COLOR_BORDER);
        this.table.setShowGrid(true);
        this.table.setSelectionBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
        this.table.setSelectionForeground(ViewConstants.COLOR_TEXT_ACTIVE);

        this.table.getTableHeader().setBackground(new Color(14, 14, 14));
        this.table.getTableHeader().setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.table.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.table.getTableHeader().setReorderingAllowed(false);
        Dimension headerDim = this.table.getTableHeader().getPreferredSize();
        this.table.getTableHeader().setPreferredSize(new Dimension(headerDim.width, 38));
        this.table.getTableHeader().setDefaultRenderer(createHeaderRenderer());

        this.tableScrollPane.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        this.tableScrollPane.setBackground(ViewConstants.COLOR_CARD_BG);
        this.tableScrollPane.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER));

        // Thiết lập kích thước cột
        int[] colWidths = new int[]{270, 160, 120, 290, 250};
        for (int i = 0; i < 5 && i < this.table.getColumnModel().getColumnCount(); i++) {
            this.table.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }

        // Renderers cho từng cột
        this.table.getColumnModel().getColumn(0).setCellRenderer(createWalletNameRenderer());
        this.table.getColumnModel().getColumn(1).setCellRenderer(createCellRenderer(SwingConstants.RIGHT, ViewConstants.COLOR_PRIMARY_LIGHT));
        this.table.getColumnModel().getColumn(2).setCellRenderer(createStatusRenderer());
        this.table.getColumnModel().getColumn(3).setCellRenderer(createNoteRenderer());
        this.table.getColumnModel().getColumn(4).setCellRenderer(new ActionsCellRenderer());
        this.table.getColumnModel().getColumn(4).setCellEditor(new ActionsCellEditor());
    }

    private void setupBottomPanel() {
        this.bottomPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        this.bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                new EmptyBorder(8, 24, 8, 24)
        ));

        this.summaryLabel.setFont(ViewConstants.FONT_BODY);
        this.summaryLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);
        this.bottomPanel.add(this.summaryLabel, BorderLayout.WEST);

        JLabel syncLabel = new JLabel("● Dữ liệu bộ nhớ đồng bộ | Chế độ: MVP Passive View");
        syncLabel.setFont(ViewConstants.FONT_STATUS);
        syncLabel.setForeground(ViewConstants.COLOR_TEXT_DIM);
        this.bottomPanel.add(syncLabel, BorderLayout.EAST);
    }
    // endregion

    // region View Switching & Filtering
    private void switchView(String mode) {
        this.viewportLayout.show(this.viewportPanel, mode);
        boolean isCards = "CARDS".equalsIgnoreCase(mode);
        updateViewModeBtnStyle(this.btnViewCards, isCards);
        updateViewModeBtnStyle(this.btnViewTable, !isCards);
    }

    private void filterAndRender() {
        String keyword = this.searchField.getText().trim().toLowerCase(Locale.ROOT);
        List<WalletItemViewModel> filteredList = this.walletViewModels.stream()
                .filter(w -> {
                    if (keyword.isEmpty()) return true;
                    boolean matchName = w.name() != null && w.name().toLowerCase(Locale.ROOT).contains(keyword);
                    boolean matchNote = w.note() != null && w.note().toLowerCase(Locale.ROOT).contains(keyword);
                    return matchName || matchNote;
                })
                .toList();

        renderCardsView(filteredList);

        // Đồng bộ dữ liệu bảng
        List<WalletResponseDTO> filteredDtos = this.rawWalletDtos.stream()
                .filter(w -> {
                    if (keyword.isEmpty()) return true;
                    boolean matchName = w.getName() != null && w.getName().toLowerCase(Locale.ROOT).contains(keyword);
                    boolean matchNote = w.getNote() != null && w.getNote().toLowerCase(Locale.ROOT).contains(keyword);
                    return matchName || matchNote;
                })
                .toList();
        this.tableModel.setWallets(filteredDtos);
    }

    private void renderCardsView(List<WalletItemViewModel> items) {
        this.cardsGridPanel.removeAll();
        if (items.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);
            emptyPanel.setPreferredSize(new Dimension(800, 200));
            JLabel emptyLabel = new JLabel("Không tìm thấy ví nào phù hợp với bộ lọc hiện tại.");
            emptyLabel.setFont(ViewConstants.FONT_BODY);
            emptyLabel.setForeground(ViewConstants.COLOR_TEXT_DIM);
            emptyPanel.add(emptyLabel);
            this.cardsGridPanel.setLayout(new BorderLayout());
            this.cardsGridPanel.add(emptyPanel, BorderLayout.CENTER);
        } else {
            this.cardsGridPanel.setLayout(new GridLayout(0, 2, 18, 18));
            for (WalletItemViewModel item : items) {
                this.cardsGridPanel.add(createWalletCard(item));
            }
        }
        this.cardsGridPanel.revalidate();
        this.cardsGridPanel.repaint();
    }

    private JPanel createWalletCard(WalletItemViewModel item) {
        RoundedCardPanel card = new RoundedCardPanel(new BorderLayout(0, 6), 10, ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_BORDER);
        card.setBorder(new EmptyBorder(12, 14, 10, 14));
        card.setPreferredSize(new Dimension(360, 175));

        // Phía trên: Tên ví + Badge Trạng thái & Mặc định
        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.setOpaque(false);

        JLabel nameLabel = new JLabel(item.name());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        topRow.add(nameLabel, BorderLayout.WEST);

        JPanel badgesRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        badgesRow.setOpaque(false);

        // Badge trạng thái
        JLabel statusBadge = createStatusBadge(item.statusText(), item.isActive());
        badgesRow.add(statusBadge);

        // Badge mặc định nếu là ví mặc định
        if (item.isDefault()) {
            JLabel defaultBadge = new JLabel("MẶC ĐỊNH");
            defaultBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            defaultBadge.setOpaque(true);
            defaultBadge.setBackground(ViewConstants.COLOR_PRIMARY);
            defaultBadge.setForeground(Color.BLACK);
            defaultBadge.setBorder(new EmptyBorder(2, 6, 2, 6));
            badgesRow.add(defaultBadge);
        }
        topRow.add(badgesRow, BorderLayout.EAST);

        // Phần giữa: Ghi chú + Số dư khả dụng
        JPanel middleBlock = new JPanel();
        middleBlock.setLayout(new BoxLayout(middleBlock, BoxLayout.Y_AXIS));
        middleBlock.setOpaque(false);

        String rawNote = (item.note() != null && !item.note().isBlank()) ? item.note() : "Không có ghi chú";
        String htmlNote = "<html><div style='width: 320px; color: #A1A1AA; font-size: 11px; line-height: 1.3;'>"
                + rawNote.replace("\n", "<br/>") + "</div></html>";
        JLabel noteLabel = new JLabel(htmlNote);
        noteLabel.setPreferredSize(new Dimension(320, 26));
        middleBlock.add(noteLabel);
        middleBlock.add(Box.createVerticalStrut(4));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 20));
        sep.setBackground(new Color(255, 255, 255, 20));
        middleBlock.add(sep);
        middleBlock.add(Box.createVerticalStrut(4));

        JLabel balTitle = new JLabel("SỐ DƯ KHẢ DỤNG");
        balTitle.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        balTitle.setForeground(ViewConstants.COLOR_TEXT_DIM);
        middleBlock.add(balTitle);

        JLabel balValue = new JLabel(item.formattedBalance());
        balValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        balValue.setForeground(item.rawBalance() != null && item.rawBalance().compareTo(BigDecimal.ZERO) < 0
                ? ViewConstants.COLOR_EXPENSE : ViewConstants.COLOR_PRIMARY_LIGHT);
        middleBlock.add(balValue);

        // Phía dưới: Hàng đầy đủ các nút thao tác tương ứng với từng thẻ ví
        JPanel actionsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actionsBar.setOpaque(false);
        actionsBar.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                new EmptyBorder(8, 0, 0, 0)
        ));

        JButton btnAudit = createCardActionBtn("Kiểm kê", ViewConstants.COLOR_TEXT_MAIN, false);
        btnAudit.addActionListener(e -> handleAdjustBalance(item));

        JButton btnEdit = createCardActionBtn("Sửa", ViewConstants.COLOR_TEXT_MAIN, false);
        btnEdit.addActionListener(e -> handleEditWallet(item));

        JButton btnToggle = createCardActionBtn(item.isActive() ? "Lưu trữ" : "Kích hoạt", ViewConstants.COLOR_TEXT_MAIN, false);
        btnToggle.addActionListener(e -> handleToggleStatus(item));

        JButton btnDel = createCardActionBtn("Xóa", ViewConstants.COLOR_EXPENSE, true);
        btnDel.addActionListener(e -> handleDeleteWallet(item));

        actionsBar.add(btnAudit);
        actionsBar.add(btnEdit);
        if (!item.isDefault()) {
            actionsBar.add(btnToggle);
            actionsBar.add(btnDel);
        }

        // Ghép thẻ
        JPanel contentContainer = new JPanel(new BorderLayout(0, 8));
        contentContainer.setOpaque(false);
        contentContainer.add(topRow, BorderLayout.NORTH);
        contentContainer.add(middleBlock, BorderLayout.CENTER);
        contentContainer.add(actionsBar, BorderLayout.SOUTH);

        card.add(contentContainer, BorderLayout.CENTER);
        return card;
    }
    // endregion

    // region Actions
    public void handleAddWallet() {
        this.presenter.onAddWalletClicked();
    }

    public void handleEditWallet() {
        this.presenter.onEditWalletClicked(getSelectedItem());
    }

    public void handleEditWallet(WalletItemViewModel item) {
        if (item != null) this.presenter.onEditWalletClicked(item);
        else showWarning("Vui lòng chọn một ví để thao tác.");
    }

    public void handleAdjustBalance() {
        this.presenter.onAdjustBalanceClicked(getSelectedItem());
    }

    public void handleAdjustBalance(WalletItemViewModel item) {
        if (item != null) this.presenter.onAdjustBalanceClicked(item);
        else showWarning("Vui lòng chọn một ví để thao tác.");
    }

    public void handleToggleStatus() {
        handleToggleStatus(getSelectedItem());
    }

    public void handleToggleStatus(WalletItemViewModel it) {
        if (it != null) this.presenter.toggleStatus(it.id(), it.isActive());
        else showWarning("Vui lòng chọn một ví để thao tác.");
    }

    public void handleDeleteWallet() {
        handleDeleteWallet(getSelectedItem());
    }

    public void handleDeleteWallet(WalletItemViewModel it) {
        if (it != null) this.presenter.deleteWallet(it.id(), it.name());
        else showWarning("Vui lòng chọn một ví để thao tác.");
    }

    @Override
    public void openCreateWalletDialog() {
        CreateWalletDialog d = new CreateWalletDialog(getParentFrame(), getWalletController());
        d.setVisible(true);
        if (d.isSuccess()) reloadWallets();
    }

    @Override
    public void openUpdateWalletDialog(WalletItemViewModel w) {
        if (w != null && findWalletDto(w.id()) != null) {
            UpdateWalletDialog d = new UpdateWalletDialog(getParentFrame(), getWalletController(), findWalletDto(w.id()));
            d.setVisible(true);
            if (d.isSuccess()) reloadWallets();
        }
    }

    @Override
    public void openAdjustBalanceDialog(WalletItemViewModel w) {
        if (w != null && findWalletDto(w.id()) != null) {
            AdjustBalanceDialog d = new AdjustBalanceDialog(getParentFrame(), getWalletController(), findWalletDto(w.id()));
            d.setVisible(true);
            if (d.isSuccess()) reloadWallets();
        }
    }
    // endregion

    // region Data Rendering
    @Override
    public void renderWallets(List<WalletItemViewModel> wallets) {
        this.walletViewModels = (wallets == null) ? Collections.emptyList() : wallets;
        this.rawWalletDtos = this.walletViewModels.stream()
                .map(vm -> new WalletResponseDTO(
                        vm.id(), vm.name(), vm.rawBalance(), "VND", vm.isDefault(),
                        vm.isActive() ? "ACTIVE" : (vm.statusText().equalsIgnoreCase("Đã xóa") ? "DELETED" : "ARCHIVED"),
                        vm.note(), null))
                .toList();

        filterAndRender();

        if (this.onWalletChangedListener != null) {
            this.onWalletChangedListener.run();
        }
    }

    @Override
    public void renderSummary(WalletsSummaryViewModel s) {
        if (s == null) {
            this.summaryLabel.setText("Không có dữ liệu ví.");
            this.kpiTotalBalanceLabel.setText("0 ₫");
            this.kpiActiveCountLabel.setText("0 ví");
            this.kpiArchivedCountLabel.setText("0 ví");
        } else {
            this.summaryLabel.setText("Tổng số ví: " + (s.activeCount() + s.archivedCount()) + " | Tổng số dư: " + s.formattedTotalBalance());
            this.kpiTotalBalanceLabel.setText(s.formattedTotalBalance());
            this.kpiActiveCountLabel.setText(s.activeCount() + " ví");
            this.kpiArchivedCountLabel.setText(s.archivedCount() + " ví");
        }
    }

    public void reloadWallets() {
        this.presenter.loadData();
    }

    private void applyFilter() {
        int i = this.statusFilterBox.getSelectedIndex();
        this.presenter.filterWallets(i <= 0 ? null : (String) this.statusFilterBox.getSelectedItem());
    }

    public void setSelectedStatusFilter(String f) {
        if (f == null) this.statusFilterBox.setSelectedIndex(0);
        else this.statusFilterBox.setSelectedItem(f);
    }
    // endregion

    // region Getters
    public WalletPresenter getPresenter() { return this.presenter; }
    public WalletGUIController getWalletController() { return this.presenter.getController(); }
    public WalletTableModel getTableModel() { return this.tableModel; }
    public JTable getTable() { return this.table; }
    public JScrollPane getScrollPane() { return this.tableScrollPane; }
    public JLabel getSummaryLabel() { return this.summaryLabel; }
    public JButton getAddButton() { return this.addButton; }
    public JButton getEditButton() { return this.editButton; }
    public JButton getAdjustButton() { return this.adjustButton; }
    public JButton getToggleStatusButton() { return this.toggleStatusButton; }
    public JButton getDeleteButton() { return this.deleteButton; }
    public JComboBox<String> getStatusFilterBox() { return this.statusFilterBox; }
    public JPanel getBottomPanel() { return this.bottomPanel; }
    public String getSummaryText() { return this.summaryLabel.getText(); }
    public JButton getBtnViewCards() { return this.btnViewCards; }
    public JButton getBtnViewTable() { return this.btnViewTable; }

    public WalletItemViewModel getSelectedItem() {
        int r = this.table.getSelectedRow();
        if (r >= 0 && r < this.tableModel.getRowCount()) {
            WalletResponseDTO dto = this.tableModel.getWalletAt(r);
            if (dto != null) {
                return findViewModel(dto.getWalletId());
            }
        }
        return null;
    }

    public WalletResponseDTO getSelectedWallet() {
        int r = this.table.getSelectedRow();
        if (r < 0) {
            showWarning("Vui lòng chọn một ví trong bảng để thao tác.");
            return null;
        }
        return this.tableModel.getWalletAt(r);
    }

    public void setOnWalletChangedListener(Runnable l) { this.onWalletChangedListener = l; }
    public void setMessageConsumer(Consumer<String> c) { this.messageConsumer = c; }
    public void setWarningConsumer(Consumer<String> c) { this.warningConsumer = c; }
    public void setErrorConsumer(Consumer<String> c) { this.errorConsumer = c; }
    public void setConfirmConsumer(Predicate<String> c) { this.confirmConsumer = c; }
    // endregion

    // region Component Builders & Renderers
    private JFrame getParentFrame() {
        Component c = SwingUtilities.getWindowAncestor(this);
        return (c instanceof JFrame f) ? f : null;
    }

    private WalletResponseDTO findWalletDto(UUID id) {
        for (WalletResponseDTO w : this.rawWalletDtos) {
            if (w.getWalletId().equals(id)) return w;
        }
        return null;
    }

    private WalletItemViewModel findViewModel(UUID id) {
        for (WalletItemViewModel vm : this.walletViewModels) {
            if (vm.id().equals(id)) return vm;
        }
        return null;
    }

    private JButton createToolBtn(String text, Color bg, Color fg, Font font) {
        JButton b = new JButton(text);
        b.setFont(font);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER),
                new EmptyBorder(6, 14, 6, 14)
        ));
        return b;
    }

    private JButton createCardActionBtn(String text, Color fg, boolean danger) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setBackground(danger ? new Color(251, 113, 133, 16) : new Color(26, 26, 26));
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color borderColor = danger ? new Color(251, 113, 133, 80) : ViewConstants.COLOR_BORDER;
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(4, 12, 4, 12)
        ));
        return b;
    }

    private JButton createViewModeButton(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(ViewConstants.FONT_STATUS);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(88, 30));
        updateViewModeBtnStyle(b, active);
        return b;
    }

    private void updateViewModeBtnStyle(JButton b, boolean active) {
        if (active) {
            b.setBackground(ViewConstants.COLOR_CARD_BG);
            b.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
            b.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_GOLD_BORDER));
        } else {
            b.setBackground(ViewConstants.COLOR_SIDEBAR_BG);
            b.setForeground(ViewConstants.COLOR_TEXT_MUTED);
            b.setBorder(BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER));
        }
    }

    private JPanel createKpiCapsule(String label, JLabel valLabel, Color valColor, Color bg, Color border) {
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setOpaque(false);
        pill.setBorder(new EmptyBorder(3, 10, 3, 10));

        JLabel lbl = new JLabel(label);
        lbl.setFont(ViewConstants.FONT_STATUS);
        lbl.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valLabel.setForeground(valColor);

        pill.add(lbl);
        pill.add(valLabel);
        return pill;
    }

    private JLabel createStatusBadge(String text, boolean active) {
        JLabel badge = new JLabel(text != null ? text.toUpperCase(Locale.ROOT) : "HOẠT ĐỘNG");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setOpaque(true);
        if (active) {
            badge.setBackground(new Color(74, 222, 128, 25));
            badge.setForeground(ViewConstants.COLOR_INCOME);
            badge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(74, 222, 128, 80)),
                    new EmptyBorder(2, 8, 2, 8)
            ));
        } else if ("Đã xóa".equalsIgnoreCase(text) || "DELETED".equalsIgnoreCase(text)) {
            badge.setBackground(new Color(251, 113, 133, 25));
            badge.setForeground(ViewConstants.COLOR_EXPENSE);
            badge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(251, 113, 133, 80)),
                    new EmptyBorder(2, 8, 2, 8)
            ));
        } else {
            badge.setBackground(new Color(251, 191, 36, 25));
            badge.setForeground(new Color(251, 191, 36));
            badge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(251, 191, 36, 80)),
                    new EmptyBorder(2, 8, 2, 8)
            ));
        }
        return badge;
    }

    private DefaultTableCellRenderer createHeaderRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setBackground(new Color(14, 14, 14));
                setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
                setFont(ViewConstants.FONT_NAV_ACTIVE);
                setHorizontalAlignment(c == 1 ? SwingConstants.RIGHT : (c == 2 || c == 4 ? SwingConstants.CENTER : SwingConstants.LEFT));
                return this;
            }
        };
    }

    private DefaultTableCellRenderer createCellRenderer(int align, Color fg) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setHorizontalAlignment(align);
                if (!s) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    setForeground(fg);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 14));
                return this;
            }
        };
    }

    private DefaultTableCellRenderer createStatusRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                String val = (v != null) ? v.toString() : "";
                if (!s) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    if ("Hoạt động".equalsIgnoreCase(val)) setForeground(ViewConstants.COLOR_INCOME);
                    else if ("Đã xóa".equalsIgnoreCase(val)) setForeground(ViewConstants.COLOR_EXPENSE);
                    else setForeground(new Color(251, 191, 36));
                }
                setText(val);
                return this;
            }
        };
    }

    private DefaultTableCellRenderer createWalletNameRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setHorizontalAlignment(SwingConstants.LEFT);
                if (!s) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    setForeground(ViewConstants.COLOR_TEXT_MAIN);
                }
                WalletResponseDTO w = (r >= 0 && r < tableModel.getRowCount()) ? tableModel.getWalletAt(r) : null;
                String name = (v != null) ? v.toString() : "";
                if (w != null && w.isDefaultWallet()) {
                    setText("<html><div style='padding: 4px 0;'><span style='font-size: 13px; font-weight: bold; color: #FAFAFA;'>" + name + "</span><br/><span style='color: #D4AF37; font-size: 10px; font-weight: bold;'>[Ví mặc định]</span></div></html>");
                } else if (w != null && "ACTIVE".equalsIgnoreCase(w.getStatus())) {
                    setText("<html><div style='padding: 4px 0;'><span style='font-size: 13px; font-weight: bold; color: #FAFAFA;'>" + name + "</span><br/><span style='color: #E5C07B; font-size: 10px;'>+ Đặt mặc định</span></div></html>");
                } else {
                    setText("<html><div style='padding: 4px 0;'><span style='font-size: 13px; font-weight: bold; color: #FAFAFA;'>" + name + "</span></div></html>");
                }
                return this;
            }
        };
    }

    private DefaultTableCellRenderer createNoteRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setHorizontalAlignment(SwingConstants.LEFT);
                if (!s) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    setForeground(ViewConstants.COLOR_TEXT_MUTED);
                }
                String note = (v != null && !v.toString().isBlank()) ? v.toString() : "-";
                String htmlNote = note.replace("\n", "<br/>");
                setText("<html><div style='padding: 2px 0;'>" + htmlNote + "</div></html>");
                return this;
            }
        };
    }
    // endregion

    // region Table Actions Column Renderer & Editor
    private class ActionsPanel extends JPanel {
        final JButton btnAdjust = createActionBtn("Kiểm kê", ViewConstants.COLOR_TEXT_MAIN, false);
        final JButton btnEdit = createActionBtn("Sửa", ViewConstants.COLOR_TEXT_MAIN, false);
        final JButton btnToggle = createActionBtn("Lưu trữ", ViewConstants.COLOR_TEXT_MAIN, false);
        final JButton btnDelete = createActionBtn("Xóa", ViewConstants.COLOR_EXPENSE, true);
        WalletItemViewModel item;

        ActionsPanel() {
            super(new FlowLayout(FlowLayout.CENTER, 4, 10));
            setOpaque(true);
            setBackground(ViewConstants.COLOR_CARD_BG);
            add(btnAdjust);
            add(btnEdit);
            add(btnToggle);
            add(btnDelete);
        }

        void bind(WalletResponseDTO dto) {
            if (dto == null) {
                this.item = null;
                return;
            }
            this.item = findViewModel(dto.getWalletId());
            boolean isDefault = dto.isDefaultWallet();
            boolean isActive = "ACTIVE".equalsIgnoreCase(dto.getStatus());
            btnToggle.setText(isActive ? "Lưu trữ" : "Kích hoạt");
            btnToggle.setVisible(!isDefault);
            btnDelete.setVisible(!isDefault);
        }

        private JButton createActionBtn(String text, Color fg, boolean danger) {
            JButton b = new JButton(text);
            b.setFont(new Font("Segoe UI", Font.BOLD, 10));
            b.setBackground(danger ? new Color(251, 113, 133, 20) : new Color(26, 26, 26));
            b.setForeground(fg);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            Color border = danger ? new Color(251, 113, 133, 80) : ViewConstants.COLOR_BORDER;
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(border),
                    new EmptyBorder(3, 8, 3, 8)
            ));
            return b;
        }
    }

    private class ActionsCellRenderer implements TableCellRenderer {
        private final ActionsPanel panel = new ActionsPanel();

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            if (s) panel.setBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
            else panel.setBackground(ViewConstants.COLOR_CARD_BG);

            WalletResponseDTO dto = (v instanceof WalletResponseDTO d) ? d : tableModel.getWalletAt(r);
            panel.bind(dto);
            int colWidth = (t.getColumnModel().getColumnCount() > c) ? t.getColumnModel().getColumn(c).getWidth() : 250;
            panel.setSize(colWidth, t.getRowHeight(r));
            panel.doLayout();
            return panel;
        }
    }

    private class ActionsCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final ActionsPanel panel = new ActionsPanel();

        ActionsCellEditor() {
            panel.btnAdjust.addActionListener(e -> {
                fireEditingStopped();
                if (panel.item != null) handleAdjustBalance(panel.item);
            });
            panel.btnEdit.addActionListener(e -> {
                fireEditingStopped();
                if (panel.item != null) handleEditWallet(panel.item);
            });
            panel.btnToggle.addActionListener(e -> {
                fireEditingStopped();
                if (panel.item != null) handleToggleStatus(panel.item);
            });
            panel.btnDelete.addActionListener(e -> {
                fireEditingStopped();
                if (panel.item != null) handleDeleteWallet(panel.item);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            panel.setBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
            WalletResponseDTO dto = (v instanceof WalletResponseDTO d) ? d : tableModel.getWalletAt(r);
            panel.bind(dto);
            int colWidth = (t.getColumnModel().getColumnCount() > c) ? t.getColumnModel().getColumn(c).getWidth() : 250;
            panel.setSize(colWidth, t.getRowHeight(r));
            panel.doLayout();
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return panel.item;
        }
    }
    // endregion

    // region BaseView Messages
    @Override
    public void showMessage(String m) {
        if (messageConsumer != null) messageConsumer.accept(m);
        else JOptionPane.showMessageDialog(this, m, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showWarning(String m) {
        if (warningConsumer != null) warningConsumer.accept(m);
        else JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void showError(String m) {
        if (errorConsumer != null) errorConsumer.accept(m);
        else JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public boolean confirmAction(String m) {
        return (confirmConsumer != null) ? confirmConsumer.test(m)
                : JOptionPane.showConfirmDialog(this, m, "Xác nhận thao tác", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
    // endregion
}
