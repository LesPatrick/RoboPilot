package com.lpbar.robopilot.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.lpbar.robopilot.R
import com.lpbar.robopilot.fragments.SteeringFragment
import com.lpbar.robopilot.services.LocationProvider
import com.lpbar.robopilot.services.NetworkService
import com.lpbar.robopilot.services.NetworkServiceInterface
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Response

class MainActivity : AppCompatActivity(), SteeringFragment.OnFragmentInteractionListener, AdapterView.OnItemSelectedListener {
    companion object {
        private var serverAddress: String = "192.168.0.213"
            set(value) { networkService.address = value }

        private var serverPort: String = "9090"
            set(value) { networkService.port = value }
        var networkService: NetworkServiceInterface = NetworkService(serverAddress, serverPort)
    }

    private val locationProvider = LocationProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openJoyPad.setOnClickListener { onOpenJoypad() }
        stopMotorsButton.setOnClickListener { onStopMotors() }
        saveMapButton.setOnClickListener { onSaveMap() }
        loadMapButton.setOnClickListener { onLoadMap() }
        stopExplorationButton.setOnClickListener { onStopExploration() }
        startExplorationButton.setOnClickListener { onStartExploration() }
        sendManualPoseButton.setOnClickListener { onManualPose() }

        ipAddressEditText.setText(serverAddress, TextView.BufferType.EDITABLE)
        portEditText.setText(serverPort, TextView.BufferType.EDITABLE)

        locationProvider.models?.let {
            mapSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, it.map { x -> x.name })
            mapSpinner.onItemSelectedListener = this
            coordinatesSpinner.onItemSelectedListener = this
        }

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

    private fun onSaveMap() {
        networkService.sendSaveMapAction(callback = {printResponse(it)}, error = {printMessage(it)})
    }

    private fun onLoadMap() {
        networkService.sendLoadMapAction(callback = {printResponse(it)}, error = {printMessage(it)})
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

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0 == mapSpinner) {
            locationProvider.models?.let {
                coordinatesSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, it[p2].locations.map { it.name })
            }
        } else if (p0 == coordinatesSpinner) {
            locationProvider.models?.let {
                val location = it[mapSpinner.selectedItemPosition].locations[p2]
                xCoordEditText.setText("%.1f".format(location.xPos))
                yCoordEditText.setText("%.1f".format(location.yPos))
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}
