package com.dicoding.ujikomlsp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("nama_siswa")
	val namaSiswa: String,

	@field:SerializedName("nisn")
	val nisn: Int,

	@field:SerializedName("id_kelas")
	val idKelas: Int,

	@field:SerializedName("kela")
	val kela: Kela? = null,

	@field:SerializedName("id_siswa")
	val idSiswa: Int
)

data class Kela(

	@field:SerializedName("nama_kelas")
	val namaKelas: String,

	@field:SerializedName("id_kelas")
	val idKelas: Int,

	@field:SerializedName("jenjang")
	val jenjang: String
)
