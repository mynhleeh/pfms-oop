# 4. Đặc tả biến đổi dữ liệu thao tác hệ thống

Tài liệu này đặc tả hợp đồng biến đổi dữ liệu cho các thao tác hệ thống làm thay đổi trạng thái (tạo ví, ghi nhận thu chi, chuyển tiền, quản lý danh mục, phục hồi dữ liệu) qua hai phần chính:
- **Tiền điều kiện**: Các ràng buộc dữ liệu và trạng thái bắt buộc phải thỏa mãn trước khi thực thi.
- **Hậu điều kiện**: Kết quả biến đổi dữ liệu sau khi thực thi thành công (tạo/xóa thể hiện, biến đổi thuộc tính, thiết lập/hủy liên kết).

Các thao tác chỉ đọc dữ liệu nhìn chung nằm ngoài phạm vi biến đổi thực thể; riêng nhóm Thống kê và báo cáo có hợp đồng kiểm tra tham số và bất biến chỉ đọc được đặc tả tại mục 4.7.

## 4.1 Cấu trúc đặc tả thao tác

Mỗi thao tác làm thay đổi dữ liệu được đặc tả thống nhất theo 4 thành phần:

| Thành phần | Ý nghĩa | Nội dung đặc tả |
| :--- | :--- | :--- |
| **Thao tác** | Tên gọi và tham số | Tên thao tác hệ thống kèm danh sách tham số đầu vào. |
| **Quy chiếu chéo** | Căn cứ nghiệp vụ | Liên kết tới ca sử dụng (`UC-*`), yêu cầu chức năng (`FR-*`), quy tắc nghiệp vụ (`BR-*`) và mã lỗi (`ERR-*`). |
| **Tiền điều kiện** | Điều kiện tiên quyết | Các ràng buộc và trạng thái dữ liệu phải thỏa mãn để hệ thống chấp nhận xử lý. |
| **Hậu điều kiện** | Trạng thái sau thực thi | Trạng thái mới của các đối tượng dữ liệu theo 4 khía cạnh:<br/>- *Tạo mới thể hiện*: Đối tượng mới được khởi tạo.<br/>- *Xóa thể hiện*: Đối tượng bị hủy bỏ vĩnh viễn.<br/>- *Biến đổi thuộc tính*: Giá trị thuộc tính (số dư, tên, trạng thái) thay đổi.<br/>- *Hình thành hoặc phá vỡ liên kết*: Quan hệ ngữ nghĩa giữa các đối tượng được kết nối hoặc gỡ bỏ. |

## 4.2 Các thao tác Quản lý ví

Nhóm chức năng này quản lý các nguồn tiền trong hệ thống (tạo ví, sửa thông tin ví và điều chỉnh số dư kiểm kê). Thao tác xem tổng hợp số dư chỉ đọc dữ liệu nên không thuộc phạm vi phần này.

### 4.2.1 Thao tác tạo ví mới

Người dùng tạo một ví mới để quản lý nguồn tiền riêng biệt và theo dõi giao dịch.

- **Thao tác hệ thống**: `createWallet(walletName, note)`
- **Quy chiếu chéo**: `UC-WAL-01`, `FR-WAL-01`, `FR-WAL-04`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- `walletName` sau khi `trim` dài 1–50 ký tự, không rỗng (`ERR-NAM-001`) và duy nhất so với `name` của mọi `Wallet` (`ACTIVE` hoặc `ARCHIVED`), không phân biệt hoa thường (`BR-NAM-01`, `ERR-NAM-002`).
- `note` (nếu có) dài tối đa 255 ký tự sau khi `trim` (`FR-WAL-01`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một thể hiện `Wallet` mới (`FR-WAL-01`).
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: `Wallet` mới được cấp `walletId` duy nhất, gán `name = walletName`, `note = note`, `status = ACTIVE`, `createdAt` hiện tại và `currentBalance = 0.00 VNĐ` (`BR-FIN-02`); tính lại tỷ lệ phân bổ số dư giữa các ví (`FR-WAL-04`).
- **Hình thành hoặc phá vỡ liên kết**: Không có.

### 4.2.2 Thao tác cập nhật thông tin ví

Người dùng sửa tên hiển thị hoặc ghi chú của ví đang tồn tại mà không làm thay đổi số dư hay lịch sử giao dịch (`BR-FIN-02`).

- **Thao tác hệ thống**: `updateWallet(walletId, walletName, note)`
- **Quy chiếu chéo**: `UC-WAL-03`, `FR-WAL-03`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với `walletId` tồn tại trong hệ thống (`FR-WAL-03`).
- `walletName` mới sau khi `trim` dài 1–50 ký tự, không rỗng (`ERR-NAM-001`) và không trùng với `name` của bất kỳ `Wallet` nào khác, không phân biệt hoa thường (`BR-NAM-01`, `ERR-NAM-002`).
- `note` mới (nếu có) dài tối đa 255 ký tự sau khi `trim` (`FR-WAL-03`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: Trên `Wallet` có mã `walletId`, cập nhật `name = walletName` và `note = note` (`FR-WAL-03`); giữ nguyên `walletId`, `status` và `currentBalance` (`BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Không có.

### 4.2.3 Thao tác điều chỉnh số dư ví thực tế

Người dùng ghi nhận mốc kiểm kê khi tiền thực tế chênh lệch với số dư phần mềm. Hệ thống tạo bản ghi đối soát bất biến và đồng bộ số dư ví theo thực tế (`BR-FIN-02`).

- **Thao tác hệ thống**: `adjustBalance(walletId, actualBalance, reason)`
- **Quy chiếu chéo**: `UC-WAL-04`, `FR-WAL-05`, `BR-FIN-01`, `BR-FIN-02`, `ERR-FIN-001`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với `walletId` tồn tại và có `status = ACTIVE` (`FR-WAL-05`).
- `actualBalance` là số thập phân 2 chữ số (`BR-FIN-01`, `ERR-FIN-001`), không âm ($\ge 0.00$ VNĐ, `BR-FIN-02`, `ERR-WAL-001`) và khác `Wallet.currentBalance` hiện tại (`FR-WAL-05`).
- `reason` (nếu có) dài tối đa 255 ký tự sau khi `trim` (`FR-WAL-05`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một thể hiện `BalanceAdjustmentRecord` bất biến (`BR-FIN-02`, `FR-WAL-05`).
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: `BalanceAdjustmentRecord` mới ghi nhận `recordId` duy nhất, `previousBalance = Wallet.currentBalance` cũ, `actualBalance`, `difference = actualBalance - previousBalance` (`BR-FIN-01`), `reason` và `adjustedAt` hiện tại; cập nhật `Wallet.currentBalance = actualBalance` (`BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Thiết lập liên kết cố định từ `BalanceAdjustmentRecord` vừa tạo tới `Wallet` qua `walletId` (`FR-WAL-05`).

## 4.3 Các thao tác Ghi nhận thu chi

Nhóm chức năng này xử lý việc ghi nhận, chỉnh sửa và xóa các giao dịch thu nhập, chi tiêu kèm phí phát sinh. Hoạt động lọc và tra cứu lịch sử thu chi chỉ đọc dữ liệu nên không thuộc phạm vi phần này.

### 4.3.1 Thao tác ghi nhận giao dịch thu chi

Người dùng ghi nhận một khoản thu nhập hoặc chi tiêu kèm phí giao dịch (nếu có), hạch toán đồng thời bản ghi gốc cùng bản ghi phí và cập nhật số dư ví tức thời (`BR-FIN-02`).

- **Thao tác hệ thống**: `recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- **Quy chiếu chéo**: `UC-TXN-01`, `FR-TXN-01`, `FR-TXN-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với `walletId` tồn tại và có `status = ACTIVE` (`FR-TXN-01`, `FR-TXN-02`).
- Thể hiện `Category` ứng với `categoryId` tồn tại ở cấp 3, có `status = ACTIVE` và thuộc nhánh `INCOME` hoặc `EXPENSE` hợp lệ (`BR-FIN-04`).
- `amount` là số thập phân chính xác $> 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`); `transactionDate` không vượt quá ngày hiện tại; `note` dài tối đa 255 ký tự sau khi `trim` (`FR-TXN-01`, `FR-TXN-02`).
- `feeAmount` là số thập phân chính xác $\ge 0.00$ VNĐ (`BR-FIN-01`, `BR-FIN-02`, `ERR-FEE-001`). Nếu `feeAmount > 0.00 VNĐ`, bắt buộc có `feeCategoryId` (`ERR-FEE-002`) trỏ tới một `Category` cấp 3 đang `ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-04`, `ERR-FEE-003`).
- Số dư `Wallet.currentBalance` sau khi áp dụng `amount` và trừ `feeAmount` phải $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một `IncomeExpenseTransaction` mới (`FR-TXN-01`, `FR-TXN-02`); nếu `feeAmount > 0.00 VNĐ`, khởi tạo đồng thời một `TransactionFee` đi kèm (`BR-FIN-02`).
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: `IncomeExpenseTransaction` nhận `transactionId` duy nhất, `amount`, `transactionDate`, `note`, `createdAt` và `transactionType` từ `Category`; `TransactionFee` (nếu có) nhận `feeId` duy nhất, `amount = feeAmount` và `parentTransactionType = INCOME_EXPENSE`; `Wallet.currentBalance` tăng `amount - feeAmount` (nếu `INCOME`) hoặc giảm `amount + feeAmount` (nếu `EXPENSE`) (`BR-FIN-01`, `BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Liên kết `IncomeExpenseTransaction` tới `Wallet` (`walletId`) và `Category` cấp 3 (`categoryId`) (`BR-FIN-04`); nếu `feeAmount > 0.00 VNĐ`, liên kết `TransactionFee` tới `IncomeExpenseTransaction` (`parentTransactionId = transactionId`), `Wallet` (`walletId`) và `Category` phí (`feeCategoryId`) (`BR-FIN-02`).

### 4.3.2 Thao tác chỉnh sửa giao dịch thu chi

Người dùng hiệu chỉnh thông tin một giao dịch thu chi đã lưu. Hệ thống hoàn nguyên tác động cũ trên ví ban đầu, áp dụng thông số mới lên ví đích và đồng bộ bản ghi phí liên kết trong cùng một bước nguyên tử (`BR-FIN-02`).

- **Thao tác hệ thống**: `updateTransaction(transactionId, walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- **Quy chiếu chéo**: `UC-TXN-03`, `FR-TXN-04`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Thể hiện `IncomeExpenseTransaction` ứng với `transactionId` tồn tại (`FR-TXN-04`), không phải bản ghi `TransactionFee` độc lập (`BR-FIN-02`).
- `Wallet` mới ứng với `walletId` tồn tại và có `status = ACTIVE` (`FR-TXN-04`).
- `Category` mới ứng với `categoryId` cùng `transactionType` với giao dịch gốc (`BR-FIN-04`); phải có `status = ACTIVE` nếu chọn mới, hoặc cho phép giữ nguyên danh mục cũ đã `DELETED` (`FR-TXN-04`).
- `amount` mới $> 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`); `feeAmount` mới $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-FEE-001`) và nếu `feeAmount > 0.00 VNĐ` phải có `feeCategoryId` hợp lệ thuộc nhánh `EXPENSE` (`BR-FIN-04`, `ERR-FEE-002`, `ERR-FEE-003`).
- Số dư `currentBalance` của ví cũ sau hoàn nguyên và ví mới sau cập nhật đều $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một `TransactionFee` mới nếu trước khi sửa chưa có phí và sau sửa `feeAmount > 0.00 VNĐ` (`BR-FIN-02`, `FR-TXN-04`); ngược lại không có.
- **Xóa thể hiện**: Xóa vĩnh viễn `TransactionFee` liên kết nếu trước đó có phí và sau sửa `feeAmount = 0.00 VNĐ` (`BR-FIN-02`, `FR-TXN-04`); ngược lại không có.
- **Biến đổi thuộc tính**: Cập nhật `amount`, `transactionDate`, `note`, `walletId`, `categoryId` trên `IncomeExpenseTransaction` (giữ nguyên `transactionId`, `transactionType`); cập nhật `amount = feeAmount`, `categoryId = feeCategoryId` trên `TransactionFee` (nếu phí cũ và mới đều $> 0.00$ VNĐ); tính lại `currentBalance` trên `Wallet` cũ và `Wallet` mới (`BR-FIN-01`, `BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Chuyển liên kết từ `Wallet`/`Category` cũ sang `Wallet`/`Category` mới (`BR-FIN-04`) và đồng bộ liên kết giữa `IncomeExpenseTransaction`, `TransactionFee`, `Wallet` mới cùng `Category` phí (`BR-FIN-02`).

### 4.3.3 Thao tác xóa giao dịch thu chi

Người dùng xóa một giao dịch thu chi khỏi sổ theo dõi. Hệ thống kiểm tra số dư hoàn nguyên, xóa đồng thời giao dịch gốc cùng bản ghi phí đi kèm và khôi phục số dư ví (`BR-FIN-02`).

- **Thao tác hệ thống**: `deleteTransaction(transactionId)`
- **Quy chiếu chéo**: `UC-TXN-04`, `FR-TXN-05`, `BR-FIN-01`, `BR-FIN-02`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `IncomeExpenseTransaction` ứng với `transactionId` tồn tại (`FR-TXN-05`), không phải bản ghi `TransactionFee` độc lập (`BR-FIN-02`).
- Nếu xóa giao dịch `INCOME`, số dư `Wallet.currentBalance` sau khi trừ `amount` và cộng hoàn lại `TransactionFee.amount` (nếu có) phải $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Xóa vĩnh viễn `IncomeExpenseTransaction` có mã `transactionId` (`FR-TXN-05`) cùng `TransactionFee` liên kết (nếu có) (`BR-FIN-02`).
- **Biến đổi thuộc tính**: Hoàn nguyên `Wallet.currentBalance`: giảm `amount - feeAmount` nếu xóa giao dịch `INCOME`, hoặc tăng `amount + feeAmount` nếu xóa giao dịch `EXPENSE` (`BR-FIN-01`, `BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Gỡ bỏ toàn bộ liên kết từ `IncomeExpenseTransaction` và `TransactionFee` (nếu có) tới `Wallet` và `Category` (`BR-FIN-02`, `FR-TXN-05`).

## 4.4 Các thao tác Chuyển tiền nội bộ

Nhóm chức năng này xử lý việc điều chuyển tiền giữa hai ví nội bộ và hủy giao dịch chuyển tiền để hoàn nguyên số dư hai ví. Hoạt động tra cứu lịch sử chuyển tiền chỉ đọc dữ liệu nên không thuộc phạm vi phần này.

### 4.4.1 Thao tác chuyển tiền giữa hai ví

Người dùng điều chuyển tiền giữa hai ví nội bộ kèm phí chuyển tiền (nếu có) theo cơ chế ghi nhận đồng thời (`BR-FIN-03`).

- **Thao tác hệ thống**: `transferFunds(sourceWalletId, destinationWalletId, amount, transferDate, note, feeAmount, feeCategoryId)`
- **Quy chiếu chéo**: `UC-TRF-01`, `FR-TRF-01`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Hai thể hiện `Wallet` ứng với `sourceWalletId` và `destinationWalletId` cùng tồn tại, có `status = ACTIVE` và phân biệt nhau (`sourceWalletId != destinationWalletId`, `BR-FIN-03`, `ERR-TRF-001`).
- `amount` là số thập phân 2 chữ số (`BR-FIN-01`, `ERR-FIN-001`) và $> 0.00$ VNĐ (`BR-FIN-03`, `ERR-TRF-002`); `transferDate` không vượt quá ngày hiện tại; `note` dài tối đa 255 ký tự sau khi `trim` (`FR-TRF-01`).
- `feeAmount` là số thập phân chính xác $\ge 0.00$ VNĐ (`BR-FIN-01`, `BR-FIN-03`, `ERR-FEE-001`). Nếu `feeAmount > 0.00 VNĐ`, bắt buộc có `feeCategoryId` (`ERR-FEE-002`) trỏ tới một `Category` cấp 3 đang `ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-02`, `BR-FIN-04`, `ERR-FEE-003`).
- Số dư ví nguồn `sourceWallet.currentBalance >= amount + feeAmount` (`BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một `TransferTransaction` mới (`FR-TRF-01`); nếu `feeAmount > 0.00 VNĐ`, khởi tạo đồng thời một `TransactionFee` thuộc loại `EXPENSE` (`BR-FIN-03`).
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: `TransferTransaction` nhận `transferId` duy nhất, `amount`, `transactionDate = transferDate`, `note`, `createdAt`; `TransactionFee` (nếu có) nhận `feeId` duy nhất, `amount = feeAmount`, `parentTransactionType = TRANSFER`; đồng thời giảm `sourceWallet.currentBalance` đi `amount + feeAmount` và tăng `destinationWallet.currentBalance` thêm `amount` (`BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`).
- **Hình thành hoặc phá vỡ liên kết**: Liên kết `TransferTransaction` tới `Wallet` nguồn (`sourceWalletId`) và `Wallet` đích (`destinationWalletId`) (`BR-FIN-03`); nếu `feeAmount > 0.00 VNĐ`, liên kết `TransactionFee` tới `TransferTransaction` (`parentTransactionId = transferId`), `Wallet` nguồn (`sourceWalletId`) và `Category` phí (`feeCategoryId`) (`BR-FIN-02`).

### 4.4.2 Thao tác xóa giao dịch chuyển tiền

Người dùng hủy một giao dịch chuyển tiền nội bộ để hoàn nguyên đồng thời số dư của cả hai ví về trạng thái trước khi chuyển (`BR-FIN-03`).

- **Thao tác hệ thống**: `deleteTransfer(transferId)`
- **Quy chiếu chéo**: `UC-TRF-03`, `FR-TRF-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `TransferTransaction` ứng với `transferId` tồn tại trong sổ chuyển tiền (`FR-TRF-02`).
- Số dư hiện tại của `Wallet` đích `destinationWallet.currentBalance >= TransferTransaction.amount` để không làm âm ví đích khi thu hồi (`BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Xóa vĩnh viễn `TransferTransaction` có mã `transferId` (`FR-TRF-02`) cùng `TransactionFee` liên kết (nếu có) (`BR-FIN-03`).
- **Biến đổi thuộc tính**: Hoàn nguyên đồng thời hai đầu ví (`BR-FIN-01`, `BR-FIN-03`): cộng `amount + feeAmount` vào `sourceWallet.currentBalance` và trừ `amount` khỏi `destinationWallet.currentBalance` (`BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Gỡ bỏ toàn bộ liên kết từ `TransferTransaction` và `TransactionFee` (nếu có) tới `Wallet` nguồn, `Wallet` đích và `Category` phí (`BR-FIN-02`, `BR-FIN-03`, `FR-TRF-02`).

## 4.5 Các thao tác Danh mục phân loại

Nhóm chức năng này quản lý cấu trúc phân loại thu chi 3 cấp, bao gồm thêm mới, đổi tên, khôi phục và xóa danh mục/nhóm nhu cầu theo cơ chế bảo toàn lịch sử giao dịch.

### 4.5.1 Thao tác thêm danh mục phân loại mới

Người dùng bổ sung một danh mục chi tiết (cấp 3) vào nhóm nhu cầu cha hoặc bổ sung một nhóm nhu cầu (cấp 2) vào nhánh loại thu chi.

- **Thao tác hệ thống**: `addCategory(categoryName, parentCategoryId)`
- **Quy chiếu chéo**: `UC-CAT-01`, `FR-CAT-01`, `BR-NAM-01`, `BR-FIN-04`, `ERR-CAT-005`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Nhóm cha hoặc nhánh loại thu chi xác định bởi `parentCategoryId` tồn tại và đang có `status = ACTIVE` (`BR-FIN-04`); nếu nhóm cha không tồn tại hoặc đang ở trạng thái `DELETED`, báo lỗi `ERR-CAT-005`.
- `categoryName` sau khi `trim` dài 1–50 ký tự, không để rỗng (`BR-NAM-01`, `ERR-NAM-001`) và duy nhất (không phân biệt hoa thường) trong cùng nhóm cha hoặc cùng nhánh loại thu chi đang `ACTIVE` (`ERR-NAM-002`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một thể hiện `Category` (cấp 3) hoặc `NeedGroup` (cấp 2) mới với mã định danh duy nhất, bất biến.
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: Gán `name = categoryName` (đã `trim`), `status = ACTIVE` và kế thừa `transactionType` (`INCOME` hoặc `EXPENSE`) từ nhánh cha.
- **Hình thành hoặc phá vỡ liên kết**: Thiết lập liên kết phân cấp từ thực thể mới tạo tới `NeedGroup` cha hoặc `TransactionType` gốc tương ứng (`parentCategoryId`, `BR-FIN-04`).

### 4.5.2 Thao tác đổi tên danh mục

Người dùng đổi tên hiển thị của một danh mục chi tiết hoặc nhóm nhu cầu mà không làm thay đổi các giao dịch lịch sử đã ghi nhận.

- **Thao tác hệ thống**: `renameCategory(categoryId, newName)`
- **Quy chiếu chéo**: `UC-CAT-02`, `FR-CAT-02`, `BR-NAM-01`, `BR-FIN-04`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Thực thể `Category` hoặc `NeedGroup` ứng với `categoryId` tồn tại trong cây phân cấp (`BR-FIN-04`).
- `newName` sau khi `trim` dài 1–50 ký tự, không để rỗng (`BR-NAM-01`, `ERR-NAM-001`) và không trùng tên (không phân biệt hoa thường) với mục khác đang `ACTIVE` trong cùng nhóm cha hoặc cùng nhánh loại thu chi (`ERR-NAM-002`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: Cập nhật `name = newName` (đã `trim`); giữ nguyên mã định danh và trạng thái `status`.
- **Hình thành hoặc phá vỡ liên kết**: Không có (bảo toàn toàn bộ liên kết phân cấp và liên kết giao dịch lịch sử).

### 4.5.3 Thao tác khôi phục danh mục đã xóa

Người dùng phục hồi trạng thái hoạt động cho một danh mục hoặc nhóm nhu cầu đã bị xóa mềm để tiếp tục sử dụng cho các giao dịch mới.

- **Thao tác hệ thống**: `restoreCategory(categoryId)`
- **Quy chiếu chéo**: `UC-CAT-03`, `FR-CAT-04`, `BR-NAM-01`, `BR-FIN-04`, `ERR-CAT-004`, `ERR-NAM-002`

**Tiền điều kiện**

- Thực thể `Category` hoặc `NeedGroup` ứng với `categoryId` tồn tại và đang có `status = DELETED`.
- Nếu khôi phục `Category` cấp 3, `NeedGroup` cha trực thuộc phải đang có `status = ACTIVE` (`BR-FIN-04`; vi phạm báo lỗi `ERR-CAT-004`).
- Tên của mục cần khôi phục không trùng (không phân biệt hoa thường) với bất kỳ mục nào khác đang `ACTIVE` trong cùng nhóm cha hoặc cùng nhánh loại thu chi (`BR-NAM-01`, `ERR-NAM-002`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: Chuyển `status` từ `DELETED` sang `ACTIVE`, đưa mục hiển thị trở lại trên biểu mẫu tạo giao dịch.
- **Hình thành hoặc phá vỡ liên kết**: Không có (bảo toàn nguyên vẹn liên kết phân cấp và lịch sử giao dịch).

### 4.5.4 Thao tác xóa danh mục phân loại

Người dùng yêu cầu xóa một danh mục hoặc nhóm nhu cầu. Hệ thống kiểm tra lịch sử giao dịch để thực hiện xóa vĩnh viễn hoặc xóa mềm nhằm bảo toàn số liệu tài chính.

- **Thao tác hệ thống**: `deleteCategory(categoryId)`
- **Quy chiếu chéo**: `UC-CAT-04`, `FR-CAT-03`, `BR-FIN-04`, `ERR-CAT-002`

**Tiền điều kiện**

- Thực thể `Category` hoặc `NeedGroup` ứng với `categoryId` tồn tại và đang có `status = ACTIVE`.
- Nếu xóa `NeedGroup`, nhóm đó không phải là nhóm `ACTIVE` cuối cùng của nhánh loại thu chi (`BR-FIN-04`; vi phạm báo lỗi `ERR-CAT-002`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Xóa vĩnh viễn thực thể khỏi hệ thống (xóa cứng) nếu mục chưa từng phát sinh giao dịch thu chi hoặc phí liên kết; giữ lại thực thể nếu đã có giao dịch lịch sử.
- **Biến đổi thuộc tính**: Nếu đã có giao dịch lịch sử (xóa mềm), chuyển `status` từ `ACTIVE` sang `DELETED` (ẩn khi nhập mới, hiển thị kèm nhãn "(đã xóa)" trên báo cáo lịch sử).
- **Hình thành hoặc phá vỡ liên kết**: Nếu xóa vĩnh viễn, gỡ bỏ liên kết phân cấp với nhóm cha; nếu xóa mềm, bảo toàn toàn bộ liên kết phân cấp và liên kết với các giao dịch lịch sử (`IncomeExpenseTransaction`, `TransactionFee`).

## 4.6 Thao tác Lưu trữ và phục hồi dữ liệu

Nhóm chức năng này đảm bảo an toàn và toàn vẹn dữ liệu, trong đó thao tác phục hồi từ bản sao lưu sẽ ghi đè toàn bộ dữ liệu hiện tại sau khi kiểm định. Các thao tác xuất sao lưu và đối soát chỉ đọc dữ liệu nên không làm thay đổi trạng thái hệ thống.

### 4.6.1 Thao tác phục hồi dữ liệu từ bản sao lưu

Người dùng khôi phục toàn bộ trạng thái dữ liệu tài chính từ tệp sao lưu JSON sau khi vượt qua quy trình kiểm tra toàn vẹn 4 bước.

- **Thao tác hệ thống**: `restoreBackupData(backupFilePath)`
- **Quy chiếu chéo**: `UC-DAT-02`, `FR-DAT-02`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, `ERR-DAT-001`, `ERR-DAT-002`

**Tiền điều kiện**

- Tệp tại `backupFilePath` tồn tại, có quyền đọc (`READ permission`) và đúng cú pháp JSON mã hóa UTF-8 (`FR-DAT-02`; vi phạm báo lỗi `ERR-DAT-001`).
- Dữ liệu trong tệp vượt qua đủ 4 bước kiểm định độc lập theo cơ chế `All-or-Nothing` (vi phạm bất kỳ bước nào báo lỗi `ERR-DAT-002`):
  1. Đầy đủ các khóa thực thể dữ liệu bắt buộc theo schema JSON.
  2. Tuân thủ cây phân cấp danh mục 3 cấp (`TransactionType` $\rightarrow$ `NeedGroup` $\rightarrow$ `Category`), không có danh mục mồ côi (`BR-FIN-04`).
  3. Toàn vẹn liên kết chuyển tiền hai chiều và không có chi phí giao dịch mồ côi (`BR-FIN-02`, `BR-FIN-03`).
  4. Số dư tái tính toán của mọi ví từ lịch sử giao dịch luôn không âm ($\ge 0.00\text{ VNĐ}$, `BR-FIN-02`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Tái tạo toàn bộ các thể hiện `Wallet`, `NeedGroup`, `Category`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee` và `BalanceAdjustmentRecord` từ tệp sao lưu.
- **Xóa thể hiện**: Hủy bỏ toàn bộ các thể hiện dữ liệu hiện tại trong bộ nhớ trước khi nạp dữ liệu mới.
- **Biến đổi thuộc tính**: Đồng bộ toàn bộ thuộc tính định danh, số tiền, thời gian, trạng thái theo bản sao lưu và tính lại `Wallet.currentBalance` của từng ví từ lịch sử dòng tiền (`BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Hủy toàn bộ mạng lưới liên kết cũ và tái lập đầy đủ các liên kết ngữ nghĩa giữa ví, danh mục 3 cấp, giao dịch thu chi, chuyển tiền, chi phí và bản ghi kiểm kê theo đúng bản sao lưu.

## 4.7 Thống kê và báo cáo

Người dùng trích xuất báo cáo dòng tiền 12 tháng hoặc báo cáo cơ cấu thu chi theo nhóm nhu cầu và danh mục ở chế độ chỉ đọc.

- **Thao tác hệ thống**: `generateCashFlowReport(endMonth, endYear)` và `generateSpendingStructureReport(startDate, endDate, transactionType)`
- **Quy chiếu chéo**: `UC-REP-01`, `UC-REP-02`, `FR-REP-01`, `FR-REP-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-REP-001`

**Tiền điều kiện**

- Với `generateCashFlowReport`: `endMonth` thuộc đoạn `[1, 12]` và `endYear >= 2000` (`FR-REP-01`).
- Với `generateSpendingStructureReport`: `startDate` và `endDate` hợp lệ, khác rỗng, thỏa `startDate <= endDate`; `transactionType` thuộc `{EXPENSE, INCOME}` (`FR-REP-02`, `BR-FIN-04`).
- Cho phép truy vấn các mốc hoặc khoảng thời gian trong tương lai ở chế độ chỉ đọc; nếu thiếu hoặc sai tham số, từ chối và báo lỗi `ERR-REP-001`.

**Hậu điều kiện**

- **Tạo mới / Xóa thể hiện**: Không có (chỉ tạo kết quả tạm thời trả về giao diện, không tạo hay xóa thực thể lưu trữ).
- **Biến đổi thuộc tính / Liên kết**: Không có (thao tác thuần đọc, bảo toàn nguyên vẹn số dư ví, trạng thái và các liên kết trong hệ thống).
