package com.pfms.domain.model.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Đối tượng đóng gói toàn bộ dữ liệu tài chính phục vụ tuần tự hóa sao lưu và phục hồi hệ thống.
 */
public final class BackupPayload {

    // region State
    private final List<Wallet> wallets;
    private final List<NeedGroup> needGroups;
    private final List<Category> categories;
    private final List<Transaction> transactions;
    private final List<BalanceAdjustmentRecord> adjustments;
    // endregion

    // region Constructors
    public BackupPayload(
            List<Wallet> wallets,
            List<NeedGroup> needGroups,
            List<Category> categories,
            List<Transaction> transactions,
            List<BalanceAdjustmentRecord> adjustments) {

        this.wallets = (wallets == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(wallets));
        this.needGroups = (needGroups == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(needGroups));
        this.categories = (categories == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(categories));
        this.transactions = (transactions == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(transactions));
        this.adjustments = (adjustments == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(adjustments));
    }
    // endregion

    // region Factory
    public static BackupPayload empty() {
        return new BackupPayload(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
    }
    // endregion

    // region Getters
    public List<Wallet> getWallets() {
        return this.wallets;
    }

    public List<NeedGroup> getNeedGroups() {
        return this.needGroups;
    }

    public List<Category> getCategories() {
        return this.categories;
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    public List<BalanceAdjustmentRecord> getAdjustments() {
        return this.adjustments;
    }

    public int getTotalRecordCount() {
        return this.wallets.size()
                + this.needGroups.size()
                + this.categories.size()
                + this.transactions.size()
                + this.adjustments.size();
    }

    public boolean isEmpty() {
        return getTotalRecordCount() == 0;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BackupPayload that)) {
            return false;
        }
        return Objects.equals(this.wallets, that.wallets)
                && Objects.equals(this.needGroups, that.needGroups)
                && Objects.equals(this.categories, that.categories)
                && Objects.equals(this.transactions, that.transactions)
                && Objects.equals(this.adjustments, that.adjustments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.wallets, this.needGroups, this.categories, this.transactions, this.adjustments);
    }

    @Override
    public String toString() {
        return String.format(
                "BackupPayload[wallets=%d, needGroups=%d, categories=%d, transactions=%d, adjustments=%d, totalRecords=%d]",
                this.wallets.size(),
                this.needGroups.size(),
                this.categories.size(),
                this.transactions.size(),
                this.adjustments.size(),
                getTotalRecordCount());
    }
    // endregion
}
