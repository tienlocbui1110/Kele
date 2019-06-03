const mysql = require('mysql');
const pool = mysql.createPool({
    host: '127.0.0.1',
    user: 'root',
    password: '',
    database: 'Kete',
    connectionLimit : 100,
    multipleStatements: true
});

module.exports = {
    query: (query, value, callback) => {
        pool.getConnection((err, connection) => {
            if (err) {
                if (connection != 'undefined')
                    connection.release();
                callback(err)
                return
            }
            connection.query(query, value, (err, result, fields) => {
                connection.release();
                callback(err, result, fields)
            });
        });
    },
    connect: (callback) => {
        pool.getConnection((err, connection) => {
            if (err) {
                connection.release();
                callback(err)
                return
            }
            callback(err, connection)
        });
    }
}