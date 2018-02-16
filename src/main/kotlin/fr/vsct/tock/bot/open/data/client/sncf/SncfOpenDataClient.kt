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

package fr.vsct.tock.bot.open.data.client.sncf

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import fr.vsct.tock.bot.open.data.OpenDataConfiguration
import fr.vsct.tock.bot.open.data.client.sncf.model.Journey
import fr.vsct.tock.bot.open.data.client.sncf.model.Place
import fr.vsct.tock.bot.open.data.client.sncf.model.StationStop
import fr.vsct.tock.bot.open.data.client.sncf.model.VehicleJourney
import fr.vsct.tock.shared.addJacksonConverter
import fr.vsct.tock.shared.create
import fr.vsct.tock.shared.jackson.addDeserializer
import fr.vsct.tock.shared.jackson.addSerializer
import fr.vsct.tock.shared.jackson.mapper
import fr.vsct.tock.shared.retrofitBuilderWithTimeoutAndLogger
import mu.KotlinLogging
import okhttp3.Credentials
import okhttp3.Interceptor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 *
 */
object SncfOpenDataClient {

    private val logger = KotlinLogging.logger {}
    private val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")

    private val api: SncfOpenDataApi =
        retrofitBuilderWithTimeoutAndLogger(
            30000,
            logger,
            interceptors = listOf(Interceptor { chain ->
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .header("Authorization", Credentials.basic(OpenDataConfiguration.sncfApiUser, ""))
                        .build()
                )
            })
        )
            .baseUrl("https://api.sncf.com/v1/coverage/sncf/")
            .addJacksonConverter(
                mapper.copy().registerModule(
                    SimpleModule()
                        .addDeserializer(LocalDateTime::class, LocalDateTimeDeserializer(dateFormat))
                        .addSerializer(LocalDateTime::class, LocalDateTimeSerializer(dateFormat))
                )
            )
            .build()
            .create()

    fun findPlace(name: String): Place? {
        return bestPlaceMatch(name)
    }

    fun places(query: String): List<Place> {
        return api.places(query).execute().body()?.places ?: emptyList()
    }

    fun bestPlaceMatch(query: String): Place? {
        val p = places(query)/*.sortedByDescending { it.quality }*/.firstOrNull()
        return if (p != null && p.embeddedType != "stop_area") {
            api.placesNearby(p.id)
                .execute()
                .body()
                ?.places
                ?.run {
                    firstOrNull { it.embeddedType == "stop_area" }
                            ?: firstOrNull()
                }
        } else {
            p
        }
    }

    fun journey(from: Place, to: Place, datetime: LocalDateTime): List<Journey> {
        return api.journeys(from.id, to.id, dateFormat.format(datetime)).execute().body()?.journeys ?: emptyList()
    }

    fun departures(from: Place, datetime: LocalDateTime): List<StationStop> {
        return api
            .departures(from.id, dateFormat.format(datetime))
            .execute()
            .body()
            ?.departures
                ?: emptyList()
    }

    fun arrivals(from: Place, datetime: LocalDateTime): List<StationStop> {
        return api
            .arrivals(from.id, dateFormat.format(datetime))
            .execute()
            .body()
            ?.arrivals
                ?: emptyList()
    }

    fun vehicleJourney(id: String): VehicleJourney? {
        return api.vehicleJourneys(id).execute().body()?.vehicleJourney?.firstOrNull()
    }
}