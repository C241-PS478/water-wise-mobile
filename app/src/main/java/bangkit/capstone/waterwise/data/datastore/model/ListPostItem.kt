package bangkit.capstone.waterwise.data.datastore.model

data class ListPostItem(
    val id: String,
    val author: Author?,
    val dateCreated: String,
    val dateModified: String,
    val description: String,
    val addressId: String,
    val predictionId: String,
    val address: Address?,
    val lat: Double?,
    val lon: Double?,
    var prediction: Float? = null,
    var predictionImageUrl: String? = null
)

data class Author(
    val id: String,
    val firebaseId: String?,
    val username: String,
    val password: String?,
    val email: String,
    val name: String,
    val isAdmin: Boolean,
    val phoneNumber: String
)

data class Address(
    val id: String,
    val longitude: Double,
    val latitude: Double,
    val country: String,
    val province: String,
    val city: String,
    val district: String,
    val village: String,
    val address: String,
    val userId: String?
)
