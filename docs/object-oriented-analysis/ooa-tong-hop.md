---
papersize: a4
geometry:
  - top=20mm
  - bottom=20mm
  - left=20mm
  - right=20mm
mainfont: "Times New Roman"
monofont: "Courier New"
header-includes: |
  \usepackage{longtable}
  \usepackage{tikz}
  \usetikzlibrary{calc}
  \usepackage{array}
  \usepackage{float}
  \floatplacement{figure}{H}
  \usepackage{caption}
  \captionsetup[figure]{labelformat=empty}
  \usepackage{calc}
  \usepackage{booktabs}
  \usepackage{colortbl}
  \usepackage{xcolor}
  \setlength{\arrayrulewidth}{0.5pt}
  \setlength{\tabcolsep}{4pt}
  \setlength{\emergencystretch}{3em}
  \setlength{\LTpre}{6pt}
  \setlength{\LTpost}{6pt}
  \usepackage{etoolbox}
  \usepackage{xparse}
  \ExplSyntaxOn
  \NewDocumentCommand{\normalcode}{m}
   {
    \tl_set:Nn \l_tmpa_tl { #1 }
    \regex_replace_all:nnN { ([a-z])([A-Z]) } { \1\c{-}\2 } \l_tmpa_tl
    \regex_replace_all:nnN { (\.) } { \1\c{allowbreak} } \l_tmpa_tl
    \regex_replace_all:nnN { (_) } { \c{textunderscore}\c{allowbreak} } \l_tmpa_tl
    \tl_use:N \l_tmpa_tl
   }
  \ExplSyntaxOff
  \renewcommand{\texttt}[1]{\normalcode{#1}}
  \AtBeginDocument{\let\passthrough\normalcode}
  \ifPDFTeX\else\newfontfamily\tablefont{Times New Roman}\AtBeginEnvironment{longtable}{\tablefont}\fi
  \usepackage{enumitem}
  \setlist[itemize,1]{label={}}
  \setlist[itemize,2]{label={}}
  \setlist[itemize,3]{label={}}
---

\thispagestyle{empty}

\begin{center}
  {\LARGE\bfseries HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG\par}
\end{center}

\vspace*{0.5cm}

\begin{tikzpicture}[remember picture, overlay]
  % Viền ngoài
  \draw[line width=1.6pt]
    ([shift={(1.2cm,-1.2cm)}]current page.north west)
    rectangle
    ([shift={(-1.2cm,1.2cm)}]current page.south east);
  % Viền trong
  \draw[line width=0.5pt]
    ([shift={(1.45cm,-1.45cm)}]current page.north west)
    rectangle
    ([shift={(-1.45cm,1.45cm)}]current page.south east);
\end{tikzpicture}

```{=latex}
\begin{center}
  \includegraphics[width=0.18\textwidth]{docs/object-oriented-analysis/images/logo.jpg}
\end{center}
```

\vspace*{0.6cm}

\begin{center}
  {\LARGE\bfseries HỆ THỐNG QUẢN LÝ TÀI CHÍNH CÁ NHÂN\par}
  \vspace*{0.4cm}
  {\Large\bfseries PHÂN TÍCH HƯỚNG ĐỐI TƯỢNG\par}
\end{center}

\vspace*{0.8cm}

```{=latex}
\newcommand{\cell}[1]{\begin{tabular}[t]{@{}l@{}}#1\end{tabular}}

\begin{center}
{\large
\renewcommand{\arraystretch}{1.4}
\begin{tabular}{@{}>{\bfseries}l@{\hspace{2em}}l@{}}
Khoa & Công nghệ Thông tin 1 \\
Học phần & Lập trình Hướng đối tượng \\
Giảng viên & \cell{Phạm Hoàng Việt \\ Nguyễn Đình Quảng} \\
Nhóm & 3 \\
Thành viên & \cell{Lê Anh Minh - B24DCCE180 \\ Ứng Trọng Trình - B24DCCE271 \\ Nguyễn Đình Tiến - B24DCCE264 \\ Nguyễn Đinh Anh Quân - B24DCCE222} \\
\end{tabular}
}
\end{center}
```

\newpage

## Mục lục Tổng quan

- [1. Giới thiệu và ngữ cảnh hệ thống](#1-giới-thiệu-và-ngữ-cảnh-hệ-thống)
  - [1.1 Mục đích và phạm vi tài liệu OOA](#11-mục-đích-và-phạm-vi-tài-liệu-ooa)
  - [1.2 Ranh giới và ngữ cảnh hệ thống](#12-ranh-giới-và-ngữ-cảnh-hệ-thống)
  - [1.3 Bảng đối chiếu khái niệm miền theo nhóm chức năng](#13-bảng-đối-chiếu-khái-niệm-miền-theo-nhóm-chức-năng)
- [2. Mô hình miền khái niệm](#2-mô-hình-miền-khái-niệm)
  - [2.1 Phương pháp luận xác định lớp khái niệm](#21-phương-pháp-luận-xác-định-lớp-khái-niệm)
  - [2.2 Bảng phân loại thực thể và đối tượng giá trị](#22-bảng-phân-loại-thực-thể-và-đối-tượng-giá-trị)
    - [2.2.1 Danh mục thực thể miền](#221-danh-mục-thực-thể-miền)
    - [2.2.2 Danh mục đối tượng giá trị](#222-danh-mục-đối-tượng-giá-trị)
  - [2.3 Đặc tả chi tiết các lớp thực thể](#23-đặc-tả-chi-tiết-các-lớp-thực-thể)
    - [2.3.1 Thực thể Ví - Wallet](#231-thực-thể-ví---wallet)
    - [2.3.2 Thực thể Giao dịch thu chi - IncomeExpenseTransaction](#232-thực-thể-giao-dịch-thu-chi---incomeexpensetransaction)
    - [2.3.3 Thực thể Giao dịch chuyển tiền - TransferTransaction](#233-thực-thể-giao-dịch-chuyển-tiền---transfertransaction)
    - [2.3.4 Thực thể Chi phí giao dịch - TransactionFee](#234-thực-thể-chi-phí-giao-dịch---transactionfee)
    - [2.3.5 Thực thể Danh mục phân loại - Category](#235-thực-thể-danh-mục-phân-loại---category)
    - [2.3.6 Thực thể Nhóm nhu cầu - NeedGroup](#236-thực-thể-nhóm-nhu-cầu---needgroup)
    - [2.3.7 Thực thể Bản ghi điều chỉnh số dư - BalanceAdjustmentRecord](#237-thực-thể-bản-ghi-điều-chỉnh-số-dư---balanceadjustmentrecord)
  - [2.4 Đặc tả chi tiết các đối tượng giá trị](#24-đặc-tả-chi-tiết-các-đối-tượng-giá-trị)
    - [2.4.1 Tiền tệ và độ chính xác số học - Money](#241-tiền-tệ-và-độ-chính-xác-số-học---money)
    - [2.4.2 Ngày giao dịch và mốc thời gian - TransactionDate](#242-ngày-giao-dịch-và-mốc-thời-gian---transactiondate)
    - [2.4.3 Loại giao dịch - TransactionType](#243-loại-giao-dịch---transactiontype)
    - [2.4.4 Dòng tiền thuần - NetCashFlow](#244-dòng-tiền-thuần---netcashflow)
    - [2.4.5 Tỷ lệ tiết kiệm - SavingsRate](#245-tỷ-lệ-tiết-kiệm---savingsrate)
    - [2.4.6 Tỷ trọng cơ cấu thu chi - SpendingStructureShare](#246-tỷ-trọng-cơ-cấu-thu-chi---spendingstructureshare)
  - [2.5 Các mối quan hệ ngữ nghĩa và bất biến miền](#25-các-mối-quan-hệ-ngữ-nghĩa-và-bất-biến-miền)
    - [2.5.1 Quan hệ sở hữu ví và bảo toàn số dư](#251-quan-hệ-sở-hữu-ví-và-bảo-toàn-số-dư)
    - [2.5.2 Quan hệ phân cấp danh mục 3 cấp](#252-quan-hệ-phân-cấp-danh-mục-3-cấp)
    - [2.5.3 Quan hệ phụ thuộc sinh tử giữa giao dịch gốc và phí](#253-quan-hệ-phụ-thuộc-sinh-tử-giữa-giao-dịch-gốc-và-phí)
- [3. Sơ đồ tuần tự hệ thống](#3-sơ-đồ-tuần-tự-hệ-thống)
  - [3.1 Nguyên tắc mô hình hóa và ranh giới tương tác hệ thống](#31-nguyên-tắc-mô-hình-hóa-và-ranh-giới-tương-tác-hệ-thống)
  - [3.2 Tổng hợp thao tác hệ thống theo nhóm chức năng](#32-tổng-hợp-thao-tác-hệ-thống-theo-nhóm-chức-năng)
  - [3.3 Nhóm chức năng Quản lý ví (WAL)](#33-nhóm-chức-năng-quản-lý-ví-wal)
    - [3.3.1 Tạo ví mới](#331-tạo-ví-mới)
    - [3.3.2 Xem tổng hợp số dư và cơ cấu ví](#332-xem-tổng-hợp-số-dư-và-cơ-cấu-ví)
    - [3.3.3 Cập nhật thông tin ví](#333-cập-nhật-thông-tin-ví)
    - [3.3.4 Điều chỉnh số dư ví thực tế](#334-điều-chỉnh-số-dư-ví-thực-tế)
  - [3.4 Nhóm chức năng Ghi nhận thu chi (TXN)](#34-nhóm-chức-năng-ghi-nhận-thu-chi-txn)
    - [3.4.1 Ghi nhận giao dịch thu chi](#341-ghi-nhận-giao-dịch-thu-chi)
    - [3.4.2 Tra cứu và lọc lịch sử giao dịch](#342-tra-cứu-và-lọc-lịch-sử-giao-dịch)
    - [3.4.3 Chỉnh sửa giao dịch thu chi](#343-chỉnh-sửa-giao-dịch-thu-chi)
    - [3.4.4 Xóa giao dịch thu chi](#344-xóa-giao-dịch-thu-chi)
  - [3.5 Nhóm chức năng Chuyển tiền nội bộ (TRF)](#35-nhóm-chức-năng-chuyển-tiền-nội-bộ-trf)
    - [3.5.1 Chuyển tiền giữa hai ví](#351-chuyển-tiền-giữa-hai-ví)
    - [3.5.2 Tra cứu lịch sử chuyển tiền](#352-tra-cứu-lịch-sử-chuyển-tiền)
    - [3.5.3 Xóa giao dịch chuyển tiền](#353-xóa-giao-dịch-chuyển-tiền)
  - [3.6 Nhóm chức năng Danh mục phân loại (CAT)](#36-nhóm-chức-năng-danh-mục-phân-loại-cat)
    - [3.6.1 Thêm danh mục phân loại mới](#361-thêm-danh-mục-phân-loại-mới)
    - [3.6.2 Đổi tên danh mục](#362-đổi-tên-danh-mục)
    - [3.6.3 Khôi phục danh mục đã xóa](#363-khôi-phục-danh-mục-đã-xóa)
    - [3.6.4 Xóa danh mục phân loại](#364-xóa-danh-mục-phân-loại)
  - [3.7 Nhóm chức năng Thống kê và báo cáo (REP)](#37-nhóm-chức-năng-thống-kê-và-báo-cáo-rep)
    - [3.7.1 Báo cáo dòng tiền 12 tháng gần nhất](#371-báo-cáo-dòng-tiền-12-tháng-gần-nhất)
    - [3.7.2 Phân tích cơ cấu thu chi theo nhóm](#372-phân-tích-cơ-cấu-thu-chi-theo-nhóm)
  - [3.8 Nhóm chức năng Lưu trữ và sao lưu dữ liệu (DAT)](#38-nhóm-chức-năng-lưu-trữ-và-sao-lưu-dữ-liệu-dat)
    - [3.8.1 Xuất dữ liệu sao lưu](#381-xuất-dữ-liệu-sao-lưu)
    - [3.8.2 Phục hồi dữ liệu từ bản sao lưu](#382-phục-hồi-dữ-liệu-từ-bản-sao-lưu)
    - [3.8.3 Đối soát toàn vẹn dữ liệu hệ thống](#383-đối-soát-toàn-vẹn-dữ-liệu-hệ-thống)
- [4. Đặc tả biến đổi dữ liệu thao tác hệ thống](#4-đặc-tả-biến-đổi-dữ-liệu-thao-tác-hệ-thống)
  - [4.1 Cấu trúc đặc tả thao tác](#41-cấu-trúc-đặc-tả-thao-tác)
  - [4.2 Các thao tác Quản lý ví](#42-các-thao-tác-quản-lý-ví)
    - [4.2.1 Thao tác tạo ví mới](#421-thao-tác-tạo-ví-mới)
    - [4.2.2 Thao tác cập nhật thông tin ví](#422-thao-tác-cập-nhật-thông-tin-ví)
    - [4.2.3 Thao tác điều chỉnh số dư ví thực tế](#423-thao-tác-điều-chỉnh-số-dư-ví-thực-tế)
  - [4.3 Các thao tác Ghi nhận thu chi](#43-các-thao-tác-ghi-nhận-thu-chi)
    - [4.3.1 Thao tác ghi nhận giao dịch thu chi](#431-thao-tác-ghi-nhận-giao-dịch-thu-chi)
    - [4.3.2 Thao tác chỉnh sửa giao dịch thu chi](#432-thao-tác-chỉnh-sửa-giao-dịch-thu-chi)
    - [4.3.3 Thao tác xóa giao dịch thu chi](#433-thao-tác-xóa-giao-dịch-thu-chi)
  - [4.4 Các thao tác Chuyển tiền nội bộ](#44-các-thao-tác-chuyển-tiền-nội-bộ)
    - [4.4.1 Thao tác chuyển tiền giữa hai ví](#441-thao-tác-chuyển-tiền-giữa-hai-ví)
    - [4.4.2 Thao tác xóa giao dịch chuyển tiền](#442-thao-tác-xóa-giao-dịch-chuyển-tiền)
  - [4.5 Các thao tác Danh mục phân loại](#45-các-thao-tác-danh-mục-phân-loại)
    - [4.5.1 Thao tác thêm danh mục phân loại mới](#451-thao-tác-thêm-danh-mục-phân-loại-mới)
    - [4.5.2 Thao tác đổi tên danh mục](#452-thao-tác-đổi-tên-danh-mục)
    - [4.5.3 Thao tác khôi phục danh mục đã xóa](#453-thao-tác-khôi-phục-danh-mục-đã-xóa)
    - [4.5.4 Thao tác xóa danh mục phân loại](#454-thao-tác-xóa-danh-mục-phân-loại)
  - [4.6 Thao tác Lưu trữ và phục hồi dữ liệu](#46-thao-tác-lưu-trữ-và-phục-hồi-dữ-liệu)
    - [4.6.1 Thao tác phục hồi dữ liệu từ bản sao lưu](#461-thao-tác-phục-hồi-dữ-liệu-từ-bản-sao-lưu)
  - [4.7 Thống kê và báo cáo](#47-thống-kê-và-báo-cáo)
- [5. Mô hình trạng thái thực thể](#5-mô-hình-trạng-thái-thực-thể)
  - [5.1 Quy chuẩn mô hình trạng thái](#51-quy-chuẩn-mô-hình-trạng-thái)
  - [5.2 Vòng đời thực thể Ví](#52-vòng-đời-thực-thể-ví)
  - [5.3 Vòng đời Giao dịch thu chi](#53-vòng-đời-giao-dịch-thu-chi)
  - [5.4 Vòng đời Giao dịch chuyển tiền](#54-vòng-đời-giao-dịch-chuyển-tiền)
  - [5.5 Vòng đời thực thể Danh mục](#55-vòng-đời-thực-thể-danh-mục)
- [6. Sơ đồ Robustness và thẻ CRC](#6-sơ-đồ-robustness-và-thẻ-crc)
  - [6.1 Sơ đồ Robustness](#61-sơ-đồ-robustness)
    - [6.1.1 Quản lý ví](#611-quản-lý-ví)
    - [6.1.2 Ghi nhận thu chi](#612-ghi-nhận-thu-chi)
    - [6.1.3 Chuyển tiền nội bộ](#613-chuyển-tiền-nội-bộ)
    - [6.1.4 Danh mục phân loại](#614-danh-mục-phân-loại)
    - [6.1.5 Thống kê và báo cáo](#615-thống-kê-và-báo-cáo)
    - [6.1.6 Lưu trữ và sao lưu](#616-lưu-trữ-và-sao-lưu)
  - [6.2 Thẻ CRC](#62-thẻ-crc)
    - [6.2.1 Lớp Ví - Wallet](#621-lớp-ví---wallet)
    - [6.2.2 Lớp Bản ghi điều chỉnh số dư - BalanceAdjustmentRecord](#622-lớp-bản-ghi-điều-chỉnh-số-dư---balanceadjustmentrecord)
    - [6.2.3 Lớp Giao dịch thu chi - IncomeExpenseTransaction](#623-lớp-giao-dịch-thu-chi---incomeexpensetransaction)
    - [6.2.4 Lớp Chi phí giao dịch - TransactionFee](#624-lớp-chi-phí-giao-dịch---transactionfee)
    - [6.2.5 Lớp Giao dịch chuyển tiền - TransferTransaction](#625-lớp-giao-dịch-chuyển-tiền---transfertransaction)
    - [6.2.6 Lớp Danh mục phân loại - Category](#626-lớp-danh-mục-phân-loại---category)
    - [6.2.7 Lớp Nhóm nhu cầu - NeedGroup](#627-lớp-nhóm-nhu-cầu---needgroup)
    - [6.2.8 Lớp Điều phối dòng tiền - CashFlowCoordinator](#628-lớp-điều-phối-dòng-tiền---cashflowcoordinator)
- [7. Ma trận truy vết](#7-ma-trận-truy-vết)
  - [7.1 Truy vết yêu cầu chức năng](#71-truy-vết-yêu-cầu-chức-năng)
  - [7.2 Kiểm soát quy tắc nghiệp vụ](#72-kiểm-soát-quy-tắc-nghiệp-vụ)
- [8. Tổng kết](#8-tổng-kết)
- [9. Tài liệu Trích dẫn](#9-tài-liệu-trích-dẫn)
\newpage

# 1. Giới thiệu và ngữ cảnh hệ thống

Tài liệu Phân tích hướng đối tượng (OOA) xác lập mô hình miền khái niệm, hành vi tương tác và hợp đồng thao tác cho Hệ thống quản lý tài chính cá nhân (PFMS). Tài liệu chuẩn hóa các khái niệm nghiệp vụ, quy tắc bất biến tài chính và sự kiện hệ thống trên nguyên tắc trung lập công nghệ (độc lập với ngôn ngữ lập trình, cơ sở dữ liệu, giao diện và mẫu thiết kế chi tiết), làm cơ sở cho giai đoạn Thiết kế hướng đối tượng (OOD).

## 1.1 Mục đích và phạm vi tài liệu OOA

Tài liệu mô hình hóa miền khái niệm, hành vi tương tác hộp đen và hợp đồng thao tác trên toàn bộ 6 nhóm chức năng nghiệp vụ:

| STT | Nhóm chức năng | Mã nhóm | Số UC | Dải ca sử dụng | Nội dung nghiệp vụ chính |
| :-: | :-------- | :-: | :-: | :------- | :------------------------ |
| 1 | Quản lý ví | `WAL` | 4 | `UC-WAL-01` - `UC-WAL-04` | Quản lý ví tiền mặt và các ví tạo thêm; tính số dư khả dụng, tỷ lệ phân bổ; bảo toàn số dư không âm (`BR-FIN-02`), số học chính xác (`BR-FIN-01`) và chuẩn đặt tên (`BR-NAM-01`). |
| 2 | Ghi nhận thu chi | `TXN` | 4 | `UC-TXN-01` - `UC-TXN-04` | Ghi nhận giao dịch thu chi; liên kết ví, gán danh mục theo phân cấp (`BR-FIN-04`), cập nhật số dư tức thời (`BR-FIN-02`) và hạch toán phí chính xác (`BR-FIN-01`). |
| 3 | Chuyển tiền nội bộ | `TRF` | 3 | `UC-TRF-01` - `UC-TRF-03` | Điều chuyển tiền giữa hai ví nội bộ; ghi nhận đồng thời bảo toàn tổng số dư (`BR-FIN-03`), kiểm soát số dư ví nguồn (`BR-FIN-02`) và tính phí chính xác (`BR-FIN-01`). |
| 4 | Danh mục phân loại | `CAT` | 4 | `UC-CAT-01` - `UC-CAT-04` | Quản lý cây phân loại 3 cấp: loại thu chi, nhóm nhu cầu, danh mục chi tiết (`BR-FIN-04`); bảo đảm toàn vẹn tham chiếu khi xóa và tính duy nhất của tên (`BR-NAM-01`). |
| 5 | Thống kê và báo cáo | `REP` | 2 | `UC-REP-01` - `UC-REP-02` | Trích xuất dữ liệu 12 tháng liên tục; tổng hợp thu chi (gồm phí theo `BR-FIN-02`), dòng tiền thuần, cơ cấu chi tiêu (`BR-FIN-04`) và tỷ lệ tiết kiệm làm tròn ngân hàng (`BR-FIN-01`). |
| 6 | Lưu trữ và sao lưu | `DAT` | 3 | `UC-DAT-01` - `UC-DAT-03` | Kiểm tra toàn vẹn dữ liệu, đối soát số dư ví (`BR-FIN-02`); xuất và phục hồi dữ liệu từ bản sao lưu hợp lệ (`AS-02`, `CON-04`). |
| **Tổng** | **6 nhóm chức năng** | | **20** | **20 ca sử dụng** | |

Tài liệu phục vụ 4 nhóm đối tượng kỹ thuật:

| Nhóm đối tượng | Vai trò kỹ thuật | Mục đích sử dụng tài liệu OOA |
| :--- | :--- | :--- |
| Kỹ sư phân tích hệ thống | Tác giả mô hình | Kiểm soát ranh giới bài toán, duy trì ma trận truy vết và tính toàn vẹn của mô hình. |
| Kỹ sư thiết kế kiến trúc | Tiếp nhận chuyển giao | Xây dựng biểu đồ lớp chi tiết, phân bổ trách nhiệm và lựa chọn mẫu thiết kế phù hợp. |
| Kỹ sư kiểm thử phần mềm | Kiểm thử chấp thuận | Đối chiếu tiền/hậu điều kiện trong hợp đồng thao tác để thiết kế kịch bản kiểm thử hộp đen. |
| Chuyên gia thẩm định kỹ thuật | Đánh giá độc lập | Rà soát tính chính xác của mô hình hướng đối tượng và tính đầy đủ của các quy tắc bất biến. |

## 1.2 Ranh giới và ngữ cảnh hệ thống

Hệ thống được tiếp cận theo mô hình hộp đen, chỉ xét thông tin tiếp nhận từ môi trường ngoài và kết quả phản hồi mà không đi vào cấu trúc nội bộ. Phần mềm vận hành cục bộ trên máy tính cá nhân, phục vụ một người dùng duy nhất và tương tác với hai thành phần môi trường là đồng hồ hệ thống (`DE-01`) cùng hệ thống tệp cục bộ (`DE-02`).

![Sơ đồ ngữ cảnh hệ thống PFMS](docs/object-oriented-analysis/images/hinh1.2.png){width=80%}

Các thành phần tương tác qua ranh giới hệ thống:
- **Người dùng**: Tác nhân duy nhất vận hành hệ thống cục bộ (`AS-01`), kiểm soát toàn bộ 6 nhóm chức năng (20 ca sử dụng). Do chạy đơn tiến trình cục bộ, hệ thống không yêu cầu xác thực danh tính, quản lý phiên mạng hay phân quyền.
- **Đồng hồ hệ thống (`DE-01`)**: Cung cấp mốc thời gian thực để gắn nhãn giao dịch và xác định chu kỳ báo cáo 12 tháng.
- **Hệ thống tệp cục bộ (`DE-02`)**: Đọc dữ liệu khi khởi động/phục hồi và ghi tệp an toàn xuống đĩa sau mỗi biến động tài chính để phòng sự cố mất nguồn.

## 1.3 Bảng đối chiếu khái niệm miền theo nhóm chức năng

Bảng tổng hợp các thực thể, đối tượng giá trị và quy tắc nghiệp vụ của 6 nhóm chức năng:

| Nhóm chức năng | Ca sử dụng | Thực thể miền | Đối tượng giá trị | Quy tắc nghiệp vụ áp dụng |
| :----- | :----- | :--------- | :-------- | :------------ |
| Quản lý ví (`WAL`) | `UC-WAL-01` - `UC-WAL-04` | `Wallet`, `BalanceAdjustmentRecord` | `Money` | Chính xác số học (`BR-FIN-01`), số dư không âm (`BR-FIN-02`), tên ví duy nhất (`BR-NAM-01`) |
| Ghi nhận thu chi (`TXN`) | `UC-TXN-01` - `UC-TXN-04` | `IncomeExpenseTransaction`, `TransactionFee` | `Money`, `TransactionType` | Chính xác số học (`BR-FIN-01`), cập nhật số dư tức thời (`BR-FIN-02`), phân loại hợp lệ (`BR-FIN-04`) |
| Chuyển tiền nội bộ (`TRF`) | `UC-TRF-01` - `UC-TRF-03` | `TransferTransaction`, `TransactionFee` | `Money` | Chính xác số học (`BR-FIN-01`), kiểm soát ví nguồn (`BR-FIN-02`), ghi nhận đồng thời (`BR-FIN-03`) |
| Danh mục phân loại (`CAT`) | `UC-CAT-01` - `UC-CAT-04` | `Category`, `NeedGroup` | `TransactionType` | Cấu trúc phân cấp 3 cấp (`BR-FIN-04`), tên danh mục duy nhất (`BR-NAM-01`) |
| Thống kê và báo cáo (`REP`) | `UC-REP-01` - `UC-REP-02` | `IncomeExpenseTransaction`, `TransactionFee`, `NeedGroup`, `Category`, `TransferTransaction` (chỉ đọc ngày của phí) | `NetCashFlow`, `SavingsRate`, `SpendingStructureShare`, `TransactionDate`, `TransactionType` | Làm tròn ngân hàng (`BR-FIN-01`), cộng phí giao dịch bảo toàn số dư (`BR-FIN-02`), tổng hợp theo nhóm nhu cầu (`BR-FIN-04`) |
| Lưu trữ và sao lưu (`DAT`) | `UC-DAT-01` - `UC-DAT-03` | | | Bảo toàn số dư khi phục hồi (`BR-FIN-02`) |
\newpage

# 2. Mô hình miền khái niệm

Mô hình miền khái niệm phản ánh các khái niệm nghiệp vụ trong quản lý tài chính cá nhân, tách biệt với chi tiết kỹ thuật (lớp lập trình, cơ sở dữ liệu hay giao diện). Mô hình định hình ngôn ngữ nghiệp vụ thống nhất, phục vụ 3 mục tiêu cốt lõi: quản lý nguồn tiền theo ví, kiểm soát dòng tiền thu chi và phân tích cơ cấu tài chính định kỳ.

## 2.1 Phương pháp luận xác định lớp khái niệm

Hệ thống kết hợp hai kỹ thuật phân tích để xác định các lớp khái niệm mà không phụ thuộc vào cấu trúc cài đặt:
- **Danh sách phân loại**: Rà soát bài toán theo các nhóm khái niệm chuẩn: giao dịch tài chính, nguồn chứa tiền tệ, cấu trúc phân loại thu chi và bản ghi kiểm kê đối soát.
- **Phân tích cụm danh từ**: Trích xuất các danh từ nghiệp vụ thực tế từ mô tả bài toán (loại bỏ danh từ công nghệ như giao diện, nút bấm, cơ sở dữ liệu, tệp tin) để phân định thành lớp khái niệm, thuộc tính hoặc đại lượng đo lường.

Các khái niệm trích xuất được chia thành hai nhóm:
- **Thực thể (Entity)**: Có định danh độc lập, duy nhất và duy trì vòng đời biến đổi trạng thái qua các giao dịch.
- **Đối tượng giá trị (Value Object)**: Mô tả đặc tính hoặc đại lượng đo lường không có định danh độc lập, bất biến và bảo đảm độ chính xác số học tuyệt đối.

## 2.2 Bảng phân loại thực thể và đối tượng giá trị

Hệ thống gồm 7 thực thể quản lý dữ liệu tài chính và 6 đối tượng giá trị phục vụ đo lường, tính toán.

### 2.2.1 Danh mục thực thể miền

| Thực thể miền | Thuộc tính chính | Quy tắc nghiệp vụ |
| :--- | :--- | :--- |
| `Wallet` | Mã ví, tên ví, ghi chú, trạng thái hoạt động | Tên ví duy nhất (`BR-NAM-01`). Số dư tính từ lịch sử giao dịch và không âm (`BR-FIN-02`). |
| `IncomeExpenseTransaction` | Mã giao dịch, số tiền, ngày giao dịch, ghi chú | Gắn với một ví và một danh mục cụ thể; tự động tăng (thu) hoặc giảm (chi) số dư ví ngay khi ghi nhận (`BR-FIN-02`, `BR-FIN-04`). |
| `TransferTransaction` | Mã chuyển tiền, số tiền, ngày chuyển, ghi chú | Ví gửi phải khác ví nhận. Tiền trừ ở ví gửi và cộng vào ví nhận đồng thời để bảo toàn tổng số dư (`BR-FIN-03`). |
| `TransactionFee` | Mã chi phí, số tiền phí | Không âm, gắn với một danh mục chi tiêu. Phụ thuộc vào giao dịch gốc; xóa giao dịch gốc sẽ xóa phí kèm theo (`BR-FIN-02`, `BR-FIN-03`). |
| `Category` | Mã danh mục, tên danh mục, trạng thái hoạt động | Tên duy nhất trong cùng nhóm cha (`BR-NAM-01`). Danh mục đã có giao dịch chỉ xóa mềm (ẩn đi), không xóa vĩnh viễn (`BR-FIN-04`). |
| `NeedGroup` | Mã nhóm, tên nhóm | Tên duy nhất trong cùng loại thu chi (`BR-NAM-01`). Mỗi loại thu chi phải giữ tối thiểu một nhóm đang hoạt động. |
| `BalanceAdjustmentRecord` | Mã bản ghi, số dư trước, số dư sau, chênh lệch, ngày kiểm kê, lý do | Lưu khoản chênh lệch khi kiểm kê thực tế (`BR-FIN-02`); bất biến sau khi tạo. |

### 2.2.2 Danh mục đối tượng giá trị

| Đối tượng giá trị | Dữ liệu cấu thành | Ý nghĩa và quy tắc tính |
| :--- | :--- | :--- |
| `Money` | Số tiền thập phân, đơn vị tiền tệ | Lưu số tiền thập phân chính xác, không dùng số thực nhị phân (`BR-FIN-01`); chia tiền làm tròn ngân hàng (nửa chẵn). |
| `TransactionDate` | Ngày, tháng, năm | Ngày phát sinh giao dịch hợp lệ theo lịch thực tế. |
| `TransactionType` | Giá trị cố định: Thu nhập hoặc Chi tiêu | Gồm hai giá trị cố định, quyết định chiều cộng/trừ tiền vào ví (`BR-FIN-04`). |
| `NetCashFlow` | Số tiền | Tổng thu nhập trừ tổng chi tiêu trong kỳ (`BR-FIN-01`). |
| `SavingsRate` | Tỷ lệ phần trăm | Bằng dòng tiền thuần chia tổng thu nhập, giữ tối thiểu 4 chữ số thập phân trung gian và làm tròn ngân hàng 2 chữ số (`0.00%` khi tổng thu nhập $\le 0$ theo `BR-FIN-01`). |
| `SpendingStructureShare` | Tỷ lệ phần trăm | Tỷ trọng thu/chi (gồm phí theo `BR-FIN-02`) của nhóm hoặc danh mục trên tổng kỳ/nhóm cha, làm tròn ngân hàng 2 chữ số (`0.00%` khi mẫu số $\le 0$ theo `BR-FIN-01`). |

## 2.3 Đặc tả chi tiết các lớp thực thể

Phần này đặc tả bản chất khái niệm, cấu trúc thuộc tính (kèm tham chiếu SRS) và quy tắc bất biến, vận hành của 7 thực thể miền.

### 2.3.1 Thực thể Ví - Wallet

Ví (`Wallet`) đại diện cho nguồn tiền hoặc tài khoản thanh toán của người dùng. Hệ thống tạo sẵn ví "Tiền mặt" mặc định và cho phép tạo thêm các ví khác để quản lý tài sản, kiểm soát số dư không âm theo thời gian thực.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `walletId` | Mã định danh ví | Chuỗi ID sinh tự động khi tạo mới (`FR-WAL-01`), bất biến và không tái sử dụng (`FR-WAL-03`). |
| `name` | Tên ví | Từ 1–50 ký tự sau khi `trim`, không để trống (`BR-NAM-01`, `ERR-NAM-001`). Duy nhất toàn hệ thống (không phân biệt hoa thường, tính cả ví lưu trữ) (`FR-WAL-01`, `ERR-NAM-002`); giải phóng tên khi xóa vĩnh viễn (`FR-WAL-03`). Ví mặc định có tên cố định là "Tiền mặt". |
| `status` | Trạng thái hoạt động | Nhận giá trị `ACTIVE` (mặc định) hoặc `ARCHIVED` (`FR-WAL-01`, `FR-WAL-03`). Cấm xóa hoặc lưu trữ ví "Tiền mặt" (`AC-WAL-03.2`). Ví khác chỉ xóa vĩnh viễn khi chưa có giao dịch, nếu đã có chỉ được chuyển sang `ARCHIVED` (`AC-WAL-03.3`). |
| `note` | Ghi chú | Mô tả tùy chọn, tối đa 255 ký tự sau khi `trim` (`CON-05`, `FR-WAL-01`, `FR-WAL-03`). |
| `createdAt` | Thời điểm tạo | Mốc thời gian từ `DE-01`, bất biến, dùng để sắp xếp và đối soát (`FR-WAL-01`, `FR-WAL-02`). |
| `currentBalance` | Số dư khả dụng (dẫn xuất) | Kiểu `Money`, tính từ lịch sử dòng tiền (`BR-FIN-02`), không gán trực tiếp. Khởi điểm `0.00 VNĐ` (`FR-WAL-01`), luôn $\ge 0.00$ VNĐ (`ERR-WAL-001`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Số dư khả dụng luôn không âm ($\text{WalletBalance}(w, t) \ge 0.00$ VNĐ) và chỉ biến động qua các giao dịch thu chi, chuyển tiền hoặc điều chỉnh số dư hợp lệ (`BR-FIN-02`, `ERR-WAL-001`).
  - Tên ví duy nhất toàn hệ thống giữa cả ví `ACTIVE` và `ARCHIVED` (`BR-NAM-01`, `ERR-NAM-002`).
  - Ví "Tiền mặt" mặc định tồn tại vĩnh viễn, cấm xóa hoặc lưu trữ (`FR-WAL-03`, `AC-WAL-03.2`).
- **Quy tắc vận hành**:
  - Kiểm tra khả năng chi trả: Mọi thao tác giảm số dư (`FR-TXN-01`, `FR-TXN-02`, `FR-TXN-04`, `FR-TXN-05`, `FR-TRF-01`, `FR-WAL-05`) phải kiểm tra đủ số dư khả dụng, nếu thiếu từ chối theo `ERR-WAL-001`.
  - Tỷ lệ phân bổ ví: Bằng $(\text{currentBalance} / \text{totalBalance}) \times 100\%$ (`FR-WAL-04`), làm tròn ngân hàng (`BR-FIN-01`); gán `0.00%` nếu tổng số dư bằng 0 (`ERR-FIN-002`). Ví `ARCHIVED` có số dư $> 0$ hiển thị kèm nhãn "(đã lưu trữ)" (`AC-WAL-04.3`).
  - Tính số dư thời gian thực (`BR-FIN-02`) theo công thức:

$$
\begin{aligned}
\text{WalletBalance}_w(T) &= \sum_{t \le T} \text{Inflow}_w(t) - \sum_{t \le T} \text{Outflow}_w(t) \\
\text{Inflow}_w(t) &= \text{Income}_w(t) + \text{TransferIn}_w(t) + \max(0, \text{Adjustment}_w(t)) \\
\text{Outflow}_w(t) &= \text{Expense}_w(t) + \text{TransferOut}_w(t) + \max(0, -\text{Adjustment}_w(t))
\end{aligned}
$$

### 2.3.2 Thực thể Giao dịch thu chi - IncomeExpenseTransaction

Giao dịch thu chi (`IncomeExpenseTransaction`) ghi nhận dòng tiền vào (`INCOME`) hoặc ra (`EXPENSE`) tại một ví cụ thể theo một danh mục cấp 3, trực tiếp cập nhật số dư ví và cung cấp dữ liệu cho báo cáo định kỳ.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `transactionId` | Mã định danh giao dịch | Chuỗi ID sinh tự động, bất biến và không tái sử dụng (`FR-TXN-01`, `FR-TXN-02`). |
| `amount` | Số tiền giao dịch | Kiểu `Money`, bắt buộc $> 0.00$ VNĐ (`BR-FIN-01`, `FR-TXN-01`, `FR-TXN-02`, `ERR-FIN-001`). |
| `transactionDate` | Ngày giao dịch | Kiểu `TransactionDate` (`YYYY-MM-DD`), không vượt quá ngày hiện tại từ `DE-01` (`FR-TXN-01`, `FR-TXN-02`); mặc định là ngày hiện tại. |
| `transactionType` | Loại giao dịch | Kiểu `TransactionType` (`INCOME` hoặc `EXPENSE`, `BR-FIN-04`). Bất biến sau khi tạo (`FR-TXN-04`). |
| `walletId` | Mã ví liên kết | Tham chiếu `Wallet` đang `ACTIVE` (`FR-TXN-01`, `FR-TXN-02`). |
| `categoryId` | Mã danh mục phân loại | Tham chiếu `Category` cấp 3 đang `ACTIVE` cùng nhánh loại thu chi (`BR-FIN-04`). Khi sửa giao dịch, cho phép giữ nguyên danh mục cũ dù đã xóa mềm (`FR-TXN-04`). |
| `note` | Ghi chú | Mô tả tùy chọn, tối đa 255 ký tự sau khi `trim` (`CON-05`, `FR-TXN-01`–`FR-TXN-03`). |
| `createdAt` | Thời điểm tạo | Mốc thời gian từ `DE-01`, bất biến, dùng sắp xếp thứ cấp khi trùng ngày giao dịch (`FR-TXN-03`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Số tiền bắt buộc $> 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`); `transactionType` bất biến sau khi tạo (`FR-TXN-04`).
  - Liên kết với một ví `ACTIVE` và một danh mục cấp 3 `ACTIVE` cùng loại thu chi (`BR-FIN-04`); cấm liên kết lên cấp 1 hoặc cấp 2 (`AC-CAT-01.2`).
  - Khả năng chi trả: Giao dịch `EXPENSE` phải thỏa $\text{currentBalance} \ge \text{amount} + \text{feeAmount}$ (`BR-FIN-02`, `ERR-WAL-001`).
- **Quy tắc vận hành**:
  - Cập nhật số dư và phí: Cộng `amount` vào ví nếu `INCOME`, trừ `amount` nếu `EXPENSE` (`BR-FIN-02`). Khi $\text{feeAmount} > 0$, tự động tạo `TransactionFee` đi kèm trong cùng một thao tác nguyên tử (`FR-TXN-01`, `FR-TXN-02`).
  - Chỉnh sửa hai pha (`FR-TXN-04`): Hoàn nguyên số dư trên ví cũ (kiểm tra không âm `ERR-WAL-001` nếu hoàn nguyên khoản thu), áp dụng số dư mới lên ví mới (kiểm tra đủ chi trả nếu là khoản chi) và đồng bộ phí liên kết.
  - Xóa giao dịch (`FR-TXN-05`): Hoàn nguyên số dư ví (kiểm tra `ERR-WAL-001` nếu xóa khoản thu) và xóa đồng thời `TransactionFee` đi kèm (`AC-TXN-05.3`).

### 2.3.3 Thực thể Giao dịch chuyển tiền - TransferTransaction

Giao dịch chuyển tiền (`TransferTransaction`) ghi nhận việc điều chuyển tiền nội bộ giữa hai ví theo cơ chế đồng thời, bảo toàn tổng số dư hệ thống trước phí và không hạch toán khoản chuyển gốc vào thu nhập hay chi tiêu.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `transferId` | Mã định danh chuyển tiền | Chuỗi ID sinh tự động, bất biến và không tái sử dụng (`FR-TRF-01`). |
| `sourceWalletId` | Mã ví nguồn | Tham chiếu ví trích tiền đang `ACTIVE`, bắt buộc khác ví đích (`BR-FIN-03`, `ERR-TRF-001`). |
| `destinationWalletId` | Mã ví đích | Tham chiếu ví nhận tiền đang `ACTIVE`, bắt buộc khác ví nguồn (`BR-FIN-03`, `ERR-TRF-001`). |
| `amount` | Số tiền chuyển | Kiểu `Money`, bắt buộc $> 0.00$ VNĐ (`BR-FIN-01`, `BR-FIN-03`, `FR-TRF-01`, `ERR-TRF-002`). |
| `transactionDate` | Ngày chuyển tiền | Kiểu `TransactionDate` (`YYYY-MM-DD`), không vượt quá ngày hiện tại từ `DE-01` (`FR-TRF-01`). |
| `note` | Ghi chú | Mô tả tùy chọn, tối đa 255 ký tự sau khi `trim` (`CON-05`, `FR-TRF-01`, `FR-TRF-02`). |
| `createdAt` | Thời điểm tạo | Mốc thời gian từ `DE-01`, bất biến, phục vụ sắp xếp và kiểm toán (`FR-TRF-02`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Ví nguồn và ví đích phải khác nhau ($\text{sourceWalletId} \neq \text{destinationWalletId}$, `BR-FIN-03`, `ERR-TRF-001`); số tiền chuyển $> 0.00$ VNĐ (`ERR-TRF-002`).
  - Bảo toàn tổng số dư hệ thống trước phí: $\sum \text{WalletBalance}(\text{after}) = \sum \text{WalletBalance}(\text{before})$ (`BR-FIN-03`); nếu có phí `feeAmount`, tổng số dư giảm đúng bằng `feeAmount`.
  - Ví nguồn phải đủ số dư thanh toán cả tiền chuyển và phí ($\text{sourceWallet.currentBalance} \ge \text{amount} + \text{feeAmount}$, `BR-FIN-02`, `ERR-WAL-001`).
- **Quy tắc vận hành**:
  - Ghi nhận đồng thời (`Atomic Execution`): Trừ ví nguồn $\text{amount} + \text{feeAmount}$ và cộng ví đích `amount` trong cùng một giao dịch nguyên tử (`BR-FIN-03`). Nếu $\text{feeAmount} > 0$, tự động tạo `TransactionFee` (loại `EXPENSE`) trích từ ví nguồn (`FR-TRF-01`).
  - Hoàn tác khi xóa (`FR-TRF-02`): Kiểm tra ví đích đủ số dư $\ge \text{amount}$ (`ERR-WAL-001`), trừ `amount` ở ví đích, hoàn $\text{amount} + \text{feeAmount}$ về ví nguồn và xóa `TransactionFee` liên kết (`BR-FIN-03`, `AC-TRF-02.1`).

### 2.3.4 Thực thể Chi phí giao dịch - TransactionFee

Chi phí giao dịch (`TransactionFee`) là khoản phí phụ do bên thứ ba thu đi kèm giao dịch gốc (thu chi hoặc chuyển tiền), mang bản chất chi tiêu (`EXPENSE`) và phụ thuộc sinh tử (`Composition`) vào giao dịch gốc.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `feeId` | Mã định danh chi phí | Chuỗi ID sinh tự động, bất biến và không tái sử dụng (`BR-FIN-02`, `FR-TXN-01`, `FR-TRF-01`). |
| `amount` | Số tiền phí | Kiểu `Money`, $\ge 0.00$ VNĐ (`BR-FIN-01`, `BR-FIN-02`, `ERR-FEE-001`). Không tạo bản ghi khi phí bằng `0.00 VNĐ`. |
| `walletId` | Mã ví chịu phí | Tham chiếu ví phát sinh thu chi hoặc ví chuyển tiền nguồn đang `ACTIVE` (`BR-FIN-02`). |
| `categoryId` | Mã danh mục phí | Tham chiếu `Category` cấp 3 đang `ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-02`, `BR-FIN-04`, `ERR-FEE-002`, `ERR-FEE-003`). |
| `parentTransactionId` | Mã giao dịch gốc | Tham chiếu `transactionId` hoặc `transferId`, bất biến, loại trừ phí mồ côi (`BR-FIN-02`). |
| `parentTransactionType` | Loại giao dịch gốc | Nhận giá trị `INCOME_EXPENSE` hoặc `TRANSFER`, phục vụ truy nguyên và hiển thị (`FR-TXN-03`, `FR-TRF-01`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Số tiền phí không âm ($\text{amount} \ge 0.00$ VNĐ, `ERR-FEE-001`); khi $\text{amount} > 0.00$ VNĐ bắt buộc gắn danh mục cấp 3 `ACTIVE` thuộc nhánh `EXPENSE` (`ERR-FEE-002`, `ERR-FEE-003`).
  - Không tồn tại phí mồ côi (`No Orphan Fee`): Phí luôn gắn với `parentTransactionId`; cấm người dùng tạo, sửa hoặc xóa trực tiếp độc lập với giao dịch gốc (`BR-FIN-02`, `FR-TXN-04`, `FR-TXN-05`).
- **Quy tắc vận hành**:
  - Hạch toán và hiển thị: Ghi nhận như một khoản `EXPENSE` trừ vào ví chịu phí, tính vào tổng chi tiêu và dòng tiền thuần (`FR-REP-01`, `AC-TXN-03.3`), hiển thị dòng riêng kèm nhãn liên kết tới giao dịch gốc trên sổ thu chi (`FR-TXN-03`).
  - Đồng bộ vòng đời: Tự động tạo, cập nhật hoặc xóa kèm hoàn phí theo các thao tác tạo, sửa, xóa trên giao dịch gốc (`AC-TXN-01.1`, `AC-TRF-01.1`, `FR-TXN-04`, `AC-TXN-05.3`, `AC-TRF-02.1`).

### 2.3.5 Thực thể Danh mục phân loại - Category

Danh mục phân loại (`Category`) là đơn vị phân loại cấp 3 gán nhãn mục đích tài chính cho giao dịch. Đây là cấp duy nhất trong cây danh mục được liên kết trực tiếp với giao dịch thu chi và phí giao dịch.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `categoryId` | Mã định danh danh mục | Chuỗi ID sinh tự động, bất biến và không tái sử dụng kể cả khi đã xóa (`BR-FIN-04`, `FR-CAT-02`). |
| `name` | Tên danh mục | Từ 1–50 ký tự sau khi `trim`, không để trống (`BR-NAM-01`, `ERR-NAM-001`). Duy nhất trong cùng `NeedGroup` cha đang hoạt động, không phân biệt hoa thường (`ERR-NAM-002`). |
| `needGroupId` | Mã nhóm nhu cầu cha | Tham chiếu `NeedGroup` cấp 2 đang `ACTIVE` (`BR-FIN-04`, `FR-CAT-01`, `ERR-CAT-005`). Chỉ được chuyển sang nhóm `ACTIVE` cùng loại thu chi (`ERR-CAT-001`, `ERR-CAT-003`). |
| `status` | Trạng thái hoạt động | Nhận giá trị `ACTIVE` hoặc `DELETED` (`BR-FIN-04`, `FR-CAT-03`): xóa vĩnh viễn nếu chưa có giao dịch; chuyển sang `DELETED` (ẩn khi nhập mới, giữ lịch sử kèm nhãn "(đã xóa)") nếu đã có giao dịch. |
| `icon` | Biểu tượng | Chuỗi mã icon tùy chọn, tối đa 50 ký tự (`FR-CAT-02`). |
| `color` | Mã màu hiển thị | Chuỗi mã màu Hex tùy chọn (ví dụ `#4CAF50`), tối đa 10 ký tự (`FR-CAT-02`). |
| `transactionType` | Loại thu chi (dẫn xuất) | Suy dẫn từ `needGroup.transactionType`, bất biến theo nhóm cha (`BR-FIN-04`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Tên danh mục duy nhất trong cùng nhóm cha đang hoạt động (`BR-NAM-01`, `ERR-NAM-002`).
  - Bắt buộc thuộc một `NeedGroup` hợp lệ (không có danh mục mồ côi, `BR-FIN-04`) và là cấp duy nhất được gán trực tiếp cho giao dịch (`FR-CAT-01`, `AC-CAT-01.2`).
- **Quy tắc vận hành**:
  - Xóa danh mục: Xóa vĩnh viễn nếu chưa phát sinh giao dịch; chuyển sang `DELETED` nếu đã có giao dịch lịch sử (`BR-FIN-04`, `FR-CAT-03`, `AC-CAT-03.1`).
  - Chuyển nhóm cha: Chỉ chuyển sang nhóm `ACTIVE` cùng `TransactionType` và không trùng tên tại nhóm đích (`ERR-CAT-001`, `ERR-CAT-003`, `ERR-NAM-002`).
  - Khôi phục: Chỉ khôi phục khi nhóm cha đang `ACTIVE` (nếu nhóm cha đã xóa phải khôi phục nhóm cha trước theo `ERR-CAT-004`) và không trùng tên với danh mục đang hoạt động trong nhóm (`ERR-NAM-002`).

### 2.3.6 Thực thể Nhóm nhu cầu - NeedGroup

Nhóm nhu cầu (`NeedGroup`) là đơn vị phân loại trung gian cấp 2 gom cụm các danh mục chi tiết có cùng tính chất tài chính, làm cơ sở quản lý ngân sách và phân tích cơ cấu thu chi theo nhóm.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `needGroupId` | Mã định danh nhóm | Chuỗi ID sinh tự động, bất biến và không tái sử dụng (`BR-FIN-04`, `FR-CAT-02`). |
| `name` | Tên nhóm nhu cầu | Từ 1–50 ký tự sau khi `trim`, không để trống (`BR-NAM-01`, `ERR-NAM-001`). Duy nhất trong cùng nhánh `TransactionType` đang hoạt động, không phân biệt hoa thường (`ERR-NAM-002`). Không dùng tên nhóm làm điều kiện cứng trong logic xử lý (`BR-FIN-04`). |
| `transactionType` | Loại thu chi gốc | Kiểu `TransactionType` (`INCOME` hoặc `EXPENSE`, `BR-FIN-04`, `FR-CAT-01`), bất biến sau khi tạo (`ERR-CAT-001`). |
| `status` | Trạng thái hoạt động | Nhận giá trị `ACTIVE` hoặc `DELETED` (`BR-FIN-04`, `FR-CAT-03`). Cấm xóa nhóm `ACTIVE` cuối cùng của mỗi loại thu chi (`ERR-CAT-002`). |
| `icon` | Biểu tượng | Chuỗi mã icon tùy chọn, tối đa 50 ký tự (`FR-CAT-02`). |
| `color` | Mã màu hiển thị | Chuỗi mã màu Hex tùy chọn, tối đa 10 ký tự (`FR-CAT-02`, `FR-REP-02`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Tên nhóm duy nhất trong cùng nhánh `TransactionType` đang hoạt động (`BR-NAM-01`, `ERR-NAM-002`); `transactionType` cố định vĩnh viễn (`ERR-CAT-001`).
  - Mỗi nhánh `INCOME` và `EXPENSE` phải duy trì tối thiểu một nhóm `ACTIVE` (`BR-FIN-04`, `FR-CAT-03`, `ERR-CAT-002`, `AC-CAT-03.3`).
- **Quy tắc vận hành**:
  - Xóa kéo theo (`cascade`): Khi xóa nhóm, các danh mục con đã có giao dịch chuyển sang xóa mềm, danh mục con chưa có giao dịch bị xóa hẳn; bản thân nhóm chuyển sang `DELETED` nếu có danh mục con chứa giao dịch, ngược lại xóa hẳn. Cần thông báo rõ số danh mục ẩn, số danh mục xóa hẳn và số giao dịch giữ lại (`BR-FIN-04`, `FR-CAT-03`).
  - Khôi phục: Giữ nguyên ID, kiểm tra không trùng tên với nhóm `ACTIVE` cùng nhánh (`ERR-NAM-002`). Chỉ khôi phục bản ghi nhóm, các danh mục con bên trong được người dùng khôi phục riêng khi cần (`BR-FIN-04`).

### 2.3.7 Thực thể Bản ghi điều chỉnh số dư - BalanceAdjustmentRecord

Bản ghi điều chỉnh số dư (`BalanceAdjustmentRecord`) ghi nhận sự kiện kiểm kê cân đối số dư ví về đúng số tiền thực tế. Thực thể lưu khoản chênh lệch bất biến để cập nhật số dư ví mà không phát sinh doanh thu hay chi phí trên sổ thu chi (`BR-FIN-02`).

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `recordId` | Mã định danh bản ghi | Chuỗi ID sinh tự động, bất biến và không tái sử dụng (`BR-FIN-02`, `FR-WAL-05`). |
| `walletId` | Mã ví điều chỉnh | Tham chiếu `Wallet` đang `ACTIVE`; từ chối ví `ARCHIVED` (`FR-WAL-05`, `AC-WAL-05.3`). |
| `actualBalance` | Số dư thực tế | Kiểu `Money`, bắt buộc $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`, `ERR-FIN-001`). |
| `previousBalance` | Số dư trước điều chỉnh | Kiểu `Money`, tính từ lịch sử dòng tiền ngay trước thời điểm kiểm kê (`BR-FIN-02`, `FR-WAL-05`). |
| `difference` | Chênh lệch điều chỉnh | Kiểu `Money` có dấu ($\text{actualBalance} - \text{previousBalance}$), bắt buộc $\neq 0.00$ VNĐ (`BR-FIN-01`, `AC-WAL-05.3`). |
| `reason` | Lý do điều chỉnh | Chuỗi tùy chọn, tối đa 255 ký tự sau khi `trim` (`CON-05`, `FR-WAL-05`, `AC-WAL-05.4`). |
| `adjustedAt` | Ngày điều chỉnh | Kiểu `TransactionDate` (`YYYY-MM-DD`), không vượt quá ngày hiện tại từ `DE-01` (`FR-WAL-05`). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Bất biến tuyệt đối (`Immutability`): Không được sửa hoặc xóa sau khi tạo (`BR-FIN-02`, `FR-WAL-05`).
  - Chỉ áp dụng cho ví `ACTIVE`, với $\text{actualBalance} \ge 0.00$ VNĐ (`ERR-WAL-001`) và $\text{difference} \neq 0.00$ VNĐ (`AC-WAL-05.3`).
- **Quy tắc vận hành**:
  - Đồng bộ số dư ví tức thời về đúng `actualBalance`, làm tổng tài sản hệ thống biến động đúng bằng `difference` mà không tính vào thu nhập, chi phí hay dòng tiền thuần (`BR-FIN-02`, `FR-WAL-05`, `FR-REP-01`).

## 2.4 Đặc tả chi tiết các đối tượng giá trị

Đối tượng giá trị không có định danh độc lập, được xác định hoàn toàn qua giá trị các thuộc tính, mang tính bất biến tuyệt đối (`Immutable`) và bảo đảm độ chính xác số học trong mọi phép tính tài chính.

### 2.4.1 Tiền tệ và độ chính xác số học - Money

`Money` là đối tượng giá trị đo lường tiền tệ bất biến, loại trừ sai số dấu chấm động nhị phân và bảo đảm độ chính xác số học cho giao dịch, số dư ví và báo cáo (`BR-FIN-01`, `CON-02`).

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `amount` | Giá trị số tiền | Số thập phân chính xác (tối thiểu 18 chữ số nguyên, 2 chữ số thập phân theo `CON-02`). Cấm dùng `float`/`double`; chỉ khởi tạo từ chuỗi thập phân hoặc số nguyên (`BR-FIN-01`, `ERR-FIN-001`). |
| `currency` | Đơn vị tiền tệ | Chuỗi chuẩn hóa, cố định là `VNĐ`. Các phép toán bắt buộc cùng đơn vị tiền tệ. |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Mọi phép toán số học trả về một thể hiện `Money` mới với quy mô cố định 2 chữ số thập phân (`Scale = 2`, `BR-FIN-01`, `CON-02`).
  - Hai đối tượng `Money` bằng nhau khi cùng đơn vị tiền tệ và cùng giá trị đại số (ví dụ `100.00 VNĐ` = `100.0 VNĐ`). Giá trị `Money` của số dư ví luôn $\ge 0.00$ VNĐ (`ERR-WAL-001`).
- **Quy tắc vận hành**:
  - Làm tròn ngân hàng (`Banker's Rounding` - làm tròn nửa chẵn) cho mọi phép chia không hết (`BR-FIN-01`, `ERR-FIN-002`).
  - Hiển thị số nguyên đồng trên giao diện nhưng lưu trữ và tính toán nội bộ đúng 2 chữ số thập phân (`CON-04`, `BR-FIN-01`).

### 2.4.2 Ngày giao dịch và mốc thời gian - TransactionDate

`TransactionDate` là đối tượng giá trị mốc thời gian bất biến, xác định ngày phát sinh sự kiện tài chính để đối soát lịch sử, lọc giao dịch và tổng hợp báo cáo 12 tháng.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `year` | Năm giao dịch | Số nguyên dương 4 chữ số theo lịch Gregory. |
| `month` | Tháng giao dịch | Số nguyên từ 1 đến 12. |
| `day` | Ngày trong tháng | Số nguyên từ 1 đến 31, khớp số ngày thực tế của tháng và năm (kể cả năm nhuận). |
| `isoString` | Chuỗi ngày (dẫn xuất) | Chuỗi ISO 8601 (`YYYY-MM-DD`), phục vụ hiển thị, lưu trữ JSON (`CON-04`) và đối soát. |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**: Tuân thủ lịch Gregory và không vượt quá ngày hiện tại từ `DE-01` khi ghi nhận (`FR-TXN-01`, `FR-TXN-02`, `FR-TRF-01`).
- **Quy tắc vận hành**: Hỗ trợ so sánh thứ tự thời gian để lọc khoảng ngày ($\text{startDate} \le \text{endDate}$, `FR-TXN-03`), gom cụm theo tháng (`YYYY-MM`) cho báo cáo 12 tháng (`FR-REP-01`) hoặc đoạn ngày cho báo cáo cơ cấu (`FR-REP-02`). Các giao dịch cùng `TransactionDate` được sắp xếp thứ cấp theo `createdAt`.

### 2.4.3 Loại giao dịch - TransactionType

`TransactionType` là đối tượng giá trị kiểu liệt kê bất biến, xác lập nhánh gốc cấp 1 của cây danh mục và chiều tác động của dòng tiền lên số dư ví.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `value` | Chiều dòng tiền | Enum đóng, chỉ nhận `INCOME` (tăng số dư) hoặc `EXPENSE` (giảm số dư). |

**Quy tắc bất biến và vận hành**

- **Quy tắc bất biến**:
  - Chỉ gồm 2 giá trị cố định vĩnh viễn là `INCOME` (dòng tiền vào) và `EXPENSE` (dòng tiền ra), đóng vai trò gốc phân loại cấp 1 (`BR-FIN-04`, `FR-CAT-01`).
  - Phạm vi duy nhất của tên `NeedGroup` được xét riêng trong từng nhánh `TransactionType` (`BR-NAM-01`). Nghiêm cấm đổi `TransactionType` của nhóm nhu cầu hoặc danh mục (`ERR-CAT-001`).
- **Quy tắc vận hành**: Định tuyến việc cộng (`INCOME`) hoặc trừ (`EXPENSE`) số dư ví (`BR-FIN-02`) và ràng buộc `TransactionFee` luôn thuộc nhánh `EXPENSE` (`ERR-FEE-003`).

### 2.4.4 Dòng tiền thuần - NetCashFlow

`NetCashFlow` là đối tượng giá trị tiền tệ tổng hợp bất biến, phản ánh thặng dư hoặc thâm hụt tài chính ròng trong một kỳ quan sát $M$.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `amount` | Số tiền ròng | Số thập phân chính xác (2 chữ số thập phân, không dùng `float`/`double`), mang giá trị âm, dương hoặc bằng 0. |
| `currency` | Đơn vị tiền tệ | Chuỗi chuẩn hóa, mặc định là `VNĐ`. |

**Công thức và quy tắc**

$$
\text{NetCashFlow}(M) = \sum_{t \in M} \text{Income}(t) - \sum_{t \in M} \text{Expense}(t)
$$

- $\sum_{t \in M} \text{Expense}(t)$ bao gồm cả các khoản phí giao dịch liên kết (`TransactionFee`) phát sinh trong kỳ.
- Ngữ nghĩa: $> 0$ là thặng dư, $< 0$ là thâm hụt (chi vượt thu), $= 0$ là cân bằng thu chi. Hiển thị số nguyên đồng trên giao diện hoặc 2 chữ số thập phân trên báo cáo chi tiết.

### 2.4.5 Tỷ lệ tiết kiệm - SavingsRate

`SavingsRate` là đối tượng giá trị tỷ lệ phần trăm bất biến, đo lường mức độ tích lũy từ dòng tiền thuần so với tổng thu nhập trong kỳ.

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `rate` | Giá trị tỷ lệ | Số thập phân chính xác; giữ tối thiểu 4 chữ số thập phân trung gian và làm tròn ngân hàng về 2 chữ số khi hiển thị. |
| `unit` | Đơn vị đo | Cố định là ký hiệu `%`. |

**Công thức và quy tắc**

$$
\text{SavingsRate} = \begin{cases}
\left(\dfrac{\text{NetCashFlow}}{\text{TotalIncome}}\right) \times 100\% & \text{if } \text{TotalIncome} > 0 \\
0.00\% & \text{if } \text{TotalIncome} \le 0
\end{cases}
$$

- Khi $\text{TotalIncome} \le 0$, mặc định bằng `0.00%` để tránh lỗi chia cho 0 (`BR-FIN-01`). Khi $\text{TotalIncome} > 0$ và $\text{NetCashFlow} < 0$, tỷ lệ mang giá trị âm phản ánh mức thâm hụt.

### 2.4.6 Tỷ trọng cơ cấu thu chi - SpendingStructureShare

`SpendingStructureShare` là đối tượng giá trị bất biến đo lường tỷ lệ đóng góp của từng nhóm nhu cầu cấp 2 hoặc danh mục cấp 3 trên tổng dòng tiền trong kỳ (`EXPENSE` gồm cả `TransactionFee`, hoặc `INCOME`).

**Cấu trúc thuộc tính**

| Thuộc tính | Ý nghĩa | Ràng buộc nghiệp vụ |
| :--- | :--- | :--- |
| `share` | Giá trị tỷ trọng | Số thập phân chính xác trong đoạn $[0.00, 100.00]$, giữ tối thiểu 4 chữ số thập phân trung gian và làm tròn ngân hàng về 2 chữ số. |
| `unit` | Đơn vị đo | Cố định là ký hiệu `%`. |
| `scope` | Phạm vi phân tích | Cấp nhóm nhu cầu (`NeedGroup`), danh mục trên nhóm cha, hoặc danh mục trên toàn kỳ (`Category`). |

**Công thức và quy tắc**

$$
\text{SpendingStructureShare}_i = \begin{cases}
\left(\dfrac{\text{ItemAmount}_i}{\text{TotalAmount}}\right) \times 100\% & \text{if } \text{TotalAmount} > 0 \\
0.00\% & \text{if } \text{TotalAmount} \le 0
\end{cases}
$$

- Khi $\text{TotalAmount} = 0.00$ VNĐ, gán tỷ trọng `0.00%` và trả về danh sách rỗng.
- Chỉ hiển thị các mục có $\text{ItemAmount} > 0.00$ VNĐ (`FR-REP-02`); mục đã xóa mềm vẫn giữ tên gốc kèm nhãn "(đã xóa)".
- Khi $\text{TotalAmount} > 0$, tổng tỷ trọng các thành phần cùng cấp xấp xỉ `100.00%` ($\sum_{i=1}^{n} \text{SpendingStructureShare}(i) \approx 100.00\%$, sai số tối đa $\pm 0.005\% \times n$ do làm tròn ngân hàng độc lập từng phần tử theo `BR-FIN-01`).

## 2.5 Các mối quan hệ ngữ nghĩa và bất biến miền

Phần này tổng hợp 3 mối quan hệ ngữ nghĩa cốt lõi chi phối sự tương tác và toàn vẹn dữ liệu giữa các thực thể trong mô hình miền.

### 2.5.1 Quan hệ sở hữu ví và bảo toàn số dư

Thiết lập liên kết một-nhiều (1:N) giữa một `Wallet` với các dòng tiền tác động lên nó (`IncomeExpenseTransaction`, `TransferTransaction`, `BalanceAdjustmentRecord`), chi phối chính sách bảo toàn tài sản (`BR-FIN-02`, `FR-WAL-01`–`FR-WAL-05`):
- **Bảo toàn số dư không âm**: Số dư ví không được gán trực tiếp mà tính theo thời gian thực từ lịch sử dòng tiền ($\sum \text{Inflow} - \sum \text{Outflow} \ge 0.00$ VNĐ); từ chối mọi thao tác xuất quỹ, điều chỉnh hoặc hoàn nguyên làm âm số dư (`ERR-WAL-001`, chi tiết tại mục **2.3.1**).
- **Bảo toàn tổng tài sản hệ thống**: Tổng số dư toàn hệ thống chỉ biến động khi phát sinh thu chi, phí giao dịch hoặc chênh lệch kiểm kê bất biến (`BalanceAdjustmentRecord`); chuyển tiền nội bộ thuần túy luôn giữ nguyên tổng tài sản (`BR-FIN-02`, `BR-FIN-03`).

### 2.5.2 Quan hệ phân cấp danh mục 3 cấp

Thiết lập quan hệ hợp thành 3 tầng nghiêm ngặt: `TransactionType` (cấp 1, cố định) $\rightarrow$ `NeedGroup` (cấp 2, nhóm nhu cầu) $\rightarrow$ `Category` (cấp 3, danh mục chi tiết) (`BR-FIN-04`, `FR-CAT-01`–`FR-CAT-03`):
- **Toàn vẹn cấu trúc và liên kết**: Giao dịch thu chi và phí chỉ được liên kết vào cấp 3 (`Category`, `AC-CAT-01.2`). Không tồn tại danh mục mồ côi (`ERR-CAT-004`, `ERR-CAT-005`); mỗi nhánh `TransactionType` cố định và luôn phải duy trì tối thiểu một `NeedGroup` đang hoạt động (`ERR-CAT-001`, `ERR-CAT-002`).
- **Đồng bộ xóa và chuyển nhóm**: Xóa `NeedGroup` sẽ tự động xóa kéo theo các `Category` con (xóa mềm nếu đã có giao dịch để giữ nguyên dữ liệu lịch sử kèm nhãn "(đã xóa)", hoặc xóa vĩnh viễn nếu chưa có giao dịch theo `AC-REP-02.3`). Chuyển nhóm cha chỉ hợp lệ trong cùng `TransactionType` và tới nhóm đang `ACTIVE` (`ERR-CAT-003`, chi tiết tại mục **2.3.5** và **2.3.6**).

### 2.5.3 Quan hệ phụ thuộc sinh tử giữa giao dịch gốc và phí

Thiết lập quan hệ phụ thuộc sinh tử (`Composition`) giữa giao dịch gốc (`IncomeExpenseTransaction` hoặc `TransferTransaction`) và khoản phí đi kèm (`TransactionFee`), tách bạch chi phí bên thứ ba khỏi dòng tiền cốt lõi (`BR-FIN-02`, `BR-FIN-03`, `FR-TXN-01`, `FR-TXN-02`, `FR-TRF-01`):
- **Loại trừ phí mồ côi**: `TransactionFee` luôn gắn với `parentTransactionId`, chỉ được tạo khi phí $> 0.00$ VNĐ kèm một `Category` cấp 3 thuộc nhánh `EXPENSE` (`ERR-FEE-001`–`ERR-FEE-003`), và hạch toán như một khoản chi tiêu độc lập trên báo cáo (`FR-REP-01`, `AC-TXN-03.3`).
- **Đồng bộ vòng đời nguyên tử**: Bản ghi phí được tự động khởi tạo, cập nhật (hoặc xóa khi phí về 0) và xóa kèm hoàn tiền về ví đồng thời với thao tác tạo, sửa, xóa giao dịch gốc (`FR-TXN-04`, `AC-TXN-05.3`, `AC-TRF-02.1`, chi tiết tại mục **2.3.3** và **2.3.4**).

\newpage
# 3. Sơ đồ tuần tự hệ thống

Sơ đồ tuần tự hệ thống (SSD) mô tả trình tự tương tác theo thời gian giữa người dùng và hệ thống cho từng kịch bản ca sử dụng. Ở giai đoạn phân tích, sơ đồ chuyển hóa các bước nghiệp vụ thành các sự kiện đầu vào tường minh, làm cơ sở xác định hợp đồng thao tác tại mục 4 trước khi thiết kế chi tiết lớp.

## 3.1 Nguyên tắc mô hình hóa và ranh giới tương tác hệ thống

Ở tầng phân tích, hệ thống được xem như một hộp đen thống nhất, chỉ xét thông tin người dùng gửi vào và kết quả hệ thống phản hồi mà không đi vào cấu trúc nội bộ (bộ điều khiển, dịch vụ, lớp dữ liệu hay cơ sở dữ liệu).

Yêu cầu từ người dùng được chuẩn hóa thành các thao tác hệ thống dạng động từ kết hợp danh từ (như `createWallet`, `recordIncomeExpense`, `transferFunds`) với tham số mang ý nghĩa nghiệp vụ, độc lập với ngôn ngữ lập trình. Hệ thống kiểm tra các quy tắc về số dư và tính hợp lệ dữ liệu để phản hồi thành công hoặc từ chối kèm mã lỗi tương ứng.

## 3.2 Tổng hợp thao tác hệ thống theo nhóm chức năng

Hệ thống PFMS cung cấp 20 thao tác tương ứng với 20 ca sử dụng thuộc 6 nhóm chức năng nhằm truy vấn hoặc thay đổi trạng thái các thực thể tài chính:

| STT | Nhóm chức năng | Mã nhóm | Ca sử dụng | Thao tác hệ thống |
| :-: | :-------- | :-: | :-------- | :----------------- |
| 1 | Quản lý ví | `WAL` | `UC-WAL-01` – `UC-WAL-04` | `createWallet`, `getWalletsSummary`, `updateWallet`, `adjustBalance` |
| 2 | Ghi nhận thu chi | `TXN` | `UC-TXN-01` – `UC-TXN-04` | `recordIncomeExpense`, `filterTransactions`, `updateTransaction`, `deleteTransaction` |
| 3 | Chuyển tiền nội bộ | `TRF` | `UC-TRF-01` – `UC-TRF-03` | `transferFunds`, `filterTransfers`, `deleteTransfer` |
| 4 | Danh mục phân loại | `CAT` | `UC-CAT-01` – `UC-CAT-04` | `addCategory`, `renameCategory`, `restoreCategory`, `deleteCategory` |
| 5 | Thống kê và báo cáo | `REP` | `UC-REP-01`, `UC-REP-02` | `generateCashFlowReport`, `generateSpendingStructureReport` |
| 6 | Lưu trữ và sao lưu | `DAT` | `UC-DAT-01` – `UC-DAT-03` | `exportBackupData`, `restoreBackupData`, `reconcileSystemData` |

Chi tiết tham số, phản hồi và sơ đồ tuần tự của từng thao tác được trình bày từ mục 3.3 đến mục 3.8.

## 3.3 Nhóm chức năng Quản lý ví (WAL)

Nhóm chức năng Quản lý ví quản lý các nguồn tiền thanh toán, theo dõi số dư khả dụng và xử lý chênh lệch kiểm kê, tuân thủ quy tắc đặt tên duy nhất (`BR-NAM-01`) và bảo toàn số dư không âm (`BR-FIN-02`).

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---- | :-------- | :---------- | :------------ | :----- |
| `UC-WAL-01` | Tạo ví mới | `createWallet` | Khởi tạo nguồn tiền mới với số dư ban đầu bằng 0 | Ghi nhận |
| `UC-WAL-02` | Theo dõi số dư tính lại và cơ cấu ví | `getWalletsSummary` | Tính số dư khả dụng tức thời và tỷ lệ phân bổ ví | Truy vấn |
| `UC-WAL-03` | Cập nhật thông tin ví | `updateWallet` | Sửa tên, ghi chú hoặc chuyển đổi trạng thái lưu trữ ví | Cập nhật |
| `UC-WAL-04` | Điều chỉnh số dư ví | `adjustBalance` | Ghi nhận mốc chênh lệch kiểm kê thực tế | Ghi nhận |

### 3.3.1 Tạo ví mới

Người dùng yêu cầu khởi tạo ví mới kèm tên và ghi chú. Hệ thống kiểm tra tên ví (`BR-NAM-01`), tạo ví với số dư khởi điểm `0.00 VNĐ` (`BR-FIN-02`) và tính lại cơ cấu phân bổ ví (`FR-WAL-04`).

- **Thao tác hệ thống**: `createWallet(walletName, note)`
- **Kết quả phản hồi**: Khởi tạo ví ở trạng thái `ACTIVE` với số dư `0.00 VNĐ` (`BR-FIN-02`), cập nhật cơ cấu số dư và trả về thông tin ví mới; từ chối nếu tên sai định dạng (`ERR-NAM-001`), trùng tên (`ERR-NAM-002`) hoặc ghi chú quá 255 ký tự.

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `walletName` | Tên ví mới | Chuỗi 1–50 ký tự sau khi `trim`, không rỗng (`ERR-NAM-001`), duy nhất toàn hệ thống (cả ví `ACTIVE` và `ARCHIVED`, không phân biệt hoa thường) (`BR-NAM-01`, `ERR-NAM-002`). |
| `note` | Ghi chú ví | Chuỗi tùy chọn, tối đa 255 ký tự sau khi `trim` (`FR-WAL-01`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự tạo ví](docs/object-oriented-analysis/images/so-do-tuan-tu-tao-vi.png){width=80%}

### 3.3.2 Xem tổng hợp số dư và cơ cấu ví

Người dùng truy vấn danh sách ví để xem số dư khả dụng và tỷ trọng phân bổ tài sản. Hệ thống tính lại số dư từng ví từ lịch sử dòng tiền (`BR-FIN-02`) và làm tròn tỷ lệ phân bổ (`BR-FIN-01`, `FR-WAL-04`).

- **Thao tác hệ thống**: `getWalletsSummary()` (không có tham số đầu vào)
- **Kết quả phản hồi**: Trả về danh sách ví kèm số dư tính lại (`BR-FIN-02`), tổng số dư hệ thống và tỷ lệ phân bổ từng ví làm tròn ngân hàng 2 chữ số thập phân (`BR-FIN-01`); gán `0.00%` cho mọi ví nếu tổng số dư bằng `0.00 VNĐ` (`ERR-FIN-002`). Thao tác chỉ đọc dữ liệu.

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự xem tổng quan ví](docs/object-oriented-analysis/images/so-do-tuan-tu-tong-quan-vi.png){width=80%}

### 3.3.3 Cập nhật thông tin ví

Người dùng yêu cầu sửa tên hoặc ghi chú của ví hiện có. Hệ thống kiểm tra tính hợp lệ (`BR-NAM-01`) và cập nhật thông tin mà không làm thay đổi số dư hay lịch sử giao dịch (`BR-FIN-02`).

- **Thao tác hệ thống**: `updateWallet(walletId, walletName, note)`
- **Kết quả phản hồi**: Cập nhật tên và ghi chú mới, giữ nguyên `walletId`, số dư và giao dịch liên kết (`BR-FIN-02`); từ chối nếu không tìm thấy ví, tên sai định dạng (`ERR-NAM-001`), trùng tên (`ERR-NAM-002`) hoặc ghi chú quá 255 ký tự.

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `walletId` | Mã định danh ví | Bắt buộc tồn tại trong danh sách ví (`FR-WAL-03`). |
| `walletName` | Tên gọi mới | Chuỗi 1–50 ký tự sau khi `trim`, không rỗng (`ERR-NAM-001`), duy nhất toàn hệ thống, không phân biệt hoa thường (`BR-NAM-01`, `ERR-NAM-002`). |
| `note` | Ghi chú mới | Chuỗi tùy chọn, tối đa 255 ký tự sau khi `trim` (`FR-WAL-03`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự cập nhật ví](docs/object-oriented-analysis/images/so-do-tuan-tu-cap-nhat-vi.png){width=80%}

### 3.3.4 Điều chỉnh số dư ví thực tế

Người dùng đối chiếu tiền thực tế với phần mềm và yêu cầu điều chỉnh khi có chênh lệch. Hệ thống tính mức chênh lệch (`BR-FIN-01`), tạo bản ghi điều chỉnh bất biến và đồng bộ số dư ví theo giá trị thực tế (`BR-FIN-02`).

- **Thao tác hệ thống**: `adjustBalance(walletId, actualBalance, reason)`
- **Kết quả phản hồi**: Tạo bản ghi `BalanceAdjustmentRecord` bất biến, đồng bộ số dư ví bằng `actualBalance` (`BR-FIN-02`) và trả về bản ghi vừa tạo; từ chối nếu ví không `ACTIVE`, sai định dạng số (`ERR-FIN-001`), số dư thực tế âm (`ERR-WAL-001`), chênh lệch bằng `0.00 VNĐ` hoặc lý do quá 255 ký tự.

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `walletId` | Mã ví cần điều chỉnh | Tồn tại và đang ở trạng thái `ACTIVE` (`FR-WAL-05`). |
| `actualBalance` | Số dư thực tế | Số thập phân 2 chữ số (`BR-FIN-01`, `ERR-FIN-001`), $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`) và khác số dư hiện tại của ví (`FR-WAL-05`). |
| `reason` | Lý do chênh lệch | Chuỗi tùy chọn, tối đa 255 ký tự sau khi `trim` (`FR-WAL-05`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự điều chỉnh số dư ví](docs/object-oriented-analysis/images/so-do-tuan-tu-dieu-chinh-so-du.png){width=80%}

## 3.4 Nhóm chức năng Ghi nhận thu chi (TXN)

Nhóm chức năng Ghi nhận thu chi hạch toán các khoản thu nhập, chi tiêu kèm phí phát sinh, đồng thời kiểm soát việc sửa, xóa giao dịch nhằm bảo toàn số dư ví không âm (`BR-FIN-02`) và phân cấp danh mục 3 cấp (`BR-FIN-04`).

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---- | :-------- | :---------- | :------------ | :----- |
| `UC-TXN-01` | Ghi nhận giao dịch thu chi | `recordIncomeExpense` | Ghi nhận khoản thu/chi kèm chi phí liên kết (nếu có) | Ghi nhận |
| `UC-TXN-02` | Tra cứu và lọc lịch sử giao dịch thu chi | `filterTransactions` | Trích xuất danh sách thu chi theo thời gian, ví và danh mục | Truy vấn |
| `UC-TXN-03` | Chỉnh sửa giao dịch thu chi | `updateTransaction` | Điều chỉnh thuộc tính giao dịch, hoàn nguyên tác động cũ và kiểm soát số dư | Cập nhật |
| `UC-TXN-04` | Xóa giao dịch thu chi | `deleteTransaction` | Xóa giao dịch, hoàn nguyên số dư ví và xóa chi phí kèm theo | Xóa |

### 3.4.1 Ghi nhận giao dịch thu chi

Người dùng gửi thông tin khoản thu/chi kèm ví thanh toán, danh mục và phí phát sinh (nếu có). Hệ thống kiểm tra danh mục cấp 3 (`BR-FIN-04`), số dư ví (`BR-FIN-02`) và ghi nhận đồng thời giao dịch gốc cùng bản ghi phí liên kết.

- **Thao tác hệ thống**: `recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- **Kết quả phản hồi**: Tạo `IncomeExpenseTransaction`, tạo `TransactionFee` liên kết nếu $\text{feeAmount} > 0.00$ VNĐ, cập nhật số dư ví (`BR-FIN-02`) và trả về giao dịch vừa lưu; từ chối nếu số tiền sai định dạng hoặc $\le 0$ (`ERR-FIN-001`), phí âm (`ERR-FEE-001`), thiếu danh mục phí (`ERR-FEE-002`), danh mục phí sai loại/đã xóa (`ERR-FEE-003`), ví không đủ số dư (`ERR-WAL-001`), hoặc ví/danh mục không hợp lệ (`BR-FIN-04`).

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `walletId` | Mã ví thanh toán | Tồn tại và đang `ACTIVE` (`FR-TXN-01`, `FR-TXN-02`). Số dư sau giao dịch và phí phải $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`). |
| `categoryId` | Mã danh mục cấp 3 | Đang `ACTIVE`, thuộc nhánh `INCOME` hoặc `EXPENSE` (`BR-FIN-04`). |
| `amount` | Số tiền giao dịch | Số thập phân 2 chữ số, $> 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`). |
| `transactionDate` | Ngày giao dịch | Định dạng `YYYY-MM-DD`, không vượt quá ngày hiện tại (`FR-TXN-01`, `FR-TXN-02`). |
| `note` | Ghi chú | Chuỗi tùy chọn, tối đa 255 ký tự sau khi `trim` (`FR-TXN-01`, `FR-TXN-02`). |
| `feeAmount` | Số tiền phí | Số thập phân 2 chữ số (`BR-FIN-01`), $\ge 0.00$ VNĐ, mặc định `0.00 VNĐ` (`BR-FIN-02`, `ERR-FEE-001`). |
| `feeCategoryId` | Mã danh mục phí | Tùy chọn khi $\text{feeAmount} = 0.00$ VNĐ, bắt buộc khi $\text{feeAmount} > 0.00$ VNĐ (`ERR-FEE-002`); phải là danh mục cấp 3 đang `ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-04`, `ERR-FEE-003`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự ghi nhận thu chi](docs/object-oriented-analysis/images/so-do-tuan-tu-ghi-nhan-thu-chi.png){width=80%}

### 3.4.2 Tra cứu và lọc lịch sử giao dịch

Người dùng gửi tiêu chí lọc theo khoảng thời gian, ví và danh mục. Hệ thống lọc sổ giao dịch và tổng hợp tổng thu, tổng chi (gồm cả phí) cùng dòng tiền thuần (`BR-FIN-01`).

- **Thao tác hệ thống**: `filterTransactions(startDate, endDate, walletId, categoryId)`
- **Kết quả phản hồi**: Trả về danh sách giao dịch thu chi và phí thỏa mãn bộ lọc sắp xếp theo ngày giảm dần (kèm nhãn "(đã xóa)" nếu danh mục đã xóa mềm), cùng tổng thu, tổng chi và dòng tiền thuần (`BR-FIN-01`); từ chối nếu $\text{startDate} > \text{endDate}$. Thao tác chỉ đọc dữ liệu.

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `startDate` | Ngày bắt đầu | Định dạng `YYYY-MM-DD`, $\le \text{endDate}$ (`FR-TXN-03`). |
| `endDate` | Ngày kết thúc | Định dạng `YYYY-MM-DD`, $\ge \text{startDate}$ (`FR-TXN-03`). |
| `walletId` | Mã ví cần lọc | Tùy chọn; để trống để lấy toàn bộ ví (`FR-TXN-03`). |
| `categoryId` | Mã nhóm hoặc danh mục | Tùy chọn; nhận mã `NeedGroup` hoặc `Category` (kể cả mục đã `DELETED`) (`BR-FIN-04`, `FR-TXN-03`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự lọc giao dịch](docs/object-oriented-analysis/images/so-do-tuan-tu-loc-giao-dich.png){width=80%}

### 3.4.3 Chỉnh sửa giao dịch thu chi

Người dùng yêu cầu sửa thông tin một giao dịch thu chi đã lưu. Hệ thống hoàn nguyên tác động cũ trên ví ban đầu và áp dụng thông số mới lên ví đích trong cùng một bước nguyên tử, đảm bảo số dư các ví không âm (`BR-FIN-02`).

- **Thao tác hệ thống**: `updateTransaction(transactionId, walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- **Kết quả phản hồi**: Hoàn nguyên số dư ví cũ, cập nhật số dư ví mới (`BR-FIN-02`), cập nhật giao dịch gốc và đồng bộ (tạo/sửa/xóa) `TransactionFee` liên kết; từ chối và giữ nguyên dữ liệu cũ nếu sai định dạng tiền (`ERR-FIN-001`), phí âm (`ERR-FEE-001`), thiếu/sai danh mục phí (`ERR-FEE-002`, `ERR-FEE-003`), làm âm số dư ví (`ERR-WAL-001`), hoặc khi giao dịch không tồn tại, sửa trực tiếp bản ghi phí hay đổi loại thu chi (`BR-FIN-04`).

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `transactionId` | Mã giao dịch cần sửa | Giao dịch gốc tồn tại trong sổ thu chi (`FR-TXN-04`), không phải bản ghi phí độc lập (`BR-FIN-02`). |
| `walletId` | Mã ví thanh toán mới | Tồn tại và đang `ACTIVE`. Số dư ví cũ sau hoàn nguyên và ví mới sau cập nhật phải $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`). |
| `categoryId` | Mã danh mục cấp 3 mới | Cùng nhánh `TransactionType` với giao dịch gốc (`BR-FIN-04`). Phải đang `ACTIVE` nếu chọn mới, hoặc giữ nguyên danh mục cũ đã xóa mềm (`FR-TXN-04`). |
| `amount` | Số tiền mới | Số thập phân 2 chữ số, $> 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`). |
| `transactionDate` | Ngày giao dịch mới | Định dạng `YYYY-MM-DD`, không vượt quá ngày hiện tại (`FR-TXN-04`). |
| `note` | Ghi chú mới | Chuỗi tùy chọn, tối đa 255 ký tự sau khi `trim` (`FR-TXN-04`). |
| `feeAmount` | Số tiền phí mới | Số thập phân 2 chữ số (`BR-FIN-01`), $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-FEE-001`). |
| `feeCategoryId` | Mã danh mục phí mới | Bắt buộc khi $\text{feeAmount} > 0.00$ VNĐ (`ERR-FEE-002`); thuộc nhánh `EXPENSE` và đang `ACTIVE` (hoặc giữ nguyên danh mục phí cũ đã xóa mềm) (`FR-TXN-04`, `ERR-FEE-003`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự chỉnh sửa giao dịch](docs/object-oriented-analysis/images/so-do-tuan-tu-chinh-sua-giao-dich.png){width=80%}

### 3.4.4 Xóa giao dịch thu chi

Người dùng yêu cầu xóa một giao dịch thu chi. Hệ thống kiểm tra số dư hoàn nguyên (`BR-FIN-02`), xóa đồng thời giao dịch gốc cùng bản ghi phí liên kết và khôi phục số dư ví.

- **Thao tác hệ thống**: `deleteTransaction(transactionId)`
- **Kết quả phản hồi**: Xóa vĩnh viễn giao dịch gốc và `TransactionFee` đi kèm, hoàn nguyên số dư ví (`BR-FIN-02`); từ chối nếu không tìm thấy giao dịch, xóa trực tiếp bản ghi phí độc lập, hoặc việc trừ lại khoản thu làm âm số dư ví (`ERR-WAL-001`).

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `transactionId` | Mã giao dịch cần xóa | Giao dịch gốc tồn tại (`FR-TXN-05`), không phải bản ghi phí độc lập. Nếu xóa giao dịch `INCOME`, số dư ví sau khi trừ khoản thu và hoàn phí phải $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự xóa giao dịch](docs/object-oriented-analysis/images/so-do-tuan-tu-xoa-giao-dich.png){width=80%}

## 3.5 Nhóm chức năng Chuyển tiền nội bộ (TRF)

Nhóm chức năng Chuyển tiền nội bộ điều chuyển tiền giữa các ví của người dùng theo cơ chế ghi nhận đồng thời (trừ ví nguồn, cộng ví đích kèm phí phát sinh theo `BR-FIN-03`) và bảo toàn số dư không âm (`BR-FIN-02`).

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---- | :-------- | :---------- | :------------ | :----- |
| `UC-TRF-01` | Chuyển tiền nội bộ giữa hai ví | `transferFunds` | Điều chuyển tiền giữa hai ví theo cơ chế ghi nhận đồng thời kèm phí (nếu có) | Ghi nhận |
| `UC-TRF-02` | Tra cứu lịch sử chuyển tiền | `filterTransfers` | Trích xuất lịch sử chuyển tiền nội bộ và tổng chi phí phát sinh | Truy vấn |
| `UC-TRF-03` | Xóa giao dịch chuyển tiền | `deleteTransfer` | Hủy giao dịch chuyển tiền, hoàn nguyên đồng thời số dư hai ví và xóa phí | Xóa |

### 3.5.1 Chuyển tiền giữa hai ví

Người dùng yêu cầu chuyển tiền từ ví nguồn sang ví đích kèm phí chuyển tiền (nếu có). Hệ thống kiểm tra hai ví phân biệt (`BR-FIN-03`) và số dư ví nguồn (`BR-FIN-02`), sau đó trừ ví nguồn, cộng ví đích đồng thời và tạo bản ghi phí liên kết.

- **Thao tác hệ thống**: `transferFunds(sourceWalletId, destinationWalletId, amount, transferDate, note, feeAmount, feeCategoryId)`
- **Kết quả phản hồi**: Tạo `TransferTransaction`, trừ đồng thời $\text{amount} + \text{feeAmount}$ ở ví nguồn, cộng `amount` vào ví đích (`BR-FIN-03`), tạo `TransactionFee` (loại `EXPENSE`) nếu $\text{feeAmount} > 0.00$ VNĐ và trả về số dư mới của hai ví; từ chối nếu trùng ví nguồn và đích (`ERR-TRF-001`), số tiền chuyển $\le 0$ (`ERR-TRF-002`), sai định dạng số (`ERR-FIN-001`), phí âm (`ERR-FEE-001`), thiếu/sai danh mục phí (`ERR-FEE-002`, `ERR-FEE-003`) hoặc ví nguồn không đủ số dư (`ERR-WAL-001`).

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `sourceWalletId` | Mã ví nguồn | Tồn tại, đang `ACTIVE` (`BR-FIN-03`) và có số dư $\ge \text{amount} + \text{feeAmount}$ (`BR-FIN-02`, `ERR-WAL-001`). |
| `destinationWalletId` | Mã ví đích | Tồn tại, đang `ACTIVE` và khác `sourceWalletId` (`BR-FIN-03`, `ERR-TRF-001`). |
| `amount` | Số tiền chuyển | Số thập phân 2 chữ số (`BR-FIN-01`, `ERR-FIN-001`), $> 0.00$ VNĐ (`BR-FIN-03`, `ERR-TRF-002`). |
| `transferDate` | Ngày chuyển tiền | Định dạng `YYYY-MM-DD`, không vượt quá ngày hiện tại (`FR-TRF-01`). |
| `note` | Ghi chú | Chuỗi tùy chọn, tối đa 255 ký tự sau khi `trim` (`FR-TRF-01`). |
| `feeAmount` | Số tiền phí chuyển | Số thập phân 2 chữ số (`BR-FIN-01`), $\ge 0.00$ VNĐ, mặc định `0.00 VNĐ` (`BR-FIN-03`, `ERR-FEE-001`). |
| `feeCategoryId` | Mã danh mục phí | Tùy chọn khi $\text{feeAmount} = 0.00$ VNĐ, bắt buộc khi $\text{feeAmount} > 0.00$ VNĐ (`ERR-FEE-002`); phải là danh mục cấp 3 đang `ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-04`, `ERR-FEE-003`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự chuyển tiền giữa các ví](docs/object-oriented-analysis/images/so-do-tuan-tu-chuyen-tien.png){width=80%}

### 3.5.2 Tra cứu lịch sử chuyển tiền

Người dùng truy vấn lịch sử chuyển tiền nội bộ theo khoảng thời gian, ví nguồn hoặc ví đích. Hệ thống lọc sổ chuyển tiền kèm phí liên kết và trả về danh sách sắp xếp theo ngày giảm dần (`FR-TRF-02`).

- **Thao tác hệ thống**: `filterTransfers(startDate, endDate, sourceWalletId, destinationWalletId)`
- **Kết quả phản hồi**: Trả về danh sách bản ghi chuyển tiền thỏa mãn bộ lọc sắp xếp theo ngày giảm dần kèm tổng tiền chuyển và tổng chi phí (`BR-FIN-01`); từ chối nếu $\text{startDate} > \text{endDate}$. Thao tác chỉ đọc dữ liệu.

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `startDate` | Ngày bắt đầu | Tùy chọn, định dạng `YYYY-MM-DD`, $\le \text{endDate}$ (`FR-TRF-02`). |
| `endDate` | Ngày kết thúc | Tùy chọn, định dạng `YYYY-MM-DD`, $\ge \text{startDate}$ (`FR-TRF-02`). |
| `sourceWalletId` | Mã ví nguồn | Tùy chọn; để trống để lấy toàn bộ ví nguồn (`FR-TRF-02`). |
| `destinationWalletId` | Mã ví đích | Tùy chọn; để trống để lấy toàn bộ ví đích (`FR-TRF-02`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự lọc lịch sử chuyển tiền](docs/object-oriented-analysis/images/so-do-tuan-tu-loc-chuyen-tien.png){width=80%}

### 3.5.3 Xóa giao dịch chuyển tiền

Người dùng yêu cầu xóa một giao dịch chuyển tiền để khôi phục số dư hai ví. Hệ thống kiểm tra số dư ví đích (`BR-FIN-02`), xóa đồng thời bản ghi chuyển tiền cùng phí liên kết và hoàn nguyên số dư hai ví (`BR-FIN-03`).

- **Thao tác hệ thống**: `deleteTransfer(transferId)`
- **Kết quả phản hồi**: Xóa vĩnh viễn `TransferTransaction` và `TransactionFee` liên kết, hoàn nguyên đồng thời hai đầu ví (`BR-FIN-02`, `BR-FIN-03`); từ chối nếu giao dịch không tồn tại hoặc ví đích không đủ số dư để thu hồi (`ERR-WAL-001`).

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `transferId` | Mã chuyển tiền cần xóa | Tồn tại trong hệ thống (`FR-TRF-02`). Số dư hiện tại của ví đích phải $\ge \text{amount}$ để không làm âm ví đích khi thu hồi (`BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự xóa giao dịch chuyển tiền](docs/object-oriented-analysis/images/so-do-tuan-tu-xoa-chuyen-tien.png){width=80%}

## 3.6 Nhóm chức năng Danh mục phân loại (CAT)

Nhóm chức năng Danh mục phân loại quản lý cây phân loại thu chi 3 cấp (`BR-FIN-04`), bảo đảm tính duy nhất của tên gọi (`BR-NAM-01`) và bảo toàn lịch sử giao dịch thông qua cơ chế xóa mềm.

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---- | :-------- | :---------- | :------------ | :----- |
| `UC-CAT-01` | Bổ sung danh mục phân loại mới | `addCategory` | Tạo mới danh mục chi tiết hoặc nhóm nhu cầu trong cây phân cấp | Ghi nhận |
| `UC-CAT-02` | Đổi tên danh mục | `renameCategory` | Đổi tên hiển thị của danh mục hoặc nhóm nhu cầu | Cập nhật |
| `UC-CAT-03` | Khôi phục hoặc tạo lại mục đã xóa | `restoreCategory` | Phục hồi trạng thái hoạt động cho mục đã xóa mềm | Cập nhật |
| `UC-CAT-04` | Xóa danh mục hoặc nhóm | `deleteCategory` | Xóa vĩnh viễn mục chưa có dữ liệu hoặc xóa mềm nếu đã có giao dịch | Xóa |

### 3.6.1 Thêm danh mục phân loại mới

Người dùng yêu cầu thêm một danh mục chi tiết (cấp 3) vào nhóm nhu cầu đang hoạt động (cấp 2), hoặc thêm một nhóm nhu cầu (cấp 2) vào nhánh loại thu chi (cấp 1).

- **Thao tác hệ thống**: `addCategory(categoryName, parentCategoryId)`
- **Kết quả phản hồi**: Khởi tạo bản ghi mới ở trạng thái `ACTIVE`, kế thừa loại thu chi từ nhánh cha và trả về cây danh mục đã cập nhật; từ chối nếu tên sai định dạng/rỗng (`ERR-NAM-001`), trùng tên trong cùng cấp cha (`ERR-NAM-002`) hoặc nhóm cha đã bị xóa (`ERR-CAT-005`).

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `categoryName` | Tên danh mục/nhóm mới | Chuỗi 1–50 ký tự sau khi `trim`, không rỗng (`ERR-NAM-001`), duy nhất trong cùng nhóm cha hoặc cùng loại thu chi (`BR-NAM-01`, `ERR-NAM-002`). |
| `parentCategoryId` | Mã nhóm cha hoặc loại thu chi | Mã hợp lệ của `NeedGroup` (khi tạo cấp 3) hoặc `TransactionType` (`INCOME`/`EXPENSE`, khi tạo cấp 2). Nhóm cha phải đang `ACTIVE` (`BR-FIN-04`, `ERR-CAT-005`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự thêm danh mục](docs/object-oriented-analysis/images/so-do-tuan-tu-them-danh-muc.png){width=80%}

### 3.6.2 Đổi tên danh mục

Người dùng yêu cầu đổi tên hiển thị của một danh mục chi tiết hoặc nhóm nhu cầu mà không làm thay đổi các liên kết giao dịch lịch sử.

- **Thao tác hệ thống**: `renameCategory(categoryId, newName)`
- **Kết quả phản hồi**: Cập nhật tên mới, giữ nguyên ID, trạng thái và các giao dịch liên kết; từ chối nếu không tìm thấy mục, tên sai định dạng (`ERR-NAM-001`) hoặc trùng tên trong cùng phạm vi cha (`ERR-NAM-002`).

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `categoryId` | Mã danh mục/nhóm | Bắt buộc tồn tại trong cây phân cấp danh mục (`BR-FIN-04`). |
| `newName` | Tên gọi mới | Chuỗi 1–50 ký tự sau khi `trim`, không rỗng (`ERR-NAM-001`), duy nhất trong cùng nhóm cha (với cấp 3) hoặc cùng loại thu chi (với cấp 2) (`BR-NAM-01`, `ERR-NAM-002`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự đổi tên danh mục](docs/object-oriented-analysis/images/so-do-tuan-tu-doi-ten-danh-muc.png){width=80%}

### 3.6.3 Khôi phục danh mục đã xóa

Người dùng phục hồi trạng thái hoạt động cho một danh mục chi tiết hoặc nhóm nhu cầu đã xóa mềm để tiếp tục sử dụng cho các giao dịch mới.

- **Thao tác hệ thống**: `restoreCategory(categoryId)`
- **Kết quả phản hồi**: Chuyển trạng thái từ `DELETED` sang `ACTIVE` và đưa trở lại danh sách chọn; từ chối nếu nhóm cha đang ở trạng thái đã xóa (`ERR-CAT-004`) hoặc tên mục khôi phục bị trùng với một mục đang `ACTIVE` cùng nhóm (`ERR-NAM-002`).

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `categoryId` | Mã mục cần khôi phục | Tồn tại và đang ở trạng thái xóa mềm (`DELETED`); giữ nguyên ID cũ khi khôi phục (`BR-FIN-04`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự khôi phục danh mục](docs/object-oriented-analysis/images/so-do-tuan-tu-khoi-phuc-danh-muc.png){width=80%}

### 3.6.4 Xóa danh mục phân loại

Người dùng yêu cầu xóa một danh mục hoặc nhóm nhu cầu. Hệ thống kiểm tra lịch sử giao dịch để quyết định xóa vĩnh viễn hay xóa mềm nhằm bảo toàn số liệu lịch sử.

- **Thao tác hệ thống**: `deleteCategory(categoryId)`
- **Kết quả phản hồi**: Xóa vĩnh viễn nếu mục chưa từng phát sinh giao dịch, hoặc chuyển sang trạng thái `DELETED` (xóa mềm) nếu đã có giao dịch lịch sử; từ chối nếu xóa nhóm nhu cầu `ACTIVE` cuối cùng của một loại thu chi (`ERR-CAT-002`).

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `categoryId` | Mã mục cần xóa | Tồn tại và đang `ACTIVE`. Nếu là `NeedGroup`, không được là nhóm `ACTIVE` cuối cùng của nhánh loại thu chi (`BR-FIN-04`, `ERR-CAT-002`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự xóa danh mục](docs/object-oriented-analysis/images/so-do-tuan-tu-xoa-danh-muc.png){width=80%}

## 3.7 Nhóm chức năng Thống kê và báo cáo (REP)

Nhóm chức năng Thống kê và báo cáo trích xuất chuỗi dữ liệu 12 tháng liên tục, tính dòng tiền thuần và phân tích cơ cấu thu chi (`EXPENSE` gồm cả phí giao dịch, hoặc `INCOME`) theo các quy tắc `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04` ở chế độ chỉ đọc.

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---- | :-------- | :---------- | :------------ | :----- |
| `UC-REP-01` | Thống kê dòng tiền 12 tháng gần nhất | `generateCashFlowReport` | Trích xuất chuỗi dữ liệu 12 tháng liên tục, tính dòng tiền thuần và tỷ lệ tiết kiệm | Truy vấn |
| `UC-REP-02` | Phân tích cơ cấu thu chi theo nhóm và danh mục | `generateSpendingStructureReport` | Phân tích cơ cấu thu chi theo nhóm nhu cầu, danh mục và biến động kỳ liền kề | Truy vấn |

### 3.7.1 Báo cáo dòng tiền 12 tháng gần nhất

Người dùng yêu cầu tổng hợp chuỗi dòng tiền trong 12 tháng liên tục tính lùi từ một mốc tháng chỉ định để đánh giá xu hướng thu chi và tỷ lệ tiết kiệm.

- **Thao tác hệ thống**: `generateCashFlowReport(endMonth, endYear)`
- **Kết quả phản hồi**: Trả về bảng thống kê 12 tháng liên tục theo thứ tự thời gian tăng dần (tổng thu, tổng chi kèm phí, dòng tiền thuần, tỷ lệ tiết kiệm từng tháng) cùng 3 chỉ số bình quân tháng; từ chối nếu tham số thời gian không hợp lệ (`ERR-REP-001`). Thao tác thuần đọc, hỗ trợ cả mốc thời gian tương lai.

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `endMonth` | Tháng kết thúc kỳ 12 tháng | Số nguyên từ 1 đến 12 (mặc định là tháng hiện tại, `FR-REP-01`, `ERR-REP-001`). |
| `endYear` | Năm kết thúc kỳ 12 tháng | Số nguyên $\ge 2000$ (mặc định là năm hiện tại, `FR-REP-01`, `ERR-REP-001`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự báo cáo dòng tiền](docs/object-oriented-analysis/images/so-do-tuan-tu-bao-cao-dong-tien.png){width=80%}

### 3.7.2 Phân tích cơ cấu thu chi theo nhóm

Người dùng yêu cầu phân tích tỷ trọng dòng tiền theo nhóm nhu cầu và danh mục chi tiết trong một khoảng thời gian kèm đối chiếu kỳ liền trước.

- **Thao tác hệ thống**: `generateSpendingStructureReport(startDate, endDate, transactionType)`
- **Kết quả phản hồi**: Trả về tổng giá trị phát sinh trong kỳ, biến động so với kỳ liền trước, và danh sách phân bổ theo `NeedGroup` (cấp 2), `Category` (cấp 3) có phát sinh dương kèm tỷ trọng phần trăm (`BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`; trả về tỷ trọng `0.00%` và danh sách rỗng nếu tổng kỳ bằng `0.00 VNĐ`); từ chối nếu tham số không hợp lệ (`ERR-REP-001`).

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `startDate` | Ngày bắt đầu kỳ phân tích | Định dạng `YYYY-MM-DD`, khác rỗng và $\le \text{endDate}$ (`FR-REP-02`). |
| `endDate` | Ngày kết thúc kỳ phân tích | Định dạng `YYYY-MM-DD`, khác rỗng và $\ge \text{startDate}$ (`FR-REP-02`). |
| `transactionType` | Loại thu chi cần phân tích | Khác rỗng, nhận giá trị `INCOME` hoặc `EXPENSE` (mặc định là `EXPENSE`, `BR-FIN-04`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự báo cáo cơ cấu thu chi](docs/object-oriented-analysis/images/so-do-tuan-tu-bao-cao-co-cau-thu-chi.png){width=80%}

## 3.8 Nhóm chức năng Lưu trữ và sao lưu dữ liệu (DAT)

Nhóm chức năng Lưu trữ và sao lưu dữ liệu đảm bảo an toàn và toàn vẹn dữ liệu trên máy tính cá nhân thông qua việc sao lưu nguyên khối ra tệp cục bộ, xác thực phục hồi và đối soát độc lập số dư theo `BR-FIN-02`, `BR-FIN-03`.

| Mã UC | Tên ca sử dụng | Thao tác hệ thống | Mục đích nghiệp vụ | Loại thao tác |
| :---- | :-------- | :---------- | :------------ | :----- |
| `UC-DAT-01` | Xuất dữ liệu tài chính | `exportBackupData` | Đóng gói toàn bộ dữ liệu tài chính ra tệp sao lưu an toàn | Truy vấn |
| `UC-DAT-02` | Phục hồi dữ liệu hệ thống từ bản sao lưu | `restoreBackupData` | Kiểm tra toàn vẹn tệp sao lưu và nạp lại dữ liệu vào hệ thống | Cập nhật |
| `UC-DAT-03` | Kiểm tra lại dữ liệu hệ thống | `reconcileSystemData` | Duyệt lịch sử giao dịch để đối soát độc lập tính cân bằng số dư ví | Đối soát |

### 3.8.1 Xuất dữ liệu sao lưu

Người dùng yêu cầu trích xuất toàn bộ dữ liệu tài chính hiện tại ra tệp JSON trên đĩa cứng cục bộ để sao lưu dự phòng.

- **Thao tác hệ thống**: `exportBackupData(exportPath)`
- **Kết quả phản hồi**: Đóng gói 5 nhóm thực thể tài chính thành tệp JSON (UTF-8, định dạng số chính xác theo `BR-FIN-01`), ghi tại `exportPath` và trả về đường dẫn tệp; từ chối và báo lỗi thiết bị nếu đường dẫn không hợp lệ, thiếu quyền ghi hoặc hết dung lượng đĩa.

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `exportPath` | Đường dẫn lưu tệp sao lưu | Đường dẫn thư mục/tệp hợp lệ trên hệ điều hành, tồn tại và có quyền ghi (`WRITE permission`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự sao lưu dữ liệu](docs/object-oriented-analysis/images/so-do-tuan-tu-sao-luu-du-lieu.png){width=80%}

### 3.8.2 Phục hồi dữ liệu từ bản sao lưu

Người dùng yêu cầu nạp lại dữ liệu từ tệp sao lưu JSON. Hệ thống thực hiện kiểm định 4 bước độc lập theo cơ chế "All-or-Nothing" trước khi ghi đè dữ liệu.

- **Thao tác hệ thống**: `restoreBackupData(backupFilePath)`
- **Kết quả phản hồi**: Ghi đè và nạp lại toàn bộ dữ liệu nếu tệp vượt qua đủ 4 bước kiểm định (cấu trúc JSON, phân cấp danh mục `BR-FIN-04`, cân bằng chuyển tiền và phí `BR-FIN-03`, số dư ví không âm `BR-FIN-02`); nếu vi phạm bất kỳ bước nào, hủy thao tác, giữ nguyên 100% dữ liệu hiện tại và báo lỗi chi tiết.

**Tham số đầu vào**

| Tham số | Ý nghĩa nghiệp vụ | Ràng buộc dữ liệu |
| :--- | :--- | :--- |
| `backupFilePath` | Đường dẫn tệp sao lưu | Đường dẫn tệp JSON hợp lệ, tồn tại và có quyền đọc (`READ permission`, `FR-DAT-02`). |

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự phục hồi dữ liệu sao lưu](docs/object-oriented-analysis/images/so-do-tuan-tu-phuc-hoi-du-lieu.png){width=80%}

### 3.8.3 Đối soát toàn vẹn dữ liệu hệ thống

Người dùng kích hoạt tiến trình đối soát độc lập để rà soát 4 điều kiện toàn vẹn cốt lõi của cơ sở dữ liệu tài chính, phát hiện sai lệch số dư hoặc liên kết mồ côi.

- **Thao tác hệ thống**: `reconcileSystemData()` (không có tham số đầu vào)
- **Kết quả phản hồi**: Duyệt toàn bộ bản ghi và trả về báo cáo kiểm tra toàn vẹn dữ liệu cho 4 điều kiện (số dư ví `BR-FIN-02`, cân bằng chuyển tiền `BR-FIN-03`, phân cấp danh mục `BR-FIN-04`, liên kết phí), phân loại mức độ (`INFO`, `WARNING`, `ERROR`) kèm khuyến nghị xử lý. Thao tác thuần đọc, không làm thay đổi dữ liệu.

**Sơ đồ tuần tự hệ thống**

![Sơ đồ tuần tự đối soát dữ liệu hệ thống](docs/object-oriented-analysis/images/so-do-tuan-tu-doi-soat-du-lieu.png){width=80%}

\newpage

# 4. Đặc tả biến đổi dữ liệu thao tác hệ thống

Tài liệu này đặc tả hợp đồng biến đổi dữ liệu cho các thao tác hệ thống làm thay đổi trạng thái (tạo ví, ghi nhận thu chi, chuyển tiền, quản lý danh mục, phục hồi dữ liệu) qua hai phần chính:
- **Tiền điều kiện**: Các ràng buộc dữ liệu và trạng thái bắt buộc phải thỏa mãn trước khi thực thi.
- **Hậu điều kiện**: Kết quả biến đổi dữ liệu sau khi thực thi thành công (tạo/xóa thể hiện, biến đổi thuộc tính, thiết lập/hủy liên kết).

Các thao tác chỉ đọc dữ liệu nhìn chung nằm ngoài phạm vi biến đổi thực thể; riêng nhóm Thống kê và báo cáo có hợp đồng kiểm tra tham số và bất biến chỉ đọc được đặc tả tại mục 4.7.

## 4.1 Cấu trúc đặc tả thao tác

Mỗi thao tác làm thay đổi dữ liệu được đặc tả thống nhất theo 4 thành phần:

| Thành phần | Ý nghĩa | Nội dung đặc tả |
| :--- | :--- | :--- |
| **Thao tác** | Tên gọi và tham số | Tên thao tác hệ thống kèm danh sách tham số đầu vào. |
| **Quy chiếu chéo** | Căn cứ nghiệp vụ | Liên kết tới ca sử dụng (`UC-*`), yêu cầu chức năng (`FR-*`), quy tắc nghiệp vụ (`BR-*`) và mã lỗi (`ERR-*`). |
| **Tiền điều kiện** | Điều kiện tiên quyết | Các ràng buộc và trạng thái dữ liệu phải thỏa mãn để hệ thống chấp nhận xử lý. |
| **Hậu điều kiện** | Trạng thái sau thực thi | Trạng thái mới của các đối tượng dữ liệu theo 4 khía cạnh:<br/>- *Tạo mới thể hiện*: Đối tượng mới được khởi tạo.<br/>- *Xóa thể hiện*: Đối tượng bị hủy bỏ vĩnh viễn.<br/>- *Biến đổi thuộc tính*: Giá trị thuộc tính (số dư, tên, trạng thái) thay đổi.<br/>- *Hình thành hoặc phá vỡ liên kết*: Quan hệ ngữ nghĩa giữa các đối tượng được kết nối hoặc gỡ bỏ. |

## 4.2 Các thao tác Quản lý ví

Nhóm chức năng này quản lý các nguồn tiền trong hệ thống (tạo ví, sửa thông tin ví và điều chỉnh số dư kiểm kê). Thao tác xem tổng hợp số dư chỉ đọc dữ liệu nên không thuộc phạm vi phần này.

### 4.2.1 Thao tác tạo ví mới

Người dùng tạo một ví mới để quản lý nguồn tiền riêng biệt và theo dõi giao dịch.

- **Thao tác hệ thống**: `createWallet(walletName, note)`
- **Quy chiếu chéo**: `UC-WAL-01`, `FR-WAL-01`, `FR-WAL-04`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- `walletName` sau khi `trim` dài 1–50 ký tự, không rỗng (`ERR-NAM-001`) và duy nhất so với `name` của mọi `Wallet` (`ACTIVE` hoặc `ARCHIVED`), không phân biệt hoa thường (`BR-NAM-01`, `ERR-NAM-002`).
- `note` (nếu có) dài tối đa 255 ký tự sau khi `trim` (`FR-WAL-01`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một thể hiện `Wallet` mới (`FR-WAL-01`).
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: `Wallet` mới được cấp `walletId` duy nhất, gán $\text{name} = \text{walletName}$, $\text{note} = \text{note}$, $\text{status} = \text{ACTIVE}$, `createdAt` hiện tại và $\text{currentBalance} = 0.00$ VNĐ (`BR-FIN-02`); tính lại tỷ lệ phân bổ số dư giữa các ví (`FR-WAL-04`).
- **Hình thành hoặc phá vỡ liên kết**: Không có.

### 4.2.2 Thao tác cập nhật thông tin ví

Người dùng sửa tên hiển thị hoặc ghi chú của ví đang tồn tại mà không làm thay đổi số dư hay lịch sử giao dịch (`BR-FIN-02`).

- **Thao tác hệ thống**: `updateWallet(walletId, walletName, note)`
- **Quy chiếu chéo**: `UC-WAL-03`, `FR-WAL-03`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với `walletId` tồn tại trong hệ thống (`FR-WAL-03`).
- `walletName` mới sau khi `trim` dài 1–50 ký tự, không rỗng (`ERR-NAM-001`) và không trùng với `name` của bất kỳ `Wallet` nào khác, không phân biệt hoa thường (`BR-NAM-01`, `ERR-NAM-002`).
- `note` mới (nếu có) dài tối đa 255 ký tự sau khi `trim` (`FR-WAL-03`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: Trên `Wallet` có mã `walletId`, cập nhật $\text{name} = \text{walletName}$ và $\text{note} = \text{note}$ (`FR-WAL-03`); giữ nguyên `walletId`, `status` và `currentBalance` (`BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Không có.

### 4.2.3 Thao tác điều chỉnh số dư ví thực tế

Người dùng ghi nhận mốc kiểm kê khi tiền thực tế chênh lệch với số dư phần mềm. Hệ thống tạo bản ghi đối soát bất biến và đồng bộ số dư ví theo thực tế (`BR-FIN-02`).

- **Thao tác hệ thống**: `adjustBalance(walletId, actualBalance, reason)`
- **Quy chiếu chéo**: `UC-WAL-04`, `FR-WAL-05`, `BR-FIN-01`, `BR-FIN-02`, `ERR-FIN-001`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với `walletId` tồn tại và có $\text{status} = \text{ACTIVE}$ (`FR-WAL-05`).
- `actualBalance` là số thập phân 2 chữ số (`BR-FIN-01`, `ERR-FIN-001`), không âm ($\ge 0.00$ VNĐ, `BR-FIN-02`, `ERR-WAL-001`) và khác `Wallet.currentBalance` hiện tại (`FR-WAL-05`).
- `reason` (nếu có) dài tối đa 255 ký tự sau khi `trim` (`FR-WAL-05`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một thể hiện `BalanceAdjustmentRecord` bất biến (`BR-FIN-02`, `FR-WAL-05`).
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: `BalanceAdjustmentRecord` mới ghi nhận `recordId` duy nhất, $\text{previousBalance} = \text{Wallet.currentBalance}$ cũ, `actualBalance`, $\text{difference} = \text{actualBalance} - \text{previousBalance}$ (`BR-FIN-01`), `reason` và `adjustedAt` hiện tại; cập nhật $\text{Wallet.currentBalance} = \text{actualBalance}$ (`BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Thiết lập liên kết cố định từ `BalanceAdjustmentRecord` vừa tạo tới `Wallet` qua `walletId` (`FR-WAL-05`).

## 4.3 Các thao tác Ghi nhận thu chi

Nhóm chức năng này xử lý việc ghi nhận, chỉnh sửa và xóa các giao dịch thu nhập, chi tiêu kèm phí phát sinh. Hoạt động lọc và tra cứu lịch sử thu chi chỉ đọc dữ liệu nên không thuộc phạm vi phần này.

### 4.3.1 Thao tác ghi nhận giao dịch thu chi

Người dùng ghi nhận một khoản thu nhập hoặc chi tiêu kèm phí giao dịch (nếu có), hạch toán đồng thời bản ghi gốc cùng bản ghi phí và cập nhật số dư ví tức thời (`BR-FIN-02`).

- **Thao tác hệ thống**: `recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- **Quy chiếu chéo**: `UC-TXN-01`, `FR-TXN-01`, `FR-TXN-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Thể hiện `Wallet` ứng với `walletId` tồn tại và có $\text{status} = \text{ACTIVE}$ (`FR-TXN-01`, `FR-TXN-02`).
- Thể hiện `Category` ứng với `categoryId` tồn tại ở cấp 3, có $\text{status} = \text{ACTIVE}$ và thuộc nhánh `INCOME` hoặc `EXPENSE` hợp lệ (`BR-FIN-04`).
- `amount` là số thập phân chính xác $> 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`); `transactionDate` không vượt quá ngày hiện tại; `note` dài tối đa 255 ký tự sau khi `trim` (`FR-TXN-01`, `FR-TXN-02`).
- `feeAmount` là số thập phân chính xác $\ge 0.00$ VNĐ (`BR-FIN-01`, `BR-FIN-02`, `ERR-FEE-001`). Nếu $\text{feeAmount} > 0.00$ VNĐ, bắt buộc có `feeCategoryId` (`ERR-FEE-002`) trỏ tới một `Category` cấp 3 đang `ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-04`, `ERR-FEE-003`).
- Số dư `Wallet.currentBalance` sau khi áp dụng `amount` và trừ `feeAmount` phải $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một `IncomeExpenseTransaction` mới (`FR-TXN-01`, `FR-TXN-02`); nếu $\text{feeAmount} > 0.00$ VNĐ, khởi tạo đồng thời một `TransactionFee` đi kèm (`BR-FIN-02`).
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: `IncomeExpenseTransaction` nhận `transactionId` duy nhất, `amount`, `transactionDate`, `note`, `createdAt` và `transactionType` từ `Category`; `TransactionFee` (nếu có) nhận `feeId` duy nhất, $\text{amount} = \text{feeAmount}$ và $\text{parentTransactionType} = \text{INCOME\_EXPENSE}$; `Wallet.currentBalance` tăng $\text{amount} - \text{feeAmount}$ (nếu `INCOME`) hoặc giảm $\text{amount} + \text{feeAmount}$ (nếu `EXPENSE`) (`BR-FIN-01`, `BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Liên kết `IncomeExpenseTransaction` tới `Wallet` (`walletId`) và `Category` cấp 3 (`categoryId`) (`BR-FIN-04`); nếu $\text{feeAmount} > 0.00$ VNĐ, liên kết `TransactionFee` tới `IncomeExpenseTransaction` ($\text{parentTransactionId} = \text{transactionId}$), `Wallet` (`walletId`) và `Category` phí (`feeCategoryId`) (`BR-FIN-02`).

### 4.3.2 Thao tác chỉnh sửa giao dịch thu chi

Người dùng hiệu chỉnh thông tin một giao dịch thu chi đã lưu. Hệ thống hoàn nguyên tác động cũ trên ví ban đầu, áp dụng thông số mới lên ví đích và đồng bộ bản ghi phí liên kết trong cùng một bước nguyên tử (`BR-FIN-02`).

- **Thao tác hệ thống**: `updateTransaction(transactionId, walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- **Quy chiếu chéo**: `UC-TXN-03`, `FR-TXN-04`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Thể hiện `IncomeExpenseTransaction` ứng với `transactionId` tồn tại (`FR-TXN-04`), không phải bản ghi `TransactionFee` độc lập (`BR-FIN-02`).
- `Wallet` mới ứng với `walletId` tồn tại và có $\text{status} = \text{ACTIVE}$ (`FR-TXN-04`).
- `Category` mới ứng với `categoryId` cùng `transactionType` với giao dịch gốc (`BR-FIN-04`); phải có $\text{status} = \text{ACTIVE}$ nếu chọn mới, hoặc cho phép giữ nguyên danh mục cũ đã `DELETED` (`FR-TXN-04`).
- `amount` mới $> 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`); `feeAmount` mới $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-FEE-001`) và nếu $\text{feeAmount} > 0.00$ VNĐ phải có `feeCategoryId` hợp lệ thuộc nhánh `EXPENSE` (`BR-FIN-04`, `ERR-FEE-002`, `ERR-FEE-003`).
- Số dư `currentBalance` của ví cũ sau hoàn nguyên và ví mới sau cập nhật đều $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một `TransactionFee` mới nếu trước khi sửa chưa có phí và sau sửa $\text{feeAmount} > 0.00$ VNĐ (`BR-FIN-02`, `FR-TXN-04`); ngược lại không có.
- **Xóa thể hiện**: Xóa vĩnh viễn `TransactionFee` liên kết nếu trước đó có phí và sau sửa $\text{feeAmount} = 0.00$ VNĐ (`BR-FIN-02`, `FR-TXN-04`); ngược lại không có.
- **Biến đổi thuộc tính**: Cập nhật `amount`, `transactionDate`, `note`, `walletId`, `categoryId` trên `IncomeExpenseTransaction` (giữ nguyên `transactionId`, `transactionType`); cập nhật $\text{amount} = \text{feeAmount}$, $\text{categoryId} = \text{feeCategoryId}$ trên `TransactionFee` (nếu phí cũ và mới đều $> 0.00$ VNĐ); tính lại `currentBalance` trên `Wallet` cũ và `Wallet` mới (`BR-FIN-01`, `BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Chuyển liên kết từ `Wallet`/`Category` cũ sang `Wallet`/`Category` mới (`BR-FIN-04`) và đồng bộ liên kết giữa `IncomeExpenseTransaction`, `TransactionFee`, `Wallet` mới cùng `Category` phí (`BR-FIN-02`).

### 4.3.3 Thao tác xóa giao dịch thu chi

Người dùng xóa một giao dịch thu chi khỏi sổ theo dõi. Hệ thống kiểm tra số dư hoàn nguyên, xóa đồng thời giao dịch gốc cùng bản ghi phí đi kèm và khôi phục số dư ví (`BR-FIN-02`).

- **Thao tác hệ thống**: `deleteTransaction(transactionId)`
- **Quy chiếu chéo**: `UC-TXN-04`, `FR-TXN-05`, `BR-FIN-01`, `BR-FIN-02`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `IncomeExpenseTransaction` ứng với `transactionId` tồn tại (`FR-TXN-05`), không phải bản ghi `TransactionFee` độc lập (`BR-FIN-02`).
- Nếu xóa giao dịch `INCOME`, số dư `Wallet.currentBalance` sau khi trừ `amount` và cộng hoàn lại `TransactionFee.amount` (nếu có) phải $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Xóa vĩnh viễn `IncomeExpenseTransaction` có mã `transactionId` (`FR-TXN-05`) cùng `TransactionFee` liên kết (nếu có) (`BR-FIN-02`).
- **Biến đổi thuộc tính**: Hoàn nguyên `Wallet.currentBalance`: giảm $\text{amount} - \text{feeAmount}$ nếu xóa giao dịch `INCOME`, hoặc tăng $\text{amount} + \text{feeAmount}$ nếu xóa giao dịch `EXPENSE` (`BR-FIN-01`, `BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Gỡ bỏ toàn bộ liên kết từ `IncomeExpenseTransaction` và `TransactionFee` (nếu có) tới `Wallet` và `Category` (`BR-FIN-02`, `FR-TXN-05`).

## 4.4 Các thao tác Chuyển tiền nội bộ

Nhóm chức năng này xử lý việc điều chuyển tiền giữa hai ví nội bộ và hủy giao dịch chuyển tiền để hoàn nguyên số dư hai ví. Hoạt động tra cứu lịch sử chuyển tiền chỉ đọc dữ liệu nên không thuộc phạm vi phần này.

### 4.4.1 Thao tác chuyển tiền giữa hai ví

Người dùng điều chuyển tiền giữa hai ví nội bộ kèm phí chuyển tiền (nếu có) theo cơ chế ghi nhận đồng thời (`BR-FIN-03`).

- **Thao tác hệ thống**: `transferFunds(sourceWalletId, destinationWalletId, amount, transferDate, note, feeAmount, feeCategoryId)`
- **Quy chiếu chéo**: `UC-TRF-01`, `FR-TRF-01`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Tiền điều kiện**

- Hai thể hiện `Wallet` ứng với `sourceWalletId` và `destinationWalletId` cùng tồn tại, có $\text{status} = \text{ACTIVE}$ và phân biệt nhau ($\text{sourceWalletId} \neq \text{destinationWalletId}$, `BR-FIN-03`, `ERR-TRF-001`).
- `amount` là số thập phân 2 chữ số (`BR-FIN-01`, `ERR-FIN-001`) và $> 0.00$ VNĐ (`BR-FIN-03`, `ERR-TRF-002`); `transferDate` không vượt quá ngày hiện tại; `note` dài tối đa 255 ký tự sau khi `trim` (`FR-TRF-01`).
- `feeAmount` là số thập phân chính xác $\ge 0.00$ VNĐ (`BR-FIN-01`, `BR-FIN-03`, `ERR-FEE-001`). Nếu $\text{feeAmount} > 0.00$ VNĐ, bắt buộc có `feeCategoryId` (`ERR-FEE-002`) trỏ tới một `Category` cấp 3 đang `ACTIVE` thuộc nhánh `EXPENSE` (`BR-FIN-02`, `BR-FIN-04`, `ERR-FEE-003`).
- Số dư ví nguồn $\text{sourceWallet.currentBalance} \ge \text{amount} + \text{feeAmount}$ (`BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một `TransferTransaction` mới (`FR-TRF-01`); nếu $\text{feeAmount} > 0.00$ VNĐ, khởi tạo đồng thời một `TransactionFee` thuộc loại `EXPENSE` (`BR-FIN-03`).
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: `TransferTransaction` nhận `transferId` duy nhất, `amount`, $\text{transactionDate} = \text{transferDate}$, `note`, `createdAt`; `TransactionFee` (nếu có) nhận `feeId` duy nhất, $\text{amount} = \text{feeAmount}$, $\text{parentTransactionType} = \text{TRANSFER}$; đồng thời giảm `sourceWallet.currentBalance` đi $\text{amount} + \text{feeAmount}$ và tăng `destinationWallet.currentBalance` thêm `amount` (`BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`).
- **Hình thành hoặc phá vỡ liên kết**: Liên kết `TransferTransaction` tới `Wallet` nguồn (`sourceWalletId`) và `Wallet` đích (`destinationWalletId`) (`BR-FIN-03`); nếu $\text{feeAmount} > 0.00$ VNĐ, liên kết `TransactionFee` tới `TransferTransaction` ($\text{parentTransactionId} = \text{transferId}$), `Wallet` nguồn (`sourceWalletId`) và `Category` phí (`feeCategoryId`) (`BR-FIN-02`).

### 4.4.2 Thao tác xóa giao dịch chuyển tiền

Người dùng hủy một giao dịch chuyển tiền nội bộ để hoàn nguyên đồng thời số dư của cả hai ví về trạng thái trước khi chuyển (`BR-FIN-03`).

- **Thao tác hệ thống**: `deleteTransfer(transferId)`
- **Quy chiếu chéo**: `UC-TRF-03`, `FR-TRF-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`

**Tiền điều kiện**

- Thể hiện `TransferTransaction` ứng với `transferId` tồn tại trong sổ chuyển tiền (`FR-TRF-02`).
- Số dư hiện tại của `Wallet` đích $\text{destinationWallet.currentBalance} \ge \text{TransferTransaction.amount}$ để không làm âm ví đích khi thu hồi (`BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Xóa vĩnh viễn `TransferTransaction` có mã `transferId` (`FR-TRF-02`) cùng `TransactionFee` liên kết (nếu có) (`BR-FIN-03`).
- **Biến đổi thuộc tính**: Hoàn nguyên đồng thời hai đầu ví (`BR-FIN-01`, `BR-FIN-03`): cộng $\text{amount} + \text{feeAmount}$ vào `sourceWallet.currentBalance` và trừ `amount` khỏi `destinationWallet.currentBalance` (`BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Gỡ bỏ toàn bộ liên kết từ `TransferTransaction` và `TransactionFee` (nếu có) tới `Wallet` nguồn, `Wallet` đích và `Category` phí (`BR-FIN-02`, `BR-FIN-03`, `FR-TRF-02`).

## 4.5 Các thao tác Danh mục phân loại

Nhóm chức năng này quản lý cấu trúc phân loại thu chi 3 cấp, bao gồm thêm mới, đổi tên, khôi phục và xóa danh mục/nhóm nhu cầu theo cơ chế bảo toàn lịch sử giao dịch.

### 4.5.1 Thao tác thêm danh mục phân loại mới

Người dùng bổ sung một danh mục chi tiết (cấp 3) vào nhóm nhu cầu cha hoặc bổ sung một nhóm nhu cầu (cấp 2) vào nhánh loại thu chi.

- **Thao tác hệ thống**: `addCategory(categoryName, parentCategoryId)`
- **Quy chiếu chéo**: `UC-CAT-01`, `FR-CAT-01`, `BR-NAM-01`, `BR-FIN-04`, `ERR-CAT-005`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Nhóm cha hoặc nhánh loại thu chi xác định bởi `parentCategoryId` tồn tại và đang có $\text{status} = \text{ACTIVE}$ (`BR-FIN-04`); nếu nhóm cha không tồn tại hoặc đang ở trạng thái `DELETED`, báo lỗi `ERR-CAT-005`.
- `categoryName` sau khi `trim` dài 1–50 ký tự, không để rỗng (`BR-NAM-01`, `ERR-NAM-001`) và duy nhất (không phân biệt hoa thường) trong cùng nhóm cha hoặc cùng nhánh loại thu chi đang `ACTIVE` (`ERR-NAM-002`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Khởi tạo một thể hiện `Category` (cấp 3) hoặc `NeedGroup` (cấp 2) mới với mã định danh duy nhất, bất biến.
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: Gán $\text{name} = \text{categoryName}$ (đã `trim`), $\text{status} = \text{ACTIVE}$ và kế thừa `transactionType` (`INCOME` hoặc `EXPENSE`) từ nhánh cha.
- **Hình thành hoặc phá vỡ liên kết**: Thiết lập liên kết phân cấp từ thực thể mới tạo tới `NeedGroup` cha hoặc `TransactionType` gốc tương ứng (`parentCategoryId`, `BR-FIN-04`).

### 4.5.2 Thao tác đổi tên danh mục

Người dùng đổi tên hiển thị của một danh mục chi tiết hoặc nhóm nhu cầu mà không làm thay đổi các giao dịch lịch sử đã ghi nhận.

- **Thao tác hệ thống**: `renameCategory(categoryId, newName)`
- **Quy chiếu chéo**: `UC-CAT-02`, `FR-CAT-02`, `BR-NAM-01`, `BR-FIN-04`, `ERR-NAM-001`, `ERR-NAM-002`

**Tiền điều kiện**

- Thực thể `Category` hoặc `NeedGroup` ứng với `categoryId` tồn tại trong cây phân cấp (`BR-FIN-04`).
- `newName` sau khi `trim` dài 1–50 ký tự, không để rỗng (`BR-NAM-01`, `ERR-NAM-001`) và không trùng tên (không phân biệt hoa thường) với mục khác đang `ACTIVE` trong cùng nhóm cha hoặc cùng nhánh loại thu chi (`ERR-NAM-002`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: Cập nhật $\text{name} = \text{newName}$ (đã `trim`); giữ nguyên mã định danh và trạng thái `status`.
- **Hình thành hoặc phá vỡ liên kết**: Không có (bảo toàn toàn bộ liên kết phân cấp và liên kết giao dịch lịch sử).

### 4.5.3 Thao tác khôi phục danh mục đã xóa

Người dùng phục hồi trạng thái hoạt động cho một danh mục hoặc nhóm nhu cầu đã bị xóa mềm để tiếp tục sử dụng cho các giao dịch mới.

- **Thao tác hệ thống**: `restoreCategory(categoryId)`
- **Quy chiếu chéo**: `UC-CAT-03`, `FR-CAT-04`, `BR-NAM-01`, `BR-FIN-04`, `ERR-CAT-004`, `ERR-NAM-002`

**Tiền điều kiện**

- Thực thể `Category` hoặc `NeedGroup` ứng với `categoryId` tồn tại và đang có $\text{status} = \text{DELETED}$.
- Nếu khôi phục `Category` cấp 3, `NeedGroup` cha trực thuộc phải đang có $\text{status} = \text{ACTIVE}$ (`BR-FIN-04`; vi phạm báo lỗi `ERR-CAT-004`).
- Tên của mục cần khôi phục không trùng (không phân biệt hoa thường) với bất kỳ mục nào khác đang `ACTIVE` trong cùng nhóm cha hoặc cùng nhánh loại thu chi (`BR-NAM-01`, `ERR-NAM-002`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Không có.
- **Biến đổi thuộc tính**: Chuyển `status` từ `DELETED` sang `ACTIVE`, đưa mục hiển thị trở lại trên biểu mẫu tạo giao dịch.
- **Hình thành hoặc phá vỡ liên kết**: Không có (bảo toàn nguyên vẹn liên kết phân cấp và lịch sử giao dịch).

### 4.5.4 Thao tác xóa danh mục phân loại

Người dùng yêu cầu xóa một danh mục hoặc nhóm nhu cầu. Hệ thống kiểm tra lịch sử giao dịch để thực hiện xóa vĩnh viễn hoặc xóa mềm nhằm bảo toàn số liệu tài chính.

- **Thao tác hệ thống**: `deleteCategory(categoryId)`
- **Quy chiếu chéo**: `UC-CAT-04`, `FR-CAT-03`, `BR-FIN-04`, `ERR-CAT-002`

**Tiền điều kiện**

- Thực thể `Category` hoặc `NeedGroup` ứng với `categoryId` tồn tại và đang có $\text{status} = \text{ACTIVE}$.
- Nếu xóa `NeedGroup`, nhóm đó không phải là nhóm `ACTIVE` cuối cùng của nhánh loại thu chi (`BR-FIN-04`; vi phạm báo lỗi `ERR-CAT-002`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Không có.
- **Xóa thể hiện**: Xóa vĩnh viễn thực thể khỏi hệ thống (xóa cứng) nếu mục chưa từng phát sinh giao dịch thu chi hoặc phí liên kết; giữ lại thực thể nếu đã có giao dịch lịch sử.
- **Biến đổi thuộc tính**: Nếu đã có giao dịch lịch sử (xóa mềm), chuyển `status` từ `ACTIVE` sang `DELETED` (ẩn khi nhập mới, hiển thị kèm nhãn "(đã xóa)" trên báo cáo lịch sử).
- **Hình thành hoặc phá vỡ liên kết**: Nếu xóa vĩnh viễn, gỡ bỏ liên kết phân cấp với nhóm cha; nếu xóa mềm, bảo toàn toàn bộ liên kết phân cấp và liên kết với các giao dịch lịch sử (`IncomeExpenseTransaction`, `TransactionFee`).

## 4.6 Thao tác Lưu trữ và phục hồi dữ liệu

Nhóm chức năng này đảm bảo an toàn và toàn vẹn dữ liệu, trong đó thao tác phục hồi từ bản sao lưu sẽ ghi đè toàn bộ dữ liệu hiện tại sau khi kiểm định. Các thao tác xuất sao lưu và đối soát chỉ đọc dữ liệu nên không làm thay đổi trạng thái hệ thống.

### 4.6.1 Thao tác phục hồi dữ liệu từ bản sao lưu

Người dùng khôi phục toàn bộ trạng thái dữ liệu tài chính từ tệp sao lưu JSON sau khi vượt qua quy trình kiểm tra toàn vẹn 4 bước.

- **Thao tác hệ thống**: `restoreBackupData(backupFilePath)`
- **Quy chiếu chéo**: `UC-DAT-02`, `FR-DAT-02`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, `ERR-DAT-001`, `ERR-DAT-002`

**Tiền điều kiện**

- Tệp tại `backupFilePath` tồn tại, có quyền đọc (`READ permission`) và đúng cú pháp JSON mã hóa UTF-8 (`FR-DAT-02`; vi phạm báo lỗi `ERR-DAT-001`).
- Dữ liệu trong tệp vượt qua đủ 4 bước kiểm định độc lập theo cơ chế `All-or-Nothing` (vi phạm bất kỳ bước nào báo lỗi `ERR-DAT-002`):
  1. Đầy đủ các khóa thực thể dữ liệu bắt buộc theo schema JSON.
  2. Tuân thủ cây phân cấp danh mục 3 cấp (`TransactionType` $\rightarrow$ `NeedGroup` $\rightarrow$ `Category`), không có danh mục mồ côi (`BR-FIN-04`).
  3. Toàn vẹn liên kết chuyển tiền hai chiều và không có chi phí giao dịch mồ côi (`BR-FIN-02`, `BR-FIN-03`).
  4. Số dư tái tính toán của mọi ví từ lịch sử giao dịch luôn không âm ($\ge 0.00$ VNĐ, `BR-FIN-02`).

**Hậu điều kiện**

- **Tạo mới thể hiện**: Tái tạo toàn bộ các thể hiện `Wallet`, `NeedGroup`, `Category`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee` và `BalanceAdjustmentRecord` từ tệp sao lưu.
- **Xóa thể hiện**: Hủy bỏ toàn bộ các thể hiện dữ liệu hiện tại trong bộ nhớ trước khi nạp dữ liệu mới.
- **Biến đổi thuộc tính**: Đồng bộ toàn bộ thuộc tính định danh, số tiền, thời gian, trạng thái theo bản sao lưu và tính lại `Wallet.currentBalance` của từng ví từ lịch sử dòng tiền (`BR-FIN-02`).
- **Hình thành hoặc phá vỡ liên kết**: Hủy toàn bộ mạng lưới liên kết cũ và tái lập đầy đủ các liên kết ngữ nghĩa giữa ví, danh mục 3 cấp, giao dịch thu chi, chuyển tiền, chi phí và bản ghi kiểm kê theo đúng bản sao lưu.

## 4.7 Thống kê và báo cáo

Người dùng trích xuất báo cáo dòng tiền 12 tháng hoặc báo cáo cơ cấu thu chi theo nhóm nhu cầu và danh mục ở chế độ chỉ đọc.

- **Thao tác hệ thống**: `generateCashFlowReport(endMonth, endYear)` và `generateSpendingStructureReport(startDate, endDate, transactionType)`
- **Quy chiếu chéo**: `UC-REP-01`, `UC-REP-02`, `FR-REP-01`, `FR-REP-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-REP-001`

**Tiền điều kiện**

- Với `generateCashFlowReport`: `endMonth` thuộc đoạn $[1, 12]$ và $\text{endYear} \ge 2000$ (`FR-REP-01`).
- Với `generateSpendingStructureReport`: `startDate` và `endDate` hợp lệ, khác rỗng, thỏa $\text{startDate} \le \text{endDate}$; `transactionType` thuộc `{EXPENSE, INCOME}` (`FR-REP-02`, `BR-FIN-04`).
- Cho phép truy vấn các mốc hoặc khoảng thời gian trong tương lai ở chế độ chỉ đọc; nếu thiếu hoặc sai tham số, từ chối và báo lỗi `ERR-REP-001`.

**Hậu điều kiện**

- **Tạo mới / Xóa thể hiện**: Không có (chỉ tạo kết quả tạm thời trả về giao diện, không tạo hay xóa thực thể lưu trữ).
- **Biến đổi thuộc tính / Liên kết**: Không có (thao tác thuần đọc, bảo toàn nguyên vẹn số dư ví, trạng thái và các liên kết trong hệ thống).

\newpage
# 5. Mô hình trạng thái thực thể

Tài liệu này mô tả vòng đời của các thực thể có hành vi biến đổi theo trạng thái dữ liệu trong hệ thống quản lý tài chính cá nhân, bao gồm tập trạng thái hợp lệ, sự kiện kích hoạt, điều kiện kiểm tra và tác vụ cập nhật số dư đi kèm. Các thực thể bất biến sau khởi tạo (như bản ghi kiểm kê số dư `BalanceAdjustmentRecord`) không thuộc phạm vi tài liệu này.

## 5.1 Quy chuẩn mô hình trạng thái

Mỗi biểu đồ (`direction LR`) và bảng chuyển trạng thái trong tài liệu được chuẩn hóa theo 4 thành phần:

| Thành phần | Ký hiệu quy ước | Nội dung kỹ thuật |
| :--- | :--- | :--- |
| **Trạng thái ban đầu** | Điểm đen tròn `[*]` (trái) | Điểm bắt đầu vòng đời khi thực thể được khởi tạo thành công từ một thao tác hệ thống. |
| **Khối trạng thái** | Hộp tên trạng thái IN HOA | Tình trạng tồn tại của thực thể (`ACTIVE`, `ARCHIVED`, `RECORDED`, `MODIFIED`, `COMMITTED`, `DELETED`). |
| **Mũi tên chuyển tiếp** | Tên thao tác / sự kiện | Thao tác hệ thống kích hoạt bước chuyển giữa hai trạng thái (không vẽ mũi tên tự lặp cho thao tác bị từ chối). |
| **Trạng thái kết thúc** | `DELETED` $\rightarrow$ `[*]` (phải) | Thực thể chuyển sang trạng thái xóa (`DELETED`) trước khi kết thúc vòng đời tại `[*]`. |

## 5.2 Vòng đời thực thể Ví

Thực thể Ví (`Wallet`) quản lý trạng thái tồn tại của nguồn tiền từ khi khởi tạo (`FR-WAL-01`), hạch toán dòng tiền hàng ngày cho đến khi chuyển sang lưu trữ hoặc xóa vĩnh viễn (`FR-WAL-03`), bảo đảm tên duy nhất (`BR-NAM-01`) và số dư không âm (`BR-FIN-02`).

- **Quy chiếu chéo**: `UC-WAL-01`, `UC-WAL-03`, `FR-WAL-01`, `FR-WAL-03`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

![Sơ đồ trạng thái của ví](docs/object-oriented-analysis/images/so-do-trang-thai-vi.png){width=80%}

| Giai đoạn chuyển đổi | Điều kiện kiểm tra | Xử lý hệ thống |
| :--- | :--- | :--- |
| **Khởi tạo ví**: `[*]` $\rightarrow$ `ACTIVE` (`createWallet`) | Tên ví dài 1–50 ký tự sau khi `trim` (`ERR-NAM-001`) và duy nhất trong các ví `ACTIVE` hoặc `ARCHIVED` (`BR-NAM-01`, `ERR-NAM-002`). | Cấp `walletId` duy nhất, đặt $\text{status} = \text{ACTIVE}$, ghi nhận `createdAt` (`FR-WAL-01`) và gán $\text{currentBalance} = 0.00$ VNĐ (`BR-FIN-02`). |
| **Lưu trữ ví**: `ACTIVE` $\rightarrow$ `ARCHIVED` (`updateWallet`) | Thể hiện `Wallet` đang `ACTIVE` và không phải là ví "Tiền mặt" mặc định (`FR-WAL-03`). | Chuyển `status` sang `ARCHIVED`, ngừng nhận giao dịch mới (`FR-WAL-03`); vẫn cộng `currentBalance` vào tổng số dư hệ thống nếu $> 0.00$ VNĐ (`BR-FIN-02`). |
| **Kích hoạt lại ví**: `ARCHIVED` $\rightarrow$ `ACTIVE` (`updateWallet`) | Thể hiện `Wallet` tồn tại và đang có $\text{status} = \text{ARCHIVED}$ (`FR-WAL-03`). | Chuyển `status` về `ACTIVE`, mở lại quyền nhận giao dịch mới (`FR-WAL-03`) và giữ nguyên số dư cùng lịch sử dòng tiền (`BR-FIN-02`). |
| **Xóa vĩnh viễn ví**: `ACTIVE` $\rightarrow$ `DELETED` $\rightarrow$ `[*]` (`updateWallet`) | `Wallet` không phải ví "Tiền mặt" mặc định và chưa từng phát sinh giao dịch thu chi, chuyển tiền hay điều chỉnh số dư (`FR-WAL-03`). | Chuyển sang `DELETED`, xóa vĩnh viễn thể hiện `Wallet` khỏi hệ thống (`FR-WAL-03`) và giải phóng tên ví (`BR-NAM-01`). |

## 5.3 Vòng đời Giao dịch thu chi

Thực thể Giao dịch thu chi (`IncomeExpenseTransaction`) phản ánh vòng đời của khoản thu/chi từ lúc ghi nhận (`FR-TXN-01`, `FR-TXN-02`), chỉnh sửa (`FR-TXN-04`) đến khi xóa khỏi sổ theo dõi (`FR-TXN-05`), luôn gắn với phân cấp danh mục 3 cấp (`BR-FIN-04`), đồng bộ `TransactionFee` đi kèm và bảo toàn số dư ví (`BR-FIN-02`).

- **Quy chiếu chéo**: `UC-TXN-01`, `UC-TXN-03`, `UC-TXN-04`, `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-04`, `FR-TXN-05`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

![Sơ đồ trạng thái của giao dịch thu chi](docs/object-oriented-analysis/images/so-do-trang-thai-giao-dich.png){width=80%}

| Giai đoạn chuyển đổi | Điều kiện kiểm tra | Xử lý hệ thống |
| :--- | :--- | :--- |
| **Ghi nhận mới**: `[*]` $\rightarrow$ `RECORDED` (`recordIncomeExpense`) | Ví và danh mục cấp 3 đang `ACTIVE` (`BR-FIN-04`); $\text{amount} > 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`); $\text{feeAmount} \ge 0.00$ VNĐ (`ERR-FEE-001`) kèm danh mục phí cấp 3 nhánh `EXPENSE` nếu có phí (`ERR-FEE-002`, `ERR-FEE-003`); số dư ví sau giao dịch $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`). | Khởi tạo `IncomeExpenseTransaction` ở trạng thái `RECORDED` (`FR-TXN-01`, `FR-TXN-02`), cố định `transactionType` theo danh mục (`BR-FIN-04`); tạo `TransactionFee` liên kết (nếu có phí) và cập nhật `Wallet.currentBalance` (`BR-FIN-02`). |
| **Chỉnh sửa**: `RECORDED` $\rightarrow$ `MODIFIED` (`updateTransaction`) | Bản ghi gốc tồn tại, giữ nguyên `transactionType` (`BR-FIN-04`); $\text{amount} > 0.00$ VNĐ (`ERR-FIN-001`), phí hợp lệ (`ERR-FEE-001`–`003`); danh mục chọn mới đang `ACTIVE` (hoặc giữ nguyên danh mục cũ đã `DELETED`, `FR-TXN-04`); số dư ví cũ/mới không âm (`BR-FIN-02`, `ERR-WAL-001`). | Chuyển sang `MODIFIED`, cập nhật thông tin giao dịch (`FR-TXN-04`); đồng bộ `TransactionFee` liên kết (tạo, sửa hoặc xóa) và tính lại `currentBalance` trên ví cũ cùng ví mới (`BR-FIN-02`). |
| **Hủy bỏ giao dịch**: `RECORDED` / `MODIFIED` $\rightarrow$ `DELETED` $\rightarrow$ `[*]` (`deleteTransaction`) | Giao dịch gốc tồn tại (`FR-TXN-05`), không phải bản ghi phí độc lập; nếu xóa khoản thu (`INCOME`), số dư ví sau hoàn nguyên phải $\ge 0.00$ VNĐ (`BR-FIN-02`, `ERR-WAL-001`). | Chuyển sang `DELETED`, xóa vĩnh viễn `IncomeExpenseTransaction` (`FR-TXN-05`) cùng `TransactionFee` liên kết (nếu có) và hoàn nguyên `Wallet.currentBalance` (`BR-FIN-02`). |

## 5.4 Vòng đời Giao dịch chuyển tiền

Thực thể Giao dịch chuyển tiền (`TransferTransaction`) quản lý vòng đời điều chuyển tiền giữa hai ví nội bộ từ lúc khởi tạo (`FR-TRF-01`) đến khi xóa (`FR-TRF-02`). Sau khi ghi nhận đồng thời (`BR-FIN-03`), bản ghi không cho phép chỉnh sửa mà chỉ cho phép xóa toàn bộ khi ví đích đủ số dư hoàn trả (`BR-FIN-02`).

- **Quy chiếu chéo**: `UC-TRF-01`, `UC-TRF-03`, `FR-TRF-01`, `FR-TRF-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

![Sơ đồ trạng thái của giao dịch chuyển tiền](docs/object-oriented-analysis/images/so-do-trang-thai-chuyen-tien.png){width=80%}

| Giai đoạn chuyển đổi | Điều kiện kiểm tra | Xử lý hệ thống |
| :--- | :--- | :--- |
| **Ghi nhận đồng thời**: `[*]` $\rightarrow$ `COMMITTED` (`transferFunds`) | Ví nguồn và ví đích đều `ACTIVE`, khác nhau (`BR-FIN-03`, `ERR-TRF-001`); $\text{amount} > 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`, `ERR-TRF-002`); phí $\ge 0.00$ VNĐ kèm danh mục cấp 3 nhánh `EXPENSE` nếu có phí (`BR-FIN-04`, `ERR-FEE-001`–`003`); số dư ví nguồn $\ge \text{amount} + \text{feeAmount}$ (`BR-FIN-02`, `ERR-WAL-001`). | Khởi tạo `TransferTransaction` ở trạng thái `COMMITTED` (`FR-TRF-01`) và `TransactionFee` liên kết (nếu có phí); đồng thời trừ $\text{amount} + \text{feeAmount}$ ở ví nguồn và cộng `amount` vào ví đích (`BR-FIN-02`, `BR-FIN-03`). |
| **Xóa và hoàn nguyên**: `COMMITTED` $\rightarrow$ `DELETED` $\rightarrow$ `[*]` (`deleteTransfer`) | Giao dịch chuyển tiền tồn tại (`FR-TRF-02`) và số dư hiện tại của ví đích `destinationWallet.currentBalance` $\ge \text{amount}$ (`BR-FIN-02`, `BR-FIN-03`, `ERR-WAL-001`). | Chuyển sang `DELETED`, xóa vĩnh viễn `TransferTransaction` (`FR-TRF-02`) cùng `TransactionFee` liên kết; đồng thời hoàn $\text{amount} + \text{feeAmount}$ về ví nguồn và trừ `amount` khỏi ví đích (`BR-FIN-02`, `BR-FIN-03`). |

## 5.5 Vòng đời thực thể Danh mục

Thực thể Danh mục (`Category` và `NeedGroup`) quản lý vòng đời phân loại thu chi từ lúc tạo mới (`FR-CAT-01`), đổi tên (`FR-CAT-02`), xóa mềm hoặc xóa vĩnh viễn đến khôi phục (`FR-CAT-03`, `FR-CAT-04`), bảo đảm cấu trúc phân cấp 3 cấp (`BR-FIN-04`) và toàn vẹn lịch sử giao dịch.

- **Quy chiếu chéo**: `UC-CAT-01` – `UC-CAT-04`, `FR-CAT-01` – `FR-CAT-04`, `BR-FIN-04`, `BR-NAM-01`, `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001` – `ERR-CAT-005`

![Sơ đồ trạng thái của danh mục](docs/object-oriented-analysis/images/so-do-trang-thai-danh-muc.png){width=80%}

| Giai đoạn chuyển đổi | Điều kiện kiểm tra | Xử lý hệ thống |
| :--- | :--- | :--- |
| **Khởi tạo danh mục**: `[*]` $\rightarrow$ `ACTIVE` (`addCategory`) | Nhóm nhu cầu cha (`parentCategoryId`) tồn tại và đang `ACTIVE` (`BR-FIN-04`, `ERR-CAT-005`); `categoryName` sau khi `trim` dài 1–50 ký tự, không rỗng (`BR-NAM-01`, `ERR-NAM-001`) và duy nhất trong cùng nhóm cha đang `ACTIVE` (`ERR-NAM-002`). | Cấp `categoryId` duy nhất, khởi tạo `Category` ở trạng thái `ACTIVE`, kế thừa `transactionType` từ nhóm cha và thiết lập liên kết phân cấp tới `NeedGroup` (`BR-FIN-04`, `FR-CAT-01`). |
| **Đổi tên danh mục**: Giữ nguyên `ACTIVE` (`renameCategory`) | Thể hiện `Category` tồn tại (`BR-FIN-04`); `newName` sau khi `trim` dài 1–50 ký tự, không rỗng (`BR-NAM-01`, `ERR-NAM-001`) và không trùng với mục khác đang `ACTIVE` trong cùng nhóm cha (`ERR-NAM-002`). | Cập nhật $\text{name} = \text{newName}$; giữ nguyên `categoryId`, trạng thái `status` và toàn bộ liên kết giao dịch lịch sử (`FR-CAT-02`, `BR-FIN-04`). |
| **Xóa mềm danh mục**: `ACTIVE` $\rightarrow$ `DELETED` (`deleteCategory`) | Mục đang `ACTIVE` và đã phát sinh giao dịch lịch sử (`BR-FIN-04`, `FR-CAT-03`); nếu là `NeedGroup`, không phải là nhóm `ACTIVE` cuối cùng của nhánh loại thu chi (`ERR-CAT-002`). | Chuyển `status` sang `DELETED` (xóa mềm), ẩn khỏi danh sách chọn khi tạo giao dịch mới nhưng bảo toàn liên kết phân cấp và hiển thị kèm nhãn "(đã xóa)" trên báo cáo lịch sử (`FR-CAT-03`, `BR-FIN-04`). |
| **Khôi phục danh mục**: `DELETED` $\rightarrow$ `ACTIVE` (`restoreCategory`) | Mục đang ở trạng thái `DELETED`; nhóm cha `NeedGroup` đang `ACTIVE` (`BR-FIN-04`, `ERR-CAT-004`) và tên mục không trùng với mục khác đang `ACTIVE` trong cùng nhóm cha (`BR-NAM-01`, `ERR-NAM-002`). | Chuyển `status` từ `DELETED` về `ACTIVE`, giữ nguyên `categoryId` và đưa mục hiển thị trở lại trên biểu mẫu ghi nhận giao dịch (`FR-CAT-03`, `FR-CAT-04`, `BR-FIN-04`). |
| **Xóa vĩnh viễn danh mục**: `ACTIVE` $\rightarrow$ `[*]` (`deleteCategory`) | Mục đang `ACTIVE`, chưa từng phát sinh bất kỳ giao dịch thu chi hay phí liên kết nào trong lịch sử (`FR-CAT-03`, `BR-FIN-04`), và (nếu là `NeedGroup`) không phải nhóm `ACTIVE` cuối cùng của loại thu chi (`ERR-CAT-002`). | Xóa vĩnh viễn thể hiện khỏi hệ thống lưu trữ (xóa cứng), gỡ bỏ liên kết phân cấp với nhóm cha và giải phóng tên khỏi phạm vi kiểm tra trùng tên (`BR-NAM-01`, `FR-CAT-03`). |
\newpage

# 6. Sơ đồ Robustness và thẻ CRC

Tài liệu này xác định cách các đối tượng bên trong phần mềm phối hợp để thực thi các ca sử dụng, chuyển từ góc nhìn tương tác hộp đen bên ngoài sang cấu trúc xử lý nội bộ và làm cơ sở phân bổ trách nhiệm cho các lớp đối tượng. Mô hình giữ tính trung lập công nghệ, độc lập với giao diện đồ họa, cơ sở dữ liệu hay ngôn ngữ lập trình.

## 6.1 Sơ đồ Robustness

Mục này thiết lập các sơ đồ Robustness nhằm làm rõ cơ chế phối hợp giữa 3 nhóm đối tượng phân tích:
- **Đối tượng giao diện (`Boundary`)**: Tiếp nhận thao tác của người dùng, thu thập dữ liệu đầu vào và hiển thị kết quả phản hồi.
- **Đối tượng điều khiển (`Control`)**: Điều phối quy trình xử lý, kiểm tra tính hợp lệ dữ liệu theo quy tắc nghiệp vụ và chỉ đạo cập nhật trạng thái.
- **Đối tượng thực thể (`Entity`)**: Lưu giữ thông tin nghiệp vụ lâu dài và thực hiện các phép tính nội tại của chính mình.

Luồng tương tác tuân thủ 3 nguyên tắc:
- Mọi yêu cầu từ người dùng đều đi qua đối tượng giao diện và chuyển giao cho đối tượng điều khiển xử lý.
- Đối tượng giao diện không tương tác trực tiếp với đối tượng thực thể mà phải thông qua đối tượng điều khiển.
- Đối tượng thực thể chỉ quản lý dữ liệu và quy tắc nội tại, không chủ động điều phối quy trình và không phụ thuộc vào tầng giao diện.

Mỗi nhóm chức năng lựa chọn một ca sử dụng tiêu biểu làm đại diện phân tích (ưu tiên kịch bản làm biến động trạng thái dữ liệu, tổng hợp báo cáo đa chiều hoặc thực thi quy tắc bất biến tài chính với sự tham gia của cả 3 nhóm đối tượng).

### 6.1.1 Quản lý ví

Người dùng tạo ví mới để theo dõi số dư và hạch toán dòng tiền độc lập. Hệ thống kiểm tra quy tắc đặt tên (`BR-NAM-01`), khởi tạo ví `ACTIVE` với số dư `0.00 VNĐ` (`BR-FIN-02`) và tính lại cơ cấu phân bổ số dư (`FR-WAL-04`).

- **Ca sử dụng đại diện**: `UC-WAL-01` (Tạo ví mới)
- **Thao tác hệ thống**: `createWallet(walletName, note)`
- **Quy chiếu chéo**: `UC-WAL-01`, `FR-WAL-01`, `FR-WAL-04`, `BR-NAM-01`, `BR-FIN-02`, `ERR-NAM-001`, `ERR-NAM-002`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | `WalletManagementView` | Nhận `walletName`, `note` và gửi yêu cầu tạo ví; hiển thị thông tin ví mới kèm cơ cấu số dư hoặc báo lỗi tên sai định dạng (`ERR-NAM-001`), trùng tên ví (`ERR-NAM-002`). |
| Điều khiển | `CashFlowCoordinator` | Chuẩn hóa chuỗi (`trim`), kiểm tra tên dài 1–50 ký tự và đối chiếu trùng tên trên cả ví `ACTIVE` lẫn `ARCHIVED` (`BR-NAM-01`, `ERR-NAM-001`, `ERR-NAM-002`), kiểm tra `note` tối đa 255 ký tự (`FR-WAL-01`); điều phối tạo ví và tính lại tỷ lệ phân bổ (`FR-WAL-04`). |
| Thực thể | `Wallet` | Cung cấp danh sách tên ví hiện có để kiểm tra trùng; lưu ví mới với `walletId` duy nhất, $\text{status} = \text{ACTIVE}$, `createdAt` (`FR-WAL-01`) và $\text{currentBalance} = 0.00$ VNĐ (`BR-FIN-02`). |

**Sơ đồ Robustness**

![Sơ đồ robustness tạo ví](docs/object-oriented-analysis/images/so-do-robustness-tao-vi.png){width=80%}

### 6.1.2 Ghi nhận thu chi

Người dùng ghi nhận khoản thu nhập hoặc chi tiêu kèm chi phí giao dịch vào sổ theo dõi. Hệ thống kiểm tra danh mục cấp 3 (`BR-FIN-04`) và số dư khả dụng (`BR-FIN-02`), sau đó lưu nguyên khối giao dịch gốc, khoản phí đi kèm và số dư ví mới.

- **Ca sử dụng đại diện**: `UC-TXN-01` (Ghi nhận giao dịch thu chi)
- **Thao tác hệ thống**: `recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- **Quy chiếu chéo**: `UC-TXN-01`, `FR-TXN-01`, `FR-TXN-02`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | `TransactionRecordingView` | Thu thập thông tin thu chi và chi phí đi kèm; hiển thị kết quả ghi nhận cùng số dư ví mới hoặc thông báo mã lỗi tương ứng (`ERR-FIN-001`, `ERR-FEE-001`–`003`, `ERR-WAL-001`). |
| Điều khiển | `CashFlowCoordinator` | Kiểm tra $\text{amount} > 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`), trạng thái `ACTIVE` của ví cùng danh mục giao dịch và danh mục phí cấp 3 nhánh `EXPENSE` (`BR-FIN-04`, `ERR-FEE-002`, `ERR-FEE-003`), phí $\ge 0.00$ VNĐ (`ERR-FEE-001`) và số dư khả dụng của ví (`BR-FIN-02`, `ERR-WAL-001`); điều phối lưu giao dịch, phí và cập nhật số dư ví. |
| Thực thể | `Wallet`, `Category`, `IncomeExpenseTransaction`, `TransactionFee` | `Category` xác nhận danh mục cấp 3 `ACTIVE` (`BR-FIN-04`); `Wallet` kiểm tra số dư và cập nhật `currentBalance` tức thời (`BR-FIN-02`); `IncomeExpenseTransaction` lưu khoản thu/chi (`FR-TXN-01`, `FR-TXN-02`); `TransactionFee` lưu khoản phí `EXPENSE` khi $\text{feeAmount} > 0.00$ VNĐ (`BR-FIN-02`). |

**Sơ đồ Robustness**

![Sơ đồ robustness ghi nhận thu chi](docs/object-oriented-analysis/images/so-do-robustness-ghi-nhan-thu-chi.png){width=80%}

### 6.1.3 Chuyển tiền nội bộ

Người dùng điều chuyển tiền giữa hai ví nội bộ đang hoạt động kèm chi phí phát sinh. Hệ thống kiểm tra hai ví phân biệt (`BR-FIN-03`) và số dư ví nguồn (`BR-FIN-02`), sau đó trừ tiền ví nguồn, cộng tiền ví đích đồng thời và tách bản ghi phí liên kết.

- **Ca sử dụng đại diện**: `UC-TRF-01` (Chuyển tiền nội bộ giữa hai ví)
- **Thao tác hệ thống**: `transferFunds(sourceWalletId, destinationWalletId, amount, transferDate, note, feeAmount, feeCategoryId)`
- **Quy chiếu chéo**: `UC-TRF-01`, `FR-TRF-01`, `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, `ERR-FIN-001`, `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | `FundTransferView` | Thu thập ví nguồn, ví đích, số tiền chuyển và chi phí; hiển thị kết quả cùng số dư mới của hai ví hoặc báo lỗi tương ứng (`ERR-TRF-001`, `ERR-TRF-002`, `ERR-FIN-001`, `ERR-FEE-001`–`003`, `ERR-WAL-001`). |
| Điều khiển | `CashFlowCoordinator` | Kiểm tra hai ví `ACTIVE` phân biệt (`BR-FIN-03`, `ERR-TRF-001`), $\text{amount} > 0.00$ VNĐ (`BR-FIN-01`, `ERR-FIN-001`, `ERR-TRF-002`), danh mục phí cấp 3 nhánh `EXPENSE` (`BR-FIN-04`, `ERR-FEE-001`–`003`) và số dư ví nguồn $\ge \text{amount} + \text{feeAmount}$ (`BR-FIN-02`, `ERR-WAL-001`); điều phối hạch toán đồng thời hai ví và tách bản ghi phí (`BR-FIN-03`). |
| Thực thể | `Wallet`, `Category`, `TransferTransaction`, `TransactionFee` | `Wallet` nguồn và đích xác nhận trạng thái `ACTIVE`, trừ $\text{amount} + \text{feeAmount}$ tại ví nguồn và cộng `amount` tại ví đích đồng thời (`BR-FIN-02`, `BR-FIN-03`); `Category` xác nhận danh mục phí cấp 3 `EXPENSE` (`BR-FIN-04`); `TransferTransaction` lưu giao dịch chuyển tiền (`FR-TRF-01`); `TransactionFee` lưu phí gắn với ví nguồn khi $\text{feeAmount} > 0.00$ VNĐ. |

**Sơ đồ Robustness**

![Sơ đồ robustness chuyển tiền giữa các ví](docs/object-oriented-analysis/images/so-do-robustness-chuyen-tien.png){width=80%}

### 6.1.4 Danh mục phân loại

Người dùng bổ sung một danh mục phân loại mới vào nhóm nhu cầu hiện có để phân loại giao dịch thu chi chi tiết, phục vụ báo cáo và phân tích tài chính.

- **Ca sử dụng đại diện**: `UC-CAT-01` (Bổ sung danh mục phân loại mới)
- **Thao tác hệ thống**: `addCategory(categoryName, parentCategoryId)`
- **Quy chiếu chéo**: `UC-CAT-01`, `FR-CAT-01`, `FR-CAT-02`, `BR-FIN-04`, `BR-NAM-01`, `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-005`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | `CategoryUI` | Thu thập thông tin danh mục từ biểu mẫu (tên danh mục, nhóm cha) và hiển thị kết quả phản hồi hoặc thông báo lỗi (`ERR-CAT-005`, `ERR-NAM-001`, `ERR-NAM-002`). |
| Điều khiển | `CategoryController` | Kiểm tra nhóm nhu cầu cha tồn tại và đang `ACTIVE` (`BR-FIN-04`, `ERR-CAT-005`); kiểm tra tên danh mục dài 1–50 ký tự sau khi `trim` (`ERR-NAM-001`) và duy nhất trong cùng nhóm cha (`BR-NAM-01`, `ERR-NAM-002`); khởi tạo thực thể `Category` ở trạng thái `ACTIVE` và thiết lập liên kết phân cấp 3 cấp (`BR-FIN-04`). |
| Thực thể | `NeedGroup`, `Category` | `NeedGroup` cung cấp trạng thái hoạt động và danh sách danh mục con để kiểm tra trùng tên; `Category` lưu danh mục cấp 3 mới tạo (`categoryId`, `name`, $\text{status} = \text{ACTIVE}$), liên kết với `NeedGroup` cha và kế thừa `transactionType`. |

**Sơ đồ Robustness**

![Sơ đồ robustness thêm danh mục](docs/object-oriented-analysis/images/so-do-robustness-them-danh-muc.png){width=80%}

### 6.1.5 Thống kê và báo cáo

Người dùng yêu cầu trích xuất chuỗi số liệu dòng tiền 12 tháng liên tục lùi từ mốc thời gian chỉ định, tính thu nhập, chi tiêu (gồm phí giao dịch), dòng tiền thuần, tỷ lệ tiết kiệm từng tháng theo chuẩn làm tròn ngân hàng và 3 mức bình quân tháng ở chế độ chỉ đọc.

- **Ca sử dụng đại diện**: `UC-REP-01` (Thống kê dòng tiền 12 tháng gần nhất)
- **Thao tác hệ thống**: `generateCashFlowReport(endMonth, endYear)`
- **Quy chiếu chéo**: `UC-REP-01`, `FR-REP-01`, `BR-FIN-01`, `BR-FIN-02`, `ERR-REP-001`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | `ReportUI` | Nhận mốc kết thúc (`endMonth`, `endYear`), gửi yêu cầu tạo báo cáo; hiển thị bảng 12 tháng liên tục, dòng 3 mức bình quân tháng và biểu đồ kết hợp cột - đường, hoặc hiển thị lỗi `ERR-REP-001` khi tham số không hợp lệ. |
| Điều khiển | `ReportController` | Kiểm tra `endMonth` thuộc $[1, 12]$ và $\text{endYear} \ge 2000$ (`ERR-REP-001`); sinh chuỗi 12 tháng liên tục; truy vấn chỉ đọc `IncomeExpenseTransaction`, `TransactionFee` và `TransferTransaction` (chỉ đọc ngày của phí chuyển khoản); tổng hợp thu chi (`BR-FIN-02`), tính `NetCashFlow`, `SavingsRate` (trung gian $\ge 4$ chữ số thập phân, làm tròn ngân hàng 2 chữ số, `0.00%` khi thu nhập $\le 0$ theo `BR-FIN-01`) và 3 mức bình quân tháng chia cho 12. |
| Thực thể | `IncomeExpenseTransaction`, `TransactionFee`, `TransferTransaction` | Cung cấp lịch sử giao dịch thu chi, chi phí liên kết (`EXPENSE`) và ngày giao dịch gốc của phí chuyển tiền phục vụ lọc theo tháng; bảo toàn nguyên vẹn trạng thái và số dư dữ liệu (bất biến chỉ đọc). |

**Sơ đồ Robustness**

![Sơ đồ robustness báo cáo dòng tiền](docs/object-oriented-analysis/images/so-do-robustness-bao-cao-dong-tien.png){width=80%}

### 6.1.6 Lưu trữ và sao lưu

Người dùng kết xuất toàn bộ dữ liệu tài chính ra tệp JSON mã hóa UTF-8 trên đĩa cứng cục bộ ở chế độ chỉ đọc, giữ nguyên độ chính xác số học thập phân theo `BR-FIN-01`, `CON-02`.

- **Ca sử dụng đại diện**: `UC-DAT-01` (Xuất dữ liệu tài chính)
- **Thao tác hệ thống**: `exportBackupData(exportPath)`
- **Quy chiếu chéo**: `UC-DAT-01`, `FR-DAT-01`, `BR-FIN-01`, `CON-02`, `ERR-DAT-001`

**Thành phần tham gia phân tích**

| Nhóm thành phần | Tên đối tượng | Trách nhiệm và ràng buộc |
| :--- | :--- | :--- |
| Giao diện | `BackupUI` | Nhận đường dẫn xuất dữ liệu (`exportPath`), gửi yêu cầu tới bộ điều khiển; hiển thị đường dẫn tệp, dung lượng và số bản ghi đã xuất hoặc cảnh báo khi lỗi quyền ghi đĩa (`ERR-DAT-001`). |
| Điều khiển | `BackupController` | Kiểm tra quyền truy cập tệp và dung lượng đĩa tại `exportPath`; truy vấn chỉ đọc toàn bộ thực thể tài chính; tuần tự hóa sang chuẩn JSON UTF-8 (giữ nguyên độ chính xác thập phân 2 chữ số theo `BR-FIN-01`, `CON-02`, `FR-DAT-01`); thực hiện ghi tệp an toàn (`Atomic File Write`) và trả kết quả cho `BackupUI`. |
| Thực thể | `FinancialDataStore` | Đại diện tập hợp các thực thể tài chính (`Wallet`, `NeedGroup`, `Category`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `BalanceAdjustmentRecord`); cung cấp ảnh chụp dữ liệu nhất quán ở chế độ chỉ đọc. |

**Sơ đồ Robustness**

![Sơ đồ robustness xuất dữ liệu sao lưu](docs/object-oriented-analysis/images/so-do-robustness-xuat-sao-luu.png){width=80%}

## 6.2 Thẻ CRC

Thẻ CRC (Class – Responsibility – Collaborator) chuyển tiếp từ mô hình phân tích hành vi sang cấu trúc lớp tĩnh, xác định ranh giới nhiệm vụ và quan hệ cộng tác giữa các lớp đối tượng dựa trên nguyên tắc chuyên gia thông tin (*Information Expert*):
- **Trách nhiệm thông tin (`Knows`)**: Lưu giữ thuộc tính định danh, trạng thái tài chính và các liên kết tham chiếu.
- **Trách nhiệm hành vi (`Does`)**: Thực hiện phép tính nghiệp vụ, kiểm soát toàn vẹn dữ liệu và điều phối luồng xử lý.

Phạm vi lập thẻ tập trung vào 7 lớp thực thể cốt lõi và lớp điều phối dòng tiền trung tâm (các đối tượng giá trị chỉ đóng gói dữ liệu bất biến nên không lập thẻ riêng).

### 6.2.1 Lớp Ví - Wallet

Lớp `Wallet` đại diện cho nguồn tiền thanh toán của người dùng để tiếp nhận, lưu trữ và chi xuất dòng tiền, bảo đảm số dư khả dụng luôn không âm theo thời gian thực (`BR-FIN-02`).

- **Tên lớp phân tích**: `Wallet`
- **Phân loại đối tượng**: Lớp thực thể (`Entity`)
- **Ca sử dụng liên quan**: `UC-WAL-01` – `UC-WAL-04`, `UC-TXN-01` – `UC-TXN-04`, `UC-TRF-01` – `UC-TRF-03`, `UC-DAT-01` – `UC-DAT-03`

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | Lưu `walletId`, `name` duy nhất (`BR-NAM-01`), `status` (`ACTIVE`, `ARCHIVED`), `note`, `createdAt`, số dư dẫn xuất `currentBalance` khởi điểm `0.00 VNĐ` (`BR-FIN-02`) và liên kết tới các bản ghi thu chi, chuyển tiền, chi phí, điều chỉnh số dư. | `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `BalanceAdjustmentRecord` |
| Hành vi | Kiểm tra định dạng và tính duy nhất của tên ví (`BR-NAM-01`, `ERR-NAM-001`, `ERR-NAM-002`); tính lại `currentBalance` tức thời từ lịch sử dòng tiền (`BR-FIN-02`) và từ chối thao tác gây âm quỹ (`ERR-WAL-001`); bảo vệ ví "Tiền mặt" mặc định và kiểm soát điều kiện chuyển `ARCHIVED` hoặc xóa vĩnh viễn (`FR-WAL-03`). | `CashFlowCoordinator`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `BalanceAdjustmentRecord` |

### 6.2.2 Lớp Bản ghi điều chỉnh số dư - BalanceAdjustmentRecord

Lớp `BalanceAdjustmentRecord` lưu vết các lần kiểm kê thực tế để cân đối số dư ví khớp với lượng tiền bên ngoài, mang tính bất biến sau khởi tạo và không làm lệch báo cáo thu chi (`BR-FIN-02`).

- **Tên lớp phân tích**: `BalanceAdjustmentRecord`
- **Phân loại đối tượng**: Lớp thực thể (`Entity`)
- **Ca sử dụng liên quan**: `UC-WAL-03`, `UC-WAL-04`, `UC-DAT-01` – `UC-DAT-03`

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | Lưu `recordId`, `walletId`, `previousBalance`, $\text{actualBalance} \ge 0.00$ VNĐ (`BR-FIN-02`), chênh lệch đại số $\text{difference} \neq 0.00$ VNĐ (`BR-FIN-01`), `reason` và `adjustedAt` (`FR-WAL-05`). | `Wallet` |
| Hành vi | Tính $\text{difference} = \text{actualBalance} - \text{previousBalance}$ (`BR-FIN-01`); từ chối tạo bản ghi khi $\text{difference} = 0.00$ VNĐ (`FR-WAL-05`), sai định dạng số (`ERR-FIN-001`) hoặc số dư thực tế âm (`ERR-WAL-001`); khóa bất biến sau khi lưu để bảo toàn vết kiểm toán (`BR-FIN-02`). | `CashFlowCoordinator`, `Wallet` |

### 6.2.3 Lớp Giao dịch thu chi - IncomeExpenseTransaction

Lớp `IncomeExpenseTransaction` ghi nhận sự kiện thu nhập (`INCOME`) hoặc chi tiêu (`EXPENSE`) tại một ví gắn với danh mục cấp 3 (`BR-FIN-04`), cập nhật tức thời số dư ví (`BR-FIN-02`) và cung cấp dữ liệu cho báo cáo thống kê.

- **Tên lớp phân tích**: `IncomeExpenseTransaction`
- **Phân loại đối tượng**: Lớp thực thể (`Entity`)
- **Ca sử dụng liên quan**: `UC-TXN-01` – `UC-TXN-04`, `UC-CAT-04`, `UC-REP-01`, `UC-REP-02`, `UC-DAT-01` – `UC-DAT-03`

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | Lưu `transactionId`, $\text{amount} > 0.00$ VNĐ (`BR-FIN-01`), `transactionDate`, `transactionType` (`INCOME`, `EXPENSE`) bất biến sau khởi tạo (`BR-FIN-04`), `note`, `createdAt` cùng tham chiếu tới `Wallet` (`walletId`), `Category` cấp 3 (`categoryId`) và `TransactionFee`. | `Wallet`, `Category`, `TransactionFee` |
| Hành vi | Kiểm tra số tiền hợp lệ (`BR-FIN-01`, `ERR-FIN-001`), ngày giao dịch và tính tương thích với danh mục cấp 3 (`BR-FIN-04`); cập nhật tăng/giảm số dư ví tức thời (`BR-FIN-02`, `ERR-WAL-001`); hoàn nguyên hai pha khi chỉnh sửa (`FR-TXN-04`) và đồng bộ vòng đời với `TransactionFee` đi kèm (`FR-TXN-05`). | `CashFlowCoordinator`, `Wallet`, `Category`, `TransactionFee` |

### 6.2.4 Lớp Chi phí giao dịch - TransactionFee

Lớp `TransactionFee` quản lý khoản phí phụ đi kèm giao dịch thu chi (`BR-FIN-02`) hoặc chuyển tiền nội bộ (`BR-FIN-03`), mang bản chất khoản chi (`EXPENSE`) và phụ thuộc sinh tử vào giao dịch gốc.

- **Tên lớp phân tích**: `TransactionFee`
- **Phân loại đối tượng**: Lớp thực thể (`Entity`)
- **Ca sử dụng liên quan**: `UC-TXN-01` – `UC-TXN-04`, `UC-TRF-01` – `UC-TRF-03`, `UC-REP-01`, `UC-REP-02`, `UC-DAT-01` – `UC-DAT-03`

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | Lưu `feeId`, $\text{amount} \ge 0.00$ VNĐ (`BR-FIN-01`, `BR-FIN-02`), mã ví chịu phí `walletId`, danh mục cấp 3 `categoryId` nhánh `EXPENSE` (`BR-FIN-04`), `parentTransactionId` và `parentTransactionType` (`INCOME_EXPENSE`, `TRANSFER`). | `Wallet`, `Category`, `IncomeExpenseTransaction`, `TransferTransaction` |
| Hành vi | Kiểm tra phí không âm (`ERR-FEE-001`) và bắt buộc gắn danh mục cấp 3 `EXPENSE` đang `ACTIVE` khi $\text{amount} > 0.00$ VNĐ (`BR-FIN-04`, `ERR-FEE-002`, `ERR-FEE-003`); chặn tạo, sửa, xóa độc lập ngoài giao dịch gốc (`BR-FIN-02`) và hạch toán vào tổng chi tiêu cũng như cơ cấu chi tiêu (`FR-REP-01`, `FR-REP-02`). | `CashFlowCoordinator`, `Wallet`, `Category`, `IncomeExpenseTransaction`, `TransferTransaction` |

### 6.2.5 Lớp Giao dịch chuyển tiền - TransferTransaction

Lớp `TransferTransaction` ghi nhận sự kiện điều chuyển tiền nội bộ giữa hai ví theo cơ chế hạch toán đồng thời, bảo toàn tổng số dư hệ thống trước phí và loại trừ khoản chuyển gốc khỏi báo cáo thu chi (`BR-FIN-03`).

- **Tên lớp phân tích**: `TransferTransaction`
- **Phân loại đối tượng**: Lớp thực thể (`Entity`)
- **Ca sử dụng liên quan**: `UC-TRF-01` – `UC-TRF-03`, `UC-REP-01`, `UC-REP-02`, `UC-DAT-01` – `UC-DAT-03`

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | Lưu `transferId`, `sourceWalletId`, `destinationWalletId` khác ví nguồn (`BR-FIN-03`), $\text{amount} > 0.00$ VNĐ (`BR-FIN-01`), `transactionDate`, `note`, `createdAt` và liên kết tới `TransactionFee` phát sinh. | `Wallet`, `TransactionFee` |
| Hành vi | Kiểm tra hai ví phân biệt (`ERR-TRF-001`), $\text{amount} > 0.00$ VNĐ (`ERR-FIN-001`, `ERR-TRF-002`) và đủ số dư ví nguồn (`ERR-WAL-001`); phối hợp trừ ví nguồn, cộng ví đích và tạo `TransactionFee` nguyên khối (`BR-FIN-02`, `BR-FIN-03`); kiểm tra số dư ví đích khi hủy chuyển tiền để hoàn nguyên đồng thời hai ví (`FR-TRF-02`). | `CashFlowCoordinator`, `Wallet`, `TransactionFee` |

### 6.2.6 Lớp Danh mục phân loại - Category

Lớp `Category` đại diện cho đơn vị phân loại chi tiết cấp 3 trong cây danh mục 3 cấp, trực tiếp gán nhãn mục đích tài chính cho các giao dịch thu chi và chi phí phát sinh (`BR-FIN-04`).

- **Tên lớp phân tích**: `Category`
- **Phân loại đối tượng**: Lớp thực thể (`Entity`)
- **Ca sử dụng liên quan**: `UC-CAT-01` – `UC-CAT-04`, `UC-TXN-01` – `UC-TXN-03`, `UC-TRF-01` – `UC-TRF-03`, `UC-REP-02`, `UC-DAT-01` – `UC-DAT-03`

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | Lưu `categoryId`, `name` dài 1–50 ký tự (`BR-NAM-01`), tham chiếu nhóm nhu cầu cha `needGroupId`, `status` (`ACTIVE`, `DELETED`), `icon`, `color` và `transactionType` (`INCOME`, `EXPENSE`) suy dẫn từ `NeedGroup` cha (`BR-FIN-04`). | `NeedGroup` |
| Hành vi | Kiểm tra định dạng và tính duy nhất của tên trong cùng `NeedGroup` cha (`BR-NAM-01`, `ERR-NAM-001`, `ERR-NAM-002`); cập nhật tên hiển thị (`UC-CAT-02`) hoặc chuyển nhóm cha cùng loại thu chi (`BR-FIN-04`, `ERR-CAT-001`, `ERR-CAT-003`); thực hiện xóa mềm (`DELETED`) nếu đã có giao dịch hoặc xóa vĩnh viễn nếu chưa có giao dịch (`UC-CAT-04`); khôi phục về `ACTIVE` khi nhóm cha đang hoạt động (`UC-CAT-03`, `ERR-CAT-004`). | `NeedGroup`, `IncomeExpenseTransaction`, `TransactionFee`, `CategoryController` |

### 6.2.7 Lớp Nhóm nhu cầu - NeedGroup

Lớp `NeedGroup` đại diện cho đơn vị phân loại trung gian cấp 2 trực thuộc loại thu chi gốc (cấp 1), gom cụm các danh mục chi tiết cùng tính chất tài chính phục vụ quản lý và phân tích cơ cấu thu chi (`BR-FIN-04`).

- **Tên lớp phân tích**: `NeedGroup`
- **Phân loại đối tượng**: Lớp thực thể (`Entity`)
- **Ca sử dụng liên quan**: `UC-CAT-01` – `UC-CAT-04`, `UC-REP-02`, `UC-DAT-01` – `UC-DAT-03`

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | Lưu `needGroupId`, `name` dài 1–50 ký tự (`BR-NAM-01`), `transactionType` (`INCOME`, `EXPENSE`) bất biến sau khởi tạo (`BR-FIN-04`), `status` (`ACTIVE`, `DELETED`), `icon`, `color` và tập liên kết tới các `Category` cấp 3 trực thuộc. | `Category` |
| Hành vi | Kiểm tra định dạng và tính duy nhất của tên nhóm trong cùng nhánh `TransactionType` (`BR-NAM-01`, `ERR-NAM-001`, `ERR-NAM-002`); quản lý tập danh mục con (`UC-CAT-01`, `UC-CAT-02`); chặn xóa nhóm `ACTIVE` cuối cùng của một loại thu chi (`BR-FIN-04`, `ERR-CAT-002`); thực thi xóa kéo theo (`cascade`) các danh mục con (`UC-CAT-04`) và khôi phục độc lập trạng thái `ACTIVE` của nhóm (`UC-CAT-03`). | `Category`, `CategoryController`, `ReportController` |

### 6.2.8 Lớp Điều phối dòng tiền - CashFlowCoordinator

Lớp `CashFlowCoordinator` đóng vai trò bộ điều khiển trung tâm điều phối các luồng nghiệp vụ quản lý ví, ghi nhận thu chi và chuyển tiền nội bộ, đảm bảo thực thi nguyên tử các quy tắc bất biến về số dư và chi phí giao dịch (`BR-FIN-01` – `BR-FIN-04`).

- **Tên lớp phân tích**: `CashFlowCoordinator`
- **Phân loại đối tượng**: Lớp điều khiển (`Control`)
- **Ca sử dụng liên quan**: `UC-WAL-01` – `UC-WAL-04`, `UC-TXN-01` – `UC-TXN-04`, `UC-TRF-01` – `UC-TRF-03`

| Nhóm trách nhiệm | Nội dung trách nhiệm cụ thể | Cộng tác viên |
| :--- | :--- | :--- |
| Thông tin | Không lưu trữ trạng thái dữ liệu nghiệp vụ lâu dài; giữ tham chiếu điều phối trong phiên giao dịch tới các thực thể `Wallet`, `Category`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee` và `BalanceAdjustmentRecord`. | `Wallet`, `Category`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `BalanceAdjustmentRecord` |
| Hành vi | Tiếp nhận yêu cầu từ tầng giao diện, kiểm tra tiền điều kiện về định dạng tên (`BR-NAM-01`), độ chính xác số học (`BR-FIN-01`), phân cấp danh mục cấp 3 (`BR-FIN-04`) và khả năng chi trả của ví (`BR-FIN-02`); điều phối thực thi nguyên tử (`Atomic Execution`) việc tạo/sửa/xóa giao dịch thu chi, chuyển tiền hai đầu ví (`BR-FIN-03`), đồng bộ `TransactionFee` đi kèm và ghi nhận `BalanceAdjustmentRecord` khi kiểm kê số dư. | `Wallet`, `Category`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `BalanceAdjustmentRecord` |

\newpage
# 7. Ma trận truy vết

Hệ thống ma trận truy vết hai chiều xác lập mối liên kết kiểm chứng giữa hồ sơ yêu cầu phần mềm (SRS) và mô hình phân tích hướng đối tượng (OOA), kết nối các yêu cầu chức năng, ca sử dụng và quy tắc nghiệp vụ với các lớp thực thể miền, đối tượng giá trị và thao tác hệ thống theo hai nguyên tắc:
- **Truy vết xuôi**: Xác nhận toàn bộ yêu cầu chức năng và quy tắc nghiệp vụ đều được hiện thực hóa đầy đủ trong mô hình phân tích, không bỏ sót ràng buộc nào.
- **Truy vết ngược**: Xác nhận mọi thực thể miền, đối tượng giá trị và thao tác hệ thống đều xuất phát từ nhu cầu thực tế, không có thành phần dư thừa hoặc mồ côi.

## 7.1 Truy vết yêu cầu chức năng

Bảng dưới đây đối chiếu trực tiếp từng yêu cầu chức năng và ca sử dụng theo 6 nhóm nghiệp vụ với các lớp thực thể miền, đối tượng giá trị cùng các thuộc tính và ràng buộc đảm nhiệm:

| Mã yêu cầu | Ca sử dụng | Lớp thực thể miền | Đối tượng giá trị liên quan | Thuộc tính và ràng buộc nghiệp vụ đảm nhiệm |
| :---- | :---- | :--------- | :-------- | :-------------- |
| **Nhóm Quản lý ví (`WAL`)** | | | | |
| `FR-WAL-01` | `UC-WAL-01` | `Wallet` | `Money` | Khởi tạo `walletId` (sinh tự động), `name` (1–50 ký tự, không trùng), `note`, `status` (`ACTIVE`), `createdAt`, $\text{currentBalance} = 0.00$ VNĐ. |
| `FR-WAL-02` | `UC-WAL-02` | `Wallet` | `Money` | Đọc toàn bộ thuộc tính (`walletId`, `name`, `status`, `note`, `createdAt`, `currentBalance`); tính tổng tài sản từ các ví `ACTIVE` và ví `ARCHIVED` có số dư dương. |
| `FR-WAL-03` | `UC-WAL-03` | `Wallet` | | Cập nhật `name`, `note`; chuyển đổi `status` giữa `ACTIVE` và `ARCHIVED`; cấm xóa hoặc lưu trữ ví "Tiền mặt" mặc định; chỉ xóa vĩnh viễn khi chưa phát sinh giao dịch. |
| `FR-WAL-04` | `UC-WAL-02` | `Wallet` | `Money`, `SpendingStructureShare` | Tính tỷ lệ phần trăm phân bổ số dư của từng ví trên tổng tài sản khả dụng theo quy tắc làm tròn ngân hàng. |
| `FR-WAL-05` | `UC-WAL-04` | `Wallet`, `BalanceAdjustmentRecord` | `Money`, `TransactionDate` | Đồng bộ `currentBalance` theo số dư kiểm kê; khởi tạo thể hiện bất biến `BalanceAdjustmentRecord` (`recordId`, `walletId`, `previousBalance`, `actualBalance`, `difference`, `reason`, `adjustedAt`). |
| **Nhóm Ghi nhận thu chi (`TXN`)** | | | | |
| `FR-TXN-01` | `UC-TXN-01` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` | Khởi tạo `IncomeExpenseTransaction` loại `EXPENSE`; kiểm tra số dư khả dụng; giảm `Wallet.currentBalance`; liên kết `Category` cấp 3; khởi tạo `TransactionFee` nếu có phí. |
| `FR-TXN-02` | `UC-TXN-01` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` | Khởi tạo `IncomeExpenseTransaction` loại `INCOME`; tăng `Wallet.currentBalance`; liên kết `Category` cấp 3 và `TransactionFee` (nếu có). |
| `FR-TXN-03` | `UC-TXN-02` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `NeedGroup`, `TransactionFee` | `Money`, `TransactionType`, `TransactionDate` | Đọc và lọc đa tiêu chí theo khoảng ngày (`TransactionDate`), ví (`walletId`), danh mục/nhóm (`categoryId`), loại giao dịch (`transactionType`). |
| `FR-TXN-04` | `UC-TXN-03` | `IncomeExpenseTransaction`, `Wallet`, `Category`, `TransactionFee` | `Money`, `TransactionDate` | Hoàn nguyên số dư ví cũ và kiểm tra khả năng chi trả trên ví mới; cập nhật `amount`, `transactionDate`, `walletId`, `categoryId`, `note` và đồng bộ `TransactionFee`; giữ nguyên `transactionType`. |
| `FR-TXN-05` | `UC-TXN-04` | `IncomeExpenseTransaction`, `Wallet`, `TransactionFee` | `Money` | Hoàn nguyên số dư ví (kiểm tra không âm khi xóa khoản thu); xóa vĩnh viễn `IncomeExpenseTransaction` và `TransactionFee` liên kết. |
| **Nhóm Chuyển tiền nội bộ (`TRF`)** | | | | |
| `FR-TRF-01` | `UC-TRF-01` | `TransferTransaction`, `Wallet`, `TransactionFee` | `Money`, `TransactionDate` | Kiểm tra $\text{sourceWalletId} \neq \text{destinationWalletId}$ và số dư ví nguồn $\ge \text{amount} + \text{feeAmount}$; trừ ví nguồn, cộng ví đích đồng thời; khởi tạo `TransferTransaction` và `TransactionFee` (nếu có). |
| `FR-TRF-02` | `UC-TRF-02`, `UC-TRF-03` | `TransferTransaction`, `Wallet`, `TransactionFee` | `Money`, `TransactionDate` | Truy vấn lịch sử chuyển tiền; khi xóa giao dịch, kiểm tra số dư ví đích $\ge \text{amount}$, hoàn nguyên đồng thời hai ví và xóa `TransactionFee` đi kèm. |
| **Nhóm Danh mục phân loại (`CAT`)** | | | | |
| `FR-CAT-01` | `UC-CAT-01` | `Category`, `NeedGroup` | `TransactionType` | Khởi tạo `Category` (`categoryId`, `name`, `needGroupId`, `transactionType`, $\text{status} = \text{ACTIVE}$); kiểm tra tên duy nhất trong cùng nhóm cha và duy trì phân cấp 3 cấp. |
| `FR-CAT-02` | `UC-CAT-02`, `UC-CAT-03` | `Category`, `NeedGroup` | `TransactionType` | Cập nhật `name`, `icon`, `color`; kiểm tra trùng tên sau khi `trim`; khôi phục mục từ trạng thái `DELETED` sang `ACTIVE`. |
| `FR-CAT-03` | `UC-CAT-03`, `UC-CAT-04` | `Category`, `NeedGroup`, `IncomeExpenseTransaction` | `TransactionType` | Chuyển `status` sang `DELETED` (xóa mềm) nếu đã có giao dịch tham chiếu; chỉ xóa vĩnh viễn khi chưa phát sinh giao dịch; cấm xóa `NeedGroup` hoạt động cuối cùng của mỗi loại thu chi. |
| **Nhóm Thống kê và báo cáo (`REP`)** | | | | |
| `FR-REP-01` | `UC-REP-01` | `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee` | `NetCashFlow`, `SavingsRate`, `Money`, `TransactionDate` | Kiểm tra tham số (`ERR-REP-001`, hỗ trợ mốc tương lai chỉ đọc); tổng hợp thu, chi và phí liên kết (chỉ đọc `TransferTransaction` để lấy ngày của phí chuyển khoản); tính dòng tiền thuần, tỷ lệ tiết kiệm (`0.00%` khi thu $\le 0$) và 3 mức bình quân chia cho 12 tháng. |
| `FR-REP-02` | `UC-REP-02` | `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `Category`, `NeedGroup` | `SpendingStructureShare`, `Money`, `TransactionType`, `TransactionDate` | Kiểm tra tham số (`ERR-REP-001`, chỉ đọc); phân bổ `EXPENSE` (gồm `TransactionFee`) hoặc `INCOME` theo `NeedGroup` và `Category` có phát sinh dương; giữ nhãn "(đã xóa)" cho mục `DELETED`; tính tỷ trọng và so sánh kỳ liền kề. |
| **Nhóm Lưu trữ và sao lưu (`DAT`)** | | | | |
| `FR-DAT-01` | `UC-DAT-01` | Toàn bộ 7 thực thể miền | `Money`, `TransactionDate`, `TransactionType` | Tuần tự hóa toàn bộ trạng thái của các thực thể miền ra tệp sao lưu JSON UTF-8 ở chế độ chỉ đọc. |
| `FR-DAT-02` | `UC-DAT-02` | Toàn bộ 7 thực thể miền | `Money`, `TransactionDate`, `TransactionType` | Kiểm định toàn vẹn 4 bước (`All-or-Nothing`) và phục hồi toàn bộ thực thể cùng mạng lưới liên kết từ tệp sao lưu. |
| `FR-DAT-03` | `UC-DAT-03` | `Wallet`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `BalanceAdjustmentRecord` | `Money` | Tái tính toán số dư toàn bộ ví từ lịch sử dòng tiền, đối soát 4 điều kiện toàn vẹn và cảnh báo sai lệch. |

## 7.2 Kiểm soát quy tắc nghiệp vụ

Bảng dưới đây xác lập mối liên kết giữa 5 quy tắc nghiệp vụ cốt lõi với các thực thể miền, đối tượng giá trị kiểm soát và mã lỗi phát sinh khi vi phạm:

| Mã quy tắc | Tên quy tắc nghiệp vụ | Thực thể và đối tượng giá trị kiểm soát | Mã lỗi miền tương ứng | Cơ chế kiểm soát và bất biến trong OOA |
| :---- | :------ | :--------- | :------ | :-------------- |
| `BR-FIN-01` | Quy tắc độ chính xác tài chính | `Money`, `NetCashFlow`, `SavingsRate`, `SpendingStructureShare` | `ERR-FIN-001`, `ERR-FIN-002` | Đóng gói giá trị tiền tệ và tỷ lệ trong các đối tượng giá trị bất biến; dùng số thập phân chính xác (cấm `float`/`double`), cố định 2 chữ số thập phân và làm tròn ngân hàng cho mọi phép chia. |
| `BR-FIN-02` | Quy tắc bảo toàn số dư ví | `Wallet`, `IncomeExpenseTransaction`, `TransferTransaction`, `BalanceAdjustmentRecord`, `TransactionFee` | `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003` | Duy trì bất biến số dư ví $\ge 0.00$ VNĐ; cấm gán trực tiếp mà tính lại từ lịch sử dòng tiền; kiểm tra khả năng chi trả trước mọi thao tác xuất quỹ; gắn vòng đời `TransactionFee` với giao dịch gốc và lưu vết `BalanceAdjustmentRecord` bất biến. |
| `BR-FIN-03` | Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ | `TransferTransaction`, `Wallet`, `TransactionFee` | `ERR-TRF-001`, `ERR-TRF-002` | Hạch toán nguyên tử đồng thời giữa ví nguồn và ví đích ($\text{sourceWalletId} \neq \text{destinationWalletId}$); bảo toàn tổng số dư hệ thống trước phí; tách riêng phí chuyển tiền thành khoản `EXPENSE`; hoàn nguyên đối xứng hai ví khi xóa chuyển tiền. |
| `BR-FIN-04` | Quy tắc phân cấp danh mục thu chi | `Category`, `NeedGroup`, `TransactionType` | `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`, `ERR-CAT-004`, `ERR-CAT-005` | Duy trì phân cấp 3 cấp (`TransactionType` $\rightarrow$ `NeedGroup` $\rightarrow$ `Category`); chỉ gán giao dịch vào cấp 3; cấm đổi `TransactionType`; áp dụng xóa mềm (`DELETED`) khi đã có giao dịch lịch sử và giữ tối thiểu một `NeedGroup` đang `ACTIVE` cho mỗi nhánh. |
| `BR-NAM-01` | Quy tắc đặt tên ví, nhóm nhu cầu và danh mục | `Wallet`, `NeedGroup`, `Category` | `ERR-NAM-001`, `ERR-NAM-002` | Chuẩn hóa `trim`, giới hạn 1–50 ký tự; kiểm tra tính duy nhất không phân biệt hoa thường theo phạm vi (toàn hệ thống với `Wallet`, cùng `TransactionType` với `NeedGroup`, cùng `NeedGroup` với `Category`); giải phóng tên khi xóa vĩnh viễn. |

\newpage

# 8. Tổng kết

Giai đoạn phân tích đã hoàn tất việc mô hình hóa các yêu cầu nghiệp vụ của Hệ thống Quản lý Tài chính Cá nhân thành cấu trúc dữ liệu và hành vi hệ thống độc lập với công nghệ cài đặt. Hệ thống ma trận truy vết và kiểm toán xác nhận toàn bộ yêu cầu chức năng, ca sử dụng cùng các quy tắc nghiệp vụ tài chính đều đã được ánh xạ vào mô hình, không còn thành phần dư thừa hay mồ côi. Kết quả phân tích trong tài liệu này là cơ sở kỹ thuật để chuyển sang giai đoạn thiết kế kiến trúc, xây dựng mô hình lớp và triển khai kiểm thử tự động.

\newpage

# 9. Tài liệu Trích dẫn

- Ambler, S. W. (2004). *The Object Primer: Agile Model-Driven Development with UML 2.0* (3rd ed.). Cambridge University Press.
- Fowler, M. (2002). *Patterns of Enterprise Application Architecture*. Addison-Wesley Professional.
- Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
- ISO/IEC/IEEE. (2018). *ISO/IEC/IEEE 29148:2018 Systems and software engineering — Life cycle processes — Requirements engineering*. IEEE.
- Jacobson, I., Christerson, M., Jonsson, P., & Overgaard, G. (1992). *Object-Oriented Software Engineering: A Use Case Driven Approach*. ACM Press / Addison-Wesley.
- Larman, C. (2004). *Applying UML and Patterns: An Introduction to Object-Oriented Analysis and Design and Iterative Development* (3rd ed.). Prentice Hall.