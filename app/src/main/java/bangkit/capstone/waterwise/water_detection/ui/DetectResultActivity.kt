package bangkit.capstone.waterwise.water_detection.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityDetectResultBinding
import bangkit.capstone.waterwise.utils.CustomToast
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.DetectWaterViewModel
import bangkit.capstone.waterwise.water_detection.machine_learning.WaterDetectionModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File

@Suppress("DEPRECATION")
class DetectResultActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetectResultBinding
    private lateinit var customToast : CustomToast

    private val loadingDialog by lazy { Helper.loadingDialog(this) }

    private val detectWaterViewModel: DetectWaterViewModel by viewModels()

    private lateinit var cameraActivityResult: ActivityResultLauncher<Intent>
    private var photoResult: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    private lateinit var waterDetectionModel: WaterDetectionModel

    companion object  {
        const val PHOTO_RESULT = "PHOTO_RESULT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        customToast = CustomToast(this)
        waterDetectionModel = WaterDetectionModel(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        with(binding){
            sendReviewBtnResult.setOnClickListener {
                showSendReviewFormDialog(this@DetectResultActivity)
            }

            btnBack.setOnClickListener {
                finish()
            }
            predictionMethodTitle.setOnClickListener{
                startActivity(Intent(this@DetectResultActivity, DetectByDataActivity::class.java))
            }
        }

        // handle result from camera activity
        photoResult = intent?.getSerializableExtra(PHOTO_RESULT) as File
        val bitmap = BitmapFactory.decodeFile(photoResult?.absolutePath)
        val orientation = Helper.getImageOrientation(photoResult!!)
        val rotatedBitmap = Helper.rotateBitmap(bitmap, orientation)
        binding.waterSourceImg.setImageBitmap(rotatedBitmap)

        detectWaterViewModel.detectWaterUsingModel(rotatedBitmap, waterDetectionModel)

        detectWaterViewModel.apply {
            isLoading.observe(this@DetectResultActivity) {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }

            isDrinkable.observe(this@DetectResultActivity) {
                setBadgeForResult(it)
                setResultDescription(it)
            }

            cleanlinessPercentage.observe(this@DetectResultActivity) {
                setCleanlinessPercentage(it * 100)
            }

            isSendReviewSuccess.observe(this@DetectResultActivity) {
                if (it) {
                    binding.sendReviewBtnResult.isEnabled = false
                    binding.sendReviewBtnResult.isClickable = false

                    MotionToast.createColorToast(
                        this@DetectResultActivity,
                        title = "Success",
                        message = "Review has been uploaded",
                        style = MotionToastStyle.SUCCESS,
                        position = Gravity.TOP,
                        duration = MotionToast.LONG_DURATION,
                        null
                    )
                }
            }

            isError.observe(this@DetectResultActivity) {
                if (it) {
                    MotionToast.createColorToast(
                        this@DetectResultActivity,
                        title = "Error",
                        message = "Failed to send review",
                        style = MotionToastStyle.ERROR,
                        position = Gravity.TOP,
                        duration = MotionToast.LONG_DURATION,
                        null
                    )
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyCurrentLocation()
        } else {
            Helper.infoDialog(this, getString(R.string.error_msg_permission_gps))
        }
    }

    private fun showSendReviewFormDialog(
        context: Context,
    ) {
        if (
            !Helper.isGPSEnabled(this) &&
            Helper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
            ) {
            Helper.infoDialog(
                this,
                "Your GPS is disabled, please enable it to use this feature"
            ).show()
            return
        } else {
            getMyCurrentLocation()
        }

        val reviewDialog = Helper.dialogBuilder(context, R.layout.form_review_dialog, true)
        val resultText = reviewDialog.findViewById<TextView>(R.id.detection_result)

        detectWaterViewModel.isDrinkable.observe(this) {
            setFormReviewDialogResult(it, resultText)
        }

        val submitBtn = reviewDialog.findViewById<Button>(R.id.send_review_btn)
        submitBtn.setOnClickListener {
            detectWaterViewModel.sendReview()
            reviewDialog.dismiss()
        }

        reviewDialog.show()
    }

    private fun getMyCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                (this),
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if(latitude == null && longitude == null){
                detectWaterViewModel.setLoadingState(true)
                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        Log.d("FETCH_CURRENT_LOCATION", "$location")
                    }

                    detectWaterViewModel.setLoadingState(false)
                }.addOnFailureListener( this) { e ->
                    detectWaterViewModel.setLoadingState(false)
                    Helper.infoDialog(this, "Failed to get your location")
                    Log.e("LocationUpdate", "Failed to get location: ${e.message}")
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getFont(font: Int): Typeface?{
        return ResourcesCompat.getFont(this, font)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBadgeForResult(isDrinkable: Boolean) {
        binding.apply {
            resultBadge.text = if (isDrinkable) getString(R.string.result_drinkable) else getString(R.string.result_not_drinkable)
            resultBadge.background = if (isDrinkable) getDrawable(R.drawable.success_badge) else getDrawable(R.drawable.error_badge)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCleanlinessPercentage(value: Number) {
        binding.cleanlinessPercentage.text = "$value%"
    }

    private fun setResultDescription(isDrinkable: Boolean) {
        if(isDrinkable) {
            binding.resultDesc.text = getString(R.string.result_drinkable_desc)
        } else {
            binding.resultDesc.text = getString(R.string.result_not_drinkable_desc)
        }
    }

    private fun setFormReviewDialogResult(isDrinkable: Boolean, textView: TextView) {
        if(isDrinkable) {
            textView.text = getString(R.string.result_drinkable)
            textView.setTextColor(getColor(R.color.success_badge))
        } else {
            textView.text = getString(R.string.result_not_drinkable)
            textView.setTextColor(getColor(R.color.error_badge))
        }
    }
}