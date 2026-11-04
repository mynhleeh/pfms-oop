# 10. Tổng kết và Lộ trình Cài đặt

## 10.1 Tổng hợp Chỉ số Thiết kế Hệ thống

### 10.1.1 Bảng Thống kê Định lượng Thành phần Kiến trúc
<!-- PLACEHOLDER: Bảng tổng hợp số liệu định lượng toàn bộ thiết kế hệ thống PFMS: tổng số gói mã nguồn Java, tổng số lớp thực thể và lớp con kế thừa, tổng số đối tượng giá trị bất biến, tổng số giao diện dịch vụ và lớp cài đặt, tổng số giao diện kho lưu trữ và bộ điều hợp, tổng số mẫu thiết kế GoF được áp dụng, và tổng số sơ đồ tuần tự chi tiết | Căn cứ: Số liệu thực tế từ các mục 01 đến 08 của hồ sơ OOD | Tiêu chí hoàn thành: Thống kê số liệu minh bạch, phản ánh quy mô hoàn chỉnh và độ phức tạp phù hợp của đồ án -->

### 10.1.2 Đánh giá Mức độ Hoàn thiện so với Tiêu chí Đề bài
<!-- PLACEHOLDER: Bảng tự đánh giá đối chiếu với 10 tiêu chí chấm điểm trong Rubric học thuật PTIT (thang điểm 100): Phân tích bài toán, Mô hình hóa UML, Tính đóng gói và thiết kế lớp, Quan hệ kết tập/hợp thành, Kế thừa và đa hình, Giao diện và tính trừu tượng, Bộ sưu tập và xử lý ngoại lệ, Nguyên lý SOLID và mẫu thiết kế; chỉ rõ các bằng chứng thiết kế tương ứng cho từng tiêu chí đạt điểm tối đa | Căn cứ: Rubric đánh giá đồ án học thuật PTIT | Tiêu chí hoàn thành: 100% tiêu chí đều có bằng chứng tài liệu đối chiếu trực tiếp, sẵn sàng cho buổi bảo vệ đồ án -->

## 10.2 Đóng Khóa Hồ sơ Thiết kế

### 10.2.1 Quy trình Quản lý Thay đổi khi Lập trình
<!-- PLACEHOLDER: Quy định kỷ luật quản lý thay đổi khi chuyển sang giai đoạn lập trình mã nguồn: nếu trong quá trình viết mã hoặc chạy kiểm thử phát hiện các thiếu sót, phát sinh ca biên (edge case) hoặc cần điều chỉnh chữ ký hàm thì phải thực hiện quy trình rà soát ngược (Two-Way Ripple) cập nhật lại hồ sơ thiết kế trước; tuyệt đối không tự ý sửa mã nguồn tạo ra sự sai lệch giữa bản vẽ thiết kế và mã nguồn thực tế | Căn cứ: Quy tắc cộng tác R7 (Bidirectional Lifecycle Synchronization) | Tiêu chí hoàn thành: Quy trình kiểm soát phiên bản và nguyên tắc cập nhật đồng bộ được nêu rõ ràng -->

### 10.2.2 Biên bản Đóng khóa Đường Cơ sở Thiết kế
<!-- PLACEHOLDER: Xác nhận đóng khóa chính thức toàn bộ 12 tài liệu của hồ sơ Thiết kế Hướng đối tượng; công bố phiên bản thiết kế cơ sở được duyệt bởi các thành viên nhóm làm căn cứ duy nhất để triển khai lập trình trong Milestone 4 | Căn cứ: Chuẩn quản lý cấu hình phần mềm ISO/IEC/IEEE 12207 | Tiêu chí hoàn thành: Tuyên bố đóng khóa rõ ràng kèm ngày tháng và phạm vi kỹ thuật được nghiệm thu -->

## 10.3 Lộ trình Cài đặt Mã nguồn

### 10.3.1 Thứ tự Ưu tiên Cài đặt Từng Gói Mã nguồn
<!-- PLACEHOLDER: Lộ trình phân rã thứ tự lập trình mã nguồn theo nguyên lý từ dưới lên (Bottom-Up Approach): Bước 1 - Cài đặt các Đối tượng giá trị bất biến (Money, DateRange) và Lớp ngoại lệ; Bước 2 - Cài đặt các Thực thể miền (Wallet, Transaction, Budget) và logic đóng gói nghiệp vụ; Bước 3 - Cài đặt Giao diện kho lưu trữ và Bộ điều hợp lưu trữ bộ nhớ; Bước 4 - Cài đặt Tầng Dịch vụ điều phối; Bước 5 - Cài đặt Bộ điều hợp lưu trữ tệp (CSV/JSON); Bước 6 - Cài đặt Tầng Giao diện dòng lệnh và Menu điều hướng | Căn cứ: Clean Architecture, Nguyên lý phân rã công việc WBS | Tiêu chí hoàn thành: Thứ tự phụ thuộc logic chặt chẽ, bảo đảm mỗi bước lập trình đều có thể biên dịch và chạy kiểm thử ngay mà không bị thiếu thư viện phụ thuộc -->

### 10.3.2 Kế hoạch Kiểm thử Tự động theo Quy trình Hướng Kiểm thử
<!-- PLACEHOLDER: Kế hoạch triển khai kiểm thử đơn vị tự động sử dụng JUnit 5 theo quy trình phát triển hướng kiểm thử (TDD): viết bài kiểm thử xác minh trước khi viết mã nguồn; quy định tỷ lệ bao phủ mã nguồn (code coverage tối thiểu 80% cho tầng Domain và tầng Service); danh mục các ca kiểm thử trọng tâm: kiểm thử tính toán số dư không âm, kiểm thử tính lãi, kiểm thử tính toán ngân sách, kiểm thử chuyển tiền nguyên tử | Căn cứ: Rule 04 Clean Code Java, Martin Fowler TDD | Tiêu chí hoàn thành: Kế hoạch kiểm thử chi tiết, bảo đảm chất lượng mã nguồn đạt chuẩn trước khi ghép nối hệ thống hoàn chỉnh -->

