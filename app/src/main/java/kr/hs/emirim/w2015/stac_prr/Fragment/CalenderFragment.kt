package kr.hs.emirim.w2015.stac_prr.Fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import kotlinx.android.synthetic.main.fragment_calender.*
import kotlinx.android.synthetic.main.fragment_calender.view.*
import kr.hs.emirim.w2015.stac_prr.R
import java.lang.String
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CalenderFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val img_pre :Drawable? = ContextCompat.getDrawable(requireContext(),R.drawable.calendar_prebtn)
        val img_fw :Drawable? = ContextCompat.getDrawable(requireContext(),R.drawable.calendar_forbtn)
        calendarView.setPreviousButtonImage(img_pre)
        calendarView.setForwardButtonImage(img_fw)

        val date = DatePickerBuilder(requireContext(), {  })
            .setTodayColor(R.color.today_green)

    }

}

