package com.pfms.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pfms.domain.model.common.Money;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for InsufficientBalanceException")
class InsufficientBalanceExceptionTest {

    private static final String WALLET_ID = "W-001";
    private static final Money BALANCE = Money.of("100000.00");
    private static final Money AMOUNT = Money.of("250000.00");

    @Test
    @DisplayName("Should create exception with 3-arg constructor and verify default ErrorCode and properties")
    void testConvenienceConstructor() {
        InsufficientBalanceException ex = new InsufficientBalanceException(WALLET_ID, BALANCE, AMOUNT);

        assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
        assertEquals(WALLET_ID, ex.getWalletId());
        assertEquals(BALANCE, ex.getBalance());
        assertEquals(AMOUNT, ex.getAmount());
        assertNotNull(ex.getTimestamp());
        assertTrue(ex.getTimestamp().isBefore(Instant.now().plusSeconds(1)));
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("Should format error message containing walletId, balance, and requested amount")
    void testFormattedMessage() {
        InsufficientBalanceException ex = new InsufficientBalanceException(WALLET_ID, BALANCE, AMOUNT);

        String msg = ex.getMessage();
        assertNotNull(msg);
        assertTrue(msg.contains(WALLET_ID), "Thông điệp phải chứa mã ví");
        assertTrue(msg.contains(BALANCE.toString()), "Thông điệp phải chứa số dư hiện có");
        assertTrue(msg.contains(AMOUNT.toString()), "Thông điệp phải chứa số tiền yêu cầu");
    }

    @Test
    @DisplayName("Should create exception with 4-arg constructor preserving custom ErrorCode")
    void testCanonicalConstructor() {
        InsufficientBalanceException ex = new InsufficientBalanceException(
                ErrorCode.ERR_WAL_001, WALLET_ID, BALANCE, AMOUNT);

        assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
        assertEquals(WALLET_ID, ex.getWalletId());
        assertEquals(BALANCE, ex.getBalance());
        assertEquals(AMOUNT, ex.getAmount());
    }

    @Test
    @DisplayName("Should throw NullPointerException when required parameters are null")
    void testDefensiveNullChecks() {
        assertThrows(NullPointerException.class, () -> new InsufficientBalanceException(null, BALANCE, AMOUNT));
        assertThrows(NullPointerException.class, () -> new InsufficientBalanceException(WALLET_ID, null, AMOUNT));
        assertThrows(NullPointerException.class, () -> new InsufficientBalanceException(WALLET_ID, BALANCE, null));

        assertThrows(NullPointerException.class,
                () -> new InsufficientBalanceException(null, WALLET_ID, BALANCE, AMOUNT));
    }

    @Test
    @DisplayName("Should satisfy inheritance hierarchy: InsufficientBalanceException -> BusinessException -> PfmsException")
    void testInheritanceHierarchy() {
        InsufficientBalanceException ex = new InsufficientBalanceException(WALLET_ID, BALANCE, AMOUNT);

        assertTrue(ex instanceof BusinessException);
        assertTrue(ex instanceof PfmsException);
        assertTrue(ex instanceof RuntimeException);
    }
}
