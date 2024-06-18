package bangkit.capstone.waterwise.data.datastore.model

data class UserModel(
    val id: String,
    val firebaseId: String?,
    val username: String,
    val email: String,
    val name: String,
    val isAdmin: Boolean,
    val phoneNumber: String
)
