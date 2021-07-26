# Web USEME
This document specifies the routes provided by `ServiceController`.

### `GET /player/current/story`
Gets the loaded story.

**Responses**  
`200 OK`
```
{
  "name": STRING,
  "statuses": [
  {
    "name": STRING,
    "value": INT
  },
  ...
  ],
  "choices": [
  {
    "id": INT,
    "options": [
    {
      "id": INT,
      "decision": INT
    },
    ...
    ]
  },
  ...
  ],
  "decisions": [
  {
    "id": INT,
    "type": DecisionTypes,
    "description": STRING,
    "dependency": STRING,
    "threshold": INT,
    "outcome1Id": INT,
    "outcome2Id": INT,
    "consequences": [
    {
      "type": StatusUpdateTypes,
      "var": INT,
      "status": STRING
    },
    ...
    ]
  },
  ...
  ],
  "choice": INT
}
```
or `NULL` if no story is loaded

### `GET /player/current/name`
Gets the name of the loaded story.

**Responses**  
`200 OK`
```
STRING
```
or `NULL` if no story is loaded

### `GET /player/current/choice`
Gets the string representation of the current choice.

**Responses**  
`200 OK`
```
STRING
```
or `NULL` if no story is loaded

### `GET /player/stories`
Gets the names of all stories in the player library.

**Responses**  
`200 OK`
```
STRING-LIST
```

### `POST /player/export`
Exports the un-started version of a story.

**Params**  
`path: STRING`  
The path to export to  
`name: STRING`  
The name of the story to export

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the path can't be exported to, an IO error occurs, or the story doesn't exist


### `POST /player/export-in-progress`
Exports the in-progress version of a story.

**Params**  
`path: STRING`  
The path to export to  
`name: STRING`  
The name of the story to export

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the path can't be exported to, an IO error occurs, or the story doesn't exist

### `POST /player/import`
Imports a story.

**Params**  
`path: STRING`  
The path to import from

**Responses**    
`200 OK`

`400 BAD REQUEST`  
If the file can't be found or is not a story file

### `POST /player/choose`
Makes a decision.

**Params**  
`decision: INT`  
The index id of the option to choose

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the id is not an option

### `POST /player/load`
Loads a story.

**Params**  
`name: STRING`  
The name of the story to load

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the named story is not in the library

### `POST /player/restart`
Restarts the loaded story.

**Responses**  
`200 OK`

### `POST /player/quit`
Quits the loaded story.

**Responses**  
`200 OK`

### `DELETE /player/remove`
Makes a decision.

**Params**  
`name: STRING`  
The name of the story to remove

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the named story is not in the library


### `GET /writer/stories`
Gets the names of all works in the writer library.

**Responses**  
`200 OK`
```
STRING-LIST
```

### `GET /writer/current/story`
Gets the loaded work's story.

**Responses**  
`200 OK`
```
{
  "name": STRING,
  "statuses": [
  {
    "name": STRING,
    "value": INT
  },
  ...
  ],
  "choices": [
  {
    "id": INT,
    "options": [
    {
      "id": INT,
      "decision": INT
    },
    ...
    ]
  },
  ...
  ],
  "decisions": [
  {
    "id": INT,
    "type": DecisionTypes,
    "description": STRING,
    "dependency": STRING,
    "threshold": INT,
    "outcome1Id": INT,
    "outcome2Id": INT,
    "consequences": [
    {
      "type": StatusUpdateTypes,
      "var": INT,
      "status": STRING
    },
    ...
    ]
  },
  ...
  ],
  "choice": INT
}
```
or `NULL` if no work is loaded

### `GET /writer/current/name`
Gets the name of the loaded work.

**Responses**  
`200 OK`  
```
STRING
```
or `NULL` if no work is loaded


### `GET /writer/current/story-name`
Gets the name of the loaded work's story.

**Responses**  
`200 OK`
```
STRING
```
or `NULL` if no work is loaded

### `GET /writer/current/statuses`
Gets the statuses of the loaded work's story.

**Responses**  
`200 OK`
```
[
  {
    "name": STRING,
    "value": INT
  },
  ...
]
```
or `NULL` if no work is loaded

### `GET /writer/current/initial-choice`
Gets the initial choice of the loaded work's story.

**Responses**  
`200 OK`
```
INT
```

### `GET /writer/current/choices`
Gets the choices of the loaded work's story.

**Responses**  
`200 OK`
```
[
  {
    "id": INT,
    "options": [
      {
        "id": INT,
        "decision": INT
      },
      ...
    ]
  },
  ...
]
```
or `NULL` if no work is loaded

### `GET /writer/current/decisions`
Gets the decisions of the loaded work's story.

**Responses**  
`200 OK`
```
[
  {
    "id": INT,
    "type": DecisionTypes,
    "description": STRING,
    "dependency": STRING,
    "threshold": INT,
    "outcome1Id": INT,
    "outcome2Id": INT,
    "consequences": [
    {
      "type": StatusUpdateTypes,
      "var": INT,
      "status": STRING
    },
    ...
    ]
  },
  ...
]
```
or `NULL` if no work is loaded

### `POST /writer/export`
Exports the loaded work's story to a file.

**Params**  
`path: STRING`  
The path to export to

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the path can't be exported to or an IO error occurs

### `POST /writer/export-to-player`
Exports the loaded work's story to the player's library.

**Responses**  
`200 OK`

### `POST /writer/import`
Imports a story as a work.

**Params**  
`path: STRING`  
The path to import from

**Responses**  
`200 OK`

`400 BAD REQUEST`
If the file can't be found or is not a story file

### `POST /writer/load`
Loads a work.

**Params**  
`name: STRING`  
The name of the work to load

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the named work doesn't exist

### `POST /writer/quit`
Quits the loaded work.

**Responses**  
`200 OK`

### `DELETE /writer/remove`
Removes a work from the writer library.

**Params**  
`name: STRING`  
The name of the work to remove

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the named work doesn't exist

### `POST /writer/rename`
Renames a work.

**Params**  
`name: STRING`  
The name of the work to rename  
`newName: STRING`  
The new name of the work

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the named work doesn't exist or the new name is invalid

### `POST /writer/start`
Starts a new work.

**Params**  
`name: STRING`  
The name of the new work to start

**Responses**  
`200 OK`

### `POST /writer/set/name`
Sets the name of the loaded work's story.

**Params**  
`name: STRING`  
The name to set the story to

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the name is invalid or no work is loaded

### `POST /writer/add/status`
Adds a status to the loaded work's story or updates the initial value of an existing status.

**Params**  
`name: STRING`  
The name of the status to add/update  
`val: INT`  
The initial value of the status

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the name is invalid or no work is loaded

### `DELETE /writer/remove/status`
Removes a status from the loaded work's story.

**Params**  
`name: STRING`  
The name of the status to remove  

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the named status doesn't exist or no work is loaded

### `POST /writer/add/choice`
Adds a choice to the loaded work's story.

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If no work is loaded

### `POST /writer/set/initial`
Sets the initial choice of the loaded work's story.

**Params**  
`choice: INT`  
The id of the initial choice

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the choice doesn't exist or no work is loaded

### `POST /writer/add/simple-decision`
Adds a simple decision to the loaded work's story.

**Request Body**  
```
{
  "description": STRING,
  "choiceId": INT,
  "outcomeId": INT
}
```

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the description is null or any choice doesn't exist

### `POST /writer/add/consequential-decision`
Adds a consequential decision to the loaded work's story.

**Request Body**  
```
{
  "description": STRING,
  "choiceId": INT,
  "outcomeId": INT,
  "consequences": STRING-LIST
}
```

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the description is null, any choice doesn't exist, the consequences format is invalid, or no work is loaded

### `POST /writer/add/simple-dependent-decision`
Adds a simple decision whose outcome is dependent on a status to the loaded work's story.

**Request Body**  
```
{
  "description": STRING,
  "choiceId": INT,
  "dependency": STRING,
  "threshold": INT,
  "outcomeBelowId": INT,
  "outcomeMeetsId": INT
}
```

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the description is null, any choice doesn't exist, the dependency doesn't exist, or no work is loaded

### `POST /writer/add/consequential-dependent-decision`
Adds a consequential decision whose outcome is dependent on a status to the loaded work's story.

**Request Body**  
```
{
  "description": STRING,
  "choiceId": INT,
  "dependency": STRING,
  "threshold": INT,
  "outcomeBelowId": INT,
  "outcomeMeetsId": INT,
  "consequences": STRING-LIST
}
```

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the description is null, any choice doesn't exist, the dependency doesn't exist, the consequences format is invalid, or no work is loaded

### `DELETE /writer/remove/option`
Removes an option from a choice in the loaded work's story.

**Params**    
`choice: INT`  
The id of the choice to remove the option from  
`option: INT`  
The id of the option to remove

**Responses**  
`200 OK`

`400 BAD REQUEST`    
If the choice or option doesn't exist or no work is loaded

### `DELETE /writer/remove/choice`
Removes an option from a choice in the loaded work's story.

**Params**  
`choice: INT`    
The id of the choice to remove

**Responses**  
`200 OK`

`400 BAD REQUEST`  
If the choice doesn't exist or can't be removed or no work is loaded
