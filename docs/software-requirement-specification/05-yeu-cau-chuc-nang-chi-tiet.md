## 4. YÊU CẦU CHỨC NĂNG CHI TIẾT

Hệ thống Quản lý Tài chính Cá nhân gồm các yêu cầu chức năng, hiện thực hóa mục tiêu hệ thống (G-01, G-02, G-03) và tuân thủ các quy tắc nghiệp vụ: độ chính xác tài chính (`BR-FIN-01`), bảo toàn số dư ví (`BR-FIN-02`), ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`), phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`) và tên của ví, nhóm nhu cầu, danh mục (`BR-NAM-01`).

Mỗi yêu cầu chức năng được đặc tả theo khuôn mẫu:

1. Bảng thuộc tính yêu cầu: mục tiêu hệ thống phục vụ, ca sử dụng tương ứng theo cú pháp `UC-<DOMAIN>-NN`, quy tắc nghiệp vụ chi phối (`MUST`, `SHOULD`, `MAY`).
2. Điều kiện tiền đề: trạng thái hệ thống và dữ liệu cần có trước khi kích hoạt.
3. Dữ liệu đầu vào: tham số, kiểu dữ liệu, ràng buộc giá trị và tính bắt buộc.
4. Quy tắc xử lý: các bước xử lý tuần tự, kiểm tra hợp lệ và mã lỗi tương ứng (`ERR-*`).
5. Dữ liệu đầu ra và bất biến: kết quả ghi nhận, thông điệp phản hồi và bất biến tài chính sau xử lý.
6. Tiêu chí nghiệm thu: kịch bản Given-When-Then kèm mã `AC-FR-xxx-yy`.

**Quy ước chung:**

- Tên nhóm nhu cầu và danh mục trong tiêu chí nghiệm thu là dữ liệu giả lập, không phải quy định cứng; khi kiểm thử thay bằng nhóm và danh mục thực tế trong cấu hình triển khai.
- Các FR gắn nhãn chỉ truy vấn, không làm thay đổi số dư ví hay bất kỳ bản ghi nào, nên không nhắc lại bất biến này ở từng FR.
- Quy tắc nghiệp vụ được viện dẫn bằng mã, tên đầy đủ. Mọi số tiền dùng số thập phân chính xác, làm tròn ngân hàng, hiển thị 2 chữ số thập phân (`BR-FIN-01`). Thao tác ghi nhiều bước là nguyên tử: cùng thành công hoặc cùng hủy.
- **Quy tắc phí giao dịch (FEE)**, dùng chung cho (`FR-TXN-01`, `FR-TXN-02`, `FR-TXN-04`, `FR-TRF-01`):
  - Chi phí nhỏ hơn 0: từ chối, `ERR-FEE-001`. Chi phí mặc định 0.00 VNĐ.
  - Chi phí lớn hơn 0 bắt buộc có danh mục phí: thiếu thì (`ERR-FEE-002`); danh mục đã xóa hoặc không thuộc nhánh Chi tiêu (`EXPENSE`) thì (`ERR-FEE-003`). Chi phí bằng 0 không cần danh mục phí.
  - Chi phí lớn hơn 0 sinh một bản ghi phí loại chi tiêu, số tiền bằng đúng chi phí, liên kết với giao dịch gốc (hoặc giao dịch chuyển tiền) và ghi đồng thời với giao dịch gốc.

Bảng chia các yêu cầu chức năng theo 6 nhóm chức năng:

| STT | Nhóm chức năng | Mã nhóm | Số lượng FR |
| :-: | :-- | :-: | :-: |
| 1 | Ví | `FR-WAL` | 5 |
| 2 | Ghi nhận thu chi | `FR-TXN` | 5 |
| 3 | Chuyển tiền nội bộ | `FR-TRF` | 2 |
| 4 | Danh mục phân loại | `FR-CAT` | 3 |
| 5 | Thống kê và báo cáo | `FR-REP` | 2 |
| 6 | Lưu trữ và sao lưu dữ liệu | `FR-DAT` | 3 |
| **Tổng** | **6 nhóm chức năng** | | **20 FR** |

### 4.1 FR-WAL: Nhóm chức năng Ví

Quản lý các ví (tài khoản và nguồn tiền dùng ngay) của Người dùng. Hệ thống có sẵn ví "Tiền mặt" và cho phép tạo thêm ví. Số dư ví luôn đúng theo bảo toàn số dư ví (`BR-FIN-02`).

#### 4.1.1 FR-WAL-01: Tạo ví mới

Bảng thuộc tính của yêu cầu chức năng Tạo mới ví (FR-WAL-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`), Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`) |
| Ca sử dụng liên quan | Tạo ví mới (`UC-WAL-01`) |
| Quy tắc áp dụng | Quy tắc tên (`BR-NAM-01`), Quy tắc bảo toàn số dư ví (`BR-FIN-02`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Hệ thống đã hoàn tất khởi tạo.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Tên ví | Chuỗi ký tự | 1 đến 50 ký tự sau khi cắt khoảng trắng hai đầu, không trùng tên ví khác (`BR-NAM-01`) | Bắt buộc |
| Ghi chú mô tả | Chuỗi ký tự | Tối đa 255 ký tự | Tùy chọn |

Quy tắc xử lý:

1. Tên rỗng sau khi cắt hoặc dài hơn 50 ký tự: từ chối, (`ERR-NAM-001`).
2. Tên trùng ví đang hoạt động hoặc ví đã lưu trữ: từ chối, (`ERR-NAM-002`).
3. Tạo ví ở trạng thái hoạt động (`ACTIVE`), số dư khởi điểm 0.00 VNĐ bảo toàn số dư ví(`BR-FIN-02`); không nhận số dư ban đầu khi tạo ví. Khi khởi tạo hệ thống lần đầu, tạo sẵn đúng một ví "Tiền mặt" số dư 0.00 VNĐ.
4. Cấp mã định danh duy nhất, ghi thời điểm tạo và lưu vào tệp dữ liệu cục bộ.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Bản ghi ví mới gồm mã định danh, tên, trạng thái, thời điểm tạo và ghi chú. |
| Bất biến hệ thống | Số dư ví mới bằng 0.00 VNĐ; tổng số dư toàn hệ thống không đổi bảo toàn số dư ví(`BR-FIN-02`). |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-WAL-01.1` | Nhập tên "MB Bank" và ghi chú hợp lệ | Tạo ví `ACTIVE`, số dư 0.00 VNĐ. |
| `AC-WAL-01.2` | Nhập tên chỉ gồm khoảng trắng hoặc dài hơn 50 ký tự | Từ chối, báo (`ERR-NAM-001`). |
| `AC-WAL-01.3` | Đã có ví "MB Bank"; tạo ví "mb bank" hoặc " MB Bank " | Từ chối, báo (`ERR-NAM-002`). |

#### 4.1.2 FR-WAL-02: Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền

Bảng thuộc tính của yêu cầu chức năng Xem danh sách ví (FR-WAL-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`) |
| Ca sử dụng liên quan | Xem danh sách ví (`UC-WAL-02`) |
| Quy tắc áp dụng | Độ chính xác tài chính(`BR-FIN-01`),bảo toàn số dư ví (`BR-FIN-02`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Dữ liệu hệ thống đã nạp vào bộ nhớ làm việc.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Bộ lọc trạng thái | Mã phân loại | `ACTIVE` (mặc định), `ARCHIVED` hoặc `ALL` | Tùy chọn |

Quy tắc xử lý:

1. Lọc ví theo trạng thái; mặc định chỉ ví `ACTIVE`.
2. Với mỗi ví, tính lại số dư khả dụng từ lịch sử dòng tiền theo `BR-FIN-02`, gồm thu, chi, tiền chuyển đi, tiền nhận về và điều chỉnh số dư. Không dùng giá trị số dư gán trực tiếp.
3. Tổng số dư hệ thống bằng tổng số dư các ví `ACTIVE` và các ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ (`BR-FIN-01`).
4. Sắp xếp danh sách theo tên ví tăng dần.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Danh sách ví gồm mã, tên, trạng thái, số dư tính lại, thời điểm giao dịch gần nhất; kèm tổng số dư. |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-WAL-02.1` | Ví "Timo" có điều chỉnh số dư 1,000,000 VNĐ, thu 500,000 VNĐ, chi 200,000 VNĐ | Số dư hiển thị đúng 1,300,000.00 VNĐ (`BR-FIN-02`). |
| `AC-WAL-02.2` | Chọn bộ lọc `ACTIVE` | Chỉ hiển thị ví đang hoạt động, ẩn ví `ARCHIVED`. |
| `AC-WAL-02.3` | Tổng hợp số dư toàn hệ thống | Khớp từng đồng với tổng số dư các ví thành phần, sai số 0.00 VNĐ (`BR-FIN-01`). |

#### 4.1.3 FR-WAL-03: Cập nhật thông tin ví

Bảng thuộc tính của yêu cầu chức năng Cập nhật thông tin ví (FR-WAL-03), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên.

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`) |
| Ca sử dụng liên quan | Cập nhật thông tin ví (`UC-WAL-03`) |
| Quy tắc áp dụng | `BR-NAM-01`, `BR-FIN-02` |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Ví cần cập nhật tồn tại trong hệ thống.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Mã định danh ví | Chuỗi ký tự | Mã ví đang tồn tại | Bắt buộc |
| Tên ví mới | Chuỗi ký tự | 1 đến 50 ký tự sau khi cắt, không trùng ví khác (`BR-NAM-01`) | Tùy chọn |
| Ghi chú mô tả mới | Chuỗi ký tự | Tối đa 255 ký tự | Tùy chọn |
| Trạng thái ví | Mã phân loại | `ACTIVE` hoặc `ARCHIVED` | Tùy chọn |

Quy tắc xử lý:

1. Kiểm tra tên mới (nếu đổi) theo `BR-NAM-01`: sai định dạng thì `ERR-NAM-001`, trùng ví khác thì `ERR-NAM-002`; không đối chiếu trùng với chính ví đang sửa.
2. Lưu trữ: cấm lưu trữ ví "Tiền mặt" có sẵn. Ví khác được chuyển sang `ARCHIVED` bất kể số dư; số dư ví lưu trữ vẫn tính vào tổng số dư hệ thống (`BR-FIN-02`).
3. Xóa vĩnh viễn: chỉ khi ví chưa từng có bản ghi nào trong sổ thu chi, sổ chuyển tiền hoặc sổ điều chỉnh số dư; khi xóa, tên ví được giải phóng. Ví đã có giao dịch chỉ được chuyển sang `ARCHIVED`. Cấm xóa ví "Tiền mặt" có sẵn.
4. Không cho đổi trực tiếp số dư ví tại chức năng này (`BR-FIN-02`). Lưu thay đổi hợp lệ vào tệp dữ liệu cục bộ.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Bản ghi ví đã cập nhật tên, ghi chú hoặc trạng thái. |
| Bất biến hệ thống | Số dư và lịch sử dòng tiền của ví giữ nguyên; tổng số dư hệ thống không đổi (`BR-FIN-02`). |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-WAL-03.1` | Đổi tên ví "Vietcombank" thành "VCB Digibank" (chưa tồn tại) | Cập nhật tên, giữ nguyên số dư và lịch sử. |
| `AC-WAL-03.2` | Yêu cầu lưu trữ hoặc xóa ví "Tiền mặt" có sẵn | Từ chối, ví vẫn `ACTIVE`. |
| `AC-WAL-03.3` | Yêu cầu xóa vĩnh viễn ví đã có ít nhất một giao dịch | Từ chối, chỉ cho chuyển sang `ARCHIVED`. |

#### 4.1.4 FR-WAL-04: Thống kê cơ cấu số dư theo ví

Bảng thuộc tính của yêu cầu chức năng Thống kê cơ cấu số dư theo ví (FR-WAL-04), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`) |
| Ca sử dụng liên quan | Xem danh sách ví (`UC-WAL-02`) |
| Quy tắc áp dụng | Độ chính xác tài chính(`BR-FIN-01`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Dữ liệu đã nạp và có ít nhất một ví.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Ngày tính toán | Ngày tháng (`YYYY-MM-DD`) | Mặc định là ngày hiện tại | Tùy chọn |

Quy tắc xử lý:

1. Chọn các ví `ACTIVE` và các ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ; ví `ARCHIVED` số dư 0.00 VNĐ không đưa vào.
2. Tổng số dư bằng tổng số dư các ví được chọn (`BR-FIN-01`).
3. Tổng bằng 0.00 VNĐ: tỷ lệ mọi ví là 0.00%, không chia cho 0 (`ERR-FIN-002`).
4. Tổng lớn hơn 0: tỷ lệ bằng số dư ví chia tổng số dư nhân 100%, tính với tối thiểu 4 chữ số thập phân trung gian, làm tròn ngân hàng 2 chữ số thập phân. Ví `ARCHIVED` hiển thị kèm nhãn "đã lưu trữ".
5. Sắp xếp theo tỷ lệ giảm dần.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Bảng cơ cấu gồm tên ví, trạng thái, số dư và tỷ lệ phần trăm. |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-WAL-04.1` | Ví A 6,000,000 VNĐ, ví B 4,000,000 VNĐ | Tỷ lệ A 60.00%, B 40.00%. |
| `AC-WAL-04.2` | Mọi ví có số dư 0.00 VNĐ | Mọi tỷ lệ 0.00%, không lỗi chia cho 0 (`ERR-FIN-002`). |
| `AC-WAL-04.3` | Ví hoạt động 9,950,000 VNĐ, ví lưu trữ 50,000 VNĐ | Ví lưu trữ hiển thị dòng riêng kèm nhãn, tỷ lệ 0.50%, tổng các tỷ lệ đúng 100.00%. |

#### 4.1.5 FR-WAL-05: Điều chỉnh số dư ví

Bảng thuộc tính của yêu cầu chức năng Điều chỉnh số dư ví (FR-WAL-05), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (`G-02`) |
| Ca sử dụng liên quan | Điều chỉnh số dư ví (`UC-WAL-04`) |
| Quy tắc áp dụng | Độ chính xác tài chính(`BR-FIN-01`),bảo toàn số dư ví (`BR-FIN-02`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Ví cần điều chỉnh tồn tại và đang `ACTIVE`.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Mã định danh ví | Chuỗi ký tự | Ví đang `ACTIVE` | Bắt buộc |
| Số dư thực tế quan sát | Số tiền | Lớn hơn hoặc bằng 0.00 VNĐ (`BR-FIN-02`) | Bắt buộc |
| Lý do điều chỉnh | Chuỗi ký tự | Tối đa 255 ký tự sau khi cắt; cho phép để trống | Tùy chọn |
| Thời điểm điều chỉnh | Ngày tháng (`YYYY-MM-DD`) | Mặc định là ngày hiện tại | Tùy chọn |

Quy tắc xử lý:

1. Không tìm thấy ví hoặc ví `ARCHIVED`: từ chối.
2. Số dư quan sát âm: từ chối, `ERR-WAL-001`; sai định dạng số: `ERR-FIN-001`.
3. Lý do (sau khi cắt) dài hơn 255 ký tự: từ chối. Lý do trống hoặc chỉ khoảng trắng: chấp nhận, lưu chuỗi rỗng.
4. Chênh lệch bằng số dư quan sát trừ số dư khả dụng hiện tại (`BR-FIN-01`, `BR-FIN-02`). Chênh lệch bằng 0.00 VNĐ: từ chối, không tạo bản ghi rỗng.
5. Tạo bản ghi điều chỉnh bất biến trong sổ điều chỉnh số dư gồm số dư trước, số dư sau (bằng số dư quan sát), chênh lệch, lý do và thời điểm. Cấm sửa bản ghi sau khi lưu.
6. Tính lại số dư ví ngay từ lịch sử dòng tiền (`BR-FIN-02`).

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Bản ghi điều chỉnh trong sổ điều chỉnh số dư kèm số dư ví đã cập nhật. |
| Bất biến hệ thống | Số dư ví sau điều chỉnh bằng đúng số dư quan sát; tổng số dư hệ thống biến động đúng bằng chênh lệchbảo toàn số dư ví (`BR-FIN-02`). |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-WAL-05.1` | Ví "Tiền mặt" ghi nhận 2,000,000.00 VNĐ, đếm thực tế 1,750,000.00 VNĐ, lý do "Kiểm kê tiền mặt định kỳ" | Tạo điều chỉnh chênh lệch -250,000.00 VNĐ, số dư ví 1,750,000.00 VNĐ. |
| `AC-WAL-05.2` | Số dư thực tế âm hoặc lý do dài quá 255 ký tự | Từ chối, số dư giữ nguyên (`ERR-WAL-001` khi số dư âm). |
| `AC-WAL-05.3` | Số dư thực tế bằng số dư hiện tại, hoặc điều chỉnh trên ví `ARCHIVED` | Từ chối, không tạo bản ghi chênh lệch 0. |
| `AC-WAL-05.4` | Yêu cầu hợp lệ nhưng để trống lý do (hoặc chỉ khoảng trắng) | Tạo điều chỉnh với lý do rỗng, số dư cập nhật đúng. |

### 4.2 FR-TXN: Nhóm chức năng Ghi nhận thu chi

Quản lý sổ giao dịch thu chi hằng ngày, gắn biến động số dư với danh mục 3 cấp. Mọi giao dịch chỉ liên kết với danh mục chi tiết cấp 3 đang dùng (`BR-FIN-04`). Quy tắc phí giao dịch FEE

#### 4.2.1 FR-TXN-01: Tạo mới giao dịch thu nhập

Bảng thuộc tính của yêu cầu chức năng Tạo mới giao dịch thu nhập (FR-TXN-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | `G-01`, `G-03` |
| Ca sử dụng liên quan | Ghi nhận giao dịch thu chi (`UC-TXN-01`) |
| Quy tắc áp dụng | Độ chính xác tài chính(`BR-FIN-01`), bảo toàn số dư ví(`BR-FIN-02`), phân cấp danh mục thu chi 3 cấp(`BR-FIN-04`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Có ít nhất một ví `ACTIVE` và một danh mục chi tiết `INCOME` đang dùng.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Ngày giao dịch | Ngày tháng (`YYYY-MM-DD`) | Không vượt quá ngày hiện tại; mặc định ngày hiện tại | Bắt buộc |
| Số tiền thu nhập | Số tiền (`VNĐ`) | Lớn hơn 0, 2 chữ số thập phân (`BR-FIN-01`) | Bắt buộc |
| Mã định danh ví | Chuỗi ký tự | Ví `ACTIVE` | Bắt buộc |
| Mã định danh danh mục | Chuỗi ký tự | Danh mục chi tiết đang dùng, thuộc nhánh `INCOME` (`BR-FIN-04`) | Bắt buộc |
| Chi phí giao dịch | Số tiền (`VNĐ`) | Không âm, mặc định 0.00 VNĐ | Tùy chọn |
| Mã định danh danh mục phí | Chuỗi ký tự | Danh mục `EXPENSE` đang dùng | Bắt buộc khi chi phí lớn hơn 0 |
| Ghi chú mô tả | Chuỗi ký tự | Tối đa 255 ký tự | Tùy chọn |

Quy tắc xử lý:

1. Số tiền sai định dạng hoặc chứa ký tự không hợp lệ: `ERR-FIN-001`; số tiền nhỏ hơn hoặc bằng 0: từ chối.
2. Ví không tồn tại hoặc `ARCHIVED`: từ chối, báo lỗi ví không hoạt động.
3. Danh mục không thuộc `INCOME`, đã xóa, hoặc không phải danh mục chi tiết đang dùng: từ chối.
4. Kiểm tra phí theo quy tắc phí giao dịch (FEE) (`ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`).
5. Khi chi phí lớn hơn 0: số dư ví trước giao dịch cộng thu nhập trừ chi phí phải lớn hơn hoặc bằng 0 (`BR-FIN-02`); nếu không, từ chối toàn bộ, `ERR-WAL-001`.
6. Tạo bản ghi thu nhập (mã duy nhất, tự liên kết nhóm danh mục từ danh mục chi tiết); tạo bản ghi phí liên kết theo quy tắc phí giao dịch (FEE).
7. Cập nhật số dư ví: cộng thu nhập, trừ chi phí (`BR-FIN-02`).

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Bản ghi thu nhập trong sổ thu chi; bản ghi phí liên kết nếu có chi phí. |
| Bất biến hệ thống | Số dư ví và tổng số dư hệ thống tăng đúng bằng thu nhập trừ chi phí (`BR-FIN-02`). |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-TXN-01.1` | Ví "MB Bank" 10,000,000.00 VNĐ; thu lương 25,000,000.00 VNĐ, chi phí 5,500.00 VNĐ, danh mục phí hợp lệ | Ghi nhận thu nhập và phí liên kết; số dư ví 34,994,500.00 VNĐ. |
| `AC-TXN-01.2` | Số tiền thu nhập nhỏ hơn hoặc bằng 0 hoặc sai định dạng | Từ chối, báo `ERR-FIN-001`. |
| `AC-TXN-01.3` | Ví "Tiền mặt" 0.00 VNĐ; thu 100,000.00 VNĐ nhưng chi phí 120,000.00 VNĐ, hoặc có phí nhưng thiếu danh mục phí | Từ chối toàn bộ, báo `ERR-WAL-001` hoặc `ERR-FEE-002`, số dư giữ nguyên. |

#### 4.2.2 FR-TXN-02: Tạo mới giao dịch chi tiêu

Bảng thuộc tính của yêu cầu chức năng Tạo mới giao dịch chi tiêu (FR-TXN-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | `G-01`, `G-03` |
| Ca sử dụng liên quan | Ghi nhận giao dịch thu chi (`UC-TXN-01`) |
| Quy tắc áp dụng | Độ chính xác tài chính(`BR-FIN-01`), bảo toàn số dư ví(`BR-FIN-02`), phân cấp danh mục thu chi 3 cấp(`BR-FIN-04`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Có ít nhất một ví `ACTIVE` với số dư lớn hơn 0 và một danh mục chi tiết `EXPENSE` đang dùng.

Dữ liệu đầu vào: như `FR-TXN-01`, khác ở hai điểm: số tiền là *Số tiền chi tiêu* (lớn hơn 0) và *Mã định danh danh mục* phải thuộc nhánh `EXPENSE`.

Quy tắc xử lý:

1. Số tiền sai định dạng: `ERR-FIN-001`; số tiền nhỏ hơn hoặc bằng 0: từ chối.
2. Ví không tồn tại hoặc `ARCHIVED`: từ chối, báo lỗi ví không hoạt động.
3. Danh mục không thuộc `EXPENSE`, đã xóa, hoặc không phải danh mục chi tiết đang dùng: từ chối.
4. Kiểm tra phí theo quy tắc phí giao dịch (FEE).
5. Số dư khả dụng của ví phải lớn hơn hoặc bằng tổng số tiền chi và chi phí (`BR-FIN-02`); nếu không, từ chối toàn bộ, `ERR-WAL-001`.
6. Tạo bản ghi chi tiêu (mã duy nhất, loại `EXPENSE`, tự liên kết nhóm danh mục); tạo bản ghi phí liên kết theo quy tắc phí giao dịch (FEE).
7. Trừ tổng số tiền chi và chi phí khỏi số dư ví (`BR-FIN-02`).

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Bản ghi chi tiêu trong sổ thu chi; bản ghi phí liên kết nếu có chi phí. |
| Bất biến hệ thống | Số dư ví và tổng số dư hệ thống giảm đúng bằng tổng số tiền chi và chi phí (`BR-FIN-02`). |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-TXN-02.1` | Ví "Tiền mặt" 500,000.00 VNĐ; chi 120,000.00 VNĐ, chi phí 3,300.00 VNĐ, danh mục phí hợp lệ | Ghi nhận chi tiêu và phí liên kết; số dư còn 376,700.00 VNĐ. |
| `AC-TXN-02.2` | Ví "Tiền mặt" 200,000.00 VNĐ; chi 150,000.00 VNĐ, chi phí 60,000.00 VNĐ | Từ chối toàn bộ, báo `ERR-WAL-001`, số dư giữ nguyên. |
| `AC-TXN-02.3` | Chọn danh mục `INCOME`, danh mục đã xóa, hoặc chi phí lớn hơn 0 nhưng không chọn danh mục phí | Từ chối, báo lỗi loại danh mục không khớp hoặc `ERR-FEE-002`. |

#### 4.2.3 FR-TXN-03: Tra cứu và lọc lịch sử giao dịch thu chi

Bảng thuộc tính của yêu cầu chức năng Tra cứu và lọc lịch sử giao dịch thu chi (FR-TXN-03), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | `G-01`, `G-03` |
| Ca sử dụng liên quan | Tra cứu và lọc lịch sử giao dịch (`UC-TXN-02`) |
| Quy tắc áp dụng | Độ chính xác tài chính(`BR-FIN-01`), phân cấp danh mục thu chi 3 cấp(`BR-FIN-04`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Khoảng ngày giao dịch | Ngày tháng (`YYYY-MM-DD`) | Ngày bắt đầu không sau ngày kết thúc; mặc định từ đầu đến cuối tháng hiện tại | Tùy chọn |
| Danh sách mã định danh ví | Danh sách chuỗi | Mặc định toàn bộ ví | Tùy chọn |
| Loại giao dịch | Mã phân loại | `ALL` (mặc định), `INCOME`, `EXPENSE` | Tùy chọn |
| Mã nhóm hoặc danh mục | Chuỗi ký tự | Gồm cả nhóm và danh mục đã xóa | Tùy chọn |
| Từ khóa tìm kiếm | Chuỗi ký tự | Tìm chuỗi con trong ghi chú, không phân biệt hoa thường | Tùy chọn |
| Phân trang | Cặp số nguyên dương | Trang từ 1; 10 đến 100 bản ghi mỗi trang; mặc định trang 1, 50 bản ghi | Tùy chọn |

Quy tắc xử lý:

1. Kết hợp đồng thời mọi tiêu chí lọc. Ngày bắt đầu sau ngày kết thúc: từ chối, báo lỗi khoảng ngày không hợp lệ.
2. Trên toàn bộ tập kết quả trước khi phân trang, tổng hợp (`BR-FIN-01`, 2 chữ số thập phân): tổng thu nhập; tổng chi tiêu (gồm phí liên kết); dòng tiền thuần bằng tổng thu trừ tổng chi.
3. Sắp xếp mặc định theo ngày giao dịch giảm dần, rồi thời điểm tạo giảm dần; trích xuất theo trang.
4. Bản ghi phí hiển thị thành dòng riêng kèm nhãn liên kết tới giao dịch gốc.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Danh sách theo trang, tổng số bản ghi, tổng số trang và ba chỉ số tổng hợp. |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-TXN-03.1` | Lọc từ 01/01/2026 đến 31/01/2026, loại `EXPENSE` | Trả đúng các khoản chi trong kỳ, tổng chi tiêu chính xác 2 chữ số thập phân (`BR-FIN-01`). |
| `AC-TXN-03.2` | Ngày bắt đầu sau ngày kết thúc | Từ chối, báo lỗi khoảng ngày không hợp lệ. |
| `AC-TXN-03.3` | Có chi 120,000.00 VNĐ kèm phí 3,300.00 VNĐ trong tháng; lọc `EXPENSE` | Hiển thị hai dòng (chi và phí liên kết), tổng chi tiêu 123,300.00 VNĐ. |

#### 4.2.4 FR-TXN-04: Chỉnh sửa giao dịch thu chi

Bảng thuộc tính của yêu cầu chức năng Chỉnh sửa giao dịch thu chi (FR-TXN-04), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | `G-01`, `G-02`, `G-03` |
| Ca sử dụng liên quan | Chỉnh sửa giao dịch thu chi (`UC-TXN-03`) |
| Quy tắc áp dụng | Độ chính xác tài chính(`BR-FIN-01`), bảo toàn số dư ví(`BR-FIN-02`), phân cấp danh mục thu chi 3 cấp(`BR-FIN-04`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Giao dịch tồn tại và không phải bản ghi phí giao dịch độc lập.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Mã định danh giao dịch | Chuỗi ký tự | Giao dịch thu chi đang tồn tại | Bắt buộc |
| Ngày giao dịch mới | Ngày tháng (`YYYY-MM-DD`) | Không vượt quá ngày hiện tại | Bắt buộc |
| Số tiền mới | Số tiền (`VNĐ`) | Lớn hơn 0, 2 chữ số thập phân | Bắt buộc |
| Mã định danh ví mới | Chuỗi ký tự | Ví `ACTIVE` | Bắt buộc |
| Mã định danh danh mục mới | Chuỗi ký tự | Cùng loại với giao dịch gốc; được giữ danh mục cũ kể cả đã xóa | Bắt buộc |
| Chi phí mới | Số tiền (`VNĐ`) | Không âm; để trống thì giữ chi phí hiện tại | Tùy chọn |
| Mã định danh danh mục phí mới | Chuỗi ký tự | Danh mục `EXPENSE` đang dùng | Bắt buộc khi phát sinh phí mới |
| Ghi chú mô tả | Chuỗi ký tự | Tối đa 255 ký tự | Tùy chọn |

Quy tắc xử lý:

1. Không tìm thấy giao dịch hoặc mã thuộc bản ghi phí độc lập: từ chối. Phí chỉ được điều chỉnh thông qua giao dịch gốc.
2. Loại giao dịch (`INCOME` hoặc `EXPENSE`) không đổi. Danh mục mới phải cùng loại (`BR-FIN-04`); giữ danh mục cũ được phép dù đã xóa, đổi sang danh mục khác thì danh mục mới phải đang hoạt động.
3. Hoàn nguyên ví cũ (`BR-FIN-02`): giao dịch `INCOME` thì trừ thu nhập cũ và hoàn chi phí cũ vào ví cũ (số dư âm thì từ chối, `ERR-WAL-001`); giao dịch `EXPENSE` thì hoàn số tiền chi cũ và chi phí cũ.
4. Áp dụng lên ví mới trên số dư sau hoàn nguyên, kể cả khi ví mới trùng ví cũ: `INCOME` thì cộng số tiền mới, trừ chi phí mới, số dư sau không âm; `EXPENSE` thì số dư phải đủ trừ số tiền mới và chi phí mới. Không đủ: `ERR-WAL-001`.
5. Xử lý phí liên kết theo quy tắc phí giao dịch (FEE): chi phí mới bằng 0 thì xóa bản ghi phí hiện có; lớn hơn 0 và chưa có phí thì tạo mới; cả hai lớn hơn 0 thì cập nhật số tiền và danh mục phí.
6. Cập nhật giao dịch, phí liên kết và số dư ví là một thao tác nguyên tử; lỗi ở bất kỳ bước nào thì hủy toàn bộ.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Giao dịch đã cập nhật; bản ghi phí liên kết được tạo mới, cập nhật hoặc xóa tương ứng. |
| Bất biến hệ thống | Số dư ví cũ và ví mới biến động đúng bằng chênh lệch giữa dữ liệu mới và cũ (`BR-FIN-02`). |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-TXN-04.1` | Sửa khoản chi 100,000.00 VNĐ từ ví A thành 150,000.00 VNĐ từ ví B (đủ tiền) | Hoàn 100,000.00 VNĐ vào ví A, trừ 150,000.00 VNĐ từ ví B, cập nhật giao dịch. |
| `AC-TXN-04.2` | Sửa khoản chi từ 200,000.00 lên 800,000.00 VNĐ khi ví chỉ còn 300,000.00 VNĐ | Từ chối, báo `ERR-WAL-001`, dữ liệu giữ nguyên. |
| `AC-TXN-04.3` | Sửa khoản chi 120,000.00 VNĐ đang có phí 3,300.00 VNĐ bằng cách đặt chi phí về 0.00 VNĐ, hoặc nhập mã một phí giao dịch để sửa trực tiếp | Xóa phí liên kết và hoàn 3,300.00 VNĐ vào ví; hoặc từ chối sửa trực tiếp phí độc lập. |

#### 4.2.5 FR-TXN-05: Xóa giao dịch thu chi

Bảng thuộc tính của yêu cầu chức năng Xóa giao dịch thu chi (FR-TXN-05), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | `G-01`, `G-02`, `G-03` |
| Ca sử dụng liên quan | Xóa giao dịch thu chi (`UC-TXN-04`) |
| Quy tắc áp dụng | Độ chính xác tài chính(`BR-FIN-01`), bảo toàn số dư ví(`BR-FIN-02`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Giao dịch tồn tại và không phải bản ghi phí giao dịch độc lập.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Mã định danh giao dịch | Chuỗi ký tự | Giao dịch thu chi đang tồn tại | Bắt buộc |
| Xác nhận xóa | Cờ logic | Phải là Đúng (`true`) để chấp thuận xóa | Bắt buộc |

Quy tắc xử lý:

1. Không tìm thấy giao dịch: từ chối. Mã là bản ghi phí độc lập: từ chối, báo phí giao dịch không được xóa trực tiếp.
2. Xác nhận là Sai (`false`): hủy thao tác, giữ nguyên dữ liệu.
3. Hoàn nguyên số dư (`BR-FIN-02`): `INCOME` thì trừ thu nhập và hoàn chi phí vào ví nhận (số dư âm thì từ chối, `ERR-WAL-001`); `EXPENSE` thì hoàn số tiền chi và chi phí vào ví chi.
4. Xóa giao dịch gốc cùng phí liên kết và hoàn nguyên số dư là một thao tác nguyên tử.
5. Tính lại các chỉ số thống kê liên quan.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Giao dịch và phí liên kết (nếu có) bị xóa vĩnh viễn khỏi sổ thu chi. |
| Bất biến hệ thống | Số dư ví phục hồi đúng về trạng thái trước khi giao dịch phát sinh (`BR-FIN-02`). |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-TXN-05.1` | Ví "MB Bank" 4,500,000.00 VNĐ sau khi chi 500,000.00 VNĐ; xác nhận xóa | Xóa giao dịch, số dư về 5,000,000.00 VNĐ. |
| `AC-TXN-05.2` | Ví 200,000.00 VNĐ; xóa khoản thu 1,000,000.00 VNĐ | Từ chối, báo `ERR-WAL-001`, dữ liệu nguyên vẹn. |
| `AC-TXN-05.3` | Ví "MB Bank" 376,700.00 VNĐ sau khi chi 120,000.00 VNĐ kèm phí 3,300.00 VNĐ; xác nhận xóa | Xóa đồng thời giao dịch và phí, số dư về 500,000.00 VNĐ. |

### 4.3 FR-TRF: Nhóm chức năng Chuyển tiền nội bộ

Chuyển tiền giữa các ví của cùng Người dùng theo cơ chế ghi nhận đồng thời (trừ ví nguồn, cộng ví đích, `BR-FIN-03`). Tổng số dư các ví không đổi; nếu có chi phí thì giảm đúng bằng chi phí.

#### 4.3.1 FR-TRF-01: Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn

Bảng thuộc tính của yêu cầu chức năng Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn (FR-TRF-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | `G-02`, `G-03` |
| Ca sử dụng liên quan | Chuyển tiền nội bộ (`UC-TRF-01`) |
| Quy tắc áp dụng | Độ chính xác tài chính(`BR-FIN-01`), bảo toàn số dư ví(`BR-FIN-02`), ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Có ít nhất hai ví `ACTIVE`, ví nguồn có số dư lớn hơn 0.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Mã định danh ví nguồn | Chuỗi ký tự | Ví `ACTIVE` | Bắt buộc |
| Mã định danh ví đích | Chuỗi ký tự | Ví `ACTIVE`, khác ví nguồn (`BR-FIN-03`) | Bắt buộc |
| Số tiền chuyển | Số tiền (`VNĐ`) | Lớn hơn 0, 2 chữ số thập phân | Bắt buộc |
| Ngày giao dịch | Ngày tháng (`YYYY-MM-DD`) | Không vượt quá ngày hiện tại; mặc định ngày hiện tại | Bắt buộc |
| Chi phí giao dịch | Số tiền (`VNĐ`) | Không âm, mặc định 0.00 VNĐ | Tùy chọn |
| Mã định danh danh mục phí | Chuỗi ký tự | Danh mục `EXPENSE` đang dùng quy tắc phí giao dịch (FEE) | Bắt buộc khi chi phí lớn hơn 0 |
| Ghi chú mô tả | Chuỗi ký tự | Tối đa 255 ký tự | Tùy chọn |

Quy tắc xử lý:

1. Sai định dạng số: `ERR-FIN-001`. Số tiền chuyển nhỏ hơn hoặc bằng 0: `ERR-TRF-002`. Chi phí âm: `ERR-FEE-001`.
2. Một trong hai ví không `ACTIVE`: từ chối. Ví đích trùng ví nguồn: `ERR-TRF-001`.
3. Kiểm tra danh mục phí theo quy tắc phí giao dịch (FEE) (`ERR-FEE-002`, `ERR-FEE-003`).
4. Số dư khả dụng ví nguồn phải lớn hơn hoặc bằng tổng số tiền chuyển và chi phí (`BR-FIN-02`, `BR-FIN-03`); nếu không, từ chối toàn bộ, `ERR-WAL-001`.
5. Trừ tổng số tiền chuyển và chi phí khỏi ví nguồn, cộng số tiền chuyển vào ví đích; hai biến động này cùng có hiệu lực hoặc cùng bị hủy (`BR-FIN-03`).
6. Tạo bản ghi chuyển tiền (mã duy nhất) trong sổ chuyển tiền nội bộ. Nếu có chi phí, tạo bản ghi phí loại chi tiêu trong sổ thu chi, liên kết với mã chuyển tiền. Chi phí tính vào tổng chi tiêu; khoản chuyển thuần túy không làm đổi tổng thu hay tổng chi.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Bản ghi chuyển tiền trong sổ chuyển tiền nội bộ; bản ghi phí liên kết trong sổ thu chi nếu có chi phí. |
| Bất biến hệ thống | Tổng số dư hệ thống không đổi khi không có chi phí, hoặc giảm đúng bằng chi phí (`BR-FIN-03`). |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-TRF-01.1` | Ví nguồn "MB Bank" 5,000,000.00 VNĐ, ví đích "Tiền mặt" 1,000,000.00 VNĐ; chuyển 2,000,000.00 VNĐ, phí 5,500.00 VNĐ, danh mục phí hợp lệ | MB Bank còn 2,994,500.00 VNĐ; Tiền mặt 3,000,000.00 VNĐ; tạo bản ghi chuyển tiền và phí 5,500.00 VNĐ; tổng số dư giảm đúng 5,500.00 VNĐ. |
| `AC-TRF-01.2` | Ví nguồn trùng ví đích, hoặc số tiền chuyển nhỏ hơn hoặc bằng 0 | Từ chối, báo `ERR-TRF-001` hoặc `ERR-TRF-002`. |
| `AC-TRF-01.3` | Ví nguồn 1,000,000.00 VNĐ chuyển 1,000,000.00 VNĐ kèm phí 5,500.00 VNĐ, hoặc có phí nhưng thiếu danh mục phí | Từ chối toàn bộ, báo `ERR-WAL-001` hoặc `ERR-FEE-002`, số dư hai ví giữ nguyên. |

#### 4.3.2 FR-TRF-02: Tra cứu và xóa giao dịch chuyển tiền nội bộ

Bảng thuộc tính của yêu cầu chức năng Tra cứu và xóa giao dịch chuyển tiền nội bộ (FR-TRF-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | `G-02`, `G-03` |
| Ca sử dụng liên quan | Tra cứu lịch sử chuyển tiền (`UC-TRF-02`), Xóa giao dịch chuyển tiền (`UC-TRF-03`) |
| Quy tắc áp dụng | Độ chính xác tài chính(`BR-FIN-01`), bảo toàn số dư ví(`BR-FIN-02`), ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong; khi xóa, giao dịch chuyển tiền phải đang tồn tại.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Mã định danh chuyển tiền | Chuỗi ký tự | Giao dịch chuyển tiền đang tồn tại | Bắt buộc khi xóa |
| Xác nhận xóa | Cờ logic | Phải là Đúng (`true`) để chấp thuận xóa | Bắt buộc khi xóa |

Quy tắc xử lý:

1. Tra cứu: hiển thị danh sách theo ngày giao dịch giảm dần gồm ngày, ví nguồn, ví đích, số tiền chuyển, chi phí, ghi chú.
2. Xóa: không tìm thấy bản ghi thì từ chối, báo lỗi không tìm thấy giao dịch chuyển tiền; xác nhận là Sai (`false`) thì hủy, giữ nguyên dữ liệu.
3. Số dư khả dụng ví đích phải lớn hơn hoặc bằng số tiền đã nhận (`BR-FIN-02`); nếu không, từ chối, `ERR-WAL-001`.
4. Khôi phục đồng thời hai ví (`BR-FIN-03`): hoàn số tiền chuyển và chi phí vào ví nguồn, thu hồi số tiền chuyển khỏi ví đích.
5. Xóa bản ghi chuyển tiền và phí liên kết (nếu có); chỉ ghi nhận khi cả cập nhật số dư và xóa bản ghi đều thành công.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Danh sách lịch sử chuyển tiền (khi tra cứu); hoặc bản ghi chuyển tiền và phí liên kết bị xóa. |
| Bất biến hệ thống | Khi xóa, số dư hai ví và tổng số dư phục hồi đúng về trạng thái trước khi chuyển (`BR-FIN-03`). |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-TRF-02.1` | Chuyển 2,000,000.00 VNĐ từ ví A sang ví B kèm phí 3,300.00 VNĐ; xác nhận xóa | Ví A được hoàn 2,003,300.00 VNĐ, ví B bị trừ 2,000,000.00 VNĐ, bản ghi chuyển tiền và phí bị xóa. |
| `AC-TRF-02.2` | Ví B sau khi nhận 2,000,000.00 VNĐ chỉ còn 500,000.00 VNĐ; yêu cầu xóa | Từ chối, báo `ERR-WAL-001`, dữ liệu hai ví nguyên vẹn. |
| `AC-TRF-02.3` | Mở xem lịch sử chuyển tiền, hoặc yêu cầu xóa với xác nhận Sai (`false`) | Hiển thị danh sách theo ngày giảm dần; hoặc từ chối xóa và bảo toàn dữ liệu. |

### 4.4 FR-CAT: Nhóm chức năng Danh mục phân loại

Quản lý cấu trúc danh mục 3 cấp (`BR-FIN-04`): Loại thu chi cố định; Nhóm nhu cầu và Danh mục chi tiết do Người dùng quản lý.

#### 4.4.1 FR-CAT-01: Tạo cấu trúc danh mục 3 cấp

Bảng thuộc tính của yêu cầu chức năng Tạo cấu trúc danh mục 3 cấp (FR-CAT-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`) |
| Ca sử dụng liên quan | Khởi tạo cấu trúc danh mục (`UC-CAT-01`) |
| Quy tắc áp dụng | Phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Hệ thống khởi tạo lần đầu trên tệp dữ liệu mới chưa có thông tin tài chính.

Dữ liệu đầu vào:

- Không yêu cầu tham số; cấu trúc mẫu do hệ thống tự thiết lập một lần khi khởi tạo lần đầu.

Quy tắc xử lý:

1. Thiết lập đúng 3 cấp (`BR-FIN-04`):
   - Cấp 1: Loại thu chi, gồm hai nhánh cố định Thu nhập (`INCOME`) và Chi tiêu (`EXPENSE`); không đổi tên, không xóa, không tạo thêm.
   - Cấp 2: Nhóm nhu cầu, do người dùng quản lý, trực thuộc một Loại thu chi.
   - Cấp 3: Danh mục chi tiết, trực thuộc một Nhóm nhu cầu và là cấp duy nhất liên kết với giao dịch.
2. Khi khởi tạo lần đầu, nạp bộ mẫu một lần duy nhất: mỗi Loại thu chi có ít nhất một nhóm mẫu, mỗi nhóm có ít nhất một danh mục mẫu. Không tự nạp lại khi khởi động lại ứng dụng.
3. Cấm ghi nhận giao dịch trực tiếp lên cấp 1 hoặc cấp 2.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Cấu trúc danh mục 3 cấp hoàn chỉnh trong tệp dữ liệu cục bộ. |
| Bất biến hệ thống | Không đổi số dư ví; luôn có đúng 2 nhánh Loại thu chi cố định. |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-CAT-01.1` | Khởi chạy lần đầu trên tệp dữ liệu mới | Sinh đủ 2 nhánh `INCOME` và `EXPENSE`, mỗi nhánh có ít nhất 1 nhóm mẫu, mỗi nhóm có ít nhất 1 danh mục mẫu. |
| `AC-CAT-01.2` | Tạo giao dịch và chọn cấp phân loại | Bắt buộc chọn danh mục chi tiết cấp 3; từ chối liên kết trực tiếp với cấp 1 hoặc cấp 2. |
| `AC-CAT-01.3` | Đã xóa một nhóm mẫu rồi khởi động lại hệ thống | Bộ mẫu không nạp lại, nhóm đã xóa giữ nguyên trạng thái đã xóa. |

#### 4.4.2 FR-CAT-02: Tùy biến danh mục người dùng

Bảng thuộc tính của yêu cầu chức năng Tùy biến danh mục người dùng (FR-CAT-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`) |
| Ca sử dụng liên quan | Thêm và sửa danh mục (`UC-CAT-02`), Xóa và khôi phục danh mục (`UC-CAT-03`) |
| Quy tắc áp dụng | `BR-NAM-01`, `BR-FIN-04` |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Cấu trúc danh mục đã sẵn sàng hoạt động.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Tên phân loại | Chuỗi ký tự | 1 đến 50 ký tự sau khi cắt, không trùng mục đang dùng cùng cấp (`BR-NAM-01`) | Bắt buộc |
| Vị trí trực thuộc | Chuỗi ký tự | Mã nhóm cha đang dùng (khi tạo danh mục), hoặc Loại thu chi `INCOME`/`EXPENSE` (khi tạo nhóm) | Bắt buộc |
| Biểu tượng nhận diện | Chuỗi ký tự | Tối đa 50 ký tự | Tùy chọn |
| Mã màu hiển thị | Chuỗi ký tự | Mã Hex, tối đa 10 ký tự | Tùy chọn |

Quy tắc xử lý:

1. Danh mục mới phải thuộc nhóm cha đang hoạt động; nhóm mới phải thuộc một Loại thu chi. Nhóm cha đã xóa: `ERR-CAT-005`. Vị trí trực thuộc không tồn tại: từ chối.
2. Kiểm tra tên (`BR-NAM-01`): rỗng hoặc dài hơn 50 ký tự thì `ERR-NAM-001`; trùng mục đang dùng cùng cấp (nhóm cùng Loại thu chi, danh mục cùng nhóm cha) thì `ERR-NAM-002`.
3. Tên trùng một mục đã xóa cùng cấp: cho người dùng chọn khôi phục mục cũ (giữ mã cũ, nối lại giao dịch quá khứ) hoặc tạo mục mới với mã định danh mới.
4. Đổi tên nhóm hoặc danh mục: kiểm tra `BR-NAM-01`, giữ nguyên mã định danh và liên kết giao dịch lịch sử. Hai nhánh Loại thu chi không có chức năng đổi tên hay xóa.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Bản ghi nhóm hoặc danh mục mới, hoặc bản ghi đã đổi tên. |
| Bất biến hệ thống | Không đổi số dư ví và giao dịch lịch sử. |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-CAT-02.1` | Thêm danh mục "Thú cưng" vào nhóm "Nhu cầu cá nhân và giải trí" với tên hợp lệ | Tạo danh mục `ACTIVE`, sẵn sàng phân loại giao dịch chi tiêu mới. |
| `AC-CAT-02.2` | Tạo nhóm hoặc danh mục có tên trống, dài quá 50 ký tự, hoặc trùng mục đang hoạt động cùng cấp | Từ chối, báo `ERR-NAM-001` hoặc `ERR-NAM-002`. |
| `AC-CAT-02.3` | Tạo danh mục chi tiết vào nhóm cha đã xóa | Từ chối, báo `ERR-CAT-005`. |

#### 4.4.3 FR-CAT-03: Kiểm soát toàn vẹn khi cập nhật và xóa danh mục

Bảng thuộc tính của yêu cầu chức năng Kiểm soát toàn vẹn khi cập nhật và xóa danh mục (FR-CAT-03), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp (`G-01`) |
| Ca sử dụng liên quan | Thêm và sửa danh mục (`UC-CAT-02`), Xóa và khôi phục danh mục (`UC-CAT-03`), Quản lý danh mục (`UC-CAT-04`) |
| Quy tắc áp dụng | `BR-NAM-01`, `BR-FIN-04` |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Nhóm nhu cầu hoặc danh mục chi tiết cần xử lý đang tồn tại.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Loại phân loại đích | Mã phân loại | Nhóm nhu cầu hoặc danh mục chi tiết | Bắt buộc |
| Mã định danh mục tiêu | Chuỗi ký tự | Mã đang tồn tại | Bắt buộc |
| Thao tác yêu cầu | Mã phân loại | Đổi tên, chuyển nhóm cha (chỉ danh mục), xóa, khôi phục | Bắt buộc |
| Tên mới | Chuỗi ký tự | 1 đến 50 ký tự sau khi cắt (`BR-NAM-01`) | Bắt buộc khi đổi tên |
| Mã định danh nhóm cha mới | Chuỗi ký tự | Nhóm đang hoạt động, cùng Loại thu chi với nhóm cũ | Bắt buộc khi chuyển nhóm |

Quy tắc xử lý:

1. Không tìm thấy mục tiêu: từ chối, báo lỗi không tìm thấy phân loại.
2. Xóa: quét sổ giao dịch. Chưa có giao dịch thì xóa vĩnh viễn. Đã có giao dịch thì chuyển sang trạng thái đã xóa, ẩn khỏi danh sách chọn cho giao dịch mới nhưng giữ nguyên trong giao dịch và báo cáo lịch sử kèm nhãn "đã xóa". Khi xóa nhóm, áp dụng quy tắc trên cho từng danh mục con: nhóm có danh mục con có giao dịch thì chuyển sang đã xóa, ngược lại xóa vĩnh viễn. Không được xóa nhóm đang dùng cuối cùng của một Loại thu chi: `ERR-CAT-002`.
3. Chuyển nhóm cha: chỉ sang nhóm đang hoạt động cùng Loại thu chi (`BR-FIN-04`). Khác Loại thu chi: `ERR-CAT-001`; nhóm đích đã xóa: `ERR-CAT-003`; trùng tên trong nhóm đích: `ERR-NAM-002`.
4. Khôi phục: giữ nguyên mã định danh và nối lại giao dịch cũ. Cùng cấp đã có mục đang hoạt động trùng tên: `ERR-NAM-002`. Danh mục có nhóm cha đã xóa: `ERR-CAT-004`, yêu cầu khôi phục nhóm cha trước.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Nhóm hoặc danh mục được cập nhật (đổi tên, chuyển nhóm, đã xóa, khôi phục hoặc xóa vĩnh viễn). |
| Bất biến hệ thống | Không đổi số dư ví; giao dịch lịch sử giữ nguyên liên kết và giá trị (`BR-FIN-04`). |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-CAT-03.1` | Xóa danh mục "Ăn uống" có 50 giao dịch; xóa danh mục "Học vẽ" mới tạo chưa có giao dịch | "Ăn uống" chuyển sang đã xóa và giữ nguyên 50 giao dịch; "Học vẽ" bị xóa vĩnh viễn. |
| `AC-CAT-03.2` | Chuyển danh mục chi tiêu "Xăng xe" sang nhóm thu nhập, hoặc sang một nhóm chi tiêu đã xóa | Từ chối, báo `ERR-CAT-001` hoặc `ERR-CAT-003`. |
| `AC-CAT-03.3` | Nhánh Chi tiêu chỉ còn 1 nhóm đang dùng và xóa nhóm đó; hoặc khôi phục danh mục khi nhóm cha đang bị xóa | Từ chối, báo `ERR-CAT-002` hoặc `ERR-CAT-004`. |

### 4.5 FR-REP: Nhóm chức năng Thống kê và báo cáo

Tổng hợp tự động dữ liệu giao dịch và số dư ví thành chuỗi dòng tiền 12 tháng gần nhất và cơ cấu thu chi, theo `BR-FIN-01` đến `BR-FIN-04`.

#### 4.5.1 FR-REP-01: Thống kê dòng tiền 12 tháng gần nhất

Bảng thuộc tính của yêu cầu chức năng Thống kê dòng tiền 12 tháng gần nhất (FR-REP-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | `G-01`, `G-03` |
| Ca sử dụng liên quan | Xem thống kê dòng tiền (`UC-REP-01`) |
| Quy tắc áp dụng | Độ chính xác tài chính (`BR-FIN-01`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Tháng kết thúc | Số nguyên dương | Từ 1 đến 12; mặc định tháng hiện tại | Tùy chọn |
| Năm kết thúc | Số nguyên dương | Từ 2000 trở đi; mặc định năm hiện tại | Tùy chọn |

Quy tắc xử lý:

1. Khung thống kê gồm đúng 12 tháng liên tục, kết thúc tại tháng và năm đã chọn, bắt đầu từ 11 tháng trước đó.
2. Với mỗi tháng (`BR-FIN-01`):
   - Tổng thu nhập tháng.
   - Tổng chi tiêu tháng, gồm phí giao dịch liên kết đang tồn tại.
   - Dòng tiền thuần tháng bằng tổng thu nhập trừ tổng chi tiêu.
   - Tỷ lệ tiết kiệm bằng dòng tiền thuần chia tổng thu nhập nhân 100 khi tổng thu nhập lớn hơn 0; ngược lại bằng 0.00% để tránh chia cho 0 (`ERR-FIN-002`).
3. Chỉ số bình quân cả chuỗi: thu nhập, chi tiêu và dòng tiền thuần bình quân bằng tổng 12 tháng chia 12. Mẫu số luôn là 12, tháng chưa có giao dịch tính 0.00 VNĐ.
4. Làm tròn ngân hàng, hiển thị 2 chữ số thập phân.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Bảng chuỗi 12 tháng (thu, chi, dòng tiền thuần, tỷ lệ tiết kiệm) kèm các chỉ số bình quân. |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-REP-01.1` | Chọn mốc kết thúc tháng 09/2026 | Xuất đủ 12 tháng từ 10/2025 đến 09/2026. |
| `AC-REP-01.2` | Một tháng thu 30,000,000.00 VNĐ, chi 18,000,000.00 VNĐ | Dòng tiền thuần +12,000,000.00 VNĐ; tỷ lệ tiết kiệm 40.00%. |
| `AC-REP-01.3` | Một tháng không có thu nhập, chi 5,000,000.00 VNĐ; hoặc chuỗi có tháng chưa phát sinh giao dịch | Dòng tiền thuần -5,000,000.00 VNĐ, tỷ lệ tiết kiệm 0.00% (`ERR-FIN-002`); tháng không giao dịch tính 0.00 VNĐ vào bình quân 12 tháng. |

#### 4.5.2 FR-REP-02: Phân tích cơ cấu thu chi theo nhóm và danh mục

Bảng thuộc tính của yêu cầu chức năng Phân tích cơ cấu thu chi theo nhóm và danh mục (FR-REP-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | `G-01`, `G-03` |
| Ca sử dụng liên quan | Xem phân tích cơ cấu thu chi (`UC-REP-02`) |
| Quy tắc áp dụng | Độ chính xác tài chính (`BR-FIN-01`), Phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Khoảng ngày phân tích | Ngày tháng (`YYYY-MM-DD`) | Ngày bắt đầu không sau ngày kết thúc | Bắt buộc |
| Loại luồng tiền phân tích | Mã phân loại | `EXPENSE` (mặc định) hoặc `INCOME` | Bắt buộc |

Quy tắc xử lý:

1. Ngày bắt đầu sau ngày kết thúc: từ chối, báo lỗi khoảng ngày không hợp lệ.
2. Gom nhóm các giao dịch đang tồn tại (`BR-FIN-04`): theo Nhóm nhu cầu cấp 2 (theo nhóm cha hiện tại của danh mục chi tiết) và theo Danh mục chi tiết cấp 3.
3. Tỷ lệ cơ cấu bằng tổng tiền của mục chia tổng tiền của loại luồng tiền nhân 100 (`BR-FIN-01`, làm tròn ngân hàng, 2 chữ số thập phân). Tổng bằng 0.00 VNĐ: tỷ lệ 0.00% (`ERR-FIN-002`).
4. Sắp xếp theo số tiền giảm dần.
5. Khi so sánh với kỳ liền kề cùng độ dài: tính chênh lệch giá trị và tỷ lệ tăng giảm; kỳ trước bằng 0.00 VNĐ thì hiển thị "mới phát sinh".
6. Nhóm hoặc danh mục đã xóa nhưng có giao dịch trong kỳ: hiển thị riêng kèm nhãn "đã xóa", không gộp vào mục khác, để tổng cơ cấu khớp tổng giao dịch thực tế.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Bảng cơ cấu 2 cấp (Nhóm nhu cầu, Danh mục chi tiết) gồm tên, tổng tiền, tỷ lệ và biến động so với kỳ trước. |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-REP-02.1` | Tổng chi 20,000,000.00 VNĐ, nhóm "Nhu cầu thiết yếu" 8,000,000.00 VNĐ | Tỷ lệ nhóm là 40.00% (`BR-FIN-01`). |
| `AC-REP-02.2` | Khoảng thời gian không có giao dịch | Tổng 0.00 VNĐ, danh sách rỗng, không lỗi chia cho 0 (`ERR-FIN-002`). |
| `AC-REP-02.3` | Kỳ có giao dịch thuộc nhóm hoặc danh mục đã xóa | Vẫn hiển thị mục đó kèm nhãn "đã xóa" với đúng số tiền và tỷ trọng; tổng báo cáo khớp tổng dòng tiền thực tế. |

### 4.6 FR-DAT: Nhóm chức năng Lưu trữ và sao lưu dữ liệu

Bảo đảm tệp dữ liệu hoạt động độc lập, không mất mát; phục hồi theo quy trình kiểm tra 4 bước của `FR-DAT-02` trên máy tính cục bộ.

#### 4.6.1 FR-DAT-01: Xuất dữ liệu tài chính

Bảng thuộc tính của yêu cầu chức năng Xuất dữ liệu tài chính (FR-DAT-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | Mục tiêu bảo đảm độ chính xác số học tài chính (`G-03`) |
| Ca sử dụng liên quan | Xuất dữ liệu tài chính (`UC-DAT-01`) |
| Quy tắc áp dụng | Độ chính xác tài chính (`BR-FIN-01`) |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Phạm vi xuất dữ liệu | Mã phân loại | `FULL` (toàn bộ hệ thống) hoặc `TRANSACTIONS` (sổ thu chi) | Bắt buộc |
| Định dạng tệp xuất | Mã phân loại | `JSON` cho cả hai phạm vi | Bắt buộc |
| Đường dẫn lưu trữ | Chuỗi ký tự | Thư mục hoặc tệp đích hợp lệ trên đĩa cục bộ | Bắt buộc |

Quy tắc xử lý:

1. Tạo ảnh chụp dữ liệu nhất quán tại một mốc thời gian cho ví, danh mục, giao dịch thu chi, chuyển tiền nội bộ và điều chỉnh số dư.
2. Nội dung JSON:
   - `FULL`: đủ 5 nhóm thực thể gồm ví; danh mục (cả nhóm và danh mục đã xóa, kèm ngày tạo); giao dịch thu chi (gồm phí liên kết); chuyển tiền nội bộ; điều chỉnh số dư.
   - `TRANSACTIONS`: mọi giao dịch thu chi và phí liên kết đang tồn tại, kèm mã giao dịch phát sinh phí.
   - Mọi trường số tiền là chuỗi số học chính xác, cố định 2 chữ số thập phân, không làm tròn hay dùng dấu chấm động (`BR-FIN-01`).
3. Đặt tên tệp `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json`, mã hóa UTF-8 không BOM, ghi tệp an toàn để không để lại tệp hỏng khi bị ngắt quãng.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Tệp JSON hoàn chỉnh tại đường dẫn chỉ định. |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-DAT-01.1` | Xuất toàn bộ hệ thống sang JSON | Tệp JSON hợp lệ, đủ 5 nhóm thực thể, UTF-8, thời điểm đồng bộ nhất quán. |
| `AC-DAT-01.2` | Kiểm tra nội dung tệp đã xuất | Mọi trường số tiền là chuỗi số cố định 2 chữ số thập phân, không dùng số thực dấu chấm động (`BR-FIN-01`). |
| `AC-DAT-01.3` | Xuất sổ thu chi khi có chuyển tiền đang tồn tại phát sinh phí và một chuyển tiền khác đã xóa | Tệp chứa phí của chuyển tiền đang tồn tại kèm mã liên kết, không chứa phí của chuyển tiền đã xóa. |

#### 4.6.2 FR-DAT-02: Phục hồi dữ liệu hệ thống từ bản sao lưu

Bảng thuộc tính của yêu cầu chức năng Phục hồi dữ liệu hệ thống từ bản sao lưu (FR-DAT-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | `G-01`, `G-02`, `G-03` |
| Ca sử dụng liên quan | Phục hồi dữ liệu từ bản sao lưu (`UC-DAT-02`) |
| Quy tắc áp dụng | `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04` |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Người dùng cung cấp tệp sao lưu JSON hợp lệ và xác nhận thay thế toàn bộ dữ liệu hiện tại.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Đường dẫn tệp sao lưu | Chuỗi ký tự | Trỏ tới tệp sao lưu JSON hợp lệ trên đĩa cục bộ | Bắt buộc |

Quy tắc xử lý:

1. Phân tích cú pháp JSON. Tệp hỏng, sai cấu trúc hoặc không UTF-8: từ chối, báo lỗi định dạng tệp không hợp lệ.
2. Kiểm tra toàn vẹn 4 bước trước khi ghi:
   - Bước 1 (`BR-FIN-01`): đủ các trường bắt buộc, ngày đúng dạng `YYYY-MM-DD`, chuỗi số tiền đúng định dạng chính xác (`ERR-FIN-001`).
   - Bước 2 (`BR-FIN-04`): mọi mã danh mục trong giao dịch trỏ tới danh mục có trong tệp với Loại thu chi tương thích.
   - Bước 3 (`BR-FIN-03`): mọi giao dịch thu chi và chuyển tiền trỏ tới ví có trong danh sách ví; mỗi bản ghi phí chuyển tiền liên kết tới một chuyển tiền có trong tệp.
   - Bước 4 (`BR-FIN-02`): tính lại số dư mọi ví, không ví nào âm.
3. Phát hiện sai lệch ở bất kỳ bước nào: hủy toàn bộ phục hồi, giữ nguyên 100% dữ liệu hiện tại và báo danh sách lỗi cụ thể.
4. Cả 4 bước đạt: thay toàn bộ dữ liệu hiện tại bằng dữ liệu sao lưu, tính lại số dư ví và cập nhật chuỗi thống kê 12 tháng.

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Toàn bộ dữ liệu được phục hồi đồng bộ: ví, danh mục, giao dịch thu chi, chuyển tiền nội bộ, điều chỉnh số dư. |
| Bất biến hệ thống | Dữ liệu chuyển hoàn toàn sang trạng thái bản sao lưu, hoặc giữ nguyên trạng cũ nếu có lỗi toàn vẹn. |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-DAT-02.1` | Phục hồi từ tệp JSON hợp lệ, đạt cả 4 bước | Dữ liệu và số dư ví phục hồi khớp thời điểm sao lưu; dữ liệu cũ bị thay thế trọn vẹn. |
| `AC-DAT-02.2` | Tệp có giao dịch trỏ tới danh mục không tồn tại, hoặc phí chuyển tiền trỏ tới mã chuyển tiền không có trong tệp | Từ chối, báo lỗi liên kết không hợp lệ, giữ nguyên dữ liệu hiện tại. |
| `AC-DAT-02.3` | Tệp có chuỗi giao dịch làm một ví âm số dư khi tính lại | Từ chối (`BR-FIN-02`), dữ liệu hiện tại được bảo toàn. |

#### 4.6.3 FR-DAT-03: Kiểm tra lại dữ liệu hệ thống

Bảng thuộc tính của yêu cầu chức năng Kiểm tra lại dữ liệu hệ thống (FR-DAT-03), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

| Thuộc tính | Giá trị |
| :-- | :-- |
| Mục tiêu đáp ứng | `G-01`, `G-02`, `G-03` |
| Ca sử dụng liên quan | Kiểm tra tính nhất quán dữ liệu (`UC-DAT-03`) |
| Quy tắc áp dụng | `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04` |
| Mức độ ưu tiên | Bắt buộc (`MUST`) |

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong và đang vận hành với tệp dữ liệu cục bộ.

Dữ liệu đầu vào:

| Tham số | Kiểu dữ liệu | Ràng buộc giá trị | Tính bắt buộc |
| :-- | :-- | :-- | :-: |
| Phương thức kích hoạt | Mã phân loại | Thủ công do người dùng yêu cầu, hoặc tự động theo lịch định kỳ | Bắt buộc |

Quy tắc xử lý:

1. Đối soát độc lập 4 điều kiện toàn vẹn:
   - Điều kiện 1 (`BR-FIN-02`): tính lại số dư từ toàn bộ lịch sử dòng tiền và so với số dư ghi nhận của từng ví; sai số cho phép bằng đúng 0.00 VNĐ (`BR-FIN-01`).
   - Điều kiện 2 (`BR-FIN-03`): với mọi chuyển tiền, số tiền trừ ở ví nguồn bằng đúng số tiền cộng ở ví đích (không tính chi phí).
   - Điều kiện 3 (`BR-FIN-04`): mọi giao dịch gắn với danh mục hợp lệ (danh mục đã xóa vẫn hợp lệ trong lịch sử), không sai lệch Loại thu chi.
   - Điều kiện 4: mọi phí giao dịch phát sinh từ chuyển tiền trỏ tới một chuyển tiền đang tồn tại.
2. Phân loại kết quả theo mức nghiêm trọng: thông tin, cảnh báo, lỗi nghiêm trọng.
3. Mỗi sai lệch kèm đề xuất hành động cụ thể (ví dụ tính lại số dư tự động, hoặc phục hồi từ bản sao lưu gần nhất).

Dữ liệu đầu ra và bất biến hệ thống:

| Thành phần | Nội dung |
| :-- | :-- |
| Bản ghi dữ liệu | Báo cáo trạng thái từng điều kiện (Đạt hoặc Không đạt) kèm số bản ghi đã quét. |

Tiêu chí nghiệm thu:

| Mã tiêu chí | Điều kiện kích hoạt | Kết quả kỳ vọng |
| :-- | :-- | :-- |
| `AC-DAT-03.1` | Dữ liệu hoàn toàn nhất quán | Báo cáo 100% Đạt cho cả 4 điều kiện: số dư ví, cân bằng chuyển tiền, liên kết danh mục, liên kết phí. |
| `AC-DAT-03.2` | Số dư ghi nhận của một ví lệch so với dòng tiền tính lại | Phát hiện đúng ví lệch (`BR-FIN-02`), trạng thái Không đạt, đề xuất tính lại số dư. |
| `AC-DAT-03.3` | Có chuyển tiền mất cân bằng nguồn đích hoặc phí chuyển tiền mồ côi | Phát hiện đúng (`BR-FIN-03`), trạng thái Không đạt kèm mã giao dịch vi phạm. |