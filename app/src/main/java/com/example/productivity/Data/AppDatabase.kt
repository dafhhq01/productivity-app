package com.example.productivity.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Tugas::class, Subtugas::class, Transaksi::class, Budget::class, Investasi::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tugasDao(): TugasDao
    abstract fun subtugasDao(): SubtugasDao
    abstract fun transaksiDao(): TransaksiDao
    abstract fun budgetDao(): BudgetDao
    abstract fun investasiDao(): InvestasiDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "productivity_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
