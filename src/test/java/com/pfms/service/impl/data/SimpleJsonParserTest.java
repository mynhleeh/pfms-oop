package com.pfms.service.impl.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử SimpleJsonParser")
class SimpleJsonParserTest {

    @Nested
    @DisplayName("Phân tích JSON hợp lệ")
    class ValidJsonTests {

        @Test
        @DisplayName("Phân tích đối tượng lồng nhau, mảng, literal và chuỗi escape hợp lệ")
        void shouldParseValidObjectWithPrimitivesEscapesAndNestedStructures() {
            String json = """
                    {
                      "name": "Ví \\"Chính\\" \\u0041\\n\\t\\\\/",
                      "balance": -125000.50,
                      "active": true,
                      "archived": false,
                      "note": null,
                      "tags": [],
                      "nested": {"items": [0, -0, 1e2, 2.5E+3, 3.25e-1]}
                    }
                    """;

            Object parsed = new SimpleJsonParser(json).parse();
            assertInstanceOf(Map.class, parsed);

            @SuppressWarnings("unchecked")
            Map<String, Object> root = (Map<String, Object>) parsed;
            assertEquals("Ví \"Chính\" A\n\t\\/", root.get("name"));
            assertEquals("-125000.50", root.get("balance"));
            assertEquals(Boolean.TRUE, root.get("active"));
            assertEquals(Boolean.FALSE, root.get("archived"));
            assertTrue(root.containsKey("note"));
            assertNull(root.get("note"));
            assertEquals(List.of(), root.get("tags"));

            @SuppressWarnings("unchecked")
            Map<String, Object> nested = (Map<String, Object>) root.get("nested");
            assertEquals(List.of("0", "-0", "1e2", "2.5E+3", "3.25e-1"), nested.get("items"));
        }

        @Test
        @DisplayName("Phân tích đối tượng rỗng và mảng rỗng")
        void shouldParseEmptyContainers() {
            assertEquals(Map.of(), new SimpleJsonParser("{}").parse());
            assertEquals(List.of(), new SimpleJsonParser("[]").parse());
        }

        @Test
        @DisplayName("Chấp nhận độ sâu lồng tối đa đúng bằng 64 cấp")
        void shouldAllowNestingAtExactMaxDepth() {
            String json = "[".repeat(64) + "1" + "]".repeat(64);

            Object result = new SimpleJsonParser(json).parse();
            assertInstanceOf(List.class, result);
        }
    }

    @Nested
    @DisplayName("Từ chối JSON sai cú pháp hoặc vượt biên an toàn")
    class InvalidJsonTests {

        @Test
        @DisplayName("Từ chối đầu vào null bằng IllegalArgumentException")
        void shouldRejectNullInputWithIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> new SimpleJsonParser(null).parse());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "{} trailing", "{\"a\":1", "[1,]", "{\"a\" 1}"})
        @DisplayName("Từ chối chuỗi rỗng, cụt hoặc dư ký tự")
        void shouldRejectEmptyTruncatedOrTrailingJson(String invalidJson) {
            assertThrows(IllegalArgumentException.class, () -> new SimpleJsonParser(invalidJson).parse());
        }

        @Test
        @DisplayName("Từ chối mảng lồng vượt quá 64 cấp")
        void shouldRejectArrayExceedingMaxDepth() {
            String deepArray = "[".repeat(65) + "1" + "]".repeat(65);
            assertThrows(IllegalArgumentException.class, () -> new SimpleJsonParser(deepArray).parse());
        }

        @Test
        @DisplayName("Từ chối object lồng vượt quá 64 cấp")
        void shouldRejectObjectExceedingMaxDepth() {
            String deepObject = "{\"k\":".repeat(65) + "1" + "}".repeat(65);
            assertThrows(IllegalArgumentException.class, () -> new SimpleJsonParser(deepObject).parse());
        }

        @ParameterizedTest
        @ValueSource(strings = {"\"line1\nline2\"", "\"col1\tcol2\"", "\"ctrl\u0000char\"", "\"cr\rchar\""})
        @DisplayName("Từ chối ký tự điều khiển chưa được escape trong chuỗi")
        void shouldRejectUnescapedControlCharacters(String jsonWithControlChar) {
            assertThrows(IllegalArgumentException.class, () -> new SimpleJsonParser(jsonWithControlChar).parse());
        }

        @ParameterizedTest
        @ValueSource(strings = {"\"\\u+041\"", "\"\\u-001\"", "\"\\u04\"", "\"\\uZZZZ\"", "\"\\x41\""})
        @DisplayName("Từ chối mã thoát unicode và ký tự thoát sai quy cách")
        void shouldRejectMalformedUnicodeOrEscapeSequences(String jsonWithBadEscape) {
            assertThrows(IllegalArgumentException.class, () -> new SimpleJsonParser(jsonWithBadEscape).parse());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-", "-.", "-.5", "1.", "01", "-01", "1e", "1e+", "1e-", "\u0661"})
        @DisplayName("Từ chối định dạng số vi phạm RFC 8259 hoặc chữ số non-ASCII")
        void shouldRejectMalformedNumbersAndNonAsciiDigits(String invalidNumberJson) {
            assertThrows(IllegalArgumentException.class, () -> new SimpleJsonParser(invalidNumberJson).parse());
        }
    }
}
