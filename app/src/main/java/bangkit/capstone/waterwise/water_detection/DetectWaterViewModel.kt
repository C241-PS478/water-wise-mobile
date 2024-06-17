package bangkit.capstone.waterwise.water_detection

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.capstone.waterwise.utils.Const
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.machine_learning.PotabilityIotModel
import bangkit.capstone.waterwise.water_detection.machine_learning.WaterDetectionModel

class DetectWaterViewModel: ViewModel() {
    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> = _isSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isDrinkable = MutableLiveData<Boolean>()
    val isDrinkable: LiveData<Boolean> = _isDrinkable

    private val _isSendReviewSuccess = MutableLiveData<Boolean>()
    val isSendReviewSuccess: LiveData<Boolean> = _isSendReviewSuccess

    private val _cleanlinessPercentage = MutableLiveData<Float>()
    val cleanlinessPercentage: LiveData<Float> = _cleanlinessPercentage

    fun detectWaterUsingModel(bitmap: Bitmap, waterDetectionModel: WaterDetectionModel) {
        _isLoading.value = true

        // classify the image
        try {
            val result = waterDetectionModel.classify(bitmap)
            val roundedResult = Helper.roundUp(result[0])
            _isSuccess.value = true
            _cleanlinessPercentage.value = roundedResult.toFloat()

            Log.d("DetectWaterViewModel", "Result: ${result[0]}")
            determineDrinkable(result[0])
        } catch (e: Exception) {
            _isError.value = true
        }
        _isLoading.value = false
    }

    fun detectWaterByDataUsingModel(data: FloatArray, potabilityIotModel: PotabilityIotModel) {
        _isLoading.value = true

        // classify
        try {
            val result = potabilityIotModel.classify(data)
            val roundedResult = Helper.roundUp(result[0])
            _isSuccess.value = true
            _cleanlinessPercentage.value = roundedResult.toFloat()

            Log.d("RESULT_BY_DATA", "Result: ${result[0]}")
            determineDrinkable(result[0])
        } catch (e: Exception) {
            _isError.value = true
        }

        _isLoading.value = false
    }

    fun sendReview() {
        _isLoading.value = true

        Handler(Looper.getMainLooper()).postDelayed({
            _isLoading.value = false
            _isSendReviewSuccess.value = true
        }, 3000)
    }

    fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    private fun determineDrinkable(result: Float) {
        return if (result > Const.CLEAN_WATER_THRESHOLD) {
            _isDrinkable.value = true
        } else {
            _isDrinkable.value = false
        }
    }
}