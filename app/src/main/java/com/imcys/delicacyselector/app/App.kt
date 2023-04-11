package com.imcys.delicacyselector.app

import android.app.Application
import com.imcys.delicacyselector.data.AppDatabase

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        appDatabase = AppDatabase.getDatabase(this)

    }

    companion object {
        lateinit var appDatabase: AppDatabase
    }
}