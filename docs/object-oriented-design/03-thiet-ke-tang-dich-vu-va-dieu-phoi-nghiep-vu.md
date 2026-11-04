# 03. Thiết kế Tầng Dịch vụ và Điều phối Nghiệp vụ

## 3.1 Nguyên tắc Phân định Trách nhiệm Tầng Dịch vụ

### 3.1.1 Áp dụng Mẫu Điều phối viên và Chuyên gia Thông tin
<!-- PLACEHOLDER: Đặc tả việc áp dụng hai mẫu cốt lõi trong bộ mẫu GRASP: mẫu Điều phối viên (tiếp nhận yêu cầu từ tầng giao diện, điều phối luồng xử lý nhưng không trực tiếp làm toán) và mẫu Chuyên gia Thông tin (ủy quyền tính toán cho chính lớp nắm giữ dữ liệu như Ví hoặc Ngân sách); giải thích lý do tránh mô hình Dịch vụ nghèo nàn (Anemic Domain Model) | Căn cứ: Craig Larman Chapters 16-17, Martin Fowler Service Layer | Tiêu chí hoàn thành: Nêu rõ ranh giới: Tầng Dịch vụ chỉ điều phối luồng nghiệp vụ và gọi phương thức của Thực thể, không lặp lại các công thức tính toán đã có trong Thực thể -->

### 3.1.2 Phân biệt Dịch vụ Điều phối Ứng dụng và Dịch vụ Nghiệp vụ Chuyên sâu
<!-- PLACEHOLDER: Quy định tiêu chí phân tách rõ ràng giữa Dịch vụ Điều phối Ứng dụng (chịu trách nhiệm trực tiếp cho một ca sử dụng, mở rộng phạm vi giao tiếp với kho lưu trữ) và Dịch vụ Nghiệp vụ Chuyên sâu (xử lý logic tài chính phức tạp liên quan đồng thời nhiều thực thể độc lập mà không thuộc về riêng thực thể nào, như thuật toán tính lãi phức hợp hoặc kiểm toán số dư chéo) | Căn cứ: Eric Evans DDD Chapter 4, Martin Fowler PoEAA | Tiêu chí hoàn thành: 100% các lớp dịch vụ được phân loại dứt khoát vào một trong hai nhóm, không có lớp nào vừa làm nhiệm vụ điều phối ca sử dụng vừa ôm đồm thuật toán tài chính chuyên sâu -->

## 3.2 Hệ thống Giao diện và Hợp đồng Dịch vụ

### 3.2.1 Nguyên tắc Lập trình Hướng Giao diện
<!-- PLACEHOLDER: Quy định nguyên tắc mọi thành phần phụ thuộc vào tầng dịch vụ đều phải thông qua Giao diện (Interface); tách biệt hoàn toàn giữa chữ ký hợp đồng công khai và lớp cài đặt cụ thể; tạo điều kiện thuận lợi cho việc kiểm thử tự động bằng cách thay thế lớp giả lập (Mock/Stub) | Căn cứ: Nguyên lý SOLID Phụ thuộc vào Trừu tượng (DIP), Joshua Bloch Effective Java | Tiêu chí hoàn thành: Mọi dịch vụ đều có cặp thành phần: Giao diện định nghĩa phương thức và Lớp cài đặt cụ thể; tầng giao diện dòng lệnh chỉ được khai báo kiểu biến là Giao diện -->

### 3.2.2 Cấu trúc Chuẩn hóa của một Hợp đồng Phương thức
<!-- PLACEHOLDER: Quy định cấu trúc đặc tả 4 thành phần bắt buộc cho mỗi phương thức dịch vụ: (1) Tên phương thức và danh sách tham số đầu vào (sử dụng mã định danh hoặc đối tượng truyền dữ liệu DTO); (2) Kiểu dữ liệu trả về; (3) Điều kiện tiên quyết (những điều kiện dữ liệu phải thỏa mãn trước khi xử lý); (4) Điều kiện sau và ngoại lệ nghiệp vụ (trạng thái hệ thống thay đổi như thế nào sau khi chạy xong, các ngoại lệ có thể ném ra) | Căn cứ: Bertrand Meyer Design by Contract, ISO/IEC 29148 | Tiêu chí hoàn thành: Đưa ra khung mẫu đặc tả phương thức chuẩn để áp dụng đồng bộ cho toàn bộ các mục dịch vụ bên dưới -->

## 3.3 Thiết kế Nhóm Dịch vụ Thực thi Tài chính

### 3.3.1 Dịch vụ Quản lý Ví
<!-- PLACEHOLDER: Đặc tả chi tiết giao diện và lớp cài đặt dịch vụ Ví: các phương thức tạo mới ví, truy vấn danh sách ví theo người dùng, cập nhật thông tin ví, kiểm tra số dư khả dụng, và vô hiệu hóa ví; cơ chế kiểm tra điều kiện không cho phép xóa ví nếu đang còn số dư lớn hơn 0 hoặc đang có ràng buộc giao dịch | Căn cứ: Yêu cầu chức năng quản lý ví, Rubric học thuật PTIT | Tiêu chí hoàn thành: Đầy đủ chữ ký phương thức, kiểu trả về, điều kiện tiên quyết và các ngoại lệ ném ra khi không tìm thấy ví hoặc số dư không hợp lệ -->

### 3.3.2 Dịch vụ Ghi nhận Thu Chi
<!-- PLACEHOLDER: Đặc tả chi tiết giao diện và lớp cài đặt dịch vụ Thu Chi: các phương thức tạo mới giao dịch thu, tạo mới giao dịch chi, sửa đổi thông tin giao dịch và hủy/xóa giao dịch; quy trình điều phối: nạp ví từ kho lưu trữ -> gọi phương thức trừ/cộng tiền của ví -> lưu giao dịch mới -> cập nhật lại ví vào kho lưu trữ -> kích hoạt kiểm tra ngân sách | Căn cứ: Craig Larman Information Expert, Martin Fowler Accounting Patterns | Tiêu chí hoàn thành: Đảm bảo tính nhất quán tuyệt đối giữa số dư trong ví và tổng các giao dịch phát sinh; mô tả chi tiết luồng xử lý ngoại lệ khi số dư ví không đủ chi -->

### 3.3.3 Dịch vụ Chuyển tiền Nội bộ
<!-- PLACEHOLDER: Đặc tả chi tiết giao diện và lớp cài đặt dịch vụ Chuyển tiền giữa hai ví: phương thức thực hiện chuyển tiền với các tham số: ví nguồn, ví đích, số tiền chuyển, phí giao dịch (nếu có), ghi chú; điều phối kiểm tra số dư ví nguồn, trừ tiền ví nguồn, cộng tiền ví đích, và ghi nhận bản ghi giao dịch chuyển tiền liên kết kép | Căn cứ: Quy tắc an toàn giao dịch tài chính, Nguyên lý tính toán số dư | Tiêu chí hoàn thành: Đảm bảo tính nguyên tử: nếu một trong hai thao tác ví thất bại (như lỗi ghi tệp) thì toàn bộ giao dịch phải được hủy bỏ, không làm lệch tiền của hệ thống -->

## 3.4 Thiết kế Nhóm Dịch vụ Kế hoạch và Phân tích

### 3.4.1 Dịch vụ Danh mục Phân loại
<!-- PLACEHOLDER: Đặc tả chi tiết giao diện và lớp cài đặt dịch vụ Danh mục: các phương thức thêm danh mục, sửa thông tin, truy vấn cây danh mục phân cấp cha-con, và thiết lập biểu tượng; cơ chế kiểm tra tính duy nhất của tên danh mục trong cùng một nhóm thu/chi và ngăn chặn việc tạo chu trình vòng lặp cha-con | Căn cứ: Yêu cầu chức năng phân loại giao dịch, Cấu trúc cây dữ liệu | Tiêu chí hoàn thành: Khóa ràng buộc không cho phép xóa danh mục mặc định của hệ thống; xử lý trường hợp chuyển đổi giao dịch sang danh mục khác khi xóa danh mục con -->

### 3.4.2 Dịch vụ Hạn mức Ngân sách
<!-- PLACEHOLDER: Đặc tả chi tiết giao diện và lớp cài đặt dịch vụ Ngân sách: các phương thức thiết lập ngân sách theo chu kỳ (tuần/tháng), gắn danh mục chi tiêu vào ngân sách, tính toán tỷ lệ tiêu hao hiện tại, và kiểm tra ngưỡng cảnh báo (80%, 100%); phương thức tự động tổng hợp số tiền đã chi trong khoảng thời gian áp dụng | Căn cứ: Quy tắc nghiệp vụ ngân sách PFMS, Craig Larman Information Expert | Tiêu chí hoàn thành: Cung cấp đầy đủ các phương thức truy vấn trạng thái cảnh báo phục vụ tầng giao diện hiển thị thông báo trực quan cho người dùng -->

### 3.4.3 Dịch vụ Báo cáo và Thống kê Tài chính
<!-- PLACEHOLDER: Đặc tả chi tiết giao diện và lớp cài đặt dịch vụ Báo cáo: phương thức tổng hợp thu chi theo khoảng thời gian, phân tích cơ cấu chi tiêu theo danh mục, tính toán số dư ròng, và lập bảng xu hướng thu chi; cơ chế trích xuất dữ liệu tổng hợp đóng gói vào các đối tượng DTO báo cáo chuyên biệt | Căn cứ: Yêu cầu chức năng thống kê báo cáo, Martin Fowler DTO Pattern | Tiêu chí hoàn thành: Dịch vụ chỉ thực hiện truy vấn và tính toán số liệu thống kê, tuyệt đối không can thiệp làm thay đổi trạng thái của các thực thể dữ liệu -->

## 3.5 Quản lý Giao dịch và An toàn Dữ liệu

### 3.5.1 Kiểm soát Tính Nguyên tử trong Biến đổi Dữ liệu
<!-- PLACEHOLDER: Đặc tả cơ chế bảo đảm tính toàn vẹn và nguyên tử (thành công toàn bộ hoặc không thay đổi gì) khi một nghiệp vụ cần ghi dữ liệu vào nhiều kho lưu trữ khác nhau; quy định giải pháp hoàn tác thủ công hoặc cơ chế sao lưu tạm thời trước khi ghi dữ liệu xuống tệp | Căn cứ: Nguyên lý ACID trong kỹ thuật phần mềm, Java Exception Handling | Tiêu chí hoàn thành: Quy trình xử lý lỗi rõ ràng khi xảy ra sự cố gián đoạn (như mất điện hoặc lỗi ghi ổ đĩa), bảo đảm hệ thống không bao giờ bị rơi vào trạng thái lệch số dư -->

### 3.5.2 Xác thực Dữ liệu và Kiểm soát Điều kiện Tiên quyết
<!-- PLACEHOLDER: Quy định các bước xác thực điều kiện tiên quyết tập trung tại tầng dịch vụ trước khi cho phép dữ liệu đi vào tầng nghiệp vụ cốt lõi: kiểm tra quyền truy cập tài nguyên, kiểm tra tính hợp lệ của mã định danh, kiểm tra xung đột thời gian; cơ chế ném ngoại lệ nghiệp vụ có định danh mã lỗi thống nhất | Căn cứ: Bertrand Meyer Design by Contract, Danh mục mã lỗi PFMS | Tiêu chí hoàn thành: Loại bỏ hoàn toàn các lỗi dữ liệu không hợp lệ ngay từ cửa ngõ tầng dịch vụ, bảo vệ tầng thực thể luôn ở trạng thái toàn vẹn -->

