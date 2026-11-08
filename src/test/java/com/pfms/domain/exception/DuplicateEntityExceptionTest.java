package com.pfms.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for DuplicateEntityException")
class DuplicateEntityExceptionTest {

    private static final String ENTITY_TYPE = "Ví";
    private static final String NAME = "Tiền mặt";

    @Test
    @DisplayName("Should create exception with 2-arg constructor and verify default ErrorCode and properties")
    void testConvenienceConstructor() {
        DuplicateEntityException ex = new DuplicateEntityException(ENTITY_TYPE, NAME);

        assertEquals(ErrorCode.ERR_NAM_002, ex.getErrorCode());
        assertEquals(ENTITY_TYPE, ex.getEntityType());
        assertEquals(NAME, ex.getName());
        assertNotNull(ex.getTimestamp());
        assertTrue(ex.getTimestamp().isBefore(Instant.now().plusSeconds(1)));
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("Should format error message containing entityType and duplicate name")
    void testFormattedMessage() {
        DuplicateEntityException ex = new DuplicateEntityException(ENTITY_TYPE, NAME);

        String msg = ex.getMessage();
        assertNotNull(msg);
        assertTrue(msg.contains(ENTITY_TYPE), "Thông điệp phải chứa loại thực thể");
        assertTrue(msg.contains(NAME), "Thông điệp phải chứa tên bị trùng lặp");
    }

    @Test
    @DisplayName("Should create exception with 3-arg constructor preserving custom ErrorCode")
    void testCanonicalConstructor() {
        DuplicateEntityException ex = new DuplicateEntityException(
                ErrorCode.ERR_NAM_002, ENTITY_TYPE, NAME);

        assertEquals(ErrorCode.ERR_NAM_002, ex.getErrorCode());
        assertEquals(ENTITY_TYPE, ex.getEntityType());
        assertEquals(NAME, ex.getName());
    }

    @Test
    @DisplayName("Should throw NullPointerException when required parameters are null")
    void testDefensiveNullChecks() {
        // Null checks for 2-arg constructor
        assertThrows(NullPointerException.class, () -> new DuplicateEntityException(null, NAME));
        assertThrows(NullPointerException.class, () -> new DuplicateEntityException(ENTITY_TYPE, null));

        // Null checks for 3-arg constructor
        assertThrows(NullPointerException.class,
                () -> new DuplicateEntityException(null, ENTITY_TYPE, NAME));
        assertThrows(NullPointerException.class,
                () -> new DuplicateEntityException(ErrorCode.ERR_NAM_002, null, NAME));
        assertThrows(NullPointerException.class,
                () -> new DuplicateEntityException(ErrorCode.ERR_NAM_002, ENTITY_TYPE, null));
    }

    @Test
    @DisplayName("Should satisfy inheritance hierarchy: DuplicateEntityException -> BusinessException -> PfmsException")
    void testInheritanceHierarchy() {
        DuplicateEntityException ex = new DuplicateEntityException(ENTITY_TYPE, NAME);

        assertTrue(ex instanceof BusinessException);
        assertTrue(ex instanceof PfmsException);
        assertTrue(ex instanceof RuntimeException);
    }
}

