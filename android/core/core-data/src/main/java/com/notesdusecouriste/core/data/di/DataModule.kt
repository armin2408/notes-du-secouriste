package com.notesdusecouriste.core.data.di

import android.content.Context
import androidx.room.Room
import com.notesdusecouriste.core.data.db.AppDatabase
import com.notesdusecouriste.core.data.db.migration.MIGRATION_1_2
import com.notesdusecouriste.core.data.db.migration.MIGRATION_2_3
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "notes_du_secouriste.db",
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
}
