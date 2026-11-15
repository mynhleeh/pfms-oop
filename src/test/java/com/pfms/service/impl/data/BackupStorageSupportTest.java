package com.pfms.service.impl.data;

import com.pfms.domain.exception.StorageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BackupStorageSupport Unit Tests")
class BackupStorageSupportTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Should throw AssertionError when trying to instantiate utility class")
    void testUtilityClassDesign() throws Exception {
        Constructor<BackupStorageSupport> constructor = BackupStorageSupport.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Test
    @DisplayName("Should resolve timestamped filename inside valid directory for FULL and TRANSACTIONS scopes")
    void shouldResolveTimestampedFilenameInsideDirectory() {
        LocalDateTime exportedAt = LocalDateTime.of(2026, 11, 15, 7, 45, 30);

        Path fullPath = BackupStorageSupport.resolveWritableTargetPath(
                tempDir.toString(), "FULL", exportedAt);
        Path txnsPath = BackupStorageSupport.resolveWritableTargetPath(
                tempDir.toString(), "TRANSACTIONS", exportedAt);

        assertEquals(tempDir.resolve("pfms_export_full_20261115_074530.json").toAbsolutePath(), fullPath);
        assertEquals(tempDir.resolve("pfms_export_transactions_20261115_074530.json").toAbsolutePath(), txnsPath);
    }

    @Test
    @DisplayName("Should reject blank path, existing file path, and path with file extension")
    void shouldRejectNonDirectoryDestinationPaths() throws Exception {
        assertThrows(StorageException.class, () -> BackupStorageSupport.resolveWritableTargetPath(null, "FULL"));
        assertThrows(StorageException.class, () -> BackupStorageSupport.resolveWritableTargetPath("   ", "FULL"));

        Path existingFile = Files.writeString(tempDir.resolve("existing.json"), "{}", StandardCharsets.UTF_8);
        assertThrows(StorageException.class,
                () -> BackupStorageSupport.resolveWritableTargetPath(existingFile.toString(), "FULL"));

        Path nonexistentJsonFile = tempDir.resolve("manual_backup.json");
        assertThrows(StorageException.class,
                () -> BackupStorageSupport.resolveWritableTargetPath(nonexistentJsonFile.toString(), "FULL"));
    }

    @Test
    @DisplayName("Should write UTF-8 without BOM cleanly and leave no temporary file")
    void shouldWriteUtf8WithoutBomAndCleanTempFile() throws Exception {
        LocalDateTime exportedAt = LocalDateTime.of(2026, 11, 15, 8, 0, 0);
        Path targetPath = BackupStorageSupport.resolveWritableTargetPath(
                tempDir.resolve("exports/2026").toString(), "FULL", exportedAt);

        String json = "{\"wallets\":[]}";
        long writtenBytes = BackupStorageSupport.writeUtf8Safely(targetPath, json);

        assertEquals(json.getBytes(StandardCharsets.UTF_8).length, writtenBytes);
        assertTrue(Files.isRegularFile(targetPath));
        assertFalse(Files.exists(targetPath.resolveSibling(targetPath.getFileName().toString() + ".tmp")));

        byte[] raw = Files.readAllBytes(targetPath);
        assertFalse(raw.length >= 3
                && (raw[0] & 0xFF) == 0xEF
                && (raw[1] & 0xFF) == 0xBB
                && (raw[2] & 0xFF) == 0xBF);
    }

    @Test
    @DisplayName("Should reject directory or missing file on read and strip UTF-8 BOM if present")
    void shouldValidateReadablePathAndStripUtf8Bom() throws Exception {
        assertThrows(StorageException.class, () -> BackupStorageSupport.resolveReadableBackupPath(tempDir.toString()));
        assertThrows(StorageException.class,
                () -> BackupStorageSupport.resolveReadableBackupPath(tempDir.resolve("missing.json").toString()));

        Path bomFile = tempDir.resolve("with_bom.json");
        byte[] bomBytes = "\uFEFF{\"wallets\":[]}".getBytes(StandardCharsets.UTF_8);
        Files.write(bomFile, bomBytes);

        Path resolved = BackupStorageSupport.resolveReadableBackupPath(bomFile.toString());
        assertEquals("{\"wallets\":[]}", BackupStorageSupport.readUtf8String(resolved));
    }
}

