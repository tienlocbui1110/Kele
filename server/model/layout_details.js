const db = require("../db")
const LayoutDetails = {}
const TABLE_LAYOUT_DETAILS = "LayoutDetails";

LayoutDetails.insert = (layoutId, inputMethodId, avgDistance, callback) => {
    db.query(`INSERT INTO ${TABLE_LAYOUT_DETAILS}(id, input_method, avg_distance) VALUES ?`, [[[layoutId, inputMethodId, avgDistance]]], callback)
}

module.exports = LayoutDetails