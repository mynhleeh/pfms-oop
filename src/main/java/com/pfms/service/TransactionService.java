package com.pfms.service;

import com.pfms.presentation.dto.transaction.DeleteTransactionRequestDTO;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.RecordTransactionRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.transaction.UpdateTransactionRequestDTO;

import java.util.UUID;

/**
 * Giao diện dịch vụ ứng dụng điều phối các ca sử dụng ghi nhận, tra cứu, chỉnh sửa và xóa giao dịch thu chi.
 * Quản lý vòng đời giao dịch thu chi, hạch toán biến động số dư ví tức thời và xử lý phụ phí đi kèm.
 */
public interface TransactionService {

    // region Commands
    TransactionResponseDTO recordIncomeExpense(RecordTransactionRequestDTO request);

    TransactionResponseDTO updateTransaction(UpdateTransactionRequestDTO request);

    void deleteTransaction(UUID transactionId);

    default void deleteTransaction(DeleteTransactionRequestDTO request) {
        deleteTransaction(request != null ? request.getTransactionId() : null);
    }
    // endregion

    // region Queries
    TransactionListResponseDTO filterTransactions(FilterTransactionsRequestDTO request);
    // endregion
}

