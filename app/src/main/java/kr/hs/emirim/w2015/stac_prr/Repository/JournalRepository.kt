package kr.hs.emirim.w2015.stac_prr.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_new_journal.*
import kr.hs.emirim.w2015.stac_prr.Model.JournalModel
import kr.hs.emirim.w2015.stac_prr.Model.PlanModel
import java.text.SimpleDateFormat

class JournalRepository {
    private val journalList = MutableLiveData<ArrayList<JournalModel>>()     // 여기가 데이터저장 배열
    private val plantjournal = MutableLiveData<ArrayList<JournalModel>>()     // 여기가 데이터저장 배열
    private val journal = MutableLiveData<JournalModel>()     // 여기가 데이터저장 배열
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    private suspend fun getJournalList(dateSort:Boolean): MutableLiveData<ArrayList<JournalModel>> {
        val datas = ArrayList<JournalModel>()
        if (dateSort==false){   //내림차순으로 가져오기
            db.collection("journals")
                .document(auth.uid.toString())
                .collection("journal")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                    for (document in it) {
                        val date = document["date"] as Timestamp
                        datas.add(JournalModel(
                            document["name"] as String,
                            document["content"] as String,
                            document["date"] as Timestamp,
                            document["imgUri"] as String?,
                            document.id))
                    }
                    Log.i("파이어베이스데이터", datas.toString());
                }
        }else{  //오름차순으로 가져오기
            db.collection("journals")
                .document(auth.uid.toString())
                .collection("journal")
                .orderBy("date")
                .get()
                .addOnSuccessListener {
                    Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                    for (document in it) {
                        val date = document["date"] as Timestamp
                        datas.add(JournalModel(
                            document["name"] as String,
                            document["content"] as String,
                            document["date"] as Timestamp,
                            document["imgUri"] as String?,
                            document.id))
                    }
                    Log.i("파이어베이스데이터", datas.toString())
                }
        }
        journalList.postValue(datas)
        return journalList
    }

    // 식물에 따라 가져오기
    suspend fun getPlantJournal(dateSort: Boolean, name:String): MutableLiveData<ArrayList<JournalModel>> {
        val datas = ArrayList<JournalModel>()
        if (dateSort==false){   //내림차순으로 가져오기
            db.collection("journals")
                .document(auth.uid.toString())
                .collection("journal")
                .whereEqualTo("name", name)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        val date = document["date"] as Timestamp
                        datas.add(JournalModel(
                            document["name"] as String,
                            document["content"] as String,
                            document["date"] as Timestamp,
                            document["imgUri"] as String?,
                            document.id))
                    }
                    Log.i("파이어베이스데이터", datas.toString());
                }
        }else{  //오름차순으로 가져오기
            db.collection("journals")
                .document(auth.uid.toString())
                .collection("journal")
                .whereEqualTo("name", name)
                .orderBy("date")
                .get()
                .addOnSuccessListener {
                    Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                    for (document in it) {
                        val date = document["date"] as Timestamp
                        datas.add(JournalModel(
                            document["name"] as String,
                            document["content"] as String,
                            document["date"] as Timestamp,
                            document["imgUri"] as String?,
                            document.id))
                    }
                    Log.i("파이어베이스데이터", datas.toString());
                }
        }
        plantjournal.postValue(datas)
        return plantjournal
    }

    // 일지 한개의 세부내용
    suspend fun getJournal(docId :String): MutableLiveData<JournalModel> {
        auth.uid?.let {
            db!!.collection("journals")
                .document(it).collection("journal").document(docId!!)
                .get()
                .addOnSuccessListener {
                    val journals = JournalModel(
                        it["name"] as String?:"",
                        it["content"] as String?:"",
                        it["date"] as Timestamp,
                        it["imgUri"] as String?:"",
                        docId
                    )
                    journal.postValue(journals)
                }
        }
        return journal
    }

    // 일지 추가
    suspend fun CreateJournal(docData : HashMap<String, Comparable<Any>?>){
        auth.uid?.let {
            db!!.collection("journals").document(it).collection("journal").document()
                .set(docData)
                .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal") }
                .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : journal", e) }
        }

    }

    // 일지 수정
    suspend fun ModifyJournal(docData: HashMap<String, Comparable<Any>?>, isImg:Boolean, docId: String){
        if (isImg) {// 파일 업로드
            auth.uid?.let {
                db!!.collection("journals").document(it).collection("journal").document(docId!!)
                    .update(mapOf(
                        "content" to docData.get("content"),
                        "name" to docData.get("name"),
                        "date" to docData.get("date"),
                        "imgUri" to docData.get("imgUri")
                    ))
                    .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal") }
                    .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : journal", e) }
            }
        }else {  //사진이 들어잇는경우 업로드
            auth.uid?.let {
                db!!.collection("journals").document(it).collection("journal").document(docId!!)
                    .update(mapOf(
                        "content" to docData.get("content"),
                        "name" to docData.get("name"),
                        "date" to docData.get("date"),
                    ))
                    .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal") }
                    .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : journal", e) }
            }
        }
    }

    // 일지 삭제
    suspend fun deleteJournal(name:String){
        db.collection("journals")
            .document(auth.uid.toString())
            .collection("journal")
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener {
                //각 아이템 삭제하기
                for (document in it) {
                    auth.uid?.let { it1 ->
                        db.collection("journals").document(it1)
                            .collection("journal")
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("TAG", "일지 파이어스토어 해당 식물관련 일지 모두삭제")
                            }
                            .addOnFailureListener { e ->
                                Log.w("TAG", "일지 파이어스토어 해당 식물관련 일지삭제안됨", e)
                            }
                    }
                }// journal for end
            }// journal success end
    }

}