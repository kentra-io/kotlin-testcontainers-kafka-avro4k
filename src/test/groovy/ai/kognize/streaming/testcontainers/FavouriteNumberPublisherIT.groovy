package ai.kognize.streaming.testcontainers

import ai.kognize.streaming.testcontainers.port.out.FavouriteNumber
import ai.kognize.streaming.testcontainers.port.out.FavouriteNumberPublisher
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct
import org.spockframework.spring.SpringSpy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Import(FavouriteNumberListener)
class FavouriteNumberPublisherIT extends ITSpecification {
    @Autowired
    FavouriteNumberPublisher favouriteNumberPublisher

    @SpringSpy
    FavouriteNumberListener favouriteNumberListener

    def 'should publish a message and successfully receive it'() {
        given:
        var randomUUID = UUID.randomUUID().toString()
        var favouriteNumber = new FavouriteNumber(7, "bogdan", randomUUID)

        when:
        favouriteNumberPublisher.publish(favouriteNumber)
        Thread.sleep(1000L) // properly you'd use Awaitility but this is a simple example

        then:
        1 * favouriteNumberListener.receive(favouriteNumber)
    }
}

@Component
@Slf4j
class FavouriteNumberListener {
    @PostConstruct
    void init() {
        log.info("FavouriteNumberListener initialized")
    }

    @KafkaListener(topics = "favourite-numbers")
    void receive(FavouriteNumber favouriteNumber) {
        log.info("Received favourite number: {}", favouriteNumber)
    }
}
