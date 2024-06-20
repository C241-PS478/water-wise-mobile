package bangkit.capstone.waterwise.data.datastore.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import bangkit.capstone.waterwise.data.remote.api.ApiService
import bangkit.capstone.waterwise.data.datastore.model.UserModel
import bangkit.capstone.waterwise.data.datastore.pref.UserPreference
import bangkit.capstone.waterwise.data.remote.response.LoginResponse
import bangkit.capstone.waterwise.result.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    suspend fun register(name: String, phoneNumber: String, email: String, password: String) = apiService.register(name, phoneNumber, email, password)

    suspend fun login(email: String, password: String) {
        _loginResult.postValue(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (!response.error) {
                _loginResult.postValue(Result.Success(response))
            } else {
                _loginResult.postValue(Result.Error("Login failed: ${response.message}"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            _loginResult.postValue(Result.Error("Login failed: ${errorResponse.message}"))
        } catch (e: Exception) {
            _loginResult.postValue(Result.Error("An unexpected error occurred: ${e.message}"))
        }
    }

    suspend fun loginWithGoogle(firebaseId: String, email: String) {
        _loginResult.postValue(Result.Loading)
        try {
            val response = apiService.loginWithGoogle(firebaseId, email)
            if (!response.error) {
                _loginResult.postValue(Result.Success(response))
            } else {
                _loginResult.postValue(Result.Error("Login failed: ${response.message}"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            _loginResult.postValue(Result.Error("Login failed: ${errorResponse.message}"))
        } catch (e: Exception) {
            _loginResult.postValue(Result.Error("An unexpected error occurred: ${e.message}"))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> = userPreference.getSession()

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
        ): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserRepository(apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
        fun clearInstance() {
            INSTANCE = null
        }
    }
}
