# Software Requirements Specification
## Hệ thống Quản lý Tài chính Cá nhân (Personal Finance Management System)

**Version:** 1.0  
**Ngày tạo:** 24/08/2026  
**Ngày sửa đổi:** 24/08/2026  
**Tác giả:** Nhóm phát triển  
**Tổ chức:** Cá nhân / Dự án học thuật  
**Trạng thái:** Bản nháp (Draft)

---

## Mục lục
<!-- TOC -->
* [1. Giới thiệu](#1-giới-thiệu)
    * [1.1 Mục đích tài liệu](#11-mục-đích-tài-liệu)
    * [1.2 Phạm vi sản phẩm](#12-phạm-vi-sản-phẩm)
    * [1.3 Định nghĩa, viết tắt và thuật ngữ](#13-định-nghĩa-viết-tắt-và-thuật-ngữ)
    * [1.4 Tổng quan tài liệu](#14-tổng-quan-tài-liệu)
* [2. Tổng quan sản phẩm](#2-tổng-quan-sản-phẩm)
    * [2.1 Góc nhìn sản phẩm](#21-góc-nhìn-sản-phẩm)
    * [2.2 Chức năng sản phẩm](#22-chức-năng-sản-phẩm)
    * [2.3 Ràng buộc sản phẩm](#23-ràng-buộc-sản-phẩm)
    * [2.4 Đặc điểm người dùng](#24-đặc-điểm-người-dùng)
    * [2.5 Giả định và phụ thuộc](#25-giả-định-và-phụ-thuộc)
    * [2.6 Phân bổ yêu cầu](#26-phân-bổ-yêu-cầu)
* [3. Yêu cầu](#3-yêu-cầu)
    * [3.1 Giao diện bên ngoài](#31-giao-diện-bên-ngoài)
    * [3.2 Yêu cầu chức năng](#32-yêu-cầu-chức-năng)
    * [3.3 Chất lượng dịch vụ](#33-chất-lượng-dịch-vụ)
    * [3.4 Tuân thủ (Compliance)](#34-tuân-thủ-compliance)
    * [3.5 Thiết kế và triển khai](#35-thiết-kế-và-triển-khai)
* [4. Xác minh](#4-xác-minh)
* [5. Phụ lục](#5-phụ-lục)
<!-- TOC -->

---

## 1. Giới thiệu


Tài liệu này đặc tả các yêu cầu phần mềm cho **Hệ thống Quản lý Tài chính Cá nhân** — một ứng dụng desktop giúp người dùng cá nhân theo dõi, phân loại và thống kê dòng tiền thu chi hàng ngày, quản lý số dư các ví/tài khoản, và trực quan hóa tình hình tài chính thông qua dashboard tổng quan. Tài liệu phục vụ nhiều đối tượng: nhóm phát triển (thiết kế, lập trình, kiểm thử), giảng viên hướng dẫn (đánh giá tiến độ), và người dùng cuối (tham chiếu chức năng).

### 1.1 Mục đích tài liệu


Tài liệu Software Requirements Specification (SRS) này xác định **hệ thống phải làm gì** (chức năng, ràng buộc, chất lượng) chứ không phải **hệ thống làm như thế nào** (thiết kế chi tiết, công nghệ cụ thể). Mục đích chính:

- Làm cơ sở thống nhất giữa các thành viên trong nhóm phát triển về phạm vi và yêu cầu của sản phẩm.
- Làm căn cứ để thiết kế kiến trúc, lập trình, kiểm thử và đánh giá chất lượng.
- Làm tài liệu tham chiếu cho giảng viên hướng dẫn và người đánh giá dự án.
- Làm cơ sở để phân bổ công việc, lập kế hoạch và theo dõi tiến độ.

**Đối tượng sử dụng:**

| Đối tượng | Cách sử dụng |
|-----------|--------------|
| Nhóm phát triển | Tham chiếu khi thiết kế, lập trình, kiểm thử |
| Giảng viên / Người đánh giá | Đánh giá phạm vi, chất lượng yêu cầu, tiến độ |
| Người dùng cuối (tham khảo) | Hiểu rõ hệ thống có thể làm gì, không làm gì |

**Tài liệu liên quan:** Tài liệu này có thể tham chiếu đến tài liệu kiến trúc phần mềm, tài liệu thiết kế chi tiết (UML), và kế hoạch kiểm thử khi các tài liệu đó được tạo.

### 1.2 Phạm vi sản phẩm


**Tên sản phẩm:** Hệ thống Quản lý Tài chính Cá nhân (Personal Finance Management System)  
**Phiên bản:** 1.0 (MVP - Sản phẩm khả thi tối thiểu)

**Mục đích chính:** Cung cấp cho người dùng cá nhân một công cụ desktop để ghi chép, phân loại và thống kê dòng tiền thu chi, theo dõi số dư các ví/tài khoản, và trực quan hóa tình hình tài chính cá nhân thông qua dashboard tổng quan với các biểu đồ trực quan.

**Năng lực cốt lõi (MVP):**

1. Ghi chép và phân loại giao dịch thu/chi theo danh mục 3 cấp (loại → nhóm → danh mục chi tiết).
2. Thống kê số dư hiện tại của các ví/tài khoản (tiền mặt, ngân hàng, ví điện tử).
3. Thống kê lịch sử giao dịch thu/chi với khả năng lọc, sắp xếp, tìm kiếm.
4. Thực hiện chuyển tiền nội bộ giữa các ví (không ảnh hưởng đến tổng thu/chi).
5. Hiển thị dashboard tổng quan với biểu đồ trực quan (cơ cấu tài sản, dòng tiền, thu/chi theo danh mục).

**Năng lực mở rộng (Phase 2 — không thuộc MVP):**

- Quản lý công nợ (cho vay / đi vay / thu nợ / trả nợ / xóa nợ).
- Quản lý sổ tiết kiệm (có kỳ hạn / không kỳ hạn, tính lãi).
- Quản lý chứng chỉ quỹ đầu tư (cổ phiếu / trái phiếu / cân bằng).
- Phân bổ tiền vào các quỹ mục tiêu cá nhân (khẩn cấp, sức khỏe, mục tiêu, hưu trí).

**Phạm vi bao gồm (In-scope):**

- Ứng dụng desktop chạy trên hệ điều hành Windows (ưu tiên), macOS và Linux.
- Lưu trữ dữ liệu cục bộ trên máy tính của người dùng (offline-first).
- Một người dùng duy nhất (single-user), không có xác thực đăng nhập.
- Tiền tệ: Đồng Việt Nam (VND) trong giai đoạn MVP.

**Phạm vi không bao gồm (Out-of-scope):**

- Đồng bộ dữ liệu lên cloud, đa thiết bị.
- Hỗ trợ nhiều người dùng, phân quyền, chia sẻ dữ liệu.
- Đa tiền tệ và tỷ giá hối đoái.

### 1.3 Định nghĩa, viết tắt và thuật ngữ


| Thuật ngữ / Viết tắt | Định nghĩa |
|----------------------|------------|
| **API** | Application Programming Interface — Giao diện lập trình ứng dụng, tập hợp các định nghĩa và giao thức để xây dựng và tích hợp phần mềm |
| **CRUD** | Create, Read, Update, Delete — Bốn thao tác cơ bản trên dữ liệu |
| **Dashboard** | Bảng điều khiển tổng quan, hiển thị các chỉ số và biểu đồ quan trọng |
| **DB** | Database — Cơ sở dữ liệu |
| **GUI** | Graphical User Interface — Giao diện đồ họa người dùng |
| **IDE** | Integrated Development Environment — Môi trường phát triển tích hợp |
| **JavaFX** | Framework GUI cho Java, thay thế Swing, hỗ trợ CSS và FXML |
| **JDBC** | Java Database Connectivity — API kết nối cơ sở dữ liệu trong Java |
| **JPA** | Java Persistence API — Đặc tả ORM cho Java |
| **MVP** | Minimum Viable Product — Sản phẩm khả thi tối thiểu |
| **OOP** | Object-Oriented Programming — Lập trình hướng đối tượng |
| **ORM** | Object-Relational Mapping — Ánh xạ đối tượng - quan hệ |
| **PoC** | Proof of Concept — Bằng chứng khả thi |
| **Repository** | Mẫu thiết kế (Design Pattern) trừu tượng hóa lớp truy cập dữ liệu |
| **SRS** | Software Requirements Specification — Tài liệu đặc tả yêu cầu phần mềm |
| **UI** | User Interface — Giao diện người dùng |
| **UX** | User Experience — Trải nghiệm người dùng |
| **VND** | Đồng Việt Nam — Đơn vị tiền tệ |
| **Ví** | Tài khoản tài chính của người dùng (tiền mặt, tài khoản ngân hàng, ví điện tử) |
| **Giao dịch thu** | Giao dịch tăng tiền trong ví (income) |
| **Giao dịch chi** | Giao dịch giảm tiền trong ví (expense) |
| **Chuyển khoản nội bộ** | Di chuyển tiền giữa các ví, không ảnh hưởng tổng tài sản |
| **Danh mục** | Phân loại chi tiết cho giao dịch thu/chi (category) |
| **Nhóm danh mục** | Tập hợp các danh mục cùng loại (group) |
| **Loại giao dịch** | Phân loại cấp cao nhất: thu (income) hoặc chi (expense) |
| **Số dư ví** | Tổng số tiền hiện có trong một ví tại một thời điểm |
| **Dòng tiền (Cash flow)** | Chênh lệch giữa tổng thu và tổng chi trong một khoảng thời gian |
| **Tỷ trọng (%)** | Phần trăm đóng góp của một thành phần trong tổng thể |

### 1.4 Tổng quan tài liệu


Tài liệu này được tổ chức thành 5 phần chính:

- **Phần 1 — Giới thiệu:** Cung cấp bối cảnh, mục đích, phạm vi và thuật ngữ của dự án.
- **Phần 2 — Tổng quan sản phẩm:** Mô tả góc nhìn tổng thể, chức năng chính, ràng buộc, đặc điểm người dùng, giả định và cách phân bổ yêu cầu giữa các giai đoạn.
- **Phần 3 — Yêu cầu:** Phần chi tiết nhất, đặc tả các yêu cầu chức năng, phi chức năng (chất lượng dịch vụ), tuân thủ, và ràng buộc thiết kế/triển khai. Mỗi yêu cầu có mã định danh duy nhất ([AREA]-[NNN]).
- **Phần 4 — Xác minh:** Mô tả cách thức kiểm chứng từng yêu cầu (phương pháp, công cụ, tiêu chí đạt).
- **Phần 5 — Phụ lục:** Từ vựng bổ sung, từ điển dữ liệu, sơ đồ ER, dữ liệu mẫu.

**Quy ước tài liệu:**

- Mỗi yêu cầu có mã định danh duy nhất theo định dạng `[AREA]-[NNN]` (ví dụ: `FUNC-001`).
- Từ khóa **"phải" (shall)** thể hiện yêu cầu bắt buộc; **"nên" (should)** thể hiện khuyến nghị; **"có thể" (may)** thể hiện tùy chọn.
- Mọi yêu cầu đều có tiêu chí chấp nhận (Acceptance Criteria) và phương pháp xác minh (Verification Method).

---

## 2. Tổng quan sản phẩm


### 2.1 Góc nhìn sản phẩm


**Bối cảnh và nguồn gốc:**

Hệ thống Quản lý Tài chính Cá nhân là một sản phẩm phần mềm **mới**, được phát triển từ đầu như một bài tập lớn môn Lập trình Hướng đối tượng (OOP), đồng thời hướng đến tính ứng dụng thực tế trong công việc và đời sống cá nhân. Sản phẩm không phải là bản thay thế hay phiên bản cải tiến của một hệ thống có sẵn, mà là một sản phẩm độc lập.

**Vị trí trong hệ thống lớn hơn:**

Sản phẩm hoạt động **độc lập**, không tích hợp trực tiếp với hệ thống bên ngoài nào trong giai đoạn MVP. Tuy nhiên, kiến trúc phần mềm được thiết kế theo hướng **mở rộng được**, cho phép trong tương lai:

- Chuyển đổi từ cơ sở dữ liệu cục bộ (SQLite/H2) sang cơ sở dữ liệu máy chủ (PostgreSQL/MySQL) mà không cần thay đổi logic nghiệp vụ.
- Tích hợp với các dịch vụ bên ngoài thông qua API (ví dụ: API ngân hàng, dịch vụ tỷ giá).

**Sơ đồ bối cảnh cấp cao:**

```
┌─────────────────────────────────────────────┐
│           Người dùng cá nhân                │
│              (Single-user)                  │
└──────────────────┬──────────────────────────┘
                   │ Tương tác trực tiếp
                   ▼
┌─────────────────────────────────────────────┐
│   Hệ thống Quản lý Tài chính Cá nhân       │
│   (Ứng dụng Desktop - JavaFX/Swing)        │
│                                             │
│   ┌─────────────┐    ┌──────────────────┐  │
│   │  Giao diện  │◄──►│  Logic nghiệp vụ │  │
│   │  (UI Layer) │    │  (Service Layer) │  │
│   └─────────────┘    └────────┬─────────┘  │
│                               │             │
│                    ┌──────────▼─────────┐  │
│                    │  Truy cập dữ liệu  │  │
│                    │ (Repository Layer) │  │
│                    └──────────┬─────────┘  │
└───────────────────────────────┼─────────────┘
                                │
                                ▼
                   ┌────────────────────────┐
                   │  Cơ sở dữ liệu cục bộ │
                   │     (SQLite / H2)      │
                   └────────────────────────┘
```

**Mô hình sở hữu và hỗ trợ:**

- **Sở hữu:** Nhóm phát triển (cá nhân/tổ chức tự quản lý).
- **Hỗ trợ:** Không có hỗ trợ chính thức trong giai đoạn MVP; người dùng tự chịu trách nhiệm về dữ liệu và sao lưu.
- **SLA:** Không áp dụng (sản phẩm chạy cục bộ, không có dịch vụ trực tuyến).

### 2.2 Chức năng sản phẩm


Các chức năng chính của sản phẩm được nhóm thành 5 nhóm nghiệp vụ lớn:

**Nhóm 1 — Quản lý danh mục và ví**

- Quản lý danh sách ví/tài khoản (thêm, sửa, xóa, xem số dư).
- Quản lý danh mục phân loại giao dịch (3 cấp: loại → nhóm → danh mục chi tiết).

**Nhóm 2 — Ghi chép giao dịch**

- Ghi chép giao dịch thu (income) với số tiền, ngày, ví, danh mục, ghi chú.
- Ghi chép giao dịch chi (expense) với số tiền, ngày, ví, danh mục, ghi chú.
- Chỉnh sửa và xóa giao dịch đã ghi.
- Thực hiện chuyển khoản nội bộ giữa các ví (không ảnh hưởng tổng thu/chi).

**Nhóm 3 — Thống kê và tra cứu**

- Xem số dư hiện tại của từng ví và tổng số dư toàn bộ ví.
- Xem lịch sử giao dịch thu/chi với bộ lọc (theo ngày, theo ví, theo danh mục, theo loại).
- Tìm kiếm giao dịch theo từ khóa (ghi chú, danh mục).
- Sắp xếp giao dịch theo các tiêu chí (ngày, số tiền, danh mục).

**Nhóm 4 — Dashboard và trực quan hóa**

- Hiển thị tổng quan tài sản (tổng số dư các ví).
- Hiển thị biểu đồ cơ cấu tài sản theo ví (dạng donut/pie chart).
- Hiển thị biểu đồ dòng tiền (cash flow) theo tháng (dạng line chart).
- Hiển thị biểu đồ thu/chi theo nhóm và danh mục (dạng bar chart).
- Hiển thị thống kê 12 tháng gần nhất (thu, chi, dòng tiền, tỷ lệ tiết kiệm).

**Nhóm 5 — Quản lý dữ liệu**

- Sao lưu (backup) dữ liệu cục bộ.
- Khôi phục (restore) dữ liệu từ bản sao lưu.
- Nhập dữ liệu thô từ file CSV/JSON (chỉ dữ liệu cá nhân, không bao gồm báo cáo/biểu đồ).
- Xuất dữ liệu thô ra file CSV/JSON (chỉ dữ liệu cá nhân, không bao gồm báo cáo/biểu đồ).


### 2.3 Ràng buộc sản phẩm


Các ràng buộc sau đây là **bắt buộc** và ảnh hưởng trực tiếp đến quyết định thiết kế:

**Ràng buộc về nền tảng:**

| Mã | Ràng buộc | Mức độ |
|----|-----------|--------|
| CONST-001 | Hệ thống phải là ứng dụng desktop chạy trên hệ điều hành Windows 10 trở lên. | Bắt buộc |
| CONST-002 | Hệ thống nên hỗ trợ thêm macOS 11+ và Linux (Ubuntu 20.04+) trong giai đoạn mở rộng. | Khuyến nghị |
| CONST-003 | Hệ thống phải chạy hoàn toàn offline, không yêu cầu kết nối Internet. | Bắt buộc |

**Ràng buộc về công nghệ:**

| Mã | Ràng buộc | Mức độ |
|----|-----------|--------|
| CONST-004 | Hệ thống phải được phát triển bằng ngôn ngữ Java (phiên bản 17 trở lên). | Bắt buộc |
| CONST-005 | Hệ thống phải sử dụng JavaFX hoặc Swing làm framework GUI. | Bắt buộc |
| CONST-006 | Hệ thống phải sử dụng cơ sở dữ liệu nhúng cục bộ (SQLite hoặc H2). | Bắt buộc |
| CONST-007 | Kiến trúc phải hỗ trợ khả năng chuyển đổi sang cơ sở dữ liệu máy chủ (PostgreSQL/MySQL) trong tương lai mà không cần thay đổi logic nghiệp vụ. | Bắt buộc |
| CONST-008 | Hệ thống phải áp dụng các nguyên lý OOP (đóng gói, kế thừa, đa hình, trừu tượng) và nguyên lý SOLID. | Bắt buộc |
| CONST-009 | Hệ thống phải sử dụng ít nhất 3 Design Patterns phù hợp (ví dụ: Repository, Factory, Observer, Strategy). | Bắt buộc |

**Ràng buộc về phạm vi người dùng:**

| Mã | Ràng buộc | Mức độ |
|----|-----------|--------|
| CONST-010 | Hệ thống phải hỗ trợ một người dùng duy nhất (single-user), không có cơ chế đăng nhập/đăng ký. | Bắt buộc |
| CONST-011 | Hệ thống phải lưu trữ dữ liệu cục bộ trên máy tính của người dùng. | Bắt buộc |

**Ràng buộc về tiền tệ và ngôn ngữ:**

| Mã | Ràng buộc | Mức độ |
|----|-----------|--------|
| CONST-012 | Hệ thống phải hỗ trợ tiền tệ Đồng Việt Nam (VND) trong giai đoạn MVP. | Bắt buộc |
| CONST-013 | Giao diện người dùng phải hiển thị bằng tiếng Anh và sử dụng tiếng Anh là ngôn ngữ chính. | Bắt buộc |

**Ràng buộc về chức năng:**

| Mã | Ràng buộc | Mức độ |
|----|-----------|--------|
| CONST-014 | Hệ thống chỉ được phép nhập/xuất dữ liệu thô (transactions, wallets, categories) dưới định dạng CSV/JSON. | Bắt buộc |

**Ràng buộc về phần cứng tối thiểu:**

| Mã | Ràng buộc | Mức độ |
|----|-----------|--------|
| CONST-015 | Hệ thống phải chạy được trên máy tính có RAM tối thiểu 4GB, dung lượng đĩa trống tối thiểu 500MB. | Bắt buộc |


### 2.4 Đặc điểm người dùng


**Nhóm người dùng chính:**

| Nhóm | Mô tả | Trình độ kỹ thuật | Tần suất sử dụng | Mục tiêu |
|------|-------|-------------------|------------------|----------|
| **Người dùng cá nhân** | Chủ sở hữu duy nhất của hệ thống, sử dụng để quản lý tài chính cá nhân | Trung bình (biết sử dụng máy tính cơ bản) | Hàng ngày | Theo dõi thu chi, kiểm soát tài chính |

**Đặc điểm chi tiết của người dùng:**

- **Độ tuổi:** Từ 18 trở lên, có thu nhập và chi tiêu cá nhân.
- **Trình độ kỹ thuật:** Có khả năng sử dụng máy tính cơ bản (cài đặt phần mềm, nhập liệu, sử dụng chuột/bàn phím). Không yêu cầu kiến thức lập trình.
- **Ngôn ngữ:** Tiếng Anh là ngôn ngữ chính của giao diện người dùng.
- **Khả năng tiếp cận:** Cần giao diện trực quan, dễ sử dụng, có hướng dẫn rõ ràng.
- **Mục tiêu sử dụng:**
  - Ghi chép nhanh các giao dịch thu chi hàng ngày.
  - Xem tổng quan tình hình tài chính cá nhân.
  - Phân tích thói quen chi tiêu để điều chỉnh.
  - Theo dõi số dư các ví/tài khoản.

**Nhóm người dùng phụ (không tương tác trực tiếp):**

| Nhóm | Mô tả |
|------|-------|
| **Giảng viên / Người đánh giá** | Xem xét sản phẩm để đánh giá chất lượng kỹ thuật và thiết kế |
| **Nhà phát triển tương lai** | Người sẽ bảo trì, mở rộng hệ thống sau khi bàn giao |

**Yêu cầu về khả năng tiếp cận (Accessibility):**

- Giao diện phải có độ tương phản màu sắc đủ rõ để người dùng có thị lực kém có thể đọc được.
- Kích thước font chữ tối thiểu 12pt cho văn bản thông thường, 14pt cho tiêu đề.
- Các nút bấm phải có kích thước tối thiểu 32x32 pixel để dễ thao tác.
- Hỗ trợ điều hướng bằng bàn phím cho các thao tác chính.

### 2.5 Giả định và phụ thuộc


**Giả định (Assumptions):**

| Mã | Giả định | Tác động nếu sai |
|----|----------|------------------|
| ASSUMP-001 | Người dùng có máy tính cá nhân chạy Windows 10+ và có quyền cài đặt phần mềm. | Nếu sai, cần hỗ trợ thêm nền tảng hoặc thay đổi phương thức phân phối. |
| ASSUMP-002 | Người dùng có kiến thức cơ bản về quản lý tài chính cá nhân (thu chi, ví, danh mục). | Nếu sai, cần bổ sung tài liệu hướng dẫn chi tiết hơn. |
| ASSUMP-003 | Người dùng chấp nhận lưu trữ dữ liệu tài chính cục bộ trên máy tính cá nhân. | Nếu sai, cần thay đổi kiến trúc sang cloud-based. |
| ASSUMP-004 | Người dùng tự chịu trách nhiệm sao lưu dữ liệu định kỳ. | Nếu sai, cần bổ sung tính năng sao lưu tự động. |
| ASSUMP-005 | Môi trường Java Runtime Environment (JRE) 17+ đã được cài đặt hoặc sẽ được cài đặt cùng ứng dụng. | Nếu sai, cần đóng gói JRE vào bộ cài đặt. |
| ASSUMP-006 | Người dùng sử dụng đơn vị tiền tệ VND và không cần chuyển đổi tỷ giá. | Nếu sai, cần bổ sung hỗ trợ đa tiền tệ. |

**Phụ thuộc (Dependencies):**

| Mã | Phụ thuộc | Tác động nếu không có |
|----|-----------|----------------------|
| DEPN-001 | Phụ thuộc vào JDK 17+ (hoặc JRE 17+ cho môi trường chạy). | Ứng dụng không thể khởi động. |
| DEPN-002 | Phụ thuộc vào thư viện JavaFX (OpenJFX) cho giao diện. | Giao diện không thể hiển thị. |
| DEPN-003 | Phụ thuộc vào thư viện biểu đồ (ví dụ: JFreeChart, JavaFX Charts). | Biểu đồ không thể hiển thị. |
| DEPN-004 | Phụ thuộc vào driver JDBC cho SQLite hoặc H2. | Không thể kết nối cơ sở dữ liệu. |
| DEPN-005 | Phụ thuộc vào hệ điều hành hỗ trợ Java (Windows, macOS, Linux). | Ứng dụng không thể chạy. |

### 2.6 Phân bổ yêu cầu


Dự án được chia thành **2 giai đoạn** phát triển chính:

**Giai đoạn 1 — MVP (Sản phẩm khả thi tối thiểu):**

| Nhóm chức năng | Mô tả | Mã yêu cầu (tham chiếu) |
|----------------|-------|-------------------------|
| Quản lý danh mục | CRUD danh mục phân loại 3 cấp | FUNC-001 đến FUNC-005 |
| Quản lý ví | CRUD ví/tài khoản, xem số dư | FUNC-006 đến FUNC-010 |
| Ghi chép giao dịch thu/chi | CRUD giao dịch thu, chi | FUNC-011 đến FUNC-018 |
| Chuyển khoản nội bộ | Chuyển tiền giữa các ví | FUNC-019 đến FUNC-022 |
| Thống kê lịch sử | Lọc, sắp xếp, tìm kiếm giao dịch | FUNC-023 đến FUNC-028 |
| Dashboard tổng quan | Biểu đồ, thống kê tổng hợp | FUNC-029 đến FUNC-035 |
| Quản lý dữ liệu | Backup, restore, import/export dữ liệu thô | FUNC-036 đến FUNC-040 |

**Giai đoạn 2 — Mở rộng (Extended):**

| Nhóm chức năng | Mô tả | Trạng thái |
|----------------|-------|------------|
| Quản lý công nợ | Cho vay, đi vay, thu nợ, trả nợ, xóa nợ | Hoãn (Deferred) |
| Quản lý sổ tiết kiệm | Sổ tiết kiệm có kỳ hạn/không kỳ hạn, tính lãi | Hoãn (Deferred) |
| Quản lý chứng chỉ quỹ | Đầu tư cổ phiếu, trái phiếu, quỹ cân bằng | Hoãn (Deferred) |
| Phân bổ quỹ mục tiêu | Quỹ khẩn cấp, sức khỏe, mục tiêu, hưu trí | Hoãn (Deferred) |
| Hỗ trợ đa nền tảng | macOS, Linux | Hoãn (Deferred) |
| Hỗ trợ đa tiền tệ | Ngoài VND | Hoãn (Deferred) |

**Ma trận phân bổ theo thành phần:**

| Thành phần | MVP | Phase 2 |
|------------|-----|---------|
| Module Ví (Wallet) | ✅ | ✅ |
| Module Giao dịch (Transaction) | ✅ | ✅ |
| Module Chuyển khoản (Transfer) | ✅ | ✅ |
| Module Danh mục (Category) | ✅ | ✅ |
| Module Dashboard | ✅ | ✅ |
| Module Công nợ (Debt) | ❌ | ✅ |
| Module Tiết kiệm (Saving) | ❌ | ✅ |
| Module Chứng chỉ quỹ (Fund Certificate) | ❌ | ✅ |
| Module Quỹ mục tiêu (Goal Fund) | ❌ | ✅ |

**Ghi chú:**

- Các yêu cầu trong Giai đoạn 2 được đặc tả sơ bộ trong Phụ lục (Phần 5) nhưng **không thuộc phạm vi MVP**.
- Kiến trúc phần mềm phải được thiết kế để có thể bổ sung các module Giai đoạn 2 mà không ảnh hưởng đến các module MVP hiện có.
- Việc ưu tiên Giai đoạn 2 sẽ được đánh giá lại sau khi MVP hoàn thành và nhận phản hồi từ người dùng.

---

## 3. Yêu cầu


Mỗi yêu cầu được trình bày theo mẫu chuẩn:

```markdown
- ID: [AREA]-[NNN]
- Tiêu đề: Mô tả ngắn gọn...
- Mô tả: Hệ thống phải...
- Lý do: ...
- Tiêu chí chấp nhận: ...
- Phương pháp xác minh: Test | Phân tích | Kiểm tra | Demo | Khác
- Thông tin thêm: Ngữ cảnh bổ sung, liên kết đến tài liệu liên quan.
```

**Sơ đồ mã yêu cầu (ID) và khả năng truy vết:**

- Định dạng ID: `[AREA]-[NNN]`, trong đó AREA thuộc tập `{FUNC, INT, PERF, SEC, REL, AVAIL, OBS, COMP, INST, BUILD, DIST, MAINT, REUSE, PORT, COST, DEAD, POC, CM}`.
- Tính duy nhất: ID phải duy nhất và bất biến.
- Khả năng truy vết: Mỗi test case (kiểm thử) có thể tham chiếu đến ID yêu cầu tương ứng.


### 3.1 Giao diện bên ngoài


#### 3.1.1 Giao diện người dùng (User Interfaces)


**Nguyên tắc chung:**

- Giao diện phải tuân thủ nguyên tắc **nhất quán** (consistency): cùng một hành động phải có cùng cách thể hiện ở mọi nơi.
- Giao diện phải tuân thủ nguyên tắc **phản hồi** (feedback): mọi hành động của người dùng phải có phản hồi trực quan (thông báo, hiệu ứng, thay đổi trạng thái).
- Giao diện phải tuân thủ nguyên tắc **khả phục hồi** (recoverability): cho phép hoàn tác (undo) các thao tác nhập liệu.
- Giao diện phải tuân thủ nguyên tắc **dễ học** (learnability): người dùng mới có thể sử dụng được sau thời gian làm quen ngắn.

**Các thành phần giao diện chính:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| INT-001 | Hệ thống phải cung cấp **cửa sổ chính** (Main Window) với menu bar, thanh công cụ và vùng nội dung chính. |
| INT-002 | Hệ thống phải cung cấp **bảng điều khiển bên** (Sidebar) để điều hướng giữa các chức năng: Dashboard, Giao dịch, Ví, Danh mục, Cài đặt. |
| INT-003 | Hệ thống phải cung cấp **form nhập liệu** (Input Form) cho giao dịch thu/chi với các trường: ngày, số tiền, ví, loại, nhóm, danh mục, ghi chú. |
| INT-004 | Hệ thống phải cung cấp **bảng dữ liệu** (Data Table) hiển thị danh sách giao dịch với khả năng sắp xếp theo cột. |
| INT-005 | Hệ thống phải cung cấp **bộ lọc** (Filter Panel) cho phép lọc giao dịch theo: khoảng ngày, ví, loại, nhóm, danh mục. |
| INT-006 | Hệ thống phải cung cấp **hộp thoại xác nhận** (Confirmation Dialog) trước khi thực hiện các thao tác xóa hoặc không thể hoàn tác. |
| INT-007 | Hệ thống phải cung cấp **thông báo lỗi** (Error Notification) rõ ràng khi thao tác thất bại, kèm hướng dẫn khắc phục. |
| INT-008 | Hệ thống phải cung cấp **thông báo thành công** (Success Notification) sau khi thao tác hoàn tất. |
| INT-009 | Hệ thống phải cung cấp **biểu đồ trực quan** (Charts) trên Dashboard: donut chart (cơ cấu tài sản), line chart (dòng tiền), bar chart (thu/chi theo danh mục). |
| INT-010 | Hệ thống phải hỗ trợ **phím tắt** (Keyboard Shortcuts) cho các thao tác thường dùng: Ctrl+N (thêm giao dịch mới), Ctrl+S (lưu), Ctrl+F (tìm kiếm), Esc (đóng hộp thoại). |

**Tiêu chuẩn giao diện:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| INT-011 | Giao diện phải sử dụng **font chữ** rõ ràng, dễ đọc (ví dụ: Segoe UI, Roboto, hoặc font hệ thống). |
| INT-012 | Giao diện phải sử dụng **bảng màu** nhất quán, với độ tương phản đủ cao để đảm bảo khả năng đọc. |
| INT-013 | Giao diện phải hỗ trợ **định dạng số tiền** theo chuẩn Việt Nam (dấu phân cách hàng nghìn, đơn vị VND). |
| INT-014 | Giao diện phải hỗ trợ **định dạng ngày tháng** theo chuẩn Việt Nam (dd/MM/yyyy). |
| INT-015 | Giao diện phải có **kích thước cửa sổ tối thiểu** 1024x768 pixel và hỗ trợ thay đổi kích thước. |

**Trạng thái giao diện:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| INT-016 | Hệ thống phải hiển thị **trạng thái rỗng** (Empty State) thân thiện khi chưa có dữ liệu (ví dụ: "Chưa có giao dịch nào. Nhấn 'Thêm' để bắt đầu."). |
| INT-017 | Hệ thống phải hiển thị **trạng thái đang tải** (Loading State) khi thực hiện thao tác mất thời gian (ví dụ: khi mở file database lớn). |
| INT-018 | Hệ thống phải hiển thị **trạng thái lỗi** (Error State) rõ ràng khi không thể thực hiện thao tác. |

#### 3.1.2 Giao diện phần cứng (Hardware Interfaces)


Hệ thống **không tương tác trực tiếp** với phần cứng chuyên dụng nào ngoài các thiết bị nhập liệu chuẩn của máy tính:

| Mã yêu cầu | Mô tả |
|------------|-------|
| INT-019 | Hệ thống phải hỗ trợ nhập liệu qua **bàn phím** (keyboard) cho tất cả các trường nhập liệu. |
| INT-020 | Hệ thống phải hỗ trợ nhập liệu qua **chuột** (mouse) cho tất cả các thao tác chọn, kéo-thả, nhấp. |
| INT-021 | Hệ thống nên hỗ trợ **bàn phím số** (numpad) để nhập số tiền nhanh hơn. |
| INT-022 | Hệ thống phải sử dụng **màn hình hiển thị** với độ phân giải tối thiểu 1024x768 pixel. |

#### 3.1.3 Giao diện phần mềm (Software Interfaces)


**Hệ điều hành:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| INT-023 | Hệ thống phải tương thích với **Windows 10/11** (64-bit). |
| INT-024 | Hệ thống nên tương thích với **macOS 11+** và **Linux Ubuntu 20.04+** (giai đoạn mở rộng). |

**Java Runtime Environment:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| INT-025 | Hệ thống phải yêu cầu **JRE 17 trở lên** để chạy. |
| INT-026 | Hệ thống phải sử dụng **JavaFX 17+** (OpenJFX) làm framework GUI. |

**Cơ sở dữ liệu:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| INT-027 | Hệ thống phải sử dụng **SQLite 3.x** hoặc **H2 2.x** làm cơ sở dữ liệu nhúng cục bộ. |
| INT-028 | Hệ thống phải kết nối cơ sở dữ liệu thông qua **JDBC** (Java Database Connectivity). |
| INT-029 | Hệ thống phải lưu trữ file cơ sở dữ liệu tại **thư mục dữ liệu ứng dụng** chuẩn của hệ điều hành (ví dụ: `%APPDATA%/FinanceManagement/` trên Windows). |

**Thư viện bên thứ ba:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| INT-030 | Hệ thống phải sử dụng thư viện **biểu đồ** (ví dụ: JavaFX Charts, JFreeChart) để hiển thị biểu đồ trên Dashboard. |
| INT-031 | Hệ thống nên sử dụng thư viện **logging** (ví dụ: SLF4J + Logback) để ghi log hệ thống. |
| INT-032 | Hệ thống nên sử dụng thư viện **kiểm thử** (JUnit 5, Mockito) cho unit test và integration test. |

**Định dạng file dữ liệu:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| INT-033 | Hệ thống phải hỗ trợ **nhập dữ liệu** từ file CSV (Comma-Separated Values) với encoding UTF-8. |
| INT-034 | Hệ thống phải hỗ trợ **xuất dữ liệu** ra file CSV với encoding UTF-8. |
| INT-035 | Hệ thống nên hỗ trợ nhập/xuất dữ liệu từ/sang định dạng **JSON** (JavaScript Object Notation). |

### 3.2 Yêu cầu chức năng


Các yêu cầu chức năng được tổ chức theo **nhóm nghiệp vụ**, mỗi nhóm mô tả một miền chức năng độc lập. Mỗi yêu cầu bao gồm: mô tả, lý do, tiêu chí chấp nhận, và phương pháp xác minh.

#### 3.2.1 Quản lý danh mục phân loại


**Mô tả nghiệp vụ:**

Hệ thống phân loại giao dịch thành 3 cấp:

- **Loại (Type):** Cấp cao nhất, gồm 2 giá trị: `thu` (income) và `chi` (expense).
- **Nhóm (Group):** Cấp trung gian, phụ thuộc vào Loại. Ví dụ với Loại = `thu`: Nhóm = `employment`, `assets`, `other`. Ví dụ với Loại = `chi`: Nhóm = `essentials`, `personal & leisure`, `obligations & transfers`, `savings & investing`.
- **Danh mục (Category):** Cấp chi tiết nhất, phụ thuộc vào Nhóm. Ví dụ: Nhóm = `essentials` → Danh mục = `ăn uống`, `đi lại`, `nhà ở`, `điện nước`, ...

**Yêu cầu chi tiết:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| FUNC-001 | **Xem danh sách danh mục:** Hệ thống phải hiển thị danh sách tất cả danh mục hiện có, phân loại theo Loại → Nhóm → Danh mục. |
| FUNC-002 | **Thêm danh mục mới:** Hệ thống phải cho phép thêm danh mục mới với các thông tin: tên danh mục, nhóm cha, loại cha. Tên danh mục phải là duy nhất trong cùng nhóm cha. |
| FUNC-003 | **Sửa danh mục:** Hệ thống phải cho phép chỉnh sửa tên danh mục đã có. Không cho phép thay đổi nhóm cha hoặc loại cha (để đảm bảo tính nhất quán dữ liệu). |
| FUNC-004 | **Xóa danh mục:** Hệ thống phải cho phép xóa danh mục khi không có giao dịch nào tham chiếu đến. Nếu có giao dịch tham chiếu, hệ thống phải cảnh báo và yêu cầu xác nhận. |
| FUNC-005 | **Khởi tạo danh mục mặc định:** Khi cài đặt lần đầu, hệ thống phải tự động tạo bộ danh mục mặc định cho cả thu và chi, bao gồm các nhóm và danh mục phổ biến. |

**Tiêu chí chấp nhận chung:**

- Người dùng có thể thực hiện đầy đủ thao tác CRUD trên danh mục.
- Hệ thống validate dữ liệu đầu vào (tên không rỗng, không trùng lặp).
- Hệ thống hiển thị thông báo lỗi rõ ràng khi thao tác thất bại.
- Hệ thống cập nhật danh sách dropdown danh mục ngay lập tức sau khi thêm/sửa/xóa.

**Phương pháp xác minh:** Test (Unit Test + Integration Test), Demo.

#### 3.2.2 Quản lý ví/tài khoản


**Mô tả nghiệp vụ:**

Ví (Wallet) là nơi lưu trữ tiền của người dùng. Mỗi ví có một tên gọi và số dư hiện tại. Số dư ví được tính toán tự động dựa trên:

- Tổng các giao dịch thu ghi trên ví đó.
- Tổng các giao dịch chi ghi trên ví đó (số âm).
- Tổng các chuyển khoản đi từ ví đó (số âm).
- Tổng các chuyển khoản đến ví đó (số dương).

**Các loại ví được hỗ trợ:**

- **Tiền mặt** (Cash): Ví tiền mặt vật lý.
- **Tài khoản ngân hàng** (Bank Account): Tài khoản tại ngân hàng.
- **Ví điện tử** (E-Wallet): Ví điện tử trực tuyến.

**Yêu cầu chi tiết:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| FUNC-006 | **Xem danh sách ví:** Hệ thống phải hiển thị danh sách tất cả ví hiện có, kèm số dư hiện tại và tỷ trọng (%) trên tổng tài sản. |
| FUNC-007 | **Thêm ví mới:** Hệ thống phải cho phép thêm ví mới với các thông tin: tên ví, loại ví (tiền mặt/ngân hàng/ví điện tử), số dư ban đầu, mô tả (tùy chọn). Tên ví phải là duy nhất. |
| FUNC-008 | **Sửa thông tin ví:** Hệ thống phải cho phép chỉnh sửa tên ví, loại ví, mô tả. Không cho phép chỉnh sửa số dư ban đầu (số dư được tính tự động từ giao dịch). |
| FUNC-009 | **Xóa ví:** Hệ thống phải cho phép xóa ví khi không có giao dịch nào tham chiếu đến. Nếu có giao dịch tham chiếu, hệ thống phải cảnh báo và yêu cầu xác nhận. |
| FUNC-010 | **Xem chi tiết ví:** Hệ thống phải cho phép xem chi tiết một ví, bao gồm: thông tin ví, số dư hiện tại, lịch sử giao dịch gần nhất trên ví đó. |

**Tiêu chí chấp nhận chung:**

- Người dùng có thể thực hiện đầy đủ thao tác CRUD trên ví.
- Số dư ví được tính toán chính xác và cập nhật theo thời gian thực khi có giao dịch mới.
- Tỷ trọng (%) của mỗi ví được tính chính xác trên tổng số dư tất cả ví.
- Hệ thống validate dữ liệu đầu vào (tên không rỗng, không trùng lặp, số dư ban đầu ≥ 0).

**Phương pháp xác minh:** Test (Unit Test + Integration Test), Demo.

#### 3.2.3 Ghi chép giao dịch thu/chi


**Mô tả nghiệp vụ:**

Giao dịch thu/chi là bản ghi về một khoản tiền vào (thu) hoặc ra (chi) khỏi một ví cụ thể. Mỗi giao dịch có:

- **Ngày giao dịch:** Ngày phát sinh giao dịch.
- **Số tiền:** Số tiền giao dịch (luôn dương, loại giao dịch quyết định thu hay chi).
- **Ví:** Ví thực hiện giao dịch.
- **Loại:** `thu` hoặc `chi`.
- **Nhóm:** Nhóm danh mục (phụ thuộc Loại).
- **Danh mục:** Danh mục chi tiết (phụ thuộc Nhóm).
- **Ghi chú:** Mô tả bổ sung (tùy chọn).

**Yêu cầu chi tiết:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| FUNC-011 | **Thêm giao dịch thu:** Hệ thống phải cho phép thêm giao dịch thu với đầy đủ thông tin: ngày, số tiền (> 0), ví, loại = `thu`, nhóm, danh mục, ghi chú. |
| FUNC-012 | **Thêm giao dịch chi:** Hệ thống phải cho phép thêm giao dịch chi với đầy đủ thông tin: ngày, số tiền (> 0), ví, loại = `chi`, nhóm, danh mục, ghi chú. |
| FUNC-013 | **Sửa giao dịch:** Hệ thống phải cho phép chỉnh sửa tất cả thông tin của giao dịch đã có. Khi sửa, số dư ví liên quan phải được cập nhật lại chính xác. |
| FUNC-014 | **Xóa giao dịch:** Hệ thống phải cho phép xóa giao dịch sau khi có xác nhận từ người dùng. Khi xóa, số dư ví liên quan phải được cập nhật lại chính xác. |
| FUNC-015 | **Xem danh sách giao dịch:** Hệ thống phải hiển thị danh sách tất cả giao dịch, mặc định sắp xếp theo ngày giảm dần (mới nhất trước). |
| FUNC-016 | **Validate danh mục theo phân cấp:** Khi nhập giao dịch, hệ thống phải đảm bảo danh mục thuộc nhóm đã chọn, và nhóm thuộc loại đã chọn. Dropdown danh mục phải được lọc theo nhóm đã chọn. |
| FUNC-017 | **Validate số tiền:** Hệ thống phải đảm bảo số tiền giao dịch > 0 và là số hợp lệ. |
| FUNC-018 | **Validate ngày giao dịch:** Hệ thống phải đảm bảo ngày giao dịch là ngày hợp lệ và không vượt quá ngày hiện tại (tùy chọn, có thể cấu hình). |

**Tiêu chí chấp nhận chung:**

- Người dùng có thể thực hiện đầy đủ thao tác CRUD trên giao dịch.
- Số dư ví được cập nhật chính xác ngay sau khi thêm/sửa/xóa giao dịch.
- Hệ thống validate đầy đủ dữ liệu đầu vào.
- Dropdown danh mục hoạt động đúng theo phân cấp Loại → Nhóm → Danh mục.
- Hệ thống hỗ trợ nhập liệu nhanh (ví dụ: chọn ngày mặc định là ngày hiện tại).

**Phương pháp xác minh:** Test (Unit Test + Integration Test), Demo.

#### 3.2.4 Chuyển khoản nội bộ giữa các ví


**Mô tả nghiệp vụ:**

Chuyển khoản nội bộ là thao tác di chuyển tiền từ ví nguồn sang ví đích. Đặc điểm:

- **Không ảnh hưởng đến tổng tài sản:** Tổng số dư tất cả ví không thay đổi.
- **Không được tính là thu/chi:** Chuyển khoản nội bộ không xuất hiện trong thống kê thu/chi.
- **Có thể có phí chuyển khoản:** Tùy chọn, người dùng có thể ghi nhận phí (sẽ được tính là giao dịch chi riêng).

**Yêu cầu chi tiết:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| FUNC-019 | **Thực hiện chuyển khoản:** Hệ thống phải cho phép thực hiện chuyển khoản với các thông tin: ngày, số tiền (> 0), ví nguồn, ví đích, ghi chú. Ví nguồn và ví đích phải khác nhau. |
| FUNC-020 | **Validate số dư ví nguồn:** Hệ thống phải đảm bảo số dư ví nguồn đủ để thực hiện chuyển khoản. Nếu không đủ, hiển thị thông báo lỗi rõ ràng. |
| FUNC-021 | **Cập nhật số dư:** Khi chuyển khoản thành công, số dư ví nguồn giảm và số dư ví đích tăng đúng bằng số tiền chuyển. |
| FUNC-022 | **Xem lịch sử chuyển khoản:** Hệ thống phải cho phép xem lịch sử các giao dịch chuyển khoản, có thể lọc theo ví nguồn, ví đích, khoảng ngày. |

**Tiêu chí chấp nhận chung:**

- Chuyển khoản được thực hiện thành công với đầy đủ thông tin.
- Số dư ví nguồn và ví đích được cập nhật chính xác.
- Tổng tài sản không thay đổi sau chuyển khoản.
- Chuyển khoản không xuất hiện trong thống kê thu/chi.
- Hệ thống validate đầy đủ (số tiền > 0, ví nguồn ≠ ví đích, số dư đủ).

**Phương pháp xác minh:** Test (Unit Test + Integration Test), Demo.

#### 3.2.5 Thống kê số dư ví


**Mô tả nghiệp vụ:**

Số dư ví là số tiền hiện có trong ví tại một thời điểm, được tính toán tự động từ các giao dịch. Hệ thống cung cấp các chế độ xem:

- **Số dư từng ví:** Danh sách từng ví với số dư hiện tại.
- **Tổng số dư:** Tổng số tiền trong tất cả ví.
- **Tỷ trọng:** Phần trăm đóng góp của mỗi ví trong tổng tài sản.

**Yêu cầu chi tiết:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| FUNC-023 | **Xem số dư từng ví:** Hệ thống phải hiển thị số dư hiện tại của từng ví, cập nhật theo thời gian thực. |
| FUNC-024 | **Xem tổng số dư:** Hệ thống phải hiển thị tổng số dư của tất cả ví. |
| FUNC-025 | **Xem tỷ trọng ví:** Hệ thống phải hiển thị tỷ trọng (%) của mỗi ví trên tổng tài sản. |
| FUNC-026 | **Xem số dư tại thời điểm bất kỳ:** Hệ thống nên cho phép xem số dư ví tại một thời điểm trong quá khứ (tính từ các giao dịch đến thời điểm đó). |

**Tiêu chí chấp nhận chung:**

- Số dư được tính toán chính xác dựa trên tất cả giao dịch (thu, chi, chuyển khoản).
- Số dư được cập nhật ngay khi có thay đổi (thêm/sửa/xóa giao dịch).
- Tỷ trọng được tính chính xác, tổng tỷ trọng = 100%.

**Phương pháp xác minh:** Test (Unit Test + Integration Test), Demo.

#### 3.2.6 Thống kê lịch sử thu/chi


**Mô tả nghiệp vụ:**

Hệ thống cung cấp khả năng tra cứu lịch sử giao dịch với nhiều tiêu chí lọc, sắp xếp và tìm kiếm. Hỗ trợ người dùng phân tích thói quen thu chi.

**Yêu cầu chi tiết:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| FUNC-027 | **Lọc giao dịch theo khoảng ngày:** Hệ thống phải cho phép lọc giao dịch trong một khoảng ngày (từ ngày - đến ngày). |
| FUNC-028 | **Lọc giao dịch theo ví:** Hệ thống phải cho phép lọc giao dịch theo một hoặc nhiều ví cụ thể. |
| FUNC-029 | **Lọc giao dịch theo loại:** Hệ thống phải cho phép lọc giao dịch theo loại (chỉ thu, chỉ chi, hoặc cả hai). |
| FUNC-030 | **Lọc giao dịch theo nhóm/danh mục:** Hệ thống phải cho phép lọc giao dịch theo nhóm hoặc danh mục cụ thể. |
| FUNC-031 | **Tìm kiếm giao dịch:** Hệ thống phải cho phép tìm kiếm giao dịch theo từ khóa trong ghi chú hoặc tên danh mục. |
| FUNC-032 | **Sắp xếp giao dịch:** Hệ thống phải cho phép sắp xếp danh sách giao dịch theo: ngày (tăng/giảm), số tiền (tăng/giảm), danh mục. |
| FUNC-033 | **Thống kê tổng hợp:** Hệ thống phải hiển thị tổng thu, tổng chi, dòng tiền (cash flow) trong khoảng thời gian đang lọc. |
| FUNC-034 | **Phân trang:** Khi danh sách giao dịch lớn (> 100 dòng), hệ thống phải hỗ trợ phân trang để đảm bảo hiệu năng. |
| FUNC-035 | **Xem chi tiết giao dịch:** Hệ thống phải cho phép xem chi tiết đầy đủ thông tin của một giao dịch khi nhấp vào. |

**Tiêu chí chấp nhận chung:**

- Bộ lọc hoạt động chính xác, kết hợp được nhiều tiêu chí.
- Tìm kiếm trả về kết quả chính xác, hỗ trợ tìm kiếm gần đúng (contains).
- Sắp xếp hoạt động đúng theo tiêu chí đã chọn.
- Thống kê tổng hợp được tính toán chính xác.
- Phân trang hoạt động mượt mà với danh sách lớn.

**Phương pháp xác minh:** Test (Unit Test + Integration Test), Demo.

#### 3.2.7 Dashboard tổng quan


**Mô tả nghiệp vụ:**

Dashboard là màn hình chính khi mở ứng dụng, cung cấp cái nhìn tổng quan về tình hình tài chính thông qua các biểu đồ và chỉ số quan trọng.

**Yêu cầu chi tiết:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| FUNC-036 | **Hiển thị tổng tài sản:** Dashboard phải hiển thị tổng số dư hiện tại của tất cả ví. |
| FUNC-037 | **Biểu đồ cơ cấu tài sản:** Dashboard phải hiển thị biểu đồ tròn (donut/pie chart) thể hiện cơ cấu tài sản theo ví. |
| FUNC-038 | **Biểu đồ dòng tiền:** Dashboard phải hiển thị biểu đồ đường (line chart) thể hiện dòng tiền (thu - chi) theo tháng trong 12 tháng gần nhất. |
| FUNC-039 | **Biểu đồ thu theo danh mục:** Dashboard phải hiển thị biểu đồ cột (bar chart) thể hiện tổng thu theo danh mục trong tháng hiện tại. |
| FUNC-040 | **Biểu đồ chi theo danh mục:** Dashboard phải hiển thị biểu đồ cột (bar chart) thể hiện tổng chi theo danh mục trong tháng hiện tại. |
| FUNC-041 | **Biểu đồ thu/chi theo nhóm:** Dashboard phải hiển thị biểu đồ cột so sánh thu và chi theo nhóm trong tháng hiện tại. |
| FUNC-042 | **Bảng thống kê 12 tháng:** Dashboard phải hiển thị bảng thống kê 12 tháng gần nhất với các cột: tháng, tổng thu, tổng chi, dòng tiền, tỷ lệ tiết kiệm (%). |
| FUNC-043 | **Làm mới dữ liệu:** Dashboard phải tự động làm mới dữ liệu khi có giao dịch mới được thêm/sửa/xóa. |

**Tiêu chí chấp nhận chung:**

- Tất cả biểu đồ hiển thị chính xác, cập nhật theo thời gian thực.
- Biểu đồ có chú thích (legend), nhãn dữ liệu (label), và tooltip khi di chuột.
- Bảng thống kê 12 tháng tự động cuộn theo tháng hiện tại.
- Dashboard tải nhanh (< 2 giây với dữ liệu 1000 giao dịch).

**Phương pháp xác minh:** Test (Unit Test + Integration Test), Demo.

#### 3.2.8 Quản lý dữ liệu (Backup, Restore, Import/Export)


**Mô tả nghiệp vụ:**

Hệ thống cung cấp các chức năng quản lý dữ liệu để đảm bảo an toàn và khả năng di chuyển dữ liệu. Lưu ý: chỉ thao tác với **dữ liệu thô** (transactions, wallets, categories), không bao gồm báo cáo/biểu đồ.

**Yêu cầu chi tiết:**

| Mã yêu cầu | Mô tả |
|------------|-------|
| FUNC-044 | **Sao lưu dữ liệu:** Hệ thống phải cho phép sao lưu toàn bộ cơ sở dữ liệu ra file backup (định dạng SQLite hoặc định dạng nén riêng). Người dùng chọn vị trí lưu file backup. |
| FUNC-045 | **Khôi phục dữ liệu:** Hệ thống phải cho phép khôi phục dữ liệu từ file backup. Trước khi khôi phục, hệ thống phải cảnh báo và yêu cầu xác nhận (dữ liệu hiện tại sẽ bị ghi đè). |
| FUNC-046 | **Sao lưu tự động:** Hệ thống nên tự động sao lưu dữ liệu định kỳ (ví dụ: mỗi khi đóng ứng dụng hoặc mỗi ngày). |
| FUNC-047 | **Nhập dữ liệu từ CSV:** Hệ thống phải cho phép nhập dữ liệu giao dịch từ file CSV. Hệ thống phải validate định dạng file và hiển thị báo cáo số bản ghi nhập thành công/thất bại. |
| FUNC-048 | **Xuất dữ liệu ra CSV:** Hệ thống phải cho phép xuất dữ liệu giao dịch ra file CSV. Người dùng có thể chọn xuất tất cả hoặc theo bộ lọc. |
| FUNC-049 | **Nhập/Xuất dữ liệu JSON:** Hệ thống nên hỗ trợ nhập/xuất dữ liệu từ/sang định dạng JSON (transactions, wallets, categories). |
| FUNC-050 | **Xóa toàn bộ dữ liệu:** Hệ thống phải cho phép xóa toàn bộ dữ liệu (reset về trạng thái ban đầu) sau khi có xác nhận rõ ràng từ người dùng (nhập mã xác nhận hoặc xác nhận 2 bước). |

**Tiêu chí chấp nhận chung:**

- Sao lưu và khôi phục hoạt động chính xác, dữ liệu sau khôi phục giống hệt dữ liệu gốc.
- Nhập/xuất CSV/JSON hoạt động đúng định dạng, encoding UTF-8.
- Hệ thống validate dữ liệu khi nhập, báo cáo rõ lỗi nếu có.
- Các thao tác nguy hiểm (xóa dữ liệu, khôi phục) yêu cầu xác nhận rõ ràng.

**Phương pháp xác minh:** Test (Unit Test + Integration Test), Demo.

### 3.3 Chất lượng dịch vụ


#### 3.3.1 Hiệu năng (Performance)


| Mã yêu cầu | Mô tả |
|------------|-------|
| PERF-001 | **Thời gian khởi động:** Ứng dụng phải khởi động và sẵn sàng sử dụng trong vòng **3 giây** trên máy tính cấu hình trung bình (CPU Intel i5 thế hệ 8+, RAM 8GB, SSD). |
| PERF-002 | **Thời gian tải Dashboard:** Dashboard phải tải và hiển thị đầy đủ biểu đồ trong vòng **2 giây** với dữ liệu 1000 giao dịch. |
| PERF-003 | **Thời gian phản hồi thao tác CRUD:** Các thao tác thêm/sửa/xóa giao dịch, ví, danh mục phải hoàn tất trong vòng **500ms**. |
| PERF-004 | **Thời gian lọc/tìm kiếm:** Thao tác lọc và tìm kiếm giao dịch phải hoàn tất trong vòng **1 giây** với dữ liệu 10.000 giao dịch. |
| PERF-005 | **Sử dụng bộ nhớ:** Ứng dụng phải sử dụng không quá **512MB RAM** khi hoạt động bình thường với 10.000 giao dịch. |
| PERF-006 | **Dung lượng database:** Cơ sở dữ liệu phải hỗ trợ lưu trữ ít nhất **50.000 giao dịch** mà vẫn đảm bảo hiệu năng. |
| PERF-007 | **Kích thước file cài đặt:** Bộ cài đặt ứng dụng phải có kích thước không quá **200MB** (bao gồm JRE nếu đóng gói). |

**Phương pháp đo lường:** Sử dụng profiler (JProfiler, VisualVM), benchmark scripts, đo thời gian thực tế trên máy tính cấu hình chuẩn.

#### 3.3.2 Bảo mật (Security)


| Mã yêu cầu | Mô tả |
|------------|-------|
| SEC-001 | **Bảo vệ dữ liệu cục bộ:** Hệ thống phải lưu trữ dữ liệu tài chính tại vị trí an toàn trên máy tính (thư mục dữ liệu ứng dụng chuẩn của hệ điều hành). |
| SEC-002 | **Mã hóa dữ liệu nhạy cảm:** Hệ thống nên mã hóa dữ liệu nhạy cảm (số tiền, ghi chú) trong cơ sở dữ liệu bằng thuật toán mã hóa đối xứng (ví dụ: AES-256). |
| SEC-003 | **Không gửi dữ liệu ra ngoài:** Hệ thống phải đảm bảo **không gửi bất kỳ dữ liệu nào** của người dùng ra ngoài máy tính (không có telemetry, không có analytics). |
| SEC-004 | **Xác thực thao tác nguy hiểm:** Các thao tác nguy hiểm (xóa dữ liệu, khôi phục backup, xóa ví có giao dịch) phải yêu cầu xác nhận rõ ràng từ người dùng. |
| SEC-005 | **Validate đầu vào:** Hệ thống phải validate tất cả dữ liệu đầu vào từ người dùng để ngăn chặn lỗi và các cuộc tấn công injection (SQL injection, CSV injection). |
| SEC-006 | **Ghi log bảo mật:** Hệ thống nên ghi log các sự kiện bảo mật quan trọng (đăng nhập, thay đổi cấu hình, xóa dữ liệu) để phục vụ kiểm tra. |
| SEC-007 | **Không lưu mật khẩu dạng plain text:** Nếu có tính năng bảo vệ bằng mật khẩu (ví dụ: mở ứng dụng), mật khẩu phải được hash bằng thuật toán an toàn (bcrypt, Argon2). |

**Phương pháp xác minh:** Phân tích mã nguồn (code review), kiểm thử bảo mật (penetration testing cơ bản), kiểm tra log.

#### 3.3.3 Độ tin cậy (Reliability)


| Mã yêu cầu | Mô tả |
|------------|-------|
| REL-001 | **Tính toàn vẹn dữ liệu:** Hệ thống phải đảm bảo tính toàn vẹn dữ liệu trong mọi tình huống (kể cả khi mất điện đột ngột, sập ứng dụng). Sử dụng transaction của cơ sở dữ liệu để đảm bảo ACID. |
| REL-002 | **Xử lý lỗi:** Hệ thống phải xử lý lỗi một cách graceful, không để ứng dụng bị crash. Mọi exception phải được bắt và hiển thị thông báo lỗi thân thiện. |
| REL-003 | **Tự phục hồi:** Khi gặp lỗi không mong muốn (ví dụ: file database bị khóa), hệ thống phải hiển thị thông báo rõ ràng và hướng dẫn khắc phục. |
| REL-004 | **Tần suất lỗi:** Tỷ lệ lỗi nghiêm trọng (crash, mất dữ liệu) phải dưới **0.1%** trong điều kiện sử dụng bình thường. |
| REL-005 | **Khả năng phục hồi sau lỗi:** Sau khi xảy ra lỗi, hệ thống phải có khả năng khôi phục về trạng thái ổn định mà không cần khởi động lại. |

**Phương pháp xác minh:** Test (Unit Test + Integration Test), kiểm thử stress, kiểm thử phục hồi sau lỗi.

#### 3.3.4 Khả dụng (Availability)


| Mã yêu cầu | Mô tả |
|------------|-------|
| AVAIL-001 | **Khả dụng cục bộ:** Ứng dụng phải khả dụng bất cứ khi nào người dùng muốn sử dụng (không phụ thuộc vào máy chủ bên ngoài). |
| AVAIL-002 | **Thời gian khởi động lại:** Trong trường hợp ứng dụng bị lỗi, thời gian khởi động lại và sẵn sàng sử dụng phải dưới **5 giây**. |
| AVAIL-003 | **Hoạt động offline:** Ứng dụng phải hoạt động đầy đủ chức năng khi không có kết nối Internet. |

**Phương pháp xác minh:** Test thủ công, đo thời gian khởi động.

#### 3.3.5 Quan sát (Observability)


| Mã yêu cầu | Mô tả |
|------------|-------|
| OBS-001 | **Ghi log hệ thống:** Hệ thống phải ghi log các sự kiện quan trọng (khởi động, đóng ứng dụng, lỗi, thao tác CRUD) vào file log. |
| OBS-002 | **Cấp độ log:** Hệ thống phải hỗ trợ các cấp độ log: DEBUG, INFO, WARN, ERROR. Cho phép cấu hình cấp độ log qua file cấu hình. |
| OBS-003 | **File log:** File log phải được lưu tại thư mục dữ liệu ứng dụng, có cơ chế rotation (theo ngày hoặc theo kích thước). |
| OBS-004 | **Thông tin log:** Mỗi log entry phải bao gồm: timestamp, cấp độ, message, stack trace (nếu có lỗi), context (user action). |
| OBS-005 | **Không log dữ liệu nhạy cảm:** Hệ thống phải đảm bảo không ghi log dữ liệu nhạy cảm (số tiền chi tiết, ghi chú cá nhân) trừ khi cần thiết cho debug. |

**Phương pháp xác minh:** Kiểm tra file log sau khi thực hiện các thao tác, đánh giá chất lượng thông tin log.

### 3.4 Tuân thủ (Compliance)


| Mã yêu cầu | Mô tả |
|------------|-------|
| COMP-001 | **Tuân thủ luật bảo vệ dữ liệu cá nhân:** Hệ thống phải tuân thủ các quy định pháp luật hiện hành về bảo vệ dữ liệu cá nhân tại Việt Nam (ví dụ: Nghị định 13/2023/NĐ-CP về bảo vệ dữ liệu cá nhân). |
| COMP-002 | **Quyền của người dùng:** Người dùng phải có quyền truy cập, chỉnh sửa, xóa toàn bộ dữ liệu cá nhân của mình bất cứ lúc nào. |
| COMP-003 | **Minh bạch về xử lý dữ liệu:** Hệ thống phải cung cấp tài liệu hoặc thông báo rõ ràng về cách dữ liệu được lưu trữ và xử lý (chỉ lưu cục bộ, không gửi ra ngoài). |
| COMP-004 | **Tuân thủ giấy phép phần mềm:** Hệ thống phải sử dụng các thư viện và công cụ có giấy phép phù hợp (mã nguồn mở: MIT, Apache 2.0, BSD; tránh GPL nếu không cần thiết). |
| COMP-005 | **Ghi nhận bản quyền:** Hệ thống phải ghi nhận bản quyền của các thư viện bên thứ ba được sử dụng (NOTICE file hoặc About dialog). |

**Phương pháp xác minh:** Phân tích (review giấy phép thư viện), kiểm tra (review tài liệu hướng dẫn).

### 3.5 Thiết kế và triển khai


#### 3.5.1 Cài đặt (Installation)


| Mã yêu cầu | Mô tả |
|------------|-------|
| INST-001 | **Hỗ trợ hệ điều hành:** Hệ thống phải hỗ trợ cài đặt trên Windows 10/11 (64-bit). |
| INST-002 | **Yêu cầu phần mềm nền:** Hệ thống phải yêu cầu JRE 17+ đã được cài đặt, hoặc đóng gói JRE vào bộ cài đặt. |
| INST-003 | **Trình cài đặt:** Hệ thống phải cung cấp trình cài đặt (installer) với giao diện đồ họa, hướng dẫn từng bước. |
| INST-004 | **Cài đặt không cần quyền admin:** Nếu có thể, hệ thống nên cho phép cài đặt mà không cần quyền quản trị viên (cài vào thư mục người dùng). |
| INST-005 | **Gỡ cài đặt:** Hệ thống phải hỗ trợ gỡ cài đặt sạch sẽ qua Control Panel hoặc công cụ gỡ cài đặt chuẩn. |
| INST-006 | **Cấu hình sau cài đặt:** Sau khi cài đặt, hệ thống phải tự động tạo cơ sở dữ liệu với bộ danh mục mặc định. |
| INST-007 | **Tùy chọn portable:** Hệ thống nên cung cấp phiên bản portable (chạy trực tiếp từ USB hoặc thư mục bất kỳ, không cần cài đặt). |

**Phương pháp xác minh:** Test cài đặt trên máy tính sạch, test gỡ cài đặt, test phiên bản portable.

#### 3.5.2 Build và Delivery (Build & Delivery)


| Mã yêu cầu | Mô tả |
|------------|-------|
| BUILD-001 | **Công cụ build:** Hệ thống phải sử dụng Maven hoặc Gradle làm công cụ build tự động. |
| BUILD-002 | **Build reproducible:** Quá trình build phải reproducible — cùng mã nguồn phải tạo ra cùng artifact. |
| BUILD-003 | **Quản lý phụ thuộc:** Hệ thống phải quản lý phụ thuộc (dependencies) thông qua Maven/Gradle, không commit thư viện vào repository. |
| BUILD-004 | **Phiên bản:** Hệ thống phải sử dụng semantic versioning (MAJOR.MINOR.PATCH) cho việc đánh số phiên bản. |
| BUILD-005 | **Artifact đầu ra:** Quá trình build phải tạo ra file JAR executable hoặc native installer (EXE/MSI cho Windows). |
| BUILD-006 | **Kiểm thử tự động:** Quá trình build phải chạy unit test và integration test tự động. Build chỉ thành công khi tất cả test pass. |
| BUILD-007 | **Code quality check:** Quá trình build nên tích hợp công cụ kiểm tra chất lượng mã (Checkstyle, PMD, SpotBugs). |

**Phương pháp xác minh:** Chạy build script, kiểm tra artifact đầu ra, chạy test tự động.

#### 3.5.3 Phân phối (Distribution)


| Mã yêu cầu | Mô tả |
|------------|-------|
| DIST-001 | **Kênh phân phối:** Hệ thống được phân phối qua file cài đặt (installer) hoặc file JAR/EXE tải về từ nguồn chính thức. |
| DIST-002 | **Cập nhật:** Hệ thống nên có cơ chế kiểm tra và thông báo cập nhật phiên bản mới (manual check, không tự động download). |
| DIST-003 | **Tài liệu hướng dẫn:** Hệ thống phải đi kèm tài liệu hướng dẫn cài đặt và sử dụng (README, User Guide). |

**Phương pháp xác minh:** Test quy trình phân phối, kiểm tra tài liệu đi kèm.

#### 3.5.4 Khả năng bảo trì (Maintainability)


| Mã yêu cầu | Mô tả |
|------------|-------|
| MAINT-001 | **Code coverage:** Unit test phải đạt code coverage tối thiểu **70%** cho các module nghiệp vụ cốt lõi. |
| MAINT-002 | **Coding convention:** Mã nguồn phải tuân thủ coding convention nhất quán (Google Java Style Guide hoặc tương đương). |
| MAINT-003 | **Tài liệu mã nguồn:** Các class và method công khai phải có Javadoc mô tả chức năng, tham số, giá trị trả về. |
| MAINT-004 | **Kiến trúc phân lớp:** Mã nguồn phải được tổ chức theo kiến trúc phân lớp rõ ràng (Presentation Layer, Service Layer, Repository Layer, Domain Layer). |
| MAINT-005 | **SOLID:** Mã nguồn phải tuân thủ nguyên lý SOLID. |
| MAINT-006 | **Design Patterns:** Mã nguồn phải sử dụng ít nhất 3 Design Patterns phù hợp (ví dụ: Repository, Factory, Observer, Strategy, Builder). |
| MAINT-007 | **Quản lý nợ kỹ thuật:** Các đoạn mã TODO, FIXME, hack phải được ghi nhận và có kế hoạch xử lý. |
| MAINT-008 | **Version control:** Mã nguồn phải được quản lý bằng Git, sử dụng branching strategy rõ ràng (GitFlow hoặc tương đương). |

**Phương pháp xác minh:** Phân tích mã nguồn (code review), đo code coverage, kiểm tra tài liệu.

#### 3.5.5 Khả năng tái sử dụng (Reusability)


| Mã yêu cầu | Mô tả |
|------------|-------|
| REUSE-001 | **Module hóa:** Các module nghiệp vụ (Wallet, Transaction, Category, Dashboard) phải được thiết kế để có thể tái sử dụng trong các dự án tương lai. |
| REUSE-002 | **Domain layer độc lập:** Domain layer (entities, value objects, domain services) phải độc lập với framework và công nghệ cụ thể. |
| REUSE-003 | **Repository abstraction:** Truy cập dữ liệu phải thông qua interface (Repository pattern), cho phép thay đổi implementation mà không ảnh hưởng logic nghiệp vụ. |

**Phương pháp xác minh:** Phân tích kiến trúc, kiểm tra tính độc lập của các module.

#### 3.5.6 Khả năng chuyển giao (Portability)


| Mã yêu cầu | Mô tả |
|------------|-------|
| PORT-001 | **Đa nền tảng (mục tiêu tương lai):** Kiến trúc phải hỗ trợ khả năng chuyển đổi sang macOS và Linux trong tương lai (sử dụng JavaFX thay vì WinForms/WPF). |
| PORT-002 | **Database abstraction:** Hệ thống phải hỗ trợ cả SQLite và H2, cho phép chuyển đổi qua cấu hình. |
| PORT-003 | **Cấu hình bên ngoài:** Đường dẫn file, thư mục dữ liệu phải được cấu hình qua file cấu hình, không hard-code. |

**Phương pháp xác minh:** Test trên các nền tảng khác nhau (nếu có), kiểm tra tính độc lập của cấu hình.

#### 3.5.7 Chi phí (Cost)


| Mã yêu cầu | Mô tả |
|------------|-------|
| COST-001 | **Phần mềm miễn phí:** Hệ thống phải được phân phối miễn phí cho người dùng cuối. |
| COST-002 | **Sử dụng công cụ miễn phí:** Hệ thống phải sử dụng các công cụ phát triển miễn phí (JDK, IDE miễn phí, thư viện mã nguồn mở). |
| COST-003 | **Không phụ thuộc dịch vụ trả phí:** Hệ thống không được phụ thuộc vào dịch vụ trả phí bên ngoài (cloud, API trả phí). |

**Phương pháp xác minh:** Kiểm tra giấy phép các thành phần, đánh giá chi phí vận hành.

#### 3.5.8 Thời hạn (Deadline)


| Mã yêu cầu | Mô tả |
|------------|-------|
| DEAD-001 | **Thời hạn MVP:** MVP (Giai đoạn 1) phải hoàn thành trong vòng **5-6 tháng** kể từ ngày bắt đầu dự án. |
| DEAD-002 | **Mốc tiến độ:** Dự án phải tuân thủ các mốc tiến độ theo yêu cầu môn học (Phân tích yêu cầu → Phân tích OO → Thiết kế UML → Cài đặt cốt lõi → SOLID/Refactor → Sản phẩm cuối). |
| DEAD-003 | **Ưu tiên:** Trong trường hợp thiếu thời gian, ưu tiên hoàn thành các chức năng cốt lõi (FUNC-001 đến FUNC-050) trước khi bổ sung tính năng mở rộng. |

**Phương pháp xác minh:** Theo dõi tiến độ qua project management tool, đánh giá tại các mốc.

#### 3.5.9 Proof of Concept (PoC)


| Mã yêu cầu | Mô tả |
|------------|-------|
| POC-001 | **PoC GUI:** Trước khi triển khai đầy đủ, nhóm phải xây dựng PoC giao diện JavaFX cơ bản (cửa sổ chính, menu, một form nhập liệu) để xác nhận khả thi. |
| POC-002 | **PoC Database:** Trước khi triển khai đầy đủ, nhóm phải xây dựng PoC kết nối cơ sở dữ liệu SQLite/H2, thực hiện CRUD cơ bản để xác nhận khả thi. |
| POC-003 | **PoC Biểu đồ:** Trước khi triển khai đầy đủ, nhóm phải xây dựng PoC hiển thị biểu đồ đơn giản (donut chart, line chart) để xác nhận thư viện biểu đồ hoạt động đúng. |

**Phương pháp xác minh:** Demo PoC, đánh giá kết quả.

#### 3.5.10 Quản lý thay đổi (Change Management)


| Mã yêu cầu | Mô tả |
|------------|-------|
| CM-001 | **Semantic versioning:** Hệ thống phải sử dụng semantic versioning (MAJOR.MINOR.PATCH) cho việc đánh số phiên bản. |
| CM-002 | **Changelog:** Mỗi phiên bản phát hành phải có changelog mô tả các thay đổi (thêm tính năng, sửa lỗi, thay đổi breaking). |
| CM-003 | **Backward compatibility:** Trong giai đoạn MVP, hệ thống không cam kết backward compatibility cho định dạng database. Khi thay đổi schema, hệ thống phải cung cấp migration script. |
| CM-004 | **Quy trình phê duyệt:** Mọi thay đổi mã nguồn phải thông qua code review trước khi merge vào nhánh chính. |

**Phương pháp xác minh:** Kiểm tra changelog, kiểm tra quy trình code review, kiểm tra migration script.

---

## 4. Phương pháp xác minh

Hệ thống sử dụng các phương pháp xác minh sau:

| Phương pháp | Mô tả | Áp dụng cho |
|------------|-------|-------------|
| **Test (Kiểm thử)** | Chạy test case (unit test, integration test, system test) để kiểm chứng hành vi | Yêu cầu chức năng, hiệu năng, độ tin cậy |
| **Analysis (Phân tích)** | Phân tích mã nguồn, tài liệu, hoặc thiết kế để kiểm chứng | Yêu cầu bảo mật, tuân thủ, kiến trúc |
| **Inspection (Kiểm tra)** | Kiểm tra thủ công bằng mắt (review tài liệu, code review) | Yêu cầu tài liệu, coding convention |
| **Demonstration (Demo)** | Trình diễn chức năng cho người đánh giá | Yêu cầu giao diện, trải nghiệm người dùng |


---

## 5. Phụ lục


### 5.1 Từ vựng bổ sung (Glossary)

| Thuật ngữ | Định nghĩa |
|-----------|------------|
| **ACID** | Atomicity, Consistency, Isolation, Durability — Bốn tính chất đảm bảo tính toàn vẹn của giao dịch cơ sở dữ liệu |
| **ACID Transaction** | Giao dịch cơ sở dữ liệu đảm bảo bốn tính chất ACID |
| **Aggregation** | Quan hệ giữa hai đối tượng trong đó một đối tượng chứa tham chiếu đến đối tượng khác nhưng không sở hữu hoàn toàn |
| **Association** | Quan hệ giữa hai đối tượng trong đó chúng có thể tồn tại độc lập |
| **Builder Pattern** | Design Pattern cho phép xây dựng đối tượng phức tạp theo từng bước |
| **Composition** | Quan hệ giữa hai đối tượng trong đó đối tượng chứa sở hữu hoàn toàn đối tượng được chứa |
| **CSV** | Comma-Separated Values — Định dạng file văn bản lưu trữ dữ liệu dạng bảng |
| **Dashboard** | Bảng điều khiển tổng quan hiển thị các chỉ số và biểu đồ quan trọng |
| **Donut Chart** | Biểu đồ tròn có lỗ ở giữa, dùng để thể hiện cơ cấu tỷ trọng |
| **Encapsulation** | Nguyên lý OOP: đóng gói dữ liệu và phương thức vào một đơn vị (class), hạn chế truy cập trực tiếp |
| **Factory Pattern** | Design Pattern cho phép tạo đối tượng mà không cần chỉ định class cụ thể |
| **Inheritance** | Nguyên lý OOP: class con kế thừa thuộc tính và phương thức từ class cha |
| **JDBC** | Java Database Connectivity — API chuẩn để kết nối cơ sở dữ liệu trong Java |
| **JSON** | JavaScript Object Notation — Định dạng trao đổi dữ liệu dạng văn bản |
| **Line Chart** | Biểu đồ đường, dùng để thể hiện xu hướng theo thời gian |
| **Observer Pattern** | Design Pattern cho phép đối tượng nhận thông báo khi đối tượng khác thay đổi trạng thái |
| **Polymorphism** | Nguyên lý OOP: cùng một phương thức có thể có hành vi khác nhau ở các class khác nhau |
| **Repository Pattern** | Design Pattern trừu tượng hóa lớp truy cập dữ liệu |
| **SOLID** | Năm nguyên lý thiết kế hướng đối tượng: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion |
| **Strategy Pattern** | Design Pattern cho phép thay đổi thuật toán/hành vi tại runtime |
| **UTF-8** | Mã hóa ký tự 8-bit phổ biến, hỗ trợ tiếng Việt có dấu |

### 5.2 Từ điển dữ liệu (Data Dictionary)


#### 5.2.1 Thực thể: Wallet (Ví)

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|------------|--------------|-----------|-------|
| `id` | Long | Primary Key, Auto-increment | Định danh duy nhất của ví |
| `name` | String(100) | Not Null, Unique | Tên ví (ví dụ: "Tiền mặt", "MB Bank") |
| `type` | Enum | Not Null | Loại ví: CASH, BANK_ACCOUNT, E_WALLET |
| `initialBalance` | BigDecimal | Not Null, ≥ 0 | Số dư ban đầu |
| `description` | String(500) | Nullable | Mô tả bổ sung |
| `createdAt` | LocalDateTime | Not Null | Ngày tạo |
| `updatedAt` | LocalDateTime | Not Null | Ngày cập nhật cuối |

**Quan hệ:**

- Một Wallet có nhiều Transaction (1-N).
- Một Wallet có nhiều Transfer (vai trò: ví nguồn hoặc ví đích) (1-N).

#### 5.2.2 Thực thể: Category (Danh mục)

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|------------|--------------|-----------|-------|
| `id` | Long | Primary Key, Auto-increment | Định danh duy nhất của danh mục |
| `name` | String(100) | Not Null | Tên danh mục |
| `groupId` | Long | Foreign Key → CategoryGroup, Not Null | Nhóm cha |
| `createdAt` | LocalDateTime | Not Null | Ngày tạo |
| `updatedAt` | LocalDateTime | Not Null | Ngày cập nhật cuối |

**Ràng buộc:** Tên danh mục phải duy nhất trong cùng nhóm cha.

#### 5.2.3 Thực thể: CategoryGroup (Nhóm danh mục)

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|------------|--------------|-----------|-------|
| `id` | Long | Primary Key, Auto-increment | Định danh duy nhất của nhóm |
| `name` | String(100) | Not Null | Tên nhóm |
| `typeId` | Long | Foreign Key → CategoryType, Not Null | Loại cha |
| `createdAt` | LocalDateTime | Not Null | Ngày tạo |
| `updatedAt` | LocalDateTime | Not Null | Ngày cập nhật cuối |

#### 5.2.4 Thực thể: CategoryType (Loại danh mục)

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|------------|--------------|-----------|-------|
| `id` | Long | Primary Key, Auto-increment | Định danh duy nhất của loại |
| `name` | String(50) | Not Null, Unique | Tên loại: "INCOME" hoặc "EXPENSE" |
| `displayName` | String(100) | Not Null | Tên hiển thị: "Thu" hoặc "Chi" |

#### 5.2.5 Thực thể: Transaction (Giao dịch thu/chi)

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|------------|--------------|-----------|-------|
| `id` | Long | Primary Key, Auto-increment | Định danh duy nhất của giao dịch |
| `date` | LocalDate | Not Null | Ngày giao dịch |
| `amount` | BigDecimal | Not Null, > 0 | Số tiền giao dịch |
| `walletId` | Long | Foreign Key → Wallet, Not Null | Ví thực hiện giao dịch |
| `categoryId` | Long | Foreign Key → Category, Not Null | Danh mục |
| `note` | String(500) | Nullable | Ghi chú |
| `createdAt` | LocalDateTime | Not Null | Ngày tạo |
| `updatedAt` | LocalDateTime | Not Null | Ngày cập nhật cuối |

**Quan hệ:**

- Một Transaction thuộc về một Wallet (N-1).
- Một Transaction thuộc về một Category (N-1).

#### 5.2.6 Thực thể: Transfer (Chuyển khoản nội bộ)

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|------------|--------------|-----------|-------|
| `id` | Long | Primary Key, Auto-increment | Định danh duy nhất của chuyển khoản |
| `date` | LocalDate | Not Null | Ngày chuyển khoản |
| `amount` | BigDecimal | Not Null, > 0 | Số tiền chuyển |
| `fromWalletId` | Long | Foreign Key → Wallet, Not Null | Ví nguồn |
| `toWalletId` | Long | Foreign Key → Wallet, Not Null | Ví đích |
| `note` | String(500) | Nullable | Ghi chú |
| `createdAt` | LocalDateTime | Not Null | Ngày tạo |
| `updatedAt` | LocalDateTime | Not Null | Ngày cập nhật cuối |

**Ràng buộc:** `fromWalletId` ≠ `toWalletId`.

### 5.3 Sơ đồ quan hệ thực thể (ER Diagram)


```
┌─────────────────────┐
│   CategoryType      │
│   (Loại danh mục)   │
│─────────────────────│
│ PK id               │
│    name             │
│    displayName      │
└──────────┬──────────┘
           │ 1
           │
           │ N
┌──────────▼──────────┐
│   CategoryGroup     │
│   (Nhóm danh mục)   │
│─────────────────────│
│ PK id               │
│    name             │
│ FK typeId           │
└──────────┬──────────┘
           │ 1
           │
           │ N
┌──────────▼──────────┐         ┌─────────────────────┐
│    Category         │         │      Wallet         │
│   (Danh mục)        │         │       (Ví)          │
│─────────────────────│         │─────────────────────│
│ PK id               │         │ PK id               │
│    name             │         │    name             │
│ FK groupId          │         │    type             │
└──────────┬──────────┘         │    initialBalance   │
           │ 1                  │    description      │
           │                    └──────────┬──────────┘
           │ N                             │ 1
┌──────────▼──────────┐                    │
│    Transaction      │                    │ N
│   (Giao dịch)       │                    │
│─────────────────────│         ┌──────────▼──────────┐
│ PK id               │         │     Transfer        │
│    date             │         │  (Chuyển khoản)     │
│    amount           │         │─────────────────────│
│ FK walletId         │         │ PK id               │
│ FK categoryId       │         │    date             │
│    note             │         │    amount           │
└─────────────────────┘         │ FK fromWalletId     │
                                │ FK toWalletId       │
                                │    note             │
                                └─────────────────────┘
```

**Mô tả quan hệ:**

| Quan hệ | Loại | Mô tả |
|---------|------|-------|
| CategoryType → CategoryGroup | 1-N | Một loại có nhiều nhóm |
| CategoryGroup → Category | 1-N | Một nhóm có nhiều danh mục |
| Wallet → Transaction | 1-N | Một ví có nhiều giao dịch |
| Category → Transaction | 1-N | Một danh mục có nhiều giao dịch |
| Wallet → Transfer (from) | 1-N | Một ví có nhiều chuyển khoản đi |
| Wallet → Transfer (to) | 1-N | Một ví có nhiều chuyển khoản đến |

### 5.4 Dữ liệu mẫu (Sample Data)


#### 5.4.1 Danh mục mặc định

**Loại: THU (INCOME)**

| Nhóm | Danh mục |
|------|----------|
| Employment (Việc làm) | Lương, Thưởng, Phụ cấp |
| Assets (Tài sản) | Lãi tiết kiệm, Cổ tức, Cho thuê |
| Other (Khác) | Quà tặng, Hoàn tiền, Thu nhập khác |

**Loại: CHI (EXPENSE)**

| Nhóm | Danh mục |
|------|----------|
| Essentials (Thiết yếu) | Ăn uống, Đi lại, Nhà ở, Điện nước, Y tế |
| Personal & Leisure (Cá nhân & Giải trí) | Mua sắm, Giải trí, Du lịch, Làm đẹp |
| Obligations & Transfers (Nghĩa vụ & Chuyển khoản) | Học phí, Bảo hiểm, Trả nợ, Biếu tặng |
| Savings & Investing (Tiết kiệm & Đầu tư) | Gửi tiết kiệm, Mua chứng chỉ quỹ, Đầu tư khác |

#### 5.4.2 Ví mặc định

| Tên ví | Loại | Số dư ban đầu (VND) |
|--------|------|---------------------|
| Tiền mặt | CASH | 2.000.000 |
| MB Bank | BANK_ACCOUNT | 15.000.000 |
| Timo | BANK_ACCOUNT | 5.000.000 |
| Vietcombank | BANK_ACCOUNT | 20.000.000 |
| MSB | BANK_ACCOUNT | 8.000.000 |
| ShopeePay | E_WALLET | 1.500.000 |

#### 5.4.3 Giao dịch mẫu

| Ngày | Số tiền | Ví | Loại | Nhóm | Danh mục | Ghi chú |
|------|---------|-----|------|------|----------|---------|
| 01/08/2026 | 15.000.000 | MB Bank | Thu | Employment | Lương | Lương tháng 8 |
| 02/08/2026 | 150.000 | Tiền mặt | Chi | Essentials | Ăn uống | Cơm trưa |
| 03/08/2026 | 50.000 | ShopeePay | Chi | Essentials | Đi lại | Grab |
| 05/08/2026 | 2.000.000 | MB Bank | Chi | Essentials | Nhà ở | Tiền thuê nhà |
| 10/08/2026 | 500.000 | Tiền mặt | Chi | Personal & Leisure | Giải trí | Xem phim |
| 15/08/2026 | 3.000.000 | MB Bank → Timo | Chuyển khoản | — | — | Chuyển tiết kiệm |

### 5.5 Use Case Diagram (Sơ đồ Use Case)


**Tác nhân (Actor):**

- **Người dùng (User):** Người sử dụng duy nhất của hệ thống.

**Use Case chính:**

```
                    ┌─────────────────────────────────────────────┐
                    │   Hệ thống Quản lý Tài chính Cá nhân       │
                    │                                             │
                    │   ┌─────────────────────────────────────┐   │
                    │   │ UC-01: Quản lý danh mục             │   │
                    │   │  - Xem danh sách danh mục           │   │
                    │   │  - Thêm danh mục                    │   │
                    │   │  - Sửa danh mục                     │   │
                    │   │  - Xóa danh mục                     │   │
                    │   └─────────────────────────────────────┘   │
                    │                                             │
                    │   ┌─────────────────────────────────────┐   │
                    │   │ UC-02: Quản lý ví                   │   │
                    │   │  - Xem danh sách ví                 │   │
                    │   │  - Thêm ví                          │   │
                    │   │  - Sửa ví                           │   │
                    │   │  - Xóa ví                           │   │
                    │   │  - Xem chi tiết ví                  │   │
                    │   └─────────────────────────────────────┘   │
                    │                                             │
                    │   ┌─────────────────────────────────────┐   │
                    │   │ UC-03: Ghi chép giao dịch thu/chi   │   │
                    │   │  - Thêm giao dịch thu                │   │
                    │   │  - Thêm giao dịch chi                │   │
                    │   │  - Sửa giao dịch                     │   │
                    │   │  - Xóa giao dịch                     │   │
                    │   │  - Xem danh sách giao dịch           │   │
                    │   └─────────────────────────────────────┘   │
                    │                                             │
                    │   ┌─────────────────────────────────────┐   │
                    │   │ UC-04: Chuyển khoản nội bộ          │   │
                    │   │  - Thực hiện chuyển khoản           │   │
                    │   │  - Xem lịch sử chuyển khoản         │   │
                    │   └─────────────────────────────────────┘   │
                    │                                             │
                    │   ┌─────────────────────────────────────┐   │
                    │   │ UC-05: Thống kê & Tra cứu           │   │
                    │   │  - Lọc giao dịch                    │   │
                    │   │  - Tìm kiếm giao dịch               │   │
                    │   │  - Sắp xếp giao dịch                │   │
                    │   │  - Xem số dư ví                     │   │
                    │   └─────────────────────────────────────┘   │
                    │                                             │
                    │   ┌─────────────────────────────────────┐   │
                    │   │ UC-06: Xem Dashboard                │   │
                    │   │  - Xem tổng tài sản                 │   │
                    │   │  - Xem biểu đồ                      │   │
                    │   │  - Xem thống kê 12 tháng            │   │
                    │   └─────────────────────────────────────┘   │
                    │                                             │
                    │   ┌─────────────────────────────────────┐   │
                    │   │ UC-07: Quản lý dữ liệu              │   │
                    │   │  - Sao lưu dữ liệu                  │   │
                    │   │  - Khôi phục dữ liệu                │   │
                    │   │  - Nhập dữ liệu CSV/JSON            │   │
                    │   │  - Xuất dữ liệu CSV/JSON            │   │
                    │   │  - Xóa toàn bộ dữ liệu              │   │
                    │   └─────────────────────────────────────┘   │
                    │                                             │
                    └─────────────────────────────────────────────┘
                                       ▲
                                       │
                                       │ (tương tác)
                                       │
                                ┌──────┴──────┐
                                │ Người dùng  │
                                │  (User)     │
                                └─────────────┘
```

### 5.6 Yêu cầu mở rộng (Phase 2 — Deferred)


#### 5.6.1 Quản lý công nợ (Debt Management)

**Mô tả:** Quản lý các khoản cho vay và đi vay giữa người dùng và người khác.

**Chức năng chính:**

- Ghi nhận khoản cho vay (lend) và đi vay (borrow).
- Ghi nhận thu nợ (collect) và trả nợ (repay).
- Ghi nhận xóa nợ (write-off) khi khoản nợ được miễn hoặc không thể thu.
- Thống kê tổng phải thu (receivable) và tổng phải trả (payable).
- Sao kê công nợ theo từng người.

**Thực thể chính:** Debt (date, batch, person, type, amount, wallet, note).

#### 5.6.2 Quản lý sổ tiết kiệm (Savings Management)

**Mô tả:** Quản lý các sổ tiết kiệm có kỳ hạn và không kỳ hạn.

**Chức năng chính:**

- Tạo sổ tiết kiệm với kỳ hạn (1 tháng, 3 tháng, 6 tháng, 12 tháng, ...) hoặc không kỳ hạn.
- Tính lãi suất đơn (không kỳ hạn) hoặc lãi suất theo kỳ hạn.
- Theo dõi tiến độ kỳ hạn.
- Tính số dư hiện tại (gốc + lãi tích lũy).

**Thực thể chính:** Saving (fund, principal, capital, term, rate, start, process, balance).

#### 5.6.3 Quản lý chứng chỉ quỹ (Fund Certificate Management)

**Mô tả:** Quản lý các khoản đầu tư vào chứng chỉ quỹ, cổ phiếu, trái phiếu.

**Chức năng chính:**

- Ghi nhận mua chứng chỉ quỹ (stock, bond, balance).
- Cập nhật giá trị hiện tại theo giá thị trường.
- Tính suất sinh lời quy đổi năm (annualized return) bằng hàm tài chính RATE().
- Tính lãi/lỗ (interest = balance - principal).

**Thực thể chính:** FundCertificate (fund, name, type, principal, start, rate, interest, balance).

#### 5.6.4 Phân bổ quỹ mục tiêu (Goal Fund Allocation)

**Mô tả:** Phân bổ tiền vào các quỹ mục tiêu cá nhân (khẩn cấp, sức khỏe, mục tiêu, hưu trí).

**Chức năng chính:**

- Tạo quỹ mục tiêu với số tiền mục tiêu (target).
- Phân bổ tiền mặt, tiết kiệm, chứng chỉ quỹ vào từng quỹ.
- Theo dõi tiến độ đạt mục tiêu (process = total / target).
- Trực quan hóa cơ cấu quỹ và tiến độ.

**Thực thể chính:** GoalFund (name, cash, saving, fundCert, total, target, process).

---

## Kết thúc tài liệu



