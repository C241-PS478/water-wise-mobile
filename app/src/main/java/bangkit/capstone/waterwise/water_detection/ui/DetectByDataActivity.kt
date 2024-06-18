package bangkit.capstone.waterwise.water_detection.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityDetectByDataBinding
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.utils.Helper.infoDialog
import bangkit.capstone.waterwise.water_detection.DetectWaterViewModel
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle


class DetectByDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetectByDataBinding
    private val detectWaterViewModel: DetectWaterViewModel by viewModels()
    private val loadingDialog by lazy { Helper.loadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetectByDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val context = this

        with(binding) {
            sendReviewBtnResult.setOnClickListener {
                showSendReviewFormDialog(context)
            }

            connectIotBtn.setOnClickListener {
                infoDialog(context, "This feature is not available yet").show()
            }

            identifyBtn.setOnClickListener {
                detectWaterViewModel.detectWaterByData()
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
                    }
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

    private fun getDrawableRes(drawable: Int): Drawable? {
        return ResourcesCompat.getDrawable(resources, drawable, null)
    }

}