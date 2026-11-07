# 09. Ma trận Truy vết Thiết kế

Tài liệu này xác lập ma trận truy vết hai chiều hoàn chỉnh giữa các lớp thiết kế hướng đối tượng với danh mục yêu cầu chức năng, các thao tác hệ thống và hệ thống quy tắc tài chính của Hệ thống Quản lý Tài chính Cá nhân PFMS. Bản ma trận đóng vai trò công cụ kiểm soát chất lượng kỹ thuật, bảo đảm tính nhất quán và loại trừ hoàn toàn sự sai lệch giữa mục tiêu nghiệp vụ với cấu trúc phần mềm.

Nội dung văn bản tập trung giải quyết hai mục tiêu kỹ thuật trọng tâm: chứng minh độ bao phủ toàn diện của toàn bộ các ca sử dụng thông qua bốn tầng kiến trúc của hệ thống; và kiểm toán chéo việc cài đặt các quy tắc nghiệp vụ tài chính bất biến, xác nhận không tồn tại các lớp thiết kế mồ côi hoặc các yêu cầu nghiệp vụ bị bỏ sót trong mã nguồn.

## 9.1 Ánh xạ Yêu cầu Chức năng sang Lớp Thiết kế

Mục này xác lập mối liên kết hai chiều giữa danh mục yêu cầu chức năng của hệ thống với các lớp thiết kế cụ thể được tổ chức theo mô hình kiến trúc bốn tầng. Phương pháp luận truy vết hai chiều bao gồm hai chiều kiểm toán độc lập:
1. Chiều xuôi: Kiểm tra từng ca sử dụng nghiệp vụ để bảo đảm được hiện thực hóa trọn vẹn bởi ít nhất một chuỗi phối hợp giữa các lớp từ tầng trình diễn, tầng dịch vụ, tầng nghiệp vụ đến tầng hạ tầng lưu trữ.
2. Chiều ngược: Kiểm tra từng lớp và phương thức trong bản vẽ thiết kế để bảo đảm đều có lý do tồn tại bắt nguồn từ một ca sử dụng hoặc quy tắc nghiệp vụ thực tế, loại trừ triệt để các lớp mồ côi không tham gia vào luồng xử lý của hệ thống.

Bảng ma trận dưới đây phân định trách nhiệm hiện thực hóa của các ca sử dụng xuyên suốt bốn tầng kiến trúc và chỉ rõ mẫu thiết kế được áp dụng để tối ưu hóa tính mở rộng:

| Mã ca sử dụng | Tên ca sử dụng | Tầng Trình diễn | Tầng Dịch vụ | Tầng Nghiệp vụ | Tầng Hạ tầng Lưu trữ | Mẫu thiết kế áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Nhóm Quản lý Ví** |  |  |  |  |  |  |
| `UC-WAL-01` | Tạo ví mới | `WalletController` | `WalletService` | `Wallet`, `CashWallet`, `CreditWallet` | `WalletRepository` | `State` |
| `UC-WAL-02` | Theo dõi số dư tính lại từ lịch sử dòng tiền và cơ cấu ví | `WalletController` | `WalletService` | `Wallet`, `Money` | `WalletRepository`, `TransactionRepository` | `Observer` |
| `UC-WAL-03` | Cập nhật thông tin ví | `WalletController` | `WalletService` | `Wallet` | `WalletRepository` | `State` |
| `UC-WAL-04` | Điều chỉnh số dư ví | `WalletController` | `WalletService` | `Wallet`, `BalanceAdjustment` | `WalletRepository`, `BalanceAdjustmentRepository` | Không áp dụng |
| **Nhóm Ghi nhận Thu Chi** |  |  |  |  |  |  |
| `UC-TXN-01` | Ghi nhận giao dịch thu chi | `TransactionController` | `TransactionService` | `Transaction`, `IncomeTransaction`, `ExpenseTransaction`, `Wallet` | `TransactionRepository`, `WalletRepository`, `CategoryRepository` | `Factory Method`, `Observer` |
| `UC-TXN-02` | Tra cứu và lọc lịch sử giao dịch thu chi | `TransactionController` | `TransactionService` | `Transaction`, `Category` | `TransactionRepository` | Không áp dụng |
| `UC-TXN-03` | Chỉnh sửa giao dịch thu chi | `TransactionController` | `TransactionService` | `Transaction`, `Wallet` | `TransactionRepository`, `WalletRepository` | `Observer` |
| `UC-TXN-04` | Xóa giao dịch thu chi | `TransactionController` | `TransactionService` | `Transaction`, `Wallet` | `TransactionRepository`, `WalletRepository` | `Observer` |
| **Nhóm Chuyển tiền Nội bộ** |  |  |  |  |  |  |
| `UC-TRF-01` | Chuyển tiền nội bộ giữa hai ví | `TransferController` | `TransferService` | `TransferTransaction`, `Wallet` | `TransferRepository`, `WalletRepository` | `Observer` |
| `UC-TRF-02` | Tra cứu lịch sử chuyển tiền | `TransferController` | `TransferService` | `TransferTransaction` | `TransferRepository` | Không áp dụng |
| `UC-TRF-03` | Xóa giao dịch chuyển tiền | `TransferController` | `TransferService` | `TransferTransaction`, `Wallet` | `TransferRepository`, `WalletRepository` | `Observer` |
| **Nhóm Quản lý Danh mục** |  |  |  |  |  |  |
| `UC-CAT-01` | Bổ sung danh mục phân loại mới | `CategoryController` | `CategoryService` | `Category`, `NeedGroup` | `CategoryRepository`, `NeedGroupRepository` | Không áp dụng |
| `UC-CAT-02` | Đổi tên danh mục | `CategoryController` | `CategoryService` | `Category` | `CategoryRepository` | Không áp dụng |
| `UC-CAT-03` | Khôi phục hoặc tạo lại mục đã xóa | `CategoryController` | `CategoryService` | `Category` | `CategoryRepository` | Không áp dụng |
| `UC-CAT-04` | Xóa danh mục hoặc nhóm | `CategoryController` | `CategoryService` | `Category`, `Transaction` | `CategoryRepository`, `TransactionRepository` | Không áp dụng |
| **Nhóm Báo cáo và Thống kê** |  |  |  |  |  |  |
| `UC-REP-01` | Thống kê dòng tiền 12 tháng gần nhất | `ReportController` | `ReportService` | `Transaction`, `Wallet` | `TransactionRepository`, `WalletRepository` | `Strategy` |
| `UC-REP-02` | Phân tích cơ cấu thu chi theo nhóm và danh mục | `ReportController` | `ReportService` | `Transaction`, `Category`, `NeedGroup` | `TransactionRepository`, `CategoryRepository` | `Strategy` |
| **Nhóm Quản trị Dữ liệu** |  |  |  |  |  |  |
| `UC-DAT-01` | Xuất dữ liệu tài chính | `DataManagementController` | `DataManagementService` | Toàn bộ thực thể hệ thống | Toàn bộ kho lưu trữ | `Strategy` |
| `UC-DAT-02` | Phục hồi dữ liệu hệ thống từ bản sao lưu | `DataManagementController` | `DataManagementService` | Toàn bộ thực thể hệ thống | Toàn bộ kho lưu trữ | Không áp dụng |
| `UC-DAT-03` | Kiểm tra lại dữ liệu hệ thống | `DataManagementController` | `DataManagementService` | `Wallet`, `Transaction`, `TransferTransaction` | Toàn bộ kho lưu trữ | Không áp dụng |

## 9.2 Đánh giá Độ Bao phủ và Tính Nhất quán Thiết kế

Mục này xác lập ma trận đối chiếu việc cài đặt các quy tắc tài chính bất biến và định danh dữ liệu của hệ thống vào các lớp thiết kế chuyên trách, bảo đảm tính toàn vẹn của dữ liệu dòng tiền và ngăn chặn các sai lệch số học trong quá trình vận hành:

| Mã quy tắc | Tên quy tắc nghiệp vụ | Lớp thiết kế chịu trách nhiệm | Phương thức thực thi chính | Cơ chế kiểm soát kỹ thuật |
| :--- | :--- | :--- | :--- | :--- |
| `BR-FIN-01` | Độ chính xác tài chính | `Money` | `of`, `plus`, `minus`, `multiply`, `divide` | Sử dụng số thập phân chính xác với quy tắc làm tròn ngân hàng, từ chối số tiền âm hoặc bằng 0. |
| `BR-FIN-02` | Bảo toàn số dư ví | `Wallet`, `WalletServiceImpl` | `validateSufficientBalance`, `recalculateBalance`, `adjustBalance` | Kiểm soát số dư khả dụng không âm đối với ví tiền mặt, tính toán lại số dư từ dòng tiền và ghi vết kiểm kê. |
| `BR-FIN-03` | Ghi nhận đồng thời khi chuyển tiền | `TransferTransaction`, `TransferServiceImpl` | `transferFunds`, `deleteTransfer` | Cập nhật nguyên tử đồng thời hai ví nguồn và ví đích, hoàn nguyên cả hai ví và kiểm tra số dư ví đích khi xóa. |
| `BR-FIN-04` | Phân cấp danh mục thu chi ba cấp | `Category`, `NeedGroup`, `CategoryServiceImpl` | `addCategory`, `deleteCategory`, `validateHierarchy` | Duy trì cây phân cấp ba cấp, cấm đổi loại khi đã có giao dịch, thực thi xóa mềm để bảo toàn lịch sử dữ liệu. |
| `BR-NAM-01` | Quy tắc định danh và tên duy nhất | `InputValidator`, `WalletServiceImpl`, `CategoryServiceImpl` | `validateName`, `existsByName` | Cắt khoảng trắng hai đầu, giới hạn từ 1 đến 50 ký tự, kiểm tra tính duy nhất không phân biệt hoa thường. |
