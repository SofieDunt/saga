# Command-Line USEME

This document contains information about using `saga.jar` and the script commands provided by `PlayerController` 
and `WriterController`.

# saga.jar
This application requires JDK 11. After downloading the jar, navigate to the directory it is in via the command line, 
then run
```
java -jar saga.jar
```
with the following inputs, depending on how you want to interact with the application:  
- play stories by executing a script at a known filePath`: -play -script filePath`  
- play stories interactively via the console: `-play -interactive`  
- write stories by executing a script at a known filePath: `-write -script filePath`  
- write stories interactively via the console: `-write -interactive`  

# Script Commands
Format: The first word in each command is the base command, which should be entered as shown. The base command is 
followed by command specifiers in {}. Specifiers should be entered as desired by the user but must conform to the 
indicated restrictions:

numeric - Input for these specifiers must be numbers
p-numeric - Input for these specifiers must be positive numbers

To have a multi-word specifier, enclose the specifier in quotations (""). Single-word specifiers may also be enclosed in quotations. 
To add quotations as part of the specifier, wrap the quotations in another pair of quotes ("""").

Any invalid inputs will not be accepted by the application; a message will be displayed prompting the user for valid input. 
If a base command has been entered, the model will ask for valid specifiers.

## PlayerController
### `next`
Chooses the first option in the current choice

### `choose {decision}`
Chooses an option in the current choice  

**Specifiers**
- `decision (p-numeric)` the index id of the option to choose

### `play {story}`
Loads a story to play  

**Specifiers**
- `story` the name of the story to load

### `quit`
Quits the currently loaded story

### `remove {story}`
Removes a story from the library

**Specifiers**
- `story` the name of the story to remove

### `restart`
Restarts the story being played  

### `play {story}`
Loads a story to play 

**Specifiers**
- `story` the name of the story to load

### `import {path}`
Imports a story into the library  

**Specifiers**
- `path` the file path of the story to import

### `export {path} {story}`
Exports the original version of the story as a file  

**Specifiers**
- `path` the file path to export the story to
- `story` the name of the story to export

### `exportInProgress {path} {story}`
Exports the story with the user's progress as a file  

**Specifiers**
- `path` the file path to export the story to
- `story` the name of the story to export

## WriterController
### `load {work}`
Loads a work to edit

**Specifiers**
- `work` the name of the work to load

### `quit`
Quits editing the loaded work

### `remove {work}`
Removes a work from the library

**Specifiers**
- `work` the name of the work to remove

### `rename {work} {name}`
Renames a work in the library

**Specifiers**
- `work` the name of the work to rename
- `name` the new name of the work

### `start {name}`
Starts a new work

**Specifiers**
- `name` the name of the work to start

### `setName {name}`
Sets the name of the story being edited

**Specifiers**
- `name` the name to set the story to

### `addStatus {name} {value}`
Adds a status to the work or updates an existing status

**Specifiers**
- `name` the name of the status to add/update
- `value (numeric)` the initial value of the status

### `removeStatus {name}`
Removes a status from the work

**Specifiers**
- `name` the name of the status to remove

### `addChoice`
Adds a choice to the work

### `setInitial {choice}`
Sets the initial choice of the work

**Specifiers**
- `choice (p-numeric)` the index id of the choice to make the initial choice

### `addSimple {description} {choice} {outcome}`
Adds a simple decision option to a choice

**Specifiers**
- `description` the description of the decision
- `choice (p-numeric)` the index id of the choice to add the option to
- `outcome (p-numeric)` the index id of the choice outcome of the decision

### `addConsequential {description} {choice} {outcome} {consequences}`
Adds a consequential decision option to a choice

**Specifiers**
- `description` the description of the decision
- `choice (p-numeric)` the index id of the choice to add the option to
- `outcome (p-numeric)` the index id of the choice outcome of the decision
- `consequences` the consequences of the decision, represented as status updates are exported and separated by commas

### `addSimpleDependent {description} {choice} {dependency} {threshold} {outcomeBelow} {outcomeMeets}`
Adds a simple decision option whose outcome is dependent on a status to a choice

**Specifiers**
- `description` the description of the decision
- `choice (p-numeric)` the index id of the choice to add the option to
- `dependency` the name of the status the outcome is dependent on
- `threshold (numeric)` the threshold to determine the outcome
- `outcomeBelow (p-numeric)` the index id of the choice outcome of the decision if the status value is below the threshold
- `outcome (p-numeric)` the index id of the outcome if the value meets the threshold

### `addConsequentialDependent {description} {choice} {dependency} {threshold} {outcomeBelow} {outcomeMeets} {consequences}`
Adds a consequential decision option whose outcome is dependent on a status to a choice

**Specifiers**
- `description` the description of the decision
- `choice (p-numeric)` the index id of the choice to add the option to
- `dependency` the name of the status the outcome is dependent on
- `threshold (numeric)` the threshold to determine the outcome
- `outcomeBelow (p-numeric)` the index id of the choice outcome of the decision if the status value is below the threshold
- `outcome (p-numeric)` the index id of the outcome if the value meets the threshold
- `consequences` the consequences of the decision, represented as status updates are exported and separated by commas

### `removeDecision {choice} {option}`
Removes an option from a choice

**Specifiers**
- `choice (p-numeric)` the index id of the choice to remove the option from
- `option (p-numeric)` the index id of the option to remove (where the first option of a choice is 1, regardless of the decision id the option is associated with)

### `removeChoice {choice}`
Removes a choice from the work

**Specifiers**
- `choice (p-numeric)` the index id of the choice to remove

### `export {path}`
Exports the work as a story file

**Specifiers**
- `path` the file path to export the work to
