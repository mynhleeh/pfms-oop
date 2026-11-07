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

(Mô tả khoảng 1 đến 2 câu về vai trò của WalletRepository trong việc kế thừa GenericRepository<Wallet, UUID>, cung cấp các thao tác truy vấn phục vụ kiểm tra tính duy nhất của tên ví, lọc trạng thái và kiểm soát điều kiện lưu trữ ví cho WalletService)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`, `UC-WAL-02`, `UC-WAL-03`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-WAL-001`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.WalletRepository` kế thừa `GenericRepository<Wallet, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| (Tên phương thức truy vấn) | (Kiểu và tên tham số) | (Kiểu trả về: Optional<Wallet>, List<Wallet>, boolean, long) | (Quy tắc lọc dữ liệu và mục đích phục vụ WalletService) |

**Chỉ dẫn thiết kế và cài đặt:**

- (Nêu các phương thức truy vấn mở rộng cần đặc tả gồm: tìm ví theo tên chính xác trả về Optional<Wallet>, kiểm tra tồn tại tên ví trả về boolean, lọc danh sách ví theo trạng thái trả về List<Wallet>, và đếm số lượng ví theo trạng thái trả về long)
- (Nêu quy tắc kế thừa 5 phương thức CRUD cơ sở từ GenericRepository<Wallet, UUID>, tuyệt đối không khai báo lặp lại)
- (Nêu nguyên tắc so khớp tên ví không phân biệt hoa thường theo BR-NAM-01 và kiểm soát điều kiện duy trì tối thiểu một ví hoạt động ACTIVE)

### 4.2.2 Kho Lưu trữ Giao dịch

Giao diện `com.pfms.domain.repository.TransactionRepository` kế thừa `GenericRepository<Transaction, UUID>`, quản lý truy xuất và lưu trữ đa hình các biến động dòng tiền trong hệ thống (bao gồm giao dịch thu chi và chuyển tiền). Kho lưu trữ cung cấp các phương thức truy vấn dữ liệu theo thời gian, ví, danh mục và tổng hợp số liệu thu chi, phục vụ điều phối luồng nghiệp vụ giao dịch đồng thời cung cấp dữ liệu chỉ đọc chuyên biệt cho `ReportService` để lập báo cáo tài chính và phân tích cơ cấu chi tiêu.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TXN-02`, `UC-TXN-03`, `UC-TXN-04`, `UC-TRF-01`, `UC-TRF-02`, `UC-TRF-03`, `UC-REP-01`, `UC-REP-02`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`, `FR-TXN-04`, `FR-TXN-05`, `FR-TRF-01`, `FR-TRF-02`, `FR-REP-01`, `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-CAT-001`, `ERR-TRF-001`, `ERR-TRF-002`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.TransactionRepository` kế thừa `GenericRepository<Transaction, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `findByDateRange` | `LocalDate startDate, LocalDate endDate` | `List<Transaction>` | Lọc danh sách giao dịch phát sinh trong khoảng thời gian từ `startDate` đến `endDate` (bao gồm cả hai ngày) ở chế độ chỉ đọc; phục vụ `ReportService` tổng hợp dòng tiền 12 tháng theo yêu cầu báo cáo tài chính. |
| `findByDateRangeAndType` | `LocalDate startDate, LocalDate endDate, TransactionType type` | `List<Transaction>` | Lọc danh sách giao dịch theo khoảng ngày và loại luồng tiền (`INCOME` hoặc `EXPENSE`) ở chế độ chỉ đọc; phục vụ `ReportService` phân tích cơ cấu thu chi theo nhóm nhu cầu và danh mục chi tiết. |
| `findByCategoryId` | `UUID categoryId` | `List<Transaction>` | Truy vấn danh sách giao dịch liên kết với danh mục chi tiết xác định ở chế độ chỉ đọc; phục vụ gom cụm số liệu cơ cấu chi tiêu theo danh mục cho báo cáo tài chính. |
| `sumAmountByFilter` | `LocalDate startDate, LocalDate endDate, TransactionType type` | `BigDecimal` | Tính tổng số tiền phát sinh theo khoảng thời gian và loại thu chi ở chế độ chỉ đọc; phục vụ kết xuất nhanh tổng thu, tổng chi và tỷ lệ tiết kiệm cho báo cáo tài chính theo quy tắc tính toán độ chính xác tài chính. |

**Chỉ dẫn thiết kế và cài đặt:**

- Các phương thức truy vấn mở rộng phục vụ phân hệ Báo cáo (hoạt động ở chế độ chỉ đọc, không làm biến đổi trạng thái dữ liệu): lọc giao dịch theo khoảng ngày `findByDateRange(LocalDate, LocalDate)` trả về `List<Transaction>`, lọc giao dịch theo khoảng ngày và loại thu chi `findByDateRangeAndType(LocalDate, LocalDate, TransactionType)` trả về `List<Transaction>`, lọc giao dịch theo danh mục chi tiêu `findByCategoryId(UUID)` trả về `List<Transaction>`, và tính tổng dòng tiền phát sinh `sumAmountByFilter(LocalDate, LocalDate, TransactionType)` trả về `BigDecimal` phục vụ kết xuất nhanh báo cáo tài chính định kỳ và cơ cấu chi tiêu; riêng phương thức tìm giao dịch theo mã ví `findByWalletId` phục vụ TXN/WAL được giữ nguyên theo phạm vi thiết kế giao dịch.
- (Nêu quy tắc kế thừa 5 phương thức CRUD cơ sở từ GenericRepository<Transaction, UUID>, lưu trữ đa hình cả giao dịch thu chi IncomeExpenseTransaction và chuyển tiền TransferTransaction)
- (Nêu cơ chế sắp xếp mặc định theo ngày giao dịch giảm dần và bảo toàn quan hệ hợp thành sinh tử với chi phí giao dịch TransactionFee)

### 4.2.3 Kho Lưu trữ Danh mục Thu Chi

Giao diện `com.pfms.domain.repository.CategoryRepository` kế thừa `GenericRepository<Category, UUID>`, đóng vai trò quản lý truy xuất và lưu trữ thực thể danh mục phân loại chi tiết (cấp 3) trong cấu trúc cây danh mục 3 cấp. Kho lưu trữ cung cấp các phương thức truy vấn chuyên biệt phục vụ `CategoryService` kiểm tra tính duy nhất của tên danh mục trong nhóm nhu cầu cha, lọc trạng thái hoạt động và thực thi cơ chế xóa mềm nhằm bảo toàn tính toàn vẹn tham chiếu cho lịch sử giao dịch.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-04`, `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`, `ERR-CAT-004`, `ERR-CAT-005`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.CategoryRepository` kế thừa `GenericRepository<Category, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `findByType` | `TransactionType type` | `List<Category>` | Truy vấn danh sách toàn bộ danh mục chi tiết trực thuộc các nhóm nhu cầu của một nhánh loại thu chi cố định (`INCOME` hoặc `EXPENSE`) theo quy tắc phân cấp danh mục ba cấp. |
| `findByNeedGroupId` | `UUID needGroupId` | `List<Category>` | Truy vấn danh sách các danh mục con chi tiết (cấp 3) trực thuộc một nhóm nhu cầu cha (cấp 2) xác định bởi `needGroupId`, phục vụ xây dựng cây phân cấp và duyệt danh mục theo nhóm phục vụ hiển thị cây danh mục. |
| `existsByNameAndNeedGroupId` | `String name, UUID needGroupId` | `boolean` | Kiểm tra sự tồn tại của danh mục có tên trùng khớp (sau khi cắt tỉa khoảng trắng ở hai đầu và so khớp không phân biệt hoa thường theo chuẩn đặt tên) trong cùng một nhóm nhu cầu cha đang hoạt động; trả về `true` nếu đã tồn tại nhằm ngăn ngừa lỗi trùng tên danh mục. |
| `existsByNameAndNeedGroupIdAndIdNot` | `String name, UUID needGroupId, UUID categoryId` | `boolean` | Kiểm tra sự tồn tại của danh mục khác có tên trùng khớp trong cùng nhóm nhu cầu cha loại trừ chính danh mục đang cập nhật; phục vụ kiểm tra tính duy nhất khi đổi tên danh mục. |
| `findByNameAndNeedGroupId` | `String name, UUID needGroupId` | `Optional<Category>` | Tìm kiếm danh mục theo tên trong cùng nhóm nhu cầu cha; phục vụ phát hiện danh mục đã xóa mềm để hỗ trợ người dùng khôi phục hoặc tạo mới theo luồng nghiệp vụ. |
| `countActiveByNeedGroupId` | `UUID needGroupId` | `long` | Đếm số lượng danh mục con chi tiết đang ở trạng thái hoạt động (`ACTIVE`) trực thuộc nhóm nhu cầu cha xác định; phục vụ `CategoryService` kiểm tra ràng buộc duy trì tối thiểu mục hoạt động trước khi xóa. |
| `findActiveCategories` | Không | `List<Category>` | Lọc toàn bộ danh sách danh mục đang ở trạng thái hoạt động (`ACTIVE`), loại trừ các danh mục đã bị xóa mềm (`DELETED`), phục vụ hiển thị danh mục hợp lệ khi ghi nhận giao dịch thu chi mới. |

**Chỉ dẫn thiết kế và cài đặt:**

- Các phương thức truy vấn mở rộng được đặc tả đầy đủ theo yêu cầu nghiệp vụ quản lý cây danh mục 3 cấp: `findByType` lọc danh mục theo nhánh loại thu chi cố định cấp 1 (`INCOME`/`EXPENSE`); `findByNeedGroupId` lấy danh sách danh mục con cấp 3 trực thuộc nhóm nhu cầu cha cấp 2; `existsByNameAndNeedGroupId` kiểm tra trùng tên danh mục trong cùng nhóm cha theo quy chuẩn đặt tên duy nhất; `existsByNameAndNeedGroupIdAndIdNot` kiểm tra trùng tên khi đổi tên danh mục loại trừ chính danh mục đang cập nhật; `findByNameAndNeedGroupId` tìm kiếm danh mục theo tên trong nhóm phục vụ luồng khôi phục danh mục đã xóa mềm; `countActiveByNeedGroupId` đếm số danh mục con hoạt động trong nhóm cha bảo đảm ràng buộc toàn vẹn; và `findActiveCategories` lọc danh sách danh mục đang hoạt động (`ACTIVE`), loại trừ các danh mục đã xóa mềm.
- Kế thừa 5 phương thức CRUD cơ sở từ `GenericRepository<Category, UUID>` (`save`, `findById`, `findAll`, `existsById`, `deleteById`) mà không khai báo lặp lại. Trong đó, theo quy định nghiệp vụ bảo toàn tham chiếu, thao tác xóa danh mục được tầng nghiệp vụ phối hợp kho lưu trữ xử lý: tầng nghiệp vụ kiểm tra liên kết lịch sử giao dịch thông qua `TransactionRepository.existsByCategoryId(categoryId)`; nếu danh mục đã từng phát sinh giao dịch lịch sử, hệ thống áp dụng cơ chế xóa mềm bằng cách cập nhật thuộc tính trạng thái sang `DELETED` và lưu lại thông qua `save` nhằm bảo toàn vẹn toàn lịch sử giao dịch và số liệu báo cáo; chỉ khi danh mục chưa từng phát sinh giao dịch nào mới thực thi xóa vật lý khỏi kho lưu trữ qua `deleteById`.
- Kiểm soát tính toàn vẹn cây danh mục 3 cấp: mỗi danh mục cấp 3 bắt buộc phải trực thuộc một nhóm nhu cầu cha cấp 2 hợp lệ đang hoạt động, nghiêm cấm tạo danh mục vào nhóm đã xóa hoặc tạo danh mục mồ côi. Khi chuyển nhóm cha, nhóm đích bắt buộc phải đang hoạt động và thuộc cùng nhánh loại thu chi. Đồng thời, tầng dịch vụ kiểm soát quy tắc bảo vệ nhóm nhu cầu duy nhất còn lại của một nhánh loại thu chi không được phép xóa, bảo đảm mỗi nhánh luôn duy trì tối thiểu một nhóm hoạt động.

### 4.2.4 Kho Lưu trữ Kiểm kê Số dư

(Mô tả khoảng 1 đến 2 câu về vai trò của BalanceAdjustmentRepository trong việc kế thừa GenericRepository<BalanceAdjustmentRecord, UUID>, ghi nhận lịch sử kiểm kê thực tế và truy vấn vết đối soát phục vụ WalletService)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.BalanceAdjustmentRepository` kế thừa `GenericRepository<BalanceAdjustmentRecord, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| (Tên phương thức truy vấn) | (Kiểu và tên tham số) | (Kiểu trả về: List<BalanceAdjustmentRecord>, Optional<BalanceAdjustmentRecord>) | (Quy tắc truy vấn lịch sử kiểm kê và mục đích phục vụ đối soát) |

**Chỉ dẫn thiết kế và cài đặt:**

- (Nêu các phương thức truy vấn mở rộng cần đặc tả gồm: tìm lịch sử kiểm kê theo mã ví findByWalletId trả về List<BalanceAdjustmentRecord>, và lấy bản ghi kiểm kê gần nhất của một ví findLatestByWalletId trả về Optional<BalanceAdjustmentRecord>)
- (Nêu quy tắc kế thừa từ GenericRepository<BalanceAdjustmentRecord, UUID>, nhấn mạnh tính chất chỉ thêm Append-only: không hỗ trợ thao tác cập nhật hay xóa bản ghi để bảo toàn vết kiểm toán)
- (Nêu ràng buộc số dư quan sát thực tế không âm theo BR-FIN-02 và nguyên tắc ghi nhận chênh lệch trực tiếp vào ví mà không tạo giao dịch thu chi giả mạo)

## 4.3 Cài đặt Hạ tầng Lưu trữ

Tầng Hạ tầng (`com.pfms.infrastructure`) hiện thực hóa toàn bộ các giao diện kho lưu trữ đã khai báo tại Tầng Nghiệp vụ (`com.pfms.domain.repository`), bảo đảm nguyên lý đảo ngược phụ thuộc. Hệ thống cung cấp hai cơ chế lưu trữ chuyên biệt: bộ điều hợp tệp tin (`com.pfms.infrastructure.file`) phục vụ lưu trữ bền vững trong môi trường vận hành thực tế và bộ điều hợp bộ nhớ (`com.pfms.infrastructure.memory`) phục vụ môi trường kiểm thử tự động.

Hai cơ chế này đóng kín toàn bộ chi tiết thao tác vào ra vật lý và cấu trúc dữ liệu nội bộ. Tầng Hạ tầng chịu trách nhiệm chuyển đổi hai chiều giữa dữ liệu lưu trữ và thực thể nghiệp vụ, quản lý an toàn luồng dữ liệu, đồng thời đóng gói các ngoại lệ vào ra cấp thấp thành `StorageException` nhằm giữ cho tầng nghiệp vụ hoàn toàn độc lập với môi trường thực thi.

### 4.3.1 Định dạng và Cấu trúc Bản ghi

Hệ thống tổ chức lưu trữ dữ liệu bền vững và tệp sao lưu trên ổ đĩa cục bộ dưới dạng tệp văn bản JSON mã hóa UTF-8, vận hành hoàn toàn độc lập mà không phụ thuộc vào hệ quản trị cơ sở dữ liệu bên ngoài. Cấu trúc tệp đóng gói trạng thái nhất quán của các nhóm thực thể nghiệp vụ, bảo toàn độ chính xác số học tài chính và phục vụ trực tiếp cho các quy trình lưu trữ, xuất dữ liệu sao lưu và phục hồi dữ liệu an toàn.

Tệp dữ liệu lưu trữ theo định dạng JSON với bảng mã UTF-8. Các trường số tiền lưu dưới dạng chuỗi số học cố định 2 chữ số thập phân để bảo toàn độ chính xác, mốc thời gian lưu theo định dạng `yyyy-MM-dd` và mã định danh lưu dưới dạng chuỗi UUID.

**Cấu trúc thuộc tính tệp dữ liệu:**

Cấu trúc các trường dữ liệu JSON lưu trữ trạng thái của 4 nhóm thực thể trên đĩa cứng:

| Trường dữ liệu | Kiểu dữ liệu JSON | Bắt buộc | Ràng buộc và Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `wallets` | `array` | Bắt buộc | Mảng danh sách các đối tượng ví tiền (`Wallet`); mỗi phần tử gồm `walletId` (chuỗi UUID), `name` (chuỗi từ 1 đến 50 ký tự theo quy chuẩn đặt tên), `balance` (chuỗi số học 2 chữ số thập phân, không âm theo quy tắc số dư ví), và `status` (`ACTIVE`/`ARCHIVED`). |
| `categories` | `array` | Bắt buộc | Mảng danh sách các bản ghi danh mục (`Category`) và nhóm nhu cầu (`NeedGroup`) theo cấu trúc phân cấp 3 cấp; mỗi phần tử gồm `categoryId`/`needGroupId` (chuỗi UUID), `name` (chuỗi theo quy chuẩn đặt tên), `needGroupId` (chuỗi UUID nhóm cha), `transactionType` (`INCOME`/`EXPENSE`), `status` (`ACTIVE`/`DELETED`), cùng các trường tùy chọn `icon`, `color`. |
| `transactions` | `array` | Bắt buộc | Mảng danh sách các bản ghi giao dịch (`Transaction`); mỗi phần tử gồm `transactionId` (chuỗi UUID), loại giao dịch đa hình (thu chi hoặc chuyển tiền); đối với giao dịch thu chi gồm `walletId` (chuỗi UUID tham chiếu ví), `categoryId` (chuỗi UUID danh mục cấp 3); đối với giao dịch chuyển tiền gồm `sourceWalletId` và `targetWalletId` (chuỗi UUID hai ví liên kết); cùng `amount` (chuỗi số học 2 chữ số thập phân), `transactionDate` (định dạng chuỗi `yyyy-MM-dd`), `note` (chuỗi ghi chú), và đối tượng lồng nhau `fee` (`TransactionFee` nếu có phát sinh chi phí). |
| `adjustments` | `array` | Bắt buộc | Mảng danh sách các bản ghi kiểm kê điều chỉnh số dư (`BalanceAdjustmentRecord`); mỗi phần tử gồm `adjustmentId` (chuỗi UUID), `walletId` (chuỗi UUID), `actualBalance` (chuỗi số học 2 chữ số thập phân không âm), `recordedBalance` (chuỗi số học), `difference` (chuỗi số học) và `adjustmentDate` (chuỗi `yyyy-MM-dd`). |

**Chỉ dẫn thiết kế và cài đặt:**

- Cấu trúc 4 mảng đối tượng chính trong tệp dữ liệu lưu trữ bền vững và sao lưu bao gồm: `wallets` (danh sách ví tiền), `categories` (danh mục phân loại và nhóm nhu cầu 3 cấp), `transactions` (toàn bộ giao dịch thu chi và chuyển tiền), và `adjustments` (các bản ghi kiểm kê điều chỉnh số dư). Khi xuất dữ liệu sao lưu toàn bộ hệ thống (`FULL`), tệp JSON đóng gói đầy đủ cả 4 mảng thực thể cùng các liên kết lịch sử; khi xuất phạm vi sổ giao dịch (`TRANSACTIONS`), tệp tập trung vào mảng giao dịch và các chi phí liên kết đang tồn tại.
- Quy cách lưu trữ các trường dữ liệu tuân thủ nghiêm ngặt quy chuẩn của SRS và OOA: mọi trường định danh duy nhất (`walletId`, `categoryId`, `transactionId`, `adjustmentId`) đều lưu dưới dạng chuỗi chuẩn UUID bất biến; toàn bộ các trường số tiền (`balance`, `amount`, `feeAmount`, `actualBalance`) bắt buộc phải lưu dưới dạng chuỗi số học cố định 2 chữ số thập phân theo quy tắc độ chính xác tài chính hai chữ số thập phân, tuyệt đối không dùng số thực dấu chấm động nhị phân (`number`/`float`/`double`) nhằm tránh sai số làm tròn khi tuần tự hóa và phục hồi. Các mốc thời gian lưu dưới dạng chuỗi chuẩn `yyyy-MM-dd`.
- Cơ chế biểu diễn đối tượng lồng nhau và phân cấp: chi phí giao dịch `TransactionFee` được biểu diễn như một đối tượng lồng nhau (`nested object`) bên trong bản ghi giao dịch chủ, phản ánh đúng quan hệ hợp thành sinh tử và bảo đảm tính đồng bộ khi sao lưu và phục hồi dữ liệu giao dịch. Cấu trúc danh mục duy trì quan hệ phân cấp 3 cấp qua tham chiếu `needGroupId` trỏ tới nhóm nhu cầu cha và xác định nhánh loại thu chi (`INCOME`/`EXPENSE`), phục vụ việc thẩm định toàn vẹn tham chiếu tại Bước 2 của quy trình phục hồi dữ liệu.

### 4.3.2 Cơ chế Đọc Ghi và An toàn Dữ liệu

Hệ thống áp dụng cơ chế đọc ghi tệp an toàn qua luồng đệm UTF-8 và tệp trung gian tạm thời nhằm ngăn ngừa triệt để nguy cơ hỏng dữ liệu khi ứng dụng gặp sự cố dừng đột ngột trong quá trình lưu trữ hoặc xuất sao lưu. Đồng thời, cơ chế khởi tạo tự động thiết lập sẵn thư mục lưu trữ và cấu trúc dữ liệu ban đầu hợp lệ ngay trong lần khởi chạy đầu tiên theo quy định khởi tạo danh mục mẫu, kết hợp cơ chế tự động tạo bản sao lưu an toàn trước khi phục hồi dữ liệu hệ thống.

**Quy trình ghi tệp nguyên tử**

Để loại trừ hoàn toàn nguy cơ hỏng tệp dữ liệu khi mất điện hoặc tiến trình bị ngắt đột ngột giữa chừng, toàn bộ thao tác lưu dữ liệu được thực thi qua quy trình hoán đổi tệp:

| Bước | Thao tác kỹ thuật | Mục đích và Cơ chế bảo vệ |
| :--- | :--- | :--- |
| 1 | Ghi toàn bộ dữ liệu ra tệp tạm thời (`.tmp`) | Tuần tự hóa toàn bộ trạng thái dữ liệu hiện tại ra một tệp tạm thời mới (ví dụ `data.json.tmp`) sử dụng bộ đệm ghi với bảng mã UTF-8 không BOM; bảo đảm nếu quá trình ghi bị ngắt quãng giữa chừng thì tệp chính vẫn giữ nguyên vẹn 100%. |
| 2 | Ép xả dữ liệu xuống đĩa vật lý (`flush` / `sync`) | Thực hiện ép xả luồng ghi dữ liệu từ bộ đệm của hệ điều hành xuống đĩa vật lý (`FileChannel.force` hoặc `flush`), bảo đảm toàn bộ nội dung của tệp tạm thời đã được lưu bền vững trên đĩa cứng trước khi thay thế tệp chính. |
| 3 | Hoán đổi nguyên tử đè lên tệp chính (`atomic move` / `replace`) | Sử dụng thao tác di chuyển hoặc đổi tên tệp nguyên tử (như `Files.move` với tùy chọn `ATOMIC_MOVE` và `REPLACE_EXISTING`) để tráo đổi tệp tạm thời đè lên tệp dữ liệu chính; bảo đảm tệp dữ liệu luôn ở trạng thái hoàn chỉnh, không bao giờ bị rơi vào trạng thái dở dang. |

**Chỉ dẫn thiết kế và cài đặt:**

- Quy trình ghi tệp an toàn được thực hiện qua 3 bước chặt chẽ: đầu tiên, toàn bộ nội dung dữ liệu JSON được tuần tự hóa và ghi vào một tệp tạm thời có phần mở rộng `.tmp`; tiếp theo, hệ thống thực hiện ép xả (`flush` và đồng bộ đĩa) để bảo đảm toàn bộ dòng byte đã được ghi bền vững xuống thiết bị lưu trữ vật lý; cuối cùng, thực hiện hoán đổi nguyên tử (`atomic move/rename`) để thay thế tệp dữ liệu chính bằng tệp tạm thời, loại bỏ rủi ro tệp chính bị hỏng do mất điện hoặc tiến trình bị hủy đột ngột trong khi ghi.
- Quy trình đọc tệp và kiểm soát an toàn dữ liệu: lớp điều hợp lưu trữ thực hiện đọc dữ liệu thông qua luồng đệm (`BufferedReader`) với bảng mã UTF-8 không BOM, giải tuần tự hóa các mảng JSON thành các tập thực thể miền tương ứng; kiểm tra quyền đọc (`READ permission`) trước khi thao tác. Nếu tệp không tồn tại hoặc phát sinh lỗi đọc vật lý, hệ thống đóng gói lỗi thành `StorageException`; nếu cấu trúc JSON bị hỏng, sai cú pháp hoặc thiếu các trường bắt buộc, hệ thống ném `DataCorruptionException` để kích hoạt cơ chế xử lý lỗi tầng trên.
- Cơ chế bảo vệ và an toàn dữ liệu khi phục hồi (`restoreBackupData`): tuân thủ nghiêm ngặt quy định tại `CON-06`, trước khi nạp đè dữ liệu từ bản sao lưu bên ngoài, hệ thống tự động tạo một bản sao lưu dự phòng hiện thời có nhãn thời gian theo định dạng `backup_YYYYMMDD_HHMMSS.json` lưu trong thư mục `backups/`. Quá trình nạp phục hồi áp dụng cơ chế Tất cả hoặc Không (All-or-Nothing): toàn bộ dữ liệu hệ thống chỉ được thay thế khi và chỉ khi tệp sao lưu vượt qua đầy đủ 4 bước kiểm định toàn vẹn nghiêm ngặt (định dạng số liệu tài chính, cấu trúc phân cấp danh mục 3 cấp, tính toàn vẹn giao dịch/chuyển tiền/phí, và bảo toàn số dư ví không âm); nếu xuất hiện bất kỳ sai phạm nào, toàn bộ quá trình lập tức bị hủy bỏ, bảo toàn 100% dữ liệu đang vận hành.
- Cơ chế kiểm tra và tự động khởi tạo dữ liệu ban đầu: khi hệ thống khởi chạy lần đầu tiên, lớp điều hợp lưu trữ kiểm tra sự tồn tại của thư mục lưu trữ và tệp dữ liệu cục bộ; nếu chưa tồn tại, hệ thống tự động tạo mới cây thư mục và khởi tạo tệp JSON rỗng với đầy đủ cấu trúc 4 mảng hợp lệ (`wallets`, `categories`, `transactions`, `adjustments`), đồng thời nạp cấu trúc danh mục mẫu ban đầu theo quy tắc phân cấp cây danh mục một lần duy nhất mà không lặp lại ở các lần khởi động kế tiếp.
- Quy tắc xử lý khi phát sinh sự cố vào ra (I/O error): nếu quá trình ghi vào tệp tạm thời gặp lỗi quyền truy cập, đầy đĩa hoặc ngắt kết nối, hệ thống lập tức hủy bỏ và xóa tệp tạm thời (`.tmp`), giữ nguyên trạng thái vẹn toàn không bị thay đổi của tệp dữ liệu chính; toàn bộ ngoại lệ kỹ thuật cấp thấp (`IOException`) được bao bọc thành `StorageException` và chuyển tiếp lên tầng trên để Tầng Trình diễn thông báo lỗi rõ ràng cho người dùng mà không làm hỏng dữ liệu hệ thống.

### 4.3.3 Cài đặt Lưu trữ Trong Bộ Nhớ

Bộ điều hợp lưu trữ bộ nhớ thuộc Tầng Hạ tầng hiện thực hóa các giao diện kho lưu trữ hoàn toàn trên bộ nhớ RAM, phục vụ việc chạy các ca kiểm thử đơn vị độc lập và tốc độ cao. Toàn bộ các lớp cài đặt này được đặt trong gói `com.pfms.infrastructure.memory`, loại bỏ sự phụ thuộc vào hệ thống tệp và bảo đảm môi trường kiểm thử không bị ô nhiễm dữ liệu rác.

**Cấu trúc lớp cài đặt bộ nhớ:**

Cấu trúc các lớp cài đặt kho lưu trữ hoạt động trên bộ nhớ RAM phục vụ môi trường kiểm thử:

| Lớp cài đặt | Giao diện hiện thực | Cấu trúc dữ liệu nội bộ | Trách nhiệm chính |
| :--- | :--- | :--- | :--- |
| `MemoryGenericRepository<E, ID>` | `GenericRepository<E, ID>` | `ConcurrentHashMap<ID, E>` | Lớp cơ sở trừu tượng cung cấp các thao tác dữ liệu chuẩn (`save`, `findById`, `findAll`, `existsById`, `deleteById`) và phương thức `clear()` giải phóng toàn bộ dữ liệu trong bộ nhớ sau mỗi ca kiểm thử. |
| `MemoryWalletRepository` | `WalletRepository` | `ConcurrentHashMap<UUID, Wallet>` | Kế thừa `MemoryGenericRepository<Wallet, UUID>`; hiện thực các thao tác truy vấn ví trên RAM (`findByName`, `existsByName`, `findByStatus`, `countByStatus`) phục vụ kiểm thử đơn vị luồng nghiệp vụ ví. |
| `MemoryTransactionRepository` | `TransactionRepository` | `ConcurrentHashMap<UUID, Transaction>` | Kế thừa `MemoryGenericRepository<Transaction, UUID>`; lưu trữ đa hình các loại giao dịch và hiện thực truy vấn lọc (`findByDateRange`, `findByDateRangeAndType`, `findByCategoryId`, `sumAmountByFilter`, `findByWalletId`). |
| `MemoryCategoryRepository` | `CategoryRepository` | `ConcurrentHashMap<UUID, Category>` | Kế thừa `MemoryGenericRepository<Category, UUID>`; hiện thực các thao tác truy vấn phân cấp cây danh mục 3 cấp và kiểm tra trùng tên (`findByType`, `findByNeedGroupId`, `existsByNameAndNeedGroupId`, `findActiveCategories`). |
| `MemoryBalanceAdjustmentRepository` | `BalanceAdjustmentRepository` | `ConcurrentHashMap<UUID, BalanceAdjustmentRecord>` | Kế thừa `MemoryGenericRepository<BalanceAdjustmentRecord, UUID>`; lưu trữ các bản ghi kiểm kê số dư ví theo cơ chế chỉ thêm (Append-only) và hiện thực truy vấn đối soát (`findByWalletId`, `findLatestByWalletId`). |

**Chỉ dẫn thiết kế và cài đặt:**

- Sử dụng cấu trúc bảng băm an toàn luồng `ConcurrentHashMap<ID, E>` làm vùng chứa dữ liệu thực thể trên bộ nhớ RAM, đánh chỉ mục trực tiếp theo khóa chính `UUID`. Cấu trúc này bảo đảm tính toàn vẹn dữ liệu trong các kịch bản kiểm thử đồng thời (concurrent tests) mà không phát sinh xung đột khóa, cho phép thực thi các thao tác đọc ghi và tìm kiếm với độ phức tạp thời gian trung bình $O(1)$.
- Cung cấp phương thức dọn sạch dữ liệu `clear()` trong lớp cơ sở `MemoryGenericRepository` (thực thi `storage.clear()`) để làm rỗng bảng băm lưu trữ. Phương thức này được tích hợp vào các bước chuẩn bị (`@BeforeEach`) hoặc dọn dẹp (`@AfterEach`) của vòng đời kiểm thử tự động (JUnit), bảo đảm thiết lập lại trạng thái ban đầu sạch sẽ và loại bỏ hoàn toàn sự phụ thuộc chéo giữa các ca kiểm thử.
- Toàn bộ dữ liệu trong các kho lưu trữ bộ nhớ chỉ tồn tại tạm thời trên bộ nhớ heap của máy ảo JVM và mang tính chất không bền vững (non-persistent). Toàn bộ dữ liệu được tự động giải phóng hoàn toàn ngay khi tiến trình kiểm thử kết thúc, bảo đảm không tạo ra bất kỳ tệp dữ liệu rác nào trên hệ thống tệp và không gây ảnh hưởng đến dữ liệu vận hành thực tế của người dùng.

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
