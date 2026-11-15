package com.pfms.infrastructure.file;

import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.BackupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FileBackupRepository Implementation Tests")
class FileBackupRepositoryTest {

    @TempDir
    Path tempDir;

    private BackupRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FileBackupRepository();
    }

    @Test
    @DisplayName("Should resolve writable target path inside directory")
    void shouldResolveWritableTargetPath() {
        LocalDateTime now = LocalDateTime.of(2026, 11, 15, 12, 0, 0);
        Path target = repository.resolveWritableTargetPath(tempDir.toString(), "FULL", now);

        assertNotNull(target);
        assertEquals(tempDir.resolve("pfms_export_full_20261115_120000.json").toAbsolutePath(), target);
    }

    @Test
    @DisplayName("writeBackup and readBackup should round-trip BackupPayload cleanly")
    void shouldWriteAndReadBackupCleanly() {
        Wallet wallet = new Wallet(UUID.randomUUID(), "Ví tiền", "Ghi chú", true);
        wallet.credit(Money.of("500000.00"));
        NeedGroup group = new NeedGroup(UUID.randomUUID(), "Sinh hoạt", TransactionType.EXPENSE, "home", "#112233");
        Category cat = new Category(UUID.randomUUID(), "Tiền điện", group.getNeedGroupId(), TransactionType.EXPENSE, "electric", "#334455");
        IncomeExpenseTransaction txn = new IncomeExpenseTransaction(
                UUID.randomUUID(), TransactionType.EXPENSE, wallet, cat,
                Money.of("200000.00"), TransactionDate.of(LocalDate.of(2026, 11, 15)), "Đóng tiền điện", null);

        BackupPayload original = new BackupPayload(
                List.of(wallet), List.of(group), List.of(cat), List.of(txn), List.of());

        Path target = repository.resolveWritableTargetPath(tempDir.toString(), "FULL", LocalDateTime.now());
        long bytes = repository.writeBackup(target, original);

        assertTrue(bytes > 0);
        assertTrue(Files.exists(target));

        BackupPayload restored = repository.readBackup(target.toString());
        assertNotNull(restored);
        assertEquals(1, restored.getWallets().size());
        assertEquals("Ví tiền", restored.getWallets().get(0).getName());
        assertEquals(1, restored.getCategories().size());
        assertEquals(1, restored.getTransactions().size());
    }

    @Test
    @DisplayName("Should validate null constraints on writeBackup and readBackup")
    void shouldValidateNullConstraints() {
        assertThrows(NullPointerException.class, () -> repository.writeBackup(null, BackupPayload.empty()));
        assertThrows(NullPointerException.class, () -> repository.writeBackup(tempDir.resolve("test.json"), null));
        assertThrows(StorageException.class, () -> repository.readBackup(null));
        assertThrows(StorageException.class, () -> repository.readBackup("   "));
    }

    @Test
    @DisplayName("Should throw StorageException on non-existent file")
    void shouldThrowStorageExceptionOnMissingFile() {
        assertThrows(StorageException.class, () -> repository.readBackup(tempDir.resolve("missing.json").toString()));
    }
}
