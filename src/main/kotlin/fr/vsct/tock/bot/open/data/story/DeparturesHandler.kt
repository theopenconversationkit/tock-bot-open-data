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


import fr.vsct.tock.bot.definition.storyWithSteps
import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.open.data.SecondaryIntent.indicate_location
import fr.vsct.tock.bot.open.data.SecondaryIntent.indicate_origin
import fr.vsct.tock.bot.open.data.SecondaryIntent.more_elements
import fr.vsct.tock.bot.open.data.SecondaryIntent.select
import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient
import fr.vsct.tock.bot.open.data.client.sncf.model.StationStop
import java.time.LocalDateTime

val departures = storyWithSteps<ScoreboardSteps>(
    Departures,
    otherStarterIntents = setOf(indicate_location),
    secondaryIntents = setOf(indicate_origin, more_elements, select)
)

/**
 *
 */
object Departures : Scoreboard() {

    override val missingOriginMessage = "From which station would you like to see the departures?"

    override fun newHandlerDefinition(bus: BotBus): ScoreboardDef = DeparturesDef(bus)

}

class DeparturesDef(bus: BotBus) : ScoreboardDef(bus) {

    override val headerMessage: String = "Departures from: {0}"

    override val noResultMessage: String = "Oops, no departure currently found, sorry :("

    override val nextMessage: String = "Next departures"

    override fun retrieveStops(): List<StationStop> = SncfOpenDataClient.departures(o, currentDate)

    override fun timeFor(stop: StationStop): LocalDateTime = stop.stopDateTime.departureDateTime

    override fun itemTitle(stop: StationStop): CharSequence = i18n("Direction {0}", stop.displayInformations.direction)

    override val itemSubtitleMessage = "Departure {0}"
}
