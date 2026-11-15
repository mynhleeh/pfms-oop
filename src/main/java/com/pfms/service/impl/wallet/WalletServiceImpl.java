package com.pfms.service.impl.wallet;

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
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.service.WalletService;
import com.pfms.service.impl.common.EntityLookupSupport;
import com.pfms.service.impl.common.RollbackSupport;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Cài đặt dịch vụ quản lý ví tiền ({@link WalletService}).
 * Điều phối vòng đời ví, duy trì ví mặc định hệ thống và ghi nhận kiểm kê điều chỉnh số dư.
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
        Wallet defaultWallet = new Wallet(UUID.randomUUID(), "Tiền mặt", "Ví mặc định hệ thống", true);
        walletRepository.save(defaultWallet);
    }

    @Override
    public WalletResponseDTO createWallet(CreateWalletRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu tạo ví không được để null");
        Wallet wallet = new Wallet(UUID.randomUUID(), request.getName(), request.getNote(), false);

        if (walletRepository.existsByName(wallet.getName())) {
            throw new DuplicateEntityException(ErrorCode.ERR_NAM_002, "Ví", wallet.getName());
        }
        return WalletSummaryCalculator.toResponseDTO(walletRepository.save(wallet));
    }

    @Override
    public WalletResponseDTO updateWallet(UpdateWalletRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu cập nhật ví không được để null");
        Objects.requireNonNull(request.getWalletId(), "Mã ví không được để null");

        String validName = EntityName.of(request.getName(), "Tên ví").getValue();
        Note.of(request.getNote(), "Ghi chú ví");

        Wallet wallet = EntityLookupSupport.findEntityOrThrow(
                walletRepository, request.getWalletId(), EntityLookupSupport.LABEL_WALLET);
        if (wallet.getStatus() == EntityStatus.DELETED) {
            throw new BusinessException(ErrorCode.ERR_WAL_001, "Không thể cập nhật thông tin ví đã bị xóa.");
        }

        if (!wallet.getName().equalsIgnoreCase(validName) && walletRepository.existsByName(validName)) {
            throw new DuplicateEntityException(ErrorCode.ERR_NAM_002, "Ví", validName);
        }

        wallet.updateInfo(validName, request.getNote());
        return WalletSummaryCalculator.toResponseDTO(walletRepository.save(wallet));
    }

    @Override
    public WalletResponseDTO archiveWallet(UUID walletId) {
        return changeWalletState(walletId, Wallet::archive);
    }

    @Override
    public WalletResponseDTO activateWallet(UUID walletId) {
        return changeWalletState(walletId, Wallet::activate);
    }

    @Override
    public void deleteWallet(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví không được để null");
        Wallet wallet = EntityLookupSupport.findEntityOrThrow(
                walletRepository, walletId, EntityLookupSupport.LABEL_WALLET);

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
        if (actualBalance.isNegative()) {
            throw new BusinessException(ErrorCode.ERR_WAL_001,
                    "Số dư thực tế quan sát không được nhỏ hơn 0.");
        }
        TransactionDate adjustedAt = TransactionDate.ofOrToday(request.getAdjustedAt());

        Wallet wallet = EntityLookupSupport.findEntityOrThrow(
                walletRepository, request.getWalletId(), EntityLookupSupport.LABEL_WALLET);
        Wallet previousSnapshot = wallet.copy();
        BalanceAdjustmentRecord record = wallet.adjustBalance(actualBalance, adjustedAt, request.getReason());

        walletRepository.save(wallet);
        try {
            balanceAdjustmentRepository.save(record);
        } catch (RuntimeException persistenceException) {
            if (persistenceException instanceof StorageException storageException) {
                RollbackSupport.runRollbackStep(() -> walletRepository.save(previousSnapshot), storageException);
            } else {
                try {
                    walletRepository.save(previousSnapshot);
                } catch (RuntimeException rollbackException) {
                    if (rollbackException != persistenceException) {
                        persistenceException.addSuppressed(rollbackException);
                    }
                }
            }
            throw persistenceException;
        }
        return toAdjustmentResponseDTO(record);
    }
    // endregion

    // region Queries
    @Override
    public WalletsSummaryResponseDTO getWalletsSummary(EntityStatus statusFilter) {
        return WalletSummaryCalculator.buildWalletsSummary(walletRepository, statusFilter);
    }

    @Override
    public WalletsSummaryResponseDTO getWalletsSummary(String statusFilter) {
        if (statusFilter == null || statusFilter.isBlank() || "ALL".equalsIgnoreCase(statusFilter.trim())) {
            return getWalletsSummary((EntityStatus) null);
        }
        try {
            EntityStatus status = EntityStatus.valueOf(statusFilter.trim().toUpperCase(java.util.Locale.ROOT));
            return getWalletsSummary(status);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Trạng thái ví không hợp lệ: " + statusFilter, ex);
        }
    }

    @Override
    public WalletsSummaryResponseDTO getWalletsSummary() {
        return getWalletsSummary(EntityStatus.ACTIVE);
    }
    // endregion

    // region Helpers
    private WalletResponseDTO changeWalletState(UUID walletId, Consumer<Wallet> stateTransition) {
        Objects.requireNonNull(walletId, "Mã ví không được để null");
        Wallet wallet = EntityLookupSupport.findEntityOrThrow(
                walletRepository, walletId, EntityLookupSupport.LABEL_WALLET);
        try {
            stateTransition.accept(wallet);
        } catch (IllegalStateException ex) {
            throw new BusinessException(ErrorCode.ERR_WAL_001, ex.getMessage());
        }
        return WalletSummaryCalculator.toResponseDTO(walletRepository.save(wallet));
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
                record.getCreatedAt());
    }
    // endregion
}
