package bangkit.capstone.waterwise.review.types

interface ReviewFormDialogListener {
    fun onTokenRetrieved(token: String)
    fun onLocationRetrieved(lat: Double, long: Double)
    fun onPredictionIdRetrieved(id: String)
}