package com.pfms.presentation.category.dialog;

import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.common.ViewConstants;

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
 * Hộp thoại modal chỉnh sửa thông tin danh mục con: đổi tên và chuyển nhóm nhu cầu cha (UC-CAT-02).
 */
public class UpdateCategoryDialog extends JDialog {

    private final CategoryGUIController categoryController;
    private final UUID categoryId;
    private final UUID initialParentGroupId;
    private final String transactionType;

    private final JComboBox<NeedGroupComboItem> parentGroupBox;
    private final JTextField nameField;

    private boolean success = false;
    private CategoryResponseDTO updatedCategory = null;

    private Consumer<String> errorConsumer = msg -> {
        JOptionPane.showMessageDialog(this, msg, "Lỗi cập nhật danh mục", JOptionPane.ERROR_MESSAGE);
    };

    public UpdateCategoryDialog(
            JFrame parent,
            CategoryGUIController categoryController,
            UUID categoryId,
            String currentName,
            UUID currentParentGroupId,
            String transactionType) {

        super(parent, "Chỉnh Sửa Danh Mục Con (UC-CAT-02)", true);
        this.categoryController = Objects.requireNonNull(categoryController, "CategoryGUIController không được để null");
        this.categoryId = Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        this.initialParentGroupId = currentParentGroupId;
        this.transactionType = transactionType;

        setSize(440, 300);
        setMinimumSize(new Dimension(380, 260));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        formPanel.setBackground(ViewConstants.COLOR_CONTENT_BG);

        // Thông tin loại giao dịch
        JLabel typeInfoLabel = new JLabel("Loại luồng tiền: " + (transactionType != null ? transactionType : "Tất cả"));
        typeInfoLabel.setFont(ViewConstants.FONT_NAV_ACTIVE);
        typeInfoLabel.setForeground(ViewConstants.COLOR_PRIMARY);
        typeInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nhóm nhu cầu cha
        JLabel parentLabel = new JLabel("Nhóm nhu cầu cha trực thuộc (*):");
        parentLabel.setFont(ViewConstants.FONT_BODY);
        parentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.parentGroupBox = new JComboBox<>();
        this.parentGroupBox.setFont(ViewConstants.FONT_BODY);
        this.parentGroupBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.parentGroupBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        setupParentGroupRenderer();
        loadCompatibleParentGroups();

        // Tên danh mục con
        JLabel nameLabel = new JLabel("Tên danh mục con (*):");
        nameLabel.setFont(ViewConstants.FONT_BODY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.nameField = new JTextField(currentName != null ? currentName : "");
        this.nameField.setFont(ViewConstants.FONT_BODY);
        this.nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        this.nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(typeInfoLabel);
        formPanel.add(Box.createVerticalStrut(10));
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

        JButton saveButton = new JButton("Lưu thay đổi");
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

    private void loadCompatibleParentGroups() {
        try {
            List<CategoryHierarchyResponseDTO> hierarchy = this.categoryController.loadCategoryHierarchy();
            this.parentGroupBox.removeAllItems();
            if (hierarchy != null) {
                for (CategoryHierarchyResponseDTO group : hierarchy) {
                    if (this.transactionType == null
                            || this.transactionType.equalsIgnoreCase(group.getTransactionType())) {
                        NeedGroupComboItem item = new NeedGroupComboItem(
                                group.getNeedGroupId(), group.getNeedGroupName(), group.getTransactionType());
                        this.parentGroupBox.addItem(item);
                        if (this.initialParentGroupId != null && this.initialParentGroupId.equals(group.getNeedGroupId())) {
                            this.parentGroupBox.setSelectedItem(item);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void triggerSave() {
        try {
            NeedGroupComboItem selected = (NeedGroupComboItem) this.parentGroupBox.getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Vui lòng chọn nhóm nhu cầu cha");
            }

            String name = getNameInputValue();
            UUID newParentGroupId = selected.id();
            this.updatedCategory = this.categoryController.handleRenameCategory(this.categoryId, name, newParentGroupId);
            this.success = true;
            dispose();
        } catch (Exception ex) {
            this.success = false;
            this.updatedCategory = null;
            this.errorConsumer.accept(ex.getMessage());
        }
    }

    public boolean isSuccess() {
        return this.success;
    }

    public CategoryResponseDTO getUpdatedCategory() {
        return this.updatedCategory;
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
