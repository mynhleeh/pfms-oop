package com.pfms.service.impl.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bộ phân tích cú pháp chuỗi JSON thuần túy sang cây cấu trúc Map, List và kiểu nguyên thủy.
 * Hoạt động theo cơ chế recursive descent lexer/parser độc lập không phụ thuộc thư viện ngoài.
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
        if (c == '{') {
            return parseObject();
        } else if (c == '[') {
            return parseArray();
        } else if (c == '"') {
            return parseString();
        } else if (c == 't' || c == 'f') {
            return parseBoolean();
        } else if (c == 'n') {
            return parseNull();
        } else if (c == '-' || Character.isDigit(c)) {
            return parseNumber();
        }
        throw new IllegalArgumentException("Ký tự không hợp lệ tại vị trí " + pos + ": " + c);
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        expect('{');
        skipWhitespace();
        if (peek() == '}') {
            pos++;
            return map;
        }
        while (true) {
            skipWhitespace();
            if (peek() != '"') {
                throw new IllegalArgumentException("Mong đợi khóa chuỗi tại vị trí " + pos);
            }
            String key = parseString();
            skipWhitespace();
            expect(':');
            Object val = parseValue();
            map.put(key, val);
            skipWhitespace();
            char next = peek();
            if (next == '}') {
                pos++;
                break;
            } else if (next == ',') {
                pos++;
            } else {
                throw new IllegalArgumentException("Mong đợi ',' hoặc '}' tại vị trí " + pos);
            }
        }
        return map;
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        expect('[');
        skipWhitespace();
        if (peek() == ']') {
            pos++;
            return list;
        }
        while (true) {
            Object val = parseValue();
            list.add(val);
            skipWhitespace();
            char next = peek();
            if (next == ']') {
                pos++;
                break;
            } else if (next == ',') {
                pos++;
            } else {
                throw new IllegalArgumentException("Mong đợi ',' hoặc ']' tại vị trí " + pos);
            }
        }
        return list;
    }

    private String parseString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (pos < src.length()) {
            char c = src.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\') {
                if (pos >= src.length()) {
                    throw new IllegalArgumentException("Ký tự thoát không hợp lệ cuối chuỗi");
                }
                char esc = src.charAt(pos++);
                switch (esc) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> {
                        if (pos + 4 > src.length()) {
                            throw new IllegalArgumentException("Ký tự unicode không đủ 4 chữ số");
                        }
                        String hex = src.substring(pos, pos + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos += 4;
                    }
                    default -> throw new IllegalArgumentException("Ký tự thoát không hợp lệ: \\" + esc);
                }
            } else {
                sb.append(c);
            }
        }
        throw new IllegalArgumentException("Chuỗi ký tự không đóng dấu nháy kép");
    }

    private Boolean parseBoolean() {
        if (src.startsWith("true", pos)) {
            pos += 4;
            return Boolean.TRUE;
        } else if (src.startsWith("false", pos)) {
            pos += 5;
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Giá trị boolean không hợp lệ tại vị trí " + pos);
    }

    private Object parseNull() {
        if (src.startsWith("null", pos)) {
            pos += 4;
            return null;
        }
        throw new IllegalArgumentException("Giá trị null không hợp lệ tại vị trí " + pos);
    }

    private String parseNumber() {
        int start = pos;
        if (src.charAt(pos) == '-') {
            pos++;
        }
        while (pos < src.length() && Character.isDigit(src.charAt(pos))) {
            pos++;
        }
        if (pos < src.length() && src.charAt(pos) == '.') {
            pos++;
            while (pos < src.length() && Character.isDigit(src.charAt(pos))) {
                pos++;
            }
        }
        return src.substring(start, pos);
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
