package com.kelsos.mbrc

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Debug
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusLiveDataProvider
import com.kelsos.mbrc.di.inject
import com.kelsos.mbrc.networking.ClientConnectionUseCase
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.protocol.VolumeInteractor
import com.kelsos.mbrc.platform.ServiceChecker
import kotterknife.bindView
import timber.log.Timber
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class NavigationActivity : AppCompatActivity() {

  @Inject
  lateinit var serviceChecker: ServiceChecker

  @Inject
  lateinit var volumeInteractor: VolumeInteractor

  @Inject
  lateinit var connectionStatusLiveDataProvider: ConnectionStatusLiveDataProvider

  @Inject
  lateinit var clientConnectionUseCase: ClientConnectionUseCase

  private val navigationView: NavigationView by bindView(R.id.nav_view)

  private lateinit var connectText: TextView
  private lateinit var connect: ImageView
  private lateinit var drawerLayout: DrawerLayout
  private lateinit var drawerToggle: ActionBarDrawerToggle

  private fun onConnectLongClick(): Boolean {
    serviceChecker.startServiceIfNotRunning()
    clientConnectionUseCase.connect()
    return true
  }

  private fun onConnectClick() {
    serviceChecker.startServiceIfNotRunning()
    clientConnectionUseCase.connect()
  }

  private fun onConnection(connectionStatus: ConnectionStatus) {
    Timber.v("Handling new connection status ${Connection.string(connectionStatus.status)}")

    @StringRes val resId: Int
    @ColorRes val colorId: Int
    when (connectionStatus.status) {
      Connection.OFF -> {
        resId = R.string.drawer_connection_status_off
        colorId = R.color.black
      }
      Connection.ON -> {
        resId = R.string.drawer_connection_status_on
        colorId = R.color.accent
      }
      Connection.ACTIVE -> {
        resId = R.string.drawer_connection_status_active
        colorId = R.color.power_on
      }
      else -> {
        resId = R.string.drawer_connection_status_off
        colorId = R.color.black
      }
    }

    connectText.setText(resId)
    connect.setColorFilter(ContextCompat.getColor(this, colorId))
  }

  private fun setupConnectionIndicator() {
    val header = navigationView.getHeaderView(0)
    connectText = header.findViewById(R.id.nav_connect_text)
    connect = header.findViewById<ImageView>(R.id.connect_button).apply {
      setOnClickListener { onConnectClick() }
      setOnLongClickListener { onConnectLongClick() }
    }
  }

  private fun setupToolbar() {
    setSupportActionBar(findViewById(R.id.toolbar))
    supportActionBar?.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)
    }
  }

  private fun setupNavigationDrawer() {
    drawerLayout = findViewById(R.id.drawer_layout)
    drawerToggle = ActionBarDrawerToggle(
      this,
      drawerLayout,
      R.string.drawer_open,
      R.string.drawer_close
    )
    drawerLayout.addDrawerListener(drawerToggle)

    val navController = findNavController(R.id.main_navigation_fragment)
    setupWithNavController(findViewById<NavigationView>(R.id.nav_view), navController)
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    drawerToggle.syncState()
    connectionStatusLiveDataProvider.observe(this) {
      onConnection(it)
    }
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    return when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> true
      KeyEvent.KEYCODE_VOLUME_DOWN -> true
      else -> super.onKeyUp(keyCode, event)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    val scopes = Toothpick.openScopes(application, this)
    scopes.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_navigation)
    scopes.inject(this)
    setupToolbar()
    setupNavigationDrawer()
    setupConnectionIndicator()
  }

  override fun onResume() {
    super.onResume()
    if (!BuildConfig.DEBUG) {
      return
    }

    // don't even consider it otherwise
    if (Debug.isDebuggerConnected()) {
      Timber.d("Keeping screen on for debugging.")
      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
      window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      Timber.d("Keeping screen on for debugging is now deactivated.")
    }
  }

  override fun onNavigateUp(): Boolean {
    return navigateUp(drawerLayout, findNavController(R.id.main_navigation_fragment))
  }

  override fun onDestroy() {
    super.onDestroy()
    connectionStatusLiveDataProvider.removeObservers(this)
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    return when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> {
        volumeInteractor.increment()
        true
      }
      KeyEvent.KEYCODE_VOLUME_DOWN -> {
        volumeInteractor.decrement()
        true
      }
      else -> super.onKeyDown(keyCode, event)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // The action bar home/up action should open or close the drawer.
    // [ActionBarDrawerToggle] will take care of this.
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    // Pass any configuration change to the drawer toggle.
    drawerToggle.onConfigurationChanged(newConfig)
  }

  companion object {
    fun start(context: Context) {
      with(context) {
        startActivity(Intent(this, NavigationActivity::class.java))
      }
    }
  }
}
