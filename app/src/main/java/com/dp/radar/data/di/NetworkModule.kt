package com.dp.radar.data.di

import com.dp.radar.data.datasources.remote.RadarApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // private const val BASE_URL = "https://us-central1-mockradar-2812e.cloudfunctions.net/serveJson/"
    private const val BASE_URL = com.dp.radar.BuildConfig.BASE_URL

    @Singleton
    @Provides
    fun provideMoshi(): Moshi =
        Moshi
            .Builder()
            .addLast(KotlinJsonAdapterFactory()) // Ensures Kotlin data classes are handled
            .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        // Logging interceptor for debugging network requests
        val logging =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        return OkHttpClient
            .Builder()
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Singleton
    @Provides
    fun provideUserApiService(retrofit: Retrofit): RadarApiService = retrofit.create(RadarApiService::class.java)
}
