package com.pfms.presentation.common.component;

import com.pfms.presentation.common.MainController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.util.Objects;

/**
 * Nhà máy khởi tạo biểu tượng vector (Vector Icon Factory) thuần JavaFX.
 * Sử dụng SVGPath kết xuất sắc nét theo kích thước và tự động đồng bộ màu sắc với CSS.
 */
public final class NavIconFactory {

    private NavIconFactory() {
        throw new UnsupportedOperationException("Lớp tiện ích không thể khởi tạo trực tiếp");
    }

    public static Node createNavIcon(String cardName, double size) {
        Objects.requireNonNull(cardName, "Tên card không được để null");
        String pathData = switch (cardName) {
            case MainController.CARD_DASHBOARD ->
                    "M3 3h7v8H3z M14 3h7v5h-7z M14 12h7v9h-7z M3 15h7v6H3z";
            case MainController.CARD_WALLETS ->
                    "M2 5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5zm0 5h20 M15 13h4v3h-4z";
            case MainController.CARD_TRANSACTIONS ->
                    "M12 2v20 M17 6c0-3-8-3-8 1s7 4 7 9c0 5-9 5-9 2";
            case MainController.CARD_TRANSFERS ->
                    "M3 7h17 M16 3l4 4-4 4 M21 17H4 M8 13l-4 4 4 4";
            case MainController.CARD_CATEGORIES ->
                    "M10 2L2 10l11 11 8-8V2H10zm5 4a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z";
            case MainController.CARD_REPORTS ->
                    "M2 21h20 M4 13h4v8H4zm6-5h4v13h-4zm6-4h4v17h-4z";
            case MainController.CARD_DATA_MANAGEMENT ->
                    "M12 2l8 3v9c0 5-4 10-8 12-4-2-8-7-8-12V5l8-3zm-3 9l2 3 4-5";
            default ->
                    "M3 3h7v8H3z M14 3h7v5h-7z M14 12h7v9h-7z M3 15h7v6H3z";
        };

        SVGPath svg = new SVGPath();
        svg.setContent(pathData);
        svg.getStyleClass().add("nav-icon");
        svg.setFill(Color.TRANSPARENT);
        svg.setStroke(Color.web("#A1A1AA"));
        svg.setStrokeWidth(1.8);
        svg.setStrokeLineCap(StrokeLineCap.ROUND);
        svg.setStrokeLineJoin(StrokeLineJoin.ROUND);

        StackPane iconPane = new StackPane(svg);
        iconPane.setAlignment(Pos.CENTER);
        iconPane.setPrefSize(size, size);
        iconPane.setMinSize(size, size);
        iconPane.setMaxSize(size, size);

        double scale = size / 24.0;
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        return iconPane;
    }

    public static Node createBrandIcon(double size) {
        SVGPath diamond = new SVGPath();
        diamond.setContent("M12 4.5 L19.5 12 L12 19.5 L4.5 12 Z");
        diamond.setFill(Color.web("#D4AF37", 0.15));
        diamond.setStroke(Color.web("#E5C07B"));
        diamond.setStrokeWidth(1.8);
        diamond.setStrokeLineCap(StrokeLineCap.ROUND);
        diamond.setStrokeLineJoin(StrokeLineJoin.ROUND);

        StackPane brandPane = new StackPane(diamond);
        brandPane.setAlignment(Pos.CENTER);
        brandPane.setPrefSize(size, size);
        brandPane.setMinSize(size, size);
        brandPane.setMaxSize(size, size);
        brandPane.setStyle("-fx-background-color: rgba(212, 175, 55, 0.1); " +
                "-fx-background-radius: 6px; -fx-border-color: rgba(212, 175, 55, 0.3); " +
                "-fx-border-radius: 6px; -fx-border-width: 1px;");

        double scale = Math.max(0.5, (size - 6) / 24.0);
        diamond.setScaleX(scale);
        diamond.setScaleY(scale);

        return brandPane;
    }
}
