package kr.hs.emirim.w2015.stac_prr.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_set.*
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SetFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val main = activity as MainActivity
        set_imgbtn_nugu.setOnClickListener(){
            main.fragmentChange_for_adapter(SetTosFragment())
        }
        set_imgbtn_iot.setOnClickListener(){
            main.fragmentChange_for_adapter(SetTosFragment())
        }
        set_imgbtn_notice.setOnClickListener(){
            main.fragmentChange_for_adapter(SetNoticeFragment())
        }
        set_imgbtn_ask.setOnClickListener(){
            main.fragmentChange_for_adapter(SetTosFragment())
        }
        set_imgbtn_tos.setOnClickListener(){
            main.fragmentChange_for_adapter(SetTosFragment())
        }
    }
}