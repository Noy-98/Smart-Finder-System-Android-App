package com.example.smart_finder_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_finder_app.R

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var deviceNames: List<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deviceName = deviceNames[position]
        holder.deviceName.text = deviceName

        // Set click listener for View Details button
        holder.viewDetailsButton.setOnClickListener {
            when (deviceName) {
                "Bike Keychain" -> {
                    val intent = Intent(holder.itemView.context, Device_Control::class.java)
                    // Add any necessary extras or data to the intent
                    holder.itemView.context.startActivity(intent)
                }
                "Car Keychain" -> {
                    val intent = Intent(holder.itemView.context, Device_Control2::class.java)
                    // Add any necessary extras or data to the intent
                    holder.itemView.context.startActivity(intent)
                }
                "House Keychain" -> {
                    val intent = Intent(holder.itemView.context, Device_Control3::class.java)
                    // Add any necessary extras or data to the intent
                    holder.itemView.context.startActivity(intent)
                }
                // Add more cases for other device names if needed
                else -> {
                    // Default case, handle as needed
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return deviceNames.size
    }

    fun setDeviceNames(deviceNames: List<String>) {
        this.deviceNames = deviceNames
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.device_name)
        val viewDetailsButton: Button = itemView.findViewById(R.id.viewDetails_bttn)
    }
}
