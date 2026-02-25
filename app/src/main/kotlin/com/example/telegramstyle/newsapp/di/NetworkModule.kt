package com.example.telegramstyle.newsapp.di

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
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier for RSS OkHttpClient with specific timeout settings.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RssHttpClient

/**
 * Hilt module providing network-related dependencies.
 * Includes OkHttp client, Retrofit, Moshi, and RSS parser configurations.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val RSS_TIMEOUT_SECONDS = 10L
    private const val DEFAULT_TIMEOUT_SECONDS = 30L
    private const val BASE_URL = "https://api.example.com/" // Placeholder, not used for RSS

    /**
     * Provides HTTP logging interceptor for debugging network requests.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Provides the default OkHttpClient with standard timeout settings.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Provides OkHttpClient specifically configured for RSS feed fetching.
     * Uses shorter timeout (10 seconds) as per design requirements.
     */
    @Provides
    @Singleton
    @RssHttpClient
    fun provideRssOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(RSS_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(RSS_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(RSS_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    /**
     * Provides Moshi instance for JSON serialization/deserialization.
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Provides Retrofit instance for API calls.
     * Note: This is primarily for potential future API integrations.
     * RSS parsing uses Rome library directly with OkHttpClient.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Provides MoshiConverterFactory for Retrofit.
     */
    @Provides
    @Singleton
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }
}
