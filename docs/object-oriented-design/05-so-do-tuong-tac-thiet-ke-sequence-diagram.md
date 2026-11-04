# 05. Sơ đồ Tuần tự Thiết kế

## 5.1 Nguyên tắc Thiết kế Sơ đồ Tuần tự Chi tiết

### 5.1.1 Chuyển đổi từ Sơ đồ Tuần tự Hệ thống sang Sơ đồ Tuần tự Chi tiết
<!-- PLACEHOLDER: Trình bày phương pháp luận mở rộng từ Sơ đồ Tuần tự Hệ thống trong phân tích (hộp đen, nơi hệ thống chỉ là một đối tượng duy nhất) sang Sơ đồ Tuần tự Thiết kế (hộp trắng, phân rã luồng tương tác giữa các đối tượng cụ thể trong 4 tầng kiến trúc); quy định thứ tự các cột đối tượng tham gia tương tác: từ trái qua phải bắt đầu từ Người dùng -> Giao diện Dòng lệnh -> Bộ Điều phối Dịch vụ -> Thực thể Nghiệp vụ -> Kho Lưu trữ Dữ liệu | Căn cứ: Craig Larman Chapter 15, ISO/IEC 19505-1 | Tiêu chí hoàn thành: 100% các thông điệp gửi đi trên sơ đồ tuần tự đều phải là tên phương thức có thật và khớp chữ ký với Sơ đồ Lớp Thiết kế -->

### 5.1.2 Quy chuẩn Ký hiệu và Khung Điều khiển Tương tác
<!-- PLACEHOLDER: Đặc tả các quy chuẩn ký hiệu trực quan của UML Sequence Diagram theo chuẩn Mermaid: đường thẳng đứng biểu diễn vòng đời đối tượng (lifeline), thanh kích hoạt thời gian thực thi (activation bar), mũi tên nét liền cho lời gọi hàm đồng bộ, mũi tên nét đứt cho kết quả trả về; quy định bắt buộc sử dụng các khung điều khiển tiêu chuẩn: alt (rẽ nhánh điều kiện thành công / thất bại), loop (vòng lặp duyệt danh sách), opt (hành động tùy chọn như gửi cảnh báo vượt hạn mức), ref (tham chiếu đến sơ đồ con) | Căn cứ: ISO/IEC 19505-1 chuẩn UML 2.5 | Tiêu chí hoàn thành: Thể hiện tường minh luồng rẽ nhánh xử lý ngoại lệ trong từng kịch bản, không vẽ sơ đồ theo kiểu chỉ có đường chạy thành công duy nhất (happy path) -->

## 5.2 Sơ đồ Tuần tự Nhóm Quản lý Ví

### 5.2.1 Sơ đồ Tạo Mới Ví và Thiết lập Số dư Ban đầu
<!-- PLACEHOLDER: Sơ đồ tuần tự chi tiết mô tả luồng tương tác khi người dùng tạo một ví mới (chọn loại ví tiền mặt, ngân hàng, ví điện tử); thể hiện tương tác: Giao diện dòng lệnh nhận lệnh -> Dịch vụ Ví xác thực tên ví không trùng lặp -> tạo đối tượng Ví con tương ứng -> gọi Kho lưu trữ Ví lưu xuống tệp -> phản hồi thông báo thành công; khung alt thể hiện trường hợp tên ví bị trùng lặp | Căn cứ: Ca sử dụng UC-WAL-01, Rubric học thuật PTIT | Tiêu chí hoàn thành: Sơ đồ Mermaid hợp lệ, thể hiện rõ việc đóng gói số dư ban đầu trong đối tượng Tiền tệ -->

### 5.2.2 Sơ đồ Điều chỉnh và Khóa Ví
<!-- PLACEHOLDER: Sơ đồ tuần tự mô tả luồng cập nhật thông tin ví và vô hiệu hóa ví: Dịch vụ Ví nạp ví từ kho lưu trữ -> kiểm tra điều kiện (ví dụ: không cho phép khóa nếu đang còn số dư lớn hơn 0) -> cập nhật trạng thái ví -> lưu lại kho lưu trữ; khung alt thể hiện trường hợp ví không tồn tại hoặc vi phạm điều kiện khóa ví | Căn cứ: Ca sử dụng UC-WAL-03 và UC-WAL-04 | Tiêu chí hoàn thành: Thể hiện đúng việc ném ngoại lệ nghiệp vụ khi số dư ví không thỏa mãn điều kiện vô hiệu hóa -->

## 5.3 Sơ đồ Tuần tự Nhóm Ghi nhận Thu Chi

### 5.3.1 Sơ đồ Ghi nhận Giao dịch Chi tiêu
<!-- PLACEHOLDER: Sơ đồ tuần tự chi tiết mô tả luồng ghi nhận một khoản chi tiêu: Giao diện nhập thông tin chi tiêu -> Dịch vụ Giao dịch nạp Ví từ Kho lưu trữ Ví -> nạp Danh mục từ Kho lưu trữ Danh mục -> khởi tạo đối tượng Giao dịch Chi tiêu -> gọi phương thức trừ tiền của Ví -> gọi Kho lưu trữ Giao dịch lưu giao dịch mới -> gọi Kho lưu trữ Ví cập nhật số dư -> gọi Dịch vụ Ngân sách kiểm tra ngưỡng tiêu hao; khung alt kiểm tra số dư ví không đủ chi; khung opt kích hoạt cảnh báo ngân sách | Căn cứ: Ca sử dụng UC-TXN-02, Craig Larman Information Expert | Tiêu chí hoàn thành: Thể hiện đầy đủ luồng tác động liên hoàn giữa Ví, Giao dịch và Ngân sách; kiểm tra đầy đủ các nhánh lỗi -->

### 5.3.2 Sơ đồ Ghi nhận Giao dịch Thu nhập và Hoàn tác
<!-- PLACEHOLDER: Sơ đồ tuần tự mô tả luồng ghi nhận khoản thu nhập (cộng tiền vào ví) và luồng hoàn tác/xóa một giao dịch đã ghi nhận: Dịch vụ Giao dịch nạp giao dịch cũ -> nạp ví liên kết -> gọi phương thức hoàn tác của giao dịch để phục hồi số dư ví -> xóa giao dịch khỏi kho lưu trữ -> lưu lại trạng thái ví đã phục hồi | Căn cứ: Ca sử dụng UC-TXN-01 và UC-TXN-04 | Tiêu chí hoàn thành: Thể hiện tính đối xứng chính xác giữa thao tác ghi nhận và thao tác hoàn tác giao dịch tài chính -->

## 5.4 Sơ đồ Tuần tự Nhóm Chuyển tiền Nội bộ

### 5.4.1 Sơ đồ Chuyển tiền giữa Hai Ví
<!-- PLACEHOLDER: Sơ đồ tuần tự chi tiết mô tả luồng chuyển tiền giữa hai ví của cùng một người dùng: Dịch vụ Chuyển tiền nạp ví nguồn và ví đích -> kiểm tra ví nguồn khác ví đích -> kiểm tra số dư ví nguồn có đủ tiền chuyển và phí -> trừ tiền ví nguồn -> cộng tiền ví đích -> tạo bản ghi Giao dịch Chuyển tiền liên kết hai ví -> lưu đồng thời hai ví và giao dịch vào các kho lưu trữ tương ứng; khung alt thể hiện nhánh lỗi nếu hai ví trùng nhau hoặc ví nguồn thiếu tiền | Căn cứ: Ca sử dụng UC-TRF-01, Quy tắc giao dịch chuyển tiền | Tiêu chí hoàn thành: Thể hiện rõ việc đảm bảo tính toàn vẹn: nếu việc cộng tiền ví đích hoặc lưu bản ghi gặp sự cố thì hệ thống phải phục hồi lại số dư ban đầu của ví nguồn -->

## 5.5 Sơ đồ Tuần tự Nhóm Danh mục và Ngân sách

### 5.5.1 Sơ đồ Quản lý Danh mục Thu Chi
<!-- PLACEHOLDER: Sơ đồ tuần tự mô tả luồng thêm danh mục mới và luồng xóa danh mục: Dịch vụ Danh mục kiểm tra tên danh mục duy nhất trong cùng nhóm -> kiểm tra danh mục cha hợp lệ -> lưu danh mục mới; đối với thao tác xóa: kiểm tra danh mục có chứa giao dịch liên kết hay không, khung alt xử lý việc chuyển giao dịch sang danh mục mặc định khác trước khi xóa | Căn cứ: Ca sử dụng UC-CAT-01 và UC-CAT-03 | Tiêu chí hoàn thành: Bảo đảm không để lại giao dịch mồ côi không có danh mục quản lý -->

### 5.5.2 Sơ đồ Thiết lập và Kiểm soát Hạn mức Ngân sách
<!-- PLACEHOLDER: Sơ đồ tuần tự mô tả luồng tạo lập ngân sách cho một danh mục trong tháng và luồng tự động kiểm tra tỷ lệ tiêu hao: Dịch vụ Ngân sách truy vấn tổng các giao dịch chi tiêu của danh mục trong chu kỳ -> tính toán tỷ lệ phần trăm đã tiêu -> so sánh với hạn mức; khung opt phát thông báo cảnh báo khi chi tiêu vượt quá 80% hoặc vượt quá 100% hạn mức | Căn cứ: Ca sử dụng UC-BUD-01 và UC-BUD-03 | Tiêu chí hoàn thành: Minh họa rõ cơ chế kiểm tra cảnh báo tự động không làm gián đoạn luồng ghi nhận giao dịch -->

## 5.6 Sơ đồ Tuần tự Nhóm Báo cáo và Quản trị Dữ liệu

### 5.6.1 Sơ đồ Tổng hợp Báo cáo Thu Chi Định kỳ
<!-- PLACEHOLDER: Sơ đồ tuần tự mô tả luồng tạo báo cáo tài chính: Giao diện yêu cầu báo cáo kèm khoảng thời gian lọc -> Dịch vụ Báo cáo truy vấn toàn bộ giao dịch thỏa mãn điều kiện thời gian từ Kho lưu trữ Giao dịch -> nhóm giao dịch theo từng danh mục -> tính tổng thu, tổng chi và số dư ròng -> đóng gói toàn bộ kết quả tính toán vào đối tượng truyền dữ liệu DTO Báo cáo -> chuyển sang Giao diện Dòng lệnh để vẽ bảng hiển thị | Căn cứ: Ca sử dụng UC-REP-01 và UC-REP-02 | Tiêu chí hoàn thành: Chứng minh việc truy vấn báo cáo là thao tác chỉ đọc (read-only), không làm biến đổi trạng thái của bất kỳ thực thể nào trong hệ thống -->

### 5.6.2 Sơ đồ Sao lưu và Phục hồi Dữ liệu Hệ thống
<!-- PLACEHOLDER: Sơ đồ tuần tự mô tả luồng sao lưu toàn bộ dữ liệu ra tệp lưu trữ dự phòng và luồng phục hồi dữ liệu từ bản sao lưu: Dịch vụ Dữ liệu khóa tạm thời các thao tác ghi -> nạp toàn bộ dữ liệu từ các kho lưu trữ -> đóng gói thành tệp nén hoặc tệp sao lưu có gắn nhãn thời gian -> lưu vào thư mục dự phòng; luồng phục hồi kiểm tra tính toàn vẹn của tệp sao lưu trước khi ghi đè lại hệ thống | Căn cứ: Ca sử dụng UC-DAT-01 và UC-DAT-02 | Tiêu chí hoàn thành: Quy trình kiểm tra tính hợp lệ của dữ liệu trước khi phục hồi, bảo đảm không nhập dữ liệu hỏng làm lỗi hệ thống -->

