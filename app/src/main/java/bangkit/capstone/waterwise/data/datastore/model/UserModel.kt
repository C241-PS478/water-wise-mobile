package bangkit.capstone.waterwise.data.datastore.model

data class UserModel(
    val userId: String,
    val firebaseId: String?,
    val email: String,
    val name: String,
    val phoneNumber: String?,
    val address: String?,
    val token: String
)
