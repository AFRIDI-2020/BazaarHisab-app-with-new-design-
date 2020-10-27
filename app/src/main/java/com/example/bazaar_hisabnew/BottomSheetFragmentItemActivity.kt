package com.example.bazaar_hisabnew

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private var finalCost: String = ""
    private var costPerUnit: String = ""


    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_activity_bottom_sheet_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = this.activity!!.getSharedPreferences(
            "com.example.bazaar_hisabnew.id",
            Context.MODE_PRIVATE
        )
        job = Job()

        val listId = sharedPreferences.getString("id", "").toString()
        val listName = sharedPreferences.getString("listName", "").toString()

        val unitLists = arrayOf(
            getString(R.string.spinner_kg),
            getString(R.string.spinner_gram),
            getString(R.string.spinner_liter),
            getString(R.string.spinner_mili),
            getString(R.string.spinner_piece),
            getString(R.string.spinner_number),
            getString(R.string.spinner_hali),
            getString(R.string.spinner_dozen),
            getString(R.string.spinner_ati),
            getString(R.string.spinner_packet),
            getString(R.string.spinner_bag),
            getString(R.string.spinner_yard),
            getString(R.string.spinner_feet),
            getString(R.string.spinner_unit)
        )

        var theUnit: String = ""

        val unitArrayAdapter =
            ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, unitLists)
        unit_spinner.adapter = unitArrayAdapter

        unit_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                theUnit = unitLists[position]
            }

        }

        includeItemTV.setOnClickListener {
            val itemName = itemNameET.text.toString()
            val quantity = quantityET.text.toString()
            var costPerUnit = costPerUnitET.text.toString()
            if (itemName.isEmpty()) {
                itemNameET.error = "পণ্যের নাম লিখুন"
                return@setOnClickListener
            }

            if (quantity.isEmpty()) {
                quantityET.error = "পণ্যের নাম লিখুন"
                return@setOnClickListener
            }


            if (costPerUnit.isEmpty()) {
                costPerUnit = "0"
            }

            val quantityInDouble: Double = quantity.toDouble()
            val costPerUnitInDouble: Double = costPerUnit.toDouble()
            val cost: Double = quantityInDouble * costPerUnitInDouble
            val finalCost = cost.toString()


            launch {
                context?.let {
                    Database(it).itemDao().addItem(
                        Item(
                            listId,
                            itemName,
                            quantity,
                            theUnit,
                            costPerUnit,
                            finalCost,
                            false
                        )
                    )
                    itemNameET.error = null
                    quantityET.error = null
                    costPerUnitET.error = null
                    itemNameET.text = null
                    quantityET.text = null
                    costPerUnitET.text = null

                    val intent = Intent(it, ItemActivity::class.java)
                    intent.putExtra("id", listId)
                    intent.putExtra("listName", listName)
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