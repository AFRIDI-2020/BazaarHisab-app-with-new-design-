package com.example.bazaar_hisabnew.db

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bazaar_hisabnew.R
import kotlinx.android.synthetic.main.show_item_one_by_one.view.*

class ItemAdapter(val items : List<Item>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.show_item_one_by_one,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.itemNameTV.text = items[position].itemName
        holder.view.costPerUnitTV.text = items[position].costPerUnit
        holder.view.quantityTV.text = items[position].quantity
        holder.view.itemCostTV.text = items[position].cost
    }
}