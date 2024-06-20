package bangkit.capstone.waterwise.review

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bangkit.capstone.waterwise.review.types.CreateReviewByDataReq
import bangkit.capstone.waterwise.review.types.CreateReviewReq
import bangkit.capstone.waterwise.review.types.CreateReviewResponse
import bangkit.capstone.waterwise.utils.Api
import kotlinx.coroutines.launch

class ReviewViewModel: ViewModel() {
    private val reviewService = Api.service(Service::class.java)

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> = _isSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFetchingLocation = MutableLiveData<Boolean>()
    val isFetchingLocation: LiveData<Boolean> = _isFetchingLocation

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _reviewResponse = MutableLiveData<CreateReviewResponse>()

    fun createReview(data: CreateReviewReq, token: String) {
        setLoadingState(true)

        val predictionId = data.predictionId
        val lat = data.lat
        val long = data.long
        val description = data.description

        viewModelScope.launch {
            try {
                val response = reviewService.createReview(
                    token,
                    predictionId,
                    lat,
                    long,
                    description
                )
                if (response.isSuccessful) {
                    _isSuccess.value = true
                    _reviewResponse.value = response.body()
                } else {
                    _isError.value = true
                    Log.e("createReview", "Err: ${response.message()}")
                }
            } catch (e: Exception) {
                _isError.value = true
                Log.e("createReview", "Err: ${e.message}")
            }

            setLoadingState(false)
        }
    }

    fun createReviewFromPredictionByData(data: CreateReviewByDataReq, token: String) {
        setLoadingState(true)
        val predictionIotId = data.predictionIotId
        val lat = data.lat
        val long = data.long
        val description = data.description

        viewModelScope.launch {
            try {
                val response = reviewService.createReview(
                    token,
                    predictionIotId,
                    lat,
                    long,
                    description
                )
                if (response.isSuccessful) {
                    _isSuccess.value = true
                    _reviewResponse.value = response.body()
                } else {
                    _isError.value = true
                    Log.e("createReview", "Err: ${response.message()}")
                }
            } catch (e: Exception) {
                _isError.value = true
                Log.e("createReview", "Err: ${e.message}")
            }

            setLoadingState(false)
        }
    }

    fun setFetchingLocationState(isFetching: Boolean) {
        _isFetchingLocation.value = isFetching
    }

    private fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}