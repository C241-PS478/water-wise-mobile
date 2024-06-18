package bangkit.capstone.waterwise.news

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("totalResults")
    val totalResults: Int,

    @field:SerializedName("articles")
    val articles: List<News>
)

data class Source(
    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: String? = null
)

data class News(
    @field:SerializedName("publishedAt")
    val publishedAt: String? = null,

    @field:SerializedName("author")
    val author: String? = null,

    @field:SerializedName("urlToImage")
    val urlToImage: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("source")
    val source: Source? = null,

    @field:SerializedName("title")
    val title: String?,

    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("content")
    val content: String? = null
)

const val QUERY_SEARCH_PARAM = "water"
const val PAGE_SIZE = 10
const val MAX_PAGE_SIZE = 100