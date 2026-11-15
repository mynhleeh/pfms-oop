package com.pfms.presentation.view.category.dialog;

import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.view.common.ViewConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Hộp thoại modal tạo Danh mục phân loại con chi tiết mới (UC-CAT-03).
 */
public class CreateCategoryDialog extends JDialog {

    private final CategoryGUIController categoryController;

    private final JComboBox<NeedGroupComboItem> parentGroupBox;
    private final JTextField nameField;

    private boolean success = false;
    private CategoryResponseDTO createdCategory = null;

    private Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Lỗi tạo danh mục", JOptionPane.ERROR_MESSAGE);
    };

    public CreateCategoryDialog(JFrame parent, CategoryGUIController categoryController) {
        super(parent, "Thêm Danh mục Con Mới", true);
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");

        setSize(420, 260);
        setMinimumSize(new Dimension(380, 220));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        formPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Nhóm nhu cầu cha
        JLabel parentLabel = new JLabel("Nhóm nhu cầu cha (*):");
        parentLabel.setFont(ViewConstants.FONT_BODY);
        parentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.parentGroupBox = new JComboBox<>();
        this.parentGroupBox.setFont(ViewConstants.FONT_BODY);
        this.parentGroupBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.parentGroupBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        setupParentGroupRenderer();
        loadParentGroups();

        // Tên danh mục con
        JLabel nameLabel = new JLabel("Tên danh mục con (*):");
        nameLabel.setFont(ViewConstants.FONT_BODY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.nameField = new JTextField();
        this.nameField.setFont(ViewConstants.FONT_BODY);
        this.nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(parentLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.parentGroupBox);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(this.nameField);

        // Nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(ViewConstants.COLOR_STATUS_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ViewConstants.COLOR_BORDER));

        JButton cancelButton = new JButton("Hủy bỏ");
        cancelButton.setFont(ViewConstants.FONT_BODY);
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Lưu danh mục");
        saveButton.setFont(ViewConstants.FONT_BODY);
        saveButton.setBackground(ViewConstants.COLOR_PRIMARY_LIGHT);
        saveButton.setForeground(ViewConstants.COLOR_STATUS_BG);
        saveButton.addActionListener(e -> triggerSave());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupParentGroupRenderer() {
        this.parentGroupBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof NeedGroupComboItem item) {
                    setText(item.name() + " [" + item.type() + "]");
                }
                return this;
            }
        });
    }

    private void loadParentGroups() {
        try {
            List<CategoryHierarchyResponseDTO> hierarchy = this.categoryController.loadCategoryHierarchy();
            this.parentGroupBox.removeAllItems();
            if (hierarchy != null) {
                for (CategoryHierarchyResponseDTO group : hierarchy) {
                    this.parentGroupBox.addItem(
                            new NeedGroupComboItem(group.getNeedGroupId(), group.getNeedGroupName(), group.getTransactionType()));
                }
            }
        } catch (Exception ex) {
            // bỏ qua hoặc ghi nhận lỗi nạp nhóm
        }
    }

    public void triggerSave() {
        try {
            NeedGroupComboItem selected = (NeedGroupComboItem) this.parentGroupBox.getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Vui lòng chọn nhóm nhu cầu cha");
            }

            String name = getNameInputValue();
            this.createdCategory = this.categoryController.handleCreateCategory(selected.id(), name);
            this.success = true;
            dispose();
        } catch (Exception ex) {
            this.success = false;
            this.createdCategory = null;
            this.errorConsumer.accept(ex.getMessage());
        }
    }

    public boolean isSuccess() {
        return this.success;
    }

    public CategoryResponseDTO getCreatedCategory() {
        return this.createdCategory;
    }

    public UUID getSelectedParentGroupId() {
        NeedGroupComboItem selected = (NeedGroupComboItem) this.parentGroupBox.getSelectedItem();
        return selected != null ? selected.id() : null;
    }

    public void setSelectedParentGroupId(UUID groupId) {
        if (groupId == null) {
            return;
        }
        for (int i = 0; i < this.parentGroupBox.getItemCount(); i++) {
            NeedGroupComboItem item = this.parentGroupBox.getItemAt(i);
            if (item != null && groupId.equals(item.id())) {
                this.parentGroupBox.setSelectedIndex(i);
                return;
            }
        }
    }

    public String getNameInputValue() {
        return this.nameField.getText().trim();
    }

    public void setNameInputValue(String name) {
        this.nameField.setText(name != null ? name : "");
    }

    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = Objects.requireNonNull(errorConsumer);
    }

    public record NeedGroupComboItem(UUID id, String name, String type) {
        @Override
        public String toString() {
            return name + " [" + type + "]";
        }
    }
}
