package com.pfms.domain.exception;

/**
 * Danh mục mã lỗi và thông điệp phản hồi chuẩn hóa của hệ thống.
 */
public enum ErrorCode {

    // region Constants
    ERR_FIN_001("ERR-FIN-001", "Dữ liệu số tiền không đúng định dạng số học hợp lệ."),
    ERR_FIN_002("ERR-FIN-002", "Lỗi số học: Phép toán chia không xác định quy tắc làm tròn."),

    ERR_NAM_001("ERR-NAM-001", "Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu."),
    ERR_NAM_002("ERR-NAM-002", "Tên đã tồn tại trong hệ thống. Vui lòng chọn tên khác."),

    ERR_WAL_001("ERR-WAL-001", "Giao dịch bị từ chối: Số dư ví không đủ để thực hiện thao tác."),

    ERR_TRF_001("ERR-TRF-001", "Giao dịch không hợp lệ: Ví chuyển và ví nhận phải khác nhau."),
    ERR_TRF_002("ERR-TRF-002", "Số tiền chuyển phải là một số dương lớn hơn 0."),

    ERR_FEE_001("ERR-FEE-001", "Chi phí không hợp lệ."),
    ERR_FEE_002("ERR-FEE-002", "Vui lòng chọn danh mục cho phí giao dịch."),
    ERR_FEE_003("ERR-FEE-003", "Danh mục phí không hợp lệ hoặc không tương thích với loại chi tiêu."),

    ERR_CAT_001("ERR-CAT-001", "Không được phép đổi loại thu chi hoặc chuyển danh mục sang nhóm khác loại."),
    ERR_CAT_002("ERR-CAT-002", "Không thể xóa nhóm cuối cùng của loại thu chi này."),
    ERR_CAT_003("ERR-CAT-003", "Không thể chuyển danh mục sang nhóm đã xóa."),
    ERR_CAT_004("ERR-CAT-004", "Nhóm chứa danh mục này đang ở trạng thái đã xóa. Hãy khôi phục nhóm trước."),
    ERR_CAT_005("ERR-CAT-005", "Không thể tạo danh mục trong nhóm đã xóa. Hãy chọn nhóm đang dùng."),

    ERR_DAT_001("ERR-DAT-001", "Tệp sao lưu không hợp lệ hoặc cấu trúc dữ liệu JSON bị hỏng."),
    ERR_DAT_002("ERR-DAT-002", "Dữ liệu sao lưu vi phạm ràng buộc toàn vẹn số dư ví hoặc dòng tiền."),

    ERR_SYS_001("ERR-SYS-001", "Lỗi hệ thống: Không thể truy xuất hoặc lưu trữ tệp dữ liệu cục bộ."),
    ERR_NOT_FOUND("ERR-SYS-002", "Không tìm thấy thực thể yêu cầu trong hệ thống.");
    // endregion

    // region State
    private final String code;
    private final String defaultMessage;
    // endregion

    // region Constructors
    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    // endregion

    // region Getters
    public String getCode() {
        return this.code;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }
    // endregion
}
