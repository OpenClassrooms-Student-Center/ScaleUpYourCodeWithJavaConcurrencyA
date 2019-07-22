# PlanetBrain Temperature Analyzer

This project presents an example from the OpenClassrooms Java 
Concurrency course, demonstrating multi-process solutions.

This project analyses open data from Nasa's Kepler project to
present mean temperatures warmer and cooler than Earth's. 

## Note to students on the JDK
This project will require you to install JDK 11. The distribution provided by Oracle or 
the OpenJDK project will suffice.


## Running with gradle
To run the single process program with gradle:
*_This uses the PlanetTemperatureAnalyzer main class, which may also be run explicitly._)
```shell
./gradlew runSingleProcess
```


To run the multi process program with gradle:
(_This uses the PlanetTemperatureAnalyzerParallel main class, which may also be run explicitly._)
```shell
./gradlew runMultiProcess
```


NOTE: On windows you should use `gradlew.bat`

## Data Files

The following files are used:
* src/main/resources/all-planets.csv
   
  This is based on an extract from the Kepler mission (but with intentional duplicates to increase processing times).
  Used by the runSingleProcess command.
  
* src/main/resources/first-55820.csv and last-55820.csv
  
  These two files each contain half of the previous file.
  
These are both based on open data exports, but have been augmented for this exercise.

You'll also find the following files which you can use to slice into your data:
* cool-planets.csv - just those planets below 288 Kelvins.
* hot-planets.csv - just those planets above 288 Kelvins.
