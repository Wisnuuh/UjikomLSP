package com.dicoding.ujikomlsp.data.remote.response

import com.google.gson.annotations.SerializedName

data class AbsensiResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("message")
	val message: String
)

data class Guru(

	@field:SerializedName("nip")
	val nip: Int? = null,

	@field:SerializedName("id_guru")
	val idGuru: Int? = null,

	@field:SerializedName("nama_guru")
	val namaGuru: String? = null,

	@field:SerializedName("pelajaran")
	val pelajaran: String? = null
)

data class Kelas(

	@field:SerializedName("nama_kelas")
	val namaKelas: String? = null,

	@field:SerializedName("id_kelas")
	val idKelas: Int? = null,

	@field:SerializedName("jenjang")
	val jenjang: String? = null
)

data class DataItem(

	@field:SerializedName("siswa")
	val siswa: Siswa? = null,

	@field:SerializedName("guru")
	val guru: Guru? = null,

	@field:SerializedName("bataswaktu_absensi")
	val bataswaktuAbsensi: String? = null,

	@field:SerializedName("id_absensi")
	val idAbsensi: Int,

	@field:SerializedName("keterangan_absen")
	val keteranganAbsen: String,

	@field:SerializedName("id_guru")
	val idGuru: Int? = null,

	@field:SerializedName("waktu_absen")
	val waktuAbsen: String? = null,

	@field:SerializedName("id_siswa")
	val idSiswa: Int? = null
)

data class Siswa(

	@field:SerializedName("nama_siswa")
	val namaSiswa: String? = null,

	@field:SerializedName("nisn")
	val nisn: Int? = null,

	@field:SerializedName("kelas")
	val kelas: Kelas? = null,

	@field:SerializedName("id_kelas")
	val idKelas: Int? = null,

	@field:SerializedName("id_siswa")
	val idSiswa: Int? = null
)
