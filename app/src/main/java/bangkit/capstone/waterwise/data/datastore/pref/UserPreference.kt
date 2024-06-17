package bangkit.capstone.waterwise.data.datastore.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import bangkit.capstone.waterwise.data.datastore.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.UserDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.userId
            preferences[FIREBASE_ID_KEY] = user.firebaseId ?: ""
            preferences[EMAIL_KEY] = user.email
            preferences[NAME_KEY] = user.name
            preferences[IS_ADMIN_KEY] = user.isAdmin.toString()
            preferences[IS_LOGIN_KEY] = true.toString()
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                userId = preferences[USER_ID_KEY] ?: "",
                firebaseId = preferences[FIREBASE_ID_KEY] ?: "",
                email = preferences[EMAIL_KEY] ?: "",
                name = preferences[NAME_KEY] ?: "",
                isAdmin = preferences[IS_ADMIN_KEY]?.toBoolean() ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val FIREBASE_ID_KEY = stringPreferencesKey("firebaseId")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name")
        private val IS_ADMIN_KEY = stringPreferencesKey("isAdmin")
        private val IS_LOGIN_KEY = stringPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
