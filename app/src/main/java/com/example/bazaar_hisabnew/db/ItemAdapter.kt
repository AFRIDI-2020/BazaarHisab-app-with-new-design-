package com.example.bazaar_hisabnew.db

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.bazaar_hisabnew.ItemActivity
import com.example.bazaar_hisabnew.R
import com.example.bazaar_hisabnew.toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.edit_item.view.*
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

        val check = items[position].isChecked

        if(!check){
            holder.view.itemNameTV.setTextColor(Color.parseColor("#000000"))
        }else{
            holder.view.itemNameTV.setTextColor(Color.parseColor("#00994C"))
        }

        val item_name = items[position].itemName
        val quantity = items[position].quantity
        val list_id = items[position].listId
        val unit = items[position].unit
        val cost_per_unit = items[position].costPerUnit
        val cost = items[position].cost

        holder.itemView.setOnClickListener {

            launch {
                context.let {
                    var isChecked : Boolean  = Database(it).itemDao().textIsChecked(items[position].id)
                    if(!isChecked){
                        holder.view.itemNameTV.setTextColor(Color.parseColor("#00994C"))
                        isChecked = true
                        val item = Item(list_id,item_name,quantity,unit,cost_per_unit,cost,isChecked)
                        item.id = items[position].id
                        Database(it).itemDao().updateItem(item)
                    }else{
                        holder.view.itemNameTV.setTextColor(Color.parseColor("#000000"))
                        isChecked = false
                        val item = Item(list_id,item_name,quantity,unit,cost_per_unit,cost,isChecked)
                        item.id = items[position].id
                        Database(it).itemDao().updateItem(item)

                    }
                }
            }

        }

        holder.itemView.setOnLongClickListener {

            AlertDialog.Builder(context).apply {
                val view = LayoutInflater.from(context).inflate(R.layout.edit_item,null)
                setView(view)
                view._itemNameET.setText(items[position].itemName)
                view._quantityET.setText(items[position].quantity)
                view._unitET.setText(items[position].unit)
                view._costPerUnitET.setText(items[position].costPerUnit)

                view._includeItemTV.setOnClickListener {
                    val _itemName = view._itemNameET.text.toString()
                    val _quantity = view._quantityET.text.toString()
                    val _unit = view._unitET.text.toString()
                    val _costPerUnit = view._costPerUnitET.text.toString()
                    val _listId = items[position].listId

                    val _quantityInDouble : Double = _quantity.toDouble()
                    val _costPerUnitInDouble : Double = _costPerUnit.toDouble()
                    val _cost : Double = _quantityInDouble * _costPerUnitInDouble
                    val _finalCost = _cost.toString()

                    val item = Item(_listId,_itemName,_quantity,_unit,_costPerUnit,_finalCost,false)

                    item.id = items[position].id

                    launch {
                        context.let {
                            val _listName = Database(it).listDao().getListName(_listId.toInt())
                            Database(it).itemDao().updateItem(item)
                            val intent = Intent(it, ItemActivity::class.java)
                            intent.putExtra("id", _listId)
                            intent.putExtra("listName",_listName)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            it.startActivity(intent)

                        }
                    }
                }
            }.show()

            true
        }




    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }
}