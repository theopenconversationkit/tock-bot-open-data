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

package fr.vsct.tock.bot.open.data.client

import fr.vsct.tock.bot.open.data.client.sncf.SncfOpenDataClient
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 *
 */
class SncfOpenDataClientIntegrationTest {

    @Test
    fun testPlaces() {
        SncfOpenDataClient.bestPlaceMatch("Paris").let { paris ->
            SncfOpenDataClient.bestPlaceMatch("Nantes").let { nantes ->
                println(SncfOpenDataClient.journey(paris!!, nantes!!, LocalDateTime.now().plusDays(1)))
            }
        }
    }

    @Test
    fun testPlaces2() {
        SncfOpenDataClient.bestPlaceMatch("Versailles").let { versailles ->
            SncfOpenDataClient.bestPlaceMatch("Nantes").let { nantes ->
                println(SncfOpenDataClient.journey(versailles!!, nantes!!, LocalDateTime.now().plusDays(1))
                    .map { it.copy(sections = it.sections.filter { it.type == "public_transport" }) })
            }
        }
    }

    @Test
    fun testDepartures() {
        SncfOpenDataClient.bestPlaceMatch("Paris Saint-Lazard").let { stLazard ->
            println(SncfOpenDataClient.departures(stLazard!!, LocalDateTime.now().plusMinutes(1)))
        }
    }
}