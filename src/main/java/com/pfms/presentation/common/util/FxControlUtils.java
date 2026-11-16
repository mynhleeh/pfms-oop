package com.pfms.presentation.common.util;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.function.Function;

/**
 * Lớp tiện ích cung cấp các hàm hỗ trợ cấu hình điều khiển JavaFX (Controls Utilities).
 * Giúp tinh giản boilerplate lặp lại mà vẫn giữ nguyên 100% cơ chế hiển thị và hành vi.
 */
public final class FxControlUtils {

    private FxControlUtils() {
        // Utility class private constructor
    }

    /**
     * Cấu hình hiển thị chuẩn cho ComboBox bằng cách trích xuất chuỗi nhãn và gán placeholder khi rỗng.
     *
     * @param comboBox ComboBox cần cấu hình
     * @param textExtractor Hàm trích xuất chuỗi đại diện từ đối tượng phần tử
     * @param nullPlaceholder Nhãn hiển thị gợi ý khi chưa chọn hoặc giá trị null
     * @param <T> Kiểu dữ liệu phần tử trong danh mục ComboBox
     */
    public static <T> void configureComboBox(
            ComboBox<T> comboBox,
            Function<T, String> textExtractor,
            String nullPlaceholder) {

        Objects.requireNonNull(comboBox, "comboBox không được null");
        Objects.requireNonNull(textExtractor, "textExtractor không được null");

        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T item) {
                if (item == null) {
                    return nullPlaceholder;
                }
                return textExtractor.apply(item);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });

        comboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(textExtractor.apply(item));
                }
            }
        });
    }

    private static final java.util.regex.Pattern WALLET_PREFIX_PATTERN =
            java.util.regex.Pattern.compile("^ví\\s+", java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.UNICODE_CASE);

    /**
     * Chuẩn hóa tên ví hiển thị trên các thẻ dòng dàn ngang (loại bỏ tiền tố không cần thiết).
     *
     * @param rawWalletName Tên ví nguyên bản từ cơ sở dữ liệu
     * @return Tên ví tinh gọn đã lược bỏ tiền tố "Ví " hoặc "—" nếu rỗng
     */
    public static String cleanWalletName(String rawWalletName) {
        if (rawWalletName == null || rawWalletName.isBlank()) {
            return "—";
        }
        return WALLET_PREFIX_PATTERN.matcher(rawWalletName.trim()).replaceFirst("");
    }

    /**
     * Ràng buộc nhãn gợi ý số tiền bằng chữ hoặc tiền tệ định dạng tự động theo ô nhập liệu.
     *
     * @param amountField Ô nhập số tiền
     * @param hintLabel Nhãn hiển thị gợi ý
     * @param formatter Định dạng số tiền
     * @param currencySuffix Hậu tố đơn vị tiền tệ (ví dụ: " VNĐ", " ₫")
     */
    public static void bindAmountHint(
            TextField amountField,
            Label hintLabel,
            DecimalFormat formatter,
            String currencySuffix) {

        Objects.requireNonNull(amountField, "amountField không được null");
        Objects.requireNonNull(hintLabel, "hintLabel không được null");
        Objects.requireNonNull(formatter, "formatter không được null");

        String suffix = (currencySuffix != null) ? currencySuffix : "";
        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                hintLabel.setText("0" + suffix);
                return;
            }
            try {
                BigDecimal value = new BigDecimal(newVal.trim());
                hintLabel.setText(formatter.format(value) + suffix);
            } catch (Exception ex) {
                hintLabel.setText("0" + suffix);
            }
        });
    }
}
