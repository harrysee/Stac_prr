package kr.hs.emirim.w2015.stac_prr.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_set_notice.*
import kr.hs.emirim.w2015.stac_prr.Adapter.SetNoticeAdapter
import kr.hs.emirim.w2015.stac_prr.NoticeData
import kr.hs.emirim.w2015.stac_prr.R

// TODO: Retitle parameter arguments, choose titles that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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

    }
    private fun initRecycler() {
        noticeAdapter = SetNoticeAdapter(requireContext())
        notice_recycler.adapter = noticeAdapter


        datas.apply {
            add(NoticeData( title = "mary", main = "24"))
            add(NoticeData( title = "jenny", main = "26"))
            add(NoticeData(title = "jhon", main = "27"))
            add(NoticeData( title = "ruby", main = "21"))
            add(NoticeData( title = "yuna", main = "23"))

            noticeAdapter.datas = datas
            noticeAdapter.notifyDataSetChanged()

        }
    }
}