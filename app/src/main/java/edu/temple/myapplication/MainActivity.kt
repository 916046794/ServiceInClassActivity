package edu.temple.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var myBoundItem: Boolean = false
    private var theCurrService = TimerService()
    private lateinit var myStartButton: MenuItem
    private lateinit var myStopButton: MenuItem

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            myBoundItem = true

            var myHandler = object: Handler(Looper.getMainLooper()){

                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    findViewById<TextView>(R.id.textView).text = msg.what.toString()
                }
            }
            theCurrService.TimerBinder().setHandler(myHandler)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            myBoundItem = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //replace buttons w/menu item and action bar
        //and change icon based on the logic instead of text
        //by default, the action bar is not included so youll need to change the theme
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //running and paused, running and not paused, or just not running

        //implement start and stop functionality for the service, bind the service
        //also, stop is always stop, but for start, it starts, pauses, and unpauses
        //change its text displaying such. Like stop should make it start,
        //once started it becomes pause, when pause become unpause and when unpause
        //becomes paused
        //make handler display the countdown in activity

        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        //undo the binding!!
        unbindService(connection)
        myBoundItem = false

        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item == myStartButton) {
            if(!theCurrService.TimerBinder().isRunning){
                    //if it's not running, make it a run, and change the name
                    item.setIcon(android.R.drawable.ic_media_pause)
                    theCurrService.TimerBinder().start(10)
            }
            else if(theCurrService.TimerBinder().paused){
                    //make it not paused
                    item.setIcon(android.R.drawable.ic_media_pause)
                    theCurrService.TimerBinder().pause()
            }
             else if(!theCurrService.TimerBinder().paused){
                    item.setIcon(android.R.drawable.ic_media_play)
                    theCurrService.TimerBinder().pause()
             }
            return true
        }
        if(item == myStopButton){
            myStartButton.setIcon(android.R.drawable.ic_media_play)
            theCurrService.TimerBinder().stop()

            return true
        }

        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.button_menu, menu)

        myStartButton = menu.findItem(R.id.startButton)
        myStopButton = menu.findItem(R.id.stopButton)

        return true
    }
}