package kr.hs.emirim.w2015.stac_prr.Repository

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_new_plant.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.hs.emirim.w2015.stac_prr.Model.HomeModel
import kr.hs.emirim.w2015.stac_prr.Model.PlantModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object PlantRepository {
    private val plantLiveData = MutableLiveData<PlantModel>()    // 세부내용
    private val plantListLiveData = MutableLiveData<ArrayList<HomeModel>>()  // 전체
    private val plantNameLiveData = MutableLiveData<ArrayList<String>>()  // 전체
    private val isComplate = MutableLiveData<Boolean>()  // 전체
    private val db = FirebaseFirestore.getInstance()
    var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    private val auth = Firebase.auth

    // 상세내용 가져오기
    suspend fun getPlantLiveData(docId : String): MutableLiveData<PlantModel> {
        db.collection("plant_info").document(docId!!)
            .addSnapshotListener { it, error ->
                Log.d("TAG", "onViewCreated: 식물 정보 가져오기 성!공!")
                val plant = PlantModel(
                    it?.get("name") as String,
                    (it["date"] as Timestamp).toDate(),
                    (it["imgurl"] ?: "") as String,
                    it["led"] as String ?: "",
                    it["memo"] as String ?: "",
                    it["specise"] as String ?: "",
                    it["temperature"] as String ?: "",
                    it["water"] as String ?: "",
                    (it["dviceNum"] ?: "" as String ?: "") as String
                )
                plantLiveData.postValue(plant)
            }
        return plantLiveData
    }
    // 이름들만 가져오기
    suspend fun getNames(): MutableLiveData<ArrayList<String>> {
        val names = ArrayList<String>()
        db.collection("plant_info")
            .whereEqualTo("userId", auth?.uid)
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    names.add(doc["name"] as String)
                    Log.i("TAG", "getNames: 일지 이름"+doc["name"])
                }
                plantNameLiveData.postValue(names)
            }.addOnFailureListener {
                Log.d("TAG", "getNames: spinner 식물 이름들 보여주기 실패")
            }
        return plantNameLiveData
    }

    // 전체 가져오기
    suspend fun getPlantListLiveData(): MutableLiveData<ArrayList<HomeModel>> {
        val homeDatas = ArrayList<HomeModel>()

        homeDatas.let {
            db.collection("plant_info")
                .whereEqualTo("userId", auth.uid)
                .addSnapshotListener(EventListener<QuerySnapshot> { value, error ->
                    if (value != null) {
                        Log.i("TAG", "getPlantListLiveData: "+value.size())
                        homeDatas.clear()
                        for (document in value) {
                            Log.i("TAG", "getDataList: 식물 데이터 보여주기 ${document.data}")
                            homeDatas?.add(HomeModel(document["name"] as String?,
                                document["specise"] as String?,
                                document["imgUri"] as String?,
                                document.id))
                            Log.d("TAG", "getDataList: 홈데이터 담기 : $homeDatas")
                            //this.homedatas = homedatas
                        }
                    }
                    plantListLiveData.postValue(homeDatas)
                    Log.d(it.toString(), "setflower: 식물 데이터 홈에서 불러오기 성공 $homeDatas")
                })

            return plantListLiveData
        }
    }
    
    // plant 생성하기
    fun createPlant(plantData: PlantModel): MutableLiveData<Boolean> {
        Log.i("TAG", "createPlantImg1111: 업로드"+"아님")
        val plantmap = hashMapOf(
            "name" to plantData.name,           //식물이름
            "specise" to plantData.specise,                  // 종류
            "led" to plantData.led,                // 빛
            "water" to plantData.water,              //급수주기
            "temperature" to plantData.temperate,        // 온도
            "memo" to plantData.memo,               //메모
            "date" to plantData.date,   // 날짜
            "imgUri" to "",        // 이미지 uri
            "userId" to auth.uid    // 식별가능한 유저 아이디
        )
        db!!.collection("plant_info").document()
            .set(plantmap)
            .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감") }
            .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류", e)
                isComplate.postValue(false)
            }
        return isComplate
    }

    // 이미지 추가하기
    fun createPlantImg(docId: String?,photoURI:Uri?, plantData:PlantModel,isEdit:Boolean): MutableLiveData<Boolean> {
        var downloadUri = ""
        val filename = "_" + System.currentTimeMillis()
        val imagesRef: StorageReference? = storageRef.child("info/" + filename)
        Log.d(ContentValues.TAG, "onViewCreated333: 사진uri "+ photoURI)

        var file: Uri? = null
        try {
            file = photoURI
            // 잘올라갔으면 다운로드 url 가져오기
            Log.d("TAG", "onViewCreated: 사진 uri444 ${photoURI}")
            // 스토리지에 올리기
            val uploadTask = imagesRef?.putFile(file!!)
            uploadTask?.continueWithTask { task ->
                Log.d("TAG", "onViewCreated: 새로운 식물 continue 들어옴")
                if (!task.isSuccessful) {
                    task.exception?.let {
                        Log.d("TAG", "onViewCreated: 사진 안올라감")
                        throw it
                    }
                }
                imagesRef.downloadUrl.addOnSuccessListener { task ->
                    downloadUri = task.toString()  // 다운로드 uri string으로 가져오기
                    if (isEdit){ // 수정일경우
                        Log.i("TAG", "createPlantImg1111: 업로드"+downloadUri)
                        docId?.let { it1 ->
                            db!!.collection("plant_info").document(it1)
                                .update(mapOf(
                                    "specise" to plantData.specise,              // 종류
                                    "led" to plantData.led,                // 빛
                                    "water" to plantData.water,              //급수주기
                                    "temperature" to plantData.temperate,        // 온도
                                    "memo" to plantData.memo,               //메모
                                    "date" to plantData.date,   // 날짜
                                    "imgUri" to downloadUri     // 이미지 uri
                                ))
                                .addOnSuccessListener {
                                    Log.d("TAG", "onViewCreated: 수정 성공 ")
                                }.addOnCanceledListener {
                                    isComplate.postValue(false)
                                    Log.d("TAG", "onViewCreated: 수정 성공 못함")
                                }
                        }
                    }else{ // 수정이 아닐때
                        Log.i("TAG", "createPlantImg1111: 업로드"+downloadUri)
                        val plantmap = hashMapOf(
                            "name" to plantData.name,           //식물이름
                            "specise" to plantData.specise,                  // 종류
                            "led" to plantData.led,                // 빛
                            "water" to plantData.water,              //급수주기
                            "temperature" to plantData.temperate,        // 온도
                            "memo" to plantData.memo,               //메모
                            "date" to plantData.date,   // 날짜
                            "imgUri" to downloadUri,        // 이미지 uri
                            "userId" to auth.uid    // 식별가능한 유저 아이디
                        )
                        db!!.collection("plant_info").document()
                            .set(plantmap)
                            .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감") }
                            .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류", e)
                                isComplate.postValue(false)
                            }
                    }
                    Log.d(downloadUri.toString(), "onViewCreated: 사진 업로드 완료")
                }// 사진이 있으면 그냥 올리기
            }// uploadtask end
        } catch (e: java.lang.Exception) {
            isComplate.postValue(false)
            Log.i("TAG", "createPlantImg: 사진업로드 실패")
        }

        return isComplate
    }

    // plant 수정하기
    fun modifyPlant(docId: String?, plantData: PlantModel): MutableLiveData<Boolean> {
        isComplate.postValue(true)
        if (docId != null) {
            db!!.collection("plant_info").document(docId)
                .update(mapOf(
                    "specise" to plantData.specise,              // 종류
                    "led" to plantData.led,                // 빛
                    "water" to plantData.water,              //급수주기
                    "temperature" to plantData.temperate,        // 온도
                    "memo" to plantData.memo,               //메모
                    "date" to plantData.date,   // 날짜
                ))
                .addOnSuccessListener {
                    Log.d("TAG", "onViewCreated: 수정 성공 ")
                }.addOnCanceledListener {
                    isComplate.postValue(false)
                    Log.d("TAG", "onViewCreated: 수정 성공 못함")
                }
            }
        return isComplate
    }

    // plant 삭제하기
    suspend fun deletePlant(docId: String?,imgUri:String?){
        if(!imgUri.equals("") && imgUri!= null){
            val storageRef = storage.reference
            val desertRef = storageRef.child(imgUri)
            // 삭제하기
            desertRef.delete().addOnSuccessListener {
                Log.i("TAG", "deletePlant: 정상사진삭제")
            }.addOnFailureListener {
                Log.i(it.toString(), "deletePlant: 사진 삭제안됨")
            }
        }
        db.collection("plant_info").document(docId!!).delete()
        .addOnSuccessListener {
            Log.d("TAG",
                "setFabClick: 식물정보 - 해당식물 plantInfo에서 삭제함")
        }
        .addOnFailureListener {
            Log.d("TAG",
                "setFabClick: 식물정보 - 해당식물 plantInfo에서 삭제안됨")
        }
    }
}