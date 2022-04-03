package kr.hs.emirim.w2015.stac_prr.Repository

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_new_journal.*
import kr.hs.emirim.w2015.stac_prr.Model.JournalModel
import kr.hs.emirim.w2015.stac_prr.Model.PlanModel
import java.text.SimpleDateFormat

object JournalRepository {
    private val journalList = MutableLiveData<ArrayList<JournalModel>>()     // 여기가 데이터저장 배열
    private val plantjournal = MutableLiveData<ArrayList<JournalModel>>()     // 여기가 데이터저장 배열
    private val journal = MutableLiveData<JournalModel>()     // 여기가 데이터저장 배열
    private val journalImgs = MutableLiveData<ArrayList<String?>>()     // 여기가 데이터저장 배열
    private val journalUploadImg = MutableLiveData<String?>()
    private var isSucces = MutableLiveData<Boolean>()
    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    
    // 전체 가져오기
    suspend fun getJournalList(dateSort:Boolean): MutableLiveData<ArrayList<JournalModel>> {
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

    // 일지 사진들만 가져오기
    suspend fun getJournalImg(name: String): MutableLiveData<ArrayList<String?>> {
        val imgData = ArrayList<String?>()
        db.collection("journals")
            .document(auth.uid.toString())
            .collection("journal")
            .whereEqualTo("name", name)
            .orderBy("date")
            .get()
            .addOnSuccessListener {
                Log.d("", "makeTestItems: 해당 이름 사진 가져오기 성공")
                for (document in it) {
                    if (document["imgUri"] as String? !=null){
                        imgData.add(document["imgUri"] as String?)
                    }
                }
                Log.i("갤러리 이미지리소스 주기", imgData.toString());
                journalImgs.postValue(imgData)
            }
        return journalImgs
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

    // 일지 이미지 추가
    suspend fun CreateJournalImg(photoURI : Uri?): MutableLiveData<String?> {
        val filename = "_" + System.currentTimeMillis()
        val imagesRef: StorageReference? = storageRef.child("journal/" + filename)
        var downloadUri: String? = null // 다운로드 uri 저장변수

        //스토리지 업로드
        var file: Uri? = null
        try {
            file = photoURI!!
            Log.d("TAG", "onViewCreated: 사진 URI : $file")
            val uploadTask = imagesRef?.putFile(file)

            uploadTask?.continueWithTask { task ->
                Log.d("TAG", "onViewCreated: 새로운 일지 continue 들어옴")
                if (!task.isSuccessful) {
                    task.exception?.let {
                        Log.d("TAG", "onViewCreated: 일지 안올라감")
                        throw it
                    }
                }
                imagesRef.downloadUrl.addOnSuccessListener { task ->
                    Log.d("TAG", "onViewCreated: 다운로드 Uri : ${task.toString()}")
                    downloadUri = task.toString()
                }
            }
            journalUploadImg.postValue(downloadUri)
        }catch (e: java.lang.Exception){
            Log.d("TAG", "onViewCreated: 사진 안들어감 : ${e.toString()}")
            journalUploadImg.postValue("")
        }
        return journalUploadImg
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

    // 일지 한개 삭제
    suspend fun deleteJournalItem(docId: String): MutableLiveData<Boolean> {
        isSucces.postValue(false)
        db.collection("journals")
            .document(auth.uid.toString())
            .collection("journal")
            .document(docId)
            .delete()
            .addOnSuccessListener {
                isSucces.postValue(true)
                Log.i("TAG", "deleteJournalItem: 삭제됨")
            }// journal success end
        return isSucces
    }
    // 해당 식물의 모든 일지 삭제
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