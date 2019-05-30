const express = require("express")
const app = express()
const bodyParser = require('body-parser')
const morgan = require("morgan")
const fs = require("fs")
const path = require("path")
const PORT = 3000

// Init database
require("./initdb")

// Init middleware
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }));
var accessLogStream = fs.createWriteStream(path.join(__dirname, 'log/access.log'), { flags: 'a' })
app.use(morgan('combined', { stream: accessLogStream }))
app.use(morgan('dev'))

// Init routes
app.use('/user', require("./routes/user/user"))
app.use('/zadmjn2019z', require("./routes/admin/admin"))


// -------------- Start server ------------------- //
app.listen(PORT, () => {
    console.log(`Server started at port ${PORT}`)
})