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

import fr.vsct.tock.bot.definition.IntentAware
import fr.vsct.tock.bot.definition.IntentAwareBase
import fr.vsct.tock.bot.definition.StoryDefinitionBase
import fr.vsct.tock.bot.definition.StoryHandler
import fr.vsct.tock.bot.definition.StoryStep
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SecondaryIntent.arrivals
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SecondaryIntent.indicate_location
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SecondaryIntent.indicate_origin
import fr.vsct.tock.bot.open.data.OpenDataStoryDefinition.SecondaryIntent.more_elements
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler
import fr.vsct.tock.bot.open.data.story.DeparturesArrivalsStoryHandler.DeparturesArrivalsSteps
import fr.vsct.tock.bot.open.data.story.GreetingsStoryHandler
import fr.vsct.tock.bot.open.data.story.SearchStoryHandler

/**
 * Stories of the bot.
 */
enum class OpenDataStoryDefinition(
        override val storyHandler: StoryHandler,
        override val otherStarterIntents: Set<IntentAware> = emptySet(),
        override val otherIntents: Set<IntentAware> = emptySet(),
        override val stepsArray: Array<out StoryStep> = emptyArray()) : StoryDefinitionBase {

    greetings(GreetingsStoryHandler),

    departures(DeparturesArrivalsStoryHandler,
            setOf(indicate_location, arrivals),
            setOf(indicate_origin, more_elements),
            DeparturesArrivalsSteps.values()
    ),
    
    search(SearchStoryHandler,
            setOf(indicate_origin),
            setOf(indicate_location));

    /**
     * Shared intents
     */
    enum class SecondaryIntent : IntentAwareBase {
        /**
         * arrivals
         */
        arrivals,
        /**
         * Simple location.
         */
        indicate_location,
        /**
         * Location with origin role specified.
         */
        indicate_origin,
        /**
         * for departures
         */
        more_elements;
    }

}