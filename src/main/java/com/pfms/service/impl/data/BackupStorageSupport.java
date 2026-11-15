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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Bộ hỗ trợ nội bộ kiểm tra đường dẫn thư mục/tệp và đọc ghi tệp sao lưu an toàn theo chuẩn UTF-8.
 */
final class BackupStorageSupport {

    // region Constants
    private static final DateTimeFormatter EXPORT_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String UTF8_BOM = "\uFEFF";
    // endregion

    // region Constructors
    private BackupStorageSupport() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static Path resolveWritableTargetPath(String destinationDirectoryPath, String scope) {
        return resolveWritableTargetPath(destinationDirectoryPath, scope, LocalDateTime.now());
    }

    static Path resolveWritableTargetPath(
            String destinationDirectoryPath,
            String scope,
            LocalDateTime exportedAt) {
        if (destinationDirectoryPath == null || destinationDirectoryPath.trim().isEmpty()) {
            throw new StorageException("Đường dẫn thư mục lưu trữ đích không hợp lệ hoặc rỗng",
                    new IllegalArgumentException("destinationPath cannot be blank"));
        }
        Path directoryPath = normalizePath(destinationDirectoryPath);
        if (Files.exists(directoryPath)) {
            if (!Files.isDirectory(directoryPath)) {
                throw new StorageException(
                        "Đường dẫn xuất sao lưu phải là thư mục hợp lệ, không được là tệp: "
                                + destinationDirectoryPath,
                        new IllegalArgumentException("destinationPath is not a directory"));
            }
        } else if (hasFileExtension(directoryPath)) {
            throw new StorageException(
                    "Đường dẫn xuất sao lưu phải là thư mục hợp lệ, không được truyền tên tệp: "
                            + destinationDirectoryPath,
                    new IllegalArgumentException("destinationPath appears to be a file path"));
        }

        String normalizedScope = (scope == null || scope.trim().isEmpty())
                ? "full"
                : scope.trim().toLowerCase(Locale.ROOT);
        LocalDateTime effectiveTimestamp = (exportedAt == null) ? LocalDateTime.now() : exportedAt;
        String fileName = "pfms_export_"
                + normalizedScope
                + "_"
                + EXPORT_TIMESTAMP_FORMATTER.format(effectiveTimestamp)
                + ".json";
        return directoryPath.resolve(fileName);
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
        if (!Files.isReadable(backupPath)) {
            throw new StorageException("Không có quyền đọc tệp sao lưu: " + backupFilePath,
                    new IOException("File is not readable: " + backupFilePath));
        }
        return backupPath;
    }

    static String readUtf8String(Path backupPath) {
        try {
            String content = Files.readString(backupPath, StandardCharsets.UTF_8);
            if (content.startsWith(UTF8_BOM)) {
                return content.substring(UTF8_BOM.length());
            }
            return content;
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

    private static boolean hasFileExtension(Path directoryPath) {
        Path lastSegment = directoryPath.getFileName();
        if (lastSegment == null) {
            return false;
        }
        String name = lastSegment.toString();
        int lastDotIndex = name.lastIndexOf('.');
        return lastDotIndex > 0 && lastDotIndex < name.length() - 1;
    }
    // endregion
}

