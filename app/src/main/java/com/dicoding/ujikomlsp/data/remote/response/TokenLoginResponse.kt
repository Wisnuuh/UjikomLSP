package com.dicoding.ujikomlsp.data.remote.response

import com.google.gson.annotations.SerializedName

data class TokenLoginResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("token")
	val token: String
)
