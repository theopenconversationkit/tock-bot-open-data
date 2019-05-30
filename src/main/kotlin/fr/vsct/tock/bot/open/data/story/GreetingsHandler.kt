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

import fr.vsct.tock.bot.connector.ga.gaMessage
import fr.vsct.tock.bot.connector.ga.withGoogleAssistant
import fr.vsct.tock.bot.connector.messenger.buttonsTemplate
import fr.vsct.tock.bot.connector.messenger.postbackButton
import fr.vsct.tock.bot.connector.messenger.withMessenger
import fr.vsct.tock.bot.connector.slack.slackAttachment
import fr.vsct.tock.bot.connector.slack.slackButton
import fr.vsct.tock.bot.connector.slack.slackMessage
import fr.vsct.tock.bot.connector.slack.withSlack
import fr.vsct.tock.bot.definition.story
import fr.vsct.tock.bot.open.data.connector.sampleButton
import fr.vsct.tock.bot.open.data.connector.sampleMessage
import fr.vsct.tock.bot.open.data.connector.withSample

/**
 * The greetings handler.
 */
val greetings = story("greetings") {
    //cleanup state
    resetDialogState()

    send("Welcome to the Tock Open Data Bot! :)")
    send("This is a Tock framework demonstration bot: https://github.com/voyages-sncf-technologies/tock")

    end {
        withMessenger {
            buttonsTemplate(
                "The bot is very limited, but ask him a route or the next departures from a station in France, and see the result! :)",
                postbackButton("Itineraries", search),
                postbackButton("Departures", Departures),
                postbackButton("Arrivals", Arrivals)
            )
        }
        withGoogleAssistant {
            gaMessage(
                "The bot is very limited, but ask him a route or the next departures from a station in France, and see the result! :)",
                "Itineraries",
                "Departures",
                "Arrivals"
            )
        }
        withSlack {
            slackMessage(
                "Hey!",
                slackAttachment(
                    "The bot is very limited, but ask him a route or the next departures from a station in France, and see the result! :)",
                    slackButton("Itineraries", search),
                    slackButton("Departures", Departures),
                    slackButton("Arrivals", Arrivals)
                )
            )
        }
        withSample {
            sampleMessage(
                "Hey!",
                sampleButton("Itineraries", search),
                sampleButton("Departures", Departures),
                sampleButton("Arrivals", Arrivals)
            )
        }
    }
}