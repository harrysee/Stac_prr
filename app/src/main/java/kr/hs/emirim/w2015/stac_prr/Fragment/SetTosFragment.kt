package kr.hs.emirim.w2015.stac_prr.Fragment

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_set_tos.*
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R

class SetTosFragment : Fragment() {
    lateinit var content: String
    lateinit var spannableString : SpannableString

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_tos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        content = tos_txt.text.toString()
        spannableString  = SpannableString(content)
        val title_list = listOf<String>(
            "제1조(개인정보의 처리 목적)",
            "제2조(개인정보의 처리 및 보유 기간)",
            "제3조(개인정보의 제3자 제공)",
            "제4조(정보주체와 법정대리인의 권리·의무 및 그 행사방법)",
            "제5조(처리하는 개인정보의 항목 작성)",
            "제6조(개인정보의 파기)",
            "제7조(개인정보의 안전성 확보 조치)",
            "제8조(개인정보 자동 수집 장치의 설치•운영 및 거부에 관한 사항)",
            "제9조 (개인정보 보호책임자)",
            "제10조(개인정보 열람청구)",
            "제11조(권익침해 구제방법)"
        )
        for( t in title_list){
            setTitleColor(t)
            Log.d(t.toString(), "호출됨  ")
        }

       // setTitleColor("제1조(개인정보의 처리 목적)")

        tos_pass_btn.setOnClickListener(){
            val main = activity as MainActivity
            main.fragmentChange_for_adapter(SetFragment())
        }

    }
    fun setTitleColor(word : String){ //지정할 텍스트 넣기
        val word =word
        val start = content.indexOf(word)   //시작위치
        val end = start + word.length       //끝위치

        // 3
        spannableString.setSpan(    
            ForegroundColorSpan(Color.parseColor("#8EC057")),   //컬러지정
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            RelativeSizeSpan(1.1f),
            start,
            end,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // 4
        tos_txt.text =(spannableString)
    }
}