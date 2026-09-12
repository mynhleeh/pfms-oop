# BÁO CÁO MILESTONE 2: OBJECT-ORIENTED ANALYSIS (OOA)
## HỆ THỐNG QUẢN LÝ TÀI CHÍNH CÁ NHÂN (PFMS)

**Nhóm 3:**
- Ứng Trọng Trình — B24DCCE271
- Nguyễn Đinh Anh Quân — B24DCCE222
- Lê Anh Minh — B24DCCE180
- Nguyễn Đình Tiến — B24DCCE264

**Tham chiếu tài liệu nền tảng:**
- Đặc tả Yêu cầu & Use Cases: `docs/requirement and use cases/req-uc.md`
- Mô hình dữ liệu nguồn: `docs/finance-mng.xlsm` & `docs/finance-mng-description.md`
- Chuẩn đánh giá môn học: `docs/assignment-requirement.pdf` (Rubric 100 điểm)

---

## 1. Phương pháp Phân tích Ngữ pháp (Noun-Verb Analysis)

Dựa trên 15 Use Cases (`req-uc.md`) và 18 bảng dữ liệu (`finance-mng.xlsm`), áp dụng kỹ thuật trích xuất thực thể theo ngữ pháp tiếng Anh & tiếng Việt:
- **Danh từ (Nouns) $\rightarrow$ Candidate Classes, Attributes, Value Objects:** ví (wallet), giao dịch (transaction), thu nhập (income), chi tiêu (expense), chuyển khoản (transfer), danh mục (category), nhóm (group), công nợ (debt), cho vay (lend), đi vay (borrow), sổ tiết kiệm (saving book), kỳ hạn (term), lãi suất (rate), chứng chỉ quỹ (fund certificate), quỹ mục tiêu (goal fund), ảnh chụp chốt tháng (monthly snapshot), số tiền (money, amount).
- **Động từ (Verbs) $\rightarrow$ Operations, Responsibilities, Collaborations:** ghi nhận thu/chi (record, credit, debit), chuyển tiền (transfer), xác thực số dư (validate balance), phân loại danh mục (classify), tính số dư (calculate balance), tính tiền lãi tích lũy (calculate accrued interest), tính suất sinh lời (calculate RATE), phân bổ tiền mặt (allocate cash), chốt số tháng (freeze snapshot), tổng hợp tài sản (aggregate assets).

---

## 2. Bảng Phân loại Candidate Objects & Ranh giới Domain

Hệ thống phân định 4 nhóm đối tượng chính theo chuẩn DDD (Domain-Driven Design):
1. **Entities:** Đối tượng có định danh duy nhất (`id`), trạng thái thay đổi theo thời gian.
2. **Value Objects:** Đối tượng bất biến (Immutable), không có `id`, định danh bằng toàn bộ giá trị các thuộc tính của nó.
3. **Enums:** Tập hợp các hằng số định kiểu có ràng buộc chặt chẽ về ngữ nghĩa.
4. **Domain Interfaces:** Các giao diện định nghĩa hợp đồng hành vi trừu tượng đa hình.

### Bảng 2.1: Danh mục Các Lớp Thực Thể (Entities)

| # | Tên Lớp (Class Name) | Loại | Scope | Trách nhiệm Nghiệp vụ Cốt lõi | Nguồn Tham chiếu |
|---|---|---|---|---|---|
| 1 | `Wallet` | Entity | `Core` | Đại diện ví/tài khoản thanh toán; quản lý số dư, ghi nhận biến động tăng/giảm | `WAL-01`, bảng `wallets` |
| 2 | `BaseTransaction` | Abstract Entity | `Core` | Lớp cơ sở trừu tượng cho giao dịch thu/chi gắn với 1 ví và danh mục phân loại | `TXN-01..03`, bảng `transactions` |
| 3 | `IncomeTransaction` | Concrete Entity | `Core` | Giao dịch thu nhập; làm tăng số dư ví tương ứng (`credit`) | `TXN-01`, `type=income` |
| 4 | `ExpenseTransaction` | Concrete Entity | `Core` | Giao dịch chi tiêu; làm giảm số dư ví tương ứng (`debit`) | `TXN-01`, `type=expense` |
| 5 | `Transfer` | Entity | `Core` | Sự kiện luân chuyển tiền nội bộ giữa 2 ví khác nhau; không tính thu/chi | `TRF-01`, bảng `transfers` |
| 6 | `CategoryGroup` | Entity | `Core` | Nhóm phân loại cấp 2 (ví dụ: essentials, employment); sở hữu các danh mục con | `CAT-01..02`, bảng `classification` |
| 7 | `Category` | Entity | `Core` | Danh mục chi tiết cấp 3 (ví dụ: groceries, salary); dùng để phân loại giao dịch | `CAT-01..02`, bảng `classification` |
| 8 | `DebtOperation` | Abstract Entity | `Deferred` | Lớp cơ sở trừu tượng cho 6 nghiệp vụ công nợ phát sinh theo từng đợt (`batch`) | `DBT-01`, bảng `debt` |
| 9 | `LendOperation` | Concrete Entity | `Deferred` | Nghiệp vụ cho vay; làm tăng khoản phải thu (`receivable`) và giảm ví | `DBT-01`, `type=lend` |
| 10 | `BorrowOperation` | Concrete Entity | `Deferred` | Nghiệp vụ đi vay; làm tăng khoản phải trả (`payable`) và tăng ví | `DBT-01`, `type=borrow` |
| 11 | `CollectOperation` | Concrete Entity | `Deferred` | Nghiệp vụ thu nợ; làm giảm khoản phải thu và tăng ví | `DBT-01`, `type=collect` |
| 12 | `RepayOperation` | Concrete Entity | `Deferred` | Nghiệp vụ trả nợ; làm giảm khoản phải trả và giảm ví | `DBT-01`, `type=repay` |
| 13 | `WriteOffLendOperation` | Concrete Entity | `Deferred` | Nghiệp vụ xóa nợ cho vay (nợ khó đòi); làm giảm khoản phải thu, không đổi ví | `DBT-01`, `type=write-off lend` |
| 14 | `WriteOffBorrowOperation` | Concrete Entity | `Deferred` | Nghiệp vụ được chủ nợ xóa nợ; làm giảm khoản phải trả, không đổi ví | `DBT-01`, `type=write-off borrow` |
| 15 | `SavingBook` | Entity | `Deferred` | Sổ tiết kiệm; quản lý vốn gốc, kỳ hạn, lãi suất và tự tính lãi tích lũy | `SAV-01`, bảng `savings` |
| 16 | `FundCertificate` | Entity | `Deferred` | Chứng chỉ quỹ/cổ phiếu đầu tư; theo dõi vốn gốc, giá trị hiện tại và lãi/lỗ | `FCT-01`, bảng `fund_certs` |
| 17 | `GoalFund` | Entity | `Deferred` | Quỹ mục tiêu cá nhân; tổng hợp tài sản từ tiền mặt, tiết kiệm và chứng chỉ quỹ | `GFD-01`, bảng `funds` |
| 18 | `AllocatedCash` | Entity | `Deferred` | Khoản tiền mặt/tiền gửi thanh khoản được phân bổ riêng cho một quỹ mục tiêu | `GFD-01`, bảng `funds_cash` |
| 19 | `MonthlySnapshot` | Entity | `Deferred` | Bản ghi chốt số dư tài sản cuối tháng; đóng băng số liệu phục vụ báo cáo xu hướng | `SST-01`, bảng `assets_total` |

---

### Bảng 2.2: Danh mục Value Objects, Enums & Interfaces

| # | Tên Định danh | Phân loại | Scope | Ý nghĩa & Vai trò Thiết kế |
|---|---|---|---|---|
| 1 | `Money` | Value Object | `Core` | Đóng gói số tiền `BigDecimal amount` và `Currency`. Đảm bảo scale=2, rounding chuẩn. Bất biến. |
| 2 | `InterestRate` | Value Object | `Deferred` | Đóng gói tỷ lệ lãi suất hàng năm dạng `BigDecimal` (ví dụ: `0.065` = 6.5%/năm). |
| 3 | `DateRange` | Value Object | `Core` | Khoảng thời gian `[startDate, endDate]`, dùng cho bộ lọc thời gian và tính toán kỳ thống kê. |
| 4 | `TermPeriod` | Value Object | `Deferred` | Quy đổi kỳ hạn tiết kiệm sang số ngày (1 tháng = 30 ngày, ..., unlimited = 0 ngày). |
| 5 | `CategoryPath` | Value Object | `Core` | Biểu diễn chuỗi phân cấp danh mục 3 cấp `type / group / category`. |
| 6 | `TransactionType` | Enum | `Core` | Hằng số `INCOME`, `EXPENSE`. |
| 7 | `DebtType` | Enum | `Deferred` | Hằng số 6 loại công nợ: `LEND`, `BORROW`, `COLLECT`, `REPAY`, `WRITE_OFF_LEND`, `WRITE_OFF_BORROW`. |
| 8 | `FundCertType` | Enum | `Deferred` | Hằng số loại tài sản đầu tư: `STOCK`, `BOND`, `BALANCE`. |
| 9 | `GoalFundType` | Enum | `Deferred` | Hằng số 4 quỹ mục tiêu: `EMERGENCY`, `HEALTH`, `TARGET`, `RETIREMENT`. |
| 10 | `WalletImpactable` | Interface | `Core` | Hợp đồng cho mọi đối tượng gây biến động số dư ví: `void applyImpact(WalletAccountBook book)`. |
| 11 | `ValuableAsset` | Interface | `Core` | Hợp đồng cho các tài sản cấu thành tổng tài sản: `BigDecimal getCurrentValue()`, `AssetType getAssetType()`. |
| 12 | `InterestCalculationStrategy` | Interface | `Deferred` | Strategy Pattern cho các thuật toán tính lãi sổ tiết kiệm (kỳ hạn cố định vs không kỳ hạn). |

---

## 3. Hệ Thống Thẻ CRC (Class - Responsibility - Collaborator)

Áp dụng kỹ thuật CRC Cards để xác định tường minh trách nhiệm của từng lớp theo nguyên lý Single Responsibility Principle (SRP) và mô hình phân quyền GRASP (Information Expert, High Cohesion, Low Coupling).

```
+-------------------------------------------------------------------------------+
| Class: Wallet                                                                 |
| Scope: Core                                                                   |
+-------------------------------------------------------+-----------------------+
| Responsibilities (Trách nhiệm)                        | Collaborators         |
+-------------------------------------------------------+-----------------------+
| 1. Quản lý danh tính ví (id, name, walletType).       | Money                 |
| 2. Quản lý số dư khả dụng (currentBalance).           | WalletImpactable      |
| 3. Tăng số dư khi nhận tiền (credit).                 | InsufficientBalance-  |
| 4. Giảm số dư khi rút/chi tiền (debit), kiểm tra      |   Exception           |
|    điều kiện không âm số dư (invariants).             | ValuableAsset         |
| 5. Cung cấp giá trị tài sản hiện tại (implements      |                       |
|    ValuableAsset).                                    |                       |
+-------------------------------------------------------+-----------------------+
```

```
+-------------------------------------------------------------------------------+
| Class: BaseTransaction (Abstract)                                             |
| Scope: Core                                                                   |
+-------------------------------------------------------+-----------------------+
| Responsibilities (Trách nhiệm)                        | Collaborators         |
+-------------------------------------------------------+-----------------------+
| 1. Đóng gói dữ liệu giao dịch (id, date, amount, note)| Money, Wallet         |
| 2. Gắn kết với một ví cụ thể và một danh mục hợp lệ.  | Category              |
| 3. Định nghĩa phương thức trừu tượng applyImpact().   | WalletImpactable      |
| 4. Xác thực số tiền giao dịch > 0.                    | InvalidAmountException|
+-------------------------------------------------------+-----------------------+
```

```
+-------------------------------------------------------------------------------+
| Class: IncomeTransaction / ExpenseTransaction (Concretes)                     |
| Scope: Core                                                                   |
+-------------------------------------------------------+-----------------------+
| Responsibilities (Trách nhiệm)                        | Collaborators         |
+-------------------------------------------------------+-----------------------+
| 1. IncomeTransaction: Tác động ví bằng cách credit    | Wallet                |
|    số tiền tương ứng vào ví chỉ định.                 | BaseTransaction       |
| 2. ExpenseTransaction: Tác động ví bằng cách debit    |                       |
|    số tiền tương ứng khỏi ví chỉ định.                |                       |
+-------------------------------------------------------+-----------------------+
```

```
+-------------------------------------------------------------------------------+
| Class: Transfer                                                               |
| Scope: Core                                                                   |
+-------------------------------------------------------+-----------------------+
| Responsibilities (Trách nhiệm)                        | Collaborators         |
+-------------------------------------------------------+-----------------------+
| 1. Ghi nhận giao dịch chuyển tiền giữa 2 ví.          | Wallet (source,       |
| 2. Xác thực điều kiện ràng buộc: source != target.    |         destination)  |
| 3. Cập nhật số dư nguyên tử: trừ ví nguồn, cộng ví    | Money                 |
|    đích qua WalletImpactable.                         | SelfTransferException |
+-------------------------------------------------------+-----------------------+
```

```
+-------------------------------------------------------------------------------+
| Class: CategoryGroup & Category                                               |
| Scope: Core                                                                   |
+-------------------------------------------------------+-----------------------+
| Responsibilities (Trách nhiệm)                        | Collaborators         |
+-------------------------------------------------------+-----------------------+
| 1. CategoryGroup: Quản lý nhóm phân loại theo type    | Category              |
|    (INCOME hoặc EXPENSE); sở hữu danh mục con.        | TransactionType       |
| 2. Category: Đóng gói tên danh mục chi tiết, tham     | CategoryPath          |
|    chiếu ngược về Group sở hữu.                       | CategoryMismatch-     |
| 3. Xác thực tính nhất quán 3 cấp (type/group/cat).    |   Exception           |
+-------------------------------------------------------+-----------------------+
```

```
+-------------------------------------------------------------------------------+
| Class: DebtOperation (Abstract)                                               |
| Scope: Deferred                                                               |
+-------------------------------------------------------+-----------------------+
| Responsibilities (Trách nhiệm)                        | Collaborators         |
+-------------------------------------------------------+-----------------------+
| 1. Đóng gói thông tin công nợ: đợt vay (batch), đối   | Money                 |
|    tác (person), ngày phát sinh, số tiền, ví, ghi chú.| Wallet                |
| 2. Định nghĩa hợp đồng đa hình: tác động ví           | WalletImpactable      |
|    (applyImpact) và tác động công nợ tổng hợp         | DebtImpactContext     |
|    (applyDebtLedger).                                 |                       |
+-------------------------------------------------------+-----------------------+
```

```
+-------------------------------------------------------------------------------+
| Class: Concrete Debt Operations (Lend, Borrow, Collect, Repay, Write-Off)     |
| Scope: Deferred                                                               |
+-------------------------------------------------------+-----------------------+
| Responsibilities (Trách nhiệm)                        | Collaborators         |
+-------------------------------------------------------+-----------------------+
| 1. LendOperation: Tăng receivable (+), giảm ví (-).   | Wallet                |
| 2. BorrowOperation: Tăng payable (+), tăng ví (+).    | DebtLedger            |
| 3. CollectOperation: Giảm receivable (-), tăng ví (+).| OverCollection-       |
| 4. RepayOperation: Giảm payable (-), giảm ví (-).     |   Exception           |
| 5. WriteOffLend: Giảm receivable (-), ví không đổi.   |                       |
| 6. WriteOffBorrow: Giảm payable (-), ví không đổi.    |                       |
+-------------------------------------------------------+-----------------------+
```

```
+-------------------------------------------------------------------------------+
| Class: SavingBook                                                             |
| Scope: Deferred                                                               |
+-------------------------------------------------------+-----------------------+
| Responsibilities (Trách nhiệm)                        | Collaborators         |
+-------------------------------------------------------+-----------------------+
| 1. Quản lý thông tin sổ tiết kiệm: vốn gốc, kỳ hạn,   | Money, InterestRate   |
|    lãi suất, ngày mở sổ.                              | TermPeriod, GoalFund  |
| 2. Tính tiến độ kỳ hạn (process %): ngày gửi / kỳ hạn.| InterestCalculation-  |
| 3. Tính lãi tích lũy và số dư hiện tại (gốc + lãi)    |   Strategy            |
|    thông qua Strategy được tiêm vào.                  | ValuableAsset         |
| 4. Cung cấp giá trị tài sản (implements ValuableAsset)|                       |
+-------------------------------------------------------+-----------------------+
```

```
+-------------------------------------------------------------------------------+
| Class: FundCertificate                                                        |
| Scope: Deferred                                                               |
+-------------------------------------------------------+-----------------------+
| Responsibilities (Trách nhiệm)                        | Collaborators         |
+-------------------------------------------------------+-----------------------+
| 1. Theo dõi chứng chỉ quỹ: mã quỹ, loại (stock/bond), | Money, FundCertType   |
|    vốn gốc đầu tư, ngày mua, giá trị thị trường.      | GoalFund              |
| 2. Tính lãi/lỗ tuyệt đối: balance - principal.        | FinancialRate-        |
| 3. Tính suất sinh lời quy đổi năm bằng hàm RATE().    |   Calculator          |
| 4. Cung cấp giá trị tài sản (implements ValuableAsset)| ValuableAsset         |
+-------------------------------------------------------+-----------------------+
```

```
+-------------------------------------------------------------------------------+
| Class: GoalFund                                                               |
| Scope: Deferred                                                               |
+-------------------------------------------------------+-----------------------+
| Responsibilities (Trách nhiệm)                        | Collaborators         |
+-------------------------------------------------------+-----------------------+
| 1. Quản lý quỹ mục tiêu (emergency, health, etc.).    | GoalFundType          |
| 2. Sở hữu phần tiền mặt phân bổ (AllocatedCash).      | AllocatedCash         |
| 3. Tập hợp danh sách SavingBooks và FundCertificates. | SavingBook, FundCert  |
| 4. Tính tổng tài sản quỹ: cash + saving + fund_cert.  | Money                 |
| 5. Tính tiến độ hoàn thành so với mục tiêu:           |                       |
|    process = total / target.                          |                       |
+-------------------------------------------------------+-----------------------+
```

---

## 4. Chi tiết Thuộc tính (Attributes) & Hành vi (Operations)

Mọi trường dữ liệu trong các lớp đều được đặt phạm vi truy cập `private` (hoặc `protected` cho lớp trừu tượng) nhằm bảo đảm nguyên lý **Encapsulation** tuyệt đối. Việc truy cập và cập nhật trạng thái chỉ được thực hiện thông qua các phương thức nghiệp vụ có kiểm soát hợp lệ.

### 4.1 Lớp `Wallet`
- **Thuộc tính:**
  - `private final UUID id;`
  - `private String name;`
  - `private Money currentBalance;`
  - `private final LocalDateTime createdAt;`
- **Phương thức nghiệp vụ:**
  - `public void credit(Money amount)`: Tăng số dư ví.
  - `public void debit(Money amount)`: Giảm số dư ví; ném `InsufficientBalanceException` nếu `currentBalance < amount`.
  - `public Money getCurrentValue()`: Trả về số dư hiện tại (implements `ValuableAsset`).
  - `public AssetType getAssetType()`: Trả về `AssetType.WALLET`.

### 4.2 Lớp trừu tượng `BaseTransaction` & Lớp con
- **Thuộc tính (`BaseTransaction`):**
  - `protected final UUID id;`
  - `protected LocalDate transactionDate;`
  - `protected Money amount;`
  - `protected Wallet wallet;`
  - `protected Category category;`
  - `protected String note;`
- **Phương thức nghiệp vụ:**
  - `public abstract void applyImpact(WalletAccountBook book);`
  - `public abstract TransactionType getTransactionType();`
- **Cài đặt tại `IncomeTransaction`:**
  - `public void applyImpact(WalletAccountBook book) { wallet.credit(this.amount); }`
  - `public TransactionType getTransactionType() { return TransactionType.INCOME; }`
- **Cài đặt tại `ExpenseTransaction`:**
  - `public void applyImpact(WalletAccountBook book) { wallet.debit(this.amount); }`
  - `public TransactionType getTransactionType() { return TransactionType.EXPENSE; }`

### 4.3 Lớp `Transfer`
- **Thuộc tính:**
  - `private final UUID id;`
  - `private LocalDate transferDate;`
  - `private Money amount;`
  - `private Wallet sourceWallet;`
  - `private Wallet destinationWallet;`
  - `private String note;`
- **Phương thức nghiệp vụ:**
  - `public void applyImpact(WalletAccountBook book)`:
    - Kiểm tra `sourceWallet.getId() != destinationWallet.getId()`.
    - Thực thi nguyên tử: `sourceWallet.debit(amount)` rồi `destinationWallet.credit(amount)`.

### 4.4 Lớp `SavingBook`
- **Thuộc tính:**
  - `private final UUID id;`
  - `private GoalFund goalFund;`
  - `private Money principal;`
  - `private TermPeriod termPeriod;`
  - `private InterestRate annualRate;`
  - `private LocalDate startDate;`
  - `private InterestCalculationStrategy calculationStrategy;`
- **Phương thức nghiệp vụ:**
  - `public Money calculateAccruedInterest(LocalDate asOfDate)`: Gọi `calculationStrategy.calculate(...)`.
  - `public Money getCurrentValue()`: Trả về `principal.add(calculateAccruedInterest(LocalDate.now()))`.
  - `public BigDecimal calculateTermProgress(LocalDate asOfDate)`: Tính tỷ lệ ngày đã gửi / tổng ngày kỳ hạn.

### 4.5 Lớp `FundCertificate`
- **Thuộc tính:**
  - `private final UUID id;`
  - `private GoalFund goalFund;`
  - `private String fundName;`
  - `private FundCertType certType;`
  - `private Money principal;`
  - `private Money currentMarketValue;`
  - `private LocalDate purchaseDate;`
- **Phương thức nghiệp vụ:**
  - `public Money calculateInterest()`: Trả về `currentMarketValue.subtract(principal)`.
  - `public BigDecimal calculateAnnualizedReturn(LocalDate asOfDate)`: Gọi `FinancialRateCalculator.calculateRate(...)`.
  - `public Money getCurrentValue()`: Trả về `currentMarketValue`.

### 4.6 Lớp `GoalFund`
- **Thuộc tính:**
  - `private final UUID id;`
  - `private GoalFundType fundType;`
  - `private Money targetAmount;`
  - `private AllocatedCash allocatedCash;`
  - `private final List<SavingBook> savings;`
  - `private final List<FundCertificate> fundCertificates;`
- **Phương thức nghiệp vụ:**
  - `public Money calculateTotalValue()`: Tổng hợp `cash + Σ savings + Σ fund_certs`.
  - `public BigDecimal calculateCompletionProgress()`: `totalValue / targetAmount` (kiểm tra target > 0).

---

## 5. Ràng Buộc Bất Biến & Cây Ngoại Lệ Nghiệp Vụ (Invariants & Exceptions)

### 5.1 Các Ràng buộc Bất biến (Business Invariants)
1. **Số tiền dương (Positive Money Invariant):** Mọi khoản tiền giao dịch, chuyển khoản, gửi tiết kiệm, mua chứng chỉ quỹ, công nợ đều phải $> 0$.
2. **Bảo toàn số dư ví (Solvency Invariant):** Không cho phép chi tiêu hoặc chuyển khoản vượt quá số dư ví khả dụng nếu không có cấu hình ví thấu chi.
3. **Tính phi chu trình trong chuyển khoản (Distinct Transfer Invariant):** Ví nguồn và ví đích trong giao dịch chuyển khoản bắt buộc phải khác nhau (`sourceWallet.id != destinationWallet.id`).
4. **Tính nhất quán của danh mục (Classification Integrity Invariant):** Giao dịch có `type=INCOME` chỉ được phép chọn danh mục thuộc nhóm của Income; giao dịch `EXPENSE` chỉ được chọn danh mục thuộc nhóm của Expense.
5. **Ràng buộc công nợ (Debt Collection Invariant):** Số tiền thu nợ (`CollectOperation`) không được vượt quá số nợ gốc còn lại của đối tác trong đợt vay đó.

### 5.2 Cây Ngoại Lệ Đặc Thù (Custom Exception Hierarchy)

Hệ thống xây dựng cây phân cấp ngoại lệ riêng biệt kế thừa từ `RuntimeException`, phục vụ việc bắt lỗi chuẩn xác và hiển thị thông báo thân thiện cho người dùng trên JavaFX UI:

```
PFMSException (Abstract RuntimeException)
├── DomainValidationException
│   ├── InvalidAmountException (Số tiền <= 0 hoặc sai scale)
│   ├── CategoryMismatchException (Danh mục không khớp type/group)
│   └── SelfTransferException (Ví nguồn trùng ví đích)
├── FinancialInsolventException
│   ├── InsufficientBalanceException (Số dư ví không đủ để chi/chuyển)
│   └── OverCollectionException (Thu nợ vượt quá số dư công nợ còn lại)
└── InfrastructureException
    ├── EntityNotFoundException (Không tìm thấy bản ghi theo ID)
    └── DataImportValidationException (Dữ liệu file Excel/CSV không hợp lệ)
```

---

## 6. Tổng Kết Đánh Giá Milestone 2

- **Độ phủ yêu cầu:** Đáp ứng đầy đủ 100% các Use Cases từ `WAL-01` tới `IEX-02`.
- **Tuân thủ SOLID:**
  - **SRP:** Mỗi class đảm nhiệm duy nhất một nghiệp vụ; không dồn logic vào một "God Class".
  - **OCP:** Sẵn sàng mở rộng loại giao dịch hoặc phương thức tính lãi mà không cần sửa đổi code hiện hữu nhờ trừu tượng hóa `BaseTransaction` và `InterestCalculationStrategy`.
  - **LSP:** Mọi lớp con `IncomeTransaction`, `ExpenseTransaction`, `LendOperation`... đều thay thế hoàn hảo cho lớp cha.
  - **ISP:** Tách nhỏ các interface: `WalletImpactable` chỉ cho biến động số dư, `ValuableAsset` chỉ cho định giá tài sản.
  - **DIP:** Tách tầng Domain thuần túy khỏi GUI và tầng lưu trữ.
