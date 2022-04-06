package kr.hs.emirim.w2015.stac_prr.View.Adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.slider_item.view.*
import kr.hs.emirim.w2015.stac_prr.Model.HomeModel
import kr.hs.emirim.w2015.stac_prr.View.Fragment.PlantInfoFragment
import kr.hs.emirim.w2015.stac_prr.R


class FhViewAdapter(var datas : ArrayList<HomeModel>, val fragment_s:FragmentActivity?, val context:Context) : RecyclerView.Adapter<FhViewAdapter.MyViewholder>() {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewholder(parent)

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        Log.d("TAG", "onBindViewHolder: 식물정보 넘겨줄때 데이터 : ${datas.get(position)}")
        holder.bind(datas.get(position),context)
        holder.itemView.setOnClickListener{
            Log.d(">>>>."+position.toString(), "클릭됨 ")
            val fragment:Fragment= PlantInfoFragment()
            val bundle: Bundle = Bundle()
            bundle.putString("imgUri",datas.get(position).imgUrl)
            bundle.putString("docId", datas.get(position).docId)
            fragment.arguments = bundle

            fragment_s?.supportFragmentManager!!.beginTransaction()
                .replace(R.id.container,fragment)
                .commit()
            Log.d("프레그먼트", "프레그먼트 갔다옴 ")
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class MyViewholder(parent:ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)) {
        val imageView = itemView.img_register_plant
        val textView = itemView.text_register_plant
        val textSpace = itemView.text_register_spacies

        fun bind(item: HomeModel, context : Context){
            Log.d("TAG", "bind: 식물정보 어댑터 실행됨")
            //imageView.setImageResource()
            imageView.setImageResource(R.drawable.ic_home_emty_item)
            if (item.imgUrl != null){
                Glide.with(context) //쓸곳
                    .load(item.imgUrl.toString())  //이미지 받아올 경로
                    .fitCenter()        // 가운데 잘라서 채우게 가져오기
                    .placeholder(R.drawable.ic_home_emty_item)
                    .into(imageView)    // 받아온 이미지를 받을 공간
            }

            textView.text = item.name
            textSpace.text = "#"+item.spacies
            Log.d(item.name, "바인드 실행 : ")

        }

    }
}

