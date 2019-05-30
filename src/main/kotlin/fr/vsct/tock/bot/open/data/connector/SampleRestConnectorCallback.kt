package fr.vsct.tock.bot.open.data.connector

import fr.vsct.tock.bot.connector.ConnectorCallbackBase
import fr.vsct.tock.bot.engine.action.Action
import fr.vsct.tock.bot.engine.action.SendSentence
import fr.vsct.tock.bot.engine.event.Event
import fr.vsct.tock.shared.jackson.mapper
import io.vertx.ext.web.RoutingContext
import mu.KotlinLogging
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList

internal class SampleRestConnectorCallback(
    applicationId: String,
    val locale: Locale,
    private val context: RoutingContext,
    private val actions: MutableList<Action> = CopyOnWriteArrayList()
) : ConnectorCallbackBase(applicationId, sampleRestConnectorType) {

    private val logger = KotlinLogging.logger {}


    fun addAction(event: Event) {
        if (event is Action) {
            actions.add(event)
        } else {
            logger.trace { "unsupported event: $event" }
        }
    }

    fun sendResponse() {
        val messages = actions
            .filterIsInstance<SendSentence>()
            .mapNotNull {
                if (it.stringText != null) {
                    SampleMessage(it.stringText!!)
                } else it.message(sampleRestConnectorType)?.let {
                    it as? SampleMessage
                }

            }
        context.response().end(mapper.writeValueAsString(SampleConnectorResponse(messages)))
    }
}