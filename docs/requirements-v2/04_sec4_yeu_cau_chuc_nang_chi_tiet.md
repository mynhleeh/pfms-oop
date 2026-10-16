## 4. YÊU CẦU CHỨC NĂNG CHI TIẾT

Phần này đặc tả chi tiết 43 yêu cầu chức năng thuộc 10 nhóm chức năng của Hệ thống Quản lý Tài chính Cá nhân. Các yêu cầu chức năng chuyển hóa trực tiếp 5 mục tiêu kỹ thuật tại Mục 2.2 thành các năng lực phần mềm cụ thể. Các yêu cầu này tuân thủ quy tắc tài chính tại Mục 3 (BR-FIN-01 đến BR-FIN-10), phục vụ phân tích thiết kế và cài đặt mã nguồn.

Các yêu cầu chức năng được phân loại theo 10 nhóm chức năng, tuân thủ các mức độ bắt buộc quy định tại Mục 1.7. Trường ca sử dụng liên quan trong từng bảng thông tin liên kết trực tiếp tới ca sử dụng tương ứng tại Mục 6 theo cú pháp `UC-<DOMAIN>-NN`. Toàn bộ kịch bản tương tác người dùng, luồng sự kiện chính và luồng ngoại lệ chi tiết nằm tập trung tại Mục 6.

Bảng chia 43 yêu cầu chức năng theo 10 nhóm chức năng:

| STT | Mã nhóm | Tên nhóm chức năng | Số lượng FR | Dải mã yêu cầu chức năng |
| :---: | :---: | :--- | :---: | :--- |
| 1 | FR-WAL | Nhóm chức năng Ví tiền | 5 | FR-WAL-01 đến FR-WAL-05 |
| 2 | FR-TXN | Nhóm chức năng Ghi nhận thu chi | 5 | FR-TXN-01 đến FR-TXN-05 |
| 3 | FR-TRF | Nhóm chức năng Chuyển tiền nội bộ | 3 | FR-TRF-01 đến FR-TRF-03 |
| 4 | FR-CAT | Nhóm chức năng Danh mục phân loại | 3 | FR-CAT-01 đến FR-CAT-03 |
| 5 | FR-DEBT | Nhóm chức năng Công nợ | 5 | FR-DEBT-01 đến FR-DEBT-05 |
| 6 | FR-SAV | Nhóm chức năng Sổ tiết kiệm | 5 | FR-SAV-01 đến FR-SAV-05 |
| 7 | FR-INV | Nhóm chức năng Tài sản đầu tư | 6 | FR-INV-01 đến FR-INV-06 |
| 8 | FR-FND | Nhóm chức năng Quỹ mục tiêu | 4 | FR-FND-01 đến FR-FND-04 |
| 9 | FR-REP | Nhóm chức năng Thống kê và báo cáo | 4 | FR-REP-01 đến FR-REP-04 |
| 10 | FR-DAT | Nhóm chức năng Lưu trữ và sao lưu dữ liệu | 3 | FR-DAT-01 đến FR-DAT-03 |
| **Tổng** | **10 nhóm chức năng** | | **43 FR** | |

---

### 4.1 FR-WAL: Nhóm chức năng Ví tiền

Nhóm chức năng Ví tiền quản lý các tài khoản thanh toán và nguồn tiền dùng được ngay của người dùng, gồm tiền mặt, tài khoản ngân hàng và ví điện tử. Hệ thống có sẵn một ví tiền mặt ban đầu và cho phép người dùng tạo thêm ví mới. Nhóm chức năng này giữ số dư ví luôn đúng theo quy tắc BR-FIN-02 và cho thấy cơ cấu tiền trong các ví ngay tại thời điểm xem.

#### 4.1.1 FR-WAL-01: Tạo ví tiền mới

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-WAL-01 |
| Tên | Tạo ví tiền mới |
| Nhóm chức năng | Ví tiền (FR-WAL) |
| Ca sử dụng liên quan | UC-WAL-01, UC-WAL-03 |
| Mô tả kỹ thuật | Hệ thống PHẢI tạo đối tượng ví tiền mới để theo dõi tiền mặt, tài khoản ngân hàng hoặc ví điện tử với số dư ban đầu xác định theo quy tắc BR-FIN-01 và BR-FIN-02. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Tên ví | Chuỗi ký tự | Độ dài từ 1 đến 50 ký tự, không trùng lặp với tên ví hiện có (không phân biệt chữ hoa, chữ thường sau khi cắt khoảng trắng thừa). | Bắt buộc |
| Loại ví | Mã phân loại | Thuộc tập hợp: `CASH`, `BANK`, `E_WALLET`. | Bắt buộc |
| Số dư ban đầu | Số tiền (VNĐ) | Giá trị số học chính xác. Điều kiện: $walletInitialBalance \ge 0$. Mặc định: 0.00. | Bắt buộc |
| Mô tả | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra tên ví không trùng: Hệ thống PHẢI so sánh không phân biệt chữ hoa, chữ thường với danh sách ví hiện có sau khi loại bỏ khoảng trắng thừa ở hai đầu. Nếu trùng lặp, Hệ thống PHẢI từ chối tiếp nhận và trả về mã lỗi `ERR-WAL-002` kèm thông báo nghiệp vụ 'Tên ví đã tồn tại'.
2. Kiểm tra số dư ban đầu: Hệ thống PHẢI kiểm tra điều kiện $walletInitialBalance \ge 0$. Nếu số dư ban đầu âm, Hệ thống PHẢI từ chối và trả về mã lỗi `ERR-WAL-003` kèm thông báo nghiệp vụ 'Số dư ban đầu không hợp lệ'.
3. Độ chính xác số học: Hệ thống PHẢI dùng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01.
4. Đặt trạng thái mặc định: Hệ thống PHẢI gán trạng thái hoạt động ban đầu của ví là `ACTIVE`. Khi hệ thống được khởi tạo lần đầu, Hệ thống PHẢI tạo sẵn đúng một ví tiền mặt có tên "Tiền mặt", loại `CASH`, số dư ban đầu 0.00 VNĐ. Người dùng đổi tên được ví này và không xóa được.
5. Đặt số dư gốc: Hệ thống PHẢI tạo số dư gốc $WalletBalance_w(0) = walletInitialBalance$ theo quy tắc BR-FIN-02. Khoản này là số dư khởi điểm của ví, không sinh bản ghi thu chi trong sổ nhật ký giao dịch.
6. Cấp mã và lưu trữ: Hệ thống PHẢI cấp mã duy nhất cho ví mới, lưu bản ghi vào tệp dữ liệu và trả về kết quả thành công.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Đối tượng ví tiền mới với mã ví, trạng thái `ACTIVE`, thời gian tạo. |
| Biến động số dư | Số dư khả dụng của ví mới được đặt bằng $walletInitialBalance$; tổng tiền trong các ví của toàn hệ thống tăng thêm $walletInitialBalance$ nếu $walletInitialBalance > 0$. |
| Thông báo phản hồi | Thông báo tạo ví thành công kèm thông tin chi tiết của ví mới. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-WAL-01.1 | Người dùng nhập tên "MB Bank", loại `BANK`, số dư ban đầu 5,000,000 VNĐ. | Hệ thống tạo thành công ví mới có trạng thái `ACTIVE`, số dư khả dụng hiển thị đúng 5,000,000.00 VNĐ. |
| TC-WAL-01.2 | Đã tồn tại ví "MB Bank", người dùng tạo ví mới với tên "mb bank" hoặc " MB Bank ". | Hệ thống từ chối tạo bản ghi, trả về mã lỗi `ERR-WAL-002` và thông báo nghiệp vụ tên ví đã tồn tại. |
| TC-WAL-01.3 | Người dùng nhập số dư ban đầu -500,000 VNĐ cho ví loại `CASH` hoặc `BANK`. | Hệ thống từ chối tạo bản ghi, trả về mã lỗi `ERR-WAL-003` và thông báo nghiệp vụ số dư ban đầu không được nhỏ hơn 0. |
| TC-WAL-01.4 | Người dùng nhập tên "Timo", loại `E_WALLET`, số dư ban đầu 0.00 VNĐ. | Hệ thống tạo thành công ví mới có trạng thái `ACTIVE`, số dư khả dụng hiển thị đúng 0.00 VNĐ. |
| TC-WAL-01.5 | Hệ thống được khởi tạo lần đầu, người dùng chưa tạo ví nào. | Danh sách ví có đúng một ví "Tiền mặt", loại tiền mặt, trạng thái hoạt động, số dư 0.00 VNĐ, không có ví nào khác. |

#### 4.1.2 FR-WAL-02: Xem danh sách và chi tiết số dư tính lại từ lịch sử giao dịch

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-WAL-02 |
| Tên | Xem danh sách và chi tiết số dư tính lại từ lịch sử giao dịch |
| Nhóm chức năng | Ví tiền (FR-WAL) |
| Ca sử dụng liên quan | UC-WAL-01, UC-WAL-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI truy vấn danh sách các ví tiền và tính lại số dư khả dụng từ lịch sử giao dịch tại thời điểm tra cứu, dựa trên toàn bộ lịch sử biến động dòng tiền theo quy tắc BR-FIN-02. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Bộ lọc trạng thái | Mã phân loại | Thuộc tập hợp: `ACTIVE` (mặc định), `ARCHIVED`, hoặc `ALL` (toàn bộ trạng thái). | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Lọc danh sách ví: Hệ thống PHẢI truy xuất các bản ghi ví trong hệ thống thỏa mãn điều kiện bộ lọc trạng thái.
2. Tính lại số dư ngay khi truy vấn: Với mỗi ví $w$, Hệ thống PHẢI tính số dư khả dụng $WalletBalance_w(T)$ theo công thức tính số dư quy định tại BR-FIN-02. Phép tính PHẢI tính đủ các số hạng dòng tiền vào, dòng tiền ra và khoản điều chỉnh số dư.
3. Quy định không lưu số dư lưu sẵn: Hệ thống CẤM lưu trường số dư lưu sẵn cho phép ghi đè trực tiếp; mọi giá trị số dư hiển thị đều là kết quả tổng hợp ngay lúc hiển thị từ số dư ban đầu và các giao dịch hợp lệ.
4. Tính tổng số dư trong các ví của toàn hệ thống: Hệ thống PHẢI tổng hợp số dư của toàn bộ các ví đang hoạt động (`ACTIVE`) và các ví đã lưu trữ (`ARCHIVED`) có số dư lớn hơn 0.00 VNĐ. Phạm vi này theo QD-124 và QD-207. Tổng số dư của các ví này là $TotalWalletBalance$. Hệ thống PHẢI dùng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01, giữ độ lệch $\Delta = 0.00$ VNĐ.
5. Sắp xếp hiển thị: Hệ thống PHẢI sắp xếp danh sách ví theo tên hoặc theo thứ tự phân loại mặc định (tiền mặt, ngân hàng, ví điện tử).

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Danh sách đối tượng ví gồm mã ví, tên ví, loại ví, trạng thái, số dư khả dụng $WalletBalance_w(T)$, ngày có giao dịch gần nhất. |
| Biến động số dư | Không làm thay đổi số dư trong hệ thống (thao tác đọc). |
| Thông báo phản hồi | Danh sách ví kèm tổng số dư tiền trong các ví của toàn hệ thống hiển thị trên giao diện. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-WAL-02.1 | Cho ví "Timo" có số dư ban đầu 1,000,000 VNĐ, có 1 giao dịch thu 500,000 VNĐ và 1 giao dịch chi 200,000 VNĐ. | Khi truy vấn danh sách ví, số dư hiển thị của ví Timo là chính xác 1,300,000.00 VNĐ. |
| TC-WAL-02.2 | Người dùng chọn bộ lọc `ACTIVE`. | Hệ thống chỉ hiển thị các ví đang hoạt động, bỏ qua các ví ở trạng thái `ARCHIVED`. |
| TC-WAL-02.3 | Tổng số dư toàn hệ thống ở chân danh sách hiển thị. | Số liệu khớp từng đơn vị đồng với tổng số dư của các ví thành phần ($TotalWalletBalance$), sai số làm tròn $\Delta = 0.00$ VNĐ. |

#### 4.1.3 FR-WAL-03: Cập nhật thông tin ví tiền

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-WAL-03 |
| Tên | Cập nhật thông tin ví tiền |
| Nhóm chức năng | Ví tiền (FR-WAL) |
| Ca sử dụng liên quan | UC-WAL-01, UC-WAL-03 |
| Mô tả kỹ thuật | Hệ thống PHẢI cho phép cập nhật tên ví, mô tả, chuyển đổi trạng thái lưu trữ hoặc xóa vĩnh viễn ví chưa phát sinh giao dịch mà không làm sai lệch số dư lịch sử theo quy tắc BR-FIN-02. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã định danh ví | Mã định danh | Mã ví cần cập nhật, bắt buộc tồn tại trong hệ thống. | Bắt buộc |
| Tên ví | Chuỗi ký tự | Độ dài từ 1 đến 50 ký tự, không trùng với tên ví khác. | Tùy chọn |
| Mô tả | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |
| Trạng thái | Mã phân loại | Thuộc tập hợp: `ACTIVE` hoặc `ARCHIVED`. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra tồn tại: Hệ thống PHẢI tìm kiếm ví theo mã ví. Nếu không tìm thấy, Hệ thống PHẢI báo lỗi nghiệp vụ không tìm thấy ví.
2. Kiểm tra tên mới không trùng: Nếu tên ví thay đổi, Hệ thống PHẢI kiểm tra tên mới không trùng với các ví khác trong hệ thống (cắt khoảng trắng thừa, so sánh không phân biệt hoa thường). Nếu trùng lặp, Hệ thống PHẢI báo lỗi nghiệp vụ tên ví đã tồn tại.
3. Điều kiện khi lưu trữ ví (`ARCHIVED`):
  - Cho phép chuyển sang trạng thái `ARCHIVED` khi số dư khả dụng hiện tại $WalletBalance_w(T)$ khác 0.00 VNĐ. Hệ thống hiển thị hộp thoại cảnh báo rủi ro xác nhận nêu rõ số dư còn lại và thông báo số dư này vẫn được tính vào tài sản ròng. Người dùng xác nhận đồng ý mới hoàn tất việc lưu trữ.
  - Ví đang có liên kết với khoản công nợ chưa tất toán theo BR-FIN-09 hoặc sổ tiết kiệm ở trạng thái `ACTIVE` theo BR-FIN-05 không được phép lưu trữ. Nếu vi phạm, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ ví còn liên quan đến khoản tài chính khác nên không thể lưu trữ.
  - Ví tiền mặt có sẵn từ lần khởi tạo đầu tiên không được phép lưu trữ và không được phép xóa. Nếu vi phạm, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ ví tiền mặt có sẵn không thể lưu trữ.
4. Điều kiện khi xóa vĩnh viễn ví:
  - Đối với ví chưa từng phát sinh bất kỳ bản ghi nào trong sổ giao dịch, sổ chuyển tiền, sổ tiết kiệm hoặc liên kết công nợ, Hệ thống cho phép xóa vĩnh viễn bản ghi ví khỏi bộ lưu trữ.
  - Đối với ví đã có dữ liệu giao dịch trong quá khứ, Hệ thống từ chối thao tác xóa vĩnh viễn và yêu cầu người dùng chuyển sang trạng thái lưu trữ.
5. Quy định giữ nguyên số dư: Hệ thống CẤM thay đổi số dư ví bằng chức năng cập nhật thông tin ví. Mọi biến động số dư phải đi qua giao dịch thu chi, chuyển tiền nội bộ hoặc khoản điều chỉnh số dư tại FR-WAL-05.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi đối tượng ví đã cập nhật thông tin trong tệp dữ liệu lưu trữ. |
| Biến động số dư | Không làm thay đổi số dư trong hệ thống. |
| Thông báo phản hồi | Thông báo cập nhật thông tin ví thành công. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-WAL-03.1 | Người dùng đổi tên ví từ "Vietcombank" thành "VCB Digibank". | Hệ thống cập nhật thành công tên mới, toàn bộ giao dịch lịch sử của ví được giữ nguyên. |
| TC-WAL-03.2 | Người dùng yêu cầu chuyển ví "Timo" sang `ARCHIVED` trong khi ví vẫn còn số dư 50,000 VNĐ và không có liên kết công nợ hay sổ tiết kiệm. | Hệ thống hiển thị hộp thoại cảnh báo số dư 50,000 VNĐ vẫn tính vào tài sản ròng; khi người dùng xác nhận, hệ thống cập nhật trạng thái `ARCHIVED` thành công. |
| TC-WAL-03.3 | Người dùng cố gắng thực hiện hành vi sửa đổi trực tiếp trường số dư lưu sẵn mà không qua giao dịch thu chi hoặc chuyển khoản. | Hệ thống ngăn chặn thao tác ghi đè và từ chối cập nhật số dư trực tiếp. |
| TC-WAL-03.4 | Người dùng yêu cầu chuyển ví tiền mặt có sẵn sang `ARCHIVED` khi số dư ví bằng 0.00 VNĐ. | Hệ thống từ chối cập nhật trạng thái và báo lỗi nghiệp vụ ví tiền mặt có sẵn không thể lưu trữ. |
| TC-WAL-03.5 | Người dùng yêu cầu xóa vĩnh viễn một ví vừa tạo chưa từng phát sinh bất kỳ giao dịch thu chi hay liên kết tài chính nào. | Hệ thống gỡ bỏ hoàn toàn bản ghi ví khỏi bộ lưu trữ và giải phóng tên ví. |
| TC-WAL-03.6 | Người dùng yêu cầu xóa vĩnh viễn ví "Timo" đã có 15 giao dịch trong quá khứ. | Hệ thống từ chối xóa vĩnh viễn và hiển thị thông báo hướng dẫn người dùng chuyển ví sang trạng thái lưu trữ. |

#### 4.1.4 FR-WAL-04: Thống kê cơ cấu tài sản theo ví

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-WAL-04 |
| Tên | Thống kê cơ cấu tài sản theo ví |
| Nhóm chức năng | Ví tiền (FR-WAL) |
| Ca sử dụng liên quan | UC-WAL-01, UC-WAL-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI tính tỷ lệ phần trăm số tiền nằm ở từng ví so với tổng tiền trong các ví của toàn hệ thống theo quy tắc BR-FIN-01. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Ngày tính toán | Ngày tháng (YYYY-MM-DD) | Ngày tính toán, mặc định là ngày hiện tại. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Xác định tập ví tính toán cơ cấu: Hệ thống PHẢI lấy danh sách gồm toàn bộ các ví có trạng thái `ACTIVE` và các ví có trạng thái `ARCHIVED` có số dư $WalletBalance_w > 0.00$ VNĐ. Ví ở trạng thái `ARCHIVED` có số dư bằng 0.00 VNĐ không đưa vào báo cáo cơ cấu ví.

2. Tính tổng số dư trong các ví:
$$TotalWalletBalance = \sum_{w \in ActiveWallets} WalletBalance_w + \sum_{w \in ArchivedWallets, WalletBalance_w > 0} WalletBalance_w$$

3. Tính tỷ lệ của từng ví theo công thức:
$$Rate_w = \left( \frac{WalletBalance_w}{TotalWalletBalance} \right) \times 100\%$$
(Áp dụng khi $TotalWalletBalance > 0$; khi $TotalWalletBalance = 0$, mặc định $Rate_w = 0.00\%$). Với ví có trạng thái `ARCHIVED`, tên ví hiển thị kèm nhãn (Đã lưu trữ).

4. Độ chính xác của phép tính: Hệ thống PHẢI chia bằng số thập phân chính xác với tối thiểu 4 chữ số thập phân, áp dụng quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01. Kết quả hiển thị định dạng với 2 chữ số thập phân kèm ký hiệu "%".

5. Xử lý an toàn khi tổng số dư bằng 0: Khi $TotalWalletBalance = 0$, Hệ thống PHẢI gán tỷ lệ của mọi ví bằng 0.00%, không thực hiện phép chia cho 0 để tránh xảy ra lỗi chia cho 0.
6. Sắp xếp kết quả: Hệ thống PHẢI sắp xếp danh sách ví theo thứ tự tỷ lệ giảm dần để cho thấy các tài khoản đang giữ phần lớn số tiền.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bảng cơ cấu tỷ lệ gồm tên ví, loại ví, số dư, tỷ trọng, xếp hạng theo tỷ lệ. Ví đã lưu trữ có số dư lớn hơn 0 hiển thị kèm nhãn (Đã lưu trữ). |
| Biến động số dư | Không làm thay đổi số dư trong hệ thống. |
| Thông báo phản hồi | Bảng số liệu tỷ lệ kèm biểu đồ cơ cấu tài sản theo ví hiển thị trên giao diện. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-WAL-04.1 | Cho ví "MSB" có số dư 6,000,000 VNĐ và ví "ShopeePay" có số dư 4,000,000 VNĐ (tổng 10,000,000 VNĐ). | Hệ thống tính ra tỷ lệ ví MSB là 60.00% và ví ShopeePay là 40.00%. |
| TC-WAL-04.2 | Khi tổng số dư toàn bộ các ví bằng 0.00 VNĐ. | Tỷ lệ hiển thị của từng ví là 0.00%, hệ thống không xảy ra lỗi chia cho 0. |
| TC-WAL-04.3 | Cho 3 ví có số dư lần lượt 1,000,000 VNĐ, 1,000,000 VNĐ và 1,000,000 VNĐ (tổng 3,000,000 VNĐ). | Tỷ lệ mỗi ví được tính là 33.33%, tổng các tỷ lệ xấp xỉ không làm sai lệch số dư thực tế. |
| TC-WAL-04.4 | Cho ví hoạt động có số dư 9,950,000 VNĐ và một ví đã lưu trữ có số dư 50,000 VNĐ (tổng 10,000,000 VNĐ). | Hệ thống hiển thị ví lưu trữ thành một dòng riêng kèm nhãn (Đã lưu trữ) với tỷ lệ 0.50%, tổng các tỷ lệ đạt đúng 100.00%. |

#### 4.1.5 FR-WAL-05: Điều chỉnh số dư ví

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-WAL-05 |
| Tên | Điều chỉnh số dư ví |
| Nhóm chức năng | Ví tiền (FR-WAL) |
| Ca sử dụng liên quan | UC-WAL-04 |
| Mô tả kỹ thuật | Hệ thống PHẢI ghi nhận một khoản điều chỉnh số dư có thể truy vết để đưa số dư khả dụng của ví về đúng giá trị thực tế quan sát được theo quy tắc BR-FIN-02. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã định danh ví | Mã định danh | Mã ví cần điều chỉnh, ví ở trạng thái `ACTIVE`, loại ví thuộc {`CASH`, `BANK`, `E_WALLET`}. | Bắt buộc |
| Số dư quan sát | Số tiền (VNĐ) | Số dư thực tế người dùng quan sát được, dùng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01. | Bắt buộc |
| Lý do điều chỉnh | Chuỗi ký tự | Lý do điều chỉnh, độ dài từ 1 đến 255 ký tự sau khi cắt khoảng trắng. | Bắt buộc |
| Thời điểm điều chỉnh | Ngày tháng (YYYY-MM-DD) | Ngày ghi nhận điều chỉnh. Mặc định: ngày hiện tại. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra tồn tại và trạng thái ví: Hệ thống PHẢI tìm ví theo mã ví. Nếu không tồn tại, Hệ thống PHẢI báo lỗi nghiệp vụ không tìm thấy ví. Nếu ví ở trạng thái `ARCHIVED`, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ ví đã lưu trữ không thể điều chỉnh.
2. Kiểm tra loại ví hợp lệ: Nếu loại ví không thuộc tập hợp {`CASH`, `BANK`, `E_WALLET`}, hệ thống từ chối và báo lỗi nghiệp vụ loại ví không hỗ trợ điều chỉnh số dư.
3. Kiểm tra lý do bắt buộc: Nếu lý do điều chỉnh rỗng hoặc chỉ chứa khoảng trắng, hệ thống từ chối và báo lỗi nghiệp vụ thiếu lý do điều chỉnh.
4. Tính giá trị chênh lệch điều chỉnh theo BR-FIN-02:
$$Adjustment_w = walletObservedBalance - WalletBalance_w(\text{trước})$$
5. Điều kiện số dư sau điều chỉnh: Nếu số dư quan sát < 0, hệ thống từ chối và báo lỗi nghiệp vụ số dư điều chỉnh không hợp lệ.
6. Khi $Adjustment_w = 0$: Hệ thống PHẢI từ chối ghi nhận và báo lỗi nghiệp vụ số dư quan sát trùng số dư hiện tại, không tạo khoản ghi rỗng.
7. Ghi nhận khoản điều chỉnh: Hệ thống PHẢI tạo bản ghi không được sửa gồm mã ví, số dư trước điều chỉnh $WalletBalance_w(\text{trước})$, số dư sau điều chỉnh (bằng số dư quan sát do người dùng nhập), giá trị chênh lệch điều chỉnh $Adjustment_w$, lý do điều chỉnh, thời điểm điều chỉnh. Cập nhật lại số dư của ví theo công thức BR-FIN-02.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi điều chỉnh lưu vào danh sách bản ghi `adjustments` kèm số dư ví sau điều chỉnh. |
| Biến động số dư | Số dư ví thay đổi đúng bằng giá trị $Adjustment_w$; tài sản ròng $NetWorth$ biến động tương ứng. |
| Thông báo phản hồi | Thông báo điều chỉnh số dư thành công kèm số dư trước và sau điều chỉnh. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-WAL-05.1 | Cho ví "Tiền mặt" có số dư hệ thống ghi nhận 2,000,000.00 VNĐ, thực tế đếm được 1,750,000.00 VNĐ. Người dùng điều chỉnh với lý do "Kiểm kê tiền mặt định kỳ". | Hệ thống tạo khoản điều chỉnh số dư $Adjustment = -250,000.00$ VNĐ, số dư ví cập nhật còn 1,750,000.00 VNĐ. |
| TC-WAL-05.2 | Người dùng gửi yêu cầu điều chỉnh với trường lý do điều chỉnh để trống. | Hệ thống từ chối và báo lỗi nghiệp vụ thiếu lý do điều chỉnh. |
| TC-WAL-05.3 | Người dùng gửi yêu cầu điều chỉnh với số dư quan sát bằng đúng số dư hiện tại của ví. | Hệ thống từ chối ghi nhận và báo lỗi nghiệp vụ số dư quan sát trùng số dư hiện tại. |
| TC-WAL-05.4 | Người dùng gửi yêu cầu điều chỉnh trên một ví đang ở trạng thái `ARCHIVED`. | Hệ thống từ chối và báo lỗi nghiệp vụ ví đã lưu trữ không thể điều chỉnh. |

### 4.2 FR-TXN: Nhóm chức năng Ghi nhận thu chi

Nhóm chức năng Ghi nhận thu chi quản lý sổ nhật ký các luồng tiền xảy ra hàng ngày của người dùng. Nhóm chức năng này kết nối biến động số dư với cấu trúc phân loại thu chi 3 cấp và bảo đảm số dư ví phản ánh chính xác theo quy tắc BR-FIN-02.

#### 4.2.1 FR-TXN-01: Tạo mới giao dịch thu nhập

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-TXN-01 |
| Tên | Tạo mới giao dịch thu nhập |
| Nhóm chức năng | Ghi nhận thu chi (FR-TXN) |
| Ca sử dụng liên quan | UC-TXN-01 |
| Mô tả kỹ thuật | Hệ thống PHẢI ghi nhận khoản thu nhập vào sổ nhật ký giao dịch, làm tăng số dư ví tiếp nhận và phân loại vào danh mục thu nhập tương ứng theo quy tắc BR-FIN-01, BR-FIN-02 và BR-FIN-04. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Ngày giao dịch | Ngày tháng (YYYY-MM-DD) | Định dạng YYYY-MM-DD, không vượt quá ngày hiện tại. Mặc định: ngày hiện tại. | Bắt buộc |
| Số tiền | Số tiền (VNĐ) | Giá trị số tiền thu nhập, yêu cầu $amount > 0$. | Bắt buộc |
| Mã định danh ví | Mã định danh | Mã ví nhận tiền, ví phải tồn tại và ở trạng thái `ACTIVE`. | Bắt buộc |
| Mã định danh danh mục | Mã định danh | Mã danh mục thu nhập đang dùng, danh mục phải thuộc nhánh có loại Thu nhập (`Income`). | Bắt buộc |
| Ghi chú | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra số tiền: Hệ thống PHẢI kiểm tra điều kiện $amount > 0$. Nếu $amount \le 0$, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ số tiền giao dịch không hợp lệ. Làm tròn $amount$ về 2 chữ số thập phân, dùng số thập phân chính xác và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01.
2. Kiểm tra ví nhận tiền: Hệ thống PHẢI tìm kiếm ví theo mã ví. Nếu không tồn tại hoặc ví có trạng thái `ARCHIVED`, Hệ thống PHẢI báo lỗi nghiệp vụ ví không ở trạng thái hoạt động.
3. Kiểm tra danh mục thu nhập: Hệ thống PHẢI truy vấn danh mục theo mã danh mục. Kiểm tra danh mục phải thuộc nhánh loại Thu nhập (`Income`) theo quy tắc BR-FIN-04. Nếu danh mục thuộc loại Chi tiêu (`Expense`), Hệ thống PHẢI báo lỗi nghiệp vụ loại danh mục không khớp. Nếu danh mục đã xóa, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ danh mục không còn được dùng cho giao dịch mới.
4. Tạo bản ghi giao dịch: Hệ thống PHẢI lưu bản ghi mới vào danh sách bản ghi `transactions` với mã duy nhất, loại `Income`, ngày, số tiền, ví nhận và danh mục. Nhóm được xác định từ nhóm hiện tại của danh mục, không lưu riêng trong giao dịch.
5. Cập nhật số dư ví: Hệ thống PHẢI cộng trực tiếp số tiền thu nhập vào số dư ví theo quy tắc BR-FIN-02:
$$WalletBalance_w(\text{sau}) = WalletBalance_w(\text{trước}) + amount$$
Tổng tiền trong các ví của toàn hệ thống tăng tương ứng.
6. Nguồn ghi khoản thu từ tài sản đầu tư: Khoản thu từ tài sản đầu tư được ghi qua sự kiện Nhận tiền mặt theo FR-INV-05, không ghi bằng giao dịch thu nhập, để thu nhập tháng không bị tính hai lần. Hệ thống không tự kiểm tra điều này khi người dùng ghi giao dịch thu nhập.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi giao dịch thu nhập mới lưu vào danh sách bản ghi `transactions` kèm mã giao dịch. |
| Biến động số dư | Số dư ví nhận tiền tăng $amount$; tổng tiền trong các ví của toàn hệ thống tăng $amount$. |
| Thông báo phản hồi | Thông báo ghi nhận giao dịch thu nhập thành công kèm thông tin chi tiết. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-TXN-01.1 | Cho ví "MB Bank" có số dư 10,000,000 VNĐ. Người dùng ghi nhận thu nhập lương 25,000,000 VNĐ vào ví này. | Hệ thống ghi nhận thành công, số dư ví MB Bank tăng lên đúng 35,000,000.00 VNĐ. |
| TC-TXN-01.2 | Người dùng nhập số tiền thu nhập bằng 0 hoặc số âm (-2,000,000 VNĐ). | Hệ thống từ chối giao dịch, báo lỗi nghiệp vụ số tiền giao dịch không hợp lệ. |
| TC-TXN-01.3 | Người dùng chọn danh mục "Ăn uống" (thuộc loại Chi tiêu (`Expense`)) cho giao dịch thu nhập. | Hệ thống từ chối ghi nhận, báo lỗi nghiệp vụ loại danh mục không khớp. |
| TC-TXN-01.4 | Người dùng ghi giao dịch thu nhập vào một danh mục thu nhập đã xóa. | Hệ thống từ chối ghi nhận, báo lỗi nghiệp vụ danh mục không còn được dùng cho giao dịch mới. |
| TC-TXN-01.5 | Người dùng chọn ví đã lưu trữ (`ARCHIVED`) làm ví nhận tiền. | Hệ thống từ chối và báo lỗi nghiệp vụ ví không ở trạng thái hoạt động. |

#### 4.2.2 FR-TXN-02: Tạo mới giao dịch chi tiêu

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-TXN-02 |
| Tên | Tạo mới giao dịch chi tiêu |
| Nhóm chức năng | Ghi nhận thu chi (FR-TXN) |
| Ca sử dụng liên quan | UC-TXN-01 |
| Mô tả kỹ thuật | Hệ thống PHẢI ghi nhận khoản chi tiêu tiêu dùng hoặc thanh toán sinh hoạt, kiểm tra số dư ví chi tiền, làm giảm số dư của ví và phân loại vào danh mục chi tiêu tương ứng theo quy tắc BR-FIN-01, BR-FIN-02 và BR-FIN-04. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Ngày giao dịch | Ngày tháng (YYYY-MM-DD) | Mặc định là ngày hiện tại. | Bắt buộc |
| Số tiền | Số tiền (VNĐ) | Giá trị số tiền chi, yêu cầu $amount > 0$. | Bắt buộc |
| Mã định danh ví | Mã định danh | Mã ví xuất tiền, ví phải tồn tại và ở trạng thái `ACTIVE`. | Bắt buộc |
| Mã định danh danh mục | Mã định danh | Mã danh mục chi tiêu đang dùng, danh mục phải thuộc nhánh có loại Chi tiêu (`Expense`). | Bắt buộc |
| Ghi chú | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra số tiền: Hệ thống PHẢI kiểm tra điều kiện $amount > 0$. Nếu $amount \le 0$, Hệ thống PHẢI báo lỗi nghiệp vụ số tiền giao dịch không hợp lệ. Làm tròn $amount$ về 2 chữ số thập phân, dùng số thập phân chính xác và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01.
2. Kiểm tra ví chi tiền: Hệ thống PHẢI tìm kiếm ví theo mã ví. Nếu không tồn tại hoặc ví có trạng thái `ARCHIVED`, Hệ thống PHẢI báo lỗi nghiệp vụ ví không ở trạng thái hoạt động.
3. Kiểm tra danh mục chi tiêu: Hệ thống PHẢI truy vấn danh mục theo mã danh mục. Kiểm tra danh mục phải thuộc nhánh loại Chi tiêu (`Expense`) theo BR-FIN-04. Nếu danh mục thuộc loại Thu nhập (`Income`), Hệ thống PHẢI báo lỗi nghiệp vụ loại danh mục không khớp. Nếu danh mục đã xóa, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ danh mục không còn được dùng cho giao dịch mới.
4. Kiểm tra khả năng chi trả của ví:
  - Với ví thông thường (`CASH`, `BANK`, `E_WALLET`): Hệ thống PHẢI kiểm tra quy tắc số dư luôn không âm theo BR-FIN-02:
   $$WalletBalance_{wallet}(\text{trước}) - amount \ge 0$$
   Nếu $WalletBalance_{wallet}(\text{trước}) < amount$, Hệ thống PHẢI từ chối giao dịch và báo lỗi nghiệp vụ số dư ví không đủ.
5. Tạo bản ghi giao dịch: Hệ thống PHẢI lưu bản ghi mới vào danh sách bản ghi `transactions` với mã giao dịch, loại `Expense`, ngày, số tiền, ví chi và danh mục. Nhóm được xác định từ nhóm hiện tại của danh mục, không lưu riêng trong giao dịch.
6. Cập nhật số dư ví: Hệ thống PHẢI trừ trực tiếp số tiền chi tiêu khỏi số dư ví theo quy tắc BR-FIN-02:
$$WalletBalance_w(\text{sau}) = WalletBalance_w(\text{trước}) - amount$$
Tổng tiền trong các ví của toàn hệ thống giảm tương ứng.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi giao dịch chi tiêu mới được lưu vào danh sách bản ghi `transactions` kèm mã giao dịch. |
| Biến động số dư | Số dư ví chi tiền giảm $amount$; tổng tiền trong các ví của toàn hệ thống giảm $amount$. |
| Thông báo phản hồi | Thông báo ghi nhận giao dịch chi tiêu thành công. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-TXN-02.1 | Cho ví "Tiền mặt" có số dư 500,000 VNĐ. Người dùng ghi nhận chi tiêu 120,000 VNĐ cho danh mục "Ăn trưa". | Hệ thống ghi nhận thành công, số dư ví Tiền mặt còn lại 380,000.00 VNĐ. |
| TC-TXN-02.2 | Cho ví "Tiền mặt" có số dư 200,000 VNĐ. Người dùng nhập khoản chi 350,000 VNĐ. | Hệ thống từ chối giao dịch, báo lỗi nghiệp vụ số dư ví không đủ, thông báo số dư ví không đủ chi trả. |
| TC-TXN-02.3 | Cho ví "ShopeePay" loại `E_WALLET` có số dư 0.00 VNĐ. Người dùng ghi nhận chi tiêu mua sắm 2,500,000 VNĐ. | Hệ thống từ chối giao dịch, báo lỗi nghiệp vụ số dư ví không đủ. |
| TC-TXN-02.4 | Người dùng chọn danh mục thuộc nhóm thu nhập (loại Thu nhập (`Income`)) cho giao dịch chi tiêu. | Hệ thống từ chối, báo lỗi nghiệp vụ loại danh mục không khớp. |
| TC-TXN-02.5 | Người dùng ghi giao dịch chi tiêu vào danh mục chi tiêu đã xóa. | Hệ thống từ chối, báo lỗi nghiệp vụ danh mục không còn được dùng cho giao dịch mới. |

#### 4.2.3 FR-TXN-03: Tra cứu và lọc lịch sử giao dịch

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-TXN-03 |
| Tên | Tra cứu và lọc lịch sử giao dịch |
| Nhóm chức năng | Ghi nhận thu chi (FR-TXN) |
| Ca sử dụng liên quan | UC-TXN-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI tìm kiếm, lọc đa tiêu chí và tổng hợp số liệu trên sổ nhật ký giao dịch thu chi để phục vụ so khớp và phân tích tài chính theo quy tắc BR-FIN-01. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Ngày bắt đầu khoảng lọc, Ngày kết thúc khoảng lọc | Ngày tháng (YYYY-MM-DD) | Khoảng ngày giao dịch (mặc định từ ngày đầu tháng đến ngày cuối của tháng hiện tại). Điều kiện: $fromDate \le toDate$. | Tùy chọn |
| Danh sách mã định danh ví | Danh sách chuỗi | Danh sách mã ví cần lọc (mặc định toàn bộ ví). | Tùy chọn |
| Loại giao dịch | Mã phân loại | Thuộc tập hợp: `ALL` (mặc định), `INCOME`, `EXPENSE`. | Tùy chọn |
| Mã định danh nhóm | Mã định danh | Mã nhóm danh mục, gồm cả nhóm đã xóa. | Tùy chọn |
| Mã định danh danh mục | Mã định danh | Mã danh mục chi tiết, gồm cả danh mục đã xóa. | Tùy chọn |
| Từ khóa tìm kiếm | Chuỗi ký tự | Từ khóa tìm kiếm trong ghi chú. | Tùy chọn |
| Chỉ số trang | Số nguyên dương | Số thứ tự trang, bắt buộc $\ge 1$, mặc định 1. | Tùy chọn |
| Số bản ghi mỗi trang | Số nguyên dương | Số bản ghi mỗi trang, trong khoảng [10, 100], mặc định 50. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kết hợp điều kiện lọc: Hệ thống PHẢI áp dụng phép toán logic AND giữa tất cả các tiêu chí lọc được người dùng chọn. Nếu $fromDate > toDate$, Hệ thống PHẢI từ chối truy vấn và báo lỗi nghiệp vụ khoảng ngày không hợp lệ.
2. Tìm kiếm từ khóa: Từ khóa tìm kiếm được so sánh không phân biệt chữ hoa, chữ thường và tìm kiếm chuỗi con trong trường ghi chú.
3. Tổng hợp số liệu tài chính: Trên toàn bộ tập kết quả thỏa mãn điều kiện lọc (trước khi phân trang), Hệ thống PHẢI tính toán các số liệu tổng hợp. Phép tính dùng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01:
  - Tổng thu nhập: $TotalIncome = \sum_{t \in Results, Type=Income} amount_t$
  - Tổng chi tiêu: $TotalExpense = \sum_{t \in Results, Type=Expense} amount_t$
  - Dòng tiền thuần: $NetCashFlow = TotalIncome - TotalExpense$
4. Sắp xếp và phân trang: Hệ thống PHẢI sắp xếp danh sách kết quả mặc định theo ngày giao dịch giảm dần (mới nhất xếp trước), sau đó theo thời điểm tạo giảm dần. Trích xuất đúng số lượng bản ghi theo chỉ số trang và số bản ghi mỗi trang.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Danh sách bản ghi giao dịch của trang hiện tại, kèm tổng số bản ghi và tổng số trang. Tên danh mục của mỗi bản ghi kèm nhãn (đã xóa) nếu danh mục đã xóa. |
| Biến động số dư | Không làm thay đổi số dư trong hệ thống. |
| Thông báo phản hồi | Bộ số liệu thống kê tổng hợp ($TotalIncome$, $TotalExpense$, $NetCashFlow$) kèm danh sách giao dịch hiển thị trên giao diện. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-TXN-03.1 | Người dùng lọc giao dịch từ ngày 01/01/2026 đến 31/01/2026, loại `EXPENSE`. | Hệ thống trả về đúng các khoản chi trong tháng 1/2026, tính ra tổng $TotalExpense$ chính xác với 2 chữ số thập phân. |
| TC-TXN-03.2 | Người dùng nhập từ khóa "tiền điện". | Hệ thống trả về toàn bộ các giao dịch có chứa cụm từ "tiền điện" trong phần ghi chú, không phân biệt chữ hoa hay chữ thường. |
| TC-TXN-03.3 | Người dùng nhập khoảng ngày có ngày bắt đầu khoảng lọc sau ngày kết thúc khoảng lọc. | Hệ thống từ chối truy vấn và báo lỗi nghiệp vụ khoảng ngày không hợp lệ. |

#### 4.2.4 FR-TXN-04: Chỉnh sửa giao dịch thu chi

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-TXN-04 |
| Tên | Chỉnh sửa giao dịch thu chi |
| Nhóm chức năng | Ghi nhận thu chi (FR-TXN) |
| Ca sử dụng liên quan | UC-TXN-03 |
| Mô tả kỹ thuật | Hệ thống PHẢI cập nhật thông tin của giao dịch đã lưu, thực hiện hoàn nguyên tác động số dư ví cũ và áp dụng số dư ví mới cùng lúc; nếu một bước thất bại thì không bước nào có hiệu lực, để dữ liệu tiền trong hệ thống không bị sai lệch theo quy tắc BR-FIN-02. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã giao dịch | Mã định danh | Mã giao dịch cần chỉnh sửa, bắt buộc tồn tại trong hệ thống. | Bắt buộc |
| Ngày giao dịch | Ngày tháng (YYYY-MM-DD) | Ngày giao dịch mới. | Bắt buộc |
| Số tiền | Số tiền (VNĐ) | Số tiền mới, yêu cầu $amount > 0$. | Bắt buộc |
| Mã định danh ví | Mã định danh | Mã ví mới, ví phải ở trạng thái `ACTIVE`. | Bắt buộc |
| Mã định danh danh mục | Mã định danh | Mã danh mục, phải cùng Loại thu chi với giao dịch gốc. Giữ nguyên danh mục cũ được phép kể cả khi đã xóa. Đổi sang danh mục khác thì danh mục mới phải đang dùng. | Bắt buộc |
| Ghi chú | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra tồn tại giao dịch: Hệ thống PHẢI tìm bản ghi theo mã giao dịch. Nếu không tìm thấy, Hệ thống PHẢI báo lỗi nghiệp vụ không tìm thấy giao dịch.
2. Loại giao dịch luôn giữ nguyên: Hệ thống CẤM thay đổi Loại thu chi của giao dịch (không cho phép chuyển giao dịch `Income` thành `Expense` hoặc ngược lại trong thao tác sửa; nếu cần đổi loại, người dùng phải xóa và tạo mới). Mã danh mục mới phải khớp Loại thu chi với giao dịch gốc theo BR-FIN-04.
3. Hoàn nguyên số dư ví cũ:
  - Nếu giao dịch cũ là `Income`: Trừ số tiền cũ khỏi ví cũ. Kiểm tra số dư ví cũ sau khi trừ: với ví thông thường, nếu $WalletBalance_{oldWallet} - amount_{old} < 0$, Hệ thống PHẢI báo lỗi nghiệp vụ số dư ví không đủ và hủy thao tác.
  - Nếu giao dịch cũ là `Expense`: Cộng số tiền cũ trả lại ví cũ: $WalletBalance_{oldWallet} + amount_{old}$.
4. Áp dụng tác động lên ví mới:
  - Nếu giao dịch mới là `Income`: Cộng số tiền mới vào ví mới: $WalletBalance_{newWallet} + amount_{new}$.
  - Nếu giao dịch mới là `Expense`: Kiểm tra số dư ví mới: với ví thông thường, yêu cầu $WalletBalance_{newWallet} - amount_{new} \ge 0$. Nếu không đủ, Hệ thống PHẢI báo lỗi nghiệp vụ số dư ví không đủ và hủy toàn bộ thao tác.
5. Giao dịch gắn danh mục đã xóa: Hệ thống PHẢI cho phép sửa giao dịch cũ gắn với danh mục đã xóa. Người dùng được giữ nguyên danh mục đó. Nếu đổi danh mục thì chỉ được chọn danh mục đang dùng cùng Loại thu chi.
6. Thực hiện đầy đủ các bước: Hệ thống PHẢI thực hiện các bước hoàn nguyên ví cũ, áp dụng ví mới và cập nhật bản ghi giao dịch cùng lúc: cả ba bước cùng có hiệu lực, hoặc không bước nào có hiệu lực. Nếu xảy ra lỗi tại bất kỳ bước nào, toàn bộ trạng thái dữ liệu phải được hoàn nguyên về ban đầu.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi giao dịch đã cập nhật thông tin ngày, số tiền, ví, danh mục và ghi chú. |
| Biến động số dư | Số dư ví cũ và ví mới được điều chỉnh chính xác theo khoản tiền chênh lệch. |
| Thông báo phản hồi | Thông báo cập nhật giao dịch thành công. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-TXN-04.1 | Cho giao dịch chi 100,000 VNĐ từ ví A. Người dùng sửa thành chi 150,000 VNĐ từ ví B (ví B có đủ tiền). | Hệ thống hoàn trả 100,000 VNĐ vào ví A và trừ 150,000 VNĐ khỏi ví B, cập nhật thông tin giao dịch thành công. |
| TC-TXN-04.2 | Người dùng sửa giao dịch chi tiêu tăng số tiền từ 200,000 VNĐ lên 800,000 VNĐ trong khi ví chỉ còn số dư khả dụng 300,000 VNĐ. | Hệ thống từ chối cập nhật, báo lỗi nghiệp vụ số dư ví không đủ, trạng thái giao dịch và số dư ví giữ nguyên vẹn. |
| TC-TXN-04.3 | Người dùng cố gắng thay đổi danh mục sang nhánh Loại thu chi đối lập. | Hệ thống từ chối thao tác, báo lỗi nghiệp vụ loại danh mục không khớp. |
| TC-TXN-04.4 | Giao dịch chi 100,000 VNĐ gắn danh mục "Ăn uống" đã xóa. Người dùng sửa số tiền thành 120,000 VNĐ và giữ nguyên danh mục. | Hệ thống chấp nhận, giao dịch vẫn gắn "Ăn uống" (đã xóa) và số dư ví được điều chỉnh đúng 20,000 VNĐ. |
| TC-TXN-04.5 | Cùng giao dịch trên, người dùng đổi danh mục sang một danh mục chi tiêu đã xóa khác. | Hệ thống từ chối. Đổi sang danh mục chi tiêu đang dùng thì được chấp nhận. |

#### 4.2.5 FR-TXN-05: Xóa giao dịch thu chi

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-TXN-05 |
| Tên | Xóa giao dịch thu chi |
| Nhóm chức năng | Ghi nhận thu chi (FR-TXN) |
| Ca sử dụng liên quan | UC-TXN-04 |
| Mô tả kỹ thuật | Hệ thống PHẢI xóa bản ghi giao dịch khỏi sổ nhật ký và hoàn nguyên số dư của ví tiền liên quan về trạng thái trước khi giao dịch xảy ra theo quy tắc BR-FIN-02. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã giao dịch | Mã định danh | Mã giao dịch cần xóa, bắt buộc tồn tại trong hệ thống. | Bắt buộc |
| Xác nhận | Cờ logic (Đúng / Sai) | Giá trị phải là `true` để xác nhận xóa. | Bắt buộc |

Quy tắc xử lý và điều kiện:

1. Kiểm tra tồn tại giao dịch: Hệ thống PHẢI tìm kiếm theo mã giao dịch. Nếu không tồn tại, Hệ thống PHẢI báo lỗi nghiệp vụ không tìm thấy giao dịch.
2. Kiểm tra điều kiện hoàn nguyên số dư theo BR-FIN-02:
  - Nếu giao dịch cần xóa là `Income`: Việc xóa khoản thu nhập yêu cầu phải trừ $amount$ khỏi ví đã nhận. Hệ thống PHẢI kiểm tra số dư hiện tại của ví: với ví thông thường, nếu $WalletBalance_{wallet} - amount < 0$, việc xóa bị từ chối để giữ số dư luôn không âm, Hệ thống PHẢI báo lỗi nghiệp vụ số dư ví không đủ.
  - Nếu giao dịch cần xóa là `Expense`: Hoàn trả $amount$ vào số dư của ví chi tiền: $WalletBalance_{wallet} = WalletBalance_{wallet} + amount$.
3. Xóa bản ghi: Hệ thống PHẢI xóa bản ghi giao dịch khỏi danh sách bản ghi `transactions`.
4. Cập nhật thống kê: Tính lại các chỉ số thống kê thu chi và tỷ lệ tiết kiệm tháng liên quan. Thao tác hoàn nguyên số dư và xóa bản ghi chỉ được ghi nhận khi cả hai đều thành công.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi giao dịch được loại bỏ khỏi tệp dữ liệu lưu trữ. |
| Biến động số dư | Số dư ví liên quan được phục hồi về trạng thái trước khi giao dịch xảy ra. |
| Thông báo phản hồi | Thông báo xóa giao dịch thành công. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-TXN-05.1 | Cho ví "MB Bank" có số dư 4,500,000 VNĐ sau khi ghi nhận một khoản chi 500,000 VNĐ. Khi người dùng xóa giao dịch chi này. | Số dư ví MB Bank được phục hồi về đúng 5,000,000.00 VNĐ. |
| TC-TXN-05.2 | Cho ví có số dư hiện tại 200,000 VNĐ, người dùng yêu cầu xóa một giao dịch thu nhập 1,000,000 VNĐ trước đó của ví này. | Hệ thống từ chối xóa và báo lỗi nghiệp vụ số dư ví không đủ do việc trừ tiền sẽ làm âm số dư ví. |
| TC-TXN-05.3 | Người dùng gửi yêu cầu xóa nhưng tham số xác nhận mang giá trị sai (`false`). | Hệ thống không thực hiện xóa và giữ nguyên dữ liệu. |

### 4.3 FR-TRF: Nhóm chức năng Chuyển tiền nội bộ

Nhóm chức năng Chuyển tiền nội bộ xử lý việc đưa tiền từ ví này sang ví khác của cùng một người dùng theo nguyên tắc ghi hai vế (trừ ví nguồn, cộng ví đích). Nhóm chức năng này giữ cho tổng tiền trong các ví luôn không đổi theo quy tắc BR-FIN-03 (ngoại trừ trường hợp phát sinh phí chuyển tiền làm giảm tổng tiền đúng bằng khoản phí theo QD-239).

#### 4.3.1 FR-TRF-01: Chuyển tiền giữa hai ví nội bộ

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-TRF-01 |
| Tên | Chuyển tiền giữa hai ví nội bộ |
| Nhóm chức năng | Chuyển tiền nội bộ (FR-TRF) |
| Ca sử dụng liên quan | UC-TRF-01 |
| Mô tả kỹ thuật | Hệ thống PHẢI áp dụng ghi hai vế để chuyển tiền từ ví nguồn sang ví đích, giữ nguyên tổng tiền trong các ví (ngoại trừ trường hợp phát sinh phí theo QD-239) theo quy tắc BR-FIN-03. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã định danh ví nguồn | Mã định danh | Mã ví xuất tiền, ví phải ở trạng thái `ACTIVE`. | Bắt buộc |
| Mã định danh ví đích | Mã định danh | Mã ví nhận tiền, ví phải ở trạng thái `ACTIVE`, $toWalletId \ne fromWalletId$. | Bắt buộc |
| Số tiền | Số tiền (VNĐ) | Số tiền chuyển, yêu cầu $amount > 0$. | Bắt buộc |
| Ngày giao dịch | Ngày tháng (YYYY-MM-DD) | Định dạng YYYY-MM-DD, không vượt quá ngày hiện tại. Mặc định: ngày hiện tại. | Bắt buộc |
| Ghi chú | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra số tiền chuyển: Hệ thống PHẢI kiểm tra $amount > 0$. Nếu $amount \le 0$, Hệ thống PHẢI báo lỗi nghiệp vụ số tiền chuyển không hợp lệ. Làm tròn về 2 chữ số thập phân theo BR-FIN-01.
2. Điều kiện hai ví khác nhau: Hệ thống PHẢI kiểm tra $fromWalletId \ne toWalletId$. Nếu ví nguồn trùng ví đích, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ ví nguồn trùng ví đích.
3. Kiểm tra trạng thái ví: Cả hai ví nguồn và đích bắt buộc tồn tại và đang ở trạng thái `ACTIVE`. Nếu một trong hai ví bị `ARCHIVED`, Hệ thống PHẢI báo lỗi nghiệp vụ ví không ở trạng thái hoạt động.
4. Kiểm tra số dư ví nguồn: Theo BR-FIN-02 và BR-FIN-03, với ví nguồn thông thường (`CASH`, `BANK`, `E_WALLET`), số dư khả dụng phải thỏa mãn:
$$WalletBalance_{src}(\text{trước}) - amount \ge 0$$
Nếu số dư không đủ, Hệ thống PHẢI báo lỗi nghiệp vụ số dư ví không đủ.
5. Ghi hai vế đồng thời (trừ ví nguồn, cộng ví đích):
  - Ví nguồn:
$$WalletBalance_{src}(\text{sau}) = WalletBalance_{src}(\text{trước}) - amount$$
  - Ví đích:
$$WalletBalance_{dst}(\text{sau}) = WalletBalance_{dst}(\text{trước}) + amount$$
  - Hai thao tác cập nhật số dư và việc tạo bản ghi chuyển tiền trong danh sách bản ghi `transfers` phải được ghi cùng nhau trong một lần ghi tệp an toàn. Nếu có lỗi, toàn bộ giao dịch phải hoàn nguyên.
6. Giữ nguyên tổng tiền trong các ví: Giao dịch chuyển tiền nội bộ thuần túy không tính vào thu nhập (`Income`) hoặc chi tiêu (`Expense`) của kỳ báo cáo. Tổng số dư toàn bộ các ví trước và sau giao dịch luôn khớp nhau: $\Delta \sum WalletBalance = 0$.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi chuyển tiền mới lưu vào danh sách bản ghi `transfers` với mã duy nhất, ngày, ví nguồn, ví đích, số tiền, ghi chú. |
| Biến động số dư | Ví nguồn giảm $amount$, ví đích tăng $amount$; tổng tiền trong các ví của toàn hệ thống không đổi. |
| Thông báo phản hồi | Thông báo chuyển tiền thành công kèm thông tin chi tiết. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-TRF-01.1 | Cho ví "MB Bank" có 5,000,000 VNĐ và ví "Tiền mặt" có 1,000,000 VNĐ. Người dùng chuyển 2,000,000 VNĐ từ MB Bank sang Tiền mặt. | Sau giao dịch, số dư MB Bank là 3,000,000.00 VNĐ, Tiền mặt là 3,000,000.00 VNĐ, tổng số dư hai ví giữ nguyên 6,000,000.00 VNĐ. |
| TC-TRF-01.2 | Người dùng chọn ví nguồn trùng với ví đích ($w_{src} = w_{dst}$). | Hệ thống từ chối và báo lỗi nghiệp vụ ví nguồn trùng ví đích. |
| TC-TRF-01.3 | Cho ví nguồn có số dư 1,500,000 VNĐ, người dùng thực hiện chuyển 2,000,000 VNĐ. | Hệ thống từ chối và báo lỗi nghiệp vụ số dư ví không đủ. |
| TC-TRF-01.4 | Người dùng nhập số tiền chuyển bằng 0 hoặc số âm (-500,000 VNĐ). | Hệ thống từ chối và báo lỗi nghiệp vụ số tiền chuyển không hợp lệ. |

#### 4.3.2 FR-TRF-02: Ghi nhận phí chuyển tiền

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-TRF-02 |
| Tên | Ghi nhận phí chuyển tiền |
| Nhóm chức năng | Chuyển tiền nội bộ (FR-TRF) |
| Ca sử dụng liên quan | UC-TRF-01, UC-TRF-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI tự động ghi nhận giao dịch chi tiêu độc lập cho khoản phí dịch vụ chuyển tiền tính từ ví nguồn, liên kết với giao dịch chuyển tiền theo quy tắc BR-FIN-03. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã lệnh chuyển tiền | Mã định danh | Mã giao dịch chuyển tiền liên kết. | Bắt buộc |
| Phí giao dịch | Số tiền (VNĐ) | Giá trị phí chuyển tiền, yêu cầu $fee \ge 0$, mặc định 0.00. | Bắt buộc khi có phí |
| Mã định danh danh mục phí | Mã định danh | Mã một danh mục chi tiêu đang dùng. Không có danh mục mặc định. | Bắt buộc khi $fee > 0$ |

Quy tắc xử lý và điều kiện:

1. Kiểm tra năng lực chi trả tổng thể của ví nguồn: Khi $fee > 0$, Hệ thống PHẢI kiểm tra số dư ví nguồn phải đủ cho cả số tiền chuyển và khoản phí:
$$WalletBalance_{src}(\text{trước}) \ge amount + fee$$
  Nếu số dư ví nguồn nhỏ hơn $amount + fee$, Hệ thống PHẢI từ chối toàn bộ giao dịch chuyển tiền và báo lỗi nghiệp vụ số dư ví không đủ.
2. Ghi riêng khoản phí: Khi $fee > 0$, khoản phí không được trừ gộp vào số tiền chuyển $amount$, mà được tách thành 2 giao dịch riêng, lưu cùng lúc và chỉ có hiệu lực khi cả hai đều thành công:
  - Giao dịch chuyển tiền nội bộ số tiền $amount$ từ ví nguồn sang ví đích (tổng tiền trong các ví không đổi).
  - Giao dịch chi tiêu (`Expense`) số tiền $fee$ từ ví nguồn, gán danh mục $feeCategoryId$, lưu trường liên kết tới lệnh chuyển tiền tương ứng.
3. Danh mục của khoản phí: Khi $fee > 0$, Hệ thống PHẢI yêu cầu người dùng chọn một danh mục chi tiêu đang dùng cho khoản phí. Nếu thiếu, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ "Vui lòng chọn danh mục cho khoản phí". Danh mục đã xóa không được chọn. Khi $fee = 0$, không cần danh mục phí.
4. Cập nhật số dư thực tế:
  - Ví nguồn:
$$WalletBalance_{src}(\text{sau}) = WalletBalance_{src}(\text{trước}) - (amount + fee)$$
  - Ví đích:
$$WalletBalance_{dst}(\text{sau}) = WalletBalance_{dst}(\text{trước}) + amount$$
  - Phí chuyển tiền: Ghi khoản chi tiêu phí độc lập theo BR-FIN-03, tính vào tổng chi tiêu (`Expense`) của hệ thống trong kỳ báo cáo.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi chuyển tiền trong danh sách `transfers` và bản ghi giao dịch chi tiêu phí liên kết trong danh sách `transactions`. |
| Biến động số dư | Ví nguồn giảm $amount + fee$, ví đích tăng $amount$; tổng tiền trong các ví của toàn hệ thống giảm đúng bằng $fee$. |
| Thông báo phản hồi | Thông báo chuyển tiền thành công kèm thông tin chi tiết về số tiền đã chuyển và khoản phí đã trừ. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-TRF-02.1 | Cho ví nguồn có số dư 2,000,000 VNĐ, chuyển 1,000,000 VNĐ sang ví đích với phí 5,500 VNĐ. | Sau giao dịch, ví nguồn còn lại chính xác 994,500.00 VNĐ, ví đích tăng 1,000,000.00 VNĐ, và hệ thống ghi nhận một giao dịch chi tiêu 5,500.00 VNĐ vào danh mục phí người dùng đã chọn (ví dụ "Phí ngân hàng"). |
| TC-TRF-02.2 | Cho ví nguồn có đúng 1,000,000 VNĐ, thực hiện chuyển 1,000,000 VNĐ với phí 5,500 VNĐ. | Hệ thống từ chối toàn bộ giao dịch do tổng số tiền cần trừ (1,005,500 VNĐ) vượt quá số dư khả dụng, báo lỗi nghiệp vụ số dư ví không đủ. |
| TC-TRF-02.3 | Chuyển tiền với phí $fee = 0.00$ VNĐ. | Hệ thống chỉ tạo bản ghi chuyển tiền, không sinh bản ghi chi tiêu phí thừa. |
| TC-TRF-02.4 | Chuyển 1,000,000 VNĐ với phí 5,500 VNĐ nhưng không chọn danh mục phí. | Hệ thống từ chối và báo lỗi nghiệp vụ "Vui lòng chọn danh mục cho khoản phí". Số dư hai ví không đổi. |
| TC-TRF-02.5 | Chuyển tiền với phí 5,500 VNĐ và chọn một danh mục đã xóa làm danh mục phí. | Hệ thống từ chối vì danh mục phí phải là danh mục đang dùng. |

#### 4.3.3 FR-TRF-03: Tra cứu và hủy giao dịch chuyển tiền nội bộ

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-TRF-03 |
| Tên | Tra cứu và hủy giao dịch chuyển tiền nội bộ |
| Nhóm chức năng | Chuyển tiền nội bộ (FR-TRF) |
| Ca sử dụng liên quan | UC-TRF-02, UC-TRF-03 |
| Mô tả kỹ thuật | Hệ thống PHẢI hiển thị lịch sử chuyển tiền nội bộ và cho phép hủy bỏ giao dịch chuyển tiền, tự động hoàn nguyên số dư của cả hai ví liên quan và khoản phí đi kèm nếu có theo quy tắc BR-FIN-03. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã lệnh chuyển tiền | Mã định danh | Mã giao dịch chuyển tiền cần hủy, bắt buộc tồn tại trong hệ thống. | Bắt buộc |
| Xác nhận | Cờ logic (Đúng / Sai) | Giá trị phải là `true` để xác nhận hủy. | Bắt buộc |

Quy tắc xử lý và điều kiện:

1. Kiểm tra tồn tại và trạng thái: Hệ thống PHẢI tìm bản ghi chuyển tiền theo mã lệnh chuyển tiền. Nếu không tìm thấy, Hệ thống PHẢI báo lỗi nghiệp vụ không tìm thấy giao dịch chuyển tiền. Nếu giao dịch đã bị hủy trước đó, Hệ thống PHẢI báo lỗi nghiệp vụ giao dịch chuyển tiền đã bị hủy trước đó.
2. Kiểm tra điều kiện hoàn nguyên tại ví đích: Ví đích phải có đủ số dư khả dụng để trừ lại số tiền đã nhận từ giao dịch chuyển tiền:
$$WalletBalance_{dst}(\text{trước}) - amount \ge 0$$
  Nếu ví đích đã chi tiêu dẫn tới số dư hiện tại nhỏ hơn $amount$, Hệ thống PHẢI từ chối hủy và báo lỗi nghiệp vụ số dư ví đích không đủ để hoàn nguyên.
3. Khôi phục số dư của cả hai ví cùng lúc:
  - Ví nguồn (hoàn trả số tiền chuyển và khoản phí nếu có):
$$WalletBalance_{src}(\text{sau}) = WalletBalance_{src}(\text{trước}) + amount + fee$$
  - Ví đích (thu lại số tiền chuyển):
$$WalletBalance_{dst}(\text{sau}) = WalletBalance_{dst}(\text{trước}) - amount$$
  - Xử lý phí liên kết: Nếu giao dịch có bản ghi chi tiêu phí liên quan, số dư phí đã được hoàn nhập vào ví nguồn trong công thức tính lại số dư ví nguồn; Hệ thống cập nhật trạng thái bản ghi chi tiêu phí liên kết sang `CANCELLED` và ghi nhận lịch sử hoàn tác theo `BR-FIN-03`.
4. Cập nhật trạng thái: Hệ thống PHẢI đánh dấu trạng thái bản ghi chuyển tiền thành `CANCELLED`. Việc hoàn nguyên số dư và đổi trạng thái chỉ được ghi nhận khi cả hai đều thành công.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi chuyển tiền và bản ghi chi phí liên kết (nếu có) cập nhật trạng thái `CANCELLED` theo QD-239. |
| Biến động số dư | Ví nguồn được cộng lại $amount + fee$, ví đích bị trừ $amount$; tổng tiền trong các ví trở lại như cũ. |
| Thông báo phản hồi | Thông báo hủy giao dịch chuyển tiền thành công. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-TRF-03.1 | Cho giao dịch chuyển 2,000,000 VNĐ từ ví A sang ví B kèm phí 3,300 VNĐ. Khi người dùng hủy giao dịch thành công. | Ví A được cộng lại 2,003,300.00 VNĐ, ví B bị trừ 2,000,000.00 VNĐ, bản ghi chi tiêu phí bị hủy. |
| TC-TRF-03.2 | Cho ví B sau khi nhận 2,000,000 VNĐ đã chi tiêu hết tiền và chỉ còn lại 500,000 VNĐ. Khi người dùng yêu cầu hủy chuyển tiền. | Hệ thống từ chối và báo lỗi nghiệp vụ số dư ví đích không đủ để hoàn nguyên. |
| TC-TRF-03.3 | Người dùng yêu cầu hủy một giao dịch chuyển tiền đã ở trạng thái `CANCELLED`. | Hệ thống từ chối và báo lỗi nghiệp vụ giao dịch chuyển tiền đã bị hủy trước đó. |

### 4.4 FR-CAT: Nhóm chức năng Danh mục phân loại

Nhóm chức năng Danh mục phân loại quản lý cấu trúc phân cấp cây 3 cấp của hệ thống. Trong đó Loại thu chi cố định còn Nhóm nhu cầu và Danh mục chi tiết do người dùng quản lý. Cây phục vụ việc phân loại chi tiết các giao dịch thu chi và làm nền tảng cho các báo cáo phân tích tài chính theo quy tắc BR-FIN-04.

#### 4.4.1 FR-CAT-01: Tạo cấu trúc phân loại 3 cấp

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-CAT-01 |
| Tên | Tạo cấu trúc phân loại 3 cấp |
| Nhóm chức năng | Danh mục phân loại (FR-CAT) |
| Ca sử dụng liên quan | UC-CAT-01 |
| Mô tả kỹ thuật | Hệ thống PHẢI tạo và giữ cây phân cấp phân loại giao dịch 3 cấp gồm Root, Group và Category, trong đó Root cố định. Hệ thống nạp một lần bộ nhóm và danh mục mẫu ban đầu khi tạo hệ thống lần đầu theo quy tắc BR-FIN-04. Sau đó người dùng tự quản lý nhóm và danh mục. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Không yêu cầu | N/A | Bộ nhóm và danh mục mẫu ban đầu được nạp tự động một lần khi tạo hệ thống lần đầu. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Tạo cây phân cấp 3 tầng: Hệ thống PHẢI tạo và giữ cấu trúc danh mục theo đúng 3 cấp độ:
  - Cấp 1: Phân loại luồng tiền tối cao, gồm hai nhánh phân loại độc lập và cố định: `INCOME` và `EXPENSE`.
  - Cấp 2: Nhóm chi tiêu hoặc thu nhập chức năng (ví dụ nhóm mẫu: Nhu cầu thiết yếu, Nhu cầu cá nhân và giải trí, Nghĩa vụ tài chính và chuyển tiền).
  - Cấp 3: Danh mục chi tiết trực tiếp gắn với từng giao dịch (ví dụ: "Ăn sáng", "Xăng xe", "Tiền điện").
2. Nạp bộ danh mục mẫu ban đầu: Khi tạo cơ sở dữ liệu, Hệ thống PHẢI nạp sẵn cấu trúc mẫu gồm 2 nhánh Root, 7 Groups và 28 Categories. Bộ mẫu bao quát các nhu cầu chi tiêu sinh hoạt cơ bản theo quy định tại BR-FIN-04. Bộ mẫu chỉ nạp một lần và không có chức năng nạp lại. Tên và mã tiếng Anh của nhóm mẫu không được dùng làm điều kiện trong bất kỳ quy tắc nào.
3. Quy tắc phân cấp luôn đúng: Toàn bộ các giao dịch tài chính trong hệ thống chỉ được phép liên kết trực tiếp tới các nút lá ở Cấp 3. Nghiêm cấm ghi giao dịch trực tiếp lên Cấp 1 hoặc Cấp 2.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Cấu trúc cây danh mục hoàn chỉnh lưu trong tệp dữ liệu gồm Root types, Groups và Categories. |
| Biến động số dư | Không làm thay đổi số dư trong hệ thống. |
| Thông báo phản hồi | Cây danh mục hiển thị đầy đủ theo phân cấp trực quan trên giao diện quản trị. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-CAT-01.1 | Hệ thống khởi chạy lần đầu tiên trên tệp dữ liệu mới hoàn toàn. | Cây danh mục được sinh đầy đủ 2 nhánh `Income` và `Expense`, với đúng 7 nhóm mẫu và 28 danh mục chi tiết mẫu. |
| TC-CAT-01.2 | Người dùng tạo giao dịch thu chi mới. | Hệ thống bắt buộc người dùng chọn danh mục ở cấp 3, không cho phép chọn trực tiếp nhóm cha ở cấp 2. |
| TC-CAT-01.3 | Kiểm tra tính kế thừa thuộc tính Loại thu chi. | Toàn bộ các danh mục con trong nhóm "Nhu cầu thiết yếu" đều kế thừa Loại thu chi `Expense` từ nhóm cha. |
| TC-CAT-01.4 | Người dùng đã xóa một nhóm mẫu rồi khởi động lại hệ thống. | Bộ mẫu không được nạp lại. Nhóm đã xóa giữ nguyên trạng thái đã xóa. |

#### 4.4.2 FR-CAT-02: Tùy biến danh mục người dùng

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-CAT-02 |
| Tên | Tùy biến danh mục người dùng |
| Nhóm chức năng | Danh mục phân loại (FR-CAT) |
| Ca sử dụng liên quan | UC-CAT-01, UC-CAT-02, UC-CAT-03 |
| Mô tả kỹ thuật | Hệ thống PHẢI cho phép người dùng bổ sung Nhóm nhu cầu và Danh mục chi tiết mới, cũng như đổi tên chúng, mà không làm dữ liệu bị sai lệch theo quy tắc BR-FIN-04. Việc xóa được quy định tại FR-CAT-03. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Tên | Chuỗi ký tự | Tên nhóm hoặc tên danh mục, độ dài từ 1 đến 50 ký tự. Tên nhóm không trùng nhóm đang dùng khác trong cùng Loại thu chi. Tên danh mục không trùng danh mục đang dùng khác trong cùng nhóm cha. | Bắt buộc |
| Mã định danh nơi chứa | Mã định danh | Khi tạo danh mục: mã nhóm cha, nhóm cha bắt buộc đang dùng. Khi tạo nhóm: Loại thu chi (`Income` hoặc `Expense`). | Bắt buộc |
| Biểu tượng | Chuỗi ký tự | Độ dài tối đa 50 ký tự. | Tùy chọn |
| Mã màu | Chuỗi ký tự | Độ dài tối đa 10 ký tự (mã màu Hex). | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra nơi chứa: Hệ thống PHẢI kiểm tra nơi chứa hợp lệ. Danh mục mới phải thuộc một nhóm cha đang dùng. Nhóm mới phải thuộc một Loại thu chi. Nếu không hợp lệ, Hệ thống PHẢI báo lỗi nghiệp vụ không tìm thấy nơi chứa.
2. Kiểm tra tên không trùng: Hệ thống PHẢI kiểm tra tên mới không trùng lặp với các mục đang dùng cùng cấp (cắt khoảng trắng thừa, so sánh không phân biệt chữ hoa, chữ thường). Nếu trùng, Hệ thống PHẢI báo lỗi nghiệp vụ tên đã tồn tại. Mục đã xóa không tính vào việc kiểm tra này.
3. Tên trùng mục đã xóa khi tạo mới: Nếu tên của nhóm hoặc danh mục đang được tạo mới trùng với một mục đã xóa cùng cấp, Hệ thống PHẢI hỏi người dùng chọn Khôi phục mục cũ (giữ mã cũ và giao dịch cũ) hoặc Tạo mới (mã mới), theo UC-CAT-03.
4. Tạo bản ghi: Hệ thống PHẢI gán mã duy nhất chưa từng dùng, liên kết với nơi chứa và lưu bản ghi.
5. Đổi tên: Hệ thống PHẢI giữ nguyên mã định danh và mọi giao dịch liên kết khi đổi tên một nhóm hoặc danh mục, và áp dụng cùng kiểm tra tên không trùng.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi nhóm hoặc danh mục mới, hoặc bản ghi đã đổi tên. |
| Biến động số dư | Không làm thay đổi số dư trong hệ thống. |
| Thông báo phản hồi | Thông báo tạo hoặc đổi tên thành công. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-CAT-02.1 | Người dùng thêm danh mục "Thú cưng" vào nhóm "Nhu cầu cá nhân và giải trí". | Danh mục mới được tạo thành công và xuất hiện trong danh sách lựa chọn khi ghi nhận chi tiêu. |
| TC-CAT-02.2 | Người dùng thêm danh mục trùng tên với danh mục đã có trong cùng một nhóm. | Hệ thống từ chối và báo lỗi nghiệp vụ tên danh mục đã tồn tại trong nhóm. |
| TC-CAT-02.3 | Người dùng tạo nhóm "Thú cưng" thuộc Loại thu chi `Expense`. | Nhóm mới được tạo thành công và xuất hiện trong danh sách nhóm khi thêm danh mục. |
| TC-CAT-02.4 | Người dùng tạo nhóm trùng tên với nhóm đang dùng trong cùng Loại thu chi. | Hệ thống từ chối và báo lỗi nghiệp vụ tên đã tồn tại. |
| TC-CAT-02.5 | Người dùng tạo danh mục "Học vẽ" trong khi "Học vẽ" đã bị xóa trong cùng nhóm. | Hệ thống hỏi chọn Khôi phục hoặc Tạo mới. Chọn Tạo mới thì tạo danh mục có mã mới. |
| TC-CAT-02.6 | Loại thu chi `Expense` có nhóm "Nhu cầu thiết yếu" và nhóm "Thú cưng". Người dùng đổi tên nhóm "Thú cưng" thành "Nhu cầu thiết yếu". | Hệ thống từ chối và báo lỗi nghiệp vụ tên nhóm đã tồn tại trong loại thu chi. Tên nhóm "Thú cưng" giữ nguyên. |
| TC-CAT-02.7 | Danh mục "Học vẽ" đã bị xóa trong nhóm "Nhu cầu cá nhân và giải trí". Người dùng đổi tên danh mục "Vẽ tay" cùng nhóm thành "Học vẽ". | Hệ thống đổi tên thành công và không hỏi Khôi phục hoặc Tạo mới. Danh mục "Học vẽ" cũ vẫn ở trạng thái đã xóa. |

#### 4.4.3 FR-CAT-03: Kiểm soát toàn vẹn khi cập nhật và xóa danh mục

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-CAT-03 |
| Tên | Kiểm soát toàn vẹn khi cập nhật và xóa danh mục |
| Nhóm chức năng | Danh mục phân loại (FR-CAT) |
| Ca sử dụng liên quan | UC-CAT-02, UC-CAT-03, UC-CAT-04 |
| Mô tả kỹ thuật | Hệ thống PHẢI kiểm soát việc đổi tên, chuyển nhóm, xóa và khôi phục danh mục và nhóm phân loại để giữ lịch sử không bị sai lệch và báo cáo tài chính luôn khớp nhau theo quy tắc BR-FIN-04. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Loại thực thể đích | Mã phân loại | Thuộc tập hợp: `CATEGORY` hoặc `GROUP`. | Bắt buộc |
| Mã định danh mục tiêu | Mã định danh | Mã danh mục hoặc mã nhóm cần thao tác. | Bắt buộc |
| Hành động | Mã phân loại | Thuộc tập hợp: `RENAME` (đổi tên), `CHANGE_PARENT` (chuyển nhóm cha, chỉ áp dụng cho danh mục), `DELETE` (xóa) hoặc `RESTORE` (khôi phục mục đã xóa). | Bắt buộc |
| Tên mới | Chuỗi ký tự | Độ dài từ 1 đến 50 ký tự (bắt buộc khi action là `RENAME`). | Tùy chọn |
| Mã định danh nhóm cha mới | Mã định danh | Mã nhóm cha mới, phải là nhóm đang dùng (áp dụng khi action là `CHANGE_PARENT`). | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra tồn tại đối tượng: Hệ thống PHẢI tìm bản ghi theo mã của đối tượng cần sửa và loại thực thể đích. Nếu không tồn tại, Hệ thống PHẢI báo lỗi nghiệp vụ không tìm thấy danh mục hoặc nhóm danh mục tương ứng.
2. Điều kiện khi xóa (`DELETE`):
  - Người dùng chỉ thấy thao tác Xóa. Hộp xác nhận PHẢI nêu số giao dịch liên quan.
  - Kiểm tra lịch sử giao dịch: Hệ thống PHẢI quét danh sách bản ghi `transactions` để kiểm tra xem danh mục đã từng có bất kỳ giao dịch nào hay chưa.
  - Nếu đã có giao dịch: Hệ thống KHÔNG được xóa vĩnh viễn. Danh mục chuyển sang trạng thái đã xóa, không còn hiện trong danh sách chọn cho giao dịch mới, nhưng giao dịch cũ được giữ nguyên và danh mục vẫn hiện trong báo cáo lịch sử và bộ lọc giao dịch kèm nhãn (đã xóa).
  - Nếu chưa từng có giao dịch: Cho phép xóa vĩnh viễn bản ghi khỏi tệp dữ liệu.
  - Với nhóm: Xóa nhóm kéo theo mọi danh mục con đang dùng, mỗi danh mục xử lý theo các quy tắc trên. Nhóm có giao dịch bên trong thì chuyển sang trạng thái đã xóa, nhóm không có thì xóa vĩnh viễn. Hộp xác nhận PHẢI nêu số danh mục sẽ bị ẩn, số danh mục xóa hẳn và tổng số giao dịch được giữ lại.
  - Nhóm đang dùng cuối cùng của một Loại thu chi không được xóa. Hệ thống PHẢI từ chối theo `ERR-CAT-002`.
3. Điều kiện khi sửa (`RENAME` / `CHANGE_PARENT`):
  - Loại luồng tiền luôn giữ nguyên: Khi đổi tên hoặc cập nhật, cấm thay đổi Loại thu chi của một Nhóm nhu cầu hoặc Danh mục đã có giao dịch liên kết. Khi chuyển nhóm cha, nếu người dùng chuyển danh mục sang một Nhóm nhu cầu thuộc Loại thu chi khác, Hệ thống PHẢI từ chối theo `ERR-CAT-001`.
  - Chuyển nhóm cha: Chỉ áp dụng cho danh mục. Nhóm đích PHẢI là nhóm đang dùng. Nếu nhóm đích đã xóa, Hệ thống PHẢI từ chối theo `ERR-CAT-003`. Tên danh mục không được trùng danh mục đang dùng trong nhóm đích. Hộp xác nhận PHẢI nêu số giao dịch, nhóm cũ, nhóm mới và cảnh báo báo cáo theo nhóm của kỳ đã qua sẽ đổi. Giữ nguyên mã và giao dịch liên kết.
  - Đổi tên: Cho phép đổi tên danh mục hoặc nhóm nếu tên mới không trùng với mục đang dùng cùng cấp trong cùng phạm vi cha. Mục đã xóa không tính vào việc kiểm tra trùng. Việc đổi tên áp dụng trên các hiển thị mới mà không làm thay đổi mã nội bộ (mã danh mục, mã nhóm) và không làm ảnh hưởng các liên kết dữ liệu lịch sử.
4. Điều kiện khi khôi phục (`RESTORE`):
  - Khôi phục giữ nguyên mã cũ và nối lại các giao dịch cũ của mục đó.
  - Hệ thống PHẢI từ chối theo `ERR-CAT-004` nếu đã có mục đang dùng cùng tên trong cùng cấp, hoặc nếu danh mục cần khôi phục thuộc một nhóm còn ở trạng thái đã xóa.
  - Khôi phục nhóm chỉ khôi phục riêng nhóm. Các danh mục con khôi phục riêng từng cái.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi danh mục hoặc nhóm được cập nhật, chuyển sang đã xóa, khôi phục hoặc xóa bỏ khỏi tệp dữ liệu. |
| Biến động số dư | Không làm thay đổi số dư trong hệ thống. |
| Thông báo phản hồi | Thông báo thực hiện thao tác thành công hoặc thông báo từ chối nêu rõ vi phạm quy định dữ liệu. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-CAT-03.1 | Cho danh mục "Ăn uống" đã liên kết với 50 giao dịch chi tiêu trong quá khứ. Khi người dùng chọn Xóa danh mục này. | Hộp xác nhận nêu 50 giao dịch. Sau khi xác nhận, danh mục chuyển sang đã xóa, không còn trong danh sách chọn giao dịch mới. 50 giao dịch cũ giữ nguyên và vẫn hiện trong báo cáo cũ kèm nhãn (đã xóa). |
| TC-CAT-03.2 | Cho danh mục "Học vẽ" vừa được tạo và chưa có giao dịch nào. Khi người dùng chọn xóa. | Hệ thống xóa hoàn toàn bản ghi khỏi tệp dữ liệu. |
| TC-CAT-03.3 | Người dùng xóa nhóm "Nhu cầu cá nhân và giải trí" gồm 5 danh mục con, trong đó 2 danh mục có 60 và 30 giao dịch, 3 danh mục chưa có giao dịch. | Hộp xác nhận nêu 2 danh mục bị ẩn, 3 danh mục xóa hẳn và 90 giao dịch được giữ lại. Sau khi xác nhận, 2 danh mục bị ẩn, 3 danh mục bị xóa, nhóm bị ẩn vì còn 90 giao dịch bên trong. |
| TC-CAT-03.4 | Người dùng cố gắng chuyển danh mục chi tiêu "Xăng xe" sang nhóm thu nhập "Thu nhập từ lao động". | Hệ thống từ chối theo `ERR-CAT-001`. |
| TC-CAT-03.5 | Loại thu chi `Income` chỉ còn một nhóm đang dùng. Người dùng xóa nhóm đó. | Hệ thống từ chối theo `ERR-CAT-002`. |
| TC-CAT-03.6 | Người dùng chuyển danh mục "Xăng xe" sang một nhóm đã xóa. | Hệ thống từ chối theo `ERR-CAT-003`. |
| TC-CAT-03.7 | Danh mục "Ăn uống" đã xóa và nhóm cha của nó cũng đã xóa. Người dùng khôi phục "Ăn uống". | Hệ thống từ chối theo `ERR-CAT-004`. Sau khi khôi phục nhóm cha trước, khôi phục "Ăn uống" thành công và 50 giao dịch cũ được nối lại. |
| TC-CAT-03.8 | Danh mục "Cà phê" có 80 giao dịch, tổng 4,000,000 VNĐ, thuộc nhóm "Nhu cầu thiết yếu". Người dùng chuyển sang nhóm "Nhu cầu cá nhân và giải trí" cùng Loại `Expense`. | Hộp xác nhận nêu 80 giao dịch và nhóm cũ, nhóm mới. Sau khi xác nhận, báo cáo theo nhóm của các kỳ cũ giảm 4,000,000 VNĐ ở "Nhu cầu thiết yếu" và tăng 4,000,000 VNĐ ở "Nhu cầu cá nhân và giải trí". Tổng chi không đổi. |

### 4.5 FR-DEBT: Nhóm chức năng Công nợ

Nhóm chức năng Công nợ theo dõi các khoản nợ phải thu và khoản nợ phải trả giữa người dùng với các cá nhân, tổ chức bên ngoài theo quy tắc BR-FIN-09.

#### 4.5.1 FR-DEBT-01: Ghi nhận khoản cho vay

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-DEBT-01 |
| Tên | Ghi nhận khoản cho vay |
| Nhóm chức năng | Công nợ (FR-DEBT) |
| Ca sử dụng liên quan | UC-DEBT-01 |
| Mô tả kỹ thuật | Hệ thống PHẢI ghi nhận một khoản tiền cho cá nhân hoặc tổ chức khác vay, trừ tiền từ một ví chỉ định và tạo khoản nợ phải thu theo quy tắc BR-FIN-09. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Ngày giao dịch | Ngày tháng (YYYY-MM-DD) | Định dạng YYYY-MM-DD, không vượt quá ngày hiện tại. Mặc định: ngày hiện tại. | Bắt buộc |
| Đối tác liên quan | Mã định danh | Độ dài từ 1 đến 100 ký tự, định danh bên vay nợ. | Bắt buộc |
| Số tiền | Số tiền (VNĐ) | Số dương $amount > 0$, dùng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01. | Bắt buộc |
| Mã định danh ví | Mã định danh | Mã định danh ví xuất tiền cho vay, ví ở trạng thái `ACTIVE`. | Bắt buộc |
| Ngày đến hạn | Ngày tháng (YYYY-MM-DD) | Ngày hẹn trả nợ, $dueDate \ge date$. | Tùy chọn |
| Ghi chú | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra dữ liệu đầu vào: Hệ thống PHẢI kiểm tra $amount > 0$. Nếu $amount \le 0$, Hệ thống PHẢI báo lỗi nghiệp vụ giao dịch công nợ không hợp lệ. Làm tròn $amount$ về 2 chữ số thập phân theo quy tắc BR-FIN-01. Độ dài tên người từ 1 đến 100 ký tự.
2. Kiểm tra ví nguồn: Hệ thống PHẢI tìm kiếm ví theo mã ví. Nếu không tìm thấy hoặc ví ở trạng thái `ARCHIVED`, Hệ thống PHẢI báo lỗi nghiệp vụ ví không ở trạng thái hoạt động.
3. Kiểm tra số dư ví nguồn theo BR-FIN-02: Với ví thông thường (`CASH`, `BANK`, `E_WALLET`), số dư khả dụng phải thỏa mãn:
$$WalletBalance_{wallet}(\text{trước}) - amount \ge 0$$
  Nếu $WalletBalance_{wallet}(\text{trước}) < amount$, Hệ thống PHẢI từ chối giao dịch và báo lỗi nghiệp vụ số dư ví không đủ.
4. Trừ số dư ví nguồn: Hệ thống PHẢI trừ trực tiếp số tiền cho vay khỏi ví theo quy tắc BR-FIN-02:
$$WalletBalance_{wallet}(\text{sau}) = WalletBalance_{wallet}(\text{trước}) - amount$$
5. Ghi nhận tài sản quyền đòi nợ theo BR-FIN-09: Hệ thống PHẢI ghi nhận một khoản nợ phải thu mới vào sổ theo dõi công nợ, làm tăng tổng nợ phải thu của hệ thống:
$$Receivables(\text{sau}) = Receivables(\text{trước}) + amount$$
6. Tạo bản ghi giao dịch công nợ: Lưu bản ghi cho vay (`LEND`) vào danh sách bản ghi `debt` gồm mã duy nhất, ngày, người vay, số tiền, ví nguồn, ngày hạn trả và ghi chú. Giao dịch này không tính vào chi phí sinh hoạt (`Expense`) của kỳ báo cáo.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi công nợ cho vay (`LEND`) lưu vào danh sách bản ghi `debt`. |
| Biến động số dư | Số dư ví nguồn giảm $amount$; tổng nợ phải thu $Receivables$ tăng $amount$; tài sản ròng $NetWorth$ giữ nguyên tại thời điểm ghi nhận. |
| Thông báo phản hồi | Thông báo ghi nhận khoản cho vay thành công kèm thông tin chi tiết. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-DEBT-01.1 | Cho ví "MB Bank" có số dư 10,000,000.00 VNĐ. Người dùng ghi nhận cho người vay A vay 3,000,000.00 VNĐ từ ví này. | Số dư ví MB Bank giảm còn đúng 7,000,000.00 VNĐ; nợ phải thu tăng thêm 3,000,000.00 VNĐ. |
| TC-DEBT-01.2 | Cho ví có số dư 1,000,000 VNĐ. Người dùng nhập khoản cho vay 2,500,000 VNĐ. | Hệ thống từ chối giao dịch và báo lỗi nghiệp vụ số dư ví không đủ. |
| TC-DEBT-01.3 | Người dùng ghi nhận khoản cho vay với số tiền bằng 0 hoặc số âm. | Hệ thống từ chối và báo lỗi nghiệp vụ giao dịch công nợ không hợp lệ. |

#### 4.5.2 FR-DEBT-02: Ghi nhận khoản đi vay

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-DEBT-02 |
| Tên | Ghi nhận khoản đi vay |
| Nhóm chức năng | Công nợ (FR-DEBT) |
| Ca sử dụng liên quan | UC-DEBT-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI ghi nhận một khoản tiền vay từ cá nhân hoặc tổ chức tín dụng, cộng tiền mặt vào một ví chỉ định và xác lập khoản nợ phải trả theo quy tắc BR-FIN-09. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Ngày giao dịch | Ngày tháng (YYYY-MM-DD) | Định dạng YYYY-MM-DD, không vượt quá ngày hiện tại. Mặc định: ngày hiện tại. | Bắt buộc |
| Đối tác liên quan | Mã định danh | Độ dài từ 1 đến 100 ký tự, định danh bên cho vay. | Bắt buộc |
| Số tiền | Số tiền (VNĐ) | Số dương $amount > 0$, dùng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01. | Bắt buộc |
| Mã định danh ví | Mã định danh | Mã định danh ví tiếp nhận tiền vay, ví ở trạng thái `ACTIVE`. | Bắt buộc |
| Ngày đến hạn | Ngày tháng (YYYY-MM-DD) | Ngày hẹn trả nợ, $dueDate \ge date$. | Tùy chọn |
| Ghi chú | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra dữ liệu đầu vào: Hệ thống PHẢI kiểm tra $amount > 0$. Nếu $amount \le 0$, Hệ thống PHẢI báo lỗi nghiệp vụ giao dịch công nợ không hợp lệ. Làm tròn $amount$ về 2 chữ số thập phân theo BR-FIN-01.
2. Kiểm tra ví tiếp nhận: Hệ thống PHẢI tìm kiếm ví theo mã ví. Nếu không tồn tại hoặc ví có trạng thái `ARCHIVED`, Hệ thống PHẢI báo lỗi nghiệp vụ ví không ở trạng thái hoạt động.
3. Cộng số dư ví tiếp nhận theo BR-FIN-02: Hệ thống PHẢI cộng trực tiếp số tiền đi vay vào ví nhận:
$$WalletBalance_{wallet}(\text{sau}) = WalletBalance_{wallet}(\text{trước}) + amount$$
4. Ghi nhận nợ phải trả theo BR-FIN-09: Hệ thống PHẢI ghi nhận một khoản nợ phải trả mới vào sổ theo dõi công nợ, làm tăng tổng nợ phải trả toàn hệ thống:
$$Payables(\text{sau}) = Payables(\text{trước}) + amount$$
5. Tạo bản ghi giao dịch công nợ: Lưu bản ghi đi vay (`BORROW`) vào danh sách bản ghi `debt`. Giao dịch này không tính vào thu nhập (`Income`) của kỳ báo cáo.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi công nợ đi vay (`BORROW`) lưu vào danh sách bản ghi `debt`. |
| Biến động số dư | Số dư ví tiếp nhận tăng $amount$; tổng nợ phải trả $Payables$ tăng $amount$; tài sản ròng $NetWorth$ giữ nguyên. |
| Thông báo phản hồi | Thông báo ghi nhận khoản đi vay thành công kèm thông tin chi tiết. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-DEBT-02.1 | Người dùng vay 20,000,000.00 VNĐ từ ngân hàng và nhận vào ví "Vietcombank". | Số dư ví Vietcombank tăng thêm đúng 20,000,000.00 VNĐ; tổng nợ phải trả $Payables$ tăng 20,000,000.00 VNĐ. |
| TC-DEBT-02.2 | Người dùng chọn ví đang ở trạng thái `ARCHIVED` để nhận tiền vay. | Hệ thống từ chối và báo lỗi nghiệp vụ ví không ở trạng thái hoạt động. |

#### 4.5.3 FR-DEBT-03: Thu lại nợ cho vay

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-DEBT-03 |
| Tên | Thu lại nợ cho vay |
| Nhóm chức năng | Công nợ (FR-DEBT) |
| Ca sử dụng liên quan | UC-DEBT-01 |
| Mô tả kỹ thuật | Hệ thống PHẢI ghi nhận khoản tiền thu lại từ người vay, cộng tiền vào một ví chỉ định và giảm tương ứng số tiền còn phải thu của người vay theo quy tắc BR-FIN-09. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Ngày giao dịch | Ngày tháng (YYYY-MM-DD) | Định dạng YYYY-MM-DD, không vượt quá ngày hiện tại. Mặc định: ngày hiện tại. | Bắt buộc |
| Đối tác liên quan | Mã định danh | Độ dài từ 1 đến 100 ký tự, định danh bên trả nợ. | Bắt buộc |
| Số tiền | Số tiền (VNĐ) | Số dương $amount > 0$, dùng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01. | Bắt buộc |
| Mã định danh ví | Mã định danh | Mã định danh ví tiếp nhận tiền thu nợ, ví ở trạng thái `ACTIVE`. | Bắt buộc |
| Ghi chú | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Xác định số dư phải thu hiện tại của người vay theo BR-FIN-09:
$$Receivable_{person} = \sum Lend_{person} - \sum Collect_{person} - \sum \text{WriteOffLend}_{person}$$
2. Kiểm tra mức thu nợ tối đa: Số tiền thu lại không được vượt quá số dư phải thu hiện có ($amount \le Receivable_{person}$). Nếu vượt quá, Hệ thống PHẢI báo lỗi nghiệp vụ số tiền vượt quá số tiền còn nợ và từ chối giao dịch.
3. Kiểm tra ví nhận tiền: Ví nhận tiền bắt buộc ở trạng thái `ACTIVE`.
4. Cộng số tiền $amount$ vào ví nhận theo BR-FIN-02:
$$WalletBalance_{wallet}(\text{sau}) = WalletBalance_{wallet}(\text{trước}) + amount$$
5. Giảm số dư phải thu đối với người vay theo BR-FIN-09:
$$Receivable_{person}(\text{sau}) = Receivable_{person}(\text{trước}) - amount$$
6. Quy định ghi nhận: Giao dịch thu nợ làm tăng tiền mặt và giảm số tiền mình được đòi, không tính vào thu nhập sinh hoạt (`Income`) của kỳ báo cáo.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi công nợ thu nợ (`COLLECT`) lưu vào danh sách bản ghi `debt`. |
| Biến động số dư | Số dư ví nhận tiền tăng $amount$; số dư phải thu $Receivable_{person}$ giảm $amount$; $NetWorth$ giữ nguyên. |
| Thông báo phản hồi | Thông báo thu nợ thành công kèm số nợ còn lại của người vay. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-DEBT-03.1 | Cho người vay A đang nợ 5,000,000.00 VNĐ. Người dùng ghi nhận A trả nợ 2,000,000.00 VNĐ vào ví Tiền mặt. | Ví Tiền mặt tăng thêm 2,000,000.00 VNĐ; nợ còn lại của A giảm xuống đúng 3,000,000.00 VNĐ. |
| TC-DEBT-03.2 | Người dùng ghi nhận thu nợ 4,000,000.00 VNĐ từ người vay A trong khi A chỉ còn nợ 3,000,000.00 VNĐ. | Hệ thống từ chối giao dịch và báo lỗi nghiệp vụ số tiền vượt quá số tiền còn nợ. |
| TC-DEBT-03.3 | Người dùng thu nợ của một người vay không có tên trong danh sách nợ phải thu. | Hệ thống từ chối và báo lỗi nghiệp vụ không tìm thấy thông tin đối tác công nợ. |

#### 4.5.4 FR-DEBT-04: Trả nợ đi vay

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-DEBT-04 |
| Tên | Trả nợ đi vay |
| Nhóm chức năng | Công nợ (FR-DEBT) |
| Ca sử dụng liên quan | UC-DEBT-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI ghi nhận khoản tiền người dùng thanh toán nợ cho người cho vay, trừ tiền từ ví chỉ định và giảm nợ phải trả theo quy tắc BR-FIN-09. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Ngày giao dịch | Ngày tháng (YYYY-MM-DD) | Định dạng YYYY-MM-DD, không vượt quá ngày hiện tại. Mặc định: ngày hiện tại. | Bắt buộc |
| Đối tác liên quan | Mã định danh | Độ dài từ 1 đến 100 ký tự, định danh người cho vay nhận tiền trả. | Bắt buộc |
| Số tiền | Số tiền (VNĐ) | Số dương $amount > 0$, dùng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01. | Bắt buộc |
| Mã định danh ví | Mã định danh | Mã định danh ví nguồn thanh toán, ví ở trạng thái `ACTIVE`. | Bắt buộc |
| Ghi chú | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Xác định nợ phải trả hiện tại đối với người cho vay theo BR-FIN-09:
$$Payable_{person} = \sum Borrow_{person} - \sum Repay_{person} - \sum \text{WriteOffBorrow}_{person}$$
2. Kiểm tra mức trả nợ tối đa: Số tiền trả nợ không được vượt quá nợ phải trả hiện tại ($amount \le Payable_{person}$). Nếu vượt quá, Hệ thống PHẢI báo lỗi nghiệp vụ số tiền vượt quá số tiền còn nợ và từ chối giao dịch.
3. Kiểm tra số dư ví nguồn theo BR-FIN-02: Hệ thống PHẢI kiểm tra $WalletBalance_{wallet}(\text{trước}) - amount \ge 0$ đối với ví thông thường. Nếu số dư không đủ chi trả, Hệ thống PHẢI báo lỗi nghiệp vụ số dư ví không đủ.
4. Trừ số tiền $amount$ khỏi ví nguồn theo BR-FIN-02:
$$WalletBalance_{wallet}(\text{sau}) = WalletBalance_{wallet}(\text{trước}) - amount$$
5. Giảm nợ phải trả đối với người cho vay theo BR-FIN-09:
$$Payable_{person}(\text{sau}) = Payable_{person}(\text{trước}) - amount$$
6. Quy định ghi nhận: Giao dịch trả nợ làm giảm tiền mặt và giảm nợ phải trả, không tính vào chi phí sinh hoạt (`Expense`) của kỳ báo cáo.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi công nợ trả nợ (`REPAY`) lưu vào danh sách bản ghi `debt`. |
| Biến động số dư | Số dư ví nguồn giảm $amount$; nợ phải trả $Payable_{person}$ giảm $amount$; $NetWorth$ giữ nguyên. |
| Thông báo phản hồi | Thông báo trả nợ thành công kèm số nợ còn lại phải trả đối với người cho vay. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-DEBT-04.1 | Cho khoản nợ người cho vay B là 5,000,000.00 VNĐ và ví Timo có số dư 4,000,000.00 VNĐ. Người dùng trả nợ cho B 2,000,000.00 VNĐ từ ví Timo. | Ví Timo còn lại 2,000,000.00 VNĐ, nợ còn lại đối với B giảm xuống 3,000,000.00 VNĐ. |
| TC-DEBT-04.2 | Người dùng thực hiện trả nợ 6,000,000.00 VNĐ cho người cho vay B trong khi nợ thực tế chỉ còn 5,000,000.00 VNĐ. | Hệ thống từ chối giao dịch và báo lỗi nghiệp vụ số tiền vượt quá số tiền còn nợ. |
| TC-DEBT-04.3 | Người dùng trả nợ 3,000,000.00 VNĐ từ ví có số dư chỉ 1,000,000.00 VNĐ. | Hệ thống từ chối và báo lỗi nghiệp vụ số dư ví không đủ. |

#### 4.5.5 FR-DEBT-05: Xóa nợ khó đòi và miễn trừ nợ

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-DEBT-05 |
| Tên | Xóa nợ khó đòi và miễn trừ nợ |
| Nhóm chức năng | Công nợ (FR-DEBT) |
| Ca sử dụng liên quan | UC-DEBT-03 |
| Mô tả kỹ thuật | Hệ thống PHẢI ghi khoản điều chỉnh cho các khoản xóa nợ khó đòi hoặc các khoản nợ đi vay được người cho vay tha thứ (miễn trừ nợ phải trả), không làm biến động số dư các ví tiền theo quy tắc BR-FIN-09. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Ngày giao dịch | Ngày tháng (YYYY-MM-DD) | Định dạng YYYY-MM-DD, không vượt quá ngày hiện tại. Mặc định: ngày hiện tại. | Bắt buộc |
| Đối tác liên quan | Mã định danh | Độ dài từ 1 đến 100 ký tự, định danh người vay hoặc người cho vay liên quan. | Bắt buộc |
| Loại xử lý nợ | Mã phân loại | Thuộc tập hợp: `WRITE_OFF_LEND` (xóa nợ phải thu) và `WRITE_OFF_BORROW` (miễn trừ nợ phải trả). | Bắt buộc |
| Số tiền | Số tiền (VNĐ) | Số dương $amount > 0$, dùng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01. | Bắt buộc |
| Ghi chú | Chuỗi ký tự | Độ dài từ 1 đến 255 ký tự sau khi cắt khoảng trắng, bắt buộc nhập lý do xử lý nợ. | Bắt buộc |

Quy tắc xử lý và điều kiện:

1. Kiểm tra lý do: Tham số ghi chú bắt buộc chứa nội dung giải trình căn cứ xóa nợ. Nếu để trống, Hệ thống PHẢI báo lỗi nghiệp vụ giao dịch công nợ không hợp lệ.
2. Xử lý giao dịch `WRITE_OFF_LEND`:
  - Kiểm tra hạn mức xóa nợ: $amount \le Receivable_{person}$. Nếu vượt quá số dư phải thu còn lại, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ số tiền vượt quá số tiền còn nợ.
  - Giảm số dư phải thu của người vay theo BR-FIN-09:
$$Receivable_{person}(\text{sau}) = Receivable_{person}(\text{trước}) - amount$$
  - Nếu số dư phải thu sau khi ghi giảm bằng 0.00 VNĐ ($Receivable_{person}(\text{sau}) = 0.00$), Hệ thống PHẢI đánh dấu trạng thái nợ phải thu của người vay này là hoàn tất (`PAID`).
  - Giảm tổng nợ phải thu toàn hệ thống $Receivables$.
  - Điều kiện không dùng tiền mặt: Không thay đổi số dư của bất kỳ ví thanh toán nào theo BR-FIN-02 và BR-FIN-09.
  - Ghi nhận khoản tổn thất tài sản vào báo cáo phân tích tài chính.
3. Xử lý giao dịch `WRITE_OFF_BORROW`:
  - Kiểm tra hạn mức miễn trừ: $amount \le Payable_{person}$. Nếu vượt quá số tiền còn nợ, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ số tiền vượt quá số tiền còn nợ.
  - Giảm số dư phải trả đối với người cho vay theo BR-FIN-09:
$$Payable_{person}(\text{sau}) = Payable_{person}(\text{trước}) - amount$$
  - Nếu số dư phải trả sau khi ghi giảm bằng 0.00 VNĐ ($Payable_{person}(\text{sau}) = 0.00$), Hệ thống PHẢI đánh dấu trạng thái nợ phải trả của người cho vay này là hoàn tất (`PAID`).
  - Giảm tổng nợ phải trả toàn hệ thống $Payables$.
  - Điều kiện không dùng tiền mặt: Không thay đổi số dư của bất kỳ ví tiền nào theo BR-FIN-02 và BR-FIN-09.
  - Ghi nhận khoản thu nhập bất thường do miễn trừ nợ vào báo cáo.
4. Giữ dữ liệu không sai lệch: Cập nhật sổ công nợ và lưu dấu vết để kiểm tra lại trong giao dịch xử lý tệp an toàn.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi công nợ với loại `WRITE_OFF_LEND` hoặc `WRITE_OFF_BORROW` lưu vào danh sách bản ghi `debt`. |
| Biến động số dư | Số dư toàn bộ các ví tiền thanh toán được giữ nguyên; $Receivables$ hoặc $Payables$ giảm tương ứng; $NetWorth$ thay đổi theo biến động quyền đòi nợ hoặc nợ phải trả. |
| Thông báo phản hồi | Thông báo xử lý xóa nợ thành công kèm số tiền còn nợ của người vay hoặc người cho vay liên quan. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-DEBT-05.1 | Người vay A còn nợ 1,000,000.00 VNĐ nhưng mất khả năng thanh toán. Người dùng ghi nhận `WRITE_OFF_LEND` 1,000,000.00 VNĐ kèm lý do. | Nợ phải thu của A về 0.00 VNĐ, trạng thái nợ chuyển sang `PAID`, tổng $Receivables$ giảm 1,000,000.00 VNĐ, số dư mọi ví tiền được giữ nguyên. |
| TC-DEBT-05.2 | Người dùng thực hiện xóa nợ với số tiền lớn hơn số tiền còn nợ của người vay hoặc người cho vay liên quan. | Hệ thống từ chối và báo lỗi nghiệp vụ số tiền vượt quá số tiền còn nợ. |
| TC-DEBT-05.3 | Người dùng thực hiện xóa nợ mà để trống trường lý do. | Hệ thống từ chối và báo lỗi nghiệp vụ giao dịch công nợ không hợp lệ. |
| TC-DEBT-05.4 | Người vay A còn nợ 1,000,000.00 VNĐ. Người dùng ghi nhận `WRITE_OFF_LEND` 400,000.00 VNĐ kèm lý do. | Nợ phải thu của A giảm còn 600,000.00 VNĐ, trạng thái nợ giữ nguyên `ACTIVE`, tổng $Receivables$ giảm 400,000.00 VNĐ, số dư mọi ví tiền được giữ nguyên. |

### 4.6 FR-SAV: Nhóm chức năng Sổ tiết kiệm

Nhóm chức năng Sổ tiết kiệm theo dõi các khoản tiền gửi tiết kiệm có kỳ hạn và không kỳ hạn tại các ngân hàng thương mại. Hệ thống tự động tính tiền lãi dự kiến theo quy tắc BR-FIN-05 và xử lý tất toán đúng hạn hoặc trước hạn.

#### 4.6.1 FR-SAV-01: Mở sổ tiết kiệm mới

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-SAV-01 |
| Tên | Mở sổ tiết kiệm mới |
| Nhóm chức năng | Sổ tiết kiệm (FR-SAV) |
| Ca sử dụng liên quan | UC-SAV-01, UC-SAV-04 |
| Mô tả kỹ thuật | Hệ thống PHẢI trừ một khoản tiền từ ví thanh toán khả dụng để mở sổ tiết kiệm ngân hàng có kỳ hạn hoặc không kỳ hạn, chuyển đổi cơ cấu tài sản từ tiền mặt sang tiền gửi tiết kiệm theo quy tắc BR-FIN-05. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Tên ngân hàng | Chuỗi ký tự | Độ dài từ 1 đến 100 ký tự, định danh tên ngân hàng thương mại mở sổ. | Bắt buộc |
| Số vốn gốc | Số tiền (VNĐ) | Giá trị số tiền gửi gốc, yêu cầu $principal > 0$, làm tròn về 2 chữ số thập phân theo BR-FIN-01. | Bắt buộc |
| Lãi suất | Số thập phân chính xác | Lãi suất cam kết (%/năm), yêu cầu $Rate \ge 0$, 2 hoặc 4 chữ số thập phân. | Bắt buộc |
| Kỳ hạn | Số nguyên dương hoặc mã phân loại | Số tháng gửi tùy chọn ($TermMonths$, số nguyên dương) hoặc mã `unlimited` (không kỳ hạn), theo BR-FIN-05. | Bắt buộc |
| Phương thức tính lãi ($interestMethod$) | Mã phân loại | Thuộc tập hợp: `SIMPLE`, `COMPOUND_MONTHLY`, `PREPAID`, `POSTPAID` theo BR-FIN-05. Mặc định `SIMPLE` khi kỳ hạn khác `unlimited` và không có chỉ định riêng. | Tùy chọn |
| Mã định danh ví | Mã định danh | Mã định danh ví xuất tiền gửi, ví ở trạng thái `ACTIVE`. | Bắt buộc |
| Ngày bắt đầu kỳ hạn | Ngày tháng (YYYY-MM-DD) | Ngày bắt đầu tính lãi. Mặc định: ngày hiện tại. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra tham số tài chính: Hệ thống PHẢI kiểm tra $principal > 0$, $Rate \ge 0$. Làm tròn về 2 chữ số thập phân theo BR-FIN-01.
2. Kiểm tra kỳ hạn và phương thức lãi: $TermMonths \in \mathbb{Z}^{+}$ khi khoản gửi có kỳ hạn xác định, hoặc khi khoản gửi không kỳ hạn (`unlimited`). Phương thức tính lãi $interestMethod$ thuộc {`SIMPLE`, `COMPOUND_MONTHLY`, `PREPAID`, `POSTPAID`}. Khi khoản gửi không kỳ hạn (`unlimited`), bắt buộc phương thức tính lãi là `SIMPLE`.
3. Kiểm tra số dư ví nguồn theo BR-FIN-02: Ví nguồn phải ở trạng thái `ACTIVE`. Kiểm tra $WalletBalance_{wallet} - principal \ge 0$. Nếu không đủ, Hệ thống PHẢI báo lỗi nghiệp vụ số dư ví không đủ.
4. Trừ tiền mặt: Trừ $principal$ khỏi ví nguồn:
$$WalletBalance_{wallet}(\text{sau}) = WalletBalance_{wallet}(\text{trước}) - principal$$
5. Tạo sổ tiết kiệm theo BR-FIN-05: Tạo bản ghi sổ tiết kiệm mới với trạng thái ban đầu là `ACTIVE`. Tổng tài sản tiết kiệm $Savings$ của hệ thống tăng thêm một khoản bằng $principal$. Tài sản ròng $NetWorth$ được giữ nguyên tại thời điểm mở sổ.
6. Tính toán lịch đáo hạn: Xác định ngày đáo hạn dự kiến $MaturityDate$ bằng cách cộng $TermMonths$ tháng lịch vào $startDate$ theo quy tắc ngày đáo hạn tại BR-FIN-05; số ngày kỳ hạn $TermDays$ là số ngày lịch thực tế từ $startDate$ đến $MaturityDate$.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Đối tượng sổ tiết kiệm mới với mã sổ tiết kiệm, trạng thái `ACTIVE`, ngày mở, ngày đáo hạn. |
| Biến động số dư | Số dư ví xuất tiền giảm $principal$; tổng tài sản sổ tiết kiệm $Savings$ tăng $principal$; tài sản ròng $NetWorth$ giữ nguyên. |
| Thông báo phản hồi | Thông báo mở sổ tiết kiệm thành công kèm thông tin chi tiết. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-SAV-01.1 | Cho ví "Vietcombank" có 100,000,000 VNĐ. Người dùng mở sổ tiết kiệm tại Vietcombank số tiền 50,000,000 VNĐ, kỳ hạn $TermMonths = 6$, lãi suất 5.50%/năm. | Số dư ví Vietcombank còn lại 50,000,000.00 VNĐ, sổ tiết kiệm mới có trạng thái `ACTIVE`, tổng tài sản tiết kiệm tăng 50,000,000.00 VNĐ. |
| TC-SAV-01.2 | Người dùng mở sổ tiết kiệm với số tiền vượt quá số dư khả dụng của ví nguồn. | Hệ thống từ chối và báo lỗi nghiệp vụ số dư ví không đủ. |
| TC-SAV-01.3 | Người dùng nhập lãi suất âm (-1.00%/năm) hoặc số tiền gửi bằng 0. | Hệ thống từ chối và báo lỗi nghiệp vụ thông số lãi suất hoặc số tiền gửi không hợp lệ. |
| TC-SAV-01.4 | Người dùng chọn kỳ hạn không kỳ hạn (`unlimited`) nhưng cố tình chọn phương thức lãi ghép tháng. | Hệ thống chuyển về hoặc ép buộc phương thức lãi đơn `SIMPLE` theo quy định BR-FIN-05. |

#### 4.6.2 FR-SAV-02: Theo dõi kỳ hạn và tiền lãi dự kiến sổ tiết kiệm

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-SAV-02 |
| Tên | Theo dõi kỳ hạn và tiền lãi dự kiến sổ tiết kiệm |
| Nhóm chức năng | Sổ tiết kiệm (FR-SAV) |
| Ca sử dụng liên quan | UC-SAV-04, UC-SAV-05 |
| Mô tả kỹ thuật | Hệ thống PHẢI tự động tính số tiền lãi lũy kế dự kiến theo từng ngày gửi và theo dõi thời gian còn lại cho tới ngày đáo hạn của từng sổ tiết kiệm theo quy tắc BR-FIN-05. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã định danh sổ tiết kiệm | Mã định danh | Mã sổ tiết kiệm cần tính toán, bắt buộc tồn tại trong hệ thống. | Bắt buộc |
| Ngày tính toán | Ngày tháng (YYYY-MM-DD) | Ngày mốc tính toán tiền lãi, mặc định là ngày hiện tại. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Xác định số ngày thực gửi: $Days = calculationDate - startDate$.
2. Tính toán tiền lãi theo phương thức quy định tại BR-FIN-05:
  - Lãi cam kết toàn kỳ theo hợp đồng ($ExpectedInterest$):
    + Phương thức lãi đơn hoặc trả sau (`SIMPLE`, `POSTPAID`):
     $$ExpectedInterest = \frac{Principal \times Rate \times TermDays}{365}$$
    + Phương thức lãi trả trước (`PREPAID`): Tiền lãi tính theo công thức lãi đơn toàn kỳ nêu trên, đã được chi trả ngay tại ngày mở sổ:
     $$PrepaidInterest = ExpectedInterest = \frac{Principal \times Rate \times TermDays}{365}$$
    + Phương thức lãi kép theo tháng (`COMPOUND_MONTHLY`):
     $$ExpectedInterest = Principal \times \left[\left(1 + \frac{Rate}{12}\right)^m - 1\right]$$
     Trong đó $m = TermMonths$ là số tháng gửi do người dùng chỉ định theo kỳ hạn hợp đồng, số nguyên dương tùy chọn theo quy tắc BR-FIN-05.
  - Tiền lãi dồn tích lũy kế đến ngày tính toán ($AccruedInterest$):
    + Khoản gửi không kỳ hạn (`unlimited`):
     $$AccruedInterest = Interest_{unlimited} = \frac{Principal \times Rate \times Days}{365}$$
    + Khoản gửi có kỳ hạn:
     $$AccruedInterest = ExpectedInterest \times SavingsTermProgress$$
     (Với phương thức `SIMPLE`, tương đương $\frac{Principal \times Rate \times Days}{365}$. Riêng phương thức `PREPAID`, do lãi đã chi trả đầu kỳ nên lãi dồn tích còn lại chờ thanh toán bằng 0.00 VNĐ).
  - Số dư ghi nhận của sổ tiết kiệm tại thời điểm tính toán: $SavingsBalance_s = Principal + AccruedInterest$ (riêng phương thức `PREPAID`, $SavingsBalance_s = Principal$).
3. Đánh giá trạng thái kỳ hạn:
  - Nếu $Days < TermDays$: Trạng thái kỳ hạn hiển thị `RUNNING`.
  - Nếu $Days \ge TermDays$: Trạng thái kỳ hạn hiển thị `MATURED`.
4. Không làm biến động số dư: Chức năng là thao tác đọc, không làm thay đổi số dư ví tiền hay số dư gốc của sổ tiết kiệm.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Báo cáo chi tiết sổ gồm: Vốn gốc, Lãi suất, Số ngày đã gửi, Tiền lãi dự kiến lũy kế, Số ngày còn lại đến khi đáo hạn, Trạng thái kỳ hạn. |
| Biến động số dư | Thao tác đọc, không làm biến động số dư. |
| Thông báo phản hồi | Thông tin tính lãi và cảnh báo đáo hạn hiển thị trên giao diện người dùng. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-SAV-02.1 | Cho sổ tiết kiệm 100,000,000.00 VNĐ gửi ngày 2026-01-01 kỳ hạn 12 tháng đáo hạn ngày 2027-01-01 ($TermDays$ = 365 ngày lịch theo BR-FIN-05) với lãi suất 6.00%/năm theo phương thức `SIMPLE`. Tính toán tại ngày thứ 180. | Tiền lãi dự kiến lũy kế được tính là $(100,000,000 \times 0.06 \times 180) / 365 = 2,958,904.11$ VNĐ; trạng thái kỳ hạn là `RUNNING`. |
| TC-SAV-02.2 | Tính toán tiền lãi cho sổ trên tại ngày thứ 365 ($Days = TermDays$). | Tiền lãi dự kiến đạt đúng $(100,000,000 \times 0.06 \times 365) / 365 = 6,000,000.00$ VNĐ; trạng thái chuyển sang `MATURED`. |

#### 4.6.3 FR-SAV-03: Tất toán sổ tiết kiệm đúng hạn

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-SAV-03 |
| Tên | Tất toán sổ tiết kiệm đúng hạn |
| Nhóm chức năng | Sổ tiết kiệm (FR-SAV) |
| Ca sử dụng liên quan | UC-SAV-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI đóng sổ tiết kiệm khi đã đến hạn đáo hạn, chuyển toàn bộ vốn gốc và lãi cam kết về một ví tiền chỉ định, ghi nhận thu nhập tiền lãi theo quy tắc BR-FIN-05. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã định danh sổ tiết kiệm | Mã định danh | Mã sổ tiết kiệm cần tất toán, bắt buộc tồn tại trong hệ thống. | Bắt buộc |
| Mã định danh ví đích | Mã định danh | Mã ví thụ hưởng tiếp nhận tiền gốc và lãi, ví ở trạng thái `ACTIVE`. | Bắt buộc |
| Ngày tất toán dự kiến | Ngày tháng (YYYY-MM-DD) | Ngày thực hiện tất toán, yêu cầu $settleDate \ge MaturityDate$. | Bắt buộc |

Quy tắc xử lý và điều kiện:

1. Kiểm tra trạng thái sổ: Hệ thống PHẢI kiểm tra sổ tiết kiệm đang ở trạng thái `ACTIVE`. Nếu không, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ sổ tiết kiệm không ở trạng thái hợp lệ để tất toán.
2. Kiểm tra điều kiện đúng hạn theo BR-FIN-05: Kiểm tra ngày tất toán $settleDate \ge MaturityDate$. Nếu tất toán trước ngày đáo hạn, Hệ thống PHẢI chuyển hướng sang quy trình xử lý của FR-SAV-04 (Tất toán trước hạn).
3. Tính toán giá trị chi trả tất toán ($Payout_{mature}$):
  - Tiền lãi cuối kỳ được tính đúng theo lãi suất cam kết ban đầu:
   $$FinalInterest = ExpectedInterest$$
  - Tổng tiền tất toán chuyển về ví:
   $$Payout_{mature} = Principal + FinalInterest$$
  (Đối với phương thức lãi trả trước `PREPAID`, do lãi đã chi trả đầu kỳ nên $FinalInterest = 0.00$ VNĐ tại ngày tất toán và $Payout_{mature} = Principal$).
4. Kiểm tra ví thụ hưởng: Ví đích nhận tiền phải ở trạng thái `ACTIVE`.
5. Chuyển tiền tất toán vào ví theo BR-FIN-02:
$$WalletBalance_{toWallet}(\text{sau}) = WalletBalance_{toWallet}(\text{trước}) + Payout_{mature}$$
6. Cập nhật trạng thái sổ tiết kiệm: Chuyển trạng thái sổ sang `SETTLED_MATURED`, ghi nhận ngày tất toán thực tế.
7. Cập nhật cấu trúc tài sản: Tổng tài sản tiết kiệm $Savings$ giảm một khoản bằng $Principal$; tài sản ròng $NetWorth$ tăng một khoản bằng tiền lãi nhận được $FinalInterest$ (đối với phương thức `PREPAID`, do $FinalInterest = 0.00$ VNĐ nên $NetWorth$ không đổi).
8. Giải phóng liên kết quỹ mục tiêu theo BR-FIN-10: Nếu sổ tiết kiệm đang được gắn vào một quỹ mục tiêu, hệ thống tự động gỡ bỏ sổ khỏi danh sách tài sản của quỹ và cập nhật lại tiến độ đã gom được.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi sổ tiết kiệm cập nhật trạng thái `SETTLED_MATURED` kèm ngày tất toán và giá trị thanh toán. |
| Biến động số dư | Số dư ví nhận tiền tăng $Payout_{mature}$; tổng tài sản sổ tiết kiệm $Savings$ giảm $Principal$; tài sản ròng $NetWorth$ tăng $FinalInterest$ (bằng 0.00 VNĐ đối với phương thức `PREPAID`). |
| Thông báo phản hồi | Thông báo tất toán sổ tiết kiệm đúng hạn thành công kèm chi tiết số tiền gốc và lãi đã nhận. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-SAV-03.1 | Sổ tiết kiệm 50,000,000 VNĐ gửi ngày 2026-01-01 kỳ hạn 6 tháng lãi suất 6.00%/năm đáo hạn ngày 2026-07-01 ($TermDays$ = 181 ngày lịch) với tiền lãi 1,487,671.23 VNĐ. Người dùng thực hiện tất toán về ví "MB Bank". | Ví MB Bank được cộng đúng 51,487,671.23 VNĐ; sổ chuyển trạng thái `SETTLED_MATURED`; $Savings$ giảm 50,000,000.00 VNĐ; $NetWorth$ tăng 1,487,671.23 VNĐ. |
| TC-SAV-03.2 | Người dùng cố gắng thực hiện tất toán đúng hạn tại một ngày trước ngày đáo hạn ($settleDate < MaturityDate$). | Hệ thống từ chối và cảnh báo sổ chưa đến ngày đáo hạn, chuyển sang phương án tất toán trước hạn. |

#### 4.6.4 FR-SAV-04: Tất toán sổ tiết kiệm trước hạn

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-SAV-04 |
| Tên | Tất toán sổ tiết kiệm trước hạn |
| Nhóm chức năng | Sổ tiết kiệm (FR-SAV) |
| Ca sử dụng liên quan | UC-SAV-03 |
| Mô tả kỹ thuật | Hệ thống PHẢI đóng sổ tiết kiệm trước ngày đáo hạn theo yêu cầu của người dùng và áp dụng hệ số phạt lên phần lãi đã cộng dồn theo lãi đơn cho toàn bộ thời gian thực gửi. Hệ thống PHẢI cảnh báo số tiền lãi bị thiệt hại theo quy tắc BR-FIN-05. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã định danh sổ tiết kiệm | Mã định danh | Mã sổ tiết kiệm cần tất toán, bắt buộc tồn tại trong hệ thống. | Bắt buộc |
| Mã định danh ví đích | Mã định danh | Mã ví tiếp nhận tiền chi trả, ví ở trạng thái `ACTIVE`. | Bắt buộc |
| Ngày tất toán dự kiến | Ngày tháng (YYYY-MM-DD) | Ngày thực hiện tất toán trước hạn. | Bắt buộc |
| Hệ số phạt tất toán sớm | Số thập phân chính xác | Hệ số áp dụng lên phần lãi đã cộng dồn theo lãi đơn khi tất toán trước hạn, điều kiện $0 < EarlyPenaltyFactor \le 1$ theo BR-FIN-05; do người dùng chỉ định cho từng giao dịch tất toán, không có giá trị mặc định cố định trong hệ thống. | Bắt buộc |
| Xác nhận | Cờ logic (Đúng / Sai) | Xác nhận chấp nhận mất tiền lãi kỳ hạn để rút tiền, giá trị `true`. | Bắt buộc |

Quy tắc xử lý và điều kiện:

1. Kiểm tra trạng thái sổ: Sổ tiết kiệm phải đang ở trạng thái `ACTIVE`.
2. Kiểm tra điều kiện trước hạn: Kiểm tra $Days = settleDate - start < TermDays$. Nếu đã đến hạn hoặc quá hạn, hệ thống hướng dẫn sang quy trình FR-SAV-03.
3. Tính lại lãi theo số ngày thực gửi, căn cứ BR-FIN-05:
  - Lãi đã cộng dồn tính theo công thức lãi đơn trên số ngày thực gửi (không dùng lãi suất cam kết theo phương thức ban đầu):
   $$AccruedSimpleInterest = \frac{Principal \times Rate \times Days}{365}$$
  - Số tiền lãi thực tế được nhận sau khi áp hệ số phạt tất toán sớm:
   $$ActualInterest = EarlyPenaltyFactor \times AccruedSimpleInterest$$
  - Lãi dự kiến bị mất khi tất toán trước hạn:
    $$
    InterestLoss = \left(ExpectedInterest \times \frac{Days}{TermDays}\right) - ActualInterest
    $$
  - Tổng số tiền thực nhận chuyển về ví:
    $$
    Payout = Principal + ActualInterest
    $$
4. Kiểm tra xác nhận của người dùng: Nếu tham số xác nhận mang giá trị sai (`false`), hệ thống chỉ trả về bảng dữ liệu so sánh tài chính cảnh báo số tiền lãi bị mất và dừng lại, không thực hiện chuyển tiền.
5. Khi tham số xác nhận mang giá trị đúng (`true`):
  - Cộng số tiền $Payout$ vào số dư ví nhận tiền theo BR-FIN-02.
  - Chuyển trạng thái sổ tiết kiệm sang `SETTLED_EARLY`, lưu trữ thông tin về $EarlyPenaltyFactor$, $ActualInterest$ và $InterestLoss$.
  - Gỡ bỏ liên kết tài sản khỏi quỹ mục tiêu theo BR-FIN-10 nếu có.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi sổ tiết kiệm cập nhật trạng thái `SETTLED_EARLY` kèm thông tin tính lại lãi. |
| Biến động số dư | Số dư ví nhận tiền tăng $Payout$; tổng tài sản sổ tiết kiệm $Savings$ giảm $Principal$; tài sản ròng $NetWorth$ tăng một khoản bằng lãi thực nhận $ActualInterest$. |
| Thông báo phản hồi | Thông báo tất toán trước hạn thành công kèm chi tiết số tiền gốc, tiền lãi thực nhận và số tiền lãi dự kiến bị mất. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-SAV-04.1 | Cho sổ 100,000,000.00 VNĐ gửi ngày 2026-01-01 kỳ hạn 12 tháng đáo hạn ngày 2027-01-01 ($TermDays$ = 365 ngày lịch) lãi suất 6.00%/năm theo phương thức lãi đơn cuối kỳ. Sổ gửi được 90 ngày thì tất toán trước hạn với hệ số phạt tất toán sớm 0.50 (giá trị minh họa do người dùng chỉ định, không phải mặc định hệ thống). | Lãi cộng dồn theo lãi đơn (không làm tròn trung gian) là $(100,000,000 \times 0.06 \times 90) / 365 = 1,479,452.0548$ VNĐ, làm tròn hiển thị $1,479,452.05$ VNĐ. Lãi thực nhận sau hệ số phạt là $1,479,452.0548 \times 0.50 = 739,726.0274$ VNĐ, làm tròn hiển thị $739,726.03$ VNĐ; ví nhận được cộng 100,739,726.03 VNĐ. Cảnh báo lãi dự kiến bị mất là $1,479,452.0548 - 739,726.0274 = 739,726.0274$ VNĐ, làm tròn hiển thị $739,726.03$ VNĐ. |
| TC-SAV-04.2 | Người dùng yêu cầu tất toán trước hạn nhưng tham số xác nhận là `false`. | Hệ thống trả về thông số tài chính để người dùng xem xét, không thay đổi số dư ví và giữ nguyên trạng thái sổ tiết kiệm. |

#### 4.6.5 FR-SAV-05: Tái tục sổ tiết kiệm với điều khoản mới

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-SAV-05 |
| Tên | Tái tục sổ tiết kiệm với điều khoản mới |
| Nhóm chức năng | Sổ tiết kiệm (FR-SAV) |
| Ca sử dụng liên quan | UC-SAV-06 |
| Mô tả kỹ thuật | Hệ thống PHẢI đóng một sổ tiết kiệm đã tất toán đúng hạn và mở một sổ tiết kiệm mới với kỳ hạn, lãi suất và phương thức lãi do người dùng chỉ định lại. Hệ thống PHẢI lấy một phần hoặc toàn bộ giá trị tất toán làm vốn gốc mới theo quy tắc BR-FIN-05. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã định danh sổ nguồn | Mã định danh | Mã sổ tiết kiệm nguồn, bắt buộc đang ở trạng thái `SETTLED_MATURED`. | Bắt buộc |
| Giá trị tái tục | Số tiền (VNĐ) | Vốn gốc tái tục cho sổ mới, $0 < rolloverAmount \le Payout_{mature}$ của sổ nguồn. | Bắt buộc |
| Mã định danh ví | Mã định danh | Mã định danh ví hiện đang giữ khoản tất toán của sổ nguồn, ví ở trạng thái `ACTIVE`, $WalletBalance_{wallet} \ge rolloverAmount$. | Bắt buộc |
| Kỳ hạn mới | Mã phân loại | Kỳ hạn mới, cùng miền giá trị với kỳ hạn tại FR-SAV-01. | Bắt buộc |
| Lãi suất mới | Số thập phân chính xác | Lãi suất cam kết mới (%/năm), $newRate \ge 0$. | Bắt buộc |
| Phương thức tính lãi mới ($newInterestMethod$) | Mã phân loại | Phương thức lãi mới theo BR-FIN-05, thuộc {`SIMPLE`, `COMPOUND_MONTHLY`, `PREPAID`, `POSTPAID`}. | Bắt buộc |
| Tên ngân hàng | Chuỗi ký tự | Tên ngân hàng hoặc tổ chức tín dụng cho sổ mới. Mặc định: giữ nguyên ngân hàng của sổ nguồn. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra điều kiện tái tục theo BR-FIN-05: Sổ nguồn phải đang ở trạng thái `SETTLED_MATURED`. Nếu sổ chưa tất toán đúng hạn hoặc đang ở trạng thái khác, hệ thống từ chối và báo lỗi nghiệp vụ sổ tiết kiệm chưa đủ điều kiện tái tục.
2. Kiểm tra vốn gốc tái tục: Nếu $rolloverAmount > Payout_{mature}$ đã ghi nhận của sổ nguồn, hệ thống từ chối và báo lỗi nghiệp vụ vốn gốc tái tục vượt quá giá trị tất toán.
3. Kiểm tra số dư ví nguồn theo BR-FIN-02: Nếu $WalletBalance_{wallet} < rolloverAmount$, hệ thống báo lỗi nghiệp vụ số dư ví không đủ và từ chối tái tục.
4. Trừ $rolloverAmount$ khỏi ví nguồn theo BR-FIN-02, sau đó chạy lại toàn bộ quy tắc tạo sổ mới của FR-SAV-01 với vốn gốc mới là $rolloverAmount$, kỳ hạn mới, lãi suất mới và phương thức tính lãi mới.
5. Gắn nhãn truy vết: Sổ mới lưu trường mã sổ nguồn liên kết để phục vụ truy vết lịch sử, không kế thừa số ngày gửi hay lãi suất của sổ nguồn.
6. Xử lý phần chênh lệch: Phần $Payout_{mature} - rolloverAmount$ (nếu có) vẫn giữ nguyên trong ví theo BR-FIN-02, không tạo thêm khoản ghi nào.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Đối tượng sổ tiết kiệm mới với mã sổ tiết kiệm mới, trạng thái `ACTIVE`, trường nguồn tái tục trỏ về sổ nguồn. |
| Biến động số dư | Số dư ví nguồn giảm $rolloverAmount$; tổng tài sản sổ tiết kiệm $Savings$ tăng $rolloverAmount$; tài sản ròng $NetWorth$ giữ nguyên tại thời điểm tái tục. |
| Thông báo phản hồi | Thông báo tái tục thành công kèm thông tin sổ mới (kỳ hạn, lãi suất, phương thức lãi, ngày đáo hạn) và số tiền còn lại trong ví nếu tái tục một phần. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-SAV-05.1 | Sổ nguồn đã tất toán đúng hạn với $Payout_{mature}$ = 51,487,671.23 VNĐ tại ví MB Bank. Người dùng tái tục toàn bộ với kỳ hạn $TermMonths = 12$, lãi suất 6.50%/năm, phương thức tính lãi = `SIMPLE`. | Ví MB Bank giảm đúng 51,487,671.23 VNĐ, sổ mới được tạo với vốn gốc 51,487,671.23 VNĐ, trạng thái `ACTIVE`, nguồn tái tục trỏ về sổ nguồn. |
| TC-SAV-05.2 | Sổ nguồn đang ở trạng thái `ACTIVE` (chưa tất toán). Người dùng yêu cầu tái tục. | Hệ thống từ chối và báo lỗi nghiệp vụ sổ tiết kiệm chưa đủ điều kiện tái tục. |
| TC-SAV-05.3 | Sổ nguồn có $Payout_{mature}$ = 30,000,000.00 VNĐ. Người dùng nhập giá trị tái tục = 35,000,000.00 VNĐ. | Hệ thống từ chối và báo lỗi nghiệp vụ vốn gốc tái tục vượt quá giá trị tất toán. |

### 4.7 FR-INV: Nhóm chức năng Tài sản đầu tư

Nhóm chức năng Tài sản đầu tư quản lý các tài sản đầu tư của người dùng như chứng chỉ quỹ, cổ phiếu, trái phiếu, vàng và bất động sản. Nhóm ghi nhận lệnh mua kèm chi phí mua, định giá tài sản theo mô hình đơn giá nhân số lượng, và xác định giá vốn theo quy tắc BR-FIN-06.

#### 4.7.1 FR-INV-01: Ghi nhận lệnh mua tài sản đầu tư

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-INV-01 |
| Tên | Ghi nhận lệnh mua tài sản đầu tư |
| Nhóm chức năng | Tài sản đầu tư (FR-INV) |
| Ca sử dụng liên quan | UC-INV-01, UC-INV-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI ghi nhận mỗi lần mua thành một lệnh mua độc lập và trừ tiền từ ví thanh toán. Hệ thống PHẢI quy đổi chi phí mua về số tiền VNĐ, xác định giá gốc và số lượng đơn vị theo quy tắc BR-FIN-06. Hệ thống tạo tài sản đầu tư mới ngay trong lệnh mua đầu tiên của tài sản đó. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Tên tài sản đầu tư | Chuỗi ký tự | Tên do người dùng đặt (ví dụ: `VESAF`, "Vàng SJC", "Đất lô 7"), độ dài từ 1 đến 50 ký tự. Tên chưa có thì hệ thống tạo tài sản mới, tên đã có thì dùng tài sản đó. | Bắt buộc |
| Loại tài sản | Lựa chọn | Nhãn loại tài sản, ví dụ chứng chỉ quỹ, cổ phiếu, trái phiếu, vàng, bất động sản. | Bắt buộc khi tạo tài sản mới |
| Ngày mua | Ngày tháng (YYYY-MM-DD) | Ngày khớp lệnh mua, không vượt quá ngày hiện tại. | Bắt buộc |
| Đơn giá mua | Số thập phân chính xác | Giá của một đơn vị tại ngày mua, $Price_{buy} > 0$, lưu đúng như nhập. Nếu không nhập thì hệ thống tính từ Số tiền mua và Số lượng theo BR-FIN-06. | Tùy chọn, nhập hai trong ba của Đơn giá mua, Số lượng, Số tiền mua |
| Số lượng | Số thập phân chính xác | Số đơn vị mua, $Units_{buy} > 0$, tối đa 4 chữ số thập phân. Nếu không nhập thì hệ thống tính từ Số tiền mua và Đơn giá mua. Không nhập Số lượng và chỉ nhập một trong hai đại lượng còn lại thì hệ thống điền 1. | Tùy chọn, nhập hai trong ba của Đơn giá mua, Số lượng, Số tiền mua |
| Số tiền mua | Số tiền (VNĐ) | Số tiền mua, $Amount_{buy} > 0$, 2 chữ số thập phân theo BR-FIN-01. Nếu không nhập thì hệ thống tính từ Đơn giá mua và Số lượng. | Tùy chọn, nhập hai trong ba của Đơn giá mua, Số lượng, Số tiền mua |
| Cách nhập chi phí mua | Lựa chọn | Số tiền, Phần trăm hoặc Không có chi phí. Mặc định Không có chi phí. | Tùy chọn |
| Giá trị chi phí mua | Số thập phân chính xác | Với Số tiền: số tiền VNĐ, $\ge 0$. Với Phần trăm: tỷ lệ phần trăm trên số tiền mua, từ 0 đến 100 (ví dụ 0.15 nghĩa là 0.15%). | Bắt buộc khi chọn Số tiền hoặc Phần trăm |
| Mã định danh ví | Mã định danh | Mã ví xuất tiền thanh toán, ví ở trạng thái `ACTIVE`. | Bắt buộc |
| Quỹ mục tiêu liên kết | Mã định danh | Mã quỹ mục tiêu có sẵn ở trạng thái hoạt động; mỗi lệnh mua gắn tối đa một quỹ theo `BR-FIN-10`. | Tùy chọn |
| Khoản đầu tư liên kết | Chuỗi ký tự | Tên khoản đầu tư có sẵn, độ dài từ 1 đến 50 ký tự; mỗi lệnh mua thuộc tối đa một khoản theo `FR-INV-06`. | Tùy chọn |
| Đại lượng giữ nguyên khi sửa | Lựa chọn | Chỉ dùng khi sửa Đơn giá mua hoặc Số lượng của lệnh mua đã ghi theo quy tắc 10: giữ nguyên một trong hai đại lượng còn lại, mặc định Số tiền mua. | Bắt buộc khi sửa, không dùng khi ghi lệnh mới |

Quy tắc xử lý và điều kiện:

1. Xác định tài sản đầu tư: Hệ thống PHẢI so khớp tên không phân biệt chữ hoa chữ thường và bỏ khoảng trắng đầu cuối. Nếu tên đã có, hệ thống dùng loại tài sản hiện có của tài sản đó, không thay đổi theo dữ liệu người dùng nhập. Nếu tên chưa có, hệ thống tạo tài sản mới với loại tài sản đã nhập.
2. Kiểm tra dữ liệu đầu vào: Hệ thống PHẢI kiểm tra người dùng nhập đúng hai trong ba đại lượng Đơn giá mua, Số lượng, Số tiền mua, hoặc chỉ nhập một trong Đơn giá mua và Số tiền mua (số lượng được điền 1).
   - Nếu nhập đủ ba, chỉ nhập Số lượng, hoặc không nhập đại lượng nào, hệ thống PHẢI từ chối và báo lỗi nghiệp vụ thiếu hoặc thừa đại lượng.
   - Hệ thống PHẢI kiểm tra từng đại lượng người dùng nhập trước khi xác định đại lượng còn lại theo quy tắc 7:
     - $Amount_{buy} > 0$, nếu vi phạm báo lỗi `ERR-INV-002`.
     - $Price_{buy} > 0$, nếu vi phạm báo lỗi `ERR-INV-001`.
     - $Units_{buy} > 0$ và có tối đa 4 chữ số thập phân, nếu vi phạm từ chối và báo lỗi nghiệp vụ số lượng mua phải lớn hơn 0 và có tối đa 4 chữ số thập phân.
   - Đại lượng do hệ thống điền $Units_{buy} = 1$ không cần kiểm tra lại.
   - Sau khi xác định theo quy tắc 7, nếu $Amount_{buy}$ làm tròn bằng 0.00 thì báo lỗi `ERR-INV-002`.
   - Với chi phí mua, hệ thống PHẢI kiểm tra $Fee_{buy} \ge 0$ (cách Số tiền) hoặc $0 \le feeRate \le 100$ (cách Phần trăm), nếu vi phạm báo lỗi `ERR-INV-010`.
   - Làm tròn số tiền về 2 chữ số thập phân theo BR-FIN-01. Số lượng dẫn xuất được lưu đầy đủ chữ số theo BR-FIN-06. Hai đại lượng người dùng nhập được lưu đúng như nhập.
   - Các quy tắc 3 đến 6 dùng $Amount_{buy}$ đã xác định theo quy tắc 7.
3. Quy đổi chi phí mua: Hệ thống PHẢI quy đổi về số tiền VNĐ và lưu số tiền này vào lệnh mua.
  - Cách Số tiền: $Fee_{buy}$ bằng số tiền người dùng nhập.
  - Cách Phần trăm: $Fee_{buy} = Amount_{buy} \times \frac{feeRate}{100}$, làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng.
  - Cách Không có chi phí: $Fee_{buy} = 0.00$ VNĐ.
4. Xác định giá gốc của lệnh mua theo BR-FIN-06. Chi phí mua luôn được tính vào giá gốc và giá gốc không đổi sau khi ghi lệnh:
$$CostBasis_{origin} = Amount_{buy} + Fee_{buy}$$
5. Kiểm tra ví nguồn: Hệ thống PHẢI kiểm tra ví nguồn ở trạng thái `ACTIVE` và đủ số dư theo BR-FIN-02:
$$WalletBalance_{wallet} - CostBasis_{origin} \ge 0$$
  Nếu không đủ tiền, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ số dư ví không đủ.
6. Trừ tiền khỏi ví thanh toán:
$$WalletBalance_{wallet}(\text{sau}) = WalletBalance_{wallet}(\text{trước}) - CostBasis_{origin}$$
7. Xác định đại lượng còn lại theo BR-FIN-06 mục 3.6:
  - Nhập Số tiền mua và Đơn giá mua: $Units_{buy} = \frac{Amount_{buy}}{Price_{buy}}$, lưu đầy đủ chữ số, không làm tròn 4 chữ số.
  - Nhập Số tiền mua và Số lượng: đơn giá mua $Price_{buy} = \frac{Amount_{buy}}{Units_{buy}}$ là đại lượng dẫn xuất, giữ đủ độ chính xác để tích đơn giá nhân số lượng làm tròn 2 chữ số thập phân bằng đúng $Amount_{buy}$.
  - Nhập Đơn giá mua và Số lượng: $Amount_{buy} = Price_{buy} \times Units_{buy}$, làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng.
  - Chỉ nhập Số tiền mua: hệ thống điền $Units_{buy} = 1$ và đơn giá mua bằng $Amount_{buy}$.
  - Chỉ nhập Đơn giá mua: hệ thống điền $Units_{buy} = 1$ và $Amount_{buy} = Price_{buy}$.
8. Lưu lệnh mua: Hệ thống PHẢI ghi nhận lệnh mua mới độc lập, thuộc đúng một tài sản đầu tư và thuộc tối đa một khoản đầu tư tại một thời điểm.
   - Bản ghi lưu ngày mua, Thời điểm ghi và số thứ tự ghi do hệ thống tự ghi, đơn giá mua, số tiền mua, chi phí mua, giá gốc, số lượng đơn vị và ví thanh toán.
   - Giá vốn ban đầu của lệnh mua bằng giá gốc. Hệ thống không cộng dồn lệnh mua vào lệnh mua khác của cùng tài sản.
   - Giá trị thị trường của lệnh mua luôn bằng số đơn vị còn nhân đơn giá hiện hành, kể cả ngay sau khi ghi, và bằng $Amount_{buy}$ khi mua ở đơn giá hiện hành.
   - Nếu ngày mua không cũ hơn ngày định giá gần nhất thì đơn giá mua trở thành đơn giá hiện hành và được lưu vào lịch sử giá. Nếu ngày mua bằng ngày định giá gần nhất thì đơn giá mua thay thế bản ghi giá của ngày đó trong lịch sử giá và trở thành đơn giá hiện hành theo QD-242.
   - Giá trị thị trường của mọi lệnh mua còn đơn vị của tài sản được tính lại theo FR-INV-03 quy tắc 3.
9. Ghi lùi ngày mua:
   - Nếu ngày mua cũ hơn ngày mua của một lệnh mua đã bị khấu trừ bởi lệnh bán của cùng tài sản, hệ thống PHẢI tính lại khấu trừ nhập trước xuất trước của các lệnh bán theo cách của FR-INV-04 quy tắc 8.
   - Khi đó, hệ thống hiển thị danh sách lệnh bán bị ảnh hưởng kèm lãi lỗ đã chốt trước và sau, và chỉ lưu khi người dùng xác nhận.
   - Nếu lệnh mua nắm giữ bị đổi đang gắn quỹ mục tiêu, hệ thống hiển thị cảnh báo trước khi xác nhận.
   - Nếu ngày mua cũ hơn một sự kiện đã ghi của cùng tài sản, hệ thống cũng PHẢI tính lại phân bổ của các sự kiện đứng sau theo số đơn vị nắm giữ mới. Trong trường hợp ngày mua cũ hơn một sự kiện đã ghi, hệ thống cũng PHẢI tính lại các lệnh bán có giá vốn xuất bán thay đổi theo đó và hiển thị tác động trong cùng bước xác nhận.
   - Nếu việc tính lại làm một hoàn vốn đứng sau vượt giá vốn còn lại, phần vượt được cộng vào lãi đã chốt và hiển thị trong cùng bước xác nhận.
10. Sửa lệnh mua: Hệ thống PHẢI cho người dùng sửa Đơn giá mua, Số lượng hoặc Số tiền mua của lệnh mua đã ghi theo quy tắc 2 và 7.
   - Khi sửa Đơn giá mua hoặc Số lượng, người dùng PHẢI chọn cho lần sửa đó giữ nguyên một trong hai đại lượng còn lại (Số tiền mua hoặc đại lượng thứ ba). Đại lượng còn lại được tính lại, mặc định giữ nguyên Số tiền mua.
   - Khi sửa Số tiền mua, Số lượng giữ nguyên và đơn giá được tính lại.
   - Chi phí mua theo cách Số tiền giữ nguyên, theo cách Phần trăm tính lại trên số tiền mua mới.
   - Giá gốc mới là $CostBasis_{origin}' = Amount_{buy}' + Fee_{buy}'$. Chênh lệch $CostBasis_{origin}' - CostBasis_{origin}$ được trừ thêm từ ví thanh toán nếu dương, hoặc hoàn lại vào ví thanh toán nếu âm, theo BR-FIN-02.
   - Đơn giá sau khi sửa áp dụng quy tắc 8. Nếu ngày mua không cũ hơn ngày định giá gần nhất thì đơn giá của lệnh mua trở thành đơn giá hiện hành và được lưu vào lịch sử giá. Khi đó, giá trị mọi lệnh mua còn đơn vị được tính lại.
   - Hệ thống PHẢI tính lại các lệnh bán và sự kiện phát sinh sau đó (phần hoàn vốn vượt giá vốn còn lại sau khi sửa được cộng vào lãi đã chốt và hiển thị trong cùng bước). Hệ thống hiển thị tác động trước và sau, cảnh báo quỹ mục tiêu và chỉ lưu khi người dùng xác nhận, kèm dấu vết.
   - Hệ thống PHẢI từ chối toàn bộ việc sửa trong các trường hợp sau:
     - Ví thanh toán không ở trạng thái `ACTIVE`.
     - Số dư không đủ cho khoản trừ thêm theo quy tắc 5.
     - Việc tính lại làm một lệnh bán sau đó thiếu đơn vị (báo lỗi `ERR-INV-009`).

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Lệnh mua mới kèm định danh riêng, Thời điểm ghi và số thứ tự ghi do hệ thống tự ghi, và tài sản đầu tư mới nếu tên chưa có. |
| Biến động số dư | Ví thanh toán giảm $CostBasis_{origin}$. Tổng tài sản đầu tư $Investments$ tăng giá trị thị trường của lệnh mua mới $Units_{buy} \times CurrentPrice$ (bằng $Amount_{buy}$ khi mua ở đơn giá hiện hành), cộng chênh lệch do định giá lại các lệnh mua còn đơn vị khi đơn giá hiện hành đổi. Tài sản ròng $NetWorth$ đổi đúng bằng mức tăng của $Investments$ trừ $CostBasis_{origin}$, nên giảm chi phí mua $Fee_{buy}$ (nếu có) khi mua ở đơn giá hiện hành. |
| Thông báo phản hồi | Thông báo ghi nhận lệnh mua thành công kèm giá gốc, chi phí mua và số lượng đơn vị nhận được. Khi giao dịch cùng ngày với giao dịch đã ghi, thông báo nêu giao dịch được tính sau những giao dịch nào theo số thứ tự ghi. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-INV-01.1 | Người dùng mua lần đầu tài sản "VESAF", loại chứng chỉ quỹ, nhập số tiền 10,000,000.00 VNĐ và đơn giá 25,000.00 VNĐ, không có chi phí mua, từ ví MB Bank. | Hệ thống tạo tài sản VESAF và một lệnh mua với 400.0000 đơn vị, giá gốc 10,000,000.00 VNĐ. Ví MB Bank bị trừ đúng 10,000,000.00 VNĐ. |
| TC-INV-01.2 | Như TC-INV-01.1, người dùng chọn chi phí mua theo số tiền 50,000.00 VNĐ. | Giá gốc là 10,050,000.00 VNĐ, số lượng đơn vị 400.0000. Ví bị trừ 10,050,000.00 VNĐ, tài sản ròng giảm 50,000.00 VNĐ. |
| TC-INV-01.3 | Như TC-INV-01.1, người dùng chọn chi phí mua theo phần trăm với tỷ lệ 0.15. | Chi phí mua quy đổi và lưu là 15,000.00 VNĐ, giá gốc là 10,015,000.00 VNĐ. |
| TC-INV-01.4 | Người dùng mua tiếp tài sản đã có, gõ tên "vesaf " (chữ thường, thừa khoảng trắng), số tiền 5,000,000.00 VNĐ tại đơn giá 26,000.00 VNĐ, không có chi phí mua, ngày mua không cũ hơn lần định giá gần nhất. | Hệ thống nhận ra tài sản VESAF, không tạo tài sản mới, ghi một lệnh mua độc lập với 192.3077 đơn vị hiển thị (lưu đầy đủ chữ số). Đơn giá hiện hành chuyển thành 26,000.0000 VNĐ. Lệnh mua trước giữ nguyên số đơn vị và giá gốc, giá trị thị trường tính lại theo đơn giá này. |
| TC-INV-01.5 | Người dùng mua tài sản "Đất lô 7", loại bất động sản, chỉ nhập số tiền 2,000,000,000.00 VNĐ, chi phí mua theo số tiền 40,000,000.00 VNĐ. | Giá gốc là 2,040,000,000.00 VNĐ, số lượng đơn vị được điền bằng 1, đơn giá mua bằng 2,000,000,000.00 VNĐ. |
| TC-INV-01.6 | Người dùng mua tiếp tài sản "Đất lô 7" nhưng chọn loại tài sản khác. | Hệ thống giữ loại tài sản hiện có, bỏ qua lựa chọn mới. |
| TC-INV-01.7 | Người dùng mua với tổng số tiền mua cộng chi phí mua lớn hơn số dư khả dụng của ví được chọn. | Hệ thống từ chối giao dịch và báo lỗi nghiệp vụ số dư ví không đủ. |
| TC-INV-01.8 | Người dùng nhập đơn giá mua bằng 0 hoặc số âm. | Hệ thống từ chối và báo lỗi `ERR-INV-001`. |
| TC-INV-01.9 | Người dùng nhập số tiền mua $\le 0$ (báo lỗi `ERR-INV-002`), hoặc nhập chi phí mua âm hay tỷ lệ phần trăm chi phí mua ngoài khoảng 0 đến 100 như 150% (báo lỗi `ERR-INV-010`). | Hệ thống từ chối giao dịch và báo lỗi tương ứng `ERR-INV-002` hoặc `ERR-INV-010`. |
| TC-INV-01.10 | Người dùng nhập ngày mua là một ngày trong tương lai. | Hệ thống từ chối và báo lỗi nghiệp vụ ngày mua không hợp lệ. |
| TC-INV-01.11 | Người dùng chọn ví đã lưu trữ làm ví thanh toán. | Hệ thống từ chối và báo lỗi nghiệp vụ ví không ở trạng thái hoạt động. |
| TC-INV-01.12 | Tài sản "Đất lô 7" có hai lệnh mua 80.0000 và 40.0000 đơn vị (giá gốc 2,000,000,000.00 VNĐ và 1,000,000,000.00 VNĐ), giá trị thị trường 4,500,000,000.00 VNĐ, đơn giá hiện hành 37,500,000.0000 VNĐ. Người dùng mua thêm 10.0000 đơn vị số tiền 400,000,000.00 VNĐ, không có chi phí mua, ngày mua không cũ hơn ngày định giá gần nhất. | Đơn giá hiện hành chuyển thành 40,000,000.0000 VNĐ. Giá trị ba lệnh mua là 3,200,000,000.00 VNĐ, 1,600,000,000.00 VNĐ và 400,000,000.00 VNĐ, tổng 5,200,000,000.00 VNĐ. |
| TC-INV-01.13 | Người dùng nhập tên tài sản đầu tư dài 51 ký tự hoặc để trống. | Hệ thống từ chối và báo lỗi nghiệp vụ tên tài sản không hợp lệ. |
| TC-INV-01.14 | Tài sản DCDS có lệnh mua B ngày 10/03/2026, 100.0000 đơn vị, giá gốc 1,200,000.00 VNĐ, và lệnh bán 100.0000 đơn vị ngày 25/03 nhận 1,400,000.00 VNĐ (lấy lệnh mua B, lãi đã chốt +200,000.00 VNĐ). Người dùng ghi lùi lệnh mua A ngày 01/03/2026, 100.0000 đơn vị, giá gốc 1,000,000.00 VNĐ. | Hệ thống hiển thị lệnh bán 25/03 bị ảnh hưởng, lãi lỗ đã chốt từ +200,000.00 VNĐ thành +400,000.00 VNĐ. Sau khi người dùng xác nhận, lệnh bán lấy lệnh mua A, lệnh mua B còn nắm giữ 100.0000 đơn vị. |
| TC-INV-01.15 | Đơn giá hiện hành của VESAF là 26,500.0000 VNĐ cập nhật ngày 11/10. Người dùng ghi lệnh mua ngày 01/10, số tiền 5,000,000.00 VNĐ tại 24,000.0000 VNĐ. | Đơn giá hiện hành giữ 26,500.0000 VNĐ, đơn giá 24,000.0000 VNĐ chỉ lưu vào lịch sử giá. Lệnh mua có 208.3333 đơn vị hiển thị (lưu đầy đủ chữ số). |
| TC-INV-01.16 | Người dùng nhập số tiền mua 16,000,000.00 VNĐ và số lượng 588.6792 đơn vị, không nhập đơn giá. | Đơn giá mua dẫn xuất được giữ đủ độ chính xác (khoảng 27,179.4893). Ví bị trừ đúng 16,000,000.00 VNĐ. Tích đơn giá nhân số lượng làm tròn 2 chữ số bằng 16,000,000.00 VNĐ. |
| TC-INV-01.17 | Người dùng nhập đủ ba đại lượng đơn giá mua, số lượng và số tiền mua, hoặc chỉ nhập số lượng. | Hệ thống từ chối và báo lỗi nghiệp vụ thiếu hoặc thừa đại lượng. |
| TC-INV-01.18 | Lệnh mua VESAF ngày 30/03 là lệnh mua duy nhất của VESAF, số tiền 16,000,000.00 VNĐ, 588.6792 đơn vị, không có chi phí mua, ví MB Bank đủ số dư. Người dùng sửa số tiền mua thành 16,500,000.00 VNĐ, giữ số lượng, ngày mua không cũ hơn ngày định giá gần nhất. | Giá gốc thành 16,500,000.00 VNĐ, ví MB Bank bị trừ thêm 500,000.00 VNĐ, đơn giá mua dẫn xuất được tính lại. Tài sản ròng không đổi. |
| TC-INV-01.19 | Như TC-INV-01.18 nhưng số dư ví MB Bank chỉ còn 300,000.00 VNĐ. | Hệ thống từ chối toàn bộ việc sửa, lệnh mua và số dư ví giữ nguyên. |
| TC-INV-01.20 | Lệnh mua A (100.0000 đơn vị, giá gốc 2,000,000.00 VNĐ) đã bị lệnh bán S1 khấu trừ 40.0000 đơn vị. Người dùng sửa số lượng của A thành 30.0000 đơn vị. | Hệ thống từ chối toàn bộ việc sửa và báo lỗi `ERR-INV-009` vì S1 thiếu đơn vị. |
| TC-INV-01.21 | Người dùng mua VESAF, nhập đơn giá 25,000.00 VNĐ và số lượng 100.12345 (5 chữ số thập phân). | Hệ thống từ chối và báo lỗi nghiệp vụ số lượng mua phải lớn hơn 0 và có tối đa 4 chữ số thập phân. |
| TC-INV-01.22 | Người dùng mua VESAF, nhập đơn giá 25,000.00 VNĐ và số lượng -5.0000. | Hệ thống từ chối và báo lỗi nghiệp vụ số lượng mua phải lớn hơn 0 và có tối đa 4 chữ số thập phân, không báo `ERR-INV-002`. |
| TC-INV-01.23 | Người dùng mua VESAF, không nhập đại lượng nào trong Đơn giá mua, Số lượng, Số tiền mua. | Hệ thống từ chối và báo lỗi nghiệp vụ thiếu hoặc thừa đại lượng. |
| TC-INV-01.24 | Lệnh mua VESAF ngày 30/03 là lệnh mua duy nhất của VESAF, số tiền 16,000,000.00 VNĐ, 588.6792 đơn vị, không có chi phí mua, ví MB Bank đủ số dư. Người dùng sửa số lượng thành 600.0000 và chọn giữ nguyên số tiền mua. | Đơn giá mua dẫn xuất được tính lại và lưu đầy đủ chữ số (khoảng 26,666.6667 VNĐ), giá gốc giữ 16,000,000.00 VNĐ, ví MB Bank không đổi. |
| TC-INV-01.25 | Như TC-INV-01.24, người dùng chọn giữ nguyên đơn giá mua. | Số tiền mua thành 16,307,693.56 VNĐ (đơn giá dẫn xuất đầy đủ chữ số nhân 600.0000), giá gốc 16,307,693.56 VNĐ, ví MB Bank bị trừ thêm 307,693.56 VNĐ. |
| TC-INV-01.26 | Lệnh mua A (100.0000 đơn vị, giá gốc 2,000,000.00 VNĐ, không có chi phí mua) đã ghi hoàn vốn 1,500,000.00 VNĐ (giá vốn còn lại 500,000.00 VNĐ). Người dùng sửa số tiền mua xuống 1,000,000.00 VNĐ. | Giá gốc mới 1,000,000.00 VNĐ, ví thanh toán được hoàn lại 1,000,000.00 VNĐ. Bước xác nhận hiển thị giá vốn còn lại về 0.00 VNĐ và phần vượt 500,000.00 VNĐ cộng vào lãi đã chốt của tài sản. |

#### 4.7.2 FR-INV-02: Cập nhật đơn giá hoặc giá trị thị trường

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-INV-02 |
| Tên | Cập nhật đơn giá hoặc giá trị thị trường |
| Nhóm chức năng | Tài sản đầu tư (FR-INV) |
| Ca sử dụng liên quan | UC-INV-01, UC-INV-03 |
| Mô tả kỹ thuật | Hệ thống PHẢI tiếp nhận đơn giá hiện hành hoặc giá trị thị trường tổng của tài sản đầu tư, người dùng nhập một trong hai. Hệ thống PHẢI định giá lại các lệnh mua còn nắm giữ theo quy tắc BR-FIN-06. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Tên tài sản đầu tư | Chuỗi ký tự | Tên của tài sản đầu tư đã có ít nhất một lệnh mua còn nắm giữ. | Bắt buộc |
| Ngày định giá | Ngày tháng (YYYY-MM-DD) | Ngày xác lập giá, không vượt quá ngày hiện tại. | Bắt buộc |
| Đơn giá hiện hành | Số thập phân chính xác | Giá hiện hành của một đơn vị, $NewPrice \ge 0$, lưu đúng như nhập. | Tùy chọn, nhập một trong hai Đơn giá hiện hành hoặc Giá trị thị trường tổng |
| Giá trị thị trường tổng | Số tiền (VNĐ) | Giá trị thị trường ước tính của toàn bộ tài sản, $MarketValue_{total} \ge 0$, 2 chữ số thập phân. | Tùy chọn, nhập một trong hai Đơn giá hiện hành hoặc Giá trị thị trường tổng |

Quy tắc xử lý và điều kiện:

1. Kiểm tra tài sản: Hệ thống PHẢI kiểm tra tên tài sản đầu tư đã có trong hệ thống và còn ít nhất một lệnh mua còn đơn vị nắm giữ. Nếu không tìm thấy hoặc tài sản đã bán hết, Hệ thống PHẢI báo lỗi nghiệp vụ không tìm thấy tài sản đầu tư đang nắm giữ.
2. Kiểm tra cách cập nhật: Hệ thống PHẢI chấp nhận đúng một trong hai giá trị Đơn giá hiện hành hoặc Giá trị thị trường tổng. Nếu nhập cả hai hoặc không nhập giá trị nào, Hệ thống PHẢI từ chối và báo lỗi nghiệp vụ thiếu hoặc thừa giá trị định giá.
3. Kiểm tra giá trị: Hệ thống PHẢI kiểm tra $NewPrice \ge 0$, nếu nhỏ hơn 0 báo lỗi `ERR-INV-001`, và kiểm tra $MarketValue_{total} \ge 0$, nếu nhỏ hơn 0 báo lỗi nghiệp vụ giá trị thị trường tổng không hợp lệ. Làm tròn giá trị thị trường tổng về 2 chữ số thập phân theo BR-FIN-01. Đơn giá hiện hành nhập được lưu đúng như nhập.
4. Kiểm tra ngày định giá: Ngày định giá không vượt quá ngày hiện tại. Không cho phép nhập giá của tương lai.
5. Ghi nhận lịch sử giá:
   - Hệ thống PHẢI lưu bản ghi gồm tên tài sản, ngày định giá và giá trị mới vào lịch sử giá của tài sản. Giá trị mới là đơn giá hiện hành hoặc giá trị thị trường tổng người dùng đã nhập.
   - Đơn giá mua của lệnh mua mới theo FR-INV-01 quy tắc 8 cũng được lưu vào lịch sử giá như một lần định giá vào ngày mua.
   - Nếu ngày định giá cũ hơn ngày định giá gần nhất, hệ thống chỉ lưu vào lịch sử, giữ nguyên đơn giá hiện hành và không thực hiện quy tắc 6 và 7.
6. Định giá lại: Nếu người dùng nhập Giá trị thị trường tổng, đơn giá hiện hành được tính bằng giá trị này chia tổng số đơn vị còn nắm giữ của tài sản theo BR-FIN-06. Giá trị thị trường của từng lệnh mua $i$ còn $Units_{remain,i}$ đơn vị được tính lại, làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng:
$$MarketValue_{remain,i} = Units_{remain,i} \times CurrentPrice$$
Giá trị thị trường tổng của toàn bộ tài sản đầu tư là tổng giá trị các lệnh mua còn đơn vị:
$$MarketValue_{total} = \sum_{i} MarketValue_{remain,i}$$
7. Phần dư làm tròn: Khi nhập Giá trị thị trường tổng, phần dư dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua còn đơn vị. Nhờ vậy tổng giá trị các lệnh mua bằng đúng giá trị đã nhập.
8. Bảo toàn tiền và giá gốc: Thao tác cập nhật giá không làm thay đổi số dư ví, số lượng đơn vị nắm giữ hay giá gốc của các lệnh mua.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi lịch sử giá mới của tài sản. Khi ngày định giá không cũ hơn ngày định giá gần nhất, đơn giá hiện hành được cập nhật và giá trị thị trường của từng lệnh mua được tính lại; ngược lại chỉ lưu lịch sử giá. |
| Biến động số dư | Không làm biến động số dư tiền mặt. Khi ngày định giá không cũ hơn ngày định giá gần nhất, tổng tài sản đầu tư $Investments$ và tài sản ròng $NetWorth$ thay đổi đúng bằng chênh lệch giá trị thị trường của tài sản; ngược lại không đổi. |
| Thông báo phản hồi | Thông báo cập nhật giá thành công kèm ngày áp dụng và giá trị thị trường mới của tài sản. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-INV-02.1 | Tài sản "DCDS" còn một lệnh mua 300.0000 đơn vị. Người dùng cập nhật đơn giá ngày hôm nay lên 32,450.5000 VNĐ. | Hệ thống lưu đơn giá mới, giá trị thị trường của lệnh mua là 9,735,150.00 VNĐ. |
| TC-INV-02.2 | Tài sản "VESAF" còn hai lệnh mua 200.0000 và 100.0000 đơn vị. Người dùng cập nhật đơn giá lên 30,000.0000 VNĐ. | Giá trị thị trường của hai lệnh mua là 6,000,000.00 VNĐ và 3,000,000.00 VNĐ, tổng 9,000,000.00 VNĐ. |
| TC-INV-02.3 | Tài sản "Đất lô 7" có hai lệnh mua còn mở 80.0000 và 40.0000 đơn vị với giá gốc 2,000,000,000.00 VNĐ và 1,000,000,000.00 VNĐ. Người dùng nhập giá trị thị trường tổng 4,500,000,000.00 VNĐ. | Đơn giá hiện hành là 37,500,000.0000 VNĐ. Giá trị thị trường của hai lệnh mua là 3,000,000,000.00 VNĐ và 1,500,000,000.00 VNĐ. |
| TC-INV-02.4 | Tài sản có ba lệnh mua còn mở, mỗi lệnh 1.0000 đơn vị, giá gốc 1,000,000.00 VNĐ, ngày mua khác nhau. Người dùng nhập giá trị thị trường tổng 1,000,000.00 VNĐ. | Hai lệnh mua cũ nhận 333,333.33 VNĐ, lệnh mua có ngày mua mới nhất nhận phần dư 333,333.34 VNĐ, tổng đúng 1,000,000.00 VNĐ. |
| TC-INV-02.5 | Người dùng nhập ngày định giá là một ngày trong tương lai. | Hệ thống từ chối cập nhật và báo lỗi nghiệp vụ ngày định giá không hợp lệ. |
| TC-INV-02.6 | Người dùng nhập đơn giá hiện hành là số âm. | Hệ thống từ chối và báo lỗi `ERR-INV-001`. |
| TC-INV-02.7 | Người dùng nhập cả đơn giá hiện hành và giá trị thị trường tổng, hoặc không nhập giá trị nào. | Hệ thống từ chối và báo lỗi nghiệp vụ thiếu hoặc thừa giá trị định giá. |
| TC-INV-02.8 | Người dùng nhập giá trị thị trường tổng là số âm. | Hệ thống từ chối và báo lỗi nghiệp vụ giá trị thị trường tổng không hợp lệ. |
| TC-INV-02.9 | Người dùng cập nhật đơn giá cho tài sản đã bán hết toàn bộ lệnh mua hoặc nhập tên chưa có. | Hệ thống từ chối và báo lỗi nghiệp vụ không tìm thấy tài sản đầu tư đang nắm giữ. |
| TC-INV-02.10 | Đơn giá VESAF đã cập nhật ngày 10/10 là 26,500.0000 VNĐ. Người dùng nhập bổ sung đơn giá 25,800.0000 VNĐ cho ngày 05/10. | Hệ thống lưu giá ngày 05/10 vào lịch sử, giữ nguyên đơn giá hiện hành 26,500.0000 VNĐ và giá trị thị trường các lệnh mua không đổi. |
| TC-INV-02.11 | Tài sản "ABC" có 1,000.0000 đơn vị, giá gốc 100,000,000.00 VNĐ. Người dùng nhập giá trị thị trường tổng 0.00 VNĐ sau khi hủy niêm yết. | Đơn giá hiện hành là 0.0000 VNĐ, giá trị thị trường 0.00 VNĐ, lãi lỗ chưa chốt là -100,000,000.00 VNĐ, tài sản vẫn nắm giữ. |

#### 4.7.3 FR-INV-03: Xem giá trị thị trường, lãi lỗ và tỷ suất sinh lời

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-INV-03 |
| Tên | Xem giá trị thị trường, lãi lỗ và tỷ suất sinh lời |
| Nhóm chức năng | Tài sản đầu tư (FR-INV) |
| Ca sử dụng liên quan | UC-INV-04, UC-INV-06, UC-INV-07, UC-INV-08 |
| Mô tả kỹ thuật | Hệ thống PHẢI tính giá trị thị trường, lãi lỗ đã chốt, lãi lỗ chưa chốt, tỷ suất sinh lời và tỷ suất nội hoàn theo quy tắc BR-FIN-06 và BR-FIN-08. Phạm vi tính là từng lệnh mua, tài sản đầu tư, khoản đầu tư hoặc toàn bộ tài sản đầu tư. Thao tác chỉ đọc. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Phạm vi xem | Lựa chọn | Tài sản đầu tư, Khoản đầu tư hoặc Tất cả. Mặc định Tất cả. | Tùy chọn |
| Tên tài sản đầu tư | Chuỗi ký tự | Tên tài sản đầu tư đã có ít nhất một lệnh mua. | Bắt buộc khi phạm vi là Tài sản đầu tư |
| Tên khoản đầu tư | Chuỗi ký tự | Tên khoản đầu tư đã tạo theo FR-INV-06. | Bắt buộc khi phạm vi là Khoản đầu tư |

Quy tắc xử lý và điều kiện:

1. Xác định phạm vi và ngày tính toán $Date_{calc}$ theo BR-FIN-08:
  - Phạm vi Tài sản đầu tư gồm mọi lệnh mua của tài sản đó, kể cả lệnh mua đã bán hết.
  - Phạm vi Khoản đầu tư gồm mọi lệnh mua đang gán vào khoản theo FR-INV-06.
  - Phạm vi Tất cả gồm mọi lệnh mua, kể cả lệnh mua chưa gán khoản.
  - Lệnh mua còn đơn vị có $Date_{calc}$ là ngày hiện tại. Lệnh mua đã bán hết có $Date_{calc}$ là ngày của lệnh bán cuối cùng khấu trừ lệnh mua đó.
2. Kiểm tra dữ liệu: Nếu tên tài sản đầu tư hoặc khoản đầu tư không có trong hệ thống, Hệ thống PHẢI báo lỗi nghiệp vụ không tìm thấy. Nếu $Date_{calc}$ của một lệnh mua trước ngày mua của chính lệnh đó (dữ liệu không nhất quán), Hệ thống PHẢI từ chối tính toàn bộ phạm vi chứa lệnh mua đó và báo `ERR-INV-007` kèm mã của lệnh mua đó.
3. Xác định giá trị thị trường $MarketValue_{remain,i}$ của từng lệnh mua $i$ còn đơn vị theo BR-FIN-06, làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng:
  - $MarketValue_{remain,i} = Units_{remain,i} \times CurrentPrice$. Đơn giá hiện hành là đơn giá của lần định giá gần nhất theo FR-INV-02, hoặc đơn giá của lệnh mua có ngày mua mới nhất, không cũ hơn lần định giá đó theo FR-INV-01. Cùng ngày mua thì lấy lệnh mua có số thứ tự ghi lớn nhất.
  - Lệnh mua đã bán hết có $MarketValue_{remain,i} = 0.00$ VNĐ.
4. Xác định lãi lỗ đã chốt và lãi lỗ chưa chốt của từng lệnh mua theo BR-FIN-08 khoản 4:
$$RealizedPL_i = Proceeds_{sold,i} - CostBasis_{sold,i} + RealizedPL_{late,i}$$
$$UnrealizedPL_i = MarketValue_{remain,i} - CostBasis_{remain,i}$$
  Tiền bán $Proceeds_{sold,i}$ là phần tiền thực nhận sau chi phí bán, phân bổ cho lệnh mua $i$ theo số đơn vị khấu trừ. Làm tròn 2 chữ số thập phân, phần dư dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua bị khấu trừ. Hệ thống dùng giá vốn xuất bán đã tính lại khi ghi lệnh bán lùi ngày theo FR-INV-04 hoặc khi ghi sự kiện có Ngày nhận sớm hơn lệnh bán theo FR-INV-05.
5. Tính tỷ suất cả lệnh $ROI_{order,i}$ theo BR-FIN-08 khoản 3:
$$ROI_{order,i} = \frac{Proceeds_{sold,i} + MarketValue_{remain,i} + Dividend_i + ReturnCapital_i - CostBasis_{origin,i}}{CostBasis_{origin,i}}$$
  Với $HoldDays < 365$, hệ thống hiển thị tỷ suất đơn. Với $HoldDays \ge 365$, hệ thống hiển thị tỷ suất kép quy đổi năm:
$$CAGR_{order,i} = \left(\frac{Proceeds_{sold,i} + MarketValue_{remain,i} + Dividend_i + ReturnCapital_i}{CostBasis_{origin,i}}\right)^{\frac{365}{HoldDays}} - 1$$
6. Tính tỷ suất phần đang giữ, chỉ cho lệnh mua còn đơn vị. Tỷ suất này luôn là phần trăm đơn, không quy đổi năm:
$$ROI_{holding,i} = \frac{MarketValue_{remain,i} - CostBasis_{remain,i}}{CostBasis_{remain,i}}$$
  Hệ thống chỉ hiển thị một dòng tỷ suất khi lệnh mua chưa từng bán, chưa nhận tiền, chưa hoàn vốn và có thời gian nắm giữ dưới 365 ngày ($HoldDays < 365$). Khi $HoldDays \ge 365$, tỷ suất cả lệnh được quy đổi kép theo năm còn tỷ suất phần đang giữ là tỷ suất đơn nên hệ thống hiển thị cả hai dòng theo QD-242. Đối với lệnh mua đã phát sinh nhận tiền mặt, hoàn vốn hoặc bán một phần, hệ thống bắt buộc hiển thị đồng thời hai chỉ số theo QD-141.
7. Tổng hợp theo tài sản đầu tư, khoản đầu tư hoặc Tất cả bằng phép cộng số thập phân chính xác các đại lượng của lệnh mua trong phạm vi, làm tròn 2 chữ số thập phân. Tỷ suất trên tổng giá gốc là phần trăm đơn, không quy đổi năm vì các lệnh mua có ngày mua khác nhau:
$$ROI_{total} = \frac{\sum_{i} Proceeds_{sold,i} + \sum_{i} MarketValue_{remain,i} + \sum_{i} Dividend_i + \sum_{i} ReturnCapital_i - \sum_{i} CostBasis_{origin,i}}{\sum_{i} CostBasis_{origin,i}}$$
  Chỉ số $i$ chạy trên các lệnh mua trong phạm vi đã chọn.
8. Tính tỷ suất nội hoàn theo BR-FIN-08 khoản 5 cho tài sản đầu tư, khoản đầu tư và Tất cả, trên dòng tiền gộp của các lệnh mua trong phạm vi:
  - Dòng tiền ra là giá gốc của từng lệnh mua tại ngày mua.
  - Dòng tiền vào là tiền bán thực nhận, tiền nhận và hoàn vốn tại ngày nhận.
  - Mốc cuối kỳ $t_n$ là ngày hiện tại nếu phạm vi còn lệnh mua có đơn vị, ngược lại là ngày muộn nhất trong các dòng tiền. Dòng tiền cuối kỳ là giá trị thị trường của các lệnh mua còn đơn vị tại $t_n$.
  - Gọi $r$ là nghiệm tỷ suất năm của phương trình theo BR-FIN-08. Khoảng từ $t_0$ đến $t_n$ từ 365 ngày trở lên hiển thị $r$. Dưới 365 ngày hiển thị tỷ suất kỳ tích lũy:
$$IRR_{period} = (1 + r)^{\frac{t_n - t_0}{365}} - 1$$
  - Khi không có nghiệm duy nhất, chuỗi dòng tiền một chiều hoặc thuật toán không hội tụ sau 100 vòng lặp, hệ thống hiển thị "N/A" kèm ghi chú giải thích đời thường theo `QD-120`. Khi đó, hệ thống vẫn hiển thị đầy đủ hai chỉ số trực quan gồm lãi lỗ thực tế và tỷ suất trên tổng giá gốc.
9. Kiểm tra giá gốc:
   - Nếu giá gốc hoặc giá vốn còn lại dùng làm mẫu số bằng 0, Hệ thống PHẢI hiển thị tỷ suất tương ứng ở trạng thái "N/A" theo `ERR-INV-008`. Lãi lỗ vẫn tính theo công thức (giá trị thị trường trừ giá vốn còn lại).
   - Nếu phạm vi không có lệnh mua, Hệ thống PHẢI hiển thị lãi lỗ bằng 0.00 VNĐ, tỷ suất và tỷ suất nội hoàn "N/A". Khi đó, Hệ thống hiển thị thông điệp "Phạm vi đã chọn chưa có lệnh mua nào nên chưa tính được tỷ suất." và không gán mã lỗi theo `QD-227`.
10. Làm tròn mọi tỷ suất thành phần trăm 2 chữ số thập phân theo quy tắc làm tròn ngân hàng nửa chẵn theo `QD-121`. Thao tác không làm thay đổi số dư ví, số lượng đơn vị hay giá vốn.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bảng theo lệnh mua gồm giá gốc, giá vốn còn lại, số đơn vị còn, giá trị thị trường, lãi lỗ đã chốt, lãi lỗ chưa chốt, tỷ suất cả lệnh, tỷ suất phần đang giữ. Dòng tổng hợp theo phạm vi gồm các tổng tiền, tỷ suất trên tổng giá gốc và tỷ suất nội hoàn. Mỗi tài sản đầu tư kèm ngày định giá gần nhất của đơn giá hiện hành. |
| Biến động số dư | Thao tác đọc, không làm biến động số dư. |
| Thông báo phản hồi | Bảng tổng hợp kèm nhãn "chưa đủ 365 ngày" cho tỷ suất đơn hoặc kỳ tích lũy, và thông điệp giải thích khi tỷ suất ở trạng thái "N/A". |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-INV-03.1 | Hôm nay là 11/10/2026. Tài sản VESAF có một lệnh mua ngày 11/10/2025, giá gốc 10,000,000.00 VNĐ, giá trị thị trường 12,000,000.00 VNĐ, chưa bán. | Lãi lỗ chưa chốt +2,000,000.00 VNĐ, tỷ suất cả lệnh 20.00% (365 ngày, quy đổi năm 20.00%), tỷ suất phần đang giữ 20.00%, hiển thị cả hai dòng theo QD-242. |
| TC-INV-03.2 | Lệnh mua mới 15 ngày, giá gốc 10,000,000.00 VNĐ, giá trị thị trường 10,200,000.00 VNĐ. | Tỷ suất cả lệnh 2.00% dạng tỷ suất đơn, gắn nhãn chưa đủ 365 ngày, không quy đổi năm. |
| TC-INV-03.3 | VESAF ngày 11/10/2025 mua 400.0000 đơn vị, giá gốc 10,050,000.00 VNĐ gồm chi phí mua 50,000.00 VNĐ. Ngày 11/04/2026 bán 100.0000 đơn vị, nhận 3,000,000.00 VNĐ, chi phí bán 3,000.00 VNĐ. Hôm nay đơn giá hiện hành 28,000.0000 VNĐ. | Giá vốn xuất bán 2,512,500.00 VNĐ, lãi lỗ đã chốt +484,500.00 VNĐ. Giá trị còn lại 8,400,000.00 VNĐ, giá vốn còn lại 7,537,500.00 VNĐ, lãi lỗ chưa chốt +862,500.00 VNĐ. Tỷ suất cả lệnh 13.40%, tỷ suất phần đang giữ 11.44%. |
| TC-INV-03.4 | Tài sản "Đất lô 7" có một lệnh mua 1.0000 đơn vị, mua 01/06/2026, giá gốc 2,040,000,000.00 VNĐ gồm chi phí mua 40,000,000.00 VNĐ. Người dùng nhập giá trị thị trường tổng 2,300,000,000.00 VNĐ. | Lãi lỗ chưa chốt +260,000,000.00 VNĐ, tỷ suất cả lệnh 12.75% dạng tỷ suất đơn (132 ngày). |
| TC-INV-03.5 | Lệnh mua ngày 11/10/2024, giá gốc 10,000,000.00 VNĐ, giá trị thị trường 12,100,000.00 VNĐ, chưa bán, hôm nay 11/10/2026. | Thời gian nắm giữ 730 ngày, tỷ suất kép quy đổi năm 10.00%/năm. |
| TC-INV-03.6 | Lệnh mua ngày 11/10/2025, giá gốc 10,000,000.00 VNĐ, giá trị thị trường 10,500,000.00 VNĐ, đã nhận tiền mặt 500,000.00 VNĐ. | Tỷ suất cả lệnh 10.00%. Tỷ suất phần đang giữ 5.00%, hiển thị cả hai dòng. |
| TC-INV-03.7 | Lệnh mua ngày 11/10/2025, giá gốc 10,000,000.00 VNĐ, đã hoàn vốn 1,000,000.00 VNĐ, giá trị thị trường 9,500,000.00 VNĐ. | Giá vốn còn lại 9,000,000.00 VNĐ, lãi lỗ chưa chốt +500,000.00 VNĐ, tỷ suất cả lệnh 5.00% trên giá gốc 10,000,000.00 VNĐ, tỷ suất phần đang giữ 5.56%. |
| TC-INV-03.8 | Lệnh mua ngày 11/01/2026, giá gốc 5,000,000.00 VNĐ, bán hết ngày 11/07/2026, nhận 5,400,000.00 VNĐ sau chi phí bán. | Lãi lỗ đã chốt +400,000.00 VNĐ, lãi lỗ chưa chốt 0.00 VNĐ, tỷ suất cả lệnh 8.00% (181 ngày). Không hiển thị tỷ suất phần đang giữ. |
| TC-INV-03.9 | Tài sản DCDS có lệnh mua A ngày 01/03/2026, 100.0000 đơn vị, giá gốc 1,000,000.00 VNĐ, và lệnh mua B ngày 10/03/2026, 100.0000 đơn vị, giá gốc 1,200,000.00 VNĐ. Lệnh bán 100.0000 đơn vị ngày 25/03 nhận 1,400,000.00 VNĐ lấy lệnh mua A (lãi +400,000.00 VNĐ). Người dùng ghi lùi lệnh bán 100.0000 đơn vị ngày 15/03 nhận 1,300,000.00 VNĐ. | Hệ thống hiển thị lãi lỗ đã chốt theo giá trị tính lại: lệnh bán 15/03 lấy lệnh mua A (+300,000.00 VNĐ), lệnh bán 25/03 lấy lệnh mua B (+200,000.00 VNĐ). Tổng đã chốt +500,000.00 VNĐ. |
| TC-INV-03.10 | Lệnh mua A mua 11/01/2026, 100.0000 đơn vị, giá gốc 1,000,000.00 VNĐ, bán hết 100.0000 đơn vị ngày 10/04 nhận 1,300,000.00 VNĐ. Sự kiện hoàn vốn 100,000.00 VNĐ, ngày nhận 20/04. | Hệ thống cảnh báo phần hoàn vốn vượt giá vốn còn lại (100,000.00 VNĐ) và chờ xác nhận. Sau khi xác nhận: giá vốn xuất bán giữ nguyên 1,000,000.00 VNĐ, lãi lỗ đã chốt của lệnh bán +300,000.00 VNĐ, khoản hoàn vốn cộng thêm 100,000.00 VNĐ vào lãi đã chốt của tài sản (tổng +400,000.00 VNĐ), tỷ suất cả lệnh 40.00%. |
| TC-INV-03.11 | Khoản "Hưu trí" gồm lệnh mua VESAF ngày 11/10/2025 giá gốc 10,000,000.00 VNĐ giá trị 11,000,000.00 VNĐ, và lệnh mua Vàng SJC ngày 11/04/2026 giá gốc 4,000,000.00 VNĐ giá trị 4,200,000.00 VNĐ. Hôm nay 11/10/2026. | Lãi lỗ chưa chốt +1,200,000.00 VNĐ, tỷ suất trên tổng giá gốc 8.57%, tỷ suất nội hoàn 10.04%/năm. |
| TC-INV-03.12 | Người dùng xem Tất cả: ngoài khoản "Hưu trí" của TC-INV-03.11 còn một lệnh mua chưa gán khoản DCDS ngày 11/08/2026, giá gốc 5,000,000.00 VNĐ, giá trị 4,800,000.00 VNĐ. | Tổng giá gốc 19,000,000.00 VNĐ, tổng giá trị 20,000,000.00 VNĐ, lãi lỗ chưa chốt +1,000,000.00 VNĐ, tỷ suất trên tổng giá gốc 5.26%, tỷ suất nội hoàn 7.83%/năm. |
| TC-INV-03.13 | VESAF có lệnh mua 400.0000 đơn vị giá gốc 10,000,000.00 VNĐ, đơn giá cập nhật 26,000.0000 VNĐ ngày 10/10. Ngày 11/10 người dùng mua thêm 5,000,000.00 VNĐ tại 26,500.0000 VNĐ (188.6792 đơn vị hiển thị, lưu đầy đủ chữ số, không chi phí mua). | Đơn giá hiện hành là 26,500.0000 VNĐ. Giá trị hai lệnh mua là 10,600,000.00 VNĐ và 5,000,000.00 VNĐ, tổng 15,600,000.00 VNĐ, lãi lỗ chưa chốt +600,000.00 VNĐ. |
| TC-INV-03.14 | "Đất lô 7" có một lệnh mua 1.0000 đơn vị, giá gốc 2,040,000,000.00 VNĐ. Người dùng nhập giá trị thị trường tổng 0.00 VNĐ. | Lãi lỗ chưa chốt -2,040,000,000.00 VNĐ, tỷ suất cả lệnh -100.00%. Tỷ suất nội hoàn "N/A" kèm thông điệp giải thích, lãi lỗ vẫn hiển thị. |
| TC-INV-03.15 | Người dùng xem khoản "Dự phòng" vừa tạo, chưa có lệnh mua nào. | Lãi lỗ bằng 0.00 VNĐ, tỷ suất và tỷ suất nội hoàn "N/A", thông điệp "Phạm vi đã chọn chưa có lệnh mua nào nên chưa tính được tỷ suất.", không có mã lỗi. |
| TC-INV-03.16 | Kiểm thử dữ liệu không nhất quán: một lệnh mua đã bán hết có ngày bán cuối cùng 05/03/2026 trước ngày mua 10/03/2026. | Hệ thống từ chối tính phạm vi chứa lệnh mua đó và báo `ERR-INV-007` kèm mã của lệnh mua đó: "Dữ liệu ngày tính toán không nhất quán với ngày mua của lệnh." |
| TC-INV-03.17 | Người dùng chọn phạm vi Tài sản đầu tư và nhập tên "ABC123", tài sản này chưa từng có lệnh mua. | Hệ thống báo lỗi nghiệp vụ không tìm thấy tài sản đầu tư "ABC123", không hiển thị số liệu. |
| TC-INV-03.18 | Người dùng chọn phạm vi Khoản đầu tư và nhập tên "Mua xe", khoản này chưa được tạo. | Hệ thống báo lỗi nghiệp vụ không tìm thấy khoản đầu tư "Mua xe", không hiển thị số liệu. |
| TC-INV-03.19 | Như TC-INV-03.13, người dùng ghi thêm lệnh mua lùi ngày 01/10 (cũ hơn lần định giá 10/10) số tiền 5,000,000.00 VNĐ tại 24,000.0000 VNĐ. | Đơn giá hiện hành giữ 26,500.0000 VNĐ. Lệnh mua mới 208.3333 đơn vị hiển thị (lưu đầy đủ chữ số), giá trị 5,520,833.33 VNĐ, lãi lỗ chưa chốt +520,833.33 VNĐ. |
| TC-INV-03.20 | "Đất lô 7" có lệnh mua P1 80.0000 đơn vị, giá gốc 2,040,000,000.00 VNĐ, giá trị thị trường 2,300,000,000.00 VNĐ (đơn giá hiện hành 28,750,000.0000 VNĐ). Người dùng mua thêm lệnh mua P2 20.0000 đơn vị, số tiền 600,000,000.00 VNĐ, chi phí mua 10,000,000.00 VNĐ, ngày mua không cũ hơn ngày định giá gần nhất. | Đơn giá hiện hành thành 30,000,000.0000 VNĐ. Giá trị P2 là 600,000,000.00 VNĐ, lãi lỗ chưa chốt của P2 là -10,000,000.00 VNĐ. Giá trị P1 tính lại thành 2,400,000,000.00 VNĐ. |
| TC-INV-03.21 | Lệnh mua ngày 11/10/2025, giá gốc 10,000,000.00 VNĐ, đã hoàn giá gốc 10,000,000.00 VNĐ (giá vốn còn lại 0.00 VNĐ), giá trị thị trường 9,500,000.00 VNĐ. | Lãi lỗ chưa chốt +9,500,000.00 VNĐ, tỷ suất cả lệnh 95.00%. Tỷ suất phần đang giữ "N/A" theo `ERR-INV-008`. |
| TC-INV-03.22 | Khoản "Tích lũy" gồm lệnh mua ngày 11/01/2026 giá gốc 10,000,000.00 VNĐ giá trị 10,600,000.00 VNĐ, và lệnh mua ngày 11/05/2026 giá gốc 5,000,000.00 VNĐ giá trị 5,200,000.00 VNĐ. Hôm nay 11/10/2026. | Từ dòng tiền đầu tiên đến nay 273 ngày. Tỷ suất trên tổng giá gốc 5.33%, tỷ suất nội hoàn kỳ tích lũy 6.27% gắn nhãn chưa đủ 365 ngày. |
| TC-INV-03.23 | Khoản "Kinh doanh nhỏ" gồm tài sản "Quán cà phê" (mua 11/10/2024 giá gốc 10,000,000.00 VNĐ, bán hết 11/10/2025 nhận 23,000,000.00 VNĐ) và tài sản "Xe tải" (mua 11/04/2026 giá gốc 12,000,000.00 VNĐ). Người dùng nhập giá trị thị trường tổng của Xe tải bằng 0.00 VNĐ. Hôm nay 11/10/2026. | Lãi lỗ đã chốt +13,000,000.00 VNĐ, lãi lỗ chưa chốt -12,000,000.00 VNĐ, tổng +1,000,000.00 VNĐ, tỷ suất trên tổng giá gốc 4.55%. Phương trình có hai nghiệm (-60.36% và +20.78%), tỷ suất nội hoàn hiển thị "N/A" kèm thông điệp giải thích. |
| TC-INV-03.24 | Lệnh mua A ngày 11/04/2026, 100.0000 đơn vị, giá gốc 2,000,000.00 VNĐ, đã nhận hoàn vốn 2,500,000.00 VNĐ (giá vốn còn lại 0.00 VNĐ, lãi đã chốt +500,000.00 VNĐ), đơn giá hiện hành 50,000.00 VNĐ. | Giá trị thị trường 5,000,000.00 VNĐ, lãi lỗ chưa chốt +5,000,000.00 VNĐ, tỷ suất phần đang giữ "N/A" theo `ERR-INV-008`, tỷ suất cả lệnh 275.00% (5,000,000.00 + 2,500,000.00 - 2,000,000.00 chia 2,000,000.00). |

#### 4.7.4 FR-INV-04: Ghi nhận lệnh bán tài sản đầu tư

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-INV-04 |
| Tên | Ghi nhận lệnh bán tài sản đầu tư |
| Nhóm chức năng | Tài sản đầu tư (FR-INV) |
| Ca sử dụng liên quan | UC-INV-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI ghi nhận mỗi lần bán thành một lệnh bán độc lập và khấu trừ đơn vị cùng giá vốn của các lệnh mua theo phương pháp nhập trước xuất trước. Hệ thống PHẢI cộng số tiền thực nhận vào ví chỉ định và xác định lãi lỗ đã chốt theo quy tắc BR-FIN-06. Khi lệnh bán được ghi lùi ngày, hệ thống PHẢI tính lại các lệnh bán và phân bổ của các sự kiện phát sinh sau ngày đó. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Tên tài sản đầu tư | Chuỗi ký tự | Tên của tài sản đầu tư đã có lệnh mua, so khớp không phân biệt chữ hoa chữ thường. | Bắt buộc |
| Ngày bán | Ngày tháng (YYYY-MM-DD) | Ngày khớp lệnh bán, không vượt quá ngày hiện tại. | Bắt buộc |
| Cách bán | Lựa chọn | Bán một phần hoặc Bán toàn bộ, áp dụng cho mọi tài sản đầu tư. | Bắt buộc |
| Số đơn vị bán | Số thập phân chính xác | $0 < Units_{sell} \le Units_{held,total}$, tối đa 4 chữ số thập phân. Nhập bằng số hiển thị của lệnh vẫn là bán một phần, phần dư số lượng đầy đủ ở lại lệnh. Bỏ qua nếu chọn Bán toàn bộ. | Bắt buộc khi Bán một phần |
| Đơn giá bán | Số thập phân chính xác | Giá của một đơn vị tại ngày bán, $Price_{sell} \ge 0$, lưu đúng như nhập. Nếu không nhập thì hệ thống tính từ Số tiền bán nhận về và số đơn vị bán. | Tùy chọn, nhập một trong hai Đơn giá bán hoặc Số tiền bán nhận về |
| Số tiền bán nhận về | Số tiền (VNĐ) | Tổng số tiền bán trước chi phí bán, $GrossProceeds \ge 0$, 2 chữ số thập phân theo BR-FIN-01. Nếu không nhập thì hệ thống tính từ đơn giá bán và số đơn vị bán. | Tùy chọn, nhập một trong hai Đơn giá bán hoặc Số tiền bán nhận về |
| Chi phí bán | Số tiền (VNĐ) | Phí, thuế hoặc khoản tương tự do người dùng nhập, $Fee_{sell} \ge 0$, 2 chữ số thập phân, mặc định 0.00 VNĐ. | Tùy chọn |
| Mã định danh ví | Mã định danh | Mã ví nhận tiền bán, ví ở trạng thái `ACTIVE`. | Bắt buộc |

Quy tắc xử lý và điều kiện:

1. Xác định số đơn vị nắm giữ ngay trước lệnh bán: Hệ thống PHẢI tìm tài sản đầu tư theo tên. Lệnh bán được xếp vào thứ tự xử lý theo ngày giao dịch, rồi số thứ tự ghi do hệ thống tự ghi (BR-FIN-06 mục 3.6 điều 8). Số đơn vị nắm giữ $Units_{held,total} = \sum_{i} Units_{held,i}$ bằng tổng đơn vị của các lệnh mua đứng trước lệnh bán, cộng đơn vị nhận thêm từ các sự kiện của FR-INV-05 đứng trước lệnh bán, trừ tổng đơn vị của các lệnh bán đứng trước.
  - Sau khi $Units_{sell}$ đã qua kiểm tra số lượng của quy tắc 2 (lớn hơn 0, tối đa 4 chữ số thập phân khi chọn Bán một phần), nếu $Units_{sell} > Units_{held,total}$, hệ thống PHẢI từ chối và báo lỗi `ERR-INV-009`. Thông báo kèm số đơn vị nắm giữ thực tế tại Ngày bán.
  - Khi chọn Bán toàn bộ, $Units_{sell} = Units_{held,total}$ tại Ngày bán tính trên số lượng đầy đủ, lệnh về 0 đơn vị, các lệnh mua đứng sau lệnh bán theo thứ tự xử lý không bị tác động.
2. Kiểm tra dữ liệu đầu vào:
   - Hệ thống PHẢI kiểm tra ví nhận tiền ở trạng thái `ACTIVE`.
   - Hệ thống PHẢI kiểm tra $Units_{sell} > 0$ (kể cả khi chọn Bán toàn bộ tại Ngày bán chưa có đơn vị nắm giữ) và, khi chọn Bán một phần, $Units_{sell}$ có tối đa 4 chữ số thập phân. Nếu vi phạm, hệ thống báo lỗi nghiệp vụ số lượng bán phải lớn hơn 0 và có tối đa 4 chữ số thập phân.
   - Hệ thống PHẢI kiểm tra người dùng nhập đúng một trong hai Đơn giá bán hoặc Số tiền bán nhận về. Nếu nhập cả hai hoặc không nhập giá trị nào thì từ chối và báo lỗi nghiệp vụ thiếu hoặc thừa đại lượng.
   - Hệ thống PHẢI kiểm tra $Price_{sell} \ge 0$ và báo lỗi `ERR-INV-001` nếu $Price_{sell} < 0$. Hệ thống PHẢI kiểm tra $GrossProceeds \ge 0$, nếu nhỏ hơn 0 báo lỗi nghiệp vụ số tiền bán nhận về không được âm.
   - Sau đó hệ thống PHẢI kiểm tra $Fee_{sell} \ge 0$, nếu nhỏ hơn 0 báo lỗi nghiệp vụ chi phí bán không được âm.
3. Xác định tổng số tiền bán theo BR-FIN-06:
  - Nhập Đơn giá bán: $GrossProceeds = Units_{sell} \times Price_{sell}$, làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng.
  - Nhập Số tiền bán nhận về: $GrossProceeds$ là số tiền người dùng nhập, đơn giá bán là đại lượng dẫn xuất giữ đủ độ chính xác để tích đơn giá nhân số đơn vị bán làm tròn 2 chữ số bằng $GrossProceeds$.
  - $GrossProceeds$ được phép bằng 0.00 VNĐ: lệnh bán chốt lỗ bằng toàn bộ giá vốn xuất bán, giao diện bắt người dùng nhập tường minh.
4. Xác định số tiền thực nhận: $NetAmount = GrossProceeds - Fee_{sell}$. Chi phí bán được trừ vào số tiền nhận. Nếu $NetAmount < 0$, hệ thống PHẢI từ chối và báo lỗi nghiệp vụ chi phí bán vượt số tiền bán.
5. Khấu trừ theo nhập trước xuất trước theo BR-FIN-06:
  - Lệnh mua có ngày mua cũ nhất bị khấu trừ trước. Các lệnh mua cùng ngày mua xếp theo số thứ tự ghi tăng dần.
  - Giá vốn xuất bán của lệnh mua $i$ bị khấu trừ $Units_{sold,i}$ đơn vị:
   $$CostBasis_{sold,i} = CostBasis_{remain,i} \times \frac{Units_{sold,i}}{Units_{remain,i}}$$
  - Khi bán hết lệnh mua $i$, giá vốn xuất bán bằng toàn bộ giá vốn còn lại của lệnh mua đó. Giá vốn xuất bán làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng nửa chẵn, giá vốn còn lại là hiệu $CostBasis_{remain,i}' = CostBasis_{remain,i} - CostBasis_{sold,i}$ nên phần dư làm tròn nằm ở giá vốn còn lại (QD-259).
  - Giá vốn bình quân $AvgCost$ chỉ để hiển thị, không dùng để khấu trừ giá vốn.
6. Xác định lãi lỗ đã chốt của lệnh bán:
$$RealizedPL = NetAmount - \sum_{i} CostBasis_{sold,i}$$
7. Tiếp nhận tiền về ví theo BR-FIN-02:
$$WalletBalance_{wallet}(\text{sau}) = WalletBalance_{wallet}(\text{trước}) + NetAmount$$
8. Tính lại khi ghi lùi ngày: Nếu tài sản đã có lệnh bán hoặc sự kiện với ngày sau Ngày bán mới, hệ thống PHẢI sắp xếp lại toàn bộ lệnh bán theo ngày bán, rồi số thứ tự ghi tăng dần. Sau đó, hệ thống thực hiện khấu trừ lại từ đầu.
  - Các lệnh bán cũ giữ nguyên số đơn vị và số tiền đã nhận, chỉ đổi lệnh mua bị khấu trừ, giá vốn xuất bán và lãi lỗ đã chốt.
  - Trước khi lưu, hệ thống PHẢI hiển thị danh sách lệnh bán bị ảnh hưởng kèm lãi lỗ đã chốt trước và sau, cùng cảnh báo quỹ mục tiêu nếu lệnh mua bị đổi khấu trừ đang gắn quỹ mục tiêu. Chỉ khi người dùng xác nhận, hệ thống mới lưu và ghi dấu vết lần tính lại.
  - Hệ thống cũng PHẢI tính lại phân bổ của các sự kiện đứng sau lệnh bán mới theo số đơn vị nắm giữ mới và hiển thị tác động trong cùng bước xác nhận. Nếu việc tính lại làm một hoàn vốn đứng sau vượt giá vốn còn lại, phần vượt được cộng vào lãi đã chốt và hiển thị trong cùng bước xác nhận.
  - Cơ chế này cũng áp dụng khi ghi sự kiện Thêm đơn vị cùng loại hoặc Hoàn vốn có Ngày nhận sớm hơn một lệnh bán hoặc một sự kiện đã ghi theo FR-INV-05 (quy tắc 6).
  - Nếu việc tính lại làm một lệnh bán sau đó không đủ đơn vị, hệ thống PHẢI từ chối toàn bộ lệnh bán mới với `ERR-INV-009` và không thay đổi dữ liệu nào.
9. Lưu lệnh bán: Hệ thống PHẢI ghi nhận lệnh bán độc lập, thuộc đúng một tài sản đầu tư.
   - Bản ghi lưu ngày bán, Thời điểm ghi và số thứ tự ghi do hệ thống tự ghi, số đơn vị, đơn giá bán hoặc số tiền bán nhận về và chi phí bán.
   - Hệ thống lưu thêm số tiền thực nhận, các lệnh mua bị khấu trừ, giá vốn xuất bán, lãi lỗ đã chốt và ví nhận tiền.
   - Số tiền thực nhận được phân bổ cho các lệnh mua bị khấu trừ theo `QD-141` để phục vụ hiển thị báo cáo.
   - Tài sản đã bán hết vẫn được giữ lại để ghi nhận sự kiện theo FR-INV-05.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Lệnh bán mới kèm định danh riêng, Thời điểm ghi và số thứ tự ghi do hệ thống tự ghi và lãi lỗ đã chốt. Các lệnh mua bị khấu trừ được cập nhật số đơn vị còn lại và giá vốn còn lại. Các lệnh bán bị tính lại (nếu có) được cập nhật kèm dấu vết. |
| Biến động số dư | Ví nhận tiền tăng $NetAmount$. Số đơn vị nắm giữ và giá vốn còn lại giảm theo phần đã bán. Tổng tài sản đầu tư $Investments$ giảm đúng giá trị thị trường của phần đã bán. Tài sản ròng $NetWorth$ thay đổi bằng chênh lệch giữa $NetAmount$ và giá trị thị trường đó, bằng $-Fee_{sell}$ khi bán đúng giá thị trường hiện hành. |
| Thông báo phản hồi | Thông báo ghi nhận lệnh bán thành công kèm số tiền thực nhận, giá vốn xuất bán và lãi lỗ đã chốt. Khi có tính lại, thông báo kèm danh sách lệnh bán bị đổi. Khi giao dịch cùng ngày với giao dịch đã ghi, thông báo nêu giao dịch được tính sau những giao dịch nào theo số thứ tự ghi. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-INV-04.1 | Tài sản "VESAF" có một lệnh mua 400.0000 đơn vị, giá gốc 10,000,000.00 VNĐ. Người dùng bán 200 đơn vị tại đơn giá 30,000.00 VNĐ, chi phí bán 10,000.00 VNĐ, về ví MB Bank. | Số tiền thực nhận 5,990,000.00 VNĐ cộng vào ví MB Bank. Giá vốn xuất bán 5,000,000.00 VNĐ, lãi đã chốt +990,000.00 VNĐ. Lệnh mua còn 200.0000 đơn vị, giá vốn còn lại 5,000,000.00 VNĐ. |
| TC-INV-04.2 | Tài sản có lệnh mua A (01/03, 100 đơn vị, giá gốc 2,000,000.00 VNĐ) và lệnh mua B (20/03, 50 đơn vị, giá gốc 1,500,000.00 VNĐ). Ngày 25/03 người dùng bán 120 đơn vị tại 28,000.00 VNĐ, không có chi phí bán. | Hệ thống khấu trừ 100 đơn vị từ A và 20 đơn vị từ B. Giá vốn xuất bán 2,600,000.00 VNĐ, lãi đã chốt +760,000.00 VNĐ. Lệnh mua B còn 30 đơn vị, giá vốn còn lại 900,000.00 VNĐ. |
| TC-INV-04.3 | Tài sản có 100 đơn vị nắm giữ. Người dùng nhập bán 150 đơn vị. | Hệ thống từ chối và báo lỗi `ERR-INV-009`, nêu số đơn vị nắm giữ là 100. |
| TC-INV-04.4 | Như TC-INV-04.2 nhưng Ngày bán là 10/03 và số đơn vị bán là 120. | Hệ thống từ chối và báo lỗi `ERR-INV-009`, nêu số đơn vị nắm giữ tại 10/03 là 100 vì lệnh mua B chưa tồn tại. |
| TC-INV-04.5 | Tài sản có lệnh mua A và B như TC-INV-04.2, chưa có lệnh bán nào ngoài lệnh bán S1 ngày 25/03, 100 đơn vị, 28,000.00 VNĐ đã ghi. Người dùng ghi thêm lệnh bán S0 ngày 15/03, 40 đơn vị, 26,000.00 VNĐ. | Hệ thống hiển thị S1 đổi từ +800,000.00 VNĐ sang +400,000.00 VNĐ và chờ xác nhận. Sau xác nhận, S0 có giá vốn 800,000.00 VNĐ, lãi +240,000.00 VNĐ. S1 khấu trừ 60 đơn vị từ A và 40 đơn vị từ B. Lệnh mua B còn 10 đơn vị, giá vốn còn lại 300,000.00 VNĐ. |
| TC-INV-04.6 | Tài sản "Đất lô 7" có một lệnh mua 1.0000 đơn vị, giá gốc 2,040,000,000.00 VNĐ. Người dùng chọn bán toàn bộ, số tiền bán nhận về 2,500,000,000.00 VNĐ, chi phí bán 25,000,000.00 VNĐ. | Số tiền thực nhận 2,475,000,000.00 VNĐ, giá vốn xuất bán 2,040,000,000.00 VNĐ, lãi đã chốt +435,000,000.00 VNĐ. Mọi lệnh mua của tài sản được tất toán. |
| TC-INV-04.7 | Tài sản "Đất lô 7" có một lệnh mua 100.0000 đơn vị, giá gốc 2,000,000,000.00 VNĐ. Người dùng bán 40.0000 đơn vị, số tiền bán nhận về 920,000,000.00 VNĐ, không có chi phí bán. | Giá vốn xuất bán 800,000,000.00 VNĐ, lãi đã chốt +120,000,000.00 VNĐ. Lệnh mua còn 60.0000 đơn vị, giá vốn còn lại 1,200,000,000.00 VNĐ. |
| TC-INV-04.8 | Người dùng nhập chi phí bán lớn hơn tổng số tiền bán. | Hệ thống từ chối và báo lỗi nghiệp vụ chi phí bán vượt số tiền bán. |
| TC-INV-04.9 | Người dùng nhập đơn giá bán là số âm. | Hệ thống từ chối và báo lỗi `ERR-INV-001`. |
| TC-INV-04.10 | Tài sản "Đất lô 7" có lệnh mua P1 (01/03, 60.0000 đơn vị) và P2 (20/03, 40.0000 đơn vị). Người dùng chọn bán toàn bộ với Ngày bán 10/03. | Số đơn vị nắm giữ tại 10/03 là 60.0000. Hệ thống bán 60.0000 đơn vị của P1, P2 giữ nguyên 40.0000 đơn vị. |
| TC-INV-04.11 | Người dùng chọn ví đã lưu trữ làm ví nhận tiền bán. | Hệ thống từ chối và báo lỗi nghiệp vụ ví không ở trạng thái hoạt động. |
| TC-INV-04.12 | Tài sản "ABC" có 1,000.0000 đơn vị, giá gốc 100,000,000.00 VNĐ. Người dùng chọn bán toàn bộ, nhập số tiền bán nhận về 0.00 VNĐ và chi phí bán 0.00 VNĐ. | Hệ thống chấp nhận. Số tiền thực nhận 0.00 VNĐ, lãi lỗ đã chốt là -100,000,000.00 VNĐ. Mọi lệnh mua của tài sản được tất toán. |
| TC-INV-04.13 | VESAF có một lệnh mua số lượng lưu 188.6792452830 đơn vị (hiển thị 188.6792). Ngày 11/10 người dùng chọn bán một phần, nhập 188.6792 đơn vị tại 27,000.0000 VNĐ. | Tiền bán nhận về 5,094,338.40 VNĐ. Lệnh mua còn 0.0000452830 đơn vị, chưa về 0. |
| TC-INV-04.14 | Như TC-INV-04.13, người dùng chọn Bán toàn bộ tại 27,000.0000 VNĐ. | Tiền bán nhận về 5,094,339.62 VNĐ tính trên số lượng đầy đủ. Lệnh mua về 0 đơn vị. |
| TC-INV-04.15 | Như TC-INV-04.13, người dùng nhập số đơn vị bán 188.67924 (5 chữ số thập phân). | Hệ thống từ chối và báo lỗi nghiệp vụ số lượng bán phải lớn hơn 0 và có tối đa 4 chữ số thập phân. |
| TC-INV-04.16 | Như TC-INV-04.13, người dùng chọn Bán một phần và nhập số đơn vị bán 0.0000. | Hệ thống từ chối và báo lỗi nghiệp vụ số lượng bán phải lớn hơn 0 và có tối đa 4 chữ số thập phân. |
| TC-INV-04.17 | Lệnh mua A (01/03, 100.0000 đơn vị) nhận thêm 10.0000 đơn vị thưởng với ngày nhận 20/04. Người dùng bán 105.0000 đơn vị với Ngày bán 15/04. | Hệ thống từ chối và báo lỗi `ERR-INV-009`, nêu số đơn vị nắm giữ tại 15/04 là 100.0000 vì đơn vị thưởng chưa nhận. |
| TC-INV-04.18 | Như TC-INV-04.17 nhưng Ngày bán là 25/04. | Hệ thống chấp nhận, số đơn vị nắm giữ tại 25/04 là 110.0000. |
| TC-INV-04.19 | Lệnh mua A 100.0000 đơn vị ngày 20/04 ghi lúc 9:00. Người dùng ghi bán 100.0000 đơn vị ngày 20/04 lúc 14:00. | Hệ thống chấp nhận, lệnh mua đứng trước lệnh bán nên số đơn vị nắm giữ là 100.0000. |
| TC-INV-04.20 | Lệnh mua A 100.0000 đơn vị ngày 01/03. Người dùng ghi bán 40.0000 đơn vị ngày 20/04 lúc 9:00, rồi lúc 14:00 ghi thêm lệnh mua C 20.0000 đơn vị ngày 20/04. | Hệ thống chấp nhận lệnh mua C. Lệnh bán 9:00 giữ nguyên lệnh mua bị khấu trừ, giá vốn xuất bán và lãi lỗ đã chốt, không tính lại và không hiển thị xác nhận. |
| TC-INV-04.21 | Lệnh mua A 100.0000 đơn vị ngày 01/03. Người dùng ghi bán 40.0000 đơn vị ngày 20/04 lúc 9:00, rồi ghi bán 30.0000 đơn vị cùng ngày 20/04 lúc 14:00. | Hệ thống chấp nhận lệnh bán thứ hai và thông báo lệnh này được tính sau lệnh bán 9:00 cùng ngày. Nhập trước xuất trước lấy tiếp từ lệnh mua A, A còn 30.0000 đơn vị. |
| TC-INV-04.22 | Tài sản "VESAF" có 400.0000 đơn vị. Người dùng chọn Bán một phần và không nhập số đơn vị bán. | Hệ thống từ chối và báo lỗi nghiệp vụ số lượng bán phải lớn hơn 0 và có tối đa 4 chữ số thập phân. |
| TC-INV-04.23 | Người dùng nhập cả đơn giá bán 30,000.00 VNĐ và số tiền bán nhận về 6,000,000.00 VNĐ. | Hệ thống từ chối và báo lỗi nghiệp vụ thiếu hoặc thừa đại lượng, không ghi lệnh bán. |
| TC-INV-04.24 | Người dùng không nhập đơn giá bán và không nhập số tiền bán nhận về. | Hệ thống từ chối và báo lỗi nghiệp vụ thiếu hoặc thừa đại lượng, không ghi lệnh bán. |
| TC-INV-04.25 | Người dùng nhập số tiền bán nhận về -1.00 VNĐ. | Hệ thống từ chối và báo lỗi nghiệp vụ số tiền bán nhận về không được âm. |
| TC-INV-04.26 | Người dùng nhập chi phí bán -1.00 VNĐ. | Hệ thống từ chối và báo lỗi nghiệp vụ chi phí bán không được âm. |
| TC-INV-04.27 | Lệnh mua A có 3.0000 đơn vị, giá vốn còn lại 1,000,000.00 VNĐ. Người dùng ghi ba lệnh bán lần lượt 1.0000 đơn vị, 1.0000 đơn vị rồi chọn Bán toàn bộ. | Giá vốn xuất bán lần lượt 333,333.33 VNĐ (còn lại 666,666.67), 333,333.34 VNĐ (666,666.67 / 2 = 333,333.335, làm tròn ngân hàng nửa chẵn, còn lại 333,333.33) và 333,333.33 VNĐ (bán hết lệnh nên bằng đúng giá vốn còn lại). Tổng giá vốn xuất bán đúng 1,000,000.00 VNĐ, giá vốn còn lại 0.00 VNĐ (QD-259). |

#### 4.7.5 FR-INV-05: Ghi nhận sự kiện của tài sản đầu tư

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-INV-05 |
| Tên | Ghi nhận sự kiện của tài sản đầu tư |
| Nhóm chức năng | Tài sản đầu tư (FR-INV) |
| Ca sử dụng liên quan | UC-INV-05, UC-REP-01 |
| Mô tả kỹ thuật | Hệ thống PHẢI ghi nhận sự kiện của tài sản đầu tư thuộc một trong ba dạng: nhận tiền mặt, thêm đơn vị cùng loại và hoàn vốn. Hệ thống PHẢI phân bổ giá trị sự kiện cho các lệnh mua theo số đơn vị nắm giữ ngay trước sự kiện và tách riêng thu nhập với hoàn vốn theo quy tắc BR-FIN-07. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Tên tài sản đầu tư | Chuỗi ký tự | Tên của tài sản đầu tư đã có lệnh mua, so khớp không phân biệt chữ hoa chữ thường. | Bắt buộc |
| Dạng sự kiện | Lựa chọn | Nhận tiền mặt, Thêm đơn vị cùng loại hoặc Hoàn vốn, áp dụng cho mọi tài sản đầu tư. | Bắt buộc |
| Ngày nhận | Ngày tháng (YYYY-MM-DD) | Ngày tiền về ví hoặc đơn vị vào tài khoản, không vượt quá ngày hiện tại. | Bắt buộc |
| Số tiền | Số tiền (VNĐ) | Số tiền thực vào ví, $Amount > 0$, 2 chữ số thập phân theo BR-FIN-01. | Bắt buộc với Nhận tiền mặt và Hoàn vốn |
| Số đơn vị nhận thêm | Số thập phân chính xác | $Units_{bonus} > 0$, 4 chữ số thập phân theo BR-FIN-07. | Bắt buộc với Thêm đơn vị cùng loại |
| Mã định danh ví | Mã định danh | Mã ví nhận tiền, ví ở trạng thái `ACTIVE`. | Bắt buộc với Nhận tiền mặt và Hoàn vốn |

Quy tắc xử lý và điều kiện:

1. Xác định tài sản và số đơn vị nắm giữ: Hệ thống PHẢI tìm tài sản đầu tư theo tên. Hệ thống tự ghi Thời điểm ghi của sự kiện và xếp thứ tự xử lý theo ngày giao dịch, rồi số thứ tự ghi (BR-FIN-06 mục 3.6 điều 8).
  - Nếu không có lệnh mua nào đứng trước sự kiện theo thứ tự xử lý (Ngày nhận trước ngày mua đầu tiên), hệ thống PHẢI từ chối cả ba dạng sự kiện và báo lỗi `ERR-INV-004` trước khi kiểm tra điều kiện kế tiếp.
  - Số đơn vị lệnh mua $i$ nắm giữ ngay trước sự kiện ($Units_{held,i}$) bằng đơn vị đã mua và đã nhận thêm, trừ đơn vị đã bán, của các giao dịch đứng trước sự kiện. Sự kiện không tính vào cơ sở phân bổ của chính nó.
  - Nếu tổng $\sum_{j} Units_{held,j} = 0$ vì tài sản đã bán hết, hệ thống PHẢI chia theo số đơn vị ngay trước lệnh bán gần nhất làm tài sản hết đơn vị và vẫn ghi nhận sự kiện.
2. Kiểm tra dữ liệu đầu vào theo thứ tự: Ngày nhận PHẢI không sau ngày hiện tại, nếu vi phạm báo lỗi `ERR-INV-016`. Hệ thống PHẢI kiểm tra $Amount > 0$ hoặc $Units_{bonus} > 0$ tùy dạng, nếu vi phạm báo lỗi `ERR-INV-006`. Với Nhận tiền mặt và Hoàn vốn, ví nhận tiền PHẢI ở trạng thái `ACTIVE`, nếu vi phạm báo lỗi `ERR-INV-015`.
  - Tài sản đã bán hết trước sự kiện vẫn được ghi nhận cả ba dạng sự kiện nếu đáp ứng quy tắc 1.
3. Dạng Nhận tiền mặt: Hệ thống PHẢI tăng số dư ví nhận tiền theo BR-FIN-02 (số hạng $InvEvent_w(t)$) và ghi nhận $Amount$ vào thu nhập của tháng chứa Ngày nhận với dòng phân loại riêng thu nhập đầu tư theo FR-REP-01. Số tiền được phân bổ cho từng lệnh mua $i$ theo BR-FIN-07:
  - $CashIncome_i = Amount \times \frac{Units_{held,i}}{\sum_{j} Units_{held,j}}$, với $Units_{held,i}$ ngay trước sự kiện hoặc ngay trước lệnh bán gần nhất làm tài sản hết đơn vị theo quy tắc 1.
  - Số tiền phân bổ làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng. Phần dư dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua được chia.
4. Dạng Thêm đơn vị cùng loại: Sự kiện không làm biến động số dư ví và không đổi giá vốn của lệnh mua. Số đơn vị nhận thêm của lệnh mua $i$:
$$Units_{bonus,i} = Units_{bonus} \times \frac{Units_{held,i}}{\sum_{j} Units_{held,j}}$$
  - Số đơn vị làm tròn 4 chữ số thập phân theo quy tắc làm tròn ngân hàng. Phần dư dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua được chia.
  - Giá vốn bình quân của lệnh mua $i$ được tính lại, với $CostBasis_{remain,i}$ là giá vốn còn lại của lệnh mua $i$ ngay trước sự kiện: $UnitPrice'_{cost,i} = \frac{CostBasis_{remain,i}}{Units_{held,i} + Units_{bonus,i}}$.
  - Lệnh mua $i$ không còn đơn vị ngay trước sự kiện ($Units_{held,i} = 0$) và không nhận đơn vị thêm ($Units_{bonus,i} = 0$) theo cơ sở phân bổ của sự kiện, kể cả khi tài sản đã hết đơn vị, KHÔNG áp dụng công thức trên: hệ thống không tính và không hiển thị giá vốn bình quân của lệnh đó, lệnh giữ 0 đơn vị và giá vốn còn lại 0, sự kiện vẫn được ghi (QD-260).
  - Khi tài sản đã hết đơn vị ngay trước sự kiện, đơn vị nhận thêm có giá vốn 0 và hệ thống KHÔNG tính lại các lệnh bán đứng trước sự kiện.
5. Dạng Hoàn vốn: Hệ thống PHẢI tăng số dư ví nhận tiền theo BR-FIN-02 (số hạng $InvEvent_w(t)$) và không ghi nhận $Amount$ vào thu nhập. Số tiền hoàn vốn phân bổ cho lệnh mua $i$ theo cùng cách phân bổ của dạng Nhận tiền mặt ($ReturnCapital_i$), làm tròn 2 chữ số thập phân. Phần dư dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua được chia.
  - Phần hoàn vốn làm giảm giá vốn còn lại của lệnh mua $i$ ngay trước sự kiện nhưng không đưa xuống dưới 0: $CostBasis'_{remain,i} = \max(CostBasis_{remain,i} - ReturnCapital_i, 0)$. Giá gốc ban đầu không đổi. Nếu $ReturnCapital_i > CostBasis_{remain,i}$ ở bất kỳ lệnh mua nào, hệ thống KHÔNG từ chối. Khi đó, hệ thống PHẢI hiển thị cảnh báo nêu giá vốn còn lại và phần vượt, chỉ lưu khi người dùng xác nhận, rồi cộng phần vượt vào lãi đã chốt của tài sản tại Ngày nhận.
  - Khi tài sản đã hết đơn vị ngay trước sự kiện, giá vốn còn lại bằng 0 nên toàn bộ khoản hoàn vốn là phần vượt, áp dụng quy tắc trên. Hệ thống PHẢI cộng khoản này vào lãi đã chốt của tài sản tại Ngày nhận, các lệnh bán đứng trước sự kiện giữ nguyên giá vốn xuất bán và lãi lỗ đã chốt.
  - Cổ tức thuộc kỳ trước ngày mua đầu tiên không có dạng thu nhập riêng, người dùng ghi nhận dưới dạng Hoàn vốn để giảm giá vốn của lệnh mua theo QD-76 và QD-242.
6. Ghi lùi ngày:
   - Nếu Ngày nhận sớm hơn một lệnh bán hoặc một sự kiện đã ghi của cùng tài sản, hệ thống PHẢI tính lại các lệnh bán đứng sau và phân bổ của các sự kiện đứng sau. Quy tắc này áp dụng cho dạng Thêm đơn vị cùng loại và Hoàn vốn. Việc tính lại tuân theo cơ chế ghi lùi ngày của FR-INV-04 (quy tắc 8): hiển thị tác động trước và sau, cảnh báo quỹ mục tiêu, người dùng xác nhận mới lưu, ghi dấu vết.
   - Lệnh bán đứng trước sự kiện không bị đổi.
   - Nếu tính lại làm một hoàn vốn đứng sau vượt giá vốn còn lại, phần vượt được cộng vào lãi đã chốt và hiển thị trong cùng bước xác nhận.
   - Dạng Nhận tiền mặt không đổi giá vốn và số đơn vị nên không tính lại lệnh bán nào.
7. Lưu sự kiện: Hệ thống PHẢI ghi nhận sự kiện độc lập, thuộc đúng một tài sản đầu tư. Bản ghi lưu dạng sự kiện, Ngày nhận, Thời điểm ghi và số thứ tự ghi do hệ thống tự ghi, số tiền hoặc số đơn vị, ví nhận tiền và giá trị phân bổ cho từng lệnh mua. Giá trị phân bổ được suy ra từ thứ tự xử lý, không lưu cố định, và được tính lại khi có giao dịch ghi lùi ngày theo quy tắc 6.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Sự kiện mới kèm định danh riêng, Thời điểm ghi và số thứ tự ghi do hệ thống tự ghi và bảng phân bổ cho từng lệnh mua. Với Thêm đơn vị cùng loại: số đơn vị và giá vốn bình quân của các lệnh mua được cập nhật. Với Hoàn vốn: giá vốn còn lại của các lệnh mua giảm, phần vượt giá vốn còn lại (hoặc toàn bộ khi đã bán hết) cộng vào lãi đã chốt của tài sản. Với Thêm đơn vị cùng loại và Hoàn vốn, khi Ngày nhận sớm hơn một lệnh bán hoặc một sự kiện đã ghi: các lệnh bán và phân bổ của các sự kiện đứng sau được tính lại kèm dấu vết. |
| Biến động số dư | Nhận tiền mặt: ví nhận tiền tăng $Amount$ và thu nhập tháng tăng $Amount$. Hoàn vốn: ví nhận tiền tăng $Amount$, thu nhập tháng không đổi, giá vốn còn lại giảm tối đa $Amount$, phần vượt giá vốn còn lại (hoặc toàn bộ khi đã bán hết) tăng lãi đã chốt. Thêm đơn vị cùng loại: số dư ví, tổng giá vốn và thu nhập tháng không đổi, số đơn vị nắm giữ tăng $Units_{bonus}$. Tổng tài sản đầu tư $Investments$ và tài sản ròng $NetWorth$ tăng $Units_{bonus} \times CurrentPrice$. Với Nhận tiền mặt và Hoàn vốn, $Investments$ không đổi và $NetWorth$ tăng $Amount$. |
| Thông báo phản hồi | Thông báo ghi nhận sự kiện thành công kèm bảng phân bổ cho từng lệnh mua. Khi có tính lại lệnh bán, thông báo kèm danh sách lệnh bán bị đổi. Khi giao dịch cùng ngày với giao dịch đã ghi, thông báo nêu giao dịch được tính sau những giao dịch nào theo số thứ tự ghi. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-INV-05.1 | Tài sản "VESAF" có lệnh mua A (01/03, 100 đơn vị, giá gốc 2,000,000.00 VNĐ) và B (20/03, 50 đơn vị, giá gốc 1,500,000.00 VNĐ). Ngày nhận 10/04, cổ tức tiền 300,000.00 VNĐ về ví MB Bank. | Phân bổ A 200,000.00 VNĐ, B 100,000.00 VNĐ. Ví MB Bank tăng 300,000.00 VNĐ, thu nhập tháng 04 tăng 300,000.00 VNĐ ở dòng thu nhập đầu tư. Giá vốn không đổi. |
| TC-INV-05.2 | Tài sản "FUND3" có ba lệnh mua X, Y, Z cùng 10 đơn vị, ngày mua tăng dần. Cổ tức tiền 100.00 VNĐ. | Phân bổ X 33.33 VNĐ, Y 33.33 VNĐ, Z 33.34 VNĐ. Phần dư 0.01 VNĐ dồn vào Z, lệnh mua có ngày mua mới nhất. |
| TC-INV-05.3 | Như TC-INV-05.1. Ngày nhận 20/04, nhận thêm 15 đơn vị thưởng. | A nhận 10 đơn vị (110 đơn vị, giá vốn bình quân 18,181.8182 VNĐ), B nhận 5 đơn vị (55 đơn vị, 27,272.7273 VNĐ). Ví và tổng giá vốn không đổi. |
| TC-INV-05.4 | Như TC-INV-05.1. Ngày nhận 10/04, hoàn vốn 600,000.00 VNĐ về ví MB Bank. | A giảm 400,000.00 VNĐ còn 1,600,000.00 VNĐ, B giảm 200,000.00 VNĐ còn 1,300,000.00 VNĐ. Giá gốc giữ nguyên. Ví tăng 600,000.00 VNĐ, thu nhập tháng không đổi. |
| TC-INV-05.5 | Tài sản có một lệnh mua giá vốn còn lại ngay trước sự kiện 2,000,000.00 VNĐ. Người dùng ghi hoàn vốn 2,500,000.00 VNĐ. | Hệ thống hiển thị cảnh báo: giá vốn còn lại 2,000,000.00 VNĐ, phần vượt 500,000.00 VNĐ. Sau khi người dùng xác nhận, giá vốn còn lại về 0, lãi đã chốt của tài sản tăng 500,000.00 VNĐ tại Ngày nhận, ví tăng 2,500,000.00 VNĐ, thu nhập tháng không đổi. |
| TC-INV-05.6 | Tài sản có một lệnh mua 100 đơn vị (01/03), đã bán hết 100 đơn vị ngày 20/03. Ngày nhận 05/04, cổ tức tiền 500,000.00 VNĐ. | Hệ thống chấp nhận, phân bổ toàn bộ 500,000.00 VNĐ cho lệnh mua đó theo đơn vị ngay trước lệnh bán. Ví tăng 500,000.00 VNĐ, thu nhập tháng 04 tăng 500,000.00 VNĐ. |
| TC-INV-05.7 | Như TC-INV-05.6 nhưng người dùng ghi nhận thêm 10 đơn vị với ngày nhận 05/04. | Hệ thống chấp nhận. Lệnh mua nhận 10 đơn vị với giá vốn 0, số đơn vị nắm giữ tăng 10, giá vốn còn lại bằng 0. |
| TC-INV-05.8 | Tài sản có lệnh mua đầu tiên ngày 01/03. Người dùng ghi nhận tiền mặt với ngày nhận 20/02. | Hệ thống từ chối và báo lỗi `ERR-INV-004`. |
| TC-INV-05.9 | Người dùng ghi sự kiện với số tiền bằng 0 hoặc số đơn vị nhận thêm bằng 0. | Hệ thống từ chối và báo lỗi `ERR-INV-006`. |
| TC-INV-05.10 | Tài sản "Đất lô 7" có hai lệnh mua P1 (80.0000 đơn vị, giá gốc 2,000,000,000.00 VNĐ) và P2 (20.0000 đơn vị, giá gốc 500,000,000.00 VNĐ). Người dùng ghi nhận tiền thuê 25,000,000.00 VNĐ. | Phân bổ theo số đơn vị nắm giữ: P1 20,000,000.00 VNĐ, P2 5,000,000.00 VNĐ. |
| TC-INV-05.11 | Tài sản "Đất lô 7" có một lệnh mua 100.0000 đơn vị, giá gốc 2,000,000,000.00 VNĐ. Người dùng chọn Thêm đơn vị cùng loại, nhận thêm 20.0000 đơn vị không trả tiền. | Lệnh mua thành 120.0000 đơn vị, giá vốn bình quân 16,666,666.6667 VNĐ, tổng giá vốn giữ nguyên 2,000,000,000.00 VNĐ. |
| TC-INV-05.12 | Tài sản có lệnh mua A (100 đơn vị, giá gốc 2,000,000.00 VNĐ), đã bán 40 đơn vị ngày 10/04 với giá vốn xuất bán 800,000.00 VNĐ. Ngày nhận 20/04, nhận thêm 10 đơn vị. | A có 60 đơn vị nắm giữ tại 20/04, nhận 10 đơn vị, thành 70 đơn vị, giá vốn còn lại 1,200,000.00 VNĐ, giá vốn bình quân 17,142.8571 VNĐ. Lệnh bán 10/04 giữ nguyên giá vốn xuất bán 800,000.00 VNĐ và lãi lỗ đã chốt. |
| TC-INV-05.13 | Tài sản có lệnh mua đầu tiên ngày 01/03. Người dùng nhận cổ tức thuộc kỳ trước, ghi dạng hoàn vốn 100,000.00 VNĐ, ngày nhận 10/03. | Hệ thống chấp nhận, phân bổ theo đơn vị nắm giữ ngay trước sự kiện (ngày 10/03), giảm giá vốn và không tính vào thu nhập tháng. |
| TC-INV-05.14 | Người dùng chọn ví đã lưu trữ làm ví nhận tiền của sự kiện nhận tiền mặt hoặc hoàn vốn. | Hệ thống từ chối và báo lỗi `ERR-INV-015`. |
| TC-INV-05.15 | Lệnh mua A (01/03, 100 đơn vị, giá gốc 2,000,000.00 VNĐ) bán hết ngày 10/04 thu về 2,800,000.00 VNĐ (lãi đã chốt +800,000.00 VNĐ). Người dùng ghi cổ tức tiền 150,000.00 VNĐ, ngày nhận 20/04. | Hệ thống chấp nhận, gắn vào tài sản, phân bổ 150,000.00 VNĐ cho A. Ví tăng 150,000.00 VNĐ, thu nhập tháng 04 tăng 150,000.00 VNĐ, tỷ suất giản đơn của lệnh mua A là 47.5% (950,000.00 chia 2,000,000.00). |
| TC-INV-05.16 | Người dùng nhập Ngày nhận là một ngày trong tương lai. | Hệ thống từ chối và báo lỗi `ERR-INV-016`. |
| TC-INV-05.17 | Lệnh mua A (100 đơn vị, giá gốc 2,000,000.00 VNĐ) đã nhận thêm 10 đơn vị ngày 20/04. Ngày nhận 30/04, nhận thêm 11 đơn vị. | Số đơn vị tại 30/04 là 110. A nhận 11 đơn vị, thành 121 đơn vị, giá vốn bình quân 16,528.9256 VNĐ. |
| TC-INV-05.18 | Lệnh mua A (100 đơn vị, giá vốn 2,000,000.00 VNĐ), đã bán hết 100 đơn vị ngày 10/04 với tiền thu về 2,800,000.00 VNĐ. Ngày nhận 20/04, hoàn vốn 300,000.00 VNĐ. | Hệ thống cảnh báo phần hoàn vốn 300,000.00 VNĐ vượt giá vốn còn lại 0.00 VNĐ và chờ xác nhận. Sau khi xác nhận: lệnh bán 10/04 giữ nguyên giá vốn xuất bán 2,000,000.00 VNĐ và lãi đã chốt +800,000.00 VNĐ. Lãi đã chốt của tài sản cộng thêm 300,000.00 VNĐ tại 20/04, tổng +1,100,000.00 VNĐ. Ví tăng 300,000.00 VNĐ, thu nhập tháng không đổi. |
| TC-INV-05.19 | Như TC-INV-05.18 nhưng người dùng chỉ bán 40 đơn vị ngày 10/04 (giá vốn xuất bán 800,000.00 VNĐ). Ngày nhận 20/04, hoàn vốn 300,000.00 VNĐ. | A còn 60 đơn vị, hoàn vốn 300,000.00 VNĐ giảm giá vốn còn lại từ 1,200,000.00 VNĐ xuống 900,000.00 VNĐ. Lệnh bán 10/04 giữ nguyên giá vốn xuất bán 800,000.00 VNĐ. |
| TC-INV-05.20 | Như TC-INV-05.6 (lệnh mua 100 đơn vị, giá vốn 2,000,000.00 VNĐ, đã bán hết ngày 20/03). Người dùng ghi hoàn vốn 300,000.00 VNĐ với ngày nhận 05/04. | Hệ thống cảnh báo phần vượt 300,000.00 VNĐ và chờ xác nhận. Sau khi xác nhận: lệnh bán 20/03 giữ nguyên giá vốn xuất bán, lãi đã chốt của tài sản cộng thêm 300,000.00 VNĐ tại 05/04. Ví tăng 300,000.00 VNĐ, thu nhập tháng không đổi. |
| TC-INV-05.21 | Lệnh mua A (100 đơn vị, giá gốc 2,000,000.00 VNĐ) bán hết ngày 10/04 thu về 2,800,000.00 VNĐ, nhận thêm 10 đơn vị thưởng ngày 20/04, sau đó bán 10 đơn vị ngày 05/05 thu về 300,000.00 VNĐ. | 10 đơn vị thưởng có giá vốn 0. Lệnh bán 05/05 có giá vốn xuất bán 0.00 VNĐ và lãi đã chốt +300,000.00 VNĐ. Tổng lãi đã chốt suốt vòng đời +1,100,000.00 VNĐ. |
| TC-INV-05.22 | Lệnh mua A (100 đơn vị, giá gốc 2,000,000.00 VNĐ), đã bán 40 đơn vị ngày 25/04 thu về 1,100,000.00 VNĐ (giá vốn xuất bán 800,000.00 VNĐ, lãi đã chốt +300,000.00 VNĐ). Người dùng ghi nhận thêm 10 đơn vị với ngày nhận 20/04. | Hệ thống hiển thị tác động và chờ xác nhận. A thành 110 đơn vị, giá vốn bình quân 18,181.8182 VNĐ. Lệnh bán 25/04 được tính lại: giá vốn xuất bán 727,272.73 VNĐ, lãi đã chốt +372,727.27 VNĐ, kèm dấu vết. |
| TC-INV-05.23 | Lệnh mua A (01/03, 100 đơn vị, giá gốc 2,000,000.00 VNĐ) và B (10/04, 50 đơn vị, giá gốc 1,500,000.00 VNĐ). Ngày 20/04 người dùng ghi thưởng 15 đơn vị lúc 9:00, rồi ghi bán 150 đơn vị thu về 4,000,000.00 VNĐ lúc 14:00. | Thưởng chia A 10 đơn vị, B 5 đơn vị (A 110, B 55). Bán 150 lấy A 110 và B 40: giá vốn xuất bán 3,090,909.09 VNĐ, lãi đã chốt +909,090.91 VNĐ. B còn 15 đơn vị, giá vốn còn lại 409,090.91 VNĐ. |
| TC-INV-05.24 | Như TC-INV-05.23 nhưng người dùng ghi bán 150 đơn vị lúc 9:00, rồi ghi thưởng 15 đơn vị lúc 14:00. | Bán 150 lấy A 100 và B 50: giá vốn xuất bán 3,500,000.00 VNĐ, lãi đã chốt +500,000.00 VNĐ, tài sản hết đơn vị. Thưởng chia theo đơn vị ngay trước lệnh bán (A 10, B 5), 15 đơn vị có giá vốn 0. |
| TC-INV-05.25 | Lệnh mua A (100 đơn vị, giá gốc 2,000,000.00 VNĐ) đã ghi hoàn vốn 1,900,000.00 VNĐ ngày 20/04. Người dùng ghi thêm hoàn vốn 300,000.00 VNĐ ngày 10/04. | Hệ thống hiển thị tác động và chờ xác nhận. Ngày 10/04 giá vốn còn lại 1,700,000.00 VNĐ. Ngày 20/04 hoàn vốn 1,900,000.00 VNĐ đưa giá vốn về 0, phần vượt 200,000.00 VNĐ cộng vào lãi đã chốt của tài sản tại 20/04. |
| TC-INV-05.26 | Tài sản có lệnh mua đầu tiên ngày 20/04 ghi lúc 9:00. Người dùng ghi cổ tức tiền 100,000.00 VNĐ ngày nhận 20/04 lúc 14:00. | Hệ thống chấp nhận, lệnh mua đứng trước sự kiện nên nhận toàn bộ 100,000.00 VNĐ. |
| TC-INV-05.27 | Lệnh mua A (01/03, 100 đơn vị) và B (20/03, 50 đơn vị). Cổ tức tiền 300,000.00 VNĐ ngày 10/04 đã chia A 200,000.00 VNĐ, B 100,000.00 VNĐ. Người dùng ghi thêm lệnh mua C ngày 05/04, 40 đơn vị. | Hệ thống tính lại phân bổ cổ tức 10/04 theo 190 đơn vị: A 157,894.74 VNĐ, B 78,947.37 VNĐ, C 63,157.89 VNĐ (tổng 300,000.00 VNĐ), hiển thị tác động, chờ xác nhận và ghi dấu vết. |
| TC-INV-05.28 | Lệnh mua A (01/03) đã bán hết 100 đơn vị ngày 10/04, nhận thêm 10 đơn vị thưởng ngày 20/04 (giá vốn 0). Lệnh mua B ngày 25/04, 50 đơn vị, giá vốn còn lại 1,500,000.00 VNĐ. Người dùng ghi hoàn vốn 600,000.00 VNĐ ngày 30/04. | Phân bổ theo đơn vị ngay trước sự kiện: A 100,000.00 VNĐ, B 500,000.00 VNĐ. Hệ thống cảnh báo phần vượt 100,000.00 VNĐ của lệnh mua A. Sau khi xác nhận: giá vốn còn lại của A là 0 và lãi đã chốt tăng 100,000.00 VNĐ, giá vốn còn lại của B giảm từ 1,500,000.00 VNĐ xuống 1,000,000.00 VNĐ. Ví tăng 600,000.00 VNĐ. |
| TC-INV-05.29 | Lệnh mua A (01/03, 100 đơn vị, giá gốc 2,000,000.00 VNĐ). Hoàn vốn 600,000.00 VNĐ ngày 10/04 chia hết cho A (giá vốn còn lại 1,400,000.00 VNĐ). Lệnh bán 50 đơn vị ngày 20/04 (giá vốn xuất bán 700,000.00 VNĐ). Người dùng ghi thêm lệnh mua B ngày 05/04, 50 đơn vị, giá gốc 1,500,000.00 VNĐ. | Hệ thống tính lại phân bổ hoàn vốn 10/04 theo 150 đơn vị: A 400,000.00 VNĐ, B 200,000.00 VNĐ, giá vốn còn lại của A là 1,600,000.00 VNĐ. Lệnh bán 20/04 được tính lại: giá vốn xuất bán 800,000.00 VNĐ (tăng 100,000.00 VNĐ), lãi đã chốt giảm 100,000.00 VNĐ, hiển thị trước và sau trong bước xác nhận kèm dấu vết. |
| TC-INV-05.30 | Lệnh mua A (01/03, 100 đơn vị) bán hết ngày 01/04. Lệnh mua B (20/03, 50 đơn vị, giá vốn 1,500,000.00 VNĐ) bán hết ngày 10/04, cả tài sản hết đơn vị. Người dùng ghi thêm 15 đơn vị thưởng ngày 20/04. | Cơ sở phân bổ lấy ngay trước lệnh bán 10/04: $Units_{held,A} = 0$, $Units_{held,B} = 50$. Lệnh mua B nhận 15 đơn vị với giá vốn 0. Lệnh mua A không tính và không hiển thị giá vốn bình quân, giữ 0 đơn vị và giá vốn còn lại 0. Sự kiện vẫn được ghi và các lệnh bán đứng trước không bị tính lại (QD-260). |
| TC-INV-05.31 | Lệnh mua A (01/03, 100 đơn vị) đã bán hết ngày 01/04. Lệnh mua B (20/03, 50 đơn vị, giá vốn còn lại 6,000,000.00 VNĐ) còn nắm giữ. Người dùng ghi 20 đơn vị thưởng ngày 15/04. | $Units_{bonus,B} = 20$, $Units_{bonus,A} = 0$. Lệnh mua B có $UnitPrice'_{cost,B} = 6{,}000{,}000.00 / 70 = 85{,}714.2857$ VNĐ. Lệnh mua A không tính và không hiển thị giá vốn bình quân (QD-260). |

#### 4.7.6 FR-INV-06: Quản lý khoản đầu tư

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-INV-06 |
| Tên | Quản lý khoản đầu tư |
| Nhóm chức năng | Tài sản đầu tư (FR-INV) |
| Ca sử dụng liên quan | UC-INV-06, UC-INV-07, UC-INV-08 |
| Mô tả kỹ thuật | Hệ thống PHẢI cho phép người dùng tự nhóm các lệnh mua thành khoản đầu tư: tạo khoản, đổi tên, gán hoặc bỏ gán lệnh mua, và xóa khoản. Khoản đầu tư chỉ là cách nhóm để xem lãi lỗ và tỷ suất theo FR-INV-03, không làm thay đổi tiền hay giá vốn. Việc lệnh mua thuộc tối đa một khoản tuân theo BR-FIN-06, tỷ suất của khoản tuân theo BR-FIN-08. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Thao tác | Lựa chọn | "Tạo", "Sửa" hoặc "Xóa". | Bắt buộc |
| Tên khoản đầu tư | Chuỗi ký tự | Độ dài từ 1 đến 50 ký tự, ví dụ "Hưu trí", "Dự phòng". Với Sửa và Xóa là tên khoản đã có. | Bắt buộc |
| Tên mới | Chuỗi ký tự | Độ dài từ 1 đến 50 ký tự. | Tùy chọn, chỉ với thao tác Sửa |
| Lệnh mua gán vào | Danh sách mã định danh | Mã lệnh mua đã có, của tài sản đầu tư bất kỳ, kể cả lệnh mua đã bán hết. | Tùy chọn, với Tạo và Sửa |
| Lệnh mua bỏ gán | Danh sách mã định danh | Mã lệnh mua đang thuộc khoản được sửa. | Tùy chọn, chỉ với thao tác Sửa |
| Xác nhận xóa | Lựa chọn Có hoặc Không | Phải là "Có" để xóa. | Bắt buộc với thao tác Xóa |

Quy tắc xử lý và điều kiện:

1. Kiểm tra tên: Hệ thống PHẢI bỏ khoảng trắng đầu cuối rồi kiểm tra độ dài từ 1 đến 50 ký tự. Tên sai độ dài bị từ chối bằng `ERR-INV-012`. Tên trùng một khoản khác, không phân biệt chữ hoa chữ thường, bị từ chối bằng `ERR-INV-013`.
2. Tạo khoản: Hệ thống PHẢI tạo khoản mới có tên hợp lệ. Khoản được phép chưa có lệnh mua nào. Người dùng có thể gán lệnh mua ngay khi tạo. Lệnh mua không bắt buộc thuộc một khoản.
3. Gán lệnh mua: Mỗi lệnh mua thuộc tối đa một khoản tại một thời điểm theo BR-FIN-06.
  - Nếu lệnh mua chưa thuộc khoản nào, hệ thống gán vào khoản này.
  - Nếu lệnh mua đang thuộc khoản khác, hệ thống PHẢI hiển thị khoản hiện tại và chỉ chuyển sang khoản này sau khi người dùng xác nhận. Lệnh mua rời khoản cũ.
  - Nếu lệnh mua đã thuộc chính khoản này, hệ thống bỏ qua và không báo lỗi.
  - Nếu mã lệnh mua không tồn tại, hệ thống từ chối bằng `ERR-INV-014`.
  - Mã trong danh sách Lệnh mua bỏ gán mà lệnh mua không thuộc khoản này bị từ chối bằng `ERR-INV-014`.
  - Mã trùng lặp trong danh sách được gộp thành một. Mã xuất hiện ở cả Lệnh mua gán vào và Lệnh mua bỏ gán bị từ chối bằng `ERR-INV-014`.
  - Thao tác gán nhiều lệnh mua được thực hiện trọn vẹn hoặc không làm gì: một mã không hợp lệ, hoặc một lần chuyển khoản bị người dùng từ chối, làm hủy toàn bộ thao tác.
4. Sửa khoản: Hệ thống PHẢI cho đổi tên, gán và bỏ gán lệnh mua trong cùng một thao tác. Tên mới tuân theo quy tắc 1, riêng tên mới chỉ khác tên cũ về chữ hoa chữ thường thì được chấp nhận. Bỏ gán lệnh mua không thuộc khoản này bị từ chối bằng `ERR-INV-014` theo quy tắc 3. Lệnh mua bỏ gán trở về trạng thái chưa gán.
5. Xóa khoản: Hệ thống PHẢI hiển thị hộp xác nhận nêu tên khoản, số lệnh mua, lãi lỗ đã chốt và chưa chốt hiện tại, và nói rõ khoản đã xóa không phục hồi được. Chỉ khi Xác nhận xóa là "Có", hệ thống mới xóa khoản. Các lệnh mua của khoản trở về trạng thái chưa gán.
6. Phạm vi tác động: Xóa khoản không xóa lệnh mua, tài sản đầu tư, lệnh bán hay sự kiện. Thao tác gán, bỏ gán, đổi tên và xóa khoản không thay đổi liên kết giữa lệnh mua và quỹ mục tiêu, vì liên kết quỹ thuộc từng lệnh mua theo FR-FND.
7. Bảo toàn: Mọi thao tác không làm thay đổi số dư ví, giá gốc, giá vốn, số đơn vị hay lãi lỗ của từng lệnh mua. Tổng các khoản cộng với lệnh mua chưa gán luôn bằng tổng toàn bộ tài sản đầu tư.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Khoản đầu tư được tạo, đổi tên hoặc xóa, kèm danh sách lệnh mua thuộc khoản sau thao tác. |
| Biến động số dư | Không làm biến động số dư ví và tổng tài sản. |
| Thông báo phản hồi | Thông báo thành công kèm tên khoản và số lệnh mua. Với thao tác xóa, nêu số lệnh mua đã trở về chưa gán. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-INV-06.1 | Người dùng tạo khoản "Hưu trí" và gán hai lệnh mua L1 (VESAF, giá gốc 10,000,000.00 VNĐ, giá trị 11,000,000.00 VNĐ) và L2 (Vàng SJC, giá gốc 4,000,000.00 VNĐ, giá trị 4,200,000.00 VNĐ). | Khoản "Hưu trí" gồm hai lệnh mua, lãi lỗ chưa chốt của khoản +1,200,000.00 VNĐ theo FR-INV-03. Số dư ví không đổi. |
| TC-INV-06.2 | Người dùng tạo khoản "Dự phòng", không gán lệnh mua nào. | Hệ thống tạo khoản rỗng, lãi lỗ bằng 0.00 VNĐ. |
| TC-INV-06.3 | Đã có khoản "Hưu trí". Người dùng tạo khoản tên " hưu trí " (chữ thường, thừa khoảng trắng). | Hệ thống từ chối bằng `ERR-INV-013`. |
| TC-INV-06.4 | Người dùng tạo khoản có tên trống hoặc dài 51 ký tự. | Hệ thống từ chối bằng `ERR-INV-012`. |
| TC-INV-06.5 | Lệnh mua L1 thuộc khoản "Hưu trí", khoản "Dự phòng" đang rỗng. Người dùng gán L1 vào "Dự phòng". | Hệ thống hiển thị khoản hiện tại "Hưu trí" và yêu cầu xác nhận. Sau khi xác nhận, L1 thuộc "Dự phòng" và rời "Hưu trí". Tổng toàn bộ tài sản đầu tư không đổi. |
| TC-INV-06.6 | Khoản "Hưu trí" gồm hai lệnh mua. Người dùng đổi tên thành "Hưu trí 2040". | Tên khoản đổi, hai lệnh mua giữ nguyên. |
| TC-INV-06.7 | Đã có khoản "Hưu trí 2040" và khoản "Dự phòng". Người dùng đổi tên "Dự phòng" thành "hưu trí 2040". | Hệ thống từ chối bằng `ERR-INV-013`, tên "Dự phòng" giữ nguyên. |
| TC-INV-06.8 | Khoản "Hưu trí" gồm L1 và L2 (lãi lỗ chưa chốt của L2 là +200,000.00 VNĐ). Người dùng bỏ gán L2. | L2 về chưa gán. Lãi lỗ chưa chốt của khoản giảm 200,000.00 VNĐ. Tổng toàn bộ tài sản đầu tư không đổi. |
| TC-INV-06.9 | L2 chưa thuộc khoản "Dự phòng". Người dùng bỏ gán L2 khỏi "Dự phòng". | Hệ thống từ chối bằng `ERR-INV-014`. |
| TC-INV-06.10 | Người dùng sửa khoản "Hưu trí" và gán vào một danh sách gồm lệnh mua L1 (chưa thuộc khoản nào) và mã LM-9999 không có trong hệ thống. | Hệ thống từ chối cả thao tác bằng `ERR-INV-014` nêu mã LM-9999. L1 vẫn chưa thuộc khoản nào, khoản "Hưu trí" không đổi. |
| TC-INV-06.11 | Một lệnh mua đã bán hết có lãi lỗ đã chốt +400,000.00 VNĐ chưa thuộc khoản nào. Người dùng gán lệnh mua này vào khoản "Hưu trí". | Lệnh mua thuộc khoản, lãi lỗ đã chốt của khoản tăng +400,000.00 VNĐ. |
| TC-INV-06.12 | L1 đã thuộc khoản "Dự phòng". Người dùng gán L1 vào "Dự phòng" lần nữa. | Hệ thống bỏ qua, không báo lỗi, không đổi dữ liệu. |
| TC-INV-06.13 | Khoản "Hưu trí 2040" có hai lệnh mua. Người dùng xóa khoản và chọn Xác nhận xóa là "Có". | Hộp xác nhận nêu tên khoản, 2 lệnh mua và việc không phục hồi được. Sau xác nhận, khoản mất, hai lệnh mua về chưa gán, lệnh bán, tài sản và số dư ví không đổi. |
| TC-INV-06.14 | Như TC-INV-06.13 nhưng người dùng chọn Xác nhận xóa là "Không". | Hệ thống không xóa, khoản giữ nguyên. |
| TC-INV-06.15 | Lệnh mua L1 thuộc khoản "Hưu trí" và đang gắn quỹ mục tiêu "Mua nhà". Người dùng xóa khoản "Hưu trí" và xác nhận. | L1 về chưa gán, vẫn gắn quỹ "Mua nhà", giá trị đóng góp vào quỹ không đổi. |
| TC-INV-06.16 | Người dùng chọn "Xóa" hoặc "Sửa" với khoản tên "Mua xe", khoản này chưa được tạo. | Hệ thống báo lỗi nghiệp vụ không tìm thấy khoản đầu tư "Mua xe", không thay đổi dữ liệu. |
| TC-INV-06.17 | Người dùng tạo khoản có tên "Quỹ hưu trí và giáo dục đại học cho con năm 2040 A" (đúng 50 ký tự). | Hệ thống chấp nhận và tạo khoản với tên này. |
| TC-INV-06.18 | Khoản "Hưu trí" đã có. Người dùng đổi tên thành "HƯU TRÍ" (chỉ khác chữ hoa chữ thường so với tên cũ của chính khoản). | Hệ thống chấp nhận và đổi tên thành "HƯU TRÍ". |
| TC-INV-06.19 | Lệnh mua L1 thuộc khoản "Hưu trí". Người dùng tạo khoản mới "Dự phòng" kèm gán L1. | Hệ thống hiển thị khoản hiện tại của L1 và yêu cầu xác nhận. Sau xác nhận, "Dự phòng" gồm L1, "Hưu trí" không còn L1. |
| TC-INV-06.20 | Người dùng tạo khoản "Tổng hợp" gồm lệnh mua VESAF (giá gốc 6,000,000.00 VNĐ, giá trị 6,300,000.00 VNĐ) và lệnh mua Vàng SJC (giá gốc 2,000,000.00 VNĐ, giá trị 2,100,000.00 VNĐ). | Hệ thống chấp nhận khoản gồm hai tài sản đầu tư. Theo FR-INV-03, lãi lỗ chưa chốt của khoản +400,000.00 VNĐ, tỷ suất trên tổng giá gốc 5.00%. |

### 4.8 FR-FND: Nhóm chức năng Quỹ mục tiêu

Nhóm chức năng Quỹ mục tiêu giúp lập kế hoạch và tổng hợp các nguồn tiền khác nhau (tiền trong ví, sổ tiết kiệm và lệnh mua của tài sản đầu tư). Nhóm chức năng này phục vụ các mục tiêu dài hạn của người dùng theo quy tắc BR-FIN-10.

#### 4.8.1 FR-FND-01: Tạo quỹ mục tiêu mới

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-FND-01 |
| Tên | Tạo quỹ mục tiêu mới |
| Nhóm chức năng | Quỹ mục tiêu (FR-FND) |
| Ca sử dụng liên quan | UC-FND-01 |
| Mô tả kỹ thuật | Hệ thống PHẢI tạo quỹ mục tiêu tài chính mới để theo dõi tiến độ đã gom được cho các kế hoạch cá nhân cụ thể theo quy tắc BR-FIN-10. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Tên quỹ | Chuỗi ký tự | Độ dài từ 1 đến 100 ký tự, tên gợi nhớ của mục tiêu tài chính, không trùng với tên quỹ khác (không phân biệt chữ hoa, chữ thường, sau khi bỏ khoảng trắng thừa ở hai đầu). | Bắt buộc |
| Mã quỹ | Mã định danh | Chuỗi ký tự viết liền không dấu, độ dài từ 1 đến 20 ký tự, duy nhất trong hệ thống. | Bắt buộc |
| Số tiền mục tiêu | Số tiền (VNĐ) | Số tiền mục tiêu cần gom, yêu cầu $targetAmount \ge 0$, 2 chữ số thập phân theo BR-FIN-01. | Bắt buộc |
| Mô tả | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra mã quỹ và tên quỹ không trùng:
   - Mã quỹ là chuỗi định danh duy nhất viết liền không dấu, độ dài từ 1 đến 20 ký tự. Hệ thống PHẢI kiểm tra không trùng lặp trong hệ thống. Nếu trùng, Hệ thống PHẢI báo lỗi nghiệp vụ mã quỹ mục tiêu đã tồn tại.
   - Hệ thống PHẢI kiểm tra tên quỹ không trùng với quỹ hiện có, không phân biệt chữ hoa, chữ thường, sau khi bỏ khoảng trắng thừa ở hai đầu. Nếu trùng, Hệ thống PHẢI từ chối và trả về mã lỗi ERR-FND-004 kèm thông báo nghiệp vụ 'Tên quỹ đã tồn tại'.
2. Kiểm tra số tiền mục tiêu: $targetAmount \ge 0$, làm tròn về 2 chữ số thập phân theo BR-FIN-01. Với quỹ mở không cố định mục tiêu, cho phép $targetAmount = 0.00$ VNĐ. Nếu $targetAmount < 0$, Hệ thống PHẢI từ chối và trả về mã lỗi ERR-FND-002.
3. Tạo bản ghi quỹ mục tiêu: Lưu bản ghi mới vào danh sách quỹ mục tiêu với trạng thái ban đầu là `IN_PROGRESS`, tỷ lệ tiến độ ban đầu là 0.00%, chưa gắn tài sản nào.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi quỹ tài chính mục tiêu mới lưu vào danh sách quỹ mục tiêu. |
| Biến động số dư | Không làm biến động số dư trong hệ thống. |
| Thông báo phản hồi | Thông báo tạo quỹ mục tiêu thành công kèm mã và tên quỹ. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-FND-01.1 | Người dùng tạo quỹ mới: tên "Mua nhà", mã "house", mục tiêu 1,000,000,000 VNĐ. | Quỹ "house" được tạo thành công với trạng thái `IN_PROGRESS`, tiến độ ban đầu 0.00%. |
| TC-FND-01.2 | Người dùng tạo quỹ mới với mã quỹ đã tồn tại trong hệ thống. | Hệ thống từ chối và báo lỗi nghiệp vụ mã quỹ mục tiêu đã tồn tại. |
| TC-FND-01.3 | Người dùng nhập số tiền mục tiêu âm (-50,000,000 VNĐ). | Hệ thống từ chối và trả về mã lỗi ERR-FND-002. |
| TC-FND-01.4 | Đã tồn tại quỹ "Mua nhà". Người dùng tạo quỹ mới với tên "mua nhà " (có khoảng trắng thừa ở cuối). | Hệ thống từ chối tạo quỹ, trả về mã lỗi ERR-FND-004 và thông báo nghiệp vụ tên quỹ đã tồn tại. |

#### 4.8.2 FR-FND-02: Gắn tài sản vào quỹ mục tiêu

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-FND-02 |
| Tên | Gắn tài sản vào quỹ mục tiêu |
| Nhóm chức năng | Quỹ mục tiêu (FR-FND) |
| Ca sử dụng liên quan | UC-FND-01 |
| Mô tả kỹ thuật | Hệ thống PHẢI liên kết hoặc gắn các nguồn tài sản thực tế (tiền mặt, sổ tiết kiệm, lệnh mua của tài sản đầu tư) vào một quỹ mục tiêu để ghi nhận mức đã gom được theo quy tắc BR-FIN-10. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã quỹ | Mã định danh | Mã quỹ mục tiêu nhận tài sản được gắn vào, bắt buộc tồn tại. | Bắt buộc |
| Loại tài sản | Mã phân loại | Thuộc tập hợp: `CASH`, `SAVING`, `INVESTMENT`. | Bắt buộc |
| Mã định danh tài sản | Mã định danh | Mã định danh của sổ tiết kiệm hoặc lệnh mua của tài sản đầu tư (khi loại là `SAVING` hoặc `INVESTMENT`). | Tùy chọn |
| Giá trị phân bổ | Số tiền (VNĐ) | Số tiền mặt gắn vào quỹ (bắt buộc khi loại là `CASH`), $allocatedAmount \ge 0$. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra quỹ mục tiêu: Kiểm tra mã quỹ tồn tại trong danh sách quỹ mục tiêu.
2. Phân loại và xử lý việc gắn tài sản vào quỹ theo BR-FIN-10:
  - Loại tiền mặt (loại `CASH`): Ghi nhận giá trị gắn vào quỹ $allocatedAmount$ vào phần tiền mặt $Cash_{fund}$. Nếu $allocatedAmount < 0$, Hệ thống PHẢI từ chối và trả về mã lỗi ERR-FND-003. Hệ thống kiểm tra tổng tiền mặt đã gắn vào toàn bộ các quỹ không được vượt quá tổng tiền hiện có trong toàn bộ các ví theo QD-124 và QD-207 ($TotalWalletBalance$):
  $$\sum_{f \in Funds} Cash_f \le TotalWalletBalance$$
  Nếu vượt quá, Hệ thống PHẢI từ chối và trả về mã lỗi ERR-FND-005, không ghi nhận kế hoạch gắn tài sản.
  - Loại sổ tiết kiệm (loại `SAVING`): Kiểm tra sổ tiết kiệm được chọn đang ở trạng thái `ACTIVE`. Gán mã quỹ vào thuộc tính liên kết của sổ. Một sổ tiết kiệm tại một thời điểm chỉ được gắn vào duy nhất 1 quỹ mục tiêu theo BR-FIN-10.
  - Loại tài sản đầu tư (loại `INVESTMENT`):
    - Gán mã quỹ vào lệnh mua của tài sản đầu tư đang nắm giữ tương ứng ($Units_{remain,i} > 0$) để phục vụ việc lập kế hoạch tài sản theo BR-FIN-10.
    - Khi lệnh mua đã gắn quỹ bị bán hết số đơn vị còn về 0 ($Units_{remain,i} = 0$), hệ thống giữ nguyên liên kết giữa lệnh mua và quỹ mục tiêu theo QD-174 và QD-195.
    - Giá trị đóng góp của lệnh mua vào quỹ bằng 0.00 VNĐ ($MarketValue_{remain,i} = 0.00$ VNĐ theo QD-102, QD-174, QD-184), không xóa liên kết khi bán hết.
    - Danh sách thành phần của quỹ ẩn mặc định các lệnh mua đã bán hết ($Units_{remain,i} = 0$). Người dùng có thể bật bộ lọc để xem, và các lệnh này hiển thị nhãn "đã bán hết".
3. Giữ nguyên tài sản: Thao tác gắn tài sản vào quỹ mục tiêu chỉ là hoạt động lập kế hoạch trên sổ theo dõi. Thao tác này không làm thay đổi quyền sở hữu hay số dư thực tế của ví tiền, sổ tiết kiệm và tài sản đầu tư theo BR-FIN-02 và BR-FIN-10.
4. Ranh giới dòng tiền và cập nhật tiến độ khi bán: Khi thực hiện lệnh bán tài sản đầu tư theo FR-INV-04 đối với lệnh mua đang gắn quỹ mục tiêu, số tiền bán thực nhận được cộng vào ví nhận tiền theo BR-FIN-02. Số tiền này không tự động chuyển vào quỹ mục tiêu. Tổng giá trị tích lũy $Total_{fund}$ và tỷ lệ tiến độ $GoalFundProgress$ của quỹ mục tiêu giảm tương ứng với giá trị xuất bán.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi liên kết tài sản lưu vào danh sách thành phần của quỹ mục tiêu. |
| Biến động số dư | Thao tác chỉ gắn trên sổ theo dõi, số dư thực tế của các ví tiền, sổ tiết kiệm và tài sản đầu tư không thay đổi. |
| Thông báo phản hồi | Thông báo gắn tài sản thành công kèm tổng giá trị đã gom được mới của quỹ. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-FND-02.1 | Cho quỹ "Dự phòng" được gán 10,000,000.00 VNĐ tiền mặt gắn vào quỹ và một sổ tiết kiệm giá trị 30,000,000.00 VNĐ. | Hệ thống hiển thị phần tiền mặt là 10,000,000.00 VNĐ, phần tiết kiệm là 30,000,000.00 VNĐ, tổng giá trị quỹ là 40,000,000.00 VNĐ. |
| TC-FND-02.2 | Cho sổ tiết kiệm S1 đang liên kết với quỹ "Dự phòng". Người dùng gán S1 sang quỹ "Mua xe". | Hệ thống tự động loại S1 khỏi "Dự phòng" và gán vào "Mua xe", tổng giá trị toàn hệ thống luôn không đổi. |
| TC-FND-02.3 | Người dùng gắn số tiền mặt lớn hơn tổng số dư khả dụng của tất cả các ví ($TotalWalletBalance$, gồm ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ theo QD-124 và QD-207). | Hệ thống từ chối và trả về mã lỗi ERR-FND-005 (vượt số tiền mặt hiện có trong các ví), quỹ giữ nguyên trạng thái cũ. |
| TC-FND-02.4 | Lệnh mua của tài sản đầu tư đang gắn quỹ "Hưu trí" bị bán hết đơn vị ($Units_{remain,i} = 0$). | Hệ thống giữ nguyên liên kết của lệnh mua với quỹ "Hưu trí", giá trị đóng góp bằng 0.00 VNĐ; ẩn mặc định trong danh sách thành phần và hiển thị nhãn "đã bán hết" khi người dùng bật bộ lọc xem. |
| TC-FND-02.5 | Người dùng gắn số tiền mặt âm (-5,000,000.00 VNĐ) vào quỹ mục tiêu. | Hệ thống từ chối và trả về mã lỗi ERR-FND-003, quỹ giữ nguyên trạng thái cũ. |

#### 4.8.3 FR-FND-03: Kiểm soát tiến độ và tổng hợp tài sản quỹ mục tiêu

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-FND-03 |
| Tên | Kiểm soát tiến độ và tổng hợp tài sản quỹ mục tiêu |
| Nhóm chức năng | Quỹ mục tiêu (FR-FND) |
| Ca sử dụng liên quan | UC-FND-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI tổng hợp giá trị hiện tại của 3 thành phần tài sản và tính tỷ lệ phần trăm hoàn thành mục tiêu, xử lý an toàn điều kiện chia cho 0 theo quy tắc BR-FIN-10. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã quỹ | Mã định danh | Mã quỹ mục tiêu cần theo dõi, hoặc để trống để tổng hợp toàn bộ danh sách quỹ. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Tổng hợp giá trị 3 thành phần tài sản thực tế của quỹ mục tiêu ($Total_{fund}$) theo BR-FIN-10:
$$Total_{fund} = Cash_{fund} + \sum_{s \in Savings_{fund}} SavingsBalance_s + \sum_{i \in FundOrders_{fund}} MarketValue_{remain,i}$$
Trong đó:
  - $SavingsBalance_s$: Số dư ghi nhận của sổ tiết kiệm ở trạng thái `ACTIVE` tại thời điểm tính toán theo BR-FIN-05.
  - $MarketValue_{remain,i}$: Giá trị thị trường hiện tại của lệnh mua $i$ theo FR-INV-03 ($MarketValue_{remain,i} = Units_{remain,i} \times CurrentPrice$). Lệnh mua đã bán hết ($Units_{remain,i} = 0$) có $MarketValue_{remain,i} = 0.00$ VNĐ theo QD-102, QD-174, QD-184.
2. Tính toán tỷ lệ tiến độ hoàn thành ($GoalFundProgress$) theo BR-FIN-10:
$$GoalFundProgress = \left(\frac{Total_{fund}}{Target}\right) \times 100\%$$
(Áp dụng khi $Target > 0.00$ VNĐ; đối với quỹ mở không xác định mục tiêu số tiền tích lũy, tiến độ hiển thị không áp dụng).
Khi $GoalFundProgress \ge 100.00\%$, hệ thống ghi nhận quỹ mục tiêu đã đạt mục tiêu tài chính (trạng thái `REACHED`).
3. Tính số tiền còn thiếu để đạt mục tiêu:
$$
Remaining = \max(0, Target - Total_{fund})
$$
4. Đánh giá trạng thái mục tiêu:
  - Nếu $GoalFundProgress \ge 100.00\%$: Đánh dấu trạng thái `REACHED`.
  - Nếu $GoalFundProgress < 100.00\%$: Đặt trạng thái `IN_PROGRESS`.
5. Độ chính xác số học: Toàn bộ phép tính dùng số thập phân chính xác với 2 chữ số thập phân, làm tròn ngân hàng theo quy tắc BR-FIN-01.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Báo cáo tiến độ quỹ gồm: Tổng tài sản tích lũy ($Total_{fund}$), Chi tiết 3 thành phần ($Cash$, $Savings$, $FundOrders$), Số tiền mục tiêu ($Target$), Tỷ lệ tiến độ ($GoalFundProgress$ %), Số tiền còn thiếu ($Remaining$), Trạng thái quỹ (`IN_PROGRESS` / `REACHED`). |
| Biến động số dư | Thao tác đọc, không làm biến động số dư. |
| Thông báo phản hồi | Dữ liệu tiến độ quỹ hiển thị dạng thanh tiến trình. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-FND-03.1 | Cho quỹ có số tiền mục tiêu Target = 100,000,000.00 VNĐ; tổng tài sản tích lũy hiện tại là 65,000,000.00 VNĐ. | Hệ thống tính ra tiến độ đạt đúng 65.00% và số tiền còn thiếu là 35,000,000.00 VNĐ, trạng thái `IN_PROGRESS`. |
| TC-FND-03.2 | Khi tổng tài sản tích lũy đạt 102,000,000.00 VNĐ (vượt mục tiêu 100,000,000.00 VNĐ). | Tiến độ hiển thị là 102.00%, số tiền còn thiếu là 0.00 VNĐ và trạng thái quỹ chuyển sang `REACHED`. |
| TC-FND-03.3 | Quỹ mục tiêu có 10,000,000.00 VNĐ tiền mặt, 1 sổ tiết kiệm 20,000,000.00 VNĐ và 1 lệnh mua tài sản đầu tư đã bán hết số đơn vị ($Units_{remain,i} = 0$). | Hệ thống xác định giá trị lệnh mua đã bán hết là 0.00 VNĐ, tổng tài sản quỹ đạt đúng 30,000,000.00 VNĐ, không phát sinh sai lệch. |

#### 4.8.4 FR-FND-04: Điều chỉnh, chuyển tài sản hoặc xóa quỹ mục tiêu

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-FND-04 |
| Tên | Điều chỉnh, chuyển tài sản hoặc xóa quỹ mục tiêu |
| Nhóm chức năng | Quỹ mục tiêu (FR-FND) |
| Ca sử dụng liên quan | UC-FND-03, UC-FND-04, UC-FND-05 |
| Mô tả kỹ thuật | Hệ thống PHẢI cho phép điều chỉnh tên quỹ, mô tả, số tiền mục tiêu, số tiền mặt gắn vào quỹ, chuyển tài sản giữa các quỹ hoặc xóa hoàn toàn quỹ mục tiêu. Khi xóa quỹ, Hệ thống PHẢI tự động giải phóng tài sản liên kết theo quy tắc BR-FIN-10. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Mã quỹ | Mã định danh | Mã quỹ mục tiêu cần điều chỉnh hoặc xóa. | Bắt buộc |
| Tên quỹ mới | Chuỗi ký tự | Độ dài từ 1 đến 100 ký tự, không trùng tên quỹ khác (không phân biệt chữ hoa, chữ thường, sau khi bỏ khoảng trắng thừa ở hai đầu). | Tùy chọn |
| Mô tả mới | Chuỗi ký tự | Độ dài tối đa 255 ký tự. | Tùy chọn |
| Mục tiêu mới | Số tiền (VNĐ) | Số tiền mục tiêu điều chỉnh, $newTarget \ge 0$, 2 chữ số thập phân. | Tùy chọn |
| Giá trị tiền mặt mới | Số tiền (VNĐ) | Số tiền mặt mới gắn vào quỹ, lấy từ tiền trong các ví, $newCashAmount \ge 0$. | Tùy chọn |
| Quỹ đích chuyển tài sản | Mã định danh | Mã quỹ mục tiêu tiếp nhận tài sản khi thực hiện chuyển tài sản giữa các quỹ. | Tùy chọn |
| Loại tài sản chuyển | Mã phân loại | Thuộc tập hợp: `CASH`, `SAVING`, `INVESTMENT` khi chuyển tài sản. | Tùy chọn |
| Giá trị hoặc mã tài sản chuyển | Hỗn hợp | Số tiền mặt chuyển ($amount \le Cash_A$) hoặc mã định danh của sổ tiết kiệm / lệnh mua chuyển sang quỹ đích. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Kiểm tra thông tin điều chỉnh: Điều kiện $newTarget \ge 0$. Nếu âm, Hệ thống PHẢI từ chối và trả về mã lỗi ERR-FND-002. Nếu có tên quỹ mới, Hệ thống PHẢI kiểm tra độ dài và không trùng tên quỹ khác (bỏ qua chính quỹ đang sửa); nếu trùng, Hệ thống PHẢI từ chối và trả về mã lỗi ERR-FND-004 kèm thông báo nghiệp vụ 'Tên quỹ đã tồn tại'. Nếu có mô tả mới, Hệ thống PHẢI kiểm tra độ dài tối đa 255 ký tự và từ chối khi vượt quá.
2. Cập nhật thông tin quỹ: Ghi nhận tên quỹ mới, mô tả mới và $newTarget$ vào danh sách quỹ mục tiêu khi người dùng điều chỉnh thông tin.
3. Cập nhật tiền mặt gắn vào quỹ: Nếu có tham số $newCashAmount$, cập nhật giá trị mới vào số tiền mặt đã gắn vào quỹ. Hệ thống kiểm tra không vượt tổng tiền mặt khả dụng trên các ví ($TotalWalletBalance$, gồm ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ theo QD-124 và QD-207). Nếu $newCashAmount < 0$, Hệ thống PHẢI từ chối và trả về mã lỗi ERR-FND-003. Nếu tổng tiền mặt gắn vào các quỹ sau điều chỉnh lớn hơn $TotalWalletBalance$, Hệ thống PHẢI từ chối và trả về mã lỗi ERR-FND-005; quỹ giữ nguyên trạng thái cũ.
4. Tính lại ngay: Hệ thống tính lại tỷ lệ tiến độ $GoalFundProgress$ và trạng thái quỹ dựa trên $newTarget$ và số tiền mặt gắn vào quỹ mới (nếu có).
5. Quy tắc chuyển tài sản giữa các quỹ:
   - Khi chuyển tiền mặt, kiểm tra số tiền chuyển không vượt số tiền mặt hiện có của quỹ nguồn ($amount \le Cash_{source}$); giảm tiền mặt quỹ nguồn và tăng tương ứng ở quỹ đích, bảo toàn tổng tiền mặt đã phân bổ.
   - Khi chuyển sổ tiết kiệm hoặc lệnh mua tài sản đầu tư, cập nhật mã quỹ đích cho tài sản đó.
   - Giữ nguyên tổng tài sản toàn hệ thống theo BR-FIN-10.
   - Thao tác điều chỉnh mục tiêu và chuyển tài sản chỉ thay đổi kế hoạch gắn tài sản trên sổ theo dõi. Thao tác này không tạo giao dịch chi tiêu hoặc làm thay đổi số dư thực tế của các ví tiền và sổ tiết kiệm liên kết.
6. Quy tắc xóa hoàn toàn quỹ mục tiêu:
  - Đối với quỹ mục tiêu chưa từng gắn bất kỳ tài sản nào, Hệ thống cho phép xóa vĩnh viễn ngay bản ghi quỹ khỏi danh sách và bộ lưu trữ.
  - Với quỹ mục tiêu đang gắn tài sản, Hệ thống bắt buộc hiển thị hộp thoại cảnh báo rủi ro xác nhận trước khi xóa. Tài sản gồm tiền mặt, sổ tiết kiệm hoặc lệnh mua của tài sản đầu tư, kể cả các lệnh mua đã bán hết có $Units_{remain,i} = 0$. Hộp thoại gồm các nội dung sau:
    - Hộp thoại liệt kê chi tiết số tiền mặt quy hoạch sẽ giải phóng, danh sách các sổ tiết kiệm và toàn bộ lệnh mua của tài sản đầu tư sẽ gỡ liên kết.
    - Hệ thống nêu rõ thao tác này không làm thay đổi số dư thực tế hay quyền sở hữu của các tài sản cơ sở.
  - Khi người dùng xác nhận xóa quỹ, Hệ thống tự động thực hiện hai bước liên tiếp:
    - Bước một, tự động giải phóng toàn bộ tài sản liên kết theo QD-123. Số tiền mặt quy hoạch trả về phần tiền mặt khả dụng chưa phân bổ trong các ví. Toàn bộ sổ tiết kiệm và toàn bộ lệnh mua của tài sản đầu tư (bao gồm cả các lệnh mua đã bán hết) được cập nhật trường mã quỹ liên kết thành rỗng.
    - Bước hai, xóa hoàn toàn bản ghi quỹ mục tiêu khỏi danh sách quỹ và bộ lưu trữ.
  - Sau khi xóa hoàn toàn, tên quỹ mục tiêu được giải phóng để người dùng có thể tạo lại khi có nhu cầu.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bản ghi quỹ mục tiêu được cập nhật với tên, mô tả, số tiền mục tiêu, số tiền mặt gắn vào quỹ và tiến độ mới, hoặc bị xóa hoàn toàn khỏi bộ lưu trữ. |
| Biến động số dư | Số dư các ví tiền mặt, sổ tiết kiệm và tài sản đầu tư được giữ nguyên. |
| Thông báo phản hồi | Thông báo điều chỉnh hoặc xóa quỹ mục tiêu thành công. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-FND-04.1 | Cho quỹ có mục tiêu cũ 50,000,000.00 VNĐ, đã tích lũy 40,000,000.00 VNĐ (đạt 80.00%). Người dùng nâng mục tiêu lên 80,000,000.00 VNĐ. | Hệ thống cập nhật tiến độ mới thành đúng 50.00%, số tiền tích lũy 40,000,000.00 VNĐ giữ nguyên. |
| TC-FND-04.2 | Người dùng yêu cầu xóa một quỹ mục tiêu vừa tạo chưa từng gắn bất kỳ tài sản nào. | Hệ thống xóa hoàn toàn bản ghi quỹ khỏi danh sách và giải phóng tên quỹ. |
| TC-FND-04.3 | Người dùng yêu cầu xóa quỹ "Mua ô tô" đang gắn 50,000,000.00 VNĐ tiền mặt và 1 sổ tiết kiệm. | Hệ thống hiển thị hộp thoại cảnh báo rủi ro; khi người dùng xác nhận, hệ thống tự động gỡ liên kết tài sản và xóa hoàn toàn bản ghi quỹ khỏi hệ thống. |
| TC-FND-04.4 | Người dùng yêu cầu xóa quỹ "Du học" đang gắn 1 lệnh mua còn đơn vị và 1 lệnh mua đã bán hết ($Units_{remain,i} = 0$). | Hệ thống hiển thị hộp thoại cảnh báo; khi xác nhận, tự động gỡ bỏ liên kết của cả hai lệnh mua khỏi quỹ và xóa hoàn toàn bản ghi quỹ khỏi hệ thống. |
| TC-FND-04.5 | Đã tồn tại quỹ "Mua nhà" và quỹ "Du học". Người dùng đổi tên quỹ "Du học" thành "Du học Úc" và cập nhật mô tả. | Hệ thống lưu tên và mô tả mới, mã quỹ, số dư và tài sản gắn vào quỹ không đổi. |
| TC-FND-04.6 | Đã tồn tại quỹ "Mua nhà". Người dùng đổi tên quỹ "Du học" thành "mua nhà " (có khoảng trắng thừa ở cuối). | Hệ thống từ chối, trả về mã lỗi ERR-FND-004 và thông báo nghiệp vụ tên quỹ đã tồn tại; tên cũ giữ nguyên. |
| TC-FND-04.7 | Ví có 100,000,000.00 VNĐ, quỹ "Mua nhà" đang gắn 30,000,000.00 VNĐ tiền mặt, quỹ "Du học" gắn 40,000,000.00 VNĐ. Người dùng đặt lại tiền mặt của quỹ "Mua nhà" thành 50,000,000.00 VNĐ. | Hệ thống lưu 50,000,000.00 VNĐ cho quỹ "Mua nhà" (tổng gắn 90,000,000.00 VNĐ không vượt tiền trong ví), số dư ví không đổi. |
| TC-FND-04.8 | Tình huống như TC-FND-04.7, người dùng đặt lại tiền mặt của quỹ "Mua nhà" thành 70,000,000.00 VNĐ (tổng gắn 110,000,000.00 VNĐ). | Hệ thống từ chối và trả về mã lỗi ERR-FND-005, quỹ "Mua nhà" giữ 30,000,000.00 VNĐ. |
| TC-FND-04.9 | Tình huống như TC-FND-04.7, người dùng đặt lại tiền mặt của quỹ "Mua nhà" thành -1,000,000.00 VNĐ. | Hệ thống từ chối và trả về mã lỗi ERR-FND-003, quỹ "Mua nhà" giữ 30,000,000.00 VNĐ. |
| TC-FND-04.10 | Tình huống như TC-FND-04.7, người dùng đặt lại tiền mặt của quỹ "Mua nhà" thành 60,000,000.00 VNĐ (tổng gắn 100,000,000.00 VNĐ đúng bằng $TotalWalletBalance$). | Hệ thống chấp nhận và lưu 60,000,000.00 VNĐ cho quỹ "Mua nhà". |
| TC-FND-04.11 | Người dùng điều chỉnh quỹ "Mua nhà" với số tiền mục tiêu mới -50,000,000.00 VNĐ. | Hệ thống từ chối và trả về mã lỗi ERR-FND-002, quỹ "Mua nhà" giữ nguyên mục tiêu cũ. |
| TC-FND-04.12 | Người dùng chuyển 10,000,000.00 VNĐ tiền mặt từ quỹ "Mua nhà" (đang có 30,000,000.00 VNĐ tiền mặt) sang quỹ "Du học". | Hệ thống cập nhật tiền mặt quỹ "Mua nhà" còn 20,000,000.00 VNĐ, quỹ "Du học" tăng thêm 10,000,000.00 VNĐ, tổng tiền mặt gắn vào các quỹ bảo toàn nguyên vẹn. |

### 4.9 FR-REP: Nhóm chức năng Thống kê và báo cáo

Nhóm chức năng Thống kê và báo cáo tự động tổng hợp dữ liệu giao dịch và tài sản để cung cấp chuỗi dòng tiền 12 tháng gần nhất cùng cơ cấu thu chi. Hệ thống lập báo cáo tài sản ròng và sao kê công nợ theo từng người vay hoặc người cho vay, tuân thủ các quy tắc BR-FIN-01 đến BR-FIN-10.

#### 4.9.1 FR-REP-01: Thống kê dòng tiền 12 tháng gần nhất

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-REP-01 |
| Tên | Thống kê dòng tiền 12 tháng gần nhất |
| Nhóm chức năng | Thống kê và báo cáo (FR-REP) |
| Ca sử dụng liên quan | UC-REP-01, UC-INV-05 |
| Mô tả kỹ thuật | Hệ thống PHẢI tổng hợp chuỗi thời gian tài chính liên tục 12 tháng tính từ một mốc thời gian chỉ định. Hệ thống phân tích tổng thu nhập, tổng chi tiêu, dòng tiền thuần, phân loại dòng thu nhập đầu tư và tỷ lệ tiết kiệm theo BR-FIN-01 và BR-FIN-07. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Tháng kết thúc | Số nguyên dương | Tháng kết thúc, yêu cầu $1 \le toMonth \le 12$, mặc định là tháng hiện tại. | Tùy chọn |
| Năm kết thúc | Số nguyên dương | Năm kết thúc, yêu cầu $toYear \ge 2000$, mặc định là năm hiện tại. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Xác định khung thời gian 12 tháng gần nhất: Xác định khoảng thời gian gồm đúng 12 tháng liên tục, kết thúc tại tháng $T = (toMonth, toYear)$ và bắt đầu từ tháng $T-11$.
2. Tổng hợp dòng tiền từng tháng theo BR-FIN-01 và BR-FIN-07:
  - Tổng thu nhập tháng $m$: $Income_m = \sum_{t \in Month_m, Type=Income} amount_t + InvestmentIncome_m$.
  - Thu nhập đầu tư tháng $m$: $InvestmentIncome_m = \sum_{e \in Receipt_m} amount_e$, với $Receipt_m$ là tập sự kiện Nhận tiền mặt có Ngày nhận thuộc tháng $m$. Khoản này là tổng số tiền nhận từ các sự kiện Nhận tiền mặt của tài sản đầu tư theo QD-76, QD-99 và QD-122. Sự kiện Hoàn vốn và Thêm đơn vị cùng loại không tính vào thu nhập.
  - Báo cáo phân tích hiển thị dòng phân loại riêng "trong đó thu nhập đầu tư" ($InvestmentIncome_m$) nằm dưới chỉ tiêu Tổng thu nhập tháng.
  - Tổng chi tiêu tháng $m$: $Expense_m = \sum_{t \in Month_m, Type=Expense} amount_t$.
  - Dòng tiền thuần tháng $m$: $NetCashFlow_m = Income_m - Expense_m$.
3. Tính tỷ lệ tiết kiệm tháng ($SavingsRate_m$ %):
$$SavingsRate_m = \begin{cases} \left(\frac{NetCashFlow_m}{Income_m}\right) \times 100, & \text{khi } Income_m > 0 \\ 0.00, & \text{khi } Income_m \le 0 \end{cases}$$
4. Tính các chỉ số thống kê trung bình 12 tháng gần nhất:
  - Thu nhập bình quân: $\overline{Income} = \frac{\sum Income_m}{12}$.
  - Chi tiêu bình quân: $\overline{Expense} = \frac{\sum Expense_m}{12}$.
  - Dòng tiền thuần bình quân: $\overline{NetCashFlow} = \frac{\sum NetCashFlow_m}{12}$.
  - Mẫu số luôn là 12, kể cả các tháng chưa có giao dịch (gồm các tháng đứng trước giao dịch đầu tiên) có giá trị 0.00. Báo cáo PHẢI hiển thị ghi chú nêu các mức bình quân tính cả các tháng chưa có giao dịch theo QD-254.
5. Xử lý số học: Tất cả phép tính dùng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng theo quy tắc BR-FIN-01.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bảng chuỗi thời gian 12 bản ghi tháng gồm tháng và năm, tổng thu nhập, phân tách thu nhập đầu tư ("trong đó thu nhập đầu tư"), tổng chi tiêu, dòng tiền thuần và tỷ lệ tiết kiệm. Bảng kèm các giá trị bình quân 12 tháng gần nhất, chia cho 12 và kèm ghi chú nêu các tháng chưa có giao dịch vẫn được tính. |
| Biến động số dư | Thao tác đọc, không làm biến động số dư. |
| Thông báo phản hồi | Bảng số liệu thống kê kèm biểu đồ trực quan hóa xu hướng dòng tiền các tháng gần nhất hiển thị trên giao diện. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-REP-01.1 | Chọn mốc kết thúc là tháng 09/2026. | Hệ thống trả về chính xác chuỗi dữ liệu 12 tháng từ tháng 10/2025 đến tháng 09/2026. |
| TC-REP-01.2 | Trong tháng 08/2026: Thu nhập 30,000,000 VNĐ, Chi tiêu 18,000,000 VNĐ. | Dòng tiền thuần tháng 8 hiển thị là +12,000,000.00 VNĐ; tỷ lệ tiết kiệm hiển thị là 40.00%. |
| TC-REP-01.3 | Trong một tháng không có thu nhập ($Income_m \le 0.00$ VNĐ) và có chi tiêu 5,000,000.00 VNĐ. | Dòng tiền thuần là -5,000,000.00 VNĐ; tỷ lệ tiết kiệm hiển thị là 0.00% mà không gây lỗi chia cho 0. |
| TC-REP-01.4 | Trong tháng 04/2026: Thu nhập tiền lương 25,000,000.00 VNĐ, cổ tức tiền mặt 5,000,000.00 VNĐ, hoàn vốn 2,000,000.00 VNĐ, chi tiêu 12,000,000.00 VNĐ. | Tổng thu nhập tháng hiển thị 30,000,000.00 VNĐ; dòng "trong đó thu nhập đầu tư" hiển thị 5,000,000.00 VNĐ; khoản hoàn vốn 2,000,000.00 VNĐ không tính vào thu nhập; dòng tiền thuần hiển thị +18,000,000.00 VNĐ. |
| TC-REP-01.5 | Trong tháng 05/2026: Thu nhập tiền lương 20,000,000.00 VNĐ, chi tiêu 8,000,000.00 VNĐ và một sự kiện Thêm đơn vị cùng loại nhận thêm 10.0000 đơn vị. | Tổng thu nhập tháng hiển thị 20,000,000.00 VNĐ; dòng "trong đó thu nhập đầu tư" hiển thị 0.00 VNĐ; sự kiện Thêm đơn vị cùng loại không tính vào thu nhập; dòng tiền thuần hiển thị +12,000,000.00 VNĐ. |
| TC-REP-01.6 | Giao dịch đầu tiên ngày 2026-08-05. Thu nhập 15,000,000.00 VNĐ mỗi tháng cho các tháng 08, 09 và 10/2026. Báo cáo kết thúc ở tháng 10/2026. | Chín tháng đầu hiển thị 0.00 VNĐ; Thu nhập bình quân là 3,750,000.00 VNĐ (45,000,000.00 chia 12); báo cáo hiển thị ghi chú nêu bình quân tính cả các tháng chưa có giao dịch. |

#### 4.9.2 FR-REP-02: Phân tích cơ cấu thu chi theo nhóm và danh mục

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-REP-02 |
| Tên | Phân tích cơ cấu thu chi theo nhóm và danh mục |
| Nhóm chức năng | Thống kê và báo cáo (FR-REP) |
| Ca sử dụng liên quan | UC-REP-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI phân tích tỷ lệ của các khoản chi tiêu hoặc thu nhập theo các nhóm và danh mục phân loại trong một khoảng thời gian xác định theo quy tắc BR-FIN-04. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Ngày bắt đầu khoảng lọc, Ngày kết thúc khoảng lọc | Ngày tháng (YYYY-MM-DD) | Khoảng thời gian phân tích, yêu cầu $fromDate \le toDate$. | Bắt buộc |
| Loại giao dịch | Mã phân loại | Thuộc tập hợp: `EXPENSE` (mặc định) hoặc `INCOME`. | Bắt buộc |

Quy tắc xử lý và điều kiện:

1. Kiểm tra khoảng thời gian: $fromDate \le toDate$. Nếu vi phạm, báo lỗi khoảng ngày không hợp lệ.
2. Gom cụm và tổng hợp theo BR-FIN-04:
  - Gom cụm theo nhóm Cấp 2 ($Group$): Tính tổng số tiền $TotalGroup_g$. Giao dịch được gom theo nhóm hiện tại của danh mục, không theo nhóm tại thời điểm ghi giao dịch.
  - Gom cụm theo danh mục chi tiết Cấp 3 ($Category$): Tính tổng số tiền $TotalCategory_c$.
3. Tính tỷ lệ phần trăm cấu thành:
$$Rate_{group} = \left(\frac{TotalGroup_g}{TotalType}\right) \times 100$$
$$Rate_{cat} = \left(\frac{TotalCategory_c}{TotalType}\right) \times 100$$
  Dùng số thập phân chính xác, làm tròn ngân hàng nửa chẵn về 2 chữ số thập phân theo quy tắc BR-FIN-01 và hiển thị dạng 2 chữ số thập phân kèm ký hiệu %.
4. Sắp xếp thứ tự: Sắp xếp các nhóm và danh mục theo thứ tự số tiền giảm dần (lớn nhất xếp trước) để nhận diện các khoản chi tiêu chiếm tỷ lệ lớn nhất.
5. Phân tích so sánh kỳ liền kề: Nếu được yêu cầu, so sánh giá trị kỳ hiện tại với kỳ trước đó có cùng độ dài:
  - Chênh lệch giá trị: $\Delta Amount = Amount_{curr} - Amount_{prev}$.
  - Tỷ lệ tăng giảm: $\%Change = \frac{\Delta Amount}{Amount_{prev}} \times 100$ khi $Amount_{prev} > 0$. Nếu $Amount_{prev} = 0$, hệ thống hiển thị nhãn "Mới" theo QD-238.
6. Hiển thị nhóm và danh mục đã xóa: Báo cáo PHẢI giữ nguyên tên gốc của nhóm hoặc danh mục đã xóa kèm nhãn (đã xóa), không gộp vào dòng Khác, để tổng báo cáo luôn khớp tổng giao dịch. Với mục đã xóa mà kỳ báo cáo không có giao dịch: kỳ ở quá khứ tính từ kỳ chứa ngày mục được tạo thì hiển thị dòng với số 0.00, kỳ trước ngày tạo hoặc kỳ ở tương lai thì ẩn dòng. Mục đang dùng giữ cách hiển thị hiện tại.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bảng phân rã 2 cấp (Group -> Category) gồm tên, số tiền, tỷ lệ, biến động so với kỳ trước. |
| Biến động số dư | Thao tác đọc, không làm biến động số dư. |
| Thông báo phản hồi | Bảng cơ cấu kèm biểu đồ phân rã tỷ lệ hiển thị trên giao diện người dùng. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-REP-02.1 | Trong tháng có tổng chi 20,000,000 VNĐ; trong đó nhóm "Nhu cầu thiết yếu" chiếm 8,000,000 VNĐ. | Hệ thống tính ra tỷ lệ nhóm "Nhu cầu thiết yếu" là chính xác 40.00%. |
| TC-REP-02.2 | Xem báo cáo cơ cấu chi tiêu trong một khoảng thời gian hoàn toàn không có giao dịch chi tiêu nào. | Hệ thống hiển thị tổng chi 0.00 VNĐ và danh sách rỗng, không xảy ra lỗi chia cho 0. |
| TC-REP-02.3 | Tháng 09/2026 có tổng chi 38,000,000 VNĐ, trong đó nhóm "Nhu cầu thiết yếu" chiếm 30,000,000 VNĐ. Sau đó nhóm này bị xóa (trạng thái đã xóa). | Xem lại báo cáo tháng 09/2026, hệ thống vẫn hiển thị "Nhu cầu thiết yếu (đã xóa)" 30,000,000 VNĐ, tỷ lệ 78.95%, tổng chi 38,000,000 VNĐ. |
| TC-REP-02.4 | Nhóm "Nhu cầu thiết yếu" tạo 01/2026, xóa 10/2026, không có giao dịch ở tháng 11/2026. Hôm nay là 12/2026. | Báo cáo tháng 11/2026 hiện dòng "Nhu cầu thiết yếu (đã xóa)" với 0.00 VNĐ. Báo cáo tháng 12/2025 (trước ngày tạo) và tháng 01/2027 (tương lai) không hiện dòng này. |

#### 4.9.3 FR-REP-03: Tổng hợp tài sản và định giá tài sản ròng

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-REP-03 |
| Tên | Tổng hợp tài sản và định giá tài sản ròng |
| Nhóm chức năng | Thống kê và báo cáo (FR-REP) |
| Ca sử dụng liên quan | UC-REP-03 |
| Mô tả kỹ thuật | Hệ thống PHẢI tổng hợp toàn bộ các nguồn tài sản sở hữu và các khoản nợ phải trả tại một thời điểm để tính toán Bảng cân đối tài chính cá nhân và Tài sản ròng theo quy tắc BR-FIN-01 đến BR-FIN-06 và BR-FIN-09. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Ngày chốt số liệu | Ngày tháng (YYYY-MM-DD) | Ngày chốt số liệu báo cáo, mặc định là ngày hiện tại. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Tổng hợp các thành phần Tài sản ($TotalAssets$) tại thời điểm $asOfDate$:
  - Tiền trong các ví: $TotalWalletBalance = \sum_{w \in ActiveWallets} WalletBalance_w + \sum_{w \in ArchivedWallets, WalletBalance_w > 0} WalletBalance_w$.
  - Tiền gửi tiết kiệm ngân hàng: $Savings = \sum_{s \in ActiveSavings} SavingsBalance_s$ theo BR-FIN-05.
  - Tài sản đầu tư: $Investments = \sum_{i \in HeldOrders} MarketValue_{remain,i}$, với $HeldOrders$ là tập lệnh mua còn đơn vị của mọi tài sản đầu tư và $MarketValue_{remain,i}$ là giá trị thị trường của lệnh mua $i$ theo BR-FIN-06.
  - Nợ phải thu: $Receivables = \sum_{person} Receivable_{person}$ theo BR-FIN-09.
  - Tổng tài sản: $TotalAssets = TotalWalletBalance + Savings + Investments + Receivables$.
2. Tổng hợp các thành phần Nợ phải trả ($TotalLiabilities$):
  - Nợ phải trả: $Payables = \sum_{person} Payable_{person}$ theo BR-FIN-09.
  - Tổng nợ phải trả: $TotalLiabilities = Payables$.
3. Tính tài sản ròng ($NetWorth$) theo BR-FIN-02, BR-FIN-06, BR-FIN-09:
$$NetWorth = TotalAssets - TotalLiabilities$$
4. Đối chiếu cân đối tài chính: Kiểm tra phương trình tài sản ròng $NetWorth = TotalAssets - TotalLiabilities$ với sai số bằng 0.00 VNĐ theo BR-FIN-01.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bảng cân đối gồm 4 thành phần Tài sản, 1 thành phần Nợ phải trả, $TotalAssets$, $TotalLiabilities$, và $NetWorth$. |
| Biến động số dư | Thao tác đọc, không làm biến động số dư. |
| Thông báo phản hồi | Báo cáo tài chính cá nhân tổng quan hiển thị trên giao diện người dùng. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-REP-03.1 | Hệ thống ghi nhận: Tiền trong các ví 50,000,000.00 VNĐ, Tiết kiệm 200,000,000.00 VNĐ, Tài sản đầu tư 100,000,000.00 VNĐ, Cho vay 20,000,000.00 VNĐ; Vay nợ 40,000,000.00 VNĐ. | Báo cáo hiển thị: Tổng tài sản = 370,000,000.00 VNĐ; Tổng nợ phải trả = 40,000,000.00 VNĐ; Tài sản ròng $NetWorth$ = 330,000,000.00 VNĐ. |
| TC-REP-03.2 | Người dùng chỉ có nợ phải trả mà không có tài sản (Nợ vay 50,000,000.00 VNĐ, tiền mặt 0.00 VNĐ). | Báo cáo hiển thị $NetWorth$ mang giá trị âm là -50,000,000.00 VNĐ một cách chính xác. |

#### 4.9.4 FR-REP-04: Lập sao kê công nợ chi tiết theo người vay và người cho vay

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-REP-04 |
| Tên | Lập sao kê công nợ chi tiết theo người vay và người cho vay |
| Nhóm chức năng | Thống kê và báo cáo (FR-REP) |
| Ca sử dụng liên quan | UC-REP-04 |
| Mô tả kỹ thuật | Hệ thống PHẢI lấy toàn bộ lịch sử các khoản cho vay, đi vay, thu nợ, trả nợ và xóa nợ liên quan tới một người vay hoặc người cho vay cụ thể, tính số nợ ròng giữa hai bên theo quy tắc BR-FIN-09. Khi không chọn đối tác, Hệ thống PHẢI tổng hợp công nợ toàn hệ thống theo cùng quy tắc. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Đối tác liên quan | Mã định danh | Tên người vay hoặc người cho vay cần lập sao kê công nợ. Để trống để xem tổng hợp toàn hệ thống. | Tùy chọn |
| Bộ lọc trạng thái | Mã phân loại | Thuộc tập hợp: `ALL` (mặc định), `ACTIVE`, `PAID`. Áp dụng cho cả khi chọn đối tác và khi không chọn đối tác. | Tùy chọn |

Quy tắc xử lý và điều kiện:

1. Lọc giao dịch theo người vay hoặc người cho vay: Truy xuất toàn bộ lịch sử các giao dịch công nợ liên quan tới cá nhân được chọn trong sổ theo dõi công nợ; khi không chọn đối tác thì lấy toàn bộ sổ theo dõi công nợ.
2. Tính toán các chỉ số tổng hợp theo BR-FIN-09:
  - Tổng số tiền đã cho vay: $TotalLend = \sum Lend$.
  - Tổng số tiền đã thu nợ: $TotalCollected = \sum Collect$.
  - Tổng số nợ cho vay đã xóa: $TotalWriteOffLend = \sum \text{WriteOffLend}$.
  - Số tiền còn phải thu: $Receivable_{person} = TotalLend - TotalCollected - TotalWriteOffLend$.
  - Tổng số tiền đã đi vay: $TotalBorrow = \sum Borrow$.
  - Tổng số tiền đã trả nợ: $TotalRepaid = \sum Repay$.
  - Tổng nợ đi vay được miễn trừ: $TotalWriteOffBorrow = \sum \text{WriteOffBorrow}$.
  - Số tiền còn phải trả: $Payable_{person} = TotalBorrow - TotalRepaid - TotalWriteOffBorrow$.
3. Xác định công nợ ròng với người vay hoặc người cho vay đó:
$$NetDebtPosition = Receivable_{person} - Payable_{person}$$
  - Nếu $NetDebtPosition > 0$: Người dùng đang là người cho vay ròng (được quyền đòi $NetDebtPosition$).
  - Nếu $NetDebtPosition < 0$: Người dùng đang là người vay ròng (phải trả $|NetDebtPosition|$).
  - Nếu $NetDebtPosition = 0$: Công nợ đã được tất toán hoặc bù trừ hoàn toàn.
4. Tổng hợp toàn hệ thống khi không chọn đối tác theo BR-FIN-09:
  - Tổng nợ phải thu: $Receivables = \sum_{person} Receivable_{person}$.
  - Tổng nợ phải trả: $Payables = \sum_{person} Payable_{person}$.
  - Công nợ ròng toàn hệ thống: $NetDebtPosition = Receivables - Payables$, diễn giải dấu như quy tắc 3.
5. Sắp xếp lịch sử: Sắp xếp các khoản ghi theo ngày xảy ra tăng dần để tái hiện sổ theo dõi công nợ chi tiết.
6. Áp dụng bộ lọc trạng thái cho cả hai chế độ: nợ phải thu hoặc nợ phải trả của một đối tác ở trạng thái `ACTIVE` khi số tiền còn nợ lớn hơn 0.00 VNĐ, ở trạng thái `PAID` khi số tiền còn nợ bằng 0.00 VNĐ sau khi đã phát sinh giao dịch. Chiều công nợ chưa từng phát sinh bất kỳ giao dịch nào không cấu thành khoản nợ và không xuất hiện ở bất kỳ bộ lọc nào.
  - Mỗi khoản là một chiều (nợ phải thu hoặc nợ phải trả) của một đối tác. Lịch sử hiển thị gồm các giao dịch của chiều được giữ lại: `LEND`, `COLLECT`, `WRITE_OFF_LEND` cho nợ phải thu; `BORROW`, `REPAY`, `WRITE_OFF_BORROW` cho nợ phải trả. Chiều bị loại có số tiền còn nợ tính bằng 0.00 VNĐ trong các tổng.
  - `ALL`: tính cả hai trạng thái.
  - `ACTIVE`: chỉ tính các khoản `ACTIVE`.
  - `PAID`: chỉ tính các khoản `PAID`; số tiền còn nợ của các khoản này bằng 0.00 VNĐ nên $Receivables$, $Payables$ và $NetDebtPosition$ bằng 0.00 VNĐ.
  - Khi không có khoản nào thỏa bộ lọc ở một đối tác được chọn hoặc trên toàn hệ thống, Hệ thống hiển thị sao kê rỗng kèm thông báo không có dữ liệu công nợ thỏa bộ lọc.
  - Ở chế độ toàn hệ thống, các tổng tính trên các khoản được giữ lại; các tổng này khớp với báo cáo tài sản ròng theo FR-REP-03 khi bộ lọc là `ALL` hoặc `ACTIVE`.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Bảng sao kê của người vay hoặc người cho vay gồm danh sách giao dịch chi tiết, $Receivable_{person}$, $Payable_{person}$, và $NetDebtPosition$; khi tổng hợp toàn hệ thống gồm $Receivables$, $Payables$ và $NetDebtPosition$. |
| Biến động số dư | Thao tác đọc, không làm biến động số dư. |
| Thông báo phản hồi | Bảng sao kê công nợ của người vay hoặc người cho vay hiển thị trên giao diện người dùng. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-REP-04.1 | Cho bên A: Cho vay 10,000,000.00 VNĐ, đã thu 4,000,000.00 VNĐ; Đi vay 2,000,000.00 VNĐ, đã trả 0.00 VNĐ. | Báo cáo hiển thị: Còn phải thu = 6,000,000.00 VNĐ; Còn phải trả = 2,000,000.00 VNĐ; Nợ ròng = +4,000,000.00 VNĐ (Người cho vay ròng). |
| TC-REP-04.2 | Tra cứu sao kê của một bên không có bất kỳ giao dịch công nợ nào trong hệ thống. | Hệ thống thông báo không có dữ liệu giao dịch công nợ với bên này. |
| TC-REP-04.3 | Không chọn đối tác. Cho bên A: Cho vay 5,000,000.00 VNĐ, đã thu 2,000,000.00 VNĐ; bên B: Đi vay 1,000,000.00 VNĐ, chưa trả. | Báo cáo hiển thị: $Receivables$ = 3,000,000.00 VNĐ; $Payables$ = 1,000,000.00 VNĐ; $NetDebtPosition$ = +2,000,000.00 VNĐ (Người cho vay ròng). |
| TC-REP-04.4 | Không chọn đối tác, bộ lọc `PAID`. Bên A còn nợ phải thu 3,000,000.00 VNĐ; bên B đã vay 2,000,000.00 VNĐ và đã trả hết 2,000,000.00 VNĐ (số tiền còn nợ 0.00 VNĐ); bên C còn nợ phải trả 1,000,000.00 VNĐ. | Báo cáo hiển thị: $Receivables$ = 0.00 VNĐ; $Payables$ = 0.00 VNĐ; $NetDebtPosition$ = 0.00 VNĐ; lịch sử chỉ gồm các giao dịch của bên B. |
| TC-REP-04.5 | Cùng dữ liệu TC-REP-04.4, không chọn đối tác, bộ lọc `ACTIVE`. | Báo cáo hiển thị: $Receivables$ = 3,000,000.00 VNĐ; $Payables$ = 1,000,000.00 VNĐ; $NetDebtPosition$ = +2,000,000.00 VNĐ, khớp báo cáo tài sản ròng. |
| TC-REP-04.6 | Chọn bên B (đã trả hết), bộ lọc `ACTIVE`. | Hệ thống thông báo không có dữ liệu công nợ thỏa bộ lọc với bên này. |
| TC-REP-04.7 | Chọn bên X, bộ lọc `ACTIVE`. Bên X có nợ phải thu 3,000,000.00 VNĐ (chưa thu) và nợ phải trả đã trả hết (0.00 VNĐ). | Báo cáo hiển thị: $Receivable_{person}$ = 3,000,000.00 VNĐ; $Payable_{person}$ = 0.00 VNĐ; $NetDebtPosition$ = +3,000,000.00 VNĐ; lịch sử chỉ gồm các giao dịch cho vay và thu nợ của bên X. |
| TC-REP-04.8 | Chọn bên B (đã trả hết nợ ở TC-REP-04.4), bộ lọc `PAID`. | Báo cáo hiển thị: $Receivable_{person}$ = 0.00 VNĐ; $Payable_{person}$ = 0.00 VNĐ; $NetDebtPosition$ = 0.00 VNĐ; lịch sử hiển thị đầy đủ giao dịch đi vay và trả nợ của bên B. |

### 4.10 FR-DAT: Nhóm chức năng Lưu trữ và sao lưu dữ liệu

Nhóm chức năng Lưu trữ và sao lưu dữ liệu bảo đảm tệp dữ liệu hệ thống chạy độc lập, không bị mất dữ liệu và phục hồi an toàn trên môi trường máy tính cục bộ.

#### 4.10.1 FR-DAT-01: Xuất dữ liệu tài chính

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-DAT-01 |
| Tên | Xuất dữ liệu tài chính |
| Nhóm chức năng | Lưu trữ và sao lưu dữ liệu (FR-DAT) |
| Ca sử dụng liên quan | UC-DAT-01 |
| Mô tả kỹ thuật | Hệ thống PHẢI xuất toàn bộ cơ sở dữ liệu hoặc lấy sổ nhật ký giao dịch ra tệp định dạng JSON hoặc CSV. Tệp phục vụ mục đích lưu trữ ngoại vi, sao lưu định kỳ hoặc xử lý trên các phần mềm bảng tính theo quy tắc BR-FIN-01. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Phạm vi xuất dữ liệu | Mã phân loại | Thuộc tập hợp: `FULL` (Toàn bộ hệ thống), `TRANSACTIONS` (Sổ nhật ký thu chi), `DEBTS` (Sổ công nợ), `SAVINGS` (Sổ tiết kiệm), `INVESTMENTS` (Tài sản đầu tư). | Bắt buộc |
| Định dạng xuất dữ liệu | Mã phân loại | Thuộc tập hợp: `JSON` (sao lưu toàn bộ hệ thống hoặc theo phạm vi) hoặc `CSV` (chỉ áp dụng cho phạm vi `TRANSACTIONS`). | Bắt buộc |
| Đường dẫn tệp đích | Chuỗi ký tự | Đường dẫn thư mục hoặc tệp đích trên máy tính cục bộ của người dùng. | Bắt buộc |

Quy tắc xử lý và điều kiện:

1. Ảnh chụp dữ liệu tại một thời điểm: Thực hiện đọc dữ liệu tại một mốc thời gian nhất quán. Mốc thời gian này bảo đảm toàn bộ các giao dịch, số dư ví, danh mục, công nợ, sổ tiết kiệm, tài sản đầu tư và quỹ mục tiêu phản ánh một trạng thái thống nhất.
2. Xuất định dạng JSON:
  - Với phạm vi `FULL`, cấu trúc tệp dữ liệu sao lưu bao gồm toàn bộ các nhóm dữ liệu của hệ thống. Các nhóm này gồm ví tiền, danh mục phân loại, giao dịch thu chi, chuyển tiền nội bộ và công nợ. Các nhóm này cũng gồm sổ tiết kiệm, tài sản đầu tư (cùng lệnh mua, lệnh bán, sự kiện và khoản đầu tư), lịch sử tài sản ròng, quỹ mục tiêu và các bản ghi điều chỉnh. Danh mục phân loại gồm cả nhóm và danh mục đã xóa, kèm ngày tạo của từng mục.
  - Các trường số tiền và số đơn vị tài sản đầu tư được lưu dưới dạng chuỗi số học chuẩn theo BR-FIN-01, giữ nguyên chữ số thập phân. Các trường này dùng số thập phân chính xác với độ chính xác cố định theo quy định CON-02, không dùng số thực dấu chấm động nhị phân.
3. Xuất định dạng CSV:
  - Định dạng CSV chỉ áp dụng cho phạm vi `TRANSACTIONS`.
  - Xuất danh sách các giao dịch thu chi dạng bảng mã hóa UTF-8 không BOM, các cột phân tách bằng dấu phẩy `,` hoặc chấm phẩy `;`.
  - Định dạng ngày là `YYYY-MM-DD`.
4. Ghi tệp an toàn và đặt tên tệp:
  - Tên tệp xuất tự động theo cấu trúc `pfms_export_{scope}_{yyyyMMdd_HHmmss}.{ext}` với bảng mã UTF-8 không BOM.
  - Tệp xuất được ghi theo cách không để lại tệp hỏng nếu quá trình ghi bị ngắt quãng.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Tệp sao lưu JSON hoặc tệp dữ liệu CSV lưu trữ tại đường dẫn chỉ định trên đĩa cứng cục bộ. |
| Biến động số dư | Thao tác đọc, không làm biến động số dư. |
| Thông báo phản hồi | Thông báo xuất dữ liệu thành công kèm đường dẫn tệp và dung lượng tệp đã ghi. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-DAT-01.1 | Người dùng yêu cầu xuất bản sao lưu đầy đủ sang định dạng JSON. | Hệ thống tạo ra một tệp JSON hợp lệ chứa đầy đủ 10 nhóm dữ liệu của hệ thống, mã hóa UTF-8. Các nhóm gồm ví tiền, danh mục phân loại, giao dịch thu chi, chuyển tiền nội bộ và công nợ. Các nhóm còn lại gồm sổ tiết kiệm, tài sản đầu tư (cùng lệnh mua, lệnh bán, sự kiện và khoản đầu tư), lịch sử tài sản ròng, quỹ mục tiêu và bản ghi điều chỉnh. |
| TC-DAT-01.2 | Kiểm tra nội dung tệp JSON đã xuất. | Toàn bộ các trường số tiền đều là chuỗi số học đầy đủ chữ số thập phân, không bị làm tròn hoặc chuyển sang ký hiệu khoa học. |

#### 4.10.2 FR-DAT-02: Phục hồi dữ liệu hệ thống từ bản sao lưu

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-DAT-02 |
| Tên | Phục hồi dữ liệu hệ thống từ bản sao lưu |
| Nhóm chức năng | Lưu trữ và sao lưu dữ liệu (FR-DAT) |
| Ca sử dụng liên quan | UC-DAT-02 |
| Mô tả kỹ thuật | Hệ thống PHẢI nhập và phục hồi dữ liệu từ tệp sao lưu, thực hiện quy trình kiểm tra 4 bước và hoàn nguyên toàn bộ quá trình nhập khi phát hiện lỗi, trước khi ghi vào hệ thống. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| Đường dẫn tệp nguồn | Chuỗi ký tự | Đường dẫn tệp sao lưu trên máy tính cục bộ. | Bắt buộc |
| Chế độ nạp dữ liệu | Mã phân loại | Chế độ nhập dữ liệu: `OVERWRITE` để thay thế toàn bộ dữ liệu hiện tại, hoặc `MERGE` để hòa trộn bổ sung. | Bắt buộc |

Quy tắc xử lý và điều kiện:

1. Kiểm tra cấu trúc tệp: Đọc tệp và phân tích cú pháp tệp JSON hoặc CSV. Nếu tệp hỏng, sai cấu trúc hoặc không mã hóa UTF-8, Hệ thống PHẢI từ chối nhập và báo lỗi nghiệp vụ định dạng dữ liệu không hợp lệ.
2. Quy trình kiểm tra 4 bước:
  - Bước 1 (Kiểm tra định dạng): Kiểm tra các trường bắt buộc, định dạng ngày tháng (YYYY-MM-DD), chuỗi số học hợp lệ.
  - Bước 2 (Toàn vẹn danh mục theo `BR-FIN-04`): Mọi mã danh mục trong các giao dịch phải trỏ tới một danh mục hợp lệ (đang dùng hoặc đã xóa) có Loại thu chi tương thích.
  - Bước 3 (Toàn vẹn ví tiền): Mọi giao dịch, chuyển tiền, công nợ, tiết kiệm, lệnh mua của tài sản đầu tư phải trỏ tới ví tồn tại trong danh sách ví.
  - Bước 4 (Kiểm tra số dư luôn không âm theo `BR-FIN-02`): Bảo đảm sau khi phục hồi dữ liệu, không có ví thông thường nào bị rơi vào trạng thái số dư âm.
3. Hoàn nguyên khi có lỗi: Nếu phát hiện bất kỳ lỗi vi phạm nào trong 4 bước kiểm tra, Hệ thống PHẢI hoàn nguyên toàn bộ quá trình nhập. Khi đó, Hệ thống PHẢI giữ nguyên 100% trạng thái dữ liệu hiện có và trả về danh sách chi tiết các lỗi dữ liệu.
4. Áp dụng dữ liệu: Khi dữ liệu hợp lệ, ghi dữ liệu vào hệ thống và tính lại toàn bộ số dư từ lịch sử giao dịch, quỹ mục tiêu và các bảng thống kê 12 tháng gần nhất.

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Toàn bộ dữ liệu hệ thống được phục hồi hoặc hợp nhất thành công. |
| Biến động số dư | Số dư ví, công nợ, tiết kiệm, tài sản đầu tư được đặt lại theo đúng dữ liệu tệp sao lưu. |
| Thông báo phản hồi | Báo cáo kết quả nhập gồm: Tổng số bản ghi xử lý, số bản ghi thêm mới, số bản ghi cập nhật và thông báo hoàn tất phục hồi. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-DAT-02.1 | Cho tệp JSON sao lưu hợp lệ của toàn bộ hệ thống; người dùng thực hiện nhập với chế độ `OVERWRITE`. | Toàn bộ dữ liệu giao dịch và số dư ví được khôi phục nguyên vẹn khớp với thời điểm sao lưu. |
| TC-DAT-02.2 | Cho tệp nhập chứa một giao dịch trỏ tới danh mục không tồn tại. | Hệ thống từ chối nhập, dữ liệu hiện tại được giữ nguyên 100% và hiển thị cảnh báo lỗi liên kết sai tới danh mục. |
| TC-DAT-02.3 | Cho tệp nhập có giao dịch làm một ví tiền mặt thông thường bị số dư âm. | Hệ thống từ chối nhập dữ liệu và cảnh báo vi phạm quy tắc số dư luôn không âm BR-FIN-02. |

#### 4.10.3 FR-DAT-03: Kiểm tra lại dữ liệu hệ thống

Bảng thông tin yêu cầu:

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | FR-DAT-03 |
| Tên | Kiểm tra lại dữ liệu hệ thống |
| Nhóm chức năng | Lưu trữ và sao lưu dữ liệu (FR-DAT) |
| Ca sử dụng liên quan | UC-DAT-03 |
| Mô tả kỹ thuật | Hệ thống PHẢI cung cấp công cụ tự động quét, kiểm tra lại và so khớp toàn bộ 4 quy tắc tài chính luôn đúng của hệ thống. Công cụ này phát hiện sớm các sai lệch số dư, khoản ghi mất cân bằng hoặc mâu thuẫn dữ liệu theo quy tắc BR-FIN-01 đến BR-FIN-06 và BR-FIN-09. |

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Giá trị cho phép | Bắt buộc / Tùy chọn |
| --- | --- | --- | --- |
| triggerSource | Mã phân loại | Nguồn gốc khởi chạy việc kiểm tra lại, thuộc tập hợp: MANUAL (thủ công từ người dùng), SCHEDULED (tự động theo lịch định kỳ). | Bắt buộc |

Quy tắc xử lý và điều kiện:

1. Quét so khớp 4 quy tắc luôn đúng của hệ thống:
  - Quy tắc 1 (Số dư ví theo BR-FIN-02): Tính toán lại số dư độc lập từ toàn bộ lịch sử biến động dòng tiền và so sánh với số dư ghi nhận của từng ví. Sai số cho phép bằng đúng 0 ($\Delta = 0.00$ VNĐ).
  - Quy tắc 2 (Ghi hai vế khi chuyển khoản theo BR-FIN-03): Quét toàn bộ các giao dịch chuyển tiền nội bộ, kiểm tra số tiền trừ tại ví nguồn phải bằng chính xác số tiền cộng tại ví đích.
  - Quy tắc 3 (Cây danh mục theo BR-FIN-04): Quét toàn bộ sổ nhật ký giao dịch. Việc quét bảo đảm không có giao dịch không còn gắn với danh mục nào (trỏ tới danh mục không có trong dữ liệu, mục đã xóa vẫn tính là có trong dữ liệu). Việc quét cũng bảo đảm không có giao dịch mâu thuẫn về nhánh Loại thu chi (thu nhập gán vào chi tiêu).
  - Quy tắc 4 (Công nợ không âm theo BR-FIN-09): Kiểm tra số tiền còn phải thu và còn phải trả của từng cá nhân thỏa mãn $Receivable_{person} \ge 0$ và $Payable_{person} \ge 0$, toàn hệ thống thỏa mãn $Receivables \ge 0$ và $Payables \ge 0$.
2. Phân loại mức độ nghiêm trọng: Phân loại kết quả kiểm tra thành 3 mức: `INFO`, `WARNING`, `ERROR`.
3. Đề xuất phương án khắc phục: Đi kèm mỗi phát hiện bất thường là đề xuất hành động cụ thể (như tính toán lại số dư, khôi phục từ tệp sao lưu).

Dữ liệu đầu ra:

| Thành phần | Đặc tả kỹ thuật |
| --- | --- |
| Bản ghi dữ liệu | Báo cáo kiểm tra lại dữ liệu hiển thị trạng thái của từng quy tắc kiểm tra với kết quả PASS hoặc FAIL. |
| Biến động số dư | Thao tác đọc, không làm biến động số dư. |
| Thông báo phản hồi | Danh sách chi tiết các sai lệch phát hiện được kèm hướng dẫn khắc phục cụ thể. |

Tiêu chí nghiệm thu:

| Mã | Điều kiện kích hoạt | Kết quả kỳ vọng |
| --- | --- | --- |
| TC-DAT-03.1 | Khi dữ liệu hệ thống hoàn toàn nhất quán. | Công cụ kiểm tra lại trả về kết quả 100% `PASS` cho toàn bộ 4 quy tắc kiểm tra số dư, ghi hai vế, danh mục và công nợ. |
| TC-DAT-03.2 | Khi có một giao dịch ngoài luồng làm sai lệch số dư ví. | Công cụ kiểm tra lại phát hiện chính xác ví bị lệch, chỉ rõ số tiền chênh lệch cụ thể và hiển thị trạng thái `FAIL` kèm khuyến nghị phục hồi dữ liệu. |

