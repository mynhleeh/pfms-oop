package com.pfms.service.impl;

import com.pfms.domain.exception.StorageException;

/**
 * Tiện ích hỗ trợ thực thi các bước hoàn nguyên dùng chung cho các lớp cài đặt
 * dịch vụ trong gói {@code com.pfms.service.impl}.
 */
final class RollbackSupport {

    // region Constructors
    private RollbackSupport() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations

    static void runRollbackStep(Runnable compensatingStep, StorageException primaryException) {
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
