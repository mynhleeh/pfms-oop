# 08. Thiết kế Xử lý Ngoại lệ và Ghi Nhật ký

## 8.1 Chiến lược Quản lý Ngoại lệ Toàn hệ thống

### 8.1.1 Phân loại Ngoại lệ Kỹ thuật và Ngoại lệ Nghiệp vụ
<!-- PLACEHOLDER: Quy định tiêu chí phân tách rõ ràng giữa hai nhóm ngoại lệ chính: Ngoại lệ Kỹ thuật (lỗi đọc ghi tệp đĩa cứng, lỗi hỏng định dạng tệp, lỗi bộ nhớ) và Ngoại lệ Nghiệp vụ (vi phạm điều kiện số dư ví không đủ chi, vi phạm hạn mức ngân sách, danh mục trùng tên); phân tích lý do kế thừa RuntimeException trong Java để tránh việc khai báo throws cồng kềnh xuyên suốt các tầng kiến trúc | Căn cứ: Joshua Bloch Effective Java (Item về Exception), Clean Architecture Exception Handling | Tiêu chí hoàn thành: Quy chuẩn rõ ràng lớp nào được phép ném ngoại lệ và lớp nào chịu trách nhiệm bắt giữ ngoại lệ -->

### 8.1.2 Nguyên tắc Bắt và Chuyển đổi Ngoại lệ Xuyên tầng
<!-- PLACEHOLDER: Đặc tả quy trình biến đổi ngoại lệ qua từng ranh giới kiến trúc: Tầng Lưu trữ bắt IOException kỹ thuật và đóng gói thành Ngoại lệ Lưu trữ; Tầng Dịch vụ ném Ngoại lệ Nghiệp vụ có gắn mã lỗi định danh; Tầng Giao diện bắt toàn bộ ngoại lệ tại vòng lặp sự kiện chính để hiển thị thông báo tiếng Việt thân thiện, không để lộ vết ngăn xếp kỹ thuật (stack trace) ra màn hình người dùng | Căn cứ: Craig Larman Chapter 16, Java Exception Chaining Pattern | Tiêu chí hoàn thành: Sơ đồ luồng lan truyền ngoại lệ minh họa việc đóng gói nguyên nhân gốc (cause exception) mà không làm mất thông tin lỗi phục vụ sửa lỗi -->

## 8.2 Cây Phân cấp Lớp Ngoại lệ Hệ thống

### 8.2.1 Sơ đồ Phân cấp Lớp Ngoại lệ Hướng đối tượng
<!-- PLACEHOLDER: Sơ đồ lớp Mermaid thể hiện cây phân cấp ngoại lệ bắt đầu từ lớp cơ sở trừu tượng của hệ thống kế thừa từ RuntimeException: chia thành hai nhánh lớn gồm Nhánh Ngoại lệ Nghiệp vụ và Nhánh Ngoại lệ Kỹ thuật; mỗi lớp ngoại lệ đều có thuộc tính lưu mã định danh lỗi và thời điểm phát sinh lỗi | Căn cứ: ISO/IEC 19505-1, Java Core Exception Hierarchy | Tiêu chí hoàn thành: Cây phân cấp rõ ràng, tuân thủ nguyên lý kế thừa và đa hình, không tạo các lớp ngoại lệ thừa thãi trùng lặp trách nhiệm -->

### 8.2.2 Danh mục các Lớp Ngoại lệ Nghiệp vụ Chi tiết
<!-- PLACEHOLDER: Bảng đặc tả chi tiết từng lớp ngoại lệ nghiệp vụ cụ thể: Ngoại lệ Không Tìm Thấy Thực Thể, Ngoại lệ Không Đủ Số Dư, Ngoại lệ Trùng Lặp Tên Dữ Liệu, Ngoại lệ Vượt Quá Hạn Mức Ngân Sách, Ngoại lệ Hợp Đồng Quá Hạn; mô tả tên lớp, lớp cha trực tiếp, danh sách tham số hàm khởi tạo, và mục đích kích hoạt | Căn cứ: Yêu cầu chức năng 20 ca sử dụng PFMS | Tiêu chí hoàn thành: Bao phủ 100% các tình huống vi phạm quy tắc nghiệp vụ đã được định nghĩa trong phân tích OOA -->

## 8.3 Bảng Mã Lỗi Nghiệp vụ Chuẩn hóa

### 8.3.1 Cấu trúc Định danh Mã Lỗi Hệ thống
<!-- PLACEHOLDER: Quy định cấu trúc tiền tố mã lỗi chuẩn gồm 3 thành phần: nhóm nghiệp vụ (VAL - xác thực dữ liệu, WAL - ví, TXN - giao dịch, BUD - ngân sách, SYS - hệ thống lưu trữ), mã số thứ tự (001 đến 999), và mức độ nghiêm trọng (INFO, WARN, ERROR); bảo đảm mã lỗi có tính duy nhất toàn cục | Căn cứ: Chuẩn thiết kế mã lỗi phần mềm công nghiệp ISO/IEC 25010 | Tiêu chí hoàn thành: Quy tắc định dạng mã lỗi tường minh, giúp việc đối chiếu giữa tài liệu và mã nguồn hoàn toàn khớp nhau -->

### 8.3.2 Danh mục Mã Lỗi và Thông điệp Phản hồi
<!-- PLACEHOLDER: Bảng từ điển mã lỗi hoàn chỉnh của hệ thống PFMS: gồm các cột Mã lỗi, Lớp ngoại lệ kích hoạt, Nguyên nhân kỹ thuật phát sinh, và Thông điệp tiếng Việt hiển thị cho người dùng; hướng dẫn hành động khắc phục cụ thể cho từng lỗi (ví dụ: thông báo số tiền cần nạp thêm khi số dư ví không đủ chi) | Căn cứ: Yêu cầu phản hồi người dùng trong ca sử dụng | Tiêu chí hoàn thành: Đầy đủ 100% các mã lỗi, thông điệp phản hồi lịch sự, chính xác và không dùng từ ngữ kỹ thuật gây khó hiểu cho người dùng cuối -->

## 8.4 Cơ chế Ghi Nhật ký Hoạt động và Kiểm toán

### 8.4.1 Cấu trúc Bản ghi Nhật ký Hệ thống
<!-- PLACEHOLDER: Đặc tả cấu trúc một dòng bản ghi nhật ký (log entry): thời gian chính xác (yyyy-MM-dd HH:mm:ss.SSS), cấp độ nghiêm trọng (INFO cho hoạt động nghiệp vụ thành công, WARN cho cảnh báo ngân sách hoặc nhập sai, ERROR cho sự cố hệ thống), tên lớp phát sinh sự kiện, mã định danh thao tác, và nội dung chi tiết | Căn cứ: Chuẩn ghi nhật ký ứng dụng Java | Tiêu chí hoàn thành: Cấu trúc bản ghi đồng nhất, dễ đọc và hỗ trợ việc trích xuất, tìm kiếm tự động khi cần kiểm tra sự cố -->

### 8.4.2 Chiến lược Ghi Tệp Nhật ký và Lưu vết Kiểm toán
<!-- PLACEHOLDER: Đặc tả cơ chế ghi nhật ký ra tệp văn bản riêng biệt (app.log hoặc audit.log) độc lập với màn hình console của người dùng; cơ chế xoay vòng tệp nhật ký theo ngày hoặc theo dung lượng; quy định bắt buộc ghi nhận lịch sử kiểm toán đối với các giao dịch làm biến động số tiền lớn hoặc các thao tác xóa dữ liệu ví | Căn cứ: Nguyên tắc an toàn dữ liệu tài chính cá nhân | Tiêu chí hoàn thành: Tách biệt hoàn toàn luồng xuất màn hình giao diện (System.out) và luồng ghi vết nhật ký tệp, không làm chậm tốc độ phản hồi của chương trình dòng lệnh -->

