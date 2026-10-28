# 6. Sơ đồ Robustness và Thẻ CRC

Tài liệu này xác định cách các đối tượng bên trong phần mềm phối hợp với nhau để thực thi các ca sử dụng, chuyển từ góc nhìn tương tác bên ngoài của người dùng sang cấu trúc xử lý nội bộ của hệ thống, làm cơ sở phân bổ trách nhiệm cho các lớp đối tượng.

Mô hình phân tích giữ tính trung lập công nghệ, không phụ thuộc vào giao diện đồ họa cụ thể, cơ sở dữ liệu hay cú pháp của ngôn ngữ lập trình.

## 6.1 Sơ đồ Robustness

Mục này thiết lập các sơ đồ Robustness nhằm làm rõ cơ chế phối hợp đối tượng khi hiện thực hóa các ca sử dụng có tính chất định hình kiến trúc của hệ thống.

Để phân định rạch ròi trách nhiệm xử lý, các đối tượng tham gia được chia thành 3 nhóm:
- Đối tượng Giao diện: Tiếp nhận thao tác từ người dùng, thu thập dữ liệu đầu vào và hiển thị kết quả phản hồi từ hệ thống.
- Đối tượng Điều khiển: Điều phối quy trình xử lý, kiểm tra tính hợp lệ của dữ liệu theo các quy tắc nghiệp vụ và chỉ đạo cập nhật trạng thái giữa các đối tượng.
- Đối tượng Thực thể: Lưu giữ thông tin nghiệp vụ lâu dài và thực hiện các phép tính toán nội tại của chính mình.

Luồng tương tác giữa các thành phần tuân thủ 3 nguyên tắc:
- Mọi yêu cầu từ người dùng đều bắt đầu từ đối tượng Giao diện và chuyển giao cho đối tượng Điều khiển xử lý.
- Đối tượng Giao diện không tương tác trực tiếp với đối tượng Thực thể mà phải thông qua đối tượng Điều khiển để bảo đảm tính độc lập giữa giao diện và dữ liệu.
- Đối tượng Thực thể chỉ quản lý dữ liệu và quy tắc nội tại của chính nó, không chủ động điều phối quy trình và không phụ thuộc vào tầng giao diện.

Nhằm tối ưu hóa độ cô đọng của tài liệu, hệ thống lược bỏ việc mô hình hóa đối với các luồng truy vấn chỉ đọc và các thao tác cập nhật thứ cấp có cùng mô thức tương tác. Mỗi nhóm chức năng lựa chọn một ca sử dụng tiêu biểu làm đại diện phân tích, ưu tiên các kịch bản làm biến động trạng thái dữ liệu, tổng hợp báo cáo đa chiều hoặc yêu cầu thực thi các quy tắc bất biến tài chính với sự tham gia đồng thời của cả 3 nhóm đối tượng.

### 6.1.1 Quản lý Ví

(Mô tả từ 1 đến 2 câu về bối cảnh nghiệp vụ khi tạo ví mới)

- Ca sử dụng đại diện: `UC-WAL-01` (Tạo ví mới)
- Thao tác hệ thống: `createWallet(walletName, note)`
- Quy chiếu chéo: `UC-WAL-01`, `FR-WAL-01`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | (Tên đối tượng) | (Thu thập dữ liệu đầu vào và hiển thị kết quả phản hồi) |
| Điều khiển | (Tên đối tượng) | (Kiểm tra quy tắc nghiệp vụ và điều phối luồng xử lý) |
| Thực thể | (Tên đối tượng) | (Lưu giữ dữ liệu và cập nhật trạng thái) |

**Sơ đồ Robustness**

(Vẽ sơ đồ Robustness bằng PlantUML với chỉ thị `left to right direction`, sử dụng 4 biểu tượng chuẩn `actor`, `boundary`, `control`, `entity`. Sơ đồ thể hiện luồng xử lý chính từ giao diện tiếp nhận đến thực thể dữ liệu và các nhánh rẽ báo lỗi khi dữ liệu không hợp lệ.)

### 6.1.2 Ghi nhận Thu chi

(Mô tả từ 1 đến 2 câu về bối cảnh nghiệp vụ khi ghi nhận giao dịch thu chi kèm chi phí nếu có)

- Ca sử dụng đại diện: `UC-TXN-01` (Ghi nhận giao dịch thu chi)
- Thao tác hệ thống: `recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- Quy chiếu chéo: `UC-TXN-01`, `FR-TXN-01`, `FR-TXN-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-FIN-001`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | (Tên đối tượng) | (Thu thập dữ liệu giao dịch và hiển thị kết quả phản hồi) |
| Điều khiển | (Tên đối tượng) | (Kiểm tra quy tắc nghiệp vụ và điều phối luồng xử lý) |
| Thực thể | (Tên các đối tượng) | (Lưu giữ dữ liệu giao dịch, chi phí và cập nhật số dư ví) |

**Sơ đồ Robustness**

(Vẽ sơ đồ Robustness bằng PlantUML với chỉ thị `left to right direction`, sử dụng 4 biểu tượng chuẩn `actor`, `boundary`, `control`, `entity`. Sơ đồ thể hiện luồng xử lý chính từ giao diện tiếp nhận đến thực thể dữ liệu và các nhánh rẽ báo lỗi khi dữ liệu không hợp lệ.)

### 6.1.3 Chuyển tiền Nội bộ

(Mô tả từ 1 đến 2 câu về bối cảnh nghiệp vụ khi thực hiện chuyển tiền nội bộ giữa hai ví kèm phí giao dịch nếu có)

- Ca sử dụng đại diện: `UC-TRF-01` (Chuyển tiền nội bộ giữa hai ví)
- Thao tác hệ thống: `transferFunds(sourceWalletId, targetWalletId, amount, transferDate, note, feeAmount, feeCategoryId)`
- Quy chiếu chéo: `UC-TRF-01`, `FR-TRF-01`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-FIN-001`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | (Tên đối tượng) | (Thu thập dữ liệu chuyển tiền và hiển thị kết quả phản hồi) |
| Điều khiển | (Tên đối tượng) | (Kiểm tra quy tắc nghiệp vụ và điều phối luồng xử lý) |
| Thực thể | (Tên các đối tượng) | (Lưu giữ dữ liệu chuyển tiền, chi phí và cập nhật số dư hai ví) |

**Sơ đồ Robustness**

(Vẽ sơ đồ Robustness bằng PlantUML với chỉ thị `left to right direction`, sử dụng 4 biểu tượng chuẩn `actor`, `boundary`, `control`, `entity`. Sơ đồ thể hiện luồng xử lý chính từ giao diện tiếp nhận đến thực thể dữ liệu và các nhánh rẽ báo lỗi khi dữ liệu không hợp lệ.)

### 6.1.4 Danh mục Phân loại

(Mô tả từ 1 đến 2 câu về bối cảnh nghiệp vụ khi bổ sung danh mục phân loại mới vào nhóm nhu cầu)

- Ca sử dụng đại diện: `UC-CAT-01` (Bổ sung danh mục phân loại mới)
- Thao tác hệ thống: `createCategory(parentId, categoryName, icon, color)`
- Quy chiếu chéo: `UC-CAT-01`, `FR-CAT-01`, `FR-CAT-02`, `BR-FIN-04`, `BR-NAM-01`, `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-005`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | (Tên đối tượng) | (Thu thập thông tin danh mục và hiển thị kết quả phản hồi) |
| Điều khiển | (Tên đối tượng) | (Kiểm tra quy tắc nghiệp vụ và điều phối luồng xử lý) |
| Thực thể | (Tên đối tượng) | (Lưu giữ thông tin danh mục và liên kết nhóm cha) |

**Sơ đồ Robustness**

(Vẽ sơ đồ Robustness bằng PlantUML với chỉ thị `left to right direction`, sử dụng 4 biểu tượng chuẩn `actor`, `boundary`, `control`, `entity`. Sơ đồ thể hiện luồng xử lý chính từ giao diện tiếp nhận đến thực thể dữ liệu và các nhánh rẽ báo lỗi khi dữ liệu không hợp lệ.)

### 6.1.5 Thống kê và Báo cáo

(Mô tả từ 1 đến 2 câu về bối cảnh nghiệp vụ khi tổng hợp chuỗi dòng tiền 12 tháng gần nhất)

- Ca sử dụng đại diện: `UC-REP-01` (Thống kê dòng tiền 12 tháng gần nhất)
- Thao tác hệ thống: `generateMonthlyCashFlowReport(endMonth, endYear)`
- Quy chiếu chéo: `UC-REP-01`, `FR-REP-01`, `BR-FIN-01`, `ERR-FIN-002`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | (Tên đối tượng) | (Tiếp nhận tiêu chí thời gian và hiển thị bảng biểu báo cáo) |
| Điều khiển | (Tên đối tượng) | (Điều phối lọc giao dịch và tính toán các chỉ số phái sinh) |
| Thực thể | (Tên đối tượng) | (Cung cấp tập dữ liệu giao dịch thu chi để tổng hợp) |

**Sơ đồ Robustness**

(Vẽ sơ đồ Robustness bằng PlantUML với chỉ thị `left to right direction`, sử dụng 4 biểu tượng chuẩn `actor`, `boundary`, `control`, `entity`. Sơ đồ thể hiện luồng xử lý chính từ giao diện tiếp nhận đến thực thể dữ liệu và các nhánh rẽ báo lỗi khi dữ liệu không hợp lệ.)

### 6.1.6 Lưu trữ và Sao lưu

(Mô tả từ 1 đến 2 câu về bối cảnh nghiệp vụ khi xuất dữ liệu tài chính toàn phần hoặc từng phần sang định dạng tệp mở)

- Ca sử dụng đại diện: `UC-DAT-01` (Xuất dữ liệu tài chính)
- Thao tác hệ thống: `exportFinancialData(scope, format, targetDirectory)`
- Quy chiếu chéo: `UC-DAT-01`, `FR-DAT-01`, `BR-FIN-01`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | (Tên đối tượng) | (Tiếp nhận tham số xuất dữ liệu và hiển thị tiến trình hoàn tất) |
| Điều khiển | (Tên đối tượng) | (Điều phối trích xuất và định dạng dữ liệu theo cấu trúc chuẩn) |
| Thực thể | (Tên các đối tượng) | (Cung cấp toàn bộ trạng thái dữ liệu nghiệp vụ để tuần tự hóa) |

**Sơ đồ Robustness**

(Vẽ sơ đồ Robustness bằng PlantUML với chỉ thị `left to right direction`, sử dụng 4 biểu tượng chuẩn `actor`, `boundary`, `control`, `entity`. Sơ đồ thể hiện luồng xử lý chính từ giao diện tiếp nhận đến thực thể dữ liệu và các nhánh rẽ báo lỗi khi dữ liệu không hợp lệ.)

## 6.2 Thẻ CRC

Thẻ CRC đóng vai trò chuyển tiếp từ mô hình phân tích hành vi sang cấu trúc lớp tĩnh, nhằm xác định ranh giới nhiệm vụ và mối quan hệ cộng tác giữa các đối tượng trong hệ thống.

Cơ sở xây dựng các thẻ CRC được tổng hợp trực tiếp từ các đối tượng thực thể và điều khiển đã xác định trong sơ đồ Robustness, kết hợp với các quy tắc tính toán và ràng buộc bất biến tài chính từ hồ sơ yêu cầu phần mềm. Kết quả phân bổ này là căn cứ để hình thành các thuộc tính, phương thức và mối liên kết trong biểu đồ lớp phân tích.

Trách nhiệm của mỗi lớp được phân định thành hai nhóm:
- Nhóm trách nhiệm thông tin: Lưu giữ các thuộc tính định danh, trạng thái tài chính và các liên kết tham chiếu giữa các đối tượng.
- Nhóm trách nhiệm hành vi: Thực hiện các phép tính toán nghiệp vụ, kiểm soát tính toàn vẹn dữ liệu và điều phối luồng xử lý.

Việc phân bổ trách nhiệm dựa trên nguyên tắc chuyên gia thông tin, theo đó một tác vụ được giao cho lớp nắm giữ đầy đủ dữ liệu nhất để tự hoàn thành tác vụ đó. Mọi trách nhiệm đều được mô tả bằng ngôn ngữ nghiệp vụ khái niệm, bảo đảm tính trung lập công nghệ và không phụ thuộc vào ngôn ngữ lập trình cụ thể.

Phạm vi mô hình hóa tập trung vào các lớp đối tượng phân tích trọng yếu, gồm các thực thể tài chính cốt lõi và thành phần điều phối dòng tiền. Các đối tượng giá trị chỉ đóng vai trò đóng gói dữ liệu và không tham gia điều phối tiến trình, nên không lập thẻ riêng.

### 6.2.1 Lớp Ví

(Mô tả từ 1 đến 2 câu về vai trò khái niệm của lớp đối tượng trong việc quản trị tài sản và theo dõi biến động số dư)

- Tên lớp phân tích: `Wallet`
- Phân loại đối tượng: (Lớp Thực thể hoặc Lớp Điều phối)
- Ca sử dụng liên quan: (Liệt kê các mã ca sử dụng UC-* mà lớp tham gia)

**Thẻ trách nhiệm và cộng tác**

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | (Mô tả các thuộc tính định danh, trạng thái tài chính hoặc quan hệ cần lưu giữ) | (Không có hoặc tên các lớp liên quan) |
| Hành vi | (Mô tả các phép tính toán nghiệp vụ, kiểm tra tính hợp lệ hoặc điều phối biến động dữ liệu) | (Tên các lớp cộng tác để hoàn thành tác vụ) |

### 6.2.2 Lớp Bản ghi Điều chỉnh Số dư

(Mô tả từ 1 đến 2 câu về vai trò khái niệm của lớp đối tượng trong việc lưu vết các khoản điều chỉnh và thiết lập số dư ban đầu)

- Tên lớp phân tích: `BalanceAdjustment`
- Phân loại đối tượng: (Lớp Thực thể hoặc Lớp Điều phối)
- Ca sử dụng liên quan: (Liệt kê các mã ca sử dụng UC-* mà lớp tham gia)

**Thẻ trách nhiệm và cộng tác**

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | (Mô tả các thuộc tính định danh, trạng thái tài chính hoặc quan hệ cần lưu giữ) | (Không có hoặc tên các lớp liên quan) |
| Hành vi | (Mô tả các phép tính toán nghiệp vụ, kiểm tra tính hợp lệ hoặc điều phối biến động dữ liệu) | (Tên các lớp cộng tác để hoàn thành tác vụ) |

### 6.2.3 Lớp Giao dịch Thu chi

(Mô tả từ 1 đến 2 câu về vai trò khái niệm của lớp đối tượng trong việc ghi nhận các giao dịch thu nhập và chi tiêu)

- Tên lớp phân tích: `Transaction`
- Phân loại đối tượng: (Lớp Thực thể hoặc Lớp Điều phối)
- Ca sử dụng liên quan: (Liệt kê các mã ca sử dụng UC-* mà lớp tham gia)

**Thẻ trách nhiệm và cộng tác**

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | (Mô tả các thuộc tính định danh, trạng thái tài chính hoặc quan hệ cần lưu giữ) | (Không có hoặc tên các lớp liên quan) |
| Hành vi | (Mô tả các phép tính toán nghiệp vụ, kiểm tra tính hợp lệ hoặc điều phối biến động dữ liệu) | (Tên các lớp cộng tác để hoàn thành tác vụ) |

### 6.2.4 Lớp Chi phí Giao dịch

(Mô tả từ 1 đến 2 câu về vai trò khái niệm của lớp đối tượng trong việc quản lý chi phí phát sinh kèm theo giao dịch)

- Tên lớp phân tích: `TransactionFee`
- Phân loại đối tượng: (Lớp Thực thể hoặc Lớp Điều phối)
- Ca sử dụng liên quan: (Liệt kê các mã ca sử dụng UC-* mà lớp tham gia)

**Thẻ trách nhiệm và cộng tác**

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | (Mô tả các thuộc tính định danh, trạng thái tài chính hoặc quan hệ cần lưu giữ) | (Không có hoặc tên các lớp liên quan) |
| Hành vi | (Mô tả các phép tính toán nghiệp vụ, kiểm tra tính hợp lệ hoặc điều phối biến động dữ liệu) | (Tên các lớp cộng tác để hoàn thành tác vụ) |

### 6.2.5 Lớp Giao dịch Chuyển tiền

(Mô tả từ 1 đến 2 câu về vai trò khái niệm của lớp đối tượng trong việc điều chuyển giá trị tiền tệ giữa hai ví nội bộ)

- Tên lớp phân tích: `Transfer`
- Phân loại đối tượng: (Lớp Thực thể hoặc Lớp Điều phối)
- Ca sử dụng liên quan: (Liệt kê các mã ca sử dụng UC-* mà lớp tham gia)

**Thẻ trách nhiệm và cộng tác**

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | (Mô tả các thuộc tính định danh, trạng thái tài chính hoặc quan hệ cần lưu giữ) | (Không có hoặc tên các lớp liên quan) |
| Hành vi | (Mô tả các phép tính toán nghiệp vụ, kiểm tra tính hợp lệ hoặc điều phối biến động dữ liệu) | (Tên các lớp cộng tác để hoàn thành tác vụ) |

### 6.2.6 Lớp Danh mục Phân loại

(Mô tả từ 1 đến 2 câu về vai trò khái niệm của lớp đối tượng trong việc phân loại chi tiết các khoản thu chi cấp 3)

- Tên lớp phân tích: `Category`
- Phân loại đối tượng: (Lớp Thực thể hoặc Lớp Điều phối)
- Ca sử dụng liên quan: (Liệt kê các mã ca sử dụng UC-* mà lớp tham gia)

**Thẻ trách nhiệm và cộng tác**

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | (Mô tả các thuộc tính định danh, trạng thái tài chính hoặc quan hệ cần lưu giữ) | (Không có hoặc tên các lớp liên quan) |
| Hành vi | (Mô tả các phép tính toán nghiệp vụ, kiểm tra tính hợp lệ hoặc điều phối biến động dữ liệu) | (Tên các lớp cộng tác để hoàn thành tác vụ) |

### 6.2.7 Lớp Nhóm Nhu cầu

(Mô tả từ 1 đến 2 câu về vai trò khái niệm của lớp đối tượng trong việc tổ chức các nhóm nhu cầu cấp 2 trực thuộc loại thu chi)

- Tên lớp phân tích: `CategoryGroup`
- Phân loại đối tượng: (Lớp Thực thể hoặc Lớp Điều phối)
- Ca sử dụng liên quan: (Liệt kê các mã ca sử dụng UC-* mà lớp tham gia)

**Thẻ trách nhiệm và cộng tác**

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | (Mô tả các thuộc tính định danh, trạng thái tài chính hoặc quan hệ cần lưu giữ) | (Không có hoặc tên các lớp liên quan) |
| Hành vi | (Mô tả các phép tính toán nghiệp vụ, kiểm tra tính hợp lệ hoặc điều phối biến động dữ liệu) | (Tên các lớp cộng tác để hoàn thành tác vụ) |

### 6.2.8 Lớp Điều phối Dòng tiền

(Mô tả từ 1 đến 2 câu về vai trò khái niệm của lớp đối tượng trong việc điều phối quy trình nghiệp vụ và kiểm soát quy tắc tài chính)

- Tên lớp phân tích: `CashFlowCoordinator`
- Phân loại đối tượng: (Lớp Thực thể hoặc Lớp Điều phối)
- Ca sử dụng liên quan: (Liệt kê các mã ca sử dụng UC-* mà lớp tham gia)

**Thẻ trách nhiệm và cộng tác**

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | (Mô tả các thuộc tính định danh, trạng thái tài chính hoặc quan hệ cần lưu giữ) | (Không có hoặc tên các lớp liên quan) |
| Hành vi | (Mô tả các phép tính toán nghiệp vụ, kiểm tra tính hợp lệ hoặc điều phối biến động dữ liệu) | (Tên các lớp cộng tác để hoàn thành tác vụ) |
