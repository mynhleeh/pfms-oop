# 7. Ma trận Truy vết Yêu cầu vào Mô hình Phân tích

Hệ thống ma trận truy vết hai chiều xác lập mối liên kết kiểm chứng kỹ thuật giữa hồ sơ yêu cầu phần mềm và mô hình phân tích hướng đối tượng. Mạng lưới này kết nối các yêu cầu chức năng, ca sử dụng và quy tắc nghiệp vụ tài chính với các lớp thực thể miền, đối tượng giá trị cùng danh mục thao tác hệ thống đã được mô hình hóa trong các phần trước.

Quá trình đối chiếu bảo đảm hai nguyên tắc truy vết:
- Truy vết xuôi: Xác nhận toàn bộ yêu cầu chức năng và quy tắc nghiệp vụ đều được hiện thực hóa đầy đủ trong cấu trúc và hành vi của mô hình phân tích, không bỏ sót bất kỳ ràng buộc nào.
- Truy vết ngược: Xác nhận mọi thực thể miền, đối tượng giá trị và thao tác hệ thống đều xuất phát từ nhu cầu sử dụng thực tế, không có thành phần dư thừa hoặc mồ côi.

## 7.1 Ma trận Đối chiếu Yêu cầu Chức năng với Thực thể và Đối tượng Giá trị

Mục này xác định phạm vi tác động của từng yêu cầu chức năng và ca sử dụng lên cấu trúc dữ liệu của mô hình phân tích. Bảng đối chiếu thiết lập mối liên kết trực tiếp giữa 20 yêu cầu chức năng theo 6 nhóm nghiệp vụ với các lớp thực thể miền và đối tượng giá trị đã được mô hình hóa tại Mục 2.

| Mã yêu cầu | Ca sử dụng | Lớp thực thể miền | Đối tượng giá trị liên quan |
| :--- | :--- | :--- | :--- |
| **Nhóm Quản lý Ví (WAL)** |  |  |  |
| `FR-WAL-01` | `UC-WAL-01` | `Wallet` | `Money` |
| `FR-WAL-02` | `UC-WAL-02` | `Wallet` | `Money` |
| `FR-WAL-03` | `UC-WAL-03` | `Wallet` |  |
| `FR-WAL-04` | `UC-WAL-02` | `Wallet` | `Money` |
| `FR-WAL-05` | `UC-WAL-04` | `Wallet`, `BalanceAdjustmentRecord` | `Money`, `TransactionDate` |
| **Nhóm Ghi nhận Thu chi (TXN)** |  |  |  |
| `FR-TXN-01` | `UC-TXN-01` | `Wallet`, `IncomeExpenseTransaction`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` |
| `FR-TXN-02` | `UC-TXN-01` | `Wallet`, `IncomeExpenseTransaction`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` |
| `FR-TXN-03` | `UC-TXN-02` | `Wallet`, `IncomeExpenseTransaction`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` |
| `FR-TXN-04` | `UC-TXN-03` | `Wallet`, `IncomeExpenseTransaction`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` |
| `FR-TXN-05` | `UC-TXN-04` | `Wallet`, `IncomeExpenseTransaction`, `TransactionFee` | `Money`, `TransactionDate` |
| **Nhóm Chuyển tiền Nội bộ (TRF)** |  |  |  |
| `FR-TRF-01` | `UC-TRF-01` | `Wallet`, `TransferTransaction`, `TransactionFee` | `Money`, `TransactionDate` |
| `FR-TRF-02` | `UC-TRF-02`, `UC-TRF-03` | `Wallet`, `TransferTransaction`, `TransactionFee` | `Money`, `TransactionDate` |
| **Nhóm Danh mục Phân loại (CAT)** |  |  |  |
| `FR-CAT-01` | `UC-CAT-01` | `Category`, `NeedGroup` | `TransactionType` |
| `FR-CAT-02` | `UC-CAT-02`, `UC-CAT-03` | `Category`, `NeedGroup` | `TransactionType` |
| `FR-CAT-03` | `UC-CAT-03`, `UC-CAT-04` | `Category`, `NeedGroup`, `IncomeExpenseTransaction` | `TransactionType` |
| **Nhóm Thống kê và Báo cáo (REP)** |  |  |  |
| `FR-REP-01` | `UC-REP-01` | `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee` | `NetCashFlow`, `SavingsRate`, `Money`, `TransactionDate` |
| `FR-REP-02` | `UC-REP-02` | `IncomeExpenseTransaction`, `Category`, `NeedGroup` | `SpendingStructureShare`, `Money`, `TransactionType` |
| **Nhóm Lưu trữ và Sao lưu Dữ liệu (DAT)** |  |  |  |
| `FR-DAT-01` | `UC-DAT-01` | Toàn bộ 7 thực thể miền | `Money`, `TransactionDate`, `TransactionType` |
| `FR-DAT-02` | `UC-DAT-02` | Toàn bộ 7 thực thể miền | `Money`, `TransactionDate`, `TransactionType` |
| `FR-DAT-03` | `UC-DAT-03` | `Wallet`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `BalanceAdjustmentRecord` | `Money` |

Bảng đối chiếu khẳng định mọi yêu cầu chức năng đều gắn liền với các thành phần dữ liệu cụ thể trong mô hình miền. Toàn bộ 7 lớp thực thể miền và 6 đối tượng giá trị đều tham gia hiện thực hóa các chức năng nghiệp vụ, bảo đảm không có thành phần dữ liệu nào bị dư thừa hay mồ côi.

## 7.2 Ma trận Tác động của Thao tác Hệ thống lên Thực thể Miền

<!-- PLACEHOLDER: Quy ước ký hiệu biến đổi dữ liệu và Bảng đối chiếu Thao tác hệ thống sang Thực thể miền | Nguồn tham chiếu: Checkpoint 2, Hợp đồng thao tác Mục 4 | Tiêu chí hoàn thành:
1. Bảng quy ước ký hiệu thao tác chuẩn:
   - C (Create instance): Khởi tạo thể hiện mới của thực thể.
   - R (Read / Guard verification): Đọc dữ liệu để kiểm tra điều kiện bảo vệ.
   - U (Update attributes / Form association): Cập nhật giá trị thuộc tính hoặc thiết lập liên kết đối tượng.
   - D (Soft-delete / Break association): Đánh dấu ẩn/xóa mềm hoặc hủy liên kết đối tượng.
2. Bảng ma trận lưới đối chiếu danh mục Thao tác hệ thống (hàng) với 7 Lớp thực thể (cột), ghi rõ ký hiệu C, R, U, D tại từng giao điểm. -->

## 7.3 Bảng Kiểm soát Quy tắc Nghiệp vụ và Ràng buộc Kỹ thuật

<!-- PLACEHOLDER: Bảng đối chiếu 5 Quy tắc nghiệp vụ (BR-FIN-01 đến BR-FIN-04, BR-NAM-01) sang các Lớp Khái niệm và Thao tác Hệ thống hiện thực hóa | Nguồn tham chiếu: SRS Mục 3 và Mục 8.1.4 | Tiêu chí hoàn thành:
1. Chứng minh 100% quy tắc nghiệp vụ và bất biến tài chính được kiểm soát chặt chẽ trong mô hình phân tích.
2. Ghi chú tích hợp ràng buộc kỹ thuật cốt lõi: Yêu cầu phi chức năng NFR-01 (Độ chính xác số học tuyệt đối) được hiện thực hóa thông qua Đối tượng giá trị Money (Mục 2.4.1), loại trừ hoàn toàn kiểu số thực dấu phẩy động nhị phân. -->

## 7.4 Báo cáo Kiểm toán Độ bao phủ và Tính Toàn vẹn Mô hình

<!-- PLACEHOLDER: Báo cáo kiểm toán định lượng về tính toàn vẹn và khép kín của hồ sơ OOA Milestone 2 theo nguyên tắc Evidence Before Assertion | Nguồn tham chiếu: Rule 00 [R3], Rule 01, ISO/IEC/IEEE 29148 | Tiêu chí hoàn thành:
Bảng Dashboard chỉ số định lượng khép kín gồm:
1. Tỷ lệ đáp ứng Yêu cầu chức năng: 20/20 FRs (100%).
2. Tỷ lệ hiện thực hóa Ca sử dụng: 20/20 UCs (100%).
3. Nguồn gốc Thực thể miền: 7/7 Entities (100% bắt nguồn từ FR/UC, 0 thực thể mồ côi).
4. Nguồn gốc Đối tượng giá trị: 6/6 Value Objects (100% bắt nguồn từ FR/UC, 0 đối tượng mồ côi).
5. Độ bao phủ Quy tắc nghiệp vụ: 5/5 BRs (100% được phân bổ kiểm soát).
Khẳng định số lượng phần tử mồ côi bằng 0 và sẵn sàng nghiệm thu chuyển giao sang OOD. -->
