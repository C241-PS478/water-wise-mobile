package bangkit.capstone.waterwise.ui.starting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.adapter.IntroPagerAdapter
import bangkit.capstone.waterwise.data.datastore.model.IntroItem
import bangkit.capstone.waterwise.databinding.ActivityIntroBinding
import bangkit.capstone.waterwise.ui.authentication.LoginActivity
import bangkit.capstone.waterwise.ui.main.MainActivity
import bangkit.capstone.waterwise.water_detection.ui.CameraActivity
import com.google.android.material.tabs.TabLayoutMediator

class IntroActivity : AppCompatActivity() {


    private lateinit var binding: ActivityIntroBinding
    private lateinit var introItems: List<IntroItem>
    private lateinit var introPagerAdapter: IntroPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.skipIntro.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        introItems = listOf(
            IntroItem(R.drawable.intro_illustration_1, "Welcome to WaterWise!", "Experience the power of technology in ensuring clean water. WaterWise helps you detect water clarity and share your findings with others. Join a global community committed to clean water."),
            IntroItem(R.drawable.intro_illustration_2, "Easy Water Clarity Detection", "Use our app to quickly and accurately assess the clarity of your water. Simply take a picture and get instant results. Ensure your water is safe to drink with just a few taps."),
            IntroItem(R.drawable.intro_illustration_3, "Share Your Location", "Report the clarity of water in your area. Share your location to help others find clean water sources nearby. Be a part of the solution by providing valuable data."),
            IntroItem(R.drawable.intro_illustration_4, "Stay Informed", "Access the latest news on clean water initiatives worldwide. Learn about global efforts and progress in water sanitation. Stay updated on the challenges and solutions in clean water advocacy."),
            IntroItem(R.drawable.intro_illustration_5, "Join the WaterWise Community", "Connect with others who care about clean water. Share your experiences and learn from the community. Together, we can make a difference in ensuring safe water for everyone.")
        )

        introPagerAdapter = IntroPagerAdapter(introItems)
        binding.viewPager2.adapter = introPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { _, _ -> }.attach()

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == introItems.size - 1) {
                    binding.btnNext.text = "Get Started"
                } else {
                    binding.btnNext.text = "Next"
                }
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.viewPager2.currentItem < introItems.size - 1) {
                binding.viewPager2.currentItem = binding.viewPager2.currentItem + 1
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}
