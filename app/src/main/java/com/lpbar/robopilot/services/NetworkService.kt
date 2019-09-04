package com.lpbar.robopilot.services

import okhttp3.*
import java.io.IOException
import com.lpbar.robopilot.R.string.send
import org.jetbrains.anko.doAsync
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.logging.Handler

interface NetworkServiceInterface {
    var address: String
    var port: String

    fun sendStopMotorsAction(callback: ((Response) -> Unit), error: ((String) -> Unit))
    fun sendSaveMapAction(mapName: String, callback: ((Response) -> Unit), error: ((String) -> Unit))
    fun sendLoadMapAction(mapName: String, callback: ((Response) -> Unit), error: ((String) -> Unit))
    fun sendStopExplorationAction(callback: ((Response) -> Unit), error: ((String) -> Unit))
    fun sendStartExplorationAction(callback: ((Response) -> Unit), error: ((String) -> Unit))
    fun sendGoToPointAction(x: Double, y: Double, callback: ((Response) -> Unit), error: ((String) -> Unit))
    fun sendManualMotorAction(angle: Double, strength: Double)
    fun sendSaveLocation(locationName: String, callback: ((Response) -> Unit), error: ((String) -> Unit))
}

enum class Endpoints(val path: String) {
    StopMotors("/stop_motors"),
    SaveMap("/save_map"),
    LoadMap("/load_map"),
    ManualPose("/manual_pose"),
    StartExploration("/start_exploration"),
    StopExploration("/stop_exploration"),
    OverrideMotors("/override_motors"),
    SaveLocation("/save_location")
}

class NetworkService(
        override var address: String,
        override var port: String
) : NetworkServiceInterface {

    private val client: OkHttpClient = OkHttpClient()

    private var udpSocket: DatagramSocket = DatagramSocket()

    private val hostAddress: String
        get() = "http://$address:$port"

    override fun sendStopMotorsAction(callback: ((Response) -> Unit), error: ((String) -> Unit)) {
        val body: String = "{}"

        val request = Request.Builder().url(hostAddress + Endpoints.StopMotors.path)
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
                .build()

        executeRequest(request, callback, error)
    }

    override fun sendSaveMapAction(mapName: String, callback: (Response) -> Unit, error: (String) -> Unit) {
        val body = FormBody.Builder()
                .add("name", mapName)
                .build()

        val request = Request.Builder().url(hostAddress + Endpoints.SaveMap.path)
                .put(body)
                .build()

        executeRequest(request, callback, error)
    }

    override fun sendLoadMapAction(mapName: String, callback: (Response) -> Unit, error: (String) -> Unit) {
        val body = FormBody.Builder()
                .add("name", mapName)
                .build()

        val request = Request.Builder().url(hostAddress + Endpoints.LoadMap.path)
                .put(body)
                .build()

        executeRequest(request, callback, error)
    }

    override fun sendStartExplorationAction(callback: (Response) -> Unit, error: ((String) -> Unit)) {
        val body: String = "{}"

        val request = Request.Builder().url(hostAddress + Endpoints.StartExploration.path)
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
                .build()

        executeRequest(request, callback, error)
    }

    override fun sendStopExplorationAction(callback: (Response) -> Unit, error: ((String) -> Unit)) {
        val body: String = "{}"

        val request = Request.Builder().url(hostAddress + Endpoints.StopExploration.path)
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
                .build()

        executeRequest(request, callback, error)
    }

    override fun sendGoToPointAction(x: Double, y: Double, callback: (Response) -> Unit, error: ((String) -> Unit)) {
        val body = FormBody.Builder()
                .add("x", x.toString())
                .add("y", y.toString())
                .build()

        val request = Request.Builder()
                .url(hostAddress + Endpoints.ManualPose.path)
                .put(body)
                .build()

        executeRequest(request, callback, error)
    }

    override fun sendManualMotorAction(angle: Double, strength: Double) {
        val angleStr = angle.toString()
        val strengthStr = strength.toString()

        doAsync {
            try {
                val buf = "{\"angle\":$angleStr, \"strength\":$strengthStr}".toByteArray()
                val packet = DatagramPacket(buf, buf.size, InetAddress.getByName(address), 11123)
                udpSocket.send(packet)
            } catch (e: Exception) {
                System.out.println(e.printStackTrace())
            }
        }
    }

    override fun sendSaveLocation(locationName: String, callback: (Response) -> Unit, error: (String) -> Unit) {
        val body = FormBody.Builder()
                .add("name", locationName)
                .build()

        val request = Request.Builder()
                .url(hostAddress + Endpoints.SaveLocation.path)
                .put(body)
                .build()

        executeRequest(request, callback, error)
    }

    private fun executeRequest(request: Request, callback: ((Response) -> Unit), error: ((String) -> Unit)) {
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                System.out.println(call.toString())
                System.out.println(e.toString())

                error(e.toString())
            }

            override fun onResponse(call: Call?, response: Response?) {
                response?.let { callback(it) }
            }
        })
    }
}