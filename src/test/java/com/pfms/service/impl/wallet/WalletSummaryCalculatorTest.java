package com.pfms.service.impl.wallet;

import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.ArchivedWalletState;
import com.pfms.domain.model.wallet.DeletedWalletState;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.infrastructure.memory.MemoryWalletRepository;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("WalletSummaryCalculator Unit Tests")
class WalletSummaryCalculatorTest {

    @Test
    @DisplayName("Should throw AssertionError when trying to instantiate utility class")
    void testUtilityClassDesign() throws Exception {
        Constructor<WalletSummaryCalculator> constructor =
                WalletSummaryCalculator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Test
    @DisplayName("Should reject null repository or wallet entity")
    void shouldRejectNullInputs() {
        assertThrows(NullPointerException.class,
                () -> WalletSummaryCalculator.buildWalletsSummary(null, EntityStatus.ACTIVE));
        assertThrows(NullPointerException.class,
                () -> WalletSummaryCalculator.toResponseDTO(null));
    }

    @Test
    @DisplayName("Should sort filtered wallets ascending by Vietnamese locale name (FR-WAL-02)")
    void shouldSortWalletsAscendingByVietnameseName() {
        MemoryWalletRepository repository = new MemoryWalletRepository();
        LocalDateTime now = LocalDateTime.now();
        Wallet cash = new Wallet(UUID.randomUUID(), "Tiền mặt", "", true, now, new ActiveWalletState(), Money.of("100.00"));
        Wallet dining = new Wallet(UUID.randomUUID(), "Ăn uống", "", false, now, new ActiveWalletState(), Money.of("200.00"));
        Wallet bank = new Wallet(UUID.randomUUID(), "Bảo hiểm", "", false, now, new ActiveWalletState(), Money.of("300.00"));
        repository.save(cash);
        repository.save(dining);
        repository.save(bank);

        WalletsSummaryResponseDTO summary = WalletSummaryCalculator.buildWalletsSummary(repository, EntityStatus.ACTIVE);

        List<String> names = summary.getWallets().stream()
                .map(WalletResponseDTO::getName)
                .toList();
        assertEquals(List.of("Ăn uống", "Bảo hiểm", "Tiền mặt"), names);
    }

    @Test
    @DisplayName("Should exclude DELETED wallets from ALL summary and share breakdown")
    void shouldExcludeDeletedWalletsFromAllSummaryAndShares() {
        MemoryWalletRepository repository = new MemoryWalletRepository();
        LocalDateTime now = LocalDateTime.now();
        Wallet active = new Wallet(UUID.randomUUID(), "Ví Active", "", false, now, new ActiveWalletState(), Money.of("1000.00"));
        Wallet archivedPositive = new Wallet(UUID.randomUUID(), "Ví Lưu Trữ Dương", "", false, now, new ArchivedWalletState(), Money.of("1000.00"));
        Wallet archivedZero = new Wallet(UUID.randomUUID(), "Ví Lưu Trữ Rỗng", "", false, now, new ArchivedWalletState(), Money.ZERO);
        Wallet deletedWithBalance = new Wallet(UUID.randomUUID(), "Ví Đã Xóa", "", false, now, new DeletedWalletState(), Money.of("5000.00"));
        repository.save(active);
        repository.save(archivedPositive);
        repository.save(archivedZero);
        repository.save(deletedWithBalance);

        WalletsSummaryResponseDTO summary = WalletSummaryCalculator.buildWalletsSummary(repository, null);

        assertEquals(3, summary.getWalletCount());
        assertFalse(summary.getWallets().stream().anyMatch(w -> w.getWalletId().equals(deletedWithBalance.getId())));
        assertEquals(0, new BigDecimal("2000.00").compareTo(summary.getTotalBalance()));
        assertEquals(2, summary.getWalletShares().size());
        assertTrue(summary.getWalletShares().containsKey(active.getId()));
        assertTrue(summary.getWalletShares().containsKey(archivedPositive.getId()));
        assertFalse(summary.getWalletShares().containsKey(archivedZero.getId()));
        assertFalse(summary.getWalletShares().containsKey(deletedWithBalance.getId()));
        assertEquals(new BigDecimal("50.00"), summary.getWalletShare(active.getId()));
        assertEquals(new BigDecimal("50.00"), summary.getWalletShare(archivedPositive.getId()));
    }

    @Test
    @DisplayName("Should scan repository once without calling findByStatus when statusFilter is non-null")
    void shouldQueryRepositoryOnceWithoutCallingFindByStatusWhenStatusFilterProvided() {
        AtomicInteger findAllCalls = new AtomicInteger(0);
        MemoryWalletRepository countingRepository = new MemoryWalletRepository() {
            @Override
            public List<Wallet> findAll() {
                findAllCalls.incrementAndGet();
                return super.findAll();
            }

            @Override
            public List<Wallet> findByStatus(EntityStatus status) {
                return fail("Không được gọi findByStatus lần 2 khi đã có allWallets từ findAll");
            }
        };
        LocalDateTime now = LocalDateTime.now();
        Wallet active = new Wallet(UUID.randomUUID(), "Ví Active", "", false, now, new ActiveWalletState(), Money.of("1000.00"));
        Wallet archived = new Wallet(UUID.randomUUID(), "Ví Lưu Trữ", "", false, now, new ArchivedWalletState(), Money.of("500.00"));
        countingRepository.save(active);
        countingRepository.save(archived);

        WalletsSummaryResponseDTO summary = WalletSummaryCalculator.buildWalletsSummary(countingRepository, EntityStatus.ACTIVE);

        assertEquals(1, findAllCalls.get());
        assertEquals(1, summary.getWalletCount());
        assertEquals(active.getId(), summary.getWallets().get(0).getWalletId());
    }

    @Test
    @DisplayName("Should include positive ARCHIVED wallet in system totalBalance when filtering ACTIVE (AC-WAL-04.3)")
    void shouldIncludePositiveArchivedWalletInTotalBalanceAndShareWhenFilteringActive() {
        MemoryWalletRepository repository = new MemoryWalletRepository();
        LocalDateTime now = LocalDateTime.now();
        Wallet active = new Wallet(UUID.randomUUID(), "Ví Hoạt Động", "", false, now, new ActiveWalletState(), Money.of("9950000.00"));
        Wallet archivedPositive = new Wallet(UUID.randomUUID(), "Ví Lưu Trữ", "", false, now, new ArchivedWalletState(), Money.of("50000.00"));
        repository.save(active);
        repository.save(archivedPositive);

        WalletsSummaryResponseDTO summary = WalletSummaryCalculator.buildWalletsSummary(repository, EntityStatus.ACTIVE);

        assertEquals(1, summary.getWalletCount());
        assertEquals(0, new BigDecimal("10000000.00").compareTo(summary.getTotalBalance()));
        assertEquals(new BigDecimal("99.50"), summary.getWalletShare(active.getId()));
    }
}

