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

import ai.tock.bot.definition.storyWithSteps
import ai.tock.bot.engine.BotBus
import ai.tock.bot.open.data.SecondaryIntent.indicate_location
import ai.tock.bot.open.data.SecondaryIntent.indicate_origin
import ai.tock.bot.open.data.SecondaryIntent.more_elements
import ai.tock.bot.open.data.SecondaryIntent.select
import ai.tock.bot.open.data.client.sncf.SncfOpenDataClient
import ai.tock.bot.open.data.client.sncf.model.StationStop
import java.time.LocalDateTime

val arrivals = storyWithSteps<ScoreboardSteps>(
    Arrivals,
    secondaryIntents = setOf(indicate_location, indicate_origin, more_elements, select)
)

object Arrivals : Scoreboard() {

    override val missingOriginMessage = "From which station would you like to see the arrivals?"
    override fun newHandlerDefinition(bus: BotBus, data: Any?): ScoreboardDef = ArrivalsDef(bus)
}

class ArrivalsDef(bus: BotBus) : ScoreboardDef(bus) {

    override val headerMessage: String = "Arrivals at {0}:"

    override val noResultMessage: String = "Oops, no arrival currently found, sorry :("

    override val nextMessage: String = "Next arrivals"

    override fun retrieveStops(): List<StationStop> = SncfOpenDataClient.arrivals(o, currentDate)

    override fun timeFor(stop: StationStop): LocalDateTime = stop.stopDateTime.arrivalDateTime

    override fun itemTitle(stop: StationStop): CharSequence = i18n(
        "{0} {1}",
        stop.displayInformations.commercialMode,
        stop.displayInformations.headsign
    )

    override val itemSubtitleMessage = "Arrival at {0}"


}
