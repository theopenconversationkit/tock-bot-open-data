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

import fr.vsct.tock.bot.connector.ga.GAHandler
import fr.vsct.tock.bot.connector.ga.carouselItem
import fr.vsct.tock.bot.connector.ga.gaFlexibleMessageForCarousel
import fr.vsct.tock.bot.connector.ga.gaImage
import fr.vsct.tock.bot.connector.messenger.MessengerHandler
import fr.vsct.tock.bot.connector.messenger.genericElement
import fr.vsct.tock.bot.connector.messenger.genericTemplate
import fr.vsct.tock.bot.connector.messenger.quickReply
import fr.vsct.tock.bot.definition.ConnectorDef
import fr.vsct.tock.bot.definition.Handler
import fr.vsct.tock.bot.definition.HandlerDef
import fr.vsct.tock.bot.definition.IntentAware
import fr.vsct.tock.bot.definition.ParameterKey
import fr.vsct.tock.bot.definition.StoryStep
import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.open.data.OpenDataConfiguration.stationImage
import fr.vsct.tock.bot.open.data.OpenDataConfiguration.trainImage
import fr.vsct.tock.bot.open.data.SecondaryIntent
import fr.vsct.tock.bot.open.data.SecondaryIntent.more_elements
import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient
import fr.vsct.tock.bot.open.data.client.sncf.model.Place
import fr.vsct.tock.bot.open.data.client.sncf.model.StationStop
import fr.vsct.tock.bot.open.data.client.sncf.model.VehicleJourney
import fr.vsct.tock.bot.open.data.story.ChoiceParameter.nextResultDate
import fr.vsct.tock.bot.open.data.story.ChoiceParameter.nextResultOrigin
import fr.vsct.tock.bot.open.data.story.ChoiceParameter.proposal
import fr.vsct.tock.bot.open.data.story.MessageFormat.timeFormat
import fr.vsct.tock.bot.open.data.story.ScoreboardDef.ContextKey.currentStops
import fr.vsct.tock.bot.open.data.story.ScoreboardDef.ContextKey.startDate
import fr.vsct.tock.nlp.entity.OrdinalValue
import fr.vsct.tock.shared.defaultZoneId
import fr.vsct.tock.translator.by
import fr.vsct.tock.translator.raw
import java.time.LocalDateTime
import java.time.ZonedDateTime

enum class ChoiceParameter : ParameterKey {
    nextResultDate, nextResultOrigin, proposal
}

enum class ScoreboardSteps : StoryStep<ScoreboardDef> {

    display,

    select {

        override val intent: IntentAware? = SecondaryIntent.select

        override fun answer(handler: ScoreboardDef): Unit =
                with(handler) {
                    if (displayedStops.isEmpty()) {
                        end("No proposal to choose. :(")
                    }
                    if (ordinal < 0 || ordinal >= displayedStops.size) {
                        end("I do not find this proposal. :(")
                    } else {
                        val stop = displayedStops[ordinal]
                        stop.findVehicleId()
                                ?.let { SncfOpenDataClient.vehicleJourney(it) }
                                ?.also {
                                    handler.connector?.displayDetails(it)
                                    end()
                                }
                                ?: end("Trip not found")
                    }
                }
    },

    disruption {
        override fun answer(handler: ScoreboardDef): Unit? {
            return handler.answer()
        }
    };

}

/**
 *
 */
abstract class Scoreboard : Handler<ScoreboardDef>() {

    abstract val missingOriginMessage: String

    abstract fun scoreboardDef(bus: BotBus): ScoreboardDef

    override fun setupHandlerDef(bus: BotBus): ScoreboardDef? {
        with(bus) {
            //check location entity
            if (location != null) {
                origin = returnsAndRemoveLocation()
            }

            //handle next result
            choice(nextResultOrigin)
                    ?.run {
                        origin = findPlace(this)
                    }

            //check mandatory entities
            when (origin) {
                null -> end(missingOriginMessage)
                else -> return scoreboardDef(bus)
            }

            return null
        }
    }
}

@GAHandler(GAScoreboardConnector::class)
@MessengerHandler(MessengerScoreboardConnector::class)
abstract class ScoreboardDef(bus: BotBus) : HandlerDef<ScoreboardConnector>(bus) {

    companion object {
        private val maxProposals: Int = 10
    }

    val o: Place = origin!!

    private enum class ContextKey : ParameterKey {
        startDate, currentStops
    }

    protected var currentDate: LocalDateTime
        get() = contextValue(startDate) ?: ZonedDateTime.now(defaultZoneId).toLocalDateTime()
        set(value) = changeContextValue(startDate, value)

    var displayedStops: Array<StationStop>
        get() = contextValue(currentStops) ?: emptyArray()
        set(value) = changeContextValue(currentStops, value)

    val ordinal: Int get() = (entityValue<OrdinalValue>("ordinal")?.value?.toInt() ?: 1) - 1

    abstract val headerMessage: String
    abstract val noResultMessage: String
    abstract val nextMessage: String

    abstract fun retrieveStops(): List<StationStop>

    abstract fun timeFor(stop: StationStop): LocalDateTime

    abstract fun itemTitle(stop: StationStop): CharSequence

    abstract val itemSubtitleMessage: String

    override fun answer() {
        //retrieve start date from postback
        choice(nextResultDate)?.apply {
            currentDate = LocalDateTime.parse(this)
        }

        send(headerMessage, origin)

        var stops = retrieveStops()

        if (stops.isEmpty()) {
            end(noResultMessage)
        } else {
            val nextIndex = Math.min(stops.size - 1, maxProposals)
            var nextDate = if (stops.isEmpty()) currentDate.plusHours(1) else timeFor(stops[nextIndex])

            //if more_elements comes from a choice, we know the next date as it is passed as parameter
            //else we will skip the maxProposals first elements of the api request
            if (isIntent(more_elements) && choice(nextResultDate) == null) {
                stops = stops.subList(nextIndex, stops.size)
                currentDate = nextDate
                if (stops.size < 2) {
                    stops = retrieveStops()
                }
                if (stops.isNotEmpty()) {
                    nextDate = timeFor(stops[Math.min(stops.size - 1, maxProposals)])
                }
            }

            displayedStops = stops.toTypedArray()

            stops
                    .filter { timeFor(it) >= currentDate }
                    .also { filteredStops ->
                        if (filteredStops.isEmpty()) {
                            end("Oops, no more results, sorry :(")
                        } else {
                            filteredStops.take(maxProposals).let {
                                connector?.display(it, nextDate)
                            }
                            end()
                        }
                    }
        }
    }
}

/**
 * Connector specific behaviour.
 */
sealed class ScoreboardConnector(context: ScoreboardDef)
    : ConnectorDef<ScoreboardDef>(context) {

    fun ScoreboardDef.subtitle(stop: StationStop): CharSequence
            = i18n(itemSubtitleMessage, timeFor(stop) by timeFormat)

    abstract fun display(trains: List<StationStop>, nextDate: LocalDateTime)

    abstract fun displayDetails(journey: VehicleJourney)
}

/**
 * Messenger specific behaviour.
 */
class MessengerScoreboardConnector(context: ScoreboardDef) : ScoreboardConnector(context) {

    override fun display(trains: List<StationStop>, nextDate: LocalDateTime) {
        with(context) {
            withMessage(
                    genericTemplate(
                            trains.map {
                                genericElement(
                                        itemTitle(it),
                                        subtitle(it),
                                        trainImage
                                )
                            },
                            quickReply(
                                    nextMessage,
                                    more_elements,
                                    parameters =
                                    nextResultDate[nextDate] + nextResultOrigin[o.name]
                            )
                    )
            )
        }
    }

    override fun displayDetails(journey: VehicleJourney) {
        with(context) {
            withMessage(
                    genericTemplate(
                            journey.stopTimes.take(10).map {
                                genericElement(
                                        (it.stopPoint?.name ?: "").raw,
                                        it.departureTime?.format(timeFormat)?.raw,
                                        stationImage
                                )
                            }
                    )
            )
        }
    }
}

/**
 * Google Assistant specific behaviour.
 */
class GAScoreboardConnector(context: ScoreboardDef) : ScoreboardConnector(context) {

    override fun display(trains: List<StationStop>, nextDate: LocalDateTime) {
        with(context) {
            withMessage(
                    gaFlexibleMessageForCarousel(
                            trains.mapIndexed { i, it ->
                                with(it) {
                                    carouselItem(
                                            intent!!,
                                            itemTitle(it),
                                            subtitle(it),
                                            gaImage(trainImage, "train"),
                                            proposal[i]
                                    )
                                }
                            },
                            listOf(nextMessage)
                    )
            )
        }
    }

    override fun displayDetails(journey: VehicleJourney) {
        with(context) {
            withMessage(
                    gaFlexibleMessageForCarousel(
                            journey.stopTimes.take(10).mapIndexed { i, it ->
                                with(it) {
                                    carouselItem(
                                            SecondaryIntent.select,
                                            (it.stopPoint?.name ?: "").raw,
                                            it.departureTime?.format(timeFormat)?.raw,
                                            gaImage(stationImage, "station"),
                                            proposal[i]
                                    )
                                }
                            }
                    )
            )
        }
    }
}
