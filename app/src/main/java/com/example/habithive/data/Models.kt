package com.example.habithive.data

import com.google.gson.annotations.SerializedName

// ---- Auth request/response (Supabase Auth REST) ----
data class Credentials(val email: String, val password: String)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
    val user: SupabaseUser?
)

data class SupabaseUser(val id: String, val email: String?)

// ---- Habit ----
data class Habit(
    val id: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    val name: String,
    val category: String? = null,
    val color: String? = null,
    val frequency: String? = null,
    @SerializedName("reminder_time") val reminderTime: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

// ---- HabitLog: one row each time a habit is ticked complete on a day ----
data class HabitLog(
    val id: String? = null,
    @SerializedName("habit_id") val habitId: String,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("log_date") val logDate: String,   // "yyyy-MM-dd"
    val completed: Boolean = true,
    @SerializedName("created_at") val createdAt: String? = null
)