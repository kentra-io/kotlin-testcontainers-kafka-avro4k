# Testcontainers with Schema Registry and Avro4k
## What this repo is
I haven't found a good example of how to use Testcontainers with Schema Registry and Avro4k, so I'm publishing this one. 
With Kafka 3.3.0 (which corresponds to Confluent platform 7.3) zookeeper was removed, so a lot of examples are outdated.

I've built it based on https://github.com/confluentinc/cp-all-in-one, but I needed only broker and schema registry so I removed all the
remaining containers and the properties that weren't necessary. You can see what't left in the `docker-compose.yml` file.

Then I've set up a very similar configuration using testcontainers in the ITSpecification class. There are workarounds present described below.

## Used technologies
- Kotlin
- Avro4k
- Testcontainers
- Spock (unnecessarily complicates this repo and doesn't bring value to this example, I added it when I was building something else but then decided to publish this repo)

## Solution & workarounds
The challenge is getting the Schema Registry to connect to the kafka broker, and having the broker advertise listeners that are resolvable
both to the schema registry from within the container, as well as the app (with test profile) that's connecting from the host machine.

The implemented solution: hardcoded port bindings for the kafka broker (so that the advertised listeners are always on the same port of the host).
As I'm using linux, I couldn't use host.docker.internal, so I had to use the IP of the host machine. This could behave differently on different OSs, so 
this as a POC with a workaround, not a production-ready solution.

If you know a better way to solve this - please share it with me! or create a PR.
