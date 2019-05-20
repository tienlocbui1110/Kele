package com.lh.convert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public final class VNIConverter {
    private static HashMap<String, String> mMapper = new HashMap<>();

    static {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("mapper.txt")), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] keys = line.split("-");
                if (keys.length != 2)
                    break;
                mMapper.put(keys[0], keys[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void convertInLast(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        Stream<String> lines = reader.lines();
        Iterator iterator = lines.iterator();
        while (iterator.hasNext()) {
            String baseLine = (String) iterator.next();
            String line = convertUnicodeToASCII(baseLine);
            line = moveNumberToLast(line);
            if (line.length() == 1)
                continue;
            writer.write(baseLine.toUpperCase() + " - " + line.toUpperCase());
            writer.newLine();
        }
        reader.close();
        writer.close();
    }

    private static String convertUnicodeToASCII(String rawString) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < rawString.length(); i++) {
            String currentChar = mMapper.getOrDefault(String.valueOf(rawString.charAt(i)), String.valueOf(rawString.charAt(i)));
            result.append(currentChar);
        }
        return result.toString();
    }

    private static String moveNumberToLast(String rawString) {
        StringBuilder result = new StringBuilder();
        StringBuilder numberBuilder = new StringBuilder();
        for (int i = 0; i < rawString.length(); i++) {
            char currentChar = rawString.charAt(i);
            if (Character.isDigit(currentChar)) {
                numberBuilder.append(currentChar);
            } else {
                result.append(currentChar);
            }
        }
        return result.append(numberBuilder.toString()).toString();
    }
}
