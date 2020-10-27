package com.example.bazaar_hisabnew

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bazaar_hisabnew.db.Database
import com.example.bazaar_hisabnew.db.Item
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.item_activity_bottom_sheet_fragment.*
import kotlinx.android.synthetic.main.show_list_one_by_one.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class BottomSheetFragmentItemActivity : BottomSheetDialogFragment(), CoroutineScope {

    private lateinit var job: Job
    private lateinit var sharedPreferences: SharedPreferences
    private var finalCost : String = ""
    private var costPerUnit : String = ""


    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_activity_bottom_sheet_fragment,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = this.activity!!.getSharedPreferences("com.example.bazaar_hisabnew.id", Context.MODE_PRIVATE)
        job = Job()

        val listId  = sharedPreferences.getString("id","").toString()
        val listName = sharedPreferences.getString("listName","").toString()

        includeItemTV.setOnClickListener {
            val itemName = itemNameET.text.toString()
            val quantity = quantityET.text.toString()
            var costPerUnit = costPerUnitET.text.toString()
            val unit = unitET.text.toString()

            if(itemName.isEmpty()){
                itemNameET.error = "পণ্যের নাম লিখুন"
                return@setOnClickListener
            }

            if(quantity.isEmpty()){
                quantityET.error = "পণ্যের নাম লিখুন"
                return@setOnClickListener
            }

            if(unit.isEmpty()){
                unitET.error = "পণ্যের নাম লিখুন"
                return@setOnClickListener
            }

            if(costPerUnit.isEmpty()){
                costPerUnit = "0"
            }

            val quantityInDouble : Double = quantity.toDouble()
            val costPerUnitInDouble : Double = costPerUnit.toDouble()
            val cost : Double = quantityInDouble * costPerUnitInDouble
            val finalCost = cost.toString()


            launch {
                context?.let {
                    Database(it).itemDao().addItem(Item(listId,itemName,quantity,unit,costPerUnit,finalCost,false))
                    itemNameET.error = null
                    quantityET.error = null
                    unitET.error = null
                    costPerUnitET.error = null
                    itemNameET.text = null
                    quantityET.text = null
                    unitET.text = null
                    costPerUnitET.text = null

                    val intent = Intent(it, ItemActivity::class.java)
                    intent.putExtra("id",listId)
                    intent.putExtra("listName",listName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    it.startActivity(intent)
                }
            }
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}