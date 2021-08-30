package kr.hs.emirim.w2015.stac_prr.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.hs.emirim.w2015.stac_prr.NoticeData
import kr.hs.emirim.w2015.stac_prr.R
import kotlin.math.log


class SetNoticeAdapter(private val context: Context) :
    RecyclerView.Adapter<SetNoticeAdapter.ViewHolder>() {

    var datas = mutableListOf<NoticeData>()
    var updown: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.notice_item, parent, false)
        Log.d(viewType.toString(), "NoticeAdapter 실행")
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(updown.toString(), "현재 닫혀있는지 ")
        holder.bind(datas[position])
        Log.i(datas[position].toString(), "onBindViewHolder: 데이터 확인 ")
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtName: TextView = itemView.findViewById(R.id.notice_title_text)
        private val txtDate : TextView = itemView.findViewById(R.id.notice_date_text)
        private val txtMain: TextView = itemView.findViewById(R.id.notice_main_text)
        private val arrowBtn: ImageButton = itemView.findViewById(R.id.notice_arrow_btn)
        private val mainLinear: LinearLayout = itemView.findViewById(R.id.notice_main_linear)

        fun bind(item: NoticeData) {
            Log.d(item.title, "bind: 데이터 들어옴")
            txtName.text = item.title
            txtDate.text = item.date
            txtMain.text = item.main
            Log.d("${txtName.text} - ${txtDate.text} - ${txtMain.text}", "bind: item 데이터 bind 확인")

            arrowBtn.setOnClickListener() {
                if(!updown){
                    //mainLinear.animation = AnimationUtils.loadAnimation(context, R.anim.open)
                    //mainLinear.layoutParams.height = 500;
                    val params = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )
                    mainLinear.layoutParams = params
                    arrowBtn.setImageResource(R.drawable.close_btn)
                    txtMain.textSize = 16.0F
                    Log.d(txtName.textSize.toString(), "바뀐 높이")
                    updown = true
                }else if(updown){
                    //mainLinear.animation = AnimationUtils.loadAnimation(context, R.anim.close) 
                    /*val display = DisplayMetrics()
                    windowManager.defaultDisplay.getMetrics(display)*/
                    val params = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        0
                    )
                    mainLinear.layoutParams = params
                    arrowBtn.setImageResource(R.drawable.open_notice)
                    txtMain.textSize = 16.0F
                    Log.d(txtMain.textSize.toString(), "바뀐 높이")
                    updown = false
                }

            }
        }
    }


}