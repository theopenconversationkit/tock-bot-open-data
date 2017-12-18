[![Build Status](https://travis-ci.org/voyages-sncf-technologies/tock-bot-open-data.png)](https://travis-ci.org/voyages-sncf-technologies/tock-bot-open-data)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/fr.vsct.tock/tock-bot-open-data/badge.svg)](https://maven-badges.herokuapp.com/maven-central/fr.vsct.tock/tock-bot-open-data)

Example of chatbot using [Tock](https://github.com/voyages-sncf-technologies/tock) and open data APIs.

# How to start

Two options available:

## Use a complete docker installation

The setup is explained in the [Tock docker project](https://github.com/voyages-sncf-technologies/tock-docker#user-content-run-the-open-data-bot-example) 

## Or run the code in the IDE

* Start the docker-compose file for the NLP stack (explained [here](https://github.com/voyages-sncf-technologies/tock-docker#user-content-docker-images-for-tock))

* You will need a (free) [SNCF Open Data key](https://data.sncf.com/) 

* To setup messenger (optional), you need a Messenger application with "messages" and "messaging_postbacks" webhook events activated (look at the [Facebook documentation](https://developers.facebook.com/docs/messenger-platform/guides/quick-start)). 

    * Set the right environment variables (see [OpenDataConfiguration](https://github.com/voyages-sncf-technologies/tock-bot-open-data/blob/master/src/main/kotlin/fr/vsct/tock/bot/open/data/OpenDataConfiguration.kt#L29))

    * Also a secure ssl tunnel (for example [ngrok](https://ngrok.com/)) is required to test the bot directly on your desktop:

        ```sh 
            ngrok http 8080
        ``` 

    * Then run the fr.vsct.tock.bot.open.data.Start.kt class

    * Take the ngrok value (ie  https://xxxx.ngrok.io ) and use it in the webhook interface of messenger settings, to specify :
        * the url : https://xxxx.ngrok.io/messenger
        * the verify token you set in tock_bot_open_data_webhook_verify_token env var
        
* To setup Google Assistant (optional), you need a google account and a google actions project with Actions sdk setup - see https://developers.google.com/actions/sdk/create-a-project
      
     * A sample action project configuration is available here : [google_actions_fr.json](https://raw.githubusercontent.com/voyages-sncf-technologies/tock-bot-open-data/master/src/main/resources/google_actions_fr.json) - you will need to set the ngrock host inside (see below)      
      
     * Also a secure ssl tunnel (for example [ngrok](https://ngrok.com/)) is required to test the bot directly on your desktop:
      
              ```sh 
                  ngrok http 8080
              ```     

* In the end you have to launch the class fr.vsct.tock.bot.open.data.StartKt. The bot is up! :)              