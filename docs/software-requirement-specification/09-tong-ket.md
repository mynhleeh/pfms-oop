## 9. Tổng kết

Bảng dưới đây tổng hợp quy mô các thành phần trong bản đặc tả Hệ thống quản lý tài chính cá nhân (PFMS):

| Thành phần | Phạm vi mã | Số lượng | Mô tả |
| :--- | :--- | :---: | :--- |
| Mục tiêu hệ thống | `G-01` đến `G-03` | 3 | Có tiêu chí đo bằng số và phân tích đánh đổi kỹ thuật |
| Quy tắc nghiệp vụ tài chính | `BR-FIN-01` đến `BR-FIN-04` | 4 | Độ chính xác tài chính, bảo toàn số dư, ghi nhận đồng thời và phân cấp danh mục 3 cấp |
| Quy tắc nghiệp vụ về tên | `BR-NAM-01` | 1 | Định dạng và tính duy nhất của tên ví, nhóm nhu cầu và danh mục |
| Yêu cầu chức năng | `FR-WAL-01` đến `FR-DAT-03` | 20 | Chia theo 6 nhóm chức năng nghiệp vụ |
| Yêu cầu phi chức năng | `NFR-01` đến `NFR-06` | 6 | Có ngưỡng nghiệm thu; `NFR-02` là định tính, `NFR-05` có một chỉ số định tính |
| Ca sử dụng chi tiết | `UC-WAL-01` đến `UC-DAT-03` | 20 | Mỗi ca gồm luồng chính, luồng rẽ nhánh và ngoại lệ |
| Sơ đồ ca sử dụng | Mục 7 | 7 | 1 sơ đồ tổng quan và 6 sơ đồ chi tiết |
| Truy vết và kiểm chứng | 4 ma trận truy vết (Mục 8.1) và 26 dòng kiểm chứng theo phương pháp I, T, D, A (20 `FR`, 6 `NFR`, Mục 8.2) | 26 | Truy vết hai chiều giữa mục tiêu, yêu cầu chức năng, ca sử dụng và quy tắc nghiệp vụ; kiểm chứng cả yêu cầu chức năng lẫn phi chức năng |