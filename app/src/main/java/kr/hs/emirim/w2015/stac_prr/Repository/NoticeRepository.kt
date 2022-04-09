package kr.hs.emirim.w2015.stac_prr.Repository

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_set.*
import kr.hs.emirim.w2015.stac_prr.Model.NoticeModel

object NoticeRepository {
    var isNotice = true
    val notices = MutableLiveData<ArrayList<NoticeModel>>()
    val db = FirebaseFirestore.getInstance()

    @SuppressLint("ApplySharedPref")
    suspend fun getNoticeDot(context : Activity?): Boolean {
        val push = context?.getSharedPreferences("push", Context.MODE_PRIVATE)!!
        val noticeSize = push.getInt("noticeSize",0)
        db.collection("noticed")
            .get()
            .addOnSuccessListener { result ->
                Log.d("TAG", "setNoticeDot: 공지사항 가지러옴 : ${result.size()} /$noticeSize")
                if (noticeSize < result.size() || noticeSize > result.size()){
                    Log.d("TAG", "setNoticeDot: 공지사항 가지러옴 개수 : ${result.size()} / $noticeSize")
                    push.edit()
                        .putInt("noticeSize",result.size())
                        .putBoolean("isOpen",true)
                        .apply()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
                isNotice = false
            }
        return isNotice
    }

    suspend fun getNoticesrep(): MutableLiveData<ArrayList<NoticeModel>> {
        val datas = ArrayList<NoticeModel>()
        db.collection("noticed")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val content = (document["content"] as String).replace("\\n", "\n")
                    datas.add(NoticeModel(document["title"] as String,document["date"] as String,content))
                    Log.d("TAG", "${document["title"]} => ${datas}")
                }
                notices.postValue(datas)
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
        return notices
    }
}