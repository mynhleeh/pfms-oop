## 8. MA TRẬN KIỂM CHỨNG VÀ TRUY VẾT YÊU CẦU

Ma trận truy vết hai chiều và phương pháp kiểm chứng kỹ thuật thiết lập tính khép kín của toàn bộ hồ sơ yêu cầu theo chuẩn ISO/IEC/IEEE 29148:2018 (Điều khoản 9.5) và chuẩn thực hành IREB CPRE. Mạng lưới truy vết liên kết chặt chẽ từ 3 mục tiêu hệ thống (`G-01` đến `G-03`), 20 yêu cầu chức năng (`FR-WAL-01` đến `FR-DAT-03`), 20 ca sử dụng (`UC-WAL-01` đến `UC-DAT-03`), các thực thể dữ liệu tài chính cốt lõi đến 5 quy tắc nghiệp vụ (`BR-FIN-01` đến `BR-FIN-04` và `BR-NAM-01`). Mối liên kết hai chiều này chứng minh mọi mục tiêu đều được chuyển hóa thành chức năng cụ thể, loại trừ hoàn toàn các yêu cầu mồ côi (zero orphan requirements), đồng thời làm căn cứ kỹ thuật phục vụ nghiệm thu phần mềm và đánh giá tác động khi có thay đổi nghiệp vụ.

Phần này tổ chức thành hai nội dung chính gồm hệ thống ma trận truy vết (Mục 8.1) và ma trận phương pháp kiểm chứng (Mục 8.2). Quy trình kiểm chứng áp dụng bốn phương pháp kỹ thuật chuẩn mực theo ISO/IEC/IEEE 29148:2018:
1. Kiểm thử tự động (T - Test): Xác minh tính đúng đắn của chức năng và thuật toán bằng các ca kiểm thử tự động với dữ liệu đầu vào và kết quả kỳ vọng định lượng.
2. Trình diễn hoạt động (D - Demonstration): Xác minh việc thực thi chức năng thông qua thao tác trực tiếp trên giao diện tương tác người dùng theo kịch bản thực tế.
3. Soát xét kỹ thuật (I - Inspection): Xác minh bằng việc đối chiếu tài liệu thiết kế, rà soát cấu trúc mã nguồn tĩnh hoặc kiểm tra cấu trúc lưu trữ dữ liệu.
4. Phân tích định lượng (A - Analysis): Xác minh các chỉ số phi chức năng bằng công thức suy luận toán học, thống kê dữ liệu hoặc đo lường hiệu năng vận hành.

Bảng dưới đây tóm tắt cấu trúc năm ma trận đối chiếu và kiểm chứng trong chương này:

| STT | Mã mục | Tên ma trận | Chiều đối chiếu | Ý nghĩa kiểm chứng |
| :---: | :---: | --- | --- | --- |
| 1 | 8.1.1 | Ma trận mục tiêu hệ thống theo yêu cầu chức năng | `G-*` sang `FR-*` | Chứng minh mọi mục tiêu hệ thống đều có yêu cầu chức năng đáp ứng |
| 2 | 8.1.2 | Ma trận ca sử dụng theo yêu cầu chức năng | `FR-*` sang `UC-*` | Chứng minh mọi yêu cầu chức năng đều có kịch bản tương tác người dùng |
| 3 | 8.1.3 | Ma trận thực thể tác động theo yêu cầu chức năng | `FR-*` sang Thực thể dữ liệu | Xác định phạm vi ảnh hưởng dữ liệu của từng chức năng nghiệp vụ |
| 4 | 8.1.4 | Ma trận quy tắc nghiệp vụ theo yêu cầu chức năng | `BR-*` sang `FR-*` | Bảo đảm mọi quy tắc nghiệp vụ và ràng buộc tài chính đều được thực thi |
| 5 | 8.2 | Ma trận phương pháp kiểm chứng yêu cầu | `FR-*`, `NFR-*` sang T, D, I, A | Xác lập phương thức nghiệm thu kỹ thuật và môi trường thực hiện cụ thể |

### 8.1 Ma trận truy vết yêu cầu theo yêu cầu chức năng

Mục này tách ma trận truy vết thành bốn bảng, mỗi bảng một quan hệ. Các bảng lấy yêu cầu chức năng làm trục và xếp từ nguồn gốc đến ràng buộc: mục tiêu hệ thống, ca sử dụng, thực thể tác động, quy tắc nghiệp vụ.

Phạm vi đối chiếu gồm 3 mục tiêu từ Mục 2.2 (`G-01` đến `G-03`) và 20 yêu cầu chức năng từ Mục 4 (`FR-WAL-01` đến `FR-DAT-03`). Phạm vi còn gồm 20 ca sử dụng chi tiết từ Mục 6 (`UC-WAL-01` đến `UC-DAT-03`). Các mục này được chia theo 6 nhóm chức năng nghiệp vụ `WAL`, `TXN`, `TRF`, `CAT`, `REP`, `DAT`.

#### 8.1.1 Ma trận mục tiêu hệ thống theo yêu cầu chức năng

Bảng dưới đây cho biết mỗi yêu cầu chức năng ở Mục 4 phục vụ những mục tiêu hệ thống nào ở Mục 2.2. Ô đánh dấu `X` là yêu cầu chức năng ở dòng đó phục vụ mục tiêu ở cột đó.

| Yêu cầu chức năng | `G-01` (Ghi nhận dòng tiền, danh mục) | `G-02` (Tổng số dư, cơ cấu ví) | `G-03` (Chính xác số học) |
| --- | --- | --- | --- |
| `FR-WAL-01` (Tạo ví mới) | X | X |  |
| `FR-WAL-02` (Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền) |  | X |  |
| `FR-WAL-03` (Cập nhật thông tin ví) |  | X |  |
| `FR-WAL-04` (Thống kê cơ cấu số dư theo ví) |  | X |  |
| `FR-WAL-05` (Điều chỉnh số dư ví) |  | X |  |
| `FR-TXN-01` (Tạo mới giao dịch thu nhập) | X |  | X |
| `FR-TXN-02` (Tạo mới giao dịch chi tiêu) | X |  | X |
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

Bảng dưới đây cho biết mỗi yêu cầu chức năng ở Mục 4 được hiện thực hóa qua những ca sử dụng nào ở Mục 6. Mã ca sử dụng theo cú pháp `UC-<DOMAIN>-NN`.

| Mã yêu cầu | Tên yêu cầu | Ca sử dụng liên quan |
| --- | --- | --- |
| `FR-WAL-01` | Tạo ví mới | `UC-WAL-01`, `UC-WAL-03` |
| `FR-WAL-02` | Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền | `UC-WAL-01`, `UC-WAL-02` |
| `FR-WAL-03` | Cập nhật thông tin ví | `UC-WAL-01`, `UC-WAL-03` |
| `FR-WAL-04` | Thống kê cơ cấu số dư theo ví | `UC-WAL-01`, `UC-WAL-02` |
| `FR-WAL-05` | Điều chỉnh số dư ví | `UC-WAL-04` |
| `FR-TXN-01` | Tạo mới giao dịch thu nhập | `UC-TXN-01` |
| `FR-TXN-02` | Tạo mới giao dịch chi tiêu | `UC-TXN-01` |
| `FR-TXN-03` | Tra cứu và lọc lịch sử giao dịch thu chi | `UC-TXN-02` |
| `FR-TXN-04` | Chỉnh sửa giao dịch thu chi | `UC-TXN-03` |
| `FR-TXN-05` | Xóa giao dịch thu chi | `UC-TXN-04` |
| `FR-TRF-01` | Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn | `UC-TRF-01` |
| `FR-TRF-02` | Tra cứu và xóa giao dịch chuyển tiền nội bộ | `UC-TRF-02`, `UC-TRF-03` |
| `FR-CAT-01` | Tạo cấu trúc danh mục 3 cấp | `UC-CAT-01` |
| `FR-CAT-02` | Tùy biến danh mục người dùng | `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03` |
| `FR-CAT-03` | Kiểm soát toàn vẹn khi cập nhật và xóa danh mục | `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04` |
| `FR-REP-01` | Thống kê dòng tiền 12 tháng gần nhất | `UC-REP-01` |
| `FR-REP-02` | Phân tích cơ cấu thu chi theo nhóm và danh mục | `UC-REP-02` |
| `FR-DAT-01` | Xuất dữ liệu tài chính | `UC-DAT-01` |
| `FR-DAT-02` | Phục hồi dữ liệu hệ thống từ bản sao lưu | `UC-DAT-02` |
| `FR-DAT-03` | Kiểm tra lại dữ liệu hệ thống | `UC-DAT-03` |

#### 8.1.3 Ma trận thực thể tác động theo yêu cầu chức năng

Bảng dưới đây cho biết mỗi yêu cầu chức năng ở Mục 4 tác động lên những thực thể nào. Các thực thể gồm Ví, Giao dịch thu chi, Giao dịch chuyển tiền, Danh mục, Điều chỉnh số dư, Thống kê tháng và Dữ liệu tài chính.

| Mã yêu cầu | Tên yêu cầu | Thực thể tác động |
| --- | --- | --- |
| `FR-WAL-01` | Tạo ví mới | Ví |
| `FR-WAL-02` | Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền | Ví |
| `FR-WAL-03` | Cập nhật thông tin ví | Ví |
| `FR-WAL-04` | Thống kê cơ cấu số dư theo ví | Ví |
| `FR-WAL-05` | Điều chỉnh số dư ví | Ví, Điều chỉnh số dư |
| `FR-TXN-01` | Tạo mới giao dịch thu nhập | Giao dịch thu chi, Ví, Danh mục |
| `FR-TXN-02` | Tạo mới giao dịch chi tiêu | Giao dịch thu chi, Ví, Danh mục |
| `FR-TXN-03` | Tra cứu và lọc lịch sử giao dịch thu chi | Giao dịch thu chi, Ví, Danh mục |
| `FR-TXN-04` | Chỉnh sửa giao dịch thu chi | Giao dịch thu chi, Ví, Danh mục |
| `FR-TXN-05` | Xóa giao dịch thu chi | Giao dịch thu chi, Ví |
| `FR-TRF-01` | Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn | Giao dịch chuyển tiền, Giao dịch thu chi, Ví |
| `FR-TRF-02` | Tra cứu và xóa giao dịch chuyển tiền nội bộ | Giao dịch chuyển tiền, Giao dịch thu chi, Ví |
| `FR-CAT-01` | Tạo cấu trúc danh mục 3 cấp | Danh mục |
| `FR-CAT-02` | Tùy biến danh mục người dùng | Danh mục |
| `FR-CAT-03` | Kiểm soát toàn vẹn khi cập nhật và xóa danh mục | Danh mục, Giao dịch thu chi |
| `FR-REP-01` | Thống kê dòng tiền 12 tháng gần nhất | Thống kê tháng, Giao dịch thu chi |
| `FR-REP-02` | Phân tích cơ cấu thu chi theo nhóm và danh mục | Thống kê tháng, Danh mục, Giao dịch thu chi |
| `FR-DAT-01` | Xuất dữ liệu tài chính | Dữ liệu tài chính |
| `FR-DAT-02` | Phục hồi dữ liệu hệ thống từ bản sao lưu | Dữ liệu tài chính |
| `FR-DAT-03` | Kiểm tra lại dữ liệu hệ thống | Dữ liệu tài chính |

#### 8.1.4 Ma trận quy tắc nghiệp vụ theo yêu cầu chức năng

Bảng dưới đây tổng hợp từ các mục 4.x.y của Mục 4, cho biết mỗi quy tắc nghiệp vụ ở Mục 3 được áp dụng ở những yêu cầu chức năng nào. Trong cột cuối, mỗi dòng là một nhóm chức năng và các mã FR của cùng nhóm ngăn nhau bằng dấu phẩy.

| Mã quy tắc | Tên quy tắc | Yêu cầu chức năng áp dụng |
| --- | --- | --- |
| `BR-FIN-01` | Quy tắc độ chính xác tài chính | `FR-WAL-02`, `FR-WAL-04`, `FR-WAL-05`<br/>`FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`, `FR-TXN-04`<br/>`FR-TRF-01`<br/>`FR-REP-01`, `FR-REP-02`<br/>`FR-DAT-01`, `FR-DAT-03` |
| `BR-FIN-02` | Quy tắc bảo toàn số dư ví | `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-05`<br/>`FR-TXN-01`, `FR-TXN-02`, `FR-TXN-04`, `FR-TXN-05`<br/>`FR-TRF-01`, `FR-TRF-02`<br/>`FR-DAT-02`, `FR-DAT-03` |
| `BR-FIN-03` | Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ | `FR-TRF-01`, `FR-TRF-02`<br/>`FR-DAT-03` |
| `BR-FIN-04` | Quy tắc phân cấp danh mục thu chi 3 cấp | `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-04`<br/>`FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`<br/>`FR-REP-02`<br/>`FR-DAT-02`, `FR-DAT-03` |
| `BR-NAM-01` | Quy tắc tên của ví, nhóm nhu cầu và danh mục | `FR-WAL-01`, `FR-WAL-03`<br/>`FR-CAT-02`, `FR-CAT-03` |

### 8.2 Ma trận phương pháp kiểm chứng yêu cầu

Bốn loại hình kiểm chứng gồm:

| Ký hiệu | Loại hình | Tên tiếng Anh | Định nghĩa |
| --- | --- | --- | --- |
| I | Soát xét kỹ thuật | Inspection | Kiểm tra trực quan mã nguồn, tài liệu thiết kế, rà soát mã nguồn tĩnh mà không chạy chương trình. |
| T | Kiểm thử tự động | Test | Chạy chương trình hoặc các mô-đun với tập dữ liệu đầu vào xác định, đối chiếu tự động kết quả thực tế với kết quả kỳ vọng (Unit test, Integration test tự động). |
| D | Trình diễn hoạt động | Demonstration | Vận hành chức năng trực tiếp trên giao diện tương tác người dùng để quan sát hành vi, luồng xử lý và kết quả đầu ra theo kịch bản sử dụng thực tế. |
| A | Phân tích định lượng | Analysis | Đánh giá các chỉ số kỹ thuật bằng công cụ phân tích dữ liệu, tính toán thống kê, profiling hiệu năng và đo lường độ bao phủ mã nguồn. Công cụ áp dụng: công cụ phân tích thống kê, công cụ đo hiệu năng và độ bao phủ kiểm thử. |

#### 8.2.1 Ma trận phương pháp kiểm chứng yêu cầu chức năng (20 FRs)

| Mã yêu cầu | Tên yêu cầu | Phương pháp chính | Môi trường và phương thức thực hiện | Tiêu chí nghiệm thu |
| --- | --- | --- | --- | --- |
| `FR-WAL-01` | Tạo ví mới | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Tạo ví thành công với tên duy nhất và số dư 0.00; lần khởi tạo đầu tiên chỉ có một ví tiền mặt có sẵn |
| `FR-WAL-02` | Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Hiển thị chính xác danh sách ví và số dư tính lại từ lịch sử dòng tiền theo đúng công thức `BR-FIN-02` |
| `FR-WAL-03` | Cập nhật thông tin ví | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Cập nhật tên ví và trạng thái; cấm đổi trực tiếp số dư khả dụng; cấm lưu trữ ví tiền mặt có sẵn |
| `FR-WAL-04` | Thống kê cơ cấu số dư theo ví | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Tính toán chính xác tỷ lệ phần trăm số dư từng ví trên tổng số dư trong các ví |
| `FR-WAL-05` | Điều chỉnh số dư ví | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Khoản điều chỉnh số dư ghi đúng chênh lệch điều chỉnh, cho phép lý do tùy chọn (tối đa 255 ký tự), từ chối khi chênh lệch bằng 0 |
| `FR-TXN-01` | Tạo mới giao dịch thu nhập | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Số tiền > 0, danh mục có loại là thu nhập. Số dư ví nhận tăng đúng bằng số tiền thu trừ chi phí. Phí giao dịch liên kết được ghi nhận khi chi phí lớn hơn 0 |
| `FR-TXN-02` | Tạo mới giao dịch chi tiêu | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Số tiền > 0, danh mục có loại là chi tiêu, số dư ví chi đủ thanh toán số tiền cộng chi phí; trừ số dư ví chính xác kèm phí giao dịch liên kết |
| `FR-TXN-03` | Tra cứu và lọc lịch sử giao dịch thu chi | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Lọc chính xác theo khoảng ngày, ví, danh mục, từ khóa. Tính đúng tổng thu chi, phí giao dịch hiển thị thành dòng riêng kèm nhãn liên kết và được tính vào tổng chi |
| `FR-TXN-04` | Chỉnh sửa giao dịch thu chi | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Hoàn tác số tiền và chi phí cũ trên ví cũ, áp dụng số tiền và chi phí mới trên ví mới. Phí giao dịch liên kết được tạo, cập nhật hoặc xóa theo chi phí mới. Số dư các ví luôn khớp |
| `FR-TXN-05` | Xóa giao dịch thu chi | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Xóa giao dịch thành công và phục hồi số dư ví chính xác theo quy tắc đảo ngược, xóa kèm phí giao dịch liên kết |
| `FR-TRF-01` | Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Ghi nhận đồng thời: số tiền trừ ví nguồn bằng đúng số tiền cộng ví đích. Khi có chi phí, ví nguồn trừ thêm chi phí và tự động tạo phí giao dịch liên kết. Tổng số dư giảm đúng bằng chi phí |
| `FR-TRF-02` | Tra cứu và xóa giao dịch chuyển tiền nội bộ | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Hiển thị danh sách chuyển tiền theo thứ tự thời gian; xóa giao dịch chuyển tiền hoàn lại tiền cho cả hai ví và xóa hoàn toàn phí liên quan |
| `FR-CAT-01` | Tạo cấu trúc danh mục 3 cấp | T, I | Môi trường kiểm thử tự động / Soát xét mã nguồn | Cấu trúc danh mục 3 cấp (Loại -> Nhóm -> Danh mục) đúng quy tắc `BR-FIN-04` |
| `FR-CAT-02` | Tùy biến danh mục người dùng | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Thêm mới danh mục con thành công; tên danh mục không trùng trong cùng một nhóm |
| `FR-CAT-03` | Kiểm soát toàn vẹn khi cập nhật và xóa danh mục | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Ẩn thay vì xóa vật lý danh mục đang có giao dịch liên kết tới; hỗ trợ chuyển danh mục sang nhóm khác cùng loại thu chi theo quy tắc `BR-FIN-04` |
| `FR-REP-01` | Thống kê dòng tiền 12 tháng gần nhất | T, A | Môi trường kiểm thử tự động / Phân tích dữ liệu | Lấy đúng 12 tháng gần nhất liên tục; tổng hợp chính xác tổng thu nhập, tổng chi tiêu, dòng tiền thuần và tỷ lệ tiết kiệm. Ba mức bình quân chia cho 12 kèm ghi chú nêu các tháng chưa có giao dịch. |
| `FR-REP-02` | Phân tích cơ cấu thu chi theo nhóm và danh mục | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Tính đúng tổng số tiền và tỷ lệ phần trăm chi tiêu của từng nhóm nhu cầu và danh mục |
| `FR-DAT-01` | Xuất dữ liệu tài chính | T, I | Môi trường kiểm thử tự động / Soát xét mã nguồn | Xuất dữ liệu JSON đúng cấu trúc và giữ nguyên độ chính xác số học `CON-02`. |
| `FR-DAT-02` | Phục hồi dữ liệu hệ thống từ bản sao lưu | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Kiểm tra dữ liệu hợp lệ 4 bước trước khi nhập vào hệ thống; khôi phục hoàn toàn trạng thái hệ thống |
| `FR-DAT-03` | Kiểm tra lại dữ liệu hệ thống | T, A | Môi trường kiểm thử tự động / Phân tích dữ liệu | Thuật toán kiểm tra lại quét toàn bộ dữ liệu tiền trong hệ thống; phát hiện các sai lệch của 4 điều kiện toàn vẹn nếu có |

#### 8.2.2 Ma trận phương pháp kiểm chứng yêu cầu phi chức năng (6 NFRs)

| Mã yêu cầu | Tên yêu cầu | Đặc tính chất lượng | Phương pháp chính | Môi trường và phương thức thực hiện | Ngưỡng nghiệm thu |
| --- | --- | --- | --- | --- | --- |
| `NFR-01` | Độ chính xác và toàn vẹn dữ liệu tài chính | Mức độ phù hợp chức năng / Tính chính xác chức năng | T, I | Môi trường kiểm thử tự động, phân tích tĩnh mã nguồn | Sai số tích lũy = 0.00 VNĐ; 0% kiểu số dấu phẩy động nhị phân; vi phạm `BR-FIN-02` = 0.00% |
| `NFR-02` | Hiệu năng và độ trễ phản hồi | Hiệu quả vận hành / Hành vi thời gian & Mức sử dụng tài nguyên | D, A | Trình diễn thao tác trực tiếp, quan sát xu hướng cấp phát bộ nhớ | Phản hồi ngay với quy mô dữ liệu cá nhân thông thường; không phát hiện rò rỉ bộ nhớ |
| `NFR-03` | Độ tin cậy và lưu trữ an toàn | Độ tin cậy / Khả năng chịu lỗi & Khả năng phục hồi | T, I | Kiểm thử giả lập lỗi, mô phỏng ngắt tiến trình đột ngột | Tỷ lệ hỏng tệp = 0.00%; ghi tệp an toàn không làm hỏng dữ liệu cũ; 100.00% tác vụ thay thế dữ liệu quy mô lớn có bản sao lưu trước đó theo `CON-06` |
| `NFR-04` | Khả năng bảo trì và kiến trúc hướng đối tượng | Khả năng bảo trì / Tính module hóa, Tính tái sử dụng, Tính phân tích được & Tính kiểm thử được | T, I | Kiểm thử tự động cho từng quy tắc nghiệp vụ, đánh giá kiểm tra kiến trúc | 100% quy tắc nghiệp vụ có ca kiểm thử tương ứng; 0 phụ thuộc vòng giữa các mô-đun |
| `NFR-05` | Tính khả dụng và trải nghiệm người dùng | Khả năng tương tác / Tính dễ học, Tính dễ vận hành & Bảo vệ người dùng khỏi lỗi | D, I | Kịch bản trải nghiệm người dùng, danh mục mã lỗi chuẩn hóa | 100% thông báo lỗi tiếng Việt rõ ràng có gợi ý xử lý |
| `NFR-06` | Tính tương thích và môi trường chạy | Tính linh hoạt / Tính thích ứng & Tính dễ cài đặt | T, I | Hạ tầng tích hợp liên tục đa nền tảng (Win/macOS/Linux) | 100% pass kiểm thử trên cả 3 hệ điều hành; 100% tệp dùng UTF-8; 0 native library |
