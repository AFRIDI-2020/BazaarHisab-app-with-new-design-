package com.example.bazaar_hisabnew

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bazaar_hisabnew.db.Database
import com.example.bazaar_hisabnew.db.Lists
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.main_activity_bottom_sheet_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class BottomSheetFragmentMainActivity : BottomSheetDialogFragment(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_activity_bottom_sheet_fragment,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        job = Job()

        createNewList.setOnClickListener {
            val newListName = newListNameET.text.toString()
            val budget = budgetET.text.toString()

            if(newListName.isEmpty()){
                newListNameET.error = "নতুন তালিকার নাম লিখুন"
                return@setOnClickListener
            }
            if(budget.isEmpty()){
                budgetET.error = "আপনার বাজেট কত টাকা?"
                return@setOnClickListener
            }

            launch {
                context?.let {
                    Database(it).listDao().addList(Lists(newListName,budget))
                    it.toast("new list added")
                    newListNameET.error = null
                    budgetET.error = null
                    newListNameET.text = null
                    budgetET.text = null
                    val intent = Intent(it,MainActivity::class.java)
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