# 06. Áp dụng Nguyên lý SOLID và Mẫu Thiết kế

## 6.1 Đánh giá Tuân thủ Năm Nguyên lý Thiết kế Cơ bản

### 6.1.1 Bảng Đối chiếu Hiện thực Hóa Từng Nguyên lý
<!-- PLACEHOLDER: Bảng đối chiếu 5 nguyên lý thiết kế SOLID (Đơn trách nhiệm, Đóng mở, Thay thế Liskov, Phân tách giao diện, Nghịch đảo phụ thuộc); mỗi nguyên lý gồm các cột: Tên nguyên lý, Mục tiêu thiết kế, Cặp lớp hoặc giao diện điển hình chứng minh việc tuân thủ trong hệ thống PFMS, và Lợi ích thu được trong việc bảo trì mã nguồn | Căn cứ: Robert C. Martin Clean Architecture, Rubric học thuật PTIT | Tiêu chí hoàn thành: 100% cả 5 nguyên lý đều có dẫn chứng cặp lớp/giao diện cụ thể có thật trong hệ thống, không đưa ra lý thuyết suông -->

### 6.1.2 Phân tích Đánh đổi Kỹ thuật khi Áp dụng
<!-- PLACEHOLDER: Trình bày phân tích đánh đổi kỹ thuật thực tế: việc tuân thủ nghiêm ngặt SOLID giúp mã nguồn có độ ghép nối lỏng lẻo và dễ kiểm thử độc lập, nhưng làm gia tăng số lượng lớp và giao diện trong dự án; quy định giới hạn áp dụng hợp lý nhằm tránh việc thiết kế quá mức (over-engineering) cho một đồ án ứng dụng dòng lệnh | Căn cứ: Martin Fowler Refactoring, Joshua Bloch Effective Java | Tiêu chí hoàn thành: Chỉ ra được điểm cân bằng thực tế giữa tính linh hoạt của thiết kế và sự đơn giản cần thiết của mã nguồn -->

## 6.2 Mẫu Nhà máy Khởi tạo Đối tượng

### 6.2.1 Động lực Áp dụng và Cấu trúc Lớp Nhà máy
<!-- PLACEHOLDER: Phân tích động lực áp dụng mẫu Nhà máy (Factory Method): giải quyết bài toán khởi tạo các loại giao dịch khác nhau (Giao dịch thu nhập, Giao dịch chi tiêu, Giao dịch chuyển tiền) từ dữ liệu nhập thô của người dùng mà không để tầng giao diện phụ thuộc trực tiếp vào các lớp con cụ thể; sơ đồ lớp thiết kế chi tiết thể hiện lớp Nhà máy Giao dịch và các phương thức khởi tạo tương ứng | Căn cứ: Erich Gamma GoF Creational Patterns, Craig Larman Creator Pattern | Tiêu chí hoàn thành: Tách biệt hoàn toàn việc tạo đối tượng giao dịch phức tạp khỏi tầng điều phối dịch vụ -->

### 6.2.2 Quy trình Xử lý Khởi tạo Giao dịch Đa dạng
<!-- PLACEHOLDER: Đặc tả quy trình nhận diện tham số và khởi tạo: lớp Nhà máy tiếp nhận đối tượng truyền dữ liệu (DTO) từ tầng giao diện -> kiểm tra loại giao dịch hợp lệ -> gọi hàm khởi tạo lớp con phù hợp -> nạp thông tin ví và danh mục -> trả về đối tượng Giao dịch trừu tượng; cơ chế ném ngoại lệ khi loại giao dịch không hợp lệ | Căn cứ: Joshua Bloch Effective Java (Static factory methods) | Tiêu chí hoàn thành: Đảm bảo nguyên lý Đóng/Mở - khi bổ sung loại giao dịch mới trong tương lai chỉ cần mở rộng lớp con và cập nhật nhà máy mà không sửa đổi tầng giao diện -->

## 6.3 Mẫu Chiến lược Thuật toán Linh hoạt

### 6.3.1 Động lực Áp dụng và Giao diện Chiến lược
<!-- PLACEHOLDER: Phân tích động lực áp dụng mẫu Chiến lược (Strategy Pattern): đóng gói các thuật toán có khả năng thay đổi và tráo đổi lẫn nhau trong lúc ứng dụng đang chạy; đặc tả hai giao diện chiến lược trọng tâm: Giao diện Chiến lược Tính Lãi (cho phép tráo đổi giữa tính lãi đơn, tính lãi kép, tính lãi bậc thang) và Giao diện Chiến lược Xuất Báo cáo (cho phép tráo đổi giữa hiển thị bảng văn bản trên màn hình dòng lệnh và xuất tệp phẳng) | Căn cứ: Erich Gamma GoF Behavioral Patterns, Rubric học thuật PTIT | Tiêu chí hoàn thành: Giao diện chiến lược được định nghĩa độc lập, nhận tham số tính toán và trả về kết quả chuẩn mà không bị phụ thuộc vào trạng thái nội bộ của lớp gọi -->

### 6.3.2 Hiện thực Các Chiến lược Cụ thể
<!-- PLACEHOLDER: Đặc tả chi tiết các lớp cài đặt chiến lược cụ thể: Lớp Tính Lãi Đơn, Lớp Tính Lãi Kép Theo Chu Kỳ; Lớp Xuất Báo Cáo Dòng Lệnh, Lớp Xuất Báo Cáo Tệp Dữ Liệu; sơ đồ lớp thể hiện mối quan hệ giữa lớp Ngữ cảnh (Context như Sổ Tiết kiệm hoặc Dịch vụ Báo cáo) và các lớp Chiến lược | Căn cứ: Chuẩn tính toán tài chính, Martin Fowler Strategy Pattern | Tiêu chí hoàn thành: Chứng minh việc chuyển đổi linh hoạt giữa các thuật toán thông qua cấu hình hoặc lựa chọn của người dùng tại thời điểm chạy chương trình -->

## 6.4 Mẫu Trạng thái Vòng đời Nghiệp vụ

### 6.4.1 Động lực Áp dụng và Máy Trạng thái Hướng đối tượng
<!-- PLACEHOLDER: Phân tích động lực áp dụng mẫu Trạng thái (State Pattern): quản lý hành vi của thực thể Hợp đồng Vay Nợ và Sổ Tiết kiệm thay đổi tùy theo trạng thái vòng đời hiện tại của nó; thay thế toàn bộ các khối lệnh điều kiện phức tạp (if-else hoặc switch-case theo mã trạng thái) bằng cấu trúc đa hình hướng đối tượng | Căn cứ: Erich Gamma GoF State Pattern, Mô hình máy trạng thái OOA | Tiêu chí hoàn thành: Mỗi trạng thái vòng đời được đóng gói thành một lớp riêng biệt, kế thừa từ một giao diện trạng thái chung -->

### 6.4.2 Chuyển đổi Trạng thái Sổ Tiết kiệm và Hợp đồng Vay Nợ
<!-- PLACEHOLDER: Sơ đồ lớp và bảng chuyển đổi trạng thái chi tiết: các trạng thái của Sổ Tiết kiệm (Đang tích lũy, Đã đến hạn tất toán, Đã tất toán trước hạn); các trạng thái của Hợp đồng Vay Nợ (Đang có hiệu lực, Quá hạn trả nợ, Đã hoàn tất thanh toán); đặc tả hành vi của các phương thức như rút tiền, thanh toán đợt nợ tại từng trạng thái cụ thể | Căn cứ: Quy tắc nghiệp vụ hợp đồng tài chính, ISO/IEC 19505 State Machine Diagram | Tiêu chí hoàn thành: Khóa chặt các thao tác không hợp lệ tại từng trạng thái (ví dụ: không cho phép thanh toán khoản nợ đã hoàn tất) bằng ngoại lệ nghiệp vụ có kiểm soát -->

## 6.5 Mẫu Quan sát Đồng bộ Dữ liệu

### 6.5.1 Động lực Áp dụng và Cơ chế Đăng ký Lắng nghe
<!-- PLACEHOLDER: Phân tích động lực áp dụng mẫu Quan sát (Observer Pattern / Publish-Subscribe): thiết lập mối liên kết lỏng lẻo giữa đối tượng phát ra sự kiện (Subject) và các đối tượng có nhu cầu lắng nghe sự kiện (Observer); đặc tả giao diện Đối tượng Lắng nghe Sự kiện Giao dịch và cơ chế đăng ký, hủy đăng ký lắng nghe trong Dịch vụ Giao dịch | Căn cứ: Erich Gamma GoF Observer Pattern, Kiến trúc hướng sự kiện lỏng lẻo | Tiêu chí hoàn thành: Đối tượng phát sự kiện hoàn toàn không biết chi tiết cụ thể của đối tượng lắng nghe, chỉ gửi thông điệp thông qua giao diện chung -->

### 6.5.2 Đồng bộ Số dư Ví và Kích hoạt Cảnh báo Ngân sách
<!-- PLACEHOLDER: Đặc tả hai lớp lắng nghe cụ thể: Lớp Lắng nghe Cập nhật Số dư Ví (tự động kích hoạt việc ghi nhận số dư tức thời khi giao dịch được xác nhận) và Lớp Lắng nghe Cảnh báo Hạn mức Ngân sách (tự động so sánh số tiền lũy kế với hạn mức ngân sách và phát tín hiệu cảnh báo ra màn hình dòng lệnh nếu chi tiêu vượt quá 80% hoặc 100%); sơ đồ tuần tự mô tả luồng phát sự kiện và xử lý bất đồng bộ hoặc tuần tự | Căn cứ: Ca sử dụng ghi nhận giao dịch và cảnh báo ngân sách | Tiêu chí hoàn thành: Loại bỏ hoàn toàn việc gọi trực tiếp cứng giữa Dịch vụ Giao dịch và Dịch vụ Ngân sách, giữ tính gắn kết cao và ghép nối lỏng lẻo -->

