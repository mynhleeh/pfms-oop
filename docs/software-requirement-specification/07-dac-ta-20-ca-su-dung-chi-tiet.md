## 6. ĐẶC TẢ 20 CA SỬ DỤNG CHI TIẾT

20 ca sử dụng của Hệ thống quản lý tài chính cá nhân mô hình hóa toàn bộ chuỗi tương tác hoàn chỉnh giữa người dùng và hệ thống để đạt được các mục tiêu nghiệp vụ cụ thể theo phương pháp Fully Dressed Use Case của Alistair Cockburn và chuẩn kỹ thuật IREB CPRE. Toàn bộ 20 ca sử dụng hiện thực hóa các yêu cầu chức năng tại Mục 4 (`FR-WAL-01` đến `FR-DAT-03`) và tuân thủ nghiêm ngặt các quy tắc nghiệp vụ tại Mục 3 (`BR-FIN-01` đến `BR-FIN-04` và `BR-NAM-01`). Hồ sơ ca sử dụng quán triệt 4 nguyên tắc kỹ thuật cốt lõi: hệ thống phục vụ một người dùng duy nhất trên môi trường máy tính để bàn cục bộ; mỗi ca sử dụng bao đóng một mục tiêu nghiệp vụ độc lập, không gộp các thao tác thành cụm "Quản lý X" mơ hồ; các giao dịch tài chính nhiều bước được ghi nhận nguyên khối hoặc tự động hoàn nguyên khi có lỗi; phân định rạch ròi giữa biến thể thành công hợp lệ và tình huống vi phạm dữ liệu.

Mỗi ca sử dụng được đặc tả nhất quán theo khuôn mẫu ca sử dụng mở rộng gồm 9 trường thông tin chuẩn:
1. Mã và tên ca sử dụng: Mã định danh phân cấp duy nhất `UC-<DOMAIN>-<NN>` và tên hành động theo cấu trúc chuẩn động từ + danh từ.
2. Bảng thuộc tính ca sử dụng: Tác nhân chính, phạm vi và cấp độ mục tiêu người dùng, các bên liên quan và mối quan tâm, mô tả tóm tắt, điều kiện kích hoạt.
3. Tiền điều kiện: Trạng thái hệ thống và điều kiện dữ liệu bắt buộc phải thỏa mãn trước khi bắt đầu thực hiện ca sử dụng.
4. Bảo đảm tối thiểu: Trạng thái hệ thống và tệp dữ liệu được hoàn nguyên nguyên vẹn khi giao dịch thất bại, bị hủy hoặc gặp lỗi dữ liệu, không để lại bản ghi rác.
5. Hậu điều kiện thành công: Trạng thái dữ liệu lâu dài được ghi nhận sau khi luồng sự kiện chính hoàn tất, bảo toàn các bất biến tài chính tại Mục 3.
6. Luồng sự kiện chính: Trình tự các bước tương tác tuần tự đánh số giữa người dùng và hệ thống từ khi bắt đầu đến khi đạt được mục tiêu.
7. Các luồng rẽ nhánh: Các biến thể thực hiện hợp lệ khác của ca sử dụng dẫn tới kết quả thành công hoặc kết thúc hợp lệ.
8. Các luồng ngoại lệ: Các tình huống vi phạm dữ liệu, vượt ngưỡng số học hoặc điều kiện biên, chỉ rõ phản ứng hệ thống, thông báo lỗi `ERR-*` và điểm phục hồi.
9. Quy tắc nghiệp vụ đặc thù: Danh sách liên kết trực tiếp tới các quy tắc nghiệp vụ `BR-*` tại Mục 3 và yêu cầu chức năng `FR-*` tại Mục 4 mà ca sử dụng trực tiếp áp dụng.

Bảng chia 20 ca sử dụng theo 6 nhóm chức năng:

| STT | Nhóm chức năng | Mã nhóm | Số lượng UC | Dải mã |
| :---: | :--- | :---: | :---: | :--- |
| 1 | Ví | `WAL` | 4 | `UC-WAL-01` đến `UC-WAL-04` |
| 2 | Ghi nhận thu chi | `TXN` | 4 | `UC-TXN-01` đến `UC-TXN-04` |
| 3 | Chuyển tiền nội bộ | `TRF` | 3 | `UC-TRF-01` đến `UC-TRF-03` |
| 4 | Danh mục phân loại | `CAT` | 4 | `UC-CAT-01` đến `UC-CAT-04` |
| 5 | Thống kê và báo cáo | `REP` | 2 | `UC-REP-01` đến `UC-REP-02` |
| 6 | Lưu trữ và sao lưu dữ liệu | `DAT` | 3 | `UC-DAT-01` đến `UC-DAT-03` |
| **Tổng** | **6 nhóm chức năng** |  | **20 UC** |  |

Bảng tổng hợp phân bổ 20 ca sử dụng của hệ thống:

| STT | Mã ca sử dụng | Tên ca sử dụng | Nhóm chức năng | Tác nhân chính | Yêu cầu chức năng ánh xạ |
| :---: | :---: | :--- | :--- | :--- | :--- |
| 1 | `UC-WAL-01` | Tạo ví mới | Ví | Người dùng | `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04` |
| 2 | `UC-WAL-02` | Theo dõi số dư tính lại từ lịch sử dòng tiền và cơ cấu ví | Ví | Người dùng | `FR-WAL-02`, `FR-WAL-04` |
| 3 | `UC-WAL-03` | Cập nhật thông tin ví | Ví | Người dùng | `FR-WAL-01`, `FR-WAL-03` |
| 4 | `UC-WAL-04` | Điều chỉnh số dư ví | Ví | Người dùng | `FR-WAL-05` |
| 5 | `UC-TXN-01` | Ghi nhận giao dịch thu chi | Ghi nhận thu chi | Người dùng | `FR-TXN-01`, `FR-TXN-02` |
| 6 | `UC-TXN-02` | Tra cứu và lọc lịch sử giao dịch thu chi | Ghi nhận thu chi | Người dùng | `FR-TXN-03` |
| 7 | `UC-TXN-03` | Chỉnh sửa giao dịch thu chi | Ghi nhận thu chi | Người dùng | `FR-TXN-04` |
| 8 | `UC-TXN-04` | Xóa giao dịch thu chi | Ghi nhận thu chi | Người dùng | `FR-TXN-05` |
| 9 | `UC-TRF-01` | Chuyển tiền nội bộ giữa hai ví | Chuyển tiền nội bộ | Người dùng | `FR-TRF-01` |
| 10 | `UC-TRF-02` | Tra cứu lịch sử chuyển tiền | Chuyển tiền nội bộ | Người dùng | `FR-TRF-02` |
| 11 | `UC-TRF-03` | Xóa giao dịch chuyển tiền | Chuyển tiền nội bộ | Người dùng | `FR-TRF-02` |
| 12 | `UC-CAT-01` | Bổ sung danh mục phân loại mới | Danh mục phân loại | Người dùng | `FR-CAT-01`, `FR-CAT-02` |
| 13 | `UC-CAT-02` | Đổi tên danh mục | Danh mục phân loại | Người dùng | `FR-CAT-02`, `FR-CAT-03` |
| 14 | `UC-CAT-03` | Khôi phục hoặc tạo lại mục đã xóa | Danh mục phân loại | Người dùng | `FR-CAT-02`, `FR-CAT-03` |
| 15 | `UC-CAT-04` | Xóa danh mục hoặc nhóm | Danh mục phân loại | Người dùng | `FR-CAT-03` |
| 16 | `UC-REP-01` | Thống kê dòng tiền 12 tháng gần nhất | Thống kê và báo cáo | Người dùng | `FR-REP-01` |
| 17 | `UC-REP-02` | Phân tích cơ cấu thu chi theo nhóm và danh mục | Thống kê và báo cáo | Người dùng | `FR-REP-02` |
| 18 | `UC-DAT-01` | Xuất dữ liệu tài chính | Lưu trữ và sao lưu dữ liệu | Người dùng | `FR-DAT-01` |
| 19 | `UC-DAT-02` | Phục hồi dữ liệu hệ thống từ bản sao lưu | Lưu trữ và sao lưu dữ liệu | Người dùng | `FR-DAT-02` |
| 20 | `UC-DAT-03` | Kiểm tra lại dữ liệu hệ thống | Lưu trữ và sao lưu dữ liệu | Người dùng | `FR-DAT-03` |

### 6.1 UC-WAL-01: Tạo ví mới

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần tạo tài khoản chứa tiền để ghi chép dòng tiền và tổng hợp tiền trong các ví chính xác. Hệ thống: Bảo đảm ví mới có số dư 0.00, tên ví hợp lệ và duy nhất theo `BR-NAM-01` và số dư luôn đúng theo quy tắc `BR-FIN-02`. |
| Mô tả tóm tắt | Tạo một ví tài chính mới với các thuộc tính định danh và ghi chú. Ví mới có số dư 0.00 VNĐ theo quy tắc luôn đúng về số dư, số tiền đang có được ghi nhận sau bằng khoản điều chỉnh số dư. Hệ thống tính lại tỷ lệ cơ cấu số dư giữa các ví. |
| Điều kiện kích hoạt | Người dùng chọn chức năng thêm ví mới trên giao diện quản trị ví. |

#### Tiền điều kiện

1. Hệ thống PFMS đã khởi chạy và đọc tệp dữ liệu cục bộ vào bộ nhớ thành công.
2. Người dùng đang ở màn hình quản lý ví.
3. Hệ thống đã đọc danh sách ví hiện có. Ở lần chạy đầu tiên, danh sách chỉ có ví tiền mặt có sẵn.

#### Bảo đảm tối thiểu

Nếu quá trình tạo ví gặp lỗi kiểm tra dữ liệu hoặc lỗi ghi tệp, trạng thái tệp dữ liệu và danh sách ví không thay đổi. Không có đối tượng ví rác nào được lưu trữ.

#### Hậu điều kiện thành công

1. Bản ghi ví mới được lưu vào tệp dữ liệu với mã duy nhất, trạng thái `ACTIVE` và số dư 0.00 VNĐ.
2. Quy tắc luôn đúng về số dư theo `BR-FIN-02` được áp dụng cho ví mới; tổng số dư trong các ví toàn hệ thống theo `FR-WAL-02` quy tắc 4 được cập nhật ngay.
3. Bảng cơ cấu số dư phản ánh đúng tỷ lệ của ví mới theo `FR-WAL-04`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Thêm ví mới" trên giao diện quản lý ví.
2. Hệ thống hiển thị biểu mẫu tạo ví gồm các trường: Tên ví, Ghi chú mô tả.
3. Người dùng nhập tên ví và nhập ghi chú mô tả (nếu có).
4. Người dùng gửi yêu cầu lưu thông tin ví mới.
5. Hệ thống kiểm tra dữ liệu đầu vào:
   - Kiểm tra định dạng tên ví theo `BR-NAM-01`. Tên sai định dạng bị từ chối theo `ERR-NAM-001`.
   - Kiểm tra tính duy nhất của tên ví theo `BR-NAM-01`. Tên trùng bị từ chối theo `ERR-NAM-002`.
6. Hệ thống tạo đối tượng ví mới trong tệp dữ liệu với mã duy nhất, gán trạng thái `ACTIVE` và số dư 0.00 VNĐ.
7. Hệ thống tính số dư tính lại từ lịch sử dòng tiền của ví mới và tổng số dư các ví toàn hệ thống theo `FR-WAL-02` quy tắc 4, bằng công thức `BR-FIN-02`.
8. Hệ thống tính lại tỷ lệ chia số dư của từng ví theo quy tắc `FR-WAL-04`.
9. Hệ thống lưu trạng thái xuống tệp dữ liệu cục bộ, hiển thị thông báo tạo ví thành công và làm mới danh sách ví cùng bảng cơ cấu số dư trên giao diện.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Trùng tên ví đã tồn tại

1. Tại bước 5 của luồng chính, hệ thống phát hiện tên ví trùng một ví hiện có theo `BR-NAM-01` (`ERR-NAM-002`).
2. Hệ thống hủy bỏ thao tác ghi, giữ nguyên dữ liệu đã nhập trên biểu mẫu và đánh dấu trường tên ví. Hệ thống hiển thị thông báo lỗi: "Tên ví này đã tồn tại trong hệ thống. Vui lòng chọn một tên khác."
3. Luồng quay lại bước 3 của luồng chính để Người dùng điều chỉnh tên ví hoặc hủy bỏ thao tác.

##### Luồng ngoại lệ 5b: Tên ví sai định dạng

1. Tại bước 5 của luồng chính, hệ thống phát hiện tên ví sai định dạng theo `BR-NAM-01` (`ERR-NAM-001`).
2. Hệ thống hủy bỏ thao tác ghi, giữ nguyên dữ liệu đã nhập trên biểu mẫu và đánh dấu trường tên ví. Hệ thống hiển thị thông báo lỗi: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Luồng quay lại bước 3 của luồng chính để Người dùng điều chỉnh tên ví hoặc hủy bỏ thao tác.

#### Quy tắc nghiệp vụ đặc thù

1. Số dư ví luôn khớp lịch sử dòng tiền theo `BR-FIN-02`. Số dư ví tại mỗi thời điểm bằng tổng dòng tiền vào trừ tổng dòng tiền ra tính đến thời điểm đó. Số dư bắt đầu từ 0.00 VNĐ khi ví được tạo.
2. Yêu cầu chức năng liên quan: `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`.

### 6.2 UC-WAL-02: Theo dõi số dư tính lại từ lịch sử dòng tiền và cơ cấu ví

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần nắm số dư khả dụng thực tế của từng tài khoản thanh toán và tỷ lệ chia dòng tiền giữa các ví để ra quyết định chi tiêu hoặc điều chuyển vốn. |
| Mô tả tóm tắt | Truy vấn và tính số dư khả dụng theo thời gian thực của từng ví dựa trên toàn bộ lịch sử dòng tiền đã có. Hệ thống phân tích tỷ lệ cơ cấu tiền trong ví giữa các ví đang hoạt động và các ví đã lưu trữ có số dư lớn hơn 0.00 VNĐ. |
| Điều kiện kích hoạt | Người dùng truy cập màn hình quản lý ví hoặc chuyển sang tab phân tích cơ cấu số dư. |

#### Tiền điều kiện

1. Hệ thống PFMS đã khởi chạy và đọc tệp dữ liệu cục bộ thành công.
2. Người dùng đang ở màn hình Quản lý ví.

#### Bảo đảm tối thiểu

Hệ thống hoạt động ở chế độ chỉ đọc, không làm thay đổi bất kỳ trường dữ liệu nào trong tệp lưu trữ.

#### Hậu điều kiện thành công

1. Số dư khả dụng của từng ví được tính toán chính xác theo công thức `BR-FIN-02` tại thời điểm truy vấn.
2. Tỷ lệ cơ cấu tiền trong các ví được hiển thị chính xác theo quy tắc `FR-WAL-04`.

#### Luồng sự kiện chính

1. Người dùng mở màn hình danh sách ví và chọn bộ lọc trạng thái (`ACTIVE`, `ARCHIVED` hoặc `ALL`, mặc định là `ACTIVE`).
2. Hệ thống quét toàn bộ các bản ghi dòng tiền liên quan đến từng ví thỏa mãn điều kiện lọc. Hệ thống áp dụng công thức `BR-FIN-02` để tính lại số dư khả dụng từ lịch sử dòng tiền:
   $$WalletBalance_w = \sum Income_w + \sum TransferIn_w - \sum Expense_w - \sum TransferOut_w + \sum Adjustment_w$$
3. Hệ thống tính tổng số dư các ví toàn hệ thống theo `FR-WAL-02` quy tắc 4 bằng phép cộng số thập phân chính xác. Tổng này gồm các ví `ACTIVE` và các ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ, không phụ thuộc bộ lọc hiển thị.
4. Hệ thống hiển thị bảng danh sách ví gồm: Mã ví, Tên ví, Trạng thái, Số dư tính lại từ lịch sử dòng tiền. Chân bảng có dòng tổng số dư khả dụng toàn hệ thống.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Xem phân tích cơ cấu tiền trong ví

1. Tại bước 1 của luồng chính, Người dùng chọn tab "Cơ cấu phân bổ ví".
2. Hệ thống tính tổng số dư trong các ví. Tổng này bằng tổng số dư các ví ở trạng thái `ACTIVE` cộng tổng số dư các ví ở trạng thái `ARCHIVED` có số dư lớn hơn 0.00 VNĐ.
3. Nếu tổng số dư trong các ví lớn hơn 0, hệ thống tính tỷ lệ của từng ví theo công thức sau:
   $$Rate_w = \left(\frac{WalletBalance_w}{TotalWalletBalance}\right) \times 100\%$$
   Trong công thức, `TotalWalletBalance` là tổng số dư trong các ví và `WalletBalance_w` là số dư của ví đang xét. Phép tính thực hiện với tối thiểu 4 chữ số thập phân, làm tròn theo quy tắc ngân hàng và định dạng hiển thị 2 chữ số thập phân (ví dụ: 35.50%).
4. Hệ thống sắp xếp danh sách ví theo thứ tự tỷ lệ giảm dần và xuất dữ liệu ra bảng thống kê kèm biểu đồ trực quan.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3a: Tổng số dư toàn bộ các ví bằng 0 khi phân tích cơ cấu

1. Tại bước 3 của luồng rẽ nhánh 1a, hệ thống thấy tổng số dư trong các ví bằng 0.
2. Hệ thống bỏ qua phép chia cho 0 để tránh lỗi số học theo mã `ERR-FIN-002`, tự động gán tỷ lệ 0.00% cho toàn bộ các ví.
3. Hệ thống hiển thị bảng thống kê với tỷ lệ 0.00% kèm thông báo trạng thái: "Tổng số dư các ví hiện tại bằng 0, không có cơ cấu phân bổ."

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc luôn đúng về số dư theo `BR-FIN-02`: Số dư hiển thị là số dư tính lại từ lịch sử dòng tiền. Không cho phép đặt số dư bằng phép gán trực tiếp vì có thể gây sai lệch.
2. Quy tắc tính toán tài chính `BR-FIN-01`: Mọi phép tính cộng dồn và chia tỷ lệ đều dùng kiểu dữ liệu số thập phân chính xác.
3. Yêu cầu chức năng liên quan: `FR-WAL-02`, `FR-WAL-04`.

### 6.3 UC-WAL-03: Cập nhật thông tin ví

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần đổi tên, ghi chú, lưu trữ ví không dùng hoặc xóa ví tạo nhầm chưa có giao dịch. Hệ thống: Cấm can thiệp trực tiếp vào số dư ví qua biểu mẫu cập nhật. Các giao dịch liên kết vẫn phải trỏ đúng tới ví. |
| Mô tả tóm tắt | Chỉnh sửa các trường thông tin mô tả của ví (tên ví, ghi chú) và chuyển đổi trạng thái hoạt động giữa `ACTIVE` và `ARCHIVED`, hoặc xóa vĩnh viễn ví chưa phát sinh giao dịch. Trường số dư bị khóa hoàn toàn. Khi lưu trữ ví còn số dư, hệ thống đưa ra cảnh báo kiểm soát. |
| Điều kiện kích hoạt | Người dùng chọn thao tác chỉnh sửa, lưu trữ hoặc xóa một ví cụ thể trong danh sách ví. |

#### Tiền điều kiện

1. Ví cần chỉnh sửa hoặc xóa đã tồn tại trong hệ thống.
2. Người dùng đang ở màn hình Quản lý ví.

#### Bảo đảm tối thiểu

Nếu thao tác cập nhật bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, thuộc tính của ví giữ nguyên trạng thái cũ. Số dư ví không bao giờ bị biến động qua ca sử dụng này.

#### Hậu điều kiện thành công

1. Thông tin tên ví, ghi chú hoặc trạng thái hoạt động được cập nhật chính xác vào tệp dữ liệu. Trường hợp xóa thì bản ghi ví chưa có giao dịch được gỡ bỏ hoàn toàn khỏi bộ lưu trữ.
2. Số dư khả dụng của ví không bị thay đổi.
3. Nếu trạng thái chuyển sang `ARCHIVED`, ví bị loại khỏi các danh sách chọn nguồn tiền của giao dịch mới nhưng toàn bộ dữ liệu giao dịch lịch sử của ví vẫn xuất hiện trong các báo cáo. Số dư ví vẫn được cộng vào tổng số dư trong các ví.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng chỉnh sửa tại dòng dữ liệu của một ví cụ thể.
2. Hệ thống hiển thị biểu mẫu cập nhật gồm: Tên ví, Ghi chú mô tả, Trạng thái hoạt động (`ACTIVE` hoặc `ARCHIVED`). Trường Số dư ở trạng thái chỉ đọc, không cho phép chỉnh sửa trực tiếp.
3. Người dùng cập nhật tên ví mới hoặc sửa ghi chú mô tả.
4. Người dùng xác nhận lưu các thay đổi.
5. Hệ thống kiểm tra dữ liệu:
   - Kiểm tra định dạng tên ví theo `BR-NAM-01`. Tên sai định dạng bị từ chối theo `ERR-NAM-001`.
   - Kiểm tra tính duy nhất của tên ví theo `BR-NAM-01`, so với các ví khác. Tên trùng bị từ chối theo `ERR-NAM-002`.
6. Hệ thống cập nhật các trường thông tin vào đối tượng ví trong tệp dữ liệu.
7. Hệ thống thông báo cập nhật thông tin ví thành công và làm mới giao diện hiển thị.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Chuyển trạng thái ví sang lưu trữ (`ARCHIVED`)

1. Tại bước 3 của luồng chính, Người dùng thay đổi trạng thái hoạt động từ `ACTIVE` sang `ARCHIVED`.
2. Hệ thống kiểm tra số dư khả dụng hiện tại của ví theo `BR-FIN-02`. Nếu số dư khác 0, hệ thống hiển thị hộp thoại cảnh báo rủi ro xác nhận: "Ví này hiện vẫn còn số dư khả dụng là [Số dư] VNĐ. Số dư này vẫn được tính vào tổng số dư trong các ví nhưng ví sẽ bị ẩn khỏi danh sách chọn khi tạo giao dịch mới. Bạn có chắc chắn muốn lưu trữ ví này không?"
3. Người dùng chọn xác nhận tiếp tục.
4. Hệ thống cập nhật trạng thái `ARCHIVED` trong tệp dữ liệu và ẩn ví khỏi toàn bộ danh sách thả xuống chọn ví trong các chức năng tạo giao dịch mới. Hệ thống giữ hiển thị trong các báo cáo lịch sử.
5. Luồng tiếp tục tại bước 7 của luồng chính.

##### Luồng rẽ nhánh 3c: Xóa vĩnh viễn ví chưa phát sinh giao dịch

1. Tại màn hình quản lý ví, Người dùng chọn chức năng xóa một ví cụ thể.
2. Hệ thống kiểm tra lịch sử dữ liệu của ví. Nếu ví chưa từng phát sinh bất kỳ bản ghi nào trong ba sổ sau, hệ thống hiển thị hộp thoại xác nhận xóa vĩnh viễn ví. Ba sổ gồm: sổ giao dịch thu chi, sổ giao dịch chuyển tiền và sổ điều chỉnh số dư.
3. Người dùng chọn xác nhận xóa.
4. Hệ thống gỡ bỏ hoàn toàn bản ghi ví khỏi bộ lưu trữ và giải phóng tên ví.
5. Hệ thống thông báo xóa ví thành công và làm mới giao diện hiển thị.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Trùng tên ví với ví khác

1. Tại bước 5 của luồng chính, hệ thống phát hiện tên ví mới trùng một ví khác theo `BR-NAM-01` (`ERR-NAM-002`).
2. Hệ thống hủy bỏ thao tác ghi, đánh dấu trường tên ví và hiển thị thông báo lỗi: "Tên ví này đã tồn tại trong hệ thống. Vui lòng chọn một tên khác."
3. Luồng quay lại bước 3 của luồng chính để Người dùng hiệu chỉnh tên ví.

##### Luồng ngoại lệ 5b: Tên ví mới sai định dạng

1. Tại bước 5 của luồng chính, hệ thống phát hiện tên ví mới sai định dạng theo `BR-NAM-01` (`ERR-NAM-001`).
2. Hệ thống hủy bỏ thao tác ghi, đánh dấu trường tên ví và hiển thị thông báo lỗi: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Luồng quay lại bước 3 của luồng chính để Người dùng hiệu chỉnh tên ví.

##### Luồng ngoại lệ 2a: Can thiệp trái phép vào số dư ví

1. Tại bước 2 của luồng chính, nếu hệ thống phát hiện yêu cầu chỉnh sửa chứa dữ liệu thay đổi số dư (số dư hiện hành) đối với ví đã có giao dịch.
2. Hệ thống từ chối xử lý và chặn cập nhật số dư. Hệ thống ghi nhật ký cảnh báo vi phạm quy tắc dữ liệu tài chính và hiển thị thông báo lỗi: "Không được phép thay đổi trực tiếp số dư ví. Vui lòng sử dụng chức năng Điều chỉnh số dư hoặc Ghi nhận giao dịch."

##### Luồng ngoại lệ 3b: Lưu trữ ví tiền mặt có sẵn

1. Tại bước 3 của luồng chính, trước khi xét luồng rẽ nhánh 3a, Người dùng đổi trạng thái của ví tiền mặt có sẵn từ `ACTIVE` sang `ARCHIVED`.
2. Hệ thống từ chối cập nhật và thông báo lỗi: "Ví tiền mặt có sẵn không thể lưu trữ."
3. Luồng quay lại bước 3 của luồng chính.

##### Luồng ngoại lệ 3e: Từ chối xóa vĩnh viễn ví đã có lịch sử dòng tiền

1. Tại bước 2 của luồng rẽ nhánh 3c, hệ thống phát hiện ví đã có bản ghi giao dịch trong quá khứ.
2. Hệ thống từ chối xóa vĩnh viễn và hiển thị thông báo lỗi: "Ví đã có lịch sử dòng tiền nên không thể xóa vĩnh viễn. Vui lòng chuyển ví sang trạng thái lưu trữ."

##### Luồng ngoại lệ 3d: Từ chối xóa ví tiền mặt có sẵn

1. Người dùng chọn xóa ví tiền mặt có sẵn từ lần khởi tạo đầu tiên.
2. Hệ thống từ chối xóa và hiển thị thông báo lỗi: "Ví tiền mặt có sẵn không thể xóa."

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc giữ dữ liệu không sai lệch: Không hỗ trợ thao tác xóa vật lý đối với ví đã có bất kỳ bản ghi nào. Bản ghi đó thuộc sổ giao dịch thu chi, sổ giao dịch chuyển tiền hoặc sổ điều chỉnh số dư. Ví chưa từng phát sinh giao dịch được phép xóa vĩnh viễn. Muốn ngừng sử dụng ví đã có giao dịch thì chuyển sang trạng thái `ARCHIVED`.
2. Quy tắc tính toán tài chính `BR-FIN-02`: Số dư ví chỉ thay đổi gián tiếp qua các nghiệp vụ có dòng tiền hợp lệ.
3. Yêu cầu chức năng liên quan: `FR-WAL-01`, `FR-WAL-03`.

### 6.4 UC-WAL-04: Điều chỉnh số dư ví

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần đồng bộ số dư hệ thống khớp với kiểm kê thực tế mà vẫn giữ dấu vết để kiểm tra lại chênh lệch. Hệ thống: Bản ghi điều chỉnh không được phép sửa. Khoản chênh lệch phải được ghi nhận thành khoản điều chỉnh số dư độc lập để truy vết. |
| Mô tả tóm tắt | Ghi nhận khoản điều chỉnh số dư có dấu vết khi số dư thực tế lệch khỏi số dư hệ thống. Hệ thống tính chênh lệch, hiển thị bản xem trước và cập nhật số dư tính lại mà không can thiệp vào các giao dịch cũ. |
| Điều kiện kích hoạt | Người dùng phát hiện số dư thực tế khác với số dư hệ thống và chọn chức năng "Điều chỉnh số dư". |

#### Tiền điều kiện

1. Ví cần điều chỉnh tồn tại trong hệ thống và đang ở trạng thái `ACTIVE`.
2. Người dùng đang ở màn hình Quản lý ví.

#### Bảo đảm tối thiểu

Nếu Người dùng hủy bỏ hoặc thao tác ghi gặp lỗi, số dư tính lại của ví được giữ nguyên trạng; không có khoản điều chỉnh rác nào được lưu lại.

#### Hậu điều kiện thành công

1. Khoản điều chỉnh mới được lưu vào tệp dữ liệu và không được sửa về sau. Khoản điều chỉnh gồm các trường: mã khoản điều chỉnh, mã ví, ngày ghi nhận, số dư trước điều chỉnh, số dư quan sát, giá trị chênh lệch điều chỉnh, lý do điều chỉnh (nếu có).
2. Số dư tính lại của ví khớp chính xác với số dư quan sát do Người dùng nhập.
3. Quy tắc luôn đúng về số dư theo `BR-FIN-02` được thỏa mãn nhờ số hạng bổ sung là tổng các khoản điều chỉnh của ví.

#### Luồng sự kiện chính

1. Người dùng chọn một ví đang hoạt động và nhấn nút "Điều chỉnh số dư".
2. Hệ thống tính toán số dư hiện tại của ví theo `BR-FIN-02`. Hệ thống hiển thị biểu mẫu điều chỉnh gồm: Số dư hiện tại (chỉ đọc), Số dư thực tế quan sát, Lý do điều chỉnh (tùy chọn).
3. Người dùng nhập số dư thực tế quan sát được và có thể nhập lý do điều chỉnh (tùy chọn, tối đa 255 ký tự).
4. Người dùng yêu cầu xem trước kết quả điều chỉnh.
5. Hệ thống kiểm tra dữ liệu và tính khoản chênh lệch:
   $$Adjustment_w = walletObservedBalance - WalletBalance_w(\text{trước})$$
   Kiểm tra giá trị chênh lệch điều chỉnh khác 0, kiểm tra độ dài lý do điều chỉnh không vượt quá 255 ký tự (nếu có nhập), và kiểm tra số dư quan sát không âm.
6. Hệ thống hiển thị hộp thoại xác nhận thể hiện: Số dư trước điều chỉnh, Số dư sau điều chỉnh, Giá trị chênh lệch (dương hoặc âm) và Lý do điều chỉnh (nếu có).
7. Người dùng nhấn nút "Xác nhận điều chỉnh".
8. Hệ thống lưu bản ghi điều chỉnh mới vào tệp dữ liệu, cập nhật số dư tính lại của ví và hiển thị thông báo điều chỉnh thành công.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Lý do điều chỉnh vượt quá độ dài tối đa

1. Tại bước 5 của luồng chính, hệ thống phát hiện trường lý do điều chỉnh có độ dài vượt quá 255 ký tự sau khi cắt khoảng trắng ở hai đầu.
2. Hệ thống chặn thao tác, đánh dấu trường lý do và thông báo lỗi: "Lý do điều chỉnh không được vượt quá 255 ký tự."
3. Luồng quay lại bước 3 của luồng chính để Người dùng điều chỉnh lại lý do.

##### Luồng ngoại lệ 5b: Số dư quan sát trùng với số dư hệ thống

1. Tại bước 5 của luồng chính, hệ thống tính toán giá trị chênh lệch điều chỉnh bằng 0.
2. Hệ thống từ chối tạo khoản ghi rỗng và hiển thị thông báo: "Số dư nhập vào trùng với số dư hệ thống đang ghi nhận, không có chênh lệch cần điều chỉnh."
3. Luồng quay lại bước 3 của luồng chính để Người dùng kiểm tra lại số liệu.

##### Luồng ngoại lệ 5c: Số dư điều chỉnh vi phạm điều kiện trạng thái

1. Tại bước 5 của luồng chính, hệ thống phát hiện số dư quan sát nhỏ hơn 0.
2. Hệ thống từ chối ghi nhận và thông báo lỗi: "Số dư thực tế nhập vào không hợp lệ (số dư ví không được nhỏ hơn 0)."
3. Luồng quay lại bước 3 của luồng chính để Người dùng điều chỉnh lại số dư.

#### Quy tắc nghiệp vụ đặc thù

1. Khoản điều chỉnh không phá công thức số dư theo `BR-FIN-02`: Khoản điều chỉnh số dư là một số hạng độc lập trong công thức tính số dư từ lịch sử dòng tiền. Khoản này không sửa đổi hay xóa bỏ các giao dịch dòng tiền trong quá khứ.
2. Quy tắc dấu vết kiểm tra lại: Bản ghi điều chỉnh số dư không được sửa, không hỗ trợ thao tác sửa hoặc xóa sau khi đã ghi nhận.
3. Yêu cầu chức năng liên quan: `FR-WAL-05`.

### 6.5 UC-TXN-01: Ghi nhận giao dịch thu chi

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần ghi nhận chính xác khoản thu chi hàng ngày gắn với danh mục và nguồn tiền tương ứng, kể cả chi phí của giao dịch. Hệ thống: Cập nhật đồng thời số dư ví, không để ví bị âm số dư. Dữ liệu thu chi được phân loại theo cấu trúc danh mục 3 cấp. |
| Mô tả tóm tắt | Ghi chép một giao dịch thu nhập hoặc chi tiêu, phân loại theo cấu trúc danh mục 3 cấp (Loại thu chi -> Nhóm nhu cầu -> Danh mục). Giao dịch liên kết với ví thanh toán tương ứng. Hệ thống cập nhật số dư ví kèm theo việc ghi giao dịch. Nếu có chi phí, hệ thống ghi riêng chi phí thành một phí giao dịch liên kết. |
| Điều kiện kích hoạt | Người dùng chọn chức năng thêm giao dịch mới trên màn hình quản lý giao dịch thu chi. |

#### Tiền điều kiện

1. Hệ thống có ít nhất một ví ở trạng thái `ACTIVE`.
2. Cấu trúc danh mục 3 cấp đã được đọc vào hệ thống.
3. Người dùng đang ở màn hình Quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu giao dịch bị hủy bỏ hoặc xảy ra lỗi kiểm tra dữ liệu, số dư ví không bị trừ hoặc cộng. Không có bản ghi giao dịch hoặc phí giao dịch rác nào được lưu vào sổ giao dịch thu chi.

#### Hậu điều kiện thành công

1. Bản ghi giao dịch mới được lưu vào sổ giao dịch thu chi với mã duy nhất.
2. Nếu chi phí lớn hơn 0, một phí giao dịch liên kết với giao dịch mới được lưu cùng lúc vào sổ giao dịch thu chi.
3. Số dư của ví liên quan được cập nhật ngay theo quy tắc `BR-FIN-02` (trừ tiền nếu `EXPENSE`, cộng tiền nếu `INCOME`, trừ thêm chi phí nếu có).
4. Số dư của ví không âm sau giao dịch.
5. Các chỉ số tổng hợp dòng tiền tháng và báo cáo cơ cấu thu chi được cập nhật ngay.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Thêm giao dịch mới".
2. Hệ thống hiển thị biểu mẫu nhập giao dịch. Biểu mẫu gồm các trường: Ngày giao dịch (mặc định là ngày hiện tại), Loại giao dịch (`INCOME` hoặc `EXPENSE`), Ví thanh toán, Danh mục chi tiết. Biểu mẫu còn có các trường sau: Số tiền giao dịch, Chi phí (tùy chọn), Danh mục phí (khi có chi phí), Ghi chú mô tả.
3. Người dùng chọn Loại giao dịch là `EXPENSE` (Chi tiêu).
4. Hệ thống tải danh sách các Nhóm nhu cầu và Danh mục chi tiết đang dùng, thuộc loại `EXPENSE` theo quy tắc `BR-FIN-04`.
5. Người dùng chọn ngày giao dịch, chọn ví thanh toán (trạng thái `ACTIVE`), chọn danh mục chi tiết và nhập số tiền giao dịch (lớn hơn 0). Người dùng nhập chi phí tùy chọn (không âm, mặc định 0.00), chọn danh mục phí khi chi phí lớn hơn 0 và nhập ghi chú mô tả nội dung chi tiêu.
6. Người dùng xác nhận lưu giao dịch mới.
7. Hệ thống kiểm tra nghiệp vụ:
   - Số tiền giao dịch phải lớn hơn 0 và được đưa về kiểu dữ liệu số thập phân chính xác với 2 chữ số thập phân theo quy tắc `BR-FIN-01`.
   - Danh mục được chọn phải đang dùng và thuộc đúng loại `EXPENSE` theo quy tắc `BR-FIN-04`.
   - Chi phí không được âm; khi chi phí lớn hơn 0 phải có danh mục phí là danh mục chi tiêu đang dùng thuộc loại `EXPENSE` theo quy tắc `BR-FIN-04`.
   - Kiểm tra số dư ví theo `BR-FIN-02`: số dư khả dụng sau khi trừ số tiền giao dịch và chi phí phải không âm.
8. Hệ thống tạo bản ghi giao dịch mới trong sổ giao dịch thu chi kèm mã giao dịch duy nhất. Nếu chi phí lớn hơn 0, hệ thống tạo thêm một phí giao dịch liên kết với giao dịch mới.
9. Hệ thống cập nhật số dư ví: trừ số tiền giao dịch và chi phí (nếu có) khỏi số dư khả dụng của ví được chọn theo quy tắc `BR-FIN-02`.
10. Hệ thống cập nhật các chỉ số tổng hợp tài chính của tháng quan sát.
11. Hệ thống lưu tệp dữ liệu, hiển thị thông báo ghi nhận giao dịch thành công và làm mới lịch sử giao dịch thu chi.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Ghi nhận giao dịch Thu nhập (`INCOME`)

1. Tại bước 3 của luồng chính, Người dùng chọn Loại giao dịch là `INCOME`.
2. Hệ thống tải danh sách các Nhóm nhu cầu và Danh mục đang dùng, thuộc loại Thu nhập (`INCOME`) (tiền lương, tiền thưởng, v.v.). Khi có chi phí, hệ thống tải thêm danh sách danh mục đang dùng thuộc loại `EXPENSE` để Người dùng chọn danh mục phí.
3. Người dùng chọn ví nhận tiền, chọn danh mục thu nhập, nhập số tiền (lớn hơn 0) và nhập chi phí tùy chọn (không âm, mặc định 0.00). Người dùng chọn danh mục phí khi chi phí lớn hơn 0 và nhập ghi chú.
4. Tại bước 7 của luồng chính, hệ thống kiểm tra danh mục thuộc loại Thu nhập (`INCOME`) và số tiền giao dịch lớn hơn 0. Thay cho kiểm tra số dư đủ để trừ, hệ thống kiểm tra số dư ví sau khi cộng số tiền giao dịch và trừ chi phí không âm. Nếu không thỏa, luồng chuyển sang luồng ngoại lệ 7b.
5. Tại bước 9 của luồng chính, hệ thống cộng số tiền giao dịch vào số dư khả dụng của ví nhận tiền và trừ chi phí (nếu có) theo quy tắc `BR-FIN-02`.
6. Luồng tiếp tục tại bước 10 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Loại giao dịch chưa có danh mục chi tiết đang dùng

1. Tại bước 4 của luồng chính hoặc bước 2 của luồng rẽ nhánh 3a, hệ thống phát hiện loại giao dịch được chọn chưa có danh mục chi tiết đang dùng nào.
2. Hệ thống không cho tiếp tục ghi nhận và hiển thị thông báo: "Loại giao dịch này chưa có danh mục chi tiết đang dùng. Hãy thêm danh mục trước khi ghi nhận giao dịch."
3. Hệ thống đề nghị mở màn hình Quản lý danh mục thu chi để Người dùng bổ sung danh mục theo `UC-CAT-01`. Nếu Người dùng đồng ý, hệ thống mở màn hình đó và giao dịch đang nhập không được lưu.
4. Nếu Người dùng từ chối, ca sử dụng kết thúc, không lưu giao dịch và không thay đổi số dư ví.

##### Luồng ngoại lệ 7a: Số tiền giao dịch không hợp lệ

1. Tại bước 7 của luồng chính, hệ thống phát hiện giá trị nhập vào không đúng định dạng số học hoặc số tiền giao dịch nhỏ hơn hoặc bằng 0.
2. Hệ thống từ chối lưu và đánh dấu trường số tiền. Nếu dữ liệu sai định dạng số học, Hệ thống từ chối vì dữ liệu số tiền không đúng định dạng số học (`ERR-FIN-001`). Nếu số tiền giao dịch nhỏ hơn hoặc bằng 0, Hệ thống từ chối vì số tiền giao dịch phải là số dương lớn hơn 0.
3. Luồng quay lại bước 5 của luồng chính để Người dùng nhập lại số tiền hợp lệ.

##### Luồng ngoại lệ 7b: Số dư ví không đủ để thực hiện giao dịch

1. Tại bước 7 của luồng chính, hệ thống phát hiện số dư khả dụng của ví không đủ. Với chi tiêu, số dư ví nhỏ hơn tổng số tiền giao dịch và chi phí. Với thu nhập có chi phí, số dư ví sau khi cộng số tiền giao dịch và trừ chi phí nhỏ hơn 0.
2. Hệ thống từ chối thực hiện giao dịch vì số dư ví không đủ (`ERR-WAL-001`). Người dùng cần chọn ví khác hoặc điều chỉnh giảm số tiền chi tiêu hoặc chi phí.
3. Luồng quay lại bước 5 của luồng chính để Người dùng chọn ví khác hoặc điều chỉnh giảm số tiền chi tiêu hoặc chi phí.

##### Luồng ngoại lệ 7c: Danh mục đã xóa hoặc không khớp Loại giao dịch

1. Tại bước 7 của luồng chính, hệ thống phát hiện danh mục đã xóa hoặc có Loại thu chi không khớp với Loại giao dịch đã chọn.
2. Hệ thống chặn thao tác và hiển thị cảnh báo: "Danh mục không hợp lệ hoặc không tương thích với Loại giao dịch đã chọn."
3. Luồng quay lại bước 5 của luồng chính để Người dùng chọn lại danh mục hợp lệ.

##### Luồng ngoại lệ 7d: Chi phí không hợp lệ hoặc thiếu danh mục phí

1. Tại bước 7 của luồng chính, hệ thống phát hiện một trong ba trường hợp sau. Thứ nhất, chi phí nhỏ hơn 0. Thứ hai, chi phí lớn hơn 0 nhưng Người dùng chưa chọn danh mục phí. Thứ ba, danh mục phí đã xóa hoặc không thuộc loại `EXPENSE`.
2. Hệ thống từ chối lưu: nếu chi phí nhỏ hơn 0, Hệ thống từ chối vì chi phí không hợp lệ (`ERR-FEE-001`). Nếu chi phí lớn hơn 0 nhưng thiếu danh mục phí, Hệ thống từ chối vì chưa chọn danh mục phí (`ERR-FEE-002`). Nếu danh mục phí đã xóa hoặc không thuộc loại chi tiêu, Hệ thống từ chối vì danh mục phí không tương thích với loại chi tiêu (`ERR-FEE-003`).
3. Luồng quay lại bước 5 của luồng chính để Người dùng nhập lại chi phí hoặc chọn lại danh mục phí.

##### Luồng ngoại lệ 7e: Ví không ở trạng thái hoạt động

1. Tại bước 7 của luồng chính, hệ thống phát hiện ví được chọn không tồn tại hoặc đang ở trạng thái lưu trữ (`ARCHIVED`).
2. Hệ thống từ chối ghi nhận giao dịch và hiển thị thông báo lỗi: "Ví được chọn không ở trạng thái hoạt động hoặc không tồn tại."
3. Luồng quay lại bước 5 của luồng chính để Người dùng chọn ví khác đang hoạt động.

##### Luồng ngoại lệ 7f: Chưa có danh mục chi tiêu để chọn cho phí giao dịch

1. Tại bước 7 của luồng chính, hệ thống phát hiện chi phí lớn hơn 0 nhưng loại `EXPENSE` chưa có danh mục chi tiết đang dùng nào để chọn làm danh mục phí. Luồng này được kiểm tra trước luồng ngoại lệ 7d.
2. Hệ thống từ chối và hiển thị thông báo: "Chưa có danh mục chi tiêu nào để chọn cho phí giao dịch. Hãy đặt chi phí bằng 0 hoặc thêm danh mục chi tiêu trước."
3. Hệ thống đề nghị mở màn hình Quản lý danh mục thu chi để Người dùng bổ sung danh mục theo `UC-CAT-01`. Nếu Người dùng đồng ý, hệ thống mở màn hình đó và giao dịch đang nhập không được lưu.
4. Nếu Người dùng từ chối, luồng quay lại bước 5 của luồng chính để Người dùng đặt chi phí bằng 0.

#### Quy tắc nghiệp vụ đặc thù

1. Số dư ví luôn khớp theo `BR-FIN-02`: Mọi giao dịch thu chi đều phản ánh trực tiếp vào số dư ví tương ứng. Việc phản ánh diễn ra cùng lúc với việc ghi giao dịch và phí giao dịch kèm theo (nếu có). Nếu một trong hai việc thất bại thì cả hai đều không có hiệu lực.
2. Quy tắc phân cấp danh mục `BR-FIN-04`: Mọi giao dịch phải được phân loại theo cấu trúc 3 cấp và giữ cho Loại thu chi luôn nhất quán.
3. Độ chính xác tài chính `BR-FIN-01`: Xử lý toàn bộ phép toán bằng kiểu số thập phân chính xác với 2 chữ số thập phân, không làm tròn nhị phân.
4. Quy tắc phí giao dịch `BR-FIN-02`: Phí giao dịch là Giao dịch thu chi loại `EXPENSE` liên kết với giao dịch phát sinh ra nó. Hai bản ghi cùng có hiệu lực hoặc cùng không có hiệu lực.
5. Yêu cầu chức năng liên quan: `FR-TXN-01`, `FR-TXN-02`.

### 6.6 UC-TXN-02: Tra cứu và lọc lịch sử giao dịch thu chi

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần tra cứu và đối chiếu các khoản thu chi đã thực hiện theo nhiều tiêu chí kết hợp để kiểm soát dòng tiền. |
| Mô tả tóm tắt | Tìm kiếm, lọc và phân trang lịch sử giao dịch thu chi đa tiêu chí (khoảng thời gian, danh sách ví, loại giao dịch, danh mục phân loại, từ khóa ghi chú). Hệ thống tự động tính tổng thu, tổng chi và dòng tiền thuần trên tập kết quả lọc. Phí giao dịch hiển thị thành dòng riêng kèm nhãn liên kết và được tính vào tổng chi. |
| Điều kiện kích hoạt | Người dùng mở màn hình lịch sử giao dịch thu chi hoặc áp dụng bộ lọc tra cứu. |

#### Tiền điều kiện

1. Hệ thống PFMS đã đọc tệp dữ liệu cục bộ thành công.
2. Người dùng đang ở màn hình Quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Truy vấn ở chế độ chỉ đọc, không làm biến đổi bất kỳ bản ghi giao dịch hay số dư ví nào trong hệ thống.

#### Hậu điều kiện thành công

1. Danh sách giao dịch hiển thị khớp chính xác với tập kết quả lọc theo điều kiện logic kết hợp VÀ.
2. Khối tổng hợp tài chính (tổng thu nhập, tổng chi tiêu, dòng tiền thuần) phản ánh đúng dữ liệu của tập kết quả lọc theo quy tắc `BR-FIN-01`.

#### Luồng sự kiện chính

1. Người dùng mở màn hình "Lịch sử giao dịch thu chi" và chọn các tiêu chí lọc:
   - Khoảng thời gian: Từ ngày đến ngày.
   - Phạm vi ví: Một hoặc nhiều ví cụ thể, hoặc toàn bộ ví.
   - Loại giao dịch: `ALL`, `INCOME`, hoặc `EXPENSE`.
   - Phân loại danh mục: Nhóm nhu cầu hoặc Danh mục chi tiết. Danh sách lọc gồm cả nhóm và danh mục đã xóa, kèm nhãn (đã xóa), để tra cứu giao dịch cũ.
   - Từ khóa tìm kiếm: Chuỗi ký tự tra cứu trong trường ghi chú mô tả.
2. Người dùng nhấn nút "Áp dụng bộ lọc".
3. Hệ thống áp dụng logic kết hợp AND trên toàn bộ các tiêu chí đã chọn để lọc sổ giao dịch thu chi. Hệ thống sắp xếp kết quả theo ngày giao dịch giảm dần (mới nhất lên đầu) và phân trang kết quả (mặc định 50 bản ghi mỗi trang).
4. Hệ thống tính tổng hợp trên toàn bộ tập kết quả lọc:
   - Tổng thu nhập: cộng số tiền của tất cả giao dịch thu nhập trong kết quả lọc.
   - Tổng chi tiêu: cộng số tiền của các giao dịch chi tiêu trong kết quả lọc.
   - Dòng tiền thuần: tổng thu nhập trừ đi tổng chi tiêu.
   - Các phép tính thực hiện bằng kiểu dữ liệu tài chính chính xác với 2 chữ số thập phân theo `BR-FIN-01`.
5. Hệ thống hiển thị bảng danh sách giao dịch, thông tin phân trang và khối thẻ tổng hợp tài chính ở đầu danh sách. Tên danh mục của mỗi giao dịch kèm nhãn (đã xóa) nếu danh mục đã xóa. Phí giao dịch hiển thị thành dòng riêng kèm nhãn "phí của giao dịch [mã giao dịch phát sinh ra nó]".

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Khoảng ngày không hợp lệ

1. Tại bước 2 của luồng chính, hệ thống kiểm tra các tiêu chí lọc và phát hiện ngày bắt đầu lớn hơn ngày kết thúc.
2. Hệ thống từ chối lọc và hiển thị thông báo lỗi: "Khoảng thời gian tra cứu không hợp lệ (ngày bắt đầu phải trước hoặc bằng ngày kết thúc)."
3. Luồng quay lại bước 1 của luồng chính để Người dùng điều chỉnh lại khoảng thời gian hợp lệ.

##### Luồng ngoại lệ 3a: Không tìm thấy bản ghi thỏa mãn điều kiện lọc

1. Tại bước 3 của luồng chính, hệ thống không tìm thấy bất kỳ giao dịch nào thỏa mãn đồng thời các tiêu chí lọc.
2. Hệ thống hiển thị bảng dữ liệu rỗng, gán tổng thu nhập, tổng chi tiêu và dòng tiền thuần bằng 0.00. Hệ thống hiển thị thông báo trạng thái: "Không có giao dịch nào thỏa mãn bộ lọc đã chọn."

#### Quy tắc nghiệp vụ đặc thù

1. Độ chính xác tài chính `BR-FIN-01`: Toàn bộ phép tổng hợp dòng tiền trên tập kết quả lọc thực hiện bằng kiểu dữ liệu tài chính chính xác.
2. Phí giao dịch trong kết quả: Mỗi phí giao dịch là Giao dịch thu chi loại `EXPENSE` nên được lọc, tính vào tổng chi tiêu và phân trang như mọi giao dịch thu chi.
3. Yêu cầu chức năng liên quan: `FR-TXN-03`.

### 6.7 UC-TXN-03: Chỉnh sửa giao dịch thu chi

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần sửa thông tin giao dịch đã lưu (số tiền, chi phí, ví, danh mục, ghi chú) mà không làm sai lệch số dư tiền hệ thống. Hệ thống: Việc sửa chỉ có hiệu lực khi cả việc hoàn nguyên giao dịch cũ và áp dụng dữ liệu mới đều thành công, bảo đảm quy tắc `BR-FIN-02` luôn đúng. |
| Mô tả tóm tắt | Hiệu chỉnh các thông tin của một bản ghi giao dịch thu chi hiện có. Hệ thống hoàn nguyên số dư của ví cũ và áp dụng số dư mới cho ví mới trong cùng một lần xử lý cùng với phí giao dịch liên kết (nếu có). Hệ thống chỉ ghi nhận khi mọi bước thành công và ngăn chặn việc làm âm số dư ví. |
| Điều kiện kích hoạt | Người dùng chọn chức năng chỉnh sửa tại một dòng giao dịch trong sổ giao dịch thu chi. |

#### Tiền điều kiện

1. Giao dịch cần chỉnh sửa đã tồn tại trong sổ giao dịch thu chi.
2. Người dùng đang ở màn hình Quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu thao tác chỉnh sửa thất bại tại bất kỳ bước kiểm tra nào, trạng thái của giao dịch cũ và số dư của các ví liên quan được giữ nguyên vẹn. Toàn bộ thao tác được hoàn nguyên.

#### Hậu điều kiện thành công

1. Bản ghi giao dịch và phí giao dịch liên kết (nếu có) được cập nhật thông tin mới trong sổ giao dịch thu chi.
2. Số dư của ví cũ và ví mới được đồng bộ hóa chính xác theo quy tắc `BR-FIN-02`.
3. Số dư của các ví liên quan không bị âm sau khi hoàn tất chỉnh sửa.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng chỉnh sửa đối với một giao dịch cụ thể từ lịch sử giao dịch thu chi.
2. Hệ thống tải dữ liệu hiện tại của giao dịch lên biểu mẫu chỉnh sửa. Dữ liệu gồm: Ngày giao dịch, Loại giao dịch, Ví thanh toán, Danh mục chi tiết, Số tiền, Chi phí, Danh mục phí, Ghi chú.
3. Người dùng thay đổi các trường cần hiệu chỉnh (số tiền mới, chi phí mới, ví thanh toán mới, danh mục mới, hoặc danh mục phí) và nhấn nút "Lưu thay đổi".
4. Hệ thống kiểm tra tính hợp lệ về mặt số học: số tiền mới lớn hơn 0, chi phí mới không âm, danh mục sau khi sửa tương thích với loại giao dịch theo `BR-FIN-04`. Nếu Người dùng đổi danh mục thì danh mục mới phải đang dùng. Hệ thống vẫn chấp nhận việc giữ nguyên danh mục cũ, kể cả khi danh mục đó đã xóa. Nếu chi phí mới lớn hơn 0 thì phải có danh mục phí mới là danh mục chi tiêu đang dùng. Giữ nguyên danh mục phí cũ vẫn được chấp nhận kể cả khi đã xóa.
5. Hệ thống tính thử việc hoàn nguyên ví cũ theo `BR-FIN-02`:
   - Nếu giao dịch cũ là `INCOME`: Ví cũ cần trừ đi số tiền cũ và được cộng lại chi phí cũ (nếu có). Kiểm tra số dư ví cũ sau khi hoàn nguyên không bị âm.
   - Nếu giao dịch cũ là `EXPENSE`: Ví cũ được hoàn trả lại số tiền cũ và chi phí cũ (nếu có).
6. Hệ thống tính thử việc áp dụng giao dịch mới vào ví mới theo `BR-FIN-02`:
   - Nếu giao dịch mới là `EXPENSE`: Kiểm tra số dư ví mới sau khi hoàn nguyên ví cũ, bảo đảm số dư này không âm sau khi trừ số tiền mới và chi phí mới.
   - Nếu giao dịch mới là `INCOME`: Kiểm tra số dư ví mới sau khi hoàn nguyên ví cũ, cộng số tiền mới và trừ chi phí mới không âm.
7. Hệ thống thực hiện các thao tác sau; chỉ khi tất cả thành công, kết quả mới được ghi nhận:
   - Hoàn nguyên số dư ví cũ.
   - Cập nhật số dư ví mới.
   - Ghi đè các trường thông tin mới vào bản ghi giao dịch trong sổ giao dịch thu chi.
   - Phí giao dịch liên kết được tạo, cập nhật hoặc xóa theo chi phí mới. Phí bị xóa nếu chi phí mới bằng 0 và giao dịch đang có phí giao dịch. Phí được tạo mới nếu chi phí mới lớn hơn 0 và giao dịch chưa có phí giao dịch. Phí được cập nhật nếu cả hai lớn hơn 0.
8. Hệ thống lưu trạng thái xuống tệp dữ liệu, hiển thị thông báo cập nhật thành công và làm mới lịch sử giao dịch thu chi.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 1a: Chỉnh sửa trực tiếp phí giao dịch

1. Tại bước 1 của luồng chính, Người dùng chọn chức năng chỉnh sửa tại một dòng phí giao dịch.
2. Hệ thống từ chối và hiển thị thông báo lỗi: "Phí giao dịch không được sửa trực tiếp. Vui lòng sửa giao dịch thu chi phát sinh ra phí này, hoặc xóa giao dịch chuyển tiền rồi tạo lại nếu phí thuộc giao dịch chuyển tiền."
3. Luồng kết thúc, trạng thái dữ liệu được bảo toàn nguyên trạng.

##### Luồng ngoại lệ 1b: Không tìm thấy giao dịch cần chỉnh sửa

1. Tại bước 1 của luồng chính, hệ thống tìm kiếm bản ghi theo mã giao dịch nhưng không tìm thấy trong sổ giao dịch thu chi.
2. Hệ thống hiển thị thông báo lỗi: "Không tìm thấy giao dịch yêu cầu."
3. Luồng kết thúc, trạng thái dữ liệu được bảo toàn nguyên trạng.

##### Luồng ngoại lệ 4a: Số tiền mới không hợp lệ

1. Tại bước 4 của luồng chính, hệ thống phát hiện giá trị nhập vào không đúng định dạng số học hoặc số tiền mới nhỏ hơn hoặc bằng 0.
2. Hệ thống từ chối lưu. Nếu dữ liệu sai định dạng số học, Hệ thống từ chối vì dữ liệu số tiền không đúng định dạng số học (`ERR-FIN-001`). Nếu số tiền mới nhỏ hơn hoặc bằng 0, Hệ thống từ chối vì số tiền giao dịch phải là số dương lớn hơn 0.
3. Luồng quay lại bước 3 của luồng chính để Người dùng nhập lại số tiền hợp lệ.

##### Luồng ngoại lệ 4b: Chi phí không hợp lệ hoặc thiếu danh mục phí

1. Tại bước 4 của luồng chính, hệ thống phát hiện một trong ba trường hợp sau. Thứ nhất, chi phí mới nhỏ hơn 0. Thứ hai, chi phí mới lớn hơn 0 nhưng giao dịch chưa có phí giao dịch và Người dùng chưa chọn danh mục phí. Thứ ba, danh mục phí mới đã xóa hoặc không thuộc loại `EXPENSE`.
2. Hệ thống từ chối lưu: nếu chi phí mới nhỏ hơn 0, Hệ thống từ chối vì chi phí không hợp lệ (`ERR-FEE-001`). Nếu chi phí mới lớn hơn 0 nhưng thiếu danh mục phí, Hệ thống từ chối vì chưa chọn danh mục phí (`ERR-FEE-002`). Nếu danh mục phí đã xóa hoặc không thuộc loại chi tiêu, Hệ thống từ chối vì danh mục phí không tương thích với loại chi tiêu (`ERR-FEE-003`).
3. Luồng quay lại bước 3 của luồng chính.

##### Luồng ngoại lệ 4c: Ví mới không ở trạng thái hoạt động

1. Tại bước 4 của luồng chính, hệ thống phát hiện ví mới được chọn không tồn tại hoặc đang ở trạng thái lưu trữ (`ARCHIVED`).
2. Hệ thống từ chối lưu và hiển thị thông báo lỗi: "Ví được chọn không ở trạng thái hoạt động hoặc không tồn tại."
3. Luồng quay lại bước 3 của luồng chính để Người dùng chọn ví khác đang hoạt động.

##### Luồng ngoại lệ 4d: Loại danh mục không khớp với loại giao dịch gốc

1. Tại bước 4 của luồng chính, Người dùng thay đổi danh mục sang một danh mục có Loại thu chi không khớp với Loại thu chi của giao dịch gốc.
2. Hệ thống từ chối lưu và hiển thị thông báo lỗi: "Loại danh mục không khớp với loại giao dịch."
3. Luồng quay lại bước 3 của luồng chính để Người dùng chọn lại danh mục hợp lệ cùng loại.

##### Luồng ngoại lệ 4e: Chưa có danh mục chi tiêu để chọn cho phí giao dịch

1. Tại bước 4 của luồng chính, hệ thống phát hiện chi phí mới lớn hơn 0 và giao dịch chưa có phí giao dịch, nhưng loại `EXPENSE` chưa có danh mục chi tiết đang dùng nào để chọn làm danh mục phí. Luồng này được kiểm tra trước luồng ngoại lệ 4b.
2. Hệ thống từ chối và hiển thị thông báo: "Chưa có danh mục chi tiêu nào để chọn cho phí giao dịch. Hãy đặt chi phí bằng 0 hoặc thêm danh mục chi tiêu trước."
3. Hệ thống đề nghị mở màn hình Quản lý danh mục thu chi để Người dùng bổ sung danh mục theo `UC-CAT-01`. Nếu Người dùng đồng ý, hệ thống mở màn hình đó và giao dịch đang nhập không được lưu.
4. Nếu Người dùng từ chối, luồng quay lại bước 3 của luồng chính để Người dùng đặt chi phí bằng 0.

##### Luồng ngoại lệ 5a: Hoàn nguyên giao dịch thu nhập làm âm số dư ví cũ

1. Tại bước 5 của luồng chính, hệ thống thấy việc trừ lại số tiền cũ và cộng lại chi phí cũ (nếu có) sẽ khiến số dư ví cũ bị âm.
2. Hệ thống từ chối chỉnh sửa vì số dư ví cũ không đủ (`ERR-WAL-001`). Việc hoàn nguyên khoản thu sẽ làm số dư ví cũ bị âm. Người dùng cần kiểm tra lại các khoản chi tiêu liên quan của ví cũ trước khi chỉnh sửa.
3. Luồng quay lại bước 3 của luồng chính hoặc Người dùng hủy bỏ thao tác.

##### Luồng ngoại lệ 6a: Số dư ví mới không đủ chi trả cho giao dịch chi tiêu mới

1. Tại bước 6 của luồng chính, hệ thống phát hiện số dư khả dụng của ví mới không đủ để thực hiện khoản chi mới có số tiền mới cùng chi phí mới. Số dư này tính sau khi hoàn nguyên ví cũ.
2. Hệ thống từ chối lưu vì số dư ví mới không đủ (`ERR-WAL-001`). Số dư khả dụng của ví mới không đủ để thực hiện khoản chi tiêu mới. Người dùng cần chọn ví khác hoặc giảm số tiền chi tiêu.
3. Luồng quay lại bước 3 của luồng chính để Người dùng chọn ví khác hoặc giảm số tiền.

##### Luồng ngoại lệ 6b: Số dư ví mới không đủ khi chi phí lớn hơn số tiền thu nhập mới

1. Tại bước 6 của luồng chính, giao dịch mới là thu nhập. Hệ thống phát hiện số dư ví mới sau khi hoàn nguyên ví cũ, cộng số tiền mới và trừ chi phí mới nhỏ hơn 0.
2. Hệ thống từ chối lưu vì số dư ví mới không đủ (`ERR-WAL-001`). Số dư khả dụng của ví mới không đủ chi trả chi phí giao dịch. Người dùng cần chọn ví khác hoặc giảm chi phí.
3. Luồng quay lại bước 3 của luồng chính để Người dùng chọn ví khác hoặc giảm chi phí.

#### Quy tắc nghiệp vụ đặc thù

1. Dữ liệu không sai lệch khi chỉnh sửa: Việc hoàn nguyên ví cũ, ghi nhận ví mới và cập nhật phí giao dịch liên kết phải hoàn tất cùng lúc. Nếu xảy ra lỗi lưu trữ, toàn bộ trạng thái phải tự động khôi phục về trước khi chỉnh sửa.
2. Số dư ví không âm theo `BR-FIN-02`: Số dư của các ví không được phép âm tại bất kỳ thời điểm nào của quá trình chuyển đổi.
3. Quy tắc phí giao dịch `BR-FIN-02`: Phí giao dịch chỉ được sửa cùng giao dịch phát sinh ra nó, không được sửa trực tiếp.
4. Yêu cầu chức năng liên quan: `FR-TXN-04`.

### 6.8 UC-TXN-04: Xóa giao dịch thu chi

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần loại bỏ một giao dịch ghi nhận sai hoặc bị hủy bỏ trong thực tế. Hệ thống: Hoàn nguyên chính xác số dư ví tương ứng (gồm cả chi phí nếu giao dịch có phí giao dịch liên kết). Hệ thống ngăn chặn tình trạng ví bị âm do xóa khoản thu nhập đã bị chi tiêu trong các giao dịch kế tiếp. |
| Mô tả tóm tắt | Xóa bỏ hoàn toàn một bản ghi giao dịch thu chi, cùng phí giao dịch liên kết nếu có, khỏi hệ thống. Hệ thống tự động thực hiện hoàn nguyên số dư ví liên quan và cập nhật lại các chỉ số thống kê dòng tiền. |
| Điều kiện kích hoạt | Người dùng chọn chức năng xóa tại một dòng giao dịch trong sổ giao dịch thu chi. |

#### Tiền điều kiện

1. Bản ghi giao dịch cần xóa tồn tại trong sổ giao dịch thu chi.
2. Người dùng đang ở màn hình Quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu thao tác xóa bị từ chối do vi phạm điều kiện số dư ví hoặc Người dùng hủy xác nhận, bản ghi giao dịch được giữ nguyên vẹn. Phí giao dịch liên kết và số dư ví cũng được giữ nguyên vẹn.

#### Hậu điều kiện thành công

1. Bản ghi giao dịch và phí giao dịch liên kết (nếu có) bị xóa hoàn toàn khỏi sổ giao dịch thu chi.
2. Số dư của ví liên quan được hoàn nguyên chính xác theo quy tắc `BR-FIN-02`. Nếu xóa chi tiêu thì cộng hoàn trả số tiền và chi phí. Nếu xóa thu nhập thì trừ lại số tiền và cộng hoàn trả chi phí.
3. Báo cáo thống kê tháng và số dư ví được cập nhật ngay.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng xóa đối với một giao dịch cụ thể từ lịch sử giao dịch thu chi.
2. Hệ thống hiển thị hộp thoại xác nhận xóa giao dịch. Hộp thoại nêu rõ các thông tin: Ngày giao dịch, Danh mục, Số tiền, Chi phí (nếu có), Ví thanh toán và tác động hoàn nguyên số dư dự kiến.
3. Người dùng nhấn nút "Xác nhận xóa".
4. Hệ thống kiểm tra điều kiện an toàn số dư theo `BR-FIN-02`:
   - Nếu giao dịch là `EXPENSE`: Số dư ví sẽ được cộng hoàn trả lại tổng số tiền giao dịch và chi phí (không có nguy cơ làm âm ví).
   - Nếu giao dịch là `INCOME`: Hệ thống kiểm tra số dư khả dụng hiện tại của ví, bảo đảm không âm sau khi trừ số tiền giao dịch và cộng lại chi phí.
5. Hệ thống thực hiện các thao tác sau; chỉ khi tất cả thành công, kết quả mới được ghi nhận:
   - Hoàn nguyên số dư ví: nếu xóa `INCOME` thì trừ số tiền giao dịch và cộng lại chi phí. Nếu xóa `EXPENSE` thì cộng tổng số tiền giao dịch và chi phí (chi phí bằng 0 khi giao dịch không có phí giao dịch).
   - Xóa bản ghi giao dịch và phí giao dịch liên kết (nếu có) khỏi sổ giao dịch thu chi.
6. Hệ thống cập nhật các chỉ số tổng hợp tài chính của tháng quan sát.
7. Hệ thống lưu tệp dữ liệu, hiển thị thông báo xóa giao dịch thành công và làm mới lịch sử giao dịch thu chi trên màn hình.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 1a: Xóa trực tiếp phí giao dịch

1. Tại bước 1 của luồng chính, Người dùng chọn chức năng xóa tại một dòng phí giao dịch.
2. Hệ thống từ chối và hiển thị thông báo lỗi: "Phí giao dịch không được xóa trực tiếp. Vui lòng xóa giao dịch thu chi phát sinh ra phí này, hoặc xóa giao dịch chuyển tiền nếu phí thuộc giao dịch chuyển tiền."
3. Luồng kết thúc, trạng thái dữ liệu được bảo toàn nguyên trạng.

##### Luồng ngoại lệ 1b: Không tìm thấy giao dịch cần xóa

1. Tại bước 1 của luồng chính, hệ thống tìm kiếm bản ghi theo mã giao dịch nhưng không tìm thấy trong sổ giao dịch thu chi.
2. Hệ thống hiển thị thông báo lỗi: "Không tìm thấy giao dịch yêu cầu."
3. Luồng kết thúc, trạng thái dữ liệu được bảo toàn nguyên trạng.

##### Luồng ngoại lệ 3a: Người dùng hủy bỏ thao tác xóa

1. Tại bước 3 của luồng chính, Người dùng chọn nút "Hủy" hoặc đóng hộp thoại xác nhận xóa giao dịch.
2. Hệ thống hủy bỏ thao tác xóa, đóng hộp thoại và giữ nguyên vẹn dữ liệu giao dịch cùng số dư các ví.
3. Luồng kết thúc, Người dùng quay lại màn hình Quản lý giao dịch thu chi.

##### Luồng ngoại lệ 4a: Không thể xóa giao dịch thu nhập do làm âm số dư ví

1. Tại bước 4 của luồng chính, hệ thống phát hiện giao dịch cần xóa là khoản thu nhập (`INCOME`). Số dư khả dụng hiện tại của ví sau khi cộng lại chi phí nhỏ hơn số tiền của giao dịch. Nguyên nhân là số tiền này đã được sử dụng trong các giao dịch chi tiêu hoặc chuyển tiền tiếp theo.
2. Hệ thống hủy bỏ thao tác xóa vì số dư ví không đủ (`ERR-WAL-001`). Việc xóa giao dịch thu nhập sẽ làm số dư ví bị âm. Người dùng cần kiểm tra lại các khoản chi tiêu liên quan của ví trước khi thực hiện.
3. Luồng kết thúc, trạng thái dữ liệu được bảo toàn nguyên trạng.

#### Quy tắc nghiệp vụ đặc thù

1. Dữ liệu không sai lệch khi xóa: Thao tác xóa bản ghi (kể cả phí giao dịch liên kết) và hoàn nguyên số dư ví phải diễn ra đồng thời. Nếu xảy ra lỗi lưu trữ, hệ thống tự động khôi phục số dư ví và bản ghi giao dịch về trạng thái trước khi xóa.
2. Số dư ví không âm theo `BR-FIN-02`: Không cho phép xóa giao dịch thu nhập nếu hệ quả làm số dư khả dụng của ví thành số âm.
3. Quy tắc phí giao dịch `BR-FIN-02`: Phí giao dịch bị xóa cùng giao dịch phát sinh ra nó và không được xóa trực tiếp.
4. Yêu cầu chức năng liên quan: `FR-TXN-05`.

### 6.9 UC-TRF-01: Chuyển tiền nội bộ giữa hai ví

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần chuyển tiền giữa các tài khoản nội bộ (rút tiền mặt, ví điện tử) và ghi nhận đúng phí chuyển tiền. Hệ thống: Áp dụng cơ chế ghi nhận đồng thời (trừ ví nguồn, cộng ví đích). Tổng số dư các ví giữ nguyên nếu không có phí, hoặc chỉ giảm đúng bằng phí theo `BR-FIN-03`. |
| Mô tả tóm tắt | Điều chuyển giá trị tiền tệ giữa hai ví nội bộ. Áp dụng cơ chế ghi nhận đồng thời: tự động trừ số tiền chuyển khỏi ví nguồn và cộng vào ví đích. Chi phí chuyển tiền (nếu có) được tách thành một phí giao dịch độc lập liên kết. |
| Điều kiện kích hoạt | Người dùng chọn chức năng chuyển tiền nội bộ trên giao diện. |

#### Tiền điều kiện

1. Hệ thống có ít nhất hai ví đang ở trạng thái `ACTIVE`.
2. Số dư khả dụng của ví nguồn được kiểm tra ở bước 8 theo điều kiện lớn hơn hoặc bằng tổng số tiền chuyển cộng phí chuyển tiền.
3. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Nếu giao dịch chuyển tiền bị lỗi ở bất kỳ bước nào (ví dụ trừ ví nguồn thành công nhưng cộng ví đích thất bại), toàn bộ giao dịch bị hủy bỏ ngay lập tức. Số dư hai ví giữ nguyên vẹn.

#### Hậu điều kiện thành công

1. Giao dịch chuyển tiền mới được lưu vào sổ giao dịch chuyển tiền với mã duy nhất.
2. Hai khoản ghi được lưu cùng lúc: ví nguồn giảm chính xác tổng số tiền chuyển và phí chuyển tiền, ví đích tăng chính xác số tiền chuyển.
3. Nếu phí chuyển tiền lớn hơn 0, một phí giao dịch độc lập được tự động ghi nhận vào sổ giao dịch thu chi liên kết với mã giao dịch chuyển tiền.
4. Tổng số dư trong các ví toàn hệ thống giảm đúng bằng phí chuyển tiền; nếu không có phí thì tổng này giữ nguyên theo `BR-FIN-03`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Chuyển tiền nội bộ".
2. Hệ thống hiển thị biểu mẫu chuyển tiền gồm: Ngày chuyển tiền (mặc định là ngày hiện tại), Ví nguồn (bị trừ tiền), Ví đích (nhận tiền). Biểu mẫu còn có Số tiền chuyển, Phí chuyển tiền (mặc định bằng 0), Danh mục phí, Ghi chú mô tả. Danh mục phí bắt buộc chọn khi phí lớn hơn 0 và không có mặc định.
3. Người dùng chọn Ví nguồn từ danh sách ví `ACTIVE`. Hệ thống hiển thị số dư khả dụng hiện tại của ví nguồn để Người dùng theo dõi.
4. Người dùng chọn Ví đích từ danh sách ví `ACTIVE`.
5. Người dùng nhập số tiền cần chuyển (lớn hơn 0).
6. Người dùng nhập phí chuyển tiền (không âm) nếu giao dịch thực tế có phát sinh phí. Nếu phí chuyển tiền lớn hơn 0, Người dùng chọn một danh mục chi tiêu đang dùng cho phí giao dịch.
7. Người dùng nhập ghi chú mô tả (nếu có) và nhấn nút "Thực hiện chuyển tiền".
8. Hệ thống kiểm tra nghiệp vụ:
   - Kiểm tra ví nguồn phải khác ví đích.
   - Kiểm tra số tiền chuyển lớn hơn 0 và phí chuyển tiền không âm, đưa về kiểu dữ liệu số thập phân chính xác với 2 chữ số thập phân theo quy tắc `BR-FIN-01`.
   - Kiểm tra khả năng thanh toán của ví nguồn theo `BR-FIN-02`: số dư ví nguồn không nhỏ hơn tổng số tiền chuyển và phí chuyển tiền.
   - Nếu phí chuyển tiền lớn hơn 0, kiểm tra đã chọn danh mục phí, danh mục đó đang dùng và thuộc loại `EXPENSE`.
9. Hệ thống ghi nhận đồng thời hai đầu giao dịch theo quy tắc `BR-FIN-03`:
   - Trừ số tiền chuyển khỏi số dư ví nguồn.
   - Cộng số tiền chuyển vào số dư ví đích.
   - Lưu giao dịch chuyển tiền vào sổ giao dịch chuyển tiền với mã giao dịch chuyển tiền duy nhất.
10. Nếu phí chuyển tiền lớn hơn 0, hệ thống tự động sinh một phí giao dịch (loại `EXPENSE`) độc lập trong sổ giao dịch thu chi với số tiền bằng phí chuyển tiền. Khoản phí này được trừ tiếp khỏi ví nguồn. Bản ghi này mang danh mục phí Người dùng đã chọn và lưu mã liên kết tới giao dịch chuyển tiền tương ứng.
11. Hệ thống lưu tệp dữ liệu cục bộ và hiển thị thông báo chuyển tiền thành công. Thông báo kèm thông tin chi tiết: số tiền chuyển, phí đã trừ, số dư mới của ví nguồn và ví đích.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 6a: Chuyển tiền không mất phí (phí chuyển tiền bằng 0)

1. Tại bước 6 của luồng chính, Người dùng giữ nguyên mức phí bằng 0 (hoặc để trống).
2. Hệ thống chỉ ghi hai khoản: trừ số tiền chuyển tại ví nguồn và cộng số tiền chuyển tại ví đích.
3. Hệ thống không tạo phí giao dịch liên kết; tổng số dư toàn bộ các ví trước và sau giao dịch được giữ nguyên hoàn toàn.
4. Luồng tiếp tục tại bước 11 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 8a: Ví nguồn trùng ví đích

1. Tại bước 8 của luồng chính, hệ thống phát hiện ví nguồn trùng ví đích.
2. Hệ thống chặn giao dịch và đánh dấu trường ví đích vì ví nguồn trùng ví đích (`ERR-TRF-001`). Người dùng cần chọn hai ví khác nhau.
3. Luồng quay lại bước 4 của luồng chính để Người dùng chọn lại ví đích.

##### Luồng ngoại lệ 8b: Số tiền chuyển hoặc chi phí không hợp lệ

1. Tại bước 8 của luồng chính, hệ thống phát hiện giá trị nhập vào không đúng định dạng số học, số tiền chuyển nhỏ hơn hoặc bằng 0, hoặc phí chuyển tiền nhỏ hơn 0.
2. Hệ thống từ chối lưu: nếu dữ liệu sai định dạng số học, Hệ thống từ chối vì dữ liệu số tiền không đúng định dạng số học (`ERR-FIN-001`). Nếu số tiền chuyển nhỏ hơn hoặc bằng 0, Hệ thống từ chối vì số tiền chuyển phải là số dương lớn hơn 0 (`ERR-TRF-002`). Nếu phí chuyển tiền nhỏ hơn 0, Hệ thống từ chối vì chi phí không hợp lệ (`ERR-FEE-001`).
3. Luồng quay lại bước 5 của luồng chính để Người dùng nhập lại số tiền hoặc chi phí hợp lệ.

##### Luồng ngoại lệ 8c: Số dư ví nguồn không đủ thanh toán

1. Tại bước 8 của luồng chính, hệ thống tính tổng số tiền phải trừ tại ví nguồn (gồm số tiền chuyển và phí chuyển tiền). Tổng này vượt quá số dư khả dụng hiện tại.
2. Hệ thống từ chối thực hiện vì số dư ví nguồn không đủ (`ERR-WAL-001`). Số dư khả dụng của ví nguồn không đủ để thực hiện chuyển tiền. Người dùng cần giảm số tiền chuyển hoặc chọn ví nguồn khác.
3. Luồng quay lại bước 5 của luồng chính để Người dùng giảm số tiền hoặc chọn ví nguồn khác.

##### Luồng ngoại lệ 8d: Thiếu hoặc sai danh mục phí

1. Tại bước 8 của luồng chính, hệ thống phát hiện phí chuyển tiền lớn hơn 0 nhưng Người dùng chưa chọn danh mục phí. Một tình huống khác là danh mục đã chọn là danh mục đã xóa hoặc không thuộc loại `EXPENSE`.
2. Hệ thống từ chối thực hiện: nếu thiếu danh mục phí, Hệ thống từ chối vì chưa chọn danh mục phí (`ERR-FEE-002`). Nếu danh mục phí đã xóa hoặc không thuộc loại chi tiêu, Hệ thống từ chối vì danh mục phí không tương thích với loại chi tiêu (`ERR-FEE-003`).
3. Luồng quay lại bước 6 của luồng chính để Người dùng chọn lại danh mục phí.

##### Luồng ngoại lệ 8e: Chưa có danh mục chi tiêu để chọn cho phí giao dịch

1. Tại bước 8 của luồng chính, hệ thống phát hiện phí chuyển tiền lớn hơn 0 nhưng loại `EXPENSE` chưa có danh mục chi tiết đang dùng nào để chọn làm danh mục phí. Luồng này được kiểm tra trước luồng ngoại lệ 8d.
2. Hệ thống từ chối và hiển thị thông báo: "Chưa có danh mục chi tiêu nào để chọn cho phí giao dịch. Hãy đặt chi phí bằng 0 hoặc thêm danh mục chi tiêu trước."
3. Hệ thống đề nghị mở màn hình Quản lý danh mục thu chi để Người dùng bổ sung danh mục theo `UC-CAT-01`. Nếu Người dùng đồng ý, hệ thống mở màn hình đó và giao dịch đang nhập không được lưu.
4. Nếu Người dùng từ chối, luồng quay lại bước 6 của luồng chính để Người dùng đặt chi phí bằng 0.

##### Luồng ngoại lệ 9a: Lỗi một đầu ví trong quá trình ghi nhận đồng thời

1. Trong quá trình chạy bước 9 hoặc 10 của luồng chính, thao tác trừ tiền ở ví nguồn thành công nhưng thao tác cộng tiền ở ví đích bị gián đoạn. Nguyên nhân là lỗi kỹ thuật hoặc lỗi ghi tệp.
2. Hệ thống hoàn nguyên toàn bộ giao dịch chuyển tiền, hủy bỏ thao tác trừ tiền ở ví nguồn, đưa hai ví về đúng trạng thái ban đầu.
3. Hệ thống ghi nhật ký lỗi chi tiết và hiển thị thông báo: "Giao dịch chuyển tiền bị gián đoạn và đã được phục hồi nguyên trạng để giữ nguyên dữ liệu số dư. Vui lòng thử lại sau."

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc ghi nhận đồng thời theo `BR-FIN-03`: Tổng số dư trên toàn bộ các ví luôn giữ nguyên đối với giao dịch chuyển tiền thuần túy.
2. Quy tắc tách riêng chi phí: Chi phí phải được ghi thành một phí giao dịch (loại `EXPENSE`) riêng biệt. Khoản này không được khấu trừ gộp làm sai lệch số tiền chuyển thực nhận của ví đích.
3. Quy tắc số học `BR-FIN-01`: Toàn bộ các bước trừ cộng số dư đều thực hiện bằng kiểu dữ liệu tài chính chính xác.
4. Yêu cầu chức năng liên quan: `FR-TRF-01`.

### 6.10 UC-TRF-02: Tra cứu lịch sử chuyển tiền

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần rà soát lại các giao dịch chuyển tiền đã thực hiện giữa các ví nội bộ để so khớp số dư. |
| Mô tả tóm tắt | Hiển thị danh sách toàn bộ các giao dịch chuyển tiền nội bộ đã thực hiện, sắp xếp theo trình tự thời gian. Danh sách thể hiện ví nguồn, ví đích, số tiền chuyển, chi phí và ghi chú. |
| Điều kiện kích hoạt | Người dùng chọn tab "Lịch sử chuyển tiền" trên màn hình Chuyển tiền nội bộ. |

#### Tiền điều kiện

1. Hệ thống PFMS đã khởi chạy và đọc tệp dữ liệu cục bộ thành công.
2. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Hệ thống chạy ở chế độ chỉ đọc, không làm thay đổi dữ liệu sổ giao dịch chuyển tiền hay số dư ví.

#### Hậu điều kiện thành công

1. Danh sách lịch sử chuyển tiền được hiển thị theo thứ tự ngày giảm dần, phản ánh toàn bộ các bản ghi trong sổ giao dịch chuyển tiền theo quy tắc `FR-TRF-02`.

#### Luồng sự kiện chính

1. Người dùng mở tab "Lịch sử chuyển tiền".
2. Hệ thống truy vấn sổ giao dịch chuyển tiền, sắp xếp các bản ghi theo ngày chuyển tiền giảm dần (mới nhất lên trước).
3. Hệ thống hiển thị bảng danh sách chuyển tiền gồm các cột thông tin: Mã giao dịch chuyển tiền, Ngày thực hiện, Ví nguồn, Ví đích. Bảng còn có các cột Số tiền chuyển, Chi phí và Ghi chú mô tả.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Chưa có giao dịch chuyển tiền nào

1. Tại bước 2 của luồng chính, hệ thống phát hiện sổ giao dịch chuyển tiền rỗng.
2. Hệ thống hiển thị bảng rỗng kèm thông báo trạng thái: "Chưa có giao dịch chuyển tiền nội bộ nào được ghi nhận."

#### Quy tắc nghiệp vụ đặc thù

1. Dữ liệu hiển thị đúng theo `BR-FIN-02`, `BR-FIN-03`. Số tiền chuyển và chi phí hiển thị phải phản ánh chính xác các khoản ghi tương ứng đã ghi nhận tại ví nguồn và ví đích. Hệ thống không hiển thị dữ liệu sai lệch với dữ liệu tiền trong hệ thống.
2. Yêu cầu chức năng liên quan: `FR-TRF-02`.

### 6.11 UC-TRF-03: Xóa giao dịch chuyển tiền

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần xóa một giao dịch chuyển tiền nhầm lẫn và trả số dư của hai ví về như cũ. Hệ thống: Áp dụng cơ chế ghi nhận đồng thời khi hoàn nguyên và chỉ cho phép xóa khi ví đích còn đủ số dư để hoàn trả. Nhờ vậy các quy tắc `BR-FIN-02` và `BR-FIN-03` luôn đúng. |
| Mô tả tóm tắt | Xóa một giao dịch chuyển tiền nội bộ đã thực hiện, tự động hoàn nguyên số dư tương ứng tại ví nguồn và ví đích. Bản ghi chuyển tiền và phí giao dịch liên kết (nếu có) bị xóa hoàn toàn khỏi sổ lưu trữ. |
| Điều kiện kích hoạt | Người dùng chọn chức năng xóa tại một dòng chuyển tiền trong bảng lịch sử. |

#### Tiền điều kiện

1. Giao dịch chuyển tiền cần xóa tồn tại trong sổ giao dịch chuyển tiền.
2. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Nếu ví đích không đủ số dư để hoàn trả, thao tác hoàn nguyên thất bại hoặc Người dùng hủy xác nhận, bản ghi giao dịch chuyển tiền, phí liên kết và số dư hai ví được giữ nguyên vẹn.

#### Hậu điều kiện thành công

1. Số dư ví nguồn và ví đích được khôi phục chính xác như trước khi chuyển: ví nguồn được cộng trả lại tổng số tiền chuyển và phí chuyển tiền. Ví đích bị trừ số tiền chuyển.
2. Phí giao dịch liên kết (nếu có) bị xóa khỏi sổ giao dịch thu chi.
3. Bản ghi giao dịch chuyển tiền bị xóa khỏi sổ giao dịch chuyển tiền.

#### Luồng sự kiện chính

1. Tại bảng lịch sử chuyển tiền, Người dùng nhấn nút "Xóa giao dịch" tại một giao dịch chuyển tiền.
2. Hệ thống hiển thị hộp thoại xác nhận xóa giao dịch chuyển tiền. Hộp thoại liệt kê chi tiết các khoản hoàn nguyên sẽ diễn ra: cộng trả lại số tiền chuyển vào ví nguồn, trừ số tiền chuyển khỏi ví đích. Hộp thoại cũng liệt kê việc hoàn phí chuyển tiền về ví nguồn (nếu có) thông qua tính lại số dư.
3. Người dùng nhấn nút "Xác nhận xóa giao dịch".
4. Hệ thống truy xuất giao dịch chuyển tiền theo mã, sau đó kiểm tra điều kiện hoàn nguyên tại ví đích theo quy tắc `FR-TRF-02`: số dư khả dụng hiện tại của ví đích phải lớn hơn hoặc bằng số tiền đã nhận.
5. Hệ thống hoàn nguyên số dư cho cả hai ví; chỉ khi cả hai thành công, kết quả mới được ghi nhận:
   - Hoàn số tiền chuyển về số dư ví nguồn nhờ xóa giao dịch chuyển tiền khỏi công thức tính lại số dư, không ghi bút toán cộng riêng.
   - Trừ số tiền chuyển khỏi số dư ví đích theo cùng cách tính lại số dư.
   - Nếu giao dịch chuyển tiền có phí liên kết, hệ thống hoàn khoản phí về ví nguồn. Cách hoàn là xóa bản ghi phí giao dịch liên kết khỏi sổ giao dịch thu chi.
   - Xóa bản ghi giao dịch chuyển tiền khỏi sổ giao dịch chuyển tiền.
6. Hệ thống lưu tệp dữ liệu, hiển thị thông báo xóa chuyển tiền thành công và làm mới số dư hai ví trên giao diện.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3a: Người dùng hủy bỏ thao tác xóa

1. Tại bước 3 của luồng chính, Người dùng chọn nút "Hủy" hoặc đóng hộp thoại xác nhận xóa giao dịch chuyển tiền.
2. Hệ thống hủy bỏ thao tác xóa, đóng hộp thoại và giữ nguyên vẹn bản ghi giao dịch chuyển tiền, phí liên kết cùng số dư hai ví.
3. Luồng kết thúc, Người dùng quay lại bảng lịch sử chuyển tiền.

##### Luồng ngoại lệ 4a: Không tìm thấy giao dịch chuyển tiền

1. Tại bước 4 của luồng chính, hệ thống truy xuất giao dịch chuyển tiền cần xóa theo quy tắc `FR-TRF-02` nhưng không tìm thấy mã đó trong sổ giao dịch chuyển tiền.
2. Hệ thống hiển thị thông báo lỗi: "Không tìm thấy giao dịch chuyển tiền yêu cầu."
3. Luồng kết thúc, trạng thái dữ liệu được bảo toàn nguyên trạng.

##### Luồng ngoại lệ 4b: Ví đích không đủ số dư để hoàn nguyên

1. Tại bước 4 của luồng chính, sau khi truy xuất được giao dịch, hệ thống phát hiện số dư khả dụng hiện tại của ví đích nhỏ hơn số tiền chuyển cần hoàn trả. Nguyên nhân là ví đích đã sử dụng tiền này trong các giao dịch chi tiêu tiếp theo.
2. Hệ thống từ chối thao tác xóa vì số dư ví đích không đủ (`ERR-WAL-001`). Ví đích không còn đủ số dư để hoàn trả số tiền chuyển. Người dùng cần nạp thêm tiền vào ví đích hoặc điều chỉnh các giao dịch chi tiêu liên quan tại ví đích trước khi xóa giao dịch chuyển tiền.
3. Luồng kết thúc, trạng thái hệ thống được bảo toàn nguyên trạng.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc ghi nhận đồng thời theo `BR-FIN-03`: Hoàn nguyên chuyển tiền phải khôi phục chính xác tại cả ví nguồn và ví đích. Nếu một ví thất bại thì cả hai đều không thay đổi.
2. Yêu cầu chức năng liên quan: `FR-TRF-02`.

### 6.12 UC-CAT-01: Bổ sung danh mục phân loại mới

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần mở rộng cấu trúc danh mục để theo dõi chi tiết các khoản chi tiêu hoặc nguồn thu đặc thù. Hệ thống: Giữ cấu trúc phân cấp 3 cấp theo `BR-FIN-04`. Danh mục không được thiếu nhóm cha. Tên danh mục và tên nhóm hợp lệ và duy nhất theo `BR-NAM-01`. |
| Mô tả tóm tắt | Bổ sung một danh mục chi tiết (Cấp 3) vào một Nhóm nhu cầu đang dùng (Cấp 2), hoặc một Nhóm nhu cầu mới (Cấp 2) vào một Loại thu chi (Cấp 1). Mục mới ở trạng thái đang dùng và danh sách chọn của biểu mẫu giao dịch được cập nhật. |
| Điều kiện kích hoạt | Người dùng chọn chức năng thêm danh mục trên màn hình Quản lý danh mục thu chi. |

#### Tiền điều kiện

1. Cấu trúc danh mục đã được đọc vào hệ thống (gồm 2 loại thu chi gốc và các nhóm hiện có, mỗi loại thu chi có ít nhất 1 nhóm).
2. Người dùng đang ở màn hình Quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu thao tác thêm gặp lỗi kiểm tra dữ liệu hoặc lỗi ghi tệp, cấu trúc danh mục giữ nguyên trạng thái cũ, không sinh ra bản ghi rác.

#### Hậu điều kiện thành công

1. Danh mục mới được thêm vào đúng Nhóm nhu cầu cha, kế thừa thuộc tính Loại thu chi của nhóm cha và được lưu vào tệp dữ liệu ở trạng thái đang dùng.
2. Cấu trúc danh mục 3 cấp giữ nguyên cấu trúc: mọi danh mục đều thuộc một nhóm cha theo `BR-FIN-04`, tên danh mục không trùng trong cùng một nhóm cha theo `BR-NAM-01`.

#### Luồng sự kiện chính

1. Người dùng mở màn hình "Quản lý danh mục thu chi", trực tiếp hoặc qua đề nghị từ ca sử dụng giao dịch.
2. Hệ thống hiển thị toàn bộ cấu trúc danh mục 3 cấp: Cấp 1 gồm hai nhánh gốc `INCOME` và `EXPENSE`; Cấp 2 gồm các Nhóm nhu cầu; Cấp 3 gồm các Danh mục chi tiết trực thuộc từng nhóm.
3. Người dùng chọn một Nhóm nhu cầu cụ thể (ví dụ: một nhóm thuộc loại Chi tiêu (`EXPENSE`)) và nhấn nút "Thêm danh mục con".
4. Hệ thống hiển thị biểu mẫu tạo danh mục gồm: Nhóm cha (chỉ đọc), Tên danh mục mới, Biểu tượng và màu sắc nhận diện (tùy chọn).
5. Người dùng nhập tên danh mục mới, chọn biểu tượng và nhấn nút "Lưu danh mục".
6. Hệ thống kiểm tra dữ liệu:
   - Kiểm tra định dạng tên danh mục theo `BR-NAM-01`. Tên sai định dạng bị từ chối theo `ERR-NAM-001`.
   - Kiểm tra tính duy nhất của tên danh mục theo `BR-NAM-01`, so với các danh mục đang dùng khác trong cùng Nhóm cha. Tên trùng bị từ chối theo `ERR-NAM-002`.
   - Kiểm tra mã nhóm cha tồn tại và đang dùng.
7. Hệ thống tạo bản ghi mới cho Danh mục trong tệp dữ liệu với mã duy nhất, đặt mã nhóm làm mã liên kết tới nhóm cha, và đặt trạng thái đang dùng.
8. Hệ thống làm mới cấu trúc danh mục hiển thị trên giao diện, tự động mở rộng nhánh nhóm cha để làm nổi bật danh mục vừa tạo.
9. Hệ thống cập nhật danh sách chọn tại các biểu mẫu giao dịch để sẵn sàng sử dụng danh mục mới.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Thêm Nhóm nhu cầu mới

1. Tại bước 3 của luồng chính, Người dùng chọn một Loại thu chi (`INCOME` hoặc `EXPENSE`) và nhấn nút "Thêm nhóm".
2. Hệ thống hiển thị biểu mẫu tạo nhóm gồm: Loại thu chi (chỉ đọc), Tên nhóm mới, Biểu tượng và màu sắc nhận diện (tùy chọn).
3. Người dùng nhập tên nhóm và nhấn nút "Lưu nhóm". Hệ thống kiểm tra tên nhóm theo `BR-NAM-01`. Tên sai định dạng bị từ chối theo `ERR-NAM-001`, tên trùng một nhóm đang dùng khác trong cùng Loại thu chi bị từ chối theo `ERR-NAM-002`.
4. Hệ thống tạo bản ghi nhóm với mã duy nhất, ở trạng thái đang dùng. Luồng tiếp tục tại bước 8 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 6a: Trùng tên danh mục trong cùng một nhóm

1. Tại bước 6 của luồng chính, hệ thống phát hiện tên danh mục nhập vào trùng một danh mục đang dùng khác trong cùng Nhóm cha theo `BR-NAM-01` (`ERR-NAM-002`).
2. Hệ thống từ chối lưu, đánh dấu trường tên danh mục và hiển thị thông báo lỗi: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác."
3. Luồng quay lại bước 5 của luồng chính để Người dùng hiệu chỉnh tên danh mục.

##### Luồng ngoại lệ 6b: Nhóm cha không tồn tại, đã xóa hoặc bị lỗi liên kết

1. Tại bước 6 của luồng chính, hệ thống kiểm tra và phát hiện một trong hai trường hợp: nhóm cha đang ở trạng thái đã xóa, hoặc mã nhóm cha không tồn tại hoặc bị lỗi liên kết.
2. Hệ thống từ chối tạo và xử lý phản hồi tương ứng:
   - Nếu nhóm cha đang ở trạng thái đã xóa, hệ thống từ chối theo `ERR-CAT-005` và thông báo lỗi: "Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng hoặc khôi phục nhóm trước."
   - Nếu nhóm cha không tồn tại hoặc bị lỗi liên kết, hệ thống thông báo lỗi cấu trúc: "Không tìm thấy nhóm nhu cầu cha hợp lệ."
3. Luồng kết thúc, hủy bỏ thao tác.

##### Luồng ngoại lệ 6c: Tên trùng với mục đã xóa

1. Tại bước 6 của luồng chính (hoặc bước 3 của luồng rẽ nhánh 3a), hệ thống phát hiện tên nhập vào trùng với một hoặc nhiều danh mục hoặc nhóm đã xóa trong cùng phạm vi.
2. Hệ thống hiển thị hộp thoại hỏi Người dùng chọn Khôi phục mục cũ (giữ mã cũ và nối lại giao dịch cũ) hoặc Tạo mới (mã mới). Nếu có nhiều mục đã xóa cùng tên trong cùng phạm vi, hộp thoại liệt kê danh sách các mục đã xóa (kèm tên và số lượng giao dịch còn gắn với mỗi mục) để Người dùng chọn đúng một mục cần khôi phục, hoặc chọn Tạo mới.
3. Nếu chọn Khôi phục, luồng chuyển sang ca sử dụng `UC-CAT-03` kèm theo mục đã chọn để khôi phục. Nếu chọn Tạo mới, luồng tiếp tục tạo bản ghi với mã mới tại bước 7 của luồng chính (danh mục) hoặc bước 4 của luồng rẽ nhánh 3a (nhóm).

##### Luồng ngoại lệ 6d: Trùng tên nhóm trong cùng một Loại thu chi

1. Tại bước 3 của luồng rẽ nhánh 3a, hệ thống phát hiện tên nhóm mới trùng một nhóm đang dùng khác trong cùng Loại thu chi theo `BR-NAM-01` (`ERR-NAM-002`).
2. Hệ thống từ chối lưu, đánh dấu trường tên nhóm và hiển thị thông báo lỗi: "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác."
3. Luồng quay lại bước 3 của luồng rẽ nhánh 3a để Người dùng nhập lại tên nhóm.

##### Luồng ngoại lệ 6e: Tên danh mục hoặc tên nhóm sai định dạng

1. Tại bước 6 của luồng chính (hoặc bước 3 của luồng rẽ nhánh 3a), hệ thống phát hiện tên danh mục hoặc tên nhóm nhập vào sai định dạng theo `BR-NAM-01` (`ERR-NAM-001`).
2. Hệ thống từ chối lưu, đánh dấu trường tên và hiển thị thông báo lỗi: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Luồng quay lại bước 5 của luồng chính (danh mục) hoặc bước 3 của luồng rẽ nhánh 3a (nhóm) để Người dùng nhập lại tên.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc cấu trúc 3 cấp theo `BR-FIN-04`: Bảo đảm mỗi Loại thu chi có ít nhất 1 nhóm đang dùng và mọi danh mục thuộc đúng một nhóm cha. Nghiêm cấm tồn tại nút thiếu nhóm cha hoặc gán sai cấp.
2. Yêu cầu chức năng liên quan: `FR-CAT-01`, `FR-CAT-02`.

### 6.13 UC-CAT-02: Đổi tên danh mục

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần chỉnh sửa tên hiển thị của danh mục cho rõ ràng hơn. Hệ thống: Giữ nguyên toàn bộ liên kết dữ liệu với các giao dịch trong quá khứ. Các giao dịch lịch sử tự động hiển thị tên mới mà không làm thay đổi dữ liệu tiền đã ghi. Tên mới hợp lệ và duy nhất theo `BR-NAM-01`. |
| Mô tả tóm tắt | Đổi tên hiển thị của một danh mục chi tiết hoặc một Nhóm nhu cầu đang dùng trong cấu trúc danh mục. Toàn bộ các giao dịch quá khứ liên kết với mục này hiển thị theo tên mới và không bị mất liên kết. Ca này cũng cho chuyển một danh mục sang nhóm khác cùng Loại thu chi. |
| Điều kiện kích hoạt | Người dùng chọn chức năng đổi tên danh mục hoặc nhóm trên cấu trúc danh mục. |

#### Tiền điều kiện

1. Danh mục hoặc nhóm cần đổi tên tồn tại và đang dùng trong cấu trúc danh mục.
2. Người dùng đang ở màn hình Quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu thao tác đổi tên gặp lỗi kiểm tra dữ liệu hoặc Người dùng hủy bỏ, tên danh mục và toàn bộ liên kết lịch sử giữ nguyên trạng.

#### Hậu điều kiện thành công

1. Tên hiển thị mới của danh mục hoặc nhóm được cập nhật vào tệp dữ liệu. Với luồng chuyển nhóm, danh mục thuộc nhóm đích và giữ nguyên mã cùng giao dịch liên kết.
2. Toàn bộ các giao dịch trong quá khứ liên kết với mã danh mục này tiếp tục giữ nguyên liên kết dữ liệu và hiển thị theo tên mới.

#### Luồng sự kiện chính

1. Người dùng chọn một danh mục hoặc một Nhóm nhu cầu trên cấu trúc danh mục và nhấn nút "Đổi tên".
2. Hệ thống hiển thị hộp thoại chỉnh sửa tên, điền sẵn tên hiển thị hiện tại.
3. Người dùng nhập tên mới và nhấn nút "Lưu thay đổi".
4. Hệ thống kiểm tra dữ liệu:
   - Kiểm tra định dạng tên mới theo `BR-NAM-01`. Tên sai định dạng bị từ chối theo `ERR-NAM-001`.
   - Kiểm tra tính duy nhất của tên mới theo `BR-NAM-01`, so với các mục đang dùng khác cùng cấp: danh mục trong cùng Nhóm cha, hoặc nhóm trong cùng Loại thu chi. Tên trùng bị từ chối theo `ERR-NAM-002`.
5. Hệ thống cập nhật trường tên của danh mục hoặc nhóm trong tệp dữ liệu. Với danh mục, toàn bộ các giao dịch lịch sử liên kết với mã danh mục này tự động phản ánh tên mới qua mã liên kết tới danh mục. Với nhóm, các danh mục con giữ nguyên mã và liên kết giao dịch.
6. Hệ thống hiển thị thông báo thành công (đổi tên hoặc chuyển nhóm) và làm mới cấu trúc danh mục trên giao diện.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Chuyển danh mục sang nhóm khác

1. Tại bước 1 của luồng chính, Người dùng chọn một danh mục chi tiết và nhấn nút "Chuyển nhóm".
2. Hệ thống hiển thị danh sách các nhóm đang dùng cùng Loại thu chi với danh mục, đánh dấu nhóm hiện tại.
3. Người dùng chọn nhóm đích. Hệ thống kiểm tra nhóm đích đang dùng, cùng Loại thu chi, và tên danh mục không trùng danh mục đang dùng trong nhóm đích theo `BR-NAM-01`.
4. Hệ thống hiển thị hộp xác nhận nêu số giao dịch của danh mục, nhóm cũ và nhóm mới. Hộp xác nhận cũng cảnh báo báo cáo theo nhóm của các kỳ đã qua sẽ tính số tiền này vào nhóm mới. Tổng thu và tổng chi không đổi.
5. Người dùng nhấn nút "Xác nhận chuyển". Hệ thống cập nhật nhóm cha của danh mục, giữ nguyên mã và mọi giao dịch liên kết.
6. Luồng tiếp tục tại bước 6 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Trùng tên danh mục trong cùng một nhóm

1. Tại bước 4 của luồng chính, hệ thống phát hiện tên mới trùng một danh mục đang dùng khác trong cùng Nhóm cha theo `BR-NAM-01` (`ERR-NAM-002`).
2. Hệ thống từ chối lưu, đánh dấu trường tên và hiển thị thông báo lỗi: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác."
3. Luồng quay lại bước 3 của luồng chính để Người dùng nhập lại tên hợp lệ.

##### Luồng ngoại lệ 4b: Trùng tên nhóm trong cùng một Loại thu chi

1. Tại bước 4 của luồng chính, hệ thống phát hiện tên mới của nhóm trùng một nhóm đang dùng khác trong cùng Loại thu chi theo `BR-NAM-01` (`ERR-NAM-002`).
2. Hệ thống từ chối lưu, đánh dấu trường tên và hiển thị thông báo lỗi: "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác."
3. Luồng quay lại bước 3 của luồng chính để Người dùng nhập lại tên hợp lệ.

##### Luồng ngoại lệ 4c: Tên mới sai định dạng

1. Tại bước 4 của luồng chính, hệ thống phát hiện tên mới của danh mục hoặc nhóm sai định dạng theo `BR-NAM-01` (`ERR-NAM-001`).
2. Hệ thống từ chối lưu, đánh dấu trường tên và hiển thị thông báo lỗi: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Luồng quay lại bước 3 của luồng chính để Người dùng nhập lại tên hợp lệ.

##### Luồng ngoại lệ 1b: Nhóm đích không hợp lệ

1. Tại bước 3 của luồng rẽ nhánh 1a, nhóm đích đã xóa.
2. Hệ thống từ chối chuyển vì nhóm đích đã xóa (`ERR-CAT-003`) và giữ nguyên danh mục ở nhóm cũ.
3. Luồng quay lại bước 3 của luồng rẽ nhánh 1a để Người dùng chọn nhóm đích khác.

##### Luồng ngoại lệ 1c: Trùng tên trong nhóm đích

1. Tại bước 3 của luồng rẽ nhánh 1a, nhóm đích đã có danh mục đang dùng cùng tên theo `BR-NAM-01` (`ERR-NAM-002`).
2. Hệ thống từ chối chuyển và hiển thị thông báo: "Nhóm đích đã có danh mục cùng tên. Hãy đổi tên danh mục trước khi chuyển."
3. Luồng quay lại bước 3 của luồng rẽ nhánh 1a để Người dùng chọn nhóm đích khác.

##### Luồng ngoại lệ 1d: Chuyển danh mục sang nhóm khác Loại thu chi

1. Tại bước 3 của luồng rẽ nhánh 1a, hệ thống nhận yêu cầu chuyển danh mục sang nhóm đích thuộc Loại thu chi khác với danh mục. Yêu cầu này không đi qua danh sách nhóm ở bước 2.
2. Hệ thống từ chối vì không được phép chuyển danh mục sang nhóm thuộc loại thu chi khác (`ERR-CAT-001`).
3. Luồng quay lại bước 3 của luồng rẽ nhánh 1a để Người dùng chọn nhóm đích khác.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc giữ lịch sử giao dịch thu chi: Việc đổi tên danh mục không được phá vỡ liên kết dữ liệu với các giao dịch lịch sử.
2. Quy tắc giữ nguyên Loại theo `BR-FIN-04`: Cấm chuyển danh mục sang Loại thu chi khác và cấm đổi Loại của nhóm hoặc danh mục đã có giao dịch. Mục đích là tránh làm sai lệch báo cáo dòng tiền quá khứ.
3. Quy tắc chuyển nhóm: Báo cáo luôn gom theo nhóm hiện tại của danh mục, không lưu nhóm tại thời điểm ghi giao dịch. Nhóm nhu cầu không chuyển được vì cha của nhóm là Loại thu chi, mà Loại thu chi không đổi.
4. Yêu cầu chức năng liên quan: `FR-CAT-02`, `FR-CAT-03`.

### 6.14 UC-CAT-03: Khôi phục hoặc tạo lại mục đã xóa

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần lấy lại một nhóm hoặc danh mục đã xóa nhầm hoặc cần dùng lại, và chọn giữa nối lại lịch sử cũ hoặc bắt đầu mới. Hệ thống: Bảo đảm việc khôi phục không làm mất hay đổi dữ liệu lịch sử, và không để hai mục đang dùng trùng tên trong cùng phạm vi theo `BR-NAM-01`. |
| Mô tả tóm tắt | Từ màn hình "Danh mục đã xóa", Người dùng chọn một nhóm hoặc danh mục đã xóa. Sau đó, Người dùng chọn Khôi phục (giữ mã cũ, nối lại giao dịch cũ) hoặc Tạo mới (mã mới, không nối giao dịch cũ). |
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
4. Hệ thống kiểm tra tên theo `BR-NAM-01`: không có mục đang dùng nào trùng tên trong cùng phạm vi (danh mục trong cùng nhóm, nhóm trong cùng Loại thu chi). Với danh mục, hệ thống kiểm tra thêm nhóm cha đang dùng.
5. Hệ thống chuyển mục sang trạng thái đang dùng với mã cũ, nối lại giao dịch cũ và đưa mục trở lại danh sách chọn khi ghi giao dịch mới.
6. Hệ thống làm mới cấu trúc danh mục và hiển thị thông báo khôi phục thành công.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Vào từ hộp hỏi của UC-CAT-01

1. Tại luồng ngoại lệ 6c của `UC-CAT-01`, Người dùng chọn Khôi phục.
2. Hệ thống nhận mục đã chọn từ `UC-CAT-01` (nếu có nhiều mục đã xóa cùng tên) hoặc lấy mục đã xóa duy nhất (nếu chỉ có một mục cùng tên) làm mục cần khôi phục, và bỏ qua các bước 1 đến 3 của luồng chính.
3. Luồng tiếp tục tại bước 4 của luồng chính.

##### Luồng rẽ nhánh 3a: Tạo mới thay vì khôi phục

1. Tại bước 3 của luồng chính, Người dùng nhấn nút "Tạo mới".
2. Hệ thống yêu cầu nhập tên (điền sẵn tên cũ) và nơi chứa. Sau đó hệ thống kiểm tra tên theo `BR-NAM-01`: đúng định dạng và không trùng mục đang dùng trong cùng phạm vi (danh mục trong cùng nhóm, nhóm trong cùng Loại thu chi). Bước này không hỏi lại Khôi phục hoặc Tạo mới.
3. Hệ thống tạo mục mới với mã mới ở trạng thái đang dùng. Mục cũ giữ nguyên trạng thái đã xóa.
4. Luồng tiếp tục tại bước 6 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3b: Tên hoặc nhóm cha không hợp lệ khi tạo mới

1. Tại bước 2 của luồng rẽ nhánh 3a, hệ thống phát hiện tên nhập vào trùng với mục đang dùng trong cùng phạm vi theo `BR-NAM-01` (`ERR-NAM-002`). Trường hợp khác là nhóm cha của danh mục đang ở trạng thái đã xóa.
2. Hệ thống từ chối tạo và hiển thị thông báo theo trường hợp: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác." (danh mục trùng tên), "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác." (nhóm trùng tên), hoặc từ chối theo `ERR-CAT-005` với thông báo: "Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng hoặc khôi phục nhóm trước." (nhóm cha đã xóa).
3. Luồng quay lại bước 2 của luồng rẽ nhánh 3a để Người dùng nhập lại tên hoặc chọn nơi chứa khác.

##### Luồng ngoại lệ 3c: Tên sai định dạng khi tạo mới

1. Tại bước 2 của luồng rẽ nhánh 3a, hệ thống phát hiện tên nhập vào sai định dạng theo `BR-NAM-01` (`ERR-NAM-001`).
2. Hệ thống từ chối tạo và hiển thị thông báo: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Luồng quay lại bước 2 của luồng rẽ nhánh 3a để Người dùng nhập lại tên.

##### Luồng ngoại lệ 4a: Trùng tên với mục đang dùng

1. Tại bước 4 của luồng chính, hệ thống phát hiện đã có mục đang dùng cùng tên trong cùng phạm vi.
2. Hệ thống từ chối theo `ERR-NAM-002` và hiển thị thông báo: "Không thể khôi phục vì tên đã được dùng. Hãy đổi tên một trong hai mục rồi thử lại."
3. Luồng kết thúc, mục giữ nguyên trạng thái đã xóa.

##### Luồng ngoại lệ 4b: Nhóm cha vẫn ở trạng thái đã xóa

1. Tại bước 4 của luồng chính, Người dùng khôi phục một danh mục mà nhóm cha của nó còn ở trạng thái đã xóa.
2. Hệ thống từ chối theo `ERR-CAT-004` với thông báo của trường hợp nhóm chứa danh mục đang ở trạng thái đã xóa.
3. Luồng kết thúc, danh mục giữ nguyên trạng thái đã xóa.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc giữ liên kết dữ liệu theo `BR-FIN-04`: Khôi phục giữ nguyên mã cũ nên không tạo dữ liệu mồ côi. Mã định danh không bao giờ dùng lại cho mục khác.
2. Khôi phục nhóm chỉ khôi phục riêng nhóm. Các danh mục con của nhóm khôi phục riêng từng cái.
3. Yêu cầu chức năng liên quan: `FR-CAT-02`, `FR-CAT-03`.

### 6.15 UC-CAT-04: Xóa danh mục hoặc nhóm

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Cấp độ mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Người dùng: Cần bỏ các danh mục hoặc nhóm không còn dùng bằng một thao tác duy nhất, kể cả khi đã có giao dịch, mà không mất lịch sử. Hệ thống: Không để giao dịch trỏ tới danh mục không còn tồn tại, và không để một Loại thu chi mất hết nhóm. |
| Mô tả tóm tắt | Người dùng chọn Xóa một danh mục hoặc một nhóm. Mục chưa có giao dịch bị xóa hẳn. Mục đã có giao dịch chuyển sang trạng thái đã xóa, giao dịch được giữ nguyên. Xóa nhóm kéo theo mọi danh mục con. |
| Điều kiện kích hoạt | Người dùng chọn chức năng xóa trên một danh mục hoặc nhóm trong cấu trúc danh mục. |

#### Tiền điều kiện

1. Danh mục hoặc nhóm cần xóa tồn tại và đang dùng trong cấu trúc danh mục.
2. Người dùng đang ở màn hình Quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu thao tác bị từ chối, bị hủy hoặc gặp lỗi ghi dữ liệu, toàn bộ cấu trúc danh mục và mọi giao dịch được giữ nguyên.

#### Hậu điều kiện thành công

1. Mục chưa có giao dịch bị xóa hoàn toàn khỏi tệp dữ liệu.
2. Mục đã có giao dịch ở trạng thái đã xóa không còn trong danh sách chọn cho giao dịch mới. Mục này vẫn hiện trong báo cáo lịch sử kèm nhãn (đã xóa) và có thể được khôi phục tại `UC-CAT-03`.
3. Khi xóa nhóm: mỗi danh mục con được xử lý theo các điểm trên. Nhóm có giao dịch bên trong thì ở trạng thái đã xóa, nhóm không có thì bị xóa hẳn.

#### Luồng sự kiện chính

1. Người dùng chọn một danh mục hoặc nhóm và nhấn nút "Xóa".
2. Hệ thống quét sổ giao dịch thu chi để đếm giao dịch liên kết tới mục đó. Với nhóm, hệ thống đếm theo từng danh mục con đang dùng.
3. Hệ thống hiển thị hộp xác nhận. Với danh mục: nêu số giao dịch liên quan và kết quả (xóa hẳn nếu chưa có giao dịch, ẩn và giữ giao dịch nếu đã có). Với nhóm: nêu số danh mục sẽ bị ẩn, số danh mục xóa hẳn và tổng số giao dịch được giữ lại.
4. Người dùng nhấn nút "Xác nhận xóa".
5. Hệ thống thực hiện xóa hoặc chuyển sang trạng thái đã xóa theo kết quả đã nêu, rồi cập nhật cấu trúc danh mục.
6. Hệ thống hiển thị thông báo thành công. Mục đã xóa có trong màn hình "Danh mục đã xóa".

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Xóa nhóm đang dùng cuối cùng của một Loại thu chi

1. Tại bước 2 của luồng chính, hệ thống phát hiện nhóm được chọn là nhóm đang dùng cuối cùng của Loại thu chi chứa nó.
2. Hệ thống từ chối theo `ERR-CAT-002` và hiển thị thông báo: "Không thể xóa nhóm cuối cùng của loại thu chi này."
3. Luồng kết thúc, cấu trúc danh mục giữ nguyên.

##### Luồng ngoại lệ 4a: Người dùng hủy xác nhận

1. Tại bước 4 của luồng chính, Người dùng nhấn nút "Hủy".
2. Hệ thống không thay đổi gì và đóng hộp xác nhận.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc giữ liên kết dữ liệu theo `BR-FIN-04`: Mục đã có giao dịch không bao giờ bị xóa vật lý, để không có giao dịch nào mất danh mục.
2. Người dùng chỉ thấy một thao tác "Xóa". Việc xóa hẳn hay ẩn do hệ thống quyết định theo số giao dịch.
3. Yêu cầu chức năng liên quan: `FR-CAT-03`.

### 6.16 UC-REP-01: Thống kê dòng tiền 12 tháng gần nhất

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Đánh giá tình hình tài chính định kỳ qua chuỗi dòng tiền 12 tháng gần nhất và tỷ lệ tiết kiệm tương ứng. |
| Mô tả tóm tắt | Tổng hợp chuỗi dòng tiền 12 tháng gần nhất từ mốc tháng chỉ định, thành bảng thống kê 12 tháng theo `FR-REP-01`. Tính toán thu nhập, chi tiêu, dòng tiền thuần và tỷ lệ tiết kiệm theo `BR-FIN-01`. |
| Điều kiện kích hoạt | Người dùng cần đánh giá dòng tiền định kỳ trong 12 tháng gần nhất. |

#### Tiền điều kiện

1. Hệ thống đã khởi chạy và đọc thành công tệp dữ liệu giao dịch.
2. Người dùng đang ở giao diện nhóm chức năng Thống kê và Báo cáo tài chính.

#### Bảo đảm tối thiểu

1. Hệ thống hoạt động ở chế độ chỉ đọc, không làm biến đổi bất kỳ dữ liệu giao dịch hay số dư ví nào trong hệ thống.

#### Hậu điều kiện thành công

1. Bảng thống kê dòng tiền gồm đúng 12 dòng liên tục lùi từ tháng kết thúc chỉ định được tổng hợp đầy đủ, không để trống trường dữ liệu nào.
2. Dòng tiền thuần và Tỷ lệ tiết kiệm được tính toán chính xác theo quy tắc `FR-REP-01`. Ba mức bình quân được chia cho 12, kèm ghi chú nêu rằng các tháng chưa có giao dịch vẫn được tính.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Thống kê dòng tiền 12 tháng gần nhất", tùy chọn chọn tháng và năm kết thúc (mặc định là tháng và năm hiện tại).
2. Hệ thống xác định mốc tháng kết thúc (gồm tháng và năm kết thúc), mặc định trích xuất từ đồng hồ hệ thống.
3. Hệ thống tạo danh sách 12 mốc tháng liên tục từ 11 tháng trước mốc tháng kết thúc đến chính mốc đó. Danh sách được sắp xếp theo trình tự thời gian tăng dần (neo tháng kết thúc ở dòng cuối cùng).
4. Với từng tháng trong chuỗi 12 tháng, hệ thống duyệt danh sách bản ghi:
   - Lọc các giao dịch có ngày nằm trong khoảng từ ngày đầu tháng đến ngày cuối tháng của tháng đang xét.
   - Tính tổng thu nhập theo `FR-REP-01`: cộng số tiền của toàn bộ giao dịch thu nhập có ngày trong tháng.
   - Tính tổng chi tiêu: cộng số tiền của toàn bộ giao dịch chi tiêu có ngày trong tháng (tính trên các giao dịch đang tồn tại).
5. Hệ thống tính toán các chỉ số phái sinh cho từng tháng theo quy tắc `FR-REP-01`:
   - Dòng tiền thuần: bằng tổng thu nhập của tháng trừ đi tổng chi tiêu của tháng đó.
   - Tỷ lệ tiết kiệm: bằng dòng tiền thuần chia cho tổng thu nhập rồi nhân với 100 khi tổng thu nhập lớn hơn 0, ngược lại bằng 0.00%. Trong đó `NetCashFlow_m` là dòng tiền thuần và `Income_m` là tổng thu nhập của tháng đang xét:
     $$
     SavingsRate_m = \begin{cases} \left(\frac{NetCashFlow_m}{Income_m}\right) \times 100\%, & \text{khi } Income_m > 0 \\ 0.00\%, & \text{khi } Income_m \le 0 \end{cases}
     $$
     Áp dụng quy tắc làm tròn ngân hàng với 2 chữ số thập phân khi hiển thị.
6. Hệ thống tính các mức bình quân tháng của chuỗi 12 tháng theo `FR-REP-01`: Thu nhập bình quân, Chi tiêu bình quân và Dòng tiền thuần bình quân. Mẫu số luôn là 12, kể cả các tháng chưa có giao dịch, và hệ thống hiển thị ghi chú nêu điều này theo `FR-REP-01`.
7. Hệ thống hiển thị bảng dữ liệu thống kê gồm 12 dòng tháng. Chân bảng có dòng hiển thị ba mức bình quân kèm ghi chú nêu các tháng chưa có giao dịch vẫn được tính. Giao diện đồng thời hiển thị biểu đồ kết hợp cột thu chi và đường biểu diễn dòng tiền thuần.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Tháng hoàn toàn không có giao dịch

1. Tại bước 4 của luồng chính, hệ thống thấy trong một tháng nào đó không có giao dịch thu chi (tổng thu nhập nhỏ hơn hoặc bằng 0 và tổng chi tiêu bằng 0).
2. Hệ thống hiển thị giá trị số tiền là "0.00 VNĐ", không để trống ô dữ liệu hoặc gán giá trị `null`.
3. Hệ thống gán dòng tiền thuần bằng 0.00 và tỷ lệ tiết kiệm bằng 0.00%, bảo đảm bảng dữ liệu 12 tháng luôn liên tục và không gây lỗi giao diện.

##### Luồng ngoại lệ 5a: Tháng có thu nhập không dương nhưng chi tiêu lớn hơn 0

1. Tại bước 5 của luồng chính, hệ thống phát hiện tổng thu nhập của tháng nhỏ hơn hoặc bằng 0 nhưng tổng chi tiêu lớn hơn 0. Đó là trường hợp Người dùng không có thu nhập trong tháng nhưng vẫn có chi tiêu sinh hoạt.
2. Nếu thực hiện phép chia trực tiếp để tính tỷ lệ tiết kiệm, hệ thống sẽ gặp lỗi chia cho 0 hoặc lỗi số học theo mã `ERR-FIN-002`.
3. Theo quy tắc xử lý biên `FR-REP-01`, hệ thống áp dụng hàm từng nhánh và gán tỷ lệ tiết kiệm bằng 0.00%. Hệ thống hiển thị dòng tiền thuần âm (bằng âm của tổng chi tiêu) và đưa ra chỉ báo thâm hụt ngân sách.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc chuỗi 12 tháng gần nhất theo `FR-REP-01`: Bảng thống kê luôn gồm đúng 12 tháng liên tục lùi từ tháng kết thúc chỉ định (mặc định tháng hiện tại). Bảng tự động thích ứng với mốc thời gian được chọn.
2. Quy tắc tính toán tài chính theo `BR-FIN-01`: Toàn bộ chỉ số thu nhập, chi tiêu, dòng tiền thuần đều tính bằng kiểu dữ liệu tiền tệ số thập phân chính xác cố định. Các chỉ số áp dụng quy tắc làm tròn ngân hàng với 2 chữ số thập phân, không có sai số dấu phẩy động.
3. Yêu cầu chức năng liên quan: `FR-REP-01`.

### 6.17 UC-REP-02: Phân tích cơ cấu thu chi theo nhóm và danh mục

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Phân tích cơ cấu thu chi 2 tầng phân cấp theo nhóm và danh mục, đối chiếu biến động so với kỳ liền trước. |
| Mô tả tóm tắt | Phân tích cơ cấu thu chi 2 tầng phân cấp theo Nhóm nhu cầu và Danh mục chi tiết trong một khoảng thời gian hoặc tháng được chọn. Kết quả kèm so sánh biến động với kỳ trước liền kề theo `FR-REP-02`. |
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
2. Hệ thống quét các giao dịch đang tồn tại trong khoảng thời gian được chọn có loại tương ứng.
3. Hệ thống tổng hợp số liệu theo 2 tầng phân cấp theo quy tắc `FR-REP-02`:
   - Cấp nhóm nhu cầu: Tính tổng số tiền và tỷ lệ % trên tổng thu hoặc chi của kỳ (`Amount_group` là tổng số tiền của nhóm, `TotalPeriod` là tổng thu hoặc chi của kỳ):
     $$Share_{group} = \left(\frac{Amount_{group}}{TotalPeriod}\right) \times 100$$
   - Cấp danh mục chi tiết: Tính tổng số tiền, tỷ lệ % trên nhóm cha và tỷ lệ % trên toàn bộ kỳ (`Amount_cat` là tổng số tiền của danh mục):
     $$
     Share_{cat/group} = \left(\frac{Amount_{cat}}{Amount_{group}}\right) \times 100\%, \quad Share_{cat/period} = \left(\frac{Amount_{cat}}{TotalPeriod}\right) \times 100\%
     $$
4. Hệ thống truy vấn số liệu của kỳ liền trước có cùng độ dài để tính toán so sánh:
   - Chênh lệch tuyệt đối: bằng số tiền kỳ hiện tại trừ đi số tiền kỳ trước đó.
   - Tỷ lệ phần trăm biến động: bằng chênh lệch tuyệt đối chia cho số tiền kỳ trước rồi nhân với 100 khi số tiền kỳ trước lớn hơn 0. Nếu số tiền kỳ trước bằng 0, hệ thống hiển thị nhãn "Mới".
5. Hệ thống sắp xếp các danh mục theo số tiền chi tiêu giảm dần.
6. Hệ thống hiển thị bảng Pivot phân cấp kèm biểu đồ tròn cơ cấu chi tiêu. Nhóm và danh mục đã xóa vẫn hiển thị kèm nhãn (đã xóa) theo quy tắc hiển thị của `FR-REP-02`.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Kỳ trước không có giao dịch khi so sánh biến động

1. Tại bước 4 của luồng chính, hệ thống phát hiện kỳ trước không có giao dịch trong danh mục này nhưng kỳ này có giao dịch.
2. Hệ thống hiển thị chênh lệch tuyệt đối bằng số tiền kỳ này và hiển thị tỷ lệ biến động là "Mới". Cách hiển thị này tránh thực hiện phép chia cho 0 hoặc phát sinh lỗi số học theo mã `ERR-FIN-002`.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc tính toán tài chính theo `BR-FIN-01`: Toàn bộ các phép tổng hợp và tỷ lệ phần trăm tính bằng kiểu dữ liệu tiền tệ số thập phân chính xác cố định. Các phép tính áp dụng quy tắc làm tròn ngân hàng. Số tiền làm tròn đến 2 chữ số thập phân. Với tỷ lệ phần trăm, phép tính trung gian giữ tối thiểu 4 chữ số thập phân và kết quả làm tròn về 2 chữ số thập phân khi hiển thị theo `FR-REP-02`.
2. Quy tắc hiển thị mục đã xóa: Nhóm và danh mục đã xóa giữ tên gốc kèm nhãn (đã xóa), không gộp vào dòng Khác. Dòng số 0 chỉ hiện cho kỳ quá khứ từ kỳ chứa ngày mục được tạo. Kỳ trước ngày tạo và kỳ ở tương lai ẩn dòng.
3. Yêu cầu chức năng liên quan: `FR-REP-02`.

### 6.18 UC-DAT-01: Xuất dữ liệu tài chính

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Sao lưu dữ liệu tài chính cục bộ ra định dạng mở, giữ nguyên vẹn độ chính xác số học, không làm tròn. |
| Mô tả tóm tắt | Xuất dữ liệu tiền trong hệ thống (toàn phần hoặc từng phần) ra định dạng JSON. Hệ thống giữ độ chính xác của các trường số học tài chính khi xuất, theo `FR-DAT-01`. |
| Điều kiện kích hoạt | Người dùng cần sao lưu định kỳ hoặc xuất dữ liệu ra tệp định dạng mở. |

#### Tiền điều kiện

1. Hệ thống PFMS đang hoạt động bình thường, có quyền đọc và ghi trên thư mục lưu trữ cục bộ được chỉ định.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

1. Quá trình xuất dữ liệu không làm biến đổi dữ liệu hiện có trong hệ thống. Nếu xảy ra lỗi ghi đĩa, hệ thống dọn dẹp tệp xuất dở dang và giữ nguyên toàn bộ dữ liệu nội bộ.

#### Hậu điều kiện thành công

1. Tệp sao lưu được tạo thành công với mã hóa UTF-8. Các trường số học được lưu dưới dạng chuỗi số học giữ nguyên các chữ số thập phân (không bị làm tròn, không dùng ký hiệu khoa học) theo quy định `CON-02`. Tệp đặt tên tự động theo cấu trúc `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json` theo quy tắc `FR-DAT-01`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Xuất dữ liệu tài chính".
2. Hệ thống hiển thị biểu mẫu xuất dữ liệu gồm các tùy chọn:
   - Phạm vi dữ liệu: `FULL` (Toàn bộ hệ thống), `TRANSACTIONS` (Sổ giao dịch thu chi).
   - Định dạng tệp: `JSON`.
   - Thư mục lưu trữ trên máy tính: Người dùng chọn qua hộp thoại duyệt thư mục.
3. Người dùng chọn phạm vi là `FULL`, định dạng `JSON`, và chọn thư mục lưu trữ đích.
4. Người dùng xác nhận bắt đầu tiến trình xuất dữ liệu.
5. Hệ thống trích xuất toàn bộ dữ liệu hiện tại từ tệp dữ liệu gồm các nhóm sau:
   - Ví, danh mục phân loại, giao dịch thu chi (kể cả phí giao dịch), chuyển tiền nội bộ và bản ghi điều chỉnh
6. Hệ thống chuyển đổi các đối tượng nghiệp vụ sang cấu trúc JSON:
   - Chuẩn hóa toàn bộ các trường số học tài chính thành chuỗi số học chính xác (ví dụ: `"50000000.00"`). Quy tắc `FR-DAT-01` cấm chuyển đổi sang kiểu số thực có sai số hoặc ký hiệu số mũ khoa học (`5.0E7`).
   - Định dạng các trường ngày tháng theo định dạng `YYYY-MM-DD`.
7. Hệ thống tạo tên tệp theo quy tắc: `pfms_export_full_{yyyyMMdd_HHmmss}.json`.
8. Hệ thống ghi tệp xuống đĩa cứng với bảng mã UTF-8 không BOM.
9. Hệ thống kiểm tra kích thước và độ nguyên vẹn của tệp vừa tạo.
10. Hệ thống hiển thị thông báo xuất dữ liệu thành công kèm đường dẫn tệp, kích thước tệp và tổng số lượng bản ghi đã xuất của từng nhóm dữ liệu.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Xuất sổ giao dịch thu chi sang định dạng JSON

1. Tại bước 3 của luồng chính, Người dùng chọn phạm vi `TRANSACTIONS`, định dạng `JSON`, và chọn thư mục lưu trữ đích.
2. Hệ thống trích xuất toàn bộ sổ giao dịch thu chi, bao gồm cả các phí giao dịch của giao dịch chuyển tiền đang tồn tại.
3. Hệ thống tạo tệp xuất dữ liệu JSON mã hóa UTF-8 không BOM gồm danh sách các đối tượng giao dịch thu chi, mỗi bản ghi phí có trường mã giao dịch phát sinh ra phí theo quy tắc `FR-DAT-01`.
4. Tên tệp sinh tự động theo cấu trúc `pfms_export_transactions_{yyyyMMdd_HHmmss}.json`.
5. Luồng tiếp tục tại bước 8 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 8a: Lỗi quyền ghi tệp hoặc ổ đĩa đầy khi xuất dữ liệu

1. Tại bước 8 của luồng chính, hệ thống không thể ghi tệp xuống thư mục chỉ định do không đủ quyền truy cập hệ thống tệp hoặc ổ đĩa đã hết dung lượng lưu trữ.
2. Hệ thống phát hiện lỗi ghi tệp, hiển thị thông báo: "Không thể lưu tệp dữ liệu vào thư mục đã chọn do lỗi quyền truy cập hoặc ổ đĩa đầy. Vui lòng chọn thư mục khác."
3. Thao tác xuất bị hủy bỏ và không để lại tệp rác trên đĩa cứng.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc giữ độ chính xác số học theo `BR-FIN-01`, `FR-DAT-01`: Không làm tròn hoặc sử dụng ký hiệu khoa học đối với trường dữ liệu số học tài chính khi xuất tệp sao lưu.
2. Yêu cầu chức năng liên quan: `FR-DAT-01`.

### 6.19 UC-DAT-02: Phục hồi dữ liệu hệ thống từ bản sao lưu

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Phục hồi dữ liệu tài chính từ một tệp sao lưu đã xuất trước đó mà không làm vi phạm các quy tắc tài chính hiện hành. |
| Mô tả tóm tắt | Phục hồi dữ liệu hệ thống từ một tệp sao lưu qua quy trình kiểm tra dữ liệu 4 bước. Bốn bước gồm cú pháp, danh mục theo `BR-FIN-04`, ví và liên kết chuyển tiền, số dư không âm theo `BR-FIN-02`. Quy trình này có hoàn nguyên tự động theo `FR-DAT-02`. |
| Điều kiện kích hoạt | Người dùng cần phục hồi dữ liệu từ một tệp sao lưu đã có trước đó. |

#### Tiền điều kiện

1. Hệ thống PFMS đang hoạt động bình thường, có quyền đọc trên tệp sao lưu được chỉ định.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

1. Nếu tệp sao lưu không hợp lệ hoặc quá trình nhập bị hủy, toàn bộ dữ liệu hệ thống hiện tại được giữ nguyên vẹn. Hệ thống không ghi bất kỳ dữ liệu sai lệch nào vào bộ lưu trữ.

#### Hậu điều kiện thành công

1. Dữ liệu hiện tại được thay thế toàn bộ bằng dữ liệu từ tệp sao lưu, toàn bộ quy tắc số dư `BR-FIN-02` và danh mục `BR-FIN-04` luôn đúng.
2. Nếu tệp nhập chứa dữ liệu lỗi: Toàn bộ thao tác bị hủy bỏ và hoàn nguyên nguyên trạng, dữ liệu hệ thống hiện tại không bị thay đổi hoặc làm sai lệch.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Nhập dữ liệu từ tệp sao lưu".
2. Hệ thống hiển thị biểu mẫu nhập dữ liệu với hộp thoại chọn tệp sao lưu từ máy tính (`.json`).
3. Người dùng chọn tệp sao lưu JSON hợp lệ.
4. Hệ thống hiển thị hộp thoại cảnh báo: "Phục hồi sẽ thay thế toàn bộ dữ liệu tài chính hiện tại bằng dữ liệu từ tệp sao lưu. Xác nhận tiếp tục thao tác?"
5. Người dùng xác nhận tiếp tục thao tác thay thế dữ liệu.
6. Hệ thống phân tích cú pháp tệp JSON và thực hiện quy trình kiểm tra dữ liệu 4 bước theo quy tắc `FR-DAT-02`:
   - Bước 1 (Kiểm tra định dạng): Kiểm tra cấu trúc cú pháp tệp và tính hợp lệ của định dạng ngày tháng YYYY-MM-DD, chuỗi số học.
   - Bước 2 (Toàn vẹn danh mục theo `BR-FIN-04`): Mọi mã danh mục được các giao dịch dùng tới phải trỏ tới một danh mục hợp lệ (đang dùng hoặc đã xóa). Danh mục đó phải có thuộc tính Loại thu chi tương thích.
   - Bước 3 (Toàn vẹn ví và liên kết chuyển tiền): Mọi giao dịch thu chi và chuyển tiền phải trỏ tới một ví tồn tại trong danh sách ví. Mỗi phí giao dịch phát sinh từ giao dịch chuyển tiền phải trỏ tới một giao dịch chuyển tiền tồn tại.
   - Bước 4 (Số dư ví luôn đúng theo `BR-FIN-02`): Tính lại số dư của từng ví từ lịch sử dòng tiền trong tệp, bảo đảm không có ví nào có số dư âm.
7. Nếu toàn bộ 4 bước kiểm tra đều ĐẠT:
   - Hệ thống bắt đầu ghi tệp.
   - Xóa dữ liệu cũ và đọc toàn bộ các bản ghi từ tệp vào tệp dữ liệu hệ thống.
   - Hệ thống hoàn tất lần ghi tệp.
   - Tính lại toàn bộ số dư từ lịch sử dòng tiền và các bảng thống kê 12 tháng gần nhất.
8. Hệ thống hiển thị báo cáo tổng kết nhập dữ liệu thành công. Báo cáo gồm: tổng số bản ghi đã phục hồi và số lượng từng loại thực thể đã phục hồi.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Người dùng hủy bỏ thao tác khi được cảnh báo thay thế dữ liệu

1. Tại bước 4 của luồng chính, trước cảnh báo thay thế toàn bộ dữ liệu, Người dùng chọn "Hủy bỏ".
2. Hệ thống dừng thao tác nhập, đóng hộp thoại và giữ nguyên hiện trạng dữ liệu.

##### Luồng ngoại lệ 6a: Tệp sao lưu không đúng định dạng cú pháp hoặc bị hỏng

1. Tại bước 6 của luồng chính, hệ thống đọc tệp và phát hiện tệp bị lỗi định dạng JSON (thiếu dấu ngoặc, tệp rỗng hoặc sai mã hóa ký tự).
2. Hệ thống hủy bỏ tiến trình nhập và thông báo lỗi: "Tệp sao lưu bị lỗi cấu trúc cú pháp hoặc bị hỏng dữ liệu. Vui lòng kiểm tra lại tệp nguồn."
3. Dữ liệu hiện tại trong hệ thống được giữ nguyên.

##### Luồng ngoại lệ 6b: Dữ liệu trong tệp vi phạm quy tắc số dư và liên kết dữ liệu

1. Tại bước 6 của luồng chính, hệ thống phát hiện ít nhất một lỗi vi phạm. Các ví dụ gồm: giao dịch chi tiêu trỏ tới ví không tồn tại, hoặc phí giao dịch trỏ tới giao dịch chuyển tiền không tồn tại. Một ví dụ khác là lịch sử dòng tiền trong tệp làm cho số dư ví tiền mặt bị âm.
2. Hệ thống lập tức hủy bỏ toàn bộ quá trình nhập, không ghi bất kỳ bản ghi nào vào tệp dữ liệu.
3. Hệ thống hiển thị bảng thông báo lỗi chi tiết. Bảng liệt kê số dòng bị lỗi và loại vi phạm (vi phạm liên kết dữ liệu hoặc vi phạm quy tắc số dư luôn đúng `BR-FIN-02`). Bảng này giúp Người dùng sửa chữa tệp trước khi thử lại.

#### Quy tắc nghiệp vụ đặc thù

1. Chỉ nhập khi tất cả đều đạt: Quá trình nhập dữ liệu phải tuân thủ nguyên tắc All-or-Nothing. Nếu xuất hiện bất kỳ lỗi nào, toàn bộ quá trình phải được hoàn nguyên về trạng thái trước đó.
2. Quy tắc kiểm tra 4 bước theo `FR-DAT-02`: Không ghi dữ liệu vào tệp dữ liệu nếu chưa vượt qua 4 bước kiểm tra. Bốn bước kiểm tra gồm cấu trúc, danh mục theo `BR-FIN-04`, ví và liên kết chuyển tiền, số dư không âm theo `BR-FIN-02`.
3. Yêu cầu chức năng liên quan: `FR-DAT-02`.

### 6.20 UC-DAT-03: Kiểm tra lại dữ liệu hệ thống

Bảng thuộc tính ca sử dụng:

| Trường | Nội dung |
| --- | --- |
| Tác nhân chính | Người dùng |
| Phạm vi và Cấp độ | Hệ thống PFMS, Mục tiêu người dùng |
| Các bên liên quan và mối quan tâm | Kiểm tra và đối soát độc lập 4 điều kiện toàn vẹn của dữ liệu để phát hiện sớm sai lệch. |
| Mô tả tóm tắt | Chạy công cụ kiểm tra lại độc lập để phát hiện các sai lệch theo `FR-DAT-03`. Các nội dung kiểm tra gồm bốn điều kiện. Đó là số dư ví theo `BR-FIN-02`, cân bằng nguồn - đích khi chuyển tiền theo `BR-FIN-03`, liên kết danh mục theo `BR-FIN-04` và tính toàn vẹn liên kết phí chuyển tiền. |
| Điều kiện kích hoạt | Người dùng cần chạy kiểm tra lại dữ liệu định kỳ hoặc khi nghi ngờ có sai lệch. |

#### Tiền điều kiện

1. Hệ thống PFMS đang hoạt động bình thường, có quyền đọc trên tệp dữ liệu cục bộ.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

1. Quá trình kiểm tra tính toàn vẹn hoạt động ở chế độ chỉ đọc, không làm biến đổi bất kỳ dữ liệu giao dịch, số dư ví nào trong hệ thống.

#### Hậu điều kiện thành công

1. Báo cáo kiểm tra lại dữ liệu phản ánh trung thực trạng thái của 4 điều kiện toàn vẹn của dữ liệu theo `FR-DAT-03`.

#### Luồng sự kiện chính

1. Người dùng chọn chức năng "Kiểm tra lại dữ liệu hệ thống".
2. Hệ thống tự động chạy tiến trình quét toàn bộ tệp dữ liệu, kiểm tra lần lượt 4 điều kiện toàn vẹn theo `FR-DAT-03`:
   - Bước 1 (Kiểm tra lại số dư ví theo `BR-FIN-02`): Quét toàn bộ lịch sử dòng tiền của từng ví và tính số dư độc lập theo công thức `BR-FIN-02`. So sánh kết quả với số dư ghi nhận của từng ví. Kiểm tra sai số bằng 0.00 VNĐ.
   - Bước 2 (Kiểm tra lại cân bằng nguồn - đích khi chuyển tiền theo `BR-FIN-03`): Quét toàn bộ sổ giao dịch chuyển tiền. Kiểm tra số tiền chuyển trừ tại ví nguồn bằng chính xác số tiền cộng tại ví đích (không tính chi phí).
   - Bước 3 (Kiểm tra lại liên kết danh mục theo `BR-FIN-04`): Quét sổ giao dịch thu chi. Kiểm tra không có giao dịch nào gắn với danh mục không có trong dữ liệu (mục đã xóa vẫn tính là có) hoặc không khớp Loại thu chi.
   - Bước 4 (Kiểm tra lại liên kết phí chuyển tiền): Quét toàn bộ các phí giao dịch phát sinh từ giao dịch chuyển tiền. Kiểm tra mỗi phí giao dịch phải trỏ tới một giao dịch chuyển tiền tồn tại.
3. Hệ thống tổng hợp kết quả thành Báo cáo Kiểm tra lại Dữ liệu, phân loại mức độ nghiêm trọng thành 3 mức: `INFO`, `WARNING`, `ERROR`.
4. Nếu tất cả đều ĐẠT, hệ thống hiển thị trạng thái hợp lệ và thông báo: "Dữ liệu hệ thống đạt 100% yêu cầu về liên kết dữ liệu và số dư khớp nhau."
5. Nếu phát hiện sai lệch, hệ thống đánh dấu đỏ, liệt kê chi tiết mã giao dịch, số tiền chênh lệch và hướng dẫn Người dùng biện pháp xử lý.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

Không có luồng ngoại lệ nghiệp vụ đặc thù bổ sung ngoài việc phân loại mức độ nghiêm trọng của sai lệch đã liệt kê ở luồng sự kiện chính.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc kiểm tra lại theo `FR-DAT-03`: Công cụ kiểm tra lại độc lập bảo đảm tự động kiểm tra 4 điều kiện toàn vẹn. Các điều kiện này gồm số dư ví, cân bằng nguồn - đích khi chuyển tiền, liên kết danh mục theo `BR-FIN-04` và liên kết phí chuyển tiền.
2. Yêu cầu chức năng liên quan: `FR-DAT-03`.
