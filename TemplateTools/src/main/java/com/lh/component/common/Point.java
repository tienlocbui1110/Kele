package com.lh.component.common;

public class Point {
    private Pair<Float, Float> mPoint;
    private static final float FLOAT_ERROR = 0.00001f;

    public Point(float x, float y) {
        mPoint = new Pair<>(x, y);
    }

    public void setPosition(float newX, float newY) {
        mPoint = new Pair<>(newX, newY);
    }

    public float x() {
        return mPoint.first;
    }

    public float y() {
        return mPoint.second;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            return this.x() - ((Point) o).x() <= FLOAT_ERROR && this.y() - ((Point) o).y() <= FLOAT_ERROR;
        }
        return super.equals(o);
    }
}
