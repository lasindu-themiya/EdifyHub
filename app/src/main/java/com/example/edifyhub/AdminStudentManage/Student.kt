package com.example.edifyhub.AdminStudentManage

import java.io.Serializable

data class Student(
    val id: String,
    var name: String,
    var age: Int,
    var mobile: String,
    var stream: String,
    var email: String,
    var profilePicRes: Int // drawable resource id
): Serializable