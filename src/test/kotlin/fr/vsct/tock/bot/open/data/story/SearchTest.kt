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

import fr.vsct.tock.bot.open.data.SecondaryIntent.indicate_location
import fr.vsct.tock.bot.open.data.client.sncf.model.Coordinates
import fr.vsct.tock.bot.open.data.client.sncf.model.Place
import fr.vsct.tock.bot.open.data.rule.OpenDataRule
import org.junit.Rule
import org.junit.Test
import java.util.Locale

/**
 *
 */
class SearchTest {

    @Rule
    @JvmField
    val rule = OpenDataRule()

    val mockedDestination = Place(
            "stop_area",
            90,
            "Lille Europe",
            "Lille Europe (Lille)",
            "stop_area:OCE:SA:87223263",
            Coordinates(50.638861, 3.075774))

    val mockedOrigin = Place(
            "stop_area",
            90,
            "Lille Europe",
            "Lille Europe (Lille)",
            "stop_area:OCE:SA:87223263",
            Coordinates(50.638861, 3.075774))

    @Test
    fun `search story asks for destination WHEN there is no destination in context`() {
        with(rule.startNewBusMock(story = search, locale = Locale.FRENCH)) {
            firstAnswer.assertText("Pour quelle destination?")
        }
    }

    @Test
    fun `search story asks for origin WHEN there is a destination but no origin in context`() {
        with(rule.startNewBusMock(story = search, locale = Locale.FRENCH)) {
            firstAnswer.assertText("Pour quelle destination?")
            destination = mockedDestination
        }
        with(rule.startBusMock()) {
            firstBusAnswer.assertText("Pour quelle origine?")
            origin = mockedOrigin
        }
        with(rule.startBusMock()) {
            firstBusAnswer.assertText("Quand souhaitez-vous partir?")
        }
    }

    @Test
    fun `search story asks for departure date WHEN there is a destination and an origin but no departure date in context`() {

        with(rule.newBusMock(story = search, locale = Locale.FRENCH)) {
            destination = mockedDestination
            intent = indicate_location
            location = mockedOrigin

            run()

            firstAnswer.assertText("Quand souhaitez-vous partir?")
        }
    }

}