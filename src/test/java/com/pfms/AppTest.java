package com.pfms;

import com.pfms.presentation.common.MainController;
import com.pfms.presentation.common.MainStage;
import com.pfms.testutil.JavaFxTestHelper;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Kiểm thử App - Điểm khởi chạy và nạp chuỗi phụ thuộc ứng dụng JavaFX")
class AppTest {

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Test
    @DisplayName("buildMainController khởi tạo chuỗi phụ thuộc và dịch vụ hoàn chỉnh")
    void testBuildMainController(@TempDir Path tempDir) {
        Path storageFile = tempDir.resolve("test_controller_data.json");
        MainController controller = App.buildMainController(storageFile);

        assertNotNull(controller);
        assertNotNull(controller.getWalletController());
        assertNotNull(controller.getTransactionController());
        assertNotNull(controller.getTransferController());
        assertNotNull(controller.getCategoryController());
        assertNotNull(controller.getReportController());
        assertNotNull(controller.getDataManagementController());
    }

    @Test
    @DisplayName("createFxApplication khởi tạo ứng dụng JavaFX thành công với đường dẫn tệp")
    void testCreateFxApplicationSuccess(@TempDir Path tempDir) {
        Path storageFile = tempDir.resolve("test_fx_app.json");
        JavaFxTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            MainStage mainStage = App.createFxApplication(storageFile, stage);
            assertNotNull(mainStage);
            assertNotNull(mainStage.getMainController());
            assertNotNull(mainStage.getSidebarNav());
            assertNotNull(mainStage.getStatusBar());
        });
    }

    @Test
    @DisplayName("createFxApplication ném NullPointerException khi tham số null")
    void testCreateFxApplicationNullChecks(@TempDir Path tempDir) {
        Path storageFile = tempDir.resolve("test_fx.json");
        assertThrows(NullPointerException.class, () -> App.createFxApplication(null, null));
        assertThrows(NullPointerException.class, () -> App.createFxApplication(storageFile, null));
    }
}
