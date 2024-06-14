package bangkit.capstone.waterwise.ui.news
import com.google.gson.annotations.SerializedName

data class Types(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String
)

data class News(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("type")
    val type: Types
)
