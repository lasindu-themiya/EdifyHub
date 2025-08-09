package com.example.edifyhub.student

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

class QuizQuestionDbHelper(context: Context) : SQLiteOpenHelper(context, "quiz_questions.db", null, 1) {

    companion object {
        const val TABLE_NAME = "quiz_questions"
        const val COL_ID = "id"
        const val COL_QUIZ_ID = "quiz_id"
        const val COL_QUESTION_ID = "question_id"
        const val COL_TEXT = "text"
        const val COL_ANSWERS = "answers"
        const val COL_CORRECT_INDEX = "correct_index"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_QUIZ_ID TEXT," +
                    "$COL_QUESTION_ID TEXT," +
                    "$COL_TEXT TEXT," +
                    "$COL_ANSWERS TEXT," +
                    "$COL_CORRECT_INDEX INTEGER" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun saveQuestions(quizId: String, questions: List<Question>) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COL_QUIZ_ID=?", arrayOf(quizId))
        for (q in questions) {
            val values = ContentValues().apply {
                put(COL_QUIZ_ID, quizId)
                put(COL_QUESTION_ID, q.id)
                put(COL_TEXT, q.text)
                put(COL_ANSWERS, q.answers.joinToString("|||"))
                put(COL_CORRECT_INDEX, q.correctIndex)
            }
            db.insert(TABLE_NAME, null, values)
        }
        db.close()
    }

    fun getQuestions(quizId: String): List<Question> {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME, null, "$COL_QUIZ_ID=?", arrayOf(quizId), null, null, null
        )
        val list = mutableListOf<Question>()
        while (cursor.moveToNext()) {
            list.add(
                Question(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COL_QUESTION_ID)),
                    text = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEXT)),
                    answers = cursor.getString(cursor.getColumnIndexOrThrow(COL_ANSWERS)).split("|||"),
                    correctIndex = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CORRECT_INDEX))
                )
            )
        }
        cursor.close()
        db.close()
        return list
    }
}