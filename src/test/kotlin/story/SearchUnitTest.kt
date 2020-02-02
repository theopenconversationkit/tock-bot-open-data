package ai.tock.bot.open.data.story

import ai.tock.bot.engine.BotBus
import ai.tock.bot.test.mock.mockMessenger
import ai.tock.bot.test.mock.test
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class SearchUnitTest {

    val bus: BotBus = mockk()

    @Test
    fun `search story asks for destination WHEN there is no destination in context`() {
        mockMessenger(bus) {
            search.test(bus)

            verify {
                bus.end("For which destination?")
            }
        }
    }
}