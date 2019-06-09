const db = require("../db")
const PointModelDetails = {}
const TABLE_POINT_MODEL_DETAILS = "PointModelDetails";
const Point = require("./point")

PointModelDetails.insert = (modelId, points, callback) => {
    let updatedPoints = points.map((value, i) => {
        return [value.x, value.y]
    })

    points = points.map((value, i) => {
        return [modelId, i, value.x, value.y]
    })

    Point.insert(updatedPoints, () => {
        db.query(`INSERT INTO ${TABLE_POINT_MODEL_DETAILS}(model, idx, x, y) VALUES ?`, [points], callback)
    })
}

PointModelDetails.insert = (modelId, points, cnn, callback) => {
    let updatedPoints = points.map((value, i) => {
        return [value.x, value.y]
    })

    points = points.map((value, i) => {
        return [modelId, i, value.x, value.y]
    })

    Point.insert(updatedPoints, cnn, () => {
        if (cnn !== undefined && cnn != null)
            cnn.query(`INSERT INTO ${TABLE_POINT_MODEL_DETAILS}(model, idx, x, y) VALUES ?`, [points], callback)
        else
            db.query(`INSERT INTO ${TABLE_POINT_MODEL_DETAILS}(model, idx, x, y) VALUES ?`, [points], callback)
    })
}

PointModelDetails.getFromModelSortIdx = (modelId, callback) => {
    db.query(`SELECT * FROM ${TABLE_POINT_MODEL_DETAILS} WHERE model=? ORDER BY idx`, [modelId], callback)
}

module.exports = PointModelDetails