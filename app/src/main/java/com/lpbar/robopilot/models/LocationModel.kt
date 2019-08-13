package com.lpbar.robopilot.models

import kotlinx.serialization.Serializable

@Serializable
class LocationModel (val name: String, val xPos: Double, val yPos: Double) {
}