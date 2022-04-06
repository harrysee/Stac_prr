package kr.hs.emirim.w2015.stac_prr.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Model.HomeModel
import kr.hs.emirim.w2015.stac_prr.Repository.PlantRepository

class HomeViewModel : ViewModel() {
    private var plantList = MutableLiveData<ArrayList<HomeModel>>()
    private val plantRepository by lazy { PlantRepository }
    
    fun getAllPlant(): MutableLiveData<ArrayList<HomeModel>> {
        viewModelScope.launch(Dispatchers.IO) {
            plantList= plantRepository.getPlantListLiveData()
        }
        return plantList
    }

}