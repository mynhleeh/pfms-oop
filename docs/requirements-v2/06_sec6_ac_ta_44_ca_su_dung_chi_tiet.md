## 6. ĐẶC TẢ 44 CA SỬ DỤNG CHI TIẾT

Phần này đặc tả chi tiết 44 ca sử dụng của Hệ thống Quản lý Tài chính Cá nhân. Mỗi ca sử dụng mô hình hóa một chuỗi tương tác hoàn chỉnh giữa tác nhân người dùng và hệ thống để đạt được một mục tiêu nghiệp vụ cụ thể. Các ca sử dụng tuân thủ các quy tắc tài chính luôn đúng tại Mục 3 và hiện thực hóa các yêu cầu chức năng tại Mục 4.

Mỗi ca sử dụng được mô tả theo cùng một khuôn gồm 9 trường thông tin:

1. Mã và Tên ca sử dụng: Mã định danh phân cấp duy nhất (`UC-<DOMAIN>-NN`) và tên hành động theo cấu trúc chuẩn Động từ + Danh từ.
2. Bảng thuộc tính ca sử dụng: Mã, Tên, Tác nhân chính, Phạm vi và Cấp độ, Các bên liên quan và mối quan tâm, Mô tả tóm tắt, Điều kiện kích hoạt.
3. Tiền điều kiện: Trạng thái hệ thống và điều kiện dữ liệu phải thỏa mãn trước khi bắt đầu chạy ca sử dụng.
4. Bảo đảm tối thiểu: Trạng thái hệ thống được bảo toàn nguyên trạng khi giao dịch thất bại, bị hủy hoặc gặp lỗi kiểm tra dữ liệu.
5. Hậu điều kiện thành công: Trạng thái dữ liệu lâu dài của hệ thống sau khi luồng sự kiện chính hoàn tất, trong đó các quy tắc tài chính luôn đúng vẫn được giữ.
6. Luồng sự kiện chính: Trình tự các bước tương tác tuần tự giữa người dùng và hệ thống từ thời điểm bắt đầu đến khi đạt được mục tiêu.
7. Các luồng rẽ nhánh: Các biến thể thực hiện hợp lệ khác của ca sử dụng dẫn tới kết quả thành công hoặc kết thúc hợp lệ.
8. Các luồng ngoại lệ: Các tình huống lỗi nhập liệu, vi phạm giới hạn số học hoặc điều kiện biên, chỉ rõ phản ứng xử lý của hệ thống, thông báo lỗi cụ thể và điểm phục hồi.
9. Quy tắc nghiệp vụ đặc thù: Danh sách liên kết trực tiếp tới các quy tắc nghiệp vụ BR-FIN tại Mục 3 và yêu cầu chức năng `FR` tại Mục 4 mà ca sử dụng trực tiếp áp dụng.

Nguyên tắc kỹ thuật xuyên suốt:
- Đơn tác nhân: Toàn bộ 44 ca sử dụng do một tác nhân duy nhất là Người dùng tương tác trực tiếp qua giao diện máy tính để bàn cục bộ.
- Độc lập nghiệp vụ: Mỗi ca sử dụng bao đóng một mục tiêu người dùng hoàn chỉnh; không gộp các thao tác tạo, sửa, xóa thành một ca sử dụng chung.
- Cập nhật không sai lệch: Mọi thao tác cập nhật dữ liệu tài chính nhiều bước chỉ được ghi nhận khi mọi bước thành công; nếu xảy ra lỗi, hệ thống tự động hoàn nguyên toàn bộ trạng thái.
- Phân định rõ luồng rẽ nhánh và ngoại lệ: Luồng rẽ nhánh thể hiện các biến thể thành công hợp lệ; các trường hợp vi phạm dữ liệu hoặc điều kiện biên được phân loại vào luồng ngoại lệ.

Bảng chia 44 ca sử dụng theo 10 nhóm chức năng nghiệp vụ:

| STT | Nhóm chức năng nghiệp vụ | Mã nhóm | Số lượng UC | Dải mã ca sử dụng |
| :---: | :--- | :---: | :---: | :--- |
| 1 | Ví tiền | WAL | 4 | UC-WAL-01 đến UC-WAL-04 |
| 2 | Ghi nhận thu chi | TXN | 4 | UC-TXN-01 đến UC-TXN-04 |
| 3 | Chuyển tiền nội bộ | TRF | 3 | UC-TRF-01 đến UC-TRF-03 |
| 4 | Danh mục phân loại | CAT | 4 | UC-CAT-01 đến UC-CAT-04 |
| 5 | Công nợ | DEBT | 3 | UC-DEBT-01 đến UC-DEBT-03 |
| 6 | Sổ tiết kiệm | SAV | 6 | UC-SAV-01 đến UC-SAV-06 |
| 7 | Tài sản đầu tư | INV | 8 | UC-INV-01 đến UC-INV-08 |
| 8 | Quỹ mục tiêu | FND | 5 | UC-FND-01 đến UC-FND-05 |
| 9 | Thống kê và báo cáo | REP | 4 | UC-REP-01 đến UC-REP-04 |
| 10 | Lưu trữ và sao lưu dữ liệu | DAT | 3 | UC-DAT-01 đến UC-DAT-03 |
| **Tổng** | **10 nhóm chức năng** | | **44 UC** | |

---

### 6.1 UC-WAL-01: Tạo ví tiền mới

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-WAL-01 |
| Tên | Tạo ví tiền mới |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần tạo tài khoản chứa tiền để ghi chép dòng tiền và tổng hợp tài sản ròng chính xác. Hệ thống: Bảo đảm số dư ban đầu không âm, tên ví là duy nhất và số dư luôn đúng theo quy tắc `BR-FIN-02`. |
| Mô tả tóm tắt | Tạo một ví tài chính mới với các thuộc tính định danh, phân loại ví, số dư ban đầu và ghi chú; đặt số dư ban đầu theo quy tắc luôn đúng về số dư và tính lại tỷ lệ cơ cấu tài sản giữa các ví. |
| Điều kiện kích hoạt | Người dùng chọn chức năng thêm ví mới trên giao diện quản trị ví tiền. |

#### Tiền điều kiện

1. Hệ thống PFMS đã khởi chạy và đọc tệp dữ liệu cục bộ vào bộ nhớ thành công.
2. Người dùng đang ở màn hình Quản lý ví tiền.
3. Hệ thống đã đọc danh sách ví hiện có. Ở lần chạy đầu tiên, danh sách chỉ có ví tiền mặt có sẵn.

#### Bảo đảm tối thiểu

Nếu quá trình tạo ví gặp lỗi kiểm tra dữ liệu hoặc lỗi ghi tệp, trạng thái tệp dữ liệu và danh sách ví không thay đổi; không có đối tượng ví rác nào được lưu trữ.

#### Hậu điều kiện thành công

1. Bản ghi ví mới được lưu vào tệp dữ liệu với mã duy nhất, trạng thái `ACTIVE` và số dư ban đầu hợp lệ.
2. Quy tắc luôn đúng về số dư theo `BR-FIN-02` được áp dụng cho ví mới; tổng tiền trong các ví toàn hệ thống ($TotalWalletBalance$ theo FR-WAL-02 quy tắc 4) được cập nhật ngay.
3. Bảng cơ cấu tài sản phản ánh đúng tỷ lệ của ví mới theo `FR-WAL-04`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Thêm ví mới" trên giao diện quản lý ví.
2. Hệ thống hiển thị biểu mẫu tạo ví gồm các trường: Tên ví, Loại ví (`CASH`, `BANK`, `E_WALLET`), Số dư ban đầu, Ghi chú mô tả.
3. Người dùng nhập tên ví, chọn loại ví từ danh sách lựa chọn, nhập số dư ban đầu (mặc định bằng 0), và nhập ghi chú mô tả (nếu có).
4. Người dùng gửi yêu cầu lưu thông tin ví mới.
5. Hệ thống kiểm tra dữ liệu đầu vào:
   - Kiểm tra chuỗi ký tự tên ví không rỗng sau khi cắt khoảng trắng đầu cuối, độ dài từ 1 đến 50 ký tự.
   - Kiểm tra tính duy nhất của tên ví (so sánh không phân biệt chữ hoa, chữ thường với tập tên ví hiện có).
   - Kiểm tra số dư ban đầu không âm ($walletInitialBalance \ge 0$) đối với các loại ví `CASH`, `BANK`, `E_WALLET`.
6. Hệ thống tạo đối tượng ví mới trong tệp dữ liệu với mã duy nhất, gán trạng thái `ACTIVE`, đưa số dư ban đầu về kiểu dữ liệu tài chính chính xác với 2 chữ số thập phân theo quy tắc `BR-FIN-01`.
7. Hệ thống tính số dư tính lại từ lịch sử giao dịch của ví mới và tổng số dư các ví toàn hệ thống ($TotalWalletBalance$ theo FR-WAL-02 quy tắc 4) theo công thức `BR-FIN-02`.
8. Hệ thống tính lại tỷ lệ chia tài sản của từng ví theo quy tắc `FR-WAL-04`.
9. Hệ thống lưu trạng thái xuống tệp dữ liệu cục bộ, hiển thị thông báo tạo ví thành công và làm mới danh sách ví cùng bảng cơ cấu tài sản trên giao diện.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Trùng tên ví đã tồn tại
1. Tại bước 5 của luồng chính, hệ thống phát hiện tên ví đã tồn tại trong danh sách ví hiện có (sau khi đưa về cùng kiểu chữ hoa, chữ thường và cắt khoảng trắng đầu cuối).
2. Hệ thống hủy bỏ thao tác ghi, giữ nguyên dữ liệu đã nhập trên biểu mẫu, đánh dấu trường tên ví và hiển thị thông báo lỗi: "Tên ví này đã tồn tại trong hệ thống. Vui lòng chọn một tên khác".
3. Luồng quay lại bước 3 của luồng chính để người dùng điều chỉnh tên ví hoặc hủy bỏ thao tác.

##### Luồng ngoại lệ 5b: Số dư ban đầu là số âm cho ví thông thường
1. Tại bước 5 của luồng chính, hệ thống thấy ví thuộc loại `CASH`, `BANK` hoặc `E_WALLET` nhưng số dư ban đầu nhập vào nhỏ hơn 0 ($walletInitialBalance < 0$).
2. Hệ thống chặn thao tác tạo ví, đánh dấu trường số dư ban đầu và hiển thị thông báo lỗi: "Số dư ban đầu của ví tiền mặt, ngân hàng hoặc ví điện tử không được nhỏ hơn 0".
3. Luồng quay lại bước 3 của luồng chính để người dùng nhập lại số dư ban đầu hợp lệ.

#### Quy tắc nghiệp vụ đặc thù

1. Số dư ví luôn khớp lịch sử giao dịch theo `BR-FIN-02`: $WalletBalance_{wallet}(t) = WalletBalance_{wallet}(0) + \sum Inflow - \sum Outflow$.
2. Quy tắc tính toán tài chính `BR-FIN-01`: Bắt buộc sử dụng kiểu dữ liệu số học có độ chính xác cố định (2 chữ số thập phân, quy tắc làm tròn ngân hàng).
3. Yêu cầu chức năng liên quan: `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`.

---

### 6.2 UC-WAL-02: Theo dõi số dư tính lại từ lịch sử giao dịch và cơ cấu ví

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-WAL-02 |
| Tên | Theo dõi số dư tính lại từ lịch sử giao dịch và cơ cấu ví |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần nắm số dư khả dụng thực tế của từng tài khoản thanh toán và tỷ lệ chia dòng tiền giữa các ví để ra quyết định chi tiêu hoặc điều chuyển vốn. |
| Mô tả tóm tắt | Truy vấn và tính số dư khả dụng theo thời gian thực của từng ví dựa trên toàn bộ lịch sử dòng tiền đã có; phân tích tỷ lệ cơ cấu tiền trong ví giữa các ví đang hoạt động và các ví đã lưu trữ có số dư lớn hơn 0.00 VNĐ. |
| Điều kiện kích hoạt | Người dùng truy cập màn hình quản lý ví hoặc chuyển sang tab phân tích cơ cấu tài sản. |

#### Tiền điều kiện

1. Hệ thống PFMS đã khởi chạy và đọc tệp dữ liệu cục bộ thành công.
2. Người dùng đang ở màn hình Quản lý ví tiền.

#### Bảo đảm tối thiểu

Hệ thống hoạt động ở chế độ chỉ đọc, không làm thay đổi bất kỳ trường dữ liệu nào trong tệp lưu trữ.

#### Hậu điều kiện thành công

1. Số dư khả dụng của từng ví được tính toán chính xác theo công thức `BR-FIN-02` tại thời điểm truy vấn.
2. Tỷ lệ cơ cấu tiền trong các ví được hiển thị chính xác theo quy tắc `FR-WAL-04`.

#### Luồng sự kiện chính

1. Người dùng mở màn hình danh sách ví và chọn bộ lọc trạng thái (`ACTIVE`, `ARCHIVED` hoặc `ALL`, mặc định là `ACTIVE`).
2. Hệ thống quét toàn bộ các bản ghi dòng tiền liên quan đến từng ví thỏa mãn điều kiện lọc, áp dụng công thức `BR-FIN-02` để tính lại số dư khả dụng từ lịch sử giao dịch:
   $$WalletBalance_w = WalletBalance_w(0) + \sum Income_w + \sum TransferIn_w + \sum Borrow_w + \sum Collect_w + \sum SavingPayout_w + \sum InvSell_w + \sum InvEvent_w - \sum Expense_w - \sum TransferOut_w - \sum Repay_w - \sum Lend_w - \sum SavingPrincipal_w - \sum InvBuy_w + \sum Adjustment_w$$
3. Hệ thống tính tổng số dư các ví toàn hệ thống ($TotalWalletBalance$ theo FR-WAL-02 quy tắc 4) bằng phép cộng số thập phân chính xác. Tổng này gồm các ví `ACTIVE` và các ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ, không phụ thuộc bộ lọc hiển thị.
4. Hệ thống hiển thị bảng danh sách ví gồm: Mã ví, Tên ví, Loại ví, Trạng thái, Số dư tính lại từ lịch sử giao dịch, và dòng tổng số dư khả dụng toàn hệ thống ở chân bảng.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Xem phân tích cơ cấu tiền trong ví
1. Tại bước 1 của luồng chính, người dùng chọn tab "Cơ cấu phân bổ ví".
2. Hệ thống tính tổng số dư của các ví ở trạng thái `ACTIVE` và các ví ở trạng thái `ARCHIVED` có số dư lớn hơn 0.00 VNĐ: $TotalWalletBalance = \sum_{w \in ActiveWallets} WalletBalance_w + \sum_{w \in ArchivedWallets, WalletBalance_w > 0} WalletBalance_w$.
3. Nếu $TotalWalletBalance > 0$, hệ thống tính tỷ lệ của từng ví theo công thức:
   $$Rate_w = \left(\frac{WalletBalance_w}{TotalWalletBalance}\right) \times 100\%$$
   Phép tính thực hiện với tối thiểu 4 chữ số thập phân, làm tròn theo quy tắc ngân hàng và định dạng hiển thị 2 chữ số thập phân (ví dụ: 35.50%).
4. Hệ thống sắp xếp danh sách ví theo thứ tự tỷ lệ giảm dần và xuất dữ liệu ra bảng thống kê kèm biểu đồ trực quan.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3a: Tổng số dư toàn bộ các ví bằng 0 khi phân tích cơ cấu
1. Tại bước 3 của luồng rẽ nhánh 1a, hệ thống thấy $TotalWalletBalance = 0$.
2. Hệ thống bỏ qua phép chia cho 0, tự động gán tỷ lệ $Rate_w = 0.00\%$ cho toàn bộ các ví.
3. Hệ thống hiển thị bảng thống kê với tỷ lệ 0.00% kèm thông báo trạng thái: "Tổng số dư các ví hiện tại bằng 0, không có cơ cấu phân bổ".

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc luôn đúng về số dư theo `BR-FIN-02`: Số dư hiển thị là số dư tính lại từ lịch sử dòng tiền, không lưu trường số dư lưu sẵn vì có thể gây sai lệch.
2. Quy tắc tính toán tài chính `BR-FIN-01`: Mọi phép tính cộng dồn và chia tỷ lệ đều dùng kiểu dữ liệu số thập phân chính xác.
3. Yêu cầu chức năng liên quan: `FR-WAL-02`, `FR-WAL-04`.

---

### 6.3 UC-WAL-03: Cập nhật thông tin ví tiền

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-WAL-03 |
| Tên | Cập nhật thông tin ví tiền |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần đổi tên, ghi chú, lưu trữ ví không dùng hoặc xóa ví tạo nhầm chưa có giao dịch. Hệ thống: Cấm can thiệp trực tiếp vào số dư ví qua biểu mẫu cập nhật. Các giao dịch liên kết vẫn phải trỏ đúng tới ví. |
| Mô tả tóm tắt | Chỉnh sửa các trường thông tin mô tả của ví (tên ví, ghi chú) và chuyển đổi trạng thái hoạt động giữa `ACTIVE` và `ARCHIVED`, hoặc xóa vĩnh viễn ví chưa phát sinh giao dịch. Trường số dư bị khóa hoàn toàn. Khi lưu trữ ví còn số dư, hệ thống đưa ra cảnh báo kiểm soát. |
| Điều kiện kích hoạt | Người dùng chọn thao tác chỉnh sửa, lưu trữ hoặc xóa một ví cụ thể trong danh sách ví. |

#### Tiền điều kiện

1. Ví cần chỉnh sửa hoặc xóa đã tồn tại trong hệ thống.
2. Người dùng đang ở màn hình Quản lý ví tiền.

#### Bảo đảm tối thiểu

Nếu thao tác cập nhật bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, thuộc tính của ví giữ nguyên trạng thái cũ; số dư ví không bao giờ bị biến động qua ca sử dụng này.

#### Hậu điều kiện thành công

1. Thông tin tên ví, ghi chú hoặc trạng thái hoạt động được cập nhật chính xác vào tệp dữ liệu, hoặc bản ghi ví chưa có giao dịch được gỡ bỏ hoàn toàn khỏi bộ lưu trữ.
2. Số dư khả dụng của ví không bị thay đổi.
3. Nếu trạng thái chuyển sang `ARCHIVED`, ví bị loại khỏi các danh sách chọn nguồn tiền của giao dịch mới nhưng dữ liệu vẫn xuất hiện đầy đủ trong các báo cáo lịch sử, số dư ví vẫn được cộng vào tài sản ròng.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng chỉnh sửa tại dòng dữ liệu của một ví cụ thể.
2. Hệ thống hiển thị biểu mẫu cập nhật gồm: Tên ví, Ghi chú mô tả, Trạng thái hoạt động (`ACTIVE` hoặc `ARCHIVED`). Trường Loại ví và Số dư ở trạng thái chỉ đọc, không cho phép chỉnh sửa.
3. Người dùng cập nhật tên ví mới hoặc sửa ghi chú mô tả.
4. Người dùng xác nhận lưu các thay đổi.
5. Hệ thống kiểm tra dữ liệu:
   - Tên ví không rỗng sau khi cắt khoảng trắng thừa, độ dài từ 1 đến 50 ký tự.
   - Tên ví không trùng lặp với tên của các ví khác đang tồn tại trong hệ thống (so sánh không phân biệt hoa thường).
6. Hệ thống cập nhật các trường thông tin vào đối tượng ví trong tệp dữ liệu.
7. Hệ thống thông báo cập nhật thông tin ví thành công và làm mới giao diện hiển thị.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Chuyển trạng thái ví sang lưu trữ (`ARCHIVED`)
1. Tại bước 3 của luồng chính, người dùng thay đổi trạng thái hoạt động từ `ACTIVE` sang `ARCHIVED`.
2. Hệ thống kiểm tra ràng buộc tài chính theo `FR-WAL-03` quy tắc 3 và `QD-239`: nếu ví đang liên kết với khoản công nợ chưa tất toán hoặc sổ tiết kiệm ở trạng thái `ACTIVE`, hệ thống từ chối lưu trữ và chuyển sang luồng ngoại lệ 3c.
3. Hệ thống kiểm tra số dư khả dụng hiện tại của ví theo `BR-FIN-02`. Nếu số dư khác 0 ($WalletBalance_w \ne 0$), hệ thống hiển thị hộp thoại cảnh báo rủi ro xác nhận: "Ví này hiện vẫn còn số dư khả dụng là [Số dư] VNĐ. Số dư này vẫn được tính vào tài sản ròng nhưng ví sẽ bị ẩn khỏi danh sách chọn khi tạo giao dịch mới. Bạn có chắc chắn muốn lưu trữ ví này không?".
4. Người dùng chọn xác nhận tiếp tục.
5. Hệ thống cập nhật trạng thái `ARCHIVED` trong tệp dữ liệu, ẩn ví khỏi toàn bộ danh sách thả xuống chọn ví trong các chức năng tạo giao dịch mới, và giữ hiển thị trong các báo cáo lịch sử.
6. Luồng tiếp tục tại bước 7 của luồng chính.

##### Luồng rẽ nhánh 3c: Xóa vĩnh viễn ví chưa phát sinh giao dịch
1. Tại màn hình quản lý ví, người dùng chọn chức năng xóa một ví cụ thể.
2. Hệ thống kiểm tra lịch sử dữ liệu của ví. Nếu ví chưa từng phát sinh bất kỳ bản ghi nào trong sổ giao dịch, sổ chuyển tiền, sổ tiết kiệm hoặc liên kết công nợ, hệ thống hiển thị hộp thoại xác nhận xóa vĩnh viễn ví.
3. Người dùng chọn xác nhận xóa.
4. Hệ thống gỡ bỏ hoàn toàn bản ghi ví khỏi bộ lưu trữ và giải phóng tên ví.
5. Hệ thống thông báo xóa ví thành công và làm mới giao diện hiển thị.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Trùng tên ví với ví khác
1. Tại bước 5 của luồng chính, hệ thống phát hiện tên ví mới trùng với tên của một ví khác đã tồn tại.
2. Hệ thống hủy bỏ thao tác ghi, đánh dấu trường tên ví và hiển thị thông báo lỗi: "Tên ví này đã tồn tại trong hệ thống. Vui lòng chọn một tên khác".
3. Luồng quay lại bước 3 của luồng chính để người dùng hiệu chỉnh tên ví.

##### Luồng ngoại lệ 2a: Can thiệp trái phép vào trường số dư ví
1. Tại bước 2 của luồng chính, nếu hệ thống phát hiện yêu cầu chỉnh sửa chứa dữ liệu thay đổi số dư (số dư hiện hành hoặc số dư ban đầu) đối với ví đã có giao dịch.
2. Hệ thống từ chối xử lý, chặn cập nhật số dư, ghi nhật ký cảnh báo vi phạm quy tắc dữ liệu tài chính và hiển thị thông báo lỗi: "Không được phép thay đổi trực tiếp số dư ví. Vui lòng sử dụng chức năng Điều chỉnh số dư hoặc Ghi nhận giao dịch".

##### Luồng ngoại lệ 3b: Lưu trữ ví tiền mặt có sẵn
1. Tại bước 3 của luồng chính, trước khi xét luồng rẽ nhánh 3a, người dùng đổi trạng thái của ví tiền mặt có sẵn từ `ACTIVE` sang `ARCHIVED`.
2. Hệ thống từ chối cập nhật và thông báo lỗi: "Ví tiền mặt có sẵn không thể lưu trữ".
3. Luồng quay lại bước 3 của luồng chính.

##### Luồng ngoại lệ 3c: Ví còn liên kết với sổ tiết kiệm đang hoạt động hoặc công nợ chưa tất toán
1. Tại bước 2 của luồng rẽ nhánh 3a, hệ thống phát hiện ví đang liên kết với khoản công nợ chưa tất toán hoặc sổ tiết kiệm ở trạng thái `ACTIVE`.
2. Hệ thống từ chối lưu trữ và hiển thị thông báo lỗi theo `FR-WAL-03`: "Ví còn liên quan đến khoản tài chính khác nên không thể lưu trữ".
3. Luồng quay lại bước 3 của luồng chính.

##### Luồng ngoại lệ 3e: Từ chối xóa vĩnh viễn ví đã có lịch sử giao dịch
1. Tại bước 2 của luồng rẽ nhánh 3c, hệ thống phát hiện ví đã có bản ghi giao dịch trong quá khứ.
2. Hệ thống từ chối xóa vĩnh viễn và hiển thị thông báo lỗi: "Ví đã có lịch sử giao dịch nên không thể xóa vĩnh viễn. Vui lòng chuyển ví sang trạng thái lưu trữ".

##### Luồng ngoại lệ 3d: Từ chối xóa ví tiền mặt có sẵn
1. Người dùng chọn xóa ví tiền mặt có sẵn từ lần khởi tạo đầu tiên.
2. Hệ thống từ chối xóa và hiển thị thông báo lỗi: "Ví tiền mặt có sẵn không thể xóa".

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc giữ dữ liệu không sai lệch: Không hỗ trợ thao tác xóa vật lý đối với ví tiền đã có bất kỳ bản ghi nào trong sổ giao dịch, sổ chuyển tiền hoặc sổ công nợ. Ví chưa từng phát sinh giao dịch được phép xóa vĩnh viễn. Muốn ngừng sử dụng ví đã có giao dịch thì chuyển sang trạng thái `ARCHIVED`.
2. Quy tắc tính toán tài chính `BR-FIN-01`: Số dư ví chỉ thay đổi gián tiếp qua các nghiệp vụ có dòng tiền hợp lệ.
3. Yêu cầu chức năng liên quan: `FR-WAL-01`, `FR-WAL-03`.

---

### 6.4 UC-WAL-04: Điều chỉnh số dư ví

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-WAL-04 |
| Tên | Điều chỉnh số dư ví |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần đồng bộ số dư hệ thống khớp với kiểm kê thực tế (tiền mặt, sao kê ngân hàng) mà vẫn giữ dấu vết để kiểm tra lại chênh lệch. Hệ thống: Bản ghi điều chỉnh không được phép sửa. Khoản chênh lệch phải được ghi nhận thành khoản điều chỉnh số dư độc lập để truy vết. |
| Mô tả tóm tắt | Ghi nhận khoản điều chỉnh số dư có dấu vết khi số dư thực tế lệch khỏi số dư hệ thống. Hệ thống tính chênh lệch, hiển thị bản xem trước và cập nhật số dư tính lại mà không can thiệp vào các giao dịch cũ. |
| Điều kiện kích hoạt | Người dùng phát hiện số dư thực tế khác với số dư hệ thống và chọn chức năng "Điều chỉnh số dư". |

#### Tiền điều kiện

1. Ví cần điều chỉnh tồn tại trong hệ thống và đang ở trạng thái `ACTIVE`.
2. Loại ví thuộc tập hợp {`CASH`, `BANK`, `E_WALLET`}.
3. Người dùng đang ở màn hình Quản lý ví tiền.

#### Bảo đảm tối thiểu

Nếu người dùng hủy bỏ hoặc thao tác ghi gặp lỗi, số dư tính lại của ví được giữ nguyên trạng; không có khoản điều chỉnh rác nào được lưu lại.

#### Hậu điều kiện thành công

1. Khoản điều chỉnh mới được lưu vào tệp dữ liệu và không được sửa về sau, với các trường: mã khoản điều chỉnh, mã ví, ngày ghi nhận, số dư trước điều chỉnh, số dư quan sát, giá trị chênh lệch điều chỉnh, lý do điều chỉnh.
2. Số dư tính lại của ví khớp chính xác với số dư quan sát do người dùng nhập.
3. Quy tắc luôn đúng về số dư theo `BR-FIN-02` được thỏa mãn nhờ số hạng bổ sung $\sum Adjustment_w$.

#### Luồng sự kiện chính

1. Người dùng chọn một ví đang hoạt động và nhấn nút "Điều chỉnh số dư".
2. Hệ thống tính toán số dư hiện tại của ví theo `BR-FIN-02` ($walletCurrentBalance$) và hiển thị biểu mẫu điều chỉnh gồm: Số dư hiện tại (chỉ đọc), Số dư thực tế quan sát, Lý do điều chỉnh.
3. Người dùng nhập số dư thực tế quan sát được và nhập nội dung giải trình bắt buộc.
4. Người dùng yêu cầu xem trước kết quả điều chỉnh.
5. Hệ thống kiểm tra dữ liệu và tính khoản chênh lệch:
   $$Adjustment = walletObservedBalance - walletCurrentBalance$$
   Kiểm tra giá trị $Adjustment \ne 0$, kiểm tra lý do điều chỉnh không được để trống, và kiểm tra điều kiện số dư hợp lệ của ví theo loại ví.
6. Hệ thống hiển thị hộp thoại xác nhận thể hiện: Số dư trước điều chỉnh, Số dư sau điều chỉnh, Giá trị chênh lệch (dương hoặc âm) và Lý do điều chỉnh.
7. Người dùng nhấn nút "Xác nhận điều chỉnh".
8. Hệ thống lưu bản ghi điều chỉnh mới vào tệp dữ liệu, cập nhật số dư tính lại của ví và hiển thị thông báo điều chỉnh thành công.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Thiếu lý do điều chỉnh
1. Tại bước 5 của luồng chính, hệ thống phát hiện trường lý do điều chỉnh để trống hoặc chỉ chứa khoảng trắng.
2. Hệ thống chặn thao tác, đánh dấu trường lý do và thông báo lỗi: "Vui lòng nhập lý do điều chỉnh số dư".
3. Luồng quay lại bước 3 của luồng chính để người dùng bổ sung lý do.

##### Luồng ngoại lệ 5b: Số dư quan sát trùng với số dư hệ thống
1. Tại bước 5 của luồng chính, hệ thống tính toán giá trị chênh lệch $Adjustment = 0$.
2. Hệ thống từ chối tạo khoản ghi rỗng và hiển thị thông báo: "Số dư nhập vào trùng với số dư hệ thống đang ghi nhận, không có chênh lệch cần điều chỉnh".
3. Luồng quay lại bước 3 của luồng chính để người dùng kiểm tra lại số liệu.

##### Luồng ngoại lệ 5c: Số dư điều chỉnh vi phạm điều kiện trạng thái
1. Tại bước 5 của luồng chính, hệ thống phát hiện số dư quan sát nhỏ hơn 0 ($walletObservedBalance < 0$).
2. Hệ thống từ chối ghi nhận và thông báo lỗi: "Số dư thực tế nhập vào không hợp lệ với loại ví này (số dư ví thông thường không được nhỏ hơn 0)".
3. Luồng quay lại bước 3 của luồng chính để người dùng điều chỉnh lại số dư.

#### Quy tắc nghiệp vụ đặc thù

1. Khoản điều chỉnh không phá công thức số dư theo `BR-FIN-02`: Khoản điều chỉnh số dư là một số hạng độc lập trong công thức tính số dư từ lịch sử giao dịch. Khoản này không sửa đổi hay xóa bỏ các giao dịch dòng tiền trong quá khứ.
2. Quy tắc dấu vết kiểm tra lại: Bản ghi điều chỉnh số dư không được sửa, không hỗ trợ thao tác sửa hoặc xóa sau khi đã ghi nhận.
3. Yêu cầu chức năng liên quan: `FR-WAL-05`.

---

### 6.5 UC-TXN-01: Ghi nhận giao dịch thu chi

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-TXN-01 |
| Tên | Ghi nhận giao dịch thu chi |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần ghi nhận chính xác khoản thu chi hàng ngày gắn với danh mục và nguồn tiền tương ứng. Hệ thống: Cập nhật đồng thời số dư ví, không để ví bị âm số dư. Dữ liệu thu chi được phân loại theo cây danh mục 3 cấp. |
| Mô tả tóm tắt | Ghi chép một giao dịch thu nhập hoặc chi tiêu, phân loại theo cây danh mục 3 cấp (Loại thu chi -> Nhóm nhu cầu -> Danh mục), liên kết với ví thanh toán tương ứng. Hệ thống cập nhật số dư ví kèm theo việc ghi giao dịch. |
| Điều kiện kích hoạt | Người dùng chọn chức năng thêm giao dịch mới trên màn hình quản lý giao dịch thu chi. |

#### Tiền điều kiện

1. Hệ thống có ít nhất một ví tiền ở trạng thái `ACTIVE`.
2. Cây danh mục 3 cấp đã có ít nhất một danh mục chi tiết đang dùng thuộc loại tương ứng.
3. Người dùng đang ở màn hình Quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu giao dịch bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, số dư ví không bị trừ hoặc cộng, không có bản ghi giao dịch rác nào được lưu vào danh sách `transactions`.

#### Hậu điều kiện thành công

1. Bản ghi giao dịch mới được lưu vào danh sách `transactions` với mã duy nhất.
2. Số dư của ví liên quan được cập nhật ngay theo quy tắc `BR-FIN-02` (trừ tiền nếu `Expense`, cộng tiền nếu `Income`).
3. Số dư của ví thông thường không âm sau giao dịch chi tiêu.
4. Các chỉ số tổng hợp dòng tiền tháng và báo cáo cơ cấu thu chi được cập nhật ngay.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Thêm giao dịch mới".
2. Hệ thống hiển thị biểu mẫu nhập giao dịch gồm các trường: Ngày giao dịch (mặc định là ngày hiện tại), Loại giao dịch (`Income` hoặc `Expense`), Ví thanh toán, Cây danh mục 3 cấp, Số tiền giao dịch, Ghi chú mô tả.
3. Người dùng chọn Loại giao dịch là `Expense` (Chi tiêu).
4. Hệ thống tải danh sách các Nhóm nhu cầu và Danh mục chi tiết đang dùng, thuộc loại `Expense` theo quy tắc `BR-FIN-04`.
5. Người dùng chọn ngày giao dịch, chọn ví thanh toán (trạng thái `ACTIVE`), chọn danh mục chi tiết, nhập số tiền giao dịch ($amount > 0$) và nhập ghi chú mô tả nội dung chi tiêu.
6. Người dùng xác nhận lưu giao dịch mới.
7. Hệ thống kiểm tra nghiệp vụ:
   - Số tiền giao dịch $amount$ phải lớn hơn 0 và được đưa về kiểu dữ liệu số thập phân chính xác với 2 chữ số thập phân theo quy tắc `BR-FIN-01`.
   - Danh mục được chọn phải đang dùng và thuộc đúng loại `Expense` theo quy tắc `BR-FIN-04`.
   - Kiểm tra số dư ví theo `BR-FIN-02`: hệ thống kiểm tra số dư khả dụng bảo đảm điều kiện $WalletBalance_{wallet} - amount \ge 0$.
8. Hệ thống tạo bản ghi giao dịch mới trong danh sách `transactions` kèm mã giao dịch duy nhất.
9. Hệ thống cập nhật số dư ví: trừ số tiền $amount$ khỏi số dư khả dụng của ví được chọn theo quy tắc `BR-FIN-02`.
10. Hệ thống cập nhật các chỉ số tổng hợp tài chính của tháng quan sát.
11. Hệ thống lưu tệp dữ liệu, hiển thị thông báo ghi nhận giao dịch thành công và làm mới bảng nhật ký giao dịch.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Ghi nhận giao dịch Thu nhập (`Income`)
1. Tại bước 3 của luồng chính, người dùng chọn Loại giao dịch là `Income`.
2. Hệ thống tải danh sách các Nhóm nhu cầu và Danh mục đang dùng, thuộc loại Thu nhập (`Income`) (tiền lương, tiền thưởng, v.v.). Khoản thu từ tài sản đầu tư không ghi bằng giao dịch này mà ghi qua sự kiện Nhận tiền mặt theo `UC-INV-05`.
3. Người dùng chọn ví nhận tiền, chọn danh mục thu nhập, nhập số tiền ($amount > 0$) và ghi chú.
4. Tại bước 7 của luồng chính, hệ thống kiểm tra danh mục thuộc loại Thu nhập (`Income`) và số tiền $amount > 0$ (bỏ qua kiểm tra số dư ví phải đủ để trừ).
5. Tại bước 9 của luồng chính, hệ thống cộng số tiền $amount$ vào số dư khả dụng của ví nhận tiền theo quy tắc `BR-FIN-02`.
6. Luồng tiếp tục tại bước 10 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 7a: Số tiền giao dịch không hợp lệ
1. Tại bước 7 của luồng chính, hệ thống phát hiện $amount \le 0$ hoặc giá trị nhập vào không đúng định dạng số học.
2. Hệ thống từ chối lưu, đánh dấu trường số tiền và hiển thị thông báo lỗi: "Số tiền giao dịch phải là một số dương lớn hơn 0".
3. Luồng quay lại bước 5 của luồng chính để người dùng nhập lại số tiền hợp lệ.

##### Luồng ngoại lệ 7b: Số dư ví không đủ chi trả
1. Tại bước 7 của luồng chính, hệ thống phát hiện ví thanh toán có số dư khả dụng nhỏ hơn số tiền chi tiêu ($WalletBalance_{wallet} < amount$).
2. Hệ thống từ chối thực hiện giao dịch và hiển thị thông báo lỗi: "Số dư khả dụng của ví không đủ để thực hiện khoản chi tiêu này".
3. Luồng quay lại bước 5 của luồng chính để người dùng chọn ví khác hoặc điều chỉnh giảm số tiền chi tiêu.

##### Luồng ngoại lệ 7c: Danh mục đã xóa hoặc không khớp Loại giao dịch
1. Tại bước 7 của luồng chính, hệ thống phát hiện danh mục đã xóa hoặc có Loại thu chi không khớp với Loại giao dịch đã chọn.
2. Hệ thống chặn thao tác và hiển thị cảnh báo: "Danh mục không hợp lệ hoặc không tương thích với loại luồng tiền đã chọn".
3. Luồng quay lại bước 5 của luồng chính để người dùng chọn lại danh mục hợp lệ.

#### Quy tắc nghiệp vụ đặc thù

1. Số dư ví luôn khớp theo `BR-FIN-02`: Mọi giao dịch thu chi đều phản ánh trực tiếp vào số dư ví tương ứng cùng lúc với việc ghi giao dịch; nếu một trong hai việc thất bại thì cả hai đều không có hiệu lực.
2. Quy tắc phân cấp danh mục `BR-FIN-04`: Mọi giao dịch phải được phân loại theo cấu trúc 3 cấp và giữ cho Loại thu chi luôn nhất quán.
3. Độ chính xác tài chính `BR-FIN-01`: Xử lý toàn bộ phép toán bằng kiểu số thập phân chính xác với 2 chữ số thập phân, không làm tròn nhị phân.
4. Yêu cầu chức năng liên quan: `FR-TXN-01`, `FR-TXN-02`.

---

### 6.6 UC-TXN-02: Tra cứu và lọc lịch sử giao dịch

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-TXN-02 |
| Tên | Tra cứu và lọc lịch sử giao dịch |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần tìm kiếm nhanh chóng và đối chiếu các khoản thu chi đã thực hiện theo nhiều tiêu chí kết hợp để kiểm soát dòng tiền. |
| Mô tả tóm tắt | Tìm kiếm, lọc và phân trang lịch sử giao dịch đa tiêu chí (khoảng thời gian, danh sách ví, loại giao dịch, danh mục phân loại, từ khóa ghi chú); tự động tính tổng thu, tổng chi và dòng tiền thuần trên tập kết quả lọc. |
| Điều kiện kích hoạt | Người dùng mở màn hình nhật ký giao dịch hoặc áp dụng bộ lọc tra cứu. |

#### Tiền điều kiện

1. Hệ thống PFMS đã đọc tệp dữ liệu cục bộ thành công.
2. Người dùng đang ở màn hình Quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Truy vấn ở chế độ chỉ đọc, không làm biến đổi bất kỳ bản ghi giao dịch hay số dư ví nào trong hệ thống.

#### Hậu điều kiện thành công

1. Danh sách giao dịch hiển thị khớp chính xác với tập kết quả lọc theo điều kiện logic kết hợp VÀ.
2. Khối tổng hợp tài chính (tổng thu nhập, tổng chi tiêu, dòng tiền thuần) phản ánh đúng dữ liệu của tập kết quả lọc theo quy tắc `BR-FIN-01`.

#### Luồng sự kiện chính

1. Người dùng mở màn hình "Nhật ký giao dịch" và chọn các tiêu chí lọc:
   - Khoảng thời gian: Từ ngày đến ngày.
   - Phạm vi ví: Một hoặc nhiều ví cụ thể, hoặc toàn bộ ví.
   - Loại giao dịch: `ALL`, `INCOME`, hoặc `EXPENSE`.
   - Phân loại danh mục: Nhóm nhu cầu hoặc Danh mục chi tiết. Danh sách lọc gồm cả nhóm và danh mục đã xóa, kèm nhãn (đã xóa), để tra cứu giao dịch cũ.
   - Từ khóa tìm kiếm: Chuỗi ký tự tra cứu trong trường ghi chú mô tả.
2. Người dùng nhấn nút "Áp dụng bộ lọc".
3. Hệ thống áp dụng logic kết hợp AND trên toàn bộ các tiêu chí đã chọn để lọc tập dữ liệu `transactions`. Hệ thống sắp xếp kết quả theo ngày giao dịch giảm dần (mới nhất lên đầu) và phân trang kết quả (mặc định 50 bản ghi mỗi trang).
4. Hệ thống tính tổng hợp trên toàn bộ tập kết quả lọc:
   $$TotalIncome = \sum Income_{filtered}$$
   $$TotalExpense = \sum Expense_{filtered}$$
   $$NetCashFlow = TotalIncome - TotalExpense$$
   Các phép tính thực hiện bằng kiểu dữ liệu tài chính chính xác với 2 chữ số thập phân theo `BR-FIN-01`.
5. Hệ thống hiển thị bảng danh sách giao dịch, thông tin phân trang và khối thẻ tổng hợp tài chính ở đầu danh sách. Tên danh mục của mỗi giao dịch kèm nhãn (đã xóa) nếu danh mục đã xóa.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3a: Không tìm thấy bản ghi thỏa mãn điều kiện lọc
1. Tại bước 3 của luồng chính, hệ thống không tìm thấy bất kỳ giao dịch nào thỏa mãn đồng thời các tiêu chí lọc.
2. Hệ thống hiển thị bảng dữ liệu rỗng, gán $TotalIncome = 0.00$, $TotalExpense = 0.00$, $NetCashFlow = 0.00$ và hiển thị thông báo trạng thái: "Không có giao dịch nào phù hợp với bộ lọc đã chọn".

#### Quy tắc nghiệp vụ đặc thù

1. Độ chính xác tài chính `BR-FIN-01`: Toàn bộ phép tổng hợp dòng tiền trên tập kết quả lọc thực hiện bằng kiểu dữ liệu tài chính chính xác.
2. Yêu cầu chức năng liên quan: `FR-TXN-03`.

---

### 6.7 UC-TXN-03: Chỉnh sửa giao dịch thu chi

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-TXN-03 |
| Tên | Chỉnh sửa giao dịch thu chi |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần sửa thông tin giao dịch đã lưu (số tiền, ví, danh mục, ghi chú) mà không làm sai lệch số dư tiền hệ thống. Hệ thống: Việc sửa chỉ có hiệu lực khi cả việc hoàn nguyên giao dịch cũ và áp dụng dữ liệu mới đều thành công, bảo đảm quy tắc `BR-FIN-02` luôn đúng. |
| Mô tả tóm tắt | Hiệu chỉnh các thông tin của một bản ghi giao dịch thu chi hiện có. Hệ thống hoàn nguyên số dư của ví cũ và áp dụng số dư mới cho ví mới trong cùng một lần xử lý, chỉ ghi nhận khi cả hai bước thành công, và ngăn chặn việc làm âm số dư ví thông thường. |
| Điều kiện kích hoạt | Người dùng chọn chức năng chỉnh sửa tại một dòng giao dịch trong sổ nhật ký. |

#### Tiền điều kiện

1. Giao dịch cần chỉnh sửa đã tồn tại trong danh sách bản ghi `transactions`.
2. Người dùng đang ở màn hình Quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu thao tác chỉnh sửa thất bại tại bất kỳ bước kiểm tra nào, trạng thái của giao dịch cũ và số dư của các ví liên quan được giữ nguyên vẹn 100% (hoàn nguyên toàn bộ).

#### Hậu điều kiện thành công

1. Bản ghi giao dịch được cập nhật thông tin mới trong tệp dữ liệu.
2. Số dư của ví cũ và ví mới được đồng bộ hóa chính xác theo quy tắc `BR-FIN-02`.
3. Số dư của các ví liên quan không bị âm sau khi hoàn tất chỉnh sửa.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng chỉnh sửa đối với một giao dịch cụ thể từ bảng nhật ký.
2. Hệ thống tải dữ liệu hiện tại của giao dịch lên biểu mẫu chỉnh sửa: Ngày giao dịch, Loại giao dịch, Ví thanh toán, Danh mục 3 cấp, Số tiền, Ghi chú.
3. Người dùng thay đổi các trường cần hiệu chỉnh (số tiền mới $newAmount$, ví thanh toán mới $newWallet$, hoặc danh mục mới) và nhấn nút "Lưu thay đổi".
4. Hệ thống kiểm tra tính hợp lệ về mặt số học: $newAmount > 0$, danh mục sau khi sửa tương thích với loại giao dịch theo `BR-FIN-04`. Nếu người dùng đổi danh mục thì danh mục mới phải đang dùng. Hệ thống vẫn chấp nhận việc giữ nguyên danh mục cũ, kể cả khi danh mục đó đã xóa.
5. Hệ thống tính thử việc hoàn nguyên ví cũ theo `BR-FIN-02`:
   - Nếu giao dịch cũ là `Income`: Ví cũ cần trừ đi số tiền cũ $oldAmount$. Kiểm tra điều kiện ví cũ không bị âm: $WalletBalance_{oldWallet} - oldAmount \ge 0$ (với ví thông thường).
   - Nếu giao dịch cũ là `Expense`: Ví cũ được hoàn trả lại số tiền cũ $oldAmount$.
6. Hệ thống tính thử việc áp dụng giao dịch mới vào ví mới theo `BR-FIN-02`:
   - Nếu giao dịch mới là `Expense`: Kiểm tra số dư ví mới sau khi hoàn nguyên ví cũ có bảo đảm điều kiện $WalletBalance'_{newWallet} - newAmount \ge 0$ hay không.
7. Hệ thống thực hiện các thao tác sau; chỉ khi tất cả thành công, kết quả mới được ghi nhận:
   - Hoàn nguyên số dư ví cũ ($oldWallet$).
   - Cập nhật số dư ví mới ($newWallet$).
   - Ghi đè các trường thông tin mới vào bản ghi giao dịch trong danh sách `transactions`.
8. Hệ thống lưu trạng thái xuống tệp dữ liệu, hiển thị thông báo cập nhật thành công và làm mới bảng nhật ký giao dịch.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Số tiền mới không hợp lệ
1. Tại bước 4 của luồng chính, hệ thống phát hiện $newAmount \le 0$ hoặc sai định dạng số học.
2. Hệ thống từ chối lưu và hiển thị thông báo lỗi: "Số tiền giao dịch phải là một số dương lớn hơn 0".
3. Luồng quay lại bước 3 của luồng chính để người dùng nhập lại số tiền hợp lệ.

##### Luồng ngoại lệ 5a: Hoàn nguyên giao dịch thu nhập làm âm số dư ví cũ
1. Tại bước 5 của luồng chính, hệ thống thấy việc trừ lại $oldAmount$ khỏi ví cũ sẽ khiến số dư ví cũ bị âm ($WalletBalance_{oldWallet} - oldAmount < 0$).
2. Hệ thống từ chối chỉnh sửa và hiển thị thông báo lỗi: "Không thể chỉnh sửa giao dịch này vì việc hoàn nguyên khoản thu sẽ làm số dư của ví cũ bị âm".
3. Luồng quay lại bước 3 của luồng chính hoặc người dùng hủy bỏ thao tác.

##### Luồng ngoại lệ 6a: Số dư ví mới không đủ chi trả cho giao dịch chi tiêu mới
1. Tại bước 6 của luồng chính, hệ thống phát hiện số dư khả dụng của ví mới không đủ để thực hiện khoản chi mới $newAmount$ ($WalletBalance'_{newWallet} < newAmount$).
2. Hệ thống từ chối lưu và thông báo lỗi: "Số dư khả dụng của ví mới không đủ để thực hiện khoản chi tiêu này".
3. Luồng quay lại bước 3 của luồng chính để người dùng chọn ví khác hoặc giảm số tiền.

#### Quy tắc nghiệp vụ đặc thù

1. Dữ liệu không sai lệch khi chỉnh sửa: Việc hoàn nguyên ví cũ và ghi nhận ví mới phải hoàn tất đầy đủ cùng lúc; nếu xảy ra lỗi lưu trữ, toàn bộ trạng thái phải tự động khôi phục về trước khi chỉnh sửa.
2. Số dư ví thông thường không âm theo `BR-FIN-02`: Số dư của các ví thông thường không được phép âm tại bất kỳ thời điểm nào của quá trình chuyển đổi.
3. Yêu cầu chức năng liên quan: `FR-TXN-04`.

---

### 6.8 UC-TXN-04: Xóa giao dịch thu chi

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-TXN-04 |
| Tên | Xóa giao dịch thu chi |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần loại bỏ một giao dịch ghi nhận sai hoặc bị hủy bỏ trong thực tế. Hệ thống: Hoàn nguyên chính xác số dư ví tương ứng, ngăn chặn tình trạng ví bị âm do xóa khoản thu nhập đã bị chi tiêu trong các giao dịch kế tiếp. |
| Mô tả tóm tắt | Xóa bỏ hoàn toàn một bản ghi giao dịch thu chi khỏi hệ thống, tự động thực hiện hoàn nguyên số dư ví liên quan và cập nhật lại các chỉ số thống kê dòng tiền. |
| Điều kiện kích hoạt | Người dùng chọn chức năng xóa tại một dòng giao dịch trong sổ nhật ký. |

#### Tiền điều kiện

1. Bản ghi giao dịch cần xóa tồn tại trong danh sách `transactions`.
2. Người dùng đang ở màn hình Quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu thao tác xóa bị từ chối do vi phạm điều kiện số dư ví hoặc người dùng hủy xác nhận, bản ghi giao dịch và số dư ví được giữ nguyên vẹn.

#### Hậu điều kiện thành công

1. Bản ghi giao dịch bị xóa hoàn toàn khỏi danh sách `transactions`.
2. Số dư của ví liên quan được hoàn nguyên chính xác theo quy tắc `BR-FIN-02` (cộng hoàn trả nếu xóa chi tiêu, trừ lại nếu xóa thu nhập).
3. Báo cáo thống kê tháng và tài sản ròng được cập nhật ngay.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng xóa đối với một giao dịch cụ thể từ bảng nhật ký.
2. Hệ thống hiển thị hộp thoại xác nhận xóa giao dịch, nêu rõ các thông tin: Ngày giao dịch, Danh mục, Số tiền, Ví thanh toán và tác động hoàn nguyên số dư dự kiến.
3. Người dùng nhấn nút "Xác nhận xóa".
4. Hệ thống kiểm tra điều kiện an toàn số dư theo `BR-FIN-02`:
   - Nếu giao dịch là `Expense`: Số dư ví sẽ được cộng hoàn trả lại $amount$ (không có nguy cơ làm âm ví).
   - Nếu giao dịch là `Income`: Hệ thống kiểm tra số dư khả dụng hiện tại của ví bảo đảm điều kiện $WalletBalance_{wallet} - amount \ge 0$ (đối với ví thông thường).
5. Hệ thống thực hiện các thao tác sau; chỉ khi tất cả thành công, kết quả mới được ghi nhận:
   - Hoàn nguyên số dư ví: trừ $amount$ nếu xóa `Income`, cộng $amount$ nếu xóa `Expense`.
   - Xóa bản ghi khỏi danh sách `transactions`.
6. Hệ thống cập nhật các chỉ số tổng hợp tài chính của tháng quan sát.
7. Hệ thống lưu tệp dữ liệu, hiển thị thông báo xóa giao dịch thành công và làm mới bảng nhật ký trên màn hình.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Không thể xóa giao dịch thu nhập do làm âm số dư ví
1. Tại bước 4 của luồng chính, hệ thống phát hiện giao dịch cần xóa là khoản thu nhập (`Income`) và số dư khả dụng hiện tại của ví nhỏ hơn số tiền của giao dịch ($WalletBalance_{wallet} < amount$). Nguyên nhân là số tiền này đã được sử dụng trong các giao dịch chi tiêu hoặc chuyển tiền tiếp theo.
2. Hệ thống hủy bỏ thao tác xóa và hiển thị thông báo lỗi: "Không thể xóa giao dịch thu nhập này vì sẽ làm số dư ví bị âm. Vui lòng kiểm tra lại các khoản chi tiêu liên quan của ví trước khi thực hiện".
3. Luồng kết thúc, trạng thái dữ liệu được bảo toàn nguyên trạng.

#### Quy tắc nghiệp vụ đặc thù

1. Dữ liệu không sai lệch khi xóa: Thao tác xóa bản ghi và hoàn nguyên số dư ví phải diễn ra đồng thời; nếu xảy ra lỗi lưu trữ, hệ thống tự động khôi phục số dư ví và bản ghi giao dịch về trạng thái trước khi xóa.
2. Số dư ví không âm theo `BR-FIN-02`: Không cho phép xóa giao dịch thu nhập nếu hệ quả làm số dư khả dụng của ví thông thường thành số âm.
3. Yêu cầu chức năng liên quan: `FR-TXN-05`.

---

### 6.9 UC-TRF-01: Chuyển tiền nội bộ giữa hai ví

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-TRF-01 |
| Tên | Chuyển tiền nội bộ giữa hai ví |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần chuyển tiền giữa các tài khoản nội bộ (rút tiền mặt, ví điện tử) và ghi nhận đúng phí chuyển tiền. Hệ thống: Áp dụng ghi hai vế (trừ ví nguồn, cộng ví đích). Tổng tiền các ví giữ nguyên nếu không có phí, hoặc chỉ giảm đúng bằng phí theo `BR-FIN-03`. |
| Mô tả tóm tắt | Điều chuyển giá trị tiền tệ giữa hai ví tiền nội bộ. Áp dụng ghi hai vế: tự động trừ số tiền chuyển khỏi ví nguồn, cộng vào ví đích và tách khoản phí chuyển tiền (nếu có) thành một bản ghi chi tiêu độc lập liên kết. |
| Điều kiện kích hoạt | Người dùng chọn chức năng chuyển tiền nội bộ trên giao diện. |

#### Tiền điều kiện

1. Hệ thống có ít nhất hai ví tiền đang ở trạng thái `ACTIVE`.
2. Số dư khả dụng của ví nguồn lớn hơn hoặc bằng tổng số tiền chuyển cộng phí chuyển tiền ($WalletBalance_{from} \ge amount + fee$).
3. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Nếu giao dịch chuyển tiền bị lỗi ở bất kỳ bước nào (ví dụ trừ ví nguồn thành công nhưng cộng ví đích thất bại), toàn bộ giao dịch bị hủy bỏ ngay lập tức, số dư hai ví giữ nguyên vẹn.

#### Hậu điều kiện thành công

1. Bản ghi chuyển tiền mới được lưu vào danh sách `transfers` với mã duy nhất.
2. Hai khoản ghi được lưu cùng lúc: ví nguồn giảm chính xác $amount + fee$, ví đích tăng chính xác $amount$.
3. Nếu có phí chuyển tiền ($fee > 0$), một bản ghi chi tiêu phí độc lập được tự động ghi nhận vào danh sách `transactions` liên kết với mã lệnh chuyển tiền.
4. Tổng tiền trong các ví toàn hệ thống giảm đúng bằng khoản phí $fee$; nếu không có phí thì tổng này giữ nguyên theo `BR-FIN-03`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Chuyển tiền nội bộ".
2. Hệ thống hiển thị biểu mẫu chuyển tiền gồm: Ngày chuyển tiền (mặc định là ngày hiện tại), Ví nguồn (bị trừ tiền), Ví đích (nhận tiền), Số tiền chuyển, Phí chuyển tiền (mặc định bằng 0), Danh mục phí, Ghi chú mô tả. Danh mục phí bắt buộc chọn khi phí lớn hơn 0 và không có mặc định.
3. Người dùng chọn Ví nguồn từ danh sách ví `ACTIVE`. Hệ thống hiển thị số dư khả dụng hiện tại của ví nguồn để người dùng theo dõi.
4. Người dùng chọn Ví đích từ danh sách ví `ACTIVE`.
5. Người dùng nhập số tiền cần chuyển ($amount > 0$).
6. Người dùng nhập phí chuyển tiền ($fee \ge 0$) nếu giao dịch thực tế có chi phí. Nếu $fee > 0$, người dùng chọn một danh mục chi tiêu đang dùng cho khoản phí.
7. Người dùng nhập ghi chú mô tả (nếu có) và nhấn nút "Thực hiện chuyển tiền".
8. Hệ thống kiểm tra nghiệp vụ:
   - Kiểm tra ví nguồn phải khác ví đích: $fromWalletId \ne toWalletId$.
   - Kiểm tra số tiền chuyển $amount > 0$ và phí chuyển tiền $fee \ge 0$, đưa về kiểu dữ liệu số thập phân chính xác với 2 chữ số thập phân theo quy tắc `BR-FIN-01`.
   - Kiểm tra khả năng thanh toán của ví nguồn theo `BR-FIN-02`: $WalletBalance_{from} \ge amount + fee$.
   - Nếu $fee > 0$, kiểm tra đã chọn danh mục phí và danh mục đó đang dùng.
9. Hệ thống ghi hai vế cùng lúc theo quy tắc `BR-FIN-03`:
   - Trừ số tiền $amount$ khỏi số dư ví nguồn $fromWalletId$.
   - Cộng số tiền $amount$ vào số dư ví đích $toWalletId$.
   - Lưu bản ghi chuyển tiền vào danh sách `transfers` với mã lệnh chuyển tiền duy nhất.
10. Nếu $fee > 0$, hệ thống tự động sinh một bản ghi chi tiêu (`Expense`) độc lập trong danh sách `transactions` với số tiền $fee$ và trừ tiếp $fee$ khỏi ví nguồn $fromWalletId$. Bản ghi này mang danh mục phí người dùng đã chọn và lưu mã liên kết tới lệnh chuyển tiền tương ứng.
11. Hệ thống lưu tệp dữ liệu cục bộ và hiển thị thông báo chuyển tiền thành công kèm thông tin chi tiết: số tiền chuyển, phí đã trừ, số dư mới của ví nguồn và ví đích.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 6a: Chuyển tiền không mất phí ($fee = 0$)
1. Tại bước 6 của luồng chính, người dùng giữ nguyên mức phí bằng 0 (hoặc để trống).
2. Hệ thống chỉ ghi hai khoản: trừ $amount$ tại ví nguồn và cộng $amount$ tại ví đích.
3. Hệ thống không tạo bản ghi chi tiêu phí liên kết; tổng số dư toàn bộ các ví trước và sau giao dịch được giữ nguyên hoàn toàn ($\Delta \sum WalletBalance = 0$).
4. Luồng tiếp tục tại bước 11 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 8a: Ví nguồn trùng ví đích
1. Tại bước 8 của luồng chính, hệ thống phát hiện $fromWalletId = toWalletId$.
2. Hệ thống chặn giao dịch, đánh dấu trường ví đích và hiển thị thông báo lỗi: "Ví nguồn và ví đích không được trùng nhau. Vui lòng chọn hai ví khác nhau".
3. Luồng quay lại bước 4 của luồng chính để người dùng chọn lại ví đích.

##### Luồng ngoại lệ 8b: Số tiền chuyển nhỏ hơn hoặc bằng 0
1. Tại bước 8 của luồng chính, hệ thống phát hiện $amount \le 0$ hoặc sai định dạng số học.
2. Hệ thống thông báo lỗi: "Số tiền chuyển phải là một số dương lớn hơn 0".
3. Luồng quay lại bước 5 của luồng chính để người dùng nhập lại số tiền hợp lệ.

##### Luồng ngoại lệ 8c: Số dư ví nguồn không đủ thanh toán
1. Tại bước 8 của luồng chính, hệ thống tính tổng số tiền phải trừ tại ví nguồn là $amount + fee$ và thấy vượt quá số dư khả dụng hiện tại ($WalletBalance_{from} < amount + fee$).
2. Hệ thống từ chối thực hiện và hiển thị thông báo lỗi: "Số dư ví nguồn không đủ để thực hiện chuyển tiền".
3. Luồng quay lại bước 5 của luồng chính để người dùng giảm số tiền hoặc chọn ví nguồn khác.

##### Luồng ngoại lệ 8d: Thiếu danh mục phí
1. Tại bước 8 của luồng chính, hệ thống phát hiện $fee > 0$ nhưng người dùng chưa chọn danh mục phí, hoặc danh mục đã chọn là danh mục đã xóa.
2. Hệ thống từ chối thực hiện và hiển thị thông báo lỗi: "Vui lòng chọn danh mục cho khoản phí".
3. Luồng quay lại bước 6 của luồng chính để người dùng chọn lại danh mục phí.

##### Luồng ngoại lệ 9a: Lỗi một vế trong quá trình ghi hai vế
1. Trong quá trình chạy bước 9 hoặc 10 của luồng chính, nếu thao tác trừ tiền ở ví nguồn thành công nhưng thao tác cộng tiền ở ví đích bị gián đoạn do lỗi kỹ thuật hoặc lỗi ghi tệp.
2. Hệ thống hoàn nguyên toàn bộ giao dịch chuyển tiền, hủy bỏ thao tác trừ tiền ở ví nguồn, đưa hai ví về đúng trạng thái ban đầu.
3. Hệ thống ghi nhật ký lỗi chi tiết và hiển thị thông báo: "Giao dịch chuyển tiền bị gián đoạn và đã được phục hồi nguyên trạng để giữ nguyên dữ liệu tài sản. Vui lòng thử lại sau".

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc ghi hai vế theo `BR-FIN-03`: Tổng tài sản trên toàn bộ các ví luôn giữ nguyên đối với giao dịch chuyển tiền thuần túy ($\Delta \sum WalletBalance = 0$).
2. Quy tắc tách riêng phí dịch vụ: Khoản phí ngân hàng phải được ghi thành một giao dịch chi tiêu (`Expense`) riêng biệt, không được khấu trừ gộp làm sai lệch số tiền chuyển thực nhận của ví đích.
3. Quy tắc số học `BR-FIN-01`: Toàn bộ các bước trừ cộng số dư đều thực hiện bằng kiểu dữ liệu tài chính chính xác.
4. Yêu cầu chức năng liên quan: `FR-TRF-01`, `FR-TRF-02`.

---

### 6.10 UC-TRF-02: Tra cứu lịch sử chuyển tiền

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-TRF-02 |
| Tên | Tra cứu lịch sử chuyển tiền |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần rà soát lại các lệnh điều chuyển dòng tiền đã thực hiện giữa các ví nội bộ để so khớp tài sản. |
| Mô tả tóm tắt | Hiển thị danh sách toàn bộ các lệnh chuyển tiền nội bộ đã thực hiện, sắp xếp theo trình tự thời gian, thể hiện ví nguồn, ví đích, số tiền chuyển, phí dịch vụ và trạng thái lệnh. |
| Điều kiện kích hoạt | Người dùng chọn tab "Lịch sử chuyển tiền" trên màn hình Chuyển tiền nội bộ. |

#### Tiền điều kiện

1. Hệ thống PFMS đã khởi chạy và đọc tệp dữ liệu cục bộ thành công.
2. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Hệ thống chạy ở chế độ chỉ đọc, không làm thay đổi dữ liệu sổ chuyển tiền hay số dư ví.

#### Hậu điều kiện thành công

1. Danh sách lịch sử chuyển tiền được hiển thị đầy đủ, chính xác theo thứ tự ngày giảm dần.

#### Luồng sự kiện chính

1. Người dùng mở tab "Lịch sử chuyển tiền".
2. Hệ thống truy vấn danh sách `transfers`, sắp xếp các bản ghi theo ngày chuyển tiền giảm dần (mới nhất lên trước).
3. Hệ thống hiển thị bảng danh sách chuyển tiền gồm các cột thông tin: Mã lệnh, Ngày thực hiện, Ví nguồn, Ví đích, Số tiền chuyển ($amount$), Phí dịch vụ ($fee$), Trạng thái lệnh (`COMPLETED` hoặc `CANCELLED`), và Ghi chú mô tả.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Chưa có bản ghi chuyển tiền nào
1. Tại bước 2 của luồng chính, hệ thống phát hiện danh sách `transfers` rỗng.
2. Hệ thống hiển thị bảng rỗng kèm thông báo trạng thái: "Chưa có giao dịch chuyển tiền nội bộ nào được ghi nhận".

#### Quy tắc nghiệp vụ đặc thù

1. Dữ liệu hiển thị đúng theo `BR-FIN-02`, `BR-FIN-03`: Số tiền chuyển, phí dịch vụ và trạng thái lệnh hiển thị phải phản ánh chính xác các khoản ghi tương ứng đã ghi nhận tại ví nguồn và ví đích. Hệ thống không hiển thị dữ liệu sai lệch với dữ liệu tiền trong hệ thống.
2. Yêu cầu chức năng liên quan: `FR-TRF-02`, `FR-TRF-03`.

---

### 6.11 UC-TRF-03: Hủy giao dịch chuyển tiền

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-TRF-03 |
| Tên | Hủy giao dịch chuyển tiền |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần hủy bỏ một lệnh chuyển tiền nhầm lẫn và trả số dư của hai ví về như cũ. Hệ thống: Áp dụng ghi hai vế khi hoàn nguyên; chỉ cho phép hủy khi ví đích còn đủ số dư để hoàn trả, giữ cho các quy tắc `BR-FIN-02` và `BR-FIN-03` luôn đúng. |
| Mô tả tóm tắt | Hủy bỏ một lệnh chuyển tiền nội bộ đã thực hiện, tự động hoàn nguyên số dư tương ứng tại ví nguồn và ví đích, hoàn trả khoản phí (nếu có) và cập nhật trạng thái lệnh thành `CANCELLED`. |
| Điều kiện kích hoạt | Người dùng chọn chức năng hủy tại một dòng chuyển tiền trong bảng lịch sử. |

#### Tiền điều kiện

1. Lệnh chuyển tiền cần hủy tồn tại trong danh sách `transfers` và đang ở trạng thái `COMPLETED`.
2. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Nếu ví đích không đủ số dư để hoàn trả hoặc thao tác hoàn nguyên thất bại, trạng thái của lệnh chuyển tiền và số dư hai ví được giữ nguyên vẹn.

#### Hậu điều kiện thành công

1. Số dư ví nguồn và ví đích được khôi phục chính xác như trước khi chuyển: ví nguồn được cộng trả lại $amount + fee$, ví đích bị trừ $amount$.
2. Bản ghi chi tiêu phí liên kết trong danh sách `transactions` (nếu có) được hoàn nguyên số dư và cập nhật trạng thái `CANCELLED` theo `QD-239`.
3. Lệnh chuyển tiền chuyển sang trạng thái `CANCELLED` trong danh sách `transfers`.

#### Luồng sự kiện chính

1. Tại bảng lịch sử chuyển tiền, người dùng nhấn nút "Hủy giao dịch" tại một lệnh chuyển tiền ở trạng thái `COMPLETED`.
2. Hệ thống hiển thị hộp thoại xác nhận hủy lệnh, liệt kê chi tiết các khoản hoàn nguyên sẽ diễn ra: cộng trả lại $amount$ vào ví nguồn, trừ $amount$ khỏi ví đích, hoàn phí $fee$ vào ví nguồn (nếu có).
3. Người dùng nhấn nút "Xác nhận hủy lệnh".
4. Hệ thống kiểm tra điều kiện hoàn nguyên tại ví đích theo quy tắc `FR-TRF-03`: số dư khả dụng hiện tại của ví đích phải lớn hơn hoặc bằng số tiền đã nhận: $WalletBalance_{to} \ge amount$.
5. Hệ thống hoàn nguyên số dư cho cả hai ví; chỉ khi cả hai thành công, kết quả mới được ghi nhận:
   - Cộng trả lại số tiền $amount$ vào số dư ví nguồn.
   - Trừ số tiền $amount$ khỏi số dư ví đích.
   - Nếu lệnh chuyển tiền có phí liên kết ($fee > 0$), hệ thống cộng hoàn trả $fee$ vào ví nguồn và cập nhật trạng thái bản ghi chi tiêu phí liên kết trong danh sách `transactions` sang `CANCELLED` theo `QD-239`.
   - Cập nhật trạng thái lệnh chuyển tiền trong danh sách `transfers` thành `CANCELLED`.
6. Hệ thống lưu tệp dữ liệu, hiển thị thông báo hủy chuyển tiền thành công và làm mới số dư hai ví trên giao diện.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Ví đích không đủ số dư để hoàn nguyên
1. Tại bước 4 của luồng chính, hệ thống phát hiện số dư khả dụng hiện tại của ví đích nhỏ hơn số tiền chuyển cần đòi lại ($WalletBalance_{to} < amount$) do ví đích đã sử dụng tiền này trong các giao dịch chi tiêu tiếp theo.
2. Hệ thống từ chối thao tác hủy và hiển thị thông báo lỗi: "Không thể hủy giao dịch chuyển tiền này vì ví đích không còn đủ số dư để hoàn trả".
3. Luồng kết thúc, trạng thái hệ thống được bảo toàn nguyên trạng.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc ghi hai vế theo `BR-FIN-03`: Hoàn nguyên chuyển tiền phải khôi phục chính xác cả hai vế tại ví nguồn và ví đích; nếu một ví thất bại thì cả hai đều không thay đổi.
2. Yêu cầu chức năng liên quan: `FR-TRF-03`.

---

### 6.12 UC-CAT-01: Bổ sung danh mục phân loại mới

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-CAT-01 |
| Tên | Bổ sung danh mục phân loại mới |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần mở rộng cây danh mục để theo dõi chi tiết các khoản chi tiêu hoặc nguồn thu đặc thù. Hệ thống: Giữ cấu trúc phân cấp 3 cấp theo `BR-FIN-04`. Danh mục không được thiếu nhóm cha hoặc trùng tên trong cùng nhóm. |
| Mô tả tóm tắt | Bổ sung một danh mục chi tiết (Cấp 3) vào một Nhóm nhu cầu đang dùng (Cấp 2), hoặc một Nhóm nhu cầu mới (Cấp 2) vào một Loại thu chi (Cấp 1). Mục mới ở trạng thái đang dùng và danh sách chọn của biểu mẫu giao dịch được cập nhật. |
| Điều kiện kích hoạt | Người dùng chọn chức năng thêm danh mục trên màn hình quản lý cây danh mục. |

#### Tiền điều kiện

1. Cây danh mục phân loại đã được đọc vào hệ thống (gồm 2 loại thu chi gốc và các nhóm hiện có, lần khởi tạo đầu tiên là 7 nhóm mẫu).
2. Người dùng đang ở màn hình Quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu thao tác thêm gặp lỗi kiểm tra dữ liệu hoặc lỗi ghi tệp, cây danh mục giữ nguyên trạng thái cũ, không sinh ra bản ghi rác.

#### Hậu điều kiện thành công

1. Danh mục mới được thêm vào đúng Nhóm nhu cầu cha, kế thừa thuộc tính Loại thu chi của nhóm cha và được lưu vào tệp dữ liệu ở trạng thái đang dùng.
2. Cây danh mục 3 cấp giữ nguyên cấu trúc: mọi danh mục đều thuộc một nhóm cha, không trùng tên trong cùng một nhóm cha theo `BR-FIN-04`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Quản lý danh mục thu chi".
2. Hệ thống hiển thị toàn bộ cây danh mục phân loại 3 cấp: Cấp 1 gồm hai nhánh gốc `Income` và `Expense`; Cấp 2 gồm các Nhóm nhu cầu; Cấp 3 gồm các Danh mục chi tiết trực thuộc từng nhóm.
3. Người dùng chọn một Nhóm nhu cầu cụ thể (ví dụ: nhóm "Nhu cầu thiết yếu" thuộc loại Chi tiêu (`Expense`)) và nhấn nút "Thêm danh mục con".
4. Hệ thống hiển thị biểu mẫu tạo danh mục gồm: Nhóm cha (chỉ đọc), Tên danh mục mới, Biểu tượng và màu sắc nhận diện (tùy chọn).
5. Người dùng nhập tên danh mục mới, chọn biểu tượng và nhấn nút "Lưu danh mục".
6. Hệ thống kiểm tra dữ liệu:
   - Tên danh mục không được để trống sau khi cắt khoảng trắng thừa, độ dài từ 1 đến 50 ký tự.
   - Tên danh mục không trùng lặp với các danh mục đang dùng khác trong cùng Nhóm cha (so sánh không phân biệt hoa thường). Danh mục đã xóa không tính.
   - Kiểm tra mã nhóm cha tồn tại và đang dùng.
7. Hệ thống tạo bản ghi mới cho Danh mục trong tệp dữ liệu với mã duy nhất, đặt mã nhóm làm mã liên kết tới nhóm cha, và đặt trạng thái đang dùng.
8. Hệ thống làm mới cây danh mục hiển thị trên giao diện, tự động mở rộng nhánh nhóm cha để làm nổi bật danh mục vừa tạo.
9. Hệ thống cập nhật danh sách chọn tại các biểu mẫu giao dịch để sẵn sàng sử dụng danh mục mới.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Thêm Nhóm nhu cầu mới
1. Tại bước 3 của luồng chính, người dùng chọn một Loại thu chi (`Income` hoặc `Expense`) và nhấn nút "Thêm nhóm".
2. Hệ thống hiển thị biểu mẫu tạo nhóm gồm: Loại thu chi (chỉ đọc), Tên nhóm mới, Biểu tượng và màu sắc nhận diện (tùy chọn).
3. Người dùng nhập tên nhóm và nhấn nút "Lưu nhóm". Hệ thống kiểm tra tên từ 1 đến 50 ký tự, không trùng với nhóm đang dùng khác trong cùng Loại thu chi (nhóm đã xóa không tính).
4. Hệ thống tạo bản ghi nhóm với mã duy nhất, ở trạng thái đang dùng. Luồng tiếp tục tại bước 8 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 6a: Trùng tên danh mục trong cùng một nhóm
1. Tại bước 6 của luồng chính, hệ thống phát hiện tên danh mục nhập vào trùng với tên của một danh mục đang dùng khác trong cùng Nhóm cha.
2. Hệ thống từ chối lưu, đánh dấu trường tên danh mục và hiển thị thông báo lỗi: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác".
3. Luồng quay lại bước 5 của luồng chính để người dùng hiệu chỉnh tên danh mục.

##### Luồng ngoại lệ 6b: Nhóm cha không tồn tại, đã xóa hoặc bị lỗi liên kết
1. Tại bước 6 của luồng chính, hệ thống thấy mã nhóm cha không tồn tại hoặc đã xóa.
2. Hệ thống từ chối tạo và thông báo lỗi cấu trúc: "Không tìm thấy nhóm nhu cầu cha hợp lệ".
3. Luồng kết thúc, hủy bỏ thao tác.

##### Luồng ngoại lệ 6c: Tên trùng với mục đã xóa
1. Tại bước 6 của luồng chính (hoặc bước 3 của luồng rẽ nhánh 3a), hệ thống phát hiện tên nhập vào trùng với một danh mục hoặc nhóm đã xóa trong cùng phạm vi.
2. Hệ thống hỏi người dùng chọn Khôi phục mục cũ (giữ mã cũ và nối lại giao dịch cũ) hoặc Tạo mới (mã mới).
3. Nếu chọn Khôi phục, luồng chuyển sang ca sử dụng `UC-CAT-03`. Nếu chọn Tạo mới, luồng tiếp tục tạo bản ghi với mã mới tại bước 7 của luồng chính (danh mục) hoặc bước 4 của luồng rẽ nhánh 3a (nhóm).

##### Luồng ngoại lệ 6d: Trùng tên nhóm trong cùng một Loại thu chi
1. Tại bước 3 của luồng rẽ nhánh 3a, hệ thống phát hiện tên nhóm mới trùng với tên của một nhóm đang dùng khác trong cùng Loại thu chi. Nhóm đã xóa không tính.
2. Hệ thống từ chối lưu, đánh dấu trường tên nhóm và hiển thị thông báo lỗi: "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác".
3. Luồng quay lại bước 3 của luồng rẽ nhánh 3a để người dùng nhập lại tên nhóm.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc cấu trúc 3 cấp theo `BR-FIN-04`: Bảo đảm cây phân cấp loại thu chi -> nhóm -> danh mục luôn đầy đủ, nghiêm cấm tồn tại nút thiếu nhóm cha hoặc gán sai tầng bậc.
2. Yêu cầu chức năng liên quan: `FR-CAT-01`, `FR-CAT-02`.

---

### 6.13 UC-CAT-02: Đổi tên danh mục

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-CAT-02 |
| Tên | Đổi tên danh mục |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần chỉnh sửa tên hiển thị của danh mục cho rõ ràng hơn. Hệ thống: Giữ nguyên toàn bộ liên kết dữ liệu với các giao dịch trong quá khứ; các giao dịch lịch sử tự động hiển thị tên mới mà không làm thay đổi dữ liệu tiền đã ghi. |
| Mô tả tóm tắt | Đổi tên hiển thị của một danh mục chi tiết hoặc một Nhóm nhu cầu đang dùng trong cây phân cấp. Toàn bộ các giao dịch quá khứ liên kết với mục này hiển thị theo tên mới và không bị mất liên kết. Ca này cũng cho chuyển một danh mục sang nhóm khác cùng Loại thu chi. |
| Điều kiện kích hoạt | Người dùng chọn chức năng đổi tên danh mục hoặc nhóm trên cây phân cấp. |

#### Tiền điều kiện

1. Danh mục hoặc nhóm cần đổi tên tồn tại và đang dùng trong cây phân cấp.
2. Người dùng đang ở màn hình Quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu thao tác đổi tên gặp lỗi kiểm tra dữ liệu hoặc người dùng hủy bỏ, tên danh mục và toàn bộ liên kết lịch sử giữ nguyên trạng.

#### Hậu điều kiện thành công

1. Tên hiển thị mới của danh mục hoặc nhóm được cập nhật vào tệp dữ liệu. Với luồng chuyển nhóm, danh mục thuộc nhóm đích và giữ nguyên mã cùng giao dịch liên kết.
2. Toàn bộ các giao dịch trong quá khứ liên kết với mã danh mục này tiếp tục giữ nguyên liên kết dữ liệu và hiển thị theo tên mới.

#### Luồng sự kiện chính

1. Người dùng chọn một danh mục hoặc một Nhóm nhu cầu trên cây phân cấp và nhấn nút "Đổi tên".
2. Hệ thống hiển thị hộp thoại chỉnh sửa tên, điền sẵn tên hiển thị hiện tại.
3. Người dùng nhập tên mới và nhấn nút "Lưu thay đổi".
4. Hệ thống kiểm tra dữ liệu:
   - Tên mới không được để trống sau khi cắt khoảng trắng thừa, độ dài từ 1 đến 50 ký tự.
   - Tên mới không trùng lặp với các mục đang dùng khác cùng cấp: danh mục trong cùng Nhóm cha, hoặc nhóm trong cùng Loại thu chi (so sánh không phân biệt hoa thường). Mục đã xóa không tính.
5. Hệ thống cập nhật trường tên của danh mục hoặc nhóm trong tệp dữ liệu. Với danh mục, toàn bộ các giao dịch lịch sử liên kết với mã danh mục này tự động phản ánh tên mới qua mã liên kết tới danh mục. Với nhóm, các danh mục con giữ nguyên mã và liên kết giao dịch.
6. Hệ thống hiển thị thông báo thành công (đổi tên hoặc chuyển nhóm) và làm mới cây danh mục trên giao diện.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Chuyển danh mục sang nhóm khác
1. Tại bước 1 của luồng chính, người dùng chọn một danh mục chi tiết và nhấn nút "Chuyển nhóm".
2. Hệ thống hiển thị danh sách các nhóm đang dùng cùng Loại thu chi với danh mục, đánh dấu nhóm hiện tại.
3. Người dùng chọn nhóm đích. Hệ thống kiểm tra nhóm đích đang dùng, cùng Loại thu chi, và tên danh mục không trùng danh mục đang dùng trong nhóm đích.
4. Hệ thống hiển thị hộp xác nhận nêu số giao dịch của danh mục, nhóm cũ, nhóm mới, và cảnh báo báo cáo theo nhóm của các kỳ đã qua sẽ tính số tiền này vào nhóm mới. Tổng thu và tổng chi không đổi.
5. Người dùng nhấn nút "Xác nhận chuyển". Hệ thống cập nhật nhóm cha của danh mục, giữ nguyên mã và mọi giao dịch liên kết.
6. Luồng tiếp tục tại bước 6 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Trùng tên danh mục trong cùng một nhóm
1. Tại bước 4 của luồng chính, hệ thống phát hiện tên mới trùng với tên của một danh mục đang dùng khác trong cùng Nhóm cha.
2. Hệ thống từ chối lưu, đánh dấu trường tên và hiển thị thông báo lỗi: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác".
3. Luồng quay lại bước 3 của luồng chính để người dùng nhập lại tên hợp lệ.

##### Luồng ngoại lệ 4b: Trùng tên nhóm trong cùng một Loại thu chi
1. Tại bước 4 của luồng chính, hệ thống phát hiện tên mới của nhóm trùng với tên của một nhóm đang dùng khác trong cùng Loại thu chi. Nhóm đã xóa không tính.
2. Hệ thống từ chối lưu, đánh dấu trường tên và hiển thị thông báo lỗi: "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác".
3. Luồng quay lại bước 3 của luồng chính để người dùng nhập lại tên hợp lệ.

##### Luồng ngoại lệ 1b: Nhóm đích không hợp lệ
1. Tại bước 3 của luồng rẽ nhánh 1a, nhóm đích đã xóa.
2. Hệ thống từ chối theo `ERR-CAT-003`, hiển thị thông báo "Không thể chuyển danh mục sang nhóm đã xóa." và giữ nguyên danh mục ở nhóm cũ.
3. Luồng quay lại bước 3 của luồng rẽ nhánh 1a để người dùng chọn nhóm đích khác.

##### Luồng ngoại lệ 1c: Trùng tên trong nhóm đích
1. Tại bước 3 của luồng rẽ nhánh 1a, nhóm đích đã có danh mục đang dùng cùng tên.
2. Hệ thống từ chối chuyển và hiển thị thông báo: "Nhóm đích đã có danh mục cùng tên. Hãy đổi tên danh mục trước khi chuyển".
3. Luồng quay lại bước 3 của luồng rẽ nhánh 1a để người dùng chọn nhóm đích khác.

##### Luồng ngoại lệ 1d: Chuyển danh mục sang nhóm khác Loại thu chi
1. Tại bước 3 của luồng rẽ nhánh 1a, hệ thống nhận yêu cầu chuyển danh mục sang nhóm đích thuộc Loại thu chi khác với danh mục. Yêu cầu này không đi qua danh sách nhóm ở bước 2.
2. Hệ thống từ chối theo `ERR-CAT-001` và hiển thị thông báo: "Không được phép đổi loại thu chi hoặc chuyển danh mục sang nhóm thuộc loại thu chi khác."
3. Luồng quay lại bước 3 của luồng rẽ nhánh 1a để người dùng chọn nhóm đích khác.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc giữ lịch sử giao dịch: Việc đổi tên danh mục không được phá vỡ liên kết dữ liệu với các giao dịch lịch sử.
2. Quy tắc giữ nguyên Loại theo `BR-FIN-04`: Cấm chuyển danh mục sang Loại thu chi khác, và cấm đổi Loại của nhóm hoặc danh mục đã có giao dịch để tránh làm sai lệch báo cáo dòng tiền quá khứ.
3. Quy tắc chuyển nhóm: Báo cáo luôn gom theo nhóm hiện tại của danh mục, không lưu nhóm tại thời điểm ghi giao dịch. Nhóm nhu cầu không chuyển được vì cha của nhóm là Loại thu chi, mà Loại thu chi không đổi.
4. Yêu cầu chức năng liên quan: `FR-CAT-02`, `FR-CAT-03`.

---

### 6.14 UC-CAT-03: Khôi phục hoặc tạo lại mục đã xóa

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-CAT-03 |
| Tên | Khôi phục hoặc tạo lại mục đã xóa |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần lấy lại một nhóm hoặc danh mục đã xóa nhầm hoặc cần dùng lại, và chọn giữa nối lại lịch sử cũ hoặc bắt đầu mới. Hệ thống: Bảo đảm việc khôi phục không làm mất hay đổi dữ liệu lịch sử, và không để hai mục đang dùng trùng tên trong cùng phạm vi. |
| Mô tả tóm tắt | Từ màn hình "Danh mục đã xóa", người dùng chọn một nhóm hoặc danh mục đã xóa rồi Khôi phục (giữ mã cũ, nối lại giao dịch cũ) hoặc Tạo mới (mã mới, không nối giao dịch cũ). |
| Điều kiện kích hoạt | Người dùng mở màn hình "Danh mục đã xóa", hoặc chọn Khôi phục tại hộp hỏi khi tên mới trùng một mục đã xóa (`UC-CAT-01`). |

#### Tiền điều kiện

1. Có ít nhất một nhóm hoặc danh mục đang ở trạng thái đã xóa.
2. Người dùng đang ở màn hình Quản lý danh mục thu chi, hoặc đang ở hộp hỏi Khôi phục hoặc Tạo mới của `UC-CAT-01`.

#### Bảo đảm tối thiểu

Nếu thao tác bị từ chối hoặc gặp lỗi ghi dữ liệu, mục đã xóa giữ nguyên trạng thái và toàn bộ giao dịch lịch sử không thay đổi.

#### Hậu điều kiện thành công

1. Với Khôi phục: mục trở lại trạng thái đang dùng với mã cũ, các giao dịch cũ gắn với mục đó được nối lại, mục xuất hiện lại trong danh sách chọn.
2. Với Tạo mới: một mục mới đang dùng được tạo với mã mới. Mục cũ vẫn đã xóa và giao dịch cũ vẫn gắn với mục cũ.
3. Các báo cáo lịch sử không bị đứt đoạn trong cả hai trường hợp.

#### Luồng sự kiện chính

1. Người dùng mở màn hình "Danh mục đã xóa".
2. Hệ thống liệt kê các nhóm và danh mục đã xóa, kèm tên, nơi chứa cũ và số giao dịch còn gắn với từng mục.
3. Người dùng chọn một mục và nhấn nút "Khôi phục".
4. Hệ thống kiểm tra: không có mục đang dùng nào trùng tên trong cùng phạm vi (danh mục trong cùng nhóm, nhóm trong cùng Loại thu chi), và với danh mục thì nhóm cha đang dùng.
5. Hệ thống chuyển mục sang trạng thái đang dùng với mã cũ, nối lại giao dịch cũ và đưa mục trở lại danh sách chọn khi ghi giao dịch mới.
6. Hệ thống làm mới cây danh mục và hiển thị thông báo khôi phục thành công.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Vào từ hộp hỏi của UC-CAT-01
1. Tại luồng ngoại lệ 6c của `UC-CAT-01`, người dùng chọn Khôi phục.
2. Hệ thống lấy mục đã xóa trùng tên trong cùng phạm vi làm mục cần khôi phục và bỏ qua các bước 1 đến 3 của luồng chính.
3. Luồng tiếp tục tại bước 4 của luồng chính.

##### Luồng rẽ nhánh 3a: Tạo mới thay vì khôi phục
1. Tại bước 3 của luồng chính, người dùng nhấn nút "Tạo mới".
2. Hệ thống yêu cầu nhập tên (điền sẵn tên cũ) và nơi chứa, rồi kiểm tra tên không trùng mục đang dùng trong cùng phạm vi (danh mục trong cùng nhóm, nhóm trong cùng Loại thu chi). Hệ thống không hỏi lại Khôi phục hoặc Tạo mới.
3. Hệ thống tạo mục mới với mã mới ở trạng thái đang dùng. Mục cũ giữ nguyên trạng thái đã xóa.
4. Luồng tiếp tục tại bước 6 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3b: Tên hoặc nhóm cha không hợp lệ khi tạo mới
1. Tại bước 2 của luồng rẽ nhánh 3a, hệ thống phát hiện tên nhập vào trùng với mục đang dùng trong cùng phạm vi, hoặc nhóm cha của danh mục đang ở trạng thái đã xóa.
2. Hệ thống từ chối tạo và hiển thị thông báo theo trường hợp: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác" (danh mục trùng tên), "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác" (nhóm trùng tên), hoặc "Không tìm thấy nhóm nhu cầu cha hợp lệ" (nhóm cha đã xóa).
3. Luồng quay lại bước 2 của luồng rẽ nhánh 3a để người dùng nhập lại tên hoặc chọn nơi chứa khác.

##### Luồng ngoại lệ 4a: Trùng tên với mục đang dùng
1. Tại bước 4 của luồng chính, hệ thống phát hiện đã có mục đang dùng cùng tên trong cùng phạm vi.
2. Hệ thống từ chối theo `ERR-CAT-004` và hiển thị thông báo: "Không thể khôi phục vì tên đã được dùng. Hãy đổi tên một trong hai mục rồi thử lại".
3. Luồng kết thúc, mục giữ nguyên trạng thái đã xóa.

##### Luồng ngoại lệ 4b: Nhóm cha vẫn ở trạng thái đã xóa
1. Tại bước 4 của luồng chính, người dùng khôi phục một danh mục mà nhóm cha của nó còn ở trạng thái đã xóa.
2. Hệ thống từ chối theo `ERR-CAT-004` và hiển thị thông báo: "Nhóm chứa danh mục này đang ở trạng thái đã xóa. Hãy khôi phục nhóm trước".
3. Luồng kết thúc, danh mục giữ nguyên trạng thái đã xóa.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc giữ liên kết dữ liệu theo `BR-FIN-04`: Khôi phục giữ nguyên mã cũ nên không tạo dữ liệu mồ côi. Mã định danh không bao giờ dùng lại cho mục khác.
2. Khôi phục nhóm chỉ khôi phục riêng nhóm. Các danh mục con của nhóm khôi phục riêng từng cái.
3. Yêu cầu chức năng liên quan: `FR-CAT-02`, `FR-CAT-03`.

---

### 6.15 UC-CAT-04: Xóa danh mục hoặc nhóm

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-CAT-04 |
| Tên | Xóa danh mục hoặc nhóm |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần bỏ các danh mục hoặc nhóm không còn dùng bằng một thao tác duy nhất, kể cả khi đã có giao dịch, mà không mất lịch sử. Hệ thống: Không để giao dịch trỏ tới danh mục không còn tồn tại, và không để một Loại thu chi mất hết nhóm. |
| Mô tả tóm tắt | Người dùng chọn Xóa một danh mục hoặc một nhóm. Mục chưa có giao dịch bị xóa hẳn. Mục đã có giao dịch chuyển sang trạng thái đã xóa, giao dịch được giữ nguyên. Xóa nhóm kéo theo mọi danh mục con. |
| Điều kiện kích hoạt | Người dùng chọn chức năng xóa trên một danh mục hoặc nhóm trong cây phân cấp. |

#### Tiền điều kiện

1. Danh mục hoặc nhóm cần xóa tồn tại và đang dùng trong cây phân cấp.
2. Người dùng đang ở màn hình Quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu thao tác bị từ chối, bị hủy hoặc gặp lỗi ghi dữ liệu, toàn bộ cây phân loại và mọi giao dịch được giữ nguyên.

#### Hậu điều kiện thành công

1. Mục chưa có giao dịch bị xóa hoàn toàn khỏi tệp dữ liệu.
2. Mục đã có giao dịch ở trạng thái đã xóa, không còn trong danh sách chọn cho giao dịch mới, vẫn hiện trong báo cáo lịch sử kèm nhãn (đã xóa) và có thể được khôi phục tại `UC-CAT-03`.
3. Khi xóa nhóm: mỗi danh mục con được xử lý theo các điểm trên. Nhóm có giao dịch bên trong thì ở trạng thái đã xóa, nhóm không có thì bị xóa hẳn.

#### Luồng sự kiện chính

1. Người dùng chọn một danh mục hoặc nhóm và nhấn nút "Xóa".
2. Hệ thống quét danh sách `transactions` để đếm giao dịch liên kết tới mục đó. Với nhóm, hệ thống đếm theo từng danh mục con đang dùng.
3. Hệ thống hiển thị hộp xác nhận. Với danh mục: nêu số giao dịch liên quan và kết quả (xóa hẳn nếu chưa có giao dịch, ẩn và giữ giao dịch nếu đã có). Với nhóm: nêu số danh mục sẽ bị ẩn, số danh mục xóa hẳn và tổng số giao dịch được giữ lại.
4. Người dùng nhấn nút "Xác nhận xóa".
5. Hệ thống thực hiện xóa hoặc chuyển sang trạng thái đã xóa theo kết quả đã nêu, rồi cập nhật cây danh mục.
6. Hệ thống hiển thị thông báo thành công. Mục đã xóa có trong màn hình "Danh mục đã xóa".

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Xóa nhóm đang dùng cuối cùng của một Loại thu chi
1. Tại bước 2 của luồng chính, hệ thống phát hiện nhóm được chọn là nhóm đang dùng cuối cùng của Loại thu chi chứa nó.
2. Hệ thống từ chối theo `ERR-CAT-002` và hiển thị thông báo: "Không thể xóa nhóm cuối cùng của loại thu chi này".
3. Luồng kết thúc, cây phân loại giữ nguyên.

##### Luồng ngoại lệ 4a: Người dùng hủy xác nhận
1. Tại bước 4 của luồng chính, người dùng nhấn nút "Hủy".
2. Hệ thống không thay đổi gì và đóng hộp xác nhận.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc giữ liên kết dữ liệu theo `BR-FIN-04`: Mục đã có giao dịch không bao giờ bị xóa vật lý, để không có giao dịch nào mất danh mục.
2. Người dùng chỉ thấy một thao tác "Xóa". Việc xóa hẳn hay ẩn do hệ thống quyết định theo số giao dịch.
3. Yêu cầu chức năng liên quan: `FR-CAT-03`.

---

### 6.16 UC-DEBT-01: Ghi nhận và thu lại khoản cho vay

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-DEBT-01 |
| Tên | Ghi nhận và thu lại khoản cho vay |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần theo dõi chi tiết nợ phải thu của từng người vay, từ khi cho vay tiền đến khi đòi lại nợ từng phần hoặc toàn bộ. Hệ thống: Đồng bộ số dư ví thanh toán, tách biệt nghiệp vụ công nợ khỏi chi tiêu tiêu dùng, và kiểm soát mức trần đòi nợ theo `BR-FIN-09`. |
| Mô tả tóm tắt | Ghi nhận khoản tiền cho một cá nhân vay (loại `LEND`), trừ số dư ví thanh toán và tăng nợ phải thu. Hệ thống cũng theo dõi và ghi nhận các đợt đòi lại nợ (loại `COLLECT`), cộng tiền về ví và giảm dần số tiền còn phải thu theo quy tắc trần thu nợ `BR-FIN-09`. |
| Điều kiện kích hoạt | Người dùng tạo khoản cho vay mới hoặc đòi lại một phần/toàn bộ nợ từ người vay. |

#### Tiền điều kiện

1. Hệ thống có ít nhất một ví tiền ở trạng thái `ACTIVE`.
2. Đối với nghiệp vụ đòi lại nợ, người vay phải đang có số tiền còn phải thu lớn hơn 0 ($Receivable_{person} > 0$).
3. Người dùng đang ở màn hình Quản lý công nợ.

#### Bảo đảm tối thiểu

Nếu giao dịch bị hủy hoặc vi phạm giới hạn số học, số dư ví và số tiền còn phải thu của người vay giữ nguyên trạng.

#### Hậu điều kiện thành công

1. Bản ghi công nợ mới được lưu vào danh sách `debt` với loại nghiệp vụ chính xác (`LEND` hoặc `COLLECT`).
2. Số dư ví liên quan được cập nhật ngay theo quy tắc `BR-FIN-02`.
3. Số tiền còn phải thu của người vay và tổng chỉ số $Receivables$ toàn hệ thống được cập nhật chính xác theo `BR-FIN-09`.
4. Số tiền còn phải thu luôn không âm: số tiền đòi lại không vượt quá số tiền còn phải thu.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Tạo khoản cho vay mới".
2. Hệ thống hiển thị biểu mẫu cho vay gồm: Ngày cho vay (mặc định là ngày hiện tại), Tên người vay, Số tiền cho vay, Ví xuất tiền, Mã đợt vay (tùy chọn), Ghi chú mô tả.
3. Người dùng nhập tên người vay, số tiền cho vay ($amount > 0$), chọn ví nguồn (trạng thái `ACTIVE`), nhập mã đợt và ghi chú.
4. Người dùng nhấn nút "Lưu khoản cho vay".
5. Hệ thống kiểm tra nghiệp vụ:
   - Tên người vay không được để trống sau khi cắt khoảng trắng thừa, độ dài từ 1 đến 100 ký tự.
   - Số tiền cho vay $amount > 0$, đưa về kiểu dữ liệu số thập phân chính xác với 2 chữ số thập phân theo quy tắc `BR-FIN-01`.
   - Kiểm tra số dư ví nguồn theo `BR-FIN-02`: $WalletBalance_{wallet} - amount \ge 0$.
6. Hệ thống ghi nhận nghiệp vụ:
   - Trừ số tiền $amount$ khỏi số dư khả dụng của ví xuất tiền theo quy tắc `BR-FIN-02`.
   - Tăng số tiền còn phải thu của người vay: $Receivable_{person}(t) = Receivable_{person}(t-1) + amount$.
   - Lưu bản ghi mới vào danh sách `debt` với loại `LEND` kèm mã khoản nợ duy nhất.
7. Hệ thống cập nhật tổng nợ phải thu toàn hệ thống ($Receivables$). Nghiệp vụ này đổi tài sản từ tiền mặt trong ví sang quyền đòi nợ, không ghi nhận vào chi tiêu tiêu dùng (`Expense`).
8. Hệ thống lưu tệp dữ liệu, hiển thị thông báo ghi nhận thành công và làm mới bảng theo dõi công nợ.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Thu lại nợ cho vay (`COLLECT`)
1. Tại bước 1 của luồng chính, người dùng chọn một người vay trong danh sách nợ phải thu và nhấn nút "Thu nợ".
2. Hệ thống hiển thị số tiền còn phải thu hiện tại ($Receivable_{person}$) và biểu mẫu thu nợ gồm: Ngày thu, Số tiền thu lại, Ví nhận tiền (danh sách ví `ACTIVE`), Ghi chú.
3. Người dùng nhập số tiền thu lại ($amount > 0$), chọn ví nhận tiền và nhấn nút "Xác nhận thu nợ".
4. Hệ thống kiểm tra giới hạn trần thu nợ theo `BR-FIN-09`: $amount \le Receivable_{person}$.
5. Hệ thống cộng số tiền $amount$ vào số dư khả dụng của ví nhận tiền theo quy tắc `BR-FIN-02`.
6. Hệ thống giảm số tiền còn phải thu của người vay: $Receivable_{person}(t) = Receivable_{person}(t-1) - amount$.
7. Nếu $Receivable_{person} = 0$, hệ thống đánh dấu trạng thái công nợ của người này là hoàn tất (`PAID`).
8. Hệ thống lưu bản ghi loại `COLLECT` vào danh sách `debt`, cập nhật tổng $Receivables$ toàn hệ thống và hiển thị thông báo thành công kèm số tiền còn nợ.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Tên người vay để trống
1. Tại bước 5 của luồng chính, hệ thống phát hiện trường tên người vay để trống hoặc chỉ chứa khoảng trắng.
2. Hệ thống báo lỗi: "Tên người vay không được để trống. Vui lòng nhập tên".
3. Luồng quay lại bước 3 của luồng chính để người dùng nhập tên người vay hợp lệ.

##### Luồng ngoại lệ 5b: Số tiền cho vay không hợp lệ
1. Tại bước 5 của luồng chính (hoặc bước 3 của luồng thu nợ), hệ thống phát hiện $amount \le 0$ hoặc sai định dạng số học.
2. Hệ thống báo lỗi: "Số tiền giao dịch phải là một số dương lớn hơn 0".
3. Luồng quay lại để người dùng nhập lại số tiền hợp lệ.

##### Luồng ngoại lệ 5c: Số dư ví xuất tiền không đủ
1. Tại bước 5 của luồng chính, hệ thống phát hiện số dư khả dụng của ví xuất tiền nhỏ hơn số tiền cho vay ($WalletBalance_{wallet} < amount$).
2. Hệ thống từ chối thực hiện và thông báo lỗi: "Số dư ví nguồn không đủ để thực hiện giao dịch công nợ".
3. Luồng quay lại bước 3 của luồng chính để người dùng chọn ví khác hoặc giảm số tiền.

##### Luồng ngoại lệ 4a: Thu lại nợ vượt quá số tiền còn phải thu
1. Tại bước 4 của luồng rẽ nhánh 1a, hệ thống phát hiện số tiền thu nợ lớn hơn số tiền còn phải thu của người vay ($amount > Receivable_{person}$).
2. Hệ thống chặn giao dịch và thông báo lỗi: "Số tiền thu lại không được vượt quá số tiền gốc còn phải thu của người vay".
3. Luồng quay lại bước 3 của luồng rẽ nhánh 1a để người dùng điều chỉnh số tiền.

#### Quy tắc nghiệp vụ đặc thù

1. Số tiền còn phải thu luôn không âm theo `BR-FIN-09`: $Receivables \ge 0$ toàn hệ thống và $Receivable_{person} \ge 0$ từng cá nhân.
2. Quy tắc dòng tiền ví theo `BR-FIN-02`: Nghiệp vụ `LEND` và `COLLECT` tác động trực tiếp đến số dư ví thanh toán.
3. Quy tắc tách khỏi thu chi tiêu dùng: Cho vay và thu nợ không phải là chi tiêu hoặc thu nhập, không tính vào báo cáo kết quả hoạt động tiêu dùng hàng tháng.
4. Yêu cầu chức năng liên quan: `FR-DEBT-01`, `FR-DEBT-03`.

---

### 6.17 UC-DEBT-02: Ghi nhận và trả khoản đi vay

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-DEBT-02 |
| Tên | Ghi nhận và trả khoản đi vay |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần theo dõi chi tiết nợ phải trả đối với từng người cho vay, từ khi nhận tiền vay đến khi hoàn trả từng phần hoặc toàn bộ. Hệ thống: Đồng bộ số dư ví thanh toán, tách biệt nghiệp vụ vay nợ khỏi thu nhập tiêu dùng, và kiểm soát mức trần trả nợ theo `BR-FIN-09`. |
| Mô tả tóm tắt | Ghi nhận khoản tiền nhận vay từ một cá nhân/tổ chức (loại `BORROW`), cộng tiền vào ví thanh toán và tăng nợ phải trả; theo dõi và hoàn trả dần khoản nợ (loại `REPAY`), trừ tiền từ ví và giảm số tiền còn phải trả theo quy tắc trần trả nợ `BR-FIN-09`. |
| Điều kiện kích hoạt | Người dùng tạo khoản đi vay mới hoặc hoàn trả một phần/toàn bộ nợ cho người cho vay. |

#### Tiền điều kiện

1. Hệ thống có ít nhất một ví tiền ở trạng thái `ACTIVE`.
2. Đối với nghiệp vụ trả nợ, người cho vay phải đang có số tiền còn phải trả lớn hơn 0 ($Payable_{person} > 0$).
3. Người dùng đang ở màn hình Quản lý công nợ.

#### Bảo đảm tối thiểu

Nếu giao dịch bị hủy hoặc vi phạm giới hạn số học, số dư ví và nợ phải trả giữ nguyên trạng.

#### Hậu điều kiện thành công

1. Bản ghi công nợ mới được lưu vào danh sách `debt` với loại nghiệp vụ chính xác (`BORROW` hoặc `REPAY`).
2. Số dư ví liên quan được cập nhật ngay theo quy tắc `BR-FIN-02`.
3. Nợ phải trả đối với người cho vay và tổng chỉ số $Payables$ toàn hệ thống được cập nhật chính xác theo `BR-FIN-09`.
4. Số tiền còn phải trả luôn không âm: số tiền hoàn trả không vượt quá số tiền còn phải trả.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Ghi nhận khoản đi vay".
2. Hệ thống hiển thị biểu mẫu đi vay gồm: Ngày vay (mặc định là ngày hiện tại), Tên người cho vay, Số tiền vay, Ví nhận tiền, Mã đợt vay (tùy chọn), Ghi chú mô tả.
3. Người dùng nhập tên người cho vay, số tiền vay ($amount > 0$), chọn ví nhận tiền (trạng thái `ACTIVE`), nhập mã đợt và ghi chú.
4. Người dùng nhấn nút "Lưu khoản đi vay".
5. Hệ thống kiểm tra nghiệp vụ:
   - Tên người cho vay không được để trống sau khi cắt khoảng trắng thừa, độ dài từ 1 đến 100 ký tự.
   - Số tiền vay $amount > 0$, đưa về kiểu dữ liệu số thập phân chính xác với 2 chữ số thập phân theo quy tắc `BR-FIN-01`.
6. Hệ thống ghi nhận nghiệp vụ:
   - Cộng số tiền $amount$ vào số dư khả dụng của ví nhận tiền theo quy tắc `BR-FIN-02`.
   - Tăng nợ phải trả đối với người cho vay: $Payable_{person}(t) = Payable_{person}(t-1) + amount$.
   - Lưu bản ghi mới vào danh sách `debt` với loại `BORROW` kèm mã khoản nợ duy nhất.
7. Hệ thống cập nhật tổng nợ phải trả toàn hệ thống ($Payables$). Nghiệp vụ này tăng tiền trong ví và tăng nợ phải trả tương ứng, không ghi nhận vào thu nhập tiêu dùng (`Income`).
8. Hệ thống lưu tệp dữ liệu, hiển thị thông báo ghi nhận thành công và làm mới giao diện theo dõi công nợ.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Trả nợ đi vay (`REPAY`)
1. Tại bước 1 của luồng chính, người dùng chọn một người cho vay trong danh sách nợ phải trả và nhấn nút "Trả nợ".
2. Hệ thống hiển thị số tiền còn phải trả hiện tại ($Payable_{person}$) và biểu mẫu trả nợ gồm: Ngày trả, Số tiền trả, Ví nguồn (danh sách ví `ACTIVE`), Ghi chú.
3. Người dùng nhập số tiền hoàn trả ($amount > 0$), chọn ví nguồn và nhấn nút "Xác nhận trả nợ".
4. Hệ thống kiểm tra giới hạn trần trả nợ theo `BR-FIN-09` ($amount \le Payable_{person}$) và kiểm tra số dư ví nguồn theo `BR-FIN-02` ($WalletBalance_{wallet} - amount \ge 0$).
5. Hệ thống trừ số tiền $amount$ khỏi số dư khả dụng của ví nguồn theo quy tắc `BR-FIN-02`.
6. Hệ thống giảm nợ phải trả đối với người cho vay: $Payable_{person}(t) = Payable_{person}(t-1) - amount$.
7. Nếu $Payable_{person} = 0$, hệ thống đánh dấu trạng thái nợ phải trả của người cho vay này là hoàn tất (`PAID`).
8. Hệ thống lưu bản ghi loại `REPAY` vào danh sách `debt`, cập nhật tổng $Payables$ toàn hệ thống và hiển thị thông báo thành công kèm số tiền còn nợ.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Tên người cho vay để trống
1. Tại bước 5 của luồng chính, hệ thống phát hiện trường tên người cho vay để trống hoặc chỉ chứa khoảng trắng.
2. Hệ thống báo lỗi: "Tên người cho vay không được để trống. Vui lòng nhập tên".
3. Luồng quay lại bước 3 của luồng chính để người dùng nhập tên người cho vay hợp lệ.

##### Luồng ngoại lệ 5b: Số tiền vay không hợp lệ
1. Tại bước 5 của luồng chính (hoặc bước 3 của luồng trả nợ), hệ thống phát hiện $amount \le 0$ hoặc sai định dạng số học.
2. Hệ thống báo lỗi: "Số tiền giao dịch phải là một số dương lớn hơn 0".
3. Luồng quay lại để người dùng nhập lại số tiền hợp lệ.

##### Luồng ngoại lệ 4a: Số dư ví nguồn không đủ khi trả nợ
1. Tại bước 4 của luồng rẽ nhánh 1a, hệ thống phát hiện số dư khả dụng của ví nguồn nhỏ hơn số tiền trả nợ ($WalletBalance_{wallet} < amount$).
2. Hệ thống từ chối thực hiện và hiển thị thông báo lỗi: "Số dư ví nguồn không đủ để thực hiện giao dịch công nợ".
3. Luồng quay lại bước 3 của luồng rẽ nhánh 1a để người dùng chọn ví khác hoặc giảm số tiền trả.

##### Luồng ngoại lệ 4b: Trả nợ vượt quá số tiền còn phải trả
1. Tại bước 4 của luồng rẽ nhánh 1a, hệ thống phát hiện số tiền trả nợ lớn hơn số tiền còn phải trả ($amount > Payable_{person}$).
2. Hệ thống chặn giao dịch và thông báo lỗi: "Số tiền hoàn trả không được vượt quá số tiền còn phải trả cho người cho vay".
3. Luồng quay lại bước 3 của luồng rẽ nhánh 1a để người dùng điều chỉnh lại số tiền trả.

#### Quy tắc nghiệp vụ đặc thù

1. Số tiền còn phải trả luôn không âm theo `BR-FIN-09`: $Payables \ge 0$ toàn hệ thống và $Payable_{person} \ge 0$ từng cá nhân.
2. Quy tắc dòng tiền ví theo `BR-FIN-02`: Nghiệp vụ `BORROW` và `REPAY` tác động trực tiếp đến số dư ví thanh toán.
3. Quy tắc tách khỏi thu chi tiêu dùng: Đi vay và trả nợ không phải là thu nhập hay chi tiêu tiêu dùng, không tính vào báo cáo kết quả hoạt động tiêu dùng hàng tháng.
4. Yêu cầu chức năng liên quan: `FR-DEBT-02`, `FR-DEBT-04`.

---

### 6.18 UC-DEBT-03: Xóa nợ khó đòi phi tiền mặt

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-DEBT-03 |
| Tên | Xóa nợ khó đòi phi tiền mặt |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần đóng các khoản nợ khó đòi hoặc được miễn trả mà không xáo trộn số dư ví tiền mặt. Hệ thống: Thực hiện nghiệp vụ phi tiền mặt. Số tiền còn nợ được ghi giảm chính xác, trong khi số dư toàn bộ các ví giữ nguyên 100%. |
| Mô tả tóm tắt | Ghi giảm số tiền còn phải thu hoặc còn phải trả khi khoản nợ được xác định không thể đòi lại hoặc được miễn trả. Hệ thống ghi nhận bản ghi nghiệp vụ phi tiền mặt (`WRITE_OFF_LEND` hoặc `WRITE_OFF_BORROW`) và không làm thay đổi số dư của bất kỳ ví tiền nào. |
| Điều kiện kích hoạt | Người dùng chọn chức năng xóa nợ khó đòi tại một khoản nợ trong sổ theo dõi công nợ. |

#### Tiền điều kiện

1. Người vay hoặc người cho vay đang có số tiền còn phải thu hoặc còn phải trả tương ứng lớn hơn 0 ($Receivable_{person} > 0$ hoặc $Payable_{person} > 0$).
2. Người dùng đang ở màn hình Quản lý công nợ.

#### Bảo đảm tối thiểu

Nếu người dùng hủy thao tác hoặc nhập số tiền vượt hạn mức, số tiền còn nợ được giữ nguyên trạng; số dư ví tiền mặt không bị ảnh hưởng.

#### Hậu điều kiện thành công

1. Bản ghi công nợ mới được lưu vào danh sách `debt` với loại `WRITE_OFF_LEND` hoặc `WRITE_OFF_BORROW`.
2. Số tiền còn nợ và chỉ số $Receivables$ hoặc $Payables$ toàn hệ thống được ghi giảm chính xác theo quy tắc `BR-FIN-09`.
3. Nếu số tiền còn nợ của đối tác sau khi ghi giảm về bằng 0.00 VNĐ, trạng thái nợ phải thu hoặc nợ phải trả của đối tác này được cập nhật thành hoàn tất (`PAID`).
4. Số dư của toàn bộ các ví tiền mặt trong hệ thống không bị thay đổi.

#### Luồng sự kiện chính

1. Người dùng chọn một đối tác nợ có khoản nợ khó đòi hoặc được miễn trả và nhấn nút "Xóa nợ".
2. Hệ thống hiển thị biểu mẫu xóa nợ gồm: Người vay hoặc người cho vay, Loại xóa nợ, Số tiền còn nợ, Số tiền xóa nợ, Lý do xóa nợ bắt buộc. Loại xóa nợ là `WRITE_OFF_LEND` nếu là nợ phải thu mất khả năng đòi, hoặc `WRITE_OFF_BORROW` nếu được người cho vay xóa nợ.
3. Người dùng chọn loại hình xóa nợ, nhập số tiền xóa nợ ($amount > 0$), và nhập lý do giải trình bắt buộc vào trường ghi chú.
4. Người dùng nhấn nút "Xác nhận xóa nợ".
5. Hệ thống kiểm tra nghiệp vụ:
   - Số tiền xóa nợ $amount > 0$, đưa về kiểu dữ liệu số thập phân chính xác với 2 chữ số thập phân theo `BR-FIN-01`.
   - Trường lý do xóa nợ không được để trống sau khi cắt khoảng trắng thừa.
   - Kiểm tra trần xóa nợ theo `BR-FIN-09`: $amount \le Receivable_{person}$ (đối với cho vay) hoặc $amount \le Payable_{person}$ (đối với đi vay).
6. Hệ thống ghi nhận nghiệp vụ phi tiền mặt:
   - Ghi giảm số tiền còn nợ tương ứng theo `BR-FIN-09`: trừ $Receivable_{person}$ nếu là `WRITE_OFF_LEND`, trừ $Payable_{person}$ nếu là `WRITE_OFF_BORROW`.
   - Nếu số tiền còn nợ tương ứng bằng 0.00 VNĐ sau khi ghi giảm, đánh dấu trạng thái nợ phải thu (hoặc nợ phải trả) của đối tác này là hoàn tất (`PAID`).
   - Giữ nguyên số dư của toàn bộ các ví tiền mặt theo `BR-FIN-02`.
   - Lưu bản ghi mới vào danh sách `debt` với loại `WRITE_OFF_LEND` hoặc `WRITE_OFF_BORROW` kèm mã khoản nợ duy nhất.
7. Hệ thống cập nhật tổng chỉ số $Receivables$ hoặc $Payables$ toàn hệ thống.
8. Hệ thống lưu tệp dữ liệu, hiển thị thông báo hoàn tất xóa nợ và cập nhật lại giao diện sổ công nợ.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Thiếu lý do giải trình xóa nợ
1. Tại bước 5 của luồng chính, hệ thống phát hiện trường lý do để trống hoặc chỉ chứa khoảng trắng.
2. Hệ thống báo lỗi: "Vui lòng nhập lý do xóa nợ để lưu dấu vết kiểm tra lại".
3. Luồng quay lại bước 3 của luồng chính để người dùng bổ sung lý do.

##### Luồng ngoại lệ 5b: Xóa nợ vượt quá số tiền còn nợ
1. Tại bước 5 của luồng chính, hệ thống phát hiện số tiền xóa nợ lớn hơn số tiền còn nợ ($amount > Receivable_{person}$ hoặc $amount > Payable_{person}$).
2. Hệ thống chặn giao dịch và thông báo lỗi: "Số tiền xóa nợ vượt quá số tiền còn nợ".
3. Luồng quay lại bước 3 của luồng chính để người dùng điều chỉnh lại số tiền xóa nợ.

#### Quy tắc nghiệp vụ đặc thù

1. Số tiền còn nợ luôn không âm theo `BR-FIN-09`: Số tiền xóa nợ không được vượt quá số tiền còn nợ.
2. Quy tắc dòng tiền ví theo `BR-FIN-02`: Nghiệp vụ xóa nợ khó đòi là nghiệp vụ phi tiền mặt, hoàn toàn không làm thay đổi số dư của bất kỳ ví tiền nào.
3. Yêu cầu chức năng liên quan: `FR-DEBT-05`.

---

### 6.19 UC-SAV-01: Mở sổ tiết kiệm mới

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-SAV-01 |
| Tên | Mở sổ tiết kiệm mới |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần lấy vốn nhàn rỗi từ ví thanh toán để gửi tiết kiệm có kỳ hạn hoặc không kỳ hạn, có thể gắn vào quỹ mục tiêu. Hệ thống: Tạo sổ tiết kiệm mới, trừ tiền ví nguồn theo `BR-FIN-02`. Hệ thống quy đổi kỳ hạn sang số ngày và kiểm soát liên kết quỹ theo `BR-FIN-10`. |
| Mô tả tóm tắt | Mở sổ tiết kiệm mới bằng cách trừ vốn gốc từ ví tiền, xác định ngân hàng, kỳ hạn gửi tùy chọn, lãi suất cam kết và ngày đáo hạn; tùy chọn liên kết sổ với một quỹ mục tiêu tài chính độc lập theo quy tắc `BR-FIN-10`. |
| Điều kiện kích hoạt | Người dùng chọn chức năng mở sổ tiết kiệm mới trên màn hình sổ tiết kiệm tích lũy. |

#### Tiền điều kiện

1. Hệ thống có ít nhất một ví tiền ở trạng thái `ACTIVE` có số dư khả dụng đủ để trừ vốn gốc ($WalletBalance_{wallet} \ge principal$).
2. Người dùng có thể chỉ định $TermMonths$ tùy ý (số nguyên dương) làm kỳ hạn gửi, hoặc chọn `unlimited` cho khoản gửi không kỳ hạn, theo `BR-FIN-05`.
3. Người dùng đang ở màn hình Quản lý sổ tiết kiệm.

#### Bảo đảm tối thiểu

Nếu thao tác mở sổ bị hủy hoặc xảy ra lỗi kiểm tra dữ liệu, số dư ví nguồn không bị trừ, không có bản ghi sổ tiết kiệm rác nào được tạo.

#### Hậu điều kiện thành công

1. Bản ghi sổ tiết kiệm mới được lưu vào danh sách `savings` với trạng thái `ACTIVE` và mã sổ tiết kiệm duy nhất.
2. Số dư ví nguồn giảm đúng bằng số vốn gốc $principal$ theo quy tắc `BR-FIN-02`.
3. Nếu có chỉ định quỹ mục tiêu, liên kết quỹ được tạo và tài sản quỹ được cập nhật đồng bộ theo `BR-FIN-10`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Mở sổ tiết kiệm mới".
2. Hệ thống hiển thị biểu mẫu mở sổ gồm các trường: Tên ngân hàng, Số vốn gốc gửi, Kỳ hạn gửi (nhập số tháng $TermMonths$ tùy ý hoặc chọn `unlimited`), Lãi suất cam kết (%/năm), Phương thức tính lãi ($interestMethod$, chọn trong tập hợp {`SIMPLE`, `COMPOUND_MONTHLY`, `PREPAID`, `POSTPAID`}, mặc định `SIMPLE`). Biểu mẫu còn có: Ngày bắt đầu gửi (mặc định là ngày hiện tại), Ví nguồn (danh sách ví `ACTIVE`), Quỹ mục tiêu liên kết (tùy chọn), Ghi chú mô tả.
3. Người dùng nhập tên ngân hàng, số tiền vốn gốc ($principal > 0$), nhập kỳ hạn gửi (ví dụ: $TermMonths = 6$), nhập lãi suất năm (ví dụ: 6.0%/năm), chọn phương thức tính lãi ($interestMethod$), chọn ngày bắt đầu, chọn ví nguồn, và chọn quỹ mục tiêu liên kết (nếu có).
4. Người dùng nhấn nút "Xác nhận mở sổ".
5. Hệ thống kiểm tra nghiệp vụ:
   - Tên ngân hàng không được để trống sau khi cắt khoảng trắng thừa, độ dài từ 1 đến 100 ký tự.
   - Số vốn gốc $principal > 0$ và lãi suất $Rate \ge 0$, đưa về kiểu dữ liệu số thập phân chính xác với 2 chữ số thập phân theo quy tắc `BR-FIN-01`.
   - Kiểm tra $TermMonths \in \mathbb{Z}^{+}$ (hoặc kỳ hạn `unlimited`), phương thức tính lãi $interestMethod$ thuộc {`SIMPLE`, `COMPOUND_MONTHLY`, `PREPAID`, `POSTPAID`} (kỳ hạn `unlimited` bắt buộc `SIMPLE`), và tính $TermDays$ theo công thức quy đổi tại `BR-FIN-05`.
   - Kiểm tra số dư khả dụng của ví nguồn theo quy tắc `BR-FIN-02`: $WalletBalance_{wallet} - principal \ge 0$.
6. Hệ thống trừ tiền ví nguồn: trừ số vốn gốc $principal$ khỏi số dư khả dụng của ví nguồn được chọn.
7. Hệ thống xác định ngày đáo hạn:
   - Đối với khoản gửi có kỳ hạn xác định: $MaturityDate$ được xác định bằng cách cộng $TermMonths$ tháng lịch vào ngày gửi $start$ theo quy tắc ngày đáo hạn tại `BR-FIN-05`; số ngày kỳ hạn $TermDays$ là số ngày lịch thực tế từ $start$ đến $MaturityDate$.
   - Đối với khoản gửi không kỳ hạn (`unlimited`), trường $MaturityDate = null$ và $TermDays = 0$.
8. Hệ thống tạo bản ghi sổ tiết kiệm mới trong danh sách `savings` với mã sổ tiết kiệm duy nhất, lưu các thuộc tính: tên ngân hàng, vốn gốc, kỳ hạn, lãi suất, phương thức tính lãi ($interestMethod$), ngày bắt đầu gửi, ngày đáo hạn, mã ví, quỹ liên kết (nếu có), trạng thái `ACTIVE`, số dư ban đầu $SavingsBalance_s = principal$.
9. Nếu người dùng chỉ định quỹ mục tiêu, hệ thống ghi nhận liên kết sổ tiết kiệm này vào quỹ tương ứng theo quy tắc `BR-FIN-10`, tự động cộng giá trị sổ vào tổng tài sản quỹ.
10. Hệ thống lưu tệp dữ liệu, hiển thị thông báo mở sổ tiết kiệm thành công kèm thông tin chi tiết: mã sổ, ngày đáo hạn dự kiến, lãi dự kiến toàn kỳ và số dư mới của ví nguồn.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Số vốn gốc không hợp lệ
1. Tại bước 5 của luồng chính, hệ thống phát hiện $principal \le 0$ hoặc sai định dạng số học.
2. Hệ thống báo lỗi: "Số tiền vốn gốc gửi tiết kiệm phải là số dương lớn hơn 0".
3. Luồng quay lại bước 3 của luồng chính để người dùng nhập lại số tiền hợp lệ.

##### Luồng ngoại lệ 5b: Số dư ví nguồn không đủ
1. Tại bước 5 của luồng chính, hệ thống phát hiện số dư khả dụng của ví nguồn nhỏ hơn số vốn gốc gửi tiết kiệm ($WalletBalance_{wallet} < principal$).
2. Hệ thống từ chối mở sổ và hiển thị thông báo lỗi: "Số dư khả dụng của ví nguồn không đủ để trừ tiền mở sổ tiết kiệm".
3. Luồng quay lại bước 3 của luồng chính để người dùng chọn ví nguồn khác hoặc giảm số vốn gốc.

##### Luồng ngoại lệ 5c: Lãi suất hoặc kỳ hạn không hợp lệ
1. Tại bước 5 của luồng chính, hệ thống phát hiện lãi suất $Rate < 0$ hoặc $TermMonths$ không phải số nguyên dương (và không phải `unlimited`).
2. Hệ thống từ chối mở sổ và thông báo lỗi: "Lãi suất cam kết và kỳ hạn gửi không hợp lệ. Vui lòng kiểm tra lại".
3. Luồng quay lại bước 3 của luồng chính để người dùng hiệu chỉnh.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc luôn đúng về số dư ví `BR-FIN-02`: Mở sổ tiết kiệm làm giảm số dư ví nguồn đúng bằng số vốn gốc $principal$.
2. Quy tắc liên kết quỹ mục tiêu `BR-FIN-10`: Mỗi sổ tiết kiệm chỉ được liên kết với tối đa một quỹ mục tiêu tại một thời điểm để tránh tính trùng lặp tài sản.
3. Yêu cầu chức năng liên quan: `FR-SAV-01`.

---

### 6.20 UC-SAV-02: Tất toán sổ tiết kiệm đúng hạn

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-SAV-02 |
| Tên | Tất toán sổ tiết kiệm đúng hạn |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần nhận lại vốn gốc và tiền lãi khi sổ tiết kiệm đến hạn hoặc quá hạn, chuyển tiền vào ví chỉ định. Hệ thống: Tính lãi theo `BR-FIN-05`. Hệ thống chuyển trạng thái sổ sang `SETTLED_MATURED`, đóng sổ vĩnh viễn và tự động gỡ liên kết quỹ theo `BR-FIN-10`. |
| Mô tả tóm tắt | Tất toán một sổ tiết kiệm đang hoạt động khi ngày hiện tại đã đến hoặc vượt quá ngày đáo hạn. Hệ thống tính toàn bộ tiền lãi cam kết, cộng tổng tiền ($Payout = Principal$ với phương thức `PREPAID`, hoặc $Payout = Principal + ExpectedInterest$ với các phương thức khác) về ví tiền chỉ định, cập nhật trạng thái sổ thành `SETTLED_MATURED` và gỡ liên kết quỹ mục tiêu. |
| Điều kiện kích hoạt | Ngày hiện tại đã đến hoặc quá ngày đáo hạn ($currentDate \ge MaturityDate$) và người dùng chọn chức năng tất toán đúng hạn. |

#### Tiền điều kiện

1. Sổ tiết kiệm đang ở trạng thái `ACTIVE` và ngày hiện tại đã đến hoặc vượt quá ngày đáo hạn ($currentDate \ge MaturityDate$).
2. Người dùng đang ở màn hình Quản lý sổ tiết kiệm.

#### Bảo đảm tối thiểu

Nếu thao tác tất toán bị hủy bỏ hoặc xảy ra lỗi ghi tệp, trạng thái sổ tiết kiệm giữ nguyên `ACTIVE`, số dư ví nhận tiền không thay đổi.

#### Hậu điều kiện thành công

1. Sổ tiết kiệm chuyển trạng thái `SETTLED_MATURED`, ghi nhận ngày tất toán thực tế là ngày hiện tại, đóng sổ và ngừng tính lãi vĩnh viễn.
2. Ví tiền chỉ định nhận đủ số tiền $Payout$ theo quy tắc `BR-FIN-02` ($Payout = Principal$ với phương thức `PREPAID`, hoặc $Payout = Principal + ExpectedInterest$ với các phương thức khác theo `QD-240`).
3. Liên kết quỹ mục tiêu tài chính (nếu có) được gỡ bỏ và cập nhật đồng bộ theo `BR-FIN-10`.

#### Luồng sự kiện chính

1. Tại danh sách sổ tiết kiệm, người dùng chọn một sổ đang hoạt động có ngày hiện tại đã đến hoặc quá ngày đáo hạn ($currentDate \ge MaturityDate$) và nhấn nút "Tất toán đúng hạn".
2. Hệ thống tính tiền lãi cam kết cả kỳ theo phương thức của sổ tại `BR-FIN-05` và `FR-SAV-02`:
   - Phương thức lãi đơn (`SIMPLE`), lãi trả trước (`PREPAID`), lãi trả sau (`POSTPAID`):
   $$ExpectedInterest = \frac{Principal \times Rate \times TermDays}{365}$$
   - Phương thức lãi kép theo tháng (`COMPOUND_MONTHLY`):
   $$ExpectedInterest = Principal \times \left[\left(1 + \frac{Rate}{12}\right)^m - 1\right]$$
   Phép tính dùng kiểu số thập phân chính xác với 2 chữ số thập phân; tiền lãi tất toán làm tròn nửa lên.
3. Hệ thống xác định tổng tiền thực nhận chuyển về ví theo `QD-240`: đối với phương thức `PREPAID`, do lãi đã chi trả đầu kỳ nên $Payout = Principal$; đối với các phương thức khác, $Payout = Principal + ExpectedInterest$. Hệ thống hiển thị biểu mẫu tất toán gồm: Mã sổ, Ngân hàng, Vốn gốc, Lãi suất, Số ngày gửi thực tế, Tiền lãi cam kết cả kỳ ($ExpectedInterest$), Tổng số tiền thực nhận ($Payout$), và danh sách chọn Ví nhận tiền.
4. Người dùng chọn ví nhận tiền (trạng thái `ACTIVE`) và nhấn nút "Xác nhận tất toán".
5. Hệ thống kiểm tra điều kiện ngày tất toán: bảo đảm $currentDate \ge MaturityDate$.
6. Hệ thống ghi nhận cùng lúc các khoản ghi sau; nếu một khoản ghi thất bại thì không khoản nào có hiệu lực:
   - Cộng số tiền $Payout$ vào số dư khả dụng của ví nhận tiền theo quy tắc `BR-FIN-02`.
   - Cập nhật trạng thái sổ tiết kiệm sang `SETTLED_MATURED`, ghi nhận ngày tất toán là ngày hiện tại và tiền lãi thực nhận bằng lãi dự kiến ($actualInterest = ExpectedInterest$; riêng phương thức `PREPAID`, ghi nhận $actualInterest = 0.00$ VNĐ tại ngày tất toán do tiền lãi đã chi trả đầu kỳ theo `QD-240`).
   - Nếu sổ có liên kết với quỹ mục tiêu, hệ thống tự động gỡ liên kết sổ tiết kiệm khỏi quỹ theo quy tắc `BR-FIN-10` và trừ giá trị sổ khỏi tài sản quỹ tương ứng.
7. Hệ thống lưu tệp dữ liệu, hiển thị thông báo tất toán thành công kèm biên lai tóm tắt số tiền vốn gốc và tiền lãi đã nhập ví.

#### Các luồng rẽ nhánh

Không có. Trường hợp rút vốn trước ngày đáo hạn được đặc tả độc lập tại ca sử dụng `UC-SAV-03`.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 1a: Cố ý tất toán sổ tiết kiệm đã đóng
1. Tại bước 1 của luồng chính, nếu người dùng gửi yêu cầu tất toán đối với một sổ tiết kiệm đã ở trạng thái `SETTLED_MATURED` hoặc `SETTLED_EARLY`.
2. Hệ thống từ chối thực hiện và thông báo lỗi: "Sổ tiết kiệm này đã được tất toán trước đó và không thể thực hiện lại".
3. Luồng kết thúc, không có thay đổi nào được ghi nhận.

##### Luồng ngoại lệ 5a: Tất toán đúng hạn khi sổ chưa đến ngày đáo hạn
1. Tại bước 5 của luồng chính, hệ thống phát hiện ngày hiện tại vẫn trước ngày đáo hạn cam kết ($currentDate < MaturityDate$).
2. Hệ thống từ chối thao tác và thông báo: "Sổ tiết kiệm chưa đến ngày đáo hạn cam kết. Vui lòng sử dụng chức năng Tất toán trước hạn nếu có nhu cầu rút vốn ngay".
3. Luồng kết thúc, trạng thái sổ được giữ nguyên `ACTIVE`.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc tính lãi tiết kiệm `BR-FIN-05`: Lãi suất tính trên cơ sở một năm có 365 ngày; tiền lãi tất toán làm tròn nửa lên tới 2 chữ số thập phân.
2. Quy tắc trạng thái trong vòng đời sổ tiết kiệm: Chuyển dịch trạng thái tuân thủ nghiêm ngặt máy trạng thái từ `ACTIVE` sang `SETTLED_MATURED`. Nghiêm cấm mở lại hoặc hoàn tác sổ đã tất toán.
3. Yêu cầu chức năng liên quan: `FR-SAV-03`.

---

### 6.21 UC-SAV-03: Tất toán sổ tiết kiệm trước hạn

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-SAV-03 |
| Tên | Tất toán sổ tiết kiệm trước hạn |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Rút vốn khỏi sổ tiết kiệm trước ngày đáo hạn, chấp nhận bị phạt tất toán sớm theo `BR-FIN-05`. |
| Mô tả tóm tắt | Tất toán một sổ tiết kiệm đang hoạt động trước ngày đáo hạn, tính lại tiền lãi thực nhận bằng cách áp hệ số phạt lên phần lãi đã cộng dồn theo lãi đơn. Hệ thống chuyển vốn gốc cộng lãi thực nhận về một ví tiền và đóng sổ. |
| Điều kiện kích hoạt | Người dùng cần rút vốn khỏi một sổ tiết kiệm đang hoạt động trước khi đến ngày đáo hạn. |

#### Tiền điều kiện

1. Sổ tiết kiệm đang ở trạng thái `ACTIVE` và ngày hiện tại chưa đến ngày đáo hạn ($currentDate < MaturityDate$).
2. Người dùng đang ở giao diện sổ tiết kiệm tích lũy.

#### Bảo đảm tối thiểu

1. Nếu thao tác tất toán bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, trạng thái sổ tiết kiệm giữ nguyên `ACTIVE`; số dư ví nhận tiền và liên kết quỹ mục tiêu không bị thay đổi.

#### Hậu điều kiện thành công

1. Sổ tiết kiệm chuyển trạng thái `SETTLED_EARLY`, ví tiền nhận $Payout = Principal + ActualInterest$ (lãi đã áp hệ số phạt tất toán sớm), phần chênh lệch lãi kỳ hạn bị hủy bỏ.
2. Liên kết quỹ mục tiêu tài chính (nếu có) được gỡ bỏ và cập nhật đồng bộ theo `BR-FIN-10`.

#### Luồng sự kiện chính

1. Người dùng chọn một sổ tiết kiệm đang hoạt động nhưng chưa đến ngày đáo hạn ($currentDate < MaturityDate$) và yêu cầu tất toán trước hạn.
2. Hệ thống hiển thị hộp thoại cảnh báo tất toán trước hạn kèm biểu mẫu nhập. Biểu mẫu gồm: Ngày tất toán (mặc định hôm nay), Hệ số phạt tất toán sớm, Ví nhận tiền, và ô đánh dấu bắt buộc "Tôi hiểu và chấp nhận mất quyền hưởng lãi suất có kỳ hạn". Hệ số phạt do người dùng chỉ định, không có giá trị mặc định của hệ thống, và phải thỏa $0 < EarlyPenaltyFactor \le 1$.
3. Hệ thống tính và hiển thị bảng đối chiếu tài chính:
   - Số ngày thực gửi: $Days = currentDate - start$.
   - Lãi cam kết ban đầu bị mất:
    $$
    ExpectedInterest = \frac{Principal \times Rate \times TermDays}{365}
    $$
    (Đối với phương thức `COMPOUND_MONTHLY`, $ExpectedInterest = Principal \times \left[\left(1 + \frac{Rate}{12}\right)^m - 1\right]$ theo `BR-FIN-05`).
   - Lãi đã cộng dồn theo lãi đơn trên số ngày thực gửi:
    $$
    AccruedSimpleInterest = \frac{Principal \times Rate \times Days}{365}
    $$
   - Lãi thực nhận sau khi áp hệ số phạt tất toán sớm:
    $$
    ActualInterest = EarlyPenaltyFactor \times AccruedSimpleInterest
    $$
   - Khoản thiệt hại lãi:
    $$
    InterestLoss = \left(ExpectedInterest \times \frac{Days}{TermDays}\right) - ActualInterest
    $$
   - Tổng tiền thực nhận chuyển về ví:
    $$
    Payout = Principal + ActualInterest
    $$
4. Người dùng kiểm tra số liệu, chọn ví nhận tiền, tích chọn ô cam kết chấp nhận mất lãi và nhấn "Xác nhận tất toán trước hạn".
5. Hệ thống cộng số tiền $Payout$ vào số dư khả dụng của ví nhận tiền theo quy tắc `BR-FIN-02`.
6. Hệ thống cập nhật trạng thái sổ tiết kiệm sang `SETTLED_EARLY`, ghi nhận ngày tất toán thực tế, lãi thực nhận, và khoản lãi bị mất do tất toán trước hạn ($lostInterest = InterestLoss$).
7. Hệ thống gỡ liên kết sổ tiết kiệm khỏi quỹ mục tiêu liên quan (nếu có) theo quy tắc `BR-FIN-10`.
8. Hệ thống hiển thị thông báo tất toán trước hạn hoàn tất, ghi nhận vào sổ nhật ký tài chính.

#### Các luồng rẽ nhánh

Không có. Việc tất toán sổ đã đến hoặc quá ngày đáo hạn thuộc phạm vi `UC-SAV-02`.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ: Cố ý tất toán sổ tiết kiệm đã đóng

1. Người dùng chọn thao tác tất toán trên một sổ tiết kiệm đã ở trạng thái `SETTLED_MATURED` hoặc `SETTLED_EARLY`.
2. Hệ thống từ chối thực hiện và thông báo: "Sổ tiết kiệm này đã được tất toán trước đó và không thể thực hiện lại".

##### Luồng ngoại lệ 4a: Chưa xác nhận chấp nhận mất lãi khi tất toán trước hạn

1. Tại bước 4 của luồng chính, Người dùng chưa tích chọn ô xác nhận cam kết chấp nhận mất lãi kỳ hạn.
2. Hệ thống vô hiệu hóa nút xác nhận và hiển thị nhắc nhở yêu cầu người dùng xác nhận điều khoản trước khi tiếp tục.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc tính lãi tiết kiệm `BR-FIN-05`: Lãi suất tính trên cơ sở 365 ngày/năm; tiền lãi tất toán làm tròn nửa lên tới 2 chữ số thập phân.
2. Quy tắc trạng thái trong vòng đời sổ tiết kiệm: Chuyển dịch trạng thái từ `ACTIVE` sang `SETTLED_EARLY`. Không cho phép mở lại sổ đã tất toán.
3. Yêu cầu chức năng liên quan: `FR-SAV-04`.

---

### 6.22 UC-SAV-04: Gửi thêm tiền vào sổ tích lũy

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-SAV-04 |
| Tên | Gửi thêm tiền vào sổ tích lũy |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Tăng vốn gốc của một sổ tiết kiệm tích lũy đang hoạt động bằng cách trừ thêm tiền từ ví tiền. |
| Mô tả tóm tắt | Gửi thêm tiền vào một sổ tiết kiệm tích lũy đang hoạt động, trừ tiền từ ví nguồn và tăng vốn gốc của sổ, cập nhật lại tiến độ kỳ hạn và lãi dự kiến. |
| Điều kiện kích hoạt | Người dùng cần gửi thêm tiền vào một sổ tiết kiệm tích lũy định kỳ đang hoạt động. |

#### Tiền điều kiện

1. Sổ tiết kiệm tích lũy đang ở trạng thái `ACTIVE`.
2. Hệ thống có ít nhất một ví tiền ở trạng thái `ACTIVE` đủ số dư để gửi thêm.
3. Người dùng đang ở giao diện sổ tiết kiệm tích lũy.

#### Bảo đảm tối thiểu

1. Nếu thao tác gửi thêm bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, số dư ví nguồn không bị trừ và vốn gốc của sổ tiết kiệm giữ nguyên trạng thái cũ.

#### Hậu điều kiện thành công

1. Vốn gốc của sổ tiết kiệm tăng đúng bằng số tiền gửi thêm.
2. Số dư ví nguồn giảm đúng bằng số tiền gửi thêm theo `BR-FIN-02`.
3. Tiến độ kỳ hạn và lãi dự kiến của sổ tiết kiệm được cập nhật lại theo `BR-FIN-05`.

#### Luồng sự kiện chính

1. Người dùng chọn một sổ tiết kiệm tích lũy đang hoạt động và chọn chức năng "Gửi thêm tiền vào sổ tích lũy".
2. Hệ thống hiển thị biểu mẫu gửi thêm tiền: Số tiền gửi thêm ($amount > 0$), Ngày nạp (mặc định hôm nay), Ví nguồn (danh sách ví `ACTIVE`), Ghi chú.
3. Người dùng nhập số tiền muốn gửi thêm, chọn ví nguồn và nhấn "Xác nhận gửi thêm tiền".
4. Hệ thống kiểm tra số dư ví nguồn theo quy tắc `BR-FIN-02`: $WalletBalance_{wallet} - amount \ge 0$.
5. Hệ thống trừ số tiền $amount$ khỏi ví nguồn, đồng thời tăng vốn gốc của sổ tiết kiệm: $Principal_{mới} = Principal + amount$.
6. Hệ thống lưu vết giao dịch nạp tích lũy và cập nhật lại tiến độ kỳ hạn, lãi dự kiến của sổ tiết kiệm theo `BR-FIN-05`.
7. Hệ thống hiển thị thông báo gửi thêm tiền thành công và cập nhật số dư vốn gốc mới của sổ tiết kiệm.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3a: Số tiền gửi thêm nhỏ hơn hoặc bằng 0

1. Tại bước 3 của luồng chính, hệ thống phát hiện $amount \le 0$.
2. Hệ thống báo lỗi: "Số tiền gửi thêm vào sổ tiết kiệm phải là số dương lớn hơn 0".
3. Người dùng nhập lại số tiền hợp lệ.

##### Luồng ngoại lệ 4a: Số dư ví nguồn không đủ

1. Tại bước 4 của luồng chính, hệ thống phát hiện số dư khả dụng của ví nguồn nhỏ hơn số tiền muốn gửi thêm ($WalletBalance_{wallet} < amount$).
2. Hệ thống từ chối nạp và hiển thị thông báo lỗi: "Số dư khả dụng của ví nguồn không đủ để gửi thêm vào sổ tiết kiệm".
3. Người dùng chọn ví nguồn khác có đủ tiền hoặc điều chỉnh giảm số tiền gửi thêm.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc luôn đúng về số dư ví `BR-FIN-02`: Gửi thêm tiền làm giảm số dư ví nguồn đúng bằng số tiền gửi thêm.
2. Quy tắc tính lãi tiết kiệm `BR-FIN-05`: Vốn gốc tăng thêm phải được đưa vào công thức tính lãi dự kiến kể từ thời điểm nạp.
3. Yêu cầu chức năng liên quan: `FR-SAV-01`, `FR-SAV-02`.

---

### 6.23 UC-SAV-05: Theo dõi kỳ hạn và tiền lãi dự kiến sổ tiết kiệm

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-SAV-05 |
| Tên | Theo dõi kỳ hạn và tiền lãi dự kiến sổ tiết kiệm |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Nắm tiến độ kỳ hạn và giá trị hiện tại (gồm cả lãi đã cộng dồn) của toàn bộ các sổ tiết kiệm theo thời gian thực. |
| Mô tả tóm tắt | Xem danh sách sổ tiết kiệm kèm tiến độ kỳ hạn, số ngày còn lại và giá trị hiện tại (gồm cả lãi đã cộng dồn), được tính tự động theo thời gian thực. |
| Điều kiện kích hoạt | Người dùng cần tra cứu tiến độ kỳ hạn và giá trị hiện tại của các sổ tiết kiệm đang nắm giữ. |

#### Tiền điều kiện

1. Hệ thống đã có ít nhất một sổ tiết kiệm trong danh sách `savings`.
2. Người dùng đang ở giao diện sổ tiết kiệm tích lũy.

#### Bảo đảm tối thiểu

1. Hệ thống hoạt động ở chế độ chỉ đọc, không làm biến đổi bất kỳ trường dữ liệu nào của các sổ tiết kiệm hay số dư ví trong hệ thống.

#### Hậu điều kiện thành công

1. Hệ thống phản ánh chính xác số ngày gửi thực tế $Days$, tiến độ kỳ hạn $SavingsTermProgress$, lãi dự kiến toàn kỳ $ExpectedInterest$ và giá trị hiện tại $SavingsBalance_s$ theo `BR-FIN-05` cho mọi sổ đang `ACTIVE`.

#### Luồng sự kiện chính

1. Người dùng mở màn hình danh sách sổ tiết kiệm, chọn bộ lọc trạng thái (`ACTIVE`, `SETTLED_MATURED`, `SETTLED_EARLY`, hoặc `ALL`).
2. Với từng sổ tiết kiệm đang ở trạng thái `ACTIVE`, hệ thống lấy ngày hệ thống hiện tại ($currentDate$) và tự động tính theo quy tắc `BR-FIN-05`:
   - Số ngày thực gửi: $Days = \max(0, currentDate - start)$.
   - Nếu là gửi không kỳ hạn (`unlimited` hoặc $TermDays = 0$):
    $$
    SavingsTermProgress = 1.0, \quad Interest_{unlimited} = \frac{Principal \times Rate \times Days}{365}, \quad SavingsBalance_s = Principal + Interest_{unlimited}
    $$
   - Nếu là gửi có kỳ hạn ($TermDays > 0$):
    $$
    SavingsTermProgress = \min\left(1.0, \frac{Days}{TermDays}\right)
    $$
    $$
    ExpectedInterest = \frac{Principal \times Rate \times TermDays}{365}
    $$
    (Đối với phương thức `COMPOUND_MONTHLY`, $ExpectedInterest = Principal \times \left[\left(1 + \frac{Rate}{12}\right)^m - 1\right]$ theo `BR-FIN-05`).
    $$
    SavingsBalance_s = Principal + (ExpectedInterest \times SavingsTermProgress)
    $$
    (Riêng phương thức `PREPAID`, do tiền lãi đã được chi trả đầu kỳ nên $SavingsBalance_s = Principal$).
    $$
    RemainingDays = \max(0, TermDays - Days)
    $$
3. Hệ thống tổng hợp tổng vốn gốc đang gửi, tổng lãi dự kiến và tổng giá trị hiện tại của toàn bộ các sổ tiết kiệm bằng phép cộng. Phép cộng dùng kiểu dữ liệu tiền tệ số thập phân chính xác cố định (2 chữ số thập phân, không sai số dấu phẩy động).
4. Hệ thống hiển thị bảng danh sách sổ tiết kiệm kèm thanh tiến độ thời gian, số ngày còn lại, lãi đã cộng dồn và các nút chức năng thao tác tương ứng.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

Không có luồng ngoại lệ nghiệp vụ đặc thù bổ sung ngoài việc hiển thị danh sách rỗng khi chưa có sổ tiết kiệm nào được mở.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc tính lãi tiết kiệm `BR-FIN-05`: Lãi suất tính trên cơ sở 365 ngày/năm, tiến độ kỳ hạn không vượt quá 1.0.
2. Yêu cầu chức năng liên quan: `FR-SAV-02`.

---

### 6.24 UC-SAV-06: Tái tục sổ tiết kiệm với điều khoản mới

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-SAV-06 |
| Tên | Tái tục sổ tiết kiệm với điều khoản mới |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Tiếp tục gửi tiết kiệm khoản tiền vừa tất toán với kỳ hạn, lãi suất hoặc phương thức lãi mới phù hợp với biểu lãi suất hiện hành, thay vì phải rút tiền ra và mở sổ mới thủ công. |
| Mô tả tóm tắt | Tái tục một phần hoặc toàn bộ số tiền nhận được từ sổ tiết kiệm đã tất toán đúng hạn để tạo một sổ tiết kiệm mới với kỳ hạn, lãi suất và điều khoản độc lập. Sổ mới giữ liên kết nguồn gốc qua mã liên kết tới sổ nguồn tái tục. |
| Điều kiện kích hoạt | Người dùng vừa tất toán đúng hạn một sổ tiết kiệm và muốn gửi tiếp một phần hoặc toàn bộ số tiền nhận được với điều khoản mới. |

#### Tiền điều kiện

1. Sổ tiết kiệm nguồn đã tất toán đúng hạn, đang ở trạng thái `SETTLED_MATURED`.
2. Ví đang giữ khoản tất toán của sổ nguồn có số dư khả dụng đủ cho vốn gốc tái tục mong muốn.

#### Bảo đảm tối thiểu

1. Nếu thao tác tái tục bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, không có sổ tiết kiệm mới nào được tạo; trạng thái sổ nguồn `SETTLED_MATURED` và số dư ví nguồn được giữ nguyên vẹn.

#### Hậu điều kiện thành công

1. Sổ tiết kiệm nguồn giữ nguyên trạng thái `SETTLED_MATURED`, không bị chỉnh sửa thêm.
2. Một sổ tiết kiệm mới được tạo với mã sổ tiết kiệm riêng, trạng thái `ACTIVE`, trường nguồn tái tục trỏ về sổ nguồn.
3. Số dư ví nguồn giảm đúng bằng vốn gốc tái tục.

#### Luồng sự kiện chính

1. Ngay sau khi hoàn tất luồng tất toán đúng hạn của `UC-SAV-02`, hệ thống hiển thị tùy chọn "Tái tục sổ tiết kiệm" bên cạnh số tiền vừa nhận được.
2. Người dùng chọn tái tục và nhập vốn gốc tái tục mong muốn, kỳ hạn mới, lãi suất mới và phương thức tính lãi mới ($newInterestMethod$).
3. Hệ thống hiển thị bản xem trước: vốn gốc tái tục, ngày đáo hạn dự kiến của sổ mới và số tiền còn lại trong ví sau khi trừ vốn gốc.
4. Người dùng xác nhận. Hệ thống trừ vốn gốc tái tục khỏi ví, đóng sổ nguồn ở trạng thái `SETTLED_MATURED` (không thay đổi thêm) và tạo sổ tiết kiệm mới theo `FR-SAV-05`.
5. Hệ thống hiển thị thông báo tái tục thành công kèm thông tin sổ mới.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh: Tái tục một phần giá trị tất toán

1. Tại bước 2 của luồng chính, người dùng nhập vốn gốc tái tục nhỏ hơn toàn bộ giá trị tất toán.
2. Hệ thống trừ đúng phần vốn gốc tái tục để mở sổ mới, phần chênh lệch còn lại vẫn giữ nguyên trong ví theo `BR-FIN-02`.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 1a: Sổ nguồn chưa đủ điều kiện tái tục

1. Người dùng cố gắng tái tục một sổ tiết kiệm chưa được tất toán đúng hạn (đang `ACTIVE`, `SETTLED_EARLY` hoặc trạng thái khác).
2. Hệ thống từ chối và hiển thị thông báo: "Chỉ có thể tái tục sổ tiết kiệm đã tất toán đúng hạn."

##### Luồng ngoại lệ 2a: Vốn gốc tái tục vượt quá giá trị tất toán

1. Tại bước 2 của luồng chính, người dùng nhập vốn gốc tái tục lớn hơn giá trị tất toán của sổ nguồn.
2. Hệ thống từ chối và hiển thị thông báo nêu rõ giới hạn tối đa cho phép tái tục.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc tính lãi tiết kiệm `BR-FIN-05`: Sổ mới không kế thừa số ngày gửi hay lãi suất của sổ nguồn, được tính lãi độc lập kể từ ngày mở sổ mới.
2. Yêu cầu chức năng liên quan: `FR-SAV-05`.

---

### 6.25 UC-INV-01: Mua tài sản đầu tư

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-INV-01 |
| Tên | Mua tài sản đầu tư |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Thanh toán tiền từ ví tiền để mua tài sản đầu tư, ghi nhận chi phí mua, quản lý giá gốc và xác định số đơn vị sở hữu. |
| Mô tả tóm tắt | Ghi nhận lệnh mua tài sản đầu tư, quy đổi chi phí mua, trừ tiền từ ví tiền và xác định số đơn vị sở hữu theo mô hình đơn giá nhân số lượng của `BR-FIN-06`; tạo tài sản đầu tư mới nếu tên chưa có; tùy chọn liên kết lệnh mua với một quỹ mục tiêu theo `BR-FIN-10` và tối đa một khoản đầu tư. |
| Điều kiện kích hoạt | Người dùng thực hiện mua tài sản đầu tư mới hoặc mua thêm tài sản đầu tư hiện có. |

#### Tiền điều kiện

1. Hệ thống có ít nhất một ví tiền ở trạng thái `ACTIVE` có đủ số dư khả dụng để thanh toán tổng số tiền mua và chi phí mua ($CostBasis_{origin}$).
2. Người dùng đang ở chức năng quản lý tài sản đầu tư.

#### Bảo đảm tối thiểu

1. Nếu thao tác mua bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, không có lệnh mua mới nào được tạo; số dư ví thanh toán và danh mục tài sản đầu tư giữ nguyên vẹn.

#### Hậu điều kiện thành công

1. Lệnh mua mới được lưu thành công kèm định danh độc lập (và tài sản đầu tư mới được tạo nếu tên chưa có trong hệ thống).
2. Số tiền mua, đơn giá mua và số lượng đơn vị mua thỏa $Amount_{buy} = Price_{buy} \times Units_{buy}$; hai đại lượng người dùng nhập được lưu đúng như nhập, số lượng dẫn xuất được lưu đầy đủ chữ số. Đơn giá mua trở thành đơn giá hiện hành nếu ngày mua không cũ hơn ngày định giá gần nhất.
3. Giá trị thị trường của lệnh mua bằng số đơn vị còn nhân đơn giá hiện hành, bằng $Amount_{buy}$ khi mua ở đơn giá hiện hành.
4. Số dư ví thanh toán giảm đúng giá gốc của lệnh mua: $CostBasis_{origin} = Amount_{buy} + Fee_{buy}$ theo `BR-FIN-02`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Mua tài sản đầu tư".
2. Hệ thống hiển thị biểu mẫu mua tài sản đầu tư gồm các trường thông tin:
   - Tên tài sản đầu tư (độ dài từ 1 đến 50 ký tự; ví dụ: VESAF, Vàng SJC, Đất lô 7).
   - Loại tài sản (chứng chỉ quỹ, cổ phiếu, trái phiếu, vàng, bất động sản; bắt buộc khi tạo tài sản mới).
   - Ngày mua (định dạng YYYY-MM-DD, mặc định ngày hiện tại, không vượt quá ngày hiện tại).
   - Số tiền mua ($Amount_{buy} > 0$; tùy chọn trong nhóm hai trong ba).
   - Đơn giá mua ($Price_{buy} > 0$; tùy chọn trong nhóm hai trong ba).
   - Số lượng đơn vị mua ($Units_{buy} > 0$, tối đa 4 chữ số thập phân; tùy chọn trong nhóm hai trong ba, bỏ trống thì hệ thống điền 1 khi người dùng chỉ nhập một trong hai đại lượng còn lại).
   - Cách nhập chi phí mua (Số tiền, Phần trăm từ 0 đến 100, hoặc Không có chi phí; mặc định Không có chi phí).
   - Giá trị chi phí mua (bắt buộc khi chọn Số tiền hoặc Phần trăm).
   - Ví nguồn thanh toán (danh sách ví ở trạng thái `ACTIVE`).
   - Quỹ mục tiêu liên kết (tùy chọn theo `BR-FIN-10`).
   - Khoản đầu tư liên kết (tùy chọn, thuộc tối đa một khoản).
3. Người dùng nhập các thông tin cần thiết và xác nhận thực hiện lệnh mua.
4. Hệ thống xác định đại lượng còn lại và kiểm tra các ràng buộc nghiệp vụ:
   - So khớp tên tài sản đầu tư (bỏ khoảng trắng đầu cuối, không phân biệt chữ hoa chữ thường). Nếu tên đã tồn tại, hệ thống dùng loại tài sản hiện có của tài sản đó, không cho đổi. Nếu tên chưa có, hệ thống tạo tài sản mới với loại tài sản đã nhập. Tên tài sản đầu tư phải có độ dài từ 1 đến 50 ký tự.
   - Kiểm tra người dùng nhập đúng hai trong ba đại lượng số tiền mua, đơn giá mua, số lượng, hoặc chỉ nhập một trong hai đại lượng số tiền mua và đơn giá mua; nếu không thì từ chối và báo lỗi nghiệp vụ thiếu hoặc thừa đại lượng.
   - Kiểm tra từng đại lượng người dùng đã nhập trước khi xác định đại lượng còn lại: số tiền mua $Amount_{buy} > 0$, nếu vi phạm báo lỗi `ERR-INV-002`; đơn giá mua $Price_{buy} > 0$, nếu vi phạm báo lỗi `ERR-INV-001`; số lượng $Units_{buy} > 0$ và có tối đa 4 chữ số thập phân, nếu vi phạm kích hoạt luồng ngoại lệ 4g.
   - Hệ thống xác định đại lượng còn lại theo `BR-FIN-06`:
     - Nhập số tiền mua và đơn giá mua: $Units_{buy} = \frac{Amount_{buy}}{Price_{buy}}$, lưu đầy đủ chữ số, không làm tròn 4 chữ số.
     - Nhập số tiền mua và số lượng: đơn giá mua là đại lượng dẫn xuất, giữ đủ độ chính xác để tích đơn giá nhân số lượng làm tròn 2 chữ số bằng đúng số tiền mua.
     - Nhập đơn giá mua và số lượng: $Amount_{buy} = Price_{buy} \times Units_{buy}$, làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng.
     - Chỉ nhập số tiền mua hoặc chỉ nhập đơn giá mua: hệ thống điền $Units_{buy} = 1$, đơn giá mua bằng số tiền mua hoặc số tiền mua bằng đơn giá mua tương ứng.
   - Nếu số tiền mua dẫn xuất làm tròn bằng 0.00 thì kích hoạt luồng ngoại lệ 4b với `ERR-INV-002`.
   - Kiểm tra chi phí mua: nếu nhập theo số tiền thì $Fee_{buy} \ge 0$; nếu nhập theo tỷ lệ phần trăm thì tỷ lệ phải nằm trong khoảng từ 0 đến 100, nếu vi phạm báo lỗi `ERR-INV-010`. Hệ thống quy đổi chi phí mua về số tiền VNĐ và làm tròn 2 chữ số thập phân theo `BR-FIN-01`.
   - Xác định giá gốc của lệnh mua: $CostBasis_{origin} = Amount_{buy} + Fee_{buy}$.
   - Kiểm tra ngày mua không vượt quá ngày hiện tại, nếu vi phạm kích hoạt luồng ngoại lệ 4e.
   - Kiểm tra ví thanh toán ở trạng thái `ACTIVE`, nếu vi phạm kích hoạt luồng ngoại lệ 4d.
   - Kiểm tra số dư khả dụng đáp ứng: $WalletBalance_{wallet} - CostBasis_{origin} \ge 0$ theo `BR-FIN-02`, nếu vi phạm kích hoạt luồng ngoại lệ 4d.
5. Hệ thống trừ tiền từ ví thanh toán:
$$WalletBalance_{wallet}(\text{sau}) = WalletBalance_{wallet}(\text{trước}) - CostBasis_{origin}$$
6. Hệ thống ghi nhận lệnh mua mới độc lập, lưu các trường: mã lệnh mua, tài sản đầu tư, ngày mua, đơn giá mua, số tiền mua, chi phí mua, giá gốc, số lượng đơn vị và ví thanh toán. Hệ thống tự ghi thêm Thời điểm ghi và số thứ tự ghi. Giá vốn ban đầu của lệnh mua bằng giá gốc.
   - Giá trị thị trường của lệnh mua luôn bằng số đơn vị còn nhân đơn giá hiện hành, kể cả ngay sau khi ghi; khi ghi lùi ngày với đơn giá mua khác đơn giá hiện hành, giá trị khác số tiền mua và chênh lệch là lãi lỗ chưa chốt.
   - Nếu ngày mua không cũ hơn ngày định giá gần nhất:
     - Đơn giá mua trở thành đơn giá hiện hành và được lưu vào lịch sử giá.
     - Nếu ngày mua bằng ngày định giá gần nhất thì đơn giá mua thay thế bản ghi giá của ngày đó trong lịch sử giá và trở thành đơn giá hiện hành theo QD-242.
     - Mọi lệnh mua còn đơn vị của tài sản được tính lại giá trị thị trường theo đơn giá mới này.
   - Nếu ngày mua cũ hơn ngày định giá gần nhất thì đơn giá mua chỉ lưu vào lịch sử giá và giữ nguyên đơn giá hiện hành.
7. Nếu người dùng chỉ định quỹ mục tiêu, hệ thống đồng bộ liên kết theo `BR-FIN-10`. Nếu người dùng chỉ định khoản đầu tư, hệ thống gán lệnh mua vào khoản đầu tư tương ứng.
8. Hệ thống hiển thị thông báo ghi nhận lệnh mua thành công kèm chi tiết: số lượng đơn vị nhận được, giá gốc, chi phí mua và số dư mới của ví tiền. Nếu giao dịch cùng ngày với giao dịch đã ghi, thông báo nêu giao dịch được tính sau những giao dịch nào.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 4a: Ghi lùi ngày mua làm thay đổi khấu trừ lệnh bán nhập trước xuất trước

1. Sau khi mọi kiểm tra của bước 4 đều đạt, hệ thống phát hiện một trong hai trường hợp. Trường hợp một: ngày mua cũ hơn ngày mua của một lệnh mua đã bị khấu trừ bởi lệnh bán của cùng tài sản đầu tư. Trường hợp hai: ngày mua cũ hơn một sự kiện đã ghi của tài sản.
2. Hệ thống tính toán lại khấu trừ nhập trước xuất trước của các lệnh bán bị ảnh hưởng theo `FR-INV-01` quy tắc 9, cùng phân bổ của các sự kiện đứng sau.
3. Hệ thống hiển thị danh sách các lệnh bán và phân bổ sự kiện bị ảnh hưởng kèm lãi lỗ đã chốt trước và sau khi tính lại (cùng phần hoàn vốn vượt giá vốn còn lại nếu có). Hệ thống hiển thị thêm cảnh báo quỹ mục tiêu nếu lệnh mua nắm giữ bị đổi đang gắn quỹ, và yêu cầu người dùng xác nhận.
4. Nếu người dùng xác nhận, hệ thống tiếp tục bước 5 của luồng chính và cập nhật các lệnh bán tương ứng.
5. Nếu người dùng hủy bỏ, hệ thống dừng giao dịch và giữ nguyên dữ liệu hiện tại.

##### Luồng rẽ nhánh 8a: Sửa lệnh mua đã ghi

1. Sau bước 8 của luồng chính, người dùng chọn một lệnh mua đã ghi và sửa đơn giá mua, số lượng hoặc số tiền mua theo `FR-INV-01` quy tắc 10. Khi sửa đơn giá hoặc số lượng, người dùng chọn giữ nguyên một trong hai đại lượng còn lại (số tiền mua là mặc định), đại lượng còn lại được tính lại; khi sửa số tiền mua, số lượng giữ nguyên.
2. Hệ thống xử lý giá trị sửa như sau:
   - Kiểm tra giá trị sửa theo cùng các ràng buộc của bước 4 (số tiền mua và đơn giá mua lớn hơn 0, số lượng lớn hơn 0 và tối đa 4 chữ số thập phân). Nếu vi phạm, hệ thống báo lỗi như luồng ngoại lệ 4b và 4g.
   - Xác định đại lượng còn lại, chi phí mua và giá gốc mới theo `BR-FIN-06` và `QD-185`.
   - Áp dụng quy tắc đơn giá hiện hành của bước 6 cho đơn giá sau khi sửa. Nếu ngày mua không cũ hơn ngày định giá gần nhất, đơn giá sau khi sửa trở thành đơn giá hiện hành và được lưu vào lịch sử giá. Khi đó, giá trị mọi lệnh mua còn đơn vị được tính lại.
3. Hệ thống tính lại các lệnh bán và sự kiện phát sinh sau đó. Nếu ví thanh toán không ở trạng thái `ACTIVE`, số dư không đủ cho khoản trừ thêm, hoặc việc tính lại làm một lệnh bán sau đó thiếu đơn vị (`ERR-INV-009`), hệ thống từ chối toàn bộ việc sửa. Hệ thống không hiển thị bước xác nhận và giữ nguyên dữ liệu.
4. Hệ thống hiển thị tác động trước và sau kèm khoản trừ thêm hoặc hoàn lại vào ví thanh toán, phần hoàn vốn vượt giá vốn còn lại nếu có, cảnh báo quỹ mục tiêu nếu có, và yêu cầu người dùng xác nhận.
5. Nếu người dùng xác nhận, hệ thống lưu lệnh mua đã sửa, điều chỉnh số dư ví thanh toán theo chênh lệch giá gốc theo `BR-FIN-02` và ghi dấu vết.
6. Nếu người dùng hủy bỏ, hệ thống dừng thao tác và giữ nguyên dữ liệu.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4b: Đơn giá hoặc số tiền mua không hợp lệ

1. Tại bước 4 của luồng chính, nếu $Amount_{buy} \le 0$ hoặc số tiền mua dẫn xuất làm tròn bằng 0.00, hệ thống từ chối và báo lỗi `ERR-INV-002`: "Số tiền mua phải lớn hơn 0."
2. Tại bước 4 của luồng chính, nếu $Price_{buy} \le 0$, hệ thống từ chối và báo lỗi `ERR-INV-001`: "Đơn giá mua phải lớn hơn 0, đơn giá bán và đơn giá hiện hành không được âm."
3. Người dùng nhập lại thông tin hợp lệ để tiếp tục.

##### Luồng ngoại lệ 4c: Chi phí mua không hợp lệ

1. Tại bước 4 của luồng chính, hệ thống phát hiện chi phí mua âm ($Fee_{buy} < 0$) hoặc tỷ lệ phần trăm chi phí mua ngoài khoảng từ 0 đến 100 ($feeRate < 0$ hoặc $feeRate > 100$).
2. Hệ thống từ chối tạo lệnh mua và báo lỗi `ERR-INV-010`: "Chi phí mua không hợp lệ. Tỷ lệ phần trăm phải nằm trong khoảng từ 0 đến 100."
3. Người dùng điều chỉnh lại chi phí mua hợp lệ.

##### Luồng ngoại lệ 4d: Ví thanh toán không hoạt động hoặc số dư không đủ

1. Tại bước 4 của luồng chính, hệ thống phát hiện ví nguồn không ở trạng thái `ACTIVE`, hoặc số dư khả dụng của ví nguồn không đủ: $WalletBalance_{wallet} < CostBasis_{origin}$.
2. Hệ thống từ chối lệnh mua. Ví không hoạt động thì thông báo: "Ví nguồn không ở trạng thái hoạt động." Số dư không đủ thì thông báo: "Số dư ví nguồn không đủ để thanh toán tiền mua và chi phí."
3. Người dùng chọn ví thanh toán khác ở trạng thái `ACTIVE` hoặc giảm số tiền mua.

##### Luồng ngoại lệ 4e: Tên tài sản đầu tư hoặc ngày mua không hợp lệ

1. Tại bước 4 của luồng chính, nếu tên tài sản đầu tư để trống hoặc dài hơn 50 ký tự, hệ thống từ chối và báo lỗi tên tài sản không hợp lệ.
2. Tại bước 4 của luồng chính, nếu ngày mua vượt quá ngày hiện tại, hệ thống từ chối và báo lỗi ngày mua không hợp lệ.
3. Người dùng chỉnh sửa lại thông tin hợp lệ.

##### Luồng ngoại lệ 4f: Thiếu hoặc thừa đại lượng định giá

1. Tại bước 4 của luồng chính, hệ thống phát hiện người dùng nhập đủ ba đại lượng số tiền mua, đơn giá mua và số lượng, chỉ nhập số lượng, hoặc không nhập đại lượng nào.
2. Hệ thống từ chối lệnh mua và thông báo lỗi nghiệp vụ: "Chỉ nhập hai trong ba đại lượng số tiền mua, đơn giá mua và số lượng, hoặc chỉ nhập một trong hai đại lượng số tiền mua và đơn giá mua."
3. Người dùng nhập lại đúng các đại lượng để tiếp tục.

##### Luồng ngoại lệ 4g: Số lượng mua không hợp lệ

1. Tại bước 4 của luồng chính, hệ thống phát hiện số lượng người dùng nhập nhỏ hơn hoặc bằng 0, hoặc có hơn 4 chữ số thập phân.
2. Hệ thống từ chối lệnh mua và thông báo lỗi nghiệp vụ: "Số lượng mua phải lớn hơn 0 và có tối đa 4 chữ số thập phân."
3. Người dùng nhập lại số lượng hợp lệ.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc định giá và giá vốn tài sản đầu tư `BR-FIN-06`: Xác định giá gốc, giá vốn, số lượng đơn vị theo mô hình đơn giá nhân số lượng; cập nhật đơn giá hiện hành khi mua mới; giá trị thị trường của lệnh mua luôn bằng số đơn vị còn nhân đơn giá hiện hành, bằng số tiền mua khi mua ở đơn giá hiện hành.
2. Quy tắc độ chính xác số học `BR-FIN-01`: Số lượng đơn vị $Units$ dẫn xuất lưu đầy đủ chữ số. Số lượng mua và số lượng bán do người dùng nhập có tối đa 4 chữ số thập phân. Tiền tệ và chi phí giữ 2 chữ số thập phân theo quy tắc làm tròn ngân hàng.
3. Quy tắc dòng tiền `BR-FIN-02`: Ghi nhận lệnh mua làm giảm số dư ví nguồn đúng bằng giá gốc $CostBasis_{origin} = Amount_{buy} + Fee_{buy}$.
4. Quy tắc liên kết quỹ mục tiêu `BR-FIN-10`: Mỗi lệnh mua chỉ thuộc về tối đa một quỹ mục tiêu.
5. Quy tắc quản lý khoản đầu tư: Mỗi lệnh mua thuộc tối đa một khoản đầu tư tại một thời điểm theo `QD-139`.
6. Yêu cầu chức năng liên quan: `FR-INV-01`, `FR-INV-02`.

---

### 6.26 UC-INV-02: Bán tài sản đầu tư

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-INV-02 |
| Tên | Bán tài sản đầu tư |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Tất toán một phần hoặc toàn bộ tài sản đầu tư, thu hồi tiền về ví, trừ chi phí bán và xác định chính xác lãi lỗ đã chốt theo phương pháp nhập trước xuất trước. |
| Mô tả tóm tắt | Ghi nhận lệnh bán tài sản đầu tư theo phương pháp nhập trước xuất trước theo `BR-FIN-06`; xác định số tiền thực nhận sau khi trừ chi phí bán theo `QD-100`; cộng tiền vào ví nhận tiền theo `BR-FIN-02`; tính toán lãi lỗ đã chốt; tự động tính lại các lệnh bán phát sinh sau đó khi ghi lùi ngày kèm lưu dấu vết theo `QD-132`. |
| Điều kiện kích hoạt | Người dùng thực hiện bán một phần hoặc toàn bộ một tài sản đầu tư đang nắm giữ. |

#### Tiền điều kiện

1. Tài sản đầu tư cần bán còn đơn vị nắm giữ lớn hơn 0 ngay trước lệnh bán ($Units_{held,total} > 0$).
2. Hệ thống có ít nhất một ví tiền ở trạng thái `ACTIVE` để tiếp nhận số tiền bán thực nhận.
3. Người dùng đang ở chức năng quản lý tài sản đầu tư.

#### Bảo đảm tối thiểu

1. Nếu thao tác bán bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, không có lệnh bán nào được lưu; số đơn vị và giá vốn của các lệnh mua được bảo toàn nguyên trạng, số dư ví nhận tiền không thay đổi.

#### Hậu điều kiện thành công

1. Lệnh bán mới được ghi nhận độc lập kèm định danh riêng, ngày bán, số đơn vị bán, đơn giá bán hoặc số tiền bán nhận về, chi phí bán, số tiền thực nhận và lãi lỗ đã chốt. Hệ thống tự ghi thêm Thời điểm ghi và số thứ tự ghi.
2. Các lệnh mua bị khấu trừ được cập nhật giảm số đơn vị còn lại và giảm giá vốn còn lại theo phương pháp nhập trước xuất trước.
3. Số dư khả dụng của ví nhận tiền tăng đúng số tiền thực nhận: $NetAmount = GrossProceeds - Fee_{sell}$ theo `BR-FIN-02`.
4. Lãi lỗ đã chốt $RealizedPL = NetAmount - \sum_{i} CostBasis_{sold,i}$ được ghi nhận vào báo cáo tài chính.
5. Trường hợp ghi lùi ngày, các lệnh bán phát sinh sau đó được tính lại khấu trừ và lãi lỗ đã chốt kèm bản ghi dấu vết lịch sử.
6. Tài sản đầu tư đã bán hết vẫn được lưu giữ trong hệ thống để ghi nhận các sự kiện sau ngày bán nếu có theo `FR-INV-05`.

#### Luồng sự kiện chính

1. Người dùng chọn tài sản đầu tư cần bán và chọn chức năng "Bán tài sản đầu tư".
2. Hệ thống hiển thị biểu mẫu bán tài sản đầu tư gồm các trường thông tin:
   - Tên tài sản đầu tư (hiển thị tên tài sản đã chọn).
   - Số đơn vị đang nắm giữ ($Units_{held,total}$ tính đến Ngày bán).
   - Ngày bán (định dạng YYYY-MM-DD, mặc định ngày hiện tại, không vượt quá ngày hiện tại).
   - Cách bán (Bán một phần hoặc Bán toàn bộ, áp dụng cho mọi tài sản đầu tư).
   - Số đơn vị bán ($Units_{sell} > 0$, tối đa 4 chữ số thập phân; bắt buộc khi chọn Bán một phần, bỏ qua khi chọn Bán toàn bộ). Nhập bằng số hiển thị của lệnh vẫn là bán một phần; Bán toàn bộ bán hết số lượng đầy đủ còn lại.
   - Đơn giá bán ($Price_{sell} \ge 0$; nhập một trong hai đơn giá bán hoặc số tiền bán nhận về).
   - Số tiền bán nhận về ($GrossProceeds \ge 0$, được phép bằng 0; nhập một trong hai đơn giá bán hoặc số tiền bán nhận về).
   - Chi phí bán ($Fee_{sell} \ge 0$; mặc định 0.00 VNĐ).
   - Ví nhận tiền (danh sách ví ở trạng thái `ACTIVE`).
3. Người dùng nhập các thông tin cần thiết và xác nhận thực hiện lệnh bán.
4. Hệ thống kiểm tra các ràng buộc nghiệp vụ:
   - Kiểm tra ví nhận tiền ở trạng thái `ACTIVE`.
   - Xác định số đơn vị nắm giữ $Units_{held,total} = \sum_{i} Units_{held,i}$ tại Ngày bán: xếp lệnh bán vào thứ tự xử lý (ngày giao dịch, rồi số thứ tự ghi). Lấy đơn vị của các lệnh mua đứng trước, cộng đơn vị nhận thêm từ các sự kiện đứng trước, trừ các lệnh bán đứng trước.
   - Nếu chọn Bán toàn bộ thì $Units_{sell} = Units_{held,total}$ tính trên số lượng đầy đủ. Kiểm tra $Units_{sell} > 0$, và khi chọn Bán một phần kiểm tra thêm có tối đa 4 chữ số thập phân, nếu vi phạm kích hoạt luồng ngoại lệ 4i.
   - Nếu $Units_{sell} > Units_{held,total}$, hệ thống từ chối và báo lỗi `ERR-INV-009`.
   - Kiểm tra người dùng nhập đúng một trong hai đơn giá bán hoặc số tiền bán nhận về; nếu nhập cả hai hoặc không nhập giá trị nào thì từ chối.
   - Kiểm tra $Price_{sell} \ge 0$, nếu $Price_{sell} < 0$ báo lỗi `ERR-INV-001`, và kiểm tra $GrossProceeds \ge 0$. Xác định tổng số tiền bán: nhập đơn giá bán thì $GrossProceeds = Units_{sell} \times Price_{sell}$ làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng; nhập số tiền bán nhận về thì đơn giá bán là đại lượng dẫn xuất. Số tiền bán nhận về bằng 0 được phép và chốt lỗ bằng toàn bộ giá vốn xuất bán.
   - Kiểm tra chi phí bán: $Fee_{sell} \ge 0$, nếu $Fee_{sell} < 0$ kích hoạt luồng ngoại lệ 4j. Chi phí bán không vượt quá tổng số tiền bán ($Fee_{sell} \le GrossProceeds$), nếu $Fee_{sell} > GrossProceeds$ kích hoạt luồng ngoại lệ 4e.
   - Xác định số tiền thực nhận: $NetAmount = GrossProceeds - Fee_{sell} \ge 0$.
5. Hệ thống thực hiện khấu trừ đơn vị và giá vốn theo phương pháp nhập trước xuất trước theo `BR-FIN-06`:
   - Lệnh mua có ngày mua cũ nhất bị khấu trừ trước. Các lệnh mua cùng ngày mua sắp xếp theo số thứ tự ghi tăng dần.
   - Với mỗi lệnh mua $i$ bị khấu trừ $Units_{sold,i}$ đơn vị:
    $$CostBasis_{sold,i} = CostBasis_{remain,i} \times \frac{Units_{sold,i}}{Units_{remain,i}}$$
    $$CostBasis_{remain,i}' = CostBasis_{remain,i} - CostBasis_{sold,i}$$
    $$Units_{remain,i}' = Units_{remain,i} - Units_{sold,i}$$
   - Giá vốn xuất bán làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng nửa chẵn, giá vốn còn lại là hiệu nên phần dư làm tròn nằm ở giá vốn còn lại (QD-259). Khi bán hết lệnh mua $i$, giá vốn xuất bán bằng toàn bộ giá vốn còn lại của lệnh mua đó.
6. Hệ thống xác định lãi lỗ đã chốt của lệnh bán:
$$RealizedPL = NetAmount - \sum_{i} CostBasis_{sold,i}$$
7. Hệ thống cập nhật các sổ dữ liệu liên quan:
   - Cộng số tiền thực nhận $NetAmount$ vào số dư khả dụng của ví nhận tiền theo `BR-FIN-02`:
    $$WalletBalance_{wallet}(\text{sau}) = WalletBalance_{wallet}(\text{trước}) + NetAmount$$
   - Cập nhật số đơn vị còn lại và giá vốn còn lại của các lệnh mua bị khấu trừ.
   - Nếu lệnh mua bị khấu trừ có liên kết quỹ mục tiêu, hệ thống tự động cập nhật giảm giá trị đóng góp của lệnh mua vào quỹ mục tiêu theo `BR-FIN-10`.
8. Hệ thống ghi nhận lệnh bán mới độc lập kèm Thời điểm ghi và số thứ tự ghi do hệ thống tự ghi. Hệ thống hiển thị biên lai giao dịch thành công gồm: số tiền thực nhận, tổng giá vốn xuất bán, chi phí bán, lãi lỗ đã chốt và số dư mới của ví nhận tiền. Nếu giao dịch cùng ngày với giao dịch đã ghi, thông báo nêu giao dịch được tính sau những giao dịch nào.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 4a: Ghi lùi ngày bán làm thay đổi thứ tự khấu trừ các lệnh bán sau đó

1. Sau khi mọi kiểm tra của bước 4 đều đạt, hệ thống phát hiện tài sản đầu tư đã có lệnh bán hoặc sự kiện với ngày sau Ngày bán mới.
2. Hệ thống sắp xếp lại toàn bộ các lệnh bán theo thứ tự Ngày bán tăng dần, rồi theo số thứ tự ghi tăng dần. Sau đó, hệ thống tính lại khấu trừ nhập trước xuất trước từ đầu theo `FR-INV-04` quy tắc 8, cùng phân bổ của các sự kiện đứng sau lệnh bán mới.
3. Các lệnh bán cũ giữ nguyên số đơn vị và số tiền đã nhận, chỉ tính lại lệnh mua bị khấu trừ, giá vốn xuất bán và lãi lỗ đã chốt.
4. Nếu việc tính lại làm một lệnh bán sau đó không đủ đơn vị nắm giữ, hệ thống từ chối toàn bộ lệnh bán mới với `ERR-INV-009` và thông báo lỗi.
5. Hệ thống hiển thị danh sách các lệnh bán và phân bổ sự kiện bị ảnh hưởng kèm giá vốn xuất bán và lãi lỗ đã chốt trước và sau khi tính lại (cùng phần hoàn vốn vượt giá vốn nếu có). Giao diện hiển thị cảnh báo quỹ mục tiêu nếu lệnh mua bị ảnh hưởng đang gắn quỹ. Hệ thống yêu cầu người dùng xác nhận.
6. Khi người dùng xác nhận, hệ thống tiếp tục bước 5 của luồng chính, cập nhật các lệnh bán tương ứng và lưu dấu vết lịch sử tính lại theo `QD-132`.
7. Nếu người dùng hủy bỏ, hệ thống dừng giao dịch và giữ nguyên dữ liệu hiện tại.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4b: Đơn giá bán không hợp lệ

1. Tại bước 4 của luồng chính, hệ thống phát hiện $Price_{sell} < 0$.
2. Hệ thống từ chối lệnh bán và báo lỗi `ERR-INV-001`: "Đơn giá mua phải lớn hơn 0, đơn giá bán và đơn giá hiện hành không được âm."
3. Người dùng nhập lại đơn giá hợp lệ để tiếp tục.

##### Luồng ngoại lệ 4c: Số đơn vị bán vượt quá số đơn vị nắm giữ tại Ngày bán

1. Tại bước 4 của luồng chính, hệ thống phát hiện $Units_{sell} > Units_{held,total}$ tính đến Ngày bán.
2. Hệ thống từ chối lệnh bán và báo lỗi `ERR-INV-009`: "Số đơn vị bán vượt quá số đơn vị nắm giữ tại ngày giao dịch." Thông báo nêu rõ số đơn vị nắm giữ thực tế tại Ngày bán.
3. Người dùng điều chỉnh số lượng bán hoặc chọn Ngày bán khác.

##### Luồng ngoại lệ 4d: Thiếu hoặc thừa đại lượng định giá

1. Tại bước 4 của luồng chính, hệ thống phát hiện người dùng nhập cả đơn giá bán và số tiền bán nhận về, hoặc không nhập giá trị nào.
2. Hệ thống từ chối lệnh bán và thông báo lỗi nghiệp vụ: "Chỉ nhập một trong hai đơn giá bán hoặc số tiền bán nhận về."
3. Người dùng nhập lại đúng một giá trị để tiếp tục.

##### Luồng ngoại lệ 4e: Chi phí bán vượt quá tổng số tiền bán

1. Tại bước 4 của luồng chính, hệ thống phát hiện $Fee_{sell} > GrossProceeds$, dẫn đến $NetAmount < 0$.
2. Hệ thống từ chối lệnh bán và thông báo lỗi: "Chi phí bán không được vượt quá tổng số tiền bán."
3. Người dùng điều chỉnh lại chi phí bán hợp lệ.

##### Luồng ngoại lệ 4f: Số tiền bán nhận về âm

1. Tại bước 4 của luồng chính, hệ thống phát hiện $GrossProceeds < 0$.
2. Hệ thống từ chối lệnh bán và thông báo lỗi nghiệp vụ: "Số tiền bán nhận về không được âm."
3. Người dùng nhập lại số tiền bán nhận về hợp lệ, được phép bằng 0.

##### Luồng ngoại lệ 4g: Ví nhận tiền không hợp lệ hoặc không hoạt động

1. Tại bước 4 của luồng chính, hệ thống phát hiện ví nhận tiền không tồn tại hoặc ở trạng thái `ARCHIVED`.
2. Hệ thống từ chối lệnh bán và thông báo: "Ví nhận tiền không ở trạng thái hoạt động."
3. Người dùng chọn một ví tiền khác ở trạng thái `ACTIVE`.

##### Luồng ngoại lệ 4h: Ghi lùi ngày dẫn đến lệnh bán sau đó thiếu đơn vị

1. Tại luồng rẽ nhánh 4a, kết quả tính lại cho thấy một lệnh bán phát sinh trong tương lai có số lượng bán vượt quá số đơn vị còn lại sau khi chèn lệnh bán mới.
2. Hệ thống từ chối toàn bộ lệnh bán mới và báo lỗi `ERR-INV-009`: "Số đơn vị bán vượt quá số đơn vị nắm giữ tại ngày giao dịch." Hệ thống không thay đổi bất kỳ dữ liệu nào.
3. Người dùng điều chỉnh giảm số lượng bán của lệnh bán mới hoặc hủy thao tác.

##### Luồng ngoại lệ 4i: Số đơn vị bán không hợp lệ

1. Tại bước 4 của luồng chính, hệ thống phát hiện số đơn vị bán nhỏ hơn hoặc bằng 0 (kể cả khi chọn Bán toàn bộ tại Ngày bán chưa có đơn vị nắm giữ), hoặc có hơn 4 chữ số thập phân khi chọn Bán một phần.
2. Hệ thống từ chối lệnh bán và thông báo lỗi nghiệp vụ: "Số lượng bán phải lớn hơn 0 và có tối đa 4 chữ số thập phân."
3. Người dùng nhập lại số đơn vị bán hợp lệ, hoặc chọn Bán toàn bộ tại Ngày bán đã có đơn vị nắm giữ.

##### Luồng ngoại lệ 4j: Chi phí bán âm

1. Tại bước 4 của luồng chính, hệ thống phát hiện $Fee_{sell} < 0$.
2. Hệ thống từ chối lệnh bán và thông báo lỗi nghiệp vụ: "Chi phí bán không được âm."
3. Người dùng nhập lại chi phí bán hợp lệ.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc định giá và giá vốn tài sản đầu tư `BR-FIN-06`: Khấu trừ đơn vị và giá vốn theo phương pháp nhập trước xuất trước. Chỉ xét các lệnh mua và sự kiện đứng trước lệnh bán theo thứ tự xử lý (ngày giao dịch, rồi số thứ tự ghi). Chi phí bán không vượt số tiền bán và số tiền bán nhận về được bằng 0. Tính lại có xác nhận và lưu dấu vết khi ghi lùi ngày bán hoặc lùi ngày mua.
2. Quy tắc độ chính xác tài chính `BR-FIN-01`: Số tiền bán và giá vốn xuất bán làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng, số đơn vị bán tối đa 4 chữ số thập phân.
3. Quy tắc dòng tiền `BR-FIN-02`: Ghi nhận lệnh bán làm tăng số dư ví nhận tiền đúng bằng số tiền thực nhận $NetAmount = GrossProceeds - Fee_{sell}$.
4. Quy tắc liên kết tài sản quỹ mục tiêu `BR-FIN-10`: Giá trị đóng góp của lệnh mua vào quỹ mục tiêu tự động giảm tương ứng khi lệnh mua bị khấu trừ xuất bán.
5. Yêu cầu chức năng liên quan: `FR-INV-04`, `FR-INV-01` (quy tắc 9).

---

### 6.27 UC-INV-03: Cập nhật đơn giá hoặc giá trị thị trường

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-INV-03 |
| Tên | Cập nhật đơn giá hoặc giá trị thị trường |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Định giá lại tài sản đầu tư theo nguyên tắc Mark-to-Market khi có đơn giá mới hoặc giá trị thị trường tổng mới. |
| Mô tả tóm tắt | Cập nhật đơn giá thị trường mới nhất hoặc giá trị thị trường tổng cho một tài sản đầu tư theo `FR-INV-02`, định giá lại các lệnh mua còn đơn vị nắm giữ theo `BR-FIN-06` và cập nhật lịch sử giá. |
| Điều kiện kích hoạt | Người dùng có đơn giá mới hoặc giá trị thị trường tổng mới của tài sản đầu tư đang nắm giữ. |

#### Tiền điều kiện

1. Tài sản đầu tư đã tồn tại và có ít nhất một lệnh mua còn đơn vị nắm giữ ($Units_{remain,i} > 0$).
2. Người dùng đang ở màn hình quản lý tài sản đầu tư.

#### Bảo đảm tối thiểu

1. Nếu thao tác cập nhật đơn giá bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, đơn giá hiện hành và lịch sử giá của tài sản đầu tư giữ nguyên trạng thái cũ; giá trị thị trường và liên kết quỹ mục tiêu không thay đổi.

#### Hậu điều kiện thành công

1. Khi Ngày định giá không cũ hơn ngày định giá gần nhất, đơn giá hiện hành $CurrentPrice$ theo `QD-168` của tài sản đầu tư được cập nhật bằng đơn giá mới $NewPrice$ theo `QD-263`. Trong trường hợp đó, khi người dùng nhập giá trị tổng, đơn giá hiện hành bằng giá trị thị trường tổng chia tổng số đơn vị còn nắm giữ theo `QD-181`.
2. Khi Ngày định giá không cũ hơn ngày định giá gần nhất, giá trị thị trường hiện tại của toàn bộ các lệnh mua còn đơn vị được tính lại: $MarketValue_{remain,i} = Units_{remain,i} \times CurrentPrice$ theo `BR-FIN-06`.
3. Bản ghi biến động giá mới được lưu vào lịch sử giá của tài sản đầu tư.
4. Khi Ngày định giá không cũ hơn ngày định giá gần nhất, giá trị đóng góp của lệnh mua vào quỹ mục tiêu liên kết (nếu có) được cập nhật đồng bộ theo `BR-FIN-10`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Cập nhật đơn giá hoặc giá trị thị trường".
2. Hệ thống hiển thị biểu mẫu cập nhật gồm: Danh sách chọn tài sản đầu tư, Ngày định giá (định dạng YYYY-MM-DD, mặc định ngày hiện tại), Đơn giá hiện hành hoặc Giá trị thị trường tổng (nhập một trong hai).
3. Người dùng chọn tài sản đầu tư, nhập đơn giá hiện hành mới ($NewPrice \ge 0$, giá trị do người dùng nhập, chỉ trở thành $CurrentPrice$ sau khi qua các bước kiểm tra 4 và 5) hoặc giá trị thị trường tổng ($MarketValue_{total} \ge 0$) và xác nhận cập nhật.
4. Hệ thống kiểm tra các ràng buộc:
   - Kiểm tra tài sản đầu tư đã có trong hệ thống và còn ít nhất một lệnh mua còn đơn vị nắm giữ, nếu vi phạm kích hoạt luồng ngoại lệ 4c.
   - Kiểm tra người dùng nhập đúng một trong hai giá trị, kiểm tra $NewPrice \ge 0$ và $MarketValue_{total} \ge 0$, nếu vi phạm kích hoạt luồng ngoại lệ 4a.
   - Kiểm tra Ngày định giá không vượt quá ngày hiện tại, nếu vi phạm kích hoạt luồng ngoại lệ 4b.
5. Hệ thống tìm tất cả các lệnh mua còn đơn vị nắm giữ ($Units_{remain,i} > 0$) của tài sản đầu tư này và so sánh Ngày định giá với ngày định giá gần nhất; nếu cũ hơn thì kích hoạt luồng rẽ nhánh 5a.
6. Hệ thống cập nhật đơn giá hiện hành $CurrentPrice = NewPrice$, hoặc $CurrentPrice = MarketValue_{total} / \sum_{i} Units_{remain,i}$ khi người dùng nhập giá trị thị trường tổng, và định giá lại từng lệnh mua theo nguyên tắc Mark-to-Market tại `BR-FIN-06`:
$$MarketValue_{remain,i} = Units_{remain,i} \times CurrentPrice$$
   Khi nhập giá trị thị trường tổng, phần dư do làm tròn được dồn vào lệnh mua có ngày mua mới nhất. Nếu cùng ngày mua, phần dư dồn vào lệnh có số thứ tự ghi lớn nhất, để tổng các lệnh bằng đúng giá trị đã nhập.
7. Hệ thống thêm bản ghi biến động giá vào lịch sử giá của tài sản đầu tư gồm tên tài sản, ngày định giá và giá trị mới là đơn giá hiện hành hoặc giá trị thị trường tổng người dùng đã nhập.
8. Nếu lệnh mua có liên kết với quỹ mục tiêu, hệ thống cập nhật lại giá trị đóng góp của lệnh mua vào quỹ mục tiêu tương ứng theo `BR-FIN-10`.
9. Hệ thống thông báo cập nhật đơn giá thành công và làm mới bảng định giá tài sản đầu tư.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 5a: Ngày định giá cũ hơn lần định giá gần nhất

1. Tại bước 5 của luồng chính, hệ thống phát hiện Ngày định giá cũ hơn ngày định giá gần nhất của tài sản đầu tư.
2. Hệ thống bỏ qua bước 6 và bước 8, chỉ lưu bản ghi vào lịch sử giá ở bước 7. Hệ thống giữ nguyên đơn giá hiện hành và giá trị thị trường của các lệnh mua, rồi chuyển sang bước 9 của luồng chính theo `FR-INV-02` quy tắc 5.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Giá trị định giá không hợp lệ

1. Tại bước 4 của luồng chính, hệ thống phát hiện $NewPrice < 0$, $MarketValue_{total} < 0$, hoặc người dùng nhập cả hai giá trị hoặc không nhập giá trị nào.
2. Hệ thống từ chối cập nhật. Nếu $NewPrice < 0$, hệ thống báo lỗi `ERR-INV-001`: "Đơn giá mua phải lớn hơn 0, đơn giá bán và đơn giá hiện hành không được âm." Nếu nhập cả hai giá trị hoặc không nhập giá trị nào, hệ thống báo lỗi nghiệp vụ thiếu hoặc thừa giá trị định giá. Nếu $MarketValue_{total} < 0$, hệ thống báo lỗi nghiệp vụ giá trị thị trường tổng không hợp lệ.
3. Người dùng nhập lại đúng một giá trị hợp lệ để tiếp tục.

##### Luồng ngoại lệ 4b: Ngày định giá vượt quá ngày hiện tại

1. Tại bước 4 của luồng chính, hệ thống phát hiện Ngày định giá sau ngày hiện tại.
2. Hệ thống từ chối cập nhật và báo lỗi nghiệp vụ ngày định giá không hợp lệ.
3. Người dùng điều chỉnh lại ngày định giá không vượt quá ngày hiện tại.

##### Luồng ngoại lệ 4c: Tài sản đầu tư không tìm thấy hoặc đã bán hết

1. Tại bước 4 của luồng chính, hệ thống phát hiện tên tài sản đầu tư không có trong hệ thống, hoặc tài sản không còn lệnh mua nào còn đơn vị nắm giữ.
2. Hệ thống từ chối cập nhật và báo lỗi nghiệp vụ không tìm thấy tài sản đầu tư đang nắm giữ theo `FR-INV-02` quy tắc 1.
3. Người dùng chọn lại tài sản đầu tư đang nắm giữ.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc định giá và giá vốn tài sản đầu tư `BR-FIN-06`: Giá trị thị trường $MarketValue_{total}$ luôn bằng số đơn vị còn lại nhân với đơn giá hiện hành. Đơn giá hiện hành mới là đơn giá mua của lệnh mua có ngày mua mới nhất không cũ hơn lần định giá gần nhất, trường hợp cùng ngày mua thì lấy lệnh có số thứ tự ghi lớn nhất.
2. Quy tắc liên kết tài sản quỹ mục tiêu `BR-FIN-10`: Định giá lại tài sản đầu tư kéo theo cập nhật đồng bộ giá trị đóng góp vào quỹ mục tiêu liên kết.
3. Yêu cầu chức năng liên quan: `FR-INV-02`.

---

### 6.28 UC-INV-04: Định giá tài sản đầu tư và xác định tỷ suất sinh lời

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-INV-04 |
| Tên | Định giá tài sản đầu tư và xác định tỷ suất sinh lời |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Đánh giá toàn diện giá trị thị trường, lãi lỗ đã chốt, lãi lỗ chưa chốt, tỷ suất cả lệnh, tỷ suất phần đang giữ và tỷ suất nội hoàn. |
| Mô tả tóm tắt | Tính toán và phân tích giá trị thị trường, hai đại lượng lãi lỗ bằng tiền và các tỷ suất sinh lời theo từng lệnh mua, tài sản đầu tư, khoản đầu tư hoặc toàn bộ danh mục theo `BR-FIN-06`, `BR-FIN-08` và `FR-INV-03`. |
| Điều kiện kích hoạt | Người dùng truy cập chức năng xem báo cáo hiệu suất hoặc định giá danh mục đầu tư. |

#### Tiền điều kiện

1. Hệ thống có ít nhất một lệnh mua tài sản đầu tư đã được ghi nhận.
2. Người dùng đang ở giao diện quản lý tài sản đầu tư hoặc báo cáo hiệu suất.

#### Bảo đảm tối thiểu

1. Hệ thống hoạt động ở chế độ chỉ đọc, không làm biến đổi bất kỳ dữ liệu lệnh mua, đơn giá hay số dư ví nào trong hệ thống.

#### Hậu điều kiện thành công

1. Toàn bộ các chỉ tiêu giá trị thị trường, lãi lỗ đã chốt, lãi lỗ chưa chốt, tỷ suất cả lệnh, tỷ suất phần đang giữ và tỷ suất nội hoàn được tính toán chính xác theo `BR-FIN-08`.
2. Dữ liệu báo cáo hiển thị trực quan, đầy đủ các nhãn giải thích kỳ nắm giữ và ghi chú giải thích đời thường khi IRR ở trạng thái "N/A" theo `QD-120`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Định giá tài sản đầu tư và xác định tỷ suất sinh lời".
2. Người dùng chọn phạm vi xem: Tài sản đầu tư, Khoản đầu tư hoặc Tất cả (mặc định Tất cả), kèm tên tài sản hoặc tên khoản tương ứng nếu chọn phạm vi hẹp.
3. Hệ thống xác định danh sách các lệnh mua thuộc phạm vi, nếu phạm vi không có lệnh mua nào thì kích hoạt luồng ngoại lệ 4d. Hệ thống xác định mốc thời gian tính toán $Date_{calc}$ cho từng lệnh mua theo `BR-FIN-08`:
   - Lệnh mua còn đơn vị: $Date_{calc}$ là ngày hiện tại.
   - Lệnh mua đã bán hết: $Date_{calc}$ là ngày của lệnh bán cuối cùng khấu trừ lệnh mua đó.
4. Hệ thống kiểm tra tính nhất quán dữ liệu: Nếu $Date_{calc}$ trước ngày mua của lệnh, kích hoạt luồng ngoại lệ 4a (`ERR-INV-007`).
5. Với từng lệnh mua $i$, hệ thống tính toán các chỉ số thành phần:
   - Giá trị thị trường $MarketValue_{remain,i}$ theo `BR-FIN-06`:
     - Lệnh mua còn đơn vị: $MarketValue_{remain,i} = Units_{remain,i} \times CurrentPrice$.
     - Lệnh mua đã bán hết: $MarketValue_{remain,i} = 0.00$ VNĐ.
   - Lãi lỗ đã chốt: $RealizedPL_i = Proceeds_{sold,i} - CostBasis_{sold,i} + RealizedPL_{late,i}$, trong đó $RealizedPL_{late,i}$ là phần hoàn vốn vượt giá vốn còn lại của lệnh mua $i$ (`BR-FIN-06` mục 3.6 điều 6), với tiền bán thực nhận $Proceeds_{sold,i}$ phân bổ theo đơn vị khấu trừ. Phần dư dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua bị khấu trừ (`QD-194`).
   - Lãi lỗ chưa chốt: $UnrealizedPL_i = MarketValue_{remain,i} - CostBasis_{remain,i}$ (bằng 0.00 VNĐ nếu đã bán hết).
   - Số ngày nắm giữ: $HoldDays_i = Date_{calc} - Date_{buy}$.
   - Tỷ suất cả lệnh $ROI_{order,i}$: đo lường toàn bộ giá gốc ban đầu $CostBasis_{origin,i}$. Nếu $HoldDays < 365$, hiển thị tỷ suất đơn giản; nếu $HoldDays \ge 365$, quy đổi tỷ suất kép theo năm $CAGR_{order,i}$.
   - Tỷ suất phần đang giữ $ROI_{holding,i}$: đo lường riêng phần đơn vị còn lại so với giá vốn còn lại $CostBasis_{remain,i}$, chỉ tính khi lệnh mua còn đơn vị nắm giữ ($Units_{remain,i} > 0$), luôn là tỷ lệ phần trăm đơn không quy đổi năm.
   - Hiển thị tỷ suất: Nếu lệnh mua chưa từng bán, chưa nhận tiền, chưa hoàn vốn và $HoldDays < 365$, hệ thống chỉ hiển thị một dòng tỷ suất. Khi $HoldDays \ge 365$, tỷ suất cả lệnh được quy đổi kép theo năm còn tỷ suất phần đang giữ là tỷ suất đơn nên hiển thị cả hai dòng theo QD-242. Nếu lệnh mua đã bán một phần, đã nhận tiền hoặc đã hoàn vốn, hệ thống hiển thị đồng thời cả hai chỉ số theo `QD-141`.
6. Hệ thống tổng hợp các chỉ tiêu theo phạm vi xem bằng phép cộng chính xác các đại lượng thành phần. Các chỉ tiêu gồm: Tổng giá gốc, Tổng giá vốn còn lại, Tổng giá trị thị trường, Tổng lãi lỗ đã chốt, Tổng lãi lỗ chưa chốt, và Tỷ suất sinh lời trên tổng giá gốc $ROI_{total}$ (phần trăm đơn, không quy năm).
7. Hệ thống tính tỷ suất nội hoàn trên chuỗi dòng tiền thực tế theo `BR-FIN-08` khoản 5:
   - Dòng tiền ra là giá gốc từng lệnh mua; dòng tiền vào là tiền bán thực nhận, cổ tức tiền mặt, hoàn vốn; dòng tiền cuối kỳ $t_n$ là tổng giá trị thị trường các lệnh mua còn đơn vị (hoặc ngày dòng tiền cuối nếu đã bán hết).
   - Thuật toán lặp số học tìm nghiệm $r$ với dung sai sai số $10^{-6}$, tối đa 100 vòng lặp.
   - Nếu $t_n - t_0 < 365$: Hiển thị tỷ suất kỳ tích lũy thực tế $IRR_{period} = (1 + r)^{\frac{t_n - t_0}{365}} - 1$, không quy năm theo `QD-118`.
   - Nếu $t_n - t_0 \ge 365$: Hiển thị tỷ suất kép năm $r$.
   - Nếu thuật toán không hội tụ sau 100 vòng lặp, đa nghiệm hoặc chuỗi dòng tiền một chiều, hệ thống hiển thị "N/A" kèm ghi chú giải thích đời thường theo `QD-120`.
     - Ghi chú có nội dung: "Lịch sử nạp và rút tiền xen kẽ nhiều lần khiến hệ thống không quy đổi được về một mức lãi suất năm tương đương duy nhất. Bạn hãy theo dõi số tiền lãi lỗ thực tế và tỷ lệ lãi trên tổng vốn để đánh giá hiệu quả."
     - Hệ thống vẫn hiển thị đầy đủ tổng lãi lỗ thực tế và tỷ suất trên tổng giá gốc.
8. Hệ thống làm tròn toàn bộ các chỉ số tỷ suất thành phần trăm 2 chữ số thập phân theo quy tắc làm tròn ngân hàng nửa chẵn `HALF_EVEN` theo `QD-121`.
9. Hệ thống hiển thị bảng phân tích hiệu suất chi tiết và biểu đồ trực quan.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Dữ liệu ngày tính toán không nhất quán với ngày mua

1. Tại bước 4 của luồng chính, hệ thống phát hiện $Date_{calc} < Date_{buy}$ của một lệnh mua trong phạm vi.
2. Hệ thống từ chối tính toán toàn bộ phạm vi chứa lệnh mua đó và báo lỗi `ERR-INV-007` kèm mã của lệnh mua không nhất quán: "Dữ liệu ngày tính toán không nhất quán với ngày mua của lệnh."
3. Người dùng kiểm tra và điều chỉnh lại dữ liệu giao dịch của lệnh mua.

##### Luồng ngoại lệ 4b: Mẫu số tính tỷ suất bằng 0

1. Tại bước 5 của luồng chính, hệ thống phát hiện giá gốc hoặc giá vốn còn lại của lệnh mua bằng 0 khi tính tỷ suất.
2. Hệ thống hiển thị tỷ suất tương ứng ở trạng thái "N/A" theo `ERR-INV-008`. Lãi lỗ vẫn tính theo công thức.
3. Người dùng tiếp tục theo dõi các chỉ tiêu số tiền lãi lỗ.

##### Luồng ngoại lệ 4c: Tên tài sản đầu tư hoặc khoản đầu tư không tồn tại

1. Tại bước 2 của luồng chính, người dùng chọn phạm vi Tài sản đầu tư hoặc Khoản đầu tư nhưng nhập tên không có trong hệ thống.
2. Hệ thống từ chối thực hiện và báo lỗi nghiệp vụ không tìm thấy tài sản đầu tư hoặc khoản đầu tư.
3. Người dùng chọn lại tên hợp lệ từ danh sách có sẵn.

##### Luồng ngoại lệ 4d: Phạm vi không có lệnh mua

1. Tại bước 3 của luồng chính, hệ thống phát hiện phạm vi đã chọn không có lệnh mua nào.
2. Hệ thống hiển thị lãi lỗ bằng 0.00 VNĐ, tỷ suất và tỷ suất nội hoàn ở trạng thái "N/A" kèm thông điệp "Phạm vi đã chọn chưa có lệnh mua nào nên chưa tính được tỷ suất.", không gán mã lỗi theo `QD-227`.
3. Người dùng gán lệnh mua vào phạm vi hoặc chọn phạm vi khác.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc định giá và giá vốn tài sản đầu tư `BR-FIN-06`: Định giá Mark-to-Market theo đơn giá hiện hành cho mọi tài sản đầu tư.
2. Quy tắc tỷ suất sinh lời của tài sản đầu tư `BR-FIN-08`: Ngưỡng 365 ngày của tỷ suất từng lệnh mua và tỷ suất nội hoàn IRR; hiển thị đồng thời 2 tỷ suất sinh lời khi có sự kiện hoặc bán một phần theo `QD-141`; cơ chế xử lý IRR N/A giải thích đời thường theo `QD-120`; làm tròn nửa chẵn theo `QD-121`.
3. Yêu cầu chức năng liên quan: `FR-INV-03`.

---

### 6.29 UC-INV-05: Ghi nhận sự kiện của tài sản đầu tư

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-INV-05 |
| Tên | Ghi nhận sự kiện của tài sản đầu tư |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Ghi nhận các quyền lợi tài chính phát sinh từ tài sản đầu tư (cổ tức tiền, đơn vị thưởng, hoàn vốn), phân bổ cho các lệnh mua và quản lý thu nhập hoặc giá vốn chính xác. |
| Mô tả tóm tắt | Ghi nhận sự kiện thuộc một trong ba dạng: nhận tiền mặt, thêm đơn vị cùng loại hoặc hoàn vốn cho tài sản đầu tư theo `BR-FIN-07` và `FR-INV-05`; phân bổ giá trị cho các lệnh mua theo số đơn vị nắm giữ ngay trước sự kiện; cập nhật số dư ví, giá vốn bình quân, giá vốn còn lại hoặc lãi lỗ đã chốt có lưu dấu vết. |
| Điều kiện kích hoạt | Người dùng thực hiện ghi nhận quyền lợi hoặc sự kiện phân phối từ tài sản đầu tư. |

#### Tiền điều kiện

1. Tài sản đầu tư đã tồn tại và có ít nhất một lệnh mua trong hệ thống.
2. Đối với dạng Nhận tiền mặt và Hoàn vốn, hệ thống có ít nhất một ví tiền ở trạng thái `ACTIVE`.
3. Người dùng đang ở chức năng quản lý tài sản đầu tư.

#### Bảo đảm tối thiểu

1. Nếu thao tác ghi nhận sự kiện bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, không có sự kiện nào được ghi nhận; số dư ví, số đơn vị và giá vốn của các lệnh mua được bảo toàn nguyên vẹn.

#### Hậu điều kiện thành công

1. Bản ghi sự kiện độc lập được lưu thành công kèm định danh riêng, Thời điểm ghi và số thứ tự ghi do hệ thống tự ghi. Bảng phân bổ giá trị cho từng lệnh mua của tài sản đầu tư suy ra từ thứ tự xử lý và tính lại khi có giao dịch ghi lùi ngày.
2. Với dạng Nhận tiền mặt: Số dư ví nhận tiền tăng đúng số tiền thực nhận theo `BR-FIN-02` (số hạng $InvEvent_w(t)$); thu nhập tháng chứa Ngày nhận tăng tương ứng và hiển thị phân loại riêng ở dòng "trong đó thu nhập đầu tư" theo `FR-REP-01`.
3. Với dạng Thêm đơn vị cùng loại: Số đơn vị nắm giữ của tài sản tăng; giá vốn bình quân của từng đơn vị giảm; tổng giá vốn, số dư ví và thu nhập tháng không đổi. Nếu Ngày nhận sớm hơn một lệnh bán hoặc một sự kiện đã ghi thì các lệnh bán đứng sau được tính lại giá vốn xuất bán và lãi lỗ đã chốt. Phân bổ của các sự kiện đứng sau cũng được tính lại, kèm dấu vết.
4. Với dạng Hoàn vốn:
   - Số dư ví nhận tiền tăng số tiền hoàn vốn theo `BR-FIN-02` (số hạng $InvEvent_w(t)$), thu nhập tháng không đổi.
   - Giá vốn còn lại của các lệnh mua giảm tương ứng, không xuống dưới 0.
   - Phần vượt giá vốn còn lại (toàn bộ khoản hoàn vốn nếu tài sản đã bán hết ngay trước sự kiện) được cộng vào lãi đã chốt của tài sản, và các lệnh bán đứng trước sự kiện giữ nguyên.
   - Nếu Ngày nhận sớm hơn một lệnh bán hoặc một sự kiện đã ghi, các lệnh bán đứng sau được tính lại giá vốn xuất bán và lãi lỗ đã chốt, cùng phân bổ của các sự kiện đứng sau, kèm dấu vết.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Ghi nhận sự kiện của tài sản đầu tư".
2. Hệ thống hiển thị biểu mẫu ghi nhận sự kiện gồm các trường thông tin:
   - Tên tài sản đầu tư (chọn từ danh sách hoặc nhập tên, so khớp không phân biệt chữ hoa chữ thường).
   - Dạng sự kiện (Nhận tiền mặt, Thêm đơn vị cùng loại hoặc Hoàn vốn, áp dụng cho mọi tài sản đầu tư).
   - Ngày nhận (định dạng YYYY-MM-DD, không sau ngày hiện tại).
   - Số tiền ($Amount > 0$, bắt buộc với Nhận tiền mặt và Hoàn vốn).
   - Số đơn vị nhận thêm ($Units_{bonus} > 0$, tối đa 4 chữ số thập phân, bắt buộc với Thêm đơn vị cùng loại).
   - Ví nhận tiền (bắt buộc với Nhận tiền mặt và Hoàn vốn, danh sách ví ở trạng thái `ACTIVE`).
3. Người dùng nhập các thông tin cần thiết và xác nhận thực hiện ghi nhận sự kiện.
4. Hệ thống kiểm tra các ràng buộc nghiệp vụ:
   - Tìm tài sản đầu tư theo tên (bỏ khoảng trắng đầu cuối, không phân biệt chữ hoa chữ thường).
   - Kiểm tra sự kiện có ít nhất một lệnh mua đứng trước theo thứ tự xử lý (ngày giao dịch, rồi số thứ tự ghi), với cả ba dạng sự kiện, nếu vi phạm báo lỗi `ERR-INV-004`.
   - Kiểm tra Ngày nhận không vượt quá ngày hiện tại, nếu vi phạm báo lỗi `ERR-INV-016` theo luồng ngoại lệ 4d.
   - Kiểm tra số tiền hoặc số đơn vị nhận thêm lớn hơn 0, nếu vi phạm báo lỗi `ERR-INV-006`.
   - Tính số đơn vị nắm giữ ngay trước sự kiện ($\sum Units_{held,j}$, gồm đơn vị mua, cộng đơn vị nhận thêm, trừ đơn vị đã bán của các giao dịch đứng trước). Nếu bằng 0 vì tài sản đã bán hết, hệ thống chia theo số đơn vị ngay trước lệnh bán gần nhất làm tài sản hết đơn vị và vẫn ghi nhận sự kiện.
   - Với dạng Hoàn vốn, nếu số tiền hoàn vốn phân bổ cho một lệnh mua vượt giá vốn còn lại của lệnh mua đó ngay trước sự kiện ($ReturnCapital_i > CostBasis_{remain,i}$), hệ thống không từ chối. Thay vào đó, hệ thống hiển thị cảnh báo để người dùng xác nhận (luồng 5c).
   - Với Nhận tiền mặt và Hoàn vốn, kiểm tra ví nhận tiền ở trạng thái `ACTIVE`, nếu vi phạm báo lỗi `ERR-INV-015` theo luồng ngoại lệ 4c.
5. Hệ thống xử lý phân bổ và cập nhật dữ liệu theo từng dạng sự kiện (rẽ nhánh theo luồng 5a, 5b hoặc 5c).
6. Nếu có cảnh báo phần vượt hoặc phải tính lại các giao dịch đã ghi, hệ thống hiển thị tác động và yêu cầu người dùng xác nhận. Nếu người dùng hủy bỏ, hệ thống dừng giao dịch và giữ nguyên dữ liệu, số dư ví không đổi.
7. Hệ thống lưu bản ghi sự kiện mới độc lập kèm Thời điểm ghi và số thứ tự ghi do hệ thống tự ghi, cập nhật các lệnh mua và lệnh bán liên quan (nếu có).
8. Hệ thống hiển thị thông báo ghi nhận sự kiện thành công kèm bảng phân bổ giá trị cho từng lệnh mua và danh sách lệnh bán bị tính lại (nếu có). Nếu giao dịch cùng ngày với giao dịch đã ghi, thông báo nêu giao dịch được tính sau những giao dịch nào.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 5a: Dạng nhận tiền mặt

1. Tại bước 5 của luồng chính, hệ thống xác định số dư ví nhận tiền tăng (áp dụng khi lưu ở bước 7):
$$WalletBalance_{wallet}(\text{sau}) = WalletBalance_{wallet}(\text{trước}) + Amount_{cash}$$
2. Hệ thống ghi nhận $Amount_{cash}$ vào thu nhập tháng của Ngày nhận với dòng phân loại riêng thể hiện thu nhập đầu tư theo `FR-REP-01`.
3. Hệ thống phân bổ $Amount_{cash}$ cho từng lệnh mua $i$ theo `BR-FIN-07`:
   - Phân bổ theo tỷ lệ đơn vị nắm giữ ngay trước sự kiện ($Units_{held,i}$), hoặc ngay trước lệnh bán gần nhất làm tài sản hết đơn vị.
   - Số tiền phân bổ làm tròn 2 chữ số thập phân, phần dư dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua được chia.
4. Sự kiện nhận tiền mặt không đổi giá vốn của lệnh mua nên không tính lại lệnh bán nào. Hệ thống tiếp tục bước 6 của luồng chính.

##### Luồng rẽ nhánh 5b: Dạng thêm đơn vị cùng loại

1. Tại bước 5 của luồng chính, hệ thống giữ nguyên số dư ví tiền và tổng giá vốn của tài sản.
2. Hệ thống phân bổ số đơn vị nhận thêm $Units_{bonus}$ cho từng lệnh mua $i$ theo tỷ lệ đơn vị ngay trước sự kiện, làm tròn 4 chữ số thập phân. Phần dư dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua được chia.
3. Hệ thống tính lại giá vốn bình quân của từng lệnh mua $i$:
$$UnitPrice'_{cost,i} = \frac{CostBasis_{remain,i}}{Units_{held,i} + Units_{bonus,i}}$$
   Lệnh mua $i$ không còn đơn vị ngay trước sự kiện ($Units_{held,i} = 0$) và không nhận đơn vị thêm ($Units_{bonus,i} = 0$) theo cơ sở phân bổ của sự kiện, kể cả khi tài sản đã hết đơn vị, không áp dụng công thức này: hệ thống không tính và không hiển thị giá vốn bình quân của lệnh đó, lệnh giữ 0 đơn vị và giá vốn còn lại 0, sự kiện vẫn được ghi (QD-260).
4. Khi tài sản đã hết đơn vị ngay trước sự kiện, đơn vị nhận thêm có giá vốn 0 và hệ thống không tính lại các lệnh bán đứng trước sự kiện. Nếu Ngày nhận sớm hơn một lệnh bán hoặc một sự kiện đã ghi, hệ thống tính lại các lệnh bán đứng sau và phân bổ của các sự kiện đứng sau theo cơ chế ghi lùi ngày của `FR-INV-04`. Khi đó, hệ thống hiển thị tác động, cảnh báo quỹ mục tiêu, chờ xác nhận và lưu dấu vết.
5. Hệ thống tiếp tục bước 6 của luồng chính.

##### Luồng rẽ nhánh 5c: Dạng hoàn vốn

1. Tại bước 5 của luồng chính, hệ thống xác định số dư ví nhận tiền tăng theo $Amount_{return}$ (áp dụng khi lưu ở bước 7), không tính vào thu nhập tháng.
2. Hệ thống phân bổ $Amount_{return}$ cho từng lệnh mua $i$ theo tỷ lệ đơn vị ngay trước sự kiện, làm tròn 2 chữ số thập phân. Phần dư dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua được chia.
3. Khi tài sản còn đơn vị ngay trước sự kiện, phần lệnh mua còn nắm giữ làm giảm giá vốn còn lại, không xuống dưới 0:
$$CostBasis'_{remain,i} = \max(CostBasis_{remain,i} - ReturnCapital_i,\ 0)$$
4. Khi tài sản đã hết đơn vị ngay trước sự kiện, giá vốn còn lại bằng 0 nên khoản hoàn vốn được cộng vào lãi đã chốt của tài sản tại Ngày nhận. Các lệnh bán đứng trước sự kiện giữ nguyên giá vốn xuất bán và lãi lỗ đã chốt. Hệ thống hiển thị cảnh báo nêu giá vốn còn lại 0.00 VNĐ và phần vượt, chỉ lưu khi người dùng xác nhận.
5. Nếu $ReturnCapital_i > CostBasis_{remain,i}$ ở một lệnh mua, hệ thống hiển thị cảnh báo nêu giá vốn còn lại và phần vượt, chỉ lưu khi người dùng xác nhận. Giá vốn còn lại của lệnh mua đó về 0 và phần vượt được cộng vào lãi đã chốt của tài sản tại Ngày nhận.
6. Nếu Ngày nhận sớm hơn một lệnh bán hoặc một sự kiện đã ghi, hệ thống tính lại các lệnh bán đứng sau và phân bổ của các sự kiện đứng sau theo cơ chế ghi lùi ngày của `FR-INV-04`. Khi đó, hệ thống hiển thị tác động trước và sau, cảnh báo quỹ mục tiêu, chờ xác nhận và lưu dấu vết. Nếu phần vượt phát sinh do tính lại, cảnh báo nằm trong cùng bước xác nhận.
7. Hệ thống tiếp tục bước 6 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Sự kiện đứng trước lệnh mua đầu tiên

1. Tại bước 4 của luồng chính, nếu không có lệnh mua nào đứng trước sự kiện theo thứ tự xử lý, hệ thống từ chối cả ba dạng sự kiện. Khi đó, hệ thống báo lỗi `ERR-INV-004`: "Sự kiện không thể đứng trước lệnh mua đầu tiên của tài sản đầu tư."
2. Người dùng điều chỉnh lại ngày nhận để tiếp tục.

##### Luồng ngoại lệ 4b: Số tiền hoặc số đơn vị không hợp lệ

1. Tại bước 4 của luồng chính, nếu $Amount \le 0$ hoặc $Units_{bonus} \le 0$, hệ thống từ chối và báo lỗi `ERR-INV-006`: "Số tiền hoặc số đơn vị của sự kiện phải lớn hơn 0."
2. Người dùng nhập lại giá trị hợp lệ.

##### Luồng ngoại lệ 4c: Ví nhận tiền không ở trạng thái hoạt động

1. Tại bước 4 của luồng chính, nếu ví nhận tiền đã lưu trữ hoặc không ở trạng thái `ACTIVE`, hệ thống từ chối và báo lỗi `ERR-INV-015`: "Ví nhận tiền không ở trạng thái hoạt động. Vui lòng chọn ví khác."
2. Người dùng chọn ví tiền khác ở trạng thái `ACTIVE`.

##### Luồng ngoại lệ 4d: Ngày nhận trong tương lai

1. Tại bước 4 của luồng chính, nếu Ngày nhận vượt quá ngày hiện tại, hệ thống từ chối và báo lỗi `ERR-INV-016`: "Ngày nhận không được sau ngày hiện tại."
2. Người dùng nhập ngày nhận hợp lệ.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc số tiền và làm tròn `BR-FIN-01`.
2. Quy tắc biến động số dư ví `BR-FIN-02`.
3. Quy tắc sự kiện của tài sản đầu tư `BR-FIN-07`: Quản lý 3 dạng sự kiện theo Ngày nhận, phân bổ cho lệnh mua, tính lại có dấu vết và tách riêng dòng thu nhập đầu tư. Cổ tức thuộc kỳ trước ngày mua đầu tiên được ghi nhận dưới dạng Hoàn vốn để giảm giá vốn của lệnh mua theo `QD-76` và `QD-242`.
4. Yêu cầu chức năng liên quan: `FR-INV-05`, `FR-REP-01`.

---

### 6.30 UC-INV-06: Tạo khoản đầu tư

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-INV-06 |
| Tên | Tạo khoản đầu tư |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Cho phép người dùng tự nhóm các lệnh mua thành khoản đầu tư tùy chỉnh (ví dụ: Hưu trí, Dự phòng, Tích lũy) để theo dõi lãi lỗ và tỷ suất sinh lời theo mục đích cá nhân. |
| Mô tả tóm tắt | Tạo khoản đầu tư mới với tên hợp lệ và gán các lệnh mua được chọn (nếu có) vào khoản theo `FR-INV-06` và `FR-INV-03`, không làm phát sinh dòng tiền. |
| Điều kiện kích hoạt | Người dùng truy cập phân hệ quản lý tài sản đầu tư và kích hoạt chức năng tạo khoản đầu tư mới. |

#### Tiền điều kiện

1. Hệ thống hoạt động bình thường.
2. Người dùng đang ở chức năng quản lý tài sản đầu tư.

#### Bảo đảm tối thiểu

1. Hệ thống không tạo khoản đầu tư mới; trạng thái của các lệnh mua và các khoản đầu tư hiện có giữ nguyên vẹn.

#### Hậu điều kiện thành công

1. Khoản đầu tư mới được ghi nhận vào hệ thống; các lệnh mua được chọn được gắn liên kết vào khoản này; nếu lệnh mua chuyển từ khoản khác sang thì liên kết cũ được gỡ bỏ; số dư ví tiền, giá gốc, giá vốn, số đơn vị, lãi lỗ của từng lệnh mua và liên kết quỹ mục tiêu không thay đổi; tổng các khoản cộng với lệnh mua chưa gán vẫn bằng tổng toàn bộ tài sản đầu tư.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng tạo mới khoản đầu tư.
2. Hệ thống hiển thị biểu mẫu tạo khoản đầu tư gồm trường nhập tên khoản đầu tư và danh sách các lệnh mua hiện có trong hệ thống. Danh sách hiển thị rõ tên tài sản, ngày mua, số đơn vị, giá gốc và khoản đầu tư hiện tại nếu đã được gán.
3. Người dùng nhập tên khoản đầu tư và có thể tích chọn một hoặc nhiều lệnh mua cần gán vào khoản mới này.
4. Người dùng xác nhận gửi yêu cầu lưu khoản đầu tư.
5. Hệ thống loại bỏ khoảng trắng thừa ở hai đầu của tên khoản đầu tư và kiểm tra độ dài:
   - Nếu độ dài không thuộc khoảng từ 1 đến 50 ký tự, hệ thống kích hoạt Luồng ngoại lệ 5a (`ERR-INV-012`).
   - Nếu tên khoản trùng với một khoản đầu tư đã có trong hệ thống (so khớp không phân biệt chữ hoa, chữ thường), hệ thống kích hoạt Luồng ngoại lệ 5b (`ERR-INV-013`).
6. Hệ thống kiểm tra danh sách lệnh mua được chọn:
   - Nếu người dùng không chọn lệnh mua nào, hệ thống tiếp tục bước 8 (cho phép tạo khoản rỗng theo Luồng rẽ nhánh 6a).
   - Nếu danh sách chứa mã định danh lệnh mua không tồn tại trong hệ thống, hệ thống kích hoạt Luồng ngoại lệ 6b (`ERR-INV-014`).
   - Nếu có mã lệnh mua trùng lặp trong danh sách đã chọn, hệ thống tự động loại bỏ trùng lặp để mỗi lệnh mua chỉ được gán một lần duy nhất.
7. Đối với các lệnh mua đang thuộc về một khoản đầu tư khác, hệ thống kích hoạt Luồng rẽ nhánh 7a để yêu cầu người dùng xác nhận việc chuyển khoản.
8. Hệ thống lưu khoản đầu tư mới với tên đã chuẩn hóa, đồng thời thực hiện gán các lệnh mua được chọn vào khoản này (trọn vẹn hoặc không làm gì).
9. Hệ thống thông báo tạo khoản đầu tư thành công, nêu tên khoản và số lượng lệnh mua đã gán. Hệ thống đồng thời hiển thị tổng giá gốc, tổng giá trị thị trường, lãi lỗ và tỷ suất sinh lời trên tổng giá gốc của khoản theo quy tắc `FR-INV-03`, `BR-FIN-06` và `BR-FIN-08`. Khoản không có lệnh mua thì các chỉ số tiền bằng 0.00 VNĐ và tỷ suất ở trạng thái "N/A" theo `QD-227`.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 6a: Tạo khoản đầu tư rỗng không có lệnh mua

1. Tại bước 6 của luồng chính, nếu người dùng chỉ nhập tên khoản mà không tích chọn bất kỳ lệnh mua nào, hệ thống tiếp tục xử lý tạo khoản.
2. Hệ thống lưu khoản đầu tư mới với danh sách lệnh mua rỗng. Các chỉ số của khoản hiển thị theo bước 9 của luồng chính.

##### Luồng rẽ nhánh 7a: Xác nhận chuyển lệnh mua đang thuộc khoản khác

1. Tại bước 7 của luồng chính, nếu phát hiện một hoặc nhiều lệnh mua được chọn đang thuộc khoản đầu tư khác, hệ thống hiển thị hộp thoại xác nhận. Hộp thoại nêu rõ mã lệnh mua, tên tài sản và tên khoản đầu tư hiện tại của từng lệnh mua.
2. Người dùng kiểm tra thông tin và chọn xác nhận chuyển sang khoản đầu tư mới.
3. Hệ thống tiến hành gỡ bỏ liên kết của lệnh mua khỏi khoản đầu tư cũ và gán sang khoản đầu tư mới tại bước 8.
4. Nếu người dùng từ chối chuyển khoản, hệ thống kích hoạt Luồng ngoại lệ 7b.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Tên khoản đầu tư không đúng độ dài

1. Tại bước 5 của luồng chính, nếu tên khoản đầu tư để trống hoặc có độ dài sau khi cắt khoảng trắng vượt quá 50 ký tự, hệ thống từ chối yêu cầu. Khi đó, hệ thống báo lỗi `ERR-INV-012`: "Tên khoản đầu tư phải có độ dài từ 1 đến 50 ký tự."
2. Người dùng nhập lại tên khoản đầu tư hợp lệ.

##### Luồng ngoại lệ 5b: Trùng tên khoản đầu tư đã tồn tại

1. Tại bước 5 của luồng chính, nếu tên khoản đầu tư sau khi cắt khoảng trắng trùng với tên của một khoản đầu tư đã có trong hệ thống (so khớp không phân biệt chữ hoa, chữ thường), hệ thống từ chối yêu cầu. Khi đó, hệ thống báo lỗi `ERR-INV-013`: "Tên khoản đầu tư đã tồn tại trong hệ thống."
2. Người dùng đổi sang tên khác không bị trùng lặp.

##### Luồng ngoại lệ 6b: Mã lệnh mua không tồn tại trong hệ thống

1. Tại bước 6 của luồng chính, nếu danh sách lệnh mua đã chọn chứa mã định danh không tồn tại trong hệ thống, hệ thống từ chối toàn bộ thao tác. Khi đó, hệ thống thông báo lỗi `ERR-INV-014`: "Thao tác gán lệnh mua không hợp lệ hoặc chứa dữ liệu mâu thuẫn." kèm mã gây lỗi.
2. Hệ thống hủy bỏ toàn bộ thao tác tạo khoản, không thay đổi bất kỳ liên kết nào.

##### Luồng ngoại lệ 7b: Người dùng từ chối xác nhận chuyển lệnh mua từ khoản khác

1. Tại bước 4 của Luồng rẽ nhánh 7a, nếu người dùng chọn hủy bỏ hoặc đóng hộp thoại xác nhận chuyển khoản, hệ thống hủy toàn bộ thao tác tạo khoản, không làm gì.
2. Hệ thống không tạo khoản đầu tư mới, không gỡ bỏ bất kỳ lệnh mua nào khỏi khoản cũ và giữ nguyên giao diện biểu mẫu để người dùng điều chỉnh lựa chọn.

#### Quy tắc nghiệp vụ đặc thù

1. Bản chất quản lý phân nhóm logic theo `FR-INV-06`: Khoản đầu tư là cấu trúc nhóm logic do người dùng tự định nghĩa nhằm mục đích theo dõi danh mục và hiệu quả đầu tư; thao tác tạo khoản không làm phát sinh giao dịch tiền mặt, không làm biến động số dư ví, không làm thay đổi giá gốc, giá vốn, số đơn vị nắm giữ hay lãi lỗ của bất kỳ lệnh mua nào.
2. Độc quyền gán lệnh mua (`BR-FIN-06`, `QD-139`, `QD-143`): Mỗi lệnh mua tại một thời điểm chỉ thuộc tối đa một khoản đầu tư duy nhất. Việc gán lệnh mua sang khoản mới sẽ tự động hủy liên kết của lệnh mua đó ở khoản cũ sau khi có xác nhận của người dùng.
3. Thao tác trọn vẹn hoặc không làm gì: Thao tác tạo khoản và gán tập hợp lệnh mua được thực thi trọn vẹn hoặc bị hủy toàn bộ. Nếu có một mã lệnh mua không hợp lệ hoặc người dùng từ chối một lần chuyển khoản, toàn bộ thao tác tạo khoản sẽ bị hủy bỏ.
4. Độc lập với liên kết Quỹ mục tiêu (`BR-FIN-10`, `QD-102`): Việc gán lệnh mua vào khoản đầu tư hoàn toàn độc lập với liên kết giữa lệnh mua và quỹ mục tiêu. Việc gán này không làm thay đổi hay gỡ bỏ liên kết quỹ mục tiêu của lệnh mua.
5. Phạm vi lệnh mua được gán (`FR-INV-06`): lệnh mua của tài sản đầu tư bất kỳ đều được gán, kể cả lệnh mua đã bán hết; lãi lỗ đã chốt của lệnh mua đã bán hết được tính vào lãi lỗ đã chốt của khoản.
6. Yêu cầu chức năng liên quan: `FR-INV-06`, `FR-INV-03`.

---

### 6.31 UC-INV-07: Sửa khoản đầu tư

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-INV-07 |
| Tên | Sửa khoản đầu tư |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Cho phép người dùng điều chỉnh cách nhóm các lệnh mua trong khoản đầu tư mà không làm thay đổi tiền, giá gốc hay giá vốn của bất kỳ lệnh mua nào. |
| Mô tả tóm tắt | Đổi tên khoản, gán thêm hoặc bỏ gán lệnh mua trong một thao tác duy nhất theo `FR-INV-06` và `FR-INV-03`, không làm phát sinh dòng tiền. |
| Điều kiện kích hoạt | Người dùng chọn một khoản đầu tư đã có và kích hoạt chức năng sửa. |

#### Tiền điều kiện

1. Hệ thống hoạt động bình thường và đã có ít nhất một khoản đầu tư.
2. Người dùng đang ở chức năng quản lý tài sản đầu tư.

#### Bảo đảm tối thiểu

1. Hệ thống không thay đổi tên khoản và không thay đổi liên kết của bất kỳ lệnh mua nào.

#### Hậu điều kiện thành công

1. Tên khoản và tập lệnh mua của khoản được cập nhật trọn vẹn; lệnh mua bỏ gán trở về trạng thái chưa gán; lệnh mua chuyển từ khoản khác sang thì rời khoản cũ; số dư ví, giá gốc, giá vốn, số đơn vị, lãi lỗ của từng lệnh mua và liên kết quỹ mục tiêu không thay đổi; tổng các khoản cộng với lệnh mua chưa gán vẫn bằng tổng toàn bộ tài sản đầu tư.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng sửa khoản đầu tư.
2. Hệ thống hiển thị biểu mẫu gồm trường tên được điền sẵn tên hiện tại của khoản, danh sách lệnh mua đang thuộc khoản và danh sách các lệnh mua khác. Mỗi lệnh mua hiển thị rõ tên tài sản, ngày mua, số đơn vị, giá gốc và khoản đầu tư hiện tại nếu đã được gán.
3. Người dùng giữ hoặc sửa tên, tích chọn các lệnh mua cần gán thêm và các lệnh mua cần bỏ gán.
4. Người dùng xác nhận gửi yêu cầu lưu.
5. Hệ thống loại bỏ khoảng trắng thừa ở hai đầu của tên mới và kiểm tra độ dài:
   - Nếu độ dài không thuộc khoảng từ 1 đến 50 ký tự, hệ thống kích hoạt Luồng ngoại lệ 5a (`ERR-INV-012`).
   - Nếu tên mới trùng với một khoản đầu tư khác, so khớp không phân biệt chữ hoa, chữ thường, hệ thống kích hoạt Luồng ngoại lệ 5b (`ERR-INV-013`). Tên mới chỉ khác tên cũ của chính khoản này về chữ hoa, chữ thường được chấp nhận.
6. Hệ thống kiểm tra các danh sách lệnh mua:
   - Nếu có mã không tồn tại, mã bỏ gán không thuộc khoản này, hoặc mã xuất hiện ở cả danh sách gán và bỏ gán, hệ thống kích hoạt Luồng ngoại lệ 6a (`ERR-INV-014`).
   - Mã trùng lặp trong cùng một danh sách được gộp thành một. Lệnh mua đã thuộc chính khoản này mà được gán lại thì được bỏ qua, không báo lỗi.
7. Đối với các lệnh mua được gán đang thuộc một khoản đầu tư khác, hệ thống kích hoạt Luồng rẽ nhánh 7a để yêu cầu người dùng xác nhận chuyển khoản.
8. Hệ thống áp dụng trọn vẹn đổi tên, gán và bỏ gán trong một thao tác duy nhất (trọn vẹn hoặc không làm gì).
9. Hệ thống thông báo sửa thành công, nêu tên khoản và số lượng lệnh mua sau thao tác. Hệ thống đồng thời hiển thị tổng giá gốc, tổng giá trị thị trường, lãi lỗ và tỷ suất sinh lời trên tổng giá gốc của khoản theo quy tắc `FR-INV-03`, `BR-FIN-06` và `BR-FIN-08`. Khoản không còn lệnh mua thì các chỉ số tiền bằng 0.00 VNĐ và tỷ suất ở trạng thái "N/A" theo `QD-227`.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 7a: Xác nhận chuyển lệnh mua đang thuộc khoản khác

1. Tại bước 7 của luồng chính, hệ thống hiển thị hộp thoại xác nhận nêu rõ mã lệnh mua, tên tài sản và tên khoản đầu tư hiện tại của từng lệnh mua cần chuyển.
2. Người dùng chọn xác nhận chuyển sang khoản đang sửa.
3. Hệ thống gỡ liên kết của lệnh mua khỏi khoản cũ và gán sang khoản đang sửa tại bước 8.
4. Nếu người dùng hủy hoặc đóng hộp thoại, hệ thống kích hoạt Luồng ngoại lệ 7b.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Không tìm thấy khoản đầu tư

1. Tại bước 2 của luồng chính, nếu khoản được chọn không còn tồn tại, hệ thống báo lỗi nghiệp vụ không tìm thấy khoản đầu tư và không thay đổi dữ liệu.

##### Luồng ngoại lệ 5a: Tên khoản đầu tư không đúng độ dài

1. Tại bước 5 của luồng chính, nếu tên mới để trống hoặc có độ dài sau khi cắt khoảng trắng vượt quá 50 ký tự, hệ thống từ chối toàn bộ thao tác. Khi đó, hệ thống báo lỗi `ERR-INV-012`: "Tên khoản đầu tư phải có độ dài từ 1 đến 50 ký tự."
2. Hệ thống giữ nguyên tên và các liên kết hiện có; người dùng nhập lại tên hợp lệ.

##### Luồng ngoại lệ 5b: Trùng tên với khoản đầu tư khác

1. Tại bước 5 của luồng chính, nếu tên mới trùng với tên của một khoản đầu tư khác, hệ thống từ chối toàn bộ thao tác và báo lỗi `ERR-INV-013`: "Tên khoản đầu tư đã tồn tại trong hệ thống."
2. Hệ thống giữ nguyên tên cũ của khoản; người dùng đổi sang tên khác.

##### Luồng ngoại lệ 6a: Danh sách lệnh mua không hợp lệ

1. Tại bước 6 của luồng chính, nếu có mã lệnh mua không tồn tại, mã bỏ gán không thuộc khoản này, hoặc mã xuất hiện ở cả danh sách gán và bỏ gán, hệ thống từ chối toàn bộ thao tác. Khi đó, hệ thống báo lỗi `ERR-INV-014`: "Thao tác gán lệnh mua không hợp lệ hoặc chứa dữ liệu mâu thuẫn." kèm mã gây lỗi.
2. Hệ thống không thay đổi tên khoản và không thay đổi bất kỳ liên kết nào.

##### Luồng ngoại lệ 7b: Người dùng từ chối xác nhận chuyển lệnh mua

1. Tại bước 4 của Luồng rẽ nhánh 7a, nếu người dùng hủy hoặc đóng hộp thoại, hệ thống hủy toàn bộ thao tác sửa, không làm gì, kể cả phần đổi tên.
2. Hệ thống giữ nguyên biểu mẫu để người dùng điều chỉnh lựa chọn.

#### Quy tắc nghiệp vụ đặc thù

1. Bản chất nhóm logic theo `FR-INV-06`: sửa khoản không làm phát sinh giao dịch tiền, không làm biến động số dư ví, không đổi giá gốc, giá vốn hay số đơn vị của lệnh mua.
2. Độc quyền gán lệnh mua (`BR-FIN-06`, `QD-139`, `QD-143`): mỗi lệnh mua tại một thời điểm thuộc tối đa một khoản. Lệnh mua bỏ gán trở về trạng thái chưa gán.
3. Trọn vẹn hoặc không làm gì: đổi tên, gán và bỏ gán thực hiện trong cùng một thao tác; một mã không hợp lệ hoặc một lần chuyển khoản bị từ chối làm hủy toàn bộ.
4. Độc lập với liên kết Quỹ mục tiêu (`BR-FIN-10`, `QD-102`): gán và bỏ gán không thay đổi liên kết quỹ mục tiêu của lệnh mua.
5. Biểu mẫu điền sẵn tên hiện tại (`QD-173`): người dùng chỉ gán hoặc bỏ gán lệnh mua mà không phải nhập lại tên; tên trống sau khi cắt khoảng trắng bị từ chối bằng `ERR-INV-012`.
6. Phạm vi lệnh mua được gán (`FR-INV-06`): lệnh mua của tài sản đầu tư bất kỳ đều được gán, kể cả lệnh mua đã bán hết; lãi lỗ đã chốt của lệnh mua đã bán hết được tính vào lãi lỗ đã chốt của khoản.
7. Yêu cầu chức năng liên quan: `FR-INV-06`, `FR-INV-03`.

---

### 6.32 UC-INV-08: Xóa khoản đầu tư

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-INV-08 |
| Tên | Xóa khoản đầu tư |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Cho phép người dùng bỏ một cách nhóm không còn dùng mà không mất dữ liệu giao dịch đầu tư. |
| Mô tả tóm tắt | Xóa khoản đầu tư sau khi người dùng xác nhận, đưa các lệnh mua của khoản về trạng thái chưa gán theo `FR-INV-06` và `FR-INV-03`, bảo toàn mọi lệnh mua, lệnh bán và sự kiện. |
| Điều kiện kích hoạt | Người dùng chọn một khoản đầu tư và kích hoạt chức năng xóa. |

#### Tiền điều kiện

1. Hệ thống hoạt động bình thường và đã có ít nhất một khoản đầu tư.
2. Người dùng đang ở chức năng quản lý tài sản đầu tư.

#### Bảo đảm tối thiểu

1. Khoản và mọi liên kết giữ nguyên.

#### Hậu điều kiện thành công

1. Khoản bị xóa khỏi hệ thống; các lệnh mua trở về trạng thái chưa gán; số dư ví, giá gốc, giá vốn, số đơn vị và lãi lỗ của từng lệnh mua, lệnh bán, sự kiện và liên kết quỹ mục tiêu không thay đổi; tổng các khoản cộng với lệnh mua chưa gán vẫn bằng tổng toàn bộ tài sản đầu tư.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng xóa khoản đầu tư.
2. Hệ thống tính số lượng lệnh mua đang chứa, lãi lỗ đã chốt và lãi lỗ chưa chốt hiện tại của khoản theo `FR-INV-03`.
3. Hệ thống hiển thị hộp thoại xác nhận nêu rõ tên khoản, số lượng lệnh mua đang chứa, lãi lỗ đã chốt, lãi lỗ chưa chốt, và thông điệp khoản đã xóa không phục hồi được.
4. Người dùng chọn xác nhận xóa.
5. Hệ thống xóa khoản và đưa các lệnh mua của khoản về trạng thái chưa gán.
6. Hệ thống thông báo xóa thành công, nêu số lượng lệnh mua đã trở về trạng thái chưa gán.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Khoản chưa có lệnh mua

1. Tại bước 3 của luồng chính, nếu khoản chưa có lệnh mua, hộp thoại nêu số lệnh mua bằng 0 và lãi lỗ bằng 0.00 VNĐ.
2. Luồng tiếp tục bước 4 như bình thường.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Không tìm thấy khoản đầu tư

1. Tại bước 2 của luồng chính, nếu khoản được chọn không còn tồn tại, hệ thống báo lỗi nghiệp vụ không tìm thấy khoản đầu tư và không thay đổi dữ liệu.

##### Luồng ngoại lệ 4a: Người dùng không xác nhận xóa

1. Tại bước 4 của luồng chính, nếu người dùng chọn không xóa hoặc đóng hộp thoại, hệ thống không xóa khoản.
2. Khoản và mọi liên kết lệnh mua giữ nguyên.

#### Quy tắc nghiệp vụ đặc thù

1. Phạm vi tác động (`QD-143`): xóa khoản không xóa lệnh mua, tài sản đầu tư, lệnh bán hay sự kiện, và không thay đổi số dư ví, giá gốc, giá vốn, số đơn vị hay lãi lỗ của từng lệnh mua.
2. Độc lập với liên kết Quỹ mục tiêu (`BR-FIN-10`, `QD-102`): lệnh mua giữ nguyên liên kết quỹ mục tiêu và giá trị đóng góp vào quỹ.
3. Xác nhận bắt buộc (`QD-101`): chỉ khi người dùng xác nhận, hệ thống mới xóa khoản; thao tác không phục hồi được.
4. Yêu cầu chức năng liên quan: `FR-INV-06`, `FR-INV-03`.

---

### 6.33 UC-FND-01: Tạo quỹ mục tiêu mới

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-FND-01 |
| Tên | Tạo quỹ mục tiêu mới |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Lập một mục tiêu tích lũy mới và gắn tài sản ban đầu vào mục tiêu đó mà không làm thay đổi số dư trong dữ liệu tiền. |
| Mô tả tóm tắt | Tạo một quỹ mục tiêu tài chính mới với số tiền mục tiêu cần tích lũy. Sau đó, hệ thống gắn tài sản ban đầu từ 3 thành phần (tiền mặt, sổ tiết kiệm liên kết, lệnh mua của tài sản đầu tư liên kết) vào quỹ theo quy tắc `BR-FIN-10`. |
| Điều kiện kích hoạt | Người dùng cần tạo một mục tiêu tích lũy tài chính mới. |

#### Tiền điều kiện

1. Hệ thống không tạo sẵn quỹ mục tiêu nào. Người dùng tự tạo quỹ mới tại ca sử dụng này.
2. Người dùng đang ở giao diện quy hoạch quỹ mục tiêu.

#### Bảo đảm tối thiểu

1. Nếu quá trình tạo quỹ gặp lỗi kiểm tra dữ liệu hoặc người dùng hủy bỏ (kể cả khi hủy hoặc gặp lỗi ở bước gắn tài sản), không có quỹ mục tiêu mới nào được tạo; bản ghi tạm thời bị hủy bỏ, các liên kết tài sản và số dư ví giữ nguyên vẹn.

#### Hậu điều kiện thành công

1. Quỹ mục tiêu mới được lưu vào danh sách bản ghi `funds` với mã quỹ duy nhất.
2. Tổng giá trị đã gom được của quỹ $Total_{fund}$ được tính tự động từ 3 thành phần ($Cash_{fund} + \sum SavingsBalance_s + \sum MarketValue_{remain,i}$) theo quy tắc `BR-FIN-10`, trong đó $SavingsBalance_s$ theo `BR-FIN-05`.
3. Tiến độ hoàn thành $GoalFundProgress$ được tính chính xác.
4. Nguyên tắc không tính trùng tài sản: không có sổ tiết kiệm hay lệnh mua của tài sản đầu tư nào liên kết với hai quỹ cùng lúc.
5. Tổng tiền mặt đã gắn vào các quỹ mục tiêu không vượt quá tổng số dư tiền mặt khả dụng trên tất cả các ví tiền của hệ thống ($TotalWalletBalance$, gồm ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ theo QD-124 và QD-207).

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Tạo quỹ mục tiêu mới".
2. Hệ thống hiển thị biểu mẫu tạo quỹ gồm các trường: Mã quỹ, Tên quỹ mục tiêu, Số tiền mục tiêu cần tích lũy, Mô tả mục tiêu.
3. Người dùng nhập mã quỹ (ví dụ: "study_abroad"), tên quỹ (ví dụ: "Quỹ du học nâng cao trình độ"), số tiền mục tiêu (ví dụ: 150,000,000 VNĐ) và nhập mô tả định hướng.
4. Người dùng xác nhận lưu quỹ mục tiêu.
5. Hệ thống kiểm tra dữ liệu:
   - Mã quỹ không trùng lặp với các quỹ đã có trong hệ thống (chuỗi viết liền không dấu, độ dài từ 1 đến 20 ký tự, không chứa khoảng trắng hay ký tự đặc biệt).
   - Tên quỹ không được để trống (độ dài từ 1 đến 100 ký tự).
   - Tên quỹ không trùng với tên quỹ đã có, không phân biệt chữ hoa, chữ thường, sau khi bỏ khoảng trắng thừa ở hai đầu.
   - Số tiền mục tiêu $targetAmount \ge 0$, đưa về kiểu dữ liệu tiền tệ số thập phân chính xác cố định theo quy tắc `BR-FIN-01`.
   - Mô tả mục tiêu có độ dài tối đa 255 ký tự (nếu có nhập).
6. Hệ thống khởi tạo bản ghi quỹ tạm thời trong phiên làm việc với 3 thành phần ban đầu bằng 0 ($Cash = 0, Savings = 0, FundOrders = 0$), tiến độ ban đầu $GoalFundProgress = 0.00\%$.
7. Hệ thống chuyển sang màn hình "Gắn tài sản vào quỹ" và hiển thị các nguồn lực tài sản khả dụng:
   - Số dư tiền mặt khả dụng chưa gắn vào quỹ nào trong hệ thống:
   $$UnallocatedCash = TotalWalletBalance - \sum_{f \in Funds} Cash_f$$
   - Danh sách các sổ tiết kiệm đang hoạt động chưa liên kết với quỹ nào.
   - Danh sách các lệnh mua của tài sản đầu tư đang nắm giữ chưa liên kết với quỹ nào.
8. Người dùng nhập số tiền mặt muốn gắn từ ví vào quỹ này ($cashAmount$), tích chọn các sổ tiết kiệm muốn gắn vào quỹ, và tích chọn các lệnh mua của tài sản đầu tư muốn gắn vào quỹ.
9. Người dùng xác nhận gắn tài sản vào quỹ mục tiêu.
10. Hệ thống kiểm tra nghiệp vụ gắn tài sản:
   - Kiểm tra tiền mặt: $cashAmount \ge 0$ và $cashAmount \le UnallocatedCash$. Nếu $cashAmount < 0$, hệ thống từ chối với mã lỗi `ERR-FND-003` theo quy tắc `BR-FIN-10`.
   - Kiểm tra tài sản liên kết: Bảo đảm các sổ tiết kiệm và lệnh mua của tài sản đầu tư được chọn không bị liên kết trùng lặp theo quy tắc `BR-FIN-10`.
   - Nếu bước 10 phát sinh lỗi hoặc người dùng hủy bỏ, bản ghi quỹ tạm thời tạo ở bước 6 bị hủy bỏ hoàn toàn, không lưu quỹ dở dang vào hệ thống.
11. Hệ thống cập nhật danh sách `funds_cash`, lưu trường giá trị tiền mặt cho quỹ tương ứng.
12. Hệ thống cập nhật liên kết mã quỹ cho các bản ghi sổ tiết kiệm trong danh sách `savings` và các bản ghi lệnh mua của tài sản đầu tư.
13. Hệ thống tính lại tổng tài sản đã gom được thực tế $Total_{fund}$ và tỷ lệ tiến độ $GoalFundProgress$ theo quy tắc `BR-FIN-10`:
$$Total_{fund} = Cash_{fund} + \sum_{s \in Savings_{fund}} SavingsBalance_s + \sum_{i \in FundOrders_{fund}} MarketValue_{remain,i}$$
Tỷ lệ tiến độ $GoalFundProgress = \frac{Total_{fund}}{targetAmount} \times 100\%$ khi $targetAmount > 0$. Nếu $targetAmount = 0.00$ VNĐ, hệ thống hiển thị tiến độ "N/A" hoặc gán $GoalFundProgress = 0.00\%$ (không được phép gán 100.00%).
14. Hệ thống thông báo gắn tài sản thành công, cập nhật giao diện tổng quan các quỹ mục tiêu kèm thanh tiến độ.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Số tiền mục tiêu nhỏ hơn 0

1. Tại bước 5 của luồng chính, Người dùng nhập số tiền mục tiêu là số âm ($targetAmount < 0$).
2. Hệ thống từ chối với mã lỗi `ERR-FND-002` và thông báo lỗi: "Số tiền mục tiêu không được nhỏ hơn 0 VNĐ".
3. Người dùng nhập lại số tiền mục tiêu hợp lệ.

##### Luồng ngoại lệ 5b: Trùng mã quỹ mục tiêu

1. Tại bước 5 của luồng chính, hệ thống phát hiện mã quỹ đã tồn tại trong hệ thống.
2. Hệ thống báo lỗi trùng mã và yêu cầu Người dùng nhập mã khác.
3. Người dùng nhập lại mã quỹ duy nhất.

##### Luồng ngoại lệ 5c: Trùng tên quỹ mục tiêu

1. Tại bước 5 của luồng chính, hệ thống phát hiện tên quỹ trùng với một quỹ đã có.
2. Hệ thống từ chối với mã lỗi `ERR-FND-004` và thông báo lỗi: "Tên quỹ đã tồn tại".
3. Người dùng nhập tên quỹ khác.

##### Luồng ngoại lệ 5d: Định dạng mã quỹ, tên quỹ hoặc mô tả không hợp lệ

1. Tại bước 5 của luồng chính, hệ thống phát hiện mã quỹ chứa ký tự đặc biệt, dấu tiếng Việt hoặc ngoài khoảng 1 đến 20 ký tự, tên quỹ trống, dài quá 100 ký tự, hoặc mô tả dài quá 255 ký tự.
2. Hệ thống từ chối và hiển thị thông báo lỗi dữ liệu đầu vào không hợp lệ.
3. Người dùng nhập lại thông tin hợp lệ.

##### Luồng ngoại lệ 10a: Số tiền mặt gắn vào quỹ vượt quá số dư còn lại

1. Tại bước 10 của luồng chính, hệ thống phát hiện số tiền mặt người dùng muốn gắn vào quỹ lớn hơn số dư tiền mặt chưa gắn vào quỹ nào ($cashAmount > UnallocatedCash$).
2. Hệ thống từ chối với mã lỗi `ERR-FND-005` và thông báo: "Tổng tiền mặt gắn vào các quỹ vượt số tiền mặt hiện có trong các ví".
3. Người dùng giảm số tiền mặt gắn vào quỹ hoặc nạp thêm tiền vào các ví.

##### Luồng ngoại lệ 10b: Liên kết trùng lặp tài sản vào nhiều quỹ khác nhau

1. Tại bước 10 của luồng chính, hệ thống phát hiện một sổ tiết kiệm hoặc lệnh mua của tài sản đầu tư đang được chọn đã thuộc về một quỹ mục tiêu khác và người dùng chưa xác nhận chuyển quyền sở hữu.
2. Hệ thống hiển thị hộp thoại cảnh báo: "Tài sản đang thuộc về quỹ mục tiêu khác. Thao tác chuyển sẽ làm giảm giá trị của quỹ cũ. Xác nhận chuyển tài sản sang quỹ mục tiêu hiện tại?".
3. Nếu Người dùng chọn Đồng ý, hệ thống tự động gỡ khỏi quỹ cũ và gán sang quỹ mới theo quy tắc `BR-FIN-10`; nếu Người dùng chọn Hủy (không đồng ý chuyển quyền), hệ thống từ chối thao tác gán tài sản với mã lỗi `ERR-FND-001` và giữ nguyên liên kết cũ theo `BR-FIN-10`.

##### Luồng ngoại lệ 10c: Số tiền mặt gắn ban đầu nhỏ hơn 0

1. Tại bước 10 của luồng chính, Người dùng nhập số tiền mặt gắn vào quỹ là số âm ($cashAmount < 0$).
2. Hệ thống từ chối với mã lỗi `ERR-FND-003` và thông báo lỗi: "Số tiền phân bổ vào quỹ không được nhỏ hơn 0 VNĐ".
3. Người dùng nhập lại số tiền mặt hợp lệ ($\ge 0$).

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc cấu trúc 3 thành phần theo `BR-FIN-10`: Giá trị quỹ luôn bằng tổng đại số chính xác của 3 thành phần ($Cash + Savings + FundOrders$).
2. Quy tắc không tính trùng tài sản: Mỗi sổ tiết kiệm và lệnh mua của tài sản đầu tư chỉ được thuộc về duy nhất một quỹ mục tiêu tại một thời điểm.
3. Quy tắc hạn mức tiền mặt: Tổng tiền mặt gắn vào các quỹ không vượt quá tổng số dư tiền mặt trên toàn bộ các ví ($\sum_{f \in Funds} Cash_f \le TotalWalletBalance$, gồm ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ theo QD-124 và QD-207). Vi phạm bị từ chối với mã lỗi `ERR-FND-005` theo `BR-FIN-10`.
4. Tách khỏi số dư ví: Quỹ mục tiêu là kế hoạch tích lũy, không phải là ví tiền thực tế; việc tạo quỹ mục tiêu không làm thay đổi tài sản ròng hay số dư khả dụng thực tế của ví.
5. Yêu cầu chức năng liên quan: `FR-FND-01`, `FR-FND-02`.

---

### 6.34 UC-FND-02: Kiểm soát tiến độ và tổng hợp tài sản quỹ mục tiêu

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-FND-02 |
| Tên | Kiểm soát tiến độ và tổng hợp tài sản quỹ mục tiêu |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Nắm tiến độ hoàn thành và cơ cấu tài sản của toàn bộ danh sách quỹ mục tiêu. |
| Mô tả tóm tắt | Xem tổng quan tiến độ tích lũy của toàn bộ danh sách quỹ mục tiêu, chia theo 3 thành phần tài sản và phân loại trạng thái đã đạt mục tiêu hay đang tích lũy. |
| Điều kiện kích hoạt | Người dùng cần tra cứu tiến độ hoàn thành hiện tại của các quỹ mục tiêu. |

#### Tiền điều kiện

1. Người dùng có thể chưa tạo quỹ mục tiêu nào. Khi đó hệ thống hiển thị danh sách rỗng.
2. Người dùng đang ở giao diện quy hoạch quỹ mục tiêu.

#### Bảo đảm tối thiểu

1. Hệ thống hoạt động ở chế độ chỉ đọc, không làm biến đổi bất kỳ dữ liệu quỹ mục tiêu hay liên kết tài sản nào trong hệ thống.

#### Hậu điều kiện thành công

1. Tiến độ hoàn thành $GoalFundProgress$ được tính chính xác; tự động hiển thị trạng thái "ĐÃ ĐẠT MỤC TIÊU" (`REACHED`) khi $GoalFundProgress \ge 100\%$.

#### Luồng sự kiện chính

1. Người dùng mở màn hình tổng quan "Quỹ mục tiêu tài chính".
2. Hệ thống quét dữ liệu và tính lại các chỉ số cho toàn bộ danh sách quỹ do người dùng tạo:
   - Thành phần tiền mặt: Lấy giá trị số tiền mặt gắn vào quỹ $Cash_{fund}$ từ danh sách `funds_cash`.
   - Thành phần sổ tiết kiệm: Quét danh sách `savings` đang liên kết với mã quỹ tương ứng và ở trạng thái `ACTIVE`, lấy tổng $SavingsBalance_s$ tính theo `BR-FIN-05`.
   - Thành phần lệnh mua của tài sản đầu tư: Quét các lệnh mua đang liên kết với mã quỹ tương ứng, lấy tổng $MarketValue_{remain,i}$ định giá theo `FR-INV-03` ($MarketValue_{remain,i} = Units_{remain,i} \times CurrentPrice$). Các lệnh mua đã bán hết ($Units_{remain,i} = 0$) có giá trị đóng góp bằng 0.00 VNĐ theo QD-102, QD-174, QD-184. Trong danh sách chi tiết thành phần của quỹ, hệ thống ẩn mặc định các lệnh mua đã bán hết.
   - Tổng tài sản tích lũy: $Total_{fund} = Cash_{fund} + \sum_{s \in Savings_{fund}} SavingsBalance_s + \sum_{i \in FundOrders_{fund}} MarketValue_{remain,i}$.
   - Tỷ lệ tiến độ: $GoalFundProgress = (Total_{fund} / Target) \times 100\%$ khi $Target > 0$. Nếu $Target = 0$, hệ thống hiển thị "N/A" hoặc gán $GoalFundProgress = 0.00\%$ (không được phép gán 100.00%).
   - Số tiền còn thiếu: $Remaining = \max(0, Target - Total_{fund})$.
3. Hệ thống phân loại trạng thái quỹ: Nếu $GoalFundProgress \ge 100\%$, gắn nhãn "ĐÃ ĐẠT MỤC TIÊU" (trạng thái `REACHED`); nếu $GoalFundProgress < 100\%$, gắn nhãn "ĐANG TÍCH LŨY" (trạng thái `IN_PROGRESS`).
4. Hệ thống hiển thị bảng so sánh tổng hợp, biểu đồ tiến độ thanh ngang và chia màu sắc cho 3 thành phần tài sản của từng quỹ. Giao diện cung cấp bộ lọc cho phép người dùng bật xem các lệnh mua đã bán hết (vốn bị ẩn mặc định) kèm nhãn "đã bán hết".

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

Không có luồng ngoại lệ nghiệp vụ đặc thù bổ sung ngoài việc hiển thị danh sách rỗng khi chưa có quỹ mục tiêu nào được tạo.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc cấu trúc 3 thành phần theo `BR-FIN-10`: Tổng tài sản tích lũy luôn bằng tổng đại số chính xác của 3 thành phần.
2. Yêu cầu chức năng liên quan: `FR-FND-03`.

---

### 6.35 UC-FND-03: Điều chỉnh thông tin và hạn mức mục tiêu của quỹ

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-FND-03 |
| Tên | Điều chỉnh thông tin và hạn mức mục tiêu của quỹ |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Cập nhật lại tên, mô tả, số tiền mục tiêu hoặc số tiền mặt gắn vào của một quỹ mà không làm thay đổi số dư tài sản thực tế. |
| Mô tả tóm tắt | Điều chỉnh tên quỹ, mô tả, số tiền mục tiêu mới hoặc số tiền mặt gắn vào của một quỹ mục tiêu đã tồn tại, tính toán lại tỷ lệ tiến độ tương ứng. |
| Điều kiện kích hoạt | Người dùng cần đổi tên, mô tả, thay đổi số tiền mục tiêu hoặc số tiền mặt gắn vào của một quỹ mục tiêu đã tạo. |

#### Tiền điều kiện

1. Quỹ mục tiêu cần điều chỉnh đã tồn tại trong danh sách `funds`.
2. Người dùng đang ở giao diện quy hoạch quỹ mục tiêu.

#### Bảo đảm tối thiểu

1. Nếu thao tác điều chỉnh bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, tên, mô tả, số tiền mục tiêu, số tiền mặt gắn vào và tiến độ của quỹ mục tiêu giữ nguyên trạng thái cũ.

#### Hậu điều kiện thành công

1. Tên quỹ, mô tả, số tiền mục tiêu và số tiền mặt gắn vào quỹ mới được cập nhật trong danh sách `funds`.
2. Tỷ lệ tiến độ $GoalFundProgress$ và trạng thái quỹ (`IN_PROGRESS` hoặc `REACHED`) được tính toán lại chính xác theo số tiền mục tiêu và số tiền mặt gắn vào quỹ mới.
3. Số dư thực tế của ví tiền, sổ tiết kiệm hay tài sản đầu tư không bị thay đổi bởi thao tác này.

#### Luồng sự kiện chính

1. Người dùng chọn một quỹ mục tiêu và yêu cầu điều chỉnh thông tin hoặc mục tiêu tích lũy.
2. Hệ thống hiển thị biểu mẫu cho phép sửa: Tên quỹ, Mô tả, Số tiền mục tiêu mới, Số tiền mặt gắn vào quỹ mới.
3. Người dùng nhập các thông số mới và xác nhận lưu điều chỉnh.
4. Hệ thống kiểm tra: nếu có nhập số tiền mục tiêu mới ($newTarget$), kiểm tra $newTarget \ge 0$; nếu có tên quỹ mới hoặc mô tả mới, kiểm tra độ dài hợp lệ (tên quỹ từ 1 đến 100 ký tự, mô tả tối đa 255 ký tự). Với tên quỹ mới, hệ thống còn kiểm tra tên không trùng tên quỹ khác (bỏ qua chính quỹ đang sửa) theo `FR-FND-04`; nếu có số tiền mặt gắn vào quỹ mới ($newCashAmount$), kiểm tra $newCashAmount \ge 0$ và tổng tiền mặt gắn vào các quỹ sau điều chỉnh không vượt $TotalWalletBalance$ theo `BR-FIN-10`.
5. Hệ thống cập nhật danh sách `funds` với tên, mô tả, số tiền mục tiêu và số tiền mặt gắn vào quỹ mới, tính toán lại tỷ lệ tiến độ: $GoalFundProgress_{mới} = (Total_{fund} / newTarget) \times 100\%$ khi $newTarget > 0$; nếu $newTarget = 0.00$ VNĐ, hệ thống hiển thị tiến độ "N/A" hoặc gán $GoalFundProgress = 0.00\%$ (không được phép gán 100.00%).
6. Thao tác này không làm thay đổi số dư thực tế của ví tiền, sổ tiết kiệm hay tài sản đầu tư.
7. Hệ thống thông báo điều chỉnh thành công và cập nhật tỷ lệ tiến độ mới.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Số tiền mục tiêu mới nhỏ hơn 0

1. Tại bước 4 của luồng chính, Người dùng nhập số tiền mục tiêu mới là số âm ($newTarget < 0$).
2. Hệ thống từ chối với mã lỗi `ERR-FND-002` và thông báo lỗi: "Số tiền mục tiêu không được nhỏ hơn 0 VNĐ".
3. Người dùng nhập lại số tiền mục tiêu hợp lệ.

##### Luồng ngoại lệ 4b: Tên quỹ mới trùng với quỹ khác

1. Tại bước 4 của luồng chính, tên quỹ mới trùng với tên của một quỹ khác, không tính chính quỹ đang sửa (không phân biệt chữ hoa, chữ thường, sau khi bỏ khoảng trắng thừa ở hai đầu).
2. Hệ thống từ chối với mã lỗi `ERR-FND-004` và thông báo lỗi: "Tên quỹ đã tồn tại".
3. Người dùng nhập lại tên quỹ khác, thông tin của quỹ giữ nguyên trạng thái cũ.

##### Luồng ngoại lệ 4c: Số tiền mặt gắn vào quỹ mới nhỏ hơn 0

1. Tại bước 4 của luồng chính, Người dùng nhập số tiền mặt gắn vào quỹ mới là số âm ($newCashAmount < 0$).
2. Hệ thống từ chối với mã lỗi `ERR-FND-003` và thông báo lỗi: "Số tiền phân bổ vào quỹ không được nhỏ hơn 0 VNĐ".
3. Người dùng nhập lại số tiền mặt hợp lệ, thông tin của quỹ giữ nguyên trạng thái cũ.

##### Luồng ngoại lệ 4d: Tổng tiền mặt gắn vào các quỹ vượt tiền trong ví

1. Tại bước 4 của luồng chính, tổng tiền mặt gắn vào các quỹ sau điều chỉnh lớn hơn $TotalWalletBalance$.
2. Hệ thống từ chối với mã lỗi `ERR-FND-005` và thông báo lỗi: "Tổng tiền mặt gắn vào các quỹ vượt số tiền mặt hiện có trong các ví".
3. Người dùng nhập lại số tiền mặt nhỏ hơn hoặc bằng phần còn có thể gắn, thông tin của quỹ giữ nguyên trạng thái cũ.

##### Luồng ngoại lệ 4e: Độ dài tên quỹ hoặc mô tả không hợp lệ

1. Tại bước 4 của luồng chính, Người dùng nhập tên quỹ mới trống hoặc vượt quá 100 ký tự, hoặc mô tả mới vượt quá 255 ký tự.
2. Hệ thống từ chối và thông báo lỗi độ dài dữ liệu không hợp lệ.
3. Người dùng nhập lại thông tin hợp lệ, thông tin cũ của quỹ giữ nguyên trạng thái.

#### Quy tắc nghiệp vụ đặc thù

1. Tách khỏi số dư ví theo `BR-FIN-10`: Việc điều chỉnh số tiền mục tiêu không làm thay đổi tài sản ròng hay số dư khả dụng thực tế của ví; tỷ lệ tiến độ được tính lại đúng công thức $GoalFundProgress = Total_{fund}/Target \times 100\%$.
2. Yêu cầu chức năng liên quan: `FR-FND-04`.

---

### 6.36 UC-FND-04: Chuyển tài sản giữa các quỹ

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-FND-04 |
| Tên | Chuyển tài sản giữa các quỹ |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Dịch chuyển tiền mặt, sổ tiết kiệm hoặc lệnh mua của tài sản đầu tư từ quỹ mục tiêu này sang quỹ mục tiêu khác mà không làm thay đổi tổng tài sản toàn hệ thống. |
| Mô tả tóm tắt | Chuyển giao số tiền mặt gắn vào quỹ hoặc quyền liên kết của một sổ tiết kiệm, lệnh mua của tài sản đầu tư từ Quỹ A sang Quỹ B, tính toán lại tổng tài sản và tiến độ của cả hai quỹ. |
| Điều kiện kích hoạt | Người dùng cần chia lại tài sản giữa hai hoặc nhiều quỹ mục tiêu đã tạo. |

#### Tiền điều kiện

1. Cả hai quỹ tham gia việc chuyển tài sản đã tồn tại trong danh sách `funds`.
2. Người dùng đang ở giao diện quy hoạch quỹ mục tiêu.

#### Bảo đảm tối thiểu

1. Nếu thao tác chuyển tài sản bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, liên kết và giá trị tài sản của cả hai quỹ mục tiêu được giữ nguyên vẹn.

#### Hậu điều kiện thành công

1. Tổng tài sản $Total$ và tiến độ $GoalFundProgress$ của cả Quỹ A và Quỹ B được tính toán lại chính xác.
2. Tổng tài sản toàn hệ thống giữ nguyên sau thao tác chuyển tài sản.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Chuyển tài sản giữa các quỹ" hoặc điều chỉnh tài sản đã gắn vào một quỹ.
2. Hệ thống hiển thị bảng chuyển giao tài sản với các tham số: Quỹ nguồn ($fundCode_A$), Quỹ đích ($fundCode_B$), Loại tài sản chuyển giao (`CASH`, `SAVING`, `INVESTMENT`), Giá trị hoặc định danh tài sản ($amount$ với tiền mặt; mã sổ tiết kiệm $savingsId$ hoặc mã lệnh mua $orderId$):
   - Chuyển số tiền mặt gắn vào quỹ: Cho phép giảm tiền mặt ở Quỹ A và tăng tương ứng ở Quỹ B trong danh sách `funds_cash`.
   - Chuyển sổ tiết kiệm hoặc lệnh mua của tài sản đầu tư: Cho phép chọn một sổ tiết kiệm hoặc lệnh mua của tài sản đầu tư đang thuộc Quỹ A và gán lại sang Quỹ B.
3. Người dùng thực hiện thao tác dịch chuyển tài sản mong muốn và nhấn "Xác nhận chuyển".
4. Hệ thống kiểm tra tính hợp lệ: Số tiền mặt lấy khỏi Quỹ A không vượt quá số tiền mặt hiện có của Quỹ A ($amount \le Cash_A$).
5. Hệ thống cập nhật đồng bộ:
   - Nếu chuyển tiền mặt: $Cash_A = Cash_A - amount$; $Cash_B = Cash_B + amount$.
   - Nếu chuyển sổ tiết kiệm hoặc lệnh mua của tài sản đầu tư: Cập nhật liên kết sang Quỹ B cho tài sản đó.
6. Hệ thống tính lại $Total$ và $GoalFundProgress$ của cả hai Quỹ A và Quỹ B. Tổng tài sản toàn hệ thống giữ nguyên.
7. Hệ thống hiển thị thông báo chuyển tài sản hoàn tất kèm bảng so sánh trước và sau khi chia lại.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Lấy tiền mặt vượt quá số tiền hiện có của Quỹ A

1. Tại bước 4 của luồng chính, hệ thống phát hiện số tiền mặt muốn lấy khỏi Quỹ A vượt quá số tiền mặt hiện có của Quỹ A ($amount > Cash_A$).
2. Hệ thống từ chối chuyển, trả về mã lỗi `ERR-FND-004` và thông báo: "Số tiền mặt lấy khỏi quỹ vượt quá số dư hiện có của quỹ nguồn".
3. Người dùng điều chỉnh lại số tiền chuyển giao.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc không tính trùng tài sản theo `BR-FIN-10`: Sau khi chuyển tài sản, mỗi sổ tiết kiệm và lệnh mua của tài sản đầu tư chỉ thuộc về duy nhất một quỹ mục tiêu.
2. Quy tắc trần tiền mặt gắn vào các quỹ theo `BR-FIN-10`: Tổng tiền mặt gắn vào các quỹ không vượt quá tổng số dư tiền mặt trên toàn bộ các ví ($TotalWalletBalance$). Tổng số dư này gồm ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ theo QD-124 và QD-207. Thao tác chuyển giữa hai quỹ bảo toàn tổng tiền mặt gắn vào các quỹ nên không làm phát sinh vi phạm hạn mức này (theo QD-231).
3. Yêu cầu chức năng liên quan: `FR-FND-04`.

---

### 6.37 UC-FND-05: Xóa quỹ mục tiêu

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-FND-05 |
| Tên | Xóa quỹ mục tiêu |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Giải phóng toàn bộ tài sản đã gắn vào một quỹ không còn nhu cầu theo dõi, không làm mất tiền mặt hay tài sản thực tế. |
| Mô tả tóm tắt | Xóa hoàn toàn một quỹ mục tiêu không còn nhu cầu theo dõi khỏi hệ thống. Hệ thống tự động giải phóng toàn bộ tài sản liên kết và hoàn trả số tiền mặt đã gắn vào quỹ về phần tiền mặt khả dụng chưa phân bổ trong các ví. |
| Điều kiện kích hoạt | Người dùng chọn thao tác xóa một quỹ mục tiêu trong danh sách quỹ. |

#### Tiền điều kiện

1. Quỹ mục tiêu cần xóa đã tồn tại trong danh sách `funds`.
2. Người dùng đang ở giao diện quy hoạch quỹ mục tiêu.

#### Bảo đảm tối thiểu

1. Nếu người dùng hủy xác nhận hoặc thao tác xóa thất bại, quỹ mục tiêu và các liên kết tài sản liên quan được giữ nguyên vẹn 100%.

#### Hậu điều kiện thành công

1. Số tiền mặt đã gắn vào quỹ được hoàn trả về phần tiền mặt khả dụng chưa phân bổ trong các ví ($UnallocatedCash$).
2. Toàn bộ các sổ tiết kiệm và toàn bộ lệnh mua của tài sản đầu tư liên kết (bao gồm cả các lệnh mua đã bán hết có $Units_{remain,i} = 0$) được gỡ bỏ liên kết khỏi quỹ.
3. Bản ghi quỹ bị xóa hoàn toàn khỏi danh sách `funds` và bộ lưu trữ, tên quỹ được giải phóng.

#### Luồng sự kiện chính

1. Người dùng chọn một quỹ mục tiêu không còn nhu cầu theo dõi và chọn chức năng "Xóa quỹ mục tiêu".
2. Hệ thống kiểm tra tài sản liên kết của quỹ:
   - Nếu quỹ chưa từng gắn tài sản: Hệ thống hiển thị hộp thoại xác nhận xóa quỹ thông thường và cho phép xóa ngay khi người dùng xác nhận (theo luồng rẽ nhánh 2a).
   - Nếu quỹ đang gắn tài sản: Hệ thống hiển thị hộp thoại cảnh báo rủi ro xác nhận. Thao tác xóa quỹ sẽ tự động gỡ bỏ toàn bộ liên kết tài sản và hoàn trả số tiền mặt đã gắn vào quỹ về phần tiền mặt khả dụng chưa phân bổ trong các ví. Thao tác này không làm thay đổi số dư hay quyền sở hữu tài sản thực tế.
3. Người dùng xác nhận đồng ý xóa quỹ.
4. Hệ thống thực hiện giải phóng tài sản tự động:
   - Cập nhật số tiền mặt đã gắn vào quỹ này về 0 trong danh sách `funds_cash`, hoàn trả số tiền mặt này về phần tiền mặt khả dụng chưa phân bổ trong các ví ($UnallocatedCash$).
   - Gỡ bỏ liên kết quỹ cho toàn bộ các sổ tiết kiệm trong danh sách `savings` đang liên kết với quỹ này.
   - Gỡ bỏ liên kết quỹ cho toàn bộ các lệnh mua của tài sản đầu tư đang liên kết với quỹ này (bao gồm cả các lệnh mua đã bán hết có $Units_{remain,i} = 0$) theo QD-123.
5. Hệ thống xóa hoàn toàn bản ghi quỹ khỏi danh sách `funds` và bộ lưu trữ.
6. Hệ thống làm mới giao diện tổng quan các quỹ mục tiêu và thông báo xóa quỹ thành công.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 2a: Xóa quỹ mục tiêu chưa từng gắn tài sản
1. Tại bước 2 của luồng chính, hệ thống phát hiện quỹ mục tiêu chưa từng gắn bất kỳ tài sản nào.
2. Hệ thống hiển thị hộp thoại xác nhận xóa quỹ.
3. Người dùng chọn xác nhận xóa.
4. Hệ thống xóa hoàn toàn bản ghi quỹ khỏi danh sách `funds` và bộ lưu trữ mà không cần qua bước giải phóng tài sản.
5. Luồng tiếp tục tại bước 6 của luồng chính.

#### Các luồng ngoại lệ

Không có luồng ngoại lệ nghiệp vụ đặc thù bổ sung ngoài các kiểm tra tồn tại quỹ đã liệt kê ở luồng sự kiện chính.

#### Quy tắc nghiệp vụ đặc thù

1. Tách khỏi số dư tài sản cơ sở theo `BR-FIN-10`: Việc xóa quỹ mục tiêu không làm thay đổi tài sản ròng hay số dư khả dụng của ví, sổ tiết kiệm và tài sản đầu tư. Toàn bộ tài sản liên kết (kể cả các lệnh mua đã bán hết) được tự động gỡ bỏ rõ ràng theo QD-123, không để lại liên kết nào trỏ tới quỹ đã xóa.
2. Giải phóng tên quỹ: Sau khi xóa hoàn toàn, tên quỹ mục tiêu được giải phóng. Người dùng được phép tạo quỹ mục tiêu mới cùng tên mà không bị từ chối theo quy tắc kiểm tra trùng tên của quyết định QD-78.
3. Yêu cầu chức năng liên quan: `FR-FND-04`.

---

### 6.38 UC-REP-01: Thống kê dòng tiền 12 tháng gần nhất

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-REP-01 |
| Tên | Thống kê dòng tiền 12 tháng gần nhất |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Đánh giá tình hình tài chính định kỳ qua chuỗi dòng tiền 12 tháng gần nhất và tỷ lệ tiết kiệm tương ứng. |
| Mô tả tóm tắt | Tổng hợp chuỗi dòng tiền 12 tháng gần nhất từ mốc tháng chỉ định, tương ứng danh sách thống kê `months_stat` theo `FR-REP-01`. Tính toán thu nhập (gồm thu nhập đầu tư), chi tiêu, dòng tiền thuần và tỷ lệ tiết kiệm theo `BR-FIN-01` và `BR-FIN-07`. |
| Điều kiện kích hoạt | Người dùng cần đánh giá dòng tiền định kỳ trong 12 tháng gần nhất. |

#### Tiền điều kiện

1. Hệ thống đã khởi chạy và đọc thành công tệp dữ liệu giao dịch và dữ liệu sự kiện của tài sản đầu tư.
2. Người dùng đang ở giao diện nhóm chức năng Thống kê và Báo cáo tài chính.

#### Bảo đảm tối thiểu

1. Hệ thống hoạt động ở chế độ chỉ đọc, không làm biến đổi bất kỳ dữ liệu giao dịch hay số dư ví nào trong hệ thống.

#### Hậu điều kiện thành công

1. Bảng thống kê `months_stat` gồm đúng 12 dòng liên tục lùi từ tháng kết thúc chỉ định được tổng hợp đầy đủ, không để trống trường dữ liệu nào.
2. Dòng tiền thuần $NetCashFlow_m = Income_m - Expense_m$ và Tỷ lệ tiết kiệm $SavingsRate_m$ được tính toán chính xác theo quy tắc `FR-REP-01`. Ba mức bình quân chia cho 12 và kèm ghi chú nêu các tháng chưa có giao dịch vẫn được tính.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Thống kê dòng tiền 12 tháng gần nhất", tùy chọn chọn tháng và năm kết thúc (mặc định là tháng và năm hiện tại).
2. Hệ thống xác định mốc tháng kết thúc $T = (toMonth, toYear)$, mặc định trích xuất từ đồng hồ hệ thống.
3. Hệ thống tạo danh sách 12 mốc tháng liên tục từ $T - 11$ đến $T$, sắp xếp theo trình tự thời gian tăng dần (neo tháng kết thúc ở dòng cuối cùng).
4. Với từng tháng $m$ trong chuỗi 12 tháng, hệ thống duyệt danh sách bản ghi:
   - Lọc các giao dịch có ngày nằm trong khoảng từ ngày đầu tháng đến ngày cuối tháng của tháng $m$.
   - Lọc các sự kiện Nhận tiền mặt của tài sản đầu tư có Ngày nhận nằm trong tháng $m$.
   - Tính tổng thu nhập theo `FR-REP-01`: $Income_m = \sum_{t \in Month_m, Type=Income} amount_t + InvestmentIncome_m$. Trong đó, $InvestmentIncome_m$ là tổng số tiền nhận từ các sự kiện Nhận tiền mặt của tài sản đầu tư theo `QD-76`, `QD-99` và `QD-122`.
   - Tính tổng chi tiêu: $Expense_m = \sum_{t \in Month_m, Type=Expense} amount_t$.
5. Hệ thống tính toán các chỉ số phái sinh cho từng tháng theo quy tắc `FR-REP-01`:
   - Dòng tiền thuần:
    $$NetCashFlow_m = Income_m - Expense_m$$
   - Tỷ lệ tiết kiệm:
    $$
    SavingsRate_m = \begin{cases} \left(\frac{NetCashFlow_m}{Income_m}\right) \times 100\%, & \text{khi } Income_m > 0 \\ 0.00\%, & \text{khi } Income_m \le 0 \end{cases}
    $$
    Áp dụng quy tắc làm tròn ngân hàng với 2 chữ số thập phân khi hiển thị.
6. Hệ thống tính các mức bình quân tháng của chuỗi 12 tháng theo `FR-REP-01`: Thu nhập bình quân, Chi tiêu bình quân và Dòng tiền thuần bình quân. Mẫu số luôn là 12, kể cả các tháng chưa có giao dịch, và hệ thống hiển thị ghi chú nêu điều này theo `FR-REP-01`.
7. Hệ thống hiển thị bảng dữ liệu `months_stat` gồm 12 dòng tháng. Mỗi dòng tháng có dòng phân loại riêng "trong đó thu nhập đầu tư" ($InvestmentIncome_m$) dưới Tổng thu nhập tháng. Chân bảng có dòng hiển thị ba mức bình quân kèm ghi chú nêu các tháng chưa có giao dịch vẫn được tính. Giao diện đồng thời hiển thị biểu đồ kết hợp cột thu chi và đường biểu diễn dòng tiền thuần.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Tháng hoàn toàn không có giao dịch và sự kiện Nhận tiền mặt

1. Tại bước 4 của luồng chính, hệ thống thấy trong một tháng $m$ nào đó không có giao dịch thu chi và không có sự kiện Nhận tiền mặt của tài sản đầu tư ($Income_m \le 0$ và $Expense_m = 0$).
2. Hệ thống hiển thị giá trị số tiền là "0.00 VNĐ", không để trống ô dữ liệu hoặc gán giá trị null.
3. Hệ thống gán $NetCashFlow_m = 0.00$ và $SavingsRate_m = 0.00\%$, bảo đảm bảng dữ liệu 12 tháng luôn liên tục và không gây lỗi giao diện.

##### Luồng ngoại lệ 5a: Tháng có thu nhập không dương nhưng chi tiêu lớn hơn 0

1. Tại bước 5 của luồng chính, hệ thống phát hiện $Income_m \le 0$ nhưng $Expense_m > 0$ (người dùng không có thu nhập trong tháng nhưng vẫn có chi tiêu sinh hoạt).
2. Nếu thực hiện phép chia trực tiếp để tính $SavingsRate_m$, hệ thống sẽ gặp lỗi chia cho 0.
3. Theo quy tắc xử lý biên `FR-REP-01`, hệ thống áp dụng hàm từng nhánh, gán $SavingsRate_m = 0.00\%$, hiển thị dòng tiền thuần âm ($NetCashFlow_m = -Expense_m$) và đưa ra chỉ báo thâm hụt ngân sách.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc chuỗi 12 tháng gần nhất theo `FR-REP-01`: Bảng thống kê luôn gồm đúng 12 tháng liên tục lùi từ tháng kết thúc chỉ định (mặc định tháng hiện tại), tự động thích ứng với mốc thời gian được chọn.
2. Quy tắc tính toán tài chính theo `BR-FIN-01`: Toàn bộ chỉ số thu nhập, chi tiêu, dòng tiền thuần đều tính bằng kiểu dữ liệu tiền tệ số thập phân chính xác cố định. Các chỉ số áp dụng quy tắc làm tròn ngân hàng với 2 chữ số thập phân, không có sai số dấu phẩy động.
3. Quy tắc sự kiện của tài sản đầu tư `BR-FIN-07`: Thu nhập đầu tư của tháng chỉ gồm số tiền nhận từ sự kiện Nhận tiền mặt ghi theo ca sử dụng `UC-INV-05` và `FR-INV-05`. Khoản này hiển thị ở dòng riêng "trong đó thu nhập đầu tư" theo `QD-76`, `QD-99`, `QD-122` và `QD-237`.
4. Yêu cầu chức năng liên quan: `FR-REP-01`, `FR-INV-05`.

---

### 6.39 UC-REP-02: Phân tích cơ cấu thu chi theo nhóm và danh mục

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-REP-02 |
| Tên | Phân tích cơ cấu thu chi theo nhóm và danh mục |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Phân tích cơ cấu thu chi 2 tầng phân cấp theo nhóm và danh mục, đối chiếu biến động so với kỳ liền trước. |
| Mô tả tóm tắt | Phân tích cơ cấu thu chi 2 tầng phân cấp theo Nhóm nhu cầu và Danh mục chi tiết trong một khoảng thời gian hoặc tháng được chọn, kèm so sánh biến động với kỳ trước liền kề theo `FR-REP-02`. |
| Điều kiện kích hoạt | Người dùng cần phân tích cơ cấu thu chi theo nhóm và danh mục của một tháng cụ thể. |

#### Tiền điều kiện

1. Hệ thống đã khởi chạy và đọc thành công tệp dữ liệu giao dịch.
2. Người dùng đang ở giao diện nhóm chức năng Thống kê và Báo cáo tài chính.

#### Bảo đảm tối thiểu

1. Hệ thống hoạt động ở chế độ chỉ đọc, không làm biến đổi bất kỳ dữ liệu phân loại hay số dư ví nào trong hệ thống.

#### Hậu điều kiện thành công

1. Báo cáo cơ cấu thu chi 2 tầng phân cấp (Nhóm nhu cầu -> Danh mục) phản ánh chính xác tỷ lệ và biến động so với kỳ liền kề theo `FR-REP-02`.

#### Luồng sự kiện chính

1. Người dùng chọn tab "Cơ cấu thu chi", chọn khoảng thời gian cần phân tích (mặc định tháng hiện tại) và chọn loại luồng tiền (`EXPENSE` hoặc `INCOME`).
2. Hệ thống quét các giao dịch trong khoảng thời gian được chọn có loại tương ứng.
3. Hệ thống tổng hợp số liệu theo 2 tầng phân cấp theo quy tắc `FR-REP-02`:
   - Cấp nhóm nhu cầu: Tính tổng số tiền và tỷ lệ % trên tổng thu hoặc chi của kỳ:
    $$Share_{group} = \left(\frac{Amount_{group}}{TotalPeriod}\right) \times 100$$
   - Cấp danh mục chi tiết: Tính tổng số tiền, tỷ lệ % trên nhóm cha và tỷ lệ % trên toàn bộ kỳ:
    $$
    Share_{cat/group} = \left(\frac{Amount_{cat}}{Amount_{group}}\right) \times 100\%, \quad Share_{cat/period} = \left(\frac{Amount_{cat}}{TotalPeriod}\right) \times 100\%
    $$
4. Hệ thống truy vấn số liệu của kỳ liền trước có cùng độ dài để tính toán so sánh theo `QD-238`:
   - Chênh lệch tuyệt đối:
    $$\Delta Amount = Amount_{curr} - Amount_{prev}$$
   - Tỷ lệ phần trăm biến động:
    $$
    \% Change = \begin{cases} \left(\frac{\Delta Amount}{Amount_{prev}}\right) \times 100\%, & \text{khi } Amount_{prev} > 0 \\ \text{"Mới"}, & \text{khi } Amount_{prev} = 0 \end{cases}
    $$
5. Hệ thống sắp xếp các danh mục theo số tiền chi tiêu giảm dần.
6. Hệ thống hiển thị bảng Pivot phân cấp kèm biểu đồ tròn cơ cấu chi tiêu. Nhóm và danh mục đã xóa vẫn hiển thị kèm nhãn (đã xóa) theo quy tắc hiển thị của `FR-REP-02`.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Kỳ trước không có giao dịch khi so sánh biến động

1. Tại bước 4 của luồng chính, hệ thống phát hiện kỳ trước không có giao dịch trong danh mục này ($Amount_{prev} = 0$) nhưng kỳ này có giao dịch ($Amount_{curr} > 0$).
2. Hệ thống hiển thị chênh lệch tuyệt đối $\Delta Amount = Amount_{curr}$ và hiển thị tỷ lệ biến động là "Mới" theo `QD-238`, tránh thực hiện phép chia cho 0.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc tính toán tài chính theo `BR-FIN-01`: Toàn bộ các phép tổng hợp và tỷ lệ phần trăm tính bằng kiểu dữ liệu tiền tệ số thập phân chính xác cố định. Các phép tính áp dụng quy tắc làm tròn ngân hàng (2 chữ số thập phân cho số tiền, 4 chữ số thập phân cho tỷ lệ).
2. Quy tắc hiển thị mục đã xóa: Nhóm và danh mục đã xóa giữ tên gốc kèm nhãn (đã xóa), không gộp vào dòng Khác. Dòng số 0 chỉ hiện cho kỳ quá khứ từ kỳ chứa ngày mục được tạo. Kỳ trước ngày tạo và kỳ ở tương lai ẩn dòng.
3. Yêu cầu chức năng liên quan: `FR-REP-02`.

---

### 6.40 UC-REP-03: Tổng hợp tài sản và định giá tài sản ròng

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-REP-03 |
| Tên | Tổng hợp tài sản và định giá tài sản ròng |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Kiểm soát tài sản ròng hợp nhất từ toàn bộ 5 thành phần tài sản và nợ phải trả của hệ thống. |
| Mô tả tóm tắt | Hợp nhất báo cáo tổng tài sản và tài sản ròng từ 5 thành phần (ví tiền, sổ tiết kiệm, tài sản đầu tư, nợ phải thu, nợ phải trả) tại ngày chốt số liệu theo `FR-REP-03`. |
| Điều kiện kích hoạt | Người dùng cần tra cứu tài sản ròng hiện tại của toàn hệ thống. |

#### Tiền điều kiện

1. Hệ thống đã khởi chạy và đọc thành công tệp dữ liệu giao dịch.
2. Người dùng đang ở giao diện nhóm chức năng Thống kê và Báo cáo tài chính.

#### Bảo đảm tối thiểu

1. Hệ thống hoạt động ở chế độ chỉ đọc, không làm biến đổi bất kỳ giá trị tài sản hay số dư ví nào trong hệ thống.

#### Hậu điều kiện thành công

1. Báo cáo tài sản ròng hợp nhất chính xác 5 thành phần (TotalWalletBalance + Savings + Investments + Receivables - Payables) theo `FR-REP-03`.

#### Luồng sự kiện chính

1. Người dùng chọn tab "Báo cáo tài sản ròng" và có thể tùy chọn Ngày chốt số liệu ($asOfDate$, mặc định là ngày hiện tại).
2. Hệ thống truy vấn đồng thời 5 thành phần tài sản tại thời điểm $asOfDate$ theo quy tắc `FR-REP-03`:
   - Thành phần 1 (Tiền trong ví $TotalWalletBalance$): Tổng số dư các ví ở trạng thái `ACTIVE` và các ví ở trạng thái `ARCHIVED` có số dư lớn hơn 0.00 VNĐ theo `FR-WAL-04`.
   - Thành phần 2 (Tiền gửi tiết kiệm $Savings$): Tổng giá trị hiện tại của các sổ tiết kiệm đang ở trạng thái `ACTIVE` theo quy tắc `BR-FIN-05`.
   - Thành phần 3 (Tài sản đầu tư $Investments$): Tổng giá trị thị trường các tài sản đầu tư còn đơn vị nắm giữ theo quy tắc `BR-FIN-06`.
   - Thành phần 4 (Nợ phải thu $Receivables$): Tổng số tiền còn phải thu (cho vay trừ đã thu và đã xóa nợ) theo quy tắc `BR-FIN-09`.
   - Thành phần 5 (Nợ phải trả $Payables$): Tổng số tiền còn phải trả (đi vay trừ đã trả và đã được miễn trừ) theo quy tắc `BR-FIN-09`.
3. Hệ thống tính toán:
$$NetWorth = TotalWalletBalance + Savings + Investments + Receivables - Payables$$
$$TotalAssets = TotalWalletBalance + Savings + Investments + Receivables$$
4. Hệ thống tính tỷ lệ phần trăm của từng thành phần trên tổng tài sản gộp $TotalAssets$:
$$Share_i = \left(\frac{Asset_i}{TotalAssets}\right) \times 100$$
Trong đó $Asset_i$ là một trong 4 thành phần cộng vào $TotalAssets$ (tiền trong ví, tiền gửi tiết kiệm, tài sản đầu tư, nợ phải thu). Nợ phải trả không thuộc $TotalAssets$ nên không có tỷ lệ.
Nếu $TotalAssets = 0.00$ VNĐ, hệ thống gán tỷ lệ $Share_i = 0.00\%$ cho toàn bộ các thành phần để tránh lỗi chia cho 0.
5. Hệ thống hiển thị bảng cân đối tài sản cá nhân kèm biểu đồ thể hiện tỷ lệ các loại tài sản.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3a: Người dùng chỉ có nợ phải trả mà không có tài sản gộp

1. Tại bước 3 của luồng chính, hệ thống phát hiện tổng tài sản gộp bằng 0 ($TotalAssets = 0.00$ VNĐ) trong khi có nợ phải trả ($TotalLiabilities > 0$).
2. Hệ thống hiển thị tài sản ròng $NetWorth$ mang giá trị âm chính xác ($NetWorth = -TotalLiabilities$), đồng thời hiển thị tỷ lệ cơ cấu các thành phần tài sản bằng 0.00% theo tiêu chí `TC-REP-03.2`.
3. Luồng tiếp tục tại bước 5 của luồng chính.

##### Luồng ngoại lệ 3b: Toàn bộ các thành phần tài sản và công nợ đều bằng 0

1. Tại bước 3 của luồng chính, hệ thống phát hiện toàn bộ 5 thành phần đều bằng 0 ($TotalAssets = 0.00$ VNĐ, $TotalLiabilities = 0.00$ VNĐ).
2. Hệ thống hiển thị $NetWorth = 0.00$ VNĐ và tỷ lệ các thành phần bằng 0.00%.
3. Luồng tiếp tục tại bước 5 của luồng chính.

#### Quy tắc nghiệp vụ đặc thù

1. Tài sản ròng theo `FR-REP-03`: Luôn cân bằng giữa 5 thành phần, phản ánh trung thực toàn bộ tiền trong ví, tài sản đầu tư, tiền gửi tiết kiệm và công nợ.
2. Yêu cầu chức năng liên quan: `FR-REP-03`.

---

### 6.41 UC-REP-04: Lập sao kê công nợ chi tiết theo người vay và người cho vay

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-REP-04 |
| Tên | Lập sao kê công nợ chi tiết theo người vay và người cho vay |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | So khớp lịch sử công nợ lũy kế theo từng người vay hoặc người cho vay và tổng hợp công nợ toàn hệ thống. |
| Mô tả tóm tắt | Tra cứu sao kê công nợ lũy kế theo từng người vay hoặc người cho vay, hoặc xem tổng hợp công nợ toàn hệ thống, phản ánh đúng số tiền còn nợ theo `FR-REP-04` và `BR-FIN-09`. |
| Điều kiện kích hoạt | Người dùng cần so khớp công nợ với một người vay hoặc người cho vay cụ thể, hoặc tra cứu tổng hợp công nợ toàn hệ thống. |

#### Tiền điều kiện

1. Hệ thống đã có ít nhất một bản ghi trong danh sách `debt`.
2. Người dùng đang ở giao diện nhóm chức năng Thống kê và Báo cáo tài chính (hoặc Quản lý Công nợ).

#### Bảo đảm tối thiểu

1. Hệ thống hoạt động ở chế độ chỉ đọc, không làm biến đổi bất kỳ dữ liệu công nợ hay số dư ví nào trong hệ thống.

#### Hậu điều kiện thành công

1. Sao kê công nợ theo từng người vay hoặc người cho vay phản ánh đúng lịch sử lũy kế và số tiền còn nợ theo `FR-REP-04`.
2. Khi bộ lọc trạng thái là `ALL` hoặc `ACTIVE`, tổng số tiền còn phải thu và còn phải trả trên sao kê của toàn bộ người vay và người cho vay khớp chính xác với chỉ số $Receivables$ và $Payables$ trên báo cáo tài sản ròng toàn hệ thống.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Sao kê công nợ theo người vay và người cho vay". Người dùng có thể xem tổng hợp toàn hệ thống hoặc chọn một đối tác nợ cụ thể, và chọn bộ lọc trạng thái `ALL` (mặc định), `ACTIVE` hoặc `PAID` cho cả hai trường hợp theo `QD-238`.
2. Nếu xem tổng hợp toàn hệ thống, hệ thống tính, trên các khoản thỏa bộ lọc trạng thái, tổng nợ phải thu ($Receivables = \sum_{person} Receivable_{person}$) và tổng nợ phải trả ($Payables = \sum_{person} Payable_{person}$) trên toàn bộ người vay và người cho vay, cùng công nợ ròng toàn hệ thống ($NetDebtPosition = Receivables - Payables$).
3. Nếu chọn một người vay hoặc người cho vay cụ thể, hệ thống trích xuất toàn bộ các bản ghi trong danh sách `debt` liên quan đến cá nhân này và thỏa bộ lọc trạng thái, sắp xếp theo thứ tự ngày tăng dần theo quy tắc `FR-REP-04`.
4. Với từng giao dịch, hệ thống tính số tiền còn nợ lũy kế: nghiệp vụ cho vay (`LEND`) tăng số tiền còn phải thu; nghiệp vụ thu nợ (`COLLECT`) hoặc xóa nợ cho vay (`WRITE_OFF_LEND`) giảm số tiền còn phải thu. Nghiệp vụ đi vay (`BORROW`) tăng số tiền còn phải trả; nghiệp vụ trả nợ (`REPAY`) hoặc được xóa nợ đi vay (`WRITE_OFF_BORROW`) giảm số tiền còn phải trả.
5. Hệ thống tổng kết, trên các khoản thỏa bộ lọc trạng thái, theo từng người vay hoặc người cho vay theo `FR-REP-04` và `QD-238`. Chỉ số cho vay gồm: Tổng đã cho vay ($TotalLend$), Tổng đã thu nợ ($TotalCollected$), Tổng nợ cho vay đã xóa ($TotalWriteOffLend$), và Số tiền còn phải thu ($Receivable_{person}$). Chỉ số đi vay gồm: Tổng đã đi vay ($TotalBorrow$), Tổng đã trả nợ ($TotalRepaid$), Tổng nợ đi vay được miễn trừ ($TotalWriteOffBorrow$), Số tiền còn phải trả ($Payable_{person}$), cùng Công nợ ròng ($NetDebtPosition = Receivable_{person} - Payable_{person}$).
6. Hệ thống hiển thị bảng sao kê chi tiết phục vụ so khớp và theo dõi nợ phải thu, nợ phải trả, kèm công nợ ròng.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

Không có luồng ngoại lệ nghiệp vụ đặc thù bổ sung ngoài việc hiển thị sao kê rỗng kèm thông báo phù hợp khi đối tác được chọn chưa từng có giao dịch công nợ, hoặc khi không có khoản nào thỏa bộ lọc trạng thái ở đối tác được chọn hay trên toàn hệ thống.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc công nợ luôn khớp theo `FR-REP-04` và `BR-FIN-09`: Khi bộ lọc trạng thái là `ALL` hoặc `ACTIVE`, tổng số tiền còn phải thu và còn phải trả trên sao kê của toàn bộ người vay và người cho vay phải khớp chính xác với chỉ số $Receivables$ và $Payables$. Hai chỉ số này nằm trên báo cáo tài sản ròng toàn hệ thống. Với bộ lọc `PAID`, các tổng bằng 0.00 VNĐ vì chỉ gồm các khoản đã tất toán.
2. Yêu cầu chức năng liên quan: `FR-REP-04`.

---

### 6.42 UC-DAT-01: Xuất dữ liệu tài chính

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-DAT-01 |
| Tên | Xuất dữ liệu tài chính |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Sao lưu dữ liệu tài chính cục bộ ra định dạng mở, giữ nguyên vẹn độ chính xác số học, không làm tròn. |
| Mô tả tóm tắt | Xuất dữ liệu tiền trong hệ thống (toàn phần hoặc từng phần) ra định dạng JSON, hoặc ra định dạng CSV cho giao dịch thu chi, giữ độ chính xác của các trường số học tài chính theo `FR-DAT-01`. |
| Điều kiện kích hoạt | Người dùng cần sao lưu định kỳ hoặc xuất dữ liệu ra tệp định dạng mở. |

#### Tiền điều kiện

1. Hệ thống PFMS đang hoạt động bình thường, có quyền đọc và ghi trên thư mục lưu trữ cục bộ được chỉ định.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

1. Quá trình xuất dữ liệu không làm biến đổi dữ liệu hiện có trong hệ thống; nếu xảy ra lỗi ghi đĩa, hệ thống dọn dẹp tệp xuất dở dang và giữ nguyên toàn bộ dữ liệu nội bộ.

#### Hậu điều kiện thành công

1. Tệp sao lưu được tạo thành công với mã hóa UTF-8, các trường số học được lưu dưới dạng chuỗi số học đầy đủ chữ số (không bị làm tròn, không dùng ký hiệu khoa học). Tệp đặt tên tự động theo cấu trúc `pfms_export_{scope}_{yyyyMMdd_HHmmss}.{ext}` theo quy tắc `FR-DAT-01`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Xuất dữ liệu tài chính".
2. Hệ thống hiển thị biểu mẫu xuất dữ liệu gồm các tùy chọn:
   - Phạm vi dữ liệu: `FULL` (Toàn bộ hệ thống), `TRANSACTIONS` (Sổ nhật ký thu chi), `DEBTS` (Sổ công nợ), `SAVINGS` (Sổ tiết kiệm), `INVESTMENTS` (Tài sản đầu tư).
   - Định dạng tệp: `JSON` (dữ liệu phân cấp) hoặc `CSV` (chỉ áp dụng khi chọn phạm vi `TRANSACTIONS`).
   - Thư mục lưu trữ trên máy tính: Người dùng chọn qua hộp thoại duyệt thư mục.
3. Người dùng chọn phạm vi là `FULL`, định dạng `JSON`, và chọn thư mục lưu trữ đích.
4. Người dùng xác nhận bắt đầu tiến trình xuất dữ liệu.
5. Hệ thống trích xuất toàn bộ dữ liệu hiện tại từ tệp dữ liệu gồm các nhóm sau:
   - ví tiền, danh mục phân loại, giao dịch thu chi, chuyển tiền nội bộ và công nợ
   - sổ tiết kiệm
   - tài sản đầu tư (cùng lệnh mua, lệnh bán, sự kiện và khoản đầu tư)
   - lịch sử tài sản ròng, quỹ mục tiêu và bản ghi điều chỉnh
6. Hệ thống chuyển đổi các đối tượng nghiệp vụ sang cấu trúc JSON:
   - Chuẩn hóa toàn bộ các trường số học tài chính thành chuỗi số học chính xác (ví dụ: `"50000000.00"`), cấm chuyển đổi sang kiểu số thực có sai số hoặc ký hiệu số mũ khoa học (`5.0E7`) theo quy tắc `FR-DAT-01`.
   - Định dạng các trường ngày tháng theo định dạng `yyyy-MM-dd`.
7. Hệ thống tạo tên tệp theo quy tắc: `pfms_export_full_{yyyyMMdd_HHmmss}.json`.
8. Hệ thống ghi tệp an toàn xuống đĩa cứng với bảng mã UTF-8 không BOM.
9. Hệ thống kiểm tra kích thước và độ nguyên vẹn của tệp vừa tạo.
10. Hệ thống hiển thị thông báo xuất dữ liệu thành công kèm đường dẫn tệp, kích thước tệp và tổng số lượng bản ghi đã xuất của từng nhóm dữ liệu.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 8a: Lỗi quyền ghi tệp hoặc ổ đĩa đầy khi xuất dữ liệu

1. Tại bước 8 của luồng chính, hệ thống không thể ghi tệp xuống thư mục chỉ định do không đủ quyền truy cập hệ thống tệp hoặc ổ đĩa đã hết dung lượng lưu trữ.
2. Hệ thống phát hiện lỗi ghi tệp, hiển thị thông báo: "Không thể lưu tệp dữ liệu vào thư mục đã chọn do lỗi quyền truy cập hoặc ổ đĩa đầy. Vui lòng chọn thư mục khác".
3. Thao tác xuất bị hủy bỏ và không để lại tệp rác trên đĩa cứng.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc giữ độ chính xác số học theo `BR-FIN-01`, `FR-DAT-01`: Không làm tròn hoặc sử dụng ký hiệu khoa học đối với trường dữ liệu số học tài chính khi xuất tệp sao lưu.
2. Yêu cầu chức năng liên quan: `FR-DAT-01`.

---

### 6.43 UC-DAT-02: Phục hồi dữ liệu hệ thống từ bản sao lưu

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-DAT-02 |
| Tên | Phục hồi dữ liệu hệ thống từ bản sao lưu |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Phục hồi dữ liệu tài chính từ một tệp sao lưu đã xuất trước đó mà không làm vi phạm các quy tắc tài chính hiện hành. |
| Mô tả tóm tắt | Phục hồi dữ liệu hệ thống từ một tệp sao lưu qua quy trình kiểm tra dữ liệu 4 bước (cú pháp, danh mục theo `BR-FIN-04`, ví tiền và số dư không âm theo `BR-FIN-02`) kèm việc hoàn nguyên tự động theo `FR-DAT-02`. |
| Điều kiện kích hoạt | Người dùng cần phục hồi dữ liệu từ một tệp sao lưu đã có trước đó. |

#### Tiền điều kiện

1. Hệ thống PFMS đang hoạt động bình thường, có quyền đọc trên tệp sao lưu được chỉ định.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

1. Nếu tệp sao lưu không hợp lệ hoặc quá trình nhập bị hủy, toàn bộ dữ liệu hệ thống hiện tại được giữ nguyên vẹn 100%, không ghi bất kỳ dữ liệu sai lệch nào vào bộ lưu trữ.

#### Hậu điều kiện thành công

1. Dữ liệu từ tệp sao lưu được phục hồi đầy đủ vào hệ thống theo chế độ đã chọn (`OVERWRITE` hoặc `MERGE`), toàn bộ quy tắc số dư `BR-FIN-02` và danh mục `BR-FIN-04` luôn đúng.
2. Nếu tệp nhập chứa dữ liệu lỗi: Toàn bộ thao tác bị hủy bỏ và hoàn nguyên nguyên trạng, dữ liệu hệ thống hiện tại không bị thay đổi hoặc làm sai lệch.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Nhập dữ liệu từ tệp sao lưu".
2. Hệ thống hiển thị biểu mẫu nhập dữ liệu: Hộp thoại chọn tệp sao lưu từ máy tính (`.json` hoặc `.csv`), và Tùy chọn chế độ nhập (`OVERWRITE`: ghi đè toàn bộ, hoặc `MERGE`: hợp nhất bổ sung).
3. Người dùng chọn tệp sao lưu JSON hợp lệ, chọn chế độ `OVERWRITE`.
4. Hệ thống hiển thị hộp thoại cảnh báo: "Chế độ ghi đè sẽ thay thế toàn bộ dữ liệu tài chính hiện tại bằng dữ liệu từ tệp sao lưu. Xác nhận tiếp tục thao tác?".
5. Người dùng xác nhận tiếp tục thao tác ghi đè dữ liệu.
6. Hệ thống phân tích cú pháp tệp JSON và thực hiện quy trình kiểm tra dữ liệu 4 bước theo quy tắc `FR-DAT-02`:
   - Bước 1 (Kiểm tra định dạng): Kiểm tra cấu trúc cú pháp tệp và tính hợp lệ của định dạng ngày tháng YYYY-MM-DD, chuỗi số học.
   - Bước 2 (Danh mục đầy đủ theo `BR-FIN-04`): Mọi mã danh mục được các giao dịch dùng tới phải thuộc về nhóm hợp lệ (đang dùng hoặc đã xóa) và có thuộc tính Loại thu chi tương thích.
   - Bước 3 (Ví tiền đầy đủ): Mọi giao dịch, chuyển tiền, công nợ, tiết kiệm, lệnh mua của tài sản đầu tư phải trỏ tới một ví tồn tại trong danh sách ví.
   - Bước 4 (Số dư ví luôn đúng theo `BR-FIN-02`): Tính lại số dư của từng ví từ lịch sử giao dịch trong tệp, bảo đảm không có ví thông thường nào có số dư âm.
7. Nếu toàn bộ 4 bước kiểm tra đều ĐẠT:
   - Hệ thống bắt đầu một lần ghi tệp an toàn.
   - Xóa dữ liệu cũ (nếu là `OVERWRITE`) và đọc toàn bộ các bản ghi từ tệp vào tệp dữ liệu hệ thống.
   - Hệ thống hoàn tất lần ghi tệp.
   - Tính lại toàn bộ số dư từ lịch sử giao dịch, quỹ mục tiêu và các bảng thống kê 12 tháng gần nhất.
8. Hệ thống hiển thị báo cáo tổng kết nhập dữ liệu thành công gồm: tổng số bản ghi xử lý, số bản ghi thêm mới, số bản ghi cập nhật, và số lượng từng loại thực thể đã phục hồi.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Người dùng hủy bỏ thao tác khi được cảnh báo ghi đè

1. Tại bước 4 của luồng chính, trước cảnh báo ghi đè toàn bộ dữ liệu, Người dùng chọn "Hủy bỏ".
2. Hệ thống dừng thao tác nhập, đóng hộp thoại và giữ nguyên hiện trạng dữ liệu.

##### Luồng ngoại lệ 6a: Tệp sao lưu không đúng định dạng cú pháp hoặc bị hỏng

1. Tại bước 6 của luồng chính, hệ thống đọc tệp và phát hiện tệp tin bị lỗi định dạng JSON hoặc CSV (thiếu dấu ngoặc, tệp rỗng, sai số cột hoặc sai mã hóa ký tự).
2. Hệ thống hủy bỏ tiến trình nhập và thông báo lỗi: "Tệp sao lưu bị lỗi cấu trúc cú pháp hoặc bị hỏng dữ liệu. Vui lòng kiểm tra lại tệp nguồn".
3. Dữ liệu hiện tại trong hệ thống được giữ nguyên.

##### Luồng ngoại lệ 6b: Dữ liệu trong tệp vi phạm quy tắc số dư và liên kết dữ liệu

1. Tại bước 6 của luồng chính, hệ thống phát hiện ít nhất một lỗi vi phạm. Ví dụ, giao dịch chi tiêu trỏ tới một ví không tồn tại, hoặc lịch sử giao dịch trong tệp làm cho số dư ví tiền mặt bị âm ($WalletBalance_{wallet} < 0$).
2. Hệ thống lập tức hủy bỏ toàn bộ quá trình nhập, không ghi bất kỳ bản ghi nào vào tệp dữ liệu.
3. Hệ thống hiển thị bảng thông báo lỗi chi tiết: liệt kê số dòng bị lỗi, loại vi phạm (vi phạm liên kết dữ liệu hoặc vi phạm quy tắc số dư luôn đúng `BR-FIN-02`) để người dùng sửa chữa tệp trước khi thử lại.

#### Quy tắc nghiệp vụ đặc thù

1. Chỉ nhập khi tất cả đều đạt: Quá trình nhập dữ liệu phải tuân thủ nguyên tắc All-or-Nothing; nếu xuất hiện bất kỳ lỗi nào, toàn bộ quá trình phải được hoàn nguyên về trạng thái trước đó.
2. Quy tắc kiểm tra 4 bước theo `FR-DAT-02`: Không ghi dữ liệu vào tệp dữ liệu nếu chưa vượt qua đầy đủ 4 bước kiểm tra cấu trúc, danh mục theo `BR-FIN-04`, ví và số dư không âm theo `BR-FIN-02`.
3. Yêu cầu chức năng liên quan: `FR-DAT-02`.

---

### 6.44 UC-DAT-03: Kiểm tra lại dữ liệu hệ thống

Bảng thuộc tính ca sử dụng:

| Thuộc tính | Đặc tả chi tiết |
| --- | --- |
| Mã | UC-DAT-03 |
| Tên | Kiểm tra lại dữ liệu hệ thống |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | So khớp độc lập 4 quy tắc tài chính luôn đúng của hệ thống để phát hiện sớm sai lệch dữ liệu. |
| Mô tả tóm tắt | Chạy công cụ kiểm tra lại độc lập để phát hiện các sai lệch theo `FR-DAT-03`. Các sai lệch gồm: số dư ví, hai vế chuyển tiền không khớp nhau, giao dịch gắn với danh mục không có trong dữ liệu (mục đã xóa vẫn tính là có), và vi phạm quy tắc công nợ luôn đúng. |
| Điều kiện kích hoạt | Người dùng cần chạy kiểm tra lại dữ liệu định kỳ hoặc khi nghi ngờ có sai lệch. |

#### Tiền điều kiện

1. Hệ thống PFMS đang hoạt động bình thường, có quyền đọc trên tệp dữ liệu cục bộ.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

1. Quá trình kiểm tra tính toàn vẹn hoạt động ở chế độ chỉ đọc, không làm biến đổi bất kỳ dữ liệu giao dịch, số dư ví hay liên kết tài sản nào trong hệ thống.

#### Hậu điều kiện thành công

1. Báo cáo kiểm tra lại dữ liệu phản ánh trung thực trạng thái của 4 quy tắc luôn đúng của hệ thống theo `FR-DAT-03`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Kiểm tra lại dữ liệu hệ thống".
2. Hệ thống tự động chạy tiến trình quét toàn bộ tệp dữ liệu, kiểm tra lần lượt 4 quy tắc luôn đúng theo quy tắc `FR-DAT-03`:
   - Bước 1 (Kiểm tra lại số dư ví theo `BR-FIN-02`): Quét toàn bộ lịch sử giao dịch của từng ví, tính số dư độc lập theo công thức `BR-FIN-02` và so sánh với số dư ghi nhận của từng ví. Kiểm tra sai số $\Delta = 0.00$ VNĐ.
   - Bước 2 (Kiểm tra lại ghi hai vế khi chuyển tiền theo `BR-FIN-03`): Quét toàn bộ danh sách bản ghi `transfers`, kiểm tra số tiền trừ tại ví nguồn bằng chính xác số tiền cộng tại ví đích.
   - Bước 3 (Kiểm tra lại liên kết danh mục theo `BR-FIN-04`): Quét danh sách `transactions`, kiểm tra không có giao dịch nào gắn với danh mục không có trong dữ liệu (mục đã xóa vẫn tính là có) hoặc không khớp Loại thu chi.
   - Bước 4 (Kiểm tra lại công nợ theo `BR-FIN-09`): Kiểm tra mọi người vay và người cho vay đều có số tiền còn phải thu và còn phải trả $\ge 0$.
3. Hệ thống tổng hợp kết quả thành Báo cáo Kiểm tra lại Dữ liệu, phân loại mức độ nghiêm trọng thành 3 mức: `INFO`, `WARNING`, `ERROR`.
4. Nếu tất cả đều ĐẠT, hệ thống hiển thị trạng thái hợp lệ và thông báo: "Dữ liệu hệ thống đạt 100% yêu cầu về liên kết dữ liệu và số dư khớp nhau".
5. Nếu phát hiện sai lệch, hệ thống đánh dấu đỏ, liệt kê chi tiết mã giao dịch, số tiền chênh lệch và hướng dẫn người dùng biện pháp xử lý.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

Không có luồng ngoại lệ nghiệp vụ đặc thù bổ sung ngoài việc phân loại mức độ nghiêm trọng của sai lệch đã liệt kê ở luồng sự kiện chính.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc kiểm tra lại theo `FR-DAT-03`: Công cụ kiểm tra lại độc lập bảo đảm tự động kiểm tra 4 điều: số dư ví, ghi hai vế khi chuyển tiền, cây danh mục và số tiền còn nợ.
2. Yêu cầu chức năng liên quan: `FR-DAT-03`.

---

