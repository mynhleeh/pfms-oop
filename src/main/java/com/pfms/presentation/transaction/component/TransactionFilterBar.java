package com.pfms.presentation.transaction.component;

import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Thanh công cụ lọc và tìm kiếm giao dịch thu chi (UC-TRN-03) sử dụng JavaFX.
 * Thiết kế 2 tầng cân đối theo nhận diện Luxury Black & Gold.
 */
public class TransactionFilterBar extends VBox {

    private final Button addButton = new Button("+ Ghi nhận giao dịch mới");
    private final Button resetButton = new Button("Đặt lại bộ lọc");
    private final Button refreshButton = new Button("Làm mới");

    private final Button btnQuickAll = new Button("Tất cả");
    private final Button btnQuickMonth = new Button("Tháng này");
    private final Button btnQuick7Days = new Button("7 ngày qua");
    private String currentQuickTag = "ALL";

    private final TextField keywordField = new TextField();

    private final ComboBox<String> typeFilterBox = new ComboBox<>();
    private final ComboBox<Object> walletFilterBox = new ComboBox<>();
    private final ComboBox<String> groupFilterBox = new ComboBox<>();
    private final ComboBox<Object> categoryFilterBox = new ComboBox<>();
    private final DatePicker startDatePicker = new DatePicker();
    private final DatePicker endDatePicker = new DatePicker();
    private final Button filterButton = new Button("Lọc");

    private final List<CategoryResponseDTO> allCategories = new ArrayList<>();

    private Runnable onAddAction;
    private Runnable onFilterAction;
    private Runnable onResetAction;
    private boolean isUpdating = false;

    public TransactionFilterBar() {
        this(null, null);
    }

    public TransactionFilterBar(Runnable onFilterAction, Runnable onResetAction) {
        this.onFilterAction = onFilterAction;
        this.onResetAction = onResetAction;

        getStyleClass().add("pfms-card");
        setSpacing(10);
        setPadding(new Insets(12, 16, 12, 16));

        initControls();
        buildUI();
    }

    public TransactionFilterBar(Runnable onFilterAction) {
        this(onFilterAction, null);
    }

    private void initControls() {
        addButton.getStyleClass().add("btn-primary");
        addButton.setOnAction(e -> {
            if (onAddAction != null) onAddAction.run();
        });

        resetButton.getStyleClass().add("btn-secondary");
        resetButton.setOnAction(e -> handleReset());

        refreshButton.getStyleClass().add("btn-secondary");
        refreshButton.setOnAction(e -> triggerFilter());

        btnQuickAll.getStyleClass().add("filter-chip");
        btnQuickAll.setOnAction(e -> setQuickDate("ALL"));

        btnQuickMonth.getStyleClass().add("filter-chip");
        btnQuickMonth.setOnAction(e -> setQuickDate("MONTH"));

        btnQuick7Days.getStyleClass().add("filter-chip");
        btnQuick7Days.setOnAction(e -> setQuickDate("7DAYS"));

        keywordField.setPromptText("Tìm kiếm ghi chú...");
        keywordField.setPrefWidth(200);
        keywordField.setOnAction(e -> triggerFilter());

        typeFilterBox.getItems().addAll("Tất cả", "INCOME", "EXPENSE");
        typeFilterBox.setValue("Tất cả");
        typeFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> onTypeFilterChanged());

        walletFilterBox.getItems().add("Tất cả ví");
        walletFilterBox.setValue("Tất cả ví");
        setupWalletComboRenderer();

        groupFilterBox.getItems().add("Tất cả nhóm");
        groupFilterBox.setValue("Tất cả nhóm");
        groupFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> onGroupFilterChanged());

        categoryFilterBox.getItems().add("Tất cả danh mục");
        categoryFilterBox.setValue("Tất cả danh mục");
        setupCategoryComboRenderer();

        startDatePicker.setPromptText("Từ ngày");
        startDatePicker.setPrefWidth(125);
        endDatePicker.setPromptText("Đến ngày");
        endDatePicker.setPrefWidth(125);

        filterButton.getStyleClass().add("btn-primary");
        filterButton.setOnAction(e -> triggerFilter());
    }

    private void buildUI() {
        // --- HÀNG 1: Tác vụ trái & Tìm kiếm + Thẻ ngày nhanh phải ---
        HBox row1 = new HBox(10);
        row1.setAlignment(Pos.CENTER_LEFT);

        HBox actionsPanel = new HBox(8, addButton, resetButton, refreshButton);
        actionsPanel.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox quickPanel = new HBox(6);
        quickPanel.setAlignment(Pos.CENTER_RIGHT);
        Label quickLabel = new Label("Thời gian:");
        quickLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");
        quickPanel.getChildren().addAll(quickLabel, btnQuickAll, btnQuickMonth, btnQuick7Days, keywordField);

        row1.getChildren().addAll(actionsPanel, spacer, quickPanel);

        // Đường phân cách
        Region divider = new Region();
        divider.setStyle("-fx-background-color: -color-border; -fx-pref-height: 1px;");

        // --- HÀNG 2: Bộ lọc chuyên sâu trải đều ---
        HBox row2 = new HBox(8);
        row2.setAlignment(Pos.CENTER_LEFT);

        Label lblType = new Label("Loại:");
        lblType.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Label lblWallet = new Label("Ví:");
        lblWallet.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Label lblGroup = new Label("Nhóm:");
        lblGroup.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Label lblCat = new Label("Danh mục:");
        lblCat.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Label lblDates = new Label("Khoảng ngày:");
        lblDates.setStyle("-fx-text-fill: -color-text-dim; -fx-font-size: 11px;");

        Label arrowLabel = new Label("→");
        arrowLabel.setStyle("-fx-text-fill: -color-text-dim; -fx-font-weight: bold;");

        row2.getChildren().addAll(
                lblType, typeFilterBox,
                lblWallet, walletFilterBox,
                lblGroup, groupFilterBox,
                lblCat, categoryFilterBox,
                lblDates, startDatePicker, arrowLabel, endDatePicker,
                filterButton
        );

        getChildren().addAll(row1, divider, row2);
    }

    private void setupWalletComboRenderer() {
        walletFilterBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Object obj) {
                if (obj instanceof WalletResponseDTO w) {
                    return w.getName();
                }
                return obj != null ? obj.toString() : "Tất cả ví";
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        });
        walletFilterBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item instanceof WalletResponseDTO w) {
                    setText(w.getName());
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    private void setupCategoryComboRenderer() {
        categoryFilterBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Object obj) {
                if (obj instanceof CategoryResponseDTO c) {
                    return c.getName();
                }
                return obj != null ? obj.toString() : "Tất cả danh mục";
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        });
        categoryFilterBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item instanceof CategoryResponseDTO c) {
                    setText(c.getName());
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    private void onTypeFilterChanged() {
        if (isUpdating) return;
        isUpdating = true;
        try {
            updateGroupOptions();
            updateCategoryOptions();
        } finally {
            isUpdating = false;
        }
    }

    private void onGroupFilterChanged() {
        if (isUpdating) return;
        isUpdating = true;
        try {
            updateCategoryOptions();
        } finally {
            isUpdating = false;
        }
    }

    private void updateGroupOptions() {
        String selectedType = typeFilterBox.getValue();
        boolean filterByType = selectedType != null && !"Tất cả".equalsIgnoreCase(selectedType);

        Set<String> groups = new LinkedHashSet<>();
        for (CategoryResponseDTO c : allCategories) {
            if (!filterByType || selectedType.equalsIgnoreCase(c.getTransactionType())) {
                if (c.getParentGroupName() != null && !c.getParentGroupName().isBlank()) {
                    groups.add(c.getParentGroupName());
                }
            }
        }

        groupFilterBox.getItems().clear();
        groupFilterBox.getItems().add("Tất cả nhóm");
        groupFilterBox.getItems().addAll(groups);
        groupFilterBox.setValue("Tất cả nhóm");
    }

    private void updateCategoryOptions() {
        String selectedType = typeFilterBox.getValue();
        boolean filterByType = selectedType != null && !"Tất cả".equalsIgnoreCase(selectedType);

        String selectedGroup = groupFilterBox.getValue();
        boolean filterByGroup = selectedGroup != null && !"Tất cả nhóm".equalsIgnoreCase(selectedGroup);

        categoryFilterBox.getItems().clear();
        categoryFilterBox.getItems().add("Tất cả danh mục");
        for (CategoryResponseDTO c : allCategories) {
            boolean matchesType = !filterByType || selectedType.equalsIgnoreCase(c.getTransactionType());
            boolean matchesGroup = !filterByGroup || (selectedGroup != null && selectedGroup.equalsIgnoreCase(c.getParentGroupName()));
            if (matchesType && matchesGroup) {
                categoryFilterBox.getItems().add(c);
            }
        }
        categoryFilterBox.setValue("Tất cả danh mục");
    }

    public void populateWallets(List<WalletResponseDTO> wallets) {
        isUpdating = true;
        try {
            walletFilterBox.getItems().clear();
            walletFilterBox.getItems().add("Tất cả ví");
            if (wallets != null) {
                walletFilterBox.getItems().addAll(wallets);
            }
            walletFilterBox.setValue("Tất cả ví");
        } finally {
            isUpdating = false;
        }
    }

    public void populateCategories(List<CategoryResponseDTO> categories) {
        isUpdating = true;
        try {
            allCategories.clear();
            if (categories != null) {
                allCategories.addAll(categories);
            }
            updateGroupOptions();
            updateCategoryOptions();
        } finally {
            isUpdating = false;
        }
    }

    public void setQuickDate(String period) {
        this.currentQuickTag = period;
        btnQuickAll.getStyleClass().remove("filter-chip-active");
        btnQuickMonth.getStyleClass().remove("filter-chip-active");
        btnQuick7Days.getStyleClass().remove("filter-chip-active");
        if ("ALL".equals(period)) btnQuickAll.getStyleClass().add("filter-chip-active");
        else if ("MONTH".equals(period)) btnQuickMonth.getStyleClass().add("filter-chip-active");
        else if ("7DAYS".equals(period)) btnQuick7Days.getStyleClass().add("filter-chip-active");

        LocalDate today = LocalDate.now();
        switch (period) {
            case "ALL" -> {
                startDatePicker.setValue(null);
                endDatePicker.setValue(null);
            }
            case "MONTH" -> {
                LocalDate firstDay = today.withDayOfMonth(1);
                startDatePicker.setValue(firstDay);
                endDatePicker.setValue(today);
            }
            case "7DAYS" -> {
                LocalDate past7 = today.minusDays(6);
                startDatePicker.setValue(past7);
                endDatePicker.setValue(today);
            }
            default -> {}
        }
        triggerFilter();
    }

    public void resetFilters() {
        isUpdating = true;
        try {
            currentQuickTag = "ALL";
            typeFilterBox.setValue("Tất cả");
            walletFilterBox.setValue("Tất cả ví");
            updateGroupOptions();
            updateCategoryOptions();
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            keywordField.clear();
        } finally {
            isUpdating = false;
        }
    }

    private void handleReset() {
        resetFilters();
        if (onResetAction != null) {
            onResetAction.run();
        } else {
            triggerFilter();
        }
    }

    private void triggerFilter() {
        if (!isUpdating && onFilterAction != null) {
            onFilterAction.run();
        }
    }

    public FilterTransactionsRequestDTO buildFilterRequest(int page, int pageSize) {
        String typeFilter = typeFilterBox.getValue();
        String type = ("Tất cả".equalsIgnoreCase(typeFilter) || typeFilter == null) ? null : typeFilter;

        Set<UUID> walletIds = null;
        Object selectedWallet = walletFilterBox.getValue();
        if (selectedWallet instanceof WalletResponseDTO w) {
            walletIds = new HashSet<>(Collections.singleton(w.getWalletId()));
        }

        Set<UUID> categoryIds = null;
        Object selectedCategory = categoryFilterBox.getValue();
        if (selectedCategory instanceof CategoryResponseDTO c) {
            categoryIds = new HashSet<>(Collections.singleton(c.getCategoryId()));
        } else {
            String selectedGroup = groupFilterBox.getValue();
            if (selectedGroup != null && !"Tất cả nhóm".equalsIgnoreCase(selectedGroup)) {
                categoryIds = new HashSet<>();
                for (CategoryResponseDTO c : allCategories) {
                    if (selectedGroup.equalsIgnoreCase(c.getParentGroupName())) {
                        if (type == null || type.equalsIgnoreCase(c.getTransactionType())) {
                            categoryIds.add(c.getCategoryId());
                        }
                    }
                }
                if (categoryIds.isEmpty()) {
                    categoryIds = null;
                }
            }
        }

        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        String kw = keywordField.getText() != null ? keywordField.getText().trim() : "";

        return new FilterTransactionsRequestDTO(start, end, walletIds, type, categoryIds, kw, page, pageSize);
    }

    public String getSelectedTypeFilter() {
        return typeFilterBox.getValue();
    }

    public void setSelectedTypeFilter(String type) {
        isUpdating = true;
        try {
            if (type == null) {
                typeFilterBox.setValue("Tất cả");
            } else {
                typeFilterBox.setValue(type);
            }
            updateGroupOptions();
            updateCategoryOptions();
        } finally {
            isUpdating = false;
        }
        triggerFilter();
    }

    // Getters
    public Button getAddButton() { return addButton; }
    public Button getResetButton() { return resetButton; }
    public Button getRefreshButton() { return refreshButton; }
    public Button getFilterButton() { return filterButton; }
    public ComboBox<String> getTypeFilterBox() { return typeFilterBox; }
    public ComboBox<Object> getWalletFilterBox() { return walletFilterBox; }
    public ComboBox<String> getGroupFilterBox() { return groupFilterBox; }
    public ComboBox<Object> getCategoryFilterBox() { return categoryFilterBox; }
    public DatePicker getStartDatePicker() { return startDatePicker; }
    public DatePicker getEndDatePicker() { return endDatePicker; }
    public TextField getKeywordField() { return keywordField; }

    public void setOnAddAction(Runnable onAddAction) { this.onAddAction = onAddAction; }
    public void setOnFilterAction(Runnable onFilterAction) { this.onFilterAction = onFilterAction; }
    public void setOnResetAction(Runnable onResetAction) { this.onResetAction = onResetAction; }
}
