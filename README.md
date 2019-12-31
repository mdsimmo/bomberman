Bomberman
=========

A Bukkit plugin for Minecraft which adds Bomberman.

See more on the bukkit page:
http://dev.bukkit.org/bukkit-plugins/bomberman/

## Licence
This plugin is public domain. You can do what you like with it with no need to give any credit or
add any licences :D

The only exception is that Bomberman uses
[exp4j](http://www.objecthunter.net/exp4j/index.html) for calculations. That software is under the
Apache licence 2.0.

## Building

To build bomberman, run

<code>gradle build</code>

The build.gradle file has been configured to let you automatically copy the generated jar to your sever:

<code>gradle install -PserverLocation=/path/to/server</code>

That line will build the jar, run the tests, delete any old bomberman plugin jar in the server and then
copy the new jar across to the server ready to be loaded.

To work with Intellij, running <code>gradle idea</code> should correctly create an idea project.