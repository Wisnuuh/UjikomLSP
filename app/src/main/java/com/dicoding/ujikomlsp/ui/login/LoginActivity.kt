package com.dicoding.ujikomlsp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.ujikomlsp.MainActivity
import com.dicoding.ujikomlsp.R
import com.dicoding.ujikomlsp.databinding.ActivityLoginBinding
import com.dicoding.ujikomlsp.preferences.UserModel
import com.dicoding.ujikomlsp.preferences.UserPreferences
import com.dicoding.ujikomlsp.preferences.dataStore
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreference: UserPreferences
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getActionBar()?.hide();

        userPreference = UserPreferences.getInstance(applicationContext.dataStore)

        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                if (user.isLogin) {
                    Log.d(TAG, "onCreate: ${user.isLogin}")
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.btnLogin.setOnClickListener {
            val nisn = binding.etNisn.text.toString()
            val pw = binding.etPassword.text.toString()
            if (nisn.isEmpty() && pw.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Masukkan NISN dan Password", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.getTokenLogin(nisn, pw)
                viewModel.messageResponse.observe(this) { message ->
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                }
                viewModel.tokenLogin.observe(this@LoginActivity) { token ->
                    lifecycleScope.launch {
                        userPreference.saveSession(UserModel(token, true))
                        Log.d(TAG, "onCreate: $token")
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}