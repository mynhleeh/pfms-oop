# 04. Thiết kế Tầng Lưu trữ và Truy xuất Dữ liệu

Tầng lưu trữ quản lý vòng đời truy xuất và lưu giữ dữ liệu của hệ thống, cung cấp lớp trừu tượng hóa mô phỏng tập hợp các đối tượng nghiệp vụ trong bộ nhớ. Toàn bộ giao diện kho lưu trữ được định nghĩa trong gói `com.pfms.domain.repository` thuộc Tầng Nghiệp vụ, tuân thủ nguyên lý Nghịch đảo Phụ thuộc. Tầng Dịch vụ chỉ tương tác với các giao diện trừu tượng này mà không phụ thuộc vào bất kỳ công nghệ lưu trữ cụ thể nào.

Các lớp cài đặt kho lưu trữ được đặt tại Tầng Hạ tầng gồm lưu trữ tệp tin trong gói `com.pfms.infrastructure.file` phục vụ vận hành thực tế và lưu trữ bộ nhớ trong gói `com.pfms.infrastructure.memory` phục vụ kiểm thử tự động. Cơ chế ánh xạ dữ liệu hai chiều chuyển đổi giữa thực thể miền và các bản ghi phẳng, bảo đảm các chi tiết định dạng tệp không rò rỉ vào mô hình nghiệp vụ.

## 4.1 Giao diện Lưu trữ Cơ sở

Giao diện lưu trữ cơ sở định hình khung thao tác dữ liệu chuẩn hóa cho toàn bộ các thực thể trong hệ thống, loại bỏ sự trùng lặp mã nguồn và thiết lập quy ước chung cho các thao tác truy xuất. Giao diện được định vị trong gói `com.pfms.domain.repository` thuộc Tầng Nghiệp vụ, sử dụng cơ chế tham số hóa kiểu trong Java để tiếp nhận kiểu thực thể `E` và kiểu định danh duy nhất `ID`. Việc xây dựng giao diện thuần Java SE giúp hệ thống không phụ thuộc vào bất kỳ thư viện hay khung công nghệ bên ngoài nào, bảo đảm tính độc lập cốt lõi của miền nghiệp vụ.

Giao diện cơ sở đóng vai trò là một hợp đồng trừu tượng mô phỏng tập hợp các đối tượng trong bộ nhớ, quy định cách thức tương tác đồng nhất giữa Tầng Dịch vụ và Tầng Hạ tầng. Mọi kho lưu trữ chuyên biệt của từng thực thể đều kế thừa giao diện này để sở hữu các phương thức quản lý vòng đời cơ bản, đồng thời áp dụng quy chuẩn nghiêm ngặt về việc sử dụng đối tượng bọc để chống lỗi tham chiếu rỗng và cơ chế đóng gói ngoại lệ lưu trữ an toàn.

### 4.1.1 Thao tác Dữ liệu Dùng chung

Giao diện `GenericRepository<E, ID>` định nghĩa tập hợp các thao tác dữ liệu cơ sở cho mọi thực thể nghiệp vụ, thiết lập chuẩn tương tác đồng nhất giữa Tầng Dịch vụ và Tầng Hạ tầng.

**Đặc tả phương thức cơ sở**

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `save` | `E entity` | `E` | Lưu thực thể vào kho lưu trữ; cập nhật trạng thái nếu mã định danh đã tồn tại hoặc tạo mới nếu chưa có, trả về thực thể sau khi lưu. |
| `findById` | `ID id` | `Optional<E>` | Truy vấn thực thể theo mã định danh duy nhất; trả về đối tượng bọc nếu tìm thấy hoặc đối tượng rỗng nếu không tồn tại. |
| `findAll` | Không | `List<E>` | Lấy toàn bộ danh sách thực thể trong kho lưu trữ; trả về danh sách rỗng nếu chưa có dữ liệu, không bao giờ trả về giá trị rỗng. |
| `existsById` | `ID id` | `boolean` | Kiểm tra sự tồn tại của thực thể theo mã định danh mà không cần nạp toàn bộ thuộc tính của đối tượng vào bộ nhớ máy ảo. |
| `deleteById` | `ID id` | `boolean` | Xóa bản ghi tương ứng với mã định danh khỏi kho lưu trữ; trả về kết quả đúng nếu xóa thành công, trả về sai nếu không tìm thấy. |

**Quy tắc thiết kế giao diện**

- Ràng buộc tham số kiểu: Tham số `E` đại diện cho thực thể nghiệp vụ; tham số `ID` đại diện cho khóa chính bất biến (`UUID`).
- Kiểm tra hợp lệ đối số: Mọi phương thức từ chối tham số đầu vào rỗng (`null`) và ném `IllegalArgumentException` nếu vi phạm.

### 4.1.2 Quy chuẩn Trả về và Xử lý Lỗi

Tầng lưu trữ thiết lập hai quy chuẩn bắt buộc nhằm bảo vệ tính toàn vẹn dữ liệu và duy trì ranh giới phân tầng độc lập: quy chuẩn kiểu dữ liệu trả về chống lỗi tham chiếu rỗng và cơ chế chuyển đổi ngoại lệ kỹ thuật.

**Quy chuẩn kiểu dữ liệu trả về**

| Tình huống truy vấn | Kiểu trả về | Quy tắc xử lý |
| :--- | :--- | :--- |
| Tìm kiếm đơn lẻ theo định danh hoặc thuộc tính | `Optional<E>` | Trả về đối tượng bọc chứa thực thể nếu tìm thấy; trả về `Optional.empty()` khi không tồn tại bản ghi. Tuyệt đối không trả về giá trị rỗng (`null`) và không ném ngoại lệ trong trường hợp dữ liệu hợp lệ nhưng chưa tồn tại. |
| Truy vấn tập hợp hoặc lọc danh sách | `List<E>` | Trả về danh sách chứa các thực thể thỏa mãn điều kiện; trả về danh sách rỗng bất biến khi không có kết quả phù hợp. Nghiêm cấm trả về `null` thay cho danh sách rỗng. |

**Cơ chế đóng gói ngoại lệ lưu trữ**

Các lớp cài đặt tại Tầng Hạ tầng khi thao tác với hệ thống tệp có thể phát sinh các ngoại lệ kỹ thuật cấp thấp như lỗi vào ra dữ liệu (`IOException`), lỗi không tìm thấy tệp hoặc sai lệch định dạng bản ghi. Để ngăn chặn việc rò rỉ các chi tiết công nghệ tệp lên Tầng Dịch vụ, toàn bộ các lỗi này được đóng gói theo nguyên tắc chuyển đổi ngoại lệ:

- Định nghĩa lớp ngoại lệ chung: Hệ thống xây dựng lớp `StorageException` kế thừa từ `RuntimeException`, đặt tại gói `com.pfms.domain.exception`. Đây là ngoại lệ không bắt buộc khai báo, đại diện cho mọi sự cố hỏng hóc hoặc tắc nghẽn tại tầng vật lý.
- Đóng gói nguyên nhân gốc: Khi Tầng Hạ tầng bắt được ngoại lệ kỹ thuật cấp thấp, lớp cài đặt phải khởi tạo `StorageException` kèm thông điệp mô tả ngữ cảnh thao tác và truyền đối tượng lỗi ban đầu vào tham số nguyên nhân gốc (`cause`) để phục vụ việc ghi nhật ký kiểm toán.
- Ranh giới trách nhiệm: Tầng Dịch vụ chỉ tương tác và bắt ngoại lệ `StorageException`, hoàn toàn không tham chiếu đến các lớp ngoại lệ vào ra nguyên thủy của hệ điều hành.

## 4.2 Giao diện Lưu trữ theo Thực thể

Các giao diện lưu trữ chuyên biệt kế thừa từ `GenericRepository<E, ID>` và bổ sung các phương thức truy vấn nghiệp vụ phù hợp với đặc thù của từng thực thể. Toàn bộ các giao diện này nằm trong gói `com.pfms.domain.repository`, phục vụ trực tiếp cho Tầng Dịch vụ điều phối các ca sử dụng nghiệp vụ mà không làm lộ chi tiết công nghệ lưu trữ.

### 4.2.1 Kho Lưu trữ Ví tiền

Giao diện `WalletRepository` kế thừa `GenericRepository<Wallet, UUID>` để quản lý vòng đời lưu trữ của các nguồn tiền trong hệ thống. Kho lưu trữ cung cấp các phương thức truy vấn mở rộng giúp `WalletService` kiểm tra ví mặc định, xác thực tính duy nhất của tên ví, lọc danh sách theo trạng thái và thống kê số lượng ví phục vụ phân tích cơ cấu tài sản.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`, `UC-WAL-02`, `UC-WAL-03`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-WAL-001`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.WalletRepository` kế thừa `GenericRepository<Wallet, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `findByName` | `String name` | `Optional<Wallet>` | Tìm ví theo tên chính xác sau khi cắt khoảng trắng hai đầu và so khớp không phân biệt hoa thường; trả về `Optional.empty()` nếu chưa tồn tại, phục vụ `WalletService` đối chiếu tên ví khi tạo mới hoặc cập nhật. |
| `existsByName` | `String name` | `boolean` | Kiểm tra nhanh sự tồn tại của tên ví không phân biệt hoa thường trên toàn bộ ví đang hoạt động lẫn ví đã lưu trữ; trả về `true` nếu tên đã tồn tại để chặn đặt tên trùng lặp. |
| `findByStatus` | `WalletStatus status` | `List<Wallet>` | Lọc danh sách ví theo trạng thái vòng đời (`ACTIVE` hoặc `ARCHIVED`), trả về danh sách rỗng nếu không có kết quả, phục vụ hiển thị danh sách ví theo bộ lọc trạng thái. |
| `countByStatus` | `WalletStatus status` | `long` | Đếm số lượng ví theo trạng thái xác định (`ACTIVE` hoặc `ARCHIVED`), phục vụ kiểm soát số lượng ví hoạt động và tổng hợp cơ cấu phân bổ số dư. |
| `findDefaultWallet` | `Không` | `Optional<Wallet>` | Truy vấn ví tiền mặt mặc định của hệ thống (có cờ `isDefault` bằng `true`); trả về `Optional.empty()` khi khởi chạy lần đầu chưa có dữ liệu, phục vụ `WalletService` kiểm tra và khởi tạo ví mặc định. |

**Chỉ dẫn thiết kế và cài đặt:**

- Bổ sung 5 phương thức truy vấn chuyên biệt gồm `findByName(String)` trả về `Optional<Wallet>`, `existsByName(String)` trả về `boolean`, `findByStatus(WalletStatus)` trả về `List<Wallet>`, `countByStatus(WalletStatus)` trả về `long` và `findDefaultWallet()` trả về `Optional<Wallet>` để đáp ứng trọn vẹn nhu cầu khởi tạo ví mặc định, tra cứu theo tên và lọc trạng thái của `WalletService`.
- Kế thừa trực tiếp 5 phương thức cơ sở (`save`, `findById`, `findAll`, `existsById`, `deleteById`) từ `GenericRepository<Wallet, UUID>` mà không khai báo lặp lại; thao tác `deleteById` chỉ được thực thi sau khi tầng dịch vụ xác nhận ví cần xóa không phải ví mặc định và chưa từng phát sinh lịch sử giao dịch hay kiểm kê.
- Chuẩn hóa chuỗi đầu vào bằng cách cắt khoảng trắng hai đầu và so khớp tên ví không phân biệt hoa thường trên cả ví `ACTIVE` lẫn `ARCHIVED`; giải phóng tên ví khỏi phạm vi kiểm tra duy nhất khi xóa vĩnh viễn thành công và khóa thao tác xóa hoặc lưu trữ đối với ví `"Tiền mặt"` mặc định nhằm duy trì tối thiểu một ví `ACTIVE`.

### 4.2.2 Kho Lưu trữ Giao dịch

Giao diện `TransactionRepository` kế thừa `GenericRepository<Transaction, UUID>` để lưu trữ đa hình toàn bộ biến động dòng tiền thu chi và chuyển tiền nội bộ. Kho lưu trữ cung cấp các phép lọc lịch sử theo ví, khoảng thời gian, danh mục và hàm tổng hợp số liệu chỉ đọc cho `TransactionService`, `TransferService`, `CategoryService` và `ReportService`.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TXN-02`, `UC-TXN-03`, `UC-TXN-04`, `UC-TRF-01`, `UC-TRF-02`, `UC-TRF-03`, `UC-REP-01`, `UC-REP-02`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`, `FR-TXN-04`, `FR-TXN-05`, `FR-TRF-01`, `FR-TRF-02`, `FR-REP-01`, `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-CAT-001`, `ERR-TRF-001`, `ERR-TRF-002`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.TransactionRepository` kế thừa `GenericRepository<Transaction, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `findByWalletId` | `UUID walletId` | `List<Transaction>` | Truy vấn toàn bộ giao dịch thu chi tại ví, giao dịch chuyển tiền có ví làm nguồn hoặc đích và giao dịch có trích phí từ ví; phục vụ `WalletService` kiểm tra điều kiện xóa ví rỗng lịch sử và lọc biến động theo ví. |
| `findByDateRange` | `LocalDate startDate, LocalDate endDate` | `List<Transaction>` | Lọc danh sách giao dịch phát sinh trong đoạn từ `startDate` đến `endDate` (tính cả hai mốc biên) ở chế độ chỉ đọc; phục vụ tra cứu lịch sử và tổng hợp báo cáo dòng tiền theo kỳ. |
| `findByDateRangeAndType` | `LocalDate startDate, LocalDate endDate, TransactionType type` | `List<Transaction>` | Lọc các giao dịch thu chi thuộc khoảng ngày và chiều dòng tiền (`INCOME` hoặc `EXPENSE`) ở chế độ chỉ đọc; phục vụ `ReportService` phân tích cơ cấu thu chi theo kỳ. |
| `findByCategoryId` | `UUID categoryId` | `List<Transaction>` | Truy vấn danh sách giao dịch thu chi hoặc giao dịch có khoản phí hợp thành gắn với mã danh mục cấp 3 xác định; phục vụ lọc lịch sử theo danh mục và gom cụm số liệu chi tiêu. |
| `existsByCategoryId` | `UUID categoryId` | `boolean` | Kiểm tra nhanh sự tồn tại của giao dịch chính hoặc khoản phí hợp thành tham chiếu tới danh mục cấp 3; phục vụ `CategoryService` quyết định xóa vật lý hay xóa mềm danh mục. |
| `sumAmountByFilter` | `LocalDate startDate, LocalDate endDate, TransactionType type` | `BigDecimal` | Tính tổng số tiền phát sinh theo khoảng ngày và chiều thu chi ở chế độ chỉ đọc, trả về `BigDecimal.ZERO` chuẩn 2 chữ số thập phân nếu không có bản ghi khớp, phục vụ kết xuất nhanh báo cáo tài chính. |

**Chỉ dẫn thiết kế và cài đặt:**

- Đặc tả nhóm phương thức truy vấn mở rộng ở chế độ chỉ đọc gồm `findByWalletId(UUID)`, `findByDateRange(LocalDate, LocalDate)`, `findByDateRangeAndType(LocalDate, LocalDate, TransactionType)`, `findByCategoryId(UUID)` trả về `List<Transaction>`, `existsByCategoryId(UUID)` trả về `boolean` và `sumAmountByFilter(LocalDate, LocalDate, TransactionType)` trả về `BigDecimal`; tầng dịch vụ trích xuất `LocalDate` từ `TransactionDate` khi truyền tham số lọc và bọc kết quả `BigDecimal` vào đối tượng giá trị `Money`.
- Kế thừa 5 phương thức cơ sở từ `GenericRepository<Transaction, UUID>` mà không khai báo lặp lại, đồng thời lưu trữ đa hình cả `IncomeExpenseTransaction` và `TransferTransaction` thông qua trường phân biệt loại bản ghi khi tuần tự hóa và giải tuần tự hóa dữ liệu tại tầng hạ tầng.
- Sắp xếp mặc định danh sách kết quả theo ngày giao dịch `transactionDate` giảm dần và tiêu chí phụ theo mốc tạo `createdAt` giảm dần; bảo toàn quan hệ hợp thành sinh tử với `TransactionFee` bằng cách lưu lồng đối tượng phí trong giao dịch gốc và xóa đồng thời khi gọi `deleteById` để ngăn phát sinh bản ghi phí mồ côi.

### 4.2.3 Kho Lưu trữ Danh mục Thu Chi

(Mô tả khoảng 1 đến 2 câu về vai trò của CategoryRepository trong việc kế thừa GenericRepository<Category, UUID>, quản lý cấu trúc cây phân loại 3 cấp, kiểm tra trùng lặp tên danh mục trong nhóm và xử lý xóa mềm bảo toàn lịch sử giao dịch cho CategoryService)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-04`, `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`, `ERR-CAT-004`, `ERR-CAT-005`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.CategoryRepository` kế thừa `GenericRepository<Category, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| (Tên phương thức truy vấn) | (Kiểu và tên tham số) | (Kiểu trả về: List<Category>, Optional<Category>, boolean) | (Quy tắc lọc phân cấp cây danh mục và mục đích phục vụ CategoryService) |

**Chỉ dẫn thiết kế và cài đặt:**

- (Nêu các phương thức truy vấn mở rộng cần đặc tả gồm: lấy danh sách danh mục theo loại thu chi findByType nhận TransactionType trả về List<Category>, tìm danh mục con theo mã nhóm nhu cầu cha findByGroupId trả về List<Category>, kiểm tra trùng tên danh mục trong cùng nhóm cha existsByNameAndGroupId trả về boolean, và lọc danh mục đang hoạt động findActiveCategories loại trừ trạng thái đã xóa mềm)
- (Nêu quy tắc kế thừa 5 phương thức CRUD cơ sở từ GenericRepository<Category, UUID>, bảo đảm thao tác xóa deleteById áp dụng cơ chế xóa mềm cập nhật trạng thái DELETED thay vì xóa vật lý)
- (Nêu quy tắc kiểm soát tính toàn vẹn cây danh mục 3 cấp theo BR-FIN-04 và bảo vệ nhóm nhu cầu duy nhất còn lại không bị xóa theo ERR-CAT-002)

### 4.2.4 Kho Lưu trữ Kiểm kê Số dư

Giao diện `BalanceAdjustmentRepository` kế thừa `GenericRepository<BalanceAdjustmentRecord, UUID>` để ghi nhận lịch sử kiểm kê số dư thực tế của các ví. Kho lưu trữ cung cấp các phương thức truy xuất vết kiểm toán theo ví và tra cứu lần đối soát gần nhất phục vụ `WalletService` cùng `DataManagementService`.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.BalanceAdjustmentRepository` kế thừa `GenericRepository<BalanceAdjustmentRecord, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `findByWalletId` | `UUID walletId` | `List<BalanceAdjustmentRecord>` | Truy vấn danh sách bản ghi kiểm kê của ví xác định, sắp xếp theo ngày ghi nhận `adjustedAt` giảm dần và thứ tự tạo bản ghi giảm dần; trả về danh sách rỗng nếu ví chưa từng kiểm kê, phục vụ kiểm tra điều kiện xóa ví và đối soát số dư. |
| `findLatestByWalletId` | `UUID walletId` | `Optional<BalanceAdjustmentRecord>` | Truy vấn bản ghi kiểm kê gần nhất của một ví theo mốc ngày `adjustedAt` và thứ tự ghi nhận; trả về `Optional.empty()` nếu ví chưa có lịch sử kiểm kê, phục vụ tra cứu nhanh lần cân đối số dư cuối cùng. |

**Chỉ dẫn thiết kế và cài đặt:**

- Đặc tả 2 phương thức truy vấn mở rộng gồm `findByWalletId(UUID)` trả về `List<BalanceAdjustmentRecord>` để kiểm tra lịch sử trước khi xóa ví hoặc đối soát toàn vẹn, và `findLatestByWalletId(UUID)` trả về `Optional<BalanceAdjustmentRecord>` để lấy bản ghi điều chỉnh mới nhất của ví.
- Kế thừa từ `GenericRepository<BalanceAdjustmentRecord, UUID>` nhưng thực thi nghiêm ngặt cơ chế chỉ cho phép ghi thêm bản ghi mới: phương thức `save` chỉ lưu bản ghi kiểm kê mới bất biến, trong khi thao tác cập nhật bản ghi cũ hoặc gọi `deleteById` bị chặn và ném `UnsupportedOperationException` nhằm bảo vệ tính toàn vẹn của vết kiểm toán.
- Bảo đảm mọi bản ghi lưu trữ đều có số dư thực tế `actualBalance` không âm và độ lệch `difference` khác `0.00 VNĐ`; khoản chênh lệch chỉ được `WalletService` cập nhật trực tiếp vào số dư ví liên kết mà không sinh giao dịch thu chi giả mạo trong `TransactionRepository`.

## 4.3 Cài đặt Hạ tầng Lưu trữ

Tầng Hạ tầng (`com.pfms.infrastructure`) hiện thực hóa toàn bộ các giao diện kho lưu trữ đã khai báo tại Tầng Nghiệp vụ (`com.pfms.domain.repository`), bảo đảm nguyên lý đảo ngược phụ thuộc. Hệ thống cung cấp hai cơ chế lưu trữ chuyên biệt: bộ điều hợp tệp tin (`com.pfms.infrastructure.file`) phục vụ lưu trữ bền vững trong môi trường vận hành thực tế và bộ điều hợp bộ nhớ (`com.pfms.infrastructure.memory`) phục vụ môi trường kiểm thử tự động.

Hai cơ chế này đóng kín toàn bộ chi tiết thao tác vào ra vật lý và cấu trúc dữ liệu nội bộ. Tầng Hạ tầng chịu trách nhiệm chuyển đổi hai chiều giữa dữ liệu lưu trữ và thực thể nghiệp vụ, quản lý an toàn luồng dữ liệu, đồng thời đóng gói các ngoại lệ vào ra cấp thấp thành `StorageException` nhằm giữ cho tầng nghiệp vụ hoàn toàn độc lập với môi trường thực thi.

### 4.3.1 Định dạng và Cấu trúc Bản ghi

Hệ thống lưu trữ toàn bộ trạng thái nghiệp vụ trên tệp `data/pfms_data.json` tại ổ đĩa cục bộ mà không dùng hệ quản trị cơ sở dữ liệu bên ngoài. Bộ điều hợp `JsonDataStore` cùng bốn lớp `JsonWalletRepository`, `JsonTransactionRepository`, `JsonCategoryRepository` và `JsonBalanceAdjustmentRepository` trong gói `com.pfms.infrastructure.file` chuyển đổi hai chiều giữa bản ghi `JSON` và thực thể miền.

Tệp dữ liệu lưu trữ theo định dạng `JSON` với bảng mã `UTF-8`. Các trường số tiền lưu dưới dạng chuỗi số học cố định 2 chữ số thập phân để giữ độ chính xác, mốc thời gian lưu theo định dạng `yyyy-MM-dd` và mã định danh lưu dưới dạng chuỗi `UUID`.

**Cấu trúc thuộc tính tệp dữ liệu:**

Cấu trúc các trường dữ liệu `JSON` lưu trữ trạng thái của 4 nhóm thực thể trên đĩa cứng:

| Trường dữ liệu | Kiểu dữ liệu JSON | Bắt buộc | Ràng buộc và Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `wallets` | `array` | Bắt buộc | Danh sách thực thể `Wallet`, mỗi phần tử gồm `walletId` (chuỗi `UUID`), `name` (1 đến 50 ký tự), `currentBalance` (chuỗi thập phân 2 chữ số không âm), `status` (`ACTIVE` hoặc `ARCHIVED`), `isDefault` (`boolean`), `note` (tối đa 255 ký tự) và `createdAt`. |
| `transactions` | `array` | Bắt buộc | Danh sách thực thể `Transaction` đa hình theo `recordType` (`INCOME_EXPENSE` gồm `walletId`, `categoryId`, `transactionType`; `TRANSFER` gồm `sourceWalletId`, `destinationWalletId`), các trường chung `transactionId`, `amount` (chuỗi thập phân dương 2 chữ số), `transactionDate` (`yyyy-MM-dd`), `note`, `createdAt` và đối tượng con `fee` (`TransactionFee`). |
| `categories` | `array` | Bắt buộc | Danh sách cây phân loại 3 cấp gồm `NeedGroup` cấp 2 (`groupId`, `groupName`, `flowType`, `groupStatus`) và `Category` cấp 3 (`categoryId`, `name`, `groupId`, `flowType`, `status`, `createdAt`), gắn với nhánh `TransactionType` (`INCOME` hoặc `EXPENSE`) và trạng thái `ACTIVE` hoặc `DELETED`. |
| `adjustments` | `array` | Bắt buộc | Danh sách bản ghi kiểm kê bất biến `BalanceAdjustmentRecord`, mỗi phần tử gồm `recordId`, `walletId` (chuỗi `UUID`), `previousBalance`, `actualBalance` (chuỗi thập phân không âm), `difference` (khác `0.00`), `adjustedAt` (`yyyy-MM-dd`) và `reason` (tối đa 255 ký tự). |

**Chỉ dẫn thiết kế và cài đặt:**

- Đối tượng gốc của tệp `JSON` luôn chứa đủ 4 mảng `wallets`, `transactions`, `categories` và `adjustments`; nhóm thực thể chưa có dữ liệu được biểu diễn bằng mảng rỗng `[]` thay vì `null` hoặc lược bỏ trường.
- Các khóa định danh và tham chiếu (`walletId`, `transactionId`, `categoryId`, `groupId`, `recordId`, `feeId`) lưu dạng chuỗi `UUID` và khôi phục qua `UUID.fromString`. Các trường tiền tệ (`currentBalance`, `amount`, `previousBalance`, `actualBalance`, `difference`) lưu dạng chuỗi 2 chữ số thập phân và nạp vào `Money` (`BigDecimal`, `RoundingMode.HALF_EVEN`) để tránh sai số dấu chấm động.
- Đối tượng `TransactionFee` (`feeId`, `parentTransactionId`, `amount`, `walletId`, `categoryId`) được nhúng trực tiếp vào trường `fee` của bản ghi `Transaction` gốc (`null` nếu miễn phí) để gắn kết vòng đời hợp thành. Cây danh mục 3 cấp được tái dựng qua liên kết từ `Category` lên `NeedGroup` (`groupId`) và nhánh `TransactionType` (`flowType`) khi nạp tệp.

### 4.3.2 Cơ chế Đọc Ghi và An toàn Dữ liệu

`JsonDataStore` ghi dữ liệu qua tệp tạm kết hợp đổi tên nguyên tử để chống hỏng cấu trúc tệp khi ứng dụng dừng đột ngột, mất điện hoặc đầy đĩa. Trong lần chạy đầu tiên khi chưa có tệp lưu trữ, bộ điều hợp tự tạo thư mục dữ liệu và tệp `JSON` gốc chứa 4 mảng chuẩn cùng bộ danh mục mặc định.

**Quy trình ghi tệp nguyên tử**

Để ngăn hỏng tệp dữ liệu khi mất điện hoặc tiến trình bị ngắt giữa chừng, thao tác lưu dữ liệu được thực thi qua quy trình hoán đổi tệp:

| Bước | Thao tác kỹ thuật | Mục đích và Cơ chế bảo vệ |
| :--- | :--- | :--- |
| Bước 1 | Tuần tự hóa đồ thị đối tượng hiện hành thành chuỗi `JSON` mã hóa `UTF-8` và ghi vào tệp tạm (`pfms_data.json.tmp`) cùng thư mục với tệp chính. | Tách quá trình ghi luồng byte mới khỏi tệp dữ liệu chính; nếu tràn đĩa hoặc ngắt tiến trình giữa chừng thì tệp chính vẫn giữ nguyên trạng thái cũ. |
| Bước 2 | Gọi `flush` trên luồng ghi và `FileChannel.force` để đẩy toàn bộ dữ liệu từ bộ đệm hệ điều hành xuống đĩa vật lý. | Bảo đảm nội dung tệp tạm đã ghi bền vững trên phần cứng lưu trữ trước khi thay thế tệp chính. |
| Bước 3 | Đóng luồng tệp tạm và gọi `Files.move` với cờ `StandardCopyOption.ATOMIC_MOVE` cùng `StandardCopyOption.REPLACE_EXISTING` để đè tệp tạm lên tệp chính. | Hoán đổi con trỏ tệp ở mức hệ thống tệp trong một thao tác nguyên tử, giữ cho tệp chính luôn ở trạng thái hợp lệ trước hoặc sau giao tác. |

**Chỉ dẫn thiết kế và cài đặt:**

- Mọi thao tác cập nhật ở các kho lưu trữ `JSON` đều ủy quyền cho `JsonDataStore` thực thi tuần tự 3 bước gồm ghi tệp tạm `.tmp`, ép xả dữ liệu xuống đĩa vật lý và hoán đổi nguyên tử đè lên tệp chính, không mở luồng ghi đè cắt ngắn trực tiếp lên tệp đang vận hành.
- Khi khởi động, `JsonDataStore` tự tạo cây thư mục qua `Files.createDirectories` và ghi tệp khởi tạo 4 mảng rỗng nếu đường dẫn chưa tồn tại; nếu tệp đã có thì đọc qua `BufferedReader` (`StandardCharsets.UTF_8`) và ném `DataCorruptionException` khi phát hiện lỗi cú pháp hoặc sai kiểu dữ liệu.
- Khi gặp lỗi vào ra (`IOException`, thiếu quyền ghi, đầy đĩa), khối xử lý ngoại lệ đóng luồng, xóa tệp `.tmp` dở dang qua `Files.deleteIfExists`, giữ nguyên tệp chính và bọc lỗi gốc thành `StorageException`. Riêng thao tác khôi phục từ `DataManagementService` tự tạo bản sao lưu dự phòng (`backup_YYYYMMDD_HHMMSS.json` tại `backups/`) và xác thực 4 bước tệp nguồn trước khi hoán đổi nguyên tử.

### 4.3.3 Cài đặt Lưu trữ Trong Bộ Nhớ

Bộ điều hợp lưu trữ bộ nhớ thuộc Tầng Hạ tầng hiện thực hóa các giao diện kho lưu trữ hoàn toàn trên bộ nhớ RAM, phục vụ việc chạy các ca kiểm thử đơn vị độc lập và tốc độ cao. Toàn bộ các lớp cài đặt này được đặt trong gói `com.pfms.infrastructure.memory`, loại bỏ sự phụ thuộc vào hệ thống tệp và bảo đảm môi trường kiểm thử không bị ô nhiễm dữ liệu rác.

**Cấu trúc lớp cài đặt bộ nhớ:**

Cấu trúc các lớp cài đặt kho lưu trữ hoạt động trên bộ nhớ RAM phục vụ môi trường kiểm thử:

| Lớp cài đặt | Giao diện hiện thực | Cấu trúc dữ liệu nội bộ | Trách nhiệm chính |
| :--- | :--- | :--- | :--- |
| (Tên lớp cài đặt bộ nhớ) | (Tên giao diện kho lưu trữ) | (Cấu trúc bảng băm hoặc danh sách trong RAM) | (Thao tác lưu trữ tạm thời và phương thức dọn sạch dữ liệu) |

**Chỉ dẫn thiết kế và cài đặt:**

- (Nêu việc sử dụng bảng băm ConcurrentHashMap để quản lý các thực thể theo khóa chính UUID trong bộ nhớ máy ảo)
- (Nêu phương thức dọn sạch dữ liệu clear để thiết lập lại trạng thái ban đầu sau mỗi ca kiểm thử đơn vị)
- (Nêu tính chất không bền vững: dữ liệu giải phóng hoàn toàn khi kết thúc tiến trình chạy kiểm thử mà không để lại tệp rác)

### 4.3.4 So sánh Đánh đổi Giữa Hai Cơ chế Lưu trữ

Việc duy trì song song hai cơ chế lưu trữ xuất phát từ sự khác biệt căn bản về yêu cầu giữa môi trường vận hành thực tế và môi trường kiểm thử tự động. Thay vì sử dụng các thư viện giả lập đối tượng phức tạp, hệ thống áp dụng nguyên lý đảo ngược phụ thuộc để cung cấp hai bộ điều hợp cụ thể cho cùng một tập giao diện kho lưu trữ. Cơ chế tiêm phụ thuộc cho phép tráo đổi linh hoạt giữa tệp tin và bộ nhớ tại điểm khởi tạo ứng dụng mà không làm thay đổi logic nghiệp vụ.

**Bảng so sánh đối chiếu đa chiều giữa hai cơ chế lưu trữ:**

| Tiêu chí so sánh | Lưu trữ bằng tệp tin (`.file`) | Lưu trữ trong bộ nhớ (`.memory`) |
| :--- | :--- | :--- |
| Phương tiện lưu trữ vật lý | Tệp văn bản JSON mã hóa UTF-8 trên ổ đĩa cục bộ | Bảng băm `ConcurrentHashMap` trong bộ nhớ RAM |
| Tính bền vững dữ liệu | Dữ liệu được bảo toàn vẹn toàn qua các phiên làm việc | Dữ liệu tự động giải phóng khi tiến trình JVM kết thúc |
| Tốc độ truy xuất và độ trễ | Phụ thuộc tốc độ I/O đĩa và thời gian phân giải chuỗi JSON | Tốc độ truy xuất trực tiếp trên RAM cấp độ nano-giây |
| Phụ thuộc môi trường thực thi | Yêu cầu quyền đọc ghi tệp và đường dẫn thư mục hợp lệ | Hoàn toàn độc lập với hệ điều hành và hệ thống tệp |
| Tính cô lập trong kiểm thử | Cần cơ chế dọn dẹp tệp dữ liệu rác sau mỗi ca kiểm thử | Cô lập tuyệt đối giữa các ca kiểm thử qua lệnh `clear()` |
| Độ phức tạp cài đặt | Cần thuật toán ghi tệp nguyên tử và cơ chế bọc tệp tạm | Cài đặt đơn giản dựa trên thao tác bản đồ khóa giá trị |
| Cơ chế xử lý lỗi | Bắt lỗi `IOException` và bọc thành `StorageException` | Rủi ro lỗi thấp, chỉ xảy ra khi cạn kiệt bộ nhớ RAM |
| Mục đích sử dụng chính | Vận hành sản phẩm thực tế cho người dùng cuối | Chạy kiểm thử đơn vị và kiểm thử tích hợp tự động |

**Đánh giá kiến trúc và ngưỡng chuyển đổi công nghệ:**

Cơ chế lưu trữ tệp tin JSON được lựa chọn để tối ưu hóa tính độc lập cho một ứng dụng tài chính cá nhân chạy trên máy trạm cục bộ, loại bỏ hoàn toàn gánh nặng phải cài đặt và cấu hình hệ quản trị cơ sở dữ liệu bên ngoài. Tuy nhiên, kiến trúc hệ thống cũng xác định rõ giới hạn kỹ thuật của giải pháp này:

1. **Giới hạn hiệu năng:** Khi quy mô dữ liệu vượt quá ngưỡng mười nghìn giao dịch hoặc xuất hiện nhu cầu đọc ghi đồng thời từ nhiều luồng tiến trình, việc tuần tự hóa toàn bộ tệp JSON sẽ trở thành nút thắt cổ chai về hiệu năng I/O.
2. **Khả năng mở rộng:** Nhờ sự phân tách triệt để của mẫu Kho lưu trữ, khi hệ thống đạt tới ngưỡng chuyển đổi này, lập trình viên có thể bổ sung gói điều hợp cơ sở dữ liệu mới (như SQLite hoặc H2 Database) mà không phải sửa đổi bất kỳ dòng mã nguồn nào trong Tầng Nghiệp vụ.
