package com.example.restclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.restclient.databinding.ActivityMainBinding
import com.example.restclient.retrofit.RetrofitManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val retrofitManager by lazy { RetrofitManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 직접 만든 REST API
        binding.btnGet.setOnClickListener {
            binding.resultFrame.text = ""

            // searchAllUsers 호출
            if(binding.searchEditText.text.isEmpty()) {
                retrofitManager.searchAllUsers { users ->
                    if(users != null) {
                        for(user in users) {
                            val str = String.format("userId: %s, password: %s, name: %s, age: %d",
                                user.id, user.password, user.name, user.age)
                            binding.resultFrame.append(str)
                            binding.resultFrame.append("\n")
                        }
                    }
                }

            } else { // searchUser(Param) 호출
                val userId = binding.searchEditText.text.toString()
                retrofitManager.searchUserName(userId) { users ->
                    Log.d("User name 호출", "(userId: $userId)")
                    if(users != null) {
                        for(user in users) {
                            val str = String.format("name: %s", user.name)
                            Log.d("response", str)
                            binding.resultFrame.append(str)
                            binding.resultFrame.append("\n")
                        }
                    }
                }
                binding.searchEditText.setText("")
            }
        }
    }
}