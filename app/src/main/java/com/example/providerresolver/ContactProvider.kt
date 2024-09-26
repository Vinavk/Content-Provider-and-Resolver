package com.example.providerresolver



import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri


class ContactProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.contentproviderexample.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/contacts")
        const val CONTACTS = 1
        const val CONTACTS_ID = 2
    }

    private lateinit var databaseHelper: ContactDatabaseHelper
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "contacts", CONTACTS)
        addURI(AUTHORITY, "contacts/#", CONTACTS_ID)
    }

    override fun onCreate(): Boolean {
        databaseHelper = ContactDatabaseHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val db = databaseHelper.readableDatabase
        return when (uriMatcher.match(uri)) {
            CONTACTS -> db.query(ContactDatabaseHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            CONTACTS_ID -> {
                val id = ContentUris.parseId(uri).toString()
                db.query(ContactDatabaseHelper.TABLE_NAME, projection, "${ContactDatabaseHelper.COLUMN_ID}=?", arrayOf(id), null, null, sortOrder)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = databaseHelper.writableDatabase
        val id = db.insert(ContactDatabaseHelper.TABLE_NAME, null, values)
        return if (id > 0) {
            context?.contentResolver?.notifyChange(uri, null)
            ContentUris.withAppendedId(CONTENT_URI, id)
        } else {
            null
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val db = databaseHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            CONTACTS -> db.update(ContactDatabaseHelper.TABLE_NAME, values, selection, selectionArgs)
            CONTACTS_ID -> {
                val id = ContentUris.parseId(uri).toString()
                db.update(ContactDatabaseHelper.TABLE_NAME, values, "${ContactDatabaseHelper.COLUMN_ID}=?", arrayOf(id))
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = databaseHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            CONTACTS -> db.delete(ContactDatabaseHelper.TABLE_NAME, selection, selectionArgs)
            CONTACTS_ID -> {
                val id = ContentUris.parseId(uri).toString()
                db.delete(ContactDatabaseHelper.TABLE_NAME, "${ContactDatabaseHelper.COLUMN_ID}=?", arrayOf(id))
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            CONTACTS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.contacts"
            CONTACTS_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.contacts"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}
