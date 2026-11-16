package com.pfms.presentation.category;

import com.pfms.presentation.category.component.CategoryTableActionRenderer;
import com.pfms.presentation.category.component.CategoryTreeCellRenderer;
import com.pfms.presentation.category.dialog.CreateCategoryDialog;
import com.pfms.presentation.category.dialog.CreateNeedGroupDialog;
import com.pfms.presentation.category.dialog.UpdateCategoryDialog;
import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.common.component.RoundedBorder;
import com.pfms.presentation.common.component.RoundedCardPanel;
import com.pfms.presentation.common.component.SearchIcon;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Quản lý Danh mục Thu Chi (Passive View).
 * Trình diễn cấu trúc phân cấp đa tầng (JTree), bảng chi tiết kèm thao tác nhanh trực tiếp (Inline Actions),
 * khung bo tròn mềm mại và thanh tìm kiếm vector thời gian thực theo chuẩn thiết kế Luxury Black &amp; Gold.
 */
public class CategoryPanel extends JPanel implements CategoryView {

    // region Fields
    private final CategoryPresenter presenter;
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Hệ thống Danh mục PFMS");
    private final DefaultTreeModel treeModel = new DefaultTreeModel(this.rootNode);
    private final JTree tree = new JTree(this.treeModel);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Loại", "Nhóm Nhu cầu", "Tên Danh mục", "Trạng thái", "Thao tác"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return c == 4;
        }
    };
    private final JTable detailTable = new JTable(this.tableModel);

    private final JLabel summaryLabel = new JLabel("Tổng số nhóm: 0 | Tổng số danh mục: 0");
    private final JLabel statTotalGroups = createStatLabel("Tổng nhóm: 0", ViewConstants.COLOR_TEXT_MAIN);
    private final JLabel statTotalCats = createStatLabel("Tổng danh mục: 0", ViewConstants.COLOR_TEXT_MAIN);
    private final JLabel statExpense = createStatLabel("Chi phí: 0", ViewConstants.COLOR_EXPENSE);
    private final JLabel statIncome = createStatLabel("Thu nhập: 0", ViewConstants.COLOR_INCOME);
    private final JLabel tableCountLabel = new JLabel("0 kết quả");

    private final JTextField searchField = new JTextField(15);

    private List<CategoryHierarchyResponseDTO> cachedHierarchy = Collections.emptyList();
    private Runnable onCategoryChangedListener;
    private Consumer<String> messageConsumer, warningConsumer, errorConsumer;
    private Predicate<String> confirmConsumer;
    private Function<String, String> inputSupplier;
    // endregion

    // region Constructors
    public CategoryPanel(CategoryGUIController categoryController) {
        Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");
        this.presenter = new CategoryPresenter(this, categoryController);
        initViews();
        reloadCategories();
    }
    // endregion

    // region UI
    private void initViews() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ViewConstants.COLOR_CONTENT_BG);

        this.tree.setFont(ViewConstants.FONT_BODY);
        this.tree.setBackground(ViewConstants.COLOR_CARD_BG);
        this.tree.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.tree.setCellRenderer(new CategoryTreeCellRenderer());
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.tree.addTreeSelectionListener(e -> onTreeNodeSelected());
        setupTreeContextMenu();

        setupDetailTable();

        this.summaryLabel.setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.summaryLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);

        // North Header Area
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(ViewConstants.COLOR_CONTENT_BG);
        north.setBorder(BorderFactory.createEmptyBorder(18, 24, 12, 24));

        // Row 1: Title + Quick Stats Pills (Rounded Capsules)
        JPanel headerTop = new JPanel(new BorderLayout(16, 0));
        headerTop.setBackground(ViewConstants.COLOR_CONTENT_BG);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel title = new JLabel("Danh mục Thu & Chi");
        title.setFont(ViewConstants.FONT_PANEL_HEADER);
        title.setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);

        JLabel subtitle = new JLabel("Hệ thống phân cấp 2 tầng: Nhóm nhu cầu (Cha) & Danh mục thu chi (Con)");
        subtitle.setFont(ViewConstants.FONT_BODY);
        subtitle.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(subtitle);
        headerTop.add(titleBox, BorderLayout.WEST);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        statsPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);
        statsPanel.add(wrapStatPill(this.statTotalGroups));
        statsPanel.add(wrapStatPill(this.statTotalCats));
        statsPanel.add(wrapStatPill(this.statExpense));
        statsPanel.add(wrapStatPill(this.statIncome));
        headerTop.add(statsPanel, BorderLayout.EAST);

        // Row 2: Action Toolbar (Buttons on Left, Rounded Vector Search on Right)
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setBackground(ViewConstants.COLOR_CONTENT_BG);

        JPanel btnGroupLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnGroupLeft.setBackground(ViewConstants.COLOR_CONTENT_BG);
        btnGroupLeft.add(toolBtn("+ Thêm Nhóm cha", ViewConstants.COLOR_PRIMARY, Color.BLACK, ViewConstants.COLOR_PRIMARY_LIGHT, e -> this.presenter.onAddNeedGroupClicked()));
        btnGroupLeft.add(toolBtn("+ Thêm Danh mục con", ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_TEXT_MAIN, ViewConstants.COLOR_BORDER, e -> this.presenter.onAddCategoryClicked()));
        bar.add(btnGroupLeft, BorderLayout.WEST);

        // Rounded Search Capsule Box with Vector SearchIcon
        RoundedCardPanel searchBox = new RoundedCardPanel(new BorderLayout(8, 0), 16, ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_BORDER);
        searchBox.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        searchBox.setPreferredSize(new Dimension(230, 34));

        JLabel searchIconLabel = new JLabel(new SearchIcon(14, ViewConstants.COLOR_PRIMARY_LIGHT));

        this.searchField.setFont(ViewConstants.FONT_BODY);
        this.searchField.setBackground(ViewConstants.COLOR_CARD_BG);
        this.searchField.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        this.searchField.setCaretColor(ViewConstants.COLOR_PRIMARY_LIGHT);
        this.searchField.setBorder(null);
        this.searchField.setOpaque(false);
        this.searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm danh mục...");
        this.searchField.setToolTipText("Tìm kiếm theo tên danh mục hoặc nhóm nhu cầu...");
        this.searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onFilterChanged(); }
            @Override public void removeUpdate(DocumentEvent e) { onFilterChanged(); }
            @Override public void changedUpdate(DocumentEvent e) { onFilterChanged(); }
        });

        searchBox.add(searchIconLabel, BorderLayout.WEST);
        searchBox.add(this.searchField, BorderLayout.CENTER);
        bar.add(searchBox, BorderLayout.EAST);

        north.add(headerTop);
        north.add(Box.createVerticalStrut(14));
        north.add(bar);
        add(north, BorderLayout.NORTH);

        // Center Split Pane with Rounded Card Frames
        JScrollPane treeSp = new JScrollPane(this.tree);
        treeSp.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        treeSp.setBackground(ViewConstants.COLOR_CARD_BG);
        treeSp.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        RoundedCardPanel treeCard = new RoundedCardPanel(new BorderLayout(), 14, ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_BORDER);
        treeCard.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel treeHeaderBar = new JPanel(new BorderLayout());
        treeHeaderBar.setBackground(new Color(18, 18, 18));
        treeHeaderBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        JLabel treeTitleLabel = new JLabel("CÂY PHÂN CẤP DANH MỤC");
        treeTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        treeTitleLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        JLabel treeTagLabel = new JLabel("CẤU TRÚC 2 TẦNG");
        treeTagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        treeTagLabel.setForeground(ViewConstants.COLOR_TEXT_DIM);

        treeHeaderBar.add(treeTitleLabel, BorderLayout.WEST);
        treeHeaderBar.add(treeTagLabel, BorderLayout.EAST);
        treeCard.add(treeHeaderBar, BorderLayout.NORTH);
        treeCard.add(treeSp, BorderLayout.CENTER);

        JPanel treeContainer = new JPanel(new BorderLayout());
        treeContainer.setBackground(ViewConstants.COLOR_CONTENT_BG);
        treeContainer.setBorder(BorderFactory.createEmptyBorder(0, 24, 12, 6));
        treeContainer.add(treeCard, BorderLayout.CENTER);

        JScrollPane tblSp = new JScrollPane(this.detailTable);
        tblSp.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        tblSp.setBackground(ViewConstants.COLOR_CARD_BG);
        tblSp.setBorder(null);

        RoundedCardPanel tableCard = new RoundedCardPanel(new BorderLayout(), 14, ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_BORDER);
        tableCard.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel tableHeaderBar = new JPanel(new BorderLayout());
        tableHeaderBar.setBackground(new Color(18, 18, 18));
        tableHeaderBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        JLabel tableTitleLabel = new JLabel("BẢNG DANH MỤC CHI TIẾT");
        tableTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tableTitleLabel.setForeground(ViewConstants.COLOR_TEXT_MUTED);

        this.tableCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        this.tableCountLabel.setForeground(ViewConstants.COLOR_TEXT_DIM);

        tableHeaderBar.add(tableTitleLabel, BorderLayout.WEST);
        tableHeaderBar.add(this.tableCountLabel, BorderLayout.EAST);
        tableCard.add(tableHeaderBar, BorderLayout.NORTH);
        tableCard.add(tblSp, BorderLayout.CENTER);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(ViewConstants.COLOR_CONTENT_BG);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 6, 12, 24));
        tableContainer.add(tableCard, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeContainer, tableContainer);
        split.setDividerLocation(360);
        split.setResizeWeight(0.32);
        split.setBorder(null);
        split.setBackground(ViewConstants.COLOR_CONTENT_BG);
        add(split, BorderLayout.CENTER);

        // South Status Bar
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(ViewConstants.COLOR_STATUS_BG);
        south.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(8, 24, 8, 24)));
        south.add(this.summaryLabel, BorderLayout.WEST);

        JLabel syncLabel = new JLabel("● Đã đồng bộ bộ nhớ | Chế độ: MVP Passive View");
        syncLabel.setFont(ViewConstants.FONT_STATUS);
        syncLabel.setForeground(ViewConstants.COLOR_TEXT_DIM);
        south.add(syncLabel, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);
    }

    private void setupDetailTable() {
        com.pfms.presentation.common.ViewUtils.styleTable(this.detailTable);
        this.detailTable.setRowHeight(42);
        if (this.detailTable.getTableHeader() != null) {
            Dimension headerDim = this.detailTable.getTableHeader().getPreferredSize();
            this.detailTable.getTableHeader().setPreferredSize(new Dimension(headerDim.width, 36));
        }

        DefaultTableCellRenderer l = com.pfms.presentation.common.ViewUtils.createCellRenderer(SwingConstants.LEFT, ViewConstants.COLOR_TEXT_MAIN);
        DefaultTableCellRenderer groupRenderer = com.pfms.presentation.common.ViewUtils.createCellRenderer(SwingConstants.LEFT, ViewConstants.COLOR_TEXT_MUTED);

        DefaultTableCellRenderer typeRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                if (!isSelected) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    boolean isExpense = "EXPENSE".equalsIgnoreCase(String.valueOf(value));
                    setForeground(isExpense ? ViewConstants.COLOR_EXPENSE : ViewConstants.COLOR_INCOME);
                }
                return this;
            }
        };

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                if (!isSelected) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    setForeground(ViewConstants.COLOR_INCOME);
                }
                return this;
            }
        };

        CategoryTableActionRenderer actionRenderer = new CategoryTableActionRenderer(
                this::handleEditFromRow,
                this::handleDeleteFromRow
        );

        int[] w = {110, 200, 260, 110, 120};
        for (int i = 0; i < Math.min(5, this.detailTable.getColumnModel().getColumnCount()); i++) {
            this.detailTable.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
            if (i == 0) {
                this.detailTable.getColumnModel().getColumn(i).setCellRenderer(typeRenderer);
            } else if (i == 1) {
                this.detailTable.getColumnModel().getColumn(i).setCellRenderer(groupRenderer);
            } else if (i == 2) {
                this.detailTable.getColumnModel().getColumn(i).setCellRenderer(l);
            } else if (i == 3) {
                this.detailTable.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
            } else if (i == 4) {
                this.detailTable.getColumnModel().getColumn(i).setCellRenderer(actionRenderer);
                this.detailTable.getColumnModel().getColumn(i).setCellEditor(actionRenderer);
            }
        }

        this.detailTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int row = detailTable.getSelectedRow();
                    if (row >= 0 && row < tableModel.getRowCount()) {
                        Object obj = tableModel.getValueAt(row, 4);
                        handleEditFromRow(obj);
                    }
                }
            }
        });
    }

    private void setupTreeContextMenu() {
        this.tree.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { maybeShowPopup(e); }
            @Override public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int selRow = tree.getClosestRowForLocation(e.getX(), e.getY());
                    if (selRow >= 0) {
                        tree.setSelectionRow(selRow);
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                        if (node != null && !node.isRoot()) {
                            showTreePopupMenu(e.getComponent(), e.getX(), e.getY(), node.getUserObject());
                        }
                    }
                }
            }
        });
    }

    private void showTreePopupMenu(Component comp, int x, int y, Object userObj) {
        JPopupMenu menu = new JPopupMenu();
        if (userObj instanceof NeedGroupNode gn) {
            JMenuItem addCat = new JMenuItem("+ Thêm Danh mục vào nhóm này");
            addCat.setFont(ViewConstants.FONT_BODY);
            addCat.addActionListener(e -> presenter.onAddCategoryClicked());

            JMenuItem rename = new JMenuItem("Đổi tên nhóm...");
            rename.setFont(ViewConstants.FONT_BODY);
            rename.addActionListener(e -> presenter.renameNeedGroup(gn.id(), gn.name()));

            JMenuItem delete = new JMenuItem("Xóa nhóm nhu cầu");
            delete.setFont(ViewConstants.FONT_BODY);
            delete.setForeground(ViewConstants.COLOR_EXPENSE);
            delete.addActionListener(e -> presenter.deleteNeedGroup(gn.id(), gn.name()));

            menu.add(addCat);
            menu.add(rename);
            menu.addSeparator();
            menu.add(delete);
            menu.show(comp, x, y);
        } else if (userObj instanceof CategoryNode cn) {
            UUID parentGroupId = findParentGroupId(cn.id());
            JMenuItem edit = new JMenuItem("Chỉnh sửa danh mục...");
            edit.setFont(ViewConstants.FONT_BODY);
            edit.addActionListener(e -> presenter.onEditCategoryClicked(cn.id(), cn.name(), parentGroupId, cn.type()));

            JMenuItem delete = new JMenuItem("Xóa danh mục");
            delete.setFont(ViewConstants.FONT_BODY);
            delete.setForeground(ViewConstants.COLOR_EXPENSE);
            delete.addActionListener(e -> presenter.deleteCategory(cn.id(), cn.name()));

            menu.add(edit);
            menu.addSeparator();
            menu.add(delete);
            menu.show(comp, x, y);
        }
    }

    private static JLabel createStatLabel(String text, Color fgColor) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ViewConstants.FONT_STATUS);
        lbl.setForeground(fgColor);
        return lbl;
    }

    private static RoundedCardPanel wrapStatPill(JLabel label) {
        RoundedCardPanel pill = new RoundedCardPanel(new FlowLayout(FlowLayout.CENTER, 0, 0), 16, ViewConstants.COLOR_CARD_BG, ViewConstants.COLOR_BORDER);
        pill.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        pill.add(label);
        return pill;
    }

    private JButton toolBtn(String text, Color bg, Color fg, Color borderColor, ActionListener l) {
        JButton b = new JButton(text);
        b.setFont(ViewConstants.FONT_NAV_ACTIVE);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.putClientProperty("Component.arc", 10);
        b.putClientProperty("JButton.buttonType", "roundRect");
        b.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, borderColor, new Insets(1, 4, 1, 4)),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        b.addActionListener(l);
        return b;
    }
    // endregion

    // region Actions
    private void handleEditFromRow(Object rowDataObj) {
        if (rowDataObj instanceof CategoryRowData row) {
            this.presenter.onEditCategoryClicked(row.id(), row.name(), row.parentGroupId(), row.type());
        }
    }

    private void handleDeleteFromRow(Object rowDataObj) {
        if (rowDataObj instanceof CategoryRowData row) {
            this.presenter.deleteCategory(row.id(), row.name());
        }
    }

    @Override
    public void openCreateNeedGroupDialog() {
        CreateNeedGroupDialog d = new CreateNeedGroupDialog((JFrame) SwingUtilities.getWindowAncestor(this), this.presenter.getController());
        d.setVisible(true);
        if (d.isSuccess()) {
            showMessage("Đã tạo nhóm nhu cầu thành công!");
            reloadCategories();
        }
    }

    @Override
    public void openCreateCategoryDialog() {
        CreateCategoryDialog d = new CreateCategoryDialog((JFrame) SwingUtilities.getWindowAncestor(this), this.presenter.getController());
        d.setVisible(true);
        if (d.isSuccess()) {
            showMessage("Đã tạo danh mục con thành công!");
            reloadCategories();
        }
    }

    @Override
    public void openUpdateCategoryDialog(UUID categoryId, String currentName, UUID currentParentGroupId, String transactionType) {
        UpdateCategoryDialog d = new UpdateCategoryDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                this.presenter.getController(),
                categoryId, currentName, currentParentGroupId, transactionType);
        d.setVisible(true);
        if (d.isSuccess()) {
            showMessage("Cập nhật danh mục thành công!");
            reloadCategories();
        }
    }

    @Override
    public String openRenameDialog(String currentName, boolean isGroup) {
        String prompt = "Nhập tên mới cho " + (isGroup ? "nhóm nhu cầu" : "danh mục con") + " (hiện tại: " + currentName + "):";
        return (this.inputSupplier != null) ? this.inputSupplier.apply(prompt) : JOptionPane.showInputDialog(this, prompt);
    }
    // endregion

    // region Data & Filtering
    @Override
    public void renderCategoryHierarchy(List<CategoryHierarchyResponseDTO> hierarchy, int totalGroups, int totalCats) {
        this.cachedHierarchy = (hierarchy != null) ? hierarchy : Collections.emptyList();
        buildTreeNodes();
        updateSummary(totalGroups, totalCats);
        onTreeNodeSelected();
        if (this.onCategoryChangedListener != null) {
            this.onCategoryChangedListener.run();
        }
    }

    public void reloadCategories() {
        this.presenter.loadData();
    }

    private void buildTreeNodes() {
        this.rootNode.removeAllChildren();
        DefaultMutableTreeNode expBranch = new DefaultMutableTreeNode("Khoản Chi (EXPENSE)");
        DefaultMutableTreeNode incBranch = new DefaultMutableTreeNode("Khoản Thu (INCOME)");

        for (CategoryHierarchyResponseDTO g : this.cachedHierarchy) {
            DefaultMutableTreeNode gNode = new DefaultMutableTreeNode(
                    new NeedGroupNode(g.getNeedGroupId(), g.getNeedGroupName(), g.getTransactionType(), g.getNeedGroupStatus()));
            if (g.getCategories() != null) {
                for (CategoryResponseDTO c : g.getCategories()) {
                    gNode.add(new DefaultMutableTreeNode(
                            new CategoryNode(c.getCategoryId(), c.getName(), c.getTransactionType(), c.getStatus())));
                }
            }
            if ("EXPENSE".equalsIgnoreCase(g.getTransactionType())) {
                expBranch.add(gNode);
            } else {
                incBranch.add(gNode);
            }
        }
        this.rootNode.add(expBranch);
        this.rootNode.add(incBranch);
        this.treeModel.reload();
        for (int i = 0; i < this.tree.getRowCount(); i++) {
            this.tree.expandRow(i);
        }
    }

    private void updateSummary(int totalGroups, int totalCategories) {
        int expCats = 0;
        int incCats = 0;
        for (CategoryHierarchyResponseDTO g : this.cachedHierarchy) {
            int count = g.getCategoryCount();
            if ("EXPENSE".equalsIgnoreCase(g.getTransactionType())) {
                expCats += count;
            } else {
                incCats += count;
            }
        }
        this.statTotalGroups.setText("Tổng số nhóm: " + totalGroups);
        this.statTotalCats.setText("Tổng danh mục: " + totalCategories);
        this.statExpense.setText("Chi phí: " + expCats);
        this.statIncome.setText("Thu nhập: " + incCats);
        this.summaryLabel.setText(String.format("Tổng số nhóm: %d | Tổng số danh mục: %d (Chi phí: %d | Thu nhập: %d)",
                totalGroups, totalCategories, expCats, incCats));
    }

    private void onFilterChanged() {
        onTreeNodeSelected();
    }

    private void onTreeNodeSelected() {
        this.tableModel.setRowCount(0);
        String keyword = (this.searchField != null) ? this.searchField.getText().trim().toLowerCase(Locale.ROOT) : "";
        DefaultMutableTreeNode sel = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();

        Predicate<CategoryHierarchyResponseDTO> treeGroupFilter;
        Predicate<CategoryResponseDTO> catNodeFilter = null;

        if (sel == null || sel.isRoot()) {
            treeGroupFilter = g -> true;
        } else {
            Object obj = sel.getUserObject();
            if (obj instanceof NeedGroupNode gn) {
                treeGroupFilter = g -> gn.id().equals(g.getNeedGroupId());
            } else if (obj instanceof CategoryNode cn) {
                treeGroupFilter = g -> true;
                catNodeFilter = c -> cn.id().equals(c.getCategoryId());
            } else if (obj instanceof String b) {
                String type = b.contains("EXPENSE") ? "EXPENSE" : "INCOME";
                treeGroupFilter = g -> type.equalsIgnoreCase(g.getTransactionType());
            } else {
                treeGroupFilter = g -> true;
            }
        }

        for (CategoryHierarchyResponseDTO g : this.cachedHierarchy) {
            if (treeGroupFilter.test(g) && g.getCategories() != null) {
                for (CategoryResponseDTO c : g.getCategories()) {
                    if (catNodeFilter != null && !catNodeFilter.test(c)) {
                        continue;
                    }
                    if (!keyword.isEmpty()) {
                        boolean matchCatName = c.getName() != null && c.getName().toLowerCase(Locale.ROOT).contains(keyword);
                        boolean matchGroupName = g.getNeedGroupName() != null && g.getNeedGroupName().toLowerCase(Locale.ROOT).contains(keyword);
                        if (!matchCatName && !matchGroupName) {
                            continue;
                        }
                    }
                    CategoryRowData rowData = new CategoryRowData(
                            c.getCategoryId(), c.getName(), g.getNeedGroupId(), g.getNeedGroupName(), c.getTransactionType(), c.getStatus());
                    this.tableModel.addRow(new Object[]{
                            c.getTransactionType(),
                            g.getNeedGroupName(),
                            c.getName(),
                            c.getStatus(),
                            rowData
                    });
                }
            }
        }
        this.tableCountLabel.setText(this.tableModel.getRowCount() + " kết quả");
    }
    // endregion

    // region Getters & Records
    public JTree getTree() { return this.tree; }
    public JTable getTable() { return this.detailTable; }
    public JTextField getSearchField() { return this.searchField; }
    public String getSummaryText() { return this.summaryLabel.getText(); }
    public CategoryPresenter getPresenter() { return this.presenter; }
    public CategoryGUIController getCategoryController() { return this.presenter.getController(); }

    public void setOnCategoryChangedListener(Runnable l) { this.onCategoryChangedListener = l; }
    public void setMessageConsumer(Consumer<String> c) { this.messageConsumer = Objects.requireNonNull(c); }
    public void setWarningConsumer(Consumer<String> c) { this.warningConsumer = Objects.requireNonNull(c); }
    public void setErrorConsumer(Consumer<String> c) { this.errorConsumer = Objects.requireNonNull(c); }
    public void setConfirmConsumer(Predicate<String> c) { this.confirmConsumer = Objects.requireNonNull(c); }
    public void setInputSupplier(Function<String, String> s) { this.inputSupplier = Objects.requireNonNull(s); }

    public record NeedGroupNode(UUID id, String name, String type, String status) {
        @Override public String toString() { return name + " (" + status + ")"; }
    }

    public record CategoryNode(UUID id, String name, String type, String status) {
        @Override public String toString() { return name; }
    }

    public record CategoryRowData(UUID id, String name, UUID parentGroupId, String groupName, String type, String status) {}
    // endregion

    // region Helpers
    @Override public void showMessage(String m) { if (messageConsumer != null) messageConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.INFORMATION_MESSAGE); }
    @Override public void showWarning(String m) { if (warningConsumer != null) warningConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Cảnh báo", JOptionPane.WARNING_MESSAGE); }
    @Override public void showError(String m) { if (errorConsumer != null) errorConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE); }
    @Override public boolean confirmAction(String m) { return (confirmConsumer != null) ? confirmConsumer.test(m) : JOptionPane.showConfirmDialog(this, m, "Xác nhận thao tác", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION; }

    private UUID findParentGroupId(UUID categoryId) {
        if (categoryId == null) {
            return null;
        }
        for (CategoryHierarchyResponseDTO group : this.cachedHierarchy) {
            if (group.getCategories() != null) {
                for (CategoryResponseDTO cat : group.getCategories()) {
                    if (categoryId.equals(cat.getCategoryId())) {
                        return group.getNeedGroupId();
                    }
                }
            }
        }
        return null;
    }
    // endregion
}
