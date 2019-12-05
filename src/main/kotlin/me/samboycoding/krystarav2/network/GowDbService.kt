package me.samboycoding.krystarav2.network

import me.samboycoding.krystarav2.network.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

val DEBUG = true

interface GowDbService {

    @GET("classes")
    fun getClasses(): Call<ClassesResponse>

    @GET("classes/{id}")
    fun getClass(@Path("id") id: Int): Call<ClassesResponse>

    @GET("kingdoms")
    fun getKingdoms(): Call<KingdomsResponse>

    @GET("kingdoms/{id}")
    fun getKingdom(@Path("id") id: Int): Call<KingdomsResponse>

    @GET("spells")
    fun getSpells(): Call<SpellsResponse>

    @GET("spells/{id}")
    fun getSpell(@Path("id") id: Int): Call<SpellsResponse>

    @GET("traits")
    fun getTraits(): Call<TraitsResponse>

    @GET("traits/{id}")
    fun getTrait(@Path("id") id: Int): Call<TraitsResponse>

    @GET("traitstones")
    fun getTraitstones(): Call<TraitstonesResponse>

    @GET("traitstones/{id}")
    fun getTraitstone(@Path("id") id: Int): Call<TraitstonesResponse>

    @GET("troops")
    fun getTroops(): Call<TroopsResponse>

    @GET("troops/{id}")
    fun getTroop(@Path("id") id: Int): Call<TroopsResponse>

    @GET("weapons")
    fun getWeapons(): Call<WeaponsResponse>

    @GET("weapons/{id}")
    fun getWeapon(@Path("id") id: Int): Call<WeaponsResponse>

    @GET("searches/classes")
    fun searchClasses(@Query("term") searchTerm: String): Call<ClassesResponse>

    @GET("searches/kingdoms")
    fun searchKingdoms(@Query("term") searchTerm: String): Call<KingdomsResponse>

    @GET("searches/spells")
    fun searchSpells(@Query("term") searchTerm: String): Call<SpellsResponse>

    @GET("searches/all")
    fun searchAll(@Query("term") searchTerm: String): Call<AllResponse>

    companion object {
        private val okClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    @Suppress("ConstantConditionIf")
                    level = if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                })
                .build()
        }

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("http://gowdb.com/en-US/api/")
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val instance by lazy {
            retrofit.create(GowDbService::class.java)
        }
    }
}