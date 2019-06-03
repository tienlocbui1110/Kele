const db = require("../db")
const Conflict = {}
const TABLE_CONFLICT = "Conflict";

Conflict.insert = (word1, word2, avg, callback) => {
    db.query(`INSERT INTO ${TABLE_CONFLICT}(word1, word2, avg_distance) VALUES ?`, [[[word1, word2, avg]]], callback)
}

module.exports = Conflict