# 03. Thiết kế Tầng Dịch vụ Ứng dụng và Điều phối Nghiệp vụ

Tài liệu này xác lập bản vẽ thiết kế chi tiết cho Tầng Dịch vụ ứng dụng của Hệ thống Quản lý Tài chính Cá nhân PFMS. Nội dung văn bản thực hiện chuyển đổi các ca sử dụng và kịch bản tương tác đã được xác thực trong hồ sơ Phân tích Hướng đối tượng sang cấu trúc các lớp dịch vụ điều phối luồng nghiệp vụ trên nền tảng ngôn ngữ Java.

Bản vẽ thiết kế tập trung giải quyết ba mục tiêu kỹ thuật trọng tâm: quản lý vòng đời và điều phối luồng thực thi của các ca sử dụng nghiệp vụ, đóng vai trò cầu nối trung gian giữa giao diện người dùng và các thực thể nghiệp vụ; phân tách rạch ròi giữa giao diện công khai và lớp cài đặt nội bộ nhằm đóng kín chi tiết điều phối; và bảo đảm tính nguyên tử của dữ liệu tài chính khi thao tác qua các kho lưu trữ trừu tượng, đồng thời ủy quyền toàn bộ các phép tính số học cho Tầng Nghiệp vụ cốt lõi.

## 3.1 Tổng quan và Danh mục Dịch vụ Ứng dụng

Mục này xác lập cấu trúc phân tách giữa giao diện công khai và lớp cài đặt nội bộ của tầng dịch vụ, đồng thời cung cấp bảng danh mục đối chiếu các dịch vụ điều phối trong toàn bộ hệ thống.

Cấu trúc mã nguồn của tầng dịch vụ được phân định rạch ròi thành hai không gian tên:
- **Giao diện công khai (`com.pfms.service`):** Định nghĩa toàn bộ các hợp đồng phương thức mà Tầng Trình diễn (`com.pfms.presentation`) được phép triệu gọi. Tầng Trình diễn chỉ phụ thuộc duy nhất vào các giao diện này, hoàn toàn không được phép tiếp xúc với lớp cài đặt cụ thể hay các thực thể nghiệp vụ.
- **Lớp cài đặt nội bộ (`com.pfms.service.impl`):** Hiện thực hóa các giao diện dịch vụ với phạm vi truy cập nội bộ gói (`package-private`), giấu kín toàn bộ logic điều phối. Lớp cài đặt tiếp nhận đối tượng truyền dữ liệu (DTO), tiêm các giao diện kho lưu trữ (`com.pfms.domain.repository`) qua hàm khởi tạo để truy xuất dữ liệu, và ủy quyền các phép tính toán số học cho các thực thể thuộc Tầng Nghiệp vụ.

**Bảng tổng hợp danh mục dịch vụ ứng dụng:**

| Dịch vụ | Giao diện công khai | Lớp cài đặt nội bộ | Trách nhiệm điều phối chính |
| :--- | :--- | :--- | :--- |
| **Quản lý ví** | `WalletService` | `WalletServiceImpl` | Điều phối tạo ví mới, cập nhật thông tin, điều chỉnh số dư kiểm kê thực tế và tính toán cơ cấu phân bổ dòng tiền. |
| **Ghi nhận thu chi** | `TransactionService` | `TransactionServiceImpl` | Điều phối ghi nhận thu chi kèm chi phí giao dịch, cập nhật số dư ví tức thời và thực hiện quy trình hoàn nguyên hai pha khi sửa hoặc xóa. |
| **Chuyển tiền nội bộ** | `TransferService` | `TransferServiceImpl` | Điều phối chuyển khoản hai chiều giữa hai ví, hạch toán chi phí phát sinh và bảo toàn tổng tài sản hệ thống. |
| **Danh mục phân loại** | `CategoryService` | `CategoryServiceImpl` | Quản lý cấu trúc cây danh mục 3 cấp, kiểm soát tính duy nhất của tên và thực thi cơ chế xóa mềm bảo toàn lịch sử giao dịch. |
| **Báo cáo và thống kê** | `ReportService` | `ReportServiceImpl` | Vận hành ở chế độ chỉ đọc: trích xuất dữ liệu, tổng hợp dòng tiền 12 tháng, đo lường tỷ lệ tiết kiệm và phân tích cơ cấu thu chi. |
| **Quản trị và sao lưu** | `DataManagementService` | `DataManagementServiceImpl` | Điều phối xuất dữ liệu JSON, phục hồi dữ liệu hệ thống theo quy trình 4 bước và đối soát độc lập 4 điều kiện toàn vẹn tài chính. |

## 3.2 Dịch vụ Quản lý Ví

Dịch vụ này điều phối toàn bộ vòng đời ví thanh toán của người dùng, từ khởi tạo ví mặc định và ví tạo thêm, cập nhật thông tin và trạng thái sử dụng cho đến kiểm kê điều chỉnh số dư thực tế và tổng hợp cơ cấu phân bổ tài sản. Toàn bộ phép toán số học và kiểm soát bất biến số dư được ủy quyền cho thực thể `Wallet`, `BalanceAdjustmentRecord` cùng các đối tượng giá trị `Money` và `TransactionDate`.

**Giao diện công khai:**

Giao diện `com.pfms.service.WalletService` cung cấp các phương thức sau cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `initializeDefaultWallet` | `Không có` | `void` | `StorageException` | Kiểm tra khi khởi động ứng dụng; nếu chưa có ví mặc định thì tạo sẵn ví `"Tiền mặt"` ở trạng thái hoạt động với số dư bằng không. |
| `createWallet` | `CreateWalletRequestDto request` | `WalletResponseDto` | `BusinessException`, `DuplicateEntityException`, `StorageException` | Kiểm tra hợp lệ tên ví, kiểm tra trùng tên và khởi tạo ví mới ở trạng thái hoạt động với số dư bằng không. |
| `getWalletsSummary` | `EntityStatus statusFilter` | `WalletsSummaryDto` | `StorageException` | Truy xuất danh sách ví theo bộ lọc trạng thái, tính tổng số dư hệ thống và tỷ trọng phân bổ của từng ví ở chế độ chỉ đọc. |
| `updateWallet` | `UpdateWalletRequestDto request` | `WalletResponseDto` | `EntityNotFoundException`, `BusinessException`, `DuplicateEntityException`, `StorageException` | Nạp ví cần sửa, xác thực tên mới không trùng với ví khác và cập nhật tên cùng ghi chú mà giữ nguyên số dư. |
| `archiveWallet` | `UUID walletId` | `WalletResponseDto` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Chuyển một ví đang hoạt động không phải ví mặc định sang trạng thái lưu trữ để ngừng phát sinh giao dịch mới. |
| `activateWallet` | `UUID walletId` | `WalletResponseDto` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Mở lại một ví đang ở trạng thái lưu trữ về trạng thái hoạt động bình thường. |
| `deleteWallet` | `UUID walletId` | `void` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Xóa vĩnh viễn ví không phải mặc định khi ví chưa từng phát sinh giao dịch thu chi, chuyển tiền hay kiểm kê số dư. |
| `adjustBalance` | `AdjustBalanceRequestDto request` | `BalanceAdjustmentResponseDto` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Đối chiếu số dư thực tế quan sát được, cập nhật lại số dư ví và lập bản ghi điều chỉnh bất biến để lưu vết kiểm toán. |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.WalletServiceImpl` có phạm vi truy cập nội bộ gói, nhận `WalletRepository`, `BalanceAdjustmentRepository` và `TransactionRepository` qua hàm khởi tạo. Trước khi gọi thực thể miền, dịch vụ chuyển đổi các giá trị thô từ DTO sang `Money` và `TransactionDate` để kích hoạt kiểm tra chuẩn tài chính và lịch pháp ngay ở đầu luồng điều phối.

**Phương thức `initializeDefaultWallet`:**

- Kiểm tra danh sách ví hiện có trong `WalletRepository`; nếu đã tồn tại ví đánh dấu mặc định thì dừng xử lý mà không ghi đè dữ liệu.
- Nếu chưa có ví mặc định khi khởi tạo hệ thống lần đầu, sinh mã `UUID` và khởi tạo thực thể `Wallet` với tên `"Tiền mặt"`, cờ `isDefault` mang giá trị `true`, trạng thái `ACTIVE` và số dư `Money.ZERO`.
- Lưu ví mặc định vào `WalletRepository` và chuyển tiếp `StorageException` nếu thao tác ghi tệp gặp sự cố.
- Hoàn tất khởi tạo để Tầng Trình diễn sẵn sàng mở phiên làm việc.

**Phương thức `createWallet`:**

- Cắt khoảng trắng hai đầu tên ví và ghi chú; từ chối nếu tên rỗng, dài quá 50 ký tự hoặc ghi chú vượt quá 255 ký tự, sau đó truy vấn `WalletRepository` để chặn trường hợp trùng tên không phân biệt hoa thường trên cả ví đang hoạt động lẫn ví đã lưu trữ.
- Sinh mã định danh `UUID` và khởi tạo thực thể `Wallet` với cờ `isDefault` mang giá trị `false`, trạng thái `ACTIVE` và số dư khởi điểm `Money.ZERO`.
- Ghi nhận thực thể mới vào `WalletRepository` và bảo đảm giữ nguyên trạng thái dữ liệu nếu thao tác lưu phát sinh `StorageException`.
- Ánh xạ thông tin ví vừa tạo sang `WalletResponseDto` để trả về Tầng Trình diễn.

**Phương thức `getWalletsSummary`:**

- Tiếp nhận bộ lọc trạng thái (mặc định là `ACTIVE` từ Tầng Trình diễn hoặc `null` khi cần xem toàn bộ) và nạp danh sách ví từ `WalletRepository`.
- Tính tổng số dư toàn hệ thống bằng `Money` trên tất cả ví `ACTIVE` cùng các ví `ARCHIVED` có số dư dương; giữ nguyên cơ chế chỉ đọc, không thay đổi trạng thái bất kỳ thực thể nào.
- Tính tỷ trọng phần trăm từng ví với tối thiểu 4 chữ số thập phân trung gian rồi làm tròn nửa chẵn về 2 chữ số; gán `0.00%` cho mọi ví khi tổng tài sản bằng không, loại các ví `ARCHIVED` có số dư bằng không khỏi bảng cơ cấu và sắp xếp bảng cơ cấu theo tỷ trọng giảm dần.
- Đóng gói danh sách ví đã lọc, tổng số dư toàn hệ thống và bảng cơ cấu vào `WalletsSummaryDto`.

**Phương thức `updateWallet`:**

- Nạp ví theo mã định danh từ `WalletRepository`, ném `EntityNotFoundException` nếu không tồn tại; chuẩn hóa và kiểm tra định dạng tên mới cùng ghi chú mới.
- Chỉ kiểm tra trùng tên không phân biệt hoa thường khi tên mới sau chuẩn hóa khác tên hiện tại nhằm cho phép giữ nguyên tên cũ khi chỉ sửa ghi chú.
- Ủy quyền phương thức `updateInfo` của `Wallet` để cập nhật tên và ghi chú trong khi bảo toàn mã định danh và số dư hiện có, sau đó lưu lại qua `WalletRepository`.
- Chuyển thực thể sau cập nhật sang `WalletResponseDto`.

**Phương thức `archiveWallet` và `activateWallet`:**

- Nạp thực thể `Wallet` từ `WalletRepository` theo mã định danh và xác nhận ví tồn tại.
- Ủy quyền `wallet.archive()` hoặc `wallet.activate()` kiểm tra điều kiện chuyển trạng thái, trong đó `archive()` tự động từ chối nếu đối tượng là ví `"Tiền mặt"` mặc định hoặc đang không ở trạng thái `ACTIVE`.
- Lưu trạng thái vòng đời mới xuống `WalletRepository` và giữ nguyên số dư hiện hành của ví.
- Trả về `WalletResponseDto` phản ánh trạng thái mới để Tầng Trình diễn cập nhật hiển thị.

**Phương thức `deleteWallet`:**

- Nạp ví từ `WalletRepository` theo mã định danh và từ chối ngay nếu đây là ví `"Tiền mặt"` mặc định.
- Kiểm tra lịch sử liên quan qua `TransactionRepository.findByWalletId` và `BalanceAdjustmentRepository.findByWalletId`; từ chối xóa nếu ví đã có ít nhất một giao dịch thu chi, chuyển tiền hoặc bản ghi kiểm kê số dư.
- Gọi `deleteById` trên `WalletRepository` để xóa vĩnh viễn ví rỗng lịch sử và giải phóng tên ví khỏi tập kiểm tra duy nhất.
- Kết thúc xử lý mà không trả về dữ liệu.

**Phương thức `adjustBalance`:**

- Chuyển đổi số dư thực tế sang `Money` (yêu cầu đúng 2 chữ số thập phân và không âm), chuyển đổi ngày kiểm kê sang `TransactionDate` (mặc định là ngày hiện tại nếu chưa nhập và không vượt quá hiện tại), đồng thời chuẩn hóa lý do điều chỉnh dài tối đa 255 ký tự.
- Nạp ví từ `WalletRepository`, xác nhận ví đang ở trạng thái `ACTIVE`, rồi gọi `wallet.adjustBalance(...)` để kiểm tra độ lệch phải khác không, cập nhật số dư ví và sinh bản ghi `BalanceAdjustmentRecord` bất biến.
- Lưu đồng thời `Wallet` qua `WalletRepository` và bản ghi kiểm kê qua `BalanceAdjustmentRepository`; nếu thao tác lưu sau phát sinh `StorageException`, khôi phục lại trạng thái ví trước kiểm kê nhằm giữ đồng bộ giữa số dư và vết kiểm toán.
- Đóng gói số dư trước kiểm kê, số dư sau kiểm kê và phần chênh lệch vào `BalanceAdjustmentResponseDto`.

## 3.3 Dịch vụ Ghi nhận Thu Chi

Dịch vụ này điều phối nghiệp vụ ghi nhận khoản thu nhập hoặc chi tiêu hàng ngày, tra cứu lịch sử giao dịch theo nhiều tiêu chí, chỉnh sửa giao dịch theo quy trình hoàn nguyên an toàn và xóa giao dịch kèm chi phí phát sinh. Mọi phép tính số dư và ràng buộc hợp thành của phí giao dịch được giao cho `IncomeExpenseTransaction`, `TransactionFee`, `TransactionFactory`, `Money` và `TransactionDate`.

**Giao diện công khai:**

Giao diện `com.pfms.service.TransactionService` cung cấp các phương thức sau cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `recordIncomeExpense` | `RecordTransactionRequestDto request` | `TransactionResponseDto` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Xác thực ví hoạt động và danh mục hợp lệ, tạo giao dịch thu chi kèm phí phát sinh nếu có và cập nhật số dư ví. |
| `filterTransactions` | `FilterTransactionsRequestDto request` | `TransactionListResponseDto` | `BusinessException`, `StorageException` | Truy vấn, lọc và phân trang danh sách giao dịch thu chi kèm dòng phí liên kết, đồng thời tổng hợp dòng tiền ở chế độ chỉ đọc. |
| `updateTransaction` | `UpdateTransactionRequestDto request` | `TransactionResponseDto` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Hoàn nguyên tác động cũ, kiểm tra điều kiện áp dụng thông tin mới trên ví nguồn mới và cập nhật giao dịch cùng phí liên kết. |
| `deleteTransaction` | `UUID transactionId` | `void` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Kiểm tra khả năng hoàn nguyên số dư ví mà không bị âm, khôi phục số dư ví và xóa vĩnh viễn giao dịch cùng khoản phí phụ thuộc. |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.TransactionServiceImpl` có phạm vi truy cập nội bộ gói, nhận `TransactionRepository`, `WalletRepository` và `CategoryRepository` qua hàm khởi tạo. Do `TransactionRepository` lưu trữ đa hình nhiều loại giao dịch, lớp này luôn lọc đúng kiểu `IncomeExpenseTransaction` trong mọi nghiệp vụ thu chi và giữ nguyên bất biến loại thu chi của giao dịch sau khi tạo.

**Phương thức `recordIncomeExpense`:**

- Chuyển đổi số tiền và mức phí sang `Money` (yêu cầu số tiền lớn hơn `0.00 VNĐ` và mức phí không âm), chuyển đổi ngày phát sinh sang `TransactionDate` (không vượt quá hiện tại) và chuẩn hóa ghi chú tối đa 255 ký tự; nếu mức phí dương thì bắt buộc có mã danh mục phí.
- Nạp ví từ `WalletRepository` bảo đảm đang `ACTIVE`; nạp danh mục cấp 3 từ `CategoryRepository` bảo đảm đang `ACTIVE` (và danh mục chi tiêu cấp 3 đang `ACTIVE` cho phí nếu phát sinh); gọi `TransactionFactory` tạo `IncomeExpenseTransaction` kèm `TransactionFee` hợp thành rồi thực thi `applyToWallet()` sau khi xác nhận số dư ví sau hạch toán không bị âm.
- Lưu bản ghi giao dịch vào `TransactionRepository` và lưu ví đã đổi số dư vào `WalletRepository`; nếu bước lưu thứ hai ném `StorageException`, thực hiện xóa bù trừ giao dịch vừa lưu và hoàn tác trạng thái ví trước khi chuyển tiếp ngoại lệ.
- Chuyển thực thể giao dịch cùng số dư ví mới sang `TransactionResponseDto`.

**Phương thức `filterTransactions`:**

- Chuyển đổi mốc ngày bắt đầu và ngày kết thúc (nếu có) sang `TransactionDate` để kiểm tra ngày bắt đầu không đứng sau ngày kết thúc; kiểm tra chỉ số trang tối thiểu bằng 1 và kích thước trang trong đoạn từ 10 đến 100 bản ghi (mặc định 50 nếu chưa chỉ định).
- Nạp toàn bộ bản ghi thuộc kiểu `IncomeExpenseTransaction` từ `TransactionRepository`, áp dụng bộ lọc theo tập ví, loại giao dịch, danh mục hoặc nhóm nhu cầu (bao gồm các danh mục đã xóa mềm) và từ khóa tìm kiếm không phân biệt hoa thường trên ghi chú; tách phần `TransactionFee` hợp thành của từng giao dịch thành dòng hiển thị phụ thuộc và sắp xếp theo ngày giao dịch giảm dần rồi đến mốc tạo giảm dần.
- Thực thi phân trang trên tập kết quả và dùng `Money` tính tổng thu nhập, tổng chi tiêu (bao gồm cả các khoản phí giao dịch liên kết) cùng dòng tiền thuần hoàn toàn ở chế độ chỉ đọc.
- Đóng gói dữ liệu trang hiện tại, thông số phân trang và ba chỉ số dòng tiền vào `TransactionListResponseDto`.

**Phương thức `updateTransaction`:**

- Nạp bản ghi theo mã định danh và xác nhận đây là `IncomeExpenseTransaction`, từ chối mọi yêu cầu sửa trực tiếp đối với `TransactionFee`; chuyển đổi số tiền mới, phí mới sang `Money` (số tiền dương, phí không âm), chuyển đổi ngày mới sang `TransactionDate` và chuẩn hóa ghi chú tối đa 255 ký tự.
- Nạp ví mới đang `ACTIVE`; kiểm tra danh mục mới tương thích với `transactionType` bất biến của giao dịch (cho phép giữ danh mục cũ đã xóa mềm nếu không thay đổi, nhưng bắt buộc danh mục mới phải đang `ACTIVE` nếu có đổi), đồng thời kiểm tra danh mục phí mới thuộc nhánh chi tiêu khi phí mới dương; mô phỏng hai pha hoàn nguyên ví cũ rồi áp dụng dữ liệu mới để bảo đảm cả hai ví đều không âm trước khi gọi `updateDetails(...)` (tự động tạo, sửa hoặc gỡ `TransactionFee` tương ứng).
- Lưu giao dịch vào `TransactionRepository` cùng ví cũ và ví mới (nếu đổi ví) vào `WalletRepository`; nếu phát sinh `StorageException` giữa quá trình lưu nhiều thực thể, khôi phục lại ảnh chụp trạng thái trước thao tác cho giao dịch và các ví liên quan.
- Ánh xạ kết quả sau cập nhật sang `TransactionResponseDto`.

**Phương thức `deleteTransaction`:**

- Nạp bản ghi từ `TransactionRepository` theo mã định danh và xác nhận là `IncomeExpenseTransaction` chứ không phải bản ghi `TransactionFee`.
- Nạp ví liên kết đang `ACTIVE`; kiểm tra trước khả năng hoàn nguyên đối với giao dịch thu nhập để bảo đảm số dư ví sau khi trừ khoản thu và cộng trả khoản phí không bị âm, rồi gọi `revertFromWallet()`.
- Xóa bản ghi giao dịch khỏi `TransactionRepository` (xóa đồng thời `TransactionFee` hợp thành để ngăn bản ghi mồ côi) và lưu lại số dư ví đã hoàn nguyên vào `WalletRepository`; nếu thao tác lưu ví gặp `StorageException`, khôi phục lại bản ghi giao dịch và trạng thái ví ban đầu.
- Kết thúc xử lý mà không trả về dữ liệu.

## 3.4 Dịch vụ Chuyển tiền Nội bộ

Dịch vụ này điều phối việc luân chuyển dòng tiền giữa hai ví độc lập trong hệ thống, tra cứu lịch sử chuyển tiền theo bộ lọc và hủy hoàn tác giao dịch chuyển tiền khi cần thiết. Quá trình xử lý bảo đảm tổng tài sản giữa các ví luôn được bảo toàn sau khi trừ phần phí phát sinh thực tế, đồng thời duy trì số dư không âm tại cả hai đầu giao dịch.

**Giao diện công khai:**

Giao diện `com.pfms.service.TransferService` cung cấp các phương thức sau cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `transferFunds` | `TransferRequestDto request` | `TransferResponseDto` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Kiểm tra hai ví hoạt động phân biệt, đủ số dư chi trả tiền chuyển và phí, thực hiện chuyển khoản nguyên tử và lưu giao dịch. |
| `filterTransfers` | `TransferFilterCriteriaDto request` | `TransferHistoryResponseDto` | `BusinessException`, `StorageException` | Tra cứu và lọc danh sách giao dịch chuyển tiền theo khoảng ngày hoặc cặp ví nguồn, ví đích ở chế độ chỉ đọc. |
| `deleteTransfer` | `UUID transferId` | `void` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Kiểm tra ví đích đủ số dư để thu hồi, hoàn nguyên hai chiều số dư cả hai ví và xóa bản ghi chuyển tiền cùng phí liên kết. |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.TransferServiceImpl` có phạm vi truy cập nội bộ gói, nhận `TransactionRepository`, `WalletRepository` và `CategoryRepository` qua hàm khởi tạo. Mọi bản ghi chuyển tiền được lưu trữ đa hình trong `TransactionRepository` dưới dạng thực thể `TransferTransaction` kèm phần `TransactionFee` hợp thành gắn với `sourceWallet`.

**Phương thức `transferFunds`:**

- Kiểm tra mã ví nguồn khác mã ví đích, chuyển đổi số tiền chuyển và phí sang `Money` (số tiền lớn hơn `0.00 VNĐ`, phí không âm), chuyển đổi ngày chuyển sang `TransactionDate` (không vượt quá hiện tại) và chuẩn hóa ghi chú tối đa 255 ký tự; nếu mức phí dương thì bắt buộc kèm mã danh mục chi tiêu cấp 3.
- Nạp cả hai thực thể `Wallet` bảo đảm đều ở trạng thái `ACTIVE`, xác nhận ví nguồn đủ chi trả tổng tiền chuyển cộng phí thông qua `Money` và `hasSufficientBalance`, sau đó dùng `TransactionFactory` khởi tạo `TransferTransaction` (kèm `TransactionFee` nếu phí dương, với danh mục chi tiêu cấp 3 đang `ACTIVE`) và gọi `applyToWallet()` để trừ ví nguồn và cộng ví đích.
- Lưu đồng thời ví nguồn, ví đích qua `WalletRepository` và giao dịch mới qua `TransactionRepository`; nếu xảy ra `StorageException` tại bất kỳ lệnh lưu nào, thực hiện khôi phục ngay trạng thái trước giao dịch của cả hai ví và xóa bỏ bản ghi chưa hoàn tất để giữ nguyên tổng tài sản.
- Đóng gói thông tin giao dịch chuyển tiền và số dư mới của hai ví vào `TransferResponseDto`.

**Phương thức `filterTransfers`:**

- Chuyển đổi mốc ngày bắt đầu và ngày kết thúc (nếu có) sang `TransactionDate` và kiểm tra ngày bắt đầu không đứng sau ngày kết thúc.
- Nạp các thực thể thuộc kiểu `TransferTransaction` từ `TransactionRepository`, lọc theo khoảng ngày, mã ví nguồn và mã ví đích; sắp xếp kết quả theo ngày chuyển tiền giảm dần rồi đến mốc tạo giảm dần, đồng thời tra cứu tên ví nguồn và ví đích qua `WalletRepository` ở chế độ chỉ đọc.
- Ủy quyền đối tượng giá trị `Money` tính tổng số tiền đã chuyển và tổng chi phí phát sinh trên tập kết quả thỏa mãn điều kiện lọc.
- Đóng gói danh sách giao dịch chuyển tiền cùng hai chỉ số tổng hợp vào `TransferHistoryResponseDto`.

**Phương thức `deleteTransfer`:**

- Nạp bản ghi theo mã định danh từ `TransactionRepository` và xác nhận đúng là thể hiện của `TransferTransaction`.
- Nạp ví nguồn và ví đích từ `WalletRepository` bảo đảm cả hai đang `ACTIVE`; kiểm tra số dư khả dụng của ví đích tối thiểu bằng số tiền gốc đã chuyển để tránh âm ví, sau đó gọi `revertFromWallet()` để trừ tiền ví đích và cộng trả tiền chuyển cùng khoản phí về ví nguồn.
- Xóa `TransferTransaction` cùng `TransactionFee` hợp thành khỏi `TransactionRepository` và lưu trạng thái mới của cả hai ví vào `WalletRepository`; nếu phát sinh `StorageException` trong chuỗi ghi dữ liệu, hoàn nguyên ảnh chụp trạng thái của cả hai ví và bản ghi giao dịch.
- Kết thúc xử lý mà không trả về dữ liệu.

## 3.5 Dịch vụ Danh mục Phân loại

Dịch vụ Danh mục Phân loại đảm nhiệm điều phối các ca sử dụng quản lý hệ thống phân loại thu chi 3 cấp (Loại thu chi cố định, Nhóm nhu cầu và Danh mục chi tiết), kiểm soát tính hợp lệ và tính duy nhất của tên gọi theo quy tắc nghiệp vụ. Đồng thời, dịch vụ chịu trách nhiệm thực thi cơ chế xóa mềm và khôi phục mục phân loại nhằm bảo toàn tuyệt đối tính toàn vẹn tham chiếu lịch sử cho toàn bộ các giao dịch tài chính.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-04`, `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`, `ERR-CAT-004`, `ERR-CAT-005`

**Giao diện công khai:**

Giao diện `com.pfms.service.CategoryService` cung cấp các phương thức cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `createCategory` | `CreateCategoryRequestDTO requestDto` | `CategoryResponseDTO` | `ValidationException`<br/>`EntityNotFoundException`<br/>`DuplicateEntityException`<br/>`BusinessException` | Điều phối bổ sung danh mục mới vào nhóm nhu cầu cha: Tiếp nhận yêu cầu tạo danh mục; kiểm tra định dạng tên theo quy tắc đặt tên; kiểm tra nhóm cha tồn tại và đang hoạt động; kiểm tra tính duy nhất của tên trong cùng nhóm cha; khởi tạo thực thể `Category` hoạt động (`ACTIVE`), kế thừa loại thu chi (`INCOME`/`EXPENSE`) từ nhóm cha và lưu trữ vào kho dữ liệu. |
| `renameCategory` | `RenameCategoryRequestDTO requestDto` | `CategoryResponseDTO` | `ValidationException`<br/>`EntityNotFoundException`<br/>`DuplicateEntityException`<br/>`BusinessException` | Điều phối đổi tên hiển thị của danh mục: Tiếp nhận yêu cầu cập nhật tên; kiểm tra độ dài từ 1 đến 50 ký tự sau khi cắt khoảng trắng theo quy tắc đặt tên; xác thực danh mục tồn tại; kiểm tra tên mới không trùng với các danh mục khác đang hoạt động trong cùng nhóm cha; cập nhật tên và lưu trữ, bảo toàn nguyên vẹn mã định danh và toàn bộ lịch sử giao dịch. |
| `restoreCategory` | `UUID categoryId` | `CategoryResponseDTO` | `EntityNotFoundException`<br/>`DuplicateEntityException`<br/>`BusinessException` | Điều phối khôi phục danh mục đã xóa mềm: Tiếp nhận mã danh mục cần khôi phục; kiểm tra danh mục tồn tại và đang ở trạng thái `DELETED`; kiểm tra nhóm nhu cầu cha trực thuộc bắt buộc phải đang hoạt động; kiểm tra tên khôi phục không trùng với danh mục đang hoạt động trong cùng nhóm; chuyển trạng thái sang `ACTIVE` và đưa danh mục trở lại danh sách lựa chọn thu chi. |
| `deleteCategory` | `UUID categoryId` | `void` | `EntityNotFoundException`<br/>`BusinessException` | Điều phối xóa danh mục: Tiếp nhận mã danh mục cần xóa; xác thực danh mục tồn tại; kiểm tra không phải nhóm nhu cầu hoạt động cuối cùng của loại thu chi; kiểm tra lịch sử giao dịch qua `TransactionRepository`: nếu chưa từng có giao dịch thì xóa vĩnh viễn (xóa cứng), nếu đã phát sinh giao dịch thì chuyển trạng thái sang `DELETED` (xóa mềm) theo quy tắc bảo toàn dữ liệu lịch sử. |
| `getCategoryHierarchy` | Không có | `List<CategoryHierarchyResponseDTO>` | Không có | Điều phối truy xuất cây danh mục phân loại: Nạp toàn bộ cấu trúc phân loại 3 cấp (Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết) ở cả trạng thái `ACTIVE` và `DELETED` để hiển thị trên giao diện hoặc cung cấp danh sách lựa chọn cho luồng ghi nhận giao dịch. |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.CategoryServiceImpl` nhận `CategoryRepository`, `NeedGroupRepository` và `TransactionRepository` qua hàm khởi tạo để xử lý logic:

- **Quy trình thực thi phương thức `createCategory(CreateCategoryRequestDTO requestDto)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Làm sạch và xác thực tên danh mục theo quy tắc đặt tên (loại bỏ khoảng trắng hai đầu, kiểm tra độ dài từ 1 đến 50 ký tự, ném `ValidationException` nếu để trống hoặc vượt ngưỡng). Nạp nhóm nhu cầu cha qua `NeedGroupRepository.findById(requestDto.getParentGroupId())`; nếu không tìm thấy ném `EntityNotFoundException`, nếu nhóm cha đang ở trạng thái `DELETED` thì từ chối tiếp nhận và ném `BusinessException` theo quy tắc cấu trúc danh mục ba cấp. Kiểm tra trùng lặp tên trong cùng nhóm cha qua `CategoryRepository.existsByNameAndNeedGroupId(...)`; nếu tên đã tồn tại ở trạng thái hoạt động thì ném `DuplicateEntityException`.
  2. *Bước 2 - Nạp thực thể và ủy quyền tính toán cho Thực thể miền:* Khởi tạo thực thể `Category` mới ở trạng thái hoạt động `ACTIVE` (`isDeleted = false`), thiết lập liên kết phân cấp ngữ nghĩa tới nhóm nhu cầu cha và kế thừa thuộc tính loại thu chi (`INCOME` hoặc `EXPENSE`) cố định từ nhóm cha theo cấu trúc phân cấp 3 cấp.
  3. *Bước 3 - Kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký:* Gọi `CategoryRepository.save(newCategory)` để lưu trữ bền vững bản ghi danh mục mới vào tệp dữ liệu, bảo đảm tính nhất quán của cây danh mục và ghi nhật ký kiểm toán hệ thống.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Ánh xạ dữ liệu của thực thể `Category` vừa lưu trữ sang `CategoryResponseDTO` (chứa `categoryId`, `name`, `parentGroupId`, `transactionType`, `status`, `createdAt`) và trả về cho Tầng Trình diễn.

- **Quy trình thực thi phương thức `renameCategory(RenameCategoryRequestDTO requestDto)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra tên mới theo quy tắc đặt tên (cắt khoảng trắng hai đầu, độ dài 1 đến 50 ký tự, ném `ValidationException` nếu vi phạm). Nạp danh mục từ `CategoryRepository.findById(requestDto.getCategoryId())`, ném `EntityNotFoundException` nếu không tồn tại. Nếu có yêu cầu chuyển nhóm cha mới: kiểm tra nhóm đích phải tồn tại và đang `ACTIVE` (nếu nhóm đích bị `DELETED` ném `BusinessException`), đồng thời nhóm đích phải cùng Loại thu chi với nhóm cũ (nếu khác Loại thu chi ném `BusinessException` theo quy tắc cấu trúc danh mục ba cấp). Kiểm tra tên mới không bị trùng với các danh mục khác trong nhóm đích qua `CategoryRepository.existsByNameAndNeedGroupIdAndIdNot(...)`, ném `DuplicateEntityException` nếu bị trùng.
  2. *Bước 2 - Nạp thực thể và ủy quyền tính toán cho Thực thể miền:* Ủy quyền thực thể `Category` cập nhật thuộc tính tên hiển thị mới (và cập nhật liên kết nhóm cha nếu có), giữ nguyên tuyệt đối mã định danh `categoryId` và toàn bộ các liên kết giao dịch lịch sử trong quá khứ.
  3. *Bước 3 - Kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký:* Gọi `CategoryRepository.save(category)` để lưu trữ cập nhật vào tệp dữ liệu và ghi nhật ký thay đổi thông tin danh mục.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thông tin thực thể `Category` đã cập nhật vào `CategoryResponseDTO` và trả về cho Tầng Trình diễn.

- **Quy trình thực thi phương thức `restoreCategory(UUID categoryId)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Nạp danh mục cần khôi phục qua `CategoryRepository.findById(categoryId)`, ném `EntityNotFoundException` nếu không tìm thấy hoặc nếu danh mục đang ở trạng thái `ACTIVE`. Nạp nhóm nhu cầu cha trực thuộc qua `NeedGroupRepository.findById(...)`; nếu nhóm cha đang ở trạng thái `DELETED` thì từ chối khôi phục và ném `BusinessException` (yêu cầu người dùng khôi phục nhóm cha trước theo quy tắc cấu trúc danh mục ba cấp). Kiểm tra tên danh mục khôi phục không trùng với bất kỳ danh mục nào đang `ACTIVE` trong cùng nhóm cha qua `CategoryRepository.existsByNameAndNeedGroupId(...)`, ném `DuplicateEntityException` nếu xảy ra xung đột tên theo quy tắc đặt tên.
  2. *Bước 2 - Nạp thực thể và ủy quyền tính toán cho Thực thể miền:* Ủy quyền thực thể `Category` chuyển trạng thái vòng đời từ `DELETED` sang `ACTIVE` (`isDeleted = false`), tái lập tính khả dụng của danh mục mà không làm xáo trộn các số liệu và liên kết lịch sử đã ghi nhận.
  3. *Bước 3 - Kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký:* Gọi `CategoryRepository.save(category)` để lưu lại trạng thái hoạt động mới vào kho dữ liệu và ghi nhật ký thao tác khôi phục.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thực thể `Category` đã phục hồi vào `CategoryResponseDTO` và trả về cho Tầng Trình diễn để hiển thị lại trên giao diện nhập liệu.

- **Quy trình thực thi phương thức `deleteCategory(UUID categoryId)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Nạp thực thể danh mục qua `CategoryRepository.findById(categoryId)`, ném `EntityNotFoundException` nếu không tìm thấy. Kiểm tra số lượng danh mục con đang hoạt động của nhóm nhu cầu cha qua `CategoryRepository.countActiveByNeedGroupId(category.getNeedGroupId())`; nếu đây là danh mục hoạt động duy nhất còn lại trong nhóm cha thì từ chối xóa và ném `BusinessException` (hoặc nếu là nhóm nhu cầu thì kiểm tra số lượng nhóm đang hoạt động của nhánh Loại thu chi tương ứng qua `NeedGroupRepository`) theo quy tắc duy trì tối thiểu một mục hoạt động.
  2. *Bước 2 - Nạp thực thể và ủy quyền tính toán cho Thực thể miền:* Kiểm tra lịch sử giao dịch liên kết của danh mục thông qua `TransactionRepository.existsByCategoryId(categoryId)`:
     - *Phân nhánh 1 (Chưa từng phát sinh giao dịch):* Thực hiện xóa vật lý (xóa cứng), phá vỡ liên kết phân cấp và chuẩn bị giải phóng hoàn toàn khỏi kho lưu trữ.
     - *Phân nhánh 2 (Đã có giao dịch trong quá khứ):* Thực hiện xóa mềm theo quy định bảo toàn dữ liệu lịch sử, ủy quyền thực thể `Category` chuyển trạng thái sang `DELETED` (`isDeleted = true`), ẩn khỏi danh sách lựa chọn tạo giao dịch mới nhưng giữ nguyên trạng thái hiển thị kèm nhãn "(đã xóa)" trong sổ cái và các báo cáo lịch sử.
  3. *Bước 3 - Kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký:* Nếu thuộc phân nhánh 1, gọi `CategoryRepository.deleteById(categoryId)` để loại bỏ hoàn toàn bản ghi; nếu thuộc phân nhánh 2, gọi `CategoryRepository.save(category)` để lưu cập nhật trạng thái xóa mềm. Ghi nhật ký kiểm toán tương ứng với từng loại thao tác xóa.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Hoàn tất điều phối và trả về kết quả thông báo xóa thành công cho Tầng Trình diễn.

- **Quy trình thực thi phương thức `getCategoryHierarchy()`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện:* Tiếp nhận yêu cầu truy vấn cấu trúc danh mục phân loại từ Tầng Trình diễn.
  2. *Bước 2 - Nạp dữ liệu từ kho lưu trữ:* Gọi `NeedGroupRepository.findAll()` và `CategoryRepository.findAll()` để nạp toàn bộ danh sách nhóm nhu cầu và danh mục chi tiết ở cả hai trạng thái `ACTIVE` và `DELETED`.
  3. *Bước 3 - Tổ chức và chuẩn hóa dữ liệu:* Sắp xếp và gom cụm các danh mục chi tiết trực thuộc từng nhóm nhu cầu cha tương ứng theo đúng cấu trúc phân cấp 3 cấp, giữ nguyên nhãn nhận diện trạng thái cho các mục đã xóa mềm.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Ánh xạ toàn bộ cấu trúc cây phân loại sang danh sách `CategoryHierarchyResponseDTO` bất biến và trả về Tầng Trình diễn phục vụ kết xuất dữ liệu trên giao diện đồ họa.

## 3.6 Dịch vụ Báo cáo và Thống kê Tài chính

Dịch vụ Báo cáo và Thống kê Tài chính vận hành thuần túy ở chế độ chỉ đọc, chịu trách nhiệm truy xuất dữ liệu từ các kho lưu trữ để tổng hợp chuỗi thời gian dòng tiền 12 tháng liên tục, đo lường dòng tiền thuần và tỷ lệ tiết kiệm theo tháng và bình quân năm. Đồng thời, dịch vụ phân tích cơ cấu phân bổ thu chi 2 cấp theo nhóm nhu cầu và danh mục chi tiết trong kỳ xác định, bảo đảm độ chính xác số học tài chính mà không làm biến đổi bất kỳ trạng thái dữ liệu nào của hệ thống.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-01`, `UC-REP-02`
- Yêu cầu chức năng: `FR-REP-01`, `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi: `ERR-REP-001`, `ERR-FIN-001`, `ERR-FIN-002`

**Giao diện công khai:**

Giao diện `com.pfms.service.ReportService` cung cấp các phương thức cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `generateCashFlowReport` | `CashFlowReportRequestDTO request` (và phương thức tiện ích `int endMonth, int endYear`) | `CashFlowReportResponseDTO` | `BusinessException` | Điều phối thống kê dòng tiền: Kiểm tra `request` hợp lệ (`endMonth` từ 1 đến 12, `endYear` từ năm 2000 trở đi); truy vấn sổ giao dịch ở chế độ chỉ đọc; tổng hợp thu nhập và chi tiêu (gồm phí giao dịch liên kết trên cả giao dịch thu chi và chuyển tiền nội bộ, loại trừ tiền gốc chuyển ví); ủy quyền `NetCashFlow` và `SavingsRate` tính dòng tiền thuần cùng tỷ lệ tiết kiệm (gán 0.00% khi tổng thu không dương); tính các chỉ số bình quân cả năm chia đều cho 12. |
| `generateSpendingStructureReport` | `SpendingStructureRequestDTO request` | `SpendingStructureReportResponseDTO` | `BusinessException` | Điều phối phân tích cơ cấu thu chi: Tiếp nhận khoảng ngày thống kê và loại luồng tiền (`EXPENSE` hoặc `INCOME`); kiểm tra khoảng ngày không rỗng, ngày bắt đầu không vượt ngày kết thúc và loại luồng tiền hợp lệ; truy vấn giao dịch (gồm phí liên kết khi phân tích `EXPENSE`) và toàn bộ danh mục (kể cả mục xóa mềm có phát sinh); gom cụm 2 cấp, chỉ giữ mục có số tiền lớn hơn 0, tính `SpendingStructureShare` theo tổng kỳ (và theo nhóm cha cho danh mục), sắp xếp giảm dần và so sánh kỳ liền kề. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `public class ReportServiceImpl implements ReportService` trong gói `com.pfms.service.impl` nhận `TransactionRepository`, `CategoryRepository` và `NeedGroupRepository` qua hàm khởi tạo để xử lý logic:

Dịch vụ vận hành hoàn toàn ở chế độ chỉ đọc (Read-only), tuyệt đối không làm thay đổi số dư ví, không chỉnh sửa bản ghi tài chính và không thực hiện thao tác ghi hay sửa đổi dữ liệu lưu trữ. Quy trình 4 bước được chuẩn hóa phù hợp với bản chất trích xuất số liệu thống kê như sau:

- **Quy trình thực thi phương thức `generateCashFlowReport(CashFlowReportRequestDTO request)` (cùng phương thức tiện ích `generateCashFlowReport(int endMonth, int endYear)`):**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra `request` khác rỗng cùng tính hợp lệ của mốc thời gian kết thúc (`endMonth` nằm trong đoạn từ 1 đến 12, `endYear` từ năm 2000 trở đi; ở tầng Trình diễn có thể dùng constructor mặc định của `CashFlowReportRequestDTO` lấy tháng và năm hiện tại). Nếu không hợp lệ, ném `BusinessException` mang mã lỗi `ERR-REP-001`. Cho phép truy vấn tháng và năm tương lai ở chế độ chỉ đọc (trả về số liệu `0.00 VNĐ` và tỷ lệ `0.00%`). Xác định khung thời gian thống kê gồm đúng 12 tháng liên tục lùi tính đến mốc `endMonth/endYear`.
  2. *Bước 2 - Nạp dữ liệu chỉ đọc từ kho lưu trữ:* Gọi `TransactionRepository.findByDateRange(startDate, endDate)` để nạp toàn bộ các giao dịch thuộc lớp cha `Transaction` trong khung 12 tháng gồm giao dịch thu chi (`IncomeExpenseTransaction`) và giao dịch chuyển tiền nội bộ (`TransferTransaction`) mà không làm biến đổi dữ liệu nguồn. Tiền gốc chuyển ví nội bộ bị loại trừ khỏi dòng thu và chi (`BR-FIN-03`), trong khi mọi khoản `TransactionFee` liên kết từ cả hai lớp con được thu nhận theo ngày của giao dịch gốc.
  3. *Bước 3 - Tổng hợp số liệu và ủy quyền tính toán số học tài chính:*
     - Gom cụm các giao dịch theo từng tháng trong chuỗi 12 tháng liên tục.
     - Với mỗi tháng, tính tổng thu nhập (tổng số tiền giao dịch `INCOME`) và tổng chi tiêu (tổng số tiền giao dịch `EXPENSE` cộng dồn toàn bộ các khoản phí giao dịch liên kết đang tồn tại từ giao dịch thu chi và chuyển tiền nội bộ).
     - Ủy quyền lớp giá trị `NetCashFlow.of(...)` tính dòng tiền thuần từng tháng bằng hiệu số giữa tổng thu nhập và tổng chi tiêu theo quy tắc độ chính xác số học tài chính.
     - Ủy quyền lớp giá trị `SavingsRate.of(...)` tính tỷ lệ tiết kiệm từng tháng: trường hợp tổng thu nhập lớn hơn 0, tính bằng công thức $\frac{\text{Dòng tiền thuần}}{\text{Tổng thu nhập}} \times 100\%$ với bước trung gian tối thiểu 4 chữ số thập phân và làm tròn ngân hàng hai chữ số thập phân; trường hợp tổng thu nhập nhỏ hơn hoặc bằng 0, gán tỷ lệ tiết kiệm mặc định là 0.00% nhằm phòng ngừa triệt để lỗi chia cho không.
     - Tính các chỉ số bình quân cả năm: lấy tổng thu nhập, tổng chi tiêu và tổng dòng tiền thuần của cả 12 tháng chia đều cho mẫu số cố định là 12 theo quy tắc làm tròn ngân hàng hai chữ số thập phân, trong đó các tháng không phát sinh giao dịch được tính bằng 0.00 VNĐ.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói bảng số liệu 12 tháng (`MonthlyCashFlowResponseDTO`) cùng các chỉ số bình quân vào đối tượng `CashFlowReportResponseDTO` bất biến và chuyển giao cho Tầng Trình diễn kết xuất bảng và biểu đồ trên giao diện đồ họa.

- **Quy trình thực thi phương thức `generateSpendingStructureReport(SpendingStructureRequestDTO request)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra `request` khác rỗng, ngày bắt đầu và ngày kết thúc khác rỗng, ngày bắt đầu không vượt quá ngày kết thúc, và `transactionType` thuộc tập `EXPENSE` hoặc `INCOME` (constructor của `SpendingStructureRequestDTO` ở tầng Trình diễn mặc định quy chuẩn giá trị null/rỗng thành `EXPENSE`, còn chuỗi khác lạ bị từ chối tại dịch vụ). Nếu vi phạm bất kỳ điều kiện nào, ném `BusinessException` mang mã lỗi `ERR-REP-001`. Cho phép truy vấn khoảng ngày tương lai ở chế độ chỉ đọc.
  2. *Bước 2 - Nạp dữ liệu chỉ đọc từ kho lưu trữ:* Gọi `TransactionRepository.findByDateRange(startDate, endDate)` ở chế độ chỉ đọc để không bỏ sót khoản phí giao dịch gắn trên giao dịch `INCOME` hoặc `TransferTransaction` khi phân tích `EXPENSE`; đồng thời nạp đầy đủ danh sách nhóm nhu cầu từ `NeedGroupRepository.findAll()` và danh mục từ `CategoryRepository.findAll()`, bao gồm cả các nhóm nhu cầu và danh mục chi tiết đã bị xóa mềm (`DELETED`).
  3. *Bước 3 - Phân tích cơ cấu và ủy quyền tính toán số học tài chính:*
     - Lọc các giao dịch phù hợp (nếu `EXPENSE` lấy các `IncomeExpenseTransaction` loại `EXPENSE` cộng dồn mọi `TransactionFee` tồn tại; nếu `INCOME` chỉ lấy `IncomeExpenseTransaction` loại `INCOME`) rồi gom cụm theo danh mục cấp 3 (`Category`) và nhóm nhu cầu cha hiện tại cấp 2 (`NeedGroup`). Chỉ đưa vào danh sách kết quả các nhóm và danh mục có tổng số tiền kỳ hiện tại lớn hơn 0; với mục đã xóa mềm, giữ nguyên tên gốc và truyền trạng thái `"DELETED"` qua DTO để Tầng Trình diễn gắn nhãn `"(đã xóa)"`.
     - Tính tổng số tiền của toàn bộ kỳ phân tích và tổng tiền của từng nhóm, từng danh mục theo quy tắc độ chính xác tài chính (`BR-FIN-01`).
     - Ủy quyền lớp giá trị `SpendingStructureShare` tính tỷ trọng phần trăm với bước trung gian tối thiểu 4 chữ số thập phân và làm tròn ngân hàng 2 chữ số thập phân: phạm vi `"NeedGroup"` tính tỷ trọng nhóm trên tổng toàn kỳ; phạm vi `"Category"` tính đồng thời tỷ trọng danh mục trên tổng toàn kỳ (`share`) và trên tổng nhóm cha (`shareInGroup`). Nếu tổng tiền toàn kỳ bằng 0.00 VNĐ, gán tỷ trọng mặc định bằng 0.00% và trả về danh sách nhóm nhu cầu rỗng.
     - Sắp xếp các nhóm nhu cầu và danh mục con theo thứ tự tổng số tiền giảm dần. Đồng thời truy vấn kỳ liền kề có cùng độ dài ngày (`days = ChronoUnit.DAYS.between(startDate, endDate) + 1`, khoảng `[startDate.minusDays(days), startDate.minusDays(1)]`) qua `TransactionRepository.findByDateRange(...)` để tính chênh lệch tuyệt đối và tỷ lệ biến động cho tổng toàn kỳ cùng từng nhóm, từng danh mục hiển thị: nếu giá trị kỳ trước lớn hơn 0 thì tính tỷ lệ theo công thức $\frac{\text{Kỳ này} - \text{Kỳ trước}}{\text{Kỳ trước}} \times 100\%$; nếu giá trị kỳ này lớn hơn 0 và kỳ trước bằng 0.00 VNĐ thì đánh dấu mục mới (`isNew = true`) để giao diện hiển thị `"Mới"`; nếu cả tổng toàn kỳ hiện tại và kỳ trước đều bằng 0.00 VNĐ thì gán chênh lệch `0.00 VNĐ`, tỷ lệ `0.00%` và `isNew = false`.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói bảng phân tích cơ cấu 2 cấp (`NeedGroupShareResponseDTO`, `CategoryShareResponseDTO`), tỷ trọng phần trăm và số liệu biến động so sánh vào đối tượng `SpendingStructureReportResponseDTO` bất biến và trả về cho Tầng Trình diễn.

## 3.7 Dịch vụ Quản trị và Sao lưu Dữ liệu

Dịch vụ Quản trị và Sao lưu Dữ liệu chịu trách nhiệm điều phối việc trích xuất ảnh chụp trạng thái tài chính đồng bộ ra tệp sao lưu JSON chuẩn hóa, phục hồi dữ liệu hệ thống qua quy trình kiểm định toàn vẹn 4 bước nghiêm ngặt theo nguyên tắc tất cả hoặc không, và thực hiện đối soát độc lập 4 điều kiện toàn vẹn sổ cái nhằm bảo đảm tính nhất quán tài chính tuyệt đối.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-DAT-01`, `UC-DAT-02`, `UC-DAT-03`
- Yêu cầu chức năng: `FR-DAT-01`, `FR-DAT-02`, `FR-DAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`

**Giao diện công khai:**

Giao diện `com.pfms.service.DataManagementService` cung cấp các phương thức cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `exportBackupData` | `ExportBackupRequestDTO requestDto` | `BackupResponseDTO` | `StorageException`<br/>`BusinessException` | Điều phối xuất dữ liệu tài chính: Trích xuất ảnh chụp trạng thái dữ liệu nhất quán tại một thời điểm theo phạm vi chỉ định (`FULL` hoặc `TRANSACTIONS`) ở chế độ chỉ đọc; định dạng các trường số tiền thành chuỗi số học chuẩn xác 2 chữ số thập phân; chuyển đổi cấu trúc JSON mã hóa UTF-8 không BOM và ghi an toàn xuống tệp đĩa cục bộ. |
| `restoreBackupData` | `RestoreBackupRequestDTO requestDto` | `RestoreResultDTO` | `StorageException`<br/>`DataCorruptionException`<br/>`InsufficientBalanceException`<br/>`BusinessException` | Điều phối phục hồi dữ liệu từ bản sao lưu: Tiếp nhận đường dẫn tệp sao lưu JSON; kiểm tra cú pháp và thực thi quy trình kiểm định toàn vẹn 4 bước nghiêm ngặt (định dạng số liệu, cấu trúc danh mục 3 cấp, liên kết chuyển tiền và phí, bảo toàn số dư ví không âm); hủy bỏ toàn bộ nếu phát hiện sai lệch; chỉ thay thế dữ liệu hệ thống khi bản sao lưu hợp lệ 100%. |
| `reconcileSystemData` | Không có | `ReconciliationReportDTO` | Không có | Điều phối đối soát toàn vẹn dữ liệu hệ thống: Quét đối soát độc lập 4 điều kiện toàn vẹn ở chế độ chỉ đọc: (1) tính lại số dư từng ví từ lịch sử dòng tiền và so khớp số dư ghi nhận hiện hành; (2) kiểm tra cân bằng số tiền chuyển giữa ví nguồn và ví đích; (3) kiểm tra liên kết danh mục và tính toàn vẹn loại thu chi; (4) kiểm tra phí giao dịch mồ côi; phân loại mức độ cảnh báo và đề xuất xử lý. |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.DataManagementServiceImpl` có phạm vi truy cập nội bộ gói, nhận `WalletRepository`, `TransactionRepository`, `CategoryRepository`, `NeedGroupRepository` và `BalanceAdjustmentRepository` qua hàm khởi tạo để xử lý logic (trong đó `TransactionRepository` lưu trữ đa hình cả giao dịch thu chi `IncomeExpenseTransaction` và chuyển tiền nội bộ `TransferTransaction`):

- **Quy trình thực thi phương thức `exportBackupData(ExportBackupRequestDTO requestDto)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra tính hợp lệ của đường dẫn lưu trữ đích `destinationPath` và quyền ghi trên hệ thống tệp cục bộ, ném `StorageException` nếu đường dẫn không hợp lệ hoặc không có quyền truy cập; xác thực phạm vi xuất dữ liệu hợp lệ (`FULL` cho toàn bộ hệ thống hoặc `TRANSACTIONS` cho riêng sổ giao dịch theo yêu cầu sao lưu).
  2. *Bước 2 - Nạp dữ liệu ảnh chụp nhất quán ở chế độ chỉ đọc:* Lần lượt gọi phương thức `findAll()` trên toàn bộ các kho lưu trữ (`WalletRepository`, `TransactionRepository`, `CategoryRepository`, `NeedGroupRepository`, `BalanceAdjustmentRepository`) để nạp ảnh chụp dữ liệu đồng bộ tại cùng một mốc thời gian, bảo đảm không làm biến đổi bất kỳ giá trị nào trong hệ thống.
  3. *Bước 3 - Chuẩn hóa dữ liệu, tuần tự hóa JSON và lưu trữ an toàn:*
     - Chuyển đổi trạng thái thực thể sang cấu trúc đối tượng sao lưu (`BackupPayloadDTO`).
     - Định dạng mọi trường số tiền sang dạng chuỗi số học chuẩn xác cố định 2 chữ số thập phân theo quy tắc độ chính xác số học tài chính, tuyệt đối không sử dụng số thực dấu phẩy động nhị phân.
     - Tuần tự hóa cấu trúc dữ liệu sang định dạng JSON với bộ mã ký tự UTF-8 không BOM.
     - Thực hiện cơ chế ghi tệp an toàn (ghi vào tệp tạm trước khi đổi tên thay thế) để tránh tạo tệp hỏng nếu quá trình ghi bị ngắt quãng giữa chừng.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thông tin kết quả xuất sao lưu (đường dẫn tệp, kích thước tệp, mốc thời gian xuất và số lượng bản ghi của từng thực thể) vào `BackupResponseDTO` và trả về cho Tầng Trình diễn.

- **Quy trình thực thi phương thức `restoreBackupData(RestoreBackupRequestDTO requestDto)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng tệp và quyền truy cập:* Kiểm tra tệp sao lưu tại đường dẫn `backupFilePath` bắt buộc phải tồn tại và ứng dụng có quyền đọc (`READ permission`). Phân tích cú pháp tệp JSON với mã hóa UTF-8; nếu tệp bị hỏng hoặc sai cấu trúc cú pháp JSON, ném `StorageException` hoặc `DataCorruptionException`.
  2. *Bước 2 - Thực thi quy trình kiểm định toàn vẹn 4 bước nghiêm ngặt (All-or-Nothing):*
     - *Kiểm tra Bước 1 (Định dạng và số liệu theo quy tắc độ chính xác tài chính):* Xác thực toàn bộ các trường bắt buộc của các thực thể phải hiện diện đầy đủ, ngày tháng đúng chuẩn `YYYY-MM-DD` và số tiền đúng định dạng số học chuẩn xác (ném `DataCorruptionException` nếu vi phạm).
     - *Kiểm tra Bước 2 (Toàn vẹn danh mục theo quy tắc cấu trúc danh mục ba cấp):* Kiểm tra cấu trúc phân cấp 3 cấp, bảo đảm mọi danh mục chi tiết đều trực thuộc một nhóm nhu cầu cha hợp lệ, không có danh mục mồ côi và Loại thu chi của danh mục tương thích với giao dịch liên kết (ném `DataCorruptionException` nếu vi phạm).
     - *Kiểm tra Bước 3 (Toàn vẹn ví và chuyển tiền theo quy tắc ghi nhận đồng thời):* Mọi giao dịch thu chi và chuyển tiền phải trỏ tới ví có trong danh sách ví của tệp; các giao dịch chuyển tiền phải bảo đảm cân bằng giữa ví nguồn và ví đích; mọi khoản phí chuyển tiền (`TransactionFee`) phải liên kết với một giao dịch chuyển tiền đang tồn tại trong tệp (ném `DataCorruptionException` nếu vi phạm).
     - *Kiểm tra Bước 4 (Bảo toàn số dư không âm theo quy tắc bảo toàn số dư ví):* Tái tính toán số dư độc lập cho toàn bộ các ví từ số dư ban đầu kết hợp toàn bộ lịch sử dòng tiền và các bản ghi kiểm kê điều chỉnh trong tệp sao lưu; bảo đảm không có bất kỳ ví nào bị âm số dư tại bất kỳ thời điểm nào (ném `InsufficientBalanceException` hoặc `DataCorruptionException` nếu phát hiện số dư âm).
     - *Cơ chế hủy bỏ bảo vệ:* Nếu phát hiện bất kỳ sai lệch nào tại bất kỳ bước kiểm tra nào, hệ thống lập tức hủy bỏ toàn bộ quá trình phục hồi, giữ nguyên vẹn 100% dữ liệu hiện tại của hệ thống.
  3. *Bước 3 - Thay thế dữ liệu nguyên tử trên toàn bộ kho lưu trữ:* Khi và chỉ khi tệp sao lưu vượt qua đầy đủ cả 4 bước kiểm tra toàn vẹn, hệ thống tiến hành xóa toàn bộ dữ liệu hiện tại trong bộ nhớ và ghi nạp đồng bộ toàn bộ các thực thể tái tạo từ tệp sao lưu vào các kho lưu trữ tương ứng (`WalletRepository`, `CategoryRepository`, `NeedGroupRepository`, `TransactionRepository`, `BalanceAdjustmentRepository`), cập nhật lại số dư ví và cấu trúc danh mục.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thông báo phục hồi thành công kèm số lượng thực thể đã phục hồi vào `RestoreResultDTO` và chuyển giao cho Tầng Trình diễn.

- **Quy trình thực thi phương thức `reconcileSystemData()`:**
  1. *Bước 1 - Khởi tạo tiến trình đối soát chỉ đọc:* Tiếp nhận yêu cầu đối soát toàn vẹn dữ liệu từ người dùng hoặc theo lịch định kỳ; thiết lập phiên làm việc thuần túy ở chế độ chỉ đọc, không làm biến đổi bất kỳ bản ghi tài chính nào.
  2. *Bước 2 - Nạp dữ liệu đồng thời từ các kho lưu trữ:* Nạp toàn bộ danh sách ví, giao dịch thu chi, chuyển tiền nội bộ, bản ghi kiểm kê số dư và danh mục phân loại từ các kho lưu trữ tương ứng; các khoản chi phí phát sinh (`TransactionFee`) được trích xuất trực tiếp từ các giao dịch cha chứa phí theo quan hệ hợp thành mà không cần kho lưu trữ phí độc lập.
  3. *Bước 3 - Quét và đối soát độc lập 4 điều kiện toàn vẹn tài chính:*
     - *Điều kiện 1 (Bảo toàn số dư ví theo quy tắc bảo toàn số dư ví):* Tính toán lại số dư độc lập từ toàn bộ lịch sử dòng tiền (gồm các khoản thu, chi, chuyển tiền đi/đến, chi phí và mốc kiểm kê điều chỉnh số dư) cho từng ví; so khớp với số dư hiện hành (`currentBalance`) đang ghi nhận trên ví. Sai số cho phép bằng đúng 0.00 VNĐ theo quy tắc độ chính xác số học tài chính.
     - *Điều kiện 2 (Cân bằng nguồn và đích khi chuyển tiền theo quy tắc chuyển tiền nội bộ):* Quét toàn bộ các giao dịch chuyển tiền nội bộ, đối soát số tiền bị trừ tại ví nguồn phải bằng chính xác số tiền được cộng tại ví đích (không tính khoản chi phí phát sinh).
     - *Điều kiện 3 (Tính toàn vẹn danh mục theo quy tắc cấu trúc danh mục ba cấp):* Quét toàn bộ sổ giao dịch thu chi, bảo đảm mọi giao dịch đều gắn với danh mục chi tiết hợp lệ cấp 3 (bao gồm cả danh mục đã xóa mềm) và không có giao dịch nào bị sai lệch Loại thu chi (`INCOME`/`EXPENSE`).
     - *Điều kiện 4 (Toàn vẹn liên kết phí giao dịch):* Quét toàn bộ các khoản chi phí phát sinh gắn kèm giao dịch thu chi và chuyển tiền, bảo đảm mọi bản ghi phí đều thuộc về một giao dịch hợp lệ đang tồn tại, không có phí mồ côi.
     - Phân loại kết quả kiểm tra theo các mức độ nghiêm trọng (`INFO`, `WARNING`, `ERROR`) và tổng hợp đề xuất hành động khắc phục cụ thể (như tự động tính lại số dư hoặc khuyến nghị phục hồi từ bản sao lưu).
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Tổng hợp trạng thái kiểm tra của cả 4 điều kiện (Đạt hoặc Không đạt), số lượng bản ghi đã quét, danh sách chi tiết các sai lệch phát hiện và hành động đề xuất vào đối tượng `ReconciliationReportDTO` và trả về Tầng Trình diễn để kết xuất báo cáo đối soát.
