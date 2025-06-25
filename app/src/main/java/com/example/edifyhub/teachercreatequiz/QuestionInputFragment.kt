package com.example.edifyhub.teachercreatequiz

import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.edifyhub.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox

class QuestionInputFragment : Fragment() {
    private var quizName = ""
    private var subject = ""
    private var numQuestions = 0
    private var numAnswers = 0
    private var paid = false
    private var amount: Double? = null

    companion object {
        fun newInstance(
            name: String,
            subject: String,
            numQuestions: Int,
            numAnswers: Int,
            paid: Boolean,
            amount: Double?
        ) =
            QuestionInputFragment().apply {
                arguments = Bundle().apply {
                    putString("name", name)
                    putString("subject", subject)
                    putInt("questions", numQuestions)
                    putInt("answers", numAnswers)
                    putBoolean("paid", paid)
                    putDouble("amount", amount ?: 0.0)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            quizName = it.getString("name") ?: ""
            subject = it.getString("subject") ?: ""
            numQuestions = it.getInt("questions")
            numAnswers = it.getInt("answers")
            paid = it.getBoolean("paid")
            amount = it.getDouble("amount", 0.0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Root vertical LinearLayout with padding and background
        val rootLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24.dpToPx(), 32.dpToPx(), 24.dpToPx(), 24.dpToPx())
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background))
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        // Title TextView: "Create Quiz"
        val titleTextView = TextView(requireContext()).apply {
            text = "Create Quiz"
            setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            textSize = 24f
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 16.dpToPx())
            typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        rootLayout.addView(titleTextView)

        // ScrollView to hold all questions and inputs
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            isFillViewport = true
        }

        // Container inside ScrollView styled like your XML, **FULL WIDTH, NO MARGINS**
        val containerLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24.dpToPx(), 24.dpToPx(), 24.dpToPx(), 24.dpToPx())
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface))
            elevation = 4.dpToPx().toFloat()
            // FULL width - no margins for full width card effect
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            clipToPadding = false
            clipChildren = false
            gravity = Gravity.CENTER_HORIZONTAL
        }

        // Quiz Name input
        val quizNameLabel = createLabel("Quiz Name")
        val quizNameEdit = createEditText("Enter quiz name").apply {
            setText(quizName)
        }
        containerLayout.addView(quizNameLabel)
        containerLayout.addView(quizNameEdit)

        // Subject input
        val subjectLabel = createLabel("Subject")
        val subjectEdit = createEditText("Enter subject").apply {
            setText(subject)
        }
        containerLayout.addView(subjectLabel)
        containerLayout.addView(subjectEdit)

        // Number of Questions input
        val questionsLabel = createLabel("Number of Questions")
        val questionsEdit = createEditText("Enter number of questions", inputType = android.text.InputType.TYPE_CLASS_NUMBER).apply {
            setText(numQuestions.toString())
        }
        containerLayout.addView(questionsLabel)
        containerLayout.addView(questionsEdit)

        // Number of Answers input
        val answersLabel = createLabel("Number of Answers per Question")
        val answersEdit = createEditText("Enter number of answers", inputType = android.text.InputType.TYPE_CLASS_NUMBER).apply {
            setText(numAnswers.toString())
        }
        containerLayout.addView(answersLabel)
        containerLayout.addView(answersEdit)

        // Paid Quiz checkbox
        val paidCheckbox = MaterialCheckBox(requireContext()).apply {
            text = "Paid Quiz?"
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            textSize = 16f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            isChecked = paid
            setPadding(0, 0, 0, 16.dpToPx())
        }
        containerLayout.addView(paidCheckbox)

        // Amount label and edit (visible only if paid is true)
        val amountLabel = createLabel("Amount").apply {
            visibility = if (paid) View.VISIBLE else View.GONE
        }
        val amountEdit = createEditText("Enter amount", inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or android.text.InputType.TYPE_CLASS_NUMBER).apply {
            visibility = if (paid) View.VISIBLE else View.GONE
            setText(amount?.takeIf { it > 0 }?.toString() ?: "")
        }
        containerLayout.addView(amountLabel)
        containerLayout.addView(amountEdit)

        // Dynamically create question inputs with answers
        fun addQuestions(questions: Int, answers: Int) {
            for (i in 1..questions) {
                val questionLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, 16.dpToPx(), 0, 16.dpToPx())
                }

                val questionTextView = createLabel("Question $i").apply {
                    setPadding(0, 0, 0, 4.dpToPx())
                }

                val questionEditText = createEditText("Enter question $i")

                questionLayout.addView(questionTextView)
                questionLayout.addView(questionEditText)

                val answerLabel = createLabel("Answers").apply {
                    setPadding(0, 12.dpToPx(), 0, 4.dpToPx())
                }
                questionLayout.addView(answerLabel)

                val radioGroup = RadioGroup(requireContext()).apply {
                    orientation = RadioGroup.VERTICAL
                }

                for (j in 1..answers) {
                    val answerLayout = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            setMargins(0, 8.dpToPx(), 0, 8.dpToPx())
                        }
                    }

                    val answerEditText = createEditText("Answer $j").apply {
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    }

                    val radioButton = RadioButton(requireContext()).apply {
                        text = "Correct"
                        id = View.generateViewId()
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                    }

                    answerLayout.addView(answerEditText)
                    answerLayout.addView(radioButton)
                    radioGroup.addView(answerLayout)
                }
                questionLayout.addView(radioGroup)
                containerLayout.addView(questionLayout)
            }
        }

        addQuestions(numQuestions, numAnswers)

        // Create Quiz button styled as in your XML
        val createButton = MaterialButton(requireContext()).apply {
            text = "Create Quiz"
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
            setPadding(24.dpToPx(), 24.dpToPx(), 24.dpToPx(), 24.dpToPx())
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 24.dpToPx()
                bottomMargin = 24.dpToPx()
            }
            cornerRadius = 12.dpToPx()

            setOnClickListener {
                // Show confirmation dialog
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to create this quiz?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()

                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        containerLayout.addView(createButton)

        scrollView.addView(containerLayout)
        rootLayout.addView(scrollView)

        // Checkbox listener to toggle Amount input visibility
        paidCheckbox.setOnCheckedChangeListener { _, isChecked ->
            amountLabel.visibility = if (isChecked) View.VISIBLE else View.GONE
            amountEdit.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        return rootLayout
    }

    // Helper function to create styled TextView label
    private fun createLabel(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            textSize = 16f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        }
    }

    // Helper function to create styled EditText
    private fun createEditText(hint: String, inputType: Int = android.text.InputType.TYPE_CLASS_TEXT): EditText {
        return EditText(requireContext()).apply {
            this.hint = hint
            this.inputType = inputType
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            setHintTextColor(ContextCompat.getColor(requireContext(), R.color.surface))
            textSize = 16f
            setBackgroundResource(R.drawable.edittext_background) // Optional: use your own background drawable if you want
            setPadding(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 8.dpToPx()
                bottomMargin = 8.dpToPx()
            }
        }
    }

    // Extension to convert dp to pixels
    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}
