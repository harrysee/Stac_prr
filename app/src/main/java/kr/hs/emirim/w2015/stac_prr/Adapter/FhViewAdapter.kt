package kr.hs.emirim.w2015.stac_prr.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.slider_item.view.*
import kr.hs.emirim.w2015.stac_prr.R


class FhViewAdapter(a : ArrayList<Int>, newtext : ArrayList<String>) : RecyclerView.Adapter<FhViewAdapter.MyViewholder>() {
    var item = a
    var ntxt = newtext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewholder(parent)

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        holder.imageView.setImageResource(item[position])
        holder.textView.setText(ntxt[position])
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class MyViewholder(parent:ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)) {
        val imageView = itemView.img_register_plant
        val textView = itemView.text_register_plant

    }
}

