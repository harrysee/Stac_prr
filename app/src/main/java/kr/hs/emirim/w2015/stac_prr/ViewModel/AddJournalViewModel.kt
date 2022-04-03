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
    var isComplate = MutableLiveData<Boolean>()
    val plantRep = PlantRepository
    val journalRep = JournalRepository
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val auth = Firebase.auth

    // 수정 원래 데이터 뿌리기 : docId
    fun getJournal(docId:String): MutableLiveData<JournalModel> {
        viewModelScope.launch {
            journals = journalRep.getJournal(docId)
        }
        return journals
    }
    
    // 생성/수정하기 - 사진있는 경우(사진업로드), 없는경우 : isImg, docData, docId, isEdit
    fun setJournal(isImg : Boolean, docData:HashMap<String,Any>,photoUri:Uri?, docId:String,isEdit:Boolean ){
        viewModelScope.launch {
            if(isImg){  // 이미지 있을 때
                val imgUri = journalRep.CreateJournalImg(photoUri)
                docData.put("imgUri",imgUri)
                when(isEdit){
                    true->{
                        journalRep.ModifyJournal(docData,isImg,docId)
                    }
                    false->{
                        journalRep.CreateJournal(docData)
                    }
                }
            }else{
                when(isEdit){
                    true->{
                        journalRep.ModifyJournal(docData,isImg,docId)
                    }
                    false->{
                        journalRep.CreateJournal(docData)
                    }
                }
            }
        }
    }
}