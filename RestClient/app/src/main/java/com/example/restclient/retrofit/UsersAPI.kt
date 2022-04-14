package com.example.restclient.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UsersAPI {
    @GET("/users") // http://xxx.xxx.xxx.xxx:3000/users
    fun searchAllUsers(): Call<List<UserDTO>>

    @GET("/users/{id}/name") // http://xxx.xxx.xxx.xxx:3000/users/{userId}/name
    fun searchUserName(@Path("id") userId: String): Call<List<UserDTO>>
}