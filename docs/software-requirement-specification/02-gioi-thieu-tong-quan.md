## 1. GIỚI THIỆU TỔNG QUAN

### 1.1 Mục đích

Tài liệu đặc tả yêu cầu phần mềm này xác định các yêu cầu cho Hệ thống quản lý tài chính cá nhân. Các yêu cầu gồm yêu cầu chức năng, yêu cầu phi chức năng, 5 quy tắc nghiệp vụ cốt lõi: Quy tắc độ chính xác tài chính, Quy tắc bảo toàn số dư ví, Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ, Quy tắc phân cấp danh mục thu chi 3 cấp, Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`), cùng tiêu chí nghiệm thu kiểm chứng được. Tài liệu đặt ra căn cứ kỹ thuật cho hoạt động phân tích thiết kế hướng đối tượng. Căn cứ này áp dụng cả cho việc cài đặt mã nguồn trên nền tảng Java 17 LTS và triển khai bộ kiểm thử tự động độc lập công nghệ.

### 1.2 Phạm vi sản phẩm

Hệ thống Quản lý Tài chính Cá nhân (PFMS) là ứng dụng máy tính để bàn chạy cục bộ trên máy ảo Java, phục vụ một người dùng duy nhất. Hệ thống đáp ứng nhu cầu quản lý tài chính cá nhân trên máy tính cá nhân. Hệ thống độc lập hoàn toàn với các hệ thống tài chính doanh nghiệp và dịch vụ ngân hàng trực tuyến.

#### 1.2.1 Phạm vi chức năng trong hệ thống

Hệ thống cung cấp các nhóm năng lực nghiệp vụ sau đây, với quy ước mã định danh được chuẩn hóa tại Mục 1.5.

| STT | Nhóm | Mã nhóm | Mô tả nghiệp vụ |
| :---: | :--- | :---: | :--- |
| 1 | Ví | `WAL` | Quản lý vòng đời ví gồm một ví tiền mặt có sẵn và các ví do người dùng tự tạo. Tính số dư khả dụng và tỷ lệ phân bổ tiền trong các ví bằng số thập phân chính xác. |
| 2 | Ghi nhận thu chi | `TXN` | Ghi nhận các giao dịch thu nhập (`INCOME`) và chi tiêu (`EXPENSE`). Liên kết với ví thanh toán và danh mục. Ghi riêng chi phí (nếu có) thành phí giao dịch liên kết. Cập nhật số dư ví cùng lúc với việc ghi nhận giao dịch. |
| 3 | Chuyển tiền nội bộ | `TRF` | Chuyển tiền giữa hai ví nội bộ theo cơ chế ghi nhận đồng thời (trừ ví nguồn, cộng ví đích). Giữ cho tổng số dư trong các ví không đổi, ngoại trừ chi phí chuyển tiền. |
| 4 | Danh mục phân loại | `CAT` | Giữ cấu trúc danh mục 3 cấp gồm Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết; bảo đảm liên kết hợp lệ tới nhóm cha khi gắn nhãn giao dịch. |
| 5 | Thống kê và báo cáo | `REP` | Trích xuất chuỗi dữ liệu 12 tháng liên tục kết thúc tại tháng người dùng chọn, mặc định là tháng hiện tại. Phân tích tỷ lệ cơ cấu chi tiêu theo nhóm danh mục, dòng tiền thuần và tỷ lệ tiết kiệm định kỳ. |
| 6 | Lưu trữ và sao lưu dữ liệu | `DAT` | Lưu dữ liệu lâu dài trên tệp JSON cục bộ bằng cách ghi tệp an toàn, tệp chính không bị hỏng khi mất điện giữa chừng. Hỗ trợ xuất và nhập tệp sao lưu độc lập để phục vụ kiểm tra lại dữ liệu. |

#### 1.2.2 Ranh giới ngoài hệ thống và căn cứ loại trừ

Các hạng mục kỹ thuật và nghiệp vụ được loại trừ khỏi ranh giới giải pháp của hệ thống.

| STT | Hạng mục | Ranh giới phân định cụ thể | Căn cứ loại trừ |
| :---: | :--- | :--- | :--- |
| 1 | Tích hợp Open Banking và cổng thanh toán trực tuyến | Hệ thống không tích hợp Open Banking API hoặc Webhook tự động cào dữ liệu giao dịch; Người dùng trực tiếp nhập các giao dịch đã xảy ra. | Ứng dụng vận hành ngoại tuyến và độc lập trên máy trạm cục bộ. Ứng dụng không lưu thông tin xác thực của bên thứ ba (Tài khoản, khóa truy cập) hay phụ thuộc vào hạ tầng mạng ngoài. |
| 2 | Quản trị đa tài khoản và kiểm soát truy cập phân quyền | Hệ thống chỉ phục vụ một Người dùng duy nhất; loại trừ việc đăng ký nhiều tài khoản trực tuyến qua mạng hoặc phân cấp quyền hạn quản trị. | Ứng dụng máy tính để bàn đơn tiến trình phục vụ một Người dùng duy nhất (Giả định `AS-01`), nên không cần phân quyền nhiều cấp và không phát sinh xung đột ghi đồng thời. |
| 3 | Chuyển mạch thanh toán và tạo giao dịch tiền thực | Hệ thống không gửi lệnh chuyển tiền đến mạng lưới thanh toán liên ngân hàng. Dữ liệu dòng tiền chỉ phản ánh thông tin kế toán quản trị nội bộ sau khi giao dịch thực tế đã diễn ra ngoài đời thực. | Giới hạn hệ thống trong phạm vi sổ sách kế toán cá nhân, không xử lý thanh toán thật. |

### 1.3 Đối tượng sử dụng và hướng dẫn đọc tài liệu

Tài liệu phục vụ nhóm vai trò kỹ thuật với mục tiêu sử dụng và lộ trình tiếp cận nội dung tương ứng.

| Nhóm đối tượng | Mục đích sử dụng tài liệu | Phạm vi tài liệu trọng tâm |
| --- | --- | --- |
| Kỹ sư phân tích và thiết kế | Xác định ranh giới hệ thống, chuyển hóa yêu cầu nghiệp vụ thành kiến trúc và thiết kế phần mềm chi tiết | Mục 1, Mục 2, Mục 4, Mục 6, Mục 7 |
| Kỹ sư phát triển phần mềm | Cài đặt logic nghiệp vụ, cấu trúc dữ liệu và xử lý ngoại lệ trên nền tảng Java 17 | Mục 3, Mục 4, Mục 6 |
| Kỹ sư kiểm thử phần mềm | Xây dựng ca kiểm thử đơn vị và kiểm thử tích hợp dựa trên luồng sự kiện và điều kiện biên | Mục 3, Mục 6, Mục 8 |
| Cán bộ thẩm định kỹ thuật | So khớp độ bao phủ của yêu cầu qua ma trận kiểm chứng và truy vết yêu cầu, kiểm tra mức độ đáp ứng yêu cầu phi chức năng ở Mục 5 | Toàn bộ tài liệu |

### 1.4 Thuật ngữ, từ viết tắt và định nghĩa

| Thuật ngữ | Định nghĩa |
| --- | --- |
| Ví | Tài khoản lưu giữ số dư tiền khả dụng thực tế của người dùng. Hệ thống có sẵn một ví tiền mặt. Người dùng có thể tạo thêm các ví khác. |
| Số dư khả dụng | Số tiền hiện có trong một ví, bằng tổng số tiền đã vào ví trừ tổng số tiền đã ra khỏi ví kể từ khi ví được tạo với số dư 0.00. Số dư này không được âm. |
| Tổng số dư | Tổng số dư khả dụng của các ví trong hệ thống. |
| Giao dịch thu chi | Bản ghi biến động tài chính đơn lẻ làm tăng (`INCOME`) hoặc giảm (`EXPENSE`) số dư khả dụng của một ví xác định, có gắn nhãn phân loại danh mục. |
| Ví nguồn, ví đích | Ví nguồn là ví bị trừ tiền, ví đích là ví được cộng tiền trong một lần chuyển tiền nội bộ. |
| Chuyển tiền nội bộ | Nghiệp vụ chuyển tiền giữa hai ví nội bộ theo cơ chế ghi nhận đồng thời (trừ ví nguồn, cộng ví đích), bảo toàn tổng số dư hệ thống trừ chi phí chuyển tiền (nếu có). |
| Giao dịch chuyển tiền | Bản ghi dữ liệu của một lần chuyển tiền nội bộ, lưu thông tin ví nguồn, ví đích, số tiền chuyển và liên kết phí giao dịch (nếu có). |
| Chi phí | Số tiền phí phát sinh khi ghi nhận một giao dịch thu chi hoặc một giao dịch chuyển tiền, có giá trị không âm. Chi phí là số tiền của phí, khác với chi tiêu (Giao dịch làm giảm số dư ví). |
| Phí giao dịch | Giao dịch chi tiêu (`EXPENSE`) do hệ thống tự động ghi nhận cho chi phí phát sinh và liên kết với giao dịch gốc (thu chi hoặc chuyển tiền). Người dùng chọn danh mục cho phí; phí được cập nhật hoặc xóa đồng thời theo giao dịch gốc. |
| Cấu trúc danh mục | Mô hình phân loại dạng cây 3 cấp gồm Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết. |
| Hoàn nguyên | Đưa số dư và dữ liệu về đúng trạng thái trước khi thao tác xảy ra; dùng khi xóa một giao dịch hoặc khi thao tác nhiều bước bị lỗi. |
| Ghi nhận đồng thời | Cơ chế cập nhật cùng lúc hai biến động số dư khi chuyển tiền: trừ tiền ví nguồn và cộng tiền ví đích cùng được ghi nhận hoặc cùng không được ghi nhận. |
| Khoản ghi | Một dòng ghi số tiền tăng hoặc giảm của một ví, như phần trừ tiền hoặc phần cộng tiền của một lần chuyển tiền, hoặc một lần điều chỉnh số dư ví. |
| Điều chỉnh số dư | Thao tác đưa số dư khả dụng của một ví về đúng số tiền thực tế Người dùng đang có. Chênh lệch được ghi thành một bản ghi riêng, có thể truy vết. |
| Sổ giao dịch thu chi | Nơi lưu các giao dịch thu chi, kể cả phí giao dịch. |
| Sổ giao dịch chuyển tiền | Nơi lưu các giao dịch chuyển tiền. |
| Sổ điều chỉnh số dư | Nơi lưu các lần điều chỉnh số dư. Mỗi bản ghi độc lập và không được sửa. |
| Lịch sử dòng tiền | Toàn bộ biến động số dư của một ví: giao dịch thu chi (Kể cả phí giao dịch), giao dịch chuyển tiền và điều chỉnh số dư. Dùng để tính lại số dư. |
| Lịch sử giao dịch thu chi | Danh sách giao dịch thu chi do chức năng tra cứu và lọc trả về, kể cả phí giao dịch. Không gồm giao dịch chuyển tiền và điều chỉnh số dư. |
| Dòng tiền thuần | Tổng số tiền thu trừ tổng số tiền chi trong cùng một khoảng thời gian. Khoản thu là giao dịch thu chi loại `INCOME`, khoản chi là loại `EXPENSE`. |
| Tỷ lệ tiết kiệm | Dòng tiền thuần của một tháng chia tổng số tiền thu của tháng đó, nhân 100%. Bằng 0.00% khi tổng số tiền thu của tháng nhỏ hơn hoặc bằng 0. |
| Số thực nhị phân | Kiểu số thực (float, double) mà máy tính lưu bằng các chữ số 0 và 1 (Hệ nhị phân), có thể làm tròn sai khi tính tiền. Hệ thống không dùng kiểu này trong tính toán nghiệp vụ. |
| Số thập phân chính xác | Kiểu số học có phần thập phân cố định, bảo đảm độ chính xác tuyệt đối và loại bỏ hoàn toàn sai số làm tròn khi tính toán số tiền và tỷ lệ phần trăm. |
| Làm tròn ngân hàng | Cách làm tròn về số chẵn gần nhất khi giá trị nằm đúng giữa hai mức. Ví dụ làm tròn đến số nguyên: 2.5 thành 2, 3.5 thành 4. |
| Ghi tệp an toàn | Cách ghi dữ liệu xuống tệp sao cho tệp chính chỉ được thay bằng nội dung mới sau khi toàn bộ nội dung mới đã ghi xong. Nếu quá trình bị ngắt giữa chừng, tệp cũ vẫn giữ nguyên vẹn. |
| Ngày tháng | Giá trị ngày viết theo định dạng YYYY-MM-DD (Năm, tháng, ngày, ngăn cách bằng dấu gạch nối), dùng cho mọi ngày được nhập và lưu trong tệp xuất hoặc tệp nhập. |
| Người dùng | Người duy nhất sử dụng hệ thống và có toàn quyền với dữ liệu. Người dùng sử dụng toàn bộ 6 nhóm chức năng nghiệp vụ trên ứng dụng máy tính để bàn chạy trực tiếp trên máy của người dùng. |

| Viết tắt | Tên đầy đủ | Ý nghĩa |
| --- | --- | --- |
| JDK | Java Development Kit | Bộ công cụ biên dịch và phát triển ứng dụng Java. |
| JRE | Java Runtime Environment | Môi trường chạy mã bytecode của ứng dụng trên máy trạm. |
| JVM | Java Virtual Machine | Máy ảo Java chạy ứng dụng cục bộ. |
| LTS | Long-Term Support | Nhánh phiên bản Java được nhà cung cấp hỗ trợ dài hạn. |
| PFMS | Personal Finance Management System | Tên hệ thống trong phạm vi tài liệu này. |
| RTM | Requirements Traceability Matrix | Ma trận kiểm chứng và truy vết yêu cầu tại Mục 8. |
| UC | Use case | Ca sử dụng đặc tả tại Mục 6 và mô hình hóa tại Mục 7. |

### 1.5 Quy ước mã định danh

Hệ thống dùng cấu trúc mã phân cấp để nhận diện duy nhất và truy vết hai chiều mọi thực thể yêu cầu.

Mã định danh nối các khối bằng dấu gạch ngang theo dạng chung `<TIỀN_TỐ>[-<NHÓM>][-NN][.x]`. Khối trong ngoặc vuông có mặt hay không tùy từng tiền tố, như bảng dưới.

Trong đó, `<NHÓM>` nhận giá trị là một mã `DOMAIN` (Mã nhóm chức năng nghiệp vụ) hoặc một mã nhóm quy tắc nghiệp vụ: `FIN` đối với quy tắc tài chính, `NAM` đối với quy tắc về tên, `FEE` đối với phí giao dịch (Chỉ dùng trong mã lỗi). `NN` là số thứ tự gồm 2 chữ số (Bắt đầu từ `01`), riêng mã lỗi dùng `NNN` gồm 3 chữ số (Bắt đầu từ `001`), đánh độc lập trong từng nhóm khi mã có khối nhóm. `x` là số thứ tự tiêu chí nghiệm thu (Bắt đầu từ `1`). Mã `AC` giữ nguyên khối `<NHÓM>-NN` của mã `FR` tương ứng, thay tiền tố bằng `AC`, rồi thêm `.x`.

Mã định danh gồm 12 dạng mã sau, dùng 10 tiền tố:

| Tiền tố | Nghĩa của mã | Thực thể định danh | Ví dụ | Phạm vi áp dụng |
| --- | --- | --- | --- | --- |
| `G-NN` | Goal | Mục tiêu hệ thống | `G-01` | Bảng mục tiêu hệ thống tại Mục 2.2 |
| `CON-NN` | Constraint | Giới hạn kỹ thuật bắt buộc | `CON-01` | Danh mục giới hạn tại Mục 2.4 |
| `AS-NN` | Assumption | Giả định vận hành hệ thống | `AS-01` | Danh mục giả định tại Mục 2.4 |
| `DE-NN` | Dependency | Sự phụ thuộc hệ thống | `DE-01` | Danh mục phụ thuộc tại Mục 2.4 |
| `BR-FIN-NN` | Business Rule, Finance | Quy tắc nghiệp vụ tài chính | `BR-FIN-01` | Các quy tắc tài chính tại Mục 3 |
| `BR-NAM-NN` | Business Rule, Name | Quy tắc nghiệp vụ về tên | `BR-NAM-01` | Quy tắc về tên của ví, nhóm nhu cầu và danh mục tại Mục 3.5 |
| `ERR-NHÓM-NNN` | Error | Mã lỗi nghiệp vụ | `ERR-NAM-001` | Các bảng mã lỗi tại Mục 3 |
| `FR-DOMAIN` | Functional Requirement | Mã nhóm chức năng | `FR-WAL` | Định danh nhóm chức năng tại Mục 1.2 và Mục 4 |
| `FR-DOMAIN-NN` | Functional Requirement | Yêu cầu chức năng | `FR-WAL-01` | Danh mục 20 yêu cầu chức năng tại Mục 4 |
| `UC-DOMAIN-NN` | Use Case | Ca sử dụng | `UC-WAL-01` | Danh mục 20 ca sử dụng tại Mục 6 |
| `NFR-NN` | Non-Functional Requirement | Yêu cầu phi chức năng | `NFR-01` | Danh mục 6 yêu cầu phi chức năng tại Mục 5 |
| `AC-DOMAIN-NN.x` | Acceptance Criteria | Tiêu chí nghiệm thu | `AC-WAL-01.1` | Tiêu chí nghiệm thu của từng FR tại Mục 4 |

Danh sách giá trị của biến `DOMAIN` là tập hợp cố định gồm 6 nhóm chức năng nghiệp vụ, tương ứng trực tiếp với cấu trúc phân rã chức năng:

| STT | Nhóm | Mã `DOMAIN` | Nghĩa của mã | Đối tượng quản lý | Số lượng FR | Số lượng UC |
| :---: | :--- | :---: | :--- | :--- | :---: | :---: |
| 1 | Ví | `WAL` | Wallet | Ví | 5 | 4 |
| 2 | Ghi nhận thu chi | `TXN` | Transaction | Giao dịch thu chi | 5 | 4 |
| 3 | Chuyển tiền nội bộ | `TRF` | Transfer | Giao dịch chuyển tiền | 2 | 3 |
| 4 | Danh mục phân loại | `CAT` | Category | Danh mục | 3 | 4 |
| 5 | Thống kê và báo cáo | `REP` | Statistics & Report | Thống kê tháng | 2 | 2 |
| 6 | Lưu trữ và sao lưu dữ liệu | `DAT` | Data Management | Dữ liệu tài chính | 3 | 3 |
| **Tổng cộng** | **6 nhóm chức năng** |  |  |  | **20 FR** | **20 UC** |

### 1.6 Quy ước mức độ bắt buộc của yêu cầu

Tài liệu dùng từ chỉ mức, viết thường, để xác định mức độ bắt buộc của giới hạn, quy tắc nghiệp vụ, yêu cầu chức năng và yêu cầu phi chức năng:

| Từ chỉ mức | Mức độ bắt buộc | Ý nghĩa |
| :--- | :--- | :--- |
| Phải | Bắt buộc | Chức năng hoặc điều kiện hệ thống phải thỏa mãn để được nghiệm thu. |
| Không được | Cấm hoàn toàn | Hành vi hoặc trạng thái hệ thống hoàn toàn không được phép xảy ra hoặc phải từ chối tiếp nhận. |
| Nên | Khuyến cáo | Quy cách được ưu tiên áp dụng, chỉ bỏ qua khi có lý do kỹ thuật xác đáng. |
| Có thể, được phép | Tùy chọn | Chức năng hoặc lựa chọn mà người dùng có thể dùng hoặc bỏ qua. |

