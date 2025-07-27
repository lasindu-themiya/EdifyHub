package com.example.edifyhub.student

import java.util.Date

data class QuizItem(
    val id: String,
    val name: String,
    val subject: String,
    val teacherName: String,
    val scheduledAt: Date,
    val meetingAt: Date,
    val amount: Double
)