# 7. Ma trận truy vết

Hệ thống ma trận truy vết hai chiều xác lập mối liên kết kiểm chứng giữa hồ sơ yêu cầu phần mềm (SRS) và mô hình phân tích hướng đối tượng (OOA), kết nối các yêu cầu chức năng, ca sử dụng và quy tắc nghiệp vụ với các lớp thực thể miền, đối tượng giá trị và thao tác hệ thống theo hai nguyên tắc:
- **Truy vết xuôi**: Xác nhận toàn bộ yêu cầu chức năng và quy tắc nghiệp vụ đều được hiện thực hóa đầy đủ trong mô hình phân tích, không bỏ sót ràng buộc nào.
- **Truy vết ngược**: Xác nhận mọi thực thể miền, đối tượng giá trị và thao tác hệ thống đều xuất phát từ nhu cầu thực tế, không có thành phần dư thừa hoặc mồ côi.

## 7.1 Truy vết yêu cầu chức năng

Bảng dưới đây đối chiếu trực tiếp từng yêu cầu chức năng và ca sử dụng theo 6 nhóm nghiệp vụ với các lớp thực thể miền, đối tượng giá trị cùng các thuộc tính và ràng buộc đảm nhiệm:

| Mã yêu cầu | Ca sử dụng | Lớp thực thể miền | Đối tượng giá trị liên quan | Thuộc tính và ràng buộc nghiệp vụ đảm nhiệm |
| :--- | :--- | :--- | :--- | :--- |
| **Nhóm Quản lý ví (`WAL`)** | | | | |
| `FR-WAL-01` | `UC-WAL-01` | `Wallet` | `Money` | Khởi tạo `walletId` (sinh tự động), `name` (1–50 ký tự, không trùng), `note`, `status` (`ACTIVE`), `createdAt`, `currentBalance = 0.00 VNĐ`. |
| `FR-WAL-02` | `UC-WAL-02` | `Wallet` | `Money` | Đọc toàn bộ thuộc tính (`walletId`, `name`, `status`, `note`, `createdAt`, `currentBalance`); tính tổng tài sản từ các ví `ACTIVE` và ví `ARCHIVED` có số dư dương. |
| `FR-WAL-03` | `UC-WAL-03` | `Wallet` | | Cập nhật `name`, `note`; chuyển đổi `status` giữa `ACTIVE` và `ARCHIVED`; cấm xóa hoặc lưu trữ ví "Tiền mặt" mặc định; chỉ xóa vĩnh viễn khi chưa phát sinh giao dịch. |
| `FR-WAL-04` | `UC-WAL-02` | `Wallet` | `Money`, `SpendingStructureShare` | Tính tỷ lệ phần trăm phân bổ số dư của từng ví trên tổng tài sản khả dụng theo quy tắc làm tròn ngân hàng. |
| `FR-WAL-05` | `UC-WAL-04` | `Wallet`, `BalanceAdjustmentRecord` | `Money`, `TransactionDate` | Đồng bộ `currentBalance` theo số dư kiểm kê; khởi tạo thể hiện bất biến `BalanceAdjustmentRecord` (`recordId`, `walletId`, `previousBalance`, `actualBalance`, `difference`, `reason`, `adjustedAt`). |
| **Nhóm Ghi nhận thu chi (`TXN`)** | | | | |
| `FR-TXN-01` | `UC-TXN-01` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` | Khởi tạo `IncomeExpenseTransaction` loại `EXPENSE`; kiểm tra số dư khả dụng; giảm `Wallet.currentBalance`; liên kết `Category` cấp 3; khởi tạo `TransactionFee` nếu có phí. |
| `FR-TXN-02` | `UC-TXN-01` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` | Khởi tạo `IncomeExpenseTransaction` loại `INCOME`; tăng `Wallet.currentBalance`; liên kết `Category` cấp 3 và `TransactionFee` (nếu có). |
| `FR-TXN-03` | `UC-TXN-02` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` | Đọc và lọc đa tiêu chí theo khoảng ngày (`TransactionDate`), ví (`walletId`), danh mục/nhóm (`categoryId`), loại giao dịch (`transactionType`). |
| `FR-TXN-04` | `UC-TXN-03` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `TransactionFee` | `Money`, `TransactionDate` | Hoàn nguyên số dư ví cũ và kiểm tra khả năng chi trả trên ví mới; cập nhật `amount`, `transactionDate`, `walletId`, `categoryId`, `note` và đồng bộ `TransactionFee`; giữ nguyên `transactionType`. |
| `FR-TXN-05` | `UC-TXN-04` | `IncomeExpenseTransaction`, `Wallet`, `TransactionFee` | `Money` | Hoàn nguyên số dư ví (kiểm tra không âm khi xóa khoản thu); xóa vĩnh viễn `IncomeExpenseTransaction` và `TransactionFee` liên kết. |
| **Nhóm Chuyển tiền nội bộ (`TRF`)** | | | | |
| `FR-TRF-01` | `UC-TRF-01` | `TransferTransaction`, `Wallet`, `TransactionFee` | `Money`, `TransactionDate` | Kiểm tra `sourceWalletId != destinationWalletId` và số dư ví nguồn $\ge amount + feeAmount$; trừ ví nguồn, cộng ví đích đồng thời; khởi tạo `TransferTransaction` và `TransactionFee` (nếu có). |
| `FR-TRF-02` | `UC-TRF-02`, `UC-TRF-03` | `TransferTransaction`, `Wallet`, `TransactionFee` | `Money`, `TransactionDate` | Truy vấn lịch sử chuyển tiền; khi xóa giao dịch, kiểm tra số dư ví đích $\ge amount$, hoàn nguyên đồng thời hai ví và xóa `TransactionFee` đi kèm. |
| **Nhóm Danh mục phân loại (`CAT`)** | | | | |
| `FR-CAT-01` | `UC-CAT-01` | `Category`, `NeedGroup` | `TransactionType` | Khởi tạo `Category` (`categoryId`, `name`, `needGroupId`, `transactionType`, `status = ACTIVE`); kiểm tra tên duy nhất trong cùng nhóm cha và duy trì phân cấp 3 cấp. |
| `FR-CAT-02` | `UC-CAT-02`, `UC-CAT-03` | `Category`, `NeedGroup` | `TransactionType` | Cập nhật `name`, `icon`, `color`; kiểm tra trùng tên sau khi `trim`; khôi phục mục từ trạng thái `DELETED` sang `ACTIVE`. |
| `FR-CAT-03` | `UC-CAT-03`, `UC-CAT-04` | `Category`, `NeedGroup`, `IncomeExpenseTransaction` | `TransactionType` | Chuyển `status` sang `DELETED` (xóa mềm) nếu đã có giao dịch tham chiếu; chỉ xóa vĩnh viễn khi chưa phát sinh giao dịch; cấm xóa `NeedGroup` hoạt động cuối cùng của mỗi loại thu chi. |
| **Nhóm Thống kê và báo cáo (`REP`)** | | | | |
| `FR-REP-01` | `UC-REP-01` | `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee` | `NetCashFlow`, `SavingsRate`, `Money`, `TransactionDate` | Kiểm tra tham số (`ERR-REP-001`, hỗ trợ mốc tương lai chỉ đọc); tổng hợp thu, chi và phí liên kết (chỉ đọc `TransferTransaction` để lấy ngày của phí chuyển khoản); tính dòng tiền thuần, tỷ lệ tiết kiệm (`0.00%` khi thu $\le 0$) và 3 mức bình quân chia cho 12 tháng. |
| `FR-REP-02` | `UC-REP-02` | `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `Category`, `NeedGroup` | `SpendingStructureShare`, `Money`, `TransactionType`, `TransactionDate` | Kiểm tra tham số (`ERR-REP-001`, chỉ đọc); phân bổ `EXPENSE` (gồm `TransactionFee`) hoặc `INCOME` theo `NeedGroup` và `Category` có phát sinh dương; giữ nhãn "(đã xóa)" cho mục `DELETED`; tính tỷ trọng và so sánh kỳ liền kề. |
| **Nhóm Lưu trữ và sao lưu (`DAT`)** | | | | |
| `FR-DAT-01` | `UC-DAT-01` | Toàn bộ 7 thực thể miền | `Money`, `TransactionDate`, `TransactionType` | Tuần tự hóa toàn bộ trạng thái của các thực thể miền ra tệp sao lưu JSON UTF-8 ở chế độ chỉ đọc. |
| `FR-DAT-02` | `UC-DAT-02` | Toàn bộ 7 thực thể miền | `Money`, `TransactionDate`, `TransactionType` | Kiểm định toàn vẹn 4 bước (`All-or-Nothing`) và phục hồi toàn bộ thực thể cùng mạng lưới liên kết từ tệp sao lưu. |
| `FR-DAT-03` | `UC-DAT-03` | `Wallet`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `BalanceAdjustmentRecord` | `Money` | Tái tính toán số dư toàn bộ ví từ lịch sử dòng tiền, đối soát 4 điều kiện toàn vẹn và cảnh báo sai lệch. |

## 7.2 Kiểm soát quy tắc nghiệp vụ

Bảng dưới đây xác lập mối liên kết giữa 5 quy tắc nghiệp vụ cốt lõi với các thực thể miền, đối tượng giá trị kiểm soát và mã lỗi phát sinh khi vi phạm:

| Mã quy tắc | Tên quy tắc nghiệp vụ | Thực thể và đối tượng giá trị kiểm soát | Mã lỗi miền tương ứng | Cơ chế kiểm soát và bất biến trong OOA |
| :--- | :--- | :--- | :--- | :--- |
| `BR-FIN-01` | Quy tắc độ chính xác tài chính | `Money`, `NetCashFlow`, `SavingsRate`, `SpendingStructureShare` | `ERR-FIN-001`<br/>`ERR-FIN-002` | Đóng gói giá trị tiền tệ và tỷ lệ trong các đối tượng giá trị bất biến; dùng số thập phân chính xác (cấm `float`/`double`), cố định 2 chữ số thập phân và làm tròn ngân hàng cho mọi phép chia. |
| `BR-FIN-02` | Quy tắc bảo toàn số dư ví | `Wallet`, `IncomeExpenseTransaction`, `TransferTransaction`, `BalanceAdjustmentRecord`, `TransactionFee` | `ERR-WAL-001`<br/>`ERR-FEE-001`<br/>`ERR-FEE-002`<br/>`ERR-FEE-003` | Duy trì bất biến số dư ví $\ge 0.00$ VNĐ; cấm gán trực tiếp mà tính lại từ lịch sử dòng tiền; kiểm tra khả năng chi trả trước mọi thao tác xuất quỹ; gắn vòng đời `TransactionFee` với giao dịch gốc và lưu vết `BalanceAdjustmentRecord` bất biến. |
| `BR-FIN-03` | Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ | `TransferTransaction`, `Wallet`, `TransactionFee` | `ERR-TRF-001`<br/>`ERR-TRF-002` | Hạch toán nguyên tử đồng thời giữa ví nguồn và ví đích (`sourceWalletId != destinationWalletId`); bảo toàn tổng số dư hệ thống trước phí; tách riêng phí chuyển tiền thành khoản `EXPENSE`; hoàn nguyên đối xứng hai ví khi xóa chuyển tiền. |
| `BR-FIN-04` | Quy tắc phân cấp danh mục thu chi | `Category`, `NeedGroup`, `TransactionType` | `ERR-CAT-001`<br/>`ERR-CAT-002`<br/>`ERR-CAT-003`<br/>`ERR-CAT-004`<br/>`ERR-CAT-005` | Duy trì phân cấp 3 cấp (`TransactionType` $\rightarrow$ `NeedGroup` $\rightarrow$ `Category`); chỉ gán giao dịch vào cấp 3; cấm đổi `TransactionType`; áp dụng xóa mềm (`DELETED`) khi đã có giao dịch lịch sử và giữ tối thiểu một `NeedGroup` đang `ACTIVE` cho mỗi nhánh. |
| `BR-NAM-01` | Quy tắc đặt tên ví, nhóm nhu cầu và danh mục | `Wallet`, `NeedGroup`, `Category` | `ERR-NAM-001`<br/>`ERR-NAM-002` | Chuẩn hóa `trim`, giới hạn 1–50 ký tự; kiểm tra tính duy nhất không phân biệt hoa thường theo phạm vi (toàn hệ thống với `Wallet`, cùng `TransactionType` với `NeedGroup`, cùng `NeedGroup` với `Category`); giải phóng tên khi xóa vĩnh viễn. |
