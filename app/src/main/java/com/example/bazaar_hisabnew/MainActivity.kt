package com.example.bazaar_hisabnew


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bazaar_hisabnew.db.Database
import com.example.bazaar_hisabnew.db.Lists
import com.example.bazaar_hisabnew.db.ListsAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    private lateinit var getListName: MutableList<Lists>
    private lateinit var adapter: ListsAdapter

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        job = Job()

        val bottomSheetFragmentMainActivity = BottomSheetFragmentMainActivity()

        newListButton.setOnClickListener {
            bottomSheetFragmentMainActivity.show(
                supportFragmentManager,
                "main activity bottom sheet fragment"
            )
        }

        val toolbar: Toolbar = findViewById(R.id.main_activity_toolbar)
        toolbar.title = resources.getString(R.string.app_name)
        setSupportActionBar(toolbar)

        listsRecyclerView.layoutManager = LinearLayoutManager(this)

        launch {
            applicationContext?.let {
                getListName = Database(it).listDao().getLists()
                adapter = ListsAdapter(getListName, this@MainActivity)
                listsRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
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
                    adapter.removeList(viewHolder)
                }

            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(listsRecyclerView)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.help -> {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
            }
            R.id.share ->{
                val message = "I am afridi"
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT,message)
                intent.type = "text/plain"

                startActivity(Intent.createChooser(intent,"শেয়ার করুন..."))
            }
            R.id.about ->{
                val intent = Intent(this,AboutActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        }

        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}