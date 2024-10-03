package ai.kognize.streaming.testcontainers.port.out

import kotlinx.serialization.Serializable

@Serializable
data class FavouriteNumber(
    val number: Int,
    val author: String,
    val uuid: String
)
