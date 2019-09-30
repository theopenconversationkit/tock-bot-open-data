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

package ai.tock.bot.open.data.story

import ai.tock.bot.engine.dialog.setTo
import ai.tock.bot.open.data.SecondaryIntent.indicate_location
import ai.tock.bot.open.data.client.sncf.model.Coordinates
import ai.tock.bot.open.data.client.sncf.model.PlaceValue
import ai.tock.bot.open.data.client.sncf.model.SncfPlace
import ai.tock.bot.open.data.locationEntity
import org.junit.jupiter.api.Test
import java.util.Locale

/**
 *
 */
class SearchTest : BaseTest() {

    private val lille = PlaceValue(
            SncfPlace(
                    "stop_area",
                    90,
                    "Lille Europe",
                    "Lille Europe (Lille)",
                    "stop_area:OCE:SA:87223263",
                    Coordinates(50.638861, 3.075774)
            )
    )
    private val paris = PlaceValue(
            SncfPlace(
                    "administrative_region",
                    90,
                    "Paris",
                    "Paris (75001-75116)",
                    "admin:fr:75056",
                    Coordinates(48.856609, 2.351499)
            )
    )

    @Test
    fun `search story asks for destination WHEN there is no destination in context`() {
        send(intent = search) {
            firstAnswer.assertText("For which destination?")
        }
    }

    @Test
    fun `search story asks for origin WHEN there is a destination BUT no origin in context`() {
        send("I would like to find a train", search) {
            firstAnswer.assertText("For which destination?")
        }
        send("Lille", indicate_location, locationEntity setTo lille) {
            firstBusAnswer.assertText("For which origin?")
        }
        send("Paris", indicate_location, locationEntity setTo paris) {
            firstBusAnswer.assertText("When?")
        }
    }

    @Test
    fun `search story asks for destination WHEN there is no destination in context AND locale is fr`() {
        send(intent = search, locale = Locale.FRENCH) {
            firstAnswer.assertText("Pour quelle destination?")
        }
    }

    @Test
    fun `search story asks for origin WHEN there is a destination BUT no origin in context AND locale is fr`() {
        send("Je voudrais rechercher un itinéraire", search, locale = Locale.FRENCH) {
            firstAnswer.assertText("Pour quelle destination?")
        }
        send("Lille", indicate_location, locationEntity setTo lille) {
            firstBusAnswer.assertText("Pour quelle origine?")
        }
        send("Paris", indicate_location, locationEntity setTo paris) {
            firstBusAnswer.assertText("Quand souhaitez-vous partir?")
        }
    }

    @Test
    fun `search story asks for departure date WHEN there is a destination AND an origin BUT no departure date in context`() {

        newRequest("Recherche", search, locale = Locale.FRENCH) {
            destination = lille
            origin = paris

            run()

            firstAnswer.assertText("Quand souhaitez-vous partir?")
        }
    }

}