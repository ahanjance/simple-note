package com.example.simplenote.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [LocalNote::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 1 -> 2: add 'isSynced' and 'idOnServer'
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val cursor = database.query("PRAGMA table_info(notes)")
                val columns = mutableSetOf<String>()
                while (cursor.moveToNext()) {
                    columns.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
                }
                cursor.close()

                if (!columns.contains("isSynced")) {
                    database.execSQL(
                        "ALTER TABLE notes ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 0"
                    )
                }

                if (!columns.contains("idOnServer")) {
                    database.execSQL(
                        "ALTER TABLE notes ADD COLUMN idOnServer INTEGER"
                    )
                }
            }
        }

        // Migration from version 2 -> 3 (if needed, empty here but prepared for future changes)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Future-proof: you can add more columns or changes here
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
