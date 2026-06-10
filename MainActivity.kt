package com.my.epubreader

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private var epubFiles = ArrayList<File>()
    private var epubNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        listView = ListView(this)
        setContentView(listView)
        
        checkPermissions()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    startActivity(intent)
                }
            } else {
                searchEpubs()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            } else {
                searchEpubs()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            searchEpubs()
        }
    }

    private fun searchEpubs() {
        epubFiles.clear()
        epubNames.clear()
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        findEpubRecursive(downloadDir)
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, epubNames)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, ViewerActivity::class.java)
            intent.putExtra("EPUB_PATH", epubFiles[position].absolutePath)
            startActivity(intent)
        }
    }

    private fun findEpubRecursive(dir: File) {
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                findEpubRecursive(file)
            } else if (file.name.lowercase().endsWith(".epub")) {
                epubFiles.add(file)
                epubNames.add(file.name)
            }
        }
    }
}