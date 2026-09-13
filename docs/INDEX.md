# Mục lục tài liệu dự án

Tài liệu này tổng hợp toàn bộ tài liệu của dự án Quản lý Tài chính Cá nhân (PFMS).

Các tài liệu thiết kế, báo cáo môn học, dữ liệu nghiệp vụ và sách tham khảo được thống kê tại đây để tiện tra cứu khi phát triển hệ thống.

## 1. Cấu trúc thư mục tài liệu

Thư mục `docs/` gồm bốn phần chính:

```
docs/
├── INDEX.md                                  # Mục lục tài liệu tổng hợp
├── academic/                                 # Yêu cầu đề bài và tiêu chí đánh giá môn học
│   └── assignment-requirement.pdf            # Đề bài và thang điểm đánh giá 6 milestone
├── domain/                                   # Dữ liệu mẫu và phân tích nghiệp vụ tài chính
│   ├── finance-mng.xlsm                      # Bảng tính Excel gốc theo dõi tài chính cá nhân
│   └── finance-mng-description.md            # Thuyết minh chi tiết các sheet, công thức và dữ liệu
├── deliverables/                             # Báo cáo và sản phẩm kỹ thuật của đồ án
│   ├── requirements/                         # Đặc tả yêu cầu phần mềm và kịch bản ca sử dụng
│   │   ├── req-uc.md                         # Đặc tả Use Case và yêu cầu chức năng (Markdown)
│   │   ├── req-uc.pdf                        # Bản xuất PDF
│   │   └── req-uc.tex                        # Bản nguồn LaTeX
│   ├── analysis/                             # Phân tích hướng đối tượng (OOA, Milestone 2)
│   │   └── framework.md                      # Khung tài liệu và quy chuẩn phân tích OOA
│   └── design/                               # Thiết kế hướng đối tượng (OOD, Milestone 3 & 5)
│       └── framework.md                      # Khung tài liệu và quy chuẩn vẽ sơ đồ UML (PlantUML)
└── reference/                                # Tài liệu kỹ thuật chuẩn và giáo trình tham khảo
    ├── requirements-engineering/             # Chuẩn kỹ nghệ yêu cầu (IREB, ISO/IEC/IEEE 29148)
    ├── software-design/                      # Thiết kế hướng đối tượng, UML và mẫu thiết kế (Craig Larman)
    └── financial-patterns/                   # Mẫu thiết kế phần mềm kế toán và tài chính (Martin Fowler)
```

## 2. Tài liệu học thuật và dữ liệu nghiệp vụ

### 2.1. Yêu cầu môn học (`academic/`)
* [assignment-requirement.pdf](./academic/assignment-requirement.pdf): Đề bài môn học, gồm yêu cầu về lập trình hướng đối tượng (đóng gói, kế thừa, đa hình, trừu tượng), giao diện đồ họa, xử lý dữ liệu và thang điểm đánh giá 6 milestone.

### 2.2. Nghiệp vụ gốc (`domain/`)
* [finance-mng.xlsm](./domain/finance-mng.xlsm): Bảng tính Excel mẫu theo dõi tài chính cá nhân gồm 10 sheet: Dashboard, Monthly, Accounts, Income, Expenses, Bills, Subscriptions, Savings, Investments, Debts.
* [finance-mng-description.md](./domain/finance-mng-description.md): Thuyết minh chi tiết các sheet trong file Excel, bao gồm danh mục thu chi, điều kiện hợp lệ của dữ liệu và các công thức tính toán (số dư lũy kế, tỷ lệ tiết kiệm, phân bổ ngân sách).

## 3. Báo cáo và sản phẩm đồ án

Toàn bộ sản phẩm kỹ thuật và báo cáo tiến độ qua các giai đoạn được lưu trữ tập trung tại thư mục `deliverables/`:

| Phân nhóm | Tập tin | Nội dung chính | Giai đoạn |
|---|---|---|---|
| Kỹ nghệ yêu cầu | [req-uc.md](./deliverables/requirements/req-uc.md) ([PDF](./deliverables/requirements/req-uc.pdf), [LaTeX](./deliverables/requirements/req-uc.tex)) | Đặc tả phạm vi hệ thống, danh sách tác nhân, 10 ca sử dụng chi tiết (luồng sự kiện, ngoại lệ, tiền điều kiện và hậu điều kiện). | Milestone 1 và xuyên suốt dự án |
| Phân tích OOA | [framework.md](./deliverables/analysis/framework.md) | Khung cấu trúc, phương pháp luận trích xuất khái niệm, thẻ CRC, quy tắc nghiệp vụ và tiêu chuẩn kiểm định cho pha OOA. | Chuẩn bị Milestone 2 |
| Thiết kế OOD | [framework.md](./deliverables/design/framework.md) | Khung cấu trúc, quy chuẩn vẽ sơ đồ UML bằng PlantUML, kiến trúc phân tầng 4 lớp, áp dụng 9 nguyên tắc GRASP, 5 nguyên lý SOLID và mẫu thiết kế GoF. | Chuẩn bị Milestone 3 & 5 |

*Ghi chú:* Các báo cáo phân tích chi tiết của từng module nghiệp vụ sẽ được lưu tại thư mục `deliverables/analysis/`; các sơ đồ lớp và tuần tự chi tiết sẽ được lưu tại thư mục `deliverables/design/`.

## 4. Tài liệu tham khảo

Thư mục `reference/` tập hợp các tài liệu chuẩn và giáo trình tham khảo dùng trong quá trình phân tích, thiết kế hệ thống.

| Chuyên đề | Tài liệu | Tác giả / Tổ chức | Mục đích sử dụng |
|---|---|---|---|
| Kỹ nghệ yêu cầu | [ireb-cpre-foundation-handbook.pdf](./reference/requirements-engineering/ireb-cpre-foundation-handbook.pdf) | IREB | Phương pháp xác định, ghi nhận và kiểm chứng yêu cầu phần mềm. |
| Kỹ nghệ yêu cầu | [ireb-cpre-glossary.pdf](./reference/requirements-engineering/ireb-cpre-glossary.pdf) | IREB | Thống nhất thuật ngữ kỹ nghệ yêu cầu (Actor, Scenario, Precondition, Quality Requirement) cho tài liệu [req-uc.md](./deliverables/requirements/req-uc.md). |
| Kỹ nghệ yêu cầu | [ireb-cpre-modeling-handbook.pdf](./reference/requirements-engineering/ireb-cpre-modeling-handbook.pdf) | IREB | Hướng dẫn vẽ sơ đồ Use Case và State Machine cho vòng đời giao dịch, hóa đơn và khoản nợ. |
| Kỹ nghệ yêu cầu | [srs-template-iso-29148.md](./reference/requirements-engineering/srs-template-iso-29148.md) và [.pdf](./reference/requirements-engineering/srs-template-iso-29148.pdf) | ISO/IEC/IEEE | Mẫu đặc tả yêu cầu phần mềm theo chuẩn ISO/IEC/IEEE 29148. |
| Thiết kế phần mềm | [applying-uml-and-patterns-3rd-edition.pdf](./reference/software-design/applying-uml-and-patterns-3rd-edition.pdf) | Craig Larman | Giáo trình phân tích và thiết kế hướng đối tượng (OOAD):<br>- Chương 9: Mô hình miền (Domain Model).<br>- Chương 15: Sơ đồ tương tác tuần tự (Sequence Diagrams).<br>- Chương 16: Sơ đồ lớp thiết kế (Design Class Diagrams).<br>- Chương 16 đến 17, 25: 9 nguyên tắc phân bổ trách nhiệm GRASP (Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations).<br>- Chương 26: Các mẫu thiết kế GoF (Strategy, Factory, Observer).<br>- Chương 34: Kiến trúc phân tầng và Sơ đồ gói (Package Diagram). |
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
