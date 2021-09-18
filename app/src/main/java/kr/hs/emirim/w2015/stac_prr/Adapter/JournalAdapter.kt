package kr.hs.emirim.w2015.stac_prr.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kr.hs.emirim.w2015.stac_prr.DataClass.JournalData
import kr.hs.emirim.w2015.stac_prr.R

class JournalAdapter(private val context: Context) :
    RecyclerView.Adapter<JournalAdapter.ViewHolder>() {
        var datas = mutableListOf<JournalData>()
        val storage = FirebaseStorage.getInstance()

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
            private val journalimg : ImageView = itemView.findViewById(R.id.journal_img)
            private val journaldate : TextView = itemView.findViewById(R.id.journal_item_date)

            fun bind(item: JournalData) {
                txtName.text = item.name
                txtJournal.text = item.journal
                journaldate.text = item.date

                if (item.imgUri != null){
                    Glide.with(context)
                        .load(item.imgUri)
                        .fitCenter()
                        .into(journalimg)
                }
            }
        }
    }
