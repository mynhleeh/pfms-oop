package com.pfms.service.impl;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.DuplicateEntityException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.BalanceAdjustmentRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.AdjustBalanceRequestDTO;
import com.pfms.presentation.dto.BalanceAdjustmentResponseDTO;
import com.pfms.presentation.dto.CreateWalletRequestDTO;
import com.pfms.presentation.dto.UpdateWalletRequestDTO;
import com.pfms.presentation.dto.WalletResponseDTO;
import com.pfms.presentation.dto.WalletsSummaryDTO;
import com.pfms.service.WalletService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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

        String name = request.getName();
        if (walletRepository.existsByName(name)) {
            throw new DuplicateEntityException(ErrorCode.ERR_NAM_002, "Ví", name);
        }

        Wallet wallet = new Wallet(
                UUID.randomUUID(),
                name,
                request.getNote(),
                false);

        Wallet saved = walletRepository.save(wallet);

        return new WalletResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getCurrentBalance().getAmount(),
                saved.getCurrentBalance().getCurrency(),
                saved.isDefaultWallet(),
                saved.getStatus().name(),
                saved.getNote(),
                saved.getCreatedAt());
    }

    @Override
    public WalletResponseDTO updateWallet(UpdateWalletRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu cập nhật ví không được để null");

        UUID walletId = request.getWalletId();
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, "Ví", walletId.toString()));

        String newName = request.getName().trim();
        if (!wallet.getName().equalsIgnoreCase(newName)) {
            if (walletRepository.existsByName(newName)) {
                throw new DuplicateEntityException(ErrorCode.ERR_NAM_002, "Ví", newName);
            }
        }

        wallet.updateInfo(newName, request.getNote());
        Wallet saved = walletRepository.save(wallet);

        return new WalletResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getCurrentBalance().getAmount(),
                saved.getCurrentBalance().getCurrency(),
                saved.isDefaultWallet(),
                saved.getStatus().name(),
                saved.getNote(),
                saved.getCreatedAt());
    }

    @Override
    public WalletResponseDTO archiveWallet(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví không được để null");

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, "Ví", walletId.toString()));

        wallet.archive();
        Wallet saved = walletRepository.save(wallet);

        return new WalletResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getCurrentBalance().getAmount(),
                saved.getCurrentBalance().getCurrency(),
                saved.isDefaultWallet(),
                saved.getStatus().name(),
                saved.getNote(),
                saved.getCreatedAt());
    }

    @Override
    public WalletResponseDTO activateWallet(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví không được để null");

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, "Ví", walletId.toString()));

        wallet.activate();
        Wallet saved = walletRepository.save(wallet);

        return new WalletResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getCurrentBalance().getAmount(),
                saved.getCurrentBalance().getCurrency(),
                saved.isDefaultWallet(),
                saved.getStatus().name(),
                saved.getNote(),
                saved.getCreatedAt());
    }

    @Override
    public void deleteWallet(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví không được để null");

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, "Ví", walletId.toString()));

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

        UUID walletId = request.getWalletId();
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, "Ví", walletId.toString()));

        Money actualBalance = Money.of(request.getActualBalance());
        TransactionDate adjustedAt = (request.getAdjustedAt() == null)
                ? TransactionDate.now()
                : TransactionDate.of(request.getAdjustedAt());

        BalanceAdjustmentRecord record = wallet.adjustBalance(actualBalance, adjustedAt, request.getReason());

        walletRepository.save(wallet);
        balanceAdjustmentRepository.save(record);

        return new BalanceAdjustmentResponseDTO(
                record.getRecordId(),
                record.getWalletId(),
                record.getPreviousBalance().getAmount(),
                record.getActualBalance().getAmount(),
                record.getDifference().getAmount(),
                record.getActualBalance().getCurrency(),
                record.getReason(),
                record.getAdjustedAt().getDate(),
                java.time.LocalDateTime.now());
    }
    // endregion

    // region Queries
    @Override
    public WalletsSummaryDTO getWalletsSummary(EntityStatus statusFilter) {
        List<Wallet> filteredWallets = (statusFilter == null)
                ? walletRepository.findAll()
                : walletRepository.findByStatus(statusFilter);

        List<Wallet> allWallets = walletRepository.findAll();
        Money totalSystemMoney = Money.ZERO;
        for (Wallet w : allWallets) {
            if (w.getStatus() == EntityStatus.ACTIVE ||
                    (w.getStatus() == EntityStatus.ARCHIVED && w.getCurrentBalance().isPositive())) {
                totalSystemMoney = totalSystemMoney.add(w.getCurrentBalance());
            }
        }
        BigDecimal totalBalance = totalSystemMoney.getAmount();

        List<WalletResponseDTO> walletDtos = new ArrayList<>(filteredWallets.size());
        for (Wallet w : filteredWallets) {
            walletDtos.add(new WalletResponseDTO(
                    w.getId(),
                    w.getName(),
                    w.getCurrentBalance().getAmount(),
                    w.getCurrentBalance().getCurrency(),
                    w.isDefaultWallet(),
                    w.getStatus().name(),
                    w.getNote(),
                    w.getCreatedAt()
            ));
        }

        Map<UUID, BigDecimal> rawShares = new HashMap<>();
        boolean isTotalZero = totalBalance.compareTo(BigDecimal.ZERO) == 0;

        for (Wallet w : filteredWallets) {
            if (w.getStatus() == EntityStatus.ARCHIVED && w.getCurrentBalance().isZero()) {
                continue;
            }

            if (isTotalZero) {
                rawShares.put(w.getId(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN));
            } else {
                BigDecimal intermediate = w.getCurrentBalance().getAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalBalance, 4, RoundingMode.HALF_EVEN);
                BigDecimal finalShare = intermediate.setScale(2, RoundingMode.HALF_EVEN);
                rawShares.put(w.getId(), finalShare);
            }
        }

        Map<UUID, BigDecimal> sortedShares = rawShares.entrySet().stream()
                .sorted(Map.Entry.<UUID, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return new WalletsSummaryDTO(walletDtos, totalBalance, totalSystemMoney.getCurrency(), sortedShares);
    }
    // endregion
}
