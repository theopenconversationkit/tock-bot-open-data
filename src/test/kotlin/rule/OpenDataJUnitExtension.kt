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

package ai.tock.bot.open.data.rule

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.provider
import ai.tock.bot.open.data.openBot
import ai.tock.bot.test.junit.TockJUnit5Extension
import ai.tock.bot.test.testTranslatorModule
import ai.tock.translator.Translator
import ai.tock.translator.TranslatorEngine

/**
 *
 */
class OpenDataJUnitExtension : TockJUnit5Extension(openBot) {

    init {
        Translator.enabled = true
        testTranslatorModule = Kodein.Module {
            bind<TranslatorEngine>() with provider { TranslatorEngineMock }
        }
    }
}