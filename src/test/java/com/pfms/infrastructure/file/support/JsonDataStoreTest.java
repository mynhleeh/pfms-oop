package com.pfms.infrastructure.file.support;

import com.pfms.domain.exception.DataCorruptionException;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử bộ lưu trữ JsonDataStore")
class JsonDataStoreTest {

    @TempDir
    Path tempDir;

    @Nested
    @DisplayName("Khởi tạo và tạo tệp ban đầu")
    class InitializationTests {

        @Test
        @DisplayName("Tự động tạo thư mục cha và tệp JSON rỗng hợp lệ khi tệp chưa tồn tại")
        void shouldInitializeAndCreateEmptyFileWhenFileDoesNotExist() {
            Path targetFile = tempDir.resolve("sub_folder").resolve("pfms_data.json");
            assertFalse(Files.exists(targetFile));

            JsonDataStore dataStore = new JsonDataStore(targetFile);

            assertTrue(Files.exists(targetFile));
            assertEquals(targetFile, dataStore.getStoragePath());

            BackupPayload payload = dataStore.load();
            assertNotNull(payload);
            assertNotNull(payload.getWallets());
            assertTrue(payload.getWallets().isEmpty());
            assertTrue(payload.getCategories().isEmpty());
            assertTrue(payload.getNeedGroups().isEmpty());
            assertTrue(payload.getTransactions().isEmpty());
            assertTrue(payload.getAdjustments().isEmpty());
        }

        @Test
        @DisplayName("Từ chối khi đường dẫn tệp null")
        void shouldThrowExceptionWhenPathIsNull() {
            assertThrows(NullPointerException.class, () -> new JsonDataStore((Path) null));
            assertThrows(NullPointerException.class, () -> new JsonDataStore((String) null));
        }

        @Test
        @DisplayName("Khởi tạo qua đường dẫn chuỗi (String path)")
        void shouldInitializeWithStringConstructor() {
            Path targetFile = tempDir.resolve("string_ctor.json");
            JsonDataStore dataStore = new JsonDataStore(targetFile.toString());
            assertTrue(Files.exists(targetFile));
            assertNotNull(dataStore.getPayload());
        }

        @Test
        @DisplayName("Ném StorageException khi đường dẫn chỉ định là một thư mục có sẵn")
        void shouldThrowStorageExceptionWhenPathIsDirectory() {
            assertThrows(StorageException.class, () -> new JsonDataStore(tempDir));
        }
    }

    @Nested
    @DisplayName("Đọc và ghi dữ liệu")
    class ReadWriteTests {

        @Test
        @DisplayName("Nạp dữ liệu đã tồn tại từ trước chính xác")
        void shouldLoadExistingDataSuccessfully() {
            Path targetFile = tempDir.resolve("existing_data.json");

            Wallet wallet = new Wallet(
                    UUID.randomUUID(),
                    "Ví Chính",
                    "Ví mặc định",
                    true,
                    LocalDateTime.now(),
                    new ActiveWalletState(),
                    Money.of("500000.00"));

            NeedGroup needGroup = new NeedGroup(
                    UUID.randomUUID(),
                    "Nhu cầu thiết yếu",
                    TransactionType.EXPENSE,
                    "shopping-cart",
                    "#FF0000");

            Category category = new Category(
                    UUID.randomUUID(),
                    "Ăn uống",
                    needGroup.getNeedGroupId(),
                    TransactionType.EXPENSE,
                    "utensils",
                    "#00FF00");

            BackupPayload initialPayload = new BackupPayload(
                    List.of(wallet),
                    List.of(needGroup),
                    List.of(category),
                    Collections.emptyList(),
                    Collections.emptyList());

            String json = JsonEntitySerializer.toJson(initialPayload);
            FileStorageSupport.writeUtf8Safely(targetFile, json);

            JsonDataStore dataStore = new JsonDataStore(targetFile);
            BackupPayload loaded = dataStore.load();

            assertEquals(1, loaded.getWallets().size());
            assertEquals("Ví Chính", loaded.getWallets().get(0).getName());
            assertEquals(1, loaded.getNeedGroups().size());
            assertEquals("Nhu cầu thiết yếu", loaded.getNeedGroups().get(0).getName());
            assertEquals(1, loaded.getCategories().size());
            assertEquals("Ăn uống", loaded.getCategories().get(0).getName());
        }

        @Test
        @DisplayName("Ghi dữ liệu mới an toàn và nạp lại khớp dữ liệu vừa ghi")
        void shouldSaveDataAtomicallyAndReload() {
            Path targetFile = tempDir.resolve("save_test.json");
            JsonDataStore dataStore = new JsonDataStore(targetFile);

            Wallet wallet = new Wallet(
                    UUID.randomUUID(),
                    "Ví Tiết Kiệm",
                    "Tiết kiệm dài hạn",
                    false,
                    LocalDateTime.now(),
                    new ActiveWalletState(),
                    Money.of("1000000.00"));

            BackupPayload newPayload = new BackupPayload(
                    List.of(wallet),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList());

            dataStore.save(newPayload);

            // Xác thực bộ nhớ đệm
            assertEquals(1, dataStore.getPayload().getWallets().size());
            assertEquals("Ví Tiết Kiệm", dataStore.getPayload().getWallets().get(0).getName());

            // Nạp lại từ đĩa vật lý
            BackupPayload reloaded = dataStore.load();
            assertEquals(1, reloaded.getWallets().size());
            assertEquals("Ví Tiết Kiệm", reloaded.getWallets().get(0).getName());
        }

        @Test
        @DisplayName("Cập nhật payload qua setPayload")
        void shouldUpdateViaSetPayload() {
            Path targetFile = tempDir.resolve("set_payload_test.json");
            JsonDataStore dataStore = new JsonDataStore(targetFile);

            Wallet wallet = new Wallet(
                    UUID.randomUUID(),
                    "Ví Phụ",
                    "",
                    false,
                    LocalDateTime.now(),
                    new ActiveWalletState(),
                    Money.of("200000.00"));

            BackupPayload newPayload = new BackupPayload(
                    List.of(wallet),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList());

            dataStore.setPayload(newPayload);

            assertEquals(1, dataStore.getPayload().getWallets().size());
            assertEquals(1, dataStore.load().getWallets().size());
        }
    }

    @Nested
    @DisplayName("Xử lý lỗi và hỏng dữ liệu")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Ném DataCorruptionException khi tệp JSON bị hỏng cú pháp")
        void shouldThrowDataCorruptionExceptionWhenFileIsMalformed() throws IOException {
            Path targetFile = tempDir.resolve("malformed.json");
            Files.writeString(targetFile, "{ \"wallets\": [ malformed json ... ");

            assertThrows(DataCorruptionException.class, () -> new JsonDataStore(targetFile));
        }

        @Test
        @DisplayName("Ném DataCorruptionException khi nội dung tệp rỗng")
        void shouldThrowDataCorruptionExceptionWhenFileIsEmpty() throws IOException {
            Path targetFile = tempDir.resolve("empty.json");
            Files.writeString(targetFile, "   ");

            assertThrows(DataCorruptionException.class, () -> new JsonDataStore(targetFile));
        }
    }
}
