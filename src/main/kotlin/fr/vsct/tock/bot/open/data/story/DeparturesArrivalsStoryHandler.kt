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
import fr.vsct.tock.bot.definition.ParameterKey
import fr.vsct.tock.bot.definition.StoryHandlerBase
import fr.vsct.tock.bot.definition.StoryStep
import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SecondaryIntent.arrivals
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SecondaryIntent.more_elements
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.departures
import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient
import fr.vsct.tock.bot.open.data.client.sncf.model.StationStop
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler.ChoiceParameter.nextResultDate
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler.ChoiceParameter.nextResultOrigin
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler.ContextKey.startDate
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler.DeparturesArrivalsSteps.arrivalsStep
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler.DeparturesArrivalsSteps.departuresStep
import fr.vsct.tock.bot.open.data.story.MessageFormat.timeFormat
import fr.vsct.tock.shared.defaultZoneId
import fr.vsct.tock.translator.I18nLabelKey
import fr.vsct.tock.translator.by
import java.time.LocalDateTime
import java.time.ZonedDateTime

/**
 *
 */
object DeparturesArrivalsStoryHandler : StoryHandlerBase() {

    enum class DeparturesArrivalsSteps : StoryStep {
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
            //check location entity
            if (location != null) {
                origin = returnsAndRemoveLocation()
            }

            //handle more_elements intent
            //if more_elements comes from a choice, we know the next date as it is passed as parameter
            //else we will skip the 4 first elements of the api request
            val skip4First = if (isIntent(more_elements)) {

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
                    val nextIndex = Math.min(stops.size, 4)
                    var nextDate =
                            if (stops.isEmpty()) currentDate
                            else stops[nextIndex].stopDateTime.run {
                                if (arrival) arrivalDateTime else departureDateTime
                            }
                    //need to skip 4 here
                    if (skip4First && stops.isNotEmpty()) {
                        stops = stops.subList(nextIndex, stops.size)
                        if (stops.isNotEmpty()) {
                            currentDate = nextDate
                            nextDate = stops[Math.min(stops.size, 4)].stopDateTime.run {
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
                                .apply {
                                    if (isEmpty()) {
                                        end("Oups, plus de résultats, désolé :(")
                                    }
                                    //messengers list does not support only 1 element
                                    else if (size == 1) {
                                        withMessengerGeneric(first().run {
                                            messengerGenericElement(title(arrival), description(arrival))
                                        })
                                        end()
                                    } else {
                                        take(4).also { trains ->
                                            withMessengerList(
                                                    trains.map {
                                                        messengerListElement(it.title(arrival), it.description(arrival))
                                                    },
                                                    ListElementStyle.compact,
                                                    messengerPostback(
                                                            if (arrival) "Arrivées suivantes" else "Départs suivants",
                                                            more_elements,
                                                            parameters =
                                                            nextResultDate[nextDate] + nextResultOrigin[origin.name]
                                                    )
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

    private fun StationStop.title(arrivals: Boolean): I18nLabelKey {
        return if (arrivals) i18n("{0} {1}", displayInformations.commercialMode, displayInformations.headsign) else i18n("Direction {0}", displayInformations.direction)
    }

    private fun StationStop.description(arrivals: Boolean): I18nLabelKey {
        return i18n("${if (arrivals) "Arrivée" else "Départ"} {0}",
                (if (arrivals) stopDateTime.arrivalDateTime else stopDateTime.departureDateTime) by timeFormat)
    }
}
