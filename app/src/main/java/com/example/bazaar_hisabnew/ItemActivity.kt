package com.example.bazaar_hisabnew

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bazaar_hisabnew.db.Database
import com.example.bazaar_hisabnew.db.ItemAdapter
import kotlinx.android.synthetic.main.activity_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ItemActivity : AppCompatActivity(),CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        job = Job()

        sharedPreferences = getSharedPreferences("com.example.bazaar_hisabnew.id", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()

        val listName = intent.getStringExtra("listName")
        pageTitleTV.text = listName

        val id  = intent.getStringExtra("id")

        editor.putString("id",id)
        editor.putString("listName",listName)
        editor.apply()

        launch {
            applicationContext?.let {

                val budget = Database(it).listDao().getBudget(id.toInt())
                budgetTV.text = budget
            }
        }
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)

        launch {
            applicationContext?.let {
                val getItem = Database(it).itemDao().getItem(id)
                val adapter = ItemAdapter(getItem)
                adapter.notifyDataSetChanged()
                itemsRecyclerView.adapter = adapter

            }
        }


        val bottomSheetFragmentItemActivity = BottomSheetFragmentItemActivity()
        addItemButton.setOnClickListener {
            bottomSheetFragmentItemActivity.show(supportFragmentManager, "item activity bottom sheet fragment")
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    override fun onBackPressed() {

        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

}