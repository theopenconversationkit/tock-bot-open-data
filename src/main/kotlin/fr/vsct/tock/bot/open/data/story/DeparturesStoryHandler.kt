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
import fr.vsct.tock.bot.open.data.client.sncf.model.Departure
import fr.vsct.tock.translator.I18nLabelKey
import fr.vsct.tock.translator.by
import java.lang.Math.min
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
            action.let { action ->
                //handle button click
                if (action is SendChoice && action.parameters[DeparturesStoryHandler.originParam] != null) {
                    origin = findPlace(action.parameters[DeparturesStoryHandler.originParam]!!)
                    departuresOffset = action.parameters[DeparturesStoryHandler.offsetParam]!!.toInt()
                }
                //do we have origin? If not, handle generic location intent
                else if (origin == null && intent == SecondaryIntent.indicate_location.intent && location != null) {
                    origin = returnsAndRemoveLocation()
                }
            }


            //now build the response
            origin.let { origin ->
                if (origin == null) {
                    end("De quelle gare souhaitez vous voir les départs?")
                } else {
                    send("Départs de la gare de {0} :", origin)
                    val departures = SncfOpenDataClient.departures(origin, ZonedDateTime.now(ZoneId.of("Europe/Paris")).toLocalDateTime())
                    if (departures.isEmpty()) {
                        end("Oups, aucun départ trouvé actuellement, désolé :(")
                    }
                    //messengers list does not support only 1 element
                    else if (departures.size == 1) {
                        withMessengerGeneric(departures.first().run {
                            messengerGenericElement(title(), description())
                        })
                        end()
                    } else {
                        withMessengerList(
                                departures
                                        .drop(departuresOffset)
                                        .take(4)
                                        .map {
                                            messengerListElement(it.title(), it.description())
                                        },
                                ListElementStyle.compact,
                                messengerPostback(
                                        "Départs suivants",
                                        SecondaryIntent.more_elements.intent,
                                        DeparturesStoryHandler.originParam to origin.name,
                                        DeparturesStoryHandler.offsetParam to min(departures.size, 4).toString()))
                        end()
                    }
                }
            }
        }
    }

    private fun Departure.title(): I18nLabelKey {
        return i18n("Direction {0}", displayInformations.direction)
    }

    private fun Departure.description(): I18nLabelKey {
        return i18n("Départ {0}", stopDateTime.departureDateTime by DeparturesStoryHandler.timeFormatter)
    }
}
