package com.example.lab2_20

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lab2_20.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private val PREFS_NAME = "notes_prefs"
    private val NOTES_KEY = "notes_key"
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1
    private val REQUEST_CODE_SCHEDULE_EXACT_ALARM = 2

    var noteList = mutableListOf<NoteItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        loadNoteItems()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NoteListFragment())
                .commit()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM), REQUEST_CODE_SCHEDULE_EXACT_ALARM)
            }
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            val title = it.getStringExtra("notificationalTitle")
            val content = it.getStringExtra("notificationalContent")
            if (title != null && content != null) {
                val noteItem = NoteItem(title, content)
                val fragment = NoteEditFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("noteItem", noteItem)
                    }
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        saveNoteItems()
    }

     fun saveNoteItems() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val notesJson = NoteItemUtils.noteItemsToJson(noteList)
        editor.putString(NOTES_KEY, notesJson)
        editor.apply()
    }

    private fun loadNoteItems() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val notesJson = sharedPreferences.getString(NOTES_KEY, null)
        if (notesJson != null) {
            noteList = NoteItemUtils.jsonToNoteItems(notesJson).toMutableList()
        }
    }
}