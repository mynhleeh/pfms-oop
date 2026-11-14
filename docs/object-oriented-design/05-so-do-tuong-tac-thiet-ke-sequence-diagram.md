# 05. Sơ đồ Tuần tự Thiết kế

Tài liệu này đặc tả luồng tương tác động giữa các đối tượng trong hệ thống nhằm hiện thực hóa các ca sử dụng trên môi trường giao diện đồ họa người dùng. Các kịch bản tương tác phân rã luồng xử lý từ sự kiện đầu vào của người dùng qua các tầng kiến trúc: tiếp nhận lệnh tại tầng trình diễn, điều phối nghiệp vụ tại tầng dịch vụ, thực thi quy tắc nghiệp vụ tại tầng miền và ghi nhận dữ liệu bền vững tại tầng hạ tầng.

Các sơ đồ tuần tự xác định trình tự gọi phương thức đồng bộ, quy chuẩn truyền nhận đối tượng truyền dữ liệu và cơ chế biến đổi trạng thái nội tại của thực thể. Mọi thông điệp trao đổi trên sơ đồ đều khớp với cấu trúc lớp tĩnh đã thiết kế, đồng thời mô hình hóa đầy đủ các nhánh ngoại lệ nghiệp vụ phát sinh trong quá trình vận hành để duy trì tính nhất quán của dữ liệu tài chính.

## 5.1 Nguyên tắc Thiết kế Sơ đồ Tuần tự Chi tiết

Mục này xác lập các nguyên tắc thiết kế và quy chuẩn mô hình hóa tương tác động cho toàn bộ hệ thống. Các quy định tại đây thiết lập khung kỹ thuật thống nhất, bảo đảm tính nhất quán trong cách biểu diễn luồng dữ liệu và lời gọi phương thức xuyên suốt các ca sử dụng.

Trong giai đoạn phân tích, sơ đồ tuần tự hệ thống mô tả phần mềm dưới dạng hộp đen: toàn bộ hệ thống được xem như một đối tượng duy nhất, chỉ thể hiện sự kiện người dùng gửi vào và phản hồi nhận lại mà không bộc lộ cấu trúc bên trong. Khi chuyển sang giai đoạn thiết kế, sơ đồ tuần tự chi tiết mở rộng hộp đen thành chuỗi tương tác cụ thể giữa các đối tượng phân bổ trên bốn tầng kiến trúc: tầng trình diễn tiếp nhận dữ liệu và điều hướng hiển thị, tầng dịch vụ điều phối luồng nghiệp vụ và quản lý giao dịch, tầng miền thực thi các quy tắc nghiệp vụ và biến đổi trạng thái, tầng hạ tầng đảm nhận việc lưu trữ dữ liệu bền vững.

Bảng so sánh đặc điểm giữa sơ đồ tuần tự hệ thống và sơ đồ tuần tự thiết kế:

| Tiêu chí | Sơ đồ tuần tự hệ thống | Sơ đồ tuần tự thiết kế |
| :--- | :--- | :--- |
| **Góc nhìn tương tác** | Hộp đen: Xem xét hệ thống là một thành phần nguyên khối duy nhất. | Hộp trắng: Phân rã luồng thực thi chi tiết qua các đối tượng thuộc bốn tầng kiến trúc. |
| **Mục đích sử dụng** | Xác định ranh giới chức năng và nắm bắt sự kiện đầu vào của người dùng. | Định hình cấu trúc gọi hàm, phân bổ trách nhiệm và hướng dẫn cài đặt mã nguồn. |
| **Thành phần tham gia** | Người dùng và đối tượng hệ thống trừu tượng. | Người dùng, lớp điều phối giao diện, dịch vụ ứng dụng, thực thể miền và kho lưu trữ. |
| **Bản chất thông điệp** | Thao tác hệ thống trừu tượng mô tả nghiệp vụ cần thực hiện. | Lời gọi phương thức đồng bộ khớp chính xác chữ ký hàm của các lớp mã nguồn. |
| **Xử lý ngoại lệ** | Mô tả thông báo từ chối tổng quát ở mức giao diện người dùng. | Ném ngoại lệ nghiệp vụ cụ thể kèm mã lỗi định danh và điều hướng luồng xử lý lỗi. |
| **Bảo toàn trạng thái** | Ghi nhận điều kiện tiên quyết và kết quả thay đổi sau thao tác. | Kiểm soát biến đổi trạng thái nội tại của thực thể và duy trì tính toàn vẹn dữ liệu. |

Các cột đối tượng tham gia tương tác trên sơ đồ được sắp xếp theo thứ tự cố định từ trái qua phải, phản ánh chiều phụ thuộc và luồng điều khiển của kiến trúc:

1. Tác nhân ngoài: Người dùng thực hiện thao tác chuột và phím trên giao diện đồ họa.
2. Tầng Trình diễn: Lớp giao diện đồ họa đọc dữ liệu biểu mẫu, kiểm tra định dạng và đóng gói đối tượng truyền dữ liệu.
3. Tầng Dịch vụ: Lớp dịch vụ ứng dụng tiếp nhận yêu cầu, nạp dữ liệu và điều phối quy trình xử lý ca sử dụng.
4. Tầng Nghiệp vụ: Các thực thể và đối tượng giá trị thực hiện tính toán, kiểm tra ràng buộc và cập nhật trạng thái.
5. Tầng Hạ tầng: Kho lưu trữ dữ liệu thực hiện các thao tác truy xuất hoặc lưu trữ bền vững.

Sơ đồ dưới đây minh họa luồng tương tác tổng quát qua bốn tầng kiến trúc:

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant Service as Tầng Dịch vụ
    participant Domain as Tầng Nghiệp vụ
    participant Repo as Tầng Hạ tầng

    User->>+GUI: 1. Nhập lệnh thao tác nghiệp vụ
    GUI->>+Service: 2. executeUseCase(requestDto)
    Service->>+Repo: 3. findById(id)
    Repo-->>-Service: 4. Optional<Entity>
    Service->>+Domain: 5. businessOperation(parameters)
    Domain-->>-Service: 6. result
    Service->>+Repo: 7. save(entity)
    Repo-->>-Service: 8. entity
    Service-->>-GUI: 9. responseDto
    GUI-->>-User: 10. Kết xuất bảng kết quả hiển thị
```

Mọi thông điệp trao đổi trên sơ đồ tuần tự bắt buộc phải khớp chính xác với chữ ký phương thức đã công bố trong các giao diện dịch vụ, thực thể miền và kho lưu trữ dữ liệu.

Bên cạnh luồng thực thi chuẩn, mọi sơ đồ tuần tự trong tài liệu bắt buộc phải mô hình hóa tường minh các nhánh ngoại lệ nghiệp vụ thông qua khung rẽ nhánh điều kiện `alt`. Thiết kế phải bao phủ đầy đủ các điểm kiểm tra ràng buộc tài chính kèm mã lỗi định danh tương ứng, loại bỏ các sơ đồ chỉ biểu diễn kịch bản thành công đơn lẻ. Đối với các giao dịch tác động đồng thời lên nhiều nguồn tiền như chuyển tiền nội bộ, luồng tương tác được bao bọc trong khung thực thi cô lập `critical` nhằm bảo đảm nguyên tắc toàn vẹn: toàn bộ thao tác cập nhật số dư các ví phải cùng thành công hoặc hoàn tác về trạng thái ban đầu nếu phát sinh sự cố.

## 5.2 Sơ đồ Tuần tự Nhóm Quản lý Ví

Mục này mô hình hóa các luồng tương tác thực thi nghiệp vụ quản lý nguồn tiền trong hệ thống. Chuỗi tương tác mô tả quy trình điều phối vòng đời của ví thanh toán: khởi tạo nguồn tiền, cập nhật thông tin nhận diện, chuyển đổi trạng thái lưu trữ và ghi nhận chênh lệch kiểm kê thực tế.

Các kịch bản làm rõ sự phối hợp giữa lớp dịch vụ điều phối với thực thể ví và kho lưu trữ dữ liệu, bảo đảm thực thi quy tắc kiểm tra tính duy nhất của tên ví, kiểm soát điều kiện số dư không âm và lưu vết kiểm toán đối soát khi phát sinh chênh lệch kiểm kê.

### 5.2.1 Sơ đồ Tạo Mới Ví

Luồng điều phối khởi tạo ví mới tiếp nhận yêu cầu từ giao diện, xác thực chuẩn định dạng tên ví và kiểm tra tính duy nhất trên toàn hệ thống trước khi khởi tạo thực thể ví với số dư ban đầu bằng không. Dữ liệu thực thể được ghi nhận bền vững xuống kho lưu trữ và chuyển đổi sang đối tượng truyền dữ liệu phản hồi về tầng trình diễn.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`
- Thao tác hệ thống: `createWallet(walletName, note)`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-WAL-02`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-02`
- Mã lỗi xử lý: `ERR-NAM-001`, `ERR-NAM-002`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant WalletService as WalletServiceImpl
    participant WalletRepo as WalletRepository
    participant Wallet as Wallet

    User->>+GUI: 1. Nhập tên ví, ghi chú và bấm "Lưu ví"
    GUI->>+WalletService: 2. createWallet(requestDto)
    alt Tên ví để trống hoặc vượt quá 50 ký tự
        WalletService-->>GUI: 3. ném ValidationException
        GUI-->>User: 4. Hiển thị thông báo tên ví không hợp lệ
    else Tên ví hợp lệ về độ dài
        WalletService->>+WalletRepo: 5. existsByName(trimmedName)
        WalletRepo-->>-WalletService: 6. boolean isDuplicate
        alt Tên ví đã tồn tại trong hệ thống
            WalletService-->>GUI: 7. ném DuplicateEntityException
            GUI-->>User: 8. Hiển thị thông báo tên ví đã tồn tại
        else Tên ví hợp lệ và duy nhất
            WalletService->>+Wallet: 9. new Wallet(walletId, trimmedName, trimmedNote, false)
            Wallet-->>-WalletService: 10. newWallet
            WalletService->>+WalletRepo: 11. save(newWallet)
            WalletRepo-->>-WalletService: 12. savedWallet
            WalletService-->>-GUI: 13. WalletResponseDto
            GUI-->>-User: 14. Hiển thị thông báo tạo ví thành công và làm mới danh sách
        end
    end
```

Toàn vẹn tài chính được bảo đảm tuyệt đối khi thực thể ví mới luôn được khởi tạo với số dư ban đầu bằng không thông qua đối tượng giá trị bất biến `Money.ZERO`, loại trừ hoàn toàn rủi ro gán số dư tùy tiện khi chưa qua thủ tục kiểm kê thực tế.

### 5.2.2 Sơ đồ Xem Tổng hợp Số dư và Cơ cấu Ví

Luồng truy vấn danh sách ví trích xuất toàn bộ nguồn tiền khả dụng, tính toán tổng tài sản tức thời và xác định tỷ trọng phân bổ tài sản toàn hệ thống ở chế độ chỉ đọc. Cơ chế tính toán áp dụng nguyên tắc làm tròn nửa chẵn và kiểm soát giá trị mẫu số triệt tiêu nhằm chống lỗi số học chia cho không.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-02`
- Thao tác hệ thống: `getWalletsSummary()`
- Yêu cầu chức năng: `FR-WAL-04`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi xử lý: `ERR-FIN-002`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant WalletService as WalletServiceImpl
    participant WalletRepo as WalletRepository
    participant Wallet as Wallet

    User->>+GUI: 1. Mở màn hình danh sách ví / Bảng điều khiển
    GUI->>+WalletService: 2. getWalletsSummary(statusFilter)
    WalletService->>+WalletRepo: 3. findAll()
    WalletRepo-->>-WalletService: 4. List<Wallet>
    alt Tổng số dư bằng 0
        WalletService-->>GUI: 5. WalletsSummaryDto (Tỷ trọng 0.00%)
        GUI-->>User: 6. Hiển thị tổng số dư 0.00 VNĐ và cơ cấu phân bổ 0%
    else Tổng số dư lớn hơn 0
        loop Duyệt qua từng ví trong danh sách
            WalletService->>+Wallet: 7. getCurrentBalance()
            Wallet-->>-WalletService: 8. currentBalance
        end
        Note over WalletService: Tính tỷ trọng % theo làm tròn nửa chẵn (HALF_EVEN)
        WalletService-->>-GUI: 9. WalletsSummaryDto (Kèm tỷ trọng phân bổ)
        GUI-->>-User: 10. Cập nhật bảng dữ liệu ví, tổng tài sản và biểu đồ cơ cấu
    end
```

Thao tác vận hành hoàn toàn ở chế độ chỉ đọc, truy xuất thông tin qua các phương thức đọc bất biến và không làm thay đổi trạng thái nội tại của bất kỳ thực thể ví nào trong hệ thống.

### 5.2.3 Sơ đồ Cập nhật Thông tin Ví

Luồng cập nhật thông tin ví tiếp nhận tên gọi và ghi chú mới từ giao diện người dùng, kiểm tra sự tồn tại của nguồn tiền, xác thực quy chuẩn định dạng và kiểm tra tính duy nhất trước khi ủy quyền thực thể cập nhật thuộc tính. Mã định danh và số dư khả dụng hiện hành được bảo toàn nguyên vẹn xuyên suốt quá trình xử lý.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-03`
- Thao tác hệ thống: `updateWallet(walletId, walletName, note)`
- Yêu cầu chức năng: `FR-WAL-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-02`
- Mã lỗi xử lý: `ERR-WAL-001`, `ERR-NAM-001`, `ERR-NAM-002`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant WalletService as WalletServiceImpl
    participant WalletRepo as WalletRepository
    participant Wallet as Wallet

    User->>+GUI: 1. Nhập tên mới, ghi chú mới và bấm "Cập nhật"
    GUI->>+WalletService: 2. updateWallet(requestDto)
    WalletService->>+WalletRepo: 3. findById(walletId)
    WalletRepo-->>-WalletService: 4. Optional<Wallet> walletOpt
    alt Không tìm thấy ví
        WalletService-->>GUI: 5. ném EntityNotFoundException
        GUI-->>User: 6. Hiển thị thông báo không tìm thấy ví
    else Tìm thấy thực thể ví
        alt Tên ví mới để trống hoặc vượt quá 50 ký tự
            WalletService-->>GUI: 7. ném ValidationException
            GUI-->>User: 8. Hiển thị thông báo tên ví không hợp lệ
        else Tên mới khác tên hiện tại và đã tồn tại trong hệ thống
            WalletService->>+WalletRepo: 9. existsByName(trimmedNewName)
            WalletRepo-->>-WalletService: 10. boolean isDuplicate (true)
            WalletService-->>GUI: 11. ném DuplicateEntityException
            GUI-->>User: 12. Hiển thị thông báo tên ví đã tồn tại
        else Tên mới hợp lệ và không bị trùng
            opt Tên mới khác tên hiện tại
                WalletService->>+WalletRepo: 13. existsByName(trimmedNewName)
                WalletRepo-->>-WalletService: 14. boolean isDuplicate (false)
            end
            WalletService->>+Wallet: 15. updateInfo(trimmedNewName, trimmedNewNote)
            Wallet-->>-WalletService: 16. void
            WalletService->>+WalletRepo: 17. save(wallet)
            WalletRepo-->>-WalletService: 18. updatedWallet
            WalletService-->>-GUI: 19. WalletResponseDto
            GUI-->>-User: 20. Đóng hộp thoại và làm mới danh sách ví
        end
    end
```

Việc cập nhật thông tin định danh giữ nguyên mã định danh `walletId` và số dư khả dụng `currentBalance`, bảo đảm không làm đứt gãy tính toàn vẹn tham chiếu của các giao dịch lịch sử gắn liền với ví.

### 5.2.4 Sơ đồ Điều chỉnh Số dư Ví Thực tế

Luồng điều chỉnh số dư ví đồng bộ số dư trên hệ thống khớp với kết quả kiểm kê thực tế của người dùng. Chuỗi tương tác xác thực tính hợp lệ của số dư thực tế, kiểm tra trạng thái hoạt động của ví, cập nhật số dư mới và khởi tạo bản ghi kiểm kê bất biến để lưu vết kiểm toán tài chính cố định.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-04`
- Thao tác hệ thống: `adjustBalance(walletId, actualBalance, reason)`
- Yêu cầu chức năng: `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi xử lý: `ERR-WAL-001`, `ERR-FIN-001`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant WalletService as WalletServiceImpl
    participant WalletRepo as WalletRepository
    participant AdjustRepo as BalanceAdjustmentRepository
    participant Wallet as Wallet

    User->>+GUI: 1. Nhập số dư thực tế, lý do kiểm kê và bấm "Xác nhận kiểm kê"
    GUI->>+WalletService: 2. adjustBalance(requestDto)
    alt Số dư thực tế âm (< 0)
        WalletService-->>GUI: 3. ném ValidationException
        GUI-->>User: 4. Hiển thị thông báo số dư thực tế không được âm
    else Số dư thực tế hợp lệ
        WalletService->>+WalletRepo: 5. findById(walletId)
        WalletRepo-->>-WalletService: 6. Optional<Wallet> walletOpt
        alt Ví không tồn tại hoặc không ở trạng thái ACTIVE
            WalletService-->>GUI: 7. ném EntityNotFoundException
            GUI-->>User: 8. Hiển thị thông báo ví không khả dụng
        else Ví tồn tại và đang hoạt động (ACTIVE)
            alt Số dư thực tế trùng khớp số dư hiện tại (độ lệch bằng 0)
                WalletService-->>GUI: 9. ném BusinessException
                GUI-->>User: 10. Hiển thị thông báo không có chênh lệch số dư kiểm kê
            else Có phát sinh chênh lệch
                WalletService->>+Wallet: 11. adjustBalance(actualBalance, date, reason)
                Wallet-->>-WalletService: 12. BalanceAdjustmentRecord record
                WalletService->>+WalletRepo: 13. save(wallet)
                WalletRepo-->>-WalletService: 14. savedWallet
                WalletService->>+AdjustRepo: 15. save(record)
                AdjustRepo-->>-WalletService: 16. savedRecord
                WalletService-->>-GUI: 17. BalanceAdjustmentResponseDto
                GUI-->>-User: 18. Đóng hộp thoại, cập nhật số dư ví và hiển thị thông báo thành công
            end
        end
    end
```

Mọi chênh lệch kiểm kê bắt buộc khởi tạo bản ghi kiểm kê bất biến `BalanceAdjustmentRecord` để lưu giữ vết kiểm toán độc lập, bảo đảm số dư khả dụng mới luôn duy trì giá trị không âm và không làm xáo trộn các số liệu thu chi nguyên thủy.

## 5.3 Sơ đồ Tuần tự Nhóm Ghi nhận Thu Chi

Mục này mô hình hóa các luồng tương tác thực thi nghiệp vụ ghi nhận biến động dòng tiền thu chi hàng ngày trong hệ thống. Chuỗi tương tác mô tả quy trình tiếp nhận sự kiện tài chính, phân loại theo danh mục chi tiết, kiểm soát chi phí giao dịch liên kết và điều phối cập nhật số dư ví tức thời.

Các kịch bản làm rõ sự phối hợp giữa lớp dịch vụ điều phối với các thực thể giao dịch, thực thể ví và các kho lưu trữ tương ứng, bảo đảm thực thi quy tắc kiểm tra số dư khả dụng không âm, kiểm soát tính hợp lệ của cây danh mục phân loại và thực hiện cơ chế hoàn nguyên hai pha khi chỉnh sửa hoặc xóa giao dịch.

### 5.3.1 Sơ đồ Ghi nhận Giao dịch Thu Chi

Luồng điều phối ghi nhận giao dịch thu chi tiếp nhận thông tin thu nhập hoặc chi tiêu kèm chi phí phát sinh (nếu có), xác thực tính hợp lệ của nguồn tiền và danh mục chi tiết, kiểm soát số dư ví khả dụng không âm trước khi khởi tạo thực thể và lưu trữ đồng thời vào kho dữ liệu.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`
- Thao tác hệ thống: `recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-CAT-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant TransactionService as TransactionServiceImpl
    participant WalletRepo as WalletRepository
    participant CatRepo as CategoryRepository
    participant TxnRepo as TransactionRepository
    participant Transaction as IncomeExpenseTransaction
    participant Wallet as Wallet

    User->>+GUI: 1. Nhập thông tin giao dịch (ví, danh mục, số tiền, ngày, phí) và bấm "Lưu"
    GUI->>+TransactionService: 2. recordIncomeExpense(requestDto)
    alt Số tiền <= 0 hoặc phí < 0
        TransactionService-->>GUI: 3. ném ValidationException
        GUI-->>User: 4. Hiển thị thông báo số tiền hoặc chi phí không hợp lệ
    else Có phí phát sinh nhưng chưa chọn danh mục phí
        TransactionService-->>GUI: 5. ném BusinessException
        GUI-->>User: 6. Hiển thị thông báo chọn danh mục cho phí giao dịch
    else Dữ liệu hợp lệ
        TransactionService->>+WalletRepo: 7. findById(walletId)
        WalletRepo-->>-TransactionService: 8. Optional<Wallet> walletOpt
        alt Ví không tồn tại hoặc không hoạt động
            TransactionService-->>GUI: 9. ném EntityNotFoundException
            GUI-->>User: 10. Hiển thị thông báo ví không khả dụng
        else Ví hợp lệ và đang hoạt động
            TransactionService->>+CatRepo: 11. findById(categoryId)
            CatRepo-->>-TransactionService: 12. Optional<Category> catOpt
            alt Danh mục không tồn tại hoặc đã bị xóa
                TransactionService-->>GUI: 13. ném EntityNotFoundException
                GUI-->>User: 14. Hiển thị thông báo danh mục không hợp lệ
            else Danh mục hợp lệ
                opt Có phí phát sinh (feeAmount > 0)
                    TransactionService->>+CatRepo: 15. findById(feeCategoryId)
                    CatRepo-->>-TransactionService: 16. Optional<Category> feeCatOpt
                end
                alt Số dư ví không đủ chi trả (tiền chi + phí)
                    TransactionService-->>GUI: 17. ném InsufficientBalanceException
                    GUI-->>User: 18. Hiển thị cảnh báo số dư ví không đủ
                else Số dư ví khả dụng đủ chi trả
                    TransactionService->>+Transaction: 19. create(txnId, walletId, categoryId, amount, date, fee)
                    Transaction-->>-TransactionService: 20. newTransaction
                    TransactionService->>+Wallet: 21. applyTransaction(newTransaction)
                    Wallet-->>-TransactionService: 22. void
                    TransactionService->>+TxnRepo: 23. save(newTransaction)
                    TxnRepo-->>-TransactionService: 24. savedTransaction
                    TransactionService->>+WalletRepo: 25. save(wallet)
                    WalletRepo-->>-TransactionService: 26. savedWallet
                    TransactionService-->>-GUI: 27. TransactionResponseDto
                    GUI-->>-User: 28. Đóng hộp thoại, làm mới sổ giao dịch và cập nhật số dư ví
                end
            end
        end
    end
```

Mọi giao dịch chi tiêu và chi phí liên kết bắt buộc kiểm soát số dư khả dụng không âm trước khi ghi nhận, bảo đảm dữ liệu luôn nhất quán giữa sổ giao dịch và số dư ví.

### 5.3.2 Sơ đồ Tra cứu và Lọc Lịch sử Giao dịch

Luồng tra cứu và trích xuất lịch sử giao dịch vận hành hoàn toàn ở chế độ chỉ đọc. Chuỗi tương tác tiếp nhận các tiêu chí lọc thời gian, nguồn tiền, danh mục, kiểm tra tính hợp lệ của khoảng ngày, nạp dữ liệu và tính toán tổng dòng tiền trước khi đóng gói phản hồi về tầng trình diễn.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-02`
- Thao tác hệ thống: `filterTransactions(filterCriteria)`
- Yêu cầu chức năng: `FR-TXN-03`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-FIN-001`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant TransactionService as TransactionServiceImpl
    participant TxnRepo as TransactionRepository

    User->>+GUI: 1. Chọn tiêu chí lọc (khoảng ngày, ví, danh mục) và bấm "Tìm kiếm"
    GUI->>+TransactionService: 2. filterTransactions(filterCriteriaDto)
    alt Ngày bắt đầu lớn hơn ngày kết thúc (startDate > endDate)
        TransactionService-->>GUI: 3. ném ValidationException
        GUI-->>User: 4. Hiển thị thông báo khoảng thời gian lọc không hợp lệ
    else Khoảng thời gian lọc hợp lệ
        TransactionService->>+TxnRepo: 5. findByDateRange(...) hoặc findByWalletId / findByCategoryId
        TxnRepo-->>-TransactionService: 6. List<Transaction> rawTransactions
        Note over TransactionService: Lọc theo tiêu chí, sắp xếp theo ngày giảm dần, tính tổng thu, tổng chi và dòng tiền thuần
        TransactionService-->>-GUI: 7. TransactionListResponseDto
        GUI-->>-User: 8. Cập nhật bảng danh sách giao dịch và bảng chỉ số tổng hợp
    end
```

Thao tác tra cứu vận hành thuần túy ở chế độ chỉ đọc, không làm biến đổi dữ liệu hay trạng thái của các thực thể giao dịch và số dư nguồn tiền trong hệ thống.

### 5.3.3 Sơ đồ Chỉnh sửa Giao dịch Thu Chi

Luồng chỉnh sửa giao dịch điều phối việc cập nhật thông tin khoản thu chi đã ghi nhận, áp dụng quy trình kiểm soát hoàn nguyên an toàn nhằm bảo đảm số dư ví sau điều chỉnh luôn thỏa mãn ràng buộc không âm.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-03`
- Thao tác hệ thống: `updateTransaction(transactionId, newAmount, newDate, newCategoryId, newNote, newFeeAmount, newFeeCategoryId)`
- Yêu cầu chức năng: `FR-TXN-04`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-CAT-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant TransactionService as TransactionServiceImpl
    participant TxnRepo as TransactionRepository
    participant WalletRepo as WalletRepository
    participant CatRepo as CategoryRepository
    participant Wallet as Wallet
    participant Transaction as IncomeExpenseTransaction

    User->>+GUI: 1. Nhập thông tin chỉnh sửa giao dịch và bấm "Cập nhật"
    GUI->>+TransactionService: 2. updateTransaction(requestDto)
    TransactionService->>+TxnRepo: 3. findById(transactionId)
    TxnRepo-->>-TransactionService: 4. Optional<Transaction> oldTxnOpt
    alt Không tìm thấy giao dịch
        TransactionService-->>GUI: 5. ném EntityNotFoundException
        GUI-->>User: 6. Hiển thị thông báo không tìm thấy giao dịch
    else Tìm thấy giao dịch
        TransactionService->>+WalletRepo: 7. findById(walletId)
        WalletRepo-->>-TransactionService: 8. Optional<Wallet> walletOpt
        TransactionService->>+CatRepo: 9. findById(newCategoryId)
        CatRepo-->>-TransactionService: 10. Optional<Category> newCatOpt
        alt Ví hoặc danh mục mới không tồn tại / không hợp lệ
            TransactionService-->>GUI: 11. ném EntityNotFoundException
            GUI-->>User: 12. Hiển thị thông báo lỗi danh mục hoặc ví không hợp lệ
        else Thông tin hợp lệ
            alt Số dư ví sau khi sửa bị âm (balanceAfterUpdate < 0)
                TransactionService-->>GUI: 13. ném InsufficientBalanceException
                GUI-->>User: 14. Hiển thị cảnh báo số dư ví không đủ để áp dụng thay đổi
            else Số dư ví sau khi sửa hợp lệ (>= 0)
                TransactionService->>+Transaction: 15. revertFromWallet(wallet)
                Transaction-->>-TransactionService: 16. void
                TransactionService->>+Transaction: 17. updateDetails(newAmount, newDate, newCategoryId, newFee)
                Transaction-->>-TransactionService: 18. void
                TransactionService->>+Transaction: 19. applyToWallet(wallet)
                Transaction-->>-TransactionService: 20. void
                TransactionService->>+TxnRepo: 21. save(txn)
                TxnRepo-->>-TransactionService: 22. updatedTxn
                TransactionService->>+WalletRepo: 23. save(wallet)
                WalletRepo-->>-TransactionService: 24. updatedWallet
                TransactionService-->>-GUI: 25. TransactionResponseDto
                GUI-->>-User: 26. Đóng hộp thoại, làm mới sổ giao dịch và cập nhật số dư ví
            end
        end
    end
```

Quy trình kiểm soát hai pha bảo đảm giao dịch chỉ được chấp nhận cập nhật khi trạng thái số dư ví sau điều chỉnh duy trì không âm, loại trừ nguy cơ xuất quỹ vượt quá khả năng tài chính.

### 5.3.4 Sơ đồ Xóa Giao dịch Thu Chi

Luồng xóa giao dịch loại bỏ bản ghi thu chi khỏi hệ thống, thu hồi khoản chi tiêu hoặc hoàn trả khoản thu nhập về ví nguồn, đồng thời xóa sạch bản ghi chi phí phụ thuộc liên kết.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-04`
- Thao tác hệ thống: `deleteTransaction(transactionId)`
- Yêu cầu chức năng: `FR-TXN-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi xử lý: `ERR-WAL-001`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant TransactionService as TransactionServiceImpl
    participant TxnRepo as TransactionRepository
    participant WalletRepo as WalletRepository
    participant Transaction as IncomeExpenseTransaction
    participant Wallet as Wallet

    User->>+GUI: 1. Chọn giao dịch và bấm "Xóa giao dịch"
    GUI->>+TransactionService: 2. deleteTransaction(transactionId)
    TransactionService->>+TxnRepo: 3. findById(transactionId)
    TxnRepo-->>-TransactionService: 4. Optional<Transaction> txnOpt
    alt Không tìm thấy giao dịch
        TransactionService-->>GUI: 5. ném EntityNotFoundException
        GUI-->>User: 6. Hiển thị thông báo không tìm thấy giao dịch
    else Tìm thấy giao dịch cần xóa
        TransactionService->>+WalletRepo: 7. findById(walletId)
        WalletRepo-->>-TransactionService: 8. Optional<Wallet> walletOpt
        alt Là giao dịch thu nhập và số dư ví không đủ để trừ hoàn nguyên
            TransactionService-->>GUI: 9. ném InsufficientBalanceException
            GUI-->>User: 10. Hiển thị cảnh báo số dư ví không đủ để hoàn tác khoản thu
        else Đủ điều kiện hoàn tác (hoặc là giao dịch chi tiêu)
            TransactionService->>+Transaction: 11. revertFromWallet(wallet)
            Transaction-->>-TransactionService: 12. void
            TransactionService->>+TxnRepo: 13. deleteById(transactionId)
            TxnRepo-->>-TransactionService: 14. boolean (deleted)
            TransactionService->>+WalletRepo: 15. save(wallet)
            WalletRepo-->>-TransactionService: 16. savedWallet
            TransactionService-->>-GUI: 17. phản hồi xóa thành công
            GUI-->>-User: 18. Đóng hộp thoại, làm mới sổ giao dịch và cập nhật số dư ví
        end
    end
```

Việc xóa giao dịch đồng thời thu hồi hoặc hoàn trả số dư ví và loại bỏ hoàn toàn các bản ghi chi phí phụ thuộc liên kết, triệt tiêu nguy cơ phát sinh dữ liệu chi phí mồ côi và bảo đảm số dư không âm.

## 5.4 Sơ đồ Tuần tự Nhóm Chuyển tiền Nội bộ

Mục này mô hình hóa các luồng tương tác thực thi nghiệp vụ điều chuyển tiền giữa các ví trong hệ thống. Chuỗi tương tác mô tả quy trình tiếp nhận sự kiện chuyển tiền, kiểm tra tính phân biệt giữa ví nguồn và ví đích, hạch toán chi phí phát sinh liên kết và điều phối cập nhật đồng thời số dư của cả hai ví.

Các kịch bản làm rõ sự phối hợp giữa lớp dịch vụ điều phối với các thực thể giao dịch chuyển tiền, thực thể ví và các kho lưu trữ tương ứng, bảo đảm thực thi quy tắc bảo toàn tổng tài sản trừ chi phí phát sinh, kiểm soát số dư ví nguồn và ví đích luôn không âm và thực hiện cơ chế hoàn nguyên hai pha khi xóa giao dịch chuyển tiền.

### 5.4.1 Sơ đồ Chuyển tiền giữa Hai Ví

Luồng điều phối chuyển tiền từ ví nguồn sang ví đích kiểm tra tính phân biệt của cặp ví, xác thực số tiền chuyển và chi phí hợp lệ, kiểm tra số dư ví nguồn đủ thanh toán tổng tiền chuyển cùng phí trước khi cập nhật đồng thời số dư hai ví và lưu vết giao dịch.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TRF-01`
- Thao tác hệ thống: `transferFunds(sourceWalletId, destinationWalletId, amount, transferDate, note, feeAmount, feeCategoryId)`
- Yêu cầu chức năng: `FR-TRF-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FIN-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant TransferService as TransferServiceImpl
    participant WalletRepo as WalletRepository
    participant CatRepo as CategoryRepository
    participant TxnRepo as TransactionRepository
    participant Transfer as TransferTransaction
    participant SourceWallet as SourceWallet
    participant DestWallet as DestWallet

    User->>+GUI: 1. Nhập thông tin chuyển tiền (ví nguồn, ví đích, số tiền, ngày, phí) và bấm "Chuyển tiền"
    GUI->>+TransferService: 2. transferFunds(requestDto)
    alt Ví nguồn trùng với ví đích
        TransferService-->>GUI: 3. ném BusinessException
        GUI-->>User: 4. Hiển thị thông báo ví chuyển và nhận phải khác nhau
    else Số tiền chuyển <= 0 hoặc phí < 0
        TransferService-->>GUI: 5. ném ValidationException
        GUI-->>User: 6. Hiển thị thông báo số tiền hoặc chi phí không hợp lệ
    else Có phí phát sinh nhưng chưa chọn danh mục phí
        TransferService-->>GUI: 7. ném BusinessException
        GUI-->>User: 8. Hiển thị thông báo chọn danh mục cho phí chuyển tiền
    else Dữ liệu đầu vào hợp lệ
        TransferService->>+WalletRepo: 9. findById(sourceWalletId)
        WalletRepo-->>-TransferService: 10. Optional<Wallet> sourceOpt
        TransferService->>+WalletRepo: 11. findById(destWalletId)
        WalletRepo-->>-TransferService: 12. Optional<Wallet> destOpt
        alt Một trong hai ví không tồn tại hoặc không hoạt động
            TransferService-->>GUI: 13. ném EntityNotFoundException
            GUI-->>User: 14. Hiển thị thông báo ví chuyển hoặc nhận không khả dụng
        else Cả hai ví hợp lệ và đang hoạt động
            alt Ví nguồn không đủ số dư chi trả (tiền chuyển + phí)
                TransferService-->>GUI: 15. ném InsufficientBalanceException
                GUI-->>User: 16. Hiển thị cảnh báo số dư ví nguồn không đủ
            else Ví nguồn đủ số dư chi trả
                critical Chuyển tiền nguyên tử hai chiều
                    TransferService->>+Transfer: 17. create(transferId, sourceId, destId, amount, date, fee)
                    Transfer-->>-TransferService: 18. newTransferInstance
                    TransferService->>+SourceWallet: 19. debit(amount + feeAmount)
                    SourceWallet-->>-TransferService: 20. void
                    TransferService->>+DestWallet: 21. credit(amount)
                    DestWallet-->>-TransferService: 22. void
                    TransferService->>+WalletRepo: 23. save(sourceWallet)
                    WalletRepo-->>-TransferService: 24. savedSourceWallet
                    TransferService->>+WalletRepo: 25. save(destWallet)
                    WalletRepo-->>-TransferService: 26. savedDestWallet
                    TransferService->>+TxnRepo: 27. save(newTransferInstance)
                    TxnRepo-->>-TransferService: 28. savedTransfer
                end
                TransferService-->>-GUI: 29. TransferResponseDto
                GUI-->>-User: 30. Đóng hộp thoại, làm mới lịch sử và cập nhật số dư hai ví
            end
        end
    end
```

Giao dịch chuyển tiền được thực hiện theo cơ chế nguyên tử bảo đảm tổng tài sản toàn hệ thống được bảo toàn (trừ phần chi phí phát sinh thực tế), đồng thời số dư ví nguồn luôn duy trì không âm.

### 5.4.2 Sơ đồ Tra cứu Lịch sử Chuyển tiền

Luồng truy vấn và trích xuất lịch sử chuyển tiền vận hành hoàn toàn ở chế độ chỉ đọc. Chuỗi tương tác nạp các giao dịch chuyển tiền theo khoảng thời gian và cặp ví liên quan, tính tổng tiền chuyển và chi phí phát sinh trước khi đóng gói phản hồi về tầng trình diễn.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TRF-02`
- Thao tác hệ thống: `filterTransfers(startDate, endDate, sourceWalletId, destinationWalletId)`
- Yêu cầu chức năng: `FR-TRF-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi xử lý: `ERR-FIN-001`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant TransferService as TransferServiceImpl
    participant TxnRepo as TransactionRepository
    participant WalletRepo as WalletRepository

    User->>+GUI: 1. Chọn tiêu chí lọc (khoảng ngày, ví nguồn, ví đích) và bấm "Tìm kiếm"
    GUI->>+TransferService: 2. filterTransfers(criteriaDto)
    alt Ngày bắt đầu lớn hơn ngày kết thúc (startDate > endDate)
        TransferService-->>GUI: 3. ném ValidationException
        GUI-->>User: 4. Hiển thị thông báo khoảng thời gian lọc không hợp lệ
    else Khoảng thời gian lọc hợp lệ
        TransferService->>+TxnRepo: 5. findByDateRange(startDate, endDate)
        TxnRepo-->>-TransferService: 6. List<Transaction> allTransactions
        Note over TransferService: Lọc giao dịch TransferTransaction, tính tổng tiền chuyển và phí
        loop Với mỗi giao dịch chuyển tiền trong danh sách
            TransferService->>+WalletRepo: 7. findById(sourceId), findById(destId)
            WalletRepo-->>-TransferService: 8. Optional<Wallet> (Lấy tên ví hiển thị)
        end
        TransferService-->>-GUI: 9. TransferHistoryResponseDto
        GUI-->>-User: 10. Cập nhật bảng dữ liệu lịch sử chuyển tiền và bảng tổng hợp
    end
```

Thao tác tra cứu vận hành thuần túy ở chế độ chỉ đọc, không làm thay đổi trạng thái của các thực thể giao dịch hay số dư của bất kỳ ví nào trong hệ thống.

### 5.4.3 Sơ đồ Xóa và Hoàn tác Giao dịch Chuyển tiền

Luồng hủy giao dịch chuyển tiền thực hiện thu hồi tiền chuyển từ ví đích, hoàn trả tiền chuyển cùng khoản phí về ví nguồn, đồng thời xóa sạch bản ghi chuyển tiền và chi phí phát sinh liên kết.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TRF-03`
- Thao tác hệ thống: `deleteTransfer(transferId)`
- Yêu cầu chức năng: `FR-TRF-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`
- Mã lỗi xử lý: `ERR-WAL-001`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant TransferService as TransferServiceImpl
    participant TxnRepo as TransactionRepository
    participant WalletRepo as WalletRepository
    participant Transfer as TransferTransaction
    participant SourceWallet as SourceWallet
    participant DestWallet as DestWallet

    User->>+GUI: 1. Chọn giao dịch chuyển tiền và bấm "Xóa/Hủy chuyển tiền"
    GUI->>+TransferService: 2. deleteTransfer(transferId)
    TransferService->>+TxnRepo: 3. findById(transferId)
    TxnRepo-->>-TransferService: 4. Optional<Transaction> txnOpt
    alt Không tìm thấy giao dịch chuyển tiền
        TransferService-->>GUI: 5. ném EntityNotFoundException
        GUI-->>User: 6. Hiển thị thông báo không tìm thấy giao dịch cần xóa
    else Tìm thấy giao dịch
        TransferService->>+WalletRepo: 7. findById(sourceWalletId)
        WalletRepo-->>-TransferService: 8. Optional<Wallet> sourceOpt
        TransferService->>+WalletRepo: 9. findById(destWalletId)
        WalletRepo-->>-TransferService: 10. Optional<Wallet> destOpt
        alt Ví đích không đủ số dư để hoàn trả (destBalance < transferAmount)
            TransferService-->>GUI: 11. ném InsufficientBalanceException
            GUI-->>User: 12. Hiển thị cảnh báo ví nhận không đủ số dư để hoàn trả
        else Ví đích đủ số dư khả dụng
            critical Hoàn nguyên hai chiều số dư hai ví
                TransferService->>+Transfer: 13. revertFromWallet(sourceWallet, destWallet)
                Transfer-->>-TransferService: 14. void
                TransferService->>+TxnRepo: 15. deleteById(transferId)
                TxnRepo-->>-TransferService: 16. boolean (deleted)
                TransferService->>+WalletRepo: 17. save(destWallet)
                WalletRepo-->>-TransferService: 18. savedDestWallet
                TransferService->>+WalletRepo: 19. save(sourceWallet)
                WalletRepo-->>-TransferService: 20. savedSourceWallet
            end
            TransferService-->>-GUI: 21. phản hồi xóa thành công
            GUI-->>-User: 22. Đóng hộp thoại, làm mới bảng chuyển tiền và cập nhật số dư cả hai ví
        end
    end
```

Cơ chế hoàn nguyên hai pha bảo đảm thu hồi đồng thời số dư cả hai ví và xóa sạch các bản ghi chi phí phụ thuộc, ngăn chặn tuyệt đối tình trạng âm số dư ví nhận.

## 5.5 Sơ đồ Tuần tự Nhóm Danh mục Phân loại

Mục này mô hình hóa các luồng tương tác thực thi nghiệp vụ quản lý hệ thống danh mục phân loại thu chi trong hệ thống. Chuỗi tương tác mô tả quy trình tiếp nhận thao tác bổ sung danh mục mới, đổi tên hiển thị, khôi phục danh mục đã ẩn và thực hiện cơ chế xóa mềm nhằm bảo đảm tính liên tục của dữ liệu tài chính.

Các kịch bản làm rõ sự phối hợp giữa lớp dịch vụ điều phối với thực thể danh mục, kho lưu trữ danh mục và kho lưu trữ giao dịch, bảo đảm tuân thủ nghiêm ngặt quy tắc cấu trúc phân cấp ba tầng, kiểm soát tính duy nhất của tên gọi trong cùng phạm vi cha và bảo toàn toàn vẹn tham chiếu cho các giao dịch lịch sử đã ghi nhận.

### 5.5.1 Sơ đồ Thêm mới Danh mục Chi tiết

Luồng tương tác mô tả quá trình tiếp nhận yêu cầu bổ sung danh mục chi tiết cấp ba vào một nhóm nhu cầu cha đang hoạt động, thực hiện kiểm tra tính hợp lệ của định danh và tính khả dụng của nhóm cha trước khi khởi tạo thực thể. Dịch vụ ứng dụng kiểm soát tính duy nhất của tên trong phạm vi nhóm cha và lưu trữ thực thể danh mục mới vào kho dữ liệu.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`
- Thao tác hệ thống: `addCategory(categoryName, parentCategoryId)`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-005`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant CategoryService as CategoryServiceImpl
    participant CategoryRepo as CategoryRepository
    participant Category as Category

    User->>+GUI: 1. Nhập thông tin danh mục mới (categoryName, parentCategoryId)
    GUI->>+CategoryService: 2. createCategory(CreateCategoryRequestDto)
    alt Tên danh mục rỗng hoặc vượt quá 50 ký tự
        CategoryService-->>GUI: 3. ném ValidationException
        GUI-->>User: 4. Hiển thị thông báo tên danh mục không hợp lệ
    else Tên danh mục hợp lệ
        CategoryService->>+CategoryRepo: 5. findById(parentCategoryId)
        CategoryRepo-->>-CategoryService: 6. Optional<Category> parentOpt
        alt Nhóm cha không tồn tại trong hệ thống
            CategoryService-->>GUI: 7. ném EntityNotFoundException
            GUI-->>User: 8. Hiển thị thông báo nhóm cha không tồn tại
        else Nhóm cha đang ở trạng thái DELETED
            CategoryService-->>GUI: 9. ném BusinessException
            GUI-->>User: 10. Hiển thị thông báo nhóm cha đã bị xóa
        else Nhóm cha hợp lệ và đang hoạt động (ACTIVE)
            CategoryService->>+CategoryRepo: 11. existsByNameAndNeedGroupId(categoryName, parentCategoryId)
            CategoryRepo-->>-CategoryService: 12. boolean isDuplicate
            alt Tên danh mục đã tồn tại trong cùng nhóm cha
                CategoryService-->>GUI: 13. ném DuplicateEntityException
                GUI-->>User: 14. Hiển thị thông báo tên danh mục bị trùng lặp
            else Tên danh mục chưa tồn tại (Hợp lệ)
                CategoryService->>+Category: 15. new Category(newId, categoryName, parentCategoryId, transactionType, icon, color)
                Category-->>-CategoryService: 16. newCategory
                CategoryService->>+CategoryRepo: 17. save(newCategory)
                CategoryRepo-->>-CategoryService: 18. savedCategory
                CategoryService-->>-GUI: 19. CategoryResponseDto
                GUI-->>-User: 20. Kết xuất thông tin danh mục mới khởi tạo
            end
        end
    end
```

Danh mục mới luôn được gắn kết vào một nhóm cha hợp lệ đang hoạt động và kế thừa loại giao dịch cố định, bảo đảm tính toàn vẹn của cấu trúc phân cấp ba tầng.

### 5.5.2 Sơ đồ Đổi tên Danh mục

Luồng tương tác mô tả quy trình tiếp nhận yêu cầu cập nhật tên hiển thị của danh mục hiện có, kiểm tra định dạng tên mới và kiểm soát tính duy nhất trong phạm vi nhóm cha trực thuộc. Dịch vụ ứng dụng nạp thực thể danh mục, cập nhật tên mới và lưu trữ vào kho dữ liệu mà không làm biến động mã định danh hay lịch sử giao dịch liên kết.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-02`
- Thao tác hệ thống: `renameCategory(categoryId, newName)`
- Yêu cầu chức năng: `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`
- Mã lỗi xử lý: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant CategoryService as CategoryServiceImpl
    participant CategoryRepo as CategoryRepository
    participant Category as Category

    User->>+GUI: 1. Nhập yêu cầu đổi tên (categoryId, newName)
    GUI->>+CategoryService: 2. renameCategory(RenameCategoryRequestDto)
    alt Tên mới rỗng hoặc vượt quá 50 ký tự
        CategoryService-->>GUI: 3. ném ValidationException
        GUI-->>User: 4. Hiển thị thông báo tên mới không hợp lệ
    else Định dạng tên mới hợp lệ
        CategoryService->>+CategoryRepo: 5. findById(categoryId)
        CategoryRepo-->>-CategoryService: 6. Optional<Category> categoryOpt
        alt Danh mục không tồn tại trong hệ thống
            CategoryService-->>GUI: 7. ném EntityNotFoundException
            GUI-->>User: 8. Hiển thị thông báo danh mục không tồn tại
        else Danh mục tồn tại
            CategoryService->>+CategoryRepo: 9. existsByNameAndNeedGroupIdAndIdNot(newName, category.getNeedGroupId(), categoryId)
            CategoryRepo-->>-CategoryService: 10. boolean isDuplicate
            alt Tên mới trùng với danh mục khác trong cùng nhóm cha
                CategoryService-->>GUI: 11. ném DuplicateEntityException
                GUI-->>User: 12. Hiển thị thông báo tên mới bị trùng lặp
            else Tên mới duy nhất (Hợp lệ)
                CategoryService->>+Category: 13. rename(newName)
                Category-->>-CategoryService: 14. void
                CategoryService->>+CategoryRepo: 15. save(category)
                CategoryRepo-->>-CategoryService: 16. updatedCategory
                CategoryService-->>-GUI: 17. CategoryResponseDto
                GUI-->>-User: 18. Kết xuất thông tin danh mục sau khi đổi tên
            end
        end
    end
```

Việc đổi tên bảo toàn tuyệt đối mã định danh và loại thu chi của danh mục, không làm đứt gãy hoặc sai lệch dữ liệu lịch sử của các giao dịch đã ghi nhận trong quá khứ theo quy tắc đặt tên bất biến của định danh.

### 5.5.3 Sơ đồ Khôi phục Danh mục Đã xóa

Luồng tương tác mô tả quy trình phục hồi danh mục đã xóa mềm trở lại trạng thái hoạt động, đồng thời xác thực trạng thái hoạt động của nhóm cha và kiểm soát xung đột tên gọi trong nhóm. Dịch vụ ứng dụng cập nhật trạng thái của thực thể sang hoạt động và lưu vào kho dữ liệu, đưa danh mục tái xuất hiện trên các biểu mẫu giao dịch.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-03`
- Thao tác hệ thống: `restoreCategory(categoryId)`
- Yêu cầu chức năng: `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-CAT-001`, `ERR-CAT-004`, `ERR-NAM-002`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant CategoryService as CategoryServiceImpl
    participant CategoryRepo as CategoryRepository
    participant Category as Category

    User->>+GUI: 1. Chọn yêu cầu khôi phục danh mục (categoryId)
    GUI->>+CategoryService: 2. restoreCategory(categoryId)
    CategoryService->>+CategoryRepo: 3. findById(categoryId)
    CategoryRepo-->>-CategoryService: 4. Optional<Category> categoryOpt
    alt Danh mục không tồn tại hoặc đang ở trạng thái ACTIVE
        CategoryService-->>GUI: 5. ném EntityNotFoundException
        GUI-->>User: 6. Hiển thị thông báo danh mục không hợp lệ để khôi phục
    else Danh mục tồn tại và đang ở trạng thái DELETED
        CategoryService->>+CategoryRepo: 7. findById(category.getNeedGroupId())
        CategoryRepo-->>-CategoryService: 8. Optional<Category> parentOpt
        alt Nhóm cha đang ở trạng thái DELETED
            CategoryService-->>GUI: 9. ném BusinessException
            GUI-->>User: 10. Hiển thị thông báo từ chối do nhóm cha đang bị xóa
        else Nhóm cha đang hoạt động (ACTIVE)
            CategoryService->>+CategoryRepo: 11. existsByNameAndNeedGroupId(category.getName(), category.getNeedGroupId())
            CategoryRepo-->>-CategoryService: 12. boolean isDuplicate
            alt Tên danh mục trùng với danh mục đang hoạt động trong nhóm
                CategoryService-->>GUI: 13. ném DuplicateEntityException
                GUI-->>User: 14. Hiển thị thông báo xung đột tên danh mục đang hoạt động
            else Không bị trùng tên (Đủ điều kiện khôi phục)
                CategoryService->>+Category: 15. restore(isParentGroupActive)
                Category-->>-CategoryService: 16. void (chuyển trạng thái sang ACTIVE)
                CategoryService->>+CategoryRepo: 17. save(category)
                CategoryRepo-->>-CategoryService: 18. restoredCategory
                CategoryService-->>-GUI: 19. CategoryResponseDto
                GUI-->>-User: 20. Kết xuất thông báo khôi phục danh mục thành công
            end
        end
    end
```

Thao tác khôi phục chỉ được phép khi nhóm cha đang hoạt động và không xảy ra xung đột định danh, đưa danh mục trở lại danh sách lựa chọn mà không làm xáo trộn số liệu báo cáo lịch sử.

### 5.5.4 Sơ đồ Xóa Danh mục (Xóa cứng và Xóa mềm)

Luồng tương tác mô tả quy trình tiếp nhận yêu cầu loại bỏ danh mục phân loại, kiểm tra ràng buộc duy trì tối thiểu nhóm nhu cầu hoạt động và kiểm tra lịch sử giao dịch tham chiếu. Dịch vụ ứng dụng tự động phân nhánh thực hiện xóa cứng nếu danh mục chưa từng phát sinh giao dịch hoặc xóa mềm chuyển trạng thái để bảo toàn tính toàn vẹn dòng tiền.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-04`
- Thao tác hệ thống: `deleteCategory(categoryId)`
- Yêu cầu chức năng: `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-04`
- Mã lỗi xử lý: `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant CategoryService as CategoryServiceImpl
    participant CategoryRepo as CategoryRepository
    participant TxnRepo as TransactionRepository
    participant Category as Category

    User->>+GUI: 1. Yêu cầu xóa danh mục (categoryId)
    GUI->>+CategoryService: 2. deleteCategory(categoryId)
    CategoryService->>+CategoryRepo: 3. findById(categoryId)
    CategoryRepo-->>-CategoryService: 4. Optional<Category> categoryOpt
    alt Danh mục không tồn tại trong hệ thống
        CategoryService-->>GUI: 5. ném EntityNotFoundException
        GUI-->>User: 6. Hiển thị thông báo không tìm thấy danh mục
    else Danh mục tồn tại
        CategoryService->>+CategoryRepo: 7. countActiveByNeedGroupId(category.getNeedGroupId())
        CategoryRepo-->>-CategoryService: 8. long activeCount
        alt Danh mục là mục hoạt động duy nhất còn lại của nhóm cha
            CategoryService-->>GUI: 9. ném BusinessException
            GUI-->>User: 10. Hiển thị thông báo không thể xóa mục hoạt động cuối cùng
        else Nhóm cha còn các danh mục hoạt động khác
            CategoryService->>+TxnRepo: 11. existsByCategoryId(categoryId)
            TxnRepo-->>-CategoryService: 12. boolean hasTransactions
            alt Chưa từng phát sinh giao dịch (Xóa cứng)
                CategoryService->>+CategoryRepo: 13. deleteById(categoryId)
                CategoryRepo-->>-CategoryService: 14. void
                CategoryService-->>GUI: 15. phản hồi xóa cứng thành công
                GUI-->>User: 16. Hiển thị thông báo đã xóa hoàn toàn danh mục khỏi hệ thống
            else Đã từng phát sinh giao dịch lịch sử (Xóa mềm)
                CategoryService->>+Category: 17. markDeleted()
                Category-->>-CategoryService: 18. void (chuyển trạng thái sang DELETED)
                CategoryService->>+CategoryRepo: 19. save(category)
                CategoryRepo-->>-CategoryService: 20. updatedCategory
                CategoryService-->>-GUI: 21. phản hồi xóa mềm thành công
                GUI-->>-User: 22. Hiển thị thông báo đã chuyển danh mục sang trạng thái đã xóa
            end
        end
    end
```

Cơ chế xóa mềm bảo toàn tuyệt đối tính toàn vẹn tham chiếu lịch sử của mọi giao dịch liên kết và duy trì tối thiểu một nhóm nhu cầu hoạt động cho mỗi loại thu chi theo quy định tài chính.

## 5.6 Sơ đồ Tuần tự Nhóm Báo cáo và Quản trị Dữ liệu

Mục này mô hình hóa các luồng tương tác thực thi nghiệp vụ kết xuất báo cáo phân tích tài chính và quản trị dữ liệu an toàn của hệ thống. Chuỗi tương tác mô tả quy trình tổng hợp dòng tiền đa chiều, đo lường cơ cấu thu chi, trích xuất tệp sao lưu định dạng chuẩn, phục hồi dữ liệu có kiểm định bốn bước và đối soát độc lập các điều kiện cân bằng tài chính.

Các kịch bản làm rõ sự phối hợp giữa lớp dịch vụ điều phối báo cáo và quản trị dữ liệu với toàn bộ các kho lưu trữ thực thể trong hệ thống, bảo đảm nguyên tắc vận hành chỉ đọc đối với các luồng thống kê đối soát, kiểm soát tính toàn vẹn của tệp JSON sao lưu và duy trì tính nhất quán tuyệt đối của sổ cái tài chính.

### 5.6.1 Sơ đồ Lập Báo cáo Dòng tiền Định kỳ

Luồng tương tác mô tả quy trình truy vấn và tổng hợp dòng tiền định kỳ trong chuỗi 12 tháng liên tục lùi tính từ mốc tháng/năm kết thúc ở chế độ chỉ đọc, tính toán dòng tiền ròng và tỷ lệ tiết kiệm từng tháng. Dịch vụ ứng dụng nạp các giao dịch từ kho lưu trữ theo khoảng ngày, tính toán các chỉ số tài chính, áp dụng tỷ lệ tiết kiệm mặc định khi tổng thu bằng không để tránh lỗi số học và đóng gói kết quả báo cáo.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-01`
- Thao tác hệ thống: `generateCashFlowReport(CashFlowReportRequestDTO)` (và tiện ích `generateCashFlowReport(endMonth, endYear)`)
- Yêu cầu chức năng: `FR-REP-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`
- Mã lỗi xử lý: `ERR-REP-001`, `ERR-FIN-001`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant ReportService as ReportServiceImpl
    participant TxnRepo as TransactionRepository
    participant NetCashFlow as NetCashFlow
    participant SavingsRate as SavingsRate

    User->>+GUI: 1. Yêu cầu lập báo cáo dòng tiền (endMonth, endYear)
    GUI->>+ReportService: 2. generateCashFlowReport(CashFlowReportRequestDTO)
    alt Tham số không hợp lệ (request null, endMonth ngoài [1, 12] hoặc endYear nhỏ hơn 2000)
        ReportService-->>GUI: 3. ném BusinessException (ERR-REP-001)
        GUI-->>User: 4. Hiển thị thông báo tham số báo cáo không hợp lệ
    else Tham số hợp lệ
        ReportService->>+TxnRepo: 5. findByDateRange(startDate, endDate)
        TxnRepo-->>-ReportService: 6. List<Transaction> (IncomeExpenseTransaction và TransferTransaction)
        loop Xử lý tổng hợp 12 tháng liên tục
            ReportService->>ReportService: 7. tách thu nhập, chi tiêu và mọi TransactionFee (bỏ tiền gốc chuyển ví)
            ReportService->>+NetCashFlow: 8. NetCashFlow.of(monthIncome, monthExpense)
            NetCashFlow-->>-ReportService: 9. NetCashFlow netFlow
            ReportService->>+SavingsRate: 10. SavingsRate.of(netFlow, monthIncome)
            SavingsRate-->>-ReportService: 11. SavingsRate (0.00% nếu tổng thu không dương)
        end
        ReportService->>ReportService: 12. tính bình quân chia 12 và đóng gói CashFlowReportResponseDTO
        ReportService-->>-GUI: 13. CashFlowReportResponseDTO
        GUI-->>-User: 14. Kết xuất bảng báo cáo dòng tiền 12 tháng kèm biểu đồ
    end
```

Thao tác vận hành hoàn toàn ở chế độ chỉ đọc, không làm biến đổi trạng thái của bất kỳ thực thể hay số dư tài chính nào trong hệ thống.

### 5.6.2 Sơ đồ Lập Báo cáo Cơ cấu Thu chi

Luồng tương tác mô tả quy trình trích xuất và phân tích cơ cấu thu chi theo nhóm nhu cầu và danh mục chi tiết trong khoảng thời gian xác định ở chế độ chỉ đọc. Dịch vụ ứng dụng kiểm tra tính hợp lệ của tham số thời gian cùng loại luồng tiền, truy vấn giao dịch (gồm cả phí giao dịch liên kết khi phân tích chi tiêu) và thông tin phân cấp danh mục, gom cụm số liệu, tính toán tỷ trọng phân bổ chính xác và đối chiếu biến động với kỳ liền kề cùng độ dài.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-02`
- Thao tác hệ thống: `generateSpendingStructureReport(SpendingStructureRequestDTO)`
- Yêu cầu chức năng: `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-REP-001`, `ERR-FIN-002`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant ReportService as ReportServiceImpl
    participant TxnRepo as TransactionRepository
    participant NeedGroupRepo as NeedGroupRepository
    participant CategoryRepo as CategoryRepository
    participant ShareVO as SpendingStructureShare

    User->>+GUI: 1. Nhập tiêu chí báo cáo (startDate, endDate, transactionType)
    GUI->>+ReportService: 2. generateSpendingStructureReport(SpendingStructureRequestDTO)
    alt Tham số không hợp lệ (request null, thiếu ngày, startDate sau endDate hoặc loại ngoài EXPENSE/INCOME)
        ReportService-->>GUI: 3. ném BusinessException (ERR-REP-001)
        GUI-->>User: 4. Hiển thị thông báo tham số báo cáo không hợp lệ
    else Tham số hợp lệ
        ReportService->>+TxnRepo: 5. findByDateRange(prevStartDate, endDate)
        TxnRepo-->>-ReportService: 6. List<Transaction> dải ngày liền kề và hiện tại
        ReportService->>+NeedGroupRepo: 7. findAll()
        NeedGroupRepo-->>-ReportService: 8. List<NeedGroup> (gồm ACTIVE và DELETED)
        ReportService->>+CategoryRepo: 9. findAll()
        CategoryRepo-->>-ReportService: 10. List<Category> (gồm ACTIVE và DELETED)
        ReportService->>ReportService: 11. phân tách hai kỳ, lọc theo luồng (cộng TransactionFee nếu EXPENSE), giữ mục lớn hơn 0 và status DELETED
        alt Tổng tiền kỳ hiện tại bằng 0.00 VNĐ
            ReportService->>ReportService: 12. gán danh sách rỗng và tỷ lệ 0.00%
        else Tổng tiền kỳ hiện tại lớn hơn 0.00 VNĐ
            loop Tính tỷ trọng cơ cấu 2 cấp
                ReportService->>+ShareVO: 13. SpendingStructureShare.of(groupAmount, totalAmount, "NeedGroup")
                ShareVO-->>-ReportService: 14. tỷ trọng nhóm (share)
                ReportService->>+ShareVO: 15. SpendingStructureShare.of(catAmount, totalAmount, "Category") và theo nhóm cha
                ShareVO-->>-ReportService: 16. tỷ trọng danh mục (share và shareInGroup)
            end
        end
        ReportService->>ReportService: 17. sắp xếp giảm dần, tính biến động kỳ liền kề (cờ mới hoặc 0.00%) và đóng gói DTO
        ReportService-->>-GUI: 18. SpendingStructureReportResponseDTO
        GUI-->>-User: 19. Kết xuất báo cáo cơ cấu thu chi theo phân cấp hai tầng
    end
```

Thao tác trích xuất và tính toán tỷ trọng phân bổ thu chi ở chế độ chỉ đọc, bảo toàn tính toàn vẹn của dữ liệu danh mục và số liệu lịch sử.

### 5.6.3 Sơ đồ Xuất Dữ liệu Sao lưu Hệ thống

Luồng tương tác mô tả quy trình trích xuất toàn bộ dữ liệu tài chính trong hệ thống và đóng gói thành tệp sao lưu JSON chuẩn hóa theo cấu trúc đã định. Dịch vụ ứng dụng nạp toàn bộ thực thể từ tất cả các kho lưu trữ dữ liệu, tổng hợp vào đối tượng dữ liệu sao lưu và ủy quyền cho bộ điều hợp tệp ghi an toàn xuống đĩa cục bộ.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-DAT-01`
- Thao tác hệ thống: `exportBackupData(destinationPath)`
- Yêu cầu chức năng: `FR-DAT-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi xử lý: `StorageException`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant DataService as DataManagementServiceImpl
    participant WalletRepo as WalletRepository
    participant TxnRepo as TransactionRepository
    participant TransferRepo as TransferRepository
    participant CategoryRepo as CategoryRepository
    participant NeedGroupRepo as NeedGroupRepository
    participant AdjRepo as BalanceAdjustmentRepository
    participant StorageAdapter as JsonStorageAdapter

    User->>+GUI: 1. Nhập lệnh xuất sao lưu (destinationPath, scope)
    GUI->>+DataService: 2. exportBackupData(ExportBackupRequestDto)
    DataService->>+WalletRepo: 3. findAll()
    WalletRepo-->>-DataService: 4. List<Wallet>
    DataService->>+TxnRepo: 5. findAll()
    TxnRepo-->>-DataService: 6. List<IncomeExpenseTransaction>
    DataService->>+TransferRepo: 7. findAll()
    TransferRepo-->>-DataService: 8. List<TransferTransaction>
    DataService->>+CategoryRepo: 9. findAll()
    CategoryRepo-->>-DataService: 10. List<Category>
    DataService->>+NeedGroupRepo: 11. findAll()
    NeedGroupRepo-->>-DataService: 12. List<NeedGroup>
    DataService->>+AdjRepo: 13. findAll()
    AdjRepo-->>-DataService: 14. List<BalanceAdjustmentRecord>
    DataService->>DataService: 15. tổng hợp dữ liệu vào BackupPayloadDto
    DataService->>+StorageAdapter: 16. writeBackupFile(destinationPath, backupPayloadDto)
    alt Sự cố I/O, lỗi quyền ghi hoặc không đủ dung lượng đĩa
        StorageAdapter-->>DataService: 17. ném StorageException
        DataService-->>GUI: 18. ném StorageException
        GUI-->>User: 19. Hiển thị thông báo lỗi xuất dữ liệu sao lưu
    else Ghi tệp sao lưu an toàn thành công
        StorageAdapter-->>-DataService: 20. BackupFileMetadata
        DataService-->>-GUI: 21. phản hồi xuất sao lưu thành công kèm đường dẫn tệp
        GUI-->>-User: 22. Kết xuất thông báo xuất dữ liệu thành công kèm đường dẫn tệp
    end
```

Việc xuất dữ liệu sao lưu là thao tác chỉ đọc dữ liệu nghiệp vụ, tạo bản chụp trạng thái nhất quán và giữ nguyên độ chính xác số học của mọi giá trị tiền tệ.

### 5.6.4 Sơ đồ Phục hồi Dữ liệu Hệ thống

Luồng tương tác mô tả quy trình nạp dữ liệu từ tệp sao lưu JSON, thực thi tuần tự quy trình kiểm định bốn bước nghiêm ngặt theo nguyên tắc tất cả hoặc không trước khi ghi đè dữ liệu vào các kho lưu trữ. Toàn bộ dữ liệu hiện tại được bảo lưu nguyên vẹn nếu phát hiện bất kỳ sai phạm nào trong các bước kiểm định.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-DAT-02`
- Thao tác hệ thống: `restoreBackupData(backupFilePath)`
- Yêu cầu chức năng: `FR-DAT-02`
- Quy tắc nghiệp vụ: `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-DAT-001`, `ERR-DAT-002`, `StorageException`

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant DataService as DataManagementServiceImpl
    participant StorageAdapter as JsonStorageAdapter
    participant WalletRepo as WalletRepository
    participant TxnRepo as TransactionRepository
    participant TransferRepo as TransferRepository
    participant CategoryRepo as CategoryRepository
    participant NeedGroupRepo as NeedGroupRepository
    participant AdjRepo as BalanceAdjustmentRepository

    User->>+GUI: 1. Nhập đường dẫn tệp sao lưu (backupFilePath)
    GUI->>+DataService: 2. restoreBackupData(RestoreBackupRequestDto)
    DataService->>+StorageAdapter: 3. readBackupFile(backupFilePath)
    alt Tệp không tồn tại hoặc lỗi đọc I/O
        StorageAdapter-->>DataService: 4. ném StorageException
        DataService-->>GUI: 5. ném StorageException
        GUI-->>User: 6. Hiển thị thông báo không thể đọc tệp sao lưu
    else Đọc tệp thành công
        StorageAdapter-->>-DataService: 7. BackupPayloadDto
        Note over DataService: Bước 1: Kiểm tra cấu trúc cú pháp JSON và schema bắt buộc
        alt Sai cú pháp JSON hoặc thiếu các khóa thực thể bắt buộc
            DataService-->>GUI: 8. ném DataCorruptionException
            GUI-->>User: 9. Từ chối: Sai cấu trúc schema tệp sao lưu
        else Cấu trúc JSON hợp lệ
            Note over DataService: Bước 2: Kiểm tra cấu trúc phân cấp danh mục 3 cấp
            alt Vi phạm phân cấp 3 cấp hoặc phát hiện danh mục mồ côi
                DataService-->>GUI: 10. ném DataCorruptionException
                GUI-->>User: 11. Từ chối: Vi phạm cấu trúc phân cấp danh mục
            else Phân cấp danh mục hợp lệ
                Note over DataService: Bước 3: Kiểm tra liên kết chuyển tiền hai chiều và phí
                alt Sai lệch cặp giao dịch chuyển tiền hoặc phí mồ côi
                    DataService-->>GUI: 12. ném DataCorruptionException
                    GUI-->>User: 13. Từ chối: Vi phạm liên kết chuyển tiền hoặc phí mồ côi
                else Liên kết giao dịch hợp lệ
                    Note over DataService: Bước 4: Tái tính toán số dư ví từ lịch sử
                    alt Phát hiện số dư ví bị âm tại bất kỳ thời điểm nào
                        DataService-->>GUI: 14. ném DataCorruptionException
                        GUI-->>User: 15. Từ chối: Vi phạm quy tắc số dư ví không được âm
                    else Vượt qua toàn bộ 4 bước kiểm định (All-or-Nothing)
                        DataService->>+WalletRepo: 16. deleteAll(), saveAll(wallets)
                        WalletRepo-->>-DataService: 17. void
                        DataService->>+NeedGroupRepo: 18. deleteAll(), saveAll(needGroups)
                        NeedGroupRepo-->>-DataService: 19. void
                        DataService->>+CategoryRepo: 20. deleteAll(), saveAll(categories)
                        CategoryRepo-->>-DataService: 21. void
                        DataService->>+TxnRepo: 22. deleteAll(), saveAll(transactions)
                        TxnRepo-->>-DataService: 23. void
                        DataService->>+TransferRepo: 24. deleteAll(), saveAll(transfers)
                        TransferRepo-->>-DataService: 25. void
                        DataService->>+AdjRepo: 26. deleteAll(), saveAll(adjustments)
                        AdjRepo-->>-DataService: 27. void
                        DataService-->>-GUI: 28. phản hồi phục hồi thành công
                        GUI-->>-User: 29. Kết xuất thông báo phục hồi toàn bộ dữ liệu thành công
                    end
                end
            end
        end
    end
```

Hệ thống bảo lưu nguyên vẹn dữ liệu hiện tại nếu phát hiện bất kỳ sai phạm nào trong bốn bước kiểm định và chỉ ghi đè khi toàn bộ tệp sao lưu hợp lệ 100%.

### 5.6.5 Sơ đồ Đối soát Toàn vẹn Dữ liệu Tài chính

Luồng tương tác mô tả quy trình quét kiểm tra độc lập bốn điều kiện cân bằng tài chính của toàn bộ hệ thống và xuất báo cáo đối soát chi tiết ở chế độ chỉ đọc. Dịch vụ ứng dụng nạp dữ liệu từ các kho lưu trữ, đánh giá tính nhất quán của số dư ví, cân bằng chuyển tiền, phân cấp danh mục và kiểm tra tính toàn vẹn của các khoản chi phí gắn liền với giao dịch chứa phí (theo quan hệ hợp thành mà không cần kho lưu trữ phí riêng biệt), phân loại cảnh báo theo các cấp độ mà không làm biến đổi dữ liệu.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-DAT-03`
- Thao tác hệ thống: `reconcileSystemData()`
- Yêu cầu chức năng: `FR-DAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi xử lý: Không ném ngoại lệ chặn, phân loại kết quả kiểm tra theo các mức INFO, WARNING, ERROR

**Sơ đồ tuần tự tương tác:**

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant GUI as Tầng Trình diễn
    participant DataService as DataManagementServiceImpl
    participant WalletRepo as WalletRepository
    participant TxnRepo as TransactionRepository
    participant TransferRepo as TransferRepository
    participant CategoryRepo as CategoryRepository

    User->>+GUI: 1. Yêu cầu đối soát dữ liệu hệ thống
    GUI->>+DataService: 2. reconcileSystemData()
    DataService->>+WalletRepo: 3. findAll()
    WalletRepo-->>-DataService: 4. List<Wallet>
    DataService->>+TxnRepo: 5. findAll()
    TxnRepo-->>-DataService: 6. List<IncomeExpenseTransaction>
    DataService->>+TransferRepo: 7. findAll()
    TransferRepo-->>-DataService: 8. List<TransferTransaction>
    DataService->>+CategoryRepo: 9. findAll()
    CategoryRepo-->>-DataService: 10. List<Category>
    Note over DataService: Điều kiện 1: Tái tính số dư ví từ lịch sử dòng tiền và so khớp số dư hiện hành
    Note over DataService: Điều kiện 2: Kiểm tra cân bằng nguồn - đích và chi phí chuyển tiền
    Note over DataService: Điều kiện 3: Kiểm tra liên kết phân cấp danh mục 3 cấp và loại thu chi
    Note over DataService: Điều kiện 4: Kiểm tra chi phí phát sinh (TransactionFee) gắn kèm các giao dịch thu chi và chuyển tiền, bảo đảm không có phí mồ côi
    DataService->>DataService: 11. tổng hợp sai lệch và phân loại mức độ nghiêm trọng (INFO, WARNING, ERROR)
    alt Tất cả 4 điều kiện đều thỏa mãn 100%
        DataService->>DataService: 12. đánh dấu trạng thái dữ liệu nhất quán hoàn hảo (INFO)
    else Phát hiện sai lệch dữ liệu
        DataService->>DataService: 13. đóng gói danh sách chi tiết bất thường kèm khuyến nghị (WARNING/ERROR)
    end
    DataService-->>-GUI: 14. ReconciliationReportDto
    GUI-->>-User: 15. Kết xuất Báo cáo đối soát toàn vẹn dữ liệu tài chính chi tiết
```

Thao tác đối soát vận hành thuần túy ở chế độ chỉ đọc, không làm biến đổi bất kỳ bản ghi tài chính nào và cung cấp báo cáo minh bạch về mức độ nhất quán của sổ cái.
