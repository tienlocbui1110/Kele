const db = require("./db")
const fs = require("fs")

const TABLE_LAYOUT = "Layout";
const TABLE_ORG_DICTIONARY = "OriginalDictionary";
const TABLE_INPUT_METHOD = "InputMethod";
const TABLE_LAYOUT_DETAILS = "LayoutDetails";
const TABLE_ALT_DICTIONARY = "AlternativeDictionary";
const TABLE_POINT_MODEL = "PointModel";
const TABLE_CONFLICT = "Conflict";
const TABLE_USER_TRACKING = "UserTracking";
const TABLE_POINT_MODEL_DETAILS = "PointModelDetails";
const TABLE_POINT = "Point";

const DEFAULT_INPUT_METHOD = "vni_sign_in_last"

// NOTE : PointModel type có 2 loại : 0 -> model tạo từ layout; 1: model do user gửi về.

db.connect((err, connection) => {
    if (err) throw err;
    // STEP 1: CREATE ALL TABLE IF EXISTS
    let CREATE_TABLE_LAYOUT = `CREATE TABLE IF NOT EXISTS ${TABLE_LAYOUT}(
        id VARCHAR(10) PRIMARY KEY,
        hash CHAR(32) NOT NULL
    )`

    let CREATE_TABLE_ORG_DICTIONARY = `CREATE TABLE IF NOT EXISTS ${TABLE_ORG_DICTIONARY}(
        word VARCHAR(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin PRIMARY KEY 
    )`

    let CREATE_TABLE_INPUT_METHOD = `CREATE TABLE IF NOT EXISTS ${TABLE_INPUT_METHOD}(
        id TINYINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
        type VARCHAR(50) NOT NULL
    )`

    let CREATE_TABLE_LAYOUT_DETAILS = `CREATE TABLE IF NOT EXISTS ${TABLE_LAYOUT_DETAILS}(
        id VARCHAR(10),
        input_method TINYINT UNSIGNED NOT NULL,
        avg_distance FLOAT,
        PRIMARY KEY(id, input_method),
        FOREIGN KEY (id) REFERENCES ${TABLE_LAYOUT}(id),
        FOREIGN KEY (input_method) REFERENCES ${TABLE_INPUT_METHOD}(id)
    )`

    let CREATE_TABLE_ALT_DICTIONARY = `CREATE TABLE IF NOT EXISTS ${TABLE_ALT_DICTIONARY}(
        alternative_word VARCHAR(12),
        input_method TINYINT UNSIGNED,
        original_word VARCHAR(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
        PRIMARY KEY(alternative_word, input_method),
        FOREIGN KEY (input_method) REFERENCES ${TABLE_INPUT_METHOD}(id),
        FOREIGN KEY (original_word) REFERENCES ${TABLE_ORG_DICTIONARY}(word)
    )`

    let CREATE_TABLE_POINT_MODEL = `CREATE TABLE IF NOT EXISTS ${TABLE_POINT_MODEL}(
        id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
        layout VARCHAR(10),
        n MEDIUMINT NOT NULL,
        first_x FLOAT NOT NULL,
        first_y FLOAT NOT NULL,
        word VARCHAR(12),
        input_method TINYINT UNSIGNED,
        type TINYINT UNSIGNED,
        FOREIGN KEY (layout) REFERENCES ${TABLE_LAYOUT}(id),
        FOREIGN KEY (word, input_method) REFERENCES ${TABLE_ALT_DICTIONARY}(alternative_word, input_method)
    )`

    let CREATE_TABLE_CONFLICT = `CREATE TABLE IF NOT EXISTS ${TABLE_CONFLICT}(
        id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
        word1 INT UNSIGNED NOT NULL,
        word2 INT UNSIGNED NOT NULL,
        avg_distance FLOAT,
        FOREIGN KEY (word1) REFERENCES ${TABLE_POINT_MODEL}(id),
        FOREIGN KEY (word2) REFERENCES ${TABLE_POINT_MODEL}(id)
    )`

    let CREATE_TABLE_USER_TRACKING = `CREATE TABLE IF NOT EXISTS ${TABLE_USER_TRACKING}(
        id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
        layout VARCHAR(10),
        points INT UNSIGNED,
        time INT,
        input_method TINYINT UNSIGNED NOT NULL,
        predicted VARCHAR(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
        user_chosen VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
        FOREIGN KEY (layout) REFERENCES ${TABLE_LAYOUT}(id),
        FOREIGN KEY (input_method) REFERENCES ${TABLE_INPUT_METHOD}(id),
        FOREIGN KEY (points) REFERENCES ${TABLE_POINT_MODEL}(id),
        FOREIGN KEY (predicted) REFERENCES ${TABLE_ORG_DICTIONARY}(word)
    )`

    let CREATE_TABLE_POINT = `CREATE TABLE IF NOT EXISTS ${TABLE_POINT}(
        x FLOAT NOT NULL,
        y FLOAT NOT NULL,
        PRIMARY KEY(x, y)
    )`

    let CREATE_TABLE_POINT_MODEL_DETAILS = `CREATE TABLE IF NOT EXISTS ${TABLE_POINT_MODEL_DETAILS}(
        model INT UNSIGNED,
        idx SMALLINT,
        x FLOAT,
        y FLOAT,
        PRIMARY KEY(model, idx),
        FOREIGN KEY (model) REFERENCES ${TABLE_POINT_MODEL}(id),
        FOREIGN KEY (x, y) REFERENCES ${TABLE_POINT}(x, y)
    )`

    // Create all tables
    connection.query(`
        ${CREATE_TABLE_LAYOUT};
        ${CREATE_TABLE_ORG_DICTIONARY};
        ${CREATE_TABLE_INPUT_METHOD};
        ${CREATE_TABLE_LAYOUT_DETAILS};
        ${CREATE_TABLE_ALT_DICTIONARY};
        ${CREATE_TABLE_POINT_MODEL};
        ${CREATE_TABLE_CONFLICT};
        ${CREATE_TABLE_USER_TRACKING};
        ${CREATE_TABLE_POINT};
        ${CREATE_TABLE_POINT_MODEL_DETAILS};
    `, async (error, results, fields) => {
            if (error) throw error
            let log = await addDefaultData()
            console.log(log)
            log = await addDefaultInputMethod()
            console.log(log)
            log = await addDefaultAlternativeDictionary()
            console.log(log)
        }
    )
});

function addDefaultData() {
    return new Promise(resolve => {
        // Step 1: checking original dictionary
        const OriginalDictionary = require("./model/original_dictionary")
        OriginalDictionary.getAll((error, results, fields) => {
            if (error) throw error
            if (results.length == 0) {
                // Add dictionary
                fs.readFile("./data/default/dictionary.txt", 'utf8', function (err, contents) {
                    if (err) throw err
                    let lines = contents.split(/\r?\n/).map(line => { return [line] })
                    db.query(`INSERT INTO ${TABLE_ORG_DICTIONARY} VALUES ?`, [lines], (error, result, fields) => {
                        if (error) throw error
                        resolve(`Added ${result.affectedRows} words`)
                    })
                });
            } else {
                resolve("Original dictionary have data. Skipped.")
            }
        })
    })
}

function addDefaultInputMethod() {
    return new Promise(resolve => {
        // Step 2: checking input method
        const InputMethod = require("./model/input_method")
        InputMethod.getAll((error, results, fields) => {
            if (error) throw error
            if (results.length == 0) {
                db.query(`INSERT INTO ${TABLE_INPUT_METHOD}(type) VALUES ?`, [[[DEFAULT_INPUT_METHOD]]], (error, result, fields) => {
                    if (error) throw error
                    resolve(`Added ${DEFAULT_INPUT_METHOD} input_method`)
                })
            } else {
                resolve("Database have input method. Skipped.")
            }
        })
    })
}

function addDefaultAlternativeDictionary() {
    return new Promise(resolve => {
        // Step 3: checking alternative dictionary
        const AlternativeDictionary = require("./model/alternative_dictionary")
        const InputMethod = require("./model/input_method")
        AlternativeDictionary.getAll((error, results) => {
            if (error) throw error
            if (results.length == 0) {
                InputMethod.getFromType(DEFAULT_INPUT_METHOD, (error, result) => {
                    if (error) throw error
                    if (result.length == 0) throw Error("Default input method not found!")
                    let inputMethodId = result[0].id
                    // Add alternative dictionary
                    fs.readFile("./data/default/vni_dic.txt", 'utf8', function (err, contents) {
                        if (err) throw err
                        let lines = contents.split(/\r?\n/).map(line => { return [line] })
                        lines = lines.map(line => {
                            let words = line[0].split(/ - /)
                            if (words.length == 2) {
                                return [words[1], inputMethodId, words[0]]
                            } else {
                                throw Error(`Alternative Dictionary have error at word ${line}`)
                            }
                        })
                        db.query(`INSERT INTO ${TABLE_ALT_DICTIONARY}(alternative_word, input_method, original_word) VALUES ?`, [lines], (error, result, fields) => {
                            if (error) throw error
                            resolve(`Added ${result.affectedRows} words to AlternativeDictionary.`)
                        })
                    });
                })
            } else {
                resolve("Database have alternative dictionary. Skipped.")
            }
        })
    })
}