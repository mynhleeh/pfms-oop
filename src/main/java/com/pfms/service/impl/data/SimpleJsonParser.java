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

    // region Constants
    private static final int MAX_DEPTH = 64;
    // endregion

    // region State
    private final String src;
    private int pos;
    // endregion

    // region Constructors
    SimpleJsonParser(String src) {
        if (src == null) {
            throw new IllegalArgumentException("Chuỗi JSON không được để null");
        }
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
        Object result = parseValue(0);
        skipWhitespace();
        if (pos < src.length()) {
            throw new IllegalArgumentException("Dư thừa ký tự ngoài JSON tại vị trí " + pos);
        }
        return result;
    }
    // endregion

    // region Helpers
    private Object parseValue(int depth) {
        skipWhitespace();
        if (pos >= src.length()) {
            throw new IllegalArgumentException("Kết thúc dữ liệu JSON bất thường");
        }
        char c = src.charAt(pos);
        return switch (c) {
            case '{' -> parseObject(depth + 1);
            case '[' -> parseArray(depth + 1);
            case '"' -> parseString();
            case 't' -> parseLiteral("true", Boolean.TRUE);
            case 'f' -> parseLiteral("false", Boolean.FALSE);
            case 'n' -> parseLiteral("null", null);
            default -> {
                if (c == '-' || isAsciiDigit(c)) {
                    yield parseNumber();
                }
                throw new IllegalArgumentException("Ký tự không hợp lệ tại vị trí " + pos + ": " + c);
            }
        };
    }

    private Map<String, Object> parseObject(int depth) {
        if (depth > MAX_DEPTH) {
            throw new IllegalArgumentException("Độ sâu lồng JSON vượt quá giới hạn tối đa " + MAX_DEPTH);
        }
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
            map.put(key, parseValue(depth));
        } while (consumeCommaOrClose('}'));
        return map;
    }

    private List<Object> parseArray(int depth) {
        if (depth > MAX_DEPTH) {
            throw new IllegalArgumentException("Độ sâu lồng JSON vượt quá giới hạn tối đa " + MAX_DEPTH);
        }
        List<Object> list = new ArrayList<>();
        expect('[');
        if (consumeIf(']')) {
            return list;
        }
        do {
            list.add(parseValue(depth));
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
            if (c < 0x20) {
                throw new IllegalArgumentException("Chuỗi JSON chứa ký tự điều khiển chưa escape tại vị trí " + (pos - 1));
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
                int codePoint = 0;
                for (int i = 0; i < 4; i++) {
                    int digit = hexDigitValue(src.charAt(pos + i));
                    if (digit < 0) {
                        throw new IllegalArgumentException("Ký tự unicode không hợp lệ tại vị trí " + (pos + i));
                    }
                    codePoint = (codePoint << 4) | digit;
                }
                pos += 4;
                yield (char) codePoint;
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
        if (pos < src.length() && src.charAt(pos) == '-') {
            pos++;
        }
        if (pos >= src.length() || !isAsciiDigit(src.charAt(pos))) {
            throw new IllegalArgumentException("Số JSON không hợp lệ tại vị trí " + start);
        }
        if (src.charAt(pos) == '0') {
            pos++;
            if (pos < src.length() && isAsciiDigit(src.charAt(pos))) {
                throw new IllegalArgumentException("Số JSON không được chứa chữ số 0 dư ở đầu tại vị trí " + start);
            }
        } else {
            consumeAsciiDigits();
        }
        if (pos < src.length() && src.charAt(pos) == '.') {
            pos++;
            if (pos >= src.length() || !isAsciiDigit(src.charAt(pos))) {
                throw new IllegalArgumentException("Phần thập phân của số JSON thiếu chữ số tại vị trí " + pos);
            }
            consumeAsciiDigits();
        }
        if (pos < src.length() && (src.charAt(pos) == 'e' || src.charAt(pos) == 'E')) {
            pos++;
            if (pos < src.length() && (src.charAt(pos) == '+' || src.charAt(pos) == '-')) {
                pos++;
            }
            if (pos >= src.length() || !isAsciiDigit(src.charAt(pos))) {
                throw new IllegalArgumentException("Phần số mũ của số JSON thiếu chữ số tại vị trí " + pos);
            }
            consumeAsciiDigits();
        }
        return src.substring(start, pos);
    }

    private void consumeAsciiDigits() {
        while (pos < src.length() && isAsciiDigit(src.charAt(pos))) {
            pos++;
        }
    }

    private static boolean isAsciiDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static int hexDigitValue(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return 10 + (c - 'a');
        }
        if (c >= 'A' && c <= 'F') {
            return 10 + (c - 'A');
        }
        return -1;
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
