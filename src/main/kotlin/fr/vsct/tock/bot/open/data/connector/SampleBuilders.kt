package fr.vsct.tock.bot.open.data.connector

import fr.vsct.tock.bot.connector.ConnectorMessage
import fr.vsct.tock.bot.definition.IntentAware
import fr.vsct.tock.bot.definition.Parameters
import fr.vsct.tock.bot.definition.StoryHandlerDefinition
import fr.vsct.tock.bot.definition.StoryStep
import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.engine.I18nTranslator
import fr.vsct.tock.bot.engine.action.SendChoice

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