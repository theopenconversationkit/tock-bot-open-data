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

val arrivals = storyWithSteps<ScoreboardSteps>(
        Arrivals,
        secondaryIntents = setOf(indicate_location, indicate_origin, more_elements, select)
)

object Arrivals : Scoreboard() {

    override val missingOriginMessage = "De quelle gare souhaitez vous voir les arrivées?"
    override fun scoreboardDef(bus: BotBus): ScoreboardDef = ArrivalsDef(bus)
}

class ArrivalsDef(bus: BotBus) : ScoreboardDef(bus) {

    override val headerMessage: String = "Arrivées à la gare de {0} :"

    override val noResultMessage: String = "Oups, aucune arrivée trouvée actuellement, désolé :("

    override val nextMessage: String = "Arrivées suivantes"

    override fun retrieveStops(): List<StationStop> = SncfOpenDataClient.arrivals(o, currentDate)

    override fun timeFor(stop: StationStop): LocalDateTime = stop.stopDateTime.arrivalDateTime

    override fun itemTitle(stop: StationStop): CharSequence
            = i18n(
            "{0} {1}",
            stop.displayInformations.commercialMode,
            stop.displayInformations.headsign)

    override val itemSubtitleMessage = "Arrivée {0}"


}
