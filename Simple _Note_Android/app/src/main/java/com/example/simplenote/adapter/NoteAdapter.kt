package com.example.simplenote.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplenote.R
import com.example.simplenote.local.LocalNote

class NoteAdapter(
    private var notes: List<LocalNote>,
    private val onClick: (LocalNote) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var itemHeight: Int = 0

    fun setItemHeight(h: Int) {
        itemHeight = h
        notifyDataSetChanged()
    }

    fun updateNotes(newNotes: List<LocalNote>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.noteTitle)
        val description: TextView = itemView.findViewById(R.id.noteDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        if (itemHeight > 0) {
            val lp = view.layoutParams
            lp?.height = itemHeight
            view.layoutParams = lp
        }
        return NoteViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.title.text = note.title
        holder.description.text = note.description

        // Checkerboard background colors
        val row = position / 2   // since 2 columns
        val col = position % 2
        val isEvenCell = (row + col) % 2 == 0
        val bgColor = if (isEvenCell) Color.parseColor("#f7f6d4") else Color.parseColor("#fdebab")
        holder.itemView.setBackgroundColor(bgColor)

        // Click listener
        holder.itemView.setOnClickListener {
            onClick(note)
        }

        // Scale animation on touch
        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false // allow click event to proceed
        }
    }

    override fun getItemCount(): Int = notes.size
}
