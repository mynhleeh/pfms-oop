package com.pfms.domain.model.wallet;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Thực thể miền Ví tiền (Wallet) đại diện cho một nguồn tiền trong hệ thống
 * PFMS. Đóng vai trò Context trong mẫu thiết kế GoF State và Aggregate Root
 * quản lý số dư khả dụng.
 */
public class Wallet {

    // region State
    private final String id;
    private String name;
    private String note;
    private final boolean defaultWallet;
    private final LocalDateTime createdAt;
    private WalletState state;
    private Money currentBalance;
    // endregion

    // region Constructors
    public Wallet() {
        this("Ví chính");
    }

    public Wallet(String name) {
        this(name, "");
    }

    public Wallet(String name, String note) {
        this(name, note, false);
    }

    public Wallet(Money initialBalance) {
        this(UUID.randomUUID().toString(), "Ví chính", "", false,
                LocalDateTime.now(), new ActiveWalletState(), initialBalance);
    }

    public Wallet(WalletState initialState, Money initialBalance) {
        this(UUID.randomUUID().toString(), "Ví chính", "", false,
                LocalDateTime.now(), initialState, initialBalance);
    }

    public Wallet(String name, String note, boolean defaultWallet) {
        this(UUID.randomUUID().toString(), name, note, defaultWallet,
                LocalDateTime.now(), new ActiveWalletState(), Money.ZERO);
    }

    public Wallet(
            String id, String name, String note, boolean defaultWallet,
            LocalDateTime createdAt, WalletState state, Money currentBalance) {

        this.id = Objects.requireNonNull(id, "Mã ví không được để null");
        this.name = validateAndNormalizeName(name);
        this.note = normalizeNote(note);
        this.defaultWallet = defaultWallet;
        this.createdAt = Objects.requireNonNull(createdAt, "Thời điểm tạo không được để null");
        this.state = Objects.requireNonNull(state, "Trạng thái khởi tạo không được để null");
        this.currentBalance = Objects.requireNonNull(currentBalance, "Số dư khởi tạo không được để null");
    }
    // endregion

    // region Commands
    public void credit(Money amount) {
        validatePositiveAmount(amount);
        this.state.credit(this, amount);
    }

    public void debit(Money amount) {
        validatePositiveAmount(amount);
        this.state.debit(this, amount);
    }

    public void archive() {
        if (this.defaultWallet) {
            throw new IllegalStateException("Không thể lưu trữ ví mặc định.");
        }
        this.state.archive(this);
    }

    public void activate() {
        this.state.activate(this);
    }

    public void delete() {
        if (this.defaultWallet) {
            throw new IllegalStateException("Không thể xóa ví mặc định.");
        }
        this.state.delete(this);
    }

    public void rename(String newName) {
        this.name = validateAndNormalizeName(newName);
    }

    public void updateNote(String newNote) {
        this.note = normalizeNote(newNote);
    }

    public void updateInfo(String newName, String newNote) {
        this.name = validateAndNormalizeName(newName);
        this.note = normalizeNote(newNote);
    }

    public BalanceAdjustmentRecord adjustBalance(Money actualBalance, TransactionDate adjustedAt, String reason) {
        if (!isActive()) {
            throw new BusinessException(ErrorCode.ERR_WAL_001, "Ví thanh toán không ở trạng thái hoạt động.");
        }
        BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                UUID.randomUUID(), this.id, this.currentBalance, actualBalance, adjustedAt, reason);
        this.currentBalance = actualBalance;
        return record;
    }
    // endregion

    // region Queries
    public String getId() {
        return this.id;
    }

    public String getWalletId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getNote() {
        return this.note;
    }

    public boolean isDefaultWallet() {
        return this.defaultWallet;
    }

    public boolean isDefault() {
        return this.defaultWallet;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public WalletState getState() {
        return this.state;
    }

    public WalletStatus getStatus() {
        return this.state.getStatus();
    }

    public boolean isActive() {
        return this.state.getStatus() == WalletStatus.ACTIVE;
    }

    public Money getCurrentBalance() {
        return this.currentBalance;
    }

    public boolean hasSufficientBalance(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Số tiền kiểm tra không được để null");
        }
        return this.currentBalance.isGreaterThanOrEqual(amount);
    }
    // endregion

    // region Helpers
    void setState(WalletState state) {
        this.state = Objects.requireNonNull(state, "Trạng thái mới không được để null");
    }

    void applyCredit(Money amount) {
        Objects.requireNonNull(amount, "Số tiền nạp không được để null");
        this.currentBalance = this.currentBalance.add(amount);
    }

    void applyDebit(Money amount) {
        Objects.requireNonNull(amount, "Số tiền rút không được để null");
        if (!hasSufficientBalance(amount)) {
            throw new InsufficientBalanceException(this.id, this.currentBalance, amount);
        }
        this.currentBalance = this.currentBalance.subtract(amount);
    }

    private static String validateAndNormalizeName(String rawName) {
        if (rawName == null) {
            throw new BusinessException(ErrorCode.ERR_NAM_001, "Tên ví không được để null.");
        }
        String trimmed = rawName.trim();
        if (trimmed.isEmpty() || trimmed.length() > 50) {
            throw new BusinessException(ErrorCode.ERR_NAM_001,
                    "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu.");
        }
        return trimmed;
    }

    private static String normalizeNote(String rawNote) {
        if (rawNote == null) {
            return "";
        }
        String trimmed = rawNote.trim();
        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("Ghi chú ví không được vượt quá 255 ký tự.");
        }
        return trimmed;
    }

    private static void validatePositiveAmount(Money amount) {
        Objects.requireNonNull(amount, "Số tiền không được để null");
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Số tiền giao dịch phải là số dương lớn hơn 0.");
        }
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Wallet wallet = (Wallet) o;
        return Objects.equals(this.id, wallet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        String statusLabel;
        try {
            statusLabel = getStatus().name();
        } catch (IllegalStateException e) {
            statusLabel = "DELETED";
        }
        return String.format("Wallet[id=%s, name='%s', balance=%s, status=%s]",
                this.id, this.name, this.currentBalance.formatDisplay(), statusLabel);
    }
    // endregion
}
