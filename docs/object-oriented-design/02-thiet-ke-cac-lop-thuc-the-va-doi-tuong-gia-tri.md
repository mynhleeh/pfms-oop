# 02. Thiết kế Lớp Thực thể và Đối tượng Giá trị

## 2.1 Nguyên tắc Thiết kế Lớp Nghiệp vụ

### 2.1.1 Chuyển đổi từ Mô hình Phân tích sang Bản vẽ Thiết kế
<!-- PLACEHOLDER: Quy định phương pháp chuyển đổi từ mô hình khái niệm trong phân tích sang bản vẽ lớp thiết kế; quy định việc bổ sung các kiểu dữ liệu cụ thể trong Java (như UUID, BigDecimal, LocalDate), quy định phạm vi truy cập (private, protected, public), danh sách hàm khởi tạo, phương thức lấy thông tin và phương thức xử lý nghiệp vụ nội tại | Căn cứ: Craig Larman Chapter 16, ISO/IEC 19505-1 | Tiêu chí hoàn thành: Bảng đối chiếu ánh xạ rõ ràng giữa kiểu dữ liệu phân tích và kiểu dữ liệu cài đặt trong Java, không bỏ sót thuộc tính nào -->

### 2.1.2 Phân biệt Thực thể và Đối tượng Giá trị
<!-- PLACEHOLDER: Tiêu chí phân loại rõ ràng giữa Thực thể (đối tượng có mã định danh duy nhất và có vòng đời thay đổi) và Đối tượng Giá trị (đối tượng bất biến, được định danh bằng chính tập giá trị của nó); quy định bắt buộc ghi đè các hàm equals(), hashCode() và toString() đối với Đối tượng Giá trị | Căn cứ: Eric Evans DDD Chapters 5-6, Martin Fowler PoEAA | Tiêu chí hoàn thành: 100% khái niệm nghiệp vụ được xếp vào đúng nhóm, không có khái niệm nào bị phân loại nhập nhằng -->

## 2.2 Sơ đồ Lớp Tổng quan

### 2.2.1 Sơ đồ Lớp Toàn hệ thống
<!-- PLACEHOLDER: Sơ đồ lớp Mermaid thể hiện toàn bộ các lớp nghiệp vụ, đối tượng giá trị, kiểu liệt kê và mối quan hệ giữa chúng; thể hiện đầy đủ các loại quan hệ: kế thừa, hiện thực hóa giao diện, hợp thành, kết tập và phụ thuộc; thể hiện đúng chỉ số số lượng (bội số) tại hai đầu quan hệ | Căn cứ: ISO/IEC 19505-1, Craig Larman Chapter 16 | Tiêu chí hoàn thành: Sơ đồ chỉ chứa các lớp thuộc tầng nghiệp vụ cốt lõi, không chứa lớp giao diện dòng lệnh hay lớp đọc ghi tệp; mọi quan hệ đều có tên gọi rõ nghĩa -->

### 2.2.2 Danh mục các Lớp và Giao diện
<!-- PLACEHOLDER: Bảng tổng hợp toàn bộ các lớp, giao diện và kiểu liệt kê trong tầng nghiệp vụ; gồm các cột: Tên lớp, Phân loại (Thực thể / Đối tượng Giá trị / Kiểu Liệt kê), Gói mã nguồn chứa lớp, Trách nhiệm chính trong hệ thống và Mã thực thể phân tích tương ứng | Căn cứ: ISO/IEC 29148 về truy vết yêu cầu | Tiêu chí hoàn thành: Khớp chính xác 100% với các lớp xuất hiện trên sơ đồ lớp tổng quan, không có lớp nào xuất hiện trên sơ đồ mà thiếu trong bảng hoặc ngược lại -->

## 2.3 Thiết kế các Lớp Thực thể

### 2.3.1 Cấu trúc Lớp Ví
<!-- PLACEHOLDER: Thiết kế chi tiết lớp cơ sở trừu tượng Ví và các lớp ví con kế thừa (Ví tiền mặt, Ví ngân hàng, Ví điện tử, Ví tín dụng); mô tả bảng thuộc tính (tên trường, kiểu dữ liệu, phạm vi truy cập, điều kiện ràng buộc) và bảng phương thức (tên hàm, tham số, kiểu trả về, ngoại lệ); cơ chế nạp tiền, trừ tiền và kiểm tra số dư khả dụng | Căn cứ: Rubric học thuật PTIT (Tính đóng gói và kế thừa), Joshua Bloch Effective Java | Tiêu chí hoàn thành: Tất cả các trường dữ liệu đều đặt phạm vi private hoặc protected; số dư bắt buộc dùng kiểu BigDecimal; không mở các hàm gán giá trị tự do làm phá vỡ tính đóng gói -->

### 2.3.2 Cấu trúc Lớp Giao dịch
<!-- PLACEHOLDER: Thiết kế chi tiết lớp cơ sở trừu tượng Giao dịch và các lớp con (Giao dịch chi tiêu, Giao dịch thu nhập, Giao dịch chuyển tiền); mô tả các trường: mã định danh duy nhất (UUID), số tiền, thời gian giao dịch, danh mục, liên kết ví nguồn và ví đích, ghi chú; các phương thức áp dụng giao dịch vào số dư và hoàn tác giao dịch | Căn cứ: Martin Fowler Accounting Patterns, ISO 8601 về định dạng thời gian | Tiêu chí hoàn thành: Thể hiện tính toàn vẹn của giao dịch kép khi chuyển tiền giữa hai ví; thời gian phát sinh giao dịch không được vượt quá thời điểm hiện tại -->

### 2.3.3 Cấu trúc Lớp Danh mục và Ngân sách
<!-- PLACEHOLDER: Thiết kế chi tiết lớp Danh mục (mã, tên, phân loại thu/chi, biểu tượng, quan hệ phân cấp cha-con) và lớp Ngân sách (mã, số tiền hạn mức, chu kỳ áp dụng, tỷ lệ cảnh báo, danh sách danh mục áp dụng); các phương thức tính toán mức chi tiêu thực tế và kiểm tra các ngưỡng cảnh báo vượt hạn mức | Căn cứ: Quy tắc nghiệp vụ PFMS, Craig Larman Information Expert | Tiêu chí hoàn thành: Ngân sách tự chịu trách nhiệm tính toán mức tiêu hao dựa trên danh sách giao dịch chi tiêu liên kết; kiểm tra tính hợp lệ của chu kỳ ngân sách -->

### 2.3.4 Cấu trúc Lớp Hợp đồng Vay Nợ và Sổ Tiết kiệm
<!-- PLACEHOLDER: Thiết kế chi tiết lớp Hợp đồng Vay Nợ (mã, thông tin đối tác, phân loại đi vay/cho vay, số tiền gốc, thời hạn, lãi suất, danh sách các đợt trả tiền) và lớp Sổ Tiết kiệm (mã, mục tiêu tiết kiệm, số tiền cần đạt, ngày đáo hạn, số tiền đã tích lũy); các phương thức ghi nhận thanh toán từng phần, tính lãi và tất toán | Căn cứ: Đề tài học thuật PTIT số 10 và 26, chuẩn tính toán tài chính | Tiêu chí hoàn thành: Đảm bảo tính toán chính xác lịch trả nợ và số tiền tích lũy; ràng buộc chặt chẽ điều kiện chuyển đổi trạng thái hợp đồng -->

## 2.4 Thiết kế các Đối tượng Giá trị

### 2.4.1 Đối tượng Tiền tệ
<!-- PLACEHOLDER: Thiết kế chi tiết lớp bất biến Tiền tệ gồm số tiền (kiểu BigDecimal kèm chế độ làm tròn HALF_UP) và đơn vị tiền tệ; các phương thức toán học bất biến trả về đối tượng Tiền tệ mới (cộng, trừ, nhân, chia tỷ lệ, so sánh lớn hơn/nhỏ hơn); cấm tuyệt đối việc sử dụng kiểu số thực float hoặc double | Căn cứ: Martin Fowler PoEAA (Mẫu Tiền tệ), Chuẩn tiền tệ Java JSR-354 | Tiêu chí hoàn thành: Đảm bảo tính bất biến tuyệt đối (lớp final, các trường final, không có hàm setter); không xảy ra lỗi sai số làm tròn khi thực hiện các phép tính số học -->

### 2.4.2 Đối tượng Khoảng Thời gian
<!-- PLACEHOLDER: Thiết kế chi tiết lớp bất biến Khoảng Thời gian gồm ngày bắt đầu và ngày kết thúc (kiểu LocalDate); phương thức kiểm tra ngày bắt đầu phải trước hoặc bằng ngày kết thúc; các phương thức tiện ích kiểm tra một ngày có nằm trong khoảng hay không, kiểm tra hai khoảng thời gian có giao nhau không, và tính tổng số ngày | Căn cứ: Martin Fowler Analysis Patterns, ISO 8601-1:2019 | Tiêu chí hoàn thành: Đóng gói toàn bộ logic so sánh và kiểm tra giao thoa thời gian; không thể sửa đổi giá trị sau khi đã tạo đối tượng -->

### 2.4.3 Đối tượng Lãi suất và Tỷ lệ
<!-- PLACEHOLDER: Thiết kế chi tiết các lớp bất biến Lãi suất và Tỷ lệ bọc giá trị tỷ lệ phần trăm (kiểu BigDecimal); phương thức xác thực tỷ lệ không được âm; phương thức chuyển đổi thành hệ số thập phân để nhân trực tiếp với số tiền gốc khi tính lãi | Căn cứ: Quy chuẩn viết mã Java sạch, Quy tắc tính toán tài chính | Tiêu chí hoàn thành: Loại bỏ hoàn toàn việc tính nhẩm tỷ lệ phần trăm bằng các phép chia thô phân tán trong mã nguồn -->

## 2.5 Quan hệ Kế thừa và Tính Đa hình

### 2.5.1 Kế thừa và Khả năng Thay thế trong Cấu trúc Ví
<!-- PLACEHOLDER: Phân tích và chứng minh tính tuân thủ nguyên lý thay thế Liskov trong cấu trúc phân cấp Ví: mọi lớp ví con đều có thể thay thế lớp ví cha tại bất kỳ vị trí nào trong mã nguồn mà không gây lỗi; thiết kế hành vi của Ví tín dụng cho phép số dư âm trong phạm vi hạn mức tín dụng bằng cách ghi đè phương thức kiểm tra số dư một cách hợp lệ | Căn cứ: Nguyên lý SOLID Liskov, Rubric học thuật PTIT (Kế thừa và Đa hình) | Tiêu chí hoàn thành: Không ném ngoại lệ UnsupportedOperationException ở bất kỳ lớp con nào; hợp đồng của lớp cha được tôn trọng đầy đủ -->

### 2.5.2 Xử lý Đa hình trong Cấu trúc Giao dịch
<!-- PLACEHOLDER: Thiết kế phương thức trừu tượng áp dụng vào ví và hoàn tác khỏi ví trong lớp Giao dịch; Giao dịch chi tiêu tự trừ tiền khỏi ví nguồn, Giao dịch thu nhập tự cộng tiền vào ví đích, Giao dịch chuyển tiền tác động đồng thời hai ví; loại bỏ hoàn toàn các câu lệnh kiểm tra kiểu (như instanceof hoặc switch-case theo loại giao dịch) | Căn cứ: Mẫu thiết kế Đa hình trong bộ mẫu GRASP của Craig Larman | Tiêu chí hoàn thành: Đảm bảo nguyên lý Đóng/Mở (Open/Closed) - khi thêm một loại giao dịch mới chỉ cần tạo lớp con mới mà không cần chỉnh sửa mã nguồn hiện có -->

