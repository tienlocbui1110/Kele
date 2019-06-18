package com.lh.component.writer;

import com.lh.IPackage.IWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileWriter implements IWriter {
    private File file;

    public FileWriter(Path path) {
        this.file = path.toFile();
        if (!file.isDirectory() && !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void write(String s) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(s.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeln(String s) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(s.getBytes());
            fileOutputStream.write("\n".getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
