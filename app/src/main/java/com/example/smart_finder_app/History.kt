package com.example.smart_finder_app

import android.content.Intent
import android.health.connect.datatypes.Device
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class History : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var recyclerView: RecyclerView
    lateinit var historyAdapter: HistoryAdapter
    lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("KeychainData")

        recyclerView = findViewById(R.id.patientList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = HistoryAdapter()
        recyclerView.adapter = historyAdapter

        // Fetch data from Firebase
        fetchDataFromFirebase()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.logs
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            if (item.itemId == R.id.home) {
                startActivity(Intent(applicationContext, HomeDashboard::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
                return@setOnItemSelectedListener true
            }  else if (item.itemId == R.id.logs) {
                return@setOnItemSelectedListener true
            } else if (item.itemId == R.id.logout) {
                auth = FirebaseAuth.getInstance()
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
                return@setOnItemSelectedListener true
            }
            false
        }
    }

    private fun fetchDataFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val deviceNames = mutableListOf<String>()
                    for (deviceSnapshot in snapshot.children) {
                        val deviceName = deviceSnapshot.key
                        deviceName?.let {
                            deviceNames.add(it)
                        }
                    }
                    historyAdapter.setDeviceNames(deviceNames)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}