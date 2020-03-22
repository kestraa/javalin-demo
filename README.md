# Javalin Demo API

Application that demonstrates the essential functionalities about Javalin Micro Framework.

## Features 

This demo shows us some simple use cases most common in API:

   * Javalin basic configuration 
   * Application events (started, stoped)
   * Interceptor handler
   * Get path parameter
   * Asynchronous requests
   * Exception handler
   * Open API documentation via Plugin

## Endpoints

| Method | Path     | Description   |
|--------|----------|--------------------------------------|
| GET    | /        | Basic response                       |
| GET    | /:name   | Pass an attribute by request URI     |
| GET    | /async   | Demonstrates an asynchronous handler | 
| POST   | /user    | Gets a JSON object from request body |
| GET    | /docs    | Demonstrates a basic OpenAPI documentation |
| GET    | /swagger | Swagger json information about API   |

## Running 

To run the application, just use **gradle** on console. The application will start on *http://localhost:8080*.

```bash
$ gradle run
```

PS. You must provide a token to make requests. The token can be any value on **Authorization** header.
 
