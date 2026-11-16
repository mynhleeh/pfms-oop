# 03. Thiết kế tầng dịch vụ ứng dụng và điều phối nghiệp vụ

Tài liệu này xác lập thiết kế chi tiết cho tầng dịch vụ ứng dụng (`com.pfms.service`) của hệ thống Quản lý Tài chính Cá nhân (PFMS), chuyển đổi các ca sử dụng từ mô hình phân tích sang cấu trúc các lớp dịch vụ điều phối luồng nghiệp vụ trên Java.

Thiết kế tập trung giải quyết ba mục tiêu kỹ thuật trọng tâm: điều phối vòng đời và luồng thực thi ca sử dụng làm cầu nối giữa giao diện và thực thể miền; phân tách rạch ròi giữa giao diện công khai (`com.pfms.service`) và lớp cài đặt (`com.pfms.service.impl`); và bảo đảm tính nguyên tử của dữ liệu tài chính qua kho lưu trữ trừu tượng, ủy quyền toàn bộ phép tính số học cho tầng nghiệp vụ cốt lõi.

## 3.1 Tổng quan và danh mục dịch vụ ứng dụng

Mục này xác lập cấu trúc phân tách giữa giao diện công khai và lớp cài đặt nội bộ của tầng dịch vụ, đồng thời cung cấp bảng danh mục đối chiếu các dịch vụ điều phối trong toàn bộ hệ thống.

Cấu trúc mã nguồn của tầng dịch vụ được phân định rạch ròi thành hai không gian tên:
- **Giao diện công khai (`com.pfms.service`):** Định nghĩa toàn bộ các hợp đồng phương thức mà tầng trình diễn (`com.pfms.presentation`) được phép triệu gọi. Tầng trình diễn chỉ phụ thuộc duy nhất vào các giao diện này, hoàn toàn không được phép tiếp xúc với lớp cài đặt cụ thể hay các thực thể nghiệp vụ.
- Lớp cài đặt dịch vụ (`com.pfms.service.impl.*`): Hiện thực hóa các giao diện dịch vụ dưới dạng lớp `public` trong các gói con theo từng phân hệ để hỗ trợ khởi tạo và tiêm phụ thuộc, còn các lớp trợ giúp và điều phối chuyên biệt bên trong mỗi phân hệ giữ phạm vi truy cập nội bộ gói (`package-private`). Lớp cài đặt tiếp nhận đối tượng truyền dữ liệu (DTO), tiêm các giao diện kho lưu trữ (`com.pfms.domain.repository`) qua hàm khởi tạo để truy xuất dữ liệu, và ủy quyền các phép tính toán số học cho các thực thể thuộc tầng nghiệp vụ.

**Bảng tổng hợp danh mục dịch vụ ứng dụng:**

| Dịch vụ | Giao diện công khai | Lớp cài đặt nội bộ | Trách nhiệm điều phối chính |
| :--- | :--- | :--- | :--- |
| **Quản lý ví** | `WalletService` | `WalletServiceImpl` | Điều phối tạo ví mới, cập nhật thông tin, điều chỉnh số dư kiểm kê thực tế và tính toán cơ cấu phân bổ dòng tiền. |
| **Ghi nhận thu chi** | `TransactionService` | `TransactionServiceImpl` | Điều phối ghi nhận thu chi kèm chi phí giao dịch, cập nhật số dư ví tức thời và thực hiện quy trình hoàn nguyên hai pha khi sửa hoặc xóa. |
| **Chuyển tiền nội bộ** | `TransferService` | `TransferServiceImpl` | Điều phối chuyển khoản hai chiều giữa hai ví, hạch toán chi phí phát sinh và bảo toàn tổng tài sản hệ thống. |
| **Danh mục phân loại** | `CategoryService` | `CategoryServiceImpl` | Quản lý cấu trúc cây danh mục 3 cấp, kiểm soát tính duy nhất của tên và thực thi cơ chế xóa mềm bảo toàn lịch sử giao dịch. |
| **Báo cáo và thống kê** | `ReportService` | `ReportServiceImpl` | Vận hành ở chế độ chỉ đọc: trích xuất dữ liệu, tổng hợp dòng tiền 12 tháng, đo lường tỷ lệ tiết kiệm và phân tích cơ cấu thu chi. |
| **Quản trị và sao lưu** | `DataManagementService` | `DataManagementServiceImpl` | Điều phối xuất dữ liệu JSON, phục hồi dữ liệu hệ thống theo quy trình 4 bước và đối soát độc lập 4 điều kiện toàn vẹn tài chính. |

## 3.2 Dịch vụ quản lý ví (WalletService)

Dịch vụ này điều phối toàn bộ vòng đời ví thanh toán của người dùng, từ khởi tạo ví mặc định và ví tạo thêm, cập nhật thông tin và trạng thái sử dụng cho đến kiểm kê điều chỉnh số dư thực tế và tổng hợp cơ cấu phân bổ tài sản. Toàn bộ phép toán số học và kiểm soát bất biến số dư được ủy quyền cho thực thể `Wallet`, `BalanceAdjustmentRecord` cùng các đối tượng giá trị `Money` và `TransactionDate`.

**Giao diện công khai:**

Giao diện `com.pfms.service.WalletService` cung cấp các phương thức sau cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `initializeDefaultWallet` | `Không có` | `void` | `StorageException` | Khởi tạo ví mặc định `"Tiền mặt"` (ACTIVE, số dư 0.00 VNĐ) khi khởi động nếu chưa tồn tại. |
| `createWallet` | `CreateWalletRequestDTO request` | `WalletResponseDTO` | `BusinessException`, `DuplicateEntityException`, `StorageException` | Xác thực tên ví (1–50 ký tự), kiểm tra trùng tên và tạo ví mới (ACTIVE, số dư 0.00 VNĐ). |
| `getWalletsSummary` | `EntityStatus statusFilter` hoặc `String statusFilter` hoặc không tham số | `WalletsSummaryResponseDTO` | `StorageException` | Truy xuất danh sách ví theo bộ lọc (`"ACTIVE"`, `"ARCHIVED"`, `"ALL"`), tính tổng số dư và tỷ trọng phân bổ của từng ví ở chế độ chỉ đọc. |
| `updateWallet` | `UpdateWalletRequestDTO request` | `WalletResponseDTO` | `EntityNotFoundException`, `BusinessException`, `DuplicateEntityException`, `StorageException` | Xác thực tên mới không trùng và cập nhật tên cùng ghi chú ví, bảo toàn số dư hiện có. |
| `archiveWallet` | `UUID walletId` | `WalletResponseDTO` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Chuyển ví đang hoạt động (không phải ví mặc định) sang trạng thái lưu trữ (`ARCHIVED`). |
| `activateWallet` | `UUID walletId` | `WalletResponseDTO` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Khôi phục ví đang lưu trữ (`ARCHIVED`) về trạng thái hoạt động bình thường (`ACTIVE`). |
| `deleteWallet` | `UUID walletId` | `void` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Xóa vĩnh viễn ví không phải mặc định khi chưa từng phát sinh giao dịch hoặc bản ghi kiểm kê. |
| `adjustBalance` | `AdjustBalanceRequestDTO request` | `BalanceAdjustmentResponseDTO` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Cập nhật số dư ví theo kiểm kê thực tế và lập bản ghi `BalanceAdjustmentRecord` bất biến lưu vết kiểm toán. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.wallet.WalletServiceImpl` nhận `WalletRepository`, `BalanceAdjustmentRepository` và `TransactionRepository` qua hàm khởi tạo. Trước khi gọi thực thể miền, dịch vụ chuyển đổi các giá trị thô từ DTO sang `Money` và `TransactionDate` để kích hoạt kiểm tra chuẩn tài chính và lịch pháp ngay ở đầu luồng điều phối.

**Phương thức `initializeDefaultWallet`:**

- Kiểm tra danh sách ví hiện có trong `WalletRepository`; nếu đã tồn tại ví đánh dấu mặc định thì dừng xử lý mà không ghi đè dữ liệu.
- Nếu chưa có ví mặc định khi khởi tạo hệ thống lần đầu, sinh mã `UUID` và khởi tạo thực thể `Wallet` với tên `"Tiền mặt"`, cờ `isDefault` mang giá trị `true`, trạng thái `ACTIVE` và số dư `Money.ZERO`.
- Lưu ví mặc định vào `WalletRepository` và chuyển tiếp `StorageException` nếu thao tác ghi tệp gặp sự cố.
- Hoàn tất khởi tạo để tầng trình diễn sẵn sàng mở phiên làm việc.

**Phương thức `createWallet`:**

- Cắt khoảng trắng hai đầu tên ví và ghi chú; từ chối nếu tên rỗng, dài quá 50 ký tự hoặc ghi chú vượt quá 255 ký tự, sau đó truy vấn `WalletRepository` để chặn trường hợp trùng tên không phân biệt hoa thường trên cả ví đang hoạt động lẫn ví đã lưu trữ.
- Sinh mã định danh `UUID` và khởi tạo thực thể `Wallet` với cờ `isDefault` mang giá trị `false`, trạng thái `ACTIVE` và số dư khởi điểm `Money.ZERO`.
- Ghi nhận thực thể mới vào `WalletRepository` và bảo đảm giữ nguyên trạng thái dữ liệu nếu thao tác lưu phát sinh `StorageException`.
- Ánh xạ thông tin ví vừa tạo sang `WalletResponseDTO` để trả về tầng trình diễn.

**Phương thức `getWalletsSummary`:**

- Tiếp nhận bộ lọc trạng thái (hỗ trợ `EntityStatus`, chuỗi lọc `String statusFilter` như `"ACTIVE"`, `"ARCHIVED"`, `"ALL"`, hoặc phương thức nạp mặc định không tham số cho ví `ACTIVE`) và nạp danh sách ví từ `WalletRepository`.
- Tính tổng số dư toàn hệ thống bằng `Money` trên tất cả ví `ACTIVE` cùng các ví `ARCHIVED` có số dư dương; giữ nguyên cơ chế chỉ đọc, không thay đổi trạng thái bất kỳ thực thể nào.
- Tính tỷ trọng phần trăm từng ví với tối thiểu 4 chữ số thập phân trung gian rồi làm tròn nửa chẵn về 2 chữ số; gán `0.00%` cho mọi ví khi tổng tài sản bằng không, loại các ví `ARCHIVED` có số dư bằng không khỏi bảng cơ cấu và sắp xếp bảng cơ cấu theo tỷ trọng giảm dần.
- Đóng gói danh sách ví đã lọc, tổng số dư toàn hệ thống và bảng cơ cấu vào `WalletsSummaryResponseDTO`.

**Phương thức `updateWallet`:**

- Nạp ví theo mã định danh từ `WalletRepository`, ném `EntityNotFoundException` nếu không tồn tại; chuẩn hóa và kiểm tra định dạng tên mới cùng ghi chú mới.
- Chỉ kiểm tra trùng tên không phân biệt hoa thường khi tên mới sau chuẩn hóa khác tên hiện tại nhằm cho phép giữ nguyên tên cũ khi chỉ sửa ghi chú.
- Ủy quyền phương thức `updateInfo` của `Wallet` để cập nhật tên và ghi chú trong khi bảo toàn mã định danh và số dư hiện có, sau đó lưu lại qua `WalletRepository`.
- Chuyển thực thể sau cập nhật sang `WalletResponseDTO`.

**Phương thức `archiveWallet` và `activateWallet`:**

- Nạp thực thể `Wallet` từ `WalletRepository` theo mã định danh và xác nhận ví tồn tại.
- Ủy quyền `wallet.archive()` hoặc `wallet.activate()` kiểm tra điều kiện chuyển trạng thái, trong đó `archive()` tự động từ chối nếu đối tượng là ví `"Tiền mặt"` mặc định hoặc đang không ở trạng thái `ACTIVE`.
- Lưu trạng thái vòng đời mới xuống `WalletRepository` và giữ nguyên số dư hiện hành của ví.
- Trả về `WalletResponseDTO` phản ánh trạng thái mới để tầng trình diễn cập nhật hiển thị.

**Phương thức `deleteWallet`:**

- Nạp ví từ `WalletRepository` theo mã định danh và từ chối ngay nếu đây là ví `"Tiền mặt"` mặc định.
- Kiểm tra lịch sử liên quan qua `TransactionRepository.findByWalletId` và `BalanceAdjustmentRepository.findByWalletId`; từ chối xóa nếu ví đã có ít nhất một giao dịch thu chi, chuyển tiền hoặc bản ghi kiểm kê số dư.
- Gọi `deleteById` trên `WalletRepository` để xóa vĩnh viễn ví rỗng lịch sử và giải phóng tên ví khỏi tập kiểm tra duy nhất.
- Kết thúc xử lý mà không trả về dữ liệu.

**Phương thức `adjustBalance`:**

- Chuyển đổi số dư thực tế sang `Money` (yêu cầu đúng 2 chữ số thập phân và không âm), chuyển đổi ngày kiểm kê sang `TransactionDate` (mặc định là ngày hiện tại nếu chưa nhập và không vượt quá hiện tại), đồng thời chuẩn hóa lý do điều chỉnh dài tối đa 255 ký tự.
- Nạp ví từ `WalletRepository`, xác nhận ví đang ở trạng thái `ACTIVE`, rồi gọi `wallet.adjustBalance(...)` để kiểm tra độ lệch phải khác không, cập nhật số dư ví và sinh bản ghi `BalanceAdjustmentRecord` bất biến.
- Lưu đồng thời `Wallet` qua `WalletRepository` và bản ghi kiểm kê qua `BalanceAdjustmentRepository`; nếu thao tác lưu sau phát sinh `StorageException`, khôi phục lại trạng thái ví trước kiểm kê nhằm giữ đồng bộ giữa số dư và vết kiểm toán.
- Đóng gói số dư trước kiểm kê, số dư sau kiểm kê và phần chênh lệch vào `BalanceAdjustmentResponseDTO`.

## 3.3 Dịch vụ ghi nhận thu chi (TransactionService)

Dịch vụ này điều phối nghiệp vụ ghi nhận khoản thu nhập hoặc chi tiêu hàng ngày, tra cứu lịch sử giao dịch theo nhiều tiêu chí, chỉnh sửa giao dịch theo quy trình hoàn nguyên an toàn và xóa giao dịch kèm chi phí phát sinh. Mọi phép tính số dư và ràng buộc hợp thành của phí giao dịch được giao cho `IncomeExpenseTransaction`, `TransactionFee`, `TransactionFactory`, `Money` và `TransactionDate`.

**Giao diện công khai:**

Giao diện `com.pfms.service.TransactionService` cung cấp các phương thức sau cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `recordIncomeExpense` | `RecordTransactionRequestDTO request` | `TransactionResponseDTO` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Xác thực ví và danh mục, tạo giao dịch thu chi kèm phụ phí (nếu có) và cập nhật số dư ví tức thời. |
| `filterTransactions` | `FilterTransactionsRequestDTO request` | `TransactionListResponseDTO` | `BusinessException`, `StorageException` | Truy vấn, lọc và phân trang danh sách giao dịch thu chi kèm phí liên kết; tổng hợp dòng tiền ở chế độ chỉ đọc. |
| `updateTransaction` | `UpdateTransactionRequestDTO request` | `TransactionResponseDTO` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Hoàn nguyên giao dịch cũ, kiểm tra điều kiện số dư ví mới và cập nhật giao dịch cùng phí liên kết. |
| `deleteTransaction` | `UUID transactionId` | `void` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Kiểm tra điều kiện không âm ví, hoàn nguyên số dư và xóa vĩnh viễn giao dịch cùng phí liên kết. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.transaction.TransactionServiceImpl` nhận `TransactionRepository`, `WalletRepository` và `CategoryRepository` qua hàm khởi tạo. Do `TransactionRepository` lưu trữ đa hình nhiều loại giao dịch, lớp này luôn lọc đúng kiểu `IncomeExpenseTransaction` trong mọi nghiệp vụ thu chi và giữ nguyên bất biến loại thu chi của giao dịch sau khi tạo.

**Phương thức `recordIncomeExpense`:**

- Chuyển đổi số tiền và mức phí sang `Money` (yêu cầu số tiền lớn hơn `0.00 VNĐ` và mức phí không âm), chuyển đổi ngày phát sinh sang `TransactionDate` (không vượt quá hiện tại) và chuẩn hóa ghi chú tối đa 255 ký tự; nếu mức phí dương thì bắt buộc có mã danh mục phí.
- Nạp ví từ `WalletRepository` bảo đảm đang `ACTIVE`; nạp danh mục cấp 3 từ `CategoryRepository` bảo đảm đang `ACTIVE` (và danh mục chi tiêu cấp 3 đang `ACTIVE` cho phí nếu phát sinh); gọi `TransactionFactory` tạo `IncomeExpenseTransaction` kèm `TransactionFee` hợp thành rồi thực thi `applyToWallet()` sau khi xác nhận số dư ví sau hạch toán không bị âm.
- Lưu bản ghi giao dịch vào `TransactionRepository` và lưu ví đã đổi số dư vào `WalletRepository`; nếu bước lưu thứ hai ném `StorageException`, thực hiện xóa bù trừ giao dịch vừa lưu và hoàn tác trạng thái ví trước khi chuyển tiếp ngoại lệ.
- Chuyển thực thể giao dịch cùng số dư ví mới sang `TransactionResponseDTO`.

**Phương thức `filterTransactions`:**

- Chuyển đổi mốc ngày bắt đầu và ngày kết thúc (nếu có) sang `TransactionDate` để kiểm tra ngày bắt đầu không đứng sau ngày kết thúc; kiểm tra chỉ số trang tối thiểu bằng 1 và kích thước trang trong đoạn từ 10 đến 100 bản ghi (mặc định 50 nếu chưa chỉ định).
- Nạp toàn bộ bản ghi thuộc kiểu `IncomeExpenseTransaction` từ `TransactionRepository`, áp dụng bộ lọc theo tập ví, loại giao dịch, danh mục hoặc nhóm nhu cầu (bao gồm các danh mục đã xóa mềm) và từ khóa tìm kiếm không phân biệt hoa thường trên ghi chú; tách phần `TransactionFee` hợp thành của từng giao dịch thành dòng hiển thị phụ thuộc và sắp xếp theo ngày giao dịch giảm dần rồi đến mốc tạo giảm dần.
- Thực thi phân trang trên tập kết quả và dùng `Money` tính tổng thu nhập, tổng chi tiêu (bao gồm cả các khoản phí giao dịch liên kết) cùng dòng tiền thuần hoàn toàn ở chế độ chỉ đọc.
- Đóng gói dữ liệu trang hiện tại, thông số phân trang và ba chỉ số dòng tiền vào `TransactionListResponseDTO`.

**Phương thức `updateTransaction`:**

- Nạp bản ghi theo mã định danh và xác nhận đây là `IncomeExpenseTransaction`, từ chối mọi yêu cầu sửa trực tiếp đối với `TransactionFee`; chuyển đổi số tiền mới, phí mới sang `Money` (số tiền dương, phí không âm), chuyển đổi ngày mới sang `TransactionDate` và chuẩn hóa ghi chú tối đa 255 ký tự.
- Nạp ví mới đang `ACTIVE`; kiểm tra danh mục mới tương thích với `transactionType` bất biến của giao dịch (cho phép giữ danh mục cũ đã xóa mềm nếu không thay đổi, nhưng bắt buộc danh mục mới phải đang `ACTIVE` nếu có đổi), đồng thời kiểm tra danh mục phí mới thuộc nhánh chi tiêu khi phí mới dương; mô phỏng hai pha hoàn nguyên ví cũ rồi áp dụng dữ liệu mới để bảo đảm cả hai ví đều không âm trước khi gọi `updateDetails(...)` (tự động tạo, sửa hoặc gỡ `TransactionFee` tương ứng).
- Lưu giao dịch vào `TransactionRepository` cùng ví cũ và ví mới (nếu đổi ví) vào `WalletRepository`; nếu phát sinh `StorageException` giữa quá trình lưu nhiều thực thể, khôi phục lại ảnh chụp trạng thái trước thao tác cho giao dịch và các ví liên quan.
- Ánh xạ kết quả sau cập nhật sang `TransactionResponseDTO`.

**Phương thức `deleteTransaction`:**

- Nạp bản ghi từ `TransactionRepository` theo mã định danh và xác nhận là `IncomeExpenseTransaction` chứ không phải bản ghi `TransactionFee`.
- Nạp ví liên kết đang `ACTIVE`; kiểm tra trước khả năng hoàn nguyên đối với giao dịch thu nhập để bảo đảm số dư ví sau khi trừ khoản thu và cộng trả khoản phí không bị âm, rồi gọi `revertFromWallet()`.
- Xóa bản ghi giao dịch khỏi `TransactionRepository` (xóa đồng thời `TransactionFee` hợp thành để ngăn bản ghi mồ côi) và lưu lại số dư ví đã hoàn nguyên vào `WalletRepository`; nếu thao tác lưu ví gặp `StorageException`, khôi phục lại bản ghi giao dịch và trạng thái ví ban đầu.
- Kết thúc xử lý mà không trả về dữ liệu.

## 3.4 Dịch vụ chuyển tiền nội bộ (TransferService)

Dịch vụ này điều phối việc luân chuyển dòng tiền giữa hai ví độc lập trong hệ thống, tra cứu lịch sử chuyển tiền theo bộ lọc và hủy hoàn tác giao dịch chuyển tiền khi cần thiết. Quá trình xử lý bảo đảm tổng tài sản giữa các ví luôn được bảo toàn sau khi trừ phần phí phát sinh thực tế, đồng thời duy trì số dư không âm tại cả hai đầu giao dịch.

**Giao diện công khai:**

Giao diện `com.pfms.service.TransferService` cung cấp các phương thức sau cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `transferFunds` | `TransferFundsRequestDTO request` | `TransferResponseDTO` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Kiểm tra hai ví phân biệt, đủ số dư chi trả chuyển khoản và phí; chuyển tiền nguyên tử và lưu giao dịch. |
| `filterTransfers` | `FilterTransfersRequestDTO request` | `TransferListResponseDTO` | `BusinessException`, `StorageException` | Tra cứu, lọc và phân trang lịch sử chuyển tiền theo khoảng ngày hoặc cặp ví nguồn/đích ở chế độ chỉ đọc. |
| `deleteTransfer` | `UUID transferId` | `void` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Kiểm tra ví đích đủ số dư thu hồi, hoàn nguyên số dư hai chiều cả hai ví và xóa bản ghi chuyển tiền cùng phí. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.transfer.TransferServiceImpl` nhận `TransactionRepository`, `WalletRepository` và `CategoryRepository` qua hàm khởi tạo. Mọi bản ghi chuyển tiền được lưu trữ đa hình trong `TransactionRepository` dưới dạng thực thể `TransferTransaction` kèm phần `TransactionFee` hợp thành gắn với `sourceWallet`.

**Phương thức `transferFunds`:**

- Kiểm tra mã ví nguồn khác mã ví đích, chuyển đổi số tiền chuyển và phí sang `Money` (số tiền lớn hơn `0.00 VNĐ`, phí không âm), chuyển đổi ngày chuyển sang `TransactionDate` (không vượt quá hiện tại) và chuẩn hóa ghi chú tối đa 255 ký tự; nếu mức phí dương thì bắt buộc kèm mã danh mục chi tiêu cấp 3.
- Nạp cả hai thực thể `Wallet` bảo đảm đều ở trạng thái `ACTIVE`, xác nhận ví nguồn đủ chi trả tổng tiền chuyển cộng phí thông qua `Money` và `hasSufficientBalance`, sau đó dùng `TransactionFactory` khởi tạo `TransferTransaction` (kèm `TransactionFee` nếu phí dương, với danh mục chi tiêu cấp 3 đang `ACTIVE`) và gọi `applyToWallet()` để trừ ví nguồn và cộng ví đích.
- Lưu đồng thời ví nguồn, ví đích qua `WalletRepository` và giao dịch mới qua `TransactionRepository`; nếu xảy ra `StorageException` tại bất kỳ lệnh lưu nào, thực hiện khôi phục ngay trạng thái trước giao dịch của cả hai ví và xóa bỏ bản ghi chưa hoàn tất để giữ nguyên tổng tài sản.
- Đóng gói thông tin giao dịch chuyển tiền và số dư mới của hai ví vào `TransferResponseDTO`.

**Phương thức `filterTransfers`:**

- Chuyển đổi mốc ngày bắt đầu và ngày kết thúc (nếu có) sang `TransactionDate` để kiểm tra ngày bắt đầu không đứng sau ngày kết thúc; kiểm tra chỉ số trang tối thiểu bằng 1 và kích thước trang trong đoạn từ 10 đến 100 bản ghi (mặc định 50 nếu chưa chỉ định).
- Nạp các thực thể thuộc kiểu `TransferTransaction` từ `TransactionRepository`, lọc theo khoảng ngày, mã ví nguồn và mã ví đích; sắp xếp kết quả theo ngày chuyển tiền giảm dần rồi đến mốc tạo giảm dần, đồng thời tra cứu tên ví nguồn và ví đích qua `WalletRepository` ở chế độ chỉ đọc.
- Thực thi phân trang trên tập kết quả và ủy quyền đối tượng giá trị `Money` tính tổng số tiền đã chuyển cùng tổng chi phí phát sinh trên toàn bộ tập kết quả thỏa mãn điều kiện lọc.
- Đóng gói danh sách giao dịch chuyển tiền của trang hiện tại, thông số phân trang và hai chỉ số tổng hợp vào `TransferListResponseDTO`.

**Phương thức `deleteTransfer`:**

- Nạp bản ghi theo mã định danh từ `TransactionRepository` và xác nhận đúng là thể hiện của `TransferTransaction`.
- Nạp ví nguồn và ví đích từ `WalletRepository` bảo đảm cả hai đang `ACTIVE`; kiểm tra số dư khả dụng của ví đích tối thiểu bằng số tiền gốc đã chuyển để tránh âm ví, sau đó gọi `revertFromWallet()` để trừ tiền ví đích và cộng trả tiền chuyển cùng khoản phí về ví nguồn.
- Xóa `TransferTransaction` cùng `TransactionFee` hợp thành khỏi `TransactionRepository` và lưu trạng thái mới của cả hai ví vào `WalletRepository`; nếu phát sinh `StorageException` trong chuỗi ghi dữ liệu, hoàn nguyên ảnh chụp trạng thái của cả hai ví và bản ghi giao dịch.
- Kết thúc xử lý mà không trả về dữ liệu.

## 3.5 Dịch vụ danh mục phân loại (CategoryService)

Dịch vụ Danh mục Phân loại đảm nhiệm điều phối các ca sử dụng quản lý hệ thống phân loại thu chi 3 cấp (Loại thu chi cố định, Nhóm nhu cầu và Danh mục chi tiết), kiểm soát tính hợp lệ và tính duy nhất của tên gọi theo quy tắc nghiệp vụ. Đồng thời, dịch vụ chịu trách nhiệm thực thi cơ chế xóa mềm và khôi phục mục phân loại nhằm bảo toàn tuyệt đối tính toàn vẹn tham chiếu lịch sử cho toàn bộ các giao dịch tài chính.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-04`, `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`, `ERR-CAT-004`, `ERR-CAT-005`

**Giao diện công khai:**

Giao diện `com.pfms.service.CategoryService` cung cấp các phương thức cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `createNeedGroup` | `CreateNeedGroupRequestDTO request` | `NeedGroupResponseDTO` | `BusinessException`<br/>`DuplicateEntityException`<br/>`StorageException` | Kiểm tra tên và tính duy nhất trong cùng loại thu chi (`INCOME`/`EXPENSE`); tạo `NeedGroup` mới (`ACTIVE`) kèm biểu tượng, mã màu. |
| `renameNeedGroup` | `RenameNeedGroupRequestDTO request` | `NeedGroupResponseDTO` | `EntityNotFoundException`<br/>`BusinessException`<br/>`DuplicateEntityException`<br/>`StorageException` | Xác thực nhóm đang `ACTIVE`, kiểm tra tên mới không trùng trong cùng loại thu chi; cập nhật tên, biểu tượng và mã màu. |
| `restoreNeedGroup` | `UUID needGroupId` | `NeedGroupResponseDTO` | `EntityNotFoundException`<br/>`BusinessException`<br/>`DuplicateEntityException`<br/>`StorageException` | Xác thực nhóm đang `DELETED`, kiểm tra tên không trùng với nhóm `ACTIVE` cùng loại; khôi phục nhóm về `ACTIVE`. |
| `deleteNeedGroup` | `UUID needGroupId` | `void` | `EntityNotFoundException`<br/>`BusinessException`<br/>`StorageException` | Từ chối nếu là nhóm duy nhất còn lại (`ERR-CAT-002`); xóa mềm danh mục con đã có giao dịch, xóa cứng mục con chưa có giao dịch và xóa nhóm cha tương ứng. |
| `createCategory` | `CreateCategoryRequestDTO request` | `CategoryResponseDTO` | `BusinessException`<br/>`EntityNotFoundException`<br/>`DuplicateEntityException`<br/>`StorageException` | Kiểm tra nhóm cha `ACTIVE`, tên hợp lệ và không trùng trong nhóm; tạo `Category` mới (`ACTIVE`) kế thừa loại thu chi từ nhóm cha. |
| `renameCategory` | `RenameCategoryRequestDTO request` | `CategoryResponseDTO` | `EntityNotFoundException`<br/>`BusinessException`<br/>`DuplicateEntityException`<br/>`StorageException` | Kiểm tra tên (1–50 ký tự), nhóm đích `ACTIVE` cùng loại; cập nhật tên hoặc chuyển nhóm mà không trùng lặp, bảo toàn lịch sử. |
| `restoreCategory` | `UUID categoryId` | `CategoryResponseDTO` | `EntityNotFoundException`<br/>`DuplicateEntityException`<br/>`BusinessException`<br/>`StorageException` | Kiểm tra nhóm cha đang `ACTIVE` và tên không trùng; khôi phục `Category` từ `DELETED` về `ACTIVE`. |
| `deleteCategory` | `UUID categoryId` | `void` | `EntityNotFoundException`<br/>`BusinessException`<br/>`StorageException` | Xác thực danh mục đang `ACTIVE`; xóa cứng nếu chưa phát sinh giao dịch, hoặc chuyển sang `DELETED` (xóa mềm) nếu đã có lịch sử. |
| `getCategoryHierarchy` | Không có | `List<CategoryHierarchyResponseDTO>` | Không có | Truy xuất toàn bộ cây danh mục phân loại 3 cấp (Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết) gồm cả `ACTIVE` và `DELETED`. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.category.CategoryServiceImpl` nhận `CategoryRepository`, `NeedGroupRepository` và `TransactionRepository` qua hàm khởi tạo để xử lý logic:

**Phương thức `createNeedGroup`:**

- Kiểm tra loại thu chi hợp lệ (`INCOME` hoặc `EXPENSE`) và chuẩn hóa tên nhóm nhu cầu qua `EntityName`; nếu rỗng hoặc vượt ngưỡng độ dài cho phép thì ném `BusinessException`.
- Truy vấn `NeedGroupRepository.existsByNameAndType(...)` để từ chối trùng tên trong cùng loại thu chi bằng `DuplicateEntityException`.
- Khởi tạo `NeedGroup` mới ở trạng thái `ACTIVE` kèm biểu tượng, mã màu hiển thị, lưu vào `NeedGroupRepository` và ánh xạ sang `NeedGroupResponseDTO`.

**Phương thức `renameNeedGroup`:**

- Nạp nhóm nhu cầu từ `NeedGroupRepository` và bảo đảm nhóm đang ở trạng thái `ACTIVE`, kiểm tra tính hợp lệ và tính duy nhất của tên mới qua `existsByNameAndTypeAndIdNot(...)`.
- Ủy quyền thực thể `NeedGroup` cập nhật tên mới và biểu tượng, mã màu (nếu có yêu cầu cập nhật giao diện), lưu lại vào `NeedGroupRepository` và trả về `NeedGroupResponseDTO`.

**Phương thức `restoreNeedGroup`:**

- Nạp nhóm nhu cầu từ `NeedGroupRepository` và xác thực nhóm đang ở trạng thái `DELETED`; kiểm tra tên khôi phục không xung đột với nhóm nào đang `ACTIVE` trong cùng loại thu chi.
- Gọi `restore()` trên `NeedGroup`, lưu trạng thái mới và trả về `NeedGroupResponseDTO`.

**Phương thức `deleteNeedGroup`:**

- Nạp nhóm nhu cầu đang hoạt động và kiểm tra `NeedGroupRepository.countActiveByType(...)`; từ chối bằng `BusinessException` (`ERR-CAT-002`) nếu đây là nhóm hoạt động cuối cùng của nhánh thu chi tương ứng.
- Quét toàn bộ danh mục con của nhóm qua `CategoryRepository.findByNeedGroupId(...)`: với danh mục đã phát sinh giao dịch trong `TransactionRepository`, chuyển sang trạng thái `DELETED` (nếu còn hoạt động); với danh mục chưa từng phát sinh giao dịch, thực hiện xóa cứng. Nếu tồn tại danh mục con bị xóa mềm thì giữ nhóm cha ở trạng thái `DELETED`, ngược lại xóa cứng nhóm cha khỏi `NeedGroupRepository`.

**Phương thức `createCategory`:**

- Làm sạch và xác thực tên danh mục qua `EntityName` (ném `BusinessException` nếu vi phạm quy tắc đặt tên); nạp nhóm nhu cầu cha qua `NeedGroupRepository` (ném `EntityNotFoundException` nếu không tìm thấy, `BusinessException` mang `ERR-CAT-005` nếu nhóm cha đã xóa) và kiểm tra trùng tên qua `CategoryRepository.existsByNameAndNeedGroupId(...)` (`DuplicateEntityException`).
- Khởi tạo thực thể `Category` mới ở trạng thái `ACTIVE`, kế thừa loại thu chi cố định từ nhóm cha, lưu vào `CategoryRepository` và trả về `CategoryResponseDTO`.

**Phương thức `renameCategory`:**

- Kiểm tra tên mới qua `EntityName`, nạp danh mục đang hoạt động qua `CategoryRepository`; nếu có yêu cầu đổi nhóm nhu cầu cha thì kiểm tra nhóm đích tồn tại, đang `ACTIVE` (`ERR-CAT-003` nếu đã xóa) và cùng loại thu chi (`ERR-CAT-001` nếu khác loại).
- Kiểm tra không trùng tên trong nhóm đích qua `existsByNameAndNeedGroupIdAndIdNot(...)`, cập nhật nhóm cha (nếu đổi) và tên trên thực thể `Category`, lưu cập nhật và trả về `CategoryResponseDTO`.

**Phương thức `restoreCategory`:**

- Nạp danh mục đang ở trạng thái `DELETED` qua `CategoryRepository`; nạp nhóm nhu cầu cha và từ chối nếu nhóm cha đang ở trạng thái `DELETED` (`ERR-CAT-004`).
- Kiểm tra tên danh mục không trùng với danh mục đang `ACTIVE` trong cùng nhóm cha, gọi `restore(...)`, lưu cập nhật và trả về `CategoryResponseDTO`.

**Phương thức `deleteCategory`:**

- Nạp danh mục đang hoạt động từ `CategoryRepository` (từ chối nếu không tìm thấy hoặc đã bị xóa); kiểm tra lịch sử giao dịch qua `TransactionRepository.existsByCategoryId(...)`: nếu đã phát sinh giao dịch thì chuyển sang trạng thái `DELETED` (xóa mềm) để bảo toàn sổ cái và báo cáo lịch sử, ngược lại xóa cứng khỏi `CategoryRepository`.

**Phương thức `getCategoryHierarchy`:**

- Nạp toàn bộ danh sách nhóm nhu cầu từ `NeedGroupRepository` và danh mục từ `CategoryRepository` (bao gồm cả trạng thái `ACTIVE` và `DELETED`).
- Gom cụm danh mục chi tiết trực thuộc từng nhóm nhu cầu theo cấu trúc phân cấp và trả về danh sách `CategoryHierarchyResponseDTO`.

## 3.6 Dịch vụ báo cáo và thống kê tài chính (ReportService)

Dịch vụ Báo cáo và Thống kê Tài chính vận hành thuần túy ở chế độ chỉ đọc, chịu trách nhiệm truy xuất dữ liệu từ các kho lưu trữ để tổng hợp chuỗi thời gian dòng tiền 12 tháng liên tục, đo lường dòng tiền thuần và tỷ lệ tiết kiệm theo tháng và bình quân năm. Đồng thời, dịch vụ phân tích cơ cấu phân bổ thu chi 2 cấp theo nhóm nhu cầu và danh mục chi tiết trong kỳ xác định, bảo đảm độ chính xác số học tài chính mà không làm biến đổi bất kỳ trạng thái dữ liệu nào của hệ thống.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-01`, `UC-REP-02`
- Yêu cầu chức năng: `FR-REP-01`, `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi: `ERR-REP-001`, `ERR-FIN-001`, `ERR-FIN-002`

**Giao diện công khai:**

Giao diện `com.pfms.service.ReportService` cung cấp các phương thức cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `generateCashFlowReport` | `CashFlowReportRequestDTO request` | `CashFlowReportResponseDTO` | `BusinessException` | Thống kê chuỗi dòng tiền 12 tháng liên tục: tổng hợp thu/chi (kèm phí), tính dòng tiền thuần (`NetCashFlow`), tỷ lệ tiết kiệm (`SavingsRate`) và các chỉ số bình quân năm. |
| `generateSpendingStructureReport` | `SpendingStructureReportRequestDTO request` | `SpendingStructureReportResponseDTO` | `BusinessException` | Phân tích cơ cấu 2 cấp theo nhóm nhu cầu và danh mục trong khoảng ngày: tính tỷ trọng (`SpendingStructureShare`), sắp xếp giảm dần và so sánh biến động kỳ liền kề. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.report.ReportServiceImpl` nhận `TransactionRepository`, `CategoryRepository` và `NeedGroupRepository` qua hàm khởi tạo để xử lý logic:

Dịch vụ vận hành hoàn toàn ở chế độ chỉ đọc (Read-only), tuyệt đối không làm thay đổi số dư ví, không chỉnh sửa bản ghi tài chính và không thực hiện thao tác ghi hay sửa đổi dữ liệu lưu trữ. Quy trình 4 bước được chuẩn hóa phù hợp với bản chất trích xuất số liệu thống kê như sau:

- **Quy trình thực thi phương thức `generateCashFlowReport(CashFlowReportRequestDTO request)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra `request` khác rỗng cùng tính hợp lệ của mốc thời gian kết thúc (`endMonth` nằm trong đoạn từ 1 đến 12, `endYear` từ năm 2000 trở đi; ở tầng Trình diễn có thể dùng constructor mặc định của `CashFlowReportRequestDTO` lấy tháng và năm hiện tại). Nếu không hợp lệ, ném `BusinessException` mang mã lỗi `ERR-REP-001`. Cho phép truy vấn tháng và năm tương lai ở chế độ chỉ đọc (trả về số liệu `0.00 VNĐ` và tỷ lệ `0.00%`). Xác định khung thời gian thống kê gồm đúng 12 tháng liên tục lùi tính đến mốc `endMonth/endYear`.
  2. *Bước 2 - Nạp dữ liệu chỉ đọc từ kho lưu trữ:* Gọi `TransactionRepository.findByDateRange(startDate, endDate)` để nạp toàn bộ các giao dịch thuộc lớp cha `Transaction` trong khung 12 tháng gồm giao dịch thu chi (`IncomeExpenseTransaction`) và giao dịch chuyển tiền nội bộ (`TransferTransaction`) mà không làm biến đổi dữ liệu nguồn. Tiền gốc chuyển ví nội bộ bị loại trừ khỏi dòng thu và chi (`BR-FIN-03`), trong khi mọi khoản `TransactionFee` liên kết từ cả hai lớp con được thu nhận theo ngày của giao dịch gốc.
  3. *Bước 3 - Tổng hợp số liệu và ủy quyền tính toán số học tài chính:*
     - Gom cụm các giao dịch theo từng tháng trong chuỗi 12 tháng liên tục.
     - Với mỗi tháng, tính tổng thu nhập (tổng số tiền giao dịch `INCOME`) và tổng chi tiêu (tổng số tiền giao dịch `EXPENSE` cộng dồn toàn bộ các khoản phí giao dịch liên kết đang tồn tại từ giao dịch thu chi và chuyển tiền nội bộ).
     - Ủy quyền lớp giá trị `NetCashFlow.of(...)` tính dòng tiền thuần từng tháng bằng hiệu số giữa tổng thu nhập và tổng chi tiêu theo quy tắc độ chính xác số học tài chính.
     - Ủy quyền lớp giá trị `SavingsRate.of(...)` tính tỷ lệ tiết kiệm từng tháng: trường hợp tổng thu nhập lớn hơn 0, tính bằng công thức (Dòng tiền thuần / Tổng thu nhập) × 100% với bước trung gian tối thiểu 4 chữ số thập phân và làm tròn ngân hàng hai chữ số thập phân; trường hợp tổng thu nhập nhỏ hơn hoặc bằng 0, gán tỷ lệ tiết kiệm mặc định là 0.00% nhằm phòng ngừa triệt để lỗi chia cho không.
     - Tính các chỉ số bình quân cả năm: lấy tổng thu nhập, tổng chi tiêu và tổng dòng tiền thuần của cả 12 tháng chia đều cho mẫu số cố định là 12 theo quy tắc làm tròn ngân hàng hai chữ số thập phân, trong đó các tháng không phát sinh giao dịch được tính bằng 0.00 VNĐ.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói bảng số liệu 12 tháng (`MonthlyCashFlowResponseDTO`) cùng các chỉ số bình quân vào đối tượng `CashFlowReportResponseDTO` bất biến và chuyển giao cho tầng trình diễn kết xuất bảng và biểu đồ trên giao diện đồ họa.

- **Quy trình thực thi phương thức `generateSpendingStructureReport(SpendingStructureReportRequestDTO request)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra `request` khác rỗng, ngày bắt đầu và ngày kết thúc khác rỗng, ngày bắt đầu không vượt quá ngày kết thúc, và `transactionType` thuộc tập `EXPENSE` hoặc `INCOME` (constructor của `SpendingStructureReportRequestDTO` ở tầng Trình diễn mặc định quy chuẩn giá trị null/rỗng thành `EXPENSE`, còn chuỗi khác lạ bị từ chối tại dịch vụ). Nếu vi phạm bất kỳ điều kiện nào, ném `BusinessException` mang mã lỗi `ERR-REP-001`. Cho phép truy vấn khoảng ngày tương lai ở chế độ chỉ đọc.
  2. *Bước 2 - Nạp dữ liệu chỉ đọc từ kho lưu trữ:* Gọi `TransactionRepository.findByDateRange(startDate, endDate)` ở chế độ chỉ đọc để không bỏ sót khoản phí giao dịch gắn trên giao dịch `INCOME` hoặc `TransferTransaction` khi phân tích `EXPENSE`; đồng thời nạp đầy đủ danh sách nhóm nhu cầu từ `NeedGroupRepository.findAll()` và danh mục từ `CategoryRepository.findAll()`, bao gồm cả các nhóm nhu cầu và danh mục chi tiết đã bị xóa mềm (`DELETED`).
  3. *Bước 3 - Phân tích cơ cấu và ủy quyền tính toán số học tài chính:*
     - Lọc các giao dịch phù hợp (nếu `EXPENSE` lấy các `IncomeExpenseTransaction` loại `EXPENSE` cộng dồn mọi `TransactionFee` tồn tại; nếu `INCOME` chỉ lấy `IncomeExpenseTransaction` loại `INCOME`) rồi gom cụm theo danh mục cấp 3 (`Category`) và nhóm nhu cầu cha hiện tại cấp 2 (`NeedGroup`). Chỉ đưa vào danh sách kết quả các nhóm và danh mục có tổng số tiền kỳ hiện tại lớn hơn 0; với mục đã xóa mềm, giữ nguyên tên gốc và truyền trạng thái `"DELETED"` qua DTO để tầng trình diễn gắn nhãn `"(đã xóa)"`.
     - Tính tổng số tiền của toàn bộ kỳ phân tích và tổng tiền của từng nhóm, từng danh mục theo quy tắc độ chính xác tài chính (`BR-FIN-01`).
     - Ủy quyền lớp giá trị `SpendingStructureShare` tính tỷ trọng phần trăm với bước trung gian tối thiểu 4 chữ số thập phân và làm tròn ngân hàng 2 chữ số thập phân: phạm vi `"NeedGroup"` tính tỷ trọng nhóm trên tổng toàn kỳ; phạm vi `"Category"` tính đồng thời tỷ trọng danh mục trên tổng toàn kỳ (`share`) và trên tổng nhóm cha (`shareInGroup`). Nếu tổng tiền toàn kỳ bằng 0.00 VNĐ, gán tỷ trọng mặc định bằng 0.00% và trả về danh sách nhóm nhu cầu rỗng.
     - Sắp xếp các nhóm nhu cầu và danh mục con theo thứ tự tổng số tiền giảm dần. Đồng thời truy vấn kỳ liền kề có cùng độ dài ngày (`days = ChronoUnit.DAYS.between(startDate, endDate) + 1`, khoảng `[startDate.minusDays(days), startDate.minusDays(1)]`) qua `TransactionRepository.findByDateRange(...)` để tính chênh lệch tuyệt đối và tỷ lệ biến động cho tổng toàn kỳ cùng từng nhóm, từng danh mục hiển thị: nếu giá trị kỳ trước lớn hơn 0 thì tính tỷ lệ theo công thức ((Kỳ này - Kỳ trước) / Kỳ trước) × 100%; nếu giá trị kỳ này lớn hơn 0 và kỳ trước bằng 0.00 VNĐ thì đánh dấu mục mới (`isNew = true`) để giao diện hiển thị `"Mới"`; nếu cả tổng toàn kỳ hiện tại và kỳ trước đều bằng 0.00 VNĐ thì gán chênh lệch `0.00 VNĐ`, tỷ lệ `0.00%` và `isNew = false`.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói bảng phân tích cơ cấu 2 cấp (`NeedGroupShareResponseDTO`, `CategoryShareResponseDTO`), tỷ trọng phần trăm và số liệu biến động so sánh vào đối tượng `SpendingStructureReportResponseDTO` bất biến và trả về cho tầng trình diễn.

## 3.7 Dịch vụ quản trị và sao lưu dữ liệu (DataManagementService)

Dịch vụ Quản trị và Sao lưu Dữ liệu chịu trách nhiệm điều phối việc trích xuất ảnh chụp trạng thái tài chính đồng bộ ra tệp sao lưu JSON chuẩn hóa, phục hồi dữ liệu hệ thống qua quy trình kiểm định toàn vẹn 4 bước nghiêm ngặt theo nguyên tắc tất cả hoặc không, và thực hiện đối soát độc lập 4 điều kiện toàn vẹn sổ cái nhằm bảo đảm tính nhất quán tài chính tuyệt đối.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-DAT-01`, `UC-DAT-02`, `UC-DAT-03`
- Yêu cầu chức năng: `FR-DAT-01`, `FR-DAT-02`, `FR-DAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-DAT-001`, `ERR-DAT-002`

**Giao diện công khai:**

Giao diện `com.pfms.service.DataManagementService` cung cấp các phương thức cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `exportBackupData` | `ExportBackupRequestDTO request` | `ExportBackupResponseDTO` | `StorageException`<br/>`BusinessException` | Trích xuất ảnh chụp dữ liệu tài chính (`FULL` hoặc `TRANSACTIONS`) ra tệp JSON UTF-8 an toàn với định dạng `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json`. |
| `restoreBackupData` | `RestoreBackupRequestDTO request` | `RestoreBackupResponseDTO` | `StorageException`<br/>`DataCorruptionException` | Phục hồi dữ liệu từ tệp JSON qua quy trình kiểm định toàn vẹn nghiêm ngặt 4 bước (All-or-Nothing) kèm cơ chế hoàn nguyên `RollbackSupport`. |
| `reconcileSystemData` | Không có | `ReconciliationReportResponseDTO` | Không có | Đối soát độc lập 4 điều kiện toàn vẹn ở chế độ chỉ đọc (bảo toàn số dư ví, cân bằng chuyển tiền, liên kết danh mục, phí không mồ côi); phân loại cảnh báo và đề xuất xử lý. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.data.DataManagementServiceImpl` nhận `WalletRepository`, `TransactionRepository`, `CategoryRepository`, `NeedGroupRepository`, `BalanceAdjustmentRepository` và `BackupRepository` qua hàm khởi tạo để xử lý logic (trong đó `TransactionRepository` lưu trữ đa hình cả giao dịch thu chi `IncomeExpenseTransaction` và chuyển tiền nội bộ `TransferTransaction`, còn `BackupRepository` cung cấp hợp đồng trừu tượng thao tác tệp sao lưu tuân thủ nguyên lý nghịch đảo phụ thuộc (DIP)):

- **Quy trình thực thi phương thức `exportBackupData(ExportBackupRequestDTO request)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Xác thực phạm vi xuất dữ liệu hợp lệ (`FULL` cho toàn bộ hệ thống hoặc `TRANSACTIONS` cho riêng sổ giao dịch, ném `BusinessException` mang `ERR-DAT-001` nếu không hợp lệ). Ủy quyền `BackupRepository.resolveWritableTargetPath` kiểm tra `destinationPath` bắt buộc là đường dẫn thư mục hợp lệ (từ chối `null`/rỗng, tệp đã tồn tại hoặc đường dẫn mang phần mở rộng tệp bằng `StorageException`) và ghép tên tệp tự động theo mẫu `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json` dùng chung mốc `exportedAt` với `ExportBackupResponseDTO`.
  2. *Bước 2 - Nạp dữ liệu ảnh chụp nhất quán ở chế độ chỉ đọc:* Lần lượt gọi phương thức `findAll()` trên các kho lưu trữ (`WalletRepository`, `TransactionRepository`, `CategoryRepository`, `NeedGroupRepository`, `BalanceAdjustmentRepository`) để nạp ảnh chụp dữ liệu đồng bộ tại cùng một mốc thời gian, bảo đảm không làm biến đổi bất kỳ giá trị nào trong hệ thống.
  3. *Bước 3 - Chuẩn hóa dữ liệu, tuần tự hóa JSON và lưu trữ an toàn:*
     - Chuyển đổi trạng thái thực thể sang cấu trúc đóng gói bất biến `BackupPayload` thuộc gói `com.pfms.domain.model.data`, giữ nguyên mốc thời gian `createdAt` của các thực thể.
     - Định dạng mọi trường số tiền sang dạng chuỗi số học chuẩn xác cố định 2 chữ số thập phân theo quy tắc độ chính xác số học tài chính, tuyệt đối không sử dụng số thực dấu phẩy động nhị phân.
     - Chuyển giao `BackupPayload` cho phương thức `BackupRepository.writeBackup(targetPath, payload)`. Tại tầng hạ tầng, lớp cài đặt `FileBackupRepository` thực hiện tuần tự hóa cấu trúc dữ liệu qua `JsonEntitySerializer` sang định dạng JSON UTF-8 không BOM và thực thi cơ chế ghi tệp an toàn qua `FileStorageSupport` (tự khởi tạo thư mục đích nếu chưa tồn tại, ghi vào tệp `.tmp` trước khi đổi tên thay thế nguyên tử và dọn tệp tạm khi gặp lỗi) để tránh tạo tệp hỏng nếu quá trình ghi bị ngắt quãng giữa chừng.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thông tin kết quả xuất sao lưu (đường dẫn tệp đầy đủ vừa tạo, kích thước tệp, mốc thời gian xuất đồng bộ và số lượng bản ghi của từng thực thể) vào `ExportBackupResponseDTO` và trả về cho tầng trình diễn.

- **Quy trình thực thi phương thức `restoreBackupData(RestoreBackupRequestDTO request)`:**
  1. *Bước 1 - Nạp và phân tích dữ liệu sao lưu qua kho lưu trữ:* Gọi phương thức `BackupRepository.readBackup(request.getBackupFilePath())`. Tại tầng hạ tầng, `FileBackupRepository` phối hợp với `FileStorageSupport` để kiểm tra tệp sao lưu bắt buộc phải tồn tại, là tệp thường và có quyền đọc, đọc nội dung UTF-8 (loại bỏ tiền tố BOM nếu có); sau đó phân tích cú pháp JSON qua `JsonEntityDeserializer` (tái cấu thành thực thể lịch sử qua `reconstitute` nhằm bảo toàn dữ liệu `ARCHIVED`/`DELETED` và giữ nguyên `createdAt`). Nếu tệp không đọc được hoặc sai cấu trúc cú pháp JSON, hệ thống ném `StorageException` hoặc `DataCorruptionException` (`ERR-DAT-001`).
  2. *Bước 2 - Thực thi quy trình kiểm định toàn vẹn dữ liệu nghiêm ngặt (All-or-Nothing):*
     - *Kiểm tra cấu trúc và số liệu:* Xác thực các trường bắt buộc của các thực thể phải hiện diện đầy đủ, ngày tháng đúng chuẩn `YYYY-MM-DD`, số tiền đúng định dạng số học chuẩn xác và không trùng lặp mã định danh giữa các bản ghi cùng loại trong payload.
     - *Kiểm tra toàn vẹn danh mục theo quy tắc phân cấp danh mục:* Kiểm tra cấu trúc phân cấp, bảo đảm mọi danh mục chi tiết đều trực thuộc nhóm nhu cầu cha hợp lệ, không có danh mục mồ côi và Loại thu chi của danh mục tương thích với giao dịch liên kết.
     - *Kiểm tra toàn vẹn ví, chuyển tiền và phí giao dịch:* Xác nhận mọi giao dịch thu chi, chuyển tiền, bản ghi điều chỉnh số dư đều trỏ tới ví có mặt trong danh sách ví của payload (từ chối các tệp xuất chỉ chứa sổ giao dịch), bảo đảm cân bằng nguồn-đích khi chuyển tiền và mọi khoản `TransactionFee` đều gắn với giao dịch hợp lệ cùng danh mục chi tiêu tồn tại.
     - *Kiểm tra bảo toàn số dư không âm:* Ủy quyền `SystemDataReconciler` tái tính toán số dư độc lập cho các ví từ lịch sử dòng tiền và điều chỉnh số dư, bảo đảm không có ví nào âm số dư hoặc lệch số dư với dòng tiền (ném `DataCorruptionException` mang `ERR-DAT-002` nếu có bất kỳ sai phạm nào).
     - *Cơ chế hủy bỏ bảo vệ:* Nếu phát hiện bất kỳ sai lệch nào tại bất kỳ bước kiểm tra nào, hệ thống lập tức hủy bỏ toàn bộ quá trình phục hồi, giữ nguyên vẹn dữ liệu hiện tại của hệ thống.
  3. *Bước 3 - Thay thế dữ liệu nguyên tử trên các kho lưu trữ:* Khi và chỉ khi tệp sao lưu vượt qua kiểm định toàn vẹn, dịch vụ chụp bản sao danh sách dữ liệu hiện hành từ `WalletRepository`, `NeedGroupRepository`, `CategoryRepository`, `BalanceAdjustmentRepository` và `TransactionRepository` rồi thực hiện xóa dữ liệu cũ theo thứ tự phụ thuộc ngược và nạp toàn bộ thực thể phục hồi theo thứ tự phụ thuộc cha-trước. Nếu phát sinh lỗi ở bước xóa hoặc ghi, dịch vụ dùng `RollbackSupport` dọn phần ghi dở và khôi phục nguyên trạng dữ liệu chụp trước khi chuyển tiếp `StorageException`.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thông báo phục hồi thành công kèm số lượng thực thể đã phục hồi vào `RestoreBackupResponseDTO` và chuyển giao cho tầng trình diễn.

- **Quy trình thực thi phương thức `reconcileSystemData()`:**
  1. *Bước 1 - Khởi tạo tiến trình đối soát chỉ đọc:* Tiếp nhận yêu cầu đối soát toàn vẹn dữ liệu từ người dùng hoặc theo lịch định kỳ; thiết lập phiên làm việc thuần túy ở chế độ chỉ đọc, không làm biến đổi bất kỳ bản ghi tài chính nào.
  2. *Bước 2 - Nạp dữ liệu đồng thời từ các kho lưu trữ:* Nạp toàn bộ danh sách ví, giao dịch thu chi, chuyển tiền nội bộ, bản ghi kiểm kê số dư và danh mục phân loại từ các kho lưu trữ tương ứng; các khoản chi phí phát sinh (`TransactionFee`) được trích xuất trực tiếp từ các giao dịch cha chứa phí theo quan hệ hợp thành mà không cần kho lưu trữ phí độc lập.
  3. *Bước 3 - Quét và đối soát độc lập 4 điều kiện toàn vẹn tài chính:*
     - *Điều kiện 1 (Bảo toàn số dư ví theo quy tắc bảo toàn số dư ví):* Tính toán lại số dư độc lập từ toàn bộ lịch sử dòng tiền (gồm các khoản thu, chi, chuyển tiền đi/đến, chi phí và mốc kiểm kê điều chỉnh số dư) cho từng ví; so khớp với số dư hiện hành (`currentBalance`) đang ghi nhận trên ví. Sai số cho phép bằng đúng 0.00 VNĐ theo quy tắc độ chính xác số học tài chính.
     - *Điều kiện 2 (Cân bằng nguồn và đích khi chuyển tiền theo quy tắc chuyển tiền nội bộ):* Quét toàn bộ các giao dịch chuyển tiền nội bộ, đối soát số tiền bị trừ tại ví nguồn phải bằng chính xác số tiền được cộng tại ví đích (không tính khoản chi phí phát sinh).
     - *Điều kiện 3 (Tính toàn vẹn danh mục theo quy tắc cấu trúc danh mục ba cấp):* Quét toàn bộ sổ giao dịch thu chi, bảo đảm mọi giao dịch đều gắn với danh mục chi tiết hợp lệ cấp 3 (bao gồm cả danh mục đã xóa mềm) và không có giao dịch nào bị sai lệch Loại thu chi (`INCOME`/`EXPENSE`).
     - *Điều kiện 4 (Toàn vẹn liên kết phí giao dịch):* Quét toàn bộ các khoản chi phí phát sinh gắn kèm giao dịch thu chi và chuyển tiền, bảo đảm mọi bản ghi phí đều thuộc về một giao dịch hợp lệ đang tồn tại, không có phí mồ côi.
     - Phân loại kết quả kiểm tra theo các mức độ nghiêm trọng (`INFO`, `WARNING`, `ERROR`) và tổng hợp đề xuất hành động khắc phục cụ thể (như tự động tính lại số dư hoặc khuyến nghị phục hồi từ bản sao lưu).
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Tổng hợp trạng thái kiểm tra của cả 4 điều kiện (Đạt hoặc Không đạt), số lượng bản ghi đã quét, danh sách chi tiết các sai lệch phát hiện và hành động đề xuất vào đối tượng `ReconciliationReportResponseDTO` và trả về tầng trình diễn để kết xuất báo cáo đối soát.
