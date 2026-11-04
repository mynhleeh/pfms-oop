# 07. Thiết kế Giao diện Dòng lệnh

## 7.1 Cấu trúc Tầng Giao diện Người dùng

### 7.1.1 Nguyên lý Phân tách Giao diện và Xử lý Lệnh
<!-- PLACEHOLDER: Đặc tả kiến trúc tầng giao diện dòng lệnh áp dụng mô hình phân tách rõ ràng giữa Lớp Hiển thị (Console View - chỉ làm nhiệm vụ in chuỗi ký tự, định dạng bảng và đọc dòng nhập) và Bộ Điều phối Giao diện (CLI Controller - tiếp nhận lựa chọn menu, điều hướng và gọi tầng dịch vụ); cam kết không chứa bất kỳ logic tính toán tài chính hoặc kiểm tra quy tắc nghiệp vụ nào tại tầng này | Căn cứ: Craig Larman Chapter 16 (UI Separation Principle), Martin Fowler PoEAA | Tiêu chí hoàn thành: Ranh giới rõ ràng: Tầng giao diện chỉ giao tiếp với tầng dịch vụ thông qua giao diện dịch vụ và đối tượng truyền dữ liệu -->

### 7.1.2 Vòng lặp Tiếp nhận Lệnh và Điều hướng Chương trình
<!-- PLACEHOLDER: Đặc tả cấu trúc vòng lặp sự kiện chính của ứng dụng: hiển thị menu chính -> chờ người dùng nhập lệnh -> phân giải mã lệnh -> gọi màn hình chức năng tương ứng -> quay trở lại menu chính khi hoàn thành hoặc kết thúc khi chọn thoát; cơ chế bắt ngoại lệ toàn cục tại vòng lặp chính để ứng dụng không bao giờ bị sập đột ngột khi xảy ra sự cố bất ngờ | Căn cứ: Đề tài học thuật PTIT số 10 (Ứng dụng tương tác dòng lệnh) | Tiêu chí hoàn thành: Sơ đồ khối hoặc máy trạng thái mô tả vòng lặp điều hướng từ lúc khởi động đến khi thoát an toàn -->

## 7.2 Cấu trúc Cây Menu Phân cấp

### 7.2.1 Sơ đồ Phân cấp các Nhóm Menu Chức năng
<!-- PLACEHOLDER: Sơ đồ cây phân cấp hoàn chỉnh của hệ thống menu: Menu Chính (Quản lý Ví, Ghi nhận Thu Chi, Chuyển tiền Nội bộ, Quản lý Danh mục & Ngân sách, Báo cáo Thống kê, Sao lưu & Cài đặt) và các menu con cấp 2, cấp 3 tương ứng; quy định mã phím bấm đồng nhất (số thứ tự từ 1 đến N, phím 0 để quay lại menu trước hoặc thoát) | Căn cứ: Yêu cầu giao diện 20 ca sử dụng PFMS | Tiêu chí hoàn thành: Bao phủ 100% các chức năng của hệ thống trong cấu trúc cây menu, không có chức năng nào bị cô lập không thể truy cập -->

### 7.2.2 Quy chuẩn Hiển thị Bảng Dữ liệu và Hộp Thoại
<!-- PLACEHOLDER: Quy chuẩn kỹ thuật về hiển thị nội dung trên màn hình dòng lệnh: quy cách vẽ khung bảng bằng các ký tự văn bản chuẩn (dấu gạch ngang, dấu gạch đứng, dấu cộng góc), quy tắc căn lề (chuỗi văn bản căn trái, số tiền và số lượng căn phải), định dạng số tiền có dấu phân cách hàng nghìn (ví dụ: 1,500,000 VND), và quy cách hiển thị thông báo kết quả (thông báo thành công, cảnh báo vượt ngưỡng ngân sách, thông báo lỗi) | Căn cứ: Chuẩn hiển thị giao diện dòng lệnh công nghiệp | Tiêu chí hoàn thành: Các mẫu khung giao diện bảng biểu mẫu đồng nhất, dễ đọc và không bị vỡ bố cục khi độ dài chuỗi ký tự thay đổi -->

## 7.3 Thiết kế Bộ Xác thực Dữ liệu Đầu vào

### 7.3.1 Xác thực Cú pháp Chuỗi Ký tự và Số học
<!-- PLACEHOLDER: Đặc tả chi tiết các lớp tiện ích kiểm tra dữ liệu đầu vào từ bàn phím trước khi chuyển đổi: kiểm tra chuỗi không rỗng, kiểm tra định dạng ngày tháng hợp lệ theo chuẩn (yyyy-MM-dd), kiểm tra số tiền là số dương hợp lệ và không vượt quá giới hạn cho phép, kiểm tra ký tự đặc biệt trong tên ví hoặc danh mục; loại bỏ hoàn toàn các khoảng trắng thừa ở hai đầu chuỗi | Căn cứ: Kỹ thuật lập trình an toàn chống lỗi nhập liệu | Tiêu chí hoàn thành: Các hàm xác thực trả về kiểu boolean hoặc kết quả xác thực kèm thông báo lỗi cụ thể, tái sử dụng được cho toàn bộ các màn hình nhập liệu -->

### 7.3.2 Cơ chế Nhắc Lại và Xử lý Lỗi Thân thiện
<!-- PLACEHOLDER: Đặc tả cơ chế lặp vòng lặp nhập liệu thân thiện: khi người dùng nhập sai định dạng (ví dụ: nhập chữ vào ô số tiền, nhập sai định dạng ngày), hệ thống không thoát màn hình mà in thông báo hướng dẫn rõ ràng và yêu cầu người dùng nhập lại ngay tại vị trí đó; hỗ trợ cơ chế nhập ký tự hủy bỏ để người dùng có thể thoát khỏi thao tác nhập dở dang | Căn cứ: Nguyên tắc thiết kế trải nghiệm người dùng dòng lệnh | Tiêu chí hoàn thành: Thử nghiệm không bị lỗi ngoại lệ kiểu dữ liệu NumberFormatException hay DateTimeParseException làm dừng chương trình -->

## 7.4 Thiết kế các Đối tượng Truyền Dữ liệu

### 7.4.1 Mục đích và Tính Đóng gói của Đối tượng Truyền Dữ liệu
<!-- PLACEHOLDER: Phân tích mục đích áp dụng mẫu Đối tượng Truyền Dữ liệu (DTO): tạo ra các thùng chứa dữ liệu thuần túy (chỉ gồm các trường dữ liệu nguyên thủy hoặc chuỗi, các hàm lấy giá trị và hàm khởi tạo) để vận chuyển thông tin qua lại giữa tầng giao diện và tầng dịch vụ; ngăn chặn việc tầng giao diện can thiệp trực tiếp vào các hàm nghiệp vụ của Thực thể miền | Căn cứ: Martin Fowler PoEAA (Data Transfer Object Pattern) | Tiêu chí hoàn thành: Không truyền đối tượng Thực thể miền lên tầng giao diện và không truyền trực tiếp các tham số dòng lệnh rời rạc xuống tầng dịch vụ -->

### 7.4.2 Danh mục các Lớp Truyền Dữ liệu Chi tiết
<!-- PLACEHOLDER: Bảng danh mục chi tiết toàn bộ các lớp DTO trong hệ thống: chia thành hai nhóm chính gồm DTO Yêu cầu (chứa dữ liệu người dùng gửi xuống để thực hiện ca sử dụng) và DTO Phản hồi (chứa kết quả tính toán hoặc danh sách đối tượng sau xử lý để hiển thị); mô tả danh sách các trường dữ liệu và kiểu tương ứng của từng lớp DTO | Căn cứ: Yêu cầu chức năng 20 ca sử dụng | Tiêu chí hoàn thành: Đầy đủ 100% các lớp DTO phục vụ cho tất cả các ca sử dụng tạo mới, cập nhật và truy vấn báo cáo của hệ thống -->

