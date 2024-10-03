package ai.kognize.streaming.testcontainers.port.out

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class FavouriteNumberPublisher(
    val kafkaTemplate: KafkaTemplate<String, FavouriteNumber>
) {

    fun publish(favouriteNumber: FavouriteNumber) {
        kafkaTemplate.sendDefault(favouriteNumber).get()
    }
}
