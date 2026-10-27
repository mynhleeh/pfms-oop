# 3. Sơ đồ Tuần tự Hệ thống

<!-- PLACEHOLDER: Tổng quan về Sơ đồ Tuần tự Hệ thống (SSD) trong OOA theo Craig Larman | Nguồn tham chiếu: Larman Chapter 10, SRS Mục 6 | Tiêu chí hoàn thành: Định nghĩa rõ SSD thể hiện các tương tác giữa tác nhân ngoài và hộp đen :System, không rò rỉ phương thức nội bộ -->

## 3.1 Nguyên tắc Mô hình hóa SSD và Ranh giới Hộp đen (:System)

<!-- PLACEHOLDER: Mô tả nguyên tắc hộp đen: Hệ thống được xem như một đối tượng duy nhất (:System), chỉ ghi nhận System Events và phản hồi thông tin nghiệp vụ, không mở rộng thành các controller hay service trong OOA | Nguồn tham chiếu: Larman Chapter 10, Rule 03 | Tiêu chí hoàn thành: Nêu rõ quy tắc đặt tên thao tác theo chuẩn động từ + danh từ -->

## 3.2 Bảng Tổng hợp System Events và System Operations Toàn Hệ thống

<!-- PLACEHOLDER: Bảng tổng hợp toàn bộ các System Events và System Operations tương ứng với 20 ca sử dụng của hệ thống | Nguồn tham chiếu: Checkpoint 2, SRS Mục 6 | Tiêu chí hoàn thành: Bảng chuẩn 5 cột: STT, Mã UC, Tên Ca sử dụng, System Operation (kèm tham số), Mục đích và loại thao tác -->

## 3.3 SSD Nhóm Chức năng Quản lý Ví (WAL)

<!-- PLACEHOLDER: Giới thiệu các kịch bản tương tác tuần tự cho nhóm chức năng Ví | Nguồn tham chiếu: SRS Mục 6.1 - 6.4 | Tiêu chí hoàn thành: Thể hiện chuỗi thông điệp hộp đen cho các ca sử dụng tạo ví và điều chỉnh số dư -->

### 3.3.1 SSD UC-WAL-01: Tạo ví mới

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-WAL-01 thể hiện luồng Người dùng gửi createWallet(walletName, note) và :System phản hồi xác nhận thành công hoặc lỗi vi phạm tên BR-NAM-01 | Nguồn tham chiếu: SRS Mục 6.1 (UC-WAL-01) | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn sequenceDiagram, có các khối alt/opt xử lý ngoại lệ ERR-NAM-001 -->

### 3.3.2 SSD UC-WAL-04: Điều chỉnh số dư ví

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-WAL-04 thể hiện luồng Người dùng gửi adjustBalance(walletId, actualBalance, reason) và :System tính toán chênh lệch, cập nhật số dư theo BR-FIN-02 | Nguồn tham chiếu: SRS Mục 6.4 (UC-WAL-04), BR-FIN-02 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện xử lý kiểm tra số dư không âm -->

## 3.4 SSD Nhóm Chức năng Ghi nhận Thu chi (TXN)

<!-- PLACEHOLDER: Giới thiệu các kịch bản tương tác tuần tự cho nhóm chức năng Ghi nhận Thu chi | Nguồn tham chiếu: SRS Mục 6.5 - 6.8 | Tiêu chí hoàn thành: Bao phủ các ca sử dụng cốt lõi ghi nhận thu chi kèm phí, sửa và xóa giao dịch -->

### 3.4.1 SSD UC-TXN-01: Ghi nhận giao dịch thu chi kèm phí

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-TXN-01 thể hiện luồng recordIncomeExpense(...) bao gồm xử lý phí tùy chọn (feeAmount, feeCategoryId), kiểm tra số dư theo BR-FIN-02 | Nguồn tham chiếu: SRS Mục 6.5 (UC-TXN-01), BR-FIN-02 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện nhánh chi tiêu từ chối khi số dư không đủ (ERR-WAL-001) và ghi nhận phí đồng thời -->

### 3.4.2 SSD UC-TXN-03: Chỉnh sửa giao dịch thu chi

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-TXN-03 thể hiện luồng updateTransaction(...) kiểm tra bảo toàn số dư sau khi thay đổi số tiền hoặc ví | Nguồn tham chiếu: SRS Mục 6.7 (UC-TXN-03), BR-FIN-02 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện hoàn nguyên giao dịch cũ và áp dụng giao dịch mới -->

### 3.4.3 SSD UC-TXN-04: Xóa giao dịch thu chi

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-TXN-04 thể hiện luồng deleteTransaction(...) hoàn nguyên số dư ví và xóa đồng thời phí giao dịch phát sinh | Nguồn tham chiếu: SRS Mục 6.8 (UC-TXN-04), BR-FIN-02 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, kiểm tra điều kiện hoàn nguyên không làm số dư âm -->

## 3.5 SSD Nhóm Chức năng Chuyển tiền Nội bộ (TRF)

<!-- PLACEHOLDER: Giới thiệu các kịch bản tương tác tuần tự cho nhóm chức năng Chuyển tiền nội bộ | Nguồn tham chiếu: SRS Mục 6.9 - 6.11 | Tiêu chí hoàn thành: Làm rõ cơ chế ghi nhận đồng thời giữa hai ví -->

### 3.5.1 SSD UC-TRF-01: Chuyển tiền nội bộ kèm phí

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-TRF-01 thể hiện luồng transferFunds(...) trừ ví nguồn, cộng ví đích và trừ phí theo BR-FIN-03 | Nguồn tham chiếu: SRS Mục 6.9 (UC-TRF-01), BR-FIN-03 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện kiểm tra số dư ví nguồn đủ trả cả tiền chuyển và phí (ERR-WAL-001) -->

### 3.5.2 SSD UC-TRF-03: Xóa giao dịch chuyển tiền

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-TRF-03 thể hiện luồng deleteTransfer(...) hoàn nguyên đồng thời số dư cả hai ví và phí | Nguồn tham chiếu: SRS Mục 6.11 (UC-TRF-03), BR-FIN-03 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, kiểm tra điều kiện ví đích đủ số dư để hoàn nguyên -->

## 3.6 SSD Nhóm Chức năng Danh mục Phân loại (CAT)

<!-- PLACEHOLDER: Giới thiệu các kịch bản tương tác tuần tự cho nhóm chức năng Danh mục phân loại | Nguồn tham chiếu: SRS Mục 6.12 - 6.15 | Tiêu chí hoàn thành: Thể hiện quy tắc cấu trúc 3 cấp và xóa mềm -->

### 3.6.1 SSD UC-CAT-01: Bổ sung danh mục phân loại mới

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-CAT-01 thể hiện tạo nhóm nhu cầu hoặc danh mục chi tiết theo BR-FIN-04 và kiểm tra trùng tên BR-NAM-01 | Nguồn tham chiếu: SRS Mục 6.12 (UC-CAT-01), BR-FIN-04 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện gán đúng nhóm cha hợp lệ -->

### 3.6.2 SSD UC-CAT-04: Xóa danh mục hoặc nhóm

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-CAT-04 thể hiện xóa mềm danh mục (SOFT_DELETED) để bảo toàn lịch sử giao dịch cũ | Nguồn tham chiếu: SRS Mục 6.15 (UC-CAT-04), FR-CAT-03 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, phân biệt trường hợp danh mục đã có giao dịch (xóa mềm) và chưa có giao dịch -->

## 3.7 SSD Nhóm Chức năng Thống kê và Báo cáo (REP)

<!-- PLACEHOLDER: Giới thiệu các kịch bản tương tác tuần tự cho nhóm chức năng Thống kê và Báo cáo | Nguồn tham chiếu: SRS Mục 6.16 - 6.17 | Tiêu chí hoàn thành: Thể hiện các yêu cầu truy vấn phân tích số liệu tài chính -->

### 3.7.1 SSD UC-REP-01: Thống kê dòng tiền 12 tháng gần nhất

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-REP-01 thể hiện Người dùng yêu cầu generateCashFlowReport(...) và :System trích xuất chuỗi 12 tháng liên tục, tính dòng tiền thuần và tỷ lệ tiết kiệm | Nguồn tham chiếu: SRS Mục 6.16 (UC-REP-01), FR-REP-01 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, trả về bảng tổng hợp dòng tiền -->

### 3.7.2 SSD UC-REP-02: Phân tích cơ cấu thu chi theo nhóm và danh mục

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-REP-02 thể hiện generateSpendingStructureReport(...) và :System tổng hợp tỷ trọng phần trăm theo cấu trúc 3 cấp | Nguồn tham chiếu: SRS Mục 6.17 (UC-REP-02), FR-REP-02 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện dữ liệu phân tích cơ cấu chi tiêu -->

## 3.8 SSD Nhóm Chức năng Lưu trữ và Sao lưu Dữ liệu (DAT)

<!-- PLACEHOLDER: Giới thiệu các kịch bản tương tác tuần tự cho nhóm chức năng Lưu trữ và Sao lưu | Nguồn tham chiếu: SRS Mục 6.18 - 6.20 | Tiêu chí hoàn thành: Thể hiện các tương tác xuất và phục hồi an toàn dữ liệu -->

### 3.8.1 SSD UC-DAT-01: Xuất dữ liệu tài chính

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-DAT-01 thể hiện xuất toàn bộ dữ liệu ra tệp định dạng chuẩn | Nguồn tham chiếu: SRS Mục 6.18 (UC-DAT-01), FR-DAT-01 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, xác nhận ghi tệp an toàn -->

### 3.8.2 SSD UC-DAT-02: Phục hồi dữ liệu hệ thống từ bản sao lưu

<!-- PLACEHOLDER: Biểu đồ tuần tự hệ thống dạng Mermaid cho UC-DAT-02 thể hiện kiểm tra tính toàn vẹn của tệp sao lưu trước khi ghi đè vào bộ nhớ | Nguồn tham chiếu: SRS Mục 6.19 (UC-DAT-02), FR-DAT-02 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện các luồng từ chối khi tệp hỏng hoặc sai cấu trúc -->
