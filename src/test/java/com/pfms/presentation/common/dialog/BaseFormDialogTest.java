package com.pfms.presentation.common.dialog;

import com.pfms.testutil.JavaFxTestHelper;
import javafx.scene.control.Label;
import javafx.stage.Window;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử BaseFormDialog - Lớp cơ sở cho các hộp thoại nhập liệu modal")
class BaseFormDialogTest {

    record DummyResult(String value) {}

    private static class ConcreteTestDialog extends BaseFormDialog<DummyResult> {
        private boolean shouldThrow = false;
        private String returnValue = "Thành công";

        public ConcreteTestDialog(Window owner) {
            super(owner, "Tiêu Đề Thử Nghiệm", 450);
            var header = createHeaderBox("Tiêu Đề", "Phụ đề");
            var form = createFormBox(10, header, new Label("Nội dung"));
            getDialogPane().setContent(form);
            setupButtons("Lưu", "Hủy bỏ");
        }

        public void setShouldThrow(boolean shouldThrow) {
            this.shouldThrow = shouldThrow;
        }

        public void setReturnValue(String value) {
            this.returnValue = value;
        }

        @Override
        protected DummyResult onExecuteSave() throws Exception {
            if (shouldThrow) {
                throw new IllegalArgumentException("Dữ liệu không hợp lệ");
            }
            return new DummyResult(returnValue);
        }
    }

    private ConcreteTestDialog dialog;

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @BeforeEach
    void setUp() {
        JavaFxTestHelper.runOnFxThread(() -> {
            dialog = new ConcreteTestDialog(null);
            dialog.setErrorConsumer(msg -> {});
        });
    }

    @Test
    @DisplayName("Khởi tạo hộp thoại có trạng thái ban đầu rỗng và cờ thành công là false")
    void testInitialState() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertFalse(dialog.isSuccess());
            assertNull(dialog.getResultData());
            assertNull(dialog.getErrorMessage());
            assertEquals("Tiêu Đề Thử Nghiệm", dialog.getTitle());
            assertEquals(450.0, dialog.getDialogPane().getPrefWidth());
        });
    }

    @Test
    @DisplayName("Thực thi triggerSave thành công khi onExecuteSave trả về DTO hợp lệ")
    void testTriggerSaveSuccess() {
        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.setReturnValue("Ghi nhận ví");
            dialog.triggerSave();

            assertTrue(dialog.isSuccess());
            assertNotNull(dialog.getResultData());
            assertEquals("Ghi nhận ví", dialog.getResultData().value());
            assertNull(dialog.getErrorMessage());
        });
    }

    @Test
    @DisplayName("Bắt giữ lỗi và chuyển tiếp qua errorConsumer khi onExecuteSave ném ngoại lệ")
    void testTriggerSaveFailure() {
        AtomicReference<String> errorRef = new AtomicReference<>();
        dialog.setErrorConsumer(errorRef::set);

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.setShouldThrow(true);
            dialog.triggerSave();

            assertFalse(dialog.isSuccess());
            assertNull(dialog.getResultData());
            assertEquals("Dữ liệu không hợp lệ", dialog.getErrorMessage());
            assertEquals("Dữ liệu không hợp lệ", errorRef.get());
        });
    }
}
