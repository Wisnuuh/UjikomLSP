package com.dicoding.ujikomlsp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.ujikomlsp.data.remote.response.AbsensiResponse
import com.dicoding.ujikomlsp.data.remote.response.DataItem
import com.dicoding.ujikomlsp.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _absensi = MutableLiveData<List<DataItem>>()
    val absensi: LiveData<List<DataItem>> = _absensi

    fun getAbsensi(token: String) {
        val client = ApiConfig.getApiService().getAbsensi(token)

        client.enqueue(object : Callback<AbsensiResponse> {
            override fun onResponse(
                call: Call<AbsensiResponse>,
                response: Response<AbsensiResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _absensi.value = responseBody?.data
                }
            }

            override fun onFailure(call: Call<AbsensiResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}