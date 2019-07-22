package com.lh.main.table30;

import com.lh.IPackage.IWriter;
import com.lh.component.template.DistanceConflict;
import com.lh.component.writer.FileWriter;
import com.lh.main.Worker;

import java.io.File;

public class Table30_N_10 {
    public static void main(String[] args) {
        // Config
        String layoutResource = "optimize_layout.json";
        String dictionaryResource = "vni_dic.txt";
        int numberOfPoints = 10;
        IWriter writer = new FileWriter(new File("./log/table30/n10.txt").toPath(), true);
        new Worker(new DistanceConflict(dictionaryResource, layoutResource, numberOfPoints, writer, false)).doWork();
    }
}
