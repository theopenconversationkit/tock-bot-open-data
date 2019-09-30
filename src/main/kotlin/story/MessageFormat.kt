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

import ai.tock.shared.defaultLocale
import ai.tock.translator.DateTimeFormatterProvider
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * To format departure datetime.
 */
object MessageFormat {

    val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val datetimeFormat = object : DateTimeFormatterProvider {
        override fun provide(locale: Locale): DateTimeFormatter {
            return when (locale) {
                Locale.FRENCH -> DateTimeFormatter.ofPattern("EEEE d MMMM 'vers' H:mm").withLocale(Locale.FRENCH)
                else -> DateTimeFormatter.ofPattern("EEEE, MMMM d, 'around' H:mm").withLocale(defaultLocale)
            }
        }
    }
}