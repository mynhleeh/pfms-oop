package com.pfms.presentation.common.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.Objects;

/**
 * Component hiển thị chỉ số tài chính dạng viên con nhộng (Capsule Pill).
 * Đồng bộ với chuẩn thiết kế Luxury Black &amp; Gold từ các tệp preview.
 */
public class KpiPill extends HBox {

    public enum Variant {
        GOLD("kpi-pill-gold"),
        INCOME("kpi-pill-income"),
        EXPENSE("kpi-pill-expense"),
        WARNING("kpi-pill-warning"),
        NEUTRAL("kpi-pill-neutral");

        private final String cssClass;

        Variant(String cssClass) {
            this.cssClass = cssClass;
        }

        public String getCssClass() {
            return cssClass;
        }
    }

    private final Label titleLabel = new Label();
    private final Label valueLabel = new Label();
    private Variant variant;

    public KpiPill(String title, String initialValue) {
        this(title, initialValue, Variant.NEUTRAL);
    }

    public KpiPill(String title, String initialValue, Variant variant) {
        Objects.requireNonNull(title, "Tiêu đề không được để null");
        Objects.requireNonNull(initialValue, "Giá trị khởi tạo không được để null");
        Objects.requireNonNull(variant, "Variant không được để null");

        this.titleLabel.setText(title);
        this.titleLabel.getStyleClass().add("kpi-pill-title");

        this.valueLabel.setText(initialValue);
        this.valueLabel.getStyleClass().add("kpi-pill-value");

        getStyleClass().add("kpi-pill");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(6);

        getChildren().addAll(this.titleLabel, this.valueLabel);
        setVariant(variant);
    }

    public String getTitle() {
        return titleLabel.getText();
    }

    public void setTitle(String title) {
        Objects.requireNonNull(title, "Tiêu đề không được để null");
        this.titleLabel.setText(title);
    }

    public String getValue() {
        return valueLabel.getText();
    }

    public void setValue(String value) {
        Objects.requireNonNull(value, "Giá trị không được để null");
        this.valueLabel.setText(value);
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        Objects.requireNonNull(variant, "Variant không được để null");
        if (this.variant != null) {
            getStyleClass().remove(this.variant.getCssClass());
        }
        this.variant = variant;
        if (!getStyleClass().contains(variant.getCssClass())) {
            getStyleClass().add(variant.getCssClass());
        }
    }
}
