package com.example.lab2_20

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2_20.databinding.ItemNoteBinding

class NoteAdapter(private val noteItems: MutableList<NoteItem>, private val onNoteItemClick: (NoteItem) -> Unit, private val onNoteItemRemoved: (NoteItem) -> Unit): RecyclerView.Adapter<NoteAdapter.NoteHolder>(){

    class NoteHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(noteItem: NoteItem) {
            binding.titleTextView.text = noteItem.title
            val shortContent = if (noteItem.content.length > 50) {
                noteItem.content.substring(0, 50) + "..."
            } else {
                noteItem.content
            }
            binding.contentTextView.text = shortContent
         }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteHolder(binding)
    }

    override fun getItemCount(): Int {
        return noteItems.size
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val noteItem = noteItems[position]
        holder.bind(noteItem)

        holder.itemView.setOnClickListener {
            onNoteItemClick(noteItem)
        }
    }

    fun removeNoteItem(position: Int) {
        val removedNoteItem = noteItems.removeAt(position)
        notifyItemRemoved(position)
        onNoteItemRemoved(removedNoteItem)
    }
}