package demo

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class IntegrationTests {

    @Container
    var container = postgres("postgres:13-alpine") {
//        withExposedPorts(6379)
        withInitScript("schema.ddl")
        withDatabaseName("db")
        withUsername("user")
        withPassword("password")
    }

    @Test
    fun `the container is up and running`() {
        assertTrue(container.isRunning)
    }

}