package bangkit.capstone.waterwise.water_detection.ui

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityDetectResultBinding
import bangkit.capstone.waterwise.utils.CustomToast
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.DetectWaterViewModel
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class DetectResult : AppCompatActivity() {
    private lateinit var binding : ActivityDetectResultBinding
    private lateinit var customToast : CustomToast

    private val loadingDialog by lazy { Helper.loadingDialog(this) }

    private val detectWaterViewModel: DetectWaterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        customToast = CustomToast(this)

        with(binding){
            sendReviewBtnResult.setOnClickListener {
                showSendReviewFormDialog(this@DetectResult)
            }

            btnBack.setOnClickListener {
                finish()
            }
        }

        detectWaterViewModel.apply {
            isLoading.observe(this@DetectResult) {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }

            isSuccess.observe(this@DetectResult) {
//                if (it) {
//                    binding.sendReviewBtnResult.isEnabled = false
//                    binding.sendReviewBtnResult.isClickable = false
//
////                    customToast.apply {
////                        setMessage("Review has been uploaded")
////                        setGravity(yOffset = 30)
////                        show()
////                    }
//
//
//                }
            }

            isSendReviewSuccess.observe(this@DetectResult) {
                if (it) {
                    binding.sendReviewBtnResult.isEnabled = false
                    binding.sendReviewBtnResult.isClickable = false

                    MotionToast.createColorToast(
                        this@DetectResult,
                        title = "Success",
                        message = "Review has been uploaded",
                        style = MotionToastStyle.SUCCESS,
                        position = Gravity.TOP,
                        duration = MotionToast.LONG_DURATION,
                        null
                    )
                }
            }

            isError.observe(this@DetectResult) {
                if (it) {
                    customToast.apply {
                        setMessage("Failed to upload review")
                        setGravity(yOffset = 30)
                        show()
                    }
                }
            }
        }
    }

    fun showSendReviewFormDialog(
        context: Context,
    ) {
        val reviewDialog = Helper.dialogBuilder(context, R.layout.form_review_dialog, true)
        val resultText = reviewDialog.findViewById<TextView>(R.id.detection_result)

        resultText.text = getString(R.string.result_drinkable)
        resultText.setTextColor(getColor(R.color.toast_success))

        val submitBtn = reviewDialog.findViewById<Button>(R.id.send_review_btn)
        submitBtn.setOnClickListener {
            detectWaterViewModel.sendReview()
            reviewDialog.dismiss()
        }

        reviewDialog.show()
    }

    private fun getFont(font: Int): Typeface?{
        return ResourcesCompat.getFont(this, font)
    }
}