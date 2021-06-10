# Spring Time in Kotlin, Episode 5: Integration testing with TestContainers

This is a demo project used in [the 5th episode of Spring Time in Kotlin series](https://www.youtube.com/watch?v=0jWo3o7r-W4) at the official [Kotlin YouTube channel](https://www.youtube.com/kotlin)

In this episode:
- We start using PostgreSQL database instead of H2
- Use TestContainers for testing
- Lean more about Kotlin and Java interoperability

##Running the application

First, start PostgreSQL database by running this command (assuming you have Docker installed on the machine):

    docker run -e POSTGRES_PASSWORD=password -e POSTGRES_USER=sa -p 5432:5432 -d postgres

Now you can launch Spring Boot application:

    ./gradlew bootRun