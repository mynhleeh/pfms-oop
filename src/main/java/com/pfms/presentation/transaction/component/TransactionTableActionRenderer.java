package com.pfms.presentation.transaction.component;

import com.pfms.presentation.common.ViewConstants;
import com.pfms.presentation.common.component.RoundedBorder;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Trình dựng và biên tập ô (Renderer &amp; Editor) cho cột Thao tác (Inline Actions) trên bảng giao dịch.
 * Cung cấp hai nút bấm bo tròn mềm mại: "Sửa" và "Xóa" trên từng dòng giao dịch.
 */
public class TransactionTableActionRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {

    private final JPanel renderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
    private final JPanel editorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));

    private final JButton renderBtnEdit = createActionButton("Sửa", ViewConstants.COLOR_SIDEBAR_ACTIVE, ViewConstants.COLOR_TEXT_ACTIVE, ViewConstants.COLOR_GOLD_BORDER);
    private final JButton renderBtnDelete = createActionButton("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE, new Color(251, 113, 133, 64));

    private final JButton editorBtnEdit = createActionButton("Sửa", ViewConstants.COLOR_SIDEBAR_ACTIVE, ViewConstants.COLOR_TEXT_ACTIVE, ViewConstants.COLOR_GOLD_BORDER);
    private final JButton editorBtnDelete = createActionButton("Xóa", ViewConstants.COLOR_STATUS_BG, ViewConstants.COLOR_EXPENSE, new Color(251, 113, 133, 64));

    private final Consumer<TransactionResponseDTO> onEditAction;
    private final Consumer<TransactionResponseDTO> onDeleteAction;
    private TransactionResponseDTO currentTxn;

    public TransactionTableActionRenderer(Consumer<TransactionResponseDTO> onEditAction, Consumer<TransactionResponseDTO> onDeleteAction) {
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

    private static JButton createActionButton(String text, Color bg, Color fg, Color borderColor) {
        JButton btn = new JButton(text);
        btn.setFont(ViewConstants.FONT_STATUS);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("Component.arc", 8);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, borderColor, new Insets(1, 4, 1, 4)),
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
        int colWidth = table.getColumnModel().getColumn(column).getWidth();
        this.renderPanel.setSize(colWidth, table.getRowHeight());
        this.renderPanel.doLayout();
        return this.renderPanel;
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof TransactionResponseDTO dto) {
            this.currentTxn = dto;
        } else {
            this.currentTxn = null;
        }
        if (isSelected) {
            this.editorPanel.setBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
        } else {
            this.editorPanel.setBackground(ViewConstants.COLOR_CARD_BG);
        }
        int colWidth = table.getColumnModel().getColumn(column).getWidth();
        this.editorPanel.setSize(colWidth, table.getRowHeight());
        this.editorPanel.doLayout();
        return this.editorPanel;
    }

    @Override
    public Object getCellEditorValue() {
        return this.currentTxn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
        if (this.currentTxn == null) {
            return;
        }
        if (e.getSource() == this.editorBtnEdit) {
            this.onEditAction.accept(this.currentTxn);
        } else if (e.getSource() == this.editorBtnDelete) {
            this.onDeleteAction.accept(this.currentTxn);
        }
    }
}
