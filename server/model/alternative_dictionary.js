const db = require("../db")
const AlternativeDictionary = {}
const TABLE_ALT_DICTIONARY = "AlternativeDictionary";

AlternativeDictionary.getAll = (callback) => {
    db.query(`SELECT * FROM ${TABLE_ALT_DICTIONARY}`, [], callback)
}

AlternativeDictionary.getFromInputMethod = (inputMethod, callback) => {
    db.query(`SELECT * FROM ${TABLE_ALT_DICTIONARY} where input_method=?`, [[inputMethod]], callback)
}

module.exports = AlternativeDictionary