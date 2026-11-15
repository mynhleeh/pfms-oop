package com.pfms.presentation.view.category;

import com.pfms.presentation.contract.CategoryView;
import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.presenter.CategoryPresenter;
import com.pfms.presentation.view.category.component.CategoryTreeCellRenderer;
import com.pfms.presentation.view.category.dialog.CreateCategoryDialog;
import com.pfms.presentation.view.category.dialog.CreateNeedGroupDialog;
import com.pfms.presentation.view.common.ViewConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Bảng điều khiển phân hệ Quản lý Danh mục Thu Chi (Passive View).
 * Trình diễn cấu trúc phân cấp đa tầng dạng cây (JTree) và bảng chi tiết danh mục con.
 */
public class CategoryPanel extends JPanel implements CategoryView {

    // region Fields
    private final CategoryPresenter presenter;
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Hệ thống Danh mục PFMS");
    private final DefaultTreeModel treeModel = new DefaultTreeModel(this.rootNode);
    private final JTree tree = new JTree(this.treeModel);
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Loại", "Nhóm Nhu cầu", "Tên Danh mục", "Trạng thái"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable detailTable = new JTable(this.tableModel);
    private final JLabel summaryLabel = new JLabel("Tổng số nhóm: 0 | Tổng số danh mục: 0");
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
        initViews(); reloadCategories();
    }
    // endregion

    // region UI
    private void initViews() {
        setLayout(new BorderLayout(10, 10)); setBackground(ViewConstants.COLOR_CONTENT_BG);
        this.tree.setFont(ViewConstants.FONT_BODY); this.tree.setCellRenderer(new CategoryTreeCellRenderer());
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.tree.addTreeSelectionListener(e -> onTreeNodeSelected()); setupDetailTable();
        this.summaryLabel.setFont(ViewConstants.FONT_NAV_ACTIVE); this.summaryLabel.setForeground(ViewConstants.COLOR_TEXT_MAIN);

        JPanel north = new JPanel(); north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(ViewConstants.COLOR_CONTENT_BG); north.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        JLabel title = new JLabel("Quản lý Danh mục Thu Chi"); title.setFont(ViewConstants.FONT_PANEL_HEADER); title.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); bar.setBackground(ViewConstants.COLOR_CONTENT_BG);
        for (JButton b : new JButton[]{
                toolBtn("+ Thêm Nhóm cha", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE, e -> this.presenter.onAddNeedGroupClicked()),
                toolBtn("+ Thêm Danh mục con", ViewConstants.COLOR_PRIMARY_LIGHT, Color.WHITE, e -> this.presenter.onAddCategoryClicked()),
                toolBtn("Đổi tên", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN, e -> handleRenameSelected()),
                toolBtn("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE, e -> handleDeleteSelected()),
                toolBtn("Làm mới", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_TEXT_MAIN, e -> reloadCategories())
        }) bar.add(b);
        north.add(title); north.add(Box.createVerticalStrut(12)); north.add(bar); add(north, BorderLayout.NORTH);

        JScrollPane treeSp = new JScrollPane(this.tree), tblSp = new JScrollPane(this.detailTable);
        treeSp.getViewport().setBackground(Color.WHITE); tblSp.getViewport().setBackground(Color.WHITE);
        treeSp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 24, 12, 6), BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)));
        tblSp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 6, 12, 24), BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER)));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeSp, tblSp);
        split.setDividerLocation(360); split.setResizeWeight(0.35); split.setBorder(null); split.setBackground(ViewConstants.COLOR_CONTENT_BG);
        add(split, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10)); south.setBackground(ViewConstants.COLOR_STATUS_BG);
        south.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER), BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        south.add(this.summaryLabel); add(south, BorderLayout.SOUTH);
    }

    private void setupDetailTable() {
        this.detailTable.setFont(ViewConstants.FONT_BODY); this.detailTable.setRowHeight(28);
        this.detailTable.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
        this.detailTable.getTableHeader().setBackground(new Color(245, 245, 245)); this.detailTable.setGridColor(ViewConstants.COLOR_BORDER);
        DefaultTableCellRenderer c = new DefaultTableCellRenderer(); c.setHorizontalAlignment(SwingConstants.CENTER);
        int[] w = {100, 180, 200, 110};
        for (int i = 0; i < Math.min(4, this.detailTable.getColumnModel().getColumnCount()); i++) {
            this.detailTable.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
            if (i == 0 || i == 3) this.detailTable.getColumnModel().getColumn(i).setCellRenderer(c);
        }
    }

    private JButton toolBtn(String text, Color bg, Color fg, ActionListener l) {
        JButton b = new JButton(text); b.setFont(ViewConstants.FONT_BODY); b.setBackground(bg); b.setForeground(fg); b.setFocusPainted(false); b.addActionListener(l); return b;
    }
    // endregion

    // region Actions
    private void handleRenameSelected() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
        if (node == null || node.isRoot()) { showWarning("Vui lòng chọn một nhóm nhu cầu hoặc danh mục con trên cây để đổi tên!"); return; }
        Object obj = node.getUserObject();
        if (obj instanceof NeedGroupNode gn) this.presenter.renameNeedGroup(gn.id(), gn.name());
        else if (obj instanceof CategoryNode cn) this.presenter.renameCategory(cn.id(), cn.name());
        else showWarning("Không thể đổi tên nhánh cấp cao!");
    }

    private void handleDeleteSelected() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
        if (node == null || node.isRoot()) { showWarning("Vui lòng chọn một nhóm nhu cầu hoặc danh mục con để xóa!"); return; }
        Object obj = node.getUserObject();
        if (obj instanceof NeedGroupNode gn) this.presenter.deleteNeedGroup(gn.id(), gn.name());
        else if (obj instanceof CategoryNode cn) this.presenter.deleteCategory(cn.id(), cn.name());
        else showWarning("Không thể xóa nhánh phân loại cấp cao!");
    }

    @Override public void openCreateNeedGroupDialog() {
        CreateNeedGroupDialog d = new CreateNeedGroupDialog((JFrame) SwingUtilities.getWindowAncestor(this), this.presenter.getController());
        d.setVisible(true); if (d.isSuccess()) { showMessage("Đã tạo nhóm nhu cầu thành công!"); reloadCategories(); }
    }

    @Override public void openCreateCategoryDialog() {
        CreateCategoryDialog d = new CreateCategoryDialog((JFrame) SwingUtilities.getWindowAncestor(this), this.presenter.getController());
        d.setVisible(true); if (d.isSuccess()) { showMessage("Đã tạo danh mục con thành công!"); reloadCategories(); }
    }

    @Override public String openRenameDialog(String currentName, boolean isGroup) {
        String prompt = "Nhập tên mới cho " + (isGroup ? "nhóm nhu cầu" : "danh mục con") + " (hiện tại: " + currentName + "):";
        return (this.inputSupplier != null) ? this.inputSupplier.apply(prompt) : JOptionPane.showInputDialog(this, prompt);
    }
    // endregion

    // region Data
    @Override public void renderCategoryHierarchy(List<CategoryHierarchyResponseDTO> hierarchy, int totalGroups, int totalCats) {
        this.cachedHierarchy = (hierarchy != null) ? hierarchy : Collections.emptyList();
        buildTreeNodes(); updateSummary(totalGroups, totalCats); onTreeNodeSelected();
        if (this.onCategoryChangedListener != null) this.onCategoryChangedListener.run();
    }

    public void reloadCategories() { this.presenter.loadData(); }

    private void buildTreeNodes() {
        this.rootNode.removeAllChildren();
        DefaultMutableTreeNode expBranch = new DefaultMutableTreeNode("Khoản Chi (EXPENSE)"), incBranch = new DefaultMutableTreeNode("Khoản Thu (INCOME)");
        for (CategoryHierarchyResponseDTO g : this.cachedHierarchy) {
            DefaultMutableTreeNode gNode = new DefaultMutableTreeNode(new NeedGroupNode(g.getNeedGroupId(), g.getNeedGroupName(), g.getTransactionType(), g.getNeedGroupStatus()));
            if (g.getCategories() != null) for (CategoryResponseDTO c : g.getCategories()) gNode.add(new DefaultMutableTreeNode(new CategoryNode(c.getCategoryId(), c.getName(), c.getTransactionType(), c.getStatus())));
            if ("EXPENSE".equalsIgnoreCase(g.getTransactionType())) expBranch.add(gNode); else incBranch.add(gNode);
        }
        this.rootNode.add(expBranch); this.rootNode.add(incBranch); this.treeModel.reload();
        for (int i = 0; i < this.tree.getRowCount(); i++) this.tree.expandRow(i);
    }

    private void updateSummary(int totalGroups, int totalCategories) {
        int expCats = 0, incCats = 0;
        for (CategoryHierarchyResponseDTO g : this.cachedHierarchy) {
            int count = g.getCategoryCount();
            if ("EXPENSE".equalsIgnoreCase(g.getTransactionType())) expCats += count; else incCats += count;
        }
        this.summaryLabel.setText(String.format("Tổng số nhóm: %d | Tổng số danh mục: %d (Chi phí: %d | Thu nhập: %d)", totalGroups, totalCategories, expCats, incCats));
    }

    private void onTreeNodeSelected() {
        this.tableModel.setRowCount(0);
        DefaultMutableTreeNode sel = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
        if (sel == null || sel.isRoot()) { populateTable(g -> true); return; }
        Object obj = sel.getUserObject();
        if (obj instanceof NeedGroupNode gn) populateTable(g -> gn.id().equals(g.getNeedGroupId()));
        else if (obj instanceof CategoryNode cn) this.tableModel.addRow(new Object[]{cn.type(), "-", cn.name(), cn.status()});
        else if (obj instanceof String b) populateTable(g -> (b.contains("EXPENSE") ? "EXPENSE" : "INCOME").equalsIgnoreCase(g.getTransactionType()));
    }
    // endregion

    // region Getters
    public JTree getTree() { return this.tree; } public JTable getTable() { return this.detailTable; }
    public String getSummaryText() { return this.summaryLabel.getText(); }
    public CategoryPresenter getPresenter() { return this.presenter; } public CategoryGUIController getCategoryController() { return this.presenter.getController(); }
    public void setOnCategoryChangedListener(Runnable l) { this.onCategoryChangedListener = l; }
    public void setMessageConsumer(Consumer<String> c) { this.messageConsumer = Objects.requireNonNull(c); } public void setWarningConsumer(Consumer<String> c) { this.warningConsumer = Objects.requireNonNull(c); }
    public void setErrorConsumer(Consumer<String> c) { this.errorConsumer = Objects.requireNonNull(c); } public void setConfirmConsumer(Predicate<String> c) { this.confirmConsumer = Objects.requireNonNull(c); }
    public void setInputSupplier(Function<String, String> s) { this.inputSupplier = Objects.requireNonNull(s); }
    public record NeedGroupNode(UUID id, String name, String type, String status) { @Override public String toString() { return name + " (" + status + ")"; } }
    public record CategoryNode(UUID id, String name, String type, String status) { @Override public String toString() { return name; } }
    // endregion

    // region Helpers
    @Override public void showMessage(String m) { if (messageConsumer != null) messageConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.INFORMATION_MESSAGE); }
    @Override public void showWarning(String m) { if (warningConsumer != null) warningConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Cảnh báo", JOptionPane.WARNING_MESSAGE); }
    @Override public void showError(String m) { if (errorConsumer != null) errorConsumer.accept(m); else JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE); }
    @Override public boolean confirmAction(String m) { return (confirmConsumer != null) ? confirmConsumer.test(m) : JOptionPane.showConfirmDialog(this, m, "Xác nhận thao tác", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION; }

    private void populateTable(Predicate<CategoryHierarchyResponseDTO> filter) {
        for (CategoryHierarchyResponseDTO g : this.cachedHierarchy) {
            if (filter.test(g) && g.getCategories() != null) {
                for (CategoryResponseDTO c : g.getCategories()) this.tableModel.addRow(new Object[]{c.getTransactionType(), g.getNeedGroupName(), c.getName(), c.getStatus()});
            }
        }
    }
    // endregion
}
