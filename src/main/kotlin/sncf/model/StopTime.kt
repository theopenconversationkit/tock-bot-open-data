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

package ai.tock.bot.open.data.client.sncf.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import ai.tock.bot.open.data.client.sncf.model.jackson.LocalTimeDeserializer
import java.time.LocalTime

/**
 *
 */
data class StopTime(
    @JsonProperty("stop_point")
    val stopPoint: StopPoint?,
    @JsonProperty("departure_time")
    @JsonDeserialize(using = LocalTimeDeserializer::class)
    val departureTime: LocalTime?,
    @JsonProperty("arrival_time")
    @JsonDeserialize(using = LocalTimeDeserializer::class)
    val arrivalTime: LocalTime?
)

