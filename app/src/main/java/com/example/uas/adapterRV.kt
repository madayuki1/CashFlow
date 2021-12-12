package com.example.firebasedemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uas.R
import com.example.uas.dcTrasaction
import org.w3c.dom.Text

class adapterRV (private val listNotes : ArrayList<dcTrasaction>):
    RecyclerView.Adapter<adapterRV.ListViewHolder>(){

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tv_date : TextView = itemView.findViewById(R.id.tv_date)
        var tv_category : TextView = itemView.findViewById(R.id.tv_category)
        var tv_transaction_type : TextView = itemView.findViewById(R.id.tv_transaction_type)
        var tv_cash : TextView = itemView.findViewById(R.id.tv_cash)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        var view : View =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_item,parent,false)
        return  ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var notes = listNotes[position]
        holder.tv_date.setText(notes.date)
        holder.tv_category.setText(notes.category_name)
        holder.tv_transaction_type.setText(notes.transaction_type)
        holder.tv_cash.setText(notes.cash)

    }

    override fun getItemCount(): Int {
        return listNotes.size
    }
}