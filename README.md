# Bong Maps

Bong Maps is a GIS application capable of displaying data from [OpenStreetMap](https://www.openstreetmap.org/) in the formats ZIP and OSM. 

This project was developed during spring 2020 by @emja, @thhk, @krbh, @frai and @jglr

## Running the application
Download the [latest release](https://github.itu.dk/trbj/BFST20Gruppe21/releases) as a .jar file.

To run the application, this file should be executed, either by opening it directly or running it from the command line like so:

```
java - jar bfst.jar
```

## Development

This project was initialized using [gradle](https://gradle.org/). Common tasks can be run through this tool. The tool is provided with the source code, so no installation is required. Only a compatible JDK is  required. All commands are using the already provided [gradle wrapper](https://github.itu.dk/trbj/BFST20Gruppe21/blob/master/gradlew)

### Running the application directly from source files

With the source files downloaded, run the following command in the root of the project.

```
gradlew run
```

### Build an executable jar from source

```
gradlew jar
```

### Running the generated executable `.jar`
Assuming an executable `.jar` file is located at the default location, run the following command at the project root:

```
java -jar build\libs\bfst.jar
```

### Running test suite

The project includes several automated tests, that can be run by using the following command:
```
gradlew test
```

To generate a test coverage report using [JaCoCo](https://www.eclemma.org/jacoco/), run the following command

```
gradlew jacocoTestReport
```

The report is viewed by opening the generated `build\reports\jacoco\test\html\index.html` in your favorite browser.
