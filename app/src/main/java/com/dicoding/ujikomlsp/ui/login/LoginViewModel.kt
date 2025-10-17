package com.dicoding.ujikomlsp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.ujikomlsp.data.remote.response.TokenLoginResponse
import com.dicoding.ujikomlsp.data.remote.retrofit.ApiConfig
import com.example.mobile.data.remote.model.LoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {
    private val _tokenLogin = MutableLiveData<String>()
    val tokenLogin: LiveData<String> = _tokenLogin

    private val _messageResponse = MutableLiveData<String>()
    val messageResponse: LiveData<String> = _messageResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getTokenLogin(username: String, password: String) {
        val loginRequest = LoginRequest(username, password)
        _isLoading.value = true
        val client = ApiConfig.getApiService().getLoginToken(loginRequest)

        client.enqueue(object : Callback<TokenLoginResponse> {
            override fun onResponse(
                call: Call<TokenLoginResponse>,
                response: Response<TokenLoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _tokenLogin.value = responseBody.token
                        _messageResponse.value = responseBody.message
                    }
                } else {
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TokenLoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onResponse: ${t.message.toString()}")
            }
        })
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}