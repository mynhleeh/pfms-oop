## 4. YÊU CẦU CHỨC NĂNG CHI TIẾT

20 yêu cầu chức năng của Hệ thống Quản lý Tài chính Cá nhân chuyển hóa 3 mục tiêu hệ thống tại Mục 2.2 (`G-01`, `G-02`, `G-03`) và tuân thủ các quy tắc nghiệp vụ tại Mục 3 (`BR-FIN-01` đến `BR-FIN-04` và `BR-NAM-01`). Các yêu cầu chức năng này xác định toàn bộ năng lực phần mềm cần cung cấp theo tiêu chuẩn ISO/IEC/IEEE 29148:2018 và Karl Wiegers. Toàn bộ đặc tả tập trung vào khía cạnh bài toán nghiệp vụ (WHAT), hoàn toàn độc lập với công nghệ lưu trữ hay ngôn ngữ lập trình cụ thể (HOW), làm cơ sở cho phân tích thiết kế hướng đối tượng và cài đặt mã nguồn.

Mỗi yêu cầu chức năng được đặc tả nhất quán theo khuôn mẫu 6 khối thông tin chuẩn:

1. Bảng thuộc tính yêu cầu: Xác định mục tiêu hệ thống phục vụ, ca sử dụng hiện thực hóa tương ứng tại Mục 6 theo cú pháp `UC-<DOMAIN>-NN`, quy tắc nghiệp vụ chi phối tại Mục 3, mức độ ưu tiên theo chuẩn RFC 2119 và mô tả khái quát chức năng. Mức độ ưu tiên tuân thủ quy ước tại Mục 1.6 gồm ba mức: Bắt buộc (`MUST`), Khuyến nghị (`SHOULD`), Tùy chọn (`MAY`).
2. Điều kiện tiền đề: Trạng thái hệ thống và điều kiện dữ liệu cần thỏa mãn trước khi kích hoạt chức năng.
3. Dữ liệu đầu vào: Bảng danh mục tham số kèm kiểu dữ liệu nghiệp vụ, ràng buộc giá trị và tính bắt buộc.
4. Quy tắc xử lý: Trình tự các bước xử lý nghiệp vụ tuần tự, các bước kiểm tra tính hợp lệ dữ liệu và viện dẫn mã lỗi tương ứng tại Mục 3 (`ERR-*`) khi phát hiện vi phạm.
5. Dữ liệu đầu ra và bất biến: Kết quả dữ liệu ghi nhận vào tệp lưu trữ, thông điệp phản hồi người dùng và các bất biến tài chính được duy trì sau xử lý.
6. Tiêu chí nghiệm thu: Bảng các kịch bản kiểm thử dạng Given-When-Then kèm mã kịch bản `AC-FR-xxx-yy` để kiểm chứng sự đáp ứng của chức năng.

Tên nhóm nhu cầu và tên danh mục xuất hiện trong các tiêu chí nghiệm thu là dữ liệu kịch bản giả lập, không phải quy định cứng của lõi nghiệp vụ. Khi kiểm thử, người thực hiện thay bằng nhóm và danh mục thực tế trong cấu hình triển khai.

Bảng chia 20 yêu cầu chức năng theo 6 nhóm chức năng:


| STT      | Nhóm chức năng             | Mã nhóm  | Số lượng FR | Dải mã                      |
| :--------: | :-------------------------- | :--------: | :-----------: | :--------------------------- |
| 1        | Ví                         | `FR-WAL` | 5           | `FR-WAL-01` đến `FR-WAL-05` |
| 2        | Ghi nhận thu chi           | `FR-TXN` | 5           | `FR-TXN-01` đến `FR-TXN-05` |
| 3        | Chuyển tiền nội bộ         | `FR-TRF` | 2           | `FR-TRF-01` đến `FR-TRF-02` |
| 4        | Danh mục phân loại         | `FR-CAT` | 3           | `FR-CAT-01` đến `FR-CAT-03` |
| 5        | Thống kê và báo cáo        | `FR-REP` | 2           | `FR-REP-01` đến `FR-REP-02` |
| 6        | Lưu trữ và sao lưu dữ liệu | `FR-DAT` | 3           | `FR-DAT-01` đến `FR-DAT-03` |
| **Tổng** | **6 nhóm chức năng**       |          | **20 FR**   |                             |

### 4.1 FR-WAL: Nhóm chức năng Ví

Nhóm chức năng Ví quản lý các tài khoản thanh toán và nguồn tiền dùng được ngay của Người dùng. Hệ thống có sẵn một ví tiền mặt và cho phép Người dùng tạo thêm ví mới. Nhóm chức năng này giữ số dư ví luôn đúng theo quy tắc `BR-FIN-02` và cho thấy cơ cấu tiền trong các ví ngay tại thời điểm xem.

#### 4.1.1 FR-WAL-01: Tạo ví mới

Bảng thuộc tính:


| Thuộc tính        | Giá trị                                                                                                                         |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu          | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`), Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`)  |
| Ca sử dụng        | Tạo ví mới (`UC-WAL-01`), Cập nhật thông tin ví (`UC-WAL-03`)                                                                   |
| Quy tắc nghiệp vụ | Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`)                             |
| Mức độ ưu tiên    | Bắt buộc                                                                                                                        |
| Mô tả             | Hệ thống tạo ví tài chính mới với số dư khả dụng ban đầu 0.00 VNĐ ở trạng thái hoạt động để người dùng theo dõi tài khoản tiền. |


Điều kiện tiền đề:

- Hệ thống đã hoàn tất khởi tạo và người dùng đang ở phiên làm việc cục bộ.

Dữ liệu đầu vào:


| Tham số       | Kiểu dữ liệu | Ràng buộc giá trị                                                                                               | Tính bắt buộc |
| ------------- | ------------ | --------------------------------------------------------------------------------------------------------------- | ------------- |
| Tên ví        | Chuỗi ký tự  | Từ 1 đến 50 ký tự sau khi cắt khoảng trắng ở hai đầu, không trùng tên ví hiện có theo quy tắc tên (`BR-NAM-01`) | Bắt buộc      |
| Ghi chú mô tả | Chuỗi ký tự  | Tối đa 255 ký tự                                                                                                | Tùy chọn      |


Quy tắc xử lý:

1. Hệ thống kiểm tra định dạng tên ví theo quy tắc tên (`BR-NAM-01`). Nếu tên rỗng sau khi cắt khoảng trắng ở hai đầu hoặc dài hơn 50 ký tự, hệ thống từ chối và báo lỗi tên sai định dạng (`ERR-NAM-001`).
2. Hệ thống kiểm tra tính duy nhất của tên ví theo quy tắc tên (`BR-NAM-01`). Nếu tên trùng với ví đang hoạt động hoặc ví đã lưu trữ, hệ thống từ chối và báo lỗi trùng tên ví (`ERR-NAM-002`).
3. Hệ thống gán trạng thái hoạt động ban đầu cho ví mới là hoạt động (`ACTIVE`), đặt số dư khả dụng khởi điểm bằng 0.00 VNĐ theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). Hệ thống không nhận số dư ban đầu trực tiếp khi tạo ví. Khi khởi tạo hệ thống lần đầu, hệ thống tạo sẵn đúng một ví tiền mặt có tên "Tiền mặt" với số dư 0.00 VNĐ.
4. Hệ thống cấp mã định danh duy nhất cho ví mới, ghi nhận thời điểm tạo lập và lưu bản ghi vào tệp dữ liệu cục bộ.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                             |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Bản ghi ví mới gồm mã định danh ví, tên ví, trạng thái hoạt động, thời điểm tạo lập và ghi chú mô tả.                                                |
| Bất biến hệ thống | Số dư khả dụng của ví mới bằng đúng 0.00 VNĐ; tổng số dư trong các ví của toàn hệ thống không thay đổi theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                   | Kết quả kỳ vọng                                                                               |
| ------------- | ------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| `AC-WAL-01.1` | Người dùng nhập tên ví "MB Bank" và ghi chú hợp lệ                                    | Hệ thống tạo thành công ví mới ở trạng thái hoạt động, số dư khả dụng hiển thị đúng 0.00 VNĐ. |
| `AC-WAL-01.2` | Người dùng nhập tên ví chỉ gồm ký tự khoảng trắng hoặc dài hơn 50 ký tự               | Hệ thống từ chối tạo bản ghi và báo lỗi định dạng tên (`ERR-NAM-001`).                        |
| `AC-WAL-01.3` | Hệ thống đã có ví "MB Bank", người dùng tạo ví mới với tên "mb bank" hoặc " MB Bank " | Hệ thống từ chối tạo bản ghi và báo lỗi tên ví đã tồn tại (`ERR-NAM-002`).                    |


#### 4.1.2 FR-WAL-02: Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                          |
| -------------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`)                                                                       |
| Ca sử dụng liên quan | Tạo ví mới (`UC-WAL-01`), Xem danh sách ví (`UC-WAL-02`)                                                                         |
| Quy tắc áp dụng      | Quy tắc độ chính xác tài chính (`BR-FIN-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`)                                            |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                |
| Mô tả hành vi        | Hệ thống truy vấn danh sách ví và tính toán số dư khả dụng độc lập theo thời gian thực từ toàn bộ lịch sử dòng tiền của từng ví. |


Điều kiện tiền đề:

- Dữ liệu hệ thống đã nạp thành công vào bộ nhớ làm việc.

Dữ liệu đầu vào:


| Tham số           | Kiểu dữ liệu | Ràng buộc giá trị                                                                         | Tính bắt buộc |
| ----------------- | ------------ | ----------------------------------------------------------------------------------------- | ------------- |
| Bộ lọc trạng thái | Mã phân loại | Thuộc tập hợp: hoạt động (`ACTIVE`, mặc định), lưu trữ (`ARCHIVED`), hoặc toàn bộ (`ALL`) | Tùy chọn      |


Quy tắc xử lý:

1. Hệ thống tiếp nhận tham số lọc trạng thái ví; mặc định chỉ truy xuất các ví có trạng thái hoạt động (`ACTIVE`).
2. Với mỗi ví, hệ thống tính lại số dư khả dụng từ lịch sử dòng tiền theo công thức quy định tại quy tắc bảo toàn số dư ví (`BR-FIN-02`). Hệ thống tính đủ các khoản thu, khoản chi, tiền chuyển đi, tiền nhận về và các khoản điều chỉnh số dư. Hệ thống tuyệt đối không sử dụng giá trị số dư gán trực tiếp.
3. Hệ thống tính tổng số dư toàn hệ thống bằng tổng số dư của toàn bộ ví đang hoạt động và các ví lưu trữ có số dư lớn hơn 0.00 VNĐ, sử dụng số thập phân chính xác và quy tắc làm tròn ngân hàng (`BR-FIN-01`).
4. Hệ thống sắp xếp danh sách ví theo thứ tự tên hoặc thời điểm tạo lập và trả về kết quả truy vấn.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                         |
| ----------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Danh sách bản ghi ví gồm mã định danh ví, tên ví, trạng thái hoạt động, số dư khả dụng tính lại và thời điểm giao dịch gần nhất. |
| Bất biến hệ thống | Thao tác đọc dữ liệu thuần túy, bảo toàn nguyên trạng số dư và lịch sử giao dịch trong hệ thống.                                 |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                         | Kết quả kỳ vọng                                                                                                                             |
| ------------- | ----------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| `AC-WAL-02.1` | Ví "Timo" có số dư điều chỉnh 1,000,000 VNĐ, một giao dịch thu 500,000 VNĐ và một giao dịch chi 200,000 VNĐ | Hệ thống tính toán và hiển thị số dư khả dụng của ví Timo chính xác 1,300,000.00 VNĐ theo quy tắc bảo toàn số dư ví (`BR-FIN-02`).          |
| `AC-WAL-02.2` | Người dùng chọn bộ lọc trạng thái hoạt động (`ACTIVE`)                                                      | Hệ thống chỉ hiển thị các ví đang hoạt động, ẩn các ví ở trạng thái lưu trữ (`ARCHIVED`).                                                   |
| `AC-WAL-02.3` | Tổng hợp số dư toàn hệ thống tại thời điểm truy vấn                                                         | Số liệu khớp từng đơn vị đồng với tổng số dư của các ví thành phần theo quy tắc độ chính xác tài chính (`BR-FIN-01`), sai số bằng 0.00 VNĐ. |


#### 4.1.3 FR-WAL-03: Cập nhật thông tin ví

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                             |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`)                                                                                                          |
| Ca sử dụng liên quan | Tạo ví mới (`UC-WAL-01`), Cập nhật thông tin ví (`UC-WAL-03`)                                                                                                       |
| Quy tắc áp dụng      | Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`)                                                                 |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                   |
| Mô tả hành vi        | Hệ thống cho phép cập nhật tên ví, ghi chú mô tả, chuyển đổi trạng thái lưu trữ hoặc xóa vĩnh viễn ví chưa phát sinh giao dịch mà không làm thay đổi số dư lịch sử. |


Điều kiện tiền đề:

- Ví cần cập nhật tồn tại trong hệ thống dữ liệu.

Dữ liệu đầu vào:


| Tham số           | Kiểu dữ liệu | Ràng buộc giá trị                                                                                            | Tính bắt buộc |
| ----------------- | ------------ | ------------------------------------------------------------------------------------------------------------ | ------------- |
| Mã định danh ví   | Chuỗi ký tự  | Mã ví hợp lệ đang tồn tại trong hệ thống                                                                     | Bắt buộc      |
| Tên ví mới        | Chuỗi ký tự  | Từ 1 đến 50 ký tự sau khi cắt khoảng trắng ở hai đầu, không trùng tên ví khác theo quy tắc tên (`BR-NAM-01`) | Tùy chọn      |
| Ghi chú mô tả mới | Chuỗi ký tự  | Tối đa 255 ký tự                                                                                             | Tùy chọn      |
| Trạng thái ví     | Mã phân loại | Thuộc tập hợp: hoạt động (`ACTIVE`) hoặc lưu trữ (`ARCHIVED`)                                                | Tùy chọn      |


Quy tắc xử lý:

1. Hệ thống kiểm tra tên ví mới (nếu có thay đổi) theo quy tắc tên (`BR-NAM-01`). Nếu tên sai định dạng, hệ thống từ chối cập nhật (`ERR-NAM-001`). Nếu tên trùng một ví khác, hệ thống từ chối cập nhật (`ERR-NAM-002`). Hệ thống không đối chiếu trùng với chính ví đang sửa.
2. Kiểm tra điều kiện lưu trữ ví: Hệ thống cấm lưu trữ ví tiền mặt có sẵn được tạo từ lần khởi tạo đầu tiên. Với các ví khác, hệ thống cho phép chuyển sang trạng thái lưu trữ (`ARCHIVED`) bất kể số dư hiện tại bằng 0 hay lớn hơn 0; số dư của ví lưu trữ vẫn được tính vào tổng số dư hệ thống theo quy tắc bảo toàn số dư ví (`BR-FIN-02`).
3. Kiểm tra điều kiện xóa vĩnh viễn ví: Hệ thống chỉ cho phép xóa vĩnh viễn bản ghi ví khi ví chưa từng phát sinh bất kỳ bản ghi nào trong sổ giao dịch thu chi, sổ giao dịch chuyển tiền hoặc sổ điều chỉnh số dư. Khi xóa thành công, tên ví được giải phóng khỏi phạm vi kiểm tra duy nhất. Nếu ví đã phát sinh giao dịch trong quá khứ, hệ thống từ chối xóa vĩnh viễn và chỉ cho phép chuyển sang trạng thái lưu trữ. Hệ thống tuyệt đối cấm xóa ví tiền mặt có sẵn.
4. Hệ thống không cho phép thay đổi trực tiếp số dư khả dụng của ví trong chức năng cập nhật thông tin ví theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). Toàn bộ thay đổi hợp lệ được lưu vào tệp dữ liệu cục bộ.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                           |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Bản ghi dữ liệu   | Bản ghi ví đã cập nhật thông tin tên, ghi chú mô tả hoặc trạng thái lưu trữ.                                                                                       |
| Bất biến hệ thống | Số dư khả dụng của ví và lịch sử dòng tiền được giữ nguyên vẹn; tổng số dư trong các ví toàn hệ thống không thay đổi theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                   | Kết quả kỳ vọng                                                                                         |
| ------------- | ------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------- |
| `AC-WAL-03.1` | Người dùng đổi tên ví "Vietcombank" thành "VCB Digibank" chưa tồn tại trong hệ thống  | Hệ thống cập nhật thành công tên ví mới, giữ nguyên số dư và toàn bộ giao dịch lịch sử của ví.          |
| `AC-WAL-03.2` | Người dùng yêu cầu chuyển trạng thái lưu trữ (`ARCHIVED`) hoặc xóa ví tiền mặt có sẵn | Hệ thống từ chối thao tác và giữ nguyên trạng thái hoạt động của ví tiền mặt có sẵn.                    |
| `AC-WAL-03.3` | Người dùng yêu cầu xóa vĩnh viễn một ví đã có ít nhất một giao dịch trong lịch sử     | Hệ thống từ chối thao tác xóa vĩnh viễn và chỉ cho phép chuyển ví sang trạng thái lưu trữ (`ARCHIVED`). |


#### 4.1.4 FR-WAL-04: Thống kê cơ cấu số dư theo ví

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                              |
| -------------------- | -------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`)                                                           |
| Ca sử dụng liên quan | Tạo ví mới (`UC-WAL-01`), Xem danh sách ví (`UC-WAL-02`)                                                             |
| Quy tắc áp dụng      | Quy tắc độ chính xác tài chính (`BR-FIN-01`)                                                                         |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                    |
| Mô tả hành vi        | Hệ thống tính toán tỷ lệ phần trăm số dư của từng ví so với tổng số dư các ví trong hệ thống tại thời điểm truy vấn. |


Điều kiện tiền đề:

- Dữ liệu hệ thống đã nạp thành công và có ít nhất một ví trong hệ thống.

Dữ liệu đầu vào:


| Tham số        | Kiểu dữ liệu            | Ràng buộc giá trị                                    | Tính bắt buộc |
| -------------- | ----------------------- | ---------------------------------------------------- | ------------- |
| Ngày tính toán | Ngày tháng (YYYY-MM-DD) | Ngày cần tổng hợp số liệu, mặc định là ngày hiện tại | Tùy chọn      |


Quy tắc xử lý:

1. Hệ thống xác định danh sách ví tính cơ cấu gồm toàn bộ các ví có trạng thái hoạt động (`ACTIVE`) và các ví lưu trữ (`ARCHIVED`) có số dư lớn hơn 0.00 VNĐ. Các ví lưu trữ có số dư bằng 0.00 VNĐ không đưa vào bảng cơ cấu.
2. Hệ thống tính tổng số dư trong các ví bằng tổng số dư của các ví được chọn, áp dụng số thập phân chính xác theo quy tắc độ chính xác tài chính (`BR-FIN-01`).
3. Nếu tổng số dư bằng 0.00 VNĐ, hệ thống gán tỷ lệ phần trăm của toàn bộ các ví bằng 0.00% và không thực hiện phép chia cho 0 để tránh lỗi số học (`ERR-FIN-002`).
4. Nếu tổng số dư lớn hơn 0.00 VNĐ, hệ thống tính tỷ lệ phần trăm của từng ví bằng số dư ví chia cho tổng số dư nhân 100%. Phép chia sử dụng số thập phân chính xác với tối thiểu 4 chữ số thập phân trung gian và làm tròn ngân hàng về 2 chữ số thập phân theo quy tắc độ chính xác tài chính (`BR-FIN-01`). Đối với ví lưu trữ có số dư lớn hơn 0, tên ví hiển thị kèm nhãn nhận diện đã lưu trữ.
5. Hệ thống sắp xếp danh sách kết quả theo thứ tự tỷ lệ phần trăm giảm dần.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                     |
| ----------------- | ------------------------------------------------------------------------------------------------------------ |
| Bản ghi dữ liệu   | Bảng phân tích cơ cấu số dư gồm tên ví, trạng thái, số dư khả dụng và tỷ lệ phần trăm tương ứng của từng ví. |
| Bất biến hệ thống | Thao tác đọc dữ liệu thuần túy, không làm thay đổi số dư hay trạng thái của các ví trong hệ thống.           |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                    | Kết quả kỳ vọng                                                                                                         |
| ------------- | -------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| `AC-WAL-04.1` | Hệ thống có ví A số dư 6,000,000 VNĐ và ví B số dư 4,000,000 VNĐ (tổng 10,000,000 VNĐ) | Hệ thống tính toán và hiển thị tỷ lệ cơ cấu ví A là 60.00% và ví B là 40.00%.                                           |
| `AC-WAL-04.2` | Toàn bộ các ví trong hệ thống đều có số dư bằng 0.00 VNĐ                               | Hệ thống hiển thị tỷ lệ của từng ví là 0.00%, không phát sinh lỗi chia cho 0 (`ERR-FIN-002`).                           |
| `AC-WAL-04.3` | Hệ thống có ví hoạt động số dư 9,950,000 VNĐ và một ví đã lưu trữ số dư 50,000 VNĐ     | Hệ thống hiển thị ví lưu trữ thành một dòng riêng kèm nhãn đã lưu trữ với tỷ lệ 0.50%, tổng các tỷ lệ đạt đúng 100.00%. |


#### 4.1.5 FR-WAL-05: Điều chỉnh số dư ví

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                  |
| -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`)                                                                               |
| Ca sử dụng liên quan | Điều chỉnh số dư ví (`UC-WAL-04`)                                                                                                        |
| Quy tắc áp dụng      | Quy tắc độ chính xác tài chính (`BR-FIN-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`)                                                    |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                        |
| Mô tả hành vi        | Hệ thống ghi nhận một khoản điều chỉnh số dư độc lập có thể truy vết để đưa số dư khả dụng của ví về đúng giá trị thực tế quan sát được. |


Điều kiện tiền đề:

- Ví cần điều chỉnh tồn tại trong hệ thống và đang ở trạng thái hoạt động (`ACTIVE`).

Dữ liệu đầu vào:


| Tham số                | Kiểu dữ liệu            | Ràng buộc giá trị                                                                              | Tính bắt buộc |
| ---------------------- | ----------------------- | ---------------------------------------------------------------------------------------------- | ------------- |
| Mã định danh ví        | Chuỗi ký tự             | Mã ví hợp lệ đang ở trạng thái hoạt động                                                       | Bắt buộc      |
| Số dư thực tế quan sát | Số tiền                 | Số thập phân chính xác lớn hơn hoặc bằng 0.00 VNĐ theo quy tắc bảo toàn số dư ví (`BR-FIN-02`) | Bắt buộc      |
| Lý do điều chỉnh       | Chuỗi ký tự             | Tối đa 255 ký tự sau khi cắt khoảng trắng ở hai đầu; cho phép để trống                          | Tùy chọn      |
| Thời điểm điều chỉnh   | Ngày tháng (YYYY-MM-DD) | Ngày ghi nhận điều chỉnh, mặc định là ngày hiện tại                                            | Tùy chọn      |


Quy tắc xử lý:

1. Hệ thống kiểm tra ví theo mã định danh. Nếu không tìm thấy ví hoặc ví đang ở trạng thái lưu trữ (`ARCHIVED`), hệ thống từ chối thực hiện thao tác.
2. Hệ thống kiểm tra số dư thực tế quan sát: Bắt buộc là số tiền không âm, lớn hơn hoặc bằng 0.00 VNĐ theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). Nếu số dư âm, hệ thống từ chối theo quy tắc bảo toàn số dư ví (`ERR-WAL-001`). Nếu sai định dạng số học, hệ thống từ chối theo lỗi định dạng số tiền (`ERR-FIN-001`).
3. Hệ thống kiểm tra lý do điều chỉnh: Cho phép để trống hoặc chỉ chứa khoảng trắng. Nếu Người dùng nhập lý do, chuỗi sau khi cắt khoảng trắng ở hai đầu có độ dài tối đa 255 ký tự; nếu vượt quá 255 ký tự, hệ thống từ chối tiếp nhận.
4. Hệ thống tính giá trị chênh lệch điều chỉnh bằng số dư thực tế quan sát trừ số dư khả dụng hiện tại của ví theo quy tắc độ chính xác tài chính (`BR-FIN-01`) và quy tắc bảo toàn số dư ví (`BR-FIN-02`). Nếu giá trị chênh lệch bằng đúng 0.00 VNĐ, hệ thống từ chối ghi nhận để tránh tạo khoản ghi rỗng.
5. Hệ thống tạo bản ghi điều chỉnh số dư độc lập và bất biến trong sổ điều chỉnh số dư, lưu trữ số dư trước điều chỉnh, số dư sau điều chỉnh (bằng đúng số dư thực tế quan sát), giá trị chênh lệch, lý do điều chỉnh (nếu có, hoặc chuỗi rỗng) và thời điểm ghi nhận. Hệ thống tuyệt đối cấm chỉnh sửa bản ghi điều chỉnh số dư sau khi đã lưu.
6. Hệ thống tính lại số dư khả dụng của ví ngay lập tức từ lịch sử dòng tiền theo quy tắc bảo toàn số dư ví (`BR-FIN-02`).

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                                                                              |
| ----------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Bản ghi điều chỉnh số dư lưu trong sổ điều chỉnh số dư kèm số dư ví đã cập nhật.                                                                                                                                      |
| Bất biến hệ thống | Số dư khả dụng của ví sau điều chỉnh bằng chính xác số dư thực tế quan sát; tổng số dư trong các ví của toàn hệ thống biến động đúng bằng giá trị chênh lệch điều chỉnh theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                               | Kết quả kỳ vọng                                                                                                      |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------- |
| `AC-WAL-05.1` | Ví "Tiền mặt" có số dư hệ thống ghi nhận 2,000,000.00 VNĐ, thực tế đếm được 1,750,000.00 VNĐ kèm lý do "Kiểm kê tiền mặt định kỳ" | Hệ thống tạo khoản điều chỉnh số dư có chênh lệch -250,000.00 VNĐ, số dư ví cập nhật chính xác 1,750,000.00 VNĐ.     |
| `AC-WAL-05.2` | Người dùng gửi yêu cầu điều chỉnh với số dư thực tế âm hoặc lý do điều chỉnh dài quá 255 ký tự                                    | Hệ thống từ chối tiếp nhận giao dịch và giữ nguyên số dư hiện tại của ví (`ERR-WAL-001`).                            |
| `AC-WAL-05.3` | Người dùng nhập số dư thực tế bằng đúng số dư hiện tại của ví hoặc điều chỉnh trên ví đã lưu trữ (`ARCHIVED`)                     | Hệ thống từ chối ghi nhận và không tạo bản ghi điều chỉnh chênh lệch bằng 0.                                         |
| `AC-WAL-05.4` | Người dùng gửi yêu cầu điều chỉnh số dư hợp lệ nhưng để trống trường lý do điều chỉnh (hoặc chỉ chứa khoảng trắng)                | Hệ thống tạo khoản điều chỉnh số dư thành công với lý do rỗng và cập nhật số dư ví chính xác theo số dư quan sát.   |


### 4.2 FR-TXN: Nhóm chức năng Ghi nhận thu chi

Nhóm chức năng Ghi nhận thu chi quản lý sổ giao dịch thu chi, gồm các luồng tiền xảy ra hàng ngày của Người dùng. Nhóm chức năng này kết nối biến động số dư với cấu trúc danh mục 3 cấp và bảo đảm số dư ví phản ánh chính xác theo quy tắc `BR-FIN-02`.

#### 4.2.1 FR-TXN-01: Tạo mới giao dịch thu nhập

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                 |
| -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`), Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)                                                             |
| Ca sử dụng liên quan | Ghi nhận giao dịch thu chi (`UC-TXN-01`)                                                                                                                                                |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`), Quy tắc cấu trúc danh mục ba cấp (`BR-FIN-04`)                                            |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                       |
| Mô tả hành vi        | Hệ thống ghi nhận khoản thu nhập vào sổ giao dịch thu chi, phân loại vào danh mục thu nhập, làm tăng số dư ví tiếp nhận và tự động tạo giao dịch phí liên kết nếu có phát sinh chi phí. |


Điều kiện tiền đề:

- Hệ thống đã có ít nhất một ví ở trạng thái hoạt động (`ACTIVE`) và ít nhất một danh mục thu nhập đang dùng.

Dữ liệu đầu vào:


| Tham số                   | Kiểu dữ liệu              | Ràng buộc giá trị                                                                                       | Tính bắt buộc                  |
| ------------------------- | ------------------------- | ------------------------------------------------------------------------------------------------------- | ------------------------------ |
| Ngày giao dịch            | Ngày tháng (`YYYY-MM-DD`) | Định dạng chuẩn, không vượt quá ngày hiện tại; mặc định là ngày hiện tại                                | Bắt buộc                       |
| Số tiền thu nhập          | Số tiền (`VNĐ`)           | Phải lớn hơn 0, làm tròn 2 chữ số thập phân theo quy tắc độ chính xác tài chính (`BR-FIN-01`)           | Bắt buộc                       |
| Mã định danh ví           | Chuỗi ký tự               | Ví phải tồn tại trong hệ thống và ở trạng thái hoạt động (`ACTIVE`)                                     | Bắt buộc                       |
| Mã định danh danh mục     | Chuỗi ký tự               | Danh mục phải đang dùng và thuộc nhánh Thu nhập (`INCOME`) theo quy tắc cấu trúc danh mục (`BR-FIN-04`) | Bắt buộc                       |
| Chi phí giao dịch         | Số tiền (`VNĐ`)           | Không được âm, mặc định là 0.00 VNĐ; nếu lớn hơn 0 thì số dư ví sau giao dịch không được âm             | Tùy chọn                       |
| Mã định danh danh mục phí | Chuỗi ký tự               | Danh mục chi tiêu đang dùng để hạch toán chi phí; bắt buộc khi chi phí lớn hơn 0                        | Bắt buộc khi chi phí lớn hơn 0 |
| Ghi chú mô tả             | Chuỗi ký tự               | Tối đa 255 ký tự                                                                                        | Tùy chọn                       |


Quy tắc xử lý:

1. Hệ thống kiểm tra số tiền thu nhập theo quy tắc độ chính xác tài chính (`BR-FIN-01`). Nếu số tiền sai định dạng số học hoặc chứa ký tự không hợp lệ, hệ thống từ chối và báo lỗi số tiền sai định dạng (`ERR-FIN-001`). Nếu số tiền nhỏ hơn hoặc bằng 0, hệ thống từ chối ghi nhận giao dịch.
2. Hệ thống kiểm tra ví nhận tiền. Nếu ví không tồn tại hoặc ở trạng thái đã lưu trữ (`ARCHIVED`), hệ thống từ chối và báo lỗi ví không hoạt động.
3. Hệ thống kiểm tra danh mục thu nhập theo quy tắc cấu trúc danh mục (`BR-FIN-04`). Nếu danh mục không thuộc nhánh Thu nhập (`INCOME`), đã bị xóa, hoặc chưa có danh mục chi tiết đang dùng nào, hệ thống từ chối tiếp nhận giao dịch.
4. Hệ thống kiểm tra chi phí giao dịch theo quy tắc độ chính xác tài chính (`BR-FIN-01`). Nếu chi phí nhỏ hơn 0, hệ thống từ chối và báo lỗi chi phí âm (`ERR-FEE-001`). Khi chi phí lớn hơn 0, người dùng phải cung cấp danh mục chi tiêu đang dùng cho phí; nếu thiếu báo lỗi thiếu danh mục phí (`ERR-FEE-002`), nếu danh mục phí đã xóa hoặc không thuộc nhánh Chi tiêu (`EXPENSE`) báo lỗi danh mục phí không hợp lệ (`ERR-FEE-003`).
5. Khi chi phí lớn hơn 0, hệ thống kiểm tra số dư ví theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). Số dư ví trước giao dịch cộng số tiền thu nhập trừ chi phí phải lớn hơn hoặc bằng 0. Nếu không thỏa mãn, hệ thống từ chối toàn bộ giao dịch do số dư ví không đủ (`ERR-WAL-001`).
6. Hệ thống tạo bản ghi giao dịch thu nhập với mã định danh duy nhất, trạng thái hợp lệ, tự động liên kết nhóm danh mục từ danh mục chi tiết. Khi có chi phí lớn hơn 0, hệ thống tạo thêm bản ghi phí giao dịch thuộc loại chi tiêu với số tiền bằng đúng chi phí, liên kết với mã giao dịch thu nhập và ghi nhận cả hai bản ghi đồng thời.
7. Hệ thống cập nhật số dư ví tiếp nhận theo quy tắc bảo toàn số dư ví (`BR-FIN-02`), tăng số tiền thu nhập và trừ chi phí nếu có. Tổng số dư trong các ví của toàn hệ thống tăng đúng bằng số tiền thu nhập trừ chi phí.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                                                          |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Bản ghi giao dịch thu nhập trong sổ giao dịch thu chi kèm mã định danh duy nhất; bản ghi phí giao dịch liên kết nếu có chi phí.                                                                   |
| Bất biến hệ thống | Số dư ví tiếp nhận tăng đúng bằng khoản thu nhập trừ chi phí; tổng số dư trong các ví của toàn hệ thống tăng đúng bằng số tiền thu nhập trừ chi phí theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                                            | Kết quả kỳ vọng                                                                                                                                 |
| ------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| `AC-TXN-01.1` | Ví "MB Bank" có số dư 10,000,000.00 VNĐ, người dùng ghi nhận thu nhập lương 25,000,000.00 VNĐ kèm chi phí 5,500.00 VNĐ với danh mục phí hợp lệ | Hệ thống ghi nhận thành công giao dịch thu nhập cùng phí giao dịch liên kết, số dư ví MB Bank tăng lên đúng 34,994,500.00 VNĐ.                  |
| `AC-TXN-01.2` | Người dùng nhập số tiền thu nhập nhỏ hơn hoặc bằng 0 hoặc sai định dạng                                                                        | Hệ thống từ chối ghi nhận giao dịch và báo lỗi số tiền sai định dạng (`ERR-FIN-001`).                                                           |
| `AC-TXN-01.3` | Ví "Tiền mặt" có số dư 0.00 VNĐ, người dùng ghi nhận thu nhập 100,000.00 VNĐ nhưng chi phí 120,000.00 VNĐ hoặc thiếu danh mục phí              | Hệ thống từ chối toàn bộ giao dịch, báo lỗi số dư ví không đủ (`ERR-WAL-001`) hoặc thiếu danh mục phí (`ERR-FEE-002`), số dư ví không thay đổi. |


#### 4.2.2 FR-TXN-02: Tạo mới giao dịch chi tiêu

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                                                   |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`), Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)                                                                                               |
| Ca sử dụng liên quan | Ghi nhận giao dịch thu chi (`UC-TXN-01`)                                                                                                                                                                                  |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`), Quy tắc cấu trúc danh mục ba cấp (`BR-FIN-04`)                                                                              |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                                                         |
| Mô tả hành vi        | Hệ thống kiểm tra khả năng chi trả của ví, ghi nhận khoản chi tiêu vào sổ giao dịch thu chi, phân loại vào danh mục chi tiêu, làm giảm số dư ví xuất tiền và tự động tạo giao dịch phí liên kết nếu có phát sinh chi phí. |


Điều kiện tiền đề:

- Hệ thống đã có ít nhất một ví ở trạng thái hoạt động (`ACTIVE`) với số dư khả dụng lớn hơn 0 và ít nhất một danh mục chi tiêu đang dùng.

Dữ liệu đầu vào:


| Tham số                   | Kiểu dữ liệu              | Ràng buộc giá trị                                                                                        | Tính bắt buộc                  |
| ------------------------- | ------------------------- | -------------------------------------------------------------------------------------------------------- | ------------------------------ |
| Ngày giao dịch            | Ngày tháng (`YYYY-MM-DD`) | Mặc định là ngày hiện tại, không vượt quá ngày hiện tại                                                  | Bắt buộc                       |
| Số tiền chi tiêu          | Số tiền (`VNĐ`)           | Phải lớn hơn 0, làm tròn 2 chữ số thập phân theo quy tắc độ chính xác tài chính (`BR-FIN-01`)            | Bắt buộc                       |
| Mã định danh ví           | Chuỗi ký tự               | Ví phải tồn tại trong hệ thống và ở trạng thái hoạt động (`ACTIVE`)                                      | Bắt buộc                       |
| Mã định danh danh mục     | Chuỗi ký tự               | Danh mục phải đang dùng và thuộc nhánh Chi tiêu (`EXPENSE`) theo quy tắc cấu trúc danh mục (`BR-FIN-04`) | Bắt buộc                       |
| Chi phí giao dịch         | Số tiền (`VNĐ`)           | Không được âm, mặc định là 0.00 VNĐ                                                                      | Tùy chọn                       |
| Mã định danh danh mục phí | Chuỗi ký tự               | Danh mục chi tiêu đang dùng để hạch toán chi phí; bắt buộc khi chi phí lớn hơn 0                         | Bắt buộc khi chi phí lớn hơn 0 |
| Ghi chú mô tả             | Chuỗi ký tự               | Tối đa 255 ký tự                                                                                         | Tùy chọn                       |


Quy tắc xử lý:

1. Hệ thống kiểm tra số tiền chi tiêu theo quy tắc độ chính xác tài chính (`BR-FIN-01`). Nếu số tiền sai định dạng số học hoặc chứa ký tự không hợp lệ, hệ thống từ chối và báo lỗi số tiền sai định dạng (`ERR-FIN-001`). Nếu số tiền nhỏ hơn hoặc bằng 0, hệ thống từ chối ghi nhận giao dịch.
2. Hệ thống kiểm tra ví chi tiền. Nếu ví không tồn tại hoặc ở trạng thái đã lưu trữ (`ARCHIVED`), hệ thống từ chối và báo lỗi ví không hoạt động.
3. Hệ thống kiểm tra danh mục chi tiêu theo quy tắc cấu trúc danh mục (`BR-FIN-04`). Nếu danh mục không thuộc nhánh Chi tiêu (`EXPENSE`), đã bị xóa, hoặc chưa có danh mục chi tiết đang dùng nào, hệ thống từ chối tiếp nhận giao dịch.
4. Hệ thống kiểm tra chi phí giao dịch theo quy tắc độ chính xác tài chính (`BR-FIN-01`). Nếu chi phí nhỏ hơn 0, hệ thống từ chối và báo lỗi chi phí âm (`ERR-FEE-001`). Khi chi phí lớn hơn 0, người dùng phải cung cấp danh mục chi tiêu đang dùng cho phí; nếu thiếu báo lỗi thiếu danh mục phí (`ERR-FEE-002`), nếu danh mục phí đã xóa hoặc không thuộc nhánh Chi tiêu (`EXPENSE`) báo lỗi danh mục phí không hợp lệ (`ERR-FEE-003`).
5. Hệ thống kiểm tra khả năng chi trả của ví theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). Số dư khả dụng hiện tại của ví phải lớn hơn hoặc bằng tổng số tiền chi tiêu và chi phí. Nếu số dư không đủ bù đắp, hệ thống từ chối toàn bộ giao dịch và báo lỗi số dư ví không đủ (`ERR-WAL-001`).
6. Hệ thống tạo bản ghi giao dịch chi tiêu với mã định danh duy nhất, loại chi tiêu (`EXPENSE`), tự động liên kết nhóm danh mục từ danh mục chi tiết. Khi có chi phí lớn hơn 0, hệ thống tạo thêm bản ghi phí giao dịch thuộc loại chi tiêu với số tiền bằng đúng chi phí, liên kết với mã giao dịch chi tiêu và ghi nhận cả hai bản ghi đồng thời.
7. Hệ thống cập nhật số dư ví xuất tiền theo quy tắc bảo toàn số dư ví (`BR-FIN-02`), trừ trực tiếp tổng số tiền chi tiêu và chi phí khỏi số dư ví. Tổng số dư trong các ví của toàn hệ thống giảm đúng bằng tổng số tiền chi và chi phí.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                                                              |
| ----------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Bản ghi giao dịch chi tiêu trong sổ giao dịch thu chi kèm mã định danh duy nhất; bản ghi phí giao dịch liên kết nếu có chi phí.                                                                       |
| Bất biến hệ thống | Số dư ví chi tiền giảm đúng bằng tổng số tiền chi tiêu và chi phí; tổng số dư trong các ví của toàn hệ thống giảm đúng bằng tổng số tiền chi và chi phí theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                                      | Kết quả kỳ vọng                                                                                                              |
| ------------- | ---------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| `AC-TXN-02.1` | Ví "Tiền mặt" có số dư 500,000.00 VNĐ, người dùng ghi nhận chi tiêu 120,000.00 VNĐ kèm chi phí 3,300.00 VNĐ với danh mục phí hợp lệ      | Hệ thống ghi nhận thành công giao dịch chi tiêu cùng phí giao dịch liên kết, số dư ví Tiền mặt còn chính xác 376,700.00 VNĐ. |
| `AC-TXN-02.2` | Ví "Tiền mặt" có số dư 200,000.00 VNĐ, người dùng ghi nhận khoản chi 150,000.00 VNĐ kèm chi phí 60,000.00 VNĐ                            | Hệ thống từ chối toàn bộ giao dịch vì tổng số tiền cần trừ vượt quá số dư khả dụng (`ERR-WAL-001`), số dư ví giữ nguyên.     |
| `AC-TXN-02.3` | Người dùng chọn danh mục thuộc nhánh Thu nhập (`INCOME`), chọn danh mục đã xóa, hoặc ghi chi phí lớn hơn 0 nhưng không chọn danh mục phí | Hệ thống từ chối tiếp nhận giao dịch và báo lỗi loại danh mục không khớp hoặc thiếu danh mục phí (`ERR-FEE-002`).            |


#### 4.2.3 FR-TXN-03: Tra cứu và lọc lịch sử giao dịch thu chi

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                      |
| -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`), Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)                                                  |
| Ca sử dụng liên quan | Tra cứu và lọc lịch sử giao dịch (`UC-TXN-02`)                                                                                                                               |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`), Quy tắc cấu trúc danh mục ba cấp (`BR-FIN-04`)                                                                          |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                            |
| Mô tả hành vi        | Hệ thống tìm kiếm, lọc theo nhiều tiêu chí kết hợp và tổng hợp số liệu thu chi, tính toán dòng tiền thuần trên sổ giao dịch thu chi phục vụ đối soát và phân tích tài chính. |


Điều kiện tiền đề:

- Hệ thống đã hoàn tất khởi tạo và người dùng truy cập sổ giao dịch thu chi.

Dữ liệu đầu vào:


| Tham số                         | Kiểu dữ liệu              | Ràng buộc giá trị                                                                                | Tính bắt buộc |
| ------------------------------- | ------------------------- | ------------------------------------------------------------------------------------------------ | ------------- |
| Khoảng ngày giao dịch           | Ngày tháng (`YYYY-MM-DD`) | Ngày bắt đầu không được sau ngày kết thúc; mặc định từ đầu tháng đến cuối tháng hiện tại         | Tùy chọn      |
| Danh sách mã định danh ví       | Danh sách chuỗi ký tự     | Lọc theo các ví được chọn; mặc định toàn bộ ví                                                   | Tùy chọn      |
| Loại giao dịch                  | Mã phân loại              | Thuộc tập hợp: tất cả (`ALL`), thu nhập (`INCOME`), chi tiêu (`EXPENSE`); mặc định là tất cả     | Tùy chọn      |
| Mã định danh nhóm hoặc danh mục | Chuỗi ký tự               | Mã nhóm hoặc danh mục cần lọc, gồm cả danh mục đã xóa                                            | Tùy chọn      |
| Từ khóa tìm kiếm                | Chuỗi ký tự               | Tìm kiếm chuỗi con trong phần ghi chú, không phân biệt chữ hoa chữ thường                        | Tùy chọn      |
| Phân trang                      | Cặp số nguyên dương       | Chỉ số trang từ 1 trở lên và số bản ghi mỗi trang từ 10 đến 100; mặc định trang 1 với 50 bản ghi | Tùy chọn      |


Quy tắc xử lý:

1. Hệ thống kết hợp đồng thời tất cả các tiêu chí lọc do người dùng thiết lập. Nếu ngày bắt đầu sau ngày kết thúc, hệ thống từ chối truy vấn và báo lỗi khoảng ngày không hợp lệ.
2. Hệ thống tìm kiếm theo từ khóa trong trường ghi chú không phân biệt chữ hoa hay chữ thường.
3. Trên toàn bộ tập kết quả thỏa mãn điều kiện lọc trước khi phân trang, hệ thống tổng hợp các chỉ số tài chính theo quy tắc độ chính xác tài chính (`BR-FIN-01`) với 2 chữ số thập phân:
   - Tổng thu nhập: Tổng số tiền của các giao dịch thu nhập đang tồn tại.
   - Tổng chi tiêu: Tổng số tiền của các giao dịch chi tiêu đang tồn tại, bao gồm cả các phí giao dịch liên kết.
   - Dòng tiền thuần: Tổng thu nhập trừ tổng chi tiêu.
4. Hệ thống sắp xếp kết quả mặc định theo ngày giao dịch giảm dần (mới nhất xếp trước), tiếp theo theo thời điểm tạo giảm dần, sau đó trích xuất các bản ghi theo trang chỉ định.
5. Bản ghi phí giao dịch thuộc loại chi tiêu được lọc và hiển thị thành một dòng riêng biệt kèm nhãn liên kết tới giao dịch gốc phát sinh chi phí.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                       |
| ----------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Danh sách bản ghi giao dịch theo trang truy vấn kèm tổng số bản ghi và tổng số trang; kèm chỉ số tổng hợp gồm tổng thu nhập, tổng chi tiêu và dòng tiền thuần. |
| Bất biến hệ thống | Thao tác tra cứu và lọc không làm thay đổi số dư ví và không làm biến động bất kỳ dữ liệu nào trong hệ thống.                                                  |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                              | Kết quả kỳ vọng                                                                                                                                                                      |
| ------------- | ---------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `AC-TXN-03.1` | Người dùng lọc giao dịch từ ngày 01/01/2026 đến 31/01/2026 với loại giao dịch chi tiêu (`EXPENSE`)               | Hệ thống trả về chính xác danh sách các khoản chi trong khoảng thời gian, tổng hợp tổng chi tiêu chuẩn xác với 2 chữ số thập phân theo quy tắc độ chính xác tài chính (`BR-FIN-01`). |
| `AC-TXN-03.2` | Người dùng nhập khoảng ngày có ngày bắt đầu sau ngày kết thúc                                                    | Hệ thống từ chối thực hiện truy vấn và báo lỗi khoảng ngày không hợp lệ.                                                                                                             |
| `AC-TXN-03.3` | Hệ thống có giao dịch chi tiêu 120,000.00 VNĐ kèm chi phí 3,300.00 VNĐ trong tháng, người dùng lọc loại chi tiêu | Hệ thống hiển thị hai dòng gồm giao dịch chi tiêu và phí giao dịch liên kết, tính tổng chi tiêu bằng đúng 123,300.00 VNĐ.                                                            |


#### 4.2.4 FR-TXN-04: Chỉnh sửa giao dịch thu chi

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                                              |
| -------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`), Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`), Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)                              |
| Ca sử dụng liên quan | Chỉnh sửa giao dịch thu chi (`UC-TXN-03`)                                                                                                                                                                            |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`), Quy tắc cấu trúc danh mục ba cấp (`BR-FIN-04`)                                                                         |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                                                    |
| Mô tả hành vi        | Hệ thống cập nhật thông tin giao dịch thu chi đã ghi nhận, hoàn nguyên tác động số dư trên ví ban đầu và áp dụng số dư trên ví mới trong cùng một bước xử lý, đồng thời điều chỉnh phí giao dịch liên kết tương ứng. |


Điều kiện tiền đề:

- Giao dịch cần chỉnh sửa tồn tại trong hệ thống và không phải là bản ghi phí giao dịch độc lập.

Dữ liệu đầu vào:


| Tham số                       | Kiểu dữ liệu              | Ràng buộc giá trị                                                                                | Tính bắt buộc                  |
| ----------------------------- | ------------------------- | ------------------------------------------------------------------------------------------------ | ------------------------------ |
| Mã định danh giao dịch        | Chuỗi ký tự               | Mã giao dịch thu chi hợp lệ đang tồn tại trong hệ thống                                          | Bắt buộc                       |
| Ngày giao dịch mới            | Ngày tháng (`YYYY-MM-DD`) | Không vượt quá ngày hiện tại                                                                     | Bắt buộc                       |
| Số tiền mới                   | Số tiền (`VNĐ`)           | Lớn hơn 0, làm tròn 2 chữ số thập phân theo quy tắc độ chính xác tài chính (`BR-FIN-01`)         | Bắt buộc                       |
| Mã định danh ví mới           | Chuỗi ký tự               | Ví phải tồn tại và ở trạng thái hoạt động (`ACTIVE`)                                             | Bắt buộc                       |
| Mã định danh danh mục mới     | Chuỗi ký tự               | Cùng loại thu chi với giao dịch gốc; cho phép giữ nguyên danh mục cũ kể cả khi đã xóa            | Bắt buộc                       |
| Chi phí mới                   | Số tiền (`VNĐ`)           | Không được âm; nếu để trống thì giữ nguyên chi phí hiện tại                                      | Tùy chọn                       |
| Mã định danh danh mục phí mới | Chuỗi ký tự               | Danh mục chi tiêu đang dùng; bắt buộc khi cập nhật chi phí mới lớn hơn 0 mà trước đó chưa có phí | Bắt buộc khi phát sinh phí mới |
| Ghi chú mô tả                 | Chuỗi ký tự               | Tối đa 255 ký tự                                                                                 | Tùy chọn                       |


Quy tắc xử lý:

1. Hệ thống kiểm tra sự tồn tại của giao dịch theo mã định danh. Nếu không tìm thấy hoặc mã thuộc về bản ghi phí giao dịch độc lập, hệ thống từ chối và báo lỗi nghiệp vụ. Phí giao dịch không được chỉnh sửa trực tiếp mà chỉ được điều chỉnh thông qua giao dịch gốc.
2. Hệ thống kiểm tra tính cố định của loại giao dịch: loại giao dịch thu chi (`INCOME` hoặc `EXPENSE`) không được phép thay đổi. Danh mục mới phải thuộc cùng loại với giao dịch gốc theo quy tắc cấu trúc danh mục (`BR-FIN-04`). Cho phép giữ nguyên danh mục cũ kể cả khi danh mục đó đã bị xóa; nếu đổi danh mục khác thì danh mục mới phải đang ở trạng thái hoạt động.
3. Hệ thống hoàn nguyên số dư trên ví ban đầu theo quy tắc bảo toàn số dư ví (`BR-FIN-02`):
   - Nếu giao dịch gốc là thu nhập (`INCOME`), hệ thống trừ số tiền thu nhập cũ và hoàn trả chi phí cũ nếu có vào ví cũ. Nếu việc trừ tiền làm số dư ví cũ bị âm, hệ thống từ chối thao tác và báo lỗi số dư ví không đủ (`ERR-WAL-001`).
   - Nếu giao dịch gốc là chi tiêu (`EXPENSE`), hệ thống hoàn trả số tiền chi cũ và chi phí cũ nếu có vào ví cũ.
4. Hệ thống áp dụng tác động số dư lên ví mới theo quy tắc bảo toàn số dư ví (`BR-FIN-02`) dựa trên trạng thái số dư sau khi đã hoàn nguyên ví cũ kể cả khi ví mới trùng với ví cũ:
   - Nếu giao dịch là thu nhập (`INCOME`), hệ thống cộng số tiền mới và trừ chi phí mới vào ví mới; số dư ví mới sau giao dịch không được âm (`ERR-WAL-001`).
   - Nếu giao dịch là chi tiêu (`EXPENSE`), hệ thống kiểm tra số dư ví mới phải đủ trừ tổng số tiền mới và chi phí mới; nếu không đủ báo lỗi số dư ví không đủ (`ERR-WAL-001`).
5. Hệ thống xử lý phí giao dịch liên kết theo quy tắc độ chính xác tài chính (`BR-FIN-01`): nếu chi phí mới bằng 0 thì xóa bản ghi phí liên kết hiện có; nếu chi phí mới lớn hơn 0 và chưa có phí thì tạo mới với danh mục phí chi tiêu hợp lệ (nếu thiếu báo lỗi thiếu danh mục phí (`ERR-FEE-002`), nếu sai loại hoặc đã xóa báo lỗi danh mục phí không hợp lệ (`ERR-FEE-003`)); nếu cả hai đều lớn hơn 0 thì cập nhật số tiền và danh mục phí của bản ghi phí hiện tại.
6. Hệ thống thực thi đồng thời việc cập nhật giao dịch, điều chỉnh phí liên kết và hoàn nguyên áp dụng số dư ví. Nếu xảy ra lỗi tại bất kỳ bước nào, toàn bộ thao tác bị hủy và dữ liệu được bảo toàn nguyên vẹn.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                                                |
| ----------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Bản ghi giao dịch thu chi đã cập nhật thông tin; bản ghi phí giao dịch liên kết tương ứng được tạo mới, cập nhật hoặc xóa bỏ.                                                           |
| Bất biến hệ thống | Số dư ví cũ và ví mới biến động đúng bằng mức chênh lệch tài chính giữa dữ liệu mới và cũ; tổng số dư trong các ví toàn hệ thống bảo toàn theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                                                             | Kết quả kỳ vọng                                                                                                       |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| `AC-TXN-04.1` | Người dùng sửa giao dịch chi 100,000.00 VNĐ từ ví A thành chi 150,000.00 VNĐ từ ví B (ví B đủ tiền)                                                             | Hệ thống hoàn lại 100,000.00 VNĐ vào ví A, trừ 150,000.00 VNĐ từ ví B và cập nhật thành công thông tin giao dịch.     |
| `AC-TXN-04.2` | Người dùng sửa tăng số tiền chi tiêu từ 200,000.00 VNĐ lên 800,000.00 VNĐ trong khi số dư khả dụng của ví chỉ còn 300,000.00 VNĐ                                | Hệ thống từ chối cập nhật và báo lỗi số dư ví không đủ (`ERR-WAL-001`), dữ liệu giao dịch và số dư ví giữ nguyên vẹn. |
| `AC-TXN-04.3` | Người dùng sửa giao dịch chi tiêu 120,000.00 VNĐ đang có phí 3,300.00 VNĐ bằng cách đặt chi phí về 0.00 VNĐ hoặc nhập mã của một phí giao dịch để sửa trực tiếp | Hệ thống xóa phí liên kết và hoàn lại 3,300.00 VNĐ vào ví, hoặc từ chối sửa trực tiếp phí giao dịch độc lập.          |


#### 4.2.5 FR-TXN-05: Xóa giao dịch thu chi

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                    |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`), Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`), Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)    |
| Ca sử dụng liên quan | Xóa giao dịch thu chi (`UC-TXN-04`)                                                                                                                                                        |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`)                                                                                               |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                          |
| Mô tả hành vi        | Hệ thống xóa bản ghi giao dịch thu chi khỏi sổ giao dịch, tự động xóa phí giao dịch liên kết nếu có và hoàn nguyên số dư của các ví liên quan về trạng thái trước khi giao dịch phát sinh. |


Điều kiện tiền đề:

- Giao dịch cần xóa tồn tại trong hệ thống và không phải là bản ghi phí giao dịch độc lập.

Dữ liệu đầu vào:


| Tham số                | Kiểu dữ liệu          | Ràng buộc giá trị                                       | Tính bắt buộc |
| ---------------------- | --------------------- | ------------------------------------------------------- | ------------- |
| Mã định danh giao dịch | Chuỗi ký tự           | Mã giao dịch thu chi hợp lệ đang tồn tại trong hệ thống | Bắt buộc      |
| Xác nhận xóa           | Cờ logic (Đúng / Sai) | Bắt buộc mang giá trị Đúng (`true`) để chấp thuận xóa   | Bắt buộc      |


Quy tắc xử lý:

1. Hệ thống kiểm tra sự tồn tại của giao dịch theo mã định danh. Nếu không tìm thấy, hệ thống từ chối và báo lỗi không tìm thấy giao dịch. Nếu mã đầu vào là mã của một bản ghi phí giao dịch độc lập, hệ thống từ chối và báo lỗi phí giao dịch không được xóa trực tiếp.
2. Hệ thống kiểm tra cờ xác nhận xóa. Nếu tham số xác nhận mang giá trị Sai (`false`), hệ thống hủy thao tác và bảo toàn nguyên trạng dữ liệu.
3. Hệ thống kiểm tra điều kiện hoàn nguyên số dư ví theo quy tắc bảo toàn số dư ví (`BR-FIN-02`):
   - Nếu giao dịch cần xóa là thu nhập (`INCOME`), việc xóa yêu cầu trừ số tiền thu nhập và hoàn lại chi phí nếu có vào ví đã nhận. Nếu số dư ví sau khi trừ bị âm, hệ thống từ chối thao tác xóa và báo lỗi số dư ví không đủ (`ERR-WAL-001`).
   - Nếu giao dịch cần xóa là chi tiêu (`EXPENSE`), hệ thống hoàn trả số tiền chi và chi phí nếu có vào số dư của ví chi tiền.
4. Hệ thống thực hiện đồng thời việc xóa bản ghi giao dịch gốc cùng bản ghi phí giao dịch liên kết nếu có khỏi sổ giao dịch thu chi, đồng thời hoàn nguyên số dư ví. Hai tác vụ cùng thành công hoặc cùng bị hủy bỏ.
5. Hệ thống tính toán lại các chỉ số thống kê tài chính và dòng tiền liên quan sau khi bản ghi bị xóa.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                                                            |
| ----------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Bản ghi giao dịch thu chi cùng phí giao dịch liên kết nếu có bị xóa vĩnh viễn khỏi sổ giao dịch thu chi.                                                                                            |
| Bất biến hệ thống | Số dư ví liên quan được phục hồi chính xác về trạng thái trước khi giao dịch phát sinh; tổng số dư trong các ví của toàn hệ thống biến động tương ứng theo quy tắc bảo toàn số dư ví (`BR-FIN-02`). |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                                           | Kết quả kỳ vọng                                                                                                      |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------- |
| `AC-TXN-05.1` | Ví "MB Bank" có số dư 4,500,000.00 VNĐ sau khi chi 500,000.00 VNĐ, người dùng xác nhận xóa giao dịch chi này                                  | Hệ thống xóa thành công bản ghi giao dịch, số dư ví MB Bank được hoàn nguyên về đúng 5,000,000.00 VNĐ.               |
| `AC-TXN-05.2` | Ví có số dư hiện tại 200,000.00 VNĐ, người dùng yêu cầu xóa giao dịch thu nhập 1,000,000.00 VNĐ đã ghi nhận trước đó                          | Hệ thống từ chối xóa vì việc trừ tiền thu nhập làm số dư ví bị âm (`ERR-WAL-001`), dữ liệu được bảo toàn nguyên vẹn. |
| `AC-TXN-05.3` | Ví "MB Bank" có số dư 376,700.00 VNĐ sau khi ghi chi tiêu 120,000.00 VNĐ kèm phí 3,300.00 VNĐ, người dùng xác nhận xóa giao dịch chi tiêu này | Hệ thống xóa đồng thời giao dịch chi tiêu và phí liên kết, số dư ví MB Bank được phục hồi về đúng 500,000.00 VNĐ.    |


### 4.3 FR-TRF: Nhóm chức năng Chuyển tiền nội bộ

Nhóm chức năng Chuyển tiền nội bộ xử lý việc chuyển tiền giữa các ví của cùng một Người dùng theo cơ chế ghi nhận đồng thời (trừ ví nguồn, cộng ví đích). Nhóm chức năng này giữ tổng số dư các ví luôn không đổi theo quy tắc `BR-FIN-03`. Nếu phát sinh chi phí, tổng số dư giảm đúng bằng chi phí đó.

#### 4.3.1 FR-TRF-01: Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                                                  |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Mục tiêu đáp ứng     | Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`), Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)                                                                                                      |
| Ca sử dụng liên quan | Chuyển tiền nội bộ (`UC-TRF-01`)                                                                                                                                                                                         |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`), Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`)                                                            |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                                                        |
| Mô tả hành vi        | Hệ thống thực hiện cơ chế ghi nhận đồng thời để chuyển tiền từ ví nguồn sang ví đích, trừ số tiền chuyển và chi phí khỏi ví nguồn, cộng số tiền chuyển vào ví đích và tự động tạo giao dịch phí liên kết nếu có chi phí. |


Điều kiện tiền đề:

- Hệ thống đã có ít nhất hai ví ở trạng thái hoạt động (`ACTIVE`) với số dư khả dụng của ví nguồn lớn hơn 0.

Dữ liệu đầu vào:


| Tham số                   | Kiểu dữ liệu              | Ràng buộc giá trị                                                                                                              | Tính bắt buộc                  |
| ------------------------- | ------------------------- | ------------------------------------------------------------------------------------------------------------------------------ | ------------------------------ |
| Mã định danh ví nguồn     | Chuỗi ký tự               | Ví nguồn phải tồn tại trong hệ thống và ở trạng thái hoạt động (`ACTIVE`)                                                      | Bắt buộc                       |
| Mã định danh ví đích      | Chuỗi ký tự               | Ví đích phải tồn tại trong hệ thống, ở trạng thái hoạt động (`ACTIVE`) và khác ví nguồn theo quy tắc chuyển tiền (`BR-FIN-03`) | Bắt buộc                       |
| Số tiền chuyển            | Số tiền (`VNĐ`)           | Phải lớn hơn 0, làm tròn 2 chữ số thập phân theo quy tắc độ chính xác tài chính (`BR-FIN-01`)                                  | Bắt buộc                       |
| Ngày giao dịch            | Ngày tháng (`YYYY-MM-DD`) | Định dạng chuẩn, không vượt quá ngày hiện tại; mặc định là ngày hiện tại                                                       | Bắt buộc                       |
| Chi phí giao dịch         | Số tiền (`VNĐ`)           | Không được âm, mặc định là 0.00 VNĐ                                                                                            | Tùy chọn                       |
| Mã định danh danh mục phí | Chuỗi ký tự               | Danh mục chi tiêu đang dùng để hạch toán chi phí; bắt buộc khi chi phí lớn hơn 0                                               | Bắt buộc khi chi phí lớn hơn 0 |
| Ghi chú mô tả             | Chuỗi ký tự               | Tối đa 255 ký tự                                                                                                               | Tùy chọn                       |


Quy tắc xử lý:

1. Hệ thống kiểm tra số tiền chuyển và chi phí giao dịch theo quy tắc độ chính xác tài chính (`BR-FIN-01`). Nếu số tiền sai định dạng số học hoặc chứa ký tự không hợp lệ, hệ thống từ chối và báo lỗi số tiền sai định dạng (`ERR-FIN-001`). Nếu số tiền chuyển nhỏ hơn hoặc bằng 0, hệ thống từ chối và báo lỗi số tiền chuyển không hợp lệ (`ERR-TRF-002`). Nếu chi phí nhỏ hơn 0, hệ thống từ chối và báo lỗi chi phí âm (`ERR-FEE-001`).
2. Hệ thống kiểm tra hai ví tham gia giao dịch theo quy tắc chuyển tiền nội bộ (`BR-FIN-03`). Ví nguồn và ví đích phải cùng tồn tại ở trạng thái hoạt động (`ACTIVE`). Nếu một trong hai ví không hoạt động, hệ thống từ chối giao dịch. Nếu ví đích trùng với ví nguồn, hệ thống từ chối và báo lỗi ví nguồn trùng ví đích (`ERR-TRF-001`).
3. Khi chi phí lớn hơn 0, hệ thống yêu cầu cung cấp danh mục chi tiêu đang dùng cho phí; nếu thiếu báo lỗi thiếu danh mục phí (`ERR-FEE-002`), nếu danh mục phí đã xóa hoặc không thuộc nhánh Chi tiêu (`EXPENSE`) báo lỗi danh mục phí không hợp lệ (`ERR-FEE-003`). Nếu chi phí bằng 0, hệ thống không yêu cầu danh mục phí.
4. Hệ thống kiểm tra khả năng chi trả của ví nguồn theo quy tắc bảo toàn số dư ví (`BR-FIN-02`) và chuyển tiền nội bộ (`BR-FIN-03`). Số dư khả dụng của ví nguồn phải lớn hơn hoặc bằng tổng số tiền chuyển và chi phí. Nếu số dư ví nguồn không đủ bù đắp, hệ thống từ chối toàn bộ giao dịch và báo lỗi số dư ví không đủ (`ERR-WAL-001`).
5. Hệ thống thực thi cơ chế ghi nhận đồng thời theo quy tắc chuyển tiền nội bộ (`BR-FIN-03`): trừ tổng số tiền chuyển và chi phí khỏi ví nguồn, cộng số tiền chuyển vào ví đích. Hai biến động số dư này cùng có hiệu lực hoặc cùng bị hủy bỏ.
6. Hệ thống tạo bản ghi giao dịch chuyển tiền với mã định danh duy nhất vào sổ chuyển tiền nội bộ. Khi chi phí lớn hơn 0, hệ thống đồng thời tạo thêm bản ghi phí giao dịch thuộc loại chi tiêu với số tiền bằng đúng chi phí, lưu vào sổ giao dịch thu chi và liên kết với mã chuyển tiền vừa tạo. Chi phí chuyển tiền được tính vào tổng chi tiêu của hệ thống, trong khi khoản chuyển tiền thuần túy không làm thay đổi doanh thu hay chi phí.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                                                                                                                 |
| ----------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Bản ghi giao dịch chuyển tiền trong sổ chuyển tiền nội bộ; kèm bản ghi phí giao dịch liên kết trong sổ giao dịch thu chi nếu có chi phí.                                                                                                                 |
| Bất biến hệ thống | Ví nguồn giảm tổng số tiền chuyển và chi phí; ví đích tăng đúng số tiền chuyển; tổng số dư trong các ví của toàn hệ thống bảo toàn nguyên vẹn khi không có chi phí, hoặc giảm đúng bằng chi phí giao dịch theo quy tắc chuyển tiền nội bộ (`BR-FIN-03`). |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                                                             | Kết quả kỳ vọng                                                                                                                                                                                   |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `AC-TRF-01.1` | Ví nguồn "MB Bank" có 5,000,000.00 VNĐ, ví đích "Tiền mặt" có 1,000,000.00 VNĐ, người dùng chuyển 2,000,000.00 VNĐ với phí 5,500.00 VNĐ kèm danh mục phí hợp lệ | Ví MB Bank còn 2,994,500.00 VNĐ, ví Tiền mặt tăng lên 3,000,000.00 VNĐ, hệ thống tạo bản ghi chuyển tiền và phí giao dịch liên kết 5,500.00 VNĐ, tổng số dư toàn hệ thống giảm đúng 5,500.00 VNĐ. |
| `AC-TRF-01.2` | Người dùng chọn ví nguồn trùng với ví đích, hoặc nhập số tiền chuyển nhỏ hơn hoặc bằng 0                                                                        | Hệ thống từ chối tiếp nhận giao dịch và báo lỗi ví nguồn trùng ví đích (`ERR-TRF-001`) hoặc số tiền không hợp lệ (`ERR-TRF-002`).                                                                 |
| `AC-TRF-01.3` | Ví nguồn có 1,000,000.00 VNĐ, người dùng chuyển 1,000,000.00 VNĐ kèm chi phí 5,500.00 VNĐ, hoặc chuyển tiền có phí nhưng thiếu danh mục phí                     | Hệ thống từ chối toàn bộ giao dịch vì số dư ví không đủ bù đắp cả tiền chuyển và phí (`ERR-WAL-001`) hoặc thiếu danh mục phí (`ERR-FEE-002`), số dư hai ví giữ nguyên.                            |


#### 4.3.2 FR-TRF-02: Tra cứu và xóa giao dịch chuyển tiền nội bộ

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                         |
| -------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`), Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)                                                                             |
| Ca sử dụng liên quan | Tra cứu lịch sử chuyển tiền (`UC-TRF-02`), Xóa giao dịch chuyển tiền (`UC-TRF-03`)                                                                                                              |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`), Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`)                                   |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                               |
| Mô tả hành vi        | Hệ thống hiển thị lịch sử chuyển tiền nội bộ theo trình tự thời gian và cho phép xóa giao dịch chuyển tiền, hoàn nguyên đồng thời số dư cả hai ví và tự động xóa phí giao dịch liên kết nếu có. |


Điều kiện tiền đề:

- Hệ thống đã hoàn tất khởi tạo; khi thực hiện xóa, giao dịch chuyển tiền phải đang tồn tại trong hệ thống.

Dữ liệu đầu vào:


| Tham số                  | Kiểu dữ liệu          | Ràng buộc giá trị                                                 | Tính bắt buộc    |
| ------------------------ | --------------------- | ----------------------------------------------------------------- | ---------------- |
| Mã định danh chuyển tiền | Chuỗi ký tự           | Mã giao dịch chuyển tiền cần xóa, bắt buộc tồn tại trong hệ thống | Bắt buộc khi xóa |
| Xác nhận xóa             | Cờ logic (Đúng / Sai) | Bắt buộc mang giá trị Đúng (`true`) để chấp thuận xóa             | Bắt buộc khi xóa |


Quy tắc xử lý:

1. Khi tra cứu lịch sử: Hệ thống truy vấn và hiển thị danh sách giao dịch chuyển tiền sắp xếp theo ngày giao dịch giảm dần, bao gồm ngày giao dịch, ví nguồn, ví đích, số tiền chuyển, chi phí và ghi chú. Thao tác tra cứu không làm biến động dữ liệu hay số dư ví.
2. Khi xóa giao dịch: Hệ thống tìm kiếm theo mã định danh chuyển tiền. Nếu không tìm thấy bản ghi, hệ thống từ chối và báo lỗi không tìm thấy giao dịch chuyển tiền. Nếu cờ xác nhận mang giá trị Sai (`false`), hệ thống hủy thao tác và bảo toàn nguyên trạng dữ liệu.
3. Hệ thống kiểm tra điều kiện hoàn nguyên số dư tại ví đích theo quy tắc bảo toàn số dư ví (`BR-FIN-02`): số dư khả dụng hiện tại của ví đích phải lớn hơn hoặc bằng số tiền đã nhận từ giao dịch chuyển tiền. Nếu ví đích đã chi tiêu dẫn tới số dư không đủ bù hoàn, hệ thống từ chối xóa và báo lỗi số dư ví không đủ (`ERR-WAL-001`).
4. Hệ thống thực hiện khôi phục số dư đồng thời trên cả hai ví theo quy tắc chuyển tiền nội bộ (`BR-FIN-03`): hoàn trả lại số tiền chuyển và chi phí nếu có vào ví nguồn, thu hồi lại số tiền chuyển khỏi ví đích.
5. Hệ thống xóa vĩnh viễn bản ghi giao dịch chuyển tiền khỏi sổ chuyển tiền và xóa bản ghi phí giao dịch liên kết nếu có khỏi sổ giao dịch thu chi. Việc cập nhật số dư và xóa bản ghi chỉ được ghi nhận khi cả hai đều thành công.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                                                                                                                       |
| ----------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Danh sách lịch sử giao dịch chuyển tiền khi tra cứu; hoặc bản ghi chuyển tiền cùng phí liên kết nếu có bị xóa khỏi hệ thống.                                                                                                                                   |
| Bất biến hệ thống | Khi xóa giao dịch thành công, ví nguồn được hoàn lại số tiền chuyển và chi phí, ví đích bị trừ số tiền chuyển; tổng số dư trong các ví của toàn hệ thống phục hồi chính xác về trạng thái trước khi chuyển tiền theo quy tắc ghi nhận đồng thời (`BR-FIN-03`). |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                                        | Kết quả kỳ vọng                                                                                                                               |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `AC-TRF-02.1` | Cho giao dịch chuyển 2,000,000.00 VNĐ từ ví A sang ví B kèm phí 3,300.00 VNĐ, người dùng xác nhận xóa giao dịch thành công                 | Ví A được hoàn trả đúng 2,003,300.00 VNĐ, ví B bị trừ lại 2,000,000.00 VNĐ, bản ghi chuyển tiền và phí giao dịch liên kết bị xóa hoàn toàn.   |
| `AC-TRF-02.2` | Ví B sau khi nhận 2,000,000.00 VNĐ đã chi tiêu dẫn tới số dư hiện tại chỉ còn 500,000.00 VNĐ, người dùng yêu cầu xóa giao dịch chuyển tiền | Hệ thống từ chối xóa giao dịch vì số dư ví B không đủ để thu hồi lại tiền (`ERR-WAL-001`), dữ liệu hai ví giữ nguyên vẹn.                     |
| `AC-TRF-02.3` | Người dùng mở xem lịch sử chuyển tiền nội bộ, hoặc yêu cầu xóa với tham số xác nhận mang giá trị Sai (`false`)                             | Hệ thống hiển thị danh sách chuyển tiền theo ngày giảm dần khi xem lịch sử, hoặc từ chối xóa và bảo toàn nguyên vẹn dữ liệu khi xác nhận sai. |


### 4.4 FR-CAT: Nhóm chức năng Danh mục phân loại

Nhóm chức năng Danh mục phân loại quản lý cấu trúc danh mục 3 cấp của hệ thống. Trong đó Loại thu chi cố định còn Nhóm nhu cầu và Danh mục chi tiết do Người dùng quản lý. Cấu trúc này phục vụ việc phân loại chi tiết các giao dịch thu chi và làm nền tảng cho các báo cáo phân tích tài chính theo quy tắc `BR-FIN-04`.

#### 4.4.1 FR-CAT-01: Tạo cấu trúc danh mục 3 cấp

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                   |
| -------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`)                                                                                                                        |
| Ca sử dụng liên quan | Khởi tạo cấu trúc danh mục (`UC-CAT-01`)                                                                                                                                                  |
| Quy tắc áp dụng      | Quy tắc cấu trúc danh mục ba cấp (`BR-FIN-04`)                                                                                                                                            |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                         |
| Mô tả hành vi        | Hệ thống khởi tạo và duy trì cấu trúc phân cấp danh mục 3 cấp gồm Loại thu chi cố định, Nhóm nhu cầu và Danh mục chi tiết, tự động nạp cấu trúc mẫu ban đầu khi tạo lập hệ thống lần đầu. |


Điều kiện tiền đề:

- Hệ thống được khởi tạo lần đầu trên tệp lưu trữ dữ liệu mới chưa từng ghi nhận thông tin tài chính.

Dữ liệu đầu vào:


| Tham số               | Kiểu dữ liệu  | Ràng buộc giá trị                                                                          | Tính bắt buộc |
| --------------------- | ------------- | ------------------------------------------------------------------------------------------ | ------------- |
| Không yêu cầu tham số | Không áp dụng | Cấu trúc mẫu ban đầu được hệ thống tự động thiết lập một lần khi khởi tạo hệ thống lần đầu | Tùy chọn      |


Quy tắc xử lý:

1. Hệ thống thiết lập cấu trúc phân cấp danh mục đúng 3 cấp theo quy tắc cấu trúc danh mục (`BR-FIN-04`):
   - Cấp 1: Loại thu chi, gồm hai nhánh phân loại độc lập và cố định là Thu nhập (`INCOME`) và Chi tiêu (`EXPENSE`). Hai nhánh này không thể đổi tên, không thể xóa bỏ và không thể tạo thêm.
   - Cấp 2: Nhóm nhu cầu, do người dùng tự quản lý và bắt buộc trực thuộc một nhánh Loại thu chi.
   - Cấp 3: Danh mục chi tiết, trực thuộc một Nhóm nhu cầu và là cấp duy nhất được phép liên kết trực tiếp với các giao dịch thu chi.
2. Khi khởi tạo hệ thống lần đầu, hệ thống tự động nạp cấu trúc danh mục mẫu ban đầu theo quy định tại quy tắc cấu trúc danh mục (`BR-FIN-04`). Cấu trúc mẫu bảo đảm mỗi Loại thu chi có tối thiểu một nhóm nhu cầu mẫu và mỗi nhóm mẫu có tối thiểu một danh mục chi tiết mẫu. Bộ mẫu này chỉ nạp đúng một lần duy nhất và không được tự động nạp lại khi người dùng khởi động lại ứng dụng.
3. Hệ thống áp dụng quy tắc phân cấp nghiêm ngặt: mọi giao dịch thu chi chỉ được liên kết với Danh mục chi tiết ở cấp 3, tuyệt đối không cho phép ghi nhận giao dịch trực tiếp lên cấp 1 hoặc cấp 2.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                     |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Bản ghi dữ liệu   | Cấu trúc danh mục 3 cấp hoàn chỉnh lưu trữ trong tệp dữ liệu cục bộ gồm Loại thu chi, Nhóm nhu cầu và Danh mục chi tiết.                                     |
| Bất biến hệ thống | Khởi tạo cấu trúc danh mục không làm thay đổi số dư ví và không làm biến động số tiền của toàn hệ thống; cấu trúc luôn bảo đảm 2 nhánh Loại thu chi cố định. |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                | Kết quả kỳ vọng                                                                                                                                                                                            |
| ------------- | ------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `AC-CAT-01.1` | Hệ thống khởi chạy lần đầu tiên trên tệp dữ liệu mới hoàn toàn     | Cấu trúc danh mục được sinh đầy đủ 2 nhánh `INCOME` và `EXPENSE`, mỗi nhánh có tối thiểu 1 nhóm nhu cầu mẫu và mỗi nhóm có tối thiểu 1 danh mục chi tiết mẫu theo quy tắc cấu trúc danh mục (`BR-FIN-04`). |
| `AC-CAT-01.2` | Người dùng tạo giao dịch thu chi mới và chọn cấp phân loại dữ liệu | Hệ thống bắt buộc người dùng chọn danh mục chi tiết ở cấp 3, từ chối việc liên kết trực tiếp giao dịch lên loại ở cấp 1 hoặc nhóm ở cấp 2.                                                                 |
| `AC-CAT-01.3` | Người dùng đã xóa một nhóm mẫu rồi khởi động lại hệ thống          | Cấu trúc mẫu không tự ý nạp lại, nhóm đã xóa giữ nguyên vẹn trạng thái đã xóa trong tệp dữ liệu.                                                                                                           |


#### 4.4.2 FR-CAT-02: Tùy biến danh mục người dùng

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                      |
| -------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`)                                                                                                                           |
| Ca sử dụng liên quan | Khởi tạo cấu trúc danh mục (`UC-CAT-01`), Thêm và sửa danh mục (`UC-CAT-02`), Xóa và khôi phục danh mục (`UC-CAT-03`)                                                                        |
| Quy tắc áp dụng      | Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`), Quy tắc cấu trúc danh mục ba cấp (`BR-FIN-04`)                                                                                   |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                            |
| Mô tả hành vi        | Hệ thống cho phép người dùng tạo mới nhóm nhu cầu, tạo mới danh mục chi tiết và đổi tên các mục phân loại theo nhu cầu quản lý tài chính cá nhân mà không làm ảnh hưởng đến dữ liệu lịch sử. |


Điều kiện tiền đề:

- Cấu trúc phân cấp danh mục đã sẵn sàng hoạt động trong hệ thống.

Dữ liệu đầu vào:


| Tham số              | Kiểu dữ liệu | Ràng buộc giá trị                                                                                                 | Tính bắt buộc |
| -------------------- | ------------ | ----------------------------------------------------------------------------------------------------------------- | ------------- |
| Tên phân loại        | Chuỗi ký tự  | Từ 1 đến 50 ký tự sau khi cắt khoảng trắng ở hai đầu, không trùng tên cùng cấp theo quy tắc đặt tên (`BR-NAM-01`) | Bắt buộc      |
| Vị trí trực thuộc    | Chuỗi ký tự  | Mã nhóm cha đang dùng khi tạo danh mục; hoặc Loại thu chi (`INCOME`, `EXPENSE`) khi tạo nhóm                      | Bắt buộc      |
| Biểu tượng nhận diện | Chuỗi ký tự  | Chuỗi ký tự đại diện, tối đa 50 ký tự                                                                             | Tùy chọn      |
| Mã màu hiển thị      | Chuỗi ký tự  | Mã màu định dạng Hex, tối đa 10 ký tự                                                                             | Tùy chọn      |


Quy tắc xử lý:

1. Hệ thống kiểm tra vị trí trực thuộc theo quy tắc cấu trúc danh mục (`BR-FIN-04`): danh mục mới phải thuộc một nhóm cha đang ở trạng thái hoạt động; nhóm mới phải thuộc một nhánh Loại thu chi. Nếu nhóm cha đang ở trạng thái đã xóa, hệ thống từ chối và báo lỗi tạo danh mục trong nhóm đã xóa (`ERR-CAT-005`). Nếu vị trí trực thuộc không tồn tại, hệ thống từ chối yêu cầu.
2. Hệ thống kiểm tra tên phân loại theo quy tắc đặt tên (`BR-NAM-01`): tên không được rỗng sau khi cắt khoảng trắng hai đầu và không dài quá 50 ký tự; nếu vi phạm báo lỗi định dạng tên (`ERR-NAM-001`). Tên không được trùng với mục đang dùng cùng cấp (nhóm cùng Loại thu chi, danh mục cùng nhóm cha); nếu vi phạm báo lỗi trùng tên (`ERR-NAM-002`).
3. Trường hợp tên tạo mới trùng với mục đã xóa cùng cấp: hệ thống thông báo cho người dùng lựa chọn khôi phục mục cũ hoặc tạo mục mới với mã định danh mới. Nếu chọn khôi phục, hệ thống giữ nguyên mã cũ và liên kết lại các giao dịch quá khứ; nếu chọn tạo mới, hệ thống cấp mã định danh mới độc lập.
4. Khi đổi tên nhóm hoặc danh mục: hệ thống kiểm tra quy tắc đặt tên (`BR-NAM-01`), giữ nguyên mã định danh và toàn bộ liên kết giao dịch lịch sử. Hai nhánh Loại thu chi cố định không có chức năng đổi tên hay xóa bỏ.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                         |
| ----------------- | ---------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Bản ghi nhóm nhu cầu hoặc danh mục chi tiết mới; hoặc bản ghi đã cập nhật tên gọi trong tệp dữ liệu.             |
| Bất biến hệ thống | Thao tác tùy biến danh mục không làm biến động số dư ví và không làm thay đổi các giao dịch lịch sử đã ghi nhận. |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                  | Kết quả kỳ vọng                                                                                                  |
| ------------- | -------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| `AC-CAT-02.1` | Người dùng thêm danh mục "Thú cưng" vào nhóm "Nhu cầu cá nhân và giải trí" với tên hợp lệ                            | Danh mục mới được tạo thành công ở trạng thái hoạt động và sẵn sàng để phân loại cho các giao dịch chi tiêu mới. |
| `AC-CAT-02.2` | Người dùng tạo nhóm hoặc danh mục với tên để trống, dài quá 50 ký tự, hoặc trùng tên với mục đang hoạt động cùng cấp | Hệ thống từ chối tiếp nhận và báo lỗi định dạng tên (`ERR-NAM-001`) hoặc lỗi trùng tên (`ERR-NAM-002`).          |
| `AC-CAT-02.3` | Người dùng gửi yêu cầu tạo danh mục chi tiết vào một nhóm cha đang ở trạng thái đã xóa                               | Hệ thống từ chối tạo bản ghi và báo lỗi không thể tạo danh mục trong nhóm đã xóa (`ERR-CAT-005`).                |


#### 4.4.3 FR-CAT-03: Kiểm soát toàn vẹn khi cập nhật và xóa danh mục

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                          |
| -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`)                                                                                               |
| Ca sử dụng liên quan | Thêm và sửa danh mục (`UC-CAT-02`), Xóa và khôi phục danh mục (`UC-CAT-03`), Quản lý danh mục (`UC-CAT-04`)                                                      |
| Quy tắc áp dụng      | Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`), Quy tắc cấu trúc danh mục ba cấp (`BR-FIN-04`)                                                       |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                |
| Mô tả hành vi        | Hệ thống kiểm soát tính toàn vẹn khi đổi tên, chuyển nhóm cha, xóa và khôi phục nhóm hoặc danh mục, bảo đảm dữ liệu lịch sử và báo cáo tài chính luôn nhất quán. |


Điều kiện tiền đề:

- Nhóm nhu cầu hoặc danh mục chi tiết cần xử lý phải đang tồn tại trong hệ thống.

Dữ liệu đầu vào:


| Tham số                   | Kiểu dữ liệu | Ràng buộc giá trị                                                                       | Tính bắt buộc            |
| ------------------------- | ------------ | --------------------------------------------------------------------------------------- | ------------------------ |
| Loại phân loại đích       | Mã phân loại | Chỉ định rõ nhóm nhu cầu hoặc danh mục chi tiết                                         | Bắt buộc                 |
| Mã định danh mục tiêu     | Chuỗi ký tự  | Mã nhóm hoặc danh mục đang tồn tại trong hệ thống                                       | Bắt buộc                 |
| Thao tác yêu cầu          | Mã phân loại | Thuộc tập hợp: đổi tên, chuyển nhóm cha (chỉ cho danh mục), xóa, khôi phục              | Bắt buộc                 |
| Tên mới                   | Chuỗi ký tự  | Từ 1 đến 50 ký tự sau khi cắt khoảng trắng ở hai đầu theo quy tắc đặt tên (`BR-NAM-01`) | Bắt buộc khi đổi tên     |
| Mã định danh nhóm cha mới | Chuỗi ký tự  | Mã nhóm cha mới đang hoạt động và cùng Loại thu chi với nhóm cũ                         | Bắt buộc khi chuyển nhóm |


Quy tắc xử lý:

1. Hệ thống kiểm tra sự tồn tại của đối tượng mục tiêu. Nếu không tìm thấy, hệ thống từ chối và báo lỗi không tìm thấy phân loại tương ứng.
2. Khi xóa nhóm hoặc danh mục:
   - Hệ thống quét sổ giao dịch thu chi để kiểm tra lịch sử liên kết dữ liệu.
   - Nếu danh mục chưa từng có giao dịch: hệ thống xóa vĩnh viễn bản ghi khỏi tệp dữ liệu.
   - Nếu danh mục đã có giao dịch: hệ thống không xóa vĩnh viễn mà chuyển sang trạng thái đã xóa, ẩn khỏi danh sách lựa chọn cho giao dịch mới nhưng bảo toàn nguyên vẹn trong các giao dịch và báo cáo lịch sử kèm nhãn đã xóa.
   - Khi xóa nhóm: hệ thống áp dụng quy tắc xóa tương ứng cho từng danh mục con bên trong; nếu nhóm còn nhóm con có giao dịch thì chuyển nhóm sang trạng thái đã xóa, nếu không có giao dịch thì xóa vĩnh viễn. Nhóm đang dùng cuối cùng của một nhánh Loại thu chi không được phép xóa; nếu vi phạm báo lỗi nhóm duy nhất (`ERR-CAT-002`).
3. Khi chuyển nhóm cha cho danh mục: danh mục chỉ được chuyển sang nhóm cha đang hoạt động cùng Loại thu chi theo quy tắc cấu trúc danh mục (`BR-FIN-04`). Nếu chuyển sang nhóm thuộc Loại thu chi khác, hệ thống từ chối và báo lỗi khác loại thu chi (`ERR-CAT-001`). Nếu nhóm đích đã bị xóa, hệ thống từ chối và báo lỗi nhóm đích đã xóa (`ERR-CAT-003`). Nếu tên danh mục bị trùng trong nhóm đích, hệ thống báo lỗi trùng tên (`ERR-NAM-002`).
4. Khi khôi phục nhóm hoặc danh mục đã xóa:
   - Hệ thống giữ nguyên mã định danh và nối lại các giao dịch cũ.
   - Nếu trong cùng cấp đã có mục đang hoạt động trùng tên, hệ thống từ chối theo lỗi trùng tên (`ERR-NAM-002`).
   - Nếu danh mục cần khôi phục có nhóm cha đang ở trạng thái đã xóa, hệ thống từ chối và báo lỗi nhóm cha đã xóa (`ERR-CAT-004`), yêu cầu khôi phục nhóm cha trước.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                                                      |
| ----------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Bản ghi nhóm hoặc danh mục được cập nhật trạng thái (đổi tên, chuyển nhóm, chuyển sang đã xóa, khôi phục hoặc xóa vĩnh viễn).                                                                 |
| Bất biến hệ thống | Thao tác cập nhật và xóa danh mục không làm thay đổi số dư ví; toàn bộ giao dịch lịch sử được bảo toàn nguyên vẹn liên kết và giá trị tài chính theo quy tắc cấu trúc danh mục (`BR-FIN-04`). |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                                         | Kết quả kỳ vọng                                                                                                                                 |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| `AC-CAT-03.1` | Người dùng xóa danh mục "Ăn uống" đã có 50 giao dịch lịch sử, hoặc xóa danh mục "Học vẽ" mới tạo chưa có giao dịch nào                      | Danh mục "Ăn uống" chuyển sang trạng thái đã xóa và bảo toàn nguyên vẹn 50 giao dịch cũ; danh mục "Học vẽ" được xóa vĩnh viễn khỏi tệp dữ liệu. |
| `AC-CAT-03.2` | Người dùng chuyển danh mục chi tiêu "Xăng xe" sang nhóm thu nhập, hoặc chuyển sang một nhóm chi tiêu đã bị xóa                              | Hệ thống từ chối thao tác và báo lỗi khác loại thu chi (`ERR-CAT-001`) hoặc nhóm đích đã xóa (`ERR-CAT-003`).                                   |
| `AC-CAT-03.3` | Nhánh Chi tiêu chỉ còn đúng 1 nhóm nhu cầu đang dùng và người dùng thực hiện xóa nhóm này; hoặc khôi phục danh mục khi nhóm cha đang bị xóa | Hệ thống từ chối xóa và báo lỗi nhóm duy nhất (`ERR-CAT-002`); hoặc từ chối khôi phục và báo lỗi nhóm cha đã xóa (`ERR-CAT-004`).               |


### 4.5 FR-REP: Nhóm chức năng Thống kê và báo cáo

Nhóm chức năng Thống kê và báo cáo tự động tổng hợp dữ liệu giao dịch và số dư ví. Nhóm chức năng cung cấp chuỗi dòng tiền 12 tháng gần nhất cùng cơ cấu thu chi theo quy tắc `BR-FIN-01` đến `BR-FIN-04`.

#### 4.5.1 FR-REP-01: Thống kê dòng tiền 12 tháng gần nhất

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                                                                    |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`), Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)                                                                                                                |
| Ca sử dụng liên quan | Xem thống kê dòng tiền (`UC-REP-01`)                                                                                                                                                                                                       |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`)                                                                                                                                                                                        |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                                                                          |
| Mô tả hành vi        | Hệ thống tổng hợp chuỗi thời gian dòng tiền liên tục 12 tháng tính đến tháng kết thúc được chọn, tính toán tổng thu nhập, tổng chi tiêu, dòng tiền thuần, tỷ lệ tiết kiệm và các chỉ số trung bình theo quy tắc làm tròn số học tài chính. |


Điều kiện tiền đề:

- Hệ thống đã hoàn tất khởi tạo và người dùng truy cập chức năng thống kê báo cáo tài chính.

Dữ liệu đầu vào:


| Tham số        | Kiểu dữ liệu    | Ràng buộc giá trị                                   | Tính bắt buộc |
| -------------- | --------------- | --------------------------------------------------- | ------------- |
| Tháng kết thúc | Số nguyên dương | Giá trị từ 1 đến 12; mặc định là tháng hiện tại     | Tùy chọn      |
| Năm kết thúc   | Số nguyên dương | Năm hợp lệ từ 2000 trở đi; mặc định là năm hiện tại | Tùy chọn      |


Quy tắc xử lý:

1. Hệ thống xác định khung thời gian thống kê gồm đúng 12 tháng liên tục, kết thúc tại tháng và năm do người dùng lựa chọn và bắt đầu từ 11 tháng trước đó.
2. Với mỗi tháng trong chuỗi 12 tháng, hệ thống tổng hợp dữ liệu giao dịch theo quy tắc độ chính xác tài chính (`BR-FIN-01`):
   - Tổng thu nhập tháng: Tổng số tiền của các giao dịch thu nhập phát sinh trong tháng.
   - Tổng chi tiêu tháng: Tổng số tiền của các giao dịch chi tiêu phát sinh trong tháng, bao gồm cả phí giao dịch liên kết đang tồn tại.
   - Dòng tiền thuần tháng: Tổng thu nhập tháng trừ đi tổng chi tiêu tháng.
   - Tỷ lệ tiết kiệm tháng: Bằng dòng tiền thuần chia cho tổng thu nhập rồi nhân với 100 khi tổng thu nhập lớn hơn 0; nếu tổng thu nhập nhỏ hơn hoặc bằng 0 thì tỷ lệ tiết kiệm mặc định bằng 0.00% để tránh lỗi chia cho 0 (`ERR-FIN-002`).
3. Hệ thống tính toán các chỉ số tài chính trung bình cho toàn bộ chuỗi 12 tháng:
   - Thu nhập bình quân: Tổng thu nhập của cả 12 tháng chia cho 12.
   - Chi tiêu bình quân: Tổng chi tiêu của cả 12 tháng chia cho 12.
   - Dòng tiền thuần bình quân: Tổng dòng tiền thuần của cả 12 tháng chia cho 12.
     Mẫu số tính bình quân luôn cố định là 12, bao gồm cả những tháng chưa phát sinh giao dịch (được tính bằng 0.00 VNĐ).
4. Mọi phép tính số học được thực hiện với độ chính xác cao theo quy tắc độ chính xác tài chính (`BR-FIN-01`), áp dụng quy tắc làm tròn ngân hàng và hiển thị kết quả cuối cùng với 2 chữ số thập phân.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                               |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Bản ghi dữ liệu   | Bảng dữ liệu chuỗi thời gian 12 tháng gồm tổng thu nhập, tổng chi tiêu, dòng tiền thuần và tỷ lệ tiết kiệm từng tháng kèm các chỉ số bình quân cả năm. |
| Bất biến hệ thống | Thao tác thống kê báo cáo là thao tác đọc dữ liệu, không làm biến động số dư ví và không làm thay đổi bất kỳ bản ghi tài chính nào trong hệ thống.     |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                               | Kết quả kỳ vọng                                                                                                                                                                                 |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `AC-REP-01.1` | Người dùng chọn mốc thời gian kết thúc là tháng 09/2026                                                                           | Hệ thống xuất bảng thống kê đầy đủ 12 tháng liên tục từ tháng 10/2025 đến tháng 09/2026.                                                                                                        |
| `AC-REP-01.2` | Trong một tháng có thu nhập 30,000,000.00 VNĐ và chi tiêu 18,000,000.00 VNĐ                                                       | Dòng tiền thuần tháng hiển thị đúng +12,000,000.00 VNĐ và tỷ lệ tiết kiệm hiển thị chính xác 40.00% theo quy tắc độ chính xác tài chính (`BR-FIN-01`).                                          |
| `AC-REP-01.3` | Một tháng trong chuỗi không có thu nhập nhưng có chi tiêu 5,000,000.00 VNĐ; hoặc chuỗi thống kê có tháng chưa phát sinh giao dịch | Dòng tiền thuần hiển thị -5,000,000.00 VNĐ, tỷ lệ tiết kiệm hiển thị 0.00% mà không gây lỗi chia cho 0 (`ERR-FIN-002`); các tháng không có giao dịch được tính 0.00 VNĐ vào bình quân 12 tháng. |


#### 4.5.2 FR-REP-02: Phân tích cơ cấu thu chi theo nhóm và danh mục

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                       |
| -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`), Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)                                                                   |
| Ca sử dụng liên quan | Xem phân tích cơ cấu thu chi (`UC-REP-02`)                                                                                                                                                    |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`), Quy tắc cấu trúc danh mục ba cấp (`BR-FIN-04`)                                                                                           |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                             |
| Mô tả hành vi        | Hệ thống phân tích tỷ trọng phân bổ của các khoản thu nhập hoặc chi tiêu theo nhóm nhu cầu và danh mục chi tiết trong một khoảng thời gian xác định, hỗ trợ so sánh biến động với kỳ liền kề. |


Điều kiện tiền đề:

- Hệ thống đã hoàn tất khởi tạo và người dùng truy cập chức năng phân tích cơ cấu tài chính.

Dữ liệu đầu vào:


| Tham số                   | Kiểu dữ liệu              | Ràng buộc giá trị                                                      | Tính bắt buộc |
| ------------------------- | ------------------------- | ---------------------------------------------------------------------- | ------------- |
| Khoảng ngày phân tích     | Ngày tháng (`YYYY-MM-DD`) | Ngày bắt đầu không được sau ngày kết thúc                              | Bắt buộc      |
| Loại luồng tiền phân tích | Mã phân loại              | Thuộc tập hợp: chi tiêu (`EXPENSE`, mặc định) hoặc thu nhập (`INCOME`) | Bắt buộc      |


Quy tắc xử lý:

1. Hệ thống kiểm tra khoảng ngày phân tích. Nếu ngày bắt đầu sau ngày kết thúc, hệ thống từ chối thực thi và báo lỗi khoảng ngày không hợp lệ.
2. Hệ thống gom cụm và tổng hợp số liệu các giao dịch đang tồn tại theo quy tắc cấu trúc danh mục (`BR-FIN-04`):
   - Gom cụm theo nhóm nhu cầu ở cấp 2: Tính tổng số tiền của từng nhóm. Giao dịch được gom theo nhóm cha hiện tại của danh mục chi tiết.
   - Gom cụm theo danh mục chi tiết ở cấp 3: Tính tổng số tiền của từng danh mục.
3. Hệ thống tính toán tỷ lệ phần trăm cơ cấu theo quy tắc độ chính xác tài chính (`BR-FIN-01`): tỷ lệ của từng nhóm hoặc danh mục bằng tổng tiền mục đó chia cho tổng tiền của loại luồng tiền tương ứng rồi nhân với 100. Phép tính áp dụng quy tắc làm tròn ngân hàng và hiển thị 2 chữ số thập phân. Nếu tổng tiền của loại bằng 0.00 VNĐ, tỷ lệ mặc định là 0.00% nhằm ngăn ngừa lỗi chia cho 0 (`ERR-FIN-002`).
4. Hệ thống sắp xếp các nhóm và danh mục theo thứ tự số tiền giảm dần để nhận diện các mục chiếm tỷ trọng lớn nhất.
5. Khi người dùng yêu cầu so sánh với kỳ liền kề có cùng độ dài thời gian: hệ thống tính chênh lệch giá trị và tỷ lệ tăng giảm tương ứng; nếu kỳ trước bằng 0.00 VNĐ thì hiển thị trạng thái mới phát sinh.
6. Trường hợp nhóm hoặc danh mục đã bị xóa nhưng có giao dịch trong kỳ phân tích: hệ thống hiển thị tên mục kèm nhãn đã xóa và giữ nguyên số liệu độc lập, không gộp vào mục khác nhằm bảo đảm tổng cơ cấu luôn khớp chính xác với tổng số tiền giao dịch thực tế.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                         |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| Bản ghi dữ liệu   | Bảng phân tích cơ cấu 2 cấp (Nhóm nhu cầu và Danh mục chi tiết) gồm tên phân loại, tổng số tiền, tỷ lệ cơ cấu và biến động so sánh với kỳ trước. |
| Bất biến hệ thống | Thao tác phân tích cơ cấu là thao tác đọc dữ liệu, không làm thay đổi số dư ví và không làm biến động dữ liệu toàn hệ thống.                     |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                             | Kết quả kỳ vọng                                                                                                                                                 |
| ------------- | --------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `AC-REP-02.1` | Trong kỳ phân tích có tổng chi tiêu 20,000,000.00 VNĐ, trong đó nhóm "Nhu cầu thiết yếu" chiếm 8,000,000.00 VNĐ | Hệ thống tính toán và hiển thị chính xác tỷ lệ của nhóm "Nhu cầu thiết yếu" là 40.00% theo quy tắc độ chính xác tài chính (`BR-FIN-01`).                        |
| `AC-REP-02.2` | Người dùng phân tích trong khoảng thời gian hoàn toàn không phát sinh giao dịch thu chi nào                     | Hệ thống hiển thị tổng tiền 0.00 VNĐ cùng danh sách rỗng mà không gây lỗi chia cho 0 (`ERR-FIN-002`).                                                           |
| `AC-REP-02.3` | Kỳ phân tích có giao dịch thuộc nhóm hoặc danh mục đã bị xóa trong quá khứ                                      | Hệ thống vẫn hiển thị nhóm hoặc danh mục đó kèm nhãn đã xóa với đúng số tiền và tỷ trọng tương ứng, tổng giá trị báo cáo khớp chính xác tổng dòng tiền thực tế. |


### 4.6 FR-DAT: Nhóm chức năng Lưu trữ và sao lưu dữ liệu

Nhóm chức năng Lưu trữ và sao lưu dữ liệu bảo đảm tệp dữ liệu hệ thống hoạt động độc lập và không bị mất mát. Dữ liệu được phục hồi theo quy trình kiểm tra 4 bước của `FR-DAT-02` trên môi trường máy tính cục bộ.

#### 4.6.1 FR-DAT-01: Xuất dữ liệu tài chính

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                                        |
| -------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)                                                                                                                                                        |
| Ca sử dụng liên quan | Xuất dữ liệu tài chính (`UC-DAT-01`)                                                                                                                                                                           |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`)                                                                                                                                                            |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                                              |
| Mô tả hành vi        | Hệ thống trích xuất ảnh chụp dữ liệu tài chính nhất quán tại một thời điểm ra tệp định dạng JSON trên đĩa cứng cục bộ, bảo toàn độ chính xác số học cố định của các trường tiền tệ phục vụ sao lưu và lưu trữ. |


Điều kiện tiền đề:

- Hệ thống đã hoàn tất khởi tạo và người dùng truy cập chức năng xuất dữ liệu.

Dữ liệu đầu vào:


| Tham số              | Kiểu dữ liệu | Ràng buộc giá trị                                                                    | Tính bắt buộc |
| -------------------- | ------------ | ------------------------------------------------------------------------------------ | ------------- |
| Phạm vi xuất dữ liệu | Mã phân loại | Thuộc tập hợp: toàn bộ hệ thống (`FULL`), hoặc sổ giao dịch thu chi (`TRANSACTIONS`) | Bắt buộc      |
| Định dạng tệp xuất   | Mã phân loại | Nhận giá trị `JSON` cho cả hai phạm vi xuất                                          | Bắt buộc      |
| Đường dẫn lưu trữ    | Chuỗi ký tự  | Đường dẫn thư mục hoặc tệp đích hợp lệ trên đĩa cứng cục bộ                          | Bắt buộc      |


Quy tắc xử lý:

1. Hệ thống tạo ảnh chụp dữ liệu tại một mốc thời gian nhất quán, bảo đảm toàn bộ danh sách ví, danh mục phân loại, giao dịch thu chi, chuyển tiền nội bộ và các khoản điều chỉnh số dư phản ánh trạng thái đồng bộ duy nhất.
2. Hệ thống định dạng nội dung xuất JSON:
   - Với phạm vi toàn bộ hệ thống (`FULL`): tệp xuất chứa toàn bộ 5 nhóm thực thể gồm ví, danh mục phân loại (gồm cả nhóm và danh mục đã xóa kèm ngày tạo), giao dịch thu chi (gồm cả phí giao dịch liên kết), chuyển tiền nội bộ và bản ghi điều chỉnh số dư.
   - Với phạm vi sổ giao dịch (`TRANSACTIONS`): tệp xuất chứa toàn bộ các giao dịch thu chi và phí giao dịch liên kết đang tồn tại (kèm mã giao dịch phát sinh ra phí).
   - Mọi trường số tiền được lưu dưới dạng chuỗi số học chuẩn xác theo quy tắc độ chính xác tài chính (`BR-FIN-01`), giữ nguyên 2 chữ số thập phân cố định và không làm tròn hay chuyển sang dạng dấu chấm động.
3. Hệ thống đặt tên tệp tự động theo mẫu `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json` với mã hóa UTF-8 không BOM, thực hiện cơ chế ghi tệp an toàn để không để lại tệp hỏng nếu quá trình ghi bị ngắt quãng.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                               |
| ----------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Tệp sao lưu định dạng JSON hoàn chỉnh lưu trữ tại đường dẫn chỉ định trên đĩa cứng cục bộ.                                             |
| Bất biến hệ thống | Thao tác xuất dữ liệu là thao tác đọc, không làm biến động số dư ví và không làm thay đổi bất kỳ bản ghi tài chính nào trong hệ thống. |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                             | Kết quả kỳ vọng                                                                                                                                                            |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `AC-DAT-01.1` | Người dùng yêu cầu xuất dữ liệu toàn bộ hệ thống sang tệp JSON                                                                  | Hệ thống tạo tệp JSON hợp lệ chứa đầy đủ 5 nhóm thực thể dữ liệu mã hóa UTF-8 với thời gian đồng bộ nhất quán.                                                             |
| `AC-DAT-01.2` | Kiểm tra nội dung tệp JSON đã xuất                                                                                              | Toàn bộ các trường số tiền đều là chuỗi số học cố định 2 chữ số thập phân theo quy tắc độ chính xác tài chính (`BR-FIN-01`), không sử dụng số thực dấu chấm động nhị phân. |
| `AC-DAT-01.3` | Xuất sổ giao dịch thu chi khi hệ thống có giao dịch chuyển tiền đang tồn tại phát sinh phí và giao dịch chuyển tiền khác đã xóa | Tệp xuất chứa bản ghi phí của giao dịch chuyển tiền đang tồn tại kèm mã liên kết, không chứa phí của giao dịch chuyển tiền đã bị xóa.                                      |


#### 4.6.2 FR-DAT-02: Phục hồi dữ liệu hệ thống từ bản sao lưu

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                                                     |
| -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`), Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`), Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)                                     |
| Ca sử dụng liên quan | Phục hồi dữ liệu từ bản sao lưu (`UC-DAT-02`)                                                                                                                                                                               |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`), Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`), Quy tắc cấu trúc danh mục ba cấp (`BR-FIN-04`)               |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                                                           |
| Mô tả hành vi        | Hệ thống phục hồi toàn bộ dữ liệu tài chính từ tệp sao lưu JSON qua quy trình kiểm tra 4 bước nghiêm ngặt, thay thế dữ liệu hiện tại bằng dữ liệu phục hồi hoặc hủy bỏ toàn bộ quá trình nếu phát hiện bất kỳ sai lệch nào. |


Điều kiện tiền đề:

- Người dùng cung cấp tệp sao lưu JSON hợp lệ và xác nhận đồng ý thay thế toàn bộ dữ liệu hiện tại trong hệ thống.

Dữ liệu đầu vào:


| Tham số               | Kiểu dữ liệu | Ràng buộc giá trị                                              | Tính bắt buộc |
| --------------------- | ------------ | -------------------------------------------------------------- | ------------- |
| Đường dẫn tệp sao lưu | Chuỗi ký tự  | Đường dẫn trỏ tới tệp sao lưu JSON hợp lệ trên đĩa cứng cục bộ | Bắt buộc      |


Quy tắc xử lý:

1. Hệ thống phân tích cú pháp tệp JSON. Nếu tệp bị hỏng, sai cấu trúc JSON hoặc không mã hóa UTF-8, hệ thống từ chối tiếp nhận và báo lỗi định dạng tệp không hợp lệ.
2. Hệ thống thực thi quy trình kiểm tra toàn vẹn 4 bước trước khi ghi dữ liệu:
   - Bước 1 (Kiểm tra định dạng và số liệu theo `BR-FIN-01`): Toàn bộ các trường bắt buộc phải hiện diện đầy đủ, định dạng ngày tháng hợp lệ dạng YYYY-MM-DD và chuỗi số tiền đúng định dạng số học chuẩn xác (`ERR-FIN-001`).
   - Bước 2 (Toàn vẹn danh mục theo `BR-FIN-04`): Mọi mã danh mục trong các giao dịch phải trỏ tới một danh mục đang tồn tại trong tệp với Loại thu chi tương thích (`INCOME` hoặc `EXPENSE`).
   - Bước 3 (Toàn vẹn ví và chuyển tiền theo `BR-FIN-03`): Mọi giao dịch thu chi và chuyển tiền phải trỏ tới ví có trong danh sách ví; mỗi bản ghi phí chuyển tiền phải liên kết tới một giao dịch chuyển tiền đang tồn tại.
   - Bước 4 (Bảo toàn số dư không âm theo `BR-FIN-02`): Tính toán lại toàn bộ số dư ví và bảo đảm không có bất kỳ ví nào bị âm số dư.
3. Cơ chế hủy bỏ khi có lỗi: Nếu phát hiện bất kỳ sai lệch nào tại bất kỳ bước kiểm tra nào, hệ thống lập tức hủy bỏ toàn bộ quá trình phục hồi, giữ nguyên vẹn 100% dữ liệu hiện tại và thông báo danh sách lỗi cụ thể.
4. Áp dụng dữ liệu: Khi cả 4 bước kiểm tra đều đạt, hệ thống thay thế toàn bộ dữ liệu hiện tại bằng dữ liệu từ tệp sao lưu, tính toán lại số dư các ví và cập nhật lại chuỗi thống kê 12 tháng gần nhất.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                                                       |
| ----------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Toàn bộ dữ liệu hệ thống được phục hồi đồng bộ, gồm danh sách ví, danh mục phân loại, giao dịch thu chi, chuyển tiền nội bộ và các bản ghi điều chỉnh.         |
| Bất biến hệ thống | Dữ liệu được chuyển đổi hoàn toàn sang trạng thái của bản sao lưu hoặc giữ nguyên trạng dữ liệu cũ nếu quá trình kiểm tra phát hiện lỗi vi phạm tính toàn vẹn. |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                                                       | Kết quả kỳ vọng                                                                                                                                  |
| ------------- | ------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| `AC-DAT-02.1` | Người dùng thực hiện phục hồi từ tệp sao lưu JSON hợp lệ thỏa mãn đầy đủ 4 bước kiểm tra                                  | Toàn bộ dữ liệu tài chính và số dư ví được phục hồi nguyên vẹn khớp với thời điểm sao lưu, dữ liệu cũ trước khi phục hồi được thay thế trọn vẹn. |
| `AC-DAT-02.2` | Tệp sao lưu chứa giao dịch trỏ tới danh mục không tồn tại, hoặc phí chuyển tiền trỏ tới mã chuyển tiền không có trong tệp | Hệ thống từ chối phục hồi dữ liệu, báo lỗi liên kết không hợp lệ và giữ nguyên vẹn dữ liệu hiện tại của hệ thống.                                |
| `AC-DAT-02.3` | Tệp sao lưu có chuỗi giao dịch dẫn tới một ví bị số dư âm sau khi tính lại dòng tiền                                      | Hệ thống từ chối phục hồi theo quy tắc bảo toàn số dư ví (`BR-FIN-02`), toàn bộ dữ liệu hiện tại được bảo toàn nguyên trạng.                     |


#### 4.6.3 FR-DAT-03: Kiểm tra lại dữ liệu hệ thống

Bảng thuộc tính:


| Thuộc tính           | Giá trị                                                                                                                                                                                                       |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mục tiêu đáp ứng     | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`), Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`), Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`)                       |
| Ca sử dụng liên quan | Kiểm tra tính nhất quán dữ liệu (`UC-DAT-03`)                                                                                                                                                                 |
| Quy tắc áp dụng      | Quy tắc độ chính xác số học tài chính (`BR-FIN-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`), Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`), Quy tắc cấu trúc danh mục ba cấp (`BR-FIN-04`) |
| Mức độ ưu tiên       | Bắt buộc (`MUST`)                                                                                                                                                                                             |
| Mô tả hành vi        | Hệ thống cung cấp cơ chế tự động đối soát toàn bộ 4 điều kiện toàn vẹn dữ liệu tài chính, phát hiện sớm các sai lệch số dư, chuyển tiền mất cân bằng, xung đột danh mục hoặc mồ côi liên kết phí.             |


Điều kiện tiền đề:

- Hệ thống đã hoàn tất khởi tạo và đang vận hành với tệp lưu trữ dữ liệu cục bộ.

Dữ liệu đầu vào:


| Tham số               | Kiểu dữ liệu | Ràng buộc giá trị                                                                          | Tính bắt buộc |
| --------------------- | ------------ | ------------------------------------------------------------------------------------------ | ------------- |
| Phương thức kích hoạt | Mã phân loại | Thuộc tập hợp: thủ công do người dùng yêu cầu, hoặc tự động theo lịch định kỳ của hệ thống | Bắt buộc      |


Quy tắc xử lý:

1. Hệ thống thực hiện quét và đối soát độc lập 4 điều kiện toàn vẹn dữ liệu:
   - Điều kiện 1 (Bảo toàn số dư ví theo `BR-FIN-02`): Tính toán lại số dư độc lập từ toàn bộ lịch sử dòng tiền và so sánh với số dư ghi nhận của từng ví. Sai số cho phép bằng đúng 0.00 VNĐ theo quy tắc độ chính xác tài chính (`BR-FIN-01`).
   - Điều kiện 2 (Cân bằng nguồn và đích khi chuyển tiền theo `BR-FIN-03`): Quét toàn bộ các giao dịch chuyển tiền nội bộ, số tiền trừ tại ví nguồn phải bằng chính xác số tiền cộng tại ví đích (không tính chi phí).
   - Điều kiện 3 (Tính toàn vẹn danh mục theo `BR-FIN-04`): Quét toàn bộ sổ giao dịch thu chi bảo đảm mọi giao dịch đều gắn với danh mục hợp lệ (danh mục đã xóa vẫn tính là hợp lệ trong lịch sử) và không có giao dịch nào bị sai lệch Loại thu chi.
   - Điều kiện 4 (Toàn vẹn liên kết phí chuyển tiền): Quét toàn bộ các phí giao dịch phát sinh từ chuyển tiền và bảo đảm trỏ tới một giao dịch chuyển tiền đang tồn tại.
2. Hệ thống phân loại kết quả kiểm tra theo các mức độ nghiêm trọng gồm thông tin, cảnh báo và lỗi nghiêm trọng.
3. Đi kèm mỗi phát hiện sai lệch, hệ thống cung cấp đề xuất hành động cụ thể như tính lại số dư tự động hoặc khuyến nghị phục hồi từ bản sao lưu gần nhất.

Dữ liệu đầu ra và bất biến hệ thống:


| Thành phần        | Nội dung                                                                                                                     |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| Bản ghi dữ liệu   | Báo cáo kiểm tra toàn vẹn dữ liệu chi tiết trạng thái của từng điều kiện (Đạt hoặc Không đạt) kèm số lượng bản ghi đã quét.  |
| Bất biến hệ thống | Thao tác kiểm tra lại là thao tác đọc và đối soát, không làm thay đổi số dư ví và không làm biến động dữ liệu toàn hệ thống. |


Tiêu chí nghiệm thu:


| Mã tiêu chí   | Điều kiện kích hoạt                                                                  | Kết quả kỳ vọng                                                                                                                                                   |
| ------------- | ------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `AC-DAT-03.1` | Dữ liệu hệ thống hoàn toàn nhất quán trên toàn bộ các tệp lưu trữ                    | Báo cáo đối soát trả về kết quả 100% Đạt cho cả 4 điều kiện: số dư ví, cân bằng chuyển tiền, liên kết danh mục và liên kết phí giao dịch.                         |
| `AC-DAT-03.2` | Số dư ghi nhận của một ví bị sai lệch so với tổng dòng tiền tính lại từ lịch sử      | Hệ thống phát hiện chính xác ví bị sai lệch số dư theo quy tắc bảo toàn số dư ví (`BR-FIN-02`), hiển thị trạng thái Không đạt và cung cấp đề xuất tính lại số dư. |
| `AC-DAT-03.3` | Tồn tại giao dịch chuyển tiền bị mất cân bằng nguồn đích hoặc phí chuyển tiền mồ côi | Hệ thống phát hiện chính xác giao dịch sai lệch theo quy tắc chuyển tiền (`BR-FIN-03`), hiển thị trạng thái Không đạt kèm mã giao dịch vi phạm.                   |
