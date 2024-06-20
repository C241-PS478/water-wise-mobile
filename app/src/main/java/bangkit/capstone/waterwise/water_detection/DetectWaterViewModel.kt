package bangkit.capstone.waterwise.water_detection

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bangkit.capstone.waterwise.utils.Api
import bangkit.capstone.waterwise.utils.Const
import bangkit.capstone.waterwise.utils.Helper
import bangkit.capstone.waterwise.water_detection.machine_learning.PotabilityIotModel
import bangkit.capstone.waterwise.water_detection.machine_learning.WaterDetectionModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DetectWaterViewModel: ViewModel() {
    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> = _isSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isPhotoInvalid = MutableLiveData<Boolean>()
    val isPhotoInvalid: LiveData<Boolean> = _isPhotoInvalid

    private val _isDrinkable = MutableLiveData<Boolean>()
    val isDrinkable: LiveData<Boolean> = _isDrinkable

    private val _cleanlinessPercentage = MutableLiveData<Float>()
    val cleanlinessPercentage: LiveData<Float> = _cleanlinessPercentage

    private val waterPredictionService = Api.service(Service::class.java)

    private val _predictionResponse = MutableLiveData<PredictionResultRes>()
    val predictionResponse: LiveData<PredictionResultRes> = _predictionResponse

    private val _predictionByDataResponse = MutableLiveData<PredictionByDataRes>()
    val predictionByDataResponse: LiveData<PredictionByDataRes> = _predictionByDataResponse

    private val _isUsingLocalModel = MutableLiveData<Boolean>()

    fun detectWaterUsingModel(bitmap: Bitmap, waterDetectionModel: WaterDetectionModel) {
        _isLoading.value = true
        _isUsingLocalModel.value = true

        // classify the image
        try {
            val result = waterDetectionModel.classify(bitmap)
            val roundedResult = Helper.formatToDecimal(result[0])
            _isSuccess.value = true
            _cleanlinessPercentage.value = roundedResult

            Log.d("DetectWaterViewModel", "Result: ${result[0]}")
            determineDrinkable(result[0])
        } catch (e: Exception) {
            _isError.value = true
        }
        _isLoading.value = false
    }

    fun detectWaterByDataUsingModel(data: FloatArray, potabilityIotModel: PotabilityIotModel) {
        _isLoading.value = true
        _isUsingLocalModel.value = true

        // classify
        try {
            val result = potabilityIotModel.classify(data)
            val roundedResult = Helper.formatToDecimal(result[0])
            _isSuccess.value = true
            _cleanlinessPercentage.value = roundedResult

            Log.d("RESULT_BY_DATA", "Result: ${result[0]}")
            determineDrinkable(result[0])
        } catch (e: Exception) {
            _isError.value = true
        }

        _isLoading.value = false
    }

    fun predictQuality(context: Context, token: String, image: File) {
        setLoadingState(true)
        viewModelScope.launch {
            var fileFinal: File? = null

            withContext(Dispatchers.IO) {
                var compressedFile: File? = null
                var compressedFileSize = image.length()

                // Compress the file until its size is less than or equal to 1MB
                while (compressedFileSize > 1 * 1024 * 1024) {
                    compressedFile = withContext(Dispatchers.Default) {
                        Compressor.compress(context, image)
                    }
                    compressedFileSize = compressedFile.length()
                }

                fileFinal = compressedFile ?: image
            }

            val requestFile = fileFinal?.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", fileFinal?.name, requestFile!!)

            try {
                val response = waterPredictionService.predictQuality(token, body)
                if (response.isSuccessful) {
                    val prediction = response.body()?.data?.prediction
                    _isSuccess.value = true
                    _predictionResponse.value = response.body()!!.data
                    _cleanlinessPercentage.value = Helper.formatToDecimal(prediction?.toFloat()!!)

                    Log.d("RESULT_BY_DATA", "Result: $prediction")
                    determineDrinkable(prediction)
                } else if (response.code() == 400){
                    _isPhotoInvalid.value = true
                }   else {
                    _isError.value = true
                    Log.e("predictQuality", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _isError.value = true
                Log.e("predictQuality", "Error: $e")
            }

            setLoadingState(false)
        }
    }

    fun predictQualityByData(data: PredictionByDataReq, token: String){
        setLoadingState(true)
        viewModelScope.launch {
            try {
                val response = waterPredictionService.predictQualityByData(
                    token,
                    data.solids,
                    data.turbidity,
                    data.organicCarbon,
                    data.chloramines,
                    data.sulfate,
                    data.ph
                )
                if (response.isSuccessful) {
                    val prediction = response.body()?.data?.prediction
                    _isSuccess.value = true
                    _predictionByDataResponse.value = response.body()!!.data
                    _cleanlinessPercentage.value = Helper.formatToDecimal(prediction?.toFloat()!!)

                    Log.d("RESULT_BY_DATA", "Result: $prediction")
                    determineDrinkable(prediction)
                } else {
                    _isError.value = true
                    Log.e("predictQuality", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _isError.value = true
                Log.e("predictQuality", "Error: $e")
            }

            setLoadingState(false)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    private fun determineDrinkable(result: Number) {
        return if (result.toFloat() > Const.CLEAN_WATER_THRESHOLD) {
            _isDrinkable.value = true
        } else {
            _isDrinkable.value = false
        }
    }
}