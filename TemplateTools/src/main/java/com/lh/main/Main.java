package com.lh.main;

import com.lh.IPackage.IWriter;
import com.lh.component.template.DistanceConflict;
import com.lh.component.writer.FileWriter;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Config
        String layoutResource = "default_layout.json";
        String dictionaryResource = "vni_dic.txt"; // Input_method : vni_last_sign
        int numberOfPoints = 50;

        // ----- Predict with Euclid distance ----- //
//        IWriter writer = new FileWriter(new File("./log/euclid_distance_optimize_layout.txt").toPath(), true);
//        new Worker(new EuclidTemplate(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();

        // ----- Predict with Cosine Template ----- //
//        IWriter writer = new FileWriter(new File("./log/cosine_distance_optimize_layout.txt").toPath(), true);
//        new Worker(new CosineTemplate(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();

        // ----- Predict with Mahalanobis Template ----- //
//        IWriter writer = new FileWriter(new File("./log/mahalanobis_distance_optimize_layout.txt").toPath(), true);
//        new Worker(new MahalanobisTemplate(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();

        //QWERTY layout DistanceConflict : distance = 0
//        layoutResource = "default_layout.json";
//        IWriter writer = new FileWriter(new File("./log/qwe_layout_conflict.txt").toPath(), true);
//        new Worker(new DistanceConflict(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();

        //Optimize Layout DistanceConflict : distance = 0
        layoutResource = "keyboard_layout.json";
        IWriter writer = new FileWriter(new File("./log/optimize_layout_conflict.txt").toPath(), true);
        new Worker(new DistanceConflict(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();

        //QWE Similarity conflict : 0 < distance < 0.5
//        layoutResource = "default_layout.json";
//        writer = new FileWriter(new File("./log/qwe_layout_similarity_conflict.txt").toPath(), true);
//        new Worker(new SimilarityDistanceConflict(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();

        //Optimize layout Similarity conflict : 0 < distance < 0.5
//        layoutResource = "test_layout.json";
//        writer = new FileWriter(new File("./log/optimize_layout_similarity_conflict.txt").toPath(), true);
//        new Worker(new SimilarityDistanceConflict(dictionaryResource, layoutResource, numberOfPoints, writer)).doWork();

    }
}
