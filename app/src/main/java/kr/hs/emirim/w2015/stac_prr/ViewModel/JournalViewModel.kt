package kr.hs.emirim.w2015.stac_prr.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Model.JournalModel
import kr.hs.emirim.w2015.stac_prr.Repository.JournalRepository
import kr.hs.emirim.w2015.stac_prr.Repository.PlantRepository

class JournalViewModel : ViewModel(){
    val plantNames = MutableLiveData<ArrayList<String>>()
    var allJournals = MutableLiveData<ArrayList<JournalModel>>()
    var journals = MutableLiveData<ArrayList<JournalModel>>()
    var isComplate = MutableLiveData<Boolean>()
    val plantRep = PlantRepository
    val journalRep = JournalRepository
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val auth = Firebase.auth
    
    // 식물이름들 가져오기
    fun getPlantName(): MutableLiveData<ArrayList<String>> {
        viewModelScope.launch {
            plantRep.getNames()
        }
        return plantNames
    }
    
    // 전체일지 보여주기 - 오름/내림
    fun getAllJournals(sorted : Boolean): MutableLiveData<ArrayList<JournalModel>> {
        viewModelScope.launch {
            allJournals = journalRep.getJournalList(sorted)
        }
        return allJournals
    }
    
    // 선택한 일지 가져오긴 - 오름/내림 : name
    fun getJournals(sorted: Boolean, name:String): MutableLiveData<ArrayList<JournalModel>> {
        viewModelScope.launch {
            journals = journalRep.getPlantJournal(sorted, name)
        }
        return journals
    }

    // 일지 삭제하기 : docId
    fun deleteJournal(docId: String): MutableLiveData<Boolean> {
        viewModelScope.launch {
            val isSuccess = journalRep.deleteJournalItem(docId)
            if(isSuccess.value == true){
                isComplate.postValue(true)
            }else{
                isComplate.postValue(false)
            }
        }
        return isComplate
    }
}