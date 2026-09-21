package com.example.habithive.data

import android.content.Context
import android.util.Log
import com.example.habithive.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// Holds the logged-in user's token so we can attach it to REST calls.
class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("habithive", Context.MODE_PRIVATE)
    var accessToken: String? get() = prefs.getString("token", null)
        set(v) { prefs.edit().putString("token", v).apply() }
    var userId: String? get() = prefs.getString("uid", null)
        set(v) { prefs.edit().putString("uid", v).apply() }
    val isLoggedIn get() = accessToken != null
    fun clear() { prefs.edit().clear().apply() }
}

// Supabase Auth endpoints (register + login)
interface AuthApi {
    @POST("auth/v1/signup")
    suspend fun signUp(@Body body: Credentials): AuthResponse

    @POST("auth/v1/token")
    suspend fun login(
        @Query("grant_type") grantType: String = "password",
        @Body body: Credentials
    ): AuthResponse
}

// Supabase REST (PostgREST) endpoints for habits + logs
interface RestApi {
    // ---- habits ----
    @GET("rest/v1/habits")
    suspend fun getHabits(
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): List<Habit>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/habits")
    suspend fun addHabit(@Body habit: Habit): List<Habit>

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/habits")
    suspend fun updateHabit(
        @Query("id") idEq: String,               // pass "eq.<id>"
        @Body fields: Map<String, String>
    ): List<Habit>

    @DELETE("rest/v1/habits")
    suspend fun deleteHabit(@Query("id") idEq: String): Response<Unit>  // pass "eq.<id>"

    // ---- habit logs ----
    @GET("rest/v1/habit_logs")
    suspend fun getLogs(
        @Query("select") select: String = "*",
        @Query("order") order: String = "log_date.desc"
    ): List<HabitLog>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/habit_logs")
    suspend fun addLog(@Body log: HabitLog): List<HabitLog>

    @DELETE("rest/v1/habit_logs")
    suspend fun deleteLog(@Query("id") idEq: String): Response<Unit>    // pass "eq.<id>"
}

// Builds Retrofit once and shares it. Adds apikey + Bearer token to every request.
object ServiceLocator {
    lateinit var session: SessionManager
    lateinit var authApi: AuthApi
    lateinit var restApi: RestApi

    fun init(context: Context) {
        session = SessionManager(context)

        val logging = HttpLoggingInterceptor { msg -> Log.d("HTTP", msg) }
            .apply { level = HttpLoggingInterceptor.Level.BASIC }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                    .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                session.accessToken?.let { builder.addHeader("Authorization", "Bearer $it") }
                chain.proceed(builder.build())
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("${BuildConfig.SUPABASE_URL}/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authApi = retrofit.create(AuthApi::class.java)
        restApi = retrofit.create(RestApi::class.java)
    }
}