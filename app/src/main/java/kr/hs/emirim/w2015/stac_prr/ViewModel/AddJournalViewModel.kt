package kr.hs.emirim.w2015.stac_prr.ViewModel

import android.net.Uri
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

class AddJournalViewModel : ViewModel() {
    var journals = MutableLiveData<JournalModel>()
    var plantNames = MutableLiveData<ArrayList<String>>()
    var isComplate = MutableLiveData<Boolean>()
    val plantRep = PlantRepository
    val journalRep = JournalRepository
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val auth = Firebase.auth

    // 식물이름들 가져오기
    fun getPlantName(): MutableLiveData<ArrayList<String>> {
        viewModelScope.launch {
            plantNames = plantRep.getNames()
        }
        return plantNames
    }
    // 수정 원래 데이터 뿌리기 : docId
    fun getJournal(docId:String): MutableLiveData<JournalModel> {
        viewModelScope.launch {
            journals = journalRep.getJournal(docId)
        }
        return journals
    }
    
    // 생성/수정하기 - 사진있는 경우(사진업로드), 없는경우 : isImg, docData, docId, isEdit
    fun setJournal(
        isImg: Boolean, docData: Map<String, Any?>,
        photoUri:Uri?, docId: String?,
        isEdit: Boolean?
    ): MutableLiveData<Boolean> {
        viewModelScope.launch {
            isComplate.postValue(true)
            if(isImg){  // 이미지 있을 때
                val imgUri = journalRep.CreateJournalImg(photoUri)
                if (imgUri.equals("")){
                    isComplate.postValue(false)
                }
                val data = mapOf<String,Any?>(
                    "content" to docData.get("content"),
                    "name" to docData.get("name"),
                    "date" to docData.get("date"),
                    "imgUri" to imgUri
                )
                //수정/추가
                when(isEdit){
                    true->{
                        if (docId != null) {
                            journalRep.ModifyJournal(data,isImg,docId)
                        }
                    }
                    false->{
                        journalRep.CreateJournal(data)
                    }
                }
            }else{
                // 수정/추가
                when(isEdit){
                    true->{
                        if (docId != null) {
                            journalRep.ModifyJournal(docData,isImg,docId)
                        }
                    }
                    false->{
                        journalRep.CreateJournal(docData)
                    }
                }
            }

        }
        return isComplate
    }
}