package bangkit.capstone.waterwise.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.capstone.waterwise.data.datastore.repository.UserRepository
import bangkit.capstone.waterwise.data.remote.api.ApiConfig
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
}
