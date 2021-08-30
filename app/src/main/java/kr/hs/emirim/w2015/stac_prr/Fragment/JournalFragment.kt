package kr.hs.emirim.w2015.stac_prr.Fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_alarm.*
import kotlinx.android.synthetic.main.fragment_journal.*
import kotlinx.android.synthetic.main.fragment_plant_info.*
import kr.hs.emirim.w2015.stac_prr.Adapter.JournalAdapter
import kr.hs.emirim.w2015.stac_prr.Adapter.JournalTabAdapter
import kr.hs.emirim.w2015.stac_prr.JournalData
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R

class JournalFragment : Fragment() {
    lateinit var journalAdapter: JournalAdapter
    lateinit var journalTabAdapter: JournalTabAdapter
    private val datas = mutableListOf<JournalData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        journal_recycler.setOnScrollListener(onScrollListener)
    }

    val onScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                fab.hide()
            } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                fab.show()
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            fab.show()
        }
    }
    private fun initRecycler() {
        journalAdapter = JournalAdapter(requireContext())
        journal_recycler.adapter = journalAdapter
        journalTabAdapter = JournalTabAdapter(requireContext())
        tab_recycler.adapter = journalTabAdapter

        datas.add(JournalData(name = "초록이", journal = "일지"))
        datas.add(JournalData(name = "무럭이", journal = "일지"))
        datas.add(JournalData(name = "완전멋진이름", journal = "일지"))
        datas.add(JournalData(name = "초록이", journal = "일지"))
        datas.add(JournalData(name = "무럭이", journal = "일지"))
        datas.add(JournalData(name = "초록이", journal = "일지"))
        datas.add(JournalData(name = "무럭이", journal = "일지"))
        datas.add(JournalData(name = "완전멋진이름", journal = "일지"))
        datas.add(JournalData(name = "초록이", journal = "일지"))
        datas.add(JournalData(name = "무럭이", journal = "일지"))
        datas.add(JournalData(name = "초록이", journal = "일지"))
        datas.add(JournalData(name = "무럭이", journal = "일지"))
        datas.add(JournalData(name = "완전멋진이름", journal = "일지"))
        datas.add(JournalData(name = "초록이", journal = "일지"))
        datas.add(JournalData(name = "무럭이", journal = "일지"))

        journalAdapter.datas = datas
        journalAdapter.notifyDataSetChanged()

        journalTabAdapter.datas = datas
        journalTabAdapter.notifyDataSetChanged()


    }



}


