package com.pfms;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher {
    static {
        // Tắt cảnh báo runtime của OpenJFX khi chạy trên Classpath (unnamed module)
        Logger.getLogger("javafx").setLevel(Level.SEVERE);
        Logger.getLogger("com.sun.javafx").setLevel(Level.SEVERE);
    }

    public static void main(String[] args) {
        App.main(args);
    }
}
