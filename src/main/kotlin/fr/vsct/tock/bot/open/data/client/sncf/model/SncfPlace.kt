package fr.vsct.tock.bot.open.data.client.sncf.model

/**
 *
 */
class SncfPlace(
    override val embeddedType: String,
    override val quality: Int,
    override val name: String,
    override val label: String?,
    override val id: String,
    override val coordinates: Coordinates?
) : Place {

    override fun toString(): String {
        return name
    }
}