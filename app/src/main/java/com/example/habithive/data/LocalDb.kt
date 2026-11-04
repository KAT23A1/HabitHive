package com.example.habithive.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** The phone's own copy of the data. synced=1 once Supabase has it; deleted=1 means "delete on the server at next sync". */
class LocalDb(ctx: Context) : SQLiteOpenHelper(ctx, "habithive_local.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE habits(
            id TEXT PRIMARY KEY, name TEXT NOT NULL, category TEXT, color TEXT,
            frequency TEXT, reminder_time TEXT, created_at TEXT,
            synced INTEGER NOT NULL DEFAULT 0, deleted INTEGER NOT NULL DEFAULT 0)""")
        db.execSQL("""CREATE TABLE logs(
            id TEXT PRIMARY KEY, habit_id TEXT NOT NULL, log_date TEXT NOT NULL,
            synced INTEGER NOT NULL DEFAULT 0, deleted INTEGER NOT NULL DEFAULT 0)""")
    }
    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS habits"); db.execSQL("DROP TABLE IF EXISTS logs"); onCreate(db)
    }
}
