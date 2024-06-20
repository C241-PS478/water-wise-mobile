package bangkit.capstone.waterwise.water_detection.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
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
import androidx.core.content.res.ResourcesCompat
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityDetectByDataBinding
import bangkit.capstone.waterwise.review.ReviewViewModel
import bangkit.capstone.waterwise.review.types.ReviewFormDialogListener
import bangkit.capstone.waterwise.utils.Const
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.DetectWaterViewModel
import bangkit.capstone.waterwise.water_detection.PredictionByDataReq
import bangkit.capstone.waterwise.water_detection.PredictionMethod
import bangkit.capstone.waterwise.water_detection.machine_learning.PotabilityIotModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle


class DetectByDataActivity : AppCompatActivity(), ReviewFormDialogListener {
    private lateinit var binding: ActivityDetectByDataBinding
    private val detectWaterViewModel: DetectWaterViewModel by viewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()

    private val loadingDialog by lazy { Helper.loadingDialog(this) }

    private lateinit var inputFields: List<CustomInputText>
    private lateinit var potabilityIotModel: PotabilityIotModel
    private var data: FloatArray? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    private var token: String = "Bearer ${Const.ACCESS_TOKEN}"

    private lateinit var formReviewDialog: ReviewFormDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetectByDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        formReviewDialog = ReviewFormDialog(reviewViewModel, detectWaterViewModel)
        onReviewSubmitted(PredictionMethod.BY_DATA)

        val context = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        potabilityIotModel = PotabilityIotModel(context)

        inputFields = getAllInputTextFields()

        onTokenRetrieved(token)
        with(binding) {
            sendReviewBtnResult.setOnClickListener {
                showSendReviewFormDialog()
            }

            connectIotBtn.setOnClickListener {
                Helper.infoDialog(context, "This feature is not available yet").show()
            }

            identifyBtn.setOnClickListener {
                if (Helper.isHasInternetConnection(context)) {
                    predictQualityByData()
                } else{
                    identifyUsingLocalModel()
                }
            }

            btnTryAgain.setOnClickListener {
                if (Helper.isHasInternetConnection(context)) {
                    predictQualityByData()
                } else{
                    identifyUsingLocalModel()
                }
            }

            btnBack.setOnClickListener {
                finish()
            }

            detectWaterViewModel.apply {
                isLoading.observe(context) {
                    loaderContainer.visibility = if (it) VISIBLE else GONE
                    if (it) errorMessageContainer.visibility = GONE
                }

                isSuccess.observe(context) {
                    if (it) {
                        identifyBtn.visibility = GONE
                        connectIotBtn.visibility = GONE
                        sendReviewBtnResult.visibility = VISIBLE
                        result.visibility = VISIBLE
                        cleanlinessLabel.visibility = VISIBLE
                        cleanlinessPercentageResult.visibility = VISIBLE
                        errorMessageContainer.visibility = GONE
                    }
                }

                isDrinkable.observe(context) {
                    setBadgeForResult(it)
                }

                cleanlinessPercentage.observe(context) {
                    setCleanlinessPercentage(it * 100)
                }

                isError.observe(context) {
                    if (it) {
                        errorMessageContainer.visibility = VISIBLE
                    }
                }

                val drinkableBackground = getDrawableRes(R.drawable.success_badge)
                val notDrinkableBackground = getDrawableRes(R.drawable.error_badge)
                isDrinkable.observe(context) {
                    result.text = if(it) "Drinkable" else "Not Drinkable"
                    result.background = if(it) drinkableBackground else notDrinkableBackground
                }

                predictionByDataResponse.observe(context) {
                    onPredictionIdRetrieved(it.id)
                }
            }

            reviewViewModel.apply {
                isLoading.observe(context) {
                    if (it) loadingDialog.show() else loadingDialog.dismiss()
                }

                isSuccess.observe(context) {
                    sendReviewBtnResult.visibility = GONE
                    sendReviewSuccessMessage.visibility = VISIBLE

                    if (it) {
                        MotionToast.createColorToast(
                            context,
                            title = "Success",
                            message = "Your review has been uploaded",
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
                            message = "Failed to send a review",
                            style = MotionToastStyle.ERROR,
                            position = Gravity.TOP,
                            duration = MotionToast.LONG_DURATION,
                            null
                        )
                    }
                }
            }
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

    private fun predictQualityByData() {
        if(checkAllEmptyFields()){
            Helper.infoDialog(this, "Please fill all fields").show()
            return
        }

        data = getAllInputData()
        require(data?.size == 6) { "Expected array of at least size 6, but got ${data?.size}" }

        val (
            solids,
            turbidity,
            organicCarbon,
            chloramines,
            sulfate,
            ph
        ) = data!!

        detectWaterViewModel.predictQualityByData(
            PredictionByDataReq(
                solids,
                turbidity,
                organicCarbon,
                chloramines,
                sulfate,
                ph
            ),
            token
        )

        disableAllInputFields()
    }

    private fun getDrawableRes(drawable: Int): Drawable? {
        return ResourcesCompat.getDrawable(resources, drawable, null)
    }

    private fun checkAllEmptyFields():Boolean {
        for (inputText in inputFields) {
            if(inputText.isEditTextEmpty()) {
                return true
            }
        }
        return false
    }

    private fun getAllInputTextFields(): List<CustomInputText> {
        val customInputTexts: List<CustomInputText>
        binding.let {
            customInputTexts = listOf(
                it.inputTextKepadatan,
                it.inputTextKekeruhan,
                it.inputTextKarbon,
                it.inputTextKlorin,
                it.inputTextSulfat,
                it.inputTextKeasaman,
            )
        }

        return customInputTexts
    }

    private fun disableAllInputFields() {
        for (inputText in inputFields) {
            inputText.setReadOnly()
        }
    }

    private fun getAllInputData(): FloatArray {
        val data = FloatArray(6)
        for ((index, inputText) in inputFields.withIndex()) {
            data[index] = inputText.getText().toFloat()
        }
        return data
    }

    private fun identifyUsingLocalModel(){
        if(checkAllEmptyFields()){
            Helper.infoDialog(this, "Please fill all fields").show()
            return
        }

        this.data = getAllInputData()
        require(data?.size == 6) { "Expected array of at least size 6, but got ${data?.size}" }

        detectWaterViewModel.detectWaterByDataUsingModel(data!!, potabilityIotModel)
        disableAllInputFields()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBadgeForResult(isDrinkable: Boolean) {
        binding.apply {
            result.text = if (isDrinkable) getString(R.string.result_drinkable) else getString(R.string.result_not_drinkable)
            result.background = if (isDrinkable) getDrawable(R.drawable.success_badge) else getDrawable(R.drawable.error_badge)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCleanlinessPercentage(value: Number) {
        binding.cleanlinessPercentageResult.text = "$value%"
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

    private fun getMyCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                (this),
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            reviewViewModel.setFetchingLocationState(true)
            if(latitude == null && longitude == null){
                reviewViewModel.setFetchingLocationState(true)
                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).
                addOnSuccessListener { location ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        Log.d("FETCH_CURRENT_LOCATION", "$location")
                    }

                    onLocationRetrieved(latitude!!, longitude!!)
                    reviewViewModel.setFetchingLocationState(false)
                }.
                addOnFailureListener( this) { e ->
                    reviewViewModel.setFetchingLocationState(false)
                    Helper.infoDialog(this, "Failed to get your location")
                    Log.e("LocationUpdate", "Failed to get location: ${e.message}")
                }
            } else {
                onLocationRetrieved(this.latitude!!, this.longitude!!)
                reviewViewModel.setFetchingLocationState(false)
            }

        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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

private operator fun FloatArray.component6(): Float {
    return this[5]
}
