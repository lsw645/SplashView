package com.lxw.splashview

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {
    private var mContentView: FrameLayout? = null;
    private var splashView: SplashView? = null
    private var handler: Handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentView = FrameLayout(this);
        mContentView!!.addView(ContentView(this))
        splashView = SplashView(this)
        mContentView!!.addView(splashView)
        //  setContentView(R.layout.activity_main)
        setContentView(mContentView)

        handler.postDelayed(Runnable {
            splashView!!.disapper()
        }, 1000)
    }


}
