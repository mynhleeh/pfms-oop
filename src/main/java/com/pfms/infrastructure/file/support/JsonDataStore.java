package com.pfms.infrastructure.file.support;

import com.pfms.domain.exception.DataCorruptionException;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.data.BackupPayload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Bộ điều hợp quản lý tệp dữ liệu JSON lưu trữ bền vững (pfms_data.json) trên đĩa cục bộ.
 * Thực thi quy trình khởi tạo thư mục/tệp ban đầu, đọc ghi tệp nguyên tử qua luồng UTF-8
 * và chuyển đổi ngoại lệ cấp thấp theo chuẩn OOD 4.3.2.
 */
public class JsonDataStore {

    // region State
    private final Path storagePath;
    private BackupPayload currentPayload;
    // endregion

    // region Constructors
    public JsonDataStore(Path storagePath) {
        Objects.requireNonNull(storagePath, "Đường dẫn tệp lưu trữ không được để null");
        this.storagePath = storagePath.toAbsolutePath().normalize();
        if (Files.exists(this.storagePath) && Files.isDirectory(this.storagePath)) {
            throw new StorageException(
                    "Đường dẫn lưu trữ phải là tệp tin, không được là thư mục: " + storagePath,
                    new IllegalArgumentException("Path is directory"));
        }
        initialize();
    }

    public JsonDataStore(String storagePathString) {
        this(Paths.get(Objects.requireNonNull(storagePathString, "Đường dẫn chuỗi tệp lưu trữ không được để null")));
    }
    // endregion

    // region Queries
    public Path getStoragePath() {
        return this.storagePath;
    }

    public synchronized BackupPayload getPayload() {
        if (this.currentPayload == null) {
            this.currentPayload = load();
        }
        return this.currentPayload;
    }

    public synchronized BackupPayload load() {
        if (!Files.exists(this.storagePath) || !Files.isRegularFile(this.storagePath)) {
            throw new StorageException(
                    "Tệp dữ liệu không tồn tại hoặc không phải là tệp hợp lệ: " + this.storagePath,
                    new java.io.FileNotFoundException(this.storagePath.toString()));
        }

        String json = FileStorageSupport.readUtf8String(this.storagePath);
        if (json == null || json.trim().isEmpty()) {
            throw DataCorruptionException.syntaxError(
                    this.storagePath.getFileName().toString(), 1,
                    new IllegalArgumentException("Nội dung tệp JSON lưu trữ rỗng"));
        }

        BackupPayload payload = JsonEntityDeserializer.fromJson(json);
        this.currentPayload = payload;
        return payload;
    }
    // endregion

    // region Commands
    public synchronized void save(BackupPayload payload) {
        Objects.requireNonNull(payload, "BackupPayload không được để null");
        String json = JsonEntitySerializer.toJson(payload);
        FileStorageSupport.writeUtf8Safely(this.storagePath, json);
        this.currentPayload = payload;
    }

    public synchronized void setPayload(BackupPayload payload) {
        save(payload);
    }
    // endregion

    // region Helpers
    private synchronized void initialize() {
        if (!Files.exists(this.storagePath)) {
            Path parent = this.storagePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                try {
                    Files.createDirectories(parent);
                } catch (IOException e) {
                    throw new StorageException("Không thể khởi tạo thư mục lưu trữ: " + parent, e);
                }
            }
            this.currentPayload = BackupPayload.empty();
            save(this.currentPayload);
        } else {
            this.currentPayload = load();
        }
    }
    // endregion
}
