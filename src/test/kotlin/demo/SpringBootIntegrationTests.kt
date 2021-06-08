package demo

import demo.kx.uuid
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.getForObject
import org.springframework.boot.test.web.client.postForObject
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.random.Random

@Testcontainers
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SpringBootIntegrationTests(@Autowired val client: TestRestTemplate) {
    companion object {
        @Container
        val container = postgres("postgres:13-alpine") {
            withInitScript("sql/schema.ddl")
            withDatabaseName("db")
            withUsername("user")
            withPassword("password")
        }

        @JvmStatic
        @DynamicPropertySource
        fun datasourceConfig(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.password", container::getPassword)
            registry.add("spring.datasource.username", container::getUsername)
        }
    }

    @Test
    @Order(1)
    fun `test hello endpoint`() {
        println(">>>>>>>>>>>>> Asserting Hello endpoint! >>>>>>>>>>>>>>>>>>>>>>>>> ")
        val entity = client.getForEntity<String>("/hello")
        Assertions.assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(entity.body).contains("Hello")
    }

    //TODO: remove
    val id = "e3b7f959-f5c2-3d07-86e3-cd9df75e0b83"//"${Random.nextInt()}".uuid()

    @Test
    @Order(2)
    fun `testing if we can post and retrieve the data`() {
//        val id = "${Random.nextInt()}".uuid()
        val message = Message(id, "some message")
        client.postForObject<Message>("/", message)

        val entity = client.getForEntity<String>("/$id")
        Assertions.assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(entity.body).contains(message.id)
        Assertions.assertThat(entity.body).contains(message.text)

        val msg = client.getForObject<Message>("/$id")!!
        Assertions.assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(msg.id).isEqualTo(message.id)
        Assertions.assertThat(msg.text).contains(message.text)
    }

    @Test
    @Order(3)
    fun `message not found`() {
//        val id = "${Random.nextInt()}".uuid()
        val entity = client.getForEntity<String>("/$id")
        Assertions.assertThat(entity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}