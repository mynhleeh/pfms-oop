---
papersize: a4
geometry:
  - top=20mm
  - bottom=20mm
  - left=20mm
  - right=20mm
mainfont: "Times New Roman"
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
  \includegraphics[width=0.18\textwidth]{docs/software-requirement-specification/images/logo.jpg}
\end{center}
```

\vspace*{0.6cm}

\begin{center}
  {\LARGE\bfseries HỆ THỐNG QUẢN LÝ TÀI CHÍNH CÁ NHÂN\par}
  \vspace*{0.4cm}
  {\Large\bfseries ĐẶC TẢ YÊU CẦU PHẦN MỀM (SRS)\par}
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

## MỤC LỤC TỔNG QUAN

- **[1. Giới thiệu tổng quan](#1-giới-thiệu-tổng-quan)**
  - [1.1 Mục đích](#11-mục-đích)
  - [1.2 Phạm vi sản phẩm](#12-phạm-vi-sản-phẩm)
    - [1.2.1 Phạm vi chức năng trong hệ thống](#121-phạm-vi-chức-năng-trong-hệ-thống)
    - [1.2.2 Ranh giới ngoài hệ thống và căn cứ loại trừ](#122-ranh-giới-ngoài-hệ-thống-và-căn-cứ-loại-trừ)
  - [1.3 Đối tượng sử dụng và hướng dẫn đọc tài liệu](#13-đối-tượng-sử-dụng-và-hướng-dẫn-đọc-tài-liệu)
  - [1.4 Thuật ngữ, từ viết tắt và định nghĩa](#14-thuật-ngữ-từ-viết-tắt-và-định-nghĩa)
  - [1.5 Quy ước mã định danh](#15-quy-ước-mã-định-danh)
  - [1.6 Quy ước mức độ bắt buộc của yêu cầu](#16-quy-ước-mức-độ-bắt-buộc-của-yêu-cầu)
- **[2. Mô tả tổng quát hệ thống](#2-mô-tả-tổng-quát-hệ-thống)**
  - [2.1 Bối cảnh hệ thống](#21-bối-cảnh-hệ-thống)
  - [2.2 Mục tiêu hệ thống](#22-mục-tiêu-hệ-thống)
  - [2.3 Đặc điểm người dùng](#23-đặc-điểm-người-dùng)
  - [2.4 Giới hạn kỹ thuật, giả định và sự phụ thuộc](#24-giới-hạn-kỹ-thuật-giả-định-và-sự-phụ-thuộc)
  - [2.5 Môi trường vận hành](#25-môi-trường-vận-hành)
  - [2.6 Luận đề kỹ thuật và phân tích đánh đổi](#26-luận-đề-kỹ-thuật-và-phân-tích-đánh-đổi)
- **[3. Quy tắc nghiệp vụ và quy định tài chính](#3-quy-tắc-nghiệp-vụ-và-quy-định-tài-chính)**
  - [3.1 Quy tắc độ chính xác tài chính (BR-FIN-01)](#31-quy-tắc-độ-chính-xác-tài-chính-br-fin-01)
  - [3.2 Quy tắc bảo toàn số dư ví (BR-FIN-02)](#32-quy-tắc-bảo-toàn-số-dư-ví-br-fin-02)
  - [3.3 Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (BR-FIN-03)](#33-quy-tắc-ghi-nhận-đồng-thời-khi-chuyển-tiền-nội-bộ-br-fin-03)
  - [3.4 Quy tắc phân cấp danh mục thu chi 3 cấp (BR-FIN-04)](#34-quy-tắc-phân-cấp-danh-mục-thu-chi-3-cấp-br-fin-04)
  - [3.5 Quy tắc tên của ví, nhóm nhu cầu và danh mục (BR-NAM-01)](#35-quy-tắc-tên-của-ví-nhóm-nhu-cầu-và-danh-mục-br-nam-01)
- **[4. Yêu cầu chức năng chi tiết](#4-yêu-cầu-chức-năng-chi-tiết)**
  - [4.1 FR-WAL: Nhóm chức năng Ví](#41-fr-wal-nhóm-chức-năng-ví)
    - [4.1.1 FR-WAL-01: Tạo ví mới](#411-fr-wal-01-tạo-ví-mới)
    - [4.1.2 FR-WAL-02: Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền](#412-fr-wal-02-xem-danh-sách-và-chi-tiết-số-dư-tính-lại-từ-lịch-sử-dòng-tiền)
    - [4.1.3 FR-WAL-03: Cập nhật thông tin ví](#413-fr-wal-03-cập-nhật-thông-tin-ví)
    - [4.1.4 FR-WAL-04: Thống kê cơ cấu số dư theo ví](#414-fr-wal-04-thống-kê-cơ-cấu-số-dư-theo-ví)
    - [4.1.5 FR-WAL-05: Điều chỉnh số dư ví](#415-fr-wal-05-điều-chỉnh-số-dư-ví)
  - [4.2 FR-TXN: Nhóm chức năng Ghi nhận thu chi](#42-fr-txn-nhóm-chức-năng-ghi-nhận-thu-chi)
    - [4.2.1 FR-TXN-01: Tạo mới giao dịch thu nhập](#421-fr-txn-01-tạo-mới-giao-dịch-thu-nhập)
    - [4.2.2 FR-TXN-02: Tạo mới giao dịch chi tiêu](#422-fr-txn-02-tạo-mới-giao-dịch-chi-tiêu)
    - [4.2.3 FR-TXN-03: Tra cứu và lọc lịch sử giao dịch thu chi](#423-fr-txn-03-tra-cứu-và-lọc-lịch-sử-giao-dịch-thu-chi)
    - [4.2.4 FR-TXN-04: Chỉnh sửa giao dịch thu chi](#424-fr-txn-04-chỉnh-sửa-giao-dịch-thu-chi)
    - [4.2.5 FR-TXN-05: Xóa giao dịch thu chi](#425-fr-txn-05-xóa-giao-dịch-thu-chi)
  - [4.3 FR-TRF: Nhóm chức năng Chuyển tiền nội bộ](#43-fr-trf-nhóm-chức-năng-chuyển-tiền-nội-bộ)
    - [4.3.1 FR-TRF-01: Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn](#431-fr-trf-01-chuyển-tiền-giữa-hai-ví-nội-bộ-kèm-chi-phí-tùy-chọn)
    - [4.3.2 FR-TRF-02: Tra cứu và xóa giao dịch chuyển tiền nội bộ](#432-fr-trf-02-tra-cứu-và-xóa-giao-dịch-chuyển-tiền-nội-bộ)
  - [4.4 FR-CAT: Nhóm chức năng Danh mục phân loại](#44-fr-cat-nhóm-chức-năng-danh-mục-phân-loại)
    - [4.4.1 FR-CAT-01: Tạo cấu trúc danh mục 3 cấp](#441-fr-cat-01-tạo-cấu-trúc-danh-mục-3-cấp)
    - [4.4.2 FR-CAT-02: Tùy biến danh mục người dùng](#442-fr-cat-02-tùy-biến-danh-mục-người-dùng)
    - [4.4.3 FR-CAT-03: Kiểm soát toàn vẹn khi cập nhật và xóa danh mục](#443-fr-cat-03-kiểm-soát-toàn-vẹn-khi-cập-nhật-và-xóa-danh-mục)
  - [4.5 FR-REP: Nhóm chức năng Thống kê và báo cáo](#45-fr-rep-nhóm-chức-năng-thống-kê-và-báo-cáo)
    - [4.5.1 FR-REP-01: Thống kê dòng tiền 12 tháng gần nhất](#451-fr-rep-01-thống-kê-dòng-tiền-12-tháng-gần-nhất)
    - [4.5.2 FR-REP-02: Phân tích cơ cấu thu chi theo nhóm và danh mục](#452-fr-rep-02-phân-tích-cơ-cấu-thu-chi-theo-nhóm-và-danh-mục)
  - [4.6 FR-DAT: Nhóm chức năng Lưu trữ và sao lưu dữ liệu](#46-fr-dat-nhóm-chức-năng-lưu-trữ-và-sao-lưu-dữ-liệu)
    - [4.6.1 FR-DAT-01: Xuất dữ liệu tài chính](#461-fr-dat-01-xuất-dữ-liệu-tài-chính)
    - [4.6.2 FR-DAT-02: Phục hồi dữ liệu hệ thống từ bản sao lưu](#462-fr-dat-02-phục-hồi-dữ-liệu-hệ-thống-từ-bản-sao-lưu)
    - [4.6.3 FR-DAT-03: Kiểm tra lại dữ liệu hệ thống](#463-fr-dat-03-kiểm-tra-lại-dữ-liệu-hệ-thống)
- **[5. Yêu cầu phi chức năng](#5-yêu-cầu-phi-chức-năng)**
  - [5.1 NFR-01: Độ chính xác và toàn vẹn dữ liệu tài chính](#51-nfr-01-độ-chính-xác-và-toàn-vẹn-dữ-liệu-tài-chính)
  - [5.2 NFR-02: Hiệu năng và độ trễ phản hồi](#52-nfr-02-hiệu-năng-và-độ-trễ-phản-hồi)
  - [5.3 NFR-03: Độ tin cậy và lưu trữ an toàn](#53-nfr-03-độ-tin-cậy-và-lưu-trữ-an-toàn)
  - [5.4 NFR-04: Khả năng bảo trì và kiến trúc hướng đối tượng](#54-nfr-04-khả-năng-bảo-trì-và-kiến-trúc-hướng-đối-tượng)
  - [5.5 NFR-05: Tính khả dụng và trải nghiệm người dùng](#55-nfr-05-tính-khả-dụng-và-trải-nghiệm-người-dùng)
  - [5.6 NFR-06: Tính tương thích và môi trường chạy](#56-nfr-06-tính-tương-thích-và-môi-trường-chạy)
- **[6. Đặc tả 20 ca sử dụng chi tiết](#6-đặc-tả-20-ca-sử-dụng-chi-tiết)**
  - [6.1 UC-WAL-01: Tạo ví mới](#61-uc-wal-01-tạo-ví-mới)
  - [6.2 UC-WAL-02: Theo dõi số dư tính lại từ lịch sử dòng tiền và cơ cấu ví](#62-uc-wal-02-theo-dõi-số-dư-tính-lại-từ-lịch-sử-dòng-tiền-và-cơ-cấu-ví)
  - [6.3 UC-WAL-03: Cập nhật thông tin ví](#63-uc-wal-03-cập-nhật-thông-tin-ví)
  - [6.4 UC-WAL-04: Điều chỉnh số dư ví](#64-uc-wal-04-điều-chỉnh-số-dư-ví)
  - [6.5 UC-TXN-01: Ghi nhận giao dịch thu chi](#65-uc-txn-01-ghi-nhận-giao-dịch-thu-chi)
  - [6.6 UC-TXN-02: Tra cứu và lọc lịch sử giao dịch thu chi](#66-uc-txn-02-tra-cứu-và-lọc-lịch-sử-giao-dịch-thu-chi)
  - [6.7 UC-TXN-03: Chỉnh sửa giao dịch thu chi](#67-uc-txn-03-chỉnh-sửa-giao-dịch-thu-chi)
  - [6.8 UC-TXN-04: Xóa giao dịch thu chi](#68-uc-txn-04-xóa-giao-dịch-thu-chi)
  - [6.9 UC-TRF-01: Chuyển tiền nội bộ giữa hai ví](#69-uc-trf-01-chuyển-tiền-nội-bộ-giữa-hai-ví)
  - [6.10 UC-TRF-02: Tra cứu lịch sử chuyển tiền](#610-uc-trf-02-tra-cứu-lịch-sử-chuyển-tiền)
  - [6.11 UC-TRF-03: Xóa giao dịch chuyển tiền](#611-uc-trf-03-xóa-giao-dịch-chuyển-tiền)
  - [6.12 UC-CAT-01: Bổ sung danh mục phân loại mới](#612-uc-cat-01-bổ-sung-danh-mục-phân-loại-mới)
  - [6.13 UC-CAT-02: Đổi tên danh mục](#613-uc-cat-02-đổi-tên-danh-mục)
  - [6.14 UC-CAT-03: Khôi phục hoặc tạo lại mục đã xóa](#614-uc-cat-03-khôi-phục-hoặc-tạo-lại-mục-đã-xóa)
  - [6.15 UC-CAT-04: Xóa danh mục hoặc nhóm](#615-uc-cat-04-xóa-danh-mục-hoặc-nhóm)
  - [6.16 UC-REP-01: Thống kê dòng tiền 12 tháng gần nhất](#616-uc-rep-01-thống-kê-dòng-tiền-12-tháng-gần-nhất)
  - [6.17 UC-REP-02: Phân tích cơ cấu thu chi theo nhóm và danh mục](#617-uc-rep-02-phân-tích-cơ-cấu-thu-chi-theo-nhóm-và-danh-mục)
  - [6.18 UC-DAT-01: Xuất dữ liệu tài chính](#618-uc-dat-01-xuất-dữ-liệu-tài-chính)
  - [6.19 UC-DAT-02: Phục hồi dữ liệu hệ thống từ bản sao lưu](#619-uc-dat-02-phục-hồi-dữ-liệu-hệ-thống-từ-bản-sao-lưu)
  - [6.20 UC-DAT-03: Kiểm tra lại dữ liệu hệ thống](#620-uc-dat-03-kiểm-tra-lại-dữ-liệu-hệ-thống)
- **[7. Sơ đồ ca sử dụng hệ thống](#7-sơ-đồ-ca-sử-dụng-hệ-thống)**
  - [7.1 Sơ đồ Ca sử dụng tổng quan kiến trúc nhóm chức năng](#71-sơ-đồ-ca-sử-dụng-tổng-quan-kiến-trúc-nhóm-chức-năng)
  - [7.2 Sơ đồ Ca sử dụng chi tiết theo 2 nhóm chức năng nghiệp vụ](#72-sơ-đồ-ca-sử-dụng-chi-tiết-theo-2-nhóm-chức-năng-nghiệp-vụ)
    - [7.2.1 Nhóm chức năng Dòng tiền và Tiền trong ví](#721-nhóm-chức-năng-dòng-tiền-và-tiền-trong-ví)
    - [7.2.2 Nhóm chức năng Thống kê, Báo cáo và Quản trị dữ liệu](#722-nhóm-chức-năng-thống-kê-báo-cáo-và-quản-trị-dữ-liệu)
- **[8. Ma trận kiểm chứng và truy vết yêu cầu](#8-ma-trận-kiểm-chứng-và-truy-vết-yêu-cầu)**
  - [8.1 Ma trận truy vết yêu cầu theo yêu cầu chức năng](#81-ma-trận-truy-vết-yêu-cầu-theo-yêu-cầu-chức-năng)
    - [8.1.1 Ma trận mục tiêu hệ thống theo yêu cầu chức năng](#811-ma-trận-mục-tiêu-hệ-thống-theo-yêu-cầu-chức-năng)
    - [8.1.2 Ma trận ca sử dụng theo yêu cầu chức năng](#812-ma-trận-ca-sử-dụng-theo-yêu-cầu-chức-năng)
    - [8.1.3 Ma trận thực thể tác động theo yêu cầu chức năng](#813-ma-trận-thực-thể-tác-động-theo-yêu-cầu-chức-năng)
    - [8.1.4 Ma trận quy tắc nghiệp vụ theo yêu cầu chức năng](#814-ma-trận-quy-tắc-nghiệp-vụ-theo-yêu-cầu-chức-năng)
  - [8.2 Ma trận phương pháp kiểm chứng yêu cầu](#82-ma-trận-phương-pháp-kiểm-chứng-yêu-cầu)
    - [8.2.1 Ma trận phương pháp kiểm chứng yêu cầu chức năng (20 FRs)](#821-ma-trận-phương-pháp-kiểm-chứng-yêu-cầu-chức-năng-20-frs)
    - [8.2.2 Ma trận phương pháp kiểm chứng yêu cầu phi chức năng (6 NFRs)](#822-ma-trận-phương-pháp-kiểm-chứng-yêu-cầu-phi-chức-năng-6-nfrs)
- **[9. Tổng kết](#9-tổng-kết)**
- **[10. Tài liệu trích dẫn](#10-tài-liệu-trích-dẫn)**

\newpage

## 1. GIỚI THIỆU TỔNG QUAN

### 1.1 Mục đích

Tài liệu Đặc tả Yêu cầu Phần mềm này xác định các yêu cầu cho Hệ thống Quản lý Tài chính Cá nhân. Các yêu cầu gồm yêu cầu chức năng, yêu cầu phi chức năng, các quy tắc nghiệp vụ và tiêu chí nghiệm thu kiểm chứng được. Tài liệu đặt ra căn cứ kỹ thuật cho hoạt động phân tích thiết kế hướng đối tượng. Căn cứ này áp dụng cả cho việc cài đặt mã nguồn trên nền tảng Java 17 LTS và triển khai bộ kiểm thử tự động độc lập công nghệ.

### 1.2 Phạm vi sản phẩm

Hệ thống Quản lý Tài chính Cá nhân (PFMS) là ứng dụng máy tính để bàn chạy cục bộ trên máy ảo Java, phục vụ một người dùng duy nhất. Hệ thống đáp ứng nhu cầu quản lý tài chính cá nhân trên máy tính cá nhân. Hệ thống độc lập hoàn toàn với các hệ thống tài chính doanh nghiệp và dịch vụ ngân hàng trực tuyến.

#### 1.2.1 Phạm vi chức năng trong hệ thống

Hệ thống cung cấp các nhóm năng lực nghiệp vụ sau đây, với quy ước mã định danh được chuẩn hóa.

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.0648}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1362}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.0648}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.7341}}|}
\hline
\rowcolor[gray]{0.9}
STT &
Nhóm &
Mã nhóm &
Mô tả nghiệp vụ \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
STT &
Nhóm &
Mã nhóm &
Mô tả nghiệp vụ \\ \hline
\endhead
1 &
Ví &
\texttt{WAL} &
Quản lý vòng đời ví gồm một ví tiền mặt có sẵn và các ví do người dùng
tự tạo. Tính số dư khả dụng và tỷ lệ phân bổ tiền trong các ví bằng số
thập phân chính xác. \\ \hline
2 &
Ghi nhận thu chi &
\texttt{TXN} &
Ghi nhận các giao dịch thu nhập (\texttt{INCOME}) và chi tiêu
(\texttt{EXPENSE}). Liên kết với ví thanh toán và danh mục. Ghi riêng
chi phí (nếu có) thành phí giao dịch liên kết. Cập nhật số dư ví cùng
lúc với việc ghi nhận giao dịch. \\ \hline
3 &
Chuyển tiền nội bộ &
\texttt{TRF} &
Chuyển tiền giữa hai ví nội bộ theo cơ chế ghi nhận đồng thời (trừ ví
nguồn, cộng ví đích). Giữ cho tổng số dư trong các ví không đổi, ngoại
trừ chi phí chuyển tiền. \\ \hline
4 &
Danh mục phân loại &
\texttt{CAT} &
Giữ cấu trúc danh mục 3 cấp gồm Loại thu chi -\textgreater{} Nhóm nhu
cầu -\textgreater{} Danh mục chi tiết; bảo đảm liên kết hợp lệ tới nhóm
cha khi gắn nhãn giao dịch. \\ \hline
5 &
Thống kê và báo cáo &
\texttt{REP} &
Trích xuất chuỗi dữ liệu 12 tháng liên tục kết thúc tại tháng người dùng
chọn, mặc định là tháng hiện tại. Phân tích tỷ lệ cơ cấu chi tiêu theo
nhóm danh mục, dòng tiền thuần và tỷ lệ tiết kiệm định kỳ. \\ \hline
6 &
Lưu trữ và sao lưu dữ liệu &
\texttt{DAT} &
Lưu dữ liệu lâu dài trên tệp JSON cục bộ bằng cách ghi tệp an toàn, tệp
chính không bị hỏng khi mất điện giữa chừng. Hỗ trợ xuất và nhập tệp sao
lưu độc lập để phục vụ kiểm tra lại dữ liệu. \\ \hline
\end{longtable}
\endgroup
```

#### 1.2.2 Ranh giới ngoài hệ thống và căn cứ loại trừ

Các hạng mục kỹ thuật và nghiệp vụ được loại trừ khỏi ranh giới giải pháp của hệ thống.

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.0527}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2080}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.3800}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.3594}}|}
\hline
\rowcolor[gray]{0.9}
STT &
Hạng mục &
Ranh giới phân định cụ thể &
Căn cứ loại trừ \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
STT &
Hạng mục &
Ranh giới phân định cụ thể &
Căn cứ loại trừ \\ \hline
\endhead
1 &
Tích hợp Open Banking và cổng thanh toán trực tuyến &
Hệ thống không tích hợp Open Banking API hoặc Webhook tự động cào dữ
liệu giao dịch; Người dùng trực tiếp nhập các giao dịch đã xảy ra. &
Ứng dụng vận hành ngoại tuyến và độc lập trên máy trạm cục bộ. Ứng dụng
không lưu thông tin xác thực của bên thứ ba (Tài khoản, khóa truy cập)
hay phụ thuộc vào hạ tầng mạng ngoài. \\ \hline
2 &
Quản trị đa tài khoản và kiểm soát truy cập phân quyền &
Hệ thống chỉ phục vụ một Người dùng duy nhất; loại trừ việc đăng ký
nhiều tài khoản trực tuyến qua mạng hoặc phân cấp quyền hạn quản trị. &
Ứng dụng máy tính để bàn đơn tiến trình, phục vụ một người dùng duy nhất
hệ thống (đơn người dùng, đơn nhiệm) (\texttt{AS-01}), nên không cần phân
quyền nhiều cấp và không phát sinh xung đột ghi đồng thời. \\ \hline
3 &
Chuyển mạch thanh toán và tạo giao dịch tiền thực &
Hệ thống không gửi lệnh chuyển tiền đến mạng lưới thanh toán liên ngân
hàng. Dữ liệu dòng tiền chỉ phản ánh thông tin kế toán quản trị nội bộ
sau khi giao dịch thực tế đã diễn ra ngoài đời thực. &
Giới hạn hệ thống trong phạm vi sổ sách kế toán cá nhân, không xử lý
thanh toán thật. \\ \hline
\end{longtable}
\endgroup
```

### 1.3 Đối tượng sử dụng và hướng dẫn đọc tài liệu

Tài liệu phục vụ nhóm vai trò kỹ thuật với mục tiêu sử dụng và lộ trình tiếp cận nội dung tương ứng.

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1894}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.6229}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1877}}|}
\hline
\rowcolor[gray]{0.9}
Nhóm đối tượng &
Mục đích sử dụng tài liệu &
Phạm vi tài liệu trọng tâm \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Nhóm đối tượng &
Mục đích sử dụng tài liệu &
Phạm vi tài liệu trọng tâm \\ \hline
\endhead
Kỹ sư phân tích và thiết kế &
Xác định ranh giới hệ thống, chuyển hóa yêu cầu nghiệp vụ thành kiến
trúc và thiết kế phần mềm chi tiết &
Mục 1, Mục 2, Mục 4, Mục 6, Mục 7 \\ \hline
Kỹ sư phát triển phần mềm &
Cài đặt logic nghiệp vụ, cấu trúc dữ liệu và xử lý ngoại lệ trên nền
tảng Java 17 &
Mục 3, Mục 4, Mục 6 \\ \hline
Kỹ sư kiểm thử phần mềm &
Xây dựng ca kiểm thử đơn vị và kiểm thử tích hợp dựa trên luồng sự kiện
và điều kiện biên &
Mục 3, Mục 6, Mục 8 \\ \hline
Cán bộ thẩm định kỹ thuật &
So khớp độ bao phủ của yêu cầu qua ma trận kiểm chứng và truy vết yêu
cầu, kiểm tra mức độ đáp ứng yêu cầu phi chức năng ở Mục 5 &
Toàn bộ tài liệu \\ \hline
\end{longtable}
\endgroup
```

### 1.4 Thuật ngữ, từ viết tắt và định nghĩa

Danh mục các thuật ngữ chuyên môn và định nghĩa nghiệp vụ được sử dụng thống nhất trong toàn bộ tài liệu đặc tả:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1571}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8429}}|}
\hline
\rowcolor[gray]{0.9}
Thuật ngữ &
Định nghĩa \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuật ngữ &
Định nghĩa \\ \hline
\endhead
Ví &
Tài khoản lưu giữ số dư tiền khả dụng thực tế của người dùng. Hệ thống
có sẵn một ví tiền mặt. Người dùng có thể tạo thêm các ví khác. \\ \hline
Số dư khả dụng &
Số tiền hiện có trong một ví, bằng tổng số tiền đã vào ví trừ tổng số
tiền đã ra khỏi ví kể từ khi ví được tạo với số dư 0.00. Số dư này không
được âm. \\ \hline
Tổng số dư &
Tổng số dư khả dụng của các ví trong hệ thống. \\ \hline
Giao dịch thu chi &
Bản ghi biến động tài chính đơn lẻ làm tăng (\texttt{INCOME}) hoặc giảm
(\texttt{EXPENSE}) số dư khả dụng của một ví xác định, có gắn nhãn phân
loại danh mục. \\ \hline
Ví nguồn, ví đích &
Ví nguồn là ví bị trừ tiền, ví đích là ví được cộng tiền trong một lần
chuyển tiền nội bộ. \\ \hline
Chuyển tiền nội bộ &
Nghiệp vụ chuyển tiền giữa hai ví nội bộ theo cơ chế ghi nhận đồng thời
(trừ ví nguồn, cộng ví đích), bảo toàn tổng số dư hệ thống trừ chi phí
chuyển tiền (nếu có). \\ \hline
Giao dịch chuyển tiền &
Bản ghi dữ liệu của một lần chuyển tiền nội bộ, lưu thông tin ví nguồn,
ví đích, số tiền chuyển và liên kết phí giao dịch (nếu có). \\ \hline
Chi phí &
Số tiền phí phát sinh khi ghi nhận một giao dịch thu chi hoặc một giao
dịch chuyển tiền, có giá trị không âm. Chi phí là số tiền của phí, khác
với chi tiêu (Giao dịch làm giảm số dư ví). \\ \hline
Phí giao dịch &
Giao dịch chi tiêu (\texttt{EXPENSE}) do hệ thống tự động ghi nhận cho
chi phí phát sinh và liên kết với giao dịch gốc (thu chi hoặc chuyển
tiền). Người dùng chọn danh mục cho phí; phí được cập nhật hoặc xóa đồng
thời theo giao dịch gốc. \\ \hline
Cấu trúc danh mục &
Mô hình phân loại dạng cây 3 cấp gồm Loại thu chi -\textgreater{} Nhóm
nhu cầu -\textgreater{} Danh mục chi tiết. \\ \hline
Hoàn nguyên &
Đưa số dư và dữ liệu về đúng trạng thái trước khi thao tác xảy ra; dùng
khi xóa một giao dịch hoặc khi thao tác nhiều bước bị lỗi. \\ \hline
Ghi nhận đồng thời &
Cơ chế cập nhật cùng lúc hai biến động số dư khi chuyển tiền: trừ tiền
ví nguồn và cộng tiền ví đích cùng được ghi nhận hoặc cùng không được
ghi nhận. \\ \hline
Khoản ghi &
Một dòng ghi số tiền tăng hoặc giảm của một ví, như phần trừ tiền hoặc
phần cộng tiền của một lần chuyển tiền, hoặc một lần điều chỉnh số dư
ví. \\ \hline
Điều chỉnh số dư &
Thao tác đưa số dư khả dụng của một ví về đúng số tiền thực tế Người
dùng đang có. Chênh lệch được ghi thành một bản ghi riêng, có thể truy
vết. \\ \hline
Sổ giao dịch thu chi &
Nơi lưu các giao dịch thu chi, kể cả phí giao dịch. \\ \hline
Sổ giao dịch chuyển tiền &
Nơi lưu các giao dịch chuyển tiền. \\ \hline
Sổ điều chỉnh số dư &
Nơi lưu các lần điều chỉnh số dư. Mỗi bản ghi độc lập và không được sửa. \\ \hline
Lịch sử dòng tiền &
Toàn bộ biến động số dư của một ví: giao dịch thu chi (Kể cả phí giao
dịch), giao dịch chuyển tiền và điều chỉnh số dư. Dùng để tính lại số
dư. \\ \hline
Lịch sử giao dịch thu chi &
Danh sách giao dịch thu chi do chức năng tra cứu và lọc trả về, kể cả
phí giao dịch. Không gồm giao dịch chuyển tiền và điều chỉnh số dư. \\ \hline
Dòng tiền thuần &
Tổng số tiền thu trừ tổng số tiền chi trong cùng một khoảng thời gian.
Khoản thu là giao dịch thu chi loại \texttt{INCOME}, khoản chi là loại
\texttt{EXPENSE}. \\ \hline
Tỷ lệ tiết kiệm &
Dòng tiền thuần của một tháng chia tổng số tiền thu của tháng đó, nhân
100\%. Bằng 0.00\% khi tổng số tiền thu của tháng nhỏ hơn hoặc bằng 0. \\ \hline
Số thực nhị phân &
Kiểu số thực (float, double) mà máy tính lưu bằng các chữ số 0 và 1 (Hệ
nhị phân), có thể làm tròn sai khi tính tiền. Hệ thống không dùng kiểu
này trong tính toán nghiệp vụ. \\ \hline
Số thập phân chính xác &
Kiểu số học có phần thập phân cố định, bảo đảm độ chính xác tuyệt đối và
loại bỏ hoàn toàn sai số làm tròn khi tính toán số tiền và tỷ lệ phần
trăm. \\ \hline
Làm tròn ngân hàng &
Cách làm tròn về số chẵn gần nhất khi giá trị nằm đúng giữa hai mức. Ví
dụ làm tròn đến số nguyên: 2.5 thành 2, 3.5 thành 4. \\ \hline
Ghi tệp an toàn &
Cách ghi dữ liệu xuống tệp sao cho tệp chính chỉ được thay bằng nội dung
mới sau khi toàn bộ nội dung mới đã ghi xong. Nếu quá trình bị ngắt giữa
chừng, tệp cũ vẫn giữ nguyên vẹn. \\ \hline
Ngày tháng &
Giá trị ngày viết theo định dạng YYYY-MM-DD (Năm, tháng, ngày, ngăn cách
bằng dấu gạch nối), dùng cho mọi ngày được nhập và lưu trong tệp xuất
hoặc tệp nhập. \\ \hline
Người dùng &
Người duy nhất sử dụng hệ thống và có toàn quyền với dữ liệu. Người dùng
sử dụng toàn bộ 6 nhóm chức năng nghiệp vụ trên ứng dụng máy tính để bàn
chạy trực tiếp trên máy của người dùng. \\ \hline
\end{longtable}
\endgroup
```

Danh mục các từ viết tắt kỹ thuật và nghiệp vụ xuất hiện trong tài liệu kèm tên đầy đủ và ý nghĩa sử dụng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.0998}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.2941}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.6061}}|}
\hline
\rowcolor[gray]{0.9}
Viết tắt &
Tên đầy đủ &
Ý nghĩa \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Viết tắt &
Tên đầy đủ &
Ý nghĩa \\ \hline
\endhead
JDK &
Java Development Kit &
Bộ công cụ biên dịch và phát triển ứng dụng Java. \\ \hline
JRE &
Java Runtime Environment &
Môi trường chạy mã bytecode của ứng dụng trên máy trạm. \\ \hline
JVM &
Java Virtual Machine &
Máy ảo Java chạy ứng dụng cục bộ. \\ \hline
LTS &
Long-Term Support &
Nhánh phiên bản Java được nhà cung cấp hỗ trợ dài hạn. \\ \hline
PFMS &
Personal Finance Management System &
Tên hệ thống trong phạm vi tài liệu này. \\ \hline
RTM &
Requirements Traceability Matrix &
Ma trận kiểm chứng và truy vết yêu cầu tại Mục 8. \\ \hline
UC &
Use case &
Ca sử dụng đặc tả tại Mục 6 và mô hình hóa tại Mục 7. \\ \hline
\end{longtable}
\endgroup
```

### 1.5 Quy ước mã định danh

Hệ thống dùng cấu trúc mã phân cấp để nhận diện duy nhất và truy vết hai chiều mọi thực thể yêu cầu.

Mã định danh nối các khối bằng dấu gạch ngang theo dạng chung `<TIỀN_TỐ>[-<NHÓM>][-NN][.x]`. Khối trong ngoặc vuông có mặt hay không tùy từng tiền tố, như bảng dưới.

Trong đó, `<NHÓM>` nhận giá trị là một mã `DOMAIN` (Mã nhóm chức năng nghiệp vụ) hoặc một mã nhóm quy tắc nghiệp vụ: `FIN` đối với quy tắc tài chính, `NAM` đối với quy tắc về tên, `FEE` đối với phí giao dịch (Chỉ dùng trong mã lỗi). `NN` là số thứ tự gồm 2 chữ số (Bắt đầu từ `01`), riêng mã lỗi dùng `NNN` gồm 3 chữ số (Bắt đầu từ `001`), đánh độc lập trong từng nhóm khi mã có khối nhóm. `x` là số thứ tự tiêu chí nghiệm thu (Bắt đầu từ `1`). Mã `AC` giữ nguyên khối `<NHÓM>-NN` của mã `FR` tương ứng, thay tiền tố bằng `AC`, rồi thêm `.x`.

Mã định danh gồm 12 dạng mã sau, dùng 10 tiền tố:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1847}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1847}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1758}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.3062}}|}
\hline
\rowcolor[gray]{0.9}
Tiền tố &
Nghĩa của mã &
Thực thể định danh &
Ví dụ &
Phạm vi áp dụng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tiền tố &
Nghĩa của mã &
Thực thể định danh &
Ví dụ &
Phạm vi áp dụng \\ \hline
\endhead
\texttt{G-NN} &
Goal &
Mục tiêu hệ thống &
\texttt{G-01} &
Bảng mục tiêu hệ thống tại Mục 2.2 \\ \hline
\texttt{CON-NN} &
Constraint &
Giới hạn kỹ thuật bắt buộc &
\texttt{CON-01} &
Danh mục giới hạn tại Mục 2.4 \\ \hline
\texttt{AS-NN} &
Assumption &
Giả định vận hành hệ thống &
\texttt{AS-01} &
Danh mục giả định tại Mục 2.4 \\ \hline
\texttt{DE-NN} &
Dependency &
Sự phụ thuộc hệ thống &
\texttt{DE-01} &
Danh mục phụ thuộc tại Mục 2.4 \\ \hline
\texttt{BR-FIN-NN} &
Business Rule, Finance &
Quy tắc nghiệp vụ tài chính &
\texttt{BR-FIN-01} &
Các quy tắc tài chính tại Mục 3 \\ \hline
\texttt{BR-NAM-NN} &
Business Rule, Name &
Quy tắc nghiệp vụ về tên &
\texttt{BR-NAM-01} &
Quy tắc về tên của ví, nhóm nhu cầu và danh mục tại Mục 3.5 \\ \hline
\texttt{ERR-NHÓM-NNN} &
Error &
Mã lỗi nghiệp vụ &
\texttt{ERR-NAM-001} &
Các bảng mã lỗi tại Mục 3 \\ \hline
\texttt{FR-DOMAIN} &
Functional Requirement &
Mã nhóm chức năng &
\texttt{FR-WAL} &
Định danh nhóm chức năng tại Mục 1.2 và Mục 4 \\ \hline
\texttt{FR-DOMAIN-NN} &
Functional Requirement &
Yêu cầu chức năng &
\texttt{FR-WAL-01} &
Danh mục 20 yêu cầu chức năng tại Mục 4 \\ \hline
\texttt{UC-DOMAIN-NN} &
Use Case &
Ca sử dụng &
\texttt{UC-WAL-01} &
Danh mục 20 ca sử dụng tại Mục 6 \\ \hline
\texttt{NFR-NN} &
Non-Functional Requirement &
Yêu cầu phi chức năng &
\texttt{NFR-01} &
Danh mục 6 yêu cầu phi chức năng tại Mục 5 \\ \hline
\texttt{AC-DOMAIN-NN.x} &
Acceptance Criteria &
Tiêu chí nghiệm thu &
\texttt{AC-WAL-01.1} &
Tiêu chí nghiệm thu của từng FR tại Mục 4 \\ \hline
\end{longtable}
\endgroup
```

Danh sách giá trị của biến `DOMAIN` là tập hợp cố định gồm các nhóm chức năng nghiệp vụ, tương ứng trực tiếp với cấu trúc phân rã chức năng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\centering\arraybackslash}p{(\linewidth - 14\tabcolsep - 8\arrayrulewidth) * \real{0.1081}}|>{\arraybackslash}p{(\linewidth - 14\tabcolsep - 8\arrayrulewidth) * \real{0.2297}}|>{\centering\arraybackslash}p{(\linewidth - 14\tabcolsep - 8\arrayrulewidth) * \real{0.1081}}|>{\arraybackslash}p{(\linewidth - 14\tabcolsep - 8\arrayrulewidth) * \real{0.1525}}|>{\arraybackslash}p{(\linewidth - 14\tabcolsep - 8\arrayrulewidth) * \real{0.1853}}|>{\centering\arraybackslash}p{(\linewidth - 14\tabcolsep - 8\arrayrulewidth) * \real{0.1081}}|>{\centering\arraybackslash}p{(\linewidth - 14\tabcolsep - 8\arrayrulewidth) * \real{0.1081}}|}
\hline
\rowcolor[gray]{0.9}
STT &
Nhóm &
Mã \texttt{DOMAIN} &
Nghĩa của mã &
Đối tượng quản lý &
Số lượng FR &
Số lượng UC \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
STT &
Nhóm &
Mã \texttt{DOMAIN} &
Nghĩa của mã &
Đối tượng quản lý &
Số lượng FR &
Số lượng UC \\ \hline
\endhead
1 &
Ví &
\texttt{WAL} &
Wallet &
Ví &
5 &
4 \\ \hline
2 &
Ghi nhận thu chi &
\texttt{TXN} &
Transaction &
Giao dịch thu chi &
5 &
4 \\ \hline
3 &
Chuyển tiền nội bộ &
\texttt{TRF} &
Transfer &
Giao dịch chuyển tiền &
2 &
3 \\ \hline
4 &
Danh mục phân loại &
\texttt{CAT} &
Category &
Danh mục &
3 &
4 \\ \hline
5 &
Thống kê và báo cáo &
\texttt{REP} &
Statistics \& Report &
Thống kê tháng &
2 &
2 \\ \hline
6 &
Lưu trữ và sao lưu dữ liệu &
\texttt{DAT} &
Data Management &
Dữ liệu tài chính &
3 &
3 \\ \hline
\textbf{Tổng cộng} &
\textbf{6 nhóm chức năng} &
\strut &
\strut &
\strut &
\textbf{20 FR} &
\textbf{20 UC} \\ \hline
\end{longtable}
\endgroup
```

### 1.6 Quy ước mức độ bắt buộc của yêu cầu

Tài liệu dùng từ chỉ mức, viết thường, để xác định mức độ bắt buộc của giới hạn, quy tắc nghiệp vụ, yêu cầu chức năng và yêu cầu phi chức năng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1108}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1360}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.7531}}|}
\hline
\rowcolor[gray]{0.9}
Từ chỉ mức &
Mức độ bắt buộc &
Ý nghĩa \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Từ chỉ mức &
Mức độ bắt buộc &
Ý nghĩa \\ \hline
\endhead
Phải &
Bắt buộc &
Chức năng hoặc điều kiện hệ thống phải thỏa mãn để được nghiệm thu. \\ \hline
Không được &
Cấm hoàn toàn &
Hành vi hoặc trạng thái hệ thống hoàn toàn không được phép xảy ra hoặc
phải từ chối tiếp nhận. \\ \hline
Nên &
Khuyến cáo &
Quy cách được ưu tiên áp dụng, chỉ bỏ qua khi có lý do kỹ thuật xác
đáng. \\ \hline
Có thể, được phép &
Tùy chọn &
Chức năng hoặc lựa chọn mà người dùng có thể dùng hoặc bỏ qua. \\ \hline
\end{longtable}
\endgroup
```

\newpage

## 2. MÔ TẢ TỔNG QUÁT HỆ THỐNG

### 2.1 Bối cảnh hệ thống

Hệ thống quản lý tài chính cá nhân (PFMS) là ứng dụng máy tính để bàn cục bộ đơn người dùng chạy trên nền tảng Java 17. Hệ thống quản lý số dư ví theo cơ chế ghi nhận đồng thời và giữ cho số dư luôn khớp về mặt toán học.

Các nguyên tắc thiết kế nền tảng chi phối hành vi nghiệp vụ và mô hình lưu trữ của hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2593}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7407}}|}
\hline
\rowcolor[gray]{0.9}
Nguyên tắc &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Nguyên tắc &
Nội dung \\ \hline
\endhead
Ghi nhận đồng thời &
Áp dụng cơ chế ghi nhận đồng thời (trừ ví nguồn, cộng ví đích) cho mọi
giao dịch điều chuyển nội bộ. Mục đích là giữ tổng số dư trong các ví
không đổi, ngoại trừ chi phí chuyển tiền. \\ \hline
Tổng hợp tiền trong ví ngay lập tức &
Tính theo công thức đại số tại thời điểm tra cứu từ lịch sử dòng tiền
của từng ví. \\ \hline
Phân tích dòng tiền 12 tháng gần nhất &
Trích xuất chuỗi dữ liệu 12 tháng liên tục kết thúc tại tháng Người dùng
chọn, mặc định là tháng hiện tại, để phân tích dòng tiền thuần định kỳ
và tỷ lệ tiết kiệm. \\ \hline
Lưu trữ tệp cục bộ an toàn &
Ghi dữ liệu trực tiếp xuống tệp tin JSON cục bộ bằng cách ghi tệp an
toàn, không làm hỏng hoặc mất dữ liệu cũ khi mất điện hoặc tắt máy đột
ngột. Dữ liệu được giữ lại khi ngoại tuyến và độc lập hoàn toàn với hệ
quản trị cơ sở dữ liệu bên ngoài. \\ \hline
\end{longtable}
\endgroup
```

![Sơ đồ phân rã mức cao các khối nghiệp vụ tài chính của hệ thống](docs/software-requirement-specification/images/so-do-tong-quan.png){width=90%}

### 2.2 Mục tiêu hệ thống

Các mục tiêu nghiệp vụ định lượng và tiêu chí kiểm chứng tương ứng.


```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.0647}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1823}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.3725}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.3805}}|}
\hline
\rowcolor[gray]{0.9}
Mã mục tiêu &
Tên mục tiêu &
Định nghĩa &
Tiêu chí đo kiểm \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã mục tiêu &
Tên mục tiêu &
Định nghĩa &
Tiêu chí đo kiểm \\ \hline
\endhead
\texttt{G-01} &
Ghi nhận dòng tiền và cấu trúc danh mục phân cấp &
Lưu các biến động số dư và gán nhãn theo cấu trúc danh mục 3 cấp (Loại,
Nhóm nhu cầu, Danh mục chi tiết) &
Tỷ lệ giao dịch thu chi đã lưu được định danh đúng ví và đúng danh mục
hợp lệ bằng 100.00\%. Kiểm chứng theo Tạo mới giao dịch thu nhập
(\texttt{FR-TXN-01}) và Tạo mới giao dịch chi tiêu (\texttt{FR-TXN-02}).
Danh mục hợp lệ gồm danh mục đang dùng và danh mục đã xóa còn tồn tại
theo Phục hồi dữ liệu hệ thống từ bản sao lưu (\texttt{FR-DAT-02}). Số
dư ví cập nhật ngay sau khi ghi nhận theo Hiệu năng và độ trễ phản hồi
(\texttt{NFR-02}). \\ \hline
\texttt{G-02} &
Xác định tổng số dư và cơ cấu phân bổ ví &
Tính tổng số dư của các ví và phần trăm số dư mỗi ví chiếm trong tổng,
tại ngày Người dùng tra cứu. Số dư của từng ví được tính từ lịch sử dòng
tiền của ví đó &
Tổng số dư hiển thị bằng đúng tổng số dư của từng ví cộng lại, chênh
lệch 0 VNĐ. Khi tổng số dư lớn hơn 0, 100.00\% số ví hiển thị tỷ lệ đúng
theo công thức của Thống kê cơ cấu số dư theo ví (\texttt{FR-WAL-04}). \\ \hline
\texttt{G-03} &
Bảo đảm độ chính xác số học tài chính &
Loại bỏ hoàn toàn sai số làm tròn số thực nhị phân trong các phép tính
số dư và tổng hợp báo cáo &
Sai số làm tròn số học bằng 0.00 VNĐ theo Độ chính xác và toàn vẹn dữ
liệu tài chính (\texttt{NFR-01}) trong các kịch bản tính toán số dư và
tổng hợp báo cáo. Bảo đảm không xuất hiện sai số dấu chấm động nhị phân
trên toàn bộ dữ liệu tài chính. \\ \hline
\end{longtable}
\endgroup
```

### 2.3 Đặc điểm người dùng

Hệ thống phục vụ một Người dùng duy nhất có toàn quyền quản trị dữ liệu cục bộ mà không phân quyền nhiều cấp. Người dùng chỉ cần kỹ năng máy tính cơ bản và hiểu biết tài chính thông dụng để ghi nhận giao dịch hàng ngày cùng theo dõi báo cáo định kỳ.

### 2.4 Giới hạn kỹ thuật, giả định và sự phụ thuộc

Bảng dưới đây liệt kê những giới hạn kỹ thuật mà hệ thống bắt buộc phải tuân thủ, những giả định làm cơ sở cho thiết kế và những thành phần bên ngoài mà hệ thống phụ thuộc để vận hành đúng.

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.0887}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.9113}}|}
\hline
\rowcolor[gray]{0.9}
Mã giới hạn &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã giới hạn &
Nội dung \\ \hline
\endhead
\texttt{CON-01} &
Ứng dụng phải được biên dịch và chạy trên môi trường Java Development
Kit phiên bản 17 LTS trở lên. \\ \hline
\texttt{CON-02} &
Mọi phép tính số học về tiền tệ, số dư ví, tỷ lệ phần trăm và biến trung
gian phải dùng kiểu số có độ chính xác cố định. Độ chính xác tối thiểu
là 18 chữ số phần nguyên và 2 chữ số thập phân. Hệ thống không được dùng
số thực dấu chấm động nhị phân để loại bỏ sai số làm tròn. \\ \hline
\texttt{CON-03} &
Toàn bộ các quy tắc tính toán số dư, ghi nhận đồng thời và tổng hợp báo
cáo phải được kiểm chứng tự động, bao phủ 100\% các quy tắc nghiệp vụ. \\ \hline
\texttt{CON-04} &
Dữ liệu phải được lưu trên tệp tin cục bộ định dạng văn bản có cấu trúc
JSON và bảng mã UTF-8. Cách lưu này cho phép sao lưu và phục hồi độc lập
mà không phụ thuộc vào hệ quản trị cơ sở dữ liệu bên ngoài. \\ \hline
\texttt{CON-05} &
Dữ liệu đầu vào phải tuân theo các giới hạn sau: tên ví, tên nhóm và tên
danh mục dài từ 1 đến 50 ký tự. Mô tả ví, ghi chú giao dịch và lý do
điều chỉnh số dư dài tối đa 255 ký tự (cho phép để trống). Số bản ghi
mỗi trang nằm trong khoảng từ 10 đến 100 và mặc định là 50. \\ \hline
\texttt{CON-06} &
Bản sao lưu tự động phải được đặt tên theo định dạng
\texttt{backup\_YYYYMMDD\_HHMMSS.json} và lưu trong thư mục riêng
\texttt{backups/}. \\ \hline
\end{longtable}
\endgroup
```

Thiết kế hệ thống dựa trên các giả định sau đây về phạm vi người dùng, phiên làm việc và nguồn gốc của tệp sao lưu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\centering\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1005}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8995}}|}
\hline
\rowcolor[gray]{0.9}
Mã giả định &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã giả định &
Nội dung \\ \hline
\endhead
\texttt{AS-01} &
Hệ thống giả định chỉ có một Người dùng và một phiên làm việc đơn tiến
trình duy nhất. Phiên này truy cập vào tệp dữ liệu tại một thời điểm
trên ứng dụng máy tính để bàn cục bộ. \\ \hline
\texttt{AS-02} &
Hệ thống giả định tệp sao lưu do chính hệ thống tạo ra và không bị chỉnh
sửa bên ngoài hệ thống. Vì vậy khi nhập tệp, Quy tắc tên của ví, nhóm
nhu cầu và danh mục (\texttt{BR-NAM-01}) không được kiểm tra lại. Tệp
hỏng hoặc sai loại vẫn bị từ chối theo quy trình kiểm tra của Phục hồi
dữ liệu hệ thống từ bản sao lưu (\texttt{FR-DAT-02}). \\ \hline
\end{longtable}
\endgroup
```

Hệ thống cần các thành phần bên ngoài sau đây của máy tính để vận hành đúng, cụ thể là nguồn thời gian và khả năng đọc, ghi tệp tin cục bộ:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.0853}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1899}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.7248}}|}
\hline
\rowcolor[gray]{0.9}
Mã phụ thuộc &
Thành phần &
Vai trò \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã phụ thuộc &
Thành phần &
Vai trò \\ \hline
\endhead
\texttt{DE-01} &
Đồng hồ hệ thống &
Cung cấp mốc thời gian thực tế từ hệ thống phần cứng và hệ điều hành.
Mốc này dùng để ghi nhận thời điểm giao dịch và xác định chuỗi 12 tháng
gần nhất trong báo cáo thống kê. \\ \hline
\texttt{DE-02} &
Hệ thống tệp tin cục bộ &
Cung cấp quyền đọc, ghi tệp tin trên ổ đĩa để lưu trữ lâu dài, áp dụng
cách ghi tệp an toàn và sao lưu dữ liệu. \\ \hline
\end{longtable}
\endgroup
```

### 2.5 Môi trường vận hành

Bảng yêu cầu môi trường vận hành tối thiểu, gồm hệ điều hành, môi trường chạy Java, cấu hình phần cứng, khả năng hiển thị và các ghi chú tương thích

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1183}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4842}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3975}}|}
\hline
\rowcolor[gray]{0.9}
Yếu tố môi trường &
Mức tối thiểu &
Ghi chú tương thích \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Yếu tố môi trường &
Mức tối thiểu &
Ghi chú tương thích \\ \hline
\endhead
Hệ điều hành &
Bất kỳ hệ điều hành máy tính cá nhân nào có môi trường thực thi Java
tương thích (Windows, macOS, Linux) &
Độc lập nền tảng nhờ máy ảo Java \\ \hline
Môi trường chạy Java &
Java Runtime Environment hoặc Java Development Kit 17 LTS trở lên &
Cần có để chạy mã bytecode của ứng dụng \\ \hline
Cấu hình phần cứng &
Cấu hình máy tính cá nhân thông thường đủ chạy môi trường thực thi Java &
Chưa xác lập ngưỡng phần cứng tối thiểu cụ thể; sẽ bổ sung sau khi có đo
đạc thực tế \\ \hline
Hiển thị &
Kích thước màn hình đủ hiển thị bảng biểu số liệu và biểu đồ báo cáo &
Chưa xác lập độ phân giải tối thiểu cụ thể; khuyến nghị 1280x720 pixel
trở lên \\ \hline
\end{longtable}
\endgroup
```

### 2.6 Luận đề kỹ thuật và phân tích đánh đổi

#### Luận đề kỹ thuật

Hệ thống giải quyết bài toán quản lý tài chính cá nhân cho một Người dùng duy nhất trên môi trường máy tính để bàn cục bộ. Hệ thống chấp nhận đánh đổi sự phức tạp của kiến trúc mạng phân tán và hệ quản trị cơ sở dữ liệu máy chủ. Đổi lại, hệ thống đạt sai số số học bằng 0.00 VNĐ theo Quy tắc độ chính xác tài chính (`BR-FIN-01`). Hệ thống còn ghi tệp an toàn cục bộ và phản hồi ngay cho các phép toán số dư với quy mô dữ liệu cá nhân thông thường.

#### Lựa chọn thiết kế và hệ quả

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.0767}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2972}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.3130}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.3130}}|}
\hline
\rowcolor[gray]{0.9}
Hạng mục &
Lựa chọn &
Lợi ích &
Hạn chế \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Hạng mục &
Lựa chọn &
Lợi ích &
Hạn chế \\ \hline
\endhead
Độ chính xác số học &
Sử dụng số thập phân với số chữ số cố định cho toàn bộ các trường tiền
tệ, tỷ lệ và biến trung gian; loại trừ số thực dấu chấm động nhị phân. &
Loại bỏ sai số làm tròn của số thực dấu chấm động nhị phân; giữ cho số
dư các ví và phép cộng trừ khớp nhau. &
Tải tính toán phần mềm cao hơn tính toán phần cứng của kiểu số nguyên
thủy; chấp nhận vì việc tính chính xác về tài chính là ưu tiên kỹ thuật
cao nhất. \\ \hline
Lưu trữ dữ liệu &
Sử dụng tệp tin cục bộ có cấu trúc JSON kết hợp cơ chế ghi tệp an toàn
bảo toàn dữ liệu chống hỏng hóc. &
Loại bỏ phụ thuộc vào phần mềm cơ sở dữ liệu bên ngoài; Người dùng trực
tiếp làm chủ tệp dữ liệu, thuận tiện sao lưu và di chuyển độc lập. &
Không có sẵn tính năng lập chỉ mục nâng cao và tinh chỉnh truy vấn tự
động của hệ cơ sở dữ liệu quan hệ. Toàn bộ dữ liệu được đọc vào bộ nhớ
khi khởi động ứng dụng. \\ \hline
Kiểm soát truy cập &
Mô hình Người dùng duy nhất vận hành trên ứng dụng máy tính để bàn cá
nhân. &
Đơn giản hóa việc khóa dữ liệu, loại trừ xung đột ghi đồng thời từ nhiều
phiên mạng; bảo đảm giao diện phản hồi ngay lập tức. &
Không hỗ trợ cộng tác đa người dùng đồng thời qua mạng; chỉ phục vụ nhu
cầu quản lý tài chính cá nhân độc lập. \\ \hline
\end{longtable}
\endgroup
```

#### Ngưỡng gãy hệ thống

Hệ thống được thiết kế cho quy mô vận hành cá nhân. Điểm gãy kỹ thuật xuất hiện trong hai trường hợp:

1. Quy mô dữ liệu vượt xa mức sử dụng cá nhân thông thường: Kiến trúc lưu trữ tệp tin phẳng JSON nguyên khối đọc toàn bộ vào bộ nhớ khi khởi động. Kiến trúc này sẽ trở thành điểm nghẽn hiệu năng và bộ nhớ khi khối lượng dữ liệu tăng đủ lớn. Khi đạt ngưỡng này (sẽ xác định bằng đo đạc thực tế), kiến trúc lưu trữ cần tái cấu trúc sang cơ sở dữ liệu quan hệ nhúng cục bộ. Cơ sở dữ liệu này có đánh chỉ mục và phân trang.
2. Nhu cầu đồng bộ ngay lập tức giữa nhiều thiết bị: Khi cập nhật đồng thời từ nhiều thiết bị, cách lưu tệp cục bộ đơn tiến trình không còn đáp ứng. Nguyên nhân là xung đột ghi dữ liệu. Khi đó, hệ thống phải chuyển sang kiến trúc dịch vụ mạng Client-Server hoặc Cloud API có kiểm soát tương tranh.

\newpage

## 3. QUY TẮC NGHIỆP VỤ VÀ QUY ĐỊNH TÀI CHÍNH

Hệ thống quản lý tài chính cá nhân áp dụng các quy tắc nghiệp vụ, đóng vai trò chính sách cốt lõi của miền bài toán và bảo vệ tính toàn vẹn của mục tiêu hệ thống. 

Mỗi quy tắc nghiệp vụ được đặc tả nhất quán theo khuôn mẫu:

1. Tên và mã quy tắc: cú pháp `<Tên quy tắc> (BR-<NHÓM>-NN)`, phân loại theo miền nghiệp vụ tài chính (`BR-FIN`) hoặc miền định danh (`BR-NAM`).
2. Phạm vi áp dụng và mục đích: xác định các thực thể dữ liệu chịu sự chi phối và mục đích bảo vệ tính đúng đắn của dữ liệu.
3. Nội dung quy tắc và biểu thức toán học: phát biểu nguyên tắc nghiệp vụ bằng câu văn định chuẩn kết hợp công thức toán học hình thức xác định các bất biến số học.
4. Quy định xử lý chi tiết: bảng các ràng buộc nghiệp vụ cụ thể về biểu diễn số học, cơ chế làm tròn và các trường hợp biên ở tầng bài toán, không rò rỉ mã nguồn cài đặt.
5. Bảng lỗi vi phạm và phản hồi hệ thống: bảng 5 cột chuẩn hóa gồm Mã lỗi (`ERR-*`), Tên lỗi, điều kiện vi phạm, hành động của hệ thống, thông điệp thông báo người dùng.

Bảng tổng hợp 5 quy tắc nghiệp vụ của hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1247}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1941}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1451}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.2058}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.3303}}|}
\hline
\rowcolor[gray]{0.9}
Mã quy tắc &
Tên quy tắc &
Phân loại quy tắc &
Thực thể áp dụng &
Ý nghĩa nghiệp vụ \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã quy tắc &
Tên quy tắc &
Phân loại quy tắc &
Thực thể áp dụng &
Ý nghĩa nghiệp vụ \\ \hline
\endhead
\texttt{BR-FIN-01} &
Quy tắc độ chính xác tài chính &
Quy tắc tính toán số học &
Toàn bộ thực thể tài chính &
Số thập phân chính xác, làm tròn xác định. \\ \hline
\texttt{BR-FIN-02} &
Quy tắc bảo toàn số dư ví &
Ràng buộc toàn vẹn dữ liệu &
Ví, giao dịch thu chi, giao dịch chuyển tiền, điều chỉnh số dư &
Số dư ví tính lại từ lịch sử dòng tiền, luôn không âm. \\ \hline
\texttt{BR-FIN-03} &
Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ &
Quy tắc giao dịch đồng thời &
Giao dịch chuyển tiền, giao dịch thu chi, ví &
Trừ ví nguồn và cộng ví đích cùng được ghi nhận hoặc cùng không được ghi
nhận. \\ \hline
\texttt{BR-FIN-04} &
Quy tắc phân cấp danh mục thu chi 3 cấp &
Ràng buộc cấu trúc phân cấp &
Danh mục, nhóm nhu cầu, loại thu chi &
Cấu trúc 3 cấp (Loại thu chi -\textgreater{} Nhóm nhu cầu
-\textgreater{} Danh mục chi tiết), mọi danh mục thuộc một nhóm cha hợp
lệ. \\ \hline
\texttt{BR-NAM-01} &
Quy tắc tên của ví, nhóm nhu cầu và danh mục &
Ràng buộc định danh dữ liệu &
Ví, nhóm nhu cầu, danh mục &
Tên đúng định dạng và không trùng trong phạm vi áp dụng. \\ \hline
\end{longtable}
\endgroup
```

### 3.1 Quy tắc độ chính xác tài chính (`BR-FIN-01`)

Quy tắc áp dụng cho mọi thực thể dữ liệu, phép tính toán số học và việc lưu trữ trong hệ thống. Mọi giá trị số tiền và tỷ lệ phần trăm bắt buộc dùng số thập phân chính xác với số chữ số thập phân cố định. Hệ thống không sử dụng số thực nhị phân trong tính toán nghiệp vụ nhằm loại bỏ sai số làm tròn, bảo đảm độ chính xác của mọi phép toán tiền tệ và tỷ lệ.

#### Quy định xử lý

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2228}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7772}}|}
\hline
\rowcolor[gray]{0.9}
Quy định &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Quy định &
Nội dung \\ \hline
\endhead
Biểu diễn giá trị &
Số thập phân chính xác, với số chữ số thập phân cố định theo Giới hạn
kiểu số có độ chính xác cố định (\texttt{CON-02}). \\ \hline
Nguồn hình thành giá trị &
Giá trị chỉ được hình thành từ dữ liệu nhập dạng thập phân chính xác
hoặc từ giá trị nguyên. Không được chuyển đổi trung gian qua số thực nhị
phân. \\ \hline
Quy tắc chữ số thập phân của tiền tệ &
Tiền tệ giao dịch và số dư hiển thị cho VNĐ dùng 0 chữ số thập phân (số
nguyên đồng) trên giao diện người dùng. Tính toán, xử lý nghiệp vụ và
chuỗi kiểm thử tự động dùng 2 chữ số thập phân. \\ \hline
Quy tắc chữ số thập phân của tỷ lệ phần trăm &
Các tỷ lệ phần trăm trung gian bắt buộc dùng tối thiểu 4 chữ số thập
phân (khuyến nghị 6 chữ số thập phân trong các bước tính trung gian). \\ \hline
Chế độ làm tròn &
Các phép chia không chia hết bắt buộc chỉ định rõ ràng quy cách làm tròn
ngân hàng (làm tròn về số chẵn gần nhất khi phần dư đúng bằng một nửa). \\ \hline
Phép chia số học &
Không bao giờ thực hiện phép chia giữa hai số thập phân chính xác mà
không kèm số chữ số thập phân và quy tắc làm tròn rõ ràng. \\ \hline
So sánh giá trị số học &
So sánh theo giá trị đại số thực, không phân biệt số chữ số 0 dư thừa ở
phần thập phân. \\ \hline
\end{longtable}
\endgroup
```

#### Lỗi vi phạm

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1174}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.2831}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1807}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.2701}}|}
\hline
\rowcolor[gray]{0.9}
Mã lỗi &
Tên lỗi &
Điều kiện vi phạm &
Hành động của hệ thống &
Thông điệp thông báo người dùng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã lỗi &
Tên lỗi &
Điều kiện vi phạm &
Hành động của hệ thống &
Thông điệp thông báo người dùng \\ \hline
\endhead
\texttt{ERR-FIN-001} &
Lỗi định dạng số học &
Dữ liệu đầu vào sai định dạng số học hoặc chứa ký tự không hợp lệ &
Từ chối xử lý dữ liệu đầu vào &
``Dữ liệu số tiền không đúng định dạng số học hợp lệ.'' \\ \hline
\texttt{ERR-FIN-002} &
Lỗi phép chia thiếu quy tắc làm tròn &
Phép toán chia không xác định quy tắc làm tròn gây số thập phân vô hạn &
Hủy giao dịch hiện tại và ghi nhật ký cảnh báo &
``Lỗi số học: Phép toán chia không xác định quy tắc làm tròn.'' \\ \hline
\texttt{ERR-FIN-003} &
Lỗi chia cho 0 &
Mẫu số của phép chia tỷ lệ bằng 0.00 VNĐ &
Bỏ qua phép chia và gán kết quả 0.00\% &
``Không có dữ liệu để tính tỷ lệ, hệ thống hiển thị 0.00\%.'' \\ \hline
\end{longtable}
\endgroup
```

### 3.2 Quy tắc bảo toàn số dư ví (`BR-FIN-02`)

Quy tắc áp dụng cho thực thể ví và toàn bộ các dòng tiền đến từ giao dịch thu chi, giao dịch chuyển tiền và điều chỉnh số dư. Số dư khả dụng của từng ví được tính lại theo thời gian thực từ lịch sử các khoản ghi dòng tiền. Nghiêm cấm đổi số dư bằng phép gán trực tiếp. Số dư ví được kiểm soát để luôn không âm.

#### Nội dung quy tắc

1. Công thức số dư tính lại từ lịch sử dòng tiền: xét ví $w$ tại thời điểm quan sát $T$. Số dư khả dụng $\text{WalletBalance}_w(T)$ được xác định duy nhất bằng tổng các dòng tiền vào trừ tổng các dòng tiền ra có đến thời điểm $T$. Ví mới tạo có số dư 0.00 VNĐ, nên số dư hiện có của ví chỉ hình thành qua giao dịch thu chi, chuyển tiền nội bộ hoặc khoản điều chỉnh số dư:

$$
\text{WalletBalance}_w(T) = \sum_{t \le T} \text{Inflow}_w(t) - \sum_{t \le T} \text{Outflow}_w(t)
$$

Trong đó:

$$
\text{Inflow}_w(t) = \text{Income}_w(t) + \text{TransferIn}_w(t) + \max\bigl(0, \text{Adjustment}_w(t)\bigr)
$$

$$
\text{Outflow}_w(t) = \text{Expense}_w(t) + \text{TransferOut}_w(t) + \max\bigl(0, -\text{Adjustment}_w(t)\bigr)
$$

Mỗi số hạng trong công thức được xác định như sau:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1604}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1185}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.0607}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.6604}}|}
\hline
\rowcolor[gray]{0.9}
Số hạng &
Loại dòng tiền &
Dấu &
Ý nghĩa \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Số hạng &
Loại dòng tiền &
Dấu &
Ý nghĩa \\ \hline
\endhead
\(\text{Income}_w(t)\) &
Thu nhập &
+ &
Khoản thu nhập được ghi trực tiếp vào ví \(w\) bằng giao dịch thu nhập
theo Tạo mới giao dịch thu nhập (\texttt{FR-TXN-01}) \\ \hline
\(\text{Expense}_w(t)\) &
Chi tiêu &
− &
Khoản chi tiêu chi trả trực tiếp từ ví \(w\), kể cả phí giao dịch theo
Tạo mới giao dịch chi tiêu (\texttt{FR-TXN-02}) \\ \hline
\(\text{TransferIn}_w(t)\) &
Nhận chuyển tiền &
+ &
Khoản tiền nhận được do chuyển từ ví khác vào ví \(w\) theo Chuyển tiền
giữa hai ví nội bộ, kèm chi phí tùy chọn (\texttt{FR-TRF-01}) \\ \hline
\(\text{TransferOut}_w(t)\) &
Chuyển tiền đi &
− &
Khoản tiền chuyển từ ví \(w\) sang ví khác theo Chuyển tiền giữa hai ví
nội bộ, kèm chi phí tùy chọn (\texttt{FR-TRF-01}) \\ \hline
\(\text{Adjustment}_w(t)\) &
Khoản điều chỉnh số dư &
+/− &
Chênh lệch từ khoản điều chỉnh số dư theo Điều chỉnh số dư ví
(\texttt{FR-WAL-05}), không thuộc luồng thu, chi hoặc chuyển tiền nào \\ \hline
\end{longtable}
\endgroup
```

2. Điều kiện về số dư khả dụng:
   - Đối với mọi ví: số dư khả dụng tại thời điểm hiện tại luôn không âm (lớn hơn hoặc bằng 0.00 VNĐ).
3. Điều kiện về khoản điều chỉnh số dư:
   - Mỗi lần điều chỉnh số dư theo Điều chỉnh số dư ví (`FR-WAL-05`) bắt buộc tạo một bản ghi độc lập và không được sửa. Bản ghi lưu trữ số dư trước và sau điều chỉnh, giá trị chênh lệch, thời điểm ghi nhận và lý do điều chỉnh (nếu có, hoặc chuỗi rỗng).
4. Điều kiện về phí giao dịch:
   - Chi phí giao dịch bắt buộc không âm (lớn hơn hoặc bằng 0.00 VNĐ), từ chối theo Lỗi chi phí không hợp lệ (`ERR-FEE-001`) nếu số tiền chi phí nhỏ hơn 0. Khi có chi phí lớn hơn 0, Người dùng bắt buộc chọn danh mục chi tiêu hợp lệ. Hệ thống từ chối theo Lỗi thiếu danh mục phí giao dịch (`ERR-FEE-002`) nếu chưa chọn danh mục, hoặc theo Lỗi danh mục phí không hợp lệ (`ERR-FEE-003`) nếu danh mục đã xóa hoặc sai loại.
   - Phí giao dịch là giao dịch thu chi loại `EXPENSE` nên được tính vào chi tiêu của ví bị trừ chi phí.
   - Phí giao dịch liên kết với giao dịch thu chi hoặc giao dịch chuyển tiền phát sinh ra nó. Phí chỉ có hiệu lực cùng giao dịch đó: hai bản ghi cùng được ghi nhận, sửa hoặc xóa, hoặc không có thay đổi nào xảy ra.

#### Quy định xử lý

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1775}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8225}}|}
\hline
\rowcolor[gray]{0.9}
Quy định &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Quy định &
Nội dung \\ \hline
\endhead
Cách tính toán &
Tính lại số dư theo thời gian thực từ lịch sử dòng tiền. Hệ thống không
được cho phép đặt số dư ví bằng phép gán trực tiếp mà không có khoản ghi
dòng tiền tương ứng. \\ \hline
Đồng bộ dữ liệu &
Mọi thao tác thêm, sửa hoặc xóa giao dịch liên quan đến ví phải tính lại
số dư ngay lập tức trong cùng lần xử lý. \\ \hline
Kiểm tra tiền điều kiện &
Trước khi thực hiện thao tác làm giảm số dư ví (chi tiêu, chuyển đi,
điều chỉnh giảm, ghi chi phí, xóa hoặc sửa giao dịch dẫn tới giảm số dư
ví), hệ thống phải kiểm tra số dư khả dụng. Thao tác bị từ chối theo Lỗi
số dư ví không đủ (\texttt{ERR-WAL-001}) nếu số dư không đủ. \\ \hline
\end{longtable}
\endgroup
```

#### Lỗi vi phạm
```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1307}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.2822}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1724}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.2660}}|}
\hline
\rowcolor[gray]{0.9}
Mã lỗi &
Tên lỗi &
Điều kiện vi phạm &
Hành động của hệ thống &
Thông điệp thông báo người dùng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã lỗi &
Tên lỗi &
Điều kiện vi phạm &
Hành động của hệ thống &
Thông điệp thông báo người dùng \\ \hline
\endhead
\texttt{ERR-WAL-001} &
Lỗi số dư ví không đủ &
Giao dịch làm số dư ví âm hoặc không đủ thanh toán số tiền và chi phí &
Từ chối thực hiện giao dịch, giữ nguyên trạng thái số dư &
``Giao dịch bị từ chối: Số dư ví không đủ để thực hiện thao tác.'' \\ \hline
\texttt{ERR-FEE-001} &
Lỗi chi phí không hợp lệ &
Chi phí giao dịch nhỏ hơn 0 &
Từ chối giao dịch &
``Chi phí không hợp lệ.'' \\ \hline
\texttt{ERR-FEE-002} &
Lỗi thiếu danh mục phí giao dịch &
Chi phí lớn hơn 0 nhưng chưa chọn danh mục cho phí giao dịch &
Từ chối tiếp nhận giao dịch &
``Vui lòng chọn danh mục cho phí giao dịch.'' \\ \hline
\texttt{ERR-FEE-003} &
Lỗi danh mục phí không hợp lệ &
Danh mục phí đã bị xóa hoặc không thuộc loại thu chi chi tiêu
(\texttt{EXPENSE}) &
Từ chối thực hiện giao dịch &
``Danh mục phí không hợp lệ hoặc không tương thích với loại chi tiêu.'' \\ \hline
\end{longtable}
\endgroup
```

### 3.3 Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`)

Quy tắc áp dụng cho thực thể giao dịch chuyển tiền, cặp thực thể ví (ví nguồn và ví đích) và thực thể giao dịch thu chi ghi phí giao dịch. Quy tắc áp dụng cơ chế ghi nhận đồng thời (trừ ví nguồn, cộng ví đích) cho mọi giao dịch chuyển tiền nội bộ. Tổng số dư trong các ví của toàn hệ thống được giữ nguyên nếu không có phí, và chi phí phát sinh được tách riêng thành một phí giao dịch độc lập.

#### Nội dung quy tắc

1. Cặp khoản ghi: với một giao dịch chuyển tiền từ ví nguồn tới ví đích với số tiền chuyển lớn hơn 0:
   - Ví nguồn giảm số tiền chuyển: số dư ví nguồn sau chuyển tiền bằng số dư trước chuyển trừ số tiền chuyển:

     $$
     \text{WalletBalance}_{w_{\text{src}}}(\text{sau}) = \text{WalletBalance}_{w_{\text{src}}}(\text{trước}) - A
     $$

   - Ví đích tăng số tiền chuyển: số dư ví đích sau chuyển tiền bằng số dư trước chuyển cộng số tiền chuyển:

     $$
     \text{WalletBalance}_{w_{\text{dst}}}(\text{sau}) = \text{WalletBalance}_{w_{\text{dst}}}(\text{trước}) + A
     $$

2. Giữ nguyên tổng số dư trong các ví: trước khi tính chi phí, tổng số dư trên toàn bộ các ví trước và sau chuyển tiền luôn không đổi:

   $$
   \sum_{w \in \text{Wallets}} \text{WalletBalance}_w(\text{sau}) = \sum_{w \in \text{Wallets}} \text{WalletBalance}_w(\text{trước})
   $$

3. Ghi nhận phí giao dịch độc lập: khi có phí chuyển tiền (chi phí lớn hơn 0) do bên thứ ba thu:
   - Số tiền trừ ví nguồn bằng số tiền chuyển cộng chi phí.
   - Số tiền cộng ví đích bằng số tiền chuyển.
   - Chi phí chuyển tiền được ghi nhận thành một phí giao dịch độc lập (loại `EXPENSE`) từ ví nguồn:

     $$
     \text{WalletBalance}_{w_{\text{src}}}(\text{sau}) = \text{WalletBalance}_{w_{\text{src}}}(\text{trước}) - (A + \text{Fee})
     $$

     $$
     \sum_{w \in \text{Wallets}} \text{WalletBalance}_w(\text{sau}) = \sum_{w \in \text{Wallets}} \text{WalletBalance}_w(\text{trước}) - \text{Fee}
     $$

#### Quy định xử lý

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1993}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8007}}|}
\hline
\rowcolor[gray]{0.9}
Quy định &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Quy định &
Nội dung \\ \hline
\endhead
Ghi nhận đồng thời &
Hai khoản ghi trừ ví nguồn và cộng ví đích bắt buộc cùng được ghi nhận
hoặc cùng không được ghi nhận. Nếu một trong hai khoản ghi thất bại, số
dư cả hai ví phải trở về giá trị trước thao tác. \\ \hline
Phân định thực thể &
Ví nguồn và ví đích bắt buộc khác nhau. Nếu trùng nhau, hệ thống từ chối
theo Lỗi ví nguồn trùng ví đích (\texttt{ERR-TRF-001}). \\ \hline
Giá trị chuyển cho phép &
Số tiền chuyển bắt buộc lớn hơn 0 (lớn hơn 0.00 VNĐ), nếu vi phạm hệ
thống từ chối theo Lỗi số tiền chuyển không hợp lệ
(\texttt{ERR-TRF-002}). Phí chuyển tiền bắt buộc không âm (lớn hơn hoặc
bằng 0.00 VNĐ), nếu vi phạm hệ thống từ chối theo Lỗi chi phí không hợp
lệ (\texttt{ERR-FEE-001}). \\ \hline
Điều kiện số dư ví nguồn &
Số dư khả dụng của ví nguồn trước khi chuyển bắt buộc lớn hơn hoặc bằng
tổng số tiền chuyển và chi phí. Nếu không đủ số dư, hệ thống từ chối
theo Lỗi số dư ví không đủ (\texttt{ERR-WAL-001}). \\ \hline
Quy tắc xóa chuyển tiền &
Khi xóa chuyển tiền, hệ thống xóa bản ghi chuyển tiền khỏi sổ giao dịch
chuyển tiền, xóa phí giao dịch liên kết (nếu có) khỏi sổ giao dịch thu
chi, hoàn số tiền chuyển cộng chi phí về ví nguồn và trừ số tiền chuyển
khỏi ví đích. Cả hai ví được tính lại số dư theo Quy tắc bảo toàn số dư
ví (\texttt{BR-FIN-02}) từ các giao dịch đang tồn tại. Hệ thống từ chối
xóa theo Lỗi số dư ví không đủ (\texttt{ERR-WAL-001}) nếu số dư ví đích
nhỏ hơn số tiền chuyển. \\ \hline
\end{longtable}
\endgroup
```

#### Lỗi vi phạm
```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1518}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1826}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1803}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.3367}}|}
\hline
\rowcolor[gray]{0.9}
Mã lỗi &
Tên lỗi &
Điều kiện vi phạm &
Hành động của hệ thống &
Thông điệp thông báo người dùng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã lỗi &
Tên lỗi &
Điều kiện vi phạm &
Hành động của hệ thống &
Thông điệp thông báo người dùng \\ \hline
\endhead
\texttt{ERR-TRF-001} &
Lỗi ví nguồn trùng ví đích &
Ví nguồn trùng với ví đích &
Từ chối thực hiện giao dịch &
``Giao dịch không hợp lệ: Ví chuyển và ví nhận phải khác nhau.'' \\ \hline
\texttt{ERR-TRF-002} &
Lỗi số tiền chuyển không hợp lệ &
Số tiền chuyển nhỏ hơn hoặc bằng 0 &
Từ chối thực hiện giao dịch &
``Số tiền chuyển phải là một số dương lớn hơn 0.'' \\ \hline
\end{longtable}
\endgroup
```

### 3.4 Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`)

Quy tắc áp dụng cho thực thể danh mục, nhóm nhu cầu, loại thu chi và mối quan hệ liên kết từ giao dịch thu chi. Quy tắc chuẩn hóa cấu trúc danh mục 3 cấp quan hệ 1:N. Loại thu chi cố định còn nhóm nhu cầu và danh mục chi tiết do Người dùng quản lý. Quy tắc giữ cho các liên kết giữa các cấp luôn đúng, không để danh mục nào mất nhóm cha và không để sai lệch báo cáo dòng tiền lịch sử.

#### Nội dung quy tắc

1. Cấu trúc danh mục 3 cấp: mô hình phân cấp với quan hệ 1:N. Mỗi loại thu chi chứa một hoặc nhiều nhóm nhu cầu, và mỗi nhóm nhu cầu chứa các danh mục chi tiết.
2. Điều kiện quan hệ cha con duy nhất:
   - Mỗi danh mục chi tiết chỉ được gán vào đúng một nhóm nhu cầu cha tại một thời điểm.
   - Mỗi nhóm nhu cầu chỉ được gán vào đúng một loại thu chi gốc tại một thời điểm.
   - Mọi danh mục đều gắn với một nhóm cha: danh mục con bắt buộc trực thuộc một nhóm cha hợp lệ đang tồn tại.
3. Giá trị khởi tạo và phần do Người dùng quản lý:
   - Cấp 1 (Loại thu chi): Thu nhập (`INCOME`), Chi tiêu (`EXPENSE`). Cố định, không cho phép chỉnh sửa hoặc xóa.
   - Cấp 2 (Nhóm nhu cầu): bộ nhóm mẫu ban đầu do hệ thống nạp một lần từ cấu hình triển khai khi khởi tạo hệ thống lần đầu. Mỗi loại thu chi phải có ít nhất một nhóm nhu cầu đang dùng. Người dùng có toàn quyền thêm, đổi tên và xóa nhóm. Tên nhóm không được dùng làm điều kiện tiên quyết trong bất kỳ quy tắc xử lý nào.
   - Cấp 3 (Danh mục chi tiết): bộ danh mục mẫu ban đầu nạp một lần cùng với nhóm nhu cầu cha tương ứng từ cấu hình triển khai. Tại thời điểm nạp, mỗi nhóm mẫu có tối thiểu 1 danh mục chi tiết mẫu. Nhóm do Người dùng tạo có thể chưa có danh mục chi tiết nào. Người dùng có toàn quyền thêm, đổi tên, chuyển nhóm và xóa danh mục theo nhu cầu sử dụng.
4. Kiểm tra tên: tên nhóm nhu cầu và tên danh mục chi tiết tuân theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`) về định dạng và tính duy nhất.

#### Quy định xử lý

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1620}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8380}}|}
\hline
\rowcolor[gray]{0.9}
Quy định &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Quy định &
Nội dung \\ \hline
\endhead
Xóa danh mục &
Người dùng chỉ thấy thao tác xóa. Hộp xác nhận nêu số giao dịch liên
quan. Danh mục chưa có giao dịch thì xóa hẳn. Danh mục đã có giao dịch
thì chuyển sang trạng thái đã xóa, bị ẩn khỏi danh sách chọn và giao
dịch cũ được giữ nguyên. \\ \hline
Xóa nhóm &
Xóa nhóm kéo theo mọi danh mục con đang dùng, mỗi danh mục xử lý theo
quy tắc xóa danh mục. Nhóm có giao dịch bên trong thì chuyển sang trạng
thái đã xóa, nhóm không có thì xóa hẳn. Hộp xác nhận nêu số danh mục sẽ
bị ẩn, số danh mục xóa hẳn và tổng số giao dịch được giữ lại. Mỗi loại
thu chi luôn còn ít nhất một nhóm đang dùng. \\ \hline
Mục đã xóa &
Mục đã xóa không hiện trong danh sách chọn cho giao dịch mới, nhưng vẫn
hiện trong báo cáo và lịch sử kèm nhãn (đã xóa). Người dùng chọn khôi
phục (giữ mã cũ, nối lại giao dịch cũ) hoặc tạo mới (mã mới). Mã định
danh không bao giờ dùng lại. Khôi phục nhóm chỉ khôi phục riêng nhóm,
danh mục con khôi phục riêng từng cái. \\ \hline
Đổi nhóm của danh mục &
Danh mục chỉ được chuyển sang nhóm nhu cầu đang dùng thuộc cùng loại thu
chi. \\ \hline
Không được đổi loại thu chi &
Hệ thống không được thay đổi thuộc tính loại thu chi của một nhóm nhu
cầu hoặc danh mục khi đã có giao dịch liên kết. Quy định này tránh sai
lệch dữ liệu phân tích thu chi trong quá khứ. \\ \hline
\end{longtable}
\endgroup
```

#### Lỗi vi phạm

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1598}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.2481}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1172}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.3262}}|}
\hline
\rowcolor[gray]{0.9}
Mã lỗi &
Tên lỗi &
Điều kiện vi phạm &
Hành động của hệ thống &
Thông điệp thông báo người dùng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã lỗi &
Tên lỗi &
Điều kiện vi phạm &
Hành động của hệ thống &
Thông điệp thông báo người dùng \\ \hline
\endhead
\texttt{ERR-CAT-001} &
Lỗi đổi loại thu chi trái phép &
Chuyển danh mục sang nhóm thuộc loại thu chi khác, hoặc đổi loại thu chi
của nhóm hoặc danh mục đã có giao dịch &
Từ chối thay đổi thuộc tính &
``Không được phép đổi loại thu chi hoặc chuyển danh mục sang nhóm thuộc
loại thu chi khác.'' \\ \hline
\texttt{ERR-CAT-002} &
Lỗi xóa nhóm cuối cùng của loại thu chi &
Xóa nhóm đang dùng cuối cùng của một loại thu chi &
Từ chối thao tác xóa &
``Không thể xóa nhóm cuối cùng của loại thu chi này.'' \\ \hline
\texttt{ERR-CAT-003} &
Lỗi chuyển danh mục sang nhóm đã xóa &
Chuyển danh mục sang nhóm đã xóa &
Từ chối thao tác chuyển &
``Không thể chuyển danh mục sang nhóm đã xóa.'' \\ \hline
\texttt{ERR-CAT-004} &
Lỗi khôi phục danh mục khi nhóm cha đã xóa &
Khôi phục danh mục khi nhóm của nó còn ở trạng thái đã xóa &
Từ chối thao tác khôi phục &
``Nhóm chứa danh mục này đang ở trạng thái đã xóa. Hãy khôi phục nhóm
trước.'' \\ \hline
\texttt{ERR-CAT-005} &
Lỗi tạo danh mục trong nhóm đã xóa &
Tạo danh mục vào nhóm đang ở trạng thái đã xóa &
Từ chối thao tác tạo &
``Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng hoặc
khôi phục nhóm trước.'' \\ \hline
\end{longtable}
\endgroup
```

### 3.5 Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`)

Quy tắc áp dụng cho thực thể ví, nhóm nhu cầu và danh mục chi tiết ở mọi thao tác đặt tên: tạo, đổi tên, chuyển nhóm, tạo mới khi khôi phục và khôi phục. Quy tắc quy định định dạng của tên và tính duy nhất của tên trong từng phạm vi.

#### Nội dung quy tắc

1. Định dạng tên: tên được cắt khoảng trắng ở hai đầu trước khi kiểm tra. Sau khi cắt, tên không được rỗng và phải dài từ 1 đến 50 ký tự.
2. Cách so sánh: hai tên được coi là trùng khi giống nhau sau khi cắt khoảng trắng ở hai đầu, không phân biệt chữ hoa, chữ thường.
3. Phạm vi duy nhất: tên không được trùng với các mục khác trong phạm vi của bảng ngay dưới danh sách này. Hệ thống không so sánh chéo giữa nhóm nhu cầu và danh mục.
4. Mục đã xóa: nhóm nhu cầu và danh mục ở trạng thái đã xóa không tính vào việc kiểm tra trùng khi tạo mới và khi đổi tên. Khi tạo mới mà tên trùng một mục đã xóa cùng cấp, hệ thống hỏi Người dùng chọn khôi phục mục cũ hoặc tạo mới (hộp hỏi thuộc ca sử dụng Bổ sung danh mục phân loại mới (`UC-CAT-01`), lựa chọn khôi phục được xử lý theo ca sử dụng Khôi phục hoặc tạo lại mục đã xóa (`UC-CAT-03`)). Khi đổi tên, hệ thống không hỏi.
5. Khôi phục: khi khôi phục một mục đã xóa, hệ thống chỉ đối chiếu tên với các mục đang dùng.

Bảng phạm vi duy nhất của tên:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1462}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3726}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4811}}|}
\hline
\rowcolor[gray]{0.9}
Đối tượng &
Phạm vi duy nhất &
Các mục được so sánh \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Đối tượng &
Phạm vi duy nhất &
Các mục được so sánh \\ \hline
\endhead
Ví &
Toàn bộ ví trong hệ thống &
Mọi ví khác, kể cả ví đã lưu trữ \\ \hline
Nhóm nhu cầu &
Cùng một loại thu chi &
Các nhóm đang dùng khác \\ \hline
Danh mục &
Cùng một nhóm cha &
Các danh mục đang dùng khác \\ \hline
\end{longtable}
\endgroup
```

#### Quy định xử lý

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1550}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8450}}|}
\hline
\rowcolor[gray]{0.9}
Quy định &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Quy định &
Nội dung \\ \hline
\endhead
Thứ tự kiểm tra &
Kiểm tra định dạng trước (Lỗi định dạng tên (\texttt{ERR-NAM-001})),
kiểm tra trùng tên sau (Lỗi trùng tên (\texttt{ERR-NAM-002})). \\ \hline
Đổi tên &
Mục đang đổi tên không bị so sánh với chính nó. \\ \hline
Chuyển nhóm &
Tên danh mục được đối chiếu với các danh mục đang dùng trong nhóm đích. \\ \hline
Khôi phục &
Tên giữ nguyên nên chỉ kiểm tra tính duy nhất (Lỗi trùng tên
(\texttt{ERR-NAM-002})). Tạo mới khi khôi phục kiểm tra cả định dạng và
tính duy nhất. \\ \hline
Nhập tệp sao lưu &
Không áp dụng. Tệp sao lưu do chính hệ thống tạo ra nên tên không được
kiểm tra lại khi nhập, theo Giả định tệp sao lưu do hệ thống tạo ra
(\texttt{AS-02}). \\ \hline
Giải phóng tên &
Khi ví bị xóa vĩnh viễn, tên ví được giải phóng khỏi phạm vi duy nhất. \\ \hline
Dữ liệu khi bị từ chối &
Hệ thống không tạo, không đổi tên, không chuyển nhóm và không khôi phục.
Dữ liệu giữ nguyên. \\ \hline
\end{longtable}
\endgroup
```

#### Lỗi vi phạm
```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.0885}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.3181}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1291}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.3157}}|}
\hline
\rowcolor[gray]{0.9}
Mã lỗi &
Tên lỗi &
Điều kiện vi phạm &
Hành động của hệ thống &
Thông điệp thông báo người dùng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã lỗi &
Tên lỗi &
Điều kiện vi phạm &
Hành động của hệ thống &
Thông điệp thông báo người dùng \\ \hline
\endhead
\texttt{ERR-NAM-001} &
Lỗi định dạng tên &
Tên rỗng sau khi cắt khoảng trắng ở hai đầu, hoặc dài hơn 50 ký tự &
Từ chối thao tác &
``Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu.'' \\ \hline
\texttt{ERR-NAM-002} &
Lỗi trùng tên &
Tên trùng một mục khác trong cùng phạm vi duy nhất &
Từ chối thao tác &
Theo bảng thông điệp ngay dưới đây \\ \hline
\end{longtable}
\endgroup
```

Thông điệp của Lỗi trùng tên (`ERR-NAM-002`) theo đối tượng và thao tác:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1391}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8609}}|}
\hline
\rowcolor[gray]{0.9}
Đối tượng và thao tác &
Thông điệp thông báo người dùng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Đối tượng và thao tác &
Thông điệp thông báo người dùng \\ \hline
\endhead
Ví &
``Tên ví này đã tồn tại trong hệ thống. Vui lòng chọn một tên khác.'' \\ \hline
Nhóm nhu cầu &
``Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác.'' \\ \hline
Danh mục &
``Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác.'' \\ \hline
Chuyển nhóm &
``Nhóm đích đã có danh mục cùng tên. Hãy đổi tên danh mục trước khi
chuyển.'' \\ \hline
Khôi phục &
``Không thể khôi phục vì tên đã được dùng. Hãy đổi tên một trong hai mục
rồi thử lại.'' \\ \hline
\end{longtable}
\endgroup
```
\newpage

## 4. YÊU CẦU CHỨC NĂNG CHI TIẾT

Hệ thống Quản lý Tài chính Cá nhân gồm các yêu cầu chức năng nhằm hiện thực hóa các mục tiêu hệ thống và tuân thủ đầy đủ các quy tắc nghiệp vụ đã đề ra.

Mỗi yêu cầu chức năng được đặc tả theo khuôn mẫu:

1. Bảng thuộc tính yêu cầu: mục tiêu hệ thống phục vụ, ca sử dụng tương ứng theo cú pháp `UC-<DOMAIN>-NN`, quy tắc nghiệp vụ chi phối và mức độ ưu tiên (`MUST`, `SHOULD`, `MAY`).
2. Điều kiện tiền đề: trạng thái hệ thống và dữ liệu cần có trước khi kích hoạt.
3. Dữ liệu đầu vào: tham số, kiểu dữ liệu, ràng buộc giá trị và tính bắt buộc.
4. Quy tắc xử lý: các bước xử lý tuần tự, kiểm tra hợp lệ và mã lỗi tương ứng (`ERR-*`).
5. Dữ liệu đầu ra và bất biến: kết quả ghi nhận, thông điệp phản hồi và bất biến tài chính sau xử lý.
6. Tiêu chí nghiệm thu: kịch bản Given-When-Then kèm mã `AC-<DOMAIN>-NN.x`.

**Quy ước chung:**

- Tên nhóm nhu cầu và danh mục trong tiêu chí nghiệm thu là dữ liệu giả lập, không phải quy định cứng; khi kiểm thử thay bằng nhóm và danh mục thực tế trong cấu hình triển khai.
- Các yêu cầu chức năng chỉ truy vấn (`FR-WAL-02`, `FR-WAL-04`, `FR-TXN-03`, `FR-REP-01`, `FR-REP-02`) không làm thay đổi số dư ví hay bất kỳ bản ghi nào, nên không nhắc lại bất biến này ở từng yêu cầu chức năng.
- Quy tắc nghiệp vụ được viện dẫn bằng tên đầy đủ kèm mã. Mọi số tiền dùng số thập phân chính xác, làm tròn ngân hàng, tính toán 2 chữ số thập phân (giao diện hiển thị VNĐ không có phần thập phân) theo quy tắc Độ chính xác tài chính (`BR-FIN-01`). Thao tác ghi nhiều bước là nguyên tử: cùng thành công hoặc cùng hủy.
- **Quy tắc phí giao dịch**, dùng chung cho các yêu cầu chức năng sau:
  - Tạo mới giao dịch thu nhập (`FR-TXN-01`).
  - Tạo mới giao dịch chi tiêu (`FR-TXN-02`).
  - Chỉnh sửa giao dịch thu chi (`FR-TXN-04`).
  - Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn (`FR-TRF-01`).

  Nội dung quy tắc:
  - Chi phí nhỏ hơn 0: từ chối, báo lỗi chi phí âm (`ERR-FEE-001`). Chi phí mặc định 0.00 VNĐ.
  - Chi phí lớn hơn 0 bắt buộc có danh mục phí. Thiếu danh mục phí: từ chối, báo lỗi thiếu danh mục phí (`ERR-FEE-002`). Danh mục phí đã xóa hoặc không thuộc nhánh Chi tiêu (`EXPENSE`): từ chối, báo lỗi danh mục phí không hợp lệ (`ERR-FEE-003`). Chi phí bằng 0 không cần danh mục phí.
  - Chi phí lớn hơn 0 sinh một bản ghi phí loại chi tiêu, số tiền bằng đúng chi phí, liên kết với giao dịch gốc (hoặc giao dịch chuyển tiền) và ghi đồng thời với giao dịch gốc.

Bảng chia các yêu cầu chức năng theo 6 nhóm chức năng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1886}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4343}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1886}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1886}}|}
\hline
\rowcolor[gray]{0.9}
STT &
Nhóm chức năng &
Mã nhóm &
Số lượng FR \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
STT &
Nhóm chức năng &
Mã nhóm &
Số lượng FR \\ \hline
\endhead
1 &
Ví &
\texttt{FR-WAL} &
5 \\ \hline
2 &
Ghi nhận thu chi &
\texttt{FR-TXN} &
5 \\ \hline
3 &
Chuyển tiền nội bộ &
\texttt{FR-TRF} &
2 \\ \hline
4 &
Danh mục phân loại &
\texttt{FR-CAT} &
3 \\ \hline
5 &
Thống kê và báo cáo &
\texttt{FR-REP} &
2 \\ \hline
6 &
Lưu trữ và sao lưu dữ liệu &
\texttt{FR-DAT} &
3 \\ \hline
\textbf{Tổng} &
\textbf{6 nhóm chức năng} &
\strut &
\textbf{20 FR} \\ \hline
\end{longtable}
\endgroup
```

### 4.1 FR-WAL: Nhóm chức năng Ví

Quản lý các ví (tài khoản và nguồn tiền dùng ngay) của Người dùng. Hệ thống có sẵn ví "Tiền mặt" và cho phép tạo thêm ví. Số dư ví luôn đúng theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`).

#### 4.1.1 FR-WAL-01: Tạo ví mới

Bảng thuộc tính của yêu cầu chức năng Tạo mới ví (FR-WAL-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2852}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7148}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}), Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví
(\texttt{G-02}) \\ \hline
Ca sử dụng liên quan &
Tạo ví mới (\texttt{UC-WAL-01}) \\ \hline
Quy tắc áp dụng &
Quy tắc tên (\texttt{BR-NAM-01}), Bảo toàn số dư ví (\texttt{BR-FIN-02}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Hệ thống đã hoàn tất khởi tạo.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1226}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1604}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.5802}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1368}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Tên ví &
Chuỗi ký tự &
1 đến 50 ký tự sau khi cắt khoảng trắng hai đầu, không trùng tên ví khác
theo quy tắc tên (\texttt{BR-NAM-01}) &
Bắt buộc \\ \hline
Ghi chú mô tả &
Chuỗi ký tự &
Tối đa 255 ký tự &
Tùy chọn \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Tên rỗng sau khi cắt hoặc dài hơn 50 ký tự: từ chối, báo lỗi tên không hợp lệ (`ERR-NAM-001`).
2. Tên trùng ví đang hoạt động hoặc ví đã lưu trữ: từ chối, báo lỗi tên bị trùng (`ERR-NAM-002`).
3. Tạo ví ở trạng thái hoạt động (`ACTIVE`), số dư khởi điểm 0.00 VNĐ theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`); không nhận số dư ban đầu khi tạo ví. Khi khởi tạo hệ thống lần đầu, tạo sẵn đúng một ví "Tiền mặt" số dư 0.00 VNĐ.
4. Cấp mã định danh duy nhất, ghi thời điểm tạo và lưu vào tệp dữ liệu cục bộ.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1953}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8047}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Bản ghi ví mới gồm mã định danh, tên, trạng thái, thời điểm tạo và ghi
chú. \\ \hline
Bất biến hệ thống &
Số dư ví mới bằng 0.00 VNĐ; tổng số dư toàn hệ thống không đổi theo quy
tắc Bảo toàn số dư ví (\texttt{BR-FIN-02}). \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4573}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3940}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-WAL-01.1} &
Nhập tên ``MB Bank'' và ghi chú hợp lệ &
Tạo ví \texttt{ACTIVE}, số dư 0.00 VNĐ. \\ \hline
\texttt{AC-WAL-01.2} &
Nhập tên chỉ gồm khoảng trắng hoặc dài hơn 50 ký tự &
Từ chối, báo lỗi tên không hợp lệ (\texttt{ERR-NAM-001}). \\ \hline
\texttt{AC-WAL-01.3} &
Đã có ví ``MB Bank''; tạo ví ``mb bank'' hoặc `` MB Bank '' &
Từ chối, báo lỗi tên bị trùng (\texttt{ERR-NAM-002}). \\ \hline
\end{longtable}
\endgroup
```

#### 4.1.2 FR-WAL-02: Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền

Bảng thuộc tính của yêu cầu chức năng Xem danh sách ví (FR-WAL-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.3049}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.6951}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (\texttt{G-02}) \\ \hline
Ca sử dụng liên quan &
Xem danh sách ví (\texttt{UC-WAL-02}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Bảo toàn số dư ví
(\texttt{BR-FIN-02}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Dữ liệu hệ thống đã nạp vào bộ nhớ làm việc.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1967}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1967}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4344}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1721}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Bộ lọc trạng thái &
Mã phân loại &
\texttt{ACTIVE} (mặc định), \texttt{ARCHIVED} hoặc \texttt{ALL} &
Tùy chọn \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Lọc ví theo trạng thái; mặc định chỉ ví `ACTIVE`.
2. Với mỗi ví, tính lại số dư khả dụng từ lịch sử dòng tiền theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`), gồm thu, chi, tiền chuyển đi, tiền nhận về và điều chỉnh số dư. Không dùng giá trị số dư gán trực tiếp.
3. Tổng số dư hệ thống bằng tổng số dư các ví `ACTIVE` và các ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ, tính theo quy tắc Độ chính xác tài chính (`BR-FIN-01`).
4. Sắp xếp danh sách theo tên ví tăng dần.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2033}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7967}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Danh sách ví gồm mã, tên, trạng thái, số dư tính lại, thời điểm giao
dịch gần nhất; kèm tổng số dư. \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3232}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.5281}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-WAL-02.1} &
Ví ``Timo'' có điều chỉnh số dư 1,000,000 VNĐ, thu 500,000 VNĐ, chi
200,000 VNĐ &
Số dư hiển thị đúng 1,300,000.00 VNĐ theo quy tắc Bảo toàn số dư ví
(\texttt{BR-FIN-02}). \\ \hline
\texttt{AC-WAL-02.2} &
Chọn bộ lọc \texttt{ACTIVE} &
Chỉ hiển thị ví đang hoạt động, ẩn ví \texttt{ARCHIVED}. \\ \hline
\texttt{AC-WAL-02.3} &
Tổng hợp số dư toàn hệ thống &
Khớp từng đồng với tổng số dư các ví thành phần, sai số 0.00 VNĐ theo
quy tắc Độ chính xác tài chính (\texttt{BR-FIN-01}). \\ \hline
\end{longtable}
\endgroup
```

#### 4.1.3 FR-WAL-03: Cập nhật thông tin ví

Bảng thuộc tính của yêu cầu chức năng Cập nhật thông tin ví (FR-WAL-03), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên.

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.3125}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.6875}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (\texttt{G-02}) \\ \hline
Ca sử dụng liên quan &
Cập nhật thông tin ví (\texttt{UC-WAL-03}) \\ \hline
Quy tắc áp dụng &
Quy tắc tên (\texttt{BR-NAM-01}), Bảo toàn số dư ví (\texttt{BR-FIN-02}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Ví cần cập nhật tồn tại trong hệ thống.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1994}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1833}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4727}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1447}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Mã định danh ví &
Chuỗi ký tự &
Mã ví đang tồn tại &
Bắt buộc \\ \hline
Tên ví mới &
Chuỗi ký tự &
1 đến 50 ký tự sau khi cắt, không trùng ví khác theo quy tắc tên
(\texttt{BR-NAM-01}) &
Tùy chọn \\ \hline
Ghi chú mô tả mới &
Chuỗi ký tự &
Tối đa 255 ký tự &
Tùy chọn \\ \hline
Trạng thái ví &
Mã phân loại &
\texttt{ACTIVE} hoặc \texttt{ARCHIVED} &
Tùy chọn \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Kiểm tra tên mới (nếu đổi) theo quy tắc tên (`BR-NAM-01`):
   - Sai định dạng: từ chối, báo lỗi tên không hợp lệ (`ERR-NAM-001`).
   - Trùng ví khác: từ chối, báo lỗi tên bị trùng (`ERR-NAM-002`).
   - Không đối chiếu trùng với chính ví đang sửa.
2. Lưu trữ: cấm lưu trữ ví "Tiền mặt" có sẵn. Ví khác được chuyển sang `ARCHIVED` bất kể số dư; số dư ví lưu trữ vẫn tính vào tổng số dư hệ thống theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`).
3. Xóa vĩnh viễn: chỉ khi ví chưa từng có bản ghi nào trong sổ thu chi, sổ chuyển tiền hoặc sổ điều chỉnh số dư; khi xóa, tên ví được giải phóng. Ví đã có giao dịch chỉ được chuyển sang `ARCHIVED`. Cấm xóa ví "Tiền mặt" có sẵn.
4. Không cho đổi trực tiếp số dư ví tại chức năng này, theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`). Lưu thay đổi hợp lệ vào tệp dữ liệu cục bộ.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2188}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7812}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Bản ghi ví đã cập nhật tên, ghi chú hoặc trạng thái. \\ \hline
Bất biến hệ thống &
Số dư và lịch sử dòng tiền của ví giữ nguyên; tổng số dư hệ thống không
đổi theo quy tắc Bảo toàn số dư ví (\texttt{BR-FIN-02}). \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.5096}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3417}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-WAL-03.1} &
Đổi tên ví ``Vietcombank'' thành ``VCB Digibank'' (chưa tồn tại) &
Cập nhật tên, giữ nguyên số dư và lịch sử. \\ \hline
\texttt{AC-WAL-03.2} &
Yêu cầu lưu trữ hoặc xóa ví ``Tiền mặt'' có sẵn &
Từ chối, ví vẫn \texttt{ACTIVE}. \\ \hline
\texttt{AC-WAL-03.3} &
Yêu cầu xóa vĩnh viễn ví đã có ít nhất một giao dịch &
Từ chối, chỉ cho chuyển sang \texttt{ARCHIVED}. \\ \hline
\end{longtable}
\endgroup
```

#### 4.1.4 FR-WAL-04: Thống kê cơ cấu số dư theo ví

Bảng thuộc tính của yêu cầu chức năng Thống kê cơ cấu số dư theo ví (FR-WAL-04), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.3488}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.6512}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (\texttt{G-02}) \\ \hline
Ca sử dụng liên quan &
Xem danh sách ví (\texttt{UC-WAL-02}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Dữ liệu đã nạp và có ít nhất một ví.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1765}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2941}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.3529}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1765}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Ngày tính toán &
Ngày tháng (\texttt{YYYY-MM-DD}) &
Mặc định là ngày hiện tại &
Tùy chọn \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Chọn các ví `ACTIVE` và các ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ; ví `ARCHIVED` số dư 0.00 VNĐ không đưa vào.
2. Tổng số dư bằng tổng số dư các ví được chọn, tính theo quy tắc Độ chính xác tài chính (`BR-FIN-01`).
3. Tổng bằng 0.00 VNĐ: tỷ lệ mọi ví là 0.00%, không chia cho 0, tránh lỗi chia cho 0 (`ERR-FIN-002`).
4. Tổng lớn hơn 0: tỷ lệ bằng số dư ví chia tổng số dư nhân 100%, tính với tối thiểu 4 chữ số thập phân trung gian, làm tròn ngân hàng 2 chữ số thập phân. Ví `ARCHIVED` hiển thị kèm nhãn "đã lưu trữ".
5. Sắp xếp theo tỷ lệ giảm dần.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2660}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7340}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Bảng cơ cấu gồm tên ví, trạng thái, số dư và tỷ lệ phần trăm. \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3524}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4989}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-WAL-04.1} &
Ví A 6,000,000 VNĐ, ví B 4,000,000 VNĐ &
Tỷ lệ A 60.00\%, B 40.00\%. \\ \hline
\texttt{AC-WAL-04.2} &
Mọi ví có số dư 0.00 VNĐ &
Mọi tỷ lệ 0.00\%, không phát sinh lỗi chia cho 0 (\texttt{ERR-FIN-003}). \\ \hline
\texttt{AC-WAL-04.3} &
Ví hoạt động 9,950,000 VNĐ, ví lưu trữ 50,000 VNĐ &
Ví lưu trữ hiển thị dòng riêng kèm nhãn, tỷ lệ 0.50\%, tổng các tỷ lệ
đúng 100.00\%. \\ \hline
\end{longtable}
\endgroup
```

#### 4.1.5 FR-WAL-05: Điều chỉnh số dư ví

Bảng thuộc tính của yêu cầu chức năng Điều chỉnh số dư ví (FR-WAL-05), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.3012}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.6988}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (\texttt{G-02}) \\ \hline
Ca sử dụng liên quan &
Điều chỉnh số dư ví (\texttt{UC-WAL-04}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Bảo toàn số dư ví
(\texttt{BR-FIN-02}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Ví cần điều chỉnh tồn tại và đang `ACTIVE`.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2216}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1773}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4765}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1247}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Mã định danh ví &
Chuỗi ký tự &
Ví đang \texttt{ACTIVE} &
Bắt buộc \\ \hline
Số dư thực tế quan sát &
Số tiền &
Lớn hơn hoặc bằng 0.00 VNĐ theo quy tắc Bảo toàn số dư ví
(\texttt{BR-FIN-02}) &
Bắt buộc \\ \hline
Lý do điều chỉnh &
Chuỗi ký tự &
Tối đa 255 ký tự sau khi cắt; cho phép để trống &
Tùy chọn \\ \hline
Thời điểm điều chỉnh &
Ngày tháng (\texttt{YYYY-MM-DD}) &
Mặc định là ngày hiện tại &
Tùy chọn \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Không tìm thấy ví hoặc ví `ARCHIVED`: từ chối.
2. Số dư quan sát âm: từ chối, báo lỗi số dư ví không đủ hoặc âm (`ERR-WAL-001`). Sai định dạng số: từ chối, báo lỗi sai định dạng số (`ERR-FIN-001`).
3. Lý do (sau khi cắt) dài hơn 255 ký tự: từ chối. Lý do trống hoặc chỉ khoảng trắng: chấp nhận, lưu chuỗi rỗng.
4. Chênh lệch bằng số dư quan sát trừ số dư khả dụng hiện tại, tính theo quy tắc Độ chính xác tài chính (`BR-FIN-01`) và quy tắc Bảo toàn số dư ví (`BR-FIN-02`). Chênh lệch bằng 0.00 VNĐ: từ chối, không tạo bản ghi rỗng.
5. Tạo bản ghi điều chỉnh bất biến trong sổ điều chỉnh số dư gồm số dư trước, số dư sau (bằng số dư quan sát), chênh lệch, lý do và thời điểm. Cấm sửa bản ghi sau khi lưu.
6. Tính lại số dư ví ngay từ lịch sử dòng tiền theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`).

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2000}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8000}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Bản ghi điều chỉnh trong sổ điều chỉnh số dư kèm số dư ví đã cập nhật. \\ \hline
Bất biến hệ thống &
Số dư ví sau điều chỉnh bằng đúng số dư quan sát; tổng số dư hệ thống
biến động đúng bằng chênh lệch theo quy tắc Bảo toàn số dư ví
(\texttt{BR-FIN-02}). \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4390}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4124}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-WAL-05.1} &
Ví ``Tiền mặt'' ghi nhận 2,000,000.00 VNĐ, đếm thực tế 1,750,000.00 VNĐ,
lý do ``Kiểm kê tiền mặt định kỳ'' &
Tạo điều chỉnh chênh lệch -250,000.00 VNĐ, số dư ví 1,750,000.00 VNĐ. \\ \hline
\texttt{AC-WAL-05.2} &
Số dư thực tế âm hoặc lý do dài quá 255 ký tự &
Từ chối, số dư giữ nguyên; khi số dư âm báo lỗi số dư ví không đủ hoặc
âm (\texttt{ERR-WAL-001}). \\ \hline
\texttt{AC-WAL-05.3} &
Số dư thực tế bằng số dư hiện tại, hoặc điều chỉnh trên ví
\texttt{ARCHIVED} &
Từ chối, không tạo bản ghi chênh lệch 0. \\ \hline
\texttt{AC-WAL-05.4} &
Yêu cầu hợp lệ nhưng để trống lý do (hoặc chỉ khoảng trắng) &
Tạo điều chỉnh với lý do rỗng, số dư cập nhật đúng. \\ \hline
\end{longtable}
\endgroup
```

### 4.2 FR-TXN: Nhóm chức năng Ghi nhận thu chi

Quản lý sổ giao dịch thu chi hằng ngày, gắn biến động số dư với danh mục 3 cấp. Mọi giao dịch chỉ liên kết với danh mục chi tiết cấp 3 đang dùng theo quy tắc Phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`). Phí giao dịch áp dụng theo quy tắc phí giao dịch nêu tại phần Quy ước chung.

#### 4.2.1 FR-TXN-01: Tạo mới giao dịch thu nhập

Bảng thuộc tính của yêu cầu chức năng Tạo mới giao dịch thu nhập (FR-TXN-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2381}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7619}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}), Mục tiêu bảo đảm độ chính xác số học tài chính
(\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Ghi nhận giao dịch thu chi (\texttt{UC-TXN-01}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Bảo toàn số dư ví
(\texttt{BR-FIN-02}), Phân cấp danh mục thu chi 3 cấp
(\texttt{BR-FIN-04}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Có ít nhất một ví `ACTIVE` và một danh mục chi tiết `INCOME` đang dùng.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2006}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1646}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4922}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1426}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Ngày giao dịch &
Ngày tháng (\texttt{YYYY-MM-DD}) &
Không vượt quá ngày hiện tại; mặc định ngày hiện tại &
Bắt buộc \\ \hline
Số tiền thu nhập &
Số tiền (\texttt{VNĐ}) &
Lớn hơn 0, 2 chữ số thập phân theo quy tắc Độ chính xác tài chính
(\texttt{BR-FIN-01}) &
Bắt buộc \\ \hline
Mã định danh ví &
Chuỗi ký tự &
Ví \texttt{ACTIVE} &
Bắt buộc \\ \hline
Mã định danh danh mục &
Chuỗi ký tự &
Danh mục chi tiết đang dùng, thuộc nhánh \texttt{INCOME} theo quy tắc
Phân cấp danh mục thu chi 3 cấp (\texttt{BR-FIN-04}) &
Bắt buộc \\ \hline
Chi phí giao dịch &
Số tiền (\texttt{VNĐ}) &
Không âm, mặc định 0.00 VNĐ &
Tùy chọn \\ \hline
Mã định danh danh mục phí &
Chuỗi ký tự &
Danh mục \texttt{EXPENSE} đang dùng &
Bắt buộc khi chi phí lớn hơn 0 \\ \hline
Ghi chú mô tả &
Chuỗi ký tự &
Tối đa 255 ký tự &
Tùy chọn \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Số tiền sai định dạng hoặc chứa ký tự không hợp lệ: từ chối, báo lỗi sai định dạng số (`ERR-FIN-001`). Số tiền nhỏ hơn hoặc bằng 0: từ chối.
2. Ví không tồn tại hoặc `ARCHIVED`: từ chối, báo lỗi ví không hoạt động.
3. Danh mục không thuộc `INCOME`, đã xóa, hoặc không phải danh mục chi tiết đang dùng: từ chối.
4. Kiểm tra phí theo quy tắc phí giao dịch nêu tại phần Quy ước chung:
   - Chi phí âm: từ chối, báo lỗi chi phí âm (`ERR-FEE-001`).
   - Có phí nhưng thiếu danh mục phí: từ chối, báo lỗi thiếu danh mục phí (`ERR-FEE-002`).
   - Danh mục phí đã xóa hoặc không thuộc nhánh `EXPENSE`: từ chối, báo lỗi danh mục phí không hợp lệ (`ERR-FEE-003`).
5. Khi chi phí lớn hơn 0: số dư ví trước giao dịch cộng thu nhập trừ chi phí phải lớn hơn hoặc bằng 0, theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`); nếu không, từ chối toàn bộ, báo lỗi số dư ví không đủ hoặc âm (`ERR-WAL-001`).
6. Tạo bản ghi thu nhập (mã duy nhất, tự liên kết nhóm danh mục từ danh mục chi tiết); tạo bản ghi phí liên kết theo quy tắc phí giao dịch.
7. Cập nhật số dư ví: cộng thu nhập, trừ chi phí, theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`).

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1991}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8009}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Bản ghi thu nhập trong sổ thu chi; bản ghi phí liên kết nếu có chi phí. \\ \hline
Bất biến hệ thống &
Số dư ví và tổng số dư hệ thống tăng đúng bằng thu nhập trừ chi phí theo
quy tắc Bảo toàn số dư ví (\texttt{BR-FIN-02}). \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4226}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4287}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-TXN-01.1} &
Ví ``MB Bank'' 10,000,000.00 VNĐ; thu lương 25,000,000.00 VNĐ, chi phí
5,500.00 VNĐ, danh mục phí hợp lệ &
Ghi nhận thu nhập và phí liên kết; số dư ví 34,994,500.00 VNĐ. \\ \hline
\texttt{AC-TXN-01.2} &
Số tiền thu nhập nhỏ hơn hoặc bằng 0 hoặc sai định dạng &
Từ chối, báo lỗi sai định dạng số (\texttt{ERR-FIN-001}). \\ \hline
\texttt{AC-TXN-01.3} &
Ví ``Tiền mặt'' 0.00 VNĐ; thu 100,000.00 VNĐ nhưng chi phí 120,000.00
VNĐ &
Từ chối toàn bộ, báo lỗi số dư ví không đủ hoặc âm
(\texttt{ERR-WAL-001}), số dư giữ nguyên. \\ \hline
\texttt{AC-TXN-01.4} &
Có chi phí lớn hơn 0 nhưng thiếu danh mục phí &
Từ chối toàn bộ, báo lỗi thiếu danh mục phí (\texttt{ERR-FEE-002}), số
dư giữ nguyên. \\ \hline
\end{longtable}
\endgroup
```

#### 4.2.2 FR-TXN-02: Tạo mới giao dịch chi tiêu

Bảng thuộc tính của yêu cầu chức năng Tạo mới giao dịch chi tiêu (FR-TXN-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2381}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7619}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}), Mục tiêu bảo đảm độ chính xác số học tài chính
(\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Ghi nhận giao dịch thu chi (\texttt{UC-TXN-01}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Bảo toàn số dư ví
(\texttt{BR-FIN-02}), Phân cấp danh mục thu chi 3 cấp
(\texttt{BR-FIN-04}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Có ít nhất một ví `ACTIVE` với số dư lớn hơn 0 và một danh mục chi tiết `EXPENSE` đang dùng.

Dữ liệu đầu vào: như yêu cầu Tạo mới giao dịch thu nhập (`FR-TXN-01`), khác ở hai điểm: số tiền là *Số tiền chi tiêu* (lớn hơn 0) và *Mã định danh danh mục* phải thuộc nhánh `EXPENSE`.

Quy tắc xử lý:

1. Số tiền sai định dạng: từ chối, báo lỗi sai định dạng số (`ERR-FIN-001`). Số tiền nhỏ hơn hoặc bằng 0: từ chối.
2. Ví không tồn tại hoặc `ARCHIVED`: từ chối, báo lỗi ví không hoạt động.
3. Danh mục không thuộc `EXPENSE`, đã xóa, hoặc không phải danh mục chi tiết đang dùng: từ chối.
4. Kiểm tra phí theo quy tắc phí giao dịch nêu tại phần Quy ước chung:
   - Chi phí âm: từ chối, báo lỗi chi phí âm (`ERR-FEE-001`).
   - Có phí nhưng thiếu danh mục phí: từ chối, báo lỗi thiếu danh mục phí (`ERR-FEE-002`).
   - Danh mục phí đã xóa hoặc không thuộc nhánh `EXPENSE`: từ chối, báo lỗi danh mục phí không hợp lệ (`ERR-FEE-003`).
5. Số dư khả dụng của ví phải lớn hơn hoặc bằng tổng số tiền chi và chi phí, theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`); nếu không, từ chối toàn bộ, báo lỗi số dư ví không đủ hoặc âm (`ERR-WAL-001`).
6. Tạo bản ghi chi tiêu (mã duy nhất, loại `EXPENSE`, tự liên kết nhóm danh mục); tạo bản ghi phí liên kết theo quy tắc phí giao dịch.
7. Trừ tổng số tiền chi và chi phí khỏi số dư ví, theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`).

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1991}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8009}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Bản ghi chi tiêu trong sổ thu chi; bản ghi phí liên kết nếu có chi phí. \\ \hline
Bất biến hệ thống &
Số dư ví và tổng số dư hệ thống giảm đúng bằng tổng số tiền chi và chi
phí theo quy tắc Bảo toàn số dư ví (\texttt{BR-FIN-02}). \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4405}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4109}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-TXN-02.1} &
Ví ``Tiền mặt'' 500,000.00 VNĐ; chi 120,000.00 VNĐ, chi phí 3,300.00
VNĐ, danh mục phí hợp lệ &
Ghi nhận chi tiêu và phí liên kết; số dư còn 376,700.00 VNĐ. \\ \hline
\texttt{AC-TXN-02.2} &
Ví ``Tiền mặt'' 200,000.00 VNĐ; chi 150,000.00 VNĐ, chi phí 60,000.00
VNĐ &
Từ chối toàn bộ, báo lỗi số dư ví không đủ hoặc âm
(\texttt{ERR-WAL-001}), số dư giữ nguyên. \\ \hline
\texttt{AC-TXN-02.3} &
Chọn danh mục \texttt{INCOME} hoặc danh mục đã xóa &
Từ chối, báo lỗi loại danh mục không khớp. \\ \hline
\texttt{AC-TXN-02.4} &
Chi phí lớn hơn 0 nhưng không chọn danh mục phí &
Từ chối, báo lỗi thiếu danh mục phí (\texttt{ERR-FEE-002}). \\ \hline
\end{longtable}
\endgroup
```

#### 4.2.3 FR-TXN-03: Tra cứu và lọc lịch sử giao dịch thu chi

Bảng thuộc tính của yêu cầu chức năng Tra cứu và lọc lịch sử giao dịch thu chi (FR-TXN-03), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2419}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7581}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}), Mục tiêu bảo đảm độ chính xác số học tài chính
(\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Tra cứu và lọc lịch sử giao dịch (\texttt{UC-TXN-02}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Phân cấp danh mục thu chi 3
cấp (\texttt{BR-FIN-04}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1976}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1785}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.5182}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1057}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Khoảng ngày giao dịch &
Ngày tháng (\texttt{YYYY-MM-DD}) &
Ngày bắt đầu không sau ngày kết thúc; mặc định từ đầu đến cuối tháng
hiện tại &
Tùy chọn \\ \hline
Danh sách mã định danh ví &
Danh sách chuỗi &
Mặc định toàn bộ ví &
Tùy chọn \\ \hline
Loại giao dịch &
Mã phân loại &
\texttt{ALL} (mặc định), \texttt{INCOME}, \texttt{EXPENSE} &
Tùy chọn \\ \hline
Mã nhóm hoặc danh mục &
Chuỗi ký tự &
Gồm cả nhóm và danh mục đã xóa &
Tùy chọn \\ \hline
Từ khóa tìm kiếm &
Chuỗi ký tự &
Tìm chuỗi con trong ghi chú, không phân biệt hoa thường &
Tùy chọn \\ \hline
Phân trang &
Cặp số nguyên dương &
Trang từ 1; 10 đến 100 bản ghi mỗi trang; mặc định trang 1, 50 bản ghi &
Tùy chọn \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Kết hợp đồng thời mọi tiêu chí lọc. Ngày bắt đầu sau ngày kết thúc: từ chối, báo lỗi khoảng ngày không hợp lệ.
2. Trên toàn bộ tập kết quả trước khi phân trang, tổng hợp theo quy tắc Độ chính xác tài chính (`BR-FIN-01`), 2 chữ số thập phân: tổng thu nhập; tổng chi tiêu (gồm phí liên kết); dòng tiền thuần bằng tổng thu trừ tổng chi.
3. Sắp xếp mặc định theo ngày giao dịch giảm dần, rồi thời điểm tạo giảm dần; trích xuất theo trang.
4. Bản ghi phí hiển thị thành dòng riêng kèm nhãn liên kết tới giao dịch gốc.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2315}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7685}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Danh sách theo trang, tổng số bản ghi, tổng số trang và ba chỉ số tổng
hợp. \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3639}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4874}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-TXN-03.1} &
Lọc từ 2026-01-01 đến 2026-01-31, loại \texttt{EXPENSE} &
Trả đúng các khoản chi trong kỳ, tổng chi tiêu chính xác 2 chữ số thập
phân theo quy tắc Độ chính xác tài chính (\texttt{BR-FIN-01}). \\ \hline
\texttt{AC-TXN-03.2} &
Ngày bắt đầu sau ngày kết thúc &
Từ chối, báo lỗi khoảng ngày không hợp lệ. \\ \hline
\texttt{AC-TXN-03.3} &
Có chi 120,000.00 VNĐ kèm phí 3,300.00 VNĐ trong tháng; lọc
\texttt{EXPENSE} &
Hiển thị hai dòng (chi và phí liên kết), tổng chi tiêu 123,300.00 VNĐ. \\ \hline
\end{longtable}
\endgroup
```

#### 4.2.4 FR-TXN-04: Chỉnh sửa giao dịch thu chi

Bảng thuộc tính của yêu cầu chức năng Chỉnh sửa giao dịch thu chi (FR-TXN-04), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2373}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7627}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}), Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví
(\texttt{G-02}), Mục tiêu bảo đảm độ chính xác số học tài chính
(\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Chỉnh sửa giao dịch thu chi (\texttt{UC-TXN-03}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Bảo toàn số dư ví
(\texttt{BR-FIN-02}), Phân cấp danh mục thu chi 3 cấp
(\texttt{BR-FIN-04}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Giao dịch tồn tại và không phải bản ghi phí giao dịch độc lập.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2460}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1841}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4127}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1571}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Mã định danh giao dịch &
Chuỗi ký tự &
Giao dịch thu chi đang tồn tại &
Bắt buộc \\ \hline
Ngày giao dịch mới &
Ngày tháng (\texttt{YYYY-MM-DD}) &
Không vượt quá ngày hiện tại &
Bắt buộc \\ \hline
Số tiền mới &
Số tiền (\texttt{VNĐ}) &
Lớn hơn 0, 2 chữ số thập phân &
Bắt buộc \\ \hline
Mã định danh ví mới &
Chuỗi ký tự &
Ví \texttt{ACTIVE} &
Bắt buộc \\ \hline
Mã định danh danh mục mới &
Chuỗi ký tự &
Cùng loại với giao dịch gốc; được giữ danh mục cũ kể cả đã xóa &
Bắt buộc \\ \hline
Chi phí mới &
Số tiền (\texttt{VNĐ}) &
Không âm; để trống thì giữ chi phí hiện tại &
Tùy chọn \\ \hline
Mã định danh danh mục phí mới &
Chuỗi ký tự &
Danh mục \texttt{EXPENSE} đang dùng &
Bắt buộc khi phát sinh phí mới \\ \hline
Ghi chú mô tả &
Chuỗi ký tự &
Tối đa 255 ký tự &
Tùy chọn \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Không tìm thấy giao dịch hoặc mã thuộc bản ghi phí độc lập: từ chối. Phí chỉ được điều chỉnh thông qua giao dịch gốc.
2. Loại giao dịch (`INCOME` hoặc `EXPENSE`) không đổi. Danh mục mới phải cùng loại theo quy tắc Phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`); giữ danh mục cũ được phép dù đã xóa, đổi sang danh mục khác thì danh mục mới phải đang hoạt động.
3. Hoàn nguyên ví cũ theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`):
   - Giao dịch `INCOME`: trừ thu nhập cũ và hoàn chi phí cũ vào ví cũ; số dư âm thì từ chối, báo lỗi số dư ví không đủ hoặc âm (`ERR-WAL-001`).
   - Giao dịch `EXPENSE`: hoàn số tiền chi cũ và chi phí cũ.
4. Áp dụng lên ví mới trên số dư sau hoàn nguyên, kể cả khi ví mới trùng ví cũ:
   - `INCOME`: cộng số tiền mới, trừ chi phí mới, số dư sau không âm.
   - `EXPENSE`: số dư phải đủ trừ số tiền mới và chi phí mới.
   - Không đủ: từ chối, báo lỗi số dư ví không đủ hoặc âm (`ERR-WAL-001`).
5. Kiểm tra và xử lý phí liên kết theo quy tắc phí giao dịch nêu tại phần Quy ước chung:
   - Chi phí âm: từ chối, báo lỗi chi phí âm (`ERR-FEE-001`).
   - Có phí nhưng thiếu danh mục phí: từ chối, báo lỗi thiếu danh mục phí (`ERR-FEE-002`).
   - Danh mục phí đã xóa hoặc không thuộc nhánh `EXPENSE`: từ chối, báo lỗi danh mục phí không hợp lệ (`ERR-FEE-003`).
   - Chi phí mới bằng 0: xóa bản ghi phí hiện có.
   - Chi phí mới lớn hơn 0 và chưa có phí: tạo mới bản ghi phí.
   - Chi phí cũ và mới đều lớn hơn 0: cập nhật số tiền và danh mục phí.
6. Cập nhật giao dịch, phí liên kết và số dư ví là một thao tác nguyên tử; lỗi ở bất kỳ bước nào thì hủy toàn bộ.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1858}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8142}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Giao dịch đã cập nhật; bản ghi phí liên kết được tạo mới, cập nhật hoặc
xóa tương ứng. \\ \hline
Bất biến hệ thống &
Số dư ví cũ và ví mới biến động đúng bằng chênh lệch giữa dữ liệu mới và
cũ theo quy tắc Bảo toàn số dư ví (\texttt{BR-FIN-02}). \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4710}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3805}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-TXN-04.1} &
Sửa khoản chi 100,000.00 VNĐ từ ví A thành 150,000.00 VNĐ từ ví B (đủ
tiền) &
Hoàn 100,000.00 VNĐ vào ví A, trừ 150,000.00 VNĐ từ ví B, cập nhật giao
dịch. \\ \hline
\texttt{AC-TXN-04.2} &
Sửa khoản chi từ 200,000.00 lên 800,000.00 VNĐ khi ví chỉ còn 300,000.00
VNĐ &
Từ chối, báo lỗi số dư ví không đủ hoặc âm (\texttt{ERR-WAL-001}), dữ
liệu giữ nguyên. \\ \hline
\texttt{AC-TXN-04.3} &
Sửa khoản chi 120,000.00 VNĐ đang có phí 3,300.00 VNĐ bằng cách đặt chi
phí về 0.00 VNĐ &
Xóa phí liên kết và hoàn 3,300.00 VNĐ vào ví. \\ \hline
\texttt{AC-TXN-04.4} &
Nhập mã một phí giao dịch độc lập để sửa trực tiếp &
Từ chối sửa trực tiếp phí độc lập. \\ \hline
\end{longtable}
\endgroup
```

#### 4.2.5 FR-TXN-05: Xóa giao dịch thu chi

Bảng thuộc tính của yêu cầu chức năng Xóa giao dịch thu chi (FR-TXN-05), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2632}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7368}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}), Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví
(\texttt{G-02}), Mục tiêu bảo đảm độ chính xác số học tài chính
(\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Xóa giao dịch thu chi (\texttt{UC-TXN-04}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Bảo toàn số dư ví
(\texttt{BR-FIN-02}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Giao dịch tồn tại và không phải bản ghi phí giao dịch độc lập.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2216}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1676}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4541}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1568}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Mã định danh giao dịch &
Chuỗi ký tự &
Giao dịch thu chi đang tồn tại &
Bắt buộc \\ \hline
Xác nhận xóa &
Cờ logic &
Phải là Đúng (\texttt{true}) để chấp thuận xóa &
Bắt buộc \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Không tìm thấy giao dịch: từ chối. Mã là bản ghi phí độc lập: từ chối, báo phí giao dịch không được xóa trực tiếp.
2. Xác nhận là Sai (`false`): hủy thao tác, giữ nguyên dữ liệu.
3. Hoàn nguyên số dư theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`):
   - `INCOME`: trừ thu nhập và hoàn chi phí vào ví nhận; số dư âm thì từ chối, báo lỗi số dư ví không đủ hoặc âm (`ERR-WAL-001`).
   - `EXPENSE`: hoàn số tiền chi và chi phí vào ví chi.
4. Xóa giao dịch gốc cùng phí liên kết và hoàn nguyên số dư là một thao tác nguyên tử.
5. Tính lại các chỉ số thống kê liên quan.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2019}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7981}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Giao dịch và phí liên kết (nếu có) bị xóa vĩnh viễn khỏi sổ thu chi. \\ \hline
Bất biến hệ thống &
Số dư ví phục hồi đúng về trạng thái trước khi giao dịch phát sinh theo
quy tắc Bảo toàn số dư ví (\texttt{BR-FIN-02}). \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4646}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3868}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-TXN-05.1} &
Ví ``MB Bank'' 4,500,000.00 VNĐ sau khi chi 500,000.00 VNĐ; xác nhận xóa &
Xóa giao dịch, số dư về 5,000,000.00 VNĐ. \\ \hline
\texttt{AC-TXN-05.2} &
Ví 200,000.00 VNĐ; xóa khoản thu 1,000,000.00 VNĐ &
Từ chối, báo lỗi số dư ví không đủ hoặc âm (\texttt{ERR-WAL-001}), dữ
liệu nguyên vẹn. \\ \hline
\texttt{AC-TXN-05.3} &
Ví ``MB Bank'' 376,700.00 VNĐ sau khi chi 120,000.00 VNĐ kèm phí
3,300.00 VNĐ; xác nhận xóa &
Xóa đồng thời giao dịch và phí, số dư về 500,000.00 VNĐ. \\ \hline
\end{longtable}
\endgroup
```

### 4.3 FR-TRF: Nhóm chức năng Chuyển tiền nội bộ

Chuyển tiền giữa các ví của cùng Người dùng theo cơ chế ghi nhận đồng thời (trừ ví nguồn, cộng ví đích) của quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`). Tổng số dư các ví không đổi; nếu có chi phí thì giảm đúng bằng chi phí.

#### 4.3.1 FR-TRF-01: Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn

Bảng thuộc tính của yêu cầu chức năng Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn (FR-TRF-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2443}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7557}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (\texttt{G-02}), Mục
tiêu bảo đảm độ chính xác số học tài chính (\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Chuyển tiền nội bộ (\texttt{UC-TRF-01}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Bảo toàn số dư ví
(\texttt{BR-FIN-02}), Ghi nhận đồng thời khi chuyển tiền nội bộ
(\texttt{BR-FIN-03}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Có ít nhất hai ví `ACTIVE`, ví nguồn có số dư lớn hơn 0.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2110}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1691}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4734}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1465}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Mã định danh ví nguồn &
Chuỗi ký tự &
Ví \texttt{ACTIVE} &
Bắt buộc \\ \hline
Mã định danh ví đích &
Chuỗi ký tự &
Ví \texttt{ACTIVE}, khác ví nguồn theo quy tắc Ghi nhận đồng thời khi
chuyển tiền nội bộ (\texttt{BR-FIN-03}) &
Bắt buộc \\ \hline
Số tiền chuyển &
Số tiền (\texttt{VNĐ}) &
Lớn hơn 0, 2 chữ số thập phân &
Bắt buộc \\ \hline
Ngày giao dịch &
Ngày tháng (\texttt{YYYY-MM-DD}) &
Không vượt quá ngày hiện tại; mặc định ngày hiện tại &
Bắt buộc \\ \hline
Chi phí giao dịch &
Số tiền (\texttt{VNĐ}) &
Không âm, mặc định 0.00 VNĐ &
Tùy chọn \\ \hline
Mã định danh danh mục phí &
Chuỗi ký tự &
Danh mục \texttt{EXPENSE} đang dùng, theo quy tắc phí giao dịch &
Bắt buộc khi chi phí lớn hơn 0 \\ \hline
Ghi chú mô tả &
Chuỗi ký tự &
Tối đa 255 ký tự &
Tùy chọn \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Kiểm tra số tiền và chi phí:
   - Sai định dạng số: từ chối, báo lỗi sai định dạng số (`ERR-FIN-001`).
   - Số tiền chuyển nhỏ hơn hoặc bằng 0: từ chối, báo lỗi số tiền chuyển không hợp lệ (`ERR-TRF-002`).
   - Chi phí âm: từ chối, báo lỗi chi phí âm (`ERR-FEE-001`).
2. Một trong hai ví không `ACTIVE`: từ chối. Ví đích trùng ví nguồn: từ chối, báo lỗi ví đích trùng ví nguồn (`ERR-TRF-001`).
3. Kiểm tra danh mục phí theo quy tắc phí giao dịch nêu tại phần Quy ước chung:
   - Có phí nhưng thiếu danh mục phí: từ chối, báo lỗi thiếu danh mục phí (`ERR-FEE-002`).
   - Danh mục phí đã xóa hoặc không thuộc nhánh `EXPENSE`: từ chối, báo lỗi danh mục phí không hợp lệ (`ERR-FEE-003`).
4. Số dư khả dụng ví nguồn phải lớn hơn hoặc bằng tổng số tiền chuyển và chi phí, theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`) và quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`); nếu không, từ chối toàn bộ, báo lỗi số dư ví không đủ hoặc âm (`ERR-WAL-001`).
5. Trừ tổng số tiền chuyển và chi phí khỏi ví nguồn, cộng số tiền chuyển vào ví đích; hai biến động này cùng có hiệu lực hoặc cùng bị hủy theo quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`).
6. Tạo bản ghi chuyển tiền (mã duy nhất) trong sổ chuyển tiền nội bộ. Nếu có chi phí, tạo bản ghi phí loại chi tiêu trong sổ thu chi, liên kết với mã chuyển tiền. Chi phí tính vào tổng chi tiêu; khoản chuyển thuần túy không làm đổi tổng thu hay tổng chi.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1826}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8174}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Bản ghi chuyển tiền trong sổ chuyển tiền nội bộ; bản ghi phí liên kết
trong sổ thu chi nếu có chi phí. \\ \hline
Bất biến hệ thống &
Tổng số dư hệ thống không đổi khi không có chi phí, hoặc giảm đúng bằng
chi phí theo quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ
(\texttt{BR-FIN-03}). \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3447}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.5067}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-TRF-01.1} &
Ví nguồn ``MB Bank'' 5,000,000.00 VNĐ, ví đích ``Tiền mặt'' 1,000,000.00
VNĐ; chuyển 2,000,000.00 VNĐ, phí 5,500.00 VNĐ, danh mục phí hợp lệ &
MB Bank còn 2,994,500.00 VNĐ; Tiền mặt 3,000,000.00 VNĐ; tạo bản ghi
chuyển tiền và phí 5,500.00 VNĐ; tổng số dư giảm đúng 5,500.00 VNĐ. \\ \hline
\texttt{AC-TRF-01.2} &
Ví nguồn trùng ví đích &
Từ chối, báo lỗi ví đích trùng ví nguồn (\texttt{ERR-TRF-001}). \\ \hline
\texttt{AC-TRF-01.3} &
Số tiền chuyển nhỏ hơn hoặc bằng 0 &
Từ chối, báo lỗi số tiền chuyển không hợp lệ (\texttt{ERR-TRF-002}). \\ \hline
\texttt{AC-TRF-01.4} &
Ví nguồn 1,000,000.00 VNĐ chuyển 1,000,000.00 VNĐ kèm phí 5,500.00 VNĐ &
Từ chối toàn bộ, báo lỗi số dư ví không đủ hoặc âm
(\texttt{ERR-WAL-001}), số dư hai ví giữ nguyên. \\ \hline
\texttt{AC-TRF-01.5} &
Có phí nhưng thiếu danh mục phí &
Từ chối toàn bộ, báo lỗi thiếu danh mục phí (\texttt{ERR-FEE-002}), số
dư hai ví giữ nguyên. \\ \hline
\end{longtable}
\endgroup
```

#### 4.3.2 FR-TRF-02: Tra cứu và xóa giao dịch chuyển tiền nội bộ

Bảng thuộc tính của yêu cầu chức năng Tra cứu và xóa giao dịch chuyển tiền nội bộ (FR-TRF-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2113}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7887}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví (\texttt{G-02}), Mục
tiêu bảo đảm độ chính xác số học tài chính (\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Tra cứu lịch sử chuyển tiền (\texttt{UC-TRF-02}), Xóa giao dịch chuyển
tiền (\texttt{UC-TRF-03}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Bảo toàn số dư ví
(\texttt{BR-FIN-02}), Ghi nhận đồng thời khi chuyển tiền nội bộ
(\texttt{BR-FIN-03}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong; khi xóa, giao dịch chuyển tiền phải đang tồn tại.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2077}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1498}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4251}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2174}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Mã định danh chuyển tiền &
Chuỗi ký tự &
Giao dịch chuyển tiền đang tồn tại &
Bắt buộc khi xóa \\ \hline
Xác nhận xóa &
Cờ logic &
Phải là Đúng (\texttt{true}) để chấp thuận xóa &
Bắt buộc khi xóa \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Tra cứu: hiển thị danh sách theo ngày giao dịch giảm dần gồm ngày, ví nguồn, ví đích, số tiền chuyển, chi phí, ghi chú.
2. Xóa: không tìm thấy bản ghi thì từ chối, báo lỗi không tìm thấy giao dịch chuyển tiền; xác nhận là Sai (`false`) thì hủy, giữ nguyên dữ liệu.
3. Số dư khả dụng ví đích phải lớn hơn hoặc bằng số tiền đã nhận, theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`); nếu không, từ chối, báo lỗi số dư ví không đủ hoặc âm (`ERR-WAL-001`).
4. Khôi phục đồng thời hai ví theo quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): hoàn số tiền chuyển và chi phí vào ví nguồn, thu hồi số tiền chuyển khỏi ví đích.
5. Xóa bản ghi chuyển tiền và phí liên kết (nếu có); chỉ ghi nhận khi cả cập nhật số dư và xóa bản ghi đều thành công.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1826}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8174}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Danh sách lịch sử chuyển tiền (khi tra cứu); hoặc bản ghi chuyển tiền và
phí liên kết bị xóa. \\ \hline
Bất biến hệ thống &
Khi xóa, số dư hai ví và tổng số dư phục hồi đúng về trạng thái trước
khi chuyển theo quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ
(\texttt{BR-FIN-03}). \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3977}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4537}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-TRF-02.1} &
Chuyển 2,000,000.00 VNĐ từ ví A sang ví B kèm phí 3,300.00 VNĐ; xác nhận
xóa &
Ví A được hoàn 2,003,300.00 VNĐ, ví B bị trừ 2,000,000.00 VNĐ, bản ghi
chuyển tiền và phí bị xóa. \\ \hline
\texttt{AC-TRF-02.2} &
Ví B sau khi nhận 2,000,000.00 VNĐ chỉ còn 500,000.00 VNĐ; yêu cầu xóa &
Từ chối, báo lỗi số dư ví không đủ hoặc âm (\texttt{ERR-WAL-001}), dữ
liệu hai ví nguyên vẹn. \\ \hline
\texttt{AC-TRF-02.3} &
Mở xem lịch sử chuyển tiền &
Hiển thị danh sách theo ngày giảm dần. \\ \hline
\texttt{AC-TRF-02.4} &
Yêu cầu xóa với xác nhận Sai (\texttt{false}) &
Từ chối xóa và bảo toàn dữ liệu. \\ \hline
\end{longtable}
\endgroup
```

### 4.4 FR-CAT: Nhóm chức năng Danh mục phân loại

Quản lý cấu trúc danh mục 3 cấp theo quy tắc Phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): Loại thu chi cố định; Nhóm nhu cầu và Danh mục chi tiết do Người dùng quản lý.

#### 4.4.1 FR-CAT-01: Tạo cấu trúc danh mục 3 cấp

Bảng thuộc tính của yêu cầu chức năng Tạo cấu trúc danh mục 3 cấp (FR-CAT-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.3099}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.6901}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}) \\ \hline
Ca sử dụng liên quan &
Khởi tạo cấu trúc danh mục (\texttt{UC-CAT-01}) \\ \hline
Quy tắc áp dụng &
Phân cấp danh mục thu chi 3 cấp (\texttt{BR-FIN-04}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Hệ thống khởi tạo lần đầu trên tệp dữ liệu mới chưa có thông tin tài chính.

Dữ liệu đầu vào:

- Không yêu cầu tham số; cấu trúc mẫu do hệ thống tự thiết lập một lần khi khởi tạo lần đầu.

Quy tắc xử lý:

1. Thiết lập đúng 3 cấp theo quy tắc Phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`):
   - Cấp 1: Loại thu chi, gồm hai nhánh cố định Thu nhập (`INCOME`) và Chi tiêu (`EXPENSE`); không đổi tên, không xóa, không tạo thêm.
   - Cấp 2: Nhóm nhu cầu, do người dùng quản lý, trực thuộc một Loại thu chi.
   - Cấp 3: Danh mục chi tiết, trực thuộc một Nhóm nhu cầu và là cấp duy nhất liên kết với giao dịch.
2. Khi khởi tạo lần đầu, nạp bộ mẫu một lần duy nhất: mỗi Loại thu chi có ít nhất một nhóm mẫu, mỗi nhóm có ít nhất một danh mục mẫu. Không tự nạp lại khi khởi động lại ứng dụng.
3. Cấm ghi nhận giao dịch trực tiếp lên cấp 1 hoặc cấp 2.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2442}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7558}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Cấu trúc danh mục 3 cấp hoàn chỉnh trong tệp dữ liệu cục bộ. \\ \hline
Bất biến hệ thống &
Không đổi số dư ví; luôn có đúng 2 nhánh Loại thu chi cố định. \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.2990}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.5524}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-CAT-01.1} &
Khởi chạy lần đầu trên tệp dữ liệu mới &
Sinh đủ 2 nhánh \texttt{INCOME} và \texttt{EXPENSE}, mỗi nhánh có ít
nhất 1 nhóm mẫu, mỗi nhóm có ít nhất 1 danh mục mẫu. \\ \hline
\texttt{AC-CAT-01.2} &
Tạo giao dịch và chọn cấp phân loại &
Bắt buộc chọn danh mục chi tiết cấp 3; từ chối liên kết trực tiếp với
cấp 1 hoặc cấp 2. \\ \hline
\texttt{AC-CAT-01.3} &
Đã xóa một nhóm mẫu rồi khởi động lại hệ thống &
Bộ mẫu không nạp lại, nhóm đã xóa giữ nguyên trạng thái đã xóa. \\ \hline
\end{longtable}
\endgroup
```

#### 4.4.2 FR-CAT-02: Tùy biến danh mục người dùng

Bảng thuộc tính của yêu cầu chức năng Tùy biến danh mục người dùng (FR-CAT-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2500}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7500}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}) \\ \hline
Ca sử dụng liên quan &
Thêm và sửa danh mục (\texttt{UC-CAT-02}), Xóa và khôi phục danh mục
(\texttt{UC-CAT-03}) \\ \hline
Quy tắc áp dụng &
Quy tắc tên (\texttt{BR-NAM-01}), Phân cấp danh mục thu chi 3 cấp
(\texttt{BR-FIN-04}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Cấu trúc danh mục đã sẵn sàng hoạt động.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1769}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1376}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.5749}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1106}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Tên phân loại &
Chuỗi ký tự &
1 đến 50 ký tự sau khi cắt, không trùng mục đang dùng cùng cấp theo quy
tắc tên (\texttt{BR-NAM-01}) &
Bắt buộc \\ \hline
Vị trí trực thuộc &
Chuỗi ký tự &
Mã nhóm cha đang dùng (khi tạo danh mục), hoặc Loại thu chi
\texttt{INCOME}/\texttt{EXPENSE} (khi tạo nhóm) &
Bắt buộc \\ \hline
Biểu tượng nhận diện &
Chuỗi ký tự &
Tối đa 50 ký tự &
Tùy chọn \\ \hline
Mã màu hiển thị &
Chuỗi ký tự &
Mã Hex, tối đa 10 ký tự &
Tùy chọn \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Danh mục mới phải thuộc nhóm cha đang hoạt động; nhóm mới phải thuộc một Loại thu chi.
   - Nhóm cha đã xóa: từ chối, báo lỗi nhóm cha đã xóa khi tạo danh mục (`ERR-CAT-005`).
   - Vị trí trực thuộc không tồn tại: từ chối.
2. Kiểm tra tên theo quy tắc tên (`BR-NAM-01`):
   - Rỗng hoặc dài hơn 50 ký tự: từ chối, báo lỗi tên không hợp lệ (`ERR-NAM-001`).
   - Trùng mục đang dùng cùng cấp (nhóm cùng Loại thu chi, danh mục cùng nhóm cha): từ chối, báo lỗi tên bị trùng (`ERR-NAM-002`).
3. Tên trùng một mục đã xóa cùng cấp: cho người dùng chọn khôi phục mục cũ (giữ mã cũ, nối lại giao dịch quá khứ) hoặc tạo mục mới với mã định danh mới.
4. Đổi tên nhóm hoặc danh mục: kiểm tra theo quy tắc tên (`BR-NAM-01`), giữ nguyên mã định danh và liên kết giao dịch lịch sử. Hai nhánh Loại thu chi không có chức năng đổi tên hay xóa.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2877}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7123}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Bản ghi nhóm hoặc danh mục mới, hoặc bản ghi đã đổi tên. \\ \hline
Bất biến hệ thống &
Không đổi số dư ví và giao dịch lịch sử. \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4440}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4073}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-CAT-02.1} &
Thêm danh mục ``Thú cưng'' vào nhóm ``Nhu cầu cá nhân và giải trí'' với
tên hợp lệ &
Tạo danh mục \texttt{ACTIVE}, sẵn sàng phân loại giao dịch chi tiêu mới. \\ \hline
\texttt{AC-CAT-02.2} &
Tạo nhóm hoặc danh mục có tên trống hoặc dài quá 50 ký tự &
Từ chối, báo lỗi tên không hợp lệ (\texttt{ERR-NAM-001}). \\ \hline
\texttt{AC-CAT-02.3} &
Tạo nhóm hoặc danh mục trùng tên mục đang hoạt động cùng cấp &
Từ chối, báo lỗi tên bị trùng (\texttt{ERR-NAM-002}). \\ \hline
\texttt{AC-CAT-02.4} &
Tạo danh mục chi tiết vào nhóm cha đã xóa &
Từ chối, báo lỗi nhóm cha đã xóa khi tạo danh mục
(\texttt{ERR-CAT-005}). \\ \hline
\end{longtable}
\endgroup
```

#### 4.4.3 FR-CAT-03: Kiểm soát toàn vẹn khi cập nhật và xóa danh mục

Bảng thuộc tính của yêu cầu chức năng Kiểm soát toàn vẹn khi cập nhật và xóa danh mục (FR-CAT-03), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2351}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7649}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}) \\ \hline
Ca sử dụng liên quan &
Thêm và sửa danh mục (\texttt{UC-CAT-02}), Xóa và khôi phục danh mục
(\texttt{UC-CAT-03}), Quản lý danh mục (\texttt{UC-CAT-04}) \\ \hline
Quy tắc áp dụng &
Quy tắc tên (\texttt{BR-NAM-01}), Phân cấp danh mục thu chi 3 cấp
(\texttt{BR-FIN-04}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Nhóm nhu cầu hoặc danh mục chi tiết cần xử lý đang tồn tại.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2013}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1462}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4809}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1716}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Loại phân loại đích &
Mã phân loại &
Nhóm nhu cầu hoặc danh mục chi tiết &
Bắt buộc \\ \hline
Mã định danh mục tiêu &
Chuỗi ký tự &
Mã đang tồn tại &
Bắt buộc \\ \hline
Thao tác yêu cầu &
Mã phân loại &
Đổi tên, chuyển nhóm cha (chỉ danh mục), xóa, khôi phục &
Bắt buộc \\ \hline
Tên mới &
Chuỗi ký tự &
1 đến 50 ký tự sau khi cắt theo quy tắc tên (\texttt{BR-NAM-01}) &
Bắt buộc khi đổi tên \\ \hline
Mã định danh nhóm cha mới &
Chuỗi ký tự &
Nhóm đang hoạt động, cùng Loại thu chi với nhóm cũ &
Bắt buộc khi chuyển nhóm \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Không tìm thấy mục tiêu: từ chối, báo lỗi không tìm thấy phân loại.
2. Xóa: quét sổ giao dịch.
   - Chưa có giao dịch: xóa vĩnh viễn.
   - Đã có giao dịch: chuyển sang trạng thái đã xóa, ẩn khỏi danh sách chọn cho giao dịch mới nhưng giữ nguyên trong giao dịch và báo cáo lịch sử kèm nhãn "đã xóa".
   - Khi xóa nhóm, áp dụng quy tắc trên cho từng danh mục con: nhóm có danh mục con có giao dịch thì chuyển sang đã xóa, ngược lại xóa vĩnh viễn.
   - Không được xóa nhóm đang dùng cuối cùng của một Loại thu chi: từ chối, báo lỗi không được xóa nhóm đang dùng cuối cùng (`ERR-CAT-002`).
3. Chuyển nhóm cha: chỉ sang nhóm đang hoạt động cùng Loại thu chi, theo quy tắc Phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`).
   - Khác Loại thu chi: từ chối, báo lỗi chuyển sang nhóm khác Loại thu chi (`ERR-CAT-001`).
   - Nhóm đích đã xóa: từ chối, báo lỗi nhóm đích đã xóa (`ERR-CAT-003`).
   - Trùng tên trong nhóm đích: từ chối, báo lỗi tên bị trùng (`ERR-NAM-002`).
4. Khôi phục: giữ nguyên mã định danh và nối lại giao dịch cũ.
   - Cùng cấp đã có mục đang hoạt động trùng tên: từ chối, báo lỗi tên bị trùng (`ERR-NAM-002`).
   - Danh mục có nhóm cha đã xóa: từ chối, báo lỗi nhóm cha đã xóa khi khôi phục (`ERR-CAT-004`), yêu cầu khôi phục nhóm cha trước.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1826}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8174}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Nhóm hoặc danh mục được cập nhật (đổi tên, chuyển nhóm, đã xóa, khôi
phục hoặc xóa vĩnh viễn). \\ \hline
Bất biến hệ thống &
Không đổi số dư ví; giao dịch lịch sử giữ nguyên liên kết và giá trị
theo quy tắc Phân cấp danh mục thu chi 3 cấp (\texttt{BR-FIN-04}). \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4103}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4410}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-CAT-03.1} &
Xóa danh mục ``Ăn uống'' có 50 giao dịch; xóa danh mục ``Học vẽ'' mới
tạo chưa có giao dịch &
``Ăn uống'' chuyển sang đã xóa và giữ nguyên 50 giao dịch; ``Học vẽ'' bị
xóa vĩnh viễn. \\ \hline
\texttt{AC-CAT-03.2} &
Chuyển danh mục chi tiêu ``Xăng xe'' sang nhóm thu nhập &
Từ chối, báo lỗi chuyển sang nhóm khác Loại thu chi
(\texttt{ERR-CAT-001}). \\ \hline
\texttt{AC-CAT-03.3} &
Chuyển danh mục chi tiêu ``Xăng xe'' sang một nhóm chi tiêu đã xóa &
Từ chối, báo lỗi nhóm đích đã xóa (\texttt{ERR-CAT-003}). \\ \hline
\texttt{AC-CAT-03.4} &
Nhánh Chi tiêu chỉ còn 1 nhóm đang dùng và xóa nhóm đó &
Từ chối, báo lỗi không được xóa nhóm đang dùng cuối cùng
(\texttt{ERR-CAT-002}). \\ \hline
\texttt{AC-CAT-03.5} &
Khôi phục danh mục khi nhóm cha đang bị xóa &
Từ chối, báo lỗi nhóm cha đã xóa khi khôi phục (\texttt{ERR-CAT-004}). \\ \hline
\end{longtable}
\endgroup
```

### 4.5 FR-REP: Nhóm chức năng Thống kê và báo cáo

Tổng hợp tự động dữ liệu giao dịch và số dư ví thành chuỗi dòng tiền 12 tháng gần nhất và cơ cấu thu chi, theo bốn quy tắc nghiệp vụ sau:

- Độ chính xác tài chính (`BR-FIN-01`).
- Bảo toàn số dư ví (`BR-FIN-02`).
- Ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`).
- Phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`).

#### 4.5.1 FR-REP-01: Thống kê dòng tiền 12 tháng gần nhất

Bảng thuộc tính của yêu cầu chức năng Thống kê dòng tiền 12 tháng gần nhất (FR-REP-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2941}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7059}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}), Mục tiêu bảo đảm độ chính xác số học tài chính
(\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Xem thống kê dòng tiền (\texttt{UC-REP-01}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1701}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2165}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4639}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1495}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Tháng kết thúc &
Số nguyên dương &
Từ 1 đến 12; mặc định tháng hiện tại &
Tùy chọn \\ \hline
Năm kết thúc &
Số nguyên dương &
Từ 2000 trở đi; mặc định năm hiện tại &
Tùy chọn \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Khung thống kê gồm đúng 12 tháng liên tục, kết thúc tại tháng và năm đã chọn, bắt đầu từ 11 tháng trước đó.
2. Với mỗi tháng, tính theo quy tắc Độ chính xác tài chính (`BR-FIN-01`):
   - Tổng thu nhập tháng.
   - Tổng chi tiêu tháng, gồm phí giao dịch liên kết đang tồn tại.
   - Dòng tiền thuần tháng bằng tổng thu nhập trừ tổng chi tiêu.
   - Tỷ lệ tiết kiệm bằng dòng tiền thuần chia tổng thu nhập nhân 100 khi tổng thu nhập lớn hơn 0; ngược lại bằng 0.00% để tránh lỗi chia cho 0 (`ERR-FIN-003`).
3. Chỉ số bình quân cả chuỗi: thu nhập, chi tiêu và dòng tiền thuần bình quân bằng tổng 12 tháng chia 12. Mẫu số luôn là 12, tháng chưa có giao dịch tính 0.00 VNĐ.
4. Làm tròn ngân hàng, hiển thị 2 chữ số thập phân theo quy tắc Độ chính xác tài chính (`BR-FIN-01`).

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2033}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7967}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Bảng chuỗi 12 tháng (thu, chi, dòng tiền thuần, tỷ lệ tiết kiệm) kèm các
chỉ số bình quân. \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3593}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4921}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-REP-01.1} &
Chọn mốc kết thúc tháng 09/2026 &
Xuất đủ 12 tháng từ 10/2025 đến 09/2026. \\ \hline
\texttt{AC-REP-01.2} &
Một tháng thu 30,000,000.00 VNĐ, chi 18,000,000.00 VNĐ &
Dòng tiền thuần +12,000,000.00 VNĐ; tỷ lệ tiết kiệm 40.00\%. \\ \hline
\texttt{AC-REP-01.3} &
Một tháng không có thu nhập, chi 5,000,000.00 VNĐ &
Dòng tiền thuần -5,000,000.00 VNĐ, tỷ lệ tiết kiệm 0.00\%, tránh lỗi
chia cho 0 (\texttt{ERR-FIN-003}). \\ \hline
\texttt{AC-REP-01.4} &
Chuỗi có tháng chưa phát sinh giao dịch &
Tháng không giao dịch tính 0.00 VNĐ vào bình quân 12 tháng. \\ \hline
\end{longtable}
\endgroup
```

#### 4.5.2 FR-REP-02: Phân tích cơ cấu thu chi theo nhóm và danh mục

Bảng thuộc tính của yêu cầu chức năng Phân tích cơ cấu thu chi theo nhóm và danh mục (FR-REP-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2451}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7549}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}), Mục tiêu bảo đảm độ chính xác số học tài chính
(\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Xem phân tích cơ cấu thu chi (\texttt{UC-REP-02}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Phân cấp danh mục thu chi 3
cấp (\texttt{BR-FIN-04}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2500}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2217}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.3915}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1368}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Khoảng ngày phân tích &
Ngày tháng (\texttt{YYYY-MM-DD}) &
Ngày bắt đầu không sau ngày kết thúc &
Bắt buộc \\ \hline
Loại luồng tiền phân tích &
Mã phân loại &
\texttt{EXPENSE} (mặc định) hoặc \texttt{INCOME} &
Bắt buộc \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Ngày bắt đầu sau ngày kết thúc: từ chối, báo lỗi khoảng ngày không hợp lệ.
2. Gom nhóm các giao dịch đang tồn tại theo quy tắc Phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): theo Nhóm nhu cầu cấp 2 (theo nhóm cha hiện tại của danh mục chi tiết) và theo Danh mục chi tiết cấp 3.
3. Tỷ lệ cơ cấu bằng tổng tiền của mục chia tổng tiền của loại luồng tiền nhân 100, làm tròn ngân hàng, 2 chữ số thập phân theo quy tắc Độ chính xác tài chính (`BR-FIN-01`). Tổng bằng 0.00 VNĐ: tỷ lệ 0.00%, tránh lỗi chia cho 0 (`ERR-FIN-003`).
4. Sắp xếp theo số tiền giảm dần.
5. Khi so sánh với kỳ liền kề cùng độ dài: tính chênh lệch giá trị và tỷ lệ tăng giảm; kỳ trước bằng 0.00 VNĐ thì hiển thị "Mới".
6. Nhóm hoặc danh mục đã xóa nhưng có giao dịch trong kỳ: hiển thị riêng kèm nhãn "đã xóa", không gộp vào mục khác, để tổng cơ cấu khớp tổng giao dịch thực tế.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2033}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7967}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Bảng cơ cấu 2 cấp (Nhóm nhu cầu, Danh mục chi tiết) gồm tên, tổng tiền,
tỷ lệ và biến động so với kỳ trước. \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3446}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.5067}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-REP-02.1} &
Tổng chi 20,000,000.00 VNĐ, nhóm ``Nhu cầu thiết yếu'' 8,000,000.00 VNĐ &
Tỷ lệ nhóm là 40.00\% theo quy tắc Độ chính xác tài chính
(\texttt{BR-FIN-01}). \\ \hline
\texttt{AC-REP-02.2} &
Khoảng thời gian không có giao dịch &
Tổng 0.00 VNĐ, danh sách rỗng, không phát sinh lỗi chia cho 0
(\texttt{ERR-FIN-003}). \\ \hline
\texttt{AC-REP-02.3} &
Kỳ có giao dịch thuộc nhóm hoặc danh mục đã xóa &
Vẫn hiển thị mục đó kèm nhãn ``đã xóa'' với đúng số tiền và tỷ trọng;
tổng báo cáo khớp tổng dòng tiền thực tế. \\ \hline
\end{longtable}
\endgroup
```

### 4.6 FR-DAT: Nhóm chức năng Lưu trữ và sao lưu dữ liệu

Bảo đảm tệp dữ liệu hoạt động độc lập, không mất mát; phục hồi theo quy trình kiểm tra 4 bước của yêu cầu chức năng Phục hồi dữ liệu hệ thống từ bản sao lưu (`FR-DAT-02`) trên máy tính cục bộ.

#### 4.6.1 FR-DAT-01: Xuất dữ liệu tài chính

Bảng thuộc tính của yêu cầu chức năng Xuất dữ liệu tài chính (FR-DAT-01), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.3440}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.6560}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu bảo đảm độ chính xác số học tài chính (\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Xuất dữ liệu tài chính (\texttt{UC-DAT-01}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2183}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1655}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4859}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1303}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Phạm vi xuất dữ liệu &
Mã phân loại &
\texttt{FULL} (toàn bộ hệ thống) hoặc \texttt{TRANSACTIONS} (sổ thu chi) &
Bắt buộc \\ \hline
Định dạng tệp xuất &
Mã phân loại &
\texttt{JSON} cho cả hai phạm vi &
Bắt buộc \\ \hline
Đường dẫn lưu trữ &
Chuỗi ký tự &
Thư mục đích hợp lệ trên đĩa cục bộ &
Bắt buộc \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Tạo ảnh chụp dữ liệu nhất quán tại một mốc thời gian cho ví, danh mục, giao dịch thu chi, chuyển tiền nội bộ và điều chỉnh số dư.
2. Nội dung JSON:
   - `FULL`: đủ 5 nhóm thực thể gồm ví; danh mục (cả nhóm và danh mục đã xóa, kèm ngày tạo); giao dịch thu chi (gồm phí liên kết); chuyển tiền nội bộ; điều chỉnh số dư.
   - `TRANSACTIONS`: mọi giao dịch thu chi và phí liên kết đang tồn tại, kèm mã giao dịch phát sinh phí.
   - Mọi trường số tiền là chuỗi số học chính xác, cố định 2 chữ số thập phân, không làm tròn hay dùng dấu chấm động, theo quy tắc Độ chính xác tài chính (`BR-FIN-01`).
3. Đặt tên tệp `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json`, mã hóa UTF-8 không BOM, ghi tệp an toàn để không để lại tệp hỏng khi bị ngắt quãng.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.3289}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.6711}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Tệp JSON hoàn chỉnh tạo theo tên tự động trong thư mục đích chỉ định. \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3300}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.5214}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-DAT-01.1} &
Xuất toàn bộ hệ thống sang JSON &
Tệp JSON hợp lệ, đủ 5 nhóm thực thể, UTF-8, thời điểm đồng bộ nhất quán. \\ \hline
\texttt{AC-DAT-01.2} &
Kiểm tra nội dung tệp đã xuất &
Mọi trường số tiền là chuỗi số cố định 2 chữ số thập phân, không dùng số
thực dấu chấm động theo quy tắc Độ chính xác tài chính
(\texttt{BR-FIN-01}). \\ \hline
\texttt{AC-DAT-01.3} &
Xuất sổ thu chi khi có chuyển tiền đang tồn tại phát sinh phí và một
chuyển tiền khác đã xóa &
Tệp chứa phí của chuyển tiền đang tồn tại kèm mã liên kết, không chứa
phí của chuyển tiền đã xóa. \\ \hline
\end{longtable}
\endgroup
```

#### 4.6.2 FR-DAT-02: Phục hồi dữ liệu hệ thống từ bản sao lưu

Bảng thuộc tính của yêu cầu chức năng Phục hồi dữ liệu hệ thống từ bản sao lưu (FR-DAT-02), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2344}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7656}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}), Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví
(\texttt{G-02}), Mục tiêu bảo đảm độ chính xác số học tài chính
(\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Phục hồi dữ liệu từ bản sao lưu (\texttt{UC-DAT-02}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Bảo toàn số dư ví
(\texttt{BR-FIN-02}), Ghi nhận đồng thời khi chuyển tiền nội bộ
(\texttt{BR-FIN-03}), Phân cấp danh mục thu chi 3 cấp
(\texttt{BR-FIN-04}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Người dùng cung cấp tệp sao lưu JSON hợp lệ và xác nhận thay thế toàn bộ dữ liệu hiện tại.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2059}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1691}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4706}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1544}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Đường dẫn tệp sao lưu &
Chuỗi ký tự &
Trỏ tới tệp sao lưu JSON hợp lệ trên đĩa cục bộ &
Bắt buộc \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Phân tích cú pháp JSON. Tệp hỏng, sai cấu trúc hoặc không UTF-8: từ chối, báo lỗi định dạng tệp không hợp lệ.
2. Kiểm tra toàn vẹn 4 bước trước khi ghi:
   - Bước 1, theo quy tắc Độ chính xác tài chính (`BR-FIN-01`): đủ các trường bắt buộc, ngày đúng dạng `YYYY-MM-DD`, chuỗi số tiền đúng định dạng chính xác; sai thì báo lỗi sai định dạng số (`ERR-FIN-001`).
   - Bước 2, theo quy tắc Phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): mọi mã danh mục trong giao dịch trỏ tới danh mục có trong tệp với Loại thu chi tương thích.
   - Bước 3, theo quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): mọi giao dịch thu chi và chuyển tiền trỏ tới ví có trong danh sách ví; mỗi bản ghi phí chuyển tiền liên kết tới một chuyển tiền có trong tệp.
   - Bước 4, theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`): tính lại số dư mọi ví, không ví nào âm.
3. Phát hiện sai lệch ở bất kỳ bước nào: hủy toàn bộ phục hồi, giữ nguyên 100% dữ liệu hiện tại và báo danh sách lỗi cụ thể.
4. Cả 4 bước đạt: tạo bản sao lưu tự động `backup_YYYYMMDD_HHMMSS.json` trong thư mục `backups/` (`CON-06`), sau đó thay toàn bộ dữ liệu hiện tại bằng dữ liệu sao lưu, tính lại số dư ví và cập nhật chuỗi thống kê 12 tháng.

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.1826}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.8174}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Toàn bộ dữ liệu được phục hồi đồng bộ: ví, danh mục, giao dịch thu chi,
chuyển tiền nội bộ, điều chỉnh số dư. \\ \hline
Bất biến hệ thống &
Dữ liệu chuyển hoàn toàn sang trạng thái bản sao lưu, hoặc giữ nguyên
trạng cũ nếu có lỗi toàn vẹn. \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3838}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4676}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-DAT-02.1} &
Phục hồi từ tệp JSON hợp lệ, đạt cả 4 bước &
Dữ liệu và số dư ví phục hồi khớp thời điểm sao lưu; dữ liệu cũ bị thay
thế trọn vẹn. \\ \hline
\texttt{AC-DAT-02.2} &
Tệp có giao dịch trỏ tới danh mục không tồn tại, hoặc phí chuyển tiền
trỏ tới mã chuyển tiền không có trong tệp &
Từ chối, báo lỗi liên kết không hợp lệ, giữ nguyên dữ liệu hiện tại. \\ \hline
\texttt{AC-DAT-02.3} &
Tệp có chuỗi giao dịch làm một ví âm số dư khi tính lại &
Từ chối theo quy tắc Bảo toàn số dư ví (\texttt{BR-FIN-02}), dữ liệu
hiện tại được bảo toàn. \\ \hline
\end{longtable}
\endgroup
```

#### 4.6.3 FR-DAT-03: Kiểm tra lại dữ liệu hệ thống

Bảng thuộc tính của yêu cầu chức năng Kiểm tra lại dữ liệu hệ thống (FR-DAT-03), gồm mục tiêu đáp ứng, ca sử dụng liên quan, quy tắc áp dụng và mức độ ưu tiên

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2344}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7656}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính &
Giá trị \\ \hline
\endhead
Mục tiêu đáp ứng &
Mục tiêu ghi nhận dòng tiền và cấu trúc danh mục phân cấp
(\texttt{G-01}), Mục tiêu xác định tổng số dư và cơ cấu phân bổ ví
(\texttt{G-02}), Mục tiêu bảo đảm độ chính xác số học tài chính
(\texttt{G-03}) \\ \hline
Ca sử dụng liên quan &
Kiểm tra tính nhất quán dữ liệu (\texttt{UC-DAT-03}) \\ \hline
Quy tắc áp dụng &
Độ chính xác tài chính (\texttt{BR-FIN-01}), Bảo toàn số dư ví
(\texttt{BR-FIN-02}), Ghi nhận đồng thời khi chuyển tiền nội bộ
(\texttt{BR-FIN-03}), Phân cấp danh mục thu chi 3 cấp
(\texttt{BR-FIN-04}) \\ \hline
Mức độ ưu tiên &
Bắt buộc (\texttt{MUST}) \\ \hline
\end{longtable}
\endgroup
```

Điều kiện tiền đề:

- Hệ thống đã khởi tạo xong và đang vận hành với tệp dữ liệu cục bộ.

Dữ liệu đầu vào:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1842}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1579}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.5197}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1382}}|}
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tham số &
Kiểu dữ liệu &
Ràng buộc giá trị &
Tính bắt buộc \\ \hline
\endhead
Phương thức kích hoạt &
Mã phân loại &
Thủ công do người dùng yêu cầu, hoặc tự động theo lịch định kỳ &
Bắt buộc \\ \hline
\end{longtable}
\endgroup
```

Quy tắc xử lý:

1. Đối soát độc lập 4 điều kiện toàn vẹn:
   - Điều kiện 1, theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`): tính lại số dư từ toàn bộ lịch sử dòng tiền và so với số dư ghi nhận của từng ví; sai số cho phép bằng đúng 0.00 VNĐ theo quy tắc Độ chính xác tài chính (`BR-FIN-01`).
   - Điều kiện 2, theo quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): với mọi chuyển tiền, số tiền trừ ở ví nguồn bằng đúng số tiền cộng ở ví đích (không tính chi phí).
   - Điều kiện 3, theo quy tắc Phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): mọi giao dịch gắn với danh mục hợp lệ (danh mục đã xóa vẫn hợp lệ trong lịch sử), không sai lệch Loại thu chi.
   - Điều kiện 4: mọi phí giao dịch phát sinh từ chuyển tiền trỏ tới một chuyển tiền đang tồn tại.
2. Phân loại kết quả theo mức nghiêm trọng: thông tin, cảnh báo, lỗi nghiêm trọng.
3. Mỗi sai lệch kèm đề xuất hành động cụ thể (ví dụ tính lại số dư tự động, hoặc phục hồi từ bản sao lưu gần nhất).

Dữ liệu đầu ra và bất biến hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2252}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7748}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Nội dung \\ \hline
\endhead
Bản ghi dữ liệu &
Báo cáo trạng thái từng điều kiện (Đạt hoặc Không đạt) kèm số bản ghi đã
quét. \\ \hline
\end{longtable}
\endgroup
```

Tiêu chí nghiệm thu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3157}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.5355}}|}
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã tiêu chí &
Điều kiện kích hoạt &
Kết quả kỳ vọng \\ \hline
\endhead
\texttt{AC-DAT-03.1} &
Dữ liệu hoàn toàn nhất quán &
Báo cáo 100\% Đạt cho cả 4 điều kiện: số dư ví, cân bằng chuyển tiền,
liên kết danh mục, liên kết phí. \\ \hline
\texttt{AC-DAT-03.2} &
Số dư ghi nhận của một ví lệch so với dòng tiền tính lại &
Phát hiện đúng ví lệch theo quy tắc Bảo toàn số dư ví
(\texttt{BR-FIN-02}), trạng thái Không đạt, đề xuất tính lại số dư. \\ \hline
\texttt{AC-DAT-03.3} &
Có chuyển tiền mất cân bằng nguồn đích hoặc phí chuyển tiền mồ côi &
Phát hiện đúng theo quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ
(\texttt{BR-FIN-03}), trạng thái Không đạt kèm mã giao dịch vi phạm. \\ \hline
\end{longtable}
\endgroup
```
\newpage

## 5. YÊU CẦU PHI CHỨC NĂNG

Các yêu cầu phi chức năng của Hệ thống Quản lý Tài chính Cá nhân (PFMS) được xây dựng dựa trên các nhóm đặc tính chất lượng phần mềm phổ biến. Các yêu cầu này đóng vai trò là ràng buộc vận hành và kiến trúc then chốt, bảo đảm hệ thống đạt độ chính xác tài chính tuyệt đối, vận hành ổn định, lưu trữ an toàn, thuận tiện khi sử dụng, tương thích với môi trường triển khai và dễ bảo trì, mở rộng theo nguyên lý thiết kế hướng đối tượng.

Mỗi yêu cầu phi chức năng được đặc tả nhất quán theo khuôn mẫu:

- Đặc tính chất lượng: xác định yêu cầu thuộc nhóm đặc tính chất lượng phần mềm nào, như độ chính xác, độ tin cậy, bảo mật, khả năng sử dụng, tính tương thích hoặc khả năng bảo trì.
- Mô tả yêu cầu: xác định phạm vi ràng buộc vận hành và giải pháp kiến trúc ở mức bài toán, làm rõ hệ thống phải đạt được điều gì mà chưa đi vào chi tiết cài đặt.
- Chỉ số đo và ngưỡng nghiệm thu: định lượng các tiêu chí đo kiểm và ngưỡng chấp thuận để có thể đánh giá yêu cầu đã được đáp ứng hay chưa.
- Phương pháp kiểm chứng: cơ chế xác minh yêu cầu, gồm kiểm thử tự động (`T`), trình diễn thao tác (`D`), soát xét kỹ thuật (`I`) và phân tích số liệu (`A`).

Bảng tổng hợp danh mục các yêu cầu phi chức năng của hệ thống theo từng đặc tính chất lượng, kèm phương pháp kiểm chứng tương ứng và chỉ số trọng tâm:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\centering\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.0887}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1595}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.2758}}|>{\centering\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.0887}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.3873}}|}
\hline
\rowcolor[gray]{0.9}
Mã yêu cầu &
Tên yêu cầu &
Đặc tính chất lượng &
Phương pháp chính &
Chỉ số trọng tâm \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã yêu cầu &
Tên yêu cầu &
Đặc tính chất lượng &
Phương pháp chính &
Chỉ số trọng tâm \\ \hline
\endhead
\texttt{NFR-01} &
Độ chính xác và toàn vẹn dữ liệu tài chính &
Mức độ phù hợp chức năng / Tính chính xác chức năng &
T, I &
Sai số tích lũy 0.00 VNĐ; 0\% trường dùng số thực nhị phân
(\texttt{float}/\texttt{double}); 0\% vi phạm quy tắc bảo toàn số dư. \\ \hline
\texttt{NFR-02} &
Hiệu năng và độ trễ phản hồi &
Hiệu quả vận hành / Hành vi thời gian, Mức sử dụng tài nguyên &
D, A &
Phản hồi dưới 1.0 giây với thao tác thông thường; không rò rỉ bộ nhớ khi
sử dụng liên tục. \\ \hline
\texttt{NFR-03} &
Độ tin cậy và lưu trữ an toàn &
Độ tin cậy / Khả năng chịu lỗi, Khả năng phục hồi &
T, I &
Ghi tệp nguyên tử an toàn; tự động sao lưu trước tác vụ thay thế dữ liệu
quy mô lớn; không mất dữ liệu khi ngắt nguồn. \\ \hline
\texttt{NFR-04} &
Khả năng bảo trì và kiến trúc hướng đối tượng &
Khả năng bảo trì / Tính mô-đun hóa, Tính tái sử dụng, Tính phân tích
được, Tính kiểm thử được &
T, I &
Không phụ thuộc vòng; trạng thái nội bộ được bảo vệ; 100\% quy tắc
nghiệp vụ có kiểm thử đơn vị. \\ \hline
\texttt{NFR-05} &
Tính khả dụng và trải nghiệm người dùng &
Khả năng tương tác / Tính dễ học, Tính dễ vận hành, Bảo vệ người dùng
khỏi lỗi &
D, I &
Xác nhận thao tác rủi ro cao; 100\% thông báo lỗi bằng tiếng Việt có
hướng dẫn khắc phục cụ thể. \\ \hline
\texttt{NFR-06} &
Tính tương thích và môi trường chạy &
Tính linh hoạt / Tính thích ứng, Tính dễ cài đặt &
T, I &
Thực thi trên JVM 17 LTS, độc lập hệ điều hành; tệp tin định dạng UTF-8
không BOM; không dùng thư viện nhị phân phụ thuộc nền tảng. \\ \hline
\end{longtable}
\endgroup
```

### 5.1 NFR-01: Độ chính xác và toàn vẹn dữ liệu tài chính

Đặc tính chất lượng: Mức độ phù hợp chức năng, đặc tính con Tính chính xác chức năng.

Mô tả yêu cầu:

1. Kiểu dữ liệu số học:
   - Dùng kiểu số thập phân chính xác cố định cho mọi trường số, biến trung gian, phép tính tiền tệ, số dư ví và tổng số dư, với tối thiểu 18 chữ số phần nguyên và 2 chữ số thập phân.
   - Cấm sử dụng kiểu số thực nhị phân (`float`, `double`) trong cấu trúc dữ liệu nghiệp vụ, thuật toán tính toán và lưu trữ. Giá trị số chỉ được khởi tạo từ dữ liệu nhập dạng thập phân chính xác hoặc từ số nguyên, tuyệt đối không chuyển đổi trung gian qua `float` hoặc `double`.
2. Độ chính xác thập phân và làm tròn:
   - Tiền VNĐ hiển thị 0 chữ số thập phân (số nguyên đồng) trên giao diện người dùng; tính toán, xử lý nghiệp vụ và kiểm thử dùng 2 chữ số thập phân.
   - Tỷ lệ phần trăm trung gian dùng tối thiểu 4 chữ số thập phân theo quy tắc Độ chính xác tài chính (`BR-FIN-01`).
   - Mọi phép chia phải chỉ định rõ số chữ số thập phân và chế độ làm tròn ngân hàng (làm tròn đến số chẵn gần nhất khi giá trị nằm giữa hai mức).
   - So sánh số theo giá trị thực, bỏ qua khác biệt do cách hiển thị số chữ số thập phân.
3. Sai số tích lũy: sai số của mọi chuỗi phép tính, trong từng giao dịch và trên toàn bộ dữ liệu tổng hợp, bảo đảm bằng 0.00 VNĐ.
4. Bảo toàn số dư theo quy tắc Bảo toàn số dư ví (`BR-FIN-02`): tại mọi thời điểm, số dư khả dụng của ví $w$ tại thời điểm quan sát $T$ thỏa mãn công thức:

   $$\text{Số dư ví } w \text{ tại } T = (\text{Thu} + \text{Chuyển vào} + \text{Điều chỉnh tăng}) - (\text{Chi} + \text{Chuyển ra} + \text{Điều chỉnh giảm})$$

   Mỗi hạng tử là tổng các khoản của ví $w$ đến thời điểm $T$. Điều chỉnh tăng và điều chỉnh giảm lần lượt là các khoản điều chỉnh số dư dương và âm theo yêu cầu Điều chỉnh số dư ví (`FR-WAL-05`).
5. Cân bằng nguồn - đích theo quy tắc Ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): mọi lần chuyển tiền nội bộ phải có số tiền trừ ở ví nguồn bằng số tiền cộng ở ví đích (chưa tính phí). Tổng tiền trong hệ thống giữ nguyên, trừ chi phí chuyển tiền phát sinh nếu có.

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với độ chính xác và tính toàn vẹn dữ liệu tài chính:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4009}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4510}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1480}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endhead
Sai số tích lũy số học &
Chênh lệch tuyệt đối giữa tổng tiền kỳ vọng và thực tế &
0.00 VNĐ \\ \hline
Kiểu số thực nhị phân trong mô hình nghiệp vụ &
Số trường dùng \texttt{float}/\texttt{double} chia tổng số trường số &
0.00\% \\ \hline
Vi phạm quy tắc Bảo toàn số dư ví (\texttt{BR-FIN-02}) &
Số giao dịch vi phạm chia tổng số giao dịch &
0.00\% trên toàn bộ tập kiểm thử \\ \hline
Sai lệch nguồn - đích khi chuyển tiền theo quy tắc Ghi nhận đồng thời
khi chuyển tiền nội bộ (\texttt{BR-FIN-03}), chưa tính phí &
Chênh lệch tuyệt đối giữa tiền trừ ví nguồn và tiền cộng ví đích &
0.00 VNĐ \\ \hline
Số chữ số thập phân của tiền VNĐ &
Số chữ số thập phân khi hiển thị, khi tính toán và kiểm thử &
0 (giao diện) / 2 (tính toán, kiểm thử) \\ \hline
Số chữ số thập phân của tỷ lệ phần trăm &
Số chữ số thập phân trong phép tính tỷ lệ &
Từ 4 trở lên \\ \hline
Phép chia có chỉ định số chữ số thập phân và làm tròn &
Số phép chia thập phân chính xác đạt yêu cầu chia tổng số phép chia &
100.00\% \\ \hline
\end{longtable}
\endgroup
```

Bảng quy định phương pháp và quy trình kiểm chứng kỹ thuật cho yêu cầu `NFR-01`:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1837}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1484}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.6678}}|}
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endhead
Soát xét kỹ thuật (\texttt{I}) &
Đánh giá kiểm tra &
Rà soát mã nguồn phần nghiệp vụ để phát hiện khai báo
\texttt{float}/\texttt{double}, việc tạo số thập phân trực tiếp từ số
thực nhị phân và các phép chia thiếu số chữ số thập phân hoặc quy tắc
làm tròn. \\ \hline
Kiểm thử tự động (\texttt{T}) &
Kiểm thử tự động &
Chạy kiểm thử giá trị biên (số tiền 18 chữ số phần nguyên, chia tỷ lệ
phần trăm cho số dư ví lẻ) và kiểm thử độ bền với chuỗi giao dịch liên
tiếp để đối chiếu số dư tính toán với số dư thực tế. \\ \hline
\end{longtable}
\endgroup
```

### 5.2 NFR-02: Hiệu năng và độ trễ phản hồi

Đặc tính chất lượng: Hiệu quả vận hành, đặc tính con Hành vi thời gian và Mức sử dụng tài nguyên.

Mô tả yêu cầu: với quy mô dữ liệu cá nhân thông thường (tối thiểu 10,000 giao dịch), hệ thống đáp ứng tức thì và không gây gián đoạn trải nghiệm ở các thao tác:

1. Ghi nhận giao dịch (thu chi hoặc chuyển tiền): cập nhật bộ nhớ và đồng bộ số dư các thực thể liên quan ngay lập tức.
2. Truy vấn dữ liệu: lọc theo khoảng thời gian, danh mục hoặc ví, tìm kiếm từ khóa và phân trang dữ liệu.
3. Tổng hợp báo cáo: thống kê dòng tiền 12 tháng gần nhất và cơ cấu chi tiêu 2 cấp danh mục.
4. Khởi động ứng dụng: thời gian từ lúc kích hoạt ứng dụng đến khi màn hình làm việc chính sẵn sàng tương tác đủ ngắn, không gián đoạn công việc của người dùng.
5. Quản lý bộ nhớ: duy trì mức tiêu thụ bộ nhớ ổn định, không xảy ra rò rỉ bộ nhớ khi làm việc liên tục trong phiên làm việc kéo dài.

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với hiệu năng vận hành và độ trễ phản hồi:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.2720}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4400}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.2880}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endhead
Độ trễ ghi nhận giao dịch, truy vấn và tổng hợp báo cáo &
Thời gian xử lý từ lúc kích hoạt thao tác đến khi giao diện hoàn tất cập
nhật &
Dưới 1.0 giây trong điều kiện vận hành chuẩn \\ \hline
Thời gian khởi động ứng dụng &
Thời gian từ lúc chạy ứng dụng đến khi giao diện chính sẵn sàng tiếp
nhận thao tác &
Dưới 3.0 giây trên máy tính cá nhân tiêu chuẩn \\ \hline
Ổn định bộ nhớ khi sử dụng liên tục &
Xu hướng cấp phát bộ nhớ JVM theo thời gian làm việc &
Không phát hiện rò rỉ bộ nhớ tăng dần \\ \hline
\end{longtable}
\endgroup
```

Bảng quy định phương pháp và quy trình kiểm chứng hiệu năng cho yêu cầu `NFR-02`:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1938}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1522}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.6540}}|}
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endhead
Trình diễn thao tác (\texttt{D}) &
Quan sát trực tiếp &
Thực hiện các thao tác ghi sổ, truy vấn, lọc và tổng hợp báo cáo trên bộ
dữ liệu mẫu để xác nhận độ trễ đáp ứng của hệ thống. \\ \hline
Đo kiểm tài nguyên (\texttt{A}) &
Phân tích số liệu &
Theo dõi biểu đồ cấp phát bộ nhớ JVM trong phiên làm việc kéo dài để
phát hiện nguy cơ rò rỉ bộ nhớ. \\ \hline
\end{longtable}
\endgroup
```

### 5.3 NFR-03: Độ tin cậy và lưu trữ an toàn

Đặc tính chất lượng: Độ tin cậy, đặc tính con Khả năng chịu lỗi và Khả năng phục hồi.

Mô tả yêu cầu:

1. Ghi tệp an toàn: khi mất nguồn điện, hệ điều hành tắt đột ngột hoặc tiến trình ứng dụng bị buộc dừng, tệp dữ liệu chính chỉ tồn tại ở một trong hai trạng thái: bản cũ nguyên vẹn hoặc bản mới đầy đủ.
   - Không ghi dở: dữ liệu mới chỉ thay thế tệp chính thức khi toàn bộ nội dung đã ghi thành công và trọn vẹn xuống tệp tạm.
   - Lưu bền xuống đĩa: dữ liệu của lần ghi hoàn tất phải được đẩy trực tiếp xuống đĩa vật lý, không tồn đọng trong vùng đệm bộ nhớ.
   - Bảo toàn bản cũ: tự động giữ lại bản sao tệp dữ liệu gốc trước khi tiến hành thay thế nếu phát hiện rủi ro gián đoạn.
2. Tự động sao lưu: tạo bản sao lưu có gắn nhãn thời gian theo mẫu `backup_YYYYMMDD_HHMMSS.json` trong thư mục `backups/`, trước khi thực hiện các tác vụ thay thế dữ liệu quy mô lớn (phục hồi dữ liệu từ tệp nhập ngoài theo ca sử dụng Phục hồi dữ liệu từ bản sao lưu (`UC-DAT-02`), đặt lại hệ thống).
3. Kiểm tra toàn vẹn khi đọc: khi đọc từ tệp lưu trữ hoặc bản sao lưu, kiểm tra cấu trúc dữ liệu trước khi nạp vào bộ nhớ (đúng lược đồ JSON, mã liên kết danh mục hợp lệ, thỏa mãn quy tắc bảo toàn số dư). Nếu phát hiện sai lệch, hủy toàn bộ thao tác đọc, giữ nguyên trạng thái dữ liệu trước đó và hiển thị thông báo lỗi chi tiết.
4. Xử lý lỗi đọc ghi: kiểm soát và xử lý toàn bộ ngoại lệ vào/ra tệp tin. Khi ổ đĩa đầy hoặc thư mục bị khóa quyền ghi, giữ nguyên tệp dữ liệu gốc và hiển thị thông báo lỗi rõ ràng.

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với độ tin cậy và an toàn lưu trữ dữ liệu:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4225}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.4487}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1288}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endhead
Tệp chính bị hỏng khi chương trình bị ngắt đột ngột &
Số tệp hỏng chia tổng số lần ghi bị ngắt &
0.00\% \\ \hline
Giữ nguyên dữ liệu lịch sử đã lưu thành công &
Tỷ lệ bản ghi giao dịch còn nguyên vẹn sau sự cố ngắt nguồn &
100.00\% \\ \hline
Tác vụ thay thế dữ liệu quy mô lớn có sao lưu trước &
Số tác vụ có sao lưu trước chia tổng số tác vụ loại này &
100.00\% \\ \hline
Dữ liệu nguyên vẹn sau phục hồi từ bản sao lưu &
So khớp cấu trúc JSON và quy tắc bảo toàn số dư sau phục hồi &
Khớp 100.00\%, không sai lệch \\ \hline
\end{longtable}
\endgroup
```

Bảng quy định phương pháp và quy trình kiểm chứng an toàn lưu trữ cho yêu cầu `NFR-03`:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1951}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1463}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.6585}}|}
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endhead
Kiểm thử giả lập lỗi (\texttt{T}) &
Kiểm thử tự động &
Mô phỏng quá trình ghi tệp dữ liệu lớn rồi buộc ngắt tiến trình giữa
chừng; khởi động lại để xác minh tệp chính còn nguyên vẹn và đọc được
bình thường. \\ \hline
Soát xét kỹ thuật (\texttt{I}) &
Đánh giá kiểm tra &
Rà soát mã nguồn thành phần lưu trữ: tệp chính không bao giờ ở trạng
thái ghi dở, dữ liệu ghi xong được đẩy xuống đĩa vật lý và toàn bộ lỗi
vào/ra tệp được kiểm soát đầy đủ. \\ \hline
\end{longtable}
\endgroup
```

### 5.4 NFR-04: Khả năng bảo trì và kiến trúc hướng đối tượng

Đặc tính chất lượng: Khả năng bảo trì, đặc tính con Tính mô-đun hóa, Tính tái sử dụng, Tính phân tích được và Tính kiểm thử được.

Mô tả yêu cầu:

1. Phân định trách nhiệm theo thành phần độc lập:
   - Giao diện người dùng không truy cập trực tiếp dữ liệu lưu trữ mà bắt buộc tương tác thông qua thành phần xử lý nghiệp vụ.
   - Phụ thuộc một chiều: thành phần bên ngoài phụ thuộc thành phần bên trong; không xuất hiện phụ thuộc ngược chiều hoặc phụ thuộc vòng giữa các mô-đun.
   - Mỗi mô-đun chỉ đảm nhiệm một trách nhiệm duy nhất. Các thực thể nghiệp vụ (người dùng, ví, giao dịch thu chi, giao dịch chuyển tiền, danh mục) chỉ quản lý trạng thái và bất biến nghiệp vụ của riêng mình, tách biệt khỏi phần hiển thị và cách thức lưu trữ.
2. Bảo vệ trạng thái nội bộ:
   - Trạng thái nội bộ của thực thể chỉ được truy xuất qua giao diện đọc có kiểm soát hoặc thao tác nghiệp vụ chuyên biệt. Không cho phép gán trực tiếp số dư ví hoặc ghi đè trạng thái giao dịch ngoài quy trình ghi nhận dòng tiền.
   - Dữ liệu dạng danh sách hoặc tập hợp trả về từ thực thể không thể bị sửa đổi trực tiếp từ bên ngoài phạm vi kiểm soát nghiệp vụ (áp dụng cơ chế bao bọc bất biến).
3. Khả năng mở rộng không phá vỡ hành vi hiện có:
   - Mọi ví được xử lý đồng nhất: thuật toán tổng hợp số dư và tính toán tỷ lệ từng ví sử dụng chung logic nghiệp vụ.
   - Bổ sung danh mục mới không đòi hỏi chỉnh sửa logic tính số dư hiện có.
4. Kiểm thử tự động và quản lý độ phức tạp:
   - Toàn bộ quy tắc nghiệp vụ, thuật toán tính số dư và công thức tỷ lệ đều có kiểm thử đơn vị tự động độc lập công nghệ, bao phủ đầy đủ các trường hợp biên (số tiền âm, bằng 0, vượt hạn mức, tên rỗng, tên dài nhất, tên vượt độ dài tối đa).
   - Mỗi thao tác nghiệp vụ duy trì một luồng xử lý rõ ràng, tránh lồng ghép nhiều nhánh điều kiện phức tạp.

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với khả năng bảo trì và kiến trúc hướng đối tượng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3470}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.5160}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1370}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endhead
Phạm vi kiểm thử quy tắc nghiệp vụ &
Tỷ lệ quy tắc nghiệp vụ có ca kiểm thử tương ứng &
100.00\%, mỗi quy tắc có tối thiểu 1 ca \\ \hline
Kiểm thử trường hợp biên &
Tỷ lệ quy tắc có ràng buộc giá trị biên được bao phủ ca kiểm thử &
100.00\% \\ \hline
Phụ thuộc vòng giữa các mô-đun &
Số lượng vòng phụ thuộc phát hiện được &
0 \\ \hline
Bảo vệ trạng thái nội bộ thực thể khỏi ghi đè &
Tỷ lệ thực thể ngăn chặn gán đè trực tiếp trạng thái từ bên ngoài &
100.00\% \\ \hline
Bảo vệ tập hợp trả về khỏi sửa đổi ngoại lai &
Tỷ lệ tập hợp dữ liệu không thể sửa đổi khi truy xuất từ bên ngoài &
100.00\% \\ \hline
\end{longtable}
\endgroup
```

Bảng quy định phương pháp và quy trình kiểm chứng kiến trúc cho yêu cầu `NFR-04`:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.2116}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1433}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.6451}}|}
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endhead
Kiểm thử tự động (\texttt{T}) &
Kiểm thử tự động &
Thực thi toàn bộ bộ kiểm thử đơn vị cho từng quy tắc nghiệp vụ, bao gồm
các ca kiểm thử giá trị biên, và xác nhận kết quả kiểm thử đạt trạng
thái thành công. \\ \hline
Soát xét kỹ thuật kiến trúc (\texttt{I}) &
Đánh giá kiểm tra &
Rà soát cấu trúc mã nguồn để bảo đảm: (1) không có phụ thuộc vòng giữa
các gói mã nguồn; (2) tầng nghiệp vụ không phụ thuộc ngược ra tầng bên
ngoài; (3) trạng thái nội bộ của thực thể được đóng gói an toàn. \\ \hline
\end{longtable}
\endgroup
```

### 5.5 NFR-05: Tính khả dụng và trải nghiệm người dùng

Đặc tính chất lượng: Khả năng tương tác, đặc tính con Tính dễ học, Tính dễ vận hành và Bảo vệ người dùng khỏi lỗi.

Mô tả yêu cầu:

1. Tinh giản quy trình nhập liệu:
   - Điền sẵn giá trị mặc định hợp lý: biểu mẫu thu chi và chuyển tiền tự động chọn ngày hiện tại; trường chọn ví tự động ưu tiên ví sử dụng gần nhất, hoặc ví "Tiền mặt" nếu hệ thống chưa ghi nhận giao dịch nào.
   - Tự động đặt con trỏ vào ô nhập số tiền khi mở hộp thoại ghi nhận giao dịch mới.
   - Lọc danh mục theo ngữ cảnh giao dịch: giao dịch thu nhập chỉ hiển thị danh mục `INCOME`, giao dịch chi tiêu chỉ hiển thị danh mục `EXPENSE`, và chỉ hiển thị các danh mục đang hoạt động. Danh mục của chi phí giao dịch luôn thuộc `EXPENSE`, kể cả khi giao dịch gốc là thu nhập.
   - Định dạng hiển thị số tiền tức thì bằng dấu phân cách hàng nghìn trong khi người dùng nhập liệu (ví dụ: nhập "1500000" hiển thị ngay thành "1,500,000 VNĐ").
2. Bảo vệ người dùng khỏi lỗi thao tác:
   - Hiển thị hộp thoại xác nhận rõ ràng trước các thao tác có mức độ rủi ro cao: xóa giao dịch, lưu trữ ví còn số dư, nhập tệp dữ liệu thay thế cơ sở dữ liệu hiện tại.
   - Kiểm tra định dạng số tiền và các trường dữ liệu bắt buộc ngay trong quá trình nhập liệu, ngăn chặn gửi dữ liệu không hợp lệ.
3. Thông báo lỗi định hướng giải quyết:
   - Mọi thông báo lỗi dữ liệu đầu vào và vi phạm quy tắc nghiệp vụ đều sử dụng tiếng Việt tự nhiên, rõ ràng, nêu đúng nguyên nhân và hướng dẫn cụ thể cách khắc phục. Ví dụ: "Số dư ví MB Bank hiện tại là 150,000 VNĐ, không đủ để chi trả khoản chi 200,000 VNĐ. Vui lòng giảm số tiền chi tiêu hoặc chuyển tiền từ ví khác sang."
   - Tuyệt đối không hiển thị lỗi kỹ thuật thô, thông tin vết ngăn xếp hoặc mã lỗi chưa xử lý ra màn hình người dùng. Mọi ngoại lệ kỹ thuật nội bộ bắt buộc phải được ánh xạ sang thông điệp trong bảng mã lỗi chuẩn (`ERR-*`).

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với tính khả dụng và trải nghiệm người dùng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3465}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.5224}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1311}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endhead
Thông báo lỗi có hướng dẫn khắc phục &
Số thông báo lỗi có kèm hướng dẫn xử lý trong bảng mã lỗi chuẩn &
100.00\% \\ \hline
Lỗi kỹ thuật hoặc vết ngăn xếp lộ ra giao diện &
Tỷ lệ thông báo lỗi kỹ thuật nội bộ hiển thị trực tiếp cho người dùng &
0.00\% \\ \hline
Thao tác xóa hoặc thay thế dữ liệu có xác nhận &
Số thao tác rủi ro cao có hộp thoại cảnh báo xác nhận chia tổng số thao
tác &
100.00\% \\ \hline
Mức độ thuận tiện khi làm quen (người dùng mới) &
Tỷ lệ người dùng thử nghiệm hoàn tất luồng tạo ví và ghi giao dịch đầu
tiên &
100.00\% không gặp trở ngại điều hướng \\ \hline
\end{longtable}
\endgroup
```

Bảng quy định phương pháp và quy trình kiểm chứng tính khả dụng cho yêu cầu `NFR-05`:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.2285}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1457}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.6258}}|}
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endhead
Trình diễn thao tác (\texttt{D}) &
Quan sát trực tiếp &
Quan sát nhóm người dùng thử nghiệm thực hiện các kịch bản tương tác cơ
bản (tạo ví, ghi chi tiêu, chuyển tiền) để đánh giá độ trực quan và mức
độ dễ tiếp cận. \\ \hline
Soát xét kỹ thuật thông báo lỗi (\texttt{I}) &
Đánh giá kiểm tra &
Rà soát toàn bộ danh mục thông điệp lỗi trong mã nguồn về tính nhất quán
tiếng Việt, tính định hướng người dùng và bảo đảm không để lộ chi tiết
kỹ thuật nội bộ ra giao diện. \\ \hline
\end{longtable}
\endgroup
```

### 5.6 NFR-06: Tính tương thích và môi trường chạy

Đặc tính chất lượng: Tính linh hoạt, đặc tính con Tính thích ứng và Tính dễ cài đặt.

Mô tả yêu cầu:

1. Môi trường thực thi: tương thích máy ảo Java (JVM) phiên bản 17 LTS trở lên.
2. Độc lập hệ điều hành: ứng dụng được đóng gói độc lập, vận hành đồng nhất trên các hệ điều hành máy tính cá nhân phổ biến có cài đặt JVM tương thích (Windows, macOS, Linux) mà không cần chỉnh sửa mã nguồn hay biên dịch lại.
3. Quản lý đường dẫn tệp tin: không viết cứng ký tự phân cách thư mục (`\` hoặc `/`); toàn bộ thao tác xử lý đường dẫn phải thông qua đối tượng đường dẫn trừu tượng độc lập nền tảng của hệ thống vào/ra tệp tin.
4. Chuẩn mã hóa UTF-8 không BOM: toàn bộ thao tác đọc, ghi tệp dữ liệu cục bộ, tệp xuất JSON và báo cáo định dạng văn bản bắt buộc chỉ định rõ UTF-8 không BOM, bảo đảm dữ liệu tiếng Việt hiển thị chính xác trên mọi hệ điều hành.
5. Không phụ thuộc thư viện nhị phân phụ thuộc nền tảng: không sử dụng thư viện nhị phân ngoại lai phụ thuộc nền tảng phần cứng, bảo đảm ứng dụng có thể đóng gói trọn vẹn thành một tệp thực thi duy nhất.

Bảng chỉ số đo và ngưỡng nghiệm thu kỹ thuật đối với tính tương thích và môi trường thực thi:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3479}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.5395}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1127}}|}
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thuộc tính đo kiểm &
Chỉ số đo &
Ngưỡng nghiệm thu \\ \hline
\endhead
Bộ kiểm thử tự động trên Windows, macOS, Linux &
Tỷ lệ ca kiểm thử đạt kết quả thành công trên từng hệ điều hành &
100.00\% \\ \hline
Thao tác đọc ghi tệp chỉ định rõ UTF-8 &
Số thao tác đọc ghi có chỉ định bảng mã UTF-8 chia tổng số thao tác tệp &
100.00\% \\ \hline
Hiển thị dữ liệu tiếng Việt có dấu &
Số trường hợp lỗi hiển thị hoặc sai lệch ký tự tiếng Việt phát hiện được &
0 \\ \hline
Thư viện nhị phân phụ thuộc nền tảng &
Số lượng thư viện nhị phân phụ thuộc nền tảng được sử dụng &
0 \\ \hline
Phiên bản môi trường Java tối thiểu &
Phiên bản JDK/JRE tối thiểu được hệ thống hỗ trợ &
Java SE 17 LTS \\ \hline
\end{longtable}
\endgroup
```

Bảng quy định phương pháp và quy trình kiểm chứng tính tương thích cho yêu cầu `NFR-06`:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.2548}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1355}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.6097}}|}
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Phương pháp &
Loại hình &
Quy trình \\ \hline
\endhead
Kiểm thử tương thích đa nền tảng (\texttt{T}) &
Kiểm thử tự động &
Thực thi toàn bộ bộ kiểm thử đơn vị tự động trên các hệ điều hành mục
tiêu để bảo đảm tính nhất quán của kết quả xử lý. \\ \hline
Soát xét mã nguồn vào/ra tệp (\texttt{I}) &
Đánh giá kiểm tra &
Rà soát toàn bộ các thao tác đọc ghi tệp để bảo đảm luôn chỉ định bảng
mã UTF-8 và sử dụng đường dẫn trừu tượng độc lập nền tảng. \\ \hline
\end{longtable}
\endgroup
```
\newpage

## 6. ĐẶC TẢ 20 CA SỬ DỤNG CHI TIẾT

20 ca sử dụng của Hệ thống Quản lý Tài chính Cá nhân (PFMS) mô hình hóa các tương tác giữa Người dùng và hệ thống, đáp ứng các yêu cầu chức năng và quy tắc nghiệp vụ tài chính theo 4 nguyên tắc thiết kế then chốt:

- Hệ thống phục vụ một người dùng duy nhất trên máy tính để bàn cục bộ.
- Mỗi ca sử dụng chỉ có một mục tiêu nghiệp vụ độc lập, không gộp thành cụm "Quản lý X" mơ hồ.
- Giao dịch tài chính nhiều bước được ghi nhận nguyên khối hoặc tự động hoàn nguyên khi có lỗi phát sinh.
- Phân biệt rõ biến thể thành công hợp lệ (luồng rẽ nhánh) với tình huống vi phạm dữ liệu (luồng ngoại lệ).

Quy ước chung: mọi ca sử dụng đều có tác nhân chính là Người dùng, phạm vi là hệ thống PFMS, cấp độ là mục tiêu người dùng; các thuộc tính này không lặp lại ở từng ca sử dụng. Thông báo lỗi được đặc tả chi tiết ở lần xuất hiện đầu tiên; các lần tiếp theo được dẫn chiếu theo mã lỗi chuẩn (`ERR-*`).

Mỗi ca sử dụng được đặc tả cấu trúc gồm 9 thành phần chuẩn hóa:

1. Mã và tên UC: `UC-<DOMAIN>-<NN>` và tên dạng động từ + danh từ.
2. Bảng thuộc tính: các bên liên quan và mối quan tâm, mô tả tóm tắt, điều kiện kích hoạt.
3. Tiền điều kiện: trạng thái hệ thống và dữ liệu cần có trước khi bắt đầu.
4. Bảo đảm tối thiểu: trạng thái được giữ nguyên khi UC thất bại hoặc bị hủy, không để lại bản ghi rác.
5. Hậu điều kiện thành công: trạng thái dữ liệu sau khi hoàn tất, bảo toàn các bất biến tài chính.
6. Luồng sự kiện chính: các bước đánh số từ lúc bắt đầu đến khi đạt mục tiêu.
7. Các luồng rẽ nhánh: biến thể hợp lệ dẫn tới thành công hoặc kết thúc hợp lệ.
8. Các luồng ngoại lệ: vi phạm dữ liệu hoặc điều kiện biên, nêu phản ứng hệ thống, mã `ERR-*` và điểm phục hồi.
9. Quy tắc nghiệp vụ đặc thù: liên kết tới `BR-*` và `FR-*` mà UC áp dụng.

Bảng phân bổ số lượng 20 ca sử dụng theo 6 nhóm chức năng nghiệp vụ của hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1965}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4105}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1965}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1965}}|}
\hline
\rowcolor[gray]{0.9}
STT &
Nhóm chức năng &
Mã nhóm &
Số lượng UC \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
STT &
Nhóm chức năng &
Mã nhóm &
Số lượng UC \\ \hline
\endhead
1 &
Ví &
\texttt{WAL} &
4 \\ \hline
2 &
Ghi nhận thu chi &
\texttt{TXN} &
4 \\ \hline
3 &
Chuyển tiền nội bộ &
\texttt{TRF} &
3 \\ \hline
4 &
Danh mục phân loại &
\texttt{CAT} &
4 \\ \hline
5 &
Thống kê và báo cáo &
\texttt{REP} &
2 \\ \hline
6 &
Lưu trữ và sao lưu dữ liệu &
\texttt{DAT} &
3 \\ \hline
\strut &
\textbf{Tổng} &
\strut &
\textbf{20} \\ \hline
\end{longtable}
\endgroup
```

Bảng danh mục 20 ca sử dụng chi tiết cùng nhóm chức năng và yêu cầu chức năng (FR) ánh xạ tương ứng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\centering\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.0950}}|>{\centering\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1247}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.3821}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.2120}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1862}}|}
\hline
\rowcolor[gray]{0.9}
STT &
Mã UC &
Tên ca sử dụng &
Nhóm chức năng &
FR ánh xạ \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
STT &
Mã UC &
Tên ca sử dụng &
Nhóm chức năng &
FR ánh xạ \\ \hline
\endhead
1 &
\texttt{UC-WAL-01} &
Tạo ví mới &
Ví &
\texttt{FR-WAL-01}, \texttt{FR-WAL-02}, \texttt{FR-WAL-03},
\texttt{FR-WAL-04} \\ \hline
2 &
\texttt{UC-WAL-02} &
Theo dõi số dư tính lại từ lịch sử dòng tiền và cơ cấu ví &
Ví &
\texttt{FR-WAL-02}, \texttt{FR-WAL-04} \\ \hline
3 &
\texttt{UC-WAL-03} &
Cập nhật thông tin ví &
Ví &
\texttt{FR-WAL-01}, \texttt{FR-WAL-03} \\ \hline
4 &
\texttt{UC-WAL-04} &
Điều chỉnh số dư ví &
Ví &
\texttt{FR-WAL-05} \\ \hline
5 &
\texttt{UC-TXN-01} &
Ghi nhận giao dịch thu chi &
Ghi nhận thu chi &
\texttt{FR-TXN-01}, \texttt{FR-TXN-02} \\ \hline
6 &
\texttt{UC-TXN-02} &
Tra cứu và lọc lịch sử giao dịch thu chi &
Ghi nhận thu chi &
\texttt{FR-TXN-03} \\ \hline
7 &
\texttt{UC-TXN-03} &
Chỉnh sửa giao dịch thu chi &
Ghi nhận thu chi &
\texttt{FR-TXN-04} \\ \hline
8 &
\texttt{UC-TXN-04} &
Xóa giao dịch thu chi &
Ghi nhận thu chi &
\texttt{FR-TXN-05} \\ \hline
9 &
\texttt{UC-TRF-01} &
Chuyển tiền nội bộ giữa hai ví &
Chuyển tiền nội bộ &
\texttt{FR-TRF-01} \\ \hline
10 &
\texttt{UC-TRF-02} &
Tra cứu lịch sử chuyển tiền &
Chuyển tiền nội bộ &
\texttt{FR-TRF-02} \\ \hline
11 &
\texttt{UC-TRF-03} &
Xóa giao dịch chuyển tiền &
Chuyển tiền nội bộ &
\texttt{FR-TRF-02} \\ \hline
12 &
\texttt{UC-CAT-01} &
Bổ sung danh mục phân loại mới &
Danh mục phân loại &
\texttt{FR-CAT-01}, \texttt{FR-CAT-02} \\ \hline
13 &
\texttt{UC-CAT-02} &
Đổi tên danh mục &
Danh mục phân loại &
\texttt{FR-CAT-02}, \texttt{FR-CAT-03} \\ \hline
14 &
\texttt{UC-CAT-03} &
Khôi phục hoặc tạo lại mục đã xóa &
Danh mục phân loại &
\texttt{FR-CAT-02}, \texttt{FR-CAT-03} \\ \hline
15 &
\texttt{UC-CAT-04} &
Xóa danh mục hoặc nhóm &
Danh mục phân loại &
\texttt{FR-CAT-03} \\ \hline
16 &
\texttt{UC-REP-01} &
Thống kê dòng tiền 12 tháng gần nhất &
Thống kê và báo cáo &
\texttt{FR-REP-01} \\ \hline
17 &
\texttt{UC-REP-02} &
Phân tích cơ cấu thu chi theo nhóm và danh mục &
Thống kê và báo cáo &
\texttt{FR-REP-02} \\ \hline
18 &
\texttt{UC-DAT-01} &
Xuất dữ liệu tài chính &
Lưu trữ và sao lưu dữ liệu &
\texttt{FR-DAT-01} \\ \hline
19 &
\texttt{UC-DAT-02} &
Phục hồi dữ liệu hệ thống từ bản sao lưu &
Lưu trữ và sao lưu dữ liệu &
\texttt{FR-DAT-02} \\ \hline
20 &
\texttt{UC-DAT-03} &
Kiểm tra lại dữ liệu hệ thống &
Lưu trữ và sao lưu dữ liệu &
\texttt{FR-DAT-03} \\ \hline
\end{longtable}
\endgroup
```

### 6.1 UC-WAL-01: Tạo ví mới

Bảng thuộc tính đặc tả mục tiêu, các bên liên quan và điều kiện kích hoạt của ca sử dụng Tạo ví mới (`UC-WAL-01`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2198}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7802}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần tạo tài khoản chứa tiền để ghi chép dòng tiền. Hệ thống:
ví mới có số dư 0.00, tên hợp lệ và duy nhất theo Quy tắc tên của ví,
nhóm nhu cầu và danh mục (\texttt{BR-NAM-01}), số dư luôn đúng theo Quy
tắc bảo toàn số dư ví (\texttt{BR-FIN-02}). \\ \hline
Mô tả tóm tắt &
Tạo ví mới với tên và ghi chú. Số dư ban đầu là 0.00 VNĐ; số tiền đang
có được ghi nhận sau bằng khoản điều chỉnh số dư (\texttt{UC-WAL-04}).
Hệ thống tính lại tổng số dư và tỷ lệ cơ cấu giữa các ví. \\ \hline
Điều kiện kích hoạt &
Người dùng chọn chức năng thêm ví mới trên giao diện quản lý ví. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Hệ thống đã khởi chạy, đọc tệp dữ liệu cục bộ thành công; Người dùng đang ở màn hình quản lý ví.
2. Danh sách ví đã được đọc (lần chạy đầu tiên chỉ có ví "Tiền mặt" có sẵn).

#### Bảo đảm tối thiểu

Nếu gặp lỗi kiểm tra dữ liệu hoặc lỗi ghi tệp, cơ sở dữ liệu và danh sách ví không thay đổi, không có ví rác nào được lưu.

#### Hậu điều kiện thành công

1. Ví mới được lưu với mã duy nhất, trạng thái `ACTIVE` và số dư 0.00 VNĐ.
2. Tổng số dư các ví (`FR-WAL-02`, quy tắc 3) và bảng cơ cấu số dư (`FR-WAL-04`) được cập nhật.

#### Luồng sự kiện chính

1. Người dùng chọn "Thêm ví mới".
2. Hệ thống hiển thị biểu mẫu gồm Tên ví, Ghi chú mô tả.
3. Người dùng nhập tên ví và ghi chú (nếu có).
4. Người dùng gửi yêu cầu lưu.
5. Hệ thống kiểm tra tên theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`): sai định dạng bị từ chối (`ERR-NAM-001`); trùng bị từ chối (`ERR-NAM-002`).
6. Hệ thống tạo ví mới với mã duy nhất, trạng thái `ACTIVE`, số dư 0.00 VNĐ.
7. Hệ thống tính lại tổng số dư các ví (`FR-WAL-02`, Quy tắc bảo toàn số dư ví `BR-FIN-02`) và tỷ lệ cơ cấu từng ví (`FR-WAL-04`).
8. Hệ thống lưu tệp dữ liệu, thông báo thành công và làm mới danh sách ví cùng bảng cơ cấu.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Trùng tên ví

1. Tại bước 5, hệ thống phát hiện tên trùng ví hiện có (`ERR-NAM-002`).
2. Hệ thống hủy thao tác ghi, giữ dữ liệu đã nhập, đánh dấu trường tên và thông báo: "Tên ví này đã tồn tại trong hệ thống. Vui lòng chọn một tên khác."
3. Quay lại bước 3 để Người dùng sửa tên hoặc hủy.

##### Luồng ngoại lệ 5b: Tên ví sai định dạng

1. Tại bước 5, hệ thống phát hiện tên sai định dạng (`ERR-NAM-001`).
2. Hệ thống hủy thao tác ghi, giữ dữ liệu đã nhập, đánh dấu trường tên và thông báo: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Quay lại bước 3.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): số dư ví tại mỗi thời điểm bằng tổng dòng tiền vào trừ tổng dòng tiền ra đến thời điểm đó; ví mới bắt đầu từ 0.00 VNĐ.
2. Yêu cầu chức năng liên quan: `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`.

### 6.2 UC-WAL-02: Theo dõi số dư tính lại từ lịch sử dòng tiền và cơ cấu ví

Bảng thuộc tính đặc tả mục tiêu nghiệp vụ và điều kiện kích hoạt của ca sử dụng Theo dõi số dư tính lại từ lịch sử dòng tiền và cơ cấu ví (`UC-WAL-02`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2126}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7874}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần biết số dư khả dụng thực tế của từng ví và tỷ lệ phân bổ
tiền giữa các ví để quyết định chi tiêu hoặc điều chuyển vốn. \\ \hline
Mô tả tóm tắt &
Tính số dư khả dụng theo thời gian thực của từng ví từ toàn bộ lịch sử
dòng tiền, và phân tích tỷ lệ cơ cấu tiền giữa các ví đang hoạt động và
các ví lưu trữ có số dư lớn hơn 0.00 VNĐ. \\ \hline
Điều kiện kích hoạt &
Người dùng mở màn hình quản lý ví hoặc chuyển sang tab ``Cơ cấu phân bổ
ví''. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Hệ thống đã khởi chạy và đọc tệp dữ liệu thành công.
2. Người dùng đang ở màn hình quản lý ví.

#### Bảo đảm tối thiểu

Hệ thống chỉ đọc, không thay đổi bất kỳ dữ liệu nào trong tệp lưu trữ.

#### Hậu điều kiện thành công

1. Số dư từng ví được tính đúng theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`) tại thời điểm truy vấn.
2. Tỷ lệ cơ cấu hiển thị đúng theo `FR-WAL-04`.

#### Luồng sự kiện chính

1. Người dùng mở danh sách ví và chọn bộ lọc trạng thái (`ACTIVE` mặc định, `ARCHIVED` hoặc `ALL`).
2. Hệ thống quét dòng tiền của từng ví thỏa điều kiện lọc và tính lại số dư theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`):
   $$
   \begin{aligned}
   WalletBalance_w &= \sum Income_w + \sum TransferIn_w \\
   &\quad - \sum Expense_w - \sum TransferOut_w + \sum Adjustment_w
   \end{aligned}
   $$
3. Hệ thống tính tổng số dư hệ thống (`FR-WAL-02`, quy tắc 3) bằng số thập phân chính xác, gồm các ví `ACTIVE` và các ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ, không phụ thuộc bộ lọc hiển thị.
4. Hệ thống hiển thị bảng gồm Mã ví, Tên ví, Trạng thái, Số dư tính lại; chân bảng có tổng số dư toàn hệ thống.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Xem phân tích cơ cấu tiền trong ví

1. Tại bước 1, Người dùng chọn tab "Cơ cấu phân bổ ví".
2. Hệ thống tính tổng số dư các ví `ACTIVE` cộng các ví `ARCHIVED` có số dư lớn hơn 0.00 VNĐ.
3. Nếu tổng lớn hơn 0, hệ thống tính tỷ lệ từng ví:
   $$Rate_w = \left(\frac{WalletBalance_w}{TotalWalletBalance}\right) \times 100\%$$
   Phép tính dùng tối thiểu 4 chữ số thập phân, làm tròn ngân hàng, hiển thị 2 chữ số thập phân (ví dụ 35.50%).
4. Hệ thống sắp xếp theo tỷ lệ giảm dần và hiển thị bảng thống kê kèm biểu đồ.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3a: Tổng số dư các ví bằng 0

1. Tại bước 3 của luồng rẽ nhánh 1a, tổng số dư bằng 0.
2. Hệ thống bỏ qua phép chia cho 0 (`ERR-FIN-003`) và gán tỷ lệ 0.00% cho mọi ví.
3. Hệ thống hiển thị bảng kèm thông báo: "Tổng số dư các ví hiện tại bằng 0, không có cơ cấu phân bổ."

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): số dư hiển thị là số dư tính lại từ lịch sử dòng tiền, không cho phép gán trực tiếp.
2. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi phép cộng dồn và chia tỷ lệ dùng số thập phân chính xác.
3. Yêu cầu chức năng liên quan: `FR-WAL-02`, `FR-WAL-04`.

### 6.3 UC-WAL-03: Cập nhật thông tin ví

Bảng thuộc tính đặc tả phạm vi cập nhật thông tin và điều kiện kích hoạt của ca sử dụng Cập nhật thông tin ví (`UC-WAL-03`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2178}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7822}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần đổi tên, ghi chú, lưu trữ ví không dùng hoặc xóa ví tạo
nhầm chưa có giao dịch. Hệ thống: cấm can thiệp trực tiếp vào số dư; các
giao dịch liên kết vẫn trỏ đúng ví. \\ \hline
Mô tả tóm tắt &
Sửa tên, ghi chú; chuyển trạng thái giữa \texttt{ACTIVE} và
\texttt{ARCHIVED}; hoặc xóa vĩnh viễn ví chưa phát sinh giao dịch.
Trường số dư bị khóa. Khi lưu trữ ví còn số dư, hệ thống cảnh báo. \\ \hline
Điều kiện kích hoạt &
Người dùng chọn chỉnh sửa, lưu trữ hoặc xóa một ví trong danh sách. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Ví cần xử lý đã tồn tại.
2. Người dùng đang ở màn hình quản lý ví.

#### Bảo đảm tối thiểu

Nếu thao tác bị hủy hoặc lỗi, thuộc tính ví giữ nguyên. Số dư ví không bao giờ thay đổi qua UC này.

#### Hậu điều kiện thành công

1. Tên, ghi chú hoặc trạng thái được cập nhật vào tệp dữ liệu; ví chưa có giao dịch được xóa hoàn toàn khi chọn xóa.
2. Số dư khả dụng của ví không đổi.
3. Ví `ARCHIVED` bị loại khỏi danh sách chọn cho giao dịch mới, nhưng lịch sử vẫn hiện trong báo cáo và số dư vẫn tính vào tổng số dư các ví.

#### Luồng sự kiện chính

1. Người dùng chọn chỉnh sửa một ví.
2. Hệ thống hiển thị biểu mẫu gồm Tên ví, Ghi chú mô tả, Trạng thái (`ACTIVE` hoặc `ARCHIVED`); trường Số dư chỉ đọc.
3. Người dùng sửa tên hoặc ghi chú.
4. Người dùng xác nhận lưu.
5. Hệ thống kiểm tra tên theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`) (so với các ví khác): sai định dạng `ERR-NAM-001`, trùng `ERR-NAM-002`.
6. Hệ thống cập nhật các trường vào đối tượng ví trong tệp dữ liệu.
7. Hệ thống thông báo thành công và làm mới giao diện.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Chuyển ví sang lưu trữ (`ARCHIVED`)

1. Tại bước 3, Người dùng đổi trạng thái từ `ACTIVE` sang `ARCHIVED`.
2. Nếu số dư ví khác 0 (theo Quy tắc bảo toàn số dư ví `BR-FIN-02`), hệ thống hiển thị cảnh báo: "Ví này hiện vẫn còn số dư khả dụng là [Số dư] VNĐ. Số dư này vẫn được tính vào tổng số dư trong các ví nhưng ví sẽ bị ẩn khỏi danh sách chọn khi tạo giao dịch mới. Bạn có chắc chắn muốn lưu trữ ví này không?"
3. Người dùng xác nhận.
4. Hệ thống cập nhật `ARCHIVED`, ẩn ví khỏi các danh sách chọn ví khi tạo giao dịch mới, giữ ví trong báo cáo lịch sử.
5. Tiếp tục tại bước 7 của luồng chính.

##### Luồng rẽ nhánh 3b: Xóa vĩnh viễn ví chưa phát sinh giao dịch

1. Người dùng chọn xóa một ví trên màn hình quản lý ví.
2. Nếu ví chưa có bản ghi nào trong sổ thu chi, sổ chuyển tiền và sổ điều chỉnh số dư, hệ thống hiển thị hộp thoại xác nhận xóa vĩnh viễn.
3. Người dùng xác nhận.
4. Hệ thống gỡ bản ghi ví khỏi bộ lưu trữ và giải phóng tên ví.
5. Hệ thống thông báo xóa thành công và làm mới giao diện.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Can thiệp trái phép vào số dư ví

1. Tại bước 2, hệ thống phát hiện yêu cầu chứa dữ liệu thay đổi số dư của ví đã có giao dịch.
2. Hệ thống từ chối, ghi nhật ký cảnh báo và thông báo: "Không được phép thay đổi trực tiếp số dư ví. Vui lòng sử dụng chức năng Điều chỉnh số dư hoặc Ghi nhận giao dịch."

##### Luồng ngoại lệ 5a: Trùng tên ví với ví khác

1. Tại bước 5, hệ thống phát hiện tên mới trùng ví khác (`ERR-NAM-002`).
2. Hệ thống hủy ghi, đánh dấu trường tên và thông báo: "Tên ví này đã tồn tại trong hệ thống. Vui lòng chọn một tên khác."
3. Quay lại bước 3.

##### Luồng ngoại lệ 5b: Tên ví mới sai định dạng

1. Tại bước 5, hệ thống phát hiện tên sai định dạng (`ERR-NAM-001`).
2. Hệ thống hủy ghi, đánh dấu trường tên và thông báo: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Quay lại bước 3.

##### Luồng ngoại lệ 3c: Lưu trữ ví tiền mặt có sẵn

1. Tại bước 3, trước khi xét luồng rẽ nhánh 3a, Người dùng đổi trạng thái ví "Tiền mặt" có sẵn sang `ARCHIVED`.
2. Hệ thống từ chối và thông báo: "Ví tiền mặt có sẵn không thể lưu trữ."
3. Quay lại bước 3.

##### Luồng ngoại lệ 3d: Xóa ví tiền mặt có sẵn

1. Tại bước 1 của luồng rẽ nhánh 3b, Người dùng chọn xóa ví "Tiền mặt" có sẵn.
2. Hệ thống từ chối và thông báo: "Ví tiền mặt có sẵn không thể xóa."

##### Luồng ngoại lệ 3e: Xóa ví đã có lịch sử dòng tiền

1. Tại bước 2 của luồng rẽ nhánh 3b, hệ thống phát hiện ví đã có giao dịch trong quá khứ.
2. Hệ thống từ chối và thông báo: "Ví đã có lịch sử dòng tiền nên không thể xóa vĩnh viễn. Vui lòng chuyển ví sang trạng thái lưu trữ."

#### Quy tắc nghiệp vụ đặc thù

1. Không xóa vật lý ví đã có bản ghi trong sổ thu chi, sổ chuyển tiền hoặc sổ điều chỉnh số dư; muốn ngừng dùng thì chuyển sang `ARCHIVED`. Ví chưa từng phát sinh giao dịch được xóa vĩnh viễn.
2. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): số dư ví chỉ thay đổi gián tiếp qua các nghiệp vụ có dòng tiền hợp lệ.
3. Yêu cầu chức năng liên quan: `FR-WAL-01`, `FR-WAL-03`.

### 6.4 UC-WAL-04: Điều chỉnh số dư ví

Bảng thuộc tính đặc tả mục tiêu đối soát chênh lệch và điều kiện kích hoạt của ca sử dụng Điều chỉnh số dư ví (`UC-WAL-04`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2082}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7918}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần đồng bộ số dư hệ thống với kiểm kê thực tế mà vẫn truy
vết được chênh lệch. Hệ thống: bản ghi điều chỉnh không được sửa; chênh
lệch được ghi thành khoản điều chỉnh độc lập. \\ \hline
Mô tả tóm tắt &
Ghi nhận khoản điều chỉnh số dư có dấu vết khi số dư thực tế lệch số dư
hệ thống. Hệ thống tính chênh lệch, hiển thị bản xem trước và cập nhật
số dư tính lại mà không động đến các giao dịch cũ. \\ \hline
Điều kiện kích hoạt &
Người dùng phát hiện số dư thực tế khác số dư hệ thống và chọn ``Điều
chỉnh số dư''. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Ví cần điều chỉnh tồn tại và đang `ACTIVE`.
2. Người dùng đang ở màn hình quản lý ví.

#### Bảo đảm tối thiểu

Nếu Người dùng hủy hoặc ghi lỗi, số dư tính lại giữ nguyên, không lưu khoản điều chỉnh rác.

#### Hậu điều kiện thành công

1. Khoản điều chỉnh mới được lưu và không sửa được về sau, gồm: mã khoản điều chỉnh, mã ví, ngày ghi nhận, số dư trước, số dư quan sát, chênh lệch, lý do (nếu có).
2. Số dư tính lại của ví bằng đúng số dư quan sát.
3. Quy tắc bảo toàn số dư ví (`BR-FIN-02`) được thỏa mãn nhờ số hạng điều chỉnh trong công thức số dư.

#### Luồng sự kiện chính

1. Người dùng chọn một ví đang hoạt động và nhấn "Điều chỉnh số dư".
2. Hệ thống tính số dư hiện tại theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`) và hiển thị biểu mẫu gồm Số dư hiện tại (chỉ đọc), Số dư thực tế quan sát, Lý do điều chỉnh (tùy chọn).
3. Người dùng nhập số dư thực tế và lý do (tùy chọn, tối đa 255 ký tự).
4. Người dùng yêu cầu xem trước kết quả.
5. Hệ thống tính chênh lệch:
   $$Adjustment_w = ObservedBalance_w - WalletBalance_w(\text{trước})$$
   Hệ thống kiểm tra chênh lệch khác 0, lý do không quá 255 ký tự, số dư quan sát không âm.
6. Hệ thống hiển thị hộp thoại xác nhận gồm số dư trước, số dư sau, chênh lệch (dương hoặc âm) và lý do.
7. Người dùng nhấn "Xác nhận điều chỉnh".
8. Hệ thống lưu bản ghi điều chỉnh, cập nhật số dư tính lại của ví và thông báo thành công.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 5a: Lý do vượt độ dài tối đa

1. Tại bước 5, lý do dài hơn 255 ký tự sau khi cắt khoảng trắng hai đầu.
2. Hệ thống chặn, đánh dấu trường lý do và thông báo: "Lý do điều chỉnh không được vượt quá 255 ký tự."
3. Quay lại bước 3.

##### Luồng ngoại lệ 5b: Số dư quan sát trùng số dư hệ thống

1. Tại bước 5, chênh lệch bằng 0.
2. Hệ thống từ chối tạo khoản ghi rỗng và thông báo: "Số dư nhập vào trùng với số dư hệ thống đang ghi nhận, không có chênh lệch cần điều chỉnh."
3. Quay lại bước 3.

##### Luồng ngoại lệ 5c: Số dư quan sát âm

1. Tại bước 5, số dư quan sát nhỏ hơn 0.
2. Hệ thống từ chối (`ERR-WAL-001`) và thông báo: "Số dư thực tế nhập vào không hợp lệ (số dư ví không được nhỏ hơn 0)."
3. Quay lại bước 3.

#### Quy tắc nghiệp vụ đặc thù

1. Khoản điều chỉnh là một số hạng độc lập trong công thức Quy tắc bảo toàn số dư ví (`BR-FIN-02`), không sửa hay xóa giao dịch dòng tiền trong quá khứ.
2. Bản ghi điều chỉnh không được sửa hoặc xóa sau khi ghi nhận, để bảo đảm dấu vết kiểm tra.
3. Yêu cầu chức năng liên quan: `FR-WAL-05`.

### 6.5 UC-TXN-01: Ghi nhận giao dịch thu chi

Bảng thuộc tính đặc tả mục tiêu ghi nhận thu chi và điều kiện kích hoạt của ca sử dụng Ghi nhận giao dịch thu chi (`UC-TXN-01`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2126}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7874}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần ghi chính xác thu chi hằng ngày gắn với danh mục và
nguồn tiền, kể cả chi phí giao dịch. Hệ thống: cập nhật đồng thời số dư
ví, không để ví âm, phân loại theo danh mục 3 cấp. \\ \hline
Mô tả tóm tắt &
Ghi một giao dịch thu nhập hoặc chi tiêu, phân loại theo danh mục 3 cấp
(Loại thu chi, Nhóm nhu cầu, Danh mục), liên kết với ví và cập nhật số
dư ví. Nếu có chi phí, hệ thống ghi riêng thành một phí giao dịch liên
kết. \\ \hline
Điều kiện kích hoạt &
Người dùng chọn thêm giao dịch mới trên màn hình quản lý giao dịch thu
chi. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Hệ thống có ít nhất một ví `ACTIVE`.
2. Cấu trúc danh mục 3 cấp đã được đọc.
3. Người dùng đang ở màn hình quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu giao dịch bị hủy hoặc lỗi, số dư ví không đổi và không lưu bản ghi giao dịch hay phí giao dịch rác.

#### Hậu điều kiện thành công

1. Bản ghi giao dịch được lưu với mã duy nhất; nếu chi phí lớn hơn 0, phí giao dịch liên kết được lưu cùng lúc.
2. Số dư ví được cập nhật ngay theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`) (`EXPENSE`: trừ tiền; `INCOME`: cộng tiền; trừ thêm chi phí nếu có) và không âm.
3. Chỉ số tổng hợp dòng tiền tháng và báo cáo cơ cấu thu chi được cập nhật.

#### Luồng sự kiện chính

1. Người dùng chọn "Thêm giao dịch mới".
2. Hệ thống hiển thị biểu mẫu gồm Ngày giao dịch (mặc định ngày hiện tại), Loại giao dịch (`INCOME` hoặc `EXPENSE`), Ví thanh toán, Danh mục chi tiết, Số tiền, Chi phí (tùy chọn), Danh mục phí (khi có chi phí), Ghi chú mô tả.
3. Người dùng chọn Loại giao dịch là `EXPENSE`.
4. Hệ thống tải các nhóm nhu cầu và danh mục chi tiết đang dùng thuộc loại `EXPENSE` theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`).
5. Người dùng chọn ngày, ví `ACTIVE`, danh mục chi tiết, nhập số tiền (lớn hơn 0), chi phí (không âm, mặc định 0.00), danh mục phí khi chi phí lớn hơn 0 và ghi chú.
6. Người dùng xác nhận lưu.
7. Hệ thống kiểm tra:
   - Số tiền lớn hơn 0, chuẩn hóa thành số thập phân chính xác 2 chữ số theo Quy tắc độ chính xác tài chính (`BR-FIN-01`).
   - Danh mục đang dùng và thuộc loại `EXPENSE` theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`).
   - Chi phí không âm; khi chi phí lớn hơn 0 phải có danh mục phí là danh mục `EXPENSE` đang dùng.
   - Số dư khả dụng sau khi trừ số tiền và chi phí không âm theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`).
8. Hệ thống tạo bản ghi giao dịch với mã duy nhất; nếu chi phí lớn hơn 0, tạo thêm phí giao dịch liên kết.
9. Hệ thống trừ số tiền và chi phí (nếu có) khỏi số dư ví theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`).
10. Hệ thống cập nhật các chỉ số tổng hợp của tháng quan sát.
11. Hệ thống lưu tệp dữ liệu, thông báo thành công và làm mới lịch sử giao dịch.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Ghi nhận giao dịch thu nhập (`INCOME`)

1. Tại bước 3, Người dùng chọn Loại giao dịch là `INCOME`.
2. Hệ thống tải các nhóm và danh mục đang dùng thuộc loại `INCOME`; khi có chi phí, tải thêm danh mục `EXPENSE` đang dùng để chọn danh mục phí.
3. Người dùng chọn ví nhận tiền, danh mục thu nhập tương ứng, nhập số tiền (lớn hơn 0), chi phí (tùy chọn), danh mục phí (khi chi phí lớn hơn 0) và ghi chú.
4. Tại bước 7, hệ thống kiểm tra danh mục thuộc `INCOME`, số tiền lớn hơn 0 và số dư ví sau khi cộng số tiền, trừ chi phí không âm. Nếu không thỏa, chuyển sang luồng ngoại lệ 7b.
5. Tại bước 9, hệ thống cộng số tiền vào ví nhận và trừ chi phí (nếu có).
6. Tiếp tục tại bước 10 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Loại giao dịch chưa có danh mục chi tiết đang dùng

1. Tại bước 4 (hoặc bước 2 của luồng rẽ nhánh 3a), loại giao dịch được chọn chưa có danh mục chi tiết đang dùng.
2. Hệ thống không cho tiếp tục và thông báo: "Loại giao dịch này chưa có danh mục chi tiết đang dùng. Hãy thêm danh mục trước khi ghi nhận giao dịch."
3. Hệ thống đề nghị mở màn hình quản lý danh mục để bổ sung theo `UC-CAT-01`. Nếu Người dùng đồng ý, hệ thống mở màn hình đó và giao dịch đang nhập không được lưu.
4. Nếu Người dùng từ chối, UC kết thúc, không lưu giao dịch, không đổi số dư.

##### Luồng ngoại lệ 7a: Số tiền không hợp lệ

1. Tại bước 7, số tiền sai định dạng số học hoặc nhỏ hơn hoặc bằng 0.
2. Hệ thống từ chối và đánh dấu trường số tiền: sai định dạng thì báo `ERR-FIN-001`; nhỏ hơn hoặc bằng 0 thì báo số tiền giao dịch phải lớn hơn 0.
3. Quay lại bước 5.

##### Luồng ngoại lệ 7b: Số dư ví không đủ

1. Tại bước 7, số dư khả dụng không đủ: với chi tiêu, số dư nhỏ hơn tổng số tiền và chi phí; với thu nhập có chi phí, số dư sau khi cộng số tiền và trừ chi phí nhỏ hơn 0.
2. Hệ thống từ chối (`ERR-WAL-001`); Người dùng cần chọn ví khác hoặc giảm số tiền hoặc chi phí.
3. Quay lại bước 5.

##### Luồng ngoại lệ 7c: Danh mục đã xóa hoặc không khớp loại giao dịch

1. Tại bước 7, danh mục đã xóa hoặc có Loại thu chi không khớp loại giao dịch.
2. Hệ thống chặn và thông báo: "Danh mục không hợp lệ hoặc không tương thích với Loại giao dịch đã chọn."
3. Quay lại bước 5.

##### Luồng ngoại lệ 7d: Chi phí không hợp lệ hoặc thiếu danh mục phí

1. Tại bước 7, chi phí nhỏ hơn 0; hoặc chi phí lớn hơn 0 nhưng chưa chọn danh mục phí; hoặc danh mục phí đã xóa hoặc không thuộc `EXPENSE`.
2. Hệ thống từ chối: chi phí âm báo `ERR-FEE-001`; thiếu danh mục phí báo `ERR-FEE-002`; danh mục phí đã xóa hoặc sai loại báo `ERR-FEE-003`.
3. Quay lại bước 5.

##### Luồng ngoại lệ 7e: Ví không ở trạng thái hoạt động

1. Tại bước 7, ví được chọn không tồn tại hoặc đang `ARCHIVED`.
2. Hệ thống từ chối và thông báo: "Ví được chọn không ở trạng thái hoạt động hoặc không tồn tại."
3. Quay lại bước 5 để chọn ví `ACTIVE` khác.

##### Luồng ngoại lệ 7f: Chưa có danh mục chi tiêu để chọn cho phí giao dịch

1. Tại bước 7, chi phí lớn hơn 0 nhưng loại `EXPENSE` chưa có danh mục chi tiết đang dùng nào. Luồng này được kiểm tra trước luồng ngoại lệ 7d.
2. Hệ thống từ chối và thông báo: "Chưa có danh mục chi tiêu nào để chọn cho phí giao dịch. Hãy đặt chi phí bằng 0 hoặc thêm danh mục chi tiêu trước."
3. Hệ thống đề nghị mở màn hình quản lý danh mục để bổ sung theo `UC-CAT-01`. Nếu Người dùng đồng ý, hệ thống mở màn hình đó và giao dịch đang nhập không được lưu.
4. Nếu Người dùng từ chối, quay lại bước 5 để đặt chi phí bằng 0.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): giao dịch phản ánh vào số dư ví cùng lúc với việc ghi giao dịch và phí liên kết; nếu một trong hai thất bại thì cả hai đều không có hiệu lực.
2. Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): mọi giao dịch phân loại theo cấu trúc 3 cấp, Loại thu chi luôn nhất quán.
3. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi phép toán dùng số thập phân chính xác 2 chữ số, không làm tròn nhị phân.
4. Phí giao dịch là giao dịch thu chi loại `EXPENSE` liên kết với giao dịch phát sinh ra nó; hai bản ghi cùng có hiệu lực hoặc cùng không.
5. Yêu cầu chức năng liên quan: `FR-TXN-01`, `FR-TXN-02`.

### 6.6 UC-TXN-02: Tra cứu và lọc lịch sử giao dịch thu chi

Bảng thuộc tính đặc tả phạm vi tra cứu, lọc giao dịch và điều kiện kích hoạt của ca sử dụng Tra cứu và lọc lịch sử giao dịch thu chi (`UC-TXN-02`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2219}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7781}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần tra cứu, đối chiếu các khoản thu chi theo nhiều tiêu chí
kết hợp để kiểm soát dòng tiền. \\ \hline
Mô tả tóm tắt &
Tìm kiếm, lọc và phân trang lịch sử giao dịch theo khoảng thời gian, ví,
loại giao dịch, danh mục, từ khóa ghi chú. Hệ thống tính tổng thu, tổng
chi, dòng tiền thuần trên tập kết quả; phí giao dịch hiển thị thành dòng
riêng kèm nhãn liên kết và được tính vào tổng chi. \\ \hline
Điều kiện kích hoạt &
Người dùng mở màn hình lịch sử giao dịch hoặc áp dụng bộ lọc. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Hệ thống đã đọc tệp dữ liệu thành công.
2. Người dùng đang ở màn hình quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Truy vấn chỉ đọc, không thay đổi giao dịch hay số dư ví.

#### Hậu điều kiện thành công

1. Danh sách giao dịch khớp đúng tập kết quả lọc (các tiêu chí kết hợp đồng thời).
2. Tổng thu nhập, tổng chi tiêu, dòng tiền thuần phản ánh đúng tập kết quả theo Quy tắc độ chính xác tài chính (`BR-FIN-01`).

#### Luồng sự kiện chính

1. Người dùng mở "Lịch sử giao dịch thu chi" và chọn tiêu chí lọc:
   - Khoảng thời gian (từ ngày, đến ngày).
   - Phạm vi ví: một hoặc nhiều ví, hoặc toàn bộ.
   - Loại giao dịch: `ALL`, `INCOME` hoặc `EXPENSE`.
   - Nhóm nhu cầu hoặc danh mục chi tiết; danh sách gồm cả mục đã xóa kèm nhãn "(đã xóa)" để tra cứu giao dịch cũ.
   - Từ khóa tìm trong ghi chú.
2. Người dùng nhấn "Áp dụng bộ lọc".
3. Hệ thống áp dụng đồng thời mọi tiêu chí, sắp xếp theo ngày giao dịch giảm dần và phân trang (mặc định 50 bản ghi mỗi trang).
4. Hệ thống tính trên toàn bộ tập kết quả (2 chữ số thập phân theo Quy tắc độ chính xác tài chính `BR-FIN-01`): tổng thu nhập; tổng chi tiêu; dòng tiền thuần bằng tổng thu trừ tổng chi.
5. Hệ thống hiển thị bảng giao dịch, thông tin phân trang và khối tổng hợp. Danh mục đã xóa có nhãn "(đã xóa)"; phí giao dịch hiển thị thành dòng riêng với nhãn "phí của giao dịch [mã giao dịch phát sinh]".

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Khoảng ngày không hợp lệ

1. Tại bước 2, ngày bắt đầu sau ngày kết thúc.
2. Hệ thống từ chối lọc và thông báo: "Khoảng thời gian tra cứu không hợp lệ (ngày bắt đầu phải trước hoặc bằng ngày kết thúc)."
3. Quay lại bước 1.

##### Luồng ngoại lệ 3a: Không có bản ghi thỏa điều kiện lọc

1. Tại bước 3, không có giao dịch nào thỏa đồng thời các tiêu chí.
2. Hệ thống hiển thị bảng rỗng, tổng thu, tổng chi, dòng tiền thuần bằng 0.00 và thông báo: "Không có giao dịch nào thỏa mãn bộ lọc đã chọn."

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi phép tổng hợp dùng số thập phân chính xác.
2. Phí giao dịch là giao dịch `EXPENSE` nên được lọc, tính vào tổng chi và phân trang như mọi giao dịch thu chi.
3. Yêu cầu chức năng liên quan: `FR-TXN-03`.

### 6.7 UC-TXN-03: Chỉnh sửa giao dịch thu chi

Bảng thuộc tính đặc tả cơ chế hoàn nguyên và cập nhật thông tin của ca sử dụng Chỉnh sửa giao dịch thu chi (`UC-TXN-03`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2305}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7695}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần sửa số tiền, chi phí, ví, danh mục, ghi chú mà không làm
sai số dư. Hệ thống: chỉ ghi nhận khi cả hoàn nguyên giao dịch cũ và áp
dụng dữ liệu mới đều thành công, giữ Quy tắc bảo toàn số dư ví
(\texttt{BR-FIN-02}) luôn đúng. \\ \hline
Mô tả tóm tắt &
Sửa một giao dịch thu chi hiện có: hoàn nguyên số dư ví cũ và áp dụng
cho ví mới trong cùng một lần xử lý, kèm cập nhật phí giao dịch liên kết
(nếu có); ngăn ví bị âm. \\ \hline
Điều kiện kích hoạt &
Người dùng chọn chỉnh sửa tại một dòng giao dịch. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Giao dịch cần sửa tồn tại trong sổ giao dịch thu chi.
2. Người dùng đang ở màn hình quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu thất bại ở bất kỳ bước kiểm tra nào, giao dịch cũ và số dư các ví liên quan giữ nguyên; toàn bộ thao tác được hoàn nguyên.

#### Hậu điều kiện thành công

1. Giao dịch và phí liên kết (nếu có) được cập nhật.
2. Số dư ví cũ và ví mới đồng bộ đúng theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`) và không âm.

#### Luồng sự kiện chính

1. Người dùng chọn chỉnh sửa một giao dịch từ lịch sử.
2. Hệ thống tải dữ liệu hiện tại lên biểu mẫu (Ngày giao dịch, Loại giao dịch, Ví, Danh mục chi tiết, Số tiền, Chi phí, Danh mục phí, Ghi chú).
3. Người dùng sửa các trường cần thiết và nhấn "Lưu thay đổi".
4. Hệ thống kiểm tra: số tiền mới lớn hơn 0, chi phí mới không âm, danh mục tương thích loại giao dịch theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`). Nếu đổi danh mục thì danh mục mới phải đang dùng; được giữ danh mục cũ kể cả khi đã xóa. Nếu chi phí mới lớn hơn 0 thì cần danh mục phí `EXPENSE` đang dùng; được giữ danh mục phí cũ kể cả khi đã xóa.
5. Hệ thống tính thử hoàn nguyên ví cũ theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`): giao dịch cũ `INCOME` thì trừ số tiền cũ, cộng lại chi phí cũ và kiểm tra số dư không âm; giao dịch cũ `EXPENSE` thì hoàn lại số tiền cũ và chi phí cũ.
6. Hệ thống tính thử áp dụng vào ví mới theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`), trên số dư sau khi hoàn nguyên ví cũ: `EXPENSE` thì số dư phải đủ trừ số tiền mới và chi phí mới; `INCOME` thì số dư sau khi cộng số tiền mới, trừ chi phí mới không âm.
7. Hệ thống thực hiện, chỉ ghi nhận khi tất cả thành công: hoàn nguyên số dư ví cũ; cập nhật số dư ví mới; ghi đè thông tin vào giao dịch; xử lý phí liên kết (xóa nếu chi phí mới bằng 0 và đang có phí; tạo mới nếu chi phí mới lớn hơn 0 và chưa có phí; cập nhật nếu cả hai lớn hơn 0).
8. Hệ thống lưu tệp dữ liệu, thông báo thành công và làm mới lịch sử.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 1a: Sửa trực tiếp phí giao dịch

1. Tại bước 1, Người dùng chọn sửa tại một dòng phí giao dịch.
2. Hệ thống từ chối và thông báo: "Phí giao dịch không được sửa trực tiếp. Vui lòng sửa giao dịch thu chi phát sinh ra phí này, hoặc xóa giao dịch chuyển tiền rồi tạo lại nếu phí thuộc giao dịch chuyển tiền."
3. Luồng kết thúc, dữ liệu giữ nguyên.

##### Luồng ngoại lệ 1b: Không tìm thấy giao dịch

1. Tại bước 1, hệ thống không tìm thấy giao dịch theo mã.
2. Hệ thống thông báo: "Không tìm thấy giao dịch yêu cầu."
3. Luồng kết thúc, dữ liệu giữ nguyên.

##### Luồng ngoại lệ 4a: Số tiền mới không hợp lệ

1. Tại bước 4, số tiền sai định dạng số học hoặc nhỏ hơn hoặc bằng 0.
2. Hệ thống từ chối: sai định dạng báo `ERR-FIN-001`; nhỏ hơn hoặc bằng 0 thì báo số tiền giao dịch phải lớn hơn 0.
3. Quay lại bước 3.

##### Luồng ngoại lệ 4b: Chi phí không hợp lệ hoặc thiếu danh mục phí

1. Tại bước 4, chi phí mới nhỏ hơn 0; hoặc chi phí mới lớn hơn 0, giao dịch chưa có phí và chưa chọn danh mục phí; hoặc danh mục phí mới đã xóa hoặc không thuộc `EXPENSE`.
2. Hệ thống từ chối: `ERR-FEE-001`, `ERR-FEE-002` hoặc `ERR-FEE-003` tương ứng.
3. Quay lại bước 3.

##### Luồng ngoại lệ 4c: Ví mới không ở trạng thái hoạt động

1. Tại bước 4, ví mới không tồn tại hoặc đang `ARCHIVED`.
2. Hệ thống từ chối và thông báo: "Ví được chọn không ở trạng thái hoạt động hoặc không tồn tại."
3. Quay lại bước 3.

##### Luồng ngoại lệ 4d: Loại danh mục không khớp loại giao dịch gốc

1. Tại bước 4, Người dùng đổi sang danh mục có Loại thu chi khác với giao dịch gốc.
2. Hệ thống từ chối và thông báo: "Loại danh mục không khớp với loại giao dịch."
3. Quay lại bước 3.

##### Luồng ngoại lệ 4e: Chưa có danh mục chi tiêu để chọn cho phí giao dịch

1. Tại bước 4, chi phí mới lớn hơn 0, giao dịch chưa có phí, nhưng loại `EXPENSE` chưa có danh mục chi tiết đang dùng nào. Luồng này được kiểm tra trước luồng 4b.
2. Hệ thống xử lý như luồng ngoại lệ 7f của `UC-TXN-01` (thông báo, đề nghị mở quản lý danh mục theo `UC-CAT-01`).
3. Nếu Người dùng từ chối, quay lại bước 3 để đặt chi phí bằng 0.

##### Luồng ngoại lệ 5a: Hoàn nguyên khoản thu làm âm số dư ví cũ

1. Tại bước 5, việc trừ số tiền cũ và cộng lại chi phí cũ (nếu có) làm số dư ví cũ âm.
2. Hệ thống từ chối (`ERR-WAL-001`); Người dùng cần kiểm tra các khoản chi liên quan của ví cũ trước khi sửa.
3. Quay lại bước 3 hoặc Người dùng hủy thao tác.

##### Luồng ngoại lệ 6a: Số dư ví mới không đủ cho khoản chi mới

1. Tại bước 6, số dư ví mới (sau khi hoàn nguyên ví cũ) không đủ trừ số tiền mới và chi phí mới.
2. Hệ thống từ chối (`ERR-WAL-001`); Người dùng chọn ví khác hoặc giảm số tiền.
3. Quay lại bước 3.

##### Luồng ngoại lệ 6b: Số dư ví mới không đủ khi chi phí lớn hơn số tiền thu nhập mới

1. Tại bước 6, giao dịch mới là thu nhập và số dư ví mới sau khi cộng số tiền mới, trừ chi phí mới nhỏ hơn 0.
2. Hệ thống từ chối (`ERR-WAL-001`); Người dùng chọn ví khác hoặc giảm chi phí.
3. Quay lại bước 3.

#### Quy tắc nghiệp vụ đặc thù

1. Hoàn nguyên ví cũ, ghi nhận ví mới và cập nhật phí liên kết phải hoàn tất cùng lúc; nếu lỗi lưu trữ, toàn bộ trạng thái tự khôi phục về trước khi sửa.
2. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): số dư ví không được âm tại bất kỳ thời điểm nào của quá trình chuyển đổi.
3. Phí giao dịch chỉ được sửa cùng giao dịch phát sinh ra nó, không sửa trực tiếp.
4. Yêu cầu chức năng liên quan: `FR-TXN-04`.

### 6.8 UC-TXN-04: Xóa giao dịch thu chi

Bảng thuộc tính đặc tả quy trình hoàn nguyên dòng tiền và điều kiện kích hoạt của ca sử dụng Xóa giao dịch thu chi (`UC-TXN-04`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2351}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7649}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần loại bỏ giao dịch ghi sai hoặc đã hủy. Hệ thống: hoàn
nguyên đúng số dư ví (gồm cả chi phí nếu có phí liên kết) và ngăn ví âm
do xóa khoản thu đã được chi tiêu ở các giao dịch sau. \\ \hline
Mô tả tóm tắt &
Xóa hoàn toàn một giao dịch thu chi cùng phí liên kết (nếu có), tự động
hoàn nguyên số dư ví và cập nhật các chỉ số thống kê dòng tiền. \\ \hline
Điều kiện kích hoạt &
Người dùng chọn xóa tại một dòng giao dịch. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Giao dịch cần xóa tồn tại.
2. Người dùng đang ở màn hình quản lý giao dịch thu chi.

#### Bảo đảm tối thiểu

Nếu bị từ chối do vi phạm điều kiện số dư hoặc Người dùng hủy xác nhận, giao dịch, phí liên kết và số dư ví giữ nguyên.

#### Hậu điều kiện thành công

1. Giao dịch và phí liên kết (nếu có) bị xóa khỏi sổ giao dịch thu chi.
2. Số dư ví được hoàn nguyên đúng theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`): xóa chi tiêu thì cộng hoàn trả số tiền và chi phí; xóa thu nhập thì trừ số tiền và cộng hoàn trả chi phí.
3. Báo cáo thống kê tháng và số dư ví được cập nhật ngay.

#### Luồng sự kiện chính

1. Người dùng chọn xóa một giao dịch từ lịch sử.
2. Hệ thống hiển thị hộp thoại xác nhận gồm Ngày giao dịch, Danh mục, Số tiền, Chi phí (nếu có), Ví và tác động hoàn nguyên số dư dự kiến.
3. Người dùng nhấn "Xác nhận xóa".
4. Hệ thống kiểm tra điều kiện số dư theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`): giao dịch `EXPENSE` thì ví được cộng hoàn trả nên không có nguy cơ âm; giao dịch `INCOME` thì số dư khả dụng hiện tại sau khi trừ số tiền và cộng lại chi phí phải không âm.
5. Hệ thống thực hiện, chỉ ghi nhận khi tất cả thành công: hoàn nguyên số dư ví; xóa giao dịch và phí liên kết (nếu có) khỏi sổ.
6. Hệ thống cập nhật các chỉ số tổng hợp của tháng quan sát.
7. Hệ thống lưu tệp dữ liệu, thông báo thành công và làm mới lịch sử.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 1a: Xóa trực tiếp phí giao dịch

1. Tại bước 1, Người dùng chọn xóa tại một dòng phí giao dịch.
2. Hệ thống từ chối và thông báo: "Phí giao dịch không được xóa trực tiếp. Vui lòng xóa giao dịch thu chi phát sinh ra phí này, hoặc xóa giao dịch chuyển tiền nếu phí thuộc giao dịch chuyển tiền."
3. Luồng kết thúc, dữ liệu giữ nguyên.

##### Luồng ngoại lệ 1b: Không tìm thấy giao dịch

1. Tại bước 1, hệ thống không tìm thấy giao dịch theo mã.
2. Hệ thống thông báo: "Không tìm thấy giao dịch yêu cầu."
3. Luồng kết thúc, dữ liệu giữ nguyên.

##### Luồng ngoại lệ 3a: Người dùng hủy xóa

1. Tại bước 3, Người dùng chọn "Hủy" hoặc đóng hộp thoại.
2. Hệ thống đóng hộp thoại, giữ nguyên giao dịch và số dư các ví.
3. Luồng kết thúc.

##### Luồng ngoại lệ 4a: Xóa khoản thu làm âm số dư ví

1. Tại bước 4, giao dịch là `INCOME` và số dư khả dụng hiện tại (sau khi cộng lại chi phí) nhỏ hơn số tiền giao dịch, vì tiền đã được dùng ở các giao dịch chi tiêu hoặc chuyển tiền sau đó.
2. Hệ thống hủy xóa (`ERR-WAL-001`); Người dùng cần kiểm tra các khoản chi liên quan của ví trước khi thực hiện.
3. Luồng kết thúc, dữ liệu giữ nguyên.

#### Quy tắc nghiệp vụ đặc thù

1. Xóa bản ghi (kể cả phí liên kết) và hoàn nguyên số dư diễn ra đồng thời; nếu lỗi lưu trữ, hệ thống tự khôi phục về trước khi xóa.
2. Quy tắc bảo toàn số dư ví (`BR-FIN-02`): không cho xóa khoản thu nếu làm số dư khả dụng của ví âm.
3. Phí giao dịch bị xóa cùng giao dịch phát sinh ra nó, không xóa trực tiếp.
4. Yêu cầu chức năng liên quan: `FR-TXN-05`.

### 6.9 UC-TRF-01: Chuyển tiền nội bộ giữa hai ví

Bảng thuộc tính đặc tả cơ chế ghi nhận đồng thời hai ví và điều kiện kích hoạt của ca sử dụng Chuyển tiền nội bộ giữa hai ví (`UC-TRF-01`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2336}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7664}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần chuyển tiền giữa các tài khoản nội bộ (tiền mặt, ví điện
tử) và ghi đúng phí. Hệ thống: ghi nhận đồng thời (trừ ví nguồn, cộng ví
đích); tổng số dư không đổi nếu không có phí, hoặc giảm đúng bằng phí
theo Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ
(\texttt{BR-FIN-03}). \\ \hline
Mô tả tóm tắt &
Điều chuyển tiền giữa hai ví nội bộ theo cơ chế ghi nhận đồng thời. Chi
phí chuyển tiền (nếu có) được tách thành một phí giao dịch độc lập liên
kết với giao dịch chuyển tiền. \\ \hline
Điều kiện kích hoạt &
Người dùng chọn chức năng chuyển tiền nội bộ. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Hệ thống có ít nhất hai ví `ACTIVE`.
2. Ví nguồn có số dư khả dụng lớn hơn 0.
3. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Nếu lỗi ở bất kỳ bước nào (ví dụ đã trừ ví nguồn nhưng cộng ví đích thất bại), toàn bộ giao dịch bị hủy ngay, số dư hai ví giữ nguyên.

#### Hậu điều kiện thành công

1. Giao dịch chuyển tiền được lưu với mã duy nhất.
2. Ví nguồn giảm đúng tổng số tiền chuyển và phí; ví đích tăng đúng số tiền chuyển, cùng lúc.
3. Nếu phí lớn hơn 0, một phí giao dịch độc lập được ghi vào sổ thu chi, liên kết với mã giao dịch chuyển tiền.
4. Tổng số dư các ví giảm đúng bằng phí; không có phí thì tổng giữ nguyên theo Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`).

#### Luồng sự kiện chính

1. Người dùng chọn "Chuyển tiền nội bộ".
2. Hệ thống hiển thị biểu mẫu gồm Ngày chuyển (mặc định ngày hiện tại), Ví nguồn, Ví đích, Số tiền chuyển, Phí chuyển tiền (mặc định 0), Danh mục phí (bắt buộc khi phí lớn hơn 0, không có mặc định), Ghi chú mô tả.
3. Người dùng chọn Ví nguồn từ các ví `ACTIVE`; hệ thống hiển thị số dư khả dụng hiện tại của ví nguồn.
4. Người dùng chọn Ví đích từ các ví `ACTIVE`.
5. Người dùng nhập số tiền chuyển (lớn hơn 0).
6. Người dùng nhập phí chuyển tiền (không âm) nếu có; nếu phí lớn hơn 0, chọn một danh mục `EXPENSE` đang dùng cho phí.
7. Người dùng nhập ghi chú (nếu có) và nhấn "Thực hiện chuyển tiền".
8. Hệ thống kiểm tra:
   - Ví nguồn khác ví đích.
   - Số tiền chuyển lớn hơn 0 và phí không âm, chuẩn hóa số thập phân chính xác 2 chữ số theo Quy tắc độ chính xác tài chính (`BR-FIN-01`).
   - Số dư ví nguồn không nhỏ hơn tổng số tiền chuyển và phí theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`).
   - Nếu phí lớn hơn 0: đã chọn danh mục phí, đang dùng và thuộc `EXPENSE`.
9. Hệ thống ghi nhận đồng thời theo Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): trừ số tiền chuyển khỏi ví nguồn; cộng số tiền chuyển vào ví đích; lưu giao dịch chuyển tiền với mã duy nhất.
10. Nếu phí lớn hơn 0, hệ thống sinh một phí giao dịch loại `EXPENSE` trong sổ thu chi, số tiền bằng phí, mang danh mục phí đã chọn và mã liên kết tới giao dịch chuyển tiền; khoản phí được trừ tiếp khỏi ví nguồn.
11. Hệ thống lưu tệp dữ liệu và thông báo thành công, kèm số tiền chuyển, phí đã trừ, số dư mới của ví nguồn và ví đích.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 6a: Chuyển tiền không mất phí

1. Tại bước 6, Người dùng giữ phí bằng 0 (hoặc để trống).
2. Hệ thống chỉ ghi hai khoản: trừ số tiền chuyển ở ví nguồn và cộng ở ví đích.
3. Hệ thống không tạo phí giao dịch; tổng số dư các ví trước và sau giữ nguyên.
4. Tiếp tục tại bước 11.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 8a: Ví nguồn trùng ví đích

1. Tại bước 8, hệ thống phát hiện ví nguồn trùng ví đích.
2. Hệ thống chặn, đánh dấu trường ví đích (`ERR-TRF-001`); Người dùng cần chọn hai ví khác nhau.
3. Quay lại bước 4.

##### Luồng ngoại lệ 8b: Số tiền chuyển hoặc chi phí không hợp lệ

1. Tại bước 8, giá trị sai định dạng số học, số tiền chuyển nhỏ hơn hoặc bằng 0, hoặc phí nhỏ hơn 0.
2. Hệ thống từ chối: sai định dạng báo `ERR-FIN-001`; số tiền chuyển nhỏ hơn hoặc bằng 0 báo `ERR-TRF-002`; phí âm báo `ERR-FEE-001`.
3. Quay lại bước 5.

##### Luồng ngoại lệ 8c: Số dư ví nguồn không đủ

1. Tại bước 8, tổng số tiền chuyển và phí vượt số dư khả dụng của ví nguồn.
2. Hệ thống từ chối (`ERR-WAL-001`); Người dùng giảm số tiền chuyển hoặc chọn ví nguồn khác.
3. Quay lại bước 5.

##### Luồng ngoại lệ 8d: Thiếu hoặc sai danh mục phí

1. Tại bước 8, phí lớn hơn 0 nhưng chưa chọn danh mục phí; hoặc danh mục đã xóa hoặc không thuộc `EXPENSE`.
2. Hệ thống từ chối: thiếu danh mục phí báo `ERR-FEE-002`; danh mục phí đã xóa hoặc sai loại báo `ERR-FEE-003`.
3. Quay lại bước 6.

##### Luồng ngoại lệ 8e: Chưa có danh mục chi tiêu để chọn cho phí giao dịch

1. Tại bước 8, phí lớn hơn 0 nhưng loại `EXPENSE` chưa có danh mục chi tiết đang dùng nào. Luồng này được kiểm tra trước luồng 8d.
2. Hệ thống xử lý như luồng ngoại lệ 7f của `UC-TXN-01` (thông báo, đề nghị mở quản lý danh mục theo `UC-CAT-01`).
3. Nếu Người dùng từ chối, quay lại bước 6 để đặt phí bằng 0.

##### Luồng ngoại lệ 9a: Lỗi một đầu ví khi ghi nhận đồng thời

1. Trong bước 9 hoặc 10, đã trừ ví nguồn nhưng cộng ví đích bị gián đoạn do lỗi kỹ thuật hoặc lỗi ghi tệp.
2. Hệ thống hoàn nguyên toàn bộ giao dịch, hủy việc trừ tiền ở ví nguồn, đưa hai ví về trạng thái ban đầu.
3. Hệ thống ghi nhật ký lỗi và thông báo: "Giao dịch chuyển tiền bị gián đoạn và đã được phục hồi nguyên trạng để giữ nguyên dữ liệu số dư. Vui lòng thử lại sau."

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): tổng số dư toàn bộ các ví luôn giữ nguyên đối với giao dịch chuyển tiền thuần túy.
2. Chi phí phải được ghi thành phí giao dịch `EXPENSE` riêng, không khấu trừ gộp làm sai số tiền ví đích thực nhận.
3. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi bước trừ cộng số dư dùng số thập phân chính xác.
4. Yêu cầu chức năng liên quan: `FR-TRF-01`.

### 6.10 UC-TRF-02: Tra cứu lịch sử chuyển tiền

Bảng thuộc tính đặc tả phạm vi theo dõi lịch sử chuyển khoản của ca sử dụng Tra cứu lịch sử chuyển tiền (`UC-TRF-02`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2198}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7802}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần rà soát các giao dịch chuyển tiền giữa các ví để so khớp
số dư. \\ \hline
Mô tả tóm tắt &
Hiển thị danh sách các giao dịch chuyển tiền nội bộ theo trình tự thời
gian, gồm ví nguồn, ví đích, số tiền chuyển, chi phí và ghi chú. \\ \hline
Điều kiện kích hoạt &
Người dùng chọn tab ``Lịch sử chuyển tiền'' trên màn hình Chuyển tiền
nội bộ. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Hệ thống đã đọc tệp dữ liệu thành công.
2. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Hệ thống chỉ đọc, không thay đổi sổ chuyển tiền hay số dư ví.

#### Hậu điều kiện thành công

Danh sách lịch sử hiển thị theo ngày giảm dần, phản ánh toàn bộ bản ghi trong sổ chuyển tiền (`FR-TRF-02`).

#### Luồng sự kiện chính

1. Người dùng mở tab "Lịch sử chuyển tiền".
2. Hệ thống truy vấn sổ chuyển tiền, sắp xếp theo ngày giảm dần.
3. Hệ thống hiển thị bảng gồm Mã giao dịch chuyển tiền, Ngày thực hiện, Ví nguồn, Ví đích, Số tiền chuyển, Chi phí, Ghi chú mô tả.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Chưa có giao dịch chuyển tiền nào

1. Tại bước 2, sổ chuyển tiền rỗng.
2. Hệ thống hiển thị bảng rỗng kèm thông báo: "Chưa có giao dịch chuyển tiền nội bộ nào được ghi nhận."

#### Quy tắc nghiệp vụ đặc thù

1. Số tiền chuyển và chi phí hiển thị phải khớp chính xác với các khoản đã ghi ở ví nguồn và ví đích (theo Quy tắc bảo toàn số dư ví `BR-FIN-02` và Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ `BR-FIN-03`).
2. Yêu cầu chức năng liên quan: `FR-TRF-02`.

### 6.11 UC-TRF-03: Xóa giao dịch chuyển tiền

Bảng thuộc tính đặc tả quy trình hoàn nguyên kép giữa hai ví của ca sử dụng Xóa giao dịch chuyển tiền (`UC-TRF-03`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2198}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7802}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần xóa giao dịch chuyển tiền nhầm và đưa số dư hai ví về
như cũ. Hệ thống: hoàn nguyên đồng thời, chỉ cho xóa khi ví đích còn đủ
số dư hoàn trả, giữ Quy tắc bảo toàn số dư ví (\texttt{BR-FIN-02}) và
Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (\texttt{BR-FIN-03})
luôn đúng. \\ \hline
Mô tả tóm tắt &
Xóa một giao dịch chuyển tiền đã thực hiện, tự động hoàn nguyên số dư ví
nguồn và ví đích; bản ghi chuyển tiền và phí liên kết (nếu có) bị xóa
khỏi sổ. \\ \hline
Điều kiện kích hoạt &
Người dùng chọn xóa tại một dòng trong bảng lịch sử chuyển tiền. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Giao dịch chuyển tiền cần xóa tồn tại trong sổ chuyển tiền.
2. Người dùng đang ở màn hình Chuyển tiền nội bộ.

#### Bảo đảm tối thiểu

Nếu ví đích không đủ số dư hoàn trả, hoàn nguyên thất bại hoặc Người dùng hủy, giao dịch chuyển tiền, phí liên kết và số dư hai ví giữ nguyên.

#### Hậu điều kiện thành công

1. Ví nguồn được cộng trả tổng số tiền chuyển và phí; ví đích bị trừ số tiền chuyển.
2. Phí liên kết (nếu có) bị xóa khỏi sổ thu chi; bản ghi chuyển tiền bị xóa khỏi sổ chuyển tiền.

#### Luồng sự kiện chính

1. Tại bảng lịch sử, Người dùng nhấn "Xóa giao dịch" ở một giao dịch chuyển tiền.
2. Hệ thống hiển thị hộp thoại xác nhận, liệt kê các khoản hoàn nguyên: cộng trả số tiền chuyển (và phí nếu có) vào ví nguồn, trừ số tiền chuyển khỏi ví đích.
3. Người dùng nhấn "Xác nhận xóa giao dịch".
4. Hệ thống truy xuất giao dịch theo mã và kiểm tra số dư khả dụng hiện tại của ví đích phải lớn hơn hoặc bằng số tiền đã nhận (`FR-TRF-02`).
5. Hệ thống hoàn nguyên cả hai ví, chỉ ghi nhận khi cả hai thành công: bỏ giao dịch chuyển tiền khỏi công thức tính lại số dư, nên ví nguồn được hoàn tiền chuyển và ví đích bị thu hồi mà không ghi bút toán riêng; xóa phí liên kết (nếu có) khỏi sổ thu chi, qua đó hoàn phí về ví nguồn; xóa bản ghi chuyển tiền khỏi sổ chuyển tiền.
6. Hệ thống lưu tệp dữ liệu, thông báo thành công và làm mới số dư hai ví.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3a: Người dùng hủy xóa

1. Tại bước 3, Người dùng chọn "Hủy" hoặc đóng hộp thoại.
2. Hệ thống đóng hộp thoại, giữ nguyên giao dịch, phí liên kết và số dư hai ví.
3. Luồng kết thúc.

##### Luồng ngoại lệ 4a: Không tìm thấy giao dịch chuyển tiền

1. Tại bước 4, hệ thống không tìm thấy mã trong sổ chuyển tiền.
2. Hệ thống thông báo: "Không tìm thấy giao dịch chuyển tiền yêu cầu."
3. Luồng kết thúc, dữ liệu giữ nguyên.

##### Luồng ngoại lệ 4b: Ví đích không đủ số dư để hoàn nguyên

1. Tại bước 4, số dư khả dụng ví đích nhỏ hơn số tiền cần hoàn trả vì ví đích đã chi tiêu số tiền đó.
2. Hệ thống từ chối xóa (`ERR-WAL-001`); Người dùng cần nạp thêm tiền vào ví đích hoặc điều chỉnh các giao dịch chi tiêu liên quan trước khi xóa.
3. Luồng kết thúc, dữ liệu giữ nguyên.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): hoàn nguyên phải khôi phục chính xác cả ví nguồn và ví đích; một ví thất bại thì cả hai không thay đổi.
2. Yêu cầu chức năng liên quan: `FR-TRF-02`.

### 6.12 UC-CAT-01: Bổ sung danh mục phân loại mới

Bảng thuộc tính đặc tả cấu trúc phân cấp danh mục 3 cấp của ca sử dụng Bổ sung danh mục phân loại mới (`UC-CAT-01`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2165}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7835}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần mở rộng cấu trúc danh mục để theo dõi chi tiết các khoản
chi hoặc nguồn thu đặc thù. Hệ thống: giữ cấu trúc 3 cấp theo Quy tắc
phân cấp danh mục thu chi 3 cấp (\texttt{BR-FIN-04}), danh mục luôn có
nhóm cha, tên hợp lệ và duy nhất theo Quy tắc tên của ví, nhóm nhu cầu
và danh mục (\texttt{BR-NAM-01}). \\ \hline
Mô tả tóm tắt &
Thêm một danh mục chi tiết (cấp 3) vào một nhóm nhu cầu đang dùng (cấp
2), hoặc thêm một nhóm nhu cầu mới vào một Loại thu chi (cấp 1). Mục mới
ở trạng thái đang dùng và danh sách chọn của biểu mẫu giao dịch được cập
nhật. \\ \hline
Điều kiện kích hoạt &
Người dùng chọn thêm danh mục trên màn hình quản lý danh mục thu chi. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Cấu trúc danh mục đã được đọc (2 Loại thu chi gốc; mỗi loại có ít nhất 1 nhóm).
2. Người dùng đang ở màn hình quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu lỗi kiểm tra hoặc lỗi ghi tệp, cấu trúc danh mục giữ nguyên, không sinh bản ghi rác.

#### Hậu điều kiện thành công

1. Danh mục mới thuộc đúng nhóm cha, kế thừa Loại thu chi của nhóm cha, lưu ở trạng thái đang dùng.
2. Cấu trúc 3 cấp được giữ: mọi danh mục thuộc một nhóm cha theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`), tên không trùng trong cùng nhóm cha theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`).

#### Luồng sự kiện chính

1. Người dùng mở "Quản lý danh mục thu chi", trực tiếp hoặc qua đề nghị từ UC giao dịch.
2. Hệ thống hiển thị cấu trúc 3 cấp: cấp 1 là hai nhánh `INCOME` và `EXPENSE`; cấp 2 là các nhóm nhu cầu; cấp 3 là các danh mục chi tiết.
3. Người dùng chọn một nhóm nhu cầu (ví dụ nhóm thuộc `EXPENSE`) và nhấn "Thêm danh mục con".
4. Hệ thống hiển thị biểu mẫu gồm Nhóm cha (chỉ đọc), Tên danh mục, Biểu tượng và màu sắc (tùy chọn).
5. Người dùng nhập tên, chọn biểu tượng và nhấn "Lưu danh mục".
6. Hệ thống kiểm tra: tên theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`) (sai định dạng `ERR-NAM-001`; trùng danh mục đang dùng trong cùng nhóm cha `ERR-NAM-002`); nhóm cha tồn tại và đang dùng.
7. Hệ thống tạo bản ghi danh mục với mã duy nhất, liên kết nhóm cha, trạng thái đang dùng.
8. Hệ thống làm mới cấu trúc danh mục, tự mở nhánh nhóm cha để làm nổi bật danh mục mới.
9. Hệ thống cập nhật danh sách chọn tại các biểu mẫu giao dịch.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Thêm nhóm nhu cầu mới

1. Tại bước 3, Người dùng chọn một Loại thu chi và nhấn "Thêm nhóm".
2. Hệ thống hiển thị biểu mẫu gồm Loại thu chi (chỉ đọc), Tên nhóm, Biểu tượng và màu sắc (tùy chọn).
3. Người dùng nhập tên nhóm và nhấn "Lưu nhóm". Hệ thống kiểm tra tên theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`): sai định dạng `ERR-NAM-001`; trùng nhóm đang dùng trong cùng Loại thu chi `ERR-NAM-002`.
4. Hệ thống tạo nhóm với mã duy nhất, trạng thái đang dùng. Tiếp tục tại bước 8 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 6a: Trùng tên danh mục trong cùng nhóm

1. Tại bước 6, tên trùng một danh mục đang dùng khác trong cùng nhóm cha (`ERR-NAM-002`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác."
3. Quay lại bước 5.

##### Luồng ngoại lệ 6b: Nhóm cha không tồn tại, đã xóa hoặc lỗi liên kết

1. Tại bước 6, nhóm cha đang ở trạng thái đã xóa, hoặc mã nhóm cha không tồn tại hoặc lỗi liên kết.
2. Hệ thống từ chối: nhóm cha đã xóa thì báo `ERR-CAT-005` với thông báo "Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng hoặc khôi phục nhóm trước."; nhóm cha không tồn tại hoặc lỗi liên kết thì báo "Không tìm thấy nhóm nhu cầu cha hợp lệ."
3. Luồng kết thúc, thao tác bị hủy.

##### Luồng ngoại lệ 6c: Tên trùng với mục đã xóa

1. Tại bước 6 (hoặc bước 3 của luồng rẽ nhánh 3a), tên trùng một hoặc nhiều danh mục hoặc nhóm đã xóa trong cùng phạm vi.
2. Hệ thống hiển thị hộp thoại cho Người dùng chọn Khôi phục mục cũ (giữ mã cũ, nối lại giao dịch cũ) hoặc Tạo mới (mã mới). Nếu có nhiều mục đã xóa cùng tên, hộp thoại liệt kê các mục (tên và số giao dịch còn gắn) để chọn đúng một mục cần khôi phục.
3. Nếu chọn Khôi phục, chuyển sang `UC-CAT-03` kèm mục đã chọn. Nếu chọn Tạo mới, tạo bản ghi với mã mới tại bước 7 (danh mục) hoặc bước 4 của luồng rẽ nhánh 3a (nhóm).

##### Luồng ngoại lệ 6d: Trùng tên nhóm trong cùng Loại thu chi

1. Tại bước 3 của luồng rẽ nhánh 3a, tên nhóm trùng một nhóm đang dùng khác trong cùng Loại thu chi (`ERR-NAM-002`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác."
3. Quay lại bước 3 của luồng rẽ nhánh 3a.

##### Luồng ngoại lệ 6e: Tên sai định dạng

1. Tại bước 6 (hoặc bước 3 của luồng rẽ nhánh 3a), tên sai định dạng (`ERR-NAM-001`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Quay lại bước 5 (danh mục) hoặc bước 3 của luồng rẽ nhánh 3a (nhóm).

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): mỗi Loại thu chi có ít nhất 1 nhóm đang dùng, mọi danh mục thuộc đúng một nhóm cha; cấm nút thiếu nhóm cha hoặc gán sai cấp.
2. Yêu cầu chức năng liên quan: `FR-CAT-01`, `FR-CAT-02`.

### 6.13 UC-CAT-02: Đổi tên danh mục

Bảng thuộc tính đặc tả phạm vi đổi tên và chuyển nhóm của ca sử dụng Đổi tên danh mục (`UC-CAT-02`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2185}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7815}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần chỉnh tên danh mục cho rõ hơn. Hệ thống: giữ nguyên liên
kết với giao dịch quá khứ, giao dịch lịch sử hiển thị tên mới mà không
đổi số tiền; tên mới hợp lệ và duy nhất theo Quy tắc tên của ví, nhóm
nhu cầu và danh mục (\texttt{BR-NAM-01}). \\ \hline
Mô tả tóm tắt &
Đổi tên hiển thị của một danh mục chi tiết hoặc một nhóm nhu cầu đang
dùng; giao dịch quá khứ không mất liên kết. UC này cũng cho chuyển một
danh mục sang nhóm khác cùng Loại thu chi. \\ \hline
Điều kiện kích hoạt &
Người dùng chọn đổi tên danh mục hoặc nhóm trên cấu trúc danh mục. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Danh mục hoặc nhóm cần đổi tên tồn tại và đang dùng.
2. Người dùng đang ở màn hình quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu lỗi kiểm tra hoặc Người dùng hủy, tên và liên kết lịch sử giữ nguyên.

#### Hậu điều kiện thành công

1. Tên mới được lưu vào tệp dữ liệu; với luồng chuyển nhóm, danh mục thuộc nhóm đích và giữ nguyên mã cùng giao dịch liên kết.
2. Giao dịch quá khứ giữ nguyên liên kết và hiển thị theo tên mới.

#### Luồng sự kiện chính

1. Người dùng chọn một danh mục hoặc nhóm và nhấn "Đổi tên".
2. Hệ thống hiển thị hộp thoại chỉnh tên, điền sẵn tên hiện tại.
3. Người dùng nhập tên mới và nhấn "Lưu thay đổi".
4. Hệ thống kiểm tra theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`): sai định dạng `ERR-NAM-001`; trùng mục đang dùng cùng cấp (danh mục trong cùng nhóm cha, nhóm trong cùng Loại thu chi) `ERR-NAM-002`.
5. Hệ thống cập nhật tên trong tệp dữ liệu. Giao dịch lịch sử phản ánh tên mới qua mã liên kết; với nhóm, các danh mục con giữ nguyên mã và liên kết giao dịch.
6. Hệ thống thông báo thành công (đổi tên hoặc chuyển nhóm) và làm mới cấu trúc danh mục.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Chuyển danh mục sang nhóm khác

1. Tại bước 1, Người dùng chọn một danh mục chi tiết và nhấn "Chuyển nhóm".
2. Hệ thống hiển thị các nhóm đang dùng cùng Loại thu chi với danh mục, đánh dấu nhóm hiện tại.
3. Người dùng chọn nhóm đích. Hệ thống kiểm tra nhóm đích đang dùng, cùng Loại thu chi, và tên danh mục không trùng danh mục đang dùng trong nhóm đích theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`).
4. Hệ thống hiển thị hộp xác nhận nêu số giao dịch của danh mục, nhóm cũ, nhóm mới, và cảnh báo rằng báo cáo theo nhóm của các kỳ đã qua sẽ tính số tiền này vào nhóm mới (tổng thu và tổng chi không đổi).
5. Người dùng nhấn "Xác nhận chuyển". Hệ thống cập nhật nhóm cha của danh mục, giữ nguyên mã và mọi giao dịch liên kết.
6. Tiếp tục tại bước 6 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Trùng tên danh mục trong cùng nhóm

1. Tại bước 4, tên mới trùng danh mục đang dùng khác trong cùng nhóm cha (`ERR-NAM-002`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác."
3. Quay lại bước 3.

##### Luồng ngoại lệ 4b: Trùng tên nhóm trong cùng Loại thu chi

1. Tại bước 4, tên mới của nhóm trùng nhóm đang dùng khác trong cùng Loại thu chi (`ERR-NAM-002`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác."
3. Quay lại bước 3.

##### Luồng ngoại lệ 4c: Tên mới sai định dạng

1. Tại bước 4, tên mới sai định dạng (`ERR-NAM-001`).
2. Hệ thống từ chối, đánh dấu trường tên và thông báo: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Quay lại bước 3.

##### Luồng ngoại lệ 1b: Nhóm đích đã xóa

1. Tại bước 3 của luồng rẽ nhánh 1a, nhóm đích đã xóa.
2. Hệ thống từ chối (`ERR-CAT-003`), giữ danh mục ở nhóm cũ.
3. Quay lại bước 3 của luồng rẽ nhánh 1a để chọn nhóm đích khác.

##### Luồng ngoại lệ 1c: Trùng tên trong nhóm đích

1. Tại bước 3 của luồng rẽ nhánh 1a, nhóm đích đã có danh mục đang dùng cùng tên (`ERR-NAM-002`).
2. Hệ thống từ chối và thông báo: "Nhóm đích đã có danh mục cùng tên. Hãy đổi tên danh mục trước khi chuyển."
3. Quay lại bước 3 của luồng rẽ nhánh 1a.

##### Luồng ngoại lệ 1d: Chuyển sang nhóm khác Loại thu chi

1. Tại bước 3 của luồng rẽ nhánh 1a, hệ thống nhận yêu cầu chuyển sang nhóm đích thuộc Loại thu chi khác (yêu cầu này không đi qua danh sách ở bước 2).
2. Hệ thống từ chối (`ERR-CAT-001`).
3. Quay lại bước 3 của luồng rẽ nhánh 1a.

#### Quy tắc nghiệp vụ đặc thù

1. Đổi tên không được phá liên kết với giao dịch lịch sử.
2. Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): cấm chuyển danh mục sang Loại thu chi khác và cấm đổi Loại của nhóm hoặc danh mục đã có giao dịch, để tránh sai lệch báo cáo dòng tiền quá khứ.
3. Báo cáo luôn gom theo nhóm hiện tại của danh mục, không lưu nhóm tại thời điểm ghi giao dịch. Nhóm nhu cầu không chuyển được vì cha của nhóm là Loại thu chi và Loại thu chi không đổi.
4. Yêu cầu chức năng liên quan: `FR-CAT-02`, `FR-CAT-03`.

### 6.14 UC-CAT-03: Khôi phục hoặc tạo lại mục đã xóa

Bảng thuộc tính đặc tả quy trình khôi phục mục đã ẩn của ca sử dụng Khôi phục hoặc tạo lại mục đã xóa (`UC-CAT-03`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2034}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7966}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần lấy lại nhóm hoặc danh mục xóa nhầm hoặc cần dùng lại,
và chọn nối lại lịch sử cũ hay bắt đầu mới. Hệ thống: khôi phục không
làm mất hay đổi dữ liệu lịch sử, không để hai mục đang dùng trùng tên
trong cùng phạm vi theo Quy tắc tên của ví, nhóm nhu cầu và danh mục
(\texttt{BR-NAM-01}). \\ \hline
Mô tả tóm tắt &
Từ màn hình ``Danh mục đã xóa'', Người dùng chọn một nhóm hoặc danh mục
đã xóa rồi chọn Khôi phục (giữ mã cũ, nối lại giao dịch cũ) hoặc Tạo mới
(mã mới, không nối giao dịch cũ). \\ \hline
Điều kiện kích hoạt &
Người dùng mở màn hình ``Danh mục đã xóa'', hoặc chọn Khôi phục tại hộp
hỏi khi tên mới trùng một mục đã xóa (\texttt{UC-CAT-01}). \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Có ít nhất một nhóm hoặc danh mục đang ở trạng thái đã xóa.
2. Người dùng đang ở màn hình quản lý danh mục thu chi, hoặc ở hộp hỏi Khôi phục hoặc Tạo mới của `UC-CAT-01`.

#### Bảo đảm tối thiểu

Nếu bị từ chối hoặc lỗi ghi, mục đã xóa giữ nguyên trạng thái và giao dịch lịch sử không đổi.

#### Hậu điều kiện thành công

1. Khôi phục: mục trở lại đang dùng với mã cũ, giao dịch cũ được nối lại, mục hiện lại trong danh sách chọn.
2. Tạo mới: mục mới đang dùng với mã mới; mục cũ vẫn đã xóa và giao dịch cũ vẫn gắn với mục cũ.
3. Báo cáo lịch sử không bị đứt đoạn trong cả hai trường hợp.

#### Luồng sự kiện chính

1. Người dùng mở "Danh mục đã xóa".
2. Hệ thống liệt kê các nhóm và danh mục đã xóa kèm tên, nơi chứa cũ và số giao dịch còn gắn.
3. Người dùng chọn một mục và nhấn "Khôi phục".
4. Hệ thống kiểm tra theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`): không có mục đang dùng nào trùng tên trong cùng phạm vi (danh mục trong cùng nhóm, nhóm trong cùng Loại thu chi); với danh mục, nhóm cha phải đang dùng.
5. Hệ thống chuyển mục sang đang dùng với mã cũ, nối lại giao dịch cũ và đưa mục trở lại danh sách chọn.
6. Hệ thống làm mới cấu trúc danh mục và thông báo khôi phục thành công.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 1a: Vào từ hộp hỏi của UC-CAT-01

1. Tại luồng ngoại lệ 6c của `UC-CAT-01`, Người dùng chọn Khôi phục.
2. Hệ thống nhận mục đã chọn từ `UC-CAT-01` (nếu có nhiều mục đã xóa cùng tên) hoặc lấy mục đã xóa duy nhất cùng tên, rồi bỏ qua các bước 1 đến 3.
3. Tiếp tục tại bước 4 của luồng chính.

##### Luồng rẽ nhánh 3a: Tạo mới thay vì khôi phục

1. Tại bước 3, Người dùng nhấn "Tạo mới".
2. Hệ thống yêu cầu nhập tên (điền sẵn tên cũ) và nơi chứa, kiểm tra tên theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`) (đúng định dạng, không trùng mục đang dùng trong cùng phạm vi). Bước này không hỏi lại Khôi phục hoặc Tạo mới.
3. Hệ thống tạo mục mới với mã mới, đang dùng; mục cũ giữ trạng thái đã xóa.
4. Tiếp tục tại bước 6 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 3b: Tên trùng hoặc nhóm cha đã xóa khi tạo mới

1. Tại bước 2 của luồng rẽ nhánh 3a, tên trùng mục đang dùng trong cùng phạm vi (`ERR-NAM-002`), hoặc nhóm cha của danh mục đang ở trạng thái đã xóa.
2. Hệ thống từ chối và thông báo theo trường hợp: "Tên danh mục này đã tồn tại trong nhóm. Vui lòng đặt tên khác." (danh mục trùng tên); "Tên nhóm này đã tồn tại trong loại thu chi. Vui lòng đặt tên khác." (nhóm trùng tên); hoặc báo `ERR-CAT-005` với thông báo "Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng hoặc khôi phục nhóm trước." (nhóm cha đã xóa).
3. Quay lại bước 2 của luồng rẽ nhánh 3a để nhập lại tên hoặc chọn nơi chứa khác.

##### Luồng ngoại lệ 3c: Tên sai định dạng khi tạo mới

1. Tại bước 2 của luồng rẽ nhánh 3a, tên sai định dạng (`ERR-NAM-001`).
2. Hệ thống từ chối và thông báo: "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."
3. Quay lại bước 2 của luồng rẽ nhánh 3a.

##### Luồng ngoại lệ 4a: Trùng tên với mục đang dùng

1. Tại bước 4, đã có mục đang dùng cùng tên trong cùng phạm vi.
2. Hệ thống từ chối (`ERR-NAM-002`) và thông báo: "Không thể khôi phục vì tên đã được dùng. Hãy đổi tên một trong hai mục rồi thử lại."
3. Luồng kết thúc, mục giữ trạng thái đã xóa.

##### Luồng ngoại lệ 4b: Nhóm cha vẫn đã xóa

1. Tại bước 4, Người dùng khôi phục một danh mục mà nhóm cha còn ở trạng thái đã xóa.
2. Hệ thống từ chối (`ERR-CAT-004`), yêu cầu khôi phục nhóm cha trước.
3. Luồng kết thúc, danh mục giữ trạng thái đã xóa.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): khôi phục giữ nguyên mã cũ nên không tạo dữ liệu mồ côi; mã định danh không bao giờ dùng lại cho mục khác.
2. Khôi phục nhóm chỉ khôi phục riêng nhóm; các danh mục con được khôi phục riêng từng cái.
3. Yêu cầu chức năng liên quan: `FR-CAT-02`, `FR-CAT-03`.

### 6.15 UC-CAT-04: Xóa danh mục hoặc nhóm

Bảng thuộc tính đặc tả cơ chế xóa cứng hoặc ẩn mềm của ca sử dụng Xóa danh mục hoặc nhóm (`UC-CAT-04`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2145}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7855}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: cần bỏ danh mục hoặc nhóm không còn dùng bằng một thao tác,
kể cả khi đã có giao dịch, mà không mất lịch sử. Hệ thống: không để giao
dịch trỏ tới danh mục không còn tồn tại và không để một Loại thu chi mất
hết nhóm. \\ \hline
Mô tả tóm tắt &
Người dùng chọn Xóa một danh mục hoặc nhóm. Mục chưa có giao dịch bị xóa
hẳn; mục đã có giao dịch chuyển sang trạng thái đã xóa và giao dịch được
giữ nguyên. Xóa nhóm kéo theo mọi danh mục con. \\ \hline
Điều kiện kích hoạt &
Người dùng chọn xóa trên một danh mục hoặc nhóm trong cấu trúc danh mục. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Danh mục hoặc nhóm cần xóa tồn tại và đang dùng.
2. Người dùng đang ở màn hình quản lý danh mục thu chi.

#### Bảo đảm tối thiểu

Nếu bị từ chối, bị hủy hoặc lỗi ghi, cấu trúc danh mục và mọi giao dịch giữ nguyên.

#### Hậu điều kiện thành công

1. Mục chưa có giao dịch bị xóa hoàn toàn khỏi tệp dữ liệu.
2. Mục đã có giao dịch ở trạng thái đã xóa: không còn trong danh sách chọn cho giao dịch mới, vẫn hiện trong báo cáo lịch sử kèm nhãn "(đã xóa)" và có thể khôi phục tại `UC-CAT-03`.
3. Khi xóa nhóm: mỗi danh mục con được xử lý theo các điểm trên; nhóm có giao dịch bên trong thì ở trạng thái đã xóa, nhóm không có thì bị xóa hẳn.

#### Luồng sự kiện chính

1. Người dùng chọn một danh mục hoặc nhóm và nhấn "Xóa".
2. Hệ thống quét sổ giao dịch thu chi để đếm giao dịch liên kết; với nhóm, đếm theo từng danh mục con đang dùng.
3. Hệ thống hiển thị hộp xác nhận. Với danh mục: nêu số giao dịch liên quan và kết quả (xóa hẳn nếu chưa có giao dịch; ẩn và giữ giao dịch nếu đã có). Với nhóm: nêu số danh mục sẽ bị ẩn, số danh mục xóa hẳn và tổng số giao dịch được giữ lại.
4. Người dùng nhấn "Xác nhận xóa".
5. Hệ thống xóa hoặc chuyển sang trạng thái đã xóa theo kết quả đã nêu, rồi cập nhật cấu trúc danh mục.
6. Hệ thống thông báo thành công; mục đã xóa có trong màn hình "Danh mục đã xóa".

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 2a: Xóa nhóm đang dùng cuối cùng của một Loại thu chi

1. Tại bước 2, nhóm được chọn là nhóm đang dùng cuối cùng của Loại thu chi chứa nó.
2. Hệ thống từ chối (`ERR-CAT-002`) và thông báo: "Không thể xóa nhóm cuối cùng của loại thu chi này."
3. Luồng kết thúc, cấu trúc danh mục giữ nguyên.

##### Luồng ngoại lệ 4a: Người dùng hủy xác nhận

1. Tại bước 4, Người dùng nhấn "Hủy".
2. Hệ thống đóng hộp xác nhận và không thay đổi gì.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): mục đã có giao dịch không bao giờ bị xóa vật lý, để không giao dịch nào mất danh mục.
2. Người dùng chỉ thấy một thao tác "Xóa"; xóa hẳn hay ẩn do hệ thống quyết định theo số giao dịch.
3. Yêu cầu chức năng liên quan: `FR-CAT-03`.

### 6.16 UC-REP-01: Thống kê dòng tiền 12 tháng gần nhất

Bảng thuộc tính đặc tả chu kỳ tổng hợp dòng tiền 12 tháng của ca sử dụng Thống kê dòng tiền 12 tháng gần nhất (`UC-REP-01`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2185}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7815}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: đánh giá tình hình tài chính định kỳ qua chuỗi dòng tiền 12
tháng và tỷ lệ tiết kiệm. \\ \hline
Mô tả tóm tắt &
Tổng hợp chuỗi dòng tiền 12 tháng gần nhất từ mốc tháng chỉ định thành
bảng thống kê theo \texttt{FR-REP-01}: thu nhập, chi tiêu, dòng tiền
thuần, tỷ lệ tiết kiệm theo Quy tắc độ chính xác tài chính
(\texttt{BR-FIN-01}). \\ \hline
Điều kiện kích hoạt &
Người dùng cần đánh giá dòng tiền định kỳ trong 12 tháng gần nhất. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Hệ thống đã khởi chạy và đọc tệp dữ liệu giao dịch thành công.
2. Người dùng đang ở giao diện nhóm chức năng Thống kê và báo cáo.

#### Bảo đảm tối thiểu

Hệ thống chỉ đọc, không thay đổi giao dịch hay số dư ví.

#### Hậu điều kiện thành công

1. Bảng thống kê gồm đúng 12 dòng liên tục lùi từ tháng kết thúc, không trống trường nào.
2. Dòng tiền thuần và tỷ lệ tiết kiệm tính đúng theo `FR-REP-01`; ba mức bình quân chia cho 12, kèm ghi chú rằng các tháng chưa có giao dịch vẫn được tính.

#### Luồng sự kiện chính

1. Người dùng chọn "Thống kê dòng tiền 12 tháng gần nhất", có thể tùy chọn mốc tháng và năm kết thúc (mặc định tháng và năm hiện tại).
2. Hệ thống xác định mốc tháng kết thúc (mặc định lấy từ đồng hồ hệ thống).
3. Hệ thống tạo danh sách 12 tháng liên tục từ 11 tháng trước mốc đến chính mốc, sắp xếp thời gian tăng dần (tháng kết thúc ở dòng cuối).
4. Với mỗi tháng, hệ thống lọc giao dịch có ngày trong tháng và tính tổng thu nhập, tổng chi tiêu (trên các giao dịch đang tồn tại).
5. Hệ thống tính các chỉ số phái sinh theo `FR-REP-01`: dòng tiền thuần bằng tổng thu trừ tổng chi; tỷ lệ tiết kiệm (`NetCashFlow_m` là dòng tiền thuần, `Income_m` là tổng thu nhập của tháng m):
   $$
   SavingsRate_m = \begin{cases} \left(\frac{NetCashFlow_m}{Income_m}\right) \times 100\%, & Income_m > 0 \\ 0.00\%, & Income_m \le 0 \end{cases}
   $$
   Làm tròn ngân hàng, hiển thị 2 chữ số thập phân.
6. Hệ thống tính thu nhập, chi tiêu và dòng tiền thuần bình quân của chuỗi (`FR-REP-01`); mẫu số luôn là 12 kể cả các tháng chưa có giao dịch, và hệ thống hiển thị ghi chú nêu điều này.
7. Hệ thống hiển thị bảng 12 dòng tháng; chân bảng có ba mức bình quân kèm ghi chú; giao diện hiển thị biểu đồ kết hợp cột thu chi và đường dòng tiền thuần.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Tháng hoàn toàn không có giao dịch

1. Tại bước 4, một tháng không có giao dịch thu chi.
2. Hệ thống hiển thị "0.00 VNĐ", không để trống ô dữ liệu.
3. Hệ thống gán dòng tiền thuần 0.00 và tỷ lệ tiết kiệm 0.00%, bảo đảm bảng 12 tháng liên tục và không gây lỗi giao diện.

##### Luồng ngoại lệ 5a: Tháng có thu nhập không dương nhưng có chi tiêu

1. Tại bước 5, tổng thu nhập của tháng nhỏ hơn hoặc bằng 0 nhưng tổng chi tiêu lớn hơn 0.
2. Chia trực tiếp sẽ gây lỗi chia cho 0 (`ERR-FIN-003`), nên hệ thống áp dụng hàm từng nhánh và gán tỷ lệ tiết kiệm 0.00%.
3. Hệ thống hiển thị dòng tiền thuần âm (bằng âm của tổng chi tiêu) và chỉ báo thâm hụt ngân sách.

#### Quy tắc nghiệp vụ đặc thù

1. `FR-REP-01`: bảng luôn gồm đúng 12 tháng liên tục lùi từ tháng kết thúc (mặc định tháng hiện tại).
2. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi chỉ số tính bằng số thập phân chính xác, làm tròn ngân hàng 2 chữ số, không có sai số dấu phẩy động.
3. Yêu cầu chức năng liên quan: `FR-REP-01`.

### 6.17 UC-REP-02: Phân tích cơ cấu thu chi theo nhóm và danh mục

Bảng thuộc tính đặc tả cơ cấu tỷ lệ chi tiêu theo nhóm và danh mục của ca sử dụng Phân tích cơ cấu thu chi theo nhóm và danh mục (`UC-REP-02`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2094}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7906}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: phân tích cơ cấu thu chi 2 tầng theo nhóm và danh mục, đối
chiếu biến động với kỳ liền trước. \\ \hline
Mô tả tóm tắt &
Phân tích cơ cấu thu chi theo nhóm nhu cầu và danh mục chi tiết trong
một khoảng thời gian chọn, kèm so sánh biến động với kỳ liền kề
(\texttt{FR-REP-02}). \\ \hline
Điều kiện kích hoạt &
Người dùng cần phân tích cơ cấu thu chi theo nhóm và danh mục của một kỳ
cụ thể. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Hệ thống đã khởi chạy và đọc tệp dữ liệu giao dịch thành công.
2. Người dùng đang ở giao diện nhóm chức năng Thống kê và báo cáo.

#### Bảo đảm tối thiểu

Hệ thống chỉ đọc, không thay đổi dữ liệu phân loại hay số dư ví.

#### Hậu điều kiện thành công

Báo cáo cơ cấu 2 tầng (Nhóm nhu cầu, Danh mục) phản ánh đúng tỷ lệ và biến động so với kỳ liền kề (`FR-REP-02`).

#### Luồng sự kiện chính

1. Người dùng chọn tab "Cơ cấu thu chi", khoảng thời gian (mặc định tháng hiện tại) và loại luồng tiền (`EXPENSE` hoặc `INCOME`).
2. Hệ thống quét các giao dịch đang tồn tại trong khoảng thời gian, thuộc loại luồng tiền đã chọn.
3. Hệ thống tổng hợp theo 2 tầng (`FR-REP-02`; `Amount_group`, `Amount_cat` là tổng tiền của nhóm và danh mục; `TotalPeriod` là tổng thu hoặc chi của kỳ):
   - Cấp nhóm nhu cầu:
     $$Share_{group} = \left(\frac{Amount_{group}}{TotalPeriod}\right) \times 100\%$$
   - Cấp danh mục chi tiết (tỷ lệ trên nhóm cha và trên toàn kỳ):
     $$Share_{cat/group} = \left(\frac{Amount_{cat}}{Amount_{group}}\right) \times 100\%, \quad Share_{cat/period} = \left(\frac{Amount_{cat}}{TotalPeriod}\right) \times 100\%$$
4. Hệ thống truy vấn kỳ liền trước cùng độ dài để so sánh: chênh lệch tuyệt đối bằng số tiền kỳ này trừ kỳ trước; tỷ lệ biến động bằng chênh lệch chia số tiền kỳ trước nhân 100 khi kỳ trước lớn hơn 0; nếu kỳ trước bằng 0 thì hiển thị nhãn "Mới".
5. Hệ thống sắp xếp nhóm và danh mục theo số tiền giảm dần.
6. Hệ thống hiển thị bảng tổng hợp phân cấp kèm biểu đồ tròn cơ cấu. Nhóm và danh mục đã xóa vẫn hiển thị kèm nhãn "(đã xóa)" (`FR-REP-02`).

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Kỳ trước không có giao dịch khi so sánh biến động

1. Tại bước 4, kỳ trước không có giao dịch trong danh mục nhưng kỳ này có.
2. Hệ thống hiển thị chênh lệch tuyệt đối bằng số tiền kỳ này và tỷ lệ biến động "Mới", tránh phép chia cho 0 (`ERR-FIN-003`).

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc độ chính xác tài chính (`BR-FIN-01`): mọi phép tổng hợp và tỷ lệ dùng số thập phân chính xác, làm tròn ngân hàng; tiền làm tròn 2 chữ số; tỷ lệ phần trăm giữ tối thiểu 4 chữ số thập phân ở bước trung gian và hiển thị 2 chữ số (`FR-REP-02`).
2. Nhóm và danh mục đã xóa giữ tên gốc kèm nhãn "(đã xóa)", không gộp vào dòng "Khác". Dòng số 0 chỉ hiện cho các kỳ quá khứ từ kỳ chứa ngày mục được tạo; kỳ trước ngày tạo và kỳ tương lai ẩn dòng.
3. Yêu cầu chức năng liên quan: `FR-REP-02`.

### 6.18 UC-DAT-01: Xuất dữ liệu tài chính

Bảng thuộc tính đặc tả cấu trúc định dạng dữ liệu xuất tệp của ca sử dụng Xuất dữ liệu tài chính (`UC-DAT-01`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2165}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7835}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: sao lưu dữ liệu tài chính cục bộ ra định dạng mở, giữ nguyên
độ chính xác số học, không làm tròn. \\ \hline
Mô tả tóm tắt &
Xuất dữ liệu (toàn phần hoặc sổ giao dịch) ra tệp JSON, giữ độ chính xác
các trường số học tài chính (\texttt{FR-DAT-01}). \\ \hline
Điều kiện kích hoạt &
Người dùng cần sao lưu định kỳ hoặc xuất dữ liệu ra tệp định dạng mở. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Hệ thống hoạt động bình thường, có quyền đọc và ghi trên thư mục lưu trữ chỉ định.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

Xuất dữ liệu không làm đổi dữ liệu hiện có. Nếu lỗi ghi đĩa, hệ thống dọn tệp xuất dở dang và giữ nguyên dữ liệu nội bộ.

#### Hậu điều kiện thành công

Tệp sao lưu được tạo với mã hóa UTF-8; các trường số học là chuỗi số giữ nguyên chữ số thập phân (không làm tròn, không dùng ký hiệu khoa học); tên tệp tự động theo `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json` (`FR-DAT-01`).

#### Luồng sự kiện chính

1. Người dùng chọn "Xuất dữ liệu tài chính".
2. Hệ thống hiển thị biểu mẫu gồm Phạm vi dữ liệu (`FULL` hoặc `TRANSACTIONS`), Định dạng tệp (`JSON`), Thư mục lưu trữ (chọn qua hộp thoại duyệt thư mục).
3. Người dùng chọn phạm vi `FULL`, định dạng `JSON` và thư mục đích.
4. Người dùng xác nhận bắt đầu xuất.
5. Hệ thống trích xuất toàn bộ dữ liệu hiện tại: ví, danh mục phân loại, giao dịch thu chi (kể cả phí giao dịch), chuyển tiền nội bộ và bản ghi điều chỉnh.
6. Hệ thống chuyển sang cấu trúc JSON: chuẩn hóa mọi trường số học tài chính thành chuỗi số chính xác (ví dụ `"50000000.00"`), cấm kiểu số thực có sai số hoặc ký hiệu số mũ như `5.0E7` (`FR-DAT-01`); ngày tháng theo `YYYY-MM-DD`.
7. Hệ thống tạo tên tệp `pfms_export_full_{yyyyMMdd_HHmmss}.json`.
8. Hệ thống ghi tệp xuống đĩa với bảng mã UTF-8 không BOM.
9. Hệ thống kiểm tra kích thước và độ nguyên vẹn của tệp vừa tạo.
10. Hệ thống thông báo thành công kèm đường dẫn, kích thước tệp và số bản ghi đã xuất của từng nhóm dữ liệu.

#### Các luồng rẽ nhánh

##### Luồng rẽ nhánh 3a: Xuất sổ giao dịch thu chi

1. Tại bước 3, Người dùng chọn phạm vi `TRANSACTIONS`, định dạng `JSON` và thư mục đích.
2. Hệ thống trích xuất toàn bộ sổ giao dịch thu chi, gồm cả phí giao dịch của các giao dịch chuyển tiền đang tồn tại.
3. Hệ thống tạo tệp JSON UTF-8 không BOM gồm danh sách giao dịch thu chi; mỗi bản ghi phí có trường mã giao dịch phát sinh ra phí (`FR-DAT-01`).
4. Tên tệp theo `pfms_export_transactions_{yyyyMMdd_HHmmss}.json`.
5. Tiếp tục tại bước 8 của luồng chính.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 8a: Lỗi quyền ghi tệp hoặc ổ đĩa đầy

1. Tại bước 8, hệ thống không ghi được tệp do thiếu quyền truy cập hoặc ổ đĩa hết dung lượng.
2. Hệ thống thông báo: "Không thể lưu tệp dữ liệu vào thư mục đã chọn do lỗi quyền truy cập hoặc ổ đĩa đầy. Vui lòng chọn thư mục khác."
3. Thao tác xuất bị hủy và không để lại tệp rác trên đĩa.

#### Quy tắc nghiệp vụ đặc thù

1. Quy tắc độ chính xác tài chính (`BR-FIN-01`), `FR-DAT-01`: không làm tròn hay dùng ký hiệu khoa học đối với trường số học tài chính khi xuất.
2. Yêu cầu chức năng liên quan: `FR-DAT-01`.

### 6.19 UC-DAT-02: Phục hồi dữ liệu hệ thống từ bản sao lưu

Bảng thuộc tính đặc tả quy trình xác thực dữ liệu và tự động sao lưu của ca sử dụng Phục hồi dữ liệu hệ thống từ bản sao lưu (`UC-DAT-02`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2240}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7760}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: phục hồi dữ liệu tài chính từ tệp sao lưu đã xuất mà không
vi phạm các quy tắc tài chính hiện hành. \\ \hline
Mô tả tóm tắt &
Phục hồi dữ liệu từ tệp sao lưu qua quy trình kiểm tra 4 bước (cú pháp;
danh mục theo Quy tắc phân cấp danh mục thu chi 3 cấp
(\texttt{BR-FIN-04}); ví và liên kết chuyển tiền; số dư không âm theo
Quy tắc bảo toàn số dư ví (\texttt{BR-FIN-02})), có hoàn nguyên tự động
theo \texttt{FR-DAT-02}. \\ \hline
Điều kiện kích hoạt &
Người dùng cần phục hồi dữ liệu từ một tệp sao lưu có sẵn. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Hệ thống hoạt động bình thường, có quyền đọc tệp sao lưu chỉ định.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

Nếu tệp không hợp lệ hoặc quá trình nhập bị hủy, dữ liệu hiện tại giữ nguyên và không ghi bất kỳ dữ liệu sai lệch nào.

#### Hậu điều kiện thành công

1. Dữ liệu hiện tại được thay thế hoàn toàn bằng dữ liệu từ tệp sao lưu; Quy tắc bảo toàn số dư ví (`BR-FIN-02`) và Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`) luôn đúng.
2. Nếu tệp chứa dữ liệu lỗi, toàn bộ thao tác bị hủy và hoàn nguyên, dữ liệu hiện tại không đổi.

#### Luồng sự kiện chính

1. Người dùng chọn "Nhập dữ liệu từ tệp sao lưu".
2. Hệ thống hiển thị biểu mẫu với hộp thoại chọn tệp sao lưu (`.json`).
3. Người dùng chọn tệp sao lưu.
4. Hệ thống hiển thị cảnh báo: "Phục hồi sẽ thay thế toàn bộ dữ liệu tài chính hiện tại bằng dữ liệu từ tệp sao lưu. Xác nhận tiếp tục thao tác?"
5. Người dùng xác nhận tiếp tục.
6. Hệ thống phân tích cú pháp JSON và kiểm tra 4 bước (`FR-DAT-02`):
   - Bước 1 (định dạng): cấu trúc tệp, định dạng ngày `YYYY-MM-DD`, chuỗi số học.
   - Bước 2 (Quy tắc phân cấp danh mục thu chi 3 cấp `BR-FIN-04`): mọi mã danh mục dùng trong giao dịch trỏ tới danh mục hợp lệ (đang dùng hoặc đã xóa) có Loại thu chi tương thích.
   - Bước 3 (ví và liên kết chuyển tiền): mọi giao dịch thu chi và chuyển tiền trỏ tới ví có trong danh sách; mỗi phí giao dịch phát sinh từ chuyển tiền trỏ tới một giao dịch chuyển tiền tồn tại.
   - Bước 4 (Quy tắc bảo toàn số dư ví `BR-FIN-02`): tính lại số dư từng ví từ lịch sử dòng tiền trong tệp, không ví nào âm.
7. Nếu cả 4 bước đều đạt, hệ thống tạo bản sao lưu tự động của dữ liệu hiện tại (`backup_YYYYMMDD_HHMMSS.json` trong `backups/`, `CON-06`), rồi bắt đầu ghi tệp, xóa dữ liệu cũ, nạp toàn bộ bản ghi từ tệp sao lưu, hoàn tất ghi, rồi tính lại toàn bộ số dư và bảng thống kê 12 tháng gần nhất.
8. Hệ thống hiển thị báo cáo nhập thành công gồm tổng số bản ghi và số lượng từng loại thực thể đã phục hồi.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

##### Luồng ngoại lệ 4a: Người dùng hủy tại cảnh báo thay thế dữ liệu

1. Tại bước 4, trước cảnh báo thay thế toàn bộ dữ liệu, Người dùng chọn "Hủy bỏ".
2. Hệ thống dừng thao tác nhập, đóng hộp thoại và giữ nguyên dữ liệu.

##### Luồng ngoại lệ 6a: Tệp sai cú pháp hoặc bị hỏng

1. Tại bước 6, tệp lỗi định dạng JSON (thiếu dấu ngoặc, rỗng hoặc sai mã hóa).
2. Hệ thống hủy nhập và thông báo: "Tệp sao lưu bị lỗi cấu trúc cú pháp hoặc bị hỏng dữ liệu. Vui lòng kiểm tra lại tệp nguồn."
3. Dữ liệu hiện tại giữ nguyên.

##### Luồng ngoại lệ 6b: Dữ liệu trong tệp vi phạm quy tắc số dư hoặc liên kết

1. Tại bước 6, hệ thống phát hiện ít nhất một vi phạm, ví dụ giao dịch chi trỏ tới ví không tồn tại, phí giao dịch trỏ tới chuyển tiền không tồn tại, hoặc lịch sử dòng tiền làm ví tiền mặt âm.
2. Hệ thống hủy toàn bộ quá trình nhập, không ghi bản ghi nào vào tệp dữ liệu.
3. Hệ thống hiển thị bảng lỗi chi tiết (số dòng lỗi, loại vi phạm: liên kết dữ liệu hoặc Quy tắc bảo toàn số dư ví (`BR-FIN-02`)) để Người dùng sửa tệp trước khi thử lại.

#### Quy tắc nghiệp vụ đặc thù

1. Nguyên tắc tất cả hoặc không: nếu có bất kỳ lỗi nào, toàn bộ quá trình hoàn nguyên về trạng thái trước đó.
2. `FR-DAT-02`: không ghi dữ liệu nếu chưa qua 4 bước kiểm tra (cấu trúc; danh mục theo Quy tắc phân cấp danh mục thu chi 3 cấp `BR-FIN-04`; ví và liên kết chuyển tiền; số dư không âm theo Quy tắc bảo toàn số dư ví `BR-FIN-02`).
3. Yêu cầu chức năng liên quan: `FR-DAT-02`.

### 6.20 UC-DAT-03: Kiểm tra lại dữ liệu hệ thống

Bảng thuộc tính đặc tả thuật toán rà soát bất biến toàn vẹn của ca sử dụng Kiểm tra lại dữ liệu hệ thống (`UC-DAT-03`):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.2240}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.7760}}|}
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Trường &
Nội dung \\ \hline
\endhead
Các bên liên quan và mối quan tâm &
Người dùng: kiểm tra, đối soát độc lập 4 điều kiện toàn vẹn để phát hiện
sớm sai lệch. \\ \hline
Mô tả tóm tắt &
Chạy công cụ kiểm tra lại độc lập theo \texttt{FR-DAT-03} cho 4 điều
kiện: số dư ví theo Quy tắc bảo toàn số dư ví (\texttt{BR-FIN-02}), cân
bằng nguồn - đích khi chuyển tiền theo Quy tắc ghi nhận đồng thời khi
chuyển tiền nội bộ (\texttt{BR-FIN-03}), liên kết danh mục theo Quy tắc
phân cấp danh mục thu chi 3 cấp (\texttt{BR-FIN-04}) và toàn vẹn liên
kết phí chuyển tiền. \\ \hline
Điều kiện kích hoạt &
Người dùng cần kiểm tra định kỳ hoặc khi nghi ngờ có sai lệch. \\ \hline
\end{longtable}
\endgroup
```

#### Tiền điều kiện

1. Hệ thống hoạt động bình thường, có quyền đọc tệp dữ liệu cục bộ.
2. Người dùng đang ở giao diện sao lưu và phục hồi dữ liệu.

#### Bảo đảm tối thiểu

Kiểm tra chỉ đọc, không thay đổi giao dịch hay số dư ví.

#### Hậu điều kiện thành công

Báo cáo kiểm tra phản ánh trung thực trạng thái của 4 điều kiện toàn vẹn (`FR-DAT-03`).

#### Luồng sự kiện chính

1. Người dùng chọn "Kiểm tra lại dữ liệu hệ thống".
2. Hệ thống quét toàn bộ tệp dữ liệu và kiểm tra lần lượt 4 điều kiện (`FR-DAT-03`):
   - Bước 1 (Quy tắc bảo toàn số dư ví `BR-FIN-02`): tính độc lập số dư từng ví từ lịch sử dòng tiền, so với số dư ghi nhận, sai số bằng 0.00 VNĐ.
   - Bước 2 (Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ `BR-FIN-03`): với mọi giao dịch chuyển tiền, số tiền trừ ở ví nguồn bằng đúng số tiền cộng ở ví đích (không tính chi phí).
   - Bước 3 (Quy tắc phân cấp danh mục thu chi 3 cấp `BR-FIN-04`): mọi giao dịch thu chi gắn với danh mục có trong dữ liệu (mục đã xóa vẫn hợp lệ) và đúng Loại thu chi.
   - Bước 4: mọi phí giao dịch phát sinh từ chuyển tiền trỏ tới một giao dịch chuyển tiền tồn tại.
3. Hệ thống tổng hợp Báo cáo kiểm tra lại dữ liệu, phân mức độ nghiêm trọng: `INFO`, `WARNING`, `ERROR`.
4. Nếu tất cả đạt, hệ thống hiển thị trạng thái hợp lệ: "Dữ liệu hệ thống đạt 100% yêu cầu về liên kết dữ liệu và số dư khớp nhau."
5. Nếu có sai lệch, hệ thống đánh dấu đỏ, liệt kê mã giao dịch, số tiền chênh lệch và hướng dẫn biện pháp xử lý.

#### Các luồng rẽ nhánh

Không có.

#### Các luồng ngoại lệ

Không có luồng ngoại lệ nghiệp vụ bổ sung ngoài việc phân loại mức nghiêm trọng của sai lệch ở bước 3 và bước 5 của luồng chính.

#### Quy tắc nghiệp vụ đặc thù

1. `FR-DAT-03`: công cụ kiểm tra độc lập tự động kiểm tra 4 điều kiện toàn vẹn: số dư ví, cân bằng nguồn - đích khi chuyển tiền, liên kết danh mục theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`) và liên kết phí chuyển tiền.
2. Yêu cầu chức năng liên quan: `FR-DAT-03`.

\newpage

## 7. Sơ đồ ca sử dụng hệ thống

Sơ đồ ca sử dụng thể hiện phạm vi chức năng, tác nhân và mối liên hệ của 20 ca sử dụng trong Hệ thống Quản lý Tài chính Cá nhân (PFMS). Hệ thống được tổ chức phân tầng thành hai cấp độ:

- Tầng 1 (vĩ mô): một sơ đồ tổng quan thể hiện ranh giới hệ thống và hai gói nghiệp vụ lớn.
- Tầng 2 (vi mô): sáu sơ đồ chi tiết bao phủ đủ 20 ca sử dụng, mỗi sơ đồ duy trì dưới 9 phần tử để tối ưu khả năng quan sát.

Ba nguyên tắc thiết kế áp dụng khi xây dựng sơ đồ ca sử dụng:

1. Đặt tên theo dạng động từ + danh từ: mỗi ca sử dụng thể hiện một mục tiêu hoàn chỉnh của Người dùng, không dùng các nút gộp chung mơ hồ.
2. Sử dụng quan hệ đúng ngữ nghĩa: không lạm dụng quan hệ phụ thuộc tuần tự, không biểu diễn các kịch bản rẽ nhánh loại trừ bằng quan hệ mở rộng.
3. Tính nhất quán và khớp nối hai chiều: 20 ca sử dụng trên 7 sơ đồ ánh xạ một-một với hồ sơ đặc tả chi tiết và ma trận truy vết.

Trong cả 7 sơ đồ, toàn bộ ca sử dụng đều do tác nhân Người dùng trực tiếp kích hoạt.

Bảng tổng hợp danh mục sơ đồ ca sử dụng của hệ thống phân bổ theo hai tầng kiến trúc, phạm vi nghiệp vụ và số lượng ca sử dụng tương ứng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\centering\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.0837}}|>{\centering\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.0887}}|>{\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.2391}}|>{\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.2830}}|>{\centering\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.0837}}|>{\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.2219}}|}
\hline
\rowcolor[gray]{0.9}
Tầng &
Mục &
Tên sơ đồ &
Phạm vi &
Số UC &
Dải mã \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Tầng &
Mục &
Tên sơ đồ &
Phạm vi &
Số UC &
Dải mã \\ \hline
\endhead
1 &
7.1 &
Tổng quan nhóm chức năng &
Toàn hệ thống (2 gói nghiệp vụ) &
20 &
\texttt{UC-WAL-01} đến \texttt{UC-DAT-03} \\ \hline
2 &
7.2.1a &
Nhóm Ví &
Tài khoản thanh toán và số dư &
4 &
\texttt{UC-WAL-01} đến \texttt{UC-WAL-04} \\ \hline
2 &
7.2.1b &
Nhóm Ghi nhận thu chi &
Dòng tiền thu nhập và chi tiêu &
4 &
\texttt{UC-TXN-01} đến \texttt{UC-TXN-04} \\ \hline
2 &
7.2.1c &
Nhóm Chuyển tiền nội bộ &
Chuyển tiền giữa hai ví &
3 &
\texttt{UC-TRF-01} đến \texttt{UC-TRF-03} \\ \hline
2 &
7.2.1d &
Nhóm Danh mục phân loại &
Cấu trúc phân loại thu chi 3 cấp &
4 &
\texttt{UC-CAT-01} đến \texttt{UC-CAT-04} \\ \hline
2 &
7.2.2a &
Nhóm Thống kê và báo cáo &
Xu hướng và cơ cấu thu chi &
2 &
\texttt{UC-REP-01} đến \texttt{UC-REP-02} \\ \hline
2 &
7.2.2b &
Nhóm Lưu trữ và sao lưu dữ liệu &
Sao lưu, phục hồi, kiểm tra dữ liệu &
3 &
\texttt{UC-DAT-01} đến \texttt{UC-DAT-03} \\ \hline
\strut &
\strut &
\textbf{Tổng cộng: 7 sơ đồ} &
\strut &
\textbf{20} &
\strut \\ \hline
\end{longtable}
\endgroup
```

### 7.1 Sơ đồ Ca sử dụng tổng quan kiến trúc nhóm chức năng

Sơ đồ tổng quan xác lập khung kiến trúc của hệ thống: Người dùng tương tác với hệ thống thông qua hai ca sử dụng tóm lược, mỗi ca đại diện cho một gói nghiệp vụ độc lập.

![](docs/software-requirement-specification/images/so-do-uc-tong-quan.png){width=80%}

Bảng phân loại chi tiết 20 ca sử dụng theo mục tiêu tương tác của Người dùng và các quy tắc nghiệp vụ tài chính chi phối thuộc hai gói chức năng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.0767}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1247}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.3249}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.4737}}|}
\hline
\rowcolor[gray]{0.9}
Gói &
Mã UC &
Mục tiêu của Người dùng &
Quy tắc nghiệp vụ \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Gói &
Mã UC &
Mục tiêu của Người dùng &
Quy tắc nghiệp vụ \\ \hline
\endhead
\textbf{Gói 1:} Dòng tiền và tiền trong ví &
\texttt{UC-WAL-01} đến \texttt{04} &
Quản lý các nguồn tiền khả dụng và kiểm soát số dư &
Quy tắc độ chính xác tài chính (\texttt{BR-FIN-01}), Quy tắc bảo toàn số
dư ví (\texttt{BR-FIN-02}), Quy tắc tên của ví, nhóm nhu cầu và danh mục
(\texttt{BR-NAM-01}) \\ \hline
\strut &
\texttt{UC-TXN-01} đến \texttt{04} &
Ghi nhận thu nhập, chi tiêu hằng ngày và phân loại theo danh mục &
Quy tắc độ chính xác tài chính (\texttt{BR-FIN-01}), Quy tắc bảo toàn số
dư ví (\texttt{BR-FIN-02}), Quy tắc phân cấp danh mục thu chi 3 cấp
(\texttt{BR-FIN-04}) \\ \hline
\strut &
\texttt{UC-TRF-01} đến \texttt{03} &
Chuyển tiền giữa hai ví, tự động trừ ví nguồn và cộng ví đích &
Quy tắc độ chính xác tài chính (\texttt{BR-FIN-01}), Quy tắc bảo toàn số
dư ví (\texttt{BR-FIN-02}), Quy tắc ghi nhận đồng thời khi chuyển tiền
nội bộ (\texttt{BR-FIN-03}) \\ \hline
\strut &
\texttt{UC-CAT-01} đến \texttt{04} &
Thiết lập cấu trúc danh mục 3 cấp phục vụ thống kê &
Quy tắc phân cấp danh mục thu chi 3 cấp (\texttt{BR-FIN-04}), Quy tắc
tên của ví, nhóm nhu cầu và danh mục (\texttt{BR-NAM-01}) \\ \hline
\textbf{Gói 2:} Thống kê, báo cáo và quản trị dữ liệu &
\texttt{UC-REP-01} đến \texttt{02} &
Theo dõi dòng tiền 12 tháng gần nhất và phân tích cơ cấu chi tiêu &
Quy tắc độ chính xác tài chính (\texttt{BR-FIN-01}), Quy tắc phân cấp
danh mục thu chi 3 cấp (\texttt{BR-FIN-04}) \\ \hline
\strut &
\texttt{UC-DAT-01} đến \texttt{03} &
Xuất dữ liệu, phục hồi bản sao lưu và kiểm tra tính toàn vẹn hệ thống &
Quy tắc độ chính xác tài chính (\texttt{BR-FIN-01}) đến Quy tắc phân cấp
danh mục thu chi 3 cấp (\texttt{BR-FIN-04}) \\ \hline
\end{longtable}
\endgroup
```

### 7.2 Sơ đồ Ca sử dụng chi tiết theo 2 nhóm chức năng nghiệp vụ

#### 7.2.1 Nhóm chức năng Dòng tiền và Tiền trong ví

Nhóm chức năng này gồm 15 ca sử dụng, được phân rã thành bốn sơ đồ chi tiết: ví, ghi nhận thu chi, chuyển tiền nội bộ (cơ chế ghi nhận đồng thời: trừ ví nguồn, cộng ví đích) và danh mục phân loại.

##### 7.2.1a Nhóm Ví (WAL)

![Sơ đồ ca sử dụng nhóm Ví](docs/software-requirement-specification/images/so-do-uc-wal.png){width=80%}

- `UC-WAL-01`: Tạo ví mới với số dư ban đầu 0.00 VNĐ.
- `UC-WAL-02`: Xem số dư khả dụng theo thời gian thực và tỷ lệ cơ cấu phân bổ giữa các ví.
- `UC-WAL-03`: Sửa thuộc tính ví; chuyển ví sang trạng thái lưu trữ là luồng rẽ nhánh hợp lệ.
- `UC-WAL-04`: Ghi nhận khoản điều chỉnh có dấu vết khi số dư thực tế lệch so với số dư hệ thống.

Quy tắc nghiệp vụ chi phối: số tiền dùng số thập phân chính xác theo Quy tắc độ chính xác tài chính (`BR-FIN-01`); số dư ví luôn không âm theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`); tên ví hợp lệ và duy nhất theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`).

##### 7.2.1b Nhóm ghi nhận thu chi (TXN)

![Sơ đồ ca sử dụng nhóm ghi nhận thu chi](docs/software-requirement-specification/images/so-do-uc-txn.png){width=80%}

- `UC-TXN-01`: Ghi nhận giao dịch thu nhập hoặc chi tiêu, kèm chi phí liên kết (nếu có) và gán danh mục 3 cấp.
- `UC-TXN-02`: Tra cứu và lọc lịch sử giao dịch theo khoảng thời gian, ví và danh mục.
- `UC-TXN-03`: Chỉnh sửa giao dịch thu chi, tự động hoàn nguyên ví cũ trước khi áp dụng ví mới và cập nhật phí liên kết.
- `UC-TXN-04`: Xóa giao dịch cùng phí liên kết và hoàn nguyên số dư ví liên quan.

Quy tắc nghiệp vụ chi phối: số thập phân chính xác theo Quy tắc độ chính xác tài chính (`BR-FIN-01`); hoàn nguyên giao dịch bảo toàn số dư ví theo Quy tắc bảo toàn số dư ví (`BR-FIN-02`); phân cấp danh mục thu chi 3 cấp theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`).

##### 7.2.1c Nhóm Chuyển tiền nội bộ (TRF)

![Sơ đồ ca sử dụng nhóm chuyển tiền](docs/software-requirement-specification/images/so-do-uc-trf.png){width=80%}

- `UC-TRF-01`: Chuyển tiền giữa hai ví nội bộ; chi phí chuyển tiền (nếu có) được ghi nhận thành phí liên kết trong cùng giao dịch.
- `UC-TRF-02`: Tra cứu lịch sử các giao dịch chuyển tiền nội bộ đã thực hiện.
- `UC-TRF-03`: Xóa giao dịch chuyển tiền cùng phí liên kết, tự động hoàn nguyên số dư cho cả hai ví.

Quy tắc nghiệp vụ chi phối:

- Quy tắc độ chính xác tài chính (`BR-FIN-01`): dùng số thập phân chính xác cho số tiền chuyển và phí giao dịch (chi phối `UC-TRF-01`).
- Quy tắc bảo toàn số dư ví (`BR-FIN-02`): số dư ví luôn không âm và tính lại từ lịch sử dòng tiền (chi phối cả 3 ca).
- Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): ghi nhận đồng thời giữa hai ví; bảo toàn tổng số dư hệ thống trừ chi phí chuyển tiền; tự động hoàn phí liên kết khi xóa giao dịch (chi phối cả 3 ca).

##### 7.2.1d Nhóm Danh mục phân loại (CAT)

Nhóm chức năng này quản lý cấu trúc danh mục 3 cấp: loại thu chi → nhóm nhu cầu → danh mục chi tiết.

![Sơ đồ ca sử dụng Nhóm Danh mục](docs/software-requirement-specification/images/so-do-uc-cat.png){width=80%}

- `UC-CAT-01`: Bổ sung nhóm hoặc danh mục mới trong cấu trúc phân cấp 3 cấp.
- `UC-CAT-02`: Đổi tên hiển thị của nhóm hoặc danh mục, và chuyển danh mục sang nhóm khác cùng loại thu chi.
- `UC-CAT-03`: Khôi phục nhóm hoặc danh mục đã ẩn (giữ nguyên liên kết lịch sử) hoặc tạo mục mới.
- `UC-CAT-04`: Xóa vĩnh viễn mục chưa có giao dịch; ẩn mục đã phát sinh giao dịch để bảo toàn dữ liệu lịch sử.

Quy tắc nghiệp vụ chi phối: mỗi danh mục thuộc đúng một nhóm cha theo Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`); tên nhóm và danh mục hợp lệ, không trùng lặp trong cùng phạm vi theo Quy tắc tên của ví, nhóm nhu cầu và danh mục (`BR-NAM-01`).

#### 7.2.2 Nhóm chức năng Thống kê, Báo cáo và Quản trị dữ liệu

Nhóm chức năng này gồm 5 ca sử dụng, được phân rã thành hai sơ đồ chi tiết: thống kê phân tích dòng tiền và sao lưu, phục hồi, kiểm tra toàn vẹn dữ liệu theo `FR-DAT-03`.

##### 7.2.2a Nhóm Thống kê và báo cáo (REP)

![Sơ đồ ca sử dụng nhóm thống kê và báo cáo](docs/software-requirement-specification/images/so-do-uc-rep.png){width=80%}

- `UC-REP-01`: Thống kê xu hướng dòng tiền 12 tháng liên tục gần nhất.
- `UC-REP-02`: Phân tích tỷ lệ cơ cấu thu chi theo nhóm nhu cầu và danh mục chi tiết.

Quy tắc nghiệp vụ chi phối: Quy tắc độ chính xác tài chính (`BR-FIN-01`) chi phối cả hai ca sử dụng; Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`) chi phối `UC-REP-02`.

Yêu cầu chức năng liên quan:

- `FR-REP-01`: thống kê chuỗi 12 tháng liên tục từ mốc chỉ định; tỷ lệ tiết kiệm bằng 0.00% khi tổng thu nhập không dương.
- `FR-REP-02`: phân tích cơ cấu chi tiêu theo nhóm và danh mục; danh mục đã xóa vẫn hiển thị đầy đủ kèm nhãn trạng thái.

##### 7.2.2b Nhóm Lưu trữ và sao lưu dữ liệu (DAT)

Nhóm chức năng này xuất dữ liệu tài chính, phục hồi từ bản sao lưu và kiểm tra độc lập các quy tắc toàn vẹn hệ thống.

![Sơ đồ ca sử dụng nhóm lưu trữ và sao lưu dữ liệu](docs/software-requirement-specification/images/so-do-uc-dat.png){width=80%}

- `UC-DAT-01`: Xuất toàn bộ dữ liệu tài chính ra tệp JSON chuẩn hóa.
- `UC-DAT-02`: Phục hồi dữ liệu từ tệp sao lưu qua quy trình kiểm tra 4 bước và tự động sao lưu trước khi thay thế.
- `UC-DAT-03`: Kiểm tra độc lập 4 điều kiện toàn vẹn hệ thống theo yêu cầu của Người dùng.

Quy tắc nghiệp vụ chi phối:

- Quy tắc độ chính xác tài chính (`BR-FIN-01`): bảo đảm độ chính xác số học khi xuất tệp (chi phối `UC-DAT-01`).
- Quy tắc bảo toàn số dư ví (`BR-FIN-02`): bảo toàn số dư ví không âm và tính lại từ lịch sử dòng tiền (chi phối `UC-DAT-02`, `UC-DAT-03`).
- Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ (`BR-FIN-03`): cân bằng nguồn - đích khi chuyển tiền nội bộ (chi phối `UC-DAT-03`).
- Quy tắc phân cấp danh mục thu chi 3 cấp (`BR-FIN-04`): kiểm soát tính hợp lệ của cây danh mục 3 cấp (chi phối `UC-DAT-02`, `UC-DAT-03`).

Yêu cầu chức năng liên quan:

- `FR-DAT-01`: xuất dữ liệu ra tệp JSON, giữ nguyên độ chính xác số học thập phân.
- `FR-DAT-02`: quy trình kiểm tra 4 bước khi nạp dữ liệu, tự động sao lưu trước khi thay thế và tự hoàn nguyên nếu lỗi.
- `FR-DAT-03`: thuật toán quét độc lập kiểm tra 4 điều kiện toàn vẹn: số dư ví, cân bằng chuyển tiền, liên kết danh mục và liên kết phí chuyển tiền.


## 8. Ma trận kiểm chứng và truy vết yêu cầu

Ma trận truy vết hai chiều và phương pháp kiểm chứng bảo đảm tính nhất quán giữa mục tiêu, yêu cầu chức năng, ca sử dụng, thực thể dữ liệu và quy tắc nghiệp vụ, làm cơ sở nghiệm thu hệ thống.


Bảng tổng hợp cấu trúc danh mục 5 ma trận truy vết và kiểm chứng yêu cầu của hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\centering\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.0685}}|>{\centering\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.0767}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.3018}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1778}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.3752}}|}
\hline
\rowcolor[gray]{0.9}
STT &
Mục &
Tên ma trận &
Chiều đối chiếu &
Ý nghĩa \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
STT &
Mục &
Tên ma trận &
Chiều đối chiếu &
Ý nghĩa \\ \hline
\endhead
1 &
8.1.1 &
Mục tiêu theo yêu cầu chức năng &
\texttt{G-*} sang \texttt{FR-*} &
Mọi mục tiêu đều có yêu cầu chức năng đáp ứng \\ \hline
2 &
8.1.2 &
Ca sử dụng theo yêu cầu chức năng &
\texttt{FR-*} sang \texttt{UC-*} &
Mọi yêu cầu chức năng đều có kịch bản tương tác \\ \hline
3 &
8.1.3 &
Thực thể tác động theo yêu cầu chức năng &
\texttt{FR-*} sang thực thể dữ liệu &
Xác định phạm vi ảnh hưởng dữ liệu của từng chức năng \\ \hline
4 &
8.1.4 &
Quy tắc nghiệp vụ theo yêu cầu chức năng &
\texttt{BR-*} sang \texttt{FR-*} &
Mọi quy tắc nghiệp vụ đều được thực thi \\ \hline
5 &
8.2 &
Phương pháp kiểm chứng yêu cầu &
\texttt{FR-*}, \texttt{NFR-*} sang T, D, I, A &
Cách nghiệm thu và môi trường thực hiện \\ \hline
\end{longtable}
\endgroup
```
\newpage

### 8.1 Ma trận truy vết yêu cầu theo yêu cầu chức năng

Bốn ma trận dưới đây thiết lập chuỗi truy vết hai chiều lấy yêu cầu chức năng làm trục trung tâm, kết nối từ nguồn gốc mục tiêu bài toán, kịch bản ca sử dụng, mô hình dữ liệu đến các quy tắc nghiệp vụ tài chính chi phối. Phạm vi bao phủ 3 mục tiêu hệ thống, 20 yêu cầu chức năng và 20 ca sử dụng, phân bổ theo 6 nhóm chức năng: `WAL`, `TXN`, `TRF`, `CAT`, `REP` và `DAT`.

#### 8.1.1 Ma trận mục tiêu hệ thống theo yêu cầu chức năng

Bảng ma trận truy vết đối chiếu giữa 20 yêu cầu chức năng và 3 mục tiêu hệ thống, trong đó ký hiệu `X` thể hiện yêu cầu chức năng trực tiếp hiện thực hóa mục tiêu tương ứng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1424}}|>{\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.5125}}|>{\centering\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1150}}|>{\centering\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1150}}|>{\centering\arraybackslash}p{(\linewidth - 10\tabcolsep - 6\arrayrulewidth) * \real{0.1150}}|}
\hline
\rowcolor[gray]{0.9}
Mã &
Tên chức năng &
\texttt{G-01} (Ghi nhận dòng tiền, danh mục) &
\texttt{G-02} (Tổng số dư, cơ cấu ví) &
\texttt{G-03} (Chính xác số học) \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã &
Tên chức năng &
\texttt{G-01} (Ghi nhận dòng tiền, danh mục) &
\texttt{G-02} (Tổng số dư, cơ cấu ví) &
\texttt{G-03} (Chính xác số học) \\ \hline
\endhead
\texttt{FR-WAL-01} &
Tạo ví mới &
X &
X &
\strut \\ \hline
\texttt{FR-WAL-02} &
Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền &
\strut &
X &
\strut \\ \hline
\texttt{FR-WAL-03} &
Cập nhật thông tin ví &
\strut &
X &
\strut \\ \hline
\texttt{FR-WAL-04} &
Thống kê cơ cấu số dư theo ví &
\strut &
X &
\strut \\ \hline
\texttt{FR-WAL-05} &
Điều chỉnh số dư ví &
\strut &
X &
\strut \\ \hline
\texttt{FR-TXN-01} &
Tạo giao dịch thu nhập &
X &
\strut &
X \\ \hline
\texttt{FR-TXN-02} &
Tạo giao dịch chi tiêu &
X &
\strut &
X \\ \hline
\texttt{FR-TXN-03} &
Tra cứu và lọc lịch sử giao dịch thu chi &
X &
\strut &
\strut \\ \hline
\texttt{FR-TXN-04} &
Chỉnh sửa giao dịch thu chi &
X &
\strut &
X \\ \hline
\texttt{FR-TXN-05} &
Xóa giao dịch thu chi &
X &
\strut &
X \\ \hline
\texttt{FR-TRF-01} &
Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn &
X &
\strut &
X \\ \hline
\texttt{FR-TRF-02} &
Tra cứu và xóa giao dịch chuyển tiền nội bộ &
X &
\strut &
X \\ \hline
\texttt{FR-CAT-01} &
Tạo cấu trúc danh mục 3 cấp &
X &
\strut &
\strut \\ \hline
\texttt{FR-CAT-02} &
Tùy biến danh mục người dùng &
X &
\strut &
\strut \\ \hline
\texttt{FR-CAT-03} &
Kiểm soát toàn vẹn khi cập nhật và xóa danh mục &
X &
\strut &
\strut \\ \hline
\texttt{FR-REP-01} &
Thống kê dòng tiền 12 tháng gần nhất &
X &
\strut &
X \\ \hline
\texttt{FR-REP-02} &
Phân tích cơ cấu thu chi theo nhóm và danh mục &
X &
\strut &
\strut \\ \hline
\texttt{FR-DAT-01} &
Xuất dữ liệu tài chính &
\strut &
X &
X \\ \hline
\texttt{FR-DAT-02} &
Phục hồi dữ liệu hệ thống từ bản sao lưu &
\strut &
X &
X \\ \hline
\texttt{FR-DAT-03} &
Kiểm tra lại dữ liệu hệ thống &
\strut &
\strut &
X \\ \hline
\end{longtable}
\endgroup
```

#### 8.1.2 Ma trận ca sử dụng theo yêu cầu chức năng

Bảng ma trận ánh xạ giữa 20 yêu cầu chức năng và các kịch bản ca sử dụng chi tiết hiện thực hóa trên giao diện và luồng xử lý:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.3972}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.6028}}|}
\hline
\rowcolor[gray]{0.9}
Yêu cầu chức năng &
Ca sử dụng liên quan \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Yêu cầu chức năng &
Ca sử dụng liên quan \\ \hline
\endhead
\texttt{FR-WAL-01} &
\texttt{UC-WAL-01}, \texttt{UC-WAL-03} \\ \hline
\texttt{FR-WAL-02} &
\texttt{UC-WAL-01}, \texttt{UC-WAL-02} \\ \hline
\texttt{FR-WAL-03} &
\texttt{UC-WAL-01}, \texttt{UC-WAL-03} \\ \hline
\texttt{FR-WAL-04} &
\texttt{UC-WAL-01}, \texttt{UC-WAL-02} \\ \hline
\texttt{FR-WAL-05} &
\texttt{UC-WAL-04} \\ \hline
\texttt{FR-TXN-01} &
\texttt{UC-TXN-01} \\ \hline
\texttt{FR-TXN-02} &
\texttt{UC-TXN-01} \\ \hline
\texttt{FR-TXN-03} &
\texttt{UC-TXN-02} \\ \hline
\texttt{FR-TXN-04} &
\texttt{UC-TXN-03} \\ \hline
\texttt{FR-TXN-05} &
\texttt{UC-TXN-04} \\ \hline
\texttt{FR-TRF-01} &
\texttt{UC-TRF-01} \\ \hline
\texttt{FR-TRF-02} &
\texttt{UC-TRF-02}, \texttt{UC-TRF-03} \\ \hline
\texttt{FR-CAT-01} &
\texttt{UC-CAT-01} \\ \hline
\texttt{FR-CAT-02} &
\texttt{UC-CAT-01}, \texttt{UC-CAT-02}, \texttt{UC-CAT-03} \\ \hline
\texttt{FR-CAT-03} &
\texttt{UC-CAT-02}, \texttt{UC-CAT-03}, \texttt{UC-CAT-04} \\ \hline
\texttt{FR-REP-01} &
\texttt{UC-REP-01} \\ \hline
\texttt{FR-REP-02} &
\texttt{UC-REP-02} \\ \hline
\texttt{FR-DAT-01} &
\texttt{UC-DAT-01} \\ \hline
\texttt{FR-DAT-02} &
\texttt{UC-DAT-02} \\ \hline
\texttt{FR-DAT-03} &
\texttt{UC-DAT-03} \\ \hline
\end{longtable}
\endgroup
```

#### 8.1.3 Ma trận thực thể tác động theo yêu cầu chức năng

Bảng ma trận xác định phạm vi các thực thể dữ liệu miền bài toán chịu tác động trực tiếp bởi từng nhóm yêu cầu chức năng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.4119}}|>{\arraybackslash}p{(\linewidth - 4\tabcolsep - 3\arrayrulewidth) * \real{0.5881}}|}
\hline
\rowcolor[gray]{0.9}
Yêu cầu chức năng &
Thực thể tác động \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Yêu cầu chức năng &
Thực thể tác động \\ \hline
\endhead
\texttt{FR-WAL-01}, \texttt{FR-WAL-02}, \texttt{FR-WAL-03},
\texttt{FR-WAL-04} &
Ví \\ \hline
\texttt{FR-WAL-05} &
Ví, Điều chỉnh số dư \\ \hline
\texttt{FR-TXN-01}, \texttt{FR-TXN-02}, \texttt{FR-TXN-03},
\texttt{FR-TXN-04} &
Giao dịch thu chi, Ví, Danh mục \\ \hline
\texttt{FR-TXN-05} &
Giao dịch thu chi, Ví \\ \hline
\texttt{FR-TRF-01} &
Giao dịch chuyển tiền, Giao dịch thu chi, Ví \\ \hline
\texttt{FR-TRF-02} &
Giao dịch chuyển tiền, Giao dịch thu chi, Ví \\ \hline
\texttt{FR-CAT-01}, \texttt{FR-CAT-02} &
Danh mục \\ \hline
\texttt{FR-CAT-03} &
Danh mục, Giao dịch thu chi \\ \hline
\texttt{FR-REP-01} &
Thống kê tháng, Giao dịch thu chi \\ \hline
\texttt{FR-REP-02} &
Thống kê tháng, Danh mục, Giao dịch thu chi \\ \hline
\texttt{FR-DAT-01}, \texttt{FR-DAT-02}, \texttt{FR-DAT-03} &
Dữ liệu tài chính \\ \hline
\end{longtable}
\endgroup
```

#### 8.1.4 Ma trận quy tắc nghiệp vụ theo yêu cầu chức năng

Bảng ma trận truy vết mức độ bao phủ của 5 quy tắc nghiệp vụ tài chính cốt lõi đối với các yêu cầu chức năng tương ứng:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.1247}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.3073}}|>{\arraybackslash}p{(\linewidth - 6\tabcolsep - 4\arrayrulewidth) * \real{0.5681}}|}
\hline
\rowcolor[gray]{0.9}
Mã quy tắc &
Tên quy tắc &
Yêu cầu chức năng áp dụng \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã quy tắc &
Tên quy tắc &
Yêu cầu chức năng áp dụng \\ \hline
\endhead
\texttt{BR-FIN-01} &
Quy tắc độ chính xác tài chính &
\texttt{FR-WAL-02}, \texttt{FR-WAL-04}, \texttt{FR-WAL-05}\newline \texttt{FR-TXN-01}, \texttt{FR-TXN-02}, \texttt{FR-TXN-03},
\texttt{FR-TXN-04}\newline \texttt{FR-TRF-01}\strut \newline \texttt{FR-REP-01}, \texttt{FR-REP-02}\newline \texttt{FR-DAT-01}, \texttt{FR-DAT-03} \\ \hline
\texttt{BR-FIN-02} &
Quy tắc bảo toàn số dư ví &
\texttt{FR-WAL-01}, \texttt{FR-WAL-02}, \texttt{FR-WAL-03},
\texttt{FR-WAL-05}\newline \texttt{FR-TXN-01}, \texttt{FR-TXN-02}, \texttt{FR-TXN-04},
\texttt{FR-TXN-05}\newline \texttt{FR-TRF-01}, \texttt{FR-TRF-02}\newline \texttt{FR-DAT-02}, \texttt{FR-DAT-03} \\ \hline
\texttt{BR-FIN-03} &
Quy tắc ghi nhận đồng thời khi chuyển tiền nội bộ &
\texttt{FR-TRF-01}, \texttt{FR-TRF-02}\newline \texttt{FR-DAT-03} \\ \hline
\texttt{BR-FIN-04} &
Quy tắc phân cấp danh mục thu chi 3 cấp &
\texttt{FR-TXN-01}, \texttt{FR-TXN-02}, \texttt{FR-TXN-04}\newline \texttt{FR-CAT-01}, \texttt{FR-CAT-02}, \texttt{FR-CAT-03}\newline \texttt{FR-REP-02}\strut \newline \texttt{FR-DAT-02}, \texttt{FR-DAT-03} \\ \hline
\texttt{BR-NAM-01} &
Quy tắc tên của ví, nhóm nhu cầu và danh mục &
\texttt{FR-WAL-01}, \texttt{FR-WAL-03}\newline \texttt{FR-CAT-02}, \texttt{FR-CAT-03} \\ \hline
\end{longtable}
\endgroup
```

### 8.2 Ma trận phương pháp kiểm chứng yêu cầu

Bảng định nghĩa 4 phương pháp kiểm chứng yêu cầu phần mềm chuẩn hóa theo tiêu chuẩn quốc tế ISO/IEC/IEEE 29148:2018:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.0587}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1410}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1727}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.6276}}|}
\hline
\rowcolor[gray]{0.9}
Ký hiệu &
Phương pháp &
Tên tiếng Anh &
Cách thực hiện \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Ký hiệu &
Phương pháp &
Tên tiếng Anh &
Cách thực hiện \\ \hline
\endhead
I &
Soát xét kỹ thuật &
Inspection &
Đối chiếu tài liệu thiết kế, rà soát mã nguồn tĩnh hoặc cấu trúc lưu
trữ, không chạy chương trình \\ \hline
T &
Kiểm thử tự động &
Test &
Chạy chương trình hoặc mô-đun với dữ liệu đầu vào xác định, so kết quả
thực tế với kết quả kỳ vọng (unit test, integration test) \\ \hline
D &
Trình diễn hoạt động &
Demonstration &
Thao tác trực tiếp trên giao diện theo kịch bản thực tế để quan sát
luồng xử lý và kết quả \\ \hline
A &
Phân tích định lượng &
Analysis &
Đánh giá chỉ số kỹ thuật bằng công cụ phân tích dữ liệu, thống kê, đo
hiệu năng và độ bao phủ kiểm thử \\ \hline
\end{longtable}
\endgroup
```

#### 8.2.1 Ma trận phương pháp kiểm chứng yêu cầu chức năng (20 FRs)

Bảng ma trận phương pháp kiểm chứng và tiêu chí nghiệm thu kỹ thuật đối với 20 yêu cầu chức năng của hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1247}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2147}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.0887}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.5720}}|}
\hline
\rowcolor[gray]{0.9}
Mã yêu cầu &
Tên yêu cầu &
Phương pháp &
Tiêu chí nghiệm thu \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã yêu cầu &
Tên yêu cầu &
Phương pháp &
Tiêu chí nghiệm thu \\ \hline
\endhead
\texttt{FR-WAL-01} &
Tạo ví mới &
T, D &
Tạo ví thành công với tên duy nhất và số dư 0.00; lần khởi tạo đầu tiên
chỉ có một ví tiền mặt có sẵn \\ \hline
\texttt{FR-WAL-02} &
Xem danh sách và chi tiết số dư tính lại từ lịch sử dòng tiền &
T, D &
Hiển thị đúng danh sách ví và số dư tính lại từ lịch sử dòng tiền theo
công thức Quy tắc bảo toàn số dư ví (\texttt{BR-FIN-02}) \\ \hline
\texttt{FR-WAL-03} &
Cập nhật thông tin ví &
T, D &
Cập nhật được tên và trạng thái ví; cấm đổi trực tiếp số dư khả dụng;
cấm lưu trữ ví tiền mặt có sẵn \\ \hline
\texttt{FR-WAL-04} &
Thống kê cơ cấu số dư theo ví &
T, D &
Tính đúng tỷ lệ phần trăm số dư của từng ví trên tổng số dư các ví \\ \hline
\texttt{FR-WAL-05} &
Điều chỉnh số dư ví &
T, D &
Ghi đúng chênh lệch điều chỉnh; lý do là tùy chọn (tối đa 255 ký tự); từ
chối khi chênh lệch bằng 0 \\ \hline
\texttt{FR-TXN-01} &
Tạo giao dịch thu nhập &
T, D &
Số tiền \textgreater{} 0 và danh mục thuộc loại thu nhập. Số dư ví nhận
tăng đúng bằng số tiền thu trừ chi phí. Ghi phí giao dịch liên kết khi
chi phí lớn hơn 0 \\ \hline
\texttt{FR-TXN-02} &
Tạo giao dịch chi tiêu &
T, D &
Số tiền \textgreater{} 0, danh mục thuộc loại chi tiêu, số dư ví đủ để
trả số tiền cộng chi phí. Trừ đúng số dư ví, kèm phí giao dịch liên kết \\ \hline
\texttt{FR-TXN-03} &
Tra cứu và lọc lịch sử giao dịch thu chi &
T, D &
Lọc đúng theo khoảng ngày, ví, danh mục, từ khóa. Tổng thu chi đúng; phí
giao dịch hiện thành dòng riêng có nhãn liên kết và được tính vào tổng
chi \\ \hline
\texttt{FR-TXN-04} &
Chỉnh sửa giao dịch thu chi &
T, D &
Hoàn nguyên số tiền và chi phí cũ trên ví cũ, áp dụng số tiền và chi phí
mới trên ví mới. Phí giao dịch liên kết được tạo, cập nhật hoặc xóa theo
chi phí mới. Số dư các ví luôn khớp \\ \hline
\texttt{FR-TXN-05} &
Xóa giao dịch thu chi &
T, D &
Xóa thành công và phục hồi đúng số dư ví theo quy tắc đảo ngược; xóa kèm
phí giao dịch liên kết \\ \hline
\texttt{FR-TRF-01} &
Chuyển tiền giữa hai ví nội bộ, kèm chi phí tùy chọn &
T, D &
Ghi nhận đồng thời: số tiền trừ ở ví nguồn bằng đúng số tiền cộng vào ví
đích. Khi có chi phí, ví nguồn bị trừ thêm và tự tạo phí giao dịch liên
kết; tổng số dư giảm đúng bằng chi phí \\ \hline
\texttt{FR-TRF-02} &
Tra cứu và xóa giao dịch chuyển tiền nội bộ &
T, D &
Danh sách chuyển tiền xếp theo thời gian; xóa chuyển tiền thì hoàn tiền
cho cả hai ví và xóa luôn phí liên quan \\ \hline
\texttt{FR-CAT-01} &
Tạo cấu trúc danh mục 3 cấp &
T, I &
Cấu trúc danh mục 3 cấp (loại -\textgreater{} nhóm -\textgreater{} danh
mục) đúng Quy tắc phân cấp danh mục thu chi 3 cấp (\texttt{BR-FIN-04}) \\ \hline
\texttt{FR-CAT-02} &
Tùy biến danh mục người dùng &
T, D &
Thêm danh mục con thành công; tên danh mục không trùng trong cùng một
nhóm \\ \hline
\texttt{FR-CAT-03} &
Kiểm soát toàn vẹn khi cập nhật và xóa danh mục &
T, D &
Ẩn thay vì xóa vật lý danh mục đã có giao dịch liên kết; cho phép chuyển
danh mục sang nhóm khác cùng loại thu chi theo Quy tắc phân cấp danh mục
thu chi 3 cấp (\texttt{BR-FIN-04}) \\ \hline
\texttt{FR-REP-01} &
Thống kê dòng tiền 12 tháng gần nhất &
T, A &
Lấy đúng 12 tháng liên tục gần nhất; tổng hợp đúng tổng thu nhập, tổng
chi tiêu, dòng tiền thuần và tỷ lệ tiết kiệm. Ba mức bình quân chia cho
12, kèm ghi chú nêu các tháng chưa có giao dịch \\ \hline
\texttt{FR-REP-02} &
Phân tích cơ cấu thu chi theo nhóm và danh mục &
T, D &
Tính đúng tổng tiền và tỷ lệ phần trăm chi tiêu của từng nhóm nhu cầu và
danh mục \\ \hline
\texttt{FR-DAT-01} &
Xuất dữ liệu tài chính &
T, I &
Xuất JSON đúng cấu trúc và giữ nguyên độ chính xác số học
(\texttt{CON-02}) \\ \hline
\texttt{FR-DAT-02} &
Phục hồi dữ liệu hệ thống từ bản sao lưu &
T, D &
Kiểm tra hợp lệ 4 bước trước khi nhập vào hệ thống; khôi phục đầy đủ
trạng thái hệ thống \\ \hline
\texttt{FR-DAT-03} &
Kiểm tra lại dữ liệu hệ thống &
T, A &
Thuật toán quét toàn bộ dữ liệu tiền; phát hiện sai lệch ở 4 điều kiện
toàn vẹn nếu có \\ \hline
\end{longtable}
\endgroup
```

#### 8.2.2 Ma trận phương pháp kiểm chứng yêu cầu phi chức năng (6 NFRs)

Bảng ma trận phương pháp kiểm chứng, môi trường thực thi và ngưỡng nghiệm thu đối với 6 yêu cầu phi chức năng của hệ thống:

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.0887}}|>{\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.1358}}|>{\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.1898}}|>{\centering\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.0887}}|>{\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.2153}}|>{\arraybackslash}p{(\linewidth - 12\tabcolsep - 7\arrayrulewidth) * \real{0.2818}}|}
\hline
\rowcolor[gray]{0.9}
Mã yêu cầu &
Tên yêu cầu &
Đặc tính chất lượng &
Phương pháp &
Môi trường và cách thực hiện &
Ngưỡng nghiệm thu \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Mã yêu cầu &
Tên yêu cầu &
Đặc tính chất lượng &
Phương pháp &
Môi trường và cách thực hiện &
Ngưỡng nghiệm thu \\ \hline
\endhead
\texttt{NFR-01} &
Độ chính xác và toàn vẹn dữ liệu tài chính &
Phù hợp chức năng / Tính chính xác chức năng &
T, I &
Kiểm thử tự động, phân tích tĩnh mã nguồn &
Sai số tích lũy = 0.00 VNĐ; 0\% kiểu số dấu phẩy động nhị phân; vi phạm
Quy tắc bảo toàn số dư ví (\texttt{BR-FIN-02}) = 0.00\% \\ \hline
\texttt{NFR-02} &
Hiệu năng và độ trễ phản hồi &
Hiệu quả vận hành / Hành vi thời gian và mức sử dụng tài nguyên &
D, A &
Trình diễn thao tác trực tiếp, quan sát xu hướng cấp phát bộ nhớ &
Phản hồi dưới 1.0 giây với thao tác thông thường; không rò rỉ bộ nhớ \\ \hline
\texttt{NFR-03} &
Độ tin cậy và lưu trữ an toàn &
Độ tin cậy / Chịu lỗi và khả năng phục hồi &
T, I &
Kiểm thử giả lập lỗi, mô phỏng ngắt tiến trình đột ngột &
Tỷ lệ hỏng tệp = 0.00\%; ghi tệp an toàn không làm hỏng dữ liệu cũ;
100.00\% tác vụ thay thế dữ liệu quy mô lớn đã có bản sao lưu trước đó
(\texttt{CON-06}) \\ \hline
\texttt{NFR-04} &
Khả năng bảo trì và kiến trúc hướng đối tượng &
Bảo trì / Mô-đun hóa, tái sử dụng, dễ phân tích, dễ kiểm thử &
T, I &
Kiểm thử tự động theo từng quy tắc nghiệp vụ, đánh giá kiến trúc &
100\% quy tắc nghiệp vụ có ca kiểm thử; 0 phụ thuộc vòng giữa các mô-đun \\ \hline
\texttt{NFR-05} &
Tính khả dụng và trải nghiệm người dùng &
Tương tác / Dễ học, dễ vận hành, bảo vệ người dùng khỏi lỗi &
D, I &
Kịch bản trải nghiệm người dùng, danh mục mã lỗi chuẩn hóa &
100\% thông báo lỗi bằng tiếng Việt, rõ ràng, có gợi ý xử lý \\ \hline
\texttt{NFR-06} &
Tính tương thích và môi trường chạy &
Linh hoạt / Thích ứng, dễ cài đặt &
T, I &
Tích hợp liên tục đa nền tảng (Windows, macOS, Linux) &
100\% pass kiểm thử trên cả 3 hệ điều hành; 100\% tệp dùng UTF-8; 0
native library \\ \hline
\end{longtable}
\endgroup
```
\newpage

## 9. Tổng kết

Bảng dưới đây tổng hợp quy mô các thành phần trong bản đặc tả Hệ thống quản lý tài chính cá nhân (PFMS):

```{=latex}
\begingroup\small\renewcommand{\arraystretch}{1.35}
\begin{longtable}[l]{|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.1819}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.2241}}|>{\centering\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.0767}}|>{\arraybackslash}p{(\linewidth - 8\tabcolsep - 5\arrayrulewidth) * \real{0.5174}}|}
\hline
\rowcolor[gray]{0.9}
Thành phần &
Phạm vi mã &
Số lượng &
Mô tả \\ \hline
\endfirsthead
\hline
\rowcolor[gray]{0.9}
Thành phần &
Phạm vi mã &
Số lượng &
Mô tả \\ \hline
\endhead
Mục tiêu hệ thống &
\texttt{G-01} đến \texttt{G-03} &
3 &
Có tiêu chí đo bằng số và phân tích đánh đổi kỹ thuật \\ \hline
Quy tắc nghiệp vụ tài chính &
\texttt{BR-FIN-01} đến \texttt{BR-FIN-04} &
4 &
Độ chính xác tài chính, bảo toàn số dư, ghi nhận đồng thời và phân cấp
danh mục 3 cấp \\ \hline
Quy tắc nghiệp vụ về tên &
\texttt{BR-NAM-01} &
1 &
Định dạng và tính duy nhất của tên ví, nhóm nhu cầu và danh mục \\ \hline
Yêu cầu chức năng &
\texttt{FR-WAL-01} đến \texttt{FR-DAT-03} &
20 &
Chia theo 6 nhóm chức năng nghiệp vụ \\ \hline
Yêu cầu phi chức năng &
\texttt{NFR-01} đến \texttt{NFR-06} &
6 &
Có ngưỡng nghiệm thu; \texttt{NFR-02} có ngưỡng thời gian định lượng (chỉ số ổn định bộ nhớ là định tính), \texttt{NFR-05} có
một chỉ số định tính \\ \hline
Ca sử dụng chi tiết &
\texttt{UC-WAL-01} đến \texttt{UC-DAT-03} &
20 &
Mỗi ca gồm luồng chính, luồng rẽ nhánh và ngoại lệ \\ \hline
Sơ đồ ca sử dụng &
Mục 7 &
7 &
1 sơ đồ tổng quan và 6 sơ đồ chi tiết \\ \hline
Truy vết và kiểm chứng &
4 ma trận truy vết (Mục 8.1) và 26 dòng kiểm chứng theo phương pháp I,
T, D, A (20 \texttt{FR}, 6 \texttt{NFR}, Mục 8.2) &
26 &
Truy vết hai chiều giữa mục tiêu, yêu cầu chức năng, ca sử dụng và quy
tắc nghiệp vụ; kiểm chứng cả yêu cầu chức năng lẫn phi chức năng \\ \hline
\end{longtable}
\endgroup
```

\newpage

## 10. TÀI LIỆU TRÍCH DẪN

Danh mục tài liệu tham khảo kỹ thuật và học thuật được trích dẫn theo chuẩn APA, sắp xếp theo thứ tự chữ cái của tên tác giả hoặc cơ quan ban hành. Mỗi mục ghi tác giả hoặc cơ quan, năm ban hành, tên tài liệu, số hiệu, nhà xuất bản và địa chỉ web nếu có.

- ASTM International. (2022). *Standard practice for using significant digits in test data to determine conformance with specifications* (ASTM E29-22).
- Bray, T. (Ed.). (2017). *The JavaScript Object Notation (JSON) data interchange format* (RFC 8259). Internet Engineering Task Force. https://www.rfc-editor.org/rfc/rfc8259
- Cockburn, A. (2001). *Writing effective use cases*. Addison-Wesley Professional.
- Glinz, M. (2025). *Glossary of requirements engineering terminology* (Version 2.2.0). International Requirements Engineering Board.
- Institute of Electrical and Electronics Engineers. (2019). *IEEE standard for floating-point arithmetic* (IEEE Std 754-2019).
- International Organization for Standardization. (2019). *Date and time: Representations for information interchange. Part 1: Basic rules* (ISO 8601-1:2019).
- International Organization for Standardization & International Electrotechnical Commission. (2012a). *Information technology: Object Management Group Unified Modeling Language (OMG UML). Part 1: Infrastructure* (ISO/IEC 19505-1:2012).
- International Organization for Standardization & International Electrotechnical Commission. (2012b). *Information technology: Object Management Group Unified Modeling Language (OMG UML). Part 2: Superstructure* (ISO/IEC 19505-2:2012).
- International Organization for Standardization & International Electrotechnical Commission. (2023). *Systems and software engineering: Systems and software quality requirements and evaluation (SQuaRE): Product quality model* (ISO/IEC 25010:2023).
- International Organization for Standardization, International Electrotechnical Commission, & Institute of Electrical and Electronics Engineers. (2018). *Systems and software engineering: Life cycle processes: Requirements engineering* (ISO/IEC/IEEE 29148:2018).
- International Software Testing Qualifications Board. (2024). *Certified Tester Foundation Level syllabus* (Version 4.0.1). https://www.istqb.org/wp-content/uploads/2024/11/ISTQB_CTFL_Syllabus_v4.0.1.pdf
- Larman, C. (2004). *Applying UML and patterns: An introduction to object-oriented analysis and design and iterative development* (3rd ed.). Prentice Hall.
- Ross, R. G. (2003). *Principles of the business rule approach*. Addison-Wesley.
- Yergeau, F. (2003). *UTF-8, a transformation format of ISO 10646* (RFC 3629). Internet Engineering Task Force. https://www.rfc-editor.org/rfc/rfc3629