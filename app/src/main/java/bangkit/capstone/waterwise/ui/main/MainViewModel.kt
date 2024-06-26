package bangkit.capstone.waterwise.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import bangkit.capstone.waterwise.data.datastore.model.UserModel
import bangkit.capstone.waterwise.data.datastore.repository.UserRepository
import bangkit.capstone.waterwise.data.remote.response.LoginResponse
import bangkit.capstone.waterwise.news.Service
import bangkit.capstone.waterwise.remote.response.MainResponse
import bangkit.capstone.waterwise.result.Result
import bangkit.capstone.waterwise.utils.Api

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
    val newsService = Api.service(Service::class.java)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _user = MutableLiveData<MainResponse?>()
    val user: LiveData<MainResponse?> = _user

    val loginResult: LiveData<Result<LoginResponse>> get() = userRepository.loginResult

    fun findSomeNews() {
        val apiKey = ""
        val client = newsService.findSome(apiKey)
    }

    fun getUserData(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }
}
