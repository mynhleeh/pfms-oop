## 6. ĐẶC TẢ 20 CA SỬ DỤNG CHI TIẾT

20 ca sử dụng của Hệ thống Quản lý Tài chính Cá nhân (PFMS) mô hình hóa các tương tác giữa Người dùng và hệ thống, đáp ứng các yêu cầu chức năng và quy tắc nghiệp vụ tài chính theo 4 nguyên tắc thiết kế then chốt:

- Hệ thống phục vụ một người dùng duy nhất trên máy tính để bàn cục bộ.
- Mỗi ca sử dụng chỉ có một mục tiêu nghiệp vụ độc lập, không gộp thành cụm "Quản lý X" mơ hồ.
- Giao dịch tài chính nhiều bước được ghi nhận nguyên khối hoặc tự động hoàn nguyên khi có lỗi phát sinh.
- Phân biệt rõ biến thể thành công hợp lệ (luồng rẽ nhánh) với tình huống vi phạm dữ liệu (luồng ngoại lệ).

Quy ước chung: mọi ca sử dụng đều có tác nhân chính là Người dùng, phạm vi là hệ thống PFMS, cấp độ là mục tiêu người dùng; các thuộc tính này không lặp lại ở từng ca sử dụng. Thông báo lỗi được đặc tả chi tiết ở lần xuất hiện đầu tiên; các lần tiếp theo được dẫn chiếu theo mã lỗi chuẩn (`ERR-*`).

Mỗi ca sử dụng được đặc tả cấu trúc gồm 9 thành phần chuẩn hóa:

1. Mã và tên UC: `UC-<DOMAIN>-<NN>` và tên dạng động từ + danh từ.
2. Bảng thuộc tính: các bên liên quan và mối quan tâm, mô tả tóm tắt, điều kiện kích hoạt.
3. Tiền điều kiện: trạng thái hệ thống và dữ liệu cần có trước khi bắt đầu.
4. Bảo đảm tối thiểu: trạng thái được giữ nguyên khi UC thất bại hoặc bị hủy, không để lại bản ghi rác.
5. Hậu điều kiện thành công: trạng thái dữ liệu sau khi hoàn tất, bảo toàn các bất biến tài chính.
6. Luồng sự kiện chính: các bước đánh số từ lúc bắt đầu đến khi đạt mục tiêu.
7. Các luồng rẽ nhánh: biến thể hợp lệ dẫn tới thành công hoặc kết thúc hợp lệ.
8. Các luồng ngoại lệ: vi phạm dữ liệu hoặc điều kiện biên, nêu phản ứng hệ thống, mã `ERR-*` và điểm phục hồi.
9. Quy tắc nghiệp vụ đặc thù: liên kết tới `BR-*` và `FR-*` mà UC áp dụng.

Bảng phân bổ số lượng 20 ca sử dụng theo 6 nhóm chức năng nghiệp vụ của hệ thống:

| STT | Nhóm chức năng | Mã nhóm | Số lượng UC |
| :---: | :--- | :---: | :---: |
| 1 | Ví | `WAL` | 4 |
| 2 | Ghi nhận thu chi | `TXN` | 4 |
| 3 | Chuyển tiền nội bộ | `TRF` | 3 |
| 4 | Danh mục phân loại | `CAT` | 4 |
| 5 | Thống kê và báo cáo | `REP` | 2 |
| 6 | Lưu trữ và sao lưu dữ liệu | `DAT` | 3 |
|  | **Tổng** |  | **20** |

Bảng danh mục 20 ca sử dụng chi tiết cùng nhóm chức năng và yêu cầu chức năng (FR) ánh xạ tương ứng:

| STT | Mã UC | Tên ca sử dụng | Nhóm chức năng | FR ánh xạ |
| :---: | :---: | :--- | :--- | :--- |
| 1 | `UC-WAL-01` | Tạo ví mới | Ví | `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04` |
| 2 | `UC-WAL-02` | Theo dõi số dư tính lại từ lịch sử dòng tiền và cơ cấu ví | Ví | `FR-WAL-02`, `FR-WAL-04` |
| 3 | `UC-WAL-03` | Cập nhật thông tin ví | Ví | `FR-WAL-01`, `FR-WAL-03` |
| 4 | `UC-WAL-04` | Điều chỉnh số dư ví | Ví | `FR-WAL-05` |
| 5 | `UC-TXN-01` | Ghi nhận giao dịch thu chi | Ghi nhận thu chi | `FR-TXN-01`, `FR-TXN-02` |
| 6 | `UC-TXN-02` | Tra cứu và lọc lịch sử giao dịch thu chi | Ghi nhận thu chi | `FR-TXN-03` |
| 7 | `UC-TXN-03` | Chỉnh sửa giao dịch thu chi | Ghi nhận thu chi | `FR-TXN-04` |
| 8 | `UC-TXN-04` | Xóa giao dịch thu chi | Ghi nhận thu chi | `FR-TXN-05` |
| 9 | `UC-TRF-01` | Chuyển tiền nội bộ giữa hai ví | Chuyển tiền nội bộ | `FR-TRF-01` |
| 10 | `UC-TRF-02` | Tra cứu lịch sử chuyển tiền | Chuyển tiền nội bộ | `FR-TRF-02` |
| 11 | `UC-TRF-03` | Xóa giao dịch chuyển tiền | Chuyển tiền nội bộ | `FR-TRF-02` |
| 12 | `UC-CAT-01` | Bổ sung danh mục phân loại mới | Danh mục phân loại | `FR-CAT-01`, `FR-CAT-02` |
| 13 | `UC-CAT-02` | Đổi tên danh mục | Danh mục phân loại | `FR-CAT-02`, `FR-CAT-03` |
| 14 | `UC-CAT-03` | Khôi phục hoặc tạo lại mục đã xóa | Danh mục phân loại | `FR-CAT-02`, `FR-CAT-03` |
| 15 | `UC-CAT-04` | Xóa danh mục hoặc nhóm | Danh mục phân loại | `FR-CAT-03` |
| 16 | `UC-REP-01` | Thống kê dòng tiền 12 tháng gần nhất | Thống kê và báo cáo | `FR-REP-01` |
| 17 | `UC-REP-02` | Phân tích cơ cấu thu chi theo nhóm và danh mục | Thống kê và báo cáo | `FR-REP-02` |
| 18 | `UC-DAT-01` | Xuất dữ liệu tài chính | Lưu trữ và sao lưu dữ liệu | `FR-DAT-01` |
| 19 | `UC-DAT-02` | Phục hồi dữ liệu hệ thống từ bản sao lưu | Lưu trữ và sao lưu dữ liệu | `FR-DAT-02` |
| 20 | `UC-DAT-03` | Kiểm tra lại dữ liệu hệ thống | Lưu trữ và sao lưu dữ liệu | `FR-DAT-03` |

### 6.1 UC-WAL-01: Tạo ví mới

Bảng thuộc tính đặc tả mục tiêu, các bên liên quan và điều kiện kích hoạt của ca sử dụng Tạo ví mới (`UC-WAL-01`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần tạo tài khoản chứa tiền để ghi chép dòng tiền. Hệ thống: ví mới có số dư 0.00, tên hợp lệ và duy nhất theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`), số dư luôn đúng theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`). |
| Mô tả tóm tắt | Tạo ví mới với tên và ghi chú. Số dư ban đầu là 0.00 VNĐ; số tiền đang có được ghi nhận sau bằng khoản điều chỉnh số dư (`UC-WAL-04`). Hệ thống tính lại tổng số dư và tỷ lệ cơ cấu giữa các ví. |
| Điều kiện kích hoạt | Người dùng chọn chức năng thêm ví mới trên giao diện quản lý ví. |

#### Tiền điều kiện

1. Hệ thống đã khởi chạy, đọc tệp dữ liệu cục bộ thành công; Người dùng đang ở màn hình quản lý ví.
2. Danh sách ví đã được đọc (lần chạy đầu tiên chỉ có ví "Tiền mặt" có sẵn).

#### Bảo đảm tối thiểu

Nếu gặp lỗi kiểm tra dữ liệu hoặc lỗi ghi tệp, cơ sở dữ liệu và danh sách ví không thay đổi, không có ví rác nào được lưu.

#### Hậu điều kiện thành công

1. Ví mới được lưu với mã duy nhất, trạng thái `ACTIVE` và số dư 0.00 VNĐ.
2. Tổng số dư các ví (`FR-WAL-02`, quy tắc 3) và bảng cơ cấu số dư (`FR-WAL-04`) được cập nhật.

#### Luồng sự kiện chính

1. Người dùng chọn "Thêm ví mới".
2. Hệ thống hiển thị biểu mẫu gồm Tên ví, Ghi chú mô tả.
3. Người dùng nhập tên ví và ghi chú (nếu có).
4. Người dùng gửi yêu cầu lưu.
5. Hệ thống kiểm tra tên theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`): sai định dạng bị từ chối (`ERR-NAM-001`); trùng bị từ chối (`ERR-NAM-002`).
6. Hệ thống tạo ví mới với mã duy nhất, trạng thái `ACTIVE`, số dư 0.00 VNĐ.
7. Hệ thống tính lại tổng số dư các ví (`FR-WAL-02`, Quy tắc bảo toàn số dư ví `BR-FIN-02`) và tỷ lệ cơ cấu từng ví (`FR-WAL-04`).
8. Hệ thống lưu tệp dữ liệu, thông báo thành công và làm mới danh sách ví cùng bảng cơ cấu.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Trùng tên ví

1. Tại bước 5, hệ thống phát hiện tên trùng ví hiện có (`ERR-NAM-002`).
2. Hệ thống hủy thao tác ghi, giữ dữ liệu đã nhập, đánh dấu trường tên và thông báo: "Tên ví này đã tồn tại trong hệ thống. Vui lòng chọn một tên khác."
3. Quay lại bước 3 để Người dùng sửa tên hoặc hủy.

##### Luồng ngoại lệ 5b: Tên ví sai định dạng

1. Tại bước 5, hệ thống phát hiện tên sai định dạng (`ERR-NAM-001`).
2. Hệ thống hủy thao tác ghi, giữ dữ liệu đã nhập, đánh dấu trường tên và thông báo: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Quay lại bước 3.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): số dư ví tại mỗi thời điểm bằng tổng dòng tiền vào trừ tổng dòng tiền ra đến thời điểm đó; ví mới bắt đầu từ 0.00 VNĐ.
2. Yêu cầu chức năng liên quan: `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`.

### 6.2 UC-WAL-02: Theo dõi số dư tính lại từ lịch sử dòng tiền và cơ cấu ví

Bảng thuộc tính đặc tả mục tiêu nghiệp vụ và điều kiện kích hoạt của ca sử dụng Theo dõi số dư tính lại từ lịch sử dòng tiền và cơ cấu ví (`UC-WAL-02`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần biết số dư khả dụng thực tế của từng ví và tỷ lệ phân bổ tiền giữa các ví để quyết định chi tiêu hoặc điều chuyển vốn. |
| Mô tả tóm tắt | Tính số dư khả dụng theo thời gian thực của từng ví từ toàn bộ lịch sử dòng tiền, và phân tích tỷ lệ cơ cấu tiền giữa các ví đang hoạt động và các ví lưu trữ có số dư lớn hơn 0.00 VNĐ. |
| Điều kiện kích hoạt | Người dùng mở màn hình quản lý ví hoặc chuyển sang tab "Cơ cấu phân bổ ví". |

#### Tiền điều kiện

1. Hệ thống đã khởi chạy và đọc tệp dữ liệu thành công.
2. Người dùng đang ở màn hình quản lý ví.

#### Bảo đảm tối thiểu

Hệ thống chỉ đọc, không thay đổi bất kỳ dữ liệu nào trong tệp lưu trữ.

#### Hậu điều kiện thành công

1. Số dư từng ví được tính đúng theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`) tại thời điểm truy vấn.
2. Tỷ lệ cơ cấu hiển thị đúng theo `FR-WAL-04`.

#### Luồng sự kiện chính

1. Người dùng mở danh sách ví và chọn bộ lọc trạng thái (`ACTIVE` mặc định, `ARCHIVED` hoặc `ALL`).
2. Hệ thống quét dòng tiền của từng ví thỏa điều kiện lọc và tính lại số dư theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`):
   $$\footnotesize WalletBalance_w = \sum Income_w + \sum TransferIn_w - \sum Expense_w - \sum TransferOut_w + \sum Adjustment_w$$
3. Hệ thống tính tổng số dư hệ thống (`FR-WAL-02`, quy tắc 3) bằng số thập phân chính xác, gồm các ví `ACTIVE` và các ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ, không phụ thuộc bộ lọc hiển thị.
4. Hệ thống hiển thị bảng gồm Mã ví, Tên ví, Trạng thái, Số dư tính lại; chân bảng có tổng số dư toàn hệ thống.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Xem phân tích cơ cấu tiền trong ví

1. Tại bước 1, Người dùng chọn tab "Cơ cấu phân bổ ví".
2. Hệ thống tính tổng số dư các ví `ACTIVE` cộng các ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ.
3. Nếu tổng lớn hơn 0, hệ thống tính tỷ lệ từng ví:
   $$Rate_w = \left(\frac{WalletBalance_w}{TotalWalletBalance}\right) \times 100\%$$
   Phép tính dùng tối thiểu 4 chữ số thập phân, làm tròn ngân hàng, hiển thị 2 chữ số thập phân (ví dụ 35.50%).
4. Hệ thống sắp xếp theo tỷ lệ giảm dần và hiển thị bảng thống kê kèm biểu đồ.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3a: Tổng số dư các ví bằng 0

1. Tại bước 3 của luồng rẽ nhánh 1a, tổng số dư bằng 0.
2. Hệ thống bỏ qua phép chia cho 0 (`ERR-FIN-002`) và gán tỷ lệ 0.00% cho mọi ví.
3. Hệ thống hiển thị bảng kèm thông báo: "Tổng số dư các ví hiện tại bằng 0, không có cơ cấu phân bổ."

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): số dư hiển thị là số dư tính lại từ lịch sử dòng tiền, không cho phép gán trực tiếp.
2. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi phép cộng dồn và chia tỷ lệ dùng số thập phân chính xác.
3. Yêu cầu chức năng liên quan: `FR-WAL-02`, `FR-WAL-04`.

### 6.3 UC-WAL-03: Cập nhật thông tin ví

Bảng thuộc tính đặc tả phạm vi cập nhật thông tin và điều kiện kích hoạt của ca sử dụng Cập nhật thông tin ví (`UC-WAL-03`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần đổi tên, ghi chú, lưu trữ ví không dùng hoặc xóa ví tạo nhầm chưa có giao dịch. Hệ thống: cấm can thiệp trực tiếp vào số dư; các giao dịch liên kết vẫn trỏ đúng ví. |
| Mô tả tóm tắt | Sửa tên, ghi chú; chuyển trạng thái giữa `ACTIVE` và `ARCHIVED`; hoặc xóa vĩnh viễn ví chưa phát sinh giao dịch. Trường số dư bị khóa. Khi lưu trữ ví còn số dư, hệ thống cảnh báo. |
| Điều kiện kích hoạt | Người dùng chọn chỉnh sửa, lưu trữ hoặc xóa một ví trong danh sách. |

#### Tiền điều kiện

1. Ví cần xử lý đã tồn tại.
2. Người dùng đang ở màn hình quản lý ví.

#### Bảo đảm tối thiểu

Nếu thao tác bị hủy hoặc lỗi, thuộc tính ví giữ nguyên. Số dư ví không bao giờ thay đổi qua UC này.

#### Hậu điều kiện thành công

1. Tên, ghi chú hoặc trạng thái được cập nhật vào tệp dữ liệu; ví chưa có giao dịch được xóa hoàn toàn khi chọn xóa.
2. Số dư khả dụng của ví không đổi.
3. Ví `ARCHIVED` bị loại khỏi danh sách chọn cho giao dịch mới, nhưng lịch sử vẫn hiện trong báo cáo và số dư vẫn tính vào tổng số dư các ví.

#### Luồng sự kiện chính

1. Người dùng chọn chỉnh sửa một ví.
2. Hệ thống hiển thị biểu mẫu gồm Tên ví, Ghi chú mô tả, Trạng thái (`ACTIVE` hoặc `ARCHIVED`); trường Số dư chỉ đọc.
3. Người dùng sửa tên hoặc ghi chú.
4. Người dùng xác nhận lưu.
5. Hệ thống kiểm tra tên theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`) (so với các ví khác): sai định dạng `ERR-NAM-001`, trùng `ERR-NAM-002`.
6. Hệ thống cập nhật các trường vào đối tượng ví trong tệp dữ liệu.
7. Hệ thống thông báo thành công và làm mới giao diện.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Chuyển ví sang lưu trữ (`ARCHIVED`)

1. Tại bước 3, Người dùng đổi trạng thái từ `ACTIVE` sang `ARCHIVED`.
2. Nếu số dư ví khác 0 (theo Quy tắc bảo toàn số dư ví `BR-FIN-02`), hệ thống hiển thị cảnh báo: "Ví này hiện vẫn còn số dư khả dụng là [Số dư] VNĐ. Số dư này vẫn được tính vào tổng số dư trong các ví nhưng ví sẽ bị ẩn khỏi danh sách chọn khi tạo giao dịch mới. Bạn có chắc chắn muốn lưu trữ ví này không?"
3. Người dùng xác nhận.
4. Hệ thống cập nhật `ARCHIVED`, ẩn ví khỏi các danh sách chọn ví khi tạo giao dịch mới, giữ ví trong báo cáo lịch sử.
5. Tiếp tục tại bước 7 của luồng chính.

##### Luồng rẽ nhánh 3b: Xóa vĩnh viễn ví chưa phát sinh giao dịch

1. Người dùng chọn xóa một ví trên màn hình quản lý ví.
2. Nếu ví chưa có bản ghi nào trong sổ thu chi, sổ chuyển tiền và sổ điều chỉnh số dư, hệ thống hiển thị hộp thoại xác nhận xóa vĩnh viễn.
3. Người dùng xác nhận.
4. Hệ thống gỡ bản ghi ví khỏi bộ lưu trữ và giải phóng tên ví.
5. Hệ thống thông báo xóa thành công và làm mới giao diện.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Can thiệp trái phép vào số dư ví

1. Tại bước 2, hệ thống phát hiện yêu cầu chứa dữ liệu thay đổi số dư của ví đã có giao dịch.
2. Hệ thống từ chối, ghi nhật ký cảnh báo và thông báo: "Không được phép thay đổi trực tiếp số dư ví. Vui lòng sử dụng chức năng Điều chỉnh số dư hoặc Ghi nhận giao dịch."

##### Luồng ngoại lệ 5a: Trùng tên ví với ví khác

1. Tại bước 5, hệ thống phát hiện tên mới trùng ví khác (`ERR-NAM-002`).
2. Hệ thống hủy ghi, đánh dấu trường tên và thông báo: "Tên ví này đã tồn tại trong hệ thống. Vui lòng chọn một tên khác."
3. Quay lại bước 3.

##### Luồng ngoại lệ 5b: Tên ví mới sai định dạng

1. Tại bước 5, hệ thống phát hiện tên sai định dạng (`ERR-NAM-001`).
2. Hệ thống hủy ghi, đánh dấu trường tên và thông báo: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Quay lại bước 3.

##### Luồng ngoại lệ 3c: Lưu trữ ví tiền mặt có sẵn

1. Tại bước 3, trước khi xét luồng rẽ nhánh 3a, Người dùng đổi trạng thái ví "Tiền mặt" có sẵn sang `ARCHIVED`.
2. Hệ thống từ chối và thông báo: "Ví tiền mặt có sẵn không thể lưu trữ."
3. Quay lại bước 3.

##### Luồng ngoại lệ 3d: Xóa ví tiền mặt có sẵn

1. Tại bước 1 của luồng rẽ nhánh 3b, Người dùng chọn xóa ví "Tiền mặt" có sẵn.
2. Hệ thống từ chối và thông báo: "Ví tiền mặt có sẵn không thể xóa."

##### Luồng ngoại lệ 3e: Xóa ví đã có lịch sử dòng tiền

1. Tại bước 2 của luồng rẽ nhánh 3b, hệ thống phát hiện ví đã có giao dịch trong quá khứ.
2. Hệ thống từ chối và thông báo: "Ví đã có lịch sử dòng tiền nên không thể xóa vĩnh viễn. Vui lòng chuyển ví sang trạng thái lưu trữ."

#### Quy tắc nghiệp vụ đặc thù

1. Không xóa vật lý ví đã có bản ghi trong sổ thu chi, sổ chuyển tiền hoặc sổ điều chỉnh số dư; muốn ngừng dùng thì chuyển sang `ARCHIVED`. Ví chưa từng phát sinh giao dịch được xóa vĩnh viễn.
2. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): số dư ví chỉ thay đổi gián tiếp qua các nghiệp vụ có dòng tiền hợp lệ.
3. Yêu cầu chức năng liên quan: `FR-WAL-01`, `FR-WAL-03`.

### 6.4 UC-WAL-04: Điều chỉnh số dư ví

Bảng thuộc tính đặc tả mục tiêu đối soát chênh lệch và điều kiện kích hoạt của ca sử dụng Điều chỉnh số dư ví (`UC-WAL-04`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần đồng bộ số dư hệ thống với kiểm kê thực tế mà vẫn truy vết được chênh lệch. Hệ thống: bản ghi điều chỉnh không được sửa; chênh lệch được ghi thành khoản điều chỉnh độc lập. |
| Mô tả tóm tắt | Ghi nhận khoản điều chỉnh số dư có dấu vết khi số dư thực tế lệch số dư hệ thống. Hệ thống tính chênh lệch, hiển thị bản xem trước và cập nhật số dư tính lại mà không động đến các giao dịch cũ. |
| Điều kiện kích hoạt | Người dùng phát hiện số dư thực tế khác số dư hệ thống và chọn "Điều chỉnh số dư". |

#### Tiền điều kiện

1. Ví cần điều chỉnh tồn tại và đang `ACTIVE`.
2. Người dùng đang ở màn hình quản lý ví.

#### Bảo đảm tối thiểu

Nếu Người dùng hủy hoặc ghi lỗi, số dư tính lại giữ nguyên, không lưu khoản điều chỉnh rác.

#### Hậu điều kiện thành công

1. Khoản điều chỉnh mới được lưu và không sửa được về sau, gồm: mã khoản điều chỉnh, mã ví, ngày ghi nhận, số dư trước, số dư quan sát, chênh lệch, lý do (nếu có).
2. Số dư tính lại của ví bằng đúng số dư quan sát.
3. Quy tắc bảo toàn số dư ví (`BR-FIN-02`) được thỏa mãn nhờ số hạng điều chỉnh trong công thức số dư.

#### Luồng sự kiện chính

1. Người dùng chọn một ví đang hoạt động và nhấn "Điều chỉnh số dư".
2. Hệ thống tính số dư hiện tại theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`) và hiển thị biểu mẫu gồm Số dư hiện tại (chỉ đọc), Số dư thực tế quan sát, Lý do điều chỉnh (tùy chọn).
3. Người dùng nhập số dư thực tế và lý do (tùy chọn, tối đa 255 ký tự).
4. Người dùng yêu cầu xem trước kết quả.
5. Hệ thống tính chênh lệch:
   $$Adjustment_w = ObservedBalance_w - WalletBalance_w(\text{trước})$$
   Hệ thống kiểm tra chênh lệch khác 0, lý do không quá 255 ký tự, số dư quan sát không âm.
6. Hệ thống hiển thị hộp thoại xác nhận gồm số dư trước, số dư sau, chênh lệch (dương hoặc âm) và lý do.
7. Người dùng nhấn "Xác nhận điều chỉnh".
8. Hệ thống lưu bản ghi điều chỉnh, cập nhật số dư tính lại của ví và thông báo thành công.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Lý do vượt độ dài tối đa

1. Tại bước 5, lý do dài hơn 255 ký tự sau khi cắt khoảng trắng hai đầu.
2. Hệ thống chặn, đánh dấu trường lý do và thông báo: "Lý do điều chỉnh không được vượt quá 255 ký tự."
3. Quay lại bước 3.

##### Luồng ngoại lệ 5b: Số dư quan sát trùng số dư hệ thống

1. Tại bước 5, chênh lệch bằng 0.
2. Hệ thống từ chối tạo khoản ghi rỗng và thông báo: "Số dư nhập vào trùng với số dư hệ thống đang ghi nhận, không có chênh lệch cần điều chỉnh."
3. Quay lại bước 3.

##### Luồng ngoại lệ 5c: Số dư quan sát âm

1. Tại bước 5, số dư quan sát nhỏ hơn 0.
2. Hệ thống từ chối (`ERR-WAL-001`) và thông báo: "Số dư thực tế nhập vào không hợp lệ (số dư ví không được nhỏ hơn 0)."
3. Quay lại bước 3.

#### Quy tắc nghiệp vụ đặc thù

1. Khoản điều chỉnh là một số hạng độc lập trong công thức Quy tắc bảo toàn số dư ví (`BR-FIN-02`), không sửa hay xóa giao dịch dòng tiền trong quá khứ.
2. Bản ghi điều chỉnh không được sửa hoặc xóa sau khi ghi nhận, để bảo đảm dấu vết kiểm tra.
3. Yêu cầu chức năng liên quan: `FR-WAL-05`.

### 6.5 UC-TXN-01: Ghi nhận giao dịch thu chi

Bảng thuộc tính đặc tả mục tiêu ghi nhận thu chi và điều kiện kích hoạt của ca sử dụng Ghi nhận giao dịch thu chi (`UC-TXN-01`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần ghi chính xác thu chi hằng ngày gắn với danh mục và nguồn tiền, kể cả chi phí giao dịch. Hệ thống: cập nhật đồng thời số dư ví, không để ví âm, phân loại theo danh mục 3 cấp. |
| Mô tả tóm tắt | Ghi một giao dịch thu nhập hoặc chi tiêu, phân loại theo danh mục 3 cấp (Loại thu chi, Nhóm nhu cầu, Danh mục), liên kết với ví và cập nhật số dư ví. Nếu có chi phí, hệ thống ghi riêng thành một phí giao dịch liên kết. |
| Điều kiện kích hoạt | Người dùng chọn thêm giao dịch mới trên màn hình quản lý giao dịch thu chi. |

#### Tiền điều kiện

1. Hệ thống có ít nhất một ví `ACTIVE`.
2. Cấu trúc danh mục 3 cấp đã được đọc.
3. Người dùng đang ở màn hình quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu giao dịch bị hủy hoặc lỗi, số dư ví không đổi và không lưu bản ghi giao dịch hay phí giao dịch rác.

#### Hậu điều kiện thành công

1. Bản ghi giao dịch được lưu với mã duy nhất; nếu chi phí lớn hơn 0, phí giao dịch liên kết được lưu cùng lúc.
2. Số dư ví được cập nhật ngay theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`) (`EXPENSE`: trừ tiền; `INCOME`: cộng tiền; trừ thêm chi phí nếu có) và không âm.
3. Chỉ số tổng hợp dòng tiền tháng và báo cáo cơ cấu thu chi được cập nhật.

#### Luồng sự kiện chính

1. Người dùng chọn "Thêm giao dịch mới".
2. Hệ thống hiển thị biểu mẫu gồm Ngày giao dịch (mặc định ngày hiện tại), Loại giao dịch (`INCOME` hoặc `EXPENSE`), Ví thanh toán, Danh mục chi tiết, Số tiền, Chi phí (tùy chọn), Danh mục phí (khi có chi phí), Ghi chú mô tả.
3. Người dùng chọn Loại giao dịch là `EXPENSE`.
4. Hệ thống tải các nhóm nhu cầu và danh mục chi tiết đang dùng thuộc loại `EXPENSE` theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`).
5. Người dùng chọn ngày, ví `ACTIVE`, danh mục chi tiết, nhập số tiền (lớn hơn 0), chi phí (không âm, mặc định 0.00), danh mục phí khi chi phí lớn hơn 0 và ghi chú.
6. Người dùng xác nhận lưu.
7. Hệ thống kiểm tra:
   - Số tiền lớn hơn 0, chuẩn hóa thành số thập phân chính xác 2 chữ số theo Quy tắc độ chính xác tài chính (`BR-FIN-01`).
   - Danh mục đang dùng và thuộc loại `EXPENSE` theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`).
   - Chi phí không âm; khi chi phí lớn hơn 0 phải có danh mục phí là danh mục `EXPENSE` đang dùng.
   - Số dư khả dụng sau khi trừ số tiền và chi phí không âm theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`).
8. Hệ thống tạo bản ghi giao dịch với mã duy nhất; nếu chi phí lớn hơn 0, tạo thêm phí giao dịch liên kết.
9. Hệ thống trừ số tiền và chi phí (nếu có) khỏi số dư ví theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`).
10. Hệ thống cập nhật các chỉ số tổng hợp của tháng quan sát.
11. Hệ thống lưu tệp dữ liệu, thông báo thành công và làm mới lịch sử giao dịch.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Ghi nhận giao dịch thu nhập (`INCOME`)

1. Tại bước 3, Người dùng chọn Loại giao dịch là `INCOME`.
2. Hệ thống tải các nhóm và danh mục đang dùng thuộc loại `INCOME`; khi có chi phí, tải thêm danh mục `EXPENSE` đang dùng để chọn danh mục phí.
3. Người dùng chọn ví nhận tiền, danh mục thu nhập tương ứng, nhập số tiền (lớn hơn 0), chi phí (tùy chọn), danh mục phí (khi chi phí lớn hơn 0) và ghi chú.
4. Tại bước 7, hệ thống kiểm tra danh mục thuộc `INCOME`, số tiền lớn hơn 0 và số dư ví sau khi cộng số tiền, trừ chi phí không âm. Nếu không thỏa, chuyển sang luồng ngoại lệ 7b.
5. Tại bước 9, hệ thống cộng số tiền vào ví nhận và trừ chi phí (nếu có).
6. Tiếp tục tại bước 10 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Loại giao dịch chưa có danh mục chi tiết đang dùng

1. Tại bước 4 (hoặc bước 2 của luồng rẽ nhánh 3a), loại giao dịch được chọn chưa có danh mục chi tiết đang dùng.
2. Hệ thống không cho tiếp tục và thông báo: "Loại giao dịch này chưa có danh mục chi tiết đang dùng. Hãy thêm danh mục trước khi ghi nhận giao dịch."
3. Hệ thống đề nghị mở màn hình quản lý danh mục để bổ sung theo `UC-CAT-01`. Nếu Người dùng đồng ý, hệ thống mở màn hình đó và giao dịch đang nhập không được lưu.
4. Nếu Người dùng từ chối, UC kết thúc, không lưu giao dịch, không đổi số dư.

##### Luồng ngoại lệ 7a: Số tiền không hợp lệ

1. Tại bước 7, số tiền sai định dạng số học hoặc nhỏ hơn hoặc bằng 0.
2. Hệ thống từ chối và đánh dấu trường số tiền: sai định dạng thì báo `ERR-FIN-001`; nhỏ hơn hoặc bằng 0 thì báo số tiền giao dịch phải lớn hơn 0.
3. Quay lại bước 5.

##### Luồng ngoại lệ 7b: Số dư ví không đủ

1. Tại bước 7, số dư khả dụng không đủ: với chi tiêu, số dư nhỏ hơn tổng số tiền và chi phí; với thu nhập có chi phí, số dư sau khi cộng số tiền và trừ chi phí nhỏ hơn 0.
2. Hệ thống từ chối (`ERR-WAL-001`); Người dùng cần chọn ví khác hoặc giảm số tiền hoặc chi phí.
3. Quay lại bước 5.

##### Luồng ngoại lệ 7c: Danh mục đã xóa hoặc không khớp loại giao dịch

1. Tại bước 7, danh mục đã xóa hoặc có Loại thu chi không khớp loại giao dịch.
2. Hệ thống chặn và thông báo: "Danh mục không hợp lệ hoặc không tương thích với Loại giao dịch đã chọn."
3. Quay lại bước 5.

##### Luồng ngoại lệ 7d: Chi phí không hợp lệ hoặc thiếu danh mục phí

1. Tại bước 7, chi phí nhỏ hơn 0; hoặc chi phí lớn hơn 0 nhưng chưa chọn danh mục phí; hoặc danh mục phí đã xóa hoặc không thuộc `EXPENSE`.
2. Hệ thống từ chối: chi phí âm báo `ERR-FEE-001`; thiếu danh mục phí báo `ERR-FEE-002`; danh mục phí đã xóa hoặc sai loại báo `ERR-FEE-003`.
3. Quay lại bước 5.

##### Luồng ngoại lệ 7e: Ví không ở trạng thái hoạt động

1. Tại bước 7, ví được chọn không tồn tại hoặc đang `ARCHIVED`.
2. Hệ thống từ chối và thông báo: "Ví được chọn không ở trạng thái hoạt động hoặc không tồn tại."
3. Quay lại bước 5 để chọn ví `ACTIVE` khác.

##### Luồng ngoại lệ 7f: Chưa có danh mục chi tiêu để chọn cho phí giao dịch

1. Tại bước 7, chi phí lớn hơn 0 nhưng loại `EXPENSE` chưa có danh mục chi tiết đang dùng nào. Luồng này được kiểm tra trước luồng ngoại lệ 7d.
2. Hệ thống từ chối và thông báo: "Chưa có danh mục chi tiêu nào để chọn cho phí giao dịch. Hãy đặt chi phí bằng 0 hoặc thêm danh mục chi tiêu trước."
3. Hệ thống đề nghị mở màn hình quản lý danh mục để bổ sung theo `UC-CAT-01`. Nếu Người dùng đồng ý, hệ thống mở màn hình đó và giao dịch đang nhập không được lưu.
4. Nếu Người dùng từ chối, quay lại bước 5 để đặt chi phí bằng 0.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): giao dịch phản ánh vào số dư ví cùng lúc với việc ghi giao dịch và phí liên kết; nếu một trong hai thất bại thì cả hai đều không có hiệu lực.
2. Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): mọi giao dịch phân loại theo cấu trúc 3 cấp, Loại thu chi luôn nhất quán.
3. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi phép toán dùng số thập phân chính xác 2 chữ số, không làm tròn nhị phân.
4. Phí giao dịch là giao dịch thu chi loại `EXPENSE` liên kết với giao dịch phát sinh ra nó; hai bản ghi cùng có hiệu lực hoặc cùng không.
5. Yêu cầu chức năng liên quan: `FR-TXN-01`, `FR-TXN-02`.

### 6.6 UC-TXN-02: Tra cứu và lọc lịch sử giao dịch thu chi

Bảng thuộc tính đặc tả phạm vi tra cứu, lọc giao dịch và điều kiện kích hoạt của ca sử dụng Tra cứu và lọc lịch sử giao dịch thu chi (`UC-TXN-02`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần tra cứu, đối chiếu các khoản thu chi theo nhiều tiêu chí kết hợp để kiểm soát dòng tiền. |
| Mô tả tóm tắt | Tìm kiếm, lọc và phân trang lịch sử giao dịch theo khoảng thời gian, ví, loại giao dịch, danh mục, từ khóa ghi chú. Hệ thống tính tổng thu, tổng chi, dòng tiền thuần trên tập kết quả; phí giao dịch hiển thị thành dòng riêng kèm nhãn liên kết và được tính vào tổng chi. |
| Điều kiện kích hoạt | Người dùng mở màn hình lịch sử giao dịch hoặc áp dụng bộ lọc. |

#### Tiền điều kiện

1. Hệ thống đã đọc tệp dữ liệu thành công.
2. Người dùng đang ở màn hình quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Truy vấn chỉ đọc, không thay đổi giao dịch hay số dư ví.

#### Hậu điều kiện thành công

1. Danh sách giao dịch khớp đúng tập kết quả lọc (các tiêu chí kết hợp đồng thời).
2. Tổng thu nhập, tổng chi tiêu, dòng tiền thuần phản ánh đúng tập kết quả theo Quy tắc độ chính xác tài chính (`BR-FIN-01`).

#### Luồng sự kiện chính

1. Người dùng mở "Lịch sử giao dịch thu chi" và chọn tiêu chí lọc:
   - Khoảng thời gian (từ ngày, đến ngày).
   - Phạm vi ví: một hoặc nhiều ví, hoặc toàn bộ.
   - Loại giao dịch: `ALL`, `INCOME` hoặc `EXPENSE`.
   - Nhóm nhu cầu hoặc danh mục chi tiết; danh sách gồm cả mục đã xóa kèm nhãn "(đã xóa)" để tra cứu giao dịch cũ.
   - Từ khóa tìm trong ghi chú.
2. Người dùng nhấn "Áp dụng bộ lọc".
3. Hệ thống áp dụng đồng thời mọi tiêu chí, sắp xếp theo ngày giao dịch giảm dần và phân trang (mặc định 50 bản ghi mỗi trang).
4. Hệ thống tính trên toàn bộ tập kết quả (2 chữ số thập phân theo Quy tắc độ chính xác tài chính `BR-FIN-01`): tổng thu nhập; tổng chi tiêu; dòng tiền thuần bằng tổng thu trừ tổng chi.
5. Hệ thống hiển thị bảng giao dịch, thông tin phân trang và khối tổng hợp. Danh mục đã xóa có nhãn "(đã xóa)"; phí giao dịch hiển thị thành dòng riêng với nhãn "phí của giao dịch [mã giao dịch phát sinh]".

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Khoảng ngày không hợp lệ

1. Tại bước 2, ngày bắt đầu sau ngày kết thúc.
2. Hệ thống từ chối lọc và thông báo: "Khoảng thời gian tra cứu không hợp lệ (ngày bắt đầu phải trước hoặc bằng ngày kết thúc)."
3. Quay lại bước 1.

##### Luồng ngoại lệ 3a: Không có bản ghi thỏa điều kiện lọc

1. Tại bước 3, không có giao dịch nào thỏa đồng thời các tiêu chí.
2. Hệ thống hiển thị bảng rỗng, tổng thu, tổng chi, dòng tiền thuần bằng 0.00 và thông báo: "Không có giao dịch nào thỏa mãn bộ lọc đã chọn."

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi phép tổng hợp dùng số thập phân chính xác.
2. Phí giao dịch là giao dịch `EXPENSE` nên được lọc, tính vào tổng chi và phân trang như mọi giao dịch thu chi.
3. Yêu cầu chức năng liên quan: `FR-TXN-03`.

### 6.7 UC-TXN-03: Chỉnh sửa giao dịch thu chi

Bảng thuộc tính đặc tả cơ chế hoàn tác và cập nhật thông tin của ca sử dụng Chỉnh sửa giao dịch thu chi (`UC-TXN-03`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần sửa số tiền, chi phí, ví, danh mục, ghi chú mà không làm sai số dư. Hệ thống: chỉ ghi nhận khi cả hoàn nguyên giao dịch cũ và áp dụng dữ liệu mới đều thành công, giữ Quy tắc bảo toàn số dư ví (`BR-FIN-02`) luôn đúng. |
| Mô tả tóm tắt | Sửa một giao dịch thu chi hiện có: hoàn nguyên số dư ví cũ và áp dụng cho ví mới trong cùng một lần xử lý, kèm cập nhật phí giao dịch liên kết (nếu có); ngăn ví bị âm. |
| Điều kiện kích hoạt | Người dùng chọn chỉnh sửa tại một dòng giao dịch. |

#### Tiền điều kiện

1. Giao dịch cần sửa tồn tại trong sổ giao dịch thu chi.
2. Người dùng đang ở màn hình quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu thất bại ở bất kỳ bước kiểm tra nào, giao dịch cũ và số dư các ví liên quan giữ nguyên; toàn bộ thao tác được hoàn nguyên.

#### Hậu điều kiện thành công

1. Giao dịch và phí liên kết (nếu có) được cập nhật.
2. Số dư ví cũ và ví mới đồng bộ đúng theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`) và không âm.

#### Luồng sự kiện chính

1. Người dùng chọn chỉnh sửa một giao dịch từ lịch sử.
2. Hệ thống tải dữ liệu hiện tại lên biểu mẫu (Ngày giao dịch, Loại giao dịch, Ví, Danh mục chi tiết, Số tiền, Chi phí, Danh mục phí, Ghi chú).
3. Người dùng sửa các trường cần thiết và nhấn "Lưu thay đổi".
4. Hệ thống kiểm tra: số tiền mới lớn hơn 0, chi phí mới không âm, danh mục tương thích loại giao dịch theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`). Nếu đổi danh mục thì danh mục mới phải đang dùng; được giữ danh mục cũ kể cả khi đã xóa. Nếu chi phí mới lớn hơn 0 thì cần danh mục phí `EXPENSE` đang dùng; được giữ danh mục phí cũ kể cả khi đã xóa.
5. Hệ thống tính thử hoàn nguyên ví cũ theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`): giao dịch cũ `INCOME` thì trừ số tiền cũ, cộng lại chi phí cũ và kiểm tra số dư không âm; giao dịch cũ `EXPENSE` thì hoàn lại số tiền cũ và chi phí cũ.
6. Hệ thống tính thử áp dụng vào ví mới theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`), trên số dư sau khi hoàn nguyên ví cũ: `EXPENSE` thì số dư phải đủ trừ số tiền mới và chi phí mới; `INCOME` thì số dư sau khi cộng số tiền mới, trừ chi phí mới không âm.
7. Hệ thống thực hiện, chỉ ghi nhận khi tất cả thành công: hoàn nguyên số dư ví cũ; cập nhật số dư ví mới; ghi đè thông tin vào giao dịch; xử lý phí liên kết (xóa nếu chi phí mới bằng 0 và đang có phí; tạo mới nếu chi phí mới lớn hơn 0 và chưa có phí; cập nhật nếu cả hai lớn hơn 0).
8. Hệ thống lưu tệp dữ liệu, thông báo thành công và làm mới lịch sử.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 1a: Sửa trực tiếp phí giao dịch

1. Tại bước 1, Người dùng chọn sửa tại một dòng phí giao dịch.
2. Hệ thống từ chối và thông báo: "Phí giao dịch không được sửa trực tiếp. Vui lòng sửa giao dịch thu chi phát sinh ra phí này, hoặc xóa giao dịch chuyển tiền rồi tạo lại nếu phí thuộc giao dịch chuyển tiền."
3. Luồng kết thúc, dữ liệu giữ nguyên.

##### Luồng ngoại lệ 1b: Không tìm thấy giao dịch

1. Tại bước 1, hệ thống không tìm thấy giao dịch theo mã.
2. Hệ thống thông báo: "Không tìm thấy giao dịch yêu cầu."
3. Luồng kết thúc, dữ liệu giữ nguyên.

##### Luồng ngoại lệ 4a: Số tiền mới không hợp lệ

1. Tại bước 4, số tiền sai định dạng số học hoặc nhỏ hơn hoặc bằng 0.
2. Hệ thống từ chối: sai định dạng báo `ERR-FIN-001`; nhỏ hơn hoặc bằng 0 thì báo số tiền giao dịch phải lớn hơn 0.
3. Quay lại bước 3.

##### Luồng ngoại lệ 4b: Chi phí không hợp lệ hoặc thiếu danh mục phí

1. Tại bước 4, chi phí mới nhỏ hơn 0; hoặc chi phí mới lớn hơn 0, giao dịch chưa có phí và chưa chọn danh mục phí; hoặc danh mục phí mới đã xóa hoặc không thuộc `EXPENSE`.
2. Hệ thống từ chối: `ERR-FEE-001`, `ERR-FEE-002` hoặc `ERR-FEE-003` tương ứng.
3. Quay lại bước 3.

##### Luồng ngoại lệ 4c: Ví mới không ở trạng thái hoạt động

1. Tại bước 4, ví mới không tồn tại hoặc đang `ARCHIVED`.
2. Hệ thống từ chối và thông báo: "Ví được chọn không ở trạng thái hoạt động hoặc không tồn tại."
3. Quay lại bước 3.

##### Luồng ngoại lệ 4d: Loại danh mục không khớp loại giao dịch gốc

1. Tại bước 4, Người dùng đổi sang danh mục có Loại thu chi khác với giao dịch gốc.
2. Hệ thống từ chối và thông báo: "Loại danh mục không khớp với loại giao dịch."
3. Quay lại bước 3.

##### Luồng ngoại lệ 4e: Chưa có danh mục chi tiêu để chọn cho phí giao dịch

1. Tại bước 4, chi phí mới lớn hơn 0, giao dịch chưa có phí, nhưng loại `EXPENSE` chưa có danh mục chi tiết đang dùng nào. Luồng này được kiểm tra trước luồng 4b.
2. Hệ thống xử lý như luồng ngoại lệ 7f của `UC-TXN-01` (thông báo, đề nghị mở quản lý danh mục theo `UC-CAT-01`).
3. Nếu Người dùng từ chối, quay lại bước 3 để đặt chi phí bằng 0.

##### Luồng ngoại lệ 5a: Hoàn nguyên khoản thu làm âm số dư ví cũ

1. Tại bước 5, việc trừ số tiền cũ và cộng lại chi phí cũ (nếu có) làm số dư ví cũ âm.
2. Hệ thống từ chối (`ERR-WAL-001`); Người dùng cần kiểm tra các khoản chi liên quan của ví cũ trước khi sửa.
3. Quay lại bước 3 hoặc Người dùng hủy thao tác.

##### Luồng ngoại lệ 6a: Số dư ví mới không đủ cho khoản chi mới

1. Tại bước 6, số dư ví mới (sau khi hoàn nguyên ví cũ) không đủ trừ số tiền mới và chi phí mới.
2. Hệ thống từ chối (`ERR-WAL-001`); Người dùng chọn ví khác hoặc giảm số tiền.
3. Quay lại bước 3.

##### Luồng ngoại lệ 6b: Số dư ví mới không đủ khi chi phí lớn hơn số tiền thu nhập mới

1. Tại bước 6, giao dịch mới là thu nhập và số dư ví mới sau khi cộng số tiền mới, trừ chi phí mới nhỏ hơn 0.
2. Hệ thống từ chối (`ERR-WAL-001`); Người dùng chọn ví khác hoặc giảm chi phí.
3. Quay lại bước 3.

#### Quy tắc nghiệp vụ đặc thù

1. Hoàn nguyên ví cũ, ghi nhận ví mới và cập nhật phí liên kết phải hoàn tất cùng lúc; nếu lỗi lưu trữ, toàn bộ trạng thái tự khôi phục về trước khi sửa.
2. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): số dư ví không được âm tại bất kỳ thời điểm nào của quá trình chuyển đổi.
3. Phí giao dịch chỉ được sửa cùng giao dịch phát sinh ra nó, không sửa trực tiếp.
4. Yêu cầu chức năng liên quan: `FR-TXN-04`.

### 6.8 UC-TXN-04: Xóa giao dịch thu chi

Bảng thuộc tính đặc tả quy trình hoàn nguyên dòng tiền và điều kiện kích hoạt của ca sử dụng Xóa giao dịch thu chi (`UC-TXN-04`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần loại bỏ giao dịch ghi sai hoặc đã hủy. Hệ thống: hoàn nguyên đúng số dư ví (gồm cả chi phí nếu có phí liên kết) và ngăn ví âm do xóa khoản thu đã được chi tiêu ở các giao dịch sau. |
| Mô tả tóm tắt | Xóa hoàn toàn một giao dịch thu chi cùng phí liên kết (nếu có), tự động hoàn nguyên số dư ví và cập nhật các chỉ số thống kê dòng tiền. |
| Điều kiện kích hoạt | Người dùng chọn xóa tại một dòng giao dịch. |

#### Tiền điều kiện

1. Giao dịch cần xóa tồn tại.
2. Người dùng đang ở màn hình quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu bị từ chối do vi phạm điều kiện số dư hoặc Người dùng hủy xác nhận, giao dịch, phí liên kết và số dư ví giữ nguyên.

#### Hậu điều kiện thành công

1. Giao dịch và phí liên kết (nếu có) bị xóa khỏi sổ giao dịch thu chi.
2. Số dư ví được hoàn nguyên đúng theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`): xóa chi tiêu thì cộng hoàn trả số tiền và chi phí; xóa thu nhập thì trừ số tiền và cộng hoàn trả chi phí.
3. Báo cáo thống kê tháng và số dư ví được cập nhật ngay.

#### Luồng sự kiện chính

1. Người dùng chọn xóa một giao dịch từ lịch sử.
2. Hệ thống hiển thị hộp thoại xác nhận gồm Ngày giao dịch, Danh mục, Số tiền, Chi phí (nếu có), Ví và tác động hoàn nguyên số dư dự kiến.
3. Người dùng nhấn "Xác nhận xóa".
4. Hệ thống kiểm tra điều kiện số dư theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`): giao dịch `EXPENSE` thì ví được cộng hoàn trả nên không có nguy cơ âm; giao dịch `INCOME` thì số dư khả dụng hiện tại sau khi trừ số tiền và cộng lại chi phí phải không âm.
5. Hệ thống thực hiện, chỉ ghi nhận khi tất cả thành công: hoàn nguyên số dư ví; xóa giao dịch và phí liên kết (nếu có) khỏi sổ.
6. Hệ thống cập nhật các chỉ số tổng hợp của tháng quan sát.
7. Hệ thống lưu tệp dữ liệu, thông báo thành công và làm mới lịch sử.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 1a: Xóa trực tiếp phí giao dịch

1. Tại bước 1, Người dùng chọn xóa tại một dòng phí giao dịch.
2. Hệ thống từ chối và thông báo: "Phí giao dịch không được xóa trực tiếp. Vui lòng xóa giao dịch thu chi phát sinh ra phí này, hoặc xóa giao dịch chuyển tiền nếu phí thuộc giao dịch chuyển tiền."
3. Luồng kết thúc, dữ liệu giữ nguyên.

##### Luồng ngoại lệ 1b: Không tìm thấy giao dịch

1. Tại bước 1, hệ thống không tìm thấy giao dịch theo mã.
2. Hệ thống thông báo: "Không tìm thấy giao dịch yêu cầu."
3. Luồng kết thúc, dữ liệu giữ nguyên.

##### Luồng ngoại lệ 3a: Người dùng hủy xóa

1. Tại bước 3, Người dùng chọn "Hủy" hoặc đóng hộp thoại.
2. Hệ thống đóng hộp thoại, giữ nguyên giao dịch và số dư các ví.
3. Luồng kết thúc.

##### Luồng ngoại lệ 4a: Xóa khoản thu làm âm số dư ví

1. Tại bước 4, giao dịch là `INCOME` và số dư khả dụng hiện tại (sau khi cộng lại chi phí) nhỏ hơn số tiền giao dịch, vì tiền đã được dùng ở các giao dịch chi tiêu hoặc chuyển tiền sau đó.
2. Hệ thống hủy xóa (`ERR-WAL-001`); Người dùng cần kiểm tra các khoản chi liên quan của ví trước khi thực hiện.
3. Luồng kết thúc, dữ liệu giữ nguyên.

#### Quy tắc nghiệp vụ đặc thù

1. Xóa bản ghi (kể cả phí liên kết) và hoàn nguyên số dư diễn ra đồng thời; nếu lỗi lưu trữ, hệ thống tự khôi phục về trước khi xóa.
2. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): không cho xóa khoản thu nếu làm số dư khả dụng của ví âm.
3. Phí giao dịch bị xóa cùng giao dịch phát sinh ra nó, không xóa trực tiếp.
4. Yêu cầu chức năng liên quan: `FR-TXN-05`.

### 6.9 UC-TRF-01: Chuyển tiền nội bộ giữa hai ví

Bảng thuộc tính đặc tả cơ chế ghi nhận đồng thời hai ví và điều kiện kích hoạt của ca sử dụng Chuyển tiền nội bộ giữa hai ví (`UC-TRF-01`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần chuyển tiền giữa các tài khoản nội bộ (tiền mặt, ví điện tử) và ghi đúng phí. Hệ thống: ghi nhận đồng thời (trừ ví nguồn, cộng ví đích); tổng số dư không đổi nếu không có phí, hoặc giảm đúng bằng phí theo Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`). |
| Mô tả tóm tắt | Điều chuyển tiền giữa hai ví nội bộ theo cơ chế ghi nhận đồng thời. Chi phí chuyển tiền (nếu có) được tách thành một phí giao dịch độc lập liên kết với giao dịch chuyển tiền. |
| Điều kiện kích hoạt | Người dùng chọn chức năng chuyển tiền nội bộ. |

#### Tiền điều kiện

1. Hệ thống có ít nhất hai ví `ACTIVE`.
2. Ví nguồn có số dư khả dụng lớn hơn 0.
3. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Nếu lỗi ở bất kỳ bước nào (ví dụ đã trừ ví nguồn nhưng cộng ví đích thất bại), toàn bộ giao dịch bị hủy ngay, số dư hai ví giữ nguyên.

#### Hậu điều kiện thành công

1. Giao dịch chuyển tiền được lưu với mã duy nhất.
2. Ví nguồn giảm đúng tổng số tiền chuyển và phí; ví đích tăng đúng số tiền chuyển, cùng lúc.
3. Nếu phí lớn hơn 0, một phí giao dịch độc lập được ghi vào sổ thu chi, liên kết với mã giao dịch chuyển tiền.
4. Tổng số dư các ví giảm đúng bằng phí; không có phí thì tổng giữ nguyên theo Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`).

#### Luồng sự kiện chính

1. Người dùng chọn "Chuyển tiền nội bộ".
2. Hệ thống hiển thị biểu mẫu gồm Ngày chuyển (mặc định ngày hiện tại), Ví nguồn, Ví đích, Số tiền chuyển, Phí chuyển tiền (mặc định 0), Danh mục phí (bắt buộc khi phí lớn hơn 0, không có mặc định), Ghi chú mô tả.
3. Người dùng chọn Ví nguồn từ các ví `ACTIVE`; hệ thống hiển thị số dư khả dụng hiện tại của ví nguồn.
4. Người dùng chọn Ví đích từ các ví `ACTIVE`.
5. Người dùng nhập số tiền chuyển (lớn hơn 0).
6. Người dùng nhập phí chuyển tiền (không âm) nếu có; nếu phí lớn hơn 0, chọn một danh mục `EXPENSE` đang dùng cho phí.
7. Người dùng nhập ghi chú (nếu có) và nhấn "Thực hiện chuyển tiền".
8. Hệ thống kiểm tra:
   - Ví nguồn khác ví đích.
   - Số tiền chuyển lớn hơn 0 và phí không âm, chuẩn hóa số thập phân chính xác 2 chữ số theo Quy tắc độ chính xác tài chính (`BR-FIN-01`).
   - Số dư ví nguồn không nhỏ hơn tổng số tiền chuyển và phí theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`).
   - Nếu phí lớn hơn 0: đã chọn danh mục phí, đang dùng và thuộc `EXPENSE`.
9. Hệ thống ghi nhận đồng thời theo Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): trừ số tiền chuyển khỏi ví nguồn; cộng số tiền chuyển vào ví đích; lưu giao dịch chuyển tiền với mã duy nhất.
10. Nếu phí lớn hơn 0, hệ thống sinh một phí giao dịch loại `EXPENSE` trong sổ thu chi, số tiền bằng phí, mang danh mục phí đã chọn và mã liên kết tới giao dịch chuyển tiền; khoản phí được trừ tiếp khỏi ví nguồn.
11. Hệ thống lưu tệp dữ liệu và thông báo thành công, kèm số tiền chuyển, phí đã trừ, số dư mới của ví nguồn và ví đích.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 6a: Chuyển tiền không mất phí

1. Tại bước 6, Người dùng giữ phí bằng 0 (hoặc để trống).
2. Hệ thống chỉ ghi hai khoản: trừ số tiền chuyển ở ví nguồn và cộng ở ví đích.
3. Hệ thống không tạo phí giao dịch; tổng số dư các ví trước và sau giữ nguyên.
4. Tiếp tục tại bước 11.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 8a: Ví nguồn trùng ví đích

1. Tại bước 8, hệ thống phát hiện ví nguồn trùng ví đích.
2. Hệ thống chặn, đánh dấu trường ví đích (`ERR-TRF-001`); Người dùng cần chọn hai ví khác nhau.
3. Quay lại bước 4.

##### Luồng ngoại lệ 8b: Số tiền chuyển hoặc chi phí không hợp lệ

1. Tại bước 8, giá trị sai định dạng số học, số tiền chuyển nhỏ hơn hoặc bằng 0, hoặc phí nhỏ hơn 0.
2. Hệ thống từ chối: sai định dạng báo `ERR-FIN-001`; số tiền chuyển nhỏ hơn hoặc bằng 0 báo `ERR-TRF-002`; phí âm báo `ERR-FEE-001`.
3. Quay lại bước 5.

##### Luồng ngoại lệ 8c: Số dư ví nguồn không đủ

1. Tại bước 8, tổng số tiền chuyển và phí vượt số dư khả dụng của ví nguồn.
2. Hệ thống từ chối (`ERR-WAL-001`); Người dùng giảm số tiền chuyển hoặc chọn ví nguồn khác.
3. Quay lại bước 5.

##### Luồng ngoại lệ 8d: Thiếu hoặc sai danh mục phí

1. Tại bước 8, phí lớn hơn 0 nhưng chưa chọn danh mục phí; hoặc danh mục đã xóa hoặc không thuộc `EXPENSE`.
2. Hệ thống từ chối: thiếu danh mục phí báo `ERR-FEE-002`; danh mục phí đã xóa hoặc sai loại báo `ERR-FEE-003`.
3. Quay lại bước 6.

##### Luồng ngoại lệ 8e: Chưa có danh mục chi tiêu để chọn cho phí giao dịch

1. Tại bước 8, phí lớn hơn 0 nhưng loại `EXPENSE` chưa có danh mục chi tiết đang dùng nào. Luồng này được kiểm tra trước luồng 8d.
2. Hệ thống xử lý như luồng ngoại lệ 7f của `UC-TXN-01` (thông báo, đề nghị mở quản lý danh mục theo `UC-CAT-01`).
3. Nếu Người dùng từ chối, quay lại bước 6 để đặt phí bằng 0.

##### Luồng ngoại lệ 9a: Lỗi một đầu ví khi ghi nhận đồng thời

1. Trong bước 9 hoặc 10, đã trừ ví nguồn nhưng cộng ví đích bị gián đoạn do lỗi kỹ thuật hoặc lỗi ghi tệp.
2. Hệ thống hoàn nguyên toàn bộ giao dịch, hủy việc trừ tiền ở ví nguồn, đưa hai ví về trạng thái ban đầu.
3. Hệ thống ghi nhật ký lỗi và thông báo: "Giao dịch chuyển tiền bị gián đoạn và đã được phục hồi nguyên trạng để giữ nguyên dữ liệu số dư. Vui lòng thử lại sau."

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): tổng số dư toàn bộ các ví luôn giữ nguyên đối với giao dịch chuyển tiền thuần túy.
2. Chi phí phải được ghi thành phí giao dịch `EXPENSE` riêng, không khấu trừ gộp làm sai số tiền ví đích thực nhận.
3. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi bước trừ cộng số dư dùng số thập phân chính xác.
4. Yêu cầu chức năng liên quan: `FR-TRF-01`.

### 6.10 UC-TRF-02: Tra cứu lịch sử chuyển tiền

Bảng thuộc tính đặc tả phạm vi theo dõi lịch sử chuyển khoản của ca sử dụng Tra cứu lịch sử chuyển tiền (`UC-TRF-02`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần rà soát các giao dịch chuyển tiền giữa các ví để so khớp số dư. |
| Mô tả tóm tắt | Hiển thị danh sách các giao dịch chuyển tiền nội bộ theo trình tự thời gian, gồm ví nguồn, ví đích, số tiền chuyển, chi phí và ghi chú. |
| Điều kiện kích hoạt | Người dùng chọn tab "Lịch sử chuyển tiền" trên màn hình Chuyển tiền nội bộ. |

#### Tiền điều kiện

1. Hệ thống đã đọc tệp dữ liệu thành công.
2. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Hệ thống chỉ đọc, không thay đổi sổ chuyển tiền hay số dư ví.

#### Hậu điều kiện thành công

Danh sách lịch sử hiển thị theo ngày giảm dần, phản ánh toàn bộ bản ghi trong sổ chuyển tiền (`FR-TRF-02`).

#### Luồng sự kiện chính

1. Người dùng mở tab "Lịch sử chuyển tiền".
2. Hệ thống truy vấn sổ chuyển tiền, sắp xếp theo ngày giảm dần.
3. Hệ thống hiển thị bảng gồm Mã giao dịch chuyển tiền, Ngày thực hiện, Ví nguồn, Ví đích, Số tiền chuyển, Chi phí, Ghi chú mô tả.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Chưa có giao dịch chuyển tiền nào

1. Tại bước 2, sổ chuyển tiền rỗng.
2. Hệ thống hiển thị bảng rỗng kèm thông báo: "Chưa có giao dịch chuyển tiền nội bộ nào được ghi nhận."

#### Quy tắc nghiệp vụ đặc thù

1. Số tiền chuyển và chi phí hiển thị phải khớp chính xác với các khoản đã ghi ở ví nguồn và ví đích (theo Quy tắc bảo toàn số dư ví `BR-FIN-02` và Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ `BR-FIN-03`).
2. Yêu cầu chức năng liên quan: `FR-TRF-02`.

### 6.11 UC-TRF-03: Xóa giao dịch chuyển tiền

Bảng thuộc tính đặc tả quy trình hoàn nguyên kép giữa hai ví của ca sử dụng Xóa giao dịch chuyển tiền (`UC-TRF-03`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần xóa giao dịch chuyển tiền nhầm và đưa số dư hai ví về như cũ. Hệ thống: hoàn nguyên đồng thời, chỉ cho xóa khi ví đích còn đủ số dư hoàn trả, giữ Quy tắc bảo toàn số dư ví (`BR-FIN-02`) và Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`) luôn đúng. |
| Mô tả tóm tắt | Xóa một giao dịch chuyển tiền đã thực hiện, tự động hoàn nguyên số dư ví nguồn và ví đích; bản ghi chuyển tiền và phí liên kết (nếu có) bị xóa khỏi sổ. |
| Điều kiện kích hoạt | Người dùng chọn xóa tại một dòng trong bảng lịch sử chuyển tiền. |

#### Tiền điều kiện

1. Giao dịch chuyển tiền cần xóa tồn tại trong sổ chuyển tiền.
2. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Nếu ví đích không đủ số dư hoàn trả, hoàn nguyên thất bại hoặc Người dùng hủy, giao dịch chuyển tiền, phí liên kết và số dư hai ví giữ nguyên.

#### Hậu điều kiện thành công

1. Ví nguồn được cộng trả tổng số tiền chuyển và phí; ví đích bị trừ số tiền chuyển.
2. Phí liên kết (nếu có) bị xóa khỏi sổ thu chi; bản ghi chuyển tiền bị xóa khỏi sổ chuyển tiền.

#### Luồng sự kiện chính

1. Tại bảng lịch sử, Người dùng nhấn "Xóa giao dịch" ở một giao dịch chuyển tiền.
2. Hệ thống hiển thị hộp thoại xác nhận, liệt kê các khoản hoàn nguyên: cộng trả số tiền chuyển (và phí nếu có) vào ví nguồn, trừ số tiền chuyển khỏi ví đích.
3. Người dùng nhấn "Xác nhận xóa giao dịch".
4. Hệ thống truy xuất giao dịch theo mã và kiểm tra số dư khả dụng hiện tại của ví đích phải lớn hơn hoặc bằng số tiền đã nhận (`FR-TRF-02`).
5. Hệ thống hoàn nguyên cả hai ví, chỉ ghi nhận khi cả hai thành công: bỏ giao dịch chuyển tiền khỏi công thức tính lại số dư, nên ví nguồn được hoàn tiền chuyển và ví đích bị thu hồi mà không ghi bút toán riêng; xóa phí liên kết (nếu có) khỏi sổ thu chi, qua đó hoàn phí về ví nguồn; xóa bản ghi chuyển tiền khỏi sổ chuyển tiền.
6. Hệ thống lưu tệp dữ liệu, thông báo thành công và làm mới số dư hai ví.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3a: Người dùng hủy xóa

1. Tại bước 3, Người dùng chọn "Hủy" hoặc đóng hộp thoại.
2. Hệ thống đóng hộp thoại, giữ nguyên giao dịch, phí liên kết và số dư hai ví.
3. Luồng kết thúc.

##### Luồng ngoại lệ 4a: Không tìm thấy giao dịch chuyển tiền

1. Tại bước 4, hệ thống không tìm thấy mã trong sổ chuyển tiền.
2. Hệ thống thông báo: "Không tìm thấy giao dịch chuyển tiền yêu cầu."
3. Luồng kết thúc, dữ liệu giữ nguyên.

##### Luồng ngoại lệ 4b: Ví đích không đủ số dư để hoàn nguyên

1. Tại bước 4, số dư khả dụng ví đích nhỏ hơn số tiền cần hoàn trả vì ví đích đã chi tiêu số tiền đó.
2. Hệ thống từ chối xóa (`ERR-WAL-001`); Người dùng cần nạp thêm tiền vào ví đích hoặc điều chỉnh các giao dịch chi tiêu liên quan trước khi xóa.
3. Luồng kết thúc, dữ liệu giữ nguyên.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): hoàn nguyên phải khôi phục chính xác cả ví nguồn và ví đích; một ví thất bại thì cả hai không thay đổi.
2. Yêu cầu chức năng liên quan: `FR-TRF-02`.

### 6.12 UC-CAT-01: Bổ sung danh mục phân loại mới

Bảng thuộc tính đặc tả cấu trúc phân cấp danh mục 3 cấp của ca sử dụng Bổ sung danh mục phân loại mới (`UC-CAT-01`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần mở rộng cấu trúc danh mục để theo dõi chi tiết các khoản chi hoặc nguồn thu đặc thù. Hệ thống: giữ cấu trúc 3 cấp theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`), danh mục luôn có nhóm cha, tên hợp lệ và duy nhất theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`). |
| Mô tả tóm tắt | Thêm một danh mục chi tiết (cấp 3) vào một nhóm nhu cầu đang dùng (cấp 2), hoặc thêm một nhóm nhu cầu mới vào một Loại thu chi (cấp 1). Mục mới ở trạng thái đang dùng và danh sách chọn của biểu mẫu giao dịch được cập nhật. |
| Điều kiện kích hoạt | Người dùng chọn thêm danh mục trên màn hình quản lý danh mục thu chi. |

#### Tiền điều kiện

1. Cấu trúc danh mục đã được đọc (2 Loại thu chi gốc; mỗi loại có ít nhất 1 nhóm).
2. Người dùng đang ở màn hình quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu lỗi kiểm tra hoặc lỗi ghi tệp, cấu trúc danh mục giữ nguyên, không sinh bản ghi rác.

#### Hậu điều kiện thành công

1. Danh mục mới thuộc đúng nhóm cha, kế thừa Loại thu chi của nhóm cha, lưu ở trạng thái đang dùng.
2. Cấu trúc 3 cấp được giữ: mọi danh mục thuộc một nhóm cha theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`), tên không trùng trong cùng nhóm cha theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`).

#### Luồng sự kiện chính

1. Người dùng mở "Quản lý danh mục thu chi", trực tiếp hoặc qua đề nghị từ UC giao dịch.
2. Hệ thống hiển thị cấu trúc 3 cấp: cấp 1 là hai nhánh `INCOME` và `EXPENSE`; cấp 2 là các nhóm nhu cầu; cấp 3 là các danh mục chi tiết.
3. Người dùng chọn một nhóm nhu cầu (ví dụ nhóm thuộc `EXPENSE`) và nhấn "Thêm danh mục con".
4. Hệ thống hiển thị biểu mẫu gồm Nhóm cha (chỉ đọc), Tên danh mục, Biểu tượng và màu sắc (tùy chọn).
5. Người dùng nhập tên, chọn biểu tượng và nhấn "Lưu danh mục".
6. Hệ thống kiểm tra: tên theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`) (sai định dạng `ERR-NAM-001`; trùng danh mục đang dùng trong cùng nhóm cha `ERR-NAM-002`); nhóm cha tồn tại và đang dùng.
7. Hệ thống tạo bản ghi danh mục với mã duy nhất, liên kết nhóm cha, trạng thái đang dùng.
8. Hệ thống làm mới cấu trúc danh mục, tự mở nhánh nhóm cha để làm nổi bật danh mục mới.
9. Hệ thống cập nhật danh sách chọn tại các biểu mẫu giao dịch.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Thêm nhóm nhu cầu mới

1. Tại bước 3, Người dùng chọn một Loại thu chi và nhấn "Thêm nhóm".
2. Hệ thống hiển thị biểu mẫu gồm Loại thu chi (chỉ đọc), Tên nhóm, Biểu tượng và màu sắc (tùy chọn).
3. Người dùng nhập tên nhóm và nhấn "Lưu nhóm". Hệ thống kiểm tra tên theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`): sai định dạng `ERR-NAM-001`; trùng nhóm đang dùng trong cùng Loại thu chi `ERR-NAM-002`.
4. Hệ thống tạo nhóm với mã duy nhất, trạng thái đang dùng. Tiếp tục tại bước 8 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 6a: Trùng tên danh mục trong cùng nhóm

1. Tại bước 6, tên trùng một danh mục đang dùng khác trong cùng nhóm cha (`ERR-NAM-002`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác."
3. Quay lại bước 5.

##### Luồng ngoại lệ 6b: Nhóm cha không tồn tại, đã xóa hoặc lỗi liên kết

1. Tại bước 6, nhóm cha đang ở trạng thái đã xóa, hoặc mã nhóm cha không tồn tại hoặc lỗi liên kết.
2. Hệ thống từ chối: nhóm cha đã xóa thì báo `ERR-CAT-005` với thông báo "Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng hoặc khôi phục nhóm trước."; nhóm cha không tồn tại hoặc lỗi liên kết thì báo "Không tìm thấy nhóm nhu cầu cha hợp lệ."
3. Luồng kết thúc, thao tác bị hủy.

##### Luồng ngoại lệ 6c: Tên trùng với mục đã xóa

1. Tại bước 6 (hoặc bước 3 của luồng rẽ nhánh 3a), tên trùng một hoặc nhiều danh mục hoặc nhóm đã xóa trong cùng phạm vi.
2. Hệ thống hiển thị hộp thoại cho Người dùng chọn Khôi phục mục cũ (giữ mã cũ, nối lại giao dịch cũ) hoặc Tạo mới (mã mới). Nếu có nhiều mục đã xóa cùng tên, hộp thoại liệt kê các mục (tên và số giao dịch còn gắn) để chọn đúng một mục cần khôi phục.
3. Nếu chọn Khôi phục, chuyển sang `UC-CAT-03` kèm mục đã chọn. Nếu chọn Tạo mới, tạo bản ghi với mã mới tại bước 7 (danh mục) hoặc bước 4 của luồng rẽ nhánh 3a (nhóm).

##### Luồng ngoại lệ 6d: Trùng tên nhóm trong cùng Loại thu chi

1. Tại bước 3 của luồng rẽ nhánh 3a, tên nhóm trùng một nhóm đang dùng khác trong cùng Loại thu chi (`ERR-NAM-002`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác."
3. Quay lại bước 3 của luồng rẽ nhánh 3a.

##### Luồng ngoại lệ 6e: Tên sai định dạng

1. Tại bước 6 (hoặc bước 3 của luồng rẽ nhánh 3a), tên sai định dạng (`ERR-NAM-001`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Quay lại bước 5 (danh mục) hoặc bước 3 của luồng rẽ nhánh 3a (nhóm).

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): mỗi Loại thu chi có ít nhất 1 nhóm đang dùng, mọi danh mục thuộc đúng một nhóm cha; cấm nút thiếu nhóm cha hoặc gán sai cấp.
2. Yêu cầu chức năng liên quan: `FR-CAT-01`, `FR-CAT-02`.

### 6.13 UC-CAT-02: Đổi tên danh mục

Bảng thuộc tính đặc tả phạm vi đổi tên và chuyển nhóm của ca sử dụng Đổi tên danh mục (`UC-CAT-02`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần chỉnh tên danh mục cho rõ hơn. Hệ thống: giữ nguyên liên kết với giao dịch quá khứ, giao dịch lịch sử hiển thị tên mới mà không đổi số tiền; tên mới hợp lệ và duy nhất theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`). |
| Mô tả tóm tắt | Đổi tên hiển thị của một danh mục chi tiết hoặc một nhóm nhu cầu đang dùng; giao dịch quá khứ không mất liên kết. UC này cũng cho chuyển một danh mục sang nhóm khác cùng Loại thu chi. |
| Điều kiện kích hoạt | Người dùng chọn đổi tên danh mục hoặc nhóm trên cấu trúc danh mục. |

#### Tiền điều kiện

1. Danh mục hoặc nhóm cần đổi tên tồn tại và đang dùng.
2. Người dùng đang ở màn hình quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu lỗi kiểm tra hoặc Người dùng hủy, tên và liên kết lịch sử giữ nguyên.

#### Hậu điều kiện thành công

1. Tên mới được lưu vào tệp dữ liệu; với luồng chuyển nhóm, danh mục thuộc nhóm đích và giữ nguyên mã cùng giao dịch liên kết.
2. Giao dịch quá khứ giữ nguyên liên kết và hiển thị theo tên mới.

#### Luồng sự kiện chính

1. Người dùng chọn một danh mục hoặc nhóm và nhấn "Đổi tên".
2. Hệ thống hiển thị hộp thoại chỉnh tên, điền sẵn tên hiện tại.
3. Người dùng nhập tên mới và nhấn "Lưu thay đổi".
4. Hệ thống kiểm tra theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`): sai định dạng `ERR-NAM-001`; trùng mục đang dùng cùng cấp (danh mục trong cùng nhóm cha, nhóm trong cùng Loại thu chi) `ERR-NAM-002`.
5. Hệ thống cập nhật tên trong tệp dữ liệu. Giao dịch lịch sử phản ánh tên mới qua mã liên kết; với nhóm, các danh mục con giữ nguyên mã và liên kết giao dịch.
6. Hệ thống thông báo thành công (đổi tên hoặc chuyển nhóm) và làm mới cấu trúc danh mục.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Chuyển danh mục sang nhóm khác

1. Tại bước 1, Người dùng chọn một danh mục chi tiết và nhấn "Chuyển nhóm".
2. Hệ thống hiển thị các nhóm đang dùng cùng Loại thu chi với danh mục, đánh dấu nhóm hiện tại.
3. Người dùng chọn nhóm đích. Hệ thống kiểm tra nhóm đích đang dùng, cùng Loại thu chi, và tên danh mục không trùng danh mục đang dùng trong nhóm đích theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`).
4. Hệ thống hiển thị hộp xác nhận nêu số giao dịch của danh mục, nhóm cũ, nhóm mới, và cảnh báo rằng báo cáo theo nhóm của các kỳ đã qua sẽ tính số tiền này vào nhóm mới (tổng thu và tổng chi không đổi).
5. Người dùng nhấn "Xác nhận chuyển". Hệ thống cập nhật nhóm cha của danh mục, giữ nguyên mã và mọi giao dịch liên kết.
6. Tiếp tục tại bước 6 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Trùng tên danh mục trong cùng nhóm

1. Tại bước 4, tên mới trùng danh mục đang dùng khác trong cùng nhóm cha (`ERR-NAM-002`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác."
3. Quay lại bước 3.

##### Luồng ngoại lệ 4b: Trùng tên nhóm trong cùng Loại thu chi

1. Tại bước 4, tên mới của nhóm trùng nhóm đang dùng khác trong cùng Loại thu chi (`ERR-NAM-002`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác."
3. Quay lại bước 3.

##### Luồng ngoại lệ 4c: Tên mới sai định dạng

1. Tại bước 4, tên mới sai định dạng (`ERR-NAM-001`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Quay lại bước 3.

##### Luồng ngoại lệ 1b: Nhóm đích đã xóa

1. Tại bước 3 của luồng rẽ nhánh 1a, nhóm đích đã xóa.
2. Hệ thống từ chối (`ERR-CAT-003`), giữ danh mục ở nhóm cũ.
3. Quay lại bước 3 của luồng rẽ nhánh 1a để chọn nhóm đích khác.

##### Luồng ngoại lệ 1c: Trùng tên trong nhóm đích

1. Tại bước 3 của luồng rẽ nhánh 1a, nhóm đích đã có danh mục đang dùng cùng tên (`ERR-NAM-002`).
2. Hệ thống từ chối và thông báo: "Nhóm đích đã có danh mục cùng tên. Hãy đổi tên danh mục trước khi chuyển."
3. Quay lại bước 3 của luồng rẽ nhánh 1a.

##### Luồng ngoại lệ 1d: Chuyển sang nhóm khác Loại thu chi

1. Tại bước 3 của luồng rẽ nhánh 1a, hệ thống nhận yêu cầu chuyển sang nhóm đích thuộc Loại thu chi khác (yêu cầu này không đi qua danh sách ở bước 2).
2. Hệ thống từ chối (`ERR-CAT-001`).
3. Quay lại bước 3 của luồng rẽ nhánh 1a.

#### Quy tắc nghiệp vụ đặc thù

1. Đổi tên không được phá liên kết với giao dịch lịch sử.
2. Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): cấm chuyển danh mục sang Loại thu chi khác và cấm đổi Loại của nhóm hoặc danh mục đã có giao dịch, để tránh sai lệch báo cáo dòng tiền quá khứ.
3. Báo cáo luôn gom theo nhóm hiện tại của danh mục, không lưu nhóm tại thời điểm ghi giao dịch. Nhóm nhu cầu không chuyển được vì cha của nhóm là Loại thu chi và Loại thu chi không đổi.
4. Yêu cầu chức năng liên quan: `FR-CAT-02`, `FR-CAT-03`.

### 6.14 UC-CAT-03: Khôi phục hoặc tạo lại mục đã xóa

Bảng thuộc tính đặc tả quy trình khôi phục mục đã ẩn của ca sử dụng Khôi phục hoặc tạo lại mục đã xóa (`UC-CAT-03`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần lấy lại nhóm hoặc danh mục xóa nhầm hoặc cần dùng lại, và chọn nối lại lịch sử cũ hay bắt đầu mới. Hệ thống: khôi phục không làm mất hay đổi dữ liệu lịch sử, không để hai mục đang dùng trùng tên trong cùng phạm vi theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`). |
| Mô tả tóm tắt | Từ màn hình "Danh mục đã xóa", Người dùng chọn một nhóm hoặc danh mục đã xóa rồi chọn Khôi phục (giữ mã cũ, nối lại giao dịch cũ) hoặc Tạo mới (mã mới, không nối giao dịch cũ). |
| Điều kiện kích hoạt | Người dùng mở màn hình "Danh mục đã xóa", hoặc chọn Khôi phục tại hộp hỏi khi tên mới trùng một mục đã xóa (`UC-CAT-01`). |

#### Tiền điều kiện

1. Có ít nhất một nhóm hoặc danh mục đang ở trạng thái đã xóa.
2. Người dùng đang ở màn hình quản lý danh mục thu chi, hoặc ở hộp hỏi Khôi phục hoặc Tạo mới của `UC-CAT-01`.

#### Bảo đảm tối thiểu

Nếu bị từ chối hoặc lỗi ghi, mục đã xóa giữ nguyên trạng thái và giao dịch lịch sử không đổi.

#### Hậu điều kiện thành công

1. Khôi phục: mục trở lại đang dùng với mã cũ, giao dịch cũ được nối lại, mục hiện lại trong danh sách chọn.
2. Tạo mới: mục mới đang dùng với mã mới; mục cũ vẫn đã xóa và giao dịch cũ vẫn gắn với mục cũ.
3. Báo cáo lịch sử không bị đứt đoạn trong cả hai trường hợp.

#### Luồng sự kiện chính

1. Người dùng mở "Danh mục đã xóa".
2. Hệ thống liệt kê các nhóm và danh mục đã xóa kèm tên, nơi chứa cũ và số giao dịch còn gắn.
3. Người dùng chọn một mục và nhấn "Khôi phục".
4. Hệ thống kiểm tra theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`): không có mục đang dùng nào trùng tên trong cùng phạm vi (danh mục trong cùng nhóm, nhóm trong cùng Loại thu chi); với danh mục, nhóm cha phải đang dùng.
5. Hệ thống chuyển mục sang đang dùng với mã cũ, nối lại giao dịch cũ và đưa mục trở lại danh sách chọn.
6. Hệ thống làm mới cấu trúc danh mục và thông báo khôi phục thành công.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Vào từ hộp hỏi của UC-CAT-01

1. Tại luồng ngoại lệ 6c của `UC-CAT-01`, Người dùng chọn Khôi phục.
2. Hệ thống nhận mục đã chọn từ `UC-CAT-01` (nếu có nhiều mục đã xóa cùng tên) hoặc lấy mục đã xóa duy nhất cùng tên, rồi bỏ qua các bước 1 đến 3.
3. Tiếp tục tại bước 4 của luồng chính.

##### Luồng rẽ nhánh 3a: Tạo mới thay vì khôi phục

1. Tại bước 3, Người dùng nhấn "Tạo mới".
2. Hệ thống yêu cầu nhập tên (điền sẵn tên cũ) và nơi chứa, kiểm tra tên theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`) (đúng định dạng, không trùng mục đang dùng trong cùng phạm vi). Bước này không hỏi lại Khôi phục hoặc Tạo mới.
3. Hệ thống tạo mục mới với mã mới, đang dùng; mục cũ giữ trạng thái đã xóa.
4. Tiếp tục tại bước 6 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3b: Tên trùng hoặc nhóm cha đã xóa khi tạo mới

1. Tại bước 2 của luồng rẽ nhánh 3a, tên trùng mục đang dùng trong cùng phạm vi (`ERR-NAM-002`), hoặc nhóm cha của danh mục đang đã xóa.
2. Hệ thống từ chối và thông báo theo trường hợp: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác." (danh mục trùng tên); "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác." (nhóm trùng tên); hoặc báo `ERR-CAT-005` với thông báo "Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng hoặc khôi phục nhóm trước." (nhóm cha đã xóa).
3. Quay lại bước 2 của luồng rẽ nhánh 3a để nhập lại tên hoặc chọn nơi chứa khác.

##### Luồng ngoại lệ 3c: Tên sai định dạng khi tạo mới

1. Tại bước 2 của luồng rẽ nhánh 3a, tên sai định dạng (`ERR-NAM-001`).
2. Hệ thống từ chối và thông báo: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Quay lại bước 2 của luồng rẽ nhánh 3a.

##### Luồng ngoại lệ 4a: Trùng tên với mục đang dùng

1. Tại bước 4, đã có mục đang dùng cùng tên trong cùng phạm vi.
2. Hệ thống từ chối (`ERR-NAM-002`) và thông báo: "Không thể khôi phục vì tên đã được dùng. Hãy đổi tên một trong hai mục rồi thử lại."
3. Luồng kết thúc, mục giữ trạng thái đã xóa.

##### Luồng ngoại lệ 4b: Nhóm cha vẫn đã xóa

1. Tại bước 4, Người dùng khôi phục một danh mục mà nhóm cha còn ở trạng thái đã xóa.
2. Hệ thống từ chối (`ERR-CAT-004`), yêu cầu khôi phục nhóm cha trước.
3. Luồng kết thúc, danh mục giữ trạng thái đã xóa.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): khôi phục giữ nguyên mã cũ nên không tạo dữ liệu mồ côi; mã định danh không bao giờ dùng lại cho mục khác.
2. Khôi phục nhóm chỉ khôi phục riêng nhóm; các danh mục con được khôi phục riêng từng cái.
3. Yêu cầu chức năng liên quan: `FR-CAT-02`, `FR-CAT-03`.

### 6.15 UC-CAT-04: Xóa danh mục hoặc nhóm

Bảng thuộc tính đặc tả cơ chế xóa cứng hoặc ẩn mềm của ca sử dụng Xóa danh mục hoặc nhóm (`UC-CAT-04`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: cần bỏ danh mục hoặc nhóm không còn dùng bằng một thao tác, kể cả khi đã có giao dịch, mà không mất lịch sử. Hệ thống: không để giao dịch trỏ tới danh mục không còn tồn tại và không để một Loại thu chi mất hết nhóm. |
| Mô tả tóm tắt | Người dùng chọn Xóa một danh mục hoặc nhóm. Mục chưa có giao dịch bị xóa hẳn; mục đã có giao dịch chuyển sang trạng thái đã xóa và giao dịch được giữ nguyên. Xóa nhóm kéo theo mọi danh mục con. |
| Điều kiện kích hoạt | Người dùng chọn xóa trên một danh mục hoặc nhóm trong cấu trúc danh mục. |

#### Tiền điều kiện

1. Danh mục hoặc nhóm cần xóa tồn tại và đang dùng.
2. Người dùng đang ở màn hình quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu bị từ chối, bị hủy hoặc lỗi ghi, cấu trúc danh mục và mọi giao dịch giữ nguyên.

#### Hậu điều kiện thành công

1. Mục chưa có giao dịch bị xóa hoàn toàn khỏi tệp dữ liệu.
2. Mục đã có giao dịch ở trạng thái đã xóa: không còn trong danh sách chọn cho giao dịch mới, vẫn hiện trong báo cáo lịch sử kèm nhãn "(đã xóa)" và có thể khôi phục tại `UC-CAT-03`.
3. Khi xóa nhóm: mỗi danh mục con được xử lý theo các điểm trên; nhóm có giao dịch bên trong thì ở trạng thái đã xóa, nhóm không có thì bị xóa hẳn.

#### Luồng sự kiện chính

1. Người dùng chọn một danh mục hoặc nhóm và nhấn "Xóa".
2. Hệ thống quét sổ giao dịch thu chi để đếm giao dịch liên kết; với nhóm, đếm theo từng danh mục con đang dùng.
3. Hệ thống hiển thị hộp xác nhận. Với danh mục: nêu số giao dịch liên quan và kết quả (xóa hẳn nếu chưa có giao dịch; ẩn và giữ giao dịch nếu đã có). Với nhóm: nêu số danh mục sẽ bị ẩn, số danh mục xóa hẳn và tổng số giao dịch được giữ lại.
4. Người dùng nhấn "Xác nhận xóa".
5. Hệ thống xóa hoặc chuyển sang trạng thái đã xóa theo kết quả đã nêu, rồi cập nhật cấu trúc danh mục.
6. Hệ thống thông báo thành công; mục đã xóa có trong màn hình "Danh mục đã xóa".

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Xóa nhóm đang dùng cuối cùng của một Loại thu chi

1. Tại bước 2, nhóm được chọn là nhóm đang dùng cuối cùng của Loại thu chi chứa nó.
2. Hệ thống từ chối (`ERR-CAT-002`) và thông báo: "Không thể xóa nhóm cuối cùng của loại thu chi này."
3. Luồng kết thúc, cấu trúc danh mục giữ nguyên.

##### Luồng ngoại lệ 4a: Người dùng hủy xác nhận

1. Tại bước 4, Người dùng nhấn "Hủy".
2. Hệ thống đóng hộp xác nhận và không thay đổi gì.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): mục đã có giao dịch không bao giờ bị xóa vật lý, để không giao dịch nào mất danh mục.
2. Người dùng chỉ thấy một thao tác "Xóa"; xóa hẳn hay ẩn do hệ thống quyết định theo số giao dịch.
3. Yêu cầu chức năng liên quan: `FR-CAT-03`.

###  UC-REP-01: Thống kê dòng tiền 12 tháng gần nhất

Bảng thuộc tính đặc tả chu kỳ tổng hợp dòng tiền 12 tháng của ca sử dụng Thống kê dòng tiền 12 tháng gần nhất (`UC-REP-01`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: đánh giá tình hình tài chính định kỳ qua chuỗi dòng tiền 12 tháng và tỷ lệ tiết kiệm. |
| Mô tả tóm tắt | Tổng hợp chuỗi dòng tiền 12 tháng gần nhất từ mốc tháng chỉ định thành bảng thống kê theo `FR-REP-01`: thu nhập, chi tiêu, dòng tiền thuần, tỷ lệ tiết kiệm theo Quy tắc độ chính xác tài chính (`BR-FIN-01`). |
| Điều kiện kích hoạt | Người dùng cần đánh giá dòng tiền định kỳ trong 12 tháng gần nhất. |

#### Tiền điều kiện

1. Hệ thống đã khởi chạy và đọc tệp dữ liệu giao dịch thành công.
2. Người dùng đang ở giao diện nhóm chức năng Thống kê và báo cáo.

#### Bảo đảm tối thiểu

Hệ thống chỉ đọc, không thay đổi giao dịch hay số dư ví.

#### Hậu điều kiện thành công

1. Bảng thống kê gồm đúng 12 dòng liên tục lùi từ tháng kết thúc, không trống trường nào.
2. Dòng tiền thuần và tỷ lệ tiết kiệm tính đúng theo `FR-REP-01`; ba mức bình quân chia cho 12, kèm ghi chú rằng các tháng chưa có giao dịch vẫn được tính.

#### Luồng sự kiện chính

1. Người dùng chọn "Thống kê dòng tiền 12 tháng gần nhất", có thể tùy chọn mốc tháng và năm kết thúc (mặc định tháng và năm hiện tại).
2. Hệ thống xác định mốc tháng kết thúc (mặc định lấy từ đồng hồ hệ thống).
3. Hệ thống tạo danh sách 12 tháng liên tục từ 11 tháng trước mốc đến chính mốc, sắp xếp thời gian tăng dần (tháng kết thúc ở dòng cuối).
4. Với mỗi tháng, hệ thống lọc giao dịch có ngày trong tháng và tính tổng thu nhập, tổng chi tiêu (trên các giao dịch đang tồn tại).
5. Hệ thống tính các chỉ số phái sinh theo `FR-REP-01`: dòng tiền thuần bằng tổng thu trừ tổng chi; tỷ lệ tiết kiệm (`NetCashFlow_m` là dòng tiền thuần, `Income_m` là tổng thu nhập của tháng m):
   $$
   SavingsRate_m = \begin{cases} \left(\frac{NetCashFlow_m}{Income_m}\right) \times 100\%, & Income_m > 0 \\ 0.00\%, & Income_m \le 0 \end{cases}
   $$
   Làm tròn ngân hàng, hiển thị 2 chữ số thập phân.
6. Hệ thống tính thu nhập, chi tiêu và dòng tiền thuần bình quân của chuỗi (`FR-REP-01`); mẫu số luôn là 12 kể cả các tháng chưa có giao dịch, và hệ thống hiển thị ghi chú nêu điều này.
7. Hệ thống hiển thị bảng 12 dòng tháng; chân bảng có ba mức bình quân kèm ghi chú; giao diện hiển thị biểu đồ kết hợp cột thu chi và đường dòng tiền thuần.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Tháng hoàn toàn không có giao dịch

1. Tại bước 4, một tháng không có giao dịch thu chi.
2. Hệ thống hiển thị "0.00 VNĐ", không để trống ô dữ liệu.
3. Hệ thống gán dòng tiền thuần 0.00 và tỷ lệ tiết kiệm 0.00%, bảo đảm bảng 12 tháng liên tục và không gây lỗi giao diện.

##### Luồng ngoại lệ 5a: Tháng có thu nhập không dương nhưng có chi tiêu

1. Tại bước 5, tổng thu nhập của tháng nhỏ hơn hoặc bằng 0 nhưng tổng chi tiêu lớn hơn 0.
2. Chia trực tiếp sẽ gây lỗi chia cho 0 (`ERR-FIN-002`), nên hệ thống áp dụng hàm từng nhánh và gán tỷ lệ tiết kiệm 0.00%.
3. Hệ thống hiển thị dòng tiền thuần âm (bằng âm của tổng chi tiêu) và chỉ báo thâm hụt ngân sách.

#### Quy tắc nghiệp vụ đặc thù

1. `FR-REP-01`: bảng luôn gồm đúng 12 tháng liên tục lùi từ tháng kết thúc (mặc định tháng hiện tại).
2. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi chỉ số tính bằng số thập phân chính xác, làm tròn ngân hàng 2 chữ số, không có sai số dấu phẩy động.
3. Yêu cầu chức năng liên quan: `FR-REP-01`.

### 6.17 UC-REP-02: Phân tích cơ cấu thu chi theo nhóm và danh mục

Bảng thuộc tính đặc tả cơ cấu tỷ lệ chi tiêu theo nhóm và danh mục của ca sử dụng Phân tích cơ cấu thu chi theo nhóm và danh mục (`UC-REP-02`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: phân tích cơ cấu thu chi 2 tầng theo nhóm và danh mục, đối chiếu biến động với kỳ liền trước. |
| Mô tả tóm tắt | Phân tích cơ cấu thu chi theo nhóm nhu cầu và danh mục chi tiết trong một khoảng thời gian chọn, kèm so sánh biến động với kỳ liền kề (`FR-REP-02`). |
| Điều kiện kích hoạt | Người dùng cần phân tích cơ cấu thu chi theo nhóm và danh mục của một kỳ cụ thể. |

#### Tiền điều kiện

1. Hệ thống đã khởi chạy và đọc tệp dữ liệu giao dịch thành công.
2. Người dùng đang ở giao diện nhóm chức năng Thống kê và báo cáo.

#### Bảo đảm tối thiểu

Hệ thống chỉ đọc, không thay đổi dữ liệu phân loại hay số dư ví.

#### Hậu điều kiện thành công

Báo cáo cơ cấu 2 tầng (Nhóm nhu cầu, Danh mục) phản ánh đúng tỷ lệ và biến động so với kỳ liền kề (`FR-REP-02`).

#### Luồng sự kiện chính

1. Người dùng chọn tab "Cơ cấu thu chi", khoảng thời gian (mặc định tháng hiện tại) và loại luồng tiền (`EXPENSE` hoặc `INCOME`).
2. Hệ thống quét các giao dịch đang tồn tại trong khoảng thời gian, thuộc loại luồng tiền đã chọn.
3. Hệ thống tổng hợp theo 2 tầng (`FR-REP-02`; `Amount_group`, `Amount_cat` là tổng tiền của nhóm và danh mục; `TotalPeriod` là tổng thu hoặc chi của kỳ):
   - Cấp nhóm nhu cầu:
     $$Share_{group} = \left(\frac{Amount_{group}}{TotalPeriod}\right) \times 100\%$$
   - Cấp danh mục chi tiết (tỷ lệ trên nhóm cha và trên toàn kỳ):
     $$Share_{cat/group} = \left(\frac{Amount_{cat}}{Amount_{group}}\right) \times 100\%, \quad Share_{cat/period} = \left(\frac{Amount_{cat}}{TotalPeriod}\right) \times 100\%$$
4. Hệ thống truy vấn kỳ liền trước cùng độ dài để so sánh: chênh lệch tuyệt đối bằng số tiền kỳ này trừ kỳ trước; tỷ lệ biến động bằng chênh lệch chia số tiền kỳ trước nhân 100 khi kỳ trước lớn hơn 0; nếu kỳ trước bằng 0 thì hiển thị nhãn "Mới".
5. Hệ thống sắp xếp nhóm và danh mục theo số tiền giảm dần.
6. Hệ thống hiển thị bảng tổng hợp phân cấp kèm biểu đồ tròn cơ cấu. Nhóm và danh mục đã xóa vẫn hiển thị kèm nhãn "(đã xóa)" (`FR-REP-02`).

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Kỳ trước không có giao dịch khi so sánh biến động

1. Tại bước 4, kỳ trước không có giao dịch trong danh mục nhưng kỳ này có.
2. Hệ thống hiển thị chênh lệch tuyệt đối bằng số tiền kỳ này và tỷ lệ biến động "Mới", tránh phép chia cho 0 (`ERR-FIN-002`).

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi phép tổng hợp và tỷ lệ dùng số thập phân chính xác, làm tròn ngân hàng; tiền làm tròn 2 chữ số; tỷ lệ phần trăm giữ tối thiểu 4 chữ số thập phân ở bước trung gian và hiển thị 2 chữ số (`FR-REP-02`).
2. Nhóm và danh mục đã xóa giữ tên gốc kèm nhãn "(đã xóa)", không gộp vào dòng "Khác". Dòng số 0 chỉ hiện cho các kỳ quá khứ từ kỳ chứa ngày mục được tạo; kỳ trước ngày tạo và kỳ tương lai ẩn dòng.
3. Yêu cầu chức năng liên quan: `FR-REP-02`.

### 6.18 UC-DAT-01: Xuất dữ liệu tài chính

Bảng thuộc tính đặc tả cấu trúc định dạng dữ liệu xuất tệp của ca sử dụng Xuất dữ liệu tài chính (`UC-DAT-01`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: sao lưu dữ liệu tài chính cục bộ ra định dạng mở, giữ nguyên độ chính xác số học, không làm tròn. |
| Mô tả tóm tắt | Xuất dữ liệu (toàn phần hoặc sổ giao dịch) ra tệp JSON, giữ độ chính xác các trường số học tài chính (`FR-DAT-01`). |
| Điều kiện kích hoạt | Người dùng cần sao lưu định kỳ hoặc xuất dữ liệu ra tệp định dạng mở. |

#### Tiền điều kiện

1. Hệ thống hoạt động bình thường, có quyền đọc và ghi trên thư mục lưu trữ chỉ định.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

Xuất dữ liệu không làm đổi dữ liệu hiện có. Nếu lỗi ghi đĩa, hệ thống dọn tệp xuất dở dang và giữ nguyên dữ liệu nội bộ.

#### Hậu điều kiện thành công

Tệp sao lưu được tạo với mã hóa UTF-8; các trường số học là chuỗi số giữ nguyên chữ số thập phân (không làm tròn, không dùng ký hiệu khoa học); tên tệp tự động theo `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json` (`FR-DAT-01`).

#### Luồng sự kiện chính

1. Người dùng chọn "Xuất dữ liệu tài chính".
2. Hệ thống hiển thị biểu mẫu gồm Phạm vi dữ liệu (`FULL` hoặc `TRANSACTIONS`), Định dạng tệp (`JSON`), Thư mục lưu trữ (chọn qua hộp thoại duyệt thư mục).
3. Người dùng chọn phạm vi `FULL`, định dạng `JSON` và thư mục đích.
4. Người dùng xác nhận bắt đầu xuất.
5. Hệ thống trích xuất toàn bộ dữ liệu hiện tại: ví, danh mục phân loại, giao dịch thu chi (kể cả phí giao dịch), chuyển tiền nội bộ và bản ghi điều chỉnh.
6. Hệ thống chuyển sang cấu trúc JSON: chuẩn hóa mọi trường số học tài chính thành chuỗi số chính xác (ví dụ `"50000000.00"`), cấm kiểu số thực có sai số hoặc ký hiệu số mũ như `5.0E7` (`FR-DAT-01`); ngày tháng theo `YYYY-MM-DD`.
7. Hệ thống tạo tên tệp `pfms_export_full_{yyyyMMdd_HHmmss}.json`.
8. Hệ thống ghi tệp xuống đĩa với bảng mã UTF-8 không BOM.
9. Hệ thống kiểm tra kích thước và độ nguyên vẹn của tệp vừa tạo.
10. Hệ thống thông báo thành công kèm đường dẫn, kích thước tệp và số bản ghi đã xuất của từng nhóm dữ liệu.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Xuất sổ giao dịch thu chi

1. Tại bước 3, Người dùng chọn phạm vi `TRANSACTIONS`, định dạng `JSON` và thư mục đích.
2. Hệ thống trích xuất toàn bộ sổ giao dịch thu chi, gồm cả phí giao dịch của các giao dịch chuyển tiền đang tồn tại.
3. Hệ thống tạo tệp JSON UTF-8 không BOM gồm danh sách giao dịch thu chi; mỗi bản ghi phí có trường mã giao dịch phát sinh ra phí (`FR-DAT-01`).
4. Tên tệp theo `pfms_export_transactions_{yyyyMMdd_HHmmss}.json`.
5. Tiếp tục tại bước 8 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 8a: Lỗi quyền ghi tệp hoặc ổ đĩa đầy

1. Tại bước 8, hệ thống không ghi được tệp do thiếu quyền truy cập hoặc ổ đĩa hết dung lượng.
2. Hệ thống thông báo: "Không thể lưu tệp dữ liệu vào thư mục đã chọn do lỗi quyền truy cập hoặc ổ đĩa đầy. Vui lòng chọn thư mục khác."
3. Thao tác xuất bị hủy và không để lại tệp rác trên đĩa.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc độ chính xác tài chính (`BR-FIN-01`), `FR-DAT-01`: không làm tròn hay dùng ký hiệu khoa học đối với trường số học tài chính khi xuất.
2. Yêu cầu chức năng liên quan: `FR-DAT-01`.

### 6.19 UC-DAT-02: Phục hồi dữ liệu hệ thống từ bản sao lưu

Bảng thuộc tính đặc tả quy trình xác thực dữ liệu và tự động sao lưu của ca sử dụng Phục hồi dữ liệu hệ thống từ bản sao lưu (`UC-DAT-02`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: phục hồi dữ liệu tài chính từ tệp sao lưu đã xuất mà không vi phạm các quy tắc tài chính hiện hành. |
| Mô tả tóm tắt | Phục hồi dữ liệu từ tệp sao lưu qua quy trình kiểm tra 4 bước (cú pháp; danh mục theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`); ví và liên kết chuyển tiền; số dư không âm theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`)), có hoàn nguyên tự động theo `FR-DAT-02`. |
| Điều kiện kích hoạt | Người dùng cần phục hồi dữ liệu từ một tệp sao lưu có sẵn. |

#### Tiền điều kiện

1. Hệ thống hoạt động bình thường, có quyền đọc tệp sao lưu chỉ định.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

Nếu tệp không hợp lệ hoặc quá trình nhập bị hủy, dữ liệu hiện tại giữ nguyên và không ghi bất kỳ dữ liệu sai lệch nào.

#### Hậu điều kiện thành công

1. Dữ liệu hiện tại được thay thế hoàn toàn bằng dữ liệu từ tệp sao lưu; Quy tắc bảo toàn số dư ví (`BR-FIN-02`) và Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`) luôn đúng.
2. Nếu tệp chứa dữ liệu lỗi, toàn bộ thao tác bị hủy và hoàn nguyên, dữ liệu hiện tại không đổi.

#### Luồng sự kiện chính

1. Người dùng chọn "Nhập dữ liệu từ tệp sao lưu".
2. Hệ thống hiển thị biểu mẫu với hộp thoại chọn tệp sao lưu (`.json`).
3. Người dùng chọn tệp sao lưu.
4. Hệ thống hiển thị cảnh báo: "Phục hồi sẽ thay thế toàn bộ dữ liệu tài chính hiện tại bằng dữ liệu từ tệp sao lưu. Xác nhận tiếp tục thao tác?"
5. Người dùng xác nhận tiếp tục.
6. Hệ thống phân tích cú pháp JSON và kiểm tra 4 bước (`FR-DAT-02`):
   - Bước 1 (định dạng): cấu trúc tệp, định dạng ngày `YYYY-MM-DD`, chuỗi số học.
   - Bước 2 (Quy tắc phân cấp danh mục thu chi 3 cấp `BR-FIN-04`): mọi mã danh mục dùng trong giao dịch trỏ tới danh mục hợp lệ (đang dùng hoặc đã xóa) có Loại thu chi tương thích.
   - Bước 3 (ví và liên kết chuyển tiền): mọi giao dịch thu chi và chuyển tiền trỏ tới ví có trong danh sách; mỗi phí giao dịch phát sinh từ chuyển tiền trỏ tới một giao dịch chuyển tiền tồn tại.
   - Bước 4 (Quy tắc bảo toàn số dư ví `BR-FIN-02`): tính lại số dư từng ví từ lịch sử dòng tiền trong tệp, không ví nào âm.
7. Nếu cả 4 bước đều đạt, hệ thống bắt đầu ghi tệp, xóa dữ liệu cũ, nạp toàn bộ bản ghi từ tệp sao lưu, hoàn tất ghi, rồi tính lại toàn bộ số dư và bảng thống kê 12 tháng gần nhất.
8. Hệ thống hiển thị báo cáo nhập thành công gồm tổng số bản ghi và số lượng từng loại thực thể đã phục hồi.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Người dùng hủy tại cảnh báo thay thế dữ liệu

1. Tại bước 4, trước cảnh báo thay thế toàn bộ dữ liệu, Người dùng chọn "Hủy bỏ".
2. Hệ thống dừng thao tác nhập, đóng hộp thoại và giữ nguyên dữ liệu.

##### Luồng ngoại lệ 6a: Tệp sai cú pháp hoặc bị hỏng

1. Tại bước 6, tệp lỗi định dạng JSON (thiếu dấu ngoặc, rỗng hoặc sai mã hóa).
2. Hệ thống hủy nhập và thông báo: "Tệp sao lưu bị lỗi cấu trúc cú pháp hoặc bị hỏng dữ liệu. Vui lòng kiểm tra lại tệp nguồn."
3. Dữ liệu hiện tại giữ nguyên.

##### Luồng ngoại lệ 6b: Dữ liệu trong tệp vi phạm quy tắc số dư hoặc liên kết

1. Tại bước 6, hệ thống phát hiện ít nhất một vi phạm, ví dụ giao dịch chi trỏ tới ví không tồn tại, phí giao dịch trỏ tới chuyển tiền không tồn tại, hoặc lịch sử dòng tiền làm ví tiền mặt âm.
2. Hệ thống hủy toàn bộ quá trình nhập, không ghi bản ghi nào vào tệp dữ liệu.
3. Hệ thống hiển thị bảng lỗi chi tiết (số dòng lỗi, loại vi phạm: liên kết dữ liệu hoặc Quy tắc bảo toàn số dư ví (`BR-FIN-02`)) để Người dùng sửa tệp trước khi thử lại.

#### Quy tắc nghiệp vụ đặc thù

1. Nguyên tắc tất cả hoặc không: nếu có bất kỳ lỗi nào, toàn bộ quá trình hoàn nguyên về trạng thái trước đó.
2. `FR-DAT-02`: không ghi dữ liệu nếu chưa qua 4 bước kiểm tra (cấu trúc; danh mục theo Quy tắc phân cấp danh mục thu chi 3 cấp `BR-FIN-04`; ví và liên kết chuyển tiền; số dư không âm theo Quy tắc bảo toàn số dư ví `BR-FIN-02`).
3. Yêu cầu chức năng liên quan: `FR-DAT-02`.

### 6.20 UC-DAT-03: Kiểm tra lại dữ liệu hệ thống

Bảng thuộc tính đặc tả thuật toán rà soát bất biến toàn vẹn của ca sử dụng Kiểm tra lại dữ liệu hệ thống (`UC-DAT-03`):

| Trường | Nội dung |
| --- | --- |
| Các bên liên quan và mối quan tâm | Người dùng: kiểm tra, đối soát độc lập 4 điều kiện toàn vẹn để phát hiện sớm sai lệch. |
| Mô tả tóm tắt | Chạy công cụ kiểm tra lại độc lập theo `FR-DAT-03` cho 4 điều kiện: số dư ví theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`), cân bằng nguồn - đích khi chuyển tiền theo Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`), liên kết danh mục theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`) và toàn vẹn liên kết phí chuyển tiền. |
| Điều kiện kích hoạt | Người dùng cần kiểm tra định kỳ hoặc khi nghi ngờ có sai lệch. |

#### Tiền điều kiện

1. Hệ thống hoạt động bình thường, có quyền đọc tệp dữ liệu cục bộ.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

Kiểm tra chỉ đọc, không thay đổi giao dịch hay số dư ví.

#### Hậu điều kiện thành công

Báo cáo kiểm tra phản ánh trung thực trạng thái của 4 điều kiện toàn vẹn (`FR-DAT-03`).

#### Luồng sự kiện chính

1. Người dùng chọn "Kiểm tra lại dữ liệu hệ thống".
2. Hệ thống quét toàn bộ tệp dữ liệu và kiểm tra lần lượt 4 điều kiện (`FR-DAT-03`):
   - Bước 1 (Quy tắc bảo toàn số dư ví `BR-FIN-02`): tính độc lập số dư từng ví từ lịch sử dòng tiền, so với số dư ghi nhận, sai số bằng 0.00 VNĐ.
   - Bước 2 (Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ `BR-FIN-03`): với mọi giao dịch chuyển tiền, số tiền trừ ở ví nguồn bằng đúng số tiền cộng ở ví đích (không tính chi phí).
   - Bước 3 (Quy tắc phân cấp danh mục thu chi 3 cấp `BR-FIN-04`): mọi giao dịch thu chi gắn với danh mục có trong dữ liệu (mục đã xóa vẫn hợp lệ) và đúng Loại thu chi.
   - Bước 4: mọi phí giao dịch phát sinh từ chuyển tiền trỏ tới một giao dịch chuyển tiền tồn tại.
3. Hệ thống tổng hợp Báo cáo kiểm tra lại dữ liệu, phân mức độ nghiêm trọng: `INFO`, `WARNING`, `ERROR`.
4. Nếu tất cả đạt, hệ thống hiển thị trạng thái hợp lệ: "Dữ liệu hệ thống đạt 100% yêu cầu về liên kết dữ liệu và số dư khớp nhau."
5. Nếu có sai lệch, hệ thống đánh dấu đỏ, liệt kê mã giao dịch, số tiền chênh lệch và hướng dẫn biện pháp xử lý.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

Không có luồng ngoại lệ nghiệp vụ bổ sung ngoài việc phân loại mức nghiêm trọng của sai lệch ở bước 3 và bước 5 của luồng chính.

#### Quy tắc nghiệp vụ đặc thù

1. `FR-DAT-03`: công cụ kiểm tra độc lập tự động kiểm tra 4 điều kiện toàn vẹn: số dư ví, cân bằng nguồn - đích khi chuyển tiền, liên kết danh mục theo Quy tắc phân cấp danh mục thu chi 3 cấp  (`BR-FIN-04`) và liên kết phí chuyển tiền.
2. Yêu cầu chức năng liên quan: `FR-DAT-03`.