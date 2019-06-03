package com.lh.kete.algorithm.common

import com.lh.kete.utils.KeteUtils
import java.util.*

class Path private constructor(private val mPoints: List<Point>) {
    private var mLength: Float = 0f
    private var polylineCached: PolylineModel? = null
    init {
        if (mPoints.size <= 1) {
            mLength = 0f
        } else {
            for (i in 1 until mPoints.size) {
                mLength += KeteUtils.distance(mPoints[i].x, mPoints[i].y, mPoints[i - 1].x, mPoints[i - 1].y)
            }
        }
    }

    fun toPolylineModel(): PolylineModel {
        polylineCached = polylineCached?:PolylineModel.Builder(this).build()
        return polylineCached?: PolylineModel.Builder(this).build()
    }

    // Unit: %
    internal fun getPointList(): List<Point> {
        return mPoints
    }

    internal fun getPathLength(): Float {
        return mLength
    }

    internal fun isValid(): Boolean {
        return mPoints.size > 1
    }

    class Builder {
        private var nPoints = LinkedList<Point>()

        fun appendPoint(point: Point) {
            if ((nPoints.size > 0 && nPoints.last() != point) || nPoints.isEmpty()) {
                nPoints.add(point)
            }
        }

        fun build(): Path {
            return Path(nPoints)
        }

        fun reset() {
            nPoints = LinkedList()
        }
    }
}