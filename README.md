# Serializing and Deserializing Json 

This is a rough example of:
* using the Moshi Json library to serialize/deserialize; and 
* using the SparkJava library to run an API server. 
* making HTTP requests to external APIs.

It should contain most of the programmatic reference you need to do Sprint 2. E.g.:
* running an API server;
* integration/system testing an API server via HTTP requests;
* serializing/deserializing classes; and
* serializing/deserializing generic classes;

However, keep in mind that this code isn't _exactly_ what you need for Sprint 2. We expect you to use this as a reference, but not to borrow too heavily (you'll need to work with different types of JSON response, requests, etc.).

Historical note: 0320 had previously used Gson for serializing/deserializing Json, but Gson seems to now be in maintenance mode, and doesn't support records well. So we switched starting in Fall 2022.

## Setup 

You'll need two dependencies for handling Json: `moshi` and `moshi-adapters`, both from `com.squareup.moshi`. You should be able to copy and paste them from the `pom.xml` of this project. Similarly, you'll depend on SparkJava: `spark-core` from `com.sparkjava`. You should be able to use this pom.xml going forward!

## Demo Framing

We're building an application that serves two (disjointed) purposes: a functioning soup restaurant, and an [idea generator](https://www.youtube.com/watch?v=RtRg9BSGNMk&ab_channel=Deadaccount).

The application is an API server that responds to "order" requests. The response is a serialized Soup. In its current form, the response always provides the first soup (if any exist). If no
recipe exists, the server replies with an error response. 

The idea generator works by providing any random idea for something to do today. However, it's a little too unconstrained, so it's your job to narrow down the search a bit!
## Running 

You can run the example by executing the `server.edu.brown.cs.student.main.Server` class `main` method. This starts up a real webserver on your computer. By default, it's set to use port `3232`, so you should be able (while the server is running!) to send requests via `localhost:3232` in your browser. One endpoint is `order`, so `localhost:3232/order` will produce an order result (or an error). The other one is `activity` where `localhost:3232/activity` gives a random activity!

In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

There are also two test classes! Check these out for an idea of a new shape of testing!

## Exercise 

### Code Walk

Take a minute to familiarize yourself with the project. Start at the entry point of the project `Server.java` and explore the different handlers.
### Run and Query

Run the `server.edu.brown.cs.student.main.Server` and confirm that you are able to make web queries from your browser. Visit the endpoints specified in `Server.java`. What do they return right now?

### Making HTTP requests
See places labeled `TODO 1` in `ActivityHandler.java`. 

Try to find the places in the code that correspond to the Architecture Diagram Hunt from the slides. 

Then, once you are familiar with the shape of the HTTP request, see if you can use the parameters of an Activity object and the different endpoints of the BoredAPI to narrow down your search a little bit. Perhaps you could search for activities for 2 participants or ones that are free!

### Handling and manipulating more complex data

See places labeled `TODO 2` in `OrderHandler.java`

Right now, the `order` request produces whichever Soup appears first in the menu list. How would you modify the code so someone could order a soup based on its name? Perhaps start by getting the value of the query parameter given in the class!


### Testing

What other integration tests should have been written in the `edu.brown.cs.student.main.TestSoupAPIHandlers` class?

Add at least one. 

## Additional Info for Sprint 2

You can deserialize a Json object into a Java object with fewer fields. E.g., if you've got a Json object with 26 different fields:

```json
{
  "A": 1,
  "B": 2,
  ...
}
```

You can deserialize this into an object with only `"A"` and `"B"` fields. This is useful when processing very large, verbose response Json from other APIs, but only need a few fields.

## Note about SLF4J Error

If you see the error below appear in your run console, you can feel free to ignore it. It is just warning you to use a more useful error logging tool (of which there are many that are used in industry), but we do not teach that in 32! If you are interested, you can learn more about logging [here](https://www.baeldung.com/java-logging-intro).

`SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.`