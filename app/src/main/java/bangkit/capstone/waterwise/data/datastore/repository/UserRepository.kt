package bangkit.capstone.waterwise.data.datastore.repository

import android.util.Log
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
        val responseLogin = apiService.login(username, password)
        try {
            if (responseLogin.isSuccessful) {
                val res = responseLogin.body()?.data
                val user = UserModel(
                    name = res?.user?.name!!,
                    userId = res.user.id!!,
                    token = res.token!!,
                    isLogin = true
                )
                ApiConfig.token = res.token
                userPreference.saveSession(user)
                emit(Result.Success(responseLogin.body()!!))
                Log.d("user_repo_login", "Success")
            } else {
                emit(Result.Error(responseLogin.message()))
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
        try {
            val response = apiService.loginWithGoogle(firebaseId, email)
            if (response.isSuccessful) {
                val res = response.body()?.data
                val user = UserModel(
                    name = res?.user?.name!!,
                    userId = res.user.id!!,
                    token = res.token!!,
                    isLogin = true
                )
                ApiConfig.token = res.token
                userPreference.saveSession(user)
                return Result.Success(response.body()!!)
            } else {
                return Result.Error(response.message())
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
            val errorMessage = errorBody?.message ?: "An error occurred"
            return Result.Error("Login failed: $errorMessage")
        } catch (e: Exception) {
            return Result.Error("Internet Issues")
        }
    }

    fun getPostWithLocation(): LiveData<kotlin.Result<ListPostResponse>> = liveData {
        emit(Result.Loading)
        try {
            val getToken = userPreference.getSession().first()
            val apiService = ApiConfig.getApiService(getToken.token)
            val locationPost = apiService.getPostWithLocation()
            emit(Result.Success(locationPost))
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(error, PredictionResponse::class.java)
            val errorMessage = errorResponse?.message ?: "An error occurred"
            emit(Result.Error("Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
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
