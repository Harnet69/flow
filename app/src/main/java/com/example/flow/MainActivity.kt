package com.example.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.flow.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendBtn.setOnClickListener {
            clearErrorMessages()
            if (binding.userPsw.text.toString() == binding.userPswRep.text.toString()) {
                viewModel.login(binding.userName.text.toString(), binding.userPsw.text.toString())
            } else {
                binding.userPswRepInput.error = "Passwords aren't the same"
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.loginUiState.collect {
                when(it){
                    is MainViewModel.LoginUiState.Success -> onSuccess()
                    is MainViewModel.LoginUiState.Loading -> onLoading()
                    is MainViewModel.LoginUiState.Error -> onError()
                    else -> Unit
                }
            }
        }
    }

    private fun onSuccess() {
        binding.progressBar.isVisible = false
        Snackbar.make(binding.login, "Login successfully", Snackbar.LENGTH_SHORT).show()
    }

    private fun onLoading() {
        binding.progressBar.isVisible = true
    }

    private fun onError() {
        binding.progressBar.isVisible = false
        Snackbar.make(binding.login, "Something went wrong, try again later", Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun clearErrorMessages() {
        binding.userNameInput.error = null
        binding.userPswRepInput.error = null
        binding.userPswInput.error = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}