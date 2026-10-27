# 4. Hợp đồng Thao tác Hệ thống

<!-- PLACEHOLDER: Tổng quan về Hợp đồng Thao tác Hệ thống (Operation Contracts) theo phương pháp Craig Larman | Nguồn tham chiếu: Larman Chapter 11, SRS Mục 4 và Mục 6 | Tiêu chí hoàn thành: Giải thích vai trò của Hợp đồng thao tác trong việc đặc tả biến đổi trạng thái của mô hình khái niệm mà không rò rỉ mã nguồn -->

## 4.1 Phương pháp Luận và Cấu trúc Hợp đồng Chuẩn Craig Larman

<!-- PLACEHOLDER: Trình bày cấu trúc chuẩn 4 phần của một Hợp đồng Thao tác (Operation, Cross References, Preconditions, Postconditions) | Nguồn tham chiếu: Larman Chapter 11 | Tiêu chí hoàn thành: Phân định rõ 4 loại biến đổi trong Postconditions: Tạo mới thể hiện, Xóa thể hiện, Cập nhật thuộc tính, Hình thành/Phá vỡ liên kết ngữ nghĩa -->

## 4.2 Hợp đồng Thao tác: createWallet

<!-- PLACEHOLDER: Hợp đồng chi tiết cho thao tác createWallet(walletName, note) | Nguồn tham chiếu: UC-WAL-01, FR-WAL-01, BR-NAM-01, BR-FIN-02 | Tiêu chí hoàn thành: Đầy đủ Preconditions (tên không trùng) và Postconditions (thể hiện Wallet mới được tạo, số dư ban đầu 0.00, trạng thái ACTIVE) -->

## 4.3 Hợp đồng Thao tác: recordIncomeExpense

<!-- PLACEHOLDER: Hợp đồng chi tiết cho thao tác recordIncomeExpense(walletId, categoryId, amount, date, type, note, feeAmount, feeCategoryId) | Nguồn tham chiếu: UC-TXN-01, FR-TXN-01, FR-TXN-02, BR-FIN-02, BR-FIN-04 | Tiêu chí hoàn thành: Đầy đủ Preconditions (ví tồn tại, danh mục hợp lệ, số dư đủ nếu EXPENSE) và Postconditions (tạo IncomeExpenseTransaction, liên kết Ví và Category, cập nhật số dư ví, tạo TransactionFee nếu có) -->

## 4.4 Hợp đồng Thao tác: transferFunds

<!-- PLACEHOLDER: Hợp đồng chi tiết cho thao tác transferFunds(sourceWalletId, destinationWalletId, amount, date, note, feeAmount, feeCategoryId) | Nguồn tham chiếu: UC-TRF-01, FR-TRF-01, BR-FIN-02, BR-FIN-03 | Tiêu chí hoàn thành: Đầy đủ Preconditions (hai ví khác nhau, ví nguồn đủ tiền trả cả chuyển và phí) và Postconditions (tạo TransferTransaction, trừ số dư nguồn, cộng số dư đích, tạo và liên kết TransactionFee) -->

## 4.5 Hợp đồng Thao tác: adjustBalance

<!-- PLACEHOLDER: Hợp đồng chi tiết cho thao tác adjustBalance(walletId, actualBalance, reason) | Nguồn tham chiếu: UC-WAL-04, FR-WAL-05, BR-FIN-02 | Tiêu chí hoàn thành: Đầy đủ Preconditions (actualBalance >= 0) và Postconditions (tạo bản ghi BalanceAdjustmentRecord bất biến, cập nhật số dư ví bằng actualBalance) -->

## 4.6 Hợp đồng Thao tác: deleteCategory

<!-- PLACEHOLDER: Hợp đồng chi tiết cho thao tác deleteCategory(categoryId) | Nguồn tham chiếu: UC-CAT-04, FR-CAT-03, BR-FIN-04 | Tiêu chí hoàn thành: Đầy đủ Preconditions (danh mục tồn tại) và Postconditions (chuyển trạng thái sang SOFT_DELETED nếu đã có giao dịch, bảo toàn liên kết với các giao dịch cũ) -->

## 4.7 Hợp đồng Thao tác: updateTransaction

<!-- PLACEHOLDER: Hợp đồng chi tiết cho thao tác updateTransaction(transactionId, ...) | Nguồn tham chiếu: UC-TXN-03, FR-TXN-04, BR-FIN-02 | Tiêu chí hoàn thành: Đầy đủ Preconditions và Postconditions (hoàn nguyên tác động của giao dịch cũ, áp dụng thuộc tính mới, điều chỉnh lại số dư ví tương ứng) -->

## 4.8 Hợp đồng Thao tác: deleteTransaction

<!-- PLACEHOLDER: Hợp đồng chi tiết cho thao tác deleteTransaction(transactionId) | Nguồn tham chiếu: UC-TXN-04, FR-TXN-05, BR-FIN-02 | Tiêu chí hoàn thành: Đầy đủ Preconditions và Postconditions (hoàn nguyên số dư ví, xóa giao dịch và phá hủy TransactionFee gắn kèm) -->

## 4.9 Bảng Tổng hợp Trách nhiệm Biến đổi Trạng thái

<!-- PLACEHOLDER: Bảng tổng hợp đối chiếu giữa các System Operations và các biến đổi trạng thái trên 7 Thực thể miền | Nguồn tham chiếu: Checkpoint 2, Hợp đồng 4.2 - 4.8 | Tiêu chí hoàn thành: Bảng ma trận thể hiện thao tác nào tạo, cập nhật hoặc xóa thể hiện của từng thực thể -->
