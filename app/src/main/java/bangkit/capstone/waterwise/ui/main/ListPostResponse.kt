package bangkit.capstone.waterwise.ui.main

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ListPostResponse(

    @field:SerializedName("listPost")
    val listPost: List<ListPostItem> = emptyList(),

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

@Parcelize
@Entity(tableName = "list_post")
data class ListPostItem(

    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,

    @field:SerializedName("dateCreated")
    val dateCreated: String? = null,

    @field:SerializedName("author/name")
    val authorName: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("longitude")
    val lon: Double? = null,

    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("latitude")
    val lat: Double? = null
) : Parcelable