package kr.hs.emirim.w2015.stac_prr.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_new_plant.*
import kr.hs.emirim.w2015.stac_prr.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R

class NewPlantFragment : Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_new_plant, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        
        // 이미지 화살표 눌렀을때
        img_btn_backhome.setOnClickListener(){
            val dir = CustomDialog(requireContext())
                .setMessage("진짜 다썻니?")
                .setPositiveBtn("네"){
                    activity.fragmentChange_for_adapter(HomeFragment())
                }
                .setNegativeBtn("아니오"){}
                .show()
        }
        
        // 완료 눌렀을 때
        btn_completion.setOnClickListener{
            activity.fragmentChange_for_adapter(HomeFragment())
        }

    }


}
