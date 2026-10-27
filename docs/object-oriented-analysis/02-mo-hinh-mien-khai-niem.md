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

Danh mục Phân loại (`Category`) là đơn vị phân loại chi tiết ở cấp 3 trong cấu trúc danh mục phân cấp của hệ thống, đóng vai trò gán nhãn mục đích tài chính cụ thể cho các giao dịch thu chi phát sinh hàng ngày. Đây là cấp duy nhất trong cây danh mục được phép liên kết trực tiếp với các bản ghi giao dịch thu chi và chi phí giao dịch.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `categoryId` | Mã định danh duy nhất của danh mục | Chuỗi ký tự định danh độc lập (ID), được sinh tự động khi tạo lập. Mang tính bất biến, không bao giờ thay đổi và không bao giờ được tái sử dụng trong toàn bộ vòng đời hệ thống kể cả khi danh mục bị xóa theo `BR-FIN-04` và `FR-CAT-02`. |
| `name` | Tên gọi của danh mục chi tiết | Chuỗi ký tự từ 1 đến 50 ký tự sau khi cắt tỉa khoảng trắng ở hai đầu (`trim`), không được để trống theo `BR-NAM-01` (vi phạm báo lỗi `ERR-NAM-001`). Bắt buộc là duy nhất trong phạm vi cùng nhóm nhu cầu cha (`NeedGroup`) đang hoạt động, so sánh không phân biệt hoa thường theo `BR-NAM-01` (vi phạm báo lỗi `ERR-NAM-002`). |
| `needGroupId` | Mã nhóm nhu cầu cha trực tiếp | Mã định danh tham chiếu đến Nhóm nhu cầu cấp 2 mà danh mục trực thuộc. Danh mục bắt buộc phải trực thuộc đúng một nhóm cha đang ở trạng thái hoạt động theo `BR-FIN-04` và `FR-CAT-01`. Không được phép tạo danh mục vào nhóm đã xóa (từ chối theo `ERR-CAT-005`). Khi chuyển nhóm, chỉ được chuyển sang nhóm đang hoạt động thuộc cùng Loại thu chi (vi phạm từ chối theo `ERR-CAT-001`, `ERR-CAT-003`). |
| `status` | Trạng thái hoạt động của danh mục | Xác định danh mục đang hoạt động (`ACTIVE`) hay đã bị xóa mềm (`DELETED`). Áp dụng cơ chế xóa mềm theo `BR-FIN-04` và `FR-CAT-03`: Nếu chưa có giao dịch thì xóa vĩnh viễn; nếu đã có giao dịch lịch sử thì chuyển sang trạng thái đã xóa, ẩn khỏi danh sách chọn cho giao dịch mới nhưng bảo toàn dữ liệu lịch sử và báo cáo kèm nhãn "(đã xóa)". |
| `icon` | Biểu tượng nhận diện danh mục | Chuỗi ký tự biểu diễn ký hiệu hoặc mã icon đồ họa đại diện cho danh mục trên giao diện người dùng. Thuộc tính tùy chọn, độ dài tối đa 50 ký tự theo `FR-CAT-02`. |
| `color` | Mã màu hiển thị trực quan | Chuỗi ký tự biểu diễn mã màu theo định dạng Hex (ví dụ: `#4CAF50`) phục vụ vẽ biểu đồ cơ cấu và phân tích trực quan. Thuộc tính tùy chọn, độ dài tối đa 10 ký tự theo `FR-CAT-02`. |

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Tên danh mục là duy nhất trong cùng một nhóm nhu cầu cha đang hoạt động theo quy tắc định danh `BR-NAM-01` (`ERR-NAM-002`).
  - Mọi danh mục bắt buộc phải trực thuộc một nhóm nhu cầu cha hợp lệ đang tồn tại, tuyệt đối không tồn tại danh mục mồ côi (`orphan category`) theo quy tắc phân cấp `BR-FIN-04`.
  - Danh mục là cấp duy nhất (cấp 3) được phép liên kết với giao dịch thu chi; hệ thống từ chối liên kết giao dịch trực tiếp lên cấp 1 (`TransactionType`) hoặc cấp 2 (`NeedGroup`) theo `FR-CAT-01` (`AC-CAT-01.2`).
- Quy tắc vận hành:
  - Cơ chế xóa: Khi người dùng thao tác xóa danh mục, hệ thống kiểm tra lịch sử liên kết trong sổ giao dịch. Nếu danh mục chưa phát sinh giao dịch thì xóa hẳn khỏi tệp lưu trữ; nếu đã có giao dịch thì chuyển trạng thái xóa mềm, ẩn khỏi danh sách nhập liệu mới nhưng giữ nguyên liên kết trong các giao dịch quá khứ và báo cáo thống kê theo `BR-FIN-04` và `FR-CAT-03` (`AC-CAT-03.1`).
  - Chuyển nhóm cha: Danh mục chỉ được phép chuyển sang một nhóm nhu cầu đang hoạt động thuộc CÙNG Loại thu chi (`INCOME` hoặc `EXPENSE`). Hệ thống từ chối nếu chuyển sang nhóm khác Loại thu chi (`ERR-CAT-001`), chuyển sang nhóm đã xóa (`ERR-CAT-003`), hoặc bị trùng tên tại nhóm đích (`ERR-NAM-002`).
  - Khôi phục danh mục: Danh mục đã xóa chỉ được phép khôi phục khi nhóm cha của nó đang ở trạng thái hoạt động (nếu nhóm cha bị xóa phải yêu cầu khôi phục nhóm cha trước theo `ERR-CAT-004`) và tên danh mục không bị trùng với một danh mục đang dùng khác trong cùng nhóm cha (`ERR-NAM-002`).

### 2.3.6 Thực thể Nhóm Nhu cầu - NeedGroup

Nhóm Nhu cầu (`NeedGroup`) là đơn vị phân loại trung gian ở cấp 2 trong cấu trúc danh mục phân cấp, có nhiệm vụ gom cụm các danh mục chi tiết có cùng tính chất tài chính hoặc mục đích chi tiêu/thu nhập tương đồng. Thực thể này tạo nền tảng cấu trúc để người dùng quản lý ngân sách và hệ thống kết xuất các báo cáo phân tích cơ cấu tài chính theo nhóm.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `needGroupId` | Mã định danh duy nhất của nhóm nhu cầu | Chuỗi ký tự định danh độc lập (ID), sinh tự động khi tạo mới. Mang tính bất biến, duy nhất toàn hệ thống, không thay đổi và không được tái sử dụng theo `BR-FIN-04` và `FR-CAT-02`. |
| `name` | Tên gọi của nhóm nhu cầu | Chuỗi ký tự từ 1 đến 50 ký tự sau khi cắt tỉa khoảng trắng ở hai đầu (`trim`), không được để trống theo `BR-NAM-01` (vi phạm báo lỗi `ERR-NAM-001`). Phải là duy nhất trong cùng một nhánh Loại thu chi (`TransactionType`) đang hoạt động, so sánh không phân biệt hoa thường theo `BR-NAM-01` (vi phạm báo lỗi `ERR-NAM-002`). Tên nhóm không được dùng làm điều kiện cứng trong bất kỳ quy tắc xử lý nào theo `BR-FIN-04`. |
| `transactionType` | Loại thu chi mà nhóm trực thuộc | Đối tượng giá trị xác định nhánh gốc cấp 1: Thu nhập (`INCOME`) hoặc Chi tiêu (`EXPENSE`) theo `BR-FIN-04` và `FR-CAT-01`. Thuộc tính này là bất biến sau khi khởi tạo; hệ thống nghiêm cấm đổi Loại thu chi của nhóm nhằm bảo vệ tính toàn vẹn của dữ liệu phân tích lịch sử (vi phạm từ chối theo `ERR-CAT-001`). |
| `status` | Trạng thái hoạt động của nhóm nhu cầu | Xác định nhóm đang hoạt động (`ACTIVE`) hay đã bị xóa mềm (`DELETED`). Áp dụng cơ chế xóa cascading theo `BR-FIN-04` và `FR-CAT-03`: Nếu nhóm hoặc các danh mục con bên trong đã có giao dịch thì chuyển sang trạng thái đã xóa; nếu hoàn toàn chưa phát sinh giao dịch thì xóa vĩnh viễn. Không được phép xóa nhóm đang dùng cuối cùng của một loại thu chi (từ chối theo `ERR-CAT-002`). |
| `icon` | Biểu tượng nhận diện nhóm nhu cầu | Chuỗi ký tự đại diện cho biểu tượng đồ họa của nhóm trên giao diện. Thuộc tính tùy chọn, độ dài tối đa 50 ký tự theo `FR-CAT-02`. |
| `color` | Mã màu hiển thị của nhóm | Chuỗi mã màu định dạng Hex (tối đa 10 ký tự) dùng để phân biệt các nhóm trên biểu đồ tròn cơ cấu thu chi theo `FR-CAT-02` và `FR-REP-02`. |

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Tên nhóm nhu cầu là duy nhất trong cùng một nhánh Loại thu chi (`TransactionType`) đang hoạt động theo `BR-NAM-01` (`ERR-NAM-002`).
  - Mỗi nhánh Loại thu chi (`INCOME`, `EXPENSE`) luôn phải có tối thiểu một nhóm nhu cầu ở trạng thái hoạt động; nghiêm cấm xóa nhóm đang dùng cuối cùng của một loại thu chi theo `BR-FIN-04` và `FR-CAT-03` (`ERR-CAT-002`, `AC-CAT-03.3`).
  - Loại thu chi (`transactionType`) của nhóm là cố định vĩnh viễn, không bao giờ được phép thay đổi sau khi tạo (`ERR-CAT-001`).
- Quy tắc vận hành:
  - Cơ chế xóa nhóm kéo theo (`cascade`): Khi xóa một nhóm nhu cầu, toàn bộ các danh mục con trực thuộc nhóm đó đều phải được xử lý xóa theo quy tắc tương ứng: danh mục con nào đã có giao dịch thì chuyển sang xóa mềm, danh mục con nào chưa có thì xóa hẳn. Hộp thoại xác nhận phải thông báo rõ số danh mục bị ẩn, số danh mục xóa hẳn và số giao dịch lịch sử được giữ lại theo `BR-FIN-04` và `FR-CAT-03`.
  - Khôi phục nhóm nhu cầu: Khi khôi phục một nhóm nhu cầu đã xóa, hệ thống giữ nguyên mã định danh cũ và chỉ kiểm tra trùng tên với các nhóm đang hoạt động trong cùng loại thu chi (`ERR-NAM-002`). Thao tác khôi phục nhóm chỉ khôi phục độc lập bản ghi nhóm, không tự động khôi phục các danh mục con đã xóa bên trong; các danh mục con phải được người dùng khôi phục riêng từng cái theo nhu cầu (`BR-FIN-04`).

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

`TransactionType` là đối tượng giá trị kiểu liệt kê định danh nghiệp vụ (Enumerated Value Object), mang tính bất biến tuyệt đối (`Immutable`), không có danh tính độc lập và được so sánh dựa trên toàn bộ giá trị biểu diễn. Đối tượng này đóng vai trò xác lập ranh giới phân cấp danh mục cao nhất (cấp 1) và định hình chiều tác động của dòng tiền lên số dư ví.

- **Tập giá trị hữu hạn**: Gồm đúng 2 giá trị đóng và khép kín:
  - `INCOME` (Thu nhập): Đại diện cho dòng tiền đi vào ví (`Inflow`), làm gia tăng tài sản khả dụng.
  - `EXPENSE` (Chi tiêu): Đại diện cho dòng tiền đi ra khỏi ví (`Outflow`), làm suy giảm tài sản khả dụng.
- **Nguồn tham chiếu nghiệp vụ**: `FR-TXN-01`, `FR-TXN-02`, `BR-FIN-02`, `BR-FIN-04`, `FR-CAT-01`.
- **Vai trò và Ràng buộc nghiệp vụ**:
  1. *Phân cấp danh mục cấp 1 cố định*: Là gốc phân loại cấp 1 trong mô hình phân cấp 3 cấp theo `BR-FIN-04`. Hai nhánh `INCOME` và `EXPENSE` được hệ thống khởi tạo mặc định, mang tính bất biến vĩnh viễn, người dùng tuyệt đối không thể sửa tên, xóa bỏ hoặc tạo thêm nhánh mới (`FR-CAT-01`).
  2. *Quyết định chiều biến động số dư ví*: Trong công thức bảo toàn số dư ví theo thời gian thực (`BR-FIN-02`), giao dịch gắn với `INCOME` được cộng vào số dư ví tiếp nhận ($+Income_w(t)$ theo `FR-TXN-01`), trong khi giao dịch gắn với `EXPENSE` được trừ khỏi số dư ví chi trả ($-Expense_w(t)$ theo `FR-TXN-02`).
  3. *Quy chuẩn chi phí giao dịch*: Chi phí giao dịch (`TransactionFee`) luôn mang bản chất là một khoản chi tiêu độc lập thuộc loại `EXPENSE` và bắt buộc liên kết với một danh mục thuộc nhánh Chi tiêu đang hoạt động (`BR-FIN-02`, `ERR-FEE-003`).
  4. *Ranh giới tính duy nhất của tên nhóm nhu cầu*: Theo quy tắc đặt tên `BR-NAM-01`, tính duy nhất của tên Nhóm nhu cầu được giới hạn trong cùng một nhánh `TransactionType`. Hai nhóm nhu cầu thuộc hai nhánh khác nhau được phép trùng tên (ví dụ: nhóm "Khác" của nhánh Thu nhập và nhóm "Khác" của nhánh Chi tiêu).
  5. *Tính bất biến liên kết*: Hệ thống nghiêm cấm mọi thao tác chuyển đổi `TransactionType` của một Nhóm nhu cầu hoặc Danh mục khi đã phát sinh giao dịch liên kết nhằm loại trừ triệt để sai lệch số liệu lịch sử (`BR-FIN-04`, `ERR-CAT-001`).

### 2.5.4 Nhóm chỉ số Thống kê và Báo cáo

Nhóm chỉ số Thống kê và Báo cáo gồm các đối tượng giá trị được sinh ra từ các phép toán tổng hợp đại số trên tập hợp dữ liệu giao dịch trong một kỳ quan sát xác định. Các đối tượng này mang tính bất biến tuyệt đối (`Immutable`), không lưu danh tính độc lập và tuân thủ chặt chẽ quy tắc độ chính xác số học tài chính `BR-FIN-01` cùng ràng buộc kỹ thuật `CON-02`, loại trừ hoàn toàn sai số làm tròn số thực nhị phân.

#### 2.5.4.1 Dòng tiền thuần (NetCashFlow)

- **Bản chất**: Đối tượng giá trị tiền tệ đo lường tổng hợp (Monetary Value Object), phản ánh thặng dư hoặc thâm hụt tài chính ròng của người dùng trong một kỳ quan sát xác định.
- **Cấu trúc thuộc tính**:
  - `amount`: Giá trị số học thập phân có dấu đại số, biểu thị số tiền ròng (có thể mang giá trị âm, dương hoặc bằng 0).
  - `currency`: Đơn vị tiền tệ chuẩn hóa, mặc định là Đồng Việt Nam (`VNĐ`).
- **Nguồn tham chiếu nghiệp vụ**: `FR-REP-01` (Mục 943), `BR-FIN-01`, `BR-FIN-02`.
- **Công thức xác định đại số**:
  Xét kỳ thống kê quan sát $M$ (ví dụ: chuỗi 1 tháng hoặc chuỗi 12 tháng liên tục):
  $$\text{NetCashFlow}(M) = \sum_{t \in M} \text{Income}(t) - \sum_{t \in M} \text{Expense}(t)$$
  Trong đó:
  - $\sum_{t \in M} \text{Income}(t)$: Tổng số tiền của các giao dịch thu nhập phát sinh trong kỳ $M$ theo `FR-TXN-01`.
  - $\sum_{t \in M} \text{Expense}(t)$: Tổng số tiền của các giao dịch chi tiêu phát sinh trong kỳ $M$ theo `FR-TXN-02`, bao gồm cả các khoản chi phí giao dịch liên kết (`TransactionFee`) phát sinh trong kỳ.
- **Ràng buộc số học và quy định làm tròn**:
  - Dữ liệu bắt buộc biểu diễn bằng số thập phân chính xác, nghiêm cấm sử dụng số thực dấu chấm động nhị phân (`float`, `double`) theo `BR-FIN-01` và `CON-02`.
  - Độ chính xác tính toán và lưu trữ duy trì ở mức 2 chữ số thập phân (`Scale = 2`). Trên giao diện người dùng, số tiền hiển thị theo quy cách số nguyên đồng cho VNĐ hoặc 2 chữ số thập phân trên báo cáo chi tiết (`AC-REP-01.2`).
  - Phân định ngữ nghĩa tài chính: $\text{NetCashFlow} > 0$ thể hiện thặng dư dòng tiền; $\text{NetCashFlow} < 0$ thể hiện thâm hụt dòng tiền (chi vượt thu); $\text{NetCashFlow} = 0$ thể hiện trạng thái cân bằng thu chi.

#### 2.5.4.2 Tỷ lệ tiết kiệm (SavingRate)

- **Bản chất**: Đối tượng giá trị tỷ lệ phần trăm tài chính (Percentage / Ratio Value Object), đo lường mức độ tích lũy tài chính hình thành từ dòng tiền thuần so với tổng thu nhập tạo ra trong kỳ.
- **Cấu trúc thuộc tính**:
  - `rate`: Giá trị tỷ lệ phần trăm dạng số thập phân chính xác.
  - `unit`: Đơn vị đo lường tỷ lệ phần trăm (`%`).
- **Nguồn tham chiếu nghiệp vụ**: `FR-REP-01` (Mục 944), `BR-FIN-01`, `AC-REP-01.2`, `AC-REP-01.3`.
- **Công thức xác định đại số**:
  $$\text{SavingRate} = \begin{cases} \left(\dfrac{\text{NetCashFlow}}{\text{TotalIncome}}\right) \times 100\% & \text{khi } \text{TotalIncome} > 0 \\ 0.00\% & \text{khi } \text{TotalIncome} \le 0 \end{cases}$$
- **Quy tắc xử lý biên và an toàn số học**:
  - Khi $\text{TotalIncome} \le 0$ (kỳ phân tích hoàn toàn không có thu nhập hoặc thu nhập bằng 0.00 VNĐ nhưng phát sinh chi tiêu): Hệ thống mặc định gán tỷ lệ tiết kiệm bằng đúng $0.00\%$. Quy tắc này loại bỏ hoàn toàn rủi ro phát sinh lỗi chia cho 0 (`Divide by Zero / ERR-FIN-002`), đồng thời bảo đảm đúng bản chất tài chính: khi không có thu nhập thì không có căn cứ tính tích lũy từ thu nhập (`AC-REP-01.3`).
  - Khi $\text{TotalIncome} > 0$ và $\text{NetCashFlow} < 0$ (chi vượt thu): Tỷ lệ tiết kiệm mang giá trị âm, phản ánh tỷ lệ thâm hụt tương ứng trên tổng thu nhập.
- **Ràng buộc số học và quy định làm tròn**:
  - Tính toán trung gian: Bắt buộc duy trì độ chính xác tối thiểu 4 chữ số thập phân (khuyến nghị 6 chữ số thập phân) theo quy tắc độ chính xác tài chính `BR-FIN-01`.
  - Quy cách làm tròn: Bắt buộc áp dụng quy tắc làm tròn ngân hàng (`Banker's Rounding` - làm tròn về số chẵn gần nhất khi phần dư đúng bằng 0.5 đơn vị cuối).
  - Kết quả hiển thị cuối cùng: Chuẩn hóa với đúng 2 chữ số thập phân kèm ký hiệu đơn vị (ví dụ: `40.00%` theo `AC-REP-01.2`).

#### 2.5.4.3 Tỷ trọng cơ cấu chi tiêu (SpendingStructureShare)

- **Bản chất**: Đối tượng giá trị tỷ trọng phân bổ (Proportion / Share Value Object), đo lường tỷ lệ đóng góp của từng nhóm nhu cầu (cấp 2) hoặc từng danh mục chi tiết (cấp 3) trên tổng quy mô dòng tiền chi tiêu (hoặc thu nhập) trong kỳ phân tích.
- **Cấu trúc thuộc tính**:
  - `share`: Giá trị tỷ trọng dạng số thập phân chính xác, nằm trong khoảng $[0.00, 100.00]$ đối với từng thành phần.
  - `unit`: Đơn vị đo lường tỷ lệ phần trăm (`%`).
  - `scope`: Cấp độ gom cụm phân tích (cấp 2 - Nhóm nhu cầu `NeedGroup`, hoặc cấp 3 - Danh mục chi tiết `Category`).
- **Nguồn tham chiếu nghiệp vụ**: `FR-REP-02` (Mục 1002, 1004), `BR-FIN-01`, `BR-FIN-04`, `AC-REP-02.1`, `AC-REP-02.2`, `AC-REP-02.3`.
- **Công thức xác định đại số**:
  $$\text{SpendingStructureShare}_i = \begin{cases} \left(\dfrac{\text{ExpenseAmount}_i}{\text{TotalExpense}}\right) \times 100\% & \text{khi } \text{TotalExpense} > 0 \\ 0.00\% & \text{khi } \text{TotalExpense} = 0.00\text{ VNĐ} \end{cases}$$
  Trong đó:
  - $\text{ExpenseAmount}_i$: Tổng số tiền chi tiêu của Nhóm nhu cầu thứ $i$ (khi gom cụm cấp 2) hoặc Danh mục chi tiết thứ $i$ (khi gom cụm cấp 3) trong kỳ phân tích.
  - $\text{TotalExpense}$: Tổng chi tiêu của toàn bộ các nhóm/danh mục trong kỳ phân tích (bao gồm cả các chi phí giao dịch liên kết).
  *(Lưu ý: Công thức áp dụng tương tự khi phân tích cơ cấu thu nhập `INCOME` theo `FR-REP-02`).*
- **Quy tắc xử lý biên và bảo toàn dữ liệu**:
  - Khi $\text{TotalExpense} = 0.00\text{ VNĐ}$ (kỳ phân tích hoàn toàn không phát sinh chi tiêu): Hệ thống hiển thị tổng tiền 0.00 VNĐ cùng tỷ trọng mặc định $0.00\%$ cho danh sách rỗng, không làm phát sinh lỗi chia cho 0 (`ERR-FIN-002`, `AC-REP-02.2`).
  - Xử lý mục đã xóa mềm (`isDeleted = true`): Nếu trong kỳ phân tích xuất hiện giao dịch thuộc nhóm hoặc danh mục đã bị xóa trong quá khứ, hệ thống vẫn hiển thị độc lập mục đó kèm nhãn "(đã xóa)" với đúng số tiền và tỷ trọng tương ứng (`AC-REP-02.3`). Không được gộp số liệu vào mục khác, bảo đảm tổng cơ cấu luôn khớp chính xác với tổng số tiền giao dịch thực tế.
- **Ràng buộc số học và bất biến tổng thể**:
  - Bước tính toán trung gian duy trì tối thiểu 4 chữ số thập phân theo `BR-FIN-01`.
  - Áp dụng quy cách làm tròn ngân hàng (`Banker's Rounding`), hiển thị kết quả cuối cùng với đúng 2 chữ số thập phân (ví dụ: `40.00%` theo `AC-REP-02.1`).
  - Bất biến tổng cơ cấu: Tổng tỷ trọng của toàn bộ các mục thành phần cấu thành trong cùng một cấp phân loại phải xấp xỉ bằng $100.00\%$:
    $$\sum_{i} \text{SpendingStructureShare}_i \approx 100.00\%$$
    (Sai số cho phép tối đa trong khoảng $\pm 0.01\%$ do hiệu ứng phân rã làm tròn ngân hàng độc lập trên từng thành phần).

## 2.6 Các Mối Quan hệ Ngữ nghĩa và Bất biến Miền

<!-- PLACEHOLDER: Tổng hợp các mối liên kết ngữ nghĩa giữa các lớp khái niệm trong miền bài toán và các bất biến nghiệp vụ chi phối | Nguồn tham chiếu: SRS Mục 3 (BR-FIN-01 đến BR-FIN-04, BR-NAM-01) | Tiêu chí hoàn thành: Làm rõ ràng buộc toàn vẹn dữ liệu ở cấp độ miền phân tích -->

### 2.6.1 Quan hệ Sở hữu Ví và Bảo toàn Số dư

<!-- PLACEHOLDER: Phân tích quan hệ giữa Ví và các dòng tiền (IncomeExpenseTransaction, TransferTransaction, BalanceAdjustmentRecord) theo bất biến bảo toàn số dư | Nguồn tham chiếu: BR-FIN-02, FR-WAL-02 | Tiêu chí hoàn thành: Mô hình hóa công thức số dư khả dụng tính lại từ lịch sử dòng tiền, kiểm soát không âm -->

### 2.6.2 Quan hệ Phân cấp Danh mục 3 Cấp

Quan hệ phân cấp danh mục là quan hệ hợp thành phân cấp nghiêm ngặt giữa ba tầng khái niệm: Loại thu chi (`TransactionType`, Cấp 1) $\rightarrow$ Nhóm nhu cầu (`NeedGroup`, Cấp 2) $\rightarrow$ Danh mục chi tiết (`Category`, Cấp 3), chịu sự chi phối toàn diện của quy tắc nghiệp vụ `BR-FIN-04` và nhóm yêu cầu `FR-CAT-01` đến `FR-CAT-03`.

- **Cấu trúc quan hệ và bản số (Multiplicity)**:
  - `TransactionType` (1) $\longleftrightarrow$ `NeedGroup` (1..*): Mỗi nhánh Loại thu chi cố định chứa một hoặc nhiều Nhóm nhu cầu. Mỗi Nhóm nhu cầu chỉ được trực thuộc duy nhất một Loại thu chi xác định. Bất biến hệ thống bắt buộc mỗi Loại thu chi luôn phải có tối thiểu một Nhóm nhu cầu ở trạng thái hoạt động (`ACTIVE`).
  - `NeedGroup` (1) $\longleftrightarrow$ `Category` (0..*): Mỗi Nhóm nhu cầu chứa không hoặc nhiều Danh mục chi tiết. Mỗi Danh mục chi tiết bắt buộc phải trực thuộc đúng một Nhóm nhu cầu cha tại một thời điểm.
  - `Category` (1) $\longleftrightarrow$ `IncomeExpenseTransaction` (0..*): Mỗi Danh mục chi tiết cấp 3 có thể liên kết với nhiều giao dịch thu chi. Danh mục là **cấp duy nhất** trong cây phân cấp được phép liên kết trực tiếp với giao dịch; hệ thống từ chối liên kết giao dịch trực tiếp lên cấp 1 hoặc cấp 2 (`AC-CAT-01.2`).
- **Ràng buộc toàn vẹn và bất biến miền**:
  1. *Loại trừ tuyệt đối danh mục mồ côi (`Orphan Category`)*: 100% danh mục chi tiết đang tồn tại trong hệ thống bắt buộc phải thuộc về một nhóm nhu cầu cha hợp lệ. Hệ thống ngăn chặn việc tạo danh mục vào nhóm đã bị xóa (`ERR-CAT-005`), hoặc khôi phục danh mục khi nhóm cha đang bị xóa (`ERR-CAT-004`).
  2. *Bất biến ranh giới Loại thu chi*: Thuộc tính Loại thu chi của Nhóm nhu cầu và Danh mục là bất biến sau khi khởi tạo. Danh mục chi tiết chỉ được phép di chuyển sang một Nhóm nhu cầu khác nếu nhóm đích đang hoạt động và thuộc **CÙNG** Loại thu chi (`INCOME` hoặc `EXPENSE`). Mọi thao tác đổi loại thu chi hoặc chuyển nhóm chéo nhánh đều bị từ chối theo `ERR-CAT-001` và `ERR-CAT-003` nhằm tránh làm sai lệch dữ liệu phân tích lịch sử.
  3. *Bảo toàn nhóm hoạt động cuối cùng*: Hệ thống tuyệt đối ngăn chặn hành động xóa nhóm nhu cầu đang dùng cuối cùng của một nhánh Loại thu chi (`ERR-CAT-002`, `AC-CAT-03.3`), bảo đảm cây danh mục không bao giờ rơi vào trạng thái rỗng một nhánh.
- **Cơ chế xóa mềm và bảo tồn ngữ nghĩa báo cáo**:
  - Khi xóa một Danh mục hoặc Nhóm nhu cầu đã phát sinh giao dịch lịch sử, hệ thống áp dụng cơ chế xóa mềm (`Soft Delete`): bản ghi không bị xóa vật lý mà chuyển sang trạng thái đã xóa (`status = DELETED`), bị ẩn khỏi danh sách lựa chọn cho các giao dịch mới.
  - Trong các báo cáo phân tích cơ cấu thu chi (`FR-REP-02`), các giao dịch quá khứ thuộc danh mục/nhóm đã xóa mềm vẫn được tổng hợp nguyên vẹn số liệu độc lập kèm nhãn `(đã xóa)`, bảo đảm tổng dòng tiền trên báo cáo luôn khớp chính xác với thực tế dòng tiền đã chi trả (`AC-REP-02.3`).

### 2.6.3 Quan hệ Phụ thuộc Sinh tử giữa Giao dịch gốc và Phí

<!-- PLACEHOLDER: Phân tích quan hệ phụ thuộc mạnh giữa Giao dịch gốc (Giao dịch thu chi hoặc Chuyển tiền) và Chi phí phát sinh (TransactionFee) | Nguồn tham chiếu: BR-FIN-02 (Mục 88), BR-FIN-03 | Tiêu chí hoàn thành: Xác định bất biến cùng được ghi nhận, chỉnh sửa hoặc xóa đồng thời, không tồn tại phí mồ côi -->
