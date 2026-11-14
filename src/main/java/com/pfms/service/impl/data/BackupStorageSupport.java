package com.pfms.service.impl.data;

import com.pfms.domain.exception.StorageException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/**
 * Bộ hỗ trợ nội bộ kiểm tra đường dẫn và đọc ghi tệp sao lưu an toàn theo chuẩn UTF-8.
 */
final class BackupStorageSupport {

    // region Constructors
    private BackupStorageSupport() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static Path resolveWritableTargetPath(String destinationPath) {
        if (destinationPath == null || destinationPath.trim().isEmpty()) {
            throw new StorageException("Đường dẫn lưu trữ đích không hợp lệ hoặc rỗng",
                    new IllegalArgumentException("destinationPath cannot be blank"));
        }
        Path targetPath = normalizePath(destinationPath);
        if (Files.exists(targetPath) && Files.isDirectory(targetPath)) {
            throw new StorageException("Đường dẫn lưu trữ phải là tệp, không được là thư mục: " + destinationPath,
                    new IllegalArgumentException("destinationPath is a directory"));
        }
        return targetPath;
    }

    static Path resolveReadableBackupPath(String backupFilePath) {
        if (backupFilePath == null || backupFilePath.trim().isEmpty()) {
            throw new StorageException("Đường dẫn tệp phục hồi không hợp lệ hoặc rỗng",
                    new IllegalArgumentException("backupFilePath cannot be blank"));
        }
        Path backupPath = normalizePath(backupFilePath);
        if (!Files.exists(backupPath) || !Files.isRegularFile(backupPath)) {
            throw new StorageException("Tệp sao lưu không tồn tại hoặc không phải là tệp hợp lệ: " + backupFilePath,
                    new FileNotFoundException(backupFilePath));
        }
        return backupPath;
    }

    static String readUtf8String(Path backupPath) {
        try {
            return Files.readString(backupPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException("Không thể đọc tệp sao lưu: " + backupPath, e);
        }
    }

    static long writeUtf8Safely(Path targetPath, String content) {
        ensureParentDirectoryExists(targetPath);
        Path tempPath = targetPath.resolveSibling(targetPath.getFileName().toString() + ".tmp");
        try {
            Files.writeString(tempPath, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            moveAtomicallyWithFallback(tempPath, targetPath);
        } catch (IOException e) {
            deleteQuietly(tempPath);
            throw new StorageException("Lỗi ghi tệp sao lưu dữ liệu: " + targetPath, e);
        }
        return measureFileSize(targetPath, content);
    }
    // endregion

    // region Helpers
    private static Path normalizePath(String rawPath) {
        try {
            return Paths.get(rawPath).toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            throw new StorageException("Đường dẫn lưu trữ không đúng cú pháp hệ điều hành: " + rawPath, e);
        }
    }

    private static void ensureParentDirectoryExists(Path targetPath) {
        Path parent = targetPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new StorageException("Không thể khởi tạo thư mục lưu trữ: " + parent, e);
            }
        }
    }

    private static void moveAtomicallyWithFallback(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static long measureFileSize(Path targetPath, String content) {
        try {
            return Files.size(targetPath);
        } catch (IOException e) {
            return content.getBytes(StandardCharsets.UTF_8).length;
        }
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
    // endregion
}

