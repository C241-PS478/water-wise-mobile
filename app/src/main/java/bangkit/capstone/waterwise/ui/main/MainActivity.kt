package bangkit.capstone.waterwise.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.capstone.waterwise.BuildConfig
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.data.datastore.model.UserModel
import bangkit.capstone.waterwise.data.datastore.pref.UserDataStore
import bangkit.capstone.waterwise.data.datastore.pref.UserPreference
import bangkit.capstone.waterwise.data.datastore.repository.UserRepository
import bangkit.capstone.waterwise.data.remote.api.ApiConfig
import bangkit.capstone.waterwise.data.remote.api.ApiService
import bangkit.capstone.waterwise.databinding.ActivityMainBinding
import bangkit.capstone.waterwise.news.NewsHorizontalAdapter
import bangkit.capstone.waterwise.news.NewsViewModel
import bangkit.capstone.waterwise.news.ui.NewsActivity
import bangkit.capstone.waterwise.result.Result
import bangkit.capstone.waterwise.ui.MapsActivity
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.utils.ViewModelFactory
import bangkit.capstone.waterwise.water_detection.ui.CameraActivity
import bangkit.capstone.waterwise.water_detection.ui.DetectByDataActivity
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val newsAdapter = NewsHorizontalAdapter()
    private val newsViewModel = NewsViewModel()

    private val loadingDialog by lazy { Helper.loadingDialog(this) }

    private lateinit var apiService: ApiService
    private lateinit var userPreference: UserPreference

    private lateinit var userSession: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(UserDataStore)

        setUserDataFromSession()
        setupRecyclerView()

        userPreference.getSession().asLiveData().observe(this) { user ->
            updateUI(user)
        }

        val newsApiToken = "Bearer  ${BuildConfig.NEWS_API_KEY}"
        newsViewModel.findSome(newsApiToken)
        newsViewModel.listNews.observe(this) {
            newsAdapter.submitList(it)
        }

        binding.mapThumbnail.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
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
            bottomNavigationView.selectedItemId = R.id.navbarHome

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

            btnDetectImage.setOnClickListener{
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
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            isNestedScrollingEnabled = false
            adapter = newsAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getUserData() {
        mainViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val res = result.data.data
                    binding.tvHelloName.text = "Hello, ${res?.user?.name}!"
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

    private fun setUserDataFromSession() {
        lifecycleScope.launch {
            userPreference.getSession().collect { userModel ->
                val token = userModel.token
                val name = userModel.name
                val userId = userModel.userId
                val isLoggedIn = userModel.isLogin

                userSession = UserModel(token, name, userId, isLoggedIn)
                apiService = ApiConfig.getApiService(token)
            }
            initViewModel()
            getUserData()
        }
    }

    private fun initViewModel() {
        val userRepo = UserRepository.getInstance(apiService, userPreference)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(userRepo))[MainViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(user: UserModel) {
        binding.apply {
            tvHelloName.text = "Hello, ${user.name}"
            userLocation.visibility = View.GONE
        }
    }
}
