## 5. YÊU CẦU PHI CHỨC NĂNG

6 yêu cầu phi chức năng (`NFR-01` đến `NFR-06`) của Hệ thống Quản lý Tài chính Cá nhân xác lập các tiêu chuẩn chất lượng sản phẩm phần mềm theo mô hình chất lượng quốc tế ISO/IEC 25010:2023 và tiêu chuẩn kỹ nghệ yêu cầu ISO/IEC/IEEE 29148:2018. Các yêu cầu này đóng vai trò ràng buộc vận hành và kiến trúc then chốt, bảo đảm hệ thống đạt độ chính xác tài chính tuyệt đối, hiệu quả vận hành tối ưu, độ tin cậy lưu trữ an toàn, tính khả dụng cao, khả năng tương thích môi trường và khả năng bảo trì theo nguyên lý hướng đối tượng phục vụ trực tiếp các tiêu chí nghiệm thu học thuật.

Mỗi yêu cầu phi chức năng được đặc tả nhất quán theo khuôn mẫu 4 khối thông tin chuẩn:
1. Bảng phân loại đặc tính chất lượng: Xác định Đặc tính chất lượng và Đặc tính con tương ứng theo phân loại chuẩn quốc tế ISO/IEC 25010:2023.
2. Mô tả yêu cầu: Diễn giải chi tiết phạm vi chất lượng, căn cứ lý do kỹ thuật và các giải pháp kiến trúc tổng quát ở tầng bài toán, không rò rỉ mã nguồn cài đặt cụ thể.
3. Bảng chỉ số đo và ngưỡng nghiệm thu: Xác lập các thuộc tính đo kiểm, chỉ số đo, công thức định lượng kèm dung sai kiểm thử và ngưỡng nghiệm thu nhị phân rõ ràng.
4. Bảng phương pháp và công cụ kiểm chứng: Xác định phương thức nghiệm thu kỹ thuật dựa trên 4 phương pháp chuẩn theo ISO 29148 gồm Kiểm thử tự động (`T` - Test), Trình diễn thao tác (`D` - Demonstration), Soát xét kỹ thuật (`I` - Inspection), Phân tích số liệu (`A` - Analysis), kèm môi trường và công cụ thực hiện.

Bảng tổng hợp 6 yêu cầu phi chức năng của hệ thống:

| Mã yêu cầu | Tên yêu cầu | Đặc tính chất lượng ISO 25010 | Phương pháp chính | Chỉ số trọng tâm |
| :---: | :--- | :--- | :---: | :--- |
| `NFR-01` | Độ chính xác và toàn vẹn dữ liệu tài chính | Mức độ phù hợp chức năng / Tính chính xác chức năng | T, I | Sai số tích lũy tuyệt đối bằng 0.00 VNĐ; 0% trường dùng số thực nhị phân (float/double); 0% vi phạm quy tắc số dư luôn khớp. |
| `NFR-02` | Hiệu năng và độ trễ phản hồi | Hiệu quả vận hành / Hành vi thời gian & Mức sử dụng tài nguyên | D, A | Phản hồi ngay với quy mô dữ liệu cá nhân thông thường; không rò rỉ bộ nhớ khi sử dụng liên tục. |
| `NFR-03` | Độ tin cậy và lưu trữ an toàn | Độ tin cậy / Khả năng chịu lỗi & Khả năng phục hồi | T, I | Ghi tệp an toàn không làm hỏng dữ liệu cũ; tự động sao lưu trước tác vụ thay thế dữ liệu quy mô lớn; không mất dữ liệu khi ngắt nguồn. |
| `NFR-04` | Khả năng bảo trì và kiến trúc hướng đối tượng | Khả năng bảo trì / Tính module hóa, Tính tái sử dụng, Tính phân tích được & Tính kiểm thử được | T, I | Thành phần độc lập về trách nhiệm, không phụ thuộc vòng, giao diện không truy cập trực tiếp dữ liệu. Trạng thái nội bộ được bảo vệ khỏi ghi đè trái phép. Mọi quy tắc nghiệp vụ có kiểm thử đơn vị tương ứng. |
| `NFR-05` | Tính khả dụng và trải nghiệm người dùng | Khả năng tương tác / Tính dễ học, Tính dễ vận hành & Bảo vệ người dùng khỏi lỗi | D, I | Xác nhận thao tác rủi ro; 100% thông báo lỗi định hướng khắc phục. |
| `NFR-06` | Tính tương thích và môi trường chạy | Tính linh hoạt / Tính thích ứng & Tính dễ cài đặt | T, I | Độc lập hệ điều hành trên JVM 17 LTS; mã hóa UTF-8 không BOM; không phụ thuộc thư viện nhị phân gốc phụ thuộc hệ điều hành. |

### 5.1 NFR-01: Độ chính xác và toàn vẹn dữ liệu tài chính

Bảng phân loại đặc tính chất lượng:

| Đặc tính chất lượng | Đặc tính con |
| --- | --- |
| Mức độ phù hợp chức năng | Tính chính xác chức năng |

Mô tả yêu cầu:

1. Quy định kiểu dữ liệu số học:
   - Hệ thống phải dùng kiểu số thập phân chính xác cố định cho các trường dữ liệu số, biến trung gian, phép tính tiền tệ, số dư ví và tổng số dư.
   - Kiểu số này yêu cầu tối thiểu 18 chữ số phần nguyên và 2 chữ số thập phân đối với tiền tệ.
   - Hệ thống không được sử dụng kiểu số thực nhị phân (float/double) trong toàn bộ các cấu trúc dữ liệu nghiệp vụ, các thuật toán tính toán và phần lưu trữ dữ liệu.
   - Quy định này loại bỏ hoàn toàn sai số làm tròn số thực nhị phân trong các phép tính tài chính.
2. Quy tắc độ chính xác thập phân và chế độ làm tròn:
   - Tạo giá trị số: Hệ thống phải tạo giá trị từ dữ liệu nhập dạng thập phân chính xác hoặc từ giá trị nguyên. Hệ thống không được tạo trực tiếp từ giá trị số thực nhị phân (float/double).
   - Quy tắc số chữ số thập phân của giao dịch tiền tệ: Đối với các giao dịch tiền tệ nội tệ (VNĐ), giá trị hiển thị dùng 0 chữ số thập phân (số nguyên đồng) trên giao diện người dùng. Giá trị tính toán, xử lý nghiệp vụ và chuỗi kiểm thử tự động dùng 2 chữ số thập phân.
   - Quy tắc số chữ số thập phân của tỷ lệ phần trăm: Hệ thống phải dùng tối thiểu 4 chữ số thập phân. Quy tắc này áp dụng cho các tỷ lệ phần trăm trung gian trong tính toán theo `BR-FIN-01`.
   - Chế độ làm tròn:
     - Khi thực hiện phép chia, tính tỷ lệ phần trăm, hệ thống phải chỉ định rõ ràng số chữ số thập phân và chế độ làm tròn ngân hàng.
     - Làm tròn ngân hàng là làm tròn đến số chẵn gần nhất khi giá trị nằm giữa hai mức.
     - Không được thực hiện phép chia thiếu số chữ số thập phân và chế độ làm tròn.
   - So sánh số: Hệ thống phải so sánh theo giá trị số thực tế, bỏ qua sai lệch do cách hiển thị số chữ số thập phân khác nhau.
3. Kiểm soát sai số tích lũy:
   - Hệ thống phải bảo đảm sai số sinh ra từ mọi chuỗi phép tính trong từng giao dịch và trên toàn bộ dữ liệu tiền tổng hợp bằng 0.00 VNĐ.
4. Toàn vẹn dữ liệu và bảo toàn số dư theo `BR-FIN-02`:
Tại mọi thời điểm, số dư khả dụng của từng ví luôn thỏa mãn phương trình bảo toàn, với w là ví đang xét và T là thời điểm quan sát:
$$\text{Số dư ví } w \text{ tại } T = \left(\text{Thu} + \text{Chuyển vào} + \text{Điều chỉnh tăng}\right) - \left(\text{Chi} + \text{Chuyển ra} + \text{Điều chỉnh giảm}\right)$$

   Trong đó mỗi hạng tử là tổng các khoản của ví w đến thời điểm T. Điều chỉnh tăng là các khoản điều chỉnh số dư dương, điều chỉnh giảm là các khoản điều chỉnh số dư âm theo `FR-WAL-05`.
5. Cân bằng nguồn - đích theo `BR-FIN-03` (trừ ví nguồn, cộng ví đích):
Hệ thống phải bảo đảm mọi thao tác điều chuyển tiền nội bộ giữa hai ví thỏa mãn phương trình cân bằng nguồn - đích. Phương trình này đòi hỏi số tiền trừ khỏi ví nguồn bằng số tiền cộng vào ví đích, chưa tính phí chuyển. Tổng lượng tiền mặt trong hệ thống được giữ nguyên sau giao dịch chuyển tiền, ngoại trừ chi phí chuyển tiền.

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Sai số tích lũy số học cho phép | Chênh lệch tuyệt đối giữa tổng tiền kỳ vọng và tổng tiền thực tế | 0.00 VNĐ (không có sai lệch) |
| Tỷ lệ xuất hiện kiểu số thực nhị phân (float/double) trong mô hình nghiệp vụ | Số trường dùng kiểu số thực nhị phân chia cho tổng số trường số | 0.00% |
| Tỷ lệ vi phạm quy tắc số dư luôn khớp `BR-FIN-02` | Số giao dịch vi phạm chia cho tổng số giao dịch | 0.00% trên toàn bộ tập dữ liệu kiểm thử |
| Tỷ lệ sai lệch nguồn - đích khi chuyển tiền `BR-FIN-03` (không tính chi phí) | Chênh lệch tuyệt đối giữa tổng tiền trừ khỏi ví nguồn và tổng tiền cộng vào ví đích, chưa tính phí | 0.00 VNĐ |
| Độ chính xác số chữ số thập phân của giao dịch tiền tệ VNĐ | Số chữ số thập phân tính toán và hiển thị | 0 chữ số thập phân (giao diện) / 2 chữ số thập phân (tính toán và kiểm thử) |
| Độ chính xác số chữ số thập phân của tỷ lệ phần trăm | Số chữ số thập phân trong phép tính tỷ lệ phần trăm | Tính toán từ 4 chữ số thập phân trở lên |
| Tỷ lệ chỉ định rõ ràng quy tắc làm tròn trong phép chia | Số phép chia số thập phân chính xác có kèm số chữ số thập phân và quy tắc làm tròn | 100.00% |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Soát xét kỹ thuật | Đánh giá kiểm tra | Rà soát toàn bộ mã nguồn phần nghiệp vụ để phát hiện khai báo kiểu số thực nhị phân (float/double) hoặc tạo số thập phân chính xác trực tiếp từ số thực nhị phân. Kiểm tra các phép chia thiếu số chữ số thập phân và quy tắc làm tròn. |
| Kiểm thử tự động | Kiểm thử tự động | Chạy bộ kiểm thử giá trị biên của phép tính (giao dịch có số tiền 18 chữ số phần nguyên theo `CON-02`, chia tỷ lệ phần trăm số dư ví lẻ). Kiểm thử độ bền chạy với chuỗi giao dịch liên tiếp theo kịch bản kiểm thử tải để đối chiếu số dư tính toán với số dư thực tế. Ngưỡng số lượng giao dịch cụ thể được bổ sung khi có benchmark chính thức. |

### 5.2 NFR-02: Hiệu năng và độ trễ phản hồi

Bảng phân loại đặc tính chất lượng:

| Đặc tính chất lượng | Đặc tính con |
| --- | --- |
| Hiệu quả vận hành | Hành vi thời gian |
| Hiệu quả vận hành | Mức sử dụng tài nguyên |

Mô tả yêu cầu:

1. Độ trễ ghi nhận giao dịch:
Thao tác ghi nhận một giao dịch (thu chi hoặc chuyển tiền) phải cập nhật bộ nhớ và đồng bộ số dư các thực thể liên quan ngay lập tức. Giao diện phải hiển thị phản hồi ngay, không tạo cảm giác trễ khi dùng với dữ liệu cá nhân thông thường.
2. Độ trễ truy vấn và lọc dữ liệu:
Thao tác lọc danh sách giao dịch theo khoảng thời gian, theo danh mục hoặc ví phải phản hồi ngay với quy mô dữ liệu cá nhân thông thường. Thao tác tìm kiếm từ khóa và hiển thị kết quả phân trang cũng phải phản hồi ngay. Người dùng không phải chờ đợi đến mức nhận biết được.
3. Độ trễ tổng hợp báo cáo:
Quá trình tính toán bảng thống kê dòng tiền 12 tháng gần nhất (Thống kê tháng) phải hoàn tất trong thời gian phản hồi ngay. Quá trình tổng hợp cơ cấu chi tiêu 2 cấp danh mục cũng phải hoàn tất trong thời gian phản hồi ngay. Thời gian phản hồi đáp ứng ngưỡng tương tác trực tiếp trên giao diện người dùng theo kịch bản sử dụng.
4. Thời gian khởi động:
Thời gian từ khi chạy ứng dụng đến khi giao diện điều khiển chính sẵn sàng tiếp nhận tương tác phải đủ ngắn. Yêu cầu này nhằm không làm gián đoạn trải nghiệm sử dụng thông thường trên máy tính cá nhân.
5. Mức chiếm dụng bộ nhớ:
Hệ thống không phát sinh rò rỉ bộ nhớ tăng dần khi sử dụng liên tục trong phiên làm việc máy tính cá nhân.

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Cảm nhận độ trễ ghi giao dịch | Đánh giá định tính qua kịch bản sử dụng trực tiếp | Không tạo cảm giác trễ có nhận biết được |
| Cảm nhận độ trễ truy vấn và lọc lịch sử | Đánh giá định tính qua kịch bản sử dụng trực tiếp | Không tạo cảm giác trễ có nhận biết được |
| Cảm nhận độ trễ tổng hợp báo cáo | Đánh giá định tính qua kịch bản sử dụng trực tiếp | Không tạo cảm giác trễ có nhận biết được |
| Ổn định bộ nhớ khi sử dụng liên tục | Theo dõi xu hướng cấp phát bộ nhớ theo thời gian sử dụng | Không phát hiện rò rỉ bộ nhớ tăng dần không được giải phóng |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Trình diễn thao tác | Trực quan thao tác | Thực hiện trực tiếp các thao tác ghi sổ, truy vấn, lọc và tổng hợp báo cáo trên bộ dữ liệu mẫu để quan sát và xác nhận cảm nhận phản hồi ngay. |
| Đo kiểm tài nguyên | Phân tích số liệu | Quan sát xu hướng cấp phát bộ nhớ trong phiên sử dụng kéo dài để phát hiện dấu hiệu rò rỉ bộ nhớ. |

Ghi chú: đây là NFR định tính, chưa có số liệu benchmark thực đo trên môi trường chuẩn cụ thể. Khi nhóm thực hiện đo đạc thực tế, số liệu ngưỡng đo được bằng số (P95, thời gian khởi động, dung lượng bộ nhớ) sẽ được bổ sung. Mỗi số liệu bổ sung đều kèm bằng chứng đo lường và ghi nhận quyết định tương ứng, không suy đoán trước.

### 5.3 NFR-03: Độ tin cậy và lưu trữ an toàn

Bảng phân loại đặc tính chất lượng:

| Đặc tính chất lượng | Đặc tính con |
| --- | --- |
| Độ tin cậy | Khả năng chịu lỗi |
| Độ tin cậy | Khả năng phục hồi |

Mô tả yêu cầu:

1. Ghi tệp an toàn chống hỏng hóc:
Hệ thống ghi tệp dữ liệu theo cách ngăn tệp bị hỏng hoặc thất thoát dữ liệu khi ngắt nguồn, tắt hệ điều hành đột ngột hoặc chương trình bị buộc dừng. Tại mọi thời điểm, tệp dữ liệu chính chỉ có thể là bản cũ nguyên vẹn hoặc bản mới đầy đủ:
   - Không ghi dở: Nội dung cập nhật chỉ trở thành tệp dữ liệu chính thức khi đã được ghi đầy đủ.
   - Lưu bền xuống đĩa: Dữ liệu của lần ghi đã hoàn tất phải nằm trên đĩa lưu trữ, không chỉ trong vùng nhớ tạm, để không mất khi mất nguồn điện.
   - Bảo toàn bản cũ: Nếu không bảo đảm được việc thay thế trọn vẹn, hệ thống giữ lại bản sao của tệp gốc trước khi thay thế.
2. Tự động sao lưu dữ liệu:
Hệ thống tự động tạo bản sao lưu dữ liệu có gắn nhãn thời gian theo định dạng `backup_YYYYMMDD_HHMMSS.json` tại thư mục riêng `backups/` theo `CON-06`. Bản sao lưu này được tạo trước các tác vụ có nguy cơ thay thế dữ liệu quy mô lớn (phục hồi dữ liệu từ tệp nhập ngoài theo `UC-DAT-02`, đặt lại hệ thống).
3. Phục hồi toàn vẹn dữ liệu:
   - Khi đọc dữ liệu vào từ tệp lưu trữ hoặc bản sao lưu, hệ thống kiểm tra dữ liệu còn nguyên vẹn trước khi cập nhật vào bộ nhớ làm việc. Dữ liệu nguyên vẹn nghĩa là đúng lược đồ cấu trúc JSON, mã liên kết tới danh mục hợp lệ, quy tắc số dư luôn khớp.
   - Trường hợp tệp sai cấu trúc JSON, có mã liên kết tới danh mục không hợp lệ hoặc sai lệch quy tắc số dư luôn khớp, toàn bộ lần đọc dữ liệu bị hủy bỏ.
   - Hệ thống giữ nguyên trạng thái dữ liệu trước đó và hiển thị thông báo lỗi chi tiết.
4. Kiểm soát và xử lý lỗi đọc ghi tệp:
Mọi thao tác đọc ghi hệ thống tệp phải kiểm soát và xử lý 100.00% ngoại lệ đọc ghi dữ liệu. Trường hợp ổ đĩa đầy hoặc thư mục bị khóa quyền ghi, hệ thống giữ nguyên trạng tệp dữ liệu gốc và thông báo lỗi rõ ràng tới Người dùng.

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Tỷ lệ tệp dữ liệu chính bị hỏng khi chương trình bị ngắt đột ngột | Số tệp bị hỏng chia cho tổng số lần ghi bị ngắt | 0.00% |
| Tỷ lệ giữ nguyên dữ liệu lịch sử đã lưu xong | Tỷ lệ bản ghi giao dịch còn nguyên sau sự cố chương trình bị ngắt | 100.00% |
| Tỷ lệ tác vụ thay thế dữ liệu quy mô lớn có bản sao lưu tạo trước đó | Số tác vụ có bản sao lưu tạo trước đó chia tổng số tác vụ thay thế dữ liệu quy mô lớn | 100.00% |
| Dữ liệu còn nguyên vẹn khi phục hồi từ bản sao lưu | Kết quả so khớp cấu trúc JSON và quy tắc số dư luôn khớp sau phục hồi | So khớp 100%, không có sai lệch |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Kiểm thử giả lập lỗi | Kiểm thử tự động | Mô phỏng quá trình ghi tệp dữ liệu dung lượng lớn và buộc ngắt chương trình đột ngột ngay khi đang ghi tệp. Sau đó khởi động lại ứng dụng để xác minh tệp dữ liệu chính vẫn nguyên vẹn và đọc dữ liệu thành công. |
| Soát xét kỹ thuật | Đánh giá kiểm tra | Rà soát mã nguồn phần lưu trữ bảo đảm việc ghi tệp an toàn được thực hiện đúng. Tệp chính không bao giờ ở trạng thái ghi dở, dữ liệu đã ghi xong nằm trên đĩa và mọi lỗi đọc ghi được xử lý đầy đủ. |

### 5.4 NFR-04: Khả năng bảo trì và kiến trúc hướng đối tượng

Bảng phân loại đặc tính chất lượng:

| Đặc tính chất lượng | Đặc tính con |
| --- | --- |
| Khả năng bảo trì | Tính module hóa |
| Khả năng bảo trì | Tính tái sử dụng |
| Khả năng bảo trì | Tính phân tích được |
| Khả năng bảo trì | Tính kiểm thử được |

Mô tả yêu cầu:

1. Phân định trách nhiệm theo thành phần độc lập:
Hệ thống tổ chức thành các thành phần độc lập về trách nhiệm. Thành phần giao diện người dùng không truy cập trực tiếp dữ liệu lưu trữ mà làm việc qua thành phần xử lý nghiệp vụ.
   - Phụ thuộc một chiều: Thành phần bên ngoài phụ thuộc vào thành phần bên trong; không có phụ thuộc ngược chiều hoặc phụ thuộc vòng giữa các mô-đun.
   - Đơn trách nhiệm mô-đun: Mỗi mô-đun chỉ đảm nhiệm một trách nhiệm duy nhất. Các thực thể nghiệp vụ (Người dùng, Ví, Giao dịch thu chi, Giao dịch chuyển tiền, Danh mục) chỉ quản lý trạng thái nghiệp vụ và các bất biến nghiệp vụ của riêng mình. Các thực thể này tách biệt hoàn toàn khỏi phần hiển thị giao diện và cách lưu trữ dữ liệu.
2. Bảo vệ trạng thái nội bộ khỏi thay đổi trái phép:
   - Dữ liệu trạng thái nội bộ của các thực thể chỉ được truy xuất qua giao diện đọc có kiểm soát hoặc thao tác nghiệp vụ chuyên biệt. Hệ thống không được cho phép đặt số dư ví bằng phép gán trực tiếp hoặc gán đè trực tiếp trạng thái giao dịch, ngoài quy trình ghi nhận dòng tiền.
   - Đối với dữ liệu dạng danh sách hoặc tập hợp, hệ thống bảo đảm giá trị trả về không thể bị sửa đổi từ bên ngoài phạm vi kiểm soát nghiệp vụ.
3. Khả năng mở rộng không phá vỡ hành vi hiện có:
   - Xử lý đồng nhất các ví: Cấu trúc dữ liệu ví phải cho phép công cụ tổng hợp số dư ví và công cụ tính tỷ lệ từng ví xử lý đồng nhất. Cả hai công cụ này áp dụng logic tính toán chung trên mọi ví.
   - Cấu trúc danh mục cho phép mở rộng danh mục mới mà không cần chỉnh sửa logic tính toán số dư hiện có.
4. Độ bao phủ kiểm thử tự động và kiểm soát độ phức tạp mã nguồn:
   - Hệ thống phải có kiểm thử đơn vị tự động độc lập công nghệ cho toàn bộ quy tắc nghiệp vụ, thuật toán tính số dư và công thức tính tỷ lệ. Kiểm thử bao gồm các trường hợp biên của mọi giới hạn trong quy tắc (ví dụ số tiền âm, bằng 0, vượt hạn mức; tên rỗng, dài nhất, vượt độ dài tối đa).
   - Mỗi thao tác nghiệp vụ chỉ đảm nhiệm một luồng xử lý rõ ràng, dễ kiểm thử độc lập, tránh lồng ghép nhiều nhánh điều kiện trong cùng một thao tác.

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Phạm vi kiểm thử quy tắc nghiệp vụ | Tỷ lệ quy tắc nghiệp vụ có ca kiểm thử tương ứng | 100% quy tắc nghiệp vụ có tối thiểu 1 ca kiểm thử |
| Kiểm thử trường hợp biên | Có ca kiểm thử cho các giá trị ở hai phía mỗi giới hạn của quy tắc | Có đủ cho mọi quy tắc nghiệp vụ có giới hạn về số hoặc độ dài |
| Mức độ phụ thuộc vòng giữa các mô-đun | Số vòng phụ thuộc giữa các mô-đun | 0 vòng phụ thuộc |
| Tỷ lệ thực thể bảo vệ trạng thái nội bộ khỏi ghi đè trái phép | Thực thể không cho phép gán đè trực tiếp trạng thái từ bên ngoài | 100.00% |
| Tỷ lệ dữ liệu tập hợp trả về không thể bị sửa đổi từ bên ngoài | Tập hợp không thể sửa đổi khi truy xuất | 100.00% |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Kiểm thử tự động | Kiểm thử tự động | Chạy bộ kiểm thử đơn vị cho từng quy tắc nghiệp vụ, bao gồm trường hợp biên, và xác nhận kết quả PASS. |
| Soát xét kỹ thuật kiến trúc | Đánh giá kiểm tra | Rà soát mã nguồn xác nhận ba điều. Thứ nhất, không có phụ thuộc vòng giữa các mô-đun. Thứ hai, thành phần chứa quy tắc nghiệp vụ không phụ thuộc thành phần bên ngoài. Thứ ba, trạng thái nội bộ của thực thể được bảo vệ khỏi ghi đè trực tiếp. |

### 5.5 NFR-05: Tính khả dụng và trải nghiệm người dùng

Bảng phân loại đặc tính chất lượng:

| Đặc tính chất lượng | Đặc tính con |
| --- | --- |
| Khả năng tương tác | Tính dễ học |
| Khả năng tương tác | Tính dễ vận hành |
| Khả năng tương tác | Bảo vệ người dùng khỏi lỗi |

Mô tả yêu cầu:

1. Tinh giản thao tác nhập liệu:
   - Tự động điền giá trị mặc định: Biểu mẫu ghi nhận thu chi và chuyển tiền tự động điền ngày hiện tại của hệ thống. Ô chọn ví thanh toán tự động chọn ví được sử dụng gần nhất. Khi chưa có giao dịch nào, ô này chọn ví tiền mặt có sẵn.
   - Tự động định vị con trỏ nhập liệu: Khi mở hộp thoại ghi nhận giao dịch, con trỏ tự động tập trung vào ô nhập số tiền để sẵn sàng nhận số liệu.
   - Lọc danh mục theo ngữ cảnh: Hệ thống tự động lọc danh mục thu chi theo phân loại ngữ cảnh để loại bỏ lỗi phân loại sai. Khi chọn giao dịch Thu nhập, chỉ hiển thị các danh mục thuộc loại Thu nhập. Khi chọn Chi tiêu, chỉ hiển thị các danh mục thuộc loại Chi tiêu. Danh mục hiển thị chỉ gồm danh mục đang dùng. Riêng danh mục của phí giao dịch luôn thuộc loại Chi tiêu, kể cả khi giao dịch gốc là Thu nhập.
   - Định dạng số tiền theo thời gian thực: Khi Người dùng nhập các chữ số vào ô tiền tệ, hệ thống tự động định dạng phân tách hàng nghìn bằng dấu phẩy. Ví dụ: nhập "1500000" tự động hiển thị "1,500,000 VNĐ". Cách này hạn chế sai sót khi nhập liệu.
2. Bảo vệ người dùng khỏi lỗi và xác nhận thao tác rủi ro:
   - Cảnh báo xác nhận trước thao tác rủi ro cao: Hệ thống phải hiển thị hộp thoại cảnh báo rủi ro khi xóa giao dịch hoặc lưu trữ ví còn số dư. Thao tác nhập tệp thay thế dữ liệu cũng yêu cầu xác nhận. Hộp thoại yêu cầu Người dùng xác nhận rõ ràng trước khi thực hiện.
   - Kiểm tra dữ liệu hợp lệ ngay khi nhập: Hệ thống kiểm tra định dạng số tiền và điều kiện cần có ngay khi nhập liệu, ngăn chặn việc gửi yêu cầu không hợp lệ.
3. Thông báo lỗi định hướng khắc phục:
   - Toàn bộ thông báo lỗi kiểm tra dữ liệu đầu vào và vi phạm quy tắc nghiệp vụ bắt buộc được thể hiện bằng ngôn ngữ tiếng Việt tự nhiên, ngắn gọn. Thông báo chỉ rõ nguyên nhân gây lỗi và đưa ra hướng xử lý khắc phục cụ thể. Ví dụ: Khi số dư ví không đủ chi trả khoản chi, thông báo hiển thị: "Số dư ví MB Bank hiện tại là 150,000 VNĐ, không đủ để chi trả khoản chi 200,000 VNĐ. Vui lòng giảm số tiền chi tiêu hoặc chuyển tiền từ ví khác sang."
   - Hệ thống không được hiển thị thông báo lỗi kỹ thuật thuần túy, vết ngăn xếp ngoại lệ hoặc mã lỗi hệ thống chưa qua xử lý lên giao diện người dùng. Hệ thống phải ánh xạ mọi ngoại lệ kỹ thuật sang thông điệp trong danh mục bảng mã lỗi chuẩn của hệ thống (`ERR-...`).

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Tỷ lệ thông báo lỗi có định hướng giải pháp khắc phục | Số thông báo lỗi có hướng dẫn cụ thể | 100.00% nằm trong danh mục bảng mã lỗi chuẩn |
| Tỷ lệ hiển thị lỗi ngoại lệ chưa bắt hoặc stack trace | Tỷ lệ thông báo kỹ thuật rò rỉ ra giao diện | 0.00% |
| Mức độ thuận tiện khi làm quen hệ thống cho người dùng mới | Đánh giá định tính qua quan sát thao tác tạo ví và ghi giao dịch đầu tiên | Người dùng thao tác thuận tiện, không gặp trở ngại; số liệu thời gian cụ thể sẽ được ghi nhận khi có benchmark chính thức |
| Tỷ lệ thao tác xóa hoặc thay thế dữ liệu có cảnh báo xác nhận | Số thao tác xóa hoặc thay thế dữ liệu có hộp thoại xác nhận | 100.00% |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Trình diễn thao tác | Trực quan thao tác | Thực hiện kiểm thử quan sát thao tác người dùng trên nhóm thử nghiệm với 3 kịch bản sử dụng cơ bản (tạo ví, ghi chi tiêu, chuyển tiền). Mục đích là đánh giá mức độ thuận tiện trong quá trình làm quen hệ thống. |
| Soát xét kỹ thuật thông báo lỗi | Đánh giá kiểm tra | Rà soát toàn bộ bảng thông điệp lỗi trong mã nguồn để kiểm tra tính đầy đủ của ngôn ngữ tiếng Việt và hướng dẫn xử lý khắc phục. Rà soát mã nguồn bảo đảm không để lộ vết ngăn xếp lỗi hệ thống ra màn hình người dùng. |

### 5.6 NFR-06: Tính tương thích và môi trường chạy

Bảng phân loại đặc tính chất lượng:

| Đặc tính chất lượng | Đặc tính con |
| --- | --- |
| Tính linh hoạt | Tính thích ứng |
| Tính linh hoạt | Tính dễ cài đặt |

Mô tả yêu cầu:

1. Môi trường chạy tiêu chuẩn:
Ứng dụng vận hành trên nền tảng máy ảo Java tương thích phiên bản 17 LTS trở lên (`CON-01`) để bảo đảm tính tương thích lâu dài và hỗ trợ xử lý thời gian theo lịch dương.
2. Độc lập nền tảng hệ điều hành:
Ứng dụng đóng gói độc lập và chạy đồng nhất trên mọi hệ điều hành máy tính cá nhân có JVM tương thích (Windows, macOS, Linux). Ứng dụng không cần thay đổi mã nguồn hoặc biên dịch lại theo từng hệ điều hành.
3. Thao tác đường dẫn hệ thống tệp:
Không được viết cứng ký tự phân cách thư mục của từng hệ điều hành (như dấu `\` trên Windows hay `/` trên Linux). Mọi thao tác đường dẫn tệp phải dùng đối tượng đường dẫn trừu tượng độc lập hệ điều hành để chạy được trên nhiều nền tảng.
4. Mã hóa ký tự UTF-8 không BOM:
Mọi thao tác đọc, ghi tệp dữ liệu cục bộ, tệp xuất dữ liệu JSON và xuất báo cáo phải chỉ định rõ ràng bảng mã UTF-8 không BOM. Quy định này bảo đảm dữ liệu tiếng Việt có dấu lưu trữ và hiển thị chính xác. Điều này cũng tránh lỗi mã hóa ký tự bất kể bảng mã mặc định của hệ điều hành máy tính là gì.
5. Không phụ thuộc thư viện nhị phân ngoại lai:
Hệ thống không được dùng thư viện nhị phân phụ thuộc nền tảng. Quy định này giúp đóng gói và phân phối độc lập thành một gói ứng dụng chạy được, không yêu cầu cài đặt phụ thuộc bổ sung theo từng hệ điều hành.

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu |
| --- | --- | --- |
| Tỷ lệ vượt qua bộ kiểm thử tự động trên các hệ điều hành mục tiêu (Windows, macOS, Linux) | Tỷ lệ ca kiểm thử đạt kết quả PASS trên từng nền tảng đã kiểm thử | 100.00% |
| Tỷ lệ thao tác đọc ghi tệp chỉ định rõ ràng bảng mã UTF-8 | Số thao tác đọc ghi tệp có chỉ định bảng mã UTF-8 chia cho tổng số thao tác đọc ghi tệp | 100.00% |
| Tỷ lệ lỗi vỡ phông chữ tiếng Việt | Số trường hợp hiển thị sai ký tự có dấu phát hiện được | 0 lỗi phông chữ |
| Số lượng thư viện nhị phân phụ thuộc nền tảng | Số thư viện nhị phân gốc phụ thuộc hệ điều hành sử dụng | 0 thư viện |
| Phiên bản Java hỗ trợ tối thiểu | Phiên bản JDK / JRE yêu cầu | Java SE 17 LTS |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp | Loại hình | Quy trình |
| --- | --- | --- |
| Kiểm thử tương thích đa nền tảng | Kiểm thử tự động | Chạy bộ kiểm thử đơn vị tự động trên từng môi trường hệ điều hành mục tiêu để bảo đảm kết quả chạy thử giống nhau giữa các hệ điều hành. |
| Soát xét mã nguồn I/O tĩnh | Đánh giá kiểm tra | Rà soát mã nguồn tĩnh đối với toàn bộ thao tác đọc ghi tệp để bảo đảm luôn chỉ định bảng mã UTF-8 và sử dụng đối tượng đường dẫn trừu tượng. |
