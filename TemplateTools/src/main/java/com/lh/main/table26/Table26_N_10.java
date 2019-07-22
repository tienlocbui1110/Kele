package com.lh.main.table26;

import com.lh.IPackage.IWriter;
import com.lh.component.template.DistanceConflict;
import com.lh.component.writer.FileWriter;
import com.lh.main.Worker;

import java.io.File;

public class Table26_N_10 {
    public static void main(String[] args) {
        // Config
        String layoutResource = "default_layout.json";
        String dictionaryResource = "vni_dic.txt";
        int numberOfPoints = 10;
        IWriter writer = new FileWriter(new File("./log/table26/n10.txt").toPath(), true);
        new Worker(new DistanceConflict(dictionaryResource, layoutResource, numberOfPoints, writer, true)).doWork();
    }
}
