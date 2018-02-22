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
import fr.vsct.tock.bot.open.data.client.sncf.model.Place
import fr.vsct.tock.bot.open.data.client.sncf.model.PlaceValue
import fr.vsct.tock.bot.open.data.departureDateEntity
import fr.vsct.tock.bot.open.data.destinationEntity
import fr.vsct.tock.bot.open.data.locationEntity
import fr.vsct.tock.bot.open.data.originEntity
import fr.vsct.tock.nlp.entity.date.DateEntityRange
import fr.vsct.tock.shared.defaultZoneId
import java.time.LocalDateTime

/**
 * entity values
 */
var BotBus.origin: PlaceValue?
    get() = entityValue(originEntity)
    set(value) = changeEntityValue(originEntity, value)

var BotBus.location: PlaceValue?
    get() = entityValue(locationEntity)
    set(value) = changeEntityValue(locationEntity, value)

var BotBus.destination: PlaceValue?
    get() = entityValue(destinationEntity)
    set(value) = changeEntityValue(destinationEntity, value)


val BotBus.departureDate: LocalDateTime?
    get() = entityValue<DateEntityRange>(departureDateEntity)?.start()?.withZoneSameInstant(defaultZoneId)?.toLocalDateTime()

fun BotBus.returnsAndRemoveLocation(): PlaceValue? {
    return location.apply {
        removeEntityValue(locationEntity)
    }
}




