package com.example.bazaar_hisabnew.db

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bazaar_hisabnew.ItemActivity
import com.example.bazaar_hisabnew.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.show_item_one_by_one.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ItemAdapter(val items : MutableList<Item>,val context: Context) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(), CoroutineScope {

    private lateinit var job: Job
    private var removedPosition : Int = 0
    private var removedItem : Item? = null

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.show_item_one_by_one,parent,false)
        )
    }

    fun remove(viewHolder: RecyclerView.ViewHolder){

        removedPosition = viewHolder.adapterPosition
        removedItem = items[removedPosition]

        launch {
            context?.let {
                Database(it).itemDao().deleteItem(removedItem!!)
            }
        }

        items.removeAt(removedPosition)
        notifyItemRemoved(removedPosition)



        Snackbar.make(viewHolder.itemView, "পণ্যটি বাদ দেওয়া হয়েছে।", Snackbar.LENGTH_LONG).setAction("বাদ দেওয়া না হোক"){


            launch {
                context.let {
                    Database(it).itemDao().addItem(removedItem!!)
                    items.add(removedItem!!)
                    notifyDataSetChanged()
                }
            }
        }.show()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        job = Job()

        holder.view.itemNameTV.text = items[position].itemName
        holder.view.costPerUnitTV.text = items[position].costPerUnit
        holder.view.quantityTV.text = items[position].quantity
        holder.view.itemCostTV.text = items[position].cost
        holder.view.unitTV.text = items[position].unit
    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }
}