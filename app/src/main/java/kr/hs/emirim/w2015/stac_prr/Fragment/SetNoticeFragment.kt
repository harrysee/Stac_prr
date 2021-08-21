package kr.hs.emirim.w2015.stac_prr.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_set_notice.*
import kr.hs.emirim.w2015.stac_prr.Adapter.SetNoticeAdapter
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.NoticeData
import kr.hs.emirim.w2015.stac_prr.R

class SetNoticeFragment : Fragment() {
    lateinit var noticeAdapter: SetNoticeAdapter
    val datas = mutableListOf<NoticeData>()

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
        
        datas.add(NoticeData(title = "mary", main = "24"))
        datas.add(NoticeData(title = "mary", main = "24"))
        datas.add(NoticeData(title = "mary", main = "24"))
        datas.add(NoticeData(title = "mary", main = "24"))
        datas.add(NoticeData(title = "mary", main = "24"))
        datas.add(NoticeData(title = "mary", main = "24"))
        datas.add(NoticeData(title = "mary", main = "24"))

        noticeAdapter.datas = datas
        noticeAdapter.notifyDataSetChanged()
    }
}