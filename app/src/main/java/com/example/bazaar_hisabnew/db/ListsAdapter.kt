package com.example.bazaar_hisabnew.db


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bazaar_hisabnew.ItemActivity
import com.example.bazaar_hisabnew.R
import kotlinx.android.synthetic.main.show_list_one_by_one.view.*


class ListsAdapter(val lists: List<Lists>) : RecyclerView.Adapter<ListsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.show_list_one_by_one, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.view.listName.text = lists[position].listName
        holder.view.listNumber.text = (position + 1).toString()
        val listName = lists[position].listName
        val id = lists[position].id

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context,ItemActivity::class.java)
            intent.putExtra("listName",listName)
            intent.putExtra("id",id.toString())
            it.context.startActivity(intent)
        }
    }


}