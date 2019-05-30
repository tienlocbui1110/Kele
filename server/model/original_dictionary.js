const db = require("../db")
const OriginalDictionary = {}
const TABLE_ORG_DICTIONARY = "OriginalDictionary";

OriginalDictionary.getAll = (callback) => {
    db.query(`SELECT * FROM ${TABLE_ORG_DICTIONARY}`, [], callback)
}

module.exports = OriginalDictionary