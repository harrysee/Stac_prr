package kr.hs.emirim.w2015.stac_prr.Repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_plant_info.*
import kr.hs.emirim.w2015.stac_prr.Model.HomeData
import kr.hs.emirim.w2015.stac_prr.Model.PlantInfo
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlantRepository {
    private val plantLiveData = MutableLiveData<PlantInfo>()    // 세부내용
    private val plantListLiveData = MutableLiveData<ArrayList<HomeData>>()  // 전체
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val auth = Firebase.auth

    // 상세내용 가져오기
    suspend fun getPlantLiveData(docId : String): MutableLiveData<PlantInfo> {
        db.collection("plant_info").document(docId!!)
            .get()
            .addOnSuccessListener {
                Log.d("TAG", "onViewCreated: 식물 정보 가져오기 성!공!")
                val plant = PlantInfo(
                    it["name"] as String,
                    (it["date"] as Timestamp).toDate(),
                    it["imgurl"] as String?:"",
                    it["led"] as String?:"",
                    it["memo"] as String?:"",
                    it["specise"] as String?:"",
                    it["temperature"] as String?:"",
                    it["water"] as Int?:0,
                    it["dviceNum"] as String?:""
                )
                plantLiveData.postValue(plant)
            }.addOnFailureListener {
                Log.d(it.toString(), "onViewCreated: 식물정보를 가져오기 못함")
            }
        return plantLiveData
    }

    // 전체 가져오기
    suspend fun getPlantListLiveData(): MutableLiveData<ArrayList<HomeData>> {
        val homeDatas = ArrayList<HomeData>()

        homeDatas.let {
            db.collection("plant_info")
                .whereEqualTo("userId", auth.uid)
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        Log.i("TAG", "getDataList: 식물 데이터 보여주기 ${document.data}")
                        homeDatas?.add(HomeData(document["name"] as String?,
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
    suspend fun createPlant(plantData: HashMap<String, Comparable<Any>?> ){
        db!!.collection("plant_info").document()
            .set(plantData)
            .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감") }
            .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류", e) }
    }

    // plant 수정하기
    suspend fun modifyPlant(docId: String?, plantData: HashMap<String, Comparable<Any>?>,isImg:Boolean ){
        docId?.let { it1 ->
            if(isImg){
                db!!.collection("plant_info").document(it1)
                    .update(mapOf(
                        "specise" to plantData.get("specise"),              // 종류
                        "led" to plantData.get("led"),                // 빛
                        "water" to plantData.get("water"),              //급수주기
                        "temperature" to plantData.get("temperature"),        // 온도
                        "memo" to plantData.get("memo"),               //메모
                        "date" to plantData.get("date"),   // 날짜
                        "imgUri" to plantData.get("imgUri")       // 이미지 uri
                    ))
                    .addOnSuccessListener {
                        Log.d("TAG", "onViewCreated: 수정 성공 ")
                    }.addOnCanceledListener {
                        Log.d("TAG", "onViewCreated: 수정 성공 못함")
                    }
            }else{  // 이미지가 없을때 빼고 업데이트
                db!!.collection("plant_info").document(it1)
                    .update(mapOf(
                        "specise" to plantData.get("specise"),              // 종류
                        "led" to plantData.get("led"),                // 빛
                        "water" to plantData.get("water"),              //급수주기
                        "temperature" to plantData.get("temperature"),        // 온도
                        "memo" to plantData.get("memo"),               //메모
                        "date" to plantData.get("date"),   // 날짜
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