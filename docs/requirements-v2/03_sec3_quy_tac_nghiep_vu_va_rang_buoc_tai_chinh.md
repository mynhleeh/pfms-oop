## 3. QUY TẮC NGHIỆP VỤ VÀ QUY ĐỊNH TÀI CHÍNH

Mục này đặc tả 10 quy tắc nghiệp vụ và bất biến nghiệp vụ (BR-FIN-01 đến BR-FIN-10) của Hệ thống Quản lý Tài chính Cá nhân. Các quy tắc này là điều kiện nghiệp vụ bắt buộc, chi phối toàn bộ thuật toán tính số dư, ghi nhận dòng tiền, tính lãi dồn tích, định giá tài sản và chia nguồn tiền tài chính.

Bảng 10 quy tắc nghiệp vụ và bất biến tài chính:

| Mã quy tắc | Tên quy tắc nghiệp vụ                   | Thực thể miền chi phối                             | Ý nghĩa nghiệp vụ                                                                                                                                                                                   |
| :----------: | :--------------------------------------- | :-------------------------------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| BR-FIN-01  | Quy tắc độ chính xác tài chính         | Toàn bộ thực thể tài chính                         | Loại bỏ sai số làm tròn số thực nhị phân; áp dụng số thập phân chính xác với quy tắc làm tròn và số chữ số thập phân xác định.                                                                          |
| BR-FIN-02  | Quy tắc bảo toàn số dư ví               | Ví tiền, Giao dịch, Chuyển tiền nội bộ, Công nợ    | Số dư khả dụng của ví tại thời điểm $T$ là hàm tổng hợp luôn cho cùng kết quả từ số dư ban đầu và lịch sử dòng tiền; luôn không âm.                                                                             |
| BR-FIN-03  | Quy tắc ghi hai vế khi chuyển tiền nội bộ   | Chuyển tiền nội bộ, Ví tiền                        | Khi chuyển tiền, việc trừ ví nguồn và việc cộng ví đích diễn ra cùng lúc: hoặc cả hai được ghi nhận, hoặc cả hai không được ghi nhận. Nhờ đó tổng tiền trong các ví của toàn hệ thống luôn được giữ nguyên. |
| BR-FIN-04  | Quy tắc phân cấp danh mục thu chi 3 cấp | Danh mục, Nhóm nhu cầu, Loại thu chi               | Cây danh mục phân cấp 3 tầng (Loại thu chi -&gt; Nhóm nhu cầu -&gt; Danh mục chi tiết); giữ cho mọi danh mục đều gắn với một nhóm cha hợp lệ.                                               |
| BR-FIN-05  | Quy tắc tính lãi tiết kiệm có kỳ hạn    | Sổ tiết kiệm                                       | Tính lãi dồn tích theo lãi suất năm chia 365 ngày; hỗ trợ tính lãi đơn, tính lãi kép và tính lại lãi khi tất toán trước hạn.                                                             |
| BR-FIN-06  | Quy tắc định giá và giá vốn tài sản đầu tư | Tài sản đầu tư                                     | Định giá các tài sản đầu tư theo mô hình đơn giá nhân số lượng, giá trị thị trường theo đơn giá hiện hành; tách riêng lãi lỗ chưa chốt và tính tỷ suất sinh lời theo thời gian nắm giữ.                                                             |
| BR-FIN-07  | Quy tắc sự kiện của tài sản đầu tư | Tài sản đầu tư, Lệnh mua, Ví tiền | Quản lý ba dạng sự kiện gồm nhận tiền mặt, thêm đơn vị cùng loại và hoàn vốn. Phân bổ giá trị cho các lệnh mua theo số đơn vị nắm giữ ngay trước sự kiện. Tăng số dư ví nhận tiền với dạng nhận tiền mặt và hoàn vốn, tăng thu nhập tháng với dạng nhận tiền mặt. Điều chỉnh giá vốn và xử lý thu nhập khi tài sản đã bán hết. |
| BR-FIN-08  | Quy tắc tỷ suất sinh lời của tài sản đầu tư | Lệnh mua, Tài sản đầu tư, Khoản đầu tư | Xác định thời gian nắm giữ và tính tỷ suất từng lệnh mua theo ngưỡng 365 ngày; tách biệt tỷ suất cả lệnh và tỷ suất phần đang giữ; xác định tỷ suất nội hoàn theo dòng tiền thật và xử lý khi phương trình không hội tụ. |
| BR-FIN-09  | Quy tắc theo dõi công nợ                | Công nợ, Ví tiền, Đối tác                          | Quản lý sổ ghi công nợ tách riêng nợ phải thu và nợ phải trả theo từng người vay hoặc người cho vay; số tiền còn nợ luôn không âm; tách riêng giao dịch tiền mặt và xóa nợ không dùng tiền mặt.                                     |
| BR-FIN-10  | Quy tắc gắn tài sản vào quỹ mục tiêu            | Quỹ mục tiêu, Ví tiền, Sổ tiết kiệm, Lệnh mua của tài sản đầu tư | Gắn tài sản vào các quỹ mục tiêu do người dùng tự tạo; kiểm soát tiến độ hoàn thành; luôn không tính trùng tài sản.                                                                              |

---

### 3.1 BR-FIN-01: Quy tắc độ chính xác tài chính

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | BR-FIN-01 |
| Tên | Độ chính xác tài chính |
| Phạm vi áp dụng | Toàn bộ các thực thể miền dữ liệu, các phép tính toán số học và lưu trữ tệp tin (Ví tiền, Giao dịch, Chuyển tiền nội bộ, Công nợ, Sổ tiết kiệm, Tài sản đầu tư, Quỹ mục tiêu) |
| Mục đích nghiệp vụ | Loại bỏ sai số làm tròn số thực nhị phân cơ số 2; giữ độ chính xác trong mọi phép toán tiền tệ, tỷ lệ chia tiền và định giá tài sản trong hệ thống |

#### Công thức

1. Kiểu dữ liệu số học: Mọi giá trị số tiền, số đơn vị tài sản đầu tư, lãi suất và tỷ lệ phân bổ bắt buộc dùng số thập phân chính xác, với số chữ số thập phân cố định theo CON-02. Hệ thống cấm sử dụng các kiểu số thực nhị phân (float/double) trong tính toán nghiệp vụ.
2. Sai số tích lũy tổng thể: Với mọi phép toán phân rã một giá trị tổng $S$ thành $n$ phần tử $\{s_1, s_2, \dots, s_n\}$:

$$S = \sum_{i=1}^{n} s_i$$

#### Quy định kỹ thuật và xử lý số học

| Tham số | Quy tắc |
| --- | --- |
| Kiểu dữ liệu lưu trữ | Số thập phân chính xác, với số chữ số thập phân cố định theo CON-02. |
| Cách tạo giá trị | Bắt buộc tạo giá trị từ chuỗi ký tự thập phân chính xác hoặc từ giá trị nguyên. Không được chuyển đổi trung gian qua số thực nhị phân. |
| Quy tắc chữ số thập phân của tiền tệ | Tiền tệ giao dịch và số dư hiển thị cho VNĐ dùng 0 chữ số thập phân (số nguyên đồng). Lưu trữ số học nội bộ và xử lý nghiệp vụ dùng 2 chữ số thập phân. |
| Quy tắc chữ số thập phân của tài sản đầu tư | Số đơn vị tài sản đầu tư, đơn giá và các tỷ lệ phần trăm trung gian bắt buộc dùng tối thiểu 4 chữ số thập phân (khuyến nghị 6 chữ số thập phân trong các bước tính trung gian). Số lượng do người dùng nhập có tối đa 4 chữ số thập phân, đại lượng dẫn xuất giữ đủ chữ số theo BR-FIN-06 mục 3.6. |
| Chế độ làm tròn | Các phép chia không chia hết bắt buộc chỉ định rõ ràng quy cách làm tròn ngân hàng (làm tròn đến số chẵn gần nhất) để cân bằng thống kê, hoặc quy tắc làm tròn nửa lên cho tiền lãi thương mại khi tất toán. |
| Phép chia số học | Không bao giờ thực hiện phép chia giữa hai số thập phân chính xác mà không kèm số chữ số thập phân và quy tắc làm tròn rõ ràng. |
| So sánh giá trị số học | So sánh theo giá trị đại số thực, không phân biệt số chữ số 0 dư thừa ở phần thập phân. |

#### Danh mục mã lỗi và xử lý vi phạm

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-FIN-001` | Dữ liệu đầu vào sai định dạng số học hoặc chứa ký tự không hợp lệ | Từ chối xử lý dữ liệu đầu vào | "Dữ liệu số tiền không đúng định dạng số học hợp lệ." |
| `ERR-FIN-002` | Phép toán chia không xác định quy tắc làm tròn gây số thập phân vô hạn | Hủy giao dịch hiện tại và ghi nhật ký cảnh báo | "Lỗi số học: Phép toán chia không xác định quy tắc làm tròn." |
| `ERR-FIN-003` | Tổng các giá trị thành phần sau phân bổ sai lệch vượt quá 0,01 đơn vị tiền tệ (trùng với đơn vị nhỏ nhất của 2 chữ số thập phân nội bộ theo BR-FIN-01, mục Quy tắc chữ số thập phân của tiền tệ) | Hủy bỏ thao tác phân bổ | "Lỗi phân bổ: Tổng giá trị thành phần không khớp với tổng gốc." |

---

### 3.2 BR-FIN-02: Quy tắc bảo toàn số dư ví

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | BR-FIN-02 |
| Tên | Bảo toàn số dư ví |
| Phạm vi áp dụng | Thực thể Ví tiền và toàn bộ các dòng tiền đến từ Giao dịch, Chuyển tiền nội bộ, Công nợ |
| Mục đích nghiệp vụ | Số dư khả dụng của từng ví tiền được tính lại theo thời gian thực từ lịch sử các khoản ghi dòng tiền; nghiêm cấm đổi số dư bằng phép gán trực tiếp; kiểm soát để số dư ví luôn không âm |

#### Công thức

1. Công thức số dư tính lại từ lịch sử giao dịch: Xét ví $w$ tại thời điểm quan sát $T$. Số dư khả dụng $WalletBalance_w(T)$ được xác định duy nhất bằng số dư ban đầu cộng tổng các dòng tiền vào và trừ tổng các dòng tiền ra có đến thời điểm $T$:

$$WalletBalance_w(T) = WalletBalance_w(0) + \sum_{t \le T} \text{Inflow}_w(t) - \sum_{t \le T} \text{Outflow}_w(t)$$

Trong đó:
$$\text{Inflow}_w(t) = Income_w(t) + TransferIn_w(t) + Borrow_w(t) + Collect_w(t) + SavingPayout_w(t) + InvSell_w(t) + InvEvent_w(t) + \max(0, Adjustment_w(t))$$
$$\text{Outflow}_w(t) = Expense_w(t) + TransferOut_w(t) + Repay_w(t) + Lend_w(t) + SavingPrincipal_w(t) + InvBuy_w(t) + \max(0, -Adjustment_w(t))$$

Mỗi số hạng trong công thức được xác định như sau:

| Số hạng | Loại dòng tiền | Dấu | Ý nghĩa | Nguồn |
| --- | --- | --- | --- | --- |
| $WalletBalance_w(0)$ | Số dư ban đầu | + | Số dư lúc tạo ví $w$ | FR-WAL-01 |
| $Income_w(t)$ | Thu nhập | + | Khoản thu nhập được ghi trực tiếp vào ví $w$ bằng giao dịch Thu nhập, không gồm tiền nhận từ sự kiện của tài sản đầu tư (tính ở $InvEvent_w(t)$) | FR-TXN |
| $Expense_w(t)$ | Chi tiêu | − | Khoản chi tiêu chi trả trực tiếp từ ví $w$ | FR-TXN |
| $TransferIn_w(t)$ | Nhận chuyển khoản | + | Khoản tiền nhận được do chuyển từ ví khác vào ví $w$ | FR-TRF |
| $TransferOut_w(t)$ | Chuyển khoản đi | − | Khoản tiền chuyển từ ví $w$ sang ví khác | FR-TRF |
| $Borrow_w(t)$ | Đi vay | + | Khoản tiền nhận về ví $w$ từ hợp đồng đi vay | FR-DEBT |
| $Repay_w(t)$ | Trả nợ đã vay | − | Khoản tiền trừ từ ví $w$ để trả nợ đã vay | FR-DEBT |
| $Lend_w(t)$ | Cho vay | − | Khoản tiền trừ từ ví $w$ để cho người vay | FR-DEBT |
| $Collect_w(t)$ | Thu lại nợ cho vay | + | Khoản tiền đòi lại được từ nợ cho vay, cộng vào ví $w$ | FR-DEBT |
| $SavingPayout_w(t)$ | Tất toán sổ tiết kiệm | + | Khoản tiền gốc và lãi nhận về ví $w$ khi tất toán sổ tiết kiệm | FR-SAV |
| $InvSell_w(t)$ | Bán tài sản đầu tư | + | Khoản tiền thu về ví $w$ từ việc bán tài sản đầu tư | FR-INV |
| $InvEvent_w(t)$ | Tiền nhận từ sự kiện tài sản đầu tư | + | Khoản $Amount$ của sự kiện Nhận tiền mặt hoặc Hoàn vốn cộng vào ví nhận tiền $w$. Dạng Thêm đơn vị cùng loại không tạo dòng tiền | FR-INV-05 |
| $SavingPrincipal_w(t)$ | Gửi tiết kiệm | − | Khoản tiền trừ từ ví $w$ để mở hoặc gửi thêm vào sổ tiết kiệm | FR-SAV |
| $InvBuy_w(t)$ | Mua tài sản đầu tư | − | Khoản tiền trừ từ ví $w$ để mua tài sản đầu tư | FR-INV |
| $Adjustment_w(t)$ | Khoản điều chỉnh số dư | +/− | Chênh lệch từ khoản điều chỉnh số dư theo FR-WAL-05, không thuộc luồng thu, chi, chuyển khoản, vay hoặc trả nợ nào | FR-WAL-05 |

2. Ranh giới dòng tiền phi tiền mặt: Các nghiệp vụ xóa nợ cho vay và miễn trừ nợ đi vay (`WRITE_OFF_LEND`, `WRITE_OFF_BORROW`) chỉ tác động lên sổ ghi công nợ. Các nghiệp vụ này không tạo dòng tiền vào hoặc ra ví tiền: biến động số dư ví bằng 0 VNĐ.
3. Điều kiện về số dư khả dụng:
  - Đối với mọi ví (loại ví thuộc tập hợp {`CASH`, `BANK`, `E_WALLET`}): Số dư khả dụng không âm tại mọi thời điểm quan sát, $WalletBalance_w(T) \ge 0.00 \text{ VNĐ}$.
4. Điều kiện về khoản điều chỉnh số dư:
   - Mỗi lần điều chỉnh số dư theo FR-WAL-05 bắt buộc tạo một bản ghi độc lập, không được sửa, chứa số dư trước điều chỉnh, số dư sau điều chỉnh, giá trị chênh lệch $Adjustment_w(t)$, thời điểm ghi nhận và lý do điều chỉnh.
   - Việc điều chỉnh chỉ áp dụng cho ví có loại ví thuộc tập hợp {`CASH`, `BANK`, `E_WALLET`}.
   - Việc điều chỉnh này không áp dụng cho số dư tính ra từ các thực thể Sổ tiết kiệm, Tài sản đầu tư hoặc Quỹ mục tiêu. Lý do là các tài sản này có cách quản lý và định giá riêng theo BR-FIN-05, BR-FIN-06 và BR-FIN-10.

#### Quy định kỹ thuật và xử lý số học

| Tham số | Quy tắc |
| --- | --- |
| Cách tính toán | Tính lại số dư theo thời gian thực từ lịch sử giao dịch. Hệ thống cấm lưu sẵn trường số dư cho phép ghi đè trực tiếp mà không có khoản ghi dòng tiền tương ứng. |
| Đồng bộ dữ liệu | Mọi thao tác thêm, sửa hoặc xóa giao dịch liên quan đến ví $w$ phải tính lại số dư ngay lập tức trong cùng lần xử lý. |
| Kiểm tra tiền điều kiện | Trước khi thực hiện giao dịch làm giảm số dư ví (chi tiêu, chuyển đi, trả nợ, cho vay, gửi tiết kiệm, mua tài sản đầu tư, điều chỉnh giảm), hệ thống bắt buộc kiểm tra số dư khả dụng. Giao dịch bị từ chối nếu số dư không đủ. |

#### Danh mục mã lỗi và xử lý vi phạm

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-WAL-001` | Giao dịch làm số dư ví âm | Từ chối thực hiện giao dịch, giữ nguyên trạng thái số dư | "Giao dịch bị từ chối: Số dư ví không đủ để thực hiện thao tác." |
| `ERR-WAL-002` | Tên ví mới trùng lặp với tên ví hiện có (không phân biệt chữ hoa, chữ thường sau khi cắt khoảng trắng thừa) | Từ chối tiếp nhận và không tạo bản ghi ví | "Tên ví đã tồn tại." |
| `ERR-WAL-003` | Số dư ban đầu âm ($walletInitialBalance < 0$) khi tạo ví | Từ chối tiếp nhận và không tạo bản ghi ví | "Số dư ban đầu không hợp lệ." |

---

### 3.3 BR-FIN-03: Quy tắc ghi hai vế khi chuyển tiền nội bộ

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | BR-FIN-03 |
| Tên | Ghi hai vế khi chuyển tiền nội bộ |
| Phạm vi áp dụng | Thực thể Chuyển tiền nội bộ, cặp thực thể Ví tiền (ví nguồn $w_{src}$ và ví đích $w_{dst}$), thực thể Giao dịch phụ ghi khoản phí |
| Mục đích nghiệp vụ | Áp dụng ghi hai vế (trừ ví nguồn, cộng ví đích) cho mọi giao dịch chuyển tiền nội bộ; giữ nguyên tổng tiền trong các ví của toàn hệ thống (ngoại trừ trường hợp phát sinh phí); tách riêng phí giao dịch thành một khoản ghi chi tiêu độc lập |

#### Công thức

1. Cặp khoản ghi: Với một lệnh chuyển tiền từ ví nguồn $w_{src}$ tới ví đích $w_{dst}$ với số tiền chuyển $A > 0$:
   - Ví nguồn giảm $A$: $WalletBalance_{w_{src}}(\text{sau}) = WalletBalance_{w_{src}}(\text{trước}) - A$
   - Ví đích tăng $A$: $WalletBalance_{w_{dst}}(\text{sau}) = WalletBalance_{w_{dst}}(\text{trước}) + A$

2. Giữ nguyên tổng tiền trong các ví: Trước khi tính phí dịch vụ ngoài, tổng số dư trên toàn bộ các ví trước và sau chuyển khoản luôn không đổi:

$$\sum_{w \in Wallets} WalletBalance_w(\text{sau}) = \sum_{w \in Wallets} WalletBalance_w(\text{trước})$$

3. Ghi nhận phí dịch vụ chuyển khoản độc lập: Khi có phí chuyển tiền $Fee \ge 0$ do bên thứ ba thu:
   - Số tiền trừ ví nguồn: $A_{srctotal} = A + Fee$
   - Số tiền cộng ví đích: $A_{dstnet} = A$
   - Khoản phí $Fee$ được ghi nhận thành một giao dịch chi tiêu (`Expense`) độc lập từ ví nguồn:
     $$WalletBalance_{w_{src}}(\text{sau}) = WalletBalance_{w_{src}}(\text{trước}) - (A + Fee)$$
     $$\sum_{w \in Wallets} WalletBalance_w(\text{sau}) = \sum_{w \in Wallets} WalletBalance_w(\text{trước}) - Fee$$

#### Quy định kỹ thuật và xử lý số học

| Tham số | Quy tắc |
| --- | --- |
| Ghi nhận đồng thời | Hai khoản ghi trừ ví nguồn và cộng ví đích bắt buộc cùng được ghi nhận hoặc cùng không được ghi nhận. Nếu một trong hai khoản ghi thất bại, số dư cả hai ví phải trở về giá trị trước thao tác. |
| Phân định thực thể | Ví nguồn và ví đích bắt buộc khác nhau: $w_{src} \ne w_{dst}$. |
| Giá trị chuyển cho phép | Số tiền chuyển $A$ bắt buộc lớn hơn 0 ($A > 0.00 \text{ VNĐ}$). Phí chuyển tiền $Fee$ bắt buộc không âm ($Fee \ge 0.00 \text{ VNĐ}$). |
| Điều kiện số dư ví nguồn | Ví nguồn bắt buộc thỏa mãn điều kiện số dư trước khi trừ: $WalletBalance_{w_{src}} \ge A + Fee$. |
| Quy tắc hủy chuyển tiền | Khi hủy chuyển khoản, hệ thống hoàn $A$ về $w_{src}$, trừ $A$ khỏi $w_{dst}$ và chuyển chi phí $Fee$ sang trạng thái `CANCELLED` kèm hoàn phí về $w_{src}$ theo `QD-239`. |

#### Danh mục mã lỗi và xử lý vi phạm

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-TRF-001` | Ví nguồn trùng với ví đích ($w_{src} = w_{dst}$) | Từ chối thực hiện giao dịch | "Giao dịch không hợp lệ: Ví chuyển và ví nhận phải khác nhau." |
| `ERR-TRF-002` | Số tiền chuyển $A \le 0$ hoặc phí $Fee < 0$ | Từ chối thực hiện giao dịch | "Số tiền chuyển hoặc phí chuyển tiền không hợp lệ." |
| `ERR-TRF-003` | Số dư ví nguồn không đủ thanh toán tổng số tiền chuyển và phí ($WalletBalance_{w_{src}} < A + Fee$) | Từ chối thực hiện giao dịch | "Số dư ví nguồn không đủ để thanh toán khoản tiền chuyển và phí dịch vụ." |

---

### 3.4 BR-FIN-04: Quy tắc phân cấp danh mục thu chi 3 cấp

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | BR-FIN-04 |
| Tên | Phân cấp danh mục thu chi 3 cấp |
| Phạm vi áp dụng | Thực thể Danh mục, Nhóm nhu cầu, Loại thu chi và mối quan hệ liên kết từ Giao dịch |
| Mục đích nghiệp vụ | Chuẩn hóa cấu trúc cây phân loại thu chi 3 tầng quan hệ 1:N. Loại thu chi cố định còn Nhóm nhu cầu và Danh mục chi tiết do người dùng quản lý. Giữ cho các liên kết giữa các cấp luôn đúng, không để danh mục nào mất nhóm cha và không để sai lệch báo cáo dòng tiền lịch sử |

#### Công thức

1. Cấu trúc cây phân cấp 3 tầng: Cây phân loại tổ chức theo mô hình phân cấp 3 tầng với quan hệ 1:N. Mỗi Loại thu chi chứa một hoặc nhiều Nhóm nhu cầu, và mỗi Nhóm nhu cầu chứa một hoặc nhiều Danh mục chi tiết.
2. Điều kiện quan hệ cha con duy nhất:
  - Mỗi danh mục chi tiết chỉ được gán vào đúng một Nhóm nhu cầu cha tại một thời điểm.
  - Mỗi Nhóm nhu cầu chỉ được gán vào đúng một Loại thu chi gốc tại một thời điểm.
  - Mọi danh mục đều gắn với một nhóm cha: Danh mục con bắt buộc trực thuộc một nhóm cha hợp lệ đang tồn tại.
3. Giá trị khởi tạo và phần do người dùng quản lý:
  - Cấp 1 (Loại thu chi): Thu nhập (`Income`), Chi tiêu (`Expense`).
  - Cấp 2 (Nhóm nhu cầu): Bộ nhóm mẫu ban đầu, nạp một lần khi hệ thống tạo lần đầu. Người dùng được thêm, đổi tên và xóa nhóm. Tên và mã tiếng Anh của nhóm mẫu không được dùng làm điều kiện trong bất kỳ quy tắc nào.
    - Mẫu thuộc loại Thu nhập: Thu nhập từ lao động, Thu nhập từ tài sản, Thu nhập khác.
    - Nhóm Thu nhập từ tài sản dành cho thu nhập từ tài sản mà người dùng không ghi thành tài sản đầu tư trong hệ thống, ví dụ tiền cho thuê nhà.
    - Khoản thu từ tài sản đầu tư không ghi bằng giao dịch Thu nhập ở bất kỳ nhóm nào của loại này. Khoản đó ghi qua sự kiện Nhận tiền mặt theo FR-INV-05.
    - Mẫu thuộc loại Chi tiêu: Nhu cầu thiết yếu, Nhu cầu cá nhân và giải trí, Nghĩa vụ tài chính và chuyển tiền, Tiết kiệm và đầu tư.
  - Cấp 3 (Danh mục chi tiết): Bộ danh mục mẫu ban đầu do đội phát triển định nghĩa khi triển khai hệ thống, nạp một lần. Người dùng cuối được thêm, đổi tên, xóa theo nhu cầu sử dụng.
4. Kiểm tra trùng tên: Tên Nhóm nhu cầu không được trùng nhóm đang dùng khác trong cùng Loại thu chi. Tên Danh mục chi tiết không được trùng danh mục đang dùng khác trong cùng nhóm. Mục đã xóa không tính vào việc kiểm tra trùng tên.

#### Quy định kỹ thuật và xử lý số học

| Tham số | Quy tắc |
| --- | --- |
| Xóa danh mục | Người dùng chỉ thấy thao tác Xóa. Hộp xác nhận nêu số giao dịch liên quan. Danh mục chưa có giao dịch thì xóa hẳn. Danh mục đã có giao dịch thì chuyển sang trạng thái đã xóa, bị ẩn khỏi danh sách chọn và giao dịch cũ được giữ nguyên. |
| Xóa nhóm | Xóa nhóm kéo theo mọi danh mục con đang dùng, mỗi danh mục xử lý theo quy tắc Xóa danh mục. Nhóm có giao dịch bên trong thì chuyển sang trạng thái đã xóa, nhóm không có thì xóa hẳn. Hộp xác nhận nêu số danh mục sẽ bị ẩn, số danh mục xóa hẳn và tổng số giao dịch được giữ lại. Mỗi Loại thu chi luôn còn ít nhất một nhóm đang dùng. |
| Mục đã xóa | Mục đã xóa không hiện trong danh sách chọn cho giao dịch mới, nhưng vẫn hiện trong báo cáo và lịch sử kèm nhãn (đã xóa). Người dùng chọn Khôi phục (giữ mã cũ, nối lại giao dịch cũ) hoặc Tạo mới (mã mới). Mã định danh không bao giờ dùng lại. Khôi phục nhóm chỉ khôi phục riêng nhóm, danh mục con khôi phục riêng từng cái. |
| Đổi nhóm của danh mục | Danh mục chỉ được chuyển sang Nhóm nhu cầu đang dùng thuộc cùng Loại thu chi. |
| Không được đổi Loại thu chi | Hệ thống cấm thay đổi thuộc tính Loại thu chi của một Nhóm nhu cầu hoặc Danh mục khi đã có giao dịch liên kết để tránh sai lệch dữ liệu phân tích thu chi trong quá khứ. |

#### Danh mục mã lỗi và xử lý vi phạm

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-CAT-001` | Chuyển danh mục sang nhóm thuộc loại thu chi khác, hoặc đổi loại thu chi của nhóm hoặc danh mục đã có giao dịch | Từ chối thay đổi thuộc tính | "Không được phép đổi loại thu chi hoặc chuyển danh mục sang nhóm thuộc loại thu chi khác." |
| `ERR-CAT-002` | Xóa nhóm đang dùng cuối cùng của một Loại thu chi | Từ chối thao tác xóa | "Không thể xóa nhóm cuối cùng của loại thu chi này." |
| `ERR-CAT-003` | Chuyển danh mục sang nhóm đã xóa | Từ chối thao tác chuyển | "Không thể chuyển danh mục sang nhóm đã xóa." |
| `ERR-CAT-004` | Khôi phục khi đã có mục đang dùng cùng tên trong cùng cấp, hoặc khôi phục danh mục khi nhóm của nó còn đã xóa | Từ chối thao tác khôi phục | "Không thể khôi phục vì tên đã được dùng hoặc nhóm chứa mục này đang ở trạng thái đã xóa." |

---

### 3.5 BR-FIN-05: Quy tắc tính lãi tiết kiệm có kỳ hạn

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | BR-FIN-05 |
| Tên | Tính lãi tiết kiệm có kỳ hạn |
| Phạm vi áp dụng | Thực thể Sổ tiết kiệm, logic tính toán lãi suất và dòng tiền tất toán |
| Mục đích nghiệp vụ | Chuẩn hóa mô hình toán tính lãi tiền gửi tiết kiệm; tự động cộng dồn lãi đơn hoặc lãi kép; áp dụng hệ số phạt lên phần lãi đã cộng dồn khi tất toán trước hạn và kiểm soát nguồn vốn tái tục |

#### Công thức

1. Quy đổi kỳ hạn và số ngày thực gửi:
  - Người dùng chỉ định kỳ hạn hợp đồng bằng số tháng gửi tùy chọn $TermMonths$ (số nguyên dương, không giới hạn ở một tập mốc cố định), hoặc chỉ định khoản gửi không kỳ hạn (kỳ hạn `unlimited`).
  - Đối với khoản gửi có kỳ hạn ($TermMonths \in \mathbb{Z}^{+}$), ngày đáo hạn ($MaturityDate$) được xác định bằng cách cộng $TermMonths$ tháng lịch vào ngày gửi ($StartDate$). Nếu tháng đáo hạn không có ngày tương ứng với ngày gửi, ngày đáo hạn là ngày cuối cùng của tháng đó. Số ngày của kỳ hạn ($TermDays$) là số ngày lịch thực tế tính từ ngày gửi đến ngày đáo hạn:
    $$TermDays = MaturityDate - StartDate \quad (\text{ngày lịch thực tế})$$
  - Đối với khoản gửi không kỳ hạn (kỳ hạn `unlimited`), $MaturityDate = null$ và $TermDays = 0$.
  - Số ngày gửi thực tế tính đến ngày đánh giá hoặc tất toán ($Days$):
    $$Days = \max(0, CurrentDate - StartDate)$$
2. Khoản gửi không kỳ hạn (kỳ hạn `unlimited`):
  - Tiến độ hoàn thành kỳ hạn được quy ước luôn đạt giá trị tối đa:
    $$SavingsTermProgress = 1.0$$
  - Lãi đã cộng dồn tính theo phương pháp lãi đơn hàng ngày:
    $$Interest_{unlimited} = \frac{Principal \times Rate \times Days}{365}$$
  - Số dư ghi nhận:
    $$SavingsBalance_s = Principal_s + Interest_{unlimited,s}$$
3. Khoản gửi có kỳ hạn xác định ($TermDays > 0$):
  - Tiến độ hoàn thành kỳ hạn ($SavingsTermProgress$, giá trị từ 0 đến 1, không có đơn vị phần trăm):
    $$SavingsTermProgress = \min\left(1.0, \frac{Days}{TermDays}\right)$$
  - Phương thức tính lãi ($interestMethod$) áp dụng cho khoản gửi có kỳ hạn, thuộc tập hợp {`SIMPLE`, `COMPOUND_MONTHLY`, `PREPAID`, `POSTPAID`}, mặc định `SIMPLE` nếu giá trị này bị bỏ trống. Lãi dự kiến toàn kỳ theo hợp đồng $ExpectedInterest$ được tính theo từng phương thức:
    - Phương thức lãi đơn hoặc trả sau (`SIMPLE`, `POSTPAID`):
      $$ExpectedInterest = \frac{Principal \times Rate \times TermDays}{365}$$
    - Phương thức trả trước (`PREPAID`): $ExpectedInterest$ tính theo công thức lãi đơn nêu trên, được khấu trừ khỏi số tiền trừ từ ví ngay tại thời điểm mở sổ theo FR-SAV-01, số dư danh nghĩa của sổ vẫn ghi nhận đủ $Principal$. Trong suốt kỳ hạn gửi, số dư ghi nhận của sổ luôn là $SavingsBalance_s = Principal_s$ do tiền lãi đã được nhận trước.
    - Phương thức lãi kép định kỳ hàng tháng (`COMPOUND_MONTHLY`): Áp dụng lãi suất danh nghĩa năm $Rate$ đã biết trước, ghép lãi theo $m$ kỳ hàng tháng cố định trong hợp đồng. Khái niệm này dùng tần suất ghép lãi xác định, khác với tỷ suất sinh lời hiệu dụng CAGR ở BR-FIN-08 (CAGR tính ngược từ giá trị thị trường quan sát được, không tính từ lãi suất cam kết trước):
      $$ExpectedInterest = Principal \times \left[\left(1 + \frac{Rate}{12}\right)^{m} - 1\right]$$
      (Trong đó $m = TermMonths$ là số tháng gửi do người dùng chỉ định theo kỳ hạn hợp đồng, số nguyên dương tùy chọn không giới hạn ở một tập mốc cố định).
  - Số dư danh nghĩa ghi nhận tại thời điểm quan sát:
    $$SavingsBalance_s = Principal_s + (ExpectedInterest_s \times SavingsTermProgress_s)$$
    (Riêng phương thức `PREPAID`, $SavingsBalance_s = Principal_s$ do tiền lãi đã được chi trả đầu kỳ).
4. Quy tắc giá trị tất toán:
  - Tất toán đúng hạn hoặc sau hạn ($Days \ge TermDays$): Nhận toàn bộ vốn gốc và 100% lãi cam kết theo hợp đồng:
    $$Payout_{mature} = Principal + ExpectedInterest$$
    Đối với phương thức tính lãi = `PREPAID`, do $ExpectedInterest$ đã được khấu trừ khi mở sổ nên $Payout_{mature} = Principal$, tiền lãi thực nhận tại ngày đáo hạn bằng 0.00 VNĐ, tài sản ròng $NetWorth$ không biến động khi tất toán.
  - Tất toán trước hạn ($Days < TermDays$): Toàn bộ lãi kỳ hạn dự kiến theo phương thức đã chọn bị hủy bỏ. Hệ thống tính lại lãi đã cộng dồn theo công thức lãi đơn trên số ngày thực gửi, không dùng phương thức ban đầu. Sau đó hệ thống áp dụng hệ số phạt tất toán sớm ($EarlyPenaltyFactor$, $0 < EarlyPenaltyFactor \le 1$, do người dùng chỉ định):
    $$AccruedSimpleInterest = \frac{Principal \times Rate \times Days}{365}$$
    $$Payout_{early} = Principal + (EarlyPenaltyFactor \times AccruedSimpleInterest)$$
  - Lãi giảm trừ do tất toán trước hạn:
    $$InterestLoss = (ExpectedInterest \times SavingsTermProgress) - (EarlyPenaltyFactor \times AccruedSimpleInterest)$$
5. Quy tắc tái tục sổ tiết kiệm:
  - Tái tục chỉ được thực hiện ngay sau khi sổ nguồn tất toán đúng hạn ($Days \ge TermDays$).
  - Vốn gốc $Principal'$ của sổ mới được lấy từ một phần hoặc toàn bộ giá trị $Payout_{mature}$ của sổ nguồn, với $0 < Principal' \le Payout_{mature}$.
  - Sổ nguồn giữ nguyên trạng thái `SETTLED_MATURED` vĩnh viễn.
  - Sổ mới được cấp mã sổ tiết kiệm độc lập và không kế thừa lịch sử lãi của sổ nguồn. Người dùng chỉ định lại kỳ hạn, lãi suất và phương thức tính lãi cho sổ mới.
  - Hệ thống ghi nhận phần chênh lệch $Payout_{mature} - Principal'$ (nếu có) vào ví thanh toán do người dùng chỉ định.

#### Quy định kỹ thuật và xử lý số học

| Tham số | Quy tắc |
| --- | --- |
| Cơ sở quy ước năm | Cơ sở tính lãi là 365 ngày trong một năm ($BaseDays = 365$), áp dụng cho mọi phép chia lãi suất theo ngày, kể cả năm nhuận. Số ngày của kỳ hạn $TermDays$ là số ngày lịch thực tế từ ngày gửi đến ngày đáo hạn. |
| Độ chính xác số học | Số vốn gốc $Principal$, tiền lãi và giá trị nhận về $Payout$ yêu cầu độ chính xác 2 chữ số thập phân. Tiền lãi thương mại khi tất toán dùng ngoại lệ làm tròn nửa lên. Ngược lại, các phép tính phân bổ và tỷ lệ tài chính khác áp dụng chế độ làm tròn ngân hàng. Tỷ lệ lãi suất $Rate$ giữ tối thiểu 4 chữ số thập phân (ví dụ 0,0650 biểu thị 6,50%/năm). Hệ số phạt tất toán sớm $EarlyPenaltyFactor$ là tham số cấu hình theo từng giao dịch tất toán trước hạn, không phải hằng số nghiệp vụ cố định, với điều kiện $0 < EarlyPenaltyFactor \le 1$. Số lần cộng lãi vào gốc $m$ của phương thức `COMPOUND_MONTHLY` là số nguyên tháng theo kỳ hạn hợp đồng. |
| Điều kiện tiền kiểm | $Principal > 0.00 \text{ VNĐ}$; $Rate \ge 0$; $TermMonths \in \mathbb{Z}^{+}$ khi khoản gửi có kỳ hạn xác định; $0 < EarlyPenaltyFactor \le 1$ khi tất toán trước hạn; $StartDate \le CurrentDate$; $interestMethod$ thuộc tập hợp {`SIMPLE`, `COMPOUND_MONTHLY`, `PREPAID`, `POSTPAID`}. |

#### Danh mục mã lỗi và xử lý vi phạm

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-SAV-001` | Số tiền gửi ban đầu $\le 0$ hoặc lãi suất $< 0$ | Từ chối tạo sổ tiết kiệm | "Thông số mở sổ tiết kiệm không hợp lệ." |
| `ERR-SAV-002` | Ngày tất toán trước ngày mở sổ | Từ chối thao tác tất toán | "Ngày tất toán không được nhỏ hơn ngày gửi tiền." |
| `ERR-SAV-003` | Yêu cầu tái tục khi sổ tiết kiệm chưa đến hạn | Từ chối thao tác tái tục | "Sổ tiết kiệm chưa đến hạn tất toán, không thể thực hiện tái tục." |
| `ERR-SAV-004` | Số vốn tái tục lớn hơn tổng giá trị nhận về khi tất toán | Từ chối tạo sổ mới | "Số tiền tái tục vượt quá tổng giá trị tất toán của sổ cũ." |

---

### 3.6 BR-FIN-06: Quy tắc định giá và giá vốn tài sản đầu tư

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | BR-FIN-06 |
| Tên | Định giá và giá vốn tài sản đầu tư |
| Phạm vi áp dụng | Tài sản đầu tư, lệnh mua, lệnh bán và biến động giá trị tài sản đầu tư |
| Mục đích nghiệp vụ | Xác định giá gốc, giá vốn, giá vốn bình quân và giá trị thị trường của tài sản đầu tư theo một mô hình thống nhất đơn giá nhân số lượng; quản lý phân bổ giá vốn theo phương pháp nhập trước xuất trước khi bán một phần; tách riêng lãi lỗ đã chốt và lãi lỗ chưa chốt |

#### Công thức

1. Mô hình đơn giá nhân số lượng của tài sản đầu tư:
Mỗi tài sản đầu tư có tên riêng và một nhãn loại tài sản như chứng chỉ quỹ, cổ phiếu, trái phiếu, vàng hoặc bất động sản. Mọi tài sản đầu tư dùng chung một mô hình: số tiền của một lần ghi nhận bằng đơn giá nhân số lượng.
  - Ở lệnh mua, người dùng nhập hai trong ba đại lượng đơn giá, số lượng và số tiền. Ở lệnh bán, người dùng nhập số lượng bán hoặc chọn bán toàn bộ, và một trong hai đại lượng đơn giá bán hoặc số tiền bán nhận về. Khi định giá lại, người dùng nhập một trong hai đại lượng đơn giá hiện hành hoặc giá trị thị trường tổng. Hệ thống tính đại lượng còn lại.
  - Hai đại lượng đã nhập được lưu đúng như nhập, không làm tròn thêm. Số tiền nhập có tối đa 2 chữ số thập phân theo BR-FIN-01, đơn giá và số lượng lưu đúng số chữ số đã nhập. Khi người dùng sửa lệnh mua, hệ thống chỉ tính lại đại lượng còn lại theo cách người dùng chọn ở điều 5; khi định giá lại, hệ thống tính lại giá trị thị trường.
  - Đại lượng số tiền có tên theo ngữ cảnh: số tiền mua ở lệnh mua, số tiền bán nhận về ở lệnh bán, giá trị thị trường ở định giá.
  - Số lượng của tài sản là tổng số đơn vị còn của các lệnh mua và chỉ đọc. Số lượng chỉ đổi qua lệnh mua, lệnh bán, sự kiện thêm đơn vị và sửa lệnh mua, người dùng không sửa trực tiếp con số tổng.
2. Khớp lệnh mua, chi phí mua và giá gốc:
Mỗi lần mua tài sản đầu tư được ghi nhận bằng một lệnh mua có định danh riêng, thuộc đúng một tài sản đầu tư và thuộc tối đa một khoản đầu tư tại một thời điểm.
  - Lệnh mua ghi kèm Thời điểm ghi do hệ thống tự ghi lúc lưu (điều 8).
  - Người dùng nhập chi phí mua $Fee_{buy}$ theo một trong ba hình thức: số tiền cố định (VNĐ), tỷ lệ phần trăm trên số tiền mua, hoặc không có chi phí.
  - Khi nhập theo tỷ lệ phần trăm, tỷ lệ phải nằm trong khoảng từ 0 đến 100.
  - Hệ thống quy đổi và lưu trữ chi phí mua thực tế theo đơn vị VNĐ.

Giá gốc của lệnh mua ($CostBasis_{origin}$) là tổng số tiền mua cộng với chi phí mua:

$$CostBasis_{origin} = Amount_{buy} + Fee_{buy}$$

Số tiền mua $Amount_{buy}$, đơn giá mua $Price_{buy}$ và số lượng đơn vị mua $Units_{buy}$ thỏa $Amount_{buy} = Price_{buy} \times Units_{buy}$. Người dùng nhập hai đại lượng, hệ thống tính đại lượng còn lại:
  - Nhập số tiền mua và đơn giá mua: số lượng đơn vị mua được tính và lưu đầy đủ chữ số, không làm tròn, để tích đơn giá nhân số lượng làm tròn 2 chữ số bằng đúng số tiền mua:

$$Units_{buy} = \frac{Amount_{buy}}{Price_{buy}}$$

  - Nhập số tiền mua và số lượng: đơn giá mua được tính bằng $Amount_{buy}$ chia $Units_{buy}$ và giữ đủ độ chính xác theo quy định kỹ thuật.
  - Nhập đơn giá mua và số lượng: số tiền mua được tính bằng $Price_{buy}$ nhân $Units_{buy}$ và làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng.
  - Không nhập số lượng và chỉ nhập một trong hai đại lượng còn lại: hệ thống điền số lượng $Units_{buy} = 1$, người dùng sửa được bằng cách sửa lệnh mua.

Giá gốc của lệnh mua là đại lượng cố định từ lúc ghi nhận lệnh mua và không thay đổi trong suốt thời gian nắm giữ, trừ khi người dùng sửa lệnh mua theo điều 5.

3. Giá vốn của lệnh mua:
Giá vốn ban đầu của lệnh mua bằng giá gốc. Tổng giá vốn được điều chỉnh trong thời gian nắm giữ khi hoàn vốn hoặc khi bán một phần. Khi nhận thêm đơn vị, tổng giá vốn giữ nguyên và chỉ giá vốn bình quân mỗi đơn vị thay đổi theo mục 3.7. Khi chưa phát sinh điều chỉnh và chưa bán, giá vốn của lệnh mua bằng giá gốc:

$$CostBasis_{remain,i} = CostBasis_{origin,i}$$

Chi phí mua luôn được tính vào giá vốn của lệnh mua. Giá vốn là căn cứ để tính lãi lỗ đã chốt khi bán và lãi lỗ chưa chốt khi định giá.
4. Định giá thị trường của tài sản đầu tư:
Tại ngày định giá có đơn giá hiện hành $CurrentPrice \ge 0$, giá trị thị trường của từng lệnh mua $i$ còn nắm giữ $Units_{remain,i}$ đơn vị là:

$$MarketValue_{remain,i} = Units_{remain,i} \times CurrentPrice$$

Giá trị thị trường của toàn bộ tài sản đầu tư là tổng giá trị thị trường các lệnh mua còn đơn vị:

$$MarketValue_{total} = \sum_{i} MarketValue_{remain,i}$$

Khi định giá lại, người dùng nhập đơn giá hiện hành hoặc giá trị thị trường tổng $MarketValue_{total}$ của tài sản. Nhập giá trị tổng thì đơn giá hiện hành được tính:

$$CurrentPrice = \frac{MarketValue_{total}}{\sum_{i} Units_{remain,i}}$$

Tài sản không còn đơn vị nắm giữ thì không định giá lại. Khi định giá về 0, tài sản vẫn nắm giữ với giá trị 0 và lãi lỗ chưa chốt được tính $UnrealizedPL = 0.00 - CostBasis_{remain} = -CostBasis_{remain}$.

  - Khi ghi nhận hoặc sửa lệnh mua có ngày mua không cũ hơn ngày định giá gần nhất, đơn giá của lệnh mua trở thành đơn giá hiện hành ($CurrentPrice$) và được lưu vào lịch sử giá.
  - Nếu ngày mua bằng ngày định giá gần nhất thì đơn giá mua thay thế bản ghi giá của ngày đó trong lịch sử giá và trở thành đơn giá hiện hành theo QD-242.
  - Khi đơn giá mua trở thành đơn giá hiện hành, mọi lệnh mua còn đơn vị của tài sản được tính lại giá trị thị trường theo đơn giá mới này.
  - Trường hợp lệnh mua ghi lùi ngày mua cũ hơn lần định giá gần nhất, hệ thống chỉ lưu đơn giá mua vào lịch sử giá và giữ nguyên đơn giá hiện hành đang có.
  - Giá trị thị trường của từng lệnh mua được làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng.
  - Khi định giá lại bằng giá trị thị trường tổng, phần dư do làm tròn được dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất. Nhờ vậy tổng các lệnh bằng đúng giá trị đã nhập.
  - Giá trị thị trường của một lệnh mua luôn theo công thức trên, kể cả ngay sau khi ghi lệnh, và không gồm chi phí mua.
  - Khi mua ở đơn giá hiện hành, giá trị này bằng số tiền mua. Khi lệnh mua ghi lùi ngày với đơn giá mua khác đơn giá hiện hành, giá trị khác số tiền mua và phần chênh lệch là lãi lỗ chưa chốt.

5. Khớp lệnh bán, chi phí bán và phương pháp nhập trước xuất trước:
Mỗi lần bán tài sản đầu tư được ghi nhận bằng một lệnh bán có định danh riêng và thuộc đúng một tài sản đầu tư. Lệnh bán ghi nhận ngày bán, Thời điểm ghi do hệ thống tự ghi (điều 8), số lượng bán, đơn giá bán và số tiền bán nhận về cùng chi phí bán do người dùng nhập. Người dùng nhập số lượng bán hoặc chọn bán toàn bộ. Người dùng nhập đúng một trong hai đại lượng đơn giá bán hoặc số tiền bán nhận về, và hệ thống tính đại lượng còn lại.

Hệ thống chỉ xét các lệnh mua và sự kiện đứng trước lệnh bán theo thứ tự xử lý ở điều 8. Số đơn vị nắm giữ xác định ngay trước lệnh bán ($Units_{held,total} = \sum_{i} Units_{held,i}$, với $Units_{held,i}$ của lệnh mua $i$ ngay trước lệnh bán); nếu số đơn vị bán vượt quá số đơn vị nắm giữ ($Units_{sell} > Units_{held,total}$), hệ thống từ chối với `ERR-INV-009`.

Khi bán, hệ thống tự động khấu trừ đơn vị và giá vốn theo phương pháp nhập trước xuất trước:
  - Lệnh mua có ngày mua cũ nhất được xuất bán trước.
  - Các lệnh mua có cùng ngày mua được sắp xếp theo số thứ tự ghi tăng dần.

Khi lệnh mua $i$ bị khấu trừ $Units_{sold,i}$ đơn vị ($Units_{sold,i} \le Units_{remain,i}$), số lượng đơn vị còn lại, giá vốn xuất bán và giá vốn còn lại của lệnh mua $i$ được tính:

$$Units_{remain,i}' = Units_{remain,i} - Units_{sold,i}$$

$$CostBasis_{sold,i} = CostBasis_{remain,i} \times \frac{Units_{sold,i}}{Units_{remain,i}}$$

$$CostBasis_{remain,i}' = CostBasis_{remain,i} - CostBasis_{sold,i}$$

Giá vốn xuất bán $CostBasis_{sold,i}$ được làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng nửa chẵn, giá vốn còn lại là hiệu nên phần dư làm tròn nằm ở giá vốn còn lại và không dồn riêng. Khi bán hết lệnh mua $i$ ($Units_{sold,i} = Units_{remain,i}$), $CostBasis_{sold,i} = CostBasis_{remain,i}$ và không tính theo tỷ lệ (QD-259).

Số lượng bán do người dùng nhập có tối đa 4 chữ số thập phân. Nhập số lượng bán bằng số hiển thị của lệnh vẫn là bán một phần theo số đã nhập, phần dư số lượng đầy đủ ở lại lệnh. Thao tác bán toàn bộ bán hết số lượng đầy đủ còn lại, lệnh về 0 đơn vị và số tiền bán nhận về tính trên số lượng đầy đủ. Nếu Ngày bán chưa có đơn vị nắm giữ thì bán toàn bộ bị từ chối vì số lượng bán phải lớn hơn 0.

Chi phí bán $Fee_{sell} \ge 0$ không được vượt quá tổng số tiền bán ($Fee_{sell} \le GrossProceeds$), số tiền thực nhận $NetAmount = GrossProceeds - Fee_{sell} \ge 0$. Đơn giá bán $Price_{sell} < 0$ báo lỗi `ERR-INV-001`. Số tiền bán nhận về $GrossProceeds \ge 0$ và được phép bằng 0: lệnh bán chốt lỗ bằng toàn bộ giá vốn xuất bán, giao diện bắt người dùng nhập tường minh.

Xử lý tính lại khi ghi lùi ngày:
  - Lệnh bán ghi lùi ngày:
    - Khi ngày bán sớm hơn một lệnh bán hoặc một sự kiện đã ghi, hệ thống sắp xếp lại các lệnh bán theo thứ tự xử lý (ngày bán, rồi số thứ tự ghi) và tính lại khấu trừ từ đầu.
    - Lệnh bán cũ giữ nguyên số đơn vị và số tiền nhận, cập nhật giá vốn xuất bán và lãi lỗ đã chốt.
    - Hệ thống hiển thị tác động trước và sau, cảnh báo quỹ mục tiêu trước khi lưu và ghi dấu vết.
    - Hệ thống cũng tính lại phân bổ của các sự kiện đứng sau lệnh bán mới theo số đơn vị nắm giữ mới.
    - Nếu hoàn vốn đứng sau vượt giá vốn còn lại, phần vượt được cộng vào lãi đã chốt và hiển thị trong cùng bước xác nhận (BR-FIN-07 mục 3.7).
    - Nếu tính lại làm lệnh bán sau đó thiếu đơn vị, hệ thống từ chối toàn bộ với `ERR-INV-009`.
  - Lệnh mua ghi lùi ngày:
    - Khi ngày mua cũ hơn một lệnh mua đã bị khấu trừ bởi lệnh bán, hệ thống tính lại các lệnh bán theo thứ tự thời gian và nhập trước xuất trước.
    - Lệnh bán cũ giữ nguyên số đơn vị và số tiền nhận, cập nhật giá vốn xuất bán và lãi lỗ đã chốt. Hệ thống hiển thị tác động và cảnh báo quỹ mục tiêu trước khi lưu, ghi dấu vết tính lại.
    - Khi ngày mua cũ hơn một sự kiện đã ghi của cùng tài sản, hệ thống cũng tính lại phân bổ của các sự kiện đứng sau theo số đơn vị nắm giữ mới. Hệ thống cũng tính lại các lệnh bán có giá vốn xuất bán thay đổi theo đó. Việc tính lại được thực hiện theo cùng cơ chế xác nhận và dấu vết.
    - Nếu việc tính lại làm một hoàn vốn đứng sau vượt giá vốn còn lại, phần vượt được cộng vào lãi đã chốt và hiển thị trong cùng bước xác nhận.
  - Sự kiện ghi lùi ngày:
    - Khi Ngày nhận của sự kiện thêm đơn vị cùng loại hoặc hoàn vốn sớm hơn một lệnh bán hoặc một sự kiện đã ghi, hệ thống tính lại các lệnh bán và phân bổ của các sự kiện đứng sau.
    - Việc tính lại được thực hiện theo cùng cơ chế như lệnh bán ghi lùi ngày ở trên, theo BR-FIN-07 mục 3.7.
    - Nếu tính lại làm một hoàn vốn đứng sau vượt giá vốn còn lại, phần vượt được cộng vào lãi đã chốt và hiển thị trong cùng bước xác nhận.
  - Sửa lệnh mua:
    - Người dùng sửa đơn giá, số lượng hoặc số tiền mua của lệnh mua theo mô hình hai đại lượng nhập và một đại lượng dẫn xuất.
    - Khi sửa đơn giá hoặc số lượng, người dùng chọn cho lần sửa đó giữ nguyên một trong hai đại lượng còn lại (số tiền mua hoặc đại lượng thứ ba). Đại lượng không được giữ nguyên được tính lại, mặc định giữ nguyên số tiền mua.
    - Khi sửa số tiền mua, số lượng giữ nguyên và đơn giá được tính lại.
    - Chi phí mua nhập theo số tiền giữ nguyên, chi phí nhập theo tỷ lệ phần trăm tính lại trên số tiền mua mới.
    - Giá gốc mới bằng số tiền mua mới cộng chi phí mua mới. Chênh lệch so với giá gốc cũ được trừ thêm từ hoặc hoàn lại vào ví thanh toán của lệnh mua theo BR-FIN-02 và ghi dấu vết.
    - Hệ thống tính lại các lệnh bán và sự kiện phát sinh sau đó theo nhập trước xuất trước (phần hoàn vốn vượt giá vốn còn lại sau khi sửa được cộng vào lãi đã chốt). Hệ thống hiển thị tác động trước và sau, cảnh báo quỹ mục tiêu, chờ người dùng xác nhận mới lưu.
    - Hệ thống từ chối toàn bộ việc sửa khi ví thanh toán không ở trạng thái hoạt động, khi số dư không đủ cho khoản trừ thêm, hoặc khi việc tính lại làm một lệnh bán sau đó thiếu đơn vị (`ERR-INV-009`).
6. Lãi lỗ đã chốt và lãi lỗ chưa chốt:
Lãi lỗ đã chốt phát sinh khi khớp lệnh bán, bằng tổng số tiền bán nhận về trừ chi phí bán $Fee_{sell}$ và trừ tổng giá vốn xuất bán của các lệnh mua:

$$RealizedPL = (GrossProceeds - Fee_{sell}) - \sum_{i} CostBasis_{sold,i}$$

  - Phần hoàn vốn vượt giá vốn còn lại của lệnh mua $i$ ($RealizedPL_{late,i} = \max(ReturnCapital_i - CostBasis_{remain,i}, 0)$) được cộng vào lãi lỗ đã chốt của lệnh mua $i$ và của tài sản tại Ngày nhận. Theo BR-FIN-07 mục 3.7, phần vượt bằng toàn bộ khoản hoàn vốn phân bổ khi tài sản đã hết đơn vị ngay trước sự kiện.
  - Phần vượt này không đổi các lệnh bán trước đó.
  - Lãi lỗ đã chốt của lệnh mua $i$ bằng lãi lỗ của các lệnh bán lấy từ lệnh mua đó cộng $RealizedPL_{late,i}$, và $RealizedPL_{late} = \sum_{i} RealizedPL_{late,i}$.
  - Ký hiệu $RealizedPL$ ở công thức trên là lãi lỗ đã chốt của một lệnh bán, $RealizedPL_i$ là lãi lỗ đã chốt của lệnh mua $i$ (BR-FIN-08), và lãi lỗ đã chốt của tài sản là tổng các $RealizedPL_i$.

Lãi lỗ chưa chốt của từng lệnh mua còn đơn vị là chênh lệch giữa giá trị thị trường và giá vốn còn lại:

$$UnrealizedPL_i = MarketValue_{remain,i} - CostBasis_{remain,i}$$

Lãi lỗ chưa chốt của toàn bộ tài sản đầu tư là tổng lãi lỗ chưa chốt của các lệnh mua còn nắm giữ:

$$UnrealizedPL = MarketValue_{total} - \sum_{i} CostBasis_{remain,i}$$

7. Giá vốn bình quân của tài sản đầu tư:
Bảng tổng hợp hiển thị giá vốn bình quân của các lệnh mua còn đơn vị ($AvgCost$) chỉ nhằm mục đích tham khảo:

$$AvgCost = \frac{\sum_{i} CostBasis_{remain,i}}{\sum_{i} Units_{remain,i}}$$

Giá vốn bình quân không được dùng để tính lãi lỗ hay khấu trừ giá vốn khi bán.

8. Thứ tự xử lý giao dịch của tài sản đầu tư:
Hệ thống tự ghi Thời điểm ghi (ngày và giờ) và số thứ tự ghi của mỗi lệnh mua, lệnh bán và sự kiện lúc người dùng lưu. Số thứ tự ghi tăng dần trong cùng một tài sản và dùng chung cho ba loại giao dịch. Người dùng chỉ nhập ngày giao dịch (ngày mua, ngày bán hoặc Ngày nhận) và không sửa Thời điểm ghi hay số thứ tự ghi. Sửa một giao dịch giữ nguyên Thời điểm ghi và số thứ tự ghi.

  - Các giao dịch của cùng một tài sản được xử lý theo ngày giao dịch tăng dần, cùng ngày thì theo số thứ tự ghi tăng dần.
  - Số đơn vị nắm giữ dùng để kiểm tra số đơn vị bán và để phân bổ một sự kiện là số đơn vị sau khi áp dụng mọi giao dịch đứng trước giao dịch đó.
  - Giao dịch ghi sau không làm đổi kết quả của giao dịch ghi trước trong cùng ngày.
  - Danh sách giao dịch hiển thị thứ tự ghi trong cùng ngày, và khi lưu giao dịch cùng ngày với giao dịch đã ghi, hệ thống thông báo giao dịch được tính sau những giao dịch nào.

#### Quy định kỹ thuật và xử lý số học

| Tham số | Quy tắc |
| --- | --- |
| Kiểm tra tiền điều kiện | $Amount_{buy} > 0.00 \text{ VNĐ}$, $Fee_{buy} \ge 0.00 \text{ VNĐ}$, tỷ lệ chi phí mua $0 \le feeRate \le 100$, $Price_{buy} > 0.00 \text{ VNĐ}$, $Units_{buy} > 0$, $0 < Units_{sell} \le Units_{held,total}$, $NewPrice \ge 0.00 \text{ VNĐ}$, $MarketValue_{total} \ge 0.00 \text{ VNĐ}$, $Price_{sell} \ge 0.00 \text{ VNĐ}$, $GrossProceeds \ge 0.00 \text{ VNĐ}$, $Fee_{sell} \ge 0.00 \text{ VNĐ}$, $Fee_{sell} \le GrossProceeds$. |
| Độ chính xác số học | Số lượng đơn vị $Units$ dẫn xuất được lưu đầy đủ chữ số, không làm tròn, để tích số lượng nhân đơn giá làm tròn 2 chữ số bằng đúng số tiền đã nhập. Số lượng mua và số lượng bán do người dùng nhập có tối đa 4 chữ số thập phân. Hai đại lượng người dùng nhập được lưu đúng như nhập. Đơn giá dẫn xuất được giữ đủ độ chính xác. Tích đơn giá nhân số lượng, làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng, bằng đúng số tiền đã nhập với mọi số lượng trong phạm vi cho phép. Giao diện hiển thị tiền và đơn giá 2 chữ số thập phân và cho phép xem nhiều chữ số hơn, việc hiển thị không đổi giá trị đã lưu. Các đại lượng tiền tệ gồm giá gốc $CostBasis_{origin}$, giá vốn $CostBasis_{remain,i}$, giá trị thị trường $MarketValue_{total}$, chi phí mua $Fee_{buy}$, chi phí bán $Fee_{sell}$, lãi lỗ $RealizedPL$ và $UnrealizedPL$ tính toán với 2 chữ số thập phân theo quy tắc làm tròn ngân hàng. |
| Thứ tự khấu trừ nhập trước xuất trước | Khấu trừ lệnh mua theo ngày mua tăng dần. Trường hợp các lệnh mua có cùng ngày mua, hệ thống sắp xếp theo số thứ tự ghi tăng dần. |

#### Danh mục mã lỗi và xử lý vi phạm

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-INV-001` | Đơn giá mua $\le 0$, hoặc đơn giá bán hoặc đơn giá hiện hành $< 0$ | Từ chối giao dịch hoặc định giá | "Đơn giá mua phải lớn hơn 0, đơn giá bán và đơn giá hiện hành không được âm." |
| `ERR-INV-002` | Số tiền mua $\le 0$, hoặc số tiền mua dẫn xuất làm tròn bằng 0.00, khi ghi hoặc sửa lệnh mua | Từ chối tạo lệnh mua | "Số tiền mua phải lớn hơn 0." |
| `ERR-INV-009` | Số đơn vị bán vượt quá số đơn vị nắm giữ ngay trước lệnh bán theo thứ tự xử lý ở điều 8 ($Units_{sell} > Units_{held,total}$), hoặc việc sửa lệnh mua hay ghi lùi ngày làm một lệnh bán sau đó thiếu đơn vị | Từ chối lệnh bán | "Số đơn vị bán vượt quá số đơn vị nắm giữ tại ngày giao dịch." |
| `ERR-INV-010` | Chi phí mua âm ($Fee_{buy} < 0$) hoặc tỷ lệ phần trăm chi phí mua ngoài khoảng từ 0 đến 100 ($feeRate < 0$ hoặc $feeRate > 100$) | Từ chối tạo lệnh mua | "Chi phí mua không hợp lệ. Tỷ lệ phần trăm phải nằm trong khoảng từ 0 đến 100." |

---

### 3.7 BR-FIN-07: Quy tắc sự kiện của tài sản đầu tư

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | BR-FIN-07 |
| Tên | Sự kiện của tài sản đầu tư |
| Phạm vi áp dụng | Tài sản đầu tư, lệnh mua, ví tiền nhận tiền, các sự kiện nhận tiền, thêm đơn vị và hoàn vốn |
| Mục đích nghiệp vụ | Quản lý ba dạng sự kiện gồm nhận tiền mặt, thêm đơn vị cùng loại và hoàn vốn. Mỗi sự kiện chỉ có một ngày là Ngày nhận, hệ thống phân bổ giá trị cho các lệnh mua theo số đơn vị nắm giữ ngay trước sự kiện theo thứ tự xử lý ở mục 3.6 điều 8. Quy tắc điều chỉnh giá vốn và xử lý thu nhập khi tài sản đã bán hết. |

#### Công thức

1. Phân loại ba dạng sự kiện của tài sản đầu tư:
Mỗi sự kiện phát sinh thuộc đúng một tài sản đầu tư và chỉ có một ngày là Ngày nhận, tức ngày tiền về ví hoặc đơn vị vào tài khoản, không vượt quá ngày hiện tại. Hệ thống chỉ dùng ngày này, không có ngày đăng ký cuối cùng hay ngày hưởng quyền riêng. Người dùng chọn một trong ba dạng sự kiện khi ghi nhận:
   - Nhận tiền mặt: Gồm cổ tức bằng tiền, tiền lãi hoặc tiền cho thuê của tài sản đầu tư đã quản lý trong hệ thống. Đây là thu nhập thực tế làm tăng số dư ví nhận tiền.
   - Thêm đơn vị cùng loại: Gồm cổ phiếu thưởng, cổ tức bằng cổ phiếu hoặc đơn vị nhận thêm của tài sản loại chứng chỉ quỹ. Sự kiện này chỉ làm tăng số lượng đơn vị và chia lại giá vốn, không làm phát sinh dòng tiền vào ví.
   - Hoàn vốn: Khoản tiền hoàn lại một phần giá gốc của tài sản đầu tư hoặc phân phối không phải cổ tức. Khoản này làm giảm giá vốn của tài sản và không được tính vào thu nhập tháng.

Điều kiện chung của cả ba dạng: tài sản đầu tư phải có ít nhất một lệnh mua đứng trước sự kiện theo thứ tự xử lý ở mục 3.6 điều 8 (ngày giao dịch, rồi số thứ tự ghi). Nếu vi phạm, hệ thống từ chối và báo lỗi `ERR-INV-004`. Tài sản đã bán hết trước sự kiện vẫn được ghi nhận sự kiện.

Số đơn vị nắm giữ $Units_{held,i}$ của lệnh mua $i$ tại thời điểm xử lý của một giao dịch được tính như sau:
  - Lấy đơn vị của lệnh mua đó nếu lệnh mua đứng trước giao dịch.
  - Cộng đơn vị nhận thêm từ các sự kiện đứng trước giao dịch.
  - Trừ đơn vị đã bán khấu trừ từ lệnh mua đó theo các lệnh bán đứng trước giao dịch.

Quy tắc phân bổ sự kiện theo số đơn vị nắm giữ:
  - Sự kiện không tính vào cơ sở phân bổ của chính nó.
  - Số tiền hoặc số đơn vị của sự kiện được chia cho từng lệnh mua theo $Units_{held,i}$ ngay trước sự kiện.
  - Nếu tài sản đã hết đơn vị ngay trước sự kiện, hệ thống chia theo số đơn vị ngay trước lệnh bán gần nhất làm tài sản hết đơn vị.

Các sự kiện phân phối tài sản khác loại không nằm trong phạm vi xử lý của hệ thống.

2. Quy tắc sự kiện nhận tiền mặt và phân bổ cho các lệnh mua:
Số tiền nhận được $Amount_{cash}$ được ghi tăng trực tiếp vào số dư của ví nhận tiền do người dùng chọn. Đồng thời, số tiền này được ghi nhận vào báo cáo thu nhập tháng với dòng phân loại riêng thể hiện thu nhập đầu tư.

Số tiền phân bổ cho từng lệnh mua $i$ ($CashIncome_i$) tỷ lệ thuận với số lượng đơn vị lệnh mua đó đang nắm giữ ngay trước sự kiện ($Units_{held,i}$):

$$CashIncome_i = Amount_{cash} \times \frac{Units_{held,i}}{\sum_{j} Units_{held,j}}$$

Số tiền phân bổ cho từng lệnh mua được làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng nửa chẵn. Phần dư do làm tròn được dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua được chia.

3. Quy tắc sự kiện thêm đơn vị cùng loại:
Sự kiện thêm đơn vị cùng loại gồm cổ phiếu thưởng, cổ tức bằng cổ phiếu hoặc đơn vị nhận thêm của tài sản loại chứng chỉ quỹ từ tái đầu tư. Sự kiện này áp dụng cho mọi tài sản đầu tư.

Sự kiện không làm phát sinh dòng tiền vào ví và không làm thay đổi tổng giá vốn của lệnh mua cũng như tài sản đầu tư. Sự kiện làm tăng số lượng đơn vị nắm giữ và làm giảm giá vốn bình quân của từng đơn vị.

Số đơn vị nhận thêm $Units_{bonus}$ được phân bổ cho từng lệnh mua $i$ tỷ lệ thuận với số lượng đơn vị lệnh mua đó đang nắm giữ ngay trước sự kiện ($Units_{held,i}$):

$$Units_{bonus,i} = Units_{bonus} \times \frac{Units_{held,i}}{\sum_{j} Units_{held,j}}$$

  - Số đơn vị phân bổ cho từng lệnh mua được làm tròn 4 chữ số thập phân theo quy tắc làm tròn ngân hàng nửa chẵn.
  - Phần dư do làm tròn được dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua được chia.
  - Số đơn vị nhận thêm là số lượng thật do quỹ phát hành và ghi trên sao kê nên được làm tròn 4 chữ số.
  - Số lượng mua dẫn xuất từ số tiền và đơn giá ở mục 3.6 khác, được lưu đầy đủ chữ số.

Sau khi phân bổ, tổng giá vốn của lệnh mua $i$ ($CostBasis_{remain,i}$, là giá vốn còn lại ngay trước sự kiện) được giữ nguyên. Giá vốn bình quân của lệnh mua $i$ được tính lại bằng cách chia tổng giá vốn cho tổng số đơn vị ngay trước sự kiện cộng với số đơn vị thưởng được phân bổ:

$$UnitPrice'_{cost,i} = \frac{CostBasis_{remain,i}}{Units_{held,i} + Units_{bonus,i}}$$

Khi tài sản đã hết đơn vị ngay trước sự kiện, giá vốn còn lại của các lệnh mua bằng 0 vì đã chuyển hết sang các lệnh bán. Đơn vị nhận thêm khi đó có giá vốn 0 và hệ thống không tính lại các lệnh bán đứng trước sự kiện.

Lệnh mua $i$ không còn đơn vị ngay trước sự kiện ($Units_{held,i} = 0$) và không nhận đơn vị thêm ($Units_{bonus,i} = 0$) theo cơ sở phân bổ của sự kiện, kể cả khi tài sản đã hết đơn vị, thì không áp dụng công thức trên: hệ thống không tính và không hiển thị giá vốn bình quân của lệnh đó, lệnh giữ 0 đơn vị và giá vốn còn lại 0, sự kiện vẫn được ghi (QD-260).

4. Quy tắc sự kiện hoàn vốn và xử lý các trường hợp đặc biệt:
Khoản tiền hoàn vốn $Amount_{return}$ được ghi tăng trực tiếp vào số dư ví nhận tiền do người dùng chọn. Khoản tiền này làm giảm giá vốn của tài sản và các lệnh mua được phân bổ, không làm giảm giá gốc ban đầu và không tính vào thu nhập tháng.

Số tiền hoàn vốn phân bổ cho từng lệnh mua $i$ ($ReturnCapital_i$) tỷ lệ thuận với số lượng đơn vị lệnh mua đó đang nắm giữ ngay trước sự kiện ($Units_{held,i}$):

$$ReturnCapital_i = Amount_{return} \times \frac{Units_{held,i}}{\sum_{j} Units_{held,j}}$$

Số tiền hoàn vốn phân bổ cho từng lệnh mua được làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng nửa chẵn. Phần dư do làm tròn được dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua được chia.

Số tiền hoàn vốn phân bổ cho lệnh mua $i$ làm giảm giá vốn còn lại của lệnh mua đó ngay trước sự kiện nhưng không đưa giá vốn xuống dưới 0. Phần vượt ($ReturnCapital_i > CostBasis_{remain,i}$) không bị từ chối. Giá vốn còn lại về 0 và phần vượt được cộng vào lãi đã chốt của tài sản tại Ngày nhận ($RealizedPL_{late}$), kèm cảnh báo nêu giá vốn còn lại và phần vượt để người dùng xác nhận trước khi lưu:

$$CostBasis_{remain,i}' = \max(CostBasis_{remain,i} - ReturnCapital_i,\ 0)$$

Khi tài sản đã hết đơn vị ngay trước sự kiện, giá vốn còn lại bằng 0 nên toàn bộ khoản hoàn vốn là phần vượt theo quy tắc trên. Khoản này làm tăng ví nhận tiền, không tính vào thu nhập tháng và được cộng vào lãi đã chốt của tài sản tại Ngày nhận ($RealizedPL_{late}$). Các lệnh bán đứng trước sự kiện giữ nguyên giá vốn xuất bán và lãi lỗ đã chốt của chúng.

Đối với cổ tức thuộc kỳ kinh doanh trước ngày mua đầu tiên của tài sản, người dùng ghi nhận khoản tiền này dưới dạng sự kiện hoàn vốn để giảm giá vốn của lệnh mua. Hệ thống phân bổ theo đơn vị nắm giữ ngay trước sự kiện.

  - Khi sự kiện thêm đơn vị cùng loại hoặc hoàn vốn có Ngày nhận sớm hơn một lệnh bán hoặc một sự kiện đã ghi của cùng tài sản, hệ thống tính lại các lệnh bán đứng sau sự kiện. Hệ thống cũng tính lại phân bổ của các sự kiện đứng sau. Việc tính lại được thực hiện theo cơ chế ghi lùi ngày ở mục 3.6.
  - Hệ thống hiển thị tác động trước và sau, cảnh báo quỹ mục tiêu trước khi lưu và ghi dấu vết.
  - Nếu tính lại làm một hoàn vốn đứng sau vượt giá vốn còn lại, phần vượt được cộng vào lãi đã chốt và hiển thị trong cùng bước xác nhận.
  - Lệnh bán đứng trước sự kiện không bị đổi.
  - Sự kiện nhận tiền mặt không đổi giá vốn và số đơn vị nên không tính lại lệnh bán nào.

#### Quy định kỹ thuật và xử lý số học

| Tham số | Quy tắc |
| --- | --- |
| Ranh giới dòng tiền và giá vốn | Sự kiện nhận tiền mặt làm tăng số dư ví nhận tiền và ghi nhận vào thu nhập tháng với dòng phân loại riêng. Sự kiện hoàn vốn làm tăng số dư ví nhận tiền, không tính vào thu nhập tháng và làm giảm giá vốn. Sự kiện thêm đơn vị cùng loại không làm biến động số dư ví tiền và giữ nguyên tổng giá vốn. |
| Mốc thời gian | Mỗi sự kiện chỉ có Ngày nhận và một Thời điểm ghi do hệ thống tự ghi. Thứ tự xử lý là ngày giao dịch, rồi số thứ tự ghi (mục 3.6 điều 8). Số đơn vị nắm giữ ngay trước sự kiện gồm đơn vị mua, đơn vị nhận thêm từ sự kiện đứng trước, trừ đơn vị đã bán đứng trước. Tài sản đã bán hết vẫn nhận sự kiện nếu có lệnh mua đứng trước sự kiện. |
| Phân bổ và xử lý làm tròn | Mọi giá trị tiền mặt, đơn vị thưởng và giá vốn phân bổ đều làm tròn theo quy tắc làm tròn ngân hàng nửa chẵn. Phần dư do làm tròn được dồn toàn bộ vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua được chia. Nếu tài sản đã hết đơn vị, hệ thống chia theo số đơn vị ngay trước lệnh bán gần nhất làm tài sản hết đơn vị. |
| Phần hoàn vốn vượt giá vốn | Phần hoàn vốn vượt giá vốn còn lại của lệnh mua ngay trước sự kiện không bị từ chối. Giá vốn còn lại về 0 và phần vượt được cộng vào lãi đã chốt của tài sản tại Ngày nhận, kèm cảnh báo để người dùng xác nhận. |
| Lãi đã chốt bổ sung | Khoản hoàn vốn nhận khi tài sản đã hết đơn vị được cộng vào lãi đã chốt của tài sản tại Ngày nhận, không đổi các lệnh bán đứng trước. |

#### Danh mục mã lỗi và xử lý vi phạm

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-INV-004` | Sự kiện không có lệnh mua nào đứng trước theo thứ tự xử lý (Ngày nhận trước ngày mua đầu tiên), đối với cả ba dạng sự kiện | Từ chối ghi nhận sự kiện | "Sự kiện không thể đứng trước lệnh mua đầu tiên của tài sản đầu tư." |
| `ERR-INV-006` | Số tiền hoặc số đơn vị nhận thêm của sự kiện $\le 0$ | Từ chối ghi nhận sự kiện | "Số tiền hoặc số đơn vị của sự kiện phải lớn hơn 0." |
| `ERR-INV-015` | Ví nhận tiền của sự kiện Nhận tiền mặt hoặc Hoàn vốn không ở trạng thái `ACTIVE` | Từ chối ghi nhận sự kiện | "Ví nhận tiền không ở trạng thái hoạt động. Vui lòng chọn ví khác." |
| `ERR-INV-016` | Ngày nhận của sự kiện sau ngày hiện tại | Từ chối ghi nhận sự kiện | "Ngày nhận không được sau ngày hiện tại." |

---

### 3.8 BR-FIN-08: Quy tắc tỷ suất sinh lời của tài sản đầu tư

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | BR-FIN-08 |
| Tên | Tỷ suất sinh lời của tài sản đầu tư |
| Phạm vi áp dụng | Lệnh mua, tài sản đầu tư, khoản đầu tư và dòng tiền thực tế |
| Mục đích nghiệp vụ | Xác định thời gian nắm giữ và tính toán tỷ suất từng lệnh mua theo ngưỡng 365 ngày. Tách biệt tỷ suất cả lệnh và tỷ suất phần đang giữ. Xác định tỷ suất nội hoàn theo dòng tiền thật và cơ chế xử lý khi phương trình không hội tụ. |

#### Công thức

1. Quy tắc thời gian nắm giữ:
Thời gian nắm giữ của một lệnh mua ($HoldDays$) được tính bằng số ngày thực tế giữa ngày tính toán $Date_{calc}$ và ngày mua của lệnh $Date_{buy}$:

$$HoldDays = Date_{calc} - Date_{buy}$$

Quy tắc xác định ngày tính toán $Date_{calc}$:
   - Đối với lệnh mua đang nắm giữ: $Date_{calc}$ là ngày hiện tại.
   - Đối với lệnh mua đã bán hết toàn bộ đơn vị: $Date_{calc}$ là ngày thực hiện lệnh bán cuối cùng tất toán toàn bộ số lượng của lệnh mua đó.

2. Quy tắc tỷ suất từng lệnh mua theo ngưỡng 365 ngày:
Tỷ suất từng lệnh mua phản ánh toàn bộ hiệu quả sinh lời của giá gốc, bao gồm cả biến động giá trị thị trường và các khoản lợi ích phát sinh trong suốt thời gian nắm giữ. Mẫu số của công thức luôn là giá gốc ($CostBasis_{origin}$).

Giá trị thị trường hiện tại của lệnh mua $i$ được xác định bằng $MarketValue_{remain,i} = Units_{remain,i} \times CurrentPrice$, với $CurrentPrice$ là đơn giá hiện hành.

Trường hợp thời gian nắm giữ dưới 365 ngày ($HoldDays < 365$), hệ thống tính tỷ suất sinh lời đơn giản và không quy đổi theo năm:

$$ROI_{order,i} = \frac{Proceeds_{sold,i} + MarketValue_{remain,i} + Dividend_i + ReturnCapital_i - CostBasis_{origin,i}}{CostBasis_{origin,i}}$$

Trường hợp thời gian nắm giữ từ 365 ngày trở lên ($HoldDays \ge 365$), hệ thống tính tỷ suất sinh lời kép theo năm:

$$CAGR_{order,i} = \left(\frac{Proceeds_{sold,i} + MarketValue_{remain,i} + Dividend_i + ReturnCapital_i}{CostBasis_{origin,i}}\right)^{\frac{365}{HoldDays}} - 1$$

Trong đó:
   - $Dividend_i$: Tổng thu nhập tiền mặt đã nhận và được phân bổ cho lệnh mua $i$.
   - $ReturnCapital_i$: Tổng số tiền hoàn vốn đã nhận và được phân bổ cho lệnh mua $i$.
   - Trường hợp lệnh mua chưa nhận thu nhập hoặc chưa hoàn vốn: $Dividend_i = 0.00 \text{ VNĐ}$, $ReturnCapital_i = 0.00 \text{ VNĐ}$.
   - $Proceeds_{sold,i}$ được định nghĩa ở khoản 3 và $MarketValue_{remain,i}$ ở mục 3.6 (BR-FIN-06). Trường hợp lệnh mua chưa bán đơn vị nào: $Proceeds_{sold,i} = 0.00 \text{ VNĐ}$.

3. Quy tắc tỷ suất cả lệnh và tỷ suất phần đang giữ:
Đối với lệnh mua đã bán một phần đơn vị, hoặc lệnh mua chưa bán hết nhưng đã phát sinh sự kiện nhận tiền mặt hoặc hoàn vốn, hệ thống tách biệt hai chỉ số tỷ suất sinh lời. Hai chỉ số này gồm tỷ suất cả lệnh và tỷ suất phần đang giữ theo `QD-141`.

Tỷ suất cả lệnh đo lường tổng thể hiệu quả sinh lời của toàn bộ giá gốc của lệnh mua $i$:

$$ROI_{order,i} = \frac{Proceeds_{sold,i} + MarketValue_{remain,i} + Dividend_i + ReturnCapital_i - CostBasis_{origin,i}}{CostBasis_{origin,i}}$$

Trong đó $Proceeds_{sold,i}$ là tổng số tiền thực nhận từ các lệnh bán được phân bổ cho lệnh mua $i$ sau khi đã trừ chi phí bán. Đại lượng $MarketValue_{remain,i}$ là giá trị thị trường của số đơn vị còn đang nắm giữ của lệnh mua $i$.

Quy tắc phân bổ tiền bán thực nhận:
Tiền bán thực nhận $Proceeds_{sold}$ của lệnh bán được phân bổ cho từng lệnh mua theo số đơn vị khấu trừ. Giá trị phân bổ làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng nửa chẵn. Phần dư do làm tròn được dồn toàn bộ vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua bị khấu trừ.

Tỷ suất cả lệnh là chỉ số chính dùng để so sánh hiệu quả và luôn hiển thị từ lúc mua cho đến khi lệnh mua được bán hết. Trường hợp thời gian nắm giữ từ 365 ngày trở lên, tỷ suất cả lệnh được quy đổi kép theo năm; dưới 365 ngày hiển thị tỷ suất đơn giản.

Tỷ suất phần đang giữ đo lường hiệu quả riêng của phần đơn vị còn lại so với giá vốn còn lại của lệnh mua:

$$ROI_{holding,i} = \frac{MarketValue_{remain,i} - CostBasis_{remain,i}}{CostBasis_{remain,i}}$$

Tỷ suất phần đang giữ chỉ hiển thị khi lệnh mua còn đơn vị nắm giữ và luôn là tỷ lệ phần trăm đơn, không quy đổi theo năm. Trường hợp lệnh mua chưa từng bán, chưa nhận tiền và chưa hoàn vốn, hai chỉ số tỷ suất có giá trị bằng nhau và hệ thống hiển thị một dòng duy nhất. Đối với lệnh mua chưa bán hết nhưng đã có sự kiện nhận tiền hoặc hoàn vốn, hoặc đã bán một phần, hệ thống bắt buộc hiển thị đồng thời cả hai chỉ số tỷ suất.

4. Quy tắc lãi lỗ đã chốt và lãi lỗ chưa chốt dạng số tiền:
Hệ thống xác định đồng thời hai đại lượng lãi lỗ bằng số tiền tuyệt đối cho từng lệnh mua và toàn bộ tài sản đầu tư.

Lãi lỗ đã chốt của lệnh mua $i$ phản ánh kết quả lãi hoặc lỗ thực tế của các lệnh bán đã thực hiện:

$$RealizedPL_i = Proceeds_{sold,i} - CostBasis_{sold,i} + RealizedPL_{late,i}$$

Trong đó $CostBasis_{sold,i}$ là tổng giá vốn của các đơn vị đã bán theo phương pháp nhập trước xuất trước. Còn $RealizedPL_{late,i}$ là phần hoàn vốn vượt giá vốn còn lại của lệnh mua $i$ theo BR-FIN-06 mục 3.6 điều 6 (bằng 0 nếu chưa có).

Lãi lỗ chưa chốt của lệnh mua $i$ phản ánh chênh lệch giữa giá trị thị trường hiện tại và giá vốn còn lại của số đơn vị đang nắm giữ:

$$UnrealizedPL_i = MarketValue_{remain,i} - CostBasis_{remain,i}$$

Đối với lệnh mua đã bán hết toàn bộ đơn vị, giá trị lãi lỗ chưa chốt bằng 0.00 VNĐ. Lãi lỗ đã chốt và chưa chốt của toàn bộ tài sản đầu tư là tổng đại số các khoản lãi lỗ tương ứng của từng lệnh mua.

5. Quy tắc tỷ suất nội hoàn theo dòng tiền thực tế:
Tỷ suất nội hoàn phản ánh tỷ suất sinh lời theo dòng tiền thực tế của khoản đầu tư hoặc toàn bộ tài sản đầu tư, có tính đến thời điểm và quy mô của mọi dòng tiền chi ra và thu về. Tỷ suất nội hoàn $r$ là nghiệm thực của phương trình cân bằng hiện giá thuần của chuỗi dòng tiền bằng 0:

$$\sum_{k=0}^{n} \frac{CF_k}{(1 + r)^{\frac{t_k - t_0}{365}}} = 0$$

Trong đó:
   - $t_0$: Ngày phát sinh dòng tiền đầu tiên của tài sản hoặc khoản đầu tư.
   - $t_k$: Ngày phát sinh dòng tiền thứ $k$. Mốc thời gian được tính bằng số ngày thực tế theo lịch dương.
   - $CF_k$: Dòng tiền thuần tại thời điểm $t_k$.
   - Dòng tiền ra (dấu âm): Tiền mua tài sản $InvBuy$ bao gồm cả số tiền mua và chi phí mua ban đầu.
   - Dòng tiền vào (dấu dương): Tiền thực nhận khi bán tài sản sau khi trừ chi phí bán, cổ tức bằng tiền mặt $Dividend$ và các khoản hoàn vốn $ReturnCapital$.
   - Dòng tiền tại ngày tính toán cuối cùng $t_n$: Giá trị thị trường hiện tại của toàn bộ số đơn vị còn đang nắm giữ ($MarketValue_{total}$). Trường hợp tài sản đã bán hết toàn bộ, giá trị thị trường bằng 0.00 VNĐ và dòng tiền cuối kỳ là dòng tiền vào muộn nhất, gồm cả sự kiện nhận sau lệnh bán cuối cùng.

Quy tắc ngưỡng 365 ngày của tỷ suất nội hoàn:
   - Trường hợp khoảng thời gian từ dòng tiền đầu tiên đến thời điểm tính toán dưới 365 ngày ($t_n - t_0 < 365$): Hệ thống không quy đổi theo năm mà hiển thị tỷ suất kỳ tích lũy thực tế của chuỗi dòng tiền theo `QD-118`.
   - Trường hợp khoảng thời gian từ 365 ngày trở lên ($t_n - t_0 \ge 365$): Hệ thống quy đổi sang tỷ suất sinh lời kép theo năm chuẩn hóa.

Cơ chế xử lý khi phương trình không hội tụ hoặc đa nghiệm:
Trường hợp chuỗi dòng tiền đổi dấu nhiều lần khiến giải thuật số học (thuật toán lặp với ngưỡng sai số $10^{-6}$) không tìm được nghiệm thực duy nhất hoặc không hội tụ sau 100 vòng lặp, hoặc chuỗi dòng tiền chỉ có một chiều:
   - Hệ thống hiển thị chỉ số tỷ suất nội hoàn ở trạng thái "N/A" theo `QD-120`.
   - Giao diện người dùng bắt buộc kèm theo dòng ghi chú giải thích bằng ngôn ngữ đời thường. Nội dung ghi chú: "Lịch sử nạp và rút tiền xen kẽ nhiều lần khiến hệ thống không quy đổi được về một mức lãi suất năm tương đương duy nhất. Bạn hãy theo dõi số tiền lãi lỗ thực tế và tỷ lệ lãi trên tổng vốn để đánh giá hiệu quả."
   - Hệ thống giữ nguyên việc tính toán và hiển thị đầy đủ hai chỉ số trực quan không phụ thuộc vào phương trình dòng tiền. Hai chỉ số này là số tiền lãi lỗ thực tế ($RealizedPL$, $UnrealizedPL$) và tỷ suất sinh lời trên tổng giá gốc để người dùng theo dõi hiệu quả đầu tư.

#### Quy định kỹ thuật và xử lý số học

| Tham số | Quy tắc |
| --- | --- |
| Quy tắc thời gian nắm giữ | Thời gian nắm giữ và khoảng cách dòng tiền tính bằng số ngày thực tế giữa hai mốc thời gian theo lịch dương. Chỉ quy đổi tỷ suất kép theo năm khi thời gian nắm giữ đạt tối thiểu 365 ngày. Dưới 365 ngày hiển thị tỷ suất đơn giản hoặc tỷ suất kỳ tích lũy. |
| Phân bổ tiền bán thực nhận | Tiền bán thực nhận phân bổ theo đơn vị khấu trừ, làm tròn 2 chữ số thập phân theo quy tắc làm tròn ngân hàng nửa chẵn. Phần dư làm tròn dồn vào lệnh mua có ngày mua mới nhất, trường hợp cùng ngày mua thì lệnh có số thứ tự ghi lớn nhất, trong các lệnh mua bị khấu trừ. |
| Độ chính xác số học | Mọi tỷ suất sinh lời hiển thị dưới dạng tỷ lệ phần trăm với 2 chữ số thập phân và áp dụng quy tắc làm tròn ngân hàng nửa chẵn theo QD-121. Ngưỡng sai số hội tụ giải thuật số trị tỷ suất nội hoàn là 0.000001, tối đa 100 vòng lặp. |
| Ranh giới dòng tiền | Dòng tiền tính tỷ suất nội hoàn gồm tiền thực chi khi mua, tiền thực nhận khi bán, cổ tức tiền mặt, tiền hoàn vốn và giá trị thị trường hiện tại của số đơn vị còn lại. |
| Cơ chế nghiệm bất định | Khi tỷ suất nội hoàn không hội tụ hoặc đa nghiệm sau 100 vòng lặp, hệ thống hiển thị "N/A" kèm ghi chú giải thích đời thường, bảo toàn số tiền lãi lỗ và tỷ suất trên tổng giá gốc. |

#### Danh mục mã lỗi và xử lý vi phạm

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-INV-007` | Dữ liệu không nhất quán: Ngày tính toán trước ngày mua của lệnh mua | Từ chối tính toán toàn bộ phạm vi chứa lệnh mua đó, thông báo nêu mã lệnh mua không nhất quán | "Dữ liệu ngày tính toán không nhất quán với ngày mua của lệnh." |
| `ERR-INV-008` | Giá gốc hoặc giá vốn còn lại của lệnh mua bằng 0 khi tính tỷ suất | Hiển thị tỷ suất ở trạng thái "N/A", lãi lỗ vẫn tính theo công thức | "Không thể tính tỷ suất sinh lời khi giá gốc hoặc giá vốn còn lại bằng 0." |
| `ERR-INV-012` | Tên khoản đầu tư sau khi bỏ khoảng trắng đầu cuối có độ dài không thuộc khoảng từ 1 đến 50 ký tự | Từ chối tạo hoặc sửa khoản đầu tư | "Tên khoản đầu tư phải có độ dài từ 1 đến 50 ký tự." |
| `ERR-INV-013` | Tên khoản đầu tư trùng lặp với khoản đầu tư khác đã có trong hệ thống (không phân biệt chữ hoa chữ thường) | Từ chối tạo hoặc sửa khoản đầu tư | "Tên khoản đầu tư đã tồn tại trong hệ thống." |
| `ERR-INV-014` | Mã lệnh mua không tồn tại, không thuộc khoản đầu tư khi bỏ gán, hoặc xuất hiện đồng thời ở cả danh sách gán và bỏ gán | Hủy bỏ toàn bộ thao tác gán hoặc bỏ gán | "Thao tác gán lệnh mua không hợp lệ hoặc chứa dữ liệu mâu thuẫn." |

---

### 3.9 BR-FIN-09: Quy tắc theo dõi công nợ

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | BR-FIN-09 |
| Tên | Theo dõi công nợ |
| Phạm vi áp dụng | Thực thể Công nợ, những người vay, người cho vay liên quan và các nghiệp vụ thu lại nợ, thanh toán nợ |
| Mục đích nghiệp vụ | Lập sổ ghi công nợ tách riêng nợ phải thu và nợ phải trả theo từng người vay hoặc người cho vay; giữ cho số tiền còn nợ luôn không âm; đặt ranh giới rõ ràng giữa dòng tiền thực tế và khoản ghi xử lý nợ không dùng tiền mặt |

#### Công thức

1. Phân loại 2 nhánh công nợ tương ứng:
  - Nợ phải thu (`RECEIVABLE`): Quyền đòi tài sản từ người khác khi có giao dịch cho vay (`LEND`), là một phần của tổng tài sản cá nhân.
  - Nợ phải trả (`PAYABLE`): Khoản tiền phải thanh toán cho người cho vay khi có giao dịch đi vay (`BORROW`), là khoản nợ phải trừ khi tính tài sản ròng.
2. Phân loại 6 loại giao dịch nghiệp vụ công nợ:
  - Giao dịch cho vay (`LEND`): Trừ tiền từ ví của Người dùng để chuyển cho người vay mượn. Hệ quả: Giảm số dư ví nguồn, tăng số dư phải thu từ người vay.
  - Giao dịch đi vay (`BORROW`): Tiền nhận vào ví của Người dùng từ người cho vay. Hệ quả: Tăng số dư ví nhận tiền, tăng số dư phải trả cho người cho vay.
  - Giao dịch thu lại nợ (`COLLECT`): Người vay hoàn trả tiền đã mượn trước đó. Hệ quả: Tăng số dư ví nhận tiền, giảm số dư phải thu tương ứng.
  - Giao dịch trả nợ đã vay (`REPAY`): Người dùng trả tiền cho người cho vay. Hệ quả: Giảm số dư ví nguồn, giảm số dư phải trả tương ứng.
  - Giao dịch xóa nợ cho vay khó đòi (`WRITE_OFF_LEND`): Người dùng xác nhận khoản cho vay không thể đòi lại. Hệ quả: Giảm số dư phải thu, ghi nhận tổn thất tài chính, không làm biến động số dư ví tiền.
  - Giao dịch miễn trừ nợ đi vay (`WRITE_OFF_BORROW`): Người cho vay miễn nợ phải trả cho Người dùng. Hệ quả: Giảm số dư phải trả, ghi nhận thu nhập khác, không làm biến động số dư ví tiền.
3. Số tiền còn nợ luôn không âm:
Tại mọi thời điểm quan sát, số tiền còn nợ chi tiết với từng người vay hoặc người cho vay và tổng số tiền còn nợ phải luôn lớn hơn hoặc bằng 0:

$$Receivable_{person} = \sum Lend_{person} - \sum Collect_{person} - \sum \text{WriteOffLend}_{person} \ge 0.00 \text{ VNĐ}$$
$$Payable_{person} = \sum Borrow_{person} - \sum Repay_{person} - \sum \text{WriteOffBorrow}_{person} \ge 0.00 \text{ VNĐ}$$

Tổng hợp toàn hệ thống:

$$Receivables = \sum_{person} Receivable_{person} \ge 0.00 \text{ VNĐ}$$
$$Payables = \sum_{person} Payable_{person} \ge 0.00 \text{ VNĐ}$$

4. Giới hạn thu lại nợ và trả nợ:
  - Tổng số tiền thu lại nợ từ một cá nhân không được vượt quá số dư phải thu còn lại của cá nhân đó:
    $$\text{Amount}_{collect} \le Receivable_{person}$$
  - Tổng số tiền trả nợ cho một người cho vay không được vượt quá số dư phải trả còn lại đối với người cho vay đó:
    $$\text{Amount}_{repay} \le Payable_{person}$$

#### Quy định kỹ thuật và xử lý số học

| Tham số | Quy tắc |
| --- | --- |
| Ranh giới dòng tiền vật lý | Chỉ 4 nghiệp vụ `LEND`, `BORROW`, `COLLECT`, `REPAY` mới tạo ra biến động số dư ví tiền mặt. Hai nghiệp vụ `WRITE_OFF_LEND` và `WRITE_OFF_BORROW` là khoản ghi thuần túy trong sổ công nợ, không được phép tác động vào số dư của bất kỳ ví tiền nào. |
| Tính toàn vẹn số liệu | Mọi khoản tiền trong giao dịch nợ đều tính toán bằng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng. |
| Đối tượng liên kết | Mọi giao dịch nợ bắt buộc gán với một cá nhân ($Person$) xác định, là người vay hoặc người cho vay. Hệ thống không chấp nhận giao dịch công nợ vô danh. |

#### Danh mục mã lỗi và xử lý vi phạm

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-DEBT-001` | Số tiền thu nợ lớn hơn số dư phải thu còn lại của người vay | Từ chối ghi nhận thu nợ | "Số tiền thu lại vượt quá số tiền còn phải thu hiện tại." |
| `ERR-DEBT-002` | Số tiền trả nợ lớn hơn số dư phải trả còn lại đối với người cho vay | Từ chối ghi nhận trả nợ | "Số tiền trả nợ vượt quá số tiền còn phải trả hiện tại." |
| `ERR-DEBT-003` | Thiếu thông tin người vay hoặc người cho vay, hoặc số tiền giao dịch $\le 0$ | Từ chối giao dịch | "Thông tin người vay, người cho vay hoặc số tiền giao dịch công nợ không hợp lệ." |

---

### 3.10 BR-FIN-10: Quy tắc gắn tài sản vào quỹ mục tiêu

| Thuộc tính | Đặc tả kỹ thuật |
| --- | --- |
| Mã | BR-FIN-10 |
| Tên | Gắn tài sản vào quỹ mục tiêu |
| Phạm vi áp dụng | Thực thể Quỹ mục tiêu, phần tiền chia từ ví tiền, các tài sản liên kết Sổ tiết kiệm và lệnh mua của tài sản đầu tư |
| Mục đích nghiệp vụ | Tạo mô hình quỹ mục tiêu tài chính gồm nhiều loại tài sản; kiểm soát việc gắn tài sản tiền mặt, tiết kiệm và lệnh mua của tài sản đầu tư vào quỹ; ngăn việc tính trùng tài sản; giữ nguyên tổng tài sản khi chuyển giữa các quỹ |

#### Công thức

1. Quỹ mục tiêu do người dùng tự tạo, hệ thống không tạo sẵn quỹ nào. Mỗi quỹ có tên và số tiền mục tiêu. Tên quỹ không được trùng với quỹ khác, không phân biệt chữ hoa, chữ thường, sau khi bỏ khoảng trắng thừa ở hai đầu.
2. Cấu trúc tổng hợp tài sản quỹ mục tiêu:
Quỹ mục tiêu là thực thể tổng hợp giá trị từ 3 phần tài sản độc lập:

$$Total_{fund} = Cash_{fund} + \sum_{s \in Savings_{fund}} SavingsBalance_s + \sum_{i \in FundOrders_{fund}} MarketValue_{remain,i}$$

Trong đó:
  - $Cash_{fund}$: Số dư tiền mặt hoặc tiền gửi trong ví được chia thẳng cho quỹ.
  - $Savings_{fund}$: Tập hợp các sổ tiết kiệm ở trạng thái `ACTIVE` đang gắn mã liên kết tới quỹ mục tiêu này. Số dư mỗi sổ $SavingsBalance_s$ theo BR-FIN-05.
  - $FundOrders_{fund}$: Tập hợp các lệnh mua của tài sản đầu tư đang gắn mã liên kết tới quỹ mục tiêu này. Giá trị thị trường $MarketValue_{remain,i} = Units_{remain,i} \times CurrentPrice$ theo BR-FIN-06 và FR-INV-03.
3. Biểu thức tính tiến độ hoàn thành mục tiêu ($GoalFundProgress$):
Trường hợp số tiền mục tiêu $Target > 0.00 \text{ VNĐ}$:

$$GoalFundProgress = \left(\frac{Total_{fund}}{Target}\right) \times 100\%$$

Khi $Target = 0.00 \text{ VNĐ}$, hệ thống xác định $GoalFundProgress = 0.00\%$ hoặc hiển thị "N/A" (không bao giờ gán $100.00\%$).
Khi $GoalFundProgress \ge 100.00\%$, hệ thống ghi nhận quỹ mục tiêu đã đạt mục tiêu tài chính đo được bằng số (`REACHED`).
4. Không tính trùng tài sản: Mỗi sổ tiết kiệm hoặc lệnh mua của tài sản đầu tư chỉ được liên kết với duy nhất một quỹ mục tiêu tại một thời điểm.
5. Giữ nguyên tổng tài sản khi chuyển tài sản giữa các quỹ: Khi chuyển quyền sở hữu của một tài sản $a$ (sổ tiết kiệm hoặc lệnh mua của tài sản đầu tư có giá trị $Value_a$) từ quỹ $f_1$ sang quỹ $f_2$:
   - Quỹ $f_1$ giảm: $Total_{f_1}(\text{sau}) = Total_{f_1}(\text{trước}) - Value_a$
   - Quỹ $f_2$ tăng: $Total_{f_2}(\text{sau}) = Total_{f_2}(\text{trước}) + Value_a$
   - Tổng tài sản trên toàn bộ các quỹ được bảo toàn:
     $$\sum_{f \in Funds} Total_f(\text{sau}) = \sum_{f \in Funds} Total_f(\text{trước})$$
6. Xử lý lệnh mua đã gắn quỹ mục tiêu khi bán và khi bán hết đơn vị:
   - Khi số đơn vị còn của lệnh mua $i$ đã gắn quỹ mục tiêu giảm về 0 do lệnh bán ($Units_{remain,i} = 0$), hệ thống giữ nguyên liên kết giữa lệnh mua và quỹ mục tiêu theo QD-174 và QD-195. Giá trị đóng góp của lệnh mua vào quỹ bằng $MarketValue_{remain,i} = 0.00 \times CurrentPrice = 0.00$ VNĐ theo QD-102, QD-174, QD-184. Hệ thống không xóa liên kết khi lệnh mua bán hết.
   - Trong danh sách thành phần của quỹ, hệ thống ẩn mặc định các lệnh mua đã bán hết ($Units_{remain,i} = 0$). Người dùng có thể bật bộ lọc để xem và các lệnh này hiển thị nhãn "đã bán hết".
   - Khi bán lệnh mua đang gắn quỹ mục tiêu, hệ thống hiển thị thông báo tiền bán thu về vào ví nhận tiền, không tự động chuyển vào quỹ mục tiêu, và tổng tài sản cùng tiến độ của quỹ giảm tương ứng.
7. Xử lý khi có giao dịch ghi lùi ngày:
   - Khi giao dịch ghi lùi ngày làm lệnh mua đã bán hết nắm giữ lại số đơn vị dương ($Units_{remain,i} > 0$), lệnh vẫn thuộc quỹ mục tiêu đã gắn trước đó và giá trị đóng góp được tính lại theo quy tắc BR-FIN-10 (QD-132, QD-142).
   - Hệ thống hiển thị cảnh báo tác động đến quỹ mục tiêu theo QD-142 để người dùng xác nhận trước khi lưu.
8. Quy tắc xóa quỹ mục tiêu:
   - Thao tác xóa quỹ mục tiêu gỡ bỏ toàn bộ liên kết đối với mọi tài sản đang gắn vào quỹ theo QD-123. Tài sản này gồm sổ tiết kiệm, các lệnh mua của tài sản đầu tư (kể cả các lệnh mua đã bán hết có $Units_{remain,i} = 0$). Thao tác xóa quỹ này cũng hoàn trả số tiền mặt đã phân bổ về phần tiền mặt khả dụng chưa phân bổ trong các ví.
   - Sau khi xóa quỹ, tên quỹ được giải phóng hoàn toàn và người dùng có thể tạo lại quỹ mục tiêu mới cùng tên khi có nhu cầu.
9. Hạn mức tiền mặt gắn vào quỹ:
   - Tổng tiền mặt gắn vào toàn bộ các quỹ không vượt tổng tiền trong các ví theo QD-124 và QD-207:
     $$\sum_{f \in Funds} Cash_f \le TotalWalletBalance$$
   - Thao tác gắn hoặc điều chỉnh tiền mặt làm tổng vượt hạn mức bị từ chối với mã lỗi `ERR-FND-005`, quỹ giữ nguyên trạng thái cũ.

#### Quy định kỹ thuật và xử lý số học

| Tham số | Quy tắc |
| --- | --- |
| Độ chính xác số học | Giá trị $Target$, $Total_{fund}$, $Cash_{fund}$ và $MarketValue_{remain,i}$ dùng số thập phân chính xác với 2 chữ số thập phân và quy tắc làm tròn ngân hàng. Tỷ lệ hoàn thành $GoalFundProgress$ giữ 2 chữ số thập phân (tỷ lệ phần trăm). |
| Điều kiện tiền kiểm | $Target \ge 0.00 \text{ VNĐ}$, $Cash_{fund} \ge 0.00 \text{ VNĐ}$. Lệnh mua đã bán hết ($Units_{remain,i} = 0$) có $MarketValue_{remain,i} = 0.00 \text{ VNĐ}$. |
| Ranh giới dòng tiền và liên kết | Tiền bán lệnh mua đang gắn quỹ đổ về ví, không tự động chuyển vào quỹ. Xóa quỹ mục tiêu giải phóng mọi liên kết trên sổ theo dõi và giải phóng tên quỹ, không làm thay đổi số dư thực tế của ví tiền, sổ tiết kiệm hay tài sản đầu tư theo QD-123. |

#### Danh mục mã lỗi và xử lý vi phạm

| Mã lỗi | Điều kiện vi phạm | Hành động của hệ thống | Thông điệp thông báo người dùng |
| :--- | :--- | :--- | :--- |
| `ERR-FND-001` | Gán tài sản đã thuộc quỹ khác khi người dùng không đồng ý chuyển quyền sở hữu | Từ chối thao tác gán tài sản | "Tài sản này đã được phân bổ cho một quỹ mục tiêu khác." |
| `ERR-FND-002` | Đặt số tiền mục tiêu $Target < 0$ VNĐ | Từ chối tạo hoặc điều chỉnh quỹ | "Số tiền mục tiêu không được nhỏ hơn 0 VNĐ." |
| `ERR-FND-003` | Chia hoặc đặt lại số tiền mặt âm vào quỹ mục tiêu | Từ chối thao tác phân bổ hoặc điều chỉnh | "Số tiền phân bổ vào quỹ không được nhỏ hơn 0 VNĐ." |
| `ERR-FND-004` | Đặt tên quỹ khi tạo hoặc đổi tên trùng với quỹ khác, không phân biệt chữ hoa, chữ thường | Từ chối tạo hoặc đổi tên quỹ | "Tên quỹ đã tồn tại." |
| `ERR-FND-005` | Tổng tiền mặt gắn vào các quỹ vượt $TotalWalletBalance$ khi gắn hoặc điều chỉnh tiền mặt | Từ chối thao tác gắn hoặc điều chỉnh tiền mặt | "Tổng tiền mặt gắn vào các quỹ vượt số tiền mặt hiện có trong các ví." |

---

