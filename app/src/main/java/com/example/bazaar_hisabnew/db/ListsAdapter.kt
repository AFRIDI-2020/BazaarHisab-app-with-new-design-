package com.example.bazaar_hisabnew.db


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.bazaar_hisabnew.ItemActivity
import com.example.bazaar_hisabnew.MainActivity
import com.example.bazaar_hisabnew.R
import com.example.bazaar_hisabnew.toast
import kotlinx.android.synthetic.main.edit_list_name_budget.view.*
import kotlinx.android.synthetic.main.show_list_one_by_one.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class ListsAdapter(val lists: MutableList<Lists>, val context: Context) : RecyclerView.Adapter<ListsAdapter.ViewHolder>(),CoroutineScope {

    private lateinit var job: Job
    private var removedPosition : Int = 0
    private var removedItem : Lists? = null

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.show_list_one_by_one, parent, false)
        )
    }

    fun removeList(viewHolder: RecyclerView.ViewHolder){
        removedPosition = viewHolder.adapterPosition
        removedItem = lists[removedPosition]

        launch {
            context?.let {
                Database(it).listDao().deleteList(removedItem!!)
            }
        }

        lists.removeAt(removedPosition)
        notifyItemRemoved(removedPosition)
        val intent = Intent(context,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        job = Job()

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

        holder.view.editIcon.setOnClickListener {
            AlertDialog.Builder(context).apply {
                val view = LayoutInflater.from(context).inflate(R.layout.edit_list_name_budget,null)
                setView(view)
                view._listNameET.setText(lists[position].listName)
                view._budgetET.setText(lists[position].budget)

                view._okTextView.setOnClickListener {
                    val _listName = view._listNameET.text.toString()
                    val _budget = view._budgetET.text.toString()

                    val list = Lists(_listName,_budget)

                    list.id = lists[position].id

                    launch {
                        context.let {
                            Database(it).listDao().updateList(list)
                            val intent = Intent(it, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            it.startActivity(intent)
                        }
                    }
                }


            }.show()
        }

    }


}