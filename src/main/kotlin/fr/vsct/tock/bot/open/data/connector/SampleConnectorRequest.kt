package fr.vsct.tock.bot.open.data.connector

import fr.vsct.tock.bot.engine.action.SendSentence
import fr.vsct.tock.bot.engine.event.Event
import fr.vsct.tock.bot.engine.user.PlayerId
import fr.vsct.tock.bot.engine.user.PlayerType.bot

data class SampleConnectorRequest(val query: String, val userId: String) {

    fun toEvent(applicationId: String): Event =
        SendSentence(
            PlayerId(userId),
            applicationId,
            PlayerId(applicationId, bot),
            query
        )

}