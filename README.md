<p align="center">
<img src="https://github.com/joglr/bong-maps/blob/master/src/main/resources/bong/views/bongIcon.png" alt="Bong Maps logo" width="200" height="200">
</p>

# Bong Maps

Bong Maps is a [GIS](https://en.wikipedia.org/wiki/Geographic_information_system) application capable of displaying data from [OpenStreetMap](https://www.openstreetmap.org/) in the formats ZIP and OSM.

This project was developed in 10 weeks during spring 2020 as a school project by a 5-person team.

### Brief

>In this project, you must design and implement a system for visualizing and working with map data in the OpenStreetMap .OSM format.

Requirements included: route finding, address search and a lag-free experience achieved by use of the datastructure KD-Tree.

### Result

<img src="https://user-images.githubusercontent.com/34659757/82275818-f77ab100-9983-11ea-92ab-817c50b22f6f.png" alt="Bong Maps logo">

## Running the application
Download the [latest release](https://github.com/joglr/bong-maps/releases/latest) as a .jar file. To run the application, this file should be executed by opening it directly.

It is also possible to run it from the command line like so:

```
java -jar bong.jar
```

Running the application using the command line exposes useful logging for development purposes.

## Development
This project was initialized using [gradle](https://gradle.org/). Common tasks can be run through this tool. The tool is provided with the source code, so no installation is required. Only a compatible JDK is  required. All commands are using the already provided [gradle wrapper](https://github.com/joglr/bong-maps/blob/master/gradlew).

### Dev Tools
The dev tools provide access to advanced features, not meant for the end user. Find the devtools in More > Devtools. Some of the dev tools provide useful logging, that only is exposed when the application is run from the command line or from an IDE.

### Points of iterest
Points of interest are saved locally, meaning that the first time you start the program, there won't be any. Also this means that if you open the program on a different computer, the points of interest won't be there. Running the tests will clear the points of interest.

### Running the application directly from source files

With the source files downloaded, run the following command in the root of the project.

```
gradlew run
```

### Building an executable jar from source

```
gradlew jar
```

### Running the generated executable `.jar`
Assuming an executable `.jar` file is located at the default location, run the following command at the project root:

```
java -jar build\libs\bong.jar
```

### Running test suite

The project includes several automated tests, that can be run by using the following command:
```
gradlew test
```

To generate a test coverage report using [JaCoCo](https://www.eclemma.org/jacoco/), run `gradlew test` and then run the following command:

```
gradlew jacocoTestReport
```

The report is viewed by opening the generated `build\reports\jacoco\test\html\index.html` in your favorite browser.
