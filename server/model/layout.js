const db = require("../db")
const Layout = {}
const TABLE_LAYOUT = "Layout";

Layout.getFromId = (id, callback) => {
    db.query(`SELECT * from ${TABLE_LAYOUT} where id=?`, [id], callback)
}

Layout.insert = (id, hash, callback) => {
    db.query(`INSERT INTO ${TABLE_LAYOUT}(id, hash) VALUES ?`, [[[id, hash]]], callback)
}

module.exports = Layout