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
- [04-dac-ta-bien-doi-du-lieu-thao-tac-he-thong.md](04-dac-ta-bien-doi-du-lieu-thao-tac-he-thong.md): Đặc tả biến đổi dữ liệu của các thao tác hệ thống
- [05-mo-hinh-trang-thai-thuc-the.md](05-mo-hinh-trang-thai-thuc-the.md): Biểu đồ máy trạng thái cho vòng đời thực thể
- [06-so-do-robustness-va-the-crc.md](06-so-do-robustness-va-the-crc.md): Sơ đồ Robustness và bộ thẻ CRC phân bổ trách nhiệm sơ bộ
- [07-ma-tran-truy-vet-yeu-cau-sang-thuc-the.md](07-ma-tran-truy-vet-yeu-cau-sang-thuc-the.md): Ma trận truy vết hai chiều giữa yêu cầu và mô hình phân tích
- [08-tong-ket.md](08-tong-ket.md): Tổng kết hồ sơ phân tích hướng đối tượng
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

### Phần 4: Đặc tả Biến đổi Dữ liệu Thao tác Hệ thống
- 4.1 Cấu trúc Đặc tả Thao tác
- 4.2 Các thao tác Quản lý Ví
- 4.3 Các thao tác Ghi nhận Thu chi
- 4.4 Các thao tác Chuyển tiền Nội bộ
- 4.5 Các thao tác Danh mục Phân loại
- 4.6 Thao tác Lưu trữ và Phục hồi Dữ liệu

### Phần 5: Mô hình Trạng thái Thực thể
- 5.1 Quy chuẩn Mô hình Trạng thái
- 5.2 Vòng đời Thực thể Ví
- 5.3 Vòng đời Giao dịch Thu chi
- 5.4 Vòng đời Giao dịch Chuyển tiền
- 5.5 Vòng đời Thực thể Danh mục

### Phần 6: Sơ đồ Robustness và Thẻ CRC
- 6.1 Sơ đồ Robustness
- 6.2 Thẻ CRC

### Phần 7: Ma trận Truy vết
- 7.1 Truy vết Yêu cầu Chức năng
- 7.2 Kiểm soát Quy tắc Nghiệp vụ

### Phần 8: Tổng kết
- Tổng kết kết quả phân tích và đóng khóa đường cơ sở nghiệp vụ

### Phần 9: Tài liệu Trích dẫn
- Danh mục giáo trình và chuẩn mực kỹ thuật quốc tế
