package bangkit.capstone.waterwise.water_detection

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetectWaterViewModel: ViewModel() {
    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> = _isSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isDrinkable = MutableLiveData<Boolean>()
    val isDrinkable: LiveData<Boolean> = _isDrinkable

    fun detectWater() {
        _isLoading.value = true
        _isDrinkable.value = true

        // set delay for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            _isLoading.value = false
            _isSuccess.value = true
        }, 3000)
    }
}