package com.example.bazaar_hisabnew

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bazaar_hisabnew.db.Database
import com.example.bazaar_hisabnew.db.ListsAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(),CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = Job()

        val bottomSheetFragmentMainActivity = BottomSheetFragmentMainActivity()

        newListButton.setOnClickListener {
            bottomSheetFragmentMainActivity.show(supportFragmentManager,"main activity bottom sheet fragment")
        }

        val toolbar : Toolbar = findViewById(R.id.main_activity_toolbar)
        toolbar.title = "বাজার-হিসাব"
        setSupportActionBar(toolbar)

        listsRecyclerView.layoutManager = LinearLayoutManager(this)

        launch {
            applicationContext?.let {
                val getListName = Database(it).listDao().getLists()
                val adapter = ListsAdapter(getListName)
                listsRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}