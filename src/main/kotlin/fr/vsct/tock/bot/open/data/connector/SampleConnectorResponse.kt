package fr.vsct.tock.bot.open.data.connector

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import fr.vsct.tock.bot.connector.ConnectorMessage
import fr.vsct.tock.bot.connector.ConnectorType

data class SampleButton(val title: String, val payload: String? = null)

@JsonInclude(NON_EMPTY)
data class SampleMessage(val text: String, val buttons: List<SampleButton> = emptyList()) : ConnectorMessage {
    @get:JsonIgnore
    override val connectorType: ConnectorType = sampleRestConnectorType
}

internal data class SampleConnectorResponse(val responses: List<SampleMessage>)