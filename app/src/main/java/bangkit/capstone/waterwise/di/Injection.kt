package bangkit.capstone.waterwise.di

import android.content.Context
import bangkit.capstone.waterwise.data.datastore.pref.UserDataStore
import bangkit.capstone.waterwise.data.datastore.pref.UserPreference
import bangkit.capstone.waterwise.data.datastore.repository.UserRepository
import bangkit.capstone.waterwise.data.remote.api.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {fun provideRepository(context: Context): UserRepository {
    val pref = UserPreference.getInstance(context.UserDataStore)
    val user = runBlocking { pref.getSession().first() }
    val apiService = ApiConfig.getApiService(user.token)
    return UserRepository.getInstance(apiService, pref)
}
}