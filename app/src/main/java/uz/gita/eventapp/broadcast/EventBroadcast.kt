package uz.gita.eventapp.broadcast

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.gita.eventapp.data.room.dao.EventDao
import uz.gita.eventapp.service.EventService
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class EventBroadcast(val cont: Context) : BroadcastReceiver() {

    var tts: TextToSpeech? = null

    @Inject
    lateinit var dao: EventDao

    init {
        if (tts == null) {
            tts = TextToSpeech(cont) { status ->
                if (status != TextToSpeech.ERROR) {
                    tts!!.language = Locale.US
                }
            }
        }

    }

    override fun onReceive(context: Context, intent: Intent) {

        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                event(1)
            }
            Intent.ACTION_SCREEN_OFF -> {
                event(2)
            }
            WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
                val action = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                if (action?.isConnected!!) {
                    event(3)
                } else {
                    event(4)
                }
            }
            Intent.ACTION_POWER_CONNECTED -> {
                val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                var levels = level * 100 / scale
                event(15)
                Log.d("TTT","${levels}t")
                if (levels == 100) {
                    event(13)
                }

            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                event(16)
            }

            Intent.ACTION_TIME_CHANGED -> {
                event(11)
            }
            Intent.ACTION_BATTERY_LOW -> {
                event(14)
            }
            Intent.ACTION_SHUTDOWN -> {
                event(12)
            }

            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                val isAirplaneModeOn = intent.getBooleanExtra("state", false)
                if (isAirplaneModeOn) {
                    event(9)
                } else {
                    event(10)
                }
            }
            Intent.ACTION_HEADSET_PLUG -> {
                val state = intent.getIntExtra("state", -1)
                if (state == 0) {
                    event(8)
                } else if (state == 1) {
                    event(7)

                }
            }
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                if (state == BluetoothAdapter.STATE_OFF) {
                    event(6)
                } else if (state == BluetoothAdapter.STATE_ON) {
                    event(5)
                }
            }
            Intent.ACTION_REBOOT -> {
                context?.startService(Intent(context, EventService::class.java))
            }

        }
        if (intent.action!!.matches("android.location.PROVIDERS_CHANGED".toRegex())) {

            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            var isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (isGpsEnabled || isNetworkEnabled) {
                event(17)
            } else {
                event(18)
            }

        }


    }

    private fun event(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val data = dao.getEventGetById(id)
            if (data.state == 1) {
                speak(data.name)
            }
        }
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "This is screen on")
    }
}