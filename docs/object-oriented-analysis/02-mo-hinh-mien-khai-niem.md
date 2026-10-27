# 2. Mô hình Miền Khái niệm

Mô hình Miền Khái niệm phản ánh trực quan các khái niệm nghiệp vụ trong không gian tài chính cá nhân, tách biệt hoàn toàn với các chi tiết kỹ thuật như lớp lập trình, bảng cơ sở dữ liệu hay giao diện người dùng.

Mô hình đóng vai trò từ điển định hình ngôn ngữ nghiệp vụ thống nhất giữa chuyên gia tài chính và nhóm phát triển phần mềm, trực tiếp phục vụ 3 mục tiêu nghiệp vụ cốt lõi: quản lý nguồn tiền theo ví, kiểm soát dòng tiền thu chi và phân tích cơ cấu tài chính định kỳ.

## 2.1 Phương pháp luận Xác định Lớp Khái niệm

Để xác định chính xác các lớp khái niệm trong miền quản lý tài chính cá nhân mà không rò rỉ cấu trúc cài đặt, hệ thống áp dụng kết hợp hai kỹ thuật phân tích:
- Kỹ thuật danh sách phân loại: Rà soát không gian bài toán theo các nhóm khái niệm chuẩn hóa gồm giao dịch tài chính, nguồn chứa tiền tệ, cấu trúc phân loại thu chi và bản ghi kiểm kê đối soát.
- Kỹ thuật phân tích cụm danh từ: Rà soát toàn bộ mô tả nghiệp vụ của bài toán tài chính cá nhân; loại bỏ các danh từ công nghệ như giao diện, nút bấm, cơ sở dữ liệu hay tệp tin; chọn lọc các danh từ nghiệp vụ thực tế để phân định thành lớp khái niệm, thuộc tính hoặc đại lượng đo lường.

Các khái niệm trích xuất được phân định thành hai nhóm cấu trúc:
- Thực thể (Entity): Khái niệm có định danh độc lập, duy nhất và duy trì vòng đời biến đổi trạng thái qua các giao dịch tài chính.
- Đối tượng giá trị (Value Object): Khái niệm mô tả đặc tính hoặc đại lượng đo lường không có định danh độc lập, mang tính bất biến và bảo đảm tính chính xác số học tuyệt đối.

## 2.2 Bảng Phân loại Thực thể và Đối tượng Giá trị

Hệ thống phân chia các khái niệm nghiệp vụ thành hai nhóm: 7 thực thể dùng để quản lý dữ liệu tài chính và 6 đối tượng giá trị dùng cho việc đo lường, tính toán.

### 2.2.1 Danh mục Thực thể Miền

| Thực thể miền | Thuộc tính chính | Quy tắc nghiệp vụ |
| :--- | :--- | :--- |
| `Wallet` | Mã ví, tên ví, ghi chú, trạng thái hoạt động | Tên ví không được trùng nhau (`BR-NAM-01`). Số dư được tính từ lịch sử giao dịch và không được phép âm (`BR-FIN-02`). |
| `IncomeExpenseTransaction` | Mã giao dịch, số tiền, ngày giao dịch, ghi chú | Mỗi giao dịch phải gắn với một ví và một danh mục cụ thể. Khi ghi nhận, số dư ví sẽ tự động tăng (nếu thu) hoặc giảm (nếu chi) ngay lập tức (`BR-FIN-02`, `BR-FIN-04`). |
| `TransferTransaction` | Mã chuyển tiền, số tiền, ngày chuyển, ghi chú | Không được chuyển tiền vào chính ví gửi. Tiền chuyển bị trừ ở ví gửi và cộng vào ví nhận cùng một lúc để giữ nguyên tổng tiền (`BR-FIN-03`). |
| `TransactionFee` | Mã chi phí, số tiền phí | Tiền phí không được âm và phải gắn với một danh mục chi tiêu. Khoản phí này đi liền với giao dịch gốc, nếu xóa giao dịch gốc thì phí cũng bị xóa theo (`BR-FIN-02`, `BR-FIN-03`). |
| `Category` | Mã danh mục, tên danh mục, trạng thái hoạt động | Tên danh mục không được trùng nhau trong cùng một nhóm cha (`BR-NAM-01`). Danh mục đã từng phát sinh giao dịch thì chỉ ẩn đi chứ không xóa hẳn khỏi hệ thống (`BR-FIN-04`). |
| `NeedGroup` | Mã nhóm, tên nhóm | Tên nhóm không được trùng nhau trong cùng loại thu hoặc chi (`BR-NAM-01`). Mỗi loại thu hay chi luôn phải giữ lại ít nhất một nhóm đang dùng, không được xóa hết. |
| `BalanceAdjustmentRecord` | Mã bản ghi, số dư trước, số dư sau, số tiền chênh lệch, ngày kiểm kê, lý do | Lưu lại khoản tiền chênh lệch khi kiểm kê số dư thực tế của ví (`BR-FIN-02`). Sau khi tạo, bản ghi này được giữ nguyên cố định, không được phép chỉnh sửa. |

### 2.2.2 Danh mục Đối tượng Giá trị

| Đối tượng giá trị | Dữ liệu cấu thành | Ý nghĩa và quy tắc tính |
| :--- | :--- | :--- |
| `Money` | Số tiền thập phân, đơn vị tiền tệ | Lưu trữ số tiền chính xác, không dùng số thực để tránh sai lệch số thập phân (`BR-FIN-01`). Khi chia tiền, áp dụng quy tắc làm tròn nửa chẵn (làm tròn ngân hàng). |
| `TransactionDate` | Ngày, tháng, năm | Ngày phát sinh giao dịch, phải đúng theo lịch thực tế. |
| `TransactionType` | Giá trị cố định: Thu nhập hoặc Chi tiêu | Chỉ gồm hai giá trị cố định, quyết định việc cộng hay trừ tiền vào ví (`BR-FIN-04`). |
| `NetCashFlow` | Số tiền | Số tiền còn lại sau khi lấy tổng thu nhập trừ đi tổng chi tiêu trong một khoảng thời gian (`BR-FIN-01`). |
| `SavingsRate` | Tỷ lệ phần trăm | Tỷ lệ phần trăm tiền tiết kiệm được, tính bằng dòng tiền thuần chia cho tổng thu nhập, lấy độ chính xác 4 chữ số thập phân (`BR-FIN-01`). |
| `SpendingStructureShare` | Tỷ lệ phần trăm | Tỷ lệ phần trăm số tiền đã chi của một nhóm hoặc một danh mục so với tổng chi tiêu (`BR-FIN-01`). |

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
| `transactionType` | Loại thu chi (dẫn xuất) | Bắt nguồn từ `needGroup.transactionType`; không lưu trực tiếp mà điều hướng qua nhóm cha. Bất biến ngầm định theo `NeedGroup` cha; là điều kiện kiểm tra tính hợp lệ khi chuyển nhóm (`BR-FIN-04`). |

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

## 2.4 Đặc tả Chi tiết các Đối tượng Giá trị

Phần này thiết lập khung chỉ dẫn hoàn thiện cho các đối tượng giá trị trong hệ thống. Khác với thực thể, đối tượng giá trị không có định danh độc lập mà được định danh hoàn toàn bằng tập giá trị thuộc tính, mang tính bất biến và được dùng để bảo đảm độ chính xác số học trong toàn bộ các phép tính toán tài chính.

### 2.4.1 Tiền tệ và Độ chính xác Số học - Money

(Mô tả khoảng 1 đến 2 câu về đại lượng đo lường giá trị tiền tệ bảo đảm tính chính xác số học tuyệt đối)

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| (Nêu thuộc tính cần có của tiền tệ) | (Nêu ý nghĩa của thuộc tính) | (Giải thích tại sao cần thuộc tính này, căn cứ vào quy tắc tài chính BR-FIN và ràng buộc thiết kế CON tương ứng trong SRS) |

**Quy tắc bất biến và vận hành**

- (Nêu các quy tắc bất biến bắt buộc tuân thủ: ví dụ không dùng số thực dấu chấm động theo BR-FIN-01 và CON-02, đại lượng tiền tệ luôn gắn liền với đơn vị tiền tệ VNĐ)
- (Nêu các quy tắc vận hành của hệ thống: ví dụ quy tắc làm tròn ngân hàng Banker's Rounding khi phát sinh phép chia hoặc tính tỷ lệ)

### 2.4.2 Ngày giao dịch và Mốc thời gian - TransactionDate

(Mô tả khoảng 1 đến 2 câu về mốc thời gian xác định thời điểm phát sinh sự kiện tài chính thực tế)

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| (Nêu thuộc tính cần có của ngày giao dịch) | (Nêu ý nghĩa của thuộc tính) | (Giải thích tại sao cần thuộc tính này, căn cứ vào nhóm yêu cầu ghi nhận thu chi FR-TXN và phụ thuộc môi trường DE tương ứng trong SRS) |

**Quy tắc bất biến và vận hành**

- (Nêu các quy tắc bất biến bắt buộc tuân thủ: ví dụ tính hợp lệ về mặt lịch pháp quốc tế, mốc thời gian không được vượt quá thời điểm hiện tại hoặc nằm ngoài giới hạn cho phép)
- (Nêu các quy tắc vận hành của hệ thống: ví dụ quy tắc so sánh thứ tự thời gian giữa các giao dịch để phục vụ tính toán số dư lịch sử)

### 2.4.3 Loại giao dịch - TransactionType

`TransactionType` là đối tượng giá trị kiểu liệt kê định danh nghiệp vụ (Enumerated Value Object), mang tính bất biến tuyệt đối (`Immutable`), không có danh tính độc lập và được so sánh dựa trên toàn bộ giá trị biểu diễn. Đối tượng này đóng vai trò xác lập ranh giới phân cấp danh mục cao nhất (cấp 1) và định hình chiều tác động của dòng tiền lên số dư ví.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `value` | Chiều tác động dòng tiền | Enum đóng, chỉ nhận `INCOME` (tăng số dư) hoặc `EXPENSE` (giảm số dư) theo `BR-FIN-04`. Bất biến sau khi gán. |

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Tập giá trị hữu hạn: Gồm đúng 2 giá trị đóng và khép kín:
    - `INCOME` (Thu nhập): Đại diện cho dòng tiền đi vào ví (`Inflow`), làm gia tăng tài sản khả dụng.
    - `EXPENSE` (Chi tiêu): Đại diện cho dòng tiền đi ra khỏi ví (`Outflow`), làm suy giảm tài sản khả dụng.
  - Phân cấp danh mục cấp 1 cố định: Là gốc phân loại cấp 1 trong mô hình phân cấp 3 cấp theo `BR-FIN-04`. Hai nhánh `INCOME` và `EXPENSE` được hệ thống khởi tạo mặc định, mang tính bất biến vĩnh viễn, người dùng tuyệt đối không thể sửa tên, xóa bỏ hoặc tạo thêm nhánh mới (`FR-CAT-01`).
  - Quyết định chiều biến động số dư ví: Trong công thức bảo toàn số dư ví theo thời gian thực (`BR-FIN-02`), giao dịch gắn với `INCOME` được cộng vào số dư ví tiếp nhận ($+Income_w(t)$ theo `FR-TXN-01`), trong khi giao dịch gắn với `EXPENSE` được trừ khỏi số dư ví chi trả ($-Expense_w(t)$ theo `FR-TXN-02`).
  - Quy chuẩn chi phí giao dịch: Chi phí giao dịch (`TransactionFee`) luôn mang bản chất là một khoản chi tiêu độc lập thuộc loại `EXPENSE` và bắt buộc liên kết với một danh mục thuộc nhánh Chi tiêu đang hoạt động (`BR-FIN-02`, `ERR-FEE-003`).
  - Ranh giới tính duy nhất của tên nhóm nhu cầu: Theo quy tắc đặt tên `BR-NAM-01`, tính duy nhất của tên Nhóm nhu cầu được giới hạn trong cùng một nhánh `TransactionType`. Hai nhóm nhu cầu thuộc hai nhánh khác nhau được phép trùng tên (ví dụ: nhóm "Khác" của nhánh Thu nhập và nhóm "Khác" của nhánh Chi tiêu).
  - Tính bất biến liên kết: Hệ thống nghiêm cấm mọi thao tác chuyển đổi `TransactionType` của một Nhóm nhu cầu hoặc Danh mục khi đã phát sinh giao dịch liên kết nhằm loại trừ triệt để sai lệch số liệu lịch sử (`BR-FIN-04`, `ERR-CAT-001`).
- Quy tắc vận hành:
  - Căn cứ vào giá trị của `TransactionType`, hệ thống định tuyến chiều tác động dòng tiền lên số dư ví khả dụng và định hình cây phân cấp danh mục cấp 1 theo `FR-TXN-01`, `FR-TXN-02`, `BR-FIN-02`, `BR-FIN-04`, `FR-CAT-01`.

### 2.4.4 Dòng tiền thuần - NetCashFlow

`NetCashFlow` là đối tượng giá trị tiền tệ đo lường tổng hợp, phản ánh thặng dư hoặc thâm hụt tài chính ròng của người dùng trong một kỳ quan sát xác định. Đối tượng này mang tính bất biến tuyệt đối, không có danh tính độc lập và được tính toán đại số từ tập hợp các giao dịch trong kỳ theo `FR-REP-01`, `BR-FIN-01` và `BR-FIN-02`.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `amount` | Giá trị số tiền ròng | Số thập phân chính xác có dấu đại số, có thể mang giá trị âm, dương hoặc bằng 0 theo `BR-FIN-01` và `CON-02`. Bất biến sau khi khởi tạo. |
| `currency` | Đơn vị tiền tệ | Chuỗi ký tự định danh đơn vị tiền tệ chuẩn hóa, mặc định là `VNĐ`. |

**Công thức xác định đại số**

Xét kỳ thống kê quan sát $M$ (chuỗi 1 tháng hoặc chuỗi 12 tháng liên tục):

$$\text{NetCashFlow}(M) = \sum_{t \in M} \text{Income}(t) - \sum_{t \in M} \text{Expense}(t)$$

Trong đó:
- $\sum_{t \in M} \text{Income}(t)$: Tổng số tiền của các giao dịch thu nhập phát sinh trong kỳ $M$ theo `FR-TXN-01`.
- $\sum_{t \in M} \text{Expense}(t)$: Tổng số tiền của các giao dịch chi tiêu phát sinh trong kỳ $M$ theo `FR-TXN-02`, bao gồm cả các khoản chi phí giao dịch liên kết (`TransactionFee`) phát sinh trong kỳ.

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Dữ liệu bắt buộc biểu diễn bằng số thập phân chính xác, nghiêm cấm sử dụng số thực dấu chấm động nhị phân (`float`, `double`) theo `BR-FIN-01` và ràng buộc kỹ thuật `CON-02`.
  - Độ chính xác tính toán và lưu trữ duy trì ở mức 2 chữ số thập phân (`Scale = 2`).
  - Phân định ngữ nghĩa tài chính: $\text{NetCashFlow} > 0$ thể hiện thặng dư dòng tiền; $\text{NetCashFlow} < 0$ thể hiện thâm hụt dòng tiền (chi vượt thu); $\text{NetCashFlow} = 0$ thể hiện trạng thái cân bằng thu chi.
- Quy tắc vận hành:
  - Trên giao diện người dùng, số tiền hiển thị theo quy cách số nguyên đồng cho VNĐ hoặc 2 chữ số thập phân trên báo cáo chi tiết theo `AC-REP-01.2`.

### 2.4.5 Tỷ lệ tiết kiệm - SavingsRate

`SavingsRate` là đối tượng giá trị tỷ lệ phần trăm tài chính, đo lường mức độ tích lũy hình thành từ dòng tiền thuần so với tổng thu nhập tạo ra trong kỳ phân tích. Đối tượng này mang tính bất biến tuyệt đối, phụ thuộc trực tiếp vào kết quả của `NetCashFlow` và tuân thủ các quy tắc an toàn số học theo `FR-REP-01`, `BR-FIN-01`, `AC-REP-01.2` và `AC-REP-01.3`.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `rate` | Giá trị tỷ lệ phần trăm | Số thập phân chính xác theo `BR-FIN-01`. Duy trì tối thiểu 4 chữ số thập phân trong tính toán trung gian và làm tròn ngân hàng về 2 chữ số thập phân khi hiển thị (`AC-REP-01.2`). |
| `unit` | Đơn vị đo lường | Đơn vị tỷ lệ phần trăm, cố định là ký hiệu `%`. |

**Công thức xác định đại số**

$$\text{SavingsRate} = \begin{cases} \left(\dfrac{\text{NetCashFlow}}{\text{TotalIncome}}\right) \times 100\% & \text{khi } \text{TotalIncome} > 0 \\ 0.00\% & \text{khi } \text{TotalIncome} \le 0 \end{cases}$$

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Xử lý biên an toàn: Khi $\text{TotalIncome} \le 0$ (kỳ phân tích không có thu nhập hoặc thu nhập bằng 0.00 VNĐ nhưng phát sinh chi tiêu), tỷ lệ tiết kiệm mặc định bằng đúng $0.00\%$. Quy tắc này loại bỏ hoàn toàn rủi ro chia cho 0 (`ERR-FIN-002`) và bảo đảm bản chất tài chính theo `AC-REP-01.3`.
  - Phản ánh thâm hụt: Khi $\text{TotalIncome} > 0$ và $\text{NetCashFlow} < 0$ (chi vượt thu), tỷ lệ tiết kiệm mang giá trị âm, phản ánh tỷ lệ thâm hụt tương ứng trên tổng thu nhập.
- Quy tắc vận hành:
  - Bước tính toán trung gian bắt buộc duy trì độ chính xác tối thiểu 4 chữ số thập phân (khuyến nghị 6 chữ số thập phân) theo `BR-FIN-01`.
  - Áp dụng quy tắc làm tròn ngân hàng (`Banker's Rounding` - làm tròn về số chẵn gần nhất khi phần dư đúng bằng 0.5 đơn vị cuối).
  - Kết quả hiển thị cuối cùng chuẩn hóa đúng 2 chữ số thập phân kèm ký hiệu đơn vị (ví dụ: `40.00%` theo `AC-REP-01.2`).

### 2.4.6 Tỷ trọng cơ cấu chi tiêu - SpendingStructureShare

`SpendingStructureShare` là đối tượng giá trị tỷ trọng phân bổ, đo lường tỷ lệ đóng góp của từng nhóm nhu cầu (cấp 2) hoặc từng danh mục chi tiết (cấp 3) trên tổng quy mô dòng tiền trong kỳ phân tích. Đối tượng được áp dụng cho cả phân tích cơ cấu chi tiêu (`EXPENSE`) và cơ cấu thu nhập (`INCOME`) theo `FR-REP-02`, `BR-FIN-01`, `BR-FIN-04` và các tiêu chí chấp thuận `AC-REP-02.1` đến `AC-REP-02.3`.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `share` | Giá trị tỷ trọng | Số thập phân chính xác nằm trong khoảng $[0.00, 100.00]$ đối với từng thành phần theo `BR-FIN-01`. |
| `unit` | Đơn vị đo lường | Đơn vị tỷ lệ phần trăm, cố định là ký hiệu `%`. |
| `scope` | Phạm vi gom cụm phân tích | Xác định cấp phân tích: Nhóm nhu cầu cấp 2 (`NeedGroup`) hoặc Danh mục chi tiết cấp 3 (`Category`) theo `FR-REP-02`. |

**Công thức xác định đại số**

$$\text{SpendingStructureShare}_i = \begin{cases} \left(\dfrac{\text{ExpenseAmount}_i}{\text{TotalExpense}}\right) \times 100\% & \text{khi } \text{TotalExpense} > 0 \\ 0.00\% & \text{khi } \text{TotalExpense} = 0.00\text{ VNĐ} \end{cases}$$

Trong đó:
- $\text{ExpenseAmount}_i$: Tổng số tiền chi tiêu của Nhóm nhu cầu thứ $i$ (khi gom cụm cấp 2) hoặc Danh mục chi tiết thứ $i$ (khi gom cụm cấp 3) trong kỳ phân tích.
- $\text{TotalExpense}$: Tổng chi tiêu của toàn bộ các nhóm hoặc danh mục trong kỳ phân tích, bao gồm cả các chi phí giao dịch liên kết.

*(Công thức áp dụng tương tự khi phân tích cơ cấu thu nhập `INCOME` theo `FR-REP-02`).*

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Khi $\text{TotalExpense} = 0.00\text{ VNĐ}$ (kỳ phân tích không phát sinh chi tiêu), hệ thống hiển thị tổng tiền 0.00 VNĐ cùng tỷ trọng mặc định $0.00\%$ cho danh sách rỗng, không làm phát sinh lỗi chia cho 0 (`ERR-FIN-002`, `AC-REP-02.2`).
  - Bảo toàn dữ liệu xóa mềm: Nếu trong kỳ phân tích xuất hiện giao dịch thuộc nhóm hoặc danh mục đã bị xóa trong quá khứ, hệ thống vẫn hiển thị độc lập mục đó kèm nhãn "(đã xóa)" với đúng số tiền và tỷ trọng tương ứng (`AC-REP-02.3`), không gộp vào mục khác.
  - Bất biến tổng cơ cấu: Tổng tỷ trọng của toàn bộ các mục thành phần trong cùng một cấp phân loại phải xấp xỉ bằng $100.00\%$:
    $$\sum_{i} \text{SpendingStructureShare}_i \approx 100.00\%$$
    (Sai số cho phép tối đa trong khoảng $\pm 0.01\%$ do hiệu ứng phân rã làm tròn ngân hàng độc lập trên từng thành phần).
- Quy tắc vận hành:
  - Bước tính toán trung gian duy trì tối thiểu 4 chữ số thập phân theo `BR-FIN-01`.
  - Áp dụng quy cách làm tròn ngân hàng (`Banker's Rounding`), hiển thị kết quả cuối cùng với đúng 2 chữ số thập phân (ví dụ: `40.00%` theo `AC-REP-02.1`).

## 2.5 Các Mối Quan hệ Ngữ nghĩa và Bất biến Miền

Phần này thiết lập khung chỉ dẫn phân tích các chính sách tài chính cốt lõi chi phối sự tương tác giữa các thực thể trong mô hình miền. Mỗi mối quan hệ cần làm rõ bản chất liên kết, các bất biến số học hoặc cấu trúc bắt buộc tuân thủ, và kịch bản xử lý vi phạm của hệ thống.

### 2.5.1 Quan hệ Sở hữu Ví và Bảo toàn Số dư

(Mô tả khoảng 1 đến 2 câu về mối liên kết giữa một Ví và toàn bộ các dòng tiền tác động đến số dư của ví đó)

**Quy tắc bất biến và vận hành**

- (Nêu các quy tắc bất biến bắt buộc tuân thủ: ví dụ điều kiện số dư khả dụng luôn không âm tại mọi thời điểm, căn cứ vào quy tắc bảo toàn số dư BR-FIN và nhóm yêu cầu ví FR-WAL)
- (Nêu các quy tắc vận hành và công thức tính toán: ví dụ công thức đại số tính lại số dư khả dụng theo thời gian thực từ các dòng tiền vào và ra, kịch bản hệ thống từ chối khi chi tiêu vượt số dư hoặc khi sửa xóa giao dịch quá khứ)

### 2.5.2 Quan hệ Phân cấp Danh mục 3 Cấp

Quan hệ phân cấp danh mục là quan hệ hợp thành nghiêm ngặt giữa ba tầng khái niệm: `TransactionType` (Cấp 1, gốc cố định bất biến) $\rightarrow$ `NeedGroup` (Cấp 2, phân nhóm mục đích) $\rightarrow$ `Category` (Cấp 3, đơn vị gán nhãn giao dịch). Cây phân cấp này xác định ranh giới mà các thực thể `Category` và `NeedGroup` có thể tồn tại và vận hành, đồng thời chi phối toàn bộ tính hợp lệ của dữ liệu thu chi trong hệ thống (`BR-FIN-04`, `FR-CAT-01`–`FR-CAT-03`).

**Quy tắc bất biến và vận hành**

- *Bất biến cấu trúc*: Cấp 3 (`Category`) là cấp duy nhất được phép liên kết trực tiếp với giao dịch thu chi; liên kết lên cấp 1 hoặc cấp 2 bị từ chối (`AC-CAT-01.2`). Mỗi `Category` và `NeedGroup` bắt buộc thuộc về đúng một nhánh `TransactionType` xác định và không được phép thay đổi nhánh đó sau khi khởi tạo (`ERR-CAT-001`). Hệ thống nghiêm cấm tồn tại danh mục mồ côi (`Orphan Category`) — mọi danh mục phải trực thuộc một nhóm cha hợp lệ đang tồn tại (`ERR-CAT-004`, `ERR-CAT-005`). Chi tiết ràng buộc thuộc tính từng thực thể xem tại mục **2.3.5** và **2.3.6**.
- *Bất biến tổng thể cây phân cấp*: Mỗi nhánh `TransactionType` (`INCOME`, `EXPENSE`) luôn phải duy trì tối thiểu một `NeedGroup` ở trạng thái hoạt động; hành động xóa nhóm cuối cùng của một nhánh bị từ chối (`ERR-CAT-002`, `AC-CAT-03.3`). Tập giá trị hai nhánh này là bất biến vĩnh viễn — không thể sửa, xóa hay mở rộng (`FR-CAT-01`). Chi tiết bản chất enum `TransactionType` xem tại mục **2.4.3**.
- *Vận hành xóa và khôi phục*: Thao tác xóa `NeedGroup` kéo theo xóa cascade toàn bộ `Category` con theo quy tắc xóa mềm hoặc xóa hẳn tùy lịch sử giao dịch (`BR-FIN-04`, `FR-CAT-03`). Thao tác chuyển nhóm cha của `Category` chỉ được phép khi nhóm đích đang hoạt động và thuộc **cùng** nhánh `TransactionType` (`ERR-CAT-001`, `ERR-CAT-003`). Dữ liệu giao dịch lịch sử liên kết với danh mục/nhóm đã xóa mềm được bảo toàn nguyên vẹn trên báo cáo kèm nhãn `(đã xóa)` (`AC-REP-02.3`).

### 2.5.3 Quan hệ Phụ thuộc Sinh tử giữa Giao dịch gốc và Phí

(Mô tả khoảng 1 đến 2 câu về quan hệ phụ thuộc mạnh giữa một giao dịch gốc và chi phí phát sinh đi kèm)

**Quy tắc bất biến và vận hành**

- (Nêu các quy tắc bất biến bắt buộc tuân thủ: ví dụ chi phí không âm, chi phí không thể tồn tại độc lập ngoài giao dịch gốc, căn cứ vào các quy tắc tài chính BR-FIN)
- (Nêu các quy tắc vận hành của hệ thống: ví dụ cơ chế ghi nhận đồng thời, bắt buộc chọn danh mục chi tiêu hợp lệ cho phí, và kịch bản xóa hoàn tác đồng bộ khi xóa giao dịch chuyển tiền)
