package com.pfms.service.impl.common;

import com.pfms.domain.exception.StorageException;

/**
 * Tiện ích thực thi bước hoàn nguyên an toàn khi xảy ra lỗi lưu trữ dữ liệu.
 * Đảm bảo không nuốt ngoại lệ gốc và gắn kết các ngoại lệ phát sinh vào danh sách suppressed.
 */
public final class RollbackSupport {

    // region Constructors
    private RollbackSupport() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    public static void runRollbackStep(Runnable compensatingStep, StorageException primaryException) {
        if (compensatingStep == null) {
            return;
        }
        try {
            compensatingStep.run();
        } catch (RuntimeException rollbackException) {
            if (primaryException != null && rollbackException != primaryException) {
                primaryException.addSuppressed(rollbackException);
            }
        }
    }
    // endregion
}

