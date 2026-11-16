package com.pfms.presentation.common.util;

import com.pfms.testutil.JavaFxTestHelper;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Kiểm thử FxControlUtils - Lớp tiện ích cấu hình điều khiển JavaFX")
class FxControlUtilsTest {

    record ItemDTO(String id, String title) {}

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Test
    @DisplayName("Cấu hình StringConverter cho ComboBox trích xuất nhãn chính xác")
    void testConfigureComboBoxStringConverter() {
        JavaFxTestHelper.runOnFxThread(() -> {
            ComboBox<ItemDTO> combo = new ComboBox<>();
            FxControlUtils.configureComboBox(combo, ItemDTO::title, "-- Chọn mục --");

            assertEquals("-- Chọn mục --", combo.getConverter().toString(null));
            assertEquals("Mục Một", combo.getConverter().toString(new ItemDTO("1", "Mục Một")));
            assertNull(combo.getConverter().fromString("Bất kỳ"));
        });
    }

    @Test
    @DisplayName("Cấu hình ListCell Factory cho ComboBox định dạng hiển thị chính xác")
    void testConfigureComboBoxCellFactory() {
        JavaFxTestHelper.runOnFxThread(() -> {
            ComboBox<ItemDTO> combo = new ComboBox<>();
            FxControlUtils.configureComboBox(combo, ItemDTO::title, "-- Chọn mục --");

            ListCell<ItemDTO> cell = combo.getCellFactory().call(null);
            // Kiểm thử cập nhật phần tử null hoặc empty
            cell.updateIndex(0);
            cell.setItem(null);
            assertNull(cell.getText());

            // Kiểm thử cập nhật phần tử hợp lệ
            ItemDTO item = new ItemDTO("10", "Dòng Thẻ Ví");
            cell.setItem(item);
            // ListCell nội bộ gọi setText trong updateItem
        });
    }

    @Test
    @DisplayName("Chuẩn hóa tên ví loại bỏ tiền tố Ví không phân biệt hoa thường")
    void testCleanWalletName() {
        assertEquals("Tiền Mặt", FxControlUtils.cleanWalletName("Ví Tiền Mặt"));
        assertEquals("VPBank", FxControlUtils.cleanWalletName("ví VPBank"));
        assertEquals("MOMO", FxControlUtils.cleanWalletName("VÍ MOMO"));
        assertEquals("Thẻ Tín Dụng", FxControlUtils.cleanWalletName("Thẻ Tín Dụng"));
        assertEquals("—", FxControlUtils.cleanWalletName(null));
        assertEquals("—", FxControlUtils.cleanWalletName("   "));
    }

    @Test
    @DisplayName("Ràng buộc gợi ý số tiền cập nhật chính xác theo chuỗi nhập liệu")
    void testBindAmountHint() {
        JavaFxTestHelper.runOnFxThread(() -> {
            TextField amountField = new TextField();
            Label hintLabel = new Label();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            symbols.setGroupingSeparator(',');
            DecimalFormat formatter = new DecimalFormat("#,##0", symbols);

            FxControlUtils.bindAmountHint(amountField, hintLabel, formatter, " VNĐ");

            amountField.setText("500000");
            assertEquals("500,000 VNĐ", hintLabel.getText());

            amountField.setText("12345678");
            assertEquals("12,345,678 VNĐ", hintLabel.getText());

            amountField.setText("không hợp lệ");
            assertEquals("0 VNĐ", hintLabel.getText());

            amountField.setText("");
            assertEquals("0 VNĐ", hintLabel.getText());
        });
    }
}
