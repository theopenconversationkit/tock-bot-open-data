package fr.vsct.tock.bot.open.data.client.sncf.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 */
class SncfPlace(
    @JsonProperty("embedded_type")
    override val embeddedType: String,
    override val quality: Int,
    override val name: String,
    override val label: String?,
    override val id: String,
    @JsonProperty("coord")
    override val coordinates: Coordinates?
) : Place {

    override fun toString(): String {
        return name
    }
}