package com.lh.main.table37;

import com.lh.IPackage.IWriter;
import com.lh.component.template.CosineTemplate;
import com.lh.component.writer.FileWriter;
import com.lh.main.Worker;

import java.io.File;

public class Table37_Cosine {
    public static void main(String[] args) {
        // Config
        String layoutResource = "optimize_layout.json";
        String dictionaryResource = "vni_dic.txt";
        int numberOfPoints = 100;
        IWriter writer = new FileWriter(new File("./log/table37/cosine.txt").toPath(), true);
        new Worker(new CosineTemplate(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();
    }
}
