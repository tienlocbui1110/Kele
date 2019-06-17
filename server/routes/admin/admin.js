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
router.get("/", (req, res) => {
    res.render("admin/index")
})

router.get("/upload/layout", (req, res) => {
    res.render("admin/interactive/upload_layout", { success: false })
})

router.post("/upload/layout", upload.single("layout"), (req, res) => {
    // Render to user first.
    res.render("admin/interactive/upload_layout", { success: true })
    let layout = req.file
    if (layout) {
        fs.readFile(layout.path, 'utf8', (err, content) => {
            if (err) {
                console.log(err)
                return
            }
            handleLayout(content)
        })
    }
})

// ----------------------------------- Handle Layout file ------------------------------------------

async function handleLayout(layout) {
    if (!isReady)
        return
    // Only accept string, to calculate hash
    var hash = md5(layout)
    if (typeof layout === 'string') {
        try {
            layout = JSON.parse(layout)
        } catch {
            return
        }
    } else {
        return
    }

    if (!validator(layout)) {
        let errorJSON = JSON.stringify({ "User": validator.errors }, null, 4)
        console.log(errorJSON)
    } else {
        // Layout valid. Handling
        // Step 1: Check if layout in database.
        let check = await checkLayoutExists(layout.id)
        if (check) {
            console.log("Layout exists. Skipped.")
            return
        }
        // Step 2: Insert layout to database.
        check = await insertLayout(layout.id, hash)
        if (!check) return
        // Step 3: Get all InputMethod
        try {
            var inputMethods = await getInputMethod()
        } catch {
            console.log("Error when get all input method.")
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
                console.log(`Error when trying get alternative dictionary with input_method = ${inputMethod.id}`)
                return
            }
            // Step 7: Parse word to model
            console.log("Parsing point model...")
            try {
                await parseAltWordToPointModel(buttonMapper, layout.id, words, inputMethod.id)
            } catch (e) {
                console.log(`Parse alternative word failed. Error: ` + e)
                return
            }
            console.log("Parse point model done!")
            // Step 8: Calculate average distance -> LayoutDetails
            try {
                console.log("Parsing average distance...")
                let avg = await calculateAverageDistance(buttonMapper, layout.id, words, inputMethod.id)
                updateLayoutDetails(layout.id, inputMethod.id, avg)
                console.log("Parse average distance done!")
            } catch (err) {
                console.log(err)
            }

            // Step 9: Calculate Conflict
            console.log("Calculate Conflict...")
            await calculateConflict(layout.id, inputMethod.id, 10)
            await calculateConflict(layout.id, inputMethod.id, 25)
            await calculateConflict(layout.id, inputMethod.id, 50)
            await calculateConflict(layout.id, inputMethod.id, 100)
            console.log("Calculate Conflict done!")
        })
    }
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
    let interval = length / (nPoint - 1)
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
                let points = parseWordToPoints(buttonMapper, word.alternative_word, 10)
                await insertToPointModel(data, points)
                points = parseWordToPoints(buttonMapper, word.alternative_word, 25)
                await insertToPointModel(data, points)
                points = parseWordToPoints(buttonMapper, word.alternative_word, 50)
                await insertToPointModel(data, points)
                points = parseWordToPoints(buttonMapper, word.alternative_word, 100)
                await insertToPointModel(data, points)
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

function calculateConflict(layoutId, inputMethodId, n) {
    return new Promise((resolve) => {
        PointModel.get(layoutId, n, inputMethodId, 0, async (err, pointModels) => {
            if (err) {
                console.log("Error when calculate average distance: " + err)
                return
            }
            // result => Array of pointModel
            let models = []
            for (var i = 0; i < pointModels.length; i++) {
                let res = await getPointModelDetails(pointModels[i].id)
                models.push(res)
            }

            for (var i = 0; i < models.length - 1; i++)
                for (var j = i + 1; j < models.length; j++) {
                    let avg = Algorithm.averageDistanceFromTwoModel(models[i], models[j])
                    if (avg <= 0.25) {
                        Conflict.insert(pointModels[i].id, pointModels[j].id, avg, (err, result) => {
                            if (err) {
                                console.log(`Conflict inserting error: ${err}`)
                            }
                        })
                    }
                }
            resolve(true)
        })
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