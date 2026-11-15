package com.pfms.domain.model.wallet;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.common.EntityName;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.Note;
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
    private final UUID id;
    private EntityName name;
    private Note note;
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
        this(UUID.randomUUID(), "Ví chính", "", false,
                LocalDateTime.now(), new ActiveWalletState(), initialBalance);
    }

    public Wallet(WalletState initialState, Money initialBalance) {
        this(UUID.randomUUID(), "Ví chính", "", false,
                LocalDateTime.now(), initialState, initialBalance);
    }

    public Wallet(String name, String note, boolean defaultWallet) {
        this(UUID.randomUUID(), name, note, defaultWallet,
                LocalDateTime.now(), new ActiveWalletState(), Money.ZERO);
    }

    public Wallet(UUID id, String name, String note, boolean defaultWallet) {
        this(id, name, note, defaultWallet,
                LocalDateTime.now(), new ActiveWalletState(), Money.ZERO);
    }

    public Wallet(
            UUID id, String name, String note, boolean defaultWallet,
            LocalDateTime createdAt, WalletState state, Money currentBalance) {

        this(id, name, note, defaultWallet, createdAt, state, currentBalance, false);
    }

    public Wallet(
            String id, String name, String note, boolean defaultWallet,
            LocalDateTime createdAt, WalletState state, Money currentBalance) {

        this(parseId(id), name, note, defaultWallet, createdAt, state, currentBalance, false);
    }

    private Wallet(
            UUID id, String name, String note, boolean defaultWallet,
            LocalDateTime createdAt, WalletState state, Money currentBalance,
            boolean reconstituting) {

        this.id = Objects.requireNonNull(id, "Mã ví không được để null");
        this.name = reconstituting ? EntityName.reconstitute(name) : EntityName.of(name, "Tên ví");
        this.note = Note.of(note, "Ghi chú ví");
        this.defaultWallet = defaultWallet;
        this.createdAt = Objects.requireNonNull(createdAt, "Thời điểm tạo không được để null");
        this.state = Objects.requireNonNull(state, "Trạng thái khởi tạo không được để null");
        this.currentBalance = Objects.requireNonNull(currentBalance, "Số dư khởi tạo không được để null");
        if (!reconstituting && currentBalance.isNegative()) {
            throw new BusinessException(
                    ErrorCode.ERR_WAL_001,
                    "Số dư khả dụng của ví không được nhỏ hơn 0.");
        }
    }

    public static Wallet reconstitute(
            UUID id, String name, String note, boolean defaultWallet,
            LocalDateTime createdAt, WalletState state, Money currentBalance) {

        return new Wallet(id, name, note, defaultWallet, createdAt, state, currentBalance, true);
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
        this.name = EntityName.of(newName, "Tên ví");
    }

    public void updateNote(String newNote) {
        this.note = Note.of(newNote, "Ghi chú ví");
    }

    public void updateInfo(String newName, String newNote) {
        this.name = EntityName.of(newName, "Tên ví");
        this.note = Note.of(newNote, "Ghi chú ví");
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
    public UUID getId() {
        return this.id;
    }

    public UUID getWalletId() {
        return this.id;
    }

    public String getWalletIdAsString() {
        return this.id.toString();
    }

    public String getName() {
        return this.name.getValue();
    }

    public EntityName getEntityName() {
        return this.name;
    }

    public String getNote() {
        return this.note.getValue();
    }

    public Note getNoteValue() {
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

    public EntityStatus getStatus() {
        return this.state.getStatus();
    }

    public boolean isActive() {
        return this.state.getStatus() == EntityStatus.ACTIVE;
    }

    public boolean isArchived() {
        return this.state.getStatus() == EntityStatus.ARCHIVED;
    }

    public boolean isDeleted() {
        return this.state.getStatus() == EntityStatus.DELETED;
    }

    public Money getCurrentBalance() {
        return this.currentBalance;
    }

    public boolean hasSufficientBalance(Money amount) {
        Objects.requireNonNull(amount, "Số tiền kiểm tra không được để null");
        return this.currentBalance.isGreaterThanOrEqual(amount);
    }

    public Wallet copy() {
        return Wallet.reconstitute(
                this.id,
                this.name.getValue(),
                this.note.getValue(),
                this.defaultWallet,
                this.createdAt,
                this.state,
                this.currentBalance);
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

    private static void validatePositiveAmount(Money amount) {
        Objects.requireNonNull(amount, "Số tiền không được để null");
        if (!amount.isPositive()) {
            throw new BusinessException(ErrorCode.ERR_FIN_001, "Số tiền giao dịch phải là số dương lớn hơn 0.");
        }
    }

    private static UUID parseId(String id) {
        Objects.requireNonNull(id, "Mã ví không được để null");
        try {
            return UUID.fromString(id.trim());
        } catch (IllegalArgumentException ex) {
            return UUID.nameUUIDFromBytes(id.trim().getBytes(java.nio.charset.StandardCharsets.UTF_8));
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
        return String.format("Wallet[id=%s, name='%s', balance=%s, status=%s]",
                this.id, this.name, this.currentBalance.formatDisplay(), getStatus().name());
    }
    // endregion
}
