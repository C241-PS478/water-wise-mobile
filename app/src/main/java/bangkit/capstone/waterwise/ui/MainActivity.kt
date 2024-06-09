package bangkit.capstone.waterwise.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import bangkit.capstone.waterwise.databinding.ActivityMainBinding
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.ui.CameraActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        with(binding) {
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