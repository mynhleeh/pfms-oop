# 09. Ma trận truy vết thiết kế

Tài liệu này xác lập ma trận truy vết hai chiều giữa mô hình thiết kế hướng đối tượng với yêu cầu chức năng (SRS) và quy tắc nghiệp vụ của hệ thống Quản lý Tài chính Cá nhân (PFMS). Bản ma trận chứng minh độ bao phủ 100% của 19 ca sử dụng qua 4 tầng kiến trúc và kiểm toán chéo việc cài đặt các quy tắc nghiệp vụ bất biến, loại trừ hoàn toàn lớp thiết kế mồ côi hoặc yêu cầu bị bỏ sót.

## 9.1 Ánh xạ yêu cầu chức năng sang lớp thiết kế

Mục này xác lập liên kết hai chiều giữa yêu cầu chức năng và các lớp thiết kế qua 4 tầng kiến trúc:
1. **Chiều xuôi**: Đảm bảo mỗi ca sử dụng được hiện thực hóa trọn vẹn qua chuỗi phối hợp: tầng trình diễn → tầng dịch vụ → tầng nghiệp vụ → tầng hạ tầng lưu trữ.
2. **Chiều ngược**: Đảm bảo mỗi lớp thiết kế đều bắt nguồn từ một ca sử dụng hoặc quy tắc nghiệp vụ thực tế, loại trừ hoàn toàn các lớp mồ côi.

Bảng ma trận dưới đây phân định trách nhiệm hiện thực hóa của các ca sử dụng xuyên suốt bốn tầng kiến trúc và chỉ rõ mẫu thiết kế được áp dụng để tối ưu hóa tính mở rộng:

| Mã ca sử dụng | Tên ca sử dụng | Tầng trình diễn | Tầng dịch vụ | Tầng nghiệp vụ | Tầng hạ tầng | Mẫu thiết kế áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Nhóm Quản lý Ví** |  |  |  |  |  |  |
| `UC-WAL-01` | Tạo ví mới | `WalletGUIController` | `WalletService` | `Wallet`, `WalletState` | `WalletRepository` | `State` |
| `UC-WAL-02` | Xem tổng hợp số dư và cơ cấu ví | `WalletGUIController`, `MainController` | `WalletService` | `Wallet`, `Money` | `WalletRepository`, `TransactionRepository` | `Observer` |
| `UC-WAL-03` | Cập nhật thông tin ví | `WalletGUIController` | `WalletService` | `Wallet` | `WalletRepository` | `State` |
| `UC-WAL-04` | Điều chỉnh số dư ví | `WalletGUIController` | `WalletService` | `Wallet`, `BalanceAdjustmentRecord` | `WalletRepository`, `BalanceAdjustmentRepository` | Không áp dụng |
| **Nhóm Ghi nhận Thu Chi** |  |  |  |  |  |  |
| `UC-TXN-01` | Ghi nhận giao dịch thu chi | `TransactionGUIController` | `TransactionService` | `Transaction`, `IncomeExpenseTransaction`, `TransactionFee`, `TransactionFactory`, `Wallet` | `TransactionRepository`, `WalletRepository`, `CategoryRepository` | `Factory Method`, `Observer` |
| `UC-TXN-02` | Tra cứu và lọc lịch sử giao dịch thu chi | `TransactionGUIController`, `MainController` | `TransactionService` | `Transaction`, `Category` | `TransactionRepository` | Không áp dụng |
| `UC-TXN-03` | Chỉnh sửa giao dịch thu chi | `TransactionGUIController` | `TransactionService` | `Transaction`, `Wallet` | `TransactionRepository`, `WalletRepository` | `Observer` |
| `UC-TXN-04` | Xóa giao dịch thu chi | `TransactionGUIController` | `TransactionService` | `Transaction`, `Wallet` | `TransactionRepository`, `WalletRepository` | `Observer` |
| **Nhóm Chuyển tiền Nội bộ** |  |  |  |  |  |  |
| `UC-TRF-01` | Chuyển tiền nội bộ giữa hai ví | `TransferGUIController` | `TransferService` | `TransferTransaction`, `Wallet` | `TransactionRepository`, `WalletRepository`, `CategoryRepository` | `Observer` |
| `UC-TRF-02` | Tra cứu lịch sử chuyển tiền | `TransferGUIController` | `TransferService` | `TransferTransaction` | `TransactionRepository`, `WalletRepository` | Không áp dụng |
| `UC-TRF-03` | Xóa giao dịch chuyển tiền | `TransferGUIController` | `TransferService` | `TransferTransaction`, `Wallet` | `TransactionRepository`, `WalletRepository` | `Observer` |
| **Nhóm Quản lý Danh mục** |  |  |  |  |  |  |
| `UC-CAT-01` | Bổ sung danh mục phân loại mới | `CategoryGUIController` | `CategoryService` | `Category`, `NeedGroup` | `CategoryRepository`, `NeedGroupRepository` | Không áp dụng |
| `UC-CAT-02` | Đổi tên danh mục | `CategoryGUIController` | `CategoryService` | `Category` | `CategoryRepository` | Không áp dụng |
| `UC-CAT-03` | Khôi phục hoặc tạo lại mục đã xóa | `CategoryGUIController` | `CategoryService` | `Category` | `CategoryRepository` | Không áp dụng |
| `UC-CAT-04` | Xóa danh mục hoặc nhóm | `CategoryGUIController` | `CategoryService` | `Category`, `Transaction` | `CategoryRepository`, `TransactionRepository` | Không áp dụng |
| **Nhóm Báo cáo và Thống kê** |  |  |  |  |  |  |
| `UC-REP-01` | Thống kê dòng tiền 12 tháng gần nhất | `ReportGUIController` | `ReportService` | `Transaction`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `NetCashFlow`, `SavingsRate` | `TransactionRepository` | `Strategy (ReportExporter tại tầng trình diễn)` |
| `UC-REP-02` | Phân tích cơ cấu thu chi theo nhóm và danh mục | `ReportGUIController` | `ReportService` | `Transaction`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `Category`, `NeedGroup`, `SpendingStructureShare` | `TransactionRepository`, `NeedGroupRepository`, `CategoryRepository` | `Strategy (ReportExporter tại tầng trình diễn)` |
| **Nhóm Quản trị Dữ liệu** |  |  |  |  |  |  |
| `UC-DAT-01` | Xuất dữ liệu tài chính | `DataManagementGUIController` | `DataManagementService` | Toàn bộ thực thể hệ thống | Toàn bộ kho lưu trữ | Không áp dụng |
| `UC-DAT-02` | Phục hồi dữ liệu hệ thống từ bản sao lưu | `DataManagementGUIController` | `DataManagementService` | Toàn bộ thực thể hệ thống | Toàn bộ kho lưu trữ | Không áp dụng |
| `UC-DAT-03` | Kiểm tra lại dữ liệu hệ thống | `DataManagementGUIController` | `DataManagementService` | `Wallet`, `Transaction`, `TransferTransaction` | Toàn bộ kho lưu trữ | Không áp dụng |

## 9.2 Đánh giá độ bao phủ và tính nhất quán thiết kế

Mục này đối chiếu việc cài đặt các quy tắc tài chính bất biến và quy tắc định danh vào các lớp thiết kế chuyên trách, bảo đảm tính toàn vẹn dữ liệu và độ chính xác số học:

| Mã quy tắc | Tên quy tắc nghiệp vụ | Lớp thiết kế chịu trách nhiệm | Phương thức thực thi chính | Cơ chế kiểm soát kỹ thuật |
| :--- | :--- | :--- | :--- | :--- |
| `BR-FIN-01` | Độ chính xác tài chính | `Money`, `NetCashFlow`, `SavingsRate`, `SpendingStructureShare` | `of`, `add`, `subtract`, `multiply`, `divide` | Sử dụng `BigDecimal` quy tắc làm tròn ngân hàng (`HALF_EVEN`), tiền làm tròn 2 chữ số, tỷ lệ giữ tối thiểu 4 chữ số thập phân trung gian và bảo vệ mẫu số bằng không. |
| `BR-FIN-02` | Bảo toàn số dư ví | `Wallet`, `WalletServiceImpl` | `debit`, `hasSufficientBalance`, `adjustBalance` | Kiểm soát số dư khả dụng không âm đối với ví tiền mặt, tính toán lại số dư từ dòng tiền và ghi vết kiểm kê. |
| `BR-FIN-03` | Ghi nhận đồng thời khi chuyển tiền | `TransferTransaction`, `TransferServiceImpl` | `applyToWallet`, `revertFromWallet`, `transferFunds`, `deleteTransfer` | Cập nhật nguyên tử đồng thời hai ví nguồn và ví đích, hoàn nguyên cả hai ví và kiểm tra số dư ví đích khi xóa. |
| `BR-FIN-04` | Phân cấp danh mục thu chi ba cấp | `Category`, `NeedGroup`, `CategoryServiceImpl` | `createCategory`, `renameCategory`, `restoreCategory`, `deleteCategory`, `deleteNeedGroup`, `changeNeedGroup`, `restore` | Duy trì cây phân cấp ba cấp, cấm đổi loại khi đã có giao dịch, thực thi xóa mềm để bảo toàn lịch sử dữ liệu. |
| `BR-NAM-01` | Quy tắc định danh và tên duy nhất | `EntityName`, `WalletServiceImpl`, `CategoryServiceImpl` | `of`, `matches`, `existsByName` | Cắt khoảng trắng hai đầu, giới hạn từ 1 đến 50 ký tự, kiểm tra tính duy nhất không phân biệt hoa thường. |
