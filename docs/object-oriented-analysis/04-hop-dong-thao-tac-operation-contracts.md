# 4. Đặc tả Biến đổi Dữ liệu Thao tác Hệ thống

Tài liệu này mô tả chi tiết các thay đổi về mặt dữ liệu trong hệ thống mỗi khi người dùng thực hiện một thao tác.

Với mỗi thao tác làm thay đổi dữ liệu (như tạo ví, ghi nhận thu chi, chuyển tiền hay xóa danh mục), tài liệu làm rõ hai nội dung chính:
- Điều kiện hợp lệ để hệ thống chấp nhận thực hiện thao tác.
- Kết quả biến đổi của các đối tượng dữ liệu sau khi thực hiện xong, bao gồm: đối tượng nào được tạo mới hoặc bị xóa bỏ, những thông tin nào bị thay đổi giá trị, và các mối liên kết nào giữa các đối tượng được thiết lập hoặc gỡ bỏ.

Các thao tác chỉ đọc dữ liệu để hiển thị hoặc xuất báo cáo không làm thay đổi trạng thái dữ liệu trong hệ thống, do đó không nằm trong phạm vi mô tả của tài liệu này.

## 4.1 Cấu trúc Đặc tả Thao tác

Mỗi thao tác làm thay đổi dữ liệu trong hệ thống được đặc tả thống nhất theo 4 thành phần quy chuẩn dưới đây:

| Thành phần | Ý nghĩa | Nội dung đặc tả |
| :--- | :--- | :--- |
| **Thao tác** | Tên gọi và dữ liệu đầu vào | Tên thao tác nghiệp vụ kèm danh sách các tham số truyền vào từ giao diện người dùng. |
| **Quy chiếu chéo** | Căn cứ nghiệp vụ liên kết | Các mã định danh liên kết trực tiếp đến tài liệu yêu cầu, bao gồm ca sử dụng (`UC-*`), yêu cầu chức năng (`FR-*`), quy tắc tài chính (`BR-*`) và mã lỗi (`ERR-*`). |
| **Tiền điều kiện** | Điều kiện bắt buộc trước khi thực hiện | Các ràng buộc và trạng thái dữ liệu bắt buộc phải thỏa mãn để hệ thống chấp nhận xử lý yêu cầu. |
| **Hậu điều kiện** | Trạng thái dữ liệu sau khi hoàn thành | Trạng thái mới của các đối tượng dữ liệu sau khi thao tác thực hiện thành công, bao gồm 4 biến đổi chính:<br/>- *Tạo mới thể hiện:* Các đối tượng dữ liệu mới được sinh ra.<br/>- *Xóa thể hiện:* Các đối tượng dữ liệu bị hủy bỏ hoặc chuyển trạng thái kết thúc.<br/>- *Biến đổi thuộc tính:* Các giá trị số dư, tên gọi hoặc trạng thái bị thay đổi.<br/>- *Hình thành hoặc phá vỡ liên kết:* Các mối quan hệ ngữ nghĩa giữa các đối tượng được kết nối hoặc gỡ bỏ. |

## 4.2 Các thao tác Quản lý Ví

### 4.2.1 Thao tác Tạo ví mới

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác createWallet(walletName, note) | Nguồn tham chiếu: UC-WAL-01, FR-WAL-01, BR-NAM-01, BR-FIN-02 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện kiểm tra tên ví và hậu điều kiện tạo thể hiện Wallet mới với số dư ban đầu 0.00 -->

### 4.2.2 Thao tác Cập nhật thông tin ví

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác updateWallet(walletId, walletName, note) | Nguồn tham chiếu: UC-WAL-03, FR-WAL-03, BR-NAM-01 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện kiểm tra tên không trùng với các ví khác và hậu điều kiện cập nhật thuộc tính tên, ghi chú của ví -->

### 4.2.3 Thao tác Điều chỉnh số dư ví thực tế

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác adjustBalance(walletId, actualBalance, reason) | Nguồn tham chiếu: UC-WAL-04, FR-WAL-05, BR-FIN-02 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện số dư kiểm kê không âm và hậu điều kiện tạo thể hiện BalanceAdjustmentRecord bất biến, cập nhật số dư ví -->

## 4.3 Các thao tác Ghi nhận Thu chi

### 4.3.1 Thao tác Ghi nhận giao dịch thu chi

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId) | Nguồn tham chiếu: UC-TXN-01, FR-TXN-01, FR-TXN-02, BR-FIN-02, BR-FIN-04 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện kiểm tra số dư và hậu điều kiện tạo thể hiện IncomeExpenseTransaction, cập nhật số dư ví, tạo thể hiện TransactionFee nếu có -->

### 4.3.2 Thao tác Chỉnh sửa giao dịch thu chi

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác updateTransaction(transactionId, walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId) | Nguồn tham chiếu: UC-TXN-03, FR-TXN-04, BR-FIN-02, BR-FIN-04 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện kiểm tra an toàn số dư sau hoàn nguyên và hậu điều kiện điều chỉnh thuộc tính, cập nhật lại số dư ví tương ứng -->

### 4.3.3 Thao tác Xóa giao dịch thu chi

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác deleteTransaction(transactionId) | Nguồn tham chiếu: UC-TXN-04, FR-TXN-05, BR-FIN-02 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện kiểm tra giao dịch tồn tại và hậu điều kiện hoàn nguyên số dư ví, xóa thể hiện giao dịch và xóa cascade thể hiện TransactionFee đi kèm -->

## 4.4 Các thao tác Chuyển tiền Nội bộ

### 4.4.1 Thao tác Chuyển tiền giữa hai ví

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác transferFunds(sourceWalletId, destinationWalletId, amount, transferDate, note, feeAmount, feeCategoryId) | Nguồn tham chiếu: UC-TRF-01, FR-TRF-01, BR-FIN-02, BR-FIN-03 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện hai ví phân biệt và ví nguồn đủ số dư chi trả chuyển tiền cùng phí; hậu điều kiện tạo TransferTransaction, trừ ví nguồn, cộng ví đích, tạo TransactionFee -->

### 4.4.2 Thao tác Xóa giao dịch chuyển tiền

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác deleteTransfer(transferId) | Nguồn tham chiếu: UC-TRF-03, FR-TRF-03, BR-FIN-02, BR-FIN-03 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện kiểm tra ví nhận đủ tiền hoàn trả để không bị âm; hậu điều kiện hoàn nguyên đồng thời số dư hai ví, xóa thể hiện TransferTransaction và TransactionFee -->

## 4.5 Các thao tác Danh mục Phân loại

### 4.5.1 Thao tác Thêm danh mục phân loại mới

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác addCategory(categoryName, parentCategoryId) | Nguồn tham chiếu: UC-CAT-01, FR-CAT-01, BR-NAM-01, BR-FIN-04 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện tên danh mục không trùng trong cùng nhóm cha và hậu điều kiện tạo thể hiện Category mới gắn kết với nhóm cha -->

### 4.5.2 Thao tác Đổi tên danh mục

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác renameCategory(categoryId, newName) | Nguồn tham chiếu: UC-CAT-02, FR-CAT-02, BR-NAM-01 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện tên mới không trùng lặp và hậu điều kiện cập nhật thuộc tính tên của thể hiện Category -->

### 4.5.3 Thao tác Khôi phục danh mục đã xóa

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác restoreCategory(categoryId) | Nguồn tham chiếu: UC-CAT-03, FR-CAT-04, BR-FIN-04 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện danh mục đang ở trạng thái xóa mềm và nhóm cha đang hoạt động; hậu điều kiện cập nhật thuộc tính trạng thái thành ACTIVE -->

### 4.5.4 Thao tác Xóa danh mục phân loại

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác deleteCategory(categoryId) | Nguồn tham chiếu: UC-CAT-04, FR-CAT-03, BR-FIN-04 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện kiểm tra danh mục tồn tại; hậu điều kiện phân nhánh xóa hoàn toàn nếu chưa có giao dịch hoặc xóa mềm chuyển trạng thái sang ARCHIVED/INACTIVE nếu đã phát sinh giao dịch -->

## 4.6 Thao tác Lưu trữ và Phục hồi Dữ liệu

### 4.6.1 Thao tác Phục hồi dữ liệu từ bản sao lưu

<!-- PLACEHOLDER: Đặc tả chi tiết cho thao tác restoreBackupData(backupFilePath) | Nguồn tham chiếu: UC-DAT-02, FR-DAT-02, BR-FIN-02, BR-FIN-03 | Tiêu chí hoàn thành: Đầy đủ tiền điều kiện tệp sao lưu nguyên vẹn, hợp lệ cấu trúc; hậu điều kiện xóa toàn bộ các thể hiện hiện tại trong bộ nhớ và tái tạo toàn bộ các thể hiện thực thể từ tệp sao lưu -->

## 4.7 Bảng Tổng hợp Biến đổi Dữ liệu trên các Thực thể Miền

<!-- PLACEHOLDER: Ma trận tổng hợp đối chiếu 13 thao tác biến đổi dữ liệu với 7 thực thể miền | Nguồn tham chiếu: Checkpoint 2, Mục 4.2 đến 4.6 | Tiêu chí hoàn thành: Bảng ma trận thể hiện rõ loại biến đổi (Tạo mới, Cập nhật, Xóa/Xóa mềm, Liên kết) của từng thao tác lên từng thực thể miền -->
