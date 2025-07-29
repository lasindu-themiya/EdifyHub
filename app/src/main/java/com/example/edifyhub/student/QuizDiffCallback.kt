package com.example.edifyhub.student

import androidx.recyclerview.widget.DiffUtil

class QuizDiffCallback : DiffUtil.ItemCallback<QuizItem>() {
    override fun areItemsTheSame(oldItem: QuizItem, newItem: QuizItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: QuizItem, newItem: QuizItem): Boolean {
        return oldItem == newItem
    }
}