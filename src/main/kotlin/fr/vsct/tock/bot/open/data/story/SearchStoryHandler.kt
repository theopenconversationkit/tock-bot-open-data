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
import fr.vsct.tock.bot.connector.messenger.model.send.ListElementStyle
import fr.vsct.tock.bot.connector.messenger.withMessengerGeneric
import fr.vsct.tock.bot.connector.messenger.withMessengerList
import fr.vsct.tock.bot.definition.StoryHandlerBase
import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SecondaryIntent
import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 *
 */
object SearchStoryHandler : StoryHandlerBase() {

    /**
     * To format departure datetime.
     */
    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd MMMM").withLocale(Locale.FRENCH)
    val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale.FRENCH)

    override fun action(bus: BotBus) {
        with(bus) {
            //handle context
            if (destination == null) {
                //handle generic location
                if (intent == SecondaryIntent.indicate_location.intent && location != null) {
                    destination = returnsAndRemoveLocation()
                }
            }

            if (origin == null) {
                //handle generic location
                if (intent == SecondaryIntent.indicate_location.intent && location != null) {
                    origin = returnsAndRemoveLocation()
                }
            }

            //build the response
            destination.let { destination ->
                if (destination == null) {
                    end("Pour quelle destination?")
                } else {
                    origin.let { origin ->
                        if (origin == null) {
                            end("Pour quelle origine?")
                        } else {
                            departureDate.let { departureDate ->
                                if (departureDate == null) {
                                    end("Quand souhaitez-vous partir?")
                                } else {
                                    send("De ${origin.name} à ${destination.name}")
                                    send("Départ le ${dateFormat.format(departureDate)} vers ${timeFormat.format(departureDate)}", breath)
                                    val journeys = SncfOpenDataClient.journey(origin, destination, departureDate)
                                    if (journeys.isEmpty()) {
                                        end("Désolé, aucun itinéraire trouvé :(")
                                    } else {
                                        journeys.first().publicTransportSections().let { sections ->

                                            if (sections.size == 1) {
                                                sections.first().let { section ->
                                                    withMessengerGeneric(
                                                            messengerGenericElement(
                                                                    "${section.from!!.name} - ${section.to!!.name}",
                                                                    "Départ à ${timeFormat.format(section.stopDateTimes!!.first().departureDateTime)}, arrivée à ${timeFormat.format(section.stopDateTimes.last().arrivalDateTime)}")
                                                    )
                                                }
                                            } else {
                                                withMessengerList(
                                                        sections.map { section ->
                                                            messengerListElement(
                                                                    "${section.from!!.name} - ${section.to!!.name}",
                                                                    "Départ à ${timeFormat.format(section.stopDateTimes!!.first().departureDateTime)}, arrivée à ${timeFormat.format(section.stopDateTimes.last().arrivalDateTime)}")
                                                        },
                                                        ListElementStyle.compact
                                                )
                                            }
                                            end()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}