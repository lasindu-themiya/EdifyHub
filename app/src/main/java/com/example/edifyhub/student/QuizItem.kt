package com.example.edifyhub.student

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class QuizItem(
    val id: String,
    val name: String,
    val subject: String,
    val teacherId: String,
    val teacherName: String,
    val scheduledAt: Date,
    val meetingAt: Date,
    val amount: Double,
    val isPaid: Boolean
) : Parcelable