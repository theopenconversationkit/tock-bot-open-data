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

import ai.tock.bot.definition.StoryDefinition
import ai.tock.bot.definition.story
import ai.tock.bot.engine.BotBus
import ai.tock.bot.engine.dialog.NextUserActionState
import ai.tock.bot.open.data.SecondaryIntent.cancel
import ai.tock.bot.open.data.openBot

private val firstNameEntity get() = openBot.entity("first_name")
private val lastNameEntity get() = openBot.entity("last_name")

private var BotBus.firstName: String?
    get() = entityText(firstNameEntity)
    set(v) = changeEntityText(firstNameEntity, v)
private var BotBus.lastName: String?
    get() = entityText(lastNameEntity)
    set(v) = changeEntityText(lastNameEntity, v)

/**
 * The identity handler.
 */
val askIdentity: StoryDefinition by lazy {
    story("ask_identity", secondaryIntents = setOf(cancel)) {

        if (isIntent(cancel)) {
            end("Ok by")
        } else {

            //we have not detected entities - ok so we assume the whole test is the entity missing
            if (!hasActionEntity(firstNameEntity) && !hasActionEntity(lastNameEntity)) {
                if (firstName != null && lastName == null) {
                    lastName = userText
                } else {
                    firstName = userText
                }
            }

            //only ask_identity intent or cancel intent allowed for next request
            nextUserActionState = NextUserActionState(
                askIdentity to 0.4,
                cancel to 0.0
            )
            when {
                firstName == null -> end("What is your first name?")
                lastName == null -> end("Ok {0}, what is your last name?", firstName)
                else -> end("Your complete name is {0} {1}. Do you want to change it or quit?", firstName, lastName)
            }
        }

    }
}