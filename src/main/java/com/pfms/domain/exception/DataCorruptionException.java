package com.pfms.domain.exception;

import java.util.Objects;

/**
 * Ngoại lệ kỹ thuật ném ra khi tệp lưu trữ bị hỏng cấu trúc dòng, thiếu trường
 * dữ liệu hoặc sai lệch định dạng không thể phân tích cú pháp.
 */
public class DataCorruptionException extends TechnicalException {

    // region State
    private final String fileName;
    private final int line;
    // endregion

    // region Constructors
    public DataCorruptionException(ErrorCode errorCode, String fileName, int line, Throwable cause) {
        this(errorCode, String.format("Tệp dữ liệu [%s] bị hỏng hoặc sai định dạng tại dòng %d.", fileName, line),
                fileName, line, cause);
    }

    public DataCorruptionException(ErrorCode errorCode, String customMessage, String fileName, int line,
            Throwable cause) {
        super(errorCode, customMessage, cause);
        this.fileName = Objects.requireNonNull(fileName, "Tên tệp không được để null");
        this.line = line;
    }
    // endregion

    // region Getters
    public String getFileName() {
        return this.fileName;
    }

    public int getLine() {
        return this.line;
    }
    // endregion

    // region Static factory methods
    public static DataCorruptionException syntaxError(String fileName, int line, Throwable cause) {
        return new DataCorruptionException(ErrorCode.ERR_DAT_001, fileName, line, cause);
    }

    public static DataCorruptionException integrityError(String fileName, int line, Throwable cause) {
        return new DataCorruptionException(ErrorCode.ERR_DAT_002, fileName, line, cause);
    }
    // endregion
}
