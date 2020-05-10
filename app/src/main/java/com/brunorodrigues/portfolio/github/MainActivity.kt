package com.brunorodrigues.portfolio.github

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val timer = object : CountDownTimer(3000, 1000) {
            override fun onFinish() {
                startActivity(RepositoryActivity.newIntent(this@MainActivity))
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {  }
        }
        timer.start()
    }
}
