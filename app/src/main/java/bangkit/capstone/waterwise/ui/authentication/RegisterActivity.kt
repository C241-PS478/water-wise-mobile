package bangkit.capstone.waterwise.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityRegisterBinding
import bangkit.capstone.waterwise.result.Result
import bangkit.capstone.waterwise.utils.ViewModelFactory
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class RegisterActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)
        setupAction()

        binding.loginClick.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }


    private fun setupAction() {
        showLoading(false)
        binding.registerButton.setOnClickListener {
            showLoading(true)
            val name = binding.nameEditText.text.toString()
            val phoneNumber = binding.phoneEditText.text.toString()
            val username = binding.emailEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            when {
                name.isEmpty() -> {
                    showLoading(false)
                    binding.nameETLayout.error = getString(R.string.null_name)
                }
                phoneNumber.isEmpty() -> {
                    showLoading(false)
                    binding.phoneETLayout.error = getString(R.string.null_phone)
                }
                email.isEmpty() -> {
                    showLoading(false)
                    binding.emailETLayout.error = getString(R.string.null_email)
                }
                password.isEmpty() -> {
                    showLoading(false)
                    binding.passwordEditTextLayout.error = getString(R.string.null_password)
                }
                else -> {
                    showLoading(true)
                    register(name, username, email, password)
                }
            }
        }
    }

    private fun register(name: String, username: String, email: String, password: String) {
        showLoading(true)
        viewModel.register(name, username, email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(true)
                        MotionToast.createColorToast(
                            this@RegisterActivity,
                            title = "Success",
                            message = "Account created successfully!",
                            style = MotionToastStyle.SUCCESS,
                            position = Gravity.BOTTOM,
                            duration = MotionToast.LONG_DURATION,
                            null
                        )
                        viewModel.getSession()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }

                    is Result.Error -> {
                        showLoading(false)
                        MotionToast.createColorToast(
                            this@RegisterActivity,
                            title = "Register Failed!",
                            message = result.error,
                            style = MotionToastStyle.ERROR,
                            position = Gravity.BOTTOM,
                            duration = MotionToast.LONG_DURATION,
                            null
                        )
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loginClick.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
