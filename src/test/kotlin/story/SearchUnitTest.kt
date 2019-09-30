package ai.tock.bot.open.data.story

import ai.tock.bot.engine.BotBus
import ai.tock.bot.test.mockMessenger
import ai.tock.bot.test.test
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchUnitTest {

    @BeforeEach
    fun beforeEach() {
        mockMessenger(bus)
    }

    val bus: BotBus = mockk()

    @Test
    fun `search story asks for destination WHEN there is no destination in context`() {
        search.test(bus)

        verify {
            bus.end("For which destination?")
        }
    }
}