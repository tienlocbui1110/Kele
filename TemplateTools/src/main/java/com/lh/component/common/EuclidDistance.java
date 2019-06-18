package com.lh.component.common;

public final class EuclidDistance {
    public static float calculate(Point p1, Point p2) {
        return (float) Math.sqrt((p1.x() - p2.x()) * (p1.x() - p2.x()) + (p1.y() - p2.y()) * (p1.y() - p2.y()));
    }
}
