[![Build Status](https://travis-ci.org/voyages-sncf-technologies/tock-bot-open-data.png)](https://travis-ci.org/voyages-sncf-technologies/tock-bot-open-data)

# How to start

You have two options:

## Use a complete docker installation

The setup is explained in the [Tock docker project](https://github.com/voyages-sncf-technologies/tock-docker#user-content-open-data-bot-example) 

## Run the code in the IDE

* Start the docker-compose file for the NLP interface (explained [here]([Tock docker project](https://github.com/voyages-sncf-technologies/tock-docker#user-content-nlp-stack)))

* You will need a (free) [SNCF Open Data key](https://data.sncf.com/) and a Messenger application (look at the [Facebook documentation](https://developers.facebook.com/docs/messenger-platform/guides/quick-start)). 

* Set the right environment variables (see [OpenDataConfiguration](https://github.com/voyages-sncf-technologies/tock-bot-open-data/blob/master/src/main/kotlin/fr/vsct/tock/bot/open/data/OpenDataConfiguration.kt))

* Also to test the bot directly on your desktop, a secure ssl tunnel (for example [ngrok](https://ngrok.com/)) is required:

```sh 
    ngrok http 8080
``` 

* Take the ngrok value (ie  https://xxxx.ngrok.io ) and use it in the webhook interface of messenger settings, to specify :
   * the url : https://xxxx.ngrok.io/messenger
   * the verify token you set in tock_bot_open_data_webhook_verify_token env var

* Then run the fr.vsct.tock.bot.open.data.Start.kt class