package br.com.apkdoandroid.interfaces

interface OnBatteryChangeListener {
    fun onBatteryLevelChanged(level: Int)
    fun onBatteryStatusChanged(status: Int)
    fun onBatteryPluggedTypeChanged(plugged: Int)
    fun onBatteryTemperatureChanged(temperature: Int)
    fun onBatteryVoltageChanged(voltage: Int)
    fun onBatteryTechnologyChanged(technology: String?)
    fun onBatteryHealthChanged(health: Int)
    fun onBatteryCapacityChanged(capacity: Int)
    fun onWirelessChargingStateChanged(isWirelessCharging: Boolean)
    fun onPowerSaveModeChanged(isPowerSaveMode: Boolean)
    fun onCarregando(estaCarregando: Boolean = false)

    // Informações adicionais

    fun onBatteryPresentChanged(isBatteryPresent: Boolean)
    fun onSmallIconChanged(smallIcon: Int)
    fun onBatteryLowChanged(isBatteryLow: Boolean)


    // Este método ainda pode ser usado para notificar sobre mudanças gerais na bateria
    fun onBatteryChanged(
        level: Int, status: Int, plugged: Int, temperature: Int, voltage: Int,
        technology: String?, health: Int, capacity: Int, isWirelessCharging: Boolean
    )
}