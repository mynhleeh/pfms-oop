package com.pfms.presentation.view.category.dialog;

import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;
import com.pfms.presentation.view.common.ViewConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Hộp thoại modal tạo Nhóm Nhu cầu mới (UC-CAT-02).
 */
public class CreateNeedGroupDialog extends JDialog {

    private final CategoryGUIController categoryController;

    private final JComboBox<String> typeBox;
    private final JTextField nameField;
    private final JTextField iconField;
    private final JTextField colorField;

    private boolean success = false;
    private NeedGroupResponseDTO createdNeedGroup = null;

    private Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Lỗi tạo nhóm nhu cầu", JOptionPane.ERROR_MESSAGE);
    };

    public CreateNeedGroupDialog(JFrame parent, CategoryGUIController categoryController) {
        super(parent, "Thêm Nhóm Nhu cầu Mới", true);
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");

        setSize(420, 360);
        setMinimumSize(new Dimension(380, 320));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        formPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Loại giao dịch
        JLabel typeLabel = new JLabel("Loại giao dịch (*):");
        typeLabel.setFont(ViewConstants.FONT_BODY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.typeBox = new JComboBox<>(new String[]{"EXPENSE", "INCOME"});
        this.typeBox.setFont(ViewConstants.FONT_BODY);
        this.typeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.typeBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tên nhóm nhu cầu
        JLabel nameLabel = new JLabel("Tên nhóm nhu cầu (*):");
        nameLabel.setFont(ViewConstants.FONT_BODY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.nameField = new JTextField();
        this.nameField.setFont(ViewConstants.FONT_BODY);
        this.nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Biểu tượng gợi ý
        JLabel iconLabel = new JLabel("Biểu tượng (Icon):");
        iconLabel.setFont(ViewConstants.FONT_BODY);
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.iconField = new JTextField();
        this.iconField.setFont(ViewConstants.FONT_BODY);
        this.iconField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.iconField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Mã màu sắc
        JLabel colorLabel = new JLabel("Mã màu sắc (Hex):");
        colorLabel.setFont(ViewConstants.FONT_BODY);
        colorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.colorField = new JTextField();
        this.colorField.setFont(ViewConstants.FONT_BODY);
        this.colorField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.colorField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(typeLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.typeBox);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.nameField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(iconLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.iconField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(colorLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.colorField);

        // Nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        JButton cancelButton = new JButton("Hủy bỏ");
        cancelButton.setFont(ViewConstants.FONT_BODY);
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Lưu nhóm");
        saveButton.setFont(ViewConstants.FONT_BODY);
        saveButton.setBackground(ViewConstants.COLOR_PRIMARY_LIGHT);
        saveButton.setForeground(ViewConstants.COLOR_STATUS_BG);
        saveButton.addActionListener(e -> triggerSave());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void triggerSave() {
        try {
            String type = getTypeInputValue();
            String name = getNameInputValue();
            String icon = getIconInputValue();
            String color = getColorInputValue();

            this.createdNeedGroup = this.categoryController.handleCreateNeedGroup(type, name, icon, color);
            this.success = true;
            dispose();
        } catch (Exception ex) {
            this.success = false;
            this.createdNeedGroup = null;
            this.errorConsumer.accept(ex.getMessage());
        }
    }

    public boolean isSuccess() {
        return this.success;
    }

    public NeedGroupResponseDTO getCreatedNeedGroup() {
        return this.createdNeedGroup;
    }

    public String getTypeInputValue() {
        Object item = this.typeBox.getSelectedItem();
        return item != null ? item.toString() : "EXPENSE";
    }

    public void setTypeInputValue(String type) {
        this.typeBox.setSelectedItem(type);
    }

    public String getNameInputValue() {
        return this.nameField.getText().trim();
    }

    public void setNameInputValue(String name) {
        this.nameField.setText(name != null ? name : "");
    }

    public String getIconInputValue() {
        return this.iconField.getText().trim();
    }

    public void setIconInputValue(String icon) {
        this.iconField.setText(icon != null ? icon : "");
    }

    public String getColorInputValue() {
        return this.colorField.getText().trim();
    }

    public void setColorInputValue(String color) {
        this.colorField.setText(color != null ? color : "");
    }

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = Objects.requireNonNull(errorConsumer);
    }
}
