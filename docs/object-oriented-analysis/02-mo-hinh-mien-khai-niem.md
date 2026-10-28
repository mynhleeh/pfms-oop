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

Ví (`Wallet`) đại diện cho nguồn tiền hoặc tài khoản thanh toán khả dụng của người dùng, dùng để tiếp nhận, lưu trữ và trích xuất các luồng tiền trong hệ thống. Hệ thống tạo sẵn ví tiền mặt mặc định ban đầu và cho phép tạo thêm các ví khác để quản lý cơ cấu tài sản và kiểm soát số dư không âm theo thời gian thực.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `walletId` | Mã định danh duy nhất của ví | Chuỗi ký tự định danh độc lập (ID), sinh tự động khi tạo mới (`FR-WAL-01`), bất biến và không tái sử dụng (`FR-WAL-03`). |
| `name` | Tên gọi của ví | Chuỗi ký tự từ 1 đến 50 ký tự sau khi cắt khoảng trắng hai đầu (`trim`), không để trống (`BR-NAM-01`, `ERR-NAM-001`). Duy nhất toàn hệ thống, không phân biệt hoa thường, áp dụng cho cả ví đang dùng và ví lưu trữ (`BR-NAM-01`, `FR-WAL-01`, `ERR-NAM-002`). Tên ví giải phóng khi xóa ví vĩnh viễn (`FR-WAL-03`). Ví mặc định hệ thống có tên cố định là "Tiền mặt". |
| `status` | Trạng thái hoạt động của ví | Nhận giá trị `ACTIVE` (mặc định) hoặc `ARCHIVED` (`FR-WAL-01`, `FR-WAL-03`). Cấm xóa hoặc chuyển ví tiền mặt mặc định sang `ARCHIVED` (`AC-WAL-03.2`). Ví khác chỉ xóa vĩnh viễn khi chưa từng phát sinh giao dịch; nếu đã có giao dịch chỉ được chuyển sang `ARCHIVED` (`FR-WAL-03`, `AC-WAL-03.3`). |
| `note` | Ghi chú hoặc mô tả chi tiết của ví | Diễn giải mục đích sử dụng hoặc thông tin tài khoản ngân hàng; tùy chọn, tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`CON-05`, `FR-WAL-01`, `FR-WAL-03`). |
| `createdAt` | Thời điểm tạo lập ví | Mốc thời gian từ đồng hồ hệ thống (`DE-01`), bất biến, phục vụ sắp xếp danh mục và đối soát dữ liệu (`FR-WAL-01`, `FR-WAL-02`). |
| `currentBalance` | Số dư khả dụng hiện tại (thuộc tính suy dẫn) | Đối tượng giá trị tiền tệ (`Money`), tính từ lịch sử dòng tiền theo `BR-FIN-02`, không gán trực tiếp. Khởi điểm luôn bằng 0.00 VNĐ (`FR-WAL-01`). Luôn không âm ($\ge 0.00$ VNĐ); từ chối mọi thao tác làm âm số dư (`ERR-WAL-001`). |

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Số dư khả dụng tại mọi thời điểm luôn không âm ($WalletBalance_w(t) \ge 0.00$ VNĐ) theo `BR-FIN-02`. Mọi thao tác xuất quỹ làm số dư âm đều bị từ chối theo mã lỗi `ERR-WAL-001`.
  - Tên ví duy nhất toàn hệ thống (so sánh không phân biệt hoa thường giữa các ví đang hoạt động và ví lưu trữ) theo `BR-NAM-01` (`ERR-NAM-002`).
  - Ví tiền mặt mặc định ban đầu ("Tiền mặt") tồn tại vĩnh viễn: cấm xóa và cấm chuyển sang `ARCHIVED` (`FR-WAL-03`, `AC-WAL-03.2`).
  - Cấm gán số dư trực tiếp; số dư chỉ biến động qua các bản ghi thu chi, chuyển tiền hoặc điều chỉnh số dư hợp lệ (`BR-FIN-02`, `FR-WAL-01`, `FR-WAL-03`).
- Quy tắc vận hành:
  - Công thức tính lại số dư theo thời gian thực: Tính lại từ toàn bộ dòng tiền theo `BR-FIN-02`:
    $$WalletBalance_w(T) = \sum_{t \le T} \text{Inflow}_w(t) - \sum_{t \le T} \text{Outflow}_w(t)$$
    với $\text{Inflow}_w(t) = Income_w(t) + TransferIn_w(t) + \max(0, Adjustment_w(t))$
    và $\text{Outflow}_w(t) = Expense_w(t) + TransferOut_w(t) + \max(0, -Adjustment_w(t))$.
  - Kiểm tra tiền điều kiện khả năng chi trả: Mọi thao tác giảm số dư ví (`FR-TXN-01`, `FR-TXN-02`, `FR-TXN-04`, `FR-TXN-05`, `FR-TRF-01`, `FR-WAL-05`) bắt buộc kiểm tra số dư khả dụng; từ chối thực thi nếu không đủ số dư thanh toán (`ERR-WAL-001`).
  - Thống kê cơ cấu phân bổ ví: Tỷ lệ mỗi ví = $(\text{Số dư ví} / \text{Tổng số dư}) \times 100\%$ (`FR-WAL-04`), làm tròn ngân hàng (`BR-FIN-01`). Nếu tổng số dư bằng 0.00 VNĐ, gán tỷ lệ mặc định 0.00% (`ERR-FIN-002`). Ví lưu trữ có số dư $> 0$ hiển thị kèm nhãn "(đã lưu trữ)" (`AC-WAL-04.3`).
  - Vòng đời và điều kiện xóa ví: Chỉ xóa vĩnh viễn khi ví chưa phát sinh giao dịch; sau khi xóa, tên ví được giải phóng (`FR-WAL-03`). Ví đã có giao dịch chỉ được chuyển sang `ARCHIVED` (`AC-WAL-03.3`).

### 2.3.2 Thực thể Giao dịch Thu chi - IncomeExpenseTransaction

Giao dịch Thu chi (`IncomeExpenseTransaction`) ghi nhận sự kiện dòng tiền vào (`INCOME`) hoặc dòng tiền ra (`EXPENSE`) tại một ví cụ thể, gắn với một danh mục chi tiết cấp 3. Thực thể trực tiếp cập nhật số dư ví theo thời gian thực và cung cấp dữ liệu cơ sở cho các báo cáo tài chính định kỳ.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `transactionId` | Mã định danh duy nhất của giao dịch | Chuỗi ký tự định danh độc lập (ID), sinh tự động khi ghi nhận thành công, bất biến và không tái sử dụng (`FR-TXN-01`, `FR-TXN-02`). |
| `amount` | Số tiền phát sinh của giao dịch | Đối tượng giá trị tiền tệ (`Money`), bắt buộc $> 0.00$ VNĐ theo `BR-FIN-01`, `FR-TXN-01` và `FR-TXN-02` (từ chối nếu $\le 0$ hoặc sai định dạng theo `ERR-FIN-001`). |
| `transactionDate` | Ngày phát sinh giao dịch | Đối tượng giá trị ngày tháng (`TransactionDate`, định dạng `YYYY-MM-DD`), không vượt quá ngày hiện tại từ đồng hồ hệ thống `DE-01` (`FR-TXN-01`, `FR-TXN-02`); mặc định lấy ngày hiện tại. |
| `transactionType` | Bản chất kinh tế của giao dịch | Đối tượng giá trị phân loại (`TransactionType`), nhận một trong hai giá trị cố định: `INCOME` hoặc `EXPENSE` theo `BR-FIN-04`. Cố định vĩnh viễn sau khởi tạo; cấm đổi loại giao dịch khi chỉnh sửa (`FR-TXN-04`). |
| `walletId` | Mã định danh ví liên kết | Tham chiếu thực thể `Wallet` chịu tác động dòng tiền (tiếp nhận với `INCOME` hoặc chi trả với `EXPENSE`). Ví tham chiếu bắt buộc đang hoạt động (`ACTIVE`) theo `FR-TXN-01` và `FR-TXN-02`. |
| `categoryId` | Mã định danh danh mục phân loại | Tham chiếu `Category` cấp 3 đang hoạt động (`ACTIVE`) thuộc cùng nhánh Loại thu chi (`BR-FIN-04`). Cho phép giữ nguyên danh mục cũ khi chỉnh sửa giao dịch dù danh mục đã bị xóa mềm (`FR-TXN-04`). |
| `note` | Ghi chú diễn giải giao dịch | Chuỗi ký tự mô tả chi tiết nội dung giao dịch; tùy chọn, tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`CON-05`, `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`). |
| `createdAt` | Thời điểm ghi nhận bản ghi | Mốc thời gian từ đồng hồ hệ thống (`DE-01`), bất biến, dùng để sắp xếp thứ tự hiển thị thứ cấp khi các giao dịch trùng ngày (`FR-TXN-03`). |

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Số tiền giao dịch bắt buộc lớn hơn không ($amount > 0.00$ VNĐ, `BR-FIN-01`, `FR-TXN-01`, `FR-TXN-02`, `ERR-FIN-001`).
  - Giao dịch bắt buộc liên kết với một ví `ACTIVE` và một danh mục cấp 3 `ACTIVE` thuộc cùng nhánh Loại thu chi (`BR-FIN-04`). Cấm liên kết trực tiếp lên cấp 1 hoặc cấp 2 (`AC-CAT-01.2`).
  - Loại giao dịch (`transactionType`) là bất biến sau khi tạo; cấm đổi giữa `INCOME` và `EXPENSE` khi cập nhật (`FR-TXN-04`).
  - Bất biến khả năng chi trả: Giao dịch chi tiêu bắt buộc thỏa mãn $WalletBalance_w \ge amount + feeAmount$; từ chối nếu không đủ số dư theo `BR-FIN-02` (`ERR-WAL-001`).
- Quy tắc vận hành:
  - Cập nhật số dư tức thời: Khi ghi nhận giao dịch, hệ thống tự động cộng $amount$ vào ví nếu là `INCOME`, hoặc trừ $amount$ khỏi ví nếu là `EXPENSE` (`BR-FIN-02`).
  - Đồng bộ chi phí giao dịch: Khi $feeAmount > 0$, hệ thống tự động tạo thực thể `TransactionFee` liên kết với `transactionId`. Cả hai bản ghi cùng được ghi nhận hoặc cùng bị hủy (`BR-FIN-02`, `FR-TXN-01`, `FR-TXN-02`).
  - Hoàn nguyên hai pha khi chỉnh sửa (`FR-TXN-04`): Hoàn nguyên số dư trên ví cũ (trừ thu nhập kèm kiểm tra không âm `ERR-WAL-001`, hoặc cộng chi tiêu), sau đó áp dụng số dư mới lên ví mới (kiểm tra khả năng chi trả nếu là chi tiêu `ERR-WAL-001`), đồng thời điều chỉnh phí liên kết tương ứng.
  - Xóa và hoàn nguyên (`FR-TXN-05`): Hoàn nguyên số dư ví (trừ thu nhập kèm kiểm tra không âm `ERR-WAL-001`, cộng chi tiêu) và xóa đồng thời bản ghi `TransactionFee` liên kết (`AC-TXN-05.3`).

### 2.3.3 Thực thể Giao dịch Chuyển tiền - TransferTransaction

Giao dịch Chuyển tiền (`TransferTransaction`) ghi nhận sự kiện điều chuyển tiền nội bộ giữa hai ví của cùng một người dùng theo cơ chế ghi nhận đồng thời. Giao dịch dịch chuyển tài sản thuần túy, bảo toàn tổng số dư toàn hệ thống trước chi phí và không tạo doanh thu hay chi phí trên giao dịch chuyển tiền gốc.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `transferId` | Mã định danh duy nhất của giao dịch chuyển tiền | Chuỗi ký tự định danh độc lập (ID), sinh tự động khi khởi tạo thành công, bất biến và không tái sử dụng (`FR-TRF-01`). |
| `sourceWalletId` | Mã định danh ví nguồn | Tham chiếu ví trích tiền chuyển. Bắt buộc đang hoạt động (`ACTIVE`) và khác ví đích theo `BR-FIN-03` (`ERR-TRF-001`). |
| `destinationWalletId` | Mã định danh ví đích | Tham chiếu ví nhận tiền chuyển. Bắt buộc đang hoạt động (`ACTIVE`) và khác ví nguồn theo `BR-FIN-03` (`ERR-TRF-001`). |
| `amount` | Số tiền điều chuyển giữa hai ví | Đối tượng giá trị tiền tệ (`Money`), bắt buộc $> 0.00$ VNĐ theo `BR-FIN-01`, `BR-FIN-03` và `FR-TRF-01` (từ chối theo `ERR-TRF-002` nếu $\le 0$). |
| `transactionDate` | Ngày thực hiện chuyển tiền | Đối tượng giá trị ngày tháng (`TransactionDate`, định dạng `YYYY-MM-DD`), không vượt quá ngày hiện tại từ `DE-01` (`FR-TRF-01`); mặc định lấy ngày hiện tại. |
| `note` | Ghi chú nội dung chuyển tiền | Diễn giải lý do hoặc mục đích chuyển tiền; tùy chọn, tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`CON-05`, `FR-TRF-01`, `FR-TRF-02`). |
| `createdAt` | Thời điểm ghi nhận bản ghi | Mốc thời gian từ đồng hồ hệ thống (`DE-01`), bất biến, phục vụ sắp xếp và kiểm toán (`FR-TRF-02`). |

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Phân định thực thể ví: Ví nguồn và ví đích bắt buộc là hai thực thể phân biệt ($sourceWalletId \ne destinationWalletId$, `BR-FIN-03`, `ERR-TRF-001`). Cấm tự chuyển tiền cho chính ví gửi.
  - Số tiền chuyển bắt buộc lớn hơn không ($amount > 0.00$ VNĐ, `BR-FIN-01`, `BR-FIN-03`, `ERR-TRF-002`).
  - Bảo toàn tổng số dư hệ thống trước chi phí: $\sum WalletBalance_w(\text{sau}) = \sum WalletBalance_w(\text{trước})$ theo `BR-FIN-03`. Khi có chi phí $Fee$, tổng số dư giảm đúng bằng $Fee$.
  - Đảm bảo khả năng thanh toán của ví nguồn: Ví nguồn bắt buộc đủ thanh toán tiền chuyển và chi phí ($WalletBalance_{w_{src}} \ge amount + feeAmount$, `BR-FIN-02`, `ERR-WAL-001`).
- Quy tắc vận hành:
  - Ghi nhận đồng thời (`Atomic Execution`): Trừ ví nguồn $(amount + feeAmount)$ và cộng ví đích $amount$ cùng thành công hoặc cùng bị hủy bỏ (`BR-FIN-03`).
  - Tách bạch chi phí chuyển tiền: Khi $feeAmount > 0$, hệ thống tự động tạo thực thể `TransactionFee` (loại `EXPENSE`) trích từ ví nguồn, liên kết với `transferId` (`BR-FIN-02`, `BR-FIN-03`, `FR-TRF-01`). Khoản chuyển thuần túy không hạch toán vào báo cáo thu nhập hay chi tiêu.
  - Kịch bản hoàn tác khi xóa giao dịch (`FR-TRF-02`): Kiểm tra ví đích có số dư $\ge amount$ để thu hồi (tránh âm ví đích, `ERR-WAL-001`); trừ $amount$ ở ví đích, hoàn $(amount + feeAmount)$ về ví nguồn, đồng thời xóa bản ghi `TransactionFee` liên kết (`BR-FIN-03`, `AC-TRF-02.1`).

### 2.3.4 Thực thể Chi phí Giao dịch - TransactionFee

Chi phí Giao dịch (`TransactionFee`) đại diện cho khoản phí phụ phát sinh do bên thứ ba thu, đi kèm trực tiếp với một giao dịch gốc (thu chi hoặc chuyển tiền nội bộ). Thực thể mang bản chất khoản chi tiêu (`EXPENSE`), có quan hệ phụ thuộc sinh tử (`Composition`) với giao dịch gốc nhằm tách bạch chi phí vận hành khỏi dòng tiền cốt lõi.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `feeId` | Mã định danh duy nhất của chi phí | Chuỗi ký tự định danh độc lập (ID), sinh tự động khi tạo phí thành công, bất biến và không tái sử dụng (`BR-FIN-02`, `FR-TXN-01`, `FR-TRF-01`). |
| `amount` | Số tiền chi phí phát sinh | Đối tượng giá trị tiền tệ (`Money`), bắt buộc $\ge 0.00$ VNĐ theo `BR-FIN-01`, `BR-FIN-02` (từ chối nếu $< 0$ theo `ERR-FEE-001`). Không tạo bản ghi khi chi phí bằng 0.00 VNĐ. |
| `walletId` | Mã định danh ví trích trả phí | Tham chiếu ví trích tiền trả phí (ví phát sinh thu chi hoặc ví chuyển tiền nguồn), bắt buộc đang hoạt động (`ACTIVE`) theo `BR-FIN-02`. |
| `categoryId` | Mã định danh danh mục chi phí | Tham chiếu `Category` cấp 3 đang hoạt động (`ACTIVE`) thuộc nhánh Chi tiêu (`EXPENSE`) theo `BR-FIN-02`, `BR-FIN-04`. Báo lỗi nếu thiếu (`ERR-FEE-002`) hoặc sai loại/đã xóa (`ERR-FEE-003`). |
| `parentTransactionId` | Mã định danh giao dịch gốc | Tham chiếu giao dịch gốc phát sinh phí (`transactionId` hoặc `transferId`), bất biến, loại trừ hoàn toàn chi phí mồ côi theo `BR-FIN-02`. |
| `parentTransactionType` | Phân loại giao dịch gốc | Nhận giá trị `INCOME_EXPENSE` hoặc `TRANSFER`, phục vụ liên kết truy nguyên dữ liệu và hiển thị trên sổ thu chi (`FR-TXN-03`, `FR-TRF-01`). |

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Chi phí giao dịch không âm ($amount \ge 0.00$ VNĐ, `BR-FIN-01`, `BR-FIN-02`, `ERR-FEE-001`).
  - Bắt buộc gắn danh mục chi tiêu hợp lệ: Khi $amount > 0.00$ VNĐ, bắt buộc chọn danh mục cấp 3 `ACTIVE` thuộc nhánh Chi tiêu (`EXPENSE`). Từ chối nếu thiếu (`ERR-FEE-002`) hoặc sai loại/đã xóa (`ERR-FEE-003`).
  - Loại trừ tuyệt đối chi phí mồ côi (`No Orphan Fee`): Phí bắt buộc gắn với giao dịch gốc qua `parentTransactionId`; cấm tạo, sửa hoặc lưu trữ phí độc lập (`BR-FIN-02`).
  - Cấm chỉnh sửa hoặc xóa trực tiếp: Người dùng không thao tác trực tiếp trên bản ghi phí; mọi biến động thực hiện thông qua cập nhật hoặc xóa giao dịch gốc (`FR-TXN-04`, `FR-TXN-05`).
- Quy tắc vận hành:
  - Hạch toán chi tiêu độc lập: Ghi nhận vào sổ thu chi như một khoản `EXPENSE` từ ví chịu phí, tính vào tổng chi tiêu toàn hệ thống và dòng tiền thuần (`FR-REP-01`, `AC-TXN-03.3`).
  - Đồng bộ vòng đời với giao dịch gốc: Tạo kèm khi tạo giao dịch gốc có phí (`AC-TXN-01.1`, `AC-TRF-01.1`); xóa khi phí mới về 0 hoặc cập nhật khi đổi giá trị/danh mục (`FR-TXN-04`); xóa bản ghi phí và hoàn phí vào ví khi xóa giao dịch gốc (`AC-TXN-05.3`, `AC-TRF-02.1`).
  - Hiển thị đối soát: Hiển thị dòng riêng loại `EXPENSE` kèm nhãn liên kết tới giao dịch gốc trên sổ giao dịch thu chi (`FR-TXN-03`).

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

Bản ghi Điều chỉnh Số dư (`BalanceAdjustmentRecord`) ghi nhận sự kiện kiểm kê thực tế nhằm cân đối và đồng bộ số dư khả dụng của ví trên hệ thống với lượng tiền thực tế quan sát được ngoài đời thực. Đây là cơ chế đối soát độc lập, không thông qua các luồng thu chi hay chuyển tiền thông thường, giúp khắc phục kịp thời các sai lệch phát sinh ngoài hệ thống mà vẫn bảo đảm tính minh bạch và khả năng truy vết tài chính toàn vẹn.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `adjustmentId` | Mã định danh duy nhất của bản ghi điều chỉnh | Chuỗi ký tự định danh độc lập, do hệ thống tự động sinh khi ghi nhận thành công, mang tính bất biến và không bao giờ tái sử dụng. |
| `walletId` | Mã định danh ví được điều chỉnh | Tham chiếu đến thực thể `Wallet` đang ở trạng thái hoạt động; hệ thống từ chối điều chỉnh đối với các ví đã đưa vào lưu trữ. |
| `balanceBefore` | Số dư khả dụng của ví trước khi điều chỉnh | Đối tượng giá trị tiền tệ (`Money`), ghi nhận lại chính xác số dư sổ sách của ví ngay trước thời điểm kiểm kê; luôn không âm. |
| `balanceAfter` | Số dư thực tế quan sát được sau điều chỉnh | Đối tượng giá trị tiền tệ (`Money`), là số tiền thực tế người dùng nhập vào; bắt buộc phải lớn hơn hoặc bằng 0.00 VNĐ; hệ thống từ chối nếu số dư thực tế bị âm hoặc sai định dạng. |
| `difference` | Giá trị chênh lệch điều chỉnh | Đối tượng giá trị tiền tệ có dấu đại số, tính bằng số dư thực tế trừ số dư sổ sách ($balanceAfter - balanceBefore$); bắt buộc phải khác 0.00 VNĐ để tránh tạo bản ghi rỗng. |
| `adjustmentDate` | Ngày ghi nhận sự kiện điều chỉnh | Đối tượng giá trị ngày tháng (`TransactionDate`, định dạng `YYYY-MM-DD`), không được vượt quá ngày hiện tại; hệ thống mặc định điền ngày hiện tại khi tạo mới. |
| `reason` | Lý do hoặc diễn giải việc kiểm kê điều chỉnh | Chuỗi ký tự mô tả nguyên nhân chênh lệch (ví dụ: làm rơi tiền lẻ, quên ghi nhận chi tiêu nhỏ); là trường tùy chọn với độ dài tối đa 255 ký tự sau khi cắt khoảng trắng thừa hai đầu. |
| `createdAt` | Thời điểm ghi nhận bản ghi | Mốc thời gian tự động từ đồng hồ hệ thống, có tính bất biến, phục vụ đối soát và kiểm toán lịch sử biến động số dư. |

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Tính bất biến tuyệt đối: Bản ghi điều chỉnh số dư sau khi tạo thành công sẽ không bao giờ được phép chỉnh sửa hay xóa bỏ, đảm bảo tính toàn vẹn và khả năng kiểm toán của sổ cái tài chính.
  - Tách biệt luồng nghiệp vụ: Khoản điều chỉnh số dư hoàn toàn độc lập với các luồng thu nhập, chi tiêu hay chuyển tiền nội bộ; không gắn với danh mục phân loại và không được hạch toán vào báo cáo thu nhập hay chi tiêu định kỳ.
  - Ràng buộc số dư thực tế không âm: Số dư sau điều chỉnh bắt buộc phải lớn hơn hoặc bằng 0.00 VNĐ; hệ thống từ chối mọi thao tác điều chỉnh dẫn đến số dư âm.
  - Chênh lệch bắt buộc khác không: Giá trị chênh lệch giữa số dư thực tế và số dư sổ sách phải khác 0.00 VNĐ để tránh ghi nhận các bản ghi thừa không làm thay đổi số dư.
- Quy tắc vận hành:
  - Công thức tích hợp vào số dư khả dụng: Giá trị chênh lệch được tính trực tiếp vào luồng tiền vào (nếu chênh lệch dương) hoặc luồng tiền ra (nếu chênh lệch âm) trong công thức xác định số dư tức thời của ví:

$$
\text{Inflow}_w(t) \mathrel{+}= \max(0, \text{difference})
$$

$$
\text{Outflow}_w(t) \mathrel{+}= \max(0, -\text{difference})
$$

  - Cập nhật số dư tức thời: Ngay khi bản ghi điều chỉnh được lưu thành công, số dư khả dụng mới của ví lập tức phản ánh đúng con số thực tế quan sát được và sẵn sàng cho các giao dịch tiếp theo.

## 2.4 Đặc tả Chi tiết các Đối tượng Giá trị

Phần này đặc tả chi tiết các đối tượng giá trị được sử dụng trong toàn bộ hệ thống. Khác với thực thể, đối tượng giá trị không có định danh độc lập mà được định danh hoàn toàn bằng tập hợp các giá trị thuộc tính, mang tính bất biến tuyệt đối và đóng vai trò bảo đảm độ chính xác số học cùng tính nhất quán dữ liệu trong mọi phép tính tài chính.

### 2.4.1 Tiền tệ và Độ chính xác Số học - Money

`Money` là đối tượng giá trị biểu diễn các đại lượng tiền tệ trong hệ thống, đảm bảo tính chính xác số học tuyệt đối và loại trừ hoàn toàn các sai số làm tròn số học nhị phân trong toàn bộ các phép tính toán tài chính. Đối tượng này mang tính bất biến (`Immutable`), không có định danh độc lập và được định danh hoàn toàn bởi giá trị số tiền cùng đơn vị tiền tệ kèm theo.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `amount` | Giá trị định lượng số tiền | Đại lượng số thập phân có độ chính xác cố định với 2 chữ số sau dấu phẩy; nghiêm cấm lưu trữ dưới dạng số thực dấu chấm động nhị phân. |
| `currency` | Đơn vị tiền tệ | Đơn vị tiền tệ chuẩn hóa của hệ thống, mặc định và cố định là đồng Việt Nam (`VNĐ`). |

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Độ chính xác số học cố định: Toàn bộ dữ liệu tiền tệ bắt buộc phải được lưu trữ và tính toán dưới dạng số thập phân có độ chính xác cố định với đúng 2 chữ số thập phân, tuyệt đối không sử dụng kiểu dữ liệu số thực dấu chấm động nhị phân (`float`, `double`) nhằm ngăn chặn triệt để hiện tượng sai số làm tròn tích lũy.
  - Gắn liền đơn vị tiền tệ: Mọi đại lượng tiền tệ đều gắn liền với đơn vị VNĐ, không thực hiện quy đổi tỷ giá ngoại tệ trong phạm vi nghiệp vụ hiện tại.
- Quy tắc vận hành:
  - Quy tắc làm tròn ngân hàng (`Banker's Rounding`): Đối với các phép tính chia tiền, tính tỷ trọng hoặc phân bổ trung gian, hệ thống áp dụng nguyên tắc làm tròn nửa chẵn (làm tròn về số chẵn gần nhất khi phần số lẻ đúng bằng 0.5 đơn vị cuối) nhằm triệt tiêu độ lệch thiên kiến thống kê.
  - Quy cách hiển thị: Trên giao diện tổng quan, số tiền được hiển thị dưới dạng số nguyên có dấu phân cách hàng nghìn (ví dụ: `1,500,000 VNĐ`), và hiển thị đầy đủ hai chữ số thập phân trên các báo cáo phân tích chi tiết cũng như trong tệp dữ liệu sao lưu.

### 2.4.2 Ngày giao dịch và Mốc thời gian - TransactionDate

`TransactionDate` là đối tượng giá trị xác định thời điểm phát sinh thực tế của các sự kiện tài chính (thu chi, chuyển tiền nội bộ, kiểm kê số dư) theo trình tự lịch pháp. Đối tượng này mang tính bất biến, đảm bảo mọi mốc thời gian đều chuẩn xác và làm căn cứ xác định thứ tự thời gian trong lịch sử giao dịch.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `date` | Mốc ngày phát sinh sự kiện tài chính | Chuỗi ký tự biểu diễn ngày tháng theo chuẩn ISO-8601 quốc tế (`YYYY-MM-DD`). |

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Tính hợp lệ lịch pháp: Ngày tháng bắt buộc phải tồn tại thực tế trên lịch dương (bao gồm kiểm tra tính hợp lệ của năm nhuận và số ngày tối đa tương ứng của từng tháng).
  - Giới hạn thời gian: Mốc ngày giao dịch không được vượt quá ngày hiện tại được ghi nhận từ đồng hồ hệ thống (không cho phép ghi nhận trước các giao dịch tương lai). Mốc năm giao dịch nằm trong khoảng cho phép từ năm 2000 đến hiện tại.
- Quy tắc vận hành:
  - Thứ tự thời gian: Các giao dịch phát sinh được sắp xếp theo thứ tự ngày giảm dần khi hiển thị trên sổ giao dịch (mới nhất lên đầu). Trường hợp nhiều giao dịch có cùng ngày phát sinh, hệ thống sử dụng mốc thời gian tạo lập kỹ thuật (`createdAt`) làm tiêu chí sắp xếp phụ.
  - Mặc định ngày ghi nhận: Khi người dùng mở biểu mẫu thêm mới giao dịch, hệ thống tự động điền sẵn ngày hiện tại để tối ưu thao tác.

### 2.4.3 Loại giao dịch - TransactionType

`TransactionType` là đối tượng giá trị kiểu liệt kê định danh nghiệp vụ (Enumerated Value Object), mang tính bất biến tuyệt đối (`Immutable`), không có danh tính độc lập và được so sánh dựa trên toàn bộ giá trị biểu diễn. Đối tượng này đóng vai trò xác lập ranh giới phân cấp danh mục cao nhất (cấp 1) và định hình chiều tác động của dòng tiền lên số dư ví.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `value` | Chiều tác động dòng tiền | Enum đóng, chỉ nhận một trong hai giá trị: Thu nhập (`INCOME`, làm tăng số dư) hoặc Chi tiêu (`EXPENSE`, làm giảm số dư). Bất biến sau khi gán. |

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Tập giá trị hữu hạn: Gồm đúng 2 giá trị đóng và khép kín:
    - `INCOME` (Thu nhập): Đại diện cho dòng tiền đi vào ví (`Inflow`), làm gia tăng tài sản khả dụng.
    - `EXPENSE` (Chi tiêu): Đại diện cho dòng tiền đi ra khỏi ví (`Outflow`), làm suy giảm tài sản khả dụng.
  - Phân cấp danh mục cấp 1 cố định: Là gốc phân loại cấp 1 trong mô hình phân cấp 3 cấp của hệ thống. Hai nhánh `INCOME` và `EXPENSE` được hệ thống khởi tạo mặc định ban đầu, mang tính bất biến vĩnh viễn; người dùng tuyệt đối không thể sửa tên, xóa bỏ hoặc tạo thêm nhánh mới.
  - Quyết định chiều biến động số dư ví: Trong công thức bảo toàn số dư ví theo thời gian thực, giao dịch gắn với `INCOME` được cộng vào số dư ví tiếp nhận ($+Income_w(t)$), trong khi giao dịch gắn với `EXPENSE` được trừ khỏi số dư ví chi trả ($-Expense_w(t)$).
  - Quy chuẩn chi phí giao dịch: Chi phí giao dịch (`TransactionFee`) luôn mang bản chất là một khoản chi tiêu độc lập thuộc loại `EXPENSE` và bắt buộc liên kết với một danh mục thuộc nhánh Chi tiêu đang hoạt động.
  - Ranh giới tính duy nhất của tên nhóm nhu cầu: Tính duy nhất của tên Nhóm nhu cầu được giới hạn trong cùng một nhánh `TransactionType`. Hai nhóm nhu cầu thuộc hai nhánh khác nhau được phép trùng tên (ví dụ: nhóm "Khác" của nhánh Thu nhập và nhóm "Khác" của nhánh Chi tiêu).
  - Tính bất biến liên kết: Hệ thống nghiêm cấm mọi thao tác chuyển đổi `TransactionType` của một Nhóm nhu cầu hoặc Danh mục khi đã phát sinh giao dịch liên kết nhằm loại trừ triệt để sai lệch số liệu lịch sử.
- Quy tắc vận hành:
  - Căn cứ vào giá trị của `TransactionType`, hệ thống định tuyến chiều tác động dòng tiền lên số dư ví khả dụng và định hình cây phân cấp danh mục cấp 1 khi hiển thị biểu mẫu và báo cáo.

### 2.4.4 Dòng tiền thuần - NetCashFlow

`NetCashFlow` là đối tượng giá trị tiền tệ đo lường tổng hợp, phản ánh thặng dư hoặc thâm hụt tài chính ròng của người dùng trong một kỳ quan sát xác định. Đối tượng này mang tính bất biến tuyệt đối, không có danh tính độc lập và được tính toán đại số từ tập hợp các giao dịch phát sinh trong kỳ.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `amount` | Giá trị số tiền ròng | Số thập phân chính xác có dấu đại số, có thể mang giá trị âm, dương hoặc bằng 0. Bất biến sau khi khởi tạo. |
| `currency` | Đơn vị tiền tệ | Chuỗi ký tự định danh đơn vị tiền tệ chuẩn hóa, mặc định là `VNĐ`. |

**Công thức xác định đại số**

Xét kỳ thống kê quan sát $M$ (chuỗi 1 tháng hoặc chuỗi 12 tháng liên tục):

$$
\text{NetCashFlow}(M) = \sum_{t \in M} \text{Income}(t) - \sum_{t \in M} \text{Expense}(t)
$$

Trong đó:
- $\sum_{t \in M} \text{Income}(t)$: Tổng số tiền của các giao dịch thu nhập phát sinh trong kỳ $M$.
- $\sum_{t \in M} \text{Expense}(t)$: Tổng số tiền của các giao dịch chi tiêu phát sinh trong kỳ $M$, bao gồm cả các khoản chi phí giao dịch liên kết (`TransactionFee`) phát sinh trong kỳ.

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Dữ liệu bắt buộc biểu diễn bằng số thập phân chính xác, nghiêm cấm sử dụng số thực dấu chấm động nhị phân (`float`, `double`).
  - Độ chính xác tính toán và lưu trữ duy trì cố định ở mức 2 chữ số thập phân.
  - Phân định ngữ nghĩa tài chính: $\text{NetCashFlow} > 0$ thể hiện thặng dư dòng tiền; $\text{NetCashFlow} < 0$ thể hiện thâm hụt dòng tiền (chi vượt thu); $\text{NetCashFlow} = 0$ thể hiện trạng thái cân bằng thu chi.
- Quy tắc vận hành:
  - Trên giao diện người dùng, số tiền hiển thị theo quy cách số nguyên đồng cho VNĐ hoặc 2 chữ số thập phân trên báo cáo phân tích chi tiết.

### 2.4.5 Tỷ lệ tiết kiệm - SavingsRate

`SavingsRate` là đối tượng giá trị tỷ lệ phần trăm tài chính, đo lường mức độ tích lũy hình thành từ dòng tiền thuần so với tổng thu nhập tạo ra trong kỳ phân tích. Đối tượng này mang tính bất biến tuyệt đối, phụ thuộc trực tiếp vào kết quả của `NetCashFlow` và tuân thủ các quy tắc an toàn số học khi tính toán.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `rate` | Giá trị tỷ lệ phần trăm | Số thập phân chính xác. Duy trì tối thiểu 4 chữ số thập phân trong tính toán trung gian và làm tròn ngân hàng về đúng 2 chữ số thập phân khi hiển thị. |
| `unit` | Đơn vị đo lường | Đơn vị tỷ lệ phần trăm, cố định là ký hiệu `%`. |

**Công thức xác định đại số**

$$
\text{SavingsRate} = \begin{cases} \left(\dfrac{\text{NetCashFlow}}{\text{TotalIncome}}\right) \times 100\% & \text{khi } \text{TotalIncome} > 0 \\ 0.00\% & \text{khi } \text{TotalIncome} \le 0 \end{cases}
$$

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Xử lý biên an toàn: Khi $\text{TotalIncome} \le 0$ (kỳ phân tích không có thu nhập hoặc thu nhập bằng 0.00 VNĐ nhưng phát sinh chi tiêu), tỷ lệ tiết kiệm mặc định bằng đúng $0.00\%$. Quy tắc này loại bỏ hoàn toàn rủi ro chia cho 0 và bảo đảm tính hợp lý của chỉ số tài chính.
  - Phản ánh thâm hụt: Khi $\text{TotalIncome} > 0$ và $\text{NetCashFlow} < 0$ (chi vượt thu), tỷ lệ tiết kiệm mang giá trị âm, phản ánh tỷ lệ thâm hụt tương ứng trên tổng thu nhập.
- Quy tắc vận hành:
  - Bước tính toán trung gian bắt buộc duy trì độ chính xác tối thiểu 4 chữ số thập phân (khuyến nghị 6 chữ số thập phân).
  - Áp dụng quy tắc làm tròn ngân hàng (`Banker's Rounding` - làm tròn về số chẵn gần nhất khi phần dư đúng bằng 0.5 đơn vị cuối).
  - Kết quả hiển thị cuối cùng chuẩn hóa đúng 2 chữ số thập phân kèm ký hiệu đơn vị (ví dụ: `40.00%`).

### 2.4.6 Tỷ trọng cơ cấu chi tiêu - SpendingStructureShare

`SpendingStructureShare` là đối tượng giá trị tỷ trọng phân bổ, đo lường tỷ lệ đóng góp của từng nhóm nhu cầu (cấp 2) hoặc từng danh mục chi tiết (cấp 3) trên tổng quy mô dòng tiền trong kỳ phân tích. Đối tượng được áp dụng cho cả phân tích cơ cấu chi tiêu (`EXPENSE`) và cơ cấu thu nhập (`INCOME`).

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `share` | Giá trị tỷ trọng | Số thập phân chính xác nằm trong khoảng $[0.00, 100.00]$ đối với từng thành phần. |
| `unit` | Đơn vị đo lường | Đơn vị tỷ lệ phần trăm, cố định là ký hiệu `%`. |
| `scope` | Phạm vi gom cụm phân tích | Xác định cấp độ phân tích: Nhóm nhu cầu cấp 2 (`NeedGroup`) hoặc Danh mục chi tiết cấp 3 (`Category`). |

**Công thức xác định đại số**

$$
\text{SpendingStructureShare}_i = \begin{cases} \left(\dfrac{\text{ExpenseAmount}_i}{\text{TotalExpense}}\right) \times 100\% & \text{khi } \text{TotalExpense} > 0 \\ 0.00\% & \text{khi } \text{TotalExpense} = 0.00\text{ VNĐ} \end{cases}
$$

Trong đó:
- $\text{ExpenseAmount}_i$: Tổng số tiền chi tiêu của Nhóm nhu cầu thứ $i$ (khi gom cụm cấp 2) hoặc Danh mục chi tiết thứ $i$ (khi gom cụm cấp 3) trong kỳ phân tích.
- $\text{TotalExpense}$: Tổng chi tiêu của toàn bộ các nhóm hoặc danh mục trong kỳ phân tích, bao gồm cả các chi phí giao dịch liên kết.

*(Công thức áp dụng tương tự khi phân tích cơ cấu thu nhập `INCOME`).*

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Khi $\text{TotalExpense} = 0.00\text{ VNĐ}$ (kỳ phân tích không phát sinh chi tiêu), hệ thống hiển thị tổng tiền 0.00 VNĐ cùng tỷ trọng mặc định $0.00\%$ cho danh sách rỗng, không làm phát sinh lỗi chia cho 0.
  - Bảo toàn dữ liệu xóa mềm: Nếu trong kỳ phân tích xuất hiện giao dịch thuộc nhóm hoặc danh mục đã bị xóa trong quá khứ, hệ thống vẫn hiển thị độc lập mục đó kèm nhãn "(đã xóa)" với đúng số tiền và tỷ trọng tương ứng, không gộp vào mục khác.
  - Bất biến tổng cơ cấu: Tổng tỷ trọng của toàn bộ các mục thành phần trong cùng một cấp phân loại phải xấp xỉ bằng $100.00\%$:

$$
\sum_{i} \text{SpendingStructureShare}_i \approx 100.00\%
$$

    (Sai số cho phép tối đa trong khoảng $\pm 0.01\%$ do hiệu ứng phân rã làm tròn ngân hàng độc lập trên từng thành phần).
- Quy tắc vận hành:
  - Bước tính toán trung gian duy trì tối thiểu 4 chữ số thập phân.
  - Áp dụng quy cách làm tròn ngân hàng (`Banker's Rounding`), hiển thị kết quả cuối cùng với đúng 2 chữ số thập phân (ví dụ: `40.00%`).

## 2.5 Các Mối Quan hệ Ngữ nghĩa và Bất biến Miền

Phần này phân tích các mối quan hệ ngữ nghĩa cốt lõi chi phối sự tương tác giữa các thực thể trong mô hình miền. Mỗi mối quan hệ làm rõ bản chất liên kết giữa các khái niệm tài chính, các điều kiện bất biến bắt buộc tuân thủ, và cơ chế xử lý của hệ thống nhằm bảo đảm tính toàn vẹn dữ liệu.

### 2.5.1 Quan hệ Sở hữu Ví và Bảo toàn Số dư

Quan hệ giữa Ví và các dòng tiền phản ánh sự gắn kết chặt chẽ giữa một nguồn tiền xác định với toàn bộ các sự kiện tài chính tác động trực tiếp đến nó. Mỗi biến động tăng hoặc giảm của ví đều bắt nguồn từ một giao dịch cụ thể được ghi nhận trong lịch sử, thiết lập nên nguyên tắc bảo toàn số dư cốt lõi của hệ thống.

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Bất biến số dư không âm: Số dư khả dụng của mỗi ví tại mọi thời điểm bắt buộc phải lớn hơn hoặc bằng 0.00 VNĐ ($WalletBalance_w(t) \ge 0.00$). Hệ thống nghiêm cấm mọi hành vi xuất quỹ dẫn đến thâm hụt số dư khả dụng.
  - Không gán giá trị số dư trực tiếp: Số dư ví không phải là một thuộc tính được cập nhật bằng phép gán độc lập, mà là kết quả dẫn xuất tích lũy từ toàn bộ các luồng tiền đã phát sinh trong lịch sử.
- Quy tắc vận hành:
  - Công thức bảo toàn số dư thời gian thực: Số dư ví tại mốc thời gian $T$ được tính toán chính xác bằng tổng toàn bộ các dòng tiền đi vào trừ đi tổng các dòng tiền đi ra:

$$
WalletBalance_w(T) = \sum_{t \le T} \text{Inflow}_w(t) - \sum_{t \le T} \text{Outflow}_w(t)
$$

    Trong đó luồng tiền vào bao gồm các khoản thu nhập, nhận chuyển tiền từ ví khác và chênh lệch điều chỉnh dương. Luồng tiền ra bao gồm các khoản chi tiêu, chuyển tiền sang ví khác, chi phí giao dịch liên kết và chênh lệch điều chỉnh âm.
  - Kiểm tra khả năng chi trả trước khi thực thi: Trước bất kỳ thao tác nào làm giảm số dư ví (ghi nhận chi tiêu, trích tiền chuyển đi, nộp phí giao dịch, điều chỉnh giảm kiểm kê, hoặc sửa và xóa giao dịch thu nhập trong quá khứ), hệ thống bắt buộc đối chiếu số dư hiện có. Nếu số dư khả dụng không đủ đáp ứng toàn bộ số tiền cần trích, hệ thống sẽ từ chối thực hiện và thông báo cảnh báo rõ ràng cho người dùng.

### 2.5.2 Quan hệ Phân cấp Danh mục 3 Cấp

Quan hệ phân cấp danh mục là quan hệ hợp thành nghiêm ngặt giữa ba tầng khái niệm: Loại thu chi `TransactionType` (Cấp 1, gốc cố định bất biến) $\rightarrow$ Nhóm nhu cầu `NeedGroup` (Cấp 2, phân nhóm mục đích) $\rightarrow$ Danh mục phân loại `Category` (Cấp 3, đơn vị gán nhãn giao dịch). Cây phân cấp này xác định ranh giới mà các thực thể `Category` và `NeedGroup` có thể tồn tại và vận hành, đồng thời chi phối toàn bộ tính hợp lệ của dữ liệu thu chi trong hệ thống.

**Quy tắc bất biến và vận hành**

- *Bất biến cấu trúc*: Cấp 3 (`Category`) là cấp duy nhất được phép liên kết trực tiếp với giao dịch thu chi; mọi yêu cầu liên kết trực tiếp lên cấp 1 hoặc cấp 2 đều bị từ chối. Mỗi `Category` và `NeedGroup` bắt buộc thuộc về đúng một nhánh `TransactionType` xác định và không được phép thay đổi nhánh đó sau khi khởi tạo. Hệ thống nghiêm cấm tồn tại danh mục mồ côi (`Orphan Category`) — mọi danh mục phải trực thuộc một nhóm cha hợp lệ đang tồn tại.
- *Bất biến tổng thể cây phân cấp*: Mỗi nhánh `TransactionType` (`INCOME`, `EXPENSE`) luôn phải duy trì tối thiểu một `NeedGroup` ở trạng thái hoạt động; hành động xóa nhóm cuối cùng của một nhánh đều bị từ chối. Tập giá trị hai nhánh này là bất biến vĩnh viễn, người dùng không thể chỉnh sửa, xóa bỏ hay mở rộng thêm nhánh mới.
- *Vận hành xóa và khôi phục*: Thao tác xóa `NeedGroup` kéo theo việc xóa phân tầng toàn bộ các `Category` con trực thuộc theo quy tắc xóa mềm (nếu đã có giao dịch) hoặc xóa hoàn toàn (nếu chưa từng có giao dịch). Thao tác chuyển nhóm cha của `Category` chỉ được phép khi nhóm đích đang hoạt động và thuộc cùng nhánh Loại thu chi. Dữ liệu giao dịch lịch sử liên kết với danh mục hoặc nhóm đã xóa mềm được bảo toàn nguyên vẹn trên các báo cáo phân tích kèm nhãn "(đã xóa)".

### 2.5.3 Quan hệ Phụ thuộc Sinh tử giữa Giao dịch gốc và Phí

Mối liên kết giữa giao dịch gốc và chi phí giao dịch là quan hệ hợp thành phụ thuộc bền chặt (`Composition`), trong đó khoản phí phụ trợ do bên thứ ba thu phát sinh đồng thời và phụ thuộc hoàn toàn vào sự tồn tại của giao dịch phát sinh dòng tiền chính (giao dịch thu chi hoặc giao dịch chuyển tiền nội bộ).

**Quy tắc bất biến và vận hành**

- Quy tắc bất biến:
  - Loại trừ tuyệt đối chi phí mồ côi: Một khoản phí giao dịch không bao giờ được phép sinh ra, tồn tại độc lập hoặc đứng ngoài vòng đời của một giao dịch gốc. Mọi bản ghi chi phí bắt buộc phải gắn liền với mã định danh của một giao dịch gốc hợp lệ.
  - Ràng buộc phân loại và danh mục: Chi phí giao dịch luôn mang bản chất kinh tế là một khoản chi tiêu và bắt buộc phải được gắn với một danh mục phân loại chi tiết đang hoạt động thuộc nhánh Chi tiêu. Số tiền chi phí bắt buộc không âm và chỉ phát sinh bản ghi khi giá trị lớn hơn 0.00 VNĐ.
  - Cấm thao tác trực tiếp: Người dùng không được phép thêm mới, chỉnh sửa hay xóa độc lập bản ghi phí; mọi thao tác với chi phí đều phải thông qua giao diện của giao dịch gốc.
- Quy tắc vận hành:
  - Thực thi nguyên tử: Việc ghi nhận giao dịch gốc và khoản phí đi kèm được thực hiện đồng thời. Nếu một trong hai bản ghi gặp lỗi về tính hợp lệ (ví dụ ví không đủ tiền trả phí hoặc danh mục phí không hợp lệ), toàn bộ thao tác sẽ bị hủy bỏ để đảm bảo dữ liệu không bị sai lệch.
  - Đồng bộ vòng đời: Khi người dùng chỉnh sửa giá trị phí của giao dịch gốc, hệ thống tự động cập nhật lại bản ghi chi phí tương ứng (hoặc xóa bỏ bản ghi phí nếu người dùng đưa mức phí về 0). Khi giao dịch gốc bị xóa, bản ghi phí liên kết sẽ tự động bị xóa đồng thời, và toàn bộ số tiền phí đã trích sẽ được hoàn nguyên đầy đủ về ví thanh toán.
