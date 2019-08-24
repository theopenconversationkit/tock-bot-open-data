package fr.vsct.tock.bot.open.data.connector

import com.fasterxml.jackson.module.kotlin.readValue
import fr.vsct.tock.bot.connector.ConnectorBase
import fr.vsct.tock.bot.connector.ConnectorCallback
import fr.vsct.tock.bot.connector.ConnectorData
import fr.vsct.tock.bot.connector.ConnectorType
import fr.vsct.tock.bot.engine.BotRepository
import fr.vsct.tock.bot.engine.ConnectorController
import fr.vsct.tock.bot.engine.action.Action
import fr.vsct.tock.bot.engine.event.Event
import fr.vsct.tock.bot.engine.user.PlayerId
import fr.vsct.tock.bot.engine.user.UserPreferences
import fr.vsct.tock.shared.Executor
import fr.vsct.tock.shared.injector
import fr.vsct.tock.shared.jackson.mapper
import fr.vsct.tock.shared.provide
import io.vertx.ext.web.RoutingContext
import mu.KotlinLogging

internal const val SAMPLE_CONNECTOR_ID = "sample"
val sampleRestConnectorType = ConnectorType(SAMPLE_CONNECTOR_ID)

class SampleRestConnector internal constructor(
    val applicationId: String,
    val path: String
) : ConnectorBase(sampleRestConnectorType) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val executor: Executor get() = injector.provide()

    override fun register(controller: ConnectorController) {
        controller.registerServices(path) { router ->
            logger.info("deploy sample rest connector services for root path $path ")

            router.post(path).handler { context ->
                try {
                    executor.executeBlocking {
                        handleRequest(controller, context, context.bodyAsString)
                    }
                } catch (e: Throwable) {
                    context.fail(e)
                }
            }
        }
    }

    //internal for tests
    internal fun handleRequest(
        controller: ConnectorController,
        context: RoutingContext,
        body: String
    ) {
        val timerData = BotRepository.requestTimer.start("sample_webhook")
        try {
            logger.debug { "Sample request input : $body" }
            val request: SampleConnectorRequest = mapper.readValue(body)
            val callback = SampleRestConnectorCallback(applicationId, request.locale, context)
            controller.handle(request.toEvent(applicationId), ConnectorData(callback))
        } catch (t: Throwable) {
            BotRepository.requestTimer.throwable(t, timerData)
            context.fail(t)
        } finally {
            BotRepository.requestTimer.end(timerData)
        }
    }

    override fun send(event: Event, callback: ConnectorCallback, delayInMs: Long) {
        val c = callback as? SampleRestConnectorCallback
        c?.addAction(event)
        if (event is Action) {
            if (event.metadata.lastAnswer) {
                c?.sendResponse()
            }
        } else {
            logger.trace { "unsupported event: $event" }
        }
    }

    override fun loadProfile(callback: ConnectorCallback, userId: PlayerId): UserPreferences {
        callback as SampleRestConnectorCallback
        return UserPreferences().apply {
            locale = callback.locale
        }
    }

}