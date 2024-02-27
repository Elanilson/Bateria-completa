package br.com.apkdoandroid.bateriacompleta

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat

import br.com.apkdoandroid.bateriacompleta.databinding.ActivityMainBinding
import br.com.apkdoandroid.interfaces.OnBatteryChangeListener
import br.com.apkdoandroid.service.BateriaReceiver
import com.devs.vectorchildfinder.VectorChildFinder
import com.devs.vectorchildfinder.VectorDrawableCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), OnBatteryChangeListener {
    private lateinit var bateriaReceiver: BateriaReceiver
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private var pararLoop = true;
    private var levelBateria = 0;
    private var coroutineJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bateriaReceiver = BateriaReceiver(this)


        //alterarCor("raio",binding.imageViewBateriaStatus, contornoColor,R.drawable.bateria_status)

        IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }.also {
            registerReceiver(bateriaReceiver, it)
        }
    }

    override fun onBatteryLevelChanged(level: Int) {
        binding.textViewNivelBateria.setText("${level}%")
        binding.textViewBatteryLevelChanged.setText("Level: ${level}")
        levelBateria = level
        if (coroutineJob?.isActive == true) {
            // Cancelar a coroutine em execução
            coroutineJob?.cancel()
        }
        coroutineJob = CoroutineScope(Dispatchers.IO).launch {

            while (true) {
                when (level) {
                    in 90..100 -> handleBatteryLevel100()
                    in 70..89 -> handleBatteryLevel75()
                    in 40..69 -> handleBatteryLevel50()
                    in 20..39 -> handleBatteryLevel25()
                    in 0..19 -> handleBatteryLevel0To19()
                    else -> handleUnknownBatteryLevel()
                }
            }


        }
    }

    override fun onBatteryStatusChanged(status: Int) {
        val statusText = when (status) {
            1 -> "Desconhecido"
            2 -> "Carregando"
            3 -> "Descarregando"
            4 -> "Em Uso da Bateria"
            5 -> "Carregada"
            else -> "Valor inválido"
        }
        //binding.textViewBatteryStatusChanged.setText("Status: ${status}")
        binding.textviewStatus.setText(statusText)
    }

    override fun onBatteryPluggedTypeChanged(plugged: Int) {
      //  binding.textViewBatteryPluggedTypeChanged.setText("Plugged: ${plugged}")
        val pluggedTypeText = when (plugged) {
            0 -> "Desconhecido"
            1 -> "Conectado à tomada (AC)"
            2 -> "Conectado via USB"
            4 -> "Conectado sem fio"
            else -> "Valor inválido"
        }
       // binding.textviewConectado.setText("${plugged}")
    }

    override fun onBatteryHealthChanged(health: Int) {
       // binding.textViewBatteryHealthChanged.setText("Health: ${health}")
        val healthText = when (health) {
            1 -> "Desconhecido"
            2 -> "Boa"
            3 -> "Superaquecida"
            4 -> "Morta"
            5 -> "Sobretensão"
            6 -> "Falha não especificada"
            else -> "Valor inválido"
        }
        binding.textviewSaude.setText(healthText)
    }
    override fun onBatteryTemperatureChanged(temperature: Int) {
        //binding.textViewBatteryTemperatureChanged.setText("Temperature: ${temperature}")
        val temperatureCelsius = temperature / 10.0
        val temperatureFahrenheit = celsiusToFahrenheit(temperatureCelsius.toDouble()).toInt()
        binding.textviewTemperatura.setText("${temperatureFahrenheit}°F")

    }

    override fun onBatteryVoltageChanged(voltage: Int) {
     //   binding.textViewBatteryVoltageChanged.setText("Voltage: ${voltage}")
        binding.textviewVoltagem.setText("${voltage}mv")
    }

    override fun onBatteryTechnologyChanged(technology: String?) {
      //  binding.textViewBatteryTechnologyChanged.setText("Technology: ${technology}")
        binding.textviewTecnologia.setText("${technology}")
    }


    override fun onBatteryCapacityChanged(capacity: Int) {
        binding.textViewBatteryCapacityChanged.setText("Capacity: ${capacity}")

    }

    override fun onWirelessChargingStateChanged(isWirelessCharging: Boolean) {
        binding.textViewWirelessChargingStateChanged.setText("isWirelessCharging: ${isWirelessCharging}")
    }

    override fun onPowerSaveModeChanged(isPowerSaveMode: Boolean) {
        binding.textViewPowerSaveModeChanged.setText("isPowerSaveMode: ${isPowerSaveMode}")

    }

    override fun onCarregando(estaCarregando: Boolean) {
       val resposta = when(estaCarregando){
            false -> "Não"
            true -> "Sim"
            else -> "Ops"
        }
        binding.textviewConectado.setText(resposta)
    }

    override fun onBatteryPresentChanged(isBatteryPresent: Boolean) {
        binding.textViewBatteryPresentChanged.setText("isBatteryPresent: ${isBatteryPresent}")
    }

    override fun onSmallIconChanged(smallIcon: Int) {
        binding.textViewSmallIconChanged.setText("smallIcon: ${smallIcon}")
    }

    override fun onBatteryLowChanged(isBatteryLow: Boolean) {
        binding.textViewBatteryLowChanged.setText("isBatteryLow: ${isBatteryLow}")
    }

    override fun onBatteryChanged(
        level: Int,
        status: Int,
        plugged: Int,
        temperature: Int,
        voltage: Int,
        technology: String?,
        health: Int,
        capacity: Int,
        isWirelessCharging: Boolean
    ) {

    }

    private suspend fun handleBatteryLevel100() {

        // Ação específica para o nível 100%
        if(levelBateria == 100){
            // Obtenha a cor desejada do resources para cada "name"
            val contornoColor = ContextCompat.getColor(this, R.color.verde200)
            val raio = ContextCompat.getColor(this, R.color.white)
            val cor6 = ContextCompat.getColor(this, R.color.verde200)
            val cor5 = ContextCompat.getColor(this, R.color.verde200)
            val cor4 = ContextCompat.getColor(this, R.color.verde200)
            val cor3 = ContextCompat.getColor(this, R.color.verde200)
            val cor2 = ContextCompat.getColor(this, R.color.verde200)
            val cor1 = ContextCompat.getColor(this, R.color.verde200)
            // Aplique a cor ao vetor com base no "name"
            alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)
        }else{
            nivel6()
        }

    }

    private suspend fun handleBatteryLevel75() {


        nivel5()

    }

    private suspend fun handleBatteryLevel50() {
        if(levelBateria > 50 && levelBateria <=69 ){
            nivel4()
        }else{
            nivel3()
        }
    }

    private suspend fun handleBatteryLevel25() {

        nivel2()



    }

    private suspend fun handleBatteryLevel0To19() {


        nivel1()
        delay(1000)
        nivel0()
        delay(1000)
        Log.d("Loop", "Loop nivel 1")


    }

    private suspend fun handleUnknownBatteryLevel() {
        nivel0()
    }

    private fun celsiusToFahrenheit(celsius: Double): Double {
        return celsius * 9 / 5 + 32
    }

    private suspend fun nivel6() {
        var contornoColor = ContextCompat.getColor(this, R.color.verde200)
        var raio = ContextCompat.getColor(this, R.color.white)
        var cor6 = ContextCompat.getColor(this, R.color.verde200)
        var cor5 = ContextCompat.getColor(this, R.color.verde200)
        var cor4 = ContextCompat.getColor(this, R.color.verde200)
        var cor3 = ContextCompat.getColor(this, R.color.verde200)
        var cor2 = ContextCompat.getColor(this, R.color.verde200)
        var cor1 = ContextCompat.getColor(this, R.color.verde200)
        // Aplique a cor ao vetor com base no "name"
        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)
        delay(1000)
         contornoColor = ContextCompat.getColor(this, R.color.verde200)
         raio = ContextCompat.getColor(this, R.color.white)
         cor6 = ContextCompat.getColor(this, R.color.transparente)
         cor5 = ContextCompat.getColor(this, R.color.verde200)
         cor4 = ContextCompat.getColor(this, R.color.verde200)
         cor3 = ContextCompat.getColor(this, R.color.verde200)
         cor2 = ContextCompat.getColor(this, R.color.verde200)
         cor1 = ContextCompat.getColor(this, R.color.verde200)
        // Aplique a cor ao vetor com base no "name"
        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)
        delay(1000)
    }


    private suspend fun nivel5() {
        var contornoColor = ContextCompat.getColor(this, R.color.verde200)
        var raio = ContextCompat.getColor(this, R.color.white)
        var cor6 = ContextCompat.getColor(this, R.color.transparente)
        var cor5 = ContextCompat.getColor(this, R.color.verde200)
        var cor4 = ContextCompat.getColor(this, R.color.verde200)
        var cor3 = ContextCompat.getColor(this, R.color.verde200)
        var cor2 = ContextCompat.getColor(this, R.color.verde200)
        var cor1 = ContextCompat.getColor(this, R.color.verde200)
        // Aplique a cor ao vetor com base no "name"
        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)
        delay(1000)
         contornoColor = ContextCompat.getColor(this, R.color.verde200)
         raio = ContextCompat.getColor(this, R.color.white)
         cor6 = ContextCompat.getColor(this, R.color.transparente)
         cor5 = ContextCompat.getColor(this, R.color.transparente)
         cor4 = ContextCompat.getColor(this, R.color.verde200)
         cor3 = ContextCompat.getColor(this, R.color.verde200)
         cor2 = ContextCompat.getColor(this, R.color.verde200)
         cor1 = ContextCompat.getColor(this, R.color.verde200)
        // Aplique a cor ao vetor com base no "name"
        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)
        delay(1000)
    }

    private suspend fun nivel4() {
        var contornoColor = ContextCompat.getColor(this, R.color.verde200)
        var raio = ContextCompat.getColor(this, R.color.white)
        var cor6 = ContextCompat.getColor(this, R.color.transparente)
        var cor5 = ContextCompat.getColor(this, R.color.transparente)
        var cor4 = ContextCompat.getColor(this, R.color.verde200)
        var cor3 = ContextCompat.getColor(this, R.color.verde200)
        var cor2 = ContextCompat.getColor(this, R.color.verde200)
        var cor1 = ContextCompat.getColor(this, R.color.verde200)
        // Aplique a cor ao vetor com base no "name"
        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)
        delay(1000)
        contornoColor = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        raio = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        cor6 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        cor5 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        cor4 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        cor3 = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        cor2 = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        cor1 = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)
        delay(1000)
    }

    private suspend fun nivel3() {
        var contornoColor = ContextCompat.getColor(this, R.color.verde200)
        var raio = ContextCompat.getColor(this, R.color.white)
        var cor6 = ContextCompat.getColor(this, R.color.transparente)
        var cor5 = ContextCompat.getColor(this, R.color.transparente)
        var cor4 = ContextCompat.getColor(this, R.color.transparente)
        var cor3 = ContextCompat.getColor(this, R.color.verde200)
        var cor2 = ContextCompat.getColor(this, R.color.verde200)
        var cor1 = ContextCompat.getColor(this, R.color.verde200)
        // Aplique a cor ao vetor com base no "name"
        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)
        delay(1000)
        contornoColor = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        raio = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        cor6 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        cor5 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        cor4 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        cor3 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        cor2 = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        cor1 = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)
        delay(1000)
    }

    private suspend fun nivel2() {

        var contornoColor = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        var raio = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        var cor6 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        var cor5 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        var cor4 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        var cor3 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        var cor2 = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        var cor1 = ContextCompat.getColor(this@MainActivity, R.color.verde200)

        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)

        delay(1000)
         contornoColor = ContextCompat.getColor(this@MainActivity, R.color.verde200)
         raio = ContextCompat.getColor(this@MainActivity, R.color.transparente)
         cor6 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
         cor5 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
         cor4 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
         cor3 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
         cor2 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
         cor1 = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)
        delay(1000)

    }

    private suspend fun nivel1() {
        val contornoColor = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        val raio = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor6 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor5 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor4 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor3 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor2 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor1 = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        // Aplique a cor ao vetor com base no "name"
        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)

    }

    private suspend fun nivel0() {
        val contornoColor = ContextCompat.getColor(this@MainActivity, R.color.verde200)
        val raio = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor6 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor5 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor4 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor3 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor2 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        val cor1 = ContextCompat.getColor(this@MainActivity, R.color.transparente)
        // Aplique a cor ao vetor com base no "name"
        alterarCor(contornoColor, cor1, cor2, cor3, cor4, cor5, cor6, raio)

    }


    suspend fun alterarCor(
        contorno: Int, color1: Int, color2: Int, color3: Int, color4: Int, color5: Int,
        color6: Int, raio: Int
    ) {

        withContext(Dispatchers.Main) {
            // Toast.makeText(this@MainActivity,"Alterando",Toast.LENGTH_SHORT).show()
            val vector = VectorChildFinder(
                this@MainActivity,
                R.drawable.bateria_status,
                binding.imageViewBateriaStatus
            )

            vector.findPathByName("contorno").setFillColor(contorno)
            vector.findPathByName("nivel1").setFillColor(color1)
            vector.findPathByName("nivel2").setFillColor(color2)
            vector.findPathByName("nivel3").setFillColor(color3)
            vector.findPathByName("nivel4").setFillColor(color4)
            vector.findPathByName("nivel5").setFillColor(color5)
            vector.findPathByName("nivel6").setFillColor(color6)
            vector.findPathByName("raio").setFillColor(raio)
        }


    }
}