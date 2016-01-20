# Bomberman Rebuild

I'm currently rebuilding bomberman from the ground up. When I'm finished,
bomberman should have a much cleaner and better api which will (hopefully)
result is a much less buggy plugin.

In the rebuild, I am also thinking greatly about extensibility and I am
working towards letting other users build plugins for bomberman :)

While I'm rebuilding the plugin, I won't be updating the main bomberman branch
(except for bug fixes).

## Building

To build bomberman, run

<code>gradle build</code>

Gradle is also configured to let you automatically copy the generated jar to your sever:

<code>gradle install -PserverLocation=/path/to/server</code>

To work with Intellij, running <code>gradle idea</code> should correctly create an idea project.