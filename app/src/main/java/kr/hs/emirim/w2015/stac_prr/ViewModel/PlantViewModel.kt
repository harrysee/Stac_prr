package kr.hs.emirim.w2015.stac_prr.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Model.PlantModel
import kr.hs.emirim.w2015.stac_prr.Repository.JournalRepository
import kr.hs.emirim.w2015.stac_prr.Repository.PlanRepository
import kr.hs.emirim.w2015.stac_prr.Repository.PlantRepository

class PlantViewModel :ViewModel(){
    private var plantLiveData = MutableLiveData<PlantModel>()
    private var imgs = MutableLiveData<ArrayList<String?>>()
    val plantRepository = PlantRepository
    val journalRepository = JournalRepository
    val planRepository = PlanRepository

    fun getPlant(docId : String): MutableLiveData<PlantModel> {
        viewModelScope.launch {
            plantLiveData = plantRepository.getPlantLiveData(docId)
        }
        return plantLiveData
    }

    fun getJournalImgs(name:String?): MutableLiveData<ArrayList<String?>> {
        viewModelScope.launch {
            imgs = name?.let { journalRepository.getJournalImg(it) }!!
        }
        return imgs
    }

    fun deleteAll(name:String, docId: String?){
        // 식물삭제
        viewModelScope.launch {
            plantRepository.deletePlant(docId)
            planRepository.deletePlan(name)
            journalRepository.deleteJournal(name)
        }
    }
}