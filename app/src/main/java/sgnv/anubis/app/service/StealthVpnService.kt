package sgnv.anubis.app.service

import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.util.Log

/**
 * Dummy VPN service to force-disconnect any active VPN.
 *
 * Android allows only one VPN at a time. When we establish() our VPN,
 * the system automatically revokes (disconnects) whatever VPN was running.
 * We then immediately close our VPN — result: no VPN is running.
 *
 * Requires VPN consent (prepare). If consent was taken by another VPN app,
 * the caller must re-request via prepareVpn() before calling disconnect().
 */
class StealthVpnService : VpnService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_DISCONNECT) {
            doDisconnect()
        }
        return START_NOT_STICKY
    }

    private fun doDisconnect() {
        try {
            val fd = Builder()
                .addAddress("10.255.255.1", 32)
                .setSession("stealth-disconnect")
                .setBlocking(false)
                .establish()

            if (fd != null) {
                // Our VPN established → other VPN is revoked
                fd.close()
                Log.d(TAG, "Dummy VPN established and closed — other VPN disconnected")
            } else {
                Log.w(TAG, "establish() returned null — no VPN consent")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to establish dummy VPN", e)
        }
        stopSelf()
    }

    override fun onRevoke() {
        stopSelf()
    }

    companion object {
        private const val TAG = "StealthVpnService"
        const val ACTION_DISCONNECT = "sgnv.anubis.app.FORCE_DISCONNECT_VPN"

        /**
         * Check if we have VPN consent.
         * Returns null if we have it, or an Intent to show the system dialog.
         */
        fun prepareVpn(context: Context): Intent? = prepare(context)

        /**
         * Start the dummy VPN to disconnect any active VPN.
         * Only works if we have VPN consent (prepareVpn returns null).
         */
        fun disconnect(context: Context) {
            val intent = Intent(context, StealthVpnService::class.java).apply {
                action = ACTION_DISCONNECT
            }
            context.startService(intent)
        }
    }
}
