package fr.vsct.tock.bot.open.data.connector

import fr.vsct.tock.bot.connector.ConnectorCallbackBase
import fr.vsct.tock.bot.engine.action.Action
import fr.vsct.tock.bot.engine.action.SendSentence
import fr.vsct.tock.bot.engine.event.Event
import fr.vsct.tock.shared.jackson.mapper
import io.vertx.ext.web.RoutingContext
import mu.KotlinLogging
import java.util.concurrent.CopyOnWriteArrayList

internal class SampleRestConnectorCallback(
    applicationId: String,
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
        val texts = actions.filterIsInstance<SendSentence>().mapNotNull { it.stringText }
        context.response().end(mapper.writeValueAsString(SampleConnectorResponse(texts)))
    }
}