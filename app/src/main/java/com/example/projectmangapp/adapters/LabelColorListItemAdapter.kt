package com.example.projectmangapp.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmangapp.R
import kotlinx.android.synthetic.main.item_label_color.view.*

class LabelColorListItemAdapter(
    private val context :Context,
    private var list :ArrayList<String>,
    private val mSelectedColor:String)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: MyViewHolder2.OnItemClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder2(LayoutInflater.from(context).inflate(R.layout.item_label_color, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if(holder is MyViewHolder2){
            holder.itemView.view_main.setBackgroundColor(Color.parseColor(item))
            if(item == mSelectedColor){
                holder.itemView.iv_selected_color.visibility = View.VISIBLE
            }
            else{
                holder.itemView.iv_selected_color.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if(onItemClickListener !=null){
                    onItemClickListener!!.onClick(position, item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

   class MyViewHolder2(view : View):RecyclerView.ViewHolder(view){

        interface OnItemClickListener{
            fun onClick(position:Int, color: String)
        }

    }
}