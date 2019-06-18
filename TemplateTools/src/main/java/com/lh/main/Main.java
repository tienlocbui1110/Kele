package com.lh.main;

import com.lh.IPackage.IWriter;
import com.lh.component.template.CosineTemplate;
import com.lh.component.template.EuclidTemplate;
import com.lh.component.writer.FileWriter;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Config
        String layoutResource = "default_layout.json";
        String dictionaryResource = "vni_dic.txt"; // Input_method : vni_last_sign
        int numberOfPoints = 50;

        // ----- Euclid Template ----- //
        IWriter writer = new FileWriter(new File("./log/euclid.txt").toPath());
        new Worker(new EuclidTemplate(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();


        // ----- Cosine Template ----- //
        writer = new FileWriter(new File("./log/cosine.txt").toPath());
        new Worker(new CosineTemplate(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();

    }
}
