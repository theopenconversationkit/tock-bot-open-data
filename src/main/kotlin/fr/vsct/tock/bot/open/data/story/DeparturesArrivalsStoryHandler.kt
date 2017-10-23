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


import fr.vsct.tock.bot.connector.messenger.genericElement
import fr.vsct.tock.bot.connector.messenger.genericTemplate
import fr.vsct.tock.bot.connector.messenger.quickReply
import fr.vsct.tock.bot.connector.messenger.withMessenger
import fr.vsct.tock.bot.definition.ParameterKey
import fr.vsct.tock.bot.definition.StoryHandlerBase
import fr.vsct.tock.bot.definition.StoryStep
import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.open.data.OpenDataConfiguration.trainImage
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SharedIntent.arrivals
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SharedIntent.more_elements
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.departures
import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler.ChoiceParameter.nextResultDate
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler.ChoiceParameter.nextResultOrigin
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler.ContextKey.startDate
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler.DeparturesArrivalsStep.arrivalsStep
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler.DeparturesArrivalsStep.departuresStep
import fr.vsct.tock.bot.open.data.story.MessageFormat.timeFormat
import fr.vsct.tock.shared.defaultZoneId
import fr.vsct.tock.translator.by
import java.time.LocalDateTime
import java.time.ZonedDateTime

/**
 *
 */
object DeparturesArrivalsStoryHandler : StoryHandlerBase() {

    enum class DeparturesArrivalsStep : StoryStep {
        departuresStep, arrivalsStep
    }

    private enum class ChoiceParameter : ParameterKey {
        nextResultDate, nextResultOrigin
    }

    private enum class ContextKey : ParameterKey {
        startDate
    }

    var BotBus.currentDate: LocalDateTime
        get() = contextValue(startDate) ?: ZonedDateTime.now(defaultZoneId).toLocalDateTime()
        set(value) = changeContextValue(startDate, value)

    override fun action(bus: BotBus) {
        with(bus) {
            val maxProposals = 10

            //check location entity
            if (location != null) {
                origin = returnsAndRemoveLocation()
            }

            //handle more_elements intent
            //if more_elements comes from a choice, we know the next date as it is passed as parameter
            //else we will skip the maxProposals first elements of the api request
            val skipMaxProposals = if (isIntent(more_elements)) {

                choice(nextResultOrigin)
                        ?.run {
                            origin = findPlace(this)
                        }
                choice(nextResultDate)
                        ?.run {
                            currentDate = LocalDateTime.parse(this)
                            false
                        } ?: true
            } else {
                false
            }

            //manage step change
            if (isIntent(arrivals)) {
                step = arrivalsStep
            } else if (isIntent(departures)) {
                step = departuresStep
            }

            val arrival = step == arrivalsStep

            //now builds the response
            origin.also { origin ->
                if (origin == null) {
                    end("De quelle gare souhaitez vous voir les ${if (arrival) "arrivées" else "départs"}?")
                } else {
                    send("${if (arrival) "Arrivées à" else "Départs de"} la gare de {0} :", origin)
                    var stops =
                            if (arrival) SncfOpenDataClient.arrivals(origin, currentDate)
                            else SncfOpenDataClient.departures(origin, currentDate)
                    val nextIndex = Math.min(stops.size - 1, maxProposals)
                    var nextDate =
                            if (stops.isEmpty()) currentDate
                            else stops[nextIndex].stopDateTime.run {
                                if (arrival) arrivalDateTime else departureDateTime
                            }
                    //need to skip maxProposals here
                    if (skipMaxProposals && stops.isNotEmpty()) {
                        stops = stops.subList(nextIndex, stops.size)
                        if (stops.isNotEmpty()) {
                            currentDate = nextDate
                            nextDate = stops[Math.min(stops.size, maxProposals)].stopDateTime.run {
                                if (arrival) arrivalDateTime else departureDateTime
                            }
                        }
                    }

                    if (stops.isEmpty()) {
                        end("Oups, ${if (arrival) "aucune arrivée trouvée" else "aucun départ trouvé"} actuellement, désolé :(")
                    } else {
                        stops
                                .filter {
                                    if (arrival) it.stopDateTime.arrivalDateTime >= currentDate
                                    else it.stopDateTime.departureDateTime >= currentDate
                                }
                                .also { filteredStops ->
                                    if (filteredStops.isEmpty()) {
                                        end("Oups, plus de résultats, désolé :(")
                                    } else {
                                        filteredStops.take(maxProposals).let { trains ->
                                            withMessenger {
                                                genericTemplate(
                                                        trains.map {
                                                            with(it) {
                                                                genericElement(
                                                                        if (arrival) i18n("{0} {1}", displayInformations.commercialMode, displayInformations.headsign) else i18n("Direction {0}", displayInformations.direction),
                                                                        i18n("${if (arrival) "Arrivée" else "Départ"} {0}",
                                                                                (if (arrival) stopDateTime.arrivalDateTime else stopDateTime.departureDateTime) by timeFormat),
                                                                        trainImage
                                                                )
                                                            }
                                                        },
                                                        quickReply(
                                                                if (arrival) "Arrivées suivantes" else "Départs suivants",
                                                                more_elements,
                                                                parameters =
                                                                nextResultDate[nextDate] + nextResultOrigin[origin.name]
                                                        )
                                                )
                                            }
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
