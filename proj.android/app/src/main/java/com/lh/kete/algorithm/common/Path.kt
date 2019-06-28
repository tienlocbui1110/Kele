package com.lh.kete.algorithm.common

import com.lh.kete.utils.KeteUtils
import pl.luwi.series.reducer.SeriesReducer
import java.util.*

class Path private constructor(private var mPoints: List<Point>) {
    private var mLength: Float = 0f
    private var polylineCached: PolylineModel? = null

    init {
        if (mPoints.size <= 1) {
            mLength = 0f
        } else {
            for (i in 1 until mPoints.size) {
                mLength += KeteUtils.distance(mPoints[i].mX, mPoints[i].mY, mPoints[i - 1].mX, mPoints[i - 1].mY)
            }
        }
    }

    fun toPolylineModel(): PolylineModel {
        polylineCached = polylineCached ?: PolylineModel.Builder(this).build()
        return polylineCached ?: PolylineModel.Builder(this).build()
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

    fun reduceNoise(epsilon: Float) {
        mPoints = SeriesReducer.reduce(mPoints, epsilon.toDouble())
        polylineCached = null
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