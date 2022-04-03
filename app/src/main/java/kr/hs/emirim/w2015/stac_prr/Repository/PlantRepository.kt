package kr.hs.emirim.w2015.stac_prr.Repository

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_new_plant.*
import kr.hs.emirim.w2015.stac_prr.Model.HomeModel
import kr.hs.emirim.w2015.stac_prr.Model.PlantModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PlantRepository {
    private val plantLiveData = MutableLiveData<PlantModel>()    // 세부내용
    private val plantListLiveData = MutableLiveData<ArrayList<HomeModel>>()  // 전체
    private val plantNameLiveData = MutableLiveData<ArrayList<String>>()  // 전체
    private val plantImgUri = MutableLiveData<String>()  // 전체
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    private val auth = Firebase.auth

    // 상세내용 가져오기
    suspend fun getPlantLiveData(docId : String): MutableLiveData<PlantModel> {
        db.collection("plant_info").document(docId!!)
            .get()
            .addOnSuccessListener {
                Log.d("TAG", "onViewCreated: 식물 정보 가져오기 성!공!")
                val plant = PlantModel(
                    it["name"] as String,
                    (it["date"] as Timestamp).toDate(),
                    it["imgurl"] as String?:"",
                    it["led"] as String?:"",
                    it["memo"] as String?:"",
                    it["specise"] as String?:"",
                    it["temperature"] as String?:"",
                    it["water"] as String?:"",
                    it["dviceNum"] as String?:""
                )
                plantLiveData.postValue(plant)
            }.addOnFailureListener {
                Log.d(it.toString(), "onViewCreated: 식물정보를 가져오기 못함")
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
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        Log.i("TAG", "getDataList: 식물 데이터 보여주기 ${document.data}")
                        homeDatas?.add(HomeModel(document["name"] as String?,
                            document["specise"] as String?,
                            document["imgUri"] as String?,
                            document.id))
                        Log.d("TAG", "getDataList: 홈데이터 담기 : $homeDatas")
                        //this.homedatas = homedatas
                    }
                    plantListLiveData.postValue(homeDatas)
                    Log.d(it.toString(), "setflower: 식물 데이터 홈에서 불러오기 성공 $homeDatas")
                }.addOnFailureListener {
                    Log.d(it.toString(), "setflower: 식물 데이터 홈에서 불러오기 실패")
                }
            return plantListLiveData
        }
    }
    
    // plant 생성하기
    suspend fun createPlant(plantData: PlantModel){
        val plantmap = hashMapOf(
            "name" to plantData.name,           //식물이름
            "specise" to plantData.specise,                  // 종류
            "led" to plantData.led,                // 빛
            "water" to plantData.water,              //급수주기
            "temperature" to plantData.temperate,        // 온도
            "memo" to plantData.memo,               //메모
            "date" to plantData.date,   // 날짜
            "imgUri" to plantData.imgUrl,        // 이미지 uri
            "userId" to auth.uid    // 식별가능한 유저 아이디
        )
        db!!.collection("plant_info").document()
            .set(plantmap)
            .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감") }
            .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류", e) }
    }

    // 이미지 추가하기
    fun createPlantImg(photoURI:Uri?, docData:PlantModel,isEdit:Boolean): MutableLiveData<String> {
        var downloadUri = ""
        if (photoURI != null) { // 이미지 있을 때
            val filename = "_" + System.currentTimeMillis()
            val imagesRef: StorageReference? = storageRef.child("info/" + filename)

            var file: Uri? = null
            try {
                file = photoURI
                // 잘올라갔으면 다운로드 url 가져오기
                Log.d("TAG", "onViewCreated: 사진 uri ${photoURI}")
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
                        Log.d(downloadUri.toString(), "onViewCreated: 사진 업로드 완료")
                        plantImgUri.postValue(downloadUri)
                    }// 사진이 있으면 그냥 올리기
                }// uploadtask end
            } catch (e: java.lang.Exception) {
                Log.i("TAG", "createPlantImg: 사진업로드 실패")
            }
        }
        return plantImgUri
    }

    // plant 수정하기
    suspend fun modifyPlant(docId: String?, plantData: PlantModel,isImg:Boolean ){
        docId?.let { it1 ->
            if(isImg){
                db!!.collection("plant_info").document(it1)
                    .update(mapOf(
                        "specise" to plantData.specise,              // 종류
                        "led" to plantData.led,                // 빛
                        "water" to plantData.water,              //급수주기
                        "temperature" to plantData.temperate,        // 온도
                        "memo" to plantData.memo,               //메모
                        "date" to plantData.date,   // 날짜
                        "imgUri" to plantData.imgUrl     // 이미지 uri
                    ))
                    .addOnSuccessListener {
                        Log.d("TAG", "onViewCreated: 수정 성공 ")
                    }.addOnCanceledListener {
                        Log.d("TAG", "onViewCreated: 수정 성공 못함")
                    }
            }else{  // 이미지가 없을때 빼고 업데이트
                db!!.collection("plant_info").document(it1)
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
                        Log.d("TAG", "onViewCreated: 수정 성공 못함")
                    }
            }
        }
    }

    // plant 삭제하기
    suspend fun deletePlant(docId: String?){
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