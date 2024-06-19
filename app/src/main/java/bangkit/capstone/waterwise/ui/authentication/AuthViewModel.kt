package bangkit.capstone.waterwise.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bangkit.capstone.waterwise.data.datastore.model.UserModel
import bangkit.capstone.waterwise.data.datastore.repository.UserRepository
import bangkit.capstone.waterwise.data.remote.response.LoginResponse
import bangkit.capstone.waterwise.result.Result
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    val loginResult: LiveData<Result<LoginResponse>> get() = userRepository.loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            userRepository.login(email, password)
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }

    fun loginWithGoogle(firebaseId: String, email: String) {
        viewModelScope.launch {
            userRepository.loginWithGoogle(firebaseId, email)
        }
    }

    suspend fun register(
        name: String,
        phoneNumber: String,
        email: String,
        password: String
    ) = userRepository.register(name, phoneNumber, email, password)
}
