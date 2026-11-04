# Hồ sơ Thiết kế Hướng đối tượng Hệ thống Quản lý Tài chính Cá nhân

> **HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG (PTIT)**

| Thông tin | Chi tiết |
| :--- | :--- |
| **Khoa** | Khoa Công nghệ Thông tin 1 |
| **Học phần** | Lập trình Hướng đối tượng |
| **Giảng viên** | Phạm Hoàng Việt <br/> Nguyễn Đình Quảng |
| **Nhóm** | 3 |
| **Thành viên** | Lê Anh Minh (B24DCCE180)<br/>Ứng Trọng Trình (B24DCCE271)<br/>Nguyễn Đình Tiến (B24DCCE264)<br/>Nguyễn Đinh Anh Quân (B24DCCE222) |

# Mục lục Tổng quan Hồ sơ Thiết kế Hướng đối tượng

<!-- PLACEHOLDER: Bảng mục lục điều hướng và tóm tắt phạm vi kỹ thuật của 12 tài liệu thành phần trong hồ sơ OOD | Căn cứ: Milestone 3 & Milestone 5 PTIT OOP Rubric, Craig Larman Chapters 13-17, Martin Fowler PoEAA | Tiêu chí hoàn thành: Bao phủ đủ 12 tệp, phân cấp tiêu đề chuẩn H2-H3-H4, liên kết tệp chính xác -->

## Danh mục Tài liệu Thành phần

- [00-preamble.md](00-preamble.md): Thông tin định danh học phần, giảng viên hướng dẫn và nhóm tác giả
- [01-kien-truc-he-thong-va-phan-tang.md](01-kien-truc-he-thong-va-phan-tang.md): Kiến trúc phân tầng, cấu trúc gói mã nguồn và quy tắc kiến trúc
- [02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md): Sơ đồ lớp thiết kế cho các lớp thực thể và đối tượng giá trị bất biến
- [03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md](03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md): Thiết kế tầng dịch vụ điều phối nghiệp vụ và hợp đồng phương thức
- [04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md): Thiết kế tầng lưu trữ dữ liệu, giao diện kho lưu trữ và các bộ điều hợp
- [05-so-do-tuong-tac-thiet-ke-sequence-diagram.md](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md): Sơ đồ tuần tự thiết kế chi tiết cho các ca sử dụng của hệ thống
- [06-ap-dung-nguyen-ly-solid-va-thiet-ke-mau-gof.md](06-ap-dung-nguyen-ly-solid-va-thiet-ke-mau-gof.md): Đánh giá tuân thủ nguyên lý thiết kế và chi tiết các mẫu thiết kế hướng đối tượng
- [07-thiet-ke-giao-dien-nguoi-dung-console-cli.md](07-thiet-ke-giao-dien-nguoi-dung-console-cli.md): Thiết kế giao diện dòng lệnh, điều hướng menu, xác thực đầu vào và đối tượng truyền dữ liệu
- [08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md): Cây phân cấp ngoại lệ hệ thống, từ điển mã lỗi nghiệp vụ và cơ chế ghi nhật ký
- [09-ma-tran-truy-vet-thiet-ke-sang-phan-tich-va-srs.md](09-ma-tran-truy-vet-thiet-ke-sang-phan-tich-va-srs.md): Ma trận truy vết hai chiều giữa yêu cầu, phân tích và bản vẽ thiết kế
- [10-tong-ket-va-lo-trinh-cai-dat.md](10-tong-ket-va-lo-trinh-cai-dat.md): Tổng hợp chỉ số thiết kế hệ thống và lộ trình cài đặt mã nguồn
- [11-tai-lieu-trich-dan.md](11-tai-lieu-trich-dan.md): Danh mục tài liệu tham khảo kỹ thuật và chuẩn mực quốc tế chuẩn APA

## Phân cấp Cấu trúc Chi tiết các Phần

### Phần 1: Kiến trúc Hệ thống và Phân tầng Mã nguồn

<!-- PLACEHOLDER: Mô tả cấu trúc phân tầng Clean Layered (Presentation, Application, Domain, Infrastructure), sơ đồ luồng dữ liệu một chiều và quy tắc phụ thuộc Dependency Rule | Căn cứ: Martin Fowler PoEAA Chapter 1, Craig Larman Chapter 13 | Tiêu chí: Làm rõ ranh giới từng tầng, không để rò rỉ chi tiết UI/IO vào Domain -->

- 1.1 Tổng quan Kiến trúc Phân tầng
- 1.2 Trách nhiệm và Ranh giới của các Tầng
- 1.3 Cấu trúc Gói Mã nguồn
- 1.4 Luồng Xử lý và Biến đổi Dữ liệu

### Phần 2: Thiết kế Lớp Thực thể và Đối tượng Giá trị

<!-- PLACEHOLDER: Mô tả sơ đồ lớp thiết kế chi tiết cho toàn bộ tầng nghiệp vụ cốt lõi, phân loại Thực thể và Đối tượng Giá trị, đặc tả thuộc tính với kiểu dữ liệu Java chính xác và hợp đồng đóng gói | Căn cứ: Milestone 3 Rubric, Eric Evans DDD, ISO/IEC 19505 | Tiêu chí: Đầy đủ phạm vi truy cập, kiểu dữ liệu tiền tệ BigDecimal, tính bất biến của đối tượng giá trị -->

- 2.1 Nguyên tắc Thiết kế Lớp Nghiệp vụ
- 2.2 Sơ đồ Lớp Tổng quan
- 2.3 Thiết kế các Lớp Thực thể
- 2.4 Thiết kế các Đối tượng Giá trị
- 2.5 Quan hệ Kế thừa và Tính Đa hình

### Phần 3: Thiết kế Tầng Dịch vụ và Điều phối Nghiệp vụ

<!-- PLACEHOLDER: Mô tả các dịch vụ điều phối ứng dụng và dịch vụ nghiệp vụ chuyên sâu, áp dụng nguyên lý GRASP Điều phối viên và Chuyên gia Thông tin, đặc tả chữ ký phương thức, điều kiện tiên quyết và điều kiện sau | Căn cứ: Craig Larman Chapters 16-17, Martin Fowler Service Layer | Tiêu chí: Dịch vụ đóng vai trò điều phối, không biến thành kịch bản giao dịch cồng kềnh -->

- 3.1 Nguyên tắc Phân định Trách nhiệm Tầng Dịch vụ
- 3.2 Hệ thống Giao diện và Hợp đồng Dịch vụ
- 3.3 Thiết kế Nhóm Dịch vụ Thực thi Tài chính
- 3.4 Thiết kế Nhóm Dịch vụ Kế hoạch và Phân tích
- 3.5 Quản lý Giao dịch và An toàn Dữ liệu

### Phần 4: Thiết kế Tầng Lưu trữ và Truy xuất Dữ liệu

<!-- PLACEHOLDER: Mô tả thiết kế tầng lưu trữ sử dụng mẫu Kho lưu trữ, đặc tả giao diện lưu trữ tổng quát và các lớp điều hợp cụ thể (lưu trữ bộ nhớ cho kiểm thử và lưu trữ tệp cho vận hành) | Căn cứ: Martin Fowler PoEAA Chapter 10, Đề tài học thuật PTIT | Tiêu chí: Tách biệt hoàn toàn cơ chế đọc ghi tệp khỏi logic nghiệp vụ cốt lõi -->

- 4.1 Nguyên tắc Thiết kế Mẫu Kho Lưu trữ
- 4.2 Giao diện Lưu trữ Tổng quát
- 4.3 Các Giao diện Lưu trữ Chuyên biệt theo Thực thể
- 4.4 Thiết kế Bộ Điều hợp Lưu trữ Tệp
- 4.5 Thiết kế Bộ Điều hợp Lưu trữ Bộ nhớ

### Phần 5: Sơ đồ Tuần tự Thiết kế

<!-- PLACEHOLDER: Mô tả luồng gọi phương thức động giữa các đối tượng qua 4 tầng kiến trúc cho 20 ca sử dụng, sử dụng đầy đủ các khung điều khiển điều kiện, lặp và tùy chọn | Căn cứ: Milestone 3 Rubric, Craig Larman Chapter 15, ISO/IEC 19505 | Tiêu chí: 100% phương thức trong sơ đồ tuần tự phải có mặt trên sơ đồ lớp tương ứng -->

- 5.1 Nguyên tắc Thiết kế Sơ đồ Tuần tự Chi tiết
- 5.2 Sơ đồ Tuần tự Nhóm Quản lý Ví
- 5.3 Sơ đồ Tuần tự Nhóm Ghi nhận Thu Chi
- 5.4 Sơ đồ Tuần tự Nhóm Chuyển tiền Nội bộ
- 5.5 Sơ đồ Tuần tự Nhóm Danh mục và Ngân sách
- 5.6 Sơ đồ Tuần tự Nhóm Báo cáo và Quản trị Dữ liệu

### Phần 6: Áp dụng Nguyên lý SOLID và Mẫu Thiết kế

<!-- PLACEHOLDER: Đánh giá chi tiết việc tuân thủ 5 nguyên lý thiết kế cơ bản và đặc tả chi tiết 4 mẫu thiết kế hướng đối tượng gồm Nhà máy, Chiến lược, Trạng thái và Quan sát | Căn cứ: Milestone 5 Rubric, Robert C. Martin Clean Architecture, Erich Gamma GoF | Tiêu chí: Mỗi mẫu thiết kế phải có sơ đồ lớp, vai trò từng thành phần và phân tích lợi ích kỹ thuật -->

- 6.1 Đánh giá Tuân thủ Năm Nguyên lý Thiết kế Cơ bản
- 6.2 Mẫu Nhà máy Khởi tạo Đối tượng
- 6.3 Mẫu Chiến lược Thuật toán Linh hoạt
- 6.4 Mẫu Trạng thái Vòng đời Nghiệp vụ
- 6.5 Mẫu Quan sát Đồng bộ Dữ liệu

### Phần 7: Thiết kế Giao diện Dòng lệnh

<!-- PLACEHOLDER: Mô tả cấu trúc tầng giao diện theo mô hình hiển thị và điều phối, cây menu phân cấp, quy chuẩn hiển thị bảng, bộ xác thực đầu vào và các đối tượng truyền dữ liệu | Căn cứ: Đề tài học thuật PTIT số 10, Martin Fowler DTO Pattern | Tiêu chí: Không để rò rỉ bất kỳ logic tính toán tài chính nào tại tầng giao diện -->

- 7.1 Cấu trúc Tầng Giao diện Người dùng
- 7.2 Cấu trúc Cây Menu Phân cấp
- 7.3 Thiết kế Bộ Xác thực Dữ liệu Đầu vào
- 7.4 Thiết kế các Đối tượng Truyền Dữ liệu

### Phần 8: Thiết kế Xử lý Ngoại lệ và Ghi Nhật ký

<!-- PLACEHOLDER: Mô tả cây phân cấp lớp ngoại lệ hệ thống, phân loại ngoại lệ kỹ thuật và nghiệp vụ, từ điển mã lỗi chuẩn hóa và cơ chế ghi nhật ký kiểm toán tệp tin | Căn cứ: Milestone 4 Rubric, Joshua Bloch Effective Java | Tiêu chí: Mọi lỗi nghiệp vụ đều có mã định danh lỗi và thông báo thân thiện -->

- 8.1 Chiến lược Quản lý Ngoại lệ Toàn hệ thống
- 8.2 Cây Phân cấp Lớp Ngoại lệ Hệ thống
- 8.3 Bảng Mã Lỗi Nghiệp vụ Chuẩn hóa
- 8.4 Cơ chế Ghi Nhật ký Hoạt động và Kiểm toán

### Phần 9: Ma trận Truy vết Thiết kế

<!-- PLACEHOLDER: Bảng ma trận đối chiếu giữa yêu cầu chức năng, thao tác hệ thống và các lớp phương thức thiết kế | Căn cứ: Chuẩn kỹ thuật ISO/IEC 29148 | Tiêu chí: Đảm bảo độ bao phủ 100% không sót nghiệp vụ và không sinh lớp mồ côi -->

- 9.1 Ánh xạ Yêu cầu Chức năng sang Lớp Thiết kế
- 9.2 Ánh xạ Thao tác Hệ thống sang Phương thức Dịch vụ
- 9.3 Đánh giá Độ Bao phủ và Tính Nhất quán Thiết kế

### Phần 10: Tổng kết và Lộ trình Cài đặt

<!-- PLACEHOLDER: Tổng hợp định lượng chỉ số thiết kế hệ thống, đối chiếu tiêu chí đề bài, đóng khóa thiết kế và lập lộ trình cài đặt mã nguồn Java | Căn cứ: Milestone 4 Rubric, Tiêu chuẩn quản lý cấu hình | Tiêu chí: Thứ tự cài đặt lớp logic rõ ràng, kế hoạch kiểm thử tự động khả thi -->

- 10.1 Tổng hợp Chỉ số Thiết kế Hệ thống
- 10.2 Đóng Khóa Hồ sơ Thiết kế
- 10.3 Lộ trình Cài đặt Mã nguồn

### Phần 11: Tài liệu Trích dẫn

<!-- PLACEHOLDER: Danh mục toàn bộ các tài liệu tham khảo kỹ thuật, sách chuyên khảo phần mềm và tiêu chuẩn quốc tế từ thư mục reference/ được trích dẫn theo chuẩn APA 7th edition, sắp xếp theo thứ tự chữ cái A-Z | Căn cứ: Thư mục reference/, Quy tắc trích dẫn Rule 07 | Tiêu chí: 100% tài liệu được viện dẫn trong hồ sơ đều có mặt đầy đủ trong danh mục -->

- Danh mục Tài liệu Tham khảo Kỹ thuật Hợp nhất theo Chuẩn APA
