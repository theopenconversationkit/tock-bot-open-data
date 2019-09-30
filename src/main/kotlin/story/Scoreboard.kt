/*
 *  This file is part of the tock-bot-open-data distribution.
 *  (https://github.com/theopenconversationkit/tock-bot-open-data)
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

package ai.tock.bot.open.data.story

import ai.tock.bot.connector.ConnectorMessage
import ai.tock.bot.connector.ga.GAHandler
import ai.tock.bot.connector.ga.carouselItem
import ai.tock.bot.connector.ga.gaFlexibleMessageForCarousel
import ai.tock.bot.connector.ga.gaImage
import ai.tock.bot.connector.messenger.MessengerHandler
import ai.tock.bot.connector.messenger.genericElement
import ai.tock.bot.connector.messenger.genericTemplate
import ai.tock.bot.connector.messenger.quickReply
import ai.tock.bot.definition.ConnectorDef
import ai.tock.bot.definition.Handler
import ai.tock.bot.definition.HandlerDef
import ai.tock.bot.definition.IntentAware
import ai.tock.bot.definition.ParameterKey
import ai.tock.bot.definition.StoryStep
import ai.tock.bot.engine.BotBus
import ai.tock.bot.open.data.OpenDataConfiguration.stationImage
import ai.tock.bot.open.data.OpenDataConfiguration.trainImage
import ai.tock.bot.open.data.SecondaryIntent
import ai.tock.bot.open.data.SecondaryIntent.more_elements
import ai.tock.bot.open.data.client.sncf.SncfOpenDataClient
import ai.tock.bot.open.data.client.sncf.SncfOpenDataClient.findPlace
import ai.tock.bot.open.data.client.sncf.model.Place
import ai.tock.bot.open.data.client.sncf.model.StationStop
import ai.tock.bot.open.data.client.sncf.model.VehicleJourney
import ai.tock.bot.open.data.story.ChoiceParameter.nextResultDate
import ai.tock.bot.open.data.story.ChoiceParameter.nextResultOrigin
import ai.tock.bot.open.data.story.ChoiceParameter.proposal
import ai.tock.bot.open.data.story.MessageFormat.timeFormat
import ai.tock.bot.open.data.story.ScoreboardDef.ContextKey.currentStops
import ai.tock.bot.open.data.story.ScoreboardDef.ContextKey.startDate
import ai.tock.nlp.entity.OrdinalValue
import ai.tock.shared.defaultZoneId
import ai.tock.translator.by
import ai.tock.translator.formatWith
import ai.tock.translator.raw
import java.time.LocalDateTime
import java.time.ZonedDateTime

enum class ChoiceParameter : ParameterKey {
    nextResultDate, nextResultOrigin, proposal
}

enum class ScoreboardSteps : StoryStep<ScoreboardDef> {

    display,

    select {

        override val intent: IntentAware? = SecondaryIntent.select
        override fun answer(): ScoreboardDef.() -> Any? = {
            end {
                if (displayedStops.isEmpty()) {
                    "No proposal to choose. :("
                } else if (ordinal < 0 || ordinal >= displayedStops.size) {
                    "I do not find this proposal. :("
                } else {
                    val stop = displayedStops[ordinal]
                    stop.findVehicleId()
                        ?.let { SncfOpenDataClient.vehicleJourney(it) }
                        ?.also { journey ->
                            connector?.displayDetails(journey)
                        }
                            ?: "Trip not found"
                }
            }
        }
    },

    disruption {
        override fun answer(): ScoreboardDef.() -> Any? = {
            answer()
        }
    };

}

/**
 *
 */
abstract class Scoreboard : Handler<ScoreboardDef>() {

    abstract val missingOriginMessage: String

    override fun checkPreconditions(): BotBus.() -> Unit = {
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
        if (origin == null) {
            end(missingOriginMessage)
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
sealed class ScoreboardConnector(context: ScoreboardDef) : ConnectorDef<ScoreboardDef>(context) {

    fun ScoreboardDef.subtitle(stop: StationStop): CharSequence =
        i18n(itemSubtitleMessage, timeFor(stop) by timeFormat)

    fun display(trains: List<StationStop>, nextDate: LocalDateTime) =
        withMessage(connectorDisplay(trains, nextDate).invoke(context))

    abstract fun connectorDisplay(
        trains: List<StationStop>,
        nextDate: LocalDateTime
    ): ScoreboardDef.() -> ConnectorMessage

    fun displayDetails(journey: VehicleJourney) = withMessage(connectorDisplayDetails(journey).invoke(context))

    abstract fun connectorDisplayDetails(journey: VehicleJourney): ScoreboardDef.() -> ConnectorMessage
}

/**
 * Messenger specific behaviour.
 */
class MessengerScoreboardConnector(context: ScoreboardDef) : ScoreboardConnector(context) {

    override fun connectorDisplay(
        trains: List<StationStop>,
        nextDate: LocalDateTime
    ): ScoreboardDef.() -> ConnectorMessage = {
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
    }

    override fun connectorDisplayDetails(journey: VehicleJourney): ScoreboardDef.() -> ConnectorMessage = {
        genericTemplate(
            journey.stopTimes.take(10).map {
                genericElement(
                    (it.stopPoint?.name ?: "").raw,
                    it.departureTime.formatWith(timeFormat, userPreferences.locale),
                    stationImage
                )
            }
        )
    }
}

/**
 * Google Assistant specific behaviour.
 */
class GAScoreboardConnector(context: ScoreboardDef) : ScoreboardConnector(context) {

    override fun connectorDisplay(
        trains: List<StationStop>,
        nextDate: LocalDateTime
    ): ScoreboardDef.() -> ConnectorMessage = {
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
    }

    override fun connectorDisplayDetails(journey: VehicleJourney): ScoreboardDef.() -> ConnectorMessage = {
        gaFlexibleMessageForCarousel(
            journey.stopTimes.take(10).mapIndexed { i, it ->
                with(it) {
                    carouselItem(
                        SecondaryIntent.select,
                        (it.stopPoint?.name ?: "").raw,
                        it.departureTime.formatWith(timeFormat, userPreferences.locale),
                        gaImage(stationImage, "station"),
                        proposal[i]
                    )
                }
            }
        )
    }

}
