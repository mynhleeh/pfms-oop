package com.pfms.testutil;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tiện ích khởi động JavaFX Toolkit và điều phối kiểm thử trên luồng FX Application Thread.
 */
public final class JavaFxTestHelper {

    private static volatile boolean initialized = false;

    private JavaFxTestHelper() {
        throw new UnsupportedOperationException("Lớp tiện ích không thể khởi tạo");
    }

    public static synchronized void initPlatform() {
        if (!initialized) {
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException ex) {
                // Toolkit đã được khởi động ở bài test trước
            }
            initialized = true;
        }
    }

    public static void runOnFxThread(Runnable action) {
        initPlatform();
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new RuntimeException("Quá thời gian chờ thực thi trên luồng JavaFX");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        if (error.get() != null) {
            if (error.get() instanceof RuntimeException re) {
                throw re;
            }
            if (error.get() instanceof Error err) {
                throw err;
            }
            throw new RuntimeException(error.get());
        }
    }
}
