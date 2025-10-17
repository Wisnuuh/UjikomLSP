package com.dicoding.ujikomlsp.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.ujikomlsp.data.remote.response.UpdateAbsensiResponse
import com.dicoding.ujikomlsp.data.remote.retrofit.ApiConfig
import com.example.mobile.data.remote.model.UpdateAbsen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAbsensiViewModel : ViewModel() {

    private val _messageUpdate = MutableLiveData<UpdateAbsensiResponse>()
    val messageUpdate: LiveData<UpdateAbsensiResponse> = _messageUpdate

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun updateAbsen(id: Int, token: String, keterangan: String) {
        _isLoading.value = true
        val updateAbsen = UpdateAbsen(keterangan)
        val client = ApiConfig.getApiService().updateAbsen(id, token, updateAbsen)

        client.enqueue(object : Callback<UpdateAbsensiResponse> {
            override fun onResponse(
                call: Call<UpdateAbsensiResponse>,
                response: Response<UpdateAbsensiResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                _messageUpdate.value = responseBody
            }

            override fun onFailure(call: Call<UpdateAbsensiResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}", )
            }
        })
    }

    companion object {
        const val TAG = "DetailAbsensiViewModel"
    }
}