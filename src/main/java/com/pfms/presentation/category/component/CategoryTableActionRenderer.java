package com.pfms.presentation.category.component;

import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.common.component.RoundedBorder;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Trình dựng và biên tập ô (Renderer &amp; Editor) cho cột Thao tác (Inline Actions) trên bảng danh mục.
 * Cung cấp hai nút bấm bo tròn mềm mại: "Sửa" và "Xóa" trên từng dòng.
 */
public class CategoryTableActionRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {

    private final JPanel renderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
    private final JPanel editorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));

    private final JButton renderBtnEdit = createActionButton("Sửa", ViewConstants.COLOR_SIDEBAR_ACTIVE, ViewConstants.COLOR_TEXT_ACTIVE, ViewConstants.COLOR_GOLD_BORDER);
    private final JButton renderBtnDelete = createActionButton("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE, new java.awt.Color(251, 113, 133, 64));

    private final JButton editorBtnEdit = createActionButton("Sửa", ViewConstants.COLOR_SIDEBAR_ACTIVE, ViewConstants.COLOR_TEXT_ACTIVE, ViewConstants.COLOR_GOLD_BORDER);
    private final JButton editorBtnDelete = createActionButton("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE, new java.awt.Color(251, 113, 133, 64));

    private final Consumer<Object> onEditAction;
    private final Consumer<Object> onDeleteAction;
    private Object currentRowData;

    public CategoryTableActionRenderer(Consumer<Object> onEditAction, Consumer<Object> onDeleteAction) {
        this.onEditAction = Objects.requireNonNull(onEditAction, "onEditAction không được để null");
        this.onDeleteAction = Objects.requireNonNull(onDeleteAction, "onDeleteAction không được để null");

        this.renderPanel.setOpaque(true);
        this.renderPanel.setBackground(ViewConstants.COLOR_CARD_BG);
        this.renderPanel.add(this.renderBtnEdit);
        this.renderPanel.add(this.renderBtnDelete);

        this.editorPanel.setOpaque(true);
        this.editorPanel.setBackground(ViewConstants.COLOR_CARD_BG);
        this.editorPanel.add(this.editorBtnEdit);
        this.editorPanel.add(this.editorBtnDelete);

        this.editorBtnEdit.addActionListener(this);
        this.editorBtnDelete.addActionListener(this);
    }

    private static JButton createActionButton(String text, java.awt.Color bg, java.awt.Color fg, java.awt.Color borderColor) {
        JButton btn = new JButton(text);
        btn.setFont(ViewConstants.FONT_STATUS);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.putClientProperty("Component.arc", 10);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, borderColor, new Insets(1, 4, 1, 4)),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        return btn;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            this.renderPanel.setBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
        } else {
            this.renderPanel.setBackground(ViewConstants.COLOR_CARD_BG);
        }
        return this.renderPanel;
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        this.currentRowData = value;
        if (isSelected) {
            this.editorPanel.setBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
        } else {
            this.editorPanel.setBackground(ViewConstants.COLOR_CARD_BG);
        }
        return this.editorPanel;
    }

    @Override
    public Object getCellEditorValue() {
        return this.currentRowData;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        Object data = this.currentRowData;
        fireEditingStopped();
        if (source == this.editorBtnEdit) {
            this.onEditAction.accept(data);
        } else if (source == this.editorBtnDelete) {
            this.onDeleteAction.accept(data);
        }
    }
}
