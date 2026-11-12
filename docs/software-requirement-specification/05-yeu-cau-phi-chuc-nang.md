## 5. YÊU CẦU PHI CHỨC NĂNG

Các yêu cầu phi chức năng của Hệ thống Quản lý Tài chính Cá nhân (PFMS) được xây dựng dựa trên các nhóm đặc tính chất lượng phần mềm phổ biến. Các yêu cầu này đóng vai trò là ràng buộc vận hành và kiến trúc then chốt, bảo đảm hệ thống đạt độ chính xác tài chính tuyệt đối, vận hành ổn định, lưu trữ an toàn, thuận tiện khi sử dụng, tương thích với môi trường triển khai và dễ bảo trì, mở rộng theo nguyên lý thiết kế hướng đối tượng.

Mỗi yêu cầu phi chức năng được đặc tả nhất quán theo khuôn mẫu:

- Đặc tính chất lượng: xác định yêu cầu thuộc nhóm đặc tính chất lượng phần mềm nào, như độ chính xác, độ tin cậy, bảo mật, khả năng sử dụng, tính tương thích hoặc khả năng bảo trì.
- Mô tả yêu cầu: xác định phạm vi ràng buộc vận hành và giải pháp kiến trúc ở mức bài toán, làm rõ hệ thống phải đạt được điều gì mà chưa đi vào chi tiết cài đặt.
- Chỉ số đo và ngưỡng nghiệm thu: định lượng các tiêu chí đo kiểm và ngưỡng chấp thuận để có thể đánh giá yêu cầu đã được đáp ứng hay chưa.
- Phương pháp kiểm chứng: cơ chế xác minh yêu cầu, gồm kiểm thử tự động (`T`), trình diễn thao tác (`D`), soát xét kỹ thuật (`I`) và phân tích số liệu (`A`).

Bảng tổng hợp danh mục các yêu cầu phi chức năng của hệ thống theo từng đặc tính chất lượng, kèm phương pháp kiểm chứng tương ứng và chỉ số trọng tâm:

| Mã yêu cầu | Tên yêu cầu | Đặc tính chất lượng | Phương pháp chính | Chỉ số trọng tâm |
| :---: | :--- | :--- | :---: | :--- |
| `NFR-01` | Độ chính xác và toàn vẹn dữ liệu tài chính | Mức độ phù hợp chức năng / Tính chính xác chức năng | T, I | Sai số tích lũy 0.00 VNĐ; 0% trường dùng số thực nhị phân (`float`/`double`); 0% vi phạm quy tắc bảo toàn số dư. |
| `NFR-02` | Hiệu năng và độ trễ phản hồi | Hiệu quả vận hành / Hành vi thời gian, Mức sử dụng tài nguyên | D, A | Phản hồi dưới 1.0 giây với thao tác thông thường; không rò rỉ bộ nhớ khi sử dụng liên tục. |
| `NFR-03` | Độ tin cậy và lưu trữ an toàn | Độ tin cậy / Khả năng chịu lỗi, Khả năng phục hồi | T, I | Ghi tệp nguyên tử an toàn; tự động sao lưu trước tác vụ thay thế dữ liệu quy mô lớn; không mất dữ liệu khi ngắt nguồn. |
| `NFR-04` | Khả năng bảo trì và kiến trúc hướng đối tượng | Khả năng bảo trì / Tính mô-đun hóa, Tính tái sử dụng, Tính phân tích được, Tính kiểm thử được | T, I | Không phụ thuộc vòng; trạng thái nội bộ được bảo vệ; 100% quy tắc nghiệp vụ có kiểm thử đơn vị. |
| `NFR-05` | Tính khả dụng và trải nghiệm người dùng | Khả năng tương tác / Tính dễ học, Tính dễ vận hành, Bảo vệ người dùng khỏi lỗi | D, I | Xác nhận thao tác rủi ro cao; 100% thông báo lỗi bằng tiếng Việt có hướng dẫn khắc phục cụ thể. |
| `NFR-06` | Tính tương thích và môi trường chạy | Tính linh hoạt / Tính thích ứng, Tính dễ cài đặt | T, I | Thực thi trên JVM 17 LTS, độc lập hệ điều hành; tệp tin định dạng UTF-8 không BOM; không dùng thư viện nhị phân phụ thuộc nền tảng. |

### 5.1 NFR-01: Độ chính xác và toàn vẹn dữ liệu tài chính

Đặc tính chất lượng: Mức độ phù hợp chức năng, đặc tính con Tính chính xác chức năng.

Mô tả yêu cầu:

1. Kiểu dữ liệu số học:
   - Dùng kiểu số thập phân chính xác cố định cho mọi trường số, biến trung gian, phép tính tiền tệ, số dư ví và tổng số dư, với tối thiểu 18 chữ số phần nguyên và 2 chữ số thập phân.
   - Cấm sử dụng kiểu số thực nhị phân (`float`, `double`) trong cấu trúc dữ liệu nghiệp vụ, thuật toán tính toán và lưu trữ. Giá trị số chỉ được khởi tạo từ dữ liệu nhập dạng thập phân chính xác hoặc từ số nguyên, tuyệt đối không chuyển đổi trung gian qua `float` hoặc `double`.
2. Độ chính xác thập phân và làm tròn:
   - Tiền VNĐ hiển thị 0 chữ số thập phân (số nguyên đồng) trên giao diện người dùng; tính toán, xử lý nghiệp vụ và kiểm thử dùng 2 chữ số thập phân.
   - Tỷ lệ phần trăm trung gian dùng tối thiểu 4 chữ số thập phân theo quy tắc Độ chính xác tài chính (`BR-FIN-01`).
   - Mọi phép chia phải chỉ định rõ số chữ số thập phân và chế độ làm tròn ngân hàng (làm tròn đến số chẵn gần nhất khi giá trị nằm giữa hai mức).
   - So sánh số theo giá trị thực, bỏ qua khác biệt do cách hiển thị số chữ số thập phân.
3. Sai số tích lũy: sai số của mọi chuỗi phép tính, trong từng giao dịch và trên toàn bộ dữ liệu tổng hợp, bảo đảm bằng 0.00 VNĐ.
4. Bảo toàn số dư theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`): tại mọi thời điểm, số dư khả dụng của ví $w$ tại thời điểm quan sát $T$ thỏa mãn công thức:

   $$\text{Số dư ví } w \text{ tại } T = (\text{Thu} + \text{Chuyển vào} + \text{Điều chỉnh tăng}) - (\text{Chi} + \text{Chuyển ra} + \text{Điều chỉnh giảm})$$

   Mỗi hạng tử là tổng các khoản của ví $w$ đến thời điểm $T$. Điều chỉnh tăng và điều chỉnh giảm lần lượt là các khoản điều chỉnh số dư dương và âm theo yêu cầu Điều chỉnh số dư ví (`FR-WAL-05`).
5. Cân bằng nguồn - đích theo quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): mọi lần chuyển tiền nội bộ phải có số tiền trừ ở ví nguồn bằng số tiền cộng ở ví đích (chưa tính phí). Tổng tiền trong hệ thống giữ nguyên, trừ chi phí chuyển tiền phát sinh nếu có.

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với độ chính xác và tính toàn vẹn dữ liệu tài chính:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Sai số tích lũy số học | Chênh lệch tuyệt đối giữa tổng tiền kỳ vọng và thực tế | 0.00 VNĐ |
| Kiểu số thực nhị phân trong mô hình nghiệp vụ | Số trường dùng `float`/`double` chia tổng số trường số | 0.00% |
| Vi phạm quy tắc Bảo toàn số dư ví (`BR-FIN-02`) | Số giao dịch vi phạm chia tổng số giao dịch | 0.00% trên toàn bộ tập kiểm thử |
| Sai lệch nguồn - đích khi chuyển tiền theo quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`), chưa tính phí | Chênh lệch tuyệt đối giữa tiền trừ ví nguồn và tiền cộng ví đích | 0.00 VNĐ |
| Số chữ số thập phân của tiền VNĐ | Số chữ số thập phân khi hiển thị, khi tính toán và kiểm thử | 0 (giao diện) / 2 (tính toán, kiểm thử) |
| Số chữ số thập phân của tỷ lệ phần trăm | Số chữ số thập phân trong phép tính tỷ lệ | Từ 4 trở lên |
| Phép chia có chỉ định số chữ số thập phân và làm tròn | Số phép chia thập phân chính xác đạt yêu cầu chia tổng số phép chia | 100.00% |

Bảng quy định phương pháp và quy trình kiểm chứng kỹ thuật cho yêu cầu `NFR-01`:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Soát xét kỹ thuật (`I`) | Đánh giá kiểm tra | Rà soát mã nguồn phần nghiệp vụ để phát hiện khai báo `float`/`double`, việc tạo số thập phân trực tiếp từ số thực nhị phân và các phép chia thiếu số chữ số thập phân hoặc quy tắc làm tròn. |
| Kiểm thử tự động (`T`) | Kiểm thử tự động | Chạy kiểm thử giá trị biên (số tiền 18 chữ số phần nguyên, chia tỷ lệ phần trăm cho số dư ví lẻ) và kiểm thử độ bền với chuỗi giao dịch liên tiếp để đối chiếu số dư tính toán với số dư thực tế. |

### 5.2 NFR-02: Hiệu năng và độ trễ phản hồi

Đặc tính chất lượng: Hiệu quả vận hành, đặc tính con Hành vi thời gian và Mức sử dụng tài nguyên.

Mô tả yêu cầu: với quy mô dữ liệu cá nhân thông thường (tối thiểu 10.000 giao dịch), hệ thống đáp ứng tức thì và không gây gián đoạn trải nghiệm ở các thao tác:

1. Ghi nhận giao dịch (thu chi hoặc chuyển tiền): cập nhật bộ nhớ và đồng bộ số dư các thực thể liên quan ngay lập tức.
2. Truy vấn dữ liệu: lọc theo khoảng thời gian, danh mục hoặc ví, tìm kiếm từ khóa và phân trang dữ liệu.
3. Tổng hợp báo cáo: thống kê dòng tiền 12 tháng gần nhất và cơ cấu chi tiêu 2 cấp danh mục.
4. Khởi động ứng dụng: thời gian từ lúc kích hoạt ứng dụng đến khi màn hình làm việc chính sẵn sàng tương tác đủ ngắn, không gián đoạn công việc của người dùng.
5. Quản lý bộ nhớ: duy trì mức tiêu thụ bộ nhớ ổn định, không xảy ra rò rỉ bộ nhớ khi làm việc liên tục trong phiên làm việc kéo dài.

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với hiệu năng vận hành và độ trễ phản hồi:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Độ trễ ghi nhận giao dịch, truy vấn và tổng hợp báo cáo | Thời gian xử lý từ lúc kích hoạt thao tác đến khi giao diện hoàn tất cập nhật | Dưới 1.0 giây trong điều kiện vận hành chuẩn |
| Thời gian khởi động ứng dụng | Thời gian từ lúc chạy ứng dụng đến khi giao diện chính sẵn sàng tiếp nhận thao tác | Dưới 3.0 giây trên máy tính cá nhân tiêu chuẩn |
| Ổn định bộ nhớ khi sử dụng liên tục | Xu hướng cấp phát bộ nhớ JVM theo thời gian làm việc | Không phát hiện rò rỉ bộ nhớ tăng dần |

Bảng quy định phương pháp và quy trình kiểm chứng hiệu năng cho yêu cầu `NFR-02`:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Trình diễn thao tác (`D`) | Quan sát trực tiếp | Thực hiện các thao tác ghi sổ, truy vấn, lọc và tổng hợp báo cáo trên bộ dữ liệu mẫu để xác nhận độ trễ đáp ứng của hệ thống. |
| Đo kiểm tài nguyên (`A`) | Phân tích số liệu | Theo dõi biểu đồ cấp phát bộ nhớ JVM trong phiên làm việc kéo dài để phát hiện nguy cơ rò rỉ bộ nhớ. |

### 5.3 NFR-03: Độ tin cậy và lưu trữ an toàn

Đặc tính chất lượng: Độ tin cậy, đặc tính con Khả năng chịu lỗi và Khả năng phục hồi.

Mô tả yêu cầu:

1. Ghi tệp an toàn: khi mất nguồn điện, hệ điều hành tắt đột ngột hoặc tiến trình ứng dụng bị buộc dừng, tệp dữ liệu chính chỉ tồn tại ở một trong hai trạng thái: bản cũ nguyên vẹn hoặc bản mới đầy đủ.
   - Không ghi dở: dữ liệu mới chỉ thay thế tệp chính thức khi toàn bộ nội dung đã ghi thành công và trọn vẹn xuống tệp tạm.
   - Lưu bền xuống đĩa: dữ liệu của lần ghi hoàn tất phải được đẩy trực tiếp xuống đĩa vật lý, không tồn đọng trong vùng đệm bộ nhớ.
   - Bảo toàn bản cũ: tự động giữ lại bản sao tệp dữ liệu gốc trước khi tiến hành thay thế nếu phát hiện rủi ro gián đoạn.
2. Tự động sao lưu: tạo bản sao lưu có gắn nhãn thời gian theo mẫu `backup_YYYYMMDD_HHMMSS.json` trong thư mục `backups/`, trước khi thực hiện các tác vụ thay thế dữ liệu quy mô lớn (phục hồi dữ liệu từ tệp nhập ngoài theo ca sử dụng Phục hồi dữ liệu từ bản sao lưu (`UC-DAT-02`), đặt lại hệ thống).
3. Kiểm tra toàn vẹn khi đọc: khi đọc từ tệp lưu trữ hoặc bản sao lưu, kiểm tra cấu trúc dữ liệu trước khi nạp vào bộ nhớ (đúng lược đồ JSON, mã liên kết danh mục hợp lệ, thỏa mãn quy tắc bảo toàn số dư). Nếu phát hiện sai lệch, hủy toàn bộ thao tác đọc, giữ nguyên trạng thái dữ liệu trước đó và hiển thị thông báo lỗi chi tiết.
4. Xử lý lỗi đọc ghi: kiểm soát và xử lý toàn bộ ngoại lệ vào/ra tệp tin. Khi ổ đĩa đầy hoặc thư mục bị khóa quyền ghi, giữ nguyên tệp dữ liệu gốc và hiển thị thông báo lỗi rõ ràng.

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với độ tin cậy và an toàn lưu trữ dữ liệu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Tệp chính bị hỏng khi chương trình bị ngắt đột ngột | Số tệp hỏng chia tổng số lần ghi bị ngắt | 0.00% |
| Giữ nguyên dữ liệu lịch sử đã lưu thành công | Tỷ lệ bản ghi giao dịch còn nguyên vẹn sau sự cố ngắt nguồn | 100.00% |
| Tác vụ thay thế dữ liệu quy mô lớn có sao lưu trước | Số tác vụ có sao lưu trước chia tổng số tác vụ loại này | 100.00% |
| Dữ liệu nguyên vẹn sau phục hồi từ bản sao lưu | So khớp cấu trúc JSON và quy tắc bảo toàn số dư sau phục hồi | Khớp 100.00%, không sai lệch |

Bảng quy định phương pháp và quy trình kiểm chứng an toàn lưu trữ cho yêu cầu `NFR-03`:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Kiểm thử giả lập lỗi (`T`) | Kiểm thử tự động | Mô phỏng quá trình ghi tệp dữ liệu lớn rồi buộc ngắt tiến trình giữa chừng; khởi động lại để xác minh tệp chính còn nguyên vẹn và đọc được bình thường. |
| Soát xét kỹ thuật (`I`) | Đánh giá kiểm tra | Rà soát mã nguồn thành phần lưu trữ: tệp chính không bao giờ ở trạng thái ghi dở, dữ liệu ghi xong được đẩy xuống đĩa vật lý và toàn bộ lỗi vào/ra tệp được kiểm soát đầy đủ. |

### 5.4 NFR-04: Khả năng bảo trì và kiến trúc hướng đối tượng

Đặc tính chất lượng: Khả năng bảo trì, đặc tính con Tính mô-đun hóa, Tính tái sử dụng, Tính phân tích được và Tính kiểm thử được.

Mô tả yêu cầu:

1. Phân định trách nhiệm theo thành phần độc lập:
   - Giao diện người dùng không truy cập trực tiếp dữ liệu lưu trữ mà bắt buộc tương tác thông qua thành phần xử lý nghiệp vụ.
   - Phụ thuộc một chiều: thành phần bên ngoài phụ thuộc thành phần bên trong; không xuất hiện phụ thuộc ngược chiều hoặc phụ thuộc vòng giữa các mô-đun.
   - Mỗi mô-đun chỉ đảm nhiệm một trách nhiệm duy nhất. Các thực thể nghiệp vụ (người dùng, ví, giao dịch thu chi, giao dịch chuyển tiền, danh mục) chỉ quản lý trạng thái và bất biến nghiệp vụ của riêng mình, tách biệt khỏi phần hiển thị và cách thức lưu trữ.
2. Bảo vệ trạng thái nội bộ:
   - Trạng thái nội bộ của thực thể chỉ được truy xuất qua giao diện đọc có kiểm soát hoặc thao tác nghiệp vụ chuyên biệt. Không cho phép gán trực tiếp số dư ví hoặc ghi đè trạng thái giao dịch ngoài quy trình ghi nhận dòng tiền.
   - Dữ liệu dạng danh sách hoặc tập hợp trả về từ thực thể không thể bị sửa đổi trực tiếp từ bên ngoài phạm vi kiểm soát nghiệp vụ (áp dụng cơ chế bao bọc bất biến).
3. Khả năng mở rộng không phá vỡ hành vi hiện có:
   - Mọi ví được xử lý đồng nhất: thuật toán tổng hợp số dư và tính toán tỷ lệ từng ví sử dụng chung logic nghiệp vụ.
   - Bổ sung danh mục mới không đòi hỏi chỉnh sửa logic tính số dư hiện có.
4. Kiểm thử tự động và quản lý độ phức tạp:
   - Toàn bộ quy tắc nghiệp vụ, thuật toán tính số dư và công thức tỷ lệ đều có kiểm thử đơn vị tự động độc lập công nghệ, bao phủ đầy đủ các trường hợp biên (số tiền âm, bằng 0, vượt hạn mức, tên rỗng, tên dài nhất, tên vượt độ dài tối đa).
   - Mỗi thao tác nghiệp vụ duy trì một luồng xử lý rõ ràng, tránh lồng ghép nhiều nhánh điều kiện phức tạp.

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với khả năng bảo trì và kiến trúc hướng đối tượng:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Phạm vi kiểm thử quy tắc nghiệp vụ | Tỷ lệ quy tắc nghiệp vụ có ca kiểm thử tương ứng | 100.00%, mỗi quy tắc có tối thiểu 1 ca |
| Kiểm thử trường hợp biên | Tỷ lệ quy tắc có ràng buộc giá trị biên được bao phủ ca kiểm thử | 100.00% |
| Phụ thuộc vòng giữa các mô-đun | Số lượng vòng phụ thuộc phát hiện được | 0 |
| Bảo vệ trạng thái nội bộ thực thể khỏi ghi đè | Tỷ lệ thực thể ngăn chặn gán đè trực tiếp trạng thái từ bên ngoài | 100.00% |
| Bảo vệ tập hợp trả về khỏi sửa đổi ngoại lai | Tỷ lệ tập hợp dữ liệu không thể sửa đổi khi truy xuất từ bên ngoài | 100.00% |

Bảng quy định phương pháp và quy trình kiểm chứng kiến trúc cho yêu cầu `NFR-04`:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Kiểm thử tự động (`T`) | Kiểm thử tự động | Thực thi toàn bộ bộ kiểm thử đơn vị cho từng quy tắc nghiệp vụ, bao gồm các ca kiểm thử giá trị biên, và xác nhận kết quả kiểm thử đạt trạng thái thành công. |
| Soát xét kỹ thuật kiến trúc (`I`) | Đánh giá kiểm tra | Rà soát cấu trúc mã nguồn để bảo đảm: (1) không có phụ thuộc vòng giữa các gói mã nguồn; (2) tầng nghiệp vụ không phụ thuộc ngược ra tầng bên ngoài; (3) trạng thái nội bộ của thực thể được đóng gói an toàn. |

### 5.5 NFR-05: Tính khả dụng và trải nghiệm người dùng

Đặc tính chất lượng: Khả năng tương tác, đặc tính con Tính dễ học, Tính dễ vận hành và Bảo vệ người dùng khỏi lỗi.

Mô tả yêu cầu:

1. Tinh giản quy trình nhập liệu:
   - Điền sẵn giá trị mặc định hợp lý: biểu mẫu thu chi và chuyển tiền tự động chọn ngày hiện tại; trường chọn ví tự động ưu tiên ví sử dụng gần nhất, hoặc ví "Tiền mặt" nếu hệ thống chưa ghi nhận giao dịch nào.
   - Tự động đặt con trỏ vào ô nhập số tiền khi mở hộp thoại ghi nhận giao dịch mới.
   - Lọc danh mục theo ngữ cảnh giao dịch: giao dịch thu nhập chỉ hiển thị danh mục `INCOME`, giao dịch chi tiêu chỉ hiển thị danh mục `EXPENSE`, và chỉ hiển thị các danh mục đang hoạt động. Danh mục của chi phí giao dịch luôn thuộc `EXPENSE`, kể cả khi giao dịch gốc là thu nhập.
   - Định dạng hiển thị số tiền tức thì bằng dấu phân cách hàng nghìn trong khi người dùng nhập liệu (ví dụ: nhập "1500000" hiển thị ngay thành "1,500,000 VNĐ").
2. Bảo vệ người dùng khỏi lỗi thao tác:
   - Hiển thị hộp thoại xác nhận rõ ràng trước các thao tác có mức độ rủi ro cao: xóa giao dịch, lưu trữ ví còn số dư, nhập tệp dữ liệu thay thế cơ sở dữ liệu hiện tại.
   - Kiểm tra định dạng số tiền và các trường dữ liệu bắt buộc ngay trong quá trình nhập liệu, ngăn chặn gửi dữ liệu không hợp lệ.
3. Thông báo lỗi định hướng giải quyết:
   - Mọi thông báo lỗi dữ liệu đầu vào và vi phạm quy tắc nghiệp vụ đều sử dụng tiếng Việt tự nhiên, rõ ràng, nêu đúng nguyên nhân và hướng dẫn cụ thể cách khắc phục. Ví dụ: "Số dư ví MB Bank hiện tại là 150,000 VNĐ, không đủ để chi trả khoản chi 200,000 VNĐ. Vui lòng giảm số tiền chi tiêu hoặc chuyển tiền từ ví khác sang."
   - Tuyệt đối không hiển thị lỗi kỹ thuật thô, thông tin vết ngăn xếp hoặc mã lỗi chưa xử lý ra màn hình người dùng. Mọi ngoại lệ kỹ thuật nội bộ bắt buộc phải được ánh xạ sang thông điệp trong bảng mã lỗi chuẩn (`ERR-*`).

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với tính khả dụng và trải nghiệm người dùng:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Thông báo lỗi có hướng dẫn khắc phục | Số thông báo lỗi có kèm hướng dẫn xử lý trong bảng mã lỗi chuẩn | 100.00% |
| Lỗi kỹ thuật hoặc vết ngăn xếp lộ ra giao diện | Tỷ lệ thông báo lỗi kỹ thuật nội bộ hiển thị trực tiếp cho người dùng | 0.00% |
| Thao tác xóa hoặc thay thế dữ liệu có xác nhận | Số thao tác rủi ro cao có hộp thoại cảnh báo xác nhận chia tổng số thao tác | 100.00% |
| Mức độ thuận tiện khi làm quen (người dùng mới) | Tỷ lệ người dùng thử nghiệm hoàn tất luồng tạo ví và ghi giao dịch đầu tiên | 100.00% không gặp trở ngại điều hướng |

Bảng quy định phương pháp và quy trình kiểm chứng tính khả dụng cho yêu cầu `NFR-05`:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Trình diễn thao tác (`D`) | Quan sát trực tiếp | Quan sát nhóm người dùng thử nghiệm thực hiện các kịch bản tương tác cơ bản (tạo ví, ghi chi tiêu, chuyển tiền) để đánh giá độ trực quan và mức độ dễ tiếp cận. |
| Soát xét kỹ thuật thông báo lỗi (`I`) | Đánh giá kiểm tra | Rà soát toàn bộ danh mục thông điệp lỗi trong mã nguồn về tính nhất quán tiếng Việt, tính định hướng người dùng và bảo đảm không để lộ chi tiết kỹ thuật nội bộ ra giao diện. |

### 5.6 NFR-06: Tính tương thích và môi trường chạy

Đặc tính chất lượng: Tính linh hoạt, đặc tính con Tính thích ứng và Tính dễ cài đặt.

Mô tả yêu cầu:

1. Môi trường thực thi: tương thích máy ảo Java (JVM) phiên bản 17 LTS trở lên.
2. Độc lập hệ điều hành: ứng dụng được đóng gói độc lập, vận hành đồng nhất trên các hệ điều hành máy tính cá nhân phổ biến có cài đặt JVM tương thích (Windows, macOS, Linux) mà không cần chỉnh sửa mã nguồn hay biên dịch lại.
3. Quản lý đường dẫn tệp tin: không viết cứng ký tự phân cách thư mục (`\` hoặc `/`); toàn bộ thao tác xử lý đường dẫn phải thông qua đối tượng đường dẫn trừu tượng độc lập nền tảng của hệ thống vào/ra tệp tin.
4. Chuẩn mã hóa UTF-8 không BOM: toàn bộ thao tác đọc, ghi tệp dữ liệu cục bộ, tệp xuất JSON và báo cáo định dạng văn bản bắt buộc chỉ định rõ UTF-8 không BOM, bảo đảm dữ liệu tiếng Việt hiển thị chính xác trên mọi hệ điều hành.
5. Không phụ thuộc thư viện nhị phân phụ thuộc nền tảng: không sử dụng thư viện nhị phân ngoại lai phụ thuộc nền tảng phần cứng, bảo đảm ứng dụng có thể đóng gói trọn vẹn thành một tệp thực thi duy nhất.

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với tính tương thích và môi trường thực thi:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Bộ kiểm thử tự động trên Windows, macOS, Linux | Tỷ lệ ca kiểm thử đạt kết quả thành công trên từng hệ điều hành | 100.00% |
| Thao tác đọc ghi tệp chỉ định rõ UTF-8 | Số thao tác đọc ghi có chỉ định bảng mã UTF-8 chia tổng số thao tác tệp | 100.00% |
| Hiển thị dữ liệu tiếng Việt có dấu | Số trường hợp lỗi hiển thị hoặc sai lệch ký tự tiếng Việt phát hiện được | 0 |
| Thư viện nhị phân phụ thuộc nền tảng | Số lượng thư viện nhị phân phụ thuộc nền tảng được sử dụng | 0 |
| Phiên bản môi trường Java tối thiểu | Phiên bản JDK/JRE tối thiểu được hệ thống hỗ trợ | Java SE 17 LTS |

Bảng quy định phương pháp và quy trình kiểm chứng tính tương thích cho yêu cầu `NFR-06`:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Kiểm thử tương thích đa nền tảng (`T`) | Kiểm thử tự động | Thực thi toàn bộ bộ kiểm thử đơn vị tự động trên các hệ điều hành mục tiêu để bảo đảm tính nhất quán của kết quả xử lý. |
| Soát xét mã nguồn vào/ra tệp (`I`) | Đánh giá kiểm tra | Rà soát toàn bộ các thao tác đọc ghi tệp để bảo đảm luôn chỉ định bảng mã UTF-8 và sử dụng đường dẫn trừu tượng độc lập nền tảng. |