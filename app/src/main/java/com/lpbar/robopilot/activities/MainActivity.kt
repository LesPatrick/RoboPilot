package com.lpbar.robopilot.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.lpbar.robopilot.R

import com.lpbar.robopilot.services.*

import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Response

class MainActivity : AppCompatActivity() {

    private var serverAddress: String = "192.168.0.213"
        set(value) { networkService.address = value }

    private var serverPort: String = "9090"
        set(value) { networkService.port = value }

    private var networkService: NetworkServiceInterface = NetworkService(serverAddress, serverPort)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stopMotorsButton.setOnClickListener { onStopMotors() }
        stopExplorationButton.setOnClickListener { onStopExploration() }
        startExplorationButton.setOnClickListener { onStartExploration() }
        sendManualPoseButton.setOnClickListener { onManualPose() }
        ipAddressEditText.addTextChangedListener( object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun afterTextChanged(p0: Editable?) {
                serverAddress = p0.toString()
            }
        })
        portEditText.addTextChangedListener( object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun afterTextChanged(p0: Editable?) {
                serverPort = p0.toString()
            }
        })
        joystick.setOnMoveListener({ angle, strength ->
            if (strength == 0) {
                networkService.sendManualMotorAction(0.0, 0.0)
                return@setOnMoveListener
            }

            var angleNorm = 0.0
            var strengthNorm: Double = strength / 100.0
            if (angle < 180) {
                angleNorm = angle - 90.0
            } else {
                angleNorm = 270.0 - angle
                strengthNorm *= -1
            }

            networkService.sendManualMotorAction(angleNorm / 90.0, strengthNorm)
        }, 100)
    }

    private fun onStopMotors() {
        networkService.sendStopMotorsAction(callback = {printResponse(it)}, error = {printMessage(it)})
    }

    private fun onStopExploration() {
        networkService.sendStopExplorationAction(callback = {printResponse(it)}, error = {printMessage(it)})
    }

    private fun onStartExploration() {
        networkService.sendStartExplorationAction(callback = {printResponse(it)}, error = {printMessage(it)})
    }

    private fun onManualPose() {
        networkService.sendGoToPointAction(
                xCoordEditText.text.toString().toDouble(),
                yCoordEditText.text.toString().toDouble(),
                callback = {printResponse(it)}, error = {printMessage(it)})
    }

    private fun printResponse(response: Response) {
        runOnUiThread {
            val consoleText = consoleOutputTextView.text
            val responseString = response.toString()
            consoleOutputTextView.text = "$responseString\n$consoleText"
        }
    }

    private fun printMessage(message: String) {
        runOnUiThread {
            val consoleText = consoleOutputTextView.text
            consoleOutputTextView.text = "$message\n$consoleText"
        }
    }
}
