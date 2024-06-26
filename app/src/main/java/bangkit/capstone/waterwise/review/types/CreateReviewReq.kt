package bangkit.capstone.waterwise.review.types

data class CreateReviewReq(
    val lat: Double,
    val long: Double,
    val description: String? = null,
    val predictionId: String
)

data class CreateReviewByDataReq(
    val lat: Double,
    val long: Double,
    val description: String? = null,
    val predictionIotId: String
)