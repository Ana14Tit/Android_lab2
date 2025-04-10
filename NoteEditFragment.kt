package com.example.lab2_20

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lab2_20.databinding.FragmentNoteEditBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar


class NoteEditFragment: Fragment() {
    lateinit var binding: FragmentNoteEditBinding
    private var noteItemToEdit: NoteItem? = null
    private var reminderTime: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteEditBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteItemToEdit = arguments?.getParcelable("noteItem")
        noteItemToEdit?.let {
            binding.titleEditText.setText(it.title)
            binding.contentEditText.setText(it.content)
        }

        binding.setReminderButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            if (title != "" && content != "") {
                showDateTimePicker()
            }
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NoteListFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            if (title != "" && content != "") {
                if (noteItemToEdit != null) {
                    val updatedNoteItem = NoteItem(title, content)
                    val index =
                        (activity as MainActivity).noteList.indexOfFirst { it == noteItemToEdit }
                    if (index != -1) {
                        (activity as MainActivity).noteList[index] = updatedNoteItem
                    }
                    Snackbar.make(view, "Note update", Snackbar.LENGTH_SHORT).show()
                } else {
                    val newNoteItem = NoteItem(title, content)
                    (activity as MainActivity).noteList.add(newNoteItem)
                    Snackbar.make(view, "Note added", Snackbar.LENGTH_SHORT).show()
                }

                (activity as MainActivity).saveNoteItems()

                reminderTime?.let {
                    setReminder(title, content, it)
                }


                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    "noteItems",
                    ArrayList((activity as MainActivity).noteList)
                )

                val noteListFragment = NoteListFragment()
                noteListFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, noteListFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                reminderTime = calendar
                Snackbar.make(binding.root, "Reminder set for ${calendar.time}", Snackbar.LENGTH_SHORT).show()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun setReminder(title: String, content: String, time: Calendar) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            putExtra("notificationalTitle", title)
            putExtra("notificationalContent", content)
        }
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingIntent)
    }
}