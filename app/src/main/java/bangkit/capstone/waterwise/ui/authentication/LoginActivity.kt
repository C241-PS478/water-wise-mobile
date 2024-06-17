package bangkit.capstone.waterwise.ui.authentication

import RegisterActivity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.data.datastore.model.UserModel
import bangkit.capstone.waterwise.databinding.ActivityLoginBinding
import bangkit.capstone.waterwise.result.Result
import bangkit.capstone.waterwise.utils.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupGoogleSignIn()
        setupAction()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[AuthViewModel::class.java]

        viewModel.loginResult.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    showToast(result.data.message)
                    // Navigate to main activity or other destination
                }
                is Result.Error -> {
                    showLoading(false)
                    showToast(result.error)
                }
            }
        })
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task)
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(Exception::class.java)
            account?.let {
                val user = UserModel(
                    userId = it.id ?: "",
                    firebaseId = it.id,
                    email = it.email ?: "",
                    name = it.displayName ?: "",
                    phoneNumber = null, // Or it.phoneNumber if available
                    address = null, // Assuming no address is available at this point
                    token = it.idToken ?: ""
                )
                viewModel.loginWithGoogle(user)
            }
        } catch (e: Exception) {
            showToast("Google Sign-In failed.")
        }
    }


    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            when {
                email.isEmpty() -> binding.emailEditText.error = getString(R.string.null_email)
                password.isEmpty() -> binding.passwordEditTextLayout.error = getString(R.string.null_password)
                else -> viewModel.login(email, password)
            }
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
