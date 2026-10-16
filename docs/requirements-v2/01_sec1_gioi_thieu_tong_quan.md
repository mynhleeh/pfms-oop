## 1. GIỚI THIỆU TỔNG QUAN

### 1.1 Mục đích

Tài liệu Đặc tả Yêu cầu Phần mềm này xác định các yêu cầu cho Hệ thống Quản lý Tài chính Cá nhân. Các yêu cầu gồm yêu cầu chức năng, yêu cầu phi chức năng, các quy tắc kế toán luôn đúng và tiêu chí nghiệm thu đo được bằng số. Tài liệu đặt ra căn cứ kỹ thuật bắt buộc cho hoạt động phân tích thiết kế hướng đối tượng, cài đặt mã nguồn trên nền tảng Java 17 LTS và triển khai bộ kiểm thử tự động độc lập công nghệ.

### 1.2 Phạm vi sản phẩm

PFMS là ứng dụng máy tính để bàn chạy cục bộ trên máy ảo Java, phục vụ một người dùng duy nhất. Hệ thống đáp ứng nhu cầu quản lý tài chính cá nhân trên máy tính cá nhân, độc lập hoàn toàn với các hệ thống tài chính doanh nghiệp và dịch vụ ngân hàng trực tuyến.

Hệ thống có 7 năng lực cốt lõi:
- Quản lý ví đa nguồn.
- Ghi nhận giao dịch thu chi và chuyển khoản theo cách ghi hai vế.
- Vận hành cây danh mục phân loại 3 cấp.
- Theo dõi công nợ hai chiều.
- Quản lý sổ tiết kiệm cùng tài sản đầu tư.
- Tổng hợp báo cáo dòng tiền và tài sản ròng.
- Kiểm tra lại dữ liệu cục bộ để phát hiện sai lệch.

Ranh giới hệ thống gồm phạm vi năng lực bên trong và danh mục các trường hợp loại trừ kỹ thuật.

#### 1.2.1 Phạm vi chức năng trong hệ thống

Hệ thống cung cấp năng lực quản lý tài chính theo 10 nhóm chức năng nghiệp vụ; quy ước mã nhóm chức năng và cấu trúc mã được quy định tại Mục 1.6:

Bảng tổng hợp phạm vi chức năng 10 nhóm chức năng nghiệp vụ:

| STT | Nhóm chức năng nghiệp vụ | Mã nhóm chức năng | Mô tả năng lực nghiệp vụ |
| :---: | :--- | :---: | :--- |
| 1 | Ví tiền | WAL | Quản lý vòng đời tài khoản tiền gồm một ví tiền mặt có sẵn và các ví do người dùng tự tạo; tính số dư khả dụng và tỷ lệ phân bổ tiền trong các ví bằng số thập phân chính xác. |
| 2 | Ghi nhận thu chi | TXN | Ghi nhận các giao dịch thu nhập (`INCOME`) và chi phí (`EXPENSE`); liên kết với ví thanh toán và danh mục chi tiêu; cập nhật số dư ví cùng lúc với việc ghi nhận giao dịch. |
| 3 | Chuyển tiền nội bộ | TRF | Chuyển tiền giữa hai ví nội bộ theo cách ghi hai vế (trừ ví nguồn, cộng ví đích); giữ cho tổng số dư trong các ví luôn không đổi. |
| 4 | Danh mục phân loại | CAT | Giữ cấu trúc phân loại cây 3 cấp gồm Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết; bảo đảm liên kết hợp lệ tới nhóm cha khi gắn nhãn giao dịch. |
| 5 | Công nợ | DEBT | Theo dõi nợ phải thu (`RECEIVABLE`) và nợ phải trả (`PAYABLE`) theo từng người vay hoặc người cho vay; ghi nhận các giao dịch trả nợ, thu nợ từng phần và xóa nợ còn lại phi tiền mặt. |
| 6 | Sổ tiết kiệm | SAV | Quản lý hợp đồng tiền gửi tiết kiệm có kỳ hạn và không kỳ hạn; tính lãi dồn tích theo lãi suất năm chia 365 ngày; xử lý tất toán đúng hạn, trước hạn và tái tục vốn gốc. |
| 7 | Tài sản đầu tư | INV | Quản lý lệnh mua và lệnh bán tài sản đầu tư; ghi nhận sự kiện của tài sản đầu tư (nhận tiền mặt, thêm đơn vị cùng loại, hoàn vốn). Định giá tài sản đầu tư theo mô hình đơn giá nhân số lượng; tính lãi lỗ đã chốt và chưa chốt, tỷ suất sinh lời đơn, tỷ suất kép quy đổi năm và tỷ suất nội hoàn. |
| 8 | Quỹ mục tiêu | FND | Chia nguồn tiền từ số dư ví, sổ tiết kiệm và lệnh mua của tài sản đầu tư vào các quỹ mục tiêu do người dùng tự tạo; đo tỷ lệ hoàn thành mục tiêu bằng số. |
| 9 | Thống kê và báo cáo | REP | Trích xuất chuỗi dữ liệu 12 tháng liên tục kết thúc tại tháng người dùng chọn, mặc định là tháng hiện tại; phân tích tỷ lệ cơ cấu chi tiêu theo nhóm danh mục, dòng tiền thuần và tỷ lệ tiết kiệm định kỳ. |
| 10 | Lưu trữ và sao lưu dữ liệu | DAT | Lưu dữ liệu lâu dài trên tệp JSON cục bộ bằng cách ghi tệp an toàn, tệp chính không bị hỏng khi mất điện giữa chừng; hỗ trợ xuất và nhập tệp sao lưu độc lập để phục vụ kiểm tra lại dữ liệu. |

#### 1.2.2 Ranh giới ngoài hệ thống và căn cứ loại trừ

Để mô hình miền xác định rõ và kiểm soát độ phức tạp kiến trúc theo khung đánh giá đồ án Lập trình Hướng đối tượng, hệ thống đặt các ranh giới loại trừ kỹ thuật:

Bảng phân định ranh giới ngoài phạm vi:

| STT | Hạng mục ngoài phạm vi | Ranh giới phân định cụ thể | Căn cứ kỹ thuật và lý do loại trừ |
| :---: | :--- | :--- | :--- |
| 1 | Tích hợp Open Banking và cổng thanh toán trực tuyến | Hệ thống không tích hợp Open Banking API hoặc Webhook tự động cào dữ liệu giao dịch; người dùng trực tiếp nhập các giao dịch đã xảy ra. | Ứng dụng vận hành ngoại tuyến và độc lập trên máy trạm cục bộ, không lưu thông tin xác thực của bên thứ ba (tài khoản, khóa truy cập) hay phụ thuộc vào hạ tầng mạng ngoài. |
| 2 | Quản trị đa tài khoản và kiểm soát truy cập phân quyền | Hệ thống quy định thực thể Người dùng để quản lý thông tin cấu hình cá nhân và đơn vị tiền tệ cơ sở; loại trừ việc đăng ký nhiều tài khoản trực tuyến qua mạng hoặc phân cấp quyền hạn quản trị. | Phù hợp với kiến trúc ứng dụng máy tính để bàn đơn tiến trình; loại bỏ chi phí quản lý phiên đăng nhập và ngăn chặn nguy cơ xung đột ghi đồng thời. |
| 3 | Chuyển mạch thanh toán và tạo giao dịch tiền thực | Hệ thống không gửi lệnh chuyển tiền đến mạng lưới thanh toán liên ngân hàng. Dữ liệu dòng tiền chỉ phản ánh thông tin kế toán quản trị nội bộ sau khi giao dịch thực tế đã diễn ra ngoài đời thực. | Giới hạn hệ thống trong phạm vi sổ sách kế toán cá nhân, không xử lý thanh toán thật. |

### 1.3 Đối tượng sử dụng và hướng dẫn đọc tài liệu

Tài liệu quy định mục đích sử dụng và phạm vi cần đọc cho các nhóm đối tượng kỹ thuật:

| Nhóm đối tượng | Mục đích sử dụng tài liệu | Phạm vi tài liệu trọng tâm |
| --- | --- | --- |
| Kỹ sư phân tích và thiết kế | Xác định ranh giới hệ thống, chuyển hóa yêu cầu nghiệp vụ thành mô hình miền và kiến trúc phần mềm chi tiết | Mục 1, Mục 2, Mục 4, Mục 6, Mục 7 |
| Kỹ sư phát triển phần mềm | Cài đặt logic nghiệp vụ, cấu trúc dữ liệu và xử lý ngoại lệ trên nền tảng Java 17 | Mục 3, Mục 4, Mục 6 |
| Kỹ sư kiểm thử phần mềm | Xây dựng ca kiểm thử đơn vị và kiểm thử tích hợp dựa trên luồng sự kiện và điều kiện biên | Mục 3, Mục 6, Mục 8 |
| Cán bộ thẩm định kỹ thuật | So khớp xem yêu cầu đã đầy đủ chưa, kiểm tra mức độ đáp ứng yêu cầu chất lượng ở mục 5 | Toàn bộ tài liệu |

Trình tự đọc tài liệu:

1. Đọc Mục 1 và Mục 2 để nắm vững ranh giới hệ thống, 5 mục tiêu kỹ thuật (G-01 đến G-05) cùng 4 giới hạn kiến trúc (CON-01 đến CON-04).
2. Đọc Mục 3 để xác định 10 quy tắc kế toán (BR-FIN-01 đến BR-FIN-10), công thức làm tròn và các điều kiện luôn đúng của số dư.
3. Đọc đối chiếu Mục 4 (Yêu cầu chức năng) với Mục 6 (Ca sử dụng chi tiết), kiểm tra yêu cầu phi chức năng tại Mục 5 và tra cứu độ bao phủ kiểm chứng qua Ma trận truy vết yêu cầu tại Mục 8.

### 1.4 Tài liệu tham khảo chuẩn

Danh mục tài liệu trích dẫn của đặc tả được trình bày tại mục 10.

### 1.5 Thuật ngữ, từ viết tắt và định nghĩa

Bảng thuật ngữ định nghĩa thống nhất các khái niệm trong toàn bộ tài liệu:

| Thuật ngữ | Định nghĩa kỹ thuật trong hệ thống |
| --- | --- |
| Ví tiền | Tài khoản chứa tiền thực tế (tiền mặt, thẻ ngân hàng, ví điện tử) lưu giữ số dư tiền khả dụng. Hệ thống có sẵn một ví tiền mặt ban đầu, người dùng tự tạo thêm các ví khác. |
| Số dư khả dụng | Số tiền hiện có trong một ví, bằng số dư ban đầu cộng các dòng tiền vào và trừ các dòng tiền ra, và không được âm. |
| Giao dịch thu chi | Bản ghi biến động tài chính đơn lẻ làm tăng (`INCOME`) hoặc giảm (`EXPENSE`) số dư khả dụng của một ví xác định, có gắn nhãn phân loại danh mục. |
| Chuyển tiền nội bộ | Nghiệp vụ chuyển tiền giữa hai ví nội bộ theo cách ghi hai vế, trừ ví nguồn và cộng ví đích cùng lúc; không làm thay đổi tài sản ròng của hệ thống. |
| Cấu trúc danh mục | Mô hình phân loại dạng cây 3 cấp gồm Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết. |
| Công nợ | Quan hệ vay mượn giữa người dùng và người khác bên ngoài, phân loại thành nợ phải thu (`RECEIVABLE`) và nợ phải trả (`PAYABLE`). |
| Nợ phải thu | Khoản tiền người dùng đã cho người khác vay, tạo ra quyền đòi lại trong tương lai. |
| Nợ phải trả | Số tiền người dùng phải hoàn trả cho người cho vay do đã đi vay. |
| Xóa nợ | Thao tác ghi nhận khoản cho vay bị mất khi không thể đòi lại, hoặc ghi nhận giảm nợ phải trả khi được người cho vay tha nợ phi tiền mặt. |
| Sổ tiết kiệm | Khoản tiền gửi tích lũy có kỳ hạn hoặc không kỳ hạn tại tổ chức tín dụng, sinh lãi định kỳ. |
| Lãi dồn tích | Tiền lãi của sổ tiết kiệm tính đến một ngày, theo cách tính ngày lãi. |
| Cách tính ngày lãi | Lãi đơn của sổ tiết kiệm bằng vốn gốc nhân lãi suất năm nhân số ngày, chia cho 365. Số ngày là số ngày gửi thực tế khi tính lãi dồn tích và tất toán trước hạn, là số ngày kỳ hạn khi tính lãi cam kết. Một năm luôn là 365 ngày, kể cả năm nhuận. |
| Đáo hạn | Ngày kết thúc kỳ hạn gửi của sổ tiết kiệm, xác định bằng ngày mở sổ cộng số tháng kỳ hạn theo lịch. |
| Tất toán | Việc đóng sổ tiết kiệm và nhận số tiền tất toán về ví, đúng hạn (`SETTLED_MATURED`) hoặc trước hạn (`SETTLED_EARLY`, lãi tính lại theo số ngày thực gửi). |
| Hoàn nguyên | Đưa số dư và dữ liệu về đúng trạng thái trước khi thao tác xảy ra; dùng khi hủy một giao dịch hoặc khi thao tác nhiều bước bị lỗi. |
| Tái tục | Đóng một sổ tiết kiệm đã tất toán đúng hạn rồi mở sổ mới với kỳ hạn, lãi suất và phương thức lãi do người dùng chỉ định lại; lấy một phần hoặc toàn bộ giá trị tất toán làm vốn gốc mới. |
| Chứng chỉ quỹ | Một loại tài sản đầu tư, đại diện cho quyền sở hữu một phần tài sản ròng của quỹ đầu tư, quản lý theo số đơn vị và đơn giá. |
| Tài sản đầu tư | Thứ cụ thể người dùng sở hữu qua một hoặc nhiều lệnh mua và được định giá riêng, mang một nhãn loại tài sản như chứng chỉ quỹ, vàng hoặc bất động sản. |
| Nhận tiền mặt | Dạng sự kiện của tài sản đầu tư ghi nhận khoản tiền thực nhận từ cổ tức, tiền lãi hoặc tiền cho thuê của tài sản đầu tư đã quản lý. Khoản tiền cộng vào ví nhận tiền, tính vào thu nhập tháng và phân bổ cho các lệnh mua theo BR-FIN-07. |
| Quỹ mục tiêu | Nhóm tài sản riêng được dành ra từ tiền mặt, sổ tiết kiệm và lệnh mua của tài sản đầu tư để phục vụ một mục tiêu tài chính cụ thể. |
| Ghi hai vế | Cách ghi mỗi lần chuyển tiền bằng hai khoản ghi cùng số tiền: một khoản trừ ví nguồn, một khoản cộng ví đích. |
| Khoản ghi | Một dòng ghi số tiền tăng hoặc giảm của một ví, như một vế của lệnh chuyển tiền hoặc một lần điều chỉnh số dư ví. |
| Tài sản ròng | Tổng tài sản trừ tổng nợ của người dùng tại một thời điểm. |
| Làm tròn ngân hàng | Cách làm tròn về số chẵn gần nhất khi giá trị nằm đúng giữa hai mức. |
| Ghi tệp an toàn | Cách ghi dữ liệu xuống tệp sao cho tệp chính thức chỉ được thay bằng nội dung mới khi nội dung đó đã ghi đầy đủ; nếu quá trình bị ngắt giữa chừng, tệp cũ vẫn giữ nguyên vẹn. |
| Người dùng duy nhất | Mô hình vận hành của hệ thống, trong đó toàn bộ dữ liệu thuộc quyền kiểm soát của một người dùng duy nhất trên ứng dụng máy tính để bàn cục bộ. |
| Người dùng | Tác nhân duy nhất của hệ thống, quản lý thông tin cấu hình cá nhân và đơn vị tiền tệ cơ sở. |

Bảng tổng hợp các từ viết tắt sử dụng trong tài liệu:

| Viết tắt | Tên đầy đủ | Ý nghĩa |
| --- | --- | --- |
| CAGR | Tỷ suất sinh lời kép quy đổi năm | Chỉ số quy đổi suất sinh lời về mốc một năm, chỉ tính khi kỳ nắm giữ đạt tối thiểu 365 ngày theo `BR-FIN-08`. |
| JDK | Java Development Kit | Bộ công cụ biên dịch và phát triển ứng dụng Java. |
| JRE | Java Runtime Environment | Môi trường chạy mã bytecode của ứng dụng trên máy trạm. |
| JVM | Java Virtual Machine | Máy ảo Java chạy ứng dụng cục bộ. |
| LTS | Long-Term Support | Nhánh phiên bản Java được nhà cung cấp hỗ trợ dài hạn. |
| PFMS | Personal Finance Management System | Tên hệ thống trong phạm vi tài liệu này. |
| RTM | Requirements Traceability Matrix | Ma trận kiểm chứng và truy vết yêu cầu tại Mục 8. |
| UC | Use case | Ca sử dụng đặc tả tại Mục 6 và mô hình hóa tại Mục 7. |

### 1.6 Quy ước mã định danh

Hệ thống dùng cấu trúc mã phân cấp để nhận diện duy nhất và truy vết hai chiều mọi thực thể yêu cầu.

Mã theo hai cấu trúc:
- Cấu trúc toàn hệ thống: `<TIỀN_TỐ>-NN`
- Cấu trúc nhóm chức năng nghiệp vụ: `<TIỀN_TỐ>-<DOMAIN>-NN[.x]`

Trong đó, `NN` là số thứ tự gồm 2 chữ số (bắt đầu từ `01`), `x` là số thứ tự tiêu chí kiểm chứng (bắt đầu từ `1`), và `DOMAIN` là mã nhóm chức năng nghiệp vụ.

Bảng quy ước tiền tố mã:

| Tiền tố | Thực thể định danh | Cấu trúc mở rộng | Ví dụ | Phạm vi áp dụng |
|---|---|---|---|---|
| G-NN | Mục tiêu kỹ thuật hệ thống | NN từ 01 đến 05 | G-01 | Bảng mục tiêu hệ thống tại Mục 2.2 |
| CON-NN | Giới hạn kỹ thuật bắt buộc | NN tăng dần từ 01 | CON-01 | Danh mục giới hạn tại Mục 2.4 |
| AS-NN | Giả định vận hành hệ thống | NN tăng dần từ 01 | AS-01 | Danh mục giả định tại Mục 2.4 |
| DE-NN | Sự phụ thuộc hệ thống | NN tăng dần từ 01 | DE-01 | Danh mục phụ thuộc tại Mục 2.4 |
| BR-FIN-NN | Quy tắc nghiệp vụ tài chính | Khối FIN cố định, NN tăng dần từ 01 | BR-FIN-01 | Các quy tắc tài chính luôn đúng tại Mục 3 |
| FR-DOMAIN | Mã nhóm chức năng | Khối DOMAIN cố định theo bảng nhóm chức năng | FR-WAL | Định danh nhóm chức năng tại Mục 1.2 và Mục 4 |
| FR-DOMAIN-NN | Yêu cầu chức năng | NN đánh số độc lập theo từng DOMAIN | FR-WAL-01 | Danh mục 43 yêu cầu chức năng tại Mục 4 |
| UC-DOMAIN-NN | Ca sử dụng | NN đánh số độc lập theo từng DOMAIN | UC-WAL-01 | Danh mục 44 ca sử dụng tại Mục 6 |
| NFR-NN | Yêu cầu phi chức năng | NN tăng dần từ 01 | NFR-01 | Danh mục 6 yêu cầu chất lượng tại Mục 5 |
| TC-DOMAIN-NN.x | Tiêu chí nghiệm thu | Lặp lại mã FR tương ứng, x tăng dần từ 1 | TC-WAL-01.1 | Tiêu chí kiểm chứng của từng FR tại Mục 4 |

Danh sách giá trị của biến `DOMAIN` là tập hợp khép kín gồm 10 nhóm chức năng nghiệp vụ, tương ứng trực tiếp với cấu trúc phân rã chức năng và mô hình miền:

| Mã DOMAIN | Nhóm chức năng nghiệp vụ | Tên tiếng Anh | Thực thể miền chính | Số lượng FR | Số lượng UC |
|---|---|---|---|---|---|
| WAL | Ví tiền | Wallet | Ví tiền | 5 | 4 |
| TXN | Ghi nhận thu chi | Transaction | Giao dịch thu chi | 5 | 4 |
| TRF | Chuyển tiền nội bộ | Transfer | Lệnh chuyển tiền | 3 | 3 |
| CAT | Danh mục phân loại | Category | Danh mục phân loại | 3 | 4 |
| DEBT | Công nợ | Debt Management | Khoản công nợ | 5 | 3 |
| SAV | Sổ tiết kiệm | Saving Account | Sổ tiết kiệm | 5 | 6 |
| INV | Tài sản đầu tư | Investment Asset | Tài sản đầu tư | 6 | 8 |
| FND | Quỹ mục tiêu | Financial Goal | Quỹ mục tiêu | 4 | 5 |
| REP | Thống kê và báo cáo | Statistics & Report | Thống kê dòng tiền | 4 | 4 |
| DAT | Lưu trữ và sao lưu dữ liệu | Data Management | Dữ liệu tài chính | 3 | 3 |
| **Tổng cộng** | **10 nhóm chức năng** | | | **43 FR** | **44 UC** |

### 1.7 Quy ước mức độ bắt buộc của yêu cầu

Các yêu cầu chức năng, yêu cầu phi chức năng và quy tắc nghiệp vụ trong tài liệu sử dụng các từ khóa quy ước để xác định mức độ bắt buộc kỹ thuật:

| Từ khóa quy ước | Mức độ bắt buộc | Ý nghĩa kỹ thuật trong tài liệu |
| :--- | :--- | :--- |
| **Bắt buộc** / **Phải** | Bắt buộc | Năng lực hoặc điều kiện hệ thống phải thỏa mãn để được nghiệm thu. |
| **Cấm** / **Không được** | Cấm hoàn toàn | Hành vi hoặc trạng thái hệ thống hoàn toàn không được phép xảy ra hoặc phải từ chối tiếp nhận. |
| **Khuyến nghị** / **Nên** | Khuyến cáo | Quy cách được ưu tiên áp dụng, chỉ bỏ qua khi có lý do kỹ thuật xác đáng. |
| **Cho phép** / **Tùy chọn** | Tùy chọn | Năng lực hoặc lựa chọn linh hoạt theo nhu cầu của người dùng. |

---

