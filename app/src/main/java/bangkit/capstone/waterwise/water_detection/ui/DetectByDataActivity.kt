package bangkit.capstone.waterwise.water_detection.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityDetectByDataBinding
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.DetectWaterViewModel
import bangkit.capstone.waterwise.water_detection.machine_learning.PotabilityIotModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle


class DetectByDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetectByDataBinding
    private val detectWaterViewModel: DetectWaterViewModel by viewModels()
    private val loadingDialog by lazy { Helper.loadingDialog(this) }

    private lateinit var inputFields: List<CustomInputText>
    private lateinit var potabilityIotModel: PotabilityIotModel
    private var data: FloatArray? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetectByDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val context = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        potabilityIotModel = PotabilityIotModel(context)

        inputFields = getAllInputTextFields()

        with(binding) {
            sendReviewBtnResult.setOnClickListener {
                showSendReviewFormDialog(context)
            }

            connectIotBtn.setOnClickListener {
                Helper.infoDialog(context, "This feature is not available yet").show()
            }

            identifyBtn.setOnClickListener {
                data = getAllInputData()
                identify(data!!)
            }

            btnBack.setOnClickListener {
                finish()
            }

            detectWaterViewModel.apply {
                isLoading.observe(context) {
                    if (it) {
                        loadingDialog.show()
                    } else {
                        loadingDialog.dismiss()
                    }
                }

                isSuccess.observe(context) {
                    if (it) {
                        identifyBtn.visibility = View.GONE
                        connectIotBtn.visibility = View.GONE
                        sendReviewBtnResult.visibility = View.VISIBLE
                        result.visibility = View.VISIBLE
                        cleanlinessLabel.visibility = View.VISIBLE
                        cleanlinessPercentageResult.visibility = View.VISIBLE
                    }
                }

                isDrinkable.observe(context) {
                    setBadgeForResult(it)
                }

                cleanlinessPercentage.observe(context) {
                    setCleanlinessPercentage(it * 100)
                }

                isSendReviewSuccess.observe(context) {
                    sendReviewBtnResult.isEnabled = false
                    sendReviewBtnResult.isClickable = false

                    if (it) {
                        MotionToast.createColorToast(
                            context,
                            title = "Success",
                            message = "Review has been uploaded",
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
                            message = "Something went wrong",
                            style = MotionToastStyle.ERROR,
                            position = Gravity.TOP,
                            duration = MotionToast.LONG_DURATION,
                            null
                        )
                    }
                }

                val drinkableBackground = getDrawableRes(R.drawable.success_badge)
                val notDrinkableBackground = getDrawableRes(R.drawable.error_badge)
                isDrinkable.observe(context) {
                    result.text = if(it) "Drinkable" else "Not Drinkable"
                    result.background = if(it) drinkableBackground else notDrinkableBackground
                }
            }
        }
    }

    private fun showSendReviewFormDialog(
        context: Context,
    ) {
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
        } else {
            getMyCurrentLocation()
        }

        val reviewDialog = Helper.dialogBuilder(context, R.layout.form_review_dialog, true)
        val resultText = reviewDialog.findViewById<TextView>(R.id.detection_result)
        val detectionMethod = reviewDialog.findViewById<TextView>(R.id.detection_method)
        detectionMethod.text = "Water detected by data"

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
                it.inputTextKlorin,
                it.inputTextKekerasan,
                it.inputTextKarbon,
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

    private fun identify(data: FloatArray){
        val isAnyEmpty = checkAllEmptyFields()

        if (isAnyEmpty) {
            Helper.infoDialog(this, "Please fill all fields").show()
        } else {
            detectWaterViewModel.detectWaterByDataUsingModel(data, potabilityIotModel)
            disableAllInputFields()
        }
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

    private fun setFormReviewDialogResult(isDrinkable: Boolean, textView: TextView) {
        if(isDrinkable) {
            textView.text = getString(R.string.result_drinkable)
            textView.setTextColor(getColor(R.color.success_badge))
        } else {
            textView.text = getString(R.string.result_not_drinkable)
            textView.setTextColor(getColor(R.color.error_badge))
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
}