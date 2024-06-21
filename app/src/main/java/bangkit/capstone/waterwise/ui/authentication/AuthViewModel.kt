package bangkit.capstone.waterwise.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bangkit.capstone.waterwise.data.datastore.model.UserModel
import bangkit.capstone.waterwise.data.datastore.repository.UserRepository
import bangkit.capstone.waterwise.data.remote.response.LoginResponse
import bangkit.capstone.waterwise.data.remote.response.RegisterResponse
import bangkit.capstone.waterwise.result.Result
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    val loginResult: LiveData<Result<LoginResponse>> get() = userRepository.loginResult

    private val _loginGoogleResult = MutableLiveData<Result<LoginResponse>>()
    val loginGoogleResult: LiveData<Result<LoginResponse>> get() = _loginGoogleResult



    fun login(
        username: String,
        password: String
    ): LiveData<Result<LoginResponse>> = userRepository.login(username, password)

    fun register(
        name: String,
        username: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = userRepository.register(name, username, email, password)

    fun getSession() {
        viewModelScope.launch {
            userRepository.getSession()
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }

    fun loginWithGoogle(firebaseId: String, email: String) {
        viewModelScope.launch {
            val result = userRepository.loginWithGoogle(firebaseId, email)
            _loginGoogleResult.postValue(result)
        }
    }
}
