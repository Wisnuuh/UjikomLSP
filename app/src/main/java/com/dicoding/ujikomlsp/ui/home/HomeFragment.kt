package com.dicoding.ujikomlsp.ui.home

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
import com.dicoding.ujikomlsp.databinding.FragmentHomeBinding
import com.dicoding.ujikomlsp.preferences.UserPreferences
import com.dicoding.ujikomlsp.preferences.dataStore
import com.dicoding.ujikomlsp.ui.detail.DetailAbsensiActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var userPreference: UserPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreferences.getInstance(requireContext().dataStore)

        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                viewModel.getAbsensi(user.token)
            }
        }

        viewModel.absensi.observe(viewLifecycleOwner) { absen ->
            setAbsensiData(absen)
        }
    }

    private fun setAbsensiData(absensi: List<DataItem>) {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvAbsensi.layoutManager = layoutManager
        val adapter = AbsensiAdapter(
            onClick = {
                val moveToDetailAbsensi = Intent(requireContext(), DetailAbsensiActivity::class.java).apply {
                    putExtra(DetailAbsensiActivity.EXTRA_IDABSENSI, it.idAbsensi.toString())
                    putExtra(DetailAbsensiActivity.EXTRA_MAPEL, it.guru?.pelajaran)
                    putExtra(DetailAbsensiActivity.EXTRA_DEADLINE, it.bataswaktuAbsensi)
                }
                startActivity(moveToDetailAbsensi)
            }
        )
        adapter.submitList(absensi)
        binding.rvAbsensi.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
