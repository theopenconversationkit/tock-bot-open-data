This connector should be registered both in bot classpath AND admin classpath.

1) Run the docker stack where everything is already configured:

```
docker-compose -f docker-compose-bot-open-data.yml up
```

2) Add a sample connector in admin interface with /test relative path.

3) Send text: `curl localhost:8080/test --data '{"query":"Hello","userId":"id"}'`

response:
```
{
  "responses": [
    {
      "text": "Welcome to the Tock Open Data Bot! :)"
    },
    {
      "text": "This is a Tock framework demonstration bot: https://github.com/theopenconversationkit/tock"
    },
    {
      "text": "Hey!",
      "buttons": [
        {
          "title": "Itineraries",
          "payload": "search?_previous_intent=greetings"
        },
        {
          "title": "Departures",
          "payload": "departures?_previous_intent=greetings"
        },
        {
          "title": "Arrivals",
          "payload": "arrivals?_previous_intent=greetings"
        }
      ]
    }
  ]
}
```

4) Select a button: `curl localhost:8080/test --data '{"payload":"search?_previous_intent=greetings","userId":"id"}'`
```
{
  "responses": [
    {
      "text": "For which destination?"
    }
  ]
}
```