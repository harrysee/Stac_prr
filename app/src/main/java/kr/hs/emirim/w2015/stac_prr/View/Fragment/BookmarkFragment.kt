package kr.hs.emirim.w2015.stac_prr.View.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_bookmark.*
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.View.Adapter.JournalAdapter
import kr.hs.emirim.w2015.stac_prr.databinding.FragmentBookmarkBinding
import kr.hs.emirim.w2015.stac_prr.viewModel.JournalViewModel


class BookmarkFragment : Fragment() {
    private lateinit var bookmarkAdapter: JournalAdapter
    private lateinit var binding : FragmentBookmarkBinding
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
        binding = FragmentBookmarkBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.getBookmarks().observe(requireActivity(), Observer {
            bookmarkAdapter = JournalAdapter(requireContext(),activity,model)
            Log.i("TAG", "북마크: 북마크 가져오기"+it)
            bookmarkAdapter.datas = it
            binding.bookmarkRecycler.adapter = bookmarkAdapter
            bookmarkAdapter.notifyDataSetChanged()
        })
        back_journal_btn.setOnClickListener(){
            val activity = activity as MainActivity
            activity.fragmentChange_for_adapter(JournalFragment())
        }
    }

}