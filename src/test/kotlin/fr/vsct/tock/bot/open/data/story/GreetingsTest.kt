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

import fr.vsct.tock.bot.connector.ga.gaConnectorType
import fr.vsct.tock.bot.connector.ga.gaMessage
import fr.vsct.tock.bot.connector.messenger.buttonsTemplate
import fr.vsct.tock.bot.connector.messenger.postbackButton
import fr.vsct.tock.bot.open.data.openBot
import fr.vsct.tock.bot.test.startMock
import org.junit.Test

/**
 *
 */
class GreetingsTest {

    @Test
    fun greetings_shouldDisplayWelcomeMessage() {
        val bus = openBot.startMock()

        bus.firstAnswer.assertText("Bienvenue chez le Bot Open Data Sncf! :)")
        bus.secondAnswer.assertText("Il s'agit d'un bot de démonstration du framework Tock : https://github.com/voyages-sncf-technologies/tock")
    }

    @Test
    fun greetings_shouldDisplayWelcomeMessageWithMessengerDedicatedMessage_whenMessengerConnectorTypeIsUsed() {
        val bus = openBot.startMock()

        with(bus) {
            firstAnswer.assertText("Bienvenue chez le Bot Open Data Sncf! :)")
            secondAnswer.assertText("Il s'agit d'un bot de démonstration du framework Tock : https://github.com/voyages-sncf-technologies/tock")
            lastAnswer.assertMessage(
                    buttonsTemplate(
                            "Il est volontairement très limité, mais demandez lui un itinéraire ou les départs à partir d'une gare et constatez le résultat! :) ",
                            postbackButton("Itinéraires", search),
                            postbackButton("Départs", Departures),
                            postbackButton("Arrivées", Arrivals)
                    )
            )
        }
    }

    @Test
    fun greetings_shouldDisplayWelcomeMessageWithGaDedicatedMessage_whenGaConnectorTypeIsUsed() {
        val bus = openBot.startMock(connectorType = gaConnectorType)

        with(bus) {
            firstAnswer.assertText("Bienvenue chez le Bot Open Data Sncf! :)")
            secondAnswer.assertText("Il s'agit d'un bot de démonstration du framework Tock : https://github.com/voyages-sncf-technologies/tock")
            lastAnswer.assertMessage(
                    gaMessage(
                            "Il est volontairement très limité, mais demandez lui un itinéraire ou les départs à partir d'une gare et constatez le résultat! :) ",
                            "Itinéraires",
                            "Départs",
                            "Arrivées")
            )
        }
    }
}