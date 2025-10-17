package com.dicoding.ujikomlsp.ui.riwayat

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.ujikomlsp.data.remote.response.DataItem
import com.dicoding.ujikomlsp.databinding.ItemRiwayatAbsensiBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RiwayatAbsensiAdapter : ListAdapter<DataItem, RiwayatAbsensiAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(val binding: ItemRiwayatAbsensiBinding): RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(absen: DataItem) {
            binding.labelJudulMapel.text = absen.guru?.pelajaran
            binding.labelKeteranganAbsen.text = "Keterangan absen: ${absen.keteranganAbsen}"
            val instant = Instant.parse(absen.waktuAbsen)
            val zonedDateTime = instant.atZone(ZoneId.of("Asia/Jakarta"))
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val jamMenit = zonedDateTime.format(timeFormatter)
            binding.labelTanggal.text = "Waktu absen: $jamMenit"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRiwayatAbsensiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val absen = getItem(position)
        holder.bind(absen)
    }

    companion object {
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}