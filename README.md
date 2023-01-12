# Movie Test Exercise
## Requirements

Please build a Spring Boot project written in Kotlin that contains
crud operations for movies with json representation like the following
example:

```
{
  "id": 1,
  "title": "Pulp Fiction",
  "releaseDate": "1994–10-14",
  "stars": [
    "John Travolta",
    "Uma Thurman",
    "Samuel L. Jackson"
  ]
}
```
- Store and retrieve data using JPA and a database as you see fit.
- Ensure that endpoints return the appropriate return codes such as
400 for invalid input. Title and release date are required and the
combination of these two fields must be unique. There must be at least
one entry in the stars array. The list of stars must not be duplicated
for the same movie.
- Write unit tests as you deem necessary and ensure that edge cases
are covered. Code will be evaluated primarily on reliably meeting
criteria, code organization, following S.O.L.I.D. principles, and
ensuring reliability through testing.

## Comments 

To be honest that's not how I would organize code for such a small project, but the task was specifically about code organization and SOLID, so I tried to organize it as by the book.

There are a few small nice things like pagination and some headers that would be a good addition, but I just didn't have time to add.  

## Build

`./gradlew build`

## Run 

`./gradlew bootRun`

or just 

`docker build -t movies . && docker run -p 8080:8080 -it movies`

Open http://localhost:8080/swagger-ui/index.html

## Unit Test

`./gradlew test`

## Integration Test

`./gradlew intTest`

