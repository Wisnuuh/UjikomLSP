package com.dicoding.ujikomlsp.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.ujikomlsp.R
import com.dicoding.ujikomlsp.databinding.FragmentDashboardBinding
import com.dicoding.ujikomlsp.databinding.FragmentProfileBinding
import com.dicoding.ujikomlsp.preferences.UserPreferences
import com.dicoding.ujikomlsp.preferences.dataStore
import com.dicoding.ujikomlsp.ui.home.HomeViewModel
import com.dicoding.ujikomlsp.ui.login.LoginActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var userPreference: UserPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreferences.getInstance(requireContext().dataStore)

        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                viewModel.getProfile(user.token)
            }
        }

        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                binding.tvNama.text = profile.namaSiswa ?: "Null"
                binding.tvNisn.text = "NISN: ${profile.nisn}" ?: "Null"
                binding.tvKelas.text = "Kelas: ${profile.kela?.jenjang}" ?: "Null"
                binding.tvJurusan.text = "Jurusan ${profile.kela?.namaKelas}" ?: "Null"
            }
        }

        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                userPreference.logout()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }
}