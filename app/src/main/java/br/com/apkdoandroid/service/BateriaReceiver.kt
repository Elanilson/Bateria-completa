package br.com.apkdoandroid.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.os.BatteryManager
import br.com.apkdoandroid.interfaces.OnBatteryChangeListener

class BateriaReceiver(private val listener: OnBatteryChangeListener) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            Intent.ACTION_BATTERY_CHANGED -> {
                listener.onBatteryLevelChanged(getBatteryLevel(intent))
                listener.onBatteryStatusChanged(getBatteryStatus(intent))
                listener.onBatteryPluggedTypeChanged(getBatteryPluggedType(intent))
                listener.onBatteryTemperatureChanged(getBatteryTemperature(intent))
                listener.onBatteryVoltageChanged(getBatteryVoltage(intent))
                listener.onBatteryTechnologyChanged(getBatteryTechnology(intent))
                listener.onBatteryHealthChanged(getBatteryHealth(intent))
                listener.onBatteryCapacityChanged(getBatteryCapacity(intent))
                listener.onWirelessChargingStateChanged(isBatteryWirelesslyCharging(intent))

                // Novas informações
                listener.onBatteryPresentChanged(intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false))
                listener.onSmallIconChanged(intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -1))
                listener.onBatteryLowChanged(intent.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, false))



                // Este método ainda pode ser usado para notificar sobre mudanças gerais na bateria
                listener.onBatteryChanged(
                    getBatteryLevel(intent), getBatteryStatus(intent),
                    getBatteryPluggedType(intent), getBatteryTemperature(intent),
                    getBatteryVoltage(intent), getBatteryTechnology(intent),
                    getBatteryHealth(intent), getBatteryCapacity(intent),
                    isBatteryWirelesslyCharging(intent)
                )



            }
            Intent.ACTION_POWER_CONNECTED -> { listener.onCarregando(true)}
            Intent.ACTION_POWER_DISCONNECTED -> { listener.onCarregando(false)}
            else -> {}
        }
    }

    private fun getBatteryLevel(intent: Intent): Int {
        return intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    }

    private fun getBatteryStatus(intent: Intent): Int {
        return intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
    }

    private fun getBatteryPluggedType(intent: Intent): Int {
        return intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
    }

    private fun getBatteryTemperature(intent: Intent): Int {
        return intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
    }

    private fun getBatteryVoltage(intent: Intent): Int {
        return intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
    }

    private fun getBatteryTechnology(intent: Intent): String? {
        return intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
    }

    private fun getBatteryHealth(intent: Intent): Int {
        return intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
    }

    private fun getBatteryCapacity(intent: Intent): Int {
        val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        return if (level != -1 && scale != -1) {
            (level.toFloat() / scale.toFloat() * 100.0f).toInt()
        } else {
            -1
        }
    }


    private fun isBatteryWirelesslyCharging(intent: Intent): Boolean {
        val plugged: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        return plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
    }





}