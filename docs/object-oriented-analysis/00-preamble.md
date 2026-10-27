# Hồ sơ Phân tích Hướng đối tượng Hệ thống Quản lý Tài chính Cá nhân (PFMS)

> **HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG (PTIT)**

| Thông tin | Chi tiết |
| :--- | :--- |
| **Khoa** | Khoa Công nghệ Thông tin 1 |
| **Học phần** | Lập trình Hướng đối tượng |
| **Giảng viên** | Phạm Hoàng Việt <br/> Nguyễn Đình Quảng |
| **Nhóm** | 3 |
| **Thành viên** | Lê Anh Minh (B24DCCE180)<br/>Ứng Trọng Trình (B24DCCE271)<br/>Nguyễn Đình Tiến (B24DCCE264)<br/>Nguyễn Đinh Anh Quân (B24DCCE222) |

# Mục lục Tổng quan Hồ sơ Phân tích Hướng đối tượng

<!-- PLACEHOLDER: Bảng mục lục điều hướng và tóm tắt phạm vi kỹ thuật của 11 tài liệu thành phần trong hồ sơ OOA | Căn cứ: Milestone 2 PTIT OOP Rubric, Craig Larman Chapter 9-11 | Tiêu chí hoàn thành: Bao phủ đủ 11 tệp, phân cấp tiêu đề chuẩn H2-H3-H4, liên kết tệp chính xác -->

## Danh mục Tài liệu Thành phần

- [00-preamble.md](00-preamble.md): Thông tin định danh học phần, giảng viên hướng dẫn và nhóm tác giả
- [01-gioi-thieu-va-ngu-canh-he-thong.md](01-gioi-thieu-va-ngu-canh-he-thong.md): Mục đích tài liệu OOA, ranh giới và ngữ cảnh hệ thống, bảng đối chiếu khái niệm miền
- [02-mo-hinh-mien-khai-niem.md](02-mo-hinh-mien-khai-niem.md): Mô hình miền khái niệm, phân loại thực thể và đối tượng giá trị, các quy tắc nghiệp vụ
- [03-so-do-tuan-tu-he-thong-ssd.md](03-so-do-tuan-tu-he-thong-ssd.md): Sơ đồ tuần tự hệ thống cho 20 ca sử dụng và bảng thao tác hệ thống
- [04-hop-dong-thao-tac-operation-contracts.md](04-hop-dong-thao-tac-operation-contracts.md): Đặc tả hợp đồng thao tác hệ thống
- [05-mo-hinh-trang-thai-thuc-the.md](05-mo-hinh-trang-thai-thuc-the.md): Biểu đồ máy trạng thái cho vòng đời thực thể
- [06-phan-tich-robustness-va-crc.md](06-phan-tich-robustness-va-crc.md): Phân tích Robustness BCE và bộ thẻ CRC phân bổ trách nhiệm sơ bộ
- [07-ma-tran-truy-vet-yeu-cau-sang-thuc-the.md](07-ma-tran-truy-vet-yeu-cau-sang-thuc-the.md): Ma trận truy vết hai chiều chức năng sang thực thể, thao tác và quy tắc nghiệp vụ
- [08-tong-ket-va-chuyen-giao-sang-ood.md](08-tong-ket-va-chuyen-giao-sang-ood.md): Tổng kết phân tích Milestone 2 và tiêu chí chuyển tiếp sang OOD
- [09-tai-lieu-trich-dan.md](09-tai-lieu-trich-dan.md): Danh mục tài liệu tham khảo kỹ thuật chuẩn APA

## Phân cấp Cấu trúc Chi tiết các Phần

### Phần 1: Giới thiệu và Ngữ cảnh Hệ thống
- 1.1 Mục đích và Phạm vi Tài liệu OOA
- 1.2 Ranh giới và Ngữ cảnh Hệ thống
- 1.3 Bảng Đối chiếu Khái niệm Miền theo Nhóm Chức năng

### Phần 2: Mô hình Miền Khái niệm
- 2.1 Phương pháp luận Xác định Lớp Khái niệm
- 2.2 Bảng Phân loại Khái niệm: Entity vs Value Object
- 2.3 Đặc tả Chi tiết các Lớp Thực thể
- 2.4 Đặc tả Chi tiết các Đối tượng Giá trị
- 2.5 Các Mối Quan hệ Ngữ nghĩa và Bất biến Miền

### Phần 3: Sơ đồ Tuần tự Hệ thống
- 3.1 Nguyên tắc Mô hình hóa và Ranh giới Tương tác Hệ thống
- 3.2 Tổng hợp Thao tác Hệ thống theo Nhóm Chức năng
- 3.3 Nhóm Chức năng Quản lý Ví (WAL)
- 3.4 Nhóm Chức năng Ghi nhận Thu chi (TXN)
- 3.5 Nhóm Chức năng Chuyển tiền Nội bộ (TRF)
- 3.6 Nhóm Chức năng Danh mục Phân loại (CAT)
- 3.7 Nhóm Chức năng Thống kê và Báo cáo (REP)
- 3.8 Nhóm Chức năng Lưu trữ và Sao lưu Dữ liệu (DAT)

### Phần 4: Hợp đồng Thao tác Hệ thống
- 4.1 Phương pháp Luận và Cấu trúc Hợp đồng Chuẩn Craig Larman
- 4.2 Hợp đồng Thao tác: createWallet
- 4.3 Hợp đồng Thao tác: recordIncomeExpense
- 4.4 Hợp đồng Thao tác: transferFunds
- 4.5 Hợp đồng Thao tác: adjustBalance
- 4.6 Hợp đồng Thao tác: deleteCategory
- 4.7 Hợp đồng Thao tác: updateTransaction
- 4.8 Hợp đồng Thao tác: deleteTransaction
- 4.9 Bảng Tổng hợp Trách nhiệm Biến đổi Trạng thái

### Phần 5: Mô hình Trạng thái Thực thể
- 5.1 Khái niệm và Vai trò của Biểu đồ Trạng thái trong OOA
- 5.2 Vòng đời Thực thể Danh mục (Category State Machine)
- 5.3 Vòng đời Giao dịch Thu chi (IncomeExpenseTransaction State Machine)
- 5.4 Vòng đời Giao dịch Chuyển tiền (TransferTransaction State Machine)

### Phần 6: Phân tích Robustness và Thẻ CRC
- 6.1 Phương pháp Phân tích Robustness (Boundary - Control - Entity)
- 6.2 Biểu đồ Robustness cho các Ca Sử dụng Cốt lõi
- 6.3 Phương pháp Thẻ CRC (Class - Responsibility - Collaborator)
- 6.4 Bộ Thẻ CRC Phân bổ Trách nhiệm Sơ bộ

### Phần 7: Ma trận Truy vết Yêu cầu sang Thực thể và Thao tác
- 7.1 Ma trận Truy vết FR / UC sang Domain Entities và Value Objects
- 7.2 Ma trận Truy vết System Operations sang Domain Entities
- 7.3 Ma trận Tuân thủ Quy tắc Nghiệp vụ (BR-FIN và BR-NAM Coverage)
- 7.4 Xác nhận Khép kín Truy vết (Zero Orphan Entities / Requirements)

### Phần 8: Tổng kết Phân tích và Tiêu chí Chuyển giao sang OOD
- 8.1 Tổng kết Kết quả Phân tích OOA (Milestone 2)
- 8.2 Tiêu chí Nghiệm thu Hoàn thành Hồ sơ OOA
- 8.3 Danh mục Đầu vào Bàn giao cho Thiết kế Hướng đối tượng (OOD)
- 8.4 Hướng dẫn Chuyển dịch từ Khái niệm sang Mô hình Lớp Thiết kế

### Phần 9: Tài liệu Trích dẫn
- 9.1 Giáo trình và Chuẩn mực Kỹ thuật Quốc tế
- 9.2 Hồ sơ Kỹ thuật Dự án (Tier 1 và Tier 2)
