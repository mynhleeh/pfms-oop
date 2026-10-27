# 3. Sơ đồ Tuần tự Hệ thống

Sơ đồ tuần tự hệ thống mô tả trình tự tương tác giữa Người dùng và Hệ thống cho từng kịch bản ca sử dụng theo trục thời gian. Trong giai đoạn phân tích, sơ đồ này đóng vai trò cầu nối nghiệp vụ: chuyển các bước mô tả bằng lời văn trong hồ sơ ca sử dụng thành các sự kiện đầu vào rõ ràng, làm căn cứ xác định các hợp đồng thao tác tại Mục 4 trước khi bước vào thiết kế chi tiết các lớp bên trong phần mềm.

## 3.1 Nguyên tắc Mô hình hóa và Ranh giới Tương tác Hệ thống

Khi mô hình hóa tuần tự ở tầng phân tích, toàn bộ phần mềm được xem như một thể thống nhất, tiếp cận hoàn toàn từ góc nhìn của người dùng bên ngoài. Cách tiếp cận này chỉ tập trung vào hai yếu tố: thông tin Người dùng gửi vào và kết quả Hệ thống phản hồi lại. Cấu trúc bên trong phần mềm được giữ kín, không đề cập đến các thành phần lập trình như bộ điều khiển, dịch vụ xử lý, các lớp dữ liệu hay cấu trúc cơ sở dữ liệu.

Các yêu cầu Người dùng gửi đến được chuẩn hóa thành các thao tác hệ thống bằng tiếng Anh theo dạng động từ kết hợp danh từ như `createWallet`, `recordTransaction`, `transferFunds`. Các tham số truyền vào thể hiện dữ liệu nghiệp vụ thực tế, không gắn với kiểu dữ liệu của bất kỳ ngôn ngữ lập trình nào. Phía Hệ thống sẽ kiểm tra các quy tắc nghiệp vụ về số dư và tính hợp lệ của dữ liệu để phản hồi kết quả thành công hoặc gửi thông báo từ chối tương ứng khi phát hiện vi phạm.

## 3.2 Tổng hợp Thao tác Hệ thống theo Nhóm Chức năng

Hệ thống PFMS tiếp nhận 20 thao tác từ người dùng, tương ứng với 20 ca sử dụng đã xác định trong tài liệu yêu cầu. Các thao tác này được chia theo 6 nhóm chức năng. Mỗi thao tác là một yêu cầu gửi đến Hệ thống để truy vấn thông tin hoặc thay đổi trạng thái của các thực thể tài chính.

| STT | Nhóm chức năng | Mã nhóm | Ca sử dụng | Thao tác hệ thống |
| :---: | :--- | :---: | :---: | :--- |
| 1 | Quản lý Ví | `WAL` | `UC-WAL-01` đến `UC-WAL-04` | `createWallet`, `getWalletsSummary`, `updateWallet`, `adjustBalance` |
| 2 | Ghi nhận Thu chi | `TXN` | `UC-TXN-01` đến `UC-TXN-04` | `recordIncomeExpense`, `filterTransactions`, `updateTransaction`, `deleteTransaction` |
| 3 | Chuyển tiền Nội bộ | `TRF` | `UC-TRF-01` đến `UC-TRF-03` | `transferFunds`, `filterTransfers`, `deleteTransfer` |
| 4 | Danh mục Phân loại | `CAT` | `UC-CAT-01` đến `UC-CAT-04` | `addCategory`, `renameCategory`, `restoreCategory`, `deleteCategory` |
| 5 | Thống kê và Báo cáo | `REP` | `UC-REP-01`, `UC-REP-02` | `generateCashFlowReport`, `generateSpendingStructureReport` |
| 6 | Lưu trữ và Sao lưu Dữ liệu | `DAT` | `UC-DAT-01` đến `UC-DAT-03` | `exportBackupData`, `restoreBackupData`, `reconcileSystemData` |

Bảng thông số chi tiết của từng thao tác, bao gồm danh sách tham số, dữ liệu phản hồi và kịch bản tuần tự được trình bày cụ thể trong từng nhóm chức năng tương ứng từ Mục 3.3 đến Mục 3.8.

## 3.3 Nhóm Chức năng Quản lý Ví (WAL)

Nhóm chức năng Quản lý Ví đảm nhiệm việc quản lý các nguồn tiền thanh toán, theo dõi số dư khả dụng và xử lý các khoản chênh lệch kiểm kê thực tế. Toàn bộ các thao tác trong nhóm này chịu sự chi phối trực tiếp của quy tắc đặt tên `BR-NAM-01` và quy tắc bảo toàn số dư không âm `BR-FIN-02`.

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---: | :--- | :--- | :--- | :--- |
| `UC-WAL-01` | Tạo ví mới | `createWallet` | Khởi tạo nguồn tiền mới với số dư ban đầu bằng 0 | Ghi nhận |
| `UC-WAL-02` | Theo dõi số dư tính lại và cơ cấu ví | `getWalletsSummary` | Tính số dư khả dụng tức thời và tỷ lệ phân bổ ví | Truy vấn |
| `UC-WAL-03` | Cập nhật thông tin ví | `updateWallet` | Sửa tên, ghi chú hoặc chuyển đổi trạng thái lưu trữ ví | Cập nhật |
| `UC-WAL-04` | Điều chỉnh số dư ví | `adjustBalance` | Ghi nhận mốc chênh lệch kiểm kê thực tế | Ghi nhận |

### 3.3.1 Tạo ví mới

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi khởi tạo nguồn tiền mới)

- Thao tác hệ thống: `createWallet(walletName, note)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.3.2 Xem tổng hợp số dư và cơ cấu ví

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi truy vấn số dư khả dụng tức thời và phân tích cơ cấu phân bổ giữa các ví)

- Thao tác hệ thống: `getWalletsSummary()`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.3.3 Cập nhật thông tin ví

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi chỉnh sửa tên, ghi chú hoặc chuyển đổi trạng thái lưu trữ của ví)

- Thao tác hệ thống: `updateWallet(walletId, walletName, note)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.3.4 Điều chỉnh số dư ví thực tế

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi ghi nhận mốc điều chỉnh số dư thực tế lệch khỏi số dư hệ thống)

- Thao tác hệ thống: `adjustBalance(walletId, actualBalance, reason)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

## 3.4 Nhóm Chức năng Ghi nhận Thu chi (TXN)

Nhóm chức năng Ghi nhận Thu chi điều hành các sự kiện phát sinh dòng tiền hàng ngày của người dùng. Các thao tác trong nhóm kiểm soát việc hạch toán thu nhập, chi tiêu kèm chi phí phát sinh, đồng thời bảo đảm việc cập nhật, chỉnh sửa hoặc xóa giao dịch luôn tuân thủ quy tắc bảo toàn số dư không âm `BR-FIN-02` và tính hợp lệ của phân cấp danh mục `BR-FIN-04`.

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---: | :--- | :--- | :--- | :--- |
| `UC-TXN-01` | Ghi nhận giao dịch thu chi | `recordIncomeExpense` | Ghi nhận khoản thu nhập hoặc chi tiêu phát sinh kèm chi phí liên kết nếu có | Ghi nhận |
| `UC-TXN-02` | Tra cứu và lọc lịch sử giao dịch thu chi | `filterTransactions` | Trích xuất danh sách giao dịch thu chi theo thời gian, ví thanh toán và danh mục | Truy vấn |
| `UC-TXN-03` | Chỉnh sửa giao dịch thu chi | `updateTransaction` | Điều chỉnh thuộc tính giao dịch, hoàn nguyên tác động cũ và kiểm soát số dư | Cập nhật |
| `UC-TXN-04` | Xóa giao dịch thu chi | `deleteTransaction` | Loại bỏ giao dịch khỏi sổ theo dõi, hoàn nguyên số dư ví và xóa chi phí kèm theo | Xóa |

### 3.4.1 Ghi nhận giao dịch thu chi

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi ghi nhận một khoản thu nhập hoặc chi tiêu phát sinh kèm chi phí nếu có)

- Thao tác hệ thống: `recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.4.2 Tra cứu và lọc lịch sử giao dịch

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi truy vấn và lọc danh sách các giao dịch thu chi theo tiêu chí thời gian, ví hoặc danh mục)

- Thao tác hệ thống: `filterTransactions(startDate, endDate, walletId, categoryId)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.4.3 Chỉnh sửa giao dịch thu chi

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi điều chỉnh thuộc tính giao dịch cũ, hoàn nguyên tác động cũ và kiểm soát số dư an toàn)

- Thao tác hệ thống: `updateTransaction(transactionId, walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.4.4 Xóa giao dịch thu chi

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi loại bỏ giao dịch khỏi sổ theo dõi, hoàn nguyên số dư ví và xóa phí kèm theo)

- Thao tác hệ thống: `deleteTransaction(transactionId)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

## 3.5 Nhóm Chức năng Chuyển tiền Nội bộ (TRF)

Nhóm chức năng Chuyển tiền Nội bộ quản lý các giao dịch điều chuyển giá trị giữa các ví nội bộ của người dùng. Các thao tác trong nhóm thực hiện nguyên tắc ghi nhận đồng thời theo `BR-FIN-03`: tự động trừ số dư ví nguồn, cộng số dư ví đích và hạch toán chi phí chuyển tiền phát sinh, đảm bảo số dư ví nguồn không âm theo `BR-FIN-02`.

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---: | :--- | :--- | :--- | :--- |
| `UC-TRF-01` | Chuyển tiền nội bộ giữa hai ví | `transferFunds` | Điều chuyển tiền giữa hai ví theo cơ chế ghi nhận đồng thời kèm chi phí nếu có | Ghi nhận |
| `UC-TRF-02` | Tra cứu lịch sử chuyển tiền | `filterTransfers` | Trích xuất lịch sử các lượt điều chuyển tiền nội bộ và tổng chi phí phát sinh | Truy vấn |
| `UC-TRF-03` | Xóa giao dịch chuyển tiền | `deleteTransfer` | Hủy giao dịch chuyển tiền, hoàn nguyên đồng thời số dư hai ví và xóa chi phí | Xóa |

### 3.5.1 Chuyển tiền giữa hai ví

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi điều chuyển tiền giữa hai ví theo cơ chế ghi nhận đồng thời kèm chi phí phát sinh nếu có)

- Thao tác hệ thống: `transferFunds(sourceWalletId, destinationWalletId, amount, transferDate, note, feeAmount, feeCategoryId)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.5.2 Tra cứu lịch sử chuyển tiền

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi truy vấn danh sách các lượt điều chuyển tiền nội bộ và tổng chi phí phát sinh)

- Thao tác hệ thống: `filterTransfers(startDate, endDate, sourceWalletId, destinationWalletId)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.5.3 Xóa giao dịch chuyển tiền

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi hủy bỏ giao dịch chuyển tiền, hoàn nguyên đồng thời số dư hai ví và xóa chi phí liên kết)

- Thao tác hệ thống: `deleteTransfer(transferId)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

## 3.6 Nhóm Chức năng Danh mục Phân loại (CAT)

Nhóm chức năng Danh mục Phân loại thiết lập hệ thống phân loại thu chi phục vụ theo dõi và phân tích tài chính. Các thao tác trong nhóm vận hành theo cấu trúc phân cấp 3 cấp quy định tại `BR-FIN-04`, bảo đảm tính duy nhất của tên gọi theo `BR-NAM-01` và duy trì tính toàn vẹn lịch sử giao dịch thông qua cơ chế xóa mềm.

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---: | :--- | :--- | :--- | :--- |
| `UC-CAT-01` | Bổ sung danh mục phân loại mới | `addCategory` | Tạo mới danh mục chi tiết thuộc nhóm nhu cầu hợp lệ trong cấu trúc phân cấp | Ghi nhận |
| `UC-CAT-02` | Đổi tên danh mục | `renameCategory` | Thay đổi tên hiển thị của danh mục hoặc nhóm nhu cầu và kiểm tra tính duy nhất | Cập nhật |
| `UC-CAT-03` | Khôi phục hoặc tạo lại mục đã xóa | `restoreCategory` | Phục hồi trạng thái hoạt động cho danh mục đã bị xóa mềm trước đó | Cập nhật |
| `UC-CAT-04` | Xóa danh mục hoặc nhóm | `deleteCategory` | Xóa hoàn toàn danh mục chưa có dữ liệu hoặc xóa mềm nếu đã phát sinh giao dịch | Xóa |

### 3.6.1 Thêm danh mục phân loại mới

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi tạo mới một danh mục chi tiết hoặc nhóm nhu cầu trong cấu trúc phân cấp)

- Thao tác hệ thống: `addCategory(categoryName, parentCategoryId)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.6.2 Đổi tên danh mục

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi thay đổi tên hiển thị của danh mục hoặc nhóm nhu cầu)

- Thao tác hệ thống: `renameCategory(categoryId, newName)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.6.3 Khôi phục danh mục đã xóa

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi phục hồi trạng thái hoạt động cho danh mục đã bị xóa mềm trước đó)

- Thao tác hệ thống: `restoreCategory(categoryId)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.6.4 Xóa danh mục phân loại

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi xóa hoàn toàn hoặc xóa mềm một danh mục hoặc nhóm nhu cầu)

- Thao tác hệ thống: `deleteCategory(categoryId)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

## 3.7 Nhóm Chức năng Thống kê và Báo cáo (REP)

Nhóm chức năng Thống kê và Báo cáo cung cấp các công cụ truy vấn và phân tích số liệu tài chính định kỳ cho người dùng. Các thao tác trong nhóm thực hiện trích xuất chuỗi dữ liệu 12 tháng liên tục, tính toán dòng tiền thuần và cơ cấu chi tiêu theo nhóm nhu cầu theo quy tắc `BR-FIN-01` và `BR-FIN-04` mà không làm thay đổi trạng thái dữ liệu hệ thống.

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---: | :--- | :--- | :--- | :--- |
| `UC-REP-01` | Thống kê dòng tiền 12 tháng gần nhất | `generateCashFlowReport` | Trích xuất chuỗi dữ liệu 12 tháng liên tục, tính dòng tiền thuần và tỷ lệ tiết kiệm | Truy vấn |
| `UC-REP-02` | Phân tích cơ cấu thu chi theo nhóm và danh mục | `generateSpendingStructureReport` | Phân tích cơ cấu chi tiêu theo từng nhóm nhu cầu và danh mục chi tiết trong kỳ | Truy vấn |

### 3.7.1 Báo cáo dòng tiền 12 tháng gần nhất

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi trích xuất chuỗi dòng tiền 12 tháng liên tục và tính toán tỷ lệ tiết kiệm định kỳ)

- Thao tác hệ thống: `generateCashFlowReport(endMonth, endYear)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.7.2 Phân tích cơ cấu thu chi theo nhóm

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi phân tích cơ cấu chi tiêu 2 tầng phân cấp và so sánh biến động với kỳ trước)

- Thao tác hệ thống: `generateSpendingStructureReport(startDate, endDate, flowType)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

## 3.8 Nhóm Chức năng Lưu trữ và Sao lưu Dữ liệu (DAT)

Nhóm chức năng Lưu trữ và Sao lưu Dữ liệu đảm bảo an toàn và tính toàn vẹn cho dữ liệu tài chính của người dùng trên môi trường máy tính cá nhân. Các thao tác trong nhóm chịu trách nhiệm sao lưu nguyên khối dữ liệu ra tệp cục bộ, xác thực và phục hồi an toàn từ bản sao lưu, đồng thời cung cấp công cụ đối soát độc lập để phát hiện sai lệch số dư theo các quy tắc `BR-FIN-02` và `BR-FIN-03`.

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---: | :--- | :--- | :--- | :--- |
| `UC-DAT-01` | Xuất dữ liệu tài chính | `exportBackupData` | Đóng gói toàn bộ dữ liệu tài chính của hệ thống ra tệp sao lưu an toàn | Truy vấn |
| `UC-DAT-02` | Phục hồi dữ liệu hệ thống từ bản sao lưu | `restoreBackupData` | Kiểm tra toàn vẹn tệp sao lưu và nạp lại toàn bộ dữ liệu tài chính vào bộ nhớ | Cập nhật |
| `UC-DAT-03` | Kiểm tra lại dữ liệu hệ thống | `reconcileSystemData` | Duyệt lại toàn bộ lịch sử giao dịch để đối soát độc lập tính cân bằng số dư ví | Đối soát |

### 3.8.1 Xuất dữ liệu sao lưu

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi xuất toàn bộ dữ liệu tài chính ra tệp sao lưu an toàn tại đường dẫn chỉ định)

- Thao tác hệ thống: `exportBackupData(exportPath)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.8.2 Phục hồi dữ liệu từ bản sao lưu

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi kiểm tra tính toàn vẹn và nạp lại toàn bộ dữ liệu từ tệp sao lưu)

- Thao tác hệ thống: `restoreBackupData(backupFilePath)`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)

### 3.8.3 Đối soát toàn vẹn dữ liệu hệ thống

(Mô tả khoảng 1 đến 2 câu về bối cảnh tương tác giữa Người dùng và Hệ thống khi khởi chạy tiến trình đối soát độc lập 4 điều kiện toàn vẹn dữ liệu)

- Thao tác hệ thống: `reconcileSystemData()`
- Kết quả phản hồi: (Nêu dữ liệu trả về khi thành công hoặc thông báo từ chối khi vi phạm quy tắc)

**Danh sách tham số đầu vào**

(Nếu không có danh sách tham số đầu vào thì ghi "Thao tác này không yêu cầu tham số đầu vào")

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| (Nêu tên tham số cần truyền) | (Nêu ý nghĩa nghiệp vụ của tham số) | (Nêu các quy tắc ràng buộc dữ liệu theo BR-NAM, BR-FIN tương ứng trong SRS) |

**Sơ đồ tuần tự hệ thống**

(Vẽ sequence diagram theo use case ở SRS)
