# 2. Mô hình Miền Khái niệm

<!-- PLACEHOLDER: Tổng quan về vai trò của Mô hình Miền Khái niệm trong OOA theo Craig Larman | Nguồn tham chiếu: Larman Chapter 9, SRS Mục 2.1 | Tiêu chí hoàn thành: Nêu bật bản chất mô hình hóa thế giới thực ("What"), không rò rỉ thiết kế phần mềm ("How") -->

## 2.1 Phương pháp luận Xác định Lớp Khái niệm

<!-- PLACEHOLDER: Trình bày phương pháp trích xuất lớp khái niệm dựa trên danh sách phân loại (Category List) và kỹ thuật phân tích danh từ trong 12 tệp đặc tả SRS | Nguồn tham chiếu: Larman Chapter 9, Scott Ambler Chapter 6 | Tiêu chí hoàn thành: Giải thích tiêu chí phân định rạch ròi giữa Entity và Value Object -->

## 2.2 Bảng Phân loại Khái niệm: Entity vs Value Object

<!-- PLACEHOLDER: Bảng phân loại chi tiết 7 Thực thể miền (Wallet, IncomeExpenseTransaction, TransferTransaction, TransactionFee, Category, NeedGroup, BalanceAdjustmentRecord) và 6 Đối tượng giá trị (Money, TransactionDate, TransactionType, SavingsRate, NetCashFlow, SpendingStructureShare) | Nguồn tham chiếu: Checkpoint 1, SRS Mục 3 và Mục 4 | Tiêu chí hoàn thành: Bảng chuẩn 4 cột: Tên lớp khái niệm, Phân loại, Thuộc tính khái niệm, Ràng buộc và quy tắc nghiệp vụ chi phối -->

## 2.3 Biểu đồ Lớp Miền Khái niệm

<!-- PLACEHOLDER: Biểu đồ lớp miền khái niệm dạng Mermaid thể hiện đầy đủ 7 thực thể và các đối tượng giá trị cấu thành, quan hệ ngữ nghĩa (kết hợp, hợp thành, phụ thuộc), bản số (multiplicity) | Nguồn tham chiếu: Larman Chapter 9, SRS Mục 3 (BR-FIN-02, BR-FIN-03, BR-FIN-04) | Tiêu chí hoàn thành: Cú pháp Mermaid hợp lệ, không chứa kiểu dữ liệu lập trình hay visibility (+/-/#), thể hiện đúng bản số 1..* và 0..1 -->

## 2.4 Đặc tả Chi tiết các Lớp Thực thể

<!-- PLACEHOLDER: Mô tả nguyên tắc đặc tả thực thể: tập trung vào định danh duy nhất (Identity), vòng đời biến đổi và các bất biến nghiệp vụ, tuyệt đối không đặc tả hàm hoặc bảng CSDL | Nguồn tham chiếu: Rule 03 (OOA Standards) | Tiêu chí hoàn thành: Xác định rõ tính toàn vẹn của từng thực thể trong hệ thống -->

### 2.4.1 Thực thể Ví (Wallet)

<!-- PLACEHOLDER: Đặc tả thực thể Ví: Mã định danh ví, Tên ví duy nhất theo BR-NAM-01, Ghi chú, Trạng thái (ACTIVE / ARCHIVED), Số dư khả dụng tính lại từ lịch sử dòng tiền theo BR-FIN-02 | Nguồn tham chiếu: FR-WAL-01 đến FR-WAL-05, BR-FIN-02, BR-NAM-01 | Tiêu chí hoàn thành: Đầy đủ thuộc tính khái niệm và bất biến số dư không âm -->

### 2.4.2 Thực thể Giao dịch Thu chi (IncomeExpenseTransaction)

<!-- PLACEHOLDER: Đặc tả thực thể Giao dịch thu chi: Mã giao dịch, Loại thu chi (TransactionType), Số tiền (Money), Ngày giao dịch (TransactionDate), Ghi chú, Liên kết Ví sở hữu và Danh mục phân loại | Nguồn tham chiếu: FR-TXN-01 đến FR-TXN-05, BR-FIN-02, BR-FIN-04 | Tiêu chí hoàn thành: Mô tả mối quan hệ sống-chết với TransactionFee khi phát sinh chi phí -->

### 2.4.3 Thực thể Giao dịch Chuyển tiền (TransferTransaction)

<!-- PLACEHOLDER: Đặc tả thực thể Giao dịch chuyển tiền nội bộ: Mã chuyển tiền, Số tiền chuyển, Ngày chuyển, Ghi chú, Ví nguồn, Ví đích, Chi phí chuyển tiền | Nguồn tham chiếu: FR-TRF-01 đến FR-TRF-03, BR-FIN-03 | Tiêu chí hoàn thành: Ràng buộc quy tắc ghi nhận đồng thời (trừ nguồn, cộng đích) hoặc cùng không ghi nhận -->

### 2.4.4 Thực thể Chi phí Giao dịch (TransactionFee)

<!-- PLACEHOLDER: Đặc tả thực thể Chi phí giao dịch: Số tiền phí (Money không âm theo BR-FIN-02), Danh mục chi tiêu hợp lệ bắt buộc, Giao dịch gốc liên kết | Nguồn tham chiếu: BR-FIN-02 (Mục 85-89), BR-FIN-03 | Tiêu chí hoàn thành: Làm rõ bản chất là giao dịch chi tiêu con phụ thuộc hoàn toàn vào vòng đời của giao dịch gốc -->

### 2.4.5 Thực thể Danh mục Phân loại (Category)

<!-- PLACEHOLDER: Đặc tả thực thể Danh mục phân loại (Cấp 3): Mã danh mục, Tên danh mục duy nhất trong nhóm cha theo BR-NAM-01, Trạng thái (ACTIVE / SOFT_DELETED), Nhóm nhu cầu cha | Nguồn tham chiếu: FR-CAT-01 đến FR-CAT-05, BR-FIN-04, BR-NAM-01 | Tiêu chí hoàn thành: Ràng buộc cơ chế xóa mềm để bảo toàn lịch sử giao dịch cũ -->

### 2.4.6 Thực thể Nhóm Nhu cầu (NeedGroup)

<!-- PLACEHOLDER: Đặc tả thực thể Nhóm nhu cầu (Cấp 2): Mã nhóm, Tên nhóm duy nhất trong loại cha theo BR-NAM-01, Loại thu chi cha (INCOME/EXPENSE) | Nguồn tham chiếu: FR-CAT-01, BR-FIN-04, BR-NAM-01 | Tiêu chí hoàn thành: Ràng buộc cấu trúc danh mục 3 cấp, không có danh mục mồ côi -->

### 2.4.7 Thực thể Bản ghi Điều chỉnh Số dư (BalanceAdjustmentRecord)

<!-- PLACEHOLDER: Đặc tả thực thể Bản ghi điều chỉnh số dư ví: Mã bản ghi, Ví điều chỉnh, Số dư trước điều chỉnh, Số dư sau điều chỉnh, Giá trị chênh lệch (+/-), Mốc thời gian, Lý do kiểm kê | Nguồn tham chiếu: FR-WAL-05, BR-FIN-02 (Mục 83-84) | Tiêu chí hoàn thành: Khẳng định tính bất biến (không được sửa đổi sau khi tạo) và tách biệt hoàn toàn với luồng thu chi thông thường -->

## 2.5 Đặc tả Chi tiết các Đối tượng Giá trị

<!-- PLACEHOLDER: Mô tả nguyên tắc đặc tả Đối tượng giá trị: tính bất biến (immutability), không có danh tính độc lập, so sánh dựa trên toàn bộ giá trị thuộc tính | Nguồn tham chiếu: Rule 03, Martin Fowler (PoEAA - Value Object) | Tiêu chí hoàn thành: Khẳng định vai trò bảo đảm độ chính xác số học và an toàn dữ liệu -->

### 2.5.1 Tiền tệ và Độ chính xác Số học (Money)

<!-- PLACEHOLDER: Đặc tả đối tượng giá trị Money: Đại lượng tiền tệ gồm giá trị số học thập phân chính xác và đơn vị tiền tệ (VNĐ), tuân thủ BR-FIN-01 và CON-02 | Nguồn tham chiếu: BR-FIN-01, CON-02 | Tiêu chí hoàn thành: Không chứa sai số dấu chấm động, chỉ định rõ quy cách làm tròn ngân hàng khi có phép chia -->

### 2.5.2 Ngày giao dịch và Mốc thời gian (TransactionDate)

<!-- PLACEHOLDER: Đặc tả đối tượng giá trị TransactionDate: Ngày, tháng, năm phát sinh sự kiện tài chính hợp lệ về lịch pháp | Nguồn tham chiếu: FR-TXN-01, DE-01 | Tiêu chí hoàn thành: Ràng buộc kiểm tra tính hợp lệ của mốc thời gian trong khoảng cho phép của hệ thống -->

### 2.5.3 Loại giao dịch (TransactionType)

<!-- PLACEHOLDER: Đặc tả giá trị định danh TransactionType gồm 2 giá trị hữu hạn: INCOME (Thu nhập) và EXPENSE (Chi tiêu) | Nguồn tham chiếu: FR-TXN-01, FR-TXN-02, BR-FIN-04 | Tiêu chí hoàn thành: Làm rõ vai trò định hình chiều biến động số dư ví và phân cấp danh mục cấp 1 -->

### 2.5.4 Nhóm chỉ số Thống kê và Báo cáo

<!-- PLACEHOLDER: Đặc tả các đối tượng giá trị dùng trong phân tích báo cáo: SavingsRate (Tỷ lệ tiết kiệm %), NetCashFlow (Dòng tiền thuần VNĐ), SpendingStructureShare (Tỷ trọng cơ cấu chi tiêu %) | Nguồn tham chiếu: FR-REP-01, FR-REP-02 | Tiêu chí hoàn thành: Xác định công thức tính toán đại số và độ chính xác tối thiểu theo quy định báo cáo -->

## 2.6 Các Mối Quan hệ Ngữ nghĩa và Bất biến Miền

<!-- PLACEHOLDER: Tổng hợp các mối liên kết ngữ nghĩa giữa các lớp khái niệm trong miền bài toán và các bất biến nghiệp vụ chi phối | Nguồn tham chiếu: SRS Mục 3 (BR-FIN-01 đến BR-FIN-04, BR-NAM-01) | Tiêu chí hoàn thành: Làm rõ ràng buộc toàn vẹn dữ liệu ở cấp độ miền phân tích -->

### 2.6.1 Quan hệ Sở hữu Ví và Bảo toàn Số dư

<!-- PLACEHOLDER: Phân tích quan hệ giữa Ví và các dòng tiền (IncomeExpenseTransaction, TransferTransaction, BalanceAdjustmentRecord) theo bất biến bảo toàn số dư | Nguồn tham chiếu: BR-FIN-02, FR-WAL-02 | Tiêu chí hoàn thành: Mô hình hóa công thức số dư khả dụng tính lại từ lịch sử dòng tiền, kiểm soát không âm -->

### 2.6.2 Quan hệ Phân cấp Danh mục 3 Cấp

<!-- PLACEHOLDER: Phân tích quan hệ hợp thành phân cấp giữa Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết | Nguồn tham chiếu: BR-FIN-04, FR-CAT-01 | Tiêu chí hoàn thành: Ràng buộc 100% danh mục thuộc về một nhóm nhu cầu cha hợp lệ, loại trừ danh mục mồ côi -->

### 2.6.3 Quan hệ Phụ thuộc Sinh tử giữa Giao dịch gốc và Phí

<!-- PLACEHOLDER: Phân tích quan hệ phụ thuộc mạnh giữa Giao dịch gốc (Giao dịch thu chi hoặc Chuyển tiền) và Chi phí phát sinh (TransactionFee) | Nguồn tham chiếu: BR-FIN-02 (Mục 88), BR-FIN-03 | Tiêu chí hoàn thành: Xác định bất biến cùng được ghi nhận, chỉnh sửa hoặc xóa đồng thời, không tồn tại phí mồ côi -->
