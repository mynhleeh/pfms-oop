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

Mục này đặc tả chi tiết cấu trúc phần mềm cho các lớp thực thể thuộc Tầng Nghiệp vụ cốt lõi. Toàn bộ các lớp thực thể trong mục này được tổ chức thống nhất trong gói `com.pfms.domain.model`. Mỗi thực thể đại diện cho một đối tượng nghiệp vụ có mã định danh duy nhất và duy trì vòng đời biến đổi trạng thái xuyên suốt quá trình vận hành của hệ thống.

Để bảo đảm tính chặt chẽ trong thiết kế hướng đối tượng, mỗi lớp thực thể được quy chuẩn cấu trúc đồng nhất qua ba nội dung:
1. Cấu trúc thuộc tính: Xác định tên trường dữ liệu, kiểu dữ liệu cụ thể, phạm vi truy cập nội bộ và ràng buộc hợp lệ.
2. Phương thức nghiệp vụ: Đặc tả chữ ký phương thức gồm tên hàm, danh sách tham số, kiểu dữ liệu trả về, ngoại lệ ném ra và tóm tắt mô tả nghiệp vụ.
3. Cơ chế đóng gói và quy tắc bất biến: Làm rõ logic kiểm soát tính toàn vẹn dữ liệu, kiểm tra điều kiện tiên quyết trong phương thức khởi tạo và loại bỏ hoàn toàn các hàm gán giá trị tự do nhằm bảo vệ trạng thái nội tại của đối tượng.

### 2.2.1 Lớp Ví

Lớp `Wallet` đại diện cho nguồn tiền thanh toán của người dùng, tiếp nhận biến động dòng tiền và giữ số dư khả dụng không âm (`BR-FIN-02`). Hệ thống dùng lớp này cho ví tiền mặt mặc định cùng các ví tạo thêm để tính cơ cấu phân bổ tài sản (`FR-WAL-04`).

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`, `UC-WAL-02`, `UC-WAL-03`, `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`, `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-WAL-001`, `ERR-FIN-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `walletId` | `UUID` | `private final` | Mã định danh duy nhất sinh tự động khi tạo mới (`FR-WAL-01`), giữ cố định và không tái sử dụng (`FR-WAL-03`). |
| `name` | `String` | `private` | Chuỗi từ 1 đến 50 ký tự sau khi cắt khoảng trắng hai đầu, duy nhất toàn hệ thống không phân biệt hoa thường (`BR-NAM-01`); từ chối nếu tên sai định dạng (`ERR-NAM-001`) hoặc trùng tên ví (`ERR-NAM-002`). |
| `currentBalance` | `Money` | `private` | Số dư khả dụng có đúng 2 chữ số thập phân (`BR-FIN-01`), khởi tạo bằng `0.00 VNĐ` (`FR-WAL-01`) và luôn không âm (`BR-FIN-02`); báo lỗi nếu sai định dạng số học (`ERR-FIN-001`) hoặc thiếu số dư (`ERR-WAL-001`). |
| `status` | `WalletStatus` | `private` | Trạng thái vòng đời nhận giá trị `ACTIVE` khi tạo mới (`FR-WAL-01`) hoặc chuyển sang `ARCHIVED` khi ngừng giao dịch (`FR-WAL-03`). |
| `isDefault` | `boolean` | `private final` | Cờ đánh dấu ví "Tiền mặt" mặc định; mang giá trị `true` cố định ở ví mặc định để chặn thao tác xóa hoặc lưu trữ (`FR-WAL-03`). |
| `note` | `String` | `private` | Chuỗi ghi chú tùy chọn, dài tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`FR-WAL-01`, `FR-WAL-03`). |
| `createdAt` | `LocalDateTime` | `private final` | Mốc thời gian tạo ví bất biến, dùng để sắp xếp danh sách ví (`FR-WAL-02`). |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `Wallet` | `UUID walletId, String name, String note, boolean isDefault` | `Wallet` | `ValidationException` | Cắt khoảng trắng hai đầu, kiểm tra tên dài 1 đến 50 ký tự (`BR-NAM-01`) và ghi chú tối đa 255 ký tự, gán `status = ACTIVE` (`FR-WAL-01`) cùng `currentBalance = Money.ZERO` (`BR-FIN-02`); báo lỗi nếu tên sai định dạng (`ERR-NAM-001`). |
| `credit` | `Money amount` | `void` | `ValidationException`, `BusinessRuleException` | Kiểm tra ví đang `ACTIVE` và `amount` lớn hơn `0.00 VNĐ` (`BR-FIN-01`), sau đó cộng `amount` vào `currentBalance` (`BR-FIN-02`); từ chối nếu số tiền sai định dạng (`ERR-FIN-001`) hoặc ví ngừng hoạt động (`ERR-WAL-001`). |
| `debit` | `Money amount` | `void` | `ValidationException`, `BusinessRuleException` | Kiểm tra ví đang `ACTIVE`, `amount` lớn hơn `0.00 VNĐ` (`BR-FIN-01`) và `currentBalance` đủ chi trả (`BR-FIN-02`), sau đó trừ `amount` khỏi `currentBalance`; báo lỗi nếu số tiền sai định dạng (`ERR-FIN-001`) hoặc thiếu số dư (`ERR-WAL-001`). |
| `adjustBalance` | `Money actualBalance, TransactionDate adjustedAt, String reason` | `BalanceAdjustmentRecord` | `ValidationException`, `BusinessRuleException` | Kiểm tra ví đang `ACTIVE`, `actualBalance` không âm (`BR-FIN-02`) và khác `currentBalance` (`FR-WAL-05`), cập nhật `currentBalance = actualBalance` rồi trả về bản ghi kiểm kê; từ chối nếu sai định dạng số (`ERR-FIN-001`), số dư âm hoặc ví lưu trữ (`ERR-WAL-001`). |
| `updateInfo` | `String newName, String newNote` | `void` | `ValidationException` | Cắt khoảng trắng hai đầu, kiểm tra `newName` dài 1 đến 50 ký tự (`BR-NAM-01`) và `newNote` tối đa 255 ký tự, cập nhật tên cùng ghi chú mà giữ nguyên số dư (`FR-WAL-03`); báo lỗi nếu tên sai định dạng (`ERR-NAM-001`). |
| `archive` | `Không` | `void` | `BusinessRuleException` | Kiểm tra `isDefault == false` rồi chuyển `status` từ `ACTIVE` sang `ARCHIVED` (`FR-WAL-03`); từ chối lưu trữ nếu thực hiện trên ví mặc định (`ERR-WAL-001`). |
| `activate` | `Không` | `void` | `Không` | Chuyển `status` từ `ARCHIVED` về `ACTIVE` để ví tiếp nhận lại giao dịch mới (`FR-WAL-03`). |
| `hasSufficientBalance` | `Money amount` | `boolean` | `ValidationException` | Trả về `true` khi `currentBalance` lớn hơn hoặc bằng `amount` (`BR-FIN-02`); báo lỗi nếu tham số số tiền truyền vào bị rỗng (`ERR-FIN-001`). |

**Cơ chế đóng gói và quy tắc bất biến**

- Hàm khởi tạo cắt khoảng trắng tên ví, kiểm tra độ dài 1 đến 50 ký tự (`BR-NAM-01`), gán trạng thái `ACTIVE` và đặt `currentBalance` bằng `0.00 VNĐ` (`FR-WAL-01`). Thực thể từ chối tên sai định dạng (`ERR-NAM-001`), còn tầng dịch vụ kiểm tra và chặn trùng tên toàn cục (`ERR-NAM-002`).
- Lớp `Wallet` đóng kín trường `currentBalance` và bỏ phương thức `setCurrentBalance`. Số dư chỉ đổi qua `credit`, `debit` hoặc `adjustBalance` để giữ số dư không âm (`BR-FIN-02`) và chặn khoản chi vượt số dư (`ERR-WAL-001`).
- Từ khóa `final` khóa cố định thuộc tính `isDefault` ngay khi tạo ví "Tiền mặt" mặc định. Nhờ đó, phương thức `archive` và tầng dịch vụ chặn được mọi thao tác lưu trữ hoặc xóa nguồn tiền mặc định của hệ thống (`FR-WAL-03`).

### 2.2.2 Lớp Giao dịch Cơ sở và Giao dịch Thu Chi

Lớp trừu tượng `Transaction` định nghĩa các thuộc tính chung và cặp phương thức đa hình cho mọi biến động dòng tiền. Lớp con `IncomeExpenseTransaction` biểu diễn giao dịch thu (`FR-TXN-01`) hoặc chi (`FR-TXN-02`) gắn với danh mục cấp 3 (`BR-FIN-04`), cập nhật số dư ví cùng phí đi kèm (`BR-FIN-02`).

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TXN-02`, `UC-TXN-03`, `UC-TXN-04`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`, `FR-TXN-04`, `FR-TXN-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-CAT-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `transactionId` | `UUID` | `protected final` | Mã định danh duy nhất khai báo tại `Transaction`, sinh tự động khi tạo mới (`FR-TXN-01`) và giữ cố định suốt vòng đời (`FR-TXN-04`). |
| `amount` | `Money` | `protected` | Số tiền giao dịch khai báo tại `Transaction`, lớn hơn `0.00 VNĐ` với 2 chữ số thập phân (`BR-FIN-01`); báo lỗi nếu số tiền không dương hoặc sai định dạng (`ERR-FIN-001`). |
| `transactionDate` | `TransactionDate` | `protected` | Ngày phát sinh giao dịch khai báo tại `Transaction`, không vượt quá ngày hiện tại (`FR-TXN-01`) và dùng để lọc lịch sử (`FR-TXN-03`). |
| `fee` | `TransactionFee` | `protected` | Thực thể chi phí giao dịch hợp thành (`0..1`) khai báo tại `Transaction`, gắn liền vòng đời với giao dịch gốc (`BR-FIN-02`). |
| `note` | `String` | `protected` | Ghi chú diễn giải khai báo tại `Transaction`, tùy chọn, tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`FR-TXN-01`). |
| `createdAt` | `LocalDateTime` | `protected final` | Mốc thời gian tạo bản ghi bất biến tại `Transaction`, dùng làm tiêu chí sắp xếp phụ khi các giao dịch trùng ngày (`FR-TXN-03`). |
| `transactionType` | `TransactionType` | `private final` | Chiều dòng tiền (`INCOME` hoặc `EXPENSE`) tại `IncomeExpenseTransaction`, cố định theo nhánh danh mục (`BR-FIN-04`) và không đổi khi chỉnh sửa (`FR-TXN-04`). |
| `wallet` | `Wallet` | `private` | Tham chiếu thực thể `Wallet` đang `ACTIVE` chịu tác động dòng tiền tại `IncomeExpenseTransaction` (`FR-TXN-01`); từ chối nếu ví ngừng hoạt động hoặc thiếu số dư (`ERR-WAL-001`). |
| `category` | `Category` | `private` | Tham chiếu thực thể `Category` cấp 3 tại `IncomeExpenseTransaction`, bắt buộc cùng `transactionType` (`BR-FIN-04`); báo lỗi nếu danh mục khác loại thu chi (`ERR-CAT-001`). |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `Transaction` | `UUID transactionId, Money amount, TransactionDate transactionDate, String note, TransactionFee fee` | `Transaction` | `ValidationException` | Hàm khởi tạo `protected` của lớp cha kiểm tra `amount` lớn hơn `0.00 VNĐ` (`BR-FIN-01`), `transactionDate` hợp lệ và `note` tối đa 255 ký tự; từ chối dữ liệu số tiền hoặc ngày sai định dạng (`ERR-FIN-001`). |
| `IncomeExpenseTransaction` | `UUID transactionId, TransactionType transactionType, Wallet wallet, Category category, Money amount, TransactionDate date, String note, TransactionFee fee` | `IncomeExpenseTransaction` | `ValidationException`, `BusinessRuleException` | Gọi `super(...)`, kiểm tra `wallet` đang `ACTIVE` và `category` cấp 3 đang `ACTIVE` khớp `transactionType` (`BR-FIN-04`); báo lỗi nếu ví không hợp lệ (`ERR-WAL-001`) hoặc danh mục sai loại thu chi (`ERR-CAT-001`). |
| `applyToWallet` | `Không` | `void` | `BusinessRuleException` | Ghi đè phương thức trừu tượng của `Transaction`: kiểm tra số dư `wallet` sau khi tính cả `amount` và `fee` không bị âm (`BR-FIN-02`), cộng `amount` nếu `INCOME` (`FR-TXN-01`) hoặc trừ `amount` nếu `EXPENSE` (`FR-TXN-02`) rồi gọi `fee.applyFee()` nếu có phí; từ chối nếu thiếu số dư (`ERR-WAL-001`). |
| `revertFromWallet` | `Không` | `void` | `BusinessRuleException` | Ghi đè phương thức trừu tượng của `Transaction`: kiểm tra số dư `wallet` đủ hoàn nguyên (`BR-FIN-02`), gọi `fee.revertFee()` nếu có phí, rồi trừ `amount` nếu `INCOME` hoặc cộng lại `amount` nếu `EXPENSE` (`FR-TXN-05`); báo lỗi nếu hoàn thu làm âm ví (`ERR-WAL-001`). |
| `updateDetails` | `Wallet newWallet, Category newCategory, Money newAmount, TransactionDate newDate, String newNote, TransactionFee newFee` | `void` | `ValidationException`, `BusinessRuleException` | Kiểm tra `newAmount` dương, `newCategory` cùng `transactionType` (`BR-FIN-04`) và đang `ACTIVE` nếu đổi danh mục mới, xác nhận ví cũ và `newWallet` đủ số dư rồi gọi `revertFromWallet()`, cập nhật thuộc tính và gọi `applyToWallet()` (`FR-TXN-04`); từ chối nếu số tiền sai (`ERR-FIN-001`), lệch loại danh mục (`ERR-CAT-001`) hoặc thiếu số dư (`ERR-WAL-001`). |
| `getFee` | `Không` | `Optional<TransactionFee>` | `Không` | Bọc trường `fee` trong `Optional` tại lớp `Transaction` để truy xuất an toàn khi giao dịch không kèm chi phí (`BR-FIN-02`). |

**Cơ chế đóng gói và quy tắc bất biến**

- Lớp `IncomeExpenseTransaction` khởi tạo qua `super(...)` và khóa `transactionId`, `createdAt` cùng `transactionType` bằng từ khóa `final`. Thiết kế này ngăn việc đổi mã định danh hoặc đảo chiều thu chi khi chỉnh sửa giao dịch (`FR-TXN-04`).
- Hai phương thức `applyToWallet` và `revertFromWallet` kiểm tra trước điều kiện số dư không âm rồi mới cập nhật `Wallet` và `TransactionFee` hợp thành (`BR-FIN-02`). Cách kiểm tra gộp cả tiền gốc lẫn phí giúp số dư ví không bị biến đổi dở dang và báo lỗi khi thiếu tiền (`ERR-WAL-001`).
- Thực thể đối chiếu nhánh của `Category` cấp 3 với `transactionType` trong suốt vòng đời (`BR-FIN-04`). Khi sửa giao dịch (`FR-TXN-04`), lớp cho phép giữ danh mục cũ đã xóa mềm nhưng từ chối danh mục mới đã xóa hoặc khác loại thu chi (`ERR-CAT-001`).

### 2.2.3 Lớp Giao dịch Chuyển tiền

Lớp `TransferTransaction` kế thừa `Transaction` để điều chuyển tiền nội bộ giữa hai ví độc lập (`FR-TRF-01`). Mọi lượt chuyển đều trừ ví nguồn và cộng ví đích cùng lúc, bảo toàn tổng tài sản trước chi phí phát sinh (`BR-FIN-03`).

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TRF-01`, `UC-TRF-02`, `UC-TRF-03`
- Yêu cầu chức năng: `FR-TRF-01`, `FR-TRF-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`
- Mã lỗi: `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FEE-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `transactionId` | `UUID` | `protected final` | Mã định danh giao dịch chuyển tiền kế thừa từ `Transaction`, sinh tự động khi tạo mới (`FR-TRF-01`) và giữ cố định (`FR-TRF-02`). |
| `sourceWallet` | `Wallet` | `private final` | Tham chiếu thực thể `Wallet` nguồn trích tiền, bắt buộc đang `ACTIVE` và khác `destinationWallet` (`BR-FIN-03`); từ chối nếu ví ngừng hoạt động (`ERR-WAL-001`) hoặc trùng ví đích (`ERR-TRF-001`). |
| `destinationWallet` | `Wallet` | `private final` | Tham chiếu thực thể `Wallet` đích nhận tiền, bắt buộc đang `ACTIVE` và khác `sourceWallet` (`BR-FIN-03`); từ chối nếu ví ngừng hoạt động (`ERR-WAL-001`) hoặc trùng ví nguồn (`ERR-TRF-001`). |
| `amount` | `Money` | `protected` | Số tiền điều chuyển kế thừa từ `Transaction`, có 2 chữ số thập phân (`BR-FIN-01`) và lớn hơn `0.00 VNĐ` (`BR-FIN-03`); báo lỗi nếu số tiền chuyển không dương (`ERR-TRF-002`). |
| `transactionDate` | `TransactionDate` | `protected` | Ngày thực hiện chuyển tiền kế thừa từ `Transaction`, không vượt quá ngày hiện tại (`FR-TRF-01`). |
| `fee` | `TransactionFee` | `protected` | Thực thể chi phí chuyển tiền hợp thành (`0..1`) kế thừa từ `Transaction`, trích từ `sourceWallet` khi có phát sinh phí (`BR-FIN-02`); từ chối nếu chi phí âm (`ERR-FEE-001`). |
| `note` | `String` | `protected` | Ghi chú mục đích chuyển tiền kế thừa từ `Transaction`, tùy chọn, tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`FR-TRF-01`). |
| `createdAt` | `LocalDateTime` | `protected final` | Mốc thời gian tạo bản ghi bất biến kế thừa từ `Transaction`, phục vụ tra cứu và sắp xếp lịch sử (`FR-TRF-02`). |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `TransferTransaction` | `UUID transactionId, Wallet sourceWallet, Wallet destinationWallet, Money amount, TransactionDate date, String note, TransactionFee fee` | `TransferTransaction` | `ValidationException`, `BusinessRuleException` | Kiểm tra hai ví đang `ACTIVE`, có mã định danh khác nhau và `amount` lớn hơn `0.00 VNĐ` (`BR-FIN-03`); báo lỗi nếu trùng ví chuyển nhận (`ERR-TRF-001`), số tiền không dương (`ERR-TRF-002`), phí âm (`ERR-FEE-001`) hoặc ví không hoạt động (`ERR-WAL-001`). |
| `applyToWallet` | `Không` | `void` | `BusinessRuleException` | Ghi đè phương thức lớp cha: kiểm tra `sourceWallet` đủ trả tổng `amount` và phí (`BR-FIN-02`), trừ `sourceWallet`, gọi `fee.applyFee()` nếu có và cộng `amount` vào `destinationWallet` (`BR-FIN-03`); từ chối nếu ví nguồn thiếu số dư (`ERR-WAL-001`). |
| `revertFromWallet` | `Không` | `void` | `BusinessRuleException` | Ghi đè phương thức lớp cha khi xóa giao dịch (`FR-TRF-02`): kiểm tra `destinationWallet` có số dư tối thiểu bằng `amount` (`BR-FIN-02`), trừ `amount` ở `destinationWallet`, cộng trả `amount` về `sourceWallet` và gọi `fee.revertFee()` nếu có phí (`BR-FIN-03`); báo lỗi nếu ví đích thiếu số dư hoàn trả (`ERR-WAL-001`). |
| `getTotalSourceDeduction` | `Không` | `Money` | `Không` | Tính tổng số tiền trích từ `sourceWallet` bằng `amount` cộng số tiền của `fee` nếu có (`BR-FIN-01`). |
| `getSourceWallet` | `Không` | `Wallet` | `Không` | Trả về thực thể ví nguồn để đối soát và hiển thị lịch sử chuyển tiền (`FR-TRF-02`). |
| `getDestinationWallet` | `Không` | `Wallet` | `Không` | Trả về thực thể ví đích để đối soát và hiển thị lịch sử chuyển tiền (`FR-TRF-02`). |

**Cơ chế đóng gói và quy tắc bất biến**

- Hàm khởi tạo khóa cố định `sourceWallet` và `destinationWallet` bằng từ khóa `final`, yêu cầu cả hai ví đang ở trạng thái `ACTIVE`. Nếu hai ví trùng mã định danh (`BR-FIN-03`), thực thể từ chối khởi tạo vì lỗi trùng ví chuyển nhận (`ERR-TRF-001`).
- Trước khi biến đổi số dư, `applyToWallet` kiểm tra `sourceWallet` đủ chi trả tổng `amount` cộng `fee` (`BR-FIN-02`). Sau bước kiểm tra này, phương thức trừ tiền ví nguồn và cộng tiền ví đích trong cùng một khối lệnh (`BR-FIN-03`).
- Lớp `TransferTransaction` không cung cấp phương thức chỉnh sửa thuộc tính sau khi tạo. Khi xóa giao dịch (`FR-TRF-02`), `revertFromWallet` yêu cầu `destinationWallet` có số dư tối thiểu bằng `amount` (`BR-FIN-03`) và từ chối nếu ví đích thiếu số dư hoàn trả (`ERR-WAL-001`).

### 2.2.4 Lớp Chi phí Giao dịch

Lớp `TransactionFee` đại diện cho khoản phụ phí phát sinh kèm theo giao dịch thu chi (`FR-TXN-01`) hoặc chuyển tiền nội bộ (`FR-TRF-01`). Khoản phí này mang bản chất chi tiêu và gắn kết hợp thành với giao dịch gốc để ngăn bản ghi phí mồ côi (`BR-FIN-02`).

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TXN-03`, `UC-TRF-01`, `UC-TRF-03`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-04`, `FR-TRF-01`, `FR-TRF-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi: `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-WAL-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `feeId` | `UUID` | `private final` | Mã định danh duy nhất của bản ghi chi phí, sinh tự động khi tạo mới và giữ cố định (`BR-FIN-02`). |
| `parentTransactionId` | `UUID` | `private final` | Mã định danh tham chiếu cố định tới giao dịch gốc (`Transaction`), dùng để ràng buộc vòng đời và ngăn chi phí mồ côi (`BR-FIN-02`). |
| `amount` | `Money` | `private` | Số tiền chi phí có 2 chữ số thập phân (`BR-FIN-01`), lớn hơn `0.00 VNĐ` khi khởi tạo bản ghi phí (`BR-FIN-02`); từ chối nếu truyền chi phí âm (`ERR-FEE-001`). |
| `wallet` | `Wallet` | `private` | Tham chiếu thực thể `Wallet` đang `ACTIVE` trích trả chi phí (`BR-FIN-02`); báo lỗi nếu ví không đủ số dư thanh toán (`ERR-WAL-001`). |
| `category` | `Category` | `private` | Tham chiếu thực thể `Category` cấp 3 đang `ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-04`); báo lỗi nếu bỏ trống danh mục phí (`ERR-FEE-002`) hoặc chọn danh mục đã xóa hay sai loại (`ERR-FEE-003`). |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `TransactionFee` | `UUID feeId, UUID parentTransactionId, Money amount, Wallet wallet, Category category` | `TransactionFee` | `ValidationException`, `BusinessRuleException` | Kiểm tra `parentTransactionId` khác `null`, `amount` lớn hơn `0.00 VNĐ` (`BR-FIN-02`) và `category` cấp 3 thuộc nhánh `EXPENSE` đang `ACTIVE` (`BR-FIN-04`); từ chối nếu chi phí âm (`ERR-FEE-001`), thiếu danh mục phí (`ERR-FEE-002`) hoặc danh mục phí không hợp lệ (`ERR-FEE-003`). |
| `applyFee` | `Không` | `void` | `BusinessRuleException` | Gọi `wallet.debit(amount)` để trích khoản phí khỏi số dư ví thanh toán (`BR-FIN-02`); báo lỗi nếu ví thiếu số dư chi trả (`ERR-WAL-001`). |
| `revertFee` | `Không` | `void` | `BusinessRuleException` | Gọi `wallet.credit(amount)` để hoàn lại khoản phí vào ví thanh toán khi chỉnh sửa (`FR-TXN-04`) hoặc xóa giao dịch gốc (`FR-TRF-02`). |
| `updateFee` | `Money newAmount, Category newCategory, Wallet newWallet` | `void` | `ValidationException`, `BusinessRuleException` | Kiểm tra `newAmount` lớn hơn `0.00 VNĐ` (`BR-FIN-01`) và `newCategory` đang `ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-04`) trước khi cập nhật thông tin phí (`FR-TXN-04`); từ chối nếu chi phí âm (`ERR-FEE-001`), thiếu danh mục phí (`ERR-FEE-002`) hoặc danh mục phí sai loại (`ERR-FEE-003`). |
| `getAmount` | `Không` | `Money` | `Không` | Trả về số tiền phí để tính tổng dòng tiền ra và tổng chi tiêu (`BR-FIN-01`). |

**Cơ chế đóng gói và quy tắc bất biến**

- Lớp khóa cố định trường `parentTransactionId` bằng từ khóa `final`, và `TransactionFactory` chỉ khởi tạo `TransactionFee` khi mức phí lớn hơn `0.00 VNĐ`. Nhờ vậy, vòng đời bản ghi phí luôn đi liền với giao dịch gốc và tự động bị xóa theo giao dịch gốc (`BR-FIN-02`).
- Hàm khởi tạo và `updateFee` từ chối ngay khi mức phí âm (`ERR-FEE-001`). Khi có phí dương, thực thể bắt buộc gắn `Category` cấp 3 thuộc nhánh `EXPENSE` (`BR-FIN-04`), báo lỗi nếu thiếu danh mục (`ERR-FEE-002`) hoặc chọn sai danh mục (`ERR-FEE-003`).
- Giao dịch gốc gọi nội bộ `applyFee` và `revertFee` trong chu trình `applyToWallet` và `revertFromWallet`. Cơ chế này đồng bộ việc trích thu và hoàn phí với số dư ví (`BR-FIN-02`), từ chối thực hiện nếu ví thiếu số dư (`ERR-WAL-001`).

### 2.2.5 Lớp Danh mục Chi tiết

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp Category trong việc phân loại dòng tiền cấp 3 và duy trì tính toàn vẹn cây danh mục)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-04`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`, `ERR-CAT-004`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc kiểm tra và logic xử lý trạng thái) |

**Cơ chế đóng gói và quy tắc bất biến**

- (Nêu ràng buộc bắt buộc phải liên kết với một nhóm nhu cầu cha hợp lệ, cùng loại thu chi)
- (Nêu quy tắc đặt tên không trùng lặp trong phạm vi cùng nhóm nhu cầu cha)
- (Nêu cơ chế xóa mềm để bảo toàn lịch sử giao dịch và điều kiện khôi phục trạng thái)

### 2.2.6 Lớp Nhóm Nhu cầu

(Mô tả khoảng 1 đến 2 câu về vai trò của lớp NeedGroup trong việc quản lý phân cấp nhóm nhu cầu cấp 2 và điều phối danh mục con)

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-04`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-002`, `ERR-CAT-005`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| (Tên thuộc tính) | (Kiểu dữ liệu) | (Phạm vi truy cập) | (Điều kiện hợp lệ, tính bất biến hoặc giá trị mặc định) |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| (Tên phương thức) | (Kiểu và tên tham số) | (Kiểu trả về) | (Ngoại lệ ném ra nếu có) | (Quy tắc kiểm tra và logic xử lý trạng thái) |

**Cơ chế đóng gói và quy tắc bất biến**

- (Nêu tính bất biến của loại thu chi gắn liền với nhóm nhu cầu sau khi khởi tạo)
- (Nêu quy tắc bảo vệ nhóm nhu cầu duy nhất còn lại của một loại thu chi không bị xóa)
- (Nêu cơ chế quản lý danh sách danh mục con và hiệu ứng xóa lan truyền)

### 2.2.7 Lớp Bản ghi Điều chỉnh Số dư

Lớp `BalanceAdjustmentRecord` lưu lại sự kiện kiểm kê nhằm đưa số dư khả dụng của ví về khớp với số tiền thực tế quan sát được (`FR-WAL-05`). Bản ghi giữ trạng thái bất biến sau khi tạo để bảo vệ vết kiểm toán và không tính khoản chênh lệch vào thu chi (`BR-FIN-02`).

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `recordId` | `UUID` | `private final` | Mã định danh duy nhất của bản ghi kiểm kê, sinh tự động khi tạo mới (`FR-WAL-05`) và giữ cố định (`BR-FIN-02`). |
| `walletId` | `UUID` | `private final` | Mã định danh tham chiếu cố định tới thực thể `Wallet` đang `ACTIVE` được kiểm kê (`FR-WAL-05`); từ chối kiểm kê nếu ví ở trạng thái lưu trữ (`ERR-WAL-001`). |
| `previousBalance` | `Money` | `private final` | Số dư khả dụng của ví ngay trước thời điểm kiểm kê, lưu 2 chữ số thập phân (`BR-FIN-01`) và giữ cố định (`BR-FIN-02`). |
| `actualBalance` | `Money` | `private final` | Số dư thực tế quan sát được, lưu 2 chữ số thập phân (`BR-FIN-01`) và bắt buộc từ `0.00 VNĐ` trở lên (`BR-FIN-02`); báo lỗi nếu sai định dạng số học (`ERR-FIN-001`) hoặc số dư thực tế âm (`ERR-WAL-001`). |
| `difference` | `Money` | `private final` | Giá trị chênh lệch đại số tính bằng `actualBalance` trừ `previousBalance` (`BR-FIN-01`), bắt buộc khác `0.00 VNĐ` (`FR-WAL-05`). |
| `adjustedAt` | `TransactionDate` | `private final` | Mốc ngày ghi nhận kiểm kê bất biến, không vượt quá ngày hiện tại (`FR-WAL-05`). |
| `reason` | `String` | `private final` | Chuỗi diễn giải lý do điều chỉnh số dư, tùy chọn, dài tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu (`FR-WAL-05`). |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `BalanceAdjustmentRecord` | `UUID recordId, UUID walletId, Money previousBalance, Money actualBalance, TransactionDate adjustedAt, String reason` | `BalanceAdjustmentRecord` | `ValidationException`, `BusinessRuleException` | Kiểm tra `actualBalance` không âm (`BR-FIN-02`), cắt khoảng trắng và kiểm tra `reason` tối đa 255 ký tự (`FR-WAL-05`), tính `difference = actualBalance.subtract(previousBalance)` (`BR-FIN-01`) khác `0.00 VNĐ`; từ chối nếu sai định dạng số học (`ERR-FIN-001`), số dư thực tế âm hoặc chênh lệch bằng 0 (`ERR-WAL-001`). |
| `isSurplus` | `Không` | `boolean` | `Không` | Trả về `true` khi `difference` lớn hơn `0.00 VNĐ`, xác định khoản điều chỉnh làm tăng dòng tiền vào của ví (`BR-FIN-02`). |
| `getAbsoluteDifference` | `Không` | `Money` | `Không` | Trả về trị tuyệt đối của `difference` để đưa vào công thức tính tổng dòng tiền vào hoặc ra trên ví (`BR-FIN-02`). |
| `getDifference` | `Không` | `Money` | `Không` | Trả về giá trị chênh lệch có dấu đại số để đối soát biến động tổng tài sản (`BR-FIN-01`). |
| `getActualBalance` | `Không` | `Money` | `Không` | Trả về số dư thực tế sau kiểm kê để đồng bộ với số dư khả dụng của ví (`FR-WAL-05`). |

**Cơ chế đóng gói và quy tắc bất biến**

- Lớp `BalanceAdjustmentRecord` khai báo `private final` cho cả 7 thuộc tính và không mở bất kỳ phương thức sửa đổi hay xóa bỏ nào. Thiết kế chỉ thêm này giữ nguyên bản ghi kiểm kê sau khi tạo (`FR-WAL-05`) và bảo toàn vết kiểm toán (`BR-FIN-02`).
- Hàm khởi tạo tự động tính `difference = actualBalance.subtract(previousBalance)` (`BR-FIN-01`), yêu cầu `actualBalance` không âm (`BR-FIN-02`) và `difference` khác `0.00 VNĐ` (`FR-WAL-05`). Thực thể từ chối khởi tạo nếu dữ liệu sai định dạng số học (`ERR-FIN-001`), số dư thực tế âm hoặc chênh lệch bằng không (`ERR-WAL-001`).
- Khoản chênh lệch `difference` chỉ cộng hoặc trừ trực tiếp vào số dư khả dụng của `Wallet` liên kết. Do bản ghi này tách biệt với `IncomeExpenseTransaction`, các báo cáo thu nhập và chi tiêu định kỳ không bị biến dạng bởi nghiệp vụ kiểm kê (`BR-FIN-02`).

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
