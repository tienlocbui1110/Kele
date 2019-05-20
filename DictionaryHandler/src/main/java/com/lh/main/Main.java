package com.lh.main;

import com.lh.convert.VNIConverter;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("vi_dictionary.txt");
            File file = new File(".", "vni_dic.txt");
            if (!file.exists())
                file.createNewFile();
            outputStream = new FileOutputStream(file);
            VNIConverter.convertInLast(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
