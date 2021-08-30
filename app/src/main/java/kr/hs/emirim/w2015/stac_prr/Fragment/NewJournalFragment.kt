package kr.hs.emirim.w2015.stac_prr.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_plan.*
import kotlinx.android.synthetic.main.fragment_new_journal.*
import kr.hs.emirim.w2015.stac_prr.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.util.*


class NewJournalFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cal = Calendar.getInstance()
        var y = 0
        var m = 0
        var d = 0

        y = cal[Calendar.YEAR]
        m = cal[Calendar.MONTH] + 1
        d = cal[Calendar.DAY_OF_MONTH]

        now_date.text = ("$y. $m. $d")

        val activity = activity as MainActivity
        R.style.AlertDialog_AppCompat

        // 이미지 화살표 눌렀을때
        addjournal_pass_btn.setOnClickListener(){
            val dir = CustomDialog(requireContext())
                .setMessage("작성중인 내용이 사라집니다\n취소하시겠습니까?")
                .setPositiveBtn("네"){
                    activity.fragmentChange_for_adapter(JournalFragment())
                }
                .setNegativeBtn("아니오"){}
                .show()
        }

        // 완료 눌렀을 때
        addjournal_complate_btn.setOnClickListener{
            activity.fragmentChange_for_adapter(JournalFragment())
        }
    }
}