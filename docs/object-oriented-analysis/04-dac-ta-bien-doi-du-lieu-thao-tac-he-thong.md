# 4. Đặc tả Biến đổi Dữ liệu Thao tác Hệ thống

Tài liệu này mô tả chi tiết các thay đổi về mặt dữ liệu trong hệ thống mỗi khi người dùng thực hiện một thao tác.

Với mỗi thao tác làm thay đổi dữ liệu (như tạo ví, ghi nhận thu chi, chuyển tiền hay xóa danh mục), tài liệu làm rõ hai nội dung chính:
- Điều kiện hợp lệ để hệ thống chấp nhận thực hiện thao tác.
- Kết quả biến đổi của các đối tượng dữ liệu sau khi thực hiện xong, bao gồm: đối tượng nào được tạo mới hoặc bị xóa bỏ, những thông tin nào bị thay đổi giá trị, và các mối liên kết nào giữa các đối tượng được thiết lập hoặc gỡ bỏ.

Các thao tác chỉ đọc dữ liệu để hiển thị hoặc xuất báo cáo không làm thay đổi trạng thái dữ liệu trong hệ thống, do đó không nằm trong phạm vi mô tả của tài liệu này.

## 4.1 Cấu trúc Đặc tả Thao tác

Mỗi thao tác làm thay đổi dữ liệu trong hệ thống được đặc tả thống nhất theo 4 thành phần quy chuẩn dưới đây:

| Thành phần | Ý nghĩa | Nội dung đặc tả |
| :--- | :--- | :--- |
| **Thao tác** | Tên gọi và dữ liệu đầu vào | Tên thao tác nghiệp vụ kèm danh sách các tham số truyền vào từ giao diện người dùng. |
| **Quy chiếu chéo** | Căn cứ nghiệp vụ liên kết | Các mã định danh liên kết trực tiếp đến tài liệu yêu cầu, bao gồm ca sử dụng (`UC-*`), yêu cầu chức năng (`FR-*`), quy tắc tài chính (`BR-*`) và mã lỗi (`ERR-*`). |
| **Tiền điều kiện** | Điều kiện bắt buộc trước khi thực hiện | Các ràng buộc và trạng thái dữ liệu bắt buộc phải thỏa mãn để hệ thống chấp nhận xử lý yêu cầu. |
| **Hậu điều kiện** | Trạng thái dữ liệu sau khi hoàn thành | Trạng thái mới của các đối tượng dữ liệu sau khi thao tác thực hiện thành công, bao gồm 4 biến đổi chính:<br/>- *Tạo mới thể hiện:* Các đối tượng dữ liệu mới được sinh ra.<br/>- *Xóa thể hiện:* Các đối tượng dữ liệu bị hủy bỏ hoặc chuyển trạng thái kết thúc.<br/>- *Biến đổi thuộc tính:* Các giá trị số dư, tên gọi hoặc trạng thái bị thay đổi.<br/>- *Hình thành hoặc phá vỡ liên kết:* Các mối quan hệ ngữ nghĩa giữa các đối tượng được kết nối hoặc gỡ bỏ. |

## 4.2 Các thao tác Quản lý Ví

Nhóm chức năng này đảm nhiệm việc quản lý các nguồn tiền trong hệ thống, bao gồm khởi tạo ví mới, chỉnh sửa thông tin ví và xử lý chênh lệch số dư sau kiểm kê thực tế. Các hoạt động tra cứu hoặc xem phân bổ số dư chỉ đọc dữ liệu để hiển thị nên không thuộc phạm vi mô tả của phần này.

### 4.2.1 Thao tác Tạo ví mới

Người dùng khởi tạo một nguồn tiền thanh toán mới để theo dõi số dư và hạch toán dòng tiền độc lập trong hệ thống. Hệ thống kiểm tra tính hợp lệ của tên ví (`BR-NAM-01`) và thiết lập ví mới ở trạng thái hoạt động với số dư ban đầu bằng 0.00 VNĐ (`BR-FIN-02`).

- Thao tác hệ thống: `createWallet(walletName, note)`
- Quy chiếu chéo: `UC-WAL-01`, `FR-WAL-01`, `FR-WAL-04`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Tham số `walletName` sau khi cắt khoảng trắng hai đầu (`trim`) có độ dài từ 1 đến 50 ký tự và không để rỗng (`BR-NAM-01`, `ERR-NAM-001`).
- Giá trị `walletName` là duy nhất, không trùng với thuộc tính `name` của bất kỳ thể hiện `Wallet` nào đang hoạt động (`ACTIVE`) hoặc đã lưu trữ (`ARCHIVED`) trong hệ thống, so sánh không phân biệt chữ hoa chữ thường (`BR-NAM-01`, `ERR-NAM-002`).
- Tham số `note` (nếu có) có độ dài tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`FR-WAL-01`).

**Hậu điều kiện**

- Tạo mới thể hiện: Khởi tạo một thể hiện `Wallet` mới trong hệ thống (`FR-WAL-01`).
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Thể hiện `Wallet` mới được cấp mã định danh `walletId` duy nhất, gán `name = walletName`, `note = note`, trạng thái `status = ACTIVE`, mốc thời gian `createdAt` hiện tại và số dư khởi điểm `currentBalance = 0.00 VNĐ` (`BR-FIN-02`); tỷ lệ phân bổ số dư giữa các ví trong hệ thống được tính lại (`FR-WAL-04`).
- Hình thành hoặc phá vỡ liên kết: Không có.

### 4.2.2 Thao tác Cập nhật thông tin ví

Người dùng chỉnh sửa tên hiển thị hoặc ghi chú mô tả của một ví đang tồn tại để chuẩn hóa thông tin quản lý nguồn tiền. Hệ thống cập nhật thuộc tính định danh của ví mà không làm thay đổi số dư khả dụng hay lịch sử giao dịch (`BR-FIN-02`).

- Thao tác hệ thống: `updateWallet(walletId, walletName, note)`
- Quy chiếu chéo: `UC-WAL-03`, `FR-WAL-03`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với mã định danh `walletId` tồn tại trong hệ thống (`FR-WAL-03`).
- Tham số `walletName` mới sau khi cắt khoảng trắng hai đầu (`trim`) có độ dài từ 1 đến 50 ký tự và không để rỗng (`BR-NAM-01`, `ERR-NAM-001`).
- Giá trị `walletName` mới không trùng với thuộc tính `name` của bất kỳ thể hiện `Wallet` nào khác trong hệ thống, không phân biệt chữ hoa chữ thường (`BR-NAM-01`, `ERR-NAM-002`).
- Tham số `note` mới (nếu có) có độ dài tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`FR-WAL-03`).

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Trên thể hiện `Wallet` có mã `walletId`, thuộc tính `name` được cập nhật thành `walletName` và thuộc tính `note` được cập nhật thành `note` mới (`FR-WAL-03`); các thuộc tính `walletId`, `status` và `currentBalance` giữ nguyên giá trị (`BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Không có.

### 4.2.3 Thao tác Điều chỉnh số dư ví thực tế

Người dùng ghi nhận mốc kiểm kê khi phát hiện số tiền thực tế bên ngoài chênh lệch với số dư đang tính trên phần mềm. Hệ thống sinh bản ghi đối soát cố định và đồng bộ số dư khả dụng của ví khớp với số dư thực tế (`BR-FIN-02`).

- Thao tác hệ thống: `adjustBalance(walletId, actualBalance, reason)`
- Quy chiếu chéo: `UC-WAL-04`, `FR-WAL-05`, `BR-FIN-01`, `BR-FIN-02`, `ERR-FIN-001`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với mã `walletId` tồn tại trong hệ thống và có trạng thái `status = ACTIVE` (`FR-WAL-05`).
- Tham số `actualBalance` là số thập phân chính xác 2 chữ số thập phân (`BR-FIN-01`, `ERR-FIN-001`), có giá trị không âm (`actualBalance >= 0.00 VNĐ`) (`BR-FIN-02`, `ERR-WAL-001`) và khác với số dư hiện tại `currentBalance` của ví (`FR-WAL-05`).
- Tham số `reason` (nếu có) có độ dài tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`FR-WAL-05`).

**Hậu điều kiện**

- Tạo mới thể hiện: Khởi tạo một thể hiện `BalanceAdjustmentRecord` mới và cố định để lưu vết kiểm kê (`BR-FIN-02`, `FR-WAL-05`).
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Thể hiện `BalanceAdjustmentRecord` mới ghi nhận mã định danh duy nhất, số dư trước điều chỉnh bằng `Wallet.currentBalance` cũ, số dư quan sát bằng `actualBalance`, giá trị chênh lệch bằng `actualBalance - Wallet.currentBalance` (`BR-FIN-01`), lý do `reason` và ngày ghi nhận hiện tại; trên thể hiện `Wallet`, thuộc tính suy dẫn `currentBalance` được tính lại khớp bằng `actualBalance` (`BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Thiết lập liên kết tham chiếu cố định từ thể hiện `BalanceAdjustmentRecord` vừa tạo tới thể hiện `Wallet` thông qua `walletId` (`FR-WAL-05`).

## 4.3 Các thao tác Ghi nhận Thu chi

Nhóm chức năng này theo dõi dòng tiền hàng ngày của người dùng, bao gồm ghi nhận các khoản thu nhập hoặc chi tiêu phát sinh kèm chi phí liên quan, chỉnh sửa thông tin giao dịch cũ và hủy bỏ giao dịch khỏi sổ theo dõi để hoàn nguyên số dư ví. Các hoạt động tra cứu hoặc lọc lịch sử thu chi theo thời gian, ví thanh toán hay danh mục chỉ phục vụ việc xem dữ liệu nên không thuộc phạm vi mô tả của phần này.

### 4.3.1 Thao tác Ghi nhận giao dịch thu chi

Người dùng ghi nhận một khoản thu nhập hoặc chi tiêu phát sinh hàng ngày kèm chi phí giao dịch (nếu có) vào sổ theo dõi. Hệ thống hạch toán đồng thời bản ghi giao dịch gốc cùng bản ghi chi phí liên kết và cập nhật số dư ví tức thời (`BR-FIN-02`).

- Thao tác hệ thống: `recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- Quy chiếu chéo: `UC-TXN-01`, `FR-TXN-01`, `FR-TXN-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với `walletId` tồn tại và có trạng thái `status = ACTIVE` (`FR-TXN-01`, `FR-TXN-02`).
- Thể hiện `Category` ứng với `categoryId` tồn tại ở cấp 3, có trạng thái `status = ACTIVE` và thuộc nhánh Loại thu chi (`INCOME` hoặc `EXPENSE`) hợp lệ (`BR-FIN-04`).
- Tham số `amount` là số thập phân chính xác lớn hơn 0.00 VNĐ (`BR-FIN-01`, `ERR-FIN-001`); `transactionDate` hợp lệ và không vượt quá ngày hiện tại; `note` dài tối đa 255 ký tự (`FR-TXN-01`, `FR-TXN-02`).
- Tham số `feeAmount` là số thập phân chính xác không âm (`feeAmount >= 0.00 VNĐ`) (`BR-FIN-01`, `ERR-FEE-001`); nếu `feeAmount > 0.00 VNĐ`, tham số `feeCategoryId` bắt buộc được cung cấp (`ERR-FEE-002`) và trỏ tới một thể hiện `Category` cấp 3 có `status = ACTIVE` thuộc nhánh Chi tiêu (`EXPENSE`) (`BR-FIN-02`, `BR-FIN-04`, `ERR-FEE-003`).
- Số dư khả dụng `Wallet.currentBalance` sau khi áp dụng `amount` (cộng nếu `INCOME`, trừ nếu `EXPENSE`) và trừ đi `feeAmount` bắt buộc không âm (`>= 0.00 VNĐ`) (`BR-FIN-02`, `ERR-WAL-001`).

**Hậu điều kiện**

- Tạo mới thể hiện: Khởi tạo một thể hiện `IncomeExpenseTransaction` mới; nếu `feeAmount > 0.00 VNĐ`, khởi tạo đồng thời một thể hiện `TransactionFee` mới (`BR-FIN-02`, `FR-TXN-01`, `FR-TXN-02`).
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Thể hiện `IncomeExpenseTransaction` nhận mã `transactionId` duy nhất, `amount`, `transactionDate`, `note`, `createdAt` và `transactionType` kế thừa từ `Category`; thể hiện `TransactionFee` (nếu có) nhận mã `feeId` duy nhất, `amount = feeAmount` và `parentTransactionType = INCOME_EXPENSE`; trên thể hiện `Wallet`, thuộc tính `currentBalance` tăng thêm `amount - feeAmount` (nếu `INCOME`) hoặc giảm đi `amount + feeAmount` (nếu `EXPENSE`) (`BR-FIN-01`, `BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Thiết lập liên kết từ `IncomeExpenseTransaction` tới `Wallet` (`walletId`) và `Category` (`categoryId`); nếu `feeAmount > 0.00 VNĐ`, thiết lập liên kết phụ thuộc từ `TransactionFee` tới `IncomeExpenseTransaction` (`parentTransactionId = transactionId`), tới `Wallet` (`walletId`) và tới `Category` chi phí (`feeCategoryId`) (`BR-FIN-02`, `BR-FIN-04`).

### 4.3.2 Thao tác Chỉnh sửa giao dịch thu chi

Người dùng điều chỉnh các thuộc tính của một giao dịch thu chi đã ghi nhận trước đó trong sổ giao dịch. Hệ thống hoàn nguyên tác động tài chính cũ trên ví ban đầu, áp dụng thông số mới lên ví đích và đồng bộ bản ghi chi phí liên kết trong cùng một bước xử lý (`BR-FIN-02`).

- Thao tác hệ thống: `updateTransaction(transactionId, walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- Quy chiếu chéo: `UC-TXN-03`, `FR-TXN-04`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Thể hiện `IncomeExpenseTransaction` ứng với `transactionId` tồn tại trong hệ thống và không phải là bản ghi `TransactionFee` độc lập (`BR-FIN-02`, `FR-TXN-04`).
- Thể hiện `Wallet` mới ứng với `walletId` tồn tại và có trạng thái `status = ACTIVE` (`FR-TXN-04`).
- Thể hiện `Category` mới ứng với `categoryId` có cùng `transactionType` (`INCOME` hoặc `EXPENSE`) với giao dịch gốc (`BR-FIN-04`); nếu đổi sang danh mục khác thì danh mục mới phải có `status = ACTIVE`, cho phép giữ nguyên danh mục cũ kể cả khi `status = DELETED` (`FR-TXN-04`).
- Tham số `amount` mới lớn hơn 0.00 VNĐ (`BR-FIN-01`, `ERR-FIN-001`); `feeAmount` mới không âm (`ERR-FEE-001`); nếu `feeAmount > 0.00 VNĐ`, `feeCategoryId` phải hợp lệ và thuộc nhánh `EXPENSE` (`ERR-FEE-002`, `ERR-FEE-003`).
- Số dư `currentBalance` của ví cũ sau khi hoàn nguyên giao dịch cũ (trừ lại `amount` cũ và cộng lại `feeAmount` cũ nếu là `INCOME`) và số dư `currentBalance` của ví mới sau khi áp dụng `amount` mới cùng `feeAmount` mới đều không âm (`>= 0.00 VNĐ`) (`BR-FIN-02`, `ERR-WAL-001`).

**Hậu điều kiện**

- Tạo mới thể hiện: Khởi tạo một thể hiện `TransactionFee` mới nếu giao dịch trước khi sửa chưa có phí (`feeAmount` cũ bằng 0.00 VNĐ) và lần sửa này phát sinh phí (`feeAmount` mới lớn hơn 0.00 VNĐ); nếu không thì không có (`BR-FIN-02`, `FR-TXN-04`).
- Xóa thể hiện: Xóa vĩnh viễn thể hiện `TransactionFee` liên kết hiện có nếu giao dịch trước đó có phí nhưng lần sửa này đặt `feeAmount = 0.00 VNĐ`; nếu không thì không có (`BR-FIN-02`, `FR-TXN-04`).
- Biến đổi thuộc tính: Trên thể hiện `IncomeExpenseTransaction`, cập nhật các thuộc tính `amount`, `transactionDate`, `note`, `walletId` và `categoryId` (giữ nguyên `transactionId` và `transactionType`); nếu phí cũ và phí mới đều lớn hơn 0.00 VNĐ, cập nhật `amount = feeAmount` và `categoryId = feeCategoryId` trên `TransactionFee`; tính lại `currentBalance` trên thể hiện `Wallet` cũ (hoàn nguyên tác động cũ) và trên thể hiện `Wallet` mới (áp dụng tác động mới) (`BR-FIN-01`, `BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Nếu thay đổi `walletId` hoặc `categoryId`, gỡ bỏ liên kết với `Wallet` cũ hoặc `Category` cũ và thiết lập liên kết tới `Wallet` mới hoặc `Category` mới; đồng bộ liên kết giữa `IncomeExpenseTransaction`, `TransactionFee`, `Wallet` mới và `Category` chi phí (`feeCategoryId`) theo trạng thái tạo mới, cập nhật hoặc xóa phí (`BR-FIN-02`, `BR-FIN-04`).

### 4.3.3 Thao tác Xóa giao dịch thu chi

Người dùng loại bỏ một giao dịch thu chi đã ghi nhận khỏi sổ theo dõi để hủy tác động tài chính của giao dịch đó. Hệ thống kiểm tra an toàn số dư hoàn nguyên, xóa đồng thời giao dịch gốc cùng bản ghi phí đi kèm và khôi phục số dư ví (`BR-FIN-02`).

- Thao tác hệ thống: `deleteTransaction(transactionId)`
- Quy chiếu chéo: `UC-TXN-04`, `FR-TXN-05`, `BR-FIN-01`, `BR-FIN-02`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `IncomeExpenseTransaction` ứng với `transactionId` tồn tại trong sổ giao dịch thu chi và không phải là bản ghi `TransactionFee` độc lập (`BR-FIN-02`, `FR-TXN-05`).
- Nếu giao dịch cần xóa có `transactionType = INCOME`, số dư `Wallet.currentBalance` hiện tại sau khi trừ đi `amount` của giao dịch và cộng hoàn lại `TransactionFee.amount` (nếu có) bắt buộc không âm (`>= 0.00 VNĐ`) (`BR-FIN-02`, `ERR-WAL-001`).

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Thể hiện `IncomeExpenseTransaction` có mã `transactionId` và thể hiện `TransactionFee` liên kết đi kèm (nếu có) bị xóa vĩnh viễn khỏi hệ thống (`BR-FIN-02`, `FR-TXN-05`).
- Biến đổi thuộc tính: Trên thể hiện `Wallet` liên kết, thuộc tính `currentBalance` được tính lại và hoàn nguyên về trạng thái trước khi có giao dịch: giảm đi `amount - feeAmount` nếu xóa giao dịch `INCOME`, hoặc tăng thêm `amount + feeAmount` nếu xóa giao dịch `EXPENSE` (`BR-FIN-01`, `BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Gỡ bỏ hoàn toàn các liên kết tham chiếu từ `IncomeExpenseTransaction` tới `Wallet` và `Category`, đồng thời hủy bỏ liên kết phụ thuộc giữa `TransactionFee` (nếu có) với `IncomeExpenseTransaction`, `Wallet` và `Category` chi phí (`BR-FIN-02`, `FR-TXN-05`).

## 4.4 Các thao tác Chuyển tiền Nội bộ

Nhóm chức năng này xử lý việc điều chuyển giá trị giữa các ví nội bộ của người dùng, bao gồm ghi nhận chuyển tiền giữa hai ví kèm chi phí phát sinh và hủy bỏ giao dịch chuyển tiền để hoàn nguyên số dư đồng thời cho cả hai ví. Hoạt động tra cứu lịch sử các lượt chuyển tiền chỉ đọc dữ liệu để hiển thị nên không thuộc phạm vi mô tả của phần này.

### 4.4.1 Thao tác Chuyển tiền giữa hai ví

Người dùng điều chuyển một khoản tiền giữa hai ví nội bộ kèm chi phí chuyển tiền (nếu có). Hệ thống thực thi cơ chế ghi nhận đồng thời để trừ tiền ví nguồn, cộng tiền ví đích và tách riêng khoản phí chuyển tiền thành bản ghi chi phí liên kết (`BR-FIN-03`).

- Thao tác hệ thống: `transferFunds(sourceWalletId, destinationWalletId, amount, transferDate, note, feeAmount, feeCategoryId)`
- Quy chiếu chéo: `UC-TRF-01`, `FR-TRF-01`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Hai thể hiện `Wallet` ứng với `sourceWalletId` và `destinationWalletId` cùng tồn tại trong hệ thống, đều có trạng thái `status = ACTIVE` và là hai thực thể phân biệt (`sourceWalletId != destinationWalletId`) (`BR-FIN-03`, `ERR-TRF-001`).
- Tham số `amount` là số thập phân chính xác lớn hơn 0.00 VNĐ (`BR-FIN-01`, `BR-FIN-03`, `ERR-FIN-001`, `ERR-TRF-002`); `transferDate` hợp lệ và không vượt quá ngày hiện tại; `note` dài tối đa 255 ký tự (`FR-TRF-01`).
- Tham số `feeAmount` là số thập phân chính xác không âm (`feeAmount >= 0.00 VNĐ`) (`BR-FIN-01`, `ERR-FEE-001`); nếu `feeAmount > 0.00 VNĐ`, tham số `feeCategoryId` bắt buộc được cung cấp (`ERR-FEE-002`) và trỏ tới một thể hiện `Category` cấp 3 có `status = ACTIVE` thuộc nhánh Chi tiêu (`EXPENSE`) (`BR-FIN-02`, `BR-FIN-04`, `ERR-FEE-003`).
- Số dư khả dụng hiện tại của ví nguồn (`sourceWallet.currentBalance`) lớn hơn hoặc bằng tổng `amount + feeAmount` (`BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`).

**Hậu điều kiện**

- Tạo mới thể hiện: Khởi tạo một thể hiện `TransferTransaction` mới; nếu `feeAmount > 0.00 VNĐ`, khởi tạo đồng thời một thể hiện `TransactionFee` mới thuộc loại `EXPENSE` (`BR-FIN-03`, `FR-TRF-01`).
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Thể hiện `TransferTransaction` nhận mã `transferId` duy nhất, `amount`, `transactionDate = transferDate`, `note` và `createdAt`; thể hiện `TransactionFee` (nếu có) nhận mã `feeId` duy nhất, `amount = feeAmount` và `parentTransactionType = TRANSFER`; thuộc tính `currentBalance` của `Wallet` nguồn giảm đi `amount + feeAmount` và `currentBalance` của `Wallet` đích tăng thêm đúng `amount` trong cùng một bước xử lý đồng thời (`BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`).
- Hình thành hoặc phá vỡ liên kết: Thiết lập liên kết từ `TransferTransaction` tới `Wallet` nguồn (`sourceWalletId`) và `Wallet` đích (`destinationWalletId`); nếu `feeAmount > 0.00 VNĐ`, thiết lập liên kết phụ thuộc từ `TransactionFee` tới `TransferTransaction` (`parentTransactionId = transferId`), tới `Wallet` nguồn (`sourceWalletId`) và tới `Category` chi phí (`feeCategoryId`) (`BR-FIN-02`, `BR-FIN-03`).

### 4.4.2 Thao tác Xóa giao dịch chuyển tiền

Người dùng hủy bỏ một giao dịch chuyển tiền nội bộ đã thực hiện để khôi phục số dư của cả hai ví về trạng thái trước khi chuyển. Hệ thống kiểm tra khả năng hoàn trả của ví đích, xóa đồng thời bản ghi chuyển tiền cùng phí liên kết và hoàn nguyên số dư hai ví (`BR-FIN-03`).

- Thao tác hệ thống: `deleteTransfer(transferId)`
- Quy chiếu chéo: `UC-TRF-03`, `FR-TRF-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `TransferTransaction` ứng với mã `transferId` tồn tại trong sổ giao dịch chuyển tiền (`FR-TRF-02`).
- Tại thời điểm xóa, số dư khả dụng hiện tại của thể hiện `Wallet` đích (`destinationWallet.currentBalance`) lớn hơn hoặc bằng số tiền đã nhận (`TransferTransaction.amount`) để việc thu hồi không làm âm số dư ví đích (`BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`).

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Thể hiện `TransferTransaction` có mã `transferId` và thể hiện `TransactionFee` liên kết đi kèm (nếu có) bị xóa vĩnh viễn khỏi hệ thống (`BR-FIN-03`, `FR-TRF-02`).
- Biến đổi thuộc tính: Thuộc tính `currentBalance` của hai ví được tính lại và hoàn nguyên đồng thời: `Wallet` nguồn được cộng hoàn lại `amount + feeAmount` (với `feeAmount` là số tiền của `TransactionFee` liên kết nếu có), và `Wallet` đích bị trừ đi đúng `amount` (`BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`).
- Hình thành hoặc phá vỡ liên kết: Gỡ bỏ hoàn toàn các liên kết tham chiếu từ `TransferTransaction` tới `Wallet` nguồn và `Wallet` đích, đồng thời hủy bỏ các liên kết phụ thuộc giữa `TransactionFee` (nếu có) với `TransferTransaction`, `Wallet` nguồn và `Category` chi phí (`BR-FIN-02`, `BR-FIN-03`, `FR-TRF-02`).

## 4.5 Các thao tác Danh mục Phân loại

Nhóm chức năng này quản lý hệ thống phân loại thu chi của người dùng, bao gồm bổ sung danh mục mới vào cấu trúc phân cấp, đổi tên danh mục, khôi phục danh mục đã ẩn và xóa danh mục phân loại theo cơ chế bảo toàn lịch sử giao dịch. Toàn bộ các thao tác trong nhóm này đều trực tiếp làm thay đổi cấu trúc dữ liệu của hệ thống.

### 4.5.1 Thao tác Thêm danh mục phân loại mới

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng bổ sung một danh mục chi tiết vào cấu trúc phân cấp thu chi)

- Thao tác hệ thống: `addCategory(categoryName, parentCategoryId)`
- Quy chiếu chéo: `UC-CAT-01`, `FR-CAT-01`, `BR-NAM-01`, `BR-FIN-04`, `ERR-CAT-001`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về nhóm cha phải tồn tại và đang hoạt động, tên danh mục mới không được để trống và không trùng với các danh mục khác trong cùng nhóm cha sau khi chuẩn hóa)

**Hậu điều kiện**

- Tạo mới thể hiện: (Nêu thể hiện danh mục mới được tạo ra)
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: (Nêu các thuộc tính ban đầu của danh mục: tên danh mục, trạng thái hoạt động mặc định ACTIVE)
- Hình thành hoặc phá vỡ liên kết: (Nêu liên kết phân cấp giữa danh mục mới với nhóm cha tương ứng)

### 4.5.2 Thao tác Đổi tên danh mục

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng thay đổi tên hiển thị của một danh mục phân loại đã có)

- Thao tác hệ thống: `renameCategory(categoryId, newName)`
- Quy chiếu chéo: `UC-CAT-02`, `FR-CAT-02`, `BR-NAM-01`, `ERR-CAT-001`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về danh mục phải tồn tại, tên mới không được để trống và không trùng với các danh mục khác trong cùng nhóm cha sau khi chuẩn hóa)

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: (Nêu thuộc tính tên của danh mục được cập nhật bằng giá trị tên mới đã chuẩn hóa)
- Hình thành hoặc phá vỡ liên kết: Không có.

### 4.5.3 Thao tác Khôi phục danh mục đã xóa

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng phục hồi trạng thái hoạt động cho danh mục đã bị xóa mềm trước đó)

- Thao tác hệ thống: `restoreCategory(categoryId)`
- Quy chiếu chéo: `UC-CAT-03`, `FR-CAT-04`, `BR-FIN-04`, `ERR-CAT-001`, `ERR-CAT-002`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về danh mục đang ở trạng thái xóa mềm, nhóm cha tương ứng đang hoạt động và tên danh mục không bị trùng với các danh mục đang hoạt động)

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: (Nêu thuộc tính trạng thái của danh mục được chuyển từ trạng thái ẩn sang ACTIVE)
- Hình thành hoặc phá vỡ liên kết: Không có.

### 4.5.4 Thao tác Xóa danh mục phân loại

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng loại bỏ một danh mục phân loại theo cơ chế bảo toàn lịch sử giao dịch)

- Thao tác hệ thống: `deleteCategory(categoryId)`
- Quy chiếu chéo: `UC-CAT-04`, `FR-CAT-03`, `BR-FIN-04`, `ERR-CAT-001`, `ERR-CAT-003`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về danh mục phải tồn tại và đang hoạt động; không phải là danh mục duy nhất còn lại trong nhóm theo quy tắc tối thiểu một danh mục)

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: (Nêu phân nhánh: nếu danh mục chưa từng phát sinh giao dịch thì xóa vĩnh viễn thể hiện khỏi hệ thống; nếu đã phát sinh giao dịch thì không xóa thể hiện)
- Biến đổi thuộc tính: (Nêu nếu danh mục đã có giao dịch thì thuộc tính trạng thái chuyển sang lưu trữ hoặc ẩn để bảo toàn dữ liệu lịch sử)
- Hình thành hoặc phá vỡ liên kết: (Nêu nếu xóa vĩnh viễn thì gỡ bỏ liên kết với nhóm cha; nếu xóa mềm thì bảo toàn nguyên vẹn liên kết với các giao dịch lịch sử cũ)

## 4.6 Thao tác Lưu trữ và Phục hồi Dữ liệu

Nhóm chức năng này đảm bảo an toàn và tính toàn vẹn cho dữ liệu tài chính, trong đó thao tác phục hồi dữ liệu từ bản sao lưu sẽ ghi đè toàn bộ dữ liệu hiện tại bằng dữ liệu được lưu trữ trong tệp. Các hoạt động xuất dữ liệu sao lưu hoặc đối soát cân bằng số dư chỉ kiểm tra và trích xuất thông tin nên không làm thay đổi trạng thái dữ liệu trong hệ thống.

### 4.6.1 Thao tác Phục hồi dữ liệu từ bản sao lưu

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng khôi phục toàn bộ trạng thái dữ liệu tài chính từ một tệp sao lưu an toàn)

- Thao tác hệ thống: `restoreBackupData(backupFilePath)`
- Quy chiếu chéo: `UC-DAT-02`, `FR-DAT-02`, `BR-FIN-02`, `BR-FIN-03`, `ERR-DAT-001`, `ERR-DAT-002`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về tệp sao lưu tại đường dẫn chỉ định phải tồn tại, đúng định dạng cấu trúc dữ liệu và vượt qua kiểm tra tính toàn vẹn)

**Hậu điều kiện**

- Tạo mới thể hiện: (Nêu toàn bộ các thể hiện thực thể được tái tạo đầy đủ từ dữ liệu lưu trữ trong tệp sao lưu)
- Xóa thể hiện: (Nêu toàn bộ các thể hiện dữ liệu hiện tại trong bộ nhớ hệ thống bị hủy bỏ hoàn toàn trước khi nạp dữ liệu mới)
- Biến đổi thuộc tính: (Nêu trạng thái của hệ thống được đồng bộ hóa hoàn toàn theo mốc thời gian lưu trữ trong tệp sao lưu)
- Hình thành hoặc phá vỡ liên kết: (Nêu toàn bộ mạng lưới liên kết ngữ nghĩa giữa các đối tượng dữ liệu được tái lập chính xác theo tệp sao lưu)
