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

import fr.vsct.tock.bot.connector.messenger.addMessengerConnector
import fr.vsct.tock.shared.property

/**
 * Messenger configuration.
 */
object MessengerConfiguration {

    /**
     * The facebook page id.
     */
    private val pageId: String = property("tock_bot_open_data_page_id", "Please specify facebook page id")

    /**
     * The messenger page token.
     */
    private val pageToken: String = property("tock_bot_open_data_page_token", "Please specify facebook page token")

    /**
     * The messenger application secret key.
     */
    private val applicationSecret: String =
        property("tock_bot_open_data_application_secret", "Please specify messenger application secret")

    /**
     * The webhook verify token.
     */
    private val webhookVerifyToken: String =
        property("tock_bot_open_data_webhook_verify_token", "Please specify messenger webhook verify token")

    fun registerMessengerConnector() {
        openBot.addMessengerConnector(
            pageId,
            pageToken,
            applicationSecret,
            webhookVerifyToken
        )
    }
}