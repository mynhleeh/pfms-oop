# 2. Mô hình miền khái niệm

Mô hình miền khái niệm phản ánh các khái niệm nghiệp vụ trong quản lý tài chính cá nhân, tách biệt với chi tiết kỹ thuật (lớp lập trình, cơ sở dữ liệu hay giao diện). Mô hình định hình ngôn ngữ nghiệp vụ thống nhất, phục vụ 3 mục tiêu cốt lõi: quản lý nguồn tiền theo ví, kiểm soát dòng tiền thu chi và phân tích cơ cấu tài chính định kỳ.

## 2.1 Phương pháp luận xác định lớp khái niệm

Hệ thống kết hợp hai kỹ thuật phân tích để xác định các lớp khái niệm mà không phụ thuộc vào cấu trúc cài đặt:
- **Danh sách phân loại**: Rà soát bài toán theo các nhóm khái niệm chuẩn: giao dịch tài chính, nguồn chứa tiền tệ, cấu trúc phân loại thu chi và bản ghi kiểm kê đối soát.
- **Phân tích cụm danh từ**: Trích xuất các danh từ nghiệp vụ thực tế từ mô tả bài toán (loại bỏ danh từ công nghệ như giao diện, nút bấm, cơ sở dữ liệu, tệp tin) để phân định thành lớp khái niệm, thuộc tính hoặc đại lượng đo lường.

Các khái niệm trích xuất được chia thành hai nhóm:
- **Thực thể (Entity)**: Có định danh độc lập, duy nhất và duy trì vòng đời biến đổi trạng thái qua các giao dịch.
- **Đối tượng giá trị (Value Object)**: Mô tả đặc tính hoặc đại lượng đo lường không có định danh độc lập, bất biến và bảo đảm độ chính xác số học tuyệt đối.

## 2.2 Bảng phân loại thực thể và đối tượng giá trị

Hệ thống gồm 7 thực thể quản lý dữ liệu tài chính và 6 đối tượng giá trị phục vụ đo lường, tính toán.

### 2.2.1 Danh mục thực thể miền

| Thực thể miền | Thuộc tính chính | Quy tắc nghiệp vụ |
| :--- | :--- | :--- |
| `Wallet` | Mã ví, tên ví, ghi chú, trạng thái hoạt động | Tên ví duy nhất (`BR-NAM-01`). Số dư tính từ lịch sử giao dịch và không âm (`BR-FIN-02`). |
| `IncomeExpenseTransaction` | Mã giao dịch, số tiền, ngày giao dịch, ghi chú | Gắn với một ví và một danh mục cụ thể; tự động tăng (thu) hoặc giảm (chi) số dư ví ngay khi ghi nhận (`BR-FIN-02`, `BR-FIN-04`). |
| `TransferTransaction` | Mã chuyển tiền, số tiền, ngày chuyển, ghi chú | Ví gửi phải khác ví nhận. Tiền trừ ở ví gửi và cộng vào ví nhận đồng thời để bảo toàn tổng số dư (`BR-FIN-03`). |
| `TransactionFee` | Mã chi phí, số tiền phí | Không âm, gắn với một danh mục chi tiêu. Phụ thuộc vào giao dịch gốc; xóa giao dịch gốc sẽ xóa phí kèm theo (`BR-FIN-02`, `BR-FIN-03`). |
| `Category` | Mã danh mục, tên danh mục, trạng thái hoạt động | Tên duy nhất trong cùng nhóm cha (`BR-NAM-01`). Danh mục đã có giao dịch chỉ xóa mềm (ẩn đi), không xóa vĩnh viễn (`BR-FIN-04`). |
| `NeedGroup` | Mã nhóm, tên nhóm | Tên duy nhất trong cùng loại thu chi (`BR-NAM-01`). Mỗi loại thu chi phải giữ tối thiểu một nhóm đang hoạt động. |
| `BalanceAdjustmentRecord` | Mã bản ghi, số dư trước, số dư sau, chênh lệch, ngày kiểm kê, lý do | Lưu khoản chênh lệch khi kiểm kê thực tế (`BR-FIN-02`); bất biến sau khi tạo. |

### 2.2.2 Danh mục đối tượng giá trị

| Đối tượng giá trị | Dữ liệu cấu thành | Ý nghĩa và quy tắc tính |
| :--- | :--- | :--- |
| `Money` | Số tiền thập phân, đơn vị tiền tệ | Lưu số tiền thập phân chính xác, không dùng số thực nhị phân (`BR-FIN-01`); chia tiền làm tròn ngân hàng (nửa chẵn). |
| `TransactionDate` | Ngày, tháng, năm | Ngày phát sinh giao dịch hợp lệ theo lịch thực tế. |
| `TransactionType` | Giá trị cố định: Thu nhập hoặc Chi tiêu | Gồm hai giá trị cố định, quyết định chiều cộng/trừ tiền vào ví (`BR-FIN-04`). |
| `NetCashFlow` | Số tiền | Tổng thu nhập trừ tổng chi tiêu trong kỳ (`BR-FIN-01`). |
| `SavingsRate` | Tỷ lệ phần trăm | Bằng dòng tiền thuần chia tổng thu nhập, giữ tối thiểu 4 chữ số thập phân trung gian và làm tròn ngân hàng 2 chữ số (`0.00%` khi tổng thu nhập $\le 0$ theo `BR-FIN-01`). |
| `SpendingStructureShare` | Tỷ lệ phần trăm | Tỷ trọng thu/chi (gồm phí theo `BR-FIN-02`) của nhóm hoặc danh mục trên tổng kỳ/nhóm cha, làm tròn ngân hàng 2 chữ số (`0.00%` khi mẫu số $\le 0$ theo `BR-FIN-01`). |

## 2.3 Đặc tả chi tiết các lớp thực thể

Phần này đặc tả bản chất khái niệm, cấu trúc thuộc tính (kèm tham chiếu SRS) và quy tắc bất biến, vận hành của 7 thực thể miền.

### 2.3.1 Thực thể Ví - Wallet

Ví (`Wallet`) đại diện cho nguồn tiền hoặc tài khoản thanh toán của người dùng. Hệ thống tạo sẵn ví "Tiền mặt" mặc định và cho phép tạo thêm các ví khác để quản lý tài sản, kiểm soát số dư không âm theo thời gian thực.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `walletId` | Mã định danh ví | Chuỗi ID sinh tự động khi tạo mới (`FR-WAL-01`), bất biến và không tái sử dụng (`FR-WAL-03`). |
| `name` | Tên ví | Từ 1–50 ký tự sau khi `trim`, không để trống (`BR-NAM-01`, `ERR-NAM-001`). Duy nhất toàn hệ thống (không phân biệt hoa thường, tính cả ví lưu trữ) (`FR-WAL-01`, `ERR-NAM-002`); giải phóng tên khi xóa vĩnh viễn (`FR-WAL-03`). Ví mặc định có tên cố định là "Tiền mặt". |
| `status` | Trạng thái hoạt động | Nhận giá trị `ACTIVE` (mặc định) hoặc `ARCHIVED` (`FR-WAL-01`, `FR-WAL-03`). Cấm xóa hoặc lưu trữ ví "Tiền mặt" (`AC-WAL-03.2`). Ví khác chỉ xóa vĩnh viễn khi chưa có giao dịch, nếu đã có chỉ được chuyển sang `ARCHIVED` (`AC-WAL-03.3`). |
| `note` | Ghi chú | Mô tả tùy chọn, tối đa 255 ký tự sau khi `trim` (`CON-05`, `FR-WAL-01`, `FR-WAL-03`). |
| `createdAt` | Thời điểm tạo | Mốc thời gian từ `DE-01`, bất biến, dùng để sắp xếp và đối soát (`FR-WAL-01`, `FR-WAL-02`). |
| `currentBalance` | Số dư khả dụng (dẫn xuất) | Kiểu `Money`, tính từ lịch sử dòng tiền (`BR-FIN-02`), không gán trực tiếp. Khởi điểm `0.00 VNĐ` (`FR-WAL-01`), luôn $\ge 0.00$ VNĐ (`ERR-WAL-001`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Số dư khả dụng luôn không âm ($WalletBalance_w(t) \ge 0.00$ VNĐ) và chỉ biến động qua các giao dịch thu chi, chuyển tiền hoặc điều chỉnh số dư hợp lệ (`BR-FIN-02`, `ERR-WAL-001`).
  - Tên ví duy nhất toàn hệ thống giữa cả ví `ACTIVE` và `ARCHIVED` (`BR-NAM-01`, `ERR-NAM-002`).
  - Ví "Tiền mặt" mặc định tồn tại vĩnh viễn, cấm xóa hoặc lưu trữ (`FR-WAL-03`, `AC-WAL-03.2`).
- **Quy tắc vận hành**:
  - Tính số dư thời gian thực (`BR-FIN-02`):
    $$WalletBalance_w(T) = \sum_{t \le T} \text{Inflow}_w(t) - \sum_{t \le T} \text{Outflow}_w(t)$$
    với $\text{Inflow}_w(t) = Income_w(t) + TransferIn_w(t) + \max(0, Adjustment_w(t))$ và $\text{Outflow}_w(t) = Expense_w(t) + TransferOut_w(t) + \max(0, -Adjustment_w(t))$.
  - Kiểm tra khả năng chi trả: Mọi thao tác giảm số dư (`FR-TXN-01`, `FR-TXN-02`, `FR-TXN-04`, `FR-TXN-05`, `FR-TRF-01`, `FR-WAL-05`) phải kiểm tra đủ số dư khả dụng, nếu thiếu từ chối theo `ERR-WAL-001`.
  - Tỷ lệ phân bổ ví: Bằng $(\text{Số dư ví} / \text{Tổng số dư}) \times 100\%$ (`FR-WAL-04`), làm tròn ngân hàng (`BR-FIN-01`); gán `0.00%` nếu tổng số dư bằng 0 (`ERR-FIN-002`). Ví `ARCHIVED` có số dư $> 0$ hiển thị kèm nhãn "(đã lưu trữ)" (`AC-WAL-04.3`).

### 2.3.2 Thực thể Giao dịch thu chi - IncomeExpenseTransaction

Giao dịch thu chi (`IncomeExpenseTransaction`) ghi nhận dòng tiền vào (`INCOME`) hoặc ra (`EXPENSE`) tại một ví cụ thể theo một danh mục cấp 3, trực tiếp cập nhật số dư ví và cung cấp dữ liệu cho báo cáo định kỳ.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `transactionId` | Mã định danh giao dịch | Chuỗi ID sinh tự động, bất biến và không tái sử dụng (`FR-TXN-01`, `FR-TXN-02`). |
| `amount` | Số tiền giao dịch | Kiểu `Money`, bắt buộc $> 0.00$ VNĐ (`BR-FIN-01`, `FR-TXN-01`, `FR-TXN-02`, `ERR-FIN-001`). |
| `transactionDate` | Ngày giao dịch | Kiểu `TransactionDate` (`YYYY-MM-DD`), không vượt quá ngày hiện tại từ `DE-01` (`FR-TXN-01`, `FR-TXN-02`); mặc định là ngày hiện tại. |
| `transactionType` | Loại giao dịch | Kiểu `TransactionType` (`INCOME` hoặc `EXPENSE`, `BR-FIN-04`). Bất biến sau khi tạo (`FR-TXN-04`). |
| `walletId` | Mã ví liên kết | Tham chiếu `Wallet` đang `ACTIVE` (`FR-TXN-01`, `FR-TXN-02`). |
| `categoryId` | Mã danh mục phân loại | Tham chiếu `Category` cấp 3 đang `ACTIVE` cùng nhánh loại thu chi (`BR-FIN-04`). Khi sửa giao dịch, cho phép giữ nguyên danh mục cũ dù đã xóa mềm (`FR-TXN-04`). |
| `note` | Ghi chú | Mô tả tùy chọn, tối đa 255 ký tự sau khi `trim` (`CON-05`, `FR-TXN-01`–`FR-TXN-03`). |
| `createdAt` | Thời điểm tạo | Mốc thời gian từ `DE-01`, bất biến, dùng sắp xếp thứ cấp khi trùng ngày giao dịch (`FR-TXN-03`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Số tiền bắt buộc $> 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`); `transactionType` bất biến sau khi tạo (`FR-TXN-04`).
  - Liên kết với một ví `ACTIVE` và một danh mục cấp 3 `ACTIVE` cùng loại thu chi (`BR-FIN-04`); cấm liên kết lên cấp 1 hoặc cấp 2 (`AC-CAT-01.2`).
  - Khả năng chi trả: Giao dịch `EXPENSE` phải thỏa $WalletBalance_w \ge amount + feeAmount$ (`BR-FIN-02`, `ERR-WAL-001`).
- **Quy tắc vận hành**:
  - Cập nhật số dư và phí: Cộng $amount$ vào ví nếu `INCOME`, trừ $amount$ nếu `EXPENSE` (`BR-FIN-02`). Khi $feeAmount > 0$, tự động tạo `TransactionFee` đi kèm trong cùng một thao tác nguyên tử (`FR-TXN-01`, `FR-TXN-02`).
  - Chỉnh sửa hai pha (`FR-TXN-04`): Hoàn nguyên số dư trên ví cũ (kiểm tra không âm `ERR-WAL-001` nếu hoàn nguyên khoản thu), áp dụng số dư mới lên ví mới (kiểm tra đủ chi trả nếu là khoản chi) và đồng bộ phí liên kết.
  - Xóa giao dịch (`FR-TXN-05`): Hoàn nguyên số dư ví (kiểm tra `ERR-WAL-001` nếu xóa khoản thu) và xóa đồng thời `TransactionFee` đi kèm (`AC-TXN-05.3`).

### 2.3.3 Thực thể Giao dịch chuyển tiền - TransferTransaction

Giao dịch chuyển tiền (`TransferTransaction`) ghi nhận việc điều chuyển tiền nội bộ giữa hai ví theo cơ chế đồng thời, bảo toàn tổng số dư hệ thống trước phí và không hạch toán khoản chuyển gốc vào thu nhập hay chi tiêu.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `transferId` | Mã định danh chuyển tiền | Chuỗi ID sinh tự động, bất biến và không tái sử dụng (`FR-TRF-01`). |
| `sourceWalletId` | Mã ví nguồn | Tham chiếu ví trích tiền đang `ACTIVE`, bắt buộc khác ví đích (`BR-FIN-03`, `ERR-TRF-001`). |
| `destinationWalletId` | Mã ví đích | Tham chiếu ví nhận tiền đang `ACTIVE`, bắt buộc khác ví nguồn (`BR-FIN-03`, `ERR-TRF-001`). |
| `amount` | Số tiền chuyển | Kiểu `Money`, bắt buộc $> 0.00$ VNĐ (`BR-FIN-01`, `BR-FIN-03`, `FR-TRF-01`, `ERR-TRF-002`). |
| `transactionDate` | Ngày chuyển tiền | Kiểu `TransactionDate` (`YYYY-MM-DD`), không vượt quá ngày hiện tại từ `DE-01` (`FR-TRF-01`). |
| `note` | Ghi chú | Mô tả tùy chọn, tối đa 255 ký tự sau khi `trim` (`CON-05`, `FR-TRF-01`, `FR-TRF-02`). |
| `createdAt` | Thời điểm tạo | Mốc thời gian từ `DE-01`, bất biến, phục vụ sắp xếp và kiểm toán (`FR-TRF-02`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Ví nguồn và ví đích phải khác nhau ($sourceWalletId \ne destinationWalletId$, `BR-FIN-03`, `ERR-TRF-001`); số tiền chuyển $> 0.00$ VNĐ (`ERR-TRF-002`).
  - Bảo toàn tổng số dư hệ thống trước phí: $\sum WalletBalance_w(\text{sau}) = \sum WalletBalance_w(\text{trước})$ (`BR-FIN-03`); nếu có phí $Fee$, tổng số dư giảm đúng bằng $Fee$.
  - Ví nguồn phải đủ số dư thanh toán cả tiền chuyển và phí ($WalletBalance_{w_{src}} \ge amount + feeAmount$, `BR-FIN-02`, `ERR-WAL-001`).
- **Quy tắc vận hành**:
  - Ghi nhận đồng thời (`Atomic Execution`): Trừ ví nguồn $(amount + feeAmount)$ và cộng ví đích $amount$ trong cùng một giao dịch nguyên tử (`BR-FIN-03`). Nếu $feeAmount > 0$, tự động tạo `TransactionFee` (loại `EXPENSE`) trích từ ví nguồn (`FR-TRF-01`).
  - Hoàn tác khi xóa (`FR-TRF-02`): Kiểm tra ví đích đủ số dư $\ge amount$ (`ERR-WAL-001`), trừ $amount$ ở ví đích, hoàn $(amount + feeAmount)$ về ví nguồn và xóa `TransactionFee` liên kết (`BR-FIN-03`, `AC-TRF-02.1`).

### 2.3.4 Thực thể Chi phí giao dịch - TransactionFee

Chi phí giao dịch (`TransactionFee`) là khoản phí phụ do bên thứ ba thu đi kèm giao dịch gốc (thu chi hoặc chuyển tiền), mang bản chất chi tiêu (`EXPENSE`) và phụ thuộc sinh tử (`Composition`) vào giao dịch gốc.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `feeId` | Mã định danh chi phí | Chuỗi ID sinh tự động, bất biến và không tái sử dụng (`BR-FIN-02`, `FR-TXN-01`, `FR-TRF-01`). |
| `amount` | Số tiền phí | Kiểu `Money`, $\ge 0.00$ VNĐ (`BR-FIN-01`, `BR-FIN-02`, `ERR-FEE-001`). Không tạo bản ghi khi phí bằng `0.00 VNĐ`. |
| `walletId` | Mã ví chịu phí | Tham chiếu ví phát sinh thu chi hoặc ví chuyển tiền nguồn đang `ACTIVE` (`BR-FIN-02`). |
| `categoryId` | Mã danh mục phí | Tham chiếu `Category` cấp 3 đang `ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-02`, `BR-FIN-04`, `ERR-FEE-002`, `ERR-FEE-003`). |
| `parentTransactionId` | Mã giao dịch gốc | Tham chiếu `transactionId` hoặc `transferId`, bất biến, loại trừ phí mồ côi (`BR-FIN-02`). |
| `parentTransactionType` | Loại giao dịch gốc | Nhận giá trị `INCOME_EXPENSE` hoặc `TRANSFER`, phục vụ truy nguyên và hiển thị (`FR-TXN-03`, `FR-TRF-01`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Số tiền phí không âm ($amount \ge 0.00$ VNĐ, `ERR-FEE-001`); khi $amount > 0.00$ VNĐ bắt buộc gắn danh mục cấp 3 `ACTIVE` thuộc nhánh `EXPENSE` (`ERR-FEE-002`, `ERR-FEE-003`).
  - Không tồn tại phí mồ côi (`No Orphan Fee`): Phí luôn gắn với `parentTransactionId`; cấm người dùng tạo, sửa hoặc xóa trực tiếp độc lập với giao dịch gốc (`BR-FIN-02`, `FR-TXN-04`, `FR-TXN-05`).
- **Quy tắc vận hành**:
  - Hạch toán và hiển thị: Ghi nhận như một khoản `EXPENSE` trừ vào ví chịu phí, tính vào tổng chi tiêu và dòng tiền thuần (`FR-REP-01`, `AC-TXN-03.3`), hiển thị dòng riêng kèm nhãn liên kết tới giao dịch gốc trên sổ thu chi (`FR-TXN-03`).
  - Đồng bộ vòng đời: Tự động tạo, cập nhật hoặc xóa kèm hoàn phí theo các thao tác tạo, sửa, xóa trên giao dịch gốc (`AC-TXN-01.1`, `AC-TRF-01.1`, `FR-TXN-04`, `AC-TXN-05.3`, `AC-TRF-02.1`).

### 2.3.5 Thực thể Danh mục phân loại - Category

Danh mục phân loại (`Category`) là đơn vị phân loại cấp 3 gán nhãn mục đích tài chính cho giao dịch. Đây là cấp duy nhất trong cây danh mục được liên kết trực tiếp với giao dịch thu chi và phí giao dịch.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `categoryId` | Mã định danh danh mục | Chuỗi ID sinh tự động, bất biến và không tái sử dụng kể cả khi đã xóa (`BR-FIN-04`, `FR-CAT-02`). |
| `name` | Tên danh mục | Từ 1–50 ký tự sau khi `trim`, không để trống (`BR-NAM-01`, `ERR-NAM-001`). Duy nhất trong cùng `NeedGroup` cha đang hoạt động, không phân biệt hoa thường (`ERR-NAM-002`). |
| `needGroupId` | Mã nhóm nhu cầu cha | Tham chiếu `NeedGroup` cấp 2 đang `ACTIVE` (`BR-FIN-04`, `FR-CAT-01`, `ERR-CAT-005`). Chỉ được chuyển sang nhóm `ACTIVE` cùng loại thu chi (`ERR-CAT-001`, `ERR-CAT-003`). |
| `status` | Trạng thái hoạt động | Nhận giá trị `ACTIVE` hoặc `DELETED` (`BR-FIN-04`, `FR-CAT-03`): xóa vĩnh viễn nếu chưa có giao dịch; chuyển sang `DELETED` (ẩn khi nhập mới, giữ lịch sử kèm nhãn "(đã xóa)") nếu đã có giao dịch. |
| `icon` | Biểu tượng | Chuỗi mã icon tùy chọn, tối đa 50 ký tự (`FR-CAT-02`). |
| `color` | Mã màu hiển thị | Chuỗi mã màu Hex tùy chọn (ví dụ `#4CAF50`), tối đa 10 ký tự (`FR-CAT-02`). |
| `transactionType` | Loại thu chi (dẫn xuất) | Suy dẫn từ `needGroup.transactionType`, bất biến theo nhóm cha (`BR-FIN-04`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Tên danh mục duy nhất trong cùng nhóm cha đang hoạt động (`BR-NAM-01`, `ERR-NAM-002`).
  - Bắt buộc thuộc một `NeedGroup` hợp lệ (không có danh mục mồ côi, `BR-FIN-04`) và là cấp duy nhất được gán trực tiếp cho giao dịch (`FR-CAT-01`, `AC-CAT-01.2`).
- **Quy tắc vận hành**:
  - Xóa danh mục: Xóa vĩnh viễn nếu chưa phát sinh giao dịch; chuyển sang `DELETED` nếu đã có giao dịch lịch sử (`BR-FIN-04`, `FR-CAT-03`, `AC-CAT-03.1`).
  - Chuyển nhóm cha: Chỉ chuyển sang nhóm `ACTIVE` cùng `TransactionType` và không trùng tên tại nhóm đích (`ERR-CAT-001`, `ERR-CAT-003`, `ERR-NAM-002`).
  - Khôi phục: Chỉ khôi phục khi nhóm cha đang `ACTIVE` (nếu nhóm cha đã xóa phải khôi phục nhóm cha trước theo `ERR-CAT-004`) và không trùng tên với danh mục đang hoạt động trong nhóm (`ERR-NAM-002`).

### 2.3.6 Thực thể Nhóm nhu cầu - NeedGroup

Nhóm nhu cầu (`NeedGroup`) là đơn vị phân loại trung gian cấp 2 gom cụm các danh mục chi tiết có cùng tính chất tài chính, làm cơ sở quản lý ngân sách và phân tích cơ cấu thu chi theo nhóm.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `needGroupId` | Mã định danh nhóm | Chuỗi ID sinh tự động, bất biến và không tái sử dụng (`BR-FIN-04`, `FR-CAT-02`). |
| `name` | Tên nhóm nhu cầu | Từ 1–50 ký tự sau khi `trim`, không để trống (`BR-NAM-01`, `ERR-NAM-001`). Duy nhất trong cùng nhánh `TransactionType` đang hoạt động, không phân biệt hoa thường (`ERR-NAM-002`). Không dùng tên nhóm làm điều kiện cứng trong logic xử lý (`BR-FIN-04`). |
| `transactionType` | Loại thu chi gốc | Kiểu `TransactionType` (`INCOME` hoặc `EXPENSE`, `BR-FIN-04`, `FR-CAT-01`), bất biến sau khi tạo (`ERR-CAT-001`). |
| `status` | Trạng thái hoạt động | Nhận giá trị `ACTIVE` hoặc `DELETED` (`BR-FIN-04`, `FR-CAT-03`). Cấm xóa nhóm `ACTIVE` cuối cùng của mỗi loại thu chi (`ERR-CAT-002`). |
| `icon` | Biểu tượng | Chuỗi mã icon tùy chọn, tối đa 50 ký tự (`FR-CAT-02`). |
| `color` | Mã màu hiển thị | Chuỗi mã màu Hex tùy chọn, tối đa 10 ký tự (`FR-CAT-02`, `FR-REP-02`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Tên nhóm duy nhất trong cùng nhánh `TransactionType` đang hoạt động (`BR-NAM-01`, `ERR-NAM-002`); `transactionType` cố định vĩnh viễn (`ERR-CAT-001`).
  - Mỗi nhánh `INCOME` và `EXPENSE` phải duy trì tối thiểu một nhóm `ACTIVE` (`BR-FIN-04`, `FR-CAT-03`, `ERR-CAT-002`, `AC-CAT-03.3`).
- **Quy tắc vận hành**:
  - Xóa kéo theo (`cascade`): Khi xóa nhóm, các danh mục con đã có giao dịch chuyển sang xóa mềm, danh mục con chưa có giao dịch bị xóa hẳn; bản thân nhóm chuyển sang `DELETED` nếu có danh mục con chứa giao dịch, ngược lại xóa hẳn. Cần thông báo rõ số danh mục ẩn, số danh mục xóa hẳn và số giao dịch giữ lại (`BR-FIN-04`, `FR-CAT-03`).
  - Khôi phục: Giữ nguyên ID, kiểm tra không trùng tên với nhóm `ACTIVE` cùng nhánh (`ERR-NAM-002`). Chỉ khôi phục bản ghi nhóm, các danh mục con bên trong được người dùng khôi phục riêng khi cần (`BR-FIN-04`).

### 2.3.7 Thực thể Bản ghi điều chỉnh số dư - BalanceAdjustmentRecord

Bản ghi điều chỉnh số dư (`BalanceAdjustmentRecord`) ghi nhận sự kiện kiểm kê cân đối số dư ví về đúng số tiền thực tế. Thực thể lưu khoản chênh lệch bất biến để cập nhật số dư ví mà không phát sinh doanh thu hay chi phí trên sổ thu chi (`BR-FIN-02`).

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `recordId` | Mã định danh bản ghi | Chuỗi ID sinh tự động, bất biến và không tái sử dụng (`BR-FIN-02`, `FR-WAL-05`). |
| `walletId` | Mã ví điều chỉnh | Tham chiếu `Wallet` đang `ACTIVE`; từ chối ví `ARCHIVED` (`FR-WAL-05`, `AC-WAL-05.3`). |
| `actualBalance` | Số dư thực tế | Kiểu `Money`, bắt buộc $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`, `ERR-FIN-001`). |
| `previousBalance` | Số dư trước điều chỉnh | Kiểu `Money`, tính từ lịch sử dòng tiền ngay trước thời điểm kiểm kê (`BR-FIN-02`, `FR-WAL-05`). |
| `difference` | Chênh lệch điều chỉnh | Kiểu `Money` có dấu ($\text{actualBalance} - \text{previousBalance}$), bắt buộc $\ne 0.00$ VNĐ (`BR-FIN-01`, `AC-WAL-05.3`). |
| `reason` | Lý do điều chỉnh | Chuỗi tùy chọn, tối đa 255 ký tự sau khi `trim` (`CON-05`, `FR-WAL-05`, `AC-WAL-05.4`). |
| `adjustedAt` | Ngày điều chỉnh | Kiểu `TransactionDate` (`YYYY-MM-DD`), không vượt quá ngày hiện tại từ `DE-01` (`FR-WAL-05`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Bất biến tuyệt đối (`Immutability`): Không được sửa hoặc xóa sau khi tạo (`BR-FIN-02`, `FR-WAL-05`).
  - Chỉ áp dụng cho ví `ACTIVE`, với $\text{actualBalance} \ge 0.00$ VNĐ (`ERR-WAL-001`) và $\text{difference} \ne 0.00$ VNĐ (`AC-WAL-05.3`).
- **Quy tắc vận hành**:
  - Đồng bộ số dư ví tức thời về đúng `actualBalance`, làm tổng tài sản hệ thống biến động đúng bằng `difference` mà không tính vào thu nhập, chi phí hay dòng tiền thuần (`BR-FIN-02`, `FR-WAL-05`, `FR-REP-01`).

## 2.4 Đặc tả chi tiết các đối tượng giá trị

Đối tượng giá trị không có định danh độc lập, được xác định hoàn toàn qua giá trị các thuộc tính, mang tính bất biến tuyệt đối (`Immutable`) và bảo đảm độ chính xác số học trong mọi phép tính tài chính.

### 2.4.1 Tiền tệ và độ chính xác số học - Money

`Money` là đối tượng giá trị đo lường tiền tệ bất biến, loại trừ sai số dấu chấm động nhị phân và bảo đảm độ chính xác số học cho giao dịch, số dư ví và báo cáo (`BR-FIN-01`, `CON-02`).

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `amount` | Giá trị số tiền | Số thập phân chính xác (tối thiểu 18 chữ số nguyên, 2 chữ số thập phân theo `CON-02`). Cấm dùng `float`/`double`; chỉ khởi tạo từ chuỗi thập phân hoặc số nguyên (`BR-FIN-01`, `ERR-FIN-001`). |
| `currency` | Đơn vị tiền tệ | Chuỗi chuẩn hóa, cố định là `VNĐ`. Các phép toán bắt buộc cùng đơn vị tiền tệ. |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Mọi phép toán số học trả về một thể hiện `Money` mới với quy mô cố định 2 chữ số thập phân (`Scale = 2`, `BR-FIN-01`, `CON-02`).
  - Hai đối tượng `Money` bằng nhau khi cùng đơn vị tiền tệ và cùng giá trị đại số (ví dụ `100.00 VNĐ` = `100.0 VNĐ`). Giá trị `Money` của số dư ví luôn $\ge 0.00$ VNĐ (`ERR-WAL-001`).
- **Quy tắc vận hành**:
  - Làm tròn ngân hàng (`Banker's Rounding` - làm tròn nửa chẵn) cho mọi phép chia không hết (`BR-FIN-01`, `ERR-FIN-002`).
  - Hiển thị số nguyên đồng trên giao diện nhưng lưu trữ và tính toán nội bộ đúng 2 chữ số thập phân (`CON-04`, `BR-FIN-01`).

### 2.4.2 Ngày giao dịch và mốc thời gian - TransactionDate

`TransactionDate` là đối tượng giá trị mốc thời gian bất biến, xác định ngày phát sinh sự kiện tài chính để đối soát lịch sử, lọc giao dịch và tổng hợp báo cáo 12 tháng.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `year` | Năm giao dịch | Số nguyên dương 4 chữ số theo lịch Gregory. |
| `month` | Tháng giao dịch | Số nguyên từ 1 đến 12. |
| `day` | Ngày trong tháng | Số nguyên từ 1 đến 31, khớp số ngày thực tế của tháng và năm (kể cả năm nhuận). |
| `isoString` | Chuỗi ngày (dẫn xuất) | Chuỗi ISO 8601 (`YYYY-MM-DD`), phục vụ hiển thị, lưu trữ JSON (`CON-04`) và đối soát. |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**: Tuân thủ lịch Gregory và không vượt quá ngày hiện tại từ `DE-01` khi ghi nhận (`FR-TXN-01`, `FR-TXN-02`, `FR-TRF-01`).
- **Quy tắc vận hành**: Hỗ trợ so sánh thứ tự thời gian để lọc khoảng ngày ($startDate \le endDate$, `FR-TXN-03`), gom cụm theo tháng (`YYYY-MM`) cho báo cáo 12 tháng (`FR-REP-01`) hoặc đoạn ngày cho báo cáo cơ cấu (`FR-REP-02`). Các giao dịch cùng `TransactionDate` được sắp xếp thứ cấp theo `createdAt`.

### 2.4.3 Loại giao dịch - TransactionType

`TransactionType` là đối tượng giá trị kiểu liệt kê bất biến, xác lập nhánh gốc cấp 1 của cây danh mục và chiều tác động của dòng tiền lên số dư ví.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `value` | Chiều dòng tiền | Enum đóng, chỉ nhận `INCOME` (tăng số dư) hoặc `EXPENSE` (giảm số dư). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Chỉ gồm 2 giá trị cố định vĩnh viễn là `INCOME` (dòng tiền vào) và `EXPENSE` (dòng tiền ra), đóng vai trò gốc phân loại cấp 1 (`BR-FIN-04`, `FR-CAT-01`).
  - Phạm vi duy nhất của tên `NeedGroup` được xét riêng trong từng nhánh `TransactionType` (`BR-NAM-01`). Nghiêm cấm đổi `TransactionType` của nhóm nhu cầu hoặc danh mục (`ERR-CAT-001`).
- **Quy tắc vận hành**: Định tuyến việc cộng (`INCOME`) hoặc trừ (`EXPENSE`) số dư ví (`BR-FIN-02`) và ràng buộc `TransactionFee` luôn thuộc nhánh `EXPENSE` (`ERR-FEE-003`).

### 2.4.4 Dòng tiền thuần - NetCashFlow

`NetCashFlow` là đối tượng giá trị tiền tệ tổng hợp bất biến, phản ánh thặng dư hoặc thâm hụt tài chính ròng trong một kỳ quan sát $M$.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `amount` | Số tiền ròng | Số thập phân chính xác (2 chữ số thập phân, không dùng `float`/`double`), mang giá trị âm, dương hoặc bằng 0. |
| `currency` | Đơn vị tiền tệ | Chuỗi chuẩn hóa, mặc định là `VNĐ`. |

**Công thức và quy tắc**

$$\text{NetCashFlow}(M) = \sum_{t \in M} \text{Income}(t) - \sum_{t \in M} \text{Expense}(t)$$

- $\sum_{t \in M} \text{Expense}(t)$ bao gồm cả các khoản phí giao dịch liên kết (`TransactionFee`) phát sinh trong kỳ.
- Ngữ nghĩa: $> 0$ là thặng dư, $< 0$ là thâm hụt (chi vượt thu), $= 0$ là cân bằng thu chi. Hiển thị số nguyên đồng trên giao diện hoặc 2 chữ số thập phân trên báo cáo chi tiết.

### 2.4.5 Tỷ lệ tiết kiệm - SavingsRate

`SavingsRate` là đối tượng giá trị tỷ lệ phần trăm bất biến, đo lường mức độ tích lũy từ dòng tiền thuần so với tổng thu nhập trong kỳ.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `rate` | Giá trị tỷ lệ | Số thập phân chính xác; giữ tối thiểu 4 chữ số thập phân trung gian và làm tròn ngân hàng về 2 chữ số khi hiển thị. |
| `unit` | Đơn vị đo | Cố định là ký hiệu `%`. |

**Công thức và quy tắc**

$$\text{SavingsRate} = \begin{cases} \left(\dfrac{\text{NetCashFlow}}{\text{TotalIncome}}\right) \times 100\% & \text{khi } \text{TotalIncome} > 0 \\ 0.00\% & \text{khi } \text{TotalIncome} \le 0 \end{cases}$$

- Khi $\text{TotalIncome} \le 0$, mặc định bằng `0.00%` để tránh lỗi chia cho 0 (`BR-FIN-01`). Khi $\text{TotalIncome} > 0$ và $\text{NetCashFlow} < 0$, tỷ lệ mang giá trị âm phản ánh mức thâm hụt.

### 2.4.6 Tỷ trọng cơ cấu thu chi - SpendingStructureShare

`SpendingStructureShare` là đối tượng giá trị bất biến đo lường tỷ lệ đóng góp của từng nhóm nhu cầu cấp 2 hoặc danh mục cấp 3 trên tổng dòng tiền trong kỳ (`EXPENSE` gồm cả `TransactionFee`, hoặc `INCOME`).

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `share` | Giá trị tỷ trọng | Số thập phân chính xác trong đoạn $[0.00, 100.00]$, giữ tối thiểu 4 chữ số thập phân trung gian và làm tròn ngân hàng về 2 chữ số. |
| `unit` | Đơn vị đo | Cố định là ký hiệu `%`. |
| `scope` | Phạm vi phân tích | Cấp nhóm nhu cầu (`NeedGroup`), danh mục trên nhóm cha, hoặc danh mục trên toàn kỳ (`Category`). |

**Công thức và quy tắc**

$$\text{SpendingStructureShare}_i = \begin{cases} \left(\dfrac{\text{ItemAmount}_i}{\text{TotalAmount}}\right) \times 100\% & \text{khi } \text{TotalAmount} > 0 \\ 0.00\% & \text{khi } \text{TotalAmount} = 0.00\text{ VNĐ} \end{cases}$$

- Khi $\text{TotalAmount} = 0.00\text{ VNĐ}$, gán tỷ trọng `0.00%` và trả về danh sách rỗng.
- Chỉ hiển thị các mục có $\text{ItemAmount}_i > 0.00\text{ VNĐ}$ (`FR-REP-02`); mục đã xóa mềm vẫn giữ tên gốc kèm nhãn "(đã xóa)".
- Khi $\text{TotalAmount} > 0$, tổng tỷ trọng các thành phần cùng cấp xấp xỉ $100.00\%$ ($\sum_{i=1}^{n} \text{SpendingStructureShare}_i \approx 100.00\%$, sai số tối đa $\pm 0.005\% \times n$ do làm tròn ngân hàng độc lập từng phần tử theo `BR-FIN-01`).

## 2.5 Các mối quan hệ ngữ nghĩa và bất biến miền

Phần này tổng hợp 3 mối quan hệ ngữ nghĩa cốt lõi chi phối sự tương tác và toàn vẹn dữ liệu giữa các thực thể trong mô hình miền.

### 2.5.1 Quan hệ sở hữu ví và bảo toàn số dư

Thiết lập liên kết một-nhiều (1:N) giữa một `Wallet` với các dòng tiền tác động lên nó (`IncomeExpenseTransaction`, `TransferTransaction`, `BalanceAdjustmentRecord`), chi phối chính sách bảo toàn tài sản (`BR-FIN-02`, `FR-WAL-01`–`FR-WAL-05`):
- **Bảo toàn số dư không âm**: Số dư ví không được gán trực tiếp mà tính theo thời gian thực từ lịch sử dòng tiền ($\sum \text{Inflow} - \sum \text{Outflow} \ge 0.00$ VNĐ); từ chối mọi thao tác xuất quỹ, điều chỉnh hoặc hoàn nguyên làm âm số dư (`ERR-WAL-001`, chi tiết tại mục **2.3.1**).
- **Bảo toàn tổng tài sản hệ thống**: Tổng số dư toàn hệ thống chỉ biến động khi phát sinh thu chi, phí giao dịch hoặc chênh lệch kiểm kê bất biến (`BalanceAdjustmentRecord`); chuyển tiền nội bộ thuần túy luôn giữ nguyên tổng tài sản (`BR-FIN-02`, `BR-FIN-03`).

### 2.5.2 Quan hệ phân cấp danh mục 3 cấp

Thiết lập quan hệ hợp thành 3 tầng nghiêm ngặt: `TransactionType` (cấp 1, cố định) $\rightarrow$ `NeedGroup` (cấp 2, nhóm nhu cầu) $\rightarrow$ `Category` (cấp 3, danh mục chi tiết) (`BR-FIN-04`, `FR-CAT-01`–`FR-CAT-03`):
- **Toàn vẹn cấu trúc và liên kết**: Giao dịch thu chi và phí chỉ được liên kết vào cấp 3 (`Category`, `AC-CAT-01.2`). Không tồn tại danh mục mồ côi (`ERR-CAT-004`, `ERR-CAT-005`); mỗi nhánh `TransactionType` cố định và luôn phải duy trì tối thiểu một `NeedGroup` đang hoạt động (`ERR-CAT-001`, `ERR-CAT-002`).
- **Đồng bộ xóa và chuyển nhóm**: Xóa `NeedGroup` sẽ tự động xóa kéo theo các `Category` con (xóa mềm nếu đã có giao dịch để giữ nguyên dữ liệu lịch sử kèm nhãn "(đã xóa)", hoặc xóa vĩnh viễn nếu chưa có giao dịch theo `AC-REP-02.3`). Chuyển nhóm cha chỉ hợp lệ trong cùng `TransactionType` và tới nhóm đang `ACTIVE` (`ERR-CAT-003`, chi tiết tại mục **2.3.5** và **2.3.6**).

### 2.5.3 Quan hệ phụ thuộc sinh tử giữa giao dịch gốc và phí

Thiết lập quan hệ phụ thuộc sinh tử (`Composition`) giữa giao dịch gốc (`IncomeExpenseTransaction` hoặc `TransferTransaction`) và khoản phí đi kèm (`TransactionFee`), tách bạch chi phí bên thứ ba khỏi dòng tiền cốt lõi (`BR-FIN-02`, `BR-FIN-03`, `FR-TXN-01`, `FR-TXN-02`, `FR-TRF-01`):
- **Loại trừ phí mồ côi**: `TransactionFee` luôn gắn với `parentTransactionId`, chỉ được tạo khi phí $> 0.00$ VNĐ kèm một `Category` cấp 3 thuộc nhánh `EXPENSE` (`ERR-FEE-001`–`ERR-FEE-003`), và hạch toán như một khoản chi tiêu độc lập trên báo cáo (`FR-REP-01`, `AC-TXN-03.3`).
- **Đồng bộ vòng đời nguyên tử**: Bản ghi phí được tự động khởi tạo, cập nhật (hoặc xóa khi phí về 0) và xóa kèm hoàn tiền về ví đồng thời với thao tác tạo, sửa, xóa giao dịch gốc (`FR-TXN-04`, `AC-TXN-05.3`, `AC-TRF-02.1`, chi tiết tại mục **2.3.3** và **2.3.4**).
