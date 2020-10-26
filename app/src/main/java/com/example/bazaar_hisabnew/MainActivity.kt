package com.example.bazaar_hisabnew

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
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
import java.util.*
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    private lateinit var getListName: MutableList<Lists>
    private lateinit var adapter: ListsAdapter

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadLocale()

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

        when(item.itemId){
            R.id.help ->
            {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
            }
            R.id.language ->
            {
                val languages = arrayOf("বাংলা","English")

                val builder = AlertDialog.Builder(this)
                builder.setSingleChoiceItems(languages,-1){dialog, which ->
                    if(which == 0){
                        setLocale("bn")
                        recreate()
                    }
                    if(which == 1){
                        setLocale("en")
                        recreate()
                    }
                    dialog.dismiss()
                }

                val mdialog = builder.create()
                mdialog.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config,baseContext.resources.displayMetrics)

        val editor = getSharedPreferences("com.example.bazaar_hisabnew.language",Context.MODE_PRIVATE).edit()
        editor.putString("language",language)
        editor.apply()
    }


    private fun loadLocale(){
        val sharedPreferences =
            getSharedPreferences("com.example.bazaar_hisabnew.language", Context.MODE_PRIVATE)


        val lang = sharedPreferences.getString("language","")
        if (lang != null) {
            setLocale(lang)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}