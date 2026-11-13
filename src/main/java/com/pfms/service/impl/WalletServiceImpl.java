package com.pfms.service.impl;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.DuplicateEntityException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.common.EntityName;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.Note;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.BalanceAdjustmentRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.wallet.AdjustBalanceRequestDTO;
import com.pfms.presentation.dto.wallet.BalanceAdjustmentResponseDTO;
import com.pfms.presentation.dto.wallet.CreateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.UpdateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryDTO;
import com.pfms.service.WalletService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Lớp cài đặt nội bộ của dịch vụ quản lý Ví tiền (WalletService).
 * Điều phối luồng nghiệp vụ giữa Tầng Trình diễn và Tầng Nghiệp vụ cốt lõi.
 */
public class WalletServiceImpl implements WalletService {

    // region State
    private final WalletRepository walletRepository;
    private final BalanceAdjustmentRepository balanceAdjustmentRepository;
    private final TransactionRepository transactionRepository;
    // endregion

    // region Constructors
    public WalletServiceImpl(
            WalletRepository walletRepository,
            BalanceAdjustmentRepository balanceAdjustmentRepository,
            TransactionRepository transactionRepository) {

        this.walletRepository = Objects.requireNonNull(walletRepository, "WalletRepository không được để null");
        this.balanceAdjustmentRepository = Objects.requireNonNull(balanceAdjustmentRepository,
                "BalanceAdjustmentRepository không được để null");
        this.transactionRepository = Objects.requireNonNull(transactionRepository,
                "TransactionRepository không được để null");
    }
    // endregion

    // region Commands
    @Override
    public void initializeDefaultWallet() {
        if (walletRepository.findDefaultWallet().isPresent()) {
            return;
        }

        Wallet defaultWallet = new Wallet(
                UUID.randomUUID(),
                "Tiền mặt",
                "Ví mặc định hệ thống",
                true);

        walletRepository.save(defaultWallet);
    }

    @Override
    public WalletResponseDTO createWallet(CreateWalletRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu tạo ví không được để null");

        Wallet wallet = new Wallet(
                UUID.randomUUID(),
                request.getName(),
                request.getNote(),
                false);

        if (walletRepository.existsByName(wallet.getName())) {
            throw new DuplicateEntityException(ErrorCode.ERR_NAM_002, "Ví", wallet.getName());
        }

        Wallet saved = walletRepository.save(wallet);

        return toResponseDTO(saved);
    }

    @Override
    public WalletResponseDTO updateWallet(UpdateWalletRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu cập nhật ví không được để null");
        Objects.requireNonNull(request.getWalletId(), "Mã ví không được để null");

        String validName = EntityName.of(request.getName(), "Tên ví").getValue();
        Note.of(request.getNote(), "Ghi chú ví");

        Wallet wallet = EntityLookupSupport.findWalletOrThrow(walletRepository, request.getWalletId());

        if (!wallet.getName().equalsIgnoreCase(validName)) {
            if (walletRepository.existsByName(validName)) {
                throw new DuplicateEntityException(ErrorCode.ERR_NAM_002, "Ví", validName);
            }
        }

        wallet.updateInfo(validName, request.getNote());
        Wallet saved = walletRepository.save(wallet);

        return toResponseDTO(saved);
    }

    @Override
    public WalletResponseDTO archiveWallet(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví không được để null");

        Wallet wallet = EntityLookupSupport.findWalletOrThrow(walletRepository, walletId);

        wallet.archive();
        Wallet saved = walletRepository.save(wallet);

        return toResponseDTO(saved);
    }

    @Override
    public WalletResponseDTO activateWallet(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví không được để null");

        Wallet wallet = EntityLookupSupport.findWalletOrThrow(walletRepository, walletId);

        wallet.activate();
        Wallet saved = walletRepository.save(wallet);

        return toResponseDTO(saved);
    }

    @Override
    public void deleteWallet(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví không được để null");

        Wallet wallet = EntityLookupSupport.findWalletOrThrow(walletRepository, walletId);

        if (wallet.isDefaultWallet()) {
            throw new BusinessException(ErrorCode.ERR_WAL_001, "Không thể xóa ví mặc định của hệ thống.");
        }

        if (!transactionRepository.findByWalletId(walletId).isEmpty()) {
            throw new BusinessException(ErrorCode.ERR_WAL_001, "Không thể xóa ví đã phát sinh giao dịch.");
        }

        if (!balanceAdjustmentRepository.findByWalletId(walletId).isEmpty()) {
            throw new BusinessException(ErrorCode.ERR_WAL_001, "Không thể xóa ví đã phát sinh lịch sử kiểm kê.");
        }

        walletRepository.deleteById(walletId);
    }

    @Override
    public BalanceAdjustmentResponseDTO adjustBalance(AdjustBalanceRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu điều chỉnh số dư không được để null");
        Objects.requireNonNull(request.getWalletId(), "Mã ví không được để null");

        Money actualBalance = Money.of(request.getActualBalance());
        TransactionDate adjustedAt = TransactionDate.ofOrToday(request.getAdjustedAt());

        Wallet wallet = EntityLookupSupport.findWalletOrThrow(walletRepository, request.getWalletId());
        Wallet previousSnapshot = wallet.copy();
        BalanceAdjustmentRecord record = wallet.adjustBalance(actualBalance, adjustedAt, request.getReason());

        walletRepository.save(wallet);
        try {
            balanceAdjustmentRepository.save(record);
        } catch (StorageException storageException) {
            runRollbackStep(() -> walletRepository.save(previousSnapshot), storageException);
            throw storageException;
        }

        return toAdjustmentResponseDTO(record);
    }
    // endregion

    // region Queries
    @Override
    public WalletsSummaryDTO getWalletsSummary(EntityStatus statusFilter) {
        List<Wallet> filteredWallets = (statusFilter == null)
                ? walletRepository.findAll()
                : walletRepository.findByStatus(statusFilter);

        Money totalSystemMoney = walletRepository.findAll().stream()
                .filter(this::isIncludedInSystemTotal)
                .map(Wallet::getCurrentBalance)
                .reduce(Money.ZERO, Money::add);

        BigDecimal totalBalance = totalSystemMoney.getAmount();
        List<WalletResponseDTO> walletDtos = filteredWallets.stream()
                .map(this::toResponseDTO)
                .toList();

        Map<UUID, BigDecimal> sortedShares = calculateWalletShares(filteredWallets, totalBalance);

        return new WalletsSummaryDTO(walletDtos, totalBalance, totalSystemMoney.getCurrency(), sortedShares);
    }
    // endregion

    // region Helpers
    private WalletResponseDTO toResponseDTO(Wallet wallet) {
        return new WalletResponseDTO(
                wallet.getId(),
                wallet.getName(),
                wallet.getCurrentBalance().getAmount(),
                wallet.getCurrentBalance().getCurrency(),
                wallet.isDefaultWallet(),
                wallet.getStatus().name(),
                wallet.getNote(),
                wallet.getCreatedAt());
    }

    private BalanceAdjustmentResponseDTO toAdjustmentResponseDTO(BalanceAdjustmentRecord record) {
        return new BalanceAdjustmentResponseDTO(
                record.getRecordId(),
                record.getWalletId(),
                record.getPreviousBalance().getAmount(),
                record.getActualBalance().getAmount(),
                record.getDifference().getAmount(),
                record.getActualBalance().getCurrency(),
                record.getReason(),
                record.getAdjustedAt().getDate(),
                LocalDateTime.now());
    }

    private boolean isIncludedInSystemTotal(Wallet wallet) {
        return wallet.getStatus() == EntityStatus.ACTIVE
                || (wallet.getStatus() == EntityStatus.ARCHIVED && wallet.getCurrentBalance().isPositive());
    }

    private Map<UUID, BigDecimal> calculateWalletShares(List<Wallet> wallets, BigDecimal totalBalance) {
        boolean isTotalZero = totalBalance.compareTo(BigDecimal.ZERO) == 0;
        Map<UUID, BigDecimal> rawShares = new HashMap<>();

        for (Wallet wallet : wallets) {
            if (wallet.getStatus() == EntityStatus.ARCHIVED && wallet.getCurrentBalance().isZero()) {
                continue;
            }
            rawShares.put(wallet.getId(), calculateShare(wallet, totalBalance, isTotalZero));
        }

        return rawShares.entrySet().stream()
                .sorted(Map.Entry.<UUID, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
    }

    private BigDecimal calculateShare(Wallet wallet, BigDecimal totalBalance, boolean isTotalZero) {
        if (isTotalZero) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }
        return wallet.getCurrentBalance().getAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(totalBalance, 4, RoundingMode.HALF_EVEN)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private void runRollbackStep(Runnable compensatingStep, StorageException primaryException) {
        try {
            compensatingStep.run();
        } catch (RuntimeException rollbackException) {
            primaryException.addSuppressed(rollbackException);
        }
    }
    // endregion
}
