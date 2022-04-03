package kr.hs.emirim.w2015.stac_prr.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kr.hs.emirim.w2015.stac_prr.Model.PlanModel
import java.security.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlanRepository {
    private val plansList = MutableLiveData<ArrayList<PlanModel>>()     // 여기가 데이터저장 배열
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
//    var date : String = SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().time) // 오늘날짜

    // 해당 날짜 plan 가져오기
    suspend fun getDatePlan(date:String): MutableLiveData<ArrayList<PlanModel>> {    //해당날짜 아이템
        val plans = ArrayList<PlanModel>()
        Log.d("TAG", "makeTestItems: 선택된 날짜 $date")
        db.collection("schedule")
            .document(auth.uid.toString())
            .collection("plans")
            .whereEqualTo("str_date",date)
            .get()
            .addOnSuccessListener {
                Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                for (document in it){
                    val item = PlanModel()
                    Log.d("TAG", "makeTestItems: ${document.data}")
                    if (document["str_date"] != null) {
                        Log.d("TAG", "makeTestItems: 모델 ${document.data}")
                        item.contents = document["title"] as String?
                        item.name = document["name"] as String?
                        item.isChecked = document["checkbox"] as Boolean
                        item.memo = document["memo"] as String?
                        item.docId = document.id
                        plans.add(item)
                    }
                    Log.d("TAG", "makeTestItems: 추가된 아이템 : ${plans[0]?.isChecked}")
                }
                plansList.postValue(plans)
            }
        return plansList
    }

    // 일정 추가
    suspend fun createPlan(docData:HashMap<String, Comparable<Any>>){
        val uid: String = auth.uid!!
        db!!.collection("schedule").document(uid).collection("plans").document()
            .set(docData)
            .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : schedule") }
            .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : schedule", e) }

    }
    // 아이템 checked 업데이트
    suspend fun checked(docId:String, isChecked:Boolean){
        db.collection("schedule")
            .document(auth.uid.toString())
            .collection("plans")
            .document(docId)
            .update("checkbox",isChecked)
            .addOnSuccessListener {
                Log.i("", "checked: checked 업데이트"+isChecked)
            }
    }
    // 일정 삭제
    suspend fun deletePlan(plantName:String){
        db.collection("schedule")
            .document(auth.uid.toString())
            .collection("plans")
            .whereEqualTo("name", plantName) // 해당식물 일정만
            .get()
            .addOnSuccessListener {
                Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                for (document in it) {
                    // 각 일정 삭제
                    auth.uid?.let { it1 ->
                        db.collection("schedule").document(it1)
                            .collection("plans")
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("TAG", "일정 | 파이어스토어 해당 식물관련 일정 모두삭제")
                            }
                            .addOnFailureListener { e ->
                                Log.w("TAG", "일정 | 파이어스토어 해당 식물관련 일지삭제안됨", e)
                            }
                    }
                }// schedule for end
            }
    }

}