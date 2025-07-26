package com.example.edifyhub.admin

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.admin.TeacherSignupRequestModel
import com.example.edifyhub.R

class TeacherAdapter(
    private val teacherList: MutableList<TeacherSignupRequestModel>,
    private val onApprove: (TeacherSignupRequestModel) -> Unit,
    private val onReject: (TeacherSignupRequestModel) -> Unit
) : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

    class TeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.teacherName)
        val email: TextView = itemView.findViewById(R.id.teacherEmail)
        val subject: TextView = itemView.findViewById(R.id.teacherSubject)
        val institute: TextView = itemView.findViewById(R.id.teacherInstitute)
        val approveIcon: ImageView = itemView.findViewById(R.id.approveIcon)
        val rejectIcon: ImageView = itemView.findViewById(R.id.rejectIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_teacher_card, parent, false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacher = teacherList[position]
        holder.name.text = teacher.name
        holder.email.text = "Email: ${teacher.email}"
        holder.subject.text = "Subject: ${teacher.subject}"
        holder.institute.text = "Institute: ${teacher.instituteName}"

        holder.approveIcon.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Approve Teacher")
                .setMessage("Are you sure you want to approve ${teacher.name}?")
                .setPositiveButton("Yes") { dialog, _ ->
                    onApprove(teacher)
                    removeItem(position)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        holder.rejectIcon.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Reject Teacher")
                .setMessage("Are you sure you want to reject ${teacher.name}?")
                .setPositiveButton("Yes") { dialog, _ ->
                    onReject(teacher)
                    removeItem(position)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun removeItem(position: Int) {
        teacherList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, teacherList.size)
    }

    override fun getItemCount(): Int = teacherList.size
}