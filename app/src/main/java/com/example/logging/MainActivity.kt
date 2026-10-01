package com.example.logging

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Timber.treeCount == 0) {
            Timber.plant(Timber.DebugTree())
        }

        val editText = findViewById<EditText>(R.id.editText)
        val buttonLog = findViewById<Button>(R.id.button_log)
        val buttonTimber = findViewById<Button>(R.id.button_timber)

        buttonLog.setOnClickListener {
            Log.v("From EditText", editText.text.toString())
        }

        buttonTimber.setOnClickListener {
            Timber.v(editText.text.toString())
        }
    }
}
