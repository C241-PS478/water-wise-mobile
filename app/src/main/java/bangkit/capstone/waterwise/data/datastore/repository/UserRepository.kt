package bangkit.capstone.waterwise.data.datastore.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import bangkit.capstone.waterwise.data.datastore.model.UserModel
import bangkit.capstone.waterwise.data.datastore.pref.UserPreference
import bangkit.capstone.waterwise.data.remote.api.ApiConfig
import bangkit.capstone.waterwise.data.remote.api.ApiService
import bangkit.capstone.waterwise.data.remote.response.LoginResponse
import bangkit.capstone.waterwise.data.remote.response.RegisterResponse
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

    fun register(
        name: String,
        username: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val registerResponse = apiService.register(name, username, email, password)
            if (!registerResponse.error) {
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

    fun login(
        username: String,
        password: String
    ) = liveData {
        emit(Result.Loading)
        try {
            val responseLogin = apiService.login(username, password)
            if (responseLogin.error == false) {
                val token = UserModel(
                    name = responseLogin.loginResult.name,
                    userId = responseLogin.loginResult.userId,
                    token = responseLogin.loginResult.token,
                    isLogin = true
                )
                ApiConfig.token = responseLogin.loginResult.token
                userPreference.saveSession(token)
                emit(Result.Success(responseLogin))
            } else {
                emit(Result.Error(responseLogin.message))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
            val errorMessage = errorBody?.message ?: "An error occurred"
            emit(Result.Error("Login failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("Internet Issues"))
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
