package kr.hs.emirim.w2015.stac_prr.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kr.hs.emirim.w2015.stac_prr.JournalData
import kr.hs.emirim.w2015.stac_prr.R

class JournalTabAdapter(private val context: Context) :
    RecyclerView.Adapter<JournalTabAdapter.ViewHolder>() {
    var datas = mutableListOf<JournalData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(this.context).inflate(R.layout.tab_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtName: TextView = itemView.findViewById(R.id.plant_name)
        private val tab : ConstraintLayout = itemView.findViewById(R.id.journal_tab_item)

        @SuppressLint("ResourceAsColor")
        fun bind(item: JournalData) {
            txtName.text = item.name
            itemView.setOnClickListener(){
                tab.setBackgroundResource(R.color.pp_green)
                txtName.setTextColor(R.color.white)
            }
        }
    }
}