package edu.temple.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private var myBoundItem: Boolean = false
    private var theCurrService = TimerService()

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            myBoundItem = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            myBoundItem = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //running and paused, running and not paused, or just not running

        //implement start and stop functionality for the service, bind the service
        //also, stop is always stop, but for start, it starts, pauses, and unpauses
        //change its text displaying such. Like stop should make it start,
        //once started it becomes pause, when pause become unpause and when unpause
        //becomes paused
        var myStartButton = findViewById<Button>(R.id.startButton)

        //make handler display the countdown in activity

        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        myStartButton.setOnClickListener {
            //check the service's state

            if(!theCurrService.TimerBinder().isRunning){
                //if it's not running, make it a run, and change the name
                myStartButton.text = "pause"
                theCurrService.TimerBinder().start(10)
            }
            else if(theCurrService.TimerBinder().paused){
                //make it not paused
                myStartButton.text = "pause"
                theCurrService.TimerBinder().pause()
            }
            else if(!theCurrService.TimerBinder().paused){
                myStartButton.text = "unpause"
                theCurrService.TimerBinder().pause()
            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            //stop her!!
            myStartButton.text = "start"
            theCurrService.TimerBinder().stop()
        }
    }

    override fun onDestroy() {
        //undo the binding!!
        unbindService(connection)
        myBoundItem = false

        super.onDestroy()
    }
}