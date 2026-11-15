package com.pfms.presentation.category.component;

import com.pfms.presentation.category.CategoryPanel.CategoryNode;
import com.pfms.presentation.category.CategoryPanel.NeedGroupNode;
import com.pfms.presentation.common.ViewConstants;

import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;

/**
 * Trình dựng ô tùy chỉnh cho cây phân cấp danh mục thu chi (JTree).
 * Định dạng biểu tượng và màu sắc theo loại giao dịch (INCOME/EXPENSE) và trạng thái.
 */
public class CategoryTreeCellRenderer extends DefaultTreeCellRenderer {

    // region Constructors
    public CategoryTreeCellRenderer() {
        setFont(ViewConstants.FONT_BODY);
        setBorder(new EmptyBorder(3, 4, 3, 4));
        setBackgroundSelectionColor(ViewConstants.COLOR_SIDEBAR_ACTIVE);
        setTextSelectionColor(ViewConstants.COLOR_TEXT_ACTIVE);
    }
    // endregion

    // region UI
    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof DefaultMutableTreeNode node) {
            Object userObj = node.getUserObject();
            if (userObj instanceof NeedGroupNode gn) {
                renderNeedGroup(gn, sel);
            } else if (userObj instanceof CategoryNode cn) {
                renderCategory(cn, sel);
            } else if (userObj instanceof String label) {
                renderBranch(label, sel);
            }
        }
        return this;
    }

    private void renderNeedGroup(NeedGroupNode gn, boolean selected) {
        setText(gn.toString());
        if (!selected) {
            boolean isExpense = "EXPENSE".equalsIgnoreCase(gn.type());
            setForeground(isExpense ? ViewConstants.COLOR_EXPENSE : ViewConstants.COLOR_INCOME);
        }
    }

    private void renderCategory(CategoryNode cn, boolean selected) {
        setText(cn.toString());
        if (!selected) {
            setForeground(ViewConstants.COLOR_TEXT_MAIN);
        }
    }

    private void renderBranch(String label, boolean selected) {
        setText(label);
        setFont(ViewConstants.FONT_NAV_ACTIVE);
        if (!selected) {
            if (label.contains("EXPENSE")) {
                setForeground(ViewConstants.COLOR_EXPENSE);
            } else if (label.contains("INCOME")) {
                setForeground(ViewConstants.COLOR_INCOME);
            } else {
                setForeground(ViewConstants.COLOR_TEXT_MAIN);
            }
        }
    }
    // endregion
}
