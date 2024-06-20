package bangkit.capstone.waterwise.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.result.Result
import bangkit.capstone.waterwise.databinding.ActivityMainBinding
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
    private val newsAdapter = NewsAdapter()
    private val newsViewModel = NewsViewModel()

    private val loadingDialog by lazy { Helper.loadingDialog(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

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
            bottomNavigationView.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navbarHome -> true

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
                        e.printStackTrace()
                    }
                }
            }

            newsViewModel.isLoading.observe(this@MainActivity) {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
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

    @SuppressLint("SetTextI18n")
    private fun getUserData() {
        mainViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val loginResult = result.data.loginResult
                    binding.tvHelloName.text = "Hello, ${loginResult.name}!"
                }
                is Result.Error -> {
                    // Handle error, e.g., show a message
                }
                is Result.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }
}
