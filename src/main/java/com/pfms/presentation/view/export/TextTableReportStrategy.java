package com.pfms.presentation.view.export;

import com.pfms.common.util.Preconditions;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.CategoryShareResponseDTO;
import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Chiến lược kết xuất báo cáo tài chính sang định dạng bảng văn bản trực quan.
 * Định dạng tiền tệ phân tách hàng nghìn (#,##0 VNĐ), tỷ lệ phần trăm (0.00%) và nhãn trạng thái.
 */
public final class TextTableReportStrategy implements ReportExportStrategy {

    // region Constants
    private static final DecimalFormat MONEY_FORMAT;
    private static final DecimalFormat PERCENT_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        MONEY_FORMAT = new DecimalFormat("#,##0", symbols);
        PERCENT_FORMAT = new DecimalFormat("0.00", symbols);
    }
    // endregion

    // region Operations
    @Override
    public String exportCashFlow(CashFlowReportResponseDTO report) {
        Preconditions.checkArgument(report != null, "CashFlowReportResponseDTO không được để null");

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                                BÁO CÁO CHUỖI DÒNG TIỀN                                 \n");
        sb.append("========================================================================================\n");
        sb.append(String.format("| %-8s | %-20s | %-20s | %-20s | %-15s |\n",
                "Tháng", "Thu nhập", "Chi tiêu", "Dòng tiền thuần", "Tỷ lệ tiết kiệm"));
        sb.append("|----------|----------------------|----------------------|----------------------|-----------------|\n");

        if (report.getMonths() != null && !report.getMonths().isEmpty()) {
            for (MonthlyCashFlowResponseDTO month : report.getMonths()) {
                sb.append(String.format("| %-8s | %20s | %20s | %20s | %15s |\n",
                        month.getYearMonth(),
                        formatMoney(month.getTotalIncome()),
                        formatMoney(month.getTotalExpense()),
                        formatMoney(month.getNetCashFlow()),
                        formatPercent(month.getSavingsRate())));
            }
        } else {
            sb.append("|                               (Không có dữ liệu tháng nào)                             |\n");
        }

        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("Bình quân thu:            %s\n", formatMoney(report.getAvgIncome())));
        sb.append(String.format("Bình quân chi:            %s\n", formatMoney(report.getAvgExpense())));
        sb.append(String.format("Bình quân dòng tiền thuần: %s\n", formatMoney(report.getAvgNetCashFlow())));
        sb.append("========================================================================================\n");

        return sb.toString();
    }

    @Override
    public String exportSpendingStructure(SpendingStructureReportResponseDTO report) {
        Preconditions.checkArgument(report != null, "SpendingStructureReportResponseDTO không được để null");

        String typeLabel = "EXPENSE".equalsIgnoreCase(report.getTransactionType()) ? "CHI TIÊU" : "THU NHẬP";

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================================\n");
        sb.append("                                      BÁO CÁO CƠ CẤU THU CHI                                        \n");
        sb.append("====================================================================================================\n");
        sb.append(String.format("Loại luồng giao dịch: %s\n", typeLabel));
        sb.append(String.format("Từ ngày: %s đến ngày: %s\n", report.getStartDate(), report.getEndDate()));
        sb.append(String.format("Tổng số tiền: %s\n", formatMoney(report.getTotalAmount())));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format("| %-35s | %-18s | %-12s | %-14s | %-10s |\n",
                "Nhóm nhu cầu / Danh mục", "Số tiền", "Tỷ trọng kỳ", "Tỷ trọng nhóm", "Trạng thái"));
        sb.append("|-------------------------------------|--------------------|--------------|----------------|------------|\n");

        if (report.getGroups() != null && !report.getGroups().isEmpty()) {
            for (NeedGroupShareResponseDTO group : report.getGroups()) {
                String groupName = group.getName();
                if ("DELETED".equalsIgnoreCase(group.getStatus())) {
                    groupName += " (đã xóa)";
                }
                if (group.isNew()) {
                    groupName += " [Mới]";
                }

                sb.append(String.format("| [Nhóm] %-28s | %18s | %12s | %14s | %-10s |\n",
                        truncate(groupName, 28),
                        formatMoney(group.getTotalAmount()),
                        formatPercent(group.getShare()),
                        "-",
                        group.getStatus()));

                if (group.getCategories() != null && !group.getCategories().isEmpty()) {
                    for (CategoryShareResponseDTO cat : group.getCategories()) {
                        String catName = "  └─ " + cat.getName();
                        if ("DELETED".equalsIgnoreCase(cat.getStatus())) {
                            catName += " (đã xóa)";
                        }
                        if (cat.isNew()) {
                            catName += " [Mới]";
                        }

                        sb.append(String.format("| %-35s | %18s | %12s | %14s | %-10s |\n",
                                truncate(catName, 35),
                                formatMoney(cat.getTotalAmount()),
                                formatPercent(cat.getShare()),
                                formatPercent(cat.getShareInGroup()),
                                cat.getStatus()));
                    }
                }
            }
        } else {
            sb.append("|                                     (Không có dữ liệu nhóm nào)                                  |\n");
        }

        sb.append("====================================================================================================\n");
        return sb.toString();
    }
    // endregion

    // region Helpers
    private static String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0 VNĐ";
        }
        return MONEY_FORMAT.format(amount) + " VNĐ";
    }

    private static String formatPercent(BigDecimal percent) {
        if (percent == null) {
            return "0.00%";
        }
        return PERCENT_FORMAT.format(percent) + "%";
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    // endregion
}
