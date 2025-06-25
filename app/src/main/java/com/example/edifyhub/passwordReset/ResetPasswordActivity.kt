package com.example.edifyhub.passwordReset

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.edifyhub.R

class ResetPasswordActivity : AppCompatActivity() {
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSubmitPassword = findViewById<Button>(R.id.btnSubmitPassword)

        setupPasswordToggle(etNewPassword, true)
        setupPasswordToggle(etConfirmPassword, false)

        btnSubmitPassword.setOnClickListener {
            // TODO: Validate passwords and reset
            startActivity(Intent(this, com.example.edifyhub.MainActivity::class.java))
            finish()
        }
    }

    private fun setupPasswordToggle(editText: EditText, isNewPassword: Boolean) {
        editText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                if (event.rawX >= (editText.right - editText.compoundDrawables[drawableEnd].bounds.width())) {
                    if (isNewPassword) {
                        isPasswordVisible = !isPasswordVisible
                        togglePasswordVisibility(editText, isPasswordVisible)
                    } else {
                        isConfirmPasswordVisible = !isConfirmPasswordVisible
                        togglePasswordVisibility(editText, isConfirmPasswordVisible)
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility(editText: EditText, visible: Boolean) {
        val eyeIcon: Drawable? = if (visible) {
            ContextCompat.getDrawable(this, R.drawable.ic_eye)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_eye)
        }
        editText.inputType = if (visible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        editText.setSelection(editText.text.length)
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeIcon, null)
    }
}