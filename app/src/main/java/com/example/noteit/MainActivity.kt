package com.example.noteit

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteit.Adapter.NotesAdapter
import com.example.noteit.Database.NoteDatabase
import com.example.noteit.Models.Note
import com.example.noteit.Models.NoteViewModel
import com.example.noteit.databinding.ActivityMainBinding
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), NotesAdapter.NoteClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataBase: NoteDatabase
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NotesAdapter
    lateinit var  selectedNote: Note


    private val swipegesture = object : SwipeGesture(this){

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val posit = viewHolder.absoluteAdapterPosition
            selectedNote = adapter.currentList(posit)

            when(direction){

                ItemTouchHelper.LEFT -> {

                    viewModel.deleteNote(selectedNote)

                }

                ItemTouchHelper.RIGHT -> {
                    viewModel.deleteNote(selectedNote)
                }
            }
        }
    }




    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if(result.resultCode == Activity.RESULT_OK){
            val note = result.data?.getSerializableExtra("note") as? Note
            if(note != null){
                viewModel.updateNote(note)
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivitiesIfAvailable(application)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing the UI
        initUI()
        val touchHelp = ItemTouchHelper(swipegesture)
        touchHelp.attachToRecyclerView(findViewById(R.id.recycler_view))

        viewModel = ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        viewModel.allnotes.observe(this) { list ->

            list.let {
                adapter.updateList(list)
            }
        }
        dataBase = NoteDatabase.getDatabase(this)
    }



    private fun initUI() {

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter = NotesAdapter(this,this)
        binding.recyclerView.adapter = adapter

        /// Recycler view animation test:


        val getcontent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

            if(result.resultCode == RESULT_OK){

                val note = result.data?.getSerializableExtra("note") as? Note
                if(note != null){
                    viewModel.insertNote(note)
                }
            }
        }

        binding.fbAddNote.setOnClickListener {
            val intent = Intent(this, addNote::class.java)

            val newFab = findViewById<FloatingActionButton>(R.id.fb_add_note)

            val options = ActivityOptionsCompat.makeClipRevealAnimation(
                newFab, 0, 0, newFab.width, newFab.height
            )
            getcontent.launch(intent, options)
        }

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null){
                    adapter.filterList(newText)
                }

                return true
            }

        })
    }

    //Function I wrote for animation the fab but didn't use as it was causing issues with inserting items into the database.
    private fun fabAnimation() {
        val newFab = findViewById<FloatingActionButton>(R.id.fb_add_note)

        val options = ActivityOptionsCompat.makeClipRevealAnimation(
            newFab, 0, 0, newFab.width, newFab.height
        )

        val intent = Intent(this, addNote::class.java)
        startActivity(intent, options.toBundle())
    }

    private fun startAddNote(view: View, note: Note) {
        val rect = Rect()
        view.getGlobalVisibleRect(rect)

        val centerX = rect.centerX()
        val centerY = rect.centerY()

        val options = ActivityOptionsCompat.makeClipRevealAnimation(view, centerX, centerY, 0, 0)
        val intent = Intent(this, addNote::class.java)
        intent.putExtra("current_note", note)
        startActivity(intent, options.toBundle())
    }

    override fun onItemClicked(note: Note) {
        val recView = findViewById<RecyclerView>(R.id.recycler_view)
        val position: Int = adapter.selectedPos(note)
        val view: View = recView.getChildAt(position)
        startAddNote(view, note)
    }


//    override fun onItemClicked(note: Note) {
//        val intent = Intent(this@MainActivity, addNote::class.java)
//        intent.putExtra("current_note", note)
//        updateNote.launch(intent)
//    }

    override fun onLongItemClicked(note: Note, cardView: CardView) {
        selectedNote = note
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(this, cardView)
        popup.setOnMenuItemClickListener(this@MainActivity)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.delete_note){
            viewModel.deleteNote(selectedNote)
            return true
        }
        return false
    }

}

// Changed note from String? to String in all the database references for it, change it back if new issues arise.
