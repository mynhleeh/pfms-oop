## 8. MA TRẬN KIỂM CHỨNG VÀ TRUY VẾT YÊU CẦU

Phần này lập các ma trận truy vết và phương pháp kiểm chứng yêu cầu để xác nhận toàn bộ các thực thể yêu cầu trong hệ thống đều đầy đủ, khép kín và kiểm chứng được độc lập.

Cấu trúc phần này gồm năm nội dung chính:

1. Ma trận mục tiêu hệ thống theo yêu cầu chức năng (Mục 8.1.1): Đối chiếu 5 mục tiêu `G-01` đến `G-05` ở Mục 2.2 với các yêu cầu chức năng phục vụ mục tiêu đó.
2. Ma trận ca sử dụng theo yêu cầu chức năng (Mục 8.1.2): Đối chiếu mỗi yêu cầu chức năng ở Mục 4 với các ca sử dụng hiện thực hóa yêu cầu đó ở Mục 6.
3. Ma trận thực thể miền nghiệp vụ theo yêu cầu chức năng (Mục 8.1.3): Đối chiếu mỗi yêu cầu chức năng với các thực thể miền nghiệp vụ mà yêu cầu đó tác động.
4. Ma trận quy tắc nghiệp vụ theo yêu cầu chức năng (Mục 8.1.4): Đối chiếu mỗi quy tắc nghiệp vụ `BR-FIN-01` đến `BR-FIN-10` ở Mục 3 với các yêu cầu chức năng áp dụng quy tắc đó.
5. Ma trận phương pháp kiểm chứng yêu cầu (Mục 8.2): Xác định phương thức và công cụ nghiệm thu kỹ thuật cho toàn bộ yêu cầu chức năng và phi chức năng.

---

### 8.1 Ma trận truy vết yêu cầu theo yêu cầu chức năng

Mục này tách ma trận truy vết thành bốn bảng, mỗi bảng một quan hệ, lấy yêu cầu chức năng làm trục và xếp từ nguồn gốc đến ràng buộc: mục tiêu hệ thống, ca sử dụng, thực thể miền nghiệp vụ, quy tắc nghiệp vụ.

Phạm vi đối chiếu gồm 5 mục tiêu từ Mục 2.2 (`G-01` đến `G-05`), 43 yêu cầu chức năng từ Mục 4 (`FR-WAL-01` đến `FR-DAT-03`) và 44 ca sử dụng chi tiết từ Mục 6 (`UC-WAL-01` đến `UC-DAT-03`), chia theo 10 nhóm chức năng nghiệp vụ `WAL`, `TXN`, `TRF`, `CAT`, `DEBT`, `SAV`, `INV`, `FND`, `REP`, `DAT`.

#### 8.1.1 Ma trận mục tiêu hệ thống theo yêu cầu chức năng

Bảng dưới đây cho biết mỗi yêu cầu chức năng ở Mục 4 phục vụ những mục tiêu hệ thống nào ở Mục 2.2. Ô đánh dấu `X` là yêu cầu chức năng ở dòng đó phục vụ mục tiêu ở cột đó.

| Yêu cầu chức năng | G-01 (Ghi nhận dòng tiền, danh mục) | G-02 (Tài sản ròng, cơ cấu) | G-03 (Công nợ, tích lũy, đầu tư) | G-04 (Quỹ mục tiêu) | G-05 (Chính xác số học) |
| --- | --- | --- | --- | --- | --- |
| FR-WAL-01 (Tạo ví tiền mới) | X | X |  |  |  |
| FR-WAL-02 (Xem danh sách và chi tiết số dư tính lại từ lịch sử giao dịch) |  | X |  |  |  |
| FR-WAL-03 (Cập nhật thông tin ví tiền) |  | X |  |  |  |
| FR-WAL-04 (Thống kê cơ cấu tài sản theo ví) |  | X |  |  |  |
| FR-WAL-05 (Điều chỉnh số dư ví) |  | X |  |  |  |
| FR-TXN-01 (Tạo mới giao dịch thu nhập) | X |  |  |  | X |
| FR-TXN-02 (Tạo mới giao dịch chi tiêu) | X |  |  |  | X |
| FR-TXN-03 (Tra cứu và lọc lịch sử giao dịch) | X |  |  |  |  |
| FR-TXN-04 (Chỉnh sửa giao dịch thu chi) | X |  |  |  | X |
| FR-TXN-05 (Xóa giao dịch thu chi) | X |  |  |  | X |
| FR-TRF-01 (Chuyển tiền giữa hai ví nội bộ) | X |  |  |  | X |
| FR-TRF-02 (Ghi nhận phí chuyển tiền) | X |  |  |  | X |
| FR-TRF-03 (Tra cứu và hủy giao dịch chuyển tiền nội bộ) | X |  |  |  | X |
| FR-CAT-01 (Tạo cấu trúc phân loại 3 cấp) | X |  |  |  |  |
| FR-CAT-02 (Tùy biến danh mục người dùng) | X |  |  |  |  |
| FR-CAT-03 (Kiểm soát toàn vẹn khi cập nhật và xóa danh mục) | X |  |  |  |  |
| FR-DEBT-01 (Ghi nhận khoản cho vay) |  | X | X |  |  |
| FR-DEBT-02 (Ghi nhận khoản đi vay) |  | X | X |  |  |
| FR-DEBT-03 (Thu lại nợ cho vay) |  | X | X |  |  |
| FR-DEBT-04 (Trả nợ đi vay) |  | X | X |  |  |
| FR-DEBT-05 (Xóa nợ khó đòi và miễn trừ nợ) |  | X | X |  |  |
| FR-SAV-01 (Mở sổ tiết kiệm mới) |  | X | X |  |  |
| FR-SAV-02 (Theo dõi kỳ hạn và tiền lãi dự kiến sổ tiết kiệm) |  |  | X |  | X |
| FR-SAV-03 (Tất toán sổ tiết kiệm đúng hạn) |  | X | X |  | X |
| FR-SAV-04 (Tất toán sổ tiết kiệm trước hạn) |  | X | X |  | X |
| FR-SAV-05 (Tái tục sổ tiết kiệm với điều khoản mới) |  |  | X |  | X |
| FR-INV-01 (Ghi nhận lệnh mua tài sản đầu tư) |  | X | X |  |  |
| FR-INV-02 (Cập nhật đơn giá hoặc giá trị thị trường) |  | X | X |  |  |
| FR-INV-03 (Xem giá trị thị trường, lãi lỗ và tỷ suất sinh lời) |  | X | X |  | X |
| FR-INV-04 (Ghi nhận lệnh bán tài sản đầu tư) |  | X | X |  | X |
| FR-INV-05 (Ghi nhận sự kiện của tài sản đầu tư) |  | X | X |  | X |
| FR-INV-06 (Quản lý khoản đầu tư) |  | X | X |  |  |
| FR-FND-01 (Tạo quỹ mục tiêu mới) |  | X |  | X |  |
| FR-FND-02 (Gắn tài sản vào quỹ mục tiêu) |  | X |  | X |  |
| FR-FND-03 (Kiểm soát tiến độ và tổng hợp tài sản quỹ mục tiêu) |  | X |  | X |  |
| FR-FND-04 (Điều chỉnh, chuyển tài sản hoặc xóa quỹ mục tiêu) |  | X |  | X |  |
| FR-REP-01 (Thống kê dòng tiền 12 tháng gần nhất) | X |  |  |  | X |
| FR-REP-02 (Phân tích cơ cấu thu chi theo nhóm và danh mục) | X |  |  |  |  |
| FR-REP-03 (Tổng hợp tài sản và định giá tài sản ròng) |  | X |  |  | X |
| FR-REP-04 (Lập sao kê công nợ chi tiết theo người vay và người cho vay) |  |  | X |  |  |
| FR-DAT-01 (Xuất dữ liệu tài chính) |  | X |  |  | X |
| FR-DAT-02 (Phục hồi dữ liệu hệ thống từ bản sao lưu) |  | X |  |  | X |
| FR-DAT-03 (Kiểm tra lại dữ liệu hệ thống) |  |  |  |  | X |

#### 8.1.2 Ma trận ca sử dụng theo yêu cầu chức năng

Bảng dưới đây cho biết mỗi yêu cầu chức năng ở Mục 4 được hiện thực hóa qua những ca sử dụng nào ở Mục 6. Mã ca sử dụng theo cú pháp `UC-<DOMAIN>-NN`.

| Mã yêu cầu | Tên chức năng | Ca sử dụng liên quan |
| --- | --- | --- |
| FR-WAL-01 | Tạo ví tiền mới | UC-WAL-01, UC-WAL-03 |
| FR-WAL-02 | Xem danh sách và chi tiết số dư tính lại từ lịch sử giao dịch | UC-WAL-01, UC-WAL-02 |
| FR-WAL-03 | Cập nhật thông tin ví tiền | UC-WAL-01, UC-WAL-03 |
| FR-WAL-04 | Thống kê cơ cấu tài sản theo ví | UC-WAL-01, UC-WAL-02 |
| FR-WAL-05 | Điều chỉnh số dư ví | UC-WAL-04 |
| FR-TXN-01 | Tạo mới giao dịch thu nhập | UC-TXN-01 |
| FR-TXN-02 | Tạo mới giao dịch chi tiêu | UC-TXN-01 |
| FR-TXN-03 | Tra cứu và lọc lịch sử giao dịch | UC-TXN-02 |
| FR-TXN-04 | Chỉnh sửa giao dịch thu chi | UC-TXN-03 |
| FR-TXN-05 | Xóa giao dịch thu chi | UC-TXN-04 |
| FR-TRF-01 | Chuyển tiền giữa hai ví nội bộ | UC-TRF-01 |
| FR-TRF-02 | Ghi nhận phí chuyển tiền | UC-TRF-01, UC-TRF-02 |
| FR-TRF-03 | Tra cứu và hủy giao dịch chuyển tiền nội bộ | UC-TRF-02, UC-TRF-03 |
| FR-CAT-01 | Tạo cấu trúc phân loại 3 cấp | UC-CAT-01 |
| FR-CAT-02 | Tùy biến danh mục người dùng | UC-CAT-01, UC-CAT-02, UC-CAT-03 |
| FR-CAT-03 | Kiểm soát toàn vẹn khi cập nhật và xóa danh mục | UC-CAT-02, UC-CAT-03, UC-CAT-04 |
| FR-DEBT-01 | Ghi nhận khoản cho vay | UC-DEBT-01 |
| FR-DEBT-02 | Ghi nhận khoản đi vay | UC-DEBT-02 |
| FR-DEBT-03 | Thu lại nợ cho vay | UC-DEBT-01 |
| FR-DEBT-04 | Trả nợ đi vay | UC-DEBT-02 |
| FR-DEBT-05 | Xóa nợ khó đòi và miễn trừ nợ | UC-DEBT-03 |
| FR-SAV-01 | Mở sổ tiết kiệm mới | UC-SAV-01, UC-SAV-04 |
| FR-SAV-02 | Theo dõi kỳ hạn và tiền lãi dự kiến sổ tiết kiệm | UC-SAV-04, UC-SAV-05 |
| FR-SAV-03 | Tất toán sổ tiết kiệm đúng hạn | UC-SAV-02 |
| FR-SAV-04 | Tất toán sổ tiết kiệm trước hạn | UC-SAV-03 |
| FR-SAV-05 | Tái tục sổ tiết kiệm với điều khoản mới | UC-SAV-06 |
| FR-INV-01 | Ghi nhận lệnh mua tài sản đầu tư | UC-INV-01, UC-INV-02 |
| FR-INV-02 | Cập nhật đơn giá hoặc giá trị thị trường | UC-INV-01, UC-INV-03 |
| FR-INV-03 | Xem giá trị thị trường, lãi lỗ và tỷ suất sinh lời | UC-INV-04, UC-INV-06, UC-INV-07, UC-INV-08 |
| FR-INV-04 | Ghi nhận lệnh bán tài sản đầu tư | UC-INV-02 |
| FR-INV-05 | Ghi nhận sự kiện của tài sản đầu tư | UC-INV-05, UC-REP-01 |
| FR-INV-06 | Quản lý khoản đầu tư | UC-INV-06, UC-INV-07, UC-INV-08 |
| FR-FND-01 | Tạo quỹ mục tiêu mới | UC-FND-01 |
| FR-FND-02 | Gắn tài sản vào quỹ mục tiêu | UC-FND-01 |
| FR-FND-03 | Kiểm soát tiến độ và tổng hợp tài sản quỹ mục tiêu | UC-FND-02 |
| FR-FND-04 | Điều chỉnh, chuyển tài sản hoặc xóa quỹ mục tiêu | UC-FND-03, UC-FND-04, UC-FND-05 |
| FR-REP-01 | Thống kê dòng tiền 12 tháng gần nhất | UC-REP-01, UC-INV-05 |
| FR-REP-02 | Phân tích cơ cấu thu chi theo nhóm và danh mục | UC-REP-02 |
| FR-REP-03 | Tổng hợp tài sản và định giá tài sản ròng | UC-REP-03 |
| FR-REP-04 | Lập sao kê công nợ chi tiết theo người vay và người cho vay | UC-REP-04 |
| FR-DAT-01 | Xuất dữ liệu tài chính | UC-DAT-01 |
| FR-DAT-02 | Phục hồi dữ liệu hệ thống từ bản sao lưu | UC-DAT-02 |
| FR-DAT-03 | Kiểm tra lại dữ liệu hệ thống | UC-DAT-03 |

#### 8.1.3 Ma trận thực thể miền nghiệp vụ theo yêu cầu chức năng

Bảng dưới đây cho biết mỗi yêu cầu chức năng ở Mục 4 tác động lên những thực thể miền nghiệp vụ nào. Các thực thể gồm Ví tiền, Giao dịch, Chuyển tiền nội bộ, Danh mục, Công nợ, Sổ tiết kiệm, Khoản đầu tư, Tài sản đầu tư, Lệnh mua của tài sản đầu tư, Quỹ mục tiêu, Thống kê tháng và Dữ liệu tài chính.

| Mã yêu cầu | Tên chức năng | Thực thể miền nghiệp vụ |
| --- | --- | --- |
| FR-WAL-01 | Tạo ví tiền mới | Ví tiền |
| FR-WAL-02 | Xem danh sách và chi tiết số dư tính lại từ lịch sử giao dịch | Ví tiền |
| FR-WAL-03 | Cập nhật thông tin ví tiền | Ví tiền |
| FR-WAL-04 | Thống kê cơ cấu tài sản theo ví | Ví tiền |
| FR-WAL-05 | Điều chỉnh số dư ví | Ví tiền |
| FR-TXN-01 | Tạo mới giao dịch thu nhập | Giao dịch, Ví tiền, Danh mục |
| FR-TXN-02 | Tạo mới giao dịch chi tiêu | Giao dịch, Ví tiền, Danh mục |
| FR-TXN-03 | Tra cứu và lọc lịch sử giao dịch | Giao dịch, Ví tiền, Danh mục |
| FR-TXN-04 | Chỉnh sửa giao dịch thu chi | Giao dịch, Ví tiền, Danh mục |
| FR-TXN-05 | Xóa giao dịch thu chi | Giao dịch, Ví tiền |
| FR-TRF-01 | Chuyển tiền giữa hai ví nội bộ | Chuyển tiền nội bộ, Ví tiền |
| FR-TRF-02 | Ghi nhận phí chuyển tiền | Chuyển tiền nội bộ, Giao dịch, Ví tiền |
| FR-TRF-03 | Tra cứu và hủy giao dịch chuyển tiền nội bộ | Chuyển tiền nội bộ, Ví tiền |
| FR-CAT-01 | Tạo cấu trúc phân loại 3 cấp | Danh mục |
| FR-CAT-02 | Tùy biến danh mục người dùng | Danh mục |
| FR-CAT-03 | Kiểm soát toàn vẹn khi cập nhật và xóa danh mục | Danh mục, Giao dịch |
| FR-DEBT-01 | Ghi nhận khoản cho vay | Công nợ, Ví tiền, Giao dịch |
| FR-DEBT-02 | Ghi nhận khoản đi vay | Công nợ, Ví tiền, Giao dịch |
| FR-DEBT-03 | Thu lại nợ cho vay | Công nợ, Ví tiền, Giao dịch |
| FR-DEBT-04 | Trả nợ đi vay | Công nợ, Ví tiền, Giao dịch |
| FR-DEBT-05 | Xóa nợ khó đòi và miễn trừ nợ | Công nợ |
| FR-SAV-01 | Mở sổ tiết kiệm mới | Sổ tiết kiệm, Ví tiền, Giao dịch |
| FR-SAV-02 | Theo dõi kỳ hạn và tiền lãi dự kiến sổ tiết kiệm | Sổ tiết kiệm |
| FR-SAV-03 | Tất toán sổ tiết kiệm đúng hạn | Sổ tiết kiệm, Ví tiền, Giao dịch |
| FR-SAV-04 | Tất toán sổ tiết kiệm trước hạn | Sổ tiết kiệm, Ví tiền, Giao dịch |
| FR-SAV-05 | Tái tục sổ tiết kiệm với điều khoản mới | Sổ tiết kiệm, Ví tiền |
| FR-INV-01 | Ghi nhận lệnh mua tài sản đầu tư | Tài sản đầu tư, Ví tiền, Giao dịch |
| FR-INV-02 | Cập nhật đơn giá hoặc giá trị thị trường | Tài sản đầu tư |
| FR-INV-03 | Xem giá trị thị trường, lãi lỗ và tỷ suất sinh lời | Tài sản đầu tư |
| FR-INV-04 | Ghi nhận lệnh bán tài sản đầu tư | Tài sản đầu tư, Ví tiền, Giao dịch |
| FR-INV-05 | Ghi nhận sự kiện của tài sản đầu tư | Tài sản đầu tư, Ví tiền, Giao dịch, Thống kê tháng |
| FR-INV-06 | Quản lý khoản đầu tư | Khoản đầu tư, Tài sản đầu tư |
| FR-FND-01 | Tạo quỹ mục tiêu mới | Quỹ mục tiêu |
| FR-FND-02 | Gắn tài sản vào quỹ mục tiêu | Quỹ mục tiêu, Ví tiền, Sổ tiết kiệm, Lệnh mua của tài sản đầu tư |
| FR-FND-03 | Kiểm soát tiến độ và tổng hợp tài sản quỹ mục tiêu | Quỹ mục tiêu |
| FR-FND-04 | Điều chỉnh, chuyển tài sản hoặc xóa quỹ mục tiêu | Quỹ mục tiêu |
| FR-REP-01 | Thống kê dòng tiền 12 tháng gần nhất | Thống kê tháng, Giao dịch, Tài sản đầu tư |
| FR-REP-02 | Phân tích cơ cấu thu chi theo nhóm và danh mục | Thống kê tháng, Danh mục, Giao dịch |
| FR-REP-03 | Tổng hợp tài sản và định giá tài sản ròng | Dữ liệu tài chính, Ví tiền, Sổ tiết kiệm, Tài sản đầu tư, Công nợ |
| FR-REP-04 | Lập sao kê công nợ chi tiết theo người vay và người cho vay | Công nợ |
| FR-DAT-01 | Xuất dữ liệu tài chính | Dữ liệu tài chính |
| FR-DAT-02 | Phục hồi dữ liệu hệ thống từ bản sao lưu | Dữ liệu tài chính |
| FR-DAT-03 | Kiểm tra lại dữ liệu hệ thống | Dữ liệu tài chính |

#### 8.1.4 Ma trận quy tắc nghiệp vụ theo yêu cầu chức năng

Bảng dưới đây tổng hợp từ các mục 4.x.y của Mục 4, cho biết mỗi quy tắc nghiệp vụ ở Mục 3 được áp dụng ở những yêu cầu chức năng nào. Trong cột cuối, mỗi dòng là một nhóm chức năng và các mã FR của cùng nhóm ngăn nhau bằng dấu phẩy.

| Mã quy tắc | Tên quy tắc | Yêu cầu chức năng áp dụng |
| --- | --- | --- |
| BR-FIN-01 | Quy tắc độ chính xác tài chính | FR-WAL-01, FR-WAL-02, FR-WAL-04, FR-WAL-05<br/>FR-TXN-01, FR-TXN-02, FR-TXN-03<br/>FR-TRF-01<br/>FR-DEBT-01, FR-DEBT-02, FR-DEBT-03, FR-DEBT-04, FR-DEBT-05<br/>FR-SAV-01<br/>FR-INV-01, FR-INV-02, FR-INV-04, FR-INV-05<br/>FR-FND-01, FR-FND-03<br/>FR-REP-01, FR-REP-02, FR-REP-03<br/>FR-DAT-01, FR-DAT-03 |
| BR-FIN-02 | Quy tắc bảo toàn số dư ví | FR-WAL-01, FR-WAL-02, FR-WAL-03, FR-WAL-05<br/>FR-TXN-01, FR-TXN-02, FR-TXN-04, FR-TXN-05<br/>FR-TRF-01<br/>FR-DEBT-01, FR-DEBT-02, FR-DEBT-03, FR-DEBT-04, FR-DEBT-05<br/>FR-SAV-01, FR-SAV-03, FR-SAV-04, FR-SAV-05<br/>FR-INV-01, FR-INV-04, FR-INV-05<br/>FR-FND-02<br/>FR-REP-03<br/>FR-DAT-02, FR-DAT-03 |
| BR-FIN-03 | Quy tắc ghi hai vế khi chuyển tiền nội bộ | FR-TRF-01, FR-TRF-02, FR-TRF-03<br/>FR-DAT-03 |
| BR-FIN-04 | Quy tắc phân cấp danh mục thu chi 3 cấp | FR-TXN-01, FR-TXN-02, FR-TXN-04<br/>FR-CAT-01, FR-CAT-02, FR-CAT-03<br/>FR-REP-02<br/>FR-DAT-02, FR-DAT-03 |
| BR-FIN-05 | Quy tắc tính lãi tiết kiệm có kỳ hạn | FR-WAL-03<br/>FR-SAV-01, FR-SAV-02, FR-SAV-03, FR-SAV-04, FR-SAV-05<br/>FR-FND-03<br/>FR-REP-03 |
| BR-FIN-06 | Quy tắc định giá và giá vốn tài sản đầu tư | FR-INV-01, FR-INV-02, FR-INV-03, FR-INV-04, FR-INV-05, FR-INV-06<br/>FR-REP-03<br/>FR-DAT-03 |
| BR-FIN-07 | Quy tắc sự kiện của tài sản đầu tư | FR-INV-05<br/>FR-REP-01 |
| BR-FIN-08 | Quy tắc tỷ suất sinh lời của tài sản đầu tư | FR-INV-03, FR-INV-06 |
| BR-FIN-09 | Quy tắc theo dõi công nợ | FR-WAL-03<br/>FR-DEBT-01, FR-DEBT-02, FR-DEBT-03, FR-DEBT-04, FR-DEBT-05<br/>FR-REP-03, FR-REP-04<br/>FR-DAT-03 |
| BR-FIN-10 | Quy tắc gắn tài sản vào quỹ mục tiêu | FR-SAV-03, FR-SAV-04<br/>FR-INV-01<br/>FR-FND-01, FR-FND-02, FR-FND-03, FR-FND-04 |

---

### 8.2 Ma trận phương pháp kiểm chứng yêu cầu

Bốn loại hình kiểm chứng gồm:

| Ký hiệu | Phương pháp kiểm chứng | Tên tiếng Anh | Định nghĩa kỹ thuật |
| --- | --- | --- | --- |
| I | Soát xét kỹ thuật | Inspection | Kiểm tra trực quan mã nguồn, tài liệu thiết kế, rà soát mã nguồn tĩnh mà không chạy chương trình. |
| T | Kiểm thử tự động | Test | Chạy chương trình hoặc các mô-đun với tập dữ liệu đầu vào xác định, đối chiếu tự động kết quả thực tế với kết quả kỳ vọng (Unit test, Integration test tự động). |
| D | Trình diễn hoạt động | Demonstration | Vận hành chức năng trực tiếp trên giao diện tương tác người dùng để quan sát hành vi, luồng xử lý và kết quả đầu ra theo kịch bản sử dụng thực tế. |
| A | Phân tích định lượng | Analysis | Đánh giá các chỉ số kỹ thuật bằng công cụ phân tích dữ liệu, tính toán thống kê, profiling hiệu năng và đo lường độ bao phủ mã nguồn. Công cụ áp dụng: công cụ phân tích thống kê, công cụ đo hiệu năng và độ bao phủ kiểm thử. |

#### 8.2.1 Ma trận phương pháp kiểm chứng yêu cầu chức năng (43 FRs)

| Mã yêu cầu | Tên chức năng | Phương pháp chính | Môi trường và phương thức thực hiện | Tiêu chí nghiệm thu kỹ thuật |
| --- | --- | --- | --- | --- |
| FR-WAL-01 | Tạo ví tiền mới | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Tạo ví thành công với tên duy nhất, loại ví hợp lệ và số dư ban đầu không âm; lần khởi tạo đầu tiên chỉ có một ví tiền mặt có sẵn |
| FR-WAL-02 | Xem danh sách và chi tiết số dư tính lại từ lịch sử giao dịch | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Hiển thị chính xác danh sách ví và số dư tính lại từ lịch sử giao dịch theo đúng công thức BR-FIN-02 |
| FR-WAL-03 | Cập nhật thông tin ví tiền | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Cập nhật tên ví và trạng thái; cấm đổi trực tiếp số dư khả dụng; cấm lưu trữ ví tiền mặt có sẵn |
| FR-WAL-04 | Thống kê cơ cấu tài sản theo ví | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Tính toán chính xác tỷ lệ phần trăm số dư từng ví trên tổng tiền trong các ví |
| FR-WAL-05 | Điều chỉnh số dư ví | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Khoản điều chỉnh số dư ghi đúng chênh lệch $Adjustment_w$, bắt buộc lý do, từ chối khi chênh lệch bằng 0 |
| FR-TXN-01 | Tạo mới giao dịch thu nhập | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Số tiền > 0, danh mục có loại là thu nhập; số dư ví nhận tăng đúng bằng số tiền thu; khoản thu từ tài sản đầu tư không ghi bằng giao dịch thu nhập mà qua sự kiện Nhận tiền mặt (FR-INV-05) |
| FR-TXN-02 | Tạo mới giao dịch chi tiêu | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Số tiền > 0, danh mục có loại là chi tiêu, số dư ví chi đủ thanh toán; trừ số dư ví chính xác |
| FR-TXN-03 | Tra cứu và lọc lịch sử giao dịch | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Lọc chính xác theo khoảng ngày, ví, danh mục, từ khóa; tính đúng tổng thu chi |
| FR-TXN-04 | Chỉnh sửa giao dịch thu chi | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Hoàn tác số tiền cũ trên ví cũ, áp dụng số tiền mới trên ví mới; số dư các ví luôn khớp |
| FR-TXN-05 | Xóa giao dịch thu chi | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Xóa giao dịch thành công và phục hồi số dư ví chính xác theo quy tắc đảo ngược |
| FR-TRF-01 | Chuyển tiền giữa hai ví nội bộ | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Ghi hai vế: số tiền trừ ví nguồn bằng đúng số tiền cộng ví đích; tổng tiền luôn không đổi |
| FR-TRF-02 | Ghi nhận phí chuyển tiền | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Ví nguồn bị trừ thêm khoản phí; tạo giao dịch chi phí chuyển khoản tự động |
| FR-TRF-03 | Tra cứu và hủy giao dịch chuyển tiền nội bộ | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Hủy chuyển tiền hoàn lại tiền cho cả hai ví và xóa phí liên quan |
| FR-CAT-01 | Tạo cấu trúc phân loại 3 cấp | T, I | Môi trường kiểm thử tự động / Soát xét mã nguồn | Cây danh mục 3 cấp (Loại -> Nhóm -> Danh mục) đúng chuẩn phân cấp |
| FR-CAT-02 | Tùy biến danh mục người dùng | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Thêm mới danh mục con thành công; tên danh mục không trùng trong cùng một nhóm |
| FR-CAT-03 | Kiểm soát toàn vẹn khi cập nhật và xóa danh mục | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Ẩn thay vì xóa vật lý danh mục đang có giao dịch liên kết tới; hỗ trợ chuyển danh mục sang nhóm khác an toàn |
| FR-DEBT-01 | Ghi nhận khoản cho vay | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Trừ tiền ví cho vay; ghi nhận số tiền còn phải thu của người vay; cập nhật sổ nợ |
| FR-DEBT-02 | Ghi nhận khoản đi vay | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Cộng tiền vào ví nhận; ghi nhận nợ phải trả cho người cho vay |
| FR-DEBT-03 | Thu lại nợ cho vay | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Cộng tiền vào ví; giảm số tiền còn phải thu; chuyển trạng thái PAID khi hết nợ |
| FR-DEBT-04 | Trả nợ đi vay | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Trừ tiền ví chi trả; giảm nợ phải trả; kiểm tra số tiền trả không vượt nợ |
| FR-DEBT-05 | Xóa nợ khó đòi và miễn trừ nợ | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Giảm số tiền còn phải thu hoặc còn phải trả đúng bằng số tiền xóa, không vượt quá số còn nợ; chuyển trạng thái PAID khi hết nợ (TC-DEBT-05.1) hoặc giữ ACTIVE khi xóa một phần (TC-DEBT-05.4); bắt buộc có lý do; số dư các ví tiền giữ nguyên không đổi |
| FR-SAV-01 | Mở sổ tiết kiệm mới | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Trừ tiền ví nguồn gửi; ghi nhận sổ tiết kiệm với gốc, kỳ hạn, lãi suất và ngày đáo hạn |
| FR-SAV-02 | Theo dõi kỳ hạn và tiền lãi dự kiến sổ tiết kiệm | T, A | Môi trường kiểm thử tự động / Phân tích dữ liệu | Tính đúng tiền lãi dự kiến theo công thức BR-FIN-05; phân loại trạng thái đáo hạn |
| FR-SAV-03 | Tất toán sổ tiết kiệm đúng hạn | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Cộng cả gốc và đủ lãi có kỳ hạn vào ví nhận (riêng phương thức `PREPAID`, chỉ chuyển vốn gốc $Payout = Principal$ do lãi đã chi trả đầu kỳ theo QD-240); chuyển trạng thái sổ sang `SETTLED_MATURED` |
| FR-SAV-04 | Tất toán sổ tiết kiệm trước hạn | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Cộng gốc và lãi đơn tính theo số ngày thực gửi nhân hệ số phạt tất toán sớm; chỉ chuyển tiền khi người dùng xác nhận |
| FR-SAV-05 | Tái tục sổ tiết kiệm với điều khoản mới | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Chỉ tái tục sổ đã `SETTLED_MATURED`; vốn gốc tái tục không vượt giá trị tất toán; sổ mới tính lãi độc lập theo điều khoản mới |
| FR-INV-01 | Ghi nhận lệnh mua tài sản đầu tư | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Trừ tiền ví mua gồm cả chi phí mua. Xác định chính xác đại lượng còn lại trong đơn giá, số lượng và số tiền mua. Sửa lệnh mua điều chỉnh ví theo chênh lệch giá gốc. Ghi Thời điểm ghi và số thứ tự ghi do hệ thống tự ghi. |
| FR-INV-02 | Cập nhật đơn giá hoặc giá trị thị trường | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Lưu lịch sử giá theo ngày với giá trị người dùng đã nhập (đơn giá hiện hành hoặc giá trị thị trường tổng); cập nhật giá trị thị trường của các lệnh mua của tài sản đầu tư |
| FR-INV-03 | Xem giá trị thị trường, lãi lỗ và tỷ suất sinh lời | T, A | Môi trường kiểm thử tự động / Phân tích dữ liệu | Tính đúng giá trị hiện tại theo đơn giá hiện hành mới nhất, tỷ suất sinh lời và lãi lỗ chưa chốt |
| FR-INV-04 | Ghi nhận lệnh bán tài sản đầu tư | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Khấu trừ đơn vị và giá vốn theo nhập trước xuất trước. Cộng tiền thực nhận sau chi phí bán vào ví. Tính lại có dấu vết khi ghi lùi ngày theo ngày bán, rồi số thứ tự ghi. |
| FR-INV-05 | Ghi nhận sự kiện của tài sản đầu tư | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Phân bổ thu nhập, đơn vị thưởng hoặc hoàn vốn đúng tỷ lệ đơn vị nắm giữ ngay trước sự kiện. Nhận tiền mặt tăng số dư ví và thu nhập tháng, Hoàn vốn tăng số dư ví và không đổi thu nhập tháng. Thứ tự trong cùng ngày theo số thứ tự ghi. Cập nhật giá vốn và lãi lỗ đã chốt, tính lại khi ghi lùi ngày có lưu dấu vết. |
| FR-INV-06 | Quản lý khoản đầu tư | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Tạo, sửa, xóa khoản đầu tư thành công; tên 1-50 ký tự; độc quyền gán lệnh mua theo BR-FIN-06; tỷ suất của khoản theo BR-FIN-08; gán/bỏ gán nguyên tử; bảo toàn các giao dịch cơ sở |
| FR-FND-01 | Tạo quỹ mục tiêu mới | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Tạo mục tiêu với số tiền mục tiêu không âm; mã quỹ và tên quỹ không trùng; trạng thái IN_PROGRESS |
| FR-FND-02 | Gắn tài sản vào quỹ mục tiêu | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Gán tài sản (tiền mặt, sổ tiết kiệm, lệnh mua của tài sản đầu tư) vào quỹ mục tiêu; mỗi sổ tiết kiệm hoặc lệnh mua chỉ gắn vào một quỹ; tổng tiền mặt gắn vào các quỹ không vượt tiền trong các ví, vi phạm bị từ chối |
| FR-FND-03 | Kiểm soát tiến độ và tổng hợp tài sản quỹ mục tiêu | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Tính đúng tỷ lệ phần trăm hoàn thành; chuyển trạng thái REACHED khi đạt 100% |
| FR-FND-04 | Điều chỉnh, chuyển tài sản hoặc xóa quỹ mục tiêu | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Điều chỉnh tên quỹ (không trùng), mô tả, mục tiêu và tiền mặt gắn vào quỹ, chuyển tài sản an toàn hoặc xóa hoàn toàn quỹ và tự động giải phóng tài sản liên kết |
| FR-REP-01 | Thống kê dòng tiền 12 tháng gần nhất | T, A | Môi trường kiểm thử tự động / Phân tích dữ liệu | Lấy đúng 12 tháng gần nhất liên tục; tổng hợp chính xác tổng thu nhập, tổng chi tiêu, dòng tiền thuần và tỷ lệ tiết kiệm. Ba mức bình quân chia cho 12 kèm ghi chú nêu các tháng chưa có giao dịch. Dòng thu nhập đầu tư từ sự kiện Nhận tiền mặt hiển thị riêng; Hoàn vốn và Thêm đơn vị cùng loại không tính vào thu nhập. |
| FR-REP-02 | Phân tích cơ cấu thu chi theo nhóm và danh mục | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Tính đúng tổng số tiền và tỷ lệ phần trăm chi tiêu của từng nhóm nhu cầu và danh mục |
| FR-REP-03 | Tổng hợp tài sản và định giá tài sản ròng | T, A | Môi trường kiểm thử tự động / Phân tích dữ liệu | Tổng hợp chuẩn xác: Tổng tài sản = Tiền trong các ví + Tiết kiệm + Tài sản đầu tư + Nợ phải thu; Tài sản ròng = Tổng tài sản - Tổng nợ phải trả |
| FR-REP-04 | Lập sao kê công nợ chi tiết theo người vay và người cho vay | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Liệt kê chi tiết toàn bộ lịch sử vay, trả, số tiền còn nợ và trạng thái theo từng người vay hoặc người cho vay, hoặc tổng hợp công nợ toàn hệ thống khi không chọn đối tác (TC-REP-04.3), kèm bộ lọc trạng thái `ALL`, `ACTIVE`, `PAID` (TC-REP-04.4 đến TC-REP-04.8) |
| FR-DAT-01 | Xuất dữ liệu tài chính | T, I | Môi trường kiểm thử tự động / Soát xét mã nguồn | Xuất toàn bộ dữ liệu ra tệp JSON mã hóa UTF-8; giữ nguyên vẹn kiểu số thập phân chính xác |
| FR-DAT-02 | Phục hồi dữ liệu hệ thống từ bản sao lưu | T, D | Môi trường kiểm thử tự động / Giao diện người dùng | Kiểm tra dữ liệu hợp lệ 4 bước trước khi nhập vào hệ thống; khôi phục hoàn toàn trạng thái hệ thống |
| FR-DAT-03 | Kiểm tra lại dữ liệu hệ thống | T, A | Môi trường kiểm thử tự động / Phân tích dữ liệu | Thuật toán kiểm tra lại quét toàn bộ dữ liệu tiền trong hệ thống; phát hiện 100% các sai lệch số dư nếu có |

#### 8.2.2 Ma trận phương pháp kiểm chứng yêu cầu phi chức năng (6 NFRs)

| Mã yêu cầu | Tên yêu cầu phi chức năng | Phân loại chất lượng | Phương pháp chính | Môi trường và phương thức thực hiện | Ngưỡng nghiệm thu đo được bằng số |
| --- | --- | --- | --- | --- | --- |
| NFR-01 | Độ chính xác và toàn vẹn dữ liệu tài chính | Mức độ phù hợp chức năng / Tính chính xác chức năng | T, I | Môi trường kiểm thử tự động, phân tích tĩnh mã nguồn | Sai số tích lũy = 0.00 VNĐ; 0% kiểu số dấu phẩy động nhị phân; vi phạm BR-FIN-02 = 0.00% |
| NFR-02 | Hiệu năng và độ trễ phản hồi | Hiệu quả vận hành / Hành vi thời gian & Mức sử dụng tài nguyên | D, A | Trình diễn thao tác trực tiếp, quan sát xu hướng cấp phát bộ nhớ | Phản hồi ngay với quy mô dữ liệu cá nhân thông thường; không phát hiện rò rỉ bộ nhớ |
| NFR-03 | Độ tin cậy và lưu trữ an toàn | Độ tin cậy / Khả năng chịu lỗi & Khả năng phục hồi | T, I | Kiểm thử giả lập lỗi, mô phỏng ngắt tiến trình đột ngột | Tỷ lệ hỏng tệp = 0.00%; ghi tệp an toàn không làm hỏng dữ liệu cũ; lưu trữ $\ge 5$ bản sao lưu |
| NFR-04 | Khả năng bảo trì và kiến trúc hướng đối tượng | Khả năng bảo trì / Tính module hóa, Tính tái sử dụng, Tính phân tích được & Tính kiểm thử được | T, I | Kiểm thử tự động cho từng quy tắc BR-FIN, đánh giá kiểm tra kiến trúc | 100% quy tắc BR-FIN có ca kiểm thử tương ứng; 0 phụ thuộc vòng giữa các mô-đun |
| NFR-05 | Tính khả dụng và trải nghiệm người dùng | Interaction capability / Tính dễ học, Tính dễ vận hành & Bảo vệ người dùng khỏi lỗi | D, I | Kịch bản trải nghiệm người dùng, danh mục mã lỗi chuẩn hóa | 100% thông báo lỗi tiếng Việt rõ ràng có gợi ý xử lý |
| NFR-06 | Tính tương thích và môi trường chạy | Flexibility / Tính thích ứng & Tính dễ cài đặt | T, I | Hạ tầng tích hợp liên tục đa nền tảng (Win/macOS/Linux) | 100% pass kiểm thử trên cả 3 hệ điều hành; 100% tệp dùng UTF-8; 0 native library |

---

