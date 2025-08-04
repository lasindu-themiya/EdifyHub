package com.example.edifyhub.teacher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R


class AttendeeAdapter : RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder>() {
    private val items = mutableListOf<Attendee>()

    fun submitList(list: List<Attendee>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendee, parent, false)
        return AttendeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendeeViewHolder, position: Int) {
        val attendee = items[position]
        holder.tvName.text = attendee.username
        holder.tvScore.text = "Score: ${attendee.score}/${attendee.total}"
        holder.tvEmail.text = "Email: ${attendee.email}"
        holder.tvMobile.text = "Mobile: ${attendee.mobile}"
    }

    override fun getItemCount() = items.size

    class AttendeeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvAttendeeName)
        val tvScore: TextView = view.findViewById(R.id.tvAttendeeScore)
        val tvEmail: TextView = view.findViewById(R.id.tvAttendeeEmail)
        val tvMobile: TextView = view.findViewById(R.id.tvAttendeeMobile)
    }
}