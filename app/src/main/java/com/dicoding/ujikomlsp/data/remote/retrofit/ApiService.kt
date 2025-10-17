package com.dicoding.ujikomlsp.data.remote.retrofit

import com.dicoding.ujikomlsp.data.remote.response.AbsensiResponse
import com.dicoding.ujikomlsp.data.remote.response.ProfileResponse
import com.dicoding.ujikomlsp.data.remote.response.TokenLoginResponse
import com.dicoding.ujikomlsp.data.remote.response.UpdateAbsensiResponse
import com.example.mobile.data.remote.model.LoginRequest
import com.example.mobile.data.remote.model.UpdateAbsen
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    fun getLoginToken(
        @Body loginRequest: LoginRequest
    ): Call<TokenLoginResponse>

    @GET("absensi")
    fun getAbsensi(
        @Header("Authorization") token: String
    ): Call<AbsensiResponse>

    @GET("riwayatabsensi")
    fun getRiwayatAbsensi(
        @Header("Authorization") token: String
    ): Call<AbsensiResponse>

    @PUT("{id}")
    @Headers("Content-Type: application/json")
    fun updateAbsen(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body updateAbsen: UpdateAbsen
    ): Call<UpdateAbsensiResponse>

    @GET("profile")
    fun getProfile(
        @Header("Authorization") token: String
    ): Call<ProfileResponse>
}