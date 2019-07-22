package com.lh.main.table35;

import com.lh.IPackage.IWriter;
import com.lh.component.template.MahalanobisTemplate;
import com.lh.component.writer.FileWriter;
import com.lh.main.Worker;

import java.io.File;

public class Table35_Mahalanobis {
    public static void main(String[] args) {
        // Config
        String layoutResource = "default_layout.json";
        String dictionaryResource = "vni_dic.txt";
        int numberOfPoints = 100;
        IWriter writer = new FileWriter(new File("./log/table35/mahalanobis.txt").toPath(), true);
        new Worker(new MahalanobisTemplate(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();
    }
}
