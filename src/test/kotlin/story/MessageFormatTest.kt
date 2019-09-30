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

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.Locale
import kotlin.test.assertEquals

/**
 *
 */
class MessageFormatTest {

    @Test
    fun testDatetimeFormat_formatWell_ForFrench() {
        assertEquals("lundi 3 février vers 12:23", MessageFormat.datetimeFormat.provide(Locale.FRENCH).format(LocalDateTime.of(2020, 2, 3, 12, 23)))
    }
}