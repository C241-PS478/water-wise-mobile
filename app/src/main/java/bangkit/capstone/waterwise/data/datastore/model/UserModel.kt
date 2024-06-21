package bangkit.capstone.waterwise.data.datastore.model

data class UserModel(

    val name: String,
    val userId: String,
    val token: String,
    val isLogin: Boolean = false
)
