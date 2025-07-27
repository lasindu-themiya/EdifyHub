package com.example.edifyhub.admin

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R

class TeacherAdapter(
    private val teacherList: MutableList<Teacher>,
    private val onEdit: (Teacher) -> Unit,
    private val onReject: (Teacher) -> Unit
) : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

    class TeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePic: ImageView = itemView.findViewById(R.id.imgProfile)
        val name: TextView = itemView.findViewById(R.id.tvName)
        val subject: TextView = itemView.findViewById(R.id.tvSubject)
        val editIcon: ImageButton = itemView.findViewById(R.id.btnEdit)
        val rejectIcon: ImageButton = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_teacher, parent, false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacher = teacherList[position]
        holder.profilePic.setImageResource(teacher.profilePicRes)
        holder.name.text = teacher.name
        holder.subject.text = teacher.subject

        holder.editIcon.setOnClickListener {
            onEdit(teacher)
        }

        holder.rejectIcon.setOnClickListener {
            onReject(teacher)
            removeItem(position)
        }
    }

    private fun removeItem(position: Int) {
        teacherList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, teacherList.size)
    }

    override fun getItemCount(): Int = teacherList.size
}