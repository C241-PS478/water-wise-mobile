package bangkit.capstone.waterwise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyviewapp.data.datastore.UserModel
import com.dicoding.storyviewapp.data.datastore.UserRepository
import com.dicoding.storyviewapp.data.remote.response.LoginResponse
import com.dicoding.storyviewapp.result.Result
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

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
}
