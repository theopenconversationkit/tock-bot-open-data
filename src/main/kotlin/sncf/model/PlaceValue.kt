package ai.tock.bot.open.data.client.sncf.model

import ai.tock.nlp.entity.Value

/**
 *
 */
data class PlaceValue(private val place: SncfPlace) : Place by place, Value {

    override fun toString(): String {
        return place.toString()
    }
}