# Mục lục Tổng quan Hồ sơ Phân tích Hướng đối tượng

<!-- PLACEHOLDER: Bảng mục lục điều hướng và tóm tắt phạm vi kỹ thuật của 11 tài liệu thành phần trong hồ sơ OOA | Căn cứ: Milestone 2 PTIT OOP Rubric, Craig Larman Chapter 9-11 | Tiêu chí hoàn thành: Bao phủ đủ 11 tệp, phân cấp tiêu đề chuẩn H2-H3-H4, liên kết tệp chính xác -->

## Danh mục Tài liệu Thành phần

- [00-preamble.md](00-preamble.md): Thông tin định danh học phần, giảng viên hướng dẫn và nhóm tác giả
- [01-muc-luc-tong-quan.md](01-muc-luc-tong-quan.md): Cấu trúc điều hướng tổng thể hồ sơ phân tích
- [02-gioi-thieu-va-ngu-canh-he-thong.md](02-gioi-thieu-va-ngu-canh-he-thong.md): Mục đích tài liệu OOA, ranh giới hộp đen và tác nhân duy nhất
- [03-mo-hinh-mien-khai-niem.md](03-mo-hinh-mien-khai-niem.md): Mô hình miền khái niệm, phân loại Entity vs Value Object và các quy tắc bất biến
- [04-so-do-tuan-tu-he-thong-ssd.md](04-so-do-tuan-tu-he-thong-ssd.md): Sơ đồ tuần tự hệ thống cho 20 ca sử dụng và bảng System Operations
- [05-hop-dong-thao-tac-operation-contracts.md](05-hop-dong-thao-tac-operation-contracts.md): Đặc tả hợp đồng thao tác hệ thống chuẩn Craig Larman
- [06-mo-hinh-trang-thai-thuc-the.md](06-mo-hinh-trang-thai-thuc-the.md): Biểu đồ máy trạng thái UML cho vòng đời thực thể
- [07-phan-tich-robustness-va-crc.md](07-phan-tich-robustness-va-crc.md): Phân tích Robustness BCE và bộ thẻ CRC phân bổ trách nhiệm sơ bộ
- [08-ma-tran-truy-vet-yeu-cau-sang-thuc-the.md](08-ma-tran-truy-vet-yeu-cau-sang-thuc-the.md): Ma trận truy vết hai chiều FR/UC sang Entity, Operation và Business Rule
- [09-tong-ket-va-chuyen-giao-sang-ood.md](09-tong-ket-va-chuyen-giao-sang-ood.md): Tổng kết phân tích Milestone 2 và tiêu chí chuyển tiếp sang OOD
- [10-tai-lieu-trich-dan.md](10-tai-lieu-trich-dan.md): Danh mục tài liệu tham khảo kỹ thuật chuẩn APA 7th edition

## Phân cấp Cấu trúc Chi tiết các Phần

### Phần 1: Giới thiệu và Ngữ cảnh Hệ thống
- 1.1 Mục đích và Phạm vi Tài liệu OOA
- 1.2 Ranh giới Hệ thống và Nguyên tắc Hộp đen (:System)
- 1.3 Mô hình Ngữ cảnh Hệ thống
- 1.4 Đặc tả Tác nhân Hệ thống (Người dùng theo AS-01)
- 1.5 Mối liên kết với Đặc tả Yêu cầu SRS

### Phần 2: Mô hình Miền Khái niệm
- 2.1 Phương pháp luận Xác định Lớp Khái niệm (Conceptual Classes)
- 2.2 Bảng Phân loại Khái niệm: Entity vs Value Object
- 2.3 Biểu đồ Lớp Miền Khái niệm (Domain Model Diagram)
- 2.4 Đặc tả Chi tiết các Lớp Thực thể (Domain Entities)
- 2.5 Đặc tả Chi tiết các Đối tượng Giá trị (Value Objects)
- 2.6 Các Mối Quan hệ Ngữ nghĩa và Bất biến Miền (BR-FIN-02, BR-FIN-03, BR-FIN-04)

### Phần 3: Sơ đồ Tuần tự Hệ thống
- 3.1 Nguyên tắc Mô hình hóa SSD và Ranh giới Hộp đen (:System)
- 3.2 Bảng Tổng hợp System Events và System Operations Toàn Hệ thống
- 3.3 SSD Nhóm Chức năng Quản lý Ví (WAL)
- 3.4 SSD Nhóm Chức năng Ghi nhận Thu chi (TXN)
- 3.5 SSD Nhóm Chức năng Chuyển tiền Nội bộ (TRF)
- 3.6 SSD Nhóm Chức năng Danh mục Phân loại (CAT)
- 3.7 SSD Nhóm Chức năng Thống kê và Báo cáo (REP)
- 3.8 SSD Nhóm Chức năng Lưu trữ và Sao lưu Dữ liệu (DAT)

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
