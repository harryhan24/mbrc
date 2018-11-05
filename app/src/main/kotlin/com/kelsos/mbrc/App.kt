package com.kelsos.mbrc

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.annotation.CallSuper
import androidx.core.content.getSystemService
import androidx.multidex.MultiDexApplication
import com.chibatching.kotpref.Kotpref
import com.github.anrwatchdog.ANRError
import com.github.anrwatchdog.ANRWatchDog
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kelsos.mbrc.di.modules.appModule
import com.kelsos.mbrc.di.modules.uiModule
import com.kelsos.mbrc.utilities.CustomLoggingTree
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import timber.log.Timber


@SuppressLint("Registered")
open class App : MultiDexApplication() {

  private var refWatcher: RefWatcher? = null

  @CallSuper
  override fun onCreate() {
    super.onCreate()
    initialize()
  }

  protected open fun modules(): List<Module> {
    val androidModule = module {
      val app = this@App as Application

      single { app }
      single { app.resources }
      single { checkNotNull(app.getSystemService<ActivityManager>()) }
      single { checkNotNull(app.getSystemService<AudioManager>()) }
      single { checkNotNull(app.getSystemService<NotificationManager>()) }
      single { checkNotNull(app.getSystemService<WifiManager>()) }
      single { checkNotNull(app.getSystemService<ConnectivityManager>()) }
    }
    return listOf(appModule, uiModule, androidModule)
  }

  protected open fun initialize() {
    if (!testMode()) {
      AndroidThreeTen.init(this)
    }

    startKoin(this, modules())

    initializeTimber()
    initializeLeakCanary()
    ANRWatchDog()
      .setANRListener { onAnr(it) }
      .setIgnoreDebugger(true)
      .start()

    Kotpref.init(this)
  }

  protected open fun onAnr(anrError: ANRError?) {
    Timber.v(anrError, "ANR error")
  }

  private fun initializeLeakCanary() {
    if (testMode()) {
      return
    }

    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return
    }
    refWatcher = installLeakCanary()
  }

  private fun initializeTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(CustomLoggingTree.create())
    }
  }


  internal open fun installLeakCanary(): RefWatcher {
    return RefWatcher.DISABLED
  }

  fun getRefWatcher(context: Context): RefWatcher? {
    val application = context.applicationContext as App
    return application.refWatcher
  }

  internal open fun testMode(): Boolean = false
}