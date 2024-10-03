package ai.kognize.streaming.testcontainers.config

import ai.kognize.streaming.testcontainers.port.out.FavouriteNumber
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, FavouriteNumber>,
                      @Value("\${kafka.topics.favourite-numbers}") favouriteNumberTopic: String): KafkaTemplate<String, FavouriteNumber> {
        KafkaTemplate(producerFactory).let {
            it.defaultTopic = favouriteNumberTopic
            return it
        }
    }
}
