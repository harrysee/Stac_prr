package kr.hs.emirim.w2015.stac_prr.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil.setContentView
import kotlinx.android.synthetic.main.fragment_journal.*
import kr.hs.emirim.w2015.stac_prr.Adapter.JournalAdapter
import kr.hs.emirim.w2015.stac_prr.JournalData
import kr.hs.emirim.w2015.stac_prr.R

class JournalFragment : Fragment() {
    lateinit var journalAdapter: JournalAdapter
    val datas = mutableListOf<JournalData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        journalAdapter = JournalAdapter(requireContext())
        journal.adapter = journalAdapter

        datas.apply {
            add(JournalData(name = "초록이", journal = "일지"))
            add(JournalData(name = "무럭이", journal = "일지"))
            add(JournalData(name = "완전멋진이름", journal = "일지"))
            add(JournalData(name = "초록이", journal = "일지"))
            add(JournalData(name = "무럭이", journal = "일지"))

            journalAdapter.datas = datas
            journalAdapter.notifyDataSetChanged()

        }
    }
}