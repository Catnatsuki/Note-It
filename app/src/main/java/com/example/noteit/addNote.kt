package com.example.noteit

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.transition.Fade
import android.transition.TransitionInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.view.ViewCompat
import com.example.noteit.databinding.ActivityAddNoteBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.yahiaangelo.markdownedittext.MarkdownEditText
import com.yahiaangelo.markdownedittext.MarkdownStylesBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

class addNote : AppCompatActivity() {

    private  lateinit var binding : ActivityAddNoteBinding

    private lateinit var note : com.example.noteit.Models.Note
    private  lateinit var old_note : com.example.noteit.Models.Note
    var isUpdate = false


    override fun onBackPressed() {
        super.onBackPressed()
        val fadeOut = Fade(Fade.OUT)
        window.exitTransition = fadeOut
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val markdownEditText = findViewById<MarkdownEditText>(R.id.et_note)
        val stylesBar = findViewById<MarkdownStylesBar>(R.id.styles_bar)
        markdownEditText.setStylesBar(stylesBar)

        try {
            old_note = intent.getSerializableExtra("current_note") as com.example.noteit.Models.Note
            binding.etTitle.setText(old_note.title)
            binding.etNote.renderMD(old_note.note)
            isUpdate = true
        }catch (e : Exception){

            e.printStackTrace()
        }

        binding.materialToolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.img_check -> {
                    val title = binding.etTitle.text.toString()
                    val note_desc = binding.etNote.getMD()

                    if(title.isNotEmpty() || note_desc.isNotEmpty()){
                        val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")
                        if(isUpdate){
                            note = com.example.noteit.Models.Note(
                                old_note.id,
                                title,
                                note_desc, formatter.format(Date())

                            )
                        }else{
                            note = com.example.noteit.Models.Note(
                                null,
                                title,
                                note_desc,
                                formatter.format(Date())
                            )
                        }

                        val intent = Intent()
                        intent.putExtra("note",note)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }else{
                        Toast.makeText(this@addNote,"Please enter your note",Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }

        }

        this.binding.materialToolbar.setNavigationOnClickListener {
            onBackPressed()
        }



    }
}