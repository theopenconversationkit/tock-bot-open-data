package ai.tock.bot.open.data.story

import ai.tock.bot.engine.BotBus
import ai.tock.bot.test.mockMessenger
import ai.tock.bot.test.test
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GreetingsUnitTest {


    @BeforeEach
    fun beforeEach() {
        mockMessenger(bus)
    }

    val bus: BotBus = mockk()

    @Test
    fun `greetings UT`() {
        greetings.test(bus)

        verify {
            bus.send("Welcome to the Tock Open Data Bot! :)")
        }

    }
}

