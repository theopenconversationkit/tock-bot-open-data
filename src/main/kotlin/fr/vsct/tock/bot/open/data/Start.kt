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
import fr.vsct.tock.bot.importNlpDump
import fr.vsct.tock.bot.installBots
import fr.vsct.tock.bot.installBotsAndAdminConnectors
import fr.vsct.tock.bot.open.data.entity.PlaceValue
import fr.vsct.tock.bot.registerBot
import fr.vsct.tock.nlp.entity.ValueResolverRepository

fun main(args: Array<String>) {
    Start.start()
}

/**
 *
 */
object Start {

    fun start() {
        ValueResolverRepository.registerType(PlaceValue::class)
        //det default locale to fr
        System.setProperty("tock_default_locale", "fr")

        with(OpenDataConfiguration) {
            addMessengerConnector(
                    pageId,
                    pageToken,
                    applicationSecret,
                    webhookVerifyToken,
                    name = "bot-open-data")
        }
        registerBot(OpenDataBotDefinition)

        installBotsAndAdminConnectors()

        importNlpDump("/bot_open_data.json")
    }
}