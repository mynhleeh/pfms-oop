package com.pfms.presentation.wallet;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.wallet.viewmodel.WalletItemViewModel;
import com.pfms.presentation.wallet.viewmodel.WalletsSummaryViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử WalletPresenter - Bộ điều phối giao diện phân hệ Ví")
class WalletPresenterTest {

    private WalletView view;
    private WalletGUIController controller;
    private WalletPresenter presenter;

    private WalletResponseDTO activeWallet;
    private WalletResponseDTO archivedWallet;

    @BeforeEach
    void setUp() {
        view = Mockito.mock(WalletView.class);
        controller = Mockito.mock(WalletGUIController.class);

        activeWallet = new WalletResponseDTO(
                UUID.randomUUID(), "Ví Tiền mặt", new BigDecimal("5000000"), "VND", true, "ACTIVE", "Chi tiêu", LocalDateTime.now());
        archivedWallet = new WalletResponseDTO(
                UUID.randomUUID(), "Ví Cũ", new BigDecimal("1000000"), "VND", false, "ARCHIVED", "Đã khóa", LocalDateTime.now());

        WalletsSummaryResponseDTO defaultSummary = new WalletsSummaryResponseDTO(
                List.of(activeWallet, archivedWallet), new BigDecimal("6000000"), "VND", Collections.emptyMap());
        when(controller.loadWalletsSummary()).thenReturn(defaultSummary);

        presenter = new WalletPresenter(view, controller);
    }

    @Test
    @DisplayName("loadData() chuyển đổi DTO sang ViewModel và chuyển tiếp sang View")
    void testLoadDataSuccess() {
        presenter.loadData();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<WalletItemViewModel>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).renderWallets(listCaptor.capture());

        List<WalletItemViewModel> items = listCaptor.getValue();
        assertEquals(2, items.size());

        WalletItemViewModel item1 = items.get(0);
        assertEquals(activeWallet.getWalletId(), item1.id());
        assertEquals(1, item1.displayOrder());
        assertEquals("Ví Tiền mặt", item1.name());
        assertEquals(new BigDecimal("5000000"), item1.rawBalance());
        assertEquals("5,000,000 VNĐ", item1.formattedBalance());
        assertEquals("Hoạt động", item1.statusText());
        assertTrue(item1.isActive());

        WalletItemViewModel item2 = items.get(1);
        assertEquals(2, item2.displayOrder());
        assertEquals("Lưu trữ", item2.statusText());
        assertFalse(item2.isActive());

        ArgumentCaptor<WalletsSummaryViewModel> summaryCaptor = ArgumentCaptor.forClass(WalletsSummaryViewModel.class);
        verify(view).renderSummary(summaryCaptor.capture());

        WalletsSummaryViewModel summaryVm = summaryCaptor.getValue();
        assertEquals(new BigDecimal("6000000"), summaryVm.rawTotalBalance());
        assertEquals("6,000,000 VNĐ", summaryVm.formattedTotalBalance());
        assertEquals(1, summaryVm.activeCount());
        assertEquals(1, summaryVm.archivedCount());
    }

    @Test
    @DisplayName("filterWallets('ACTIVE') gọi controller với bộ lọc và cập nhật ViewModel")
    void testFilterWalletsActive() {
        WalletsSummaryResponseDTO activeSummary = new WalletsSummaryResponseDTO(
                List.of(activeWallet), new BigDecimal("5000000"), "VND", Collections.emptyMap());
        when(controller.loadWalletsSummary("ACTIVE")).thenReturn(activeSummary);

        presenter.filterWallets("ACTIVE");

        verify(controller).loadWalletsSummary("ACTIVE");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<WalletItemViewModel>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).renderWallets(listCaptor.capture());
        assertEquals(1, listCaptor.getValue().size());
        assertEquals("Ví Tiền mặt", listCaptor.getValue().get(0).name());
    }

    @Test
    @DisplayName("filterWallets('Tất cả') gọi controller không tham số bộ lọc")
    void testFilterWalletsAll() {
        presenter.filterWallets("Tất cả");
        verify(controller).loadWalletsSummary();
    }

    @Test
    @DisplayName("toggleStatus(id, true) gọi handleArchiveWallet và tải lại dữ liệu")
    void testToggleStatusArchive() {
        presenter.toggleStatus(activeWallet.getWalletId(), true);

        verify(controller).handleArchiveWallet(activeWallet.getWalletId());
        verify(view).showMessage("Đã chuyển ví sang trạng thái lưu trữ.");
        verify(controller, atLeastOnce()).loadWalletsSummary();
    }

    @Test
    @DisplayName("toggleStatus(id, false) gọi handleActivateWallet và tải lại dữ liệu")
    void testToggleStatusActivate() {
        presenter.toggleStatus(archivedWallet.getWalletId(), false);

        verify(controller).handleActivateWallet(archivedWallet.getWalletId());
        verify(view).showMessage("Đã kích hoạt lại ví thành công.");
        verify(controller, atLeastOnce()).loadWalletsSummary();
    }

    @Test
    @DisplayName("toggleStatus với id null hiển thị cảnh báo không gọi controller")
    void testToggleStatusNullId() {
        presenter.toggleStatus(null, true);

        verify(view).showWarning("Vui lòng chọn một ví trong bảng để thao tác.");
        verify(controller, never()).handleArchiveWallet(any());
        verify(controller, never()).handleActivateWallet(any());
    }

    @Test
    @DisplayName("deleteWallet(id) khi người dùng xác nhận YES gọi controller và tải lại")
    void testDeleteWalletConfirmed() {
        when(view.confirmAction(anyString())).thenReturn(true);

        presenter.deleteWallet(activeWallet.getWalletId());

        verify(controller).handleDeleteWallet(activeWallet.getWalletId());
        verify(view).showMessage("Đã xóa ví thành công.");
        verify(controller, atLeastOnce()).loadWalletsSummary();
    }

    @Test
    @DisplayName("deleteWallet(id) khi người dùng hủy NO không gọi controller")
    void testDeleteWalletCancelled() {
        when(view.confirmAction(anyString())).thenReturn(false);

        presenter.deleteWallet(activeWallet.getWalletId());

        verify(controller, never()).handleDeleteWallet(any());
        verify(view, never()).showMessage(anyString());
    }

    @Test
    @DisplayName("deleteWallet với id null hiển thị cảnh báo")
    void testDeleteWalletNullId() {
        presenter.deleteWallet(null);

        verify(view).showWarning("Vui lòng chọn một ví trong bảng để thao tác.");
        verify(view, never()).confirmAction(anyString());
        verify(controller, never()).handleDeleteWallet(any());
    }

    @Test
    @DisplayName("Khi controller ném BusinessException presenter bắt lỗi và gọi view.showError")
    void testBusinessExceptionHandling() {
        doThrow(new BusinessException(ErrorCode.ERR_WAL_001, "Lỗi nghiệp vụ ví"))
                .when(controller).handleDeleteWallet(activeWallet.getWalletId());
        when(view.confirmAction(anyString())).thenReturn(true);

        presenter.deleteWallet(activeWallet.getWalletId());

        verify(view).showError("Lỗi nghiệp vụ ví");
    }

    @Test
    @DisplayName("Điều hướng hộp thoại Thêm, Sửa, Kiểm kê số dư")
    void testDialogNavigation() {
        presenter.onAddWalletClicked();
        verify(view).openCreateWalletDialog();

        WalletItemViewModel item = new WalletItemViewModel(
                activeWallet.getWalletId(), 1, "Ví", BigDecimal.ZERO, "0 VNĐ", "Hoạt động", "", true);

        presenter.onEditWalletClicked(item);
        verify(view).openUpdateWalletDialog(item);

        presenter.onAdjustBalanceClicked(item);
        verify(view).openAdjustBalanceDialog(item);

        presenter.onEditWalletClicked(null);
        verify(view).showWarning("Vui lòng chọn một ví trong bảng để thao tác.");

        presenter.onAdjustBalanceClicked(null);
        verify(view, times(2)).showWarning("Vui lòng chọn một ví trong bảng để thao tác.");
    }
}
