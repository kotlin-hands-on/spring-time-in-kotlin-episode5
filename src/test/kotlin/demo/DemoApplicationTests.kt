package demo

import demo.kx.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.getForObject
import org.springframework.boot.test.web.client.postForObject
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.random.Random
//import org.junit.jupiter.api.MethodOrderer
//import org.junit.jupiter.api.Order
//import org.junit.jupiter.api.TestMethodOrder

@Testcontainers
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
 )
//@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DemoApplicationTests(
    @Autowired val client: TestRestTemplate,
    @Autowired val jdbcTemplate: JdbcTemplate
    ) {

    @AfterEach
    fun cleanup(){
        jdbcTemplate.execute("truncate table messages")
    }

    companion object {
        @Container
        val container = postgres("postgres:13-alpine") {
            withInitScript("sql/schema.sql")
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
//    @Order(1)
    fun `test hello endpoint`() {
        val entity = client.getForEntity<String>("/hello")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("Hello")
    }

    @Test
//    @Order(2)
    fun `testing if we can post and retrieve the data`() {
        val id = "${Random.nextInt()}".uuid()
        val message = Message(id, "some message")
        client.postForObject<Message>("/", message)

        val entity = client.getForEntity<String>("/$id")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains(message.id)
        assertThat(entity.body).contains(message.text)

        val msg = client.getForObject<Message>("/$id")!!
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(msg.id).isEqualTo(message.id)
        assertThat(msg.text).contains(message.text)
    }

    @Test
//    @Order(3)
    fun `message not found`() {
        val id = "${Random.nextInt()}".uuid()
        val entity = client.getForEntity<String>("/$id")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

}
