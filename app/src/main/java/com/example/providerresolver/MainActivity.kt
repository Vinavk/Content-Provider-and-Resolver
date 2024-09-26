package com.example.providerresolver

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.providerresolver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnInsert.setOnClickListener {
            val values = ContentValues().apply {
                put(ContactDatabaseHelper.COLUMN_NAME, "jenny")
                put(ContactDatabaseHelper.COLUMN_PHONE, "123-456-7890")
                put(ContactDatabaseHelper.COLUMN_NAME, "john")
                put(ContactDatabaseHelper.COLUMN_PHONE, "123-456-7890")
            }
            val uri: Uri? = contentResolver.insert(ContactProvider.CONTENT_URI, values)
            if (uri != null) {
                Toast.makeText(this, "Contact Inserted: $uri", Toast.LENGTH_SHORT).show()
            }
        }

        // Query all contacts
        binding.btnQuery.setOnClickListener {
            val cursor: Cursor? = contentResolver.query(ContactProvider.CONTENT_URI, null, null, null, null)
            cursor?.use {
                val idColumn = it.getColumnIndex(ContactDatabaseHelper.COLUMN_ID)
                val nameColumn = it.getColumnIndex(ContactDatabaseHelper.COLUMN_NAME)
                val phoneColumn = it.getColumnIndex(ContactDatabaseHelper.COLUMN_PHONE)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val phone = it.getString(phoneColumn)
                    Toast.makeText(this, "ID: $id, Name: $name, Phone: $phone", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}