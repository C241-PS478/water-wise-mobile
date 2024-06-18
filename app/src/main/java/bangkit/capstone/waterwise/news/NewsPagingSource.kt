package bangkit.capstone.waterwise.news

import androidx.paging.PagingSource
import androidx.paging.PagingState
import bangkit.capstone.waterwise.BuildConfig
import bangkit.capstone.waterwise.utils.Api

class NewsPagingSource: PagingSource<Int, News>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, News> {
        return try {
            val position = params.key ?: 1
            val newsService = Api.service(Service::class.java)
            val apiKey = BuildConfig.NEWS_API_KEY

            val response = newsService.findAll(
                "Bearer $apiKey",
                QUERY_SEARCH_PARAM,
                position,
                PAGE_SIZE
            )

            LoadResult.Page(
                data = response.articles,
                prevKey = if (position == 1) null else (position - 1),
                nextKey = if (position == response.totalResults) null else (position + 1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, News>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}