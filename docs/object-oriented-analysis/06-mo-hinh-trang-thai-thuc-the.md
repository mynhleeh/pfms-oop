# 5. Mô hình Trạng thái Thực thể

<!-- PLACEHOLDER: Tổng quan về Mô hình Trạng thái Thực thể (Entity State Machine Diagrams) trong OOA | Nguồn tham chiếu: Craig Larman Chapter 29, Scott Ambler Chapter 6 | Tiêu chí hoàn thành: Giải thích vai trò của biểu đồ trạng thái UML trong việc mô hình hóa vòng đời của các thực thể có hành vi phụ thuộc vào trạng thái -->

## 5.1 Khái niệm và Vai trò của Biểu đồ Trạng thái trong OOA

<!-- PLACEHOLDER: Trình bày các thành phần chuẩn của biểu đồ trạng thái UML (Trạng thái ban đầu, Trạng thái kết thúc, Trạng thái chuyển tiếp, Sự kiện kích hoạt Event, Điều kiện bảo vệ Guard condition, Hành động Action) | Nguồn tham chiếu: Larman Chapter 29 | Tiêu chí hoàn thành: Định hướng mô hình hóa ở tầng khái niệm miền, không rò rỉ mã cài đặt hay State Pattern của OOD -->

## 5.2 Vòng đời Thực thể Danh mục (Category State Machine)

<!-- PLACEHOLDER: Giới thiệu vòng đời của thực thể Category và tầm quan trọng của cơ chế xóa mềm bảo toàn tính toàn vẹn dữ liệu | Nguồn tham chiếu: FR-CAT-03, BR-FIN-04, Checkpoint 3 | Tiêu chí hoàn thành: Nêu bật lý do tại sao không xóa vật lý danh mục đã có giao dịch -->

### 5.2.1 Biểu đồ Trạng thái Danh mục

<!-- PLACEHOLDER: Biểu đồ trạng thái UML dạng Mermaid (stateDiagram-v2) thể hiện các trạng thái ACTIVE và SOFT_DELETED của Category | Nguồn tham chiếu: UC-CAT-01, UC-CAT-03, UC-CAT-04 | Tiêu chí hoàn thành: Cú pháp Mermaid hợp lệ, thể hiện nhánh hard delete khi chưa có giao dịch và soft delete khi đã có giao dịch -->

### 5.2.2 Bảng Chuyển trạng thái và Sự kiện Kích hoạt

<!-- PLACEHOLDER: Bảng chi tiết các bước chuyển trạng thái của Category gồm Trạng thái hiện tại, Sự kiện kích hoạt, Điều kiện bảo vệ (Guard), Trạng thái tiếp theo, Hành động hệ thống | Nguồn tham chiếu: SRS Mục 6.12 - 6.15 | Tiêu chí hoàn thành: Bảng chuẩn 5 cột bao phủ đầy đủ các kịch bản tạo mới, xóa mềm và khôi phục -->

## 5.3 Vòng đời Giao dịch Thu chi (IncomeExpenseTransaction State Machine)

<!-- PLACEHOLDER: Giới thiệu vòng đời của thực thể IncomeExpenseTransaction từ khi ghi nhận đến khi hoàn nguyên số dư | Nguồn tham chiếu: SRS Mục 6.5 - 6.8, BR-FIN-02, Checkpoint 3 | Tiêu chí hoàn thành: Làm rõ cơ chế tự động hoàn nguyên số dư ví và phí khi sửa hoặc xóa -->

### 5.3.1 Biểu đồ Trạng thái Giao dịch Thu chi

<!-- PLACEHOLDER: Biểu đồ trạng thái UML dạng Mermaid (stateDiagram-v2) thể hiện các trạng thái RECORDED, MODIFIED, DELETED của IncomeExpenseTransaction | Nguồn tham chiếu: UC-TXN-01, UC-TXN-03, UC-TXN-04 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện luồng hoàn nguyên tài chính an toàn -->

### 5.3.2 Bảng Chuyển trạng thái và Xử lý Hoàn nguyên Số dư

<!-- PLACEHOLDER: Bảng chi tiết các bước chuyển trạng thái của IncomeExpenseTransaction kèm điều kiện kiểm tra số dư ví không âm trước khi cho phép chuyển trạng thái | Nguồn tham chiếu: BR-FIN-02, ERR-WAL-001 | Tiêu chí hoàn thành: Bảng chuẩn 5 cột, nêu rõ hành động đồng bộ hóa trên TransactionFee -->

## 5.4 Vòng đời Giao dịch Chuyển tiền (TransferTransaction State Machine)

<!-- PLACEHOLDER: Giới thiệu vòng đời của thực thể TransferTransaction theo nguyên tắc ghi nhận đồng thời | Nguồn tham chiếu: SRS Mục 6.9 - 6.11, BR-FIN-03, Checkpoint 3 | Tiêu chí hoàn thành: Khẳng định tính nguyên khối (atomic) của giao dịch chuyển tiền nội bộ -->

### 5.4.1 Biểu đồ Trạng thái Chuyển tiền

<!-- PLACEHOLDER: Biểu đồ trạng thái UML dạng Mermaid (stateDiagram-v2) thể hiện các trạng thái INITIALIZED, COMMITTED, REVERTED của TransferTransaction | Nguồn tham chiếu: UC-TRF-01, UC-TRF-03, BR-FIN-03 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện nhánh hủy bỏ khi số dư nguồn không đủ và nhánh hoàn nguyên an toàn -->

### 5.4.2 Bảng Chuyển trạng thái và Ràng buộc Ghi nhận Đồng thời

<!-- PLACEHOLDER: Bảng chi tiết các bước chuyển trạng thái của TransferTransaction gắn liền với bất biến BR-FIN-03 (trừ ví nguồn và cộng ví đích cùng được ghi nhận hoặc cùng không ghi nhận) | Nguồn tham chiếu: BR-FIN-03, BR-FIN-02 | Tiêu chí hoàn thành: Bảng chuẩn 5 cột, nêu rõ điều kiện bảo vệ khi xóa chuyển tiền (ví đích đủ số dư để hoàn trả) -->
