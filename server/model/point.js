const db = require("../db")
const Point = {}
const TABLE_POINT = "Point";

Point.insert = (points, callback) => {
    db.query(`INSERT IGNORE INTO ${TABLE_POINT}(x, y) VALUES ?`,[points], callback)
}

module.exports = Point