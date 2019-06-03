const db = require("../db")
const UserTracking = {}
const TABLE_USER_TRACKING = "UserTracking";

UserTracking.insert = (data, callback) => {
    db.query(`INSERT INTO ${TABLE_USER_TRACKING}(layout, points, time, avg_distance, predicted, user_chosen, input_method) 
    VALUES ?`, [[[data.layout, data.points, data.time, data.avg_distance, data.predicted, data.user_chosen, data.input_method]]], callback)
}

module.exports = UserTracking