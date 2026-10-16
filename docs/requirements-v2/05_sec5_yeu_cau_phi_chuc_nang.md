## 5. YÊU CẦU PHI CHỨC NĂNG

Mục này xác lập 6 yêu cầu phi chức năng (NFR-01 đến NFR-06) nêu các chỉ số chất lượng phần mềm của Hệ thống Quản lý Tài chính Cá nhân bằng số đo được. Các yêu cầu này đặt các ngưỡng kỹ thuật kiểm chứng được bằng số liệu qua công cụ tự động.

Mỗi yêu cầu phi chức năng được trình bày thống nhất theo 4 thành phần kỹ thuật:

1. Bảng phân loại thuộc tính chất lượng: Xác định đặc tính chất lượng chính và phân nhóm thuộc tính con.
2. Mô tả yêu cầu kỹ thuật: Quy định phạm vi kỹ thuật, các mức quy định chuẩn và giải pháp kiến trúc bảo đảm chất lượng.
3. Bảng chỉ số đo và ngưỡng nghiệm thu: Xác lập công thức tính toán, đơn vị đo lường và ngưỡng chấp thuận kỹ thuật.
4. Bảng phương pháp và công cụ kiểm chứng: Xác định phương thức nghiệm thu theo bốn mã ký hiệu `I` (Soát xét kỹ thuật), `T` (Kiểm thử tự động), `D` (Trình diễn thao tác), `A` (Phân tích số liệu). Bảng nêu thêm môi trường công cụ tương ứng.

Bảng tổng hợp 6 yêu cầu phi chức năng của hệ thống:

| Mã yêu cầu | Tên yêu cầu chất lượng | Đặc tính chất lượng | Chỉ số kỹ thuật trọng tâm |
| :---: | :--- | :--- | :--- |
| NFR-01 | Độ chính xác và toàn vẹn dữ liệu tài chính | Mức độ phù hợp chức năng / Tính chính xác chức năng | Sai số tích lũy $\lvert \Delta S \rvert = 0.00$ VNĐ; 0% trường dùng số thực nhị phân (float/double); 0% vi phạm quy tắc số dư luôn khớp. |
| NFR-02 | Hiệu năng và độ trễ phản hồi | Hiệu quả vận hành / Hành vi thời gian & Mức sử dụng tài nguyên | Phản hồi ngay với quy mô dữ liệu cá nhân thông thường; không rò rỉ bộ nhớ khi sử dụng liên tục. |
| NFR-03 | Độ tin cậy và lưu trữ an toàn | Độ tin cậy / Khả năng chịu lỗi & Khả năng phục hồi | Ghi tệp an toàn không làm hỏng dữ liệu cũ; giữ bản sao lưu xoay vòng; không mất dữ liệu khi ngắt nguồn. |
| NFR-04 | Khả năng bảo trì và kiến trúc hướng đối tượng | Khả năng bảo trì / Tính module hóa, Tính tái sử dụng, Tính phân tích được & Tính kiểm thử được | Thành phần độc lập về trách nhiệm, không phụ thuộc vòng, giao diện không truy cập trực tiếp dữ liệu; trạng thái nội bộ được bảo vệ khỏi ghi đè trái phép; mọi quy tắc nghiệp vụ có kiểm thử đơn vị tương ứng. |
| NFR-05 | Tính khả dụng và trải nghiệm người dùng | Interaction capability / Tính dễ học, Tính dễ vận hành & Bảo vệ người dùng khỏi lỗi | Xác nhận thao tác rủi ro; 100% thông báo lỗi định hướng khắc phục. |
| NFR-06 | Tính tương thích và môi trường chạy | Flexibility / Tính thích ứng & Tính dễ cài đặt | Độc lập hệ điều hành trên JVM 17 LTS; mã hóa UTF-8 không BOM; không phụ thuộc thư viện nhị phân gốc phụ thuộc hệ điều hành. |

---

### 5.1 NFR-01: Độ chính xác và toàn vẹn dữ liệu tài chính

Bảng phân loại thuộc tính chất lượng:

| Đặc tính chất lượng chính | Phân nhóm thuộc tính con |
| --- | --- |
| Mức độ phù hợp chức năng | Tính chính xác chức năng |

Mô tả yêu cầu kỹ thuật:

1. Quy định kiểu dữ liệu số học:
  - Các trường dữ liệu số, biến trung gian và phép tính tiền tệ, số dư ví, lãi suất, số đơn vị tài sản đầu tư, đơn giá cùng tổng tài sản bắt buộc dùng kiểu số thập phân chính xác cố định.
  - Kiểu số này yêu cầu tối thiểu 18 chữ số phần nguyên và 2 chữ số thập phân đối với tiền tệ, số đơn vị và đơn giá theo quy tắc số chữ số thập phân của tài sản đầu tư ở dưới.
  - Hệ thống nghiêm cấm sử dụng kiểu số thực nhị phân (float/double) trong toàn bộ các cấu trúc dữ liệu nghiệp vụ, các thuật toán tính toán và phần lưu trữ dữ liệu.
  - Quy định này loại bỏ hoàn toàn sai số làm tròn số thực nhị phân trong các phép tính tài chính.
2. Quy tắc độ chính xác thập phân và chế độ làm tròn:
  - Tạo giá trị số: Bắt buộc tạo từ chuỗi ký tự thập phân chính xác hoặc từ kiểu số nguyên. Nghiêm cấm tạo trực tiếp từ giá trị số thực nhị phân (float/double).
  - Quy tắc số chữ số thập phân của giao dịch tiền tệ: Đối với các giao dịch tiền tệ nội tệ (VNĐ), giá trị hiển thị dùng 0 chữ số thập phân (số nguyên đồng). Giá trị lưu trữ và xử lý số học nội bộ dùng mức 2 (2 chữ số thập phân).
  - Quy tắc số chữ số thập phân của tài sản đầu tư và tỷ lệ: Đối với số đơn vị tài sản đầu tư và đơn giá trên một đơn vị, bắt buộc dùng tối thiểu 4 chữ số thập phân trong tính toán; số lượng do người dùng nhập có tối đa 4 chữ số thập phân và đại lượng dẫn xuất giữ đủ chữ số theo `BR-FIN-06` mục 3.6.
  - Chế độ làm tròn:
    - Khi thực hiện phép chia, quy đổi lãi suất ngày ($annualRate / 365$) hoặc tính chi phí mua, chi phí bán, hệ thống phải chỉ định rõ ràng số chữ số thập phân và chế độ làm tròn ngân hàng.
    - Làm tròn ngân hàng là làm tròn đến số chẵn gần nhất khi giá trị nằm giữa hai mức.
    - Cách này loại bỏ sai lệch dồn dần.
    - Tiền lãi thương mại khi tất toán là ngoại lệ và dùng chế độ làm tròn nửa lên (làm tròn lên mức cao hơn khi giá trị nằm đúng giữa hai mức).
    - Không được thực hiện phép chia thiếu số chữ số thập phân và chế độ làm tròn.
  - So sánh số: Bắt buộc so sánh theo giá trị số thực tế, bỏ qua sai lệch do cách hiển thị số chữ số thập phân khác nhau.
3. Kiểm soát sai số tích lũy:
  - Sai số sinh ra từ mọi chuỗi phép tính trong từng giao dịch và trên toàn bộ dữ liệu tiền tổng hợp bắt buộc bằng 0.00 VNĐ.
  - Khi chia một giá trị tổng thành các phần lẻ, hệ thống tự động bù sai số làm tròn vào phần tử có giá trị lớn nhất nếu sai số không vượt quá 0.01 đơn vị tiền tệ.
  - Riêng phân bổ cho các lệnh mua của tài sản đầu tư theo `BR-FIN-06`, `BR-FIN-07` và `BR-FIN-08`, phần dư dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất theo `QD-194`.
  - Trường hợp sai số vượt ngưỡng 0.01 đơn vị tiền tệ, hệ thống hủy thao tác và báo lỗi `ERR-FIN-003`.
4. Toàn vẹn dữ liệu và bảo toàn số dư theo `BR-FIN-02`:
Tại mọi thời điểm trạng thái $t$, số dư khả dụng của từng ví tiền luôn thỏa mãn phương trình bảo toàn:
$$WalletBalance_{wallet}(t) = WalletBalance_{wallet}(0) + \sum Income + \sum TransferIn + \sum Borrow + \sum Collect + \sum SavingPayout + \sum InvSell + \sum InvEvent - \left(\sum Expense + \sum TransferOut + \sum Repay + \sum Lend + \sum SavingPrincipal + \sum InvBuy\right) \pm \sum Adjustment$$
5. Cân bằng hai vế theo `BR-FIN-03` (trừ ví nguồn, cộng ví đích):
Mọi thao tác điều chuyển tiền nội bộ giữa hai ví bắt buộc thỏa mãn phương trình cân bằng hai vế ($Debit_{src} = Credit_{dst}$), giữ nguyên tổng lượng tiền mặt trong hệ thống sau giao dịch chuyển khoản.

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu chấp thuận |
| --- | --- | --- |
| Sai số tích lũy số học cho phép ($\lvert \Delta S \rvert$) | $\lvert \Delta S \rvert = \lvert S_{expected} - S_{actual} \rvert$ | $0.00$ VNĐ (không có sai lệch) |
| Tỷ lệ xuất hiện kiểu số thực nhị phân (float/double) trong mô hình nghiệp vụ | $Count(\text{Binary Floating Point}) / Count(\text{Total Numeric Fields})$ | $0.00\%$ |
| Tỷ lệ vi phạm quy tắc số dư luôn khớp `BR-FIN-02` | $Count(\text{Violations}) / Count(\text{Transactions})$ | $0.00\%$ trên toàn bộ tập dữ liệu kiểm thử |
| Tỷ lệ sai lệch hai vế khi chuyển khoản `BR-FIN-03` | $\lvert \sum Debit - \sum Credit \rvert$ | $0.00$ VNĐ |
| Độ chính xác số chữ số thập phân của giao dịch tiền tệ VNĐ | Số chữ số thập phân lưu trữ và hiển thị | 0 chữ số thập phân (hiển thị) / 2 chữ số thập phân (lưu trữ nội bộ) |
| Độ chính xác số chữ số thập phân của tài sản đầu tư và đơn giá | Số chữ số thập phân số đơn vị tài sản đầu tư và đơn giá | Tính toán từ $\ge 4$ chữ số thập phân; số lượng người dùng nhập tối đa 4 chữ số thập phân |
| Tỷ lệ chỉ định rõ ràng quy tắc làm tròn trong phép chia | Số phép chia số thập phân chính xác có kèm số chữ số thập phân và quy tắc làm tròn | $100.00\%$ |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp nghiệm thu | Phương pháp kiểm chứng | Quy trình kiểm chứng |
| --- | --- | --- |
| Soát xét kỹ thuật | Đánh giá kiểm tra | Rà soát toàn bộ mã nguồn phần nghiệp vụ để phát hiện khai báo kiểu số thực nhị phân (float/double) hoặc tạo số thập phân chính xác trực tiếp từ số thực nhị phân; kiểm tra các phép chia thiếu số chữ số thập phân và quy tắc làm tròn. |
| Kiểm thử tự động | Kiểm thử tự động | Chạy bộ kiểm thử giá trị biên của phép tính (giao dịch giá trị lớn $10^{14}$ VNĐ, chia tỷ lệ lãi suất ngày lẻ, chia số đơn vị tài sản đầu tư lẻ). Kiểm thử độ bền chạy với chuỗi 100,000 giao dịch liên tiếp để đối chiếu số dư tính toán với số dư thực tế. |

---

### 5.2 NFR-02: Hiệu năng và độ trễ phản hồi

Bảng phân loại thuộc tính chất lượng:

| Đặc tính chất lượng chính | Phân nhóm thuộc tính con |
| --- | --- |
| Hiệu quả vận hành | Hành vi thời gian |
| Hiệu quả vận hành | Mức sử dụng tài nguyên |

Mô tả yêu cầu kỹ thuật:

1. Độ trễ ghi nhận giao dịch:
Thao tác ghi nhận một giao dịch (thu chi, chuyển tiền, công nợ hoặc tài sản đầu tư) phải cập nhật bộ nhớ và đồng bộ số dư các thực thể liên quan ngay lập tức. Giao diện phải hiển thị phản hồi ngay, không tạo cảm giác trễ khi dùng với dữ liệu cá nhân thông thường.
2. Độ trễ truy vấn và lọc dữ liệu:
Thao tác lọc danh sách giao dịch theo khoảng thời gian, theo danh mục hoặc ví, tìm kiếm từ khóa và hiển thị kết quả phân trang phải phản hồi ngay với quy mô dữ liệu cá nhân thông thường. Việc này không yêu cầu người dùng chờ đợi có nhận biết được.
3. Độ trễ tổng hợp báo cáo:
Quá trình tính toán bảng thống kê dòng tiền 12 tháng gần nhất (Thống kê tháng), tổng hợp cơ cấu chi tiêu 2 cấp danh mục và tính toán tài sản ròng phải hoàn tất trong thời gian phản hồi ngay. Thời gian này phù hợp thao tác tương tác trực tiếp trên giao diện.
4. Thời gian khởi động:
Thời gian từ khi chạy ứng dụng đến khi giao diện điều khiển chính sẵn sàng tiếp nhận tương tác phải đủ ngắn để không làm gián đoạn trải nghiệm sử dụng thông thường trên máy tính cá nhân.
5. Mức chiếm dụng bộ nhớ:
Hệ thống phải vận hành ổn định trong phạm vi tài nguyên bộ nhớ hợp lý của một ứng dụng desktop cá nhân, không bị rò rỉ bộ nhớ khi sử dụng liên tục.

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu chấp thuận |
| --- | --- | --- |
| Cảm nhận độ trễ ghi sổ giao dịch | Đánh giá định tính qua kịch bản sử dụng trực tiếp | Không tạo cảm giác trễ có nhận biết được |
| Cảm nhận độ trễ truy vấn và lọc lịch sử | Đánh giá định tính qua kịch bản sử dụng trực tiếp | Không tạo cảm giác trễ có nhận biết được |
| Cảm nhận độ trễ tổng hợp báo cáo | Đánh giá định tính qua kịch bản sử dụng trực tiếp | Không tạo cảm giác trễ có nhận biết được |
| Ổn định bộ nhớ khi sử dụng liên tục | Theo dõi xu hướng cấp phát bộ nhớ theo thời gian sử dụng | Không phát hiện rò rỉ bộ nhớ tăng dần không được giải phóng |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp nghiệm thu | Phương pháp kiểm chứng | Quy trình kiểm chứng |
| --- | --- | --- |
| Trình diễn thao tác | Trực quan thao tác | Thực hiện trực tiếp các thao tác ghi sổ, truy vấn, lọc và tổng hợp báo cáo trên bộ dữ liệu mẫu để quan sát và xác nhận cảm nhận phản hồi ngay. |
| Đo kiểm tài nguyên | Phân tích số liệu | Quan sát xu hướng cấp phát bộ nhớ trong phiên sử dụng kéo dài để phát hiện dấu hiệu rò rỉ bộ nhớ. |

Ghi chú: đây là NFR định tính, chưa có số liệu benchmark thực đo trên môi trường chuẩn cụ thể. Khi nhóm thực hiện đo đạc thực tế, số liệu ngưỡng đo được bằng số (P95, thời gian khởi động, dung lượng bộ nhớ) sẽ được bổ sung kèm bằng chứng đo lường và ghi nhận quyết định tương ứng, không suy đoán trước.

---

### 5.3 NFR-03: Độ tin cậy và lưu trữ an toàn

Bảng phân loại thuộc tính chất lượng:

| Đặc tính chất lượng chính | Phân nhóm thuộc tính con |
| --- | --- |
| Độ tin cậy | Khả năng chịu lỗi |
| Độ tin cậy | Khả năng phục hồi |

Mô tả yêu cầu kỹ thuật:

1. Ghi tệp an toàn chống hỏng hóc:
Hệ thống ghi tệp dữ liệu theo cách ngăn tệp bị hỏng hoặc thất thoát dữ liệu khi ngắt nguồn, tắt hệ điều hành đột ngột hoặc chương trình bị buộc dừng. Tại mọi thời điểm, tệp dữ liệu chính chỉ có thể là bản cũ nguyên vẹn hoặc bản mới đầy đủ:
  - Không ghi dở: Nội dung cập nhật chỉ trở thành tệp dữ liệu chính thức khi đã được ghi đầy đủ.
  - Lưu bền xuống đĩa: Dữ liệu của lần ghi đã hoàn tất phải nằm trên đĩa lưu trữ, không chỉ trong vùng nhớ tạm, để không mất khi mất nguồn điện.
  - Bảo toàn bản cũ: Nếu không bảo đảm được việc thay thế trọn vẹn, hệ thống giữ lại bản sao của tệp gốc trước khi thay thế.
2. Tự động sao lưu và xoay vòng dữ liệu:
Hệ thống tự động tạo bản sao lưu dữ liệu có gắn nhãn thời gian theo định dạng `backup_YYYYMMDD_HHMMSS.json` tại thư mục riêng `backups/` trong các trường hợp:
  - Trước khi thực hiện các tác vụ có nguy cơ ghi đè dữ liệu quy mô lớn (phục hồi dữ liệu từ tệp nhập ngoài theo `UC-DAT-02`, đặt lại hệ thống).
  - Giữ xoay vòng tối thiểu 5 bản sao lưu gần nhất; tự động thanh lý các bản sao lưu cũ vượt quá giới hạn để giải phóng dung lượng lưu trữ.
3. Phục hồi toàn vẹn dữ liệu:
  - Khi đọc dữ liệu vào từ tệp lưu trữ hoặc bản sao lưu, hệ thống kiểm tra dữ liệu còn nguyên vẹn trước khi cập nhật vào bộ nhớ làm việc. Dữ liệu nguyên vẹn nghĩa là đúng lược đồ cấu trúc JSON, mã liên kết tới danh mục hợp lệ, quy tắc số dư luôn khớp.
  - Trường hợp tệp sai cấu trúc JSON, có mã liên kết tới danh mục không hợp lệ hoặc sai lệch quy tắc số dư luôn khớp, toàn bộ lần đọc dữ liệu bị hủy bỏ.
  - Hệ thống giữ nguyên trạng thái dữ liệu trước đó và hiển thị thông báo lỗi chi tiết.
4. Kiểm soát và xử lý lỗi đọc ghi tệp:
Mọi thao tác đọc ghi hệ thống tệp phải kiểm soát và xử lý đầy đủ lỗi đọc ghi dữ liệu. Trường hợp ổ đĩa đầy hoặc thư mục bị khóa quyền ghi, hệ thống giữ nguyên trạng tệp dữ liệu gốc và thông báo lỗi rõ ràng tới người dùng.

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu chấp thuận |
| --- | --- | --- |
| Tỷ lệ tệp dữ liệu chính bị hỏng khi chương trình bị ngắt đột ngột | $Count(\text{Corrupted Files}) / Count(\text{Interrupted Writes})$ | $0.00\%$ |
| Tỷ lệ giữ nguyên dữ liệu lịch sử đã lưu xong | Tỷ lệ bản ghi giao dịch còn nguyên sau sự cố chương trình bị ngắt | $100.00\%$ |
| Số lượng bản sao lưu tự động tối thiểu được giữ ($N_{backups}$) | Số bản ghi sao lưu gần nhất trong thư mục `backups/` | $\ge 5$ bản sao lưu |
| Dữ liệu còn nguyên vẹn khi phục hồi từ bản sao lưu | Kết quả so khớp cấu trúc JSON và quy tắc số dư luôn khớp sau phục hồi | So khớp 100%, không có sai lệch |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp nghiệm thu | Phương pháp kiểm chứng | Quy trình kiểm chứng |
| --- | --- | --- |
| Kiểm thử giả lập lỗi | Kiểm thử tự động | Mô phỏng quá trình ghi tệp dữ liệu dung lượng lớn và buộc ngắt chương trình đột ngột ngay khi đang ghi tệp. Sau đó khởi động lại ứng dụng để xác minh tệp dữ liệu chính vẫn nguyên vẹn và đọc dữ liệu thành công. |
| Soát xét kỹ thuật | Đánh giá kiểm tra | Rà soát mã nguồn phần lưu trữ bảo đảm việc ghi tệp an toàn được thực hiện đúng. Tệp chính không bao giờ ở trạng thái ghi dở, dữ liệu đã ghi xong nằm trên đĩa và mọi lỗi đọc ghi được xử lý đầy đủ. |

---

### 5.4 NFR-04: Khả năng bảo trì và kiến trúc hướng đối tượng

Bảng phân loại thuộc tính chất lượng:

| Đặc tính chất lượng chính | Phân nhóm thuộc tính con |
| --- | --- |
| Khả năng bảo trì | Tính module hóa |
| Khả năng bảo trì | Tính tái sử dụng |
| Khả năng bảo trì | Tính phân tích được |
| Khả năng bảo trì | Tính kiểm thử được |

Mô tả yêu cầu kỹ thuật:

1. Phân định trách nhiệm theo thành phần độc lập:
Hệ thống tổ chức thành các thành phần độc lập về trách nhiệm. Thành phần giao diện người dùng không truy cập trực tiếp dữ liệu lưu trữ mà làm việc qua thành phần xử lý nghiệp vụ.
  - Phụ thuộc một chiều: Thành phần bên ngoài phụ thuộc vào thành phần bên trong; không có phụ thuộc ngược chiều hoặc phụ thuộc vòng giữa các mô-đun.
  - Đơn trách nhiệm mô-đun: Mỗi mô-đun chỉ đảm nhiệm một trách nhiệm duy nhất. Các thực thể miền nghiệp vụ (Người dùng, Ví tiền, Giao dịch, Công nợ, Sổ tiết kiệm, Tài sản đầu tư, Quỹ mục tiêu) chỉ quản lý trạng thái nghiệp vụ và các quy tắc luôn đúng của riêng mình. Các thực thể này tách biệt hoàn toàn khỏi phần hiển thị giao diện và cách lưu trữ dữ liệu.
2. Bảo vệ trạng thái nội bộ khỏi thay đổi trái phép:
  - Dữ liệu trạng thái nội bộ của các thực thể miền chỉ được truy xuất qua giao diện đọc có kiểm soát hoặc thao tác nghiệp vụ chuyên biệt. Hệ thống cấm cho phép gán đè trực tiếp số dư ví hoặc trạng thái giao dịch ngoài quy trình ghi nhận dòng tiền.
  - Đối với dữ liệu dạng danh sách hoặc tập hợp, hệ thống bảo đảm giá trị trả về không thể bị sửa đổi từ bên ngoài phạm vi kiểm soát nghiệp vụ.
3. Khả năng mở rộng không phá vỡ hành vi hiện có:
  - Xử lý đồng nhất các loại tài sản: Cấu trúc dữ liệu tài sản (Ví tiền, Sổ tiết kiệm, Tài sản đầu tư) phải cho phép công cụ tổng hợp tài sản ròng xử lý đồng nhất. Cấu trúc này cũng cho phép công cụ tính tỷ lệ từng loại tài sản xử lý đồng nhất. Cả hai công cụ này không phân biệt loại tài sản cụ thể khi tính toán.
  - Cấu trúc phân loại danh mục và các thực thể tài sản cho phép mở rộng danh mục hoặc loại tài sản mới mà không cần chỉnh sửa logic tính toán số dư hiện có.
4. Độ bao phủ kiểm thử tự động và kiểm soát độ phức tạp mã nguồn:
  - Toàn bộ quy tắc nghiệp vụ tài chính, thuật toán tính số dư và công thức tính lãi suất bắt buộc có kiểm thử đơn vị tự động độc lập công nghệ. Kiểm thử bao gồm các trường hợp biên (giá trị âm, giá trị bằng 0, vượt hạn mức).
  - Mỗi thao tác nghiệp vụ chỉ đảm nhiệm một luồng xử lý rõ ràng, dễ kiểm thử độc lập, tránh lồng ghép nhiều nhánh điều kiện trong cùng một thao tác.

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu chấp thuận |
| --- | --- | --- |
| Phạm vi kiểm thử quy tắc nghiệp vụ tài chính | Tỷ lệ quy tắc BR-FIN có ca kiểm thử tương ứng | 100% quy tắc BR-FIN có tối thiểu 1 ca kiểm thử |
| Kiểm thử trường hợp biên | Có ca kiểm thử cho giá trị âm, bằng 0 và vượt hạn mức | Có đủ cho mọi quy tắc BR-FIN liên quan điều kiện về số |
| Mức độ phụ thuộc vòng giữa các mô-đun | Số vòng phụ thuộc giữa các mô-đun | $0$ vòng phụ thuộc |
| Tỷ lệ thực thể miền bảo vệ trạng thái nội bộ khỏi ghi đè trái phép | Thực thể không cho phép gán đè trực tiếp trạng thái từ bên ngoài | $100.00\%$ |
| Tỷ lệ dữ liệu tập hợp trả về không thể bị sửa đổi từ bên ngoài | Tập hợp không thể sửa đổi khi truy xuất | $100.00\%$ |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp nghiệm thu | Phương pháp kiểm chứng | Quy trình kiểm chứng |
| --- | --- | --- |
| Kiểm thử tự động | Kiểm thử tự động | Chạy bộ kiểm thử đơn vị cho từng quy tắc nghiệp vụ BR-FIN, bao gồm trường hợp biên, và xác nhận kết quả PASS. |
| Soát xét kỹ thuật kiến trúc | Đánh giá kiểm tra | Rà soát mã nguồn xác nhận: không có phụ thuộc vòng giữa các mô-đun, thành phần chứa quy tắc nghiệp vụ không phụ thuộc thành phần bên ngoài, trạng thái nội bộ của thực thể được bảo vệ khỏi ghi đè trực tiếp. |

---

### 5.5 NFR-05: Tính khả dụng và trải nghiệm người dùng

Bảng phân loại thuộc tính chất lượng:

| Đặc tính chất lượng chính | Phân nhóm thuộc tính con |
| --- | --- |
| Interaction capability | Tính dễ học |
| Interaction capability | Tính dễ vận hành |
| Interaction capability | Bảo vệ người dùng khỏi lỗi |

Mô tả yêu cầu kỹ thuật:

1. Tinh giản thao tác nhập liệu:
  - Tự động điền giá trị mặc định: Biểu mẫu ghi nhận thu chi và chuyển tiền tự động điền ngày hiện tại của hệ thống. Ô chọn ví thanh toán tự động chọn ví được sử dụng gần nhất. Khi chưa có giao dịch nào, ô này chọn ví tiền mặt có sẵn.
  - Tự động định vị con trỏ nhập liệu: Khi mở hộp thoại ghi nhận giao dịch, con trỏ tự động tập trung vào ô nhập số tiền để sẵn sàng nhận số liệu.
  - Lọc danh mục theo ngữ cảnh: Hệ thống tự động lọc danh mục thu chi theo phân loại ngữ cảnh (khi chọn giao dịch Thu nhập chỉ hiển thị các danh mục thuộc loại Thu nhập; khi chọn Chi tiêu chỉ hiển thị các danh mục thuộc loại Chi tiêu), và chỉ gồm danh mục đang dùng, để loại bỏ lỗi phân loại sai.
  - Định dạng số tiền theo thời gian thực: Khi người dùng nhập các chữ số vào ô tiền tệ, hệ thống tự động định dạng phân tách hàng nghìn bằng dấu phẩy (ví dụ: nhập "1500000" tự động hiển thị "1,500,000 VNĐ"). Cách này hạn chế sai sót khi nhập liệu.
2. Bảo vệ người dùng khỏi lỗi và xác nhận thao tác rủi ro:
  - Cảnh báo xác nhận trước thao tác rủi ro cao: Hệ thống phải hiển thị hộp thoại cảnh báo rủi ro khi xóa giao dịch, lưu trữ ví còn số dư, hoặc xóa quỹ mục tiêu đang gắn tài sản. Thao tác tất toán sổ tiết kiệm trước hạn hoặc nhập tệp ghi đè cũng yêu cầu xác nhận. Hộp thoại tính số tiền lãi bị mất nếu có, yêu cầu người dùng xác nhận rõ ràng trước khi thực hiện.
  - Kiểm tra dữ liệu hợp lệ ngay khi nhập: Hệ thống kiểm tra định dạng số tiền và điều kiện cần có ngay khi nhập liệu, ngăn chặn việc gửi yêu cầu không hợp lệ.
3. Thông báo lỗi định hướng khắc phục:
  - Toàn bộ thông báo lỗi kiểm tra dữ liệu đầu vào và vi phạm quy tắc nghiệp vụ bắt buộc được thể hiện bằng ngôn ngữ tiếng Việt tự nhiên, ngắn gọn. Thông báo chỉ rõ nguyên nhân gây lỗi và đưa ra hướng xử lý khắc phục cụ thể. Ví dụ: Khi số dư ví không đủ chi trả khoản chi, thông báo hiển thị: "Số dư ví MB Bank hiện tại là 150,000 VNĐ, không đủ để chi trả khoản chi 200,000 VNĐ. Vui lòng giảm số tiền chi tiêu hoặc chuyển tiền từ ví khác sang."
  - Hệ thống cấm hiển thị thông báo lỗi kỹ thuật thuần túy, vết ngăn xếp ngoại lệ hoặc mã lỗi hệ thống chưa qua xử lý lên giao diện người dùng. Mọi ngoại lệ kỹ thuật bắt buộc ánh xạ sang thông điệp trong danh mục bảng mã lỗi chuẩn của hệ thống (`ERR-...`).

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu chấp thuận |
| --- | --- | --- |
| Tỷ lệ thông báo lỗi có định hướng giải pháp khắc phục | Số thông báo lỗi có hướng dẫn cụ thể | $100.00\%$ nằm trong danh mục bảng mã lỗi chuẩn |
| Tỷ lệ hiển thị lỗi ngoại lệ chưa bắt hoặc stack trace | Tỷ lệ thông báo kỹ thuật rò rỉ ra giao diện | $0.00\%$ |
| Thời gian làm quen hệ thống cho người dùng mới | Thời gian hoàn thành việc tạo ví và ghi giao dịch đầu tiên | $\le 2.0$ phút (không cần tài liệu hướng dẫn) |
| Tỷ lệ thao tác xóa hoặc ghi đè dữ liệu có cảnh báo xác nhận | Số thao tác xóa hoặc ghi đè có hộp thoại xác nhận | $100.00\%$ |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp nghiệm thu | Phương pháp kiểm chứng | Quy trình kiểm chứng |
| --- | --- | --- |
| Trình diễn thao tác | Trực quan thao tác | Thực hiện kiểm thử quan sát thao tác người dùng trên nhóm thử nghiệm với 3 kịch bản sử dụng cơ bản (tạo ví, ghi chi tiêu, chuyển tiền); bấm giờ đo thời gian hoàn tất tác vụ. |
| Soát xét kỹ thuật thông báo lỗi | Đánh giá kiểm tra | Rà soát toàn bộ bảng thông điệp lỗi trong mã nguồn để kiểm tra tính đầy đủ của ngôn ngữ tiếng Việt và hướng dẫn xử lý khắc phục; rà soát mã nguồn bảo đảm không để lộ vết ngăn xếp lỗi hệ thống ra màn hình người dùng. |

---

### 5.6 NFR-06: Tính tương thích và môi trường chạy

Bảng phân loại thuộc tính chất lượng:

| Đặc tính chất lượng chính | Phân nhóm thuộc tính con |
| --- | --- |
| Flexibility | Tính thích ứng |
| Flexibility | Tính dễ cài đặt |

Mô tả yêu cầu kỹ thuật:

1. Môi trường chạy tiêu chuẩn:
Ứng dụng vận hành trên nền tảng máy ảo tương thích phiên bản ngôn ngữ lập trình hỗ trợ dài hạn (Long-Term Support) mới nhất trở lên, bảo đảm chạy ổn định lâu dài và hỗ trợ xử lý thời gian theo lịch dương.
2. Độc lập nền tảng hệ điều hành:
Ứng dụng đóng gói độc lập và chạy đồng nhất trên mọi hệ điều hành máy tính cá nhân có JVM tương thích (Windows, macOS, Linux) mà không cần thay đổi mã nguồn hoặc biên dịch lại theo từng hệ điều hành.
3. Thao tác đường dẫn hệ thống tệp:
Không được viết cứng ký tự phân cách thư mục của từng hệ điều hành (như dấu `\` trên Windows hay `/` trên Linux). Mọi thao tác đường dẫn tệp phải dùng đối tượng đường dẫn trừu tượng độc lập hệ điều hành để chạy được trên nhiều nền tảng.
4. Mã hóa ký tự UTF-8 không BOM:
Mọi thao tác đọc, ghi tệp dữ liệu cục bộ, tệp xuất dữ liệu JSON và xuất báo cáo phải chỉ định rõ ràng bảng mã UTF-8 không BOM. Quy định này bảo đảm dữ liệu tiếng Việt có dấu lưu trữ và hiển thị chính xác, tránh lỗi mã hóa ký tự bất kể bảng mã mặc định của hệ điều hành máy tính là gì.
5. Không phụ thuộc thư viện nhị phân ngoại lai:
Ứng dụng không được dùng thư viện nhị phân phụ thuộc nền tảng, để có thể đóng gói và phân phối độc lập thành một gói ứng dụng chạy được, không yêu cầu cài đặt phụ thuộc bổ sung theo từng hệ điều hành.

Bảng chỉ số đo và ngưỡng nghiệm thu:

| Thuộc tính đo kiểm | Chỉ số đo | Ngưỡng nghiệm thu chấp thuận |
| --- | --- | --- |
| Tỷ lệ vượt qua bộ kiểm thử tự động trên các hệ điều hành mục tiêu (Windows, macOS, Linux) | Tỷ lệ ca kiểm thử đạt kết quả PASS trên từng nền tảng đã kiểm thử | $100.00\%$ |
| Tỷ lệ thao tác đọc ghi tệp chỉ định rõ ràng bảng mã UTF-8 | Tỷ lệ thao tác đọc ghi tệp chỉ định rõ ràng bảng mã UTF-8 | $100.00\%$ |
| Tỷ lệ lỗi vỡ phông chữ tiếng Việt | Số trường hợp hiển thị sai ký tự có dấu phát hiện được | $0$ lỗi phông chữ |
| Số lượng thư viện nhị phân phụ thuộc nền tảng | Số thư viện nhị phân gốc phụ thuộc hệ điều hành sử dụng | $0$ thư viện |
| Phiên bản Java hỗ trợ tối thiểu | Phiên bản JDK / JRE yêu cầu | Java SE 17 LTS |

Bảng phương pháp và công cụ kiểm chứng:

| Phương pháp nghiệm thu | Phương pháp kiểm chứng | Quy trình kiểm chứng |
| --- | --- | --- |
| Kiểm thử tương thích đa nền tảng | Kiểm thử tự động | Chạy bộ kiểm thử đơn vị tự động trên từng môi trường hệ điều hành mục tiêu để bảo đảm kết quả chạy thử giống nhau giữa các hệ điều hành. |
| Soát xét mã nguồn I/O tĩnh | Đánh giá kiểm tra | Rà soát mã nguồn tĩnh đối với toàn bộ thao tác đọc ghi tệp để bảo đảm luôn chỉ định bảng mã UTF-8 và sử dụng đối tượng đường dẫn trừu tượng. |

