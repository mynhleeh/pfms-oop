package com.pfms.service.impl;

import com.pfms.domain.exception.StorageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử RollbackSupport (com.pfms.service.impl)")
class RollbackSupportTest {

    @Test
    @DisplayName("Hàm khởi tạo private phải ném AssertionError khi gọi qua Reflection")
    void shouldPreventInstantiationViaReflection() throws NoSuchMethodException {
        Constructor<RollbackSupport> constructor = RollbackSupport.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, exception.getCause());
        assertEquals("Utility class cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("runRollbackStep thực thi bình thường khi compensatingStep thành công")
    void shouldExecuteCompensatingStepSuccessfully() {
        AtomicBoolean executed = new AtomicBoolean(false);
        StorageException primary = new StorageException("Gốc", new IOException("disk fail"));

        RollbackSupport.runRollbackStep(() -> executed.set(true), primary);

        assertTrue(executed.get());
        assertEquals(0, primary.getSuppressed().length);
    }

    @Test
    @DisplayName("runRollbackStep không làm gì khi compensatingStep là null")
    void shouldHandleNullCompensatingStep() {
        StorageException primary = new StorageException("Gốc", new IOException("disk fail"));
        RollbackSupport.runRollbackStep(null, primary);
        assertEquals(0, primary.getSuppressed().length);
    }

    @Test
    @DisplayName("runRollbackStep bắt RuntimeException và ghi nhận suppressed exception")
    void shouldAttachSuppressedWhenStepFails() {
        StorageException primary = new StorageException("Gốc", new IOException("disk fail"));
        RuntimeException rollbackError = new IllegalStateException("Rollback failure");

        RollbackSupport.runRollbackStep(() -> {
            throw rollbackError;
        }, primary);

        assertEquals(1, primary.getSuppressed().length);
        assertSame(rollbackError, primary.getSuppressed()[0]);
    }

    @Test
    @DisplayName("runRollbackStep tránh self-suppression khi ngoại lệ bị ném trùng với ngoại lệ gốc")
    void shouldAvoidSelfSuppression() {
        StorageException primary = new StorageException("Gốc", new IOException("disk fail"));

        RollbackSupport.runRollbackStep(() -> {
            throw primary;
        }, primary);

        assertEquals(0, primary.getSuppressed().length);
    }

    @Test
    @DisplayName("runRollbackStep xử lý an toàn khi primaryException là null")
    void shouldHandleNullPrimaryException() {
        RollbackSupport.runRollbackStep(() -> {
            throw new RuntimeException("Lỗi mà không có primary");
        }, null);
    }
}

