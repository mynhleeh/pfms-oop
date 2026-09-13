# Mục lục tài liệu dự án

Tài liệu này tổng hợp toàn bộ tài liệu của dự án Quản lý Tài chính Cá nhân (PFMS).

Các tài liệu thiết kế, báo cáo môn học, dữ liệu nghiệp vụ và sách tham khảo được thống kê tại đây để tiện tra cứu khi phát triển hệ thống.

## 1. Cấu trúc thư mục tài liệu

Thư mục `docs/` gồm bốn phần chính:

```
docs/
├── INDEX.md                                  # Mục lục tài liệu tổng hợp
├── academic/                                 # Yêu cầu đề bài và tiêu chí đánh giá môn học
├── domain/                                   # Dữ liệu mẫu và phân tích nghiệp vụ tài chính
├── deliverables/                             # Báo cáo và sản phẩm kỹ thuật của đồ án
│   ├── requirements/                         # Đặc tả yêu cầu phần mềm và kịch bản ca sử dụng
│   └── milestones/                           # Báo cáo kỹ thuật theo từng giai đoạn (OOA, OOD)
└── reference/                                # Tài liệu kỹ thuật chuẩn và giáo trình tham khảo
    ├── requirements-engineering/             # Chuẩn kỹ nghệ yêu cầu (IREB, ISO/IEC/IEEE 29148)
    ├── software-design/                      # Thiết kế hướng đối tượng, UML và mẫu thiết kế
    └── financial-patterns/                   # Mẫu thiết kế phần mềm kế toán và tài chính
```

## 2. Tài liệu học thuật và dữ liệu nghiệp vụ

### 2.1. Yêu cầu môn học (`academic/`)
* [assignment-requirement.pdf](./academic/assignment-requirement.pdf): Đề bài môn học, gồm yêu cầu về lập trình hướng đối tượng (đóng gói, kế thừa, đa hình, trừu tượng), giao diện đồ họa, xử lý dữ liệu và thang điểm đánh giá.

### 2.2. Nghiệp vụ gốc (`domain/`)
* [finance-mng.xlsm](./domain/finance-mng.xlsm): Bảng tính Excel mẫu theo dõi tài chính cá nhân gồm 10 sheet: Dashboard, Monthly, Accounts, Income, Expenses, Bills, Subscriptions, Savings, Investments, Debts.
* [finance-mng-description.md](./domain/finance-mng-description.md): Thuyết minh chi tiết các sheet trong file Excel, bao gồm danh mục thu chi, điều kiện hợp lệ của dữ liệu và các công thức tính toán (số dư lũy kế, tỷ lệ tiết kiệm, phân bổ ngân sách).

## 3. Báo cáo và sản phẩm đồ án

| Sản phẩm | Tập tin | Nội dung chính | Giai đoạn |
|---|---|---|---|
| Yêu cầu và ca sử dụng | [req-uc.md](./deliverables/requirements/req-uc.md) ([PDF](./deliverables/requirements/req-uc.pdf), [LaTeX](./deliverables/requirements/req-uc.tex)) | Đặc tả phạm vi hệ thống, danh sách tác nhân, 10 ca sử dụng chi tiết (luồng sự kiện, ngoại lệ, tiền điều kiện và hậu điều kiện). | Milestone 1 và xuyên suốt dự án |
| Báo cáo OOA | [milestone-2-ooa.md](./deliverables/milestones/milestone-2-ooa.md) | Mô hình miền (Domain Model), thẻ CRC (Class-Responsibility-Collaboration), các thuộc tính và ranh giới trách nhiệm của từng lớp. | Milestone 2 |
| Báo cáo OOD và UML | [milestone-3-uml-design.md](./deliverables/milestones/milestone-3-uml-design.md) | Kiến trúc phân tầng (Model-Service-Repository), sơ đồ lớp, sơ đồ tuần tự cùng các mẫu thiết kế GRASP và GoF áp dụng trong hệ thống. | Milestone 3 |

## 4. Tài liệu tham khảo

Thư mục `reference/` tập hợp các tài liệu chuẩn và giáo trình tham khảo dùng trong quá trình phân tích, thiết kế hệ thống.

| Chuyên đề | Tài liệu | Tác giả / Tổ chức | Mục đích sử dụng |
|---|---|---|---|
| Kỹ nghệ yêu cầu | [ireb-cpre-foundation-handbook.pdf](./reference/requirements-engineering/ireb-cpre-foundation-handbook.pdf) | IREB | Phương pháp xác định, ghi nhận và kiểm chứng yêu cầu phần mềm. |
| Kỹ nghệ yêu cầu | [ireb-cpre-glossary.pdf](./reference/requirements-engineering/ireb-cpre-glossary.pdf) | IREB | Thống nhất thuật ngữ kỹ nghệ yêu cầu (Actor, Scenario, Precondition, Quality Requirement) cho tài liệu [req-uc.md](./deliverables/requirements/req-uc.md). |
| Kỹ nghệ yêu cầu | [ireb-cpre-modeling-handbook.pdf](./reference/requirements-engineering/ireb-cpre-modeling-handbook.pdf) | IREB | Hướng dẫn vẽ sơ đồ Use Case và State Machine cho vòng đời giao dịch, hóa đơn và khoản nợ. |
| Kỹ nghệ yêu cầu | [srs-template-iso-29148.md](./reference/requirements-engineering/srs-template-iso-29148.md) và [.pdf](./reference/requirements-engineering/srs-template-iso-29148.pdf) | ISO/IEC/IEEE | Mẫu đặc tả yêu cầu phần mềm theo chuẩn ISO/IEC/IEEE 29148. |
| Thiết kế phần mềm | [applying-uml-and-patterns-3rd-edition.pdf](./reference/software-design/applying-uml-and-patterns-3rd-edition.pdf) | Craig Larman | Giáo trình phân tích và thiết kế hướng đối tượng (OOAD):<br>- Chương 9: Mô hình miền (Domain Model).<br>- Chương 16 đến 17: 9 nguyên tắc phân bổ trách nhiệm GRASP (Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations).<br>- Chương 26: Các mẫu thiết kế GoF (Strategy, Factory, Observer). |
| Mẫu thiết kế tài chính | [accounting-patterns-martin-fowler.pdf](./reference/financial-patterns/accounting-patterns-martin-fowler.pdf) | Martin Fowler | Mẫu thiết kế phần mềm kế toán và tài chính:<br>- Mô hình Account, Entry, Transaction nhằm giữ tính nhất quán số dư.<br>- Nguyên tắc ghi sổ kép (Double-Entry) khi chuyển khoản và cập nhật số dư giữa các tài khoản (tiền mặt, ngân hàng, tiết kiệm, nợ). |

## 5. Quy định mở rộng và đóng góp tài liệu

Để tài liệu dự án luôn nhất quán và dễ tìm kiếm:

1. Đặt tên file:
   - Dùng chữ thường, nối bằng dấu gạch ngang theo kiểu `kebab-case.ext`.
   - Không dùng dấu cách hoặc ký tự đặc biệt trong tên file và thư mục.
2. Phân loại tài liệu mới:
   - Sách và tài liệu tham khảo lưu vào thư mục con phù hợp trong `docs/reference/` (`requirements-engineering/`, `software-design/`, `financial-patterns/`, hoặc tạo thư mục con mới nếu có chủ đề mới).
   - Khi thêm tài liệu mới, bổ sung một dòng tương ứng vào bảng ở mục 4 của file này.
3. Liên kết:
   - Dùng liên kết Markdown chuẩn với đường dẫn tương đối (ví dụ: `[file.ext](./thu-muc/file.ext)`) để có thể mở file trực tiếp trong IDE hoặc trình duyệt Git.
