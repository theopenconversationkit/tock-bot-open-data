package ai.tock.bot.open.data.story

import ai.tock.bot.engine.BotBus
import ai.tock.bot.engine.entityValue
import ai.tock.bot.test.mock.mockMessenger
import ai.tock.bot.test.mock.test
import ai.tock.nlp.api.client.model.Entity
import ai.tock.nlp.api.client.model.EntityType
import ai.tock.nlp.entity.Value
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class SearchUnitTest {

    val bus: BotBus = mockk()

    @Test
    fun `search story asks for destination WHEN there is no destination in context`() {
        every {
            bus.entityValue<Value>(
                any<Entity>()
            )
        } returns null
        mockMessenger(bus) {
            search.test(bus)

            verify {
                bus.end("For which destination?")
            }
        }
    }
}