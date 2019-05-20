package com.lh.kete.algorithm.common

import com.lh.kete.utils.KeteUtils

class PolylineModel private constructor(private val mPoints: ArrayList<Point>) {
    internal fun getPointList(): List<Point> {
        return mPoints
    }

    companion object {
        const val N_POINTS: Int = 100
    }

    class Builder {
        private var nPoints = ArrayList<Point>()

        constructor(path: Path) {
            val len = path.getPathLength()
            val pointList = path.getPointList()
            val interval = len / N_POINTS
            if (pointList.size <= 1)
                return
            var iterPoint = pointList[0]
            nPoints.add(Point(iterPoint.x, iterPoint.y))
            var nextPoint = 1
            for (i in 1 until N_POINTS) {
                // Step 1: calculate distance with next point
                var distance = 0f
                while (distance < interval) {
                    if (nextPoint >= pointList.size)
                        break
                    val tmpDistance =
                        KeteUtils.distance(iterPoint.x, iterPoint.y, pointList[nextPoint].x, pointList[nextPoint].y)
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
                        val fX = iterPoint.x + (pointList[nextPoint].x - iterPoint.x) * percentagePoint
                        val fY = iterPoint.y + (pointList[nextPoint].y - iterPoint.y) * percentagePoint
                        iterPoint = Point(fX, fY)
                        break
                    }
                }

                // Nếu không có nextPoint thì lấy point cuối cùng - Có thể length sai, hoặc sai số từ float
                if (nextPoint >= pointList.size)
                    break
                // Lấy iterPoint làm điểm tiếp theo
                else
                    nPoints.add(Point(iterPoint.x, iterPoint.y))
            }
        }

        fun build(): PolylineModel {
            return PolylineModel(nPoints)
        }
    }
}