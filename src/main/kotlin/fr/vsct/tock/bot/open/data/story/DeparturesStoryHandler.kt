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


import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient
import fr.vsct.tock.bot.open.data.client.sncf.model.StationStop
import java.time.LocalDateTime

/**
 *
 */
object DeparturesStoryHandler : ScoreboardStoryHandler() {

    override val missingOriginMessage = "De quelle gare souhaitez vous voir les départs?"

    override fun newDefinition(bus: BotBus): ScoreboardStoryHandlerDefinition
            = DeparturesStoryHandlerDefinition(bus)

}

class DeparturesStoryHandlerDefinition(bus: BotBus) : ScoreboardStoryHandlerDefinition(bus) {

    override val headerMessage: String = "Départs de la gare de {0} :"

    override val noResultMessage: String = "Oups, aucun départ trouvé actuellement, désolé :("

    override val nextMessage: String = "Départs suivants"

    override fun retrieveStops(): List<StationStop> = SncfOpenDataClient.departures(o, currentDate)

    override fun timeFor(stop: StationStop): LocalDateTime = stop.stopDateTime.departureDateTime

    override fun itemTitle(stop: StationStop): CharSequence
            = i18n("Direction {0}", stop.displayInformations.direction)

    override val itemSubtitleMessage = "Départ {0}"
}
