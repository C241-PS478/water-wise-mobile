package bangkit.capstone.waterwise.water_detection

import com.google.gson.annotations.SerializedName
import java.io.File
data class PredictionRequest(
    val image: File
)

data class PredictionResponse(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val data : PredictionResultRes
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