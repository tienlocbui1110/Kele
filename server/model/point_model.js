const db = require("../db")
const PointModel = {}
const TABLE_POINT_MODEL = "PointModel";

const PointModelDetails = require("./point_model_details")

// NOTE : PointModel type có 2 loại : 0 -> model tạo từ layout; 1: model do user gửi về.

// put points = array[point] 
// put data.layout, data.word, data.input_method, data.type
PointModel.insert = (data, points, callback) => {
    data.n = points.length
    data.first_x = points[0].x
    data.first_y = points[0].y
    db.query(`INSERT INTO ${TABLE_POINT_MODEL}(layout, n, first_x, first_y, word, input_method, type) VALUES ?`, [[[data.layout, data.n, data.first_x, data.first_y, data.word, data.input_method, data.type]]], (err, result) => {
        if (err) {
            callback(err)
            return
        }
        if (result.affectedRows != 0) {
            // Add To PointModelDetails
            let id = result.insertId
            PointModelDetails.insert(id, points, (err) => {
                if (err) {
                    callback(err)
                    return
                }
                callback(err, result)
            })
        } else {
            callback(err, result)
        }
    })
}

PointModel.get = (layoutId, n, input_method, type, callback) => {
    db.query(`SELECT * FROM ${TABLE_POINT_MODEL} WHERE layout=? AND n=? AND input_method=? AND type=?`, [layoutId,n, input_method, type], callback)
}

module.exports = PointModel