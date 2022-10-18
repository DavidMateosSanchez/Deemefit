package com.example.deemefit.view

import android.app.*
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.deemefit.R
import com.example.deemefit.databinding.ActivityCronometroBinding
import com.example.deemefit.viewmodel.TimerService
import android.app.PendingIntent
import android.content.*


class CronometroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCronometroBinding
    private lateinit var serviceIntent: Intent
    private var time = 0
    private var tiempoDescanso = 0
    private val channelName = "channelName"
    private val channelId = "channelId"
    private lateinit var notificationCustomStyle: Notification
    private val notificationCustomStyleID = 1
    private lateinit var myRunnable: Runnable
    private lateinit var myHandler: Handler

    companion object {
        const val INTENT_REQUEST = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCronometroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        crearCanalNotificaciones()
        crearEstiloNotificaciones()
        myHandler = Handler()

        binding.btnStop.isEnabled = true
        binding.btnPause.isEnabled = false


        binding.btnPlay.setOnClickListener {
            if (binding.etTiempoDescanso.text.isNotEmpty()) {
                val tiempoTotal = Integer.parseInt(binding.etTiempoDescanso.text.toString())
                if (tiempoTotal <= 1200){
                    iniciarTimer()
                    crearHandler()
                } else{
                    Toast.makeText(this, "Tiempo de descanso introducido demasiado elevado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Introduce un tiempo de descanso", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnPause.setOnClickListener {
            detenerTimer()
        }

        binding.btnStop.setOnClickListener {
            resetearTimer()
        }

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(actualizarTiempo, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private fun resetearTimer() {
        detenerTimer()
        time = 0
        binding.tvTiempo.text = getTimeStringFromInt(time)
        binding.btnPlay.isEnabled = true
        binding.btnStop.isEnabled = false
        binding.btnPause.isEnabled = false
        binding.etTiempoDescanso.isEnabled = true
    }

    private fun iniciarTimer() {
        serviceIntent.putExtra(TimerService.TIMER_EXTRA, time)
        startService(serviceIntent)
        binding.btnStop.isEnabled = true
        binding.btnPause.isEnabled = true
        binding.btnPlay.isEnabled = false
        binding.etTiempoDescanso.isEnabled = false
    }

    private fun detenerTimer() {
        stopService(serviceIntent)
        binding.btnPlay.isEnabled = true
        binding.btnStop.isEnabled = true
        binding.btnPause.isEnabled = false
    }

    private val actualizarTiempo: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getIntExtra(TimerService.TIMER_EXTRA, 0)
            binding.tvTiempo.text = getTimeStringFromInt(time)
        }
    }

    private fun getTimeStringFromInt(time: Int): String {
        val resultInt = time

        return formatoTiempo(resultInt)
    }

    private fun formatoTiempo(sec: Int): String = String.format("%02d", sec)

    //Con esta función creamos un estilo personalizado para la notificación mostrada, le pasamos el icono, los diferentes layouts, el sonido de la notificación...
    private fun crearEstiloNotificaciones() {

        val pendingIntent = Intent(this, CronometroActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(CronometroActivity::class.java)
        stackBuilder.addNextIntent(pendingIntent)
        val resultPendingIntent =
            stackBuilder.getPendingIntent(INTENT_REQUEST, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationLayout = RemoteViews(packageName, R.layout.notification_small)
        val notificationLayoutExpended = RemoteViews(packageName, R.layout.notification_expanded)

        notificationCustomStyle = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_notification)
            it.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            it.setCustomContentView(notificationLayout)
            it.setCustomBigContentView(notificationLayoutExpended)
            it.setContentIntent(resultPendingIntent)
            it.setAutoCancel(true)
            it.setDefaults(Notification.DEFAULT_SOUND)
        }.build()
    }

    //Con esta función creamos el canal que utilizará la notificación, dándole una importancia y diciendole al manager del sistema que utilice este canal para mostrar nuestra notificación
    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
                lightColor = Color.RED
                enableLights(true)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun detenerHandler(){
        myHandler.removeCallbacks(myRunnable)
    }

    //Con esta función creamos un Handler que se repetirá en bucle y lo utilizaremos para detectar el momento en el que el tiempo del cronómetro coincide con el tiempo de descanso aportado por el ususario
    private fun crearHandler() {
        val notificationManager = NotificationManagerCompat.from(this)
        tiempoDescanso = Integer.parseInt(binding.etTiempoDescanso.text.toString())

        myRunnable = Runnable {
            if (time == tiempoDescanso && binding.etTiempoDescanso.text.isNotEmpty()) {
                resetearTimer()
                notificationManager.notify(notificationCustomStyleID, notificationCustomStyle)
                detenerHandler()
            }
            myHandler.postDelayed(myRunnable, 500)
        }
        myHandler.postDelayed(myRunnable, 1000)
    }
}
