package bangkit.capstone.waterwise.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityMainBinding
import bangkit.capstone.waterwise.databinding.ActivityNewsBinding
import bangkit.capstone.waterwise.news.NewsAdapter
import bangkit.capstone.waterwise.news.NewsViewModel
import bangkit.capstone.waterwise.news.ui.NewsActivity
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.ui.CameraActivity
import bangkit.capstone.waterwise.water_detection.ui.DetectByDataActivity
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsbinding: ActivityNewsBinding
    private val newsAdapter = NewsAdapter()
    private val newsViewModel = NewsViewModel()

    private val loadingDialog by lazy { Helper.loadingDialog(this) }

    val username = intent.getStringExtra(USER_EXTRA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        getUserData()

        binding.mapThumbnail.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        binding.btnDetectImage.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
        binding.btnDetectData.setOnClickListener {
            val intent = Intent(this, DetectByDataActivity::class.java)
            startActivity(intent)
        }
        binding.moreNews.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        with(binding) {
            bottomNavigationView.background = null
            bottomNavigationView.menu.getItem(2).isEnabled = false
            bottomNavigationView.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navbarHome ->  true

                    R.id.navbarNews -> {
                        startActivity(Intent(this@MainActivity, NewsActivity::class.java))
                        finish()
                        true
                    }

                    else -> false
                }
            }
            bottomNavigationView.selectedItemId = R.id.navbarNews

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

            newsViewModel.findAllPaginated().cachedIn(lifecycleScope).observe(this@MainActivity) { pagingData ->
                lifecycleScope.launch {
                    try {
                        newsAdapter.submitData(pagingData)
                    } catch (e: Exception) {
                        // Handle any exceptions that occur during data submission
                        // For example, you can show an error message or log the error
                        e.printStackTrace()
                    }
                }
            }


            setupRecyclerView()
        }

        newsViewModel.isLoading.observe(this) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            isNestedScrollingEnabled = false
            adapter = newsAdapter
        }
    }

    private fun getUserData() {
        if (username != null) {
            mainViewModel.getName(username)
        }

        mainViewModel.user.observe(this) {
            if (it != null) {
                binding.tvHelloName.text = "Hello, ${it.name}!"

            }
        }
    }
    companion object {
        const val USER_EXTRA = "USERNAME"
    }
}