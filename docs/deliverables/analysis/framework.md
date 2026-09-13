# Khung nội dung tài liệu phân tích hướng đối tượng (OOA)

Tài liệu này hướng dẫn cấu trúc và cách viết báo cáo Phân tích Hướng đối tượng (OOA, Milestone 2) cho đồ án Quản lý Tài chính Cá nhân (PFMS).

Tài liệu này chỉ phục vụ pha phân tích nghiệp vụ, không phải thiết kế phần mềm (OOD). Toàn bộ sơ đồ UML chi tiết như sơ đồ lớp với các kiểu dữ liệu lập trình, sơ đồ tuần tự các lời gọi hàm, hay kiến trúc phân tầng sẽ được làm riêng ở tài liệu Milestone 3. Tại đây, nhóm chỉ tập trung vào việc nhận diện đúng các đối tượng thực tế trong bài toán tài chính và làm rõ trách nhiệm của từng đối tượng.

---

## Mục lục báo cáo OOA

Khi viết báo cáo OOA, các thành viên tổ chức nội dung theo 9 mục sau:

1. [Mục tiêu và phạm vi phân tích](#1-mục-tiêu-và-phạm-vi-phân-tích)
2. [Cách trích xuất đối tượng từ ca sử dụng](#2-cách-trích-xuất-đối-tượng-từ-ca-sử-dụng)
3. [Từ điển khái niệm miền](#3-từ-điển-khái-niệm-miền)
4. [Mối quan hệ nghiệp vụ giữa các đối tượng](#4-mối-quan-hệ-nghiệp-vụ-giữa-các-đối-tượng)
5. [Phân bổ trách nhiệm bằng thẻ CRC](#5-phân-bổ-trách-nhiệm-bằng-thẻ-crc)
6. [Quy tắc nghiệp vụ và ràng buộc dữ liệu](#6-quy-tắc-nghiệp-vụ-và-ràng-buộc-dữ-liệu)
7. [Vòng đời nghiệp vụ của các đối tượng chính](#7-vòng-đời-nghiệp-vụ-của-các-đối-tượng-chính)
8. [Bảng đối chiếu ca sử dụng với đối tượng](#8-bảng-đối-chiếu-ca-sử-dụng-với-đối-tượng)
9. [Bảng tự kiểm tra chất lượng phân tích](#9-bảng-tự-kiểm-tra-chất-lượng-phân-tích)

---

## 1. Mục tiêu và phạm vi phân tích

### Mục đích cần đạt
Làm rõ bài toán tài chính cá nhân cần quản lý những thực thể nào trong đời thực, thông tin của chúng là gì và chúng tương tác với nhau ra sao.

### Hướng dẫn cách viết
Nêu ngắn gọn phạm vi phân tích dựa trên tài liệu yêu cầu [req-uc.md](../requirements/req-uc.md):
* Nhóm nghiệp vụ cốt lõi làm trước: Quản lý ví tiền, ghi nhận thu chi, chuyển tiền nội bộ.
* Nhóm nghiệp vụ mở rộng làm sau: Theo dõi công nợ, sổ tiết kiệm, chứng chỉ quỹ đầu tư, quỹ mục tiêu.

Nhấn mạnh rõ ranh giới phân tích: Không đưa vào tài liệu này các khái niệm về công nghệ, giao diện màn hình (nút bấm, form nhập liệu JavaFX) hay lưu trữ dữ liệu (bảng cơ sở dữ liệu, câu lệnh SQL, file lưu trữ). Những phần đó thuộc về pha thiết kế và cài đặt tiếp theo.

### Quy tắc ranh giới khi phân công làm việc nhóm
Khi một thành viên được phân công phụ trách một nhóm đối tượng cụ thể (ví dụ nhóm cốt lõi: Wallet, Transaction, Transfer, Category):
* Giữ đúng phạm vi ca sử dụng: Chỉ phân tích các ca sử dụng được phân công (ví dụ từ UC01 đến UC05), không tự ý phân tích các ca sử dụng thuộc nhóm khác.
* Xử lý điểm chạm liên module: Nếu đối tượng của mình có liên quan đến tiền bạc của module khác (ví dụ số dư ví thay đổi khi trả nợ hoặc gửi tiết kiệm), chỉ mô tả điểm chạm ở mức khái quát (ví dụ ví tiền nhận biến động số dư qua một giao dịch chung). Tuyệt đối không tự ý đưa các thuộc tính riêng của module khác (như mã khoản nợ, kỳ hạn sổ tiết kiệm, mã chứng chỉ quỹ) vào các lớp ví tiền hay giao dịch thu chi của mình.
* Tính độc lập của module: Mỗi thành viên giữ cho các lớp trong phần của mình sạch sẽ, đúng trách nhiệm tự thân. Việc tích hợp chi tiết sẽ do thành viên phụ trách module phụ thuộc thực hiện sau.

---

## 2. Cách trích xuất đối tượng từ ca sử dụng

### Mục đích cần đạt
Ghi lại cách nhóm tìm ra các đối tượng từ văn bản mô tả 10 ca sử dụng, tránh việc tự nghĩ ra các lớp mà không có căn cứ từ yêu cầu thực tế.

### Hướng dẫn cách viết
Đọc lại các kịch bản trong mục 6 của tài liệu [req-uc.md](../requirements/req-uc.md). Với mỗi ca sử dụng, lọc ra các danh từ và phân loại theo 3 nhóm:
* Đối tượng nghiệp vụ: Những thứ có dữ liệu riêng và hành vi riêng cần đưa vào mô hình (ví dụ: ví tiền, giao dịch, khoản nợ).
* Thuộc tính của đối tượng: Những danh từ thực chất chỉ là giá trị đo lường hoặc mô tả cho đối tượng khác (ví dụ: số tiền, ngày giao dịch, lãi suất).
* Yếu tố ngoài phạm vi OOA: Các danh từ chỉ thao tác người dùng, nút bấm, hoặc giao diện (ví dụ: nút lưu, màn hình chính).

### Mẫu bảng lọc danh từ

| Từ ngữ trong ca sử dụng | Xuất hiện ở ca sử dụng | Kết luận | Lý do |
|---|---|---|---|
| Ví tiền | UC01, UC04 | Đối tượng | Nơi giữ tiền, có tên và số dư thay đổi qua các giao dịch |
| Số tiền | UC01, UC04 | Thuộc tính | Giá trị đo lường của giao dịch, thuộc về đối tượng giao dịch |
| Nút thêm mới | UC01 | Loại bỏ | Chi tiết giao diện người dùng, không thuộc nghiệp vụ |

---

## 3. Từ điển khái niệm miền

### Mục đích cần đạt
Định nghĩa rõ ràng từng đối tượng nghiệp vụ được giữ lại sau bước lọc, giúp cả nhóm và giảng viên hiểu thống nhất một nghĩa.

### Hướng dẫn cách viết
Với mỗi đối tượng, viết một đoạn ngắn từ 3 đến 5 dòng gồm:
* Tên gọi tiếng Việt và thuật ngữ tiếng Anh tương ứng.
* Ý nghĩa đời thực: Đối tượng này đại diện cho cái gì trong đời sống tài chính cá nhân?
* Dữ liệu đối tượng cần giữ: Liệt kê các thông tin bản chất mà đối tượng cần lưu giữ. Ghi chú rõ các thông tin liên quan đến tiền bạc đều tính bằng số thập phân chính xác (BigDecimal).
* Đối tượng thuộc nhóm Entity (có định danh riêng để phân biệt qua thời gian) hay Value Object (chỉ mang giá trị đo lường, không cần định danh).
* Ranh giới dữ liệu: Chỉ ghi nhận các dữ liệu nội tại của đối tượng trong phạm vi được giao, không thêm các trường dữ liệu mang tính dự phòng cho module khác.

### Mẫu bảng mô tả khái niệm

| Thuật ngữ | Tên tiếng Việt | Bản chất đời thực | Thông tin đối tượng nắm giữ |
|---|---|---|---|
| Wallet | Ví tiền / Tài khoản | Nơi cất giữ tiền mặt hoặc tài khoản ngân hàng | Tên ví, loại ví, số dư ban đầu, số dư hiện tại |
| Transaction | Giao dịch thu chi | Sự kiện tiền đi vào hoặc đi ra khỏi một ví cụ thể | Ngày ghi nhận, số tiền, loại thu hoặc chi, ghi chú |
| Category | Danh mục phân loại | Nhãn để gom nhóm các khoản thu chi phục vụ thống kê | Tên danh mục, nhóm danh mục, loại thu hoặc chi |

---

## 4. Mối quan hệ nghiệp vụ giữa các đối tượng

### Mục đích cần đạt
Mô tả cách các đối tượng gắn kết với nhau trong thực tế mà không cần dùng đến các ký hiệu UML phức tạp.

### Hướng dẫn cách viết
Trình bày mối quan hệ bằng lời văn rõ ràng hoặc bảng tổng hợp. Với mỗi cặp đối tượng có liên hệ, chỉ rõ:
* Bản chất quan hệ: Đối tượng này chứa, sở hữu, hay tham chiếu đến đối tượng kia?
* Số lượng tham gia: Một ví chứa nhiều giao dịch hay một giao dịch? Một giao dịch thuộc về bao nhiêu ví?
* Tính phụ thuộc vòng đời: Nếu xóa một ví thì các giao dịch trong ví đó có bị xóa theo không, hay phải giữ lại để đối soát?
* Điểm chạm liên module: Nếu có mối quan hệ với module khác (ví dụ trả nợ làm giảm số dư ví), chỉ ghi nhận điểm chạm bằng lời văn tổng quát rằng đối tượng nhận tác động qua giao dịch tiền tệ chung, không đưa các lớp ngoài phạm vi vào bảng quan hệ nội bộ này.

Nếu muốn trực quan hóa, nhóm chỉ cần vẽ một sơ đồ khối khái niệm đơn giản. Mỗi đối tượng là một ô chữ nhật có tên, nối với nhau bằng đường thẳng ghi rõ quan hệ (ví dụ: Ví tiền "chứa" Giao dịch, Giao dịch "thuộc" Danh mục). Chưa cần vẽ thuộc tính, kiểu dữ liệu, hay mũi tên chỉ thị kỹ thuật của UML.

### Mẫu bảng quan hệ nghiệp vụ

| Đối tượng A | Quan hệ | Đối tượng B | Số lượng (Bản số) | Ý nghĩa nghiệp vụ |
|---|---|---|---|---|
| Wallet | chứa | Transaction | 1 Wallet có 0 hoặc nhiều Transaction | Mỗi giao dịch phát sinh bắt buộc phải gắn với một ví cụ thể |
| Transaction | thuộc | Category | Mỗi Transaction có đúng 1 Category | Giao dịch phải được phân loại để phục vụ báo cáo chi tiêu |
| Wallet (nguồn) | chuyển tiền sang | Wallet (đích) | 1 giao dịch chuyển tiền gắn với đúng 2 ví | Chuyển khoản nội bộ giữa hai tài khoản của cùng một người |

---

## 5. Phân bổ trách nhiệm bằng thẻ CRC

### Mục đích cần đạt
Xác định mỗi đối tượng sẽ tự quản lý những gì và phối hợp với ai để thực hiện công việc. Đây là nội dung trọng tâm của báo cáo Milestone 2 theo yêu cầu môn học.

### Hướng dẫn cách viết
Sử dụng phương pháp thẻ CRC (Class, Responsibility, Collaborator). Mỗi đối tượng có một thẻ nhỏ chia làm hai cột:
* Cột trách nhiệm: Gồm việc đối tượng biết (Knowing: biết số dư của mình, biết ngày tạo) và việc đối tượng tự làm (Doing: tự kiểm tra dữ liệu hợp lệ, tự tăng giảm số dư khi có giao dịch mới).
* Cột cộng tác viên: Tên các đối tượng khác mà nó cần hỏi thông tin hoặc phối hợp cùng.

### Mẫu thẻ CRC

| Lớp: Wallet | |
|---|---|
| **Trách nhiệm (Responsibilities)** | **Cộng tác viên (Collaborators)** |
| Biết tên ví và số dư hiện tại | |
| Nhận giao dịch mới và tự điều chỉnh số dư của mình | Transaction |
| Kiểm tra số dư có đủ để thực hiện chi tiêu hoặc chuyển tiền hay không | |

| Lớp: Transaction | |
|---|---|
| **Trách nhiệm (Responsibilities)** | **Cộng tác viên (Collaborators)** |
| Biết ngày, số tiền, loại giao dịch và ghi chú | |
| Xác thực số tiền giao dịch phải lớn hơn 0 | |
| Gắn kết với danh mục chi tiêu tương ứng | Category |

---

## 6. Quy tắc nghiệp vụ và ràng buộc dữ liệu

### Mục đích cần đạt
Quy định rõ các ràng buộc logic của hệ thống tài chính để bước thiết kế và cài đặt sau này không làm sai lệch số liệu.

### Hướng dẫn cách viết
Tổng hợp các luật tính toán từ tài liệu [finance-mng-description.md](../../domain/finance-mng-description.md) thành danh sách quy tắc rõ ràng:
* Quy tắc tính số dư: Số dư ví = Số dư ban đầu + Tổng thu - Tổng chi + Tiền chuyển đến - Tiền chuyển đi.
* Ràng buộc chi tiêu: Ví tiền mặt hoặc ví ngân hàng thông thường không được phép có số dư âm, trừ trường hợp thấu chi có thỏa thuận trước.
* Ràng buộc phân loại: Thu nhập chỉ được chọn các danh mục thuộc loại thu, chi tiêu chỉ được chọn các danh mục thuộc loại chi.
* Quy ước độ chính xác: Toàn bộ phép tính số tiền và lãi suất đều dùng số thập phân chính xác, không dùng số thực dấu chấm động để tránh sai số làm tròn.

### Mẫu bảng quy tắc nghiệp vụ

| Mã quy tắc | Tên quy tắc | Đối tượng áp dụng | Nội dung quy tắc |
|---|---|---|---|
| BR01 | Số tiền dương | Transaction | Số tiền của mỗi giao dịch bắt buộc phải lớn hơn 0 |
| BR02 | Cân bằng chuyển khoản | Transfer | Số tiền trừ ở ví nguồn phải bằng chính xác số tiền cộng vào ví đích |
| BR03 | Toàn vẹn danh mục | Category | Không được xóa danh mục nếu đang có giao dịch tham chiếu tới danh mục đó |

---

## 7. Vòng đời nghiệp vụ của các đối tượng chính

### Mục đích cần đạt
Mô tả các trạng thái thay đổi theo thời gian của các thực thể phức tạp trong hệ thống.

### Hướng dẫn cách viết
Trong đồ án này, hầu hết giao dịch thu chi chỉ phát sinh một lần rồi lưu lại. Tuy nhiên, có hai đối tượng có vòng đời kéo dài cần được mô tả kỹ:
* Khoản nợ (Debt): Trải qua các giai đoạn từ khi tạo khoản nợ (mới vay hoặc cho vay), trả dần từng phần, đến khi trả hết (tất toán) hoặc bị xóa nợ (không đòi được).
* Sổ tiết kiệm (Saving): Trải qua các giai đoạn từ khi mở sổ, đang gửi trong kỳ hạn, đến ngày đáo hạn, rút trước hạn hoặc tái tục sang kỳ tiếp theo.

Quy định về việc ghi Không áp dụng (N/A):
Nếu thành viên được phân công phụ trách module mà các đối tượng bên trong là sự kiện bất biến hoặc dữ liệu danh mục tĩnh (ví dụ: giao dịch thu chi một khi đã ghi nhận là cố định, danh mục phân loại không có vòng đời trạng thái), thành viên chỉ cần ghi rõ: "Không áp dụng (N/A) do các đối tượng trong module này là sự kiện bất biến, không có chuyển đổi trạng thái". Tuyệt đối không tự ý phân tích hộ phần nợ hoặc tiết kiệm của thành viên khác để lấp đầy mục này.

Mô tả vòng đời bằng bảng các trạng thái và điều kiện chuyển đổi, không cần vẽ sơ đồ máy trạng thái UML phức tạp.

### Mẫu bảng vòng đời trạng thái

| Trạng thái hiện tại | Sự kiện kích hoạt | Điều kiện chuyển | Trạng thái tiếp theo | Ý nghĩa nghiệp vụ |
|---|---|---|---|---|
| Mới phát sinh | Người vay trả một phần | Số tiền trả nhỏ hơn tổng nợ | Đang trả dần | Khoản nợ được giảm trừ nhưng chưa hết |
| Đang trả dần | Người vay trả hết phần còn lại | Số tiền trả bằng đúng dư nợ | Đã tất toán | Khoản nợ kết thúc, không còn nghĩa vụ nợ |
| Đang gửi | Đến ngày kết thúc kỳ hạn | Đến đúng ngày đáo hạn | Đã đáo hạn | Sẵn sàng để rút gốc và lãi hoặc tái tục |

---

## 8. Bảng đối chiếu ca sử dụng với đối tượng

### Mục đích cần đạt
Kiểm tra chéo để bảo đảm mọi ca sử dụng đều có đối tượng xử lý và không có đối tượng nào bị thừa thãi.

### Hướng dẫn cách viết
Lập bảng liệt kê 10 ca sử dụng từ [req-uc.md](../requirements/req-uc.md) ở cột bên trái, cột bên phải là các đối tượng tham gia xử lý ca sử dụng đó.

### Mẫu bảng đối chiếu

| Mã Use Case | Tên ca sử dụng | Các đối tượng nghiệp vụ tham gia |
|---|---|---|
| UC01 | Ghi nhận giao dịch thu chi | Wallet, Transaction, Category |
| UC02 | Xem lịch sử và lọc giao dịch | Wallet, Transaction, Category |
| UC03 | Chuyển tiền nội bộ giữa các ví | Wallet, Transfer |
| UC04 | Quản lý danh sách ví | Wallet |
| UC05 | Quản lý danh mục thu chi | Category |

---

## 9. Bảng tự kiểm tra chất lượng phân tích

### Mục đích cần đạt
Giúp nhóm tự rà soát lại toàn bộ báo cáo OOA trước khi nộp bài cho giảng viên, bảo đảm đúng trọng tâm phân tích hướng đối tượng của Milestone 2.

### Bảng kiểm tra tự đánh giá

| Câu hỏi tự kiểm tra | Kết quả mong đợi | Đánh giá |
|---|---|---|
| Báo cáo có bị lẫn các lớp kỹ thuật như Controller, Service, Repository, DAO không? | Không có, các lớp này thuộc về OOD ở Milestone 3 | Đạt / Chưa |
| Báo cáo có bị lẫn các chi tiết giao diện như cửa sổ, bảng chọn, nút bấm không? | Không có, giao diện thuộc về pha thiết kế | Đạt / Chưa |
| Tên các đối tượng có xuất phát từ ngữ cảnh đời thực của bài toán tài chính không? | Có, bám sát các khái niệm trong file Excel và Use Case | Đạt / Chưa |
| Các trách nhiệm trong thẻ CRC có rõ ràng việc đối tượng tự làm và việc nó biết không? | Có, phân định rành mạch giữa Knowing và Doing | Đạt / Chưa |
| Các phép tính tiền bạc có ghi chú dùng số thập phân chính xác không? | Có, toàn bộ quy ước dùng BigDecimal | Đạt / Chưa |
| Phạm vi dữ liệu có bị lấn sân sang các module khác không? | Không có, các đối tượng chỉ giữ thông tin tự thân, điểm chạm liên module được giữ ở mức khái quát | Đạt / Chưa |
| Có bị lạm dụng các sơ đồ UML chi tiết của pha thiết kế không? | Không, các sơ đồ UML chi tiết dành cho Milestone 3 | Đạt / Chưa |

---

## Bước tiếp theo sau OOA

Sau khi hoàn thiện báo cáo OOA theo khung trên và được thông qua ở Milestone 2, nhóm sẽ mang danh sách các đối tượng và trách nhiệm này sang Milestone 3 (UML Design & OOD). Ở Milestone 3, nhóm mới tiến hành vẽ sơ đồ lớp chi tiết, chọn các mẫu thiết kế hướng đối tượng (như Factory, Strategy, Observer) và phân chia các tầng mã nguồn cụ thể.
