package fr.vsct.tock.bot.open.data.connector

import fr.vsct.tock.bot.connector.Connector
import fr.vsct.tock.bot.connector.ConnectorConfiguration
import fr.vsct.tock.bot.connector.ConnectorProvider
import fr.vsct.tock.bot.connector.ConnectorType
import fr.vsct.tock.bot.connector.ConnectorTypeConfiguration
import fr.vsct.tock.shared.resourceAsString

internal object SampleRestConnectorProvider : ConnectorProvider {

    override val connectorType: ConnectorType get() = sampleRestConnectorType

    override fun connector(connectorConfiguration: ConnectorConfiguration): Connector {
        with(connectorConfiguration) {
            return SampleRestConnector(
                connectorId,
                path
            )
        }
    }

    override fun configuration(): ConnectorTypeConfiguration =
        ConnectorTypeConfiguration(
            sampleRestConnectorType,
            svgIcon = resourceAsString("/sampleConnector.svg")
        )
}

//used in file META-INF/services/fr.vsct.tock.bot.connector.ConnectorProvider
internal class SampleRestConnectorProviderService : ConnectorProvider by SampleRestConnectorProvider