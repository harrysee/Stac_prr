package kr.hs.emirim.w2015.stac_prr.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.hs.emirim.w2015.stac_prr.ImageDialog
import kr.hs.emirim.w2015.stac_prr.JournalDialog
import kr.hs.emirim.w2015.stac_prr.R

class GalleryAdapter(private val context: Context) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    var datas = ArrayList<String?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(this.context).inflate(R.layout.gallery_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val gallery: ImageView = itemView.findViewById(R.id.gallery_img)

        fun bind(item: String?) {
            if (item!= null){
                Glide.with(context)
                    .load(item)
                    .fitCenter()
                    .into(gallery)
            }
            itemView.setOnClickListener{
                val dir = ImageDialog(context)
                    .setImg(item)
                    .show()
            }
        }
    }
}