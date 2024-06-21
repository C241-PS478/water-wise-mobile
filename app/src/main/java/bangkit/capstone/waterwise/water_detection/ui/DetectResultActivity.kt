package bangkit.capstone.waterwise.water_detection.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.data.datastore.pref.UserDataStore
import bangkit.capstone.waterwise.data.datastore.pref.UserPreference
import bangkit.capstone.waterwise.databinding.ActivityDetectResultBinding
import bangkit.capstone.waterwise.review.ReviewViewModel
import bangkit.capstone.waterwise.review.types.ReviewFormDialogListener
import bangkit.capstone.waterwise.utils.CustomToast
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.DetectWaterViewModel
import bangkit.capstone.waterwise.water_detection.PredictionMethod
import bangkit.capstone.waterwise.water_detection.ViewModelFactory
import bangkit.capstone.waterwise.water_detection.machine_learning.WaterDetectionModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File

@Suppress("DEPRECATION")
class DetectResultActivity : AppCompatActivity(), ReviewFormDialogListener {
    private lateinit var binding : ActivityDetectResultBinding
    private lateinit var customToast : CustomToast

    private val loadingDialog by lazy { Helper.loadingDialog(this) }

    private lateinit var detectWaterViewModel: DetectWaterViewModel
    private val reviewViewModel: ReviewViewModel by viewModels()

    private var photoResult: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var finalImage: File? = null

    private lateinit var waterDetectionModel: WaterDetectionModel
    private var token: String = ""
    private var _rotatedBitmap: Bitmap? = null

    private lateinit var formReviewDialog: ReviewFormDialog
    private lateinit var userPreference: UserPreference

    companion object  {
        const val PHOTO_RESULT = "PHOTO_RESULT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        userPreference = UserPreference.getInstance(UserDataStore)
        detectWaterViewModel = ViewModelProvider(this, ViewModelFactory(userPreference))[DetectWaterViewModel::class.java]

        formReviewDialog = ReviewFormDialog(reviewViewModel, detectWaterViewModel)
        onReviewSubmitted(PredictionMethod.BY_IMAGE)

        val context = this
        customToast = CustomToast(this)
        waterDetectionModel = WaterDetectionModel(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        with(binding){
            sendReviewBtnResult.setOnClickListener {
                showSendReviewFormDialog()
            }
            btnTryAgain.setOnClickListener {
                if (Helper.isHasInternetConnection(context)) {
                    detectWaterViewModel.predictQuality(context, token, finalImage!!)
                } else {
                    detectWaterViewModel.detectWaterUsingModel(_rotatedBitmap!!, waterDetectionModel)
                }
            }
            btnBack.setOnClickListener {
                finish()
            }
            predictionMethodTitle.setOnClickListener{
                startActivity(Intent(context, DetectByDataActivity::class.java))
                finish()
            }
        }

        // handle result from camera activity
        photoResult = intent?.getSerializableExtra(PHOTO_RESULT) as File
        val bitmap = BitmapFactory.decodeFile(photoResult?.absolutePath)
        val orientation = Helper.getImageOrientation(photoResult!!)
        val rotatedBitmap = Helper.rotateBitmap(bitmap, orientation)
        _rotatedBitmap = rotatedBitmap
        binding.waterSourceImg.setImageBitmap(rotatedBitmap)

        finalImage = Helper.bitmapToImage(rotatedBitmap, this)
        if (Helper.isHasInternetConnection(this)) {
            detectWaterViewModel.predictQuality(this, token, finalImage!!)
        } else {
            detectWaterViewModel.detectWaterUsingModel(rotatedBitmap, waterDetectionModel)
        }

        //TO DO
        // get token from shared preferences
        detectWaterViewModel.getToken().observe(this){
            token = "Bearer $it"
            onTokenRetrieved(token)
        }

        detectWaterViewModel.apply {
            isLoading.observe(context) {
                if (it) {
                    binding.errorMessageContainer.visibility = GONE
                    binding.loaderContainer.visibility = VISIBLE
                    binding.sendReviewBtnResult.visibility = GONE
                } else {
                    binding.loaderContainer.visibility = GONE
                    binding.sendReviewSuccessMessage.visibility = GONE
                }
            }

            isDrinkable.observe(context) {
                setBadgeForResult(it)
                setResultDescription(it)
            }

            cleanlinessPercentage.observe(context) {
                setCleanlinessPercentage(it * 100)
            }

            predictionResponse.observe(context) {
                onPredictionIdRetrieved(it.id)
            }

            isSuccess.observe(context) {
                if (it) {
                    binding.errorMessageContainer.visibility = GONE
                    binding.sendReviewBtnResult.visibility = VISIBLE
                }
            }

            isPhotoInvalid.observe(context) {
                if (it) {
                    binding.errorMessageContainer.visibility = VISIBLE
                    binding.sendReviewBtnResult.visibility = GONE

                    Helper.infoDialog(context, "The water object is too small, please take another photo" ).show()
                }
            }

            isError.observe(context) {
                if (it) {
                    binding.errorMessageContainer.visibility = VISIBLE
                    binding.sendReviewBtnResult.visibility = GONE
                }
            }
        }

        reviewViewModel.apply {
            isLoading.observe(context) {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }

            isSuccess.observe(context) {
                if (it) {
                    binding.sendReviewBtnResult.isEnabled = false
                    binding.sendReviewBtnResult.isClickable = false
                    binding.sendReviewBtnResult.visibility = GONE
                    binding.sendReviewSuccessMessage.visibility = VISIBLE

                    MotionToast.createColorToast(
                        context,
                        title = "Success",
                        message = "Review has been sent",
                        style = MotionToastStyle.SUCCESS,
                        position = Gravity.TOP,
                        duration = MotionToast.LONG_DURATION,
                        null
                    )
                }
            }

            isError.observe(context) {
                if (it) {
                    MotionToast.createColorToast(
                        context,
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

    private fun getUserToken(){

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

    private fun showSendReviewFormDialog() {
        if(!Helper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        else if (
            !Helper.isGPSEnabled(this) &&
            Helper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            Helper.infoDialog(
                this,
                "Your GPS is disabled, please enable it to use this feature"
            ).show()
            return
        }
        getMyCurrentLocation()

        val transaction = supportFragmentManager.beginTransaction()
        formReviewDialog.show(transaction, "ReviewFormDialog")
    }

    private fun getMyCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                (this),
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            reviewViewModel.setFetchingLocationState(true)
            if(latitude == null && longitude == null){
                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude

                        onLocationRetrieved(location.latitude, location.longitude)
                        Log.d("FETCH_CURRENT_LOCATION", "$location")
                    }
                    reviewViewModel.setFetchingLocationState(false)
                }.addOnFailureListener( this) { e ->
                    formReviewDialog.dismiss()
                    Helper.infoDialog(this, "Failed to get your location")
                    reviewViewModel.setFetchingLocationState(false)

                    Log.e("LocationUpdate", "Failed to get location: ${e.message}")
                }
            } else{
                reviewViewModel.setFetchingLocationState(false)
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

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

    override fun onTokenRetrieved(token: String) {
        this.token = token
        formReviewDialog.onTokenRetrieved(token)
    }

    override fun onLocationRetrieved(lat: Double, long: Double) {
        this.latitude = lat
        this.longitude = long
        formReviewDialog.onLocationRetrieved(lat, long)
    }

    override fun onPredictionIdRetrieved(id: String) {
        formReviewDialog.onPredictionIdRetrieved(id)
    }

    override fun onReviewSubmitted(predictionMethod: PredictionMethod) {
        formReviewDialog.onReviewSubmitted(predictionMethod)
    }
}