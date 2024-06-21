package bangkit.capstone.waterwise.water_detection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bangkit.capstone.waterwise.data.datastore.pref.UserPreference

class ViewModelFactory(private val pref: UserPreference) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetectWaterViewModel::class.java)) {
            return DetectWaterViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}