## 3. QUY TẮC NGHIỆP VỤ VÀ QUY ĐỊNH TÀI CHÍNH

Hệ thống quản lý tài chính cá nhân áp dụng 5 quy tắc nghiệp vụ và bất biến nghiệp vụ (`BR-FIN-01` đến `BR-FIN-04` và `BR-NAM-01`). Các quy tắc này đóng vai trò chính sách cốt lõi của miền bài toán (Problem Domain Policies), trực tiếp bảo vệ tính toàn vẹn của 3 mục tiêu hệ thống tại Mục 2.2 (`G-01`, `G-02`, `G-03`). Quy tắc nghiệp vụ chi phối toàn bộ việc tính toán số dư ví, ghi nhận dòng tiền, xử lý chuyển tiền nội bộ, phân cấp danh mục thu chi và định danh thực thể. Toàn bộ các yêu cầu chức năng tại Mục 4 và các ca sử dụng tại Mục 6 bắt buộc phải tuân thủ và không được phép vi phạm các quy tắc này.

Mỗi quy tắc nghiệp vụ được đặc tả nhất quán theo khuôn mẫu 5 phần chuẩn:
1. Mã định danh và tên quy tắc: Cú pháp `BR-<DOMAIN>-NN: <Tên quy tắc>`, phân loại theo miền nghiệp vụ tài chính (`BR-FIN`) hoặc miền định danh (`BR-NAM`).
2. Phạm vi áp dụng và mục đích: Xác định các thực thể dữ liệu chịu sự chi phối và mục đích bảo vệ tính đúng đắn của dữ liệu.
3. Nội dung quy tắc và biểu thức toán học: Phát biểu nguyên tắc nghiệp vụ bằng câu văn định chuẩn kết hợp công thức toán học hình thức xác định các bất biến số học.
4. Quy định xử lý chi tiết: Bảng các ràng buộc nghiệp vụ cụ thể về biểu diễn số học, cơ chế làm tròn và các trường hợp biên ở tầng bài toán, không rò rỉ mã nguồn cài đặt.
5. Bảng mã lỗi vi phạm và phản hồi hệ thống: Bảng 4 cột chuẩn hóa gồm Mã lỗi (`ERR-*`), điều kiện vi phạm, hành động của hệ thống, thông điệp thông báo người dùng.

Bảng tổng hợp 5 quy tắc nghiệp vụ và bất biến của hệ thống:

| Mã quy tắc | Tên quy tắc | Phân loại quy tắc | Thực thể áp dụng | Ý nghĩa nghiệp vụ |
| :---: | :--- | :--- | :--- | :--- |
| `BR-FIN-01` | Quy tắc độ chính xác tài chính | Quy tắc tính toán số học | Toàn bộ thực thể tài chính | Số thập phân chính xác, làm tròn xác định. |
| `BR-FIN-02` | Quy tắc bảo toàn số dư ví | Ràng buộc toàn vẹn dữ liệu | Ví, giao dịch thu chi, giao dịch chuyển tiền, điều chỉnh số dư | Số dư ví tính lại từ lịch sử dòng tiền, luôn không âm. |
| `BR-FIN-03` | Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ | Quy tắc giao dịch đồng thời | Giao dịch chuyển tiền, giao dịch thu chi, ví | Trừ ví nguồn và cộng ví đích cùng được ghi nhận hoặc cùng không được ghi nhận. |
| `BR-FIN-04` | Quy tắc phân cấp danh mục thu chi 3 cấp | Ràng buộc cấu trúc phân cấp | Danh mục, nhóm nhu cầu, loại thu chi | Cấu trúc 3 cấp (Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết), mọi danh mục thuộc một nhóm cha hợp lệ. |
| `BR-NAM-01` | Quy tắc tên của ví, nhóm nhu cầu và danh mục | Ràng buộc định danh dữ liệu | Ví, nhóm nhu cầu, danh mục | Tên đúng định dạng và không trùng trong phạm vi áp dụng. |

### 3.1 BR-FIN-01: Quy tắc độ chính xác tài chính

Quy tắc áp dụng cho mọi thực thể dữ liệu, phép tính toán số học và việc lưu trữ (Ví, giao dịch thu chi, giao dịch chuyển tiền, danh mục, điều chỉnh số dư). Quy tắc nhằm loại bỏ sai số làm tròn số thực nhị phân cơ số 2. Quy tắc cũng giữ độ chính xác trong mọi phép toán tiền tệ và tỷ lệ trong hệ thống.

#### Nội dung

Mọi giá trị số tiền và tỷ lệ phần trăm bắt buộc dùng số thập phân chính xác, với số chữ số thập phân cố định theo `CON-02`. Hệ thống không được sử dụng số thực nhị phân trong tính toán nghiệp vụ.

#### Quy định xử lý

| Quy định | Nội dung |
| --- | --- |
| Biểu diễn giá trị | Số thập phân chính xác, với số chữ số thập phân cố định theo `CON-02`. |
| Nguồn hình thành giá trị | Giá trị chỉ được hình thành từ dữ liệu nhập dạng thập phân chính xác hoặc từ giá trị nguyên. Không được chuyển đổi trung gian qua số thực nhị phân. |
| Quy tắc chữ số thập phân của tiền tệ | Tiền tệ giao dịch và số dư hiển thị cho VNĐ dùng 0 chữ số thập phân (Số nguyên đồng) trên giao diện người dùng. Tính toán, xử lý nghiệp vụ và chuỗi kiểm thử tự động dùng 2 chữ số thập phân. |
| Quy tắc chữ số thập phân của tỷ lệ phần trăm | Các tỷ lệ phần trăm trung gian bắt buộc dùng tối thiểu 4 chữ số thập phân (Khuyến nghị 6 chữ số thập phân trong các bước tính trung gian). |
| Chế độ làm tròn | Các phép chia không chia hết bắt buộc chỉ định rõ ràng quy cách làm tròn ngân hàng (Làm tròn về số chẵn gần nhất khi phần dư đúng bằng một nửa). |
| Phép chia số học | Không bao giờ thực hiện phép chia giữa hai số thập phân chính xác mà không kèm số chữ số thập phân và quy tắc làm tròn rõ ràng. |
| So sánh giá trị số học | So sánh theo giá trị đại số thực, không phân biệt số chữ số 0 dư thừa ở phần thập phân. |

#### Mã lỗi

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-FIN-001` | Dữ liệu đầu vào sai định dạng số học hoặc chứa ký tự không hợp lệ | Từ chối xử lý dữ liệu đầu vào | "Dữ liệu số tiền không đúng định dạng số học hợp lệ." |
| `ERR-FIN-002` | Phép toán chia không xác định quy tắc làm tròn gây số thập phân vô hạn | Hủy giao dịch hiện tại và ghi nhật ký cảnh báo | "Lỗi số học: Phép toán chia không xác định quy tắc làm tròn." |

### 3.2 BR-FIN-02: Quy tắc bảo toàn số dư ví

Quy tắc áp dụng cho thực thể Ví và toàn bộ các dòng tiền đến từ giao dịch thu chi, giao dịch chuyển tiền và điều chỉnh số dư. Số dư khả dụng của từng ví được tính lại theo thời gian thực từ lịch sử các khoản ghi dòng tiền. Nghiêm cấm đổi số dư bằng phép gán trực tiếp. Số dư ví được kiểm soát để luôn không âm.

#### Nội dung quy tắc

1. Công thức số dư tính lại từ lịch sử dòng tiền: Xét ví $w$ tại thời điểm quan sát $T$. Số dư khả dụng $WalletBalance_w(T)$ được xác định duy nhất bằng tổng các dòng tiền vào trừ tổng các dòng tiền ra có đến thời điểm $T$. Ví mới tạo có số dư 0.00 VNĐ, nên số dư hiện có của ví chỉ hình thành qua giao dịch thu chi, chuyển tiền nội bộ hoặc khoản điều chỉnh số dư:

$$
WalletBalance_w(T) = \sum_{t \le T} \text{Inflow}_w(t) - \sum_{t \le T} \text{Outflow}_w(t)
$$

Trong đó:

$$
\text{Inflow}_w(t) = Income_w(t) + TransferIn_w(t) + \max(0, Adjustment_w(t))
$$

$$
\text{Outflow}_w(t) = Expense_w(t) + TransferOut_w(t) + \max(0, -Adjustment_w(t))
$$

Mỗi số hạng trong công thức được xác định như sau:

| Số hạng | Loại dòng tiền | Dấu | Ý nghĩa |
| --- | --- | --- | --- |
| $Income_w(t)$ | Thu nhập | + | Khoản thu nhập được ghi trực tiếp vào ví $w$ bằng giao dịch Thu nhập theo `FR-TXN-01` |
| $Expense_w(t)$ | Chi tiêu | − | Khoản chi tiêu chi trả trực tiếp từ ví $w$, kể cả phí giao dịch theo `FR-TXN-02` |
| $TransferIn_w(t)$ | Nhận chuyển tiền | + | Khoản tiền nhận được do chuyển từ ví khác vào ví $w$ theo `FR-TRF-01` |
| $TransferOut_w(t)$ | Chuyển tiền đi | − | Khoản tiền chuyển từ ví $w$ sang ví khác theo `FR-TRF-01` |
| $Adjustment_w(t)$ | Khoản điều chỉnh số dư | +/− | Chênh lệch từ khoản điều chỉnh số dư theo `FR-WAL-05`, không thuộc luồng thu, chi hoặc chuyển tiền nào |

2. Điều kiện về số dư khả dụng:
   - Đối với mọi ví: Số dư khả dụng tại thời điểm hiện tại luôn không âm (lớn hơn hoặc bằng 0.00 VNĐ).
3. Điều kiện về khoản điều chỉnh số dư:
   - Mỗi lần điều chỉnh số dư theo `FR-WAL-05` bắt buộc tạo một bản ghi độc lập và không được sửa. Bản ghi lưu trữ số dư trước và sau điều chỉnh, giá trị chênh lệch, thời điểm ghi nhận và lý do điều chỉnh (Nếu có, hoặc chuỗi rỗng).
4. Điều kiện về phí giao dịch:
   - Chi phí giao dịch bắt buộc không âm (Lớn hơn hoặc bằng 0.00 VNĐ), từ chối theo mã `ERR-FEE-001` nếu số tiền chi phí nhỏ hơn 0. Khi có chi phí lớn hơn 0, Người dùng bắt buộc chọn danh mục chi tiêu hợp lệ. Hệ thống từ chối theo `ERR-FEE-002` nếu chưa chọn danh mục, hoặc theo `ERR-FEE-003` nếu danh mục đã xóa hoặc sai loại.
   - Phí giao dịch là Giao dịch thu chi loại `EXPENSE` nên được tính vào chi tiêu của ví bị trừ chi phí.
   - Phí giao dịch liên kết với giao dịch thu chi hoặc giao dịch chuyển tiền phát sinh ra nó. Phí chỉ có hiệu lực cùng giao dịch đó: hai bản ghi cùng được ghi nhận, sửa hoặc xóa, hoặc không có thay đổi nào xảy ra.

#### Quy định xử lý

| Quy định | Nội dung |
| --- | --- |
| Cách tính toán | Tính lại số dư theo thời gian thực từ lịch sử dòng tiền. Hệ thống không được cho phép đặt số dư ví bằng phép gán trực tiếp mà không có khoản ghi dòng tiền tương ứng. |
| Đồng bộ dữ liệu | Mọi thao tác thêm, sửa hoặc xóa giao dịch liên quan đến ví phải tính lại số dư ngay lập tức trong cùng lần xử lý. |
| Kiểm tra tiền điều kiện | Trước khi thực hiện thao tác làm giảm số dư ví (Chi tiêu, chuyển đi, điều chỉnh giảm, ghi chi phí, xóa hoặc sửa giao dịch dẫn tới giảm số dư ví), Hệ thống phải kiểm tra số dư khả dụng. Thao tác bị từ chối theo mã `ERR-WAL-001` nếu số dư không đủ. |

#### Mã lỗi

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-WAL-001` | Giao dịch làm số dư ví âm hoặc không đủ thanh toán số tiền và chi phí | Từ chối thực hiện giao dịch, giữ nguyên trạng thái số dư | "Giao dịch bị từ chối: Số dư ví không đủ để thực hiện thao tác." |
| `ERR-FEE-001` | Chi phí giao dịch nhỏ hơn 0 | Từ chối giao dịch | "Chi phí không hợp lệ." |
| `ERR-FEE-002` | Chi phí lớn hơn 0 nhưng chưa chọn danh mục cho phí giao dịch | Từ chối tiếp nhận giao dịch | "Vui lòng chọn danh mục cho phí giao dịch." |
| `ERR-FEE-003` | Danh mục phí đã bị xóa hoặc không thuộc loại thu chi chi tiêu (`EXPENSE`) | Từ chối thực hiện giao dịch | "Danh mục phí không hợp lệ hoặc không tương thích với loại chi tiêu." |

### 3.3 BR-FIN-03: Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ

Quy tắc áp dụng cho thực thể giao dịch chuyển tiền, cặp thực thể ví (Ví nguồn và ví đích) và thực thể giao dịch thu chi ghi phí giao dịch. Quy tắc áp dụng cơ chế ghi nhận đồng thời (Trừ ví nguồn, cộng ví đích) cho mọi giao dịch chuyển tiền nội bộ. Tổng số dư trong các ví của toàn hệ thống được giữ nguyên nếu không có phí, và chi phí phát sinh được tách riêng thành một phí giao dịch độc lập.

#### Nội dung quy tắc

1. Cặp khoản ghi: Với một giao dịch chuyển tiền từ ví nguồn tới ví đích với số tiền chuyển lớn hơn 0:
   - Ví nguồn giảm số tiền chuyển: số dư ví nguồn sau chuyển tiền bằng số dư trước chuyển trừ số tiền chuyển ($WalletBalance_{w_{src}}(\text{sau}) = WalletBalance_{w_{src}}(\text{trước}) - A$).
   - Ví đích tăng số tiền chuyển: số dư ví đích sau chuyển tiền bằng số dư trước chuyển cộng số tiền chuyển ($WalletBalance_{w_{dst}}(\text{sau}) = WalletBalance_{w_{dst}}(\text{trước}) + A$).
2. Giữ nguyên tổng số dư trong các ví: Trước khi tính chi phí, tổng số dư trên toàn bộ các ví trước và sau chuyển tiền luôn không đổi:
   $$
   \sum_{w \in Wallets} WalletBalance_w(\text{sau}) = \sum_{w \in Wallets} WalletBalance_w(\text{trước})
   $$
3. Ghi nhận phí giao dịch độc lập: Khi có phí chuyển tiền (chi phí lớn hơn hoặc bằng 0) do bên thứ ba thu:
   - Số tiền trừ ví nguồn bằng số tiền chuyển cộng chi phí.
   - Số tiền cộng ví đích bằng số tiền chuyển.
   - Chi phí chuyển tiền được ghi nhận thành một phí giao dịch độc lập (loại `EXPENSE`) từ ví nguồn:
     $$WalletBalance_{w_{src}}(\text{sau}) = WalletBalance_{w_{src}}(\text{trước}) - (A + Fee)$$
     $$\sum_{w \in Wallets} WalletBalance_w(\text{sau}) = \sum_{w \in Wallets} WalletBalance_w(\text{trước}) - Fee$$

#### Quy định xử lý

| Quy định | Nội dung |
| --- | --- |
| Ghi nhận đồng thời | Hai khoản ghi trừ ví nguồn và cộng ví đích bắt buộc cùng được ghi nhận hoặc cùng không được ghi nhận. Nếu một trong hai khoản ghi thất bại, số dư cả hai ví phải trở về giá trị trước thao tác. |
| Phân định thực thể | Ví nguồn và ví đích bắt buộc khác nhau. Nếu trùng nhau, hệ thống từ chối theo mã `ERR-TRF-001`. |
| Giá trị chuyển cho phép | Số tiền chuyển bắt buộc lớn hơn 0 (Lớn hơn 0.00 VNĐ), nếu vi phạm hệ thống từ chối theo mã `ERR-TRF-002`. Phí chuyển tiền bắt buộc không âm (Lớn hơn hoặc bằng 0.00 VNĐ), nếu vi phạm hệ thống từ chối theo mã `ERR-FEE-001`. |
| Điều kiện số dư ví nguồn | Số dư khả dụng của ví nguồn trước khi chuyển bắt buộc lớn hơn hoặc bằng tổng số tiền chuyển và chi phí. Nếu không đủ số dư, hệ thống từ chối theo mã `ERR-WAL-001`. |
| Quy tắc xóa chuyển tiền | Khi xóa chuyển tiền, hệ thống xóa bản ghi chuyển tiền khỏi sổ giao dịch chuyển tiền, xóa phí giao dịch liên kết (Nếu có) khỏi sổ giao dịch thu chi, hoàn số tiền chuyển cộng chi phí về ví nguồn và trừ số tiền chuyển khỏi ví đích. Cả hai ví được tính lại số dư theo `BR-FIN-02` từ các giao dịch đang tồn tại. Hệ thống từ chối xóa theo mã `ERR-WAL-001` nếu số dư ví đích nhỏ hơn số tiền chuyển. |

#### Mã lỗi

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-TRF-001` | Ví nguồn trùng với ví đích | Từ chối thực hiện giao dịch | "Giao dịch không hợp lệ: Ví chuyển và ví nhận phải khác nhau" |
| `ERR-TRF-002` | Số tiền chuyển nhỏ hơn hoặc bằng 0 | Từ chối thực hiện giao dịch | "Số tiền chuyển phải là một số dương lớn hơn 0"|

### 3.4 BR-FIN-04: Quy tắc phân cấp danh mục thu chi 3 cấp

Quy tắc áp dụng cho thực thể danh mục, nhóm nhu cầu, loại thu chi và mối quan hệ liên kết từ giao dịch thu chi. Quy tắc chuẩn hóa cấu trúc danh mục 3 cấp quan hệ 1:N. Loại thu chi cố định còn nhóm nhu cầu và danh mục chi tiết do người dùng quản lý. Quy tắc giữ cho các liên kết giữa các cấp luôn đúng, không để danh mục nào mất nhóm cha và không để sai lệch báo cáo dòng tiền lịch sử.

#### Nội dung quy tắc

1. Cấu trúc danh mục 3 cấp: Mô hình phân cấp với quan hệ 1:N. Mỗi loại thu chi chứa một hoặc nhiều nhóm nhu cầu, và mỗi nhóm nhu cầu chứa các danh mục chi tiết.
2. Điều kiện quan hệ cha con duy nhất:
   - Mỗi danh mục chi tiết chỉ được gán vào đúng một nhóm nhu cầu cha tại một thời điểm.
   - Mỗi nhóm nhu cầu chỉ được gán vào đúng một loại thu chi gốc tại một thời điểm.
   - Mọi danh mục đều gắn với một nhóm cha: Danh mục con bắt buộc trực thuộc một nhóm cha hợp lệ đang tồn tại.
3. Giá trị khởi tạo và phần do người dùng quản lý:
   - Cấp 1 (Loại thu chi): Thu nhập (`INCOME`), Chi tiêu (`EXPENSE`). Cố định, không cho phép chỉnh sửa hoặc xóa.
   - Cấp 2 (Nhóm nhu cầu): Bộ nhóm mẫu ban đầu do hệ thống nạp một lần từ cấu hình triển khai khi khởi tạo hệ thống lần đầu. Mỗi loại thu chi phải có ít nhất một nhóm nhu cầu đang dùng. Người dùng có toàn quyền thêm, đổi tên và xóa nhóm. Tên nhóm không được dùng làm điều kiện tiên quyết trong bất kỳ quy tắc xử lý nào.
   - Cấp 3 (Danh mục chi tiết): Bộ danh mục mẫu ban đầu nạp một lần cùng với nhóm nhu cầu cha tương ứng từ cấu hình triển khai. Tại thời điểm nạp, mỗi nhóm mẫu có tối thiểu 1 danh mục chi tiết mẫu. Nhóm do người dùng tạo có thể chưa có danh mục chi tiết nào. Người dùng có toàn quyền thêm, đổi tên, chuyển nhóm và xóa danh mục theo nhu cầu sử dụng.
4. Kiểm tra tên: Tên nhóm nhu cầu và tên danh mục chi tiết tuân theo `BR-NAM-01` về định dạng và tính duy nhất.

#### Quy định xử lý

| Quy định | Nội dung |
| --- | --- |
| Xóa danh mục | Người dùng chỉ thấy thao tác xóa. Hộp xác nhận nêu số giao dịch liên quan. Danh mục chưa có giao dịch thì xóa hẳn. Danh mục đã có giao dịch thì chuyển sang trạng thái đã xóa, bị ẩn khỏi danh sách chọn và giao dịch cũ được giữ nguyên. |
| Xóa nhóm | Xóa nhóm kéo theo mọi danh mục con đang dùng, mỗi danh mục xử lý theo quy tắc xóa danh mục. Nhóm có giao dịch bên trong thì chuyển sang trạng thái đã xóa, nhóm không có thì xóa hẳn. Hộp xác nhận nêu số danh mục sẽ bị ẩn, số danh mục xóa hẳn và tổng số giao dịch được giữ lại. Mỗi loại thu chi luôn còn ít nhất một nhóm đang dùng. |
| Mục đã xóa | Mục đã xóa không hiện trong danh sách chọn cho giao dịch mới, nhưng vẫn hiện trong báo cáo và lịch sử kèm nhãn (Đã xóa). Người dùng chọn khôi phục (Giữ mã cũ, nối lại giao dịch cũ) hoặc tạo mới (Mã mới). Mã định danh không bao giờ dùng lại. Khôi phục nhóm chỉ khôi phục riêng nhóm, danh mục con khôi phục riêng từng cái. |
| Đổi nhóm của danh mục | Danh mục chỉ được chuyển sang nhóm nhu cầu đang dùng thuộc cùng loại thu chi. |
| Không được đổi loại thu chi | Hệ thống không được thay đổi thuộc tính loại thu chi của một nhóm nhu cầu hoặc danh mục khi đã có giao dịch liên kết. Quy định này tránh sai lệch dữ liệu phân tích thu chi trong quá khứ. |

#### Mã lỗi

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-CAT-001` | Chuyển danh mục sang nhóm thuộc loại thu chi khác, hoặc đổi loại thu chi của nhóm hoặc danh mục đã có giao dịch | Từ chối thay đổi thuộc tính | "Không được phép đổi loại thu chi hoặc chuyển danh mục sang nhóm thuộc loại thu chi khác" |
| `ERR-CAT-002` | Xóa nhóm đang dùng cuối cùng của một Loại thu chi | Từ chối thao tác xóa | "Không thể xóa nhóm cuối cùng của loại thu chi này" |
| `ERR-CAT-003` | Chuyển danh mục sang nhóm đã xóa | Từ chối thao tác chuyển | "Không thể chuyển danh mục sang nhóm đã xóa" |
| `ERR-CAT-004` | Khôi phục danh mục khi nhóm của nó còn ở trạng thái đã xóa | Từ chối thao tác khôi phục | "Nhóm chứa danh mục này đang ở trạng thái đã xóa. Hãy khôi phục nhóm trước" |
| `ERR-CAT-005` | Tạo danh mục vào nhóm đang ở trạng thái đã xóa | Từ chối thao tác tạo | "Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng hoặc khôi phục nhóm trước" |

### 3.5 BR-NAM-01: Quy tắc tên của ví, nhóm nhu cầu và danh mục

Quy tắc áp dụng cho thực thể ví, nhóm nhu cầu và danh mục chi tiết ở mọi thao tác đặt tên: Tạo, đổi tên, chuyển nhóm, tạo mới khi khôi phục và khôi phục. Quy tắc quy định định dạng của tên và tính duy nhất của tên trong từng phạm vi.

#### Nội dung quy tắc

1. Định dạng tên: Tên được cắt khoảng trắng ở hai đầu trước khi kiểm tra. Sau khi cắt, tên không được rỗng và phải dài từ 1 đến 50 ký tự.
2. Cách so sánh: Hai tên được coi là trùng khi giống nhau sau khi cắt khoảng trắng ở hai đầu, không phân biệt chữ hoa, chữ thường.
3. Phạm vi duy nhất: Tên không được trùng với các mục khác trong phạm vi của bảng ngay dưới danh sách này. Hệ thống không so sánh chéo giữa nhóm nhu cầu và danh mục.
4. Mục đã xóa: Nhóm nhu cầu và danh mục ở trạng thái đã xóa không tính vào việc kiểm tra trùng khi tạo mới và khi đổi tên. Khi tạo mới mà tên trùng một mục đã xóa cùng cấp, hệ thống hỏi người dùng chọn khôi phục mục cũ hoặc tạo mới (Hộp hỏi thuộc `UC-CAT-01`, lựa chọn Khôi phục được xử lý theo `UC-CAT-03`). Khi đổi tên, hệ thống không hỏi.
5. Khôi phục: Khi khôi phục một mục đã xóa, hệ thống chỉ đối chiếu tên với các mục đang dùng.

Bảng phạm vi duy nhất của tên:

| Đối tượng | Phạm vi duy nhất | Các mục được so sánh |
| --- | --- | --- |
| Ví | Toàn bộ ví trong hệ thống | Mọi ví khác, kể cả ví đã lưu trữ |
| Nhóm nhu cầu | Cùng một Loại thu chi | Các nhóm đang dùng khác |
| Danh mục | Cùng một nhóm cha | Các danh mục đang dùng khác |

#### Quy định xử lý

| Quy định | Nội dung |
| --- | --- |
| Thứ tự kiểm tra | Kiểm tra định dạng trước (`ERR-NAM-001`), kiểm tra trùng tên sau (`ERR-NAM-002`). |
| Đổi tên | Mục đang đổi tên không bị so sánh với chính nó. |
| Chuyển nhóm | Tên danh mục được đối chiếu với các danh mục đang dùng trong nhóm đích. |
| Khôi phục | Tên giữ nguyên nên chỉ kiểm tra tính duy nhất (`ERR-NAM-002`). Tạo mới khi khôi phục kiểm tra cả định dạng và tính duy nhất. |
| Nhập tệp sao lưu | Không áp dụng. Tệp sao lưu do chính hệ thống tạo ra nên tên không được kiểm tra lại khi nhập, theo giả định `AS-02`. |
| Giải phóng tên | Khi ví bị xóa vĩnh viễn, tên ví được giải phóng khỏi phạm vi duy nhất. |
| Dữ liệu khi bị từ chối | Hệ thống không tạo, không đổi tên, không chuyển nhóm và không khôi phục. Dữ liệu giữ nguyên. |

#### Mã lỗi

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-NAM-001` | Tên rỗng sau khi cắt khoảng trắng ở hai đầu, hoặc dài hơn 50 ký tự | Từ chối thao tác | "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu." |
| `ERR-NAM-002` | Tên trùng một mục khác trong cùng phạm vi duy nhất | Từ chối thao tác | Theo bảng thông điệp ngay dưới đây |

Thông điệp của `ERR-NAM-002` theo đối tượng và thao tác:

| Đối tượng và thao tác | Thông điệp thông báo người dùng |
| --- | --- |
| Ví | "Tên ví này đã tồn tại trong hệ thống. Vui lòng chọn một tên khác." |
| Nhóm nhu cầu | "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác." |
| Danh mục | "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác." |
| Chuyển nhóm | "Nhóm đích đã có danh mục cùng tên. Hãy đổi tên danh mục trước khi chuyển." |
| Khôi phục | "Không thể khôi phục vì tên đã được dùng. Hãy đổi tên một trong hai mục rồi thử lại." |
