package com.example.smart_finder_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Device_Control3 : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var keychainRef: DatabaseReference
    private lateinit var distanceView: TextView
    private lateinit var onButton: Button
    private lateinit var offButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_control3)

        val Map_Button = findViewById<ImageView>(R.id.map_bttn)

        database = FirebaseDatabase.getInstance()
        keychainRef = database.getReference("KeychainData/House Keychain")

        distanceView = findViewById(R.id.distance_view)
        onButton = findViewById(R.id.on_bttn)
        offButton = findViewById(R.id.off_bttn)

        keychainRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val frontDistance = snapshot.child("FrontDistance").getValue(Long::class.java)
                distanceView.text = frontDistance.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        onButton.setOnClickListener {
            keychainRef.child("Buzzer").setValue(1)
            showToast("Buzzer turned ON")
        }

        offButton.setOnClickListener {
            keychainRef.child("Buzzer").setValue(0)
            showToast("Buzzer turned OFF")
        }


        Map_Button.setOnClickListener {
            val intent = Intent (this, Maps::class.java)
            startActivity(intent)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}