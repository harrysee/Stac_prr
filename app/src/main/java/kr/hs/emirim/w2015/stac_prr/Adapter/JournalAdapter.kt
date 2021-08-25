package kr.hs.emirim.w2015.stac_prr.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.hs.emirim.w2015.stac_prr.Fragment.JournalFragment
import kr.hs.emirim.w2015.stac_prr.JournalData
import kr.hs.emirim.w2015.stac_prr.R

class JournalAdapter(private val context: Context) :
    RecyclerView.Adapter<JournalAdapter.ViewHolder>() {
        var datas = mutableListOf<JournalData>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(this.context).inflate(R.layout.journal_item, parent,false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = datas.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(datas[position])
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val txtName: TextView = itemView.findViewById(R.id.journal_title)
            private val txtJournal: TextView = itemView.findViewById(R.id.journal_content)

            fun bind(item: JournalData) {
                txtName.text = item.name
                txtJournal.text = item.journal

            }
        }
    }
