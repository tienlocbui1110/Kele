package com.lh.main.table38;

import com.lh.IPackage.IWriter;
import com.lh.component.template.EuclidTemplate;
import com.lh.component.writer.FileWriter;
import com.lh.main.Worker;

import java.io.File;

public class Table38_Epsilon_2 {
    public static void main(String[] args) {
        // Config
        String layoutResource = "default_layout.json";
        String dictionaryResource = "vni_dic.txt";
        int numberOfPoints = 50;
        IWriter writer = new FileWriter(new File("./log/table38/epsilon_2.txt").toPath(), true);
        new Worker(new EuclidTemplate(dictionaryResource, layoutResource, numberOfPoints, writer, 2)).doWork();
    }
}
