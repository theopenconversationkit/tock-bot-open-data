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

package ai.tock.bot.open.data.client.sncf

import ai.tock.bot.open.data.client.sncf.model.ArrivalsResponse
import ai.tock.bot.open.data.client.sncf.model.DeparturesResponse
import ai.tock.bot.open.data.client.sncf.model.JourneysResponse
import ai.tock.bot.open.data.client.sncf.model.PlacesNearbyResponse
import ai.tock.bot.open.data.client.sncf.model.PlacesResponse
import ai.tock.bot.open.data.client.sncf.model.VehicleJourneysResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SncfOpenDataApi {

    @GET("places")
    fun places(@Query("q") query: String): Call<PlacesResponse>

    @GET("places/{id}/places_nearby?distance=10000&type=stop_area")
    fun placesNearby(@Path("id") id: String): Call<PlacesNearbyResponse>

    @GET("journeys")
    fun journeys(@Query("from") from: String, @Query("to") to: String, @Query("datetime") datetime: String): Call<JourneysResponse>

    @GET("stop_areas/{stopId}/departures")
    fun departures(@Path("stopId") stopId: String, @Query("from_datetime") datetime: String): Call<DeparturesResponse>

    @GET("stop_areas/{stopId}/arrivals")
    fun arrivals(@Path("stopId") stopId: String, @Query("from_datetime") datetime: String): Call<ArrivalsResponse>

    @GET("vehicle_journeys/{vehicleId}")
    fun vehicleJourneys(@Path("vehicleId") vehicleId: String): Call<VehicleJourneysResponse>
}

