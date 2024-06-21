package bangkit.capstone.waterwise.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import bangkit.capstone.waterwise.utils.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsViewModel : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listNews = MutableLiveData<List<News>>()
    val listNews: LiveData<List<News>> = _listNews

    private val apiService = Api.service(Service::class.java)

    fun findAllPaginated(): LiveData<PagingData<News>> {
        _isLoading.value = true
        val data =  Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                maxSize = MAX_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NewsPagingSource() }
        ).liveData

        _isLoading.value = false
        return data
    }

    fun findSome(token: String) {
        val client = apiService.findSome(token)
        _isLoading.value = true
        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful) {
                    _listNews.value = responseBody?.articles
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }
}