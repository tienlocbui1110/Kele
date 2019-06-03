const db = require("../db")
const InputMethod = {}
const TABLE_INPUT_METHOD = "InputMethod";

InputMethod.getAll = (callback) => {
    db.query(`SELECT * FROM ${TABLE_INPUT_METHOD}`, [], callback)
}

InputMethod.getFromType = (type, callback) => {
    db.query(`SELECT * FROM ${TABLE_INPUT_METHOD} WHERE type=?`, [type], callback)
}

module.exports = InputMethod