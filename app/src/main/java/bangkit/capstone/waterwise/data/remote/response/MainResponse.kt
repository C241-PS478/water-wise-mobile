package bangkit.capstone.waterwise.remote.response

import com.google.gson.annotations.SerializedName

data class MainResponse(

	@field:SerializedName("avatar_url")
	val avatarUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

)

