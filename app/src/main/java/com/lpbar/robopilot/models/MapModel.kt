package com.lpbar.robopilot.models

import kotlinx.serialization.Serializable

@Serializable
class MapModel (val name: String, val locations: Array<LocationModel>) {
}