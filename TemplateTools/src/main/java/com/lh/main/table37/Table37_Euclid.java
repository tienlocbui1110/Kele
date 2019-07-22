package com.lh.main.table37;

import com.lh.IPackage.IWriter;
import com.lh.component.template.EuclidTemplate;
import com.lh.component.writer.FileWriter;
import com.lh.main.Worker;

import java.io.File;

public class Table37_Euclid {
    public static void main(String[] args) {
        // Config
        String layoutResource = "optimize_layout.json";
        String dictionaryResource = "vni_dic.txt";
        int numberOfPoints = 100;
        IWriter writer = new FileWriter(new File("./log/table37/euclid.txt").toPath(), true);
        new Worker(new EuclidTemplate(dictionaryResource, layoutResource, numberOfPoints, writer, 0, false)).doWork();
    }
}
