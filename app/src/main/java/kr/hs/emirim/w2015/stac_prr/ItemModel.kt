package kr.hs.emirim.w2015.stac_prr

import android.graphics.Paint
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class ItemModel {
    var items: ArrayList<ItemEntity?> = ArrayList()     // 여기가 데이터저장 배열
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var date : Date = Calendar.getInstance().time

    // 각자 선택 메서드
    fun toggleEachItemClick(pos: Int) {
        items[pos]?.let {
            if(it.isChecked){
                it.isChecked = false
            }else if(!it.isChecked){
                it.isChecked = true
            }
            //체크박스 선택부분 업데이트
            db.collection("schedule")
                .document(auth.uid.toString())
                .collection("plans")
                .document(it.docId)
                .update("checkbox",it.isChecked)
        }

    }

    fun makeTestItems() {    //테스트 아이템
        items.clear()
        db.collection("schedule")
            .document(auth.uid.toString())
            .collection("plans")
            .whereEqualTo("date",Timestamp(date))
            .get()
            .addOnSuccessListener {
                Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                for (document in it){
                    var item = ItemEntity()
                    item.contents = document["name"] as String
                    item.name = document["title"] as String
                    item.isChecked = document["chekbox"] as Boolean
                    item.docId = document.id
                }
            }
    }

    inner class ItemEntity {     //체크되었는지랑 내용을 담은 클래스 생성
        var isChecked: Boolean = false
        var name : String? = null
        var contents: String? = null
        var docId : String=""
    }
}