package kr.hs.emirim.w2015.stac_prr.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.hs.emirim.w2015.stac_prr.NoticeData
import kr.hs.emirim.w2015.stac_prr.R

class SetNoticeAdapter(private val context: Context) : RecyclerView.Adapter<SetNoticeAdapter.ViewHolder>() {

    var datas = mutableListOf<NoticeData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.notice_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val txtName: TextView = itemView.findViewById(R.id.notice_title_text)
        private val txtAge: TextView = itemView.findViewById(R.id.notice_main_text)

        fun bind(item: NoticeData) {
            txtName.text = item.title
            txtAge.text = item.main.toString()
        }
    }


}