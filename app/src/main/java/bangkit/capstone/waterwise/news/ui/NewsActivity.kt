package bangkit.capstone.waterwise.news.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityNewsBinding
import bangkit.capstone.waterwise.news.NewsAdapter
import bangkit.capstone.waterwise.news.NewsViewModel
import bangkit.capstone.waterwise.ui.MainActivity
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.ui.CameraActivity

@Suppress("DEPRECATION")
class NewsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewsBinding
    private val newsAdapter = NewsAdapter()
    private val newsViewModel = NewsViewModel()

    private val loadingDialog by lazy { Helper.loadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            bottomNavigationView.background = null
            bottomNavigationView.menu.getItem(2).isEnabled = false
            bottomNavigationView.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navbarHome -> {
                        startActivity(Intent(this@NewsActivity, MainActivity::class.java))
                        finish()
                        true
                    }
                    R.id.navbarNews -> true
                    else -> false
                }
            }
            bottomNavigationView.selectedItemId = R.id.navbarNews

            fab.setOnClickListener {
                if (Helper.isPermissionGranted(this@NewsActivity, Manifest.permission.CAMERA)) {
                    startActivity(Intent(this@NewsActivity, CameraActivity::class.java))
                } else {
                    ActivityCompat.requestPermissions(
                        this@NewsActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        10
                    )
                }
            }
        }

        newsViewModel.isLoading.observe(this) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }

        newsViewModel.findAllPaginated().cachedIn(lifecycle).observe(this) {
            newsAdapter.submitData(lifecycle, it)
        }

        setupRecyclerView(binding)
    }

    private fun setupRecyclerView(binding: ActivityNewsBinding) {
        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            isNestedScrollingEnabled = false
            adapter = newsAdapter
        }
    }
}