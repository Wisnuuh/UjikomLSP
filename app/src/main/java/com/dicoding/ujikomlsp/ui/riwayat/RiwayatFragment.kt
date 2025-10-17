package com.dicoding.ujikomlsp.ui.riwayat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.ujikomlsp.data.remote.response.DataItem
import com.dicoding.ujikomlsp.databinding.FragmentDashboardBinding
import com.dicoding.ujikomlsp.preferences.UserPreferences
import com.dicoding.ujikomlsp.preferences.dataStore
import com.dicoding.ujikomlsp.ui.detail.DetailAbsensiActivity
import com.dicoding.ujikomlsp.ui.home.AbsensiAdapter
import com.dicoding.ujikomlsp.ui.home.HomeViewModel
import kotlinx.coroutines.launch

class RiwayatFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val viewModel by viewModels<RiwayatViewModel>()
    private lateinit var userPreference: UserPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreferences.getInstance(requireContext().dataStore)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayatAbsensi.layoutManager = layoutManager

        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                viewModel.getRiwayatAbsensi(user.token)
            }
        }

        viewModel.riwayatAbsensi.observe(viewLifecycleOwner) { absen ->
            setAbsensiData(absen)
        }
    }

    private fun setAbsensiData(absensi: List<DataItem>) {
        val adapter = RiwayatAbsensiAdapter()
        adapter.submitList(absensi)
        binding.rvRiwayatAbsensi.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}