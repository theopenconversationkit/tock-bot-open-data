[![Gitter](https://badges.gitter.im/tockchat/Lobby.svg)](https://gitter.im/tockchat/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=body_badge)
[![Build Status](https://travis-ci.com/theopenconversationkit/tock-bot-open-data.png)](https://travis-ci.com/theopenconversationkit/tock-bot-open-data)
[![Maven Central](https://img.shields.io/maven-central/v/ai.tock/tock-bot-open-data.svg)](https://search.maven.org/search?q=tock-bot-open-data)

Example of chatbot using [Tock](https://github.com/theopenconversationkit/tock) and open data APIs.

# How to start

Two options available:

## Use a complete docker installation

The setup is explained in the [Tock docker project](https://github.com/theopenconversationkit/tock-docker#user-content-run-the-open-data-bot-example) 

## Or run the code in the IDE

* Start the docker-compose file for the NLP stack (explained [here](https://github.com/theopenconversationkit/tock-docker#user-content-docker-images-for-tock))

* You will need a (free) [SNCF Open Data key](https://data.sncf.com/) and to set the environment var (see [OpenDataConfiguration](https://github.com/theopenconversationkit/tock-bot-open-data/blob/master/src/main/kotlin/OpenDataConfiguration.kt#L32)).

* To setup messenger (optional), you need a Messenger application with "messages" and "messaging_postbacks" webhook events activated - look at the [Facebook documentation](https://developers.facebook.com/docs/messenger-platform/guides/quick-start) and 
[Tock Messenger Configuration](https://github.com/theopenconversationkit/tock/tree/master/bot/connector-messenger) instructions.
        
* To setup Google Assistant (optional), you need a Google account and a Google Actions project with Actions sdk setup - see [Google Actions configuration](https://developers.google.com/actions/sdk/create-a-project)
and  [Tock Google Assistant configuration](https://github.com/theopenconversationkit/tock/tree/master/bot/connector-ga).

* In the end you have to start the OpenDataBot launcher in IntelliJ. The bot is up! :)              
