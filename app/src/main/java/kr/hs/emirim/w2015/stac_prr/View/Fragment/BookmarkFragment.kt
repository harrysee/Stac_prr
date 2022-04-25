package kr.hs.emirim.w2015.stac_prr.View.Fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_bookmark.*
import kotlinx.android.synthetic.main.fragment_set_iot.*
import kotlinx.android.synthetic.main.fragment_set_iot.iot_pass_btn
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.View.Adapter.JournalAdapter
import kr.hs.emirim.w2015.stac_prr.databinding.FragmentJournalBinding
import kr.hs.emirim.w2015.stac_prr.viewModel.JournalViewModel


class BookmarkFragment : Fragment() {
    private lateinit var journalAdapter: JournalAdapter
    private lateinit var binding : FragmentJournalBinding
    private var dateSort = false    //false-내림차순 / true -오름차순
    private val model by lazy{
        ViewModelProvider(requireActivity()).get(JournalViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_journal_btn.setOnClickListener(){
            val activity = activity as MainActivity
            activity.fragmentChange_for_adapter(JournalFragment())
        }
    }

    private fun initRecycler(name: String?) {
        Log.i(ContentValues.TAG, "initRecycler: 한개씩 보여줌"+dateSort)
        if (name != null) {
            Log.i("TAG", "initRecycler: 파이어베이스 일지 가져오기"+dateSort)
            model.getJournals(dateSort,name).observe(requireActivity(), Observer {
                journalAdapter = JournalAdapter(requireContext(),activity,model)
                journalAdapter.datas = it
                binding.journalRecycler.adapter = journalAdapter
                journalAdapter.notifyDataSetChanged()
            })
        }
    }
}