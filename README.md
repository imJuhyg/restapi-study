# REST API의 개념부터 실습까지

## 목차
[0. 개요](#개요)  
[1. REST API란?](#1-rest-api란)  
&nbsp; [1-1 REST API의 구성](#1-1-rest-api의-구성)  
[2. REST API에 적용되어야 하는 조건](#2-rest-api에-적용되어야-하는-조건)  
[3. REST API 설계 가이드](#3-rest-api-설계-가이드)  
&nbsp;  [3-1 자원에 대한 행위 표현 - HTTP Method](#3-1-자원에-대한-행위-표현---http-method)  
&nbsp;  [3-2 URI 명명 규칙](#3-2-uri-명명-규칙)   
&nbsp;  [3-3 URI에서 리소스간의 관계를 표현하기](#3-3-uri에서-리소스간의-관계를-표현하기)  
[4. HTTP 응답 코드](#4-http-응답-코드)  
[5. Express와 MySQL로 REST API 테스트 서버 구축하기](#5-express와-mysql로-rest-api-테스트-서버-구축하기)  
&nbsp;  [5-1 Node.js 설치 및 설치 확인](#5-1-nodejs-설치-및-설치-확인)  
&nbsp;  [5-2 Express/MySQL 모듈 설치](#5-2-expressmysql-모듈-설치)  
&nbsp;  [5-3 데이터베이스 설계](#5-3-데이터베이스-설계)  
&nbsp;  [5-4 모듈 Import 및 Connection pool 설정](#5-4-모듈-import-및-connection-pool-설정)  
&nbsp;  [5-5 저장된 User 정보 가져오기](#5-5-저장된-user-정보-가져오기)  
[6. 클라이언트(안드로이드) 설정](#6-클라이언트안드로이드-설정)  
&nbsp;  [6-1 Retrofit](#6-1-retrofit)  
&nbsp;  [6-2 설계과정](#6-2-설계과정)  
&nbsp;  [6-3 Retrofit Build](#6-3-retrofit-build)  
&nbsp;  [6-4 데이터 클래스 작성](#6-4-데이터-클래스-작성)  
&nbsp;  [6-5 인터페이스 작성](#6-5-인터페이스-작성)  
&nbsp;&nbsp;    [6-5-1 Retrofit Annotation](#6-5-1-retrofit-annotation)  
&nbsp;  [6-6 서버와 통신하기](#6-6-서버와-통신하기)  
&nbsp;&nbsp;    [6-6-1 Call 객체 생성하기](#6-6-1-call-객체-생성하기)  
&nbsp;&nbsp;    [6-6-2 enqueue()와 execute()](#6-6-2-enqueue와-execute)  
&nbsp;&nbsp;    [6-6-3 RetrofitManager 생성](#6-6-3-retrofitmanager-생성)  
&nbsp;&nbsp;    [6-6-4 테스트 API 서버에서 GET Method로 데이터 요청하기](#6-6-4-테스트-api-서버에서-get-method로-데이터-요청하기)  
[7. Logging-Interceptor로 통신 과정 확인하기](#7-logging-interceptor로-통신-과정-확인하기)  
&nbsp;  [7-1 build.gradle에 logging-interceptor 모듈 추가](#7-1-buildgradle에-logging-interceptor-모듈-추가)  
&nbsp;  [7-2 OkHttpClient 인스턴스 생성](#7-2-okhttpclient-인스턴스-생성)  
&nbsp;  [7-3 loggingInterceptor 생성](#7-3-logginginterceptor-생성)  
&nbsp;  [7-4 loggingInterceptor 레벨 설정](#7-4-logginginterceptor-레벨-설정)  
&nbsp;  [7-5 설정된 OkHttpClient를 Retrofit 빌더에 추가](#7-5-설정된-okhttpclient를-retrofit-빌더에-추가)

---

## 개요
이 문서는 [REST API 제대로 알고 사용하기](https://meetup.toast.com/posts/92), [위키백과](https://ko.wikipedia.org/wiki/REST)를 참조하여 만들었습니다. REST API의 개념과 Retrofit의 사용 방법을 전반적으로 알아보고 Node.js를 통해 GET 요청을 받을 수 있는 간단한 API 서버를 만들어 실습하는 과정을 담았습니다.

### 진행순서
1. REST API의 기초와 설계 가이드
2. Node.js, Express, MySQL로 REST API GET 테스트 서버 구축하기
3. Retrofit 기초와 안드로이드에서 Retrofit으로 테스트 API 서버 호출해보기
4. OkHttp의 Logging-Interceptor를 통해 통신 과정 중 일어나는 과정을 로그로 확인해보기

### 개발환경
* Android, Node.js
* Express, MySQL, Retrofit2(+Gson, OkHttp3-Interceptor)

---

## 1. REST API란?
REST(Representational State Transfer)는 분산 하이퍼미디어(WWW) 시스템을 위한 소프트웨어 아키텍처의 한 형식으로, 자원을 정의하고 자원에 대한 주소를 지정하는 방법을 다룹니다. HTTP의 주요 저자 중 한 사람인 로이 필딩(Roy Fielding)이 처음 소개하였으며 HTTP*의 장점을 최대한 활용할 수 있는 아키텍쳐입니다.

<details>
<summary>HTTP*</summary>
<div markdown="1">
WWW상에서 텍스트 기반의 데이터를 주고 받을 수 있는 프로토콜입니다. 클라이언트가 HTML문서(또는 JSON 데이터 및 XML)를 요청하고, 서버는 이에 대한 응답을 하는 방식으로 이루어집니다.
</div>
</details>
 

### 1-1 REST API의 구성
* 자원(Resource)에 대한 표현(Representation)

  자원은 URI에 해당하며 자원을 표현하기 위한 이름이 정의되어야 합니다.

  예) http://example.com/korea/busan - 한국, 부산에 대한 자원을 korea, busan이라고 표현했습니다.

&nbsp;
* 행위

  자원에 대한 표현을 정의했다면 자원에 대한 행위를 정해야 합니다. 행위는 Http Method(GET, POST, PUT, DELETE 등)로 표현합니다.

  예) GET /example.com/korea/busan – ‘busan’에 대한 자료를 가져오는 행위

&nbsp;

---
## 2. REST API에 적용되어야 하는 조건
* <b>인터페이스 일관성</b>: 일관적인 인터페이스로 분리되어야 합니다. 이것은 URI에 지정한 자원에 대해 통일된 인터페이스를 제공해야 한다는 의미입니다.
* <b>무상태(Stateless)</b>: 요청된 작업에 대한 상태정보를 서버에 저장하지 않습니다. 따라서 Rest API 서버는 단순히 들어오는 요청만을 처리하면 됩니다.
* <b>캐시 처리 가능(Cacheable)</b>: HTTP에서 사용할 수 있는 캐싱기능을 그대로 사용할 수 있으며 클라이언트는 서버의 응답을 캐싱할 수 있어야 합니다.
* <b>계층형 시스템(Layered System)</b>: Rest 서버에 로드 밸런싱이나 공유 캐시 기능을 지원하는 중간 서버를 두어 시스템의 확장성과 유연성을 향상시킬 수 있습니다.
* <b>클라이언트/서버 구조</b>: 서버는 리소스를 요청할 수 있는 API를 제공하고 클라이언트는 사용자 인증, 컨텍스트(세션, 로그인 정보)등을 관리하는 역할로 분리하여 서로 간의 의존성을 줄이고 각 파트를 독립적으로 개선할 수 있어야 합니다.

&nbsp;

---
## 3. REST API 설계 가이드
### 3-1 자원에 대한 행위 표현 - HTTP Method
|Method|설명|
|---|---|
|GET|서버로부터 리소스를 요청합니다.|
|POST|서버에 리소스 생성을 요청합니다.|
|PUT|해당 리소스를 수정합니다.|
|DELETE|해당 리소스를 삭제합니다.|

&nbsp;
### 3-2 URI 명명 규칙
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

5. URI는 스키마와 호스트를 제외하고는 대소문자를 구분합니다. 대소문자에 따라 서로 다른 리소스로 인식될 수 있습니다. 따라서 자원에 대문자를 사용하는 것을 피해야합니다.

6. 파일 확장자를 URI에 포함시키지 않습니다. 파일 확장자를 표현해야 할 경우 응답으로 받고 싶은 타입을 명시할 수 있는 Accept header를 사용합니다.

&nbsp;
### 3-3 URI에서 리소스간의 관계를 표현하기
소유(has)관계를 표현하고 싶은 경우는 아래의 예시처럼 사용할 수 있습니다.
* /리소스명/{리소스ID}/리소스와 관계있는 다른 리소스명  
  예) <b>/users/1/devices</b> – ‘1번’ 유저가 가진 devices를 표현할 수 있습니다.

&nbsp;

---
## 4. HTTP 응답 코드
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
## 5. Express와 MySQL로 REST API 테스트 서버 구축하기
간단하게 GET Method를 테스트해볼 수 있는 API 서버를 구축해보도록 하겠습니다.
* 준비물: vscode, Node.js, Express, MySQL

### 5-1 Node.js 설치 및 설치 확인
vscode 터미널에 아래와 같이 입력하여 Node.js가 제대로 설치 되었는지 확인 할 수 있습니다.
```
> node -v && npm -v
```

&nbsp;

### 5-2 Express/MySQL 모듈 설치
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

### 5-3 데이터베이스 설계
REST API 서버에서 사용할 데이터베이스를 아래와 같이 설계했습니다.  

<b>Users</b>
|Field|Type|Null|Key|Default|
|---|---|---|---|---|
|id|varchar(20)|NO|PRI|NULL|
|password|varchar(20)|NO||NULL|
|name|varchar(20)|NO||NULL|
|age|int|NO||NULL|

&nbsp;

### 5-4 모듈 Import 및 Connection pool 설정
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

### 5-5 저장된 User 정보 가져오기
express의 get 메소드를 통해 자원을 설정하고 함수의 두번째 인자(res)로 쿼리문의 결과를 전송할 수 있습니다.
```javascript
// 1. 데이터베이스에 저장된 모든 User 정보 가져오기
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

// 2. 모든 유저의 나이 정보 가져오기
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
// 3. 연관 관계(id 가 ~~인 users 리소스 중 name 리소스 가져오기)
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

---

## 6. 클라이언트(안드로이드) 설정
### 6-1 Retrofit
[Retrofit 공식문서](https://square.github.io/retrofit/)  
* REST API를 위한 HTTP 통신 라이브러리
* 개발자가 네트워킹을 위해 스레드를 따로 만들 필요가 없으며 Callback을 통해 UI스레드를 업데이트 할 수 있습니다.
* 네트워크로 받은 데이터를 객체로 변환하여 사용할 수 있습니다.(Gson Converter 사용)
* OkHttp 라이브러리를 베이스로 만들어졌습니다.
* Retrofit은 자체적인 비동기 처리로 AsyncTask를 사용하는 OkHttp보다 실행속도가 빠릅니다.

&nbsp;

### 6-2 설계과정
1. Retrofit Build
2. 데이터 클래스 작성(JSON Data <-> Object)
3. HTTP Method를 사용할 인터페이스 작성(GET, POST, PUT, DELETE)

&nbsp;

### 6-3 Retrofit Build
#### 1.build.gradle에 Retrofit 및 gson 모듈 추가
```Gradle
// Retrofit
implementation 'com.squareup.retrofit2:retrofit:2.8.1'
implementation 'com.squareup.retrofit2:converter-gson:2.8.1'
```


#### 2. Retrofit 객체를 여러 곳에서 사용할 수 있도록 싱글톤 클래스 'RetrofitService'를 생성합니다.

* baseUrl: ex) http://www.example.com
* addConverterFactory: 사용할 Converter를 입력합니다.(Gson, Moshi 등)

```kotlin
object RetrofitService {
    private var retrofit: Retrofit? = null

    fun getRetrofit(baseUrl: String): Retrofit? {
        if(retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}
```

&nbsp;

### 6-4 데이터 클래스 작성
HTTP통신의 응답 결과를 Object 형태로 저장하려는 경우 데이터 클래스를 생성해야 합니다. 이 예제에서는 <b>Users</b> 데이터를 받을 수 있도록 UserDTO 클래스를 작성했습니다.

```kotlin
data class UserDTO(
    val id: String?,
    val password: String?,
    val name: String?,
    val age: Int?
)
```

<b>!</b> 데이터베이스 테이블의 필드명이 'id'인 경우 애플리케이션의 데이터 클래스 변수명도 동일하게 'id'로 설정해야합니다. 필드명이 'id'이고 이에 매칭되는 데이터 클래스의 변수명이 'userId'인 경우 응답결과로 'userid=null' 데이터가 반환되는 것을 확인했습니다. 이것은 Gson Converter가 JSON 데이터를 객체로 변환하는 과정에서 변수명이 매칭되지 않아 발생되는 문제점이라고 생각됩니다.

* 데이터 클래스가 잘못 설정된 경우 실행결과
  ```kotlin
  data class UserDTO(
    val userId: String?,
    val password: String?,
    val name: String?,
    val age: Int?
  )
  // 테이블의 필드명은 id임.

  // 실행 결과
  D/response: userId: null, password: 1234, name: kim, age: 20
  D/response: userId: null, password: qq!9435221, name: choi, age: 25
  D/response: userId: null, password: 9430, name: lee, age: 21
  D/response: userId: null, password: a0123, name: park, age: 23
  ```

* 올바른 실행결과  
  ```kotlin
  data class UserDTO(
    val id: String?,
    val password: String?,
    val name: String?,
    val age: Int?
  )
  // 테이블의 필드명은 id임.

  // 실행 결과
  D/response: id: abc123, password: 1234, name: kim, age: 20
  D/response: id: bgood, password: qq!9435221, name: choi, age: 25
  D/response: id: qwer0000, password: 9430, name: lee, age: 21
  D/response: id: zz8888, password: a0123, name: park, age: 23
  ```

&nbsp;

### 6-5 인터페이스 작성
HTTP Method(GET, POST, PUT, DELETE)를 사용하여 서버에 요청할 API 인터페이스를 작성합니다.

### 6-5-1 Retrofit Annotation
* @GET: 지정된 경로에 있는 데이터를 요청합니다.  
  ```kotlin
  // http://xxx.xxx.xxx.xxx:3000/users
  @GET("/users")
  fun searchAllUsers(): Call<List<UserDTO>>
  ```

* @POST: @Field 또는 @Body Annotation과 함께 사용하여 생성을 요청할 수 있습니다.

* @PUT: @Field 또는 @Body Annotation과 함께 사용하여 데이터 업데이트를 요청할 수 있습니다.

* @DELETE: 데이터 삭제를 요청할 수 있습니다.

* @Path: 경로에 매개변수가 포함되는 경우 Path Annotation과 함께 값을 담을 매개변수를 포함합니다.  
  ```kotlin
  // http://xxx.xxx.xxx.xxx:3000/users/{userId}/name
  @GET("/users/{id}/name") 
  fun searchUserName(@Path("id") userId: String): Call<List<UserDTO>>
  ```
  
* @Query: 경로에 Query 파라미터가 포함되는 경우 Query Annotation과 함께 쿼리명 및 쿼리에 들어갈 값을 담을 매개변수를 포함합니다.  
  ```kotlin
  // http://xxx.xxx.xxx.xxx:3000/users?userid={userId}
  @GET("/users") 
  fun searchUserName(@Query("userid") userId: String): Call<List<UserDTO>>
  ```

* @Field: 주로 POST Annotation과 함께 사용됩니다. Request 데이터를 보낼 때 Url에 key-value 형식으로 값을 넣어 요청할 수 있습니다.(@FormUrlEncoded와 함께 사용)  
  ```kotlin
  // http://xxx.xxx.xxx.xxx:300/user/userid={userId}&password={passoword}
  @FormUrlEncoded
  @POST("/user")
  fun insertUser(@Field("userid") userId: String, @Field("password") password: String): Call<UserDTO>
  ```

* @FieldMap: Map 자료구조 형태를 통해서 데이터를 한꺼번에 보낼 수 있습니다.(@FormUrlEncoded와 함께 사용)  
  ```kotlin
  @FormUrlEncoded
  @POST("/user")
  fun insertUser(@FieldMap HashMap<String, String> paramMap): Call<UserDTO>

  // HashMap.put("userid", "abc123")
  // HashMap.put("password", "1234")
  // -> http://xxx.xxx.xxx.xxx:300/user/userid=abc123&password=1234
  ```

* @Body: 요청할 때 Object 자체를 JSON 데이터로 직렬화하여 서버에 전송하고자하는 경우 사용합니다. 따라서 @Field Annotation과는 다르게 Object 타입을 매개변수로 받습니다(Single request body). Object가 JSON 데이터로 직렬화 되는것은 Retrofit이 Gson Converter를 사용하기 때문에 가능합니다.  
  ```kotlin
  @POST("/user")
  fun insertUser(@Body user: User): Call<UserDTO>
  ```

### 6-6 서버와 통신하기
### 6-6-1 Call 객체 생성하기
위에서 만든 API 인터페이스를 호출하여 Call 객체를 만들 수 있습니다. Call 객체는 이름 그대로 '명시한 자원과 행위를 가지고 서버에 호출한다'는 의미입니다. 따라서 호출에 대한 응답을 받기 위해서는 enqueue() 또는 execute()를 사용해야 합니다.
```kotlin
// Get UserAPI  
private val userApi: UsersAPI? = RetrofitService.getRetrofit("http://xxx.xx.x.x:3000")?.create(UsersAPI::class.java)

// 유저 데이터 요청(GET)
fun searchAllUsers(result: (List<UserDTO>?) -> Unit) {
    // API 인터페이스를 통해 Call 객체 생성
    val call: Call<List<UserDTO>> = userApi?.searchAllUsers() ?: return

    // ... //
}
```

이외에도 요청을 취소하는 메소드인 cancel()과 상황을 알 수 있는 isCanceled, isExecuted 도 있습니다.

&nbsp;

### 6-6-2 enqueue()와 execute()
* Call.enqueue(): 호출과 응답까지에 대한 과정을 <b>비동기적</b>으로 처리하고 싶다면 enqueue() 메소드를 사용해야합니다. 결과는 Callback으로 받을 수 있으며 Callback 내부에는 호출에 성공하여 응답 받은 경우인 onResponse와 실패한 경우인 onFailure를 정의할 수 있습니다.  
  ```kotlin
  // Async
  call.enqueue(object : Callback<List<UserDTO>> {
    // 응답에 성공한 경우
    override fun onResponse(call: Call<List<UserDTO>>, response: Response<List<UserDTO>>) {
        result(response.body()) // 결과 반환
    }
    // 응답에 실패한 경우
    override fun onFailure(call: Call<List<UserDTO>>, t: Throwable) {
        Log.d("RetrofitManager", "searchAllUsers onFailure")
        t.printStackTrace()
    }
  })
  ```
* Call.execute(): 호출과 응답까지에 대한 과정을 <b>동기적</b>으로 처리할 경우 사용합니다. 반환 객체는 enqueue()와 동일하게 Response이며 execute() 메소드는 하나의 Call 객체에 대해서 단 한번만 호출해야 하고 이미 비동기 호출인 enqueue()를 사용했다면 역시 에러가 발생합니다.
  ```kotlin
  // Sync
  val response: Response<List<UserDTO>> = call.execute()
  ```

### 6-6-3 RetrofitManager 생성
위의 코드를 종합하여 액티비티에서 간단하게 호출 메소드만 사용할 수 있도록 API 인터페이스의 생성과 요청을 담당하는 클래스를 생성했습니다.

```kotlin
class RetrofitManager {
    // Get retrofit interface
    // Retrofit.create(interface::class.java)
    private val userApi: UsersAPI? =
        RetrofitService.getRetrofit("http://xxx.xx.x.x:3000")?.create(UsersAPI::class.java)

    // 모든 유저 데이터
    fun searchAllUsers(result: (List<UserDTO>?) -> Unit) {
        val call: Call<List<UserDTO>> = userApi?.searchAllUsers() ?: return

        // Async
        call.enqueue(object : Callback<List<UserDTO>> {
            // 응답에 성공한 경우
            override fun onResponse(call: Call<List<UserDTO>>, response: Response<List<UserDTO>>) {
                Log.d("response", response.raw().toString())
                result(response.body()) // 결과 반환
            }
            // 응답에 실패한 경우
            override fun onFailure(call: Call<List<UserDTO>>, t: Throwable) {
                Log.d("RetrofitManager", "searchAllUsers onFailure")
                t.printStackTrace()
            }
        })

        // Sync
        // val response: Response<List<UserDTO>> = call.execute()

    }

    // id가 {}인 유저 데이터
    fun searchUserName(userId: String, result: (List<UserDTO>?) -> Unit) {
        val call: Call<List<UserDTO>> = userApi?.searchUserName(userId) ?: return

        call.enqueue(object : Callback<List<UserDTO>> {
            override fun onResponse(call: Call<List<UserDTO>>, response: Response<List<UserDTO>>) {
                result(response.body())
            }

            override fun onFailure(call: Call<List<UserDTO>>, t: Throwable) {
                Log.d("RetrofitManager", "searchUser onFailure")
                t.printStackTrace()
            }
        })
    }
}
```

&nbsp;

### 6-6-4 테스트 API 서버에서 GET Method로 데이터 요청하기
메인 액티비티에서 GET 메소드를 호출하는 예시입니다.
```kotlin
private val retrofitManager by lazy { RetrofitManager() }
private lateinit var binding: ActivityMainBinding

override fun onCreate(savedInstanceState: Bundle?) {
  // ... //

  // 유저 데이터 요청/실행 결과 출력
  binding.btnGet.setOnClickListener {
    binding.resultFrame.text = ""

    // editText에 내용이 없다면 모든 유저 데이터 요청
    if(binding.searchEditText.text.isEmpty()) {
      retrofitManager.searchAllUsers { users ->
        if(users != null) {
          for(user in users) {
            val str = String.format("userId: %s, password: %s, name: %s, age: %d", user.id, user.password, user.name, user.age)
            binding.resultFrame.append(str)
            binding.resultFrame.append("\n")
          }
        }
      }
    } else { // editText에 내용이 있다면 해당 ID를 가진 유저 데이터 요청
      val userId = binding.searchEditText.text.toString()
      retrofitManager.searchUserName(userId) { users ->
        if(users != null) {
          for(user in users) {
            val str = String.format("name: %s", user.name)
            binding.resultFrame.append(str)
            binding.resultFrame.append("\n")
          }
        }
      }
      binding.searchEditText.setText("")
    }
  }
  // ... //
}
```

<i><b>실행결과</b></i>  
1. 모든 User 데이터 요청  
```kotlin
D/response: id: abc123, password: 1234, name: kim, age: 20
D/response: id: bgood, password: qq!9435221, name: choi, age: 25
D/response: id: qwer0000, password: 9430, name: lee, age: 21
D/response: id: zz8888, password: a0123, name: park, age: 23
```

2. User ID 값을 받아 해당하는 유저의 이름 요청
```kotlin
D/User name 호출: (userId: abc123)
D/response: name: kim
```

&nbsp;

---

## 7. Logging-Interceptor로 통신 과정 확인하기
OkHttp의 Logging-Interceptor를 사용함으로써 HTTP의 요청 <-> 응답 과정에서 일어나는 일을 로그로 확인할 수 있습니다.

### 7-1 build.gradle에 logging-interceptor 모듈 추가
```Gradle
// logging-interceptor
implementation 'com.squareup.okhttp3:logging-interceptor:4.8.1'
```

### 7-2 OkHttpClient 인스턴스 생성
OkHttpClient 인스턴스를 생성하고 logging-interceptor를 적용하는 과정입니다. 생성된 OkHttpClient 인스턴스는 Retrofit이 Build되는 과정에서 client() 메소드를 사용해 적용합니다.
```kotlin
// 1. OkHttp 인스턴스 생성
val client = OkHttpClient.Builder()
```

### 7-3 loggingInterceptor 생성
```kotlin
// 2. logging-interceptor 생성
val loggingInterceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger {
  override fun log(message: String) {
    Log.d("Logging-Interceptor", message)
  }
})
```
HttpLoggingInterceptor 인터페이스를 통해 로그에 담긴 메시지를 확인할 수 있습니다.

### 7-4 loggingInterceptor 레벨 설정
생성된 loggingInterceptor에 레벨을 설정합니다. 레벨에 따라 출력되는 로그는 다음과 같습니다.
|Level|설명|
|---|---|
|BASIC|요청과 응답에 대한 Start Line을 출력합니다.|
|HEADERS|요청과 응답에 대한 Start Line과 함께 Header 부분을 포함하여 출력합니다.|
|BODY|요청과 응답에 대한 Start Line, Header를 출력하고 Body의 내용이 존재한다면 Body 부분까지 포함하여 출력합니다.|

설정된 레벨에 따른 출력 결과는 다음과 같습니다.  
```kotlin
// BASIC
loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
client.addInterceptor(loggingInterceptor)

// 출력 결과
D/Logging-Interceptor: --> GET http://xxx.xx.x.x:3000/users
D/Logging-Interceptor: <-- 200 OK http://xxx.xx.x.x:3000/users (66ms, 235-byte body)
```
```kotlin
// HEADERS
loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
client.addInterceptor(loggingInterceptor)

// 출력 결과
D/Logging-Interceptor: --> GET http://xxx.xx.x.x:3000/users
D/Logging-Interceptor: --> END GET
D/Logging-Interceptor: <-- 200 OK http://xxx.xx.x.x:3000/users (67ms)
D/Logging-Interceptor: X-Powered-By: Express
D/Logging-Interceptor: Content-Type: application/json; charset=utf-8
D/Logging-Interceptor: Content-Length: 235
D/Logging-Interceptor: ETag: W/"eb-rppBjgiN/fajCkTXGmk1nJPLgHY"
D/Logging-Interceptor: Date: Thu, 14 Apr 2022 12:49:21 GMT
D/Logging-Interceptor: Connection: keep-alive
D/Logging-Interceptor: Keep-Alive: timeout=5
D/Logging-Interceptor: <-- END HTTP
```
```kotlin
// BODY
loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
client.addInterceptor(loggingInterceptor)

// 출력 결과
D/Logging-Interceptor: --> GET http://xxx.xx.x.x:3000/users/abc123/name
D/Logging-Interceptor: --> END GET
D/Logging-Interceptor: <-- 200 OK http://xxx.xx.x.x:3000/users/abc123/name (8ms)
D/Logging-Interceptor: X-Powered-By: Express
D/Logging-Interceptor: Content-Type: application/json; D/Logging-Interceptor: Content-Length: 16
D/Logging-Interceptor: ETag: W/"10-i3I4aFUar27gWwXYiF8WSlGQ8+Q"
D/Logging-Interceptor: Date: Thu, 14 Apr 2022 12:50:09 GMT
D/Logging-Interceptor: Connection: keep-alive
D/Logging-Interceptor: Keep-Alive: timeout=5
D/Logging-Interceptor: [{"name":"kim"}]
D/Logging-Interceptor: <-- END HTTP (16-byte body)
```

### 7-5 설정된 OkHttpClient를 Retrofit 빌더에 추가
```kotlin
if(retrofit == null) {
  retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .addConverterFactory(GsonConverterFactory.create())
    .client(client.build()) // OkHttpClient
    .build()
}
```

이제 HttpLoggingInterceptor.Logger에 의해 로그를 콜백 받을 수 있습니다.

---
