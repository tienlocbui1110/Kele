package com.lh.kete.algorithm.common

import com.lh.kete.utils.KeteUtils

class PolylineModel private constructor(private val mPoints: ArrayList<Point>) {

    // Unit: %
    internal fun getPointList(): List<Point> {
        return mPoints
    }

    fun isValid(): Boolean {
        return mPoints.size == N_POINTS
    }

    companion object {
        var N_POINTS: Int = 0
    }

    class Builder {
        private var nPoints = ArrayList<Point>()
        private var mLength: Float

        constructor(points: ArrayList<Point>) {
            this.nPoints = points
            mLength = 0f
        }

        constructor(path: Path) {
            mLength = path.getPathLength()
            val pointList = path.getPointList()
            val interval = mLength / (N_POINTS - 1)
            if (pointList.size <= 1)
                return
            var iterPoint = pointList[0]
            nPoints.add(Point(iterPoint.mX, iterPoint.mY))
            var nextPoint = 1
            for (i in 1 until N_POINTS) {
                // Step 1: calculate distance with next point
                var distance = 0f
                while (distance < interval) {
                    if (nextPoint >= pointList.size)
                        break
                    val tmpDistance =
                            KeteUtils.distance(iterPoint.mX, iterPoint.mY, pointList[nextPoint].mX, pointList[nextPoint].mY)
                    // Nếu distance + tmpDistance < interval, nghĩa là ta xét đoạn line tiếp theo.
                    if (distance + tmpDistance < interval) {
                        distance += tmpDistance
                        iterPoint = pointList[nextPoint++]
                    } else {
                        // Nếu distance + tmpDistance >= interval, ta xét iterPoint dựa trên % có được.
                        // Lấy part = interval - distance => ra được khoảng cách cần ở đoạn line mới
                        // lấy part / tmpDistance => ra được tỉ lệ của điểm mới
                        val part = interval - distance
                        val percentagePoint = part / tmpDistance
                        val fX = iterPoint.mX + (pointList[nextPoint].mX - iterPoint.mX) * percentagePoint
                        val fY = iterPoint.mY + (pointList[nextPoint].mY - iterPoint.mY) * percentagePoint
                        iterPoint = Point(fX, fY)
                        break
                    }
                }

                // Nếu không có nextPoint thì lấy point cuối cùng - Có thể length sai, hoặc sai số từ float
                if (nextPoint >= pointList.size) {
                    nPoints.add(pointList.last())
                    continue
                }
                // Lấy iterPoint làm điểm tiếp theo
                else {
                    nPoints.add(Point(iterPoint.mX, iterPoint.mY))
                }
            }
        }

        fun build(): PolylineModel {
            return PolylineModel(nPoints)
        }
    }
}