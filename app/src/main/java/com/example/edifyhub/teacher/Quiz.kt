package com.example.edifyhub.teacher

import java.util.*

data class Quiz(
    val id: String,
    val name: String,
    val subject: String,
    val numQuestions: Int,
    val numAnswers: Int,
    val paid: Boolean,
    val amount: Double?,
    val createdAt: Date?,
    val scheduledAt: Date?,
    val meetingAt: Date?
)