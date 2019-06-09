const db = require("../db")
const Point = {}
const TABLE_POINT = "Point";

Point.insert = (points, callback) => {
    db.query(`INSERT IGNORE INTO ${TABLE_POINT}(x, y) VALUES ?`, [points], callback)
}

Point.insert = (points, cnn, callback) => {
    if (cnn !== undefined && cnn != null)
        cnn.query(`INSERT IGNORE INTO ${TABLE_POINT}(x, y) VALUES ?`, [points], callback)
    else
        Point.insert(points, callback)
}

module.exports = Point