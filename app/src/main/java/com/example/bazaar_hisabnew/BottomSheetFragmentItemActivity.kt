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
        context?.toast(listId)

        includeItemTV.setOnClickListener {
            val itemName = itemNameET.text.toString()
            val quantity = quantityET.text.toString()
            val unit = unitET.text.toString()
            val costPerUnit = costPerUnitET.text.toString()
            val costPerUnitInt = (costPerUnitET.text.toString()).toInt()
            val quantityInt = quantity.toInt()
            val itemCost = (costPerUnitInt * quantityInt).toString()
            val finalQuantity = "$quantity $unit"

            if(itemName.isEmpty()){
                itemNameET.error = "পণ্যের নাম লিখুন"
                return@setOnClickListener
            }

            if(quantity.isEmpty()){
                quantityET.error = "পরিমাণ লিখুন"
                return@setOnClickListener
            }

            if(unit.isEmpty()){
                unitET.error = "পণ্যের পরিমাপন একক লিখুন"
                return@setOnClickListener
            }

            launch {
                context?.let {
                    Database(it).itemDao().addItem(Item(listId,itemName,finalQuantity,costPerUnit,itemCost))
                    itemNameET.error = null
                    quantityET.error = null
                    unitET.error = null
                    itemNameET.text = null
                    quantityET.text = null
                    unitET.text = null
                    val intent = Intent(it,ItemActivity::class.java)
                    intent.putExtra("listName",listName)
                    intent.putExtra("id",listId)
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