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

import fr.vsct.tock.bot.importI18nDump
import fr.vsct.tock.bot.importNlpDump
import fr.vsct.tock.bot.open.data.GoogleAssistantConfiguration.registerGoogleAssistantConnector
import fr.vsct.tock.bot.open.data.MessengerConfiguration.registerMessengerConnector
import fr.vsct.tock.bot.open.data.entity.PlaceValue
import fr.vsct.tock.bot.registerAndInstallBot
import fr.vsct.tock.nlp.entity.ValueResolverRepository

fun main(args: Array<String>) {
    Start.start()
}

/**
 * This is the entry point of the bot.
 */
object Start {

    fun start() {
        setup()

        registerMessengerConnector()
        registerGoogleAssistantConnector()

        registerAndInstallBot(openBot)

        importNlpDump("/bot_open_data.json")
        importI18nDump("/labels.json")
    }

    private fun setup() {
        //we add a new value type in order to manage open data api place
        ValueResolverRepository.registerType(PlaceValue::class)
        //set default zone id, these are french trains, so...
        System.setProperty("tock_default_zone", "Europe/Paris")
    }
}