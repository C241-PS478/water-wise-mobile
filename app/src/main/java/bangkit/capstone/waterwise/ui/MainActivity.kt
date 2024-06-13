package bangkit.capstone.waterwise.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityMainBinding
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.ui.CameraActivity

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showRecyclerView()

        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        with(binding) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)

            toolbar.menu.clear()
        }
    }

    private fun showRecyclerView() {
            recyclerView = findViewById(R.id.news_recycler_view)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        }
    }
}