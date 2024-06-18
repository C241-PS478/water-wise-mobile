package bangkit.capstone.waterwise.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData

class NewsViewModel : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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
}