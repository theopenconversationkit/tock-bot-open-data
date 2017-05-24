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
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import fr.vsct.tock.bot.open.data.OpenDataConfiguration
import fr.vsct.tock.bot.open.data.client.sncf.model.Departure
import fr.vsct.tock.bot.open.data.client.sncf.model.Journey
import fr.vsct.tock.bot.open.data.client.sncf.model.Place
import fr.vsct.tock.shared.addJacksonConverter
import fr.vsct.tock.shared.create
import fr.vsct.tock.shared.error
import fr.vsct.tock.shared.jackson.addDeserializer
import fr.vsct.tock.shared.jackson.addSerializer
import fr.vsct.tock.shared.jackson.mapper
import fr.vsct.tock.shared.retrofitBuilderWithTimeout
import mu.KotlinLogging
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


/**
 *
 */
object SncfOpenDataClient {

    private val logger = KotlinLogging.logger {}

    private val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")

    private val api: SncfOpenDataApi =
            retrofitBuilderWithTimeout(
                    30000,
                    object : Interceptor {
                        override fun intercept(chain: Interceptor.Chain): Response {
                            return chain.proceed(chain.request()
                                    .newBuilder()
                                    .header("Authorization", Credentials.basic(OpenDataConfiguration.sncfApiUser, ""))
                                    .build())
                        }
                    })
                    .baseUrl("https://api.sncf.com/v1/coverage/sncf/")
                    .addJacksonConverter(mapper.copy().registerModule(SimpleModule()
                            .addDeserializer(LocalDateTime::class, LocalDateTimeDeserializer(dateFormat))
                            .addSerializer(LocalDateTime::class, LocalDateTimeSerializer(dateFormat))
                    ))
                    .build()
                    .create()

    private val placesCache: Cache<String, List<Place>> = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(12, TimeUnit.HOURS).build()
    private val departuresCache: Cache<Pair<String, LocalDateTime>, List<Departure>> = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(1, TimeUnit.MINUTES).build()


    fun <K, V> Cache<K, V>.getOrPut(key: K,
                                    defaultValue: () -> V): V? {
        return getIfPresent(key) ?: try {
            val result = defaultValue.invoke()
            if (result != null) {
                put(key, result)
            }
            result
        } catch(t: Throwable) {
            logger.error(t)
            null
        }
    }

    fun places(query: String): List<Place> {
        return placesCache.getOrPut(query) {
            api.places(query).execute().body().places
        } ?: emptyList()
    }

    fun bestPlaceMatch(query: String): Place? {
        return places(query).maxBy { it.quality }
    }

    fun journey(from: Place, to: Place, datetime: LocalDateTime): List<Journey> {
        return api.journeys(from.id, to.id, dateFormat.format(datetime)).execute().body().journeys
    }

    fun departures(from: Place, datetime: LocalDateTime): List<Departure> {
        return departuresCache.getOrPut(from.id to datetime) {
            api.departures(from.id, dateFormat.format(datetime)).execute().body().departures
        } ?: emptyList()
    }
}