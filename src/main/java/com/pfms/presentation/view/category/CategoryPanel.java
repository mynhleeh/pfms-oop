package com.pfms.presentation.view.category;

import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.view.category.dialog.CreateCategoryDialog;
import com.pfms.presentation.view.category.dialog.CreateNeedGroupDialog;
import com.pfms.presentation.view.common.ViewConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Quản lý Danh mục Thu Chi (CARD_CATEGORIES).
 * Trình diễn cấu trúc phân cấp đa tầng dạng cây (JTree) và bảng chi tiết danh mục con.
 */
public class CategoryPanel extends JPanel {

    private final CategoryGUIController categoryController;

    private final DefaultMutableTreeNode rootNode;
    private final DefaultTreeModel treeModel;
    private final JTree tree;

    private final DefaultTableModel tableModel;
    private final JTable detailTable;
    private final JLabel summaryLabel;

    private List<CategoryHierarchyResponseDTO> cachedHierarchy = new ArrayList<>();
    private Runnable onCategoryChangedListener;

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
    private java.util.function.Function<String, String> inputSupplier = prompt -> {
        return JOptionPane.showInputDialog(this, prompt);
    };

    public CategoryPanel(CategoryGUIController categoryController) {
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");

        setLayout(new BorderLayout(10, 10));
        setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Khởi tạo JTree
        this.rootNode = new DefaultMutableTreeNode("Hệ thống Danh mục PFMS");
        this.treeModel = new DefaultTreeModel(this.rootNode);
        this.tree = new JTree(this.treeModel);
        this.tree.setFont(ViewConstants.FONT_BODY);
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.tree.addTreeSelectionListener(e -> onTreeNodeSelected());

        // Khởi tạo JTable
        String[] columns = {"Loại", "Nhóm Nhu cầu", "Tên Danh mục", "Trạng thái"};
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.detailTable = new JTable(this.tableModel);
        setupDetailTable();

        this.summaryLabel = new JLabel("Tổng số nhóm: 0 | Tổng số danh mục: 0");
        this.summaryLabel.setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.summaryLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);

        initViews();
        reloadCategories();
    }

    private void initViews() {
        // North Container: Tiêu đề và Thanh công cụ
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JLabel titleLabel = new JLabel("Quản lý Danh mục Thu Chi");
        titleLabel.setFont(ViewConstants.FONT_PANEL_HEADER);
        titleLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(ViewConstants.COLOR_CONTENT_BG);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addGroupButton = createToolButton("+ Thêm Nhóm cha", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE);
        addGroupButton.addActionListener(e -> openCreateNeedGroupDialog());

        JButton addCategoryButton = createToolButton("+ Thêm Danh mục con", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE);
        addCategoryButton.addActionListener(e -> openCreateCategoryDialog());

        JButton renameButton = createToolButton("Đổi tên", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        renameButton.addActionListener(e -> handleRenameSelected());

        JButton deleteButton = createToolButton("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE);
        deleteButton.addActionListener(e -> handleDeleteSelected());

        JButton refreshButton = createToolButton("Làm mới", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN);
        refreshButton.addActionListener(e -> reloadCategories());

        toolbar.add(addGroupButton);
        toolbar.add(addCategoryButton);
        toolbar.add(renameButton);
        toolbar.add(deleteButton);
        toolbar.add(refreshButton);

        northPanel.add(titleLabel);
        northPanel.add(Box.createVerticalStrut(12));
        northPanel.add(toolbar);
        add(northPanel, BorderLayout.NORTH);

        // Center: JSplitPane (Left: JTree, Right: JTable)
        JScrollPane treeScrollPane = new JScrollPane(this.tree);
        treeScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 24, 12, 6),
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)));
        treeScrollPane.getViewport().setBackground(Color.WHITE);

        JScrollPane tableScrollPane = new JScrollPane(this.detailTable);
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 6, 12, 24),
                BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)));
        tableScrollPane.getViewport().setBackground(Color.WHITE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, tableScrollPane);
        splitPane.setDividerLocation(360);
        splitPane.setResizeWeight(0.35);
        splitPane.setBorder(null);
        splitPane.setBackground(ViewConstants.COLOR_CONTENT_BG);
        add(splitPane, BorderLayout.CENTER);

        // South: Thanh tóm tắt
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10));
        southPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        southPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        southPanel.add(this.summaryLabel);
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

    private void setupDetailTable() {
        this.detailTable.setFont(ViewConstants.FONT_BODY);
        this.detailTable.setRowHeight(28);
        this.detailTable.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.detailTable.getTableHeader().setBackground(new Color(245, 245, 245));
        this.detailTable.setGridColor(ViewConstants.COLOR_BORDER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

        if (this.detailTable.getColumnModel().getColumnCount() >= 4) {
            this.detailTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            this.detailTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

            this.detailTable.getColumnModel().getColumn(1).setPreferredWidth(180);
            this.detailTable.getColumnModel().getColumn(2).setPreferredWidth(200);

            this.detailTable.getColumnModel().getColumn(3).setPreferredWidth(110);
            this.detailTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        }
    }

    public void reloadCategories() {
        try {
            this.cachedHierarchy = this.categoryController.loadCategoryHierarchy();
            buildTreeNodes();
            updateSummary();
            onTreeNodeSelected();

            if (this.onCategoryChangedListener != null) {
                this.onCategoryChangedListener.run();
            }
        } catch (Exception ex) {
            this.errorConsumer.accept("Không thể tải danh sách danh mục: " + ex.getMessage());
        }
    }

    private void buildTreeNodes() {
        this.rootNode.removeAllChildren();

        DefaultMutableTreeNode expenseBranch = new DefaultMutableTreeNode("Khoản Chi (EXPENSE)");
        DefaultMutableTreeNode incomeBranch = new DefaultMutableTreeNode("Khoản Thu (INCOME)");

        if (this.cachedHierarchy != null) {
            for (CategoryHierarchyResponseDTO group : this.cachedHierarchy) {
                NeedGroupNode groupNodeData = new NeedGroupNode(
                        group.getNeedGroupId(), group.getNeedGroupName(), group.getTransactionType(), group.getNeedGroupStatus());
                DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(groupNodeData);

                if (group.getCategories() != null) {
                    for (CategoryResponseDTO cat : group.getCategories()) {
                        CategoryNode catNodeData = new CategoryNode(
                                cat.getCategoryId(), cat.getName(), cat.getTransactionType(), cat.getStatus());
                        groupNode.add(new DefaultMutableTreeNode(catNodeData));
                    }
                }

                if ("EXPENSE".equalsIgnoreCase(group.getTransactionType())) {
                    expenseBranch.add(groupNode);
                } else {
                    incomeBranch.add(groupNode);
                }
            }
        }

        this.rootNode.add(expenseBranch);
        this.rootNode.add(incomeBranch);
        this.treeModel.reload();

        // Mở rộng tất cả các nhánh
        for (int i = 0; i < this.tree.getRowCount(); i++) {
            this.tree.expandRow(i);
        }
    }

    private void updateSummary() {
        int totalGroups = 0;
        int totalCategories = 0;
        int expenseCategories = 0;
        int incomeCategories = 0;

        if (this.cachedHierarchy != null) {
            totalGroups = this.cachedHierarchy.size();
            for (CategoryHierarchyResponseDTO group : this.cachedHierarchy) {
                int count = group.getCategoryCount();
                totalCategories += count;
                if ("EXPENSE".equalsIgnoreCase(group.getTransactionType())) {
                    expenseCategories += count;
                } else {
                    incomeCategories += count;
                }
            }
        }

        this.summaryLabel.setText(String.format(
                "Tổng số nhóm: %d | Tổng số danh mục: %d (Chi phí: %d | Thu nhập: %d)",
                totalGroups, totalCategories, expenseCategories, incomeCategories));
    }

    private void onTreeNodeSelected() {
        this.tableModel.setRowCount(0);
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();

        if (selectedNode == null || selectedNode.isRoot()) {
            // Nạp toàn bộ
            populateTableWithAll();
            return;
        }

        Object userObject = selectedNode.getUserObject();
        if (userObject instanceof NeedGroupNode gn) {
            // Hiển thị danh mục của nhóm này
            findAndPopulateGroup(gn.id());
        } else if (userObject instanceof CategoryNode cn) {
            // Hiển thị một danh mục đơn lẻ
            this.tableModel.addRow(new Object[]{cn.type(), "-", cn.name(), cn.status()});
        } else if (userObject instanceof String branchName) {
            String targetType = branchName.contains("EXPENSE") ? "EXPENSE" : "INCOME";
            populateTableByType(targetType);
        }
    }

    private void populateTableWithAll() {
        if (this.cachedHierarchy == null) return;
        for (CategoryHierarchyResponseDTO group : this.cachedHierarchy) {
            if (group.getCategories() != null) {
                for (CategoryResponseDTO cat : group.getCategories()) {
                    this.tableModel.addRow(new Object[]{
                            cat.getTransactionType(), group.getNeedGroupName(), cat.getName(), cat.getStatus()});
                }
            }
        }
    }

    private void populateTableByType(String type) {
        if (this.cachedHierarchy == null) return;
        for (CategoryHierarchyResponseDTO group : this.cachedHierarchy) {
            if (type.equalsIgnoreCase(group.getTransactionType()) && group.getCategories() != null) {
                for (CategoryResponseDTO cat : group.getCategories()) {
                    this.tableModel.addRow(new Object[]{
                            cat.getTransactionType(), group.getNeedGroupName(), cat.getName(), cat.getStatus()});
                }
            }
        }
    }

    private void findAndPopulateGroup(UUID needGroupId) {
        if (this.cachedHierarchy == null || needGroupId == null) return;
        for (CategoryHierarchyResponseDTO group : this.cachedHierarchy) {
            if (needGroupId.equals(group.getNeedGroupId()) && group.getCategories() != null) {
                for (CategoryResponseDTO cat : group.getCategories()) {
                    this.tableModel.addRow(new Object[]{
                            cat.getTransactionType(), group.getNeedGroupName(), cat.getName(), cat.getStatus()});
                }
                break;
            }
        }
    }

    private void openCreateNeedGroupDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        CreateNeedGroupDialog dialog = new CreateNeedGroupDialog(parentFrame, this.categoryController);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            this.messageConsumer.accept("Đã tạo nhóm nhu cầu thành công!");
            reloadCategories();
        }
    }

    private void openCreateCategoryDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        CreateCategoryDialog dialog = new CreateCategoryDialog(parentFrame, this.categoryController);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            this.messageConsumer.accept("Đã tạo danh mục con thành công!");
            reloadCategories();
        }
    }

    private void handleRenameSelected() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
        if (selectedNode == null || selectedNode.isRoot()) {
            this.warningConsumer.accept("Vui lòng chọn một nhóm nhu cầu hoặc danh mục con trên cây để đổi tên!");
            return;
        }

        Object obj = selectedNode.getUserObject();
        if (obj instanceof NeedGroupNode gn) {
            String newName = this.inputSupplier.apply("Nhập tên mới cho nhóm nhu cầu (hiện tại: " + gn.name() + "):");
            if (newName != null && !newName.trim().isEmpty()) {
                try {
                    this.categoryController.handleRenameNeedGroup(gn.id(), newName.trim());
                    this.messageConsumer.accept("Đổi tên nhóm nhu cầu thành công!");
                    reloadCategories();
                } catch (Exception ex) {
                    this.errorConsumer.accept("Đổi tên thất bại: " + ex.getMessage());
                }
            }
        } else if (obj instanceof CategoryNode cn) {
            String newName = this.inputSupplier.apply("Nhập tên mới cho danh mục con (hiện tại: " + cn.name() + "):");
            if (newName != null && !newName.trim().isEmpty()) {
                try {
                    this.categoryController.handleRenameCategory(cn.id(), newName.trim());
                    this.messageConsumer.accept("Đổi tên danh mục con thành công!");
                    reloadCategories();
                } catch (Exception ex) {
                    this.errorConsumer.accept("Đổi tên thất bại: " + ex.getMessage());
                }
            }
        } else {
            this.warningConsumer.accept("Không thể đổi tên nhánh cấp cao!");
        }
    }

    private void handleDeleteSelected() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
        if (selectedNode == null || selectedNode.isRoot()) {
            this.warningConsumer.accept("Vui lòng chọn một nhóm nhu cầu hoặc danh mục con để xóa!");
            return;
        }

        Object obj = selectedNode.getUserObject();
        if (obj instanceof NeedGroupNode gn) {
            boolean confirm = this.confirmConsumer.test(
                    "Bạn có chắc chắn muốn xóa nhóm nhu cầu \"" + gn.name() + "\" không?");
            if (confirm) {
                try {
                    this.categoryController.handleDeleteNeedGroup(gn.id());
                    this.messageConsumer.accept("Đã xóa nhóm nhu cầu thành công!");
                    reloadCategories();
                } catch (Exception ex) {
                    this.errorConsumer.accept("Xóa nhóm thất bại: " + ex.getMessage());
                }
            }
        } else if (obj instanceof CategoryNode cn) {
            boolean confirm = this.confirmConsumer.test(
                    "Bạn có chắc chắn muốn xóa danh mục con \"" + cn.name() + "\" không?");
            if (confirm) {
                try {
                    this.categoryController.handleDeleteCategory(cn.id());
                    this.messageConsumer.accept("Đã xóa danh mục con thành công!");
                    reloadCategories();
                } catch (Exception ex) {
                    this.errorConsumer.accept("Xóa danh mục thất bại: " + ex.getMessage());
                }
            }
        } else {
            this.warningConsumer.accept("Không thể xóa nhánh phân loại cấp cao!");
        }
    }

    // Getters and Listeners
    public JTree getTree() {
        return this.tree;
    }

    public JTable getTable() {
        return this.detailTable;
    }

    public String getSummaryText() {
        return this.summaryLabel.getText();
    }

    public void setOnCategoryChangedListener(Runnable listener) {
        this.onCategoryChangedListener = listener;
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

    public void setInputSupplier(java.util.function.Function<String, String> supplier) {
        this.inputSupplier = Objects.requireNonNull(supplier);
    }

    public record NeedGroupNode(UUID id, String name, String type, String status) {
        @Override
        public String toString() {
            return name + " (" + status + ")";
        }
    }

    public record CategoryNode(UUID id, String name, String type, String status) {
        @Override
        public String toString() {
            return name;
        }
    }
}
