package com.lh.component.common;

public class Point implements pl.luwi.series.reducer.Point {
    private Pair<Double, Double> mPoint;
    private static final double FLOAT_ERROR = 0.00001f;

    public Point(double x, double y) {
        mPoint = new Pair<>(x, y);
    }

    public void setPosition(double newX, double newY) {
        mPoint = new Pair<>(newX, newY);
    }

    public double x() {
        return mPoint.first;
    }

    public double y() {
        return mPoint.second;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            return this.x() - ((Point) o).x() <= FLOAT_ERROR && this.y() - ((Point) o).y() <= FLOAT_ERROR;
        }
        return super.equals(o);
    }

    @Override
    public double getX() {
        return x();
    }

    @Override
    public double getY() {
        return y();
    }
}
