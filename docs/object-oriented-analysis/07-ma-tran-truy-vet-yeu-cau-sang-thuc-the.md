# 7. Ma trận Truy vết

Hệ thống ma trận truy vết hai chiều xác lập mối liên kết kiểm chứng kỹ thuật giữa hồ sơ yêu cầu phần mềm và mô hình phân tích hướng đối tượng. Mạng lưới này kết nối các yêu cầu chức năng, ca sử dụng và quy tắc nghiệp vụ tài chính với các lớp thực thể miền, đối tượng giá trị cùng danh mục thao tác hệ thống đã được mô hình hóa trong các phần trước.

Quá trình đối chiếu bảo đảm hai nguyên tắc truy vết:
- Truy vết xuôi: Xác nhận toàn bộ yêu cầu chức năng và quy tắc nghiệp vụ đều được hiện thực hóa đầy đủ trong cấu trúc và hành vi của mô hình phân tích, không bỏ sót bất kỳ ràng buộc nào.
- Truy vết ngược: Xác nhận mọi thực thể miền, đối tượng giá trị và thao tác hệ thống đều xuất phát từ nhu cầu sử dụng thực tế, không có thành phần dư thừa hoặc mồ côi.

## 7.1 Truy vết Yêu cầu Chức năng

Mục này xác định phạm vi tác động của từng yêu cầu chức năng và ca sử dụng lên cấu trúc dữ liệu của mô hình phân tích. Bảng đối chiếu thiết lập mối liên kết hai chiều trực tiếp giữa các yêu cầu chức năng theo từng nhóm nghiệp vụ với các lớp thực thể miền, đối tượng giá trị, đồng thời chỉ rõ thuộc tính cùng ràng buộc nghiệp vụ đảm nhiệm. Cấu trúc chi tiết đến cấp độ thuộc tính này đóng vai trò là căn cứ đối soát kỹ thuật, bảo đảm tính truy vết hai chiều xuyên suốt mô hình mà không phụ thuộc vào cách trình bày ở các phần mô tả.

| Mã yêu cầu | Ca sử dụng | Lớp thực thể miền | Đối tượng giá trị liên quan | Thuộc tính & Ràng buộc nghiệp vụ đảm nhiệm |
| :--- | :--- | :--- | :--- | :--- |
| **Nhóm Quản lý Ví (WAL)** |  |  |  |  |
| `FR-WAL-01` | `UC-WAL-01` | `Wallet` | `Money` | Khởi tạo `walletId` (sinh tự động), `name` (độ dài theo quy định, không trùng), `note`, `status` (`ACTIVE`), `createdAt`, `currentBalance` khởi tạo bằng 0. |
| `FR-WAL-02` | `UC-WAL-02` | `Wallet` | `Money` | Đọc toàn bộ thuộc tính (`walletId`, `name`, `status`, `note`, `createdAt`, `currentBalance`); tính tổng tài sản từ các ví hoạt động và ví lưu trữ có số dư dương. |
| `FR-WAL-03` | `UC-WAL-03` | `Wallet` |  | Cập nhật `name`, `note`; chuyển đổi `status` giữa hoạt động và lưu trữ; kiểm soát không xóa hoặc lưu trữ ví tiền mặt mặc định; chỉ xóa vật lý khi chưa phát sinh giao dịch. |
| `FR-WAL-04` | `UC-WAL-02` | `Wallet` | `Money`, `SpendingStructureShare` | Tính tỷ lệ phần trăm phân bổ số dư của từng ví trên tổng tài sản khả dụng; áp dụng quy tắc làm tròn ngân hàng. |
| `FR-WAL-05` | `UC-WAL-04` | `Wallet`, `BalanceAdjustmentRecord` | `Money`, `TransactionDate` | Cập nhật `currentBalance` theo số dư kiểm kê; khởi tạo thể hiện bất biến `BalanceAdjustmentRecord` (`recordId`, `previousBalance`, `actualBalance`, `differenceAmount`, `adjustmentDate`, `reason`). |
| **Nhóm Ghi nhận Thu chi (TXN)** |  |  |  |  |
| `FR-TXN-01` | `UC-TXN-01` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` | Khởi tạo thể hiện `IncomeExpenseTransaction` với loại chi tiêu; kiểm tra số dư ví khả dụng; giảm `Wallet.currentBalance`; liên kết danh mục chi tiết; khởi tạo `TransactionFee` nếu có phí kèm theo. |
| `FR-TXN-02` | `UC-TXN-01` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `NeedGroup` | `Money`, `TransactionType`, `TransactionDate` | Khởi tạo thể hiện `IncomeExpenseTransaction` với loại thu nhập; tăng `Wallet.currentBalance`; liên kết danh mục thu nhập chi tiết. |
| `FR-TXN-03` | `UC-TXN-02` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` | Đọc và lọc đa tiêu chí theo khoảng thời gian (`TransactionDate`), ví (`walletId`), danh mục (`categoryId`), loại giao dịch (`transactionType`). |
| `FR-TXN-04` | `UC-TXN-03` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `TransactionFee` | `Money`, `TransactionDate` | Hoàn nguyên số dư ví cũ; kiểm tra khả năng chi trả của số dư mới; cập nhật `amount`, `transactionDate`, `categoryId`, `note`, `TransactionFee.amount`; không thay đổi bản chất loại giao dịch. |
| `FR-TXN-05` | `UC-TXN-04` | `IncomeExpenseTransaction`, `Wallet`, `TransactionFee` | `Money` | Hoàn nguyên số dư ví ngược lại bản chất giao dịch; hủy thể hiện `IncomeExpenseTransaction` và `TransactionFee` liên kết. |
| **Nhóm Chuyển tiền Nội bộ (TRF)** |  |  |  |  |
| `FR-TRF-01` | `UC-TRF-01` | `TransferTransaction`, `Wallet`, `TransactionFee` | `Money`, `TransactionDate` | Kiểm tra ví nguồn khác ví đích; kiểm tra số dư ví nguồn đủ thanh toán gồm số tiền chuyển và phí; giảm số dư ví nguồn; tăng số dư ví đích; khởi tạo `TransferTransaction` và `TransactionFee` (nếu có). |
| `FR-TRF-02` | `UC-TRF-02`, `UC-TRF-03` | `TransferTransaction`, `Wallet`, `TransactionFee` | `Money`, `TransactionDate` | Truy vấn lịch sử chuyển tiền; khi xóa giao dịch, hoàn nguyên đồng thời hai ví (kiểm tra số dư ví đích đủ để trừ hoàn nguyên) và hủy bản ghi phí đi kèm. |
| **Nhóm Danh mục Phân loại (CAT)** |  |  |  |  |
| `FR-CAT-01` | `UC-CAT-01` | `Category`, `NeedGroup` | `TransactionType` | Khởi tạo `Category` (`categoryId`, `name`, `tier`, `parentId`, `transactionType`, `status`); kiểm tra tính duy nhất của tên trong cùng nhóm cha; duy trì cấu trúc phân cấp danh mục. |
| `FR-CAT-02` | `UC-CAT-02`, `UC-CAT-03` | `Category`, `NeedGroup` | `TransactionType` | Cập nhật `name`; kiểm tra trùng tên sau khi cắt khoảng trắng; khôi phục danh mục lưu trữ sang hoạt động. |
| `FR-CAT-03` | `UC-CAT-03`, `UC-CAT-04` | `Category`, `NeedGroup`, `IncomeExpenseTransaction` | `TransactionType` | Chuyển trạng thái sang lưu trữ nếu danh mục đã có giao dịch tham chiếu; chỉ xóa vật lý khi chưa có dữ liệu phát sinh; không xóa nhóm nếu làm rỗng phân loại. |
| **Nhóm Thống kê và Báo cáo (REP)** |  |  |  |  |
| `FR-REP-01` | `UC-REP-01` | `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee` | `NetCashFlow`, `SavingsRate`, `Money`, `TransactionDate` | Kiểm tra hợp lệ tham số báo cáo (`ERR-REP-001`, hỗ trợ mốc tương lai chỉ đọc); tổng hợp thu, chi và phí liên kết (chỉ đọc `TransferTransaction` để lấy ngày cho `TransactionFee` nhánh `EXPENSE`, không cộng số tiền chuyển gốc); tính dòng tiền thuần, tỷ lệ tiết kiệm (`0.00%` khi thu không dương) và bình quân chia cố định cho 12 tháng. |
| `FR-REP-02` | `UC-REP-02` | `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `Category`, `NeedGroup` | `SpendingStructureShare`, `Money`, `TransactionType`, `TransactionDate` | Kiểm tra hợp lệ tham số báo cáo (`ERR-REP-001`, hỗ trợ khoảng ngày tương lai chỉ đọc); phân bổ `EXPENSE` (gồm cả `TransactionFee`, chỉ đọc `TransferTransaction` để lấy ngày của phí liên kết và không cộng số tiền chuyển gốc) hoặc `INCOME` theo nhóm nhu cầu và danh mục; bảo lưu mục đã xóa kèm trạng thái `DELETED`; tính tỷ trọng, tổng hợp so sánh kỳ liền kề (`"Mới"` hoặc `0.00%`) và lọc nhóm/danh mục phát sinh dương. |
| **Nhóm Lưu trữ và Sao lưu Dữ liệu (DAT)** |  |  |  |  |
| `FR-DAT-01` | `UC-DAT-01` | Các thực thể miền trong hệ thống | `Money`, `TransactionDate`, `TransactionType` | Tuần tự hóa toàn bộ cấu trúc trạng thái của các thực thể miền thành tệp sao lưu dữ liệu. |
| `FR-DAT-02` | `UC-DAT-02` | Các thực thể miền trong hệ thống | `Money`, `TransactionDate`, `TransactionType` | Khôi phục thực thể từ tệp sao lưu; kiểm tra định dạng và tính toàn vẹn liên kết; tái lập cấu trúc đối tượng hoàn chỉnh. |
| `FR-DAT-03` | `UC-DAT-03` | `Wallet`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `BalanceAdjustmentRecord` | `Money` | Tái tính toán số dư toàn bộ ví từ lịch sử dòng tiền; đối soát chéo với thuộc tính số dư hiện tại; phát hiện và cảnh báo sai lệch. |

Bảng đối chiếu khẳng định mọi yêu cầu chức năng đều gắn liền với các thành phần dữ liệu cụ thể trong mô hình miền. Các lớp thực thể miền và đối tượng giá trị đều tham gia hiện thực hóa các chức năng nghiệp vụ, bảo đảm không có thành phần dữ liệu nào bị dư thừa hay mồ côi.

## 7.2 Kiểm soát Quy tắc Nghiệp vụ

Mục này tổng hợp cơ chế kiểm soát các quy tắc nghiệp vụ tài chính và định danh dữ liệu trong mô hình phân tích. Bảng xác lập mối liên kết giữa các quy tắc cốt lõi với các thực thể miền, đối tượng giá trị chịu trách nhiệm thực thi, cùng danh mục mã ngoại lệ phát sinh khi phát hiện vi phạm. Cơ chế này bảo đảm mọi bất biến nghiệp vụ đều được kiểm soát chặt chẽ trong cấu trúc và hành vi của đối tượng.

| Mã quy tắc | Tên quy tắc nghiệp vụ | Thực thể & Đối tượng giá trị kiểm soát | Mã lỗi miền tương ứng | Cơ chế kiểm soát & Bất biến trong OOA |
| :--- | :--- | :--- | :--- | :--- |
| `BR-FIN-01` | Quy tắc độ chính xác tài chính | `Money`, `NetCashFlow`, `SavingsRate`, `SpendingStructureShare` | `ERR-FIN-001`<br/>`ERR-FIN-002` | Đóng gói toàn bộ giá trị tiền tệ và tỷ lệ trong các đối tượng giá trị bất biến; sử dụng số thập phân chính xác, loại trừ hoàn toàn số thực dấu phẩy động nhị phân; quy định số chữ số thập phân và cơ chế làm tròn ngân hàng cho mọi phép toán. |
| `BR-FIN-02` | Quy tắc bảo toàn số dư ví | `Wallet`, `IncomeExpenseTransaction`, `TransferTransaction`, `BalanceAdjustmentRecord`, `TransactionFee` | `ERR-WAL-001`<br/>`ERR-FEE-001`<br/>`ERR-FEE-002`<br/>`ERR-FEE-003` | Duy trì bất biến số dư ví luôn không âm; cấm gán trực tiếp số dư mà bắt buộc tính toán lại từ lịch sử dòng tiền; đặt tiền điều kiện kiểm tra khả năng chi trả trước mọi thao tác giảm quỹ; liên kết vòng đời phí giao dịch với giao dịch gốc; lưu vết điều chỉnh kiểm kê bất biến. |
| `BR-FIN-03` | Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ | `TransferTransaction`, `Wallet`, `TransactionFee` | `ERR-TRF-001`<br/>`ERR-TRF-002` | Thực thi cơ chế ghi nhận đồng thời giữa ví nguồn và ví đích; kiểm tra định danh ví nguồn khác ví đích; bảo toàn tổng số dư toàn hệ thống khi không có phí; tách riêng phí chuyển tiền thành khoản chi tiêu độc lập; hoàn nguyên đối xứng cả hai ví khi xóa giao dịch chuyển tiền. |
| `BR-FIN-04` | Quy tắc phân cấp danh mục thu chi | `Category`, `NeedGroup`, `TransactionType` | `ERR-CAT-001`<br/>`ERR-CAT-002`<br/>`ERR-CAT-003`<br/>`ERR-CAT-004`<br/>`ERR-CAT-005` | Duy trì cấu trúc phân cấp quan hệ 1:N giữa Loại thu chi, Nhóm nhu cầu và Danh mục chi tiết; cấm đổi loại thu chi khi đã có giao dịch phát sinh; áp dụng trạng thái lưu trữ đối với danh mục và nhóm đã có giao dịch nhằm bảo toàn lịch sử báo cáo; bảo đảm mỗi loại thu chi luôn có ít nhất một nhóm hoạt động. |
| `BR-NAM-01` | Quy tắc tên của ví, nhóm nhu cầu và danh mục | `Wallet`, `NeedGroup`, `Category` | `ERR-NAM-001`<br/>`ERR-NAM-002` | Chuẩn hóa cắt khoảng trắng hai đầu; giới hạn độ dài hợp lệ; kiểm tra tính duy nhất không phân biệt hoa thường theo từng phạm vi quản lý (toàn hệ thống đối với ví, trong cùng loại đối với nhóm, trong cùng nhóm đối với danh mục); giải phóng tên ví khi bị xóa vật lý. |

Bảng kiểm soát khẳng định toàn bộ các quy tắc nghiệp vụ và ràng buộc kỹ thuật cốt lõi đều được phân bổ trách nhiệm rõ ràng cho các lớp khái niệm trong mô hình phân tích, loại trừ hoàn toàn nguy cơ sai lệch số học hoặc vi phạm bất biến dữ liệu trong các giai đoạn phát triển tiếp theo.
