package demo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DemoTests {

    @Test
    fun `test some math`() {
        assertThat(1 + 2).isEqualTo(3)
    }

}