# 02. Thiết kế Lớp Thực thể và Đối tượng Giá trị

Tài liệu này xác lập bản vẽ thiết kế chi tiết cho toàn bộ các lớp nghiệp vụ thuộc Tầng Nghiệp vụ cốt lõi của Hệ thống Quản lý Tài chính Cá nhân PFMS. Nội dung văn bản thực hiện chuyển đổi các thực thể miền và đối tượng giá trị đã được xác thực trong hồ sơ Phân tích Hướng đối tượng sang cấu trúc lớp cài đặt hoàn chỉnh trên nền tảng ngôn ngữ Java.

Bản vẽ thiết kế tập trung giải quyết ba mục tiêu kỹ thuật trọng tâm: bảo toàn độ chính xác số học thông qua việc xây dựng đối tượng giá trị tiền tệ bất biến kèm quy tắc làm tròn ngân hàng; kiểm soát chặt chẽ tính đóng gói nhằm bảo vệ trạng thái nội tại và duy trì số dư khả dụng không âm tại mọi thời điểm; và tổ chức cấu trúc phân cấp giao dịch kế thừa từ lớp cơ sở trừu tượng nhằm hiện thực hóa tính đa hình trong luồng hạch toán dòng tiền mà không cần sử dụng các cấu trúc điều kiện rẽ nhánh thủ công.

## 2.1 Sơ đồ Lớp Tổng quan

Mục này cung cấp góc nhìn toàn cảnh về cấu trúc tĩnh của Tầng Nghiệp vụ cốt lõi trước khi đi vào đặc tả chi tiết từng thành phần. Bản vẽ trực quan hóa mối quan hệ cấu trúc giữa các lớp thực thể quản trị trạng thái và các lớp đối tượng giá trị bất biến, làm nổi bật các liên kết kế thừa, hợp thành, kết tập và phụ thuộc trong toàn bộ không gian miền tài chính.

Đi kèm với sơ đồ lớp, bảng danh mục lớp và kiểu dữ liệu xác lập bản chỉ mục định danh hoàn chỉnh, tổng hợp đầy đủ trách nhiệm kỹ thuật của từng thành phần và bảo đảm tính truy vết hai chiều nhất quán giữa mô hình phân tích nghiệp vụ và cấu trúc mã nguồn phần mềm.

### 2.1.1 Sơ đồ Cấu trúc các Lớp Thực thể

Sơ đồ dưới đây mô hình hóa cấu trúc tĩnh của 8 lớp thực thể cốt lõi thuộc Tầng Nghiệp vụ. Sơ đồ tập trung thể hiện bốn loại quan hệ cấu trúc hướng đối tượng then chốt gồm kế thừa, hợp thành, kết tập và liên kết, định hình xương sống dòng tiền và phân cấp danh mục của toàn bộ hệ thống:

```mermaid
classDiagram
    direction TB

    class Wallet
    class Transaction {
        <<abstract>>
    }
    class IncomeExpenseTransaction
    class TransferTransaction
    class TransactionFee
    class Category
    class NeedGroup
    class BalanceAdjustmentRecord

    %% Ke thua - Inheritance
    Transaction <|-- IncomeExpenseTransaction
    Transaction <|-- TransferTransaction

    %% Quan he Hop thanh - Composition
    IncomeExpenseTransaction "1" *-- "0..1" TransactionFee : chi phí kèm
    TransferTransaction "1" *-- "0..1" TransactionFee : chi phí kèm

    %% Ket tap - Aggregation
    NeedGroup "1" o-- "*" Category : chứa

    %% Lien ket - Association
    Category "1" <-- "*" IncomeExpenseTransaction : phân loại
    Category "1" <-- "*" TransactionFee : danh mục chi
    Wallet "1" <-- "*" IncomeExpenseTransaction : tác động số dư
    Wallet "1" <-- "*" TransferTransaction : ví nguồn / đích
    Wallet "1" <-- "*" TransactionFee : trích phí
    Wallet "1" <-- "*" BalanceAdjustmentRecord : kiểm kê số dư
```

Bản vẽ thiết lập bốn loại quan hệ cấu trúc hướng đối tượng cốt lõi trong mô hình miền:

1. Quan hệ Kế thừa: Áp dụng để trừu tượng hóa các thuộc tính và hành vi chung của dòng tiền, cho phép các lớp giao dịch cụ thể đa hình hóa việc hạch toán số dư mà không phụ thuộc vào các cấu trúc điều kiện rẽ nhánh thủ công.
2. Quan hệ Hợp thành: Áp dụng cho các thành phần tài chính có sự ràng buộc sinh tử, bảo đảm các khoản chi phí liên kết luôn gắn liền với vòng đời của giao dịch gốc và loại trừ hoàn toàn nguy cơ phát sinh dữ liệu mồ côi.
3. Quan hệ Kết tập: Áp dụng để tổ chức cấu trúc phân cấp danh mục nhiều cấp, cho phép nhóm phân loại cấp trên gom cụm các danh mục chi tiết cấp dưới mà vẫn duy trì tính độc lập của từng phần tử.
4. Quan hệ Liên kết: Thể hiện sự tương tác nghiệp vụ giữa các thực thể có định danh và vòng đời độc lập, bảo đảm mọi biến động tài chính luôn truy nguyên được nguồn tiền chịu tác động và mục đích phân loại.

Các đối tượng giá trị bất biến và kiểu liệt kê nghiệp vụ đóng vai trò là kiểu dữ liệu cấu thành trạng thái của các thực thể trên, được tra cứu tổng hợp tại bảng danh mục ở Mục 2.1.2 và đặc tả chi tiết tại Mục 2.3.

### 2.1.2 Danh mục Lớp và Kiểu Dữ liệu

Bảng dưới đây tổng hợp danh mục toàn bộ các lớp thực thể, đối tượng giá trị và kiểu liệt kê trong Tầng Nghiệp vụ cốt lõi. Toàn bộ các thành phần này được định vị vật lý trong gói mã nguồn `com.pfms.domain.model` và được phân loại theo các nhóm nghiệp vụ tương ứng của hệ thống:

| Tên lớp / Kiểu dữ liệu | Phân loại OOP | Trách nhiệm kỹ thuật chính |
| :--- | :--- | :--- |
| **1. Nhóm Quản lý Ví (WAL)** |  |  |
| `Wallet` | Thực thể | Quản lý nguồn tiền khả dụng, bảo toàn bất biến số dư không âm và tiếp nhận các biến động dòng tiền. |
| `BalanceAdjustmentRecord` | Thực thể | Bản ghi kiểm kê thực tế bất biến, ghi nhận chênh lệch kiểm kê để cân đối số dư ví về đúng thực tế quan sát. |
| `WalletStatus` | Kiểu liệt kê | Trạng thái vòng đời của ví gồm Đang hoạt động (`ACTIVE`) hoặc Đã lưu trữ (`ARCHIVED`). |
| **2. Nhóm Ghi nhận Thu chi (TXN)** |  |  |
| `IncomeExpenseTransaction` | Thực thể | Hiện thực hóa giao dịch thu nhập hoặc chi tiêu tại một ví, liên kết phân loại trực tiếp với danh mục cấp 3. |
| `TransactionFee` | Thực thể | Quản lý khoản chi phí phát sinh phụ thuộc sinh tử kèm theo giao dịch thu chi hoặc chuyển tiền nội bộ. |
| `TransactionType` | Kiểu liệt kê | Xác định chiều tác động của dòng tiền gồm Thu nhập (`INCOME`) hoặc Chi tiêu (`EXPENSE`). |
| **3. Nhóm Chuyển tiền Nội bộ (TRF)** |  |  |
| `Transaction` | Thực thể trừu tượng | Lớp cơ sở trừu tượng định nghĩa các thuộc tính chung và hợp đồng phương thức đa hình cho các biến động dòng tiền. |
| `TransferTransaction` | Thực thể | Hiện thực hóa giao dịch điều chuyển tiền nội bộ giữa hai ví độc lập theo cơ chế bảo toàn tổng tài sản. |
| **4. Nhóm Danh mục Phân loại (CAT)** |  |  |
| `NeedGroup` | Thực thể | Quản lý nhóm nhu cầu cấp 2 gom cụm các danh mục chi tiết, trực thuộc một nhánh loại thu chi cố định. |
| `Category` | Thực thể | Quản lý danh mục chi tiết cấp 3 gán nhãn mục đích cho giao dịch thu chi, áp dụng cơ chế xóa mềm bảo toàn lịch sử. |
| `GroupStatus` | Kiểu liệt kê | Trạng thái vòng đời của nhóm nhu cầu gồm Đang hoạt động (`ACTIVE`) hoặc Đã xóa mềm (`DELETED`). |
| `CategoryStatus` | Kiểu liệt kê | Trạng thái vòng đời của danh mục gồm Đang hoạt động (`ACTIVE`) hoặc Đã xóa mềm (`DELETED`). |
| **5. Nhóm Thống kê và Báo cáo (REP)** |  |  |
| `Money` | Đối tượng giá trị | Bọc giá trị tiền tệ bằng `BigDecimal` kèm quy tắc làm tròn nửa chẵn, loại trừ hoàn toàn sai số dấu chấm động. |
| `TransactionDate` | Đối tượng giá trị | Bọc mốc ngày phát sinh giao dịch, kiểm soát tính hợp lệ lịch pháp và ràng buộc thời gian không vượt quá hiện tại. |
| `NetCashFlow` | Đối tượng giá trị | Đo lường dòng tiền thuần ròng bằng tổng thu nhập trừ tổng chi tiêu trong một kỳ quan sát định kỳ. |
| `SavingsRate` | Đối tượng giá trị | Đo lường tỷ lệ phần trăm tiền tiết kiệm tích lũy được so với tổng thu nhập tạo ra trong kỳ phân tích. |
| `SpendingStructureShare` | Đối tượng giá trị | Đo lường tỷ trọng phần trăm đóng góp của từng nhóm nhu cầu hoặc danh mục chi tiết trên tổng quy mô chi tiêu. |

> [!NOTE]
> **Nhóm Lưu trữ và Quản trị Dữ liệu (DAT):** Các chức năng sao lưu dữ liệu, khôi phục và nạp dữ liệu mẫu được hiện thực hóa thông qua các dịch vụ thuộc Tầng Dịch vụ (`com.pfms.service`) và các bộ điều hợp tệp thuộc Tầng Hạ tầng (`com.pfms.infrastructure`). Tầng Nghiệp vụ cốt lõi không định nghĩa lớp thực thể riêng cho nhóm này nhằm bảo đảm tính độc lập hoàn toàn khỏi hệ thống tệp cục bộ theo nguyên lý Nghịch đảo Phụ thuộc.

## 2.2 Thiết kế các Lớp Thực thể

Mục này đặc tả chi tiết cấu trúc phần mềm cho các lớp thực thể thuộc Tầng Nghiệp vụ cốt lõi. Toàn bộ các lớp thực thể trong mục này được tổ chức thống nhất trong gói `com.pfms.model.entity`. Mỗi thực thể đại diện cho một đối tượng nghiệp vụ có mã định danh duy nhất và duy trì vòng đời biến đổi trạng thái xuyên suốt quá trình vận hành của hệ thống.

Để bảo đảm tính chặt chẽ trong thiết kế hướng đối tượng, mỗi lớp thực thể được quy chuẩn cấu trúc đồng nhất qua ba nội dung:
1. Cấu trúc thuộc tính: Xác định tên trường dữ liệu, kiểu dữ liệu cụ thể, phạm vi truy cập nội bộ và ràng buộc hợp lệ.
2. Phương thức nghiệp vụ: Đặc tả chữ ký phương thức gồm tên hàm, danh sách tham số, kiểu dữ liệu trả về, ngoại lệ ném ra và tóm tắt mô tả nghiệp vụ.
3. Cơ chế đóng gói và quy tắc bất biến: Làm rõ logic kiểm soát tính toàn vẹn dữ liệu, kiểm tra điều kiện tiên quyết trong phương thức khởi tạo và loại bỏ hoàn toàn các hàm gán giá trị tự do nhằm bảo vệ trạng thái nội tại của đối tượng.

### 2.2.1 Lớp Ví

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp Ví trong việc quản lý nguồn tiền và kiểm soát số dư)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`, `UC-WAL-02`, `UC-WAL-03`, `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`, `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-WAL-001`, `ERR-FIN-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc kiểm tra và logic xử lý trạng thái) |

**Cơ chế đóng gói và quy tắc bất biến**

- (Nêu logic kiểm tra tính hợp lệ trong phương thức khởi tạo)
- (Nêu cơ chế bảo vệ số dư khả dụng, không mở setter tự do và duy trì số dư không âm)
- (Nêu quy tắc bảo vệ ví tiền mặt mặc định không bị xóa hoặc lưu trữ)

### 2.2.2 Lớp Giao dịch Cơ sở và Giao dịch Thu Chi

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp trừu tượng Transaction và lớp con IncomeExpenseTransaction trong việc quản lý và hạch toán các khoản thu chi phát sinh)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TXN-02`, `UC-TXN-03`, `UC-TXN-04`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`, `FR-TXN-04`, `FR-TXN-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-CAT-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc kiểm tra và logic xử lý trạng thái) |

**Cơ chế đóng gói và quy tắc bất biến**

- (Nêu cơ chế kế thừa từ Transaction, hàm khởi tạo và tính bất biến của mã giao dịch, ngày giao dịch)
- (Nêu cơ chế đa hình qua các phương thức thực thi và hoàn nguyên dòng tiền tác động lên số dư ví)
- (Nêu quy tắc kiểm soát tính tương thích giữa loại giao dịch và danh mục thu chi)

### 2.2.3 Lớp Giao dịch Chuyển tiền

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp TransferTransaction trong việc điều chuyển dòng tiền giữa hai ví và bảo toàn tổng tài sản)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TRF-01`, `UC-TRF-02`, `UC-TRF-03`
- Yêu cầu chức năng: `FR-TRF-01`, `FR-TRF-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`
- Mã lỗi: `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FEE-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc kiểm tra và logic xử lý trạng thái) |

**Cơ chế đóng gói và quy tắc bất biến**

- (Nêu điều kiện kiểm tra ví nguồn khác ví đích và cả hai ví đều ở trạng thái hoạt động)
- (Nêu cơ chế hạch toán đồng thời trừ ví nguồn và cộng ví đích để bảo toàn tổng tài sản)
- (Nêu quy tắc hoàn nguyên số dư khi hủy bỏ giao dịch chuyển tiền)

### 2.2.4 Lớp Chi phí Giao dịch

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp TransactionFee trong việc ghi nhận phụ phí phát sinh và quan hệ sinh tử với giao dịch gốc)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TXN-03`, `UC-TRF-01`, `UC-TRF-03`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-04`, `FR-TRF-01`, `FR-TRF-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi: `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-WAL-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc kiểm tra và logic xử lý trạng thái) |

**Cơ chế đóng gói và quy tắc bất biến**

- (Nêu quan hệ hợp thành phụ thuộc hoàn toàn vào vòng đời của giao dịch chủ)
- (Nêu quy tắc bắt buộc số tiền phí không âm và danh mục hạch toán phải thuộc loại chi tiêu)
- (Nêu cơ chế trích xuất phí tác động đến số dư ví thanh toán)

### 2.2.5 Lớp Danh mục Chi tiết

Lớp `Category` đại diện cho đơn vị phân loại chi tiết ở cấp 3 trong cấu trúc cây danh mục của hệ thống, đóng vai trò gán nhãn mục đích tài chính cho các giao dịch thu chi và chi phí giao dịch. Lớp này chịu trách nhiệm duy trì tính toàn vẹn tham chiếu phân cấp với nhóm nhu cầu cha và thực thi cơ chế xóa mềm nhằm bảo toàn lịch sử sổ sách tài chính.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-04`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`, `ERR-CAT-004`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `categoryId` | `UUID` | `private final` | Mã định danh duy nhất của danh mục, sinh tự động khi khởi tạo, bất biến và không bao giờ tái sử dụng (theo quy tắc cấu trúc danh mục ba cấp và yêu cầu tùy biến danh mục). |
| `name` | `String` | `private` | Tên gọi danh mục từ 1 đến 50 ký tự sau khi cắt tỉa khoảng trắng hai đầu (`trim`), không được để trống theo quy tắc đặt tên; duy nhất trong cùng nhóm nhu cầu cha đang hoạt động theo cơ chế so sánh không phân biệt hoa thường và kiểm tra trùng lặp. |
| `needGroupId` | `UUID` | `private` | Mã định danh tham chiếu đến nhóm nhu cầu cha cấp 2 trực thuộc; bắt buộc trỏ tới một `NeedGroup` đang hoạt động (`ACTIVE`), loại trừ hoàn toàn danh mục mồ côi hoặc thuộc nhóm đã xóa theo quy tắc cấu trúc danh mục ba cấp. |
| `transactionType` | `TransactionType` | `private final` | Nhánh loại thu chi (`INCOME` hoặc `EXPENSE`) kế thừa từ nhóm nhu cầu cha; mang tính bất biến tuyệt đối sau khởi tạo nhằm bảo vệ tính toàn vẹn dữ liệu lịch sử theo quy tắc cấu trúc danh mục ba cấp, ngăn chặn đổi loại thu chi. |
| `status` | `CategoryStatus` | `private` | Trạng thái vòng đời nhận giá trị Đang hoạt động (`ACTIVE`) hoặc Đã xóa mềm (`DELETED`); mặc định là `ACTIVE` khi tạo mới theo quy tắc cấu trúc danh mục ba cấp và kiểm soát toàn vẹn khi cập nhật. |
| `icon` | `String` | `private` | Ký hiệu hoặc mã biểu tượng đồ họa đại diện trên giao diện người dùng; tùy chọn, độ dài tối đa 50 ký tự phục vụ tùy biến hiển thị. |
| `color` | `String` | `private` | Chuỗi mã màu định dạng Hex (ví dụ `#4CAF50`) phục vụ hiển thị trực quan; tùy chọn, độ dài tối đa 10 ký tự. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `Category` *(Constructor)* | `UUID categoryId, String name, UUID needGroupId, TransactionType transactionType, String icon, String color` | Không có | `BusinessException` | Khởi tạo thực thể danh mục mới ở trạng thái `ACTIVE`; kiểm tra định dạng tên theo quy tắc đặt tên, bắt buộc có mã nhóm cha và loại thu chi kế thừa theo cấu trúc danh mục ba cấp. |
| `getCategoryId` | Không có | `UUID` | Không có | Trả về mã định danh duy nhất của danh mục chi tiết. |
| `getName` | Không có | `String` | Không có | Trả về tên hiển thị hiện tại của danh mục. |
| `getNeedGroupId` | Không có | `UUID` | Không có | Trả về mã định danh nhóm nhu cầu cha cấp 2 mà danh mục trực thuộc. |
| `getTransactionType` | Không có | `TransactionType` | Không có | Trả về loại thu chi (`INCOME` hoặc `EXPENSE`) của danh mục. |
| `getStatus` | Không có | `CategoryStatus` | Không có | Trả về trạng thái hoạt động vòng đời hiện tại của danh mục. |
| `getIcon` | Không có | `String` | Không có | Trả về mã biểu tượng đồ họa đại diện của danh mục. |
| `getColor` | Không có | `String` | Không có | Trả về chuỗi mã màu hiển thị trực quan của danh mục. |
| `isActive` | Không có | `boolean` | Không có | Kiểm tra danh mục có đang ở trạng thái hoạt động (`ACTIVE`) hay không. |
| `isDeleted` | Không có | `boolean` | Không có | Kiểm tra danh mục có đang ở trạng thái đã xóa mềm (`DELETED`) hay không. |
| `rename` | `String newName` | `void` | `BusinessException` | Cập nhật tên danh mục sau khi chuẩn hóa cắt tỉa khoảng trắng (1-50 ký tự), ném ngoại lệ nếu tên để trống; giữ nguyên mã định danh và toàn bộ liên kết lịch sử theo quy tắc đặt tên và bảo toàn dữ liệu lịch sử. |
| `changeNeedGroup` | `UUID newNeedGroupId, TransactionType targetGroupType` | `void` | `BusinessException` | Chuyển danh mục sang nhóm nhu cầu cha mới; ném ngoại lệ nếu nhóm đích khác loại thu chi hoặc nhóm đích đã bị xóa theo quy tắc cấu trúc danh mục ba cấp. |
| `updateAppearance` | `String newIcon, String newColor` | `void` | Không có | Cập nhật biểu tượng nhận diện và mã màu hiển thị của danh mục theo tùy biến người dùng. |
| `markDeleted` | Không có | `void` | Không có | Chuyển trạng thái sang `DELETED` (xóa mềm), ẩn danh mục khỏi danh sách lựa chọn cho giao dịch mới nhưng bảo toàn liên kết với các giao dịch lịch sử và báo cáo theo quy tắc xóa mềm bảo toàn lịch sử và cấu trúc danh mục ba cấp. |
| `restore` | `CategoryStatus parentGroupStatus` | `void` | `BusinessException` | Khôi phục danh mục về trạng thái `ACTIVE`; ném ngoại lệ từ chối nếu nhóm nhu cầu cha vẫn đang ở trạng thái đã xóa theo quy tắc cấu trúc danh mục ba cấp và kiểm soát toàn vẹn khi khôi phục. |

**Cơ chế đóng gói và quy tắc bất biến**

- Ràng buộc liên kết phân cấp cha con: Danh mục chi tiết cấp 3 bắt buộc phải luôn trực thuộc một nhóm nhu cầu cha cấp 2 (`NeedGroup`) hợp lệ đang hoạt động; hệ thống nghiêm cấm tồn tại danh mục mồ côi hoặc tạo danh mục trong nhóm đã xóa theo quy tắc cấu trúc danh mục ba cấp. Thuộc tính `transactionType` được kế thừa trực tiếp từ nhóm cha và mang tính bất biến tuyệt đối; khi chuyển nhóm cha (`changeNeedGroup`), nhóm đích bắt buộc phải đang hoạt động và có cùng loại thu chi, từ chối mọi hành vi chuyển nhóm chéo loại hoặc chuyển vào nhóm đã xóa.
- Quy tắc kiểm soát định danh và tính duy nhất: Thuộc tính `name` được bảo vệ đóng gói chặt chẽ qua phương thức `rename()`, bắt buộc chuẩn hóa cắt tỉa khoảng trắng ở hai đầu (`trim`) với độ dài từ 1 đến 50 ký tự, không được để trống hoặc vượt quá độ dài quy định. Tên danh mục là duy nhất trong phạm vi cùng nhóm nhu cầu cha đang hoạt động, so sánh không phân biệt chữ hoa chữ thường theo quy tắc đặt tên và kiểm tra trùng lặp. Hai danh mục thuộc hai nhóm cha khác nhau được phép trùng tên.
- Cơ chế xóa mềm bảo toàn lịch sử và điều kiện khôi phục: Khi thực hiện xóa danh mục đã phát sinh giao dịch lịch sử, thực thể chuyển sang trạng thái xóa mềm (`DELETED`) thông qua `markDeleted()`. Danh mục bị ẩn khỏi danh sách lựa chọn khi tạo giao dịch mới, nhưng liên kết tham chiếu trong sổ giao dịch và báo cáo tài chính được giữ nguyên vẹn kèm nhãn "(đã xóa)" theo quy tắc xóa mềm bảo toàn dữ liệu lịch sử. Thao tác khôi phục (`restore()`) chỉ được kích hoạt khi nhóm cha trực thuộc đang ở trạng thái `ACTIVE` (không bị xóa) và tên danh mục không bị trùng lặp với một danh mục đang dùng khác trong cùng nhóm cha.

### 2.2.6 Lớp Nhóm Nhu cầu

Lớp `NeedGroup` đại diện cho đơn vị phân loại trung gian ở cấp 2 trong cấu trúc phân cấp danh mục của hệ thống, quản lý việc gom cụm các danh mục chi tiết cấp 3 có cùng tính chất tài chính. Lớp này đóng vai trò điều phối tập hợp danh mục con, làm căn cứ tổng hợp cơ cấu chi tiêu trên báo cáo và bảo toàn ranh giới phân loại theo nhánh thu chi gốc.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-04`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-002`, `ERR-CAT-005`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `needGroupId` | `UUID` | `private final` | Mã định danh duy nhất của nhóm nhu cầu, sinh tự động khi khởi tạo, bất biến và không bao giờ tái sử dụng (theo quy tắc cấu trúc danh mục ba cấp và yêu cầu tùy biến danh mục). |
| `name` | `String` | `private` | Tên gọi nhóm nhu cầu từ 1 đến 50 ký tự sau khi cắt tỉa khoảng trắng (`trim`), không được để trống; duy nhất trong cùng nhánh `TransactionType` đang hoạt động theo cơ chế so sánh không phân biệt hoa thường theo quy tắc đặt tên và kiểm tra trùng lặp. Không được dùng làm điều kiện cứng trong logic xử lý theo quy tắc cấu trúc danh mục. |
| `transactionType` | `TransactionType` | `private final` | Nhánh loại thu chi gốc cấp 1 (`INCOME` hoặc `EXPENSE`); bất biến tuyệt đối sau khi khởi tạo, nghiêm cấm đổi loại thu chi theo quy tắc cấu trúc danh mục ba cấp. |
| `status` | `GroupStatus` | `private` | Trạng thái vòng đời nhận giá trị Đang hoạt động (`ACTIVE`) hoặc Đã xóa mềm (`DELETED`); mặc định là `ACTIVE` khi tạo mới theo quy tắc cấu trúc danh mục ba cấp và kiểm soát toàn vẹn khi cập nhật. |
| `icon` | `String` | `private` | Ký hiệu hoặc biểu tượng đồ họa đại diện của nhóm trên giao diện; tùy chọn, độ dài tối đa 50 ký tự. |
| `color` | `String` | `private` | Chuỗi mã màu định dạng Hex (tối đa 10 ký tự) dùng để phân biệt các nhóm trên biểu đồ tròn cơ cấu thu chi phục vụ hiển thị và phân tích cơ cấu chi tiêu. |
| `categories` | `List<Category>` | `private final` | Tập hợp các danh mục chi tiết cấp 3 trực thuộc trong quan hệ kết tập (`aggregation`); khởi tạo danh sách rỗng, bảo vệ đóng gói không mở setter tự do theo quy tắc cấu trúc danh mục ba cấp. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `NeedGroup` *(Constructor)* | `UUID needGroupId, String name, TransactionType transactionType, String icon, String color` | Không có | `BusinessException` | Khởi tạo nhóm nhu cầu mới ở trạng thái `ACTIVE` với danh sách danh mục con rỗng; kiểm tra định dạng tên theo quy tắc đặt tên và tính hợp lệ theo cấu trúc danh mục ba cấp. |
| `getNeedGroupId` | Không có | `UUID` | Không có | Trả về mã định danh duy nhất của nhóm nhu cầu. |
| `getName` | Không có | `String` | Không có | Trả về tên hiển thị hiện tại của nhóm nhu cầu. |
| `getTransactionType` | Không có | `TransactionType` | Không có | Trả về loại thu chi cố định (`INCOME` hoặc `EXPENSE`) của nhóm. |
| `getStatus` | Không có | `GroupStatus` | Không có | Trả về trạng thái hoạt động vòng đời của nhóm nhu cầu. |
| `getIcon` | Không có | `String` | Không có | Trả về mã biểu tượng đồ họa đại diện của nhóm nhu cầu. |
| `getColor` | Không có | `String` | Không có | Trả về chuỗi mã màu hiển thị của nhóm nhu cầu. |
| `getCategories` | Không có | `List<Category>` | Không có | Trả về danh sách danh mục con dưới dạng chỉ đọc (`Collections.unmodifiableList`) nhằm bảo vệ tính toàn vẹn tập hợp. |
| `isActive` | Không có | `boolean` | Không có | Kiểm tra nhóm nhu cầu có đang ở trạng thái hoạt động (`ACTIVE`) hay không. |
| `isDeleted` | Không có | `boolean` | Không có | Kiểm tra nhóm nhu cầu có đang ở trạng thái đã xóa mềm (`DELETED`) hay không. |
| `rename` | `String newName` | `void` | `BusinessException` | Cập nhật tên nhóm mới sau khi cắt tỉa khoảng trắng (1-50 ký tự), ném ngoại lệ nếu tên để trống; giữ nguyên mã và loại thu chi theo quy tắc đặt tên và bảo toàn dữ liệu. |
| `updateAppearance` | `String newIcon, String newColor` | `void` | Không có | Cập nhật biểu tượng nhận diện và mã màu hiển thị của nhóm nhu cầu phục vụ trực quan hóa báo cáo. |
| `addCategory` | `Category category` | `void` | `BusinessException` | Tiếp nhận thêm danh mục con vào nhóm; ném ngoại lệ nếu nhóm đang bị xóa hoặc danh mục không cùng loại thu chi theo quy tắc cấu trúc danh mục ba cấp. |
| `removeCategory` | `Category category` | `void` | Không có | Loại bỏ một danh mục con khỏi danh sách trực thuộc của nhóm khi danh mục bị xóa cứng hoặc chuyển nhóm theo quy tắc cấu trúc danh mục ba cấp. |
| `markDeleted` | `int activeGroupCountInType` | `void` | `BusinessException` | Chuyển trạng thái sang `DELETED` và kích hoạt xóa lan truyền cho các danh mục con; ném ngoại lệ nếu là nhóm hoạt động duy nhất còn lại của loại thu chi (`activeGroupCountInType <= 1`) theo quy tắc duy trì tối thiểu một nhóm hoạt động. |
| `restore` | Không có | `void` | Không có | Khôi phục trạng thái nhóm từ `DELETED` về `ACTIVE` một cách độc lập; không tự động khôi phục các danh mục con bên trong theo quy tắc phân cấp danh mục và ca sử dụng khôi phục độc lập. |

**Cơ chế đóng gói và quy tắc bất biến**

- Tính bất biến của loại thu chi: Thuộc tính `transactionType` được khai báo `final`, thiết lập duy nhất một lần tại hàm khởi tạo và không có phương thức sửa đổi. Quy tắc này ngăn chặn triệt để hành vi thay đổi loại thu chi của nhóm giữa `INCOME` và `EXPENSE`, bảo đảm tính nhất quán cấu trúc cây phân cấp và không làm sai lệch số liệu thống kê lịch sử theo quy tắc cấu trúc danh mục ba cấp, ngăn chặn đổi loại thu chi gây sai lệch dữ liệu.
- Bảo vệ nhóm nhu cầu duy nhất còn lại: Mỗi nhánh Loại thu chi (`INCOME`, `EXPENSE`) bắt buộc phải duy trì tối thiểu một nhóm nhu cầu ở trạng thái hoạt động (`ACTIVE`). Phương thức `markDeleted()` kiểm tra số lượng nhóm đang hoạt động trong cùng loại; nếu là nhóm hoạt động cuối cùng, hệ thống từ chối xóa và ném ngoại lệ theo quy tắc duy trì tối thiểu một nhóm hoạt động cho mỗi nhánh thu chi.
- Quản lý danh mục con và hiệu ứng xóa lan truyền: Tập hợp `categories` được đóng gói an toàn, chỉ truy xuất qua bản sao hoặc danh sách không thể sửa đổi (`unmodifiableList`). Khi xóa nhóm nhu cầu (`markDeleted`), hệ thống áp dụng cơ chế xóa lan truyền (`cascade`): toàn bộ danh mục con đã có giao dịch được chuyển sang xóa mềm (`DELETED`), còn các danh mục con chưa phát sinh giao dịch được loại bỏ vĩnh viễn khỏi hệ thống theo quy tắc phân cấp danh mục. Khi nhóm được khôi phục (`restore()`), nhóm chuyển về `ACTIVE` nhưng các danh mục con vẫn giữ trạng thái đã xóa để người dùng khôi phục riêng lẻ từng mục theo nhu cầu theo quy tắc cấu trúc danh mục và quy trình khôi phục độc lập.

### 2.2.7 Lớp Bản ghi Điều chỉnh Số dư

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp BalanceAdjustmentRecord trong việc lưu vết kiểm kê đối soát và điều chỉnh số dư thực tế)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc kiểm tra và logic xử lý trạng thái) |

**Cơ chế đóng gói và quy tắc bất biến**

- (Nêu tính bất biến tuyệt đối của bản ghi sau khi khởi tạo, không hỗ trợ chỉnh sửa hay xóa bỏ)
- (Nêu công thức tính độ chênh lệch kiểm kê và ràng buộc số dư quan sát không âm)
- (Nêu nguyên tắc ghi nhận đối soát trực tiếp vào ví mà không sinh ra doanh thu hay chi phí)

## 2.3 Thiết kế các Đối tượng Giá trị

Mục này đặc tả chi tiết cấu trúc phần mềm cho các đối tượng giá trị và kiểu liệt kê thuộc Tầng Nghiệp vụ cốt lõi. Toàn bộ các lớp trong mục này được tổ chức thống nhất trong gói `com.pfms.domain.model`. Khác với thực thể, đối tượng giá trị không có mã định danh độc lập, được định danh duy nhất dựa trên giá trị của các thuộc tính thành phần và duy trì tính bất biến tuyệt đối xuyên suốt vòng đời.

Để bảo đảm tính chặt chẽ trong thiết kế hướng đối tượng, mỗi đối tượng giá trị được quy chuẩn cấu trúc đồng nhất qua ba nội dung:
1. Cấu trúc thuộc tính: Xác định các trường dữ liệu nội bộ được đóng gói cố định (`private final`), kiểu dữ liệu Java bọc bên trong và điều kiện ràng buộc hợp lệ.
2. Phương thức nghiệp vụ: Đặc tả chữ ký phương thức tính toán số học, phép so sánh logic, hàm định dạng kết xuất và cơ chế luôn trả về một thể hiện mới thay vì thay đổi trạng thái hiện tại.
3. Tính bất biến và cơ chế so sánh: Làm rõ logic tự kiểm tra tính hợp lệ ngay khi khởi tạo, loại bỏ hoàn toàn các hàm sửa đổi giá trị và quy tắc ghi đè các phương thức so sánh bình đẳng theo giá trị.

### 2.3.1 Tiền tệ

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp Money trong việc bọc số học BigDecimal, quy chuẩn làm tròn ngân hàng và loại trừ sai số dấu chấm động)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`, `UC-TXN-01`, `UC-TRF-01`, `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-TXN-01`, `FR-TRF-01`, `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc tính toán số học, so sánh hoặc định dạng kết xuất) |

**Tính bất biến và cơ chế so sánh**

- (Nêu kỷ luật bất biến, từ khóa final, loại trừ setter và cơ chế luôn trả về thể hiện mới)
- (Nêu cơ chế so sánh bằng nhau qua equals và hashCode dựa trên giá trị thuộc tính)
- (Nêu việc cài đặt giao diện Comparable để so sánh thứ tự tự nhiên phục vụ sắp xếp)

### 2.3.2 Ngày Giao dịch

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp TransactionDate trong việc bọc LocalDate, kiểm soát lịch pháp và ràng buộc mốc thời gian không vượt quá hiện tại)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TRF-01`, `UC-REP-01`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-03`, `FR-TRF-01`, `FR-REP-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi: `ERR-FIN-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc kiểm tra lịch pháp, so sánh ngày hoặc trích xuất chu kỳ báo cáo) |

**Tính bất biến và cơ chế so sánh**

- (Nêu tính bất biến sau khi khởi tạo và logic kiểm tra ngày không vượt quá hiện tại)
- (Nêu cơ chế so sánh bằng nhau qua equals và hashCode)
- (Nêu việc cài đặt giao diện Comparable để so sánh thứ tự thời gian)

### 2.3.3 Kiểu Liệt kê Loại Giao dịch

(Mô tả khoảng 1 đến 2 câu về vai trò của enum TransactionType trong việc xác lập hai chiều thu chi cố định và ranh giới phân cấp danh mục cấp 1)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-CAT-01`, `UC-CAT-03`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`, `FR-CAT-01`
- Quy tắc nghiệp vụ: `BR-FIN-04`
- Mã lỗi: `ERR-CAT-003`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên giá trị hằng số) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Mô tả giá trị hằng số và quy ước hạch toán dòng tiền) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc kiểm tra hoặc ánh xạ nhãn hiển thị) |

**Tính bất biến và cơ chế so sánh**

- (Nêu tính đóng và bất biến tuyệt đối của các hằng số liệt kê)
- (Nêu cơ chế so sánh bằng nhau qua toán tử so sánh hoặc equals)
- (Nêu vai trò định hướng kiểm tra tính tương thích giữa giao dịch và danh mục)

### 2.3.4 Dòng Tiền Thuần

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp NetCashFlow trong việc đại diện cho dòng tiền ròng có dấu và đo lường thặng dư tài chính theo chu kỳ)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-01`
- Yêu cầu chức năng: `FR-REP-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi: `ERR-FIN-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc tính toán hiệu số thu chi, kiểm tra thâm hụt hoặc định dạng hiển thị) |

**Tính bất biến và cơ chế so sánh**

- (Nêu tính bất biến và khả năng hỗ trợ cả giá trị âm, dương và bằng không)
- (Nêu cơ chế so sánh bằng nhau qua equals và hashCode dựa trên giá trị số học)
- (Nêu việc cài đặt Comparable để đánh giá và so sánh mức thặng dư qua các kỳ)

### 2.3.5 Tỷ lệ Tiết kiệm

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp SavingsRate trong việc đo lường tỷ lệ phần trăm tiền tích lũy và xử lý an toàn mẫu số bằng không)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-01`
- Yêu cầu chức năng: `FR-REP-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi: `ERR-FIN-002`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc tính toán tỷ lệ, xử lý biên khi thu nhập bằng không và định dạng phần trăm) |

**Tính bất biến và cơ chế so sánh**

- (Nêu tính bất biến và xử lý an toàn để tránh lỗi chia cho không khi thu nhập bằng không)
- (Nêu cơ chế so sánh bằng nhau qua equals và hashCode)
- (Nêu việc cài đặt Comparable để so sánh hiệu quả tiết kiệm giữa các kỳ)

### 2.3.6 Tỷ trọng Cơ cấu Chi tiêu

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp SpendingStructureShare trong việc đo lường tỷ trọng đóng góp của từng nhóm chi tiêu và giới hạn khoảng giá trị)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-02`
- Yêu cầu chức năng: `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi: `ERR-FIN-002`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc tính toán tỷ trọng, xử lý biên khi chi tiêu bằng không và định dạng phần trăm) |

**Tính bất biến và cơ chế so sánh**

- (Nêu tính bất biến và ràng buộc giá trị tỷ trọng nằm trong khoảng từ không đến một trăm phần trăm)
- (Nêu cơ chế so sánh bằng nhau qua equals và hashCode)
- (Nêu việc cài đặt Comparable để sắp xếp tỷ trọng chi tiêu giảm dần)
