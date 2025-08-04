package com.example.edifyhub.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.edifyhub.R

data class TeacherItem(
    val id: String,
    val name: String,
    val institute: String,
    val about: String,
    val imageUrl: String?,
    val subject: String
)

class TeacherListAdapter(
    private var teachers: List<TeacherItem>,
    private val onViewInstitute: (teacherId: String) -> Unit
) : RecyclerView.Adapter<TeacherListAdapter.TeacherViewHolder>() {

    class TeacherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.teacherProfileImage)
        val name: TextView = view.findViewById(R.id.teacherName)
        val institute: TextView = view.findViewById(R.id.teacherInstitute)
        val subject: TextView = view.findViewById(R.id.teacherSubject)
        val about: TextView = view.findViewById(R.id.teacherAbout)
        val btnViewInstitute: Button = view.findViewById(R.id.btnViewInstitute)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_studentviewteacher, parent, false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacher = teachers[position]
        holder.name.text = teacher.name
        holder.institute.text = teacher.institute
        holder.subject.text = teacher.subject
        holder.about.text = teacher.about
        Glide.with(holder.profileImage.context)
            .load(teacher.imageUrl)
            .placeholder(R.drawable.baseline_person_24)
            .error(R.drawable.baseline_person_24)
            .circleCrop()
            .into(holder.profileImage)

        holder.btnViewInstitute.setOnClickListener {
            onViewInstitute(teacher.id)
        }
    }

    override fun getItemCount() = teachers.size

    fun submitList(newList: List<TeacherItem>) {
        teachers = newList
        notifyDataSetChanged()
    }
}