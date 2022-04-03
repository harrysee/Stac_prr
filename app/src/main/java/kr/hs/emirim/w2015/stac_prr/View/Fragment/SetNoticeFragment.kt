package kr.hs.emirim.w2015.stac_prr.View.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_set_notice.*
import kr.hs.emirim.w2015.stac_prr.View.Adapter.SetNoticeAdapter
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.Model.NoticeModel
import kr.hs.emirim.w2015.stac_prr.R

class SetNoticeFragment : Fragment() {
    lateinit var noticeAdapter: SetNoticeAdapter
    val datas = mutableListOf<NoticeModel>()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_notice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()

        notice_pass_btn.setOnClickListener(){
            val activity = activity as MainActivity
            activity.fragmentChange_for_adapter(SetFragment())
        }


    }
    private fun initRecycler() {
        noticeAdapter = SetNoticeAdapter(requireContext())
        notice_recycler.adapter = noticeAdapter

        db.collection("noticed")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val content = (document["content"] as String).replace("\\n", "\n")
                    datas.add(NoticeModel(document["title"] as String,document["date"] as String,content))
                    Log.d("TAG", "${document["title"]} => ${datas}")
                }
                noticeAdapter.datas = datas
                noticeAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),"공지사항 불러오기 실패", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "Error getting documents: ", exception)
            }

    }
}