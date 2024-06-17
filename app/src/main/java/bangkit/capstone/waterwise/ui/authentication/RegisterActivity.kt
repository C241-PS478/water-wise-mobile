// RegisterActivity.kt
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.data.remote.response.RegisterResponse
import bangkit.capstone.waterwise.databinding.ActivityRegisterBinding
import bangkit.capstone.waterwise.ui.authentication.AuthViewModel
import bangkit.capstone.waterwise.ui.authentication.LoginActivity
import bangkit.capstone.waterwise.utils.ViewModelFactory
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
            showLoading(true)
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            when {
                name.isEmpty() -> {
                    showLoading(false)
                    binding.nameETLayout.error = getString(R.string.null_name)
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
                            val response = viewModel.register(name, email, password)
                            showLoading(false)
                            showToast(response.message)

                            val customLayout = LayoutInflater.from(this@RegisterActivity)
                                .inflate(R.layout.custom_dialog_layout, null)
                            val animationView =
                                customLayout.findViewById<LottieAnimationView>(R.id.animationView)
                            animationView.setAnimation("confirm.json")
                            animationView.playAnimation()

                            AlertDialog.Builder(this@RegisterActivity).apply {
                                setTitle("Good News!")
                                setMessage(getString(R.string.success_registration))
                                setView(customLayout)
                                setPositiveButton(getString(R.string.next)) { _, _ ->
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        } catch (e: HttpException) {
                            showLoading(false)
                            val errorBody = e.response()?.errorBody()?.string()
                            val errorResponse =
                                Gson().fromJson(errorBody, RegisterResponse::class.java)
                            showToast(errorResponse.message)
                        }

                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
