# 4. Đặc tả Biến đổi Dữ liệu Thao tác Hệ thống

Tài liệu này mô tả chi tiết các thay đổi về mặt dữ liệu trong hệ thống mỗi khi người dùng thực hiện một thao tác.

Với mỗi thao tác làm thay đổi dữ liệu (như tạo ví, ghi nhận thu chi, chuyển tiền hay xóa danh mục), tài liệu làm rõ hai nội dung chính:
- Điều kiện hợp lệ để hệ thống chấp nhận thực hiện thao tác.
- Kết quả biến đổi của các đối tượng dữ liệu sau khi thực hiện xong, bao gồm: đối tượng nào được tạo mới hoặc bị xóa bỏ, những thông tin nào bị thay đổi giá trị, và các mối liên kết nào giữa các đối tượng được thiết lập hoặc gỡ bỏ.

Các thao tác chỉ đọc dữ liệu để hiển thị hoặc xuất báo cáo nhìn chung không làm thay đổi trạng thái dữ liệu nên nằm ngoài phạm vi biến đổi thực thể; riêng nhóm Thống kê và Báo cáo có hợp đồng kiểm tra tham số và bất biến chỉ đọc được đặc tả bổ sung tại mục 4.7.

## 4.1 Cấu trúc Đặc tả Thao tác

Mỗi thao tác làm thay đổi dữ liệu trong hệ thống được đặc tả thống nhất theo 4 thành phần quy chuẩn dưới đây:

| Thành phần | Ý nghĩa | Nội dung đặc tả |
| :--- | :--- | :--- |
| **Thao tác** | Tên gọi và dữ liệu đầu vào | Tên thao tác nghiệp vụ kèm danh sách các tham số truyền vào từ giao diện người dùng. |
| **Quy chiếu chéo** | Căn cứ nghiệp vụ liên kết | Các mã định danh liên kết trực tiếp đến tài liệu yêu cầu, bao gồm:<br/>- Ca sử dụng (`UC-*`)<br/>- Yêu cầu chức năng (`FR-*`)<br/>- Quy tắc tài chính (`BR-*`)<br/>- Mã lỗi (`ERR-*`). |
| **Tiền điều kiện** | Điều kiện bắt buộc trước khi thực hiện | Các ràng buộc và trạng thái dữ liệu bắt buộc phải thỏa mãn để hệ thống chấp nhận xử lý yêu cầu. |
| **Hậu điều kiện** | Trạng thái dữ liệu sau khi hoàn thành | Trạng thái mới của các đối tượng dữ liệu sau khi thao tác thực hiện thành công, bao gồm 4 biến đổi chính:<br/>- *Tạo mới thể hiện:* Các đối tượng dữ liệu mới được sinh ra.<br/>- *Xóa thể hiện:* Các đối tượng dữ liệu bị hủy bỏ hoặc chuyển trạng thái kết thúc.<br/>- *Biến đổi thuộc tính:* Các giá trị số dư, tên gọi hoặc trạng thái bị thay đổi.<br/>- *Hình thành hoặc phá vỡ liên kết:* Các mối quan hệ ngữ nghĩa giữa các đối tượng được kết nối hoặc gỡ bỏ. |

## 4.2 Các thao tác Quản lý Ví

Nhóm chức năng này đảm nhiệm việc quản lý các nguồn tiền trong hệ thống, bao gồm khởi tạo ví mới, chỉnh sửa thông tin ví và xử lý chênh lệch số dư sau kiểm kê thực tế. Các hoạt động tra cứu hoặc xem phân bổ số dư chỉ đọc dữ liệu để hiển thị nên không thuộc phạm vi mô tả của phần này.

### 4.2.1 Thao tác Tạo ví mới

Người dùng tạo một ví mới để quản lý một nguồn tiền riêng biệt, ví dụ tiền mặt hoặc tài khoản ngân hàng, nhằm phân loại và theo dõi giao dịch.

- Thao tác hệ thống: `createWallet(walletName, note)`
- Quy chiếu chéo: `UC-WAL-01`, `FR-WAL-01`, `FR-WAL-04`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Tham số `walletName` sau khi cắt khoảng trắng hai đầu (`trim`) có độ dài từ 1 đến 50 ký tự, không để rỗng và là duy nhất so với thuộc tính `name` của mọi thể hiện `Wallet` (`ACTIVE` hoặc `ARCHIVED`) không phân biệt hoa thường (`BR-NAM-01`); trường hợp vi phạm, báo lỗi tên sai định dạng (`ERR-NAM-001`), trùng tên ví (`ERR-NAM-002`).
- Tham số `note` (nếu có) dài tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`FR-WAL-01`).

**Hậu điều kiện**

- Tạo mới thể hiện: Khởi tạo một thể hiện `Wallet` mới trong hệ thống (`FR-WAL-01`).
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Thể hiện `Wallet` mới được cấp mã `walletId` duy nhất, gán `name = walletName`, `note = note`, trạng thái `status = ACTIVE`, mốc thời gian `createdAt` hiện tại và số dư khởi điểm `currentBalance = 0.00 VNĐ` (`BR-FIN-02`); tỷ lệ phân bổ số dư giữa các ví được tính lại (`FR-WAL-04`).
- Hình thành hoặc phá vỡ liên kết: Không có.

### 4.2.2 Thao tác Cập nhật thông tin ví

Người dùng chỉnh sửa tên hiển thị hoặc ghi chú mô tả của một ví đang tồn tại để chuẩn hóa thông tin quản lý nguồn tiền. Hệ thống cập nhật thuộc tính định danh của ví mà không làm thay đổi số dư khả dụng hay lịch sử giao dịch (`BR-FIN-02`).

- Thao tác hệ thống: `updateWallet(walletId, walletName, note)`
- Quy chiếu chéo: `UC-WAL-03`, `FR-WAL-03`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với mã định danh `walletId` tồn tại trong hệ thống (`FR-WAL-03`).
- Tham số `walletName` mới sau khi cắt khoảng trắng hai đầu (`trim`) có độ dài từ 1 đến 50 ký tự, không để rỗng và không trùng với thuộc tính `name` của bất kỳ thể hiện `Wallet` nào khác trong hệ thống, không phân biệt hoa thường (`BR-NAM-01`); trường hợp vi phạm, báo lỗi tên sai định dạng (`ERR-NAM-001`), trùng tên ví (`ERR-NAM-002`).
- Tham số `note` mới (nếu có) dài tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`FR-WAL-03`).

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Trên thể hiện `Wallet` có mã `walletId`, cập nhật `name = walletName` và `note = note` mới (`FR-WAL-03`); giữ nguyên `walletId`, `status` và `currentBalance` (`BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Không có.

### 4.2.3 Thao tác Điều chỉnh số dư ví thực tế

Người dùng ghi nhận mốc kiểm kê khi phát hiện số tiền thực tế bên ngoài chênh lệch với số dư đang tính trên phần mềm. Hệ thống sinh bản ghi đối soát cố định và đồng bộ số dư khả dụng của ví khớp với số dư thực tế (`BR-FIN-02`).

- Thao tác hệ thống: `adjustBalance(walletId, actualBalance, reason)`
- Quy chiếu chéo: `UC-WAL-04`, `FR-WAL-05`, `BR-FIN-01`, `BR-FIN-02`, `ERR-FIN-001`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với mã `walletId` tồn tại trong hệ thống và có trạng thái `status = ACTIVE` (`FR-WAL-05`).
- Tham số `actualBalance` là số thập phân chính xác 2 chữ số thập phân (`BR-FIN-01`), không âm (`actualBalance >= 0.00 VNĐ`) (`BR-FIN-02`) và khác với số dư hiện tại `currentBalance` của ví (`FR-WAL-05`); trường hợp vi phạm, báo lỗi sai định dạng số học (`ERR-FIN-001`), số dư thực tế âm (`ERR-WAL-001`).
- Tham số `reason` (nếu có) dài tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`FR-WAL-05`).

**Hậu điều kiện**

- Tạo mới thể hiện: Khởi tạo một thể hiện `BalanceAdjustmentRecord` mới theo yêu cầu điều chỉnh số dư (`FR-WAL-05`) và giữ cố định để lưu vết kiểm kê (`BR-FIN-02`).
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Thể hiện `BalanceAdjustmentRecord` mới ghi nhận mã định danh duy nhất, số dư trước điều chỉnh bằng `Wallet.currentBalance` cũ, số dư quan sát bằng `actualBalance`, giá trị chênh lệch bằng `actualBalance - Wallet.currentBalance` (`BR-FIN-01`), lý do `reason` và ngày ghi nhận hiện tại; trên thể hiện `Wallet`, thuộc tính `currentBalance` được tính lại khớp bằng `actualBalance` (`BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Thiết lập liên kết tham chiếu cố định từ thể hiện `BalanceAdjustmentRecord` vừa tạo tới thể hiện `Wallet` thông qua `walletId` (`FR-WAL-05`).

## 4.3 Các thao tác Ghi nhận Thu chi

Nhóm chức năng này theo dõi dòng tiền hàng ngày của người dùng, bao gồm ghi nhận các khoản thu nhập hoặc chi tiêu phát sinh kèm chi phí liên quan, chỉnh sửa thông tin giao dịch cũ và hủy bỏ giao dịch khỏi sổ theo dõi để hoàn nguyên số dư ví. Các hoạt động tra cứu hoặc lọc lịch sử thu chi theo thời gian, ví thanh toán hay danh mục chỉ phục vụ việc xem dữ liệu nên không thuộc phạm vi mô tả của phần này.

### 4.3.1 Thao tác Ghi nhận giao dịch thu chi

Người dùng ghi nhận một khoản thu nhập hoặc chi tiêu phát sinh hàng ngày kèm chi phí giao dịch (nếu có) vào sổ theo dõi. Hệ thống hạch toán đồng thời bản ghi giao dịch gốc cùng bản ghi chi phí liên kết và cập nhật số dư ví tức thời (`BR-FIN-02`).

- Thao tác hệ thống: `recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- Quy chiếu chéo: `UC-TXN-01`, `FR-TXN-01`, `FR-TXN-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với `walletId` tồn tại và có trạng thái `status = ACTIVE` khi ghi nhận khoản thu (`FR-TXN-01`) hoặc khoản chi (`FR-TXN-02`).
- Thể hiện `Category` ứng với `categoryId` tồn tại ở cấp 3, có trạng thái `status = ACTIVE` và thuộc nhánh Loại thu chi (`INCOME` hoặc `EXPENSE`) hợp lệ (`BR-FIN-04`).
- Tham số `amount` là số thập phân chính xác lớn hơn 0.00 VNĐ (`BR-FIN-01`); trường hợp vi phạm, báo lỗi số tiền sai định dạng (`ERR-FIN-001`). Ngày `transactionDate` không vượt quá ngày hiện tại và `note` dài tối đa 255 ký tự đối với khoản thu (`FR-TXN-01`) hoặc khoản chi (`FR-TXN-02`).
- Tham số `feeAmount` là số thập phân chính xác (`BR-FIN-01`) và không âm (`feeAmount >= 0.00 VNĐ`) (`BR-FIN-02`); nếu `feeAmount > 0.00 VNĐ`, bắt buộc cung cấp `feeCategoryId` trỏ tới một thể hiện `Category` cấp 3 có `status = ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-04`); trường hợp vi phạm, báo lỗi chi phí âm (`ERR-FEE-001`), thiếu danh mục phí (`ERR-FEE-002`), danh mục phí sai loại hoặc đã xóa (`ERR-FEE-003`).
- Số dư khả dụng `Wallet.currentBalance` sau khi áp dụng `amount` và trừ đi `feeAmount` bắt buộc không âm (`>= 0.00 VNĐ`) (`BR-FIN-02`); trường hợp vi phạm, báo lỗi không đủ số dư ví (`ERR-WAL-001`).

**Hậu điều kiện**

- Tạo mới thể hiện: Khởi tạo một thể hiện `IncomeExpenseTransaction` mới cho khoản thu (`FR-TXN-01`) hoặc khoản chi (`FR-TXN-02`); nếu `feeAmount > 0.00 VNĐ`, khởi tạo đồng thời một thể hiện `TransactionFee` mới đi kèm (`BR-FIN-02`).
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Thể hiện `IncomeExpenseTransaction` nhận mã `transactionId` duy nhất, `amount`, `transactionDate`, `note`, `createdAt` và `transactionType` kế thừa từ `Category`; thể hiện `TransactionFee` (nếu có) nhận mã `feeId` duy nhất, `amount = feeAmount` và `parentTransactionType = INCOME_EXPENSE`; trên thể hiện `Wallet`, thuộc tính `currentBalance` được tính toán chính xác (`BR-FIN-01`) để tăng thêm `amount - feeAmount` (nếu `INCOME`) hoặc giảm đi `amount + feeAmount` (nếu `EXPENSE`) (`BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Thiết lập liên kết từ `IncomeExpenseTransaction` tới `Wallet` (`walletId`) và tới `Category` cấp 3 (`categoryId`) (`BR-FIN-04`); nếu `feeAmount > 0.00 VNĐ`, thiết lập liên kết phụ thuộc từ `TransactionFee` tới `IncomeExpenseTransaction` (`parentTransactionId = transactionId`), tới `Wallet` (`walletId`) và tới `Category` chi phí (`feeCategoryId`) (`BR-FIN-02`).

### 4.3.2 Thao tác Chỉnh sửa giao dịch thu chi

Người dùng điều chỉnh các thuộc tính của một giao dịch thu chi đã ghi nhận trước đó trong sổ giao dịch. Hệ thống hoàn nguyên tác động tài chính cũ trên ví ban đầu, áp dụng thông số mới lên ví đích và đồng bộ bản ghi chi phí liên kết trong cùng một bước xử lý (`BR-FIN-02`).

- Thao tác hệ thống: `updateTransaction(transactionId, walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- Quy chiếu chéo: `UC-TXN-03`, `FR-TXN-04`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Thể hiện `IncomeExpenseTransaction` ứng với `transactionId` tồn tại trong sổ giao dịch (`FR-TXN-04`) và không phải là bản ghi `TransactionFee` độc lập (`BR-FIN-02`).
- Thể hiện `Wallet` mới ứng với `walletId` tồn tại và có trạng thái `status = ACTIVE` (`FR-TXN-04`).
- Thể hiện `Category` mới ứng với `categoryId` có cùng `transactionType` (`INCOME` hoặc `EXPENSE`) với giao dịch gốc (`BR-FIN-04`); nếu chọn mới thì phải có `status = ACTIVE`, cho phép giữ nguyên danh mục cũ đã xóa mềm (`status = DELETED`) (`FR-TXN-04`).
- Tham số `amount` mới là số thập phân chính xác lớn hơn 0.00 VNĐ (`BR-FIN-01`); `feeAmount` mới không âm (`BR-FIN-02`) và nếu `feeAmount > 0.00 VNĐ` thì phải có `feeCategoryId` hợp lệ thuộc nhánh `EXPENSE` (`BR-FIN-04`); trường hợp vi phạm, báo lỗi số tiền sai định dạng (`ERR-FIN-001`), chi phí âm (`ERR-FEE-001`), thiếu danh mục phí (`ERR-FEE-002`), danh mục phí sai loại hoặc đã xóa (`ERR-FEE-003`).
- Số dư `currentBalance` của ví cũ sau hoàn nguyên và của ví mới sau khi áp dụng thông số mới đều không âm (`>= 0.00 VNĐ`) (`BR-FIN-02`); trường hợp vi phạm, báo lỗi làm âm số dư ví (`ERR-WAL-001`).

**Hậu điều kiện**

- Tạo mới thể hiện: Khởi tạo một thể hiện `TransactionFee` mới đi kèm giao dịch (`BR-FIN-02`) nếu giao dịch trước khi sửa chưa có phí và lần sửa này đặt `feeAmount > 0.00 VNĐ` (`FR-TXN-04`); ngược lại không có.
- Xóa thể hiện: Xóa vĩnh viễn thể hiện `TransactionFee` liên kết (`BR-FIN-02`) nếu giao dịch trước đó có phí nhưng lần sửa này đặt `feeAmount = 0.00 VNĐ` (`FR-TXN-04`); ngược lại không có.
- Biến đổi thuộc tính: Trên `IncomeExpenseTransaction`, cập nhật `amount`, `transactionDate`, `note`, `walletId` và `categoryId` (giữ nguyên `transactionId` và `transactionType`); nếu phí cũ và mới đều lớn hơn 0.00 VNĐ, cập nhật `amount = feeAmount` và `categoryId = feeCategoryId` trên `TransactionFee`; tính toán chính xác (`BR-FIN-01`) để cập nhật lại `currentBalance` trên `Wallet` cũ và `Wallet` mới (`BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Gỡ bỏ liên kết với `Wallet` hoặc `Category` cũ (nếu thay đổi) và thiết lập liên kết tới `Wallet` mới cùng `Category` cấp 3 mới (`BR-FIN-04`); đồng bộ liên kết giữa `IncomeExpenseTransaction`, `TransactionFee`, `Wallet` mới và `Category` chi phí (`feeCategoryId`) (`BR-FIN-02`).

### 4.3.3 Thao tác Xóa giao dịch thu chi

Người dùng loại bỏ một giao dịch thu chi đã ghi nhận khỏi sổ theo dõi để hủy tác động tài chính của giao dịch đó. Hệ thống kiểm tra an toàn số dư hoàn nguyên, xóa đồng thời giao dịch gốc cùng bản ghi phí đi kèm và khôi phục số dư ví (`BR-FIN-02`).

- Thao tác hệ thống: `deleteTransaction(transactionId)`
- Quy chiếu chéo: `UC-TXN-04`, `FR-TXN-05`, `BR-FIN-01`, `BR-FIN-02`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `IncomeExpenseTransaction` ứng với `transactionId` tồn tại trong sổ giao dịch thu chi (`FR-TXN-05`) và không phải là bản ghi `TransactionFee` độc lập (`BR-FIN-02`).
- Nếu giao dịch cần xóa có `transactionType = INCOME`, số dư `Wallet.currentBalance` hiện tại sau khi trừ đi `amount` và cộng hoàn lại `TransactionFee.amount` (nếu có) bắt buộc không âm (`>= 0.00 VNĐ`) (`BR-FIN-02`); trường hợp vi phạm, báo lỗi làm âm số dư ví (`ERR-WAL-001`).

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Xóa vĩnh viễn thể hiện `IncomeExpenseTransaction` có mã `transactionId` khỏi sổ thu chi (`FR-TXN-05`) cùng thể hiện `TransactionFee` liên kết đi kèm nếu có (`BR-FIN-02`).
- Biến đổi thuộc tính: Trên thể hiện `Wallet` liên kết, tính toán chính xác (`BR-FIN-01`) để hoàn nguyên thuộc tính `currentBalance`: giảm đi `amount - feeAmount` nếu xóa giao dịch `INCOME`, hoặc tăng thêm `amount + feeAmount` nếu xóa giao dịch `EXPENSE` (`BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Gỡ bỏ hoàn toàn các liên kết tham chiếu từ `IncomeExpenseTransaction` tới `Wallet` và `Category` (`FR-TXN-05`), đồng thời hủy bỏ liên kết phụ thuộc giữa `TransactionFee` (nếu có) với `IncomeExpenseTransaction`, `Wallet` và `Category` chi phí (`BR-FIN-02`).

## 4.4 Các thao tác Chuyển tiền Nội bộ

Nhóm chức năng này xử lý việc điều chuyển giá trị giữa các ví nội bộ của người dùng, bao gồm ghi nhận chuyển tiền giữa hai ví kèm chi phí phát sinh và hủy bỏ giao dịch chuyển tiền để hoàn nguyên số dư đồng thời cho cả hai ví. Hoạt động tra cứu lịch sử các lượt chuyển tiền chỉ đọc dữ liệu để hiển thị nên không thuộc phạm vi mô tả của phần này.

### 4.4.1 Thao tác Chuyển tiền giữa hai ví

Người dùng điều chuyển một khoản tiền giữa hai ví nội bộ kèm chi phí chuyển tiền (nếu có). Hệ thống thực thi cơ chế ghi nhận đồng thời để trừ tiền ví nguồn, cộng tiền ví đích và tách riêng khoản phí chuyển tiền thành bản ghi chi phí liên kết (`BR-FIN-03`).

- Thao tác hệ thống: `transferFunds(sourceWalletId, destinationWalletId, amount, transferDate, note, feeAmount, feeCategoryId)`
- Quy chiếu chéo: `UC-TRF-01`, `FR-TRF-01`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Hai thể hiện `Wallet` ứng với `sourceWalletId` và `destinationWalletId` cùng tồn tại, đều có trạng thái `status = ACTIVE` và là hai thực thể phân biệt (`sourceWalletId != destinationWalletId`) (`BR-FIN-03`); trường hợp vi phạm, báo lỗi trùng ví chuyển và ví nhận (`ERR-TRF-001`).
- Tham số `amount` là số thập phân chính xác 2 chữ số thập phân (`BR-FIN-01`) và lớn hơn 0.00 VNĐ (`BR-FIN-03`); trường hợp vi phạm, báo lỗi sai định dạng số học (`ERR-FIN-001`), số tiền chuyển nhỏ hơn hoặc bằng 0 (`ERR-TRF-002`). Ngày `transferDate` không vượt quá ngày hiện tại và `note` dài tối đa 255 ký tự (`FR-TRF-01`).
- Tham số `feeAmount` là số thập phân chính xác (`BR-FIN-01`) và không âm (`feeAmount >= 0.00 VNĐ`) (`BR-FIN-03`); nếu `feeAmount > 0.00 VNĐ`, bắt buộc cung cấp `feeCategoryId` (`BR-FIN-02`) trỏ tới một thể hiện `Category` cấp 3 có `status = ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-04`); trường hợp vi phạm, báo lỗi chi phí âm (`ERR-FEE-001`), thiếu danh mục phí (`ERR-FEE-002`), danh mục phí sai loại hoặc đã xóa (`ERR-FEE-003`).
- Số dư khả dụng hiện tại của ví nguồn (`sourceWallet.currentBalance`) lớn hơn hoặc bằng tổng `amount + feeAmount` khi chuyển đi (`BR-FIN-03`) để bảo toàn số dư không âm (`BR-FIN-02`); trường hợp vi phạm, báo lỗi ví nguồn không đủ số dư (`ERR-WAL-001`).

**Hậu điều kiện**

- Tạo mới thể hiện: Khởi tạo một thể hiện `TransferTransaction` mới (`FR-TRF-01`); nếu `feeAmount > 0.00 VNĐ`, khởi tạo đồng thời một thể hiện `TransactionFee` mới thuộc loại `EXPENSE` (`BR-FIN-03`).
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Thể hiện `TransferTransaction` nhận mã `transferId` duy nhất, `amount`, `transactionDate = transferDate`, `note` và `createdAt`; thể hiện `TransactionFee` (nếu có) nhận mã `feeId` duy nhất, `amount = feeAmount` và `parentTransactionType = TRANSFER`; tính toán chính xác (`BR-FIN-01`) trong cùng một bước xử lý đồng thời (`BR-FIN-03`) để giảm `currentBalance` của `Wallet` nguồn đi `amount + feeAmount` và tăng `currentBalance` của `Wallet` đích thêm đúng `amount` (`BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Thiết lập liên kết từ `TransferTransaction` tới `Wallet` nguồn (`sourceWalletId`) và `Wallet` đích (`destinationWalletId`) (`BR-FIN-03`); nếu `feeAmount > 0.00 VNĐ`, thiết lập liên kết phụ thuộc từ `TransactionFee` tới `TransferTransaction` (`parentTransactionId = transferId`), tới `Wallet` nguồn (`sourceWalletId`) và tới `Category` chi phí (`feeCategoryId`) (`BR-FIN-02`).

### 4.4.2 Thao tác Xóa giao dịch chuyển tiền

Người dùng hủy bỏ một giao dịch chuyển tiền nội bộ đã thực hiện để khôi phục số dư của cả hai ví về trạng thái trước khi chuyển. Hệ thống kiểm tra khả năng hoàn trả của ví đích, xóa đồng thời bản ghi chuyển tiền cùng phí liên kết và hoàn nguyên số dư hai ví (`BR-FIN-03`).

- Thao tác hệ thống: `deleteTransfer(transferId)`
- Quy chiếu chéo: `UC-TRF-03`, `FR-TRF-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `TransferTransaction` ứng với mã `transferId` tồn tại trong sổ giao dịch chuyển tiền (`FR-TRF-02`).
- Khi thu hồi đồng thời hai đầu ví (`BR-FIN-03`), số dư khả dụng hiện tại của `Wallet` đích (`destinationWallet.currentBalance`) phải lớn hơn hoặc bằng số tiền đã nhận (`TransferTransaction.amount`) để không làm âm số dư ví đích (`BR-FIN-02`); trường hợp vi phạm, báo lỗi ví đích không đủ số dư để hoàn trả (`ERR-WAL-001`).

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Xóa vĩnh viễn thể hiện `TransferTransaction` có mã `transferId` khỏi sổ chuyển tiền (`FR-TRF-02`) và xóa đồng thời thể hiện `TransactionFee` liên kết đi kèm nếu có (`BR-FIN-03`).
- Biến đổi thuộc tính: Tính toán chính xác (`BR-FIN-01`) và hoàn nguyên đồng thời hai đầu ví (`BR-FIN-03`): cộng hoàn lại `amount + feeAmount` vào `currentBalance` của `Wallet` nguồn và trừ đi đúng `amount` khỏi `currentBalance` của `Wallet` đích (`BR-FIN-02`).
- Hình thành hoặc phá vỡ liên kết: Gỡ bỏ hoàn toàn các liên kết tham chiếu từ `TransferTransaction` tới `Wallet` nguồn và `Wallet` đích (`FR-TRF-02`), đồng thời hủy bỏ các liên kết phụ thuộc giữa `TransactionFee` (nếu có) với `TransferTransaction` (`BR-FIN-03`), `Wallet` nguồn và `Category` chi phí (`BR-FIN-02`).

## 4.5 Các thao tác Danh mục Phân loại

Nhóm chức năng này quản lý hệ thống phân loại thu chi của người dùng, bao gồm bổ sung danh mục mới vào cấu trúc phân cấp, đổi tên danh mục, khôi phục danh mục đã ẩn và xóa danh mục phân loại theo cơ chế bảo toàn lịch sử giao dịch. Toàn bộ các thao tác trong nhóm này đều trực tiếp làm thay đổi cấu trúc dữ liệu của hệ thống.

### 4.5.1 Thao tác Thêm danh mục phân loại mới

Người dùng muốn mở rộng cấu trúc phân loại chi tiêu bằng cách thêm một danh mục mới, giúp việc ghi nhận các giao dịch chi tiêu chi tiết hơn và dễ dàng theo dõi tài chính.

- Thao tác hệ thống: `addCategory(categoryName, parentCategoryId)`
- Quy chiếu chéo: `UC-CAT-01`, `FR-CAT-01`, `BR-NAM-01`, `BR-FIN-04`, `ERR-CAT-001`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Nhóm nhu cầu cha xác định bởi `parentCategoryId` bắt buộc phải tồn tại trong hệ thống và đang ở trạng thái hoạt động (`ACTIVE`), không được ở trạng thái đã xóa (theo `BR-FIN-04`).
- Trường hợp phát sinh (`ERR-CAT-005`): Người dùng tạo danh mục vào nhóm nhu cầu đang ở trạng thái đã xóa hoặc nhóm cha không tồn tại trong hệ thống.
- Tên danh mục mới (`categoryName`) không được để trống, sau khi loại bỏ khoảng trắng ở hai đầu (`trim`) phải có độ dài từ 1 đến 50 ký tự (theo `BR-NAM-01`).
- Trường hợp phát sinh (`ERR-NAM-001`): Tên danh mục mới để trống, chỉ chứa khoảng trắng hoặc có độ dài vượt quá 50 ký tự sau khi cắt tỉa khoảng trắng ở hai đầu.
- Tên danh mục mới sau khi loại bỏ khoảng trắng ở hai đầu và không phân biệt chữ hoa, chữ thường phải là duy nhất trong cùng nhóm cha, không được trùng với tên của bất kỳ danh mục nào khác đang hoạt động (`ACTIVE`) trong cùng nhóm nhu cầu cha đó (theo `BR-NAM-01`).
- Trường hợp phát sinh (`ERR-NAM-002`): Tên danh mục mới trùng với tên của một danh mục khác đang hoạt động trong cùng nhóm nhu cầu cha.

**Hậu điều kiện**

- Tạo mới thể hiện: Một thể hiện thực thể `Category` mới được tạo ra trong hệ thống với mã định danh duy nhất (`categoryId`) được cấp phát tự động và mang tính bất biến.
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính:
  - Thuộc tính `name` được gán bằng giá trị `categoryName` đã cắt tỉa khoảng trắng hai đầu.
  - Thuộc tính `status` được gán trạng thái hoạt động ban đầu là `ACTIVE` (`isDeleted = false`).
  - Kế thừa thuộc tính loại thu chi (`transactionType`: `INCOME` hoặc `EXPENSE`) từ nhóm cha trực thuộc.
- Hình thành hoặc phá vỡ liên kết:
  - Thiết lập liên kết phân cấp ngữ nghĩa giữa thể hiện `Category` mới tạo với thể hiện nhóm nhu cầu cha `NeedGroup` tương ứng (`parentCategoryId`) theo cấu trúc phân cấp 3 cấp (`BR-FIN-04`).
  - Phá vỡ liên kết: Không có.

### 4.5.2 Thao tác Đổi tên danh mục

Người dùng thay đổi tên hiển thị của một danh mục phân loại đã tồn tại trong hệ thống nhằm chuẩn hóa hoặc điều chỉnh cách gọi theo nhu cầu quản lý tài chính mà không làm thay đổi các giao dịch đã ghi nhận.

- Thao tác hệ thống: `renameCategory(categoryId, newName)`
- Quy chiếu chéo: `UC-CAT-02`, `FR-CAT-02`, `BR-NAM-01`, `ERR-CAT-001`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Danh mục phân loại xác định bởi `categoryId` bắt buộc phải tồn tại trong hệ thống và thuộc cấu trúc phân cấp danh mục đang quản lý (theo `BR-FIN-04`).
- Tên mới (`newName`) không được để trống, sau khi loại bỏ khoảng trắng ở hai đầu (`trim`) phải có độ dài từ 1 đến 50 ký tự (theo `BR-NAM-01`).
- Trường hợp phát sinh (`ERR-NAM-001`): Tên mới của danh mục để trống, chỉ chứa khoảng trắng hoặc có độ dài vượt quá 50 ký tự sau khi cắt tỉa khoảng trắng ở hai đầu.
- Tên mới sau khi loại bỏ khoảng trắng ở hai đầu và không phân biệt chữ hoa, chữ thường không được trùng với tên của bất kỳ danh mục nào khác đang hoạt động trong cùng nhóm nhu cầu cha; mục đang đổi tên không bị so sánh với chính nó (theo `BR-NAM-01`).
- Trường hợp phát sinh (`ERR-NAM-002`): Tên mới của danh mục trùng với tên của một danh mục khác đang hoạt động trong cùng nhóm nhu cầu cha.

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Thuộc tính tên (`name`) của thể hiện `Category` được cập nhật bằng giá trị tên mới `newName` đã chuẩn hóa; mã định danh `categoryId` và trạng thái hoạt động được giữ nguyên vẹn.
- Hình thành hoặc phá vỡ liên kết: Không có. (Toàn bộ liên kết phân cấp với nhóm cha và liên kết với các giao dịch lịch sử trong quá khứ được bảo toàn nguyên vẹn).

### 4.5.3 Thao tác Khôi phục danh mục đã xóa

Người dùng phục hồi trạng thái hoạt động cho một danh mục phân loại đã bị xóa mềm trước đó để tiếp tục sử dụng phân loại cho các giao dịch thu chi mới.

- Thao tác hệ thống: `restoreCategory(categoryId)`
- Quy chiếu chéo: `UC-CAT-03`, `FR-CAT-04`, `BR-FIN-04`, `ERR-CAT-001`, `ERR-CAT-002`

**Tiền điều kiện**

- Danh mục phân loại xác định bởi `categoryId` bắt buộc phải tồn tại trong hệ thống và hiện đang ở trạng thái đã xóa mềm (`DELETED` / `HIDDEN`, `isDeleted = true`).
- Nhóm nhu cầu cha trực thuộc của danh mục bắt buộc phải đang ở trạng thái hoạt động (`ACTIVE`), không được ở trạng thái đã xóa (theo `BR-FIN-04`).
- Trường hợp phát sinh (`ERR-CAT-004`): Người dùng khôi phục danh mục khi nhóm nhu cầu cha trực thuộc của nó vẫn đang ở trạng thái đã xóa.
- Tên của danh mục cần khôi phục sau khi loại bỏ khoảng trắng ở hai đầu và không phân biệt chữ hoa, chữ thường không được trùng với tên của bất kỳ danh mục nào khác đang hoạt động (`ACTIVE`) trong cùng nhóm nhu cầu cha (theo `BR-NAM-01`).
- Trường hợp phát sinh (`ERR-NAM-002`): Tên của danh mục cần khôi phục trùng với tên của một danh mục khác đang hoạt động trong cùng nhóm nhu cầu cha.

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Thuộc tính trạng thái của thể hiện `Category` được chuyển từ trạng thái đã xóa mềm (`DELETED` / `HIDDEN`, `isDeleted = true`) sang trạng thái hoạt động (`ACTIVE`, `isDeleted = false`), cho phép danh mục hiển thị trở lại trên các biểu mẫu ghi nhận giao dịch thu chi.
- Hình thành hoặc phá vỡ liên kết: Không có. (Bảo toàn nguyên vẹn liên kết phân cấp với nhóm cha và toàn bộ liên kết lịch sử với các giao dịch cũ đã ghi nhận).

### 4.5.4 Thao tác Xóa danh mục phân loại

Người dùng yêu cầu loại bỏ một danh mục phân loại không còn sử dụng, hệ thống tự động kiểm tra lịch sử phát sinh giao dịch để thực hiện xóa vĩnh viễn hoặc xóa mềm nhằm bảo toàn số liệu tài chính.

- Thao tác hệ thống: `deleteCategory(categoryId)`
- Quy chiếu chéo: `UC-CAT-04`, `FR-CAT-03`, `BR-FIN-04`, `ERR-CAT-001`, `ERR-CAT-003`

**Tiền điều kiện**

- Danh mục phân loại xác định bởi `categoryId` bắt buộc phải tồn tại trong hệ thống và hiện đang ở trạng thái hoạt động (`ACTIVE`).
- Danh mục cần xóa không phải là danh mục hoạt động duy nhất còn lại trong nhóm nhu cầu cha, đảm bảo duy trì tối thiểu một danh mục hoạt động cho mỗi nhóm (theo `BR-FIN-04`).
- Trường hợp phát sinh (`ERR-CAT-002`): Danh mục cần xóa là danh mục hoạt động duy nhất còn lại trong nhóm nhu cầu cha, vi phạm yêu cầu duy trì tối thiểu một danh mục hoạt động cho mỗi nhóm.

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện:
  - Nếu danh mục chưa từng phát sinh bất kỳ giao dịch thu chi nào trong lịch sử: Thể hiện thực thể `Category` bị xóa hoàn toàn và vĩnh viễn khỏi hệ thống lưu trữ (xóa cứng).
  - Nếu danh mục đã từng phát sinh ít nhất một giao dịch thu chi trong lịch sử: Không xóa thể hiện thực thể `Category` khỏi hệ thống lưu trữ nhằm bảo toàn dữ liệu lịch sử tài chính.
- Biến đổi thuộc tính:
  - Trường hợp danh mục đã có giao dịch lịch sử (xóa mềm): Thuộc tính trạng thái của thể hiện `Category` chuyển từ hoạt động (`ACTIVE`, `isDeleted = false`) sang đã xóa (`DELETED` / `HIDDEN`, `isDeleted = true`), ẩn khỏi danh sách chọn khi tạo giao dịch mới nhưng giữ nguyên trạng thái hiển thị kèm nhãn "(đã xóa)" trên các báo cáo thống kê lịch sử.
  - Trường hợp danh mục chưa từng phát sinh giao dịch: Không có (thể hiện đã bị hủy bỏ hoàn toàn).
- Hình thành hoặc phá vỡ liên kết:
  - Trường hợp xóa vĩnh viễn: Phá vỡ hoàn toàn liên kết phân cấp giữa thể hiện `Category` với nhóm nhu cầu cha `NeedGroup`.
  - Trường hợp xóa mềm: Không phá vỡ bất kỳ liên kết nào; toàn bộ liên kết phân cấp với nhóm cha cũng như liên kết ngữ nghĩa với các giao dịch lịch sử (`IncomeExpenseTransaction`, `TransactionFee`) được bảo toàn nguyên vẹn.

## 4.6 Thao tác Lưu trữ và Phục hồi Dữ liệu

Nhóm chức năng này đảm bảo an toàn và tính toàn vẹn cho dữ liệu tài chính, trong đó thao tác phục hồi dữ liệu từ bản sao lưu sẽ ghi đè toàn bộ dữ liệu hiện tại bằng dữ liệu được lưu trữ trong tệp. Các hoạt động xuất dữ liệu sao lưu hoặc đối soát cân bằng số dư chỉ kiểm tra và trích xuất thông tin nên không làm thay đổi trạng thái dữ liệu trong hệ thống.

### 4.6.1 Thao tác Phục hồi dữ liệu từ bản sao lưu

Người dùng khôi phục toàn bộ trạng thái dữ liệu tài chính của hệ thống từ một tệp sao lưu an toàn đã tạo trước đó, nạp lại toàn bộ dữ liệu vào bộ nhớ sau khi vượt qua quy trình kiểm tra toàn vẹn nghiêm ngặt.

- Thao tác hệ thống: `restoreBackupData(backupFilePath)`
- Quy chiếu chéo: `UC-DAT-02`, `FR-DAT-02`, `BR-FIN-02`, `BR-FIN-03`, `ERR-DAT-001`, `ERR-DAT-002`

**Tiền điều kiện**

- Tệp sao lưu tại đường dẫn chỉ định (`backupFilePath`) bắt buộc phải tồn tại trên hệ thống tệp cục bộ, ứng dụng có quyền đọc (`READ permission`) và nội dung tệp phải đúng định dạng cấu trúc JSON với bộ mã ký tự UTF-8 (theo `FR-DAT-02`).
- Trường hợp phát sinh (`ERR-DAT-001`): Tệp sao lưu không tồn tại tại đường dẫn chỉ định, ứng dụng không có quyền đọc tệp hoặc nội dung tệp bị lỗi cấu trúc cú pháp JSON hay sai định dạng mã hóa ký tự UTF-8.
- Dữ liệu trong tệp sao lưu phải vượt qua đầy đủ 4 bước kiểm tra tính toàn vẹn độc lập theo cơ chế tất cả hoặc không (`All-or-Nothing`), nếu phát hiện bất kỳ sai lệch nào hệ thống sẽ từ chối toàn bộ quá trình phục hồi (theo `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`):
  - Cấu trúc cú pháp JSON hợp lệ, chứa đầy đủ các khóa thực thể dữ liệu bắt buộc của hệ thống.
  - Cấu trúc phân cấp danh mục thu chi phải tuân thủ đúng mô hình 3 cấp (Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết), mỗi danh mục phải trực thuộc một nhóm nhu cầu cha hợp lệ và không có danh mục mồ côi (theo `BR-FIN-04`).
  - Các liên kết giao dịch chuyển tiền hai chiều giữa ví nguồn và ví đích cùng chi phí giao dịch đi kèm phải bảo đảm tính nhất quán và toàn vẹn theo nguyên tắc ghi nhận đồng thời (theo `BR-FIN-03`).
  - Số dư của tất cả các ví sau khi tái tính toán từ toàn bộ lịch sử giao dịch trong tệp sao lưu không được âm, luôn lớn hơn hoặc bằng $0.00\text{ VND}$ (theo quy tắc bảo toàn số dư ví `BR-FIN-02`).
- Trường hợp phát sinh (`ERR-DAT-002`): Dữ liệu trong tệp sao lưu vi phạm ít nhất một trong 4 bước kiểm tra tính toàn vẹn độc lập (cấu trúc JSON thiếu khóa thực thể bắt buộc, vi phạm phân cấp danh mục 3 cấp hoặc có danh mục mồ côi, sai lệch liên kết chuyển tiền hai chiều hoặc chi phí mồ côi, hoặc số dư ví bị âm khi tái tính toán từ lịch sử giao dịch).

**Hậu điều kiện**

- Tạo mới thể hiện: Toàn bộ các thể hiện thực thể miền bao gồm ví (`Wallet`), danh mục phân loại (`TransactionType`, `NeedGroup`, `Category`), giao dịch thu chi (`IncomeExpenseTransaction`), giao dịch chuyển tiền (`TransferTransaction`), chi phí giao dịch (`TransactionFee`) và bản ghi kiểm kê đối soát (`BalanceAdjustmentRecord`) được tái tạo đầy đủ trong bộ nhớ hệ thống từ dữ liệu của tệp sao lưu.
- Xóa thể hiện: Toàn bộ các thể hiện dữ liệu hiện tại trong bộ nhớ hệ thống bị hủy bỏ hoàn toàn trước khi tiếp nhận và nạp dữ liệu mới từ bản sao lưu.
- Biến đổi thuộc tính:
  - Toàn bộ các thuộc tính định danh, thông tin định lượng, thời gian và trạng thái của các thực thể được đồng bộ hóa chuẩn xác theo mốc thời gian lưu trữ trong tệp sao lưu.
  - Thuộc tính số dư của từng ví (`Wallet.currentBalance`) được tính toán và cập nhật lại một cách nhất quán từ số dư ban đầu kết hợp tổng hợp toàn bộ các giao dịch thu, chi, chuyển tiền, chi phí và các mốc kiểm kê đối soát có trong bản sao lưu theo quy tắc bảo toàn số dư (`BR-FIN-02`), bảo đảm số dư hệ thống sau phục hồi khớp đúng tuyệt đối với số liệu lịch sử.
- Hình thành hoặc phá vỡ liên kết:
  - Phá vỡ hoàn toàn toàn bộ mạng lưới liên kết giữa các đối tượng dữ liệu hiện tại cũ trước khi phục hồi.
  - Tái lập chính xác toàn bộ mạng lưới liên kết ngữ nghĩa giữa các thể hiện thực thể mới được tạo ra theo đúng cấu trúc trong tệp sao lưu: liên kết giữa ví với các giao dịch và bản ghi kiểm kê; liên kết chuyển tiền giữa ví nguồn và ví đích; liên kết phân cấp 3 cấp giữa loại thu chi, nhóm nhu cầu và danh mục; liên kết giữa giao dịch với danh mục phân loại và chi phí giao dịch tương ứng.

## 4.7 Thống kê và Báo cáo

Người dùng yêu cầu hệ thống trích xuất và hiển thị các báo cáo phân tích tài chính định kỳ, bao gồm dòng tiền thu chi theo chuỗi 12 tháng hoặc cơ cấu phân bổ thu chi theo nhóm nhu cầu và danh mục nhằm theo dõi diễn biến tài chính.

- Thao tác hệ thống: `generateCashFlowReport(endMonth, endYear)` và `generateSpendingStructureReport(startDate, endDate, transactionType)`
- Quy chiếu chéo: `UC-REP-01`, `UC-REP-02`, `FR-REP-01`, `FR-REP-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-REP-001`

**Tiền điều kiện**

- Đối với báo cáo dòng tiền 12 tháng: tham số `endMonth` phải nằm trong đoạn `[1, 12]` và `endYear` phải từ `2000` trở lên (giao diện có thể điền mặc định tháng và năm hiện tại nếu người dùng không chọn, nhưng đối tượng yêu cầu truyền xuống hệ thống phải khác rỗng).
- Đối với báo cáo cơ cấu thu chi: `startDate` và `endDate` bắt buộc khác rỗng và thỏa mãn `startDate <= endDate`; loại luồng tiền `transactionType` bắt buộc thuộc `{EXPENSE, INCOME}` (giao diện điền mặc định `EXPENSE`, nhưng đối tượng yêu cầu truyền xuống hệ thống phải khác rỗng).
- Mốc và khoảng thời gian tương lai: các mốc `(endMonth, endYear)` hoặc khoảng ngày `[startDate, endDate]` thuộc tương lai đều được phép truy vấn ở chế độ chỉ đọc.
- Trường hợp phát sinh (`ERR-REP-001`): thiếu tham số yêu cầu, `endMonth` ngoài `[1, 12]`, `endYear < 2000`, `startDate > endDate`, hoặc `transactionType` không thuộc `{EXPENSE, INCOME}`.

**Hậu điều kiện**

- Tạo mới thể hiện: Không có. (Thao tác thuần truy vấn dữ liệu chỉ đọc, chỉ khởi tạo cấu trúc dữ liệu kết quả tạm thời để truyền lên giao diện hiển thị, không tạo mới bất kỳ thực thể lưu trữ nào trong hệ thống).
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Không có. (Thao tác chỉ đọc, tuyệt đối không làm thay đổi số dư ví, trạng thái hay bất kỳ giá trị thuộc tính nào của các thực thể trong hệ thống).
- Hình thành hoặc phá vỡ liên kết: Không có.
