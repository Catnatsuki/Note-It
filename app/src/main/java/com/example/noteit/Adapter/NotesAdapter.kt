package com.example.noteit.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteit.Models.Note
import com.example.noteit.R
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak
import kotlin.random.Random

class NotesAdapter(private val context : Context, val listener: NoteClickListener) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val NotesList = ArrayList<Note>()
    private val fullList = ArrayList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return NotesList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = NotesList[position]
        holder.title.text = currentNote.title
        holder.title.isSelected = true

        holder.markwon.setMarkdown(holder.Note_tv,currentNote.note)

//        holder.Note_tv.text = currentNote.note
        holder.date.text = currentNote.date
        holder.date.isSelected = true

//        holder.note_layout.setCardBackgroundColor(holder.itemView.resources.getColor(randomColor(), null))


        holder.note_layout.setOnClickListener {
            listener.onItemClicked(NotesList[holder.adapterPosition])
        }


        holder.note_layout.setOnLongClickListener {
            listener.onLongItemClicked(NotesList[holder.adapterPosition], holder.note_layout)
            true
        }
    }


    fun updateList(newList: List<Note>){
        fullList.clear()
        fullList.addAll(newList)

        NotesList.clear()
        NotesList.addAll(fullList)
        notifyDataSetChanged()
    }

    // Method to delete a note on swipe.
    fun currentList(position: Int): Note {
        return NotesList[position]
    }

    //Method to return selected notepostion
    fun selectedPos(note: Note) : Int{
        val secPos = NotesList.indexOf(note)
        return secPos
    }

    fun filterList(search : String){
        NotesList.clear()
        for(item in fullList){
            if(item.title?.lowercase()?.contains(search.lowercase()) == true || item.note?.lowercase()?.contains(search.lowercase())){
                NotesList.add(item)
            }
        }
        notifyDataSetChanged()
    }


    fun randomColor(): Int{

        val list = ArrayList<Int>()
        list.add(com.google.android.material.R.color.m3_sys_color_dynamic_dark_background)
        list.add(com.google.android.material.R.color.m3_sys_color_dynamic_light_background)
        list.add(com.google.android.material.R.color.m3_sys_color_dynamic_light_on_background)
        list.add(com.google.android.material.R.color.m3_sys_color_dynamic_dark_on_background)
        list.add(com.google.android.material.R.color.m3_sys_color_dynamic_light_inverse_primary)
        list.add(com.google.android.material.R.color.material_dynamic_tertiary70)

        val seed = System.currentTimeMillis().toInt()
        val randomIndes = Random(seed).nextInt(list.size)
        return list[randomIndes]

    }



    inner class  NoteViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val note_layout = itemView.findViewById<CardView>(R.id.card_layout)
        val title = itemView.findViewById<TextView>(R.id.tv_title)
        val Note_tv = itemView.findViewById<TextView>(R.id.tv_note)
        val date = itemView.findViewById<TextView>(R.id.tv_date)
        val markwon = Markwon.builder(itemView.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(itemView.context))
            .usePlugin(object : AbstractMarkwonPlugin(){
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(
                        SoftLineBreak::class.java
                    ){visitor, _ -> visitor.forceNewLine()}
                }
            })
            .build()

    }

    interface NoteClickListener{
        fun onItemClicked(note:Note)
        fun onLongItemClicked(note:Note,cardView: CardView)
    }

}