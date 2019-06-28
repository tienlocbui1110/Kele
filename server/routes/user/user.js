const express = require('express')
const router = express.Router()
const fs = require("fs")
const Ajv = require('ajv')
const ajv = Ajv()
const InputMethod = require("../../model/input_method")
const UserTracking = require("../../model/user_tracking")
const PointModel = require("../../model/point_model")

var isReady = false
var validator;

fs.readFile("data/schema-validator/schema_user.json", "utf-8", (err, data) => {
    if (err) throw err
    validator = ajv.compile(JSON.parse(data))
    isReady = true
})

router.post("/", (req, res) => {
    if (!isReady) {
        res.send("Not ready.")
        return
    }
    let data = req.body
    var valid = validator(data)
    if (!valid) {
        let errorJSON = JSON.stringify({ "User": validator.errors }, null, 4)
        console.log(errorJSON)
        res.status(400)
        res.send(errorJSON)
    } else {
        // Post valid. Insert to UserTracking
        res.send("OK")
        data.type = 1

        InputMethod.getFromType(data.input_method, (error, result) => {
            if (error) {
                console.log(error)
                return
            }
            if (result.length != 0) {
                let tmpInputMethod = result[0].id
                data.input_method = null
                data.layout = data.layoutId
                data.user_chosen = data.chosen
                // Step 1: Add to PointModel
                PointModel.insert(data, data.points, (err, res) => {
                    if (err) {
                        console.log(err)
                        return
                    }
                    // Step 2: Add to UserTracking
                    data.input_method = tmpInputMethod
                    data.points = res.insertId
                    if (!data.input_type) {
                        data.input_type = 0
                    }
                    UserTracking.insert(data, (error) => {
                        if (error) {
                            console.log(error)
                            return
                        }
                    })
                })

            } else {
                console.log(`${data.input_method} not found!`)
            }
        })
    }
})

router.get("/status", (req, res) => {
    res.send("ok")
})

module.exports = router