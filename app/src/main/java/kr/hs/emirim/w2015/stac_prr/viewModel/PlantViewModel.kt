package kr.hs.emirim.w2015.stac_prr.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Model.PlantModel
import kr.hs.emirim.w2015.stac_prr.Repository.JournalRepository
import kr.hs.emirim.w2015.stac_prr.Repository.PlanRepository
import kr.hs.emirim.w2015.stac_prr.Repository.PlantRepository

class PlantViewModel :ViewModel(){
    var plantLiveData = MutableLiveData<PlantModel>()
    private var imgs = MutableLiveData<ArrayList<String?>>()
    val plantRepository = PlantRepository
    val journalRepository = JournalRepository
    val planRepository = PlanRepository

    fun getPlant(docId : String): MutableLiveData<PlantModel> {
        viewModelScope.launch{
            plantLiveData = plantRepository.getPlantLiveData(docId)
        }
        Log.i("TAG", "getPlant: 정보 보내기"+plantLiveData)
        return plantLiveData
    }

    fun getJournalImgs(name:String?): MutableLiveData<ArrayList<String?>> {
        viewModelScope.launch {
            imgs = name?.let { journalRepository.getJournalImg(it) }!!
        }
        return imgs
    }

    fun deleteAll(name:String, docId: String?,imgUri:String?){
        // 식물삭제
        viewModelScope.launch {
            val delplan = async { planRepository.deletePlan(name)  }
            val delJournal = async { journalRepository.deleteJournal(name)  }
            val delplant =async { plantRepository.deletePlant(docId, imgUri)  }

            delplan.await()
            delJournal.await()
            delplant.await()
        }
    }
}