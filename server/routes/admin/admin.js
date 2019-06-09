const express = require('express')
const router = express.Router()
const multer = require('multer')
const upload = multer({ dest: 'uploads/' })
const fs = require("fs")
const path = require("path")
const Ajv = require('ajv')
const ajv = Ajv()
const md5 = require("md5")

const Layout = require("../../model/layout")
const LayoutDetails = require("../../model/layout_details")
const InputMethod = require("../../model/input_method")
const AlternativeDictionary = require("../../model/alternative_dictionary")
const PointModel = require("../../model/point_model")
const PointModelDetails = require("../../model/point_model_details")
const Conflict = require("../../model/conflict")
const Algorithm = require("../../algorithm/Algorithm")

var isReady = false
var validator;

fs.readFile("data/schema-validator/schema_layout.json", "utf-8", (err, data) => {
    if (err) throw err
    validator = ajv.compile(JSON.parse(data))
    isReady = true
})

// -------- NO NEED TO UPDATE TO GIT --------------------------------- //

var testingCheck = false
router.get("/layout/dont/use/on/server", async (req, res) => {
    res.send("OK!!!")
    if (!testingCheck) {
        testingCheck = true
        for (var i = 50001; i < 50004; i++) {
            // Generate LayoutID
            let layoutId = getAutoIncrementNumber(i)
            let chars = ["Q", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "G", "H", "K", "L", "X", "C", "V", "B", "N", "M", "Z", "J", "W", "F"]
            chars = shuffle(chars)
            let layout = `{
                "version": 1,
                "id": "${layoutId}",
                "button": [
                  {
                    "char": "´",
                    "width": 9.1,
                    "computing_char": "1",
                    "height": 22.5,
                    "x": 0,
                    "y": 2
                  },
                  {
                    "char": "\`",
                    "computing_char": "2",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 10.1,
                    "y": 2
                  },
                  {
                    "char": "?",
                    "computing_char": "3",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 20.2,
                    "y": 2
                  },
                  {
                    "char": "~",
                    "computing_char": "4",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 30.3,
                    "y": 2
                  },
                  {
                    "char": ".",
                    "computing_char": "5",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 40.4,
                    "y": 2
                  },
                  {
                    "char": "^",
                    "computing_char": "6",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 50.5,
                    "y": 2
                  },
                  {
                    "char": " ̉",
                    "computing_char": "7",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 60.6,
                    "y": 2
                  },
                  {
                    "char": "ˇ",
                    "computing_char": "8",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 70.7,
                    "y": 2
                  },
                  {
                    "char": "-",
                    "computing_char": "9",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 80.8,
                    "y": 2
                  },
                  {
                    "char": "DEL",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 90.9,
                    "y": 2
                  },
                  {
                    "char": "${chars[0]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 0,
                    "y": 26.5
                  },
                  {
                    "char": "${chars[1]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 10.1,
                    "y": 26.5
                  },
                  {
                    "char": "${chars[2]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 20.2,
                    "y": 26.5
                  },
                  {
                    "char": "${chars[3]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 30.3,
                    "y": 26.5
                  },
                  {
                    "char": "${chars[4]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 40.4,
                    "y": 26.5
                  },
                  {
                    "char": "${chars[5]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 50.5,
                    "y": 26.5
                  },
                  {
                    "char": "${chars[6]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 60.6,
                    "y": 26.5
                  },
                  {
                    "char": "${chars[7]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 70.7,
                    "y": 26.5
                  },
                  {
                    "char": "${chars[8]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 80.8,
                    "y": 26.5
                  },
                  {
                    "char": "${chars[9]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 90.9,
                    "y": 26.5
                  },
                  {
                    "char": "${chars[10]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 4.55,
                    "y": 51.0
                  },
                  {
                    "char": "${chars[11]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 14.65,
                    "y": 51.0
                  },
                  {
                    "char": "${chars[12]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 24.75,
                    "y": 51.0
                  },
                  {
                    "char": "${chars[13]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 34.85,
                    "y": 51.0
                  },
                  {
                    "char": "${chars[14]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 44.95,
                    "y": 51.0
                  },
                  {
                    "char": "${chars[15]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 55.05,
                    "y": 51.0
                  },
                  {
                    "char": "${chars[16]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 65.15,
                    "y": 51.0
                  },
                  {
                    "char": "${chars[17]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 75.25,
                    "y": 51.0
                  },
                  {
                    "char": "${chars[18]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 85.35,
                    "y": 51.0
                  },
                  {
                    "char": "${chars[19]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 14.65,
                    "y": 75.5
                  },
                  {
                    "char": "${chars[20]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 24.75,
                    "y": 75.5
                  },
                  {
                    "char": "${chars[21]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 34.85,
                    "y": 75.5
                  },
                  {
                    "char": "${chars[22]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 44.95,
                    "y": 75.5
                  },
                  {
                    "char": "${chars[23]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 55.05,
                    "y": 75.5
                  },
                  {
                    "char": "${chars[24]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 65.15,
                    "y": 75.5
                  },
                  {
                    "char": "${chars[25]}",
                    "width": 9.1,
                    "height": 22.5,
                    "x": 75.25,
                    "y": 75.5
                  }
                ],
                "otherConfig": {
                  "backgroundColor": "#482659",
                  "maxHeight": 200
                },
                "commonButtonUI": {
                  "fontSize": 16
                }
              }`
            // Write layout before do anything
            fs.writeFileSync(`layout/${layoutId}.txt`, layout)
            try {
                let result = await handleLayout(layout)
                console.log(result)
            } catch (e) {
                console.log(e)
            }
        }
    }
})

function getAutoIncrementNumber(number) {
    var pad = '00000'
    var ctxt = '' + number
    return pad.substr(0, pad.length - ctxt.length) + ctxt
}

function shuffle(a) {
    for (let i = a.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [a[i], a[j]] = [a[j], a[i]];
    }
    return a;
}

// ----------------------------------------------------------------------- //

// ----------------------------------- Handle Layout file ------------------------------------------
writer = fs.createWriteStream('log/layout_inspector.log', { flags: 'a' });

function handleLayout(layout) {
    return new Promise(async (resolve, reject) => {
        if (!isReady) {
            reject({ error: "No" })
            return
        }
        // Only accept string, to calculate hash
        var hash = md5(layout)
        if (typeof layout === 'string') {
            try {
                layout = JSON.parse(layout)
            } catch (e) {
                reject(e)
                return
            }
        } else {
            reject({ error: "Layout is not string!" })
            return
        }

        if (!validator(layout)) {
            reject(validator.errors)
            return
        } else {
            // Step 3: Get all InputMethod
            try {
                var inputMethods = await getInputMethod()
            } catch {
                reject({ error: "Error when get all input method." })
                return
            }

            // Step 4: mapping button from layout
            var buttonMapper = await getButtonMapper(layout.button)
            // Step 5: Handle inputMethod
            inputMethods.forEach(async (inputMethod) => {
                // Step 6: Get all alternativeWord at current input
                try {
                    var words = await getAlternativeDictionary(inputMethod.id)
                } catch {
                    reject({ error: `Error when trying get alternative dictionary with input_method = ${inputMethod.id}` })
                    return
                }
                // Step 7: Parse word to model
                console.log("Parsing point model...")
                try {
                    await parseAltWordToPointModel(buttonMapper, layout.id, words, inputMethod.id)
                } catch (e) {
                    reject({ error: `Parse alternative word failed. Error: ` + e })
                    return
                }
                console.log("Parse point model done!")
                // Step 8: Calculate average distance -> LayoutDetails
                try {
                    console.log("Parsing average distance...")
                    var avg = await calculateAverageDistance(buttonMapper, layout.id, words, inputMethod.id)
                    console.log("Parse average distance done!")
                } catch (err) {
                    reject(err)
                    return
                }

                // Step 9: Calculate Conflict
                console.log("Calculate Conflict...")
                // await calculateConflict(layout.id, inputMethod.id, 10)
                // await calculateConflict(layout.id, inputMethod.id, 25)
                var conflicter = await calculateConflict(words)
                // await calculateConflict(layout.id, inputMethod.id, 100)
                console.log("Calculate Conflict done!")
                writer.write(`Layout ${layout.id} -- avg: ${avg} -- Pair conflict: ${conflicter.pair} -- Word conflict: ${conflicter.wordNumbs} -- Conflict Percent: ${conflicter.wordNumbs / words.length}\n`)
                resolve(`Layout ${layout.id} --- DONE!`)
            })
        }
    })
}

// ----------------- HELPER FUNCTION ---------------------------------------------------//

function checkLayoutExists(id) {
    return new Promise((resolve) => {
        Layout.getFromId(id, (err, data) => {
            if (err) {
                console.log(err)
                resolve(true)
                return
            }
            if (data.length == 0)
                resolve(false)
            else
                resolve(true)
        })
    })
}

function insertLayout(id, hash) {
    return new Promise((resolve) => {
        Layout.insert(id, hash, (err, result) => {
            if (err) {
                console.log(err)
                resolve(false)
                return
            }
            if (result.affectedRows != 0)
                resolve(true)
            else
                resolve(false)
        })
    })
}

function getInputMethod() {
    return new Promise((resolve, reject) => {
        InputMethod.getAll((err, result) => {
            if (err) {
                reject(err)
            } else {
                resolve(result)
            }
        })
    })
}

function getButtonMapper(buttons) {
    return new Promise((resolve, reject) => {
        var buttonMapper = {}
        buttons.forEach((button) => {
            let mapper
            if (button.computing_char) {
                mapper = button.computing_char
            } else {
                mapper = button.char
            }
            buttonMapper[mapper] = {
                x: button.width / 2 + button.x,
                y: button.height / 2 + button.y
            }
        })
        resolve(buttonMapper)
    })
}

function getAlternativeDictionary(inputMethod) {
    return new Promise((resolve, reject) => {
        AlternativeDictionary.getFromInputMethod(inputMethod, (err, result) => {
            if (err) {
                reject(err)
            } else {
                resolve(result)
            }
        })
    })
}

function parseWordToPoints(buttonMapper, word, nPoint) {
    let points = []
    for (var i = 0; i < word.length; i++) {
        points.push(buttonMapper[word.charAt(i)])
    }
    return getEqualPointSegment(points, nPoint)
}

function getEqualPointSegment(points, nPoint) {
    if (points.length <= 1)
        throw Error(`Word only have ${points.length} point!!!`)
    // Get Length
    let length = 0
    let result = []
    for (var i = 1; i < points.length; i++) {
        length += Algorithm.distance(points[i], points[i - 1])
    }
    let interval = length / nPoint
    let iterPoint = points[0]
    result.push(iterPoint)
    let nextPoint = 1

    for (var i = 1; i < nPoint; i++) {
        // Step 1: calculate distance with next point
        let distance = 0
        while (distance < interval) {
            if (nextPoint >= points.length)
                break
            let tmpDistance = Algorithm.distance(iterPoint, points[nextPoint])
            // Nếu distance + tmpDistance < interval, nghĩa là ta xét đoạn line tiếp theo.
            if (distance + tmpDistance < interval) {
                distance += tmpDistance
                iterPoint = points[nextPoint++]
            } else {
                // Nếu distance + tmpDistance >= interval, ta xét iterPoint dựa trên % có được.
                // Lấy part = interval - distance => ra được khoảng cách cần ở đoạn line mới
                // lấy part / tmpDistance => ra được tỉ lệ của điểm mới
                let part = interval - distance
                let percentagePoint = part / tmpDistance
                let fX = iterPoint.x + (points[nextPoint].x - iterPoint.x) * percentagePoint
                let fY = iterPoint.y + (points[nextPoint].y - iterPoint.y) * percentagePoint
                iterPoint = {
                    x: fX,
                    y: fY
                }
                break
            }
        }

        // Nếu không có nextPoint thì lấy point cuối cùng - Có thể length sai, hoặc sai số từ float
        if (nextPoint >= points.size) {
            result.push(points[points.length - 1])
            break
        }
        // Lấy iterPoint làm điểm tiếp theo
        else
            result.push(iterPoint)
    }
    return result
}

function insertToPointModel(data, points) {
    return new Promise((resolve, reject) => {
        PointModel.insert(data, points, (error, result) => {
            if (error) {
                resolve(false)
            } else {
                resolve(true)
            }
        })
    })
}

function parseAltWordToPointModel(buttonMapper, layoutId, words, inputMethodId) {
    return new Promise((resolve, reject) => {
        var count = 0
        words.forEach(async (word) => {
            let data = {
                layout: layoutId,
                word: word.alternative_word,
                input_method: inputMethodId,
                type: 0
            }
            try {
                // let points = parseWordToPoints(buttonMapper, word.alternative_word, 10)
                // await insertToPointModel(data, points)
                // points = parseWordToPoints(buttonMapper, word.alternative_word, 25)
                // await insertToPointModel(data, points)
                points = parseWordToPoints(buttonMapper, word.alternative_word, 50)
                word.points = points;
                // points = parseWordToPoints(buttonMapper, word.alternative_word, 100)
                // await insertToPointModel(data, points)
                count++
                if (count == words.length) {
                    resolve(true)
                }
            } catch (e) {
                reject(e)
            }
        })
    })
}

function calculateAverageDistance(buttonMapper, layoutId, words, inputMethodId) {
    return new Promise((resolve, reject) => {
        let sum = 0
        for (var i = 0; i < words.length; i++) {
            let word = words[i]
            for (var j = 0; j < word.alternative_word.length - 1; j++) {
                let pointA = buttonMapper[word.alternative_word.charAt(j)]
                let pointB = buttonMapper[word.alternative_word.charAt(j + 1)]
                sum += Algorithm.distance(pointA, pointB)
            }
        }
        resolve(sum / words.length)
    })
}

function updateLayoutDetails(layoutId, inputMethodId, avgDistance) {
    return new Promise((resolve) => {
        LayoutDetails.insert(layoutId, inputMethodId, avgDistance, (err, result) => {
            if (err) {
                console.log("Error when update layout details: " + err)
            }
            resolve(true)
        })
    })
}

function calculateConflict(words) {
    return new Promise((resolve, reject) => {
        var conflicter = {
            pair: 0
        }
        var dup = []
        for (var i = 0; i < words.length - 1; i++)
            for (var j = i + 1; j < words.length; j++) {
                try {
                    var avg = Algorithm.averageDistanceFromTwoModel(words[i].points, words[j].points)
                    if (avg <= 0.25) {
                        conflicter.pair++
                        dup.push(words[i].alternative_word)
                        dup.push(words[j].alternative_word)
                    }
                } catch (e) {
                    console.log({ first: models[i], second: models[j], err: e })
                }
            }
        conflicter.wordNumbs = dup.filter((value, index, self) => {
            return self.indexOf(value) === index
        }).length
        resolve(conflicter)
    })
}

function getPointModelDetails(pointModelId) {
    return new Promise((resolve) => {
        PointModelDetails.getFromModelSortIdx(pointModelId, (err, result) => {
            if (err) reject(err)
            else {
                resolve(result)
            }
        })
    })
}
// ------------------------------ END ---------------------------------------- //

module.exports = router