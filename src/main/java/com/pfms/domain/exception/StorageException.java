package com.pfms.domain.exception;

/**
 * Ngoại lệ kỹ thuật ném ra tại Tầng Hạ tầng khi xảy ra lỗi I/O đọc ghi tệp
 * cục bộ trên đĩa cứng hệ thống, bọc trọn vẹn ngoại lệ gốc.
 */
public class StorageException extends TechnicalException {

    // region Constructors
    public StorageException(Throwable cause) {
        this(ErrorCode.ERR_SYS_001, cause);
    }

    public StorageException(String customMessage, Throwable cause) {
        this(ErrorCode.ERR_SYS_001, customMessage, cause);
    }

    public StorageException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public StorageException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
    // endregion
}
