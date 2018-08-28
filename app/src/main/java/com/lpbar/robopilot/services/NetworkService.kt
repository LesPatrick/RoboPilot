package com.lpbar.robopilot.services

import okhttp3.*
import java.io.IOException

interface NetworkServiceInterface {
    var address: String
    var port: String

    fun sendStopMotorsAction(callback: ((Response) -> Unit))
    fun sendStopExplorationAction(callback: ((Response) -> Unit))
    fun sendStartExplorationAction(callback: ((Response) -> Unit))
    fun sendGoToPointAction(x: Double, y: Double, callback: ((Response) -> Unit))
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
        get() = "http://$address:$port"

    override fun sendStopMotorsAction(callback: ((Response) -> Unit)) {
        val body: String = "{}"
        val request = Request.Builder().url(hostAddress + Endpoints.StopMotors.path)
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
                .build()

        executeRequest(request, callback)
    }

    override fun sendStartExplorationAction(callback: (Response) -> Unit) {
        val body: String = "{}"
        val request = Request.Builder().url(hostAddress + Endpoints.StartExploration.path)
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
                .build()

        executeRequest(request, callback)
    }

    override fun sendStopExplorationAction(callback: (Response) -> Unit) {
        val body: String = "{}"
        val request = Request.Builder().url(hostAddress + Endpoints.StopExploration.path)
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
                .build()

        executeRequest(request, callback)
    }

    override fun sendGoToPointAction(x: Double, y: Double, callback: (Response) -> Unit) {
        val body: String = """"
            {
                x: $x,
                y: $y
            }
            """"
        val request = Request.Builder()
                .url(hostAddress + Endpoints.ManualPose.path)
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
                .build()

        executeRequest(request, callback)
    }

    private fun executeRequest(request: Request, callback: ((Response) -> Unit)) {
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                System.out.println(call.toString())
                System.out.println(e.toString())
            }

            override fun onResponse(call: Call?, response: Response?) {
                response?.let { callback(it) }
            }
        })
    }
}