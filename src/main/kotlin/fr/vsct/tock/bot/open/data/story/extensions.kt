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
import fr.vsct.tock.nlp.api.client.model.Entity
import fr.vsct.tock.nlp.api.client.model.EntityType
import fr.vsct.tock.nlp.entity.date.DateEntityRange
import java.time.LocalDateTime

private val originEntity = Entity(EntityType("vsc:location"), "origin")
private val destinationEntity = Entity(EntityType("vsc:location"), "destination")
private val locationEntity = Entity(EntityType("vsc:location"), "location")
private val departureDateEntity = Entity(EntityType("duckling:datetime"), "departure_date")

private fun ContextValue?.placeValue(): PlaceValue? {
    return if (this == null) null
    else if (evaluated) {
        value as PlaceValue
    } else {
        content?.let {
            findPlaceValue(it)?.let {
                changeValue(it).value as PlaceValue
            }
        }
    }
}

private fun findPlaceValue(name: String): PlaceValue? {
    return SncfOpenDataClient.bestPlaceMatch(name)?.let {
        PlaceValue(it)
    }
}

fun findPlace(name: String): Place? {
    return findPlaceValue(name)?.place
}

var BotBus.origin: Place?
    get() = entities[originEntity.role]?.value?.placeValue()?.place
    set(value) = changeEntityValue(originEntity, value?.let { PlaceValue(value) })

val BotBus.location: Place? get() = entities[locationEntity.role]?.value?.placeValue()?.place

var BotBus.destination: Place?
    get() = entities[destinationEntity.role]?.value?.placeValue()?.place
    set(value) = changeEntityValue(destinationEntity, value?.let { PlaceValue(value) })


val BotBus.departureDate: LocalDateTime?
    get() = entityValue<DateEntityRange>(departureDateEntity.role)?.start()?.toLocalDateTime()

fun BotBus.returnsAndRemoveLocation(): Place? {
    return location.apply {
        removeEntityValue("location")
    }
}
