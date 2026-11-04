# 04. Thiết kế Tầng Lưu trữ và Truy xuất Dữ liệu

## 4.1 Nguyên tắc Thiết kế Mẫu Kho Lưu trữ

### 4.1.1 Cô lập Cơ chế Lưu trữ khỏi Tầng Nghiệp vụ
<!-- PLACEHOLDER: Trình bày vai trò của mẫu Kho lưu trữ (Repository) trong việc tạo ra một lớp trừu tượng mô phỏng tập hợp các đối tượng trong bộ nhớ; giải thích lý do tầng nghiệp vụ không được phép thao tác trực tiếp với các luồng đọc ghi tệp (FileInputStream, BufferedReader) hoặc đường dẫn tệp cứng; chứng minh việc áp dụng nguyên lý Nghịch đảo Phụ thuộc (DIP) giúp hệ thống có thể tráo đổi nơi lưu dữ liệu mà không làm thay đổi một dòng mã nguồn nào của tầng nghiệp vụ | Căn cứ: Martin Fowler PoEAA Chapter 10 (Repository Pattern), Robert C. Martin Clean Architecture | Tiêu chí hoàn thành: Nêu rõ ranh giới: Tầng Nghiệp vụ chỉ biết đến Giao diện Kho lưu trữ, toàn bộ chi tiết công nghệ tệp được đóng kín trong Tầng Hạ tầng -->

### 4.1.2 Chiến lược Ánh xạ Đối tượng và Bản ghi Dữ liệu
<!-- PLACEHOLDER: Quy định cơ chế chuyển đổi hai chiều giữa Đối tượng Thực thể trong bộ nhớ (Domain Entity với các thuộc tính hướng đối tượng phức tạp như Money, DateRange) và Bản ghi lưu trữ dạng phẳng (chuỗi văn bản CSV phân tách bởi dấu phẩy hoặc nút dữ liệu JSON); quy tắc quản lý khóa chính định danh duy nhất (UUID) để đối chiếu chính xác bản ghi | Căn cứ: Martin Fowler Data Mapper Pattern, Chuẩn định dạng RFC 4180 (CSV) và RFC 8259 (JSON) | Tiêu chí hoàn thành: Không để rò rỉ cấu trúc tệp phẳng vào thuộc tính của lớp thực thể; xử lý an toàn các ký tự đặc biệt (dấu phẩy, dấu xuống dòng) trong nội dung ghi chú -->

## 4.2 Giao diện Lưu trữ Tổng quát

### 4.2.1 Thiết kế Giao diện Khung Dùng chung
<!-- PLACEHOLDER: Thiết kế chi tiết giao diện trừu tượng tổng quát tham số hóa bằng Generic trong Java: nhận kiểu thực thể T và kiểu định danh ID; đặc tả các chữ ký hàm cơ bản: lưu mới hoặc cập nhật (save), tìm kiếm theo định danh (findById trả về Optional<T>), lấy toàn bộ danh sách (findAll), kiểm tra tồn tại (existsById), và xóa theo định danh (deleteById); quy định việc trả về Optional để chống lỗi null | Căn cứ: Joshua Bloch Effective Java (Item về Generics và Optional), Spring Data Repository Design Conventions | Tiêu chí hoàn thành: Khung giao diện dùng chung hoàn chỉnh, có đầy đủ kiểu dữ liệu tham số hóa, không phụ thuộc vào bất kỳ thư viện bên ngoài nào -->

### 4.2.2 Quy định Ngoại lệ và Trạng thái Trả về
<!-- PLACEHOLDER: Quy định các ngoại lệ có thể phát sinh từ tầng lưu trữ (như lỗi không tìm thấy thực thể, lỗi hỏng định dạng tệp, lỗi khóa truy cập tệp) và cơ chế bọc các lỗi kỹ thuật này vào lớp ngoại lệ lưu trữ chung của hệ thống trước khi truyền ngược lên tầng trên | Căn cứ: Java Exception Handling Best Practices, Danh mục mã lỗi PFMS | Tiêu chí hoàn thành: Tầng lưu trữ không ném trực tiếp lỗi IOException nguyên thủy của hệ thống lên tầng giao diện; 100% phương thức tìm kiếm đơn lẻ đều trả về Optional an toàn -->

## 4.3 Các Giao diện Lưu trữ Chuyên biệt theo Thực thể

### 4.3.1 Kho Lưu trữ Ví
<!-- PLACEHOLDER: Đặc tả giao diện mở rộng chuyên biệt cho thực thể Ví kế thừa từ giao diện dùng chung: các phương thức truy vấn nghiệp vụ bổ sung như tìm danh sách ví theo người dùng, tìm ví theo tên, và kiểm tra tên ví có bị trùng lặp hay không; các phương thức cập nhật nhanh số dư khả dụng | Căn cứ: Yêu cầu chức năng quản lý ví, Cấu trúc dữ liệu danh mục | Tiêu chí hoàn thành: Đầy đủ chữ ký phương thức, kiểu tham số đầu vào và kiểu danh sách trả về, phục vụ trực tiếp cho Dịch vụ Ví -->

### 4.3.2 Kho Lưu trữ Giao dịch
<!-- PLACEHOLDER: Đặc tả giao diện mở rộng chuyên biệt cho thực thể Giao dịch kế thừa từ giao diện dùng chung: các phương thức truy vấn nghiệp vụ nâng cao như tìm danh sách giao dịch theo mã ví, lọc giao dịch theo khoảng ngày, lọc giao dịch theo danh mục thu/chi, và tính tổng số tiền phát sinh theo điều kiện lọc | Căn cứ: Yêu cầu chức năng ghi nhận và báo cáo thu chi, Martin Fowler Accounting Patterns | Tiêu chí hoàn thành: Cung cấp các phương thức lọc dữ liệu tối ưu, phục vụ việc tổng hợp báo cáo mà không cần nạp toàn bộ lịch sử giao dịch vào bộ nhớ máy ảo -->

### 4.3.3 Kho Lưu trữ Danh mục và Ngân sách
<!-- PLACEHOLDER: Đặc tả giao diện chuyên biệt cho Danh mục (tìm theo loại thu/chi, tìm danh mục con theo mã cha) và Ngân sách (tìm ngân sách đang hoạt động trong khoảng thời gian hiện tại, tìm ngân sách gắn với một danh mục cụ thể) | Căn cứ: Yêu cầu phân loại và quản lý hạn mức ngân sách | Tiêu chí hoàn thành: Đáp ứng đầy đủ các truy vấn kiểm tra điều kiện ngân sách trước khi ghi nhận một khoản chi tiêu mới -->

## 4.4 Thiết kế Bộ Điều hợp Lưu trữ Tệp

### 4.4.1 Cấu trúc Định dạng Bản ghi Tệp Dữ liệu
<!-- PLACEHOLDER: Đặc tả cấu trúc tệp lưu trữ thực tế trên đĩa cứng: thiết kế bảng định dạng từng cột cho các tệp dữ liệu phẳng (ví dụ: wallets.csv, transactions.csv, categories.csv, budgets.csv); quy định định dạng trường ngày tháng chuẩn (yyyy-MM-dd HH:mm:ss), định dạng số tiền nguyên/thập phân không chứa ký tự tiền tệ gây nhiễu, quy tắc mã hóa ký tự UTF-8 không BOM | Căn cứ: Chuẩn RFC 4180 về CSV, Chuẩn ISO 8601 về ngày tháng, Chuẩn RFC 3629 về UTF-8 | Tiêu chí hoàn thành: Bảng đặc tả thứ tự cột, kiểu dữ liệu, ví dụ mẫu dữ liệu thô cho từng tệp dữ liệu của hệ thống -->

### 4.4.2 Cơ chế Đọc Ghi và An toàn Hệ thống Tệp
<!-- PLACEHOLDER: Đặc tả cơ chế đọc/ghi tệp tin cậy: quy trình ghi tệp an toàn sử dụng cơ chế tệp tạm (ghi ra file tạm trước, khi thành công mới đổi tên đè vào file chính) nhằm chống mất mát dữ liệu khi ứng dụng bị dừng đột ngột; cơ chế tự động khởi tạo tệp rỗng có dòng tiêu đề chuẩn khi ứng dụng chạy lần đầu; tiện ích sao lưu định kỳ | Căn cứ: Java NIO File System Best Practices, Cơ chế ghi tệp an toàn | Tiêu chí hoàn thành: Quy trình kiểm soát lỗi tệp chi tiết, bảo đảm tệp dữ liệu không bao giờ bị hỏng cấu trúc (corrupted file) trong quá trình vận hành -->

## 4.5 Thiết kế Bộ Điều hợp Lưu trữ Bộ nhớ

### 4.5.1 Cài đặt Lưu trữ Tạm thời phục vụ Kiểm thử Tự động
<!-- PLACEHOLDER: Đặc tả các lớp cài đặt Kho lưu trữ hoạt động hoàn toàn trên bộ nhớ RAM (sử dụng cấu trúc dữ liệu Java ConcurrentHashMap hoặc HashMap); phục vụ trực tiếp cho việc chạy hàng trăm bài kiểm thử đơn vị JUnit 5 với tốc độ mili-giây mà không phụ thuộc vào hệ thống tệp đĩa cứng và không để lại rác dữ liệu sau khi test | Căn cứ: Martin Fowler Test-Driven Development (TDD), Rubric học thuật PTIT (Bộ sưu tập dữ liệu Java Collections) | Tiêu chí hoàn thành: Cài đặt đúng 100% hợp đồng của các giao diện kho lưu trữ; khả năng xóa sạch dữ liệu sau mỗi ca kiểm thử (phương thức clear) -->

### 4.5.2 So sánh Đánh đổi giữa Hai Cơ chế Lưu trữ
<!-- PLACEHOLDER: Bảng so sánh định lượng giữa Bộ điều hợp Tệp và Bộ điều hợp Bộ nhớ theo các tiêu chí: tốc độ truy xuất, tính bền vững của dữ liệu khi tắt chương trình, độ phức tạp cài đặt, và trường hợp sử dụng phù hợp trong vòng đời dự án; chứng minh sự linh hoạt của kiến trúc khi cho phép chuyển đổi chế độ lưu trữ thông qua cấu hình hệ thống | Căn cứ: Nguyên lý thiết kế phần mềm, Clean Architecture | Tiêu chí hoàn thành: Bảng đối chiếu rõ ràng giúp người đọc đồ án hiểu rõ lý do tại sao hệ thống cần cả hai bộ điều hợp -->

