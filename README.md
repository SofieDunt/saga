# Saga
This Java application allows users to

**play interactive stories**
- play through stories, making both *trivial and consequential decisions*

**create interactive stories**
- create interactive stories *without any programming knowledge*

The appplication was built following the MVC design pattern, allowing the model to be used in a variety of ways with 
different controller and view implementations:

## Command Line (`StoryMain.java`)
Allows users to interact with the model via the command line by inputting commands as a stream of user input or as a script.
Uses an `ApplicationController` and a `LibraryApplicationView`.

## Web Application (`StoryApplication.java`)
Allows users to interact with the model via a [web client](https://github.com/SofieDunt/saga-frontend). 
Uses a `StoryApplicationController`.

## Potential Extension
The `StoryApplicationController` interface could also be used to allow users to interact with the model via a GUI built with 
Java Swing/JavaFX.

# Setup
Download or clone this repository and run `mvn clean install`.

# Run
To run the command-line version, run `StoryMain` with the desired arguments (see USEME.md).

To run the web application version, run `./mvnw spring-boot:run` or `StoryApplication`.

# Code Walkthrough
Directly under the java source folder, there are five packages:
- **controller** contains interfaces and classes relevant to the commanding the model and view according to user inpu
  - **command** contains function objects that order the model to execute specific commands
  - **service** contains interfaces and classes specific to the web controller
- **io** contains interfaces and classes relevant to importing/exporting to/from the model
- **model** contains interfaces and classes relevant to the logic of playing/creating stories
- **utils** contains interfaces and classes that may be used for the convenience of multiple classes across all packages
- **view** contains interfaces and classes relevant to displaying information about the model to the user

# Model Concept
The model represents interactive stories as a series of `Choice`s, where each choice presents the user with a set of
`Decision`s, or options to choose from. Each decision has an outcome, or the user's next choice. Simple text, or story events 
that do not require a decision, can be represented by a choice with only one option. The end of the story is represented by 
a choice with no options.

Stories also have statuses, which can be thought of as different point categories. Decisions may update the number of points a 
user has in one or multiple categories. The number of points a user has may impact the outcome of a decision.
