const express = require("express")
const app = express()
const bodyParser = require('body-parser')
const morgan = require("morgan")
const fs = require("fs")
const path = require("path")
const util = require('util');
const process = require("process")
const PORT = (process.env.NODE_ENV && process.env.NODE_ENV.indexOf('production') > -1) ? 80 : 3000

// log
var morganLog = fs.createWriteStream(path.join(__dirname, 'log/access.log'), { flags: 'a' })
var consoleLog = fs.createWriteStream(path.join(__dirname, 'log/console.log'), { flags: 'a' });
app.use(morgan('combined', { stream: morganLog }))
app.use(morgan('dev'))
console.log = function (d) {
    let date = new Date()
    consoleLog.write(date + " - " + util.format(d) + '\n');
    process.stdout.write(date + " - " + util.format(d) + '\n');
};

// Init database
require("./initdb")

// View engine
app.use(express.static('public'))
app.set('view engine', 'ejs');

// Init middleware
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }));

// Init routes
app.use('/user', require("./routes/user/user"))
app.use('/zadmjn2019z', require("./routes/admin/admin"))

// -------------- DEFAULT ROUTE ------------------ //

app.get("*", (req, res) => {
    res.status(404)
    res.render("http404")
})

// -------------- Start server ------------------- //
app.listen(PORT, () => {
    console.log(`Server started at port ${PORT}`)
})