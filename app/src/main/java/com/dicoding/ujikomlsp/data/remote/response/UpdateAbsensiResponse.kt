package com.dicoding.ujikomlsp.data.remote.response

import com.google.gson.annotations.SerializedName

data class UpdateAbsensiResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String
)

data class Data(

	@field:SerializedName("bataswaktu_absensi")
	val bataswaktuAbsensi: String? = null,

	@field:SerializedName("id_absensi")
	val idAbsensi: Int? = null,

	@field:SerializedName("keterangan_absen")
	val keteranganAbsen: String? = null,

	@field:SerializedName("id_guru")
	val idGuru: Int? = null,

	@field:SerializedName("waktu_absen")
	val waktuAbsen: String? = null,

	@field:SerializedName("id_siswa")
	val idSiswa: Int? = null
)
