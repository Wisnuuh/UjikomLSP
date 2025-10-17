package com.dicoding.ujikomlsp.ui.riwayat

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

class RiwayatViewModel : ViewModel() {

    private val _riwayatAbsensi = MutableLiveData<List<DataItem>>()
    val riwayatAbsensi: LiveData<List<DataItem>> = _riwayatAbsensi

    fun getRiwayatAbsensi(token: String) {
        val client = ApiConfig.getApiService().getRiwayatAbsensi(token)

        client.enqueue(object : Callback<AbsensiResponse> {
            override fun onResponse(
                call: Call<AbsensiResponse>,
                response: Response<AbsensiResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _riwayatAbsensi.value = responseBody?.data
                }
            }

            override fun onFailure(call: Call<AbsensiResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        const val TAG = "RiwayatViewModel"
    }
}