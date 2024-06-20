package bangkit.capstone.waterwise.data.datastore.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.lifecycle.liveData
import bangkit.capstone.waterwise.data.remote.api.ApiService
import bangkit.capstone.waterwise.data.datastore.model.UserModel
import bangkit.capstone.waterwise.data.datastore.pref.UserPreference
import bangkit.capstone.waterwise.data.remote.api.ApiConfig
import bangkit.capstone.waterwise.data.remote.response.LoginResponse
import bangkit.capstone.waterwise.data.remote.response.RegisterResponse
import bangkit.capstone.waterwise.result.Result
import bangkit.capstone.waterwise.ui.main.ListPostResponse
import bangkit.capstone.waterwise.water_detection.PredictionResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun register(
        name: String,
        phoneNumber: String,
        username: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val registerResponse = apiService.register(name, phoneNumber, username, email, password)
            if (registerResponse.error == false) {
                emit(Result.Success(registerResponse))
            } else {
                emit(Result.Error(registerResponse.message))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
            val errorMessage = errorBody?.message ?: "An error occurred"
            emit(Result.Error("Registration failed: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error("Internet Issues"))
        }
    }

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

    suspend fun loginWithGoogle(firebaseId: String, email: String): Result<LoginResponse> {
        return try {
            val response = apiService.loginWithGoogle(firebaseId, email)
            if (!response.error) {
                Result.Success(response)
            } else {
                Result.Error("Login failed: ${response.message}")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            Result.Error("Login failed: ${errorResponse.message}")
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

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
