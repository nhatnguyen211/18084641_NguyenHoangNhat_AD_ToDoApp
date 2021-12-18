package com.example.todoapp



import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_recylerview_work.view.*


class Apdapter(var list:ArrayList<Work>) : RecyclerView.Adapter<Apdapter.RecyclerViewHolder>() {


    var ItemClickListener: ((position:Int,checked:String,cardView:CardView)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recylerview_work,parent,false)

        return  RecyclerViewHolder(itemView)

    }
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = list[position]
        holder.txtWork.text = currentItem.work
        ItemClickListener?.invoke(position,currentItem.checked.toString(),holder.cardView)

        if (currentItem.checked=="1"){
            val textColor = SpannableString("Complete")
            textColor.setSpan(ForegroundColorSpan(Color.GREEN),0,textColor.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.txtStatus.text = textColor
        }
        else{
            val textColor = SpannableString("Not Complete")
            textColor.setSpan(ForegroundColorSpan(Color.RED),0,textColor.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.txtStatus.text = textColor
        }

    }
    override fun getItemCount(): Int {
        return list.size
    }
    class RecyclerViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val txtWork: TextView = itemView.txtWorkRe
        val txtStatus: TextView = itemView.txtStatusRe
        val cardView: CardView = itemView.cardView

    }
    fun updateList(newList: ArrayList<Work>){
        this.list = newList
        notifyDataSetChanged()
    }
}