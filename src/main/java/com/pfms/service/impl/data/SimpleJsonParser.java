package com.pfms.service.impl.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bộ phân tích cú pháp chuỗi JSON thuần túy sang cây cấu trúc Map, List và kiểu nguyên thủy.
 * Hoạt động theo cơ chế recursive descent parser độc lập không phụ thuộc thư viện ngoài.
 */
final class SimpleJsonParser {

    // region State
    private final String src;
    private int pos;
    // endregion

    // region Constructors
    SimpleJsonParser(String src) {
        this.src = src;
        this.pos = 0;
    }
    // endregion

    // region Operations
    Object parse() {
        skipWhitespace();
        if (pos >= src.length()) {
            throw new IllegalArgumentException("JSON rỗng");
        }
        Object result = parseValue();
        skipWhitespace();
        if (pos < src.length()) {
            throw new IllegalArgumentException("Dư thừa ký tự ngoài JSON tại vị trí " + pos);
        }
        return result;
    }
    // endregion

    // region Helpers
    private Object parseValue() {
        skipWhitespace();
        if (pos >= src.length()) {
            throw new IllegalArgumentException("Kết thúc dữ liệu JSON bất thường");
        }
        char c = src.charAt(pos);
        return switch (c) {
            case '{' -> parseObject();
            case '[' -> parseArray();
            case '"' -> parseString();
            case 't' -> parseLiteral("true", Boolean.TRUE);
            case 'f' -> parseLiteral("false", Boolean.FALSE);
            case 'n' -> parseLiteral("null", null);
            default -> {
                if (c == '-' || Character.isDigit(c)) {
                    yield parseNumber();
                }
                throw new IllegalArgumentException("Ký tự không hợp lệ tại vị trí " + pos + ": " + c);
            }
        };
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        expect('{');
        if (consumeIf('}')) {
            return map;
        }
        do {
            skipWhitespace();
            if (peek() != '"') {
                throw new IllegalArgumentException("Mong đợi khóa chuỗi tại vị trí " + pos);
            }
            String key = parseString();
            expect(':');
            map.put(key, parseValue());
        } while (consumeCommaOrClose('}'));
        return map;
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        expect('[');
        if (consumeIf(']')) {
            return list;
        }
        do {
            list.add(parseValue());
        } while (consumeCommaOrClose(']'));
        return list;
    }

    private boolean consumeIf(char target) {
        skipWhitespace();
        if (peek() == target) {
            pos++;
            return true;
        }
        return false;
    }

    private boolean consumeCommaOrClose(char closeChar) {
        if (consumeIf(closeChar)) {
            return false;
        }
        if (consumeIf(',')) {
            return true;
        }
        throw new IllegalArgumentException("Mong đợi ',' hoặc '" + closeChar + "' tại vị trí " + pos);
    }

    private String parseString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (pos < src.length()) {
            char c = src.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            }
            sb.append(c == '\\' ? readEscapedChar() : c);
        }
        throw new IllegalArgumentException("Chuỗi ký tự không đóng dấu nháy kép");
    }

    private char readEscapedChar() {
        if (pos >= src.length()) {
            throw new IllegalArgumentException("Ký tự thoát không hợp lệ cuối chuỗi");
        }
        char esc = src.charAt(pos++);
        return switch (esc) {
            case '"', '\\', '/' -> esc;
            case 'b' -> '\b';
            case 'f' -> '\f';
            case 'n' -> '\n';
            case 'r' -> '\r';
            case 't' -> '\t';
            case 'u' -> {
                if (pos + 4 > src.length()) {
                    throw new IllegalArgumentException("Ký tự unicode không đủ 4 chữ số");
                }
                char unicodeChar = (char) Integer.parseInt(src.substring(pos, pos + 4), 16);
                pos += 4;
                yield unicodeChar;
            }
            default -> throw new IllegalArgumentException("Ký tự thoát không hợp lệ: \\" + esc);
        };
    }

    private Object parseLiteral(String literal, Object value) {
        if (src.startsWith(literal, pos)) {
            pos += literal.length();
            return value;
        }
        throw new IllegalArgumentException("Giá trị JSON không hợp lệ tại vị trí " + pos);
    }

    private String parseNumber() {
        int start = pos;
        if (src.charAt(pos) == '-') {
            pos++;
        }
        consumeDigits();
        if (pos < src.length() && src.charAt(pos) == '.') {
            pos++;
            consumeDigits();
        }
        return src.substring(start, pos);
    }

    private void consumeDigits() {
        while (pos < src.length() && Character.isDigit(src.charAt(pos))) {
            pos++;
        }
    }

    private void skipWhitespace() {
        while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) {
            pos++;
        }
    }

    private char peek() {
        if (pos >= src.length()) {
            throw new IllegalArgumentException("Kết thúc tệp JSON bất thường");
        }
        return src.charAt(pos);
    }

    private void expect(char expected) {
        skipWhitespace();
        if (pos >= src.length() || src.charAt(pos) != expected) {
            throw new IllegalArgumentException("Mong đợi ký tự '" + expected + "' tại vị trí " + pos);
        }
        pos++;
    }
    // endregion
}
