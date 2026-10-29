## 5. YÊU CẦU PHI CHỨC NĂNG

Các yêu cầu phi chức năng (`NFR-01` đến `NFR-06`) của Hệ thống quản lý tài chính cá nhân xác lập các tiêu chuẩn chất lượng theo mô hình ISO/IEC 25010:2023 và tiêu chuẩn kỹ nghệ yêu cầu ISO/IEC/IEEE 29148:2018. Các yêu cầu này là ràng buộc vận hành và kiến trúc then chốt, bảo đảm hệ thống chính xác về tài chính, vận hành hiệu quả, lưu trữ tin cậy, dễ sử dụng, tương thích môi trường và dễ bảo trì theo nguyên lý hướng đối tượng.

Mỗi yêu cầu phi chức năng được đặc tả theo khuôn mẫu 4 khối:

1. Đặc tính chất lượng: đặc tính và đặc tính con theo ISO/IEC 25010:2023.
2. Mô tả yêu cầu: phạm vi, căn cứ kỹ thuật và giải pháp kiến trúc ở mức bài toán, không đi vào mã nguồn cài đặt.
3. Chỉ số đo và ngưỡng nghiệm thu: thuộc tính đo kiểm, chỉ số đo và ngưỡng nghiệm thu rõ ràng.
4. Phương pháp kiểm chứng: theo ISO 29148, gồm kiểm thử tự động (`T`), trình diễn thao tác (`D`), soát xét kỹ thuật (`I`) và phân tích số liệu (`A`).

Bảng tổng hợp 6 yêu cầu phi chức năng:

| Mã yêu cầu | Tên yêu cầu | Đặc tính chất lượng ISO 25010 | Phương pháp chính | Chỉ số trọng tâm |
| :---: | :--- | :--- | :---: | :--- |
| `NFR-01` | Độ chính xác và toàn vẹn dữ liệu tài chính | Mức độ phù hợp chức năng / Tính chính xác chức năng | T, I | Sai số tích lũy 0.00 VNĐ; 0% trường dùng số thực nhị phân (float/double); 0% vi phạm quy tắc bảo toàn số dư. |
| `NFR-02` | Hiệu năng và độ trễ phản hồi | Hiệu quả vận hành / Hành vi thời gian, Mức sử dụng tài nguyên | D, A | Phản hồi tức thì với dữ liệu cá nhân thông thường; không rò rỉ bộ nhớ. |
| `NFR-03` | Độ tin cậy và lưu trữ an toàn | Độ tin cậy / Khả năng chịu lỗi, Khả năng phục hồi | T, I | Ghi tệp an toàn; tự động sao lưu trước tác vụ thay thế dữ liệu quy mô lớn; không mất dữ liệu khi ngắt nguồn. |
| `NFR-04` | Khả năng bảo trì và kiến trúc hướng đối tượng | Khả năng bảo trì / Tính module hóa, Tính tái sử dụng, Tính phân tích được, Tính kiểm thử được | T, I | Không phụ thuộc vòng; trạng thái nội bộ được bảo vệ; mọi quy tắc nghiệp vụ có kiểm thử đơn vị. |
| `NFR-05` | Tính khả dụng và trải nghiệm người dùng | Khả năng tương tác / Tính dễ học, Tính dễ vận hành, Bảo vệ người dùng khỏi lỗi | D, I | Xác nhận thao tác rủi ro; 100% thông báo lỗi có hướng khắc phục. |
| `NFR-06` | Tính tương thích và môi trường chạy | Tính linh hoạt / Tính thích ứng, Tính dễ cài đặt | T, I | Chạy trên JVM 17 LTS, độc lập hệ điều hành; UTF-8 không BOM; không dùng thư viện nhị phân phụ thuộc nền tảng. |

### 5.1 NFR-01: Độ chính xác và toàn vẹn dữ liệu tài chính

Đặc tính chất lượng: Mức độ phù hợp chức năng, đặc tính con Tính chính xác chức năng.

Mô tả yêu cầu:

1. Kiểu dữ liệu số học:
   - Dùng kiểu số thập phân chính xác cố định cho mọi trường số, biến trung gian, phép tính tiền tệ, số dư ví và tổng số dư, với tối thiểu 18 chữ số phần nguyên và 2 chữ số thập phân.
   - Cấm kiểu số thực nhị phân (float/double) trong cấu trúc dữ liệu nghiệp vụ, thuật toán tính toán và lưu trữ. Giá trị số chỉ được tạo từ dữ liệu nhập dạng thập phân chính xác hoặc từ số nguyên, không tạo trực tiếp từ float/double.
2. Độ chính xác thập phân và làm tròn:
   - Tiền VNĐ hiển thị 0 chữ số thập phân (số nguyên đồng) trên giao diện; tính toán, xử lý nghiệp vụ và kiểm thử dùng 2 chữ số thập phân.
   - Tỷ lệ phần trăm trung gian dùng tối thiểu 4 chữ số thập phân (`BR-FIN-01`).
   - Mọi phép chia phải chỉ định rõ số chữ số thập phân và chế độ làm tròn ngân hàng (làm tròn đến số chẵn gần nhất khi giá trị nằm giữa hai mức).
   - So sánh số theo giá trị thực, bỏ qua khác biệt do cách hiển thị số chữ số thập phân.
3. Sai số tích lũy: sai số của mọi chuỗi phép tính, trong từng giao dịch và trên toàn bộ dữ liệu tổng hợp, bằng 0.00 VNĐ.
4. Bảo toàn số dư (`BR-FIN-02`): tại mọi thời điểm, số dư khả dụng của ví w tại thời điểm quan sát T thỏa mãn:

   $$\text{Số dư ví } w \text{ tại } T = (\text{Thu} + \text{Chuyển vào} + \text{Điều chỉnh tăng}) - (\text{Chi} + \text{Chuyển ra} + \text{Điều chỉnh giảm})$$

   Mỗi hạng tử là tổng các khoản của ví w đến thời điểm T. Điều chỉnh tăng và điều chỉnh giảm lần lượt là các khoản điều chỉnh số dư dương và âm theo `FR-WAL-05`.
5. Cân bằng nguồn - đích (`BR-FIN-03`): mọi lần chuyển tiền nội bộ phải có số tiền trừ ở ví nguồn bằng số tiền cộng ở ví đích (chưa tính phí). Tổng tiền trong hệ thống giữ nguyên, trừ chi phí chuyển tiền.

Chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Sai số tích lũy số học | Chênh lệch tuyệt đối giữa tổng tiền kỳ vọng và thực tế | 0.00 VNĐ |
| Kiểu số thực nhị phân trong mô hình nghiệp vụ | Số trường dùng float/double chia tổng số trường số | 0.00% |
| Vi phạm quy tắc bảo toàn số dư (`BR-FIN-02`) | Số giao dịch vi phạm chia tổng số giao dịch | 0.00% trên toàn bộ tập kiểm thử |
| Sai lệch nguồn - đích khi chuyển tiền (`BR-FIN-03`, chưa tính phí) | Chênh lệch tuyệt đối giữa tiền trừ ví nguồn và tiền cộng ví đích | 0.00 VNĐ |
| Số chữ số thập phân của tiền VNĐ | Số chữ số thập phân khi hiển thị, khi tính toán và kiểm thử | 0 (giao diện) / 2 (tính toán, kiểm thử) |
| Số chữ số thập phân của tỷ lệ phần trăm | Số chữ số thập phân trong phép tính tỷ lệ | Từ 4 trở lên |
| Phép chia có chỉ định số chữ số thập phân và làm tròn | Số phép chia thập phân chính xác đạt yêu cầu chia tổng số phép chia | 100.00% |

Phương pháp kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Soát xét kỹ thuật (`I`) | Đánh giá kiểm tra | Rà soát mã nguồn phần nghiệp vụ để phát hiện khai báo float/double, việc tạo số thập phân trực tiếp từ số thực nhị phân và các phép chia thiếu số chữ số thập phân hoặc quy tắc làm tròn. |
| Kiểm thử tự động (`T`) | Kiểm thử tự động | Chạy kiểm thử giá trị biên (số tiền 18 chữ số phần nguyên theo `CON-02`, chia tỷ lệ phần trăm cho số dư ví lẻ) và kiểm thử độ bền với chuỗi giao dịch liên tiếp để đối chiếu số dư tính toán với số dư thực tế. Ngưỡng số lượng giao dịch cụ thể sẽ bổ sung khi có benchmark chính thức. |

### 5.2 NFR-02: Hiệu năng và độ trễ phản hồi

Đặc tính chất lượng: Hiệu quả vận hành, đặc tính con Hành vi thời gian và Mức sử dụng tài nguyên.

Mô tả yêu cầu: với quy mô dữ liệu cá nhân thông thường, người dùng không cảm nhận được độ trễ ở các thao tác sau:

1. Ghi nhận giao dịch (thu chi hoặc chuyển tiền): cập nhật bộ nhớ và đồng bộ số dư các thực thể liên quan ngay lập tức.
2. Truy vấn: lọc theo khoảng thời gian, danh mục hoặc ví, tìm kiếm từ khóa và phân trang.
3. Tổng hợp báo cáo: thống kê dòng tiền 12 tháng gần nhất và cơ cấu chi tiêu 2 cấp danh mục.
4. Khởi động: thời gian từ lúc chạy ứng dụng đến khi giao diện chính sẵn sàng đủ ngắn để không gián đoạn việc sử dụng trên máy tính cá nhân.
5. Bộ nhớ: không rò rỉ bộ nhớ tăng dần khi sử dụng liên tục trong một phiên làm việc.

Chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Độ trễ ghi giao dịch, truy vấn, tổng hợp báo cáo và khởi động | Đánh giá định tính qua kịch bản sử dụng trực tiếp | Không có cảm giác trễ nhận biết được |
| Ổn định bộ nhớ khi sử dụng liên tục | Xu hướng cấp phát bộ nhớ theo thời gian | Không phát hiện rò rỉ bộ nhớ tăng dần |

Phương pháp kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Trình diễn thao tác (`D`) | Quan sát trực tiếp | Thực hiện các thao tác ghi sổ, truy vấn, lọc và tổng hợp báo cáo trên bộ dữ liệu mẫu để xác nhận phản hồi tức thì. |
| Đo kiểm tài nguyên (`A`) | Phân tích số liệu | Theo dõi xu hướng cấp phát bộ nhớ trong phiên sử dụng kéo dài để phát hiện rò rỉ. |

Ghi chú: đây là NFR định tính, chưa có số liệu benchmark trên môi trường chuẩn. Khi đo đạc thực tế, các ngưỡng bằng số (P95, thời gian khởi động, dung lượng bộ nhớ) sẽ được bổ sung kèm bằng chứng đo.

### 5.3 NFR-03: Độ tin cậy và lưu trữ an toàn

Đặc tính chất lượng: Độ tin cậy, đặc tính con Khả năng chịu lỗi và Khả năng phục hồi.

Mô tả yêu cầu:

1. Ghi tệp an toàn: khi ngắt nguồn, tắt hệ điều hành đột ngột hoặc chương trình bị buộc dừng, tệp dữ liệu chính chỉ có thể là bản cũ nguyên vẹn hoặc bản mới đầy đủ.
   - Không ghi dở: nội dung mới chỉ trở thành tệp chính thức khi đã được ghi đầy đủ.
   - Lưu bền xuống đĩa: dữ liệu của lần ghi đã hoàn tất phải nằm trên đĩa, không chỉ trong vùng nhớ tạm.
   - Bảo toàn bản cũ: nếu không bảo đảm được việc thay thế trọn vẹn, giữ lại bản sao tệp gốc trước khi thay thế.
2. Tự động sao lưu: tạo bản sao lưu gắn nhãn thời gian theo mẫu `backup_YYYYMMDD_HHMMSS.json` trong thư mục `backups/` (`CON-06`), trước các tác vụ có nguy cơ thay thế dữ liệu quy mô lớn (phục hồi dữ liệu từ tệp nhập ngoài theo `UC-DAT-02`, đặt lại hệ thống).
3. Kiểm tra toàn vẹn khi đọc: khi đọc từ tệp lưu trữ hoặc bản sao lưu, kiểm tra dữ liệu trước khi nạp vào bộ nhớ làm việc (đúng lược đồ JSON, mã liên kết danh mục hợp lệ, quy tắc bảo toàn số dư được thỏa mãn). Nếu vi phạm, hủy toàn bộ lần đọc, giữ nguyên trạng thái dữ liệu trước đó và hiển thị thông báo lỗi chi tiết.
4. Xử lý lỗi đọc ghi: kiểm soát và xử lý 100% ngoại lệ đọc ghi tệp. Khi ổ đĩa đầy hoặc thư mục bị khóa quyền ghi, giữ nguyên tệp dữ liệu gốc và thông báo lỗi rõ ràng.

Chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Tệp chính bị hỏng khi chương trình bị ngắt đột ngột | Số tệp hỏng chia tổng số lần ghi bị ngắt | 0.00% |
| Giữ nguyên dữ liệu lịch sử đã lưu xong | Tỷ lệ bản ghi giao dịch còn nguyên sau sự cố ngắt | 100.00% |
| Tác vụ thay thế dữ liệu quy mô lớn có sao lưu trước | Số tác vụ có sao lưu trước chia tổng số tác vụ loại này | 100.00% |
| Dữ liệu nguyên vẹn sau phục hồi từ bản sao lưu | So khớp cấu trúc JSON và quy tắc bảo toàn số dư sau phục hồi | Khớp 100%, không sai lệch |

Phương pháp kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Kiểm thử giả lập lỗi (`T`) | Kiểm thử tự động | Mô phỏng ghi tệp dữ liệu lớn rồi buộc ngắt chương trình giữa chừng; khởi động lại để xác minh tệp chính còn nguyên vẹn và đọc được. |
| Soát xét kỹ thuật (`I`) | Đánh giá kiểm tra | Rà soát mã nguồn phần lưu trữ: tệp chính không bao giờ ở trạng thái ghi dở, dữ liệu đã ghi xong nằm trên đĩa và mọi lỗi đọc ghi được xử lý đầy đủ. |

### 5.4 NFR-04: Khả năng bảo trì và kiến trúc hướng đối tượng

Đặc tính chất lượng: Khả năng bảo trì, đặc tính con Tính module hóa, Tính tái sử dụng, Tính phân tích được và Tính kiểm thử được.

Mô tả yêu cầu:

1. Phân định trách nhiệm theo thành phần độc lập:
   - Giao diện người dùng không truy cập trực tiếp dữ liệu lưu trữ mà làm việc qua thành phần xử lý nghiệp vụ.
   - Phụ thuộc một chiều: thành phần bên ngoài phụ thuộc thành phần bên trong; không có phụ thuộc ngược chiều hoặc phụ thuộc vòng giữa các mô-đun.
   - Mỗi mô-đun chỉ đảm nhiệm một trách nhiệm. Các thực thể nghiệp vụ (người dùng, ví, giao dịch thu chi, giao dịch chuyển tiền, danh mục) chỉ quản lý trạng thái và bất biến nghiệp vụ của riêng mình, tách biệt khỏi phần hiển thị và cách lưu trữ.
2. Bảo vệ trạng thái nội bộ:
   - Trạng thái nội bộ của thực thể chỉ được truy xuất qua giao diện đọc có kiểm soát hoặc thao tác nghiệp vụ chuyên biệt. Không cho gán trực tiếp số dư ví hoặc ghi đè trạng thái giao dịch ngoài quy trình ghi nhận dòng tiền.
   - Dữ liệu dạng danh sách hoặc tập hợp trả về không thể bị sửa đổi từ bên ngoài phạm vi kiểm soát nghiệp vụ.
3. Mở rộng không phá vỡ hành vi hiện có:
   - Mọi ví được xử lý đồng nhất: công cụ tổng hợp số dư và công cụ tính tỷ lệ từng ví dùng chung một logic tính toán.
   - Thêm danh mục mới không đòi hỏi sửa logic tính số dư hiện có.
4. Kiểm thử tự động và độ phức tạp:
   - Có kiểm thử đơn vị tự động, độc lập công nghệ, cho toàn bộ quy tắc nghiệp vụ, thuật toán tính số dư và công thức tính tỷ lệ, gồm các trường hợp biên (số tiền âm, bằng 0, vượt hạn mức, tên rỗng, tên dài nhất, tên vượt độ dài tối đa).
   - Mỗi thao tác nghiệp vụ chỉ có một luồng xử lý rõ ràng, tránh lồng nhiều nhánh điều kiện.

Chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Phạm vi kiểm thử quy tắc nghiệp vụ | Tỷ lệ quy tắc có ca kiểm thử tương ứng | 100%, mỗi quy tắc có tối thiểu 1 ca |
| Kiểm thử trường hợp biên | Có ca kiểm thử ở hai phía mỗi giới hạn của quy tắc | Đủ cho mọi quy tắc có giới hạn về số hoặc độ dài |
| Phụ thuộc vòng giữa các mô-đun | Số vòng phụ thuộc | 0 |
| Thực thể bảo vệ trạng thái nội bộ khỏi ghi đè trái phép | Tỷ lệ thực thể không cho gán đè trực tiếp từ bên ngoài | 100.00% |
| Tập hợp trả về không thể sửa đổi từ bên ngoài | Tỷ lệ tập hợp không thể sửa đổi khi truy xuất | 100.00% |

Phương pháp kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Kiểm thử tự động (`T`) | Kiểm thử tự động | Chạy kiểm thử đơn vị cho từng quy tắc nghiệp vụ, gồm trường hợp biên, và xác nhận kết quả PASS. |
| Soát xét kỹ thuật kiến trúc (`I`) | Đánh giá kiểm tra | Rà soát mã nguồn để xác nhận: (1) không có phụ thuộc vòng; (2) thành phần chứa quy tắc nghiệp vụ không phụ thuộc thành phần bên ngoài; (3) trạng thái nội bộ của thực thể không bị ghi đè trực tiếp. |

### 5.5 NFR-05: Tính khả dụng và trải nghiệm người dùng

Đặc tính chất lượng: Khả năng tương tác, đặc tính con Tính dễ học, Tính dễ vận hành và Bảo vệ người dùng khỏi lỗi.

Mô tả yêu cầu:

1. Tinh giản nhập liệu:
   - Giá trị mặc định: biểu mẫu thu chi và chuyển tiền tự điền ngày hiện tại; ô chọn ví tự chọn ví dùng gần nhất, hoặc ví "Tiền mặt" nếu chưa có giao dịch nào.
   - Con trỏ tự đặt vào ô số tiền khi mở hộp thoại ghi nhận giao dịch.
   - Lọc danh mục theo ngữ cảnh: giao dịch thu nhập chỉ hiển thị danh mục `INCOME`, giao dịch chi tiêu chỉ hiển thị danh mục `EXPENSE`, và chỉ gồm danh mục đang dùng. Danh mục của phí giao dịch luôn thuộc `EXPENSE`, kể cả khi giao dịch gốc là thu nhập.
   - Định dạng số tiền theo thời gian thực bằng dấu phân tách hàng nghìn, ví dụ nhập "1500000" hiển thị "1,500,000 VNĐ".
2. Bảo vệ người dùng khỏi lỗi:
   - Hiển thị hộp thoại xác nhận rõ ràng trước thao tác rủi ro cao: xóa giao dịch, lưu trữ ví còn số dư, nhập tệp thay thế dữ liệu cũ.
   - Kiểm tra định dạng số tiền và điều kiện bắt buộc ngay khi nhập, ngăn gửi yêu cầu không hợp lệ.
3. Thông báo lỗi định hướng khắc phục:
   - Thông báo lỗi dữ liệu đầu vào và vi phạm quy tắc nghiệp vụ dùng tiếng Việt tự nhiên, ngắn gọn, nêu rõ nguyên nhân và hướng xử lý. Ví dụ: "Số dư ví MB Bank hiện tại là 150,000 VNĐ, không đủ để chi trả khoản chi 200,000 VNĐ. Vui lòng giảm số tiền chi tiêu hoặc chuyển tiền từ ví khác sang."
   - Không hiển thị lỗi kỹ thuật thuần túy, vết ngăn xếp (stack trace) hoặc mã lỗi chưa xử lý ra giao diện. Mọi ngoại lệ kỹ thuật được ánh xạ sang thông điệp trong bảng mã lỗi chuẩn (`ERR-*`).

Chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Thông báo lỗi có hướng khắc phục | Số thông báo có hướng dẫn cụ thể trong bảng mã lỗi chuẩn | 100.00% |
| Lỗi kỹ thuật hoặc stack trace lộ ra giao diện | Tỷ lệ thông báo kỹ thuật rò rỉ | 0.00% |
| Thao tác xóa hoặc thay thế dữ liệu có xác nhận | Số thao tác có hộp thoại xác nhận chia tổng số thao tác loại này | 100.00% |
| Mức thuận tiện khi làm quen (người dùng mới) | Đánh giá định tính qua thao tác tạo ví và ghi giao dịch đầu tiên | Thao tác thuận tiện, không gặp trở ngại; số liệu thời gian sẽ bổ sung khi có benchmark chính thức |

Phương pháp kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Trình diễn thao tác (`D`) | Quan sát trực tiếp | Quan sát nhóm thử nghiệm thực hiện 3 kịch bản cơ bản (tạo ví, ghi chi tiêu, chuyển tiền) để đánh giá mức độ thuận tiện khi làm quen. |
| Soát xét kỹ thuật thông báo lỗi (`I`) | Đánh giá kiểm tra | Rà soát bảng thông điệp lỗi trong mã nguồn về tính đầy đủ của tiếng Việt và hướng dẫn khắc phục; bảo đảm không lộ stack trace ra màn hình. |

### 5.6 NFR-06: Tính tương thích và môi trường chạy

Đặc tính chất lượng: Tính linh hoạt, đặc tính con Tính thích ứng và Tính dễ cài đặt.

Mô tả yêu cầu:

1. Môi trường chạy: máy ảo Java tương thích phiên bản 17 LTS trở lên (`CON-01`).
2. Độc lập hệ điều hành: ứng dụng đóng gói độc lập, chạy đồng nhất trên mọi hệ điều hành máy tính cá nhân có JVM tương thích (Windows, macOS, Linux), không cần sửa mã nguồn hay biên dịch lại.
3. Đường dẫn tệp: không viết cứng ký tự phân cách thư mục (`\` hoặc `/`); mọi thao tác đường dẫn dùng đối tượng đường dẫn trừu tượng độc lập hệ điều hành.
4. Mã hóa UTF-8 không BOM: mọi thao tác đọc, ghi tệp dữ liệu cục bộ, tệp xuất JSON và xuất báo cáo phải chỉ định rõ UTF-8 không BOM, để tiếng Việt có dấu lưu trữ và hiển thị đúng, bất kể bảng mã mặc định của hệ điều hành.
5. Không phụ thuộc thư viện nhị phân ngoại lai: không dùng thư viện nhị phân phụ thuộc nền tảng, để đóng gói thành một gói ứng dụng chạy được mà không cần cài thêm phụ thuộc theo từng hệ điều hành.

Chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Bộ kiểm thử tự động trên Windows, macOS, Linux | Tỷ lệ ca kiểm thử PASS trên từng nền tảng | 100.00% |
| Thao tác đọc ghi tệp chỉ định UTF-8 | Số thao tác có chỉ định UTF-8 chia tổng số thao tác đọc ghi tệp | 100.00% |
| Hiển thị tiếng Việt có dấu | Số trường hợp hiển thị sai ký tự phát hiện được | 0 |
| Thư viện nhị phân phụ thuộc nền tảng | Số thư viện sử dụng | 0 |
| Phiên bản Java tối thiểu | Phiên bản JDK/JRE yêu cầu | Java SE 17 LTS |

Phương pháp kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Kiểm thử tương thích đa nền tảng (`T`) | Kiểm thử tự động | Chạy bộ kiểm thử đơn vị trên từng hệ điều hành mục tiêu để bảo đảm kết quả giống nhau. |
| Soát xét mã nguồn I/O tĩnh (`I`) | Đánh giá kiểm tra | Rà soát mọi thao tác đọc ghi tệp để bảo đảm luôn chỉ định UTF-8 và dùng đối tượng đường dẫn trừu tượng. |