package com.fitnessbuddy.di

import com.fitnessbuddy.BuildConfig
import com.fitnessbuddy.data.repository.GeminiRepositoryImpl
import com.fitnessbuddy.domain.repository.GeminiRepository
import com.google.ai.client.generativeai.GenerativeModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeminiModule {

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-3-flash-preview",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class GeminiBindingModule {
    
    @Binds
    @Singleton
    abstract fun bindGeminiRepository(
        geminiRepositoryImpl: GeminiRepositoryImpl
    ): GeminiRepository
}
