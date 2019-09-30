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

package ai.tock.bot.open.data

import ai.tock.bot.definition.IntentDef
import ai.tock.bot.definition.bot
import ai.tock.bot.open.data.story.arrivals
import ai.tock.bot.open.data.story.askIdentity
import ai.tock.bot.open.data.story.changeLanguage
import ai.tock.bot.open.data.story.departures
import ai.tock.bot.open.data.story.greetings
import ai.tock.bot.open.data.story.search

/**
 * The bot definition.
 */
val openBot = bot(
    "bot_open_data",
    stories =
    listOf(
        greetings,
        departures,
        arrivals,
        search,
        changeLanguage,
        askIdentity
    ),
    hello = greetings
)

/**
 * Secondary intents supported by the bot.
 */
enum class SecondaryIntent : IntentDef {

    /**
     * Simple location.
     */
    indicate_location,
    /**
     * Location with origin role specified.
     */
    indicate_origin,
    /**
     * next elements.
     */
    more_elements,
    /**
     * select intent.
     */
    select,
    /**
     * cancel intent.
     */
    cancel
}

/**
 * The shared entities (used in more than one story).
 */
val originEntity = openBot.entity("location", "origin")
val destinationEntity = openBot.entity("location", "destination")
val locationEntity = openBot.entity("location")
val departureDateEntity = openBot.entity("duckling:datetime", "departure_date")







