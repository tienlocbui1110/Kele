const Algorithm = {}

Algorithm.distance = (pointA, pointB) => {
    return Math.sqrt((Math.pow(pointA.x - pointB.x, 2)) + (Math.pow(pointA.y - pointB.y, 2)))
}

Algorithm.distancePoints = (points) => {
    let sum = 0
    for (var i = 1; i < points.length; i++) {
        sum += Algorithm.distance(points[i], points[i - 1])
    }
    return sum
}

Algorithm.averageDistanceFromTwoModel = (points1, points2) => {
    let sum = 0
    for (var i = 0; i < points1.length; i++) {
        sum += Algorithm.distance(points1[i], points2[i])
    }
    return sum / points1.length
}

module.exports = Algorithm