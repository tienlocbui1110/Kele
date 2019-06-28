const db = require("../db")
const UserTracking = {}
const TABLE_USER_TRACKING = "UserTracking";


// TYPE: 0 : dữ liệu từ user đã được chuyển thành polyline chuẩn.
// TYPE: 1 : dữ liệu từ user dạng raw
UserTracking.insert = (data, callback) => {
    db.query(`INSERT INTO ${TABLE_USER_TRACKING}(layout, points, time, type, user_chosen, input_method) 
    VALUES ?`, [[[data.layout, data.points, data.time, data.input_type, data.user_chosen, data.input_method]]], callback)
}

module.exports = UserTracking