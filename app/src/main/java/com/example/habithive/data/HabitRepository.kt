package com.example.habithive.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.database.getStringOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.time.Instant
import java.util.UUID

/** Local-first data access: the UI reads/writes the phone DB; sync() talks to Supabase. */
class HabitRepository(private val ctx: Context, helper: LocalDb, private val api: RestApi) {
    private val db = helper.writableDatabase
    private val mutex = Mutex()          // never run two syncs at once
    private val tag = "REPO"
    private val cm get() = ctx.getSystemService(ConnectivityManager::class.java)

    // ---------- connectivity ----------
    fun isOnline(): Boolean {
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    fun registerNetworkCallback(onAvailable: () -> Unit): ConnectivityManager.NetworkCallback {
        val cb = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { onAvailable() }
        }
        cm.registerDefaultNetworkCallback(cb)
        return cb
    }
    fun unregisterNetworkCallback(cb: ConnectivityManager.NetworkCallback) =
        runCatching { cm.unregisterNetworkCallback(cb) }

    // ---------- reads (always from the phone) ----------
    suspend fun habits(): List<Habit> = withContext(Dispatchers.IO) {
        val out = mutableListOf<Habit>()
        db.rawQuery("SELECT id,name,category,color,frequency,reminder_time,created_at FROM habits " +
                "WHERE deleted=0 ORDER BY created_at DESC", null).use { c ->
            while (c.moveToNext()) out += Habit(c.getString(0), null, c.getString(1),
                c.getStringOrNull(2), c.getStringOrNull(3), c.getStringOrNull(4),
                c.getStringOrNull(5), c.getStringOrNull(6))
        }
        out
    }
    suspend fun logs(): List<HabitLog> = withContext(Dispatchers.IO) {
        val out = mutableListOf<HabitLog>()
        db.rawQuery("SELECT id,habit_id,log_date FROM logs WHERE deleted=0", null).use { c ->
            while (c.moveToNext()) out += HabitLog(id = c.getString(0), habitId = c.getString(1), logDate = c.getString(2))
        }
        out
    }
    suspend fun pendingCount(): Int = withContext(Dispatchers.IO) {
        db.rawQuery("SELECT (SELECT COUNT(*) FROM habits WHERE synced=0)+(SELECT COUNT(*) FROM logs WHERE synced=0)", null)
            .use { it.moveToFirst(); it.getInt(0) }
    }

    // ---------- writes (phone first, flagged "not synced") ----------
    suspend fun addHabit(name: String) = withContext(Dispatchers.IO) {
        val cv = ContentValues().apply {
            put("id", UUID.randomUUID().toString()); put("name", name)
            put("created_at", Instant.now().toString()); put("synced", 0); put("deleted", 0)
        }
        db.insert("habits", null, cv)
        Log.d(tag, "Saved habit locally: $name")
    }
    suspend fun renameHabit(id: String, name: String) = withContext(Dispatchers.IO) {
        db.execSQL("UPDATE habits SET name=?, synced=0 WHERE id=?", arrayOf(name, id))
    }
    suspend fun deleteHabit(id: String) = withContext(Dispatchers.IO) {
        val onServer = db.rawQuery("SELECT synced FROM habits WHERE id=?", arrayOf(id))
            .use { it.moveToFirst() && it.getInt(0) == 1 }
        db.delete("logs", "habit_id=?", arrayOf(id))
        if (onServer) db.execSQL("UPDATE habits SET deleted=1, synced=0 WHERE id=?", arrayOf(id))
        else db.delete("habits", "id=?", arrayOf(id))
    }
    suspend fun setDoneToday(habitId: String, date: String, done: Boolean) = withContext(Dispatchers.IO) {
        if (done) {
            val exists = db.rawQuery("SELECT 1 FROM logs WHERE habit_id=? AND log_date=? AND deleted=0",
                arrayOf(habitId, date)).use { it.moveToFirst() }
            if (!exists) db.insert("logs", null, ContentValues().apply {
                put("id", UUID.randomUUID().toString()); put("habit_id", habitId)
                put("log_date", date); put("synced", 0); put("deleted", 0)
            })
        } else {
            db.delete("logs", "habit_id=? AND log_date=? AND synced=0", arrayOf(habitId, date))
            db.execSQL("UPDATE logs SET deleted=1, synced=0 WHERE habit_id=? AND log_date=? AND synced=1",
                arrayOf(habitId, date))
        }
    }

    // ---------- sync: push my changes, then pull the server's copy ----------
    suspend fun sync(): Boolean = mutex.withLock {
        withContext(Dispatchers.IO) {
            if (!isOnline()) { Log.d(tag, "Offline - sync postponed"); return@withContext false }
            try {
                // 1) deletes
                for (id in ids("logs", "deleted=1")) { ok(api.deleteLog("eq.$id")); db.delete("logs", "id=?", arrayOf(id)) }
                for (id in ids("habits", "deleted=1")) { ok(api.deleteHabit("eq.$id")); db.delete("habits", "id=?", arrayOf(id)) }
                // 2) new + changed rows
                val hp = unsyncedHabits()
                if (hp.isNotEmpty()) { ok(api.upsertHabits(hp)); hp.forEach { db.execSQL("UPDATE habits SET synced=1 WHERE id=?", arrayOf(it.id)) } }
                val lp = unsyncedLogs()
                if (lp.isNotEmpty()) { ok(api.upsertLogs(lp)); lp.forEach { db.execSQL("UPDATE logs SET synced=1 WHERE id=?", arrayOf(it.id)) } }
                // 3) pull the server's copy
                val rh = api.getHabits(); val rl = api.getLogs()
                db.beginTransaction()
                try {
                    db.delete("habits", "synced=1", null); db.delete("logs", "synced=1", null)
                    rh.forEach { h -> db.insertWithOnConflict("habits", null, ContentValues().apply {
                        put("id", h.id); put("name", h.name); put("category", h.category); put("color", h.color)
                        put("frequency", h.frequency); put("reminder_time", h.reminderTime)
                        put("created_at", h.createdAt); put("synced", 1); put("deleted", 0)
                    }, SQLiteDatabase.CONFLICT_REPLACE) }
                    rl.forEach { l -> db.insertWithOnConflict("logs", null, ContentValues().apply {
                        put("id", l.id); put("habit_id", l.habitId); put("log_date", l.logDate)
                        put("synced", 1); put("deleted", 0)
                    }, SQLiteDatabase.CONFLICT_REPLACE) }
                    db.setTransactionSuccessful()
                } finally { db.endTransaction() }
                Log.d(tag, "Sync OK: pushed ${hp.size} habits, ${lp.size} logs")
                true
            } catch (e: Exception) { Log.e(tag, "Sync failed", e); false }
        }
    }

    /** Called on logout so the next user never sees this user's data. */
    suspend fun wipe() = withContext(Dispatchers.IO) { db.delete("habits", null, null); db.delete("logs", null, null) }

    // ---------- helpers ----------
    private fun ok(r: Response<Unit>) { if (!r.isSuccessful) throw IOException("HTTP ${r.code()}") }
    private fun ids(table: String, where: String): List<String> {
        val out = mutableListOf<String>()
        db.rawQuery("SELECT id FROM $table WHERE $where", null).use { while (it.moveToNext()) out += it.getString(0) }
        return out
    }
    private fun unsyncedHabits(): List<Habit> {
        val out = mutableListOf<Habit>()
        db.rawQuery("SELECT id,name,category,color,frequency,reminder_time,created_at FROM habits WHERE synced=0 AND deleted=0", null).use { c ->
            while (c.moveToNext()) out += Habit(c.getString(0), null, c.getString(1), c.getStringOrNull(2),
                c.getStringOrNull(3), c.getStringOrNull(4), c.getStringOrNull(5), c.getStringOrNull(6))
        }
        return out
    }
    private fun unsyncedLogs(): List<HabitLog> {
        val out = mutableListOf<HabitLog>()
        db.rawQuery("SELECT id,habit_id,log_date FROM logs WHERE synced=0 AND deleted=0", null).use { c ->
            while (c.moveToNext()) out += HabitLog(id = c.getString(0), habitId = c.getString(1), logDate = c.getString(2))
        }
        return out
    }
}
