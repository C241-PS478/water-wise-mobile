package bangkit.capstone.waterwise.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import bangkit.capstone.waterwise.result.Result
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.data.datastore.model.UserModel
import bangkit.capstone.waterwise.databinding.ActivityLoginBinding
import bangkit.capstone.waterwise.ui.main.MainActivity
import bangkit.capstone.waterwise.utils.CustomToast
import bangkit.capstone.waterwise.utils.ViewModelFactory
import bangkit.capstone.waterwise.water_detection.ui.CameraActivity.Companion.TAG
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var customToast : CustomToast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customToast = CustomToast(this)
        auth = Firebase.auth

        binding.googleSignInButton.setOnClickListener {
            googleSignIn()
        }

        setupViewModel()
        setupAction()
    }

    private fun googleSignIn() {
        val credentialManager = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    user?.let {
                        lifecycleScope.launch {
                            viewModel.loginWithGoogle(it.uid, it.email ?: "")
                        }
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
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
                    MotionToast.createColorToast(
                        this@LoginActivity,
                        title = "Success",
                        message = result.data.message,
                        style = MotionToastStyle.SUCCESS,
                        position = Gravity.BOTTOM,
                        duration = MotionToast.LONG_DURATION,
                        null
                    )
                    // Navigate to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    MotionToast.createColorToast(
                        this@LoginActivity,
                        title = "Error found",
                        message = result.error,
                        style = MotionToastStyle.ERROR,
                        position = Gravity.BOTTOM,
                        duration = MotionToast.LONG_DURATION,
                        null
                    )
                }
            }
        })
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                MotionToast.createColorToast(
                    this@LoginActivity,
                    title = "Error email",
                    message = R.string.null_email.toString(),
                    style = MotionToastStyle.ERROR,
                    position = Gravity.BOTTOM,
                    duration = MotionToast.LONG_DURATION,
                    null
                )
                return@setOnClickListener
            }

            showLoading(true)

            viewModel.login(email, password)
            viewModel.loginResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        MotionToast.createColorToast(
                            this@LoginActivity,
                            title = "Success",
                            message = R.string.login_success.toString(),
                            style = MotionToastStyle.SUCCESS,
                            position = Gravity.BOTTOM,
                            duration = MotionToast.LONG_DURATION,
                            null
                        )

                        val user = UserModel(
                            result.data.loginResult.token,
                            result.data.loginResult.name,
                            result.data.loginResult.userId,
                            true
                        )
                        viewModel.saveSession(user)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        MotionToast.createColorToast(
                            this@LoginActivity,
                            title = "Error",
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
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
