package com.example.edifyhub.admin

data class TeacherSignupRequestModel(
    val id: String,
    val name: String,
    val email: String,
    val subject: String,
    val instituteName: String
)