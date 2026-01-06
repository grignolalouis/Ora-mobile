package com.ora.app

import android.app.Application
import com.ora.app.core.di.coreModule
import com.ora.app.core.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class OraApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@OraApplication)
            modules(
                coreModule,
                networkModule
            )
        }
    }
}
