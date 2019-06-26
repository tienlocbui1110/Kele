//package com.lh.component.template;
//
//import com.lh.IPackage.IWriter;
//import com.lh.component.common.*;
//import com.lh.component.writer.DefaultWriter;
//import org.ejml.simple.SimpleMatrix;
//
//public class EuclidAdvTemplate extends BaseTemplate {
//    private IWriter mWriter;
//
//    public EuclidAdvTemplate(String dictionaryResource, String layoutResource, int numberOfPoints, IWriter writer) {
//        super(dictionaryResource, layoutResource, numberOfPoints);
//        this.mWriter = writer;
//    }
//
//
//    public EuclidAdvTemplate(String layoutResource, int numberOfPoints) {
//        this("vni_dic.txt", layoutResource, numberOfPoints);
//    }
//
//    public EuclidAdvTemplate(String dictionaryResource, String layoutResource, int numberOfPoints) {
//        this(dictionaryResource, layoutResource, numberOfPoints, new DefaultWriter());
//    }
//
//    @Override
//    public void onWorking() {
//        for (int i = 0; i < mUserTracking.size(); i++) {
//            predict(mUserTracking.getUser(i));
//        }
//    }
//
//    private void predict(User userTracking) {
//        PredictorResult result = new PredictorResult();
//        float xRange = 10f;
//        float yRange = 20f;
//        float minX = userTracking.swipeModel.getPoint(0).x() - xRange;
//        float maxX = userTracking.swipeModel.getPoint(0).x() + xRange;
//        float minY = userTracking.swipeModel.getPoint(0).y() - yRange;
//        float maxY = userTracking.swipeModel.getPoint(0).y() + yRange;
//
//        for (int i = 0; i < mDictionary.size(); i++) {
//            Polyline baseModel = mDictionary.getTranslatedWord(i);
//            String predictWord = mDictionary.getOriginalWord(i).getWord();
//            if (baseModel.getPoint(0).x() >= minX && baseModel.getPoint(0).x() <= maxX &&
//                    baseModel.getPoint(0).y() >= minY && baseModel.getPoint(0).y() <= maxY) {
//                SimpleMatrix baseMatrix = buildMatrix(baseModel);
//                SimpleMatrix userMatrix = buildMatrix(userTracking.swipeModel);
//                SimpleMatrix euclidMatrix = userMatrix.minus(baseMatrix).elementPower(2);
//                float euclidDistance = (float) Math.sqrt(euclidMatrix.elementSum());
//                result.addResult(euclidDistance, predictWord);
//            }
//        }
//
//        // Check if predict different than user.
//        String[] nearestWord = result.getResult();
//        for (String s : nearestWord) {
//            if (userTracking.chosenWord.equals(s)) {
//                mWriter.writeln("OK - ID: " + userTracking.trackId + " - user word: " + userTracking.chosenWord + " - predict: " + s);
//                return;
//            }
//        }
//        String predicted = nearestWord.length > 0 ? nearestWord[0] : "<undefined>";
//        mWriter.writeln("WRONG - ID: " + userTracking.trackId + " - user word: " + userTracking.chosenWord + " - predict: " + predicted);
//    }
//
//    // x1 x2 x3 xn
//    // y1 y2 y3 yn
//    private SimpleMatrix buildMatrix(Polyline polyline) {
//        SimpleMatrix matrix = new SimpleMatrix(1, polyline.pointCount() * 2);
//        for (int i = 0; i < polyline.pointCount(); i++) {
//            Point point = polyline.getPoint(i);
//            matrix.setRow(0, 2 * i, point.x());
//            matrix.setRow(0, 2 * i + 1, point.y());
//        }
//        return matrix;
//    }
//}
