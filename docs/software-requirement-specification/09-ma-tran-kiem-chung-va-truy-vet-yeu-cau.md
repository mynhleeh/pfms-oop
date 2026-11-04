## 8. Ma trận kiểm chứng và truy vết yêu cầu

Ma trận truy vết hai chiều và phương pháp kiểm chứng bảo đảm tính nhất quán giữa mục tiêu, yêu cầu chức năng, ca sử dụng, thực thể dữ liệu và quy tắc nghiệp vụ, làm cơ sở nghiệm thu hệ thống.


Bảng tổng hợp cấu trúc danh mục 5 ma trận truy vết và kiểm chứng yêu cầu của hệ thống:

| STT | Mục | Tên ma trận | Chiều đối chiếu | Ý nghĩa |
| :---: | :---: | :--- | :--- | :--- |
| 1 | 8.1.1 | Mục tiêu theo yêu cầu chức năng | `G-*` sang `FR-*` | Mọi mục tiêu đều có yêu cầu chức năng đáp ứng |
| 2 | 8.1.2 | Ca sử dụng theo yêu cầu chức năng | `FR-*` sang `UC-*` | Mọi yêu cầu chức năng đều có kịch bản tương tác |
| 3 | 8.1.3 | Thực thể tác động theo yêu cầu chức năng | `FR-*` sang thực thể dữ liệu | Xác định phạm vi ảnh hưởng dữ liệu của từng chức năng |
| 4 | 8.1.4 | Quy tắc nghiệp vụ theo yêu cầu chức năng | `BR-*` sang `FR-*` | Mọi quy tắc nghiệp vụ đều được thực thi |
| 5 | 8.2 | Phương pháp kiểm chứng yêu cầu | `FR-*`, `NFR-*` sang T, D, I, A | Cách nghiệm thu và môi trường thực hiện |

### 8.1 Ma trận truy vết yêu cầu theo yêu cầu chức năng

Bốn ma trận dưới đây thiết lập chuỗi truy vết hai chiều lấy yêu cầu chức năng làm trục trung tâm, kết nối từ nguồn gốc mục tiêu bài toán, kịch bản ca sử dụng, mô hình dữ liệu đến các quy tắc nghiệp vụ tài chính chi phối. Phạm vi bao phủ 3 mục tiêu hệ thống, 20 yêu cầu chức năng và 20 ca sử dụng, phân bổ theo 6 nhóm chức năng: `WAL`, `TXN`, `TRF`, `CAT`, `REP` và `DAT`.

#### 8.1.1 Ma trận mục tiêu hệ thống theo yêu cầu chức năng

Bảng ma trận truy vết đối chiếu giữa 20 yêu cầu chức năng và 3 mục tiêu hệ thống, trong đó ký hiệu `X` thể hiện yêu cầu chức năng trực tiếp hiện thực hóa mục tiêu tương ứng:

| Yêu cầu chức năng | `G-01` (Ghi nhận dòng tiền, danh mục) | `G-02` (Tổng số dư, cơ cấu ví) | `G-03` (Chính xác số học) |
| --- | :---: | :---: | :---: |
| `FR-WAL-01` (Tạo ví mới) | X | X |  |
| `FR-WAL-02` (Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền) |  | X |  |
| `FR-WAL-03` (Cập nhật thông tin ví) |  | X |  |
| `FR-WAL-04` (Thống kê cơ cấu số dư theo ví) |  | X |  |
| `FR-WAL-05` (Điều chỉnh số dư ví) |  | X |  |
| `FR-TXN-01` (Tạo giao dịch thu nhập) | X |  | X |
| `FR-TXN-02` (Tạo giao dịch chi tiêu) | X |  | X |
| `FR-TXN-03` (Tra cứu và lọc lịch sử giao dịch thu chi) | X |  |  |
| `FR-TXN-04` (Chỉnh sửa giao dịch thu chi) | X |  | X |
| `FR-TXN-05` (Xóa giao dịch thu chi) | X |  | X |
| `FR-TRF-01` (Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn) | X |  | X |
| `FR-TRF-02` (Tra cứu và xóa giao dịch chuyển tiền nội bộ) | X |  | X |
| `FR-CAT-01` (Tạo cấu trúc danh mục 3 cấp) | X |  |  |
| `FR-CAT-02` (Tùy biến danh mục người dùng) | X |  |  |
| `FR-CAT-03` (Kiểm soát toàn vẹn khi cập nhật và xóa danh mục) | X |  |  |
| `FR-REP-01` (Thống kê dòng tiền 12 tháng gần nhất) | X |  | X |
| `FR-REP-02` (Phân tích cơ cấu thu chi theo nhóm và danh mục) | X |  |  |
| `FR-DAT-01` (Xuất dữ liệu tài chính) |  | X | X |
| `FR-DAT-02` (Phục hồi dữ liệu hệ thống từ bản sao lưu) |  | X | X |
| `FR-DAT-03` (Kiểm tra lại dữ liệu hệ thống) |  |  | X |

#### 8.1.2 Ma trận ca sử dụng theo yêu cầu chức năng

Bảng ma trận ánh xạ giữa 20 yêu cầu chức năng và các kịch bản ca sử dụng chi tiết hiện thực hóa trên giao diện và luồng xử lý:

| Yêu cầu chức năng | Ca sử dụng liên quan |
| --- | --- |
| `FR-WAL-01` | `UC-WAL-01`, `UC-WAL-03` |
| `FR-WAL-02` | `UC-WAL-01`, `UC-WAL-02` |
| `FR-WAL-03` | `UC-WAL-01`, `UC-WAL-03` |
| `FR-WAL-04` | `UC-WAL-01`, `UC-WAL-02` |
| `FR-WAL-05` | `UC-WAL-04` |
| `FR-TXN-01` | `UC-TXN-01` |
| `FR-TXN-02` | `UC-TXN-01` |
| `FR-TXN-03` | `UC-TXN-02` |
| `FR-TXN-04` | `UC-TXN-03` |
| `FR-TXN-05` | `UC-TXN-04` |
| `FR-TRF-01` | `UC-TRF-01` |
| `FR-TRF-02` | `UC-TRF-02`, `UC-TRF-03` |
| `FR-CAT-01` | `UC-CAT-01` |
| `FR-CAT-02` | `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03` |
| `FR-CAT-03` | `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04` |
| `FR-REP-01` | `UC-REP-01` |
| `FR-REP-02` | `UC-REP-02` |
| `FR-DAT-01` | `UC-DAT-01` |
| `FR-DAT-02` | `UC-DAT-02` |
| `FR-DAT-03` | `UC-DAT-03` |

#### 8.1.3 Ma trận thực thể tác động theo yêu cầu chức năng

Bảng ma trận xác định phạm vi các thực thể dữ liệu miền bài toán chịu tác động trực tiếp bởi từng nhóm yêu cầu chức năng:

| Yêu cầu chức năng | Thực thể tác động |
| --- | --- |
| `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04` | Ví |
| `FR-WAL-05` | Ví, Điều chỉnh số dư |
| `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`, `FR-TXN-04` | Giao dịch thu chi, Ví, Danh mục |
| `FR-TXN-05` | Giao dịch thu chi, Ví |
| `FR-TRF-01` | Giao dịch chuyển tiền, Giao dịch thu chi, Ví |
| `FR-TRF-02` | Giao dịch chuyển tiền, Giao dịch thu chi, Ví |
| `FR-CAT-01`, `FR-CAT-02` | Danh mục |
| `FR-CAT-03` | Danh mục, Giao dịch thu chi |
| `FR-REP-01` | Thống kê tháng, Giao dịch thu chi |
| `FR-REP-02` | Thống kê tháng, Danh mục, Giao dịch thu chi |
| `FR-DAT-01`, `FR-DAT-02`, `FR-DAT-03` | Dữ liệu tài chính |

#### 8.1.4 Ma trận quy tắc nghiệp vụ theo yêu cầu chức năng

Bảng ma trận truy vết mức độ bao phủ của 5 quy tắc nghiệp vụ tài chính cốt lõi đối với các yêu cầu chức năng tương ứng:

| Mã quy tắc | Tên quy tắc | Yêu cầu chức năng áp dụng |
| --- | --- | --- |
| `BR-FIN-01` | Quy tắc độ chính xác tài chính | `FR-WAL-02`, `FR-WAL-04`, `FR-WAL-05`<br/>`FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`, `FR-TXN-04`<br/>`FR-TRF-01`<br/>`FR-REP-01`, `FR-REP-02`<br/>`FR-DAT-01`, `FR-DAT-03` |
| `BR-FIN-02` | Quy tắc bảo toàn số dư ví | `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-05`<br/>`FR-TXN-01`, `FR-TXN-02`, `FR-TXN-04`, `FR-TXN-05`<br/>`FR-TRF-01`, `FR-TRF-02`<br/>`FR-DAT-02`, `FR-DAT-03` |
| `BR-FIN-03` | Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ | `FR-TRF-01`, `FR-TRF-02`<br/>`FR-DAT-03` |
| `BR-FIN-04` | Quy tắc phân cấp danh mục thu chi 3 cấp | `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-04`<br/>`FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`<br/>`FR-REP-02`<br/>`FR-DAT-02`, `FR-DAT-03` |
| `BR-NAM-01` | Quy tắc tên của ví, nhóm nhu cầu và danh mục | `FR-WAL-01`, `FR-WAL-03`<br/>`FR-CAT-02`, `FR-CAT-03` |

### 8.2 Ma trận phương pháp kiểm chứng yêu cầu

Bảng định nghĩa 4 phương pháp kiểm chứng yêu cầu phần mềm chuẩn hóa theo tiêu chuẩn quốc tế ISO/IEC/IEEE 29148:2018:

| Ký hiệu | Phương pháp | Tên tiếng Anh | Cách thực hiện |
| :---: | --- | --- | --- |
| I | Soát xét kỹ thuật | Inspection | Đối chiếu tài liệu thiết kế, rà soát mã nguồn tĩnh hoặc cấu trúc lưu trữ, không chạy chương trình |
| T | Kiểm thử tự động | Test | Chạy chương trình hoặc mô-đun với dữ liệu đầu vào xác định, so kết quả thực tế với kết quả kỳ vọng (unit test, integration test) |
| D | Trình diễn hoạt động | Demonstration | Thao tác trực tiếp trên giao diện theo kịch bản thực tế để quan sát luồng xử lý và kết quả |
| A | Phân tích định lượng | Analysis | Đánh giá chỉ số kỹ thuật bằng công cụ phân tích dữ liệu, thống kê, đo hiệu năng và độ bao phủ kiểm thử |

#### 8.2.1 Ma trận phương pháp kiểm chứng yêu cầu chức năng (20 FRs)

Bảng ma trận phương pháp kiểm chứng và tiêu chí nghiệm thu kỹ thuật đối với 20 yêu cầu chức năng của hệ thống:

| Mã yêu cầu | Tên yêu cầu | Phương pháp | Tiêu chí nghiệm thu |
| --- | --- | :---: | --- |
| `FR-WAL-01` | Tạo ví mới | T, D | Tạo ví thành công với tên duy nhất và số dư 0.00; lần khởi tạo đầu tiên chỉ có một ví tiền mặt có sẵn |
| `FR-WAL-02` | Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền | T, D | Hiển thị đúng danh sách ví và số dư tính lại từ lịch sử dòng tiền theo công thức Quy tắc bảo toàn số dư ví (`BR-FIN-02`) |
| `FR-WAL-03` | Cập nhật thông tin ví | T, D | Cập nhật được tên và trạng thái ví; cấm đổi trực tiếp số dư khả dụng; cấm lưu trữ ví tiền mặt có sẵn |
| `FR-WAL-04` | Thống kê cơ cấu số dư theo ví | T, D | Tính đúng tỷ lệ phần trăm số dư của từng ví trên tổng số dư các ví |
| `FR-WAL-05` | Điều chỉnh số dư ví | T, D | Ghi đúng chênh lệch điều chỉnh; lý do là tùy chọn (tối đa 255 ký tự); từ chối khi chênh lệch bằng 0 |
| `FR-TXN-01` | Tạo giao dịch thu nhập | T, D | Số tiền > 0 và danh mục thuộc loại thu nhập. Số dư ví nhận tăng đúng bằng số tiền thu trừ chi phí. Ghi phí giao dịch liên kết khi chi phí lớn hơn 0 |
| `FR-TXN-02` | Tạo giao dịch chi tiêu | T, D | Số tiền > 0, danh mục thuộc loại chi tiêu, số dư ví đủ để trả số tiền cộng chi phí. Trừ đúng số dư ví, kèm phí giao dịch liên kết |
| `FR-TXN-03` | Tra cứu và lọc lịch sử giao dịch thu chi | T, D | Lọc đúng theo khoảng ngày, ví, danh mục, từ khóa. Tổng thu chi đúng; phí giao dịch hiện thành dòng riêng có nhãn liên kết và được tính vào tổng chi |
| `FR-TXN-04` | Chỉnh sửa giao dịch thu chi | T, D | Hoàn tác số tiền và chi phí cũ trên ví cũ, áp dụng số tiền và chi phí mới trên ví mới. Phí giao dịch liên kết được tạo, cập nhật hoặc xóa theo chi phí mới. Số dư các ví luôn khớp |
| `FR-TXN-05` | Xóa giao dịch thu chi | T, D | Xóa thành công và phục hồi đúng số dư ví theo quy tắc đảo ngược; xóa kèm phí giao dịch liên kết |
| `FR-TRF-01` | Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn | T, D | Ghi nhận đồng thời: số tiền trừ ở ví nguồn bằng đúng số tiền cộng vào ví đích. Khi có chi phí, ví nguồn bị trừ thêm và tự tạo phí giao dịch liên kết; tổng số dư giảm đúng bằng chi phí |
| `FR-TRF-02` | Tra cứu và xóa giao dịch chuyển tiền nội bộ | T, D | Danh sách chuyển tiền xếp theo thời gian; xóa chuyển tiền thì hoàn tiền cho cả hai ví và xóa luôn phí liên quan |
| `FR-CAT-01` | Tạo cấu trúc danh mục 3 cấp | T, I | Cấu trúc danh mục 3 cấp (loại -> nhóm -> danh mục) đúng Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`) |
| `FR-CAT-02` | Tùy biến danh mục người dùng | T, D | Thêm danh mục con thành công; tên danh mục không trùng trong cùng một nhóm |
| `FR-CAT-03` | Kiểm soát toàn vẹn khi cập nhật và xóa danh mục | T, D | Ẩn thay vì xóa vật lý danh mục đã có giao dịch liên kết; cho phép chuyển danh mục sang nhóm khác cùng loại thu chi theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`) |
| `FR-REP-01` | Thống kê dòng tiền 12 tháng gần nhất | T, A | Lấy đúng 12 tháng liên tục gần nhất; tổng hợp đúng tổng thu nhập, tổng chi tiêu, dòng tiền thuần và tỷ lệ tiết kiệm. Ba mức bình quân chia cho 12, kèm ghi chú nêu các tháng chưa có giao dịch |
| `FR-REP-02` | Phân tích cơ cấu thu chi theo nhóm và danh mục | T, D | Tính đúng tổng tiền và tỷ lệ phần trăm chi tiêu của từng nhóm nhu cầu và danh mục |
| `FR-DAT-01` | Xuất dữ liệu tài chính | T, I | Xuất JSON đúng cấu trúc và giữ nguyên độ chính xác số học (`CON-02`) |
| `FR-DAT-02` | Phục hồi dữ liệu hệ thống từ bản sao lưu | T, D | Kiểm tra hợp lệ 4 bước trước khi nhập vào hệ thống; khôi phục đầy đủ trạng thái hệ thống |
| `FR-DAT-03` | Kiểm tra lại dữ liệu hệ thống | T, A | Thuật toán quét toàn bộ dữ liệu tiền; phát hiện sai lệch ở 4 điều kiện toàn vẹn nếu có |

#### 8.2.2 Ma trận phương pháp kiểm chứng yêu cầu phi chức năng (6 NFRs)

Bảng ma trận phương pháp kiểm chứng, môi trường thực thi và ngưỡng nghiệm thu đối với 6 yêu cầu phi chức năng của hệ thống:

| Mã yêu cầu | Tên yêu cầu | Đặc tính chất lượng | Phương pháp | Môi trường và cách thực hiện | Ngưỡng nghiệm thu |
| --- | --- | --- | :---: | --- | --- |
| `NFR-01` | Độ chính xác và toàn vẹn dữ liệu tài chính | Phù hợp chức năng / Tính chính xác chức năng | T, I | Kiểm thử tự động, phân tích tĩnh mã nguồn | Sai số tích lũy = 0.00 VNĐ; 0% kiểu số dấu phẩy động nhị phân; vi phạm Quy tắc bảo toàn số dư ví (`BR-FIN-02`) = 0.00% |
| `NFR-02` | Hiệu năng và độ trễ phản hồi | Hiệu quả vận hành / Hành vi thời gian và mức sử dụng tài nguyên | D, A | Trình diễn thao tác trực tiếp, quan sát xu hướng cấp phát bộ nhớ | Phản hồi dưới 1.0 giây với thao tác thông thường; không rò rỉ bộ nhớ |
| `NFR-03` | Độ tin cậy và lưu trữ an toàn | Độ tin cậy / Chịu lỗi và khả năng phục hồi | T, I | Kiểm thử giả lập lỗi, mô phỏng ngắt tiến trình đột ngột | Tỷ lệ hỏng tệp = 0.00%; ghi tệp an toàn không làm hỏng dữ liệu cũ; 100.00% tác vụ thay thế dữ liệu quy mô lớn đã có bản sao lưu trước đó (`CON-06`) |
| `NFR-04` | Khả năng bảo trì và kiến trúc hướng đối tượng | Bảo trì / Mô-đun hóa, tái sử dụng, dễ phân tích, dễ kiểm thử | T, I | Kiểm thử tự động theo từng quy tắc nghiệp vụ, đánh giá kiến trúc | 100% quy tắc nghiệp vụ có ca kiểm thử; 0 phụ thuộc vòng giữa các mô-đun |
| `NFR-05` | Tính khả dụng và trải nghiệm người dùng | Tương tác / Dễ học, dễ vận hành, bảo vệ người dùng khỏi lỗi | D, I | Kịch bản trải nghiệm người dùng, danh mục mã lỗi chuẩn hóa | 100% thông báo lỗi bằng tiếng Việt, rõ ràng, có gợi ý xử lý |
| `NFR-06` | Tính tương thích và môi trường chạy | Linh hoạt / Thích ứng, dễ cài đặt | T, I | Tích hợp liên tục đa nền tảng (Windows, macOS, Linux) | 100% pass kiểm thử trên cả 3 hệ điều hành; 100% tệp dùng UTF-8; 0 native library |
