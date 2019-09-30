package ai.tock.bot.open.data.connector

import ai.tock.bot.connector.ConnectorMessage
import ai.tock.bot.definition.IntentAware
import ai.tock.bot.definition.Parameters
import ai.tock.bot.definition.StoryHandlerDefinition
import ai.tock.bot.definition.StoryStep
import ai.tock.bot.engine.BotBus
import ai.tock.bot.engine.I18nTranslator
import ai.tock.bot.engine.action.SendChoice

/**
 * Adds a Sample [ConnectorMessage] if the current connector is Sample.
 * You need to call [BotBus.send] or [BotBus.end] later to send this message.
 */
fun BotBus.withSample(messageProvider: () -> SampleMessage): BotBus {
    return withMessage(sampleRestConnectorType, messageProvider)
}

/**
 * Creates a text with buttons.
 */
fun I18nTranslator.sampleMessage(title: CharSequence, vararg buttons: SampleButton): SampleMessage =
    SampleMessage(
        translate(title).toString(), buttons.toList()
    )

/**
 * Creates a sample button.
 */
fun BotBus.sampleButton(
    title: CharSequence,
    targetIntent: IntentAware? = null,
    step: StoryStep<out StoryHandlerDefinition>? = null,
    parameters: Parameters = Parameters()
): SampleButton =
    SampleButton(
        translate(title).toString(),
        targetIntent?.let { i -> SendChoice.encodeChoiceId(this, i, step, parameters.toMap()) }
    )