package com.example.edifyhub.admin

import java.io.Serializable

data class Teacher(
    val id: String,
    val name: String,
    val subject: String,
    val email: String,
    val status: String,
    val profilePicRes: Int = com.example.edifyhub.R.drawable.ic_profile
) : Serializable