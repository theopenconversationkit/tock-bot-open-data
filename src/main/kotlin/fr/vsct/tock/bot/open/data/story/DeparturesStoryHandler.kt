/*
 *  This file is part of the tock-bot-open-data distribution.
 *  (https://github.com/voyages-sncf-technologies/tock-bot-open-data)
 *  Copyright (c) 2017 VSCT.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package fr.vsct.tock.bot.open.data.story

import fr.vsct.tock.bot.connector.messenger.messengerGenericElement
import fr.vsct.tock.bot.connector.messenger.messengerListElement
import fr.vsct.tock.bot.connector.messenger.messengerPostback
import fr.vsct.tock.bot.connector.messenger.model.send.ListElementStyle
import fr.vsct.tock.bot.connector.messenger.withMessengerGeneric
import fr.vsct.tock.bot.connector.messenger.withMessengerList
import fr.vsct.tock.bot.definition.StoryHandlerBase
import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.engine.action.SendChoice
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SecondaryIntent
import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 *
 */
object DeparturesStoryHandler : StoryHandlerBase() {

    /**
     * To format departure time.
     */
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale.FRENCH)

    /**
     * "More elements" choice parameters
     */
    val originParam = "origin"
    val offsetParam = "offset"

    override fun action(bus: BotBus) {

        with(bus) {
            var departuresOffset = 0

            //check entities
            bus.action.let { action ->
                //handle button click
                if (action is SendChoice) {
                    origin = loadOrigin(action.parameters[DeparturesStoryHandler.originParam]!!)
                    departuresOffset = action.parameters[DeparturesStoryHandler.offsetParam]!!.toInt()
                }
                //do we have origin? If not, handle generic location intent
                else if (originPlace == null && intent == SecondaryIntent.indicate_location.intent && locationPlace != null) {
                    origin = returnAndRemoveLocation()
                }
            }


            //now build the response
            originPlace.let { originPlace ->
                if (originPlace == null) {
                    end("De quelle gare souhaitez vous voir les départs?")
                } else {
                    send("Départs de la gare de ${originPlace.name} :")
                    val departures = SncfOpenDataClient.departures(originPlace, ZonedDateTime.now(ZoneId.of("Europe/Paris")).toLocalDateTime())
                    if (departures.isEmpty()) {
                        end("Oups, aucun départ trouvé actuellement, désolé :(")
                    }
                    //messengers list does not support only 1 element
                    else if (departures.size == 1) {
                        withMessengerGeneric(departures.first().run { messengerGenericElement("Direction ${displayInformations.direction}", "Départ ${stopDateTime.departureDateTime.format(DeparturesStoryHandler.timeFormatter)}") })
                        end()
                    } else {
                        withMessengerList(
                                departures
                                        .drop(departuresOffset)
                                        .take(4)
                                        .map { messengerListElement("Direction ${it.displayInformations.direction}", "Départ ${it.stopDateTime.departureDateTime.format(DeparturesStoryHandler.timeFormatter)}") },
                                ListElementStyle.compact,
                                messengerPostback(
                                        "Départs suivants",
                                        SecondaryIntent.more_elements.intent,
                                        DeparturesStoryHandler.originParam to origin!!.content!!,
                                        DeparturesStoryHandler.offsetParam to Math.min(departures.size, 4).toString()))
                        end()
                    }
                }
            }
        }
    }
}
