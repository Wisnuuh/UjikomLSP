package com.dicoding.ujikomlsp.ui.home

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.ujikomlsp.data.remote.response.DataItem
import com.dicoding.ujikomlsp.databinding.ItemAbsensiBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class AbsensiAdapter(private val onClick: (DataItem) -> Unit): ListAdapter<DataItem, AbsensiAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(val binding: ItemAbsensiBinding): RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(absen: DataItem) {
            binding.labelJudulMapel.text = absen.guru?.pelajaran
            val instant = Instant.parse(absen.bataswaktuAbsensi)
            val zonedDateTime = instant.atZone(ZoneId.of("Asia/Jakarta"))
            val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm", Locale("id", "ID"))
            val hasil = zonedDateTime.format(formatter)
            binding.labelTanggal.text = "Tenggat: $hasil"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemAbsensiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val absen = getItem(position)
        holder.bind(absen)

        holder.itemView.setOnClickListener {
            onClick(absen)
        }
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