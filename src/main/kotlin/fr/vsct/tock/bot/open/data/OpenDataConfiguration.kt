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

import fr.vsct.tock.shared.property

/**
 *
 */
object OpenDataConfiguration {

    /**
     * Sncf open data api user
     */
    val sncfApiUser = property("tock_bot_open_data_sncf_api_user", "Please specify sncf open data api user")

    /**
     * Wikipedia image :)
     */
    val trainImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/France_road_sign_ID12a.svg/238px-France_road_sign_ID12a.svg.png"

    /**
     * Wikipedia image :)
     */
    val stationImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/85/%C3%89bauche_gare.svg/200px-%C3%89bauche_gare.svg.png"

}