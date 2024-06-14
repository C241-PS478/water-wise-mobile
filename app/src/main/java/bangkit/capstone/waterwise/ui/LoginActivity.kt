package bangkit.capstone.waterwise.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.custom.CustomEmailReq
import bangkit.capstone.waterwise.custom.CustomPasswordReq
import bangkit.capstone.waterwise.databinding.ActivityLoginBinding
import bangkit.capstone.waterwise.utils.ViewModelFactory
import bangkit.capstone.waterwise.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var emailReq: CustomEmailReq
    private lateinit var passwordReq: CustomPasswordReq
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        emailReq = binding.emailEditText
        passwordReq = binding.passwordEditText
    }
}