package com.happyminds.thepattapp

import android.app.Application
import com.happyminds.thepattapp.di.initKoin
import org.koin.android.ext.koin.androidContext

class PattApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@PattApp)
        }
    }
}
