package com.example.edifyhub.teacher

sealed class QuizScheduleItem {
    data class Header(val title: String) : QuizScheduleItem()
    data class QuizItem(val quiz: Quiz) : QuizScheduleItem()
}