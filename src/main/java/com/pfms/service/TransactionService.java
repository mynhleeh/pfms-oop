package com.pfms.service;

import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.RecordTransactionRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.transaction.UpdateTransactionRequestDTO;

import java.util.UUID;

/**
 * Dịch vụ quản lý giao dịch thu chi phát sinh trên ví.
 * Điều phối ghi nhận, chỉnh sửa, xóa và truy vấn/lọc lịch sử giao dịch kèm phụ phí.
 */
public interface TransactionService {

    // region Commands
    TransactionResponseDTO recordIncomeExpense(RecordTransactionRequestDTO request);

    TransactionResponseDTO updateTransaction(UpdateTransactionRequestDTO request);

    void deleteTransaction(UUID transactionId);
    // endregion

    // region Queries
    TransactionListResponseDTO filterTransactions(FilterTransactionsRequestDTO request);
    // endregion
}

