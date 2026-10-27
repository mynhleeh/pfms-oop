# 6. Phân tích Robustness và Thẻ CRC

<!-- PLACEHOLDER: Tổng quan về phân tích Robustness (Ivar Jacobson) và phương pháp thẻ CRC (Scott Ambler) trong OOA | Nguồn tham chiếu: Scott Ambler Chapter 6, Craig Larman Chapter 15 | Tiêu chí hoàn thành: Giải thích vai trò cầu nối phân tích - thiết kế nhằm kiểm tra tính khả thi của ca sử dụng và phân bổ trách nhiệm sơ bộ phục vụ Milestone 2 -->

## 6.1 Phương pháp Phân tích Robustness (Boundary - Control - Entity)

<!-- PLACEHOLDER: Trình bày quy tắc 3 loại đối tượng trong Robustness Analysis: Boundary Object (Giao diện tiếp nhận), Control Object (Điều phối quy trình nghiệp vụ), Entity Object (Thực thể miền lưu giữ dữ liệu) | Nguồn tham chiếu: Jacobson Object-Oriented Software Engineering | Tiêu chí hoàn thành: Nêu rõ 4 quy tắc kết nối hợp lệ (Actor chỉ giao tiếp Boundary, Boundary không giao tiếp trực tiếp Entity, Control điều phối giữa Boundary và Entity, Entity không gọi Control) -->

## 6.2 Biểu đồ Robustness cho các Ca Sử dụng Cốt lõi

<!-- PLACEHOLDER: Giới thiệu danh mục các ca sử dụng phức tạp cần phân tích Robustness | Nguồn tham chiếu: SRS Mục 6 | Tiêu chí hoàn thành: Chọn lọc 4 ca sử dụng đặc trưng đại diện cho các luồng biến động tài chính -->

### 6.2.1 Robustness UC-WAL-01: Tạo ví mới

<!-- PLACEHOLDER: Biểu đồ Robustness dạng Mermaid cho UC-WAL-01 thể hiện luồng từ Người dùng -> WalletCreationBoundary -> WalletCreationController -> Wallet Entity | Nguồn tham chiếu: SRS Mục 6.1 (UC-WAL-01) | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện xử lý kiểm tra trùng tên BR-NAM-01 và khởi tạo số dư 0.00 -->

### 6.2.2 Robustness UC-TXN-01: Ghi nhận giao dịch thu chi kèm phí

<!-- PLACEHOLDER: Biểu đồ Robustness dạng Mermaid cho UC-TXN-01 thể hiện TransactionEntryBoundary -> TransactionRecordController -> Wallet, Category, Transaction, TransactionFee Entities | Nguồn tham chiếu: SRS Mục 6.5 (UC-TXN-01), BR-FIN-02 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện luồng kiểm tra số dư và ghi nhận đồng thời phí -->

### 6.2.3 Robustness UC-TRF-01: Chuyển tiền nội bộ kèm phí

<!-- PLACEHOLDER: Biểu đồ Robustness dạng Mermaid cho UC-TRF-01 thể hiện TransferBoundary -> TransferController -> SourceWallet, DestinationWallet, TransferTransaction, TransactionFee | Nguồn tham chiếu: SRS Mục 6.9 (UC-TRF-01), BR-FIN-03 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện cơ chế ghi nhận nguyên khối (atomic) giữa hai ví -->

### 6.2.4 Robustness UC-WAL-04: Điều chỉnh số dư ví

<!-- PLACEHOLDER: Biểu đồ Robustness dạng Mermaid cho UC-WAL-04 thể hiện BalanceAdjustmentBoundary -> BalanceAdjustmentController -> Wallet, BalanceAdjustmentRecord | Nguồn tham chiếu: SRS Mục 6.4 (UC-WAL-04), BR-FIN-02 | Tiêu chí hoàn thành: Biểu đồ Mermaid chuẩn, thể hiện tính toán chênh lệch và cập nhật số dư -->

## 6.3 Phương pháp Thẻ CRC (Class - Responsibility - Collaborator)

<!-- PLACEHOLDER: Trình bày phương pháp thẻ CRC theo Scott Ambler: Định nghĩa Class, Responsibilities (Trách nhiệm biết và Trách nhiệm làm), Collaborators (Đối tác cộng tác) | Nguồn tham chiếu: Scott Ambler The Object Primer Chapter 6 | Tiêu chí hoàn thành: Làm rõ cách thức phân bổ trách nhiệm dựa trên nguyên tắc chuyên gia thông tin (Information Expert) của Larman mà không gán method cụ thể -->

## 6.4 Bộ Thẻ CRC Phân bổ Trách nhiệm Sơ bộ

<!-- PLACEHOLDER: Bảng tổng hợp bộ thẻ CRC cho các lớp khái niệm cốt lõi phục vụ đánh giá Rubric Milestone 2 môn học OOP PTIT | Nguồn tham chiếu: Rubric 100 điểm PTIT, Checkpoint 1 | Tiêu chí hoàn thành: Đầy đủ các thẻ CRC cho từng lớp chính, xác định rõ trách nhiệm và đối tác -->

### 6.4.1 Thẻ CRC Thực thể Ví (Wallet)

<!-- PLACEHOLDER: Thẻ CRC cho lớp Wallet: Trách nhiệm quản lý tên ví, trạng thái và cung cấp lịch sử tính toán số dư; Đối tác cộng tác: IncomeExpenseTransaction, TransferTransaction, BalanceAdjustmentRecord | Nguồn tham chiếu: FR-WAL-01 đến FR-WAL-05, BR-FIN-02 | Tiêu chí hoàn thành: Bảng chuẩn 3 phần (Class, Responsibilities, Collaborators) -->

### 6.4.2 Thẻ CRC Giao dịch Thu chi (IncomeExpenseTransaction)

<!-- PLACEHOLDER: Thẻ CRC cho lớp IncomeExpenseTransaction: Trách nhiệm lưu trữ dữ liệu thu chi, tính chất biến động dòng tiền, liên kết phí; Đối tác: Wallet, Category, TransactionFee | Nguồn tham chiếu: FR-TXN-01 đến FR-TXN-05 | Tiêu chí hoàn thành: Bảng chuẩn 3 phần -->

### 6.4.3 Thẻ CRC Giao dịch Chuyển tiền (TransferTransaction)

<!-- PLACEHOLDER: Thẻ CRC cho lớp TransferTransaction: Trách nhiệm điều phối dịch chuyển tiền tệ giữa 2 ví, bảo đảm ghi nhận đồng thời; Đối tác: Wallet (Source/Dest), TransactionFee | Nguồn tham chiếu: FR-TRF-01 đến FR-TRF-03, BR-FIN-03 | Tiêu chí hoàn thành: Bảng chuẩn 3 phần -->

### 6.4.4 Thẻ CRC Danh mục Phân loại (Category)

<!-- PLACEHOLDER: Thẻ CRC cho lớp Category: Trách nhiệm lưu trữ tên danh mục, trạng thái xóa mềm, cung cấp thông tin phân loại cho giao dịch; Đối tác: NeedGroup, IncomeExpenseTransaction | Nguồn tham chiếu: FR-CAT-01 đến FR-CAT-05, BR-FIN-04 | Tiêu chí hoàn thành: Bảng chuẩn 3 phần -->

### 6.4.5 Thẻ CRC Điều chỉnh Số dư (BalanceAdjustmentRecord)

<!-- PLACEHOLDER: Thẻ CRC cho lớp BalanceAdjustmentRecord: Trách nhiệm lưu trữ bất biến số dư trước/sau, chênh lệch và lý do kiểm kê; Đối tác: Wallet | Nguồn tham chiếu: FR-WAL-05, BR-FIN-02 | Tiêu chí hoàn thành: Bảng chuẩn 3 phần -->

### 6.4.6 Thẻ CRC Điều phối Dòng tiền (CashFlowCoordinator)

<!-- PLACEHOLDER: Thẻ CRC cho lớp CashFlowCoordinator (Control): Trách nhiệm tính toán tổng hợp số dư toàn bộ ví, tổng hợp dòng tiền 12 tháng và phân tích cơ cấu chi tiêu; Đối tác: Wallet, Category, IncomeExpenseTransaction | Nguồn tham chiếu: FR-WAL-02, FR-REP-01, FR-REP-02 | Tiêu chí hoàn thành: Bảng chuẩn 3 phần -->
