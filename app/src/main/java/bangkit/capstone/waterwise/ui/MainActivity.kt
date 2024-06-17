package bangkit.capstone.waterwise.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityMainBinding
import bangkit.capstone.waterwise.news.ui.NewsActivity
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.ui.CameraActivity

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            bottomNavigationView.background = null
            bottomNavigationView.menu.getItem(2).isEnabled = false
            bottomNavigationView.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navbarNews -> {
                       startActivity(Intent(this@MainActivity, NewsActivity::class.java))
                        true
                    }
                    else -> false
                }
            }

            fab.setOnClickListener {
                if (Helper.isPermissionGranted(this@MainActivity, Manifest.permission.CAMERA)) {
                    startActivity(Intent(this@MainActivity, CameraActivity::class.java))
                } else {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        10
                    )
                }
            }
        }
    }

}