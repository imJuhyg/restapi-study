const express = require('express')
const app = express()
const port = 3000

app.listen(port, () => {
    console.log('app.listen')
})

const mysql = require('mysql')
const pool = mysql.createPool({
    connectionLimit : 10,
    waitForConnections : true, // 풀에 여유 커넥션이 없는 경우 대기 여부
    host : 'localhost',
    user : 'temp_user',
    password : '1234',
    database : 'Sample_DB'
})

app.get('/', (req, res) => {
    res.send("REST 규칙에 맞게 URI를 입력해보세요!")
})

// 1. GET
// 1-1 데이터베이스에 저장된 모든 User 정보 가져오기
app.get('/users', (req, res, next) => {
    pool.getConnection((error, connection) => {
        if(error) { // DB에러 발생
            console.log(`Connection error (message: ${error})`)
            connection.release() // 커넥션 반환
        }
        
        connection.query('SELECT * FROM Users', (error, results, field) => {
            if(error) console.log(error)
            try {
                res.send(results)
                res.end()
            } catch(error) {
                console.log(error)
            }
            connection.release()
        })
    })
})

// 1-2 모든 유저의 나이 정보 가져오기
app.get('/users/ages', (req, res) => {
    pool.getConnection((error, connection) => {
        if(error) {
            console.log(`Connection error (message: ${error})`)
            connection.release()
        }

        connection.query('SELECT age FROM users', (error, results, field) => {
            if(error) console.log(error)
            try {
                res.send(results)
                res.end()
            } catch(error) {
                console.log(error)
            }
            connection.release()
        })
    })
})

// 1-3 연관 관계(id 가 ~~인 users 리소스 중 name 리소스 가져오기)
app.get('/users/:userid/name', (req, res, next) => {
    pool.getConnection((error, connection) => {
        if(error) {
            console.log(`Connection error (message: ${error})`)
            connection.release()
        }
    
        var param_userid = req.params.userid // 요청 정보에서 ':userid' 값을 가져온다
        connection.query(`SELECT name FROM Users WHERE id='${param_userid}'`, (error, results, field) => {
            if(error) console.log(error)
            try {
                res.send(results)
                res.end()
            } catch(error) {
                console.log(error)
            }
            connection.release()
        })
    })
})