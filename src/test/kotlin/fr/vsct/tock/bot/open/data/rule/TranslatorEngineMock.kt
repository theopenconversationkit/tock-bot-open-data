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

package fr.vsct.tock.bot.open.data.rule

import fr.vsct.tock.translator.TranslatorEngine
import java.util.Locale

/**
 * Used to test French labels.
 */
object TranslatorEngineMock : TranslatorEngine {

    override fun translate(text: String, source: Locale, target: Locale): String {
        return when (target) {
            Locale.FRENCH ->
                when (text) {
                    "Welcome to the Tock Open Data Bot! :)" -> "Bienvenue chez le Bot Open Data Sncf! :)"
                    "This is a Tock framework demonstration bot: https://github.com/voyages-sncf-technologies/tock" -> "Il s'agit d'un bot de démonstration du framework Tock : https://github.com/voyages-sncf-technologies/tock"
                    "The bot is very limited, but ask him a route or the next departures from a station in France, and see the result! :)" -> "Il est volontairement très limité, mais demandez lui un itinéraire ou les départs à partir d'une gare et constatez le résultat! :)"
                    "Itineraries" -> "Itinéraires"
                    "Departures" -> "Départs"
                    "Arrivals" -> "Arrivées"
                    "From {0} to {1}" -> "De {0} à {1}"
                    "Departure on {0}" -> "Départ le {0}"
                    "Departure {0}" -> "Départ {0}"
                    "Sorry, no routes found :(" -> "Désolé, aucun itinéraire trouvé :("
                    "Here is the first proposal:" -> "Voici la première proposition :"
                    "Departure at {0}, arrival at {1}" -> "Départ à {0}, arrivée à {1}"
                    "For which destination?" -> "Pour quelle destination?"
                    "For which origin?" -> "Pour quelle origine?"
                    "When?" -> "Quand souhaitez-vous partir?"
                    "No proposal to choose. :(" -> "Aucune proposition à choisir. :("
                    "I do not find this proposal. :(" -> "Je ne trouve pas cette proposition. :("
                    "Trip not found" -> "Trajet non trouvé"
                    "Oops, no more results, sorry :(" -> "Oups, plus de résultats, désolé :("
                    "station" -> "gare"
                    "From which station would you like to see the arrivals?" -> "De quelle gare souhaitez vous voir les arrivées?"
                    "Arrivals at {0} train station:" -> "Arrivées à la gare de {0} :"
                    "Arrivals at {0}:" -> "Arrivée à {0} :"
                    "Arrival at {0}" -> "Arrivée {0}"
                    "Oops, no arrival currently found, sorry :(" -> "Oups, aucune arrivée trouvée actuellement, désolé :("
                    "Next arrivals" -> "Arrivées suivantes"
                    "Arrival {0}" -> "Arrivée {0}"
                    "From which station would you like to see the departures?" -> "De quelle gare souhaitez vous voir les départs?"
                    "Departures from: {0}" -> "Départs de la gare de {0} :"
                    "Oops, no departure currently found, sorry :(" -> "Oups, aucun départ trouvé actuellement, désolé :("
                    "Next departures" -> "Départs suivants"
                    else -> text
                }
            else -> text
        }
    }
}