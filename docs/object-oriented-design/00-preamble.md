# Hồ sơ Thiết kế Hướng đối tượng Hệ thống Quản lý Tài chính Cá nhân

> **HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG (PTIT)**

| Thông tin | Chi tiết |
| :--- | :--- |
| **Khoa** | Khoa Công nghệ Thông tin 1 |
| **Học phần** | Lập trình Hướng đối tượng |
| **Giảng viên** | Phạm Hoàng Việt <br/> Nguyễn Đình Quảng |
| **Nhóm** | 3 |
| **Thành viên** | Lê Anh Minh (B24DCCE180)<br/>Ứng Trọng Trình (B24DCCE271)<br/>Nguyễn Đình Tiến (B24DCCE264)<br/>Nguyễn Đinh Anh Quân (B24DCCE222) |

## Mục lục Tổng quan Hồ sơ Thiết kế Hướng đối tượng

### Danh mục Tài liệu Thành phần

- [00-preamble.md](00-preamble.md): Thông tin định danh học phần, giảng viên hướng dẫn và nhóm tác giả
- [01-kien-truc-he-thong-va-phan-tang.md](01-kien-truc-he-thong-va-phan-tang.md): Kiến trúc phân tầng, cấu trúc gói mã nguồn và quy tắc kiến trúc
- [02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md): Sơ đồ lớp thiết kế cho các lớp thực thể và đối tượng giá trị bất biến
- [03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md](03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md): Thiết kế tầng dịch vụ ứng dụng và điều phối nghiệp vụ
- [04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md): Thiết kế tầng lưu trữ dữ liệu, giao diện kho lưu trữ và các bộ điều hợp
- [05-so-do-tuong-tac-thiet-ke-sequence-diagram.md](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md): Sơ đồ tuần tự thiết kế chi tiết cho các ca sử dụng của hệ thống
- [06-ap-dung-nguyen-ly-solid-va-thiet-ke-mau-gof.md](06-ap-dung-nguyen-ly-solid-va-thiet-ke-mau-gof.md): Đánh giá tuân thủ nguyên lý thiết kế và chi tiết các mẫu thiết kế hướng đối tượng
- [07-thiet-ke-giao-dien-nguoi-dung.md](07-thiet-ke-giao-dien-nguoi-dung.md): Thiết kế giao diện đồ họa người dùng, bố cục màn hình, bắt sự kiện và đối tượng truyền dữ liệu
- [08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md): Cây phân cấp ngoại lệ hệ thống và từ điển mã lỗi nghiệp vụ chuẩn hóa
- [09-ma-tran-truy-vet-thiet-ke-sang-phan-tich-va-srs.md](09-ma-tran-truy-vet-thiet-ke-sang-phan-tich-va-srs.md): Ma trận truy vết hai chiều giữa yêu cầu, phân tích và bản vẽ thiết kế
- [10-tong-ket-va-lo-trinh-cai-dat.md](10-tong-ket-va-lo-trinh-cai-dat.md): Tổng kết kết quả thiết kế và lộ trình cài đặt mã nguồn
- [11-tai-lieu-trich-dan.md](11-tai-lieu-trich-dan.md): Danh mục tài liệu tham khảo kỹ thuật và chuẩn mực quốc tế chuẩn APA
- [ood-tong-hop.md](ood-tong-hop.md): Toàn văn tài liệu tổng hợp thiết kế hướng đối tượng hợp nhất

### Phân cấp Cấu trúc Chi tiết các Phần

- [01. Kiến trúc hệ thống và phân tầng mã nguồn](01-kien-truc-he-thong-va-phan-tang.md#01-kiến-trúc-hệ-thống-và-phân-tầng-mã-nguồn)
  - [1.1 Tổng quan kiến trúc phân tầng](01-kien-truc-he-thong-va-phan-tang.md#11-tổng-quan-kiến-trúc-phân-tầng)
    - [1.1.1 Lý do lựa chọn kiến trúc phân tầng](01-kien-truc-he-thong-va-phan-tang.md#111-lý-do-lựa-chọn-kiến-trúc-phân-tầng)
    - [1.1.2 Sơ đồ kiến trúc phân tầng](01-kien-truc-he-thong-va-phan-tang.md#112-sơ-đồ-kiến-trúc-phân-tầng)
  - [1.2 Trách nhiệm và ranh giới của các tầng](01-kien-truc-he-thong-va-phan-tang.md#12-trách-nhiệm-và-ranh-giới-của-các-tầng)
  - [1.3 Cấu trúc gói mã nguồn](01-kien-truc-he-thong-va-phan-tang.md#13-cấu-trúc-gói-mã-nguồn)
  - [1.4 Luồng xử lý và biến đổi dữ liệu](01-kien-truc-he-thong-va-phan-tang.md#14-luồng-xử-lý-và-biến-đổi-dữ-liệu)
- [02. Thiết kế lớp thực thể và đối tượng giá trị](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#02-thiết-kế-lớp-thực-thể-và-đối-tượng-giá-trị)
  - [2.1 Sơ đồ lớp tổng quan](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#21-sơ-đồ-lớp-tổng-quan)
    - [2.1.1 Sơ đồ cấu trúc các lớp thực thể](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#211-sơ-đồ-cấu-trúc-các-lớp-thực-thể)
    - [2.1.2 Danh mục lớp và kiểu dữ liệu](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#212-danh-mục-lớp-và-kiểu-dữ-liệu)
  - [2.2 Thiết kế các lớp thực thể](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#22-thiết-kế-các-lớp-thực-thể)
    - [2.2.1 Lớp Ví (Wallet)](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#221-lớp-ví-wallet)
    - [2.2.2 Lớp giao dịch cơ sở và giao dịch thu chi](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#222-lớp-giao-dịch-cơ-sở-và-giao-dịch-thu-chi)
    - [2.2.3 Lớp giao dịch chuyển tiền](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#223-lớp-giao-dịch-chuyển-tiền)
    - [2.2.4 Lớp chi phí giao dịch](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#224-lớp-chi-phí-giao-dịch)
    - [2.2.5 Lớp danh mục chi tiết](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#225-lớp-danh-mục-chi-tiết)
    - [2.2.6 Lớp nhóm nhu cầu](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#226-lớp-nhóm-nhu-cầu)
    - [2.2.7 Lớp bản ghi điều chỉnh số dư](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#227-lớp-bản-ghi-điều-chỉnh-số-dư)
  - [2.3 Thiết kế các đối tượng giá trị](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#23-thiết-kế-các-đối-tượng-giá-trị)
    - [2.3.1 Tiền tệ (Money)](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#231-tiền-tệ-money)
    - [2.3.2 Ngày giao dịch (TransactionDate)](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#232-ngày-giao-dịch-transactiondate)
    - [2.3.3 Kiểu liệt kê loại giao dịch (TransactionType)](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#233-kiểu-liệt-kê-loại-giao-dịch-transactiontype)
    - [2.3.4 Dòng tiền thuần (NetCashFlow)](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#234-dòng-tiền-thuần-netcashflow)
    - [2.3.5 Tỷ lệ tiết kiệm (SavingsRate)](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#235-tỷ-lệ-tiết-kiệm-savingsrate)
    - [2.3.6 Tỷ trọng cơ cấu thu chi (SpendingStructureShare)](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#236-tỷ-trọng-cơ-cấu-thu-chi-spendingstructureshare)
    - [2.3.7 Tên định danh thực thể (EntityName)](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#237-tên-định-danh-thực-thể-entityname)
    - [2.3.8 Ghi chú diễn giải (Note)](02-thiet-ke-cac-lop-thuc-the-va-doi-tuong-gia-tri.md#238-ghi-chú-diễn-giải-note)
- [03. Thiết kế tầng dịch vụ ứng dụng và điều phối nghiệp vụ](03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md#03-thiết-kế-tầng-dịch-vụ-ứng-dụng-và-điều-phối-nghiệp-vụ)
  - [3.1 Tổng quan và danh mục dịch vụ ứng dụng](03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md#31-tổng-quan-và-danh-mục-dịch-vụ-ứng-dụng)
  - [3.2 Dịch vụ quản lý ví (WalletService)](03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md#32-dịch-vụ-quản-lý-ví-walletservice)
  - [3.3 Dịch vụ ghi nhận thu chi (TransactionService)](03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md#33-dịch-vụ-ghi-nhận-thu-chi-transactionservice)
  - [3.4 Dịch vụ chuyển tiền nội bộ (TransferService)](03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md#34-dịch-vụ-chuyển-tiền-nội-bộ-transferservice)
  - [3.5 Dịch vụ danh mục phân loại (CategoryService)](03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md#35-dịch-vụ-danh-mục-phân-loại-categoryservice)
  - [3.6 Dịch vụ báo cáo và thống kê tài chính (ReportService)](03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md#36-dịch-vụ-báo-cáo-và-thống-kê-tài-chính-reportservice)
  - [3.7 Dịch vụ quản trị và sao lưu dữ liệu (DataManagementService)](03-thiet-ke-tang-dich-vu-va-dieu-phoi-nghiep-vu.md#37-dịch-vụ-quản-trị-và-sao-lưu-dữ-liệu-datamanagementservice)
- [04. Thiết kế tầng lưu trữ và truy xuất dữ liệu](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#04-thiết-kế-tầng-lưu-trữ-và-truy-xuất-dữ-liệu)
  - [4.1 Giao diện lưu trữ cơ sở (GenericRepository)](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#41-giao-diện-lưu-trữ-cơ-sở-genericrepository)
    - [4.1.1 Thao tác dữ liệu dùng chung](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#411-thao-tác-dữ-liệu-dùng-chung)
    - [4.1.2 Quy chuẩn trả về và xử lý lỗi](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#412-quy-chuẩn-trả-về-và-xử-lý-lỗi)
  - [4.2 Giao diện lưu trữ theo thực thể](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#42-giao-diện-lưu-trữ-theo-thực-thể)
    - [4.2.1 Kho lưu trữ ví tiền (WalletRepository)](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#421-kho-lưu-trữ-ví-tiền-walletrepository)
    - [4.2.2 Kho lưu trữ giao dịch (TransactionRepository)](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#422-kho-lưu-trữ-giao-dịch-transactionrepository)
    - [4.2.3 Kho lưu trữ danh mục thu chi (CategoryRepository)](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#423-kho-lưu-trữ-danh-mục-thu-chi-categoryrepository)
    - [4.2.4 Kho lưu trữ kiểm kê số dư (BalanceAdjustmentRepository)](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#424-kho-lưu-trữ-kiểm-kê-số-dư-balanceadjustmentrepository)
    - [4.2.5 Kho lưu trữ sao lưu hệ thống (BackupRepository)](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#425-kho-lưu-trữ-sao-lưu-hệ-thống-backuprepository)
  - [4.3 Cài đặt hạ tầng lưu trữ](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#43-cài-đặt-hạ-tầng-lưu-trữ)
    - [4.3.1 Định dạng và cấu trúc bản ghi](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#431-định-dạng-và-cấu-trúc-bản-ghi)
    - [4.3.2 Cơ chế đọc ghi và an toàn dữ liệu](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#432-cơ-chế-đọc-ghi-và-an-toàn-dữ-liệu)
    - [4.3.3 Cài đặt lưu trữ trong bộ nhớ (Memory Repository)](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#433-cài-đặt-lưu-trữ-trong-bộ-nhớ-memory-repository)
    - [4.3.4 So sánh đánh đổi giữa hai cơ chế lưu trữ](04-thiet-ke-tang-luu-tru-va-truy-xuat-du-lieu.md#434-so-sánh-đánh-đổi-giữa-hai-cơ-chế-lưu-trữ)
- [05. Sơ đồ tuần tự thiết kế](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#05-sơ-đồ-tuần-tự-thiết-kế)
  - [5.1 Nguyên tắc thiết kế sơ đồ tuần tự chi tiết](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#51-nguyên-tắc-thiết-kế-sơ-đồ-tuần-tự-chi-tiết)
  - [5.2 Sơ đồ tuần tự nhóm quản lý ví](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#52-sơ-đồ-tuần-tự-nhóm-quản-lý-ví)
    - [5.2.1 Sơ đồ tạo mới ví](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#521-sơ-đồ-tạo-mới-ví)
    - [5.2.2 Sơ đồ xem tổng hợp số dư và cơ cấu ví](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#522-sơ-đồ-xem-tổng-hợp-số-dư-và-cơ-cấu-ví)
    - [5.2.3 Sơ đồ cập nhật thông tin ví](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#523-sơ-đồ-cập-nhật-thông-tin-ví)
    - [5.2.4 Sơ đồ điều chỉnh số dư ví thực tế](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#524-sơ-đồ-điều-chỉnh-số-dư-ví-thực-tế)
  - [5.3 Sơ đồ tuần tự nhóm ghi nhận thu chi](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#53-sơ-đồ-tuần-tự-nhóm-ghi-nhận-thu-chi)
    - [5.3.1 Sơ đồ ghi nhận giao dịch thu chi](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#531-sơ-đồ-ghi-nhận-giao-dịch-thu-chi)
    - [5.3.2 Sơ đồ tra cứu và lọc lịch sử giao dịch](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#532-sơ-đồ-tra-cứu-và-lọc-lịch-sử-giao-dịch)
    - [5.3.3 Sơ đồ chỉnh sửa giao dịch thu chi](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#533-sơ-đồ-chỉnh-sửa-giao-dịch-thu-chi)
    - [5.3.4 Sơ đồ xóa giao dịch thu chi](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#534-sơ-đồ-xóa-giao-dịch-thu-chi)
  - [5.4 Sơ đồ tuần tự nhóm chuyển tiền nội bộ](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#54-sơ-đồ-tuần-tự-nhóm-chuyển-tiền-nội-bộ)
    - [5.4.1 Sơ đồ chuyển tiền giữa hai ví](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#541-sơ-đồ-chuyển-tiền-giữa-hai-ví)
    - [5.4.2 Sơ đồ tra cứu lịch sử chuyển tiền](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#542-sơ-đồ-tra-cứu-lịch-sử-chuyển-tiền)
    - [5.4.3 Sơ đồ xóa và hoàn tác giao dịch chuyển tiền](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#543-sơ-đồ-xóa-và-hoàn-tác-giao-dịch-chuyển-tiền)
  - [5.5 Sơ đồ tuần tự nhóm danh mục phân loại](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#55-sơ-đồ-tuần-tự-nhóm-danh-mục-phân-loại)
    - [5.5.1 Sơ đồ thêm mới danh mục chi tiết](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#551-sơ-đồ-thêm-mới-danh-mục-chi-tiết)
    - [5.5.2 Sơ đồ đổi tên danh mục](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#552-sơ-đồ-đổi-tên-danh-mục)
    - [5.5.3 Sơ đồ khôi phục danh mục đã xóa](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#553-sơ-đồ-khôi-phục-danh-mục-đã-xóa)
    - [5.5.4 Sơ đồ xóa danh mục (xóa cứng và xóa mềm)](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#554-sơ-đồ-xóa-danh-mục-xóa-cứng-và-xóa-mềm)
  - [5.6 Sơ đồ tuần tự nhóm báo cáo và quản trị dữ liệu](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#56-sơ-đồ-tuần-tự-nhóm-báo-cáo-và-quản-trị-dữ-liệu)
    - [5.6.1 Sơ đồ lập báo cáo dòng tiền định kỳ](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#561-sơ-đồ-lập-báo-cáo-dòng-tiền-định-kỳ)
    - [5.6.2 Sơ đồ lập báo cáo cơ cấu thu chi](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#562-sơ-đồ-lập-báo-cáo-cơ-cấu-thu-chi)
    - [5.6.3 Sơ đồ xuất dữ liệu sao lưu hệ thống](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#563-sơ-đồ-xuất-dữ-liệu-sao-lưu-hệ-thống)
    - [5.6.4 Sơ đồ phục hồi dữ liệu hệ thống](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#564-sơ-đồ-phục-hồi-dữ-liệu-hệ-thống)
    - [5.6.5 Sơ đồ đối soát toàn vẹn dữ liệu tài chính](05-so-do-tuong-tac-thiet-ke-sequence-diagram.md#565-sơ-đồ-đối-soát-toàn-vẹn-dữ-liệu-tài-chính)
- [06. Áp dụng nguyên lý SOLID và mẫu thiết kế hướng đối tượng](06-ap-dung-nguyen-ly-solid-va-thiet-ke-mau-gof.md#06-áp-dụng-nguyên-lý-solid-và-mẫu-thiết-kế-hướng-đối-tượng)
  - [6.1 Đánh giá tuân thủ năm nguyên lý SOLID](06-ap-dung-nguyen-ly-solid-va-thiet-ke-mau-gof.md#61-đánh-giá-tuân-thủ-năm-nguyên-lý-solid)
  - [6.2 Áp dụng các mẫu thiết kế trong hệ thống](06-ap-dung-nguyen-ly-solid-va-thiet-ke-mau-gof.md#62-áp-dụng-các-mẫu-thiết-kế-trong-hệ-thống)
    - [6.2.1 Mẫu Factory Method khởi tạo giao dịch](06-ap-dung-nguyen-ly-solid-va-thiet-ke-mau-gof.md#621-mẫu-factory-method-khởi-tạo-giao-dịch)
    - [6.2.2 Mẫu Strategy định dạng báo cáo tài chính](06-ap-dung-nguyen-ly-solid-va-thiet-ke-mau-gof.md#622-mẫu-strategy-định-dạng-báo-cáo-tài-chính)
    - [6.2.3 Mẫu State quản lý vòng đời ví tiền](06-ap-dung-nguyen-ly-solid-va-thiet-ke-mau-gof.md#623-mẫu-state-quản-lý-vòng-đời-ví-tiền)
    - [6.2.4 Mẫu Observer xử lý sự kiện giao dịch](06-ap-dung-nguyen-ly-solid-va-thiet-ke-mau-gof.md#624-mẫu-observer-xử-lý-sự-kiện-giao-dịch)
- [07. Thiết kế giao diện người dùng](07-thiet-ke-giao-dien-nguoi-dung.md#07-thiết-kế-giao-diện-người-dùng)
  - [7.1 Cấu trúc tầng giao diện người dùng](07-thiet-ke-giao-dien-nguoi-dung.md#71-cấu-trúc-tầng-giao-diện-người-dùng)
    - [7.1.1 Nguyên lý phân tách giao diện và xử lý sự kiện](07-thiet-ke-giao-dien-nguoi-dung.md#711-nguyên-lý-phân-tách-giao-diện-và-xử-lý-sự-kiện)
    - [7.1.2 Vòng đời ứng dụng đồ họa và luồng bắt sự kiện](07-thiet-ke-giao-dien-nguoi-dung.md#712-vòng-đời-ứng-dụng-đồ-họa-và-luồng-bắt-sự-kiện)
  - [7.2 Bố cục cửa sổ chính và màn hình phân hệ](07-thiet-ke-giao-dien-nguoi-dung.md#72-bố-cục-cửa-sổ-chính-và-màn-hình-phân-hệ)
    - [7.2.1 Cấu trúc phân vùng cửa sổ chính](07-thiet-ke-giao-dien-nguoi-dung.md#721-cấu-trúc-phân-vùng-cửa-sổ-chính)
    - [7.2.2 Ánh xạ màn hình chức năng và ca sử dụng](07-thiet-ke-giao-dien-nguoi-dung.md#722-ánh-xạ-màn-hình-chức-năng-và-ca-sử-dụng)
    - [7.2.3 Quy chuẩn hiển thị bảng dữ liệu và hộp thoại](07-thiet-ke-giao-dien-nguoi-dung.md#723-quy-chuẩn-hiển-thị-bảng-dữ-liệu-và-hộp-thoại)
  - [7.3 Thiết kế biểu mẫu nhập liệu và bắt sự kiện](07-thiet-ke-giao-dien-nguoi-dung.md#73-thiết-kế-biểu-mẫu-nhập-liệu-và-bắt-sự-kiện)
    - [7.3.1 Xác thực biểu mẫu đồ họa](07-thiet-ke-giao-dien-nguoi-dung.md#731-xác-thực-biểu-mẫu-đồ-họa)
    - [7.3.2 Phản hồi người dùng và hộp thoại xác nhận](07-thiet-ke-giao-dien-nguoi-dung.md#732-phản-hồi-người-dùng-và-hộp-thoại-xác-nhận)
  - [7.4 Thiết kế các đối tượng truyền dữ liệu (DTO)](07-thiet-ke-giao-dien-nguoi-dung.md#74-thiết-kế-các-đối-tượng-truyền-dữ-liệu-dto)
    - [7.4.1 Mục đích và tính đóng gói của đối tượng truyền dữ liệu](07-thiet-ke-giao-dien-nguoi-dung.md#741-mục-đích-và-tính-đóng-gói-của-đối-tượng-truyền-dữ-liệu)
    - [7.4.2 Danh mục các lớp truyền dữ liệu chi tiết](07-thiet-ke-giao-dien-nguoi-dung.md#742-danh-mục-các-lớp-truyền-dữ-liệu-chi-tiết)
- [08. Thiết kế xử lý ngoại lệ và nhật ký](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md#08-thiết-kế-xử-lý-ngoại-lệ-và-nhật-ký)
  - [8.1 Chiến lược quản lý ngoại lệ toàn hệ thống](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md#81-chiến-lược-quản-lý-ngoại-lệ-toàn-hệ-thống)
    - [8.1.1 Phân loại ngoại lệ kỹ thuật và ngoại lệ nghiệp vụ](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md#811-phân-loại-ngoại-lệ-kỹ-thuật-và-ngoại-lệ-nghiệp-vụ)
    - [8.1.2 Nguyên tắc bắt và chuyển đổi ngoại lệ xuyên tầng](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md#812-nguyên-tắc-bắt-và-chuyển-đổi-ngoại-lệ-xuyên-tầng)
  - [8.2 Cây phân cấp lớp ngoại lệ hệ thống](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md#82-cây-phân-cấp-lớp-ngoại-lệ-hệ-thống)
    - [8.2.1 Sơ đồ phân cấp lớp ngoại lệ hướng đối tượng](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md#821-sơ-đồ-phân-cấp-lớp-ngoại-lệ-hướng-đối-tượng)
    - [8.2.2 Danh mục các lớp ngoại lệ nghiệp vụ chi tiết](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md#822-danh-mục-các-lớp-ngoại-lệ-nghiệp-vụ-chi-tiết)
  - [8.3 Bảng mã lỗi nghiệp vụ chuẩn hóa](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md#83-bảng-mã-lỗi-nghiệp-vụ-chuẩn-hóa)
    - [8.3.1 Cấu trúc định danh mã lỗi hệ thống](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md#831-cấu-trúc-định-danh-mã-lỗi-hệ-thống)
    - [8.3.2 Danh mục mã lỗi và thông điệp phản hồi](08-thiet-ke-xu-ly-ngoai-le-va-nhat-ky.md#832-danh-mục-mã-lỗi-và-thông-điệp-phản-hồi)
- [09. Ma trận truy vết thiết kế](09-ma-tran-truy-vet-thiet-ke-sang-phan-tich-va-srs.md#09-ma-trận-truy-vết-thiết-kế)
  - [9.1 Ánh xạ yêu cầu chức năng sang lớp thiết kế](09-ma-tran-truy-vet-thiet-ke-sang-phan-tich-va-srs.md#91-ánh-xạ-yêu-cầu-chức-năng-sang-lớp-thiết-kế)
  - [9.2 Đánh giá độ bao phủ và tính nhất quán thiết kế](09-ma-tran-truy-vet-thiet-ke-sang-phan-tich-va-srs.md#92-đánh-giá-độ-bao-phủ-và-tính-nhất-quán-thiết-kế)
- [10. Tổng kết](10-tong-ket-va-lo-trinh-cai-dat.md#10-tổng-kết)
- [11. Tài liệu tham khảo](11-tai-lieu-trich-dan.md#11-tài-liệu-tham-khảo)
