# 03. Thiết kế Tầng Dịch vụ Ứng dụng và Điều phối Nghiệp vụ

Tài liệu này xác lập bản vẽ thiết kế chi tiết cho Tầng Dịch vụ ứng dụng của Hệ thống Quản lý Tài chính Cá nhân PFMS. Nội dung văn bản thực hiện chuyển đổi các ca sử dụng và kịch bản tương tác đã được xác thực trong hồ sơ Phân tích Hướng đối tượng sang cấu trúc các lớp dịch vụ điều phối luồng nghiệp vụ trên nền tảng ngôn ngữ Java.

Bản vẽ thiết kế tập trung giải quyết ba mục tiêu kỹ thuật trọng tâm: quản lý vòng đời và điều phối luồng thực thi của các ca sử dụng nghiệp vụ, đóng vai trò cầu nối trung gian giữa giao diện dòng lệnh và các thực thể nghiệp vụ; phân tách rạch ròi giữa giao diện công khai và lớp cài đặt nội bộ nhằm đóng kín chi tiết điều phối; và bảo đảm tính nguyên tử của dữ liệu tài chính khi thao tác qua các kho lưu trữ trừu tượng, đồng thời ủy quyền toàn bộ các phép tính số học cho Tầng Nghiệp vụ cốt lõi.

## 3.1 Tổng quan và Danh mục Dịch vụ Ứng dụng

Mục này xác lập cấu trúc phân tách giữa giao diện công khai và lớp cài đặt nội bộ của tầng dịch vụ, đồng thời cung cấp bảng danh mục đối chiếu các dịch vụ điều phối trong toàn bộ hệ thống.

Cấu trúc mã nguồn của tầng dịch vụ được phân định rạch ròi thành hai không gian tên:
- **Giao diện công khai (`com.pfms.service`):** Định nghĩa toàn bộ các hợp đồng phương thức mà Tầng Trình diễn (`com.pfms.cli`) được phép triệu gọi. Tầng Trình diễn chỉ phụ thuộc duy nhất vào các giao diện này, hoàn toàn không được phép tiếp xúc với lớp cài đặt cụ thể hay các thực thể nghiệp vụ.
- **Lớp cài đặt nội bộ (`com.pfms.service.impl`):** Hiện thực hóa các giao diện dịch vụ với phạm vi truy cập nội bộ gói (`package-private`), giấu kín toàn bộ logic điều phối. Lớp cài đặt tiếp nhận đối tượng truyền dữ liệu (DTO), tiêm các giao diện kho lưu trữ (`com.pfms.domain.repository`) qua hàm khởi tạo để truy xuất dữ liệu, và ủy quyền các phép tính toán số học cho các thực thể thuộc Tầng Nghiệp vụ.

**Bảng tổng hợp danh mục dịch vụ ứng dụng:**

| Dịch vụ | Giao diện công khai | Lớp cài đặt nội bộ | Trách nhiệm điều phối chính |
| :--- | :--- | :--- | :--- |
| **Quản lý ví** | `WalletService` | `WalletServiceImpl` | Điều phối tạo ví mới, cập nhật thông tin, điều chỉnh số dư kiểm kê thực tế và tính toán cơ cấu phân bổ dòng tiền. |
| **Ghi nhận thu chi** | `TransactionService` | `TransactionServiceImpl` | Điều phối ghi nhận thu chi kèm chi phí giao dịch, cập nhật số dư ví tức thời và thực hiện quy trình hoàn nguyên hai pha khi sửa hoặc xóa. |
| **Chuyển tiền nội bộ** | `TransferService` | `TransferServiceImpl` | Điều phối chuyển khoản hai chiều giữa hai ví, hạch toán chi phí phát sinh và bảo toàn tổng tài sản hệ thống. |
| **Danh mục phân loại** | `CategoryService` | `CategoryServiceImpl` | Quản lý cấu trúc cây danh mục 3 cấp, kiểm soát tính duy nhất của tên và thực thi cơ chế xóa mềm bảo toàn lịch sử giao dịch. |
| **Báo cáo và thống kê** | `ReportService` | `ReportServiceImpl` | Vận hành ở chế độ chỉ đọc: trích xuất dữ liệu, tổng hợp dòng tiền 12 tháng, đo lường tỷ lệ tiết kiệm và phân tích cơ cấu chi tiêu. |
| **Quản trị và sao lưu** | `DataManagementService` | `DataManagementServiceImpl` | Điều phối xuất dữ liệu JSON, phục hồi dữ liệu hệ thống theo quy trình 4 bước và đối soát độc lập 4 điều kiện toàn vẹn tài chính. |

## 3.2 Dịch vụ Quản lý Ví

Dịch vụ này điều phối toàn bộ vòng đời ví thanh toán của người dùng, từ khởi tạo ví mặc định và ví tạo thêm, cập nhật thông tin và trạng thái sử dụng cho đến kiểm kê điều chỉnh số dư thực tế và tổng hợp cơ cấu phân bổ tài sản. Toàn bộ phép toán số học và kiểm soát bất biến số dư được ủy quyền cho thực thể `Wallet`, `BalanceAdjustmentRecord` cùng các đối tượng giá trị `Money` và `TransactionDate`.

**Giao diện công khai:**

Giao diện `com.pfms.service.WalletService` cung cấp các phương thức sau cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `initializeDefaultWallet` | `Không có` | `void` | `StorageException` | Kiểm tra khi khởi động ứng dụng; nếu chưa có ví mặc định thì tạo sẵn ví `"Tiền mặt"` ở trạng thái hoạt động với số dư bằng không. |
| `createWallet` | `CreateWalletRequestDto request` | `WalletResponseDto` | `BusinessException`, `DuplicateEntityException`, `StorageException` | Kiểm tra hợp lệ tên ví, kiểm tra trùng tên và khởi tạo ví mới ở trạng thái hoạt động với số dư bằng không. |
| `getWalletsSummary` | `WalletStatus statusFilter` | `WalletsSummaryDto` | `StorageException` | Truy xuất danh sách ví theo bộ lọc trạng thái, tính tổng số dư hệ thống và tỷ trọng phân bổ của từng ví ở chế độ chỉ đọc. |
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

(Mô tả khoảng 1 đến 2 câu về vai trò của dịch vụ trong việc điều phối các ca sử dụng quản lý danh mục, kiểm soát cấu trúc cây 3 cấp và xử lý xóa mềm bảo toàn lịch sử)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-04`, `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`, `ERR-CAT-004`, `ERR-CAT-005`

**Giao diện công khai:**

Giao diện `com.pfms.service.CategoryService` cung cấp các phương thức cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức nghiệp vụ) | (Tham số DTO hoặc kiểu định danh) | (Kiểu dữ liệu DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.CategoryServiceImpl` nhận `CategoryRepository`, `NeedGroupRepository` và `TransactionRepository` qua hàm khởi tạo để xử lý logic:
- (Đặc tả chi tiết quy trình thực thi cho từng phương thức theo 4 bước chuẩn: kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ; nạp thực thể và ủy quyền tính toán cho Thực thể miền hoặc Đối tượng giá trị; kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký/vết kiểm toán; đóng gói kết quả sang DTO phản hồi trước khi trả về Tầng Trình diễn)

## 3.6 Dịch vụ Báo cáo và Thống kê Tài chính

(Mô tả khoảng 1 đến 2 câu về vai trò của dịch vụ trong việc vận hành ở chế độ chỉ đọc: tổng hợp số liệu thu chi, phân tích cơ cấu chi tiêu và lập báo cáo dòng tiền)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-01`, `UC-REP-02`
- Yêu cầu chức năng: `FR-REP-01`, `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-FIN-002`

**Giao diện công khai:**

Giao diện `com.pfms.service.ReportService` cung cấp các phương thức cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức nghiệp vụ) | (Tham số DTO hoặc kiểu định danh) | (Kiểu dữ liệu DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.ReportServiceImpl` nhận `TransactionRepository`, `CategoryRepository` và `WalletRepository` qua hàm khởi tạo để xử lý logic:
- (Đặc tả chi tiết quy trình thực thi cho từng phương thức theo 4 bước chuẩn: kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ; nạp thực thể và ủy quyền tính toán cho Thực thể miền hoặc Đối tượng giá trị; kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký/vết kiểm toán; đóng gói kết quả sang DTO phản hồi trước khi trả về Tầng Trình diễn)

## 3.7 Dịch vụ Quản trị và Sao lưu Dữ liệu

(Mô tả khoảng 1 đến 2 câu về vai trò của dịch vụ trong việc điều phối xuất dữ liệu sao lưu, phục hồi dữ liệu hệ thống theo quy trình 4 bước và kiểm tra đối soát tính toàn vẹn)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-DAT-01`, `UC-DAT-02`, `UC-DAT-03`
- Yêu cầu chức năng: `FR-DAT-01`, `FR-DAT-02`, `FR-DAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`

**Giao diện công khai:**

Giao diện `com.pfms.service.DataManagementService` cung cấp các phương thức cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức nghiệp vụ) | (Tham số DTO hoặc kiểu định danh) | (Kiểu dữ liệu DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.DataManagementServiceImpl` nhận `WalletRepository`, `TransactionRepository`, `TransferRepository`, `CategoryRepository`, `NeedGroupRepository` và `BalanceAdjustmentRepository` qua hàm khởi tạo để xử lý logic:
- (Đặc tả chi tiết quy trình thực thi cho từng phương thức theo 4 bước chuẩn: kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ; nạp thực thể và ủy quyền tính toán cho Thực thể miền hoặc Đối tượng giá trị; kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký/vết kiểm toán; đóng gói kết quả sang DTO phản hồi trước khi trả về Tầng Trình diễn)
