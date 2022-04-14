package com.example.restclient.retrofit

import android.util.Log
import retrofit2.*

class RetrofitManager {
    // Get UsersAPI
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
                result(response.body()) // 결과 반환
            }
            // 응답에 실패한 경우
            override fun onFailure(call: Call<List<UserDTO>>, t: Throwable) {
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