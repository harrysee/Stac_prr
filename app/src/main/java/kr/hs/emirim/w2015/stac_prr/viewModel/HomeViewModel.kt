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
    init {
        // 생성 로직
        // 식물데이터 HomeModel로 가져오기
        viewModelScope.launch {
            plantList= plantRepository.getPlantListLiveData()
        }
    }

    fun getAllPlant(): MutableLiveData<ArrayList<HomeModel>> {
        return plantList
    }

}