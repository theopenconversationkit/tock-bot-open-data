package ai.tock.bot.open.data.story

import ai.tock.bot.connector.ConnectorType
import ai.tock.bot.definition.IntentAware
import ai.tock.bot.engine.dialog.EntityValue
import ai.tock.bot.open.data.rule.OpenDataJUnitExtension
import ai.tock.bot.test.BotBusMock
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.Locale

abstract class BaseTest {

    @RegisterExtension
    @JvmField
    val ext = OpenDataJUnitExtension()

    fun send(
            text: String = "",
            intent: IntentAware = ext.testContext.defaultStoryDefinition(ext.botDefinition),
            vararg entities: EntityValue,
            connectorType: ConnectorType = ext.testContext.defaultConnectorType(),
            locale: Locale = ext.testContext.defaultLocale(),
            tests: BotBusMock.() -> Unit) {
        ext.send(text, intent, *entities, connectorType = connectorType, locale = locale, tests = tests)
    }

    fun newRequest(
            text: String,
            intent: IntentAware = ext.testContext.defaultStoryDefinition(ext.botDefinition),
            connectorType: ConnectorType = ext.testContext.defaultConnectorType(),
            locale: Locale = ext.testContext.defaultLocale(),
            tests: BotBusMock.() -> Unit
    ) {
        ext.newRequest(text, intent, connectorType = connectorType, locale = locale, tests = tests)
    }
}