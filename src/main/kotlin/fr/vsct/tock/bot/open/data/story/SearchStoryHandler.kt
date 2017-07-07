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
import fr.vsct.tock.bot.connector.messenger.model.send.ListElementStyle.compact
import fr.vsct.tock.bot.connector.messenger.withMessengerGeneric
import fr.vsct.tock.bot.connector.messenger.withMessengerList
import fr.vsct.tock.bot.definition.StoryHandlerBase
import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SecondaryIntent.indicate_location
import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient
import fr.vsct.tock.bot.open.data.client.sncf.model.Section
import fr.vsct.tock.bot.open.data.story.MessageFormat.dateFormat
import fr.vsct.tock.bot.open.data.story.MessageFormat.timeFormat
import fr.vsct.tock.translator.I18nLabelKey
import fr.vsct.tock.translator.by

/**
 *
 */
object SearchStoryHandler : StoryHandlerBase() {

    override fun action(bus: BotBus) {
        with(bus) {
            //handle generic location intent
            if (intent == indicate_location.intent && location != null) {
                if (destination == null) {
                    destination = returnsAndRemoveLocation()
                } else if (origin == null) {
                    origin = returnsAndRemoveLocation()
                } else {
                    destination = returnsAndRemoveLocation()
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
                                    send("De {0} à {1}", origin, destination)
                                    send("Départ le {0} vers {1}", breath, departureDate by dateFormat, departureDate by timeFormat)
                                    val journeys = SncfOpenDataClient.journey(origin, destination, departureDate)
                                    if (journeys.isEmpty()) {
                                        end("Désolé, aucun itinéraire trouvé :(")
                                    } else {
                                        journeys.first().publicTransportSections().let { sections ->

                                            if (sections.size == 1) {
                                                sections.first().let { section ->
                                                    withMessengerGeneric(
                                                            messengerGenericElement(
                                                                    section.title(),
                                                                    section.description()
                                                            )
                                                    )
                                                }
                                            } else {
                                                withMessengerList(
                                                        sections.map { section ->
                                                            messengerListElement(
                                                                    section.title(),
                                                                    section.description()
                                                            )
                                                        },
                                                        compact
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

    private fun Section.title(): I18nLabelKey {
        return i18n("{0} - {1}", from, to)
    }

    private fun Section.description(): I18nLabelKey {
        return i18n(
                "Départ à {0}, arrivée à {1}",
                stopDateTimes!!.first().departureDateTime by timeFormat,
                stopDateTimes.last().arrivalDateTime by timeFormat
        )
    }
}