package com.lh.kete.util;

public class ShellParser {
    public static String parse(String json) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < json.length(); i++) {
            char nextChar = json.charAt(i);
            if (nextChar == '\'') {
                builder.append("'\\''");
                continue;
            }
            if (nextChar == '"') {
                builder.append("\\\"");
                continue;
            }
            if (nextChar != '\\') {
                builder.append(nextChar);
                continue;
            }

            // Next char is \
            int count = 0;
            do {
                count++;
                if (i == (json.length() - 1))
                    break;
                i++;
                nextChar = json.charAt(i);
            } while (nextChar == '\\');
            if (nextChar == '\"') {
                for (int j = 0; j < count * 2; j++)
                    builder.append('\\');
                builder.append("\\\"");
            } else {
                for (int j = 0; j < count; j++)
                    builder.append('\\');
                if (nextChar != '\\')
                    builder.append(nextChar);
            }
        }
        return builder.toString();
    }
}
