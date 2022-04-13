# REST API의 개념부터 실습까지

### 목차

---

### 개요
이 문서는 [REST API 제대로 알고 사용하기](https://meetup.toast.com/posts/92), [위키백과](https://ko.wikipedia.org/wiki/REST)를 참조하여 만들었습니다.

#### 진행순서
1. REST API의 기초와 설계 가이드
2. Node.js, Express, Mysql로 REST API 서버 구축하기
3. Retrofit 기초와 안드로이드에서 Retrofit으로 REST API 호출해보기
4. 공공데이터포털 Open API 연동하기

---

### 개발환경
* Android, Node.js
* Express, MySQL, Retrofit2(+Gson, OkHttp3-Interceptor)

---

### 1. REST API란?
REST(Representational State Transfer)는 분산 하이퍼미디어(WWW) 시스템을 위한 소프트웨어 아키텍처의 한 형식으로, 자원을 정의하고 자원에 대한 주소를 지정하는 방법에 대한 전반을 다룹니다. HTTP의 주요 저자 중 한 사람인 로이 필딩(Roy Fielding)이 처음 소개하였으며 HTTP*의 장점을 최대한 활용할 수 있는 아키텍쳐입니다.

<details>
<summary>HTTP*</summary>
<div markdown="1">
WWW상에서 텍스트 기반의 데이터를 주고 받을 수 있는 프로토콜입니다. 클라이언트가 HTML문서(또는 JSON 데이터 및 XML)를 요청하고, 서버는 이에 대한 응답을 하는 방식으로 이루어집니다.
</div>
</details>
 

#### 1-1 REST API의 구성
* 자원(Resource)에 대한 표현(Representation)

  자원은 URI에 해당하며 자원을 표현하기 위한 이름이 정의되어야 합니다.

  예) http://example.com/korea/busan - 한국, 부산에 대한 자원을 korea, busan이라고 표현했습니다.

&nbsp;
* 행위

  자원에 대한 표현을 정의했다면 자원에 대한 행위를 정해야 합니다. 행위는 Http Method(GET, POST, PUT, DELETE)로 표현합니다.

  예) GET /example.com/korea/busan – ‘busan’에 대한 자료를 가져오는 행위

&nbsp;

---
### 2. REST API에 적용되어야 하는 조건
* <b>인터페이스 일관성</b>: 일관적인 인터페이스로 분리되어야 합니다. 이것은 URI에 지정한 자원에 대해 통일된 인터페이스를 제공해야 한다는 의미입니다.
* <b>무상태(Stateless)</b>: 요청된 작업에 대한 상태정보를 서버에 저장하지 않습니다. 따라서 Rest API 서버는 단순히 들어오는 요청만을 처리하면 됩니다.
* <b>캐시 처리 가능(Cacheable)</b>: HTTP에서 사용할 수 있는 캐싱*기능을 그대로 사용할 수 있으며 클라이언트는 서버의 응답을 캐싱할 수 있어야 합니다.

<details>
<summary>HTTP Caching*</summary>
<div markdown="1">
웹 사이트에서 이전에 사용되었던 리소스들을 캐시에 저장하였다가 재사용함으로써 웹 사이트의 반응성을 향상시킬 수 있는 기술입니다.
</div>
</details>

&nbsp;
* <b>계층형 시스템(Layered System)</b>: Rest 서버에 로드 밸런싱이나 공유 캐시 기능을 지원하는 중간 서버를 두어 시스템의 확장성과 유연성을 향상시킬 수 있습니다.
* <b>클라이언트/서버 구조</b>: 서버는 리소스를 요청할 수 있는 API를 제공하고 클라이언트는 사용자 인증, 컨텍스트(세션, 로그인 정보)등을 관리하는 역할로 분리하여 서로 간의 의존성을 줄이고 각 파트를 독립적으로 개선할 수 있어야 합니다.

&nbsp;

---
### 3. REST API 설계 가이드
#### 3-1 자원에 대한 행위 표현 - HTTP Method
|Method|설명|
|---|---|
|GET|서버로부터 리소스를 요청합니다.|
|POST|서버에 리소스 생성을 요청합니다.|
|PUT|해당 리소스를 수정합니다.|
|DELETE|해당 리소스를 삭제합니다.|

&nbsp;
#### 3-2 URI 명명 규칙
1. URI는 명사를 사용하여 정보의 자원을 표현해야 합니다.  
   잘못된 예) <b>GET /delete/korea/busan</b>  
   위의 예시는 ‘busan’자원을 삭제하라는 표현으로 볼 수 있습니다. 하지만 ‘delete’와 같은 행위가 URI에 포함되어서는 안됩니다.  
   
   올바른 예)  <b>DELETE /korea/busan</b>

2. 슬래시 구분자(/)는 자원의 계층관계를 나타내는 데 사용됩니다.  
   예) <b>http://example.com/korea/busan/haeundae</b>
   ‘heaundae’는 ‘busan’에 포함되며 ‘busan’은 ‘korea’에 포함됩니다.

3. URI의 마지막 문자로 슬래시 구분자(/)를 사용하지 않습니다.  
   자원을 표현하는 URI은 해당 자원에 유일하게 접근할 수 있는 고유한 식별자여야 합니다. 즉 URI가 다르다는 것은 자원이 다르다는 것이며 역으로 자원이 다르면 URI도 다릅니다. 이러한 규칙을 명확하게 하기 위해 URI의 마지막 문자에 슬래시 구분자(/)를 붙이지 않습니다.  

   잘못된 예) <b>http://exmaple.com/busan - http://example.com/korea/busan</b> 서로 다른 URI이지만 같은 자원이 나오는 경우는 잘못된 예입니다.  

   <b>http://example.com/korea/busan/</b> - URI의 마지막 문자에 슬래시 구분자를 붙인 경우입니다.  

   올바른 예) <b>http://example.com/korea/busan</b> 

4. 자원의 이름이 길 때, 가독성을 높이기 위해 하이픈(-)을 사용할 수 있습니다. 또한 밑줄(_)은 사용하지 않습니다. 이 또한 가독성을 위해 밑줄 대신 하이픈(-)을 사용합니다.  
   예) <b>http://example.com/korea/busan/haeundae-beach</b>

5. URI는 대소문자를 구분하므로 자원에 대문자를 사용하는 것을 피해야합니다.

6. 파일 확장자를 URI에 포함시키지 않습니다. 파일 확장자를 표현해야 할 경우 응답으로 받고 싶은 타입을 명시할 수 있는 Accept header를 사용합니다.

&nbsp;
#### 3-3 URI에서 리소스간의 관계를 표현하기
소유(has)관계를 표현하고 싶은 경우는 아래의 예시처럼 사용할 수 있습니다.
* /리소스명/{리소스ID}/리소스와 관계있는 다른 리소스명  
  예) <b>/users/1/devices</b> – ‘1번’ 유저가 가진 devices를 표현할 수 있습니다.

&nbsp;

---
### 4. HTTP 응답 코드
클라이언트 요청에 따른 서버의 응답 코드입니다.
|의미|응답 코드|설명|
|---|---|---|
|성공|200|요청이 성공적으로 수행되었음을 의미합니다.|
||201|POST를 통해 리소스 생성 작업을 요청하고, 성공적으로 생성되었음을 의미합니다.|
|리다이렉션|301|리소스에 대한 URI가 다른 URI로 변경되었음을 의미합니다. 이를 응답할 때 Location Header에 변경된 URI를 명시해야 합니다.|
|클라이언트 요청 에러|400|클라이언트가 잘못된 요청을 했을 경우입니다.|
||401|클라이언트가 인증되지 않은 상태에서 인증이 필요한 자원을 요청한 경우입니다.|
||403|클라이언트의 인증 상태와 관계없이 응답하고 싶지 않은 리소스를 요청한 경우입니다.|
||404|클라이언트가 존재하지 않는 자원에 대해 요청한 경우입니다.|
||405|클라이언트가 자원을 요청할 때 사용할 수 없는 Http Method를 사용한 경우입니다.|

&nbsp;

---
### 5. Express와 MySQL로 REST API 서버 구축하기
* 준비물: vscode, Node.js, Express, MySQL

#### 5-1 Node.js 설치 및 설치 확인
vscode 터미널에 아래와 같이 입력하여 Node.js가 제대로 설치 되었는지 확인 할 수 있습니다.
```
> node -v && npm -v
```

&nbsp;

#### 5-2 Express/MySQL 모듈 설치
프로젝트 폴더를 생성하고 생성한 폴더로 이동하여 Express를 설치합니다.
```
> npm init

...

// 입력 생략 가능
package name: (rest-server) 
version: (1.0.0) 
description: 
entry point: (example.js) 
test command: 
git repository: 
keywords: 
author: 
license: (ISC) 

...

Is this OK? (yes)

> npm install express --save // Express 설치
> npm install mysql // MySQL 설치
```

&nbsp;

#### 5-3 데이터베이스 설계
REST API 서버에서 사용할 데이터베이스를 아래와 같이 설계했습니다.  

<b>Users</b>
|Field|Type|Null|Key|Default|
|---|---|---|---|---|
|id|varchar(20)|NO|PRI|NULL|
|password|varchar(20)|NO||NULL|
|name|varchar(20)|NO||NULL|
|age|int|NO||NULL|

&nbsp;

#### 5-4 모듈 Import 및 Connection pool 설정
```javascript
// Express
const express = require('express')
const app = express()
const port = 3000

app.listen(port, () => {
    console.log('app.listen')
})

// MySQL
const mysql = require('mysql')
const pool = mysql.createPool({
    connectionLimit : 10,
    waitForConnections : true, // 풀에 여유 커넥션이 없는 경우 대기 여부
    host : 'localhost',
    user : 'temp_user',
    password : '1234',
    database : 'Sample_DB'
})
```

&nbsp;

#### 5-5 GET
express의 get 메소드를 통해 자원을 설정하고 함수의 두번째 인자(res)로 쿼리문의 결과를 전송할 수 있습니다.
```javascript
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
```

&nbsp;

자원에 매개변수가 포함되어야 할 경우 아래와 같이 사용할 수 있습니다.
```javascript
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
```

&nbsp;

#### 5-6 POST

&nbsp;

#### 5-6 PUT

&nbsp;

#### 5-6 DELETE

&nbsp;

---

### 6. 응답 메시지 설정 및 확인하기

&nbsp;

---

### 7. 클라이언트(안드로이드) 설정
#### 7-1
