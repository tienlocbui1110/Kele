package com.lh.component.writer;

import com.lh.IPackage.IWriter;

public class DefaultWriter implements IWriter {
    @Override
    public void write(String s) {
        System.out.print(s);
    }

    @Override
    public void writeln(String s) {
        System.out.println(s);
    }
}
