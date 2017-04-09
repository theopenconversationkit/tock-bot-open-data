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
import fr.vsct.tock.bot.engine.dialog.ContextValue
import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient
import fr.vsct.tock.bot.open.data.client.sncf.model.Place
import fr.vsct.tock.bot.open.data.entity.PlaceValue
import fr.vsct.tock.nlp.entity.date.DateEntityValue
import ft.vsct.tock.nlp.api.client.model.Entity
import ft.vsct.tock.nlp.api.client.model.EntityType
import java.time.LocalDateTime
import java.time.ZoneId

var BotBus.origin: ContextValue?
    get() = entities["origin"]?.value
    set(value) = changeEntityValue("origin", value)

val BotBus.originPlace: Place? get() = origin.place()

val BotBus.location: ContextValue? get() = entities["location"]?.value

val BotBus.locationPlace: Place? get() = location.place()

var BotBus.destination: ContextValue?
    get() = entities["destination"]?.value
    set(value) = changeEntityValue("destination", value)

val BotBus.destinationPlace: Place? get() = destination.place()

val BotBus.departureDate: ContextValue? get() = entities["departure_date"]?.value

val BotBus.departureDateValue: LocalDateTime? get() = (departureDate?.value as DateEntityValue?)?.date?.withZoneSameInstant(ZoneId.of("Europe/Paris"))?.toLocalDateTime()

fun BotBus.returnAndRemoveLocation(): ContextValue? {
    return location.apply {
        removeEntityValue("location")
    }
}

fun BotBus.loadOrigin(location: String): ContextValue {
    return ContextValue(
            Entity(EntityType("vsc:location"), "origin"),
            SncfOpenDataClient.bestPlaceMatch(location)?.let { PlaceValue(it) },
            location)
}

fun Place?.toValue(): PlaceValue? = if (this == null) null else PlaceValue(this)

fun ContextValue?.placeValue(): PlaceValue? {
    return if (this == null) null
    else if (evaluated) {
        value as PlaceValue
    } else let {
        SncfOpenDataClient.bestPlaceMatch(content!!)?.let {
            changeValue(it.toValue()).value as PlaceValue
        }
    }
}

fun ContextValue?.place(): Place? = placeValue()?.place
