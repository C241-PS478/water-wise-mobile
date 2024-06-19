package bangkit.capstone.waterwise.ui.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.data.remote.response.RegisterResponse
import bangkit.capstone.waterwise.databinding.ActivityRegisterBinding
import bangkit.capstone.waterwise.utils.ViewModelFactory
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
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

        setupView()
        setupAction()

        binding.loginClick
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.registerButton.setOnClickListener {
            showLoading(true)
            val name = binding.nameEditText.text.toString()
            val phoneNumber = binding.phoneEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            when {
                name.isEmpty() -> {
                    showLoading(false)
                    binding.nameETLayout.error = getString(R.string.null_name)
                }
                phoneNumber.isEmpty() -> {
                    showLoading(false)
                    binding.emailETLayout.error = getString(R.string.null_phone)
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
                    lifecycleScope.launch {
                        try {
                            val response = viewModel.register(name, phoneNumber, email, password)
                            showLoading(false)
                            handleRegistrationResponse(response)
                        } catch (e: HttpException) {
                            showLoading(false)
                            val errorBody = e.response()?.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                            MotionToast.createColorToast(
                                this@RegisterActivity,
                                title = "Error Response",
                                message = errorResponse.message,
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
    }

    private fun handleRegistrationResponse(response: RegisterResponse) {
        if (!response.error) {
            showSuccessDialog()
        } else {
            MotionToast.createColorToast(
                this@RegisterActivity,
                title = "Information",
                message = response.message,
                style = MotionToastStyle.INFO,
                position = Gravity.BOTTOM,
                duration = MotionToast.LONG_DURATION,
                null
            )
        }
    }

    @SuppressLint("InflateParams")
    private fun showSuccessDialog() {
        val customLayout = LayoutInflater.from(this@RegisterActivity)
            .inflate(R.layout.custom_dialog_layout, null)
        val animationView =
            customLayout.findViewById<LottieAnimationView>(R.id.animationView)
        animationView.setAnimation("confirm.json")
        animationView.playAnimation()

        AlertDialog.Builder(this@RegisterActivity).apply {
            setTitle("Success!")
            setMessage(getString(R.string.success_registration))
            setView(customLayout)
            setPositiveButton(getString(R.string.next)) { _, _ ->
                startActivity(Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
                finish()
            }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
