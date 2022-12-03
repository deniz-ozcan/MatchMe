package com.denizozcan.matchme
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class SoundService : Service() {
    internal lateinit var player: MediaPlayer
    lateinit var mp: MediaPlayer

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onCreate(){
        super.onCreate()
        player=MediaPlayer.create(applicationContext, R.raw.backround_music)
        player.isLooping = true
        player.setVolume(100f, 100f)
    }

    override fun onStart(intent: Intent, startId: Int){
        player.start()
    }

    override fun onDestroy(){
        player.stop()
    }

    fun playWhenFinished(context1: Context){
        mp=MediaPlayer.create(context1, R.raw.when_finished)
        mp.start()
    }

    fun playWhenMatched(context2: Context){
        mp=MediaPlayer.create(context2, R.raw.when_matched)
        mp.start()
    }

    fun playWhenWin(context3: Context){
        mp.stop()
        mp=MediaPlayer.create(context3, R.raw.when_win)
        mp.start()
    }
}