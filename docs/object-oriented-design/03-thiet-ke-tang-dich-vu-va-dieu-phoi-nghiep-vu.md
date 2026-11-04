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
