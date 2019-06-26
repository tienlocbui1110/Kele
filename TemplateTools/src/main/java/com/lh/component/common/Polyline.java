package com.lh.component.common;

import java.util.ArrayList;
import java.util.List;

public class Polyline {
    private ArrayList<Point> mPoints;
    private float mLength = -1;

    public Polyline() {
        mPoints = new ArrayList<>();
    }

    public void addPoint(float x, float y) {
        addPoint(new Point(x, y));
    }

    public void addPoint(Point p) {
        mPoints.add(p);
        mLength = -1;
    }

    public int pointCount() {
        return mPoints.size();
    }

    public Point getPoint(int index) {
        if (index >= pointCount())
            throw new ArrayIndexOutOfBoundsException();
        return mPoints.get(index);
    }

    /**
     * This function will create n lines with same length, represent by mPoints.
     *
     * @param n is number of points.
     */
    public void createEquidistant(int n) {
        ArrayList<Point> mNewPoints = new ArrayList<>(n);
        if (pointCount() <= 1)
            return;

        float interval = getLength() / (n - 1);
        Point flagPoint = getPoint(0);
        mNewPoints.add(flagPoint);
        int pointIndex = 1;
        for (int i = 1; i < n; i++) {
            float distance = 0;
            while (distance < interval) {
                if (pointIndex >= pointCount())
                    break;
                // distance from currentFlag to next point
                float nextDistance = EuclidDistance.calculate(flagPoint, getPoint(pointIndex));
                // Nếu distance + nextDistance < interval, nghĩa là ta xét đoạn line tiếp theo.
                if (distance + nextDistance < interval) {
                    distance += nextDistance;
                    flagPoint = getPoint(pointIndex++);
                } else {
                    // Nếu distance + nextDistance >= interval, ta xét flagPoint dựa trên % có được.
                    // Lấy part = interval - distance => ra được khoảng cách cần ở đoạn line mới
                    // lấy part / nextDistance => ra được tỉ lệ của điểm mới
                    float part = interval - distance;
                    float percentagePoint = part / nextDistance;
                    float fX = flagPoint.x() + (getPoint(pointIndex).x() - flagPoint.x()) * percentagePoint;
                    float fY = flagPoint.y() + (getPoint(pointIndex).y() - flagPoint.y()) * percentagePoint;
                    flagPoint = new Point(fX, fY);
                    break;
                }
            }

            // Nếu tới điểm cuối thì lấy point cuối cùng - Có thể length sai, hoặc sai số từ float
            if (pointIndex >= pointCount()) {
                mNewPoints.add(getPoint(pointCount() - 1));
                break;
            }
            // Lấy flagPoint làm điểm tiếp theo
            else {
                mNewPoints.add(flagPoint);
            }
        }
        mPoints.clear();
        mPoints = mNewPoints;
    }

    public float getLength() {
        if (mLength >= 0)
            return mLength;
        mLength = 0;
        for (int i = 1; i < pointCount(); i++) {
            mLength += EuclidDistance.calculate(getPoint(i), getPoint(i - 1));
        }
        return mLength;
    }

    public List<Point> getPoints() {
        return mPoints;
    }

    public Polyline clone() {
        Polyline newPolyline = new Polyline();
        newPolyline.mLength = this.mLength;
        for (int i = 0; i < mPoints.size(); i++) {
            newPolyline.mPoints.add(this.mPoints.get(i));
        }
        return newPolyline;
    }

    public String exportToCSV(String x, String y) {
        StringBuilder builder = new StringBuilder();
        builder.append(x).append(",").append("y").append("\n");
        for (int i = 0; i < mPoints.size(); i++) {
            builder.append(mPoints.get(i).x()).append(",").append(mPoints.get(i).y()).append("\n");
        }
        return builder.toString();
    }
}
