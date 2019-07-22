# PlanetBrain Temperature Analyzer

This project presents an example from the OpenClassrooms Java 
Concurrency course, demonstrating multi-process solutions.

This projet analyses open data from Nasa's Kepler project to
present mean temperatures warmer and cooler than Earth's. 

## Running with gradle
To run the single process program with gradle:
*_This uses the PlanetTemperatureAnalyzer main class._)
```shell
./gradlew runSingleProcess
```


To run the multi process program with gradle:
(_This uses the PlanetTemperatureAnalyzerParallel main class._)
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

You'll also find cool-planets.csv and hot-planets.csv which you can use to slice your data further.
