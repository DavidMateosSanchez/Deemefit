package com.example.deemefit.viewmodel

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.*

//Esta clase es la encargada de crear el tiempo que aparece en el cronómetro cuando el usuario lo inicia, aumenta el tiempo en 1 unidad cada segundo
class TimerService : Service() {

    override fun onBind(p0: Intent?): IBinder? = null

    private val timer = Timer()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val time = intent.getIntExtra(TIMER_EXTRA, 0)
        timer.scheduleAtFixedRate(TimeTask(time),0, 1000)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    private inner class TimeTask(private var time:Int) : TimerTask(){
        override fun run() {
            val intent = Intent(TIMER_UPDATED)
            time++
            intent.putExtra(TIMER_EXTRA, time)
            sendBroadcast(intent)
        }
    }

    companion object {
        val TIMER_UPDATED= "timerUpdate"
        val TIMER_EXTRA = "timeExtra"
    }

}