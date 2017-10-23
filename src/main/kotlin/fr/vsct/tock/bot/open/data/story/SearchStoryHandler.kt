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

import fr.vsct.tock.bot.connector.ga.carouselItem
import fr.vsct.tock.bot.connector.ga.gaFlexibleMessageForCarousel
import fr.vsct.tock.bot.connector.ga.gaImage
import fr.vsct.tock.bot.connector.ga.withGoogleAssistant
import fr.vsct.tock.bot.connector.messenger.flexibleListTemplate
import fr.vsct.tock.bot.connector.messenger.listElement
import fr.vsct.tock.bot.connector.messenger.model.send.ListElementStyle.compact
import fr.vsct.tock.bot.connector.messenger.withMessenger
import fr.vsct.tock.bot.definition.ParameterKey
import fr.vsct.tock.bot.definition.StoryHandlerBase
import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.open.data.OpenDataConfiguration.trainImage
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SharedIntent.indicate_location
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.search
import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient
import fr.vsct.tock.bot.open.data.story.MessageFormat.dateFormat
import fr.vsct.tock.bot.open.data.story.MessageFormat.timeFormat
import fr.vsct.tock.bot.open.data.story.SearchStoryHandler.SearchParameter.proposal
import fr.vsct.tock.translator.by

/**
 *
 */
object SearchStoryHandler : StoryHandlerBase() {

    private enum class SearchParameter : ParameterKey {
        proposal
    }

    override fun action(bus: BotBus) {
        with(bus) {
            //handle generic location intent
            if (isIntent(indicate_location) && location != null) {
                if (destination == null || origin != null) {
                    destination = returnsAndRemoveLocation()
                } else {
                    origin = returnsAndRemoveLocation()
                }
            }

            val d = destination
            val o = origin
            val date = departureDate

            //build the response
            if (d == null) {
                end("Pour quelle destination?")
            } else if (o == null) {
                end("Pour quelle origine?")
            } else if (date == null) {
                end("Quand souhaitez-vous partir?")
            } else {
                send("De {0} à {1}", o, d)
                send("Départ le {0} vers {1}", date by dateFormat, date by timeFormat)
                val journeys = SncfOpenDataClient.journey(o, d, date)
                if (journeys.isEmpty()) {
                    end("Désolé, aucun itinéraire trouvé :(")
                } else {
                    send("Voici la première proposition :")
                    journeys.first().publicTransportSections().let { sections ->
                        withMessenger {
                            flexibleListTemplate(
                                    sections.map { section ->
                                        with(section) {
                                            listElement(
                                                    i18n("{0} - {1}", from, to),
                                                    i18n(
                                                            "Départ à {0}, arrivée à {1}",
                                                            stopDateTimes!!.first().departureDateTime by timeFormat,
                                                            stopDateTimes.last().arrivalDateTime by timeFormat
                                                    ),
                                                    trainImage
                                            )
                                        }
                                    },
                                    compact
                            )
                        }
                        withGoogleAssistant {
                            gaFlexibleMessageForCarousel(
                                    sections.mapIndexed { i, section ->
                                        with(section) {
                                            carouselItem(
                                                    search,
                                                    i18n("{0} - {1}", from, to),
                                                    i18n(
                                                            "Départ à {0}, arrivée à {1}",
                                                            stopDateTimes!!.first().departureDateTime by timeFormat,
                                                            stopDateTimes.last().arrivalDateTime by timeFormat
                                                    ),
                                                    gaImage(trainImage, "train"),
                                                    proposal[i]
                                            )
                                        }
                                    }
                            )
                        }

                        end()
                    }
                }
            }
        }
    }
}