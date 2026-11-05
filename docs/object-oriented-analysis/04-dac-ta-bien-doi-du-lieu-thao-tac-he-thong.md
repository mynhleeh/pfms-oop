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
| **Quy chiếu chéo** | Căn cứ nghiệp vụ liên kết | Các mã định danh liên kết trực tiếp đến tài liệu yêu cầu, bao gồm:<br/>- Ca sử dụng (`UC-*`)<br/>- Yêu cầu chức năng (`FR-*`)<br/>- Quy tắc tài chính (`BR-*`)<br/>- Mã lỗi (`ERR-*`). |
| **Tiền điều kiện** | Điều kiện bắt buộc trước khi thực hiện | Các ràng buộc và trạng thái dữ liệu bắt buộc phải thỏa mãn để hệ thống chấp nhận xử lý yêu cầu. |
| **Hậu điều kiện** | Trạng thái dữ liệu sau khi hoàn thành | Trạng thái mới của các đối tượng dữ liệu sau khi thao tác thực hiện thành công, bao gồm 4 biến đổi chính:<br/>- *Tạo mới thể hiện:* Các đối tượng dữ liệu mới được sinh ra.<br/>- *Xóa thể hiện:* Các đối tượng dữ liệu bị hủy bỏ hoặc chuyển trạng thái kết thúc.<br/>- *Biến đổi thuộc tính:* Các giá trị số dư, tên gọi hoặc trạng thái bị thay đổi.<br/>- *Hình thành hoặc phá vỡ liên kết:* Các mối quan hệ ngữ nghĩa giữa các đối tượng được kết nối hoặc gỡ bỏ. |

## 4.2 Các thao tác Quản lý Ví

Nhóm chức năng này đảm nhiệm việc quản lý các nguồn tiền trong hệ thống, bao gồm khởi tạo ví mới, chỉnh sửa thông tin ví và xử lý chênh lệch số dư sau kiểm kê thực tế. Các hoạt động tra cứu hoặc xem phân bổ số dư chỉ đọc dữ liệu để hiển thị nên không thuộc phạm vi mô tả của phần này.

### 4.2.1 Thao tác Tạo ví mới

Người dùng tạo một ví mới để quản lý một nguồn tiền riêng biệt, ví dụ tiền mặt hoặc tài khoản ngân hàng, nhằm phân loại và theo dõi giao dịch.

- Thao tác hệ thống: `createWallet(walletName, note)`
- Quy chiếu chéo: `UC-WAL-01`, `FR-WAL-01`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về tính hợp lệ của dữ liệu đầu vào và trạng thái tiên quyết của đối tượng trước khi hệ thống chấp nhận thực hiện)

**Hậu điều kiện**

- Tạo mới thể hiện: (Nêu thể hiện thực thể nào được tạo ra; nếu không có ghi "Không có")
- Xóa thể hiện: (Nêu thể hiện thực thể nào bị xóa bỏ hoặc kết thúc vòng đời; nếu không có ghi "Không có")
- Biến đổi thuộc tính: (Nêu thuộc tính của thể hiện nào bị thay đổi giá trị; nếu không có ghi "Không có")
- Hình thành hoặc phá vỡ liên kết: (Nêu mối quan hệ ngữ nghĩa giữa các thể hiện được thiết lập hoặc gỡ bỏ; nếu không có ghi "Không có")

### 4.2.2 Thao tác Cập nhật thông tin ví

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng chỉnh sửa tên hoặc ghi chú của ví đang tồn tại)

- Thao tác hệ thống: `updateWallet(walletId, walletName, note)`
- Quy chiếu chéo: `UC-WAL-03`, `FR-WAL-03`, `BR-NAM-01`, `ERR-WAL-002`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về ví phải tồn tại, tên ví mới không được để trống và không trùng với tên các ví khác sau khi chuẩn hóa)

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: (Nêu các thuộc tính được cập nhật giá trị mới: tên ví, ghi chú)
- Hình thành hoặc phá vỡ liên kết: Không có.

### 4.2.3 Thao tác Điều chỉnh số dư ví thực tế

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng ghi nhận mốc chênh lệch kiểm kê số dư thực tế lệch khỏi số dư hệ thống)

- Thao tác hệ thống: `adjustBalance(walletId, actualBalance, reason)`
- Quy chiếu chéo: `UC-WAL-04`, `FR-WAL-05`, `BR-FIN-02`, `ERR-WAL-001`, `ERR-WAL-002`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về ví phải tồn tại, số dư thực tế kiểm kê không được âm, lý do kiểm kê hợp lệ)

**Hậu điều kiện**

- Tạo mới thể hiện: (Nêu bản ghi kiểm kê đối soát bất biến được tạo ra để lưu vết số dư cũ, số dư mới và mức chênh lệch)
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: (Nêu thuộc tính số dư của ví được cập nhật bằng số dư thực tế kiểm kê)
- Hình thành hoặc phá vỡ liên kết: (Nêu liên kết giữa bản ghi kiểm kê đối soát với ví tương ứng)

## 4.3 Các thao tác Ghi nhận Thu chi

Nhóm chức năng này theo dõi dòng tiền hàng ngày của người dùng, bao gồm ghi nhận các khoản thu nhập hoặc chi tiêu phát sinh kèm chi phí liên quan, chỉnh sửa thông tin giao dịch cũ và hủy bỏ giao dịch khỏi sổ theo dõi để hoàn nguyên số dư ví. Các hoạt động tra cứu hoặc lọc lịch sử thu chi theo thời gian, ví thanh toán hay danh mục chỉ phục vụ việc xem dữ liệu nên không thuộc phạm vi mô tả của phần này.

### 4.3.1 Thao tác Ghi nhận giao dịch thu chi

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng ghi nhận khoản thu nhập hoặc chi tiêu phát sinh hàng ngày kèm chi phí nếu có)

- Thao tác hệ thống: `recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- Quy chiếu chéo: `UC-TXN-01`, `FR-TXN-01`, `FR-TXN-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-WAL-001`, `ERR-WAL-002`, `ERR-CAT-001`, `ERR-TXN-001`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về ví tồn tại và đang hoạt động, danh mục hợp lệ đúng loại thu/chi, số tiền giao dịch lớn hơn 0, số dư khả dụng đủ chi trả nếu là khoản chi hoặc có phí kèm theo)

**Hậu điều kiện**

- Tạo mới thể hiện: (Nêu thể hiện giao dịch thu chi được tạo ra; nếu có phí phát sinh thì nêu thể hiện chi phí giao dịch đi kèm được tạo ra)
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: (Nêu thuộc tính số dư của ví được cập nhật: tăng nếu là thu nhập, giảm nếu là chi tiêu; giảm thêm khoản phí nếu có)
- Hình thành hoặc phá vỡ liên kết: (Nêu liên kết giữa giao dịch với ví, với danh mục thu chi; và liên kết giữa giao dịch với chi phí kèm danh mục chi phí tương ứng)

### 4.3.2 Thao tác Chỉnh sửa giao dịch thu chi

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng điều chỉnh thông tin của một giao dịch thu chi đã ghi nhận trước đó)

- Thao tác hệ thống: `updateTransaction(transactionId, walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- Quy chiếu chéo: `UC-TXN-03`, `FR-TXN-04`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-TXN-002`, `ERR-WAL-001`, `ERR-WAL-002`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về giao dịch phải tồn tại, ví và danh mục hợp lệ; kiểm tra bảo toàn số dư không âm sau khi hoàn nguyên giao dịch cũ và áp dụng thông số mới)

**Hậu điều kiện**

- Tạo mới thể hiện: (Nêu thể hiện chi phí mới được tạo nếu trước đó chưa có phí nhưng lần sửa này phát sinh phí; nếu không ghi "Không có")
- Xóa thể hiện: (Nêu thể hiện chi phí cũ bị xóa nếu lần sửa này loại bỏ chi phí; nếu không ghi "Không có")
- Biến đổi thuộc tính: (Nêu các thuộc tính của giao dịch được cập nhật giá trị mới; số dư của ví liên quan được điều chỉnh lại tương ứng)
- Hình thành hoặc phá vỡ liên kết: (Nêu các liên kết với ví, danh mục hoặc chi phí được thiết lập lại nếu người dùng thay đổi ví hoặc danh mục)

### 4.3.3 Thao tác Xóa giao dịch thu chi

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng hủy bỏ hoàn toàn một giao dịch thu chi khỏi hệ thống)

- Thao tác hệ thống: `deleteTransaction(transactionId)`
- Quy chiếu chéo: `UC-TXN-04`, `FR-TXN-05`, `BR-FIN-02`, `ERR-TXN-002`, `ERR-WAL-001`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về giao dịch phải tồn tại; đối với giao dịch thu nhập, việc hoàn nguyên trừ tiền ví không được làm số dư ví bị âm)

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: (Nêu thể hiện giao dịch bị xóa khỏi hệ thống; thể hiện chi phí liên kết đi kèm bị xóa theo)
- Biến đổi thuộc tính: (Nêu số dư ví được hoàn nguyên: giảm nếu xóa thu nhập, tăng nếu xóa chi tiêu; hoàn trả lại tiền phí nếu có)
- Hình thành hoặc phá vỡ liên kết: (Nêu tất cả các liên kết giữa giao dịch này với ví, danh mục và chi phí bị hủy bỏ hoàn toàn)

## 4.4 Các thao tác Chuyển tiền Nội bộ

Nhóm chức năng này xử lý việc điều chuyển giá trị giữa các ví nội bộ của người dùng, bao gồm ghi nhận chuyển tiền giữa hai ví kèm chi phí phát sinh và hủy bỏ giao dịch chuyển tiền để hoàn nguyên số dư đồng thời cho cả hai ví. Hoạt động tra cứu lịch sử các lượt chuyển tiền chỉ đọc dữ liệu để hiển thị nên không thuộc phạm vi mô tả của phần này.

### 4.4.1 Thao tác Chuyển tiền giữa hai ví

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng điều chuyển tiền giữa hai ví nội bộ theo cơ chế ghi nhận đồng thời kèm chi phí nếu có)

- Thao tác hệ thống: `transferFunds(sourceWalletId, destinationWalletId, amount, transferDate, note, feeAmount, feeCategoryId)`
- Quy chiếu chéo: `UC-TRF-01`, `FR-TRF-01`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `ERR-TRF-001`, `ERR-WAL-001`, `ERR-WAL-002`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về hai ví nguồn và đích phải tồn tại, đang hoạt động và phân biệt nhau; số tiền chuyển lớn hơn 0; số dư khả dụng của ví nguồn phải đủ chi trả tổng số tiền chuyển và tiền phí phát sinh)

**Hậu điều kiện**

- Tạo mới thể hiện: (Nêu thể hiện giao dịch chuyển tiền được tạo ra; nếu có phí phát sinh thì nêu thể hiện chi phí giao dịch đi kèm được tạo ra)
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: (Nêu thuộc tính số dư của ví nguồn bị trừ đi số tiền chuyển và tiền phí; thuộc tính số dư của ví đích được cộng thêm đúng số tiền chuyển theo nguyên tắc ghi nhận đồng thời)
- Hình thành hoặc phá vỡ liên kết: (Nêu liên kết giữa giao dịch chuyển tiền với ví nguồn, ví đích; và liên kết với chi phí kèm danh mục chi phí tương ứng nếu có phí)

### 4.4.2 Thao tác Xóa giao dịch chuyển tiền

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng hủy bỏ giao dịch chuyển tiền và hoàn nguyên số dư cho cả hai ví)

- Thao tác hệ thống: `deleteTransfer(transferId)`
- Quy chiếu chéo: `UC-TRF-03`, `FR-TRF-03`, `BR-FIN-02`, `BR-FIN-03`, `ERR-TRF-002`, `ERR-WAL-001`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về giao dịch chuyển tiền phải tồn tại; việc hoàn nguyên trừ tiền ở ví đích không được làm số dư ví đích bị âm)

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: (Nêu thể hiện giao dịch chuyển tiền bị xóa khỏi hệ thống; thể hiện chi phí liên kết đi kèm bị xóa theo)
- Biến đổi thuộc tính: (Nêu số dư hai ví được hoàn nguyên đồng thời: ví nguồn được cộng lại tiền chuyển và tiền phí; ví đích bị trừ đi số tiền chuyển)
- Hình thành hoặc phá vỡ liên kết: (Nêu tất cả các liên kết giữa giao dịch chuyển tiền với ví nguồn, ví đích và chi phí bị hủy bỏ hoàn toàn)

## 4.5 Các thao tác Danh mục Phân loại

Nhóm chức năng này quản lý hệ thống phân loại thu chi của người dùng, bao gồm bổ sung danh mục mới vào cấu trúc phân cấp, đổi tên danh mục, khôi phục danh mục đã ẩn và xóa danh mục phân loại theo cơ chế bảo toàn lịch sử giao dịch. Toàn bộ các thao tác trong nhóm này đều trực tiếp làm thay đổi cấu trúc dữ liệu của hệ thống.

### 4.5.1 Thao tác Thêm danh mục phân loại mới

Người dùng muốn mở rộng cấu trúc phân loại chi tiêu bằng cách thêm một danh mục mới, giúp việc ghi nhận các giao dịch chi tiêu chi tiết hơn và dễ dàng theo dõi tài chính.

- Thao tác hệ thống: `addCategory(categoryName, parentCategoryId)`
- Quy chiếu chéo: `UC-CAT-01`, `FR-CAT-01`, `BR-NAM-01`, `BR-FIN-04`, `ERR-CAT-001`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Nhóm nhu cầu cha xác định bởi `parentCategoryId` bắt buộc phải tồn tại trong hệ thống và đang ở trạng thái hoạt động (`ACTIVE`), không được ở trạng thái đã xóa (theo `BR-FIN-04`, lỗi `ERR-CAT-005`).
- Tên danh mục mới (`categoryName`) không được để trống, sau khi loại bỏ khoảng trắng ở hai đầu (`trim`) phải có độ dài từ 1 đến 50 ký tự (theo `BR-NAM-01`, lỗi `ERR-NAM-001`).
- Tên danh mục mới sau khi loại bỏ khoảng trắng ở hai đầu và không phân biệt chữ hoa, chữ thường phải là duy nhất trong cùng nhóm cha, không được trùng với tên của bất kỳ danh mục nào khác đang hoạt động (`ACTIVE`) trong cùng nhóm nhu cầu cha đó (theo `BR-NAM-01`, lỗi `ERR-NAM-002`).

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
- Tên mới (`newName`) không được để trống, sau khi loại bỏ khoảng trắng ở hai đầu (`trim`) phải có độ dài từ 1 đến 50 ký tự (theo `BR-NAM-01`, lỗi `ERR-NAM-001`).
- Tên mới sau khi loại bỏ khoảng trắng ở hai đầu và không phân biệt chữ hoa, chữ thường không được trùng với tên của bất kỳ danh mục nào khác đang hoạt động trong cùng nhóm nhu cầu cha; mục đang đổi tên không bị so sánh với chính nó (theo `BR-NAM-01`, lỗi `ERR-NAM-002`).

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
- Nhóm nhu cầu cha trực thuộc của danh mục bắt buộc phải đang ở trạng thái hoạt động (`ACTIVE`), không được ở trạng thái đã xóa (theo `BR-FIN-04`, lỗi `ERR-CAT-004`).
- Tên của danh mục cần khôi phục sau khi loại bỏ khoảng trắng ở hai đầu và không phân biệt chữ hoa, chữ thường không được trùng với tên của bất kỳ danh mục nào khác đang hoạt động (`ACTIVE`) trong cùng nhóm nhu cầu cha (theo `BR-NAM-01`, lỗi `ERR-NAM-002`).

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
- Danh mục cần xóa không phải là danh mục hoạt động duy nhất còn lại trong nhóm nhu cầu cha, đảm bảo duy trì tối thiểu một danh mục hoạt động cho mỗi nhóm (theo `BR-FIN-04`, lỗi `ERR-CAT-002`).

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

- Tệp sao lưu tại đường dẫn chỉ định (`backupFilePath`) bắt buộc phải tồn tại trên hệ thống tệp cục bộ, ứng dụng có quyền đọc (`READ permission`) và nội dung tệp phải đúng định dạng cấu trúc JSON với bộ mã ký tự UTF-8 (theo `FR-DAT-02`, lỗi `ERR-DAT-001`).
- Dữ liệu trong tệp sao lưu phải vượt qua đầy đủ 4 bước kiểm tra tính toàn vẹn độc lập theo cơ chế tất cả hoặc không (`All-or-Nothing`), nếu phát hiện bất kỳ sai lệch nào hệ thống sẽ từ chối toàn bộ quá trình phục hồi (theo `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, lỗi `ERR-DAT-002`):
  - Cấu trúc cú pháp JSON hợp lệ, chứa đầy đủ các khóa thực thể dữ liệu bắt buộc của hệ thống.
  - Cấu trúc phân cấp danh mục thu chi phải tuân thủ đúng mô hình 3 cấp (Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết), mỗi danh mục phải trực thuộc một nhóm nhu cầu cha hợp lệ và không có danh mục mồ côi (theo `BR-FIN-04`).
  - Các liên kết giao dịch chuyển tiền hai chiều giữa ví nguồn và ví đích cùng chi phí giao dịch đi kèm phải bảo đảm tính nhất quán và toàn vẹn theo nguyên tắc ghi nhận đồng thời (theo `BR-FIN-03`).
  - Số dư của tất cả các ví sau khi tái tính toán từ toàn bộ lịch sử giao dịch trong tệp sao lưu không được âm, luôn lớn hơn hoặc bằng $0.00\text{ VND}$ (theo quy tắc bảo toàn số dư ví `BR-FIN-02`).

**Hậu điều kiện**

- Tạo mới thể hiện: Toàn bộ các thể hiện thực thể miền bao gồm ví (`Wallet`), danh mục phân loại (`TransactionType`, `NeedGroup`, `Category`), giao dịch thu chi (`IncomeExpenseTransaction`), giao dịch chuyển tiền (`TransferTransaction`), chi phí giao dịch (`TransactionFee`) và bản ghi kiểm kê đối soát (`BalanceAdjustmentRecord`) được tái tạo đầy đủ trong bộ nhớ hệ thống từ dữ liệu của tệp sao lưu.
- Xóa thể hiện: Toàn bộ các thể hiện dữ liệu hiện tại trong bộ nhớ hệ thống bị hủy bỏ hoàn toàn trước khi tiếp nhận và nạp dữ liệu mới từ bản sao lưu.
- Biến đổi thuộc tính:
  - Toàn bộ các thuộc tính định danh, thông tin định lượng, thời gian và trạng thái của các thực thể được đồng bộ hóa chuẩn xác theo mốc thời gian lưu trữ trong tệp sao lưu.
  - Thuộc tính số dư của từng ví (`Wallet.balance`) được tính toán và cập nhật lại một cách nhất quán từ số dư ban đầu kết hợp tổng hợp toàn bộ các giao dịch thu, chi, chuyển tiền, chi phí và các mốc kiểm kê đối soát có trong bản sao lưu theo quy tắc bảo toàn số dư (`BR-FIN-02`), bảo đảm số dư hệ thống sau phục hồi khớp đúng tuyệt đối với số liệu lịch sử.
- Hình thành hoặc phá vỡ liên kết:
  - Phá vỡ hoàn toàn toàn bộ mạng lưới liên kết giữa các đối tượng dữ liệu hiện tại cũ trước khi phục hồi.
  - Tái lập chính xác toàn bộ mạng lưới liên kết ngữ nghĩa giữa các thể hiện thực thể mới được tạo ra theo đúng cấu trúc trong tệp sao lưu: liên kết giữa ví với các giao dịch và bản ghi kiểm kê; liên kết chuyển tiền giữa ví nguồn và ví đích; liên kết phân cấp 3 cấp giữa loại thu chi, nhóm nhu cầu và danh mục; liên kết giữa giao dịch với danh mục phân loại và chi phí giao dịch tương ứng.

### 4.7 Thống kê và Báo cáo

Người dùng yêu cầu hệ thống trích xuất và hiển thị các báo cáo phân tích tài chính định kỳ, bao gồm dòng tiền thu chi theo chuỗi thời gian hoặc cơ cấu phân bổ chi tiêu theo nhóm danh mục nhằm theo dõi diễn biến tài chính.

- Thao tác hệ thống: `generateReport(reportType, startDate, endDate, filters)`
- Quy chiếu chéo: `UC-REP-01`, `FR-REP-01`, `BR-REP-01`, `ERR-REP-001`

**Tiền điều kiện**

- Loại báo cáo chỉ định (`reportType`) phải tồn tại và hợp lệ trong danh mục các loại báo cáo được hệ thống hỗ trợ (báo cáo dòng tiền 12 tháng hoặc báo cáo cơ cấu thu chi).
- Khoảng thời gian thống kê (`startDate`, `endDate`) phải đúng định dạng ngày tháng hợp lệ và thỏa mãn điều kiện logic thời gian bắt đầu không vượt quá thời gian kết thúc (`startDate <= endDate`) (lỗi `ERR-REP-001`).
- Các tiêu chí lọc trong `filters` (nếu có, như ví hoặc nhóm danh mục) phải tham chiếu đến các thực thể hợp lệ đang tồn tại trong hệ thống.

**Hậu điều kiện**

- Tạo mới thể hiện: Không có. (Thao tác thuần truy vấn dữ liệu chỉ đọc, chỉ khởi tạo đối tượng cấu trúc dữ liệu báo cáo tạm thời trên bộ nhớ giao diện để hiển thị kết quả cho người dùng, không tạo mới bất kỳ thể hiện thực thể lưu trữ nào trong hệ thống).
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: Không có. (Thao tác chỉ đọc, tuyệt đối không làm thay đổi số dư ví, trạng thái hay bất kỳ giá trị thuộc tính nào của các thực thể trong hệ thống).
- Hình thành hoặc phá vỡ liên kết: Không có.
