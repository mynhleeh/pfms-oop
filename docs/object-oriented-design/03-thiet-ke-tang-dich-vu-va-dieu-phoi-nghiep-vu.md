# 03. Thiết kế Tầng Dịch vụ Ứng dụng và Điều phối Nghiệp vụ

Tài liệu này xác lập bản vẽ thiết kế chi tiết cho Tầng Dịch vụ ứng dụng của Hệ thống Quản lý Tài chính Cá nhân PFMS. Nội dung văn bản thực hiện chuyển đổi các ca sử dụng và kịch bản tương tác đã được xác thực trong hồ sơ Phân tích Hướng đối tượng sang cấu trúc các lớp dịch vụ điều phối luồng nghiệp vụ trên nền tảng ngôn ngữ Java.

Bản vẽ thiết kế tập trung giải quyết ba mục tiêu kỹ thuật trọng tâm: quản lý vòng đời và điều phối luồng thực thi của các ca sử dụng nghiệp vụ, đóng vai trò cầu nối trung gian giữa giao diện dòng lệnh và các thực thể nghiệp vụ; phân tách rạch ròi giữa giao diện công khai và lớp cài đặt nội bộ nhằm đóng kín chi tiết điều phối; và bảo đảm tính nguyên tử của dữ liệu tài chính khi thao tác qua các kho lưu trữ trừu tượng, đồng thời ủy quyền toàn bộ các phép tính số học cho Tầng Nghiệp vụ cốt lõi.

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
| **Báo cáo và thống kê** | `ReportService` | `ReportServiceImpl` | Vận hành ở chế độ chỉ đọc: trích xuất dữ liệu, tổng hợp dòng tiền 12 tháng, đo lường tỷ lệ tiết kiệm và phân tích cơ cấu chi tiêu. |
| **Quản trị và sao lưu** | `DataManagementService` | `DataManagementServiceImpl` | Điều phối xuất dữ liệu JSON, phục hồi dữ liệu hệ thống theo quy trình 4 bước và đối soát độc lập 4 điều kiện toàn vẹn tài chính. |

## 3.2 Dịch vụ Quản lý Ví

(Mô tả khoảng 1 đến 2 câu về vai trò của dịch vụ trong việc điều phối các ca sử dụng quản lý nguồn tiền, cập nhật thông tin nhận diện, kiểm kê điều chỉnh số dư thực tế và phân tích cơ cấu số dư)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`, `UC-WAL-02`, `UC-WAL-03`, `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`, `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-WAL-001`, `ERR-FIN-001`

**Giao diện công khai:**

Giao diện `com.pfms.service.WalletService` cung cấp các phương thức cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức nghiệp vụ) | (Tham số DTO hoặc kiểu định danh) | (Kiểu dữ liệu DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.WalletServiceImpl` nhận `WalletRepository` và `BalanceAdjustmentRepository` qua hàm khởi tạo để xử lý logic:
- (Đặc tả chi tiết quy trình thực thi cho từng phương thức theo 4 bước chuẩn: kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ; nạp thực thể và ủy quyền tính toán cho Thực thể miền hoặc Đối tượng giá trị; kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký/vết kiểm toán; đóng gói kết quả sang DTO phản hồi trước khi trả về Tầng Trình diễn)

## 3.3 Dịch vụ Ghi nhận Thu Chi

(Mô tả khoảng 1 đến 2 câu về vai trò của dịch vụ trong việc điều phối ghi nhận thu chi, tra cứu lịch sử, chỉnh sửa và hoàn nguyên giao dịch tài chính)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TXN-02`, `UC-TXN-03`, `UC-TXN-04`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`, `FR-TXN-04`, `FR-TXN-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-CAT-001`

**Giao diện công khai:**

Giao diện `com.pfms.service.TransactionService` cung cấp các phương thức cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức nghiệp vụ) | (Tham số DTO hoặc kiểu định danh) | (Kiểu dữ liệu DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.TransactionServiceImpl` nhận `TransactionRepository`, `WalletRepository` và `CategoryRepository` qua hàm khởi tạo để xử lý logic:
- (Đặc tả chi tiết quy trình thực thi cho từng phương thức theo 4 bước chuẩn: kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ; nạp thực thể và ủy quyền tính toán cho Thực thể miền hoặc Đối tượng giá trị; kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký/vết kiểm toán; đóng gói kết quả sang DTO phản hồi trước khi trả về Tầng Trình diễn)

## 3.4 Dịch vụ Chuyển tiền Nội bộ

(Mô tả khoảng 1 đến 2 câu về vai trò của dịch vụ trong việc điều phối luồng chuyển tiền giữa hai ví, hạch toán chi phí phát sinh và bảo toàn tổng tài sản)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TRF-01`, `UC-TRF-02`, `UC-TRF-03`
- Yêu cầu chức năng: `FR-TRF-01`, `FR-TRF-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi: `ERR-TRF-001`, `ERR-TRF-002`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-WAL-001`, `ERR-FIN-001`

**Giao diện công khai:**

Giao diện `com.pfms.service.TransferService` cung cấp các phương thức cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức nghiệp vụ) | (Tham số DTO hoặc kiểu định danh) | (Kiểu dữ liệu DTO hoặc void) | (Ngoại lệ ném ra nếu có) | (Mô tả luồng điều phối ca sử dụng) |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.TransferServiceImpl` nhận `TransferRepository`, `WalletRepository` và `CategoryRepository` qua hàm khởi tạo để xử lý logic:
- (Đặc tả chi tiết quy trình thực thi cho từng phương thức theo 4 bước chuẩn: kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ; nạp thực thể và ủy quyền tính toán cho Thực thể miền hoặc Đối tượng giá trị; kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký/vết kiểm toán; đóng gói kết quả sang DTO phản hồi trước khi trả về Tầng Trình diễn)

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
| `createCategory` | `CreateCategoryRequestDto requestDto` | `CategoryResponseDto` | `ValidationException`<br/>`EntityNotFoundException`<br/>`DuplicateEntityException`<br/>`BusinessException` | Điều phối bổ sung danh mục mới vào nhóm nhu cầu cha: Tiếp nhận yêu cầu tạo danh mục; kiểm tra định dạng tên theo quy tắc đặt tên; kiểm tra nhóm cha tồn tại và đang hoạt động; kiểm tra tính duy nhất của tên trong cùng nhóm cha; khởi tạo thực thể `Category` hoạt động (`ACTIVE`), kế thừa loại thu chi (`INCOME`/`EXPENSE`) từ nhóm cha và lưu trữ vào kho dữ liệu. |
| `renameCategory` | `RenameCategoryRequestDto requestDto` | `CategoryResponseDto` | `ValidationException`<br/>`EntityNotFoundException`<br/>`DuplicateEntityException`<br/>`BusinessException` | Điều phối đổi tên hiển thị của danh mục: Tiếp nhận yêu cầu cập nhật tên; kiểm tra độ dài từ 1 đến 50 ký tự sau khi cắt khoảng trắng theo quy tắc đặt tên; xác thực danh mục tồn tại; kiểm tra tên mới không trùng với các danh mục khác đang hoạt động trong cùng nhóm cha; cập nhật tên và lưu trữ, bảo toàn nguyên vẹn mã định danh và toàn bộ lịch sử giao dịch. |
| `restoreCategory` | `UUID categoryId` | `CategoryResponseDto` | `EntityNotFoundException`<br/>`DuplicateEntityException`<br/>`BusinessException` | Điều phối khôi phục danh mục đã xóa mềm: Tiếp nhận mã danh mục cần khôi phục; kiểm tra danh mục tồn tại và đang ở trạng thái `DELETED`; kiểm tra nhóm nhu cầu cha trực thuộc bắt buộc phải đang hoạt động; kiểm tra tên khôi phục không trùng với danh mục đang hoạt động trong cùng nhóm; chuyển trạng thái sang `ACTIVE` và đưa danh mục trở lại danh sách lựa chọn thu chi. |
| `deleteCategory` | `UUID categoryId` | `void` | `EntityNotFoundException`<br/>`BusinessException` | Điều phối xóa danh mục: Tiếp nhận mã danh mục cần xóa; xác thực danh mục tồn tại; kiểm tra không phải nhóm nhu cầu hoạt động cuối cùng của loại thu chi; kiểm tra lịch sử giao dịch qua `TransactionRepository`: nếu chưa từng có giao dịch thì xóa vĩnh viễn (xóa cứng), nếu đã phát sinh giao dịch thì chuyển trạng thái sang `DELETED` (xóa mềm) theo quy tắc bảo toàn dữ liệu lịch sử. |
| `getCategoryHierarchy` | Không có | `List<CategoryResponseDto>` | Không có | Điều phối truy xuất cây danh mục phân loại: Nạp toàn bộ cấu trúc phân loại 3 cấp (Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết) ở cả trạng thái `ACTIVE` và `DELETED` để hiển thị trên giao diện hoặc cung cấp danh sách lựa chọn cho luồng ghi nhận giao dịch. |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.CategoryServiceImpl` nhận `CategoryRepository`, `NeedGroupRepository` và `TransactionRepository` qua hàm khởi tạo để xử lý logic:

- **Quy trình thực thi phương thức `createCategory(CreateCategoryRequestDto requestDto)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Làm sạch và xác thực tên danh mục theo quy tắc đặt tên (loại bỏ khoảng trắng hai đầu, kiểm tra độ dài từ 1 đến 50 ký tự, ném `ValidationException` nếu để trống hoặc vượt ngưỡng). Nạp nhóm nhu cầu cha qua `NeedGroupRepository.findById(requestDto.getParentGroupId())`; nếu không tìm thấy ném `EntityNotFoundException`, nếu nhóm cha đang ở trạng thái `DELETED` thì từ chối tiếp nhận và ném `BusinessException` theo quy tắc cấu trúc danh mục ba cấp. Kiểm tra trùng lặp tên trong cùng nhóm cha qua `CategoryRepository.existsByNameAndNeedGroupId(...)`; nếu tên đã tồn tại ở trạng thái hoạt động thì ném `DuplicateEntityException`.
  2. *Bước 2 - Nạp thực thể và ủy quyền tính toán cho Thực thể miền:* Khởi tạo thực thể `Category` mới ở trạng thái hoạt động `ACTIVE` (`isDeleted = false`), thiết lập liên kết phân cấp ngữ nghĩa tới nhóm nhu cầu cha và kế thừa thuộc tính loại thu chi (`INCOME` hoặc `EXPENSE`) cố định từ nhóm cha theo cấu trúc phân cấp 3 cấp.
  3. *Bước 3 - Kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký:* Gọi `CategoryRepository.save(newCategory)` để lưu trữ bền vững bản ghi danh mục mới vào tệp dữ liệu, bảo đảm tính nhất quán của cây danh mục và ghi nhật ký kiểm toán hệ thống.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Ánh xạ dữ liệu của thực thể `Category` vừa lưu trữ sang `CategoryResponseDto` (chứa `categoryId`, `name`, `parentGroupId`, `transactionType`, `status`, `createdAt`) và trả về cho Tầng Trình diễn.

- **Quy trình thực thi phương thức `renameCategory(RenameCategoryRequestDto requestDto)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra tên mới theo quy tắc đặt tên (cắt khoảng trắng hai đầu, độ dài 1 đến 50 ký tự, ném `ValidationException` nếu vi phạm). Nạp danh mục từ `CategoryRepository.findById(requestDto.getCategoryId())`, ném `EntityNotFoundException` nếu không tồn tại. Nếu có yêu cầu chuyển nhóm cha mới: kiểm tra nhóm đích phải tồn tại và đang `ACTIVE` (nếu nhóm đích bị `DELETED` ném `BusinessException`), đồng thời nhóm đích phải cùng Loại thu chi với nhóm cũ (nếu khác Loại thu chi ném `BusinessException` theo quy tắc cấu trúc danh mục ba cấp). Kiểm tra tên mới không bị trùng với các danh mục khác trong nhóm đích qua `CategoryRepository.existsByNameAndNeedGroupIdAndIdNot(...)`, ném `DuplicateEntityException` nếu bị trùng.
  2. *Bước 2 - Nạp thực thể và ủy quyền tính toán cho Thực thể miền:* Ủy quyền thực thể `Category` cập nhật thuộc tính tên hiển thị mới (và cập nhật liên kết nhóm cha nếu có), giữ nguyên tuyệt đối mã định danh `categoryId` và toàn bộ các liên kết giao dịch lịch sử trong quá khứ.
  3. *Bước 3 - Kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký:* Gọi `CategoryRepository.save(category)` để lưu trữ cập nhật vào tệp dữ liệu và ghi nhật ký thay đổi thông tin danh mục.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thông tin thực thể `Category` đã cập nhật vào `CategoryResponseDto` và trả về cho Tầng Trình diễn.

- **Quy trình thực thi phương thức `restoreCategory(UUID categoryId)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Nạp danh mục cần khôi phục qua `CategoryRepository.findById(categoryId)`, ném `EntityNotFoundException` nếu không tìm thấy hoặc nếu danh mục đang ở trạng thái `ACTIVE`. Nạp nhóm nhu cầu cha trực thuộc qua `NeedGroupRepository.findById(...)`; nếu nhóm cha đang ở trạng thái `DELETED` thì từ chối khôi phục và ném `BusinessException` (yêu cầu người dùng khôi phục nhóm cha trước theo quy tắc cấu trúc danh mục ba cấp). Kiểm tra tên danh mục khôi phục không trùng với bất kỳ danh mục nào đang `ACTIVE` trong cùng nhóm cha qua `CategoryRepository.existsByNameAndNeedGroupId(...)`, ném `DuplicateEntityException` nếu xảy ra xung đột tên theo quy tắc đặt tên.
  2. *Bước 2 - Nạp thực thể và ủy quyền tính toán cho Thực thể miền:* Ủy quyền thực thể `Category` chuyển trạng thái vòng đời từ `DELETED` sang `ACTIVE` (`isDeleted = false`), tái lập tính khả dụng của danh mục mà không làm xáo trộn các số liệu và liên kết lịch sử đã ghi nhận.
  3. *Bước 3 - Kiểm soát lưu trữ nguyên tử qua kho lưu trữ kèm ghi nhật ký:* Gọi `CategoryRepository.save(category)` để lưu lại trạng thái hoạt động mới vào kho dữ liệu và ghi nhật ký thao tác khôi phục.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thực thể `Category` đã phục hồi vào `CategoryResponseDto` và trả về cho Tầng Trình diễn để hiển thị lại trên giao diện nhập liệu.

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
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Ánh xạ toàn bộ cấu trúc cây phân loại sang danh sách `CategoryResponseDto` bất biến và trả về Tầng Trình diễn phục vụ kết xuất bảng trên giao diện dòng lệnh.

## 3.6 Dịch vụ Báo cáo và Thống kê Tài chính

Dịch vụ Báo cáo và Thống kê Tài chính vận hành thuần túy ở chế độ chỉ đọc, chịu trách nhiệm truy xuất dữ liệu từ các kho lưu trữ để tổng hợp chuỗi thời gian dòng tiền 12 tháng liên tục, đo lường dòng tiền thuần và tỷ lệ tiết kiệm theo tháng và bình quân năm. Đồng thời, dịch vụ phân tích cơ cấu phân bổ thu chi 2 cấp theo nhóm nhu cầu và danh mục chi tiết trong kỳ xác định, bảo đảm độ chính xác số học tài chính mà không làm biến đổi bất kỳ trạng thái dữ liệu nào của hệ thống.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-01`, `UC-REP-02`
- Yêu cầu chức năng: `FR-REP-01`, `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-FIN-002`

**Giao diện công khai:**

Giao diện `com.pfms.service.ReportService` cung cấp các phương thức cho Tầng Trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `generateCashFlowReport` | `int endMonth, int endYear` | `CashFlowReportDto` | `ValidationException`<br/>`BusinessException` | Điều phối thống kê dòng tiền: Xác định chuỗi 12 tháng liên tục lùi tính đến mốc `endMonth/endYear`; truy vấn sổ giao dịch ở chế độ chỉ đọc; tổng hợp thu nhập, chi tiêu (bao gồm cả phí giao dịch liên kết); tính dòng tiền thuần và tỷ lệ tiết kiệm từng tháng theo quy tắc độ chính xác tài chính; xử lý trường hợp tổng thu nhập không dương gán tỷ lệ mặc định 0.00% nhằm triệt tiêu lỗi chia cho không; tính các chỉ số bình quân cả năm chia đều cho 12. |
| `generateSpendingStructureReport` | `SpendingReportCriteriaDto criteriaDto` | `SpendingStructureReportDto` | `ValidationException`<br/>`BusinessException` | Điều phối phân tích cơ cấu chi tiêu: Tiếp nhận khoảng ngày thống kê và loại luồng tiền (`EXPENSE` hoặc `INCOME`); kiểm tra điều kiện ngày bắt đầu không vượt quá ngày kết thúc; truy vấn giao dịch và thông tin danh mục ở chế độ chỉ đọc (bao gồm cả mục đã xóa mềm có giao dịch trong kỳ); gom cụm 2 cấp theo nhóm nhu cầu và danh mục chi tiết; tính tổng tiền, tỷ trọng cơ cấu phần trăm làm tròn ngân hàng, sắp xếp giảm dần và tính chênh lệch so sánh với kỳ liền kề. |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.ReportServiceImpl` nhận `TransactionRepository`, `CategoryRepository` và `WalletRepository` qua hàm khởi tạo để xử lý logic:

Dịch vụ vận hành hoàn toàn ở chế độ chỉ đọc (Read-only), tuyệt đối không làm thay đổi số dư ví, không chỉnh sửa bản ghi tài chính và không thực hiện thao tác ghi hay sửa đổi dữ liệu lưu trữ. Quy trình 4 bước được chuẩn hóa phù hợp với bản chất trích xuất số liệu thống kê như sau:

- **Quy trình thực thi phương thức `generateCashFlowReport(int endMonth, int endYear)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra tính hợp lệ của mốc thời gian kết thúc (`endMonth` nằm trong đoạn từ 1 đến 12, `endYear` từ năm 2000 trở đi; nếu không chỉ định thì mặc định lấy tháng và năm hiện tại theo yêu cầu thống kê, ném `ValidationException` nếu sai định dạng). Xác định khung thời gian thống kê gồm đúng 12 tháng liên tục lùi tính từ mốc tháng và năm kết thúc.
  2. *Bước 2 - Nạp dữ liệu chỉ đọc từ kho lưu trữ:* Gọi `TransactionRepository.findByDateRange(...)` để nạp toàn bộ các giao dịch thu chi (`IncomeExpenseTransaction`) và giao dịch có chi phí phát sinh (`TransactionFee`) trong khung thời gian 12 tháng đã xác định mà không làm biến đổi dữ liệu nguồn.
  3. *Bước 3 - Tổng hợp số liệu và ủy quyền tính toán số học tài chính:*
     - Gom cụm các giao dịch theo từng tháng trong chuỗi 12 tháng liên tục.
     - Với mỗi tháng, tính tổng thu nhập (tổng số tiền giao dịch `INCOME`) và tổng chi tiêu (tổng số tiền giao dịch `EXPENSE` cộng dồn toàn bộ các khoản phí giao dịch liên kết đang tồn tại).
     - Tính dòng tiền thuần từng tháng bằng hiệu số giữa tổng thu nhập và tổng chi tiêu theo quy tắc độ chính xác số học tài chính.
     - Tính tỷ lệ tiết kiệm từng tháng: trường hợp tổng thu nhập lớn hơn 0, tính bằng công thức $\frac{\text{Dòng tiền thuần}}{\text{Tổng thu nhập}} \times 100\%$ và áp dụng quy tắc làm tròn ngân hàng hai chữ số thập phân; trường hợp tổng thu nhập nhỏ hơn hoặc bằng 0, gán tỷ lệ tiết kiệm mặc định là 0.00% nhằm phòng ngừa triệt để lỗi chia cho không.
     - Tính các chỉ số bình quân cả năm: lấy tổng thu nhập, tổng chi tiêu và tổng dòng tiền thuần của cả 12 tháng chia đều cho mẫu số cố định là 12, trong đó các tháng không phát sinh giao dịch được tính bằng 0.00 VNĐ.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói bảng số liệu 12 tháng cùng các chỉ số bình quân vào đối tượng `CashFlowReportDto` bất biến và chuyển giao cho Tầng Trình diễn kết xuất bảng và biểu đồ trên màn hình dòng lệnh.

- **Quy trình thực thi phương thức `generateSpendingStructureReport(SpendingReportCriteriaDto criteriaDto)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra khoảng ngày phân tích (`startDate` không được lớn hơn `endDate`, ném `ValidationException` nếu vi phạm). Xác thực loại luồng tiền phân tích (`transactionType` là `EXPENSE` hoặc `INCOME`, mặc định là `EXPENSE` theo yêu cầu phân tích).
  2. *Bước 2 - Nạp dữ liệu chỉ đọc từ kho lưu trữ:* Gọi `TransactionRepository.findByDateRangeAndType(...)` để nạp danh sách giao dịch tương ứng trong khoảng ngày; nạp cấu trúc phân cấp từ `CategoryRepository` và `NeedGroupRepository`, bao gồm cả các nhóm nhu cầu và danh mục chi tiết đã bị xóa mềm (`DELETED`) nhưng có phát sinh giao dịch trong kỳ theo quy tắc cấu trúc danh mục ba cấp.
  3. *Bước 3 - Phân tích cơ cấu và ủy quyền tính toán số học tài chính:*
     - Gom cụm giao dịch theo mô hình 2 cấp: Cấp 2 theo nhóm nhu cầu (`NeedGroup`) và Cấp 3 theo danh mục chi tiết (`Category`). Đối với các mục đã bị xóa mềm, giữ nguyên số liệu độc lập và bổ sung nhãn "(đã xóa)" để bảo đảm tổng cơ cấu khớp chính xác với thực tế dòng tiền.
     - Tính tổng số tiền của toàn bộ kỳ phân tích và tổng tiền của từng nhóm, từng danh mục theo quy tắc độ chính xác tài chính.
     - Tính tỷ trọng phần trăm: lấy tổng tiền của từng mục chia cho tổng tiền toàn kỳ rồi nhân với 100, áp dụng quy tắc làm tròn ngân hàng hai chữ số thập phân; nếu tổng tiền toàn kỳ bằng 0.00 VNĐ, gán tỷ trọng mặc định bằng 0.00% để ngăn chặn lỗi chia cho không.
     - Sắp xếp các nhóm và danh mục theo thứ tự số tiền giảm dần. Nếu có yêu cầu so sánh với kỳ liền kề có cùng độ dài thời gian, nạp dữ liệu kỳ trước để tính toán chênh lệch giá trị và tỷ lệ tăng/giảm tương ứng.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói bảng phân tích cơ cấu 2 cấp, tỷ trọng phần trăm và số liệu biến động so sánh vào đối tượng `SpendingStructureReportDto` bất biến và trả về cho Tầng Trình diễn.

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
| `exportBackupData` | `ExportBackupRequestDto requestDto` | `BackupResponseDto` | `StorageException`<br/>`BusinessException` | Điều phối xuất dữ liệu tài chính: Trích xuất ảnh chụp trạng thái dữ liệu nhất quán tại một thời điểm theo phạm vi chỉ định (`FULL` hoặc `TRANSACTIONS`) ở chế độ chỉ đọc; định dạng các trường số tiền thành chuỗi số học chuẩn xác 2 chữ số thập phân; chuyển đổi cấu trúc JSON mã hóa UTF-8 không BOM và ghi an toàn xuống tệp đĩa cục bộ. |
| `restoreBackupData` | `RestoreBackupRequestDto requestDto` | `RestoreResultDto` | `StorageException`<br/>`DataCorruptionException`<br/>`InsufficientBalanceException`<br/>`BusinessException` | Điều phối phục hồi dữ liệu từ bản sao lưu: Tiếp nhận đường dẫn tệp sao lưu JSON; kiểm tra cú pháp và thực thi quy trình kiểm định toàn vẹn 4 bước nghiêm ngặt (định dạng số liệu, cấu trúc danh mục 3 cấp, liên kết chuyển tiền và phí, bảo toàn số dư ví không âm); hủy bỏ toàn bộ nếu phát hiện sai lệch; chỉ thay thế dữ liệu hệ thống khi bản sao lưu hợp lệ 100%. |
| `reconcileSystemData` | Không có | `ReconciliationReportDto` | Không có | Điều phối đối soát toàn vẹn dữ liệu hệ thống: Quét đối soát độc lập 4 điều kiện toàn vẹn ở chế độ chỉ đọc: (1) tính lại số dư từng ví từ lịch sử dòng tiền và so khớp số dư ghi nhận hiện hành; (2) kiểm tra cân bằng số tiền chuyển giữa ví nguồn và ví đích; (3) kiểm tra liên kết danh mục và tính toàn vẹn loại thu chi; (4) kiểm tra phí giao dịch mồ côi; phân loại mức độ cảnh báo và đề xuất xử lý. |

**Cài đặt dịch vụ:**

Lớp cài đặt `com.pfms.service.impl.DataManagementServiceImpl` nhận `WalletRepository`, `TransactionRepository`, `TransferRepository`, `CategoryRepository`, `NeedGroupRepository` và `BalanceAdjustmentRepository` qua hàm khởi tạo để xử lý logic:

- **Quy trình thực thi phương thức `exportBackupData(ExportBackupRequestDto requestDto)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra tính hợp lệ của đường dẫn lưu trữ đích `destinationPath` và quyền ghi trên hệ thống tệp cục bộ, ném `StorageException` nếu đường dẫn không hợp lệ hoặc không có quyền truy cập; xác thực phạm vi xuất dữ liệu hợp lệ (`FULL` cho toàn bộ hệ thống hoặc `TRANSACTIONS` cho riêng sổ giao dịch theo yêu cầu sao lưu).
  2. *Bước 2 - Nạp dữ liệu ảnh chụp nhất quán ở chế độ chỉ đọc:* Lần lượt gọi phương thức `findAll()` trên toàn bộ các kho lưu trữ (`WalletRepository`, `TransactionRepository`, `TransferRepository`, `CategoryRepository`, `NeedGroupRepository`, `BalanceAdjustmentRepository`) để nạp ảnh chụp dữ liệu đồng bộ tại cùng một mốc thời gian, bảo đảm không làm biến đổi bất kỳ giá trị nào trong hệ thống.
  3. *Bước 3 - Chuẩn hóa dữ liệu, tuần tự hóa JSON và lưu trữ an toàn:*
     - Chuyển đổi trạng thái thực thể sang cấu trúc đối tượng sao lưu (`BackupPayloadDto`).
     - Định dạng mọi trường số tiền sang dạng chuỗi số học chuẩn xác cố định 2 chữ số thập phân theo quy tắc độ chính xác số học tài chính, tuyệt đối không sử dụng số thực dấu phẩy động nhị phân.
     - Tuần tự hóa cấu trúc dữ liệu sang định dạng JSON với bộ mã ký tự UTF-8 không BOM.
     - Thực hiện cơ chế ghi tệp an toàn (ghi vào tệp tạm trước khi đổi tên thay thế) để tránh tạo tệp hỏng nếu quá trình ghi bị ngắt quãng giữa chừng.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thông tin kết quả xuất sao lưu (đường dẫn tệp, kích thước tệp, mốc thời gian xuất và số lượng bản ghi của từng thực thể) vào `BackupResponseDto` và trả về cho Tầng Trình diễn.

- **Quy trình thực thi phương thức `restoreBackupData(RestoreBackupRequestDto requestDto)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng tệp và quyền truy cập:* Kiểm tra tệp sao lưu tại đường dẫn `backupFilePath` bắt buộc phải tồn tại và ứng dụng có quyền đọc (`READ permission`). Phân tích cú pháp tệp JSON với mã hóa UTF-8; nếu tệp bị hỏng hoặc sai cấu trúc cú pháp JSON, ném `StorageException` hoặc `DataCorruptionException`.
  2. *Bước 2 - Thực thi quy trình kiểm định toàn vẹn 4 bước nghiêm ngặt (All-or-Nothing):*
     - *Kiểm tra Bước 1 (Định dạng và số liệu theo quy tắc độ chính xác tài chính):* Xác thực toàn bộ các trường bắt buộc của các thực thể phải hiện diện đầy đủ, ngày tháng đúng chuẩn `YYYY-MM-DD` và số tiền đúng định dạng số học chuẩn xác (ném `DataCorruptionException` nếu vi phạm).
     - *Kiểm tra Bước 2 (Toàn vẹn danh mục theo quy tắc cấu trúc danh mục ba cấp):* Kiểm tra cấu trúc phân cấp 3 cấp, bảo đảm mọi danh mục chi tiết đều trực thuộc một nhóm nhu cầu cha hợp lệ, không có danh mục mồ côi và Loại thu chi của danh mục tương thích với giao dịch liên kết (ném `DataCorruptionException` nếu vi phạm).
     - *Kiểm tra Bước 3 (Toàn vẹn ví và chuyển tiền theo quy tắc ghi nhận đồng thời):* Mọi giao dịch thu chi và chuyển tiền phải trỏ tới ví có trong danh sách ví của tệp; các giao dịch chuyển tiền phải bảo đảm cân bằng giữa ví nguồn và ví đích; mọi khoản phí chuyển tiền (`TransactionFee`) phải liên kết với một giao dịch chuyển tiền đang tồn tại trong tệp (ném `DataCorruptionException` nếu vi phạm).
     - *Kiểm tra Bước 4 (Bảo toàn số dư không âm theo quy tắc bảo toàn số dư ví):* Tái tính toán số dư độc lập cho toàn bộ các ví từ số dư ban đầu kết hợp toàn bộ lịch sử dòng tiền và các bản ghi kiểm kê điều chỉnh trong tệp sao lưu; bảo đảm không có bất kỳ ví nào bị âm số dư tại bất kỳ thời điểm nào (ném `InsufficientBalanceException` hoặc `DataCorruptionException` nếu phát hiện số dư âm).
     - *Cơ chế hủy bỏ bảo vệ:* Nếu phát hiện bất kỳ sai lệch nào tại bất kỳ bước kiểm tra nào, hệ thống lập tức hủy bỏ toàn bộ quá trình phục hồi, giữ nguyên vẹn 100% dữ liệu hiện tại của hệ thống.
  3. *Bước 3 - Thay thế dữ liệu nguyên tử trên toàn bộ kho lưu trữ:* Khi và chỉ khi tệp sao lưu vượt qua đầy đủ cả 4 bước kiểm tra toàn vẹn, hệ thống tiến hành xóa toàn bộ dữ liệu hiện tại trong bộ nhớ và ghi nạp đồng bộ toàn bộ các thực thể tái tạo từ tệp sao lưu vào các kho lưu trữ tương ứng (`WalletRepository`, `CategoryRepository`, `NeedGroupRepository`, `TransactionRepository`, `TransferRepository`, `BalanceAdjustmentRepository`), cập nhật lại số dư ví và cấu trúc danh mục.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thông báo phục hồi thành công kèm số lượng thực thể đã phục hồi vào `RestoreResultDto` và chuyển giao cho Tầng Trình diễn.

- **Quy trình thực thi phương thức `reconcileSystemData()`:**
  1. *Bước 1 - Khởi tạo tiến trình đối soát chỉ đọc:* Tiếp nhận yêu cầu đối soát toàn vẹn dữ liệu từ người dùng hoặc theo lịch định kỳ; thiết lập phiên làm việc thuần túy ở chế độ chỉ đọc, không làm biến đổi bất kỳ bản ghi tài chính nào.
  2. *Bước 2 - Nạp dữ liệu đồng thời từ các kho lưu trữ:* Nạp toàn bộ danh sách ví, giao dịch thu chi, chuyển tiền nội bộ, bản ghi kiểm kê số dư và danh mục phân loại từ các kho lưu trữ tương ứng; các khoản chi phí phát sinh (`TransactionFee`) được trích xuất trực tiếp từ các giao dịch cha chứa phí theo quan hệ hợp thành mà không cần kho lưu trữ phí độc lập.
  3. *Bước 3 - Quét và đối soát độc lập 4 điều kiện toàn vẹn tài chính:*
     - *Điều kiện 1 (Bảo toàn số dư ví theo quy tắc bảo toàn số dư ví):* Tính toán lại số dư độc lập từ toàn bộ lịch sử dòng tiền (gồm các khoản thu, chi, chuyển tiền đi/đến, chi phí và mốc kiểm kê điều chỉnh số dư) cho từng ví; so khớp với số dư hiện hành (`currentBalance`) đang ghi nhận trên ví. Sai số cho phép bằng đúng 0.00 VNĐ theo quy tắc độ chính xác số học tài chính.
     - *Điều kiện 2 (Cân bằng nguồn và đích khi chuyển tiền theo quy tắc chuyển tiền nội bộ):* Quét toàn bộ các giao dịch chuyển tiền nội bộ, đối soát số tiền bị trừ tại ví nguồn phải bằng chính xác số tiền được cộng tại ví đích (không tính khoản chi phí phát sinh).
     - *Điều kiện 3 (Tính toàn vẹn danh mục theo quy tắc cấu trúc danh mục ba cấp):* Quét toàn bộ sổ giao dịch thu chi, bảo đảm mọi giao dịch đều gắn với danh mục chi tiết hợp lệ cấp 3 (bao gồm cả danh mục đã xóa mềm) và không có giao dịch nào bị sai lệch Loại thu chi (`INCOME`/`EXPENSE`).
     - *Điều kiện 4 (Toàn vẹn liên kết phí giao dịch):* Quét toàn bộ các khoản chi phí phát sinh gắn kèm giao dịch thu chi và chuyển tiền, bảo đảm mọi bản ghi phí đều thuộc về một giao dịch hợp lệ đang tồn tại, không có phí mồ côi.
     - Phân loại kết quả kiểm tra theo các mức độ nghiêm trọng (`INFO`, `WARNING`, `ERROR`) và tổng hợp đề xuất hành động khắc phục cụ thể (như tự động tính lại số dư hoặc khuyến nghị phục hồi từ bản sao lưu).
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Tổng hợp trạng thái kiểm tra của cả 4 điều kiện (Đạt hoặc Không đạt), số lượng bản ghi đã quét, danh sách chi tiết các sai lệch phát hiện và hành động đề xuất vào đối tượng `ReconciliationReportDto` và trả về Tầng Trình diễn để kết xuất báo cáo đối soát.
