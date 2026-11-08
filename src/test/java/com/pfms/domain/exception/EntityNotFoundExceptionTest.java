package com.pfms.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for EntityNotFoundException")
class EntityNotFoundExceptionTest {

    private static final String ENTITY_TYPE = "Ví";
    private static final String IDENTIFIER = "W-999";

    @Test
    @DisplayName("Should create exception with 2-arg constructor and verify default ErrorCode and properties")
    void testConvenienceConstructor() {
        EntityNotFoundException ex = new EntityNotFoundException(ENTITY_TYPE, IDENTIFIER);

        assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        assertEquals(ENTITY_TYPE, ex.getEntityType());
        assertEquals(IDENTIFIER, ex.getIdentifier());
        assertNotNull(ex.getTimestamp());
        assertTrue(ex.getTimestamp().isBefore(Instant.now().plusSeconds(1)));
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("Should format error message containing entityType and identifier")
    void testFormattedMessage() {
        EntityNotFoundException ex = new EntityNotFoundException(ENTITY_TYPE, IDENTIFIER);

        String msg = ex.getMessage();
        assertNotNull(msg);
        assertTrue(msg.contains(ENTITY_TYPE), "Thông điệp phải chứa loại thực thể");
        assertTrue(msg.contains(IDENTIFIER), "Thông điệp phải chứa mã định danh");
    }

    @Test
    @DisplayName("Should create exception with 3-arg constructor preserving custom ErrorCode")
    void testCanonicalConstructor() {
        EntityNotFoundException ex = new EntityNotFoundException(
                ErrorCode.ERR_NOT_FOUND, ENTITY_TYPE, IDENTIFIER);

        assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        assertEquals(ENTITY_TYPE, ex.getEntityType());
        assertEquals(IDENTIFIER, ex.getIdentifier());
    }

    @Test
    @DisplayName("Should throw NullPointerException when required parameters are null")
    void testDefensiveNullChecks() {
        assertThrows(NullPointerException.class, () -> new EntityNotFoundException(null, IDENTIFIER));
        assertThrows(NullPointerException.class, () -> new EntityNotFoundException(ENTITY_TYPE, null));

        assertThrows(NullPointerException.class,
                () -> new EntityNotFoundException(null, ENTITY_TYPE, IDENTIFIER));
        assertThrows(NullPointerException.class,
                () -> new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, null, IDENTIFIER));
        assertThrows(NullPointerException.class,
                () -> new EntityNotFoundException(ErrorCode.ERR_NOT_FOUND, ENTITY_TYPE, null));
    }

    @Test
    @DisplayName("Should satisfy inheritance hierarchy: EntityNotFoundException -> BusinessException -> PfmsException")
    void testInheritanceHierarchy() {
        EntityNotFoundException ex = new EntityNotFoundException(ENTITY_TYPE, IDENTIFIER);

        assertTrue(ex instanceof BusinessException);
        assertTrue(ex instanceof PfmsException);
        assertTrue(ex instanceof RuntimeException);
    }
}

