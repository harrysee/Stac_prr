package kr.hs.emirim.w2015.stac_prr

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kr.hs.emirim.w2015.stac_prr.Adapter.PlanAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ItemModel {
    var items: ArrayList<ItemEntity?> = ArrayList()     // 여기가 데이터저장 배열
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var date : String = SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().time)

    fun makeTestItems() {    //테스트 아이템
        items.clear()
        Log.d("TAG", "makeTestItems: 선택된 날짜 $date")
        db.collection("schedule")
            .document(auth.uid.toString())
            .collection("plans")
            .whereEqualTo("str_date",date)
            .get()
            .addOnSuccessListener {
                Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                for (document in it){
                    var item = ItemEntity()
                    Log.d("TAG", "makeTestItems: ${document.data}")
                    if (document["str_date"] != null) {
                        Log.d("TAG", "makeTestItems: 모델 ${document.data}")
                        item.contents = document["title"] as String?
                        item.name = document["name"] as String?
                        item.isChecked = document["checkbox"] as Boolean
                        item.memo = document["memo"] as String?
                        item.docId = document.id
                        items.add(item)
                    }
                    Log.d("TAG", "makeTestItems: 추가된 아이템 : ${items[0]?.isChecked}")
                }
            }
    }

    inner class ItemEntity {     //체크되었는지랑 내용을 담은 클래스 생성
        var isChecked: Boolean = false
        var name : String? = null
        var contents: String? = null
        var memo : String? = null
        var docId : String=""
    }
}