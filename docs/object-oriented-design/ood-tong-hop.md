---
papersize: a4
geometry:
  - top=20mm
  - bottom=20mm
  - left=20mm
  - right=20mm
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
  {\Large\bfseries THIẾT KẾ HƯỚNG ĐỐI TƯỢNG\par}
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

- [01. Kiến trúc hệ thống và phân tầng mã nguồn](#01-kiến-trúc-hệ-thống-và-phân-tầng-mã-nguồn)
  - [1.1 Tổng quan kiến trúc phân tầng](#11-tổng-quan-kiến-trúc-phân-tầng)
    - [1.1.1 Lý do lựa chọn kiến trúc phân tầng](#111-lý-do-lựa-chọn-kiến-trúc-phân-tầng)
    - [1.1.2 Sơ đồ kiến trúc phân tầng](#112-sơ-đồ-kiến-trúc-phân-tầng)
  - [1.2 Trách nhiệm và ranh giới của các tầng](#12-trách-nhiệm-và-ranh-giới-của-các-tầng)
  - [1.3 Cấu trúc gói mã nguồn](#13-cấu-trúc-gói-mã-nguồn)
  - [1.4 Luồng xử lý và biến đổi dữ liệu](#14-luồng-xử-lý-và-biến-đổi-dữ-liệu)
- [02. Thiết kế lớp thực thể và đối tượng giá trị](#02-thiết-kế-lớp-thực-thể-và-đối-tượng-giá-trị)
  - [2.1 Sơ đồ lớp tổng quan](#21-sơ-đồ-lớp-tổng-quan)
    - [2.1.1 Sơ đồ cấu trúc các lớp thực thể](#211-sơ-đồ-cấu-trúc-các-lớp-thực-thể)
    - [2.1.2 Danh mục lớp và kiểu dữ liệu](#212-danh-mục-lớp-và-kiểu-dữ-liệu)
  - [2.2 Thiết kế các lớp thực thể](#22-thiết-kế-các-lớp-thực-thể)
    - [2.2.1 Lớp Ví (Wallet)](#221-lớp-ví-wallet)
    - [2.2.2 Lớp giao dịch cơ sở và giao dịch thu chi](#222-lớp-giao-dịch-cơ-sở-và-giao-dịch-thu-chi)
    - [2.2.3 Lớp giao dịch chuyển tiền](#223-lớp-giao-dịch-chuyển-tiền)
    - [2.2.4 Lớp chi phí giao dịch](#224-lớp-chi-phí-giao-dịch)
    - [2.2.5 Lớp danh mục chi tiết](#225-lớp-danh-mục-chi-tiết)
    - [2.2.6 Lớp nhóm nhu cầu](#226-lớp-nhóm-nhu-cầu)
    - [2.2.7 Lớp bản ghi điều chỉnh số dư](#227-lớp-bản-ghi-điều-chỉnh-số-dư)
  - [2.3 Thiết kế các đối tượng giá trị](#23-thiết-kế-các-đối-tượng-giá-trị)
    - [2.3.1 Tiền tệ (Money)](#231-tiền-tệ-money)
    - [2.3.2 Ngày giao dịch (TransactionDate)](#232-ngày-giao-dịch-transactiondate)
    - [2.3.3 Kiểu liệt kê loại giao dịch (TransactionType)](#233-kiểu-liệt-kê-loại-giao-dịch-transactiontype)
    - [2.3.4 Dòng tiền thuần (NetCashFlow)](#234-dòng-tiền-thuần-netcashflow)
    - [2.3.5 Tỷ lệ tiết kiệm (SavingsRate)](#235-tỷ-lệ-tiết-kiệm-savingsrate)
    - [2.3.6 Tỷ trọng cơ cấu thu chi (SpendingStructureShare)](#236-tỷ-trọng-cơ-cấu-thu-chi-spendingstructureshare)
    - [2.3.7 Tên định danh thực thể (EntityName)](#237-tên-định-danh-thực-thể-entityname)
    - [2.3.8 Ghi chú diễn giải (Note)](#238-ghi-chú-diễn-giải-note)
- [03. Thiết kế tầng dịch vụ ứng dụng và điều phối nghiệp vụ](#03-thiết-kế-tầng-dịch-vụ-ứng-dụng-và-điều-phối-nghiệp-vụ)
  - [3.1 Tổng quan và danh mục dịch vụ ứng dụng](#31-tổng-quan-và-danh-mục-dịch-vụ-ứng-dụng)
  - [3.2 Dịch vụ quản lý ví (WalletService)](#32-dịch-vụ-quản-lý-ví-walletservice)
  - [3.3 Dịch vụ ghi nhận thu chi (TransactionService)](#33-dịch-vụ-ghi-nhận-thu-chi-transactionservice)
  - [3.4 Dịch vụ chuyển tiền nội bộ (TransferService)](#34-dịch-vụ-chuyển-tiền-nội-bộ-transferservice)
  - [3.5 Dịch vụ danh mục phân loại (CategoryService)](#35-dịch-vụ-danh-mục-phân-loại-categoryservice)
  - [3.6 Dịch vụ báo cáo và thống kê tài chính (ReportService)](#36-dịch-vụ-báo-cáo-và-thống-kê-tài-chính-reportservice)
  - [3.7 Dịch vụ quản trị và sao lưu dữ liệu (DataManagementService)](#37-dịch-vụ-quản-trị-và-sao-lưu-dữ-liệu-datamanagementservice)
- [04. Thiết kế tầng lưu trữ và truy xuất dữ liệu](#04-thiết-kế-tầng-lưu-trữ-và-truy-xuất-dữ-liệu)
  - [4.1 Giao diện lưu trữ cơ sở (GenericRepository)](#41-giao-diện-lưu-trữ-cơ-sở-genericrepository)
    - [4.1.1 Thao tác dữ liệu dùng chung](#411-thao-tác-dữ-liệu-dùng-chung)
    - [4.1.2 Quy chuẩn trả về và xử lý lỗi](#412-quy-chuẩn-trả-về-và-xử-lý-lỗi)
  - [4.2 Giao diện lưu trữ theo thực thể](#42-giao-diện-lưu-trữ-theo-thực-thể)
    - [4.2.1 Kho lưu trữ ví tiền (WalletRepository)](#421-kho-lưu-trữ-ví-tiền-walletrepository)
    - [4.2.2 Kho lưu trữ giao dịch (TransactionRepository)](#422-kho-lưu-trữ-giao-dịch-transactionrepository)
    - [4.2.3 Kho lưu trữ danh mục thu chi (CategoryRepository)](#423-kho-lưu-trữ-danh-mục-thu-chi-categoryrepository)
    - [4.2.4 Kho lưu trữ kiểm kê số dư (BalanceAdjustmentRepository)](#424-kho-lưu-trữ-kiểm-kê-số-dư-balanceadjustmentrepository)
    - [4.2.5 Kho lưu trữ sao lưu hệ thống (BackupRepository)](#425-kho-lưu-trữ-sao-lưu-hệ-thống-backuprepository)
  - [4.3 Cài đặt hạ tầng lưu trữ](#43-cài-đặt-hạ-tầng-lưu-trữ)
    - [4.3.1 Định dạng và cấu trúc bản ghi](#431-định-dạng-và-cấu-trúc-bản-ghi)
    - [4.3.2 Cơ chế đọc ghi và an toàn dữ liệu](#432-cơ-chế-đọc-ghi-và-an-toàn-dữ-liệu)
    - [4.3.3 Cài đặt lưu trữ trong bộ nhớ (Memory Repository)](#433-cài-đặt-lưu-trữ-trong-bộ-nhớ-memory-repository)
    - [4.3.4 So sánh đánh đổi giữa hai cơ chế lưu trữ](#434-so-sánh-đánh-đổi-giữa-hai-cơ-chế-lưu-trữ)
- [05. Sơ đồ tuần tự thiết kế](#05-sơ-đồ-tuần-tự-thiết-kế)
  - [5.1 Nguyên tắc thiết kế sơ đồ tuần tự chi tiết](#51-nguyên-tắc-thiết-kế-sơ-đồ-tuần-tự-chi-tiết)
  - [5.2 Sơ đồ tuần tự nhóm quản lý ví](#52-sơ-đồ-tuần-tự-nhóm-quản-lý-ví)
    - [5.2.1 Sơ đồ tạo mới ví](#521-sơ-đồ-tạo-mới-ví)
    - [5.2.2 Sơ đồ xem tổng hợp số dư và cơ cấu ví](#522-sơ-đồ-xem-tổng-hợp-số-dư-và-cơ-cấu-ví)
    - [5.2.3 Sơ đồ cập nhật thông tin ví](#523-sơ-đồ-cập-nhật-thông-tin-ví)
    - [5.2.4 Sơ đồ điều chỉnh số dư ví thực tế](#524-sơ-đồ-điều-chỉnh-số-dư-ví-thực-tế)
  - [5.3 Sơ đồ tuần tự nhóm ghi nhận thu chi](#53-sơ-đồ-tuần-tự-nhóm-ghi-nhận-thu-chi)
    - [5.3.1 Sơ đồ ghi nhận giao dịch thu chi](#531-sơ-đồ-ghi-nhận-giao-dịch-thu-chi)
    - [5.3.2 Sơ đồ tra cứu và lọc lịch sử giao dịch](#532-sơ-đồ-tra-cứu-và-lọc-lịch-sử-giao-dịch)
    - [5.3.3 Sơ đồ chỉnh sửa giao dịch thu chi](#533-sơ-đồ-chỉnh-sửa-giao-dịch-thu-chi)
    - [5.3.4 Sơ đồ xóa giao dịch thu chi](#534-sơ-đồ-xóa-giao-dịch-thu-chi)
  - [5.4 Sơ đồ tuần tự nhóm chuyển tiền nội bộ](#54-sơ-đồ-tuần-tự-nhóm-chuyển-tiền-nội-bộ)
    - [5.4.1 Sơ đồ chuyển tiền giữa hai ví](#541-sơ-đồ-chuyển-tiền-giữa-hai-ví)
    - [5.4.2 Sơ đồ tra cứu lịch sử chuyển tiền](#542-sơ-đồ-tra-cứu-lịch-sử-chuyển-tiền)
    - [5.4.3 Sơ đồ xóa và hoàn tác giao dịch chuyển tiền](#543-sơ-đồ-xóa-và-hoàn-tác-giao-dịch-chuyển-tiền)
  - [5.5 Sơ đồ tuần tự nhóm danh mục phân loại](#55-sơ-đồ-tuần-tự-nhóm-danh-mục-phân-loại)
    - [5.5.1 Sơ đồ thêm mới danh mục chi tiết](#551-sơ-đồ-thêm-mới-danh-mục-chi-tiết)
    - [5.5.2 Sơ đồ đổi tên danh mục](#552-sơ-đồ-đổi-tên-danh-mục)
    - [5.5.3 Sơ đồ khôi phục danh mục đã xóa](#553-sơ-đồ-khôi-phục-danh-mục-đã-xóa)
    - [5.5.4 Sơ đồ xóa danh mục (xóa cứng và xóa mềm)](#554-sơ-đồ-xóa-danh-mục-xóa-cứng-và-xóa-mềm)
  - [5.6 Sơ đồ tuần tự nhóm báo cáo và quản trị dữ liệu](#56-sơ-đồ-tuần-tự-nhóm-báo-cáo-và-quản-trị-dữ-liệu)
    - [5.6.1 Sơ đồ lập báo cáo dòng tiền định kỳ](#561-sơ-đồ-lập-báo-cáo-dòng-tiền-định-kỳ)
    - [5.6.2 Sơ đồ lập báo cáo cơ cấu thu chi](#562-sơ-đồ-lập-báo-cáo-cơ-cấu-thu-chi)
    - [5.6.3 Sơ đồ xuất dữ liệu sao lưu hệ thống](#563-sơ-đồ-xuất-dữ-liệu-sao-lưu-hệ-thống)
    - [5.6.4 Sơ đồ phục hồi dữ liệu hệ thống](#564-sơ-đồ-phục-hồi-dữ-liệu-hệ-thống)
    - [5.6.5 Sơ đồ đối soát toàn vẹn dữ liệu tài chính](#565-sơ-đồ-đối-soát-toàn-vẹn-dữ-liệu-tài-chính)
- [06. Áp dụng nguyên lý SOLID và mẫu thiết kế hướng đối tượng](#06-áp-dụng-nguyên-lý-solid-và-mẫu-thiết-kế-hướng-đối-tượng)
  - [6.1 Đánh giá tuân thủ năm nguyên lý SOLID](#61-đánh-giá-tuân-thủ-năm-nguyên-lý-solid)
  - [6.2 Áp dụng các mẫu thiết kế trong hệ thống](#62-áp-dụng-các-mẫu-thiết-kế-trong-hệ-thống)
    - [6.2.1 Mẫu Factory Method khởi tạo giao dịch](#621-mẫu-factory-method-khởi-tạo-giao-dịch)
    - [6.2.2 Mẫu Strategy định dạng báo cáo tài chính](#622-mẫu-strategy-định-dạng-báo-cáo-tài-chính)
    - [6.2.3 Mẫu State quản lý vòng đời ví tiền](#623-mẫu-state-quản-lý-vòng-đời-ví-tiền)
    - [6.2.4 Mẫu Observer xử lý sự kiện giao dịch](#624-mẫu-observer-xử-lý-sự-kiện-giao-dịch)
- [07. Thiết kế giao diện người dùng](#07-thiết-kế-giao-diện-người-dùng)
  - [7.1 Cấu trúc tầng giao diện người dùng](#71-cấu-trúc-tầng-giao-diện-người-dùng)
    - [7.1.1 Nguyên lý phân tách giao diện và xử lý sự kiện](#711-nguyên-lý-phân-tách-giao-diện-và-xử-lý-sự-kiện)
    - [7.1.2 Vòng đời ứng dụng đồ họa và luồng bắt sự kiện](#712-vòng-đời-ứng-dụng-đồ-họa-và-luồng-bắt-sự-kiện)
  - [7.2 Bố cục cửa sổ chính và màn hình phân hệ](#72-bố-cục-cửa-sổ-chính-và-màn-hình-phân-hệ)
    - [7.2.1 Cấu trúc phân vùng cửa sổ chính](#721-cấu-trúc-phân-vùng-cửa-sổ-chính)
    - [7.2.2 Ánh xạ màn hình chức năng và ca sử dụng](#722-ánh-xạ-màn-hình-chức-năng-và-ca-sử-dụng)
    - [7.2.3 Quy chuẩn hiển thị bảng dữ liệu và hộp thoại](#723-quy-chuẩn-hiển-thị-bảng-dữ-liệu-và-hộp-thoại)
  - [7.3 Thiết kế biểu mẫu nhập liệu và bắt sự kiện](#73-thiết-kế-biểu-mẫu-nhập-liệu-và-bắt-sự-kiện)
    - [7.3.1 Xác thực biểu mẫu đồ họa](#731-xác-thực-biểu-mẫu-đồ-họa)
    - [7.3.2 Phản hồi người dùng và hộp thoại xác nhận](#732-phản-hồi-người-dùng-và-hộp-thoại-xác-nhận)
  - [7.4 Thiết kế các đối tượng truyền dữ liệu (DTO)](#74-thiết-kế-các-đối-tượng-truyền-dữ-liệu-dto)
    - [7.4.1 Mục đích và tính đóng gói của đối tượng truyền dữ liệu](#741-mục-đích-và-tính-đóng-gói-của-đối-tượng-truyền-dữ-liệu)
    - [7.4.2 Danh mục các lớp truyền dữ liệu chi tiết](#742-danh-mục-các-lớp-truyền-dữ-liệu-chi-tiết)
- [08. Thiết kế xử lý ngoại lệ và nhật ký](#08-thiết-kế-xử-lý-ngoại-lệ-và-nhật-ký)
  - [8.1 Chiến lược quản lý ngoại lệ toàn hệ thống](#81-chiến-lược-quản-lý-ngoại-lệ-toàn-hệ-thống)
    - [8.1.1 Phân loại ngoại lệ kỹ thuật và ngoại lệ nghiệp vụ](#811-phân-loại-ngoại-lệ-kỹ-thuật-và-ngoại-lệ-nghiệp-vụ)
    - [8.1.2 Nguyên tắc bắt và chuyển đổi ngoại lệ xuyên tầng](#812-nguyên-tắc-bắt-và-chuyển-đổi-ngoại-lệ-xuyên-tầng)
  - [8.2 Cây phân cấp lớp ngoại lệ hệ thống](#82-cây-phân-cấp-lớp-ngoại-lệ-hệ-thống)
    - [8.2.1 Sơ đồ phân cấp lớp ngoại lệ hướng đối tượng](#821-sơ-đồ-phân-cấp-lớp-ngoại-lệ-hướng-đối-tượng)
    - [8.2.2 Danh mục các lớp ngoại lệ nghiệp vụ chi tiết](#822-danh-mục-các-lớp-ngoại-lệ-nghiệp-vụ-chi-tiết)
  - [8.3 Bảng mã lỗi nghiệp vụ chuẩn hóa](#83-bảng-mã-lỗi-nghiệp-vụ-chuẩn-hóa)
    - [8.3.1 Cấu trúc định danh mã lỗi hệ thống](#831-cấu-trúc-định-danh-mã-lỗi-hệ-thống)
    - [8.3.2 Danh mục mã lỗi và thông điệp phản hồi](#832-danh-mục-mã-lỗi-và-thông-điệp-phản-hồi)
- [09. Ma trận truy vết thiết kế](#09-ma-trận-truy-vết-thiết-kế)
  - [9.1 Ánh xạ yêu cầu chức năng sang lớp thiết kế](#91-ánh-xạ-yêu-cầu-chức-năng-sang-lớp-thiết-kế)
  - [9.2 Đánh giá độ bao phủ và tính nhất quán thiết kế](#92-đánh-giá-độ-bao-phủ-và-tính-nhất-quán-thiết-kế)
- [10. Tổng kết](#10-tổng-kết)
- [11. Tài liệu tham khảo](#11-tài-liệu-tham-khảo)

\newpage

# 01. Kiến trúc hệ thống và phân tầng mã nguồn

Tài liệu này xác lập kiến trúc tổng thể cho Hệ thống quản lý tài chính cá nhân (PFMS) giao diện đồ họa (GUI), thực thi đơn tiến trình cục bộ trên máy tính cá nhân. Hệ thống được tổ chức theo mô hình kiến trúc phân tầng gồm 4 tầng chức năng độc lập và 1 tầng nền tảng kỹ thuật dùng chung (`com.pfms.common`).

Kiến trúc giải quyết ba mục tiêu cốt lõi: bảo vệ tính toàn vẹn của quy tắc tài chính thông qua nguyên lý nghịch đảo phụ thuộc (DIP); chuẩn hóa phân cấp gói `com.pfms` không chu trình phụ thuộc; và bảo đảm luồng xử lý dữ liệu hai chiều khép kín giữa biểu mẫu đồ họa và hệ thống tệp lưu trữ.

## 1.1 Tổng quan kiến trúc phân tầng

Mục này so sánh kiến trúc phân tầng với các mô hình kiến trúc khác và đặc tả sơ đồ phân tầng thể hiện chiều phụ thuộc lúc biên dịch cùng luồng gọi hàm lúc thực thi.

### 1.1.1 Lý do lựa chọn kiến trúc phân tầng

Hệ thống quản lý các nguồn tiền, ghi nhận thu chi, chuyển khoản nội bộ và ngân sách. Nghiệp vụ đòi hỏi số học chính xác tuyệt đối, số dư không âm và bảo toàn dòng tiền.

Bảng so sánh định lượng giữa các mô hình kiến trúc:

| Tiêu chí kỹ thuật | Mô hình kịch bản giao dịch | **Mô hình kiến trúc phân tầng** | Mô hình kiến trúc vi dịch vụ | Mô hình kiến trúc hướng sự kiện |
| :--- | :--- | :--- | :--- | :--- |
| Mức độ ghép nối | Rất cao: Giao diện gắn chặt logic và tệp. | Rất thấp: Tương tác qua interface và DTO. | Rất thấp: Độc lập qua tiến trình mạng. | Rất thấp: Ghép nối lỏng phát/nhận sự kiện. |
| Mức độ gắn kết | Rất thấp: Một hàm xử lý I/O, nghiệp vụ và tệp. | Cao: Mỗi lớp đảm nhiệm một trách nhiệm. | Cao: Mỗi dịch vụ bao trọn một miền. | Cao: Mỗi thành phần xử lý một loại sự kiện. |
| Tính nhất quán dữ liệu | Kém: Dễ sai lệch số dư khi lỗi quy trình. | Rất cao: Giao dịch nguyên tử, nhất quán tức thời. | Thấp: Cần giao dịch phân tán, nhất quán cuối. | Thấp: Bất đồng bộ, chỉ đạt nhất quán cuối. |
| Khả năng kiểm thử | Rất kém: Phụ thuộc GUI và tệp vật lý. | Rất tốt: Kiểm thử tự động độc lập bằng Unit Test. | Kém: Cần môi trường mạng và dịch vụ phụ thuộc. | Kém: Khó tái hiện kịch bản bất đồng bộ. |
| Thay đổi công nghệ | Rất kém: Đổi GUI hoặc tệp làm vỡ toàn bộ mã. | Rất tốt: Tráo đổi GUI hoặc lưu trữ độc lập. | Rất tốt: Công nghệ riêng cho từng dịch vụ. | Tốt: Dễ thêm thành phần mà không sửa mã cũ. |
| Chi phí vận hành | Rất thấp: Đơn tiến trình, không hạ tầng phụ. | Thấp: Chạy trên một JVM, không tốn tài nguyên mạng. | Rất cao: Cần quản lý mạng, service discovery, container. | Rất cao: Cần message broker và quản lý luồng. |
| Mức độ phù hợp | Không phù hợp: Dễ sai lệch khi mở rộng nghiệp vụ. | **Tối ưu**: Bảo đảm toàn vẹn dữ liệu, dễ kiểm thử cục bộ. | Không phù hợp: Quá phức tạp cho ứng dụng đơn máy. | Không phù hợp: Bất đồng bộ không cần thiết cho desktop app. |

Áp dụng kiến trúc phân tầng chấp nhận chi phí ánh xạ DTO qua các lớp trung gian để đổi lấy tính độc lập của quy tắc tài chính và năng lực kiểm thử tự động toàn diện. Ranh giới giữa các tầng tuân thủ nguyên tắc đóng kín, ngăn giao tiếp vượt cấp giữa tầng trình diễn và tầng lưu trữ.

### 1.1.2 Sơ đồ kiến trúc phân tầng

Hệ thống gồm 4 tầng chức năng độc lập (hướng tâm, đóng kín) và 1 tầng nền tảng dùng chung:
0. **Tầng nền tảng dùng chung (Common Layer)**: Chứa tiện ích tiền điều kiện (`Preconditions`) và hàm bổ trợ phi nghiệp vụ, không phụ thuộc tầng nào.
1. **Tầng trình diễn (Presentation Layer)**: Tiếp nhận sự kiện người dùng, điều hướng màn hình, đóng gói DTO và kết xuất bảng dữ liệu hiển thị.
2. **Tầng dịch vụ (Application Layer)**: Điều phối ca sử dụng, quản lý giao dịch tài chính, nạp dữ liệu và ủy quyền tính toán cho tầng nghiệp vụ.
3. **Tầng nghiệp vụ cốt lõi (Domain Layer)**: Trung tâm hệ thống, chứa thực thể, đối tượng giá trị bất biến, quy tắc tài chính và giao diện repository trừu tượng.
4. **Tầng hạ tầng (Infrastructure Layer)**: Cài đặt cụ thể các giao diện repository bằng tệp JSON cục bộ hoặc lưu trữ bộ nhớ (in-memory) phục vụ kiểm thử.

Sơ đồ khối cấu trúc phân tầng và luồng thực thi:

![Sơ đồ Kiến trúc Phân tầng Hệ thống](docs/object-oriented-design/images/so-do-1.png){width=80%}


Nguyên tắc phụ thuộc và luồng thực thi:
- **Phụ thuộc mã nguồn (Compile-time)**: Hướng tâm về tầng nghiệp vụ. Tầng hạ tầng phụ thuộc ngược vào interface repository của tầng nghiệp vụ (DIP), giúp nghiệp vụ độc lập hoàn toàn với hệ thống tệp. Tầng nền tảng dùng chung (`com.pfms.common`) độc lập tuyệt đối (zero internal dependency), dùng chung cho cả 4 tầng mà không tạo phụ thuộc vòng.
- **Luồng thực thi (Runtime)**: Đi từ tầng trình diễn $\rightarrow$ tầng dịch vụ $\rightarrow$ tầng nghiệp vụ $\rightarrow$ bộ điều hợp tầng hạ tầng.

## 1.2 Trách nhiệm và ranh giới của các tầng

Bảng phân định trách nhiệm và ràng buộc ranh giới giữa các tầng kiến trúc:

| Tầng kiến trúc | Trách nhiệm chính | Ràng buộc ranh giới |
| :--- | :--- | :--- |
| **Tầng trình diễn** | Tiếp nhận sự kiện, điều hướng màn hình, đóng gói DTO và kết xuất giao diện đồ họa. | Không chứa logic tính toán tài chính; không gọi trực tiếp repository; không tham chiếu thực thể domain. |
| **Tầng dịch vụ** | Điều phối ca sử dụng, quản lý giao dịch, nạp thực thể từ repository và ủy quyền tính toán cho domain. | Không chứa quy tắc kiểm tra tài chính chuyên sâu; không phụ thuộc cơ chế lưu trữ tệp cụ thể. |
| **Tầng nghiệp vụ** | Thực thi quy tắc bất biến tài chính, tính toán số học chính xác, kiểm soát số dư không âm và khai báo interface repository. | Độc lập hoàn toàn với 3 tầng còn lại; không tham chiếu GUI hay tệp tin; chỉ dùng thư viện Java chuẩn và `com.pfms.common`. |
| **Tầng hạ tầng** | Hiện thực hóa interface repository bằng đọc/ghi tệp cục bộ hoặc lưu trữ bộ nhớ phục vụ kiểm thử. | Không thay đổi quy tắc tài chính; chỉ chuyển đổi giữa dữ liệu lưu trữ thô và đối tượng nghiệp vụ. |
| **Tầng nền tảng dùng chung** | Cung cấp tiện ích tiền điều kiện (`Preconditions`) và hàm bổ trợ kỹ thuật phi nghiệp vụ. | Tuyệt đối không chứa logic nghiệp vụ; không import bất kỳ lớp nào thuộc 4 tầng chức năng trên. |

## 1.3 Cấu trúc gói mã nguồn

Cấu trúc gói định hình không gian tên vật lý trong mã nguồn Java, bảo đảm không tồn tại chu trình phụ thuộc vòng giữa các gói. Toàn bộ mã nguồn đặt dưới gói gốc `com.pfms`:

```text
com.pfms
├── common                  # Tầng nền tảng dùng chung: Tiện ích kỹ thuật phi nghiệp vụ
│   └── util                # Tiền điều kiện kỹ thuật (Preconditions)
├── presentation            # Tầng trình diễn: Giao diện đồ họa (GUI)
│   ├── controller          # Điều phối luồng màn hình và xử lý sự kiện giao diện
│   ├── view                # Cửa sổ chính, biểu mẫu và bảng hiển thị
│   └── dto                 # Đối tượng truyền dữ liệu (DTO) giữa giao diện và dịch vụ
├── service                 # Tầng dịch vụ: Interface điều phối ca sử dụng nghiệp vụ
│   └── impl                # Cài đặt dịch vụ và lớp hỗ trợ điều phối nội bộ
├── domain                  # Tầng nghiệp vụ: Thực thể, quy tắc và hợp đồng repository
│   ├── model               # Thực thể và đối tượng giá trị bất biến
│   ├── repository          # Giao diện kho lưu trữ trừu tượng
│   ├── service             # Dịch vụ miền xử lý quy tắc đa thực thể
│   └── exception           # Ngoại lệ nghiệp vụ tài chính
└── infrastructure          # Tầng hạ tầng: Bộ điều hợp tệp cục bộ và bộ nhớ kiểm thử
    ├── file                # Hiện thực hóa repository bằng đọc/ghi tệp JSON
    └── memory              # Hiện thực hóa repository trong RAM phục vụ kiểm thử
```

**Ma trận quyền truy cập và phụ thuộc gói:**

| Gói nguồn | Gói đích được phép import | Phạm vi truy cập | Thành phần được phép sử dụng |
| :--- | :--- | :--- | :--- |
| `presentation` | `service`, `presentation.dto`, `common.util` | `public` | Gọi phương thức công khai của service interface và tiện ích kiểm tra; không import `domain` hoặc `infrastructure`. |
| `service` | `domain`, `presentation.dto`, `common.util` | `public` | Thao tác trên entity, value object, gọi interface `domain.repository`; không import `presentation` hoặc `infrastructure`. |
| `domain` | `common.util`, Thư viện Java chuẩn | `public` | Độc lập khỏi các tầng ngoài; chỉ dùng `common.util` và thư viện chuẩn (`java.time`, `java.math`, `java.util`). |
| `infrastructure` | `domain`, `common.util` | `public` | Hiện thực hóa interface từ `domain.repository` và dùng tiện ích kỹ thuật; không import `service` hoặc `presentation`. |
| `common` | Thư viện Java chuẩn | `public` | Độc lập tuyệt đối (zero internal dependency); không import bất kỳ gói nào trong `com.pfms`. |

**Quy tắc kiểm soát phạm vi lớp nội bộ:**
1. **Phạm vi `public`**: Khai báo cho service interface, lớp cài đặt service trong `service.impl` (phục vụ tiêm phụ thuộc), lớp tiện ích dùng chung liên tầng (`EntityLookupSupport`, `RollbackSupport`), interface repository, DTO và thực thể domain.
2. **Phạm vi `package-private`**: Giới hạn cho các lớp hỗ trợ nội bộ phân hệ dịch vụ (`FileStorageSupport`, `SystemDataReconciler`, `TransactionPersistenceCoordinator`, `TransferPersistenceCoordinator`, `CategoryHierarchySupport`, `WalletSummaryCalculator`, `SpendingPeriodAccumulator`) để tránh phơi bày chi tiết nội bộ ra ngoài gói.
3. **Lớp tiện ích `common.util`**: Thiết kế theo chuẩn Utility Class (phương thức `public static`, constructor `private` ném ngoại lệ ngăn khởi tạo đối tượng).

## 1.4 Luồng xử lý và biến đổi dữ liệu

Hệ thống chuyển đổi dữ liệu qua từng tầng theo quy trình khép kín, ngăn giao diện đồ họa tiếp xúc trực tiếp với thực thể nghiệp vụ hoặc cấu trúc tệp tin.

**Sơ đồ luồng dữ liệu hai chiều:**

![Luồng xử lý và biến đổi dữ liệu - PFMS](docs/object-oriented-design/images/so-do-luong-xu-ly-du-lieu.png){width=80%}

**Bảng đặc tả xử lý tại các ranh giới:**

| Ranh giới | Chiều đi | Chiều về | Trách nhiệm xử lý |
| :--- | :--- | :--- | :--- |
| **Người dùng $\leftrightarrow$ Trình diễn** | Biểu mẫu nhập $\rightarrow$ `Request DTO` | `Response DTO` $\rightarrow$ Hiển thị `JTable` / Hộp thoại | Kiểm tra dữ liệu biểu mẫu không rỗng, đúng kiểu số; định dạng tiền tệ và ngày tháng khi hiển thị. |
| **Trình diễn $\leftrightarrow$ Dịch vụ** | `Request DTO` $\rightarrow$ Phương thức dịch vụ | Kết quả xử lý $\rightarrow$ `Response DTO` | Nhận DTO từ giao diện để thực thi ca sử dụng; đóng gói kết quả trả về thành DTO, không trả trực tiếp thực thể. |
| **Dịch vụ $\leftrightarrow$ Nghiệp vụ** | Tham số dịch vụ $\rightarrow$ Thực thể & Đối tượng giá trị | Thực thể sau xử lý $\rightarrow$ Dịch vụ | Tính toán số học tiền tệ, kiểm tra số dư không âm và cập nhật trạng thái thực thể. |
| **Nghiệp vụ $\leftrightarrow$ Hạ tầng** | Thực thể $\rightarrow$ Bản ghi tệp | Bản ghi tệp $\rightarrow$ Thực thể | Chuyển thuộc tính thực thể thành dòng văn bản/JSON lưu tệp; đọc tệp để nạp lại thực thể vào bộ nhớ. |

\newpage

# 02. Thiết kế lớp thực thể và đối tượng giá trị

Tài liệu này xác lập thiết kế chi tiết cho các lớp thuộc tầng nghiệp vụ cốt lõi (`com.pfms.domain.model`) của hệ thống Quản lý Tài chính Cá nhân (PFMS), chuyển đổi các thực thể miền và đối tượng giá trị từ mô hình phân tích sang cấu trúc cài đặt Java hoàn chỉnh.

Thiết kế tập trung vào ba mục tiêu kỹ thuật trọng tâm: bảo toàn độ chính xác số học qua đối tượng giá trị tiền tệ bất biến với quy tắc làm tròn ngân hàng (`HALF_EVEN`); đóng gói chặt chẽ để bảo vệ trạng thái nội tại và duy trì số dư khả dụng không âm; và hiện thực hóa tính đa hình trong luồng hạch toán dòng tiền qua lớp cơ sở trừu tượng mà không dùng rẽ nhánh thủ công.

## 2.1 Sơ đồ lớp tổng quan

Mục này trực quan hóa cấu trúc tĩnh của tầng nghiệp vụ, thể hiện mối quan hệ giữa các lớp thực thể quản trị trạng thái và các đối tượng giá trị bất biến (kế thừa, hợp thành, kết tập và liên kết). Bảng danh mục đi kèm tổng hợp trách nhiệm kỹ thuật của từng thành phần, đảm bảo tính truy vết hai chiều với mô hình phân tích.

### 2.1.1 Sơ đồ cấu trúc các lớp thực thể

Sơ đồ dưới đây mô hình hóa cấu trúc tĩnh của 8 lớp thực thể cốt lõi thuộc tầng nghiệp vụ. Sơ đồ tập trung thể hiện bốn loại quan hệ cấu trúc hướng đối tượng then chốt gồm kế thừa, hợp thành, kết tập và liên kết, định hình xương sống dòng tiền và phân cấp danh mục của toàn bộ hệ thống:

![Sơ đồ lớp miền nghiệp vụ PFMS](docs/object-oriented-design/images/so-do-lop-mien-nghiep-vu.png){width=80%}

Bản vẽ thiết lập bốn loại quan hệ cấu trúc hướng đối tượng cốt lõi trong mô hình miền:

1. **Quan hệ kế thừa**: Áp dụng để trừu tượng hóa các thuộc tính và hành vi chung của dòng tiền, cho phép các lớp giao dịch cụ thể đa hình hóa việc hạch toán số dư mà không phụ thuộc vào các cấu trúc điều kiện rẽ nhánh thủ công.
2. **Quan hệ hợp thành**: Áp dụng cho các thành phần tài chính có sự ràng buộc sinh tử, bảo đảm các khoản chi phí liên kết luôn gắn liền với vòng đời của giao dịch gốc và loại trừ hoàn toàn nguy cơ phát sinh dữ liệu mồ côi.
3. **Quan hệ kết tập**: Áp dụng để tổ chức cấu trúc phân cấp danh mục nhiều cấp, cho phép nhóm phân loại cấp trên gom cụm các danh mục chi tiết cấp dưới mà vẫn duy trì tính độc lập của từng phần tử.
4. **Quan hệ liên kết**: Thể hiện sự tương tác nghiệp vụ giữa các thực thể có định danh và vòng đời độc lập, bảo đảm mọi biến động tài chính luôn truy nguyên được nguồn tiền chịu tác động và mục đích phân loại.

Các đối tượng giá trị bất biến và kiểu liệt kê nghiệp vụ đóng vai trò là kiểu dữ liệu cấu thành trạng thái của các thực thể trên, được tra cứu tổng hợp tại bảng danh mục ở Mục 2.1.2 và đặc tả chi tiết tại Mục 2.3.

### 2.1.2 Danh mục lớp và kiểu dữ liệu

Bảng dưới đây tổng hợp danh mục toàn bộ các lớp thực thể, lớp khởi tạo, đối tượng giá trị và kiểu liệt kê trong tầng nghiệp vụ cốt lõi. Toàn bộ các thành phần này được định vị vật lý trong gói mã nguồn `com.pfms.domain.model` và được phân loại theo các nhóm nghiệp vụ tương ứng của hệ thống:

| Tên lớp / Kiểu dữ liệu | Phân loại OOP | Trách nhiệm kỹ thuật chính |
| :--- | :--- | :--- |
| **1. Nhóm Quản lý Ví (WAL)** |  |  |
| `Wallet` | Thực thể | Quản lý nguồn tiền khả dụng, bảo toàn bất biến số dư không âm và tiếp nhận các biến động dòng tiền. |
| `BalanceAdjustmentRecord` | Thực thể | Bản ghi kiểm kê thực tế bất biến, ghi nhận chênh lệch kiểm kê để cân đối số dư ví về đúng thực tế quan sát. |
| **2. Nhóm Ghi nhận Thu chi (TXN)** |  |  |
| `Transaction` | Thực thể trừu tượng | Lớp cơ sở trừu tượng định nghĩa các thuộc tính chung và hợp đồng phương thức đa hình cho giao dịch thu chi cùng chuyển tiền nội bộ. |
| `IncomeExpenseTransaction` | Thực thể | Hiện thực hóa giao dịch thu nhập hoặc chi tiêu tại một ví, liên kết phân loại trực tiếp với danh mục cấp 3. |
| `TransactionFee` | Thực thể | Quản lý khoản chi phí phát sinh phụ thuộc sinh tử kèm theo giao dịch thu chi hoặc chuyển tiền nội bộ. |
| `TransactionFactory` | Lớp khởi tạo | Đóng gói quy trình khởi tạo đa hình `IncomeExpenseTransaction`, `TransferTransaction` kèm bản ghi `TransactionFee` hợp thành. |
| `TransactionType` | Kiểu liệt kê | Xác định chiều tác động của dòng tiền gồm Thu nhập (`INCOME`) hoặc Chi tiêu (`EXPENSE`). |
| **3. Nhóm Chuyển tiền Nội bộ (TRF)** |  |  |
| `TransferTransaction` | Thực thể | Hiện thực hóa giao dịch điều chuyển tiền nội bộ giữa hai ví độc lập theo cơ chế bảo toàn tổng tài sản. |
| **4. Nhóm Danh mục Phân loại (CAT)** |  |  |
| `NeedGroup` | Thực thể | Quản lý nhóm nhu cầu cấp 2 gom cụm các danh mục chi tiết, trực thuộc một nhánh loại thu chi cố định. |
| `Category` | Thực thể | Quản lý danh mục chi tiết cấp 3 gán nhãn mục đích cho giao dịch thu chi, áp dụng cơ chế xóa mềm bảo toàn lịch sử. |
| **5. Nhóm Thống kê, Báo cáo (REP) và Đối tượng Giá trị Dùng chung** |  |  |
| `EntityStatus` | Kiểu liệt kê | Trạng thái vòng đời dùng chung cho các thực thể miền (Ví, Danh mục, Nhóm nhu cầu), gồm Đang hoạt động (`ACTIVE`), Đã lưu trữ (`ARCHIVED`), Đã xóa (`DELETED`). |
| `EntityName` | Đối tượng giá trị | Bọc tên định danh cho Ví và Danh mục, chuẩn hóa khoảng trắng và bảo đảm độ dài 1 đến 50 ký tự theo quy tắc đặt tên `BR-NAM-01`. |
| `Note` | Đối tượng giá trị | Bọc chuỗi ghi chú diễn giải cho Ví và Giao dịch, giới hạn tối đa 255 ký tự kèm cơ chế đối tượng rỗng an toàn (`Null Object`). |
| `Money` | Đối tượng giá trị | Bọc giá trị tiền tệ bằng `BigDecimal` kèm quy tắc làm tròn nửa chẵn, loại trừ hoàn toàn sai số dấu chấm động. |
| `TransactionDate` | Đối tượng giá trị | Bọc mốc ngày phát sinh giao dịch, kiểm soát tính hợp lệ lịch pháp và ràng buộc thời gian không vượt quá hiện tại. |
| `NetCashFlow` | Đối tượng giá trị | Đo lường dòng tiền thuần ròng bằng tổng thu nhập trừ tổng chi tiêu trong một kỳ quan sát định kỳ. |
| `SavingsRate` | Đối tượng giá trị | Đo lường tỷ lệ phần trăm tiền tiết kiệm tích lũy được so với tổng thu nhập tạo ra trong kỳ phân tích. |
| `SpendingStructureShare` | Đối tượng giá trị | Đo lường tỷ trọng phần trăm đóng góp của từng nhóm nhu cầu trên tổng kỳ hoặc của từng danh mục chi tiết trên tổng kỳ và tổng nhóm cha (`EXPENSE` gồm phí liên kết hoặc `INCOME`). |

> [!NOTE]
> **Nhóm Lưu trữ và Quản trị Dữ liệu (DAT):** Các chức năng sao lưu dữ liệu, khôi phục và nạp dữ liệu mẫu được hiện thực hóa thông qua các dịch vụ thuộc tầng dịch vụ (`com.pfms.service`) và các bộ điều hợp tệp thuộc tầng hạ tầng (`com.pfms.infrastructure`). tầng nghiệp vụ cốt lõi không định nghĩa lớp thực thể riêng cho nhóm này nhằm bảo đảm tính độc lập hoàn toàn khỏi hệ thống tệp cục bộ theo nguyên lý nghịch đảo phụ thuộc.

## 2.2 Thiết kế các lớp thực thể

Mục này đặc tả chi tiết cấu trúc cho các lớp thực thể thuộc gói `com.pfms.domain.model`. Mỗi thực thể đại diện cho một đối tượng nghiệp vụ có mã định danh duy nhất (`UUID`) và duy trì vòng đời biến đổi trạng thái xuyên suốt quá trình vận hành.

Mỗi lớp thực thể được quy chuẩn cấu trúc đồng nhất qua ba nội dung:
1. **Cấu trúc thuộc tính**: Tên trường dữ liệu, kiểu dữ liệu, phạm vi truy cập nội bộ (`private`/`protected`) và ràng buộc hợp lệ.
2. **Phương thức nghiệp vụ**: Chữ ký phương thức, danh sách tham số, kiểu trả về, ngoại lệ và tóm tắt xử lý nghiệp vụ.
3. **Cơ chế đóng gói và quy tắc bất biến**: Logic bảo vệ toàn vẹn dữ liệu, kiểm tra điều kiện tiên quyết và loại bỏ setter tự do nhằm bảo vệ trạng thái nội tại.

### 2.2.1 Lớp Ví (Wallet)

Lớp `Wallet` đại diện cho nguồn tiền thanh toán của người dùng, tiếp nhận biến động dòng tiền và giữ số dư khả dụng không âm. Hệ thống dùng lớp này cho ví tiền mặt mặc định cùng các ví tạo thêm để tính cơ cấu phân bổ tài sản.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`, `UC-WAL-02`, `UC-WAL-03`, `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`, `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-WAL-001`, `ERR-FIN-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `walletId` | `UUID` | `private final` | Mã định danh duy nhất sinh tự động khi tạo mới, giữ cố định và không tái sử dụng. |
| `name` | `EntityName` | `private` | Đối tượng giá trị bọc tên ví từ 1 đến 50 ký tự sau khi chuẩn hóa khoảng trắng, duy nhất toàn hệ thống không phân biệt hoa thường. |
| `currentBalance` | `Money` | `private` | Số dư khả dụng có đúng 2 chữ số thập phân, khởi tạo bằng `0.00 VNĐ` và luôn không âm. |
| `status` | `EntityStatus` | `private` | Trạng thái vòng đời nhận giá trị `ACTIVE` khi tạo mới hoặc chuyển sang `ARCHIVED` khi ngừng giao dịch. |
| `isDefault` | `boolean` | `private final` | Cờ đánh dấu ví "Tiền mặt" mặc định; mang giá trị `true` cố định ở ví mặc định để chặn thao tác xóa hoặc lưu trữ. |
| `note` | `Note` | `private` | Đối tượng giá trị bọc chuỗi ghi chú tùy chọn, dài tối đa 255 ký tự, áp dụng đối tượng rỗng an toàn (`Note.EMPTY`) khi để trống. |
| `createdAt` | `LocalDateTime` | `private final` | Mốc thời gian tạo ví bất biến, dùng để sắp xếp danh sách ví. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `Wallet` | `UUID walletId, String name, String note, boolean isDefault` | `Wallet` | `BusinessException, IllegalArgumentException` | Kiểm tra `walletId` khác `null`; chuẩn hóa và đóng gói `name` vào `EntityName` (1-50 ký tự theo `ERR_NAM_001`) và `note` vào `Note` (tối đa 255 ký tự); gán `status = ACTIVE`, `currentBalance = Money.ZERO` và `createdAt` hiện tại. |
| `credit` | `Money amount` | `void` | `BusinessException, NullPointerException` | Kiểm tra ví đang `ACTIVE` (`ERR_WAL_001`) và `amount` lớn hơn `0.00 VNĐ` (`ERR_FIN_001`), sau đó cộng `amount` vào `currentBalance`. |
| `debit` | `Money amount` | `void` | `BusinessException, InsufficientBalanceException, NullPointerException` | Kiểm tra ví đang `ACTIVE` (`ERR_WAL_001`), `amount` lớn hơn `0.00 VNĐ` (`ERR_FIN_001`) và `currentBalance` đủ chi trả (`ERR_WAL_001`), sau đó trừ `amount` khỏi `currentBalance`. |
| `adjustBalance` | `Money actualBalance, TransactionDate adjustedAt, String reason` | `BalanceAdjustmentRecord` | `BusinessException, IllegalArgumentException` | Kiểm tra ví đang `ACTIVE` (`ERR_WAL_001`), khởi tạo và xác thực bản ghi `BalanceAdjustmentRecord` trước để bảo đảm `actualBalance` không âm (`ERR_WAL_001`), độ lệch khác không (`ERR_FIN_001`) và `reason` hợp lệ, sau đó mới cập nhật `currentBalance = actualBalance` rồi trả về bản ghi. |
| `updateInfo` | `String newName, String newNote` | `void` | `BusinessException, IllegalArgumentException` | Chuẩn hóa và đóng gói `newName` vào `EntityName` (1-50 ký tự theo `ERR_NAM_001`) và `newNote` vào `Note` (tối đa 255 ký tự); cập nhật tên cùng ghi chú mà giữ nguyên số dư. |
| `archive` | `Không` | `void` | `IllegalStateException, BusinessException` | Kiểm tra `isDefault == false` và ví đang `ACTIVE`, sau đó chuyển `status` sang `ARCHIVED`. |
| `activate` | `Không` | `void` | `IllegalStateException, BusinessException` | Kiểm tra ví đang `ARCHIVED` rồi chuyển `status` về `ACTIVE` để ví tiếp nhận lại giao dịch mới. |
| `getWalletId` | `Không` | `UUID` | `Không` | Trả về mã định danh duy nhất của ví. |
| `getName` | `Không` | `String` | `Không` | Trả về chuỗi tên ví hiện tại. |
| `getEntityName` | `Không` | `EntityName` | `Không` | Trả về đối tượng giá trị tên ví. |
| `getNote` | `Không` | `String` | `Không` | Trả về chuỗi ghi chú diễn giải của ví. |
| `getNoteValue` | `Không` | `Note` | `Không` | Trả về đối tượng giá trị ghi chú của ví. |
| `getStatus` | `Không` | `EntityStatus` | `Không` | Trả về trạng thái vòng đời hiện tại của ví. |
| `isDefault` | `Không` | `boolean` | `Không` | Trả về `true` nếu đây là ví tiền mặt mặc định. |
| `isActive` | `Không` | `boolean` | `Không` | Kiểm tra ví có đang ở trạng thái `ACTIVE` hay không. |
| `isArchived` | `Không` | `boolean` | `Không` | Kiểm tra ví có đang ở trạng thái `ARCHIVED` hay không. |
| `isDeleted` | `Không` | `boolean` | `Không` | Kiểm tra ví có đang ở trạng thái `DELETED` hay không. |
| `getCurrentBalance` | `Không` | `Money` | `Không` | Trả về số dư khả dụng hiện tại của ví. |
| `hasSufficientBalance` | `Money amount` | `boolean` | `NullPointerException` | Kiểm tra tham số khác `null` và trả về `true` khi `currentBalance` lớn hơn hoặc bằng `amount`. |

**Cơ chế đóng gói và quy tắc bất biến**

- Hàm khởi tạo chuẩn hóa tên ví qua đối tượng giá trị `EntityName` (1 đến 50 ký tự theo `ERR_NAM_001`) và ghi chú qua `Note` (tối đa 255 ký tự), gán trạng thái `ACTIVE` và đặt `currentBalance` bằng `0.00 VNĐ`. Thực thể kiểm tra định dạng tên nội bộ qua `EntityName`, còn tầng dịch vụ kiểm tra tính duy nhất của tên trên toàn hệ thống.
- Lớp `Wallet` đóng kín các trường nội tại, chỉ mở các phương thức đọc chỉ đọc và loại bỏ mọi phương thức gán giá trị tự do như `setCurrentBalance`. Số dư chỉ thay đổi qua `credit`, `debit` hoặc `adjustBalance` để duy trì số dư khả dụng không âm và chặn mọi khoản chi vượt số dư.
- Từ khóa `final` khóa cố định thuộc tính `isDefault` ngay khi tạo ví "Tiền mặt" mặc định, giúp phương thức `archive` và tầng dịch vụ chặn thao tác lưu trữ hoặc xóa nguồn tiền mặc định.

### 2.2.2 Lớp giao dịch cơ sở và giao dịch thu chi

Lớp trừu tượng `Transaction` định nghĩa các thuộc tính cốt lõi chung và cặp phương thức hợp đồng đa hình cho mọi biến động dòng tiền trong hệ thống. Lớp con cụ thể `IncomeExpenseTransaction` hiện thực hóa giao dịch thu nhập hoặc chi tiêu phát sinh tại một ví xác định, liên kết trực tiếp với danh mục cấp 3 và hạch toán số dư ví kèm theo phụ phí hợp thành nếu có.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TXN-02`, `UC-TXN-03`, `UC-TXN-04`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`, `FR-TXN-04`, `FR-TXN-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-CAT-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `transactionId` | `UUID` | `protected final` | Mã định danh duy nhất của giao dịch, sinh tự động khi tạo mới và giữ cố định suốt vòng đời. |
| `amount` | `Money` | `protected` | Số tiền giao dịch, có đúng 2 chữ số thập phân và bắt buộc lớn hơn `0.00 VNĐ`. |
| `transactionDate` | `TransactionDate` | `protected` | Ngày phát sinh giao dịch, bọc đối tượng giá trị lịch pháp và không được vượt quá ngày hiện tại. |
| `fee` | `TransactionFee` | `protected` | Bản ghi chi phí giao dịch hợp thành (`0..1`), gắn liền sinh tử với giao dịch gốc (nhận `null` nếu không có phí). |
| `note` | `Note` | `protected` | Đối tượng giá trị bọc chuỗi ghi chú diễn giải tùy chọn, dài tối đa 255 ký tự sau khi cắt tỉa khoảng trắng hai đầu, áp dụng đối tượng rỗng an toàn (`Note.EMPTY`). |
| `createdAt` | `LocalDateTime` | `protected final` | Mốc thời gian tạo bản ghi bất biến, phục vụ sắp xếp lịch sử giao dịch thứ cấp khi trùng ngày. |
| `transactionType` | `TransactionType` | `private final` | Chiều biến động dòng tiền (`INCOME` hoặc `EXPENSE`) tại `IncomeExpenseTransaction`, mang tính bất biến. |
| `wallet` | `Wallet` | `private` | Tham chiếu thực thể `Wallet` đang hoạt động (`ACTIVE`) tiếp nhận hoặc trích xuất dòng tiền. |
| `category` | `Category` | `private` | Tham chiếu thực thể `Category` cấp 3 đang `ACTIVE`, bắt buộc có loại thu chi khớp với `transactionType`. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `Transaction` *(Constructor)* | `UUID transactionId, Money amount, TransactionDate transactionDate, String note, TransactionFee fee` | Không có | `BusinessException` | Hàm khởi tạo của lớp cha trừu tượng; kiểm tra `transactionId` và `transactionDate` khác `null`, `amount` lớn hơn `0.00 VNĐ` (`ERR_FIN_001`), đóng gói `note` vào đối tượng giá trị `Note` (tối đa 255 ký tự) và gán `createdAt` thời điểm hiện tại. |
| `IncomeExpenseTransaction` *(Constructor)* | `UUID transactionId, TransactionType transactionType, Wallet wallet, Category category, Money amount, TransactionDate date, String note, TransactionFee fee` | Không có | `BusinessException` | Gọi `super(...)`; kiểm tra `wallet.isActive()`, `category.isActive()` khớp `transactionType` (`ERR_CAT_001`), và nếu có `fee` thì khớp `transactionId` cùng `wallet`. |
| `applyToWallet` | Không có | `void` | `BusinessException` | Phương thức trừu tượng đa hình: Lớp con kiểm tra `wallet.isActive()` và tính toán số dư ròng; thực hiện `wallet.credit(amount)` nếu là `INCOME` hoặc `wallet.debit(amount)` nếu là `EXPENSE`, sau đó gọi `fee.applyFee()` nếu có phí. |
| `revertFromWallet` | Không có | `void` | `BusinessException` | Phương thức trừu tượng đa hình: Lớp con kiểm tra số dư đủ điều kiện hoàn nguyên; gọi `fee.revertFee()` nếu có phí, sau đó gọi `wallet.debit(amount)` nếu là `INCOME` hoặc `wallet.credit(amount)` nếu là `EXPENSE`. |
| `updateDetails` | `Wallet newWallet, Category newCategory, Money newAmount, TransactionDate newDate, String newNote, TransactionFee newFee` | `void` | `BusinessException` | Kiểm tra tham số hợp lệ; hoàn nguyên giao dịch cũ thông qua `revertFromWallet()`, cập nhật thông tin mới và thực thi hạch toán lại qua `applyToWallet()`. |
| `getTransactionId` | Không có | `UUID` | Không có | Trả về mã định danh duy nhất của giao dịch. |
| `getAmount` | Không có | `Money` | Không có | Trả về đối tượng giá trị số tiền của giao dịch. |
| `getTransactionDate` | Không có | `TransactionDate` | Không có | Trả về mốc ngày ghi nhận giao dịch. |
| `getCreatedAt` | Không có | `LocalDateTime` | Không có | Trả về thời điểm tạo bản ghi giao dịch. |
| `getFee` | Không có | `Optional<TransactionFee>` | Không có | Trả về phụ phí giao dịch dạng `Optional`. |
| `getNote` | Không có | `String` | Không có | Trả về chuỗi ghi chú của giao dịch. |
| `getNoteValue` | Không có | `Note` | Không có | Trả về đối tượng giá trị ghi chú của giao dịch. |
| `getWallet` | Không có | `Wallet` | Không có | Trả về thực thể ví thanh toán chịu tác động. |
| `getCategory` | Không có | `Category` | Không có | Trả về thực thể danh mục phân loại chi tiêu/thu nhập. |
| `getTransactionType` | Không có | `TransactionType` | Không có | Trả về loại thu chi (`INCOME` hoặc `EXPENSE`) của giao dịch. |

**Cơ chế đóng gói và quy tắc bất biến**

- Khung kế thừa trừu tượng và tính bất biến: Lớp `Transaction` bảo vệ tính toàn vẹn định danh bằng cách khóa cố định `transactionId` và `createdAt` bằng từ khóa `final`. Toàn bộ các thuộc tính dùng chung được phân quyền truy cập `protected` để các lớp con (`IncomeExpenseTransaction`, `TransferTransaction`) kế thừa trọn vẹn mà không phá vỡ tính đóng gói đối với bên ngoài.
- Đa hình hóa hạch toán dòng tiền (Polymorphism): Cặp phương thức trừu tượng `applyToWallet()` và `revertFromWallet()` chuyển giao toàn bộ logic tác động số dư cho các lớp con cụ thể, loại bỏ hoàn toàn các cấu trúc điều kiện rẽ nhánh thủ công (`if-else` trên loại giao dịch) theo nguyên lý Đóng/Mở (OCP).
- Kiểm soát tương thích danh mục và phí liên kết: Tại `IncomeExpenseTransaction`, thuộc tính `category` bắt buộc phải đang hoạt động (`ACTIVE`) và có loại thu chi tương thích tuyệt đối với `transactionType`, ngăn chặn hoàn toàn việc phân loại nhầm thu nhập vào danh mục chi tiêu theo `BR-FIN-04`. Nếu giao dịch có kèm theo `fee`, bản ghi phí bắt buộc phải có `parentTransactionId` trùng khớp với `transactionId` của giao dịch chủ.

### 2.2.3 Lớp giao dịch chuyển tiền

Lớp `TransferTransaction` kế thừa từ lớp trừu tượng `Transaction` nhằm điều chuyển dòng tiền nội bộ giữa hai ví độc lập trong hệ thống. Mọi giao dịch chuyển tiền đều thực thi cơ chế hạch toán kép: trích nợ ví nguồn và ghi có ví đích đồng thời trong cùng một thao tác nghiệp vụ, bảo toàn tổng tài sản của người dùng trước khi tính chi phí chuyển tiền phát sinh nếu có.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TRF-01`, `UC-TRF-02`, `UC-TRF-03`
- Yêu cầu chức năng: `FR-TRF-01`, `FR-TRF-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`
- Mã lỗi: `ERR-FIN-001`, `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `transactionId` | `UUID` | `protected final` | Mã định danh duy nhất của giao dịch, kế thừa từ `Transaction`. |
| `sourceWallet` | `Wallet` | `private final` | Tham chiếu thực thể `Wallet` nguồn trích tiền, bắt buộc đang hoạt động (`ACTIVE`) và có mã định danh khác với `destinationWallet`. |
| `destinationWallet` | `Wallet` | `private final` | Tham chiếu thực thể `Wallet` đích nhận tiền, bắt buộc đang hoạt động (`ACTIVE`) và có mã định danh khác với `sourceWallet`. |
| `amount` | `Money` | `protected` | Số tiền điều chuyển (> 0.00 VNĐ), kế thừa từ `Transaction`. |
| `transactionDate` | `TransactionDate` | `protected` | Ngày phát sinh giao dịch (không vượt quá ngày hiện tại), kế thừa từ `Transaction`. |
| `fee` | `TransactionFee` | `protected` | Phụ phí giao dịch hợp thành (`0..1`), kế thừa từ `Transaction`. |
| `note` | `Note` | `protected` | Ghi chú diễn giải tùy chọn (tối đa 255 ký tự), kế thừa từ `Transaction`. |
| `createdAt` | `LocalDateTime` | `protected final` | Thời điểm tạo bản ghi giao dịch, kế thừa từ `Transaction`. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `TransferTransaction` *(Constructor)* | `UUID transactionId, Wallet sourceWallet, Wallet destinationWallet, Money amount, TransactionDate transactionDate, String note, TransactionFee fee` | Không có | `BusinessException` | Gọi `super(...)` đóng gói `note` vào `Note`; kiểm tra hai ví khác `null`, cùng thỏa mãn `isActive()`, có mã định danh khác nhau (`ERR_TRF_001`), số tiền dương (`ERR_TRF_002`), và nếu có `fee` thì khớp `transactionId` cùng ví trích phí trùng `sourceWallet` (`ERR_FEE_001`). |
| `applyToWallet` | Không có | `void` | `BusinessException` | Phương thức trừu tượng đa hình: Kiểm tra hai ví đang `ACTIVE`, kiểm tra `sourceWallet` đủ trả tổng số tiền `amount` cùng phụ phí `fee`, thực hiện `sourceWallet.debit(amount)`, gọi `fee.applyFee()` nếu có và gọi `destinationWallet.credit(amount)`. |
| `revertFromWallet` | Không có | `void` | `BusinessException` | Phương thức trừu tượng đa hình khi xóa giao dịch: Kiểm tra hai ví đang `ACTIVE`, kiểm tra `destinationWallet` có số dư tối thiểu bằng `amount`, thực hiện `destinationWallet.debit(amount)`, gọi `fee.revertFee()` nếu có và gọi `sourceWallet.credit(amount)`. |
| `getTotalSourceDeduction` | Không có | `Money` | Không có | Tính tổng số tiền bị trừ khỏi `sourceWallet` bao gồm tiền chuyển `amount` cộng số tiền của phụ phí `fee` nếu có. |
| `getSourceWallet` | Không có | `Wallet` | Không có | Trả về thực thể ví nguồn. |
| `getDestinationWallet` | Không có | `Wallet` | Không có | Trả về thực thể ví đích. |

**Cơ chế đóng gói và quy tắc bất biến**

- Khóa định tuyến ví và bảo toàn tổng tài sản: Lớp `TransferTransaction` khóa cố định `sourceWallet` và `destinationWallet` bằng từ khóa `final`, không cung cấp phương thức chỉnh sửa lộ trình chuyển tiền sau khi tạo nhằm bảo toàn tính toàn vẹn của sổ nhật ký điều chuyển tài sản. Hai ví bắt buộc phải có mã định danh khác nhau (`ERR_TRF_001`) và đều ở trạng thái hoạt động (`ACTIVE`) tại thời điểm tạo và hạch toán.
- Đảm bảo tính nguyên tử trong hạch toán dòng tiền: Phương thức `applyToWallet()` kiểm tra số dư khả dụng tổng thể của ví nguồn (`amount + fee.amount`) trước khi biến đổi trạng thái, sau đó trích nợ ví nguồn và ghi có ví đích trong cùng một khối lệnh, loại trừ nguy cơ ví nguồn bị trừ tiền nhưng ví đích chưa nhận được hoặc thiếu tiền thanh toán phụ phí theo `BR-FIN-02`.
- Kiểm soát điều kiện biên khi hoàn nguyên giao dịch: Giao dịch chuyển tiền không hỗ trợ sửa đổi trực tiếp; khi người dùng thực hiện xóa giao dịch, phương thức `revertFromWallet()` chỉ cho phép hoàn nguyên khi số dư hiện tại của `destinationWallet` tối thiểu bằng khoản tiền đã nhận (`amount`), bảo đảm ví đích không bị rơi vào trạng thái số dư âm theo quy tắc bảo toàn số dư không âm `BR-FIN-02`.

### 2.2.4 Lớp chi phí giao dịch

Lớp `TransactionFee` đại diện cho khoản phụ phí phát sinh phụ thuộc sinh tử kèm theo giao dịch thu chi hoặc chuyển tiền nội bộ. Khoản phí này mang bản chất chi tiêu và gắn kết hợp thành với giao dịch gốc nhằm bảo đảm tính toàn vẹn dữ liệu và loại trừ nguy cơ phát sinh bản ghi chi phí mồ côi.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TXN-03`, `UC-TXN-04`, `UC-TRF-01`, `UC-TRF-03`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-04`, `FR-TXN-05`, `FR-TRF-01`, `FR-TRF-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi: `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-WAL-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `feeId` | `UUID` | `private final` | Mã định danh duy nhất của bản ghi chi phí, sinh tự động khi khởi tạo và giữ cố định suốt vòng đời. |
| `parentTransactionId` | `UUID` | `private final` | Mã định danh giao dịch gốc (`Transaction`), dùng để ràng buộc vòng đời hợp thành và ngăn bản ghi phí mồ côi. |
| `amount` | `Money` | `private` | Số tiền chi phí có đúng 2 chữ số thập phân và bắt buộc lớn hơn `0.00 VNĐ`. |
| `wallet` | `Wallet` | `private` | Tham chiếu thực thể `Wallet` đang hoạt động (`ACTIVE`) dùng để trích chi phí. |
| `category` | `Category` | `private` | Tham chiếu thực thể `Category` cấp 3 đang hoạt động (`ACTIVE`) thuộc nhánh chi tiêu (`EXPENSE`). |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `TransactionFee` *(Constructor)* | `UUID feeId, UUID parentTransactionId, Money amount, Wallet wallet, Category category` | Không có | `BusinessException` | Kiểm tra các tham số khác `null`; kiểm tra `amount` lớn hơn `0.00 VNĐ` (`ERR_FEE_001`); kiểm tra `category` khác `null` (`ERR_FEE_002`); kiểm tra `wallet.isActive()` và `category.isActive()` thuộc loại `EXPENSE` (`ERR_FEE_003`). |
| `applyFee` | Không có | `void` | `BusinessException` | Gọi `wallet.debit(amount)` để trích tiền phí khỏi ví thanh toán; ném `ERR_WAL_001` nếu số dư ví không đủ chi trả. |
| `revertFee` | Không có | `void` | `BusinessException` | Gọi `wallet.credit(amount)` để hoàn lại tiền phí vào ví khi giao dịch gốc bị hủy hoặc hoàn nguyên. |
| `updateFee` | `Money newAmount, Category newCategory, Wallet newWallet` | `void` | `BusinessException` | Kiểm tra tham số hợp lệ, ví và danh mục mới đang `ACTIVE` thuộc nhánh `EXPENSE` trước khi cập nhật lại số tiền, danh mục hoặc ví trích phí. |
| `getFeeId` | Không có | `UUID` | Không có | Trả về mã định danh duy nhất của phụ phí. |
| `getParentTransactionId` | Không có | `UUID` | Không có | Trả về mã định danh giao dịch chủ quản. |
| `getAmount` | Không có | `Money` | Không có | Trả về số tiền phụ phí. |
| `getWallet` | Không có | `Wallet` | Không có | Trả về thực thể ví trích phụ phí. |
| `getCategory` | Không có | `Category` | Không có | Trả về danh mục phân loại chi phí (`EXPENSE`). |

**Cơ chế đóng gói và quy tắc bất biến**

- Quan hệ hợp thành phụ thuộc vòng đời: Trường `parentTransactionId` được khóa cố định bằng từ khóa `final`. Thực thể `TransactionFee` chỉ được khởi tạo thông qua `TransactionFactory` khi giao dịch phát sinh chi phí lớn hơn `0.00 VNĐ`, bảo đảm bản ghi phí luôn đồng hành và tự động giải phóng theo vòng đời của giao dịch gốc.
- Ràng buộc phân loại và số dư tài chính: Mức phí bắt buộc phải mang giá trị dương (`amount > 0.00 VNĐ`) và gắn cố định với một `Category` chi tiêu đang hoạt động (`ACTIVE`). Giao dịch chủ quản chịu trách nhiệm kiểm tra số dư khả dụng tổng thể của ví trước khi thực thi nhằm bảo đảm bất biến số dư không âm theo `BR-FIN-02`.
- Cơ chế hạch toán dòng tiền: Phương thức `applyFee()` và `revertFee()` thực thi trích xuất và hoàn nguyên trực tiếp trên đối tượng `Wallet` thông qua các hành vi `debit()` và `credit()`, bảo đảm dữ liệu số dư ví luôn đồng bộ với sổ nhật ký giao dịch.

### 2.2.5 Lớp danh mục chi tiết

Lớp `Category` đại diện cho đơn vị phân loại chi tiết ở cấp 3 trong cấu trúc cây danh mục của hệ thống, đóng vai trò gán nhãn mục đích tài chính cho các giao dịch thu chi và chi phí giao dịch. Lớp này chịu trách nhiệm duy trì tính toàn vẹn tham chiếu phân cấp với nhóm nhu cầu cha và thực thi cơ chế xóa mềm nhằm bảo toàn lịch sử sổ sách tài chính.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-04`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-003`, `ERR-CAT-004`, `ERR-CAT-005`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `categoryId` | `UUID` | `private final` | Mã định danh duy nhất của danh mục, sinh tự động khi khởi tạo và bất biến. |
| `name` | `EntityName` | `private` | Bọc tên danh mục từ 1–50 ký tự (`BR-NAM-01`), duy nhất trong cùng nhóm cha đang hoạt động. |
| `needGroupId` | `UUID` | `private` | Mã định danh nhóm cha cấp 2 (`NeedGroup` đang `ACTIVE`), cấm danh mục mồ côi. |
| `transactionType` | `TransactionType` | `private final` | Loại thu chi (`INCOME`/`EXPENSE`) kế thừa từ nhóm cha, bất biến sau khởi tạo. |
| `status` | `EntityStatus` | `private` | Trạng thái vòng đời (`ACTIVE` hoặc `DELETED`), mặc định là `ACTIVE` khi tạo mới. |
| `icon` | `String` | `private` | Biểu tượng đồ họa đại diện (tùy chọn, tối đa 50 ký tự). |
| `color` | `String` | `private` | Mã màu Hex hiển thị (ví dụ `#4CAF50`, tối đa 10 ký tự). |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `Category` *(Constructor)* | `UUID categoryId, String name, UUID needGroupId, TransactionType transactionType, String icon, String color` | Không có | `BusinessException` | Khởi tạo thực thể danh mục mới ở trạng thái `ACTIVE`; chuẩn hóa và đóng gói `name` vào `EntityName` theo quy tắc đặt tên `BR-NAM-01`, bắt buộc có mã nhóm cha và loại thu chi kế thừa theo cấu trúc danh mục ba cấp. |
| `getCategoryId` | Không có | `UUID` | Không có | Trả về mã định danh duy nhất của danh mục chi tiết. |
| `getName` | Không có | `String` | Không có | Trả về chuỗi tên hiển thị hiện tại của danh mục từ đối tượng giá trị `EntityName`. |
| `getEntityName` | Không có | `EntityName` | Không có | Trả về đối tượng giá trị tên danh mục phục vụ thao tác định danh và so sánh khóa chuẩn hóa. |
| `getNeedGroupId` | Không có | `UUID` | Không có | Trả về mã định danh nhóm nhu cầu cha cấp 2 mà danh mục trực thuộc. |
| `getTransactionType` | Không có | `TransactionType` | Không có | Trả về loại thu chi (`INCOME` hoặc `EXPENSE`) của danh mục. |
| `getStatus` | Không có | `EntityStatus` | Không có | Trả về trạng thái hoạt động vòng đời hiện tại của danh mục. |
| `getIcon` | Không có | `String` | Không có | Trả về mã biểu tượng đồ họa đại diện của danh mục. |
| `getColor` | Không có | `String` | Không có | Trả về chuỗi mã màu hiển thị trực quan của danh mục. |
| `isActive` | Không có | `boolean` | Không có | Kiểm tra danh mục có đang ở trạng thái hoạt động (`ACTIVE`) hay không. |
| `isDeleted` | Không có | `boolean` | Không có | Kiểm tra danh mục có đang ở trạng thái đã xóa mềm (`DELETED`) hay không. |
| `rename` | `String newName` | `void` | `BusinessException` | Cập nhật tên danh mục qua `EntityName.of(newName, ...)` (1-50 ký tự theo `BR-NAM-01`), ném ngoại lệ nếu tên để trống; giữ nguyên mã định danh và toàn bộ liên kết lịch sử theo quy tắc đặt tên và bảo toàn dữ liệu lịch sử. |
| `changeNeedGroup` | `UUID newNeedGroupId, TransactionType targetGroupType` | `void` | `BusinessException` | Chuyển danh mục sang nhóm nhu cầu cha mới; ném ngoại lệ nếu nhóm đích khác loại thu chi hoặc nhóm đích đã bị xóa theo quy tắc cấu trúc danh mục ba cấp. |
| `updateAppearance` | `String newIcon, String newColor` | `void` | Không có | Cập nhật biểu tượng nhận diện và mã màu hiển thị của danh mục theo tùy biến người dùng. |
| `markDeleted` | Không có | `void` | Không có | Chuyển trạng thái sang `DELETED` (xóa mềm), ẩn danh mục khỏi danh sách lựa chọn cho giao dịch mới nhưng bảo toàn liên kết với các giao dịch lịch sử và báo cáo theo quy tắc xóa mềm bảo toàn lịch sử và cấu trúc danh mục ba cấp. |
| `restore` | `boolean isParentGroupActive` | `void` | `BusinessException` | Khôi phục danh mục về trạng thái `ACTIVE`; ném ngoại lệ từ chối nếu nhóm nhu cầu cha không ở trạng thái hoạt động (`isParentGroupActive == false`) theo quy tắc cấu trúc danh mục ba cấp và kiểm soát toàn vẹn khi khôi phục. |

**Cơ chế đóng gói và quy tắc bất biến**

- Ràng buộc liên kết phân cấp cha con: Danh mục chi tiết cấp 3 bắt buộc phải luôn trực thuộc một nhóm nhu cầu cha cấp 2 (`NeedGroup`) hợp lệ đang hoạt động; hệ thống nghiêm cấm tồn tại danh mục mồ côi hoặc tạo danh mục trong nhóm đã xóa theo quy tắc cấu trúc danh mục ba cấp. Thuộc tính `transactionType` được kế thừa trực tiếp từ nhóm cha và mang tính bất biến tuyệt đối; khi chuyển nhóm cha (`changeNeedGroup`), nhóm đích bắt buộc phải đang hoạt động và có cùng loại thu chi, từ chối mọi hành vi chuyển nhóm chéo loại hoặc chuyển vào nhóm đã xóa.
- Quy tắc kiểm soát định danh và tính duy nhất: Thuộc tính `name` được bọc trong đối tượng giá trị `EntityName` và bảo vệ đóng gói chặt chẽ qua phương thức `rename()`, bắt buộc chuẩn hóa cắt tỉa khoảng trắng ở hai đầu (`trim`) với độ dài từ 1 đến 50 ký tự theo `BR-NAM-01`, không được để trống hoặc vượt quá độ dài quy định. Tên danh mục là duy nhất trong phạm vi cùng nhóm nhu cầu cha đang hoạt động, so sánh không phân biệt chữ hoa chữ thường qua `normalizedKey` theo quy tắc đặt tên và kiểm tra trùng lặp. Hai danh mục thuộc hai nhóm cha khác nhau được phép trùng tên.
- Cơ chế xóa mềm bảo toàn lịch sử và điều kiện khôi phục: Khi thực hiện xóa danh mục đã phát sinh giao dịch lịch sử, thực thể chuyển sang trạng thái xóa mềm (`DELETED`) thông qua `markDeleted()`. Danh mục bị ẩn khỏi danh sách lựa chọn khi tạo giao dịch mới, nhưng liên kết tham chiếu trong sổ giao dịch và báo cáo tài chính được giữ nguyên vẹn kèm nhãn "(đã xóa)" theo quy tắc xóa mềm bảo toàn dữ liệu lịch sử. Thao tác khôi phục (`restore()`) chỉ được kích hoạt khi nhóm cha trực thuộc đang ở trạng thái `ACTIVE` (không bị xóa) và tên danh mục không bị trùng lặp với một danh mục đang dùng khác trong cùng nhóm cha.

### 2.2.6 Lớp nhóm nhu cầu

Lớp `NeedGroup` đại diện cho đơn vị phân loại trung gian ở cấp 2 trong cấu trúc phân cấp danh mục của hệ thống, quản lý việc gom cụm các danh mục chi tiết cấp 3 có cùng tính chất tài chính. Lớp này đóng vai trò điều phối tập hợp danh mục con, làm căn cứ tổng hợp cơ cấu thu chi trên báo cáo và bảo toàn ranh giới phân loại theo nhánh thu chi gốc.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-04`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-005`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `needGroupId` | `UUID` | `private final` | Mã định danh duy nhất của nhóm nhu cầu, sinh tự động khi khởi tạo và bất biến. |
| `name` | `String` | `private` | Tên nhóm nhu cầu từ 1–50 ký tự, duy nhất trong cùng nhánh `TransactionType` đang hoạt động. |
| `transactionType` | `TransactionType` | `private final` | Nhánh loại thu chi (`INCOME`/`EXPENSE`), bất biến tuyệt đối sau khởi tạo. |
| `status` | `GroupStatus` | `private` | Trạng thái vòng đời (`ACTIVE` hoặc `DELETED`), mặc định là `ACTIVE` khi tạo mới. |
| `icon` | `String` | `private` | Biểu tượng đại diện (tùy chọn, tối đa 50 ký tự). |
| `color` | `String` | `private` | Mã màu Hex trên biểu đồ cơ cấu (tối đa 10 ký tự). |
| `categories` | `List<Category>` | `private final` | Tập hợp danh mục chi tiết trực thuộc (kết tập), bảo vệ đóng gói không mở setter. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `NeedGroup` *(Constructor)* | `UUID needGroupId, String name, TransactionType transactionType, String icon, String color` | Không có | `BusinessException` | Khởi tạo nhóm nhu cầu mới ở trạng thái `ACTIVE` với danh sách danh mục con rỗng; kiểm tra định dạng tên theo quy tắc đặt tên và tính hợp lệ theo cấu trúc danh mục ba cấp. |
| `getNeedGroupId` | Không có | `UUID` | Không có | Trả về mã định danh duy nhất của nhóm nhu cầu. |
| `getName` | Không có | `String` | Không có | Trả về tên hiển thị hiện tại của nhóm nhu cầu. |
| `getTransactionType` | Không có | `TransactionType` | Không có | Trả về loại thu chi cố định (`INCOME` hoặc `EXPENSE`) của nhóm. |
| `getStatus` | Không có | `GroupStatus` | Không có | Trả về trạng thái hoạt động vòng đời của nhóm nhu cầu. |
| `getIcon` | Không có | `String` | Không có | Trả về mã biểu tượng đồ họa đại diện của nhóm nhu cầu. |
| `getColor` | Không có | `String` | Không có | Trả về chuỗi mã màu hiển thị của nhóm nhu cầu. |
| `getCategories` | Không có | `List<Category>` | Không có | Trả về danh sách danh mục con dưới dạng chỉ đọc (`Collections.unmodifiableList`) nhằm bảo vệ tính toàn vẹn tập hợp. |
| `isActive` | Không có | `boolean` | Không có | Kiểm tra nhóm nhu cầu có đang ở trạng thái hoạt động (`ACTIVE`) hay không. |
| `isDeleted` | Không có | `boolean` | Không có | Kiểm tra nhóm nhu cầu có đang ở trạng thái đã xóa mềm (`DELETED`) hay không. |
| `rename` | `String newName` | `void` | `BusinessException` | Cập nhật tên nhóm mới sau khi cắt tỉa khoảng trắng (1-50 ký tự), ném ngoại lệ nếu tên để trống; giữ nguyên mã và loại thu chi theo quy tắc đặt tên và bảo toàn dữ liệu. |
| `updateAppearance` | `String newIcon, String newColor` | `void` | Không có | Cập nhật biểu tượng nhận diện và mã màu hiển thị của nhóm nhu cầu phục vụ trực quan hóa báo cáo. |
| `addCategory` | `Category category` | `void` | `BusinessException` | Tiếp nhận thêm danh mục con vào nhóm; ném ngoại lệ nếu nhóm đang bị xóa hoặc danh mục không cùng loại thu chi theo quy tắc cấu trúc danh mục ba cấp. |
| `removeCategory` | `Category category` | `void` | Không có | Loại bỏ một danh mục con khỏi danh sách trực thuộc của nhóm khi danh mục bị xóa cứng hoặc chuyển nhóm theo quy tắc cấu trúc danh mục ba cấp. |
| `markDeleted` | `int activeGroupCountInType` | `void` | `BusinessException` | Chuyển trạng thái sang `DELETED` và kích hoạt xóa lan truyền cho các danh mục con; ném ngoại lệ nếu là nhóm hoạt động duy nhất còn lại của loại thu chi (`activeGroupCountInType <= 1`) theo quy tắc duy trì tối thiểu một nhóm hoạt động. |
| `restore` | Không có | `void` | Không có | Khôi phục trạng thái nhóm từ `DELETED` về `ACTIVE` một cách độc lập; không tự động khôi phục các danh mục con bên trong theo quy tắc phân cấp danh mục và ca sử dụng khôi phục độc lập. |

**Cơ chế đóng gói và quy tắc bất biến**

- Tính bất biến của loại thu chi: Thuộc tính `transactionType` được khai báo `final`, thiết lập duy nhất một lần tại hàm khởi tạo và không có phương thức sửa đổi. Quy tắc này ngăn chặn triệt để hành vi thay đổi loại thu chi của nhóm giữa `INCOME` và `EXPENSE`, bảo đảm tính nhất quán cấu trúc cây phân cấp và không làm sai lệch số liệu thống kê lịch sử theo quy tắc cấu trúc danh mục ba cấp, ngăn chặn đổi loại thu chi gây sai lệch dữ liệu.
- Bảo vệ nhóm nhu cầu duy nhất còn lại: Mỗi nhánh Loại thu chi (`INCOME`, `EXPENSE`) bắt buộc phải duy trì tối thiểu một nhóm nhu cầu ở trạng thái hoạt động (`ACTIVE`). Phương thức `markDeleted()` kiểm tra số lượng nhóm đang hoạt động trong cùng loại; nếu là nhóm hoạt động cuối cùng, hệ thống từ chối xóa và ném ngoại lệ theo quy tắc duy trì tối thiểu một nhóm hoạt động cho mỗi nhánh thu chi.
- Quản lý danh mục con và hiệu ứng xóa lan truyền: Tập hợp `categories` được đóng gói an toàn, chỉ truy xuất qua bản sao hoặc danh sách không thể sửa đổi (`unmodifiableList`). Khi xóa nhóm nhu cầu (`markDeleted`), hệ thống áp dụng cơ chế xóa lan truyền (`cascade`): toàn bộ danh mục con đã có giao dịch được chuyển sang xóa mềm (`DELETED`), còn các danh mục con chưa phát sinh giao dịch được loại bỏ vĩnh viễn khỏi hệ thống theo quy tắc phân cấp danh mục. Khi nhóm được khôi phục (`restore()`), nhóm chuyển về `ACTIVE` nhưng các danh mục con vẫn giữ trạng thái đã xóa để người dùng khôi phục riêng lẻ từng mục theo nhu cầu theo quy tắc cấu trúc danh mục và quy trình khôi phục độc lập.

### 2.2.7 Lớp bản ghi điều chỉnh số dư

Lớp `BalanceAdjustmentRecord` lưu lại sự kiện kiểm kê đối soát nhằm đưa số dư khả dụng của ví về khớp với số tiền thực tế quan sát được. Bản ghi giữ trạng thái bất biến tuyệt đối sau khi tạo để bảo toàn vết kiểm toán và tách biệt khoản chênh lệch khỏi dòng tiền thu chi định kỳ.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `recordId` | `UUID` | `private final` | Mã định danh duy nhất của bản ghi kiểm kê, sinh tự động khi tạo mới và giữ cố định. |
| `walletId` | `UUID` | `private final` | Mã định danh tham chiếu cố định tới thực thể `Wallet` được kiểm kê, bắt buộc khác `null`. |
| `previousBalance` | `Money` | `private final` | Số dư khả dụng của ví ngay trước thời điểm kiểm kê, giữ cố định. |
| `actualBalance` | `Money` | `private final` | Số dư thực tế quan sát được, có đúng 2 chữ số thập phân và bắt buộc không âm (`>= 0.00 VNĐ`). |
| `difference` | `Money` | `private final` | Giá trị chênh lệch đại số tính bằng `actualBalance.subtract(previousBalance)`, bắt buộc khác `0.00 VNĐ`. |
| `adjustedAt` | `TransactionDate` | `private final` | Mốc ngày ghi nhận kiểm kê bất biến, không vượt quá ngày hiện tại từ đồng hồ hệ thống. |
| `reason` | `String` | `private final` | Chuỗi diễn giải lý do điều chỉnh số dư, tùy chọn, dài tối đa 255 ký tự sau khi cắt khoảng trắng hai đầu. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `BalanceAdjustmentRecord` *(Constructor)* | `UUID recordId, UUID walletId, Money previousBalance, Money actualBalance, TransactionDate adjustedAt, String reason` | Không có | `BusinessException`, `IllegalArgumentException` | Kiểm tra các trường bắt buộc khác `null`; `actualBalance` không âm (`ERR_WAL_001`); `difference` khác `0.00 VNĐ` (`ERR_FIN_001`); chuẩn hóa `reason` tối đa 255 ký tự; tính tự động `difference = actualBalance.subtract(previousBalance)`. |
| `getRecordId` | Không có | `UUID` | Không có | Trả về mã định danh duy nhất của bản ghi kiểm kê phục vụ lưu trữ và tra cứu vết kiểm toán. |
| `getWalletId` | Không có | `UUID` | Không có | Trả về mã định danh của ví được kiểm kê. |
| `getPreviousBalance` | Không có | `Money` | Không có | Trả về số dư ví trước thời điểm kiểm kê. |
| `getActualBalance` | Không có | `Money` | Không có | Trả về số dư thực tế sau kiểm kê. |
| `getDifference` | Không có | `Money` | Không có | Trả về giá trị chênh lệch đại số có dấu. |
| `getAbsoluteDifference` | Không có | `Money` | Không có | Trả về trị tuyệt đối của chênh lệch (`difference.abs()`). |
| `getAdjustedAt` | Không có | `TransactionDate` | Không có | Trả về mốc ngày ghi nhận kiểm kê. |
| `getReason` | Không có | `String` | Không có | Trả về lý do điều chỉnh số dư. |
| `isSurplus` | Không có | `boolean` | Không có | Trả về `true` khi chênh lệch dương (tăng số dư ví). |

**Cơ chế đóng gói và quy tắc bất biến**

- Tính bất biến tuyệt đối bảo vệ vết kiểm toán: Toàn bộ 7 thuộc tính đều khai báo `private final`, lớp không cung cấp bất kỳ phương thức setter nào, bảo đảm bản ghi sau khi khởi tạo sẽ không bị biến dạng theo quy tắc dấu vết kiểm tra lại của `UC-WAL-04`.
- Tự động tính chênh lệch và kiểm tra điều kiện biên: Hàm khởi tạo tự động tính toán `difference = actualBalance.subtract(previousBalance)`. Bắt buộc `actualBalance >= 0.00 VNĐ` (không cho phép ví âm tiền theo `BR-FIN-02`) và `difference != 0.00 VNĐ` (từ chối tạo bản ghi kiểm kê rỗng khi số dư thực tế trùng số dư sổ sách).
- Phân lập hoàn toàn khỏi giao dịch thu chi: Biến động chênh lệch kiểm kê được ghi nhận riêng biệt, không đi qua `IncomeExpenseTransaction`, giữ cho báo cáo phân tích thu chi định kỳ theo danh mục không bị sai lệch cấu trúc dòng tiền.

## 2.3 Thiết kế các đối tượng giá trị

Mục này đặc tả chi tiết các đối tượng giá trị và kiểu liệt kê thuộc gói `com.pfms.domain.model`. Đối tượng giá trị không có định danh độc lập, được định danh dựa trên giá trị của các thuộc tính thành phần và duy trì tính bất biến tuyệt đối xuyên suốt vòng đời.

Mỗi đối tượng giá trị được quy chuẩn cấu trúc đồng nhất qua ba nội dung:
1. **Cấu trúc thuộc tính**: Các trường dữ liệu nội bộ đóng gói cố định (`private final`), kiểu dữ liệu Java và ràng buộc hợp lệ.
2. **Phương thức nghiệp vụ**: Phương thức tính toán số học, so sánh logic, định dạng kết xuất và cơ chế luôn trả về thể hiện mới.
3. **Tính bất biến và cơ chế so sánh**: Logic tự kiểm tra hợp lệ khi khởi tạo, loại bỏ hàm sửa đổi giá trị và ghi đè `equals`/`hashCode`/`compareTo`.

### 2.3.1 Tiền tệ (Money)

Lớp `Money` bọc kiểu `BigDecimal` để lưu giá trị tiền tệ bất biến với 2 chữ số thập phân và làm tròn nửa chẵn `RoundingMode.HALF_EVEN`, loại bỏ sai số dấu chấm động. Đối tượng thực hiện các phép toán số học và kiểm tra biên giá trị cho số dư ví, giao dịch thu chi, chuyển tiền cùng kiểm kê đối soát.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`, `UC-TXN-01`, `UC-TRF-01`, `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-TXN-01`, `FR-TRF-01`, `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `ZERO` | `Money` | `public static final` | Hằng số tiền tệ mang giá trị `0.00 VNĐ`, làm mốc số dư ban đầu của ví mới và mốc so sánh dấu đại số. |
| `amount` | `BigDecimal` | `private final` | Số thập phân chuẩn hóa cố định 2 chữ số thập phân bằng `RoundingMode.HALF_EVEN`, cấm khởi tạo từ `float` hoặc `double`. |
| `currency` | `String` | `private final` | Mã đơn vị tiền tệ bất biến, cố định là `"VNĐ"` trên toàn hệ thống để đồng nhất các phép tính tài chính. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `of` | `String rawAmount` | `Money` | `BusinessException` | Phân tích chuỗi thập phân hợp lệ, kiểm tra rỗng và định dạng theo `ERR_FIN_001`, chuẩn hóa 2 chữ số thập phân bằng `RoundingMode.HALF_EVEN` và gán đơn vị `"VNĐ"`. |
| `of` | `BigDecimal value` | `Money` | `NullPointerException` | Kiểm tra `value` khác `null`, chuẩn hóa `setScale(2, RoundingMode.HALF_EVEN)` và trả về thể hiện `Money` mới. |
| `add` | `Money other` | `Money` | `NullPointerException` | Kiểm tra `other` khác `null`, cộng `this.amount` với `other.amount` và trả về thể hiện `Money` mới, dùng khi ghi nhận khoản thu hoặc nhận chuyển tiền. |
| `subtract` | `Money other` | `Money` | `NullPointerException` | Kiểm tra `other` khác `null`, trừ `other.amount` khỏi `this.amount` và trả về thể hiện `Money` mới, dùng khi trừ số dư ví hoặc tính chênh lệch kiểm kê. |
| `abs` | `Không` | `Money` | `Không` | Trả về thể hiện `Money` mới mang giá trị tuyệt đối không âm, dùng để tách dòng tiền vào hoặc ra từ khoản chênh lệch kiểm kê. |
| `requireNonNegative` | `Không` | `Money` | `IllegalStateException` | Kiểm tra `amount` từ `0.00 VNĐ` trở lên để bảo đảm số dư ví và số dư kiểm kê không âm. |
| `isPositive` | `Không` | `boolean` | `Không` | Trả về `true` khi `amount` lớn hơn `0.00`, dùng để kiểm tra số tiền giao dịch thu chi và chuyển tiền phải dương. |
| `isNegative` | `Không` | `boolean` | `Không` | Trả về `true` khi `amount` nhỏ hơn `0.00`, dùng để phát hiện và chặn thao tác làm âm số dư ví. |
| `isZero` | `Không` | `boolean` | `Không` | Trả về `true` khi `amount` bằng `0.00`, dùng để kiểm tra chênh lệch kiểm kê bằng không hoặc mẫu số bằng không khi tính tỷ lệ. |
| `getAmount` | `Không` | `BigDecimal` | `Không` | Trả về giá trị `BigDecimal` chuẩn 2 chữ số thập phân. |
| `compareTo` | `Money other` | `int` | `NullPointerException` | So sánh thứ tự đại số giữa hai giá trị tiền tệ. |
| `formatDisplay` | `Không` | `String` | `Không` | Định dạng chuỗi số nguyên đồng có phân cách hàng nghìn kèm đơn vị `"VNĐ"`. |

**Tính bất biến và cơ chế so sánh**

- Lớp `Money` khai báo `public final`, khóa toàn bộ thuộc tính bằng `private final` và bỏ mọi phương thức sửa đổi trạng thái; các phép toán `add`, `subtract` và `abs` luôn trả về một thể hiện `Money` mới độc lập.
- Phương thức `equals` và `hashCode` so sánh trực tiếp trên cặp thuộc tính `amount` (đã chuẩn hóa `scale = 2` khi khởi tạo) và `currency`, bảo đảm hai đối tượng `Money` có cùng giá trị đại số thực luôn bằng nhau và sinh cùng mã băm.
- Lớp cài đặt giao diện `Comparable<Money>` và hiện thực hóa `compareTo` bằng phép so sánh đại số của `BigDecimal`, giúp các thực thể miền kiểm tra điều kiện đủ số dư ví và sắp xếp danh sách theo quy mô tiền tệ.

### 2.3.2 Ngày giao dịch (TransactionDate)

Lớp `TransactionDate` là đối tượng giá trị bất biến bọc kiểu `LocalDate` của Java, kiểm soát tính hợp lệ lịch pháp và bảo đảm mọi mốc thời gian giao dịch không vượt quá ngày hiện tại, đồng thời cung cấp các phép so sánh thứ tự thời gian và trích xuất chu kỳ báo cáo tháng phục vụ tổng hợp chuỗi 12 tháng.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TRF-01`, `UC-REP-01`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-03`, `FR-TRF-01`, `FR-REP-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi: `ERR-FIN-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `date` | `LocalDate` | `private final` | Ngày phát sinh giao dịch theo lịch Gregory quốc tế; bắt buộc không được sau ngày hiện tại từ đồng hồ hệ thống; từ chối mọi ngày không tồn tại trên thực tế (ví dụ: `31/02`) theo quy tắc độ chính xác tài chính; bất biến sau khi khởi tạo. |
| `isoString` | `String` | `private final` | Chuỗi biểu diễn ngày chuẩn ISO 8601 (`YYYY-MM-DD`) sinh tự động từ `date`; phục vụ hiển thị, lưu trữ JSON và phân định chu kỳ báo cáo tháng. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `TransactionDate` *(Constructor)* | `LocalDate date` | Không có | `BusinessException` | Khởi tạo đối tượng giá trị ngày giao dịch; kiểm tra `date` không được `null` và không được sau ngày hiện tại; ném ngoại lệ nếu vi phạm. |
| `of` *(Factory method)* | `int year, int month, int day` | `TransactionDate` | `BusinessException` | Phương thức tạo thể hiện từ bộ ba số nguyên; kiểm tra tính hợp lệ lịch pháp (tháng 1–12, ngày khớp số ngày thực tế của tháng bao gồm năm nhuận) và ràng buộc không vượt quá ngày hiện tại. |
| `getDate` | Không có | `LocalDate` | Không có | Trả về đối tượng `LocalDate` nội tại. |
| `getIsoString` | Không có | `String` | Không có | Trả về chuỗi ngày chuẩn `YYYY-MM-DD`. |
| `getYearMonth` | Không có | `String` | Không có | Trả về chuỗi chu kỳ tháng dạng `YYYY-MM`. |
| `isBefore` | `TransactionDate other` | `boolean` | Không có | Kiểm tra ngày này có trước ngày `other` hay không. |
| `isAfter` | `TransactionDate other` | `boolean` | Không có | Kiểm tra ngày này có sau ngày `other` hay không. |
| `isEqual` | `TransactionDate other` | `boolean` | Không có | Kiểm tra hai ngày có trùng nhau hay không. |
| `compareTo` | `TransactionDate other` | `int` | Không có | So sánh thứ tự thời gian chuẩn giữa hai ngày; trả về số âm, 0 hoặc dương; phục vụ sắp xếp danh sách giao dịch theo thứ tự ngày giảm dần. |
| `equals` | `Object obj` | `boolean` | Không có | Ghi đè so sánh bằng nhau dựa trên giá trị `date` nội tại theo ngữ nghĩa đối tượng giá trị. |
| `hashCode` | Không có | `int` | Không có | Ghi đè mã băm nhất quán với `equals` dựa trên giá trị `date` phục vụ lưu trữ trong tập hợp. |
| `toString` | Không có | `String` | Không có | Trả về chuỗi biểu diễn ngày chuẩn `YYYY-MM-DD` tương đương `isoString`. |

**Tính bất biến và cơ chế so sánh**

- Tính bất biến và kiểm tra ràng buộc thời gian: Thuộc tính `date` được khai báo `private final`; toàn bộ trạng thái được thiết lập duy nhất một lần trong hàm khởi tạo và không có phương thức sửa đổi. Mọi thay đổi ngày giao dịch tương đương với việc tạo một thể hiện `TransactionDate` mới độc lập. Hàm khởi tạo thực thi kiểm tra bắt buộc: (1) ngày phải tồn tại thực tế trên lịch Gregory; (2) ngày không được vượt quá ngày hiện tại do đồng hồ hệ thống cung cấp tại thời điểm khởi tạo; vi phạm bất kỳ điều kiện nào sẽ ném ngoại lệ `BusinessException`.
- Cơ chế so sánh bằng nhau: Phương thức `equals` được ghi đè để so sánh hai đối tượng `TransactionDate` dựa trên giá trị `date` nội tại; `hashCode` được ghi đè nhất quán với `equals` đảm bảo tính đúng đắn khi sử dụng trong `HashSet` hoặc `HashMap`.
- Cài đặt `Comparable` để so sánh thứ tự thời gian: Lớp cài đặt giao diện `Comparable<TransactionDate>`; phương thức `compareTo` ủy quyền cho `LocalDate.compareTo` để so sánh thứ tự thời gian chuẩn; phục vụ sắp xếp danh sách giao dịch theo ngày giảm dần và kiểm tra điều kiện khoảng ngày lọc trong báo cáo tài chính.

### 2.3.3 Kiểu liệt kê loại giao dịch (TransactionType)

Kiểu liệt kê `TransactionType` xác lập tập giá trị đóng gồm đúng hai hằng số `INCOME` và `EXPENSE`, đại diện cho hai chiều thu chi cố định; đây đồng thời là ranh giới phân cấp danh mục cấp 1 bất biến vĩnh viễn, định hướng kiểm tra tính tương thích giữa giao dịch và danh mục trong toàn bộ hệ thống.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-CAT-01`, `UC-CAT-03`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`, `FR-CAT-01`
- Quy tắc nghiệp vụ: `BR-FIN-04`
- Mã lỗi: `ERR-CAT-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `INCOME` | `TransactionType` | `public static final` | Hằng số biểu diễn chiều dòng tiền vào (Thu nhập); khi gắn với giao dịch, hệ thống cộng số tiền vào số dư ví tiếp nhận; khi gắn với nhóm nhu cầu hoặc danh mục, xác lập nhánh phân loại Thu nhập trong cấu trúc cây danh mục 3 cấp. |
| `EXPENSE` | `TransactionType` | `public static final` | Hằng số biểu diễn chiều dòng tiền ra (Chi tiêu); khi gắn với giao dịch, hệ thống trừ số tiền khỏi số dư ví chi trả; khi gắn với nhóm nhu cầu hoặc danh mục, xác lập nhánh phân loại Chi tiêu trong cấu trúc cây danh mục 3 cấp. |
| `displayLabel` | `String` | `private final` | Nhãn hiển thị tiếng Việt tương ứng: `"Thu nhập"` cho `INCOME` và `"Chi tiêu"` cho `EXPENSE`; phục vụ kết xuất bảng giao dịch và báo cáo trên giao diện đồ họa người dùng. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `getDisplayLabel` | Không có | `String` | Không có | Trả về nhãn hiển thị tiếng Việt (`"Thu nhập"` hoặc `"Chi tiêu"`). |
| `isCompatibleWith` | `TransactionType categoryType` | `boolean` | Không có | Kiểm tra loại thu chi có tương thích với danh mục phân loại hay không. |
| `fromString` | `String value` | `TransactionType` | `BusinessException` | Chuyển đổi chuỗi ký tự (`"INCOME"`/`"EXPENSE"`) sang hằng số tương ứng. |
| `opposite` | Không có | `TransactionType` | Không có | Trả về loại thu chi đối lập (`INCOME` ↔ `EXPENSE`). |

**Tính bất biến và cơ chế so sánh**

- Tính đóng và bất biến tuyệt đối của các hằng số liệt kê: `TransactionType` là kiểu `enum` Java với đúng hai hằng số `INCOME` và `EXPENSE`; tập giá trị này cố định vĩnh viễn trong mô hình phân loại, người dùng tuyệt đối không thể thêm, sửa tên hay xóa bỏ nhánh thu chi. Mỗi hằng số mang tính bất biến tuyệt đối sau khi lớp được nạp; không có phương thức sửa đổi thuộc tính `displayLabel` hay bất kỳ trường nào khác.
- Cơ chế so sánh bằng nhau: Vì là kiểu `enum`, phép so sánh tham chiếu `==` và phương thức `equals()` đều cho kết quả đồng nhất; hai hằng số cùng tên luôn là cùng một thể hiện theo đảm bảo của JVM. Không cần ghi đè `hashCode()` vì `enum` đã cung cấp cơ chế mã băm nhất quán theo bản sắc thể hiện.
- Vai trò định hướng kiểm tra tính tương thích giữa giao dịch và danh mục: Mỗi lần ghi nhận hoặc cập nhật giao dịch thu chi, hệ thống sử dụng `isCompatibleWith()` để bảo đảm danh mục phân loại thuộc đúng nhánh loại thu chi tương ứng; vi phạm sẽ bị từ chối và ném ngoại lệ. Khi phân tích cơ cấu báo cáo, `TransactionType` xác định chiều luồng tiền được gom cụm (chi tiêu hay thu nhập) để tính `SpendingStructureShare`.

### 2.3.4 Dòng tiền thuần (NetCashFlow)

Lớp `NetCashFlow` là đối tượng giá trị tiền tệ bất biến đại diện cho dòng tiền ròng có dấu đại số, đo lường mức thặng dư hoặc thâm hụt tài chính thuần của người dùng trong một kỳ quan sát xác định bằng công thức tổng thu nhập trừ tổng chi tiêu (gồm phí giao dịch liên kết theo quy tắc bảo toàn số dư ví BR-FIN-02).

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-01`
- Yêu cầu chức năng: `FR-REP-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi: `ERR-FIN-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `amount` | `BigDecimal` | `private final` | Giá trị số tiền ròng có dấu đại số; biểu diễn bằng `BigDecimal` với quy mô cố định 2 chữ số thập phân theo quy tắc độ chính xác tài chính; nghiêm cấm dùng `double` hoặc `float`; có thể mang giá trị âm (thâm hụt), dương (thặng dư) hoặc bằng 0 (cân bằng thu chi); bất biến sau khi khởi tạo. |
| `currency` | `String` | `private final` | Đơn vị tiền tệ chuẩn hóa, cố định là `"VNĐ"` trên toàn hệ thống; bất biến sau khi khởi tạo. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `NetCashFlow` *(Constructor)* | `BigDecimal amount` | Không có | `BusinessException` | Khởi tạo đối tượng giá trị dòng tiền thuần; kiểm tra `amount` không được `null` và phải đúng định dạng số thập phân; thiết lập quy mô 2 chữ số thập phân với quy tắc làm tròn ngân hàng. |
| `of` *(Factory method)* | `BigDecimal totalIncome, BigDecimal totalExpense` | `NetCashFlow` | `BusinessException` | Tính và khởi tạo dòng tiền thuần từ tổng thu nhập và tổng chi tiêu theo công thức NetCashFlow = TotalIncome - TotalExpense; áp dụng quy tắc làm tròn ngân hàng. |
| `getAmount` | Không có | `BigDecimal` | Không có | Trả về giá trị số tiền ròng có dấu. |
| `getCurrency` | Không có | `String` | Không có | Trả về đơn vị tiền tệ của dòng tiền thuần. |
| `isPositive` | Không có | `boolean` | Không có | Kiểm tra dòng tiền thuần có dương (thặng dư) hay không. |
| `isNegative` | Không có | `boolean` | Không có | Kiểm tra dòng tiền thuần có âm (thâm hụt) hay không. |
| `isZero` | Không có | `boolean` | Không có | Kiểm tra dòng tiền thuần có bằng 0 (cân bằng) hay không. |
| `format` | Không có | `String` | Không có | Định dạng kết xuất có dấu (ví dụ `+12,000,000 VNĐ` hoặc `-5,000,000 VNĐ`). |
| `compareTo` | `NetCashFlow other` | `int` | Không có | So sánh thứ tự giữa hai dòng tiền thuần dựa trên `amount`. |
| `equals` | `Object obj` | `boolean` | Không có | Ghi đè so sánh bằng nhau dựa trên giá trị `amount` và `currency` theo ngữ nghĩa đối tượng giá trị. |
| `hashCode` | Không có | `int` | Không có | Ghi đè mã băm nhất quán với `equals` dựa trên `amount` và `currency`. |

**Tính bất biến và cơ chế so sánh**

- Tính bất biến và khả năng hỗ trợ cả giá trị âm, dương và bằng không: Tất cả thuộc tính được khai báo `private final`; không có phương thức sửa đổi. Dòng tiền thuần có thể mang giá trị âm (thâm hụt khi chi vượt thu), dương (thặng dư khi thu vượt chi) hoặc bằng 0 (cân bằng); điều này phân biệt `NetCashFlow` với `Money` vốn chỉ hỗ trợ giá trị không âm cho số dư ví. Tính toán tuân thủ quy mô cố định 2 chữ số thập phân và quy tắc làm tròn ngân hàng.
- Cơ chế so sánh bằng nhau: Phương thức `equals` được ghi đè để so sánh hai đối tượng `NetCashFlow` dựa trên giá trị `amount` đại số và `currency`; `hashCode` được ghi đè nhất quán với `equals` đảm bảo tính đúng đắn khi sử dụng trong tập hợp.
- Cài đặt `Comparable` để đánh giá và so sánh mức thặng dư qua các kỳ: Lớp cài đặt giao diện `Comparable<NetCashFlow>`; phương thức `compareTo` ủy quyền cho `BigDecimal.compareTo` trên `amount`; phục vụ sắp xếp và so sánh mức thặng dư/thâm hụt tài chính giữa các tháng trong chuỗi 12 tháng của báo cáo dòng tiền.

### 2.3.5 Tỷ lệ tiết kiệm (SavingsRate)

Lớp `SavingsRate` là đối tượng giá trị tỷ lệ phần trăm bất biến đo lường mức độ tích lũy tài chính trong kỳ bằng cách tính dòng tiền thuần chia cho tổng thu nhập, với cơ chế xử lý biên an toàn trả về 0.00% khi thu nhập bằng không để loại trừ hoàn toàn lỗi chia cho số không.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-01`
- Yêu cầu chức năng: `FR-REP-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi: `ERR-FIN-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `rate` | `BigDecimal` | `private final` | Giá trị tỷ lệ tiết kiệm tính bằng phần trăm; biểu diễn bằng `BigDecimal` duy trì tối thiểu 4 chữ số thập phân trong tính toán trung gian và làm tròn ngân hàng về đúng 2 chữ số thập phân khi lưu trữ; có thể mang giá trị âm khi chi vượt thu; bất biến sau khi khởi tạo. |
| `unit` | `String` | `private final` | Đơn vị đo lường tỷ lệ phần trăm, cố định là `"%"`; bất biến sau khi khởi tạo. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `SavingsRate` *(Constructor)* | `BigDecimal rate` | Không có | `BusinessException` | Khởi tạo đối tượng giá trị tỷ lệ tiết kiệm từ giá trị tỷ lệ đã tính toán sẵn; kiểm tra `rate` không được `null`. |
| `of` *(Factory method)* | `BigDecimal netCashFlow, BigDecimal totalIncome` | `SavingsRate` | `BusinessException` | Tính và khởi tạo tỷ lệ tiết kiệm theo công thức: khi `totalIncome > 0` áp dụng SavingsRate = (NetCashFlow / TotalIncome) × 100%; khi `totalIncome ≤ 0` gán mặc định `0.00%` tránh lỗi chia cho 0; bước tính trung gian duy trì tối thiểu 4 chữ số thập phân rồi làm tròn ngân hàng về 2 chữ số thập phân. |
| `getRate` | Không có | `BigDecimal` | Không có | Trả về giá trị tỷ lệ tiết kiệm phần trăm. |
| `getUnit` | Không có | `String` | Không có | Trả về ký hiệu đơn vị phần trăm phục vụ định dạng kết xuất. |
| `isNegative` | Không có | `boolean` | Không có | Kiểm tra tỷ lệ tiết kiệm có âm hay không. |
| `format` | Không có | `String` | Không có | Định dạng chuỗi phần trăm với đúng 2 chữ số thập phân (ví dụ `"40.00%"`). |
| `compareTo` | `SavingsRate other` | `int` | Không có | So sánh thứ tự giữa hai tỷ lệ tiết kiệm dựa trên `rate`. |
| `equals` | `Object obj` | `boolean` | Không có | Ghi đè so sánh bằng nhau dựa trên giá trị `rate` theo ngữ nghĩa đối tượng giá trị. |
| `hashCode` | Không có | `int` | Không có | Ghi đè mã băm nhất quán với `equals` dựa trên `rate`. |

**Tính bất biến và cơ chế so sánh**

- Tính bất biến và xử lý an toàn tránh lỗi chia cho không khi thu nhập bằng không: Tất cả thuộc tính được khai báo `private final`; không có phương thức sửa đổi trạng thái sau khi tạo. Phương thức factory `of()` thực thi kiểm tra biên: khi `totalIncome ≤ 0`, thay vì thực hiện phép chia sẽ gán trực tiếp `rate = 0.00` nhằm ngăn chặn ngoại lệ số học; khi `totalIncome > 0`, phép tính sử dụng độ chính xác trung gian tối thiểu 4 chữ số thập phân và áp dụng quy tắc làm tròn ngân hàng (`HALF_EVEN`) về đúng 2 chữ số thập phân.
- Cơ chế so sánh bằng nhau: Phương thức `equals` được ghi đè để so sánh hai đối tượng `SavingsRate` dựa trên giá trị đại số của `rate` (`BigDecimal.compareTo` = 0); phương thức `hashCode` được ghi đè nhất quán với `equals` để đảm bảo an toàn khi lưu trữ trong cấu trúc dữ liệu bảng băm.
- Cài đặt `Comparable` để so sánh hiệu quả tiết kiệm giữa các kỳ: Lớp cài đặt giao diện `Comparable<SavingsRate>`; phương thức `compareTo` ủy quyền so sánh giá trị cho `BigDecimal.compareTo` trên thuộc tính `rate`; phục vụ phân tích biến động và so sánh tỷ lệ tiết kiệm tháng-trên-tháng trong chuỗi 12 tháng của báo cáo dòng tiền.

### 2.3.6 Tỷ trọng cơ cấu thu chi (SpendingStructureShare)

Lớp `SpendingStructureShare` là đối tượng giá trị tỷ trọng bất biến đo lường phần trăm đóng góp của từng nhóm nhu cầu (cấp 2, phạm vi `"NeedGroup"`) trên tổng kỳ, hoặc từng danh mục chi tiết (cấp 3, phạm vi `"Category"`) trên tổng kỳ và tổng nhóm cha cho cả luồng `EXPENSE` (bao gồm phí giao dịch liên kết) lẫn `INCOME`, với ràng buộc giá trị trong đoạn [0.00, 100.00] và cơ chế bảo vệ mẫu số bằng không trả về 0.00%.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-02`
- Yêu cầu chức năng: `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi: `ERR-FIN-002`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `share` | `BigDecimal` | `private final` | Tỷ trọng phần trăm đóng góp trong khoảng [0.00, 100.00]; biểu diễn bằng `BigDecimal` duy trì tối thiểu 4 chữ số thập phân trong tính toán trung gian và làm tròn ngân hàng về đúng 2 chữ số thập phân khi lưu trữ; bất biến sau khi khởi tạo. |
| `unit` | `String` | `private final` | Đơn vị đo lường tỷ trọng, cố định là `"%"`; bất biến sau khi khởi tạo. |
| `scope` | `String` | `private final` | Phạm vi phân tích cơ cấu, nhận giá trị `"NeedGroup"` (nhóm nhu cầu cấp 2) hoặc `"Category"` (danh mục chi tiết cấp 3); bất biến sau khi khởi tạo. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `SpendingStructureShare` *(Constructor)* | `BigDecimal share, String scope` | Không có | `BusinessException` | Khởi tạo đối tượng giá trị tỷ trọng cơ cấu; kiểm tra `share` không được `null`, phải nằm trong đoạn [0.00, 100.00]; kiểm tra `scope` hợp lệ; thiết lập làm tròn ngân hàng 2 chữ số thập phân. |
| `of` *(Factory method)* | `BigDecimal itemAmount, BigDecimal totalAmount, String scope` | `SpendingStructureShare` | `BusinessException` | Tính và khởi tạo tỷ trọng cơ cấu: khi `totalAmount > 0` áp dụng Share = (ItemAmount / TotalAmount) × 100%; khi `totalAmount = 0` gán mặc định `0.00%` tránh lỗi chia cho 0; tính toán duy trì tối thiểu 4 chữ số thập phân trung gian rồi làm tròn ngân hàng về 2 chữ số thập phân. |
| `getShare` | Không có | `BigDecimal` | Không có | Trả về giá trị tỷ trọng phần trăm. |
| `getUnit` | Không có | `String` | Không có | Trả về ký hiệu đơn vị phần trăm (`"%"`) phục vụ định dạng kết xuất. |
| `getScope` | Không có | `String` | Không có | Trả về phạm vi phân loại cấp 2 (`"NeedGroup"`) hoặc cấp 3 (`"Category"`). |
| `isZero` | Không có | `boolean` | Không có | Kiểm tra tỷ trọng có bằng 0.00% hay không. |
| `format` | Không có | `String` | Không có | Định dạng chuỗi tỷ trọng với 2 chữ số thập phân (ví dụ `"40.00%"`). |
| `compareTo` | `SpendingStructureShare other` | `int` | Không có | So sánh thứ tự giữa hai tỷ trọng theo chiều giảm dần. |
| `equals` | `Object obj` | `boolean` | Không có | Ghi đè so sánh bằng nhau dựa trên giá trị đại số của `share` (`compareTo` = 0) và thuộc tính `scope` theo ngữ nghĩa đối tượng giá trị. |
| `hashCode` | Không có | `int` | Không có | Ghi đè mã băm nhất quán với `equals` dựa trên `share` và `scope`. |

**Tính bất biến và cơ chế so sánh**

- Tính bất biến và ràng buộc giá trị tỷ trọng nằm trong khoảng từ không đến một trăm phần trăm: Tất cả thuộc tính được khai báo `private final`; không cung cấp phương thức thay đổi trạng thái. Ràng buộc bất biến 0.00 ≤ share ≤ 100.00 được kiểm tra nghiêm ngặt ngay tại constructor và factory method. Khi tổng số tiền phân tích bằng 0, phương thức factory gán mặc định 0.00% nhằm loại trừ lỗi chia cho số không. Tổng tỷ trọng của n thành phần trong cùng cấp phân loại có thể lệch tối đa trong khoảng ± 0.005% × n do mỗi thành phần được làm tròn ngân hàng độc lập theo quy tắc độ chính xác tài chính.
- Cơ chế so sánh bằng nhau qua equals và hashCode: Phương thức `equals` được ghi đè để so sánh hai đối tượng `SpendingStructureShare` dựa trên giá trị tỷ trọng `share` (so sánh bằng `compareTo` trên `BigDecimal` để bỏ qua khác biệt về scale hiển thị) kết hợp với phạm vi phân cấp `scope`; phương thức `hashCode` được sinh nhất quán từ các trường tương ứng.
- Cài đặt Comparable để sắp xếp tỷ trọng thu chi giảm dần: Lớp cài đặt giao diện `Comparable<SpendingStructureShare>`; phương thức `compareTo` thực hiện so sánh đảo ngược (`other.share.compareTo(this.share)`) để tự động sắp xếp danh sách nhóm nhu cầu và danh mục theo thứ tự tỷ trọng giảm dần từ cao xuống thấp khi kết xuất bảng báo cáo cơ cấu thu chi.

### 2.3.7 Tên định danh thực thể (EntityName)

Lớp `EntityName` là đối tượng giá trị bất biến đại diện cho tên gọi định danh của các thực thể cốt lõi trong hệ thống (`Wallet`, `Category` và `NeedGroup`). Lớp này đóng gói quy tắc cắt tỉa khoảng trắng, kiểm soát độ dài hợp lệ từ 1 đến 50 ký tự theo quy tắc đặt tên `BR-NAM-01`, tạo khóa chuẩn hóa chữ thường phục vụ kiểm tra trùng lặp không phân biệt hoa thường và loại bỏ hoàn toàn mã lặp tại các thực thể.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`, `UC-WAL-03`, `UC-CAT-01`, `UC-CAT-02`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-WAL-02`, `FR-CAT-01`, `FR-CAT-02`
- Quy tắc nghiệp vụ: `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `value` | `String` | `private final` | Chuỗi ký tự tên gọi sau khi cắt tỉa khoảng trắng hai đầu; bắt buộc có độ dài từ 1 đến 50 ký tự; không bao giờ mang giá trị `null` hoặc rỗng; bất biến sau khi khởi tạo. |
| `normalizedKey` | `String` | `private final` | Khóa chuỗi viết thường chuẩn hóa theo ngôn ngữ (`value.toLowerCase(Locale.ROOT)`); dùng để so sánh bằng nhau và kiểm tra trùng lặp không phân biệt hoa thường; bất biến sau khi khởi tạo. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `EntityName` *(Constructor)* | `String rawName, String fieldLabel` | Không có | `BusinessException` | Cắt tỉa khoảng trắng hai đầu; kiểm tra `rawName` không được `null` hoặc rỗng và độ dài từ 1 đến 50 ký tự theo `BR-NAM-01`; ném `BusinessException(ErrorCode.ERR_NAM_001)` nếu vi phạm; khởi tạo `value` và `normalizedKey`. |
| `of` *(Factory method)* | `String rawName` | `EntityName` | `BusinessException` | Phương thức tĩnh thuận tiện tạo thể hiện `EntityName` với nhãn mặc định `"Tên"`. |
| `of` *(Factory method)* | `String rawName, String fieldLabel` | `EntityName` | `BusinessException` | Phương thức tĩnh tạo thể hiện `EntityName` kèm theo nhãn trường tùy biến phục vụ thông báo lỗi chi tiết. |
| `getValue` | Không có | `String` | Không có | Trả về chuỗi tên đã chuẩn hóa khoảng trắng. |
| `getNormalizedKey` | Không có | `String` | Không có | Trả về khóa chuẩn hóa chữ thường. |
| `length` | Không có | `int` | Không có | Trả về độ dài chuỗi ký tự của tên đã chuẩn hóa. |
| `compareTo` | `EntityName other` | `int` | Không có | So sánh thứ tự từ điển tự nhiên giữa hai tên theo `value`. |
| `equals` | `Object obj` | `boolean` | Không có | Ghi đè so sánh bằng nhau dựa trên `normalizedKey` theo ngữ nghĩa đối tượng giá trị không phân biệt hoa thường. |
| `hashCode` | Không có | `int` | Không có | Ghi đè mã băm nhất quán với `equals` dựa trên `normalizedKey` phục vụ tìm kiếm và quản lý trong tập hợp `Set` hoặc `Map`. |
| `toString` | Không có | `String` | Không có | Trả về chuỗi giá trị tên `value` tương đương với phương thức `getValue()`. |

**Tính bất biến và cơ chế so sánh**

- Tính bất biến tuyệt đối và đóng gói quy tắc đặt tên: Tất cả thuộc tính được khai báo `private final`; lớp không cung cấp bất kỳ phương thức setter nào. Mọi thao tác đổi tên thực thể đều thực hiện bằng cách tạo một thể hiện `EntityName` mới thay thế. Quy tắc kiểm tra độ dài 1 đến 50 ký tự và cắt tỉa khoảng trắng theo `BR-NAM-01` được tập trung hoàn toàn tại đây, giải phóng các thực thể `Wallet` và `Category` khỏi việc trùng lặp mã kiểm tra.
- Cơ chế so sánh bằng nhau không phân biệt chữ hoa chữ thường: Phương thức `equals()` so sánh trực tiếp thuộc tính `normalizedKey` (`value.toLowerCase(Locale.ROOT)`), bảo đảm các chuỗi chỉ khác nhau về cách viết hoa thường (ví dụ `"Tiền mặt"` và `"TIỀN MẶT"`) được hệ thống định danh là cùng một giá trị, ngăn chặn triệt để hiện tượng trùng tên logic tại kho lưu trữ.
- Cài đặt Comparable và Serializable: Lớp cài đặt giao diện `Comparable<EntityName>` ủy quyền so sánh thứ tự từ điển tự nhiên cho `value.compareTo(other.value)` và cài đặt `java.io.Serializable` bảo đảm an toàn khi lưu trữ dữ liệu hoặc truyền tải giữa các thành phần của phần mềm.

### 2.3.8 Ghi chú diễn giải (Note)

Lớp `Note` là đối tượng giá trị bất biến đại diện cho nội dung ghi chú diễn giải tùy chọn đi kèm theo các thực thể tài chính (`Wallet`, `Transaction` và các lớp con giao dịch). Lớp này đóng gói quy tắc cắt tỉa khoảng trắng, giới hạn độ dài tối đa 255 ký tự, loại bỏ hoàn toàn giá trị `null` thông qua mô hình Đối tượng Rỗng (`Null Object Pattern`) và cung cấp hằng số rỗng dùng chung `Note.EMPTY`.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`, `UC-WAL-03`, `UC-TXN-01`, `UC-TXN-02`, `UC-TRF-01`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-WAL-02`, `FR-TXN-01`, `FR-TXN-02`, `FR-TRF-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi: `ERR-FIN-001`

**Cấu trúc thuộc tính**

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Ràng buộc |
| :--- | :--- | :--- | :--- |
| `EMPTY` | `Note` | `public static final` | Thể hiện đối tượng rỗng đại diện cho trường hợp không có ghi chú hoặc ghi chú chỉ chứa khoảng trắng, mang giá trị chuỗi rỗng `""`. |
| `MAX_LENGTH` | `int` | `public static final` | Hằng số quy định độ dài tối đa cho phép của ghi chú, cố định là 255 ký tự trên toàn hệ thống. |
| `value` | `String` | `private final` | Chuỗi ghi chú sau khi chuẩn hóa; không bao giờ mang giá trị `null`; độ dài từ 0 đến 255 ký tự; bất biến sau khi khởi tạo. |

**Phương thức nghiệp vụ**

| Phương thức | Tham số | Kiểu trả về | Ngoại lệ | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `Note` *(Constructor)* | `String rawNote, String fieldLabel` | Không có | `BusinessException` | Cắt tỉa khoảng trắng hai đầu; chuyển `null` thành chuỗi rỗng; kiểm tra độ dài không vượt quá 255 ký tự; ném `BusinessException(ErrorCode.ERR_FIN_001)` nếu vượt quá; gán cho `value`. |
| `empty` *(Factory method)* | Không có | `Note` | Không có | Trả về thể hiện đối tượng rỗng `Note.EMPTY`. |
| `of` *(Factory method)* | `String rawNote` | `Note` | `BusinessException` | Phương thức tĩnh tạo thể hiện `Note` với nhãn mặc định `"Ghi chú"`; tự động trả về `Note.EMPTY` nếu đầu vào là `null` hoặc rỗng. |
| `of` *(Factory method)* | `String rawNote, String fieldLabel` | `Note` | `BusinessException` | Phương thức tĩnh tạo thể hiện `Note` kèm nhãn trường tùy biến phục vụ thông báo lỗi chi tiết; tự động trả về `Note.EMPTY` nếu rỗng. |
| `getValue` | Không có | `String` | Không có | Trả về chuỗi ghi chú đã chuẩn hóa (không bao giờ `null`). |
| `length` | Không có | `int` | Không có | Trả về số lượng ký tự của chuỗi ghi chú nội tại. |
| `isEmpty` | Không có | `boolean` | Không có | Kiểm tra ghi chú có rỗng hay không; trả về `true` khi `value.isEmpty()`. |
| `isPresent` | Không có | `boolean` | Không có | Kiểm tra ghi chú có chứa nội dung hay không; tương đương `!isEmpty()`. |
| `compareTo` | `Note other` | `int` | Không có | So sánh thứ tự từ điển giữa hai ghi chú dựa trên `value`. |
| `equals` | `Object obj` | `boolean` | Không có | Ghi đè so sánh bằng nhau dựa trên thuộc tính `value` theo ngữ nghĩa đối tượng giá trị. |
| `hashCode` | Không có | `int` | Không có | Ghi đè mã băm nhất quán với `equals` dựa trên `value` phục vụ lưu trữ trong tập hợp. |
| `toString` | Không có | `String` | Không có | Trả về chuỗi nội dung ghi chú `value` tương đương với phương thức `getValue()`. |

**Tính bất biến và cơ chế so sánh**

- Áp dụng mô hình Đối tượng Rỗng (Null Object Pattern): Lớp `Note` loại bỏ hoàn toàn các lỗi truy cập con trỏ rỗng (`NullPointerException`) bằng cách chuẩn hóa mọi đầu vào `null` hoặc khoảng trắng rỗng về thể hiện duy nhất `Note.EMPTY`. Các thực thể sở hữu không cần thực hiện kiểm tra `null` thủ công trước khi truy xuất chuỗi ghi chú.
- Tính bất biến và kiểm soát giới hạn độ dài: Thuộc tính `value` được bảo vệ đóng gói `private final`; mọi thao tác chỉnh sửa ghi chú trong `Wallet.updateInfo()` hay `Transaction.updateDetails()` đều tạo thể hiện `Note` mới độc lập. Logic giới hạn độ dài tối đa 255 ký tự được tập trung tại một nơi duy nhất.
- Cài đặt Comparable và Serializable: Lớp cài đặt giao diện `Comparable<Note>` và `java.io.Serializable`, bảo đảm khả năng sắp xếp từ điển và tính tương thích tuần tự hóa dữ liệu khi lưu trữ JSON hoặc sao lưu hệ thống.

\newpage

# 03. Thiết kế tầng dịch vụ ứng dụng và điều phối nghiệp vụ

Tài liệu này xác lập thiết kế chi tiết cho tầng dịch vụ ứng dụng (`com.pfms.service`) của hệ thống Quản lý Tài chính Cá nhân (PFMS), chuyển đổi các ca sử dụng từ mô hình phân tích sang cấu trúc các lớp dịch vụ điều phối luồng nghiệp vụ trên Java.

Thiết kế tập trung giải quyết ba mục tiêu kỹ thuật trọng tâm: điều phối vòng đời và luồng thực thi ca sử dụng làm cầu nối giữa giao diện và thực thể miền; phân tách rạch ròi giữa giao diện công khai (`com.pfms.service`) và lớp cài đặt (`com.pfms.service.impl`); và bảo đảm tính nguyên tử của dữ liệu tài chính qua kho lưu trữ trừu tượng, ủy quyền toàn bộ phép tính số học cho tầng nghiệp vụ cốt lõi.

## 3.1 Tổng quan và danh mục dịch vụ ứng dụng

Mục này xác lập cấu trúc phân tách giữa giao diện công khai và lớp cài đặt nội bộ của tầng dịch vụ, đồng thời cung cấp bảng danh mục đối chiếu các dịch vụ điều phối trong toàn bộ hệ thống.

Cấu trúc mã nguồn của tầng dịch vụ được phân định rạch ròi thành hai không gian tên:
- **Giao diện công khai (`com.pfms.service`):** Định nghĩa toàn bộ các hợp đồng phương thức mà tầng trình diễn (`com.pfms.presentation`) được phép triệu gọi. Tầng trình diễn chỉ phụ thuộc duy nhất vào các giao diện này, hoàn toàn không được phép tiếp xúc với lớp cài đặt cụ thể hay các thực thể nghiệp vụ.
- Lớp cài đặt dịch vụ (`com.pfms.service.impl.*`): Hiện thực hóa các giao diện dịch vụ dưới dạng lớp `public` trong các gói con theo từng phân hệ để hỗ trợ khởi tạo và tiêm phụ thuộc, còn các lớp trợ giúp và điều phối chuyên biệt bên trong mỗi phân hệ giữ phạm vi truy cập nội bộ gói (`package-private`). Lớp cài đặt tiếp nhận đối tượng truyền dữ liệu (DTO), tiêm các giao diện kho lưu trữ (`com.pfms.domain.repository`) qua hàm khởi tạo để truy xuất dữ liệu, và ủy quyền các phép tính toán số học cho các thực thể thuộc tầng nghiệp vụ.

**Bảng tổng hợp danh mục dịch vụ ứng dụng:**

| Dịch vụ | Giao diện công khai | Lớp cài đặt nội bộ | Trách nhiệm điều phối chính |
| :--- | :--- | :--- | :--- |
| **Quản lý ví** | `WalletService` | `WalletServiceImpl` | Điều phối tạo ví mới, cập nhật thông tin, điều chỉnh số dư kiểm kê thực tế và tính toán cơ cấu phân bổ dòng tiền. |
| **Ghi nhận thu chi** | `TransactionService` | `TransactionServiceImpl` | Điều phối ghi nhận thu chi kèm chi phí giao dịch, cập nhật số dư ví tức thời và thực hiện quy trình hoàn nguyên hai pha khi sửa hoặc xóa. |
| **Chuyển tiền nội bộ** | `TransferService` | `TransferServiceImpl` | Điều phối chuyển khoản hai chiều giữa hai ví, hạch toán chi phí phát sinh và bảo toàn tổng tài sản hệ thống. |
| **Danh mục phân loại** | `CategoryService` | `CategoryServiceImpl` | Quản lý cấu trúc cây danh mục 3 cấp, kiểm soát tính duy nhất của tên và thực thi cơ chế xóa mềm bảo toàn lịch sử giao dịch. |
| **Báo cáo và thống kê** | `ReportService` | `ReportServiceImpl` | Vận hành ở chế độ chỉ đọc: trích xuất dữ liệu, tổng hợp dòng tiền 12 tháng, đo lường tỷ lệ tiết kiệm và phân tích cơ cấu thu chi. |
| **Quản trị và sao lưu** | `DataManagementService` | `DataManagementServiceImpl` | Điều phối xuất dữ liệu JSON, phục hồi dữ liệu hệ thống theo quy trình 4 bước và đối soát độc lập 4 điều kiện toàn vẹn tài chính. |

## 3.2 Dịch vụ quản lý ví (WalletService)

Dịch vụ này điều phối toàn bộ vòng đời ví thanh toán của người dùng, từ khởi tạo ví mặc định và ví tạo thêm, cập nhật thông tin và trạng thái sử dụng cho đến kiểm kê điều chỉnh số dư thực tế và tổng hợp cơ cấu phân bổ tài sản. Toàn bộ phép toán số học và kiểm soát bất biến số dư được ủy quyền cho thực thể `Wallet`, `BalanceAdjustmentRecord` cùng các đối tượng giá trị `Money` và `TransactionDate`.

**Giao diện công khai:**

Giao diện `com.pfms.service.WalletService` cung cấp các phương thức sau cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `initializeDefaultWallet` | `Không có` | `void` | `StorageException` | Khởi tạo ví mặc định `"Tiền mặt"` (ACTIVE, số dư 0.00 VNĐ) khi khởi động nếu chưa tồn tại. |
| `createWallet` | `CreateWalletRequestDTO request` | `WalletResponseDTO` | `BusinessException`, `DuplicateEntityException`, `StorageException` | Xác thực tên ví (1–50 ký tự), kiểm tra trùng tên và tạo ví mới (ACTIVE, số dư 0.00 VNĐ). |
| `getWalletsSummary` | `EntityStatus statusFilter` hoặc `String statusFilter` hoặc không tham số | `WalletsSummaryResponseDTO` | `StorageException` | Truy xuất danh sách ví theo bộ lọc (`"ACTIVE"`, `"ARCHIVED"`, `"ALL"`), tính tổng số dư và tỷ trọng phân bổ của từng ví ở chế độ chỉ đọc. |
| `updateWallet` | `UpdateWalletRequestDTO request` | `WalletResponseDTO` | `EntityNotFoundException`, `BusinessException`, `DuplicateEntityException`, `StorageException` | Xác thực tên mới không trùng và cập nhật tên cùng ghi chú ví, bảo toàn số dư hiện có. |
| `archiveWallet` | `UUID walletId` | `WalletResponseDTO` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Chuyển ví đang hoạt động (không phải ví mặc định) sang trạng thái lưu trữ (`ARCHIVED`). |
| `activateWallet` | `UUID walletId` | `WalletResponseDTO` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Khôi phục ví đang lưu trữ (`ARCHIVED`) về trạng thái hoạt động bình thường (`ACTIVE`). |
| `deleteWallet` | `UUID walletId` | `void` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Xóa vĩnh viễn ví không phải mặc định khi chưa từng phát sinh giao dịch hoặc bản ghi kiểm kê. |
| `adjustBalance` | `AdjustBalanceRequestDTO request` | `BalanceAdjustmentResponseDTO` | `EntityNotFoundException`, `BusinessException`, `StorageException` | Cập nhật số dư ví theo kiểm kê thực tế và lập bản ghi `BalanceAdjustmentRecord` bất biến lưu vết kiểm toán. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.wallet.WalletServiceImpl` nhận `WalletRepository`, `BalanceAdjustmentRepository` và `TransactionRepository` qua hàm khởi tạo. Trước khi gọi thực thể miền, dịch vụ chuyển đổi các giá trị thô từ DTO sang `Money` và `TransactionDate` để kích hoạt kiểm tra chuẩn tài chính và lịch pháp ngay ở đầu luồng điều phối.

**Phương thức `initializeDefaultWallet`:**

- Kiểm tra danh sách ví hiện có trong `WalletRepository`; nếu đã tồn tại ví đánh dấu mặc định thì dừng xử lý mà không ghi đè dữ liệu.
- Nếu chưa có ví mặc định khi khởi tạo hệ thống lần đầu, sinh mã `UUID` và khởi tạo thực thể `Wallet` với tên `"Tiền mặt"`, cờ `isDefault` mang giá trị `true`, trạng thái `ACTIVE` và số dư `Money.ZERO`.
- Lưu ví mặc định vào `WalletRepository` và chuyển tiếp `StorageException` nếu thao tác ghi tệp gặp sự cố.
- Hoàn tất khởi tạo để tầng trình diễn sẵn sàng mở phiên làm việc.

**Phương thức `createWallet`:**

- Cắt khoảng trắng hai đầu tên ví và ghi chú; từ chối nếu tên rỗng, dài quá 50 ký tự hoặc ghi chú vượt quá 255 ký tự, sau đó truy vấn `WalletRepository` để chặn trường hợp trùng tên không phân biệt hoa thường trên cả ví đang hoạt động lẫn ví đã lưu trữ.
- Sinh mã định danh `UUID` và khởi tạo thực thể `Wallet` với cờ `isDefault` mang giá trị `false`, trạng thái `ACTIVE` và số dư khởi điểm `Money.ZERO`.
- Ghi nhận thực thể mới vào `WalletRepository` và bảo đảm giữ nguyên trạng thái dữ liệu nếu thao tác lưu phát sinh `StorageException`.
- Ánh xạ thông tin ví vừa tạo sang `WalletResponseDTO` để trả về tầng trình diễn.

**Phương thức `getWalletsSummary`:**

- Tiếp nhận bộ lọc trạng thái (hỗ trợ `EntityStatus`, chuỗi lọc `String statusFilter` như `"ACTIVE"`, `"ARCHIVED"`, `"ALL"`, hoặc phương thức nạp mặc định không tham số cho ví `ACTIVE`) và nạp danh sách ví từ `WalletRepository`.
- Tính tổng số dư toàn hệ thống bằng `Money` trên tất cả ví `ACTIVE` cùng các ví `ARCHIVED` có số dư dương; giữ nguyên cơ chế chỉ đọc, không thay đổi trạng thái bất kỳ thực thể nào.
- Tính tỷ trọng phần trăm từng ví với tối thiểu 4 chữ số thập phân trung gian rồi làm tròn nửa chẵn về 2 chữ số; gán `0.00%` cho mọi ví khi tổng tài sản bằng không, loại các ví `ARCHIVED` có số dư bằng không khỏi bảng cơ cấu và sắp xếp bảng cơ cấu theo tỷ trọng giảm dần.
- Đóng gói danh sách ví đã lọc, tổng số dư toàn hệ thống và bảng cơ cấu vào `WalletsSummaryResponseDTO`.

**Phương thức `updateWallet`:**

- Nạp ví theo mã định danh từ `WalletRepository`, ném `EntityNotFoundException` nếu không tồn tại; chuẩn hóa và kiểm tra định dạng tên mới cùng ghi chú mới.
- Chỉ kiểm tra trùng tên không phân biệt hoa thường khi tên mới sau chuẩn hóa khác tên hiện tại nhằm cho phép giữ nguyên tên cũ khi chỉ sửa ghi chú.
- Ủy quyền phương thức `updateInfo` của `Wallet` để cập nhật tên và ghi chú trong khi bảo toàn mã định danh và số dư hiện có, sau đó lưu lại qua `WalletRepository`.
- Chuyển thực thể sau cập nhật sang `WalletResponseDTO`.

**Phương thức `archiveWallet` và `activateWallet`:**

- Nạp thực thể `Wallet` từ `WalletRepository` theo mã định danh và xác nhận ví tồn tại.
- Ủy quyền `wallet.archive()` hoặc `wallet.activate()` kiểm tra điều kiện chuyển trạng thái, trong đó `archive()` tự động từ chối nếu đối tượng là ví `"Tiền mặt"` mặc định hoặc đang không ở trạng thái `ACTIVE`.
- Lưu trạng thái vòng đời mới xuống `WalletRepository` và giữ nguyên số dư hiện hành của ví.
- Trả về `WalletResponseDTO` phản ánh trạng thái mới để tầng trình diễn cập nhật hiển thị.

**Phương thức `deleteWallet`:**

- Nạp ví từ `WalletRepository` theo mã định danh và từ chối ngay nếu đây là ví `"Tiền mặt"` mặc định.
- Kiểm tra lịch sử liên quan qua `TransactionRepository.findByWalletId` và `BalanceAdjustmentRepository.findByWalletId`; từ chối xóa nếu ví đã có ít nhất một giao dịch thu chi, chuyển tiền hoặc bản ghi kiểm kê số dư.
- Gọi `deleteById` trên `WalletRepository` để xóa vĩnh viễn ví rỗng lịch sử và giải phóng tên ví khỏi tập kiểm tra duy nhất.
- Kết thúc xử lý mà không trả về dữ liệu.

**Phương thức `adjustBalance`:**

- Chuyển đổi số dư thực tế sang `Money` (yêu cầu đúng 2 chữ số thập phân và không âm), chuyển đổi ngày kiểm kê sang `TransactionDate` (mặc định là ngày hiện tại nếu chưa nhập và không vượt quá hiện tại), đồng thời chuẩn hóa lý do điều chỉnh dài tối đa 255 ký tự.
- Nạp ví từ `WalletRepository`, xác nhận ví đang ở trạng thái `ACTIVE`, rồi gọi `wallet.adjustBalance(...)` để kiểm tra độ lệch phải khác không, cập nhật số dư ví và sinh bản ghi `BalanceAdjustmentRecord` bất biến.
- Lưu đồng thời `Wallet` qua `WalletRepository` và bản ghi kiểm kê qua `BalanceAdjustmentRepository`; nếu thao tác lưu sau phát sinh `StorageException`, khôi phục lại trạng thái ví trước kiểm kê nhằm giữ đồng bộ giữa số dư và vết kiểm toán.
- Đóng gói số dư trước kiểm kê, số dư sau kiểm kê và phần chênh lệch vào `BalanceAdjustmentResponseDTO`.

## 3.3 Dịch vụ ghi nhận thu chi (TransactionService)

Dịch vụ này điều phối nghiệp vụ ghi nhận khoản thu nhập hoặc chi tiêu hàng ngày, tra cứu lịch sử giao dịch theo nhiều tiêu chí, chỉnh sửa giao dịch theo quy trình hoàn nguyên an toàn và xóa giao dịch kèm chi phí phát sinh. Mọi phép tính số dư và ràng buộc hợp thành của phí giao dịch được giao cho `IncomeExpenseTransaction`, `TransactionFee`, `TransactionFactory`, `Money` và `TransactionDate`.

**Giao diện công khai:**

Giao diện `com.pfms.service.TransactionService` cung cấp các phương thức sau cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `recordIncomeExpense` | `RecordTransactionRequestDTO request` | `TransactionResponseDTO` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Xác thực ví và danh mục, tạo giao dịch thu chi kèm phụ phí (nếu có) và cập nhật số dư ví tức thời. |
| `filterTransactions` | `FilterTransactionsRequestDTO request` | `TransactionListResponseDTO` | `BusinessException`, `StorageException` | Truy vấn, lọc và phân trang danh sách giao dịch thu chi kèm phí liên kết; tổng hợp dòng tiền ở chế độ chỉ đọc. |
| `updateTransaction` | `UpdateTransactionRequestDTO request` | `TransactionResponseDTO` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Hoàn nguyên giao dịch cũ, kiểm tra điều kiện số dư ví mới và cập nhật giao dịch cùng phí liên kết. |
| `deleteTransaction` | `UUID transactionId` | `void` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Kiểm tra điều kiện không âm ví, hoàn nguyên số dư và xóa vĩnh viễn giao dịch cùng phí liên kết. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.transaction.TransactionServiceImpl` nhận `TransactionRepository`, `WalletRepository` và `CategoryRepository` qua hàm khởi tạo. Do `TransactionRepository` lưu trữ đa hình nhiều loại giao dịch, lớp này luôn lọc đúng kiểu `IncomeExpenseTransaction` trong mọi nghiệp vụ thu chi và giữ nguyên bất biến loại thu chi của giao dịch sau khi tạo.

**Phương thức `recordIncomeExpense`:**

- Chuyển đổi số tiền và mức phí sang `Money` (yêu cầu số tiền lớn hơn `0.00 VNĐ` và mức phí không âm), chuyển đổi ngày phát sinh sang `TransactionDate` (không vượt quá hiện tại) và chuẩn hóa ghi chú tối đa 255 ký tự; nếu mức phí dương thì bắt buộc có mã danh mục phí.
- Nạp ví từ `WalletRepository` bảo đảm đang `ACTIVE`; nạp danh mục cấp 3 từ `CategoryRepository` bảo đảm đang `ACTIVE` (và danh mục chi tiêu cấp 3 đang `ACTIVE` cho phí nếu phát sinh); gọi `TransactionFactory` tạo `IncomeExpenseTransaction` kèm `TransactionFee` hợp thành rồi thực thi `applyToWallet()` sau khi xác nhận số dư ví sau hạch toán không bị âm.
- Lưu bản ghi giao dịch vào `TransactionRepository` và lưu ví đã đổi số dư vào `WalletRepository`; nếu bước lưu thứ hai ném `StorageException`, thực hiện xóa bù trừ giao dịch vừa lưu và hoàn tác trạng thái ví trước khi chuyển tiếp ngoại lệ.
- Chuyển thực thể giao dịch cùng số dư ví mới sang `TransactionResponseDTO`.

**Phương thức `filterTransactions`:**

- Chuyển đổi mốc ngày bắt đầu và ngày kết thúc (nếu có) sang `TransactionDate` để kiểm tra ngày bắt đầu không đứng sau ngày kết thúc; kiểm tra chỉ số trang tối thiểu bằng 1 và kích thước trang trong đoạn từ 10 đến 100 bản ghi (mặc định 50 nếu chưa chỉ định).
- Nạp toàn bộ bản ghi thuộc kiểu `IncomeExpenseTransaction` từ `TransactionRepository`, áp dụng bộ lọc theo tập ví, loại giao dịch, danh mục hoặc nhóm nhu cầu (bao gồm các danh mục đã xóa mềm) và từ khóa tìm kiếm không phân biệt hoa thường trên ghi chú; tách phần `TransactionFee` hợp thành của từng giao dịch thành dòng hiển thị phụ thuộc và sắp xếp theo ngày giao dịch giảm dần rồi đến mốc tạo giảm dần.
- Thực thi phân trang trên tập kết quả và dùng `Money` tính tổng thu nhập, tổng chi tiêu (bao gồm cả các khoản phí giao dịch liên kết) cùng dòng tiền thuần hoàn toàn ở chế độ chỉ đọc.
- Đóng gói dữ liệu trang hiện tại, thông số phân trang và ba chỉ số dòng tiền vào `TransactionListResponseDTO`.

**Phương thức `updateTransaction`:**

- Nạp bản ghi theo mã định danh và xác nhận đây là `IncomeExpenseTransaction`, từ chối mọi yêu cầu sửa trực tiếp đối với `TransactionFee`; chuyển đổi số tiền mới, phí mới sang `Money` (số tiền dương, phí không âm), chuyển đổi ngày mới sang `TransactionDate` và chuẩn hóa ghi chú tối đa 255 ký tự.
- Nạp ví mới đang `ACTIVE`; kiểm tra danh mục mới tương thích với `transactionType` bất biến của giao dịch (cho phép giữ danh mục cũ đã xóa mềm nếu không thay đổi, nhưng bắt buộc danh mục mới phải đang `ACTIVE` nếu có đổi), đồng thời kiểm tra danh mục phí mới thuộc nhánh chi tiêu khi phí mới dương; mô phỏng hai pha hoàn nguyên ví cũ rồi áp dụng dữ liệu mới để bảo đảm cả hai ví đều không âm trước khi gọi `updateDetails(...)` (tự động tạo, sửa hoặc gỡ `TransactionFee` tương ứng).
- Lưu giao dịch vào `TransactionRepository` cùng ví cũ và ví mới (nếu đổi ví) vào `WalletRepository`; nếu phát sinh `StorageException` giữa quá trình lưu nhiều thực thể, khôi phục lại ảnh chụp trạng thái trước thao tác cho giao dịch và các ví liên quan.
- Ánh xạ kết quả sau cập nhật sang `TransactionResponseDTO`.

**Phương thức `deleteTransaction`:**

- Nạp bản ghi từ `TransactionRepository` theo mã định danh và xác nhận là `IncomeExpenseTransaction` chứ không phải bản ghi `TransactionFee`.
- Nạp ví liên kết đang `ACTIVE`; kiểm tra trước khả năng hoàn nguyên đối với giao dịch thu nhập để bảo đảm số dư ví sau khi trừ khoản thu và cộng trả khoản phí không bị âm, rồi gọi `revertFromWallet()`.
- Xóa bản ghi giao dịch khỏi `TransactionRepository` (xóa đồng thời `TransactionFee` hợp thành để ngăn bản ghi mồ côi) và lưu lại số dư ví đã hoàn nguyên vào `WalletRepository`; nếu thao tác lưu ví gặp `StorageException`, khôi phục lại bản ghi giao dịch và trạng thái ví ban đầu.
- Kết thúc xử lý mà không trả về dữ liệu.

## 3.4 Dịch vụ chuyển tiền nội bộ (TransferService)

Dịch vụ này điều phối việc luân chuyển dòng tiền giữa hai ví độc lập trong hệ thống, tra cứu lịch sử chuyển tiền theo bộ lọc và hủy hoàn tác giao dịch chuyển tiền khi cần thiết. Quá trình xử lý bảo đảm tổng tài sản giữa các ví luôn được bảo toàn sau khi trừ phần phí phát sinh thực tế, đồng thời duy trì số dư không âm tại cả hai đầu giao dịch.

**Giao diện công khai:**

Giao diện `com.pfms.service.TransferService` cung cấp các phương thức sau cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `transferFunds` | `TransferFundsRequestDTO request` | `TransferResponseDTO` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Kiểm tra hai ví phân biệt, đủ số dư chi trả chuyển khoản và phí; chuyển tiền nguyên tử và lưu giao dịch. |
| `filterTransfers` | `FilterTransfersRequestDTO request` | `TransferListResponseDTO` | `BusinessException`, `StorageException` | Tra cứu, lọc và phân trang lịch sử chuyển tiền theo khoảng ngày hoặc cặp ví nguồn/đích ở chế độ chỉ đọc. |
| `deleteTransfer` | `UUID transferId` | `void` | `EntityNotFoundException`, `BusinessException`, `InsufficientBalanceException`, `StorageException` | Kiểm tra ví đích đủ số dư thu hồi, hoàn nguyên số dư hai chiều cả hai ví và xóa bản ghi chuyển tiền cùng phí. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.transfer.TransferServiceImpl` nhận `TransactionRepository`, `WalletRepository` và `CategoryRepository` qua hàm khởi tạo. Mọi bản ghi chuyển tiền được lưu trữ đa hình trong `TransactionRepository` dưới dạng thực thể `TransferTransaction` kèm phần `TransactionFee` hợp thành gắn với `sourceWallet`.

**Phương thức `transferFunds`:**

- Kiểm tra mã ví nguồn khác mã ví đích, chuyển đổi số tiền chuyển và phí sang `Money` (số tiền lớn hơn `0.00 VNĐ`, phí không âm), chuyển đổi ngày chuyển sang `TransactionDate` (không vượt quá hiện tại) và chuẩn hóa ghi chú tối đa 255 ký tự; nếu mức phí dương thì bắt buộc kèm mã danh mục chi tiêu cấp 3.
- Nạp cả hai thực thể `Wallet` bảo đảm đều ở trạng thái `ACTIVE`, xác nhận ví nguồn đủ chi trả tổng tiền chuyển cộng phí thông qua `Money` và `hasSufficientBalance`, sau đó dùng `TransactionFactory` khởi tạo `TransferTransaction` (kèm `TransactionFee` nếu phí dương, với danh mục chi tiêu cấp 3 đang `ACTIVE`) và gọi `applyToWallet()` để trừ ví nguồn và cộng ví đích.
- Lưu đồng thời ví nguồn, ví đích qua `WalletRepository` và giao dịch mới qua `TransactionRepository`; nếu xảy ra `StorageException` tại bất kỳ lệnh lưu nào, thực hiện khôi phục ngay trạng thái trước giao dịch của cả hai ví và xóa bỏ bản ghi chưa hoàn tất để giữ nguyên tổng tài sản.
- Đóng gói thông tin giao dịch chuyển tiền và số dư mới của hai ví vào `TransferResponseDTO`.

**Phương thức `filterTransfers`:**

- Chuyển đổi mốc ngày bắt đầu và ngày kết thúc (nếu có) sang `TransactionDate` để kiểm tra ngày bắt đầu không đứng sau ngày kết thúc; kiểm tra chỉ số trang tối thiểu bằng 1 và kích thước trang trong đoạn từ 10 đến 100 bản ghi (mặc định 50 nếu chưa chỉ định).
- Nạp các thực thể thuộc kiểu `TransferTransaction` từ `TransactionRepository`, lọc theo khoảng ngày, mã ví nguồn và mã ví đích; sắp xếp kết quả theo ngày chuyển tiền giảm dần rồi đến mốc tạo giảm dần, đồng thời tra cứu tên ví nguồn và ví đích qua `WalletRepository` ở chế độ chỉ đọc.
- Thực thi phân trang trên tập kết quả và ủy quyền đối tượng giá trị `Money` tính tổng số tiền đã chuyển cùng tổng chi phí phát sinh trên toàn bộ tập kết quả thỏa mãn điều kiện lọc.
- Đóng gói danh sách giao dịch chuyển tiền của trang hiện tại, thông số phân trang và hai chỉ số tổng hợp vào `TransferListResponseDTO`.

**Phương thức `deleteTransfer`:**

- Nạp bản ghi theo mã định danh từ `TransactionRepository` và xác nhận đúng là thể hiện của `TransferTransaction`.
- Nạp ví nguồn và ví đích từ `WalletRepository` bảo đảm cả hai đang `ACTIVE`; kiểm tra số dư khả dụng của ví đích tối thiểu bằng số tiền gốc đã chuyển để tránh âm ví, sau đó gọi `revertFromWallet()` để trừ tiền ví đích và cộng trả tiền chuyển cùng khoản phí về ví nguồn.
- Xóa `TransferTransaction` cùng `TransactionFee` hợp thành khỏi `TransactionRepository` và lưu trạng thái mới của cả hai ví vào `WalletRepository`; nếu phát sinh `StorageException` trong chuỗi ghi dữ liệu, hoàn nguyên ảnh chụp trạng thái của cả hai ví và bản ghi giao dịch.
- Kết thúc xử lý mà không trả về dữ liệu.

## 3.5 Dịch vụ danh mục phân loại (CategoryService)

Dịch vụ Danh mục Phân loại đảm nhiệm điều phối các ca sử dụng quản lý hệ thống phân loại thu chi 3 cấp (Loại thu chi cố định, Nhóm nhu cầu và Danh mục chi tiết), kiểm soát tính hợp lệ và tính duy nhất của tên gọi theo quy tắc nghiệp vụ. Đồng thời, dịch vụ chịu trách nhiệm thực thi cơ chế xóa mềm và khôi phục mục phân loại nhằm bảo toàn tuyệt đối tính toàn vẹn tham chiếu lịch sử cho toàn bộ các giao dịch tài chính.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-04`, `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`, `ERR-CAT-004`, `ERR-CAT-005`

**Giao diện công khai:**

Giao diện `com.pfms.service.CategoryService` cung cấp các phương thức cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `createNeedGroup` | `CreateNeedGroupRequestDTO request` | `NeedGroupResponseDTO` | `BusinessException`<br/>`DuplicateEntityException`<br/>`StorageException` | Kiểm tra tên và tính duy nhất trong cùng loại thu chi (`INCOME`/`EXPENSE`); tạo `NeedGroup` mới (`ACTIVE`) kèm biểu tượng, mã màu. |
| `renameNeedGroup` | `RenameNeedGroupRequestDTO request` | `NeedGroupResponseDTO` | `EntityNotFoundException`<br/>`BusinessException`<br/>`DuplicateEntityException`<br/>`StorageException` | Xác thực nhóm đang `ACTIVE`, kiểm tra tên mới không trùng trong cùng loại thu chi; cập nhật tên, biểu tượng và mã màu. |
| `restoreNeedGroup` | `UUID needGroupId` | `NeedGroupResponseDTO` | `EntityNotFoundException`<br/>`BusinessException`<br/>`DuplicateEntityException`<br/>`StorageException` | Xác thực nhóm đang `DELETED`, kiểm tra tên không trùng với nhóm `ACTIVE` cùng loại; khôi phục nhóm về `ACTIVE`. |
| `deleteNeedGroup` | `UUID needGroupId` | `void` | `EntityNotFoundException`<br/>`BusinessException`<br/>`StorageException` | Từ chối nếu là nhóm duy nhất còn lại (`ERR-CAT-002`); xóa mềm danh mục con đã có giao dịch, xóa cứng mục con chưa có giao dịch và xóa nhóm cha tương ứng. |
| `createCategory` | `CreateCategoryRequestDTO request` | `CategoryResponseDTO` | `BusinessException`<br/>`EntityNotFoundException`<br/>`DuplicateEntityException`<br/>`StorageException` | Kiểm tra nhóm cha `ACTIVE`, tên hợp lệ và không trùng trong nhóm; tạo `Category` mới (`ACTIVE`) kế thừa loại thu chi từ nhóm cha. |
| `renameCategory` | `RenameCategoryRequestDTO request` | `CategoryResponseDTO` | `EntityNotFoundException`<br/>`BusinessException`<br/>`DuplicateEntityException`<br/>`StorageException` | Kiểm tra tên (1–50 ký tự), nhóm đích `ACTIVE` cùng loại; cập nhật tên hoặc chuyển nhóm mà không trùng lặp, bảo toàn lịch sử. |
| `restoreCategory` | `UUID categoryId` | `CategoryResponseDTO` | `EntityNotFoundException`<br/>`DuplicateEntityException`<br/>`BusinessException`<br/>`StorageException` | Kiểm tra nhóm cha đang `ACTIVE` và tên không trùng; khôi phục `Category` từ `DELETED` về `ACTIVE`. |
| `deleteCategory` | `UUID categoryId` | `void` | `EntityNotFoundException`<br/>`BusinessException`<br/>`StorageException` | Xác thực danh mục đang `ACTIVE`; xóa cứng nếu chưa phát sinh giao dịch, hoặc chuyển sang `DELETED` (xóa mềm) nếu đã có lịch sử. |
| `getCategoryHierarchy` | Không có | `List<CategoryHierarchyResponseDTO>` | Không có | Truy xuất toàn bộ cây danh mục phân loại 3 cấp (Loại thu chi -> Nhóm nhu cầu -> Danh mục chi tiết) gồm cả `ACTIVE` và `DELETED`. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.category.CategoryServiceImpl` nhận `CategoryRepository`, `NeedGroupRepository` và `TransactionRepository` qua hàm khởi tạo để xử lý logic:

**Phương thức `createNeedGroup`:**

- Kiểm tra loại thu chi hợp lệ (`INCOME` hoặc `EXPENSE`) và chuẩn hóa tên nhóm nhu cầu qua `EntityName`; nếu rỗng hoặc vượt ngưỡng độ dài cho phép thì ném `BusinessException`.
- Truy vấn `NeedGroupRepository.existsByNameAndType(...)` để từ chối trùng tên trong cùng loại thu chi bằng `DuplicateEntityException`.
- Khởi tạo `NeedGroup` mới ở trạng thái `ACTIVE` kèm biểu tượng, mã màu hiển thị, lưu vào `NeedGroupRepository` và ánh xạ sang `NeedGroupResponseDTO`.

**Phương thức `renameNeedGroup`:**

- Nạp nhóm nhu cầu từ `NeedGroupRepository` và bảo đảm nhóm đang ở trạng thái `ACTIVE`, kiểm tra tính hợp lệ và tính duy nhất của tên mới qua `existsByNameAndTypeAndIdNot(...)`.
- Ủy quyền thực thể `NeedGroup` cập nhật tên mới và biểu tượng, mã màu (nếu có yêu cầu cập nhật giao diện), lưu lại vào `NeedGroupRepository` và trả về `NeedGroupResponseDTO`.

**Phương thức `restoreNeedGroup`:**

- Nạp nhóm nhu cầu từ `NeedGroupRepository` và xác thực nhóm đang ở trạng thái `DELETED`; kiểm tra tên khôi phục không xung đột với nhóm nào đang `ACTIVE` trong cùng loại thu chi.
- Gọi `restore()` trên `NeedGroup`, lưu trạng thái mới và trả về `NeedGroupResponseDTO`.

**Phương thức `deleteNeedGroup`:**

- Nạp nhóm nhu cầu đang hoạt động và kiểm tra `NeedGroupRepository.countActiveByType(...)`; từ chối bằng `BusinessException` (`ERR-CAT-002`) nếu đây là nhóm hoạt động cuối cùng của nhánh thu chi tương ứng.
- Quét toàn bộ danh mục con của nhóm qua `CategoryRepository.findByNeedGroupId(...)`: với danh mục đã phát sinh giao dịch trong `TransactionRepository`, chuyển sang trạng thái `DELETED` (nếu còn hoạt động); với danh mục chưa từng phát sinh giao dịch, thực hiện xóa cứng. Nếu tồn tại danh mục con bị xóa mềm thì giữ nhóm cha ở trạng thái `DELETED`, ngược lại xóa cứng nhóm cha khỏi `NeedGroupRepository`.

**Phương thức `createCategory`:**

- Làm sạch và xác thực tên danh mục qua `EntityName` (ném `BusinessException` nếu vi phạm quy tắc đặt tên); nạp nhóm nhu cầu cha qua `NeedGroupRepository` (ném `EntityNotFoundException` nếu không tìm thấy, `BusinessException` mang `ERR-CAT-005` nếu nhóm cha đã xóa) và kiểm tra trùng tên qua `CategoryRepository.existsByNameAndNeedGroupId(...)` (`DuplicateEntityException`).
- Khởi tạo thực thể `Category` mới ở trạng thái `ACTIVE`, kế thừa loại thu chi cố định từ nhóm cha, lưu vào `CategoryRepository` và trả về `CategoryResponseDTO`.

**Phương thức `renameCategory`:**

- Kiểm tra tên mới qua `EntityName`, nạp danh mục đang hoạt động qua `CategoryRepository`; nếu có yêu cầu đổi nhóm nhu cầu cha thì kiểm tra nhóm đích tồn tại, đang `ACTIVE` (`ERR-CAT-003` nếu đã xóa) và cùng loại thu chi (`ERR-CAT-001` nếu khác loại).
- Kiểm tra không trùng tên trong nhóm đích qua `existsByNameAndNeedGroupIdAndIdNot(...)`, cập nhật nhóm cha (nếu đổi) và tên trên thực thể `Category`, lưu cập nhật và trả về `CategoryResponseDTO`.

**Phương thức `restoreCategory`:**

- Nạp danh mục đang ở trạng thái `DELETED` qua `CategoryRepository`; nạp nhóm nhu cầu cha và từ chối nếu nhóm cha đang ở trạng thái `DELETED` (`ERR-CAT-004`).
- Kiểm tra tên danh mục không trùng với danh mục đang `ACTIVE` trong cùng nhóm cha, gọi `restore(...)`, lưu cập nhật và trả về `CategoryResponseDTO`.

**Phương thức `deleteCategory`:**

- Nạp danh mục đang hoạt động từ `CategoryRepository` (từ chối nếu không tìm thấy hoặc đã bị xóa); kiểm tra lịch sử giao dịch qua `TransactionRepository.existsByCategoryId(...)`: nếu đã phát sinh giao dịch thì chuyển sang trạng thái `DELETED` (xóa mềm) để bảo toàn sổ cái và báo cáo lịch sử, ngược lại xóa cứng khỏi `CategoryRepository`.

**Phương thức `getCategoryHierarchy`:**

- Nạp toàn bộ danh sách nhóm nhu cầu từ `NeedGroupRepository` và danh mục từ `CategoryRepository` (bao gồm cả trạng thái `ACTIVE` và `DELETED`).
- Gom cụm danh mục chi tiết trực thuộc từng nhóm nhu cầu theo cấu trúc phân cấp và trả về danh sách `CategoryHierarchyResponseDTO`.

## 3.6 Dịch vụ báo cáo và thống kê tài chính (ReportService)

Dịch vụ Báo cáo và Thống kê Tài chính vận hành thuần túy ở chế độ chỉ đọc, chịu trách nhiệm truy xuất dữ liệu từ các kho lưu trữ để tổng hợp chuỗi thời gian dòng tiền 12 tháng liên tục, đo lường dòng tiền thuần và tỷ lệ tiết kiệm theo tháng và bình quân năm. Đồng thời, dịch vụ phân tích cơ cấu phân bổ thu chi 2 cấp theo nhóm nhu cầu và danh mục chi tiết trong kỳ xác định, bảo đảm độ chính xác số học tài chính mà không làm biến đổi bất kỳ trạng thái dữ liệu nào của hệ thống.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-01`, `UC-REP-02`
- Yêu cầu chức năng: `FR-REP-01`, `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi: `ERR-REP-001`, `ERR-FIN-001`, `ERR-FIN-002`

**Giao diện công khai:**

Giao diện `com.pfms.service.ReportService` cung cấp các phương thức cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `generateCashFlowReport` | `CashFlowReportRequestDTO request` | `CashFlowReportResponseDTO` | `BusinessException` | Thống kê chuỗi dòng tiền 12 tháng liên tục: tổng hợp thu/chi (kèm phí), tính dòng tiền thuần (`NetCashFlow`), tỷ lệ tiết kiệm (`SavingsRate`) và các chỉ số bình quân năm. |
| `generateSpendingStructureReport` | `SpendingStructureReportRequestDTO request` | `SpendingStructureReportResponseDTO` | `BusinessException` | Phân tích cơ cấu 2 cấp theo nhóm nhu cầu và danh mục trong khoảng ngày: tính tỷ trọng (`SpendingStructureShare`), sắp xếp giảm dần và so sánh biến động kỳ liền kề. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.report.ReportServiceImpl` nhận `TransactionRepository`, `CategoryRepository` và `NeedGroupRepository` qua hàm khởi tạo để xử lý logic:

Dịch vụ vận hành hoàn toàn ở chế độ chỉ đọc (Read-only), tuyệt đối không làm thay đổi số dư ví, không chỉnh sửa bản ghi tài chính và không thực hiện thao tác ghi hay sửa đổi dữ liệu lưu trữ. Quy trình 4 bước được chuẩn hóa phù hợp với bản chất trích xuất số liệu thống kê như sau:

- **Quy trình thực thi phương thức `generateCashFlowReport(CashFlowReportRequestDTO request)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra `request` khác rỗng cùng tính hợp lệ của mốc thời gian kết thúc (`endMonth` nằm trong đoạn từ 1 đến 12, `endYear` từ năm 2000 trở đi; ở tầng Trình diễn có thể dùng constructor mặc định của `CashFlowReportRequestDTO` lấy tháng và năm hiện tại). Nếu không hợp lệ, ném `BusinessException` mang mã lỗi `ERR-REP-001`. Cho phép truy vấn tháng và năm tương lai ở chế độ chỉ đọc (trả về số liệu `0.00 VNĐ` và tỷ lệ `0.00%`). Xác định khung thời gian thống kê gồm đúng 12 tháng liên tục lùi tính đến mốc `endMonth/endYear`.
  2. *Bước 2 - Nạp dữ liệu chỉ đọc từ kho lưu trữ:* Gọi `TransactionRepository.findByDateRange(startDate, endDate)` để nạp toàn bộ các giao dịch thuộc lớp cha `Transaction` trong khung 12 tháng gồm giao dịch thu chi (`IncomeExpenseTransaction`) và giao dịch chuyển tiền nội bộ (`TransferTransaction`) mà không làm biến đổi dữ liệu nguồn. Tiền gốc chuyển ví nội bộ bị loại trừ khỏi dòng thu và chi (`BR-FIN-03`), trong khi mọi khoản `TransactionFee` liên kết từ cả hai lớp con được thu nhận theo ngày của giao dịch gốc.
  3. *Bước 3 - Tổng hợp số liệu và ủy quyền tính toán số học tài chính:*
     - Gom cụm các giao dịch theo từng tháng trong chuỗi 12 tháng liên tục.
     - Với mỗi tháng, tính tổng thu nhập (tổng số tiền giao dịch `INCOME`) và tổng chi tiêu (tổng số tiền giao dịch `EXPENSE` cộng dồn toàn bộ các khoản phí giao dịch liên kết đang tồn tại từ giao dịch thu chi và chuyển tiền nội bộ).
     - Ủy quyền lớp giá trị `NetCashFlow.of(...)` tính dòng tiền thuần từng tháng bằng hiệu số giữa tổng thu nhập và tổng chi tiêu theo quy tắc độ chính xác số học tài chính.
     - Ủy quyền lớp giá trị `SavingsRate.of(...)` tính tỷ lệ tiết kiệm từng tháng: trường hợp tổng thu nhập lớn hơn 0, tính bằng công thức (Dòng tiền thuần / Tổng thu nhập) × 100% với bước trung gian tối thiểu 4 chữ số thập phân và làm tròn ngân hàng hai chữ số thập phân; trường hợp tổng thu nhập nhỏ hơn hoặc bằng 0, gán tỷ lệ tiết kiệm mặc định là 0.00% nhằm phòng ngừa triệt để lỗi chia cho không.
     - Tính các chỉ số bình quân cả năm: lấy tổng thu nhập, tổng chi tiêu và tổng dòng tiền thuần của cả 12 tháng chia đều cho mẫu số cố định là 12 theo quy tắc làm tròn ngân hàng hai chữ số thập phân, trong đó các tháng không phát sinh giao dịch được tính bằng 0.00 VNĐ.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói bảng số liệu 12 tháng (`MonthlyCashFlowResponseDTO`) cùng các chỉ số bình quân vào đối tượng `CashFlowReportResponseDTO` bất biến và chuyển giao cho tầng trình diễn kết xuất bảng và biểu đồ trên giao diện đồ họa.

- **Quy trình thực thi phương thức `generateSpendingStructureReport(SpendingStructureReportRequestDTO request)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Kiểm tra `request` khác rỗng, ngày bắt đầu và ngày kết thúc khác rỗng, ngày bắt đầu không vượt quá ngày kết thúc, và `transactionType` thuộc tập `EXPENSE` hoặc `INCOME` (constructor của `SpendingStructureReportRequestDTO` ở tầng Trình diễn mặc định quy chuẩn giá trị null/rỗng thành `EXPENSE`, còn chuỗi khác lạ bị từ chối tại dịch vụ). Nếu vi phạm bất kỳ điều kiện nào, ném `BusinessException` mang mã lỗi `ERR-REP-001`. Cho phép truy vấn khoảng ngày tương lai ở chế độ chỉ đọc.
  2. *Bước 2 - Nạp dữ liệu chỉ đọc từ kho lưu trữ:* Gọi `TransactionRepository.findByDateRange(startDate, endDate)` ở chế độ chỉ đọc để không bỏ sót khoản phí giao dịch gắn trên giao dịch `INCOME` hoặc `TransferTransaction` khi phân tích `EXPENSE`; đồng thời nạp đầy đủ danh sách nhóm nhu cầu từ `NeedGroupRepository.findAll()` và danh mục từ `CategoryRepository.findAll()`, bao gồm cả các nhóm nhu cầu và danh mục chi tiết đã bị xóa mềm (`DELETED`).
  3. *Bước 3 - Phân tích cơ cấu và ủy quyền tính toán số học tài chính:*
     - Lọc các giao dịch phù hợp (nếu `EXPENSE` lấy các `IncomeExpenseTransaction` loại `EXPENSE` cộng dồn mọi `TransactionFee` tồn tại; nếu `INCOME` chỉ lấy `IncomeExpenseTransaction` loại `INCOME`) rồi gom cụm theo danh mục cấp 3 (`Category`) và nhóm nhu cầu cha hiện tại cấp 2 (`NeedGroup`). Chỉ đưa vào danh sách kết quả các nhóm và danh mục có tổng số tiền kỳ hiện tại lớn hơn 0; với mục đã xóa mềm, giữ nguyên tên gốc và truyền trạng thái `"DELETED"` qua DTO để tầng trình diễn gắn nhãn `"(đã xóa)"`.
     - Tính tổng số tiền của toàn bộ kỳ phân tích và tổng tiền của từng nhóm, từng danh mục theo quy tắc độ chính xác tài chính (`BR-FIN-01`).
     - Ủy quyền lớp giá trị `SpendingStructureShare` tính tỷ trọng phần trăm với bước trung gian tối thiểu 4 chữ số thập phân và làm tròn ngân hàng 2 chữ số thập phân: phạm vi `"NeedGroup"` tính tỷ trọng nhóm trên tổng toàn kỳ; phạm vi `"Category"` tính đồng thời tỷ trọng danh mục trên tổng toàn kỳ (`share`) và trên tổng nhóm cha (`shareInGroup`). Nếu tổng tiền toàn kỳ bằng 0.00 VNĐ, gán tỷ trọng mặc định bằng 0.00% và trả về danh sách nhóm nhu cầu rỗng.
     - Sắp xếp các nhóm nhu cầu và danh mục con theo thứ tự tổng số tiền giảm dần. Đồng thời truy vấn kỳ liền kề có cùng độ dài ngày (`days = ChronoUnit.DAYS.between(startDate, endDate) + 1`, khoảng `[startDate.minusDays(days), startDate.minusDays(1)]`) qua `TransactionRepository.findByDateRange(...)` để tính chênh lệch tuyệt đối và tỷ lệ biến động cho tổng toàn kỳ cùng từng nhóm, từng danh mục hiển thị: nếu giá trị kỳ trước lớn hơn 0 thì tính tỷ lệ theo công thức ((Kỳ này - Kỳ trước) / Kỳ trước) × 100%; nếu giá trị kỳ này lớn hơn 0 và kỳ trước bằng 0.00 VNĐ thì đánh dấu mục mới (`isNew = true`) để giao diện hiển thị `"Mới"`; nếu cả tổng toàn kỳ hiện tại và kỳ trước đều bằng 0.00 VNĐ thì gán chênh lệch `0.00 VNĐ`, tỷ lệ `0.00%` và `isNew = false`.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói bảng phân tích cơ cấu 2 cấp (`NeedGroupShareResponseDTO`, `CategoryShareResponseDTO`), tỷ trọng phần trăm và số liệu biến động so sánh vào đối tượng `SpendingStructureReportResponseDTO` bất biến và trả về cho tầng trình diễn.

## 3.7 Dịch vụ quản trị và sao lưu dữ liệu (DataManagementService)

Dịch vụ Quản trị và Sao lưu Dữ liệu chịu trách nhiệm điều phối việc trích xuất ảnh chụp trạng thái tài chính đồng bộ ra tệp sao lưu JSON chuẩn hóa, phục hồi dữ liệu hệ thống qua quy trình kiểm định toàn vẹn 4 bước nghiêm ngặt theo nguyên tắc tất cả hoặc không, và thực hiện đối soát độc lập 4 điều kiện toàn vẹn sổ cái nhằm bảo đảm tính nhất quán tài chính tuyệt đối.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-DAT-01`, `UC-DAT-02`, `UC-DAT-03`
- Yêu cầu chức năng: `FR-DAT-01`, `FR-DAT-02`, `FR-DAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-DAT-001`, `ERR-DAT-002`

**Giao diện công khai:**

Giao diện `com.pfms.service.DataManagementService` cung cấp các phương thức cho tầng trình diễn triệu gọi:

| Phương thức | Tham số đầu vào | Kiểu dữ liệu trả về | Ngoại lệ ném ra | Mô tả ca sử dụng điều phối |
| :--- | :--- | :--- | :--- | :--- |
| `exportBackupData` | `ExportBackupRequestDTO request` | `ExportBackupResponseDTO` | `StorageException`<br/>`BusinessException` | Trích xuất ảnh chụp dữ liệu tài chính (`FULL` hoặc `TRANSACTIONS`) ra tệp JSON UTF-8 an toàn với định dạng `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json`. |
| `restoreBackupData` | `RestoreBackupRequestDTO request` | `RestoreBackupResponseDTO` | `StorageException`<br/>`DataCorruptionException` | Phục hồi dữ liệu từ tệp JSON qua quy trình kiểm định toàn vẹn nghiêm ngặt 4 bước (All-or-Nothing) kèm cơ chế hoàn nguyên `RollbackSupport`. |
| `reconcileSystemData` | Không có | `ReconciliationReportResponseDTO` | Không có | Đối soát độc lập 4 điều kiện toàn vẹn ở chế độ chỉ đọc (bảo toàn số dư ví, cân bằng chuyển tiền, liên kết danh mục, phí không mồ côi); phân loại cảnh báo và đề xuất xử lý. |

**Cài đặt dịch vụ:**

Lớp cài đặt công khai `com.pfms.service.impl.data.DataManagementServiceImpl` nhận `WalletRepository`, `TransactionRepository`, `CategoryRepository`, `NeedGroupRepository`, `BalanceAdjustmentRepository` và `BackupRepository` qua hàm khởi tạo để xử lý logic (trong đó `TransactionRepository` lưu trữ đa hình cả giao dịch thu chi `IncomeExpenseTransaction` và chuyển tiền nội bộ `TransferTransaction`, còn `BackupRepository` cung cấp hợp đồng trừu tượng thao tác tệp sao lưu tuân thủ nguyên lý nghịch đảo phụ thuộc (DIP)):

- **Quy trình thực thi phương thức `exportBackupData(ExportBackupRequestDTO request)`:**
  1. *Bước 1 - Kiểm tra tiền điều kiện định dạng và quy tắc nghiệp vụ:* Xác thực phạm vi xuất dữ liệu hợp lệ (`FULL` cho toàn bộ hệ thống hoặc `TRANSACTIONS` cho riêng sổ giao dịch, ném `BusinessException` mang `ERR-DAT-001` nếu không hợp lệ). Ủy quyền `BackupRepository.resolveWritableTargetPath` kiểm tra `destinationPath` bắt buộc là đường dẫn thư mục hợp lệ (từ chối `null`/rỗng, tệp đã tồn tại hoặc đường dẫn mang phần mở rộng tệp bằng `StorageException`) và ghép tên tệp tự động theo mẫu `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json` dùng chung mốc `exportedAt` với `ExportBackupResponseDTO`.
  2. *Bước 2 - Nạp dữ liệu ảnh chụp nhất quán ở chế độ chỉ đọc:* Lần lượt gọi phương thức `findAll()` trên các kho lưu trữ (`WalletRepository`, `TransactionRepository`, `CategoryRepository`, `NeedGroupRepository`, `BalanceAdjustmentRepository`) để nạp ảnh chụp dữ liệu đồng bộ tại cùng một mốc thời gian, bảo đảm không làm biến đổi bất kỳ giá trị nào trong hệ thống.
  3. *Bước 3 - Chuẩn hóa dữ liệu, tuần tự hóa JSON và lưu trữ an toàn:*
     - Chuyển đổi trạng thái thực thể sang cấu trúc đóng gói bất biến `BackupPayload` thuộc gói `com.pfms.domain.model.data`, giữ nguyên mốc thời gian `createdAt` của các thực thể.
     - Định dạng mọi trường số tiền sang dạng chuỗi số học chuẩn xác cố định 2 chữ số thập phân theo quy tắc độ chính xác số học tài chính, tuyệt đối không sử dụng số thực dấu phẩy động nhị phân.
     - Chuyển giao `BackupPayload` cho phương thức `BackupRepository.writeBackup(targetPath, payload)`. Tại tầng hạ tầng, lớp cài đặt `FileBackupRepository` thực hiện tuần tự hóa cấu trúc dữ liệu qua `JsonEntitySerializer` sang định dạng JSON UTF-8 không BOM và thực thi cơ chế ghi tệp an toàn qua `FileStorageSupport` (tự khởi tạo thư mục đích nếu chưa tồn tại, ghi vào tệp `.tmp` trước khi đổi tên thay thế nguyên tử và dọn tệp tạm khi gặp lỗi) để tránh tạo tệp hỏng nếu quá trình ghi bị ngắt quãng giữa chừng.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thông tin kết quả xuất sao lưu (đường dẫn tệp đầy đủ vừa tạo, kích thước tệp, mốc thời gian xuất đồng bộ và số lượng bản ghi của từng thực thể) vào `ExportBackupResponseDTO` và trả về cho tầng trình diễn.

- **Quy trình thực thi phương thức `restoreBackupData(RestoreBackupRequestDTO request)`:**
  1. *Bước 1 - Nạp và phân tích dữ liệu sao lưu qua kho lưu trữ:* Gọi phương thức `BackupRepository.readBackup(request.getBackupFilePath())`. Tại tầng hạ tầng, `FileBackupRepository` phối hợp với `FileStorageSupport` để kiểm tra tệp sao lưu bắt buộc phải tồn tại, là tệp thường và có quyền đọc, đọc nội dung UTF-8 (loại bỏ tiền tố BOM nếu có); sau đó phân tích cú pháp JSON qua `JsonEntityDeserializer` (tái cấu thành thực thể lịch sử qua `reconstitute` nhằm bảo toàn dữ liệu `ARCHIVED`/`DELETED` và giữ nguyên `createdAt`). Nếu tệp không đọc được hoặc sai cấu trúc cú pháp JSON, hệ thống ném `StorageException` hoặc `DataCorruptionException` (`ERR-DAT-001`).
  2. *Bước 2 - Thực thi quy trình kiểm định toàn vẹn dữ liệu nghiêm ngặt (All-or-Nothing):*
     - *Kiểm tra cấu trúc và số liệu:* Xác thực các trường bắt buộc của các thực thể phải hiện diện đầy đủ, ngày tháng đúng chuẩn `YYYY-MM-DD`, số tiền đúng định dạng số học chuẩn xác và không trùng lặp mã định danh giữa các bản ghi cùng loại trong payload.
     - *Kiểm tra toàn vẹn danh mục theo quy tắc phân cấp danh mục:* Kiểm tra cấu trúc phân cấp, bảo đảm mọi danh mục chi tiết đều trực thuộc nhóm nhu cầu cha hợp lệ, không có danh mục mồ côi và Loại thu chi của danh mục tương thích với giao dịch liên kết.
     - *Kiểm tra toàn vẹn ví, chuyển tiền và phí giao dịch:* Xác nhận mọi giao dịch thu chi, chuyển tiền, bản ghi điều chỉnh số dư đều trỏ tới ví có mặt trong danh sách ví của payload (từ chối các tệp xuất chỉ chứa sổ giao dịch), bảo đảm cân bằng nguồn-đích khi chuyển tiền và mọi khoản `TransactionFee` đều gắn với giao dịch hợp lệ cùng danh mục chi tiêu tồn tại.
     - *Kiểm tra bảo toàn số dư không âm:* Ủy quyền `SystemDataReconciler` tái tính toán số dư độc lập cho các ví từ lịch sử dòng tiền và điều chỉnh số dư, bảo đảm không có ví nào âm số dư hoặc lệch số dư với dòng tiền (ném `DataCorruptionException` mang `ERR-DAT-002` nếu có bất kỳ sai phạm nào).
     - *Cơ chế hủy bỏ bảo vệ:* Nếu phát hiện bất kỳ sai lệch nào tại bất kỳ bước kiểm tra nào, hệ thống lập tức hủy bỏ toàn bộ quá trình phục hồi, giữ nguyên vẹn dữ liệu hiện tại của hệ thống.
  3. *Bước 3 - Thay thế dữ liệu nguyên tử trên các kho lưu trữ:* Khi và chỉ khi tệp sao lưu vượt qua kiểm định toàn vẹn, dịch vụ chụp bản sao danh sách dữ liệu hiện hành từ `WalletRepository`, `NeedGroupRepository`, `CategoryRepository`, `BalanceAdjustmentRepository` và `TransactionRepository` rồi thực hiện xóa dữ liệu cũ theo thứ tự phụ thuộc ngược và nạp toàn bộ thực thể phục hồi theo thứ tự phụ thuộc cha-trước. Nếu phát sinh lỗi ở bước xóa hoặc ghi, dịch vụ dùng `RollbackSupport` dọn phần ghi dở và khôi phục nguyên trạng dữ liệu chụp trước khi chuyển tiếp `StorageException`.
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Đóng gói thông báo phục hồi thành công kèm số lượng thực thể đã phục hồi vào `RestoreBackupResponseDTO` và chuyển giao cho tầng trình diễn.

- **Quy trình thực thi phương thức `reconcileSystemData()`:**
  1. *Bước 1 - Khởi tạo tiến trình đối soát chỉ đọc:* Tiếp nhận yêu cầu đối soát toàn vẹn dữ liệu từ người dùng hoặc theo lịch định kỳ; thiết lập phiên làm việc thuần túy ở chế độ chỉ đọc, không làm biến đổi bất kỳ bản ghi tài chính nào.
  2. *Bước 2 - Nạp dữ liệu đồng thời từ các kho lưu trữ:* Nạp toàn bộ danh sách ví, giao dịch thu chi, chuyển tiền nội bộ, bản ghi kiểm kê số dư và danh mục phân loại từ các kho lưu trữ tương ứng; các khoản chi phí phát sinh (`TransactionFee`) được trích xuất trực tiếp từ các giao dịch cha chứa phí theo quan hệ hợp thành mà không cần kho lưu trữ phí độc lập.
  3. *Bước 3 - Quét và đối soát độc lập 4 điều kiện toàn vẹn tài chính:*
     - *Điều kiện 1 (Bảo toàn số dư ví theo quy tắc bảo toàn số dư ví):* Tính toán lại số dư độc lập từ toàn bộ lịch sử dòng tiền (gồm các khoản thu, chi, chuyển tiền đi/đến, chi phí và mốc kiểm kê điều chỉnh số dư) cho từng ví; so khớp với số dư hiện hành (`currentBalance`) đang ghi nhận trên ví. Sai số cho phép bằng đúng 0.00 VNĐ theo quy tắc độ chính xác số học tài chính.
     - *Điều kiện 2 (Cân bằng nguồn và đích khi chuyển tiền theo quy tắc chuyển tiền nội bộ):* Quét toàn bộ các giao dịch chuyển tiền nội bộ, đối soát số tiền bị trừ tại ví nguồn phải bằng chính xác số tiền được cộng tại ví đích (không tính khoản chi phí phát sinh).
     - *Điều kiện 3 (Tính toàn vẹn danh mục theo quy tắc cấu trúc danh mục ba cấp):* Quét toàn bộ sổ giao dịch thu chi, bảo đảm mọi giao dịch đều gắn với danh mục chi tiết hợp lệ cấp 3 (bao gồm cả danh mục đã xóa mềm) và không có giao dịch nào bị sai lệch Loại thu chi (`INCOME`/`EXPENSE`).
     - *Điều kiện 4 (Toàn vẹn liên kết phí giao dịch):* Quét toàn bộ các khoản chi phí phát sinh gắn kèm giao dịch thu chi và chuyển tiền, bảo đảm mọi bản ghi phí đều thuộc về một giao dịch hợp lệ đang tồn tại, không có phí mồ côi.
     - Phân loại kết quả kiểm tra theo các mức độ nghiêm trọng (`INFO`, `WARNING`, `ERROR`) và tổng hợp đề xuất hành động khắc phục cụ thể (như tự động tính lại số dư hoặc khuyến nghị phục hồi từ bản sao lưu).
  4. *Bước 4 - Đóng gói kết quả sang DTO phản hồi:* Tổng hợp trạng thái kiểm tra của cả 4 điều kiện (Đạt hoặc Không đạt), số lượng bản ghi đã quét, danh sách chi tiết các sai lệch phát hiện và hành động đề xuất vào đối tượng `ReconciliationReportResponseDTO` và trả về tầng trình diễn để kết xuất báo cáo đối soát.

\newpage

# 04. Thiết kế tầng lưu trữ và truy xuất dữ liệu

Tầng lưu trữ quản lý vòng đời truy xuất và lưu giữ dữ liệu của hệ thống, cung cấp lớp trừu tượng hóa mô phỏng tập hợp đối tượng nghiệp vụ trong bộ nhớ. Toàn bộ giao diện kho lưu trữ được định nghĩa trong gói `com.pfms.domain.repository` thuộc tầng nghiệp vụ, tuân thủ nguyên lý nghịch đảo phụ thuộc (DIP). Tầng dịch vụ chỉ tương tác với các giao diện trừu tượng này mà không phụ thuộc vào công nghệ lưu trữ cụ thể.

Các lớp cài đặt kho lưu trữ được đặt tại tầng hạ tầng gồm: lưu trữ tệp tin (`com.pfms.infrastructure.file`) phục vụ vận hành thực tế và lưu trữ bộ nhớ (`com.pfms.infrastructure.memory`) phục vụ kiểm thử tự động. Cơ chế ánh xạ hai chiều chuyển đổi giữa thực thể miền và bản ghi JSON phẳng, đảm bảo chi tiết định dạng tệp không rò rỉ vào mô hình nghiệp vụ.

## 4.1 Giao diện lưu trữ cơ sở (GenericRepository)

Giao diện lưu trữ cơ sở định hình khung thao tác dữ liệu chuẩn hóa cho toàn bộ thực thể trong hệ thống, loại bỏ trùng lặp mã nguồn và thiết lập chuẩn tương tác đồng nhất giữa tầng dịch vụ và tầng hạ tầng. Được định vị tại `com.pfms.domain.repository`, giao diện sử dụng generic thuần Java SE (`<E, ID>`) độc lập hoàn toàn khỏi thư viện hay framework bên ngoài, áp dụng quy chuẩn nghiêm ngặt về đối tượng bọc (`Optional`) chống lỗi tham chiếu rỗng và cơ chế đóng gói ngoại lệ an toàn.

### 4.1.1 Thao tác dữ liệu dùng chung

Giao diện `GenericRepository<E, ID>` định nghĩa tập hợp các thao tác dữ liệu cơ sở cho mọi thực thể nghiệp vụ, thiết lập chuẩn tương tác đồng nhất giữa tầng dịch vụ và tầng hạ tầng.

**Đặc tả phương thức cơ sở**

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `save` | `E entity` | `E` | Lưu thực thể vào kho lưu trữ; cập nhật trạng thái nếu mã định danh đã tồn tại hoặc tạo mới nếu chưa có, trả về thực thể sau khi lưu. |
| `saveAll` | `Iterable<E> entities` | `List<E>` | Lưu tập hợp thực thể vào kho lưu trữ; trả về danh sách các thực thể sau khi lưu (sử dụng phương thức mặc định lặp qua từng phần tử). |
| `findById` | `ID id` | `Optional<E>` | Truy vấn thực thể theo mã định danh duy nhất; trả về đối tượng bọc nếu tìm thấy hoặc đối tượng rỗng nếu không tồn tại. |
| `findAll` | Không | `List<E>` | Lấy toàn bộ danh sách thực thể trong kho lưu trữ; trả về danh sách rỗng nếu chưa có dữ liệu, không bao giờ trả về giá trị rỗng. |
| `existsById` | `ID id` | `boolean` | Kiểm tra sự tồn tại của thực thể theo mã định danh mà không cần nạp toàn bộ thuộc tính của đối tượng vào bộ nhớ máy ảo. |
| `deleteById` | `ID id` | `boolean` | Xóa bản ghi tương ứng với mã định danh khỏi kho lưu trữ; trả về kết quả đúng nếu xóa thành công, trả về sai nếu không tìm thấy. |
| `deleteAll` | Không | `void` | Xóa toàn bộ các bản ghi khỏi kho lưu trữ phục vụ việc phục hồi dữ liệu hệ thống từ bản sao lưu hoặc thiết lập lại trạng thái. |

**Quy tắc thiết kế giao diện**

- Ràng buộc tham số kiểu: Tham số `E` đại diện cho thực thể nghiệp vụ; tham số `ID` đại diện cho khóa chính bất biến (`UUID`).
- Kiểm tra hợp lệ đối số: Mọi phương thức từ chối tham số đầu vào rỗng (`null`) và ném `IllegalArgumentException` nếu vi phạm.

### 4.1.2 Quy chuẩn trả về và xử lý lỗi

Tầng lưu trữ thiết lập hai quy chuẩn bắt buộc nhằm bảo vệ tính toàn vẹn dữ liệu và duy trì ranh giới phân tầng độc lập: quy chuẩn kiểu dữ liệu trả về chống lỗi tham chiếu rỗng và cơ chế chuyển đổi ngoại lệ kỹ thuật.

**Quy chuẩn kiểu dữ liệu trả về**

| Tình huống truy vấn | Kiểu trả về | Quy tắc xử lý |
| :--- | :--- | :--- |
| Tìm kiếm đơn lẻ theo định danh hoặc thuộc tính | `Optional<E>` | Trả về đối tượng bọc chứa thực thể nếu tìm thấy; trả về `Optional.empty()` khi không tồn tại bản ghi. Tuyệt đối không trả về giá trị rỗng (`null`) và không ném ngoại lệ trong trường hợp dữ liệu hợp lệ nhưng chưa tồn tại. |
| Truy vấn tập hợp hoặc lọc danh sách | `List<E>` | Trả về danh sách chứa các thực thể thỏa mãn điều kiện; trả về danh sách rỗng bất biến khi không có kết quả phù hợp. Nghiêm cấm trả về `null` thay cho danh sách rỗng. |

**Cơ chế đóng gói ngoại lệ lưu trữ**

Các lớp cài đặt tại tầng hạ tầng khi thao tác với hệ thống tệp có thể phát sinh các ngoại lệ kỹ thuật cấp thấp như lỗi vào ra dữ liệu (`IOException`), lỗi không tìm thấy tệp hoặc sai lệch định dạng bản ghi. Để ngăn chặn việc rò rỉ các chi tiết công nghệ tệp lên tầng dịch vụ, toàn bộ các lỗi này được đóng gói theo nguyên tắc chuyển đổi ngoại lệ:

- Định nghĩa lớp ngoại lệ chung: Hệ thống xây dựng lớp `StorageException` kế thừa từ `RuntimeException`, đặt tại gói `com.pfms.domain.exception`. Đây là ngoại lệ không bắt buộc khai báo, đại diện cho mọi sự cố hỏng hóc hoặc tắc nghẽn tại tầng vật lý.
- Đóng gói nguyên nhân gốc: Khi tầng hạ tầng bắt được ngoại lệ kỹ thuật cấp thấp, lớp cài đặt phải khởi tạo `StorageException` kèm thông điệp mô tả ngữ cảnh thao tác và truyền đối tượng lỗi ban đầu vào tham số nguyên nhân gốc (`cause`) để phục vụ việc ghi nhật ký kiểm toán.
- Ranh giới trách nhiệm: tầng dịch vụ chỉ tương tác và bắt ngoại lệ `StorageException`, hoàn toàn không tham chiếu đến các lớp ngoại lệ vào ra nguyên thủy của hệ điều hành.

## 4.2 Giao diện lưu trữ theo thực thể

Các giao diện lưu trữ chuyên biệt kế thừa từ `GenericRepository<E, ID>` và bổ sung các phương thức truy vấn nghiệp vụ phù hợp với đặc thù của từng thực thể. Toàn bộ các giao diện này nằm trong gói `com.pfms.domain.repository`, phục vụ trực tiếp cho tầng dịch vụ điều phối các ca sử dụng nghiệp vụ mà không làm lộ chi tiết công nghệ lưu trữ.

### 4.2.1 Kho lưu trữ ví tiền (WalletRepository)

Giao diện `WalletRepository` kế thừa `GenericRepository<Wallet, UUID>` để quản lý vòng đời lưu trữ của các nguồn tiền trong hệ thống. Kho lưu trữ cung cấp các phương thức truy vấn mở rộng giúp `WalletService` kiểm tra ví mặc định, xác thực tính duy nhất của tên ví, lọc danh sách theo trạng thái và thống kê số lượng ví phục vụ phân tích cơ cấu tài sản.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`, `UC-WAL-02`, `UC-WAL-03`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-WAL-02`, `FR-WAL-03`, `FR-WAL-04`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-WAL-001`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.WalletRepository` kế thừa `GenericRepository<Wallet, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `findByName` | `String name` | `Optional<Wallet>` | Tìm ví theo tên chuẩn hóa (không phân biệt hoa thường); trả về `Optional.empty()` nếu chưa tồn tại. |
| `existsByName` | `String name` | `boolean` | Kiểm tra sự tồn tại của tên ví (không phân biệt hoa thường) trên cả ví `ACTIVE` lẫn `ARCHIVED`. |
| `findByStatus` | `EntityStatus status` | `List<Wallet>` | Lọc danh sách ví theo trạng thái vòng đời (`ACTIVE` hoặc `ARCHIVED`). |
| `countByStatus` | `EntityStatus status` | `long` | Đếm số lượng ví theo trạng thái xác định (`ACTIVE` hoặc `ARCHIVED`). |
| `findDefaultWallet` | `Không` | `Optional<Wallet>` | Truy vấn ví tiền mặt mặc định (`isDefault = true`); trả về `Optional.empty()` nếu chưa khởi tạo. |

**Chỉ dẫn thiết kế và cài đặt:**

- 5 phương thức truy vấn mở rộng đáp ứng nhu cầu kiểm tra ví mặc định, tra cứu theo tên không phân biệt hoa thường và lọc trạng thái hoạt động của `WalletService`.
- Kế thừa trực tiếp 5 phương thức cơ sở (`save`, `findById`, `findAll`, `existsById`, `deleteById`) từ `GenericRepository<Wallet, UUID>` mà không khai báo lặp lại; thao tác `deleteById` chỉ được thực thi sau khi tầng dịch vụ xác nhận ví cần xóa không phải ví mặc định và chưa từng phát sinh lịch sử giao dịch hay kiểm kê.
- Chuẩn hóa chuỗi đầu vào bằng cách cắt khoảng trắng hai đầu và so khớp tên ví không phân biệt hoa thường trên cả ví `ACTIVE` lẫn `ARCHIVED`; giải phóng tên ví khỏi phạm vi kiểm tra duy nhất khi xóa vĩnh viễn thành công và khóa thao tác xóa hoặc lưu trữ đối với ví `"Tiền mặt"` mặc định nhằm duy trì tối thiểu một ví `ACTIVE`.

### 4.2.2 Kho lưu trữ giao dịch (TransactionRepository)

Giao diện `TransactionRepository` kế thừa `GenericRepository<Transaction, UUID>` để lưu trữ đa hình toàn bộ biến động dòng tiền thu chi và chuyển tiền nội bộ. Kho lưu trữ cung cấp các phép lọc lịch sử theo ví, khoảng thời gian, danh mục và hàm tổng hợp số liệu chỉ đọc cho `TransactionService`, `TransferService`, `CategoryService` và `ReportService`.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`, `UC-TXN-02`, `UC-TXN-03`, `UC-TXN-04`, `UC-TRF-01`, `UC-TRF-02`, `UC-TRF-03`, `UC-REP-01`, `UC-REP-02`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`, `FR-TXN-03`, `FR-TXN-04`, `FR-TXN-05`, `FR-TRF-01`, `FR-TRF-02`, `FR-REP-01`, `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`, `ERR-CAT-001`, `ERR-TRF-001`, `ERR-TRF-002`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.TransactionRepository` kế thừa `GenericRepository<Transaction, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `findByWalletId` | `UUID walletId` | `List<Transaction>` | Truy vấn toàn bộ giao dịch thu chi, chuyển tiền (nguồn/đích) hoặc trích phí gắn với ví. |
| `findByDateRange` | `LocalDate startDate, LocalDate endDate` | `List<Transaction>` | Lọc danh sách giao dịch trong khoảng ngày `[startDate, endDate]` (chế độ chỉ đọc). |
| `findByDateRangeAndType` | `LocalDate startDate, LocalDate endDate, TransactionType type` | `List<Transaction>` | Lọc giao dịch thu chi (`IncomeExpenseTransaction`) theo khoảng ngày và loại thu chi (`INCOME`/`EXPENSE`). |
| `findByCategoryId` | `UUID categoryId` | `List<Transaction>` | Truy vấn giao dịch thu chi hoặc giao dịch có phụ phí gắn với danh mục cấp 3. |
| `existsByCategoryId` | `UUID categoryId` | `boolean` | Kiểm tra sự tồn tại của giao dịch hoặc phụ phí tham chiếu tới danh mục cấp 3. |
| `sumAmountByFilter` | `LocalDate startDate, LocalDate endDate, TransactionType type` | `BigDecimal` | Tính tổng số tiền gốc các giao dịch `IncomeExpenseTransaction` theo khoảng ngày và loại thu chi. |

**Chỉ dẫn thiết kế và cài đặt:**

- Nhóm phương thức truy vấn mở rộng vận hành ở chế độ chỉ đọc. Tầng dịch vụ trích xuất `LocalDate` từ `TransactionDate` khi lọc và bọc kết quả `BigDecimal` vào `Money`. `findByDateRangeAndType` và `sumAmountByFilter` chỉ tính trên `IncomeExpenseTransaction`, do đó `ReportServiceImpl` sử dụng `findByDateRange` khi lập báo cáo tài chính để tổng hợp đầy đủ mọi khoản `TransactionFee` từ cả thu chi lẫn chuyển tiền.
- Kế thừa 5 phương thức cơ sở từ `GenericRepository<Transaction, UUID>` mà không khai báo lặp lại, đồng thời lưu trữ đa hình cả `IncomeExpenseTransaction` và `TransferTransaction` thông qua trường phân biệt loại bản ghi khi tuần tự hóa và giải tuần tự hóa dữ liệu tại tầng hạ tầng.
- Sắp xếp mặc định danh sách kết quả theo ngày giao dịch `transactionDate` giảm dần và tiêu chí phụ theo mốc tạo `createdAt` giảm dần; bảo toàn quan hệ hợp thành sinh tử với `TransactionFee` bằng cách lưu lồng đối tượng phí trong giao dịch gốc và xóa đồng thời khi gọi `deleteById` để ngăn phát sinh bản ghi phí mồ côi.

### 4.2.3 Kho lưu trữ danh mục thu chi (CategoryRepository)

Giao diện `com.pfms.domain.repository.CategoryRepository` kế thừa `GenericRepository<Category, UUID>`, đóng vai trò quản lý truy xuất và lưu trữ thực thể danh mục phân loại chi tiết (cấp 3) trong cấu trúc cây danh mục 3 cấp. Kho lưu trữ cung cấp các phương thức truy vấn chuyên biệt phục vụ `CategoryService` kiểm tra tính duy nhất của tên danh mục trong nhóm nhu cầu cha, lọc trạng thái hoạt động và thực thi cơ chế xóa mềm nhằm bảo toàn tính toàn vẹn tham chiếu cho lịch sử giao dịch.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`, `UC-CAT-02`, `UC-CAT-03`, `UC-CAT-04`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-04`, `BR-NAM-01`
- Mã lỗi: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`, `ERR-CAT-004`, `ERR-CAT-005`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.CategoryRepository` kế thừa `GenericRepository<Category, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `findByType` | `TransactionType type` | `List<Category>` | Truy vấn danh mục chi tiết trực thuộc các nhóm nhu cầu của một nhánh loại thu chi (`INCOME`/`EXPENSE`). |
| `findByNeedGroupId` | `UUID needGroupId` | `List<Category>` | Truy vấn danh sách danh mục con cấp 3 trực thuộc nhóm nhu cầu cha cấp 2 (`needGroupId`). |
| `existsByNameAndNeedGroupId` | `String name, UUID needGroupId` | `boolean` | Kiểm tra trùng tên danh mục (không phân biệt hoa thường) trong cùng nhóm cha đang hoạt động. |
| `existsByNameAndNeedGroupIdAndIdNot` | `String name, UUID needGroupId, UUID categoryId` | `boolean` | Kiểm tra trùng tên trong cùng nhóm cha, loại trừ chính danh mục đang cập nhật. |
| `findByNameAndNeedGroupId` | `String name, UUID needGroupId` | `Optional<Category>` | Tìm danh mục theo tên trong cùng nhóm nhu cầu cha (phục vụ khôi phục danh mục xóa mềm). |
| `countActiveByNeedGroupId` | `UUID needGroupId` | `long` | Đếm số lượng danh mục con đang hoạt động (`ACTIVE`) trực thuộc nhóm nhu cầu cha. |
| `findActiveCategories` | Không | `List<Category>` | Lọc danh sách danh mục đang hoạt động (`ACTIVE`), loại trừ danh mục đã xóa mềm (`DELETED`). |

**Chỉ dẫn thiết kế và cài đặt:**

- Các phương thức truy vấn mở rộng hỗ trợ quản lý cây danh mục 3 cấp: lọc theo nhánh loại thu chi (`findByType`), duyệt danh mục con theo nhóm cha (`findByNeedGroupId`), kiểm tra trùng tên trong nhóm (`existsByNameAndNeedGroupId`, `existsByNameAndNeedGroupIdAndIdNot`), tìm kiếm khôi phục danh mục xóa mềm (`findByNameAndNeedGroupId`), kiểm tra ràng buộc số lượng (`countActiveByNeedGroupId`) và lọc danh mục đang hoạt động (`findActiveCategories`).
- Kế thừa 5 phương thức CRUD cơ sở từ `GenericRepository<Category, UUID>` (`save`, `findById`, `findAll`, `existsById`, `deleteById`) mà không khai báo lặp lại. Trong đó, theo quy định nghiệp vụ bảo toàn tham chiếu, thao tác xóa danh mục được tầng nghiệp vụ phối hợp kho lưu trữ xử lý: tầng nghiệp vụ kiểm tra liên kết lịch sử giao dịch thông qua `TransactionRepository.existsByCategoryId(categoryId)`; nếu danh mục đã từng phát sinh giao dịch lịch sử, hệ thống áp dụng cơ chế xóa mềm bằng cách cập nhật thuộc tính trạng thái sang `DELETED` và lưu lại thông qua `save` nhằm bảo toàn vẹn toàn lịch sử giao dịch và số liệu báo cáo; chỉ khi danh mục chưa từng phát sinh giao dịch nào mới thực thi xóa vật lý khỏi kho lưu trữ qua `deleteById`.
- Kiểm soát tính toàn vẹn cây danh mục 3 cấp: mỗi danh mục cấp 3 bắt buộc phải trực thuộc một nhóm nhu cầu cha cấp 2 hợp lệ đang hoạt động, nghiêm cấm tạo danh mục vào nhóm đã xóa hoặc tạo danh mục mồ côi. Khi chuyển nhóm cha, nhóm đích bắt buộc phải đang hoạt động và thuộc cùng nhánh loại thu chi. Đồng thời, tầng dịch vụ kiểm soát quy tắc bảo vệ nhóm nhu cầu duy nhất còn lại của một nhánh loại thu chi không được phép xóa, bảo đảm mỗi nhánh luôn duy trì tối thiểu một nhóm hoạt động.

### 4.2.4 Kho lưu trữ kiểm kê số dư (BalanceAdjustmentRepository)

Giao diện `BalanceAdjustmentRepository` kế thừa `GenericRepository<BalanceAdjustmentRecord, UUID>` để ghi nhận lịch sử kiểm kê số dư thực tế của các ví. Kho lưu trữ cung cấp các phương thức truy xuất vết kiểm toán theo ví và tra cứu lần đối soát gần nhất phục vụ `WalletService` cùng `DataManagementService`.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-04`
- Yêu cầu chức năng: `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi: `ERR-FIN-001`, `ERR-WAL-001`

**Giao diện kho lưu trữ:**

Giao diện `com.pfms.domain.repository.BalanceAdjustmentRepository` kế thừa `GenericRepository<BalanceAdjustmentRecord, UUID>` và định nghĩa các phương thức mở rộng chuyên biệt:

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `findByWalletId` | `UUID walletId` | `List<BalanceAdjustmentRecord>` | Truy vấn danh sách bản ghi kiểm kê của ví theo thứ tự ngày `adjustedAt` giảm dần. |
| `findLatestByWalletId` | `UUID walletId` | `Optional<BalanceAdjustmentRecord>` | Truy vấn bản ghi kiểm kê gần nhất của một ví; trả về `Optional.empty()` nếu chưa từng kiểm kê. |

**Chỉ dẫn thiết kế và cài đặt:**

- 2 phương thức truy vấn mở rộng phục vụ kiểm tra lịch sử trước khi xóa ví, đối soát số dư toàn vẹn và tra cứu bản ghi điều chỉnh gần nhất của ví.
- Kế thừa từ `GenericRepository<BalanceAdjustmentRecord, UUID>` nhưng thực thi nghiêm ngặt cơ chế chỉ cho phép ghi thêm bản ghi mới: phương thức `save` chỉ lưu bản ghi kiểm kê mới bất biến, trong khi thao tác cập nhật bản ghi cũ hoặc gọi `deleteById` bị chặn và ném `UnsupportedOperationException` nhằm bảo vệ tính toàn vẹn của vết kiểm toán.
- Bảo đảm mọi bản ghi lưu trữ đều có số dư thực tế `actualBalance` không âm và độ lệch `difference` khác `0.00 VNĐ`; khoản chênh lệch chỉ được `WalletService` cập nhật trực tiếp vào số dư ví liên kết mà không sinh giao dịch thu chi giả mạo trong `TransactionRepository`.

### 4.2.5 Kho lưu trữ sao lưu hệ thống (BackupRepository)

Giao diện `BackupRepository` trong gói `com.pfms.domain.repository` định nghĩa hợp đồng trừu tượng phục vụ việc xuất và phục hồi tệp sao lưu dữ liệu toàn hệ thống mà không phụ thuộc vào công nghệ tệp hay thư viện thứ ba.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-DAT-01`, `UC-DAT-02`
- Yêu cầu chức năng: `FR-DAT-01`, `FR-DAT-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`, `BR-DAT-01`, `BR-DAT-02`
- Mã lỗi: `ERR-DAT-001`, `ERR-DAT-002`, `StorageException`

**Giao diện kho lưu trữ:**

| Phương thức | Tham số | Kiểu trả về | Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `writeBackup` | `Path targetPath, BackupPayload payload` | `long` | Tuần tự hóa cấu trúc `BackupPayload` sang JSON và ghi nguyên tử vào tệp đích `targetPath`, trả về dung lượng tệp (bytes) đã ghi. |
| `resolveWritableTargetPath` | `String destinationDirectoryPath, String scope, LocalDateTime exportedAt` | `Path` | Xác thực đường dẫn thư mục đích và sinh đường dẫn tệp sao lưu chuẩn hóa có nhãn thời gian: `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json`. |
| `readBackup` | `String backupFilePath` | `BackupPayload` | Xác thực tính hợp lệ của tệp sao lưu, đọc nội dung UTF-8 (loại bỏ BOM) và giải tuần tự hóa JSON thành cấu trúc `BackupPayload`. |

**Chỉ dẫn thiết kế và cài đặt:**

- Khác với các kho lưu trữ thực thể kế thừa `GenericRepository`, `BackupRepository` là kho lưu trữ chuyên biệt xử lý đối tượng tổng hợp `BackupPayload`. Giao diện này thuộc tầng nghiệp vụ (`domain.repository`), giúp tầng dịch vụ (`DataManagementServiceImpl`) hoàn toàn không phụ thuộc vào các engine xử lý tệp hay thư viện JSON cụ thể.
- Tại tầng hạ tầng, lớp cài đặt `FileBackupRepository` thuộc gói `com.pfms.infrastructure.file` hiện thực hóa giao diện này bằng cách phối hợp với các thành phần hỗ trợ kỹ thuật trong gói con `com.pfms.infrastructure.file.support` (`FileStorageSupport`, `JsonEntitySerializer`, `JsonEntityDeserializer`).

## 4.3 Cài đặt hạ tầng lưu trữ

tầng hạ tầng (`com.pfms.infrastructure`) hiện thực hóa toàn bộ các giao diện kho lưu trữ đã khai báo tại tầng nghiệp vụ (`com.pfms.domain.repository`), bảo đảm nguyên lý nghịch đảo phụ thuộc (DIP). Hệ thống cung cấp hai cơ chế lưu trữ chuyên biệt: bộ điều hợp tệp tin (`com.pfms.infrastructure.file`) phục vụ lưu trữ bền vững trong môi trường vận hành thực tế và bộ điều hợp bộ nhớ (`com.pfms.infrastructure.memory`) phục vụ môi trường kiểm thử tự động.

Nhằm bảo đảm tính đóng gói và cấu trúc rạch ròi, gói hạ tầng tệp tin được phân tách thành 2 cấp độ:
1. **Gói công khai `com.pfms.infrastructure.file`:** Chứa các lớp Repository hiện thực hóa trực tiếp các giao diện từ `domain.repository` (`JsonWalletRepository`, `JsonNeedGroupRepository`, `JsonCategoryRepository`, `JsonTransactionRepository`, `JsonBalanceAdjustmentRepository`, `FileBackupRepository`).
2. **Gói con kỹ thuật `com.pfms.infrastructure.file.support`:** Đóng kín các engine I/O và tiện ích cấp thấp (`FileStorageSupport`, `JsonEntitySerializer`, `JsonEntityDeserializer`, `JsonDataStore`), ngăn chặn việc rò rỉ chi tiết công nghệ Gson hay cơ chế hoán đổi tệp nguyên tử ra ngoài.

Hai cơ chế này đóng kín toàn bộ chi tiết thao tác vào ra vật lý và cấu trúc dữ liệu nội bộ. tầng hạ tầng chịu trách nhiệm chuyển đổi hai chiều giữa dữ liệu lưu trữ và thực thể nghiệp vụ, quản lý an toàn luồng dữ liệu, đồng thời đóng gói các ngoại lệ vào ra cấp thấp thành `StorageException` nhằm giữ cho tầng nghiệp vụ hoàn toàn độc lập với môi trường thực thi.

### 4.3.1 Định dạng và cấu trúc bản ghi

Hệ thống tổ chức lưu trữ dữ liệu bền vững và tệp sao lưu trên ổ đĩa cục bộ dưới dạng tệp văn bản JSON mã hóa UTF-8, vận hành hoàn toàn độc lập mà không phụ thuộc vào hệ quản trị cơ sở dữ liệu bên ngoài. Cấu trúc tệp đóng gói trạng thái nhất quán của các nhóm thực thể nghiệp vụ, bảo toàn độ chính xác số học tài chính và phục vụ trực tiếp cho các quy trình lưu trữ, xuất dữ liệu sao lưu và phục hồi dữ liệu an toàn.

Tệp dữ liệu lưu trữ theo định dạng `JSON` với bảng mã `UTF-8`. Các trường số tiền lưu dưới dạng chuỗi số học cố định 2 chữ số thập phân để giữ độ chính xác, mốc thời gian lưu theo định dạng `yyyy-MM-dd` và mã định danh lưu dưới dạng chuỗi `UUID`.

**Cấu trúc thuộc tính tệp dữ liệu:**

Cấu trúc các trường dữ liệu `JSON` lưu trữ trạng thái của các thực thể trên đĩa cứng:

| Trường dữ liệu | Kiểu dữ liệu JSON | Bắt buộc | Ràng buộc và Mô tả kỹ thuật |
| :--- | :--- | :--- | :--- |
| `wallets` | `array` | Bắt buộc | Danh sách thực thể `Wallet`, mỗi phần tử gồm `walletId` (chuỗi `UUID`), `name` (chuỗi từ 1 đến 50 ký tự theo quy chuẩn đặt tên), `currentBalance` (chuỗi số học thập phân 2 chữ số không âm), `status` (`ACTIVE` hoặc `ARCHIVED`), `isDefault` (`boolean`, cờ đánh dấu ví tiền mặt mặc định), `note` (chuỗi ghi chú tối đa 255 ký tự) và `createdAt` (mốc thời gian tạo bản ghi). |
| `categories` | `array` | Bắt buộc | Danh sách phân cấp danh mục gồm `NeedGroup` cấp 2 (`groupId`, `groupName`, `flowType`, `groupStatus`) và `Category` cấp 3 (`categoryId`, `name`, `groupId`, `flowType`, `status`, `createdAt`), gắn với nhánh `TransactionType` (`INCOME` hoặc `EXPENSE`) và trạng thái `ACTIVE` hoặc `DELETED`, cùng các trường tùy chọn `icon`, `color`. |
| `transactions` | `array` | Bắt buộc | Danh sách thực thể `Transaction` lưu trữ đa hình theo `recordType`: đối với `INCOME_EXPENSE` gồm `walletId` (chuỗi `UUID`), `categoryId` (chuỗi `UUID`), `transactionType` (`INCOME`/`EXPENSE`); đối với `TRANSFER` gồm `sourceWalletId` và `destinationWalletId` (chuỗi `UUID`); cùng các trường chung `transactionId` (chuỗi `UUID`), `amount` (chuỗi số học dương 2 chữ số thập phân), `transactionDate` (chuỗi định dạng `yyyy-MM-dd`), `note` (chuỗi ghi chú), `createdAt` (mốc thời gian tạo bản ghi) và đối tượng con lồng nhau `fee` (`TransactionFee` nếu có phát sinh chi phí). |
| `adjustments` | `array` | Bắt buộc | Danh sách bản ghi kiểm kê bất biến `BalanceAdjustmentRecord`, mỗi phần tử gồm `recordId` (chuỗi `UUID`), `walletId` (chuỗi `UUID`), `previousBalance` (chuỗi số học số dư trước kiểm kê), `actualBalance` (chuỗi số học thập phân 2 chữ số không âm), `difference` (chuỗi số học chênh lệch đại số khác `0.00`), `adjustedAt` (chuỗi `yyyy-MM-dd`) và `reason` (chuỗi diễn giải tối đa 255 ký tự). |

**Chỉ dẫn thiết kế và cài đặt:**

- Cấu trúc các mảng đối tượng trong tệp dữ liệu lưu trữ bền vững và sao lưu bao gồm: `wallets` (danh sách ví tiền), `categories` (danh mục phân loại và nhóm nhu cầu), `transactions` (toàn bộ giao dịch thu chi và chuyển tiền), và `adjustments` (các bản ghi kiểm kê điều chỉnh số dư). Khi xuất dữ liệu sao lưu toàn bộ hệ thống (`FULL`), tệp JSON đóng gói đầy đủ các mảng thực thể cùng các liên kết lịch sử; khi xuất phạm vi sổ giao dịch (`TRANSACTIONS`), tệp tập trung vào mảng giao dịch và các chi phí liên kết đang tồn tại. Nhóm thực thể chưa có dữ liệu được biểu diễn bằng mảng rỗng `[]` thay vì `null` hoặc lược bỏ trường.
- Quy cách lưu trữ các trường dữ liệu tuân thủ nghiêm ngặt quy chuẩn phân tích miền: mọi trường định danh duy nhất (`walletId`, `categoryId`, `groupId`, `transactionId`, `recordId`, `feeId`) đều lưu dưới dạng chuỗi chuẩn `UUID` bất biến và khôi phục qua `UUID.fromString`. Toàn bộ các trường số tiền (`currentBalance`, `amount`, `previousBalance`, `actualBalance`, `difference`) bắt buộc phải lưu dưới dạng chuỗi số học cố định 2 chữ số thập phân theo quy tắc độ chính xác tài chính, tuyệt đối không dùng số thực dấu chấm động nhị phân (`number`/`float`/`double`) nhằm tránh sai số làm tròn khi tuần tự hóa và nạp vào đối tượng giá trị `Money` (`BigDecimal`, `RoundingMode.HALF_EVEN`). Các mốc thời gian lưu dưới dạng chuỗi chuẩn `yyyy-MM-dd`.
- Cơ chế biểu diễn đối tượng lồng nhau và phân cấp: chi phí giao dịch `TransactionFee` được biểu diễn như một đối tượng lồng nhau (`nested object`) bên trong trường `fee` của bản ghi giao dịch chủ (`null` nếu không có chi phí), phản ánh đúng quan hệ hợp thành sinh tử và bảo đảm tính đồng bộ khi sao lưu và phục hồi dữ liệu giao dịch. Cấu trúc danh mục duy trì quan hệ phân cấp qua liên kết từ `Category` lên `NeedGroup` (`groupId`) và nhánh `TransactionType` (`flowType`) khi nạp tệp, phục vụ thẩm định toàn vẹn tham chiếu trong quy trình phục hồi dữ liệu.

### 4.3.2 Cơ chế đọc ghi và an toàn dữ liệu

Bộ điều hợp `JsonDataStore` cùng các kho lưu trữ JSON áp dụng cơ chế đọc ghi qua luồng đệm UTF-8 và tệp tạm thời kết hợp đổi tên nguyên tử (`ATOMIC_MOVE`), ngăn ngừa triệt để nguy cơ hỏng dữ liệu khi mất điện hoặc ngắt tiến trình đột ngột. Trong lần chạy đầu tiên, hệ thống tự động khởi tạo thư mục và tệp JSON gốc chứa các mảng rỗng hợp lệ cùng danh mục mẫu mặc định, đồng thời hỗ trợ tạo bản sao lưu an toàn trước khi phục hồi dữ liệu.

**Quy trình ghi tệp nguyên tử**

Để ngăn hỏng tệp dữ liệu khi mất điện hoặc tiến trình bị ngắt giữa chừng, thao tác lưu dữ liệu được thực thi qua quy trình hoán đổi tệp:

| Bước | Thao tác kỹ thuật | Mục đích và Cơ chế bảo vệ |
| :--- | :--- | :--- |
| 1 | Tuần tự hóa đồ thị đối tượng hiện hành thành chuỗi `JSON` mã hóa `UTF-8` không BOM và ghi vào tệp tạm (`pfms_data.json.tmp`) cùng thư mục với tệp chính. | Tách quá trình ghi luồng byte mới khỏi tệp dữ liệu chính; nếu tràn đĩa hoặc ngắt tiến trình giữa chừng thì tệp chính vẫn giữ nguyên vẹn. |
| 2 | Ép xả dữ liệu xuống đĩa vật lý bằng cách gọi `flush` trên luồng ghi và `FileChannel.force` (hoặc đồng bộ hệ điều hành). | Đẩy toàn bộ dữ liệu từ bộ đệm của hệ điều hành xuống đĩa vật lý, bảo đảm nội dung tệp tạm đã được lưu bền vững trên phần cứng trước khi thay thế tệp chính. |
| 3 | Đóng luồng tệp tạm và gọi `Files.move` với các cờ `StandardCopyOption.ATOMIC_MOVE` cùng `StandardCopyOption.REPLACE_EXISTING` để đè tệp tạm lên tệp chính. | Hoán đổi con trỏ tệp ở mức hệ thống tệp trong một thao tác nguyên tử duy nhất; bảo đảm tệp chính luôn ở trạng thái hoàn chỉnh, không bao giờ bị rơi vào trạng thái dở dang. |

**Chỉ dẫn thiết kế và cài đặt:**

- Mọi thao tác cập nhật ở các kho lưu trữ `JSON` đều ủy quyền cho `JsonDataStore` thực thi qua quy trình ghi tệp tạm `.tmp`, ép xả dữ liệu xuống đĩa vật lý và hoán đổi nguyên tử đè lên tệp chính, tuyệt đối không mở luồng ghi đè trực tiếp lên tệp đang vận hành.
- Quy trình đọc tệp và kiểm soát an toàn dữ liệu: lớp điều hợp lưu trữ thực hiện đọc dữ liệu thông qua luồng đệm (`BufferedReader`) với bảng mã UTF-8 không BOM, giải tuần tự hóa các mảng JSON thành các tập thực thể miền tương ứng; kiểm tra quyền đọc (`READ permission`) trước khi thao tác. Nếu tệp không tồn tại hoặc phát sinh lỗi đọc vật lý, hệ thống đóng gói lỗi thành `StorageException`; nếu cấu trúc JSON bị hỏng, sai cú pháp hoặc thiếu các trường bắt buộc, hệ thống ném `DataCorruptionException` để kích hoạt cơ chế xử lý lỗi tầng trên.
- Cơ chế bảo vệ và an toàn dữ liệu khi phục hồi (`restoreBackupData`): trước khi nạp đè dữ liệu từ bản sao lưu bên ngoài, hệ thống tự động tạo bản sao lưu dự phòng hiện thời có nhãn thời gian theo định dạng `backup_YYYYMMDD_HHMMSS.json` lưu trong thư mục `backups/`. Quá trình phục hồi áp dụng cơ chế tất cả hoặc không (All-or-Nothing): toàn bộ dữ liệu hệ thống chỉ được thay thế khi tệp sao lưu vượt qua đầy đủ các kiểm tra toàn vẹn về định dạng số liệu tài chính, cấu trúc phân cấp danh mục, tính toàn vẹn giao dịch cùng chi phí liên kết, và bảo toàn số dư ví không âm; nếu xuất hiện bất kỳ sai phạm nào, toàn bộ quá trình lập tức bị hủy bỏ để bảo toàn dữ liệu đang vận hành.
- Cơ chế kiểm tra và tự động khởi tạo dữ liệu ban đầu: khi hệ thống khởi chạy lần đầu tiên, `JsonDataStore` kiểm tra sự tồn tại của thư mục lưu trữ và tệp dữ liệu cục bộ; nếu chưa tồn tại, hệ thống tự động tạo mới cây thư mục qua `Files.createDirectories` và khởi tạo tệp JSON rỗng với đầy đủ các mảng dữ liệu hợp lệ (`wallets`, `categories`, `transactions`, `adjustments`), đồng thời nạp cấu trúc danh mục mẫu ban đầu một lần duy nhất mà không lặp lại ở các lần khởi động kế tiếp.
- Quy tắc xử lý khi phát sinh sự cố vào ra (I/O error): nếu quá trình ghi vào tệp tạm thời gặp lỗi quyền truy cập, đầy đĩa hoặc ngắt kết nối, khối xử lý ngoại lệ đóng luồng, xóa tệp `.tmp` dở dang qua `Files.deleteIfExists`, giữ nguyên trạng thái vẹn toàn không bị thay đổi của tệp dữ liệu chính; toàn bộ ngoại lệ kỹ thuật cấp thấp (`IOException`) được bao bọc thành `StorageException` và chuyển tiếp lên tầng trên để tầng trình diễn thông báo lỗi rõ ràng cho người dùng mà không làm hỏng dữ liệu hệ thống.

### 4.3.3 Cài đặt lưu trữ trong bộ nhớ (Memory Repository)

Bộ điều hợp lưu trữ bộ nhớ thuộc tầng hạ tầng hiện thực hóa các giao diện kho lưu trữ hoàn toàn trên bộ nhớ RAM, phục vụ việc chạy các ca kiểm thử đơn vị độc lập và tốc độ cao. Toàn bộ các lớp cài đặt này được đặt trong gói `com.pfms.infrastructure.memory`, loại bỏ sự phụ thuộc vào hệ thống tệp và bảo đảm môi trường kiểm thử không bị ô nhiễm dữ liệu rác.

**Cấu trúc lớp cài đặt bộ nhớ:**

Cấu trúc các lớp cài đặt kho lưu trữ hoạt động trên bộ nhớ RAM phục vụ môi trường kiểm thử:

| Lớp cài đặt | Giao diện hiện thực | Cấu trúc dữ liệu nội bộ | Trách nhiệm chính |
| :--- | :--- | :--- | :--- |
| `MemoryGenericRepository<E, ID>` | `GenericRepository<E, ID>` | `ConcurrentHashMap<ID, E>` | Lớp cơ sở trừu tượng cung cấp các thao tác dữ liệu chuẩn (`save`, `findById`, `findAll`, `existsById`, `deleteById`) và phương thức `clear()` giải phóng toàn bộ dữ liệu trong bộ nhớ sau mỗi ca kiểm thử. |
| `MemoryWalletRepository` | `WalletRepository` | `ConcurrentHashMap<UUID, Wallet>` | Kế thừa `MemoryGenericRepository<Wallet, UUID>`; hiện thực các thao tác truy vấn ví trên RAM (`findByName`, `existsByName`, `findByStatus`, `countByStatus`) phục vụ kiểm thử đơn vị luồng nghiệp vụ ví. |
| `MemoryTransactionRepository` | `TransactionRepository` | `ConcurrentHashMap<UUID, Transaction>` | Kế thừa `MemoryGenericRepository<Transaction, UUID>`; lưu trữ đa hình các loại giao dịch và hiện thực truy vấn lọc (`findByDateRange`, `findByDateRangeAndType`, `findByCategoryId`, `existsByCategoryId`, `sumAmountByFilter`, `findByWalletId`). |
| `MemoryCategoryRepository` | `CategoryRepository` | `ConcurrentHashMap<UUID, Category>` | Kế thừa `MemoryGenericRepository<Category, UUID>`; hiện thực các thao tác truy vấn phân cấp cây danh mục 3 cấp và kiểm tra trùng tên (`findByType`, `findByNeedGroupId`, `existsByNameAndNeedGroupId`, `findActiveCategories`). |
| `MemoryBalanceAdjustmentRepository` | `BalanceAdjustmentRepository` | `ConcurrentHashMap<UUID, BalanceAdjustmentRecord>` | Kế thừa `MemoryGenericRepository<BalanceAdjustmentRecord, UUID>`; lưu trữ các bản ghi kiểm kê số dư ví theo cơ chế chỉ thêm (Append-only) và hiện thực truy vấn đối soát (`findByWalletId`, `findLatestByWalletId`). |

**Chỉ dẫn thiết kế và cài đặt:**

- Sử dụng cấu trúc bảng băm an toàn luồng `ConcurrentHashMap<ID, E>` làm vùng chứa dữ liệu thực thể trên bộ nhớ RAM, đánh chỉ mục trực tiếp theo khóa chính `UUID`. Cấu trúc này bảo đảm tính toàn vẹn dữ liệu trong các kịch bản kiểm thử đồng thời (concurrent tests) mà không phát sinh xung đột khóa, cho phép thực thi các thao tác đọc ghi và tìm kiếm với độ phức tạp thời gian trung bình O(1).
- Cung cấp phương thức dọn sạch dữ liệu `clear()` trong lớp cơ sở `MemoryGenericRepository` (thực thi `storage.clear()`) để làm rỗng bảng băm lưu trữ. Phương thức này được tích hợp vào các bước chuẩn bị (`@BeforeEach`) hoặc dọn dẹp (`@AfterEach`) của vòng đời kiểm thử tự động (JUnit), bảo đảm thiết lập lại trạng thái ban đầu sạch sẽ và loại bỏ hoàn toàn sự phụ thuộc chéo giữa các ca kiểm thử.
- Toàn bộ dữ liệu trong các kho lưu trữ bộ nhớ chỉ tồn tại tạm thời trên bộ nhớ heap của máy ảo JVM và mang tính chất không bền vững (non-persistent). Toàn bộ dữ liệu được tự động giải phóng hoàn toàn ngay khi tiến trình kiểm thử kết thúc, bảo đảm không tạo ra bất kỳ tệp dữ liệu rác nào trên hệ thống tệp và không gây ảnh hưởng đến dữ liệu vận hành thực tế của người dùng.

### 4.3.4 So sánh đánh đổi giữa hai cơ chế lưu trữ

Việc duy trì song song hai cơ chế lưu trữ xuất phát từ sự khác biệt căn bản về yêu cầu giữa môi trường vận hành thực tế và môi trường kiểm thử tự động. Thay vì sử dụng các thư viện giả lập đối tượng phức tạp, hệ thống áp dụng nguyên lý nghịch đảo phụ thuộc để cung cấp hai bộ điều hợp cụ thể cho cùng một tập giao diện kho lưu trữ. Cơ chế tiêm phụ thuộc cho phép tráo đổi linh hoạt giữa tệp tin và bộ nhớ tại điểm khởi tạo ứng dụng mà không làm thay đổi logic nghiệp vụ.

**Bảng so sánh đối chiếu đa chiều giữa hai cơ chế lưu trữ:**

| Tiêu chí so sánh | Lưu trữ bằng tệp tin (`.file`) | Lưu trữ trong bộ nhớ (`.memory`) |
| :--- | :--- | :--- |
| Phương tiện lưu trữ vật lý | Tệp văn bản JSON mã hóa UTF-8 trên ổ đĩa cục bộ | Bảng băm `ConcurrentHashMap` trong bộ nhớ RAM |
| Tính bền vững dữ liệu | Dữ liệu được bảo toàn vẹn toàn qua các phiên làm việc | Dữ liệu tự động giải phóng khi tiến trình JVM kết thúc |
| Tốc độ truy xuất và độ trễ | Phụ thuộc tốc độ I/O đĩa và thời gian phân giải chuỗi JSON | Tốc độ truy xuất trực tiếp trên RAM cấp độ nano-giây |
| Phụ thuộc môi trường thực thi | Yêu cầu quyền đọc ghi tệp và đường dẫn thư mục hợp lệ | Hoàn toàn độc lập với hệ điều hành và hệ thống tệp |
| Tính cô lập trong kiểm thử | Cần cơ chế dọn dẹp tệp dữ liệu rác sau mỗi ca kiểm thử | Cô lập tuyệt đối giữa các ca kiểm thử qua lệnh `clear()` |
| Độ phức tạp cài đặt | Cần thuật toán ghi tệp nguyên tử và cơ chế bọc tệp tạm | Cài đặt đơn giản dựa trên thao tác bản đồ khóa giá trị |
| Cơ chế xử lý lỗi | Bắt lỗi `IOException` và bọc thành `StorageException` | Rủi ro lỗi thấp, chỉ xảy ra khi cạn kiệt bộ nhớ RAM |
| Mục đích sử dụng chính | Vận hành sản phẩm thực tế cho người dùng cuối | Chạy kiểm thử đơn vị và kiểm thử tích hợp tự động |

**Đánh giá kiến trúc và ngưỡng chuyển đổi công nghệ:**

Cơ chế lưu trữ tệp tin JSON được lựa chọn để tối ưu hóa tính độc lập cho một ứng dụng tài chính cá nhân chạy trên máy trạm cục bộ, loại bỏ hoàn toàn gánh nặng phải cài đặt và cấu hình hệ quản trị cơ sở dữ liệu bên ngoài. Tuy nhiên, kiến trúc hệ thống cũng xác định rõ giới hạn kỹ thuật của giải pháp này:

1. **Giới hạn hiệu năng:** Khi quy mô dữ liệu vượt quá ngưỡng mười nghìn giao dịch hoặc xuất hiện nhu cầu đọc ghi đồng thời từ nhiều luồng tiến trình, việc tuần tự hóa toàn bộ tệp JSON sẽ trở thành nút thắt cổ chai về hiệu năng I/O.
2. **Khả năng mở rộng:** Nhờ sự phân tách triệt để của mẫu kho lưu trữ (Repository Pattern), khi hệ thống đạt tới ngưỡng chuyển đổi này, lập trình viên có thể bổ sung gói điều hợp cơ sở dữ liệu mới (như SQLite hoặc H2 Database) mà không phải sửa đổi bất kỳ dòng mã nguồn nào trong tầng nghiệp vụ.

\newpage

# 05. Sơ đồ tuần tự thiết kế

Tài liệu này đặc tả luồng tương tác động giữa các đối tượng trong hệ thống để hiện thực hóa các ca sử dụng qua 4 tầng kiến trúc: tầng trình diễn tiếp nhận sự kiện, tầng dịch vụ điều phối ca sử dụng, tầng nghiệp vụ thực thi quy tắc miền và tầng hạ tầng lưu trữ dữ liệu bền vững.

Các sơ đồ tuần tự xác định trình tự gọi phương thức đồng bộ, quy chuẩn trao đổi DTO và cơ chế biến đổi trạng thái của thực thể. Mọi thông điệp trao đổi khớp chính xác chữ ký phương thức đã thiết kế, mô hình hóa đầy đủ các nhánh ngoại lệ (`alt`) và khối giao dịch nguyên tử (`critical`) nhằm bảo toàn tính nhất quán dữ liệu tài chính.

## 5.1 Nguyên tắc thiết kế sơ đồ tuần tự chi tiết

Mục này xác lập nguyên tắc thiết kế và quy chuẩn mô hình hóa tương tác động, bảo đảm tính nhất quán trong biểu diễn luồng dữ liệu và lời gọi phương thức.

Nếu sơ đồ tuần tự hệ thống (ở giai đoạn phân tích) xem phần mềm như một "hộp đen" chỉ nhận sự kiện và trả phản hồi, thì sơ đồ tuần tự thiết kế mở rộng thành "hộp trắng" phân rã luồng gọi hàm qua 4 tầng kiến trúc: tầng trình diễn (tiếp nhận sự kiện/hiển thị), tầng dịch vụ (điều phối ca sử dụng), tầng nghiệp vụ (thực thi quy tắc miền) và tầng hạ tầng (lưu trữ bền vững).

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

1. **Tác nhân ngoài**: Người dùng thực hiện thao tác chuột và phím trên giao diện đồ họa.
2. **Tầng trình diễn**: Lớp giao diện đồ họa đọc dữ liệu biểu mẫu, kiểm tra định dạng và đóng gói đối tượng truyền dữ liệu.
3. **Tầng dịch vụ**: Lớp dịch vụ ứng dụng tiếp nhận yêu cầu, nạp dữ liệu và điều phối quy trình xử lý ca sử dụng.
4. **Tầng nghiệp vụ**: Các thực thể và đối tượng giá trị thực hiện tính toán, kiểm tra ràng buộc và cập nhật trạng thái.
5. **Tầng hạ tầng**: Kho lưu trữ dữ liệu thực hiện các thao tác truy xuất hoặc lưu trữ bền vững.

Sơ đồ dưới đây minh họa luồng tương tác tổng quát qua bốn tầng kiến trúc:

![Sơ đồ tuần tự luồng xử lý qua bốn tầng](docs/object-oriented-design/images/so-do-tuan-tu-bon-tang.png){width=80%}

Mọi thông điệp trao đổi trên sơ đồ tuần tự bắt buộc phải khớp chính xác với chữ ký phương thức đã công bố trong các giao diện dịch vụ, thực thể miền và kho lưu trữ dữ liệu.

Mọi sơ đồ tuần tự mô hình hóa tường minh các nhánh ngoại lệ qua khung điều kiện `alt` kèm mã lỗi định danh tương ứng. Đối với các giao dịch tác động đồng thời lên nhiều ví (như chuyển tiền nội bộ), luồng tương tác được bao bọc trong khung `critical` để bảo đảm tính nguyên tử: toàn bộ thao tác cập nhật số dư phải cùng thành công hoặc hoàn tác toàn bộ khi phát sinh lỗi.

## 5.2 Sơ đồ tuần tự nhóm quản lý ví

Mục này mô hình hóa các luồng tương tác quản lý vòng đời ví: khởi tạo nguồn tiền, cập nhật thông tin, chuyển đổi trạng thái lưu trữ và kiểm kê số dư thực tế. Các kịch bản làm rõ sự phối hợp giữa `WalletServiceImpl` với thực thể `Wallet` và kho lưu trữ, đảm bảo tính duy nhất của tên ví, duy trì số dư không âm và lưu vết kiểm toán kiểm kê.

### 5.2.1 Sơ đồ tạo mới ví

Luồng điều phối khởi tạo ví mới tiếp nhận yêu cầu từ giao diện, xác thực chuẩn định dạng tên ví và kiểm tra tính duy nhất trên toàn hệ thống trước khi khởi tạo thực thể ví với số dư ban đầu bằng không. Dữ liệu thực thể được ghi nhận bền vững xuống kho lưu trữ và chuyển đổi sang đối tượng truyền dữ liệu phản hồi về tầng trình diễn.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-01`
- Thao tác hệ thống: `createWallet(walletName, note)`
- Yêu cầu chức năng: `FR-WAL-01`, `FR-WAL-02`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-02`
- Mã lỗi xử lý: `ERR-NAM-001`, `ERR-NAM-002`

**Sơ đồ tuần tự tương tác:**
![Sơ đồ tuần tự thiết kế tạo ví](docs/object-oriented-design/images/so-do-tuan-tu-thiet-ke-tao-vi.png){width=80%}

Toàn vẹn tài chính được bảo đảm tuyệt đối khi thực thể ví mới luôn được khởi tạo với số dư ban đầu bằng không thông qua đối tượng giá trị bất biến `Money.ZERO`, loại trừ hoàn toàn rủi ro gán số dư tùy tiện khi chưa qua thủ tục kiểm kê thực tế.

### 5.2.2 Sơ đồ xem tổng hợp số dư và cơ cấu ví

Luồng truy vấn danh sách ví trích xuất toàn bộ nguồn tiền khả dụng, tính toán tổng tài sản tức thời và xác định tỷ trọng phân bổ tài sản toàn hệ thống ở chế độ chỉ đọc. Cơ chế tính toán áp dụng nguyên tắc làm tròn nửa chẵn và kiểm soát giá trị mẫu số triệt tiêu nhằm chống lỗi số học chia cho không.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-02`
- Thao tác hệ thống: `getWalletsSummary()`
- Yêu cầu chức năng: `FR-WAL-04`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi xử lý: `ERR-FIN-002`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế xem tổng quan ví](docs/object-oriented-design/images/so-do-tuan-tu-thiet-ke-tong-quan-vi.png){width=80%}

Thao tác vận hành hoàn toàn ở chế độ chỉ đọc, truy xuất thông tin qua các phương thức đọc bất biến và không làm thay đổi trạng thái nội tại của bất kỳ thực thể ví nào trong hệ thống.

### 5.2.3 Sơ đồ cập nhật thông tin ví

Luồng cập nhật thông tin ví tiếp nhận tên gọi và ghi chú mới từ giao diện người dùng, kiểm tra sự tồn tại của nguồn tiền, xác thực quy chuẩn định dạng và kiểm tra tính duy nhất trước khi ủy quyền thực thể cập nhật thuộc tính. Mã định danh và số dư khả dụng hiện hành được bảo toàn nguyên vẹn xuyên suốt quá trình xử lý.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-03`
- Thao tác hệ thống: `updateWallet(walletId, walletName, note)`
- Yêu cầu chức năng: `FR-WAL-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-02`
- Mã lỗi xử lý: `ERR-WAL-001`, `ERR-NAM-001`, `ERR-NAM-002`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế cập nhật ví](docs/object-oriented-design/images/so-do-tuan-tu-thiet-ke-cap-nhat-vi.png){width=80%}

Việc cập nhật thông tin định danh giữ nguyên mã định danh `walletId` và số dư khả dụng `currentBalance`, bảo đảm không làm đứt gãy tính toàn vẹn tham chiếu của các giao dịch lịch sử gắn liền với ví.

### 5.2.4 Sơ đồ điều chỉnh số dư ví thực tế

Luồng điều chỉnh số dư ví đồng bộ số dư trên hệ thống khớp với kết quả kiểm kê thực tế của người dùng. Chuỗi tương tác xác thực tính hợp lệ của số dư thực tế, kiểm tra trạng thái hoạt động của ví, cập nhật số dư mới và khởi tạo bản ghi kiểm kê bất biến để lưu vết kiểm toán tài chính cố định.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-WAL-04`
- Thao tác hệ thống: `adjustBalance(walletId, actualBalance, reason)`
- Yêu cầu chức năng: `FR-WAL-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi xử lý: `ERR-WAL-001`, `ERR-FIN-001`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế điều chỉnh số dư ví](docs/object-oriented-design/images/so-do-tuan-tu-thiet-ke-dieu-chinh-so-du.png){width=80%}

Mọi chênh lệch kiểm kê bắt buộc khởi tạo bản ghi kiểm kê bất biến `BalanceAdjustmentRecord` để lưu giữ vết kiểm toán độc lập, bảo đảm số dư khả dụng mới luôn duy trì giá trị không âm và không làm xáo trộn các số liệu thu chi nguyên thủy.

## 5.3 Sơ đồ tuần tự nhóm ghi nhận thu chi

Mục này mô hình hóa luồng ghi nhận biến động thu chi: tiếp nhận giao dịch, phân loại danh mục, tính phụ phí và cập nhật số dư ví tức thời. Các kịch bản làm rõ sự phối hợp giữa `TransactionServiceImpl` với `IncomeExpenseTransaction`, `Wallet` và các repository, đảm bảo số dư không âm, tính hợp lệ của danh mục và cơ chế hoàn nguyên hai pha khi sửa/xóa giao dịch.

### 5.3.1 Sơ đồ ghi nhận giao dịch thu chi

Luồng điều phối ghi nhận giao dịch thu chi tiếp nhận thông tin thu nhập hoặc chi tiêu kèm chi phí phát sinh (nếu có), xác thực tính hợp lệ của nguồn tiền và danh mục chi tiết, kiểm soát số dư ví khả dụng không âm trước khi khởi tạo thực thể và lưu trữ đồng thời vào kho dữ liệu.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-01`
- Thao tác hệ thống: `recordIncomeExpense(walletId, categoryId, amount, transactionDate, note, feeAmount, feeCategoryId)`
- Yêu cầu chức năng: `FR-TXN-01`, `FR-TXN-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-CAT-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế ghi nhận giao dịch thu chi](docs/object-oriented-design/images/so-do-tuan-tu-ghi-nhan-giao-dich-thu-chi.png){width=80%}

Mọi giao dịch chi tiêu và chi phí liên kết bắt buộc kiểm soát số dư khả dụng không âm trước khi ghi nhận, bảo đảm dữ liệu luôn nhất quán giữa sổ giao dịch và số dư ví.

### 5.3.2 Sơ đồ tra cứu và lọc lịch sử giao dịch

Luồng tra cứu và trích xuất lịch sử giao dịch vận hành hoàn toàn ở chế độ chỉ đọc. Chuỗi tương tác tiếp nhận các tiêu chí lọc thời gian, nguồn tiền, danh mục, kiểm tra tính hợp lệ của khoảng ngày, nạp dữ liệu và tính toán tổng dòng tiền trước khi đóng gói phản hồi về tầng trình diễn.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-02`
- Thao tác hệ thống: `filterTransactions(filterCriteria)`
- Yêu cầu chức năng: `FR-TXN-03`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-FIN-001`

**Sơ đồ tuần tự tương tác:**
![Sơ đồ tuần tự thiết kế lọc giao dịch](docs/object-oriented-design/images/so-do-tuan-tu-loc-giao-dich.png){width=80%}

Thao tác tra cứu vận hành thuần túy ở chế độ chỉ đọc, không làm biến đổi dữ liệu hay trạng thái của các thực thể giao dịch và số dư nguồn tiền trong hệ thống.

### 5.3.3 Sơ đồ chỉnh sửa giao dịch thu chi

Luồng chỉnh sửa giao dịch điều phối việc cập nhật thông tin khoản thu chi đã ghi nhận, áp dụng quy trình kiểm soát hoàn nguyên an toàn nhằm bảo đảm số dư ví sau điều chỉnh luôn thỏa mãn ràng buộc không âm.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-03`
- Thao tác hệ thống: `updateTransaction(transactionId, newAmount, newDate, newCategoryId, newNote, newFeeAmount, newFeeCategoryId)`
- Yêu cầu chức năng: `FR-TXN-04`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-FIN-001`, `ERR-WAL-001`, `ERR-CAT-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế chỉnh sửa giao dịch](docs/object-oriented-design/images/so-do-tuan-tu-chinh-sua-giao-dich.png){width=80%}

Quy trình kiểm soát hai pha bảo đảm giao dịch chỉ được chấp nhận cập nhật khi trạng thái số dư ví sau điều chỉnh duy trì không âm, loại trừ nguy cơ xuất quỹ vượt quá khả năng tài chính.

### 5.3.4 Sơ đồ xóa giao dịch thu chi

Luồng xóa giao dịch loại bỏ bản ghi thu chi khỏi hệ thống, thu hồi khoản chi tiêu hoặc hoàn trả khoản thu nhập về ví nguồn, đồng thời xóa sạch bản ghi chi phí phụ thuộc liên kết.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TXN-04`
- Thao tác hệ thống: `deleteTransaction(transactionId)`
- Yêu cầu chức năng: `FR-TXN-05`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`
- Mã lỗi xử lý: `ERR-WAL-001`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế xóa giao dịch](docs/object-oriented-design/images/so-do-tuan-tu-xoa-giao-dich.png){width=80%}

Việc xóa giao dịch đồng thời thu hồi hoặc hoàn trả số dư ví và loại bỏ hoàn toàn các bản ghi chi phí phụ thuộc liên kết, triệt tiêu nguy cơ phát sinh dữ liệu chi phí mồ côi và bảo đảm số dư không âm.

## 5.4 Sơ đồ tuần tự nhóm chuyển tiền nội bộ

Mục này mô hình hóa các luồng tương tác thực thi nghiệp vụ điều chuyển tiền giữa các ví trong hệ thống. Chuỗi tương tác mô tả quy trình tiếp nhận sự kiện chuyển tiền, kiểm tra tính phân biệt giữa ví nguồn và ví đích, hạch toán chi phí phát sinh liên kết và điều phối cập nhật đồng thời số dư của cả hai ví.

Các kịch bản làm rõ sự phối hợp giữa lớp dịch vụ điều phối với các thực thể giao dịch chuyển tiền, thực thể ví và các kho lưu trữ tương ứng, bảo đảm thực thi quy tắc bảo toàn tổng tài sản trừ chi phí phát sinh, kiểm soát số dư ví nguồn và ví đích luôn không âm và thực hiện cơ chế hoàn nguyên hai pha khi xóa giao dịch chuyển tiền.

### 5.4.1 Sơ đồ chuyển tiền giữa hai ví

Luồng điều phối chuyển tiền từ ví nguồn sang ví đích kiểm tra tính phân biệt của cặp ví, xác thực số tiền chuyển và chi phí hợp lệ, kiểm tra số dư ví nguồn đủ thanh toán tổng tiền chuyển cùng phí trước khi cập nhật đồng thời số dư hai ví và lưu vết giao dịch.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TRF-01`
- Thao tác hệ thống: `transferFunds(sourceWalletId, destinationWalletId, amount, transferDate, note, feeAmount, feeCategoryId)`
- Yêu cầu chức năng: `FR-TRF-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-TRF-001`, `ERR-TRF-002`, `ERR-WAL-001`, `ERR-FIN-001`, `ERR-FEE-001`, `ERR-FEE-002`, `ERR-FEE-003`

**Sơ đồ tuần tự tương tác:**
![Sơ đồ tuần tự thiết kế chuyển tiền giữa hai ví](docs/object-oriented-design/images/so-do-tuan-tu-chuyen-tien.png){width=80%}

Giao dịch chuyển tiền được thực hiện theo cơ chế nguyên tử bảo đảm tổng tài sản toàn hệ thống được bảo toàn (trừ phần chi phí phát sinh thực tế), đồng thời số dư ví nguồn luôn duy trì không âm.

### 5.4.2 Sơ đồ tra cứu lịch sử chuyển tiền

Luồng truy vấn và trích xuất lịch sử chuyển tiền vận hành hoàn toàn ở chế độ chỉ đọc. Chuỗi tương tác nạp các giao dịch chuyển tiền theo khoảng thời gian và cặp ví liên quan, tính tổng tiền chuyển và chi phí phát sinh trước khi đóng gói phản hồi về tầng trình diễn.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TRF-02`
- Thao tác hệ thống: `filterTransfers(startDate, endDate, sourceWalletId, destinationWalletId)`
- Yêu cầu chức năng: `FR-TRF-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi xử lý: `ERR-FIN-001`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế lọc giao dịch chuyển tiền](docs/object-oriented-design/images/so-do-tuan-tu-loc-chuyen-tien.png){width=80%}

Thao tác tra cứu vận hành thuần túy ở chế độ chỉ đọc, không làm thay đổi trạng thái của các thực thể giao dịch hay số dư của bất kỳ ví nào trong hệ thống.

### 5.4.3 Sơ đồ xóa và hoàn tác giao dịch chuyển tiền

Luồng hủy giao dịch chuyển tiền thực hiện thu hồi tiền chuyển từ ví đích, hoàn trả tiền chuyển cùng khoản phí về ví nguồn, đồng thời xóa sạch bản ghi chuyển tiền và chi phí phát sinh liên kết.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-TRF-03`
- Thao tác hệ thống: `deleteTransfer(transferId)`
- Yêu cầu chức năng: `FR-TRF-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`
- Mã lỗi xử lý: `ERR-WAL-001`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế xóa giao dịch chuyển tiền](docs/object-oriented-design/images/so-do-tuan-tu-xoa-chuyen-tien.png){width=80%}

Cơ chế hoàn nguyên hai pha bảo đảm thu hồi đồng thời số dư cả hai ví và xóa sạch các bản ghi chi phí phụ thuộc, ngăn chặn tuyệt đối tình trạng âm số dư ví nhận.

## 5.5 Sơ đồ tuần tự nhóm danh mục phân loại

Mục này mô hình hóa các luồng tương tác thực thi nghiệp vụ quản lý hệ thống danh mục phân loại thu chi trong hệ thống. Chuỗi tương tác mô tả quy trình tiếp nhận thao tác bổ sung danh mục mới, đổi tên hiển thị, khôi phục danh mục đã ẩn và thực hiện cơ chế xóa mềm nhằm bảo đảm tính liên tục của dữ liệu tài chính.

Các kịch bản làm rõ sự phối hợp giữa lớp dịch vụ điều phối với thực thể danh mục, kho lưu trữ danh mục và kho lưu trữ giao dịch, bảo đảm tuân thủ nghiêm ngặt quy tắc cấu trúc phân cấp ba tầng, kiểm soát tính duy nhất của tên gọi trong cùng phạm vi cha và bảo toàn tính toàn vẹn tham chiếu cho các giao dịch lịch sử đã ghi nhận.

### 5.5.1 Sơ đồ thêm mới danh mục chi tiết

Luồng tương tác mô tả quá trình tiếp nhận yêu cầu bổ sung danh mục chi tiết cấp ba vào một nhóm nhu cầu cha đang hoạt động, thực hiện kiểm tra tính hợp lệ của định danh và tính khả dụng của nhóm cha trước khi khởi tạo thực thể. Dịch vụ ứng dụng kiểm soát tính duy nhất của tên trong phạm vi nhóm cha và lưu trữ thực thể danh mục mới vào kho dữ liệu.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-01`
- Thao tác hệ thống: `addCategory(categoryName, parentCategoryId)`
- Yêu cầu chức năng: `FR-CAT-01`, `FR-CAT-02`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`, `ERR-CAT-005`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế tạo danh mục mới](docs/object-oriented-design/images/so-do-tuan-tu-tao-danh-muc.png){width=80%}

Danh mục mới luôn được gắn kết vào một nhóm cha hợp lệ đang hoạt động và kế thừa loại giao dịch cố định, bảo đảm tính toàn vẹn của cấu trúc phân cấp ba tầng.

### 5.5.2 Sơ đồ đổi tên danh mục

Luồng tương tác mô tả quy trình tiếp nhận yêu cầu cập nhật tên hiển thị của danh mục hiện có, kiểm tra định dạng tên mới và kiểm soát tính duy nhất trong phạm vi nhóm cha trực thuộc. Dịch vụ ứng dụng nạp thực thể danh mục, cập nhật tên mới và lưu trữ vào kho dữ liệu mà không làm biến động mã định danh hay lịch sử giao dịch liên kết.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-02`
- Thao tác hệ thống: `renameCategory(categoryId, newName)`
- Yêu cầu chức năng: `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`
- Mã lỗi xử lý: `ERR-NAM-001`, `ERR-NAM-002`, `ERR-CAT-001`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế đổi tên danh mục](docs/object-oriented-design/images/so-do-tuan-tu-doi-ten-danh-muc.png){width=80%}

Việc đổi tên bảo toàn tuyệt đối mã định danh và loại thu chi của danh mục, không làm đứt gãy hoặc sai lệch dữ liệu lịch sử của các giao dịch đã ghi nhận trong quá khứ theo quy tắc đặt tên bất biến của định danh.

### 5.5.3 Sơ đồ khôi phục danh mục đã xóa

Luồng tương tác mô tả quy trình phục hồi danh mục đã xóa mềm trở lại trạng thái hoạt động, đồng thời xác thực trạng thái hoạt động của nhóm cha và kiểm soát xung đột tên gọi trong nhóm. Dịch vụ ứng dụng cập nhật trạng thái của thực thể sang hoạt động và lưu vào kho dữ liệu, đưa danh mục tái xuất hiện trên các biểu mẫu giao dịch.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-03`
- Thao tác hệ thống: `restoreCategory(categoryId)`
- Yêu cầu chức năng: `FR-CAT-02`, `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-NAM-01`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-CAT-001`, `ERR-CAT-004`, `ERR-NAM-002`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế khôi phục danh mục](docs/object-oriented-design/images/so-do-tuan-tu-khoi-phuc-danh-muc.png){width=80%}

Thao tác khôi phục chỉ được phép khi nhóm cha đang hoạt động và không xảy ra xung đột định danh, đưa danh mục trở lại danh sách lựa chọn mà không làm xáo trộn số liệu báo cáo lịch sử.

### 5.5.4 Sơ đồ xóa danh mục (xóa cứng và xóa mềm)

Luồng tương tác mô tả quy trình tiếp nhận yêu cầu loại bỏ danh mục phân loại, kiểm tra ràng buộc duy trì tối thiểu nhóm nhu cầu hoạt động và kiểm tra lịch sử giao dịch tham chiếu. Dịch vụ ứng dụng tự động phân nhánh thực hiện xóa cứng nếu danh mục chưa từng phát sinh giao dịch hoặc xóa mềm chuyển trạng thái để bảo toàn tính toàn vẹn dòng tiền.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-CAT-04`
- Thao tác hệ thống: `deleteCategory(categoryId)`
- Yêu cầu chức năng: `FR-CAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-04`
- Mã lỗi xử lý: `ERR-CAT-001`, `ERR-CAT-002`, `ERR-CAT-003`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế xóa danh mục](docs/object-oriented-design/images/so-do-tuan-tu-xoa-danh-muc.png){width=80%}

Cơ chế xóa mềm bảo toàn tuyệt đối tính toàn vẹn tham chiếu lịch sử của mọi giao dịch liên kết và duy trì tối thiểu một nhóm nhu cầu hoạt động cho mỗi loại thu chi theo quy định tài chính.

## 5.6 Sơ đồ tuần tự nhóm báo cáo và quản trị dữ liệu

Mục này mô hình hóa các luồng tương tác thực thi nghiệp vụ kết xuất báo cáo phân tích tài chính và quản trị dữ liệu an toàn của hệ thống. Chuỗi tương tác mô tả quy trình tổng hợp dòng tiền đa chiều, đo lường cơ cấu thu chi, trích xuất tệp sao lưu định dạng chuẩn, phục hồi dữ liệu có kiểm định bốn bước và đối soát độc lập các điều kiện cân bằng tài chính.

Các kịch bản làm rõ sự phối hợp giữa lớp dịch vụ điều phối báo cáo và quản trị dữ liệu với toàn bộ các kho lưu trữ thực thể trong hệ thống, bảo đảm nguyên tắc vận hành chỉ đọc đối với các luồng thống kê đối soát, kiểm soát tính toàn vẹn của tệp JSON sao lưu và duy trì tính nhất quán tuyệt đối của sổ cái tài chính.

### 5.6.1 Sơ đồ lập báo cáo dòng tiền định kỳ

Luồng tương tác mô tả quy trình truy vấn và tổng hợp dòng tiền định kỳ trong chuỗi 12 tháng liên tục lùi tính từ mốc tháng/năm kết thúc ở chế độ chỉ đọc, tính toán dòng tiền ròng và tỷ lệ tiết kiệm từng tháng. Dịch vụ ứng dụng nạp các giao dịch từ kho lưu trữ theo khoảng ngày, tính toán các chỉ số tài chính, áp dụng tỷ lệ tiết kiệm mặc định khi tổng thu bằng không để tránh lỗi số học và đóng gói kết quả báo cáo.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-01`
- Thao tác hệ thống: `generateCashFlowReport(CashFlowReportRequestDTO)`
- Yêu cầu chức năng: `FR-REP-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`
- Mã lỗi xử lý: `ERR-REP-001`, `ERR-FIN-001`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế lập báo cáo dòng tiền](docs/object-oriented-design/images/so-do-tuan-tu-bao-cao-dong-tien.png){width=80%}

Thao tác vận hành hoàn toàn ở chế độ chỉ đọc, không làm biến đổi trạng thái của bất kỳ thực thể hay số dư tài chính nào trong hệ thống.

### 5.6.2 Sơ đồ lập báo cáo cơ cấu thu chi

Luồng tương tác mô tả quy trình trích xuất và phân tích cơ cấu thu chi theo nhóm nhu cầu và danh mục chi tiết trong khoảng thời gian xác định ở chế độ chỉ đọc. Dịch vụ ứng dụng kiểm tra tính hợp lệ của tham số thời gian cùng loại luồng tiền, truy vấn giao dịch (gồm cả phí giao dịch liên kết khi phân tích chi tiêu) và thông tin phân cấp danh mục, gom cụm số liệu, tính toán tỷ trọng phân bổ chính xác và đối chiếu biến động với kỳ liền kề cùng độ dài.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-REP-02`
- Thao tác hệ thống: `generateSpendingStructureReport(SpendingStructureReportRequestDTO)`
- Yêu cầu chức năng: `FR-REP-02`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-REP-001`, `ERR-FIN-002`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế lập báo cáo cơ cấu thu chi](docs/object-oriented-design/images/so-do-tuan-tu-bao-cao-co-cau-thu-chi.png){width=80%}

Thao tác trích xuất và tính toán tỷ trọng phân bổ thu chi ở chế độ chỉ đọc, bảo toàn tính toàn vẹn của dữ liệu danh mục và số liệu lịch sử.

### 5.6.3 Sơ đồ xuất dữ liệu sao lưu hệ thống

Luồng tương tác mô tả quy trình trích xuất dữ liệu tài chính theo phạm vi chỉ định và đóng gói thành tệp sao lưu JSON chuẩn hóa. Dịch vụ ứng dụng kiểm tra phạm vi xuất hợp lệ, xác thực đường dẫn thư mục đích để tự sinh tên tệp `pfms_export_{scope}_{yyyyMMdd_HHmmss}.json`, nạp ảnh chụp từ các kho lưu trữ rồi tuần tự hóa và ghi an toàn xuống đĩa cục bộ.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-DAT-01`
- Thao tác hệ thống: `exportBackupData(destinationPath, scope)`
- Yêu cầu chức năng: `FR-DAT-01`
- Quy tắc nghiệp vụ: `BR-FIN-01`
- Mã lỗi xử lý: `ERR-DAT-001`, `StorageException`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế xuất dữ liệu sao lưu](docs/object-oriented-design/images/so-do-tuan-tu-xuat-sao-luu.png){width=80%}

Việc xuất dữ liệu sao lưu là thao tác chỉ đọc dữ liệu nghiệp vụ, tạo bản chụp trạng thái nhất quán và giữ nguyên độ chính xác số học của mọi giá trị tiền tệ.

### 5.6.4 Sơ đồ phục hồi dữ liệu hệ thống

Luồng tương tác mô tả quy trình nạp dữ liệu từ tệp sao lưu JSON, giải tuần tự hóa thành thực thể lịch sử và thực thi quy trình kiểm định toàn vẹn nghiêm ngặt theo nguyên tắc tất cả hoặc không trước khi thay thế dữ liệu vào các kho lưu trữ. Nếu phát sinh lỗi ghi khi thay thế, dịch vụ tự động hoàn nguyên bản chụp qua `RollbackSupport` để bảo toàn nguyên vẹn dữ liệu hiện tại.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-DAT-02`
- Thao tác hệ thống: `restoreBackupData(backupFilePath)`
- Yêu cầu chức năng: `FR-DAT-02`
- Quy tắc nghiệp vụ: `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi xử lý: `ERR-DAT-001`, `ERR-DAT-002`, `StorageException`

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế phục hồi dữ liệu từ tệp sao lưu](docs/object-oriented-design/images/so-do-tuan-tu-phuc-hoi-sao-luu.png){width=80%}

Hệ thống bảo lưu nguyên vẹn dữ liệu hiện tại nếu phát hiện bất kỳ sai phạm nào trong quá trình kiểm định và chỉ ghi đè khi toàn bộ tệp sao lưu hợp lệ.

### 5.6.5 Sơ đồ đối soát toàn vẹn dữ liệu tài chính

Luồng tương tác mô tả quy trình quét kiểm tra độc lập bốn điều kiện cân bằng tài chính của toàn bộ hệ thống và xuất báo cáo đối soát chi tiết ở chế độ chỉ đọc. Dịch vụ ứng dụng nạp dữ liệu từ các kho lưu trữ, đánh giá tính nhất quán của số dư ví, cân bằng chuyển tiền, phân cấp danh mục và kiểm tra tính toàn vẹn của các khoản chi phí gắn liền với giao dịch chứa phí (theo quan hệ hợp thành mà không cần kho lưu trữ phí riêng biệt), phân loại cảnh báo theo các cấp độ mà không làm biến đổi dữ liệu.

**Quy chiếu yêu cầu:**
- Ca sử dụng: `UC-DAT-03`
- Thao tác hệ thống: `reconcileSystemData()`
- Yêu cầu chức năng: `FR-DAT-03`
- Quy tắc nghiệp vụ: `BR-FIN-01`, `BR-FIN-02`, `BR-FIN-03`, `BR-FIN-04`
- Mã lỗi xử lý: Không ném ngoại lệ chặn, phân loại kết quả kiểm tra theo các mức INFO, WARNING, ERROR

**Sơ đồ tuần tự tương tác:**

![Sơ đồ tuần tự thiết kế đối soát toàn vẹn dữ liệu hệ thống](docs/object-oriented-design/images/so-do-tuan-tu-doi-soat-du-lieu.png){width=80%}

Thao tác đối soát vận hành thuần túy ở chế độ chỉ đọc, không làm biến đổi bất kỳ bản ghi tài chính nào và cung cấp báo cáo minh bạch về mức độ nhất quán của sổ cái.

\newpage

# 06. Áp dụng nguyên lý SOLID và mẫu thiết kế hướng đối tượng

Tài liệu này đánh giá chất lượng kiến trúc và đặc tả các mẫu thiết kế hướng đối tượng trong hệ thống Quản lý Tài chính Cá nhân (PFMS), đảm bảo hệ thống tuân thủ các nguyên lý phần mềm sạch.

Tài liệu tập trung vào ba mục tiêu kỹ thuật trọng tâm: đối chiếu định lượng mức độ tuân thủ 5 nguyên lý SOLID trên các lớp và giao diện thực tế, xác định điểm cân bằng kỹ thuật tránh thiết kế quá mức (over-engineering); đặc tả 4 mẫu thiết kế GoF cốt lõi (Factory Method, Strategy, State, Observer) nhằm triệt tiêu rẽ nhánh thủ công và tách biệt tác vụ phụ trợ; và đảm bảo mã nguồn đạt độ ghép nối lỏng, tính gắn kết cao cùng khả năng kiểm thử độc lập với JUnit 5.

## 6.1 Đánh giá tuân thủ năm nguyên lý SOLID

Mục này đánh giá mức độ tuân thủ năm nguyên lý SOLID trong kiến trúc của hệ thống Quản lý Tài chính Cá nhân PFMS. Mọi minh chứng kỹ thuật đều được đối chiếu trực tiếp từ các lớp thực thể, dịch vụ và kho lưu trữ thực tế của hệ thống, không dùng các ví dụ trừu tượng ngoài phạm vi dự án.

**Đối chiếu cài đặt năm nguyên lý trong hệ thống:**

Hệ thống tuân thủ trọn vẹn năm nguyên lý thiết kế thông qua việc phân tách cấu trúc bốn tầng hướng tâm và thiết lập các hợp đồng giao diện nghiêm ngặt:

| Nguyên lý thiết kế | Quy tắc cốt lõi | Minh chứng cài đặt trong hệ thống PFMS |
| :--- | :--- | :--- |
| **Đơn trách nhiệm**<br/>(Single Responsibility Principle - SRP) | Mỗi lớp chỉ đảm nhiệm một lý do duy nhất để thay đổi; tách biệt giữa quy tắc nghiệp vụ, điều phối ca sử dụng và lưu trữ tệp. | Phân tách rõ ràng giữa: thực thể `Wallet` (quy tắc tính số dư), dịch vụ `WalletServiceImpl` (điều phối ca sử dụng) và kho lưu trữ `WalletRepository` (đọc/ghi tệp). Mỗi lớp chỉ có một lý do duy nhất để thay đổi, không gây ảnh hưởng chéo. |
| **Đóng mở**<br/>(Open/Closed Principle - OCP) | Thực thể phần mềm mở rộng cho việc phát triển thêm hành vi mới nhưng đóng kín trước việc sửa đổi mã nguồn sẵn có. | Lớp trừu tượng `Transaction` và hai lớp con `IncomeExpenseTransaction`, `TransferTransaction`. Khi bổ sung loại giao dịch mới, chỉ cần tạo lớp con mới kế thừa `Transaction` mà không cần sửa đổi mã nguồn lớp cha hay cấu trúc rẽ nhánh trong tầng dịch vụ. |
| **Thay thế Liskov**<br/>(Liskov Substitution Principle - LSP) | Các đối tượng của lớp con có thể thay thế hoàn toàn cho đối tượng của lớp cha mà không làm thay đổi tính đúng đắn của chương trình. | `IncomeExpenseTransaction` và `TransferTransaction` có thể thay thế hoàn toàn cho `Transaction` trong `TransactionRepository.save(Transaction)` và các luồng trích xuất ngày, phí của `ReportServiceImpl` mà không làm sai lệch tính đúng đắn của chương trình. |
| **Phân tách Giao diện**<br/>(Interface Segregation Principle - ISP) | Không ép buộc các thành phần phụ thuộc vào các giao diện chứa phương thức mà chúng không có nhu cầu sử dụng. | Chia nhỏ thành 6 giao diện dịch vụ độc lập: `WalletService`, `TransactionService`, `TransferService`, `CategoryService`, `ReportService`, `DataManagementService`. Mỗi Controller tầng trình diễn chỉ phụ thuộc vào đúng giao diện cần dùng (ví dụ: `WalletGUIController` chỉ phụ thuộc `WalletService`). |
| **Nghịch đảo Phụ thuộc**<br/>(Dependency Inversion Principle - DIP) | Mô đun cấp cao không phụ thuộc mô đun cấp thấp; cả hai phụ thuộc vào sự trừu tượng hóa; trừu tượng hóa không phụ thuộc chi tiết cài đặt. | Tầng dịch vụ (`WalletServiceImpl`) không phụ thuộc vào lớp lưu tệp cụ thể (`JsonWalletRepository`) mà phụ thuộc vào interface `WalletRepository` của tầng nghiệp vụ. Cài đặt cụ thể được tiêm qua constructor, cho phép dễ dàng tráo đổi sang `MemoryWalletRepository` khi chạy unit test. |

**Phân tích đánh đổi kỹ thuật và giới hạn áp dụng:**

Việc tuân thủ nghiêm ngặt năm nguyên lý thiết kế giúp mã nguồn có độ ghép nối lỏng và dễ kiểm thử độc lập, nhưng đòi hỏi sự cân nhắc kỹ lưỡng về chi phí đánh đổi trong bối cảnh ứng dụng máy tính để bàn Java chạy cục bộ:

**Chi phí kỹ thuật phát sinh:**
- Gia tăng số lượng tệp mã nguồn: Việc phân tách giao diện và lớp cài đặt làm tăng số lượng tệp cần quản lý trong dự án (mỗi giao diện đều đi kèm ít nhất một lớp cài đặt tương ứng).
- Chi phí chuyển đổi dữ liệu giữa các tầng: Dữ liệu từ người dùng phải chuyển đổi qua lại nhiều lần từ DTO sang thực thể nghiệp vụ, rồi từ thực thể sang bản ghi tệp JSON làm tăng số lượng dòng mã trung gian.
- Đòi hỏi thời gian làm quen kiến trúc: Thành viên phát triển cần nắm vững mô hình phân tầng và cách tương tác qua giao diện thay vì lối viết mã kịch bản thủ tục đơn giản.

**Điểm cân bằng thực tế trong PFMS nhằm tránh thiết kế quá mức:**
- Tiêm phụ thuộc thủ công qua hàm khởi tạo: Hệ thống sử dụng cơ chế Dependency Injection thuần Java tại điểm khởi động chương trình (`Main`), không đưa các khung công nghệ nặng như Spring Framework hay Google Guice vào ứng dụng.
- Tái sử dụng giao diện kho lưu trữ cơ sở: Áp dụng `GenericRepository<E, ID>` với 5 phương thức CRUD chuẩn để loại bỏ trùng lặp mã nguồn, chỉ khai báo thêm các phương thức truy vấn thực sự cần thiết theo ca sử dụng.
- Tách biệt giao diện và cài đặt theo gói con: Đặt các lớp cài đặt dịch vụ công khai trong các gói con theo phân hệ thuộc `com.pfms.service.impl` để hỗ trợ tiêm phụ thuộc rõ ràng, chia sẻ tiện ích dùng chung qua `com.pfms.service.impl.common` (`EntityLookupSupport`, `RollbackSupport`), đồng thời giữ các lớp hỗ trợ và điều phối đặc thù trong từng phân hệ ở phạm vi `package-private` nhằm bảo vệ tính đóng gói.

## 6.2 Áp dụng các mẫu thiết kế trong hệ thống

Mục này đặc tả việc áp dụng các mẫu thiết kế hướng đối tượng vào cấu trúc phần mềm của hệ thống Quản lý Tài chính Cá nhân PFMS. Hệ thống chọn lọc bốn giải pháp cấu trúc và hành vi cốt lõi nhằm chuẩn hóa quá trình tạo đối tượng, đóng gói thuật toán kết xuất dữ liệu, kiểm soát vòng đời thực thể và phân tách sự kiện kiểm toán; loại trừ việc áp dụng hình thức các mẫu thiết kế gây phức tạp hóa không cần thiết cho ứng dụng.

Bốn mẫu thiết kế gồm Factory Method, Strategy, State và Observer được đặc tả chi tiết dưới đây theo cấu trúc đồng nhất: động lực áp dụng và bài toán thực tế, cấu trúc lớp và phân định vai trò, quy trình thực thi và lợi ích kỹ thuật thu được khi bảo trì hệ thống.

### 6.2.1 Mẫu Factory Method khởi tạo giao dịch

**Bài toán thực tế và động lực áp dụng:**

Trong hệ thống PFMS, các thực thể đơn lẻ như `Wallet` hay `Category` có cấu trúc khởi tạo đơn giản nên được tạo trực tiếp qua hàm khởi tạo (Constructor). Mẫu Factory Method được áp dụng chuyên biệt cho cây phân cấp `Transaction` nhằm giải quyết bài toán khởi tạo đa hình phức tạp:

- Phân tách cấu trúc đa hình: Hệ thống quản lý hai loại biến động tài chính có thuộc tính khác biệt gồm giao dịch thu chi tại một ví (`IncomeExpenseTransaction`) và giao dịch chuyển tiền giữa hai ví (`TransferTransaction`).
- Quản lý ràng buộc hợp thành sinh tử: Cả hai loại giao dịch đều có thể phát sinh chi phí giao dịch (`TransactionFee`). Bản ghi phí này phải được khởi tạo đồng thời và gắn liền với vòng đời của giao dịch gốc.
- Tránh ô nhiễm tầng dịch vụ: Nếu không có Factory, `TransactionServiceImpl` và `TransferServiceImpl` sẽ phải trực tiếp gọi lệnh `new` cho từng lớp con và tự xử lý logic tạo phí, khiến tầng dịch vụ bị ghép nối chặt với chi tiết cài đặt của tầng nghiệp vụ.

Lớp `TransactionFactory` được định vị tại tầng nghiệp vụ (`com.pfms.domain.model`) để đóng gói toàn bộ quy trình tiếp nhận tham số, khởi tạo đúng lớp con cụ thể kèm chi phí đi kèm, và trả về kiểu tham chiếu trừu tượng `Transaction`.

**Cấu trúc lớp và sơ đồ thiết kế:**

Sơ đồ lớp dưới đây mô tả cấu trúc lớp `TransactionFactory` cùng mối quan hệ giữa tầng dịch vụ và các lớp thực thể giao dịch:

![Sơ đồ lớp thiết kế TransactionFactory và các loại giao dịch](docs/object-oriented-design/images/so-do-lop-transaction-factory.png){width=80%}

**Cơ chế khởi tạo và giá trị kỹ thuật:**

Lớp `TransactionFactory` cung cấp các phương thức tĩnh nhằm chuẩn hóa quy trình sinh đối tượng và mang lại các giá trị kỹ thuật thực tế:

- Tự động đóng gói chi phí hợp thành: Khi tham số phí hợp lệ (`feeAmount > 0`), Factory tự động khởi tạo thể hiện `TransactionFee` liên kết và nạp vào thực thể giao dịch tương ứng; ngược lại, trường phí nhận giá trị rỗng. Toàn bộ logic ràng buộc này nằm trọn trong Factory, loại trừ nguy cơ phát sinh bản ghi phí mồ côi.
- Tách biệt trách nhiệm điều phối: tầng dịch vụ chỉ tập trung điều phối luồng nghiệp vụ ca sử dụng, hoàn toàn giải phóng khỏi chi tiết khởi tạo và ràng buộc cấu trúc của các lớp thực thể con.
- Khả năng mở rộng không xâm lấn: Khi hệ thống cần mở rộng loại giao dịch mới trong tương lai (như giao dịch định kỳ tự động), lập trình viên chỉ cần tạo lớp kế thừa mới từ `Transaction` và bổ sung phương thức tạo trên `TransactionFactory` mà không phải chỉnh sửa bất kỳ dòng mã nào trong tầng dịch vụ.
- Bảo đảm tính toàn vẹn dữ liệu: Mọi kiểm tra bất biến về số tiền dương, ngày phát sinh hợp lệ và danh mục phí đều được đóng gói tập trung tại Factory, bảo đảm mọi thực thể `Transaction` ra đời luôn ở trạng thái hoàn chỉnh và hợp lệ.

### 6.2.2 Mẫu Strategy định dạng báo cáo tài chính

**Bài toán thực tế và động lực áp dụng:**

Phân hệ Báo cáo (`REP`) chịu trách nhiệm tổng hợp dữ liệu thu chi theo kỳ quan sát (`UC-REP-01`) và phân tích cơ cấu thu chi theo nhóm nhu cầu và danh mục (`UC-REP-02`). Sau khi tầng dịch vụ tính toán xong số liệu và trả về các DTO phản hồi chuẩn (`CashFlowReportResponseDTO`, `SpendingStructureReportResponseDTO`), tầng trình diễn cần kết xuất kết quả theo nhiều định dạng khác nhau cho người dùng:
- Hiển thị trực quan trên giao diện đồ họa: Dữ liệu được định dạng thành bảng đồ họa `JTable`, có căn lề chuẩn và hiển thị dấu phân cách hàng nghìn.
- Kết xuất tệp phẳng bền vững: Dữ liệu được chuyển đổi thành cấu trúc JSON lưu xuống đĩa cục bộ phục vụ lưu trữ lâu dài hoặc tái sử dụng cho các công cụ phân tích khác.

Nếu bộ điều khiển giao diện (`ReportGUIController`) hoặc dịch vụ nghiệp vụ tự ôm toàn bộ logic định dạng cho từng đầu ra:
- tầng dịch vụ (`ReportServiceImpl`) bị vi phạm nguyên lý Đơn trách nhiệm nếu phải trộn lẫn logic tổng hợp tài chính với định dạng bảng hay xuất JSON, đồng thời làm rò rỉ chi tiết trình bày vào tầng ứng dụng.
- Mỗi khi cần bổ sung định dạng kết xuất mới (như định dạng CSV cho bảng tính) hoặc thay đổi bố cục hiển thị, lập trình viên buộc phải sửa mã điều khiển hoặc mã dịch vụ đang ổn định.

Giải pháp là tách hẳn trách nhiệm định dạng ra khỏi `ReportService` và áp dụng mẫu Strategy tại thành phần kết xuất thuộc tầng trình diễn (`ReportExporter`). Giao diện chiến lược `ReportExportStrategy` đóng gói các thuật toán định dạng độc lập dựa trên dữ liệu DTO mà `ReportService` trả về, cho phép tầng trình diễn tráo đổi định dạng kết xuất linh hoạt tại thời điểm chạy.

**Cấu trúc lớp và sơ đồ thiết kế:**

Sơ đồ lớp dưới đây mô tả cấu trúc tách biệt giữa `ReportServiceImpl` (tổng hợp số liệu thuần) và mẫu Strategy tại `ReportExporter` (kết xuất định dạng):

![Sơ đồ lớp thiết kế báo cáo và chiến lược xuất báo cáo](docs/object-oriented-design/images/so-do-lop-report-export-strategy.png){width=80%}

**Cơ chế chuyển đổi và giá trị kỹ thuật:**

Mẫu Strategy kết hợp với ranh giới tách biệt khỏi `ReportService` mang lại các giá trị kỹ thuật trực tiếp:

- Bảo toàn ranh giới sạch cho `ReportService`: `ReportServiceImpl` chỉ phụ thuộc ba kho lưu trữ (`TransactionRepository`, `CategoryRepository`, `NeedGroupRepository`) và trả về DTO phản hồi bất biến, không phụ thuộc vào giao diện hay chiến lược kết xuất.
- Tráo đổi định dạng tại thời điểm chạy: Bộ điều khiển tại tầng trình diễn (`presentation.controller`) nhận DTO từ `ReportService`, chọn chiến lược tương ứng (`TextTableReportStrategy` xuất văn bản dạng bảng hoặc `JsonReportStrategy` xuất JSON) cho `ReportExporter` để định dạng hiển thị hoặc xuất dữ liệu.
- Khả năng mở rộng không xâm lấn: Khi bổ sung định dạng xuất mới trong tương lai (như `CsvReportStrategy`), lập trình viên chỉ cần thêm một lớp cài đặt `ReportExportStrategy` mà không phải sửa bất kỳ dòng mã nào trong `ReportServiceImpl` hay `ReportExporter`.
- Năng lực kiểm thử độc lập: Quy trình tính toán tài chính trong `ReportServiceImpl` được kiểm thử độc lập trên DTO thuần, còn từng chiến lược kết xuất được kiểm thử chuỗi đầu ra riêng biệt theo đúng DTO đầu vào.

### 6.2.3 Mẫu State quản lý vòng đời ví tiền

**Bài toán thực tế và động lực áp dụng:**

Trong phân hệ Quản lý Ví (`WAL`), thực thể Ví tiền (`Wallet`) trải qua một vòng đời biến đổi trạng thái chặt chẽ gồm ba trạng thái: Đang hoạt động (`ACTIVE`), Đã lưu trữ (`ARCHIVED`) và Đã xóa (`DELETED`). Hành vi tài chính của ví thay đổi căn bản tùy theo trạng thái hiện tại:
- Trạng thái `ACTIVE`: Tiếp nhận bình thường mọi biến động dòng tiền gồm nạp tiền, chi tiêu, chuyển tiền đi và nhận tiền chuyển đến.
- Trạng thái `ARCHIVED`: Ví được tạm ngừng sử dụng, số dư vẫn được tính vào tổng tài sản của người dùng nhưng từ chối tuyệt đối mọi giao dịch thu chi hoặc điều chuyển tiền mới (`FR-WAL-03`). Ví chỉ cho phép thao tác kích hoạt lại (`activate`).
- Trạng thái `DELETED`: Ví đã bị xóa khỏi hệ thống, từ chối mọi thao tác nghiệp vụ và chuyển đổi.

Nếu quản lý trạng thái bằng biến cờ liệt kê (`enum`) thông thường:
- Các phương thức biến động số dư (`credit`, `debit`) và phương thức chuyển trạng thái của `Wallet` sẽ ngập tràn các khối lệnh kiểm tra điều kiện lặp lại: `if (this.status == ARCHIVED) throw ...; else if (this.status == DELETED) throw ...`.
- Logic kiểm soát vòng đời bị phân mảnh rải rác trong nhiều hàm; khi hệ thống bổ sung trạng thái mới (như tạm khóa để kiểm kê), lập trình viên buộc phải rà soát và sửa đổi toàn bộ các câu lệnh rẽ nhánh trong thực thể.

Giải pháp là áp dụng mẫu State: đóng gói các quy tắc hành vi của từng trạng thái vòng đời vào các lớp trạng thái độc lập kế thừa từ giao diện chung `WalletState`. thực thể `Wallet` giữ vai trò là ngữ cảnh (Context), ủy quyền toàn bộ các hành vi tài chính cho đối tượng trạng thái hiện tại xử lý thông qua tính đa hình.

**Cấu trúc lớp và sơ đồ thiết kế:**

Sơ đồ lớp dưới đây mô tả cấu trúc mẫu State áp dụng cho thực thể Ví tiền:

![Sơ đồ lớp thiết kế trạng thái ví (State Pattern)](docs/object-oriented-design/images/so-do-lop-wallet-state.png){width=80%}

**Cơ chế chuyển đổi và giá trị kỹ thuật:**

Mẫu State thiết lập cơ chế kiểm soát vòng đời chặt chẽ và an toàn cho nguồn tiền:

- Khóa thao tác không hợp lệ qua tính đa hình: Thay vì phải viết các câu lệnh điều kiện rẽ nhánh thủ công, mỗi lớp trạng thái tự kiểm soát các hành vi hợp lệ và từ chối các hành vi trái phép. Ví dụ: `ArchivedWalletState` tự động ném ngoại lệ nghiệp vụ khi phương thức `credit()` hoặc `debit()` được triệu gọi, bảo đảm ví đã lưu trữ không bao giờ bị phát sinh giao dịch mới ngoài ý muốn.
- Cô lập logic chuyển đổi trạng thái: Toàn bộ điều kiện tiên quyết để chuyển từ trạng thái này sang trạng thái khác (như từ `ACTIVE` sang `ARCHIVED` hoặc chuyển ngược lại về `ACTIVE`) được gói gọn bên trong các lớp trạng thái cụ thể thông qua lời gọi `wallet.setState(...)`. Trạng thái nội tại của ví luôn được bảo vệ toàn vẹn.
- Khả năng mở rộng trạng thái mới: Khi hệ thống cần bổ sung trạng thái vòng đời mới trong tương lai, lập trình viên chỉ cần tạo thêm một lớp mới cài đặt giao diện `WalletState` và định nghĩa các bước chuyển tương ứng mà không làm xáo trộn mã nguồn của các trạng thái đã chạy ổn định.

### 6.2.4 Mẫu Observer xử lý sự kiện giao dịch

**Bài toán thực tế và động lực áp dụng:**

Trong hệ thống PFMS, mỗi khi một giao dịch tài chính (thu chi hoặc điều chuyển tiền) được ghi nhận thành công, hệ thống cần lập tức kích hoạt hai tác vụ phụ trợ độc lập:
- Cập nhật số dư tức thời của ví: Phản ánh biến động tiền tệ lên trường `currentBalance` của các ví liên quan (cộng tiền vào ví thu, trừ tiền khỏi ví chi hoặc chuyển khoản đồng thời giữa hai ví theo quy tắc `BR-FIN-02`).
- Ghi nhận vết kiểm toán tài chính: Ghi lại thông tin giao dịch gồm mốc thời gian, loại giao dịch, số tiền biến động và mã định danh vào tệp `audit.log` phục vụ việc đối soát và khôi phục dữ liệu ở phân hệ `DAT`.

Nếu `TransactionServiceImpl` và `TransferServiceImpl` trực tiếp gọi tuần tự các dịch vụ phụ trợ:
- tầng dịch vụ bị ghép nối chặt với các hệ thống ghi log kiểm toán và dịch vụ cập nhật ví, dẫn đến hiện tượng dịch vụ bị phình to và ôm đồm quá nhiều trách nhiệm ngoài luồng nghiệp vụ chính.
- Khi hệ thống cần bổ sung thêm một hành động mới sau giao dịch trong tương lai (như kiểm tra đối soát số dư tức thời hoặc hiển thị thông báo ra màn hình), lập trình viên buộc phải can thiệp và sửa đổi trực tiếp mã nguồn của dịch vụ giao dịch.

Giải pháp là áp dụng mẫu Observer theo cơ chế hướng sự kiện nội bộ: dịch vụ giao dịch đóng vai trò là nguồn phát sự kiện; khi giao dịch được xác nhận thành công, dịch vụ chỉ phát ra sự kiện `TransactionCreatedEvent`. Các bộ lắng nghe đăng ký nhận sự kiện sẽ tự động đón bắt và thực thi tác vụ riêng biệt.

**Cấu trúc lớp và sơ đồ thiết kế:**

Sơ đồ lớp dưới đây mô tả cấu trúc mẫu Observer trong luồng xử lý sự kiện giao dịch:

![Sơ đồ lớp thiết kế phát và lắng nghe sự kiện giao dịch (Observer Pattern)](docs/object-oriented-design/images/so-do-lop-transaction-event-observer.png){width=80%}

**Cơ chế phát sự kiện và giá trị kỹ thuật:**

Mẫu Observer thiết lập cơ chế liên kết lỏng lẻo và mang lại các giá trị kiến trúc thực tế:

- Liên kết lỏng lẻo giữa nguồn phát và bên tiếp nhận: `TransactionServiceImpl` hoàn toàn không phụ thuộc vào chi tiết cài đặt của các bộ lắng nghe. Dịch vụ chỉ phát đi thông điệp `TransactionCreatedEvent` thông qua bộ điều phối `TransactionEventPublisher`, loại bỏ hoàn toàn các lời gọi hàm trực tiếp sang lớp kiểm toán.
- Phân tách luồng nghiệp vụ chính khỏi tác vụ phụ trợ: tầng dịch vụ chỉ tập trung vào việc xác thực và hạch toán dòng tiền của ca sử dụng. Toàn bộ logic ghi vết nhật ký tệp và tính toán số dư được chuyển giao sang các listener độc lập, giúp mã nguồn tầng dịch vụ luôn ngắn gọn và sáng rõ.
- Khả năng mở rộng không giới hạn: Khi hệ thống cần bổ sung tác vụ mới sau giao dịch (như tự động kiểm tra toàn vẹn tài chính sau mỗi lần chuyển khoản), lập trình viên chỉ cần tạo thêm lớp mới cài đặt giao diện `TransactionEventListener` và đăng ký vào bộ phát sự kiện mà không phải sửa đổi bất kỳ dòng mã nào trong `TransactionServiceImpl`.
- Độc lập kiểm thử: Năng lực kiểm thử đơn vị của `TransactionServiceImpl` được tối ưu hóa tối đa; kiểm thử viên có thể kiểm tra tính đúng đắn của quy trình giao dịch mà không cần khởi tạo hệ thống tệp ghi nhật ký kiểm toán thực tế.

\newpage

# 07. Thiết kế giao diện người dùng

Tài liệu này đặc tả thiết kế tầng trình diễn đồ họa (GUI) cho hệ thống Quản lý Tài chính Cá nhân (PFMS): cấu trúc gói giao diện, mô hình MVC hướng sự kiện trên Event Dispatch Thread (EDT), bố cục màn hình chính, biểu mẫu nhập liệu, xử lý ngoại lệ và danh mục đối tượng truyền dữ liệu (DTO) kết nối với tầng dịch vụ.

## 7.1 Cấu trúc tầng giao diện người dùng

Mục này trình bày cấu trúc phân rã các gói nội bộ của tầng trình diễn, bảng phân định trách nhiệm thành phần, vòng đời ứng dụng đồ họa và luồng xử lý sự kiện giao diện.

### 7.1.1 Nguyên lý phân tách giao diện và xử lý sự kiện

Tầng trình diễn tuân thủ nguyên lý phân tách trách nhiệm: các thành phần giao diện chỉ tương tác với người dùng qua chuột, bàn phím và màn hình đồ họa, hoàn toàn không chứa quy tắc tính toán số dư hay logic lưu trữ dữ liệu.

Mã nguồn tầng trình diễn nằm trong không gian tên `com.pfms.presentation`, gồm ba gói:

- Gói Khung nhìn (`com.pfms.presentation.view`): Chứa cửa sổ chính (`MainFrame`), thanh điều hướng bên (`SidebarPanel`), các bảng điều khiển phân hệ (`DashboardPanel`, `WalletPanel`, `TransactionPanel`, `TransferPanel`, `CategoryPanel`, `ReportPanel`, `DataManagementPanel`) và các hộp thoại nhập liệu chuyên biệt (`JDialog`).
- Gói Điều phối (`com.pfms.presentation.controller`): Chứa các bộ điều khiển giao diện (`MainController`, `WalletGUIController`, `TransactionGUIController`, `TransferGUIController`, `CategoryGUIController`, `ReportGUIController`, `DataManagementGUIController`). Các lớp này đăng ký lắng nghe sự kiện (`ActionListener`, `DocumentListener`), thực hiện kiểm tra sơ bộ dữ liệu biểu mẫu qua lớp tiện ích, đóng gói dữ liệu vào DTO yêu cầu và gọi phương thức tương ứng trên tầng dịch vụ qua giao diện trừu tượng.
- Gói Truyền Dữ liệu (`com.pfms.presentation.dto`): Chứa các đối tượng dữ liệu thô bất biến (`RequestDTO` và `ResponseDTO`) dùng để trao đổi dữ liệu hai chiều giữa tầng trình diễn và tầng dịch vụ.

![Sơ đồ Kiến trúc Tầng trình diễn](docs/object-oriented-design/images/so-do-7.png){width=80%}

Bảng phân định trách nhiệm nội bộ tầng trình diễn:

| Thành phần | Trách nhiệm cho phép | Ranh giới cấm đoán |
| :--- | :--- | :--- |
| **Gói Khung nhìn**<br/>(`presentation.view`) | Xây dựng bố cục giao diện đồ họa, hiển thị bảng dữ liệu `JTable`, thu thập dữ liệu thô từ các ô nhập liệu, kích hoạt hộp thoại xác nhận hoặc thông báo kết quả. | Không tính toán số dư, không kiểm tra quy tắc nghiệp vụ tài chính, không gọi trực tiếp tầng dịch vụ, tầng nghiệp vụ hay kho lưu trữ dữ liệu. |
| **Gói Điều phối**<br/>(`presentation.controller`) | Nhận sự kiện từ giao diện người dùng, điều hướng màn hình hiển thị qua `CardLayout`, đóng gói tham số vào DTO yêu cầu, gọi Giao diện tầng dịch vụ, nhận DTO phản hồi và cập nhật lại mô hình dữ liệu (`TableModel`) của Khung nhìn. | Không tính toán tài chính, không sửa đổi số liệu nghiệp vụ, không trực tiếp thao tác các thành phần đồ họa cấp thấp, không phụ thuộc vào lớp cài đặt cụ thể của dịch vụ hay kho lưu trữ. |
| **Gói Truyền Dữ liệu**<br/>(`presentation.dto`) | Đóng gói tham số nhập thành `RequestDTO`, nhận kết quả ca sử dụng thành `ResponseDTO`, bảo đảm tính bất biến của dữ liệu trung chuyển giữa GUI và Service. | Không chứa logic tính toán tài chính hoặc quy tắc nghiệp vụ, không tham chiếu thực thể miền (`Domain Entity`), không sửa đổi dữ liệu sau khi khởi tạo. |

Lợi ích kỹ thuật của kiến trúc phân tách:
1. Độc lập công nghệ hiển thị: tầng dịch vụ và tầng nghiệp vụ phía sau hoàn toàn không phụ thuộc vào công nghệ giao diện đồ họa cụ thể, cho phép kiểm thử tự động 100% bằng JUnit 5 qua các đối tượng DTO.
2. Dễ bảo trì và nâng cấp: Việc thay đổi bố cục màu sắc, phông chữ hoặc cấu trúc màn hình đồ họa chỉ giới hạn trong gói `view` và `controller`, không gây ảnh hưởng dây chuyền đến các tầng còn lại.

### 7.1.2 Vòng đời ứng dụng đồ họa và luồng bắt sự kiện

Ứng dụng đồ họa PFMS vận hành trên nền tảng luồng giao diện người dùng chuyên biệt Event Dispatch Thread (EDT), bảo đảm tính an toàn luồng và giao diện mượt mà:

- Giai đoạn khởi động: Nạp cấu hình hệ thống, khởi tạo các dịch vụ nghiệp vụ và hiển thị cửa sổ chính `MainFrame` trên luồng giao diện người dùng EDT.
- Giai đoạn xử lý sự kiện: Người dùng tương tác bằng chuột hoặc phím trên các nút bấm (`JButton`), bảng dữ liệu (`JTable`) hoặc ô nhập liệu (`JTextField`). Bộ điều khiển tiếp nhận sự kiện, xác thực dữ liệu qua `InputValidator`, đóng gói DTO và ủy quyền thực thi cho tầng dịch vụ.
- Giai đoạn cập nhật khung nhìn: Sau khi tầng dịch vụ xử lý thành công, bộ điều khiển nhận `ResponseDTO`, chuyển đổi thành dữ liệu hiển thị và cập nhật lại `DefaultTableModel` trên luồng EDT. Nếu phát sinh lỗi nghiệp vụ hoặc ngoại lệ hệ thống, bộ điều khiển hiển thị hộp thoại cảnh báo `JOptionPane.showMessageDialog` với thông điệp rõ ràng bằng tiếng Việt.

Sơ đồ tuần tự xử lý sự kiện giao diện:

![Luồng xử lý sự kiện giao diện đồ họa khi tạo ví mới](docs/object-oriented-design/images/so-do-tuan-tu-su-kien-gui-tao-vi.png){width=80%}

## 7.2 Bố cục cửa sổ chính và màn hình phân hệ

Mục này xác lập cấu trúc bố cục phân vùng của cửa sổ chính `MainFrame`, bảng ánh xạ từ các nút điều hướng màn hình đến ca sử dụng nghiệp vụ và quy chuẩn hiển thị bảng dữ liệu `JTable`.

### 7.2.1 Cấu trúc phân vùng cửa sổ chính

Cửa sổ chính `MainFrame` áp dụng bố cục vùng `BorderLayout` chia thành ba phân khu trực quan:

- Phân khu điều hướng bên trái (`BorderLayout.WEST`): Chiếm chiều rộng cố định 220 pixel, chứa biểu trưng ứng dụng PFMS và danh sách các nút điều hướng phân hệ xếp theo chiều dọc (`GridLayout`).
- Phân khu nội dung trung tâm (`BorderLayout.CENTER`): Sử dụng cơ chế quản lý bố cục thẻ (`CardLayout`) để chuyển đổi linh hoạt giữa bảy màn hình phân hệ mà không phải mở nhiều cửa sổ con độc lập.
- Phân khu trạng thái phía dưới (`BorderLayout.SOUTH`): Hiển thị dòng trạng thái gồm đường dẫn tệp dữ liệu đang làm việc, thời gian đồng bộ gần nhất và tổng số dư khả dụng toàn hệ thống.

Sơ đồ phân vùng không gian cửa sổ chính:

![Sơ đồ phân vùng không gian cửa sổ chính](docs/object-oriented-design/images/so-do-7.png){width=80%}

### 7.2.2 Ánh xạ màn hình chức năng và ca sử dụng

Bảng ánh xạ chi tiết các thao tác giao diện người dùng đến từng ca sử dụng nghiệp vụ:

| Màn hình phân hệ | Nút bấm thao tác | Tên thao tác nghiệp vụ | Mã ca sử dụng | Controller điều phối |
| :--- | :--- | :--- | :--- | :--- |
| **Tổng quan** | Xem tự động | Nạp tổng quan tài sản và giao dịch gần đây | `UC-WAL-02`, `UC-TXN-02` | `MainController` |
| **Quản lý Ví** | Thêm ví | Mở hộp thoại nhập tên và ghi chú tạo ví mới | `UC-WAL-01` | `WalletGUIController` |
| **Quản lý Ví** | Sửa ví | Mở hộp thoại cập nhật tên và ghi chú ví | `UC-WAL-03` | `WalletGUIController` |
| **Quản lý Ví** | Điều chỉnh số dư | Mở hộp thoại kiểm kê số dư thực tế | `UC-WAL-04` | `WalletGUIController` |
| **Quản lý Ví** | Lưu trữ / Kích hoạt | Chuyển đổi trạng thái hoạt động của ví | `UC-WAL-03` | `WalletGUIController` |
| **Ghi nhận Thu Chi** | Thêm giao dịch | Mở hộp thoại ghi nhận thu hoặc chi | `UC-TXN-01` | `TransactionGUIController` |
| **Ghi nhận Thu Chi** | Lọc dữ liệu | Lọc danh sách giao dịch theo ví, ngày, loại | `UC-TXN-02` | `TransactionGUIController` |
| **Ghi nhận Thu Chi** | Sửa giao dịch | Mở hộp thoại sửa thông tin giao dịch | `UC-TXN-03` | `TransactionGUIController` |
| **Ghi nhận Thu Chi** | Xóa giao dịch | Xóa giao dịch và hoàn nguyên số dư ví | `UC-TXN-04` | `TransactionGUIController` |
| **Chuyển tiền Nội bộ** | Chuyển tiền | Mở hộp thoại chuyển tiền giữa hai ví | `UC-TRF-01` | `TransferGUIController` |
| **Chuyển tiền Nội bộ** | Xem lịch sử | Nạp danh sách các lần chuyển tiền | `UC-TRF-02` | `TransferGUIController` |
| **Chuyển tiền Nội bộ** | Hủy chuyển tiền | Hủy giao dịch chuyển và hoàn trả số dư | `UC-TRF-03` | `TransferGUIController` |
| **Quản lý Danh mục** | Thêm danh mục | Mở hộp thoại tạo danh mục con cấp 3 | `UC-CAT-01` | `CategoryGUIController` |
| **Quản lý Danh mục** | Sửa danh mục | Mở hộp thoại đổi tên hoặc chuyển nhóm cha | `UC-CAT-02` | `CategoryGUIController` |
| **Quản lý Danh mục** | Xóa danh mục | Xóa mềm danh mục thu chi | `UC-CAT-03` | `CategoryGUIController` |
| **Quản lý Danh mục** | Xem cây danh mục | Hiển thị cấu trúc cây 3 cấp bằng JTree | `UC-CAT-04` | `CategoryGUIController` |
| **Báo cáo Thống kê** | Báo cáo dòng tiền | Biểu diễn chuỗi dòng tiền 12 tháng liên tục | `UC-REP-01` | `ReportGUIController` |
| **Báo cáo Thống kê** | Cơ cấu thu chi | Phân tích cơ cấu và tỷ trọng thu chi theo nhóm nhu cầu và danh mục | `UC-REP-02` | `ReportGUIController` |
| **Quản trị Dữ liệu** | Xuất sao lưu | Mở hộp thoại chọn thư mục lưu dữ liệu sao lưu | `UC-DAT-01` | `DataManagementGUIController` |
| **Quản trị Dữ liệu** | Phục hồi dữ liệu | Mở hộp thoại chọn tệp JSON để phục hồi | `UC-DAT-02` | `DataManagementGUIController` |
| **Quản trị Dữ liệu** | Kiểm tra đối soát | Kích hoạt quy trình đối soát toàn vẹn | `UC-DAT-03` | `DataManagementGUIController` |

### 7.2.3 Quy chuẩn hiển thị bảng dữ liệu và hộp thoại

Giao diện đồ họa PFMS áp dụng chuẩn kết xuất bảng dữ liệu `JTable` hiện đại thông qua các bộ vẽ ô tùy biến (`Custom TableCellRenderer`):

1. Căn lề dữ liệu theo ngữ nghĩa: Căn trái cho dữ liệu chuỗi văn bản, tên ví, tên danh mục và ghi chú; căn giữa cho mã định danh, ngày tháng và trạng thái; căn phải cho số tiền và giá trị số học.
2. Định dạng số tiền và màu sắc trực quan: Số tiền được định dạng theo mẫu phân tách hàng nghìn chuẩn Việt Nam (`#,##0 VNĐ`). Các khoản thu nhập được hiển thị với màu xanh lá cây kèm tiền tố dấu cộng, các khoản chi tiêu hiển thị với màu đỏ hoặc cam kèm tiền tố dấu trừ.
3. Hộp thoại nhập liệu modal (`JDialog`): Mọi thao tác thêm mới hoặc sửa đổi dữ liệu đều mở cửa sổ hộp thoại có tính năng khóa màn hình cha (`setModal(true)`), giúp người dùng tập trung hoàn thành tác vụ nhập liệu trước khi quay lại màn hình chính.

Mẫu giao diện bảng danh sách ví:

| # | Mã ví | Tên ví | Đơn vị | Số dư khả dụng | Trạng thái |
| :---: | :---: | :--- | :---: | ---: | :---: |
| 1 | WAL-000001 | Tiền mặt sinh hoạt | VND | 4,500,000 VNĐ | Hoạt động |
| 2 | WAL-000002 | Tài khoản MB Bank | VND | 28,350,000 VNĐ | Hoạt động |
| 3 | WAL-000003 | Quỹ dự phòng sự cố | VND | 50,000,000 VNĐ | Hoạt động |

## 7.3 Thiết kế biểu mẫu nhập liệu và bắt sự kiện

Mục này đặc tả lớp tiện ích kiểm tra tính hợp lệ của dữ liệu biểu mẫu đồ họa, xác lập cơ chế phản hồi lỗi tại chỗ và xử lý xác nhận an toàn cho các tác vụ nhạy cảm.

### 7.3.1 Xác thực biểu mẫu đồ họa

Toàn bộ dữ liệu nhập từ các trường `JTextField`, `JComboBox` và `JTextArea` ban đầu đều ở dạng chuỗi ký tự thô. Để bảo vệ tầng dịch vụ khỏi dữ liệu rác, hệ thống thiết kế lớp tiện ích `InputValidator` đặt trong gói `com.pfms.presentation.util`.

Lớp `InputValidator` thực hiện kiểm tra dữ liệu trước khi bộ điều khiển tạo DTO:
- Kiểm tra chuỗi bắt buộc: Kiểm tra độ dài và loại bỏ khoảng trắng thừa hai đầu chuỗi, bảo đảm các trường tên ví, tên giao dịch không bị để trống.
- Kiểm tra định dạng ngày tháng: Hỗ trợ chuyển đổi chuỗi ngày tháng từ định dạng thân thiện Việt Nam (`dd/MM/yyyy`) hoặc chuẩn quốc tế (`yyyy-MM-dd`) sang đối tượng `java.time.LocalDate`.
- Kiểm tra giá trị tiền tệ: Kiểm tra chuỗi số hợp lệ, từ chối số âm hoặc số 0 đối với số tiền giao dịch, chuyển đổi an toàn sang `java.math.BigDecimal` với quy tắc làm tròn rõ ràng.

Bảng đặc tả các phương thức xác thực của `InputValidator`:

| Phương thức | Tham số đầu vào | Kiểu trả về | Ràng buộc và Quy tắc Xác thực |
| :--- | :--- | :--- | :--- |
| `validateNonEmpty(String text, String fieldName)` | Chuỗi nhập, Tên trường | `String` | Chuỗi không được rỗng sau khi cắt tỉa; ném ngoại lệ kiểm tra kèm tên trường nếu vi phạm. |
| `validatePositiveAmount(String text, String fieldName)` | Chuỗi nhập, Tên trường | `BigDecimal` | Chuỗi phải là số học hợp lệ; giá trị phải lớn hơn 0; không vượt quá 100 tỷ VNĐ. |
| `validateNonNegativeAmount(String text, String fieldName)` | Chuỗi nhập, Tên trường | `BigDecimal` | Dùng cho số dư khởi điểm hoặc kiểm kê số dư: giá trị phải lớn hơn hoặc bằng 0. |
| `validateDate(String text, String fieldName)` | Chuỗi nhập, Tên trường | `LocalDate` | Định dạng nhập `dd/MM/yyyy` hoặc `yyyy-MM-dd`; kiểm tra ngày tồn tại thực tế trên lịch. |
| `validateTextLength(String text, int maxLength, String fieldName)` | Chuỗi nhập, Độ dài tối đa, Tên trường | `String` | Độ dài chuỗi sau cắt tỉa không vượt quá ngưỡng cho phép (tên ví tối đa 50 ký tự, ghi chú 255 ký tự). |

### 7.3.2 Phản hồi người dùng và hộp thoại xác nhận

Nhằm bảo đảm trải nghiệm sử dụng an toàn và tuân thủ nghiêm ngặt yêu cầu phi chức năng về khả năng sử dụng:

1. Hộp thoại xác nhận thao tác nhạy cảm: Đối với các thao tác có nguy cơ làm thay đổi cấu trúc dữ liệu hoặc tác động tài chính lớn (xóa ví, xóa giao dịch, hủy chuyển tiền, khôi phục sao lưu dữ liệu), hệ thống bắt buộc mở hộp thoại xác nhận `JOptionPane.showConfirmDialog` với mức cảnh báo `WARNING_MESSAGE`. Thao tác chỉ được thực thi khi người dùng nhấn nút đồng ý (`JOptionPane.YES_OPTION`).
2. Thông báo lỗi chuẩn hóa và thân thiện: Khi phát sinh lỗi vi phạm quy tắc tài chính từ tầng dịch vụ (như số dư không đủ, tên ví bị trùng lặp), bộ điều khiển bắt giữ ngoại lệ và hiển thị hộp thoại `JOptionPane.showMessageDialog` với mã lỗi nghiệp vụ chuẩn hóa (`ERR-...`) kèm hướng dẫn xử lý cụ thể, không để lộ vết ngăn xếp lỗi hệ thống ra màn hình đồ họa.

## 7.4 Thiết kế các đối tượng truyền dữ liệu (DTO)

Mục này đặc tả cấu trúc các đối tượng truyền dữ liệu đóng vai trò cầu nối thông tin giữa tầng trình diễn và tầng dịch vụ, xác lập tính đóng gói bất biến và danh mục các lớp dữ liệu phục vụ các ca sử dụng.

### 7.4.1 Mục đích và tính đóng gói của đối tượng truyền dữ liệu

Trong kiến trúc phân tầng của PFMS, việc phân tách rạch ròi giữa các thành phần giao diện người dùng và mô hình nghiệp vụ cốt lõi được bảo đảm thông qua mẫu thiết kế đối tượng truyền dữ liệu (DTO).

Mục đích kỹ thuật của mẫu thiết kế DTO:
- Ngăn chặn rò rỉ thực thể miền: tầng trình diễn đồ họa không tiếp xúc trực tiếp với các thực thể miền (`Wallet`, `Transaction`, `Category`), loại bỏ nguy cơ giao diện tự ý gọi các phương thức thay đổi trạng thái số dư mà không thông qua dịch vụ điều phối.
- Tối ưu hóa dữ liệu hiển thị: DTO chỉ chắt lọc các trường dữ liệu thô (chuỗi, số, ngày tháng) mà các bảng `JTable` hoặc biểu mẫu nhập liệu cần sử dụng.
- Độc lập tiến hóa mã nguồn: Cho phép cấu trúc giao diện đồ họa có thể thay đổi linh hoạt mà không làm ảnh hưởng đến cấu trúc lưu trữ và quy tắc nghiệp vụ tầng miền.

Nguyên tắc đóng gói và thiết kế bất biến:
- Cấu trúc dữ liệu thuần túy: Mỗi lớp DTO chỉ bao gồm các trường dữ liệu riêng tư (`private`), hàm khởi tạo đầy đủ tham số và các phương thức lấy giá trị (`getter`), hoàn toàn không chứa logic tính toán tài chính.
- Tính bất biến của dữ liệu: Mọi trường thuộc tính trong DTO được khai báo với từ khóa `final` để bảo đảm dữ liệu không bị thay đổi trong quá trình trung chuyển giữa các tầng.
- Phân tách hai chiều: Hệ thống phân định thành hai nhóm độc lập gồm DTO yêu cầu (`RequestDTO` gửi từ GUI xuống Dịch vụ) và DTO phản hồi (`ResponseDTO` trả về từ Dịch vụ lên GUI).

### 7.4.2 Danh mục các lớp truyền dữ liệu chi tiết

Toàn bộ các lớp DTO trình diễn trong hệ thống được đặt trong gói `com.pfms.presentation.dto` (tách biệt với lớp đóng gói nội bộ `BackupPayload` thuộc `com.pfms.service.impl.data`) và phân định thành hai nhóm chính gồm DTO yêu cầu và DTO phản hồi:

Bảng Danh mục các Lớp DTO yêu cầu:

| Tên lớp DTO yêu cầu | Danh sách Thuộc tính và Kiểu dữ liệu | Mô tả Mục đích Dữ liệu |
| :--- | :--- | :--- |
| **Phân hệ Quản lý Ví (WAL)** |  |  |
| `CreateWalletRequestDTO` | `name: String, note: String` | Dữ liệu tạo ví tài chính mới (tên ví và ghi chú). |
| `UpdateWalletRequestDTO` | `walletId: UUID, name: String, note: String` | Dữ liệu cập nhật tên và ghi chú ví, bảo toàn số dư hiện có. |
| `AdjustBalanceRequestDTO` | `walletId: UUID, actualBalance: BigDecimal, reason: String, adjustedAt: LocalDate` | Dữ liệu kiểm kê số dư thực tế (mã ví, số dư quan sát, lý do, ngày kiểm kê). |
| **Phân hệ Ghi nhận Thu Chi (TXN)** |  |  |
| `RecordTransactionRequestDTO` | `walletId: UUID, categoryId: UUID, amount: BigDecimal, transactionDate: LocalDate, note: String, feeAmount: BigDecimal, feeCategoryId: UUID` | Dữ liệu ghi nhận giao dịch thu chi kèm phụ phí và danh mục phí liên kết. |
| `UpdateTransactionRequestDTO` | `transactionId: UUID, newWalletId: UUID, newAmount: BigDecimal, newDate: LocalDate, newCategoryId: UUID, newNote: String, newFeeAmount: BigDecimal, newFeeCategoryId: UUID` | Dữ liệu cập nhật giao dịch thu chi (ví mới, số tiền, ngày, danh mục, ghi chú, phụ phí). |
| `FilterTransactionsRequestDTO` | `startDate: LocalDate, endDate: LocalDate, walletIds: Set<UUID>, transactionType: String, categoryIds: Set<UUID>, keyword: String, pageNumber: int, pageSize: int` | Bộ tiêu chí lọc sổ giao dịch (khoảng ngày, tập ví, loại thu chi, danh mục, từ khóa) và phân trang. |
| **Phân hệ Chuyển tiền Nội bộ (TRF)** |  |  |
| `TransferFundsRequestDTO` | `sourceWalletId: UUID, destinationWalletId: UUID, amount: BigDecimal, transferDate: LocalDate, note: String, feeAmount: BigDecimal, feeCategoryId: UUID` | Dữ liệu chuyển tiền giữa hai ví kèm phụ phí và danh mục phí liên kết. |
| `FilterTransfersRequestDTO` | `startDate: LocalDate, endDate: LocalDate, sourceWalletId: UUID, destinationWalletId: UUID, pageNumber: int, pageSize: int` | Tiêu chí lọc lịch sử chuyển tiền (khoảng ngày, ví gửi, ví nhận) và phân trang. |
| **Phân hệ Quản lý Danh mục (CAT)** |  |  |
| `CreateNeedGroupRequestDTO` | `transactionType: String, name: String, icon: String, color: String` | Dữ liệu tạo nhóm nhu cầu cấp 2 (loại thu chi, tên, biểu tượng, mã màu). |
| `RenameNeedGroupRequestDTO` | `needGroupId: UUID, name: String, icon: String, color: String` | Dữ liệu đổi tên hoặc cập nhật biểu tượng, mã màu nhóm nhu cầu cấp 2. |
| `CreateCategoryRequestDTO` | `parentGroupId: UUID, name: String` | Dữ liệu tạo danh mục chi tiết cấp 3 thuộc nhóm nhu cầu cha. |
| `RenameCategoryRequestDTO` | `categoryId: UUID, name: String, newParentGroupId: UUID` | Dữ liệu đổi tên hoặc chuyển nhóm nhu cầu cha của danh mục cấp 3. |
| **Phân hệ Báo cáo Thống kê (REP)** |  |  |
| `CashFlowReportRequestDTO` | `endMonth: int, endYear: int` | Mốc tháng và năm kết thúc để truy vấn chuỗi dòng tiền 12 tháng liên tục. |
| `SpendingStructureReportRequestDTO` | `startDate: LocalDate, endDate: LocalDate, transactionType: String` | Khoảng ngày và loại luồng tiền (`EXPENSE`/`INCOME`) để phân tích cơ cấu tỷ trọng 2 cấp. |
| **Phân hệ Quản trị Dữ liệu (DAT)** |  |  |
| `ExportBackupRequestDTO` | `destinationPath: String, scope: String` | Đường dẫn thư mục đích và phạm vi sao lưu (`FULL` hoặc `TRANSACTIONS`). |
| `RestoreBackupRequestDTO` | `backupFilePath: String` | Đường dẫn tệp sao lưu JSON để phục hồi dữ liệu (All-or-Nothing). |

Bảng Danh mục các Lớp DTO phản hồi:

| Tên lớp DTO phản hồi | Danh sách Thuộc tính và Kiểu dữ liệu | Mô tả Dữ liệu Kết xuất |
| :--- | :--- | :--- |
| **Phân hệ Quản lý Ví (WAL)** |  |  |
| `WalletResponseDTO` | `walletId: UUID, name: String, balance: BigDecimal, currency: String, defaultWallet: boolean, status: String, note: String, createdAt: LocalDateTime` | Thông tin chi tiết một ví tài chính phục vụ hiển thị kết quả hoặc dòng bảng `JTable`. |
| `WalletsSummaryResponseDTO` | `wallets: List<WalletResponseDTO>, totalBalance: BigDecimal, currency: String, walletShares: Map<UUID, BigDecimal>` | Bảng tổng hợp các ví khả dụng, tổng số dư toàn hệ thống, đơn vị tiền tệ và tỷ trọng phân bổ (%). |
| `BalanceAdjustmentResponseDTO` | `recordId: UUID, walletId: UUID, previousBalance: BigDecimal, actualBalance: BigDecimal, difference: BigDecimal, currency: String, reason: String, adjustedAt: LocalDate, createdAt: LocalDateTime` | Kết quả điều chỉnh số dư: mã bản ghi, số dư trước/sau, chênh lệch và ngày kiểm kê lưu vết kiểm toán. |
| **Phân hệ Ghi nhận Thu Chi (TXN)** |  |  |
| `TransactionResponseDTO` | `transactionId: UUID, walletId: UUID, walletName: String, categoryId: UUID, categoryName: String, amount: BigDecimal, transactionType: String, transactionDate: LocalDate, note: String, hasFee: boolean, feeAmount: BigDecimal, feeCategoryId: UUID, feeCategoryName: String, walletBalanceAfter: BigDecimal, createdAt: LocalDateTime` | Kết xuất chi tiết giao dịch thu chi sau khi tạo/cập nhật, kèm chi tiết phụ phí và số dư ví sau biến động. |
| `TransactionListResponseDTO` | `items: List<TransactionResponseDTO>, totalIncome: BigDecimal, totalExpense: BigDecimal, netCashFlow: BigDecimal, pageNumber: int, pageSize: int, totalRecords: long, totalPages: int` | Danh sách phân trang giao dịch thu chi kèm 3 chỉ số tổng hợp (thu nhập, chi tiêu gồm phí, dòng tiền thuần). |
| **Phân hệ Chuyển tiền Nội bộ (TRF)** |  |  |
| `TransferResponseDTO` | `transferId: UUID, sourceWalletId: UUID, sourceWalletName: String, destinationWalletId: UUID, destinationWalletName: String, amount: BigDecimal, transferDate: LocalDate, note: String, feeAmount: BigDecimal, feeCategoryId: UUID, feeCategoryName: String, sourceBalanceAfter: BigDecimal, destinationBalanceAfter: BigDecimal, createdAt: LocalDateTime` | Kết quả chuyển khoản nguyên tử: mã giao dịch, cặp ví, số tiền chuyển, phụ phí và số dư cập nhật hai ví. |
| `TransferListResponseDTO` | `items: List<TransferResponseDTO>, totalTransferredAmount: BigDecimal, totalFees: BigDecimal, pageNumber: int, pageSize: int, totalRecords: long, totalPages: int` | Danh sách phân trang lịch sử chuyển tiền nội bộ kèm tổng tiền điều chuyển và tổng phụ phí phát sinh. |
| **Phân hệ Quản lý Danh mục (CAT)** |  |  |
| `NeedGroupResponseDTO` | `needGroupId: UUID, name: String, transactionType: String, status: String, icon: String, color: String, createdAt: LocalDateTime` | Thông tin chi tiết nhóm nhu cầu cấp 2 (trạng thái, biểu tượng, mã màu, thời điểm tạo). |
| `CategoryResponseDTO` | `categoryId: UUID, name: String, parentGroupId: UUID, parentGroupName: String, transactionType: String, status: String, createdAt: LocalDateTime` | Thông tin chi tiết danh mục cấp 3 kèm nhóm cha và loại thu chi hiển thị trên bảng/hộp chọn. |
| `CategoryHierarchyResponseDTO` | `transactionType: String, needGroupId: UUID, needGroupName: String, needGroupStatus: String, categories: List<CategoryResponseDTO>` | Cấu trúc phân cấp 3 cấp theo từng nhóm nhu cầu kèm danh sách danh mục con trực thuộc (`JTree`). |
| **Phân hệ Báo cáo Thống kê (REP)** |  |  |
| `MonthlyCashFlowResponseDTO` | `yearMonth: String, totalIncome: BigDecimal, totalExpense: BigDecimal, netCashFlow: BigDecimal, savingsRate: BigDecimal` | Dữ liệu dòng tiền một tháng: tổng thu, tổng chi gồm phí, dòng tiền thuần và tỷ lệ tiết kiệm. |
| `CashFlowReportResponseDTO` | `months: List<MonthlyCashFlowResponseDTO>, avgIncome: BigDecimal, avgExpense: BigDecimal, avgNetCashFlow: BigDecimal` | Bảng tổng hợp chuỗi 12 tháng liên tục kèm các chỉ số bình quân năm. |
| `CategoryShareResponseDTO` | `categoryId: UUID, name: String, status: String, totalAmount: BigDecimal, share: BigDecimal, shareInGroup: BigDecimal, previousTotalAmount: BigDecimal, differenceAmount: BigDecimal, percentageChange: BigDecimal, isNew: boolean` | Số liệu danh mục cấp 3: tổng tiền, tỷ trọng (`share`, `shareInGroup`), trạng thái và biến động kỳ liền kề (`isNew`). |
| `NeedGroupShareResponseDTO` | `needGroupId: UUID, name: String, status: String, totalAmount: BigDecimal, share: BigDecimal, previousTotalAmount: BigDecimal, differenceAmount: BigDecimal, percentageChange: BigDecimal, isNew: boolean, categories: List<CategoryShareResponseDTO>` | Số liệu nhóm nhu cầu cấp 2: tổng tiền, tỷ trọng toàn kỳ, trạng thái, biến động kỳ liền kề (`isNew`) và danh mục con. |
| `SpendingStructureReportResponseDTO` | `transactionType: String, startDate: LocalDate, endDate: LocalDate, totalAmount: BigDecimal, previousTotalAmount: BigDecimal, differenceAmount: BigDecimal, percentageChange: BigDecimal, isNew: boolean, groups: List<NeedGroupShareResponseDTO>` | Báo cáo cơ cấu thu chi 2 cấp hoàn chỉnh, đối chiếu biến động so với kỳ liền kề và danh sách nhóm nhu cầu. |
| **Phân hệ Quản trị Dữ liệu (DAT)** |  |  |
| `ExportBackupResponseDTO` | `filePath: String, fileSizeBytes: long, exportedAt: LocalDateTime, scope: String, walletCount: int, categoryCount: int, transactionCount: int, adjustmentCount: int` | Kết quả xuất sao lưu: đường dẫn tệp, kích thước (byte), mốc thời gian, phạm vi và số lượng bản ghi. |
| `RestoreBackupResponseDTO` | `success: boolean, message: String, restoredAt: LocalDateTime, restoredWallets: int, restoredCategories: int, restoredTransactions: int, restoredAdjustments: int` | Kết quả phục hồi dữ liệu: trạng thái thành công, thông điệp, thời điểm hoàn tất và số lượng bản ghi phục hồi. |
| `ReconciliationIssueResponseDTO` | `severity: String, ruleCode: String, issueType: String, message: String, affectedEntityId: String, recommendation: String` | Chi tiết từng sai lệch hoặc bất thường phát hiện trong quá trình đối soát toàn vẹn. |
| `ReconciliationReportResponseDTO` | `walletBalanceConsistent: boolean, transferConsistent: boolean, categoryHierarchyConsistent: boolean, feeConsistent: boolean, scannedWallets: int, scannedTransactions: int, scannedCategories: int, scannedAdjustments: int, issues: List<ReconciliationIssueResponseDTO>` | Báo cáo đối soát độc lập 4 điều kiện toàn vẹn, số lượng bản ghi quét và danh sách cảnh báo sai lệch. |

\newpage

# 08. Thiết kế xử lý ngoại lệ và nhật ký

Tài liệu này đặc tả cơ chế kiểm soát ngoại lệ và từ điển mã lỗi chuẩn hóa cho hệ thống Quản lý Tài chính Cá nhân (PFMS): phân tách rạch ròi ngoại lệ nghiệp vụ và ngoại lệ kỹ thuật, cơ chế bọc lỗi xuyên tầng (Exception Chaining), cây phân cấp ngoại lệ kế thừa `RuntimeException` và bảo vệ an toàn giao diện đồ họa (GUI) trước các sự cố dữ liệu.

## 8.1 Chiến lược quản lý ngoại lệ toàn hệ thống

Mục này xác lập nguyên tắc quản lý sai số và kiểm soát ngoại lệ xuyên suốt bốn tầng kiến trúc của hệ thống PFMS. Nội dung định hình cơ chế phân loại lỗi, căn cứ lựa chọn kiểu ngoại lệ trong Java và quy tắc bọc lỗi bảo đảm tính toàn vẹn của dữ liệu tài chính.

### 8.1.1 Phân loại ngoại lệ kỹ thuật và ngoại lệ nghiệp vụ

Hệ thống phân tách toàn bộ các tình huống bất thường thành hai nhóm ngoại lệ:

| Tiêu chí | Ngoại lệ nghiệp vụ (Business Exception) | Ngoại lệ kỹ thuật (Technical Exception) |
| :--- | :--- | :--- |
| **Bản chất lỗi** | Vi phạm quy tắc tài chính do thao tác người dùng (ví dụ: không đủ số dư, vượt hạn mức). | Sự cố hệ thống, đọc ghi tệp đĩa cứng hoặc hỏng dữ liệu lưu trữ. |
| **Nơi phát sinh** | Tầng nghiệp vụ cốt lõi (`domain`) và tầng dịch vụ (`service`). | Tầng hạ tầng lưu trữ (`infrastructure`). |
| **Xử lý hiển thị** | Trích xuất thông điệp tiếng Việt thân thiện lên hộp thoại giao diện người dùng. | Thông báo lỗi hệ thống và an toàn giữ nguyên màn hình làm việc. |

Toàn bộ các lớp ngoại lệ của hệ thống đều kế thừa từ `java.lang.RuntimeException` (Unchecked Exception) nhằm:
- Loại bỏ từ khóa `throws` cồng kềnh trên chữ ký phương thức, tránh rò rỉ chi tiết cài đặt giữa các tầng.
- Đảm bảo tính đóng gói của Clean Architecture: tầng trình diễn và tầng dịch vụ giao tiếp qua Interface mà không bị ràng buộc bởi ngoại lệ kiểm tra (Checked Exception).
- Phù hợp với cơ chế tương tác giao diện đồ họa: khi có lỗi, giao dịch bị hủy và bộ điều khiển hiển thị hộp thoại cảnh báo `JOptionPane` mà không làm sập giao diện.

### 8.1.2 Nguyên tắc bắt và chuyển đổi ngoại lệ xuyên tầng

Hệ thống áp dụng kỹ thuật bọc ngoại lệ (*Exception Chaining*) để bảo vệ ranh giới phân tầng. Khi phát sinh lỗi kỹ thuật ở tầng dưới, tầng đó không để rò rỉ chi tiết công nghệ (như `java.io.IOException`) lên các tầng trên mà phải đóng gói vào một ngoại lệ có ngữ nghĩa phù hợp, lưu giữ tham chiếu nguyên nhân gốc qua hàm khởi tạo `super(message, cause)`.

Bảng phân định trách nhiệm bắt và ném ngoại lệ giữa bốn tầng kiến trúc:

| Tầng kiến trúc | Trách nhiệm ném ngoại lệ | Trách nhiệm bắt và chuyển đổi ngoại lệ |
| :--- | :--- | :--- |
| **Tầng trình diễn**<br/>(`com.pfms.presentation`) | Ném ngoại lệ định dạng đầu vào khi chuỗi nhập liệu sai cú pháp. | Điểm chốt chặn cuối cùng: Bắt toàn bộ ngoại lệ tại Controller, hiển thị `JOptionPane` thân thiện và giữ an toàn màn hình. |
| **Tầng dịch vụ**<br/>(`com.pfms.service`) | Ném ngoại lệ nghiệp vụ kèm mã lỗi khi điều kiện ca sử dụng không thỏa mãn. | Không nuốt lỗi; cho phép ngoại lệ từ tầng nghiệp vụ và tầng hạ tầng lan truyền tự nhiên lên tầng trình diễn. |
| **Tầng nghiệp vụ cốt lõi**<br/>(`com.pfms.domain`) | Ném ngoại lệ nghiệp vụ ngay tại thực thể khi vi phạm ràng buộc số dư hoặc ngân sách. | Không bắt giữ lỗi; hoàn toàn độc lập với thư viện I/O hay giao diện đồ họa. |
| **Tầng hạ tầng lưu trữ**<br/>(`com.pfms.infrastructure`) | Ném ngoại lệ hạ tầng (`StorageException`). | Bắt giữ toàn bộ `IOException`, bọc nguyên nhân gốc vào `StorageException` trước khi ném ra ngoài. |

## 8.2 Cây phân cấp lớp ngoại lệ hệ thống

Mục này thiết lập cấu trúc phân cấp các lớp ngoại lệ theo nguyên lý kế thừa và đa hình trong lập trình hướng đối tượng, cung cấp khung bẫy lỗi có cấu trúc và khả năng mở rộng cho toàn bộ ứng dụng.

### 8.2.1 Sơ đồ phân cấp lớp ngoại lệ hướng đối tượng

Cây phân cấp ngoại lệ của hệ thống bắt đầu từ lớp trừu tượng gốc `PfmsException`, kế thừa trực tiếp từ `java.lang.RuntimeException`. Cấu trúc được phân nhánh thành hai họ ngoại lệ độc lập gồm ngoại lệ nghiệp vụ và ngoại lệ kỹ thuật:

![Sơ đồ phân cấp lớp ngoại lệ](docs/object-oriented-design/images/so-do-phan-cap-lop-ngoai-le.png){width=80%}

Đặc điểm kiến trúc của cây phân cấp:
- Lớp cơ sở `PfmsException` đóng gói thuộc tính `errorCode` (mã định danh nghiệp vụ) và `timestamp` (thời điểm phát sinh lỗi UTC), bảo đảm mọi ngoại lệ khi được ném ra đều mang đầy đủ ngữ cảnh phục vụ việc hiển thị và kiểm toán.
- Lớp `BusinessException` là lớp ngoại lệ cụ thể (*concrete class*), đóng vai trò ngoại lệ nghiệp vụ chung (*General Business Exception*). Lớp này có thể được khởi tạo trực tiếp với `ErrorCode` (`new BusinessException(errorCode)`) cho các vi phạm nghiệp vụ tiêu chuẩn mà không đòi hỏi thuộc tính mở rộng, đồng thời làm lớp cha cho các ngoại lệ nghiệp vụ chuyên biệt mang trạng thái mở rộng (`InsufficientBalanceException`, `DuplicateEntityException`, `EntityNotFoundException`). Nhánh này không mang theo tham chiếu `Throwable cause` kỹ thuật.
- Nhánh `TechnicalException` luôn bắt buộc tiếp nhận tham chiếu `Throwable cause` qua hàm khởi tạo để bảo lưu trọn vẹn vết ngăn xếp gốc của hệ thống máy ảo Java.

### 8.2.2 Danh mục các lớp ngoại lệ nghiệp vụ chi tiết

Bảng dưới đây đặc tả chi tiết toàn bộ các lớp ngoại lệ cụ thể trong hệ thống PFMS, làm căn cứ cài đặt mã nguồn Java cho gói `com.pfms.domain.exception`:

| Tên lớp ngoại lệ | Lớp cha trực tiếp | Tham số hàm khởi tạo | Điều kiện kích hoạt nghiệp vụ |
| :--- | :--- | :--- | :--- |
| **`InsufficientBalanceException`** | `BusinessException` | `ErrorCode code, String walletId, Money balance, Money amount` | Ném ra khi số tiền rút hoặc chuyển khoản vượt quá số dư khả dụng của ví nguồn. |
| **`EntityNotFoundException`** | `BusinessException` | `ErrorCode code, String entityType, String identifier` | Ném ra khi tra cứu thực thể (Ví, Danh mục, Giao dịch) theo định danh nhưng không tồn tại. |
| **`DuplicateEntityException`** | `BusinessException` | `ErrorCode code, String entityType, String name` | Ném ra khi tạo mới hoặc cập nhật Ví, Danh mục bị trùng tên trong cùng phạm vi duy nhất. |
| **`StorageException`** | `TechnicalException` | `ErrorCode code, String message, Throwable cause` | Ném ra tại tầng hạ tầng khi xảy ra lỗi I/O đọc ghi tệp JSON, bọc nguyên nhân gốc `java.io.IOException`. |
| **`DataCorruptionException`** | `TechnicalException` | `ErrorCode code, String fileName, int line, Throwable cause` | Ném ra khi tệp lưu trữ JSON bị hỏng cấu trúc, thiếu trường bắt buộc hoặc sai định dạng. |

## 8.3 Bảng mã lỗi nghiệp vụ chuẩn hóa

Mục này xác lập hệ thống mã định danh lỗi chuẩn hóa cho PFMS, bảo đảm tính duy nhất toàn cục, hỗ trợ việc đối chiếu hai chiều giữa tài liệu yêu cầu, giao diện người dùng và mã nguồn Java.

### 8.3.1 Cấu trúc định danh mã lỗi hệ thống

Toàn bộ các mã lỗi trong hệ thống được định nghĩa qua kiểu liệt kê `com.pfms.domain.exception.ErrorCode` theo cấu trúc:

`ERR-[PHÂN_HỆ]-[SỐ_THỨ_TỰ]`

Quy ước các thành phần định danh:
- Tiền tố `ERR`: Ký hiệu cố định biểu thị mã lỗi của hệ thống.
- Tiền tố quy tắc: Gồm 3 ký tự in hoa đại diện cho miền quy tắc nghiệp vụ hoặc phân hệ phát sinh lỗi, kế thừa trực tiếp từ hồ sơ yêu cầu SRS.
- Số thứ tự: Gồm 3 chữ số từ `001` đến `999` đại diện cho nguyên nhân lỗi cụ thể.

Bảng phân nhóm tiền tố mã lỗi chuẩn hóa hệ thống:

| Tiền tố | Miền quy tắc nghiệp vụ | Phạm vi trách nhiệm kỹ thuật |
| :--- | :--- | :--- |
| **`FIN`** | Quy tắc tài chính chung | Lỗi số tiền giao dịch không hợp lệ nhỏ hơn hoặc bằng không, lỗi làm tròn số học tiền tệ. |
| **`NAM`** | Định danh và tính duy nhất | Lỗi tên để trống, tên dài quá 50 ký tự hoặc trùng tên Ví, trùng tên Danh mục. |
| **`WAL`** | Ràng buộc số dư ví | Lỗi số dư khả dụng không đủ chi trả, thao tác xuất quỹ làm âm số dư ví. |
| **`TRF`** | Chuyển khoản nội bộ | Lỗi ví nguồn trùng ví đích, số tiền chuyển không hợp lệ. |
| **`FEE`** | Chi phí giao dịch | Lỗi chi phí âm, giao dịch có chi phí nhưng chưa chọn danh mục chi tiêu cho phí. |
| **`CAT`** | Cây danh mục ba cấp | Lỗi đổi loại khi đã có giao dịch, xóa nhóm cuối cùng, thao tác trên nhóm đã xóa. |
| **`REP`** | Báo cáo và thống kê | Lỗi thiếu tham số yêu cầu, mốc tháng hoặc năm ngoài miền hợp lệ, ngày bắt đầu sau ngày kết thúc hoặc loại luồng tiền ngoài `EXPENSE` và `INCOME`. |
| **`DAT`** | Quản trị và sao lưu tệp | Lỗi cấu trúc tệp sao lưu JSON, vi phạm liên kết chuyển tiền hoặc phí mồ côi khi phục hồi. |
| **`SYS`** | Hạ tầng hệ thống | Lỗi I/O đọc ghi tệp cục bộ trên đĩa cứng hệ thống. |

### 8.3.2 Danh mục mã lỗi và thông điệp phản hồi

Bảng dưới đây xác lập từ điển mã lỗi chuẩn hóa của hệ thống PFMS, bảo đảm tính truy vết hai chiều nhất quán với tài liệu Yêu cầu SRS, đồng thời định rõ lớp ngoại lệ Java tương ứng và thông điệp hiển thị trên giao diện đồ họa:

| Mã lỗi Enum | Lớp ngoại lệ Java phụ trách | Nguyên nhân kích hoạt nghiệp vụ | Thông điệp phản hồi giao diện |
| :--- | :--- | :--- | :--- |
| **`ERR_FIN_001`** | `BusinessException` | Dữ liệu số tiền không đúng định dạng số học hoặc nhỏ hơn hoặc bằng 0. | Dữ liệu số tiền không đúng định dạng số học hợp lệ. |
| **`ERR_FIN_002`** | `BusinessException` | Phép toán chia không xác định quy tắc làm tròn gây số thập phân vô hạn. | Lỗi số học: Phép toán chia không xác định quy tắc làm tròn. |
| **`ERR_NAM_001`** | `BusinessException` | Tên để trống sau khi cắt khoảng trắng, hoặc dài quá 50 ký tự. | Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu. |
| **`ERR_NAM_002`** | `DuplicateEntityException` | Tên ví hoặc tên danh mục bị trùng lặp trong cùng phạm vi duy nhất. | Tên đã tồn tại trong hệ thống. Vui lòng chọn tên khác. |
| **`ERR_WAL_001`** | `InsufficientBalanceException` | Giao dịch làm số dư ví âm hoặc không đủ thanh toán số tiền và chi phí. | Giao dịch bị từ chối: Số dư ví không đủ để thực hiện thao tác. |
| **`ERR_TRF_001`** | `BusinessException` | Ví nguồn chuyển tiền trùng với ví đích nhận tiền. | Giao dịch không hợp lệ: Ví chuyển và ví nhận phải khác nhau. |
| **`ERR_TRF_002`** | `BusinessException` | Số tiền yêu cầu chuyển khoản nhỏ hơn hoặc bằng 0. | Số tiền chuyển phải là một số dương lớn hơn 0. |
| **`ERR_FEE_001`** | `BusinessException` | Chi phí giao dịch được nhập giá trị nhỏ hơn 0. | Chi phí không hợp lệ. |
| **`ERR_FEE_002`** | `BusinessException` | Chi phí lớn hơn 0 nhưng chưa chọn danh mục chi tiêu cho khoản phí. | Vui lòng chọn danh mục cho phí giao dịch. |
| **`ERR_FEE_003`** | `BusinessException` | Danh mục phí đã bị xóa hoặc không thuộc loại chi tiêu (`EXPENSE`). | Danh mục phí không hợp lệ hoặc không tương thích với loại chi tiêu. |
| **`ERR_CAT_001`** | `BusinessException` | Chuyển danh mục sang nhóm khác loại, hoặc đổi loại khi đã có giao dịch. | Không được phép đổi loại thu chi hoặc chuyển danh mục sang nhóm khác loại. |
| **`ERR_CAT_002`** | `BusinessException` | Thực hiện xóa nhóm nhu cầu đang dùng cuối cùng của một loại thu chi. | Không thể xóa nhóm cuối cùng của loại thu chi này. |
| **`ERR_CAT_003`** | `BusinessException` | Chuyển danh mục chi tiết sang một nhóm nhu cầu đã bị xóa. | Không thể chuyển danh mục sang nhóm đã xóa. |
| **`ERR_CAT_004`** | `BusinessException` | Khôi phục danh mục khi nhóm nhu cầu của nó còn ở trạng thái đã xóa. | Nhóm chứa danh mục này đang ở trạng thái đã xóa. Hãy khôi phục nhóm trước. |
| **`ERR_CAT_005`** | `BusinessException` | Tạo danh mục mới vào một nhóm nhu cầu đang ở trạng thái đã xóa. | Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng. |
| **`ERR_REP_001`** | `BusinessException` | Thiếu tham số bắt buộc, `endMonth` ngoài `[1, 12]`, `endYear < 2000`, `startDate > endDate` hoặc `transactionType` khác `EXPENSE` và `INCOME`. | Tham số thời gian hoặc loại báo cáo không hợp lệ. |
| **`ERR_DAT_001`** | `DataCorruptionException` | Tệp sao lưu JSON sai cấu trúc schema hoặc vi phạm phân cấp 3 cấp. | Tệp sao lưu không hợp lệ hoặc cấu trúc dữ liệu JSON bị hỏng. |
| **`ERR_DAT_002`** | `DataCorruptionException` | Tệp sao lưu JSON vi phạm liên kết chuyển tiền hoặc làm âm số dư ví. | Dữ liệu sao lưu vi phạm ràng buộc toàn vẹn số dư ví hoặc dòng tiền. |
| **`ERR_SYS_001`** | `StorageException` | Lỗi I/O đọc ghi tệp cục bộ trên đĩa cứng hệ thống. | Lỗi hệ thống: Không thể truy xuất hoặc lưu trữ tệp dữ liệu cục bộ. |
| **`ERR_NOT_FOUND`** | `EntityNotFoundException` | Tra cứu thực thể (Ví, Danh mục, Giao dịch) theo định danh nhưng không tồn tại trong hệ thống. | Không tìm thấy thực thể yêu cầu trong hệ thống. |

\newpage

# 09. Ma trận truy vết thiết kế

Tài liệu này xác lập ma trận truy vết hai chiều giữa mô hình thiết kế hướng đối tượng với yêu cầu chức năng (SRS) và quy tắc nghiệp vụ của hệ thống Quản lý Tài chính Cá nhân (PFMS). Bản ma trận chứng minh độ bao phủ 100% của 19 ca sử dụng qua 4 tầng kiến trúc và kiểm toán chéo việc cài đặt các quy tắc nghiệp vụ bất biến, loại trừ hoàn toàn lớp thiết kế mồ côi hoặc yêu cầu bị bỏ sót.

## 9.1 Ánh xạ yêu cầu chức năng sang lớp thiết kế

Mục này xác lập liên kết hai chiều giữa yêu cầu chức năng và các lớp thiết kế qua 4 tầng kiến trúc:
1. **Chiều xuôi**: Đảm bảo mỗi ca sử dụng được hiện thực hóa trọn vẹn qua chuỗi phối hợp: tầng trình diễn → tầng dịch vụ → tầng nghiệp vụ → tầng hạ tầng lưu trữ.
2. **Chiều ngược**: Đảm bảo mỗi lớp thiết kế đều bắt nguồn từ một ca sử dụng hoặc quy tắc nghiệp vụ thực tế, loại trừ hoàn toàn các lớp mồ côi.

Bảng ma trận dưới đây phân định trách nhiệm hiện thực hóa của các ca sử dụng xuyên suốt bốn tầng kiến trúc và chỉ rõ mẫu thiết kế được áp dụng để tối ưu hóa tính mở rộng:

| Mã ca sử dụng | Tên ca sử dụng | Tầng trình diễn | Tầng dịch vụ | Tầng nghiệp vụ | Tầng hạ tầng | Mẫu thiết kế áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Nhóm Quản lý Ví** |  |  |  |  |  |  |
| `UC-WAL-01` | Tạo ví mới | `WalletGUIController` | `WalletService` | `Wallet`, `WalletState` | `WalletRepository` | `State` |
| `UC-WAL-02` | Xem tổng hợp số dư và cơ cấu ví | `WalletGUIController`, `MainController` | `WalletService` | `Wallet`, `Money` | `WalletRepository`, `TransactionRepository` | `Observer` |
| `UC-WAL-03` | Cập nhật thông tin ví | `WalletGUIController` | `WalletService` | `Wallet` | `WalletRepository` | `State` |
| `UC-WAL-04` | Điều chỉnh số dư ví | `WalletGUIController` | `WalletService` | `Wallet`, `BalanceAdjustmentRecord` | `WalletRepository`, `BalanceAdjustmentRepository` | Không áp dụng |
| **Nhóm Ghi nhận Thu Chi** |  |  |  |  |  |  |
| `UC-TXN-01` | Ghi nhận giao dịch thu chi | `TransactionGUIController` | `TransactionService` | `Transaction`, `IncomeExpenseTransaction`, `TransactionFee`, `TransactionFactory`, `Wallet` | `TransactionRepository`, `WalletRepository`, `CategoryRepository` | `Factory Method`, `Observer` |
| `UC-TXN-02` | Tra cứu và lọc lịch sử giao dịch thu chi | `TransactionGUIController`, `MainController` | `TransactionService` | `Transaction`, `Category` | `TransactionRepository` | Không áp dụng |
| `UC-TXN-03` | Chỉnh sửa giao dịch thu chi | `TransactionGUIController` | `TransactionService` | `Transaction`, `Wallet` | `TransactionRepository`, `WalletRepository` | `Observer` |
| `UC-TXN-04` | Xóa giao dịch thu chi | `TransactionGUIController` | `TransactionService` | `Transaction`, `Wallet` | `TransactionRepository`, `WalletRepository` | `Observer` |
| **Nhóm Chuyển tiền Nội bộ** |  |  |  |  |  |  |
| `UC-TRF-01` | Chuyển tiền nội bộ giữa hai ví | `TransferGUIController` | `TransferService` | `TransferTransaction`, `Wallet` | `TransactionRepository`, `WalletRepository`, `CategoryRepository` | `Observer` |
| `UC-TRF-02` | Tra cứu lịch sử chuyển tiền | `TransferGUIController` | `TransferService` | `TransferTransaction` | `TransactionRepository`, `WalletRepository` | Không áp dụng |
| `UC-TRF-03` | Xóa giao dịch chuyển tiền | `TransferGUIController` | `TransferService` | `TransferTransaction`, `Wallet` | `TransactionRepository`, `WalletRepository` | `Observer` |
| **Nhóm Quản lý Danh mục** |  |  |  |  |  |  |
| `UC-CAT-01` | Bổ sung danh mục phân loại mới | `CategoryGUIController` | `CategoryService` | `Category`, `NeedGroup` | `CategoryRepository`, `NeedGroupRepository` | Không áp dụng |
| `UC-CAT-02` | Đổi tên danh mục | `CategoryGUIController` | `CategoryService` | `Category` | `CategoryRepository` | Không áp dụng |
| `UC-CAT-03` | Khôi phục hoặc tạo lại mục đã xóa | `CategoryGUIController` | `CategoryService` | `Category` | `CategoryRepository` | Không áp dụng |
| `UC-CAT-04` | Xóa danh mục hoặc nhóm | `CategoryGUIController` | `CategoryService` | `Category`, `Transaction` | `CategoryRepository`, `TransactionRepository` | Không áp dụng |
| **Nhóm Báo cáo và Thống kê** |  |  |  |  |  |  |
| `UC-REP-01` | Thống kê dòng tiền 12 tháng gần nhất | `ReportGUIController` | `ReportService` | `Transaction`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `NetCashFlow`, `SavingsRate` | `TransactionRepository` | `Strategy (ReportExporter tại tầng trình diễn)` |
| `UC-REP-02` | Phân tích cơ cấu thu chi theo nhóm và danh mục | `ReportGUIController` | `ReportService` | `Transaction`, `IncomeExpenseTransaction`, `TransferTransaction`, `TransactionFee`, `Category`, `NeedGroup`, `SpendingStructureShare` | `TransactionRepository`, `NeedGroupRepository`, `CategoryRepository` | `Strategy (ReportExporter tại tầng trình diễn)` |
| **Nhóm Quản trị Dữ liệu** |  |  |  |  |  |  |
| `UC-DAT-01` | Xuất dữ liệu tài chính | `DataManagementGUIController` | `DataManagementService` | Toàn bộ thực thể hệ thống | Toàn bộ kho lưu trữ | Không áp dụng |
| `UC-DAT-02` | Phục hồi dữ liệu hệ thống từ bản sao lưu | `DataManagementGUIController` | `DataManagementService` | Toàn bộ thực thể hệ thống | Toàn bộ kho lưu trữ | Không áp dụng |
| `UC-DAT-03` | Kiểm tra lại dữ liệu hệ thống | `DataManagementGUIController` | `DataManagementService` | `Wallet`, `Transaction`, `TransferTransaction` | Toàn bộ kho lưu trữ | Không áp dụng |

## 9.2 Đánh giá độ bao phủ và tính nhất quán thiết kế

Mục này đối chiếu việc cài đặt các quy tắc tài chính bất biến và quy tắc định danh vào các lớp thiết kế chuyên trách, bảo đảm tính toàn vẹn dữ liệu và độ chính xác số học:

| Mã quy tắc | Tên quy tắc nghiệp vụ | Lớp thiết kế chịu trách nhiệm | Phương thức thực thi chính | Cơ chế kiểm soát kỹ thuật |
| :--- | :--- | :--- | :--- | :--- |
| `BR-FIN-01` | Độ chính xác tài chính | `Money`, `NetCashFlow`, `SavingsRate`, `SpendingStructureShare` | `of`, `add`, `subtract`, `multiply`, `divide` | Sử dụng `BigDecimal` quy tắc làm tròn ngân hàng (`HALF_EVEN`), tiền làm tròn 2 chữ số, tỷ lệ giữ tối thiểu 4 chữ số thập phân trung gian và bảo vệ mẫu số bằng không. |
| `BR-FIN-02` | Bảo toàn số dư ví | `Wallet`, `WalletServiceImpl` | `debit`, `hasSufficientBalance`, `adjustBalance` | Kiểm soát số dư khả dụng không âm đối với ví tiền mặt, tính toán lại số dư từ dòng tiền và ghi vết kiểm kê. |
| `BR-FIN-03` | Ghi nhận đồng thời khi chuyển tiền | `TransferTransaction`, `TransferServiceImpl` | `applyToWallet`, `revertFromWallet`, `transferFunds`, `deleteTransfer` | Cập nhật nguyên tử đồng thời hai ví nguồn và ví đích, hoàn nguyên cả hai ví và kiểm tra số dư ví đích khi xóa. |
| `BR-FIN-04` | Phân cấp danh mục thu chi ba cấp | `Category`, `NeedGroup`, `CategoryServiceImpl` | `createCategory`, `renameCategory`, `restoreCategory`, `deleteCategory`, `deleteNeedGroup`, `changeNeedGroup`, `restore` | Duy trì cây phân cấp ba cấp, cấm đổi loại khi đã có giao dịch, thực thi xóa mềm để bảo toàn lịch sử dữ liệu. |
| `BR-NAM-01` | Quy tắc định danh và tên duy nhất | `EntityName`, `WalletServiceImpl`, `CategoryServiceImpl` | `of`, `matches`, `existsByName` | Cắt khoảng trắng hai đầu, giới hạn từ 1 đến 50 ký tự, kiểm tra tính duy nhất không phân biệt hoa thường. |

\newpage

# 10. Tổng kết

Giai đoạn Thiết kế Hướng đối tượng đã hoàn tất việc chuyển đổi toàn diện các yêu cầu nghiệp vụ của Hệ thống Quản lý Tài chính Cá nhân PFMS thành mô hình kiến trúc phần mềm bốn tầng hoàn chỉnh trên nền tảng ngôn ngữ Java. Hệ thống các bản vẽ thiết kế đã định hình rõ ràng ranh giới và trách nhiệm của từng thành phần, bao gồm tầng trình diễn đồ họa, tầng dịch vụ điều phối, tầng nghiệp vụ với các lớp thực thể và đối tượng giá trị bất biến, tầng hạ tầng lưu trữ tệp, cùng việc áp dụng các mẫu thiết kế hướng đối tượng nhằm tối ưu hóa tính mở rộng và bảo trì.

Kết quả kiểm toán ma trận truy vết xác nhận toàn bộ các ca sử dụng của hệ thống đều được hiện thực hóa trọn vẹn qua bốn tầng kiến trúc, không tồn tại lớp thiết kế mồ côi hay yêu cầu nghiệp vụ nào bị bỏ sót. Các quy tắc tài chính bất biến cốt lõi gồm tính toán số học chính xác, kiểm soát số dư ví không âm, tính nguyên tử trong chuyển tiền nội bộ, cấu trúc phân cấp danh mục ba cấp và tính duy nhất của định danh đều đã được chỉ định vị trí kiểm soát chặt chẽ tại các phương thức chuyên trách.

Hồ sơ thiết kế này là căn cứ kỹ thuật đường cơ sở để đội ngũ phát triển bước vào giai đoạn cài đặt mã nguồn. Quá trình lập trình được triển khai theo nguyên tắc từ dưới lên: ưu tiên xây dựng các đối tượng giá trị và cây ngoại lệ, tiếp tục với các thực thể miền và quy tắc nghiệp vụ, cài đặt tầng dịch vụ và kho lưu trữ, cuối cùng hoàn thiện tầng trình diễn giao diện đồ họa. Toàn bộ các lớp nghiệp vụ được kiểm thử tự động bằng các bộ kiểm thử đơn vị nhằm chứng minh độ chính xác trước khi ghép nối hệ thống hoàn chỉnh.


# 11. Tài liệu tham khảo

- Ambler, S. W. (2004). *The object primer: Agile model-driven development with UML 2.0* (3rd ed.). Cambridge University Press.
- Bloch, J. (2018). *Effective Java* (3rd ed.). Addison-Wesley Professional.
- Bray, T. (Ed.). (2017). *The JavaScript Object Notation (JSON) data interchange format* (RFC 8259). Internet Engineering Task Force. https://www.rfc-editor.org/rfc/rfc8259
- Evans, E. (2003). *Domain-driven design: Tackling complexity in the heart of software*. Addison-Wesley Professional.
- Fowler, M. (2002). *Patterns of enterprise application architecture*. Addison-Wesley Professional.
- Fowler, M. (2019). *Refactoring: Improving the design of existing code* (2nd ed.). Addison-Wesley Professional.
- Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design patterns: Elements of reusable object-oriented software*. Addison-Wesley Professional.
- International Organization for Standardization. (2019). *Date and time: Representations for information interchange. Part 1: Basic rules* (ISO 8601-1:2019).
- International Organization for Standardization & International Electrotechnical Commission. (2012a). *Information technology: Object Management Group Unified Modeling Language (OMG UML). Part 1: Infrastructure* (ISO/IEC 19505-1:2012).
- International Organization for Standardization & International Electrotechnical Commission. (2012b). *Information technology: Object Management Group Unified Modeling Language (OMG UML). Part 2: Superstructure* (ISO/IEC 19505-2:2012).
- International Organization for Standardization & International Electrotechnical Commission. (2023). *Systems and software engineering: Systems and software quality requirements and evaluation (SQuaRE): Product quality model* (ISO/IEC 25010:2023).
- International Organization for Standardization, International Electrotechnical Commission, & Institute of Electrical and Electronics Engineers. (2018). *Systems and software engineering: Life cycle processes: Requirements engineering* (ISO/IEC/IEEE 29148:2018).
- Larman, C. (2004). *Applying UML and patterns: An introduction to object-oriented analysis and design and iterative development* (3rd ed.). Prentice Hall.
- Martin, R. C. (2018). *Clean architecture: A craftsman's guide to software structure and design*. Prentice Hall.
- Shafranovich, Y. (2005). *Common format and MIME type for comma-separated values (CSV) files* (RFC 4180). Internet Engineering Task Force. https://www.rfc-editor.org/rfc/rfc4180
- Yergeau, F. (2003). *UTF-8, a transformation format of ISO 10646* (RFC 3629). Internet Engineering Task Force. https://www.rfc-editor.org/rfc/rfc3629
