package bangkit.capstone.waterwise.ui.main

import androidx.lifecycle.ViewModel
import bangkit.capstone.waterwise.data.datastore.repository.UserRepository

class MapsViewModel(private val userRepository: UserRepository): ViewModel() {

    fun getPostLocation() = userRepository.getPostWithLocation()

}