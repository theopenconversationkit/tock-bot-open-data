package fr.vsct.tock.bot.open.data

import fr.vsct.tock.bot.engine.dialog.ContextValue
import fr.vsct.tock.bot.engine.dialog.Dialog
import fr.vsct.tock.bot.engine.nlp.NlpListener
import fr.vsct.tock.bot.engine.user.UserTimeline
import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient.findPlaceValue
import fr.vsct.tock.nlp.api.client.model.NlpResult

/**
 * Evaluate place entity values.
 */
object OpenDataNlpListener : NlpListener {

    override fun evaluateEntities(userTimeline: UserTimeline, dialog: Dialog, nlpResult: NlpResult): List<ContextValue> {
        //evaluate localities
        return nlpResult
                .entities
                .filter { it.entity.entityType == locationEntity.entityType }
                .mapNotNull { entityValue ->
                    findPlaceValue(nlpResult.entityTextContent(entityValue))?.let { ContextValue(entityValue.entity, it) }
                }
    }
}
