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
import fr.vsct.tock.bot.open.data.rule.OpenDataRule
import org.junit.Rule
import org.junit.Test
import java.util.Locale

/**
 *
 */
class GreetingsTest {

    @Rule
    @JvmField
    val rule = OpenDataRule()

    @Test
    fun `greetings story displays welcome message WHEN locale is fr`() {
        with(rule.startNewBusMock(locale = Locale.FRENCH)) {
            firstAnswer.assertText("Bienvenue chez le Bot Open Data Sncf! :)")
            secondAnswer.assertText("Il s'agit d'un bot de démonstration du framework Tock : https://github.com/voyages-sncf-technologies/tock")
        }
    }

    @Test
    fun `greetings story displays welcome message with Messenger dedicated message WHEN context contains Messenger connector and fr locale`() {
        with(rule.startNewBusMock(locale = Locale.FRENCH)) {
            firstAnswer.assertText("Bienvenue chez le Bot Open Data Sncf! :)")
            secondAnswer.assertText("Il s'agit d'un bot de démonstration du framework Tock : https://github.com/voyages-sncf-technologies/tock")
            lastAnswer.assertMessage(
                    buttonsTemplate(
                            "Il est volontairement très limité, mais demandez lui un itinéraire ou les départs à partir d'une gare et constatez le résultat! :)",
                            postbackButton("Itinéraires", search),
                            postbackButton("Départs", Departures),
                            postbackButton("Arrivées", Arrivals)
                    )
            )
        }
    }

    @Test
    fun `greetings story displays welcome message with GA dedicated message WHEN context contains GA connector and fr locale`() {
        with(rule.startNewBusMock(connectorType = gaConnectorType, locale = Locale.FRENCH)) {
            firstAnswer.assertText("Bienvenue chez le Bot Open Data Sncf! :)")
            secondAnswer.assertText("Il s'agit d'un bot de démonstration du framework Tock : https://github.com/voyages-sncf-technologies/tock")
            lastAnswer.assertMessage(
                    gaMessage(
                            "Il est volontairement très limité, mais demandez lui un itinéraire ou les départs à partir d'une gare et constatez le résultat! :)",
                            "Itinéraires",
                            "Départs",
                            "Arrivées")
            )
        }
    }
}