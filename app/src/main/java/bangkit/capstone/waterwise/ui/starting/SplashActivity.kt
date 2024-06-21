package bangkit.capstone.waterwise.ui.starting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.data.datastore.pref.UserDataStore
import bangkit.capstone.waterwise.data.datastore.pref.UserPreference
import bangkit.capstone.waterwise.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var userPreference: UserPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        userPreference = UserPreference.getInstance(UserDataStore)

        Handler(Looper.getMainLooper()).postDelayed({
            userPreference.getSession().asLiveData().observe(this@SplashActivity){
                if(it.isLogin){
                val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else{
                    val intent = Intent(this, IntroActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            }


        }, 4000L)
    }
}