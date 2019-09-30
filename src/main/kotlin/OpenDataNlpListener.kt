package ai.tock.bot.open.data

import ai.tock.bot.engine.dialog.Dialog
import ai.tock.bot.engine.dialog.EntityValue
import ai.tock.bot.engine.event.Event
import ai.tock.bot.engine.nlp.NlpListener
import ai.tock.bot.engine.user.UserTimeline
import ai.tock.bot.open.data.client.sncf.SncfOpenDataClient.findPlace
import ai.tock.bot.open.data.client.sncf.model.PlaceValue
import ai.tock.nlp.api.client.model.NlpResult
import ai.tock.nlp.entity.ValueResolverRepository

/**
 * Evaluate place entity values.
 */
object OpenDataNlpListener : NlpListener {

    init {
        //we add a new value type in order to manage open data api place
        //{"@type" : "place"} will look better in mongo than {"@type" : "ai.tock.bot.open.data.client.sncf.model.Place"}
        ValueResolverRepository.registerType(PlaceValue::class)
    }

    override fun evaluateEntities(
        userTimeline: UserTimeline,
        dialog: Dialog,
        event: Event,
        nlpResult: NlpResult
    ): List<EntityValue> {
        //evaluate localities
        return nlpResult
            .entities
            .filter { it.entity.entityType == locationEntity.entityType }
            .mapNotNull { entityValue ->
                findPlace(nlpResult.entityTextContent(entityValue))?.let { EntityValue(entityValue.entity, it) }
            }
    }
}
