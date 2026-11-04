# 09. Ma trận Truy vết Thiết kế

## 9.1 Ánh xạ Yêu cầu Chức năng sang Lớp Thiết kế

### 9.1.1 Phương pháp Luận Truy vết Hai chiều
<!-- PLACEHOLDER: Trình bày phương pháp luận truy vết hai chiều theo chuẩn công nghệ phần mềm ISO/IEC 29148: bảo đảm mọi yêu cầu chức năng (gồm 20 ca sử dụng) đều được hiện thực hóa bởi ít nhất một lớp thiết kế cụ thể (truy vết xuôi), và ngược lại, mọi lớp trong bản vẽ thiết kế đều có lý do tồn tại xuất phát từ yêu cầu nghiệp vụ thực tế (truy vết ngược); cam kết loại bỏ hoàn toàn các lớp mồ côi không phục vụ ca sử dụng nào | Căn cứ: Chuẩn kỹ thuật phần mềm ISO/IEC 29148, Craig Larman Chapter 16 | Tiêu chí hoàn thành: Nguyên tắc kiểm toán rõ ràng để đánh giá tính toàn vẹn giữa hồ sơ yêu cầu, hồ sơ phân tích và hồ sơ thiết kế -->

### 9.1.2 Bảng Đối chiếu Yêu cầu Chức năng và Các Lớp Hiện thực
<!-- PLACEHOLDER: Bảng ma trận đối chiếu toàn diện 20 ca sử dụng của hệ thống PFMS: gồm các cột Mã ca sử dụng, Tên ca sử dụng, Lớp Giao diện tiếp nhận, Lớp Dịch vụ điều phối, Lớp Thực thể chịu trách nhiệm, Lớp Kho lưu trữ dữ liệu, và Mẫu thiết kế áp dụng (nếu có); bảo đảm liên kết đầy đủ từ nhóm Quản lý ví, Ghi nhận thu chi, Chuyển tiền, Ngân sách đến Báo cáo và Dữ liệu | Căn cứ: Danh mục ca sử dụng trong SRS và OOA | Tiêu chí hoàn thành: 100% ca sử dụng đều được phân bổ đầy đủ các lớp qua 4 tầng kiến trúc, không có ca sử dụng nào bị khuyết tầng -->

## 9.2 Ánh xạ Thao tác Hệ thống sang Phương thức Dịch vụ

### 9.2.1 Quy tắc Chuyển đổi Hợp đồng Thao tác Hệ thống
<!-- PLACEHOLDER: Đặc tả quy tắc chuyển đổi có hệ thống từ các Thao tác Hệ thống trong phân tích OOA (được định nghĩa trong file 04-dac-ta-bien-doi-du-lieu-thao-tac-he-thong.md) sang các phương thức cụ thể trong tầng Dịch vụ OOD; quy định việc ánh xạ tham số đầu vào, kiểu dữ liệu trả về, điều kiện tiên quyết thành mã kiểm tra hợp lệ, và điều kiện sau thành các lệnh biến đổi trạng thái thực thể | Căn cứ: Craig Larman Operation Contracts to Method Design | Tiêu chí hoàn thành: Bảng quy ước chuyển đổi đồng nhất, bảo đảm không làm biến dạng ngữ nghĩa của hợp đồng nghiệp vụ gốc -->

### 9.2.2 Bảng Đối chiếu Thao tác Hệ thống và Chữ ký Phương thức
<!-- PLACEHOLDER: Bảng ma trận chi tiết đối chiếu toàn bộ các thao tác hệ thống đã được đặc tả trong OOA sang chữ ký phương thức Java cụ thể trong OOD: gồm các cột Mã thao tác hệ thống OOA, Tên thao tác, Tên lớp Giao diện dịch vụ chứa phương thức OOD, Chữ ký phương thức Java đầy đủ (tên hàm, danh sách tham số, kiểu trả về), và Danh mục ngoại lệ nghiệp vụ có thể ném ra | Căn cứ: Hồ sơ đặc tả thao tác hệ thống OOA | Tiêu chí hoàn thành: Khớp chính xác 1:1 giữa thao tác phân tích và phương thức thiết kế, không có thao tác phân tích nào bị bỏ quên -->

## 9.3 Đánh giá Độ Bao phủ và Tính Nhất quán Thiết kế

### 9.3.1 Báo cáo Kiểm toán Độ Bao phủ Nghiệp vụ
<!-- PLACEHOLDER: Báo cáo định lượng về mức độ bao phủ của hồ sơ thiết kế: tổng số yêu cầu chức năng (20/20 đạt 100%), tổng số thao tác hệ thống được hiện thực hóa, số lượng lớp và giao diện được sinh ra; bảng kiểm tra khẳng định không có lớp thiết kế thừa không rõ nguồn gốc và không có yêu cầu nghiệp vụ nào bị bỏ sót | Căn cứ: Tiêu chuẩn kiểm toán chất lượng tài liệu thiết kế phần mềm | Tiêu chí hoàn thành: Số liệu định lượng minh bạch, chứng minh tính đầy đủ và gắn kết chặt chẽ của toàn bộ hệ thống -->

### 9.3.2 Bảng Đối chiếu Quy tắc Nghiệp vụ và Ràng buộc Ranh giới
<!-- PLACEHOLDER: Bảng kiểm tra chéo việc cài đặt các Quy tắc nghiệp vụ bất biến đã được định nghĩa trong SRS: quy tắc số dư ví tiền mặt không âm, quy tắc hạn mức tín dụng, quy tắc kiểm tra vượt ngân sách, quy tắc khóa ví, quy tắc tính lãi hợp đồng; chỉ rõ vị trí lớp và phương thức cụ thể chịu trách nhiệm thực thi từng quy tắc | Căn cứ: Quy tắc nghiệp vụ PFMS trong SRS | Tiêu chí hoàn thành: 100% quy tắc nghiệp vụ cốt lõi đều được chỉ rõ vị trí mã nguồn chịu trách nhiệm thực thi -->

