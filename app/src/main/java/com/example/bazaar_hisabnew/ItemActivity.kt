package com.example.bazaar_hisabnew

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bazaar_hisabnew.db.Database
import com.example.bazaar_hisabnew.db.Item
import com.example.bazaar_hisabnew.db.ItemAdapter
import kotlinx.android.synthetic.main.activity_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ItemActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    private var totalCost: Double = 0.0
    private lateinit var getItem: MutableList<Item>
    private lateinit var adapter: ItemAdapter
    private var budget: String = ""

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        job = Job()

        sharedPreferences =
            getSharedPreferences("com.example.bazaar_hisabnew.id", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        val listName = intent.getStringExtra("listName")
        pageTitleTV.text = listName

        val id = intent.getStringExtra("id")

        editor.putString("id", id)
        editor.putString("listName", listName)
        editor.apply()

        launch {
            applicationContext?.let {

                budget = Database(it).listDao().getBudget(id.toInt())
                budgetTV.text = budget
                accountingBudgetTV.text = budget

                val getAllCost: List<String> = Database(it).itemDao().getAllCost(id.toString())
                for (i in getAllCost.indices) {
                    totalCost += (getAllCost[i].toDouble())
                }

                totalCostTV.text = totalCost.toString()
                accountingTotalCostTV.text = totalCost.toString()

                val remaining = budget.toDouble() - totalCost
                remainingCostTV.text = remaining.toString()

                if (remaining < 0.0) {
                    remainingCostTV.setTextColor(Color.parseColor("#E31000"))
                }
            }
        }
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)


        launch {
            applicationContext?.let {
                getItem = Database(it).itemDao().getItem(id.toString())
                adapter = ItemAdapter(getItem, this@ItemActivity)
                adapter.notifyDataSetChanged()
                itemsRecyclerView.adapter = adapter

            }
        }

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    adapter.remove(viewHolder)

                    Handler(Looper.getMainLooper()).postDelayed({
                        launch {
                            applicationContext?.let {
                                totalCost = 0.0
                                val getAllCost : List<String> = Database(it).itemDao().getAllCost(id.toString())
                                for (i in getAllCost.indices){
                                    totalCost += (getAllCost[i].toDouble())
                                }

                                totalCostTV.text = totalCost.toString()
                                accountingTotalCostTV.text = totalCost.toString()

                                val remaining = budget.toDouble() - totalCost
                                remainingCostTV.text = remaining.toString()

                                if(remaining<0.0){
                                    remainingCostTV.setTextColor(Color.parseColor("#E31000"))
                                }
                            }
                        }
                    },2500)

                }

            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(itemsRecyclerView)


        val bottomSheetFragmentItemActivity = BottomSheetFragmentItemActivity()
        addItemButton.setOnClickListener {
            bottomSheetFragmentItemActivity.show(
                supportFragmentManager,
                "item activity bottom sheet fragment"
            )
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    override fun onBackPressed() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}