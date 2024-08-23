# Ktor Live Proof of Concept

This is a very rough proof of concept for creating something akin to:
- Phoenix LiveView
- Laravel LiveWire
- Symfony LiveComponents

## Why?

Having spent a lot of time in projects with SPA's and separate backends I've come to learn that a lot of complexity is
in the communication from the backend to the frontend. In a _lot_ of cases the frontend is just a dumb client that
gets data from the backend and renders it. The backend is the one that has all the business logic. 

Other more traditional frameworks like Laravel, Symfony, Django, Ruby on Rails already do this for decades and still
deliver a great developer experience. However, they saw the need to add more interactivity to their applications and
added things like LiveWire, LiveComponents, TurboLinks, etc.

Besides that, there are other libraries like Hotwired Turbo, Hotwired Streams and HTMX that use a more conservative
(and therefore much more simpler to understand!) approach to adding interactivity to web applications.

This is a proof of concept to see if we can do something similar with Ktor and Kotlin.

## What is this?

- Composable HTML on the server
- Initial render on the server
- Real-time updates to the client

## Why would you want this?

- No need to write JavaScript
- No need to write API endpoints
- No need to write client-side state management
- All business logic on the backend
- Very minimal JS code needed

## How does it work?

- Upon receiving the first request a `molecule` `StateFlow` (https://github.com/cashapp/molecule) is created that renders HTML
- The server sends HTML to the client
  - This contains 2 JS scripts
    - The `idiomorph` library to merge the server-rendered HTML with the client-rendered HTML
    - The `live` script to startup the websocket and attach the handlers
- The client renders the HTML and starts the websocket connection
- The websocket connection will send the `molecule` `StateFlow` updates to the client
- The client will update the HTML with the new data using `idiomorph`

## How to run
- `./gradlew run`
- Open `http://localhost:8080/` in your browser
- Open `http://localhost:8080/` in another browser window
- Fun!

## What's next?
- `molecule` now outputs rendered HTML. This is fully sent to the client. This is not ideal. We should only send the 
   diff and merge that in the frontend. The HTML library is pretty flexible and using a different `TagConsumer` one 
   might be able to output a diff, or at least something that is diffable. Another option is to actually go full Compose
   and implement a new `Applier`. This would be a lot of work.
- The `live` script is very basic. It should be able to handle more than just clicks.
- No idea on how to do forms yet, although that can actually be done with basic POST requests and intercepting them with 
  the `live` script.

## And now?
I'm not sure. I think this is a very interesting concept and I would like to explore it further. Any help is appreciated!
  
