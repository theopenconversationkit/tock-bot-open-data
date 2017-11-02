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

import fr.vsct.tock.bot.connector.ConnectorType
import fr.vsct.tock.bot.connector.ga.carouselItem
import fr.vsct.tock.bot.connector.ga.gaConnectorType
import fr.vsct.tock.bot.connector.ga.gaFlexibleMessageForCarousel
import fr.vsct.tock.bot.connector.ga.gaImage
import fr.vsct.tock.bot.connector.messenger.genericElement
import fr.vsct.tock.bot.connector.messenger.genericTemplate
import fr.vsct.tock.bot.connector.messenger.messengerConnectorType
import fr.vsct.tock.bot.connector.messenger.quickReply
import fr.vsct.tock.bot.definition.ConnectorStoryHandlerBase
import fr.vsct.tock.bot.definition.ParameterKey
import fr.vsct.tock.bot.definition.StoryHandlerBase
import fr.vsct.tock.bot.definition.StoryHandlerDefinitionBase
import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.open.data.OpenDataConfiguration.trainImage
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SharedIntent.more_elements
import fr.vsct.tock.bot.open.data.client.sncf.model.Place
import fr.vsct.tock.bot.open.data.client.sncf.model.StationStop
import fr.vsct.tock.bot.open.data.story.ChoiceParameter.nextResultDate
import fr.vsct.tock.bot.open.data.story.ChoiceParameter.nextResultOrigin
import fr.vsct.tock.bot.open.data.story.ChoiceParameter.proposal
import fr.vsct.tock.bot.open.data.story.MessageFormat.timeFormat
import fr.vsct.tock.bot.open.data.story.ScoreboardStoryHandlerDefinition.ContextKey.startDate
import fr.vsct.tock.shared.defaultZoneId
import fr.vsct.tock.translator.by
import java.time.LocalDateTime
import java.time.ZonedDateTime

enum class ChoiceParameter : ParameterKey {
    nextResultDate, nextResultOrigin, proposal
}

/**
 *
 */
abstract class ScoreboardStoryHandler : StoryHandlerBase<ScoreboardStoryHandlerDefinition>() {

    abstract val missingOriginMessage: String

    abstract fun newDefinition(bus: BotBus): ScoreboardStoryHandlerDefinition

    override fun computeStoryHandlerDefinition(bus: BotBus): ScoreboardStoryHandlerDefinition? {
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
                else -> return newDefinition(bus)
            }

            return null
        }
    }
}

abstract class ScoreboardStoryHandlerDefinition(bus: BotBus)
    : StoryHandlerDefinitionBase<ScoreboardConnectorStoryHandler>(bus) {

    override fun provideConnector(connectorType: ConnectorType): ScoreboardConnectorStoryHandler? =
            when (connectorType) {
                messengerConnectorType -> MessengerScoreboardConnectorStoryHandler(this)
                gaConnectorType -> GaScoreboardConnectorStoryHandler(this)
                else -> null
            }

    companion object {
        private val maxProposals: Int = 10
    }

    val o: Place = origin!!

    private enum class ContextKey : ParameterKey {
        startDate
    }

    protected var currentDate: LocalDateTime
        get() = contextValue(startDate) ?: ZonedDateTime.now(defaultZoneId).toLocalDateTime()
        set(value) = changeContextValue(startDate, value)


    abstract val headerMessage: String
    abstract val noResultMessage: String
    abstract val nextMessage: String

    abstract fun retrieveStops(): List<StationStop>

    abstract fun timeFor(stop: StationStop): LocalDateTime

    abstract fun itemTitle(stop: StationStop): CharSequence

    abstract val itemSubtitleMessage: String

    override fun handle() {
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


            stops
                    .filter { timeFor(it) >= currentDate }
                    .also { filteredStops ->
                        if (filteredStops.isEmpty()) {
                            end("Oups, plus de résultats, désolé :(")
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
sealed class ScoreboardConnectorStoryHandler(context: ScoreboardStoryHandlerDefinition)
    : ConnectorStoryHandlerBase<ScoreboardStoryHandlerDefinition>(context) {

    fun ScoreboardStoryHandlerDefinition.subtitle(stop: StationStop): CharSequence
            = i18n(itemSubtitleMessage, timeFor(stop) by timeFormat)

    abstract fun display(trains: List<StationStop>, nextDate: LocalDateTime)
}

/**
 * Messenger specific behaviour.
 */
class MessengerScoreboardConnectorStoryHandler(context: ScoreboardStoryHandlerDefinition) : ScoreboardConnectorStoryHandler(context) {

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
}

/**
 * Google Assistant specific behaviour.
 */
class GaScoreboardConnectorStoryHandler(context: ScoreboardStoryHandlerDefinition) : ScoreboardConnectorStoryHandler(context) {

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
}
