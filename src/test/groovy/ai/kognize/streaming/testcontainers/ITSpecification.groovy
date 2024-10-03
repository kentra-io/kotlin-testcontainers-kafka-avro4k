package ai.kognize.streaming.testcontainers


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest
@Testcontainers
@Import(App)
@ActiveProfiles("test")
class ITSpecification extends Specification {
    public static final String CONFLUENT_PLATFORM_VERSION_TAG = "7.7.1"
    public static final String DOCKER_HOST_IP_LINUX = "172.17.0.1" // workaround, described in the readme
    public static final List<String> STATIC_BROKER_PORT_BINDINGS = Arrays.asList("29092:29092", "9092:9092")  // workaround, described in the readme
    private static final Network NETWORK = Network.newNetwork()

    @Shared
    static final GenericContainer kafka = new GenericContainer(DockerImageName.parse("confluentinc/cp-kafka:$CONFLUENT_PLATFORM_VERSION_TAG"))
            .withNetwork(NETWORK)
            .withNetworkAliases("broker")
            .withExposedPorts(9092, 29092)
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
            .withEnv("KAFKA_CREATE_TOPICS", "favourite-numbers")
            .withEnv("KAFKA_NODE_ID", "1")
            .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT")
            .withEnv("KAFKA_ADVERTISED_LISTENERS", "PLAINTEXT://$DOCKER_HOST_IP_LINUX:29092")
            .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
            .withEnv("KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS", "0")
            .withEnv("KAFKA_PROCESS_ROLES", "broker,controller")
            .withEnv("KAFKA_CONTROLLER_QUORUM_VOTERS", "1@broker:29093")
            .withEnv("KAFKA_LISTENERS", "PLAINTEXT://broker:29092,CONTROLLER://broker:29093,PLAINTEXT_HOST://0.0.0.0:9092")
            .withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "PLAINTEXT")
            .withEnv("KAFKA_CONTROLLER_LISTENER_NAMES", "CONTROLLER")
            .withEnv("CLUSTER_ID", "Mk1Q7Q9vQ7e1Q7Q9vQ7e1Q")

    @Shared
    static GenericContainer schemaRegistry = new GenericContainer(DockerImageName.parse("confluentinc/cp-schema-registry:$CONFLUENT_PLATFORM_VERSION_TAG"))
            .withNetwork(NETWORK)
            .withNetworkAliases("schema-registry")
            .withExposedPorts(8081)
            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
            .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "broker:29092")
            .waitingFor(Wait.forHttp("/subjects").forStatusCode(200));

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        kafka.setPortBindings(STATIC_BROKER_PORT_BINDINGS)
        kafka.start()
        schemaRegistry.start()
        // this one remains dynamic because it's a best practice and there's no need to make it static
        registry.add("spring.kafka.properties.schema.registry.url", () -> "http://localhost:" + schemaRegistry.getMappedPort(8081))
    }
}
