package ai.tock.bot.open.data.connector

import ai.tock.bot.connector.Connector
import ai.tock.bot.connector.ConnectorConfiguration
import ai.tock.bot.connector.ConnectorProvider
import ai.tock.bot.connector.ConnectorType
import ai.tock.bot.connector.ConnectorTypeConfiguration
import ai.tock.shared.resourceAsString

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

//used in file META-INF/services/ai.tock.bot.connector.ConnectorProvider
internal class SampleRestConnectorProviderService : ConnectorProvider by SampleRestConnectorProvider