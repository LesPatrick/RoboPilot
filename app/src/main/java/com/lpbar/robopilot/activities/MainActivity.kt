package com.lpbar.robopilot.activities

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.lpbar.robopilot.R
import com.lpbar.robopilot.fragments.SteeringFragment

import com.lpbar.robopilot.services.*

import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Response

class MainActivity : AppCompatActivity(), SteeringFragment.OnFragmentInteractionListener {
    companion object {
        private var serverAddress: String = "192.168.0.213"
            set(value) { networkService.address = value }

        private var serverPort: String = "9090"
            set(value) { networkService.port = value }
        var networkService: NetworkServiceInterface = NetworkService(serverAddress, serverPort)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openJoyPad.setOnClickListener { onOpenJoypad() }
        stopMotorsButton.setOnClickListener { onStopMotors() }
        stopExplorationButton.setOnClickListener { onStopExploration() }
        startExplorationButton.setOnClickListener { onStartExploration() }
        sendManualPoseButton.setOnClickListener { onManualPose() }

        ipAddressEditText.setText(serverAddress, TextView.BufferType.EDITABLE)
        portEditText.setText(serverPort, TextView.BufferType.EDITABLE)

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

    private fun onOpenJoypad() {
        val joypadFragment = SteeringFragment.newInstance()
        joypadFragment.show(supportFragmentManager, "joypadFgm")
    }

    private fun printResponse(response: Response) {
        runOnUiThread {
            val consoleText = consoleOutputTextView.text
            val responseString = response.toString()
            consoleOutputTextView.text = "$responseString\n$consoleText"
        }
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    private fun printMessage(message: String) {
        runOnUiThread {
            val consoleText = consoleOutputTextView.text
            consoleOutputTextView.text = "$message\n$consoleText"
        }
    }
}
