package kr.hs.emirim.w2015.stac_prr.View.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_set_notice.*
import kr.hs.emirim.w2015.stac_prr.View.Adapter.SetNoticeAdapter
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.Model.NoticeModel
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.viewModel.NoticeViewModel

class SetNoticeFragment : Fragment() {
    lateinit var noticeAdapter: SetNoticeAdapter
    val datas = mutableListOf<NoticeModel>()
    val db = FirebaseFirestore.getInstance()
    val model by lazy{
        ViewModelProvider(requireActivity()).get(NoticeViewModel::class.java)
    }

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
        val push = context?.getSharedPreferences("push", Context.MODE_PRIVATE)!!
        push.edit()
            .putBoolean("isOpen",false)
            .apply()

        notice_pass_btn.setOnClickListener(){
            val activity = activity as MainActivity
            activity.fragmentChange_for_adapter(SetFragment())
        }
    }
    private fun initRecycler() {
        model.getNotices().observe(requireActivity(), Observer{
            Log.i("TAG", "initRecycler: 공지사항 가져옴"+it)
            noticeAdapter = SetNoticeAdapter(requireContext())
            notice_recycler.adapter = noticeAdapter
            noticeAdapter.datas = it
            noticeAdapter.notifyDataSetChanged()
        })
    }
}