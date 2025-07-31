package com.example.edifyhub.student

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import com.google.firebase.Timestamp
import java.util.*

class AttendedQuizDbHelper(context: Context) : SQLiteOpenHelper(context, "attended_quiz.db", null, 1) {

    companion object {
        const val TABLE_NAME = "attended_quiz"
        const val COL_ID = "id"
        const val COL_USER_ID = "user_id"
        const val COL_QUIZ_ID = "quiz_id"
        const val COL_TEACHER_ID = "teacher_id"
        const val COL_QUIZ_NAME = "quiz_name"
        const val COL_TEACHER_NAME = "teacher_name"
        const val COL_SUBJECT = "subject"
        const val COL_SCORE = "score"
        const val COL_TOTAL = "total"
        const val COL_TIMESTAMP = "timestamp"
        const val COL_MEETING_JOIN_URL = "meeting_join_url"
        const val COL_MEETING_AT = "meeting_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_USER_ID TEXT," +
                    "$COL_QUIZ_ID TEXT," +
                    "$COL_TEACHER_ID TEXT," +
                    "$COL_QUIZ_NAME TEXT," +
                    "$COL_TEACHER_NAME TEXT," +
                    "$COL_SUBJECT TEXT," +
                    "$COL_SCORE INTEGER," +
                    "$COL_TOTAL INTEGER," +
                    "$COL_TIMESTAMP INTEGER," +
                    "$COL_MEETING_JOIN_URL TEXT," +
                    "$COL_MEETING_AT INTEGER" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun saveAttendedQuizzes(userId: String, quizzes: List<AttendedQuiz>) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COL_USER_ID=?", arrayOf(userId))
        for (quiz in quizzes) {
            val values = ContentValues().apply {
                put(COL_USER_ID, userId)
                put(COL_QUIZ_ID, quiz.quizId)
                put(COL_TEACHER_ID, quiz.teacherId)
                put(COL_QUIZ_NAME, quiz.quizName)
                put(COL_TEACHER_NAME, quiz.teacherName)
                put(COL_SUBJECT, quiz.subject)
                put(COL_SCORE, quiz.score)
                put(COL_TOTAL, quiz.total)
                put(COL_TIMESTAMP, quiz.timestamp?.seconds)
                put(COL_MEETING_JOIN_URL, quiz.meetingJoinUrl)
                put(COL_MEETING_AT, quiz.meetingAt?.time)
            }
            db.insert(TABLE_NAME, null, values)
        }
        db.close()
    }

    fun getAttendedQuizzes(userId: String): List<AttendedQuiz> {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME, null, "$COL_USER_ID=?", arrayOf(userId), null, null, null
        )
        val list = mutableListOf<AttendedQuiz>()
        while (cursor.moveToNext()) {
            list.add(
                AttendedQuiz(
                    quizId = cursor.getString(cursor.getColumnIndexOrThrow(COL_QUIZ_ID)),
                    teacherId = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEACHER_ID)),
                    quizName = cursor.getString(cursor.getColumnIndexOrThrow(COL_QUIZ_NAME)),
                    teacherName = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEACHER_NAME)),
                    subject = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT)),
                    score = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCORE)),
                    total = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL)),
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)).let {
                        if (it > 0) Timestamp(it, 0) else null
                    },
                    meetingJoinUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_MEETING_JOIN_URL)),
                    meetingAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_MEETING_AT)).let {
                        if (it > 0) Date(it) else null
                    }
                )
            )
        }
        cursor.close()
        db.close()
        return list
    }
}