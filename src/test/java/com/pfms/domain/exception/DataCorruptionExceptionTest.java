package com.pfms.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for DataCorruptionException")
class DataCorruptionExceptionTest {

    private static final String FILE_NAME = "wallets.json";
    private static final int LINE_NUMBER = 14;
    private static final RuntimeException ROOT_CAUSE = new IllegalArgumentException("Unexpected token '{'");

    @Test
    @DisplayName("Should format error message containing fileName and line number")
    void testFormattedMessage() {
        DataCorruptionException ex = DataCorruptionException.syntaxError(FILE_NAME, LINE_NUMBER, ROOT_CAUSE);

        String msg = ex.getMessage();
        assertNotNull(msg);
        assertTrue(msg.contains(FILE_NAME), "Thông điệp phải chứa tên tệp");
        assertTrue(msg.contains(String.valueOf(LINE_NUMBER)), "Thông điệp phải chứa số dòng");
    }

    @Test
    @DisplayName("Should create exception with canonical 4-arg constructor preserving custom ErrorCode")
    void testCanonicalConstructor() {
        DataCorruptionException ex = new DataCorruptionException(
                ErrorCode.ERR_DAT_002, FILE_NAME, LINE_NUMBER, ROOT_CAUSE);

        assertEquals(ErrorCode.ERR_DAT_002, ex.getErrorCode());
        assertEquals(FILE_NAME, ex.getFileName());
        assertEquals(LINE_NUMBER, ex.getLine());
        assertSame(ROOT_CAUSE, ex.getCause());
    }

    @Test
    @DisplayName("Should create exception with custom message constructor")
    void testCustomMessageConstructor() {
        String customMsg = "Cấu trúc JSON bị cắt ngắn bất ngờ";
        DataCorruptionException ex = new DataCorruptionException(
                ErrorCode.ERR_DAT_001, customMsg, FILE_NAME, LINE_NUMBER, ROOT_CAUSE);

        assertEquals(ErrorCode.ERR_DAT_001, ex.getErrorCode());
        assertEquals(customMsg, ex.getMessage());
        assertEquals(FILE_NAME, ex.getFileName());
        assertEquals(LINE_NUMBER, ex.getLine());
        assertSame(ROOT_CAUSE, ex.getCause());
    }

    @Test
    @DisplayName("Should fallback to default message when custom message is null in 5-arg constructor")
    void testNullCustomMessageFallsBackToDefaultMessage() {
        DataCorruptionException ex = new DataCorruptionException(
                ErrorCode.ERR_DAT_001, null, FILE_NAME, LINE_NUMBER, ROOT_CAUSE);

        assertEquals(ErrorCode.ERR_DAT_001, ex.getErrorCode());
        assertEquals(ErrorCode.ERR_DAT_001.getDefaultMessage(), ex.getMessage());
        assertEquals(FILE_NAME, ex.getFileName());
        assertEquals(LINE_NUMBER, ex.getLine());
        assertSame(ROOT_CAUSE, ex.getCause());
    }

    @Test
    @DisplayName("Should throw NullPointerException when required parameters are null")
    void testDefensiveNullChecks() {
        // Null checks for static factory methods
        assertThrows(NullPointerException.class,
                () -> DataCorruptionException.syntaxError(null, LINE_NUMBER, ROOT_CAUSE));
        assertThrows(NullPointerException.class,
                () -> DataCorruptionException.syntaxError(FILE_NAME, LINE_NUMBER, null));
        assertThrows(NullPointerException.class,
                () -> DataCorruptionException.integrityError(null, LINE_NUMBER, ROOT_CAUSE));
        assertThrows(NullPointerException.class,
                () -> DataCorruptionException.integrityError(FILE_NAME, LINE_NUMBER, null));

        // Null checks for canonical constructor
        assertThrows(NullPointerException.class,
                () -> new DataCorruptionException(null, FILE_NAME, LINE_NUMBER, ROOT_CAUSE));
        assertThrows(NullPointerException.class,
                () -> new DataCorruptionException(ErrorCode.ERR_DAT_001, null, LINE_NUMBER, ROOT_CAUSE));
        assertThrows(NullPointerException.class,
                () -> new DataCorruptionException(ErrorCode.ERR_DAT_001, FILE_NAME, LINE_NUMBER, null));

        // Null checks for 5-arg constructor
        assertThrows(NullPointerException.class,
                () -> new DataCorruptionException(null, "msg", FILE_NAME, LINE_NUMBER, ROOT_CAUSE));
        assertThrows(NullPointerException.class,
                () -> new DataCorruptionException(ErrorCode.ERR_DAT_001, "msg", null, LINE_NUMBER, ROOT_CAUSE));
        assertThrows(NullPointerException.class,
                () -> new DataCorruptionException(ErrorCode.ERR_DAT_001, "msg", FILE_NAME, LINE_NUMBER, null));
    }

    @Test
    @DisplayName("Should create exception via static factory methods for syntax and integrity errors")
    void testStaticFactoryMethods() {
        DataCorruptionException syntaxEx = DataCorruptionException.syntaxError(FILE_NAME, LINE_NUMBER, ROOT_CAUSE);
        assertEquals(ErrorCode.ERR_DAT_001, syntaxEx.getErrorCode());
        assertEquals(FILE_NAME, syntaxEx.getFileName());
        assertEquals(LINE_NUMBER, syntaxEx.getLine());
        assertSame(ROOT_CAUSE, syntaxEx.getCause());
        assertNotNull(syntaxEx.getTimestamp());
        assertTrue(syntaxEx.getTimestamp().isBefore(Instant.now().plusSeconds(1)));

        DataCorruptionException integrityEx = DataCorruptionException.integrityError(FILE_NAME, LINE_NUMBER, ROOT_CAUSE);
        assertEquals(ErrorCode.ERR_DAT_002, integrityEx.getErrorCode());
        assertEquals(FILE_NAME, integrityEx.getFileName());
        assertEquals(LINE_NUMBER, integrityEx.getLine());
        assertSame(ROOT_CAUSE, integrityEx.getCause());
    }

    @Test
    @DisplayName("Should satisfy inheritance hierarchy: DataCorruptionException -> TechnicalException -> PfmsException")
    void testInheritanceHierarchy() {
        DataCorruptionException ex = DataCorruptionException.syntaxError(FILE_NAME, LINE_NUMBER, ROOT_CAUSE);

        assertTrue(ex instanceof TechnicalException);
        assertTrue(ex instanceof PfmsException);
        assertTrue(ex instanceof RuntimeException);
        assertFalse((Object) ex instanceof BusinessException,
                "DataCorruptionException không được thuộc nhánh BusinessException");
    }
}

