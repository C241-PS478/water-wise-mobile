package bangkit.capstone.waterwise.datastore.model

data class UserModel(
    val token: String,
    val name: String,
    val userId: String,
    val isLogin: Boolean = false
)