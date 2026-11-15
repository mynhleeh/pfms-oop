package com.pfms.domain.model.common;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for EntityName Value Object")
class EntityNameTest {

    @Nested
    @DisplayName("Group 1: Null and Invalid Input Tests")
    class NullAndInvalidInputTests {

        @Test
        @DisplayName("Should throw BusinessException when rawName is null with default label")
        void testNullRawNameDefaultLabel() {
            BusinessException ex = assertThrows(BusinessException.class, () -> EntityName.of(null));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
            assertEquals("Tên không được để null.", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw BusinessException when rawName is null with custom label")
        void testNullRawNameCustomLabel() {
            BusinessException exWallet = assertThrows(BusinessException.class,
                    () -> EntityName.of(null, "Tên ví"));
            assertEquals(ErrorCode.ERR_NAM_001, exWallet.getErrorCode());
            assertEquals("Tên ví không được để null.", exWallet.getMessage());

            BusinessException exCategory = assertThrows(BusinessException.class,
                    () -> EntityName.of(null, "Tên danh mục"));
            assertEquals(ErrorCode.ERR_NAM_001, exCategory.getErrorCode());
            assertEquals("Tên danh mục không được để null.", exCategory.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
        @DisplayName("Should throw BusinessException when rawName is empty or only whitespace")
        void testEmptyOrWhitespaceRawName(String blankName) {
            BusinessException ex = assertThrows(BusinessException.class, () -> EntityName.of(blankName));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
            assertEquals("Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu.", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw BusinessException when rawName exceeds 50 characters")
        void testRawNameExceeding50Characters() {
            String longName = "A".repeat(51);
            BusinessException ex = assertThrows(BusinessException.class, () -> EntityName.of(longName));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
            assertEquals("Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu.", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw BusinessException when rawName exceeds 50 characters even with spaces trimmed")
        void testRawNameExceeding50CharactersWithSpaces() {
            String longName = "   " + "B".repeat(51) + "   ";
            BusinessException ex = assertThrows(BusinessException.class, () -> EntityName.of(longName));
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
            assertEquals("Tên phải có từ 1 đến 50 ký tự, không tính khoảng trắng ở hai đầu.", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Group 2: Boundary and Normalization Tests")
    class BoundaryAndNormalizationTests {

        @Test
        @DisplayName("Should successfully initialize with minimum length (1 character)")
        void testMinimumLength() {
            EntityName name = EntityName.of("X");
            assertEquals("X", name.getValue());
            assertEquals(1, name.length());
        }

        @Test
        @DisplayName("Should trim whitespace on 1 character input")
        void testMinimumLengthWithTrim() {
            EntityName name = EntityName.of("   X   ");
            assertEquals("X", name.getValue());
            assertEquals(1, name.length());
        }

        @Test
        @DisplayName("Should successfully initialize with maximum length (50 characters)")
        void testMaximumLength() {
            String exact50 = "C".repeat(50);
            EntityName name = EntityName.of(exact50);
            assertEquals(exact50, name.getValue());
            assertEquals(50, name.length());
        }

        @Test
        @DisplayName("Should trim whitespace and accept 50 characters")
        void testMaximumLengthWithTrim() {
            String exact50 = "D".repeat(50);
            EntityName name = EntityName.of("   " + exact50 + "   ");
            assertEquals(exact50, name.getValue());
            assertEquals(50, name.length());
        }

        @Test
        @DisplayName("Should preserve internal spaces when trimming ends")
        void testPreserveInternalSpaces() {
            EntityName name = EntityName.of("  Ví Tiền Mặt Chính  ");
            assertEquals("Ví Tiền Mặt Chính", name.getValue());
            assertEquals("Ví Tiền Mặt Chính", name.toString());
        }
    }

    @Nested
    @DisplayName("Group 3: Uniqueness and Comparison Tests")
    class UniquenessAndComparisonTests {

        @Test
        @DisplayName("Should produce consistent lowercase normalizedKey")
        void testNormalizedKey() {
            EntityName upper = EntityName.of("VÍ TIỀN");
            EntityName lower = EntityName.of("ví tiền");
            EntityName mixed = EntityName.of("  Ví Tiền  ");

            assertEquals(upper.getNormalizedKey(), lower.getNormalizedKey());
            assertEquals(lower.getNormalizedKey(), mixed.getNormalizedKey());
        }

        @Test
        @DisplayName("Should match another EntityName ignoring case")
        void testMatchesEntityName() {
            EntityName name1 = EntityName.of("Ăn Uống");
            EntityName name2 = EntityName.of("ăn uống");
            EntityName name3 = EntityName.of("Mua Sắm");

            assertTrue(name1.matches(name2));
            assertTrue(name2.matches(name1));
            assertFalse(name1.matches(name3));
            assertFalse(name1.matches((EntityName) null));
        }

        @Test
        @DisplayName("Should match raw string ignoring case")
        void testMatchesRawString() {
            EntityName name = EntityName.of("Ví Tiền");

            assertTrue(name.matches("VÍ TIỀN"));
            assertTrue(name.matches("  ví tiền  "));
            assertFalse(name.matches("Ví khác"));
            assertFalse(name.matches((String) null));
            assertFalse(name.matches("   "));
        }
    }

    @Nested
    @DisplayName("Group 4: Value Object Contracts (equals, hashCode, compareTo, toString)")
    class ValueObjectContractsTests {

        @Test
        @DisplayName("Should satisfy equals and hashCode contract")
        void testEqualsAndHashCode() {
            EntityName name1 = EntityName.of("Ăn Uống");
            EntityName name2 = EntityName.of("  Ăn Uống  ");
            EntityName nameDiff = EntityName.of("Mua Sắm");

            // Reflexive
            assertEquals(name1, name1);

            // Symmetric
            assertEquals(name1, name2);
            assertEquals(name2, name1);

            // HashCode consistency
            assertEquals(name1.hashCode(), name2.hashCode());

            // Inequality
            assertNotEquals(name1, nameDiff);
            assertNotEquals(name1, null);
            assertNotEquals(name1, "Ăn Uống"); // Different class
        }

        @Test
        @DisplayName("Should work correctly in HashSet")
        void testHashSetUsage() {
            Set<EntityName> set = new HashSet<>();
            set.add(EntityName.of("Lương"));
            set.add(EntityName.of("  Lương  "));
            set.add(EntityName.of("Thưởng"));

            assertEquals(2, set.size());
            assertTrue(set.contains(EntityName.of("Lương")));
            assertTrue(set.contains(EntityName.of("Thưởng")));
        }

        @Test
        @DisplayName("Should implement Comparable correctly")
        void testComparable() {
            EntityName a = EntityName.of("An");
            EntityName b = EntityName.of("Bình");
            EntityName c = EntityName.of("Cường");

            List<EntityName> list = new ArrayList<>();
            list.add(c);
            list.add(a);
            list.add(b);

            Collections.sort(list);

            assertEquals(a, list.get(0));
            assertEquals(b, list.get(1));
            assertEquals(c, list.get(2));
        }

        @Test
        @DisplayName("toString should return the trimmed value")
        void testToString() {
            EntityName name = EntityName.of("  Ví Chính  ");
            assertEquals("Ví Chính", name.toString());
        }
    }

    @Nested
    @DisplayName("Group 7: Reconstitute for Backup Restore [AS-02]")
    class ReconstituteTests {

        @Test
        @DisplayName("Should reconstitute valid name preserving exact trimmed value and key")
        void testReconstituteValidName() {
            EntityName name = EntityName.reconstitute("  Ví Tiết Kiệm  ");
            assertEquals("Ví Tiết Kiệm", name.getValue());
            assertEquals("ví tiết kiệm", name.getNormalizedKey());
        }

        @Test
        @DisplayName("Should bypass BR-NAM-01 length constraints on restore per AS-02")
        void testReconstituteBypassesBrNam01() {
            String longName = "Tên rất dài vượt quá 50 ký tự ".repeat(3);
            EntityName nameLong = assertDoesNotThrow(() -> EntityName.reconstitute(longName));
            assertEquals(longName.trim(), nameLong.getValue());

            EntityName nameBlank = assertDoesNotThrow(() -> EntityName.reconstitute("   "));
            assertEquals("", nameBlank.getValue());

            EntityName nameNull = assertDoesNotThrow(() -> EntityName.reconstitute(null));
            assertEquals("", nameNull.getValue());
        }
    }
}

