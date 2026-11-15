package com.pfms.presentation.contract;

/**
 * Hợp đồng phản hồi và thông báo người dùng cơ sở.
 * 100% không phụ thuộc các thư viện giao diện đồ họa Swing/AWT.
 */
public interface BaseView {

    void showMessage(String message);

    void showWarning(String message);

    void showError(String message);

    boolean confirmAction(String message);
}
