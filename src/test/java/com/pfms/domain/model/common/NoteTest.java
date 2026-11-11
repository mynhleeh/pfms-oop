package com.pfms.domain.model.common;

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

@DisplayName("Unit Tests for Note Value Object")
class NoteTest {

    @Nested
    @DisplayName("Group 1: Null and Empty Handling")
    class NullAndEmptyHandlingTests {

        @Test
        @DisplayName("Should normalize null rawNote to empty string")
        void testNullRawNote() {
            Note note = Note.of(null);
            assertNotNull(note);
            assertEquals("", note.getValue());
            assertTrue(note.isEmpty());
            assertEquals(0, note.length());
            assertEquals(Note.EMPTY, note);
        }

        @Test
        @DisplayName("Should normalize null rawNote with custom label to empty string")
        void testNullRawNoteWithCustomLabel() {
            Note noteWallet = Note.of(null, "Ghi chú ví");
            assertEquals("", noteWallet.getValue());
            assertTrue(noteWallet.isEmpty());

            Note noteTxn = Note.of(null, "Ghi chú giao dịch");
            assertEquals("", noteTxn.getValue());
            assertTrue(noteTxn.isEmpty());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
        @DisplayName("Should normalize whitespace-only strings to empty string")
        void testWhitespaceOnlyRawNote(String blank) {
            Note note = Note.of(blank);
            assertEquals("", note.getValue());
            assertTrue(note.isEmpty());
            assertEquals(0, note.length());
            assertEquals(Note.EMPTY, note);
        }

        @Test
        @DisplayName("empty() factory should return singleton EMPTY instance")
        void testEmptyFactory() {
            Note note = Note.empty();
            assertSame(Note.EMPTY, note);
            assertTrue(note.isEmpty());
            assertEquals("", note.getValue());
        }
    }

    @Nested
    @DisplayName("Group 2: Boundary and Length Constraints")
    class BoundaryAndLengthConstraintsTests {

        @Test
        @DisplayName("Should accept valid string of exactly 255 characters")
        void testExact255Characters() {
            String exact255 = "A".repeat(255);
            Note note = Note.of(exact255);
            assertEquals(exact255, note.getValue());
            assertEquals(255, note.length());
            assertFalse(note.isEmpty());
        }

        @Test
        @DisplayName("Should accept 255 characters after trimming leading/trailing spaces")
        void testTrimmed255Characters() {
            String exact255 = "B".repeat(255);
            Note note = Note.of("   " + exact255 + "   ");
            assertEquals(exact255, note.getValue());
            assertEquals(255, note.length());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when note exceeds 255 characters (default label)")
        void testExceeds255CharactersDefaultLabel() {
            String longNote = "C".repeat(256);
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Note.of(longNote));
            assertEquals("Ghi chú không được vượt quá 255 ký tự.", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when note exceeds 255 characters (custom labels)")
        void testExceeds255CharactersCustomLabels() {
            String longNote = "D".repeat(256);

            IllegalArgumentException exWallet = assertThrows(IllegalArgumentException.class,
                    () -> Note.of(longNote, "Ghi chú ví"));
            assertEquals("Ghi chú ví không được vượt quá 255 ký tự.", exWallet.getMessage());

            IllegalArgumentException exTxn = assertThrows(IllegalArgumentException.class,
                    () -> Note.of(longNote, "Ghi chú giao dịch"));
            assertEquals("Ghi chú giao dịch không được vượt quá 255 ký tự.", exTxn.getMessage());
        }
    }

    @Nested
    @DisplayName("Group 3: Normalization and Formatting")
    class NormalizationAndFormattingTests {

        @Test
        @DisplayName("Should trim leading and trailing spaces while preserving internal spaces")
        void testTrimEdgesPreserveInternal() {
            Note note = Note.of("   Ăn tối tại nhà hàng cùng gia đình   ");
            assertEquals("Ăn tối tại nhà hàng cùng gia đình", note.getValue());
            assertEquals("Ăn tối tại nhà hàng cùng gia đình", note.toString());
            assertEquals(33, note.length());
        }
    }

    @Nested
    @DisplayName("Group 4: Value Object Contracts")
    class ValueObjectContractsTests {

        @Test
        @DisplayName("Should fulfill equals and hashCode contract")
        void testEqualsAndHashCode() {
            Note note1 = Note.of("Mua sách");
            Note note2 = Note.of("  Mua sách  ");
            Note noteDiff = Note.of("Mua cafe");

            assertEquals(note1, note1);
            assertEquals(note1, note2);
            assertEquals(note2, note1);
            assertEquals(note1.hashCode(), note2.hashCode());

            assertNotEquals(note1, noteDiff);
            assertNotEquals(note1, null);
            assertNotEquals(note1, "Mua sách");
        }

        @Test
        @DisplayName("Should work correctly in HashSet")
        void testHashSetUsage() {
            Set<Note> set = new HashSet<>();
            set.add(Note.of("Tiền thưởng"));
            set.add(Note.of("  Tiền thưởng  "));
            set.add(Note.of("Tiền lương"));

            assertEquals(2, set.size());
            assertTrue(set.contains(Note.of("Tiền thưởng")));
            assertTrue(set.contains(Note.of("Tiền lương")));
        }

        @Test
        @DisplayName("Should implement Comparable correctly")
        void testComparable() {
            Note a = Note.of("A");
            Note b = Note.of("B");
            Note c = Note.of("C");

            List<Note> list = new ArrayList<>();
            list.add(c);
            list.add(a);
            list.add(b);

            Collections.sort(list);

            assertEquals(a, list.get(0));
            assertEquals(b, list.get(1));
            assertEquals(c, list.get(2));
        }

        @Test
        @DisplayName("toString should return the internal string value")
        void testToString() {
            assertEquals("Tiền xăng", Note.of("  Tiền xăng  ").toString());
            assertEquals("", Note.empty().toString());
        }
    }
}

