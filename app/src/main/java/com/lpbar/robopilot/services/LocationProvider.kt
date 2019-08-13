package com.lpbar.robopilot.services

import com.lpbar.robopilot.models.MapModel
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.json.*

class LocationProvider {
    val jsonParser: Json = Json(JsonConfiguration.Stable)
    var models: List<MapModel>? = null

    init {
        loadFromFile()
    }

    private fun loadFromFile() {
        try {
            val contents = """
                [
  {
    "name": "first floor",
    "locations": [
      {
        "name": "important room",
        "xPos": 1.5,
        "yPos": 2.0
      },
      {
        "name": "important room 2",
        "xPos": 0.0,
        "yPos": 2.0
      }
    ]
  },
  {
    "name": "second floor",
    "locations": [
      {
        "name": "important room",
        "xPos": 1.5,
        "yPos": 2.0
      },
      {
        "name": "important room 2",
        "xPos": 0.0,
        "yPos": 2.0
      }
    ]
  }
]

            """.trimIndent()
            models = jsonParser.parse(ArrayListSerializer(MapModel.serializer()), contents)
        } catch (e: Exception) {
            print(e.message)
        }

    }
}