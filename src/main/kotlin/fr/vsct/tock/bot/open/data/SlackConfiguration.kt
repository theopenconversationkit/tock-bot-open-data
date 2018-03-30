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

package fr.vsct.tock.bot.open.data

import fr.vsct.tock.bot.connector.slack.addSlackConnector
import fr.vsct.tock.shared.property

/**
 *
 */
object SlackConfiguration {

    /**
     * The slack token1.
     */
    private val token1: String = property("tock_bot_open_data_slack_token1", "Please specify token1 for slack")

    /**
     * The slack token2.
     */
    private val token2: String = property("tock_bot_open_data_slack_token2", "Please specify token2 for slack")

    /**
     * The slack token3.
     */
    private val token3: String = property("tock_bot_open_data_slack_token3", "Please specify token3 for slack")


    fun registerSlackConnector() {
        openBot.addSlackConnector(token1, token2, token3)
    }
}