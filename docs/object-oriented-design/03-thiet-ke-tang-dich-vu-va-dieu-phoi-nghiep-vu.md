# 03. Thiết kế Tầng Dịch vụ Ứng dụng và Điều phối Nghiệp vụ

Tầng dịch vụ ứng dụng xử lý các ca sử dụng của hệ thống, làm việc như lớp trung gian giữa giao diện dòng lệnh và các lớp nghiệp vụ. Các giao diện dịch vụ công khai nằm trong gói `com.pfms.service`, còn các lớp cài đặt tương ứng nằm trong gói `com.pfms.service.impl`. Giao diện dòng lệnh chỉ tương tác qua các giao diện dịch vụ, không phụ thuộc vào lớp cài đặt cụ thể.

Lớp dịch vụ chịu trách nhiệm tiếp nhận dữ liệu đầu vào, kiểm tra điều kiện tiên quyết, nạp và lưu thực thể qua kho lưu trữ, đồng thời bảo đảm tính nguyên tử khi ghi tệp. Các phép tính tài chính, kiểm soát số dư không âm và tỷ lệ phân bổ do các thực thể và đối tượng giá trị tự thực hiện. Khi có lỗi dữ liệu hoặc sự cố lưu trữ, dịch vụ ném ngoại lệ tương ứng với mã lỗi nghiệp vụ để giao diện hiển thị thông báo cho người dùng.

## 3.1 Dịch vụ Quản lý Ví

(Mô tả khoảng 1 đến 2 câu về vai trò của WalletService trong việc điều phối các ca sử dụng quản lý ví, kiểm tra tính duy nhất của tên ví, truy vấn cơ cấu số dư và ghi nhận điều chỉnh số dư thực tế)

- Quy chiếu: `UC-WAL-01`, `UC-WAL-02`, `UC-WAL-03`, `UC-WAL-04`, `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`, `FR-WAL-05`, `BR-FIN-01`, `BR-FIN-02`, `BR-NAM-01`, `ERR-NAM-001`, `ERR-NAM-002`, `ERR-WAL-001`, `ERR-FIN-001`, `ERR-FIN-002`

**Đặc tả phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Tham số DTO hoặc nguyên thủy) | (Kiểu trả về DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Quy tắc điều phối và kiểm soát giao dịch**

- (Nêu logic kiểm tra tiền điều kiện về định dạng và tính duy nhất của tên ví theo quy tắc tên trước khi tạo mới hoặc cập nhật)
- (Nêu quy tắc nạp thực thể và ủy quyền tính toán số dư thực tế cho Thực thể Wallet, bảo đảm dịch vụ không tự làm toán số học)
- (Nêu quy tắc kiểm soát giao dịch nguyên tử khi lưu bản ghi điều chỉnh số dư, ngăn chặn sai lệch trạng thái khi xảy ra sự cố tệp)

## 3.2 Dịch vụ Ghi nhận Thu Chi

(Mô tả khoảng 1 đến 2 câu về vai trò của TransactionService trong việc điều phối ghi nhận thu chi, tra cứu lịch sử, chỉnh sửa và xóa giao dịch tài chính)

- Quy chiếu: `UC-TXN-01`, `UC-TXN-02`, `UC-TXN-03`, `UC-TXN-04`, `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`, `FR-TXN-04`, `FR-TXN-05`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-WAL-001`, `ERR-CAT-001`

**Đặc tả phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Tham số DTO hoặc nguyên thủy) | (Kiểu trả về DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Quy tắc điều phối và kiểm soát giao dịch**

- (Nêu quy tắc kiểm tra điều kiện tiên quyết về tính hợp lệ của danh mục chi tiêu và danh mục phí trước khi ghi nhận giao dịch)
- (Nêu quy tắc thẩm định số dư khả dụng thông qua Thực thể Wallet trước khi trừ tiền chi tiêu hoặc phí kèm theo)
- (Nêu cơ chế cập nhật đồng thời giữa sổ giao dịch và số dư ví trong kho lưu trữ, bảo đảm tính toàn vẹn khi có lỗi phát sinh)

## 3.3 Dịch vụ Chuyển tiền Nội bộ

(Mô tả khoảng 1 đến 2 câu về vai trò của TransferService trong việc điều phối luồng chuyển tiền giữa hai ví độc lập và hạch toán chi phí giao dịch)

- Quy chiếu: `UC-TRF-01`, `UC-TRF-02`, `UC-TRF-03`, `FR-TRF-01`, `FR-TRF-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, `ERR-TRF-001`, `ERR-TRF-002`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-WAL-001`, `ERR-FIN-001`

**Đặc tả phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Tham số DTO hoặc nguyên thủy) | (Kiểu trả về DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Quy tắc điều phối và kiểm soát giao dịch**

- (Nêu quy tắc kiểm tra hai ví tham gia giao dịch phải khác nhau và cùng ở trạng thái hoạt động)
- (Nêu quy trình ủy quyền kiểm tra số dư ví nguồn gồm cả tiền chuyển và phí trước khi thực hiện hạch toán)
- (Nêu cơ chế giao dịch nguyên tử bảo đảm trừ tiền ví nguồn và cộng tiền ví đích diễn ra đồng thời, tự động hủy bỏ nếu ghi tệp thất bại)

## 3.4 Dịch vụ Danh mục Phân loại

(Mô tả khoảng 1 đến 2 câu về vai trò của CategoryService trong việc điều phối các ca sử dụng quản lý danh mục, kiểm soát cấu trúc cây 3 cấp và xử lý xóa mềm bảo toàn lịch sử)

- Quy chiếu: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`, `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`, `BR-FIN-04`, `BR-NAM-01`, `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`, `ERR-CAT-004`, `ERR-CAT-005`

**Đặc tả phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Tham số DTO hoặc nguyên thủy) | (Kiểu trả về DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Quy tắc điều phối và kiểm soát giao dịch**

- (Nêu quy tắc kiểm tra định dạng và tính duy nhất của tên nhóm hoặc tên danh mục trong cùng phạm vi phân loại)
- (Nêu quy tắc kiểm tra điều kiện tiên quyết khi đổi loại thu chi, khôi phục danh mục hoặc di chuyển danh mục giữa các nhóm)
- (Nêu cơ chế xóa mềm nhằm bảo toàn liên kết với các giao dịch cũ trong lịch sử và ngăn chặn việc xóa nhóm cuối cùng của một loại thu chi)

## 3.5 Dịch vụ Báo cáo và Thống kê Tài chính

(Mô tả khoảng 1 đến 2 câu về vai trò của ReportService trong việc tổng hợp số liệu thu chi, phân tích cơ cấu chi tiêu và lập báo cáo dòng tiền thuần ròng)

- Quy chiếu: `UC-REP-01`, `UC-REP-02`, `FR-REP-01`, `FR-REP-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-FIN-002`

**Đặc tả phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Tham số DTO hoặc nguyên thủy) | (Kiểu trả về DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Quy tắc điều phối và kiểm soát giao dịch**

- (Nêu cơ chế vận hành ở chế độ chỉ đọc, trích xuất dữ liệu từ các kho lưu trữ mà tuyệt đối không làm thay đổi trạng thái của bất kỳ thực thể nào)
- (Nêu quy tắc ủy quyền tính toán dòng tiền thuần ròng và tỷ lệ tiết kiệm cho các Đối tượng giá trị tương ứng)
- (Nêu quy trình đóng gói dữ liệu kết quả vào các đối tượng DTO báo cáo chuyên biệt trước khi chuyển giao cho tầng hiển thị)

## 3.6 Dịch vụ Quản trị và Sao lưu Dữ liệu

(Mô tả khoảng 1 đến 2 câu về vai trò của DataManagementService trong việc điều phối xuất dữ liệu sao lưu, phục hồi dữ liệu hệ thống và kiểm tra đối soát tính toàn vẹn)

- Quy chiếu: `UC-DAT-01`, `UC-DAT-02`, `UC-DAT-03`, `FR-DAT-01`, `FR-DAT-02`, `FR-DAT-03`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-WAL-001`

**Đặc tả phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Tham số DTO hoặc nguyên thủy) | (Kiểu trả về DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Quy tắc điều phối và kiểm soát giao dịch**

- (Nêu quy tắc xuất dữ liệu tài chính sang định dạng JSON giữ nguyên độ chính xác số học của chuỗi tiền tệ và không làm biến đổi dữ liệu hệ thống)
- (Nêu quy trình phục hồi dữ liệu qua 4 bước kiểm tra toàn vẹn nghiêm ngặt kèm cơ chế tự động hoàn nguyên khi phát hiện bất kỳ sai lệch nào)
- (Nêu cơ chế đối soát độc lập 4 điều kiện toàn vẹn tài chính ở chế độ chỉ đọc và phân loại mức độ cảnh báo)
