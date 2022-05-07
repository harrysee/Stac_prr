package kr.hs.emirim.w2015.stac_prr.Repository

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kr.hs.emirim.w2015.stac_prr.Model.JournalModel

object JournalRepository {
    private val journalList = MutableLiveData<ArrayList<JournalModel>>()     // 오름차순 모든 식물
    private val journalListD = MutableLiveData<ArrayList<JournalModel>>()     // 내림차순 모든 식물
    private val plantjournal = MutableLiveData<ArrayList<JournalModel>>()     // 해당식물 배열 가져오기
    private val bookmarkjournal = MutableLiveData<ArrayList<JournalModel>>()    // 북마크 식물 가져오기
    private val journal = MutableLiveData<JournalModel>()     // 여기가 데이터저장 배열
    private val journalUploadImg = MutableLiveData<String?>()
    private var isSucces = MutableLiveData<Boolean>()
    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    val journalImgs = MutableLiveData<ArrayList<String?>>()     // 여기가 데이터저장 배열
    var isComplate = MutableLiveData<Boolean>()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    
    // 전체 가져오기 - 오름차순
    suspend fun getJournalListAsk(): MutableLiveData<ArrayList<JournalModel>> {
        val datas = ArrayList<JournalModel>()
        db.collection("journals")
            .document(auth.uid.toString())
            .collection("journal")
            .orderBy("date")
            .addSnapshotListener { value, error ->
                Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                datas.clear()
                if (value != null) {
                    for (document in value) {
                        datas.add(
                            JournalModel(
                                document["name"] as String,
                                document["content"] as String,
                                document["date"] as Timestamp,
                                document["imgUri"] as String?,
                                document["bookmark"] as Boolean?,
                                document.id))
                    }
                    journalList.postValue(datas)
                }
                Log.i("파이어베이스데이터", datas.toString())
            }
        return journalList
    }

    suspend fun getJournalListDest(): MutableLiveData<ArrayList<JournalModel>> {
        val datas = ArrayList<JournalModel>()
        db.collection("journals")
            .document(auth.uid.toString())
            .collection("journal")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener<QuerySnapshot>{ value, error->
                Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                if (value != null) {
                    datas.clear()
                    for (document in value) {
                        datas.add(JournalModel(
                            document["name"] as String,
                            document["content"] as String,
                            document["date"] as Timestamp,
                            document["imgUri"] as String?,
                            document["bookmark"] as Boolean?,
                            document.id))
                        Log.i("TAG", "getJournalList: 일지 데이터"+document)
                    }
                    journalListD.postValue(datas)
                }
                Log.i("파이어베이스데이터", datas.toString());
            })
        return journalListD
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
                .addSnapshotListener { value, error ->
                    datas.clear()
                    if (value != null) {
                        for (document in value) {
                            datas.add(JournalModel(
                                document["name"] as String,
                                document["content"] as String,
                                document["date"] as Timestamp,
                                document["imgUri"] as String?,
                                document["bookmark"] as Boolean?,
                                document.id))
                        }
                        plantjournal.postValue(datas)
                    }
                    Log.i("","파이어베이스데이터"+datas);
                }
        }else{  //오름차순으로 가져오기
            db.collection("journals")
                .document(auth.uid.toString())
                .collection("journal")
                .whereEqualTo("name", name)
                .orderBy("date")
                .addSnapshotListener { value, error ->
                    datas.clear()
                    Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                    if (value != null) {
                        for (document in value) {
                            datas.add(JournalModel(
                                document["name"] as String,
                                document["content"] as String,
                                document["date"] as Timestamp,
                                document["imgUri"] as String?,
                                document["bookmark"] as Boolean?,
                                document.id))
                        }
                        plantjournal.postValue(datas)
                    }
                    Log.i("파이어베이스데이터 일지이름별", datas.toString());
                }
        }
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
                        it["imgUri"] as String?,
                        it["bookmark"] as Boolean,
                        docId
                    )
                    journal.postValue(journals)
                }
        }
        return journal
    }

    // 북마크만 가져오기
    suspend fun getBookmarks(): MutableLiveData<ArrayList<JournalModel>> {
        val datas = ArrayList<JournalModel>()
        db.collection("journals")
            .document(auth.uid.toString())
            .collection("journal")
            .whereEqualTo("bookmark",true)
            .orderBy("date")
            .addSnapshotListener { value, error ->
                datas.clear()
                if (value != null) {
                    for(document in value){
                        datas.add(JournalModel(
                            document["name"] as String,
                            document["content"] as String,
                            document["date"] as Timestamp,
                            document["imgUri"] as String?,
                            document["bookmark"] as Boolean?,
                            document.id)
                        )
                    }
                    bookmarkjournal.postValue(datas)
                }
            }
        return bookmarkjournal
    }
    // 일지 사진들만 가져오기
    suspend fun getJournalImg(name: String): MutableLiveData<ArrayList<String?>> {
        Log.i(TAG, "getJournalImg: 사진 가지러옴"+name)
        val imgData = ArrayList<String?>()
        db.collection("journals")
            .document(auth.uid.toString())
            .collection("journal")
            .whereEqualTo("name", name)
            .orderBy("date")
            .addSnapshotListener { value, error ->
                Log.d("", "makeTestItems: 해당 이름 사진 가져오기 성공")
                imgData.clear()
                if (value != null) {
                    for (document in value) {
                        if (document["imgUri"] as String? !=null){
                            imgData.add(document["imgUri"] as String?)
                        }
                    }
                    journalImgs.postValue(imgData)
                }
                Log.i("갤러리 이미지리소스 주기11", imgData.toString()+ journalImgs.toString());
            }
        return journalImgs
    }

    // 일지 추가
    suspend fun CreateJournal(docData: Map<String, Any?>){
        Log.i("TAG", "CreateJournal: 일지생성"+docData)
        val datas = hashMapOf(
            "content" to docData["content"],
            "name" to docData["name"],
            "date" to docData["date"],   // 날짜
            "bookmark" to docData["bookmark"],
            "imgUri" to ""
        )
        auth.uid?.let {
            db!!.collection("journals").document(it).collection("journal").document()
                .set(datas)
                .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal") }
                .addOnFailureListener { e -> Log.w("TAG", "파이어베이스 일지 오류 : journal", e) }
        }
    }

    // 일지 이미지 추가
    suspend fun CreateJournalImg(photoURI : Uri?,isEdit:Boolean,docData: Map<String, Any?>,docId: String): MutableLiveData<String?> {
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
                    when(isEdit){
                        true->{
                            auth.uid?.let {
                                Log.i(TAG, "CreateJournalImg: 사진 수정하기 들어옴"+downloadUri)
                                db!!.collection("journals").document(it).collection("journal")
                                    .document(docId!!)
                                    .update(mapOf(
                                        "content" to docData.get("content"),
                                        "name" to docData.get("name"),
                                        "date" to docData.get("date"),
                                        "bookmark" to docData.get("bookmark"),
                                        "imgUri" to downloadUri
                                    ))
                                    .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal") }
                                    .addOnFailureListener { e ->
                                        Log.w("TAG",
                                            "파이어스토어 업로드 오류 : journal",
                                            e)
                                    }
                            }
                        }
                        else->{
                            val data = mapOf<String,Any?>(
                                "content" to docData.get("content"),
                                "name" to docData.get("name"),
                                "date" to docData.get("date"),
                                "imgUri" to downloadUri
                            )
                            auth.uid?.let {
                                db!!.collection("journals").document(it).collection("journal").document()
                                    .set(data)
                                    .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal"+data) }
                                    .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : journal", e)
                                    isComplate.postValue(false)}
                            }
                        }

                    }
                }
            }
        }catch (e: java.lang.Exception){
            Log.d("TAG", "onViewCreated: 사진 안들어감 : ${e.toString()}")
            isComplate.postValue(false)
        }
        return journalUploadImg
    }

    // 일지 수정
    suspend fun ModifyJournal(docData: Map<String, Any?>,docId: String?){
        auth.uid?.let {
            db!!.collection("journals").document(it).collection("journal").document(docId!!)
                .update(mapOf(
                    "content" to docData.get("content"),
                    "name" to docData.get("name"),
                    "date" to docData.get("date"),
                ))
                .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal") }
                .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : journal", e)
                isComplate.postValue(false)}
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

    suspend fun setBookmark(docId: String, isChecked : Boolean){
        db.collection("journals")
            .document(auth.uid.toString())
            .collection("journal")
            .document(docId)
            .update("bookmark",isChecked)
            .addOnSuccessListener {
                Log.i("", "checked: checked 업데이트"+isChecked)
            }
    }

}