package com.lpbar.robopilot.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lpbar.robopilot.R

import com.lpbar.robopilot.services.*

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val defaultAddress: String = "0.0.0.0"
    val defaultPort: String = "9090"

    val networkService: NetworkServiceInterface = NetworkService(defaultAddress, defaultPort)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stopMotorsButton.setOnClickListener { this.onStopMotors(it) }
    }

    private fun onStopMotors(button: View) {
        networkService.sendStopMotorsAction {
            print(it)
        }
    }

}
