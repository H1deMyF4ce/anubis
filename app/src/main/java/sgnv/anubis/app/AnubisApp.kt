package sgnv.anubis.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import sgnv.anubis.app.data.db.AppDatabase
import sgnv.anubis.app.shizuku.ShizukuManager

class AnubisApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    lateinit var shizukuManager: ShizukuManager
        private set

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Init Shizuku once — all components share this instance
        shizukuManager = ShizukuManager(packageManager)
        shizukuManager.startListening()
    }

    override fun onTerminate() {
        shizukuManager.stopListening()
        shizukuManager.unbindUserService()
        super.onTerminate()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "VPN Stealth Monitor",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Мониторинг состояния VPN и замороженных приложений"
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "anubis_monitor"
    }
}
