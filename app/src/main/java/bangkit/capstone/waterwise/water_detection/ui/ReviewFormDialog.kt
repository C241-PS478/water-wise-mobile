package bangkit.capstone.waterwise.water_detection.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.DialogFragment
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.review.ReviewViewModel
import bangkit.capstone.waterwise.review.types.CreateReviewReq
import bangkit.capstone.waterwise.review.types.ReviewFormDialogListener
import bangkit.capstone.waterwise.water_detection.DetectWaterViewModel
import com.google.android.material.textfield.TextInputEditText

class ReviewFormDialog(
    private val reviewViewModel: ReviewViewModel,
    private val detectWaterViewModel: DetectWaterViewModel): DialogFragment(),
    ReviewFormDialogListener {

    private var sendReviewSubmitBtn: Button? = null
    private var sendReviewDescriptionInput: TextInputEditText? = null
    private var sendReviewLoader: LinearLayout? = null
    private var resultText: TextView? = null

    private var lat: Double? = null
    private var long: Double? = null
    private var predictionId: String? = null
    private var token: String? = null

    private var tokenListener: ReviewFormDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ReviewFormDialogListener) {
            tokenListener = context
        } else {
            throw RuntimeException("Activity must implement ReviewFormDialogListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.form_review_dialog, container, true)

        sendReviewSubmitBtn = view.findViewById(R.id.send_review_btn)
        sendReviewDescriptionInput = view.findViewById(R.id.description_input_text)
        sendReviewLoader = view.findViewById(R.id.review_form_loader)
        resultText = view.findViewById(R.id.detection_result)

        reviewViewModel.isFetchingLocation.observe(viewLifecycleOwner) {
            setFormReviewWhenFetchingLocation(it)
        }

        sendReviewSubmitBtn?.setOnClickListener {
            sendReview(
                lat!!,
                long!!,
                predictionId!!,
                token!!
            )
            dismiss()
        }

        detectWaterViewModel.isDrinkable.observe(this) {
            setFormReviewDialogResult(it)
        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(isCancelable)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes.windowAnimations = android.R.transition.fade
            setGravity(Gravity.CENTER)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        return dialog
    }

    override fun onTokenRetrieved(token: String) {
        this.token = token
    }

    override fun onLocationRetrieved(lat: Double, long: Double) {
        this.lat = lat
        this.long = long
    }

    override fun onPredictionIdRetrieved(id: String) {
        this.predictionId = id
    }

    private fun setFormReviewWhenFetchingLocation(isFetching: Boolean) {
        sendReviewLoader?.visibility = if(isFetching) VISIBLE else GONE
        sendReviewSubmitBtn?.visibility = if(isFetching) GONE else VISIBLE
        sendReviewDescriptionInput?.visibility = if(isFetching) GONE else VISIBLE
    }

    private fun getDescInput(): String {
        return sendReviewDescriptionInput?.text.toString()
    }

    private fun setFormReviewDialogResult(isDrinkable: Boolean) {
        if(isDrinkable) {
            resultText?.text = getString(R.string.result_drinkable)
            resultText?.setTextColor(getColor(requireActivity(), R.color.success_badge))
        } else {
            resultText?.text = getString(R.string.result_not_drinkable)
            resultText?.setTextColor(getColor(requireActivity(), R.color.error_badge))
        }
    }

    private fun sendReview(lat: Double, long: Double, predictionId: String, token: String) {
        val data =  CreateReviewReq(
            lat,
            long,
            getDescInput(),
            predictionId
        )
        reviewViewModel.createReview(data,token)
    }
}