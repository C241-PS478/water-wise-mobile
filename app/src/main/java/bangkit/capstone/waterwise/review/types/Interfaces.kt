package bangkit.capstone.waterwise.review.types

import bangkit.capstone.waterwise.water_detection.PredictionMethod

interface ReviewFormDialogListener {
    fun onTokenRetrieved(token: String)
    fun onLocationRetrieved(lat: Double, long: Double)
    fun onPredictionIdRetrieved(id: String)
    fun onReviewSubmitted(predictionMethod: PredictionMethod)
}