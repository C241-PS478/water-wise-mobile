package bangkit.capstone.waterwise.water_detection

import com.google.gson.annotations.SerializedName

data class PredictionResponse<T>(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val data : T
)

data class PredictionResultRes (
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("authorId")
    val authorId: String,

    @field:SerializedName("imageUrl")
    val imageUrl: String,

    @field:SerializedName("prediction")
    val prediction: Double,

    @field:SerializedName("dateCreated")
    val dateCreated: String
)


data class PredictionByDataReq(
    @field:SerializedName("solids")
    val solids: Float,

    @field:SerializedName("turbidity")
    val turbidity: Float,

    @field:SerializedName("organic_carbon")
    val organicCarbon: Float,

    @field:SerializedName("chloramines")
    val chloramines: Float,

    @field:SerializedName("sulfate")
    val sulfate: Float,

    @field:SerializedName("ph")
    val ph: Float
)

data class PredictionByDataRes(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("authorId")
    val authorId: String,

    @field:SerializedName("prediction")
    val prediction: Double,

    @field:SerializedName("solids")
    val solids: Float,

    @field:SerializedName("turbidity")
    val turbidity: Float,

    @field:SerializedName("organic_carbon")
    val organicCarbon: Float,

    @field:SerializedName("chloramines")
    val chloramines: Float,

    @field:SerializedName("sulfate")
    val sulfate: Float,

    @field:SerializedName("ph")
    val ph: Float
)

enum class PredictionMethod {
    BY_IMAGE,
    BY_DATA
}