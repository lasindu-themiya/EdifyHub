package com.example.edifyhub.admin

import com.example.edifyhub.R
import java.io.Serializable

data class Student(
    val id: String,
    var name: String,
    var age: Int,
    var mobile: String,
    var stream: String,
    var email: String,
    var profilePicRes: Int = R.drawable.ic_profile
): Serializable