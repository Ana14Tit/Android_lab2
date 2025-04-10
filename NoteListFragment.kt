package com.example.lab2_20

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2_20.databinding.FragmentNoteListBinding
import com.google.android.material.snackbar.Snackbar

class NoteListFragment: Fragment() {
    lateinit var binding: FragmentNoteListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noteItems = (activity as MainActivity).noteList

        binding.rcView.layoutManager = LinearLayoutManager(context)
        val adapter = NoteAdapter(noteItems, onNoteItemClick =  { noteItem ->
            val bundle = Bundle().apply {
                putParcelable("noteItem", noteItem)
            }
            val NoteEditFragment = NoteEditFragment().apply {
                arguments = bundle
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NoteEditFragment)
                .addToBackStack(null)
                .commit()
        }, onNoteItemRemoved = { noteItem ->
            (activity as MainActivity).noteList.remove(noteItem)
            Snackbar.make(view, "Note deleted", Snackbar.LENGTH_SHORT).show()
            (activity as MainActivity).saveNoteItems()
        })
        binding.rcView.adapter = adapter

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeNoteItem(viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(binding.rcView)

        binding.addNoteButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NoteEditFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}