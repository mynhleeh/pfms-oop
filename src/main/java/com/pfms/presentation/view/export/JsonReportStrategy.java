package com.pfms.presentation.view.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.pfms.common.util.Preconditions;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;

import java.time.LocalDate;

/**
 * Chiến lược kết xuất báo cáo tài chính sang chuỗi JSON định dạng chuẩn (pretty printing).
 * Sử dụng thư viện Gson và định dạng ngày tháng theo chuẩn ISO-8601.
 */
public final class JsonReportStrategy implements ReportExportStrategy {

    // region State
    private final Gson gson;
    // endregion

    // region Constructors
    public JsonReportStrategy() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(LocalDate.class,
                        (JsonSerializer<LocalDate>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
                .create();
    }
    // endregion

    // region Operations
    @Override
    public String exportCashFlow(CashFlowReportResponseDTO report) {
        Preconditions.checkArgument(report != null, "CashFlowReportResponseDTO không được để null");
        return this.gson.toJson(report);
    }

    @Override
    public String exportSpendingStructure(SpendingStructureReportResponseDTO report) {
        Preconditions.checkArgument(report != null, "SpendingStructureReportResponseDTO không được để null");
        return this.gson.toJson(report);
    }
    // endregion
}
