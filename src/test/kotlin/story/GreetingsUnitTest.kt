package ai.tock.bot.open.data.story

import ai.tock.bot.engine.BotBus
import ai.tock.bot.test.mock.mockMessenger
import ai.tock.bot.test.mock.test
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class GreetingsUnitTest {

    val bus: BotBus = mockk()

    @Test
    fun `greetings UT`() {
        mockMessenger(bus) {
            greetings.test(bus)

            verify {
                bus.send("Welcome to the Tock Open Data Bot! :)")
            }
        }

    }
}

