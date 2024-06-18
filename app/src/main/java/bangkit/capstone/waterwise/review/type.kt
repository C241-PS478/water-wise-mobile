package bangkit.capstone.waterwise.review

object Types {
    data class Review(
        val id: Int,
        val description: String,
        val predictionByImage: PredictionByImage?,
        val predictionByData: PredictionByData?,
        val type: PredictionType,
        val createdAt: String
    )

    data class PredictionByImage(
        val id: Int,
        val imageUrl: String,
        val result: Float
    )

    data class PredictionByData(
        val id: Int,
        val solids: Float,
        val turbidity: Float,
        val carbon: Float,
        val chlorine: Float,
        val sulfate : Float,
        val ph: Float,
        val result: Float
    )

    enum class PredictionType {
        PREDICTION_BY_IMAGE,
        PREDICTION_BY_DATA
    }
}