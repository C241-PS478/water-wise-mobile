package bangkit.capstone.waterwise.review.types

import com.google.gson.annotations.SerializedName

data class CreateReviewResponse(

	@field:SerializedName("data")
	val data: Data? = null
)

data class Data(

	@field:SerializedName("dateCreated")
	val dateCreated: String? = null,

	@field:SerializedName("address")
	val address: Address? = null,

	@field:SerializedName("author")
	val author: Author? = null,

	@field:SerializedName("prediction")
	val prediction: Prediction? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("dateModified")
	val dateModified: Any? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("authorId")
	val authorId: String? = null,

	@field:SerializedName("predictionId")
	val predictionId: String? = null,

	@field:SerializedName("addressId")
	val addressId: String? = null
)

data class Author(

	@field:SerializedName("password")
	val password: Any? = null,

	@field:SerializedName("phoneNumber")
	val phoneNumber: String? = null,

	@field:SerializedName("firebaseId")
	val firebaseId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("isAdmin")
	val isAdmin: Boolean? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)

data class Address(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("province")
	val province: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("latitude")
	val latitude: Any? = null,

	@field:SerializedName("district")
	val district: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("village")
	val village: String? = null,

	@field:SerializedName("userId")
	val userId: Any? = null,

	@field:SerializedName("longitude")
	val longitude: Any? = null
)

data class Prediction(

	@field:SerializedName("dateCreated")
	val dateCreated: String? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: String? = null,

	@field:SerializedName("prediction")
	val prediction: Any? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("authorId")
	val authorId: String? = null
)
