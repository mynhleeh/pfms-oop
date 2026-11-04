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

### 4.7 Thống kê và Báo cáo

(Mô tả khoảng 1 đến 2 câu về bối cảnh nghiệp vụ khi người dùng muốn xem báo cáo tài chính, thống kê chi tiêu, thu nhập …)

- Thao tác hệ thống: `generateReport(reportType, startDate, endDate, filters)`
- Quy chiếu chéo: `UC-REP-01`, `FR-REP-01`, `BR-REP-01`, `ERR-REP-001`

**Tiền điều kiện**

- (Nêu các điều kiện bắt buộc về ngày tháng hợp lệ, loại báo cáo tồn tại, người dùng có quyền truy cập)

**Hậu điều kiện**

- Tạo mới thể hiện: Không có.
- Xóa thể hiện: Không có.
- Biến đổi thuộc tính: (N/A – chỉ trả về dữ liệu báo cáo)
- Hình thành hoặc phá vỡ liên kết: Không có.
