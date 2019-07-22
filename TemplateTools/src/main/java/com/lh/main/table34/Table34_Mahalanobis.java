package com.lh.main.table34;

import com.lh.IPackage.IWriter;
import com.lh.component.template.CosineTemplate;
import com.lh.component.template.DistanceConflict;
import com.lh.component.template.MahalanobisTemplate;
import com.lh.component.writer.FileWriter;
import com.lh.main.Worker;

import java.io.File;

public class Table34_Mahalanobis {
    public static void main(String[] args) {
        // Config
        String layoutResource = "default_layout.json";
        String dictionaryResource = "vni_dic.txt";
        int numberOfPoints = 50;
        IWriter writer = new FileWriter(new File("./log/table34/mahalanobis.txt").toPath(), true);
        new Worker(new MahalanobisTemplate(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();
    }
}
