package com.pfms.domain.model.transaction;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.Wallet;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Khởi tạo các loại giao dịch và phụ phí đi kèm trong hệ thống PFMS.
 */
public final class TransactionFactory {

    // region Constructors
    private TransactionFactory() {
        throw new UnsupportedOperationException("Lớp Factory tiện ích không được phép khởi tạo.");
    }
    // endregion

    // region Factories
    /**
     * Khởi tạo giao dịch thu chi mới với mã giao dịch sinh ngẫu nhiên.
     */
    public static IncomeExpenseTransaction createIncomeExpense(
            TransactionType transactionType,
            Wallet wallet,
            Category category,
            Money amount,
            TransactionDate transactionDate,
            String note,
            Money feeAmount,
            Category feeCategory) {

        return createIncomeExpense(
                UUID.randomUUID(),
                transactionType,
                wallet,
                category,
                amount,
                transactionDate,
                note,
                feeAmount,
                feeCategory);
    }

    /**
     * Khởi tạo giao dịch thu chi với mã định danh chỉ định trước kèm thông số phụ phí.
     */
    public static IncomeExpenseTransaction createIncomeExpense(
            UUID transactionId,
            TransactionType transactionType,
            Wallet wallet,
            Category category,
            Money amount,
            TransactionDate transactionDate,
            String note,
            Money feeAmount,
            Category feeCategory) {

        TransactionFee fee = createFeeInternal(transactionId, feeAmount, wallet, feeCategory);
        return createIncomeExpense(
                transactionId,
                transactionType,
                wallet,
                category,
                amount,
                transactionDate,
                note,
                fee);
    }

    /**
     * Khởi tạo giao dịch thu chi mới với thể hiện phụ phí cung cấp trực tiếp.
     */
    public static IncomeExpenseTransaction createIncomeExpense(
            UUID transactionId,
            TransactionType transactionType,
            Wallet wallet,
            Category category,
            Money amount,
            TransactionDate transactionDate,
            String note,
            TransactionFee fee) {

        return new IncomeExpenseTransaction(
                transactionId,
                transactionType,
                wallet,
                category,
                amount,
                transactionDate,
                note,
                fee);
    }

    /**
     * Khởi tạo giao dịch chuyển tiền nội bộ mới với mã sinh tự động.
     */
    public static TransferTransaction createTransfer(
            Wallet sourceWallet,
            Wallet destinationWallet,
            Money amount,
            TransactionDate transactionDate,
            String note,
            Money feeAmount,
            Category feeCategory) {

        return createTransfer(
                UUID.randomUUID(),
                sourceWallet,
                destinationWallet,
                amount,
                transactionDate,
                note,
                feeAmount,
                feeCategory);
    }

    /**
     * Khởi tạo giao dịch chuyển tiền nội bộ với mã định danh chỉ định trước kèm thông số phụ phí.
     */
    public static TransferTransaction createTransfer(
            UUID transactionId,
            Wallet sourceWallet,
            Wallet destinationWallet,
            Money amount,
            TransactionDate transactionDate,
            String note,
            Money feeAmount,
            Category feeCategory) {

        TransactionFee fee = createFeeInternal(transactionId, feeAmount, sourceWallet, feeCategory);
        return createTransfer(
                transactionId,
                sourceWallet,
                destinationWallet,
                amount,
                transactionDate,
                note,
                fee);
    }

    /**
     * Khởi tạo giao dịch chuyển tiền nội bộ mới với thể hiện phụ phí cung cấp trực tiếp.
     */
    public static TransferTransaction createTransfer(
            UUID transactionId,
            Wallet sourceWallet,
            Wallet destinationWallet,
            Money amount,
            TransactionDate transactionDate,
            String note,
            TransactionFee fee) {

        return new TransferTransaction(
                transactionId,
                sourceWallet,
                destinationWallet,
                amount,
                transactionDate,
                note,
                fee);
    }

    /**
     * Khởi tạo chi phí giao dịch độc lập với mã feeId chỉ định trước.
     */
    public static TransactionFee createFee(
            UUID feeId,
            UUID parentTransactionId,
            Money amount,
            Wallet wallet,
            Category category) {

        return new TransactionFee(feeId, parentTransactionId, amount, wallet, category);
    }

    /**
     * Khởi tạo chi phí giao dịch tự động sinh mã feeId.
     */
    public static TransactionFee createFee(
            UUID parentTransactionId,
            Money amount,
            Wallet wallet,
            Category category) {

        return createFee(UUID.randomUUID(), parentTransactionId, amount, wallet, category);
    }

    /**
     * Reconstitutes a persisted income/expense transaction with its original createdAt
     * and historical wallet/category references.
     */
    public static IncomeExpenseTransaction reconstituteIncomeExpense(
            UUID transactionId,
            TransactionType transactionType,
            Wallet wallet,
            Category category,
            Money amount,
            TransactionDate transactionDate,
            String note,
            TransactionFee fee,
            LocalDateTime createdAt) {

        return IncomeExpenseTransaction.reconstitute(
                transactionId,
                transactionType,
                wallet,
                category,
                amount,
                transactionDate,
                note,
                fee,
                createdAt);
    }

    /**
     * Reconstitutes a persisted transfer transaction with its original createdAt
     * and historical wallet references.
     */
    public static TransferTransaction reconstituteTransfer(
            UUID transactionId,
            Wallet sourceWallet,
            Wallet destinationWallet,
            Money amount,
            TransactionDate transactionDate,
            String note,
            TransactionFee fee,
            LocalDateTime createdAt) {

        return TransferTransaction.reconstitute(
                transactionId,
                sourceWallet,
                destinationWallet,
                amount,
                transactionDate,
                note,
                fee,
                createdAt);
    }

    /**
     * Reconstitutes a persisted transaction fee allowing archived wallet or deleted fee category.
     */
    public static TransactionFee reconstituteFee(
            UUID feeId,
            UUID parentTransactionId,
            Money amount,
            Wallet wallet,
            Category category) {

        return TransactionFee.reconstitute(feeId, parentTransactionId, amount, wallet, category);
    }
    // endregion

    // region Helpers
    /**
     * Tự động khởi tạo TransactionFee hợp thành nếu phí phát sinh hợp lệ (khác null và khác 0 VNĐ).
     */
    private static TransactionFee createFeeInternal(
            UUID parentTransactionId,
            Money feeAmount,
            Wallet deductionWallet,
            Category feeCategory) {

        if (feeAmount == null || feeAmount.compareTo(Money.ZERO) == 0) {
            return null;
        }

        return createFee(
                UUID.randomUUID(),
                parentTransactionId,
                feeAmount,
                deductionWallet,
                feeCategory);
    }
    // endregion
}
