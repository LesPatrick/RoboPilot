package com.lpbar.robopilot.services

import okhttp3.*
import java.io.IOException

interface NetworkServiceInterface {
    var address: String
    var port: String

    fun sendStopMotorsAction(callback: ((Response) -> Unit))
}

enum class Endpoints(val path: String) {
    StopMotors("/stop_motors"),
    ManualPose("/manual_pose"),
    StartExploration("/start_exploration"),
    StopExploration("/stop_exploration")
}

class NetworkService(
        override var address: String,
        override var port: String
) : NetworkServiceInterface {

    private val client: OkHttpClient = OkHttpClient()

    private val hostAddress: String
        get() = "$address:$port"

    override fun sendStopMotorsAction(callback: ((Response) -> Unit)) {
        val request = Request.Builder().url(hostAddress + Endpoints.StopMotors.path).build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call?, response: Response?) {
                response?.let { callback(it) }
            }
        })
    }
}