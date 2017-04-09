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

import fr.vsct.tock.bot.open.data.client.sncf.model.DeparturesResponse
import fr.vsct.tock.bot.open.data.client.sncf.model.JourneysResponse
import fr.vsct.tock.bot.open.data.client.sncf.model.PlacesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SncfOpenDataApi {

    @GET("places")
    fun places(@Query("q") query: String): Call<PlacesResponse>

    @GET("journeys")
    fun journeys(@Query("from") from: String, @Query("to") to: String, @Query("datetime") datetime: String): Call<JourneysResponse>

    @GET("stop_areas/{stopId}/departures")
    fun departures(@Path("stopId") stopId: String, @Query("datetime") datetime: String): Call<DeparturesResponse>

}

