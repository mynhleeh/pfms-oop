# 2. Mô hình Miền Khái niệm

Mô hình Miền Khái niệm xác lập cấu trúc quan niệm về không gian bài toán của Hệ thống Quản lý Tài chính Cá nhân. Mô hình không đóng vai trò bản thiết kế của các thành phần phần mềm như lớp lập trình, bảng cơ sở dữ liệu hay màn hình giao diện, mà là sự phản ánh trực quan các khái niệm nghiệp vụ thực tế trong không gian tài chính.

Mô hình đóng vai trò từ điển trực quan định hình ngôn ngữ nghiệp vụ thống nhất giữa chuyên gia tài chính và đội ngũ phát triển. Các lớp khái niệm, thuộc tính bản chất và mối quan hệ ngữ nghĩa được trích xuất nhằm bảo đảm tính khép kín của phạm vi nghiệp vụ, trực tiếp phục vụ 3 mục tiêu cốt lõi của hệ thống gồm: quản lý tập trung cơ cấu nguồn tiền, kiểm soát minh bạch toàn bộ dòng tiền thu chi và phân tích bức tranh tài chính định kỳ.

## 2.1 Phương pháp luận Xác định Lớp Khái niệm

Để xác định đầy đủ và chính xác các lớp khái niệm trong miền quản lý tài chính cá nhân mà không rò rỉ cấu trúc cài đặt phần mềm, hệ thống áp dụng kết hợp hai kỹ thuật phân tích chuẩn:

1. Kỹ thuật Danh sách Phân loại Khái niệm: Rà soát không gian bài toán theo các nhóm khái niệm chuẩn hóa:
   - Giao dịch tài chính: Sự kiện phát sinh dòng tiền gồm Giao dịch thu chi `IncomeExpenseTransaction`, Giao dịch chuyển tiền nội bộ `TransferTransaction` và Chi phí giao dịch `TransactionFee`.
   - Nơi lưu trữ tiền tệ: Nguồn chứa dòng tiền thực tế được mô hình hóa thành Ví `Wallet`.
   - Cấu trúc phân loại và tổ chức: Cấu trúc phân cấp phục vụ quản lý dòng tiền gồm Nhóm nhu cầu `NeedGroup` và Danh mục phân loại `Category`.
   - Bản ghi kiểm kê và đối soát: Sự kiện cân đối dòng tiền độc lập được mô hình hóa thành Bản ghi điều chỉnh số dư `BalanceAdjustmentRecord`.

2. Kỹ thuật Phân tích Cụm Danh từ: Thực hiện rà soát toàn bộ các tệp tài liệu Đặc tả Yêu cầu Phần mềm. Toàn bộ danh từ và cụm danh từ liên quan đến cấu trúc công nghệ hoặc giải pháp kỹ thuật như giao diện, nút bấm, cơ sở dữ liệu, bảng dữ liệu, khóa ngoại, tệp lưu trữ đều bị loại bỏ. Các danh từ phản ánh thực tại nghiệp vụ được chọn lọc và phân định thành Lớp khái niệm, Thuộc tính hoặc Đại lượng đo lường.

3. Tiêu chí Phân định giữa Thực thể và Đối tượng Giá trị:
   - Thực thể: Đại diện cho khái niệm có định danh độc lập, duy nhất và duy trì sự liên tục xuyên suốt vòng đời hoạt động. Hai thực thể dù mang toàn bộ giá trị thuộc tính giống hệt nhau nhưng khác định danh thì vẫn là hai đối tượng nghiệp vụ riêng biệt. Trạng thái của thực thể biến đổi liên tục qua thời gian thông qua các sự kiện nghiệp vụ tài chính.
   - Đối tượng Giá trị: Đại diện cho đặc tính mô tả hoặc đại lượng đo lường không có định danh độc lập. Hai đối tượng giá trị được coi là đồng nhất khi và chỉ khi toàn bộ tập thuộc tính cấu thành của chúng bằng nhau. Đối tượng giá trị mang tính bất biến tuyệt đối; mọi thao tác tính toán hoặc chuyển đổi đều tạo ra một thể hiện giá trị mới, bảo đảm loại trừ sai số số học.

## 2.2 Bảng Phân loại Khái niệm: Entity vs Value Object

Bảng dưới đây tổng hợp 7 Thực thể miền và 6 Đối tượng giá trị của hệ thống, xác lập rõ bản chất khái niệm và quy tắc nghiệp vụ ràng buộc:

| Tên lớp khái niệm | Phân loại | Thuộc tính bản chất | Ràng buộc và quy tắc nghiệp vụ chi phối |
| :--- | :--- | :--- | :--- |
| `Wallet` | Thực thể | Mã định danh, Tên ví, Ghi chú, Trạng thái hoạt động | Tên ví duy nhất toàn hệ thống theo `BR-NAM-01`. Số dư tính từ lịch sử dòng tiền, luôn không âm theo `BR-FIN-02` và từ chối theo `ERR-WAL-001`. |
| `IncomeExpenseTransaction` | Thực thể | Mã giao dịch, Ghi chú | Thuộc về một ví và một danh mục hợp lệ. Cập nhật số dư tức thời theo chiều thu hoặc chi theo `BR-FIN-02` và `BR-FIN-04`. |
| `TransferTransaction` | Thực thể | Mã chuyển tiền, Ghi chú | Ví nguồn phải khác ví đích theo `ERR-TRF-001`. Trừ ví nguồn và cộng ví đích đồng thời theo `BR-FIN-03` và `ERR-TRF-002`. |
| `TransactionFee` | Thực thể | Mã chi phí | Chi phí không âm theo `ERR-FEE-001`, bắt buộc gắn danh mục chi tiêu hợp lệ theo `ERR-FEE-002` và `ERR-FEE-003`. Phụ thuộc sinh tử vào giao dịch gốc theo `BR-FIN-02` và `BR-FIN-03`. |
| `Category` | Thực thể | Mã danh mục, Tên danh mục, Trạng thái hoạt động | Tên duy nhất trong nhóm cha theo `BR-NAM-01`. Phải trực thuộc một nhóm nhu cầu đang dùng. Áp dụng cơ chế xóa mềm khi đã có giao dịch liên kết theo `BR-FIN-04`. |
| `NeedGroup` | Thực thể | Mã nhóm, Tên nhóm | Tên duy nhất trong loại thu chi theo `BR-NAM-01`. Thuộc loại thu chi cố định theo `BR-FIN-04`. Không được phép xóa nhóm đang dùng cuối cùng theo `ERR-CAT-002`. |
| `BalanceAdjustmentRecord` | Thực thể | Mã bản ghi, Số dư trước, Số dư sau, Chênh lệch, Mốc kiểm kê, Lý do | Tính toán chênh lệch số dư độc lập với luồng thu chi theo `FR-WAL-05` và `BR-FIN-02`. Bản ghi mang tính bất biến sau khi tạo, không được chỉnh sửa. |
| `Money` | Đối tượng giá trị | Giá trị số học thập phân, Đơn vị tiền tệ | Đảm bảo tính chính xác số học tuyệt đối, loại trừ sai số dấu chấm động theo `CON-02` và `BR-FIN-01`. Áp dụng làm tròn ngân hàng khi phát sinh phép chia. |
| `TransactionDate` | Đối tượng giá trị | Ngày, Tháng, Năm | Hợp lệ về mặt lịch pháp, nằm trong giới hạn kiểm soát thời gian của hệ thống theo `DE-01` và `FR-TXN-01`. |
| `TransactionType` | Đối tượng giá trị | Phân loại cố định gồm Thu nhập hoặc Chi tiêu | Xác lập định danh loại giao dịch và tầng phân cấp danh mục cấp 1 cố định theo `BR-FIN-04`. |
| `SavingsRate` | Đối tượng giá trị | Tỷ lệ phần trăm tiết kiệm | Xác định từ dòng tiền thuần trên thu nhập theo `FR-REP-02`, làm tròn ngân hàng với tối thiểu 4 chữ số thập phân theo `BR-FIN-01`. |
| `NetCashFlow` | Đối tượng giá trị | Giá trị dòng tiền thuần | Hiệu số đại số giữa tổng thu nhập và tổng chi tiêu trong kỳ quan sát theo `FR-REP-01` và `BR-FIN-01`. |
| `SpendingStructureShare` | Đối tượng giá trị | Tỷ trọng phần trăm chi tiêu | Tỷ lệ chi tiêu của từng nhóm hoặc danh mục trên tổng chi tiêu định kỳ theo `FR-REP-01` và `BR-FIN-01`. |

## 2.3 Đặc tả Chi tiết các Lớp Thực thể

Phần này thiết lập khung chỉ dẫn hoàn thiện cho 7 thực thể miền của hệ thống. Mỗi thực thể cần được làm rõ qua 3 nội dung: mô tả bản chất khái niệm trong 1 đến 2 câu, xác định cấu trúc thuộc tính kèm căn cứ nghiệp vụ tương ứng từ hồ sơ SRS, và làm rõ các quy tắc bất biến cùng quy tắc vận hành.

### 2.3.1 Thực thể Ví - Wallet

(Mô tả khoảng 1 đến 2 câu về thực thể Ví trong việc quản lý và lưu trữ nguồn tiền)

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| (Nêu thuộc tính cần có) | (Nêu ý nghĩa của thuộc tính) | (Giải thích tại sao cần thuộc tính này, căn cứ vào các quy tắc tài chính BR-FIN, quy tắc đặt tên BR-NAM và nhóm yêu cầu chức năng ví FR-WAL tương ứng trong SRS) |

**Quy tắc bất biến và vận hành**

- (Nêu các quy tắc bất biến bắt buộc tuân thủ: ví dụ số dư khả dụng của ví tại mọi thời điểm luôn không âm, tên ví là duy nhất toàn hệ thống)
- (Nêu các quy tắc vận hành của hệ thống: ví dụ công thức tính lại số dư theo thời gian thực từ lịch sử dòng tiền, cách thức kiểm tra khi phát sinh chi tiêu và vòng đời lưu trữ của ví)

### 2.3.2 Thực thể Giao dịch Thu chi - IncomeExpenseTransaction

(Mô tả khoảng 1 đến 2 câu về sự kiện phát sinh dòng tiền thu hoặc chi gắn với một ví xác định)

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| (Nêu thuộc tính cần có) | (Nêu ý nghĩa của thuộc tính) | (Giải thích tại sao cần thuộc tính này, căn cứ vào các quy tắc tài chính BR-FIN và nhóm yêu cầu chức năng ghi nhận thu chi FR-TXN tương ứng trong SRS) |

**Quy tắc bất biến và vận hành**

- (Nêu các quy tắc bất biến bắt buộc tuân thủ: ví dụ giao dịch luôn thuộc về một ví và một danh mục hợp lệ đang hoạt động, số tiền giao dịch phải lớn hơn không)
- (Nêu các quy tắc vận hành của hệ thống: ví dụ cơ chế cập nhật số dư ví tức thời theo chiều thu hoặc chi, quan hệ phụ thuộc hoàn toàn với chi phí phát sinh)

### 2.3.3 Thực thể Giao dịch Chuyển tiền - TransferTransaction

(Mô tả khoảng 1 đến 2 câu về sự kiện điều chuyển tiền nội bộ giữa hai ví khác nhau của người dùng)

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| (Nêu thuộc tính cần có) | (Nêu ý nghĩa của thuộc tính) | (Giải thích tại sao cần thuộc tính này, căn cứ vào các quy tắc tài chính BR-FIN và nhóm yêu cầu chức năng chuyển tiền nội bộ FR-TRF tương ứng trong SRS) |

**Quy tắc bất biến và vận hành**

- (Nêu các quy tắc bất biến bắt buộc tuân thủ: ví dụ ví nguồn bắt buộc khác ví đích, số tiền chuyển phải lớn hơn không, bảo toàn tổng số dư trên toàn bộ các ví trước chi phí)
- (Nêu các quy tắc vận hành của hệ thống: ví dụ quy tắc trừ nguồn cộng đích đồng thời, kiểm tra số dư ví nguồn có đủ thanh toán số tiền chuyển và chi phí, kịch bản hoàn tác khi xóa giao dịch)

### 2.3.4 Thực thể Chi phí Giao dịch - TransactionFee

(Mô tả khoảng 1 đến 2 câu về khoản chi phí phát sinh do bên thứ ba thu đi kèm với giao dịch gốc)

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| (Nêu thuộc tính cần có) | (Nêu ý nghĩa của thuộc tính) | (Giải thích tại sao cần thuộc tính này, căn cứ vào các quy tắc tài chính BR-FIN và các nhóm mã lỗi liên quan đến chi phí trong SRS) |

**Quy tắc bất biến và vận hành**

- (Nêu các quy tắc bất biến bắt buộc tuân thủ: ví dụ chi phí không âm, khi chi phí lớn hơn không bắt buộc chọn danh mục chi tiêu hợp lệ, không tồn tại chi phí mồ côi ngoài giao dịch gốc)
- (Nêu các quy tắc vận hành của hệ thống: ví dụ cơ chế hạch toán chi phí như một khoản chi tiêu từ ví chịu phí, đồng bộ vòng đời cùng ghi nhận hoặc cùng xóa với giao dịch gốc)

### 2.3.5 Thực thể Danh mục Phân loại - Category

(Mô tả khoảng 1 đến 2 câu về đơn vị phân loại chi tiết cấp 3 dùng để gán mục đích cho các khoản thu chi)

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| (Nêu thuộc tính cần có) | (Nêu ý nghĩa của thuộc tính) | (Giải thích tại sao cần thuộc tính này, căn cứ vào quy tắc phân cấp danh mục BR-FIN, quy tắc đặt tên BR-NAM và nhóm yêu cầu quản lý danh mục FR-CAT tương ứng trong SRS) |

**Quy tắc bất biến và vận hành**

- (Nêu các quy tắc bất biến bắt buộc tuân thủ: ví dụ tên danh mục là duy nhất trong cùng nhóm nhu cầu cha, danh mục luôn trực thuộc một nhóm nhu cầu đang hoạt động)
- (Nêu các quy tắc vận hành của hệ thống: ví dụ cơ chế xóa mềm khi danh mục đã phát sinh giao dịch lịch sử, điều kiện chuyển danh mục sang nhóm nhu cầu mới cùng loại thu chi)

### 2.3.6 Thực thể Nhóm Nhu cầu - NeedGroup

(Mô tả khoảng 1 đến 2 câu về đơn vị phân loại trung gian cấp 2 gom cụm các danh mục có cùng tính chất tài chính)

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| (Nêu thuộc tính cần có) | (Nêu ý nghĩa của thuộc tính) | (Giải thích tại sao cần thuộc tính này, căn cứ vào quy tắc phân cấp danh mục BR-FIN, quy tắc đặt tên BR-NAM và nhóm yêu cầu quản lý danh mục FR-CAT tương ứng trong SRS) |

**Quy tắc bất biến và vận hành**

- (Nêu các quy tắc bất biến bắt buộc tuân thủ: ví dụ tên nhóm là duy nhất trong cùng loại thu chi, mỗi loại thu chi luôn có tối thiểu một nhóm nhu cầu đang hoạt động)
- (Nêu các quy tắc vận hành của hệ thống: ví dụ cơ chế xóa nhóm kéo theo các danh mục con, ngăn chặn xóa nhóm hoạt động cuối cùng của loại thu chi)

### 2.3.7 Thực thể Bản ghi Điều chỉnh Số dư - BalanceAdjustmentRecord

(Mô tả khoảng 1 đến 2 câu về bản ghi kiểm kê thực tế để cân đối lại số dư ví với thực tế đời thực)

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| (Nêu thuộc tính cần có) | (Nêu ý nghĩa của thuộc tính) | (Giải thích tại sao cần thuộc tính này, căn cứ vào các quy tắc tài chính BR-FIN và nhóm yêu cầu kiểm kê ví FR-WAL tương ứng trong SRS) |

**Quy tắc bất biến và vận hành**

- (Nêu các quy tắc bất biến bắt buộc tuân thủ: ví dụ bản ghi mang tính bất biến tuyệt đối sau khi tạo không được sửa đổi, tách biệt hoàn toàn với luồng thu chi thông thường)
- (Nêu các quy tắc vận hành của hệ thống: ví dụ công thức xác định giá trị chênh lệch giữa số dư thực tế và số dư sổ sách, cơ chế cộng hoặc trừ chênh lệch vào công thức số dư khả dụng)

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
